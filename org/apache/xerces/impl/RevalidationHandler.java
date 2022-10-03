package org.apache.xerces.impl;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.parser.XMLDocumentFilter;

public interface RevalidationHandler extends XMLDocumentFilter
{
    boolean characterData(final String p0, final Augmentations p1);
}
