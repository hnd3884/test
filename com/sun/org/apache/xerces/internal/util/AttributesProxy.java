package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.AttributeList;

public final class AttributesProxy implements AttributeList, Attributes2
{
    private XMLAttributes fAttributes;
    
    public AttributesProxy(final XMLAttributes attributes) {
        this.fAttributes = attributes;
    }
    
    public void setAttributes(final XMLAttributes attributes) {
        this.fAttributes = attributes;
    }
    
    public XMLAttributes getAttributes() {
        return this.fAttributes;
    }
    
    @Override
    public int getLength() {
        return this.fAttributes.getLength();
    }
    
    @Override
    public String getQName(final int index) {
        return this.fAttributes.getQName(index);
    }
    
    @Override
    public String getURI(final int index) {
        final String uri = this.fAttributes.getURI(index);
        return (uri != null) ? uri : XMLSymbols.EMPTY_STRING;
    }
    
    @Override
    public String getLocalName(final int index) {
        return this.fAttributes.getLocalName(index);
    }
    
    @Override
    public String getType(final int i) {
        return this.fAttributes.getType(i);
    }
    
    @Override
    public String getType(final String name) {
        return this.fAttributes.getType(name);
    }
    
    @Override
    public String getType(final String uri, final String localName) {
        return uri.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getType(null, localName) : this.fAttributes.getType(uri, localName);
    }
    
    @Override
    public String getValue(final int i) {
        return this.fAttributes.getValue(i);
    }
    
    @Override
    public String getValue(final String name) {
        return this.fAttributes.getValue(name);
    }
    
    @Override
    public String getValue(final String uri, final String localName) {
        return uri.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getValue(null, localName) : this.fAttributes.getValue(uri, localName);
    }
    
    @Override
    public int getIndex(final String qName) {
        return this.fAttributes.getIndex(qName);
    }
    
    @Override
    public int getIndex(final String uri, final String localPart) {
        return uri.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getIndex(null, localPart) : this.fAttributes.getIndex(uri, localPart);
    }
    
    @Override
    public boolean isDeclared(final int index) {
        if (index < 0 || index >= this.fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
    }
    
    @Override
    public boolean isDeclared(final String qName) {
        final int index = this.getIndex(qName);
        if (index == -1) {
            throw new IllegalArgumentException(qName);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
    }
    
    @Override
    public boolean isDeclared(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        if (index == -1) {
            throw new IllegalArgumentException(localName);
        }
        return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
    }
    
    @Override
    public boolean isSpecified(final int index) {
        if (index < 0 || index >= this.fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.fAttributes.isSpecified(index);
    }
    
    @Override
    public boolean isSpecified(final String qName) {
        final int index = this.getIndex(qName);
        if (index == -1) {
            throw new IllegalArgumentException(qName);
        }
        return this.fAttributes.isSpecified(index);
    }
    
    @Override
    public boolean isSpecified(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        if (index == -1) {
            throw new IllegalArgumentException(localName);
        }
        return this.fAttributes.isSpecified(index);
    }
    
    @Override
    public String getName(final int i) {
        return this.fAttributes.getQName(i);
    }
}
