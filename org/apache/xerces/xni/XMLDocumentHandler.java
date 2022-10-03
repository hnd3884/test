package org.apache.xerces.xni;

import org.apache.xerces.xni.parser.XMLDocumentSource;

public interface XMLDocumentHandler
{
    void startDocument(final XMLLocator p0, final String p1, final NamespaceContext p2, final Augmentations p3) throws XNIException;
    
    void xmlDecl(final String p0, final String p1, final String p2, final Augmentations p3) throws XNIException;
    
    void doctypeDecl(final String p0, final String p1, final String p2, final Augmentations p3) throws XNIException;
    
    void comment(final XMLString p0, final Augmentations p1) throws XNIException;
    
    void processingInstruction(final String p0, final XMLString p1, final Augmentations p2) throws XNIException;
    
    void startElement(final QName p0, final XMLAttributes p1, final Augmentations p2) throws XNIException;
    
    void emptyElement(final QName p0, final XMLAttributes p1, final Augmentations p2) throws XNIException;
    
    void startGeneralEntity(final String p0, final XMLResourceIdentifier p1, final String p2, final Augmentations p3) throws XNIException;
    
    void textDecl(final String p0, final String p1, final Augmentations p2) throws XNIException;
    
    void endGeneralEntity(final String p0, final Augmentations p1) throws XNIException;
    
    void characters(final XMLString p0, final Augmentations p1) throws XNIException;
    
    void ignorableWhitespace(final XMLString p0, final Augmentations p1) throws XNIException;
    
    void endElement(final QName p0, final Augmentations p1) throws XNIException;
    
    void startCDATA(final Augmentations p0) throws XNIException;
    
    void endCDATA(final Augmentations p0) throws XNIException;
    
    void endDocument(final Augmentations p0) throws XNIException;
    
    void setDocumentSource(final XMLDocumentSource p0);
    
    XMLDocumentSource getDocumentSource();
}
