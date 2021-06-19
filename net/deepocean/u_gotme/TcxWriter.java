// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;
import java.util.Iterator;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class TcxWriter implements IgotuWriter
{
    private static TcxWriter theInstance;
    private TrackLog trackLog;
    private WaypointLog waypointLog;
    private RouteLog routeLog;
    private int trackPoints;
    private int wayPoints;
    private String gpxVersion;
    Document doc;
    Element tcxElement;
    
    private TcxWriter() {
        this.trackLog = TrackLog.getInstance();
        this.waypointLog = WaypointLog.getInstance();
        this.routeLog = RouteLog.getInstance();
    }
    
    public static TcxWriter getInstance() {
        if (TcxWriter.theInstance == null) {
            TcxWriter.theInstance = new TcxWriter();
        }
        return TcxWriter.theInstance;
    }
    
    private void createGpxDocument(final Device.DeviceType deviceType) throws ParserConfigurationException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        this.doc = docBuilder.newDocument();
        this.tcxElement = this.doc.createElement("TrainingCenterDatabase");
        this.doc.appendChild(this.tcxElement);
        final String creator = new String("u-gotMe - ") + Device.getDeviceTypeDescription(deviceType);
        this.addTcxHeader(this.doc, this.tcxElement, creator);
    }
    
    void writeTcxDocument(final String fileName) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        final DOMSource source = new DOMSource(this.doc);
        final StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);
    }
    
    private void addTcxHeader(final Document doc, final Element tcxElement, final String creator) {
        Attr attr = doc.createAttribute("xmlns");
        attr.setValue("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2");
        tcxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xmlns:xsi");
        attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
        tcxElement.setAttributeNode(attr);
        attr = doc.createAttribute("xsi:schemaLocation");
        attr.setValue("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd");
        tcxElement.setAttributeNode(attr);
    }
    
    private void appendTrackTcx(final Document doc, final Element trackElement, final int trackNo, final int segmentNo) {
        final ArrayList<TrackLogPoint> points = this.trackLog.getTrackPoints(trackNo, segmentNo);
        for (final TrackLogPoint point : points) {
            final Element pointElement = doc.createElement("Trackpoint");
            trackElement.appendChild(pointElement);
            Element element = doc.createElement("Time");
            final DateTime dateTime = point.getDateTime();
            final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
            element.appendChild(doc.createTextNode(dateTimeString));
            pointElement.appendChild(element);
            final Element positionElement = doc.createElement("Position");
            pointElement.appendChild(positionElement);
            element = doc.createElement("LatitudeDegrees");
            element.appendChild(doc.createTextNode(String.valueOf(point.getLatitude())));
            positionElement.appendChild(element);
            element = doc.createElement("LongitudeDegrees");
            element.appendChild(doc.createTextNode(String.valueOf(point.getLongitude())));
            positionElement.appendChild(element);
            element = doc.createElement("AltitudeMeters");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            element = doc.createElement("DistanceMeters");
            element.appendChild(doc.createTextNode(String.valueOf(point.getElevation())));
            pointElement.appendChild(element);
            final int heartRate = point.getHeartRate();
            if (heartRate > 0) {
                final Element heartRateElement = doc.createElement("HeartRateBpm");
                pointElement.appendChild(heartRateElement);
                final Attr attr = doc.createAttribute("xsi:type");
                attr.setValue("HeartRateInBeatsPerMinute_t");
                heartRateElement.setAttributeNode(attr);
                element = doc.createElement("Value");
                element.appendChild(doc.createTextNode(Long.toString(heartRate)));
                heartRateElement.appendChild(element);
            }
            ++this.trackPoints;
        }
    }
    
    private void addTrack(final Document doc, final Element gpxElement, final int trackNo, final String trackName) {
        final Track track = this.trackLog.getTrack(trackNo);
        final int numberOfSegments = this.trackLog.getNumberOfTrackSegments(trackNo);
        final Element foldersElement = doc.createElement("Folders");
        gpxElement.appendChild(foldersElement);
        final Element activitiesElement = doc.createElement("Activities");
        gpxElement.appendChild(activitiesElement);
        final Element activityElement = doc.createElement("Activity");
        activitiesElement.appendChild(activityElement);
        Attr attr = doc.createAttribute("Sport");
        attr.setValue("Other");
        activityElement.setAttributeNode(attr);
        final DateTime dateTime = this.trackLog.getTrackStartTime(trackNo);
        final String dateTimeString = dateTime.format("YYYY-MM-DD") + "T" + dateTime.format("hh:mm:ss") + "Z";
        final Element idElement = doc.createElement("Id");
        idElement.appendChild(doc.createTextNode(dateTimeString));
        activityElement.appendChild(idElement);
        final Element lapElement = doc.createElement("Lap");
        attr = doc.createAttribute("StartTime");
        attr.setValue(dateTimeString);
        lapElement.setAttributeNode(attr);
        activityElement.appendChild(lapElement);
        Element element = doc.createElement("TotalTimeSeconds");
        element.appendChild(doc.createTextNode(Long.toString(track.getTotalTrackDuration())));
        lapElement.appendChild(element);
        element = doc.createElement("DistanceMeters");
        element.appendChild(doc.createTextNode(Double.toString(track.getTrackDistance())));
        lapElement.appendChild(element);
        element = doc.createElement("MaximumSpeed");
        element.appendChild(doc.createTextNode(Double.toString(track.getTrackMaxSpeed())));
        lapElement.appendChild(element);
        final long kCal = Math.round(track.getCalories());
        element = doc.createElement("Calories");
        element.appendChild(doc.createTextNode(Long.toString(kCal)));
        lapElement.appendChild(element);
        long heartRate = Math.round(track.getAverageHeartRate());
        if (heartRate > 0L) {
            final Element heartRateElement = doc.createElement("AverageHeartRateBpm");
            lapElement.appendChild(heartRateElement);
            attr = doc.createAttribute("xsi:type");
            attr.setValue("HeartRateInBeatsPerMinute_t");
            heartRateElement.setAttributeNode(attr);
            element = doc.createElement("Value");
            element.appendChild(doc.createTextNode(Long.toString(heartRate)));
            heartRateElement.appendChild(element);
        }
        heartRate = track.getMaxHeartRate();
        if (heartRate > 0L) {
            final Element heartRateElement = doc.createElement("MaximumHeartRateBpm");
            lapElement.appendChild(heartRateElement);
            attr = doc.createAttribute("xsi:type");
            attr.setValue("HeartRateInBeatsPerMinute_t");
            heartRateElement.setAttributeNode(attr);
            element = doc.createElement("Value");
            element.appendChild(doc.createTextNode(Long.toString(heartRate)));
            heartRateElement.appendChild(element);
        }
        element = doc.createElement("Intensity");
        element.appendChild(doc.createTextNode("Active"));
        lapElement.appendChild(element);
        element = doc.createElement("TriggerMethod");
        element.appendChild(doc.createTextNode("Manual"));
        lapElement.appendChild(element);
        final Element trackElement = doc.createElement("Track");
        lapElement.appendChild(trackElement);
        for (int i = 0; i < numberOfSegments; ++i) {
            this.appendTrackTcx(doc, trackElement, trackNo, i);
        }
    }
    
    public void writeTrackToFile(final String fileName, final int trackNo, final String trackName) {
        this.wayPoints = 0;
        this.trackPoints = 0;
        try {
            this.createGpxDocument(this.trackLog.getDeviceType());
            this.addTrack(this.doc, this.tcxElement, trackNo, trackName);
            this.writeTcxDocument(fileName);
            DebugLogger.info("TcxWriter says: 'File saved to " + fileName + "!'");
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
    }
    
    public void writeRouteToFile(final String fileName) {
    }
    
    public void writeHeartRateToFile(final String fileName, final int trackNo) {
    }
    
    static {
        TcxWriter.theInstance = null;
    }
}
