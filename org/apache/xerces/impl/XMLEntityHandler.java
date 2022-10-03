package org.apache.xerces.impl;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;

public interface XMLEntityHandler
{
    void startEntity(final String p0, final XMLResourceIdentifier p1, final String p2, final Augmentations p3) throws XNIException;
    
    void endEntity(final String p0, final Augmentations p1) throws XNIException;
}
