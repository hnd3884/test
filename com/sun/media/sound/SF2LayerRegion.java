package com.sun.media.sound;

public final class SF2LayerRegion extends SF2Region
{
    SF2Sample sample;
    
    public SF2Sample getSample() {
        return this.sample;
    }
    
    public void setSample(final SF2Sample sample) {
        this.sample = sample;
    }
}
