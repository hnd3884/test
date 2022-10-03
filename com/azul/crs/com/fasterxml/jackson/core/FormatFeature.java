package com.azul.crs.com.fasterxml.jackson.core;

import com.azul.crs.com.fasterxml.jackson.core.util.JacksonFeature;

public interface FormatFeature extends JacksonFeature
{
    boolean enabledByDefault();
    
    int getMask();
    
    boolean enabledIn(final int p0);
}
