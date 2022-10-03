package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public class XMLDocumentFilterImpl implements XMLDocumentFilter
{
    private XMLDocumentHandler next;
    private XMLDocumentSource source;
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.next = handler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.next;
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
        this.source = source;
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.source;
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        this.next.characters(text, augs);
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        this.next.comment(text, augs);
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        this.next.doctypeDecl(rootElement, publicId, systemId, augs);
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.next.emptyElement(element, attributes, augs);
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        this.next.endCDATA(augs);
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        this.next.endDocument(augs);
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        this.next.endElement(element, augs);
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        this.next.endGeneralEntity(name, augs);
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.next.ignorableWhitespace(text, augs);
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        this.next.processingInstruction(target, data, augs);
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.next.startCDATA(augs);
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.next.startDocument(locator, encoding, namespaceContext, augs);
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.next.startElement(element, attributes, augs);
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        this.next.startGeneralEntity(name, identifier, encoding, augs);
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        this.next.textDecl(version, encoding, augs);
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        this.next.xmlDecl(version, encoding, standalone, augs);
    }
}
