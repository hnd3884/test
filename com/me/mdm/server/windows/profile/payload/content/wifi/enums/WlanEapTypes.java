package com.me.mdm.server.windows.profile.payload.content.wifi.enums;

public enum WlanEapTypes
{
    EAP_TLS("13"), 
    EAP_SIM("18"), 
    EAP_TTLS("21"), 
    EAP_AKA("23"), 
    EAP_PEAP("25"), 
    EAP_MSCHAPv2("26"), 
    EAP_AKA_PRIME("50");
    
    private String value;
    
    private WlanEapTypes(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
