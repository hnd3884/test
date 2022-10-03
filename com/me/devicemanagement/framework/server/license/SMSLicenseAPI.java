package com.me.devicemanagement.framework.server.license;

public interface SMSLicenseAPI
{
    int getSMSCredits();
    
    int getPurchasedSMSCredits();
    
    int getComplimentarySMSCredits();
}
