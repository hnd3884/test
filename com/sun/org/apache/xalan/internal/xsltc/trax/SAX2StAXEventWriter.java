package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import org.xml.sax.Attributes;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.ext.Locator2;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;

public class SAX2StAXEventWriter extends SAX2StAXBaseWriter
{
    private XMLEventWriter writer;
    private XMLEventFactory eventFactory;
    private List namespaceStack;
    private boolean needToCallStartDocument;
    
    public SAX2StAXEventWriter() {
        this.namespaceStack = new ArrayList();
        this.needToCallStartDocument = false;
        this.eventFactory = XMLEventFactory.newInstance();
    }
    
    public SAX2StAXEventWriter(final XMLEventWriter writer) {
        this.namespaceStack = new ArrayList();
        this.needToCallStartDocument = false;
        this.writer = writer;
        this.eventFactory = XMLEventFactory.newInstance();
    }
    
    public SAX2StAXEventWriter(final XMLEventWriter writer, final XMLEventFactory factory) {
        this.namespaceStack = new ArrayList();
        this.needToCallStartDocument = false;
        this.writer = writer;
        if (factory != null) {
            this.eventFactory = factory;
        }
        else {
            this.eventFactory = XMLEventFactory.newInstance();
        }
    }
    
    public XMLEventWriter getEventWriter() {
        return this.writer;
    }
    
    public void setEventWriter(final XMLEventWriter writer) {
        this.writer = writer;
    }
    
    public XMLEventFactory getEventFactory() {
        return this.eventFactory;
    }
    
    public void setEventFactory(final XMLEventFactory factory) {
        this.eventFactory = factory;
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.namespaceStack.clear();
        this.eventFactory.setLocation(this.getCurrentLocation());
        this.needToCallStartDocument = true;
    }
    
    private void writeStartDocument() throws SAXException {
        try {
            if (this.docLocator == null) {
                this.writer.add(this.eventFactory.createStartDocument());
            }
            else {
                try {
                    this.writer.add(this.eventFactory.createStartDocument(((Locator2)this.docLocator).getEncoding(), ((Locator2)this.docLocator).getXMLVersion()));
                }
                catch (final ClassCastException e) {
                    this.writer.add(this.eventFactory.createStartDocument());
                }
            }
        }
        catch (final XMLStreamException e2) {
            throw new SAXException(e2);
        }
        this.needToCallStartDocument = false;
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.writer.add(this.eventFactory.createEndDocument());
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endDocument();
        this.namespaceStack.clear();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (this.needToCallStartDocument) {
            this.writeStartDocument();
        }
        this.eventFactory.setLocation(this.getCurrentLocation());
        final Collection[] events = { null, null };
        this.createStartEvents(attributes, events);
        this.namespaceStack.add(events[0]);
        try {
            final String[] qname = { null, null };
            SAX2StAXBaseWriter.parseQName(qName, qname);
            this.writer.add(this.eventFactory.createStartElement(qname[0], uri, qname[1], events[1].iterator(), events[0].iterator()));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
        finally {
            super.startElement(uri, localName, qName, attributes);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        this.eventFactory.setLocation(this.getCurrentLocation());
        final String[] qname = { null, null };
        SAX2StAXBaseWriter.parseQName(qName, qname);
        final Collection nsList = this.namespaceStack.remove(this.namespaceStack.size() - 1);
        final Iterator nsIter = nsList.iterator();
        try {
            this.writer.add(this.eventFactory.createEndElement(qname[0], uri, qname[1], nsIter));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.needToCallStartDocument) {
            this.writeStartDocument();
        }
        super.comment(ch, start, length);
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.writer.add(this.eventFactory.createComment(new String(ch, start, length)));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        try {
            if (!this.isCDATA) {
                this.eventFactory.setLocation(this.getCurrentLocation());
                this.writer.add(this.eventFactory.createCharacters(new String(ch, start, length)));
            }
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        this.characters(ch, start, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.needToCallStartDocument) {
            this.writeStartDocument();
        }
        super.processingInstruction(target, data);
        try {
            this.writer.add(this.eventFactory.createProcessingInstruction(target, data));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.eventFactory.setLocation(this.getCurrentLocation());
        try {
            this.writer.add(this.eventFactory.createCData(this.CDATABuffer.toString()));
        }
        catch (final XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endCDATA();
    }
    
    protected void createStartEvents(final Attributes attributes, final Collection[] events) {
        Map nsMap = null;
        List attrs = null;
        if (this.namespaces != null) {
            for (int nDecls = this.namespaces.size(), i = 0; i < nDecls; ++i) {
                final String prefix = this.namespaces.elementAt(i++);
                final String uri = this.namespaces.elementAt(i);
                final Namespace ns = this.createNamespace(prefix, uri);
                if (nsMap == null) {
                    nsMap = new HashMap();
                }
                nsMap.put(prefix, ns);
            }
        }
        final String[] qname = { null, null };
        for (int i = 0, s = attributes.getLength(); i < s; ++i) {
            SAX2StAXBaseWriter.parseQName(attributes.getQName(i), qname);
            final String attrPrefix = qname[0];
            final String attrLocal = qname[1];
            final String attrQName = attributes.getQName(i);
            final String attrValue = attributes.getValue(i);
            final String attrURI = attributes.getURI(i);
            if ("xmlns".equals(attrQName) || "xmlns".equals(attrPrefix)) {
                if (nsMap == null) {
                    nsMap = new HashMap();
                }
                if (!nsMap.containsKey(attrLocal)) {
                    final Namespace ns2 = this.createNamespace(attrLocal, attrValue);
                    nsMap.put(attrLocal, ns2);
                }
            }
            else {
                Attribute attribute;
                if (attrPrefix.length() > 0) {
                    attribute = this.eventFactory.createAttribute(attrPrefix, attrURI, attrLocal, attrValue);
                }
                else {
                    attribute = this.eventFactory.createAttribute(attrLocal, attrValue);
                }
                if (attrs == null) {
                    attrs = new ArrayList();
                }
                attrs.add(attribute);
            }
        }
        events[0] = ((nsMap == null) ? Collections.EMPTY_LIST : nsMap.values());
        events[1] = ((attrs == null) ? Collections.EMPTY_LIST : attrs);
    }
    
    protected Namespace createNamespace(final String prefix, final String uri) {
        if (prefix == null || prefix.length() == 0) {
            return this.eventFactory.createNamespace(uri);
        }
        return this.eventFactory.createNamespace(prefix, uri);
    }
}
