// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class GpxReader implements IgotuReader
{
    private static GpxReader theInstance;
    
    private GpxReader() {
    }
    
    public static GpxReader getInstance() {
        if (GpxReader.theInstance == null) {
            GpxReader.theInstance = new GpxReader();
        }
        return GpxReader.theInstance;
    }
    
    private Element getChildElement(final Element parent, final String elementName) {
        Element element = null;
        final NodeList nodeList = parent.getElementsByTagName(elementName);
        if (nodeList.getLength() == 1) {
            if (nodeList.item(0).getNodeType() == 1) {
                element = (Element)nodeList.item(0);
            }
        }
        else {
            DebugLogger.error("Number of nodes <" + elementName + "> tags in GPX file not equal to one as expected...");
        }
        return element;
    }
    
    private String getChildElementValue(final Element parent, final String tagName) {
        final Element element = this.getChildElement(parent, tagName);
        String value;
        if (element != null) {
            final NodeList list = element.getChildNodes();
            final Node node = list.item(0);
            if (node != null) {
                value = node.getNodeValue();
            }
            else {
                value = "";
            }
        }
        else {
            value = "";
            DebugLogger.error("Tag <" + tagName + "> not found in GPX file");
        }
        return value;
    }
    
    public void readRouteFromFile(final String fileName) {
        final RouteLog routeLog = RouteLog.getInstance();
        routeLog.clear();
        try {
            final File fXmlFile = new File(fileName);
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            final Element gpxElement = doc.getDocumentElement();
            if (gpxElement.getNodeName().equals("gpx")) {
                final Element routeElement = this.getChildElement(gpxElement, "rte");
                NodeList nList;
                if (routeElement != null) {
                    nList = routeElement.getElementsByTagName("rtept");
                }
                else {
                    nList = gpxElement.getElementsByTagName("wpt");
                }
                if (nList != null && nList.getLength() > 0) {
                    for (int i = 0; i < nList.getLength(); ++i) {
                        final Node nNode = nList.item(i);
                        if (nNode.getNodeType() == 1) {
                            final Element eElement = (Element)nNode;
                            final double longitude = Double.parseDouble(eElement.getAttribute("lon"));
                            final double latitude = Double.parseDouble(eElement.getAttribute("lat"));
                            final String name = this.getChildElementValue(eElement, "name");
                            final String symbol = this.getChildElementValue(eElement, "sym");
                            final String value = this.getChildElementValue(eElement, "ele");
                            if (!value.equals("")) {
                                final double elevation = Double.parseDouble(value);
                            }
                            else {
                                final double elevation = 0.0;
                            }
                            final double elevation = 0.0;
                            final RoutePoint waypoint = new RoutePoint((int)(longitude * 1.0E7), (int)(latitude * 1.0E7), (int)(elevation * 100.0), name, symbol);
                            routeLog.appendWaypoint(waypoint);
                        }
                    }
                }
                else {
                    DebugLogger.error("No <rte> or <wpt> data in GPX file");
                }
                routeLog.dumpLog();
            }
            else {
                DebugLogger.error(fileName + " does not seem to be a GPX file");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        GpxReader.theInstance = null;
    }
}
