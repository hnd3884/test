package javax.sound.midi;

public interface Synthesizer extends MidiDevice
{
    int getMaxPolyphony();
    
    long getLatency();
    
    MidiChannel[] getChannels();
    
    VoiceStatus[] getVoiceStatus();
    
    boolean isSoundbankSupported(final Soundbank p0);
    
    boolean loadInstrument(final Instrument p0);
    
    void unloadInstrument(final Instrument p0);
    
    boolean remapInstrument(final Instrument p0, final Instrument p1);
    
    Soundbank getDefaultSoundbank();
    
    Instrument[] getAvailableInstruments();
    
    Instrument[] getLoadedInstruments();
    
    boolean loadAllInstruments(final Soundbank p0);
    
    void unloadAllInstruments(final Soundbank p0);
    
    boolean loadInstruments(final Soundbank p0, final Patch[] p1);
    
    void unloadInstruments(final Soundbank p0, final Patch[] p1);
}
