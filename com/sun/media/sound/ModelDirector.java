package com.sun.media.sound;

public interface ModelDirector
{
    void noteOn(final int p0, final int p1);
    
    void noteOff(final int p0, final int p1);
    
    void close();
}
