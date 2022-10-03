package org.cyberneko.html.parsers;

import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.xni.Augmentations;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

public class DOMParser extends org.apache.xerces.parsers.DOMParser
{
    public DOMParser() {
        super((XMLParserConfiguration)new HTMLConfiguration());
        try {
            this.setProperty("http://apache.org/xml/properties/dom/document-class-name", (Object)"org.apache.html.dom.HTMLDocumentImpl");
        }
        catch (final SAXNotRecognizedException e) {
            throw new RuntimeException("http://apache.org/xml/properties/dom/document-class-name property not recognized");
        }
        catch (final SAXNotSupportedException e2) {
            throw new RuntimeException("http://apache.org/xml/properties/dom/document-class-name property not supported");
        }
    }
    
    public void doctypeDecl(final String root, final String pubid, final String sysid, final Augmentations augs) throws XNIException {
        final String VERSION = XercesBridge.getInstance().getVersion();
        boolean okay = true;
        if (VERSION.startsWith("Xerces-J 2.")) {
            okay = (getParserSubVersion() > 5);
        }
        else if (VERSION.startsWith("XML4J")) {
            okay = false;
        }
        if (okay) {
            super.doctypeDecl(root, pubid, sysid, augs);
        }
    }
    
    private static int getParserSubVersion() {
        try {
            final String VERSION = XercesBridge.getInstance().getVersion();
            final int index1 = VERSION.indexOf(46) + 1;
            int index2 = VERSION.indexOf(46, index1);
            if (index2 == -1) {
                index2 = VERSION.length();
            }
            return Integer.parseInt(VERSION.substring(index1, index2));
        }
        catch (final Exception e) {
            return -1;
        }
    }
}
