// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.ArrayList;

public class Connection
{
    public static final int BLOCK_SIZE = 4096;
    public static final int HEADER_SIZE = 3;
    private static final int WRITEBLOCK_SIZE = 256;
    private static final int WRITECHUNK_SIZE = 16;
    private byte[] identificationCommand;
    private byte[] modelCommand;
    private byte[] modelCommand2;
    private byte[] nmeaSwitchCommand;
    private byte[] countCommand;
    private byte[] readCommand;
    private byte[] writeCommand;
    private byte[] unknownWriteCommand1;
    private byte[] unknownWriteCommand2;
    private byte[] unknownPurgeCommand1;
    private byte[] unknownPurgeCommand2;
    protected byte[] responseHeader;
    protected byte[] responseData;
    protected int responseCount;
    protected int responseHeaderCount;
    protected int responseDataCount;
    protected int expectedDataLength;
    protected int expectedHeaderLength;
    
    public Connection() {
        this.identificationCommand = new byte[] { -109, 10, 0, 0,  0,  0, 0,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        //                                        0x93, 0x0a, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        this.modelCommand          = new byte[] { -109, 5,  4, 0,  3,  1, -97, 0,  0, 0, 0, 0, 0, 0, 0, 0 };
        //                                        0x93, 0x05, 0x04, 0x00, 0x03, 0x01, 0x9f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        this.modelCommand2         = new byte[] { -109, 5,  4, 0,  0,  0, -97, 0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.nmeaSwitchCommand     = new byte[] { -109, 1,  1, 0,  0,  0, 0,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.countCommand          = new byte[] { -109, 11, 3, 0,  29, 0, 0,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.readCommand           = new byte[] { -109, 5,  7, 16, 0,  4, 3,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.writeCommand          = new byte[] { -109, 6,  7, 0,  0,  4, 32,  16, 0, 0, 0, 0, 0, 0, 0, 0 };
        this.unknownWriteCommand1  = new byte[] { -109, 6,  4, 0,  0,  1, 6,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.unknownWriteCommand2  = new byte[] { -109, 5,  4, 0,  1,  1, 5,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.unknownPurgeCommand1  = new byte[] { -109, 12, 0, 30, 0,  0, 0,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.unknownPurgeCommand2  = new byte[] { -109, 8,  2, 0,  0,  0, 0,   0,  0, 0, 0, 0, 0, 0, 0, 0 };
        this.responseHeader = new byte[3];
        this.responseData = new byte[4096];
    }
    
    public ArrayList<String> getComportList() {
        return null;
    }
    
    public void open(final String comport) {
    }
    
    public void close() {
    }
    
    public int outputAndWaitForResponse(final byte[] outputString, final int expectedDataLength) {
        return 0;
    }
    
    byte[] getResponseData() {
        return this.responseData;
    }
    
    byte[] getResponseHeader() {
        return this.responseHeader;
    }
    
    public int getResponseHeaderLength() {
        return this.responseHeaderCount;
    }
    
    public int getResponseDataLength() {
        return this.responseDataCount;
    }
    
    public int commandGetIdentification() {
        return this.outputAndWaitForResponse(this.identificationCommand, 10);
    }
    
    public int commandGetModel() {
        return this.outputAndWaitForResponse(this.modelCommand, 3);
    }
    
    public int commandGetModel2() {
        return this.outputAndWaitForResponse(this.modelCommand2, 3);
    }
    
    public int commandNmeaSwitch(final byte mode) {
        this.nmeaSwitchCommand[3] = mode;
        return this.outputAndWaitForResponse(this.nmeaSwitchCommand, 0);
    }
    
    public int commmandCount() {
        return this.outputAndWaitForResponse(this.countCommand, 3);
    }
    
    public int commandReadFlash(final int readPosition, final int size) {
        this.readCommand[7] = (byte)(readPosition >> 16 & 0xFF);
        this.readCommand[8] = (byte)(readPosition >> 8 & 0xFF);
        this.readCommand[9] = (byte)(readPosition & 0xFF);
        this.readCommand[3] = (byte)(size >> 8 & 0xFF);
        this.readCommand[4] = (byte)(size & 0xFF);
        final int dataLength = this.outputAndWaitForResponse(this.readCommand, size);
        return dataLength;
    }
    
    public boolean commandWriteFlash(final int writePosition, final int size, final byte[] writeData) {
        boolean isError = false;
        if (size % 256 != 0) {
            DebugLogger.error("Size of data to write not a multiple of 256");
            isError = true;
        }
        if (!isError) {
            final byte[] byteBuffer = new byte[16];
            final int blocks = size / 256;
            this.writeCommand[3] = 1;
            this.writeCommand[4] = 0;
            this.writeCommand[6] = 2;
            for (int block = 0; block < blocks && !isError; ++block) {
                final int address = writePosition + block * 256;
                this.writeCommand[7] = (byte)(address >> 16 & 0xFF);
                this.writeCommand[8] = (byte)(address >> 8 & 0xFF);
                this.writeCommand[9] = (byte)(address & 0xFF);
                DebugLogger.info("Send block to address 0x" + Integer.toHexString(address));
                int dataLength = this.outputAndWaitForResponse(this.writeCommand, 0);
                if (dataLength != 0) {
                    isError = true;
                }
                if (!isError) {
                    int blockByteCount = 0;
                    int chunkByteCount = 0;
                    while (blockByteCount < 256) {
                        byteBuffer[chunkByteCount] = writeData[block * 256 + blockByteCount];
                        if (++chunkByteCount == 15) {
                            dataLength = this.outputAndWaitForResponse(byteBuffer, 0);
                            chunkByteCount = 0;
                        }
                        ++blockByteCount;
                    }
                    if (chunkByteCount > 0) {
                        while (chunkByteCount < 15) {
                            byteBuffer[chunkByteCount] = 0;
                            ++chunkByteCount;
                        }
                        dataLength = this.outputAndWaitForResponse(byteBuffer, 0);
                    }
                }
                if (isError) {
                    DebugLogger.error("Error writing block " + Integer.toHexString(writePosition));
                }
            }
        }
        return isError;
    }
    
    public boolean commandEraseFlash(final int erasePosition) {
        boolean isError = false;
        this.unknownWriteCommand1[4] = 0;
        int dataLength = this.outputAndWaitForResponse(this.unknownWriteCommand1, 0);
        if (dataLength != 0) {
            DebugLogger.error("Error on unknownWriteCommand1 while erasing block " + Integer.toHexString(erasePosition));
            isError = true;
        }
        if (!isError) {
            this.writeCommand[3] = 0;
            this.writeCommand[4] = 0;
            this.writeCommand[6] = 32;
            this.writeCommand[7] = (byte)(erasePosition >> 16 & 0xFF);
            this.writeCommand[8] = (byte)(erasePosition >> 8 & 0xFF);
            this.writeCommand[9] = (byte)(erasePosition & 0xFF);
            dataLength = this.outputAndWaitForResponse(this.writeCommand, 0);
            if (dataLength != 0) {
                DebugLogger.error("Error on writeCommand while erasing block " + Integer.toHexString(erasePosition));
                isError = true;
            }
        }
        boolean exit = false;
        int count = 0;
        this.unknownWriteCommand2[3] = 0;
        this.unknownWriteCommand2[4] = 1;
        while (!exit && !isError) {
            dataLength = this.outputAndWaitForResponse(this.unknownWriteCommand2, 1);
            if (dataLength != 1) {
                DebugLogger.error("Error on unknownWriteCommand2 while erasing block " + Integer.toHexString(erasePosition));
                isError = true;
            }
            if (this.responseData[0] != 0 && !isError) {
                if (count < 5) {
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e) {
                        DebugLogger.error("Interrupted exception during Thread.sleep()");
                    }
                    ++count;
                }
                else {
                    isError = true;
                    exit = true;
                }
            }
            else {
                exit = true;
            }
        }
        return isError;
    }
}
