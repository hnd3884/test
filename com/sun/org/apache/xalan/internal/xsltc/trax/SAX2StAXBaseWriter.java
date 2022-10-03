package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import org.xml.sax.Locator;
import java.util.Vector;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAX2StAXBaseWriter extends DefaultHandler implements LexicalHandler
{
    protected boolean isCDATA;
    protected StringBuffer CDATABuffer;
    protected Vector namespaces;
    protected Locator docLocator;
    protected XMLReporter reporter;
    
    public SAX2StAXBaseWriter() {
    }
    
    public SAX2StAXBaseWriter(final XMLReporter reporter) {
        this.reporter = reporter;
    }
    
    public void setXMLReporter(final XMLReporter reporter) {
        this.reporter = reporter;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.docLocator = locator;
    }
    
    public Location getCurrentLocation() {
        if (this.docLocator != null) {
            return new SAXLocation(this.docLocator);
        }
        return null;
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        this.reportException("ERROR", e);
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        this.reportException("FATAL", e);
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        this.reportException("WARNING", e);
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.namespaces = new Vector(2);
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.namespaces = null;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.namespaces = null;
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.namespaces = null;
    }
    
    @Override
    public void startPrefixMapping(String prefix, final String uri) throws SAXException {
        if (prefix == null) {
            prefix = "";
        }
        else if (prefix.equals("xml")) {
            return;
        }
        if (this.namespaces == null) {
            this.namespaces = new Vector(2);
        }
        this.namespaces.addElement(prefix);
        this.namespaces.addElement(uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.isCDATA = true;
        if (this.CDATABuffer == null) {
            this.CDATABuffer = new StringBuffer();
        }
        else {
            this.CDATABuffer.setLength(0);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.isCDATA) {
            this.CDATABuffer.append(ch, start, length);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.isCDATA = false;
        this.CDATABuffer.setLength(0);
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    protected void reportException(final String type, final SAXException e) throws SAXException {
        if (this.reporter != null) {
            try {
                this.reporter.report(e.getMessage(), type, e, this.getCurrentLocation());
            }
            catch (final XMLStreamException e2) {
                throw new SAXException(e2);
            }
        }
    }
    
    public static final void parseQName(final String qName, final String[] results) {
        final int idx = qName.indexOf(58);
        String prefix;
        String local;
        if (idx >= 0) {
            prefix = qName.substring(0, idx);
            local = qName.substring(idx + 1);
        }
        else {
            prefix = "";
            local = qName;
        }
        results[0] = prefix;
        results[1] = local;
    }
    
    private static final class SAXLocation implements Location
    {
        private int lineNumber;
        private int columnNumber;
        private String publicId;
        private String systemId;
        
        private SAXLocation(final Locator locator) {
            this.lineNumber = locator.getLineNumber();
            this.columnNumber = locator.getColumnNumber();
            this.publicId = locator.getPublicId();
            this.systemId = locator.getSystemId();
        }
        
        @Override
        public int getLineNumber() {
            return this.lineNumber;
        }
        
        @Override
        public int getColumnNumber() {
            return this.columnNumber;
        }
        
        @Override
        public int getCharacterOffset() {
            return -1;
        }
        
        @Override
        public String getPublicId() {
            return this.publicId;
        }
        
        @Override
        public String getSystemId() {
            return this.systemId;
        }
    }
}
