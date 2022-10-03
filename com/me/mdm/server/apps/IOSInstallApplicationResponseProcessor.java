package com.me.mdm.server.apps;

import java.util.Hashtable;
import org.json.JSONException;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.Properties;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.vpp.VPPManagedUserHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppLicenseHandler;
import com.adventnet.sym.server.mdm.apps.ManagedAppDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSInstallApplicationResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static Logger logger;
    private static final List<String> NETWORK_ERRORS;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final JSONObject response = new JSONObject();
        try {
            final String strData = params.optString("strData");
            final String strCommandUuid = params.optString("strCommandUuid");
            final Long resourceID = params.optLong("resourceId");
            final Long customerId = params.optLong("customerId");
            String errorRemarks = "";
            final HashMap hashPlist = PlistWrapper.getInstance().getHashFromPlist(strData);
            Integer errorCode = null;
            final String installAppState = hashPlist.get("State");
            final String installAppStatus = hashPlist.get("Status");
            IOSInstallApplicationResponseProcessor.logger.log(Level.INFO, "installAppState: {0}", installAppState);
            int installationStatus = 0;
            int collectionStatus = 7;
            boolean licenseUpdate = false;
            if (installAppStatus != null && !installAppStatus.isEmpty() && installAppStatus.equals("Acknowledged")) {
                errorRemarks = "dc.db.mdm.apps.status.Installing";
                installationStatus = 1;
                collectionStatus = 3;
                licenseUpdate = true;
            }
            else if (installAppStatus != null && !installAppStatus.isEmpty() && installAppStatus.equalsIgnoreCase("Error")) {
                final IOSErrorStatusHandler errorHandler = new IOSErrorStatusHandler();
                final List<String> errorDomainList = new ArrayList<String>();
                errorDomainList.add("MCMDMErrorDomain");
                final List errorCodeList = new ArrayList();
                final JSONObject error = errorHandler.getIOSErrors(errorDomainList, errorCodeList, strData);
                errorRemarks = error.optString("LocalizedRemarks");
                errorCode = error.optInt("ErrorCode");
                final String errorDomain = error.optString("ErrorDomain");
                IOSInstallApplicationResponseProcessor.logger.log(Level.INFO, "IOSInstallApplicationResponseProcessor: Resource id: {0}, Error Remarks: {1}, Error Code: {2}, Error Domain: {3}", new Object[] { resourceID, errorRemarks, errorCode, errorDomain });
                final String rejectionReason = hashPlist.get("RejectionReason");
                if (rejectionReason != null && rejectionReason.equalsIgnoreCase("AppAlreadyQueued")) {
                    errorRemarks = "dc.db.mdm.apps.status.already_scheduled";
                    installationStatus = 1;
                    collectionStatus = 3;
                }
                else if (rejectionReason != null && rejectionReason.equalsIgnoreCase("AppAlreadyInstalled")) {
                    installationStatus = 5;
                    errorRemarks = "dc.db.mdm.collection.App_already_installed_ios";
                    collectionStatus = 12;
                }
                else if (rejectionReason != null && rejectionReason.equalsIgnoreCase("PromptingForManagement")) {
                    installationStatus = 1;
                    collectionStatus = 3;
                    errorRemarks = "mdm.apps.status.PromptingManagement";
                }
                else if (rejectionReason != null && rejectionReason.equalsIgnoreCase("NotAnApp")) {
                    installationStatus = 0;
                    collectionStatus = 7;
                    errorRemarks = "mdm.apps.status.NotAApp";
                }
                else if (rejectionReason != null && (rejectionReason.equalsIgnoreCase("CouldNotVerifyAppID") || rejectionReason.equalsIgnoreCase("LicenseNotFound"))) {
                    installationStatus = 0;
                    collectionStatus = 7;
                    final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                    final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
                    if (errorRemarks != null && (errorRemarks.equalsIgnoreCase("The iTunes Store ID of the application could not be validated.") || ((errorRemarks.toLowerCase().contains("license") || errorRemarks.toLowerCase().contains("licence")) && (errorRemarks.toLowerCase().contains("for the app with itunes store id") || errorRemarks.toLowerCase().contains("for app with itunes store id"))))) {
                        errorRemarks = "dc.mdm.vpp.app_download_failed";
                        final Boolean isVPPApp = new ManagedAppDataHandler().isAppPurchasedFromPortal(appGroupId);
                        errorCode = 9006;
                        if (isVPPApp) {
                            final Properties appLicenseDetails = new VPPAppLicenseHandler().getVPPAppLicenseDetailsForDevice(appGroupId, resourceID);
                            final Boolean isUserBasedAssignment = ((Hashtable<K, Boolean>)appLicenseDetails).getOrDefault(1, Boolean.FALSE);
                            final Boolean isDeviceBasedAssignment = ((Hashtable<K, Boolean>)appLicenseDetails).getOrDefault(2, Boolean.FALSE);
                            if (isUserBasedAssignment) {
                                final Boolean isUserNotAssociated = VPPManagedUserHandler.getInstance().checkIfUserNotAssociatedForVppApp(resourceID, appGroupId);
                                if (isUserNotAssociated) {
                                    errorCode = 9007;
                                    errorRemarks = "dc.mdm.vpp.user_not_accepted_vppinvitation";
                                }
                            }
                            else if (!isDeviceBasedAssignment) {
                                final String helpUrl = "/help/app_management/ios_app_management.html?$(traceurl)&pgSrc=$(pageSource)#Purchasing_apps_in_vpp";
                                final String errorMessage = "dc.mdm.vpp.app_with_no_license@@@<a target='blank' href=\"$(mdmUrl)" + helpUrl + "\">@@@</a>";
                                MDMUtil.getInstance();
                                errorRemarks = MDMUtil.replaceProductUrlLoaderValuesinText(errorMessage, "afterInstallApp");
                                errorCode = null;
                            }
                        }
                    }
                }
                else if (rejectionReason != null && rejectionReason.equalsIgnoreCase("AppStoreDisabled")) {
                    installationStatus = 0;
                    collectionStatus = 12;
                }
                else if (rejectionReason != null && rejectionReason.equalsIgnoreCase("UserRejected")) {
                    installationStatus = 0;
                    collectionStatus = 12;
                    errorRemarks = MDMUtil.replaceProductUrlLoaderValuesinText(I18N.getMsg("dc.db.mdm.apps.status.UserRejected", new Object[0]), "UserRejected");
                }
                else if (errorRemarks != null && errorRemarks.toLowerCase().contains("log in") && errorRemarks.toLowerCase().contains("itunes")) {
                    errorRemarks = "dc.db.mdm.apps.status.AppStoreLoginNeeded";
                }
                else if (errorRemarks != null && errorRemarks.equalsIgnoreCase("Cannot connect to iTunes Store")) {
                    installationStatus = 0;
                    collectionStatus = 7;
                    errorRemarks = "dc.mdm.app.app_store_not_reachable_in_device";
                    errorCode = 52006;
                }
                else if (errorDomain != null && IOSInstallApplicationResponseProcessor.NETWORK_ERRORS.contains(errorDomain)) {
                    installationStatus = 0;
                    collectionStatus = 7;
                    errorRemarks = "mdm.apps.internet.connectivity.issue";
                    errorCode = 64041;
                }
                else if (errorRemarks != null && errorRemarks.equalsIgnoreCase("Internal Error")) {
                    installationStatus = 0;
                    collectionStatus = 7;
                    errorRemarks = "mdm.apps.internal.error";
                    errorCode = 64042;
                }
                else if (rejectionReason != null && rejectionReason.equalsIgnoreCase("NotSupported")) {
                    installationStatus = 0;
                    collectionStatus = 7;
                    errorRemarks = "mdm.apps.status.NotSupported";
                    errorCode = 12999;
                }
                else {
                    collectionStatus = 7;
                    installationStatus = 0;
                }
                if (strCommandUuid.contains("ManageApplication") && installationStatus != 1) {
                    installationStatus = 6;
                }
            }
            else {
                IOSInstallApplicationResponseProcessor.logger.log(Level.INFO, "Received Status Data : {0}", strData);
            }
            response.put("remarks", (Object)errorRemarks);
            response.put("ErrorCode", (Object)errorCode);
            response.put("installationStatus", installationStatus);
            response.put("collectionStatus", collectionStatus);
            response.put("isLicenseUpdate", licenseUpdate);
        }
        catch (final Exception e) {
            IOSInstallApplicationResponseProcessor.logger.log(Level.SEVERE, "Exception in process iOS install application", e);
        }
        return response;
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        JSONObject appProcessor = null;
        final String commandUUID = params.optString("strCommandUuid");
        final Long resourceID = params.optLong("resourceId");
        try {
            final String strStatus = params.optString("strStatus");
            if (strStatus.equalsIgnoreCase("NotNow")) {
                this.removeNotNow(params);
            }
            else {
                final JSONObject response = new JSONObject();
                final JSONObject seqParams = new JSONObject();
                appProcessor = this.processQueuedCommand(params);
                final int status = appProcessor.optInt("installationStatus");
                if (status == 2 || status == 5) {
                    response.put("action", 1);
                    seqParams.put("status", 6);
                }
                else if (status == 1) {
                    response.put("action", 4);
                    seqParams.put("timeout", 10000);
                    seqParams.put("retryCount", 5);
                }
                else {
                    response.put("action", 2);
                    String remarks = appProcessor.optString("remarks");
                    int errorCode = appProcessor.optInt("ErrorCode");
                    if (errorCode == 21007) {
                        remarks = "dc.mdm.kiosk.conflicting.kiosk.payload";
                        errorCode = 48000;
                    }
                    seqParams.put("Remarks", (Object)remarks);
                    seqParams.put("ErrorCode", errorCode);
                }
                seqParams.put("isNeedToRemove", false);
                response.put("commandUUID", (Object)commandUUID);
                response.put("resourceID", (Object)resourceID);
                response.put("params", (Object)seqParams);
                response.put("isNotify", params.optBoolean("isNotify", (boolean)Boolean.FALSE));
                SeqCmdRepository.getInstance().processSeqCommand(response);
                appProcessor.put("isNeedToAddQueue", true);
            }
        }
        catch (final Exception e) {
            IOSInstallApplicationResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception While processing the immediate install application for resource" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return appProcessor;
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        params.put("isNotify", (Object)Boolean.TRUE);
        return this.processImmediateSeqCommand(params);
    }
    
    private void removeNotNow(final JSONObject params) throws JSONException {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject response = new JSONObject();
        final JSONObject seqParams = new JSONObject();
        seqParams.put("Remarks", (Object)"dc.mdm.kiosk.conflicting.kiosk.payload");
        seqParams.put("ErrorCode", 3002);
        response.put("action", 2);
        response.put("commandUUID", (Object)commandUUID);
        response.put("resourceID", (Object)resourceID);
        response.put("params", (Object)seqParams);
        SeqCmdRepository.getInstance().processSeqCommand(response);
    }
    
    static {
        IOSInstallApplicationResponseProcessor.logger = Logger.getLogger("MDMCommandsLogger");
        (NETWORK_ERRORS = new ArrayList<String>()).add("NSURLErrorDomain");
        IOSInstallApplicationResponseProcessor.NETWORK_ERRORS.add("kCFErrorDomainCFNetwork");
    }
}
