package com.me.mdm.server.apps.multiversion;

import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import org.json.JSONObject;

public class AndroidAppVersionHandler extends BaseAppVersionHandler
{
    @Override
    @Deprecated
    public void checkIfChannelAllowedToBeMerged(final JSONObject mergeRequestJSON) throws Exception {
        AppVersionDBUtil.getInstance().checkIfChannelAllowedToBeMerged(mergeRequestJSON);
    }
    
    @Override
    public Map<String, List<Long>> removeDevicesContainingLatestVersionOfApp(final List<Long> deviceList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isStoreApp = AppsUtil.getInstance().isNonAccountStoreApp(collectionId);
        if (isStoreApp) {
            final List<Long> clonedDeviceList = new ArrayList<Long>(deviceList);
            final List<Long> removedListOfResources = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedDeviceList", clonedDeviceList);
            retMap.put("removedDeviceList", removedListOfResources);
            return retMap;
        }
        return super.removeDevicesContainingLatestVersionOfApp(deviceList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Map<String, List<Long>> removeGroupsContainingLatestVersionOfApp(final List<Long> groupList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isStoreApp = AppsUtil.getInstance().isNonAccountStoreApp(collectionId);
        if (isStoreApp) {
            final List<Long> clonedGroupList = new ArrayList<Long>(groupList);
            final List<Long> removedListOfGroups = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedGroupList", clonedGroupList);
            retMap.put("removedGroupList", removedListOfGroups);
            return retMap;
        }
        return super.removeGroupsContainingLatestVersionOfApp(groupList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Map<String, List<Long>> removeUsersContainingLatestVersionOfApp(final List<Long> userList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isStoreApp = AppsUtil.getInstance().isNonAccountStoreApp(collectionId);
        if (isStoreApp) {
            final List<Long> clonedUserList = new ArrayList<Long>(userList);
            final List<Long> removedListOfResources = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedUserList", clonedUserList);
            retMap.put("removedUserList", removedListOfResources);
            return retMap;
        }
        return super.removeUsersContainingLatestVersionOfApp(userList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Criteria getDistributedDeviceListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        final Boolean isNonAccountStoreApp = AppsUtil.getInstance().isNonAccountStoreApp(collectionID);
        if (isNonAccountStoreApp) {
            Criteria distributedDeviceListCriteria = null;
            final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
            distributedDeviceListCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)appID, 0);
            final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
            final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
            final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 1));
            return distributedDeviceListCriteria;
        }
        return super.getDistributedDeviceListForAppCriteria(collectionID, profileID);
    }
    
    @Override
    public Criteria getDistributedGroupListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        final Boolean isNonAccountStoreApp = AppsUtil.getInstance().isNonAccountStoreApp(collectionID);
        if (isNonAccountStoreApp) {
            Criteria distributedGroupListCriteria = null;
            final Criteria collectionIDCriteria = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionID, 0);
            final Criteria distributionStatusCriteria = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_STATUS"), (Object)MDMUtil.getInstance().getCollectionStatusToBeIgnoredForGroupReDistribution().toArray(), 8);
            final Criteria alreadyDistributedCriteria = collectionIDCriteria.and(distributionStatusCriteria);
            final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
            final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
            final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 1));
            distributedGroupListCriteria = alreadyDistributedCriteria;
            return distributedGroupListCriteria;
        }
        return super.getDistributedGroupListForAppCriteria(collectionID, profileID);
    }
    
    @Override
    protected Criteria getBundleIdCriteria(final String identifier) {
        return new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)Boolean.TRUE);
    }
    
    @Override
    protected Criteria getVersionCriteria(final String version, final String versionCode) {
        final Criteria versionCodeCriteria = new Criteria(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)versionCode, 0);
        final Criteria versionCriteria = new Criteria(new Column("MdAppDetails", "APP_VERSION"), (Object)version, 0);
        return versionCodeCriteria.and(versionCriteria);
    }
    
    @Override
    protected void throwSameVersionPresentException(final JSONObject customParamJSON, final String version, final String versionCode) throws APIHTTPException, Exception {
        if (!MDMStringUtils.isEmpty(versionCode)) {
            final String errorCode = "APP0017";
            final String errorObject = I18N.getMsg("mdm.api.android_app_version_exists", new Object[] { versionCode });
            throw new APIHTTPException(customParamJSON, errorCode, new Object[] { customParamJSON.get("app_name"), errorObject });
        }
        super.throwSameVersionPresentException(customParamJSON, version, versionCode);
    }
    
    @Override
    protected void throwLowerAppVersionCannotBeUploadedInLabelException(final JSONObject customParamJSON, final JSONObject uploadedVersion, final JSONObject conflictAppVersion) throws Exception {
        final String uploadedAppVersionCode = uploadedVersion.getString("APP_NAME_SHORT_VERSION");
        final String conflictingAppVersionCode = conflictAppVersion.getString("APP_NAME_SHORT_VERSION");
        if (!MDMStringUtils.isEmpty(uploadedAppVersionCode) && !MDMStringUtils.isEmpty(conflictingAppVersionCode) && !uploadedAppVersionCode.equals(conflictingAppVersionCode)) {
            final String errorCode = "APP0014";
            final String errorObject = I18N.getMsg("mdm.api.error.app_version_not_allowed_android", new Object[] { uploadedVersion.get("APP_NAME_SHORT_VERSION"), conflictAppVersion.get("APP_NAME_SHORT_VERSION") });
            throw new APIHTTPException(customParamJSON, errorCode, new Object[] { errorObject });
        }
        super.throwLowerAppVersionCannotBeUploadedInLabelException(customParamJSON, uploadedVersion, conflictAppVersion);
    }
    
    @Override
    protected Criteria getPlatformCriteria() {
        return new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
    }
    
    @Override
    public void handleDBChangesForAppApproval(final JSONObject requestJSON) throws Exception {
        final Long collectionID = requestJSON.getLong("COLLECTION_ID");
        final Boolean isNonAccountStoreApp = AppsUtil.getInstance().isNonAccountStoreApp(collectionID);
        if (isNonAccountStoreApp) {
            throw new UnsupportedOperationException();
        }
        super.handleDBChangesForAppApproval(requestJSON);
    }
    
    @Override
    public JSONObject getLowerVersionAppsThanGiveApp(final JSONObject requestJSON) throws Exception {
        final Long packageId = APIUtil.getResourceID(requestJSON, "app_id");
        final Long labelId = APIUtil.getResourceID(requestJSON, "label_id");
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, labelId);
        final Boolean isPurchasedFromPortal = appDetailsMap.get("IS_PURCHASED_FROM_PORTAL");
        final int packageType = appDetailsMap.get("PACKAGE_TYPE");
        if (!isPurchasedFromPortal && packageType != 2) {
            throw new UnsupportedOperationException();
        }
        return super.getLowerVersionAppsThanGiveApp(requestJSON);
    }
    
    @Override
    public JSONObject getHigherVersionAppsThanGivenApps(final JSONObject requestJSON) throws Exception {
        final Long packageId = APIUtil.getResourceID(requestJSON, "app_id");
        final Long labelId = APIUtil.getResourceID(requestJSON, "label_id");
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, labelId);
        final Boolean isPurchasedFromPortal = appDetailsMap.get("IS_PURCHASED_FROM_PORTAL");
        final int packageType = appDetailsMap.get("PACKAGE_TYPE");
        if (!isPurchasedFromPortal && packageType != 2) {
            throw new UnsupportedOperationException();
        }
        return super.getHigherVersionAppsThanGivenApps(requestJSON);
    }
    
    @Override
    protected Boolean checkIfAppVersionAreSame(final JSONObject uploadedAppVersion, final JSONObject validationAppVersion) {
        final String uploadedAppVersionCode = uploadedAppVersion.getString("APP_NAME_SHORT_VERSION");
        final String validationAppVersionCode = validationAppVersion.getString("APP_NAME_SHORT_VERSION");
        final String uploadedAppVersionName = uploadedAppVersion.getString("APP_VERSION");
        final String validationAppVersionName = validationAppVersion.getString("APP_VERSION");
        return uploadedAppVersionCode.equals(validationAppVersionCode) && uploadedAppVersionName.equals(validationAppVersionName);
    }
}
