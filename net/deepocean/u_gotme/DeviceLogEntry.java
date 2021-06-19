// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;

public class DeviceLogEntry
{
    private DateTime dateTime;
    private String message;
    
    public DeviceLogEntry(final DateTime dateTime, final String message) {
        this.dateTime = dateTime;
        this.message = message;
    }
    
    DateTime getDateTime() {
        return this.dateTime;
    }
    
    DateTime getLocalDateTime() {
        return ToolBox.convertUtcToLocal(this.dateTime);
    }
    
    String getLogMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return new String(this.dateTime.format("YYYY-MM-DD hh:mm:ss") + "(UTC) " + this.message);
    }
}
