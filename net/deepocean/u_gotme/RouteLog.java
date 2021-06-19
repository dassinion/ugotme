// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.Iterator;
import java.util.ArrayList;

public class RouteLog
{
    private static RouteLog theInstance;
    private ArrayList<RoutePoint> theLog;
    private Device.DeviceType deviceType;
    private static int nextRoutePointNumber;
    
    private RouteLog() {
        this.theLog = new ArrayList<RoutePoint>();
        this.deviceType = Device.DeviceType.DEVICETYPE_UNKNOWN;
        this.clear();
    }
    
    public static RouteLog getInstance() {
        if (RouteLog.theInstance == null) {
            RouteLog.theInstance = new RouteLog();
        }
        return RouteLog.theInstance;
    }
    
    public void clear() {
        this.theLog.clear();
        RouteLog.nextRoutePointNumber = 1;
    }
    
    public boolean appendData(final byte[] data, final int length) {
        final byte[] theData = new byte[288];
        if (length % 288 != 0) {
            DebugLogger.error("Route Waypoint data has invalid length");
        }
        boolean hasMoreData = true;
        int count = 0;
        final int maxCount = length / 288;
        while (hasMoreData && count < maxCount) {
            final int offset = 288 * count;
            final int start = (data[offset + 0] & 0xFF) + ((data[offset + 1] & 0xFF) << 8) + ((data[offset + 2] & 0xFF) << 16) + ((data[offset + 3] & 0xFF) << 24);
            if (start == 35670200) {
                for (int i = 0; i < 288; ++i) {
                    theData[i] = data[offset + i];
                }
                final RoutePoint point = new RoutePoint(theData, RouteLog.nextRoutePointNumber);
                this.theLog.add(point);
                ++RouteLog.nextRoutePointNumber;
                ++count;
            }
            else {
                hasMoreData = false;
            }
        }
        return hasMoreData;
    }
    
    public void appendWaypoint(final RoutePoint waypoint) {
        this.theLog.add(waypoint);
    }
    
    public int getNumberOfEntries() {
        return this.theLog.size();
    }
    
    public void dumpLog() {
        for (final RoutePoint point : this.theLog) {
            DebugLogger.info(point.toString());
        }
    }
    
    public ArrayList<RoutePoint> getWaypoints() {
        return this.theLog;
    }
    
    public byte[] getWaypointAsByteArray(final int index) {
        byte[] record;
        if (index < this.theLog.size()) {
            record = this.theLog.get(index).getRecordAsByteArray();
        }
        else {
            record = null;
        }
        return record;
    }
    
    public void setDeviceType(final Device.DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public Device.DeviceType getDeviceType() {
        return this.deviceType;
    }
    
    static {
        RouteLog.theInstance = null;
    }
}
