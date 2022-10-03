package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public interface ModelChannelMixer extends MidiChannel
{
    boolean process(final float[][] p0, final int p1, final int p2);
    
    void stop();
}
