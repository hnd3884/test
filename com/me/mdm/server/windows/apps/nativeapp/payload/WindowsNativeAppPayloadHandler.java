package com.me.mdm.server.windows.apps.nativeapp.payload;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import org.json.JSONObject;
import com.me.mdm.server.settings.location.LocationSettingsRequestHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppCommandPayload;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.logging.Logger;

public class WindowsNativeAppPayloadHandler
{
    private Logger logger;
    
    public WindowsNativeAppPayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public IOSNativeAppCommandPayload createSyncAgentSettingsCommand(final DeviceDetails device, final String strUDID) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("SyncAgentSettings");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "SyncAgentSettings");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SyncAgentSettings", commandPayload.toString() });
        final JSONObject syncReqData = LocationSettingsRequestHandler.getInstance().getiOSLocationSettingPayloadJSON(device);
        final JSONObject contentMgmtObj = new JSONObject();
        final Boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
        if (isProfessional) {
            contentMgmtObj.put("isContentRepoEnabled", (Object)Boolean.TRUE);
        }
        syncReqData.put("ContentMgmtSettings", (Object)contentMgmtObj);
        commandPayload.setRequestData(syncReqData);
        commandPayload.setCommandUUID(strUDID);
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = new IOSNativeAppCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
}
