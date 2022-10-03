package com.me.mdm.server.windows.profile.payload.content.wifi.enums;

public enum WlanAuthentication
{
    OPEN("open"), 
    SHARED("shared"), 
    WPA_ENTERPRISE("WPA"), 
    WPA_PERSONAL("WPAPSK"), 
    WPA2_ENTERPRISE("WPA2"), 
    WPA2_PERSONAL("WPA2PSK");
    
    private String value;
    
    private WlanAuthentication(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
