package com.sun.media.sound;

public abstract class ModelAbstractChannelMixer implements ModelChannelMixer
{
    @Override
    public abstract boolean process(final float[][] p0, final int p1, final int p2);
    
    @Override
    public abstract void stop();
    
    @Override
    public void allNotesOff() {
    }
    
    @Override
    public void allSoundOff() {
    }
    
    @Override
    public void controlChange(final int n, final int n2) {
    }
    
    @Override
    public int getChannelPressure() {
        return 0;
    }
    
    @Override
    public int getController(final int n) {
        return 0;
    }
    
    @Override
    public boolean getMono() {
        return false;
    }
    
    @Override
    public boolean getMute() {
        return false;
    }
    
    @Override
    public boolean getOmni() {
        return false;
    }
    
    @Override
    public int getPitchBend() {
        return 0;
    }
    
    @Override
    public int getPolyPressure(final int n) {
        return 0;
    }
    
    @Override
    public int getProgram() {
        return 0;
    }
    
    @Override
    public boolean getSolo() {
        return false;
    }
    
    @Override
    public boolean localControl(final boolean b) {
        return false;
    }
    
    @Override
    public void noteOff(final int n) {
    }
    
    @Override
    public void noteOff(final int n, final int n2) {
    }
    
    @Override
    public void noteOn(final int n, final int n2) {
    }
    
    @Override
    public void programChange(final int n) {
    }
    
    @Override
    public void programChange(final int n, final int n2) {
    }
    
    @Override
    public void resetAllControllers() {
    }
    
    @Override
    public void setChannelPressure(final int n) {
    }
    
    @Override
    public void setMono(final boolean b) {
    }
    
    @Override
    public void setMute(final boolean b) {
    }
    
    @Override
    public void setOmni(final boolean b) {
    }
    
    @Override
    public void setPitchBend(final int n) {
    }
    
    @Override
    public void setPolyPressure(final int n, final int n2) {
    }
    
    @Override
    public void setSolo(final boolean b) {
    }
}
