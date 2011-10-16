/*
 * Copyright 2011 StackFrame, LLC
 * All rights reserved.
 */
package com.stackframe.spot2kml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A servlet that reads SPOT data and converts it to KML.
 *
 * @author mcculley
 */
public class SPOT2KMLServlet extends HttpServlet {

    private final Map<String, CachedMessages> cache = new ConcurrentHashMap<String, CachedMessages>();
    private final ScheduledExecutorService backgroundService = Executors.newScheduledThreadPool(1);
    private final Runnable cacheRefresher = new Runnable() {

        public void run() {
            for (Map.Entry<String, CachedMessages> entry : cache.entrySet()) {
                String id = entry.getKey();
                CachedMessages cm = entry.getValue();
                long ageSinceLastUse = System.currentTimeMillis() - cm.getLastRetrieve();
                long expirationPeriod = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
                if (ageSinceLastUse > expirationPeriod) {
                    cache.remove(id);
                } else {
                    try {
                        refreshCache(id, cm);
                    } catch (Exception t) {
                        log("error when refreshing cache", t);
                    }
                }
            }
        }
    };

    @Override
    public void init() throws ServletException {
        super.init();
        backgroundService.scheduleWithFixedDelay(cacheRefresher, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void destroy() {
        backgroundService.shutdown();
        super.destroy();
    }

    private static Document makeKML() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            DOMImplementation di = db.getDOMImplementation();
            String publicID = null;
            String systemID = null;
            String namespace = "http://www.opengis.net/kml/2.2";
            DocumentType type = di.createDocumentType("kml", publicID, systemID);
            Document output = di.createDocument(namespace, "kml", type);
            output.setXmlStandalone(true);
            return output;
        } catch (ParserConfigurationException pce) {
            throw new AssertionError(pce);
        }
    }

    private static SPOTMessage parse(Element e) {
        String esn = e.getElementsByTagName("esn").item(0).getTextContent();
        String esnName = e.getElementsByTagName("esnName").item(0).getTextContent();
        String messageType = e.getElementsByTagName("messageType").item(0).getTextContent();
        long timeInGMTSecond = Long.parseLong(e.getElementsByTagName("timeInGMTSecond").item(0).getTextContent());
        double latitude = Double.parseDouble(e.getElementsByTagName("latitude").item(0).getTextContent());
        double longitude = Double.parseDouble(e.getElementsByTagName("longitude").item(0).getTextContent());
        return new SPOTMessage(esn, esnName, messageType, timeInGMTSecond, latitude, longitude);
    }

    private static Collection<SPOTMessage> getMessages(Document document) {
        Collection<SPOTMessage> messages = new ArrayList<SPOTMessage>();
        NodeList nodes = document.getElementsByTagName("message");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            messages.add(parse((Element) node));
        }

        return messages;
    }

    private static void serialize(Document document, Writer writer) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", new Integer(4));
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(writer);
            transformer.transform(new DOMSource(document), result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Element createPlacemark(SPOTMessage message, Document destinationDocument) {
        Element placemark = destinationDocument.createElement("Placemark");
        Element name = destinationDocument.createElement("name");
        placemark.appendChild(name);
        name.appendChild(destinationDocument.createTextNode(message.esnName));
        Element point = destinationDocument.createElement("Point");
        placemark.appendChild(point);
        Element coordinates = destinationDocument.createElement("coordinates");
        point.appendChild(coordinates);
        coordinates.appendChild(destinationDocument.createTextNode(String.format("%f,%f", message.longitude, message.latitude)));
        return placemark;
    }

    private void refreshCache(String id, CachedMessages cm) throws IOException, SAXException {
        long age = System.currentTimeMillis() - cm.getLastUpdate();
        if (age > SPOTUtils.refreshLimit) {
            Document document = SPOTUtils.getData(id);
            Collection<SPOTMessage> messages = getMessages(document);
            cm.addAll(messages);
        }
    }

    private synchronized SortedSet<SPOTMessage> getMessages(String id) throws IOException, SAXException {
        CachedMessages cm = cache.get(id);
        if (cm == null) {
            cm = new CachedMessages();
            cache.put(id, cm);
        }

        refreshCache(id, cm);
        return cm.getMessages();
    }

    private static Document makeKML(SortedSet<SPOTMessage> messages) {
        Document kml = makeKML();
        Element root = kml.getDocumentElement();
        Element documentElement = kml.createElement("Document");
        root.appendChild(documentElement);
        documentElement.appendChild(createPlacemark(messages.first(), kml));
        // FIXME: Add trail of older messages.
        // FIXME: Add configurable size of trail of older messages.
        // FIXME: Add configurable icon.
        // FIXME: Add a cue that shows the speed between messages.
        return kml;
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/vnd.google-earth.kml+xml");
        String SPOTID = request.getParameter("SPOTID");
        PrintWriter out = response.getWriter();
        try {
            SortedSet<SPOTMessage> messages = getMessages(SPOTID);
            Document kml = makeKML(messages);
            serialize(kml, out);
        } catch (SAXException se) {
            throw new IOException(se);
        } finally {
            out.close();
        }
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "A servlet that reads SPOT data and converts it to KML.";
    }
}
