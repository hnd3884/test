package com.sun.media.sound;

import java.util.ArrayList;
import javax.sound.midi.Soundbank;
import java.util.List;
import javax.sound.midi.SoundbankResource;

public final class SF2Layer extends SoundbankResource
{
    String name;
    SF2GlobalRegion globalregion;
    List<SF2LayerRegion> regions;
    
    public SF2Layer(final SF2Soundbank sf2Soundbank) {
        super(sf2Soundbank, null, null);
        this.name = "";
        this.globalregion = null;
        this.regions = new ArrayList<SF2LayerRegion>();
    }
    
    public SF2Layer() {
        super(null, null, null);
        this.name = "";
        this.globalregion = null;
        this.regions = new ArrayList<SF2LayerRegion>();
    }
    
    @Override
    public Object getData() {
        return null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<SF2LayerRegion> getRegions() {
        return this.regions;
    }
    
    public SF2GlobalRegion getGlobalRegion() {
        return this.globalregion;
    }
    
    public void setGlobalZone(final SF2GlobalRegion globalregion) {
        this.globalregion = globalregion;
    }
    
    @Override
    public String toString() {
        return "Layer: " + this.name;
    }
}
