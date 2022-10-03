package org.cyberneko.html;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

public interface HTMLTagBalancingListener
{
    void ignoredStartElement(final QName p0, final XMLAttributes p1, final Augmentations p2);
    
    void ignoredEndElement(final QName p0, final Augmentations p1);
}
