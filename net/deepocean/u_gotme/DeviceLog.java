// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.Iterator;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import hirondelle.date4j.DateTime;
import java.util.ArrayList;

public class DeviceLog
{
    private static DeviceLog theInstance;
    private ArrayList<DeviceLogEntry> theLog;
    
    private DeviceLog() {
        this.theLog = new ArrayList<DeviceLogEntry>();
    }
    
    public static DeviceLog getInstance() {
        if (DeviceLog.theInstance == null) {
            DeviceLog.theInstance = new DeviceLog();
        }
        return DeviceLog.theInstance;
    }
    
    public void appendData(final byte[] data, final int length) {
        if (length % 32 == 0) {
            final int number = length / 32;
            final byte[] logString = new byte[24];
            for (int i = 0; i < number; ++i) {
                if ((data[i * 32 + 0] & 0xFF) == 0xF1) {
                    final DateTime dateTime = ToolBox.bytesToDateTime(data[i * 32 + 1], data[i * 32 + 2], data[i * 32 + 3], data[i * 32 + 4], data[i * 32 + 5]);
                    for (int j = 0; j < 24; ++j) {
                        final byte nextByte = data[i * 32 + 6 + j];
                        if (nextByte != 0) {
                            logString[j] = nextByte;
                        }
                        else {
                            logString[j] = 32;
                        }
                    }
                    final DeviceLogEntry entry = new DeviceLogEntry(dateTime, new String(logString));
                    this.theLog.add(entry);
                    DebugLogger.debug("Device Log entry: " + entry.toString());
                }
            }
        }
        else {
            DebugLogger.error("Invalid block size when converting data to DeviceLog");
        }
    }
    
    public void clear() {
        this.theLog.clear();
    }
    
    public ArrayList<DeviceLogEntry> getDeviceLogEntries() {
        return this.theLog;
    }
    
    public int getNumberOfEntries() {
        return this.theLog.size();
    }
    
    public void writeToFile(final String fileName) {
        try {
            final FileWriter outFile = new FileWriter(fileName);
            final PrintWriter out = new PrintWriter(outFile);
            final Iterator<DeviceLogEntry> iterator = this.theLog.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                final DeviceLogEntry entry = iterator.next();
                out.println(String.format("%4d", count) + ": " + entry.toString());
                ++count;
            }
            out.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing file " + fileName);
        }
    }
    
    static {
        DeviceLog.theInstance = null;
    }
}
