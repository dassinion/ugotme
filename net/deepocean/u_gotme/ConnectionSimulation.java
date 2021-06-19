// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ConnectionSimulation extends Connection
{
    private byte[] flashDump;
    private int flashDumpLength;
    private byte[] responseAcknowledge;
    private int responseAcknowledgeLength;
    private byte[] responseModel;
    private int responseModelLength;
    private byte[] responseIdentification;
    private int responseIdentificationLength;
    private byte[] responseCount;
    private int responseCountLength;
    boolean isWriting;
    int bytesToWrite;
    int addressToWrite;
    byte[] response;
    int responseOffset;
    int responseLength;
    int count;
    
    public ConnectionSimulation(final String simulationDataPath) {
        this.initialiseSimulationData(simulationDataPath);
        this.isWriting = false;
        this.addressToWrite = 0;
        this.bytesToWrite = 0;
    }
    
    @Override
    public ArrayList<String> getComportList() {
        final ArrayList<String> ports = new ArrayList<String>();
        ports.add("simulation");
        return ports;
    }
    
    private int readData(final String fileName, final byte[] buffer) {
        int bytesRead = 0;
        try {
            final FileInputStream inputStream = new FileInputStream(fileName);
            bytesRead = inputStream.read(buffer);
            inputStream.close();
            DebugLogger.info("Simulation: Read " + bytesRead + " bytes from " + fileName);
        }
        catch (FileNotFoundException ex) {
            DebugLogger.error("Error opening simulation data file " + fileName);
        }
        catch (IOException ex2) {
            DebugLogger.error("Error reading simulation data file " + fileName);
        }
        return bytesRead;
    }
    
    private void initialiseSimulationData(final String simulationDataPath) {
        this.flashDump = new byte[8388610];
        String fileName = simulationDataPath + "\\memory_dump.dat";
        this.flashDumpLength = this.readData(fileName, this.flashDump);
        this.responseModel = new byte[100];
        fileName = simulationDataPath + "\\modelcmd_response.dat";
        this.responseModelLength = this.readData(fileName, this.responseModel);
        this.responseIdentification = new byte[100];
        fileName = simulationDataPath + "\\identificationcmd_response.dat";
        this.responseIdentificationLength = this.readData(fileName, this.responseIdentification);
        this.responseCount = new byte[100];
        fileName = simulationDataPath + "\\countcmd_response.dat";
        this.responseCountLength = this.readData(fileName, this.responseCount);
        this.responseAcknowledge = new byte[100];
        fileName = simulationDataPath + "\\acknowledge_response.dat";
        this.responseAcknowledgeLength = this.readData(fileName, this.responseAcknowledge);
    }
    
    private boolean isEqual(final byte[] data, final String test) {
        boolean equal = true;
        for (int length = test.length(), i = 0; i < length && equal; i += 2) {
            final String subString = test.substring(i, i + 2);
            if (!subString.equals("xx") && (byte)Integer.parseInt(subString, 16) != data[i / 2]) {
                equal = false;
            }
        }
        return equal;
    }
    
    private void handleWriteCommand(final byte[] outputString, final int expectedDataLength) {
        int i = 0;
        int checkSum = 0;
        while (i < 16) {
            checkSum += (outputString[i] & 0xFF);
            ++i;
        }
        if ((checkSum & 0xFF) != 0x0) {}
        for (i = 0; i < 15 && this.bytesToWrite - i > 0; ++i) {
            this.flashDump[this.addressToWrite + i] = outputString[i];
            ++this.count;
        }
        this.addressToWrite += 15;
        this.bytesToWrite -= Math.min(this.bytesToWrite, 15);
        this.response = this.responseAcknowledge;
        this.responseLength = this.responseAcknowledgeLength - 3;
        this.responseOffset = 3;
        this.sendResponseHeader();
        this.sendResponseData();
        if (this.bytesToWrite <= 0) {
            this.isWriting = false;
        }
    }
    
    private void sendResponseData() {
        if (this.response != null) {
            for (int i = 0; i < this.responseLength; ++i) {
                this.responseData[i] = this.response[this.responseOffset + i];
            }
            this.responseDataCount = this.responseLength;
        }
    }
    
    private void sendResponseHeader() {
        if (this.response != null) {
            for (int i = 0; i < 3; ++i) {
                this.responseHeader[i] = this.response[i];
            }
            this.responseHeaderCount = 3;
        }
    }
    
    @Override
    public int outputAndWaitForResponse(final byte[] outputString, final int expectedDataLength) {
        this.responseLength = 0;
        this.responseOffset = 0;
        this.response = null;
        if (this.isWriting) {
            this.handleWriteCommand(outputString, expectedDataLength);
        }
        else if (this.isEqual(outputString, "9305040003019F0000000000000000xx")) {
            this.response = this.responseModel;
            this.responseOffset = 3;
            this.responseLength = this.responseModelLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to model command");
        }
        else if (this.isEqual(outputString, "930A00000000000000000000000000xx")) {
            this.response = this.responseIdentification;
            this.responseOffset = 3;
            this.responseLength = this.responseIdentificationLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to identification command");
        }
        else if (this.isEqual(outputString, "930B03001D00000000000000000000xx")) {
            this.response = this.responseCount;
            this.responseOffset = 3;
            this.responseLength = this.responseCountLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to count command");
        }
        else if (this.isEqual(outputString, "930101xx0000000000000000000000xx")) {
            this.response = this.responseAcknowledge;
            this.responseOffset = 3;
            this.responseLength = this.responseAcknowledgeLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to NMEA switch command");
        }
        else if (this.isEqual(outputString, "930507xxxx0403xxxxxx0000000000xx")) {
            this.response = this.flashDump;
            this.responseOffset = 0;
            this.responseOffset |= (outputString[7] & 0xFF);
            this.responseOffset <<= 8;
            this.responseOffset |= (outputString[8] & 0xFF);
            this.responseOffset <<= 8;
            this.responseOffset |= (outputString[9] & 0xFF);
            this.responseLength = 0;
            this.responseLength |= (outputString[3] & 0xFF);
            this.responseLength <<= 8;
            this.responseLength |= (outputString[4] & 0xFF);
            this.responseHeader[0] = -109;
            this.responseHeader[1] = outputString[3];
            this.responseHeader[2] = outputString[4];
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to read command (0x" + String.format("%06x", this.responseOffset) + ", 0x" + String.format("%04x", this.responseLength) + ")");
        }
        else if (this.isEqual(outputString, "930607xxxx0402xxxxxx0000000000xx")) {
            this.addressToWrite = 0;
            this.addressToWrite |= (outputString[7] & 0xFF);
            this.addressToWrite <<= 8;
            this.addressToWrite |= (outputString[8] & 0xFF);
            this.addressToWrite <<= 8;
            this.addressToWrite |= (outputString[9] & 0xFF);
            this.bytesToWrite = 0;
            this.bytesToWrite |= (outputString[3] & 0xFF);
            this.bytesToWrite <<= 8;
            this.bytesToWrite |= (outputString[4] & 0xFF);
            this.count = 0;
            this.isWriting = true;
            this.response = this.responseAcknowledge;
            this.responseOffset = 3;
            this.responseLength = this.responseAcknowledgeLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to write command (0x" + String.format("%06x", this.addressToWrite) + ", 0x" + String.format("%04x", this.bytesToWrite) + ")");
        }
        else if (this.isEqual(outputString, "93060700000420xxxxxx0000000000xx")) {
            this.addressToWrite = 0;
            this.addressToWrite |= (outputString[7] & 0xFF);
            this.addressToWrite <<= 8;
            this.addressToWrite |= (outputString[8] & 0xFF);
            this.addressToWrite <<= 8;
            this.addressToWrite |= (outputString[9] & 0xFF);
            this.response = this.responseAcknowledge;
            this.responseOffset = 3;
            this.responseLength = this.responseAcknowledgeLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to purge command (0x" + String.format("%06x", this.addressToWrite) + ")");
        }
        else if (this.isEqual(outputString, "93060400xx01060000000000000000xx")) {
            this.response = this.responseAcknowledge;
            this.responseOffset = 3;
            this.responseLength = this.responseAcknowledgeLength - 3;
            this.sendResponseHeader();
            this.sendResponseData();
            DebugLogger.debug("Simulation: generating response to unknown write command 1");
        }
        else if (this.isEqual(outputString, "930504xxxx01050000000000000000xx")) {
            this.responseHeader[0] = -109;
            this.responseHeader[1] = 0;
            this.responseHeader[2] = 1;
            this.responseHeaderCount = 3;
            this.responseData[0] = 0;
            this.responseDataCount = 1;
            DebugLogger.debug("Simulation: generating response to unknown write command 2");
        }
        return this.responseLength;
    }
}
