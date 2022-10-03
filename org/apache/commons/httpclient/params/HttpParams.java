package org.apache.commons.httpclient.params;

public interface HttpParams
{
    HttpParams getDefaults();
    
    void setDefaults(final HttpParams p0);
    
    Object getParameter(final String p0);
    
    void setParameter(final String p0, final Object p1);
    
    long getLongParameter(final String p0, final long p1);
    
    void setLongParameter(final String p0, final long p1);
    
    int getIntParameter(final String p0, final int p1);
    
    void setIntParameter(final String p0, final int p1);
    
    double getDoubleParameter(final String p0, final double p1);
    
    void setDoubleParameter(final String p0, final double p1);
    
    boolean getBooleanParameter(final String p0, final boolean p1);
    
    void setBooleanParameter(final String p0, final boolean p1);
    
    boolean isParameterSet(final String p0);
    
    boolean isParameterSetLocally(final String p0);
    
    boolean isParameterTrue(final String p0);
    
    boolean isParameterFalse(final String p0);
}
