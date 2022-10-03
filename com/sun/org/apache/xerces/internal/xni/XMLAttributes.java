package com.sun.org.apache.xerces.internal.xni;

public interface XMLAttributes
{
    int addAttribute(final QName p0, final String p1, final String p2);
    
    void removeAllAttributes();
    
    void removeAttributeAt(final int p0);
    
    int getLength();
    
    int getIndex(final String p0);
    
    int getIndex(final String p0, final String p1);
    
    void setName(final int p0, final QName p1);
    
    void getName(final int p0, final QName p1);
    
    String getPrefix(final int p0);
    
    String getURI(final int p0);
    
    String getLocalName(final int p0);
    
    String getQName(final int p0);
    
    QName getQualifiedName(final int p0);
    
    void setType(final int p0, final String p1);
    
    String getType(final int p0);
    
    String getType(final String p0);
    
    String getType(final String p0, final String p1);
    
    void setValue(final int p0, final String p1);
    
    void setValue(final int p0, final String p1, final XMLString p2);
    
    String getValue(final int p0);
    
    String getValue(final String p0);
    
    String getValue(final String p0, final String p1);
    
    void setNonNormalizedValue(final int p0, final String p1);
    
    String getNonNormalizedValue(final int p0);
    
    void setSpecified(final int p0, final boolean p1);
    
    boolean isSpecified(final int p0);
    
    Augmentations getAugmentations(final int p0);
    
    Augmentations getAugmentations(final String p0, final String p1);
    
    Augmentations getAugmentations(final String p0);
    
    void setAugmentations(final int p0, final Augmentations p1);
}
