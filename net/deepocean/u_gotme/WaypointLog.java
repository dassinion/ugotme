// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.Iterator;
import java.util.ArrayList;

public class WaypointLog
{
    private static WaypointLog theInstance;
    private ArrayList<Waypoint> theLog;
    private Device.DeviceType deviceType;
    
    private WaypointLog() {
        this.theLog = new ArrayList<Waypoint>();
        this.deviceType = Device.DeviceType.DEVICETYPE_UNKNOWN;
    }
    
    public static WaypointLog getInstance() {
        if (WaypointLog.theInstance == null) {
            WaypointLog.theInstance = new WaypointLog();
        }
        return WaypointLog.theInstance;
    }
    
    public void clear() {
        this.theLog.clear();
    }
    
    public boolean appendData(final byte[] data, final int length) {
        if (length % Waypoint.WAYPOINT_RECORDSIZE != 0) {
            DebugLogger.error("Waypoint data has invalid length");
        }
        boolean hasMoreData = true;
        int count = 0;
        final int maxCount = length / Waypoint.WAYPOINT_RECORDSIZE;
        while (hasMoreData && count < maxCount) {
            final int offset = Waypoint.WAYPOINT_RECORDSIZE * count;
            final int start = (data[offset + 0] & 0xFF) + ((data[offset + 1] & 0xFF) << 8) + ((data[offset + 2] & 0xFF) << 16) + ((data[offset + 3] & 0xFF) << 24);
            if (start == 35670106) {
                final int latitude = (data[offset + 4] & 0xFF) + ((data[offset + 5] & 0xFF) << 8) + ((data[offset + 6] & 0xFF) << 16) + (data[offset + 7] << 24);
                final int longitude = (data[offset + 8] & 0xFF) + ((data[offset + 9] & 0xFF) << 8) + ((data[offset + 10] & 0xFF) << 16) + (data[offset + 11] << 24);
                final int elevation = (data[offset + 12] & 0xFF) + ((data[offset + 13] & 0xFF) << 8) + ((data[offset + 14] & 0xFF) << 16) + (data[offset + 15] << 24);
                final long time = (data[offset + 16] & 0xFF) + ((data[offset + 17] & 0xFF) << 8) + ((data[offset + 18] & 0xFF) << 16) + ((data[offset + 19] & 0xFF) << 24);
                final int id = (data[offset + 20] & 0xFF) + ((data[offset + 21] & 0xFF) << 8) + ((data[offset + 22] & 0xFF) << 16) + ((data[offset + 23] & 0xFF) << 24);
                final Waypoint point = new Waypoint(time, longitude, latitude, elevation, id);
                this.theLog.add(point);
                ++count;
            }
            else {
                hasMoreData = false;
            }
        }
        return hasMoreData;
    }
    
    public int getNumberOfEntries() {
        return this.theLog.size();
    }
    
    public void dumpLog() {
        for (final Waypoint point : this.theLog) {
            DebugLogger.info(point.toString());
        }
    }
    
    public ArrayList<Waypoint> getWaypoints() {
        return this.theLog;
    }
    
    public void setDeviceType(final Device.DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public Device.DeviceType getDeviceType() {
        return this.deviceType;
    }
    
    static {
        WaypointLog.theInstance = null;
    }
}
