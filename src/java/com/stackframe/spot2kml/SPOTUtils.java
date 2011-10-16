/*
 * Copyright 2011 StackFrame, LLC
 * All rights reserved.
 */
package com.stackframe.spot2kml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
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

     static Document getData(String id) throws IOException, SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(makeURL(id).toExternalForm());
        } catch (ParserConfigurationException pce) {
            throw new AssertionError(pce);
        }
    }

}
