package com.me.mdm.server.adep;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppleDEPSyncScheduler
{
    public static Logger logger;
    
    public void executeTask() {
        try {
            DEPEnrollmentUtil.validateDEPTokenExpiry();
        }
        catch (final Exception e) {
            AppleDEPSyncScheduler.logger.log(Level.SEVERE, "Exception while validateDEPTokenExpiry", e);
        }
        try {
            if ("true".equals(MDMUtil.getSyMParameter("IS_ABM_PROFILE_UPDATE_REQ"))) {
                DEPEnrollmentUtil.createAndAssignAllDEPProfileAsynchronously();
                MDMUtil.deleteSyMParameter("IS_ABM_PROFILE_UPDATE_REQ");
            }
            else {
                DEPEnrollmentUtil.syncAllDepToken();
            }
        }
        catch (final Exception e) {
            AppleDEPSyncScheduler.logger.log(Level.SEVERE, "Exception while AppleDEPSyncScheduler", e);
        }
    }
    
    static {
        AppleDEPSyncScheduler.logger = Logger.getLogger("MDMEnrollment");
    }
}
