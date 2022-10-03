package com.me.mdm.server.security;

import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;

public abstract class MDMBaseSecurityUtil
{
    protected Logger logger;
    
    public MDMBaseSecurityUtil() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static MDMBaseSecurityUtil getInstance() {
        return new MDMDefaultSecurityUtil();
    }
    
    public static MDMBaseSecurityUtil getInstance(final int platform) {
        switch (platform) {
            case 2: {
                return new MDMAndroidSecurityUtil();
            }
            default: {
                return new MDMDefaultSecurityUtil();
            }
        }
    }
    
    public void calculateCmFileChecksum(final Long customerId) throws Exception {
        DocMgmt.getInstance().calculateCheckSumForFiles(customerId);
    }
    
    public void calculateCheckSumForApps(final Long customerId) throws Exception {
        MDMRestAPIFactoryProvider.getAppsUtilAPI().calculateCheckSumForAppFiles(customerId);
    }
    
    public abstract void notifyAgents(final Long p0, final String p1);
    
    public abstract void toggleCheckSumValidation(final Long p0, final boolean p1);
}
