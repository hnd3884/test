package org.apache.xerces.impl.xs.assertion;

import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

public interface XMLAssertHandler extends XSAssertionXPath2Value
{
    void startElement(final QName p0, final XMLAttributes p1, final Augmentations p2) throws Exception;
    
    void endElement(final QName p0, final Augmentations p1) throws Exception;
    
    void characters(final XMLString p0);
    
    void comment(final XMLString p0);
    
    void processingInstruction(final String p0, final XMLString p1);
    
    void setProperty(final String p0, final Object p1) throws IllegalArgumentException;
    
    Object getProperty(final String p0) throws IllegalArgumentException;
}
