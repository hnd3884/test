package org.apache.xerces.jaxp;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDocumentFilter;

class TeeXMLDocumentFilterImpl implements XMLDocumentFilter
{
    private XMLDocumentHandler next;
    private XMLDocumentHandler side;
    private XMLDocumentSource source;
    
    public XMLDocumentHandler getSide() {
        return this.side;
    }
    
    public void setSide(final XMLDocumentHandler side) {
        this.side = side;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.source;
    }
    
    public void setDocumentSource(final XMLDocumentSource source) {
        this.source = source;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.next;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler next) {
        this.next = next;
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.side.characters(xmlString, augmentations);
        this.next.characters(xmlString, augmentations);
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.side.comment(xmlString, augmentations);
        this.next.comment(xmlString, augmentations);
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        this.side.doctypeDecl(s, s2, s3, augmentations);
        this.next.doctypeDecl(s, s2, s3, augmentations);
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.side.emptyElement(qName, xmlAttributes, augmentations);
        this.next.emptyElement(qName, xmlAttributes, augmentations);
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        this.side.endCDATA(augmentations);
        this.next.endCDATA(augmentations);
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        this.side.endDocument(augmentations);
        this.next.endDocument(augmentations);
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        this.side.endElement(qName, augmentations);
        this.next.endElement(qName, augmentations);
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        this.side.endGeneralEntity(s, augmentations);
        this.next.endGeneralEntity(s, augmentations);
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.side.ignorableWhitespace(xmlString, augmentations);
        this.next.ignorableWhitespace(xmlString, augmentations);
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.side.processingInstruction(s, xmlString, augmentations);
        this.next.processingInstruction(s, xmlString, augmentations);
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        this.side.startCDATA(augmentations);
        this.next.startCDATA(augmentations);
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
        this.side.startDocument(xmlLocator, s, namespaceContext, augmentations);
        this.next.startDocument(xmlLocator, s, namespaceContext, augmentations);
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.side.startElement(qName, xmlAttributes, augmentations);
        this.next.startElement(qName, xmlAttributes, augmentations);
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        this.side.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
        this.next.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        this.side.textDecl(s, s2, augmentations);
        this.next.textDecl(s, s2, augmentations);
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        this.side.xmlDecl(s, s2, s3, augmentations);
        this.next.xmlDecl(s, s2, s3, augmentations);
    }
}
