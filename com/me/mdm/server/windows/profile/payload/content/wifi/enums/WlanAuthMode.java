package com.me.mdm.server.windows.profile.payload.content.wifi.enums;

public enum WlanAuthMode
{
    USER("user"), 
    MACHINE("machine");
    
    private String value;
    
    private WlanAuthMode(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
