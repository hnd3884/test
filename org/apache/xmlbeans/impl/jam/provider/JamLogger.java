package org.apache.xmlbeans.impl.jam.provider;

public interface JamLogger
{
    void setVerbose(final Class p0);
    
    boolean isVerbose(final Object p0);
    
    boolean isVerbose(final Class p0);
    
    void setShowWarnings(final boolean p0);
    
    void verbose(final String p0, final Object p1);
    
    void verbose(final Throwable p0, final Object p1);
    
    void verbose(final String p0);
    
    void verbose(final Throwable p0);
    
    void warning(final Throwable p0);
    
    void warning(final String p0);
    
    void error(final Throwable p0);
    
    void error(final String p0);
    
    @Deprecated
    boolean isVerbose();
}
