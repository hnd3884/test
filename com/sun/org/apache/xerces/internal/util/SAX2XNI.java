package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.jaxp.validation.WrappedSAXException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.xml.sax.Locator;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import org.xml.sax.ContentHandler;

public class SAX2XNI implements ContentHandler, XMLDocumentSource
{
    private XMLDocumentHandler fCore;
    private final NamespaceSupport nsContext;
    private final SymbolTable symbolTable;
    private Locator locator;
    private final XMLAttributes xa;
    
    public SAX2XNI(final XMLDocumentHandler core) {
        this.nsContext = new NamespaceSupport();
        this.symbolTable = new SymbolTable();
        this.xa = new XMLAttributesImpl();
        this.fCore = core;
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fCore = handler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fCore;
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            this.nsContext.reset();
            XMLLocator xmlLocator;
            if (this.locator == null) {
                xmlLocator = new SimpleLocator(null, null, -1, -1);
            }
            else {
                xmlLocator = new LocatorWrapper(this.locator);
            }
            this.fCore.startDocument(xmlLocator, null, this.nsContext, null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this.fCore.endDocument(null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void startElement(final String uri, final String local, final String qname, final Attributes att) throws SAXException {
        try {
            this.fCore.startElement(this.createQName(uri, local, qname), this.createAttributes(att), null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void endElement(final String uri, final String local, final String qname) throws SAXException {
        try {
            this.fCore.endElement(this.createQName(uri, local, qname), null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        try {
            this.fCore.characters(new XMLString(buf, offset, len), null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] buf, final int offset, final int len) throws SAXException {
        try {
            this.fCore.ignorableWhitespace(new XMLString(buf, offset, len), null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        this.nsContext.pushContext();
        this.nsContext.declarePrefix(prefix, uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) {
        this.nsContext.popContext();
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.fCore.processingInstruction(this.symbolize(target), this.createXMLString(data), null);
        }
        catch (final WrappedSAXException e) {
            throw e.exception;
        }
    }
    
    @Override
    public void skippedEntity(final String name) {
    }
    
    @Override
    public void setDocumentLocator(final Locator _loc) {
        this.locator = _loc;
    }
    
    private QName createQName(String uri, String local, final String raw) {
        final int idx = raw.indexOf(58);
        if (local.length() == 0) {
            uri = "";
            if (idx < 0) {
                local = raw;
            }
            else {
                local = raw.substring(idx + 1);
            }
        }
        String prefix;
        if (idx < 0) {
            prefix = null;
        }
        else {
            prefix = raw.substring(0, idx);
        }
        if (uri != null && uri.length() == 0) {
            uri = null;
        }
        return new QName(this.symbolize(prefix), this.symbolize(local), this.symbolize(raw), this.symbolize(uri));
    }
    
    private String symbolize(final String s) {
        if (s == null) {
            return null;
        }
        return this.symbolTable.addSymbol(s);
    }
    
    private XMLString createXMLString(final String str) {
        return new XMLString(str.toCharArray(), 0, str.length());
    }
    
    private XMLAttributes createAttributes(final Attributes att) {
        this.xa.removeAllAttributes();
        for (int len = att.getLength(), i = 0; i < len; ++i) {
            this.xa.addAttribute(this.createQName(att.getURI(i), att.getLocalName(i), att.getQName(i)), att.getType(i), att.getValue(i));
        }
        return this.xa;
    }
}
