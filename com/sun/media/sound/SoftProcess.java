package com.sun.media.sound;

public interface SoftProcess extends SoftControl
{
    void init(final SoftSynthesizer p0);
    
    double[] get(final int p0, final String p1);
    
    void processControlLogic();
    
    void reset();
}
