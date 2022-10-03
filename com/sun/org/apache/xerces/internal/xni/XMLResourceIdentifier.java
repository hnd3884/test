package com.sun.org.apache.xerces.internal.xni;

public interface XMLResourceIdentifier
{
    void setPublicId(final String p0);
    
    String getPublicId();
    
    void setExpandedSystemId(final String p0);
    
    String getExpandedSystemId();
    
    void setLiteralSystemId(final String p0);
    
    String getLiteralSystemId();
    
    void setBaseSystemId(final String p0);
    
    String getBaseSystemId();
    
    void setNamespace(final String p0);
    
    String getNamespace();
}
