package javax.sound.midi;

public interface Soundbank
{
    String getName();
    
    String getVersion();
    
    String getVendor();
    
    String getDescription();
    
    SoundbankResource[] getResources();
    
    Instrument[] getInstruments();
    
    Instrument getInstrument(final Patch p0);
}
