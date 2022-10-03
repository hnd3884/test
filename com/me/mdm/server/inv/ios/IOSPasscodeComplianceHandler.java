package com.me.mdm.server.inv.ios;

import com.me.mdm.server.profiles.ios.IOSPasscodeRestrictionHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSPasscodeComplianceHandler
{
    private static final Logger LOGGER;
    
    public void checkDevicePasscodeCompliance(final Long resourceId) {
        try {
            IOSPasscodeComplianceHandler.LOGGER.log(Level.INFO, "Inside passcode compliance handler for resource:{0}", new Object[] { resourceId });
            JSONObject details = new JSONObject();
            details = InventoryUtil.getInstance().getSecurityInfo(resourceId, details);
            final JSONObject securityDetails = details.optJSONObject("security");
            final boolean isPasscodeComplaintWithProfile = securityDetails.optBoolean("PASSCODE_COMPLAINT_PROFILES", false);
            if (isPasscodeComplaintWithProfile) {
                IOSPasscodeComplianceHandler.LOGGER.log(Level.INFO, "Resource:{0} is passcode profile compliance", new Object[] { resourceId });
                this.handledPasscodeComplianceWithProfile(resourceId);
            }
        }
        catch (final SyMException e) {
            IOSPasscodeComplianceHandler.LOGGER.log(Level.SEVERE, "Exception in ios passcode compliance handler", (Throwable)e);
        }
    }
    
    protected void handledPasscodeComplianceWithProfile(final Long resourceId) {
        new IOSPasscodeRestrictionHandler().handlePasscodeComplianceForDevice(resourceId);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
