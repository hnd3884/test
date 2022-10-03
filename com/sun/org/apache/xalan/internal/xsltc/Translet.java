package com.sun.org.apache.xalan.internal.xsltc;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public interface Translet
{
    void transform(final DOM p0, final SerializationHandler p1) throws TransletException;
    
    void transform(final DOM p0, final SerializationHandler[] p1) throws TransletException;
    
    void transform(final DOM p0, final DTMAxisIterator p1, final SerializationHandler p2) throws TransletException;
    
    Object addParameter(final String p0, final Object p1);
    
    void buildKeys(final DOM p0, final DTMAxisIterator p1, final SerializationHandler p2, final int p3) throws TransletException;
    
    void addAuxiliaryClass(final Class p0);
    
    Class getAuxiliaryClass(final String p0);
    
    String[] getNamesArray();
    
    String[] getUrisArray();
    
    int[] getTypesArray();
    
    String[] getNamespaceArray();
    
    boolean overrideDefaultParser();
    
    void setOverrideDefaultParser(final boolean p0);
}
