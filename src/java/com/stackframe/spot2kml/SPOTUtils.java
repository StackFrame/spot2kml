/*
 * Copyright 2011 StackFrame, LLC
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with this file.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stackframe.spot2kml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Various utility constants and functions for dealing with SPOT data.
 *
 * @author mcculley
 */
class SPOTUtils {

    static final long refreshLimit = TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES); // Minimum time in minutes between updates.

    private SPOTUtils() {
        // inhibit construction
    }

    private static URL makeURL(String id) {
        try {
            String base = "http://share.findmespot.com/messageService/guestlinkservlet";
            String full = String.format("%s?glId=%s&completeXml=true", base, id);
            return new URL(full);
        } catch (MalformedURLException mue) {
            // This shouldn't happen as we are in control of what the URL looks like.
            throw new AssertionError(mue);
        }
    }

    private static Document getData(String id) throws IOException, SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(makeURL(id).toExternalForm());
        } catch (ParserConfigurationException pce) {
            throw new AssertionError(pce);
        }
    }

    /**
     * Given an XML element containing a SPOT message, parse it into a SPOTMessage object.
     *
     * @param e a message Element
     * @return a SPOTMessage parsed from the element
     */
    private static SPOTMessage parse(Element e) {
        String esn = e.getElementsByTagName("esn").item(0).getTextContent();
        String esnName = e.getElementsByTagName("esnName").item(0).getTextContent();
        String messageType = e.getElementsByTagName("messageType").item(0).getTextContent();
        long timeInGMTSecond = Long.parseLong(e.getElementsByTagName("timeInGMTSecond").item(0).getTextContent());
        double latitude = Double.parseDouble(e.getElementsByTagName("latitude").item(0).getTextContent());
        double longitude = Double.parseDouble(e.getElementsByTagName("longitude").item(0).getTextContent());
        return new SPOTMessage(esn, esnName, messageType, timeInGMTSecond, latitude, longitude);
    }

    /**
     * Given an XML document containing SPOT messages, parse out a collection of SPOTMessage objects.
     *
     * @param document an XML Document containing SPOT messages
     * @return a Collection of SPOTMessage objects
     */
    private static Collection<SPOTMessage> getMessages(Document document) {
        Collection<SPOTMessage> messages = new ArrayList<SPOTMessage>();
        NodeList nodes = document.getElementsByTagName("message");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            messages.add(SPOTUtils.parse((Element) node));
        }

        return messages;
    }

    /**
     * Given a SPOT ID, parse out a collection of SPOTMessage objects.
     *
     * @param id the SPOT ID
     * @return a Collection of SPOTMessage objects
     */
    static Collection<SPOTMessage> getMessages(String id) throws IOException, SAXException {
        return getMessages(getData(id));
    }
}
