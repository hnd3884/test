package com.me.mdm.server.windows.profile.payload.content.wifi.enums;

public enum WlanEncryption
{
    NONE("none"), 
    WEP("WEP"), 
    TKIP("TKIP"), 
    AES("AES");
    
    private String value;
    
    private WlanEncryption(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
