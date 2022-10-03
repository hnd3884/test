package com.me.mdm.server.apps.businessstore.ios;

import org.json.JSONArray;
import java.util.List;
import com.me.mdm.server.apps.ios.vpp.VPPLicenseSyncHandler;
import com.me.mdm.server.apps.ios.vpp.VPPAssetsAPIHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class VPPLicenseHandlingProcessor extends DCQueueDataProcessor
{
    Logger vppAppMgmtLogger;
    
    public VPPLicenseHandlingProcessor() {
        this.vppAppMgmtLogger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public void processData(final DCQueueData dcQueueData) {
        final Long startTime = MDMUtil.getCurrentTimeInMillis();
        final String separator = "\t";
        this.vppAppMgmtLogger.log(Level.INFO, "License Handling Task started {0}{1}{2} Time spent waiting in queue - {3}", new Object[] { separator, dcQueueData.fileName, separator, startTime - dcQueueData.postTime });
        final JSONObject messageJSON = new JSONObject(dcQueueData.queueData.toString());
        final Long customerID = dcQueueData.customerID;
        final String msgType = messageJSON.getString("MsgType");
        final VPPAppMgmtHandler vppAppMgmtHandler = new VPPAppMgmtHandler();
        final String s = msgType;
        switch (s) {
            case "VppAppLicenseRemovalForDevice": {
                this.removeVppAppsForDevicesFromConfigSourceBS(messageJSON, customerID);
                break;
            }
            case "VppAppInsufficientLicenseHandling": {
                vppAppMgmtHandler.assignLicensesForFailedDevices(messageJSON, customerID);
                break;
            }
            case "VppAppImproperLicenseRemoval": {
                vppAppMgmtHandler.removeImproperLicenseForApps(messageJSON, customerID);
                break;
            }
            case "VppSingleAppLicenseSync": {
                this.startVPPSingleAppLicenseSync(messageJSON, customerID);
                break;
            }
            default: {
                this.vppAppMgmtLogger.log(Level.INFO, "Unsupported message Type in VPP License Handling");
                break;
            }
        }
        this.vppAppMgmtLogger.log(Level.INFO, "License Handling ended{0}{1}{2} Time spent waiting in queue - {3}", new Object[] { separator, dcQueueData.fileName, separator, MDMUtil.getCurrentTimeInMillis() - startTime });
    }
    
    private void startVPPSingleAppLicenseSync(final JSONObject jsonObject, final Long customerID) {
        try {
            final Long appGroupID = jsonObject.getLong("APP_GROUP_ID");
            final String storeID = jsonObject.getString("ADAM_ID");
            final Long userID = jsonObject.getLong("USER_ID");
            final Long businessStoreID = jsonObject.getLong("BUSINESSSTORE_ID");
            new VPPAssetsAPIHandler(businessStoreID, customerID).syncVPPAssets(Boolean.FALSE);
            this.vppAppMgmtLogger.log(Level.INFO, "Queue Processing started for VPP Single App License Sync for App: {0} in BusinessStore: {1}", new Object[] { appGroupID, businessStoreID });
            final VPPLicenseSyncHandler syncHandler = new VPPLicenseSyncHandler(customerID, userID, appGroupID, storeID, businessStoreID, Boolean.FALSE, Boolean.FALSE);
            syncHandler.syncVppLicenses();
            this.vppAppMgmtLogger.log(Level.INFO, "Queue Processing ended for VPP Single App License Sync for App: {0} in BusinessStore: {1}", new Object[] { appGroupID, businessStoreID });
        }
        catch (final Exception e) {
            this.vppAppMgmtLogger.log(Level.SEVERE, "Exception in startVPPSingleAppLicenseSync", e);
        }
    }
    
    private void removeVppAppsForDevicesFromConfigSourceBS(final JSONObject jsonObject, final Long customerID) {
        try {
            final JSONObject appToDeviceLicenseDetails = jsonObject.optJSONObject("appToDeviceLicenseDetails");
            final JSONArray configSourceArray = jsonObject.optJSONArray("configSourceList");
            final Boolean isAllSourceDisassociation = jsonObject.optBoolean("isAllSourceDisassociation", false);
            List configSourceList = null;
            if (configSourceArray != null && configSourceArray.length() > 0) {
                configSourceList = configSourceArray.toList();
            }
            new IOSStoreHandler(null, customerID).removeAppLicensesForResources(appToDeviceLicenseDetails, configSourceList, customerID, isAllSourceDisassociation);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in removeAppsLicensesForDevices", e);
        }
    }
}
