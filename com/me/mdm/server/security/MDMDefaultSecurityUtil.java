package com.me.mdm.server.security;

public class MDMDefaultSecurityUtil extends MDMBaseSecurityUtil
{
    @Override
    public void notifyAgents(final Long customerId, final String commandUUID) {
    }
    
    @Override
    public void toggleCheckSumValidation(final Long customerId, final boolean enable) {
        new MDMIOSSecurityUtil().toggleCheckSumValidation(customerId, enable);
        new MDMAndroidSecurityUtil().toggleCheckSumValidation(customerId, enable);
    }
    
    public void notifyAgentsForCheckSumSettings(final Long customerId) {
        new MDMIOSSecurityUtil().notifyAgentsForCheckSumSettings(customerId);
        new MDMAndroidSecurityUtil().notifyAgentsForCheckSumSettings(customerId);
    }
}
