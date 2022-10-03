package com.sun.media.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.midi.Synthesizer;

public interface AudioSynthesizer extends Synthesizer
{
    AudioFormat getFormat();
    
    AudioSynthesizerPropertyInfo[] getPropertyInfo(final Map<String, Object> p0);
    
    void open(final SourceDataLine p0, final Map<String, Object> p1) throws MidiUnavailableException;
    
    AudioInputStream openStream(final AudioFormat p0, final Map<String, Object> p1) throws MidiUnavailableException;
}
