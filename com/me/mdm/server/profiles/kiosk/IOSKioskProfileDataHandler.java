package com.me.mdm.server.profiles.kiosk;

import java.util.Hashtable;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.Iterator;
import java.util.Arrays;
import org.json.JSONArray;
import java.util.Properties;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.LockScreenMessageUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.config.MDMCollectionUtil;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IOSKioskProfileDataHandler
{
    public static Logger logger;
    private static Logger mdmlogger;
    
    public JSONObject isProfileApplicableForIOSKioskAutomation(final Long collectionID, final Long customerId) {
        final JSONObject applicableApps = new JSONObject();
        try {
            final DataObject kioskDO = this.getKioskProfileDetails(collectionID, customerId);
            if (!kioskDO.isEmpty()) {
                final Row kioskRow = kioskDO.getFirstRow("AppLockPolicy");
                if (kioskDO.containsTable("AppLockPolicyApps")) {
                    final Row appRow = kioskDO.getFirstRow("AppLockPolicyApps");
                    final Long appGroupID = (Long)appRow.get("APP_GROUP_ID");
                    applicableApps.put("APP_GROUP_ID", (Object)appGroupID);
                }
                if (!kioskDO.containsTable("AppLockPolicyApps")) {
                    final String bundleIdentifier = (String)kioskRow.get("IDENTIFIER");
                    final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(bundleIdentifier, 1, customerId);
                    applicableApps.put("APP_GROUP_ID", (Object)appGroupID);
                }
                final Integer appType = (Integer)kioskRow.get("KIOSK_MODE");
                applicableApps.put("KIOSK_MODE", (Object)appType);
            }
        }
        catch (final Exception e) {
            IOSKioskProfileDataHandler.logger.log(Level.SEVERE, "Error while retriving Kiosk App", e);
        }
        return applicableApps;
    }
    
    public boolean isIOSKioskAppAutomation(final Long collectionId, final Long customerId) {
        final JSONObject isApplicableApps = this.isProfileApplicableForIOSKioskAutomation(collectionId, customerId);
        if (isApplicableApps.length() != 0) {
            final Integer appType = isApplicableApps.optInt("KIOSK_MODE");
            if (appType == 3 || appType == 1) {
                return true;
            }
        }
        return false;
    }
    
    public Integer getKioskType(final Long collectionId) {
        Integer kioskType = null;
        try {
            final SelectQuery kioskQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
            kioskQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            kioskQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            kioskQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria criteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0);
            kioskQuery.setCriteria(criteria);
            kioskQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject kioskDO = MDMUtil.getPersistence().get(kioskQuery);
            if (!kioskDO.isEmpty()) {
                final Row row = kioskDO.getFirstRow("AppLockPolicy");
                kioskType = (Integer)row.get("KIOSK_MODE");
            }
        }
        catch (final DataAccessException e) {
            IOSKioskProfileDataHandler.logger.log(Level.SEVERE, "Exception in getting kiosk type for collection", (Throwable)e);
        }
        return kioskType;
    }
    
    public void updateFailedKioskAppForResource(final List resourceList, final Long collectionId, final Long customerId) throws Exception {
        if (resourceList != null && resourceList.size() != 0) {
            final Long resourceId = resourceList.get(0);
            final JSONObject params = new JSONObject();
            params.put("resourceId", (Object)resourceId);
            params.put("collectionId", (Object)collectionId);
            params.put("isAppFailed", true);
            params.put("customerId", (Object)customerId);
            final IOSInstallProfileResponseProcessor processor = new IOSInstallProfileResponseProcessor();
            final String remarks = processor.processKioskResponse(params);
            IOSKioskProfileDataHandler.mdmlogger.log(Level.INFO, "Resource List marked as failure due to no kiosk app in the device{0}", resourceList.toString());
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionId, 7, remarks);
            MDMCollectionStatusUpdate.getInstance().updateCollnToResListErrorCode(resourceList, collectionId, 21008);
        }
    }
    
    private JSONObject isKioskProfileAssociatedToResourceForApp(final Long resourceId, final Criteria criteria, final Long collectionId) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        query.addJoin(new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        query.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        query.addJoin(new Join("AppLockPolicy", "AppLockPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        query.addJoin(new Join("AppLockPolicyApps", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        query.addJoin(new Join("RecentProfileForResource", "MDMCollnToResErrorCode", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 1));
        final Criteria resource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria appCriteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0).and(new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)1, 0));
        final Criteria removeCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria kioskCriteria = resource.and(appCriteria).and(removeCriteria);
        query.addSelectColumn(new Column((String)null, "*"));
        query.setCriteria(kioskCriteria.and(criteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (!dataObject.isEmpty()) {
            final Row appRow = dataObject.getFirstRow("AppLockPolicyApps");
            final Row row = dataObject.getFirstRow("RecentProfileForResource");
            final JSONObject object = new JSONObject();
            object.put("APP_GROUP_ID", (Object)appRow.get("APP_GROUP_ID"));
            object.put("COLLECTION_ID", (Object)row.get("COLLECTION_ID"));
            return object;
        }
        return null;
    }
    
    public List isNeedToAddUpdateKiosk(final JSONObject appObject) throws Exception {
        final List resourceList = (List)appObject.opt("resourceList");
        final Long collectionId = appObject.optLong("collectionId");
        final List kioskList = new ArrayList();
        for (int i = 0; i < resourceList.size(); ++i) {
            final Long resourceId = resourceList.get(i);
            final Criteria successCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
            final Criteria singleAppKiosk = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)1, 0);
            final JSONObject kiosk = this.isKioskProfileAssociatedToResourceForApp(resourceId, successCriteria.and(singleAppKiosk), collectionId);
            if (kiosk != null) {
                final Long profileCollectionId = kiosk.getLong("COLLECTION_ID");
                final JSONObject object = new JSONObject();
                object.put("profileCollectionId", (Object)profileCollectionId);
                object.put("collectionId", (Object)collectionId);
                object.put("resourceId", (Object)resourceId);
                object.put("customerId", appObject.optLong("customerId"));
                object.put("UserId", (Object)appObject.optString("UserId"));
                object.put("isAppUpgrade", appObject.optBoolean("isAppUpgrade"));
                this.addKioskAppUpdateSeqCmd(object);
                kioskList.add(resourceId);
            }
            else {
                final Criteria failureCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)7, 0);
                final Criteria notValidError = new Criteria(new Column("MDMCollnToResErrorCode", "ERROR_CODE"), (Object)new int[] { 21008, 29000 }, 9);
                final JSONObject failedKiosk = this.isKioskProfileAssociatedToResourceForApp(resourceId, failureCriteria.and(notValidError).and(singleAppKiosk), collectionId);
                if (failedKiosk != null && appObject.optBoolean("isAppUpgrade")) {
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceId, Long.toString(collectionId), 7, "mdm.apps.ios.kiosk.updateProfileFailure");
                }
            }
        }
        resourceList.removeAll(kioskList);
        return kioskList;
    }
    
    public JSONObject isAnyUpdateAvailableAppForResource(final Long collectionId, final Long resourceId) throws Exception {
        final Criteria criteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new int[] { 12 }, 8);
        final MDMCollectionUtil collecitonUtil = new MDMCollectionUtil();
        final JSONObject collectionStatus = collecitonUtil.getAssociatedCollectionStatusForResource(collectionId, resourceId, criteria);
        return collectionStatus;
    }
    
    private boolean isNotEligibleForKioskSilentUpdate(final Long resourceId, final Long collectionId) throws Exception {
        final List appList = new ArrayList();
        appList.add(collectionId);
        final List appCollectionList = AppsUtil.getInstance().getEnterpriseAppsCollection(appList);
        final DataObject dataObject = InventoryUtil.getInstance().getDeviceDetailedInfo(resourceId);
        final Row deviceDetailRow = dataObject.getRow("MdDeviceInfo");
        final boolean isSupervised = (boolean)deviceDetailRow.get("IS_SUPERVISED");
        final String osVersion = (String)deviceDetailRow.get("OS_VERSION");
        final Row modelInfoRow = dataObject.getRow("MdModelInfo");
        final int modelType = (int)modelInfoRow.get("MODEL_TYPE");
        if (isSupervised && modelType == 5 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowKioskRemovalAndAddForUpdate")) {
            IOSKioskProfileDataHandler.logger.log(Level.INFO, "Apple TV.  Kiosk Removal and profile feature enabled");
            return true;
        }
        if (isSupervised && new VersionChecker().isGreaterOrEqual(osVersion, "12")) {
            IOSKioskProfileDataHandler.logger.log(Level.INFO, "iOS 12 and above device so no seq command. resourceID:{0} & collectionId:{1}", new Object[] { resourceId, collectionId });
            return false;
        }
        if (!appCollectionList.isEmpty() && isSupervised && new VersionChecker().isGreaterOrEqual(osVersion, "11.2")) {
            IOSKioskProfileDataHandler.logger.log(Level.INFO, "iOS 11.2 and Above enterprise device resourceId:{0}& appId:", new Object[] { resourceId, collectionId });
            return false;
        }
        return true;
    }
    
    public void addKioskAppUpdateSeqCmd(final JSONObject object) {
        try {
            final Long profileCollectionId = object.optLong("profileCollectionId");
            final Long collectionId = object.optLong("collectionId");
            final Long resourceId = object.optLong("resourceId");
            final String userId = object.optString("UserId");
            final Long customerId = object.optLong("customerId");
            if (profileCollectionId != 0L) {
                final boolean notEligible = this.isNotEligibleForKioskSilentUpdate(resourceId, collectionId);
                if (notEligible) {
                    Long commandId = IOSSeqCmdUtil.getInstance().getCommandIDForKioskSeqCommand(profileCollectionId, collectionId, "KioskUpdateProfile", collectionId);
                    if (commandId == null) {
                        commandId = IOSSeqCmdUtil.getInstance().createKioskAppUpdateSeqCmd(collectionId, profileCollectionId, resourceId, customerId);
                    }
                    final List commandList = new ArrayList();
                    commandList.add(commandId);
                    final List resourceList = new ArrayList();
                    resourceList.add(resourceId);
                    final AppsUtil appHandler = new AppsUtil();
                    final Properties isEliglible = appHandler.isiOSDeviceApplicableForSilentDistribution(collectionId, resourceList, customerId);
                    final List applicableResourceList = ((Hashtable<K, List>)isEliglible).get("RESOURCELIST");
                    if (applicableResourceList != null && applicableResourceList.size() != 0) {
                        final JSONObject params = new JSONObject();
                        params.put("UserId", (Object)userId);
                        SeqCmdRepository.getInstance().executeSequentially(applicableResourceList, commandList, params);
                        final JSONObject appDetailsObject = AppsUtil.getInstance().getAppDetailsFromCollectionId(collectionId);
                        final String appName = appDetailsObject.optString("GROUP_DISPLAY_NAME");
                        final Long appId = appDetailsObject.optLong("APP_ID");
                        final Long appGroupId = appDetailsObject.optLong("APP_GROUP_ID");
                        final JSONObject lockscreenJSON = new JSONObject();
                        lockscreenJSON.put("resourceId", (Object)resourceId);
                        lockscreenJSON.put("phoneNumber", (Object)"");
                        lockscreenJSON.put("lockMessage", (Object)I18N.getMsg("mdm.apps.ios.kioskApp_updating_remoteLock", new Object[] { appName }));
                        LockScreenMessageUtil.getInstance().addorUpdateLockScreenMessage(lockscreenJSON);
                        NotificationHandler.getInstance().SendNotification(applicableResourceList, 1);
                        resourceList.removeAll(applicableResourceList);
                        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                        if (object.optBoolean("isAppUpgrade")) {
                            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(applicableResourceList, collectionId, 18, "dc.db.mdm.apps.status.automatic_update");
                            handler.updateAppInstallationStatus(resourceId, appGroupId, appId, 0, "dc.db.mdm.apps.status.automatic_update", 0);
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(applicableResourceList, collectionId, 18, "dc.db.mdm.apps.status.automatic_install");
                            handler.updateAppInstallationStatus(resourceId, appGroupId, appId, 0, "dc.db.mdm.apps.status.automatic_install", 0);
                        }
                    }
                    final String helpUrl = "/help/profile_management/ios/mdm_app_lock.html#update_non_vpp_apps";
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionId, 7, "mdm.apps.ios.kiosk_silent_notApplicable@@@<a target='blank' href=\"$(mdmUrl)" + helpUrl + "?$(traceurl)&$(did)" + "\">@@@</a>");
                }
                else {
                    final List collectionList = new ArrayList();
                    collectionList.add(collectionId);
                    final List automateResourceList = new ArrayList();
                    automateResourceList.add(resourceId);
                    final List commandId2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandId2, automateResourceList);
                    NotificationHandler.getInstance().SendNotification(automateResourceList, 1);
                }
            }
        }
        catch (final Exception e) {
            IOSKioskProfileDataHandler.logger.log(Level.SEVERE, "Exception While Creating updateSeqCmd", e);
        }
    }
    
    public DataObject getKioskProfileDetails(final Long collectionID, final Long customerId) throws DataAccessException {
        final SelectQuery kioskQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        kioskQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        kioskQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        kioskQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        kioskQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        final Criteria criteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionID, 0);
        kioskQuery.setCriteria(criteria);
        kioskQuery.addSelectColumn(new Column("AppLockPolicy", "CONFIG_DATA_ITEM_ID"));
        kioskQuery.addSelectColumn(new Column("AppLockPolicy", "KIOSK_MODE"));
        kioskQuery.addSelectColumn(new Column("AppLockPolicy", "IDENTIFIER"));
        kioskQuery.addSelectColumn(new Column("AppLockPolicy", "AUTO_DISTRIBUTE_APPS"));
        kioskQuery.addSelectColumn(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"));
        kioskQuery.addSelectColumn(new Column("AppLockPolicyApps", "APP_GROUP_ID"));
        final DataObject kioskDO = MDMUtil.getPersistence().get(kioskQuery);
        return kioskDO;
    }
    
    public JSONObject profileDetailsForIOSKioskAutomation(final Long collectionID, final Long customerId) {
        final JSONObject applicableApps = new JSONObject();
        try {
            final DataObject kioskDO = this.getKioskProfileDetails(collectionID, customerId);
            if (!kioskDO.isEmpty()) {
                final Row kioskRow = kioskDO.getFirstRow("AppLockPolicy");
                if (kioskDO.containsTable("AppLockPolicyApps")) {
                    final List appGroupIDs = new ArrayList();
                    final Iterator<Row> appRows = kioskDO.getRows("AppLockPolicyApps");
                    while (appRows.hasNext()) {
                        final Row appRow = appRows.next();
                        appGroupIDs.add(appRow.get("APP_GROUP_ID"));
                    }
                    applicableApps.put("APP_GROUP_ID", (Object)new JSONArray((Collection)appGroupIDs));
                }
                if (!kioskDO.containsTable("AppLockPolicyApps")) {
                    final String bundleIdentifier = (String)kioskRow.get("IDENTIFIER");
                    final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(bundleIdentifier, 1, customerId);
                    applicableApps.put("APP_GROUP_ID", (Collection)Arrays.asList(appGroupID));
                }
                final Integer appType = (Integer)kioskRow.get("KIOSK_MODE");
                final Boolean autoDistributeApps = (Boolean)kioskRow.get("AUTO_DISTRIBUTE_APPS");
                applicableApps.put("KIOSK_MODE", (Object)appType);
                applicableApps.put("AUTO_DISTRIBUTE_APPS", (Object)autoDistributeApps);
            }
        }
        catch (final Exception e) {
            IOSKioskProfileDataHandler.logger.log(Level.SEVERE, "Error while retriving Kiosk App", e);
        }
        return applicableApps;
    }
    
    public JSONObject getLatestSingleWebAppConfiguration(final Long resourceId) {
        final JSONObject singleWebAppJSON = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            selectQuery.addJoin(new Join("AppLockPolicy", "WebClipToConfigRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            selectQuery.addJoin(new Join("WebClipToConfigRel", "WebClipPolicies", new String[] { "WEBCLIP_POLICY_ID" }, new String[] { "WEBCLIP_POLICY_ID" }, 2));
            final Criteria kioskTypeCriteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)3, 0);
            final Criteria isRemoved = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria resourceIdCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria profileStatusCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new Integer[] { 18, 3, 12, 16, 6 }, 8);
            selectQuery.setRange(new Range(0, 1));
            selectQuery.addSortColumn(new SortColumn(new Column("ResourceToProfileHistory", "LAST_MODIFIED_TIME"), true));
            selectQuery.setCriteria(kioskTypeCriteria.and(isRemoved).and(profileStatusCriteria).and(resourceIdCriteria));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("AppLockPolicy", "CONFIG_DATA_ITEM_ID"));
            selectQuery.addSelectColumn(new Column("AppLockPolicy", "IDLE_REFRESH_TIMEOUT"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "WEBCLIP_URL"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "WEBCLIP_LABEL"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row webclipRow = dataObject.getFirstRow("WebClipPolicies");
                final String webclipURL = (String)webclipRow.get("WEBCLIP_URL");
                final String webClipLabel = (String)webclipRow.get("WEBCLIP_LABEL");
                final Row cfgRow = dataObject.getFirstRow("CfgDataToCollection");
                final Long collectionId = (Long)cfgRow.get("COLLECTION_ID");
                final Row appLockRow = dataObject.getFirstRow("AppLockPolicy");
                final int refreshTimeOut = (int)appLockRow.get("IDLE_REFRESH_TIMEOUT");
                singleWebAppJSON.put("WEBCLIP_URL", (Object)webclipURL);
                singleWebAppJSON.put("COLLECTION_ID", (Object)collectionId);
                singleWebAppJSON.put("IDLE_REFRESH_TIMEOUT", refreshTimeOut);
                singleWebAppJSON.put("WEBCLIP_LABEL", (Object)webClipLabel);
            }
        }
        catch (final Exception e) {
            IOSKioskProfileDataHandler.logger.log(Level.SEVERE, "Exception in getting single web app configuration", e);
        }
        return singleWebAppJSON;
    }
    
    public boolean isMEMDMIsRestricted(final Long resourceId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria kioskTypeCriteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)2, 0).and(new Criteria(new Column("AppLockPolicy", "SHOW_ME_MDM_APP"), (Object)false, 0));
        final Criteria isRemoved = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria resourceIdCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileStatusCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new Integer[] { 18, 3, 12, 16, 6 }, 8);
        selectQuery.setCriteria(kioskTypeCriteria.and(isRemoved).and(profileStatusCriteria).and(resourceIdCriteria));
        selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    static {
        IOSKioskProfileDataHandler.logger = Logger.getLogger("MDMConfigLogger");
        IOSKioskProfileDataHandler.mdmlogger = Logger.getLogger("MDMLogger");
    }
}
