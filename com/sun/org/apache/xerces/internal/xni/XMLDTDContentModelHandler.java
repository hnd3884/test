package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;

public interface XMLDTDContentModelHandler
{
    public static final short SEPARATOR_CHOICE = 0;
    public static final short SEPARATOR_SEQUENCE = 1;
    public static final short OCCURS_ZERO_OR_ONE = 2;
    public static final short OCCURS_ZERO_OR_MORE = 3;
    public static final short OCCURS_ONE_OR_MORE = 4;
    
    void startContentModel(final String p0, final Augmentations p1) throws XNIException;
    
    void any(final Augmentations p0) throws XNIException;
    
    void empty(final Augmentations p0) throws XNIException;
    
    void startGroup(final Augmentations p0) throws XNIException;
    
    void pcdata(final Augmentations p0) throws XNIException;
    
    void element(final String p0, final Augmentations p1) throws XNIException;
    
    void separator(final short p0, final Augmentations p1) throws XNIException;
    
    void occurrence(final short p0, final Augmentations p1) throws XNIException;
    
    void endGroup(final Augmentations p0) throws XNIException;
    
    void endContentModel(final Augmentations p0) throws XNIException;
    
    void setDTDContentModelSource(final XMLDTDContentModelSource p0);
    
    XMLDTDContentModelSource getDTDContentModelSource();
}
