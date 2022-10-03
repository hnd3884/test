package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class VirtualMachinesAllowed extends GenericJson
{
    @Key
    private Boolean virtualMachinesAllowed;
    
    public Boolean getVirtualMachinesAllowed() {
        return this.virtualMachinesAllowed;
    }
    
    public VirtualMachinesAllowed setVirtualMachinesAllowed(final Boolean virtualMachinesAllowed) {
        this.virtualMachinesAllowed = virtualMachinesAllowed;
        return this;
    }
    
    public VirtualMachinesAllowed set(final String s, final Object o) {
        return (VirtualMachinesAllowed)super.set(s, o);
    }
    
    public VirtualMachinesAllowed clone() {
        return (VirtualMachinesAllowed)super.clone();
    }
}
