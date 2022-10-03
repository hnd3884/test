package com.me.mdm.server.apps.multiversion;

import com.adventnet.ds.query.UpdateQuery;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.apps.AppVersionChecker;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseAppVersionHandler implements AppVersionHandlerInterface
{
    protected static Logger logger;
    
    @Override
    public Map<String, List<Long>> removeDevicesContainingLatestVersionOfApp(final List<Long> deviceList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws DataAccessException, Exception {
        final List<Long> clonedDeviceList = new ArrayList<Long>(deviceList);
        final List<Long> removedListOfResources = new ArrayList<Long>();
        final List<Long> collectionIDsOfAppWithHigherVersionApp = AppsUtil.getInstance().getListOfAppCollnsVersionHigherThanGivenColln(appProfileId, collectionId, Boolean.FALSE);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), 0);
        final Criteria recentProfileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"), 0);
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", resourceCriteria.and(recentProfileCriteria), 2));
        final Criteria deviceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)clonedDeviceList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)appProfileId, 0);
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionIDsOfAppWithHigherVersionApp.toArray(), 8);
        final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(deviceCriteria.and(profileCriteria).and(collectionIDCriteria).and(markedForDeleteCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        if (!dao.isEmpty()) {
            final Iterator<Row> rows = dao.getRows("MdAppCatalogToResource");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                final Boolean isResRemoved = clonedDeviceList.remove(resourceId);
                if (isResRemoved) {
                    removedListOfResources.add(resourceId);
                }
            }
        }
        final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
        retMap.put("modifiedDeviceList", clonedDeviceList);
        retMap.put("removedDeviceList", removedListOfResources);
        return retMap;
    }
    
    @Override
    public Map<String, List<Long>> removeGroupsContainingLatestVersionOfApp(final List<Long> groupList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws DataAccessException, Exception {
        final List<Long> clonedGroupList = new ArrayList<Long>(groupList);
        final List<Long> removedListOfGroups = new ArrayList<Long>();
        final List<Long> collectionIDsOfAppWithHigherVersionApp = AppsUtil.getInstance().getListOfAppCollnsVersionHigherThanGivenColln(appProfileId, collectionId, Boolean.FALSE);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToGroup"));
        selectQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "GROUP_ID"), 0);
        final Criteria recentProfileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), 0);
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", resourceCriteria.and(recentProfileCriteria), 2));
        final Criteria groupCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)clonedGroupList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)appProfileId, 0);
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionIDsOfAppWithHigherVersionApp.toArray(), 8);
        final Criteria notMarkedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        selectQuery.setCriteria(groupCriteria.and(profileCriteria).and(collectionIDCriteria).and(notMarkedForDeleteCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        if (!dao.isEmpty()) {
            final Iterator<Row> rows = dao.getRows("MdAppCatalogToGroup");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long groupId = (Long)row.get("RESOURCE_ID");
                final Boolean isGrpRemoved = clonedGroupList.remove(groupId);
                if (isGrpRemoved) {
                    removedListOfGroups.add(groupId);
                }
            }
        }
        final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
        retMap.put("modifiedGroupList", clonedGroupList);
        retMap.put("removedGroupList", removedListOfGroups);
        return retMap;
    }
    
    @Override
    public Map<String, List<Long>> removeUsersContainingLatestVersionOfApp(final List<Long> userList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws DataAccessException, Exception {
        final List<Long> clonedUserList = new ArrayList<Long>(userList);
        final List<Long> removedListOfResources = new ArrayList<Long>();
        final List<Long> collectionIDsOfAppWithHigherVersionApp = AppsUtil.getInstance().getListOfAppCollnsVersionHigherThanGivenColln(appProfileId, collectionId, Boolean.FALSE);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToUser"));
        selectQuery.addJoin(new Join("MdAppCatalogToUser", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), 0);
        final Criteria recentProfileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"), 0);
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForMDMResource", resourceCriteria.and(recentProfileCriteria), 2));
        final Criteria userCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)clonedUserList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)appProfileId, 0);
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionIDsOfAppWithHigherVersionApp.toArray(), 8);
        final Criteria notMarkedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        selectQuery.setCriteria(userCriteria.and(profileCriteria).and(collectionIDCriteria).and(notMarkedForDeleteCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        if (!dao.isEmpty()) {
            final Iterator<Row> rows = dao.getRows("MdAppCatalogToUser");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                final Boolean isResRemoved = clonedUserList.remove(resourceId);
                if (isResRemoved) {
                    removedListOfResources.add(resourceId);
                }
            }
        }
        final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
        retMap.put("modifiedUserList", clonedUserList);
        retMap.put("removedUserList", removedListOfResources);
        return retMap;
    }
    
    @Deprecated
    @Override
    public JSONObject getPossibleChannelsToMergeApp(final JSONObject requestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long packageID = APIUtil.getResourceID(requestJSON, "app_id");
        final Long releaseLabelID = APIUtil.getResourceID(requestJSON, "label_id");
        final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        final List<Long> listOfAppIDsWithVersionHigher = AppsUtil.getInstance().getListOfAppIDsWithVersionHigher(packageID, releaseLabelID, Boolean.FALSE);
        final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria packageIDCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 1);
        final Criteria higherVersionAppCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)listOfAppIDsWithVersionHigher.toArray(), 9);
        appAllLiveVersionQuery.setCriteria(packageIDCriteria.and(releaseLabelCriteria).and(higherVersionAppCriteria));
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"), (Object)search, 12);
            appAllLiveVersionQuery.setCriteria(appAllLiveVersionQuery.getCriteria().and(searchCriteria));
        }
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "IDENTIFIER"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final int count = MDMDBUtil.getCount(appAllLiveVersionQuery, "AppReleaseLabel", "RELEASE_LABEL_ID");
        responseJSON.put("paging", (Object)pagingUtil.getPagingJSON(count));
        if (count != 0) {
            if (!selectAll) {
                appAllLiveVersionQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("channel_name")) {
                        appAllLiveVersionQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    appAllLiveVersionQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE", true));
                }
            }
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appAllLiveVersionQuery);
            while (ds.next()) {
                final JSONObject lowerVersionChannels = new JSONObject();
                lowerVersionChannels.put("bundle_identifier", ds.getValue("IDENTIFIER"));
                lowerVersionChannels.put("app_name", ds.getValue("APP_NAME"));
                lowerVersionChannels.put("app_version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                lowerVersionChannels.put("version", ds.getValue("APP_VERSION"));
                if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                    final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                    if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                        if (!displayImageLoc.startsWith("http")) {
                            lowerVersionChannels.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                        }
                        else {
                            lowerVersionChannels.put("icon", (Object)displayImageLoc);
                        }
                    }
                }
                lowerVersionChannels.put("release_label_id", ds.getValue("RELEASE_LABEL_ID"));
                lowerVersionChannels.put("release_label_name", (Object)I18N.getMsg((String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                jsonArray.put((Object)lowerVersionChannels);
            }
        }
        responseJSON.put("possible_channels_merge", (Object)jsonArray);
        return responseJSON;
    }
    
    @Override
    @Deprecated
    public void checkIfChannelAllowedToBeMerged(final JSONObject mergeRequestJSON) throws Exception {
    }
    
    @Override
    public Criteria getDistributedDeviceListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        Criteria distributedDeviceListCriteria = null;
        final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
        final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
        final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
        final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 1));
        final Criteria alreadyInstalledVersionEqualCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)appID, 0);
        final Criteria updateAvailableCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)Boolean.FALSE, 0);
        final Criteria appUpDoDateInDeviceCriteria = alreadyInstalledVersionEqualCriteria.and(updateAvailableCriteria);
        final List listOfCollnIDsWithVersionHigherThanTheGivenCollectionID = AppsUtil.getInstance().getListOfAppCollnsVersionHigherThanGivenColln(profileID, collectionID, Boolean.FALSE);
        final Criteria alreadyPublishedHigherVersionCriteria = new Criteria(Column.getColumn("ApprovedAppToColln", "COLLECTION_ID"), (Object)listOfCollnIDsWithVersionHigherThanTheGivenCollectionID.toArray(), 8);
        distributedDeviceListCriteria = alreadyPublishedHigherVersionCriteria.or(appUpDoDateInDeviceCriteria);
        return distributedDeviceListCriteria;
    }
    
    @Override
    public Criteria getDistributedGroupListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        Criteria distributedGroupListCriteria = null;
        final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
        final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
        final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 1));
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria distributionCriteria = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_STATUS"), (Object)MDMUtil.getInstance().getCollectionStatusToBeIgnoredForGroupReDistribution().toArray(), 8);
        final Criteria appSuccessfullyInstalledOrInProgressCriteria = collectionIDCriteria.and(distributionCriteria);
        final Criteria updateAvailableCriteria = new Criteria(new Column("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)Boolean.FALSE, 0);
        final Criteria appUpToDateInGroupCriteria = appSuccessfullyInstalledOrInProgressCriteria.and(updateAvailableCriteria);
        final List listOfCollnIDsWithVersionHigherThanTheGivenCollectionID = AppsUtil.getInstance().getListOfAppCollnsVersionHigherThanGivenColln(profileID, collectionID, Boolean.FALSE);
        final Criteria alreadyAssignedHigherVersionCriteria = new Criteria(Column.getColumn("ApprovedAppToColln", "COLLECTION_ID"), (Object)listOfCollnIDsWithVersionHigherThanTheGivenCollectionID.toArray(), 8);
        distributedGroupListCriteria = alreadyAssignedHigherVersionCriteria.or(appUpToDateInGroupCriteria);
        return distributedGroupListCriteria;
    }
    
    @Override
    public void validateAppVersionForUploadWithReleaseLabel(final JSONObject uploadAppDetails) throws Exception {
        final Integer platformType = uploadAppDetails.getInt("PLATFORM_TYPE");
        final Long customerID = uploadAppDetails.getLong("CUSTOMER_ID");
        final String identifier = uploadAppDetails.getString("packagename");
        final String versionName = uploadAppDetails.getString("APP_VERSION");
        final String versionCode = uploadAppDetails.getString("APP_NAME_SHORT_VERSION");
        final String appName = uploadAppDetails.getString("app_name");
        final Long appId = uploadAppDetails.getLong("app_id");
        final Long releaseLabelId = uploadAppDetails.getLong("RELEASE_LABEL_ID");
        final Boolean forceUpdateAsBeta = uploadAppDetails.optBoolean("force_update_in_label");
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria customerCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria platformCriteria = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appId, 0);
        final Criteria versionCriteria = this.getVersionCriteria(versionName, versionCode);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        final Criteria finalCriteria = customerCriteria.and(platformCriteria).and(packageCriteria).and(versionCriteria.or(releaseLabelCriteria));
        selectQuery.addSelectColumn(new Column("Profile", "*"));
        selectQuery.addSelectColumn(new Column("AppReleaseLabel", "*"));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "*"));
        selectQuery.setCriteria(finalCriteria);
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final Boolean isAppInTrash = (Boolean)ds.getValue("IS_MOVED_TO_TRASH");
            final JSONObject customParamsJson = new JSONObject().put("app_name", (Object)String.valueOf(appName)).put("version", (Object)versionName).put("bundle_identifier", (Object)String.valueOf(identifier)).put("is_app_in_trash", (Object)isAppInTrash).put("versioncode", (Object)versionCode);
            if (isAppInTrash) {
                customParamsJson.put("existing_app_name", (Object)String.valueOf(appName));
                throw new APIHTTPException(customParamsJson, "APP0019", new Object[0]);
            }
            final String appVersionName = (String)ds.getValue("APP_VERSION");
            final String appVersionCode = (String)ds.getValue("APP_NAME_SHORT_VERSION");
            final Long appReleaseLabel = (Long)ds.getValue("RELEASE_LABEL_ID");
            final JSONObject validationAppVersionJSON = new JSONObject().put("APP_VERSION", (Object)appVersionName).put("APP_NAME_SHORT_VERSION", (Object)appVersionCode);
            final Boolean isSameAppVersionFound = this.checkIfAppVersionAreSame(uploadAppDetails, validationAppVersionJSON);
            if (isSameAppVersionFound) {
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameAppVersionUpload")) {
                    if (forceUpdateAsBeta == Boolean.FALSE) {
                        BaseAppVersionHandler.logger.log(Level.SEVERE, "-- [SAME APP VERSION FOUND IN DIFFERENT LABEL CANNOT ADD AS SEPARATE]-- {0}--{1}--{2}", new Object[] { identifier, versionName, versionCode });
                        this.throwSameVersionPresentException(customParamsJson, versionName, versionCode);
                    }
                    else {
                        if (appReleaseLabel.equals(releaseLabelId)) {
                            continue;
                        }
                        BaseAppVersionHandler.logger.log(Level.SEVERE, "-- [SAME APP VERSION FOUND IN DIFFERENT LABEL]-- {0}--{1}--{2}", new Object[] { identifier, versionName, versionCode });
                        this.throwSameVersionPresentException(customParamsJson, versionName, versionCode);
                    }
                }
                else {
                    BaseAppVersionHandler.logger.log(Level.SEVERE, "-- [SAME APP VERSION FOUND]-- {0}--{1}--{2}", new Object[] { identifier, versionName, versionCode });
                    this.throwSameVersionPresentException(customParamsJson, versionName, versionCode);
                }
            }
            else {
                if (!appReleaseLabel.equals(releaseLabelId)) {
                    continue;
                }
                final Boolean isGreaterOrEqual = AppVersionChecker.getInstance(platformType).isAppVersionGreaterOrEqual(uploadAppDetails, validationAppVersionJSON);
                if (isGreaterOrEqual) {
                    continue;
                }
                BaseAppVersionHandler.logger.log(Level.SEVERE, "App cannot be updated in the channel {0}, as it already has an higher version {1} or versionCode {2} than the uploaded one {3} or {4} ", new Object[] { releaseLabelId, appVersionName, versionName, appVersionCode, versionCode });
                this.throwLowerAppVersionCannotBeUploadedInLabelException(customParamsJson, uploadAppDetails, validationAppVersionJSON);
            }
        }
    }
    
    @Override
    public void validateAppVersionForUpload(final JSONObject uploadedAppDetails) throws APIHTTPException, DataAccessException, Exception {
        final Long customerID = uploadedAppDetails.getLong("CUSTOMER_ID");
        final String identifier = uploadedAppDetails.getString("packagename");
        final String versionName = uploadedAppDetails.getString("versionname");
        final String versionCode = uploadedAppDetails.getString("versioncode");
        final String appName = uploadedAppDetails.getString("app_name");
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameAppVersionUpload")) {
            final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
            selectQuery.setCriteria(this.getVersionCriteria(versionName, versionCode).and(customerCriteria).and(this.getBundleIdCriteria(identifier)).and(this.getPlatformCriteria()));
            selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final JSONObject customParamsJson = new JSONObject().put("app_name", (Object)String.valueOf(appName)).put("version", (Object)versionName).put("bundle_identifier", (Object)String.valueOf(identifier)).put("versioncode", (Object)versionCode);
            if (!dataObject.isEmpty()) {
                BaseAppVersionHandler.logger.log(Level.SEVERE, "-- [SAME APP VERSION FOUND]-- {0}--{1}--{2}", new Object[] { identifier, versionName, versionCode });
                this.throwSameVersionPresentException(customParamsJson, versionName, versionCode);
            }
        }
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        selectQuery.setCriteria(customerCriteria.and(this.getBundleIdCriteria(identifier).and(this.getPlatformCriteria())));
        selectQuery.addSelectColumn(new Column("Profile", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("Profile");
            final Boolean isTrash = (Boolean)row.get("IS_MOVED_TO_TRASH");
            if (isTrash) {
                final JSONObject customParamsJson2 = new JSONObject().put("app_name", (Object)String.valueOf(appName)).put("existing_app_name", (Object)String.valueOf(appName)).put("version", (Object)versionName).put("bundle_identifier", (Object)String.valueOf(identifier)).put("is_app_in_trash", (Object)isTrash).put("versioncode", (Object)versionCode);
                throw new APIHTTPException(customParamsJson2, "APP0019", new Object[0]);
            }
        }
    }
    
    protected Criteria getBundleIdCriteria(final String identifier) {
        return new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)Boolean.FALSE);
    }
    
    protected Criteria getVersionCriteria(final String version, final String versionCode) {
        return new Criteria(new Column("MdAppDetails", "APP_VERSION"), (Object)version, 0);
    }
    
    protected void throwSameVersionPresentException(final JSONObject customParamJSON, final String version, final String versionCode) throws APIHTTPException, Exception {
        final String errorCode = "APP0017";
        final String errorObject = I18N.getMsg("mdm.api.non_android_app_version_exits", new Object[] { version });
        throw new APIHTTPException(customParamJSON, errorCode, new Object[] { customParamJSON.get("app_name"), errorObject });
    }
    
    protected void throwLowerAppVersionCannotBeUploadedInLabelException(final JSONObject customParamJSON, final JSONObject uploadedVersion, final JSONObject conflictAppVersion) throws Exception {
        final String errorCode = "APP0014";
        final String errorObject = I18N.getMsg("mdm.api.error.app_version_not_allowed_non_android", new Object[] { uploadedVersion.get("APP_VERSION"), conflictAppVersion.get("APP_VERSION") });
        throw new APIHTTPException(customParamJSON, errorCode, new Object[] { errorObject });
    }
    
    protected abstract Criteria getPlatformCriteria();
    
    @Override
    public Boolean isCurrentPackageNewToAppRepo(final String bundleID, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria bundleIdCriteria = this.getBundleIdCriteria(bundleID);
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.setCriteria(customerCriteria.and(bundleIdCriteria).and(this.getPlatformCriteria()));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return dataObject.isEmpty();
    }
    
    @Override
    public JSONObject getLowerVersionAppsThanGiveApp(final JSONObject requestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long packageID = APIUtil.getResourceID(requestJSON, "app_id");
        final Long releaseLabelID = APIUtil.getResourceID(requestJSON, "label_id");
        final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        final List<Long> listOfAppIDsWithVersionHigher = AppsUtil.getInstance().getListOfAppIDsWithVersionHigher(packageID, releaseLabelID, Boolean.TRUE);
        final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
        appAllLiveVersionQuery.addJoin(new Join("AppGroupToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appAllLiveVersionQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        final Criteria packageIDCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 1);
        final Criteria higherVersionAppCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)listOfAppIDsWithVersionHigher.toArray(), 9);
        appAllLiveVersionQuery.setCriteria(packageIDCriteria.and(releaseLabelCriteria).and(higherVersionAppCriteria));
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"), (Object)search, 12);
            appAllLiveVersionQuery.setCriteria(appAllLiveVersionQuery.getCriteria().and(searchCriteria));
        }
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "IDENTIFIER"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppGroupToCollection", "APP_VERSION_STATUS"));
        appAllLiveVersionQuery.addSortColumn(new SortColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "LABEL_ASSIGNED_TIME"), false));
        final int count = MDMDBUtil.getCount(appAllLiveVersionQuery, "AppReleaseLabel", "RELEASE_LABEL_ID");
        responseJSON.put("paging", (Object)pagingUtil.getPagingJSON(count));
        if (count != 0) {
            if (!selectAll) {
                appAllLiveVersionQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("channel_name")) {
                        appAllLiveVersionQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    appAllLiveVersionQuery.addSortColumn(new SortColumn("MdAppDetails", "APP_VERSION", true));
                }
            }
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appAllLiveVersionQuery);
            while (ds.next()) {
                final JSONObject lowerVersionChannels = new JSONObject();
                lowerVersionChannels.put("bundle_identifier", ds.getValue("IDENTIFIER"));
                lowerVersionChannels.put("app_name", ds.getValue("APP_NAME"));
                lowerVersionChannels.put("version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                lowerVersionChannels.put("is_approved", ds.getValue("APP_VERSION_STATUS") != null);
                lowerVersionChannels.put("APP_VERSION".toLowerCase(), ds.getValue("APP_VERSION"));
                if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                    final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                    if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                        if (!displayImageLoc.startsWith("http")) {
                            lowerVersionChannels.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                        }
                        else {
                            lowerVersionChannels.put("icon", (Object)displayImageLoc);
                        }
                    }
                }
                lowerVersionChannels.put("release_label_id", ds.getValue("RELEASE_LABEL_ID"));
                lowerVersionChannels.put("release_label_name", (Object)I18N.getMsg((String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                jsonArray.put((Object)lowerVersionChannels);
            }
        }
        responseJSON.put("downgrade_available_versions", (Object)jsonArray);
        return responseJSON;
    }
    
    @Override
    public JSONObject getHigherVersionAppsThanGivenApps(final JSONObject requestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long packageID = APIUtil.getResourceID(requestJSON, "app_id");
        final Long releaseLabelID = APIUtil.getResourceID(requestJSON, "label_id");
        final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        final List<Long> listOfAppIDsWithVersionHigher = AppsUtil.getInstance().getListOfAppIdsWithVersionLower(packageID, releaseLabelID, Boolean.FALSE);
        final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
        appAllLiveVersionQuery.addJoin(new Join("AppGroupToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appAllLiveVersionQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        final Criteria packageIDCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 1);
        final Criteria higherVersionAppCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)listOfAppIDsWithVersionHigher.toArray(), 9);
        appAllLiveVersionQuery.setCriteria(packageIDCriteria.and(releaseLabelCriteria).and(higherVersionAppCriteria));
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"), (Object)search, 12);
            appAllLiveVersionQuery.setCriteria(appAllLiveVersionQuery.getCriteria().and(searchCriteria));
        }
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "IDENTIFIER"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppGroupToCollection", "APP_VERSION_STATUS"));
        appAllLiveVersionQuery.addSortColumn(new SortColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "LABEL_ASSIGNED_TIME"), false));
        final int count = MDMDBUtil.getCount(appAllLiveVersionQuery, "AppReleaseLabel", "RELEASE_LABEL_ID");
        responseJSON.put("paging", (Object)pagingUtil.getPagingJSON(count));
        if (count != 0) {
            if (!selectAll) {
                appAllLiveVersionQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("channel_name")) {
                        appAllLiveVersionQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    appAllLiveVersionQuery.addSortColumn(new SortColumn("MdAppDetails", "APP_VERSION", false));
                }
            }
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appAllLiveVersionQuery);
            while (ds.next()) {
                final JSONObject lowerVersionChannels = new JSONObject();
                lowerVersionChannels.put("bundle_identifier", ds.getValue("IDENTIFIER"));
                lowerVersionChannels.put("app_name", ds.getValue("APP_NAME"));
                lowerVersionChannels.put("version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                lowerVersionChannels.put("is_approved", ds.getValue("APP_VERSION_STATUS") != null);
                lowerVersionChannels.put("APP_VERSION".toLowerCase(), ds.getValue("APP_VERSION"));
                if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                    final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                    if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                        if (!displayImageLoc.startsWith("http")) {
                            lowerVersionChannels.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                        }
                        else {
                            lowerVersionChannels.put("icon", (Object)displayImageLoc);
                        }
                    }
                }
                lowerVersionChannels.put("release_label_id", ds.getValue("RELEASE_LABEL_ID"));
                lowerVersionChannels.put("release_label_name", (Object)I18N.getMsg((String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                jsonArray.put((Object)lowerVersionChannels);
            }
        }
        responseJSON.put("upgrade_available_versions", (Object)jsonArray);
        return responseJSON;
    }
    
    private void markGivenCollectionAsApproved(final Long packageId, final Long releaseLabelId) throws DataAccessException {
        final Criteria packageCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria labelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        final UpdateQuery updateQuery1 = (UpdateQuery)new UpdateQueryImpl("AppGroupToCollection");
        updateQuery1.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        updateQuery1.setCriteria(packageCriteria);
        updateQuery1.setUpdateColumn("APP_VERSION_STATUS", (Object)null);
        final UpdateQuery updateQuery2 = (UpdateQuery)new UpdateQueryImpl("AppGroupToCollection");
        updateQuery2.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        updateQuery2.setCriteria(packageCriteria.and(labelCriteria));
        updateQuery2.setUpdateColumn("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
        MDMUtil.getPersistence().update(updateQuery1);
        MDMUtil.getPersistence().update(updateQuery2);
    }
    
    @Override
    public void handleDBChangesForAppApproval(final JSONObject requestJSON) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            final Long packageId = requestJSON.getLong("PACKAGE_ID");
            final Long labelId = requestJSON.getLong("RELEASE_LABEL_ID");
            final Long appGroupId = requestJSON.getLong("APP_GROUP_ID");
            final Long approvedAppId = requestJSON.getLong("APPROVED_APP_ID");
            this.markGivenCollectionAsApproved(packageId, labelId);
            final List labelsWithLowerVersionApp = AppsUtil.getInstance().getListOfReleaseLabelsWithAppVersionLowerThanGivenApp(packageId, labelId, Boolean.FALSE);
            AppsUtil.getInstance().setApprovedAppIdForResource(labelsWithLowerVersionApp, appGroupId, approvedAppId);
            AppsUtil.getInstance().setAppUpdateForApps(appGroupId, labelId, false);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception ex) {
            BaseAppVersionHandler.logger.log(Level.SEVERE, "Exception in handleDBChangesForAppApproval", ex);
            MDMUtil.getUserTransaction().rollback();
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    protected Boolean checkIfAppVersionAreSame(final JSONObject uploadedAppVersion, final JSONObject validationAppVersion) {
        final String uploadedAppVersionName = uploadedAppVersion.getString("APP_VERSION");
        final String validationAppVersionName = validationAppVersion.getString("APP_VERSION");
        return uploadedAppVersionName.equals(validationAppVersionName);
    }
    
    static {
        BaseAppVersionHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
