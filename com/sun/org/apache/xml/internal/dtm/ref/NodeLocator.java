package com.sun.org.apache.xml.internal.dtm.ref;

import javax.xml.transform.SourceLocator;

public class NodeLocator implements SourceLocator
{
    protected String m_publicId;
    protected String m_systemId;
    protected int m_lineNumber;
    protected int m_columnNumber;
    
    public NodeLocator(final String publicId, final String systemId, final int lineNumber, final int columnNumber) {
        this.m_publicId = publicId;
        this.m_systemId = systemId;
        this.m_lineNumber = lineNumber;
        this.m_columnNumber = columnNumber;
    }
    
    @Override
    public String getPublicId() {
        return this.m_publicId;
    }
    
    @Override
    public String getSystemId() {
        return this.m_systemId;
    }
    
    @Override
    public int getLineNumber() {
        return this.m_lineNumber;
    }
    
    @Override
    public int getColumnNumber() {
        return this.m_columnNumber;
    }
    
    @Override
    public String toString() {
        return "file '" + this.m_systemId + "', line #" + this.m_lineNumber + ", column #" + this.m_columnNumber;
    }
}
