package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSRegion
{
    public static final int OPTION_SELFNONEXCLUSIVE = 1;
    List<DLSModulator> modulators;
    int keyfrom;
    int keyto;
    int velfrom;
    int velto;
    int options;
    int exclusiveClass;
    int fusoptions;
    int phasegroup;
    long channel;
    DLSSample sample;
    DLSSampleOptions sampleoptions;
    
    public DLSRegion() {
        this.modulators = new ArrayList<DLSModulator>();
        this.sample = null;
    }
    
    public List<DLSModulator> getModulators() {
        return this.modulators;
    }
    
    public long getChannel() {
        return this.channel;
    }
    
    public void setChannel(final long channel) {
        this.channel = channel;
    }
    
    public int getExclusiveClass() {
        return this.exclusiveClass;
    }
    
    public void setExclusiveClass(final int exclusiveClass) {
        this.exclusiveClass = exclusiveClass;
    }
    
    public int getFusoptions() {
        return this.fusoptions;
    }
    
    public void setFusoptions(final int fusoptions) {
        this.fusoptions = fusoptions;
    }
    
    public int getKeyfrom() {
        return this.keyfrom;
    }
    
    public void setKeyfrom(final int keyfrom) {
        this.keyfrom = keyfrom;
    }
    
    public int getKeyto() {
        return this.keyto;
    }
    
    public void setKeyto(final int keyto) {
        this.keyto = keyto;
    }
    
    public int getOptions() {
        return this.options;
    }
    
    public void setOptions(final int options) {
        this.options = options;
    }
    
    public int getPhasegroup() {
        return this.phasegroup;
    }
    
    public void setPhasegroup(final int phasegroup) {
        this.phasegroup = phasegroup;
    }
    
    public DLSSample getSample() {
        return this.sample;
    }
    
    public void setSample(final DLSSample sample) {
        this.sample = sample;
    }
    
    public int getVelfrom() {
        return this.velfrom;
    }
    
    public void setVelfrom(final int velfrom) {
        this.velfrom = velfrom;
    }
    
    public int getVelto() {
        return this.velto;
    }
    
    public void setVelto(final int velto) {
        this.velto = velto;
    }
    
    public void setModulators(final List<DLSModulator> modulators) {
        this.modulators = modulators;
    }
    
    public DLSSampleOptions getSampleoptions() {
        return this.sampleoptions;
    }
    
    public void setSampleoptions(final DLSSampleOptions sampleoptions) {
        this.sampleoptions = sampleoptions;
    }
}
