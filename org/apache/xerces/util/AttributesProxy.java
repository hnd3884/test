package org.apache.xerces.util;

import org.apache.xerces.xni.XMLAttributes;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.AttributeList;

public final class AttributesProxy implements AttributeList, Attributes2
{
    private XMLAttributes fAttributes;
    
    public AttributesProxy(final XMLAttributes fAttributes) {
        this.fAttributes = fAttributes;
    }
    
    public void setAttributes(final XMLAttributes fAttributes) {
        this.fAttributes = fAttributes;
    }
    
    public XMLAttributes getAttributes() {
        return this.fAttributes;
    }
    
    public int getLength() {
        return this.fAttributes.getLength();
    }
    
    public String getQName(final int n) {
        return this.fAttributes.getQName(n);
    }
    
    public String getURI(final int n) {
        final String uri = this.fAttributes.getURI(n);
        return (uri != null) ? uri : XMLSymbols.EMPTY_STRING;
    }
    
    public String getLocalName(final int n) {
        return this.fAttributes.getLocalName(n);
    }
    
    public String getType(final int n) {
        return this.fAttributes.getType(n);
    }
    
    public String getType(final String s) {
        return this.fAttributes.getType(s);
    }
    
    public String getType(final String s, final String s2) {
        return s.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getType(null, s2) : this.fAttributes.getType(s, s2);
    }
    
    public String getValue(final int n) {
        return this.fAttributes.getValue(n);
    }
    
    public String getValue(final String s) {
        return this.fAttributes.getValue(s);
    }
    
    public String getValue(final String s, final String s2) {
        return s.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getValue(null, s2) : this.fAttributes.getValue(s, s2);
    }
    
    public int getIndex(final String s) {
        return this.fAttributes.getIndex(s);
    }
    
    public int getIndex(final String s, final String s2) {
        return s.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getIndex(null, s2) : this.fAttributes.getIndex(s, s2);
    }
    
    public boolean isDeclared(final int n) {
        if (n < 0 || n >= this.fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
    }
    
    public boolean isDeclared(final String s) {
        final int index = this.getIndex(s);
        if (index == -1) {
            throw new IllegalArgumentException(s);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
    }
    
    public boolean isDeclared(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        if (index == -1) {
            throw new IllegalArgumentException(s2);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
    }
    
    public boolean isSpecified(final int n) {
        if (n < 0 || n >= this.fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return this.fAttributes.isSpecified(n);
    }
    
    public boolean isSpecified(final String s) {
        final int index = this.getIndex(s);
        if (index == -1) {
            throw new IllegalArgumentException(s);
        }
        return this.fAttributes.isSpecified(index);
    }
    
    public boolean isSpecified(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        if (index == -1) {
            throw new IllegalArgumentException(s2);
        }
        return this.fAttributes.isSpecified(index);
    }
    
    public String getName(final int n) {
        return this.fAttributes.getQName(n);
    }
}
