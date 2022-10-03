package com.me.mdm.server.apps.multiversion;

import java.util.HashSet;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.api.APIUtil;
import java.util.Set;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.Arrays;
import com.adventnet.ds.query.GroupByColumn;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONException;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.AppVersionChecker;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.logging.Logger;

public class AppVersionDBUtil
{
    private static Logger logger;
    private static AppVersionDBUtil appVersionDBUtil;
    public static final Integer RELEASE_LABEL_PRODUCTION;
    public static final Integer RELEASE_LABEL_BETA;
    private static Map<Integer, String> releaseTypeToDisplayName;
    
    private AppVersionDBUtil() {
    }
    
    public static AppVersionDBUtil getInstance() {
        if (AppVersionDBUtil.appVersionDBUtil == null) {
            AppVersionDBUtil.appVersionDBUtil = new AppVersionDBUtil();
        }
        return AppVersionDBUtil.appVersionDBUtil;
    }
    
    @Deprecated
    public Boolean addLabelCriteriaAndJoin(final SelectQuery selectQuery, final Integer releaseLabelType, final Long customerId) {
        Boolean isQueryModified = Boolean.FALSE;
        final List tableList = selectQuery.getTableList();
        if (tableList.contains(Table.getTable("AppGroupToCollection"))) {
            if (!tableList.contains(Table.getTable("AppReleaseLabel"))) {
                final Join appReleaseLabelJoin = this.getAppReleaseLabelJoin();
                selectQuery.addJoin(appReleaseLabelJoin);
            }
            final Criteria appReleaseLabelCriteria = this.getAppReleaseLabelCriteria(releaseLabelType, customerId);
            Criteria sqlCriteria = selectQuery.getCriteria();
            if (sqlCriteria != null) {
                sqlCriteria = sqlCriteria.and(appReleaseLabelCriteria);
            }
            else {
                sqlCriteria = appReleaseLabelCriteria;
            }
            selectQuery.setCriteria(sqlCriteria);
            isQueryModified = Boolean.TRUE;
        }
        return isQueryModified;
    }
    
    @Deprecated
    public Criteria getAppReleaseLabelCriteria(final Integer releaseLabelType, final Long customerId) {
        Criteria appReleaseLabelCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)releaseLabelType, 0);
        Criteria customerIdCriteria = null;
        if (customerId != null) {
            customerIdCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerId, 0);
            appReleaseLabelCriteria = appReleaseLabelCriteria.and(customerIdCriteria);
        }
        return appReleaseLabelCriteria;
    }
    
    public void addDefaultAppReleaseLabels(final Long customerId) throws DataAccessException {
        AppVersionDBUtil.logger.log(Level.INFO, "Going to populate default release labels for customerId-{0}", customerId);
        final DataObject appReleaseLabelDO = (DataObject)new WritableDataObject();
        for (final Map.Entry<Integer, String> releaseDetail : AppVersionDBUtil.releaseTypeToDisplayName.entrySet()) {
            final Row releaseLabelRow = new Row("AppReleaseLabel");
            releaseLabelRow.set("RELEASE_LABEL_TYPE", (Object)releaseDetail.getKey());
            releaseLabelRow.set("RELEASE_LABEL_DISPLAY_NAME", (Object)releaseDetail.getValue());
            releaseLabelRow.set("CUSTOMER_ID", (Object)customerId);
            appReleaseLabelDO.addRow(releaseLabelRow);
        }
        MDMUtil.getPersistence().add(appReleaseLabelDO);
        AppVersionDBUtil.logger.log(Level.INFO, "Successfully populated default release labels for customerId-{0}", customerId);
    }
    
    public Criteria getCriteriaForCollectionIdWithMdAppToCollection() {
        final Criteria collectionCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)Column.getColumn("MdAppToCollection", "COLLECTION_ID"), 0);
        return collectionCriteria;
    }
    
    public Criteria getCriteriaForCollectionIdWithProfileToCollection() {
        final Criteria collectionCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)Column.getColumn("ProfileToCollection", "COLLECTION_ID"), 0);
        return collectionCriteria;
    }
    
    public Join getAppReleaseLabelJoin() {
        final Join appReleaseLabelJoin = this.getAppReleaseLabelJoin(2);
        return appReleaseLabelJoin;
    }
    
    public Join getAppReleaseLabelJoin(final Integer joinType) {
        final Join appReleaseLabelJoin = new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, (int)joinType);
        return appReleaseLabelJoin;
    }
    
    public Long getProductionAppReleaseLabelIDForCustomer(final Long customerId) throws DataAccessException {
        return this.getAppReleaseLabelID(AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, customerId);
    }
    
    private Long getAppReleaseLabelID(final Integer releaseLabelType, final Long customerId) throws DataAccessException {
        Long releaseLabelId = -1L;
        final SelectQuery appReleaseLabelIdQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        appReleaseLabelIdQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        final Criteria releaseLabelTypeCri = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)releaseLabelType, 0);
        final Criteria customerCri = new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerId, 0);
        appReleaseLabelIdQuery.setCriteria(releaseLabelTypeCri.and(customerCri));
        final DataObject appReleaseLabelDO = DataAccess.get(appReleaseLabelIdQuery);
        if (!appReleaseLabelDO.isEmpty()) {
            releaseLabelId = (Long)appReleaseLabelDO.getFirstRow("AppReleaseLabel").get("RELEASE_LABEL_ID");
        }
        return releaseLabelId;
    }
    
    public Long getApprovedReleaseLabelForGivePackage(final Long packageId, final Long customerId) throws DataAccessException {
        Long releaseLabelId = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("AppGroupToCollection", "*"));
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria packageCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageId, 0);
        selectQuery.setCriteria(this.getApprovedAppVersionCriteria().and(customerCriteria).and(packageCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row appGroupCollectionRow = dataObject.getFirstRow("AppGroupToCollection");
            releaseLabelId = (Long)appGroupCollectionRow.get("RELEASE_LABEL_ID");
        }
        return releaseLabelId;
    }
    
    public Criteria getApprovedAppVersionCriteria() {
        return new Criteria(new Column("AppGroupToCollection", "APP_VERSION_STATUS"), (Object)AppMgmtConstants.APP_VERSION_APPROVED, 0);
    }
    
    public Criteria getNonApprovedAppVersionCriteria() {
        return new Criteria(Column.getColumn("AppGroupToCollection", "APP_VERSION_STATUS"), (Object)null, 0);
    }
    
    public JSONObject getPossibleUpdatesForTheGivenVersionOfApp(final JSONObject uploadedAppDetails) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Boolean isSameVersionPresent = Boolean.FALSE;
        try {
            jsonObject.put("isPreviousVersionOfAppAvailable", (Object)Boolean.FALSE);
            jsonObject.put("existingAppName", (Object)"");
            jsonObject.put("existingAppPackageId", (Object)"");
            jsonObject.put("existingAppGroupId", (Object)"");
            jsonObject.put("possibleUpdates", (Object)"");
            final String uploadedBundleIdentifier = String.valueOf(uploadedAppDetails.get("packagename"));
            final String uploadedAppVersion = String.valueOf(uploadedAppDetails.get("versionname")).trim();
            final String uploadedAppVersionCode = uploadedAppDetails.optString("versioncode", "--").trim();
            final JSONObject uploadedVersionDetails = new JSONObject();
            uploadedVersionDetails.put("APP_VERSION", (Object)uploadedAppVersion);
            uploadedVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)uploadedAppVersionCode);
            final Long customerId = uploadedAppDetails.getLong("CUSTOMER_ID");
            final Integer platformType = uploadedAppDetails.getInt("PLATFORM_TYPE");
            final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
            appAllLiveVersionQuery.addJoin(new Join("AppGroupToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            appAllLiveVersionQuery.addJoin(getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            Criteria appGroupBundleIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)uploadedBundleIdentifier, 0, (boolean)Boolean.FALSE);
            final Criteria appGroupPlatformCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            if (platformType.equals(3)) {
                appGroupBundleIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)uploadedBundleIdentifier, 10, (boolean)Boolean.FALSE);
            }
            appAllLiveVersionQuery.setCriteria(appGroupBundleIdCri.and(customerCri).and(appGroupPlatformCri));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_VERSION_STATUS"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"));
            appAllLiveVersionQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            appAllLiveVersionQuery.addSortColumn(new SortColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "LABEL_ASSIGNED_TIME"), false));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appAllLiveVersionQuery);
            while (ds.next()) {
                final String appVersion = (String)ds.getValue("APP_VERSION");
                final String appVersionCode = (String)ds.getValue("APP_NAME_SHORT_VERSION");
                jsonObject.put("isPreviousVersionOfAppAvailable", (Object)Boolean.TRUE);
                jsonObject.put("isAppMovedToTrash", (ds.getValue("IS_MOVED_TO_TRASH") != null) ? ds.getValue("IS_MOVED_TO_TRASH") : Boolean.FALSE);
                jsonObject.put("existingAppPackageId", (Object)ds.getValue("PACKAGE_ID"));
                jsonObject.put("existingAppGroupId", (Object)ds.getValue("APP_GROUP_ID"));
                final JSONObject versionDetails = new JSONObject();
                versionDetails.put("APP_VERSION", (Object)appVersion);
                versionDetails.put("APP_NAME_SHORT_VERSION", (Object)appVersionCode);
                final Boolean isGreaterOrEqual = AppVersionChecker.getInstance(platformType).isAppVersionGreaterOrEqual(uploadedVersionDetails, versionDetails);
                if (isGreaterOrEqual) {
                    final JSONObject lowerVersionApps = new JSONObject();
                    lowerVersionApps.put("bundle_identifier", ds.getValue("IDENTIFIER"));
                    lowerVersionApps.put("app_name", ds.getValue("APP_NAME"));
                    lowerVersionApps.put("version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                    lowerVersionApps.put("app_version", ds.getValue("APP_VERSION"));
                    if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                        final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                        if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                            if (!displayImageLoc.startsWith("http")) {
                                lowerVersionApps.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                            }
                            else {
                                lowerVersionApps.put("icon", (Object)displayImageLoc);
                            }
                        }
                    }
                    final Boolean isApproved = ds.getValue("APP_VERSION_STATUS") != null;
                    lowerVersionApps.put("release_label_id", ds.getValue("RELEASE_LABEL_ID"));
                    String versionLabel = (String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME");
                    versionLabel = ((versionLabel != null) ? I18N.getMsg(versionLabel, new Object[0]) : "");
                    lowerVersionApps.put("release_label_name", (Object)versionLabel);
                    lowerVersionApps.put("is_approved", (Object)isApproved);
                    if ((platformType == 2 && uploadedAppVersionCode.equals(appVersionCode) && uploadedAppVersion.equals(appVersion)) || (platformType != 2 && uploadedAppVersion.equals(appVersion))) {
                        jsonArray = new JSONArray();
                        jsonArray.put((Object)lowerVersionApps);
                        isSameVersionPresent = Boolean.TRUE;
                        break;
                    }
                    jsonArray.put((Object)lowerVersionApps);
                }
            }
            jsonObject.put("possibleUpdates", (Object)jsonArray);
            jsonObject.put("isSameVersionPresent", (Object)isSameVersionPresent);
        }
        catch (final JSONException ex) {
            AppVersionDBUtil.logger.log(Level.SEVERE, (Throwable)ex, () -> "Exception while getting possible updates  for app with details ->" + jsonObject2.toString());
            throw ex;
        }
        catch (final Exception ex2) {
            AppVersionDBUtil.logger.log(Level.SEVERE, ex2, () -> "Exception while getting possible updates  for app with details ->" + jsonObject3.toString());
            throw ex2;
        }
        return jsonObject;
    }
    
    public String getChannelName(final Long releaseLabelID) throws DataAccessException, Exception {
        final List list = new ArrayList();
        list.add(releaseLabelID);
        return this.getChannelNameMap(list).get(releaseLabelID);
    }
    
    public HashMap getChannelNameMap(final Collection<Long> releaseLabelIDs) throws DataAccessException, Exception {
        final HashMap channelIDNameMap = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelIDs.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final DataObject releaseLabelDao = DataAccess.get(selectQuery);
        final Iterator<Row> releaseLabelRows = releaseLabelDao.getRows("AppReleaseLabel");
        while (releaseLabelRows.hasNext()) {
            final Row releaseLabelRow = releaseLabelRows.next();
            channelIDNameMap.put(releaseLabelRow.get("RELEASE_LABEL_ID"), I18N.getMsg((String)releaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
        }
        return channelIDNameMap;
    }
    
    @Deprecated
    public void checkIfChannelAllowedToBeMerged(final JSONObject mergeRequestJSON) throws Exception {
        final Long packageID = (Long)mergeRequestJSON.get("PACKAGE_ID");
        final Long releaseLabelID = (Long)mergeRequestJSON.get("RELEASE_LABEL_ID");
        final List releaseLabelsToBeMerged = JSONUtil.convertJSONArrayToList(mergeRequestJSON.getJSONArray("release_labels_to_merge"));
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageID, releaseLabelID);
        final int platformType = appDetailsMap.get("PLATFORM_TYPE");
        final JSONObject baseChannelVersion = new JSONObject();
        baseChannelVersion.put("APP_VERSION", appDetailsMap.get("APP_VERSION"));
        baseChannelVersion.put("APP_NAME_SHORT_VERSION", appDetailsMap.get("APP_NAME_SHORT_VERSION"));
        final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria packageIDCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria releaseLabelIDCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelsToBeMerged.toArray(), 8);
        appAllLiveVersionQuery.setCriteria(packageIDCriteria.and(releaseLabelIDCriteria));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        final DataObject dao = DataAccess.get(appAllLiveVersionQuery);
        final Iterator<Row> channelVersionRows = dao.getRows("MdAppDetails");
        while (channelVersionRows.hasNext()) {
            final Row channelVersionRow = channelVersionRows.next();
            final JSONObject channelToBeMergedVersion = new JSONObject();
            channelToBeMergedVersion.put("APP_VERSION", channelVersionRow.get("APP_VERSION"));
            channelToBeMergedVersion.put("APP_NAME_SHORT_VERSION", channelVersionRow.get("APP_NAME_SHORT_VERSION"));
            final Boolean isGreater = AppVersionChecker.getInstance(platformType).isAppVersionGreater(baseChannelVersion, channelToBeMergedVersion);
            if (!isGreater) {
                AppVersionDBUtil.logger.log(Level.SEVERE, "Channel chosen to be merged has the higher version {0} than the version in the base channel {1} for the appID {2}", new Object[] { channelVersionRow.get("APP_VERSION"), appDetailsMap.get("APP_VERSION"), packageID });
                throw new APIHTTPException("APP0024", new Object[] { baseChannelVersion.get("APP_VERSION"), channelToBeMergedVersion.get("APP_VERSION") });
            }
        }
    }
    
    public Long getAppIdFromPackageAppGroupIdAndReleaseLabel(final Long releaseLabelId, final JSONObject otherParams) throws Exception {
        Long appId = null;
        final SelectQuery appIdQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        appIdQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appIdQuery.addJoin(new Join("AppGroupToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appIdQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        appIdQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        Criteria crit = null;
        if (otherParams != null && otherParams.has("PACKAGE_ID")) {
            final Criteria packageCriteria = crit = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)otherParams.getLong("PACKAGE_ID"), 0);
        }
        if (otherParams != null && otherParams.has("APP_GROUP_ID")) {
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)otherParams.getLong("APP_GROUP_ID"), 0);
            if (crit == null) {
                crit = appGroupCriteria;
            }
            else {
                crit = crit.and(appGroupCriteria);
            }
        }
        if (crit == null) {
            throw new Exception("Method call should have either packageId as not null or a valid APP_GROUP_ID key in otherParams");
        }
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        appIdQuery.setCriteria(crit.and(releaseLabelCriteria));
        appIdQuery.addSelectColumn(Column.getColumn("InstallAppPolicy", "CONFIG_DATA_ITEM_ID"));
        appIdQuery.addSelectColumn(Column.getColumn("InstallAppPolicy", "APP_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(appIdQuery);
        final Row installAppPolicyRow = dao.getFirstRow("InstallAppPolicy");
        if (installAppPolicyRow != null) {
            appId = (Long)installAppPolicyRow.get("APP_ID");
        }
        return appId;
    }
    
    public Long getAppIdFromConfigDataItemId(final Long configDataItem) throws Exception {
        return (Long)DBUtil.getValueFromDB("InstallAppPolicy", "CONFIG_DATA_ITEM_ID", (Object)configDataItem, "APP_ID");
    }
    
    public Join getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable() {
        return this.getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable(null);
    }
    
    public Join getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable(final String baseTableAlias) {
        return this.getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable(baseTableAlias, null);
    }
    
    public Join getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable(final String baseTableAlias, final String derivedTableSuffix) {
        return this.getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable(baseTableAlias, derivedTableSuffix, 2);
    }
    
    public Join getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable(final String baseTableAlias, final String derivedTableSuffix, final Integer joinType) {
        final SelectQuery latestAssignedTimeSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppCollnToReleaseLabelHistory"));
        final Column latestAssignedTime = Column.getColumn("AppCollnToReleaseLabelHistory", "LABEL_ASSIGNED_TIME").maximum();
        final String tableColAliasSuffix = MDMStringUtils.isEmpty(derivedTableSuffix) ? "_MAX" : ("_MAX_" + derivedTableSuffix);
        final String colAlias = ("LABEL_ASSIGNED_TIME" + tableColAliasSuffix).toLowerCase();
        latestAssignedTime.setColumnAlias(colAlias);
        latestAssignedTimeSubQuery.addSelectColumn(latestAssignedTime);
        final Column appCollnIdColumn = Column.getColumn("AppCollnToReleaseLabelHistory", "COLLECTION_ID");
        latestAssignedTimeSubQuery.addSelectColumn(appCollnIdColumn);
        latestAssignedTimeSubQuery.setGroupByClause(new GroupByClause((List)Arrays.asList(new GroupByColumn(appCollnIdColumn, (boolean)Boolean.FALSE))));
        final DerivedTable derivedTable = new DerivedTable("AppCollnToReleaseLabelHistory" + tableColAliasSuffix, (Query)latestAssignedTimeSubQuery);
        Table baseTable = Table.getTable("AppCollnToReleaseLabelHistory");
        if (!MDMStringUtils.isEmpty(baseTableAlias)) {
            baseTable = Table.getTable("AppCollnToReleaseLabelHistory", baseTableAlias);
        }
        final Join appCollnReleaseLabelToMaxJoin = new Join(baseTable, (Table)derivedTable, new String[] { "COLLECTION_ID", "LABEL_ASSIGNED_TIME" }, new String[] { "COLLECTION_ID", colAlias }, (int)joinType);
        return appCollnReleaseLabelToMaxJoin;
    }
    
    public Criteria getCriteriaForCollectionsLatestAppReleaseLabelFromHistoryTable(final String baseTableAlias) {
        final Criteria releaseLabelCri = new Criteria(Column.getColumn(baseTableAlias, "RELEASE_LABEL_ID"), (Object)Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), 0);
        return releaseLabelCri;
    }
    
    public Long getReleaseLabelIdForCollectionInAppGroupToCollection(final Long collectionId) throws Exception {
        Long retVal = null;
        if (collectionId != null) {
            retVal = (Long)DBUtil.getValueFromDB("AppGroupToCollection", "COLLECTION_ID", (Object)collectionId, "RELEASE_LABEL_ID");
        }
        return retVal;
    }
    
    public Map<Long, Map<String, String>> getAvailableReleaseLabelsForSpecificPackage(final Long packageId) throws Exception {
        final SelectQuery releaseLabelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        releaseLabelQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        releaseLabelQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        releaseLabelQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        releaseLabelQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        releaseLabelQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        releaseLabelQuery.addJoin(new Join("AppGroupToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        releaseLabelQuery.addJoin(getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        releaseLabelQuery.addJoin(new Join("AppGroupToCollection", "ReleaseLabelToAppTrack", new String[] { "APP_GROUP_ID", "RELEASE_LABEL_ID" }, new String[] { "APP_GROUP_ID", "RELEASE_LABEL_ID" }, 1));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_VERSION_STATUS"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("MdPackage", "PLATFORM_TYPE"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("ReleaseLabelToAppTrack", "STATUS"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("ReleaseLabelToAppTrack", "TRACK_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("ReleaseLabelToAppTrack", "RELEASE_LABEL_ID"));
        releaseLabelQuery.addSelectColumn(Column.getColumn("ReleaseLabelToAppTrack", "APP_GROUP_ID"));
        releaseLabelQuery.addSortColumn(new SortColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "LABEL_ASSIGNED_TIME"), false));
        releaseLabelQuery.setCriteria(new Criteria(new Column("MdPackage", "PACKAGE_ID"), (Object)packageId, 0));
        final Map<Long, Map<String, String>> relLabelIdToData = new LinkedHashMap<Long, Map<String, String>>();
        final DataObject dao = MDMUtil.getPersistence().get(releaseLabelQuery);
        if (!dao.isEmpty()) {
            final Row packageRow = dao.getFirstRow("MdPackage");
            final Integer platformType = (Integer)packageRow.get("PLATFORM_TYPE");
            final Iterator<Row> appDetailsIter = dao.getRows("MdAppDetails");
            while (appDetailsIter.hasNext()) {
                boolean isDistributable = true;
                final Row appDetailRow = appDetailsIter.next();
                final Long appId = (Long)appDetailRow.get("APP_ID");
                final String appVersionName = (String)appDetailRow.get("APP_VERSION");
                final String appVersionCode = (String)appDetailRow.get("APP_NAME_SHORT_VERSION");
                final Row mdAppToCollnRow = dao.getRow("MdAppToCollection", new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)appId, 0));
                final Long collectionId = (Long)mdAppToCollnRow.get("COLLECTION_ID");
                final Row appGroupToCollnRow = dao.getRow("AppGroupToCollection", new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
                final Long releaseLabelId = (Long)appGroupToCollnRow.get("RELEASE_LABEL_ID");
                final Integer appVersionStatus = (Integer)appGroupToCollnRow.get("APP_VERSION_STATUS");
                final Row appTrackRow = dao.getRow("ReleaseLabelToAppTrack", new Criteria(Column.getColumn("ReleaseLabelToAppTrack", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0));
                Map relLabelData = relLabelIdToData.get(releaseLabelId);
                if (relLabelData == null) {
                    relLabelData = new HashMap();
                    final Row appReleaseLabelRow = dao.getRow("AppReleaseLabel", new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0));
                    final String displayI18NKey = I18N.getMsg((String)appReleaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]);
                    relLabelData.put("RELEASE_LABEL_DISPLAY_NAME", displayI18NKey);
                    relLabelData.put("APP_VERSION_STATUS", appVersionStatus != null);
                }
                final String existAppVersion = relLabelData.get("APP_VERSION");
                final String existAppVersionCode = relLabelData.getOrDefault("APP_NAME_SHORT_VERSION", "--");
                if (existAppVersion == null) {
                    relLabelData.put("APP_VERSION", appVersionName);
                    relLabelData.put("APP_NAME_SHORT_VERSION", appVersionCode);
                }
                else {
                    final JSONObject appVersionDetails = new JSONObject();
                    appVersionDetails.put("APP_VERSION", (Object)appVersionName);
                    appVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)appVersionCode);
                    final JSONObject existAppVersionDetails = new JSONObject();
                    existAppVersionDetails.put("APP_VERSION", (Object)existAppVersion);
                    existAppVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)existAppVersionCode);
                    final Boolean isAppVersionGreaterThanExisting = AppVersionChecker.getInstance(platformType).isAppVersionGreater(appVersionDetails, existAppVersionDetails);
                    if (isAppVersionGreaterThanExisting) {
                        relLabelData.put("APP_VERSION", appVersionName);
                        relLabelData.put("APP_NAME_SHORT_VERSION", appVersionCode);
                    }
                }
                if (appTrackRow != null) {
                    isDistributable = ((int)appTrackRow.get("STATUS") != 0);
                }
                relLabelData.put("is_distributable", isDistributable);
                relLabelIdToData.put(releaseLabelId, relLabelData);
            }
        }
        return relLabelIdToData;
    }
    
    public JSONObject getCollectionForPackageAndReleaseLabel(final JSONObject inputJson) throws JSONException, DataAccessException {
        final JSONObject respJson = new JSONObject();
        final Long packageId = (Long)inputJson.get("PACKAGE_ID");
        final Long releaseLabelId = (Long)inputJson.get("RELEASE_LABEL_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(packageCriteria.and(releaseLabelCriteria));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        if (!dao.isEmpty()) {
            final Row profileToCollnRow = dao.getFirstRow("ProfileToCollection");
            if (profileToCollnRow != null) {
                final Long collectionId = (Long)profileToCollnRow.get("COLLECTION_ID");
                final Long profileId = (Long)profileToCollnRow.get("PROFILE_ID");
                respJson.put("COLLECTION_ID", (Object)collectionId);
                respJson.put("PROFILE_ID", (Object)profileId);
            }
        }
        return respJson;
    }
    
    @Deprecated
    public void validateIfAppReleaseLabelIsNotInProductionLabel(final Long releaseLabelId) throws DataAccessException {
        final SelectQuery releaseLabelBetaQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        final Criteria releaseLabelIdCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        final Criteria releaseLabelTypeCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, 1);
        releaseLabelBetaQuery.setCriteria(releaseLabelIdCriteria.and(releaseLabelTypeCriteria));
        releaseLabelBetaQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(releaseLabelBetaQuery);
        if (dao.isEmpty()) {
            throw new APIHTTPException("APP0015", new Object[0]);
        }
    }
    
    public JSONObject convertCollnToResListAsJSON(final Map<Long, List> collectionToApplicableResList) throws JSONException {
        final JSONObject collnToResJSON = new JSONObject();
        final Set<Long> collnIds = collectionToApplicableResList.keySet();
        for (final Long collectionId : collnIds) {
            final List<Long> resList = collectionToApplicableResList.get(collectionId);
            final JSONArray resJsonArr = new JSONArray();
            for (final Long resId : resList) {
                resJsonArr.put((Object)resId);
            }
            collnToResJSON.put(String.valueOf(collectionId), (Object)resJsonArr);
        }
        return collnToResJSON;
    }
    
    public Map<Long, String> getCollectionToReleaseLabelNameMap(final List collectionIdList) throws Exception {
        final Map<Long, String> collnIdToReleaseLabelText = new HashMap<Long, String>();
        final SelectQuery appCollectionLabelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        appCollectionLabelQuery.addJoin(this.getAppReleaseLabelJoin());
        final Criteria collnListCri = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionIdList.toArray(), 8);
        appCollectionLabelQuery.setCriteria(collnListCri);
        appCollectionLabelQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        appCollectionLabelQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
        appCollectionLabelQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appCollectionLabelQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final DataObject dao = DataAccess.get(appCollectionLabelQuery);
        if (!dao.isEmpty()) {
            final Iterator<Row> appGrpToCollnRows = dao.getRows("AppGroupToCollection");
            while (appGrpToCollnRows.hasNext()) {
                final Row appGrpToCollnRow = appGrpToCollnRows.next();
                final Long collectionId = (Long)appGrpToCollnRow.get("COLLECTION_ID");
                final Long releaseLabelId = (Long)appGrpToCollnRow.get("RELEASE_LABEL_ID");
                final Row appRelLabelRow = dao.getRow("AppReleaseLabel", new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0));
                String appRelLabelName = (String)appRelLabelRow.get("RELEASE_LABEL_DISPLAY_NAME");
                appRelLabelName = I18N.getMsg(appRelLabelName, new Object[0]);
                collnIdToReleaseLabelText.put(collectionId, appRelLabelName);
            }
        }
        return collnIdToReleaseLabelText;
    }
    
    public Long getReleaseLabelIdForAppCollectionId(final Long appCollectionId) {
        Long releaseLabelId = null;
        try {
            releaseLabelId = (Long)DBUtil.getValueFromDB("AppGroupToCollection", "COLLECTION_ID", (Object)appCollectionId, "RELEASE_LABEL_ID");
        }
        catch (final Exception ex) {
            AppVersionDBUtil.logger.log(Level.SEVERE, "Exception while trying to obtain the ReleaseLabelId for the CollectionId", ex);
        }
        return releaseLabelId;
    }
    
    public JSONArray convertMapOfReleaseLabelToJSONArray(final Map<Long, Map<String, String>> releaseLabelIdToDetails) throws JSONException {
        final JSONArray relLabelDetailsArr = new JSONArray();
        for (final Long relLabel : releaseLabelIdToDetails.keySet()) {
            final Map<String, String> relLabelDetails = releaseLabelIdToDetails.get(relLabel);
            final JSONObject relLabelDetail = new JSONObject();
            relLabelDetail.put("RELEASE_LABEL_ID".toLowerCase(), (Object)String.valueOf(relLabel));
            relLabelDetail.put("release_label_name", (Object)relLabelDetails.get("RELEASE_LABEL_DISPLAY_NAME"));
            relLabelDetail.put("APP_VERSION".toLowerCase(), (Object)AppsUtil.getValidVersion(relLabelDetails.get("APP_VERSION")));
            relLabelDetail.put("version_code", (Object)relLabelDetails.get("APP_NAME_SHORT_VERSION"));
            relLabelDetail.put("is_approved", (Object)relLabelDetails.get("APP_VERSION_STATUS"));
            relLabelDetail.put("is_distributable", (Object)relLabelDetails.get("is_distributable"));
            relLabelDetailsArr.put((Object)relLabelDetail);
        }
        return relLabelDetailsArr;
    }
    
    @Deprecated
    public int getLatestReleaseLabelType(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        final Column column = Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE").maximum();
        column.setColumnAlias("MaxReleaseLabelType");
        selectQuery.addSelectColumn(column);
        final Criteria criteria = new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(criteria);
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        ds.next();
        return (int)ds.getValue("MaxReleaseLabelType");
    }
    
    public long addChannel(final Long customerID, final String name) throws DataAccessException, Exception {
        final DataObject dao = (DataObject)new WritableDataObject();
        final Row appReleaseLabelRow = new Row("AppReleaseLabel");
        appReleaseLabelRow.set("CUSTOMER_ID", (Object)customerID);
        appReleaseLabelRow.set("RELEASE_LABEL_DISPLAY_NAME", (Object)name);
        dao.addRow(appReleaseLabelRow);
        DataAccess.add(dao);
        AppVersionDBUtil.logger.log(Level.INFO, "New channel {0} created with the release label id {1}", new Object[] { appReleaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME"), appReleaseLabelRow.get("RELEASE_LABEL_ID") });
        return (long)appReleaseLabelRow.get("RELEASE_LABEL_ID");
    }
    
    @Deprecated
    public JSONObject getAvailableReleaseLabelIDSForNewVersionOfApp(final JSONObject requestJSON) throws DataAccessException, Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final Long packageID = APIUtil.getResourceID(requestJSON, "app_id");
        final Long appGroupID = (Long)DBUtil.getValueFromDB("MdPackageToAppGroup", "PACKAGE_ID", (Object)packageID, "APP_GROUP_ID");
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject availableChannelsJSON = new JSONObject();
        final JSONArray arrayOfAvailableChannels = new JSONArray();
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupID, 0);
        sql.setCriteria(appGroupCriteria);
        sql.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
        final DerivedTable derivedTable = new DerivedTable("AppGroupToCollection", (Query)sql);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        final Join join = new Join(Table.getTable("AppReleaseLabel"), (Table)derivedTable, new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 1);
        sq.addJoin(join);
        final Criteria customerCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria nullCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)null, 0);
        sq.setCriteria(customerCriteria.and(nullCriteria));
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"), (Object)search, 12);
            sq.setCriteria(sq.getCriteria().and(searchCriteria));
        }
        sq.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        sq.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"));
        sq.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final int count = MDMDBUtil.getCount(sq, "AppReleaseLabel", "RELEASE_LABEL_ID");
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                availableChannelsJSON.put("paging", (Object)pagingJSON);
            }
            if (!selectAll) {
                sq.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("channel_name")) {
                        sq.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    sq.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE", true));
                }
            }
            final DataObject dao = DataAccess.get(sq);
            if (!dao.isEmpty()) {
                final Iterator<Row> appReleaseLabelRows = dao.getRows("AppReleaseLabel");
                while (appReleaseLabelRows.hasNext()) {
                    final Row channelDetailsRow = appReleaseLabelRows.next();
                    final JSONObject channelDetailsJSON = new JSONObject();
                    channelDetailsJSON.put("RELEASE_LABEL_TYPE", channelDetailsRow.get("RELEASE_LABEL_TYPE"));
                    channelDetailsJSON.put("RELEASE_LABEL_DISPLAY_NAME", (Object)I18N.getMsg((String)channelDetailsRow.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                    channelDetailsJSON.put("RELEASE_LABEL_ID", channelDetailsRow.get("RELEASE_LABEL_ID"));
                    arrayOfAvailableChannels.put((Object)channelDetailsJSON);
                }
            }
        }
        else {
            AppVersionDBUtil.logger.log(Level.INFO, "No free channel available for the given appGroupID{0}", new Object[] { appGroupID });
        }
        availableChannelsJSON.put("channels_available", (Object)arrayOfAvailableChannels);
        return availableChannelsJSON;
    }
    
    public JSONObject getChannels(final JSONObject requestJSON) {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray responseJSONArray = new JSONArray();
        try {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final Long customerID = APIUtil.getCustomerID(requestJSON);
            final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
            final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
            final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
            selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerID, 0));
            if (search != null) {
                selectQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"), (Object)search, 12));
            }
            final int channelCount = MDMDBUtil.getCount(selectQuery, "AppReleaseLabel", "RELEASE_LABEL_ID");
            if (channelCount != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(channelCount);
                if (pagingJSON != null) {
                    responseJSON.put("paging", (Object)pagingJSON);
                }
                if (!selectAll) {
                    selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                    if (orderByJSON != null && orderByJSON.has("orderby")) {
                        final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                        if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("channel_name")) {
                            selectQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", (boolean)isSortOrderASC));
                        }
                    }
                    else {
                        selectQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_ID", true));
                    }
                }
                final DataObject releaseChannelDao = DataAccess.get(selectQuery);
                final Iterator<Row> releaseLabelRows = releaseChannelDao.getRows("AppReleaseLabel");
                while (releaseLabelRows.hasNext()) {
                    final Row releaseLabelRow = releaseLabelRows.next();
                    final JSONObject releaseChannelDetails = new JSONObject();
                    releaseChannelDetails.put("RELEASE_LABEL_DISPLAY_NAME", (Object)I18N.getMsg((String)releaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                    releaseChannelDetails.put("RELEASE_LABEL_ID", releaseLabelRow.get("RELEASE_LABEL_ID"));
                    responseJSONArray.put((Object)releaseChannelDetails);
                }
            }
            responseJSON.put("channels_available", (Object)responseJSONArray);
        }
        catch (final Exception ex) {
            AppVersionDBUtil.logger.log(Level.SEVERE, "Exception in getChannels method", ex);
        }
        return responseJSON;
    }
    
    public JSONObject getChannelDetails(final JSONObject message) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final Long releaseLabelID = APIUtil.getResourceID(message, "label_id");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 0));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final DataObject dao = DataAccess.get(selectQuery);
        if (!dao.isEmpty()) {
            final Row row = dao.getFirstRow("AppReleaseLabel");
            responseJSON.put("RELEASE_LABEL_DISPLAY_NAME", row.get("RELEASE_LABEL_DISPLAY_NAME"));
            responseJSON.put("RELEASE_LABEL_ID", row.get("RELEASE_LABEL_ID"));
        }
        return responseJSON;
    }
    
    public void updateChannel(final Long releaseLabelID, final String labelName) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AppReleaseLabel");
        updateQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 0).and(new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)null, 0)));
        updateQuery.setUpdateColumn("RELEASE_LABEL_DISPLAY_NAME", (Object)labelName);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    public void validateAppVersionForDelete(final Long packageId, final Long releaseLabelId) throws APIHTTPException {
        try {
            final Criteria packageCriteria = new Criteria(new Column("MdPackageToAppData", "PACKAGE_ID"), (Object)packageId, 0);
            final Criteria releaseLabelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            final SelectQuery appVersionToGroup = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToGroup"));
            appVersionToGroup.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
            appVersionToGroup.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            appVersionToGroup.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            appVersionToGroup.setCriteria(packageCriteria.and(releaseLabelCriteria));
            appVersionToGroup.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
            final SelectQuery appVersionToDevice = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
            appVersionToDevice.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            appVersionToDevice.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
            appVersionToDevice.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            appVersionToDevice.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            appVersionToDevice.setCriteria(packageCriteria.and(releaseLabelCriteria).and(managedDeviceCriteria));
            appVersionToDevice.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
            final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)appVersionToDevice, (Query)appVersionToGroup, false);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)unionQuery);
            if (dmDataSetWrapper.next()) {
                throw new APIHTTPException("COM0015", new Object[] { "This particular version has active assignments, cannot be deleted" });
            }
        }
        catch (final Exception ex) {
            AppVersionDBUtil.logger.log(Level.SEVERE, "Exception in validateAppVersionForDelete", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public HashMap<Long, Set<Long>> getApprovedReleaseLabelToPackageMap(final List packageIds) throws DataAccessException {
        final HashMap labelToPackageMap = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.setCriteria(this.getApprovedAppVersionCriteria().and(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds.toArray(), 8)));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("AppGroupToCollection");
            while (iterator.hasNext()) {
                final Row appGroupToCollnRow = iterator.next();
                final Long labelId = (Long)appGroupToCollnRow.get("RELEASE_LABEL_ID");
                final Long appGroupId = (Long)appGroupToCollnRow.get("APP_GROUP_ID");
                final Long packageId = (Long)dataObject.getValue("MdPackageToAppGroup", "PACKAGE_ID", new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0));
                HashSet<Long> packageSet = labelToPackageMap.get(labelId);
                if (packageSet == null) {
                    packageSet = new HashSet<Long>();
                }
                packageSet.add(packageId);
                labelToPackageMap.put(labelId, packageSet);
            }
        }
        return labelToPackageMap;
    }
    
    public boolean isAppVersionAllowedToDelete(final HashMap appDetailsMap) throws DataAccessException {
        AppVersionDBUtil.logger.log(Level.INFO, "-------------Inside is app version sage to delete validation---------------------");
        final Integer approvedVersionStatus = appDetailsMap.get("APP_VERSION_STATUS");
        if (approvedVersionStatus != null && approvedVersionStatus.equals(AppMgmtConstants.APP_VERSION_APPROVED)) {
            AppVersionDBUtil.logger.log(Level.INFO, "App Version approved can not be deleted");
            return false;
        }
        final Long packageID = appDetailsMap.get("PACKAGE_ID");
        final Long releaseLabelId = appDetailsMap.get("RELEASE_LABEL_ID");
        final String appVersion = appDetailsMap.get("APP_VERSION");
        final String appVersionCode = appDetailsMap.get("APP_NAME_SHORT_VERSION");
        final String appIdentifier = appDetailsMap.get("IDENTIFIER");
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        final SelectQuery appVersionToGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        appVersionToGroupQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appVersionToGroupQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appVersionToGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appVersionToGroupQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final SelectQuery appVersionToDeviceQuery = (SelectQuery)appVersionToGroupQuery.clone();
        appVersionToGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppCatalogToGroup", new String[] { "APP_ID" }, new String[] { "APPROVED_APP_ID" }, 2));
        final Criteria appVersionAssociatedToGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APPROVED_APP_ID"), (Object)null, 1);
        final Criteria appVersionAssociatedToDeviceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APPROVED_APP_ID"), (Object)null, 1);
        final Criteria allCustomerAppCriteria = new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0).and(new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)appVersionCode, 0)).and(new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)appVersion, 0).and(new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)appIdentifier, 0)));
        appVersionToGroupQuery.setCriteria(packageCriteria.and(releaseLabelCriteria).or(allCustomerAppCriteria).and(appVersionAssociatedToGroupCriteria));
        appVersionToGroupQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        final DataObject dataObject = DataAccess.get(appVersionToGroupQuery);
        if (!dataObject.isEmpty()) {
            AppVersionDBUtil.logger.log(Level.INFO, "App version is associated to group hence can not be deleted");
            return false;
        }
        appVersionToDeviceQuery.addJoin(new Join("MdAppToCollection", "MdAppCatalogToResource", new String[] { "APP_ID" }, new String[] { "APPROVED_APP_ID" }, 2));
        appVersionToDeviceQuery.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        appVersionToDeviceQuery.setCriteria(packageCriteria.and(releaseLabelCriteria).or(allCustomerAppCriteria).and(appVersionAssociatedToDeviceCriteria).and(managedDeviceCriteria));
        appVersionToDeviceQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        final DataObject dataObject2 = DataAccess.get(appVersionToDeviceQuery);
        if (!dataObject2.isEmpty()) {
            AppVersionDBUtil.logger.log(Level.INFO, "App version is associated to device hence can not be deleted");
            return false;
        }
        return true;
    }
    
    public JSONObject validateIfUpgradeDowngradeAvailableForAppVersion(final Long packageId, final Long releaseLabelId) throws DataAccessException, Exception {
        Boolean isUpgradeAvailable = Boolean.FALSE;
        Boolean isDowngradeAvailable = Boolean.FALSE;
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, releaseLabelId);
        final JSONObject baseChannelAppVersion = new JSONObject();
        baseChannelAppVersion.put("APP_VERSION", appDetailsMap.get("APP_VERSION"));
        baseChannelAppVersion.put("APP_NAME_SHORT_VERSION", appDetailsMap.get("APP_NAME_SHORT_VERSION"));
        final int platformType = appDetailsMap.get("PLATFORM_TYPE");
        final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria labelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 1);
        appAllLiveVersionQuery.setCriteria(packageCriteria.and(labelCriteria));
        appAllLiveVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
        final DataObject dataObject = DataAccess.get(appAllLiveVersionQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdAppDetails");
            while (iterator.hasNext()) {
                final Row otherVersionRow = iterator.next();
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("APP_VERSION", otherVersionRow.get("APP_VERSION"));
                jsonObject.put("APP_NAME_SHORT_VERSION", otherVersionRow.get("APP_NAME_SHORT_VERSION"));
                final Boolean isGreater = AppVersionChecker.getInstance(platformType).isAppVersionGreater(jsonObject, baseChannelAppVersion);
                if (isGreater) {
                    isUpgradeAvailable = Boolean.TRUE;
                    if (platformType != 1) {
                        break;
                    }
                    if (isDowngradeAvailable) {
                        break;
                    }
                    continue;
                }
                else {
                    if (platformType != 1) {
                        continue;
                    }
                    isDowngradeAvailable = Boolean.TRUE;
                    if (isUpgradeAvailable) {
                        break;
                    }
                    continue;
                }
            }
        }
        final JSONObject response = new JSONObject();
        response.put("is_upgrade_available", (Object)isUpgradeAvailable);
        response.put("is_downgrade_available", (Object)isDowngradeAvailable);
        response.put("is_distributable", (Object)appDetailsMap.getOrDefault("is_distributable", true));
        return response;
    }
    
    public Map<Long, List<Long>> getNonProdLabelForAppGroup(final List<Long> appGroupIds, final Long customerID) {
        final Map<Long, List<Long>> appGroupToReleaseLabel = new HashMap<Long, List<Long>>();
        try {
            final Long prodLabel = getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
            final Criteria notProdCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)prodLabel, 1);
            selectQuery.setCriteria(appGroupCriteria.and(notProdCriteria));
            selectQuery.addSelectColumn(new Column("AppGroupToCollection", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("AppGroupToCollection");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                    final Long releaseLabelId = (Long)row.get("RELEASE_LABEL_ID");
                    if (!appGroupToReleaseLabel.containsKey(appGroupId)) {
                        appGroupToReleaseLabel.put(appGroupId, new ArrayList<Long>());
                    }
                    appGroupToReleaseLabel.get(appGroupId).add(releaseLabelId);
                }
            }
        }
        catch (final DataAccessException e) {
            AppVersionDBUtil.logger.log(Level.SEVERE, "Cannot fetch non prod label for app group {0}", (Throwable)e);
        }
        return appGroupToReleaseLabel;
    }
    
    static {
        AppVersionDBUtil.logger = Logger.getLogger("MDMAppMgmtLogger");
        AppVersionDBUtil.appVersionDBUtil = null;
        RELEASE_LABEL_PRODUCTION = 1;
        RELEASE_LABEL_BETA = 2;
        AppVersionDBUtil.releaseTypeToDisplayName = new HashMap<Integer, String>() {
            {
                this.put(AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, "mdm.db.appmgmt.release_label_prod");
                this.put(AppVersionDBUtil.RELEASE_LABEL_BETA, "mdm.db.appmgmt.release_label_beta");
            }
        };
    }
}
