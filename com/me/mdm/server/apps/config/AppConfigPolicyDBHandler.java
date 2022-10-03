package com.me.mdm.server.apps.config;

import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.io.InputStream;
import java.io.IOException;
import com.me.mdm.files.FileFacade;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.util.Properties;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.adventnet.sym.server.mdm.util.MDMAgentBuildVersionsUtil;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppConfigPolicyDBHandler
{
    public static AppConfigPolicyDBHandler appConfigPolicyDBHandler;
    protected Logger logger;
    private static JSONObject oemApps;
    DownloadManager downloadManager;
    
    public AppConfigPolicyDBHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.downloadManager = null;
    }
    
    public static AppConfigPolicyDBHandler getInstance() {
        if (AppConfigPolicyDBHandler.appConfigPolicyDBHandler == null) {
            AppConfigPolicyDBHandler.appConfigPolicyDBHandler = new AppConfigPolicyDBHandler();
        }
        return AppConfigPolicyDBHandler.appConfigPolicyDBHandler;
    }
    
    public JSONObject getPayloadDetailsJSONFromPayloadID(final Long configDataItemID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedAppConfigurationPolicy"));
        final Criteria configDataItemIDCriteria = new Criteria(Column.getColumn("ManagedAppConfigurationPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
        selectQuery.setCriteria(configDataItemIDCriteria);
        selectQuery.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "Payload key not found" });
        }
        final Row row = dataObject.getFirstRow("ManagedAppConfigurationPolicy");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("APP_ID", row.get("APP_ID"));
        jsonObject.put("APP_GROUP_ID", row.get("APP_GROUP_ID"));
        return jsonObject;
    }
    
    public void validateAppPayloadNotAlreadyExists(final Long collectionID, final Long appGroupID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria packageCriteria = new Criteria(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)appGroupID, 0);
        selectQuery.setCriteria(collectionCriteria.and(packageCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            throw new APIHTTPException("APPCONFIGPOLICY001", new Object[0]);
        }
    }
    
    public DataObject getAppConfigProfileDetails(final Long collectionID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfigurationPolicy", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfigurationPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return dataObject;
    }
    
    public JSONObject getApplicableAppDetails(final Long collectionID) throws DataAccessException {
        final JSONObject appDetailsJSON = new JSONObject();
        final JSONArray appGroupIDs = new JSONArray();
        final DataObject dataObject = this.getAppConfigProfileDetails(collectionID);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("ManagedAppConfigurationPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                appGroupIDs.put(row.get("APP_GROUP_ID"));
            }
        }
        appDetailsJSON.put("APP_GROUP_ID", (Object)appGroupIDs);
        return appDetailsJSON;
    }
    
    public Long getProductionAppIDFromAppGroupID(final Long appGroupID, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0));
        selectQuery.setCriteria(selectQuery.getCriteria().and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria()));
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppToCollection");
            return (Long)row.get("APP_ID");
        }
        throw new APIHTTPException("COM0008", new Object[] { "App Group ID not found" });
    }
    
    public JSONObject getAppDetailsForAppGroupID(final Long appGroupID) throws Exception {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.setCriteria(new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppGroupDetails");
            response.put("GROUP_DISPLAY_NAME", row.get("GROUP_DISPLAY_NAME"));
            response.put("IDENTIFIER", row.get("IDENTIFIER"));
        }
        return response;
    }
    
    private void instantiateDownloadManager() {
        if (this.downloadManager == null) {
            try {
                this.downloadManager = new DownloadManager();
                final Class clss = this.downloadManager.getClass();
                final Method initProxyMethod = clss.getDeclaredMethod("initiateProxy", (Class[])new Class[0]);
                initProxyMethod.setAccessible(true);
                initProxyMethod.invoke(this.downloadManager, new Object[0]);
                final Method setDownloadTimeOutMethod = clss.getDeclaredMethod("setDownloadTimeout", (Class[])new Class[0]);
                setDownloadTimeOutMethod.setAccessible(true);
                setDownloadTimeOutMethod.invoke(this.downloadManager, new Object[0]);
                final Method setNetworkTypeMethod = clss.getDeclaredMethod("setNetworkType", (Class[])new Class[0]);
                setNetworkTypeMethod.setAccessible(true);
                setNetworkTypeMethod.invoke(this.downloadManager, new Object[0]);
                final Method setProxyTypeMethod = clss.getDeclaredMethod("setProxyType", (Class[])new Class[0]);
                setProxyTypeMethod.setAccessible(true);
                setProxyTypeMethod.invoke(setProxyTypeMethod, new Object[0]);
                final Field connectionTimeOutField = clss.getDeclaredField("connectionTimeOut");
                connectionTimeOutField.setAccessible(true);
                connectionTimeOutField.set(this.downloadManager, 10000);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in instantiating download manager", ex);
            }
        }
    }
    
    private void loadOEMApps(final Boolean needDownloadFromStaticServer) {
        this.logger.log(Level.INFO, "starting loading oem apps");
        InputStream inputStream = null;
        try {
            final String destinationFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDMConfiguration" + File.separator + "oem-config-apps.json";
            final String tempFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDMConfiguration" + File.separator + "temp" + File.separator + "oem-config-apps.json";
            final String sourceFile = MDMAgentBuildVersionsUtil.getMDMAgentInfo("agentstaticserverurl") + "MISC/Android/oem/oem-config-apps.json";
            if (needDownloadFromStaticServer) {
                this.instantiateDownloadManager();
                final DownloadStatus downloadStatus = this.downloadManager.downloadFile(sourceFile, tempFile, (Properties)null, true, new SSLValidationType[0]);
                if (downloadStatus.getStatus() != 0) {
                    this.logger.log(Level.WARNING, "Downloading oem identifiers from static server fails....", downloadStatus.getErrorMessage());
                }
                else {
                    final File f = new File(tempFile);
                    if (f.exists()) {
                        InputStream is = null;
                        try {
                            is = new FileInputStream(tempFile);
                            final String jsonTxt = IOUtils.toString(is, "UTF-8");
                            new JSONObject(jsonTxt);
                            FileAccessUtil.copyFileWithinServer(tempFile, destinationFile);
                            this.logger.log(Level.INFO, "completed loading oem apps from static server");
                        }
                        catch (final Exception ex) {
                            this.logger.log(Level.SEVERE, "OEM App Parsing exception hence loading the file which is cached", ex);
                        }
                        finally {
                            if (is != null) {
                                is.close();
                            }
                            if (f.exists()) {
                                new FileFacade().deleteFile(f.getParent());
                            }
                        }
                    }
                }
            }
            final File f2 = new File(destinationFile);
            if (f2.exists()) {
                inputStream = new FileInputStream(destinationFile);
                final String jsonTxt2 = IOUtils.toString(inputStream, "UTF-8");
                AppConfigPolicyDBHandler.oemApps = new JSONObject(jsonTxt2);
            }
            else {
                this.logger.log(Level.SEVERE, "oem-config-apps.json not found");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Loading oem-config-apps.json failed .....", e);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (final IOException ex2) {
                this.logger.log(Level.SEVERE, "Exception closing stream in loading oem apps", ex2);
            }
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (final IOException ex3) {
                this.logger.log(Level.SEVERE, "Exception closing stream in loading oem apps", ex3);
            }
        }
    }
    
    public List getOEMApps(final Boolean isNeedIdentifiersOnly, final Boolean needDownloadFromStaticServer) {
        this.loadOEMApps(needDownloadFromStaticServer);
        final JSONArray jsonArray = AppConfigPolicyDBHandler.oemApps.getJSONArray("oem_apps");
        if (isNeedIdentifiersOnly) {
            final List<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                list.add(jsonArray.getJSONObject(i).getString("identifier"));
            }
            return list;
        }
        return JSONUtil.convertJSONArrayToList(jsonArray);
    }
    
    public JSONObject getAppConfigFeedbackForDevice(final JSONObject requestJSON) throws Exception {
        final JSONObject feedbackJSON = new JSONObject();
        final JSONArray arrayOfFeedBacks = new JSONArray();
        final Long resourceID = APIUtil.getResourceID(requestJSON, "device_id");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppConfigurationFeedback"));
        query.addJoin(new Join("AppConfigurationFeedback", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(new Column("AppConfigurationFeedback", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(resourceCriteria);
        query.addSelectColumn(new Column((String)null, "*"));
        final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(requestJSON);
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        if (search != null) {
            final Criteria searchCriteria = new Criteria(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)search, 12);
            query.setCriteria(query.getCriteria().and(searchCriteria));
        }
        final int count = MDMDBUtil.getCount(query, "AppConfigurationFeedback", "APP_GROUP_ID");
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                feedbackJSON.put("paging", (Object)pagingJSON);
            }
            if (!selectAll) {
                query.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("app_name")) {
                        query.addSortColumn(new SortColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    query.addSortColumn(new SortColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME", true));
                }
            }
            final DataObject dataObject = DataAccess.get(query);
            final Iterator<Row> feedbacks = dataObject.getRows("AppConfigurationFeedback");
            while (feedbacks.hasNext()) {
                final Row feedback = feedbacks.next();
                String feedbackPath = String.valueOf(feedback.get("FEEDBACK_STORED_PATH"));
                feedbackPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + feedbackPath;
                final String feedbackQuery = PayloadHandler.getInstance().readProfileFromFile(feedbackPath);
                final JSONObject feedBackJSON = new JSONObject(feedbackQuery);
                final JSONObject appDetails = this.getAppDetailsForAppGroupID((Long)feedback.get("APP_GROUP_ID"));
                feedBackJSON.put("app_name", appDetails.get("GROUP_DISPLAY_NAME"));
                arrayOfFeedBacks.put((Object)feedBackJSON);
            }
            feedbackJSON.put("config_feedbacks", (Object)arrayOfFeedBacks);
        }
        return feedbackJSON;
    }
    
    public JSONObject getAppConfigFeedbackOfSpecificApp(final Long resourceID, final Long appID) throws Exception {
        JSONObject feedBackJSON = new JSONObject();
        final Criteria resourceCriteria = new Criteria(new Column("AppConfigurationFeedback", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria appCriteria = new Criteria(new Column("AppConfigurationFeedback", "APP_GROUP_ID"), (Object)appID, 0);
        final DataObject dataObject = DataAccess.get("AppConfigurationFeedback", resourceCriteria.and(appCriteria));
        if (!dataObject.isEmpty()) {
            final Row feedback = dataObject.getFirstRow("AppConfigurationFeedback");
            String feedbackPath = String.valueOf(feedback.get("FEEDBACK_STORED_PATH"));
            feedbackPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + feedbackPath;
            final String query = PayloadHandler.getInstance().readProfileFromFile(feedbackPath);
            feedBackJSON = new JSONObject(query);
            final JSONObject appDetails = this.getAppDetailsForAppGroupID((Long)feedback.get("APP_GROUP_ID"));
            feedBackJSON.put("app_name", appDetails.get("GROUP_DISPLAY_NAME"));
        }
        return feedBackJSON;
    }
    
    public void deleteAppConfigFeedback(final Long resourceID, final List appGroupIDs) {
        try {
            this.logger.log(Level.INFO, "Deleting app feedback for resource {0} from apps {1}", new Object[] { resourceID, appGroupIDs });
            final Criteria resourceCriteria = new Criteria(new Column("AppConfigurationFeedback", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria appGroupCriteria = new Criteria(new Column("AppConfigurationFeedback", "APP_GROUP_ID"), (Object)appGroupIDs.toArray(), 8);
            final Criteria finalCriteria = resourceCriteria.and(appGroupCriteria);
            final DataObject dataObject = DataAccess.get("AppConfigurationFeedback", finalCriteria);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("AppConfigurationFeedback");
                while (iterator.hasNext()) {
                    final Row feedback = iterator.next();
                    String feedbackPath = String.valueOf(feedback.get("FEEDBACK_STORED_PATH"));
                    feedbackPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + feedbackPath;
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(feedbackPath);
                }
                DataAccess.delete("AppConfigurationFeedback", finalCriteria);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deleting app feedback", ex);
        }
    }
    
    public Map<String, List<Long>> removeDevicesWithOEMProfileFromSameVendor(final List<Long> deviceIDs, final Long profileID, final Long collectionID) throws Exception {
        final List applicableDeviceList = new ArrayList(deviceIDs);
        final List notApplicableDeviceList = new ArrayList();
        final JSONObject vendors = this.getApplicableAppDetails(collectionID);
        final List appGroupIDs = JSONUtil.getInstance().convertJSONArrayTOList(vendors.getJSONArray("APP_GROUP_ID"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 1);
        final Criteria appGroupCriteria = new Criteria(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)appGroupIDs.toArray(), 8);
        selectQuery.setCriteria(profileCriteria.and(appGroupCriteria));
        selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("RecentProfileForResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long deviceID = (Long)row.get("RESOURCE_ID");
                final Boolean isRemoved = applicableDeviceList.remove(deviceID);
                if (isRemoved) {
                    notApplicableDeviceList.add(deviceID);
                }
            }
        }
        final Map<String, List<Long>> map = new HashMap<String, List<Long>>();
        map.put("modifiedDeviceList", applicableDeviceList);
        map.put("removedDeviceList", notApplicableDeviceList);
        return map;
    }
    
    public Map<String, List<Long>> removeGroupsWithOEMProfileFromSameVendor(final List groupIds, final Long profileID, final Long collectionID) throws Exception {
        final List applicableGroupList = new ArrayList(groupIds);
        final List notApplicableGroupList = new ArrayList();
        final JSONObject vendors = this.getApplicableAppDetails(collectionID);
        final List appGroupIDs = JSONUtil.getInstance().convertJSONArrayTOList(vendors.getJSONArray("APP_GROUP_ID"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria profileCriteria = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileID, 1);
        final Criteria appGroupCriteria = new Criteria(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)appGroupIDs.toArray(), 8);
        final Criteria groupCriteria = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8);
        selectQuery.setCriteria(profileCriteria.and(appGroupCriteria).and(groupCriteria));
        selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("RecentProfileForGroup");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long groupID = (Long)row.get("GROUP_ID");
                final Boolean isRemoved = applicableGroupList.remove(groupID);
                if (isRemoved) {
                    notApplicableGroupList.add(groupID);
                }
            }
        }
        final Map<String, List<Long>> map = new HashMap<String, List<Long>>();
        map.put("modifiedGroupList", applicableGroupList);
        map.put("removedGroupList", notApplicableGroupList);
        return map;
    }
    
    public Map<String, List<Long>> removeUsersWithOEMProfileFromSameVendor(final List userList, final Long profileID, final Long collectionID) throws Exception {
        final List applicableUserList = new ArrayList(userList);
        final List notApplicableUserList = new ArrayList();
        final JSONObject vendors = this.getApplicableAppDetails(collectionID);
        final List appGroupIDs = JSONUtil.getInstance().convertJSONArrayTOList(vendors.getJSONArray("APP_GROUP_ID"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForMDMResource"));
        selectQuery.addJoin(new Join("RecentProfileForMDMResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria profileCriteria = new Criteria(new Column("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileID, 1);
        final Criteria appGroupCriteria = new Criteria(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)appGroupIDs.toArray(), 8);
        final Criteria groupCriteria = new Criteria(new Column("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)userList.toArray(), 8);
        selectQuery.setCriteria(profileCriteria.and(appGroupCriteria).and(groupCriteria));
        selectQuery.addSelectColumn(new Column("RecentProfileForMDMResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("RecentProfileForMDMResource", "PROFILE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("RecentProfileForMDMResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long userID = (Long)row.get("RESOURCE_ID");
                final Boolean isRemoved = applicableUserList.remove(userID);
                if (isRemoved) {
                    notApplicableUserList.add(userID);
                }
            }
        }
        final Map<String, List<Long>> map = new HashMap<String, List<Long>>();
        map.put("modifiedUserList", applicableUserList);
        map.put("removedUserList", notApplicableUserList);
        return map;
    }
    
    public JSONArray getConfiguredVendors(final JSONObject apiRequest) throws DataAccessException {
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final JSONArray array = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfigurationPolicy", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0)));
        final Criteria oemCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)this.getOEMApps(true, false).toArray(), 8, (boolean)Boolean.TRUE);
        selectQuery.setCriteria(selectQuery.getCriteria().and(oemCriteria));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdAppGroupDetails");
            while (iterator.hasNext()) {
                final JSONObject appdetails = new JSONObject();
                final Row row = iterator.next();
                appdetails.put("APP_GROUP_ID", row.get("APP_GROUP_ID"));
                appdetails.put("app_name", row.get("GROUP_DISPLAY_NAME"));
                appdetails.put("identifier", row.get("IDENTIFIER"));
                array.put((Object)appdetails);
            }
        }
        return array;
    }
    
    public DataObject getAppConfigDO(final Long[] configItems) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppConfigPolicy"));
        selectQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfiguration", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfiguration", "AppConfigTemplate", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 2));
        selectQuery.addJoin(new Join("AppConfigTemplate", "AppConfigTemplateExtn", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 2));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configItems, 8));
        return DataAccess.get(selectQuery);
    }
    
    public JSONArray getConfiguredAppsUnderPolicies(final JSONObject apiRequest) throws Exception {
        final JSONArray appIDs = new JSONArray();
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedAppConfigurationPolicy"));
        selectQuery.addJoin(new Join("ManagedAppConfigurationPolicy", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.addSelectColumn(new Column("MdPackage", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdPackage");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                appIDs.put((Object)String.valueOf(row.get("PACKAGE_ID")));
            }
        }
        return appIDs;
    }
    
    public boolean isConfigurationApplicableForApp(final Long collectionId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "ManagedAppConfigurationPolicy", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
        selectQuery.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return dataObject.isEmpty();
    }
    
    public JSONObject verifyOEMAppIfPresent(final String identifier, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria identifierCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)Boolean.TRUE);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(identifierCriteria.and(customerCriteria));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
        selectQuery.addSelectColumn(new Column("Profile", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final JSONObject response = new JSONObject();
        if (dataObject.isEmpty()) {
            response.put("is_app_present", (Object)Boolean.FALSE);
            return response;
        }
        final Row profileRow = dataObject.getFirstRow("Profile");
        final Row appGroupRow = dataObject.getFirstRow("MdAppGroupDetails");
        final Boolean isAppInTrash = (Boolean)profileRow.get("IS_MOVED_TO_TRASH");
        if (isAppInTrash) {
            throw new APIHTTPException("COM0015", new Object[] { "App is in Trash" });
        }
        response.put("is_app_present", (Object)Boolean.TRUE);
        response.put("APP_GROUP_ID", appGroupRow.get("APP_GROUP_ID"));
        response.put("app_name", appGroupRow.get("GROUP_DISPLAY_NAME"));
        return response;
    }
    
    public JSONObject validateIfAppConfigurationInsideAppRepoApplicable(final Map appDetailsMap) throws DataAccessException {
        final JSONObject response = new JSONObject();
        Boolean isOEMApp = Boolean.FALSE;
        Boolean isConfiguredUnderProfiles = Boolean.FALSE;
        final Long appGroupId = appDetailsMap.get("APP_GROUP_ID");
        final String identifier = appDetailsMap.get("IDENTIFIER");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedAppConfigurationPolicy"));
        selectQuery.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)appGroupId, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        isConfiguredUnderProfiles = !dataObject.isEmpty();
        final List OEMApps = this.getOEMApps(true, false);
        isOEMApp = OEMApps.contains(identifier);
        response.put("is_oem_app", (Object)isOEMApp);
        response.put("is_configured_under_profiles", (Object)isConfiguredUnderProfiles);
        return response;
    }
    
    static {
        AppConfigPolicyDBHandler.appConfigPolicyDBHandler = null;
        AppConfigPolicyDBHandler.oemApps = null;
    }
}
