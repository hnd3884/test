package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

public class SilentUpdateDynamicChecker
{
    public boolean isShowAlertMsg(final String qppmUniqueId) {
        return true;
    }
    
    public boolean isQPPMApplicable(final String qppmUniqueId) {
        return true;
    }
    
    public String getAlertMsg(final String qppmUniqueId) {
        return null;
    }
}
