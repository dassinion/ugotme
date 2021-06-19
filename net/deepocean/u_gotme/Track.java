// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimeZone;
import hirondelle.date4j.DateTime;
import java.util.ArrayList;

public class Track
{
    private static int nextTrackId;
    private static TrackLogPoint previousTrackPoint;
    private int trackId;
    private ArrayList<TrackSegment> segments;
    private TrackSegment currentSegment;
    private int segmentSeparation;
    
    public Track() {
        this.trackId = Track.nextTrackId;
        ++Track.nextTrackId;
        this.segments = new ArrayList<TrackSegment>();
        TrackSegment.reset();
        this.currentSegment = new TrackSegment();
        this.segmentSeparation = Settings.getInstance().getSegmentSeparationLimit();
    }
    
    public static void reset() {
        Track.nextTrackId = 0;
        Track.previousTrackPoint = null;
    }
    
    public void finish() {
        if (!this.currentSegment.isEmptySegment()) {
            this.currentSegment.finish();
            this.segments.add(this.currentSegment);
        }
    }
    
    public void appendTrackpoint(final TrackLogPoint point) {
        final DateTime dateTime = point.getDateTime();
        long seconds;
        if (Track.previousTrackPoint != null) {
            final DateTime prevDateTime = Track.previousTrackPoint.getDateTime();
            seconds = prevDateTime.numSecondsFrom(dateTime);
        }
        else {
            seconds = 0L;
        }
        if (this.segmentSeparation > 0 && seconds > this.segmentSeparation && !this.currentSegment.isEmptySegment()) {
            this.currentSegment.finish();
            this.segments.add(this.currentSegment);
            this.currentSegment = new TrackSegment();
        }
        this.currentSegment.appendTrackpoint(point);
        Track.previousTrackPoint = point;
    }
    
    public void appendWaypoint(final TrackLogPoint point) {
        this.currentSegment.appendWaypoint(point);
    }
    
    public void appendHeartRates(final ArrayList<HeartRatePoint> points) {
        this.currentSegment.appendHeartRatePoints(points);
    }
    
    public DateTime getStartTime() {
        DateTime returnTime;
        if (this.segments.size() > 0) {
            returnTime = this.segments.get(0).getStartTime();
        }
        else {
            returnTime = null;
        }
        return returnTime;
    }
    
    public DateTime getLocalStartTime() {
        final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        final TimeZone localTimeZone = TimeZone.getDefault();
        final DateTime gmtTime = this.getStartTime();
        DateTime localTime;
        if (gmtTime != null) {
            localTime = gmtTime.changeTimeZone(gmtTimeZone, localTimeZone);
        }
        else {
            localTime = null;
        }
        return localTime;
    }
    
    public DateTime getEndTime() {
        DateTime returnTime;
        if (this.segments.size() > 0) {
            returnTime = this.segments.get(this.segments.size() - 1).getEndTime();
        }
        else {
            returnTime = null;
        }
        return returnTime;
    }
    
    public DateTime getLocalEndTime() {
        final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        final TimeZone localTimeZone = TimeZone.getDefault();
        final DateTime gmtTime = this.getEndTime();
        DateTime localTime;
        if (gmtTime != null) {
            localTime = gmtTime.changeTimeZone(gmtTimeZone, localTimeZone);
        }
        else {
            localTime = null;
        }
        return localTime;
    }
    
    public int getTrackId() {
        return this.trackId;
    }
    
    public void dumpSegments() {
        for (final TrackSegment segment : this.segments) {
            DebugLogger.info("Segment " + segment.getSegmentId() + " " + segment.getStartTime().format("YYYY-MM-DD hh:mm") + " " + segment.getEndTime().format("YYYY-MM-DD hh:mm") + " track " + segment.getNumberOfTrackpoints() + " wp " + segment.getNumberOfWaypoints() + " heart " + segment.getNumberOfHeartRatePoints());
        }
    }
    
    public ArrayList<TrackSegment> getSegments() {
        return this.segments;
    }
    
    public TrackSegment getSegment(final int segmentNumber) {
        TrackSegment segment;
        if (segmentNumber >= 0 && segmentNumber < this.segments.size()) {
            segment = this.segments.get(segmentNumber);
        }
        else {
            segment = null;
        }
        return segment;
    }
    
    public int getNumberOfSegments() {
        return this.segments.size();
    }
    
    public int getNumberOfTrackPoints() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        int number = 0;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            number += segment.getNumberOfTrackpoints();
        }
        return number;
    }
    
    public int getNumberOfWaypoints() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        int number = 0;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            number += segment.getNumberOfWaypoints();
        }
        return number;
    }
    
    public int getNumberOfHeartRatePoints() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        int number = 0;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            number += segment.getNumberOfHeartRatePoints();
        }
        return number;
    }
    
    public ArrayList<HeartRatePoint> getHeartRateValues() {
        final ArrayList<HeartRatePoint> rates = new ArrayList<HeartRatePoint>();
        for (final TrackSegment segment : this.segments) {
            rates.addAll(segment.getHeartRatePoints());
        }
        return rates;
    }
    
    public double getAverageHeartRate() {
        final ArrayList<HeartRatePoint> rates = this.getHeartRateValues();
        double averageHeartRate = 0.0;
        int count = 0;
        for (final HeartRatePoint point : rates) {
            final int heartRate = point.getHeartRateValue();
            if (heartRate > 0) {
                ++count;
                averageHeartRate += heartRate;
            }
        }
        if (count > 0) {
            averageHeartRate /= count;
        }
        else {
            averageHeartRate = -1.0;
        }
        return averageHeartRate;
    }
    
    public int getMaxHeartRate() {
        final ArrayList<HeartRatePoint> rates = this.getHeartRateValues();
        int max = -1;
        for (final HeartRatePoint point : rates) {
            final int heartRate = point.getHeartRateValue();
            if (heartRate > 0 && heartRate > max) {
                max = heartRate;
            }
        }
        return max;
    }
    
    public double getCalories() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        double calories = 0.0;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            calories += segment.getSegmentCalories();
        }
        return calories;
    }
    
    public boolean isEmptyTrack() {
        boolean isEmpty = true;
        if (this.segments.size() > 0) {
            isEmpty = false;
        }
        else if (!this.currentSegment.isEmptySegment()) {
            isEmpty = false;
        }
        return isEmpty;
    }
    
    public String getDescription() {
        final String description = "Track " + String.format("%4d", this.getTrackId()) + " " + this.getLocalStartTime().format("YYYY-MM-DD hh:mm") + " - " + this.getLocalEndTime().format("hh:mm") + " segments: " + String.format("%4d", this.getNumberOfSegments()) + " trackpnts: " + String.format("%6d", this.getNumberOfTrackPoints()) + " waypnts: " + String.format("%6d", this.getNumberOfWaypoints()) + " heartrate " + String.format("%6d", this.getNumberOfHeartRatePoints());
        return description;
    }
    
    public double getTrackDistance() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        double distance = 0.0;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            distance += segment.getSegmentDistance();
        }
        return distance;
    }
    
    public double getTrackMaxSpeed() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        double speedMax = 0.0;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            final double speed = segment.getSegmentMaxSpeed();
            if (speed > speedMax) {
                speedMax = speed;
            }
        }
        return speedMax;
    }
    
    public long getTrackDuration() {
        final Iterator<TrackSegment> iterator = this.segments.iterator();
        long duration = 0L;
        while (iterator.hasNext()) {
            final TrackSegment segment = iterator.next();
            duration += segment.getSegmentDuration();
        }
        return duration;
    }
    
    public long getTotalTrackDuration() {
        long duration = 0L;
        final int size = this.segments.size();
        if (size > 0) {
            final DateTime start = this.segments.get(0).getStartTime();
            final DateTime end = this.segments.get(size - 1).getEndTime();
            duration = start.numSecondsFrom(end);
        }
        return duration;
    }
    
    static {
        Track.nextTrackId = 0;
        Track.previousTrackPoint = null;
    }
}
