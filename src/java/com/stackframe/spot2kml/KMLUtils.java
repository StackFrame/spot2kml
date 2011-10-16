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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * Utilities for dealing with KML.
 *
 * @author mcculley
 */
class KMLUtils {

    private KMLUtils() {
        // inhibit construction
    }

    static Document makeKML() {
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
}
