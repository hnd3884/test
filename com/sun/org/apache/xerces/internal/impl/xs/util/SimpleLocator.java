package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public class SimpleLocator implements XMLLocator
{
    String lsid;
    String esid;
    int line;
    int column;
    int charOffset;
    
    public SimpleLocator() {
    }
    
    public SimpleLocator(final String lsid, final String esid, final int line, final int column) {
        this(lsid, esid, line, column, -1);
    }
    
    public void setValues(final String lsid, final String esid, final int line, final int column) {
        this.setValues(lsid, esid, line, column, -1);
    }
    
    public SimpleLocator(final String lsid, final String esid, final int line, final int column, final int offset) {
        this.line = line;
        this.column = column;
        this.lsid = lsid;
        this.esid = esid;
        this.charOffset = offset;
    }
    
    public void setValues(final String lsid, final String esid, final int line, final int column, final int offset) {
        this.line = line;
        this.column = column;
        this.lsid = lsid;
        this.esid = esid;
        this.charOffset = offset;
    }
    
    @Override
    public int getLineNumber() {
        return this.line;
    }
    
    @Override
    public int getColumnNumber() {
        return this.column;
    }
    
    @Override
    public int getCharacterOffset() {
        return this.charOffset;
    }
    
    @Override
    public String getPublicId() {
        return null;
    }
    
    @Override
    public String getExpandedSystemId() {
        return this.esid;
    }
    
    @Override
    public String getLiteralSystemId() {
        return this.lsid;
    }
    
    @Override
    public String getBaseSystemId() {
        return null;
    }
    
    public void setColumnNumber(final int col) {
        this.column = col;
    }
    
    public void setLineNumber(final int line) {
        this.line = line;
    }
    
    public void setCharacterOffset(final int offset) {
        this.charOffset = offset;
    }
    
    public void setBaseSystemId(final String systemId) {
    }
    
    public void setExpandedSystemId(final String systemId) {
        this.esid = systemId;
    }
    
    public void setLiteralSystemId(final String systemId) {
        this.lsid = systemId;
    }
    
    public void setPublicId(final String publicId) {
    }
    
    @Override
    public String getEncoding() {
        return null;
    }
    
    @Override
    public String getXMLVersion() {
        return null;
    }
}
