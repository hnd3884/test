package org.apache.xerces.impl.xs.util;

import org.apache.xerces.xni.XMLLocator;

public final class SimpleLocator implements XMLLocator
{
    private String lsid;
    private String esid;
    private int line;
    private int column;
    private int charOffset;
    
    public SimpleLocator() {
    }
    
    public SimpleLocator(final String s, final String s2, final int n, final int n2) {
        this(s, s2, n, n2, -1);
    }
    
    public void setValues(final String s, final String s2, final int n, final int n2) {
        this.setValues(s, s2, n, n2, -1);
    }
    
    public SimpleLocator(final String lsid, final String esid, final int line, final int column, final int charOffset) {
        this.line = line;
        this.column = column;
        this.lsid = lsid;
        this.esid = esid;
        this.charOffset = charOffset;
    }
    
    public void setValues(final String lsid, final String esid, final int line, final int column, final int charOffset) {
        this.line = line;
        this.column = column;
        this.lsid = lsid;
        this.esid = esid;
        this.charOffset = charOffset;
    }
    
    public int getLineNumber() {
        return this.line;
    }
    
    public int getColumnNumber() {
        return this.column;
    }
    
    public int getCharacterOffset() {
        return this.charOffset;
    }
    
    public String getPublicId() {
        return null;
    }
    
    public String getExpandedSystemId() {
        return this.esid;
    }
    
    public String getLiteralSystemId() {
        return this.lsid;
    }
    
    public String getBaseSystemId() {
        return null;
    }
    
    public void setColumnNumber(final int column) {
        this.column = column;
    }
    
    public void setLineNumber(final int line) {
        this.line = line;
    }
    
    public void setCharacterOffset(final int charOffset) {
        this.charOffset = charOffset;
    }
    
    public void setBaseSystemId(final String s) {
    }
    
    public void setExpandedSystemId(final String esid) {
        this.esid = esid;
    }
    
    public void setLiteralSystemId(final String lsid) {
        this.lsid = lsid;
    }
    
    public void setPublicId(final String s) {
    }
    
    public String getEncoding() {
        return null;
    }
    
    public String getXMLVersion() {
        return null;
    }
}
