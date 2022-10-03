package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;

public interface XMLDTDHandler
{
    public static final short CONDITIONAL_INCLUDE = 0;
    public static final short CONDITIONAL_IGNORE = 1;
    
    void startDTD(final XMLLocator p0, final Augmentations p1) throws XNIException;
    
    void startParameterEntity(final String p0, final XMLResourceIdentifier p1, final String p2, final Augmentations p3) throws XNIException;
    
    void textDecl(final String p0, final String p1, final Augmentations p2) throws XNIException;
    
    void endParameterEntity(final String p0, final Augmentations p1) throws XNIException;
    
    void startExternalSubset(final XMLResourceIdentifier p0, final Augmentations p1) throws XNIException;
    
    void endExternalSubset(final Augmentations p0) throws XNIException;
    
    void comment(final XMLString p0, final Augmentations p1) throws XNIException;
    
    void processingInstruction(final String p0, final XMLString p1, final Augmentations p2) throws XNIException;
    
    void elementDecl(final String p0, final String p1, final Augmentations p2) throws XNIException;
    
    void startAttlist(final String p0, final Augmentations p1) throws XNIException;
    
    void attributeDecl(final String p0, final String p1, final String p2, final String[] p3, final String p4, final XMLString p5, final XMLString p6, final Augmentations p7) throws XNIException;
    
    void endAttlist(final Augmentations p0) throws XNIException;
    
    void internalEntityDecl(final String p0, final XMLString p1, final XMLString p2, final Augmentations p3) throws XNIException;
    
    void externalEntityDecl(final String p0, final XMLResourceIdentifier p1, final Augmentations p2) throws XNIException;
    
    void unparsedEntityDecl(final String p0, final XMLResourceIdentifier p1, final String p2, final Augmentations p3) throws XNIException;
    
    void notationDecl(final String p0, final XMLResourceIdentifier p1, final Augmentations p2) throws XNIException;
    
    void startConditional(final short p0, final Augmentations p1) throws XNIException;
    
    void ignoredCharacters(final XMLString p0, final Augmentations p1) throws XNIException;
    
    void endConditional(final Augmentations p0) throws XNIException;
    
    void endDTD(final Augmentations p0) throws XNIException;
    
    void setDTDSource(final XMLDTDSource p0);
    
    XMLDTDSource getDTDSource();
}
