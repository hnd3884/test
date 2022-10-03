package com.sun.tracing;

public interface Probe
{
    boolean isEnabled();
    
    void trigger(final Object... p0);
}
