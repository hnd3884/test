package com.sun.media.sound;

public interface ModelWavetable extends ModelOscillator
{
    public static final int LOOP_TYPE_OFF = 0;
    public static final int LOOP_TYPE_FORWARD = 1;
    public static final int LOOP_TYPE_RELEASE = 2;
    public static final int LOOP_TYPE_PINGPONG = 4;
    public static final int LOOP_TYPE_REVERSE = 8;
    
    AudioFloatInputStream openStream();
    
    float getLoopLength();
    
    float getLoopStart();
    
    int getLoopType();
    
    float getPitchcorrection();
}
