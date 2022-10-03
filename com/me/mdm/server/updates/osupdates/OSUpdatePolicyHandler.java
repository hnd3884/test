package com.me.mdm.server.updates.osupdates;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.DerivedColumn;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.QueryConstructionException;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.HashSet;
import com.me.mdm.server.doc.DocAPIHandler;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.Collection;
import java.util.Set;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.constants.MDMDeploymentTemplateConstants;
import java.util.List;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Properties;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayloadHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import java.io.File;
import com.me.mdm.server.updates.osupdates.ios.IOSOSUpdateHandler;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.me.mdm.server.profiles.ProfileException;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class OSUpdatePolicyHandler
{
    private static final Logger LOGGER;
    public static final String ANDROID_OS_UPDATE_INSTALL_JSON = "os_update_install.json";
    public static final String ANDROID_OS_UPDATE_REMOVE_JSON = "os_update_remove.json";
    public static final String CHROME_OS_UPDATE_INSTALL_JSON = "chrome_os_update_install.json";
    public static final String CHROME_OS_UPDATE_REMOVE_JSON = "chrome_os_update_remove.json";
    private static OSUpdatePolicyHandler osUpdatePolicyHandler;
    
    public static OSUpdatePolicyHandler getInstance() {
        if (OSUpdatePolicyHandler.osUpdatePolicyHandler == null) {
            OSUpdatePolicyHandler.osUpdatePolicyHandler = new OSUpdatePolicyHandler();
        }
        return OSUpdatePolicyHandler.osUpdatePolicyHandler;
    }
    
    public JSONObject addOrUpdateOSPolicy(final JSONObject msgHeaderJSON, final JSONObject dataJson) throws Exception {
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "addOrUpdateOSPolicy policyJSON : {0}", dataJson);
        final JSONObject responseJSON = new JSONObject();
        try {
            JSONUtil.getInstance();
            final Long customerID = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
            final JSONObject profileJSON = dataJson.getJSONObject("Profile");
            JSONUtil.getInstance();
            Long profileID = JSONUtil.optLongForUVH(profileJSON, "PROFILE_ID", Long.valueOf(-1L));
            if (customerID != -1L && profileID != -1L && !ProfileUtil.getInstance().isCustomerEligible(customerID, profileID)) {
                throw new ProfileException();
            }
            JSONUtil.getInstance();
            final Long userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
            final JSONObject osUpdatePolicyJSON = dataJson.getJSONObject("OSUpdatePolicy");
            String sEventLogRemarks = "mdm.actionlog.osupdate.create_success";
            if (profileID != -1L) {
                sEventLogRemarks = "mdm.actionlog.osupdate.update_success";
            }
            Long collectionID = null;
            responseJSON.put("isNewProfileDetected", false);
            if (profileID == -1L) {
                profileJSON.put("PROFILE_TYPE", 3);
                profileJSON.put("CREATED_BY", (Object)userID);
                profileJSON.put("PROFILE_DESCRIPTION", (Object)"OS Update Policy Profile");
                profileJSON.put("CUSTOMER_ID", (Object)customerID);
                profileJSON.put("SECURITY_TYPE", 3);
                ProfileConfigHandler.addOrModifyProfileCollection(profileJSON);
                JSONUtil.getInstance();
                profileID = JSONUtil.optLongForUVH(profileJSON, "PROFILE_ID", Long.valueOf(-1L));
                JSONUtil.getInstance();
                collectionID = JSONUtil.optLongForUVH(profileJSON, "COLLECTION_ID", Long.valueOf(-1L));
                responseJSON.put("isNewProfileDetected", true);
            }
            else {
                collectionID = ProfileHandler.getRecentProfileCollectionID(profileID);
                profileJSON.put("LAST_MODIFIED_BY", (Object)userID);
                ProfileHandler.addOrUpdateProfile(profileJSON);
            }
            final DataObject dataObject = this.getOSUpdatePolicy(collectionID);
            Row osUpdateRow = null;
            if (dataObject.isEmpty()) {
                osUpdateRow = new Row("OSUpdatePolicy");
                osUpdateRow.set("COLLECTION_ID", (Object)collectionID);
                osUpdateRow.set("POLICY_TYPE", (Object)osUpdatePolicyJSON.getInt("POLICY_TYPE"));
                osUpdateRow.set("DEFER_DAYS", (Object)osUpdatePolicyJSON.optInt("DEFER_DAYS", 0));
                final Row row = osUpdateRow;
                final String s = "EXPIRY_TIME";
                JSONUtil.getInstance();
                row.set(s, (Object)JSONUtil.optLongForUVH(osUpdatePolicyJSON, "EXPIRY_TIME", Long.valueOf(-1L)));
                osUpdateRow.set("RELEASE_CHANNEL", (Object)Integer.parseInt(osUpdatePolicyJSON.optString("RELEASE_CHANNEL", "0")));
                dataObject.addRow(osUpdateRow);
            }
            else {
                osUpdateRow = dataObject.getFirstRow("OSUpdatePolicy");
                osUpdateRow.set("POLICY_TYPE", (Object)osUpdatePolicyJSON.getInt("POLICY_TYPE"));
                osUpdateRow.set("DEFER_DAYS", (Object)osUpdatePolicyJSON.optInt("DEFER_DAYS", 0));
                osUpdateRow.set("EXPIRY_TIME", (Object)JSONUtil.optLongForUVH(osUpdatePolicyJSON, "EXPIRY_TIME", Long.valueOf(-1L)));
                osUpdateRow.set("RELEASE_CHANNEL", (Object)Integer.parseInt(osUpdatePolicyJSON.optString("RELEASE_CHANNEL", "0")));
                dataObject.updateRow(osUpdateRow);
            }
            if (!dataObject.containsTable("MdmDeploymentTemplate")) {
                this.addOrUpdateDeploymentTemplate(dataObject, profileJSON, customerID, userID);
                this.addOrUpdateDeploymentTemplateToOsUpdatePolicy(dataObject, collectionID);
            }
            if (dataJson.has("DeploymentWindowTemplate")) {
                this.addOrUpdateWindowPolicy(dataObject, dataJson.optJSONObject("DeploymentWindowTemplate"));
            }
            if (dataJson.has("DeploymentNotifTemplate")) {
                this.addOrUpdateNotifyPolicy(dataObject, dataJson.optJSONObject("DeploymentNotifTemplate"));
            }
            if (dataJson.has("DeploymentPolicySettings")) {
                this.addOrUpdateDepSettingsPolicy(dataObject, dataJson.optJSONObject("DeploymentPolicySettings"));
            }
            if (dataJson.has("DeploymentPolicyFiles")) {
                this.addOrUpdateDepPolicyFiles(dataObject, dataJson.optJSONObject("DeploymentPolicyFiles"));
            }
            MDMUtil.getPersistence().update(dataObject);
            responseJSON.put("PROFILE_ID", (Object)profileID);
            responseJSON.put("COLLECTION_ID", (Object)collectionID);
            this.publishOSUpdatePolicy(msgHeaderJSON, responseJSON);
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            final Object remarksArgs = profileJSON.optString("PROFILE_NAME") + "@@@" + sUserName;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2121, null, sUserName, sEventLogRemarks, remarksArgs, customerID);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "Successfully created the OS update policy : {0}", responseJSON);
        }
        catch (final JSONException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid post params", (Throwable)e);
            throw e;
        }
        catch (final ProfileException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid access for customer", (Throwable)e2);
            throw e2;
        }
        catch (final DataAccessException e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Can't perform data operation", (Throwable)e3);
            throw e3;
        }
        catch (final SyMException e4) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Can't able to add or modify collection", (Throwable)e4);
            throw e4;
        }
        catch (final Exception e5) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception add osupdate policy", e5);
            throw e5;
        }
        return responseJSON;
    }
    
    public void publishOSUpdatePolicy(final JSONObject msgHeaderJSON, final JSONObject distributeOSUpdatePolicyJSON) throws Exception {
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy distributedPolicyJSON : {0}", distributeOSUpdatePolicyJSON);
        final Long profileIds = distributeOSUpdatePolicyJSON.optLong("PROFILE_ID");
        final List metaDataList = new ArrayList();
        final Boolean isNewProfileDetected = distributeOSUpdatePolicyJSON.getBoolean("isNewProfileDetected");
        final Long collectionID = JSONUtil.optLongForUVH(distributeOSUpdatePolicyJSON, "COLLECTION_ID", Long.valueOf(-1L));
        final Long customerID = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
        final IOSOSUpdateHandler iososUpdateHandler = new IOSOSUpdateHandler();
        final JSONObject iosCmdObject = iososUpdateHandler.addIOSOSUpdatePolicyXML(collectionID, mdmProfileDir, mdmProfileRelativeDirPath);
        final String androidInstallCmdFileName = mdmProfileDir + File.separator + "os_update_install.json";
        final String androidInstallOsUpdateCmdUUID = AndroidPayloadHandler.getInstance().createScheduleOSUpdateCommandJSON(collectionID, androidInstallCmdFileName);
        final String androidInstallCmdRelPath = mdmProfileRelativeDirPath + File.separator + "os_update_install.json";
        final String androidRemoveCmdFileName = mdmProfileDir + File.separator + "os_update_remove.json";
        final String androidRemoveOsUpdateCmdUUID = AndroidPayloadHandler.getInstance().createRemoveOSUpdateCommandJSON(collectionID, androidRemoveCmdFileName);
        final String androidRemoveCmdRelPath = mdmProfileRelativeDirPath + File.separator + "os_update_remove.json";
        final String chromeInstallCmdFileName = mdmProfileDir + File.separator + "chrome_os_update_install.json";
        final String chromeInstallOsUpdateCmdUUID = ChromePayloadHandler.getInstance().createScheduleOSUpdateCommandJSON(collectionID, chromeInstallCmdFileName);
        final String chromeInstallCmdRelPath = mdmProfileRelativeDirPath + File.separator + "chrome_os_update_install.json";
        final String chromeRemoveCmdFileName = mdmProfileDir + File.separator + "chrome_os_update_remove.json";
        final String chromeRemoveOsUpdateCmdUUID = ChromePayloadHandler.getInstance().createRemoveOSUpdateCommandJSON(collectionID, chromeRemoveCmdFileName);
        final String chromeRemoveCmdRelPath = mdmProfileRelativeDirPath + File.separator + "chrome_os_update_remove.json";
        if (isNewProfileDetected) {
            iososUpdateHandler.addOSUpdateCommand(collectionID, iosCmdObject);
            Properties androidInstallOsUpdateProps = new Properties();
            androidInstallOsUpdateProps = new Properties();
            androidInstallOsUpdateProps.setProperty("commandUUID", androidInstallOsUpdateCmdUUID);
            androidInstallOsUpdateProps.setProperty("commandType", "OsUpdatePolicy");
            androidInstallOsUpdateProps.setProperty("commandFilePath", androidInstallCmdRelPath);
            androidInstallOsUpdateProps.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(androidInstallOsUpdateProps);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy android collection command : {0}", "OsUpdatePolicy");
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy android collection metadata : {0}", androidInstallOsUpdateProps);
            Properties androidRemoveOsUpdateProps = new Properties();
            androidRemoveOsUpdateProps = new Properties();
            androidRemoveOsUpdateProps.setProperty("commandUUID", androidRemoveOsUpdateCmdUUID);
            androidRemoveOsUpdateProps.setProperty("commandType", "RemoveOsUpdatePolicy");
            androidRemoveOsUpdateProps.setProperty("commandFilePath", androidRemoveCmdRelPath);
            androidRemoveOsUpdateProps.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(androidRemoveOsUpdateProps);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy android collection command : {0}", "RemoveOsUpdatePolicy");
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy android collection metadata : {0}", androidRemoveOsUpdateProps);
            Properties chromeInstallOsUpdateProps = new Properties();
            chromeInstallOsUpdateProps = new Properties();
            chromeInstallOsUpdateProps.setProperty("commandUUID", chromeInstallOsUpdateCmdUUID);
            chromeInstallOsUpdateProps.setProperty("commandType", "ChromeOsUpdatePolicy");
            chromeInstallOsUpdateProps.setProperty("commandFilePath", chromeInstallCmdRelPath);
            chromeInstallOsUpdateProps.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(chromeInstallOsUpdateProps);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy chrome collection command : {0}", "ChromeOsUpdatePolicy");
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy chrome collection metadata : {0}", chromeInstallOsUpdateProps);
            Properties chromeRemoveOsUpdateProps = new Properties();
            chromeRemoveOsUpdateProps = new Properties();
            chromeRemoveOsUpdateProps.setProperty("commandUUID", chromeRemoveOsUpdateCmdUUID);
            chromeRemoveOsUpdateProps.setProperty("commandType", "RemoveChromeOsUpdatePolicy");
            chromeRemoveOsUpdateProps.setProperty("commandFilePath", chromeRemoveCmdRelPath);
            chromeRemoveOsUpdateProps.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(chromeRemoveOsUpdateProps);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy android collection command : {0}", "RemoveChromeOsUpdatePolicy");
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy android collection metadata : {0}", chromeRemoveOsUpdateProps);
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
        }
        final Long userID = msgHeaderJSON.optLong("USER_ID");
        final String loggedonName = msgHeaderJSON.optString("loggedOnUserName");
        OSUpdatePolicyHandler.LOGGER.log(Level.FINE, "Going to execute the task for publish task");
        final CommonQueueData osUpdateQueueData = new CommonQueueData();
        osUpdateQueueData.setClassName("com.me.mdm.server.updates.osupdates.task.OSUpdatePublishTask");
        osUpdateQueueData.setTaskName("OSUpdatePublishTask");
        osUpdateQueueData.setCustomerId(customerID);
        final JSONObject queueData = new JSONObject();
        queueData.put("USER_ID", (Object)userID);
        queueData.put("LOGGEDONUSERNAME", (Object)loggedonName);
        queueData.put("PROFILE_ID", (Object)profileIds);
        osUpdateQueueData.setJsonQueueData(queueData);
        OSUpdatePolicyHandler.LOGGER.log(Level.FINE, "Going to add to queue for publish task");
        CommonQueueUtil.getInstance().addToQueue(osUpdateQueueData, CommonQueues.MDM_PROFILE_MGMT);
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy Successfully completed.");
    }
    
    private void addOrUpdateWindowPolicy(final DataObject dataObject, final JSONObject windowData) throws DataAccessException, JSONException {
        Row execWindowTempRow = dataObject.getRow("DeploymentWindowTemplate");
        if (execWindowTempRow == null) {
            execWindowTempRow = new Row("DeploymentWindowTemplate");
            execWindowTempRow.set("DEPLOYMENT_TEMPLATE_ID", dataObject.getRow("MdmDeploymentTemplate").get("DEPLOYMENT_TEMPLATE_ID"));
            execWindowTempRow.set("WINDOW_START_TIME", (Object)windowData.optInt("WINDOW_START_TIME", 1));
            execWindowTempRow.set("WINDOW_END_TIME", (Object)windowData.optInt("WINDOW_END_TIME", 1440));
            execWindowTempRow.set("WINDOW_WEEK_OF_MONTH", (Object)windowData.optString("WINDOW_WEEK_OF_MONTH", "1,2,3,4,5"));
            execWindowTempRow.set("WINDOW_DAY_OF_WEEK", (Object)windowData.optString("WINDOW_DAY_OF_WEEK", "1,2,3,4,5,6,7"));
            dataObject.addRow(execWindowTempRow);
        }
        else {
            execWindowTempRow.set("WINDOW_START_TIME", (Object)windowData.optInt("WINDOW_START_TIME", 1));
            execWindowTempRow.set("WINDOW_END_TIME", (Object)windowData.optInt("WINDOW_END_TIME", 1440));
            execWindowTempRow.set("WINDOW_WEEK_OF_MONTH", (Object)windowData.optString("WINDOW_WEEK_OF_MONTH", "1,2,3,4,5"));
            execWindowTempRow.set("WINDOW_DAY_OF_WEEK", (Object)windowData.optString("WINDOW_DAY_OF_WEEK", "1,2,3,4,5,6,7"));
            dataObject.updateRow(execWindowTempRow);
        }
    }
    
    private void addOrUpdateNotifyPolicy(final DataObject dataObject, final JSONObject windowData) throws DataAccessException, JSONException {
        Row execNotifyTempRow = dataObject.getRow("DeploymentNotifTemplate");
        if (execNotifyTempRow == null) {
            execNotifyTempRow = new Row("DeploymentNotifTemplate");
            execNotifyTempRow.set("DEPLOYMENT_TEMPLATE_ID", dataObject.getRow("MdmDeploymentTemplate").get("DEPLOYMENT_TEMPLATE_ID"));
            execNotifyTempRow.set("NOTIFY_TITLE", (Object)windowData.optString("NOTIFY_TITLE", "Pending OS Updates!"));
            execNotifyTempRow.set("NOTIFY_MESSAGE", (Object)windowData.optString("NOTIFY_MESSAGE", "Your Administrator has configured a updates to be installed on this device"));
            execNotifyTempRow.set("ALLOW_USERS_TO_SKIP", (Object)windowData.optBoolean("ALLOW_USERS_TO_SKIP", true));
            execNotifyTempRow.set("MAX_SKIPS_ALLOWED", (Object)windowData.optInt("MAX_SKIPS_ALLOWED", 10));
            dataObject.addRow(execNotifyTempRow);
        }
        else {
            execNotifyTempRow.set("NOTIFY_TITLE", (Object)windowData.optString("NOTIFY_TITLE", "Pending OS Updates!"));
            execNotifyTempRow.set("NOTIFY_MESSAGE", (Object)windowData.optString("NOTIFY_MESSAGE", "Your Administrator has configured a updates to be installed on this device"));
            execNotifyTempRow.set("ALLOW_USERS_TO_SKIP", (Object)windowData.optBoolean("ALLOW_USERS_TO_SKIP", true));
            execNotifyTempRow.set("MAX_SKIPS_ALLOWED", (Object)windowData.optInt("MAX_SKIPS_ALLOWED", 10));
            dataObject.updateRow(execNotifyTempRow);
        }
    }
    
    private void addOrUpdateDepSettingsPolicy(final DataObject dataObject, final JSONObject settings) throws DataAccessException, JSONException {
        Row execNotifyTempRow = dataObject.getRow("DeploymentPolicySettings");
        if (execNotifyTempRow == null) {
            execNotifyTempRow = new Row("DeploymentPolicySettings");
            execNotifyTempRow.set("DEPLOYMENT_TEMPLATE_ID", dataObject.getRow("MdmDeploymentTemplate").get("DEPLOYMENT_TEMPLATE_ID"));
            execNotifyTempRow.set("MAX_TARGET_PREFIX", (Object)settings.optString("MAX_TARGET_PREFIX"));
            execNotifyTempRow.set("REBOOT_AFTER_UPDATE", (Object)settings.optBoolean("REBOOT_AFTER_UPDATE"));
            execNotifyTempRow.set("DOWNLOAD_OVER_WIFI", (Object)settings.optBoolean("DOWNLOAD_OVER_WIFI"));
            execNotifyTempRow.set("DOWNLOAD_IN_DEP_WINDOW", (Object)settings.optBoolean("DOWNLOAD_IN_DEP_WINDOW"));
            dataObject.addRow(execNotifyTempRow);
        }
        else {
            execNotifyTempRow.set("MAX_TARGET_PREFIX", (Object)settings.optString("MAX_TARGET_PREFIX"));
            execNotifyTempRow.set("REBOOT_AFTER_UPDATE", (Object)settings.optBoolean("REBOOT_AFTER_UPDATE"));
            execNotifyTempRow.set("DOWNLOAD_OVER_WIFI", (Object)settings.optBoolean("DOWNLOAD_OVER_WIFI"));
            execNotifyTempRow.set("DOWNLOAD_IN_DEP_WINDOW", (Object)settings.optBoolean("DOWNLOAD_IN_DEP_WINDOW"));
            dataObject.updateRow(execNotifyTempRow);
        }
    }
    
    private void addOrUpdateDepPolicyFiles(final DataObject dataObject, final JSONObject settings) throws DataAccessException, JSONException {
        Row depPolicyRow = dataObject.getRow("DeploymentPolicyFiles");
        if (depPolicyRow == null) {
            depPolicyRow = new Row("DeploymentPolicyFiles");
            depPolicyRow.set("DEPLOYMENT_TEMPLATE_ID", dataObject.getRow("MdmDeploymentTemplate").get("DEPLOYMENT_TEMPLATE_ID"));
            depPolicyRow.set("DOWNLOAD_FILE_LOCATION", (Object)settings.optString("DOWNLOAD_FILE_LOCATION"));
            depPolicyRow.set("DOC_ID", (Object)settings.getLong("DOC_ID"));
            dataObject.addRow(depPolicyRow);
        }
        else {
            depPolicyRow.set("DOWNLOAD_FILE_LOCATION", (Object)settings.optString("DOWNLOAD_FILE_LOCATION"));
            depPolicyRow.set("DOC_ID", (Object)settings.getLong("DOC_ID"));
            dataObject.updateRow(depPolicyRow);
        }
    }
    
    private void addOrUpdateDeploymentTemplate(final DataObject dataObject, final JSONObject profileJSON, final Long customerId, final Long userId) throws DataAccessException, JSONException {
        Row row = dataObject.getRow("MdmDeploymentTemplate");
        if (row == null) {
            row = new Row("MdmDeploymentTemplate");
            row.set("DEPLOYMENT_TEMPLATE_NAME", (Object)(profileJSON.optString("PROFILE_NAME", "OS Update Policy") + " Template"));
            row.set("DEPLOYMENT_TEMPLATE_DESC", (Object)"Auto Generated Template");
            row.set("CREATED_BY", (Object)userId);
            row.set("MODIFIED_BY", (Object)userId);
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("CREATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("DEPLOYMENT_TEMPLATE_TYPE", (Object)MDMDeploymentTemplateConstants.OS_UPDATE_DEPLOYMENT_POLICY);
            dataObject.addRow(row);
        }
        else {
            row.set("DEPLOYMENT_TEMPLATE_NAME", (Object)(profileJSON.optString("PROFILE_NAME", "OS Update Policy") + " Template"));
            row.set("DEPLOYMENT_TEMPLATE_DESC", (Object)"Auto Generated Template");
            row.set("MODIFIED_BY", (Object)userId);
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("DEPLOYMENT_TEMPLATE_TYPE", (Object)MDMDeploymentTemplateConstants.OS_UPDATE_DEPLOYMENT_POLICY);
            dataObject.updateRow(row);
        }
    }
    
    private void addOrUpdateDeploymentTemplateToOsUpdatePolicy(final DataObject dataObject, final Long collectionId) throws DataAccessException, JSONException {
        Row row = dataObject.getRow("DeploymentTempToOSUpdate");
        if (row == null) {
            row = new Row("DeploymentTempToOSUpdate");
            row.set("COLLECTION_ID", (Object)collectionId);
            row.set("DEPLOYMENT_TEMPLATE_ID", dataObject.getRow("MdmDeploymentTemplate").get("DEPLOYMENT_TEMPLATE_ID"));
            dataObject.addRow(row);
        }
        else {
            row.set("DEPLOYMENT_TEMPLATE_ID", dataObject.getRow("MdmDeploymentTemplate").get("DEPLOYMENT_TEMPLATE_ID"));
            dataObject.updateRow(row);
        }
    }
    
    public DataObject getOSUpdatePolicy(final Long collectionID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OSUpdatePolicy"));
        final Join recentProfileJoin = new Join("OSUpdatePolicy", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join profileJoin = new Join("RecentProfileToColln", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join depTempJoin = new Join("OSUpdatePolicy", "DeploymentTempToOSUpdate", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
        final Join templateJoin = new Join("DeploymentTempToOSUpdate", "MdmDeploymentTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join notifyTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentNotifTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join windowTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentWindowTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join settingsTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentPolicySettings", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join filesTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentPolicyFiles", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        selectQuery.addJoin(recentProfileJoin);
        selectQuery.addJoin(profileJoin);
        selectQuery.addJoin(depTempJoin);
        selectQuery.addJoin(templateJoin);
        selectQuery.addJoin(notifyTemplateJoin);
        selectQuery.addJoin(windowTemplateJoin);
        selectQuery.addJoin(settingsTemplateJoin);
        selectQuery.addJoin(filesTemplateJoin);
        final Criteria criteria = new Criteria(Column.getColumn("OSUpdatePolicy", "COLLECTION_ID"), (Object)collectionID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public JSONObject getOSUpdatePolicyJSON(final Long collectionID) throws Exception {
        final JSONObject policyJSON = new JSONObject();
        final DataObject dataObject = this.getOSUpdatePolicy(collectionID);
        final List tableNames = dataObject.getTableNames();
        Row policyRow = null;
        String tableName = null;
        if (!dataObject.isEmpty()) {
            for (int i = 0; i < tableNames.size(); ++i) {
                tableName = tableNames.get(i);
                policyRow = dataObject.getFirstRow(tableName);
                if (policyRow != null) {
                    policyJSON.put(tableName, (Object)policyRow.getAsJSON());
                }
            }
        }
        return policyJSON;
    }
    
    public JSONObject distributeOSUpdatePolicy(final JSONObject msgHeaderJSON, final JSONObject distributeOSPolicyJSON) throws Exception {
        try {
            final Long userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLong(msgHeaderJSON, "CUSTOMER_ID", -1L);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "distributeOSUpdatePolicy : {0}", distributeOSPolicyJSON);
            final List<Long> allProfileIds = new ArrayList<Long>();
            List<Long> collectionIds = new ArrayList<Long>();
            final List resourceIds = new ArrayList();
            final ArrayList<Long> groupIds = new ArrayList<Long>();
            List membersList = new ArrayList();
            if (distributeOSPolicyJSON.has("PROFILE_ID")) {
                allProfileIds.add(JSONUtil.optLongForUVH(distributeOSPolicyJSON, "PROFILE_ID", Long.valueOf(-1L)));
            }
            else if (distributeOSPolicyJSON.has("PROFILE_IDS")) {
                final JSONArray profilesArray = distributeOSPolicyJSON.optJSONArray("PROFILE_IDS");
                for (int i = 0; i < profilesArray.length(); ++i) {
                    allProfileIds.add(Long.valueOf(String.valueOf(profilesArray.get(i))));
                }
            }
            HashMap deviceMap = null;
            if (distributeOSPolicyJSON.has("GROUP_IDS")) {
                final JSONArray resources = distributeOSPolicyJSON.getJSONArray("GROUP_IDS");
                for (int j = 0; j < resources.length(); ++j) {
                    groupIds.add(Long.valueOf(resources.get(j).toString()));
                }
                membersList = ProfileAssociateHandler.getMemberGroupsId(groupIds);
                deviceMap = MDMCustomGroupUtil.getInstance().getPlatformBasedMemberIdForGroups(groupIds);
            }
            HashMap resMap = null;
            if (distributeOSPolicyJSON.has("DEVICE_IDS")) {
                final JSONArray resources2 = distributeOSPolicyJSON.getJSONArray("DEVICE_IDS");
                for (int k = 0; k < resources2.length(); ++k) {
                    resourceIds.add(Long.valueOf(resources2.get(k).toString()));
                }
                resMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceIds);
            }
            HashMap profileMap = null;
            profileMap = new ProfileHandler().getPlatformBasedProfileIds(allProfileIds);
            for (final int platform : profileMap.keySet()) {
                final ArrayList<?> profileIds = new ArrayList<Object>();
                profileIds.addAll(profileMap.get(platform));
                if (!profileIds.isEmpty()) {
                    final Map profileCollectionMap = this.getProfileCollectionIds(profileIds);
                    collectionIds = new ArrayList<Long>(profileCollectionMap.values());
                    List applicableDeviceList = new ArrayList();
                    List notApplicableList = new ArrayList();
                    if (deviceMap != null && !deviceMap.isEmpty()) {
                        applicableDeviceList.addAll(deviceMap.get(platform));
                        for (final int platformKey : deviceMap.keySet()) {
                            if (platformKey != platform) {
                                notApplicableList.addAll(deviceMap.get(platformKey));
                            }
                        }
                    }
                    if (resMap != null) {
                        applicableDeviceList.addAll(resMap.get(platform));
                        for (final int platformKey : resMap.keySet()) {
                            if (platformKey != platform) {
                                notApplicableList.addAll(resMap.get(platformKey));
                            }
                        }
                    }
                    applicableDeviceList = this.removeDuplicatesFromList((List<Object>)applicableDeviceList);
                    notApplicableList = this.removeDuplicatesFromList((List<Object>)notApplicableList);
                    OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "distributeOSUpdatePolicy collectionIds : {0}", collectionIds);
                    OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "distributeOSUpdatePolicy groupIds : {0}", groupIds);
                    OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "distributeOSUpdatePolicy : applicableDeviceList {0}", applicableDeviceList);
                    OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "distributeOSUpdatePolicy : notApplicableList {0}", notApplicableList);
                    final List finalResourceList = new ArrayList();
                    finalResourceList.addAll(notApplicableList);
                    finalResourceList.addAll(applicableDeviceList);
                    if (!notApplicableList.isEmpty() || !applicableDeviceList.isEmpty()) {
                        this.addOrUpdateRecentProfileForDevice(finalResourceList, profileCollectionMap, false);
                        this.addOrUpdateResourceToProfileHistory(finalResourceList, profileCollectionMap, userID);
                    }
                    if (!applicableDeviceList.isEmpty()) {
                        this.addTargetIDsForCollectionProfile(applicableDeviceList, collectionIds, "--", 12);
                    }
                    if (!notApplicableList.isEmpty()) {
                        final String notApplicableRemarks = "dc.mdm.devicemgmt.not_supported_profile_platform";
                        this.addTargetIDsForCollectionProfile(notApplicableList, collectionIds, notApplicableRemarks, 8);
                    }
                    if (!groupIds.isEmpty()) {
                        this.addOrUpdateRecentProfileForGroup(groupIds, profileCollectionMap, false);
                        this.addOrUpdateGroupToProfileHistory(groupIds, profileCollectionMap, userID);
                        this.updateGroupCollectionStatusSummary(groupIds, collectionIds);
                    }
                    ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
                    ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                    resMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(applicableDeviceList);
                    final List androidResourceList = new ArrayList(resMap.get(2));
                    if (!androidResourceList.isEmpty()) {
                        final List collectionIdsCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "OsUpdatePolicy");
                        DeviceCommandRepository.getInstance().assignCommandToDevices(collectionIdsCommandList, androidResourceList);
                        NotificationHandler.getInstance().SendNotification(androidResourceList, 2);
                        this.handleOSUpdateFiles(androidResourceList, collectionIds, customerId, userID, true);
                    }
                    final List chromeResourceList = new ArrayList(resMap.get(4));
                    if (!chromeResourceList.isEmpty()) {
                        final List collectionIdsCommandList2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "ChromeOsUpdatePolicy");
                        DeviceCommandRepository.getInstance().assignCommandToDevices(collectionIdsCommandList2, chromeResourceList);
                        NotificationHandler.getInstance().SendNotification(chromeResourceList, 4);
                    }
                    final ArrayList<?> iOSResourceList = new ArrayList<Object>(resMap.get(1));
                    if (iOSResourceList.isEmpty()) {
                        continue;
                    }
                    final List<Long> unsupervisedList = this.getUnsupervisedDevice((List<Long>)iOSResourceList);
                    iOSResourceList.removeAll(unsupervisedList);
                    final CommonQueueData osPolicyData = new CommonQueueData();
                    osPolicyData.setClassName("com.me.mdm.server.updates.osupdates.task.OSUpdateScheduleTask");
                    osPolicyData.setTaskName("OSUpdateScheduleTask");
                    osPolicyData.setCustomerId(customerId);
                    final JSONObject data = new JSONObject();
                    data.put("PROFILE_IDS", (Object)JSONUtil.getInstance().convertListToJSONArray(profileIds));
                    data.put("RESOURCE_IDS", (Object)JSONUtil.getInstance().convertListToJSONArray(iOSResourceList));
                    osPolicyData.setJsonQueueData(data);
                    final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "RestrictOSUpdates");
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, iOSResourceList);
                    NotificationHandler.getInstance().SendNotification(iOSResourceList, 1);
                    for (final Long collnID : collectionIds) {
                        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(iOSResourceList, collnID, 18, "mdm.db.osupdate.notification_sent");
                        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(unsupervisedList, collnID, 8, "mdm.db.osupdate.unsupervised_device");
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResListErrorCode(unsupervisedList, collnID, 29000);
                    }
                    CommonQueueUtil.getInstance().addToQueue(osPolicyData, CommonQueues.MDM_PROFILE_MGMT);
                }
            }
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "distributeOSUpdatePolicy successfully completed.");
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            for (final Long profileId : allProfileIds) {
                ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerId, groupIds, profileId, "mdm.actionlog.osupdate.association_groups_success", 2124, sUserName, "Group", new Long(System.currentTimeMillis()));
                ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerId, resourceIds, profileId, "mdm.actionlog.osupdate.association_device_success", 2124, sUserName, "Resource", new Long(System.currentTimeMillis()));
            }
        }
        catch (final Exception e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in distribute osupdate profile", e);
            throw e;
        }
        return null;
    }
    
    private List<Long> getUnsupervisedDevice(final List<Long> resourceList) {
        final List<Long> unsupervisedDevice = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            selectQuery.setCriteria(new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8).and(new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)false, 0)));
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                while (iterator.hasNext()) {
                    final Row deviceRow = iterator.next();
                    unsupervisedDevice.add((Long)deviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getUnsupervisedDevice", ex);
        }
        return unsupervisedDevice;
    }
    
    private void handleOSUpdateFiles(final List androidResourceList, final List<Long> collectionIds, final Long customerId, final Long userID, final Boolean associate) throws Exception {
        final SelectQuery sQuery = this.osUpdatePolicyDetailsQuery();
        final Criteria collectioncriteria = new Criteria(Column.getColumn("OSUpdatePolicy", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
        final Criteria filesCriteria = new Criteria(Column.getColumn("DeploymentPolicyFiles", "DOWNLOAD_FILE_LOCATION"), (Object)null, 1);
        sQuery.setCriteria(collectioncriteria.and(filesCriteria));
        sQuery.addSelectColumn(Column.getColumn("DeploymentPolicyFiles", "*"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(sQuery);
        final JSONArray docList = new JSONArray();
        final JSONArray resArray = new JSONArray();
        Iterator iterator = dataObject.getRows("DeploymentPolicyFiles");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            docList.put(row.get("DOC_ID"));
        }
        iterator = androidResourceList.iterator();
        while (iterator.hasNext()) {
            resArray.put(iterator.next());
        }
        if (docList.length() != 0) {
            final JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_id", (Object)userID);
            bodyJSON.put("user_name", (Object)DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID)));
            bodyJSON.put("docs", (Object)docList);
            bodyJSON.put("devices", (Object)resArray);
            bodyJSON.put("task", (Object)(associate ? "association" : "disassociation"));
            new DocAPIHandler().shareOrRemoveDocToDevices(bodyJSON);
        }
    }
    
    private <T> List<T> removeDuplicatesFromList(final List<T> listWithDuplicates) {
        final Set<T> hs = new HashSet<T>();
        hs.addAll((Collection<? extends T>)listWithDuplicates);
        listWithDuplicates.clear();
        listWithDuplicates.addAll((Collection<? extends T>)hs);
        return listWithDuplicates;
    }
    
    public JSONObject retryOSupdatePolicy(final JSONObject msgHeaderJSON, final JSONObject retryPolicyListJSON) throws Exception {
        final List<Long> profileIds = new ArrayList<Long>();
        final Long customerId = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
        if (retryPolicyListJSON.has("PROFILE_ID")) {
            profileIds.add(JSONUtil.optLongForUVH(retryPolicyListJSON, "PROFILE_ID", Long.valueOf(-1L)));
        }
        if (customerId != -1L && !ProfileUtil.getInstance().isCustomerEligible(customerId, profileIds)) {
            throw new ProfileException();
        }
        final HashSet resourceHash = new HashSet();
        final List<Long> resourceIds = new ArrayList<Long>();
        if (retryPolicyListJSON.has("RESOURCE_ID")) {
            resourceIds.add(JSONUtil.optLongForUVH(retryPolicyListJSON, "RESOURCE_ID", Long.valueOf(-1L)));
        }
        else if (retryPolicyListJSON.has("RESOURCE_IDS")) {
            final JSONArray profilesArray = retryPolicyListJSON.optJSONArray("RESOURCE_IDS");
            for (int i = 0; i < profilesArray.length(); ++i) {
                resourceIds.add(Long.valueOf(String.valueOf(profilesArray.get(i))));
            }
        }
        final int platformType = ProfileUtil.getInstance().getPlatformType(profileIds.get(0));
        HashMap deviceMap = null;
        if (retryPolicyListJSON.has("GROUP_IDS")) {
            final JSONArray groupArray = retryPolicyListJSON.optJSONArray("GROUP_IDS");
            final List groupIds = new ArrayList();
            for (int j = 0; j < groupArray.length(); ++j) {
                groupIds.add(Long.valueOf(groupArray.get(j).toString()));
            }
            deviceMap = MDMCustomGroupUtil.getInstance().getPlatformBasedMemberIdForGroups(groupIds);
            resourceHash.addAll(deviceMap.get(platformType));
        }
        HashMap resourceMap = null;
        if (!resourceIds.isEmpty()) {
            resourceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceIds);
            resourceHash.addAll(resourceMap.get(platformType));
        }
        final List<Long> resourceList = new ArrayList<Long>(resourceHash);
        Criteria criteria = null;
        if (platformType == 1) {
            criteria = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
        }
        final List applicableResource = this.getApplicableResourceToRetry(profileIds.get(0), resourceList, criteria);
        resourceList.removeAll(applicableResource);
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "Not applicable for OSUpdate Retry resourceList:{0}", new Object[] { resourceList });
        final CommonQueueData data = new CommonQueueData();
        data.setTaskName("OSUpdateScheduleTask");
        data.setClassName("com.me.mdm.server.updates.osupdates.task.OSUpdateScheduleTask");
        data.setCustomerId(customerId);
        final JSONObject details = new JSONObject();
        details.put("PROFILE_IDS", (Object)JSONUtil.getInstance().convertListToJSONArray(profileIds));
        details.put("RESOURCE_IDS", (Object)JSONUtil.getInstance().convertListToJSONArray(applicableResource));
        data.setJsonQueueData(details);
        final Map profileCollectionMap = this.getProfileCollectionIds(profileIds);
        final ArrayList<Long> collectionIds = new ArrayList<Long>(profileCollectionMap.values());
        for (final Long collnID : collectionIds) {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(applicableResource, collnID, 16, "mdm.osupdate.remarks.policyScheduled");
        }
        CommonQueueUtil.getInstance().addToQueue(data, CommonQueues.MDM_PROFILE_MGMT);
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "RetryOSupdatePolicy successfully completed.");
        return null;
    }
    
    public JSONObject removeDistributedOSUpdatePolicy(final JSONObject msgHeaderJSON, final JSONObject removeDistributedPolicyJSON) throws Exception {
        Long userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
        Long customerId = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "removeDistributedOSUpdatePolicy : {0}", removeDistributedPolicyJSON);
        try {
            final List<Long> profileIds = new ArrayList<Long>();
            List<Long> collectionIds = new ArrayList<Long>();
            final ArrayList<Long> groupIds = new ArrayList<Long>();
            final boolean notifyDevice = removeDistributedPolicyJSON.optBoolean("NOTIFY_DEVICE", true);
            final List resourceIds = new ArrayList();
            List membersList = new ArrayList();
            if (removeDistributedPolicyJSON.has("PROFILE_ID")) {
                profileIds.add(JSONUtil.optLongForUVH(removeDistributedPolicyJSON, "PROFILE_ID", Long.valueOf(-1L)));
            }
            else if (removeDistributedPolicyJSON.has("PROFILE_IDS")) {
                final JSONArray profilesArray = removeDistributedPolicyJSON.optJSONArray("PROFILE_IDS");
                for (int i = 0; i < profilesArray.length(); ++i) {
                    profileIds.add(Long.valueOf(String.valueOf(profilesArray.get(i))));
                }
            }
            final Map profileCollectionMap = this.getProfileCollectionIds(profileIds);
            collectionIds = new ArrayList<Long>(profileCollectionMap.values());
            if (removeDistributedPolicyJSON.has("GROUP_IDS")) {
                final JSONArray resources = removeDistributedPolicyJSON.getJSONArray("GROUP_IDS");
                for (int j = 0; j < resources.length(); ++j) {
                    groupIds.add(Long.valueOf(resources.get(j).toString()));
                }
                membersList = ProfileAssociateHandler.getMemberGroupsId(groupIds);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                final HashMap<Long, List> excludeProfileForGroup = handler.getGroupDeviceExcludeProfileMap(membersList, (HashMap)profileCollectionMap, groupIds);
                final HashMap<Long, List> excludeProfileForDevice = handler.getDeviceExcludeProfileMap(membersList, (HashMap)profileCollectionMap);
                for (final Long profileId : profileIds) {
                    final List groupExclude = excludeProfileForGroup.get(profileId);
                    if (groupExclude != null) {
                        membersList.removeAll(groupExclude);
                    }
                    final List deviceExclude = excludeProfileForDevice.get(profileId);
                    if (deviceExclude != null) {
                        membersList.removeAll(deviceExclude);
                    }
                }
            }
            if (removeDistributedPolicyJSON.has("DEVICE_IDS")) {
                final JSONArray resources = removeDistributedPolicyJSON.getJSONArray("DEVICE_IDS");
                for (int j = 0; j < resources.length(); ++j) {
                    resourceIds.add(Long.valueOf(resources.get(j).toString()));
                }
            }
            resourceIds.addAll(membersList);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "removeDistributedOSUpdatePolicy collectionIds : {0}", collectionIds);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "removeDistributedOSUpdatePolicy groupIds : {0}", groupIds);
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "removeDistributedOSUpdatePolicy : resourceIds {0}", resourceIds);
            if (!resourceIds.isEmpty()) {
                this.addOrUpdateRecentProfileForDevice(resourceIds, profileCollectionMap, true);
                this.addTargetIDsForCollectionProfile(resourceIds, collectionIds, "", 12);
            }
            if (!groupIds.isEmpty()) {
                this.addOrUpdateRecentProfileForGroup(groupIds, profileCollectionMap, true);
                this.updateGroupCollectionStatusSummary(groupIds, collectionIds);
            }
            final HashMap<Integer, Set> resMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceIds);
            final List androidResourceList = new ArrayList(resMap.get(2));
            if (!androidResourceList.isEmpty()) {
                final List collectionIdsCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "RemoveOsUpdatePolicy");
                DeviceCommandRepository.getInstance().assignCommandToDevices(collectionIdsCommandList, androidResourceList);
                if (notifyDevice) {
                    NotificationHandler.getInstance().SendNotification(androidResourceList, 2);
                }
                this.handleOSUpdateFiles(androidResourceList, collectionIds, customerId, userID, false);
            }
            final List chromeResourceList = new ArrayList(resMap.get(4));
            if (!chromeResourceList.isEmpty()) {
                final List collectionIdsCommandList2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "RemoveChromeOsUpdatePolicy");
                DeviceCommandRepository.getInstance().assignCommandToDevices(collectionIdsCommandList2, chromeResourceList);
                if (notifyDevice) {
                    NotificationHandler.getInstance().SendNotification(chromeResourceList, 4);
                }
            }
            final List iOSResourceList = new ArrayList(resMap.get(1));
            if (!iOSResourceList.isEmpty()) {
                final List collectionIdsCommandList3 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "ScheduleOSUpdate");
                DeviceCommandRepository.getInstance().deleteResourceCommand(iOSResourceList, collectionIdsCommandList3);
                SeqCmdUtils.getInstance().removeSeqInstallCmd(iOSResourceList, collectionIds, "ScheduleOSUpdate");
                new IOSOSUpdateHandler().checkAndRemoveRestrictOSUpdateCommand(iOSResourceList);
                final List removeCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIds, "RemoveRestrictOSUpdates");
                DeviceCommandRepository.getInstance().assignCommandToDevices(removeCommandList, iOSResourceList);
                if (notifyDevice) {
                    NotificationHandler.getInstance().SendNotification(iOSResourceList, 1);
                }
                this.resetCollectionToResourceStatus(iOSResourceList, collectionIds, 18, "mdm.osupdate.remark.removepolicy");
            }
            final Boolean eventLog = msgHeaderJSON.optBoolean("REMOVE_EVENT_LOG");
            if (!eventLog) {
                userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
                customerId = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
                final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
                for (final Long profileId2 : profileIds) {
                    ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerId, groupIds, profileId2, "mdm.actionlog.osupdate.disassociation_groups_success", 2126, sUserName, "Group", new Long(System.currentTimeMillis()));
                    ProfileAssociateHandler.getInstance().addProfileActionEventLogEntry(customerId, resourceIds, profileId2, "mdm.actionlog.osupdate.disassociation_device_success", 2126, sUserName, "Resource", new Long(System.currentTimeMillis()));
                }
            }
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "removeDistributedOSUpdatePolicy successfully completed.");
            return removeDistributedPolicyJSON;
        }
        catch (final DataAccessException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Can't able perform DB operation in remove device osupdate policy", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid params for remove device osupdate policy", (Throwable)e2);
            throw e2;
        }
        catch (final SyMException e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getting member Id for remove device osupdate policy", (Throwable)e3);
            throw e3;
        }
        catch (final Exception e4) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in remove device osupdate policy", e4);
            throw e4;
        }
    }
    
    public List<Long> getManagedDevicesAssignedForProfiles(final List profileIds) throws DataAccessException, QueryConstructionException {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("RecentProfileForResource"));
        final Criteria criteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
        final Criteria removedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        query.setCriteria(criteria.and(removedCriteria));
        query.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
        query.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
        query.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        final Iterator it = dataObject.getRows("RecentProfileForResource");
        final List<Long> managedDevicesList = new ArrayList<Long>();
        while (it.hasNext()) {
            final Row row = it.next();
            managedDevicesList.add((Long)row.get("RESOURCE_ID"));
        }
        return managedDevicesList;
    }
    
    public List<Long> getManagedGroupsAssignedForProfiles(final List profileIds) throws DataAccessException, QueryConstructionException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        final Criteria criteria = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
        final Criteria removedCriteria = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        query.setCriteria(criteria.and(removedCriteria));
        query.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
        query.addSelectColumn(new Column("RecentProfileForGroup", "COLLECTION_ID"));
        query.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        final Iterator it = dataObject.getRows("RecentProfileForGroup");
        final List<Long> managedGroupsList = new ArrayList<Long>();
        while (it.hasNext()) {
            final Row row = it.next();
            managedGroupsList.add((Long)row.get("GROUP_ID"));
        }
        return managedGroupsList;
    }
    
    public void deleteRecentProfileForResourceListCollection(final List resourceList, final List collectionList) throws DataAccessException {
        final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8, (boolean)Boolean.FALSE);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionList.toArray(), 8, (boolean)Boolean.FALSE);
        DataAccess.delete("RecentProfileForResource", collectionCriteria.and(resourceCriteria));
    }
    
    public void resetCollectionToResourceStatus(final List resourceList, final List collectionIds, final int status, final String remarks) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
        Criteria resourceCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        Criteria collectionCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
        updateQuery.setCriteria(resourceCriteria.and(collectionCriteria));
        updateQuery.setUpdateColumn("STATUS", (Object)status);
        updateQuery.setUpdateColumn("APPLIED_TIME", (Object)System.currentTimeMillis());
        updateQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
        if (remarks != null) {
            updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
            updateQuery.setUpdateColumn("REMARKS_EN", (Object)remarks);
        }
        MDMUtil.getPersistence().update(updateQuery);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MDMCollnToResErrorCode");
        resourceCriteria = new Criteria(Column.getColumn("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        collectionCriteria = new Criteria(Column.getColumn("MDMCollnToResErrorCode", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
        deleteQuery.setCriteria(resourceCriteria.and(collectionCriteria));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    public void updateGroupCollectionStatusSummary(final List groupList, final List collectionList) {
        if (groupList.size() > 0) {
            for (int i = 0; i < groupList.size(); ++i) {
                for (int k = 0; k < collectionList.size(); ++k) {
                    GroupCollectionStatusSummary.getInstance().addOrUpdateGroupCollectionStatusSummary(groupList.get(i), collectionList.get(k));
                }
            }
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
    }
    
    public Map<Long, Long> getProfileCollectionIds(final List profileIds) throws Exception {
        final Map<Long, Long> profileMap = new HashMap<Long, Long>();
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileToColln", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
        final DataObject dataObject = DataAccess.get("RecentProfileToColln", criteria);
        Row recentProfileToCollectionRow = null;
        Long profileID = null;
        Long collectionID = null;
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("RecentProfileToColln");
            while (iterator.hasNext()) {
                recentProfileToCollectionRow = iterator.next();
                profileID = (Long)recentProfileToCollectionRow.get("PROFILE_ID");
                collectionID = (Long)recentProfileToCollectionRow.get("COLLECTION_ID");
                profileMap.put(profileID, collectionID);
            }
        }
        return profileMap;
    }
    
    public void addTargetIDsForCollectionProfile(final List resourceIds, final List collectionIds, final String remarks, final Integer status) throws Exception {
        try {
            if (!resourceIds.isEmpty() && !collectionIds.isEmpty()) {
                final SelectQuery collnQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
                final Join colnToErrorCodeJoin = new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
                Criteria collectionCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
                final Criteria resourceCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
                Criteria criteria = resourceCriteria.and(collectionCriteria);
                collnQuery.addJoin(colnToErrorCodeJoin);
                collnQuery.setCriteria(criteria);
                collnQuery.addSelectColumn(new Column("CollnToResources", "*"));
                collnQuery.addSelectColumn(new Column("MDMCollnToResErrorCode", "*"));
                final DataObject dataObject = MDMUtil.getPersistence().get(collnQuery);
                Criteria resourceIDCriteria = null;
                Row collectionRow = null;
                Long resourceID = null;
                Long collectionID = null;
                for (int j = 0; j < resourceIds.size(); ++j) {
                    resourceID = resourceIds.get(j);
                    resourceIDCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
                    for (int k = 0; k < collectionIds.size(); ++k) {
                        collectionID = collectionIds.get(k);
                        collectionCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
                        criteria = resourceIDCriteria.and(collectionCriteria);
                        if (!dataObject.isEmpty()) {
                            collectionRow = dataObject.getRow("CollnToResources", criteria);
                        }
                        if (collectionRow == null) {
                            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "Inserting resource {0} and collection {1} in CollnToResources", new Object[] { resourceID, collectionID });
                            collectionRow = new Row("CollnToResources");
                            collectionRow.set("COLLECTION_ID", (Object)collectionID);
                            collectionRow.set("RESOURCE_ID", (Object)resourceID);
                            collectionRow.set("STATUS", (Object)status);
                            collectionRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                            collectionRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                            collectionRow.set("REMARKS", (Object)remarks);
                            collectionRow.set("REMARKS_EN", (Object)remarks);
                            dataObject.addRow(collectionRow);
                        }
                        else {
                            collectionRow.set("STATUS", (Object)status);
                            collectionRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                            collectionRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                            collectionRow.set("REMARKS", (Object)remarks);
                            collectionRow.set("REMARKS_EN", (Object)remarks);
                            dataObject.updateRow(collectionRow);
                        }
                    }
                }
                dataObject.deleteRows("MDMCollnToResErrorCode", (Row)null);
                DataAccess.update(dataObject);
            }
        }
        catch (final Exception ex2) {
            throw new SyMException(1001, (Throwable)ex2);
        }
    }
    
    public void addOrUpdateRecentProfileForDevice(final List deviceList, final Map profileCollectionList, final Boolean markForDelete) throws Exception {
        final List profileList = new ArrayList(profileCollectionList.keySet());
        final Column deviceIDColumn = new Column("RecentProfileForResource", "RESOURCE_ID");
        final Column profileIDColumn = new Column("RecentProfileForResource", "PROFILE_ID");
        Criteria deviceIdCriteria = new Criteria(deviceIDColumn, (Object)deviceList.toArray(), 8);
        Criteria profileCriteria = new Criteria(profileIDColumn, (Object)profileList.toArray(), 8);
        Criteria criteria = deviceIdCriteria.and(profileCriteria);
        final DataObject groupProfileCheckDO = MDMUtil.getPersistence().get("RecentProfileForResource", criteria);
        Row collectionRow = null;
        Long deviceID = null;
        Long profileID = null;
        Long collectionID = null;
        for (int j = 0; j < deviceList.size(); ++j) {
            deviceID = deviceList.get(j);
            deviceIdCriteria = new Criteria(deviceIDColumn, (Object)deviceID, 0);
            for (int k = 0; k < profileList.size(); ++k) {
                profileID = profileList.get(k);
                collectionID = profileCollectionList.get(profileID);
                profileCriteria = new Criteria(profileIDColumn, (Object)profileID, 0);
                criteria = deviceIdCriteria.and(profileCriteria);
                if (!groupProfileCheckDO.isEmpty()) {
                    collectionRow = groupProfileCheckDO.getRow("RecentProfileForResource", criteria);
                }
                if (collectionRow == null) {
                    collectionRow = new Row("RecentProfileForResource");
                    collectionRow.set("RESOURCE_ID", (Object)deviceID);
                    collectionRow.set("PROFILE_ID", (Object)profileID);
                    collectionRow.set("COLLECTION_ID", (Object)collectionID);
                    collectionRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                    groupProfileCheckDO.addRow(collectionRow);
                }
                else {
                    collectionRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                    collectionRow.set("COLLECTION_ID", (Object)collectionID);
                    groupProfileCheckDO.updateRow(collectionRow);
                }
            }
        }
        DataAccess.update(groupProfileCheckDO);
    }
    
    public void addOrUpdateResourceToProfileHistory(final List deviceList, final Map profileCollectionList, final Long userId) throws Exception {
        final List profileList = new ArrayList(profileCollectionList.keySet());
        final List collnList = new ArrayList(profileCollectionList.values());
        final Column deviceIDColumn = new Column("ResourceToProfileHistory", "RESOURCE_ID");
        final Column profileIDColumn = new Column("ResourceToProfileHistory", "PROFILE_ID");
        final Column collnIDColumn = new Column("ResourceToProfileHistory", "COLLECTION_ID");
        Criteria deviceIdCriteria = new Criteria(deviceIDColumn, (Object)deviceList.toArray(), 8);
        Criteria profileCriteria = new Criteria(profileIDColumn, (Object)profileList.toArray(), 8);
        final Criteria collnIDcriteria = new Criteria(collnIDColumn, (Object)collnList.toArray(), 8);
        Criteria criteria = deviceIdCriteria.and(profileCriteria).and(collnIDcriteria);
        final DataObject resToProfileHistoryDO = MDMUtil.getPersistence().get("ResourceToProfileHistory", criteria);
        Row historyRow = null;
        Long deviceID = null;
        Long profileID = null;
        Long collectionID = null;
        for (int j = 0; j < deviceList.size(); ++j) {
            deviceID = deviceList.get(j);
            deviceIdCriteria = new Criteria(deviceIDColumn, (Object)deviceID, 0);
            for (int k = 0; k < profileList.size(); ++k) {
                profileID = profileList.get(k);
                collectionID = profileCollectionList.get(profileID);
                profileCriteria = new Criteria(profileIDColumn, (Object)profileID, 0);
                criteria = deviceIdCriteria.and(profileCriteria);
                if (!resToProfileHistoryDO.isEmpty()) {
                    historyRow = resToProfileHistoryDO.getRow("ResourceToProfileHistory", criteria);
                }
                if (historyRow == null) {
                    historyRow = new Row("ResourceToProfileHistory");
                    historyRow.set("RESOURCE_ID", (Object)deviceID);
                    historyRow.set("PROFILE_ID", (Object)profileID);
                    historyRow.set("COLLECTION_ID", (Object)collectionID);
                    historyRow.set("ASSOCIATED_BY", (Object)userId);
                    historyRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("PROFILE_ORIGIN_TYPE", (Object)false);
                    historyRow.set("REMARKS", (Object)"");
                    resToProfileHistoryDO.addRow(historyRow);
                }
                else {
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("PROFILE_ORIGIN_TYPE", (Object)false);
                    historyRow.set("REMARKS", (Object)"");
                    resToProfileHistoryDO.updateRow(historyRow);
                }
            }
        }
        DataAccess.update(resToProfileHistoryDO);
    }
    
    public void addOrUpdateGroupToProfileHistory(final List groupList, final Map profileCollectionList, final Long userId) throws Exception {
        final List profileList = new ArrayList(profileCollectionList.keySet());
        final List collnList = new ArrayList(profileCollectionList.values());
        final Column deviceIDColumn = new Column("GroupToProfileHistory", "GROUP_ID");
        final Column profileIDColumn = new Column("GroupToProfileHistory", "PROFILE_ID");
        final Column collnIDColumn = new Column("GroupToProfileHistory", "COLLECTION_ID");
        Criteria deviceIdCriteria = new Criteria(deviceIDColumn, (Object)groupList.toArray(), 8);
        Criteria profileCriteria = new Criteria(profileIDColumn, (Object)profileList.toArray(), 8);
        final Criteria collnCriteria = new Criteria(collnIDColumn, (Object)collnList.toArray(), 8);
        Criteria criteria = deviceIdCriteria.and(profileCriteria).and(collnCriteria);
        final DataObject resToProfileHistoryDO = MDMUtil.getPersistence().get("GroupToProfileHistory", criteria);
        Row historyRow = null;
        Long deviceID = null;
        Long profileID = null;
        Long collectionID = null;
        for (int j = 0; j < groupList.size(); ++j) {
            deviceID = groupList.get(j);
            deviceIdCriteria = new Criteria(deviceIDColumn, (Object)deviceID, 0);
            for (int k = 0; k < profileList.size(); ++k) {
                profileID = profileList.get(k);
                collectionID = profileCollectionList.get(profileID);
                profileCriteria = new Criteria(profileIDColumn, (Object)profileID, 0);
                criteria = deviceIdCriteria.and(profileCriteria);
                if (!resToProfileHistoryDO.isEmpty()) {
                    historyRow = resToProfileHistoryDO.getRow("GroupToProfileHistory", criteria);
                }
                if (historyRow == null) {
                    historyRow = new Row("GroupToProfileHistory");
                    historyRow.set("GROUP_ID", (Object)deviceID);
                    historyRow.set("PROFILE_ID", (Object)profileID);
                    historyRow.set("COLLECTION_ID", (Object)collectionID);
                    historyRow.set("ASSOCIATED_BY", (Object)userId);
                    historyRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("REMARKS", (Object)"");
                    resToProfileHistoryDO.addRow(historyRow);
                }
                else {
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    resToProfileHistoryDO.updateRow(historyRow);
                }
            }
        }
        DataAccess.update(resToProfileHistoryDO);
    }
    
    public void addOrUpdateRecentProfileForGroup(final List groupList, final Map profileCollectionList, final Boolean markForDelete) throws Exception {
        final List profileList = new ArrayList(profileCollectionList.keySet());
        final Column groupIdColumn = new Column("RecentProfileForGroup", "GROUP_ID");
        final Column profileColumn = new Column("RecentProfileForGroup", "PROFILE_ID");
        Criteria groupIdCriteria = new Criteria(groupIdColumn, (Object)groupList.toArray(), 8);
        Criteria profileCriteria = new Criteria(profileColumn, (Object)profileList.toArray(), 8);
        Criteria criteria = groupIdCriteria.and(profileCriteria);
        final DataObject deviceProfileCheckDO = MDMUtil.getPersistence().get("RecentProfileForGroup", criteria);
        Row collectionRow = null;
        for (int j = 0; j < groupList.size(); ++j) {
            groupIdCriteria = new Criteria(groupIdColumn, groupList.get(j), 0);
            for (int k = 0; k < profileList.size(); ++k) {
                profileCriteria = new Criteria(profileColumn, profileList.get(k), 0);
                criteria = groupIdCriteria.and(profileCriteria);
                if (!deviceProfileCheckDO.isEmpty()) {
                    collectionRow = deviceProfileCheckDO.getRow("RecentProfileForGroup", criteria);
                }
                if (collectionRow == null) {
                    collectionRow = new Row("RecentProfileForGroup");
                    collectionRow.set("GROUP_ID", groupList.get(j));
                    collectionRow.set("PROFILE_ID", profileList.get(k));
                    collectionRow.set("COLLECTION_ID", profileCollectionList.get(profileList.get(k)));
                    collectionRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                    deviceProfileCheckDO.addRow(collectionRow);
                }
                else {
                    collectionRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                    collectionRow.set("COLLECTION_ID", profileCollectionList.get(profileList.get(k)));
                    deviceProfileCheckDO.updateRow(collectionRow);
                }
            }
        }
        DataAccess.update(deviceProfileCheckDO);
    }
    
    public List<Long> getRecentProfileCollectionIDs(final List profileIds) {
        List collectionIds = null;
        try {
            if (!profileIds.isEmpty()) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileToColln"));
                final Join collectionJoin = new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
                selectQuery.addJoin(collectionJoin);
                final Criteria criteria = new Criteria(Column.getColumn("RecentProfileToColln", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
                selectQuery.setCriteria(criteria);
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"));
                selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "COLLECTION_ID"));
                selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
                final DataObject profileCollectionStatusDO = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = profileCollectionStatusDO.getRows("CollectionStatus");
                collectionIds = DBUtil.getColumnValuesAsList(iterator, "COLLECTION_ID");
            }
        }
        catch (final Exception exp) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getRecentProfileCollectionIDs", exp);
        }
        return collectionIds;
    }
    
    public List getPlatformTargetResourceIds(final List collectionIds, final int platformType) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(managedDeviceJoin);
        final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
        final Criteria managedDevice = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria isAssociated = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(platformCriteria.and(collectionCriteria).and(managedDevice).and(isAssociated));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Iterator itr = dataObject.getRows("ManagedDevice");
        return DBUtil.getColumnValuesAsList(itr, "RESOURCE_ID");
    }
    
    public ArrayList getRecentOSUpdatePoliciesByDevice(final Long deviceResId) {
        ArrayList osUpdatePoliciesByDevice = null;
        try {
            final SelectQuery osUpdatePoliciesByDeviceQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            osUpdatePoliciesByDeviceQuery.addJoin(profileJoin);
            final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria groupCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceResId, 0);
            osUpdatePoliciesByDeviceQuery.setCriteria(markedForDeleteCriteria.and(groupCriteria));
            osUpdatePoliciesByDeviceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            osUpdatePoliciesByDeviceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            osUpdatePoliciesByDeviceQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            final DataObject dataObject = DataAccess.get(osUpdatePoliciesByDeviceQuery);
            final Iterator iterator = dataObject.getRows("Profile");
            osUpdatePoliciesByDevice = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "PROFILE_ID");
        }
        catch (final Exception ex) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getRecentOSUpdatePoliciesByDevice", ex);
        }
        return osUpdatePoliciesByDevice;
    }
    
    public ArrayList getRecentOSUpdatePoliciesByGroup(final Long groupResId) {
        ArrayList osUpdatePoliciesByGroup = null;
        try {
            final SelectQuery osUpdatePoliciesByGroupQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForGroup"));
            final Join profileJoin = new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            osUpdatePoliciesByGroupQuery.addJoin(profileJoin);
            final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria groupCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupResId, 0);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)3, 0);
            osUpdatePoliciesByGroupQuery.setCriteria(markedForDeleteCriteria.and(groupCriteria).and(profileTypeCriteria));
            osUpdatePoliciesByGroupQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            osUpdatePoliciesByGroupQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
            osUpdatePoliciesByGroupQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            final DataObject dataObject = DataAccess.get(osUpdatePoliciesByGroupQuery);
            final Iterator iterator = dataObject.getRows("Profile");
            osUpdatePoliciesByGroup = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "PROFILE_ID");
        }
        catch (final Exception ex) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getRecentOSUpdatePoliciesByGroup", ex);
        }
        return osUpdatePoliciesByGroup;
    }
    
    public JSONObject trashOSUpdatePolicy(final JSONObject msgHeaderJSON, final JSONObject trashPolicyJSON) throws Exception {
        try {
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "trashOSUpdatePolicy : {0}", trashPolicyJSON);
            final Long userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
            final Long customerID = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
            final List<Long> profileIds = new ArrayList<Long>();
            if (trashPolicyJSON.has("PROFILE_ID")) {
                profileIds.add(JSONUtil.optLongForUVH(trashPolicyJSON, "PROFILE_ID", Long.valueOf(-1L)));
            }
            else if (trashPolicyJSON.has("PROFILE_IDS")) {
                final JSONArray profilesArray = trashPolicyJSON.optJSONArray("PROFILE_IDS");
                for (int i = 0; i < profilesArray.length(); ++i) {
                    profileIds.add(Long.valueOf(String.valueOf(profilesArray.get(i))));
                }
            }
            if (customerID != -1L && !ProfileUtil.getInstance().isCustomerEligible(customerID, profileIds)) {
                throw new ProfileException();
            }
            final List<Long> managedGroupsAssignedForProfile = this.getManagedGroupsAssignedForProfiles(profileIds);
            if (!managedGroupsAssignedForProfile.isEmpty()) {
                final JSONArray groupsArray = new JSONArray((Collection)managedGroupsAssignedForProfile);
                trashPolicyJSON.put("GROUP_IDS", (Object)groupsArray);
            }
            final List<Long> managedDevicesAssignedForProfile = this.getManagedDevicesAssignedForProfiles(profileIds);
            if (!managedDevicesAssignedForProfile.isEmpty()) {
                final JSONArray devicesArray = new JSONArray((Collection)managedDevicesAssignedForProfile);
                trashPolicyJSON.put("DEVICE_IDS", (Object)devicesArray);
            }
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("Profile");
            query.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria profileIdCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
            final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria criteria = profileIdCriteria.and(customerCriteria);
            query.setCriteria(criteria);
            query.setUpdateColumn("LAST_MODIFIED_BY", (Object)userID);
            query.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)Boolean.TRUE);
            query.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            DataAccess.update(query);
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            this.handleOSUpdateFilesTrashing(profileIds, customerID, userID, sUserName);
            this.removeDistributedOSUpdatePolicy(msgHeaderJSON, trashPolicyJSON);
            final List profileName = DBUtil.getDistinctColumnValue("Profile", "PROFILE_NAME", profileIdCriteria);
            for (int j = 0; j < profileName.size(); ++j) {
                final Object remarksArgs = profileName.get(j).toString() + "@@@" + sUserName;
                final String sEventLogRemarks = "mdm.actionlog.osupdate.trash_success";
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2127, null, sUserName, sEventLogRemarks, remarksArgs, customerID);
            }
        }
        catch (final JSONException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in json params", (Throwable)e);
            throw e;
        }
        catch (final ProfileException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invaild access in trashOSUpdate policy", (Throwable)e2);
            throw e2;
        }
        catch (final DataAccessException | QueryConstructionException e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "DB operation failed in trashOSUpdate policy", e3);
            throw e3;
        }
        catch (final Exception e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in trashOSUpdate policy", e3);
            throw e3;
        }
        return null;
    }
    
    private void handleOSUpdateFilesTrashing(final List<Long> profileIds, final Long customerID, final Long userID, final String sUserName) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OSUpdatePolicy"));
        final Join recentProfileJoin = new Join("OSUpdatePolicy", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join profileJoin = new Join("RecentProfileToColln", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join depTempJoin = new Join("OSUpdatePolicy", "DeploymentTempToOSUpdate", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
        final Join templateJoin = new Join("DeploymentTempToOSUpdate", "MdmDeploymentTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join notifyTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentNotifTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join windowTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentWindowTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join settingsTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentPolicySettings", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        final Join filesTemplateJoin = new Join("DeploymentTempToOSUpdate", "DeploymentPolicyFiles", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1);
        selectQuery.addJoin(recentProfileJoin);
        selectQuery.addJoin(profileJoin);
        selectQuery.addJoin(depTempJoin);
        selectQuery.addJoin(templateJoin);
        selectQuery.addJoin(notifyTemplateJoin);
        selectQuery.addJoin(windowTemplateJoin);
        selectQuery.addJoin(settingsTemplateJoin);
        selectQuery.addJoin(filesTemplateJoin);
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileToColln", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONArray docIDs = new JSONArray();
        if (!dataObject.isEmpty() && dataObject.containsTable("DeploymentPolicyFiles")) {
            final Iterator iterator = dataObject.getRows("DeploymentPolicyFiles");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long docID = (Long)row.get("DOC_ID");
                docIDs.put((Object)docID);
            }
        }
        if (docIDs.length() > 0) {
            final JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("customer_id", (Object)customerID);
            bodyJSON.put("user_id", (Object)userID);
            bodyJSON.put("user_name", (Object)sUserName);
            bodyJSON.put("docs", (Object)docIDs);
            new DocAPIHandler().deleteDoc(bodyJSON);
        }
    }
    
    public void restoreOSUpdatePolicy(final JSONObject msgHeaderJSON, final JSONObject deleteJSON) throws Exception {
        try {
            final Long userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
            final Long customerID = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
            final List<Long> profileIds = new ArrayList<Long>();
            if (deleteJSON.has("PROFILE_ID")) {
                profileIds.add(JSONUtil.optLongForUVH(deleteJSON, "PROFILE_ID", Long.valueOf(-1L)));
            }
            else if (deleteJSON.has("PROFILE_IDS")) {
                final JSONArray profilesArray = deleteJSON.optJSONArray("PROFILE_IDS");
                for (int i = 0; i < profilesArray.length(); ++i) {
                    profileIds.add(Long.valueOf(String.valueOf(profilesArray.get(i))));
                }
            }
            if (customerID != -1L && !ProfileUtil.getInstance().isCustomerEligible(customerID, profileIds)) {
                throw new ProfileException();
            }
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("Profile");
            query.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria profileIdCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
            final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria criteria = profileIdCriteria.and(customerCriteria);
            query.setCriteria(criteria);
            query.setUpdateColumn("LAST_MODIFIED_BY", (Object)userID);
            query.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)Boolean.FALSE);
            query.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            DataAccess.update(query);
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            final List profileName = DBUtil.getDistinctColumnValue("Profile", "PROFILE_NAME", profileIdCriteria);
            for (int j = 0; j < profileName.size(); ++j) {
                final Object remarksArgs = profileName.get(j).toString() + "@@@" + sUserName;
                final String sEventLogRemarks = "mdm.actionlog.osupdate.restore_success";
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2128, null, sUserName, sEventLogRemarks, remarksArgs, customerID);
            }
        }
        catch (final DataAccessException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in dboperation on restore osupdate", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid post params / json error in restore osupdate", (Throwable)e2);
            throw e2;
        }
        catch (final ProfileException e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid access to restore the osupdate", (Throwable)e3);
            throw e3;
        }
        catch (final Exception e4) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in restore OSUpdate", e4);
            throw e4;
        }
    }
    
    public void deleteOSUpdatePolicy(final JSONObject msgHeaderJSON, final JSONObject detailsJSON) throws Exception {
        try {
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "OSUpdate policy deleteaction. ResourceJSON:{0}", new Object[] { detailsJSON });
            final Long userID = JSONUtil.optLongForUVH(msgHeaderJSON, "USER_ID", Long.valueOf(-1L));
            final Long customerID = JSONUtil.optLongForUVH(msgHeaderJSON, "CUSTOMER_ID", Long.valueOf(-1L));
            final List<Long> profileIds = new ArrayList<Long>();
            if (detailsJSON.has("PROFILE_ID")) {
                profileIds.add(JSONUtil.optLongForUVH(detailsJSON, "PROFILE_ID", Long.valueOf(-1L)));
            }
            else if (detailsJSON.has("PROFILE_IDS")) {
                final JSONArray profilesArray = detailsJSON.optJSONArray("PROFILE_IDS");
                for (int i = 0; i < profilesArray.length(); ++i) {
                    profileIds.add(Long.valueOf(String.valueOf(profilesArray.get(i))));
                }
            }
            if (customerID != -1L && !ProfileUtil.getInstance().isCustomerEligible(customerID, profileIds)) {
                throw new ProfileException();
            }
            if (!ProfileAssociateHandler.getInstance().isProfileDeleteSafe(profileIds)) {
                throw new APIHTTPException("PAY0007", new Object[0]);
            }
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Profile");
            final Criteria profileIdCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
            deleteQuery.setCriteria(profileIdCriteria);
            MDMUtil.getPersistenceLite().delete(deleteQuery);
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            final List profileName = DBUtil.getDistinctColumnValue("Profile", "PROFILE_NAME", profileIdCriteria);
            for (int j = 0; j < profileName.size(); ++j) {
                final Object remarksArgs = profileName.get(j).toString() + "@@@" + sUserName;
                final String sEventLogRemarks = "mdm.actionlog.osupdate.delete_success";
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2123, null, sUserName, sEventLogRemarks, remarksArgs, customerID);
            }
        }
        catch (final DataAccessException | QueryConstructionException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "DB operation error exception", e);
            throw e;
        }
        catch (final JSONException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid JSON params", (Throwable)e2);
            throw e2;
        }
        catch (final ProfileException e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid access for delete", (Throwable)e3);
            throw e3;
        }
        catch (final Exception e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in adding action logger", e);
            throw e;
        }
    }
    
    public boolean checkOSPolicyNameExist(final Long customerId, final JSONObject dataJson) {
        boolean isExist = false;
        try {
            final JSONObject profileJSON = dataJson.getJSONObject("Profile");
            final Long profileID = JSONUtil.optLongForUVH(profileJSON, "PROFILE_ID", Long.valueOf(-1L));
            final String profileName = String.valueOf(profileJSON.get("PROFILE_NAME"));
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "*** checkOSPolicyNameExist inputs: CustomerId:{0}; Profilename:{1}", new Object[] { customerId, profileName });
            if (customerId != null && profileName != null && !profileName.isEmpty()) {
                final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria profilenameCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)profileName.trim(), 0);
                final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)3, 0);
                final Criteria profileIdCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 1);
                final Criteria profileMoveToTrashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
                final Criteria criteria = customerCriteria.and(profilenameCriteria).and(profileTypeCriteria).and(profileIdCriteria).and(profileMoveToTrashCriteria);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                final Join customerToProfJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                selectQuery.addJoin(customerToProfJoin);
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
                selectQuery.setCriteria(criteria);
                final DataObject profNameDO = MDMUtil.getPersistence().get(selectQuery);
                if (profNameDO != null && !profNameDO.isEmpty()) {
                    isExist = true;
                }
            }
        }
        catch (final Exception ex) {
            OSUpdatePolicyHandler.LOGGER.log(Level.WARNING, "Exception occurred while checkOSPolicyNameExist {0}", ex);
        }
        OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "**checkProfileNameExist ** IS OS POLICY NAME EXIST: {0}", isExist);
        return isExist;
    }
    
    public List getCollectionsAssociatedToResource(final Long resourceID) {
        List collectionIds = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            final Join join = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(join);
            final SelectQuery resourcePlatformTypeQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            resourcePlatformTypeQuery.setCriteria(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
            resourcePlatformTypeQuery.addSelectColumn(new Column("ManagedDevice", "PLATFORM_TYPE"));
            final DerivedColumn derivedColumn = new DerivedColumn("derived", resourcePlatformTypeQuery);
            final Criteria resC = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria profileTypeC = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)3, 0);
            final Criteria platformTypeCriteria = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)derivedColumn, 0);
            selectQuery.setCriteria(resC.and(profileTypeC).and(platformTypeCriteria));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dO.getRows("RecentProfileForResource");
            collectionIds = DBUtil.getColumnValuesAsList(iterator, "COLLECTION_ID");
        }
        catch (final Exception exp) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getCollectionsAssociatedToResource", exp);
        }
        return collectionIds;
    }
    
    public Long getUserIdWhoAssociatedProfileToGroup(final Long groupId, final Long profileId) throws DataAccessException {
        final Column deviceIDColumn = new Column("GroupToProfileHistory", "GROUP_ID");
        final Column profileIDColumn = new Column("GroupToProfileHistory", "PROFILE_ID");
        final Criteria deviceIdCriteria = new Criteria(deviceIDColumn, (Object)groupId, 0);
        final Criteria profileCriteria = new Criteria(profileIDColumn, (Object)profileId, 0);
        final Criteria criteria = deviceIdCriteria.and(profileCriteria);
        final DataObject resToProfileHistoryDO = MDMUtil.getPersistence().get("GroupToProfileHistory", criteria);
        if (!resToProfileHistoryDO.isEmpty()) {
            return (Long)resToProfileHistoryDO.getFirstRow("GroupToProfileHistory").get("ASSOCIATED_BY");
        }
        throw new RuntimeException("Policy not Associated to the given group");
    }
    
    public SelectQuery osUpdatePolicyDetailsQuery() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("OSUpdatePolicy"));
        sQuery.addJoin(new Join("OSUpdatePolicy", "DeploymentTempToOSUpdate", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        sQuery.addJoin(new Join("DeploymentTempToOSUpdate", "MdmDeploymentTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("MdmDeploymentTemplate", "DeploymentWindowTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("MdmDeploymentTemplate", "DeploymentNotifTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("MdmDeploymentTemplate", "DeploymentPolicySettings", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("MdmDeploymentTemplate", "DeploymentPolicyFiles", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("OSUpdatePolicy", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("RecentProfileToColln", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        sQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        return sQuery;
    }
    
    public JSONObject getAllOSUpdatePolicy(final Integer startIndex, final Integer limit, final Boolean isTrashed, final Long timeStamp, final Long customerId, final String search) throws Exception {
        final JSONObject response = new JSONObject();
        try {
            final JSONArray result = new JSONArray();
            final SelectQuery sQuery = this.osUpdatePolicyDetailsQuery();
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "DEFER_DAYS"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "POLICY_TYPE"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "EXPIRY_TIME"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "RELEASE_CHANNEL"));
            sQuery.addSelectColumn(new Column("DeploymentTempToOSUpdate", "DEPLOYMENT_TEMPLATE_ID"));
            sQuery.addSelectColumn(new Column("DeploymentWindowTemplate", "WINDOW_DAY_OF_WEEK"));
            sQuery.addSelectColumn(new Column("DeploymentWindowTemplate", "WINDOW_END_TIME"));
            sQuery.addSelectColumn(new Column("DeploymentWindowTemplate", "WINDOW_START_TIME"));
            sQuery.addSelectColumn(new Column("DeploymentWindowTemplate", "WINDOW_WEEK_OF_MONTH"));
            sQuery.addSelectColumn(new Column("DeploymentNotifTemplate", "ALLOW_USERS_TO_SKIP"));
            sQuery.addSelectColumn(new Column("DeploymentNotifTemplate", "MAX_SKIPS_ALLOWED"));
            sQuery.addSelectColumn(new Column("DeploymentNotifTemplate", "NOTIFY_MESSAGE"));
            sQuery.addSelectColumn(new Column("DeploymentNotifTemplate", "NOTIFY_TITLE"));
            sQuery.addSelectColumn(new Column("DeploymentPolicySettings", "REBOOT_AFTER_UPDATE"));
            sQuery.addSelectColumn(new Column("DeploymentPolicySettings", "MAX_TARGET_PREFIX"));
            sQuery.addSelectColumn(new Column("DeploymentPolicyFiles", "DOWNLOAD_FILE_LOCATION"));
            sQuery.addSelectColumn(new Column("DeploymentPolicyFiles", "DOC_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileToColln", "PROFILE_ID"));
            sQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
            sQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
            sQuery.addSelectColumn(new Column("Profile", "IS_MOVED_TO_TRASH"));
            sQuery.addSortColumn(new SortColumn("Profile", "PROFILE_ID", true));
            Criteria criteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            if (isTrashed != null) {
                criteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)isTrashed, 0);
            }
            if (timeStamp != null) {
                final Criteria timeCriteria = new Criteria(new Column("Profile", "LAST_MODIFIED_TIME"), (Object)timeStamp, 5);
                if (criteria != null) {
                    criteria = criteria.and(timeCriteria);
                }
                else {
                    criteria = timeCriteria;
                }
            }
            final Criteria filterCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)search, 12, false);
            if (search != null) {
                criteria = ((criteria == null) ? filterCriteria : criteria.and(filterCriteria));
            }
            if (criteria != null) {
                sQuery.setCriteria(criteria);
            }
            sQuery.setRange(new Range((int)startIndex, (int)limit));
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (dataSetWrapper.next()) {
                final JSONObject osUpdateJSON = new JSONObject();
                final JSONObject osUpdateProfile = new JSONObject();
                final JSONObject osUpdateNotify = new JSONObject();
                final JSONObject osUpdatewindow = new JSONObject();
                final JSONObject osUpdatePolicy = new JSONObject();
                final JSONObject osUpdateSetting = new JSONObject();
                final JSONObject osUpdateFiles = new JSONObject();
                final Long profileId = (Long)dataSetWrapper.getValue("PROFILE_ID");
                osUpdateProfile.put("PROFILE_ID", (Object)profileId);
                osUpdateProfile.put("PROFILE_NAME", (Object)dataSetWrapper.getValue("PROFILE_NAME"));
                osUpdateProfile.put("PLATFORM_TYPE", (Object)dataSetWrapper.getValue("PLATFORM_TYPE"));
                final boolean isTrashedProfile = (boolean)dataSetWrapper.getValue("IS_MOVED_TO_TRASH");
                osUpdatePolicy.put("POLICY_TYPE", (Object)dataSetWrapper.getValue("POLICY_TYPE"));
                osUpdatePolicy.put("DEFER_DAYS", (Object)dataSetWrapper.getValue("DEFER_DAYS"));
                final String dayOfWeek = (String)dataSetWrapper.getValue("WINDOW_DAY_OF_WEEK");
                if (!MDMStringUtils.isEmpty(dayOfWeek)) {
                    osUpdatewindow.put("WINDOW_DAY_OF_WEEK", (Object)dayOfWeek);
                }
                final String weekOfMonth = (String)dataSetWrapper.getValue("WINDOW_WEEK_OF_MONTH");
                if (!MDMStringUtils.isEmpty(dayOfWeek)) {
                    osUpdatewindow.put("WINDOW_WEEK_OF_MONTH", (Object)weekOfMonth);
                }
                final Integer windowStartTime = (Integer)dataSetWrapper.getValue("WINDOW_START_TIME");
                if (windowStartTime != null) {
                    osUpdatewindow.put("WINDOW_START_TIME", (Object)windowStartTime);
                }
                final Integer windowEndTime = (Integer)dataSetWrapper.getValue("WINDOW_END_TIME");
                if (windowEndTime != null) {
                    osUpdatewindow.put("WINDOW_END_TIME", (Object)windowEndTime);
                }
                final String notifyTitle = (String)dataSetWrapper.getValue("NOTIFY_TITLE");
                if (!MDMStringUtils.isEmpty(notifyTitle)) {
                    osUpdateNotify.put("NOTIFY_TITLE", (Object)notifyTitle);
                }
                final String notifyMessage = (String)dataSetWrapper.getValue("NOTIFY_MESSAGE");
                if (!MDMStringUtils.isEmpty(notifyMessage)) {
                    osUpdateNotify.put("NOTIFY_MESSAGE", (Object)notifyMessage);
                }
                final Boolean userToSkip = (Boolean)dataSetWrapper.getValue("ALLOW_USERS_TO_SKIP");
                if (userToSkip != null) {
                    osUpdateNotify.put("ALLOW_USERS_TO_SKIP", (Object)userToSkip);
                }
                final Integer maxSkipAllowed = (Integer)dataSetWrapper.getValue("MAX_SKIPS_ALLOWED");
                if (maxSkipAllowed != null) {
                    osUpdateNotify.put("MAX_SKIPS_ALLOWED", (Object)maxSkipAllowed);
                }
                final String maxTargetPrefix = (String)dataSetWrapper.getValue("MAX_TARGET_PREFIX");
                if (!MDMStringUtils.isEmpty(maxTargetPrefix)) {
                    osUpdateSetting.put("MAX_TARGET_PREFIX", (Object)maxTargetPrefix);
                }
                final Boolean rebootAfterUpdate = (Boolean)dataSetWrapper.getValue("REBOOT_AFTER_UPDATE");
                if (rebootAfterUpdate != null) {
                    osUpdateSetting.put("REBOOT_AFTER_UPDATE", (Object)rebootAfterUpdate);
                }
                final Long docID = (Long)dataSetWrapper.getValue("DOC_ID");
                if (docID != null) {
                    osUpdateFiles.put("DOC_ID", (Object)docID);
                }
                final String downloadLocation = (String)dataSetWrapper.getValue("DOWNLOAD_FILE_LOCATION");
                if (downloadLocation != null) {
                    osUpdateFiles.put("DOWNLOAD_FILE_LOCATION", (Object)downloadLocation);
                }
                osUpdateJSON.put("Profile", (Object)osUpdateProfile);
                osUpdateJSON.put("OSUpdatePolicy", (Object)osUpdatePolicy);
                osUpdateJSON.put("DeploymentWindowTemplate", (Object)osUpdatewindow);
                osUpdateJSON.put("DeploymentNotifTemplate", (Object)osUpdateNotify);
                osUpdateJSON.put("DeploymentPolicySettings", (Object)osUpdateSetting);
                osUpdateJSON.put("DeploymentPolicyFiles", (Object)osUpdateFiles);
                osUpdateJSON.put("isTrashed", isTrashedProfile);
                result.put((Object)osUpdateJSON);
            }
            response.put("osupdatepolicies", (Object)result);
        }
        catch (final DataAccessException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in accessing the data", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in json.Invalid params", (Throwable)e2);
            throw e2;
        }
        catch (final Exception e3) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in DMDataSetWrapper", e3);
            throw e3;
        }
        return response;
    }
    
    public JSONObject getOSUpdatePolicyDetail(final Long profileId) throws DataAccessException, JSONException {
        final JSONObject osUpdateJSON = new JSONObject();
        try {
            final SelectQuery sQuery = this.osUpdatePolicyDetailsQuery();
            sQuery.setCriteria(new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(sQuery);
            if (!dO.isEmpty()) {
                final JSONObject osUpdateProfile = new JSONObject();
                final JSONObject osUpdateNotify = new JSONObject();
                final JSONObject osUpdatewindow = new JSONObject();
                final JSONObject osUpdatePolicy = new JSONObject();
                final JSONObject osUpdateSetting = new JSONObject();
                final JSONObject osUpdateFiles = new JSONObject();
                final Row profileRow = dO.getRow("Profile");
                osUpdateProfile.put("PROFILE_ID", (Object)profileId);
                osUpdateProfile.put("PROFILE_NAME", (Object)profileRow.get("PROFILE_NAME"));
                osUpdateProfile.put("PLATFORM_TYPE", (Object)profileRow.get("PLATFORM_TYPE"));
                final boolean isTrashedProfile = (boolean)profileRow.get("IS_MOVED_TO_TRASH");
                final Row osPolicyRow = dO.getRow("OSUpdatePolicy");
                osUpdatePolicy.put("POLICY_TYPE", (Object)osPolicyRow.get("POLICY_TYPE"));
                osUpdatePolicy.put("DEFER_DAYS", (Object)osPolicyRow.get("DEFER_DAYS"));
                final Row osPolicyWindowRow = dO.getRow("DeploymentWindowTemplate");
                if (osPolicyWindowRow != null) {
                    osUpdatewindow.put("WINDOW_DAY_OF_WEEK", (Object)osPolicyWindowRow.get("WINDOW_DAY_OF_WEEK"));
                    osUpdatewindow.put("WINDOW_WEEK_OF_MONTH", (Object)osPolicyWindowRow.get("WINDOW_WEEK_OF_MONTH"));
                    osUpdatewindow.put("WINDOW_START_TIME", (Object)osPolicyWindowRow.get("WINDOW_START_TIME"));
                    osUpdatewindow.put("WINDOW_END_TIME", (Object)osPolicyWindowRow.get("WINDOW_END_TIME"));
                }
                final Row osPolicyNotifyRow = dO.getRow("DeploymentNotifTemplate");
                if (osPolicyNotifyRow != null) {
                    osUpdateNotify.put("NOTIFY_TITLE", (Object)osPolicyNotifyRow.get("NOTIFY_TITLE"));
                    osUpdateNotify.put("NOTIFY_MESSAGE", (Object)osPolicyNotifyRow.get("NOTIFY_MESSAGE"));
                    osUpdateNotify.put("ALLOW_USERS_TO_SKIP", (Object)osPolicyNotifyRow.get("ALLOW_USERS_TO_SKIP"));
                    osUpdateNotify.put("MAX_SKIPS_ALLOWED", (Object)osPolicyNotifyRow.get("MAX_SKIPS_ALLOWED"));
                }
                final Row osPolicySettingRow = dO.getRow("DeploymentPolicySettings");
                if (osPolicySettingRow != null) {
                    osUpdateSetting.put("MAX_TARGET_PREFIX", (Object)osPolicySettingRow.get("MAX_TARGET_PREFIX"));
                    osUpdateSetting.put("REBOOT_AFTER_UPDATE", (Object)osPolicySettingRow.get("REBOOT_AFTER_UPDATE"));
                    osUpdateSetting.put("DOWNLOAD_OVER_WIFI", (Object)osPolicySettingRow.get("DOWNLOAD_OVER_WIFI"));
                    osUpdateSetting.put("DOWNLOAD_IN_DEP_WINDOW", (Object)osPolicySettingRow.get("DOWNLOAD_IN_DEP_WINDOW"));
                }
                final Row osPolicyFilesRow = dO.getRow("DeploymentPolicyFiles");
                if (osPolicyFilesRow != null) {
                    osUpdateFiles.put("DOWNLOAD_FILE_LOCATION", osPolicyFilesRow.get("DOWNLOAD_FILE_LOCATION"));
                    osUpdateFiles.put("DOC_ID", osPolicyFilesRow.get("DOC_ID"));
                }
                osUpdateJSON.put("Profile", (Object)osUpdateProfile);
                osUpdateJSON.put("OSUpdatePolicy", (Object)osUpdatePolicy);
                osUpdateJSON.put("DeploymentWindowTemplate", (Object)osUpdatewindow);
                osUpdateJSON.put("DeploymentNotifTemplate", (Object)osUpdateNotify);
                osUpdateJSON.put("DeploymentPolicySettings", (Object)osUpdateSetting);
                osUpdateJSON.put("DeploymentPolicyFiles", (Object)osUpdateFiles);
                osUpdateJSON.put("isTrashed", isTrashedProfile);
            }
        }
        catch (final DataAccessException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in accessing DB", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Invalid params", (Throwable)e2);
            throw e2;
        }
        return osUpdateJSON;
    }
    
    public Integer getAllOSUpdatePolicyCount(final Criteria criteria, final Long customerId, final String search) throws Exception {
        return ProfileUtil.getInstance().getProfileCountOnType(3, criteria, customerId, search);
    }
    
    private List<Long> getApplicableResourceToRetry(final Long profileId, final List<Long> resourceIds, final Criteria criteria) {
        final List<Long> applicableResource = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            Criteria recentProfileAssociated = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            if (criteria != null) {
                recentProfileAssociated = recentProfileAssociated.and(criteria);
            }
            selectQuery.setCriteria(profileCriteria.and(resourceCriteria).and(recentProfileAssociated));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("RecentProfileForResource");
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    applicableResource.add((Long)resourceRow.get("RESOURCE_ID"));
                }
            }
            OSUpdatePolicyHandler.LOGGER.log(Level.INFO, "Retry Policy OSUpdate applicable resource:{0}", new Object[] { applicableResource });
        }
        catch (final DataAccessException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in accessing DB", (Throwable)e);
        }
        return applicableResource;
    }
    
    public List<Long> getOSUpdatePolicyId(final Criteria criteria) {
        List<Long> profileId = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            Criteria osUpdatePolicyCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)3, 0);
            if (criteria != null) {
                osUpdatePolicyCriteria = osUpdatePolicyCriteria.and(criteria);
            }
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            selectQuery.setCriteria(osUpdatePolicyCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                profileId = DBUtil.getColumnValuesAsList(dataObject.getRows("Profile"), "PROFILE_ID");
            }
        }
        catch (final DataAccessException e) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in accessing DB", (Throwable)e);
        }
        catch (final Exception e2) {
            OSUpdatePolicyHandler.LOGGER.log(Level.SEVERE, "Exception in getcolumn as list", e2);
        }
        return profileId;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        OSUpdatePolicyHandler.osUpdatePolicyHandler = null;
    }
}
