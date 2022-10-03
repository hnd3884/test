package com.me.mdm.chrome.agent.Appmgmt;

import java.io.IOException;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.ChromeApplicationsSettings;
import com.google.chromedevicemanagement.v1.model.ChromeApp;
import java.util.List;
import java.util.ArrayList;
import com.google.chromedevicemanagement.v1.model.AndroidApplicationsSettings;
import com.google.chromedevicemanagement.v1.model.AndroidApp;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.me.mdm.chrome.agent.Context;
import org.json.JSONException;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;

public class AppMgmtRequestHandler extends ProcessRequestHandler
{
    private static final String INSTALL_APP_DATA = "InstallAppData";
    private static final String REMOVE_APP_DATA = "RemoveAppData";
    private static final String APP_VERSION = "AppVersion";
    private static final String PACKAGE_NAME = "PackageName";
    private static final String COLLECTION_ID = "CollectionID";
    private static final String APP_URL = "AppUrl";
    private static final String PLATFORM_TYPE = "PlatformType";
    private static final String PACKAGE_TYPE = "PackageType";
    private static final String APP_NAME = "AppName";
    private static final String APP_PINNING = "PinApp";
    
    @Override
    public void processRequest(final Request request, final Response response) {
        final String requestCommand = request.requestType;
        final Context context = request.getContainer().getContext();
        if (requestCommand.equalsIgnoreCase("InstallApplication")) {
            try {
                final JSONObject policyDetails = (JSONObject)request.requestData;
                this.logger.log(Level.INFO, "Payload Data : {0}", policyDetails);
                new MDMAgentParamsTableHandler(context).addJSONObject("InstallAppData", policyDetails);
                this.installApplication(context, request, response);
            }
            catch (final JSONException ex) {
                Logger.getLogger(AppMgmtRequestHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                response.setErrorCode(12060);
            }
        }
        else if (requestCommand.equalsIgnoreCase("RemoveApplication")) {
            try {
                final JSONObject policyDetails = (JSONObject)request.requestData;
                this.logger.log(Level.INFO, "Remove App: {0}", policyDetails);
                new MDMAgentParamsTableHandler(context).addJSONObject("RemoveAppData", policyDetails);
                this.removeApplication(context, request, response);
            }
            catch (final JSONException ex) {
                Logger.getLogger(AppMgmtRequestHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                response.setErrorCode(12060);
            }
        }
    }
    
    private void installApplication(final Context context, final Request request, final Response response) throws JSONException {
        final JSONObject installAppdetails = new MDMAgentParamsTableHandler(context).getJSONObject("InstallAppData");
        try {
            if (installAppdetails == null) {
                this.logger.log(Level.INFO, "No Apps to install");
                return;
            }
            final String appVersion = installAppdetails.optString("AppVersion", (String)null);
            final String packageName = installAppdetails.optString("PackageName", (String)null);
            final String collectionId = installAppdetails.optString("CollectionID", (String)null);
            final String appURL = installAppdetails.optString("AppUrl", (String)null);
            final String appName = installAppdetails.optString("AppName", (String)null);
            final int platformType = installAppdetails.optInt("PlatformType", 4);
            final int packageType = installAppdetails.optInt("PackageType", 0);
            final boolean isPinned = installAppdetails.optBoolean("PinApp", false);
            final UserPolicy userPolicy = new UserPolicy();
            if (platformType == 2) {
                final AndroidApp androidApp = new AndroidApp();
                AndroidApplicationsSettings androidApplicationsSettings = ((UserPolicy)context.getCMPAService().enterprises().users().getUserPolicy(context.getCMPAEnterpriseAndUDID()).execute()).getAndroidApplicationsSettings();
                if (androidApplicationsSettings == null) {
                    androidApplicationsSettings = new AndroidApplicationsSettings();
                }
                List<AndroidApp> androidApps = new ArrayList<AndroidApp>();
                if (androidApplicationsSettings.getApps() != null) {
                    androidApps = androidApplicationsSettings.getApps();
                    this.logger.log(Level.INFO, "Installed Apps {0}", androidApps);
                }
                androidApp.setAppId(packageName);
                androidApp.setInstalled(Boolean.valueOf(true));
                androidApp.setPinned(Boolean.valueOf(isPinned));
                androidApps.add(androidApp);
                androidApplicationsSettings.setApps((List)androidApps);
                userPolicy.setAndroidApplicationsSettings(androidApplicationsSettings);
            }
            else if (platformType == 4) {
                final ChromeApp chromeApp = new ChromeApp();
                ChromeApplicationsSettings chromeApplicationSettings = ((UserPolicy)context.getCMPAService().enterprises().users().getUserPolicy(context.getCMPAEnterpriseAndUDID()).execute()).getChromeApplicationsSettings();
                if (chromeApplicationSettings == null) {
                    chromeApplicationSettings = new ChromeApplicationsSettings();
                }
                List<ChromeApp> chromeApps = new ArrayList<ChromeApp>();
                if (chromeApplicationSettings.getApps() != null) {
                    chromeApps = chromeApplicationSettings.getApps();
                    this.logger.log(Level.INFO, "Installed Chrome Apps {0}", chromeApps);
                }
                chromeApp.setAppId(packageName);
                if (packageType == 2) {
                    chromeApp.setUrl(appURL);
                }
                chromeApp.setInstalled(Boolean.valueOf(true));
                chromeApp.setPinned(Boolean.valueOf(isPinned));
                chromeApps.add(chromeApp);
                chromeApplicationSettings.setApps((List)chromeApps);
                userPolicy.setChromeApplicationsSettings(chromeApplicationSettings);
            }
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while installing Apps", ex);
            response.setErrorCode(12110);
            try {
                response.setErrorMessage(ex.getMessage());
            }
            catch (final JSONException ex2) {
                Logger.getLogger(AppMgmtRequestHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
    }
    
    private void removeApplication(final Context context, final Request request, final Response response) throws JSONException {
        final JSONObject removeAppDetails = new MDMAgentParamsTableHandler(context).getJSONObject("RemoveAppData");
        try {
            if (removeAppDetails == null) {
                this.logger.log(Level.INFO, "No Apps to install");
                return;
            }
            final UserPolicy userPolicy = new UserPolicy();
            final String packageName = removeAppDetails.optString("PackageName", (String)null);
            final int platformType = removeAppDetails.optInt("PlatformType", 4);
            if (platformType == 2) {
                final AndroidApp androidApp = new AndroidApp();
                AndroidApplicationsSettings androidApplicationsSettings = ((UserPolicy)context.getCMPAService().enterprises().users().getUserPolicy(context.getCMPAEnterpriseAndUDID()).execute()).getAndroidApplicationsSettings();
                if (androidApplicationsSettings == null) {
                    androidApplicationsSettings = new AndroidApplicationsSettings();
                }
                final List<AndroidApp> androidApps = new ArrayList<AndroidApp>();
                androidApp.setAppId(packageName);
                androidApp.setInstalled(Boolean.valueOf(false));
                androidApps.add(androidApp);
                androidApplicationsSettings.setApps((List)androidApps);
                userPolicy.setAndroidApplicationsSettings(androidApplicationsSettings);
            }
            else if (platformType == 4) {
                final ChromeApp chromeApp = new ChromeApp();
                ChromeApplicationsSettings chromeApplicationSettings = ((UserPolicy)context.getCMPAService().enterprises().users().getUserPolicy(context.getCMPAEnterpriseAndUDID()).execute()).getChromeApplicationsSettings();
                if (chromeApplicationSettings == null) {
                    chromeApplicationSettings = new ChromeApplicationsSettings();
                }
                final List<ChromeApp> chromeApps = new ArrayList<ChromeApp>();
                chromeApp.setAppId(packageName);
                chromeApp.setInstalled(Boolean.valueOf(false));
                chromeApps.add(chromeApp);
                chromeApplicationSettings.setApps((List)chromeApps);
                userPolicy.setChromeApplicationsSettings(chromeApplicationSettings);
            }
            GoogleChromeAPIWrapper.updateUserPolicy(userPolicy, context);
        }
        catch (final IOException ex) {
            this.logger.log(Level.SEVERE, "Exception while removing App updates", ex);
        }
    }
}
