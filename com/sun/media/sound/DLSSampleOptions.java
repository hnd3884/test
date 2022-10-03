package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSSampleOptions
{
    int unitynote;
    short finetune;
    int attenuation;
    long options;
    List<DLSSampleLoop> loops;
    
    public DLSSampleOptions() {
        this.loops = new ArrayList<DLSSampleLoop>();
    }
    
    public int getAttenuation() {
        return this.attenuation;
    }
    
    public void setAttenuation(final int attenuation) {
        this.attenuation = attenuation;
    }
    
    public short getFinetune() {
        return this.finetune;
    }
    
    public void setFinetune(final short finetune) {
        this.finetune = finetune;
    }
    
    public List<DLSSampleLoop> getLoops() {
        return this.loops;
    }
    
    public long getOptions() {
        return this.options;
    }
    
    public void setOptions(final long options) {
        this.options = options;
    }
    
    public int getUnitynote() {
        return this.unitynote;
    }
    
    public void setUnitynote(final int unitynote) {
        this.unitynote = unitynote;
    }
}
