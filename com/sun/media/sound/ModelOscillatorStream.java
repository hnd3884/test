package com.sun.media.sound;

import java.io.IOException;
import javax.sound.midi.VoiceStatus;
import javax.sound.midi.MidiChannel;

public interface ModelOscillatorStream
{
    void setPitch(final float p0);
    
    void noteOn(final MidiChannel p0, final VoiceStatus p1, final int p2, final int p3);
    
    void noteOff(final int p0);
    
    int read(final float[][] p0, final int p1, final int p2) throws IOException;
    
    void close() throws IOException;
}
