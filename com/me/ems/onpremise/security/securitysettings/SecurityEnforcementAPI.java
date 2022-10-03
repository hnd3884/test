package com.me.ems.onpremise.security.securitysettings;

public interface SecurityEnforcementAPI
{
    void initiateEnforcementPeriod() throws Exception;
    
    void autoEnforcement() throws Exception;
    
    void manualEnforcement() throws Exception;
}
