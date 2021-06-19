// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class TrackSegment
{
    private static int nextSegmentId;
    private int trackSegmentNo;
    private ArrayList<TrackLogPoint> trackPoints;
    private ArrayList<TrackLogPoint> wayPoints;
    private ArrayList<HeartRatePoint> heartRatePoints;
    
    public TrackSegment() {
        this.trackSegmentNo = TrackSegment.nextSegmentId;
        ++TrackSegment.nextSegmentId;
        this.trackPoints = new ArrayList<TrackLogPoint>();
        this.wayPoints = new ArrayList<TrackLogPoint>();
        this.heartRatePoints = new ArrayList<HeartRatePoint>();
    }
    
    public static void reset() {
        TrackSegment.nextSegmentId = 0;
    }
    
    public void appendTrackpoint(final TrackLogPoint point) {
        this.trackPoints.add(point);
    }
    
    public void appendWaypoint(final TrackLogPoint point) {
        this.wayPoints.add(point);
    }
    
    public void appendHeartRatePoints(final ArrayList<HeartRatePoint> points) {
        this.heartRatePoints.addAll(points);
    }
    
    public void finish() {
        this.parseHeartRates();
    }
    
    private void parseHeartRates() {
        HeartRatePoint heartRatePoint = null;
        TrackLogPoint trackPoint = null;
        final Iterator<TrackLogPoint> trackLogIterator = this.trackPoints.iterator();
        final Iterator<HeartRatePoint> heartRateIterator = this.heartRatePoints.iterator();
        boolean exit = false;
        if (heartRateIterator.hasNext()) {
            heartRatePoint = heartRateIterator.next();
        }
        else {
            heartRatePoint = null;
            exit = true;
        }
        if (trackLogIterator.hasNext()) {
            trackPoint = trackLogIterator.next();
        }
        else {
            trackPoint = null;
            exit = true;
        }
        double sum = 0.0;
        int count = 0;
        while (!exit) {
            final DateTime trackPointDateTime = trackPoint.getDateTime();
            final DateTime heartRateDateTime = heartRatePoint.getDateTime();
            if (trackPoint.pointIsStartOfTrack) {
                sum = 0.0;
                count = 0;
            }
            if (heartRateDateTime.lteq(trackPointDateTime)) {
                if (heartRatePoint.getHeartRateValue() > 0) {
                    sum += heartRatePoint.getHeartRateValue();
                    ++count;
                }
                if (heartRateIterator.hasNext()) {
                    heartRatePoint = heartRateIterator.next();
                }
                else {
                    heartRatePoint = null;
                    exit = true;
                }
            }
            else {
                if (count > 0) {
                    trackPoint.setHeartRate((int)(sum / count));
                    sum = 0.0;
                    count = 0;
                }
                else {
                    trackPoint.setHeartRate(-1);
                }
                if (trackLogIterator.hasNext()) {
                    trackPoint = trackLogIterator.next();
                }
                else {
                    trackPoint = null;
                    exit = true;
                }
            }
        }
        if (count > 0 && trackPoint != null) {
            trackPoint.setHeartRate((int)(sum / count));
        }
        while (trackLogIterator.hasNext()) {
            trackPoint = trackLogIterator.next();
            trackPoint.setHeartRate(-1);
        }
    }
    
    public int getSegmentId() {
        return this.trackSegmentNo;
    }
    
    public int getNumberOfWaypoints() {
        return this.wayPoints.size();
    }
    
    public int getNumberOfTrackpoints() {
        return this.trackPoints.size();
    }
    
    public int getNumberOfHeartRatePoints() {
        return this.heartRatePoints.size();
    }
    
    public boolean isEmptySegment() {
        final boolean isEmpty = this.wayPoints.size() <= 0 && this.trackPoints.size() <= 0 && this.heartRatePoints.size() <= 0;
        return isEmpty;
    }
    
    public ArrayList<TrackLogPoint> getTrackPoints() {
        return this.trackPoints;
    }
    
    public ArrayList<TrackLogPoint> getWayPoints() {
        return this.wayPoints;
    }
    
    public ArrayList<HeartRatePoint> getHeartRatePoints() {
        return this.heartRatePoints;
    }
    
    public DateTime getStartTime() {
        DateTime dateTime = null;
        if (this.trackPoints.size() > 0) {
            dateTime = this.trackPoints.get(0).getDateTime();
        }
        if (this.wayPoints.size() > 0) {
            final DateTime pointDateTime = this.wayPoints.get(0).getDateTime();
            if (dateTime == null) {
                dateTime = pointDateTime;
            }
            else if (pointDateTime.lt(dateTime)) {
                dateTime = pointDateTime;
            }
        }
        if (this.heartRatePoints.size() > 0) {
            final DateTime pointDateTime = this.heartRatePoints.get(0).getDateTime();
            if (dateTime == null) {
                dateTime = pointDateTime;
            }
            else if (pointDateTime.lt(dateTime)) {
                dateTime = pointDateTime;
            }
        }
        return dateTime;
    }
    
    public DateTime getEndTime() {
        DateTime dateTime = null;
        if (this.trackPoints.size() > 0) {
            dateTime = this.trackPoints.get(this.trackPoints.size() - 1).getDateTime();
        }
        if (this.wayPoints.size() > 0) {
            final DateTime pointDateTime = this.wayPoints.get(this.wayPoints.size() - 1).getDateTime();
            if (dateTime == null) {
                dateTime = pointDateTime;
            }
            else if (pointDateTime.gt(dateTime)) {
                dateTime = pointDateTime;
            }
        }
        if (this.heartRatePoints.size() > 0) {
            final DateTime pointDateTime = this.heartRatePoints.get(this.heartRatePoints.size() - 1).getDateTime();
            if (dateTime == null) {
                dateTime = pointDateTime;
            }
            else if (pointDateTime.gt(dateTime)) {
                dateTime = pointDateTime;
            }
        }
        return dateTime;
    }
    
    public double getSegmentDistance() {
        double distance = 0.0;
        final double radius = 6371000.0;
        final Iterator<TrackLogPoint> iterator = this.trackPoints.iterator();
        if (iterator.hasNext()) {
            TrackLogPoint prevPoint = iterator.next();
            while (iterator.hasNext()) {
                final TrackLogPoint point = iterator.next();
                final double lat1 = 6.283185307179586 * prevPoint.getLatitude() / 360.0;
                final double lon1 = 6.283185307179586 * prevPoint.getLongitude() / 360.0;
                final double lat2 = 6.283185307179586 * point.getLatitude() / 360.0;
                final double lon2 = 6.283185307179586 * point.getLongitude() / 360.0;
                distance += Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * radius;
                prevPoint = point;
            }
        }
        return distance;
    }
    
    public double getSegmentMaxSpeed() {
        double speedMax = 0.0;
        for (final TrackLogPoint point : this.trackPoints) {
            final double speed = point.getSpeed();
            if (speed > speedMax) {
                speedMax = speed;
            }
        }
        return speedMax;
    }
    
    public long getSegmentDuration() {
        long duration = 0L;
        final DateTime start = this.getStartTime();
        final DateTime end = this.getEndTime();
        if (start != null && end != null) {
            duration = start.numSecondsFrom(end);
        }
        return duration;
    }
    
    public double getSegmentCalories() {
        double calories = 0.0;
        final Settings settings = Settings.getInstance();
        final double age = settings.getProfileAge();
        final double length = settings.getProfileLength();
        final double weight = settings.getProfileWeight();
        final String gender = settings.getProfileGender();
        final double metabolicEquivalent = settings.getMetabolicEquivalent();
        final boolean isMale = gender.equals("male");
        double restMetabolicRate;
        if (isMale) {
            restMetabolicRate = 10.0 * weight + 6.25 * length - 5.0 * age + 5.0;
        }
        else {
            restMetabolicRate = 10.0 * weight + 6.25 * length - 5.0 * age - 161.0;
        }
        final Iterator<TrackLogPoint> iterator = this.trackPoints.iterator();
        if (iterator.hasNext()) {
            TrackLogPoint prevPoint = iterator.next();
            while (iterator.hasNext()) {
                final TrackLogPoint point = iterator.next();
                final DateTime time = point.getDateTime();
                final DateTime prevTime = prevPoint.getDateTime();
                final double seconds = (double)prevTime.numSecondsFrom(time);
                final double rate = point.getHeartRate();
                double burned;
                if (rate > 0.0) {
                    if (isMale) {
                        burned = (age * 0.2017 + weight * 0.1988 + rate * 0.6309 - 55.0969) * (seconds / 60.0) / 4.184;
                    }
                    else {
                        burned = (age * 0.074 + weight * 0.1263 + rate * 0.4472 - 20.4022) * (seconds / 60.0) / 4.184;
                    }
                }
                else {
                    burned = metabolicEquivalent * restMetabolicRate / 24.0 * seconds / 3600.0;
                }
                calories += burned;
                prevPoint = point;
            }
        }
        return calories;
    }
    
    static {
        TrackSegment.nextSegmentId = 0;
    }
}
