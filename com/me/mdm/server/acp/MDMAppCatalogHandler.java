package com.me.mdm.server.acp;

import java.util.Hashtable;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.AppleAppStoreSearchHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppLicenseHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.apps.MDMAppUpdateMgmtHandler;
import java.util.Collection;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.vpp.VPPManagedUserHandler;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;

public class MDMAppCatalogHandler implements ProtocolConstants
{
    Long resourceId;
    Long customerId;
    String devicePlatform;
    RelationalAPI relApi;
    Connection conn;
    Logger logger;
    
    public MDMAppCatalogHandler() {
        this.resourceId = null;
        this.customerId = null;
        this.devicePlatform = null;
        this.relApi = null;
        this.conn = null;
        this.logger = Logger.getLogger("MDMAppCatalogLogger");
    }
    
    public JSONObject processMessage(final JSONObject requestJSON) {
        final String messageType = requestJSON.optString("MsgRequestType", "Error");
        JSONObject responseJSON;
        try {
            this.relApi = RelationalAPI.getInstance();
            this.conn = this.relApi.getConnection();
            this.resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(String.valueOf(requestJSON.get("UDID")));
            this.customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(this.resourceId);
            this.devicePlatform = String.valueOf(requestJSON.get("DevicePlatform"));
            if (this.resourceId == null) {
                throw new Exception("Device is not yet managed by MDM. UDID received is " + requestJSON.get("UDID"));
            }
            if (messageType.equalsIgnoreCase("ApplicationList")) {
                responseJSON = this.processApplicationList(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("ApplicationSummary")) {
                responseJSON = this.processApplicationSummary(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("ApplicationDetails")) {
                responseJSON = this.processApplicationDetails(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("InstallApplication")) {
                responseJSON = this.processAppInstall(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("UpdateApplication")) {
                responseJSON = this.processAppUpdate(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("VppRegisteredUser")) {
                responseJSON = this.processVppRegistered(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("ScheduleAppCatalogSync")) {
                responseJSON = this.processScheduleAppCatalogSync(requestJSON);
            }
            else if (messageType.equalsIgnoreCase("AppCatalogSyncStatus")) {
                responseJSON = this.getAppCatalogLastSyncTime(requestJSON);
            }
            else {
                responseJSON = this.getErrorResponse(messageType, 15001L, "dc.mdm.actionlog.appmgmt.no_apps_available");
            }
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Request received is invalid", (Throwable)ex);
            responseJSON = this.getErrorResponse(messageType, 15002L, "dc.mdm.actionlog.appmgmt.no_apps_available");
        }
        catch (final SQLException ex2) {
            this.logger.log(Level.SEVERE, null, ex2);
            responseJSON = this.getErrorResponse(messageType, 15010L, "dc.mdm.actionlog.appmgmt.no_apps_available");
        }
        catch (final QueryConstructionException ex3) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex3);
            responseJSON = this.getErrorResponse(messageType, 15010L, "dc.mdm.actionlog.appmgmt.no_apps_available");
        }
        catch (final DataAccessException ex4) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex4);
            responseJSON = this.getErrorResponse(messageType, 15010L, "dc.mdm.actionlog.appmgmt.no_apps_available");
        }
        catch (final Exception ex5) {
            this.logger.log(Level.SEVERE, null, ex5);
            responseJSON = this.getErrorResponse(messageType, 15003L, "mdm.appcatalog.unknown_device");
        }
        finally {
            try {
                if (this.conn != null) {
                    this.conn.close();
                }
            }
            catch (final SQLException ex6) {
                this.logger.log(Level.SEVERE, null, ex6);
            }
        }
        return responseJSON;
    }
    
    public JSONObject getSyncAppCatalogCommandJSON(final Long resourceId) throws JSONException {
        return this.getMessageResponseJsonForAppCatalogCommands(resourceId, "ApplicationList");
    }
    
    public JSONObject getAppCatalogSummaryCommandJSON(final Long resourceId) throws JSONException {
        return this.getMessageResponseJsonForAppCatalogCommands(resourceId, "ApplicationSummary");
    }
    
    protected JSONObject processApplicationList(final JSONObject requestJSON) throws JSONException, SQLException, QueryConstructionException, DataAccessException, Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"ApplicationListResponse");
        final JSONObject messageRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final Long lastSyncTime = messageRequestJSON.optLong("LastSyncTime", -1L);
        final Long appCatalogSyncTime = AppsUtil.getInstance().getAppCatalogSyncTime(this.resourceId);
        JSONObject messageResponseJSON;
        if (lastSyncTime < appCatalogSyncTime) {
            messageResponseJSON = this.getApplicationListJSON(messageRequestJSON);
            final Boolean requireSummaryInfo = messageRequestJSON.optBoolean("RequireSummaryInfo", (boolean)Boolean.FALSE);
            if (requireSummaryInfo) {
                messageResponseJSON.put("AppCatalogSummary", (Object)this.processApplicationSummary(requestJSON));
            }
        }
        else if (appCatalogSyncTime == -1L) {
            messageResponseJSON = new JSONObject();
            messageResponseJSON.put("ManagedApps", (Object)new JSONArray());
            messageResponseJSON.put("ApplicationCount", 0);
            messageResponseJSON.put("LastSyncTime", -1);
        }
        else {
            messageResponseJSON = new JSONObject();
        }
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    protected JSONObject processScheduleAppCatalogSync(final JSONObject requestJSON) throws JSONException {
        return null;
    }
    
    protected JSONObject getAppCatalogLastSyncTime(final JSONObject requestJSON) throws JSONException, SQLException, QueryConstructionException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"AppCatalogSyncStatusResponse");
        final JSONObject messageResponseJSON = new JSONObject();
        final Long syncTime = AppsUtil.getInstance().getAppCatalogSyncTime(this.resourceId);
        messageResponseJSON.put("SyncTime", (Object)syncTime);
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    private JSONObject processApplicationSummary(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"ApplicationSummaryResponse");
        final JSONObject messageResponseJSON = new JSONObject();
        messageResponseJSON.put("AppStatus", (Object)this.getAppStatusWiseSummary());
        messageResponseJSON.put("AppType", (Object)this.getAppTypeWiseSummary());
        messageResponseJSON.put("AppCategory", (Object)this.getAppCategorySummary());
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    private JSONObject processApplicationDetails(final JSONObject requestJSON) throws JSONException, SQLException, DataAccessException, QueryConstructionException, Exception {
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"ApplicationDetailsResponse");
        final JSONObject messageRequestJSON = requestJSON.getJSONObject("MsgRequest");
        Long appId = JSONUtil.optLongForUVH(messageRequestJSON, "AppId", Long.valueOf(-1L));
        final String appIdentifier = (String)messageRequestJSON.opt("AppIdentifier");
        if (appId == -1L && appIdentifier != null) {
            appId = AppsUtil.getInstance().getPublishedAppId(this.resourceId, appIdentifier);
        }
        JSONObject messageResponseJSON = null;
        if (appId != null && appId != -1L) {
            messageResponseJSON = this.getApplicationDetailsJSON(appId);
            if (messageResponseJSON == null) {
                responseJSON = this.getErrorResponse("ApplicationDetailsResponse", 15004L, "App not Associated");
            }
            else {
                responseJSON.put("Status", (Object)"Acknowledged");
                responseJSON.put("MsgResponse", (Object)messageResponseJSON);
            }
        }
        else {
            responseJSON = this.getErrorResponse("ApplicationDetailsResponse", 15002L, "mdm.appcatalog.invalid_request_keys_missing");
        }
        return responseJSON;
    }
    
    protected JSONObject processAppInstall(final JSONObject requestJSON) throws JSONException, Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"InstallApplicationResponse");
        responseJSON.put("MsgResponse", (Object)this.processAppInstallAndUpdate(requestJSON));
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    protected JSONObject processAppUpdate(final JSONObject requestJSON) throws JSONException, Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"UpdateApplicationResponse");
        responseJSON.put("MsgResponse", (Object)this.processAppInstallAndUpdate(requestJSON));
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    protected JSONObject processAppInstallAndUpdate(final JSONObject requestJSON) throws JSONException, Exception {
        final String messageResponse = "{}";
        final JSONObject messageRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final Long appId = JSONUtil.optLongForUVH(messageRequestJSON, "AppId", (Long)null);
        final String commandName = String.valueOf(requestJSON.get("MsgRequestType"));
        this.installAndUpdateApp(appId, this.resourceId, commandName, this.getNotificationType());
        final JSONObject messageResponseJSON = new JSONObject(messageResponse);
        return messageResponseJSON;
    }
    
    protected JSONObject processAllAppInstallAndUpdate(final JSONObject requestJSON) throws JSONException, Exception {
        final String messageResponse = "{}";
        final String msgRequestType = String.valueOf(requestJSON.get("MsgRequestType"));
        final String commandName = this.getCommandName(msgRequestType);
        final List appIdList = this.getInstallOrUpdateAppId(msgRequestType);
        for (final Long appId : appIdList) {
            this.installAndUpdateApp(appId, this.resourceId, commandName, this.getNotificationType());
        }
        final JSONObject messageResponseJSON = new JSONObject(messageResponse);
        return messageResponseJSON;
    }
    
    private String getCommandName(final String msgRequestType) {
        if (msgRequestType.contains("Update")) {
            return "UpdateApplication";
        }
        return "InstallApplication";
    }
    
    private List getInstallOrUpdateAppId(final String commandName) {
        final List appIdList = new ArrayList();
        try {
            String tocheckAppAction = "";
            if (commandName.contains("Update")) {
                tocheckAppAction = "Update";
            }
            else if (commandName.contains("Install")) {
                tocheckAppAction = "Install";
            }
            final JSONObject appsListJson = this.getApplicationListJSON(new JSONObject());
            final JSONArray managedApps = appsListJson.optJSONArray("ManagedApps");
            if (managedApps != null) {
                for (int i = 0; i < managedApps.length(); ++i) {
                    final JSONObject singleAppDetails = (JSONObject)managedApps.get(i);
                    final String appAction = singleAppDetails.optString("AppAction");
                    final Integer appStatus = singleAppDetails.optInt("AppStatus");
                    if (appAction.equalsIgnoreCase(tocheckAppAction) && appStatus == 0) {
                        final Long appId = singleAppDetails.optLong("AppId");
                        if (appId != null) {
                            appIdList.add(appId);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getInstallOrUpdateAppId:{0}", ex);
        }
        return appIdList;
    }
    
    public void installAndUpdateApp(final Long appId, final Long resId, final String commandName, final int notificationType) throws Exception {
        final List resList = new ArrayList();
        resList.add(resId);
        final Long collectionId = MDMUtil.getInstance().getApplicableAppCollectionForResource(appId, resId);
        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
        if (!this.isAppCommandRemove(collectionId, resId)) {
            final Long userID = ProfileAssociateHandler.getInstance().getAssociatedByUser(resId, null, collectionId);
            final Long profileID = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionId).get("PROFILE_ID");
            final JSONObject params = new JSONObject();
            final JSONObject associatedUser = new JSONObject();
            associatedUser.put(profileID.toString(), (Object)userID);
            params.put("UserId", (Object)associatedUser.toString());
            params.put("commandName", (Object)commandName);
            final List commandIdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionId), commandName);
            params.put("isMSIJson", (Object)ProfileUtil.getInstance().getWindowsAppMSICommand(Arrays.asList(collectionId), commandIdList));
            SeqCmdRepository.getInstance().executeSequentially(resList, commandIdList, params);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandIdList, resList);
            if (notificationType == 5) {
                this.logger.log(Level.SEVERE, "Invalid platform type for resourceId:{0}", this.resourceId);
                throw new Exception("Platform Type not recognised");
            }
            NotificationHandler.getInstance().SendNotification(resList, notificationType);
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resId, collectionId.toString(), 18, "dc.db.mdm.apps.status.automatic_install");
            new AppInstallationStatusHandler().updateAppInstallationStatusFromDevice(resId, appGroupId, appId, 1, "dc.db.mdm.apps.status.Installing", 0);
        }
    }
    
    private JSONObject processVppRegistered(final JSONObject requestJSON) throws JSONException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"VppRegisteredUserResponse");
        final JSONObject messageRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final Long appId = JSONUtil.optLongForUVH(messageRequestJSON, "AppId", (Long)null);
        final Long collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
        final JSONObject messageResponseJSON = new JSONObject();
        final JSONArray excludeJsonArray = messageRequestJSON.optJSONArray("ExcludeKeysList");
        final ArrayList<String> excludeKeyList = new ArrayList<String>();
        if (excludeJsonArray != null) {
            for (int i = 0; i < excludeJsonArray.length(); ++i) {
                excludeKeyList.add(String.valueOf(excludeJsonArray.get(i)));
            }
        }
        if (!excludeKeyList.contains("IsVppUserAssignMentNeeded")) {
            final String invitationUrl = VPPManagedUserHandler.getInstance().getInvitationURL(this.resourceId, appGroupId, this.customerId);
            if (invitationUrl != null && invitationUrl.equalsIgnoreCase("registeredAlready")) {
                messageResponseJSON.put("IsVppUserAssignMentNeeded", (Object)Boolean.FALSE);
            }
            else if (invitationUrl != null) {
                messageResponseJSON.put("IsVppUserAssignMentNeeded", (Object)Boolean.TRUE);
                messageResponseJSON.put("InvitationUrl", (Object)invitationUrl);
            }
        }
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    private JSONObject getMessageResponseJsonForAppCatalogCommands(final Long resourceId, final String messageRequestType) throws JSONException {
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("MsgRequestType", (Object)messageRequestType);
        requestJSON.put("UDID", (Object)ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId));
        requestJSON.put("DevicePlatform", MDMAppCatalogHandler.PLATFORM_MAP.get(String.valueOf(ManagedDeviceHandler.getInstance().getPlatformType(resourceId))));
        requestJSON.put("MsgVersion", 1.0);
        final JSONObject messageRequestJSON = new JSONObject();
        requestJSON.put("MsgRequest", (Object)messageRequestJSON);
        return this.processMessage(requestJSON).getJSONObject("MsgResponse");
    }
    
    private JSONObject getApplicationDetailsJSON(final Long appId) throws SQLException, JSONException, DataAccessException, QueryConstructionException, Exception {
        final Criteria appIdCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
        final DMDataSetWrapper dataSet = this.getApplicationListDS(appIdCriteria, null);
        final JSONArray managedAppsArray = this.getManagedAppsJSON(dataSet, new ArrayList<String>());
        if (managedAppsArray.isNull(0)) {
            return null;
        }
        return managedAppsArray.getJSONObject(0);
    }
    
    private JSONObject getApplicationListJSON(final JSONObject messageRequestJSON) throws JSONException, SQLException, QueryConstructionException, DataAccessException, Exception {
        JSONObject messageResponseJSON = null;
        final Criteria appListCriteria = this.processApplicationFilters(messageRequestJSON);
        final SortColumn sortByColumn = this.processSortByColumn(messageRequestJSON);
        final DMDataSetWrapper dataSet = this.getApplicationListDS(appListCriteria, sortByColumn);
        final JSONArray excludeJsonArray = messageRequestJSON.optJSONArray("ExcludeKeysList");
        final ArrayList<String> excludeKeyList = new ArrayList<String>();
        if (excludeJsonArray != null) {
            for (int i = 0; i < excludeJsonArray.length(); ++i) {
                excludeKeyList.add(String.valueOf(excludeJsonArray.get(i)));
            }
        }
        if (dataSet != null) {
            final JSONArray managedAppsJSON = this.getManagedAppsJSON(dataSet, excludeKeyList);
            messageResponseJSON = new JSONObject();
            messageResponseJSON.put("ManagedApps", (Object)managedAppsJSON);
            messageResponseJSON.put("ApplicationCount", managedAppsJSON.length());
            messageResponseJSON.put("LastSyncTime", (Object)AppsUtil.getInstance().getAppCatalogSyncTime(this.resourceId));
        }
        return messageResponseJSON;
    }
    
    private DMDataSetWrapper getApplicationListDS(Criteria cri, final SortColumn sortColumn) throws Exception {
        DMDataSetWrapper dataSet = null;
        try {
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(this.resourceId);
            final SelectQuery appListQuery = AppsUtil.getInstance().getQueryforResourceAppDetails(this.resourceId, null, -1, null, platformType);
            final Criteria appNotInFailedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)7, 1);
            if (cri == null) {
                cri = appNotInFailedCriteria;
            }
            else {
                cri = cri.and(appNotInFailedCriteria);
            }
            if (cri != null) {
                appListQuery.setCriteria(cri.and(appListQuery.getCriteria()));
            }
            if (sortColumn != null) {
                appListQuery.removeSortColumn(0);
                appListQuery.addSortColumn(sortColumn);
            }
            final Join collnToResJoin = new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2);
            final Criteria collnToResCri = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)this.resourceId, 0);
            appListQuery.addJoin(collnToResJoin);
            appListQuery.setCriteria(appListQuery.getCriteria().and(collnToResCri));
            appListQuery.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
            appListQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS", "CollnToResAlias.STATUS"));
            appListQuery.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
            appListQuery.addSortColumn(this.getSortByTimeCOlumn());
            dataSet = DMDataSetWrapper.executeQuery((Object)appListQuery);
        }
        catch (final SQLException ex) {
            this.logger.log(Level.SEVERE, "Query execution error in MDMAppCatalogHandler.getApplicationListDS", ex);
            throw ex;
        }
        catch (final QueryConstructionException ex2) {
            this.logger.log(Level.SEVERE, "Query construction error in MDMAppCatalogHandler.getApplicationListDS", (Throwable)ex2);
            throw ex2;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in MDMAppCatalogHandler.getApplicationListDS", exp);
            throw exp;
        }
        return dataSet;
    }
    
    private DataObject getAppDeviceLicenseDO() throws SQLException, QueryConstructionException, DataAccessException {
        SelectQuery appLicenseListQuery = null;
        appLicenseListQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPLicenseToDevice"));
        final Join licenseToDeviceJoin = new Join("MdVPPLicenseToDevice", "MdVPPLicenseDetails", new String[] { "LICENSE_DETAIL_ID" }, new String[] { "LICENSE_DETAIL_ID" }, 2);
        final Join licenseDetailsJoin = new Join("MdVPPLicenseDetails", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        appLicenseListQuery.addJoin(licenseToDeviceJoin);
        appLicenseListQuery.addJoin(licenseDetailsJoin);
        final Criteria resIdcri = new Criteria(Column.getColumn("MdVPPLicenseToDevice", "MANAGED_DEVICE_ID"), (Object)this.resourceId, 0);
        appLicenseListQuery.setCriteria(resIdcri);
        appLicenseListQuery.addSelectColumn(Column.getColumn("MdVPPLicenseToDevice", "*"));
        appLicenseListQuery.addSelectColumn(Column.getColumn("MdLicenseToAppGroupRel", "*"));
        final DataObject deviceBasedLicenseDo = MDMUtil.getPersistence().get(appLicenseListQuery);
        return deviceBasedLicenseDo;
    }
    
    private JSONArray getManagedAppsJSON(final DMDataSetWrapper dataSet, final ArrayList<String> excludeKeyList) throws JSONException, SQLException, DataAccessException, Exception {
        JSONArray managedAppsJSONArray = null;
        try {
            managedAppsJSONArray = new JSONArray();
            final ArrayList<String> keyList = new ArrayList<String>();
            keyList.addAll(MDMAppCatalogHandler.MANAGED_APPS_KEY_LIST);
            keyList.removeAll(excludeKeyList);
            final DataObject deviceLicenseDO = this.getAppDeviceLicenseDO();
            while (dataSet.next()) {
                JSONObject managedAppJSON = new JSONObject();
                final Boolean isMarkedForDelete = (Boolean)dataSet.getValue("MARKED_FOR_DELETE");
                for (final String key : keyList) {
                    if (key.equalsIgnoreCase("AppType")) {
                        final int appType = Integer.valueOf(dataSet.getValue("PACKAGE_TYPE").toString());
                        if (appType == 0) {
                            managedAppJSON.put("AppType", (Object)"StoreApp");
                            managedAppJSON.put("IsPaidApp", (Object)Boolean.FALSE);
                        }
                        else if (appType == 1) {
                            managedAppJSON.put("AppType", (Object)"StoreApp");
                            managedAppJSON.put("IsPaidApp", (Object)Boolean.TRUE);
                        }
                        else {
                            managedAppJSON.put("AppType", (Object)"EnterpriseApp");
                            managedAppJSON.put("IsPaidApp", (Object)Boolean.FALSE);
                            managedAppJSON.put("AppFileUrl", dataSet.getValue("APP_FILE_LOC"));
                        }
                    }
                    else if (key.equalsIgnoreCase("IsMarkedForDelete")) {
                        managedAppJSON.put("IsMarkedForDelete", (Object)isMarkedForDelete);
                    }
                    else if (key.equalsIgnoreCase("AppAction")) {
                        final int appType = (int)dataSet.getValue("PACKAGE_TYPE");
                        if (isMarkedForDelete) {
                            managedAppJSON.put("AppAction", (Object)"Remove");
                        }
                        else {
                            final Long publishedAppId = (Long)dataSet.getValue("PUBLISHED_APP_ID");
                            final Long installedAppId = (Long)dataSet.getValue("INSTALLED_APP_ID");
                            if (new MDMAppUpdateMgmtHandler().isAppCatalogUpgradeAction(installedAppId, publishedAppId, appType, this.getPlatformType())) {
                                managedAppJSON.put("AppAction", (Object)"Update");
                            }
                            else {
                                managedAppJSON.put("AppAction", (Object)"Install");
                            }
                        }
                    }
                    else if (key.equalsIgnoreCase("IsInstalled")) {
                        final int installedStatus = (int)dataSet.getValue("STATUS");
                        if (installedStatus == 2) {
                            managedAppJSON.put("IsInstalled", (Object)Boolean.TRUE);
                        }
                        else {
                            managedAppJSON.put("IsInstalled", (Object)Boolean.FALSE);
                        }
                    }
                    else if (key.equalsIgnoreCase("IsVppLicensed")) {
                        Boolean isVppLicensed = null;
                        final Boolean isPortalPurchased = (Boolean)dataSet.getValue("IS_PURCHASED_FROM_PORTAL");
                        if (this.getPlatformType() == 1 && isPortalPurchased != null && isPortalPurchased) {
                            isVppLicensed = Boolean.TRUE;
                        }
                        else {
                            isVppLicensed = Boolean.FALSE;
                        }
                        managedAppJSON.put("IsVppLicensed", (Object)isVppLicensed);
                    }
                    else if (key.equalsIgnoreCase("isAppUserAssignable")) {
                        final Long appGroupId = (Long)dataSet.getValue("APP_GROUP_ID");
                        Properties vppAppLicenseDetails = new Properties();
                        vppAppLicenseDetails = new VPPAppLicenseHandler().getVPPAppLicenseDetailsForDevice(appGroupId, this.resourceId);
                        final Boolean isAppUserAssignable = ((Hashtable<K, Boolean>)vppAppLicenseDetails).getOrDefault(1, Boolean.FALSE);
                        managedAppJSON.put("isAppUserAssignable", (Object)isAppUserAssignable);
                    }
                    else if (key.equalsIgnoreCase("AppDescription")) {
                        final String storeId = (String)dataSet.getValue("STORE_ID");
                        String description = (String)dataSet.getValue("DESCRIPTION");
                        if (this.devicePlatform.equalsIgnoreCase("IOS") && storeId != null && !storeId.equals("") && !storeId.equals("0") && MDMStringUtils.isEmpty(description)) {
                            final AppleAppStoreSearchHandler appSearch = new AppleAppStoreSearchHandler();
                            description = String.valueOf(appSearch.getCompleteAppDetails(Integer.parseInt(storeId), this.customerId).get("description"));
                        }
                        description = ((description == null) ? I18N.getMsg("dc.mdm.actionlog.appmgmt.description_not_available", new Object[0]) : (description.equals("null") ? I18N.getMsg("dc.mdm.actionlog.appmgmt.description_not_available", new Object[0]) : description));
                        managedAppJSON.put("AppDescription", (Object)description);
                    }
                    else if (key.equalsIgnoreCase("AppStatus")) {
                        managedAppJSON.put("AppStatus", MDMAppCatalogHandler.STATUS_CONSTANT_MAP.get(String.valueOf(dataSet.getValue((String)MDMAppCatalogHandler.KEY_TO_DB_COLUMN_NAME_MAP.get(key)))));
                    }
                    else if (key.equalsIgnoreCase("AppIconImageUrl")) {
                        final HashMap hm = new HashMap();
                        final String filePath = (String)dataSet.getValue("DISPLAY_IMAGE_LOC");
                        if (MDMStringUtils.isEmpty(filePath)) {
                            continue;
                        }
                        hm.put("path", dataSet.getValue("DISPLAY_IMAGE_LOC"));
                        hm.put("IS_SERVER", false);
                        hm.put("IS_AUTHTOKEN", true);
                        final String display_image_loc = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        managedAppJSON.put("AppIconImageUrl", (Object)display_image_loc);
                    }
                    else if (key.equalsIgnoreCase("AppCollectionStatus")) {
                        managedAppJSON.put("AppCollectionStatus", dataSet.getValue("CollnToResAlias.STATUS"));
                    }
                    else {
                        managedAppJSON.put(key, dataSet.getValue((String)MDMAppCatalogHandler.KEY_TO_DB_COLUMN_NAME_MAP.get(key)));
                    }
                }
                managedAppJSON = this.modifyManagedAppResponse(managedAppJSON, dataSet);
                managedAppsJSONArray.put((Object)managedAppJSON);
            }
        }
        catch (final SQLException ex) {
            this.logger.log(Level.SEVERE, "Value for a column is not present in DB. Exception in MDMAppCatalogHandler.getManagedAppsJSON", ex);
            throw ex;
        }
        return managedAppsJSONArray;
    }
    
    protected JSONObject modifyManagedAppResponse(final JSONObject managedAppJSON, final DMDataSetWrapper dataSet) throws Exception {
        return managedAppJSON;
    }
    
    private boolean isMarkedForDelete(final Long collectionId, final Long resourceId) throws DataAccessException {
        boolean isMarkedForDelete = false;
        try {
            final Criteria collnCri = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resIdCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cri = collnCri.and(resIdCri);
            final DataObject dObj = DataAccess.get("RecentProfileForResource", cri);
            if (!dObj.isEmpty()) {
                isMarkedForDelete = (boolean)dObj.getFirstValue("RecentProfileForResource", "MARKED_FOR_DELETE");
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Query Exception in MDMAppCatalogHandler.isMarkedForDelete", (Throwable)ex);
            throw ex;
        }
        return isMarkedForDelete;
    }
    
    private Criteria processApplicationFilters(final JSONObject messageRequestJSON) throws JSONException {
        Criteria appListCriteria = null;
        Criteria appStatusCriteria = null;
        if (messageRequestJSON.has("FilterByAppStatus")) {
            final ArrayList<Integer> statusConstants = new ArrayList<Integer>();
            final JSONArray statusFilters = messageRequestJSON.getJSONArray("FilterByAppStatus");
            for (int i = 0; i < statusFilters.length(); ++i) {
                final String statusKey = String.valueOf(statusFilters.get(i));
                final Integer status = Integer.valueOf(MDMAppCatalogHandler.STATUS_CONSTANT_MAP.get(statusKey));
                statusConstants.add(status);
                if (status == 5) {
                    statusConstants.add(6);
                }
            }
            appStatusCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)statusConstants.toArray(), 8, false);
        }
        Criteria appNameCriteria = null;
        if (messageRequestJSON.has("FilterByAppName")) {
            final String appName = String.valueOf(messageRequestJSON.get("FilterByAppName"));
            appNameCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME"), (Object)appName, 12, false);
        }
        Criteria bundleIdentifierCriteria = null;
        if (messageRequestJSON.has("FilterByAppBundleIdentifier")) {
            final String bundleIdentifier = String.valueOf(messageRequestJSON.get("FilterByAppBundleIdentifier"));
            bundleIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)bundleIdentifier, 0);
        }
        if (appStatusCriteria != null) {
            appListCriteria = appStatusCriteria;
        }
        if (bundleIdentifierCriteria != null) {
            appListCriteria = bundleIdentifierCriteria;
        }
        if (appNameCriteria != null) {
            if (appListCriteria != null) {
                appListCriteria = appListCriteria.or(appNameCriteria);
            }
            else {
                appListCriteria = appNameCriteria;
            }
        }
        return appListCriteria;
    }
    
    private SortColumn processSortByColumn(final JSONObject messageRequestJSON) {
        Column sortColumn = Column.getColumn("MdAppCatalogToResource", "STATUS");
        if (messageRequestJSON.has("SortByColumn")) {
            final String columnName = messageRequestJSON.optString("SortByColumn");
            if (columnName.equalsIgnoreCase("AppName")) {
                sortColumn = Column.getColumn("MdAppDetails", "APP_NAME");
            }
            else if (columnName.equalsIgnoreCase("AppCategory")) {
                sortColumn = Column.getColumn("AppCategory", "APP_CATEGORY_NAME");
            }
            else if (columnName.equalsIgnoreCase("AppUpdatedTime")) {
                sortColumn = Column.getColumn("MdAppCatalogToResource", "UPDATED_AT");
            }
        }
        final Boolean isDescending = messageRequestJSON.optBoolean("SortByDescending", (boolean)Boolean.FALSE);
        final SortColumn sortByColumn = new SortColumn(sortColumn, !isDescending);
        return sortByColumn;
    }
    
    private SortColumn getSortByTimeCOlumn() {
        final Column sortColumn = Column.getColumn("MdAppCatalogToResource", "UPDATED_AT");
        final SortColumn sortByColumn = new SortColumn(sortColumn, false);
        return sortByColumn;
    }
    
    private JSONObject getAppStatusWiseSummary() throws JSONException {
        final JSONObject statusSummary = new JSONObject();
        statusSummary.put("YetToInstall", 0);
        statusSummary.put("Installing", 0);
        statusSummary.put("Installed", 0);
        final SelectQuery appSummaryQuery = this.getAppSummaryBaseQuery();
        final Column installStatusColumn = Column.getColumn("MdAppCatalogToResource", "STATUS");
        final Column appColumn = Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID");
        appSummaryQuery.addSelectColumn(installStatusColumn);
        appSummaryQuery.addSelectColumn(appColumn.count());
        final List groupByColumns = new ArrayList();
        groupByColumns.add(installStatusColumn);
        final GroupByClause groupByClause = new GroupByClause(groupByColumns);
        appSummaryQuery.setGroupByClause(groupByClause);
        final HashMap statusWiseMap = DBUtil.executeCountQuery(appSummaryQuery);
        for (final Map.Entry pairs : statusWiseMap.entrySet()) {
            final int statusConstant = pairs.getKey();
            final int count = pairs.getValue();
            if (statusConstant == 0) {
                statusSummary.put("YetToInstall", count);
            }
            else if (statusConstant == 1) {
                statusSummary.put("Installing", count);
            }
            else {
                if (statusConstant != 2) {
                    continue;
                }
                statusSummary.put("Installed", count);
            }
        }
        return statusSummary;
    }
    
    private JSONObject getAppTypeWiseSummary() throws JSONException {
        final JSONObject appStoreSummary = new JSONObject();
        appStoreSummary.put("Total", 0);
        appStoreSummary.put("Free", 0);
        appStoreSummary.put("Paid", 0);
        final JSONObject enterpriseAppSummary = new JSONObject();
        enterpriseAppSummary.put("Total", 0);
        enterpriseAppSummary.put("Free", 0);
        enterpriseAppSummary.put("Paid", 0);
        final SelectQuery appSummaryQuery = this.getAppSummaryBaseQuery();
        final Column packageTypeColumn = Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE");
        final Column appColumn = Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID");
        appSummaryQuery.addSelectColumn(packageTypeColumn);
        appSummaryQuery.addSelectColumn(appColumn.count());
        final List groupByColumns = new ArrayList();
        groupByColumns.add(packageTypeColumn);
        final GroupByClause groupByClause = new GroupByClause(groupByColumns);
        appSummaryQuery.setGroupByClause(groupByClause);
        final HashMap packageTypeMap = DBUtil.executeCountQuery(appSummaryQuery);
        for (final Map.Entry pairs : packageTypeMap.entrySet()) {
            final int packageType = pairs.getKey();
            final int packageTypeCount = pairs.getValue();
            if (packageType == 0) {
                appStoreSummary.put("Free", packageTypeCount);
            }
            else if (packageType == 1) {
                appStoreSummary.put("Paid", packageTypeCount);
            }
            else {
                if (packageType != 2) {
                    continue;
                }
                enterpriseAppSummary.put("Free", packageTypeCount);
            }
        }
        appStoreSummary.put("Total", appStoreSummary.getInt("Free") + appStoreSummary.getInt("Paid"));
        enterpriseAppSummary.put("Total", enterpriseAppSummary.getInt("Free") + enterpriseAppSummary.getInt("Paid"));
        final JSONObject appTypeSummary = new JSONObject();
        appTypeSummary.put("StoreApp", (Object)appStoreSummary);
        appTypeSummary.put("EnterpriseApp", (Object)enterpriseAppSummary);
        return appTypeSummary;
    }
    
    private JSONArray getAppCategorySummary() throws Exception {
        final JSONArray appCategorySummary = new JSONArray();
        final SelectQuery appSummaryQuery = this.getAppSummaryBaseQuery();
        final Column categoryKeyColumn = Column.getColumn("AppCategory", "APP_CATEGORY_LABEL");
        final Column categoryNameColumn = Column.getColumn("AppCategory", "APP_CATEGORY_NAME");
        Column appColumn = Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID");
        appColumn = appColumn.count();
        appColumn.setColumnAlias("COUNT_GROUP_ID");
        appSummaryQuery.addSelectColumn(categoryKeyColumn);
        appSummaryQuery.addSelectColumn(categoryNameColumn);
        appSummaryQuery.addSelectColumn(appColumn);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(categoryKeyColumn);
        groupByColumns.add(categoryNameColumn);
        final GroupByClause groupByClause = new GroupByClause(groupByColumns);
        appSummaryQuery.setGroupByClause(groupByClause);
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)appSummaryQuery);
            while (dataSet.next()) {
                final String categoryNameKey = dataSet.getValue("APP_CATEGORY_LABEL").toString();
                final String categoryEnglishName = dataSet.getValue("APP_CATEGORY_NAME").toString();
                final int count = Integer.valueOf(dataSet.getValue("COUNT_GROUP_ID").toString());
                final JSONObject categoryJSON = new JSONObject();
                categoryJSON.put("CategoryName", (Object)categoryEnglishName);
                categoryJSON.put("CategoryKey", (Object)categoryNameKey);
                categoryJSON.put("CategoryAppCount", count);
                appCategorySummary.put((Object)categoryJSON);
            }
        }
        catch (final SQLException ex) {
            this.logger.log(Level.SEVERE, "Query Execution error in MDMAppCatalogHandler.getAppCategoySummary", ex);
            throw ex;
        }
        catch (final QueryConstructionException ex2) {
            this.logger.log(Level.SEVERE, "Query Construction error in MDMAppCatalogHandler.getAppCategoySummary", (Throwable)ex2);
            throw ex2;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Query Construction error in MDMAppCatalogHandler.getAppCategoySummary", exp);
            throw exp;
        }
        return appCategorySummary;
    }
    
    private JSONObject getErrorResponse(final String responseType, final Long errorCode, final String errorKey) {
        try {
            final String errorMessage = I18NUtil.transformRemarksInEnglish(errorKey, (String)null);
            final JSONObject errorJSON = new JSONObject();
            errorJSON.put("ErrorCode", (Object)errorCode);
            errorJSON.put("ErrorKey", (Object)errorKey);
            errorJSON.put("ErrorMessage", (Object)errorMessage);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("MsgResponseType", (Object)responseType);
            responseJSON.put("Status", (Object)"Error");
            responseJSON.put("MsgResponse", (Object)errorJSON);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while constructing errorJSON", (Throwable)ex);
            return null;
        }
    }
    
    private SelectQuery getAppSummaryBaseQuery() {
        final SelectQuery appSummaryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        final Join appGroupPackDataJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appCategoryRelJoin = new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appCategoryJoin = new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2);
        final Join resJoin = new Join("MdAppGroupDetails", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join mgDeviceJoin = new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        appSummaryQuery.addJoin(appGroupPackDataJoin);
        appSummaryQuery.addJoin(appCategoryRelJoin);
        appSummaryQuery.addJoin(appCategoryJoin);
        appSummaryQuery.addJoin(resJoin);
        appSummaryQuery.addJoin(mgDeviceJoin);
        appSummaryQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)this.resourceId, 0));
        appSummaryQuery.setCriteria(appSummaryQuery.getCriteria().and(AppsUtil.getInstance().getAppListExcludeCriteria(this.customerId)));
        return appSummaryQuery;
    }
    
    private int getNotificationType() {
        if (this.devicePlatform.equalsIgnoreCase("IOS")) {
            return 1;
        }
        if (this.devicePlatform.equalsIgnoreCase("WINDOWSPHONE")) {
            return 3;
        }
        if (this.devicePlatform.equalsIgnoreCase("ANDROID")) {
            return 2;
        }
        return 5;
    }
    
    private int getPlatformType() {
        if (this.devicePlatform.equalsIgnoreCase("IOS")) {
            return 1;
        }
        if (this.devicePlatform.equalsIgnoreCase("WINDOWSPHONE")) {
            return 3;
        }
        if (this.devicePlatform.equalsIgnoreCase("ANDROID")) {
            return 2;
        }
        return -1;
    }
    
    public boolean isAppCommandRemove(final Long collectionID, final Long resourceID) throws DataAccessException {
        boolean isMarkedForDelete = false;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
        selectQuery.setCriteria(criteria.and(resCriteria));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            isMarkedForDelete = true;
        }
        else {
            final Iterator iterator = dataObject.getRows("RecentProfileForResource");
            boolean isAnyProfileFalse = false;
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (!(boolean)row.get("MARKED_FOR_DELETE")) {
                    isAnyProfileFalse = true;
                    isMarkedForDelete = false;
                }
                else {
                    isMarkedForDelete = true;
                }
            }
            if (isAnyProfileFalse) {
                isMarkedForDelete = false;
            }
        }
        return isMarkedForDelete;
    }
    
    public JSONArray getAppCatalogForDevice(final Long deviceId, final String searchValue, final int filterValue, final PagingUtil pagingUtil) throws Exception {
        final int platFormType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)deviceId, "PLATFORM_TYPE");
        final SelectQuery selectQuery = AppsUtil.getInstance().getQueryforResourceAppDetails(deviceId, null, filterValue, searchValue, platFormType);
        selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
        selectQuery.addSortColumn(new SortColumn("MdAppDetails", "APP_NAME", (boolean)Boolean.TRUE));
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONArray appsArray = new JSONArray();
        while (ds.next()) {
            final JSONObject detailJSON = new JSONObject();
            final int packageType = (int)ds.getValue("PACKAGE_TYPE");
            final int platformType = (int)ds.getValue("PLATFORM_TYPE");
            final Integer scope = (Integer)ds.getValue("SCOPE");
            boolean isInstalled = false;
            final int appInstallationStatus = (int)ds.getValue("STATUS");
            if (appInstallationStatus == 2) {
                isInstalled = true;
            }
            final Long publishedAppId = (Long)ds.getValue("PUBLISHED_APP_ID");
            final Long installedAppId = (Long)ds.getValue("INSTALLED_APP_ID");
            Boolean isUpgrade = false;
            if (installedAppId != null && installedAppId != (long)publishedAppId) {
                isUpgrade = true;
            }
            detailJSON.put("APP_NAME", ds.getValue("APP_NAME"));
            detailJSON.put("IDENTIFIER", ds.getValue("IDENTIFIER"));
            detailJSON.put("APP_ID", ds.getValue("PACKAGE_ID"));
            detailJSON.put("APP_VERSION_ID", ds.getValue("APP_ID"));
            detailJSON.put("APP_VERSION", ds.getValue("APP_VERSION"));
            detailJSON.put("APP_CATEGORY_NAME", (Object)I18N.getMsg((String)ds.getValue("APP_CATEGORY_LABEL"), new Object[0]));
            detailJSON.put("DISPLAY_NAME", ds.getValue("PROFILE_NAME"));
            detailJSON.put("VERSION_CODE", ds.getValue("APP_NAME_SHORT_VERSION"));
            detailJSON.put("PACKAGE_TYPE", packageType);
            detailJSON.put("DESCRIPTION", ds.getValue("DESCRIPTION"));
            detailJSON.put("scope", (Object)((scope != null) ? ((scope == 0) ? "device" : "container") : "device"));
            detailJSON.put("IS_INSTALLED", isInstalled);
            detailJSON.put("IS_UPGRADE", (Object)isUpgrade);
            detailJSON.put("TO_UNINSTALL", ds.getValue("MARKED_FOR_DELETE"));
            final String checkSum = (String)ds.getValue("APP_CHECKSUM");
            if (!MDMStringUtils.isEmpty(checkSum)) {
                detailJSON.put("AppCheckSum", (Object)checkSum);
            }
            if (ds.getValue("DISPLAY_IMAGE_LOC") != null) {
                final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                detailJSON.put("icon", (Object)displayImageLoc);
            }
            if (ds.getValue("APP_FILE_LOC") != null) {
                final String appURL = String.valueOf(ds.getValue("APP_FILE_LOC"));
                detailJSON.put("APP_URL", (Object)appURL);
            }
            if (platformType == 1 && packageType != 2) {
                final Boolean isPortalPurchased = (Boolean)ds.getValue("IS_PURCHASED_FROM_PORTAL");
                if (isPortalPurchased != null && isPortalPurchased) {
                    detailJSON.put("VPP_LICENSE_TYPE", true);
                }
                else {
                    detailJSON.put("VPP_LICENSE_TYPE", false);
                }
            }
            appsArray.put((Object)detailJSON);
        }
        return appsArray;
    }
}
