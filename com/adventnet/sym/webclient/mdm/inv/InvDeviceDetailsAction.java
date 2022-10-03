package com.adventnet.sym.webclient.mdm.inv;

import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import org.json.JSONObject;
import java.util.logging.Logger;

public class InvDeviceDetailsAction
{
    public Logger out;
    
    public InvDeviceDetailsAction() {
        this.out = Logger.getLogger(InvDeviceDetailsAction.class.getName());
    }
    
    private String getTrimmedOSForMac(String version) {
        version = version.replaceFirst("\\.", "");
        if (!version.contains(".")) {
            version += ".0";
        }
        return version;
    }
    
    public void trackRemoteSessionData(final JSONObject preConditionsToValidate, final Long customerId) throws Exception {
        MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Remote_Module", "iosRemoteClickCount");
        final Boolean isScreenRestricted = (Boolean)preConditionsToValidate.get("isIOSScreenCaptureRestricted");
        if (isScreenRestricted) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Remote_Module", "iOSScreenCaptureRestricted");
        }
        else if (!(boolean)preConditionsToValidate.get("isIOS11AndAbove")) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Remote_Module", "iOS11AndBelow");
        }
        else if (!(boolean)preConditionsToValidate.get("isAgentRemoteCompatible")) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Remote_Module", "iOSAgentNotCompatible");
        }
        else if (!(boolean)preConditionsToValidate.get("isAgentInstalled")) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Remote_Module", "iOSAgentNotInstalled");
        }
        else if (!(boolean)preConditionsToValidate.get("isAgentAddedToPackage")) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Remote_Module", "iOSAgentNotAddedToPackage");
        }
    }
}
