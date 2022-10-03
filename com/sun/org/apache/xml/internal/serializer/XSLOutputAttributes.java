package com.sun.org.apache.xml.internal.serializer;

import java.util.Vector;

interface XSLOutputAttributes
{
    String getDoctypePublic();
    
    String getDoctypeSystem();
    
    String getEncoding();
    
    boolean getIndent();
    
    int getIndentAmount();
    
    String getMediaType();
    
    boolean getOmitXMLDeclaration();
    
    String getStandalone();
    
    String getVersion();
    
    void setCdataSectionElements(final Vector p0);
    
    void setDoctype(final String p0, final String p1);
    
    void setDoctypePublic(final String p0);
    
    void setDoctypeSystem(final String p0);
    
    void setEncoding(final String p0);
    
    void setIndent(final boolean p0);
    
    void setMediaType(final String p0);
    
    void setOmitXMLDeclaration(final boolean p0);
    
    void setStandalone(final String p0);
    
    void setVersion(final String p0);
}
