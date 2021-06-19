// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.ListIterator;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CsvWriter implements IgotuWriter
{
    private static CsvWriter theInstance;
    private static final String SEPARATOR = ";";
    
    private CsvWriter() {
    }
    
    public static CsvWriter getInstance() {
        if (CsvWriter.theInstance == null) {
            CsvWriter.theInstance = new CsvWriter();
        }
        return CsvWriter.theInstance;
    }
    
    private void writeDeviceInfo(final PrintWriter writer, final Device.DeviceType type) {
        writer.write("Device;" + Device.getDeviceTypeDescription(type) + "\n");
        writer.write("\n");
    }
    
    private void writeTrackInfo(final PrintWriter writer, final Track track) {
        writer.write("Start;" + track.getLocalStartTime().format("YYYY-MM-DD hh:mm:ss") + "\n");
        writer.write("End;" + track.getLocalEndTime().format("YYYY-MM-DD hh:mm:ss") + "\n");
        writer.write("Average heart rate (bpm);" + track.getAverageHeartRate() + "\n");
        writer.write("Maximum heart rate (bpm);" + track.getMaxHeartRate() + "\n");
        writer.write("Calories estimation (kCal);" + track.getCalories() + "\n");
        writer.write("Segments;" + track.getNumberOfSegments() + "\n");
        writer.write("Trackpoints;" + track.getNumberOfTrackPoints() + "\n");
        writer.write("Waypoints;" + track.getNumberOfWaypoints() + "\n");
        writer.write("Heartrate points;" + track.getNumberOfHeartRatePoints() + "\n");
        writer.write("\n");
    }
    
    private void writeTrackHeader(final PrintWriter writer) {
        writer.print("Date-time");
        writer.print(";");
        writer.print("Latitude (deg)");
        writer.print(";");
        writer.print("Longitude (deg)");
        writer.print(";");
        writer.print("Barometric height (m)");
        writer.print(";");
        writer.print("GPS heigth (m)");
        writer.print(";");
        writer.print("Speed (m/s)");
        writer.print(";");
        writer.print("Heading (deg)");
        writer.print(";");
        writer.print("Precision (m)");
        writer.print(";");
        writer.print("Number of satellites");
        writer.print(";");
        writer.print("Heart rate (bpm)");
        writer.print("\n");
    }
    
    private void writeTrackPoint(final PrintWriter writer, final TrackLogPoint point) {
        writer.print(point.getDateTime().format("YYYY-MM-DD hh:mm:ss"));
        writer.print(";");
        writer.print(Double.toString(point.getLatitude()));
        writer.print(";");
        writer.print(Double.toString(point.getLongitude()));
        writer.print(";");
        writer.print(Double.toString(point.getElevationByAtmosphericPresure()));
        writer.print(";");
        writer.print(Double.toString(point.getElevationByGps()));
        writer.print(";");
        writer.print(Double.toString(point.getSpeed()));
        writer.print(";");
        writer.print(Double.toString(point.getCourse()));
        writer.print(";");
        writer.print(point.getEhpe());
        writer.print(";");
        writer.print(point.getSatelliteNumber());
        writer.print(";");
        writer.print(point.getHeartRate());
        writer.print("\n");
    }
    
    public void writeTrackToFile(final String fileName, final int trackNo, final String trackName) {
        final TrackLog log = TrackLog.getInstance();
        final Track track = log.getTrack(trackNo);
        try {
            final FileWriter fw = new FileWriter(fileName);
            final PrintWriter pw = new PrintWriter(fw);
            this.writeDeviceInfo(pw, log.getDeviceType());
            this.writeTrackInfo(pw, track);
            pw.write("WAYPOINTS\n");
            this.writeTrackHeader(pw);
            ArrayList<TrackSegment> segments = track.getSegments();
            for (final TrackSegment segment : segments) {
                final ArrayList<TrackLogPoint> points = segment.getWayPoints();
                for (final TrackLogPoint point : points) {
                    this.writeTrackPoint(pw, point);
                }
            }
            pw.write("\n");
            pw.write("TRACKPOINTS\n");
            this.writeTrackHeader(pw);
            segments = track.getSegments();
            for (final TrackSegment segment : segments) {
                final ArrayList<TrackLogPoint> points = segment.getTrackPoints();
                for (final TrackLogPoint point : points) {
                    this.writeTrackPoint(pw, point);
                }
            }
            pw.flush();
            pw.close();
            fw.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing to file " + fileName + ": " + e.toString());
        }
    }
    
    private void writeWaypointInfo(final PrintWriter writer, final WaypointLog log) {
        writer.print("Number of waypoints;" + log.getNumberOfEntries() + "\n");
        writer.print("\n");
    }
    
    private void writeWaypointHeader(final PrintWriter writer) {
        writer.print("ID");
        writer.print(";");
        writer.print("Latitude (deg)");
        writer.print(";");
        writer.print("Longitude (deg)");
        writer.print(";");
        writer.print("Elevation (m)");
        writer.print("\n");
    }
    
    private void writeWaypoint(final PrintWriter writer, final Waypoint point) {
        writer.print(point.getId());
        writer.print(";");
        writer.print(point.getLatitude());
        writer.print(";");
        writer.print(point.getLongitude());
        writer.print(";");
        writer.print(point.getElevation());
        writer.print("\n");
    }
    
    public void writeWaypointsToFile(final String fileName) {
        final WaypointLog log = WaypointLog.getInstance();
        final ArrayList<Waypoint> points = log.getWaypoints();
        final Iterator<Waypoint> iterator = points.iterator();
        try {
            final FileWriter fw = new FileWriter(fileName);
            final PrintWriter pw = new PrintWriter(fw);
            this.writeDeviceInfo(pw, log.getDeviceType());
            this.writeWaypointInfo(pw, log);
            pw.write("WAYPOINT LOG\n");
            this.writeWaypointHeader(pw);
            while (iterator.hasNext()) {
                final Waypoint point = iterator.next();
                this.writeWaypoint(pw, point);
            }
            pw.write("\n");
            pw.flush();
            pw.close();
            fw.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing to file " + fileName + ": " + e.toString());
        }
    }
    
    private void writeRouteInfo(final PrintWriter writer, final RouteLog log) {
        writer.print("Number of route points;" + log.getNumberOfEntries() + "\n");
        writer.print("\n");
    }
    
    private void writeRouteHeader(final PrintWriter writer) {
        writer.print("Name");
        writer.print(";");
        writer.print("Latitude (deg)");
        writer.print(";");
        writer.print("Longitude (deg)");
        writer.print(";");
        writer.print("Elevation (m)");
        writer.print(";");
        writer.print("Symbol");
        writer.print("\n");
    }
    
    private void writeRoutePoint(final PrintWriter writer, final RoutePoint point) {
        writer.print(point.getName());
        writer.print(";");
        writer.print(point.getLatitude());
        writer.print(";");
        writer.print(point.getLongitude());
        writer.print(";");
        writer.print(point.getElevation());
        writer.print(";");
        writer.print(point.getSymbolName());
        writer.print("\n");
    }
    
    public void writeRouteToFile(final String fileName) {
        final RouteLog log = RouteLog.getInstance();
        final ArrayList<RoutePoint> points = log.getWaypoints();
        final Iterator<RoutePoint> iterator = points.iterator();
        try {
            final FileWriter fw = new FileWriter(fileName);
            final PrintWriter pw = new PrintWriter(fw);
            this.writeDeviceInfo(pw, log.getDeviceType());
            this.writeRouteInfo(pw, log);
            pw.write("ROUTE\n");
            this.writeRouteHeader(pw);
            while (iterator.hasNext()) {
                final RoutePoint point = iterator.next();
                this.writeRoutePoint(pw, point);
            }
            pw.write("\n");
            pw.flush();
            pw.close();
            fw.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing Route to file " + fileName + ": " + e.toString());
        }
    }
    
    public void writeHeartRateToFile(final String fileName, final int trackNo) {
        final TrackLog log = TrackLog.getInstance();
        final List<HeartRatePoint> rates = log.getTrackHeartRateLog(trackNo);
        if (rates != null) {
            try {
                final FileWriter fw = new FileWriter(fileName);
                final PrintWriter pw = new PrintWriter(fw);
                pw.print("DateTime (UTC)");
                pw.print(",");
                pw.print("heart rate (bpm)\n");
                final ListIterator<HeartRatePoint> iterator = rates.listIterator();
                while (iterator.hasNext()) {
                    final HeartRatePoint rate = iterator.next();
                    if (rate.getHeartRateValue() > 0) {
                        pw.print(rate.getDateTime().format("YYYY-MM-DD hh:mm:ss"));
                        pw.print(",");
                        pw.print(rate.getHeartRateValue());
                        pw.print("\n");
                    }
                }
                pw.flush();
                pw.close();
                fw.close();
            }
            catch (IOException e) {
                DebugLogger.error("Error writing to file " + fileName + ": " + e.toString());
            }
        }
    }
    
    static {
        CsvWriter.theInstance = null;
    }
}
