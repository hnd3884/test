package com.sun.media.sound;

public final class SF2InstrumentRegion extends SF2Region
{
    SF2Layer layer;
    
    public SF2Layer getLayer() {
        return this.layer;
    }
    
    public void setLayer(final SF2Layer layer) {
        this.layer = layer;
    }
}
