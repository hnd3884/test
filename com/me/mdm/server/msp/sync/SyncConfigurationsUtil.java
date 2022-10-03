package com.me.mdm.server.msp.sync;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Level;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class SyncConfigurationsUtil
{
    private static Logger logger;
    
    public static boolean checkIfProfileIsForAllCustomers(final Long profileId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Row row = dataObject.getFirstRow("Profile");
        final Integer profileSharedScope = (Integer)row.get("PROFILE_SHARED_SCOPE");
        final Boolean isForAllCustomers = profileSharedScope == 1;
        return isForAllCustomers;
    }
    
    public static boolean checkIfAppIsForAllCustomers(final Long appId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appId, 0));
        selectQuery.addSelectColumn(Column.getColumn("MdPackage", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Row row = dataObject.getFirstRow("MdPackage");
        final Integer appSharedScope = (Integer)row.get("APP_SHARED_SCOPE");
        final Boolean isForAllCustomers = appSharedScope == 1;
        return isForAllCustomers;
    }
    
    static List getApplicableCustomers(final Long customerId) throws DataAccessException {
        final List<Long> customerList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        selectQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 1));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Iterator<Row> iterator = dataObject.getRows("CustomerInfo");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long customerID = (Long)row.get("CUSTOMER_ID");
            customerList.add(customerID);
        }
        return customerList;
    }
    
    static Long getProfileIdFromProfileIdentifier(final String profileIdentifier, final Long customerId) throws DataAccessException {
        Long profileId = -1L;
        final Criteria profileIdentifierCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_PAYLOAD_IDENTIFIER"), (Object)profileIdentifier, 0, (boolean)Boolean.FALSE);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.setCriteria(profileIdentifierCriteria.and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("Profile");
            profileId = (Long)row.get("PROFILE_ID");
        }
        return profileId;
    }
    
    static DataObject getProfileDO(final String profileIdentifier, final Long customerId) throws DataAccessException {
        final Criteria profileIdentifierCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_PAYLOAD_IDENTIFIER"), (Object)profileIdentifier, 0, (boolean)Boolean.FALSE);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        selectQuery.addJoin(new Join("RecentPubProfileToColln", "IOSCollectionPayload", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("Profile", "ProfileToManagement", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        selectQuery.addJoin(new Join("ProfileToManagement", "ManagementModel", new String[] { "MANAGEMENT_ID" }, new String[] { "MANAGEMENT_ID" }, 1));
        selectQuery.setCriteria(profileIdentifierCriteria.and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "*"));
        selectQuery.addSelectColumn(Column.getColumn("IOSCollectionPayload", "*"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToManagement", "*"));
        selectQuery.addSelectColumn(Column.getColumn("RecentPubProfileToColln", "*"));
        selectQuery.addSelectColumn(Column.getColumn("ManagementModel", "*"));
        return DataAccess.get(selectQuery);
    }
    
    static JSONObject extractRequiredDetailsFromProfileDOToClone(final DataObject profileDO) throws DataAccessException {
        final JSONObject cloneProfileDetails = new JSONObject();
        final Row profileRow = profileDO.getFirstRow("Profile");
        final Row iOSCollectionPayloadRow = profileDO.getFirstRow("IOSCollectionPayload");
        final Row managementModelRow = profileDO.getFirstRow("ManagementModel");
        cloneProfileDetails.put("PROFILE_NAME", profileRow.get("PROFILE_NAME"));
        cloneProfileDetails.put("PROFILE_TYPE", profileRow.get("PROFILE_TYPE"));
        cloneProfileDetails.put("PROFILE_DESCRIPTION", profileRow.get("PROFILE_DESCRIPTION"));
        cloneProfileDetails.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
        cloneProfileDetails.put("SCOPE", profileRow.get("SCOPE"));
        cloneProfileDetails.put("LAST_MODIFIED_BY", profileRow.get("LAST_MODIFIED_BY"));
        cloneProfileDetails.put("SECURITY_TYPE", iOSCollectionPayloadRow.get("SECURITY_TYPE"));
        cloneProfileDetails.put("PROFILE_PAYLOAD_IDENTIFIER", profileRow.get("PROFILE_PAYLOAD_IDENTIFIER"));
        cloneProfileDetails.put("management_type", managementModelRow.get("MANAGEMENT_IDENTIFIER"));
        return cloneProfileDetails;
    }
    
    public static String getProfilePayloadIdentifier(final Long profileId) throws Exception {
        return (String)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)profileId, "PROFILE_PAYLOAD_IDENTIFIER");
    }
    
    public static void updateIsMoveToAllApplicable(final Long profileId) throws DataAccessException {
        Boolean isMoveToAllCustomersApplicable = Boolean.FALSE;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(new Column("Profile", "*"));
        selectQuery.addSelectColumn(new Column("ConfigData", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Row profileRow = dataObject.getFirstRow("Profile");
        final int platformType = (int)profileRow.get("PLATFORM_TYPE");
        final int profileType = (int)profileRow.get("PROFILE_TYPE");
        final Integer profileSharedScope = (Integer)profileRow.get("PROFILE_SHARED_SCOPE");
        final Boolean isForAllCustomers = profileSharedScope == 1;
        if ((platformType == 2 || platformType == 1) && profileType == 1 && !isForAllCustomers && CustomerInfoUtil.getInstance().isMSP()) {
            final Iterator<Row> iterator = dataObject.getRows("ConfigData");
            Boolean isRestrictedPayloadFound = Boolean.FALSE;
            while (iterator.hasNext()) {
                final Row configRow = iterator.next();
                final Integer configId = (Integer)configRow.get("CONFIG_ID");
                if (ConfigurationSyncEngineConstants.restrictedPayloads.contains(configId)) {
                    isRestrictedPayloadFound = Boolean.TRUE;
                }
            }
            isMoveToAllCustomersApplicable = (((boolean)isRestrictedPayloadFound) ? Boolean.FALSE : Boolean.TRUE);
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
        updateQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        updateQuery.setUpdateColumn("IS_MOVE_TO_ALL_APPLICABLE", (Object)isMoveToAllCustomersApplicable);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    public static void addApprovedAppVersion(final Long packageId, final Long childCustomerId) throws DataAccessException {
        SyncConfigurationsUtil.logger.log(Level.INFO, "AddApprovedAppVersion method invoked with props: {0} {1}", new Object[] { packageId, childCustomerId });
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        selectQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("MdPackage", "AaaLogin", new String[] { "PACKAGE_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria appVersionLabelCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(appCriteria.and(appVersionLabelCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final JSONObject requestJSON = new JSONObject();
        final Row appDetailsRow = dataObject.getFirstRow("MdAppDetails");
        final Row packagePolicyRow = dataObject.containsTable("MdPackagePolicy") ? dataObject.getFirstRow("MdPackagePolicy") : null;
        final Row packageToAppDataRow = dataObject.getFirstRow("MdPackageToAppData");
        final Row categoryRow = dataObject.containsTable("MdAppGroupCategoryRel") ? dataObject.getFirstRow("MdAppGroupCategoryRel") : null;
        final Row releaseLabelRow = dataObject.getFirstRow("AppReleaseLabel");
        final Row aaaLoginRow = dataObject.getFirstRow("AaaLogin");
        requestJSON.put("platform_type", appDetailsRow.get("PLATFORM_TYPE"));
        requestJSON.put("app_name", appDetailsRow.get("APP_NAME"));
        requestJSON.put("app_type", 2);
        final String appFileLoc = (String)packageToAppDataRow.get("APP_FILE_LOC");
        if (!MDMStringUtils.isEmpty(appFileLoc) && !appFileLoc.equalsIgnoreCase("Not Available")) {
            requestJSON.put("app_file", (Object)appFileLoc);
        }
        final String displayImageLoc = (String)packageToAppDataRow.get("DISPLAY_IMAGE_LOC");
        if (!MDMStringUtils.isEmpty(displayImageLoc) && !displayImageLoc.equalsIgnoreCase("Not Available")) {
            requestJSON.put("display_image", (Object)displayImageLoc);
        }
        final String fullImageLoc = (String)packageToAppDataRow.get("FULL_IMAGE_LOC");
        if (!MDMStringUtils.isEmpty(fullImageLoc) && !fullImageLoc.equalsIgnoreCase("Not Available")) {
            requestJSON.put("full_image", (Object)fullImageLoc);
        }
        if (categoryRow != null) {
            requestJSON.put("app_category_id", (Object)categoryRow.get("APP_CATEGORY_ID"));
        }
        if (packagePolicyRow != null) {
            requestJSON.put("remove_app_with_profile", packagePolicyRow.get("REMOVE_APP_WITH_PROFILE"));
            requestJSON.put("prevent_backup", packagePolicyRow.get("PREVENT_BACKUP"));
        }
        requestJSON.put("supported_devices", packageToAppDataRow.get("SUPPORTED_DEVICES"));
        requestJSON.put("description", packageToAppDataRow.get("DESCRIPTION"));
        requestJSON.put("label_id", (Object)releaseLabelRow.get("RELEASE_LABEL_ID"));
        requestJSON.put("customerID", appDetailsRow.get("CUSTOMER_ID"));
        requestJSON.put("userID", aaaLoginRow.get("USER_ID"));
        requestJSON.put("LOGIN_ID", aaaLoginRow.get("LOGIN_ID"));
        requestJSON.put("userName", aaaLoginRow.get("NAME"));
        requestJSON.put("childCustomerId", (Object)childCustomerId);
        requestJSON.put("app_id", packageToAppDataRow.get("PACKAGE_ID"));
        requestJSON.put("is_for_all_customers", true);
        requestJSON.put("app_unique_identifier", (Object)(appDetailsRow.get("IDENTIFIER") + "@@@" + appDetailsRow.get("APP_VERSION") + "@@@" + appDetailsRow.get("APP_NAME_SHORT_VERSION")));
        final JSONObject customerJSON = new JSONObject();
        customerJSON.put("customer_id", (Object)childCustomerId);
        final JSONObject filterJSON = new JSONObject();
        filterJSON.put("filters", (Object)customerJSON);
        requestJSON.put("msg_header", (Object)filterJSON);
        SyncConfigurationListeners.invokeListeners(requestJSON, 201);
    }
    
    public static void moveAllCustomerAppToTrash(final Long packageId, final Long customerId) throws Exception {
        SyncConfigurationsUtil.logger.log(Level.INFO, "Trashed all customer app created on new customer creation listener {0} {1}", new Object[] { packageId, customerId });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "AaaLogin", new String[] { "PACKAGE_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Row mdPackageRow = dataObject.getFirstRow("MdPackage");
        final Row aaaLoginRow = dataObject.getFirstRow("AaaLogin");
        final JSONObject syncJSON = new JSONObject();
        syncJSON.put("app_ids", (Object)new Long[] { packageId });
        syncJSON.put("softdelete", false);
        syncJSON.put("CUSTOMER_ID", (Object)customerId);
        syncJSON.put("childCustomerId", (Object)customerId);
        syncJSON.put("PROFILE_TYPE", 2);
        syncJSON.put("LAST_MODIFIED_BY", mdPackageRow.get("PACKAGE_MODIFIED_BY"));
        syncJSON.put("LOGIN_ID", aaaLoginRow.get("LOGIN_ID"));
        SyncConfigurationListeners.invokeListeners(syncJSON, 209);
    }
    
    public static void addNonApprovedAppVersions(final Long packageId, final Long releaseLabelId, final Long childCustomerId) throws Exception {
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        selectQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("MdPackage", "AaaLogin", new String[] { "PACKAGE_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria appVersionLabelCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(appCriteria.and(appVersionLabelCriteria));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        if (dmDataSetWrapper.next()) {
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("platform_type", dmDataSetWrapper.getValue("PLATFORM_TYPE"));
            requestJSON.put("app_name", dmDataSetWrapper.getValue("APP_NAME"));
            requestJSON.put("app_type", 2);
            final String appFileLoc = (String)dmDataSetWrapper.getValue("APP_FILE_LOC");
            if (!MDMStringUtils.isEmpty(appFileLoc) && !appFileLoc.equalsIgnoreCase("Not Available")) {
                requestJSON.put("app_file", (Object)appFileLoc);
            }
            final String displayImageLoc = (String)dmDataSetWrapper.getValue("DISPLAY_IMAGE_LOC");
            if (!MDMStringUtils.isEmpty(displayImageLoc) && !displayImageLoc.equalsIgnoreCase("Not Available")) {
                requestJSON.put("display_image", (Object)displayImageLoc);
            }
            final String fullImageLoc = (String)dmDataSetWrapper.getValue("FULL_IMAGE_LOC");
            if (!MDMStringUtils.isEmpty(fullImageLoc) && !fullImageLoc.equalsIgnoreCase("Not Available")) {
                requestJSON.put("full_image", (Object)fullImageLoc);
            }
            if (dmDataSetWrapper.getValue("APP_CATEGORY_ID") != null) {
                requestJSON.put("app_category_id", (Object)dmDataSetWrapper.getValue("APP_CATEGORY_ID"));
            }
            requestJSON.put("remove_app_with_profile", dmDataSetWrapper.getValue("REMOVE_APP_WITH_PROFILE"));
            requestJSON.put("prevent_backup", dmDataSetWrapper.getValue("PREVENT_BACKUP"));
            requestJSON.put("supported_devices", dmDataSetWrapper.getValue("SUPPORTED_DEVICES"));
            requestJSON.put("description", dmDataSetWrapper.getValue("DESCRIPTION"));
            requestJSON.put("label_id", (Object)dmDataSetWrapper.getValue("RELEASE_LABEL_ID"));
            requestJSON.put("customerID", dmDataSetWrapper.getValue("CUSTOMER_ID"));
            requestJSON.put("userID", dmDataSetWrapper.getValue("USER_ID"));
            requestJSON.put("LOGIN_ID", dmDataSetWrapper.getValue("LOGIN_ID"));
            requestJSON.put("userName", dmDataSetWrapper.getValue("NAME"));
            requestJSON.put("childCustomerId", (Object)childCustomerId);
            requestJSON.put("force_update_in_label", false);
            requestJSON.put("app_id", dmDataSetWrapper.getValue("PACKAGE_ID"));
            requestJSON.put("app_unique_identifier", (Object)(dmDataSetWrapper.getValue("IDENTIFIER") + "@@@" + dmDataSetWrapper.getValue("APP_VERSION") + "@@@" + dmDataSetWrapper.getValue("APP_NAME_SHORT_VERSION")));
            final JSONObject customerJSON = new JSONObject();
            customerJSON.put("customer_id", (Object)childCustomerId);
            final JSONObject resourcesJSON = new JSONObject();
            resourcesJSON.put("app_id", dmDataSetWrapper.getValue("PACKAGE_ID"));
            resourcesJSON.put("label_id", dmDataSetWrapper.getValue("RELEASE_LABEL_ID"));
            final JSONObject headerJSON = new JSONObject();
            headerJSON.put("filters", (Object)customerJSON);
            headerJSON.put("resource_identifier", (Object)resourcesJSON);
            requestJSON.put("msg_header", (Object)headerJSON);
            SyncConfigurationListeners.invokeListeners(requestJSON, 202);
        }
    }
    
    public static String getAppUniqueIdentifier(final Long appId, final Long labelId) throws DataAccessException {
        String appUniqueIdentifier = "--";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appId, 0);
        final Criteria labelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)labelId, 0);
        selectQuery.setCriteria(appCriteria.and(labelCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row mdAppRow = dataObject.getFirstRow("MdAppDetails");
            final String appVersion = (String)mdAppRow.get("APP_VERSION");
            final String appVersionCode = (String)mdAppRow.get("APP_NAME_SHORT_VERSION");
            final String identifier = (String)mdAppRow.get("IDENTIFIER");
            appUniqueIdentifier = identifier + "@@@" + appVersion + "@@@" + appVersionCode;
        }
        return appUniqueIdentifier;
    }
    
    public static void validateAppScope(final String identifier, final int platform, final Boolean isForAllCustomers) throws Exception {
        if (platform != 2 && platform != 1) {
            return;
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        selectQuery.setCriteria(appGroupCriteria.and(platformCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        while (dmDataSetWrapper.next()) {
            if (isForAllCustomers) {
                final int packageType = (int)dmDataSetWrapper.getValue("PACKAGE_TYPE");
                if (packageType != 2) {
                    SyncConfigurationsUtil.logger.log(Level.SEVERE, "App already present in customer {0} type {1}", new Object[] { dmDataSetWrapper.getValue("CUSTOMER_ID"), packageType });
                    throw new APIHTTPException("APP0033", new Object[0]);
                }
            }
            final Long customerIdFromDB = (Long)dmDataSetWrapper.getValue("CUSTOMER_ID");
            final int appSharedScope = (int)dmDataSetWrapper.getValue("APP_SHARED_SCOPE");
            final Boolean isForAllCustomerFromDB = (appSharedScope == 1) ? Boolean.TRUE : Boolean.FALSE;
            if (!isForAllCustomers.equals(isForAllCustomerFromDB)) {
                SyncConfigurationsUtil.logger.log(Level.SEVERE, "Unable to add app in scope {0} for customer {1} as app already present in different scope in customer {2}", new Object[] { isForAllCustomers, customerId, customerIdFromDB });
                if (!isForAllCustomers) {
                    final String errorArgs = (platform == 1) ? "IPA" : "APK";
                    throw new APIHTTPException("APP0031", new Object[] { errorArgs });
                }
                final String customerName = dmDataSetWrapper.getValue("CUSTOMER_NAME") + "'s";
                throw new APIHTTPException("APP0032", new Object[] { customerName });
            }
        }
    }
    
    static {
        SyncConfigurationsUtil.logger = Logger.getLogger("MDMConfigLogger");
    }
}
