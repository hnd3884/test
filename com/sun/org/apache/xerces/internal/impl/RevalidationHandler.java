package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public interface RevalidationHandler extends XMLDocumentFilter
{
    boolean characterData(final String p0, final Augmentations p1);
}
