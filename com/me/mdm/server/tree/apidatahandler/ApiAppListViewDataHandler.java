package com.me.mdm.server.tree.apidatahandler;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SortColumn;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppAssociationHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.Set;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Properties;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.HashSet;
import org.json.JSONArray;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.List;

public class ApiAppListViewDataHandler extends ApiListViewDataHandler
{
    List businessStoreList;
    List preferedLocationListForResource;
    
    public ApiAppListViewDataHandler() {
        this.businessStoreList = null;
        this.preferedLocationListForResource = null;
    }
    
    @Override
    protected JSONObject fetchResultObject() throws APIHTTPException {
        try {
            final Boolean isGroup = this.requestJson.optBoolean("isGroup");
            final Long customerId = this.requestJson.optLong("customerId");
            final String filterButtonVal = this.requestJson.optString("filterButtonVal");
            final JSONArray platformArray = this.requestJson.optJSONArray("platform");
            final Integer startIndex = this.requestJson.optInt("startIndex");
            final Integer noOfObj = this.requestJson.optInt("noOfObj");
            String selectAllValue = null;
            if (this.requestJson.optBoolean("selectAll")) {
                selectAllValue = "all";
            }
            final Long businessStoreID = this.requestJson.optLong("businessstore_id");
            Boolean containsDeviceResource = false;
            Integer totalResCount = 0;
            JSONObject resultJson = new JSONObject();
            int totaliosDevicesInGroups = 0;
            int totalUsersInGroups = 0;
            HashMap licensesAssociatedToResources = new HashMap();
            ProfileDistributionListHandler profileDisInstance = null;
            int platformType = 0;
            final List<Long> resIDList = new ArrayList<Long>();
            boolean isABMFilterNeeded = false;
            HashMap remainingLicenseCountMap = new HashMap();
            if (isGroup) {
                final JSONArray groupIdArray = this.requestJson.getJSONArray("groupIds");
                final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
                resIDList.addAll(groupIdList);
            }
            else {
                final JSONArray deviceIdArray = this.requestJson.getJSONArray("deviceIds");
                final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
                resIDList.addAll(deviceIdList);
            }
            if (isGroup) {
                final JSONArray groupIdArray = this.requestJson.getJSONArray("groupIds");
                final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
                totalResCount = groupIdList.size();
                profileDisInstance = ProfileDistributionListHandler.getDistributionProfileListHandler(platformType);
                remainingLicenseCountMap = profileDisInstance.getRemainingLicenseCountMap(customerId, this.businessStoreList);
                totaliosDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountInGroups(groupIdList, null, 1);
                if (this.preferedLocationListForResource.size() == 1) {
                    isABMFilterNeeded = true;
                }
                licensesAssociatedToResources = profileDisInstance.getLicensesAssociatedToGroupsMap(groupIdList, customerId, this.businessStoreList);
                totalUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountInGroups(groupIdList);
                final List grpList = CustomGroupingHandler.getCustomGroupsList(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupIdList.toArray(), 8).and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getMDMGroupType().toArray(), 8)));
                if (!grpList.isEmpty()) {
                    containsDeviceResource = true;
                }
            }
            else {
                final JSONArray deviceIdArray = this.requestJson.getJSONArray("deviceIds");
                final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
                totalResCount = deviceIdList.size();
                if (platformArray.length() > 0) {
                    platformType = platformArray.optInt(0);
                }
                profileDisInstance = ProfileDistributionListHandler.getDistributionProfileListHandler(platformType);
                if (platformType == 1 && deviceIdList.size() > 0) {
                    isABMFilterNeeded = true;
                }
                remainingLicenseCountMap = profileDisInstance.getRemainingLicenseCountMap(customerId, this.businessStoreList);
                totaliosDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountForResources(deviceIdList, null);
                totalUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountForResources(deviceIdList, null);
                licensesAssociatedToResources = profileDisInstance.getLicensesAssociatedToResourcesMap(deviceIdList, customerId, this.businessStoreList);
                final List deviceList = ManagedDeviceHandler.getInstance().getDeviceResourceIDs(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceIdList.toArray(), 8));
                if (!deviceList.isEmpty()) {
                    containsDeviceResource = true;
                }
            }
            ApiAppListViewDataHandler.logger.log(Level.FINE, "Query formation for profile filtered values completed with result");
            final JSONArray appDetails = new JSONArray();
            final Set<Long> appcategoryMap = new HashSet<Long>();
            final JSONArray appCategories = new JSONArray();
            try {
                final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)this.selectQuery);
                final List displayedAppGroupIds = new ArrayList();
                while (ds.next()) {
                    platformType = (int)ds.getValue("PLATFORM_TYPE");
                    final Long profileId = (Long)ds.getValue("PROFILE_ID");
                    final String profileName = (String)ds.getValue("PROFILE_NAME");
                    String appImageUrl = (String)ds.getValue("DISPLAY_IMAGE_LOC");
                    final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                    final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
                    String appCategory = (String)ds.getValue("APP_CATEGORY_LABEL");
                    appCategory = I18N.getMsg(appCategory, new Object[0]);
                    final int packageType = (int)ds.getValue("PACKAGE_TYPE");
                    final Long packageId = (Long)ds.getValue("PACKAGE_ID");
                    final Long appGroupId = (Long)ds.getValue("APP_GROUP_ID");
                    final Boolean paidApp = (Boolean)ds.getValue("IS_PAID_APP");
                    final Boolean purchasedApp = (Boolean)ds.getValue("IS_PURCHASED_FROM_PORTAL");
                    final Integer appAssignmentType = (Integer)ds.getValue("LICENSE_ASSIGN_TYPE");
                    String backgroundColor = (String)ds.getValue("IMG_BG");
                    final String releaseLabelDisplayName = (String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME");
                    final Long releaseLabelId = (Long)ds.getValue("RELEASE_LABEL_ID");
                    final Long appCategoryId = (Long)ds.getValue("APP_CATEGORY_ID");
                    final Integer appSharedScope = (Integer)ds.getValue("APP_SHARED_SCOPE");
                    if (displayedAppGroupIds.contains(appGroupId)) {
                        continue;
                    }
                    if ((backgroundColor == null || backgroundColor.equals("transparent")) && platformType == 3) {
                        backgroundColor = "#0078d7";
                    }
                    displayedAppGroupIds.add(appGroupId);
                    if (!appcategoryMap.contains(appCategoryId)) {
                        appcategoryMap.add(appCategoryId);
                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("category_id", (Object)appCategoryId);
                        jsonObject.put("APP_CATEGORY_NAME", (Object)appCategory);
                        jsonObject.put("app_category_key", (Object)ds.getValue("APP_CATEGORY_LABEL"));
                        appCategories.put((Object)jsonObject);
                    }
                    final JSONObject profileObject = new JSONObject();
                    final Properties userDataProperties = new Properties();
                    profileObject.put("app_id", (Object)packageId);
                    profileObject.put("name", (Object)profileName);
                    profileObject.put("platform_type", platformType);
                    profileObject.put("version", latestVer);
                    profileObject.put("isUpgrade", (Object)Boolean.FALSE);
                    profileObject.put("isBusinessStoreApp", (Object)purchasedApp);
                    profileObject.put("isEnabled", (Object)Boolean.TRUE);
                    profileObject.put("category", (Object)appCategory);
                    profileObject.put("release_label_id", (Object)releaseLabelId);
                    profileObject.put("release_label_name", (Object)releaseLabelDisplayName);
                    profileObject.put("background_color", (Object)backgroundColor);
                    profileObject.put("is_for_all_customers", appSharedScope == 1);
                    final int appType = MDMAppMgmtHandler.getInstance().getAppType(packageType, platformType);
                    if (appType != 0) {
                        final HashMap hm = new HashMap();
                        if (appImageUrl != null) {
                            appImageUrl = appImageUrl.replace("\\", "/");
                            hm.put("path", appImageUrl);
                            hm.put("IS_SERVER", true);
                            hm.put("IS_AUTHTOKEN", false);
                            hm.put("isApi", true);
                            appImageUrl = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        }
                    }
                    if (MDMStringUtils.isEmpty(appImageUrl)) {
                        appImageUrl = this.getDefaultAppImage(platformType);
                    }
                    profileObject.put("app_type", appType);
                    profileObject.put("app_image_url", (Object)appImageUrl);
                    boolean hasUnusedLicenses = true;
                    int totalCountInGroup = 0;
                    int remainingLicense = 0;
                    int totalLicense = 0;
                    int usedLicense = 0;
                    if (platformType != 3 && ((platformType != 2 && purchasedApp) || (platformType == 2 && purchasedApp && paidApp))) {
                        if (remainingLicenseCountMap.containsKey(appGroupId)) {
                            final JSONObject licenseSummaryJSON = remainingLicenseCountMap.get(appGroupId);
                            remainingLicense = licenseSummaryJSON.optInt("AVAILABLE_LICENSE_COUNT");
                            totalLicense = licenseSummaryJSON.optInt("TOTAL_LICENSE");
                            usedLicense = licenseSummaryJSON.optInt("ASSIGNED_LICENSE_COUNT");
                        }
                        if (appAssignmentType != null) {
                            if (appAssignmentType == 2) {
                                totalCountInGroup = totaliosDevicesInGroups;
                            }
                            else {
                                totalCountInGroup = totalUsersInGroups;
                            }
                        }
                        if (this.businessStoreList.size() == 1) {
                            hasUnusedLicenses = profileDisInstance.getIfAppHasEnoughLicensesForGroup(appGroupId, remainingLicense, totalCountInGroup, licensesAssociatedToResources);
                        }
                    }
                    if (!hasUnusedLicenses) {
                        final String insufficientLicenseMsg = I18N.getMsg("dc.mdm.group.insufficient_license", new Object[] { totalCountInGroup, remainingLicense });
                        if (!filterButtonVal.equalsIgnoreCase("associated")) {
                            ((Hashtable<String, String>)userDataProperties).put("remarks", insufficientLicenseMsg);
                            ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                        }
                        profileObject.put("isEnabled", (Object)Boolean.FALSE);
                    }
                    if (platformType == 4 && containsDeviceResource) {
                        final String insufficientLicenseMsg = I18N.getMsg("Chrome app cannot be distributed to the devices or device group.", new Object[] { totalCountInGroup, remainingLicense });
                        if (!filterButtonVal.equalsIgnoreCase("associated")) {
                            ((Hashtable<String, String>)userDataProperties).put("remarks", insufficientLicenseMsg);
                            ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", Boolean.TRUE);
                        }
                        profileObject.put("isEnabled", (Object)Boolean.FALSE);
                    }
                    final Long PROFILE_ID = (Long)ds.getValue("recent_profile_id");
                    final Integer updateCount = (Integer)ds.getValue("update_associated_count");
                    if (PROFILE_ID != null && updateCount != null && updateCount != 0) {
                        profileObject.put("isUpgrade", (Object)Boolean.TRUE);
                    }
                    if (filterButtonVal.equalsIgnoreCase("associated")) {
                        if (PROFILE_ID == null) {
                            continue;
                        }
                        if (PROFILE_ID != null) {
                            final Integer betaResCount = (Integer)ds.getValue("beta_res_count");
                            if (betaResCount != null && betaResCount != 0) {
                                profileObject.put("isEnabled", (Object)Boolean.FALSE);
                                ((Hashtable<String, String>)userDataProperties).put("remarks", I18N.getMsg("mdm.appmgmt.app_already_distributed", new Object[0]));
                                ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                            }
                        }
                    }
                    final JSONObject userDataJsonObject = IdpsUtil.convertPropertiesToJSONObject(userDataProperties);
                    profileObject.put("user_data", (Object)userDataJsonObject);
                    profileObject.put("availableLicense", remainingLicense);
                    profileObject.put("totalLicense", totalLicense);
                    profileObject.put("usedLicense", usedLicense);
                    appDetails.put((Object)profileObject);
                }
            }
            catch (final SQLException ex) {
                ApiAppListViewDataHandler.logger.log(Level.WARNING, "Exception occurred in fetchResultObject: {0}", ex);
            }
            catch (final QueryConstructionException ex2) {
                ApiAppListViewDataHandler.logger.log(Level.WARNING, "Exception occurred in fetchResultObject: {0}", (Throwable)ex2);
            }
            catch (final JSONException ex3) {
                ApiAppListViewDataHandler.logger.log(Level.WARNING, "Exception occurred in fetchResultObject: {0}", (Throwable)ex3);
            }
            if (filterButtonVal.equalsIgnoreCase("all") || filterButtonVal == "") {
                resultJson.put("yet_to_apply", (Object)appDetails);
            }
            if (filterButtonVal.equalsIgnoreCase("associated") || filterButtonVal == "") {
                resultJson.put("successfull_applied", (Object)appDetails);
            }
            resultJson.put("app_category", (Object)appCategories);
            resultJson.put("preferred_bs_store", (Object)JSONUtil.getInstance().convertListToStringJSONArray(this.preferedLocationListForResource));
            resultJson.put("is_abm_filter_needed", isABMFilterNeeded);
            resultJson = this.getTrashMessageEnable(this.requestJson, resultJson);
            return resultJson;
        }
        catch (final Exception ex4) {
            ApiAppListViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching filter data for apps", ex4);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    protected SelectQuery getSelectQuery() {
        final Long customerId = this.requestJson.optLong("customerId");
        (this.selectQuery = ProfileUtil.getInstance().getQueryforAppColln(customerId)).addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        this.selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        this.selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        this.selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        this.selectQuery.addJoin(new Join("MdPackageToAppData", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdPackageToAppData", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID", "MdPackageToAppData_APP_GROUP_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PRIVATE_APP_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        this.selectQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_LABEL"));
        this.selectQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "IMG_BG"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "STORE_ASSET_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
        return this.selectQuery;
    }
    
    @Override
    protected SelectQuery setCriteria() throws APIHTTPException {
        try {
            final Boolean isGroup = this.requestJson.optBoolean("isGroup");
            final JSONArray groupIdArray = this.requestJson.optJSONArray("groupIds");
            final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
            final JSONArray deviceIdArray = this.requestJson.optJSONArray("deviceIds");
            final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
            final JSONArray platformArray = this.requestJson.optJSONArray("platform");
            final List<Long> platformTypeList = JSONUtil.getInstance().convertLongJSONArrayTOList(platformArray);
            final Long customerId = this.requestJson.optLong("customerId");
            Long businessstore_id = this.requestJson.optLong("businessstore_id");
            final String searchValue = this.requestJson.optString("searchValue");
            final String filterButtonVal = this.requestJson.optString("filterButtonVal");
            final JSONArray appType = this.requestJson.optJSONArray("appType");
            final List<Long> appTypeList = JSONUtil.getInstance().convertLongJSONArrayTOList(appType);
            final JSONArray licenseType = this.requestJson.optJSONArray("licenseType");
            final List<Long> licenseTypeList = JSONUtil.getInstance().convertJSONArrayTOList(licenseType);
            final JSONArray categoryType = this.requestJson.optJSONArray("categoryType");
            final List<Long> categoryTypeList = JSONUtil.getInstance().convertLongJSONArrayTOList(categoryType);
            final List resIDList = new ArrayList();
            if (isGroup) {
                if (groupIdList.isEmpty()) {
                    throw new APIHTTPException("COM0005", new Object[] { "group_ids" });
                }
                resIDList.addAll(groupIdList);
                this.getAppGroupSubQuery(groupIdList);
            }
            else {
                if (deviceIdList.isEmpty()) {
                    throw new APIHTTPException("COM0005", new Object[] { "device_ids" });
                }
                resIDList.addAll(deviceIdList);
                this.getAppDeviceSubQuery(deviceIdList);
            }
            this.businessStoreList = new ArrayList();
            if (businessstore_id != null && businessstore_id != 0L) {
                this.businessStoreList.add(businessstore_id);
            }
            this.preferedLocationListForResource = VPPAppAssociationHandler.getInstance().getPreferedLocationListForResource(resIDList, isGroup);
            if (this.preferedLocationListForResource != null && this.preferedLocationListForResource.size() == 1 && this.businessStoreList.isEmpty()) {
                this.businessStoreList = this.preferedLocationListForResource;
                businessstore_id = this.businessStoreList.get(0);
            }
            final List selectColumnList = this.selectQuery.getSelectColumns();
            this.selectQuery.setGroupByClause(new GroupByClause(selectColumnList));
            Criteria criteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            criteria = criteria.and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria());
            criteria = criteria.and(this.getProfileTypeCriteria(2));
            if (!platformTypeList.isEmpty()) {
                criteria = criteria.and(this.getPlatformTypeCriteria(platformTypeList));
            }
            if (!appTypeList.isEmpty()) {
                criteria = criteria.and(this.getappTypeCriteria(appTypeList));
            }
            if (!licenseTypeList.isEmpty()) {
                criteria = criteria.and(this.getLicenseTypeCriteria(licenseTypeList));
            }
            if (!categoryTypeList.isEmpty()) {
                criteria = criteria.and(this.getCategoryCriteria(categoryTypeList));
            }
            if (searchValue != null && !searchValue.isEmpty()) {
                final Criteria searchProfileCri = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchValue, 12, false);
                criteria = criteria.and(searchProfileCri);
            }
            if (businessstore_id != null && businessstore_id != 0L) {
                final Criteria businessStoreCri = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessstore_id, 0);
                final Criteria otherNonPortalApps = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)null, 0);
                criteria = criteria.and(businessStoreCri.or(otherNonPortalApps));
            }
            if (customerId != null) {
                final Criteria customerCri = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = criteria.and(customerCri);
            }
            Criteria selectGroupCri = null;
            if (filterButtonVal.equalsIgnoreCase("all")) {
                selectGroupCri = new Criteria(Column.getColumn("derivedProfileTable", "recent_profile_res_count"), (Object)null, 0);
                final Criteria updatedCriteriaCheck = new Criteria(Column.getColumn("mdappcatalog", "update_associated_count"), (Object)null, 1);
                selectGroupCri = selectGroupCri.or(updatedCriteriaCheck);
            }
            else if (filterButtonVal.equalsIgnoreCase("associated")) {
                selectGroupCri = new Criteria(Column.getColumn("derivedProfileTable", "recent_profile_res_count"), (Object)null, 1);
            }
            if (selectGroupCri != null) {
                criteria = criteria.and(selectGroupCri);
            }
            this.selectQuery.setCriteria(criteria);
            final SortColumn sortCol = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
            this.selectQuery.addSortColumn(sortCol);
            this.selectQuery.setDistinct(true);
        }
        catch (final Exception ex) {
            ApiAppListViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching profile for distribution", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return this.selectQuery;
    }
    
    private void getAppGroupSubQuery(final List groupIds) {
        final Criteria availableDistributeAppCri = null;
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        final Column countCol = new Column("RecentProfileForGroup", "GROUP_ID").count();
        countCol.setColumnAlias("recent_profile_res_count");
        profileSQ.addSelectColumn(countCol);
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForGroup", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)groupIds.size(), 4));
        profileSQ.setGroupByClause(groupByClause);
        final Table profileTab = Table.getTable("Profile");
        final DerivedTable profileDerievedTab = new DerivedTable("derivedProfileTable", (Query)profileSQ);
        final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("derivedProfileTable", "PROFILE_ID"), 0);
        this.selectQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
        final Column recentProfileId = Column.getColumn("derivedProfileTable", "PROFILE_ID", "recent_profile_id");
        final Column recentProfileCountCol = Column.getColumn("derivedProfileTable", "recent_profile_res_count");
        this.selectQuery.addSelectColumn(recentProfileId);
        this.selectQuery.addSelectColumn(recentProfileCountCol);
        final SelectQuery prodSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForGroup"));
        prodSelectQuery.addJoin(new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        prodSelectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria updateAvailableJoinCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "GROUP_ID"), 0));
        prodSelectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToGroup", updateAvailableJoinCriteria, 2));
        prodSelectQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "ApprovedAppColln", 2));
        prodSelectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "AppGroupToCollection", 2));
        final Column groupColumn = new Column("RecentProfileForGroup", "GROUP_ID").count();
        groupColumn.setColumnAlias("update_associated_count");
        final GroupByColumn groupByCol = new GroupByColumn(new Column("RecentProfileForGroup", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByListCatalog = new ArrayList<GroupByColumn>();
        groupByListCatalog.add(groupByCol);
        final Criteria criteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
        final Criteria groupIdsCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupIds.toArray(), 8).and(new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8));
        final Criteria approvedAppVersionCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
        prodSelectQuery.setCriteria(criteria.and(groupIdsCriteria).and(approvedAppVersionCriteria));
        final GroupByClause groupByClauseCatalog = new GroupByClause((List)groupByListCatalog, new Criteria(groupColumn, (Object)0, 5));
        prodSelectQuery.setGroupByClause(groupByClauseCatalog);
        prodSelectQuery.addSelectColumn(groupColumn);
        prodSelectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        final DerivedTable derivedTable = new DerivedTable("mdappcatalog", (Query)prodSelectQuery);
        this.selectQuery.addJoin(new Join(Table.getTable("Profile"), (Table)derivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        final Column prodUpdateAssociatedCount = Column.getColumn("mdappcatalog", "update_associated_count");
        this.selectQuery.addSelectColumn(prodUpdateAssociatedCount);
        final SelectQuery betaAssociatedQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        betaAssociatedQuery.addJoin(new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        betaAssociatedQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria betaAppApprovedCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), 0));
        betaAssociatedQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToGroup", betaAppApprovedCriteria, 2));
        betaAssociatedQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "ApprovedAppColln", 2));
        betaAssociatedQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "AppGroupToCollection", 2));
        final Criteria groupIdCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8);
        final Criteria markedForDelFalse = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        final Criteria nonApprovedCriteria = AppVersionDBUtil.getInstance().getNonApprovedAppVersionCriteria();
        betaAssociatedQuery.setCriteria(groupIdCriteria.and(markedForDelFalse).and(nonApprovedCriteria));
        final Column betaResCount = new Column("RecentProfileForGroup", "GROUP_ID").count();
        final Column profileIdCol = Column.getColumn("RecentProfileForGroup", "PROFILE_ID");
        betaResCount.setColumnAlias("beta_res_count");
        betaAssociatedQuery.addSelectColumn(betaResCount);
        betaAssociatedQuery.addSelectColumn(profileIdCol);
        final List<Column> gbcColumnList = new ArrayList<Column>();
        gbcColumnList.add(profileIdCol);
        final GroupByClause profileGroupBy = new GroupByClause((List)gbcColumnList, new Criteria(betaResCount, (Object)groupIds.size(), 4));
        betaAssociatedQuery.setGroupByClause(profileGroupBy);
        final DerivedTable prodBetaResDerivedTable = new DerivedTable("prodbetares", (Query)betaAssociatedQuery);
        this.selectQuery.addJoin(new Join(Table.getTable("Profile"), (Table)prodBetaResDerivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        this.selectQuery.addSelectColumn(new Column("prodbetares", "beta_res_count"));
        final SortColumn sortProdUpdateAssociatedCount = new SortColumn(prodUpdateAssociatedCount, false);
        this.selectQuery.addSortColumn(sortProdUpdateAssociatedCount);
        final SortColumn sortRecentProfileIdCountCol = new SortColumn(recentProfileCountCol, false, false);
        this.selectQuery.addSortColumn(sortRecentProfileIdCountCol);
    }
    
    private void getAppDeviceSubQuery(final List deviceIds) {
        final Criteria availableDistributeAppCri = null;
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        final Column countCol = new Column("RecentProfileForResource", "RESOURCE_ID").count();
        countCol.setColumnAlias("recent_profile_res_count");
        profileSQ.addSelectColumn(countCol);
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds.toArray(), 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForResource", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)deviceIds.size(), 4));
        profileSQ.setGroupByClause(groupByClause);
        final Table profileTab = Table.getTable("Profile");
        final DerivedTable profileDerievedTab = new DerivedTable("derivedProfileTable", (Query)profileSQ);
        final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("derivedProfileTable", "PROFILE_ID"), 0);
        this.selectQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
        final Column recentProfileId = Column.getColumn("derivedProfileTable", "PROFILE_ID", "recent_profile_id");
        final Column recentProfileCountCol = Column.getColumn("derivedProfileTable", "recent_profile_res_count");
        this.selectQuery.addSelectColumn(recentProfileId);
        this.selectQuery.addSelectColumn(recentProfileCountCol);
        final SelectQuery prodSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        prodSelectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        prodSelectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria updateAvailableJoinCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"), 0));
        prodSelectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResourceExtn", updateAvailableJoinCriteria, 2));
        prodSelectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
        prodSelectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "ApprovedAppColln", 2));
        prodSelectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "AppGroupToCollection", 2));
        final Column groupColumn = new Column("RecentProfileForResource", "RESOURCE_ID").count();
        groupColumn.setColumnAlias("update_associated_count");
        final GroupByColumn groupByCol = new GroupByColumn(new Column("RecentProfileForResource", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByListCatalog = new ArrayList<GroupByColumn>();
        groupByListCatalog.add(groupByCol);
        final Criteria criteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
        final Criteria deviceIdsCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds.toArray(), 8);
        final Criteria approvedAppVersionCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
        prodSelectQuery.setCriteria(criteria.and(deviceIdsCriteria).and(approvedAppVersionCriteria));
        final GroupByClause groupByClauseCatalog = new GroupByClause((List)groupByListCatalog, new Criteria(groupColumn, (Object)0, 4));
        prodSelectQuery.setGroupByClause(groupByClauseCatalog);
        prodSelectQuery.addSelectColumn(groupColumn);
        prodSelectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        final DerivedTable derivedTable = new DerivedTable("mdappcatalog", (Query)prodSelectQuery);
        this.selectQuery.addJoin(new Join(Table.getTable("Profile"), (Table)derivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        final Column prodUpdateAssociatedCount = Column.getColumn("mdappcatalog", "update_associated_count");
        this.selectQuery.addSelectColumn(prodUpdateAssociatedCount);
        final SelectQuery betaAssociatedQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        betaAssociatedQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        betaAssociatedQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria betaAppAssociatedCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), 0));
        betaAssociatedQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResource", betaAppAssociatedCriteria, 2));
        betaAssociatedQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "ApprovedAppColln", 2));
        betaAssociatedQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "AppGroupToCollection", 2));
        final Criteria deviceIdCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds.toArray(), 8);
        final Criteria markedForDelFalse = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        final Criteria nonApprovedVersionCriteria = AppVersionDBUtil.getInstance().getNonApprovedAppVersionCriteria();
        betaAssociatedQuery.setCriteria(deviceIdCriteria.and(markedForDelFalse).and(nonApprovedVersionCriteria));
        final Column betaResCount = new Column("RecentProfileForResource", "RESOURCE_ID").count();
        final Column profileIdCol = Column.getColumn("RecentProfileForResource", "PROFILE_ID");
        betaResCount.setColumnAlias("beta_res_count");
        betaAssociatedQuery.addSelectColumn(betaResCount);
        betaAssociatedQuery.addSelectColumn(profileIdCol);
        final List<Column> gbcColumnList = new ArrayList<Column>();
        gbcColumnList.add(profileIdCol);
        final GroupByClause profileGroupBy = new GroupByClause((List)gbcColumnList, new Criteria(betaResCount, (Object)deviceIds.size(), 4));
        betaAssociatedQuery.setGroupByClause(profileGroupBy);
        final DerivedTable prodBetaResDerivedTable = new DerivedTable("prodbetares", (Query)betaAssociatedQuery);
        this.selectQuery.addJoin(new Join(Table.getTable("Profile"), (Table)prodBetaResDerivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        this.selectQuery.addSelectColumn(new Column("prodbetares", "beta_res_count"));
        final SortColumn sortProdUpdateAssociatedCount = new SortColumn(prodUpdateAssociatedCount, false);
        this.selectQuery.addSortColumn(sortProdUpdateAssociatedCount);
        final SortColumn sortRecentProfileIdCountCol = new SortColumn(recentProfileCountCol, false, false);
        this.selectQuery.addSortColumn(sortRecentProfileIdCountCol);
    }
    
    private Criteria getCategoryCriteria(final List categoryTypes) {
        final Criteria modelTypeCri = new Criteria(Column.getColumn("MdAppGroupCategoryRel", "APP_CATEGORY_ID"), (Object)categoryTypes.toArray(), 8);
        return modelTypeCri;
    }
    
    private Criteria getLicenseTypeCriteria(final List licenseTypes) {
        boolean filterApply = true;
        final boolean isPaidApp = licenseTypes.contains(702);
        if (isPaidApp && licenseTypes.contains(701)) {
            filterApply = false;
        }
        if (filterApply) {
            final Criteria licenseTypeCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)isPaidApp, 0);
            return licenseTypeCri;
        }
        return null;
    }
    
    private Criteria getappTypeCriteria(final List<Long> appTypes) {
        Criteria appTypeCri = null;
        for (final Long appType : appTypes) {
            if (appTypeCri == null) {
                appTypeCri = MDMUtil.getInstance().getPackageTypeCriteria(appType);
            }
            else {
                appTypeCri = appTypeCri.or(MDMUtil.getInstance().getPackageTypeCriteria(appType));
            }
        }
        return appTypeCri;
    }
    
    private String getDefaultAppImage(final int platformType) throws Exception {
        String value = null;
        if (platformType == 1) {
            value = "/images/ios-grey.png";
        }
        else if (platformType == 2) {
            value = "/images/android-grey.png";
        }
        else if (platformType == 3) {
            value = "/images/windows-grey.png";
        }
        else if (platformType == 4) {
            value = "/images/chrome-large.png";
        }
        return value;
    }
    
    private JSONObject getTrashMessageEnable(final JSONObject requestJSON, final JSONObject resultJson) {
        final Boolean isGroup = requestJSON.optBoolean("isGroup");
        final Long customerId = requestJSON.optLong("customerId");
        final JSONArray platformArray = requestJSON.optJSONArray("platform");
        final List<Long> platformTypeList = JSONUtil.getInstance().convertLongJSONArrayTOList(platformArray);
        try {
            final SelectQuery profileTrashQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            Criteria criteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.TRUE, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0));
            if (!platformTypeList.isEmpty()) {
                criteria = criteria.and(this.getPlatformTypeCriteria(platformTypeList));
            }
            profileTrashQuery.setCriteria(criteria);
            final int trashCount = DBUtil.getRecordCount(profileTrashQuery, "Profile", "PROFILE_ID");
            if (trashCount == 0) {
                return resultJson;
            }
            int trashedApps = 0;
            final SelectQuery appTrashCountQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
            List<Long> resourceIdList = null;
            if (isGroup) {
                final JSONArray groupIdArray = requestJSON.getJSONArray("groupIds");
                resourceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
                appTrashCountQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
                appTrashCountQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            else {
                final JSONArray deviceIdArray = requestJSON.getJSONArray("deviceIds");
                resourceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
                appTrashCountQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            appTrashCountQuery.addJoin(new Join("ManagedDevice", "MdAppGroupDetails", new String[] { "PLATFORM_TYPE" }, new String[] { "PLATFORM_TYPE" }, 2));
            appTrashCountQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appTrashCountQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            appTrashCountQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            appTrashCountQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            appTrashCountQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appTrashCountQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            final Criteria resCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
            final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            appTrashCountQuery.setCriteria(resCriteria.and(trashCriteria).and(customerCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(appTrashCountQuery);
            final Iterator iterator = dataObject.getRows("Profile");
            while (iterator.hasNext()) {
                ++trashedApps;
                iterator.next();
            }
            resultJson.put("is_app_inTrash", trashedApps > 0);
            resultJson.put("trash_count", trashedApps);
        }
        catch (final Exception e) {
            ApiAppListViewDataHandler.logger.log(Level.WARNING, "Unbale to get if apps Present in trash : ", e);
        }
        return resultJson;
    }
}
