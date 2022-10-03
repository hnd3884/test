package org.apache.axiom.om;

import java.io.UnsupportedEncodingException;
import java.io.InputStream;

public interface OMDataSourceExt extends OMDataSource
{
    @Deprecated
    public static final String LOSSY_PREFIX = "lossyPrefix";
    
    Object getObject();
    
    boolean isDestructiveRead();
    
    boolean isDestructiveWrite();
    
    InputStream getXMLInputStream(final String p0) throws UnsupportedEncodingException;
    
    byte[] getXMLBytes(final String p0) throws UnsupportedEncodingException;
    
    void close();
    
    OMDataSourceExt copy();
    
    boolean hasProperty(final String p0);
    
    Object getProperty(final String p0);
    
    Object setProperty(final String p0, final Object p1);
}
