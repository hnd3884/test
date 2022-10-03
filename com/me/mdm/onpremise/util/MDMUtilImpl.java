package com.me.mdm.onpremise.util;

import java.util.Hashtable;
import com.me.mdm.uem.LicenseActionListenerImpl;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.uem.actionconstants.LicenseAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import java.util.HashMap;
import org.json.JSONObject;
import java.io.IOException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;

public class MDMUtilImpl extends com.me.mdm.server.util.MDMUtilImpl
{
    public String getSelfEnrollURL(final String serverUrl) {
        return serverUrl + "/mdm/enroll";
    }
    
    public String getAgentAppDownloadBaseUrl() {
        String downloadURL = "";
        try {
            final Properties props = MDMUtil.getDCServerInfo();
            final int httpPort = WebServerUtil.getHttpPort();
            downloadURL = "http://" + props.getProperty("SERVER_MAC_IPADDR") + ":" + httpPort;
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMUtilImpl.class.getName()).log(Level.WARNING, "Exception occurred while getting Android Admin App download URL", ex);
        }
        Logger.getLogger(MDMUtilImpl.class.getName()).log(Level.INFO, "Android Admin App Download URL : {0}", downloadURL);
        return downloadURL;
    }
    
    public String getAuthTokenString(final String authtokenName, final boolean isInitialParam) {
        return "";
    }
    
    public String getHttpsServerBaseUrl() throws Exception {
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        final Properties serverProp = MDMUtil.getDCServerInfo();
        String serverIP = null;
        String httpsPort = null;
        if (natProps.size() > 0) {
            serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            httpsPort = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
        }
        else {
            serverIP = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_NAME");
            httpsPort = String.valueOf(((Hashtable<K, Object>)serverProp).get("HTTPS_PORT"));
        }
        final StringBuilder baseURLStr = new StringBuilder("https://" + serverIP + ":" + httpsPort);
        return baseURLStr.toString();
    }
    
    public String getHttpServerBaseUrl() throws Exception {
        final Properties serverProp = MDMUtil.getDCServerInfo();
        final String serverIP = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_IPADDR");
        final String serverPort = String.valueOf(((Hashtable<K, Object>)serverProp).get("SERVER_PORT"));
        final StringBuilder baseURLStr = new StringBuilder("http://" + serverIP + ":" + serverPort);
        return baseURLStr.toString();
    }
    
    public boolean useProxyForApns(final String version) {
        return Boolean.TRUE;
    }
    
    public Process exec(final String args) {
        try {
            return Runtime.getRuntime().exec(args);
        }
        catch (final IOException ex) {
            Logger.getLogger(MDMUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Process exec(final String[] args) {
        try {
            return Runtime.getRuntime().exec(args);
        }
        catch (final IOException ex) {
            Logger.getLogger(MDMUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String getWindowsAzureADDiscoverURL(final JSONObject params) throws Exception {
        return params.get("httpsServerBaseURL") + "/mdm/client/v2/wpdiscover/admin/" + params.getLong("customerID") + "?" + params.get("urlParams");
    }
    
    public String getExtractionFilePath(final String filePath) {
        return filePath.replaceAll("/", "\\\\");
    }
    
    public String replaceProductSpecificDynamicValues(final String data, final HashMap managedDeviceAndUserDetails) {
        return data;
    }
    
    public int getCurrentBuildNumber() {
        Integer buildNumber = null;
        try {
            buildNumber = Integer.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("CURRENT_BUILD_VERSION", 2)));
            return buildNumber;
        }
        catch (final Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "couldn't get mdm build number from cache.. going to take from DCServerBuildHistory ", ex);
            try {
                buildNumber = Integer.valueOf(String.valueOf(DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB()));
                ApiFactoryProvider.getCacheAccessAPI().putCache("CURRENT_BUILD_VERSION", (Object)buildNumber, 2);
                return buildNumber;
            }
            catch (final Exception ex2) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "couldn't get mdm build number from DCServerBuildHistory also", ex);
                throw ex;
            }
        }
    }
    
    public boolean isClearedDetailsForFreeEdition(final String mobileDeviceIds) {
        final Logger logger = Logger.getLogger(MDMUtilImpl.class.getName());
        try {
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final boolean mobileUpdated = MDMUtil.getInstance().updateFreeEditionDetails(mobileDeviceIds);
            final Long currentlyLoggedInUserLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            DMOnPremiseUserHandler.deleteDCUsersExceptCurrent(currentlyLoggedInUserLoginId);
            logger.log(Level.INFO, "Additional Technicians Deleted(Except currently logged in user) successfully");
            if (mobileUpdated) {
                DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, "mdm.license.moved_to_free_edition", (Object)null, true);
                return true;
            }
        }
        catch (final Exception e) {
            logger.log(Level.WARNING, "Exception in isClearedDetailsForFreeEdition ", e);
        }
        return false;
    }
    
    public JSONObject getLicenseMessages(final JSONObject requestJson) {
        final JSONObject responseJson = new JSONObject();
        return responseJson;
    }
    
    public Boolean showEndpointCentralLicenseMessageBox(final String messageBox) {
        try {
            final String setTime = SyMUtil.getSyMParameter("MDM_ADDON_REMOVAL_TIME");
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable() && setTime != null) {
                if (messageBox.equalsIgnoreCase("UEM_CENTRAL_LICENSE_LIMIT_EXCEED") && MDMUtil.getCurrentTimeInMillis() > Long.valueOf(setTime)) {
                    final JSONObject licenseDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().licenseListener(LicenseAction.GET_USAGE_DETAILS);
                    final Integer mobileDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
                    final Long managedComputerDevices = (Long)LicenseProvider.getInstance().getDCLicenseAPI().getManagedComputersCount();
                    final Long totalCount = mobileDeviceCount + managedComputerDevices;
                    final Long allowedCount = Long.parseLong(String.valueOf(licenseDetails.optInt("allowedCount")));
                    if (allowedCount < totalCount) {
                        return false;
                    }
                }
                else if (messageBox.equalsIgnoreCase("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING") && MDMUtil.getCurrentTimeInMillis() < Long.valueOf(setTime)) {
                    final JSONObject licenseDetails = LicenseActionListenerImpl.getMDMUsageDetails();
                    final Integer managedMobileDevices = (licenseDetails != null) ? licenseDetails.optInt("managedCount", 0) : 0;
                    final Long managedComputerDevices = (Long)LicenseProvider.getInstance().getDCLicenseAPI().getManagedComputersCount();
                    final Long totalCount = managedComputerDevices + managedMobileDevices;
                    final Long allowedMobileDeviceCount = Long.parseLong(LicenseProvider.getInstance().getMDMLicenseAPI().getPurchasedMobileCount());
                    final Long allowedComputerCount = Long.parseLong(LicenseProvider.getInstance().getDCLicenseAPI().getPurchasedComputerCount());
                    final String mdmLicenseType = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
                    final boolean isDCFree = LicenseProvider.getGeneralLicenseAPI().isFreeLicense();
                    final Boolean isFreeMDMLicense = allowedMobileDeviceCount == 25L && mdmLicenseType.equalsIgnoreCase("Professional");
                    if (isFreeMDMLicense && isDCFree) {
                        if (totalCount > 25L) {
                            return false;
                        }
                    }
                    else if (isFreeMDMLicense && totalCount > allowedComputerCount) {
                        return false;
                    }
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            return true;
        }
        return true;
    }
}
