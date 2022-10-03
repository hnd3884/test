package com.sun.media.sound;

public interface ModelOscillator
{
    int getChannels();
    
    float getAttenuation();
    
    ModelOscillatorStream open(final float p0);
}
