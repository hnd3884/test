package com.me.mdm.onpremise.server.enrollment;

import com.me.mdm.onpremise.server.time.ServerTimeValidationUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.adep.DepErrorsAPI;

public class DepErrorsOnPremImpl implements DepErrorsAPI
{
    private static final Logger LOGGER;
    
    public int getErrorCause(int errorCode) {
        DepErrorsOnPremImpl.LOGGER.log(Level.INFO, "DepErrorsOnPremImpl: DATA-IN: ABM error code: {0}", new Object[] { errorCode });
        if (errorCode == 1015) {
            errorCode = this.getOauthErrorCause(errorCode);
        }
        return errorCode;
    }
    
    public JSONObject getErrorRemarkArgs(final int errorCode) {
        JSONObject errorRemarkArgs = null;
        DepErrorsOnPremImpl.LOGGER.log(Level.INFO, "DepErrorsOnPremImpl: DATA-IN: ABM error code: {0}", new Object[] { errorCode });
        if (errorCode == 1019) {
            errorRemarkArgs = this.getServerTimeMismatchRemarks();
        }
        return errorRemarkArgs;
    }
    
    private int getOauthErrorCause(int errorCode) {
        final JSONObject timeDifferenceInfo = ServerTimeValidationUtil.getTimeDifferenceInfo();
        if (timeDifferenceInfo != null) {
            DepErrorsOnPremImpl.LOGGER.log(Level.INFO, "DepErrorsOnPremImpl: Is Sync needed: {0}", new Object[] { timeDifferenceInfo.opt("sync_needed") });
            if (timeDifferenceInfo.getBoolean("sync_needed") && !timeDifferenceInfo.get("difference_type").equals("equal")) {
                DepErrorsOnPremImpl.LOGGER.log(Level.INFO, "DepErrorsOnPremImpl: Difference type: {1}, Difference Value: {2}", new Object[] { timeDifferenceInfo.opt("difference_type"), timeDifferenceInfo.opt("difference_value") });
                errorCode = 1019;
            }
        }
        return errorCode;
    }
    
    private JSONObject getServerTimeMismatchRemarks() {
        JSONObject serverTimeMismatchRemarkArgs = null;
        final JSONObject timeDifferenceInfo = ServerTimeValidationUtil.getTimeDifferenceInfo();
        if (timeDifferenceInfo != null) {
            DepErrorsOnPremImpl.LOGGER.log(Level.INFO, "DepErrorsOnPremImpl: Is Sync needed: {0}", new Object[] { timeDifferenceInfo.opt("sync_needed") });
            if (timeDifferenceInfo.getBoolean("sync_needed") && !timeDifferenceInfo.get("difference_type").equals("equal")) {
                DepErrorsOnPremImpl.LOGGER.log(Level.INFO, "DepErrorsOnPremImpl: Difference type: {1}, Difference Value: {2}", new Object[] { timeDifferenceInfo.opt("difference_type"), timeDifferenceInfo.opt("difference_value") });
                serverTimeMismatchRemarkArgs = new JSONObject();
                serverTimeMismatchRemarkArgs.put("difference_type", timeDifferenceInfo.get("difference_type"));
                serverTimeMismatchRemarkArgs.put("difference_value", timeDifferenceInfo.get("difference_value"));
            }
        }
        return serverTimeMismatchRemarkArgs;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
