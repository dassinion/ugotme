// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.awt.image.BufferedImage;
import javax.swing.Icon;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;

public class MapFrame extends JFrame
{
    private static final int MAXSTRINGLENGTH = 1700;
    private static final int MAXCOMPRESSIONPARAMS = 7;
    private static final int[] compressionParamPoints;
    private static final double[] compressionParamMaxSlopeDev;
    private int compressionParamIndex;
    JLabel label;
    Coordinate firstPoint;
    Coordinate lastPoint;
    int totalPointCount;
    int compressedPointCount;
    Coordinate previousEncodedPoint;
    double maxSlopeDeviation;
    String resultString;
    public static final String MAPTYPE_ROAD = "roadmap";
    public static final String MAPTYPE_SATELLITE = "satellite";
    public static final String MAPTYPE_TERRAIN = "terrain";
    public static final String MAPTYPE_HYBRID = "hybrid";
    private static String mapType;
    
    public MapFrame() {
        this.compressionParamIndex = 6;
        this.previousEncodedPoint = null;
        this.maxSlopeDeviation = 5.0E-4;
        this.setResizable(false);
        final JPanel panel = new JPanel();
        panel.add(this.label = new JLabel());
        this.add(panel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public static String getMapType() {
        return MapFrame.mapType;
    }
    
    public static void setMapType(final String newMapType) {
        if (newMapType.equals("roadmap") || newMapType.equals("satellite") || newMapType.equals("terrain") || newMapType.equals("hybrid")) {
            MapFrame.mapType = newMapType;
            DebugLogger.info("Map type set to " + MapFrame.mapType);
        }
    }
    
    private String getMapString() {
        final String mapString = "http://maps.googleapis.com/maps/api/staticmap?size=480x480&sensor=false&maptype=" + MapFrame.mapType;
        return mapString;
    }
    
    void setCompressionParameter(final int numberOfPoints) {
        boolean found = false;
        this.compressionParamIndex = 6;
        while (!found && this.compressionParamIndex > 0) {
            if (numberOfPoints < MapFrame.compressionParamPoints[this.compressionParamIndex]) {
                --this.compressionParamIndex;
            }
            else {
                found = true;
            }
        }
        this.maxSlopeDeviation = MapFrame.compressionParamMaxSlopeDev[this.compressionParamIndex];
        DebugLogger.debug("Number of points: " + numberOfPoints + " factor: " + this.maxSlopeDeviation);
    }
    
    boolean setHigherCompression() {
        boolean done = false;
        if (this.compressionParamIndex < 6) {
            ++this.compressionParamIndex;
            this.maxSlopeDeviation = MapFrame.compressionParamMaxSlopeDev[this.compressionParamIndex];
            DebugLogger.debug("Next factor: " + this.maxSlopeDeviation);
            done = true;
        }
        return done;
    }
    
    void resetCompression() {
        this.firstPoint = null;
        this.lastPoint = null;
        this.totalPointCount = 0;
        this.compressedPointCount = 0;
    }
    
    Coordinate compress(final Coordinate point) {
        Coordinate returnPoint = null;
        ++this.totalPointCount;
        if (this.firstPoint == null) {
            returnPoint = point;
            this.firstPoint = point;
        }
        else if (this.lastPoint == null) {
            this.lastPoint = point;
        }
        else {
            final Coordinate midPoint = this.lastPoint;
            this.lastPoint = point;
            final double x1 = this.firstPoint.getLongitude();
            final double y1 = this.firstPoint.getLatitude();
            final double xm = midPoint.getLongitude();
            final double ym = midPoint.getLatitude();
            final double x2 = this.lastPoint.getLongitude();
            final double y2 = this.lastPoint.getLatitude();
            final double factor = Math.sqrt((ym - y1) * (ym - y1) + (xm - x1) * (xm - x1)) * this.maxSlopeDeviation;
            boolean deviates = false;
            double averageSlope = xm - x1;
            double slope = x2 - xm;
            if (slope >= averageSlope + factor || slope <= averageSlope - factor) {
                deviates = true;
            }
            averageSlope = ym - y1;
            slope = y2 - ym;
            if (slope >= averageSlope + factor || slope <= averageSlope - factor) {
                deviates = true;
            }
            if (deviates) {
                returnPoint = midPoint;
                this.firstPoint = midPoint;
            }
        }
        if (returnPoint != null) {
            ++this.compressedPointCount;
        }
        return returnPoint;
    }
    
    Coordinate finishCompression() {
        DebugLogger.info("Track compressed: skipped " + (this.totalPointCount - this.compressedPointCount) + " points out of " + this.totalPointCount);
        if (this.totalPointCount > 0) {
            DebugLogger.debug("Compressed to " + 100 * this.compressedPointCount / this.totalPointCount + "% @ max. allowed slope delta " + this.maxSlopeDeviation);
        }
        return this.lastPoint;
    }
    
    private void resetPointEncoding() {
        this.previousEncodedPoint = null;
    }
    
    private String encodePoint(final Coordinate point) {
        String encodedPointString = "";
        if (this.previousEncodedPoint == null) {
            encodedPointString += this.encodeValue(point.getLatitude());
            encodedPointString += this.encodeValue(point.getLongitude());
        }
        else {
            final double deltaLat = point.getLatitude() - this.previousEncodedPoint.getLatitude();
            final double deltaLon = point.getLongitude() - this.previousEncodedPoint.getLongitude();
            encodedPointString += this.encodeValue(deltaLat);
            encodedPointString += this.encodeValue(deltaLon);
        }
        this.previousEncodedPoint = point;
        return encodedPointString;
    }
    
    private String encodeValue(double value) {
        String conversion = "";
        boolean finished = false;
        boolean isNegative;
        if (value < 0.0) {
            value = -value;
            isNegative = true;
        }
        else {
            isNegative = false;
        }
        value *= 100000.0;
        int binValue = (int)Math.round(value);
        if (binValue == 0) {
            isNegative = false;
        }
        if (isNegative) {
            binValue ^= -1;
            ++binValue;
        }
        binValue <<= 1;
        if (isNegative) {
            binValue ^= -1;
        }
        for (int i = 0; i < 6 && !finished; ++i) {
            int charCode = binValue & 0x1F;
            binValue >>= 5;
            if (i < 5) {
                final int nextCharCode = binValue >> (i + 1) * 5 & 0x1F;
                if (binValue > 0) {
                    charCode |= 0x20;
                }
                else {
                    finished = true;
                }
            }
            charCode += 63;
            final char theChar = (char)charCode;
            conversion += theChar;
        }
        return conversion;
    }
    
    String convertTrackMarkers(final int trackNo, final int maxStringLength) {
        final String markerString = "";
        return markerString;
    }
    
    String compressAndConvertTrack(final int trackNo, final int maxStringLength) {
        String trackString = "";
        final TrackLog trackLog = TrackLog.getInstance();
        final int numberOfSegments = trackLog.getNumberOfTrackSegments(trackNo);
        int numberOfTrackPoints = 0;
        for (int segment = 0; segment < numberOfSegments; ++segment) {
            final ArrayList<TrackLogPoint> points = trackLog.getTrackPoints(trackNo, segment);
            numberOfTrackPoints += points.size();
        }
        this.setCompressionParameter(numberOfTrackPoints);
        boolean maxCompressionReached = false;
        boolean found = false;
        while (!found && !maxCompressionReached) {
            boolean bailOut = false;
            trackString = "";
            for (int segment = 0; segment < numberOfSegments && !bailOut; ++segment) {
                this.resetCompression();
                this.resetPointEncoding();
                String pathString;
                if (segment % 2 > 0) {
                    pathString = "path=color:blue|enc:";
                }
                else {
                    pathString = "path=color:red|enc:";
                }
                if (trackString.length() + pathString.length() < maxStringLength) {
                    trackString += pathString;
                }
                else {
                    bailOut = true;
                }
                final ArrayList<TrackLogPoint> points = trackLog.getTrackPoints(trackNo, segment);
                final Iterator<TrackLogPoint> iterator = points.iterator();
                while (iterator.hasNext() && !bailOut) {
                    final TrackLogPoint point = iterator.next();
                    final Coordinate compressedPoint = this.compress(point);
                    if (compressedPoint != null) {
                        final String pointString = this.encodePoint(compressedPoint);
                        if (trackString.length() + pointString.length() < maxStringLength) {
                            trackString += pointString;
                        }
                        else {
                            bailOut = true;
                        }
                    }
                }
                final Coordinate compressedPoint = this.finishCompression();
                if (compressedPoint != null) {
                    final String pointString = this.encodePoint(compressedPoint);
                    if (trackString.length() + pointString.length() < maxStringLength) {
                        trackString += pointString;
                    }
                    else {
                        bailOut = true;
                    }
                }
                if (segment < numberOfSegments - 1 && !bailOut) {
                    trackString += '&';
                }
            }
            if (!bailOut) {
                found = true;
            }
            else {
                maxCompressionReached = !this.setHigherCompression();
            }
        }
        if (!found) {
            DebugLogger.info(this.resultString = "Track truncated for printing. Too much points");
        }
        return trackString;
    }
    
    String compressAndConvertRoute(final int maxStringLength) {
        final RouteLog routeLog = RouteLog.getInstance();
        final int numberOfRoutePoints = routeLog.getNumberOfEntries();
        this.setCompressionParameter(numberOfRoutePoints);
        boolean bailOut = false;
        final ArrayList<RoutePoint> points = routeLog.getWaypoints();
        this.resetCompression();
        this.resetPointEncoding();
        String routeString = "path=color:0xff0000|weight:5|enc:";
        final Iterator<RoutePoint> iterator = points.iterator();
        while (iterator.hasNext() && !bailOut) {
            final RoutePoint point = iterator.next();
            final Coordinate compressedPoint = this.compress(point);
            if (compressedPoint != null) {
                final String pointString = this.encodePoint(compressedPoint);
                if (routeString.length() + pointString.length() < maxStringLength) {
                    routeString += pointString;
                }
                else {
                    bailOut = true;
                }
            }
        }
        final Coordinate compressedPoint = this.finishCompression();
        if (compressedPoint != null) {
            final String pointString = this.encodePoint(compressedPoint);
            if (routeString.length() + pointString.length() < maxStringLength) {
                routeString += pointString;
            }
            else {
                bailOut = true;
            }
        }
        if (!bailOut) {
            String markersString = "&markers=color:blue|label:S|";
            RoutePoint point = points.get(0);
            markersString = markersString + point.getLatitude() + "," + point.getLongitude();
            markersString += "&markers=color:green|label:F|";
            point = points.get(points.size() - 1);
            markersString = markersString + point.getLatitude() + "," + point.getLongitude();
            if (routeString.length() + markersString.length() < maxStringLength) {
                routeString += markersString;
            }
        }
        if (bailOut) {
            DebugLogger.info(this.resultString = "Route truncated for printing. Too much points");
        }
        return routeString;
    }
    
    private String convertWaypoints(final int maxStringLength) {
        final WaypointLog waypointLog = WaypointLog.getInstance();
        final ArrayList<Waypoint> points = waypointLog.getWaypoints();
        final Iterator<Waypoint> iterator = points.iterator();
        String markersString;
        if (iterator.hasNext()) {
            markersString = "markers=color:blue";
            boolean bailOut = false;
            while (iterator.hasNext() && !bailOut) {
                final Waypoint point = iterator.next();
                final String waypointString = "|" + point.getLatitude() + "," + point.getLongitude();
                if (markersString.length() + waypointString.length() < maxStringLength) {
                    markersString += waypointString;
                }
                else {
                    DebugLogger.info(this.resultString = "Waypoints truncated, to much waypoints to show on map");
                    bailOut = true;
                }
            }
        }
        else {
            markersString = null;
            DebugLogger.error(this.resultString = "There are no waypoints to show");
        }
        return markersString;
    }
    
    public String showTrack(final int trackNo) {
        this.resultString = "Track shown";
        String trackString = this.getMapString() + "&";
        trackString += this.compressAndConvertTrack(trackNo, 1700 - trackString.length());
        DebugLogger.debug("Google URL: " + trackString + " Length: " + trackString.length());
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(trackString));
            this.label.setIcon(new ImageIcon(image));
            this.pack();
        }
        catch (Exception e) {
            this.resultString = "Unable to get Google map";
            DebugLogger.error(this.resultString + ": " + e.getMessage());
            this.dispose();
        }
        return this.resultString;
    }
    
    public String showWaypoints() {
        this.resultString = "Waypoints shown";
        String mapString = this.getMapString() + "&";
        final String markersString = this.convertWaypoints(1700 - mapString.length());
        mapString += markersString;
        if (markersString != null) {
            DebugLogger.debug("Google URL: " + mapString + " Length: " + mapString.length());
            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL(mapString));
                this.label.setIcon(new ImageIcon(image));
                this.pack();
            }
            catch (Exception e) {
                this.resultString = "Unable to get Google map";
                DebugLogger.error(this.resultString + ": " + e.getMessage());
                this.dispose();
            }
        }
        return this.resultString;
    }
    
    String showRoute() {
        this.resultString = "Route shown";
        String trackString = this.getMapString() + "&";
        trackString += this.compressAndConvertRoute(1700 - trackString.length());
        DebugLogger.debug("Google URL: " + trackString + " Length: " + trackString.length());
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(trackString));
            this.label.setIcon(new ImageIcon(image));
            this.pack();
        }
        catch (Exception e) {
            this.resultString = "Unable to get Google map";
            DebugLogger.error(this.resultString + ": " + e.getMessage());
            this.dispose();
        }
        return this.resultString;
    }
    
    static {
        compressionParamPoints = new int[] { 0, 25, 500, 1000, 1500, 2000, 2500, 3000 };
        compressionParamMaxSlopeDev = new double[] { 0.0, 0.2, 0.3, 0.6, 0.7, 0.8, 0.85, 0.9 };
        MapFrame.mapType = "roadmap";
    }
}
