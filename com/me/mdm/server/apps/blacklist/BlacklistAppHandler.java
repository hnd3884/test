package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.Collection;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONArray;
import com.me.mdm.core.management.ManagementUtil;
import com.me.mdm.core.management.ManagementConstants;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class BlacklistAppHandler
{
    public static final String APP_ID_LIST = "appIDs";
    public static final String APP_IDENTIFIER_MAP = "appIdentifierMap";
    public static final String RESOURCE_LIST = "resourceIDs";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String APP_GROUP_DETAILS = "appGroupdetails";
    public static final String USER_ID = "userID";
    public static final String PROFILE_COLLECTION_MAP = "profileCollectionMap";
    public static final String IDENTIFIER = "identifier";
    public static final String APP_NAME = "appname";
    public static final String VERSION = "version";
    public static final String CODE_REQUIREMENT = "code_requirement";
    public static final String SIGNING_IDENTIFIER = "signing_identifier";
    public static final String INSTALLATION_PATH = "installation_path";
    public static final int[] platforms;
    public static final int USER_GROUP = 3;
    public static final int PLATFORM_IND_GROUP = 1;
    public static final int DEVICE = 2;
    public static final int USER = 4;
    public static final int NETWORK = 5;
    public static final int BLACKLIST_APP_OPERATION = 1;
    public static final int REMOVE_BLACKLIST_APP_OPERATION = 2;
    public static final int DEVICE_SCOPE = 0;
    public static final int CONTAINER_SCOPE = 1;
    public static final String SCOPE = "scope";
    public static final String TYPE = "Type";
    public static final String OPERATION = "Operation";
    public static final int NOTIFY = 1;
    public static final int UNINSTALL = 2;
    public static final int NOTIFY_UNISTALL = 3;
    private Logger logger;
    
    public BlacklistAppHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public void blackListAppWithIdentifier(final HashMap params, final Long customerID) throws Exception {
        this.logger.log(Level.INFO, "Blacklist Action Being Performed : (origin : blackListAppWithIdentifier){0}", params.toString());
        try {
            final HashMap appIdentifiers = params.get("appIdentifierMap");
            final List appGroupIds = this.addAndGetAppsInRepository(appIdentifiers, customerID);
            params.put("appGroupdetails", appGroupIds);
            this.blackListAppWithAppDetails(params, customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error in performing Blaklist operation. : ", e);
            throw e;
        }
        this.logger.log(Level.INFO, "Blacklist operation was completed");
    }
    
    public void blackListAppsWithAppGroupIds(final HashMap params, final Long customerID) throws Exception {
        this.logger.log(Level.INFO, "Blacklist Action Being Performed : (origin : blackListAppsWithAppGroupIds){0}", params.toString());
        try {
            final List appIds = params.get("appIDs");
            final List appDetailsList = this.getAppGroupDetails(appIds);
            params.put("appGroupdetails", appDetailsList);
            this.blackListAppWithAppDetails(params, customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error in performing Blaklist operation. : ", e);
            throw e;
        }
        this.logger.log(Level.INFO, "Blacklist operation was completed");
    }
    
    public void addOrUpdateAppBlackListSettings(final Properties params, final Long customerID) throws DataAccessException {
        this.logger.log(Level.INFO, "Blacklist settings being updated");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppSettings"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppSettings", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        Integer setting = ((Hashtable<K, Integer>)params).get("BLACKLIST_ACTION_TYPE");
        Integer days = ((Hashtable<K, Integer>)params).get("NOTIFY_DAYS");
        if (setting == null) {
            setting = 0;
        }
        if (days == null) {
            days = 0;
        }
        Row row = dataObject.getFirstRow("BlacklistAppSettings");
        if (row != null) {
            row.set("BLACKLIST_ACTION_TYPE", (Object)setting);
            row.set("NOTIFY_DAYS", (Object)days);
            dataObject.updateRow(row);
        }
        else {
            row = new Row("BlacklistAppSettings");
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("BLACKLIST_ACTION_TYPE", (Object)setting);
            row.set("NOTIFY_DAYS", (Object)days);
            dataObject.addRow(row);
        }
        MDMUtil.getPersistence().update(dataObject);
        this.logger.log(Level.INFO, "Blacklist settings updated to Notify type : {0}  notify days : {1}", new Object[] { setting, days });
    }
    
    public JSONObject getBlackListAppSettings(final Long customerID) throws Exception {
        final JSONObject settings = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppSettings"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppSettings", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        Row row = null;
        if (!dataObject.isEmpty()) {
            row = dataObject.getFirstRow("BlacklistAppSettings");
        }
        if (row == null) {
            this.logger.log(Level.INFO, "Settings not present in DB, adding default values");
            settings.put("message", (Object)"settings not configured for the customer adding and returning default settings");
            row = new Row("BlacklistAppSettings");
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("BLACKLIST_ACTION_TYPE", (Object)2);
            row.set("NOTIFY_DAYS", (Object)0);
            dataObject.addRow(row);
            MDMUtil.getPersistence().update(dataObject);
            settings.put("BLACKLIST_ACTION_TYPE", 2);
            settings.put("NOTIFY_DAYS", 0);
        }
        else {
            settings.put("BLACKLIST_ACTION_TYPE", row.get("BLACKLIST_ACTION_TYPE"));
            settings.put("NOTIFY_DAYS", row.get("NOTIFY_DAYS"));
        }
        this.logger.log(Level.INFO, "Blacklist settings Queried : {0}", settings);
        return settings;
    }
    
    private void blackListAppWithAppDetails(final HashMap params, final Long customerID) throws Exception {
        final Long userID = params.get("userID");
        final int type = params.get("resourceType");
        final HashMap associationParams = new HashMap();
        associationParams.put("userID", userID);
        associationParams.put("CUSTOMER_ID", customerID);
        associationParams.put("Operation", params.get("Operation"));
        final List appGroupIds = params.get("appGroupdetails");
        final HashMap profileCollnMap = this.addAndGetProfileCollectionMap(appGroupIds, associationParams);
        params.put("profileCollectionMap", profileCollnMap);
        final int opertaion = params.get("Operation");
        params.put("CUSTOMER_ID", customerID);
        if (opertaion == 1) {
            BaseBlacklistAppHandler.getBlacklistHandler(type).blacklistAppInResource(params);
        }
        else if (opertaion == 2) {
            BaseBlacklistAppHandler.getBlacklistHandler(type).removeBlacklistAppInResource(params);
        }
    }
    
    private Criteria combineCriteria(Criteria baseCriteria, final Criteria combineCriteria, final int operation) {
        if (baseCriteria == null) {
            baseCriteria = combineCriteria;
        }
        else if (operation == 1) {
            baseCriteria = baseCriteria.and(combineCriteria);
        }
        else if (operation == 2) {
            baseCriteria = baseCriteria.or(combineCriteria);
        }
        return baseCriteria;
    }
    
    public List addAndGetAppsInRepository(final HashMap appIdentifiers, final Long customerID) throws Exception {
        return this.addAndGetAppsInRepository(appIdentifiers, customerID, false);
    }
    
    public List addAndGetAppsInRepository(final HashMap appIdentifiers, final Long customerID, final boolean validate) throws Exception {
        final List appgroupIds = new ArrayList();
        final DataObject dataObject = this.getAppDO(appIdentifiers, customerID);
        if (validate && !dataObject.isEmpty()) {
            throw new APIHTTPException("APP0027", new Object[0]);
        }
        for (final int platform : BlacklistAppHandler.platforms) {
            final List apps = appIdentifiers.get(platform);
            if (apps != null) {
                for (final JSONObject curApp : apps) {
                    final String identifier = String.valueOf(curApp.get("identifier"));
                    final String grpDisplayName = curApp.optString("appname", identifier);
                    final String appVersion = curApp.optString("version", (String)null);
                    final String codeRequirement = curApp.optString("code_requirement", (String)null);
                    final String codeSignature = curApp.optString("signing_identifier", (String)null);
                    final String installationPath = curApp.optString("INSTALLATION_PATH".toLowerCase(), (String)null);
                    final Criteria IdentiferCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0);
                    final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
                    final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
                    Row row = dataObject.getRow("MdAppGroupDetails", IdentiferCriteria.and(platformCriteria).and(customerCriteria));
                    if (row == null) {
                        row = new Row("MdAppGroupDetails");
                        row.set("IDENTIFIER", (Object)identifier);
                        row.set("PLATFORM_TYPE", (Object)platform);
                        row.set("GROUP_DISPLAY_NAME", (Object)grpDisplayName);
                        row.set("ADDED_TIME", (Object)System.currentTimeMillis());
                        row.set("APP_TYPE", (Object)2);
                        row.set("CUSTOMER_ID", (Object)customerID);
                        dataObject.addRow(row);
                        this.logger.log(Level.INFO, "New App found during Blacklist action : {0} : {1}", new Object[] { grpDisplayName, identifier });
                        this.addAppDetailRel(dataObject, curApp, customerID, platform);
                    }
                    else if (appVersion != null && !appVersion.isEmpty()) {
                        final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                        final Row appRelRow = dataObject.getRow("MdAppToGroupRel", new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0));
                        if (appRelRow == null) {
                            this.addAppDetailRel(dataObject, curApp, customerID, platform);
                        }
                    }
                    this.addMacAppProperiesRow(dataObject, row.get("APP_GROUP_ID"), codeRequirement, installationPath, codeSignature);
                }
            }
        }
        MDMUtil.getPersistence().update(dataObject);
        final Iterator iterator2 = dataObject.getRows("MdAppGroupDetails");
        while (iterator2.hasNext()) {
            final Row row2 = iterator2.next();
            final Long appGrpID = (Long)row2.get("APP_GROUP_ID");
            final String identifier2 = (String)row2.get("IDENTIFIER");
            final String appName = (String)row2.get("GROUP_DISPLAY_NAME");
            final Integer platform2 = (Integer)row2.get("PLATFORM_TYPE");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("APP_GROUP_ID", (Object)appGrpID);
            jsonObject.put("IDENTIFIER", (Object)identifier2);
            jsonObject.put("GROUP_DISPLAY_NAME", (Object)appName);
            jsonObject.put("PLATFORM_TYPE", (Object)platform2);
            appgroupIds.add(jsonObject);
        }
        this.logger.log(Level.INFO, "newly Detected Apps were added successfully during blacklist action");
        return appgroupIds;
    }
    
    private void addAppDetailRel(final DataObject dataObject, final JSONObject appObject, final Long customerId, final int platformType) throws Exception {
        final String identifier = String.valueOf(appObject.get("identifier"));
        final String grpDisplayName = appObject.optString("appname", identifier);
        final String appVersion = appObject.optString("version", "--");
        final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)identifier, 0);
        final Criteria versionCriteria = new Criteria(new Column("MdAppDetails", "APP_VERSION"), (Object)appVersion, 2);
        Row appDetailsRow = dataObject.getRow("MdAppDetails", identifierCriteria.and(versionCriteria));
        if (appDetailsRow == null) {
            appDetailsRow = new Row("MdAppDetails");
            appDetailsRow.set("PLATFORM_TYPE", (Object)platformType);
            appDetailsRow.set("CUSTOMER_ID", (Object)customerId);
            appDetailsRow.set("APP_TYPE", (Object)0);
            appDetailsRow.set("APP_NAME", (Object)grpDisplayName);
            appDetailsRow.set("APP_VERSION", (Object)appVersion);
            appDetailsRow.set("APP_NAME_SHORT_VERSION", (Object)appVersion);
            appDetailsRow.set("IDENTIFIER", (Object)identifier);
            final Row appGroupRow = dataObject.getRow("MdAppGroupDetails", new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0));
            final Object appGroupId = appGroupRow.get("APP_GROUP_ID");
            final Row appGroupRelRow = new Row("MdAppToGroupRel");
            appGroupRelRow.set("APP_GROUP_ID", appGroupId);
            appGroupRelRow.set("APP_ID", appDetailsRow.get("APP_ID"));
            dataObject.addRow(appDetailsRow);
            dataObject.addRow(appGroupRelRow);
            this.logger.log(Level.INFO, "added app version details during blacklist");
        }
        else {
            this.logger.log(Level.INFO, "No app version details is added during blacklist");
        }
    }
    
    private void addMacAppProperiesRow(final DataObject dataObject, final Object appGroupID, String codeRqmts, String installationPath, final String codeSignature) throws Exception {
        if (MDMStringUtils.isEmpty(codeRqmts)) {
            return;
        }
        codeRqmts = MDMStringUtils.getDecodedString(codeRqmts);
        Row row;
        if (appGroupID instanceof UniqueValueHolder) {
            row = null;
        }
        else {
            row = DBUtil.getRowFromDB("MacAppProperties", "APP_GROUP_ID", appGroupID);
        }
        if (row == null) {
            row = new Row("MacAppProperties");
            row.set("APP_GROUP_ID", appGroupID);
            row.set("CODE_REQUIREMENT", (Object)codeRqmts);
            row.set("INSTALLATION_PATH", (Object)installationPath);
            row.set("SIGNING_IDENTIFIER", (Object)codeSignature);
            dataObject.addRow(row);
        }
        else {
            row.set("CODE_REQUIREMENT", (Object)codeRqmts);
            row.set("APP_GROUP_ID", appGroupID);
            installationPath = (MDMStringUtils.isEmpty(installationPath) ? null : installationPath);
            row.set("INSTALLATION_PATH", (Object)installationPath);
            if (!MDMStringUtils.isEmpty(codeSignature)) {
                row.set("SIGNING_IDENTIFIER", (Object)codeSignature);
            }
            dataObject.updateRow(row);
        }
    }
    
    private DataObject getAppDO(final HashMap appIdentifiers, final Long customerId) throws Exception {
        Criteria combinedCriteria = null;
        for (final int i : BlacklistAppHandler.platforms) {
            final List apps = appIdentifiers.get(i);
            if (apps != null) {
                final Iterator iterator = apps.iterator();
                final List appIds = new ArrayList();
                while (iterator.hasNext()) {
                    final JSONObject jsonObject = iterator.next();
                    appIds.add(jsonObject.get("identifier"));
                }
                final Criteria IdentiferCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)appIds.toArray(), 8);
                final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)i, 0);
                combinedCriteria = this.combineCriteria(combinedCriteria, IdentiferCriteria.and(platformCriteria), 2);
            }
        }
        final Criteria customerIdCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        combinedCriteria = combinedCriteria.and(customerIdCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(combinedCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    private HashMap addAndGetProfileCollectionMap(final List appGroupIDs, final HashMap params) throws Exception {
        final Iterator iterator = appGroupIDs.iterator();
        final List appList = new ArrayList();
        final Long customerID = params.get("CUSTOMER_ID");
        final HashMap profileCollectionMap = new HashMap();
        final Long userID = params.get("userID");
        while (iterator.hasNext()) {
            final JSONObject jsonObject = iterator.next();
            appList.add(jsonObject.get("APP_GROUP_ID"));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appList.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        DMDataSetWrapper ds = null;
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final List containingAppIds = new ArrayList();
        while (ds.next()) {
            final Long appGroupId = (Long)ds.getValue("APP_GROUP_ID");
            final Long profileID = (Long)ds.getValue("PROFILE_ID");
            final Long collectionID = (Long)ds.getValue("COLLECTION_ID");
            containingAppIds.add(appGroupId);
            profileCollectionMap.put(profileID, collectionID);
        }
        final Iterator itr = appGroupIDs.iterator();
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Long managementValue = ManagementUtil.getManagementIDForType(ManagementConstants.Types.MOBILE_MGMT);
        while (itr.hasNext()) {
            final JSONObject jsonObject2 = itr.next();
            final Long appGrpID = (Long)jsonObject2.get("APP_GROUP_ID");
            if (!containingAppIds.contains(appGrpID)) {
                final Row profileRow = new Row("Profile");
                String appName = String.valueOf(jsonObject2.get("GROUP_DISPLAY_NAME"));
                if (appName.length() > 71) {
                    appName = appName.substring(0, 70);
                }
                final String appIdentifier = String.valueOf(jsonObject2.get("IDENTIFIER"));
                final Integer platformType = (Integer)jsonObject2.get("PLATFORM_TYPE");
                profileRow.set("PROFILE_NAME", (Object)appName);
                profileRow.set("PROFILE_DESCRIPTION", (Object)(appName + " Blacklist profile"));
                profileRow.set("PROFILE_IDENTIFIER", (Object)(appIdentifier + ";BlackList"));
                profileRow.set("PROFILE_PAYLOAD_IDENTIFIER", (Object)(appIdentifier + ";BlackList"));
                profileRow.set("PROFILE_TYPE", (Object)4);
                profileRow.set("CREATED_BY", (Object)userID);
                profileRow.set("CREATION_TIME", (Object)System.currentTimeMillis());
                profileRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                profileRow.set("LAST_MODIFIED_BY", (Object)userID);
                profileRow.set("PLATFORM_TYPE", (Object)platformType);
                final Row profileToCustomerRel = new Row("ProfileToCustomerRel");
                profileToCustomerRel.set("CUSTOMER_ID", (Object)customerID);
                profileToCustomerRel.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                final Row collectionRow = new Row("Collection");
                collectionRow.set("COLLECTION_NAME", (Object)appName);
                collectionRow.set("DESCRIPTION", (Object)(appName + " Blacklist Profile"));
                collectionRow.set("CREATION_TIME", (Object)System.currentTimeMillis());
                collectionRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                collectionRow.set("COLLECTION_TYPE", (Object)3);
                collectionRow.set("IS_CONFIG_COLLECTION", (Object)false);
                collectionRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                final Row profileToCollectionRow = new Row("ProfileToCollection");
                profileToCollectionRow.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                profileToCollectionRow.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
                final Row recentProfileToColln = new Row("RecentProfileToColln");
                recentProfileToColln.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                recentProfileToColln.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
                final Row recentPublishedProfile = new Row("RecentPubProfileToColln");
                recentPublishedProfile.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                recentPublishedProfile.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
                final Row blackListAppToCollection = new Row("BlacklistAppToCollection");
                blackListAppToCollection.set("APP_GROUP_ID", (Object)appGrpID);
                blackListAppToCollection.set("COLLECTION_ID", collectionRow.get("COLLECTION_ID"));
                if (params.get("Operation") == 2) {
                    blackListAppToCollection.set("APPLIED_STATUS", (Object)Boolean.FALSE);
                }
                final Row manaegementRow = new Row("ProfileToManagement");
                manaegementRow.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                manaegementRow.set("MANAGEMENT_ID", (Object)managementValue);
                dataObject.addRow(profileRow);
                dataObject.addRow(collectionRow);
                dataObject.addRow(profileToCollectionRow);
                dataObject.addRow(recentProfileToColln);
                dataObject.addRow(recentPublishedProfile);
                dataObject.addRow(profileToCustomerRel);
                dataObject.addRow(blackListAppToCollection);
                dataObject.addRow(manaegementRow);
            }
        }
        MDMUtil.getPersistence().update(dataObject);
        final Iterator iterator2 = dataObject.getRows("ProfileToCollection");
        while (iterator2.hasNext()) {
            final Row row = iterator2.next();
            final Long profileID2 = (Long)row.get("PROFILE_ID");
            final Long collectionID2 = (Long)row.get("COLLECTION_ID");
            profileCollectionMap.put(profileID2, collectionID2);
        }
        return profileCollectionMap;
    }
    
    private List getAppGroupDetails(final List appIds) throws Exception {
        final List appList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appIds.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdAppGroupDetails");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Integer platformtype = (Integer)row.get("PLATFORM_TYPE");
            final Long appID = (Long)row.get("APP_GROUP_ID");
            final String identifier = (String)row.get("IDENTIFIER");
            final String displayname = (String)row.get("GROUP_DISPLAY_NAME");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("APP_GROUP_ID", (Object)appID);
            jsonObject.put("PLATFORM_TYPE", (Object)platformtype);
            jsonObject.put("IDENTIFIER", (Object)identifier);
            jsonObject.put("GROUP_DISPLAY_NAME", (Object)displayname);
            appList.add(jsonObject);
        }
        return appList;
    }
    
    public JSONArray getResourceDetails(final List resourceIDs) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final JSONObject jsonObject = new JSONObject();
                final Long resId = (Long)ds.getValue("RESOURCE_ID");
                final String username = (String)ds.getValue("DISPLAY_NAME");
                final String usermail = (String)ds.getValue("EMAIL_ADDRESS");
                final int platform = (int)ds.getValue("PLATFORM_TYPE");
                final Boolean isSupervised = (Boolean)ds.getValue("IS_SUPERVISED");
                final String osVersion = (String)ds.getValue("OS_VERSION");
                final String resName = (String)ds.getValue("NAME");
                jsonObject.put("RESOURCE_ID", (Object)resId);
                jsonObject.put("PLATFORM_TYPE", platform);
                if (isSupervised != null) {
                    jsonObject.put("IS_SUPERVISED", (Object)isSupervised);
                }
                if (osVersion != null) {
                    jsonObject.put("OS_VERSION", (Object)osVersion);
                }
                jsonObject.put("DISPLAY_NAME", (Object)username);
                jsonObject.put("EMAIL_ADDRESS", (Object)usermail);
                jsonObject.put("NAME", (Object)resName);
                jsonArray.put((Object)jsonObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getResourceDetails() Exception -- ", e);
        }
        return jsonArray;
    }
    
    public List getAppNames(final List collectionIDs) throws DataAccessException {
        final List appnames = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)collectionIDs.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdAppGroupDetails");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String appName = (String)row.get("GROUP_DISPLAY_NAME");
            appnames.add(appName);
        }
        return appnames;
    }
    
    public HashMap getNetworkLevelBlacklistedApps(final Long customerID, final Integer platformType) throws DataAccessException {
        final HashMap profileCollectionMap = new HashMap();
        final HashMap associatedUserMap = new HashMap();
        final HashMap hashMap = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria blacklistCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 0);
        if (platformType != null) {
            final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
            blacklistCriteria = blacklistCriteria.and(platformCriteria);
        }
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(blacklistCriteria.and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        Iterator iterator = dataObject.getRows("ProfileToCollection");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long profileID = (Long)row.get("PROFILE_ID");
            final Long collectionID = (Long)row.get("COLLECTION_ID");
            profileCollectionMap.put(profileID, collectionID);
        }
        iterator = dataObject.getRows("Profile");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final HashMap profileProps = new HashMap();
            final Long profileID2 = (Long)row.get("PROFILE_ID");
            final Long userID = (Long)row.get("LAST_MODIFIED_BY");
            profileProps.put("associatedByUser", userID);
            associatedUserMap.put(profileID2, profileProps);
        }
        hashMap.put("profileCollectionMap", profileCollectionMap);
        hashMap.put("profileProperties", associatedUserMap);
        return hashMap;
    }
    
    public HashMap appSummaryDetails(final Long customerID) throws Exception {
        return this.appSummaryDetails(customerID, true);
    }
    
    public HashMap appSummaryDetails(final Long customerID, final List requiredData) throws Exception {
        return this.appSummaryDetails(customerID, true, requiredData);
    }
    
    public HashMap appSummaryDetails(final Long customerID, final Boolean hideOtherApps) throws Exception {
        return this.appSummaryDetails(customerID, hideOtherApps, null);
    }
    
    public HashMap appSummaryDetails(final Long customerID, final Boolean hideOtherApps, final List requiredData) throws Exception {
        final HashMap appDetails = new HashMap();
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        if (requiredData == null || requiredData.contains("managedCount")) {
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria installedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
            selectQuery.setCriteria(customerCriteria.and(installedCriteria));
            appDetails.put("managedCount", DBUtil.getRecordCount(selectQuery, "MdAppCatalogToResource", "APP_GROUP_ID"));
        }
        if (requiredData == null || requiredData.contains("blacklistCount")) {
            SelectQuery countQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
            countQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            countQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria localCriteria = new Criteria(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)Boolean.FALSE, 0);
            countQuery.setCriteria(localCriteria.and(customerCriteria));
            final Integer localBlacklist = MDMDBUtil.getDistinctCount(countQuery, "BlacklistAppToCollection", "COLLECTION_ID").optInt("DISTINCT_COUNT", 0);
            countQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
            countQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria networkCriteria = new Criteria(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)Boolean.TRUE, 0);
            countQuery.setCriteria(networkCriteria.and(customerCriteria));
            final Integer globalBlacklist = MDMDBUtil.getDistinctCount(countQuery, "BlacklistAppToCollection", "COLLECTION_ID").optInt("DISTINCT_COUNT", 0);
            appDetails.put("blacklistCount", globalBlacklist + localBlacklist);
        }
        this.logger.log(Level.INFO, "blcnr complete");
        if (requiredData == null || requiredData.contains("discoveredCount")) {
            selectQuery = BlacklistQueryUtils.getInstance().getDiscoveredCount(customerID, hideOtherApps);
            appDetails.put("discoveredCount", MDMDBUtil.getDistinctCount(selectQuery, "MdAppToGroupRel", "APP_GROUP_ID").optInt("DISTINCT_COUNT", 0));
        }
        this.logger.log(Level.INFO, "discovered complete");
        if (requiredData == null || requiredData.contains("devicesWithBlacklistedApps")) {
            selectQuery = BlacklistQueryUtils.getInstance().getDeviceWithBlacklistAppCount(customerID);
            appDetails.put("devicesWithBlacklistedApps", MDMDBUtil.getDistinctCount(selectQuery, "ManagedDevice", "RESOURCE_ID").optInt("DISTINCT_COUNT", 0));
        }
        return appDetails;
    }
    
    public JSONObject getBlacklistedAppDetails(final Long customerId, final Long appGroupId) throws APIHTTPException {
        try {
            final JSONObject appDetails = new JSONObject();
            final HashMap appGroupDetails = MDMUtil.getInstance().getAppGroupDetails(appGroupId);
            if (appGroupDetails == null) {
                throw new APIHTTPException("COM0008", new Object[] { appGroupId });
            }
            appDetails.put("APP_GROUP_ID", appGroupDetails.get("APP_GROUP_ID"));
            appDetails.put("IDENTIFIER", appGroupDetails.get("IDENTIFIER"));
            appDetails.put("PLATFORM_TYPE", appGroupDetails.get("PLATFORM_TYPE"));
            appDetails.put("GROUP_DISPLAY_NAME", appGroupDetails.get("GROUP_DISPLAY_NAME"));
            final SelectQuery installCountQuery = BlacklistQueryUtils.getInstance().installAppCount(customerId, appGroupId);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)installCountQuery);
            if (dmDataSetWrapper != null && dmDataSetWrapper.next()) {
                final Object count = dmDataSetWrapper.getValue("INSTALL_COUNT");
                appDetails.put("install_count", (Object)Long.valueOf(count.toString()));
            }
            final int blacklistType = BlacklistQueryUtils.getInstance().getBlacklistStatus(customerId, appGroupId);
            appDetails.put("blacklist_type", blacklistType);
            if (blacklistType != 2) {
                final SelectQuery blacklistPendingCountQuery = BlacklistQueryUtils.getInstance().blacklistPendingCountQuery(customerId, appGroupId);
                final DMDataSetWrapper dmDataSetWrapper2 = DMDataSetWrapper.executeQuery((Object)blacklistPendingCountQuery);
                if (dmDataSetWrapper2 != null && dmDataSetWrapper2.next()) {
                    final Object count2 = dmDataSetWrapper2.getValue("blacklistPendingCount");
                    appDetails.put("blacklist_pending_count", (Object)Long.valueOf(count2.toString()));
                }
            }
            return appDetails;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Issue on getting blacklisted app details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceToAppDetails(final Long customerId, final Long deviceId) throws APIHTTPException {
        JSONObject deviceDetails = new JSONObject();
        try {
            deviceDetails = InventoryUtil.getInstance().getDeviceUserInfo(deviceId, deviceDetails);
            deviceDetails = InventoryUtil.getInstance().getOSInfo(deviceId, deviceDetails);
            deviceDetails = InventoryUtil.getInstance().getAgentContactInfo(deviceId, deviceDetails);
            final SelectQuery installCountQuery = BlacklistQueryUtils.getInstance().installAppsonDeviceCount(customerId, deviceId);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)installCountQuery);
            if (dmDataSetWrapper != null && dmDataSetWrapper.next()) {
                final Object count = dmDataSetWrapper.getValue("INSTALL_COUNT");
                deviceDetails.put("install_count", (Object)Long.valueOf(count.toString()));
            }
            final SelectQuery blacklistPendingCount = BlacklistQueryUtils.getInstance().blacklistDeviceCountQuery(customerId, deviceId, true);
            final DMDataSetWrapper blackListWrapper = DMDataSetWrapper.executeQuery((Object)blacklistPendingCount);
            if (blackListWrapper != null && blackListWrapper.next()) {
                final Object count2 = blackListWrapper.getValue("blacklistPendingCount");
                deviceDetails.put("blacklist_pending_count", (Object)Long.valueOf(count2.toString()));
            }
            final SelectQuery blacklistCount = BlacklistQueryUtils.getInstance().blacklistDeviceCountQuery(customerId, deviceId, false);
            final DMDataSetWrapper successListWrap = DMDataSetWrapper.executeQuery((Object)blacklistCount);
            if (successListWrap != null && successListWrap.next()) {
                final Object count3 = successListWrap.getValue("blacklistPendingCount");
                deviceDetails.put("blacklisted_count", (Object)Long.valueOf(count3.toString()));
            }
            final String deviceName = (String)DBUtil.getValueFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)deviceId, "NAME");
            deviceDetails.put("device_name", (Object)deviceName);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Issue in fetching device app details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return deviceDetails;
    }
    
    public int getInstalledManagedAppCount(final Long customerId, final JSONObject viewObject) throws APIHTTPException {
        int count = 0;
        if (viewObject.optBoolean("SHOW_MANAGED_APPS")) {
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria resCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)new Column("ManagedDevice", "RESOURCE_ID"), 0);
            final Criteria appCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "APP_ID"), (Object)new Column("MdAppToGroupRel", "APP_ID"), 0);
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdInstalledAppResourceRel", resCriteria.and(appCriteria), 2));
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria installedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
            final Criteria managedCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
            selectQuery.setCriteria(customerCriteria.and(installedCriteria).and(managedCriteria));
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            try {
                count = DBUtil.getRecordCount(selectQuery, "MdAppCatalogToResource", "APP_GROUP_ID");
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Issue on getting managed app count", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        return count;
    }
    
    public Long getDiscoveredAppCount(final Long customerId, final boolean installedOnly, final JSONObject appViewData) {
        Long count = 0L;
        SelectQuery derivedQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        derivedQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        derivedQuery.addJoin(new Join("ManagedDevice", "MdInstalledAppResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        derivedQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        derivedQuery.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria appCatalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), 0);
        final Criteria managedAppCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria installedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
        derivedQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResource", appCatalogCriteria.and(managedAppCriteria).and(installedCriteria), 1));
        final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria managedDeviceCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
        derivedQuery.setCriteria(customerCriteria.and(managedDeviceCriteria));
        final Criteria showAppCriteria = AppsUtil.getInstance().showAppCriteria(customerId);
        if (showAppCriteria != null) {
            derivedQuery.setCriteria(derivedQuery.getCriteria().and(showAppCriteria));
        }
        derivedQuery = RBDAUtil.getInstance().getRBDAQuery(derivedQuery);
        final Column derivedcolumn = new Column("MdAppGroupDetails", "APP_GROUP_ID");
        derivedcolumn.setColumnAlias("APP_GROUP_ID");
        derivedQuery.addSelectColumn(derivedcolumn);
        final DerivedTable derivedTable = new DerivedTable("Installed", (Query)derivedQuery);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        final int joinType = installedOnly ? 2 : 1;
        selectQuery.addJoin(new Join(Table.getTable("MdAppGroupDetails"), (Table)derivedTable, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, joinType));
        selectQuery.setCriteria(new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0));
        final boolean isShowSystemApp = appViewData.optBoolean("SHOW_SYSTEM_APPS", false);
        if (!isShowSystemApp) {
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("MdAppGroupDetails", "APP_TYPE"), (Object)2, 1)));
        }
        final Column column = new Column("MdAppGroupDetails", "APP_GROUP_ID").distinct().count();
        column.setColumnAlias("DISCOVERED_COUNT");
        selectQuery.addSelectColumn(column);
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmDataSetWrapper != null && dmDataSetWrapper.next()) {
                count = Long.valueOf(dmDataSetWrapper.getValue("DISCOVERED_COUNT").toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Issue on fetching discovered app count", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return count;
    }
    
    public JSONObject getBlacklistedAppsOnDevice(final Long customerId, final Long deviceId) {
        final JSONObject responseJSON = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deviceCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
        final Criteria blackListedCriteria = new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)new int[] { 4, 11, 6, 7, 10 }, 8);
        selectQuery.setCriteria(customerCriteria.and(deviceCriteria).and(blackListedCriteria).and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(new Column("BlacklistAppCollectionStatus", "SCOPE"));
        selectQuery.addSelectColumn(new Column("BlacklistAppCollectionStatus", "STATUS"));
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmDataSetWrapper != null) {
                final JSONArray jsonArray = new JSONArray();
                while (dmDataSetWrapper.next()) {
                    final JSONObject appObject = new JSONObject();
                    appObject.put("IDENTIFIER", dmDataSetWrapper.getValue("IDENTIFIER"));
                    appObject.put("PLATFORM_TYPE", dmDataSetWrapper.getValue("PLATFORM_TYPE"));
                    appObject.put("GROUP_DISPLAY_NAME", dmDataSetWrapper.getValue("GROUP_DISPLAY_NAME"));
                    appObject.put("STATUS", dmDataSetWrapper.getValue("STATUS"));
                    appObject.put("SCOPE", dmDataSetWrapper.getValue("SCOPE"));
                    jsonArray.put((Object)appObject);
                }
                responseJSON.put("apps", (Object)jsonArray);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Issue on getting blacklisted apps on device", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    public Long getDeviceWithBlacklist(final Long customerId) throws APIHTTPException {
        Long count = 0L;
        final SelectQuery selectQuery = BlacklistQueryUtils.getInstance().getDeviceWithBlacklistAppCount(customerId);
        final Column column = new Column("ManagedDevice", "RESOURCE_ID").distinct().count();
        column.setColumnAlias("DEVICE_COUNT");
        selectQuery.addSelectColumn(column);
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmDataSetWrapper != null && dmDataSetWrapper.next()) {
                count = Long.valueOf(dmDataSetWrapper.getValue("DEVICE_COUNT").toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Issue on getting device with blacklist count", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return count;
    }
    
    public List<String> verifyCriticalApps(final List<Long> appGroupIds, final Long customerID) {
        final List<String> appList = new ArrayList<String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        final Criteria appCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        final Map<Integer, List<String>> criticalApps = AppMgmtConstants.CRITICAL_APPS;
        final List<String> criticalAppsList = new ArrayList<String>();
        for (final int platform : BlacklistAppHandler.platforms) {
            criticalAppsList.addAll(criticalApps.getOrDefault(platform, new ArrayList<String>()));
        }
        final Criteria criticalAppsCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)criticalAppsList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(appCriteria.and(criticalAppsCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("MdAppGroupDetails");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final int platFormType = (int)row.get("PLATFORM_TYPE");
                    final String identifier = (String)row.get("IDENTIFIER");
                    if (criticalApps.containsKey(platFormType) && criticalApps.get(platFormType).contains(identifier)) {
                        appList.add(StringEscapeUtils.escapeHtml((String)row.get("GROUP_DISPLAY_NAME")));
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Couldn't fetch critical apps {0}", (Throwable)e);
        }
        return appList;
    }
    
    static {
        platforms = new int[] { 1, 2, 3 };
    }
}
