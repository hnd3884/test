package javax.sound.midi;

public interface MidiChannel
{
    void noteOn(final int p0, final int p1);
    
    void noteOff(final int p0, final int p1);
    
    void noteOff(final int p0);
    
    void setPolyPressure(final int p0, final int p1);
    
    int getPolyPressure(final int p0);
    
    void setChannelPressure(final int p0);
    
    int getChannelPressure();
    
    void controlChange(final int p0, final int p1);
    
    int getController(final int p0);
    
    void programChange(final int p0);
    
    void programChange(final int p0, final int p1);
    
    int getProgram();
    
    void setPitchBend(final int p0);
    
    int getPitchBend();
    
    void resetAllControllers();
    
    void allNotesOff();
    
    void allSoundOff();
    
    boolean localControl(final boolean p0);
    
    void setMono(final boolean p0);
    
    boolean getMono();
    
    void setOmni(final boolean p0);
    
    boolean getOmni();
    
    void setMute(final boolean p0);
    
    boolean getMute();
    
    void setSolo(final boolean p0);
    
    boolean getSolo();
}
