// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortEventListener;

public class ConnectionSerialJssc extends ConnectionSerial implements SerialPortEventListener
{
    private SerialPort serialPort1;
    
    public ArrayList<String> getComportList() {
        final String[] portList = SerialPortList.getPortNames();
        final ArrayList<String> ports = new ArrayList<String>();
        for (int i = 0; i < portList.length; ++i) {
            ports.add(portList[i]);
        }
        return ports;
    }
    
    public void open(final String comport) {
        this.serialPort1 = new SerialPort(comport);
        try {
            this.serialPort1.openPort();
        }
        catch (SerialPortException e) {
            DebugLogger.error("Error opening comport " + comport + ": " + e.getMessage());
        }
        try {
            this.serialPort1.setParams(9600, 8, 1, 0);
        }
        catch (SerialPortException e) {
            DebugLogger.error("Error configuring port " + e.getMessage());
        }
        try {
            final int mask = 1;
            this.serialPort1.setEventsMask(mask);
            this.serialPort1.addEventListener((SerialPortEventListener)this);
            DebugLogger.debug("Eventlistener added");
        }
        catch (SerialPortException e) {
            DebugLogger.error("Error adding eventlistener - " + e.getMessage());
        }
    }
    
    public void close() {
        if (this.serialPort1 != null) {
            try {
                this.serialPort1.removeEventListener();
                this.serialPort1.closePort();
                this.serialPort1 = null;
            }
            catch (SerialPortException e) {
                DebugLogger.error("Error closing comport: " + e.getMessage());
            }
        }
        DebugLogger.info("Comport closed");
    }
    
    @Override
    int comportRead(final byte[] buffer) {
        int bytesRead = 0;
        try {
            bytesRead = 0;
            if (this.serialPort1 != null && this.serialPort1.getInputBufferBytesCount() > 0) {
                final byte[] comportBuffer = this.serialPort1.readBytes();
                bytesRead = comportBuffer.length;
                for (int i = 0; i < bytesRead; ++i) {
                    buffer[i] = comportBuffer[i];
                }
            }
        }
        catch (SerialPortException e) {
            DebugLogger.error("Error reading comport " + e.getMessage());
        }
        return bytesRead;
    }
    
    @Override
    boolean comportWrite(final byte[] buffer, final int length) {
        try {
            this.serialPort1.writeBytes(buffer);
        }
        catch (SerialPortException e) {
            DebugLogger.error("IO Exception writing to comport: " + e.getMessage());
        }
        return false;
    }
    
    public void serialEvent(final SerialPortEvent event) {
        if (event.isRXCHAR()) {
            this.handeRxEvent();
        }
    }
}
