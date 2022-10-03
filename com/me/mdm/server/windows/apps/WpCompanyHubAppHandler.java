package com.me.mdm.server.windows.apps;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.agent.MDMAgentConstants;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WpCompanyHubAppHandler
{
    public static Logger logger;
    private static WpCompanyHubAppHandler wpCHAHandler;
    
    public static WpCompanyHubAppHandler getInstance() {
        if (WpCompanyHubAppHandler.wpCHAHandler == null) {
            WpCompanyHubAppHandler.wpCHAHandler = new WpCompanyHubAppHandler();
        }
        return WpCompanyHubAppHandler.wpCHAHandler;
    }
    
    public JSONObject handleWPCompanyHubApp(final JSONObject requestJSON) {
        JSONObject wpCompanyHubAppJson = null;
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        try {
            String publishUrl = null;
            final String wpAppFilePath = this.getWPCompanyHubAppFolderPath(customerId);
            final String wpAppDBPath = this.getWPCompanyHubAppSignedFolderDBPath(customerId);
            final String localTempFile = requestJSON.optString("APP_FILE");
            if (localTempFile != null) {
                final HashMap fileMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(localTempFile, wpAppFilePath, wpAppDBPath, false, false);
                publishUrl = fileMap.get("destDCFileName");
            }
            final String bundleIdentifier = (String)requestJSON.opt("BUNDLE_IDENTIFIER");
            if (bundleIdentifier != null) {
                final Long appID = (Long)DBUtil.getValueFromDB("MdAppDetails", "IDENTIFIER", (Object)bundleIdentifier, "APP_ID");
                requestJSON.put("APP_ID", (Object)appID);
            }
            final JSONObject jsonObject = this.createWPCompanyHubAppJSON(requestJSON);
            MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(jsonObject);
            MDMMessageHandler.getInstance().messageAction("NO_APP_ADDED", customerId);
            final Properties wpNativeApp = new Properties();
            final Long appId = (Long)jsonObject.get("APP_ID");
            final boolean isNativeAppUpgrade = (boolean)requestJSON.optBoolean("IS_WP_NATIVE_APP_UPGRADE", false);
            Long resourceId = null;
            if (appId != null) {
                final Long collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
                final Integer distributionType = requestJSON.getInt("DISTRIBUTE_TYPE");
                ((Hashtable<String, Long>)wpNativeApp).put("APP_ID", appId);
                ((Hashtable<String, Long>)wpNativeApp).put("CUSTOMER_ID", customerId);
                ((Hashtable<String, Integer>)wpNativeApp).put("DISTRIBUTE_TYPE", distributionType);
                ((Hashtable<String, String>)wpNativeApp).put("APP_FILE_PATH", publishUrl);
                wpCompanyHubAppJson = new JSONObject();
                if (isNativeAppUpgrade) {
                    ((Hashtable<String, Integer>)wpNativeApp).put("DISTRIBUTE_TYPE", -1);
                }
                this.updateCompanyHubApp(wpNativeApp);
                wpCompanyHubAppJson.put("APP_NAME", jsonObject.get("APP_NAME"));
                wpCompanyHubAppJson.put("APP_VERSION", jsonObject.get("APP_VERSION"));
                wpCompanyHubAppJson.put("BUNDLE_IDENTIFIER", jsonObject.get("BUNDLE_IDENTIFIER"));
                final Properties companyHubAppProp = this.getWpCompanyHubAppDetails(customerId);
                wpCompanyHubAppJson.put("DISTRIBUTE_TYPE", ((Hashtable<K, Object>)companyHubAppProp).get("DISTRIBUTE_TYPE"));
                wpCompanyHubAppJson.put("APP_FILE_PATH", ((Hashtable<K, Object>)companyHubAppProp).get("APP_FILE_PATH"));
                final List resourceList = MDMEnrollmentUtil.getInstance().getEnrolledWindowsDevicesList(appId, customerId);
                final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                if (!isAppBasedEnrollment) {
                    if (!isNativeAppUpgrade) {
                        this.distributeWPCompanyHubAppwithEnrolledDevices(resourceList, appId, customerId, distributionType);
                        if (distributionType == 0) {
                            this.sendWPCompanyHubAppMail(resourceList, appId, customerId, distributionType);
                        }
                    }
                    else {
                        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
                        this.handleWpNativeAppSpecialHandling(customerId, appId);
                        for (int i = 0; i < resourceList.size(); ++i) {
                            resourceId = resourceList.get(i);
                            AppsUtil.getInstance().updatePublishedAppId(resourceId, appGroupId, appId);
                        }
                        MessageProvider.getInstance().hideMessage("NEW_WINDOWS_APP", customerId);
                    }
                }
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.WARNING, " Exception in handleWPCompanyHubApp ", ex);
        }
        return wpCompanyHubAppJson;
    }
    
    private void handleWpNativeAppSpecialHandling(final Long customerId, final Long appId) throws DataAccessException {
        List resourceIdList = null;
        String uploadedAgentVersion = null;
        String minSupportedOSVersion = null;
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("MdAppDetails"));
        sql.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        sql.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        sql.setCriteria(new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0, (boolean)Boolean.FALSE));
        final DataObject dobj = MDMUtil.getPersistence().get(sql);
        if (!dobj.isEmpty()) {
            final Row row = dobj.getFirstRow("MdAppDetails");
            uploadedAgentVersion = row.get("APP_VERSION").toString();
            if (uploadedAgentVersion.trim().startsWith("9.0.") || uploadedAgentVersion.trim().startsWith("9.1.")) {
                minSupportedOSVersion = "8.0";
            }
            else if (!uploadedAgentVersion.trim().equalsIgnoreCase("")) {
                minSupportedOSVersion = "8.1";
            }
        }
        if (minSupportedOSVersion != null) {
            if (minSupportedOSVersion.contains("8.1")) {
                resourceIdList = ManagedDeviceHandler.getInstance().getWindowsPhone81AboveDevices(customerId);
            }
            else {
                resourceIdList = ManagedDeviceHandler.getInstance().getWindowsPhoneManagedDevicesForCustomer(customerId);
            }
        }
        this.distributeWPCompanyHubAppwithEnrolledDevices(resourceIdList, appId, customerId, 1);
    }
    
    public Boolean hasLocationSupportCompatibleAgent(final Long resourceId) {
        try {
            final String installedAgentVersion = ManagedDeviceHandler.getInstance().getInstalledAgentVersion(resourceId);
            if (installedAgentVersion != null && installedAgentVersion.startsWith("9.2.")) {
                return Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in WpCompanyHubAppHandler.hasLocationSupportCompatibleAgent {0}", ex);
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }
    
    public List getResourcesWithLocationSupportComptabileAgent(List resourceList) throws DataAccessException {
        if (resourceList == null) {
            resourceList = ManagedDeviceHandler.getInstance().getWindows81AboveManagedDeviceResourceIDs(null);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8, (boolean)Boolean.FALSE);
        final Criteria agentVersionCriteria = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"9.0", 10, (boolean)Boolean.TRUE).or(new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"9.1", 10, (boolean)Boolean.TRUE)).negate();
        selectQuery.setCriteria(resourceCriteria.and(agentVersionCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iter = dao.getRows("ManagedDevice");
        final List supportedDevicesList = new ArrayList();
        while (iter.hasNext()) {
            final Row managedDeviceRow = iter.next();
            supportedDevicesList.add(managedDeviceRow.get("RESOURCE_ID"));
        }
        return supportedDevicesList;
    }
    
    public void updateCompanyHubApp(final Properties wpNativeApp) {
        try {
            final Long appid = ((Hashtable<K, Long>)wpNativeApp).get("APP_ID");
            final Long customerId = ((Hashtable<K, Long>)wpNativeApp).get("CUSTOMER_ID");
            final Criteria cusCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("WpAppSettings", cusCri);
            Row wpCompanyHubAppRow = null;
            if (!dObj.isEmpty()) {
                wpCompanyHubAppRow = dObj.getRow("WpAppSettings");
                wpCompanyHubAppRow.set("APP_ID", (Object)appid);
                wpCompanyHubAppRow.set("APP_FILE_PATH", ((Hashtable<K, Object>)wpNativeApp).get("APP_FILE_PATH"));
                final int distributeType = ((Hashtable<K, Integer>)wpNativeApp).get("DISTRIBUTE_TYPE");
                if (distributeType != -1) {
                    wpCompanyHubAppRow.set("DISTRIBUTE_TYPE", (Object)distributeType);
                }
                dObj.updateRow(wpCompanyHubAppRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in updateCompanyHubApp", ex);
        }
    }
    
    public Properties getWpCompanyHubAppDetails(final Long customerId) {
        Properties companyHubAppProp = null;
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("WpAppSettings", cusCri);
            Long appId = null;
            if (!dObj.isEmpty()) {
                final Row wpCompanyHubAppRow = dObj.getRow("WpAppSettings", cusCri);
                appId = (Long)wpCompanyHubAppRow.get("APP_ID");
                if (appId != null) {
                    companyHubAppProp = new Properties();
                    ((Hashtable<String, Long>)companyHubAppProp).put("APP_ID", appId);
                    ((Hashtable<String, Object>)companyHubAppProp).put("CUSTOMER_ID", wpCompanyHubAppRow.get("CUSTOMER_ID"));
                    ((Hashtable<String, Object>)companyHubAppProp).put("DISTRIBUTE_TYPE", wpCompanyHubAppRow.get("DISTRIBUTE_TYPE"));
                    ((Hashtable<String, Object>)companyHubAppProp).put("APP_FILE_PATH", wpCompanyHubAppRow.get("APP_FILE_PATH"));
                }
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in getWpCompanyHubAppDetails", ex);
        }
        return companyHubAppProp;
    }
    
    private void distributeWPCompanyHubAppwithEnrolledDevices(final List resourceList, final Long appId, final Long customerId, final int distributeType) {
        try {
            final HashMap profileCollectionMap = MDMUtil.getInstance().getProfiletoCollectionMap(appId);
            final JSONObject associatedUser = ProfileUtil.getInstance().getAssociatedUserForProfile(profileCollectionMap.keySet().iterator().next());
            WpCompanyHubAppHandler.logger.log(Level.INFO, "distributeWPCompanyHubAppwithEnrolledDevices :: Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { profileCollectionMap, resourceList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, List>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Integer>)properties).put("platformtype", 3);
            ((Hashtable<String, Long>)properties).put("customerId", customerId);
            ((Hashtable<String, Object>)properties).put("UserId", associatedUser.get("UserId"));
            ((Hashtable<String, Object>)properties).put("loggedOnUserName", associatedUser.get("loggedOnUserName"));
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            if (distributeType == 1) {
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
            }
            else if (distributeType == 0) {
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            }
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.WARNING, " Exception in distributeWPCompanyHubAppwithEnrolledDevices ", ex);
        }
    }
    
    public void sendWPCompanyHubAppSilentInstallMail(final Long customerId, final Long resourceId, final Long appId) {
        try {
            final Long wpNativeAppId = this.getWPCompanyHubAppId(customerId);
            if (appId.equals(wpNativeAppId)) {
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                this.sendWPCompanyHubAppMail(resourceList, appId, customerId, 1);
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.WARNING, " Exception in sendWPCompanyHubAppSilentInstallMail ", ex);
        }
    }
    
    public JSONObject sendWPCompanyHubAppMail(final List resourceList, final Long appId, final Long customerId, final int distributeType) throws Exception {
        Long resourceId = null;
        final JSONObject response = new JSONObject();
        try {
            final Boolean isMailSettingsConfigured = MDMEnrollmentUtil.getInstance().isMailServerConfigured();
            if (isMailSettingsConfigured) {
                WpCompanyHubAppHandler.logger.log(Level.INFO, " Mail settings configured, proceeding with sending app enroll mail for resources : {0} ", resourceId);
                for (int i = 0; i < resourceList.size(); ++i) {
                    resourceId = resourceList.get(i);
                    final String authPassword = IosNativeAppHandler.getInstance().generateEnrollmentId(resourceId);
                    final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId);
                    final HashMap userInfoMap = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
                    final String userName = userInfoMap.get("NAME");
                    final String userEmail = userInfoMap.get("EMAIL_ADDRESS");
                    final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(WpCompanyHubAppHandler.logger);
                    final Properties serverProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                    final Properties enrolAuthlMailProperties = new Properties();
                    ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_name$", userName);
                    ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_name$", ((Hashtable<K, Object>)serverProps).get("NAT_ADDRESS"));
                    ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_port$", ((Hashtable<K, Object>)serverProps).get("NAT_HTTPS_PORT"));
                    ((Hashtable<String, String>)enrolAuthlMailProperties).put("$enrollment_id$", authPassword);
                    ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_emailid$", userEmail);
                    if (distributeType == 0) {
                        String wpNativeAppFilePath = "http://www.windowsphone.com/s?appid=551ab9a7-413b-4b79-8142-74550af0c72e";
                        if (this.isWPCompanyHubAppUpload(customerId)) {
                            wpNativeAppFilePath = MDMEnrollmentUtil.getInstance().getWindowsAgentDownloadURL();
                            wpNativeAppFilePath = wpNativeAppFilePath + "/" + customerId;
                        }
                        ((Hashtable<String, String>)enrolAuthlMailProperties).put("$app_download_url$", wpNativeAppFilePath);
                        mailGenerator.sendMail(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_WINDOWS_DOWNLOAD_TEMPLATE, "MDM", customerId, enrolAuthlMailProperties);
                    }
                    else if (distributeType == 1) {
                        mailGenerator.sendMail(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_WINDOWS_TEMPLATE, "MDM", customerId, enrolAuthlMailProperties);
                    }
                }
                response.put("success", true);
                response.put("code", 0);
                response.put("message", (Object)"successfully sent app enroll mail");
            }
            else {
                WpCompanyHubAppHandler.logger.log(Level.INFO, " Mail settings NOT configured, NOT  sending app enroll mail for resources : {0} ", resourceId);
                response.put("success", false);
                response.put("code", 1);
                response.put("message", (Object)"Mail Server Settings not configured");
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.WARNING, " Exception in sendWPCompanyHubAppMail ", ex);
            response.put("success", false);
            response.put("code", 2);
            response.put("message", (Object)"Unknown error");
        }
        return response;
    }
    
    public JSONObject createWPCompanyHubAppJSON(final JSONObject requestJSON) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        Long packageId = -1L;
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final Long appId = requestJSON.optLong("APP_ID", -1L);
            Long profileId = -1L;
            Long collectionId = -1L;
            jsonObject.put("APP_ID", (Object)appId);
            if (appId != null && appId != -1L) {
                packageId = (Long)DBUtil.getValueFromDB("MdPackageToAppData", "APP_ID", (Object)appId, "PACKAGE_ID");
                collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
                final HashMap profileMap = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionId);
                profileId = profileMap.get("PROFILE_ID");
            }
            jsonObject.put("PACKAGE_ID", (Object)packageId);
            jsonObject.put("APP_NAME", (Object)"ME MDM for Windows");
            jsonObject.put("APP_VERSION", requestJSON.get("APP_VERSION"));
            jsonObject.put("BUNDLE_IDENTIFIER", requestJSON.get("BUNDLE_IDENTIFIER"));
            jsonObject.put("PLATFORM_TYPE", 3);
            jsonObject.put("APP_TITLE", (Object)"");
            jsonObject.put("APP_CATEGORY_NAME", (Object)"Business");
            jsonObject.put("COUNTRY_CODE", (Object)"US");
            jsonObject.put("PACKAGE_TYPE", 2);
            jsonObject.put("DISPLAY_IMAGE_LOC", (Object)"/images/wpNativeApp.png");
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("APP_FILE", (Object)requestJSON.optString("APP_FILE", ""));
            final JSONObject packageAppDataJSON = new JSONObject();
            packageAppDataJSON.put("SUPPORTED_DEVICES", 8);
            packageAppDataJSON.put("SUPPORTED_ARCH", (Object)"1");
            final JSONObject packageAppGroupJSON = new JSONObject();
            packageAppGroupJSON.put("IS_PAID_APP", false);
            final JSONObject packagePolicyJSON = new JSONObject();
            packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", true);
            packagePolicyJSON.put("PREVENT_BACKUP", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
            jsonObject.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            jsonObject.put("PackagePolicyForm", (Object)packagePolicyJSON);
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            jsonObject.put("PROFILE_ID", (Object)profileId);
            jsonObject.put("COLLECTION_ID", (Object)collectionId);
            jsonObject.put("PROFILE_NAME", (Object)"ME MDM for Windows");
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"Windows Native App");
            jsonObject.put("PROFILE_TYPE", 2);
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("APP_CONFIG", true);
            final String bundleID = (String)requestJSON.get("BUNDLE_IDENTIFIER");
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(3).isCurrentPackageNewToAppRepo(bundleID, customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.WARNING, "Exception in createWPCompanyHubAppJSON", ex);
        }
        return jsonObject;
    }
    
    public Long downloadWpCompanyHubApp(final HttpServletRequest request, final HttpServletResponse response, final Long customerID, final boolean isSigned) {
        final Long days = 0L;
        BufferedInputStream buffIn = null;
        BufferedOutputStream buffOut = null;
        String wpNativeAppFullPath = "";
        try {
            if (isSigned) {
                wpNativeAppFullPath = this.getWPCompanyHubAppSignedFullFilePath(customerID);
            }
            else {
                wpNativeAppFullPath = this.getWPCompanyHubAppUnSignedFullFilePath();
            }
            final File file = new File(wpNativeAppFullPath);
            if (file.exists()) {
                final byte[] b = new byte[2048];
                int len = 0;
                buffIn = new BufferedInputStream(new FileInputStream(wpNativeAppFullPath));
                buffOut = new BufferedOutputStream((OutputStream)response.getOutputStream());
                response.setHeader("Content-Length", "" + new File(wpNativeAppFullPath).length());
                response.setContentType("application/force-download");
                response.setHeader("Content-Disposition", "attachment;filename=mdmwindowsagent.xap");
                response.setHeader("Content-Transfer-Encoding", "binary");
                while ((len = buffIn.read(b)) > 0) {
                    buffOut.write(b, 0, len);
                    buffOut.flush();
                }
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.WARNING, " Exception in downloadWpCompanyHubApp ", ex);
            try {
                if (buffIn != null) {
                    buffIn.close();
                    buffOut.close();
                }
            }
            catch (final Exception ex) {
                WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception while closing stream", ex);
            }
        }
        finally {
            try {
                if (buffIn != null) {
                    buffIn.close();
                    buffOut.close();
                }
            }
            catch (final Exception ex2) {
                WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception while closing stream", ex2);
            }
        }
        return days;
    }
    
    public String getWPCompanyHubAppSignedFolderDBPath(final Long customerId) throws Exception {
        final String appRepositoryFolder = File.separator + MDMAgentConstants.MDM_WINDOWS_SIGNED_FOLDER + File.separator + customerId;
        return appRepositoryFolder;
    }
    
    public String getWPCompanyHubAppFolderPath(final Long customerId) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String aetFolder = webappsDir + File.separator + MDMAgentConstants.MDM_WINDOWS_SIGNED_FOLDER + File.separator + customerId;
        return aetFolder;
    }
    
    public String getWPCompanyHubAppUnSignedFullFilePath() throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String wpNativeAppFolder = webappsDir + File.separator + MDMAgentConstants.MDM_WP_UNSIGNED_AGENT_DOWNLOAD_URL;
        return wpNativeAppFolder;
    }
    
    public String getWPCompanyHubAppSignedFullFilePath(final Long customerId) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String wpNativeAppFilePath = this.getWpCompanyHubSignedFilePath(customerId);
        final String wpNativeAppFolder = webappsDir + File.separator + wpNativeAppFilePath;
        return wpNativeAppFolder;
    }
    
    public boolean isWPCompanyHubAppUpload(final Long customerId) {
        boolean isWCHApp = false;
        try {
            final Long appId = this.getWPCompanyHubAppId(customerId);
            if (appId != null) {
                isWCHApp = true;
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in isWindowsCompanyHubAppUpload", ex);
        }
        return isWCHApp;
    }
    
    public Long getWPCompanyHubAppId(final Long customerId) {
        Long appId = null;
        try {
            appId = (Long)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerId, "APP_ID");
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in getWpNativeAppId", ex);
        }
        return appId;
    }
    
    public String getWpCompanyHubSignedFilePath(final Long customerId) {
        String wpNativeAppFilePath = null;
        try {
            wpNativeAppFilePath = (String)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerId, "APP_FILE_PATH");
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in getWpCompanyHubSignedFilePath", ex);
        }
        return wpNativeAppFilePath;
    }
    
    public Properties getCompanyHubAppEnrollmentProp(final Long customerId) {
        Properties windowsAppProp = null;
        String bundleIdentifier = null;
        String storeURI = null;
        final String storeName = "ME MDM App Store";
        String appFilePath = "";
        try {
            final Properties companyHubAppProp = this.getWpCompanyHubAppDetails(customerId);
            if (companyHubAppProp != null) {
                final Long appId = ((Hashtable<K, Long>)companyHubAppProp).get("APP_ID");
                windowsAppProp = new Properties();
                ((Hashtable<String, Object>)windowsAppProp).put("APP_ID", ((Hashtable<K, Object>)companyHubAppProp).get("APP_ID"));
                bundleIdentifier = AppsUtil.getInstance().getAppIdentifier(appId);
                appFilePath = ((Hashtable<K, String>)companyHubAppProp).get("APP_FILE_PATH");
                storeURI = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + appFilePath;
                ((Hashtable<String, String>)windowsAppProp).put("StoreProductId", bundleIdentifier);
                ((Hashtable<String, String>)windowsAppProp).put("StoreURI", storeURI.replace('\\', '/'));
                ((Hashtable<String, String>)windowsAppProp).put("StoreName", storeName);
            }
        }
        catch (final Exception ex) {
            WpCompanyHubAppHandler.logger.log(Level.SEVERE, "Exception in getWindowsPhoneAppDetails", ex);
        }
        return windowsAppProp;
    }
    
    private void updateWpCompanyHubAppMessage(final Long customerId, final Boolean isAppBasedEnrollment) {
        final Properties wpCompanyHubAppProperties = WpAppSettingsHandler.getInstance().getWpAETDetails(customerId);
        final Boolean isAetUploaded = (wpCompanyHubAppProperties == null) ? Boolean.FALSE : Boolean.TRUE;
        Boolean appUploaded = Boolean.FALSE;
        if (wpCompanyHubAppProperties != null) {
            final Long appId = ((Hashtable<K, Long>)wpCompanyHubAppProperties).get("APP_ID");
            appUploaded = ((appId == null) ? Boolean.FALSE : Boolean.TRUE);
        }
        if (!isAppBasedEnrollment && isAetUploaded && appUploaded) {
            MDMAppMgmtHandler.getInstance().isWpLatestAvailable(customerId);
        }
        else {
            MessageProvider.getInstance().hideMessage("NEW_WINDOWS_APP", customerId);
        }
    }
    
    public void updateWpCompanyHubAppMessage() throws Exception {
        final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
        if (CustomerInfoUtil.getInstance().isMSP()) {
            final List customerIdList = Arrays.asList(CustomerInfoUtil.getInstance().getCustomerIdsFromDB());
            for (final Object customerId : customerIdList) {
                this.updateWpCompanyHubAppMessage((Long)customerId, isAppBasedEnrollment);
            }
        }
        else {
            this.updateWpCompanyHubAppMessage(CustomerInfoUtil.getInstance().getDefaultCustomer(), isAppBasedEnrollment);
        }
    }
    
    public String getPackageFullName(final Long commandID, final Long resID) throws Exception {
        String packageFullName = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
        final Join appJoin = new Join("MdCollectionCommand", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join installedAppjoin = new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join apptogrpJoin = new Join("MdPackageToAppGroup", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appgrptoappJoin = new Join("MdAppToGroupRel", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join appdetailsJoin = new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join packageDataJoin = new Join("AppGroupToCollection", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria commandCriteria = new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)commandID, 0);
        final Criteria resCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resID, 0);
        selectQuery.addJoin(appJoin);
        selectQuery.addJoin(installedAppjoin);
        selectQuery.addJoin(apptogrpJoin);
        selectQuery.addJoin(appgrptoappJoin);
        selectQuery.addJoin(appdetailsJoin);
        selectQuery.addJoin(packageDataJoin);
        selectQuery.setCriteria(commandCriteria.and(resCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row pkgRow = dataObject.getFirstRow("MdPackageToAppGroup");
            if (!(boolean)pkgRow.get("IS_PURCHASED_FROM_PORTAL")) {
                final Row appRow = dataObject.getFirstRow("MdAppDetails");
                final Row pkgAppRow = dataObject.getFirstRow("MdPackageToAppData");
                packageFullName = (String)appRow.get("IDENTIFIER");
                final String fileLoc = (String)pkgAppRow.get("APP_FILE_LOC");
                if (packageFullName != null && fileLoc.toLowerCase().contains(".appxbundle")) {
                    packageFullName = packageFullName.replaceAll("x86|x64", "neutral");
                    packageFullName = packageFullName.replaceAll("__", "_~_");
                }
            }
            else {
                final Row row = dataObject.getFirstRow("MdPackageToAppData");
                final Long appGrpId = (Long)row.get("APP_GROUP_ID");
                final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
                selectQuery2.addJoin(new Join("MdAppToGroupRel", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                selectQuery2.addJoin(new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                final Criteria appgrpCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGrpId, 0);
                selectQuery2.setCriteria(appgrpCriteria.and(resCriteria));
                selectQuery2.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
                selectQuery2.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
                final DataObject dataObject2 = MDMUtil.getPersistence().get(selectQuery2);
                final Row row2 = dataObject2.getFirstRow("MdAppDetails");
                packageFullName = (String)row2.get("IDENTIFIER");
            }
        }
        return packageFullName;
    }
    
    public Boolean isMSIAlreadyInstalledStatus(final JSONObject commandStatusObject) {
        Boolean isMSIAlreadyInstalled = Boolean.FALSE;
        if (commandStatusObject != null && commandStatusObject.has("statusMap")) {
            final JSONObject statusMapJson = commandStatusObject.optJSONObject("statusMap");
            if (statusMapJson != null) {
                final Iterator statusMapJsonKeys = statusMapJson.keys();
                while (statusMapJsonKeys.hasNext()) {
                    final String statusMapJsonKey = statusMapJsonKeys.next();
                    if (statusMapJsonKey.contains(";Add") && statusMapJson.optInt(statusMapJsonKey, -111) == 418) {
                        isMSIAlreadyInstalled = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        return isMSIAlreadyInstalled;
    }
    
    static {
        WpCompanyHubAppHandler.logger = Logger.getLogger("MDMConfigLogger");
        WpCompanyHubAppHandler.wpCHAHandler = null;
    }
}
