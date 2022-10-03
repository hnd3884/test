package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public interface XPointerProcessor
{
    public static final int EVENT_ELEMENT_START = 0;
    public static final int EVENT_ELEMENT_END = 1;
    public static final int EVENT_ELEMENT_EMPTY = 2;
    
    void parseXPointer(final String p0) throws XNIException;
    
    boolean resolveXPointer(final QName p0, final XMLAttributes p1, final Augmentations p2, final int p3) throws XNIException;
    
    boolean isFragmentResolved() throws XNIException;
    
    boolean isXPointerResolved() throws XNIException;
}
