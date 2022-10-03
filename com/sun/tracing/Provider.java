package com.sun.tracing;

import java.lang.reflect.Method;

public interface Provider
{
    Probe getProbe(final Method p0);
    
    void dispose();
}
