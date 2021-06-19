// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import purejavacomm.SerialPortEvent;
import purejavacomm.UnsupportedCommOperationException;
import java.util.TooManyListenersException;
import java.io.IOException;
import purejavacomm.PortInUseException;
import java.util.Enumeration;
import java.util.ArrayList;
import purejavacomm.SerialPort;
import java.io.OutputStream;
import java.io.InputStream;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPortEventListener;

public class ConnectionSerialPurejava extends ConnectionSerial implements SerialPortEventListener
{
    private static CommPortIdentifier portId;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SerialPort serialPort;
    
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
            ConnectionSerialPurejava.portId = CommPortIdentifier.getPortIdentifier(comport);
        }
        catch (Exception e) {
            DebugLogger.error("Error while getting comport ID for comport " + comport);
        }
        try {
            this.serialPort = (SerialPort)ConnectionSerialPurejava.portId.open("igotu2gpx2", 2000);
            DebugLogger.info("Comport " + ConnectionSerialPurejava.portId.getName() + " opened");
        }
        catch (PortInUseException e2) {
            DebugLogger.error("Error opening comport " + comport + ": port in use");
        }
        try {
            this.inputStream = this.serialPort.getInputStream();
            this.outputStream = this.serialPort.getOutputStream();
        }
        catch (IOException e3) {
            DebugLogger.error("IO Exception while getting comport streams");
        }
        try {
            this.serialPort.addEventListener((SerialPortEventListener)this);
            DebugLogger.debug("Eventlistener added");
        }
        catch (TooManyListenersException e4) {
            DebugLogger.error("Error adding eventlistener - to many listeners");
        }
        this.serialPort.notifyOnDataAvailable(true);
        try {
            this.serialPort.setSerialPortParams(9600, 8, 1, 0);
        }
        catch (UnsupportedCommOperationException e5) {
            DebugLogger.error("Error configuring port");
        }
    }
    
    public void close() {
        if (this.serialPort != null) {
            this.serialPort.removeEventListener();
            this.serialPort.close();
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
