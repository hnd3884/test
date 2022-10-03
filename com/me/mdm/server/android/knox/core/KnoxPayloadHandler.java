package com.me.mdm.server.android.knox.core;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.android.knox.enroll.KnoxActivationManager;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import com.me.mdm.server.android.knox.KnoxUtil;
import org.json.JSONObject;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import java.util.logging.Logger;

public class KnoxPayloadHandler
{
    private static KnoxPayloadHandler pHandler;
    private Logger logger;
    
    public KnoxPayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static KnoxPayloadHandler getInstance() {
        if (KnoxPayloadHandler.pHandler == null) {
            KnoxPayloadHandler.pHandler = new KnoxPayloadHandler();
        }
        return KnoxPayloadHandler.pHandler;
    }
    
    public AndroidCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final AndroidCommandPayload commandPayload = new AndroidCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        commandPayload.setScope(1);
        return commandPayload;
    }
    
    public AndroidCommandPayload createCreateContainerPayload(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("CreateContainer");
        final JSONObject reqData = new JSONObject();
        reqData.put("KnoxLicenseKey", (Object)KnoxUtil.getInstance().getLicense(resourceId));
        commandPayload.setRequestData(reqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "CreateContainer");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "CreateContainer", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createRemoveContainerPayload() throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoveContainer");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "RemoveContainer");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveContainer", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createLockContainerPayload() throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ContainerLock");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ContainerLock");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ContainerLock", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createUnlockContainerPayload() throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ContainerUnlock");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ContainerUnlock");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ContainerUnlock", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createClearContainerPasswordPayload() throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ClearContainerPasscode");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ClearContainerPasscode");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ClearContainerPasscode", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createApplyLicensePayload(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ActivateKnoxLicense");
        final JSONObject reqData = new JSONObject();
        reqData.put("KnoxLicenseKey", (Object)KnoxUtil.getInstance().getLicense(resourceId));
        commandPayload.setRequestData(reqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ActivateKnoxLicense");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ActivateKnoxLicense", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createRevokeLicensePayload(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DeactivateKnoxLicense");
        final JSONObject reqData = new JSONObject();
        final Long licenseId = KnoxUtil.getInstance().getAssignedLicense(resourceId);
        String license = "";
        if (licenseId != null) {
            license = (String)DBUtil.getValueFromDB("KnoxLicenseDetail", "LICENSE_ID", (Object)licenseId, "LICENSE_DATA");
        }
        reqData.put("KnoxLicenseKey", (Object)license);
        commandPayload.setRequestData(reqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "DeactivateKnoxLicense");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeactivateKnoxLicense", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createGetKnoxAvailabilityCommand(final String source) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("GetKnoxAvailability");
        commandPayload.setScope(0);
        final JSONObject reqData = new JSONObject();
        reqData.put("Source", (Object)source);
        commandPayload.setRequestData(reqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "GetKnoxAvailabilityEnrollment");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "GetKnoxAvailabilityEnrollment", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createAppMigrationCommand(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("MigrateAppToContainer");
        final JSONObject reqData = new JSONObject();
        final Long appId = null;
        final SelectQuery appListQuery = AppsUtil.getInstance().getQueryforResourceAppDetails(resourceId, appId, 2, "", 2, 2);
        final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(resourceId);
        reqData.put("AppsToMigrate", (Object)AppsUtil.getInstance().getAppCatalogListJSON(appListQuery, resourceId, appId, platformType));
        commandPayload.setRequestData(reqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "MigrateAppToContainer");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "MigrateAppToContainer", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createActivateKnoxCommand(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ActivateKnox");
        final JSONObject licenseReqData = new JSONObject();
        final JSONObject containerReqData = new JSONObject();
        final JSONObject knoxReqData = new JSONObject();
        licenseReqData.put("KnoxLicenseKey", (Object)KnoxUtil.getInstance().getLicense(resourceId));
        licenseReqData.put("ApplyLicense", true);
        knoxReqData.accumulate("KnoxLicense", (Object)licenseReqData);
        containerReqData.put("CreateContainer", true);
        containerReqData.put("OverridePersonalContainer", (Object)KnoxActivationManager.getInstance().getOverrideContainerFromDS(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId)));
        knoxReqData.accumulate("KnoxContainer", (Object)containerReqData);
        commandPayload.setRequestData(knoxReqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ActivateKnoxLicense");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ActivateKnoxLicense", commandPayload.toString() });
        return commandPayload;
    }
    
    public AndroidCommandPayload createDeactivateKnoxCommand(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DeactivateKnox");
        final JSONObject licenseReqData = new JSONObject();
        final JSONObject containerReqData = new JSONObject();
        final JSONObject knoxReqData = new JSONObject();
        licenseReqData.put("KnoxLicenseKey", (Object)KnoxUtil.getInstance().getLicense(resourceId));
        licenseReqData.put("RevokeLicense", true);
        knoxReqData.accumulate("KnoxLicense", (Object)licenseReqData);
        containerReqData.put("RemoveContainer", true);
        knoxReqData.accumulate("KnoxContainer", (Object)containerReqData);
        commandPayload.setRequestData(knoxReqData);
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ActivateKnoxLicense");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ActivateKnoxLicense", commandPayload.toString() });
        return commandPayload;
    }
    
    static {
        KnoxPayloadHandler.pHandler = null;
    }
}
