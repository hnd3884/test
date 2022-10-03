package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Upgrades extends GenericJson
{
    @Key
    private Integer upgradesAvailable;
    
    public Integer getUpgradesAvailable() {
        return this.upgradesAvailable;
    }
    
    public Upgrades setUpgradesAvailable(final Integer upgradesAvailable) {
        this.upgradesAvailable = upgradesAvailable;
        return this;
    }
    
    public Upgrades set(final String s, final Object o) {
        return (Upgrades)super.set(s, o);
    }
    
    public Upgrades clone() {
        return (Upgrades)super.clone();
    }
}
