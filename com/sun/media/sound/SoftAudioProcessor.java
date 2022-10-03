package com.sun.media.sound;

public interface SoftAudioProcessor
{
    void globalParameterControlChange(final int[] p0, final long p1, final long p2);
    
    void init(final float p0, final float p1);
    
    void setInput(final int p0, final SoftAudioBuffer p1);
    
    void setOutput(final int p0, final SoftAudioBuffer p1);
    
    void setMixMode(final boolean p0);
    
    void processAudio();
    
    void processControlLogic();
}
