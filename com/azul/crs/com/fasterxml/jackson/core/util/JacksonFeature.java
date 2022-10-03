package com.azul.crs.com.fasterxml.jackson.core.util;

public interface JacksonFeature
{
    boolean enabledByDefault();
    
    int getMask();
    
    boolean enabledIn(final int p0);
}
