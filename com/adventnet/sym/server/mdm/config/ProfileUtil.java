package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.mdm.server.profiles.config.ConfigHandler;
import com.me.mdm.server.profiles.config.ProfileConfigurationUtil;
import org.json.JSONArray;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import org.apache.commons.io.FilenameUtils;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.tracker.mics.MICSProfileFeatureController;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.profiles.ProfileException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import org.apache.commons.lang.StringUtils;
import com.adventnet.ds.query.SortColumn;
import java.util.LinkedHashMap;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.i18n.I18N;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Set;
import java.util.Collection;
import java.util.TreeSet;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.DerivedTable;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.core.management.ManagementConstants;
import com.adventnet.ds.query.GroupByClause;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Map;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Logger;

public class ProfileUtil
{
    private static ProfileUtil profileUtil;
    private static Logger logger;
    public static final int REMOVE_PROFILE = 0;
    public static final int INSTALL_PROFILE = 1;
    public static final List<Integer> STANDARDLICENSE_NOTAPPLICABLE_CONFIG;
    
    public static ProfileUtil getInstance() {
        if (ProfileUtil.profileUtil == null) {
            ProfileUtil.profileUtil = new ProfileUtil();
        }
        return ProfileUtil.profileUtil;
    }
    
    public int getProfileCount(final Integer platform) {
        int profileCount = -1;
        try {
            final Criteria profileTypeCri = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)1, 0);
            final Criteria DeleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            Criteria profileCri = profileTypeCri.and(DeleteCri);
            if (platform != null) {
                final Criteria platformCri = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
                profileCri = profileCri.and(platformCri);
            }
            profileCount = DBUtil.getRecordCount("Profile", "PROFILE_ID", profileCri);
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getProfileCount", ex);
        }
        return profileCount;
    }
    
    public List getOSVersionsAssignedForProfile(final Long profileId) throws DataAccessException, SyMException, QueryConstructionException, SQLException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
        final Join join = new Join("MdDeviceInfo", "RecentProfileForResource", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), 0), 2);
        query.addJoin(join);
        final Criteria criteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        query.setCriteria(criteria);
        final Column osVer = new Column("MdDeviceInfo", "OS_VERSION");
        query.addSelectColumn(osVer.distinct());
        Connection c = null;
        final List list = new ArrayList();
        DataSet ds = null;
        try {
            c = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)query, c);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    list.add(value);
                }
            }
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(c, ds);
        }
        return list;
    }
    
    public Map getGroupsAssignedForProfile(final Long profileId) throws DataAccessException, QueryConstructionException {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("RecentProfileForGroup"));
        final Join join = new Join("RecentProfileForGroup", "Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "GROUP_ID"), 0), 2);
        query.addJoin(join);
        Criteria criteria = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria cri = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        criteria = criteria.and(cri);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
        query.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
        query.addSelectColumn(new Column("Resource", "NAME"));
        query.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        final Iterator it = dataObject.getRows("Resource");
        final HashMap map = new HashMap();
        while (it.hasNext()) {
            final Row row = it.next();
            final Object value = row.get("NAME");
            final Object key = row.get("RESOURCE_ID");
            if (value != null && key != null) {
                map.put(key, value);
            }
        }
        return map;
    }
    
    public JSONObject getPolicyCountJson(final Integer platform) {
        final JSONObject policyCount = new JSONObject();
        RelationalAPI relationalAPI = null;
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            sQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Criteria cType = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)1, 0);
            if (platform != null) {
                final Criteria cPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
                sQuery.setCriteria(cPlatform.and(cType));
            }
            else {
                sQuery.setCriteria(cType);
            }
            sQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
            sQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID").count());
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("ConfigData", "CONFIG_ID");
            list.add(groupByCol);
            final GroupByClause groupBy = new GroupByClause(list);
            sQuery.setGroupByClause(groupBy);
            relationalAPI = RelationalAPI.getInstance();
            conn = relationalAPI.getConnection();
            ds = relationalAPI.executeQuery((Query)sQuery, conn);
            while (ds.next()) {
                final Object configType = ds.getValue(1);
                final Object configCount = ds.getValue(2);
                if (configType != null && configCount != null) {
                    policyCount.put(configType.toString(), configCount);
                }
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getPolicyCount", e);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return policyCount;
    }
    
    public JSONObject getModelWisePolicyCountJson(final boolean isUEM) {
        try {
            SelectQuery sQuery = this.getModelWisePolicyCountBaseQuery();
            final Criteria isTrashed = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            sQuery.setCriteria(sQuery.getCriteria().and(isTrashed));
            if (isUEM) {
                sQuery = this.getAdditionalQueryForPolicyCreatedInUEM(sQuery);
            }
            return this.getModelWisePolicyCountJson(sQuery);
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getModelWisePolicyCountJson", e);
            return new JSONObject();
        }
    }
    
    public JSONObject getModelWiseSuccessfullyDistributedPolicyCountJson(final boolean isUEM) {
        try {
            SelectQuery sQuery = this.getModelWisePolicyCountBaseQuery();
            sQuery.addJoin(new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria statusCrit = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)6, 0);
            final Criteria successCrit = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            sQuery.setCriteria(sQuery.getCriteria().and(statusCrit.and(successCrit)));
            if (isUEM) {
                sQuery = this.getAdditionalQueryForPolicyCreatedInUEM(sQuery);
            }
            return this.getModelWisePolicyCountJson(sQuery);
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getModelWiseSuccessfullyDistributedPolicyCountJson", e);
            return new JSONObject();
        }
    }
    
    private SelectQuery getAdditionalQueryForPolicyCreatedInUEM(final SelectQuery sQuery) {
        sQuery.addJoin(new Join("Profile", "ProfileToManagement", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        sQuery.addJoin(new Join("ProfileToManagement", "ManagementModel", new String[] { "MANAGEMENT_ID" }, new String[] { "MANAGEMENT_IDENTIFIER" }, 2));
        sQuery.setCriteria(sQuery.getCriteria().and(new Criteria(Column.getColumn("ManagementModel", "MANAGEMENT_IDENTIFIER"), (Object)ManagementConstants.Types.MODERN_MGMT, 0)));
        return sQuery;
    }
    
    private SelectQuery getModelWisePolicyCountBaseQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        final Criteria profileTypeCrit = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0);
        selectQuery.setCriteria(profileTypeCrit);
        selectQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_ID"));
        final Column count = Column.getColumn("ConfigData", "CONFIG_DATA_ID").count();
        count.setColumnAlias("count");
        selectQuery.addSelectColumn(count);
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        final List list = new ArrayList();
        final Column groupByCol = Column.getColumn("ConfigData", "CONFIG_ID");
        final Column groupByPlatform = Column.getColumn("Profile", "PLATFORM_TYPE");
        list.add(groupByCol);
        list.add(groupByPlatform);
        final GroupByClause groupBy = new GroupByClause(list);
        selectQuery.setGroupByClause(groupBy);
        return selectQuery;
    }
    
    private JSONObject getModelWisePolicyCountJson(final SelectQuery sQuery) throws Exception {
        final JSONObject modelWiseCount = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Object configType = ds.getValue("CONFIG_ID");
                final Object configCount = ds.getValue("count");
                final Object model = ds.getValue("PLATFORM_TYPE");
                if (configType != null && configCount != null && null != model) {
                    final JSONObject innerObj = (JSONObject)modelWiseCount.opt(model.toString());
                    modelWiseCount.put(model.toString(), (Object)((innerObj != null) ? innerObj.put(configType.toString(), configCount) : new JSONObject().put(configType.toString(), configCount)));
                }
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getModelWisePolicyCountJson", e);
            throw e;
        }
        return modelWiseCount;
    }
    
    public JSONObject getAddMorePolicyCountJson() {
        final JSONObject policyCount = new JSONObject();
        try {
            final DerivedTable addMoreConfigTable = MDMCoreQuery.getInstance().getAddMoreSupportedConfig();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl((Table)addMoreConfigTable);
            sQuery.addSelectColumn(new Column(addMoreConfigTable.getTableAlias(), "CONFIG_ID"));
            final Column profileCount = new Column(addMoreConfigTable.getTableAlias(), "PROFILE_ID").count();
            profileCount.setColumnAlias("PROFILE_COUNT");
            sQuery.addSelectColumn(profileCount);
            final Column maxConfigDataItemCount = new Column(addMoreConfigTable.getTableAlias(), "CONFIG_DATA_ITEM_COUNT").maximum();
            maxConfigDataItemCount.setColumnAlias("MAX_CONFIG_DATA_ITEM_COUNT");
            sQuery.addSelectColumn(maxConfigDataItemCount);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn(addMoreConfigTable.getTableAlias(), "CONFIG_ID");
            list.add(groupByCol);
            final GroupByClause groupBy = new GroupByClause(list);
            sQuery.setGroupByClause(groupBy);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (dataSetWrapper.next()) {
                final Object configType = dataSetWrapper.getValue("CONFIG_ID");
                final Object configCount = dataSetWrapper.getValue("PROFILE_COUNT");
                final Object configMaxCount = dataSetWrapper.getValue("MAX_CONFIG_DATA_ITEM_COUNT");
                if (configType != null) {
                    final JSONObject jsonObject = new JSONObject();
                    if (configCount != null && configMaxCount != null) {
                        jsonObject.put("PROFILE_COUNT", configCount);
                        jsonObject.put("MAX_COUNT", configMaxCount);
                    }
                    policyCount.put(configType.toString(), (Object)jsonObject);
                }
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getPolicyCount", e);
        }
        return policyCount;
    }
    
    public boolean isProfileDeleteSafe(final String profileIds) {
        boolean isProfileDeleteSafe = false;
        try {
            final String[] sArrProfileID = profileIds.split(",");
            final SelectQuery profileSafeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join profileJoin = new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            profileSafeQuery.addJoin(profileJoin);
            profileSafeQuery.addJoin(managedDeviceJoin);
            profileSafeQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            final Criteria profileIdCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)sArrProfileID, 8);
            final Criteria managedDeviceStatusCri = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
            profileSafeQuery.setCriteria(profileIdCri.and(managedDeviceStatusCri));
            final DataObject dObj = MDMUtil.getPersistence().get(profileSafeQuery);
            if (dObj.isEmpty()) {
                isProfileDeleteSafe = true;
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception in isProfileDeleteSafe...", ex);
        }
        return isProfileDeleteSafe;
    }
    
    public List<Long> getResourcesWithAssociatedProfileIds(final List profileIdList) throws DataAccessException {
        final Set<Long> uniqueResourceList = new TreeSet<Long>();
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        sql.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
        final Criteria profileIdCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileIdList.toArray(), 8, (boolean)Boolean.FALSE);
        sql.setCriteria(profileIdCriteria);
        final DataObject dao = MDMUtil.getPersistence().get(sql);
        final Iterator iter = dao.getRows("RecentProfileForResource");
        while (iter.hasNext()) {
            final Row row = iter.next();
            uniqueResourceList.add((Long)row.get("RESOURCE_ID"));
        }
        final List<Long> uniqueResList = new ArrayList<Long>(uniqueResourceList);
        return uniqueResList;
    }
    
    public List<Long> getGroupsWithAssociatedProfileIds(final List profileIdList) throws DataAccessException {
        final Set<Long> uniqueGroupList = new TreeSet<Long>();
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        sql.addSelectColumn(Column.getColumn("RecentProfileForGroup", "*"));
        final Criteria profileIdCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileIdList.toArray(), 8, (boolean)Boolean.FALSE);
        sql.setCriteria(profileIdCriteria);
        final DataObject dao = MDMUtil.getPersistence().get(sql);
        final Iterator iter = dao.getRows("RecentProfileForGroup");
        while (iter.hasNext()) {
            final Row row = iter.next();
            uniqueGroupList.add((Long)row.get("GROUP_ID"));
        }
        final List<Long> uniqueGrpList = new ArrayList<Long>(uniqueGroupList);
        return uniqueGrpList;
    }
    
    public boolean uploadProfileImageFile(final String sourceFileName, final String destination, final String fileName) {
        boolean fileUploaded = false;
        final String folderPath = destination;
        try {
            final String fileSourceName = ApiFactoryProvider.getFileAccessAPI().getFileName(sourceFileName);
            if (!MDMUtil.getInstance().validate(fileSourceName)) {
                fileUploaded = false;
                ProfileUtil.logger.log(Level.INFO, "Not a valid Image File Format");
                return fileUploaded;
            }
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(folderPath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(folderPath);
            }
            final String completeFilePath = folderPath + File.separator + fileName;
            ApiFactoryProvider.getFileAccessAPI().copyFile(sourceFileName, completeFilePath);
            final String webdir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
            if (!sourceFileName.contains(webdir)) {
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(ApiFactoryProvider.getFileAccessAPI().getParent(sourceFileName));
            }
            fileUploaded = true;
            ProfileUtil.logger.log(Level.INFO, "{0} - File uploaded successfully.", fileName);
        }
        catch (final Exception e) {
            fileUploaded = false;
            ProfileUtil.logger.log(Level.WARNING, "Exception while uploading Webclips File", e);
        }
        return fileUploaded;
    }
    
    public static String getAndroidWallpaperFolderPath(final Long collectionID) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String kioskWallPaperImagePath = webappsDir + File.separator + "mdm" + File.separator + "wallpaper" + File.separator + collectionID;
        return kioskWallPaperImagePath;
    }
    
    public static String getAndroidWallpaperDBPath(final Long collectionID) throws Exception {
        return File.separator + "mdm" + File.separator + "wallpaper" + File.separator + collectionID;
    }
    
    public static String getCustomProfileFolderPath(final Long id, final Long customerID) {
        final String profilePath = getInstance().getProfilePathWithParentDir(customerID, "customprofiles") + File.separator + id;
        return profilePath;
    }
    
    public static String getCustomProfileDBPath(final Long id, final Long customerId) {
        return getInstance().getProfileRepoRelativeFolderPath(customerId) + File.separator + "customprofiles" + File.separator + id;
    }
    
    private static String getCustomProfilePath(final Long id) {
        return File.separator + "mdm" + File.separator + "customprofiles" + File.separator + id;
    }
    
    public static String getFontFolderPath(final Long id, final Long customerID) {
        final String webappsDir = DCMetaDataUtil.getInstance().getClientDataDir(customerID);
        final String profilePath = webappsDir + getFontPath(id);
        return profilePath;
    }
    
    public static String getFontDBPath(final Long id, final Long customerId) {
        final String customProfileDBPath = DCMetaDataUtil.getInstance().getClientDataDirRelative(customerId);
        return customProfileDBPath + getFontPath(id);
    }
    
    private static String getFontPath(final Long id) {
        return File.separator + "mdm" + File.separator + "fonts" + File.separator + id;
    }
    
    public static String getProfileWebClipsFolderPath() throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appCatalogWebClipsImagePath = webappsDir + File.separator + "mdm" + File.separator + "webclips";
        return appCatalogWebClipsImagePath;
    }
    
    public static String getProfileWebClipsFolderPath(final Long collectionID) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appCatalogWebClipsImagePath = webappsDir + File.separator + "mdm" + File.separator + "webclips" + File.separator + collectionID;
        return appCatalogWebClipsImagePath;
    }
    
    public static String getProfileWebClipsRelativeFolderPath() throws Exception {
        final String webclipsPath = File.separator + "mdm" + File.separator + "webclips" + File.separator;
        return webclipsPath;
    }
    
    public static String getProfileWebClipsDBPath(final Long collectionID) throws Exception {
        return "" + collectionID;
    }
    
    public static String getProfileEmailAccountIconFolderPath() throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appCatalogWebClipsImagePath = webappsDir + File.separator + "mdm" + File.separator + "emailAccountIcon";
        return appCatalogWebClipsImagePath;
    }
    
    public static String getProfileExchangeActiveSyncAccountIconFolderPath() throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appCatalogWebClipsImagePath = webappsDir + File.separator + "mdm" + File.separator + "windows" + File.separator + "exchangeActiveSyncAccountIcon";
        return appCatalogWebClipsImagePath;
    }
    
    private Properties getGrouptoProfileHistoryDetails(final Long profileId, final Long groupId) throws DataAccessException, Exception {
        final Properties prop = new Properties();
        final Criteria cProfile = new Criteria(new Column("GroupToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria cGroupId = new Criteria(new Column("GroupToProfileHistory", "GROUP_ID"), (Object)groupId, 0);
        final SelectQuery groupProfile = (SelectQuery)new SelectQueryImpl(new Table("GroupToProfileHistory"));
        groupProfile.addJoin(new Join("GroupToProfileHistory", "RecentProfileForGroup", new String[] { "GROUP_ID", "COLLECTION_ID" }, new String[] { "GROUP_ID", "COLLECTION_ID" }, 2));
        groupProfile.setCriteria(cProfile.and(cGroupId));
        groupProfile.addSelectColumn(new Column("GroupToProfileHistory", "ASSOCIATED_BY"));
        groupProfile.addSelectColumn(new Column("GroupToProfileHistory", "ASSOCIATED_TIME"));
        groupProfile.addSelectColumn(new Column("GroupToProfileHistory", "COLLECTION_STATUS"));
        groupProfile.addSelectColumn(new Column("GroupToProfileHistory", "GROUP_HISTORY_ID"));
        groupProfile.addSelectColumn(new Column("GroupToProfileHistory", "PROFILE_ID"));
        groupProfile.addSelectColumn(new Column("GroupToProfileHistory", "COLLECTION_ID"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(groupProfile);
        final Row row = DO.getRow("GroupToProfileHistory");
        final Long assignedBy = (Long)row.get("ASSOCIATED_BY");
        final Long assignedAtLong = (Long)row.get("ASSOCIATED_TIME");
        final int status = (int)row.get("COLLECTION_STATUS");
        final String name = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)assignedBy, "FIRST_NAME");
        ((Hashtable<String, String>)prop).put("assignedBy", name);
        final String assignedAt = DateTimeUtil.longdateToString((long)assignedAtLong, "MMM d, yyyy hh:mm aaa");
        ((Hashtable<String, String>)prop).put("assignedAt", assignedAt);
        ((Hashtable<String, Object>)prop).put("status", DBUtil.getValueFromDB("ConfigStatusDefn", "STATUS_ID", (Object)status, "LABEL"));
        ((Hashtable<String, Object>)prop).put("statusImg", DBUtil.getValueFromDB("ConfigStatusDefn", "STATUS_ID", (Object)status, "IMAGE_NAME"));
        return prop;
    }
    
    private Properties getDevicetoProfileHistoryDetails(final Long profileId, final Long deviceId) throws DataAccessException, Exception {
        final Properties prop = new Properties();
        final Criteria cProfile = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria cGroupId = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)deviceId, 0);
        final DataObject DO = MDMUtil.getPersistence().get("ResourceToProfileHistory", cProfile.and(cGroupId));
        final Row row = DO.getFirstRow("ResourceToProfileHistory");
        final Long assignedBy = (Long)row.get("ASSOCIATED_BY");
        final Long assignedAtLong = (Long)row.get("ASSOCIATED_TIME");
        final String name = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)assignedBy, "FIRST_NAME");
        ((Hashtable<String, String>)prop).put("assignedBy", name);
        final String assignedAt = DateTimeUtil.longdateToString((long)assignedAtLong, "MMM d, yyyy hh:mm aaa");
        ((Hashtable<String, String>)prop).put("assignedAt", assignedAt);
        return prop;
    }
    
    private Properties getDevicetoRecentProfileDetails(final Long profileId, final Long deviceId) throws DataAccessException, Exception {
        final Properties prop = new Properties();
        final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToProfileHistory"));
        final Criteria cProfile = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria cGroupId = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)deviceId, 0);
        final Join recentProfileTocllnJoin = new Join("ResourceToProfileHistory", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        profileQuery.addJoin(recentProfileTocllnJoin);
        profileQuery.setCriteria(cProfile.and(cGroupId));
        profileQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_BY"));
        profileQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
        profileQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_TIME"));
        profileQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "COLLECTION_ID"));
        final DataObject DO = MDMUtil.getPersistence().get(profileQuery);
        final Row row = DO.getFirstRow("ResourceToProfileHistory");
        final Long assignedBy = (Long)row.get("ASSOCIATED_BY");
        final Long assignedAtLong = (Long)row.get("ASSOCIATED_TIME");
        final Long updatedCollectionId = (Long)row.get("COLLECTION_ID");
        final String name = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)assignedBy, "FIRST_NAME");
        ((Hashtable<String, String>)prop).put("lastAssignedBy", name);
        final String assignedAt = DateTimeUtil.longdateToString((long)assignedAtLong, "MMM d, yyyy hh:mm aaa");
        ((Hashtable<String, String>)prop).put("lastAssignedAt", assignedAt);
        ((Hashtable<String, Long>)prop).put("updatedCollectionId", updatedCollectionId);
        return prop;
    }
    
    private SelectQuery getAppPackageDataQuery(final List<Long> collectionIds) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        sQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        sQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 1));
        final Criteria cCollection = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(new Long[0]), 8);
        sQuery.setCriteria(cCollection);
        return sQuery;
    }
    
    private Properties getAppCategory(final Long collectionId, final int platform) throws DataAccessException {
        final Properties prop = new Properties();
        try {
            final SelectQuery sQuery = this.getAppPackageDataQuery(Arrays.asList(collectionId));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            sQuery.addSelectColumn(Column.getColumn("AppCategory", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdPackage", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            final Row appRow = DO.getFirstRow("AppCategory");
            final Row apppackRow = DO.getFirstRow("MdPackageToAppGroup");
            ((Hashtable<String, Object>)prop).put("appCategory", appRow.get("APP_CATEGORY_NAME"));
            final int appType = (int)apppackRow.get("PACKAGE_TYPE");
            if (platform == 1 && (appType == 0 || appType == 1)) {
                ((Hashtable<String, String>)prop).put("appType", I18N.getMsg("dc.mdm.actionlog.appmgmt.appStoreApp", new Object[0]));
            }
            else if (platform == 1 && appType == 2) {
                ((Hashtable<String, String>)prop).put("appType", I18N.getMsg("dc.mdm.actionlog.appmgmt.enterpriseApp", new Object[0]));
            }
            else if (platform == 2 && (appType == 0 || appType == 1)) {
                ((Hashtable<String, String>)prop).put("appType", I18N.getMsg("dc.mdm.actionlog.appmgmt.playStoreApp", new Object[0]));
            }
            else if (platform == 2 && appType == 2) {
                ((Hashtable<String, String>)prop).put("appType", I18N.getMsg("dc.mdm.actionlog.appmgmt.android_enterpriseApp", new Object[0]));
            }
            else if (platform == 3 && (appType == 0 || appType == 1)) {
                ((Hashtable<String, String>)prop).put("appType", I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_businessStoreApp", new Object[0]));
            }
            else if (platform == 3 && appType == 2) {
                ((Hashtable<String, String>)prop).put("appType", I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_enterpriseApp", new Object[0]));
            }
            final Boolean isPaid = (Boolean)apppackRow.get("IS_PAID_APP");
            ((Hashtable<String, Object>)prop).put("appGroupID", apppackRow.get("APP_GROUP_ID"));
            if (isPaid) {
                ((Hashtable<String, String>)prop).put("licenseType", I18N.getMsg("dc.inv.common.Paid", new Object[0]));
            }
            else {
                ((Hashtable<String, String>)prop).put("licenseType", I18N.getMsg("dc.inv.common.Free", new Object[0]));
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occoured in getAppCategory....", ex);
        }
        return prop;
    }
    
    private SelectQuery getQueryforProfileColln(final Long customerId) {
        final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Join profileVerJoin = new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profileCollnJoin = new Join("RecentPubProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join profileToCustJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        profileQuery.addJoin(profileVerJoin);
        profileQuery.addJoin(profileCollnJoin);
        profileQuery.addJoin(profileToCustJoin);
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "SCOPE"));
        profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        if (customerId != null) {
            final Criteria customerIdCri = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            profileQuery.setCriteria(customerIdCri);
        }
        return profileQuery;
    }
    
    public SelectQuery getQueryforAppColln(final Long customerId) {
        final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Join profileVerJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join appGroupToCollectionJoin = new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join appReleaseLabelJoin = AppVersionDBUtil.getInstance().getAppReleaseLabelJoin();
        final Join profileToCustJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        profileQuery.addJoin(profileVerJoin);
        profileQuery.addJoin(profileToCustJoin);
        profileQuery.addJoin(appGroupToCollectionJoin);
        profileQuery.addJoin(appReleaseLabelJoin);
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        profileQuery.addSelectColumn(Column.getColumn("Profile", "SCOPE"));
        profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        profileQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        profileQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        profileQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"));
        profileQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        if (customerId != null) {
            final Criteria customerIdCri = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            profileQuery.setCriteria(customerIdCri);
        }
        return profileQuery;
    }
    
    public SelectQuery getQueryforProfileCollnDevice(final Long deviceResId, final Long customerId) {
        SelectQuery profileQuery = null;
        try {
            profileQuery = this.getQueryforProfileColln(customerId);
            if (deviceResId != null) {
                final int devicePlatform = ManagedDeviceHandler.getInstance().getPlatformType(deviceResId);
                final Join profileforDeviceJoin = new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                final Join recentProfileJoin = new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentProfileForResource", "ProfileColln", 2);
                profileQuery.addJoin(profileforDeviceJoin);
                profileQuery.addJoin(recentProfileJoin);
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "ProfileColln.COLLECTION_ID"));
                final Criteria deviceResCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceResId, 0);
                final Criteria devicePlatCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)devicePlatform, 0);
                profileQuery.setCriteria(deviceResCri.and(devicePlatCri));
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occured in getQueryforProfileCollnDevice: {0}", ex);
        }
        return profileQuery;
    }
    
    public SelectQuery getQueryforAppCollnDevice(final Long deviceResId, final Long customerId) {
        SelectQuery profileQuery = null;
        try {
            profileQuery = this.getQueryforAppColln(customerId);
            if (deviceResId != null) {
                final int devicePlatform = ManagedDeviceHandler.getInstance().getPlatformType(deviceResId);
                final Join profileforDeviceJoin = new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                final Join recentProfileJoin = new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentProfileForResource", "ProfileColln", 2);
                profileQuery.addJoin(profileforDeviceJoin);
                profileQuery.addJoin(recentProfileJoin);
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "ProfileColln.COLLECTION_ID"));
                final Criteria deviceResCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceResId, 0);
                final Criteria devicePlatCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)devicePlatform, 0);
                profileQuery.setCriteria(deviceResCri.and(devicePlatCri));
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occured in getQueryforProfileCollnDevice: {0}", ex);
        }
        return profileQuery;
    }
    
    public HashMap getGroupProfileDetailsMap(final Long profileId, final Long groupId, final Boolean isApp) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        HashMap profileMap = null;
        try {
            SelectQuery profileQuery;
            if (isApp) {
                profileQuery = this.getQueryforAppCollnGroup(groupId, null);
            }
            else {
                profileQuery = this.getQueryforProfileCollnGroup(groupId, null);
            }
            final Join createUserJoin = new Join("GroupToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, "GroupToProfileHistory", "CreatedUser", 1);
            final Join modifiedUserJoin = new Join("GroupToProfileHistory", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "GroupToProfileHistory", "ModifiedUser", 1);
            profileQuery.addJoin(createUserJoin);
            profileQuery.addJoin(modifiedUserJoin);
            profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
            profileQuery.addSelectColumn(Column.getColumn("CreatedUser", "FIRST_NAME", "CreatedUser.FIRST_NAME"));
            profileQuery.addSelectColumn(Column.getColumn("ModifiedUser", "FIRST_NAME", "ModifiedUser.FIRST_NAME"));
            Criteria profileCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria groupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
            profileCri = profileCri.and(groupCri);
            profileQuery.setCriteria(profileCri);
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)profileQuery, conn);
            profileMap = new HashMap();
            if (ds.next()) {
                final String profileName = (String)ds.getValue("PROFILE_NAME");
                final int profileType = (int)ds.getValue("PROFILE_TYPE");
                final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                final int executionVer = (int)ds.getValue("ProfileColln.PROFILE_VERSION");
                final Long profileCollnId = (Long)ds.getValue("COLLECTION_ID");
                final int platform = (int)ds.getValue("PLATFORM_TYPE");
                final String modifiedBy = (String)ds.getValue("ModifiedUser.FIRST_NAME");
                final Long modifiedTimeLong = (Long)ds.getValue("LAST_MODIFIED_TIME");
                final String description = (String)ds.getValue("PROFILE_DESCRIPTION");
                final Integer scope = (Integer)ds.getValue("SCOPE");
                profileMap.put("profileId", profileId);
                profileMap.put("profileType", profileType);
                profileMap.put("profileName", profileName);
                profileMap.put("modifiedBy", modifiedBy);
                profileMap.put("scope", scope);
                final String modifiedTime = DateTimeUtil.longdateToString((long)modifiedTimeLong, "MMM d, yyyy hh:mm aaa");
                profileMap.put("modifiedTime", modifiedTime);
                profileMap.put("latestVer", latestVer);
                profileMap.put("executionVer", executionVer);
                profileMap.put("profileCollnId", profileCollnId);
                profileMap.put("description", description);
                profileMap.put("platform", platform);
                final Properties prop = this.getGrouptoProfileHistoryDetails(profileId, groupId);
                profileMap.put("assignedBy", ((Hashtable<K, Object>)prop).get("assignedBy"));
                profileMap.put("assignedAt", ((Hashtable<K, Object>)prop).get("assignedAt"));
                profileMap.put("status", ((Hashtable<K, Object>)prop).get("status"));
                profileMap.put("statusImg", ((Hashtable<K, Object>)prop).get("statusImg"));
                if (profileType == 2) {
                    final Properties appProp = this.getAppCategory(profileCollnId, platform);
                    profileMap.put("appGroupID", ((Hashtable<K, Object>)appProp).get("appGroupID"));
                    profileMap.put("groupId", ((Hashtable<K, Object>)appProp).get("groupId"));
                    profileMap.put("appCategory", ((Hashtable<K, Object>)appProp).get("appCategory"));
                    profileMap.put("appType", ((Hashtable<K, Object>)appProp).get("appType"));
                    profileMap.put("licenseType", ((Hashtable<K, Object>)appProp).get("licenseType"));
                }
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occoured in setProfileGroupDetails....", ex);
        }
        finally {
            MDMCustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return profileMap;
    }
    
    public HashMap getDeviceProfileDetailsMap(final Long profileId, final Long deviceId, final Boolean isApp) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        HashMap profileMap = null;
        try {
            SelectQuery profileQuery;
            if (isApp) {
                profileQuery = this.getQueryforAppCollnDevice(deviceId, null);
            }
            else {
                profileQuery = this.getQueryforProfileCollnDevice(deviceId, null);
            }
            final Join groupHistoryJoin = new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2);
            final Join createUserJoin = new Join("ResourceToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, "ResourceToProfileHistory", "CreatedUser", 2);
            final Join modifiedUserJoin = new Join("ResourceToProfileHistory", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "ResourceToProfileHistory", "ModifiedUser", 2);
            profileQuery.addJoin(groupHistoryJoin);
            profileQuery.addJoin(createUserJoin);
            profileQuery.addJoin(modifiedUserJoin);
            profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
            profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "AssociatedCollectionId"));
            profileQuery.addSelectColumn(Column.getColumn("CreatedUser", "FIRST_NAME", "CreatedUser.FIRST_NAME"));
            profileQuery.addSelectColumn(Column.getColumn("ModifiedUser", "FIRST_NAME", "ModifiedUser.FIRST_NAME"));
            profileQuery.addSelectColumn(new Column("ResourceToProfileHistory", "ASSOCIATED_TIME"));
            Criteria profileCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria groupCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceId, 0);
            profileCri = profileCri.and(groupCri);
            profileQuery.setCriteria(profileCri);
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)profileQuery, conn);
            profileMap = new HashMap();
            if (ds.next()) {
                final String profileName = (String)ds.getValue("PROFILE_NAME");
                final int profileType = (int)ds.getValue("PROFILE_TYPE");
                final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                final int executionVer = (int)ds.getValue("ProfileColln.PROFILE_VERSION");
                final Long profileCollnId = (Long)ds.getValue("COLLECTION_ID");
                final int platform = (int)ds.getValue("PLATFORM_TYPE");
                final String modifiedBy = (String)ds.getValue("ModifiedUser.FIRST_NAME");
                final Long modifiedTimeLong = (Long)ds.getValue("LAST_MODIFIED_TIME");
                final String description = (String)ds.getValue("PROFILE_DESCRIPTION");
                final Integer scope = (Integer)ds.getValue("SCOPE");
                final String assignedBy = (String)ds.getValue("CreatedUser.FIRST_NAME");
                final Long assignedAtLong = (Long)ds.getValue("ASSOCIATED_TIME");
                final Long associatedCollectionId = (Long)ds.getValue("AssociatedCollectionId");
                profileMap.put("profileId", profileId);
                profileMap.put("profileType", profileType);
                profileMap.put("profileName", profileName);
                profileMap.put("modifiedBy", modifiedBy);
                profileMap.put("scope", scope);
                final String modifiedTime = DateTimeUtil.longdateToString((long)modifiedTimeLong, "MMM d, yyyy hh:mm aaa");
                final String assignedAt = DateTimeUtil.longdateToString((long)assignedAtLong, "MMM d, yyyy hh:mm aaa");
                profileMap.put("modifiedTime", modifiedTime);
                profileMap.put("latestVer", latestVer);
                profileMap.put("executionVer", executionVer);
                profileMap.put("profileCollnId", profileCollnId);
                profileMap.put("description", description);
                profileMap.put("platform", platform);
                profileMap.put("lastAssignedBy", assignedBy);
                profileMap.put("lastAssignedAt", assignedAt);
                profileMap.put("updatedCollectionId", associatedCollectionId);
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occoured in setProfileDeviceDetails....", ex);
        }
        finally {
            MDMCustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return profileMap;
    }
    
    public SelectQuery getQueryforProfileCollnGroup(final Long groupResId, final Long customerId) {
        SelectQuery profileQuery = null;
        try {
            profileQuery = this.getQueryforProfileColln(customerId);
            if (groupResId != null) {
                final Join profileforGroupJoin = new Join("Profile", "RecentProfileForGroup", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                final Join recentProfileJoin = new Join("RecentProfileForGroup", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentProfileForGroup", "ProfileColln", 2);
                final Join groupHistoryJoin = new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "COLLECTION_ID", "GROUP_ID" }, new String[] { "COLLECTION_ID", "GROUP_ID" }, 2);
                final Join aaaUserJoin = new Join("GroupToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2);
                profileQuery.addJoin(profileforGroupJoin);
                profileQuery.addJoin(recentProfileJoin);
                profileQuery.addJoin(groupHistoryJoin);
                profileQuery.addJoin(aaaUserJoin);
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "ProfileColln.COLLECTION_ID"));
                profileQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_BY", "associatedByUser"));
                profileQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME", "associatedByUserName"));
                Criteria groupResCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupResId, 0);
                if (profileQuery.getCriteria() != null) {
                    groupResCri = profileQuery.getCriteria().and(groupResCri);
                }
                profileQuery.setCriteria(groupResCri);
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occured in getQueryforProfileCollnGroup: {0}", ex);
        }
        return profileQuery;
    }
    
    public SelectQuery getQueryforAppCollnGroup(final Long groupResId, final Long customerId) {
        SelectQuery profileQuery = null;
        try {
            if (groupResId != null) {
                profileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                final Join profileforGroupJoin = new Join("Profile", "RecentProfileForGroup", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                final Join recentProfileJoin = new Join("RecentProfileForGroup", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentProfileForGroup", "ProfileColln", 2);
                final Join groupHistoryJoin = new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "COLLECTION_ID", "GROUP_ID" }, new String[] { "COLLECTION_ID", "GROUP_ID" }, 2);
                final Join aaaUserJoin = new Join("GroupToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2);
                profileQuery.addJoin(profileforGroupJoin);
                profileQuery.addJoin(recentProfileJoin);
                profileQuery.addJoin(groupHistoryJoin);
                profileQuery.addJoin(aaaUserJoin);
                profileQuery.addJoin(new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                profileQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                final Criteria appCatalogToRecentResourceCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), 0));
                profileQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToGroup", appCatalogToRecentResourceCriteria, 2));
                profileQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "ApprovedAppColln", 2));
                profileQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "ProfileToCollection", 2));
                profileQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                profileQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "AppGroupToCollection", 2));
                profileQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "ProfileColln.COLLECTION_ID"));
                profileQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_BY", "associatedByUser"));
                profileQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME", "associatedByUserName"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
                profileQuery.addSelectColumn(Column.getColumn("Profile", "SCOPE"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
                profileQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
                profileQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
                profileQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"));
                profileQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
                final Criteria groupResCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupResId, 0);
                profileQuery.setCriteria(groupResCri);
                if (customerId != null) {
                    final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
                    profileQuery.setCriteria(profileQuery.getCriteria().and(customerCriteria));
                }
                profileQuery.setDistinct(true);
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occured in getQueryforProfileCollnGroup: {0}", ex);
        }
        return profileQuery;
    }
    
    public SelectQuery getQueryForAppCollnUser(final Long userId, final Long customerId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentProfileForMDMResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForMDMResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentProfileForMDMResource", "ProfileColln", 2));
        selectQuery.addJoin(new Join("RecentProfileForMDMResource", "MDMResourceToProfileHistory", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MDMResourceToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForMDMResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"), 0);
        final Criteria userIdJoinCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), 0);
        selectQuery.addJoin(new Join("RecentProfileForMDMResource", "MdAppCatalogToUser", appGroupCriteria.and(userIdJoinCriteria), 2));
        selectQuery.addJoin(new Join("MdAppCatalogToUser", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToUser", "ApprovedAppColln", 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "ProfileToCollection", 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppColln", "AppGroupToCollection", 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria userIdCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)userId, 0);
        if (customerId != null) {
            userIdCriteria = userIdCriteria.and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
        }
        selectQuery.setCriteria(userIdCriteria);
        selectQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "ProfileColln.COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MDMResourceToProfileHistory", "ASSOCIATED_BY", "associatedByUser"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME", "associatedByUserName"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "SCOPE"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        return selectQuery;
    }
    
    public SelectQuery getQueryforProfileCollnManagedUser(final Long userId, final Long customerId, final Integer profileType) {
        SelectQuery profileQuery = null;
        try {
            profileQuery = this.getQueryforProfileColln(customerId);
            if (userId != null) {
                final Join profileforMDMResourceJoin = new Join("Profile", "RecentProfileForMDMResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
                final Join mdmResourceManagedUserJoin = new Join("RecentProfileForMDMResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
                final Join recentProfileJoin = new Join("RecentProfileForMDMResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "RecentProfileForMDMResource", "ProfileColln", 2);
                final Join groupHistoryJoin = new Join("RecentProfileForMDMResource", "MDMResourceToProfileHistory", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2);
                final Join aaaUserJoin = new Join("RecentProfileForMDMResource", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2);
                profileQuery.addJoin(profileforMDMResourceJoin);
                profileQuery.addJoin(mdmResourceManagedUserJoin);
                profileQuery.addJoin(recentProfileJoin);
                profileQuery.addJoin(groupHistoryJoin);
                profileQuery.addJoin(aaaUserJoin);
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
                profileQuery.addSelectColumn(Column.getColumn("ProfileColln", "COLLECTION_ID", "ProfileColln.COLLECTION_ID"));
                profileQuery.addSelectColumn(Column.getColumn("MDMResourceToProfileHistory", "ASSOCIATED_BY", "associatedByUser"));
                profileQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME", "associatedByUserName"));
                final Criteria groupResCri = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)userId, 0);
                profileQuery.setCriteria(groupResCri);
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occured in getQueryforProfileCollnGroup: {0}", ex);
        }
        return profileQuery;
    }
    
    public Map getManagedDevicesAssignedForProfile(final Long profileId) throws DataAccessException, QueryConstructionException {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("RecentProfileForResource"));
        final Criteria criteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
        query.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
        query.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        final Iterator it = dataObject.getRows("RecentProfileForResource");
        final HashMap<Object, Object> map = new HashMap<Object, Object>();
        while (it.hasNext()) {
            final Row row = it.next();
            final Object value = row.get("COLLECTION_ID");
            final Object key = row.get("RESOURCE_ID");
            map.put(key, value);
        }
        return map;
    }
    
    public Map getManagedGroupsAssignedForProfile(final Long profileId) throws DataAccessException, QueryConstructionException {
        return this.getManagedGroupAssignedForProfile(new ArrayList(), profileId);
    }
    
    public LinkedHashMap<Object, Object> getManagedGroupAssignedForProfile(final List groupList, final Long profileId) throws DataAccessException, QueryConstructionException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        Criteria criteria = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        if (groupList != null && groupList.size() > 0) {
            criteria = criteria.and(new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupList.toArray(), 8));
        }
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
        query.addSelectColumn(new Column("RecentProfileForGroup", "COLLECTION_ID"));
        query.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        dataObject.sortRows("RecentProfileForGroup", new SortColumn[] { new SortColumn(new Column("RecentProfileForGroup", "COLLECTION_ID"), false) });
        final Iterator it = dataObject.getRows("RecentProfileForGroup");
        final LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        while (it.hasNext()) {
            final Row row_RECENTPROFILEFORGROUP = it.next();
            final Object value = row_RECENTPROFILEFORGROUP.get("COLLECTION_ID");
            final Object key = row_RECENTPROFILEFORGROUP.get("GROUP_ID");
            map.put(key, value);
        }
        return map;
    }
    
    public int getPlatformType(final Long profileId) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Criteria criteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        query.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        final Row profile = dataObject.getRow("Profile");
        final int platformType = (int)profile.get("PLATFORM_TYPE");
        return platformType;
    }
    
    public void disassociateResourcesForProfiles(final Long profileID, final Long customerID, final Boolean isGroup, final Map grplist, final Integer profileType) {
        final HashMap params = new HashMap();
        params.put("PROFILE_ID", profileID);
        params.put("CUSTOMER_ID", customerID);
        params.put("isGroup", isGroup);
        params.put("GrpList", grplist);
        params.put("isApp", false);
        params.put("commandName", this.getProfileCommand(profileType, 0));
        this.disassociateResourcesForProfiles(params);
    }
    
    public void disassociateResourcesForProfiles(final Long profileID, final Long customerID, final Boolean isGroup, final Map grplist, final Integer profileType, final Long userId) {
        final HashMap params = new HashMap();
        params.put("PROFILE_ID", profileID);
        params.put("CUSTOMER_ID", customerID);
        params.put("isGroup", isGroup);
        params.put("GrpList", grplist);
        params.put("isApp", false);
        params.put("commandName", this.getProfileCommand(profileType, 0));
        params.put("loggedOnUser", userId);
        this.disassociateResourcesForProfiles(params);
    }
    
    public void disassociateResourcesForProfiles(final HashMap params) {
        try {
            final Long profileID = params.get("PROFILE_ID");
            final Long customerID = params.get("CUSTOMER_ID");
            final Boolean isGroup = params.get("isGroup");
            final Map grplist = params.get("GrpList");
            final boolean isApp = params.get("isApp");
            final List profileCollectionList = new ArrayList();
            final Map<Long, Long> profileCollnMap = new HashMap<Long, Long>();
            final int platformType = this.getPlatformType(profileID);
            final List resourceList = new ArrayList(grplist.keySet());
            final List collIDList = new ArrayList(grplist.values());
            final Boolean isAppConfig = isApp;
            final Properties colln_props = new Properties();
            final Properties properties = new Properties();
            for (final Object collId : collIDList) {
                profileCollnMap.put(profileID, (Long)collId);
                ((Hashtable<String, Long>)colln_props).put("PROFILE_ID", profileID);
                ((Hashtable<String, Object>)colln_props).put("COLLECTION_ID", collId);
                profileCollectionList.add(colln_props);
            }
            ((Hashtable<String, List>)properties).put("profileCollectionList", profileCollectionList);
            ((Hashtable<String, Map<Long, Long>>)properties).put("profileCollnMap", profileCollnMap);
            ((Hashtable<String, Map<Long, Long>>)properties).put("profileCollectionMap", profileCollnMap);
            ((Hashtable<String, Object>)properties).put("commandName", params.get("commandName"));
            ((Hashtable<String, List>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Integer>)properties).put("platformtype", platformType);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", isAppConfig);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", MDMUtil.getInstance().getLoggedInUserID());
            if (isGroup) {
                ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
            }
            else {
                ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            }
        }
        catch (final Exception exp) {
            ProfileUtil.logger.log(Level.WARNING, "Exception in disAssociation of groups and devices", exp);
        }
    }
    
    public void markAsDeleted(final List<Long> profileIDs, final Long customerId) {
        try {
            final Long currentlyLoggedInUserLoginId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            this.markAsDeleted(StringUtils.join(profileIDs.toArray(), ","), customerId, currentlyLoggedInUserLoginId);
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception while fetching userid", ex);
        }
    }
    
    public void markAsDeleted(final List<Long> profileIDs, final Long customerId, final Long userId) {
        this.markAsDeleted(StringUtils.join(profileIDs.toArray(), ","), customerId, userId);
    }
    
    private void markAsDeleted(final String profileID, final Long customerId, final Long currentlyLoggedInUserLoginId) {
        try {
            final String[] sArrProfileID = profileID.split(",");
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("Profile");
            query.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)sArrProfileID, 8).and(new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
            query.setCriteria(criteria);
            query.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)Boolean.TRUE);
            query.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            query.setUpdateColumn("LAST_MODIFIED_BY", (Object)currentlyLoggedInUserLoginId);
            MDMUtil.getPersistence().update(query);
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception in Mark the profile as Deleted", ex);
        }
    }
    
    public void moveProfilesToTrash(final String profileId, final Long customerId, final Long loginId, final Integer profileType) throws Exception {
        ProfileUtil.logger.log(Level.INFO, "Profile moved to trash.Profile Ids:{0}", new Object[] { profileId });
        final String[] sArrProfileID = profileId.split(",");
        if (!this.isCustomerEligible(customerId, Arrays.asList(sArrProfileID))) {
            ProfileUtil.logger.log(Level.INFO, "Customer Id is invalid for profile");
            throw new ProfileException();
        }
        final Long[] profileArray = MDMStringUtils.convertStringToLongArray(sArrProfileID);
        this.moveProfilesToTrash(Arrays.asList(profileArray), customerId, profileType);
        final String sUserName = DMUserHandler.getUserName(loginId);
        final ArrayList multipleRemarksArgs = MDMUtil.getInstance().getProfileDetails(profileId);
        final Iterator remarksIterator = multipleRemarksArgs.iterator();
        final List remarksArgsList = new ArrayList();
        while (remarksIterator.hasNext()) {
            final HashMap profileHash = remarksIterator.next();
            remarksArgsList.add(profileHash.get("PROFILE_NAME"));
            MICSProfileFeatureController.addTrackingData(profileHash.get("PLATFORM_TYPE"), MICSProfileFeatureController.ProfileOperation.DELETE);
        }
        if (multipleRemarksArgs.size() > 0) {
            MDMEventLogHandler.getInstance().addEvent(2021, sUserName, "mdm.profile.trash.eventlog", remarksArgsList, customerId, new Long(System.currentTimeMillis()));
        }
    }
    
    public void moveProfilesToTrash(final List<Long> profileIds, final Long customerId, final Integer profileType) throws QueryConstructionException, DataAccessException {
        this.markAsDeleted(profileIds, customerId);
        for (final Long sArrProfileID1 : profileIds) {
            try {
                Map grpList = new HashMap();
                grpList = this.getManagedGroupsAssignedForProfile(sArrProfileID1);
                if (!grpList.isEmpty()) {
                    ProfileUtil.logger.log(Level.INFO, "Disassociating the profile from group due to trash.GroupList:{0}", new Object[] { grpList });
                    this.disassociateResourcesForProfiles(sArrProfileID1, customerId, true, grpList, profileType);
                }
                grpList = this.getManagedDevicesAssignedForProfile(sArrProfileID1);
                if (grpList.isEmpty()) {
                    continue;
                }
                ProfileUtil.logger.log(Level.INFO, "Disassociating the profile from the device due to trash.DeviceList:{0}", new Object[0]);
                this.disassociateResourcesForProfiles(sArrProfileID1, customerId, false, grpList, profileType);
            }
            catch (final DataAccessException | QueryConstructionException ex) {
                ProfileUtil.logger.log(Level.SEVERE, "Exception while moving the profile to trash.", ex);
                throw ex;
            }
        }
    }
    
    public void moveProfilesToTrash(final List<Long> profileIds, final Long customerId, final Integer profileType, final Long userId) throws QueryConstructionException, DataAccessException {
        this.markAsDeleted(profileIds, customerId);
        for (final Long sArrProfileID1 : profileIds) {
            try {
                Map grpList = new HashMap();
                grpList = this.getManagedGroupsAssignedForProfile(sArrProfileID1);
                if (!grpList.isEmpty()) {
                    ProfileUtil.logger.log(Level.INFO, "Disassociating the profile from group due to trash.GroupList:{0}", new Object[] { grpList });
                    this.disassociateResourcesForProfiles(sArrProfileID1, customerId, true, grpList, profileType, userId);
                }
                grpList = this.getManagedDevicesAssignedForProfile(sArrProfileID1);
                if (grpList.isEmpty()) {
                    continue;
                }
                ProfileUtil.logger.log(Level.INFO, "Disassociating the profile from the device due to trash.DeviceList:{0}", new Object[0]);
                this.disassociateResourcesForProfiles(sArrProfileID1, customerId, false, grpList, profileType, userId);
            }
            catch (final DataAccessException | QueryConstructionException ex) {
                ProfileUtil.logger.log(Level.SEVERE, "Exception while moving the profile to trash.", ex);
                throw ex;
            }
        }
    }
    
    public boolean isClientCertificateProfile(final Long collectionID, final int configID, final List<String> allowedExtensions) throws SyMException, DataAccessException {
        boolean isClientCertificate = false;
        final Long cfgDataID = ProfileConfigHandler.getConfigDataIds(collectionID, configID).get(0);
        final List<Integer> configList = new ArrayList<Integer>();
        configList.add(configID);
        final Column col = Column.getColumn("ConfigData", "CONFIG_DATA_ID");
        final Criteria criteria = new Criteria(col, (Object)cfgDataID, 0);
        final MDMConfigQuery configQuery = new MDMConfigQuery(configList, criteria);
        final DataObject resultDO = MDMConfigQueryUtil.getConfigDataObject(configQuery);
        final Iterator iterator = resultDO.getRows("CredentialCertificateInfo");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String certificateFile = (String)row.get("CERTIFICATE_FILE_NAME");
            final String ext = FilenameUtils.getExtension(certificateFile);
            if (allowedExtensions.contains(ext)) {
                isClientCertificate = true;
            }
        }
        return isClientCertificate;
    }
    
    public JSONObject deleteProfile(final JSONObject json) throws JSONException, ProfileException {
        final JSONObject responseJSON = new JSONObject();
        try {
            final String sProfileIDs = String.valueOf(json.get("profileIDs"));
            final String[] profileIds = sProfileIDs.split(",");
            final Long customerId = json.getLong("CUSTOMER_ID");
            if (!this.isCustomerEligible(customerId, Arrays.asList(sProfileIDs.split(",")))) {
                ProfileUtil.logger.log(Level.INFO, "Customer Id is invalid for profile");
                throw new ProfileException();
            }
            if (new ProfileAssociateHandler().isProfileDeleteSafe(Arrays.asList(profileIds))) {
                final String sUserName = DMUserHandler.getDCUser(Long.valueOf(json.getLong("LOGIN_ID")));
                String sEventLogRemarks = null;
                final ArrayList multipleRemarksArgs = MDMUtil.getInstance().getProfileDetails(sProfileIDs);
                MDMUtil.getUserTransaction().begin();
                this.deleteProfileFile(Arrays.asList(profileIds));
                final Boolean successfullyDeleted = ProfileConfigHandler.deleteProfile(sProfileIDs, customerId);
                if (successfullyDeleted) {
                    ProfileUtil.logger.log(Level.INFO, "Profiles removed succeeded. Profile ID : {0}", sProfileIDs);
                    MDMUtil.getUserTransaction().commit();
                    sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.delete_success";
                    responseJSON.put("Status", (Object)"success");
                    responseJSON.put("Remarks", (Object)"dc.mdm.device_mgmt.profile_deleted_successfully");
                }
                else {
                    responseJSON.put("Status", (Object)"failure");
                    sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.delete_failure";
                    MDMUtil.getUserTransaction().rollback();
                    responseJSON.put("Remarks", (Object)"dc.mdm.device_mgmt.unable_to_delete_profile");
                }
                MDMMessageHandler.getInstance().messageAction("NO_PROFILE_ADDED", customerId);
                final Iterator remarksIterator = multipleRemarksArgs.iterator();
                final List remarksArgsList = new ArrayList();
                while (remarksIterator.hasNext()) {
                    final HashMap profileHash = remarksIterator.next();
                    remarksArgsList.add(profileHash.get("PROFILE_NAME"));
                }
                if (multipleRemarksArgs.size() > 0) {
                    MDMEventLogHandler.getInstance().addEvent(2021, sUserName, sEventLogRemarks, remarksArgsList, customerId, new Long(System.currentTimeMillis()));
                }
            }
            else {
                responseJSON.put("Status", (Object)"failure");
                responseJSON.put("Remarks", (Object)"");
            }
        }
        catch (final ProfileException e) {
            ProfileUtil.logger.log(Level.INFO, "Invalid profile exception");
            throw e;
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.SEVERE, null, ex);
            responseJSON.put("Status", (Object)"failure");
        }
        return responseJSON;
    }
    
    public static boolean hasIOSManagedSettingConfigID(final List payloadList) {
        final List temporaryList = new ArrayList(payloadList);
        final List settingsList = new ArrayList();
        settingsList.add(518);
        settingsList.add(521);
        settingsList.add(173);
        settingsList.add(951);
        settingsList.add(529);
        temporaryList.retainAll(settingsList);
        return !temporaryList.isEmpty();
    }
    
    public static boolean containsConfigIDs(final List staticList, final List payloadList) {
        final List temporaryList = new ArrayList(payloadList);
        temporaryList.retainAll(staticList);
        return !temporaryList.isEmpty();
    }
    
    public List getProfileNameWithRestriction(final Long resourceId, final String tableName, final String restrictionName, final String restrictionValue) {
        final ArrayList profileNames = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            selectQuery.addJoin(new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigDataItem", tableName, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria markForDeleteFalse = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria restrictionCri = new Criteria(Column.getColumn(tableName, restrictionName), (Object)restrictionValue, 0);
            final Criteria resIdCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(markForDeleteFalse.and(restrictionCri).and(resIdCri));
            selectQuery.addSelectColumn(new Column("Profile", "*"));
            final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
            if (!dobj.isEmpty()) {
                final Iterator iter = dobj.getRows("Profile");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    final String singleProfileName = (String)row.get("PROFILE_NAME");
                    profileNames.add(singleProfileName);
                }
            }
        }
        catch (final DataAccessException dataAccessExcep) {
            ProfileUtil.logger.log(Level.SEVERE, "DataAccessException in isRestrictedInProfile", (Throwable)dataAccessExcep);
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in isRestrictedInProfile", ex);
        }
        return profileNames;
    }
    
    public static int getProfileType(final Long profileId) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Criteria criteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        query.addSelectColumn(new Column("Profile", "PROFILE_TYPE"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        final Row profile = dataObject.getRow("Profile");
        final int profileType = (int)profile.get("PROFILE_TYPE");
        return profileType;
    }
    
    public static String getProfileScopeName(final Integer type, final int platform) {
        String pType = "--";
        try {
            if (type != null) {
                pType = MDMUtil.getInstance().getPlatformName(platform);
                if (platform == 4 || platform == 2) {
                    if (type == 0) {
                        pType = pType + " " + I18N.getMsg("dc.mdm.inv.devices", new Object[0]);
                    }
                    else if (platform == 4) {
                        pType = pType + " " + I18N.getMsg("dc.common.USERS", new Object[0]);
                    }
                    else if (platform == 2) {
                        pType = " " + I18N.getMsg("dc.mdm.knox.knox_container", new Object[0]);
                    }
                }
                else if (platform == 1) {
                    pType = I18N.getMsg("dc.mdm.actionlog.appmgmt.iphones", new Object[0]) + "/" + I18N.getMsg("dc.mdm.actionlog.appmgmt.ipads", new Object[0]);
                }
                else if (platform == 6 || platform == 3) {
                    pType = pType + " " + I18N.getMsg("dc.mdm.inv.devices", new Object[0]);
                }
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception occoured in getProfileScopeName....", ex);
        }
        return pType;
    }
    
    public JSONObject getWindowsAppMSICommand(final List<Long> collectionIdList, final List<Long> commandIdList) throws SQLException, QueryConstructionException, JSONException {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery sQuery = this.getAppPackageDataQuery(collectionIdList);
        sQuery.addJoin(new Join("MdAppToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria collectionCommandCriteria = new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)commandIdList.toArray(new Long[0]), 8);
        sQuery.setCriteria(sQuery.getCriteria().and(collectionCommandCriteria));
        sQuery.addSelectColumn(Column.getColumn("MdCollectionCommand", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
        final RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relApi.getConnection();
            ds = relApi.executeQuery((Query)sQuery, conn);
            while (ds.next()) {
                final Long commandId = (Long)ds.getValue("COMMAND_ID");
                final String appFileLoc = (String)ds.getValue("APP_FILE_LOC");
                if (appFileLoc != null) {
                    jsonObject.put(String.valueOf(commandId), appFileLoc.toLowerCase().endsWith(".msi"));
                }
            }
        }
        finally {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return jsonObject;
    }
    
    public static JSONObject getCreatedUserDetailsForProfile(final Long profileId) {
        final JSONObject associatedUserDetails = new JSONObject();
        try {
            final SelectQuery associatedBySelectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            associatedBySelectQuery.addJoin(new Join("AaaUser", "Profile", new String[] { "USER_ID" }, new String[] { "CREATED_BY" }, 2));
            final Criteria profileIdCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
            associatedBySelectQuery.setCriteria(profileIdCriteria);
            associatedBySelectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(associatedBySelectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AaaUser");
                associatedUserDetails.put("USER_ID", row.get("USER_ID"));
                associatedUserDetails.put("FIRST_NAME", row.get("FIRST_NAME"));
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.SEVERE, " Exception in getCreatedUserDetailsForProfile ", ex);
        }
        return associatedUserDetails;
    }
    
    public JSONObject getAssociatedUserForProfile(final Long profileID) {
        final JSONObject associatedUserDetails = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            final Join join = new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, 2);
            selectQuery.addJoin(join);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                Row row = dataObject.getFirstRow("Profile");
                associatedUserDetails.put("UserID", row.get("LAST_MODIFIED_BY"));
                row = dataObject.getFirstRow("AaaUser");
                associatedUserDetails.put("loggedOnUserName", row.get("FIRST_NAME"));
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.WARNING, "Cannot get the associated user ", e);
        }
        return associatedUserDetails;
    }
    
    public HashMap getProfileAssociatedUserForResource(final Long resourceId, final HashMap profileCollnMap) {
        final HashMap userProfileMap = new HashMap();
        try {
            final List<Long> profileList = new ArrayList<Long>();
            final List<Long> collectionList = new ArrayList<Long>();
            final Set keySet = profileCollnMap.keySet();
            for (final Object profileId : keySet) {
                final Long collectionId = profileCollnMap.get(profileId);
                profileList.add((Long)profileId);
                collectionList.add(collectionId);
            }
            final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProfileHistory"));
            profileQuery.addJoin(new Join("ResourceToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
            final Criteria profileCriteria = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Criteria collectionCriteria = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            final Criteria resourceCriteria = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
            profileQuery.setCriteria(profileCriteria.and(collectionCriteria).and(resourceCriteria));
            profileQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
            profileQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_ID"));
            profileQuery.addSelectColumn(new Column("ResourceToProfileHistory", "PROFILE_ID"));
            profileQuery.addSelectColumn(new Column("ResourceToProfileHistory", "COLLECTION_ID"));
            profileQuery.addSelectColumn(new Column("ResourceToProfileHistory", "ASSOCIATED_BY"));
            profileQuery.addSelectColumn(new Column("AaaUser", "USER_ID"));
            profileQuery.addSelectColumn(new Column("AaaUser", "FIRST_NAME"));
            final DataObject dataObject = MDMUtil.getPersistence().get(profileQuery);
            final Iterator iterator = dataObject.getRows("ResourceToProfileHistory");
            while (iterator.hasNext()) {
                final Row profileRow = iterator.next();
                final Long profileId2 = (Long)profileRow.get("PROFILE_ID");
                final Long userId = (Long)profileRow.get("ASSOCIATED_BY");
                final Row userRow = dataObject.getRow("AaaUser", new Criteria(new Column("AaaUser", "USER_ID"), (Object)userId, 0));
                final String associatedUser = (String)userRow.get("FIRST_NAME");
                final HashMap properties = new HashMap();
                properties.put("associatedByUserName", associatedUser);
                properties.put("associatedByUser", userId);
                userProfileMap.put(profileId2, properties);
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception while getting the Profile Associated User For Resource", ex);
        }
        return userProfileMap;
    }
    
    public void modifyProfileName(final Long profileID, final String profileName) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("Profile");
                if (row != null) {
                    row.set("PROFILE_NAME", (Object)profileName);
                    dataObject.updateRow(row);
                    MDMUtil.getPersistence().update(dataObject);
                }
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "error wile modifying profile name,, ", e);
        }
    }
    
    public boolean isCustomerEligible(final Long customerId, final List profileIds) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCustomerRel"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria eligibleCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(new Column("ProfileToCustomerRel", "PROFILE_ID"), (Object)profileIds.toArray(), 8));
            selectQuery.setCriteria(eligibleCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.INFO, "Exception while checking customer eligible", ex);
        }
        return false;
    }
    
    public boolean isCustomerEligible(final Long customerId, final Long profileId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCustomerRel"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria eligibleCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(new Column("ProfileToCustomerRel", "PROFILE_ID"), (Object)profileId, 0));
            selectQuery.setCriteria(eligibleCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.INFO, "Exception while checking customer eligible", ex);
        }
        return false;
    }
    
    public Integer getProfileCountOnType(final Integer profileType, final Criteria profileCriteria, final Long customerId, final String search) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria profileTypeCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        if (profileCriteria != null) {
            profileTypeCriteria = profileTypeCriteria.and(profileCriteria);
        }
        profileTypeCriteria = profileTypeCriteria.and(new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
        final Criteria filterCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)search, 12, false);
        if (search != null) {
            profileTypeCriteria = ((profileTypeCriteria == null) ? filterCriteria : profileTypeCriteria.and(filterCriteria));
        }
        selectQuery.setCriteria(profileTypeCriteria);
        Column profileIdColumn = new Column("Profile", "PROFILE_ID");
        profileIdColumn = profileIdColumn.distinct();
        profileIdColumn = profileIdColumn.count();
        selectQuery.addSelectColumn(profileIdColumn);
        return DBUtil.getRecordCount(selectQuery);
    }
    
    public static SelectQuery getProfileToConfigIdQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        return selectQuery;
    }
    
    public JSONObject getProfileIdsFromConfig(final List<Integer> configIds, final Criteria criteria) {
        final JSONObject profileConfigCustomerJSON = new JSONObject();
        try {
            ProfileUtil.logger.log(Level.INFO, "Started getting profileids from config:{0}", configIds);
            final SelectQuery selectQuery = getProfileToConfigIdQuery();
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            Criteria configIdCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configIds.toArray(), 8);
            if (criteria != null) {
                configIdCriteria = configIdCriteria.and(criteria);
            }
            selectQuery.setCriteria(configIdCriteria);
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator customerIterator = dataObject.getRows("ProfileToCustomerRel");
                while (customerIterator.hasNext()) {
                    final Row customerRow = customerIterator.next();
                    final Long customerId = (Long)customerRow.get("CUSTOMER_ID");
                    final Long profileId = (Long)customerRow.get("PROFILE_ID");
                    JSONObject profileConfigJSON = null;
                    if (profileConfigCustomerJSON.has(customerId.toString())) {
                        profileConfigJSON = profileConfigCustomerJSON.getJSONObject(customerId.toString());
                    }
                    else {
                        profileConfigJSON = new JSONObject();
                    }
                    final JSONObject collectionJSON = new JSONObject();
                    final Iterator collectionIterator = dataObject.getRows("ProfileToCollection", new Criteria(new Column("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0));
                    while (collectionIterator.hasNext()) {
                        final Row collectionRow = collectionIterator.next();
                        final Long collectionId = (Long)collectionRow.get("COLLECTION_ID");
                        final Iterator configIterator = dataObject.getRows("ConfigData", new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
                        final JSONArray configArray = new JSONArray();
                        while (configIterator.hasNext()) {
                            final Row configIdRow = configIterator.next();
                            final Integer configId = (Integer)configIdRow.get("CONFIG_ID");
                            configArray.put((Object)configId);
                        }
                        collectionJSON.put(collectionId.toString(), (Object)configArray);
                    }
                    profileConfigJSON.put(profileId.toString(), (Object)collectionJSON);
                    profileConfigCustomerJSON.put(customerId.toString(), (Object)profileConfigJSON);
                }
            }
            ProfileUtil.logger.log(Level.INFO, "Ending the getprofile config.JSON:{0}", new Object[] { profileConfigCustomerJSON });
        }
        catch (final DataAccessException e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getting profile from config", (Throwable)e);
        }
        catch (final JSONException e2) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getting profile from config", (Throwable)e2);
        }
        return profileConfigCustomerJSON;
    }
    
    public Boolean hasKioskConfigWithAutoDistributeAppsConfigured(final Integer platform, final Long collectionId) throws DataAccessException {
        String tableName = "";
        String columnName = "AUTO_DISTRIBUTE_APPS";
        String kioskModeColName = "KIOSK_MODE";
        Integer multiAppKioskMode = -1;
        Integer[] configIdArr = null;
        final SelectQuery kioskQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Collection"));
        kioskQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        kioskQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        kioskQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        if (platform.equals(1) || platform.equals(6) || platform.equals(7)) {
            kioskQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            kioskQuery.addSelectColumn(Column.getColumn("AppLockPolicy", "CONFIG_DATA_ITEM_ID"));
            kioskQuery.addSelectColumn(Column.getColumn("AppLockPolicy", "KIOSK_MODE"));
            kioskQuery.addSelectColumn(Column.getColumn("AppLockPolicy", "AUTO_DISTRIBUTE_APPS"));
            tableName = "AppLockPolicy";
            configIdArr = new Integer[] { 183 };
            multiAppKioskMode = 2;
        }
        else if (platform.equals(2)) {
            kioskQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            kioskQuery.addSelectColumn(Column.getColumn("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"));
            kioskQuery.addSelectColumn(Column.getColumn("AndroidKioskPolicy", "KIOSK_MODE"));
            kioskQuery.addSelectColumn(Column.getColumn("AndroidKioskPolicy", "AUTO_DISTRIBUTE_APPS"));
            tableName = "AndroidKioskPolicy";
            configIdArr = new Integer[] { 557 };
            multiAppKioskMode = 1;
        }
        else if (platform.equals(3)) {
            kioskQuery.addJoin(new Join("ConfigDataItem", "WindowsKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            kioskQuery.addSelectColumn(Column.getColumn("WindowsKioskPolicy", "CONFIG_DATA_ITEM_ID"));
            kioskQuery.addJoin(new Join("ConfigDataItem", "WindowsLockdownPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            kioskQuery.addJoin(new Join("WindowsLockdownPolicy", "LockdownRules", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 1));
            kioskQuery.addJoin(new Join("LockdownRules", "WindowsLockdownConfig", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2));
            kioskQuery.addSelectColumn(Column.getColumn("WindowsLockdownConfig", "RULE_ID"));
            kioskQuery.addSelectColumn(Column.getColumn("WindowsLockdownConfig", "AUTO_DISTRIBUTE_APPS"));
            configIdArr = new Integer[] { 608, 611 };
            kioskModeColName = "";
        }
        else if (platform.equals(4)) {
            kioskQuery.addJoin(new Join("ConfigDataItem", "ChromeKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            kioskQuery.addSelectColumn(Column.getColumn("ChromeKioskPolicy", "CONFIG_DATA_ITEM_ID"));
            tableName = "ChromeKioskPolicy";
            columnName = "";
            configIdArr = new Integer[] { 705 };
            multiAppKioskMode = 1;
        }
        final Criteria configIdsCrit = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configIdArr, 8);
        final Criteria collectionCrit = new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)collectionId, 0);
        kioskQuery.setCriteria(configIdsCrit.and(collectionCrit));
        Boolean retVal = Boolean.FALSE;
        final DataObject dao = MDMUtil.getPersistenceLite().get(kioskQuery);
        if (!dao.isEmpty()) {
            if (!MDMStringUtils.isEmpty(tableName)) {
                final Row kioskPolicyRow = dao.getFirstRow(tableName);
                if (!MDMStringUtils.isEmpty(columnName)) {
                    final Boolean autoDistributeApps = (Boolean)kioskPolicyRow.get(columnName);
                    final Integer kioskMode = (Integer)kioskPolicyRow.get(kioskModeColName);
                    if (kioskMode.equals(multiAppKioskMode) && autoDistributeApps) {
                        retVal = Boolean.TRUE;
                    }
                    else if (!kioskMode.equals(multiAppKioskMode)) {
                        retVal = Boolean.TRUE;
                    }
                }
                else {
                    retVal = Boolean.TRUE;
                }
            }
            else if (dao.containsTable("WindowsKioskPolicy")) {
                retVal = Boolean.TRUE;
            }
            else if (dao.containsTable("WindowsLockdownConfig")) {
                final Row windowsLockdownConfig = dao.getFirstRow("WindowsLockdownConfig");
                final Boolean autoDistributeApps = retVal = (Boolean)windowsLockdownConfig.get("AUTO_DISTRIBUTE_APPS");
            }
        }
        return retVal;
    }
    
    public String getProfileCommand(final int profiletype, final int commandType) {
        switch (profiletype) {
            case 1: {
                return (commandType == 1) ? "InstallProfile" : "RemoveProfile";
            }
            case 2: {
                return (commandType == 1) ? "InstallApplication" : "RemoveApplication";
            }
            case 8: {
                return (commandType == 1) ? "InstallDataProfile" : "RemoveDataProfile";
            }
            case 10: {
                return (commandType == 1) ? "InstallApplicationConfiguration" : "RemoveApplicationConfiguration";
            }
            case 11: {
                return (commandType == 1) ? "InstallScheduleConfiguration" : "RemoveScheduleConfiguration";
            }
            case 12: {
                return (commandType == 1) ? "InstallAppUpdatePolicy" : "RemoveAppUpdatePolicy";
            }
            default: {
                return "InstallProfile";
            }
        }
    }
    
    public void updateMdmConfigStatus(final List<Long> resourceIds, final int status, final String remarks, final Criteria additioanlCriteria) throws DataAccessException, SyMException {
        final DataObject dataObject = this.getMDMConfigDO(resourceIds, additioanlCriteria);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("RecentProfileForResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long collectionId = (Long)row.get("COLLECTION_ID");
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceId, collectionId.toString(), status, remarks);
            }
        }
    }
    
    public DataObject getMDMConfigDO(final List<Long> resourceIds, final Criteria additioanlCriteria) throws DataAccessException {
        final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_ID"));
        final Criteria resourceIdCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
        if (additioanlCriteria != null) {
            selectQuery.setCriteria(resourceIdCriteria.and(additioanlCriteria));
        }
        else {
            selectQuery.setCriteria(resourceIdCriteria);
        }
        return MDMUtil.getPersistenceLite().get((SelectQuery)selectQuery);
    }
    
    public static String getProfileWebClipsRelativeFolderPath(final Long collectionId) throws Exception {
        final String webclipsPath = File.separator + "mdm" + File.separator + "webclips" + File.separator + collectionId;
        return webclipsPath;
    }
    
    public void deleteProfileFile(final List profileIds) {
        try {
            ProfileUtil.logger.log(Level.INFO, "Profile file is going to delete {0}", profileIds);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_DATA_FILE_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ConfigData", "LABEL"));
            selectQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> commandRows = dataObject.getRows("MdCommands", new Criteria(Column.getColumn("MdCommands", "COMMAND_DATA_FILE_PATH"), (Object)File.separator, 12));
            final List<String> toBeDeletedFilePaths = DBUtil.getColumnValuesAsList((Iterator)commandRows, "COMMAND_DATA_FILE_PATH");
            for (final String profileFilePath : toBeDeletedFilePaths) {
                this.deleteProfileCommandFiles(profileFilePath);
            }
            final Iterator<Row> configDataRows = dataObject.getRows("ConfigData");
            final HashMap payloadNameToConfigDataIds = new HashMap();
            while (configDataRows.hasNext()) {
                final Row configDataRow = configDataRows.next();
                final String configName = (String)configDataRow.get("LABEL");
                final Long configDataId = (Long)configDataRow.get("CONFIG_DATA_ID");
                final String payloadName = ProfileConfigurationUtil.getInstance().getPayloadName(configName);
                final boolean isFileExists = ProfileConfigurationUtil.getInstance().checkFileExists(payloadName);
                if (isFileExists) {
                    final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName);
                    configHandler.deletePayloadFile(dataObject, configDataId);
                }
                final boolean isSubPayloadExists = ProfileConfigurationUtil.getInstance().checkIfSubPayloadExists(payloadName);
                if (isSubPayloadExists) {
                    final List newConfigDataIds = new ArrayList();
                    newConfigDataIds.add(configDataId);
                    if (payloadNameToConfigDataIds.containsKey(payloadName)) {
                        final List updatedConfigDataIds = payloadNameToConfigDataIds.get(payloadName);
                        newConfigDataIds.addAll(updatedConfigDataIds);
                    }
                    payloadNameToConfigDataIds.put(payloadName, newConfigDataIds);
                }
            }
            if (!payloadNameToConfigDataIds.isEmpty()) {
                for (final String payloadName2 : payloadNameToConfigDataIds.keySet()) {
                    final List configDataIds = payloadNameToConfigDataIds.get(payloadName2);
                    final ConfigHandler configHandler2 = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName2);
                    configHandler2.deleteSubPayloadsIfPresent(configDataIds);
                }
            }
        }
        catch (final Exception ex) {
            ProfileUtil.logger.log(Level.WARNING, "Exception while deleting the Profile file", ex);
        }
    }
    
    public boolean deleteProfileFile(String profileFilePath) throws Exception {
        boolean fileDeleted = false;
        if (profileFilePath != null) {
            profileFilePath = profileFilePath.replace("/", File.separator);
            profileFilePath = (profileFilePath.startsWith(File.separator) ? profileFilePath : (File.separator + profileFilePath));
            profileFilePath = MDMMetaDataUtil.getInstance().getClientDataParentDir() + profileFilePath;
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            if (fileAccessAPI.isFile(profileFilePath)) {
                fileDeleted = fileAccessAPI.deleteFile(profileFilePath);
            }
            else if (fileAccessAPI.isDirectory(profileFilePath)) {
                fileDeleted = fileAccessAPI.deleteDirectory(profileFilePath);
            }
            else {
                ProfileUtil.logger.log(Level.WARNING, "no file exists at {0}", new Object[] { String.valueOf(profileFilePath) });
            }
        }
        return fileDeleted;
    }
    
    public boolean deleteProfileCommandFiles(String profileFilePath) throws Exception {
        boolean fileDeleted = false;
        if (profileFilePath != null) {
            profileFilePath = profileFilePath.replace("/", File.separator);
            profileFilePath = (profileFilePath.startsWith(File.separator) ? profileFilePath : (File.separator + profileFilePath));
            profileFilePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + profileFilePath;
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            if (MDMFileUtil.testForPathTraversal(profileFilePath)) {
                if (fileAccessAPI.isFile(profileFilePath)) {
                    fileDeleted = fileAccessAPI.deleteFile(profileFilePath);
                }
                else if (fileAccessAPI.isDirectory(profileFilePath)) {
                    fileDeleted = fileAccessAPI.deleteDirectory(profileFilePath);
                }
                else {
                    ProfileUtil.logger.log(Level.WARNING, "no file exists at {0}", new Object[] { String.valueOf(profileFilePath) });
                }
            }
        }
        return fileDeleted;
    }
    
    public DataObject getProfileDO(final List profileIds) throws DataAccessException {
        final Criteria criteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
        return DataAccess.get("Profile", criteria);
    }
    
    public Properties getProfileToAppDetailsMap(final List profileList) {
        final Properties profileToAppDetailsProps = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join profileToCollectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join appToCollectionjoin = new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join packagetoAppJoin = new Join("AppGroupToCollection", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            selectQuery.addJoin(profileToCollectionJoin);
            selectQuery.addJoin(appToCollectionjoin);
            selectQuery.addJoin(packagetoAppJoin);
            selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8));
            final Column appStoreColumn = new Column("MdPackageToAppData", "STORE_ID");
            final Column appGroupColumn = new Column("MdPackageToAppData", "APP_GROUP_ID");
            final Column profileColumn = new Column("Profile", "PROFILE_ID");
            selectQuery.addSelectColumn(appGroupColumn);
            selectQuery.addSelectColumn(appStoreColumn);
            selectQuery.addSelectColumn(profileColumn);
            final List groupByList = new ArrayList();
            groupByList.add(profileColumn);
            groupByList.add(appGroupColumn);
            groupByList.add(appStoreColumn);
            final GroupByClause groupByClause = new GroupByClause(groupByList);
            selectQuery.setGroupByClause(groupByClause);
            final DMDataSetWrapper profileToAppDs = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (profileToAppDs.next()) {
                final Long profileID = (Long)profileToAppDs.getValue("PROFILE_ID");
                final Properties appProps = new Properties();
                ((Hashtable<String, Object>)appProps).put("STORE_ID", profileToAppDs.getValue("STORE_ID"));
                ((Hashtable<String, Object>)appProps).put("APP_GROUP_ID", profileToAppDs.getValue("APP_GROUP_ID"));
                ((Hashtable<Long, Properties>)profileToAppDetailsProps).put(profileID, appProps);
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getProfileToAppDetailsMap", e);
        }
        return profileToAppDetailsProps;
    }
    
    public JSONObject getPlatformToProfileMap(final List profileList) {
        final JSONObject platformToProfileMap = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8));
            final DataObject profileDO = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iter = profileDO.getRows("Profile");
            while (iter.hasNext()) {
                final Row profileRow = iter.next();
                final Long profileID = (Long)profileRow.get("PROFILE_ID");
                final int platformType = (int)profileRow.get("PLATFORM_TYPE");
                final JSONArray tempArray = platformToProfileMap.optJSONArray(String.valueOf(platformType));
                List tempProfileList = null;
                if (tempArray == null) {
                    tempProfileList = new ArrayList();
                }
                else {
                    tempProfileList = tempArray.toList();
                }
                if (!tempProfileList.contains(profileID)) {
                    tempProfileList.add(profileID);
                }
                platformToProfileMap.put(String.valueOf(platformType), (Collection)tempProfileList);
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in ");
        }
        return platformToProfileMap;
    }
    
    public String getProfileRepoRelativeFolderPath(final Long customerId) {
        final String profileRepoFolderPath = "mdm" + File.separator + "profilerepository" + File.separator + customerId;
        return profileRepoFolderPath;
    }
    
    public String getProfileRepoParentDir() {
        return MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("profilerepository");
    }
    
    public String getProfilePathWithParentDir(final Long customerID, final String dirName) {
        final String profilePath = this.getProfileRepoDataDir(customerID) + File.separator + dirName.toLowerCase();
        return profilePath;
    }
    
    public String getProfileRepoDataDir(final Long customerId) {
        final String clientDataDir = this.getProfileRepoParentDir() + File.separator + customerId;
        return clientDataDir;
    }
    
    public String getMdmProfileFolderPath(final Long customerID, final String domainName, final Long collnID) {
        final String filePath = this.getProfilePath(customerID, domainName) + File.separator + collnID;
        return filePath;
    }
    
    public String getProfilePath(final Long customerID, final String dirName) {
        final String domainPath = this.getProfileRepoRelativeFolderPath(customerID) + File.separator + dirName.toLowerCase();
        return domainPath;
    }
    
    public Properties getPackageToProfileMap(final List packageList) {
        final Properties packageToProfileMap = new Properties();
        try {
            final List<List> splitPackageList = MDMUtil.getInstance().splitListIntoSubLists(packageList, 1000);
            for (final List tempPackageList : splitPackageList) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
                final Column profileColumn = Column.getColumn("Profile", "PROFILE_ID");
                final Column packageColumn = Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID");
                final Column appGroupColumn = Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID");
                selectQuery.addSelectColumn(profileColumn);
                selectQuery.addSelectColumn(packageColumn);
                selectQuery.addSelectColumn(appGroupColumn);
                final List groupByList = new ArrayList();
                groupByList.add(profileColumn);
                groupByList.add(packageColumn);
                groupByList.add(appGroupColumn);
                final GroupByClause groupByClause = new GroupByClause(groupByList);
                selectQuery.setGroupByClause(groupByClause);
                selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)tempPackageList.toArray(), 8));
                final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (dataSetWrapper.next()) {
                    final Long profileID = (Long)dataSetWrapper.getValue("PROFILE_ID");
                    final Long packageID = (Long)dataSetWrapper.getValue("PACKAGE_ID");
                    ((Hashtable<Long, Long>)packageToProfileMap).put(packageID, profileID);
                }
            }
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getPackageToProfileMap", e);
        }
        return packageToProfileMap;
    }
    
    public static DataObject getRecentProfileForResourceDO(final Criteria criteria) {
        DataObject recentProfileForResourceDO = (DataObject)new WritableDataObject();
        try {
            final SelectQuery recentProfileForResourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            recentProfileForResourceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
            recentProfileForResourceQuery.setCriteria(criteria);
            recentProfileForResourceDO = MDMUtil.getPersistence().get(recentProfileForResourceQuery);
        }
        catch (final Exception e) {
            ProfileUtil.logger.log(Level.SEVERE, "Exception in getRecentProfileForResourceDO", e);
        }
        return recentProfileForResourceDO;
    }
    
    public Map<Long, List<Long>> getProfileToDevices(final List<Long> resList, final List<Long> profileList) throws DataAccessException {
        final Map<Long, List<Long>> profileToDevices = new HashMap<Long, List<Long>>();
        final Criteria resCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        final Criteria markedForDel = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final DataObject dataObject = getRecentProfileForResourceDO(resCriteria.and(profileCriteria).and(markedForDel));
        if (dataObject != null && !dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("RecentProfileForResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long profileId = (Long)row.get("PROFILE_ID");
                final Long resID = (Long)row.get("RESOURCE_ID");
                if (!profileToDevices.containsKey(profileId)) {
                    profileToDevices.put(profileId, new ArrayList<Long>());
                }
                profileToDevices.get(profileId).add(resID);
            }
        }
        return profileToDevices;
    }
    
    static {
        ProfileUtil.profileUtil = null;
        ProfileUtil.logger = Logger.getLogger("MDMConfigLogger");
        STANDARDLICENSE_NOTAPPLICABLE_CONFIG = new ArrayList<Integer>() {
            {
                this.add(770);
                this.add(902);
                this.add(901);
                this.add(903);
            }
        };
    }
}
