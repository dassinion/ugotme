// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;
import java.util.Iterator;
import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Attr;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class GpxWriter implements IgotuWriter
{
    private static GpxWriter theInstance;
    private TrackLog trackLog;
    private WaypointLog waypointLog;
    private RouteLog routeLog;
    private int trackPoints;
    private int wayPoints;
    private String gpxVersion;
    Document doc;
    Element gpxElement;
    
    private GpxWriter() {
        this.trackLog = TrackLog.getInstance();
        this.waypointLog = WaypointLog.getInstance();
        this.routeLog = RouteLog.getInstance();
        this.gpxVersion = new String("1.1");
    }
    
    public static GpxWriter getInstance() {
        if (GpxWriter.theInstance == null) {
            GpxWriter.theInstance = new GpxWriter();
        }
        return GpxWriter.theInstance;
    }
    
    public void setGpxVersion(final String newVersion) {
        if (newVersion.equals("1.0") || newVersion.equals("1.1")) {
            this.gpxVersion = newVersion;
        }
        else {
            DebugLogger.error("Illegal GPX version " + newVersion + ". Version left to " + this.gpxVersion);
        }
    }
    
    private void createGpxDocument(final Device.DeviceType deviceType) throws ParserConfigurationException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        this.doc = docBuilder.newDocument();
        this.gpxElement = this.doc.createElement("gpx");
        this.doc.appendChild(this.gpxElement);
        final String creator = new String("u-gotMe - ") + Device.getDeviceTypeDescription(deviceType);
        if (this.gpxVersion.equals("1.0")) {
            this.addGpx1_0Header(this.doc, this.gpxElement, creator);
        }
        else if (this.gpxVersion.equals("1.1")) {
            this.addGpx1_1Header(this.doc, this.gpxElement, creator);
        }
    }
    
    private void addGpx1_0Header(final Document doc, final Element gpxElement, final String creator) {
        Attr attr = doc.createAttribute("creator");
        attr.setValue(creator);
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("version");
        attr.setValue("1.0");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xmlns");
        attr.setValue("http://www.topografix.com/GPX/1/0");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xmlns:xsi");
        attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xsi:schemaLocation");
        attr.setValue("http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd ");
        gpxElement.setAttributeNode(attr);
    }
    
    private void addGpx1_1Header(final Document doc, final Element gpxElement, final String creator) {
        Attr attr = doc.createAttribute("creator");
        attr.setValue(creator);
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("version");
        attr.setValue("1.1");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xmlns:xsi");
        attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xmlns");
        attr.setValue("http://www.topografix.com/GPX/1/1");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xmlns:u-gotMe");
        attr.setValue("http://u-gotme.deepocean.net");
        gpxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xsi:schemaLocation");
        attr.setValue("http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://u-gotme.deepocean.net http://www.deepocean.net/u-gotme/u-gotme.xsd");
        gpxElement.setAttributeNode(attr);
    }
    
    void writeGpxDocument(final String fileName) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        final DOMSource source = new DOMSource(this.doc);
        final StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);
    }
    
    private void appendTrackGpx1_0(final Document doc, final Element segmentElement, final int trackNo, final int segmentNo) {
        final ArrayList<TrackLogPoint> points = this.trackLog.getTrackPoints(trackNo, segmentNo);
        for (final TrackLogPoint point : points) {
            final Element pointElement = doc.createElement("trkpt");
            segmentElement.appendChild(pointElement);
            Element element = doc.createElement("ele");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = doc.createElement("time");
            final DateTime dateTime = point.getDateTime();
            final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
            element.appendChild(doc.createTextNode(dateTimeString));
            pointElement.appendChild(element);
            element = doc.createElement("course");
            element.appendChild(doc.createTextNode(String.valueOf(point.getCourse())));
            pointElement.appendChild(element);
            element = doc.createElement("speed");
            element.appendChild(doc.createTextNode(String.valueOf(point.getSpeed())));
            pointElement.appendChild(element);
            element = doc.createElement("sat");
            element.appendChild(doc.createTextNode(String.valueOf(point.getSatelliteNumber())));
            pointElement.appendChild(element);
            Attr attr = doc.createAttribute("lat");
            attr.setValue(String.valueOf(point.getLatitude()));
            pointElement.setAttributeNode(attr);
            attr = doc.createAttribute("lon");
            attr.setValue(String.valueOf(point.getLongitude()));
            pointElement.setAttributeNode(attr);
            ++this.trackPoints;
        }
    }
    
    private void appendTrackGpx1_1(final Document doc, final Element segmentElement, final int trackNo, final int segmentNo) {
        final ArrayList<TrackLogPoint> points = this.trackLog.getTrackPoints(trackNo, segmentNo);
        for (final TrackLogPoint point : points) {
            final Element pointElement = doc.createElement("trkpt");
            segmentElement.appendChild(pointElement);
            Element element = doc.createElement("ele");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = doc.createElement("time");
            final DateTime dateTime = point.getDateTime();
            final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
            element.appendChild(doc.createTextNode(dateTimeString));
            pointElement.appendChild(element);
            element = doc.createElement("sat");
            element.appendChild(doc.createTextNode(String.valueOf(point.getSatelliteNumber())));
            pointElement.appendChild(element);
            final Element extensionsElement = doc.createElement("extensions");
            pointElement.appendChild(extensionsElement);
            element = doc.createElement("u-gotMe:course");
            element.appendChild(doc.createTextNode(String.valueOf(point.getCourse())));
            extensionsElement.appendChild(element);
            element = doc.createElement("u-gotMe:speed");
            element.appendChild(doc.createTextNode(String.valueOf(point.getSpeed())));
            extensionsElement.appendChild(element);
            element = doc.createElement("u-gotMe:ehpe");
            element.appendChild(doc.createTextNode(String.valueOf(point.getEhpe())));
            extensionsElement.appendChild(element);
            if (point.getDeviceType() == Device.DeviceType.DEVICETYPE_GT800PRO || point.getDeviceType() == Device.DeviceType.DEVICETYPE_GT820PRO || point.getDeviceType() == Device.DeviceType.DEVICETYPE_GT900PRO) {
                element = doc.createElement("u-gotMe:elegps");
                element.appendChild(doc.createTextNode(String.valueOf(point.getElevationByGps())));
                extensionsElement.appendChild(element);
            }
            element = doc.createElement("u-gotMe:heartrate");
            element.appendChild(doc.createTextNode(String.valueOf(point.getHeartRate())));
            extensionsElement.appendChild(element);
            Attr attr = doc.createAttribute("lat");
            attr.setValue(String.valueOf(point.getLatitude()));
            pointElement.setAttributeNode(attr);
            attr = doc.createAttribute("lon");
            attr.setValue(String.valueOf(point.getLongitude()));
            pointElement.setAttributeNode(attr);
            ++this.trackPoints;
        }
    }
    
    private void appendWaypointsGpx1_0(final Document doc, final Element trackElement, final int trackNo, final int segmentNo) {
        final ArrayList<TrackLogPoint> points = this.trackLog.getWayPoints(trackNo, segmentNo);
        for (final TrackLogPoint point : points) {
            final Element pointElement = doc.createElement("wpt");
            trackElement.appendChild(pointElement);
            Element element = doc.createElement("ele");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = doc.createElement("time");
            final DateTime dateTime = point.getDateTime();
            final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
            element.appendChild(doc.createTextNode(dateTimeString));
            pointElement.appendChild(element);
            element = doc.createElement("name");
            element.appendChild(doc.createTextNode(String.valueOf(this.wayPoints)));
            pointElement.appendChild(element);
            element = doc.createElement("sym");
            element.appendChild(doc.createTextNode("Waypoint"));
            pointElement.appendChild(element);
            element = doc.createElement("sat");
            element.appendChild(doc.createTextNode(String.valueOf(point.getSatelliteNumber())));
            pointElement.appendChild(element);
            Attr attr = doc.createAttribute("lat");
            attr.setValue(String.valueOf(point.getLatitude()));
            pointElement.setAttributeNode(attr);
            attr = doc.createAttribute("lon");
            attr.setValue(String.valueOf(point.getLongitude()));
            pointElement.setAttributeNode(attr);
            ++this.wayPoints;
        }
    }
    
    private void appendWaypointsGpx1_1(final Document doc, final Element trackElement, final int trackNo, final int segmentNo) {
        final ArrayList<TrackLogPoint> points = this.trackLog.getWayPoints(trackNo, segmentNo);
        for (final TrackLogPoint point : points) {
            final Element pointElement = doc.createElement("wpt");
            trackElement.appendChild(pointElement);
            Element element = doc.createElement("ele");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = doc.createElement("time");
            final DateTime dateTime = point.getDateTime();
            final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
            element.appendChild(doc.createTextNode(dateTimeString));
            pointElement.appendChild(element);
            element = doc.createElement("name");
            element.appendChild(doc.createTextNode(String.valueOf(this.wayPoints)));
            pointElement.appendChild(element);
            element = doc.createElement("sym");
            element.appendChild(doc.createTextNode("Waypoint"));
            pointElement.appendChild(element);
            element = doc.createElement("sat");
            element.appendChild(doc.createTextNode(String.valueOf(point.getSatelliteNumber())));
            pointElement.appendChild(element);
            final Element extensionsElement = doc.createElement("extensions");
            pointElement.appendChild(extensionsElement);
            element = doc.createElement("u-gotMe:ehpe");
            element.appendChild(doc.createTextNode(String.valueOf(point.getEhpe())));
            extensionsElement.appendChild(element);
            Attr attr = doc.createAttribute("lat");
            attr.setValue(String.valueOf(point.getLatitude()));
            pointElement.setAttributeNode(attr);
            attr = doc.createAttribute("lon");
            attr.setValue(String.valueOf(point.getLongitude()));
            pointElement.setAttributeNode(attr);
            ++this.wayPoints;
        }
    }
    
    private void addTrack(final Document doc, final Element gpxElement, final int trackNo, final String trackName) {
        final int numberOfSegments = this.trackLog.getNumberOfTrackSegments(trackNo);
        for (int i = 0; i < numberOfSegments; ++i) {
            if (this.gpxVersion.equals("1.0")) {
                this.appendWaypointsGpx1_0(doc, gpxElement, trackNo, i);
            }
            else if (this.gpxVersion.equals("1.1")) {
                this.appendWaypointsGpx1_1(doc, gpxElement, trackNo, i);
            }
        }
        final Element trackElement = doc.createElement("trk");
        gpxElement.appendChild(trackElement);
        Element element = doc.createElement("name");
        element.appendChild(doc.createTextNode(trackName));
        trackElement.appendChild(element);
        final String description = "i-gotu " + Device.getDeviceTypeDescription(this.trackLog.getDeviceType()) + " logged track";
        element = doc.createElement("desc");
        element.appendChild(doc.createTextNode(description));
        trackElement.appendChild(element);
        for (int i = 0; i < numberOfSegments; ++i) {
            final Element segmentElement = doc.createElement("trkseg");
            trackElement.appendChild(segmentElement);
            if (this.gpxVersion.equals("1.0")) {
                this.appendTrackGpx1_0(doc, segmentElement, trackNo, i);
            }
            else if (this.gpxVersion.equals("1.1")) {
                this.appendTrackGpx1_1(doc, segmentElement, trackNo, i);
            }
        }
    }
    
    private void addWaypointsFromLog(final Document doc, final Element gpxElement) {
        final ArrayList<Waypoint> points = this.waypointLog.getWaypoints();
        for (final Waypoint point : points) {
            final Element pointElement = doc.createElement("wpt");
            gpxElement.appendChild(pointElement);
            Element element = doc.createElement("ele");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = doc.createElement("time");
            final DateTime dateTime = point.getDateTime();
            final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
            element.appendChild(doc.createTextNode(dateTimeString));
            pointElement.appendChild(element);
            element = doc.createElement("name");
            element.appendChild(doc.createTextNode(String.valueOf(point.getId())));
            pointElement.appendChild(element);
            element = doc.createElement("sym");
            element.appendChild(doc.createTextNode("Waypoint"));
            pointElement.appendChild(element);
            Attr attr = doc.createAttribute("lat");
            attr.setValue(String.valueOf(point.getLatitude()));
            pointElement.setAttributeNode(attr);
            attr = doc.createAttribute("lon");
            attr.setValue(String.valueOf(point.getLongitude()));
            pointElement.setAttributeNode(attr);
            ++this.wayPoints;
        }
    }
    
    private void addRoutePointsFromLog(final Element routeElement) {
        final ArrayList<RoutePoint> points = this.routeLog.getWaypoints();
        for (final RoutePoint point : points) {
            final Element pointElement = this.doc.createElement("rtept");
            routeElement.appendChild(pointElement);
            Attr attr = this.doc.createAttribute("lat");
            attr.setValue(String.valueOf(point.getLatitude()));
            pointElement.setAttributeNode(attr);
            attr = this.doc.createAttribute("lon");
            attr.setValue(String.valueOf(point.getLongitude()));
            pointElement.setAttributeNode(attr);
            Element element = this.doc.createElement("ele");
            element.appendChild(this.doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = this.doc.createElement("name");
            element.appendChild(this.doc.createTextNode(String.valueOf(point.getName())));
            pointElement.appendChild(element);
            element = this.doc.createElement("sym");
            element.appendChild(this.doc.createTextNode(point.getSymbolName()));
            pointElement.appendChild(element);
            ++this.wayPoints;
        }
    }
    
    public void writeTrackToFile(final String fileName, final int trackNo, final String trackName) {
        this.wayPoints = 0;
        this.trackPoints = 0;
        try {
            this.createGpxDocument(this.trackLog.getDeviceType());
            this.addTrack(this.doc, this.gpxElement, trackNo, trackName);
            this.writeGpxDocument(fileName);
            DebugLogger.info("GpxWriter says: 'File saved to " + fileName + "!'");
            DebugLogger.info("Track: " + trackName + ", track points: " + this.trackPoints + ", wayPoints: " + this.wayPoints);
        }
        catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    public void writeWaypointsToFile(final String fileName) {
        this.wayPoints = 0;
        try {
            this.createGpxDocument(this.waypointLog.getDeviceType());
            this.addWaypointsFromLog(this.doc, this.gpxElement);
            this.writeGpxDocument(fileName);
            DebugLogger.info("GpxWriter says: 'File saved to " + fileName + "!'");
            DebugLogger.info("Added waypoints: " + this.wayPoints);
        }
        catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    public void writeRouteToFile(final String fileName) {
        this.wayPoints = 0;
        try {
            this.createGpxDocument(this.routeLog.getDeviceType());
            final Element routeElement = this.doc.createElement("rte");
            this.gpxElement.appendChild(routeElement);
            Element element = this.doc.createElement("name");
            element.appendChild(this.doc.createTextNode("route"));
            routeElement.appendChild(element);
            element = this.doc.createElement("src");
            element.appendChild(this.doc.createTextNode("u-gotMe"));
            routeElement.appendChild(element);
            this.addRoutePointsFromLog(routeElement);
            this.writeGpxDocument(fileName);
            DebugLogger.info("GpxWriter says: 'File saved to " + fileName + "!'");
            DebugLogger.info("Added route points: " + this.wayPoints);
        }
        catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    public void writeHeartRateToFile(final String fileName, final int trackNo) {
    }
    
    static {
        GpxWriter.theInstance = null;
    }
}
