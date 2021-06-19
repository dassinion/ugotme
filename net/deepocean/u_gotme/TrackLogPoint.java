// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;

public class TrackLogPoint extends Coordinate
{
    public static final int TRACKLOGPOINT_RECORDSIZE = 32;
    int flags;
    DateTime dateTime;
    int status;
    int ehpe;
    int satelliteMap;
    int elevationPressure;
    int elevationGps;
    int speed;
    int course;
    int timeout;
    int maxFound;
    int satelliteNumber;
    int weightCriteria;
    int sleepTime;
    Device.DeviceType deviceType;
    boolean pointIsStartOfTrack;
    boolean pointIsEndOfTrack;
    int heartRate;
    int cadence;
    
    public TrackLogPoint(final byte[] record, final Device.DeviceType deviceType) {
        this.pointIsEndOfTrack = false;
        this.pointIsStartOfTrack = false;
        this.deviceType = deviceType;
        this.flags = (record[0] & 0xFF);
        this.dateTime = ToolBox.bytesToDateTime(record[1], record[2], record[3], record[4], record[5]);
        this.status = (record[6] & 0xF0) >> 4;
        this.ehpe = ((record[6] & 0xF0) << 4 | (record[7] & 0xFF));
        this.latitude = (record[12] << 24 | (record[13] & 0xFF) << 16 | (record[14] & 0xFF) << 8 | (record[15] & 0xFF));
        this.longitude = (record[16] << 24 | (record[17] & 0xFF) << 16 | (record[18] & 0xFF) << 8 | (record[19] & 0xFF));
        this.elevationPressure = (record[8] << 24 | (record[9] & 0xFF) << 16 | (record[10] & 0xFF) << 8 | (record[11] & 0xFF));
        this.elevationGps = (record[20] << 24 | (record[21] & 0xFF) << 16 | (record[22] & 0xFF) << 8 | (record[23] & 0xFF));
        if (deviceType == Device.DeviceType.DEVICETYPE_GT800PRO || deviceType == Device.DeviceType.DEVICETYPE_GT820PRO || deviceType == Device.DeviceType.DEVICETYPE_GT900PRO) {
            this.elevation = this.elevationPressure;
        }
        else {
            this.elevation = this.elevationGps;
        }
        this.speed = ((record[24] & 0xFF) << 8 | (record[25] & 0xFF));
        this.course = ((record[26] & 0xFF) << 8 | (record[27] & 0xFF));
        this.timeout = (record[28] & 0xFF);
        this.satelliteNumber = (record[29] & 0xF);
        this.cadence = -1;
        this.heartRate = -1;
    }
    
    @Override
    public String toString() {
        final String outputString = new String("");
        return outputString;
    }
    
    public DateTime getDateTime() {
        return this.dateTime;
    }
    
    public double getEhpe() {
        return this.ehpe / 100.0 * 16.0;
    }
    
    public double getElevationByAtmosphericPresure() {
        return this.elevationPressure / 100.0;
    }
    
    public double getElevationByGps() {
        return this.elevationGps / 100.0;
    }
    
    public boolean isStartOfTrack() {
        return (this.flags & 0x40) != 0x0;
    }
    
    public boolean isEndOfTrack() {
        return (this.flags & 0x20) != 0x0;
    }
    
    public boolean isWaypoint() {
        return (this.flags & 0x4) != 0x0;
    }
    
    public double getCourse() {
        return this.course / 100.0;
    }
    
    public double getSpeed() {
        return this.speed / 100.0;
    }
    
    public int getSatelliteNumber() {
        return this.satelliteNumber;
    }
    
    public Device.DeviceType getDeviceType() {
        return this.deviceType;
    }
    
    public void setHeartRate(final int rate) {
        this.heartRate = rate;
    }
    
    public int getHeartRate() {
        return this.heartRate;
    }
    
    public void setCadence(final int cadence) {
        this.cadence = cadence;
    }
    
    public int getCadence() {
        return this.cadence;
    }
}
