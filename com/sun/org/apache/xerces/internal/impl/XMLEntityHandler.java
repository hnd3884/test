package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public interface XMLEntityHandler
{
    void startEntity(final String p0, final XMLResourceIdentifier p1, final String p2, final Augmentations p3) throws XNIException;
    
    void endEntity(final String p0, final Augmentations p1) throws IOException, XNIException;
}
