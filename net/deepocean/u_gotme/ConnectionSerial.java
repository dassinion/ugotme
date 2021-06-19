// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

public abstract class ConnectionSerial extends Connection
{
    private static final int COMM_TIMEOUT_IN_MS = 2000;
    private static final int MAX_RETRIES = 3;
    private Thread readThread;
    private String divertCode;
    private static String TimeStamp;
    private byte[] buffer;
    private boolean errorBlock;
    private boolean expectingResponse;
    private int localResponseCount;
    private int localResponseHeaderCount;
    private int localResponseDataCount;
    private ResponseType responseType;
    private int derivedDataLength;
    private boolean timeout;
    private int blockCount;
    private int crapBytesCount;
    
    public ConnectionSerial() {
        this.divertCode = "10";
        this.errorBlock = false;
        this.buffer = new byte[4099];
        this.expectedHeaderLength = 3;
        this.errorBlock = false;
        this.expectingResponse = false;
        this.timeout = false;
        this.blockCount = 0;
        this.responseType = ResponseType.UNKNOWN;
    }
    
    int comportRead(final byte[] buffer) {
        return 0;
    }
    
    boolean comportWrite(final byte[] buffer, final int length) {
        return false;
    }
    
    protected void handeRxEvent() {
        synchronized (this) {
            int bytesRead = 0;
            this.errorBlock = false;
            boolean endOfBlock = false;
            bytesRead = this.comportRead(this.buffer);
            if (bytesRead > 0) {
                DebugLogger.debug("Device says 'Auch! Here are " + bytesRead + " bytes!'");
            }
            if (this.expectingResponse) {
                for (int i = 0; i < bytesRead && !this.errorBlock && this.blockCount < 2; ++i) {
                    if (this.localResponseHeaderCount == 0) {
                        if (this.buffer[i] == -109) {
                            if (this.crapBytesCount > 0) {
                                DebugLogger.debug("Number of bytes preceding response: " + this.crapBytesCount);
                            }
                            this.responseHeader[this.localResponseHeaderCount] = this.buffer[i];
                            ++this.localResponseHeaderCount;
                            ++this.localResponseCount;
                        }
                        else {
                            ++this.crapBytesCount;
                            if (this.blockCount > 0) {
                                DebugLogger.error("Unexpected data");
                                this.errorBlock = true;
                            }
                        }
                        endOfBlock = false;
                    }
                    else if (this.localResponseHeaderCount < 3) {
                        this.responseHeader[this.localResponseHeaderCount] = this.buffer[i];
                        endOfBlock = false;
                        ++this.localResponseHeaderCount;
                        ++this.localResponseCount;
                        if (this.localResponseHeaderCount == 3) {
                            this.derivedDataLength = ((this.responseHeader[1] & 0xFF) << 8 | (this.responseHeader[2] & 0xFF));
                            if (this.derivedDataLength != this.expectedDataLength && this.derivedDataLength != 0) {
                                DebugLogger.error("Inconsistent header");
                                this.errorBlock = true;
                            }
                            if (this.derivedDataLength == 0) {
                                DebugLogger.debug("Response block found, data length: " + this.derivedDataLength + " bytes");
                                this.responseCount = this.localResponseCount;
                                this.responseHeaderCount = this.localResponseHeaderCount;
                                this.responseDataCount = this.localResponseDataCount;
                                this.localResponseCount = 0;
                                this.localResponseHeaderCount = 0;
                                this.localResponseDataCount = 0;
                                endOfBlock = true;
                                ++this.blockCount;
                            }
                        }
                    }
                    else {
                        this.responseData[this.localResponseDataCount] = this.buffer[i];
                        ++this.localResponseDataCount;
                        ++this.localResponseCount;
                        endOfBlock = false;
                        if (this.localResponseDataCount == this.derivedDataLength) {
                            DebugLogger.debug("Response block found, data length: " + this.derivedDataLength + " bytes");
                            this.responseCount = this.localResponseCount;
                            this.responseHeaderCount = this.localResponseHeaderCount;
                            this.responseDataCount = this.localResponseDataCount;
                            this.localResponseCount = 0;
                            this.localResponseHeaderCount = 0;
                            this.localResponseDataCount = 0;
                            endOfBlock = true;
                            ++this.blockCount;
                        }
                    }
                }
                if (this.errorBlock) {
                    this.notify();
                }
                if (endOfBlock) {
                    if (this.responseType == ResponseType.ONEBLOCK) {
                        if (this.blockCount == 1) {
                            this.timeout = false;
                            this.notify();
                        }
                        else {
                            DebugLogger.error("Found more response blocks while expecting one");
                        }
                    }
                    else if (this.responseType == ResponseType.TWOBLOCK) {
                        if (this.blockCount == 2) {
                            this.timeout = false;
                            this.notify();
                            DebugLogger.debug("Two block response - wakeup");
                        }
                    }
                    else if (this.responseType == ResponseType.UNKNOWN) {
                        if (this.blockCount == 2) {
                            this.responseType = ResponseType.TWOBLOCK;
                            this.timeout = false;
                            this.notify();
                            DebugLogger.debug("Two block response identified - wakeup");
                        }
                        else if (this.responseDataCount > 0) {
                            this.responseType = ResponseType.ONEBLOCK;
                            this.timeout = false;
                            this.notify();
                            DebugLogger.debug("One block response identified - wakeup");
                        }
                        else {
                            this.timeout = true;
                            DebugLogger.debug("One block response - timeout");
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public int outputAndWaitForResponse(final byte[] outputString, final int expectedDataLength) {
        int dataLength = 0;
        int retries = 0;
        boolean ready = false;
        int i = 0;
        int sum = 0;
        while (i < 15) {
            sum += outputString[i];
            ++i;
        }
        outputString[15] = (byte)(256 - (sum & 0xFF));

        String hexoutput = byteArrayToHex(outputString);
        DebugLogger.debug("send CMD to device: " + hexoutput);

        while (!ready && retries < 3) {
            synchronized (this) {
                this.localResponseCount = 0;
                this.localResponseHeaderCount = 0;
                this.localResponseDataCount = 0;
                this.responseCount = 0;
                this.responseHeaderCount = 0;
                this.responseDataCount = 0;
                this.expectedDataLength = expectedDataLength;
                this.expectingResponse = true;
                this.timeout = false;
                this.blockCount = 0;
                this.crapBytesCount = 0;
            }
            DebugLogger.debug("Kicking devices' ass. (Retry " + retries + ")");
            this.comportWrite(outputString, 16);
            synchronized (this) {
                try {
                    this.wait(2000L);
                    dataLength = this.responseDataCount;
                }
                catch (InterruptedException ex) {}
                if (this.timeout) {
                    if (this.responseType == ResponseType.UNKNOWN) {
                        this.responseType = ResponseType.ONEBLOCK;
                        DebugLogger.debug("One block response identified");
                        if (!this.errorBlock && this.responseHeaderCount == 3 && this.responseDataCount == expectedDataLength) {
                            ready = true;
                        }
                        else {
                            DebugLogger.error("Error reading device. Error: " + this.errorBlock + ", Header: " + this.responseHeaderCount + " Data: " + this.responseDataCount + ". I am going to retry");
                            ++retries;
                        }
                    }
                    else {
                        DebugLogger.error("No response from device or response to short");
                        dataLength = this.responseDataCount;
                    }
                }
                if (!this.errorBlock && this.responseHeaderCount == 3 && this.responseDataCount == expectedDataLength) {
                    ready = true;
                }
                else {
                    DebugLogger.error("Error reading device. Error: " + this.errorBlock + ", Header: " + this.responseHeaderCount + " Data: " + this.responseDataCount + ". I am going to retry");
                    ++retries;
                }
            }
        }
        if (!ready) {
            DebugLogger.error("Wtf! Failed to communicate to device.");
        }
        return dataLength;
    }
    
    private enum ResponseType
    {
        UNKNOWN, 
        ONEBLOCK, 
        TWOBLOCK;
    }


    public static String byteArrayToHex(byte[] a) {
       StringBuilder sb = new StringBuilder();
       for(byte b: a)
          sb.append("0x" + String.format("%02x", b) + ", ");
       return sb.toString();
    }
}
