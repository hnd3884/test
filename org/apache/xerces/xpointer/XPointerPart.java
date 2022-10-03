package org.apache.xerces.xpointer;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;

public interface XPointerPart
{
    public static final int EVENT_ELEMENT_START = 0;
    public static final int EVENT_ELEMENT_END = 1;
    public static final int EVENT_ELEMENT_EMPTY = 2;
    
    void parseXPointer(final String p0) throws XNIException;
    
    boolean resolveXPointer(final QName p0, final XMLAttributes p1, final Augmentations p2, final int p3) throws XNIException;
    
    boolean isFragmentResolved() throws XNIException;
    
    boolean isChildFragmentResolved() throws XNIException;
    
    String getSchemeName();
    
    String getSchemeData();
    
    void setSchemeName(final String p0);
    
    void setSchemeData(final String p0);
}
