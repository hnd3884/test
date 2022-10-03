package com.me.mdm.server.windows.profile.payload.content.wifi.enums;

public enum WlanConnectionType
{
    ESS("ESS"), 
    IBSS("IBSS");
    
    private String value;
    
    private WlanConnectionType(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
