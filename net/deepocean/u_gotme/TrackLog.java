// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;
import java.util.Iterator;
import java.util.ArrayList;

public class TrackLog
{
    private static boolean counterResetFound;
    private static int prevMillis;
    private static TrackLogPoint previousPoint;
    private static int RECORDLENGTH;
    static TrackLog theInstance;
    private byte[] record;
    private ArrayList<Track> tracks;
    private Device.DeviceType deviceType;
    private Track currentTrack;
    private boolean stateIsLogging;
    
    private TrackLog() {
        this.record = new byte[TrackLog.RECORDLENGTH];
        this.tracks = new ArrayList<Track>();
    }
    
    public static TrackLog getInstance() {
        if (TrackLog.theInstance == null) {
            TrackLog.theInstance = new TrackLog();
        }
        return TrackLog.theInstance;
    }
    
    public void appendData(final byte[] data, final int length) {
        if (length % TrackLog.RECORDLENGTH != 0) {
            DebugLogger.error("Datalenght not a multiple of 0x20 bytes");
        }
        else {
            for (int records = length / TrackLog.RECORDLENGTH, i = 0; i < records; ++i) {
                final boolean trackStartFound = false;
                ToolBox.copyBytes(data, this.record, i * TrackLog.RECORDLENGTH, TrackLog.RECORDLENGTH);
                final int flags = this.record[0] & 0xFF;
                final int millis = (this.record[4] & 0xFF) << 8 | (this.record[5] & 0xFF);
                final int recordPrevMillis = (this.record[30] & 0xFF) << 8 | (this.record[31] & 0xFF);
                if (this.deviceType == Device.DeviceType.DEVICETYPE_GT820 || this.deviceType == Device.DeviceType.DEVICETYPE_GT820PRO || this.deviceType == Device.DeviceType.DEVICETYPE_GT900 || this.deviceType == Device.DeviceType.DEVICETYPE_GT900PRO) {
                    if (flags == 241) {
                        final byte[] logBytes = new byte[24];
                        ToolBox.copyBytes(this.record, logBytes, 6, 24);
                        final String logString = new String(logBytes);
                        if (logString.startsWith("RESET COUNTER")) {
                            if (this.stateIsLogging) {
                                this.newTrack();
                            }
                        }
                        else if (logString.startsWith("POWER UP")) {
                            if (!this.stateIsLogging) {
                                this.newTrack();
                            }
                            this.stateIsLogging = true;
                        }
                        else if (logString.startsWith("!!!SYSTEM OFF")) {
                            this.stateIsLogging = false;
                        }
                    }
                    else if (flags == 245) {
                        final ArrayList<HeartRatePoint> newHeartRatePoints = HeartRatePoint.parseHeartRateRecord(this.record);
                        this.currentTrack.appendHeartRates(newHeartRatePoints);
                    }
                    else if ((flags & 0x10) == 0x0) {
                        final TrackLogPoint point = new TrackLogPoint(this.record, this.deviceType);
                        if (point.isWaypoint()) {
                            this.currentTrack.appendWaypoint(point);
                        }
                        else {
                            this.currentTrack.appendTrackpoint(point);
                        }
                    }
                }
                else if (this.deviceType == Device.DeviceType.DEVICETYPE_GT800 || this.deviceType == Device.DeviceType.DEVICETYPE_GT800PRO) {
                    if ((flags & 0x10) == 0x0) {
                        if ((flags & 0x40) != 0x0) {
                            this.newTrack();
                        }
                        final TrackLogPoint point = new TrackLogPoint(this.record, this.deviceType);
                        if (point.isWaypoint()) {
                            this.currentTrack.appendWaypoint(point);
                        }
                        else {
                            this.currentTrack.appendTrackpoint(point);
                        }
                    }
                }
                else if (this.deviceType == Device.DeviceType.DEVICETYPE_GT120) {
                    if ((flags & 0x40) != 0x0) {
                        this.newTrack();
                    }
                    else if ((flags & 0x10) == 0x0 && this.record[1] != 0) {
                        final TrackLogPoint point = new TrackLogPoint(this.record, this.deviceType);
                        if (point.isWaypoint()) {
                            this.currentTrack.appendWaypoint(point);
                        }
                        else {
                            this.currentTrack.appendTrackpoint(point);
                        }
                    }
                }
                if (TrackLog.prevMillis != 65535 && (this.deviceType == Device.DeviceType.DEVICETYPE_GT800 || this.deviceType == Device.DeviceType.DEVICETYPE_GT800PRO || this.deviceType == Device.DeviceType.DEVICETYPE_GT820 || this.deviceType == Device.DeviceType.DEVICETYPE_GT820PRO || this.deviceType == Device.DeviceType.DEVICETYPE_GT900 || this.deviceType == Device.DeviceType.DEVICETYPE_GT900PRO) && recordPrevMillis != TrackLog.prevMillis) {
                    DebugLogger.error("Missing record(s) detected while parsing waypoints");
                }
                TrackLog.prevMillis = millis;
            }
        }
    }
    
    public void initialiseTracklog() {
        this.tracks.clear();
        Track.reset();
        TrackLog.counterResetFound = false;
        TrackLog.prevMillis = 65535;
        TrackLog.previousPoint = null;
        this.currentTrack = new Track();
        this.stateIsLogging = false;
    }
    
    public void finishTracklog() {
        if (!this.currentTrack.isEmptyTrack()) {
            this.currentTrack.finish();
            this.tracks.add(this.currentTrack);
        }
    }
    
    private void newTrack() {
        if (!this.currentTrack.isEmptyTrack()) {
            this.currentTrack.finish();
            this.tracks.add(this.currentTrack);
            this.currentTrack = new Track();
        }
    }
    
    int getNumberOfRecords() {
        int records = 0;
        for (final Track track : this.tracks) {
            records += track.getNumberOfTrackPoints();
            records += track.getNumberOfWaypoints();
        }
        return records;
    }
    
    void dumpTracks() {
        for (final Track track : this.tracks) {
            DebugLogger.info("Track: " + track.getTrackId());
            track.dumpSegments();
        }
    }
    
    public int getNumberOfTracks() {
        return this.tracks.size();
    }
    
    public int getNumberOfTrackSegments(final int trackNumber) {
        int number;
        if (trackNumber >= 0 && trackNumber < this.tracks.size()) {
            final Track track = this.tracks.get(trackNumber);
            number = track.getNumberOfSegments();
        }
        else {
            number = 0;
        }
        return number;
    }
    
    public ArrayList<TrackLogPoint> getTrackPoints(final int trackNumber, final int segmentNumber) {
        return this.tracks.get(trackNumber).getSegment(segmentNumber).getTrackPoints();
    }
    
    public ArrayList<TrackLogPoint> getWayPoints(final int trackNumber, final int segmentNumber) {
        return this.tracks.get(trackNumber).getSegment(segmentNumber).getWayPoints();
    }
    
    public DateTime getTrackStartTime(final int trackNumber) {
        return this.tracks.get(trackNumber).getStartTime();
    }
    
    public ArrayList<HeartRatePoint> getTrackHeartRateLog(final int trackNumber) {
        return this.tracks.get(trackNumber).getHeartRateValues();
    }
    
    public ArrayList<HeartRatePoint> getTrackSegmentHeartRateLog(final int trackNumber, final int segmentNumber) {
        return this.tracks.get(trackNumber).getSegment(segmentNumber).getHeartRatePoints();
    }
    
    public double getAverageHeartRate(final int trackNumber) {
        return this.tracks.get(trackNumber).getAverageHeartRate();
    }
    
    public String getTrackDescription(final int trackNumber) {
        String description;
        if (trackNumber >= 0 && trackNumber < this.tracks.size()) {
            final Track track = this.tracks.get(trackNumber);
            description = track.getDescription();
        }
        else {
            description = "Non existing track";
        }
        return description;
    }
    
    public void setDeviceType(final Device.DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public Device.DeviceType getDeviceType() {
        return this.deviceType;
    }
    
    public Track getTrack(final int trackId) {
        return this.tracks.get(trackId);
    }
    
    static {
        TrackLog.counterResetFound = false;
        TrackLog.prevMillis = 65535;
        TrackLog.previousPoint = null;
        TrackLog.RECORDLENGTH = 32;
        TrackLog.theInstance = null;
    }
}
