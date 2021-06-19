// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import gnu.io.SerialPortEvent;
import java.util.TooManyListenersException;
import java.io.IOException;
import gnu.io.PortInUseException;
import java.util.Enumeration;
import java.util.ArrayList;
import gnu.io.SerialPort;
import java.io.OutputStream;
import java.io.InputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEventListener;

public class ConnectionSerialRxtx extends ConnectionSerial implements SerialPortEventListener
{
    private static CommPortIdentifier portId1;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SerialPort serialPort1;
    
    public ArrayList<String> getComportList() {
        final Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        final ArrayList<String> ports = new ArrayList<String>();
        while (portIdentifiers.hasMoreElements()) {
            final CommPortIdentifier pid = portIdentifiers.nextElement();
            ports.add(pid.getName());
        }
        return ports;
    }
    
    public void open(final String comport) {
        try {
            ConnectionSerialRxtx.portId1 = CommPortIdentifier.getPortIdentifier(comport);
        }
        catch (Exception e) {
            DebugLogger.error("Error while getting comport ID for comport " + comport);
        }
        try {
            this.serialPort1 = (SerialPort)ConnectionSerialRxtx.portId1.open("igotu2gpx2", 2000);
            DebugLogger.info("Comport " + ConnectionSerialRxtx.portId1.getName() + " opened");
        }
        catch (PortInUseException e2) {
            DebugLogger.error("Error opening comport " + comport + ": port in use");
        }
        try {
            this.inputStream = this.serialPort1.getInputStream();
            this.outputStream = this.serialPort1.getOutputStream();
        }
        catch (IOException e3) {
            DebugLogger.error("IO Exception while getting comport streams");
        }
        try {
            this.serialPort1.addEventListener((SerialPortEventListener)this);
            DebugLogger.debug("Eventlistener added");
        }
        catch (TooManyListenersException e4) {
            DebugLogger.error("Error adding eventlistener - to many listeners");
        }
        this.serialPort1.notifyOnDataAvailable(true);
    }
    
    public void close() {
        if (this.serialPort1 != null) {
            this.serialPort1.removeEventListener();
            this.serialPort1.close();
        }
        DebugLogger.info("Comport closed");
    }
    
    @Override
    int comportRead(final byte[] buffer) {
        int bytesRead = 0;
        try {
            bytesRead = this.inputStream.read(buffer);
        }
        catch (IOException e) {
            DebugLogger.error("Error receiving bytes");
        }
        return bytesRead;
    }
    
    @Override
    boolean comportWrite(final byte[] buffer, final int length) {
        try {
            this.outputStream.write(buffer, 0, length);
        }
        catch (IOException e) {
            DebugLogger.error("IO Exception writing to comport");
        }
        return false;
    }
    
    public void serialEvent(final SerialPortEvent event) {
        switch (event.getEventType()) {
            case 1: {
                this.handeRxEvent();
                break;
            }
        }
    }
}
