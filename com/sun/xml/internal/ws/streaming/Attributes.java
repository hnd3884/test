package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;

public interface Attributes
{
    int getLength();
    
    boolean isNamespaceDeclaration(final int p0);
    
    QName getName(final int p0);
    
    String getURI(final int p0);
    
    String getLocalName(final int p0);
    
    String getPrefix(final int p0);
    
    String getValue(final int p0);
    
    int getIndex(final QName p0);
    
    int getIndex(final String p0, final String p1);
    
    int getIndex(final String p0);
    
    String getValue(final QName p0);
    
    String getValue(final String p0, final String p1);
    
    String getValue(final String p0);
}
