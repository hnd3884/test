package com.me.mdm.server.windows.profile.payload.content.wifi.enums;

public enum WlanConnectionMode
{
    AUTO("auto"), 
    MANUAL("manual");
    
    private String value;
    
    private WlanConnectionMode(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
