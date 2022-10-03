package com.zoho.clustering.failover;

public enum ErrorCode
{
    ERROR_GENERAL(11), 
    ERROR_INTERRUPT(12), 
    ERROR_IP_CLASH(101), 
    ERROR_IP_BINDING(102), 
    ERROR_IP_UNBINDING(103), 
    ERROR_IF_DOWN(104);
    
    public final int intValue;
    
    private ErrorCode(final int intValue) {
        this.intValue = intValue;
    }
}
