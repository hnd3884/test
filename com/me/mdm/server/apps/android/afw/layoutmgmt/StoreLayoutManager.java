package com.me.mdm.server.apps.android.afw.layoutmgmt;

import java.util.Hashtable;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.google.api.services.androidenterprise.model.LocalizedText;
import com.google.api.services.androidenterprise.model.StoreCluster;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.io.IOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import java.util.Properties;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class StoreLayoutManager implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    public static final Integer STORELAYOUT_STATUS_INPROGRESS;
    public static final Integer STORELAYOUT_STATUS_COMPLETED;
    public static final Integer STORELAYOUT_STATUS_FAILED;
    public static final String STORE_LAYOUT_MIGRATION_NEEDED = "storeLayoutMigrationNeeded";
    public static final String STORE_LAYOUT_MIGRATION_NOTIFICATION = "storeLayoutMigrationNotification";
    protected static Logger logger;
    
    @Deprecated
    public void publishStoreLayout(final Long customerId, final JSONObject storeLayoutJSON) throws DataAccessException, JSONException {
        final Long bsId = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW).getLong("BUSINESSSTORE_ID");
        final Long storeLayoutId = this.persistStoreLayout(bsId, storeLayoutJSON);
        this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_INPROGRESS, "dc.mdm.android.pfw.storelayout.inprogress");
        this.startLayOutSyncTask(customerId);
    }
    
    public JSONObject getStoreLayoutForBusinessStore(final Long bsId, final Long customerId) throws Exception {
        final Long storeLayoutId = (Long)DBUtil.getValueFromDB("StoreLayoutToBusinessStore", "BUSINESSSTORE_ID", (Object)bsId, "STORE_LAYOUT_ID");
        if (storeLayoutId != null) {
            return this.getStoreLayout(storeLayoutId, customerId);
        }
        return new JSONObject();
    }
    
    @Deprecated
    private void startLayOutSyncTask(final Long customerId) {
        try {
            final CommonQueueData queueItem = new CommonQueueData();
            queueItem.setCustomerId(customerId);
            queueItem.setClassName("com.me.mdm.server.apps.android.afw.layoutmgmt.StoreLayoutManager");
            queueItem.setTaskName("StoreLayoutSyncScheduler");
            queueItem.setEmptyJsonQueueData();
            CommonQueueUtil.getInstance().addToQueue(queueItem, CommonQueues.MDM_APP_MGMT);
        }
        catch (final Exception exp) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, "Exception occured while adding item to common queue", exp);
        }
    }
    
    public Long persistStoreLayout(final Long bsId, final JSONObject storeLayoutJSON) throws DataAccessException, JSONException {
        final Long storeLayoutId = this.addOrUpdateStoreLayout(storeLayoutJSON);
        this.addOrUpdateStoreLayoutToBStore(storeLayoutId, bsId);
        final List latestPageIdList = new ArrayList();
        final JSONArray pageArr = storeLayoutJSON.getJSONArray("StoreLayoutPage");
        final Long homePageId = this.addOrUpdateStoreLayoutPage(storeLayoutId, pageArr.getJSONObject(0));
        pageArr.put(0, (Object)pageArr.getJSONObject(0).put("PAGE_ID", (Object)homePageId));
        latestPageIdList.add(homePageId);
        for (int i = 1; i < pageArr.length(); ++i) {
            final JSONObject pageJSON = pageArr.getJSONObject(i);
            final Long pageId = this.addOrUpdateStoreLayoutPage(storeLayoutId, pageJSON);
            pageJSON.put("PAGE_ID", (Object)pageId);
            pageArr.put(i, (Object)pageJSON);
            latestPageIdList.add(pageId);
        }
        final Criteria storeLayoutIdCriteria = new Criteria(new Column("StoreLayoutPage", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0);
        final Criteria pageIdCriteria = new Criteria(new Column("StoreLayoutPage", "PAGE_ID"), (Object)latestPageIdList.toArray(), 9);
        DataAccess.delete("StoreLayoutPage", storeLayoutIdCriteria.and(pageIdCriteria));
        this.addOrUpdateStoreLayoutHomePage(storeLayoutId, homePageId);
        return storeLayoutId;
    }
    
    private Long addOrUpdateStoreLayout(final JSONObject storeLayoutJSON) throws DataAccessException, JSONException {
        final Long storeLayoutId = storeLayoutJSON.optLong("STORE_LAYOUT_ID", -1L);
        final Long aaaUserId = storeLayoutJSON.getLong("LAST_MODIFIED_BY");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayout"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("StoreLayout", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0));
        DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("StoreLayout");
            row.set("LAST_MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("LAST_MODIFIED_BY", (Object)aaaUserId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getFirstRow("StoreLayout");
            row.set("LAST_MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("LAST_MODIFIED_BY", (Object)aaaUserId);
            dO.updateRow(row);
        }
        dO = DataAccess.update(dO);
        return (Long)dO.getFirstValue("StoreLayout", "STORE_LAYOUT_ID");
    }
    
    private void addOrUpdateStoreLayoutToBStore(final Long storeLayoutId, final Long bsId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayoutToBusinessStore"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("StoreLayoutToBusinessStore", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("StoreLayoutToBusinessStore");
            row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
            row.set("BUSINESSSTORE_ID", (Object)bsId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("StoreLayoutToBusinessStore");
            row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
            row.set("BUSINESSSTORE_ID", (Object)bsId);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
    }
    
    private Long addOrUpdateStoreLayoutPage(final Long storeLayoutId, final JSONObject pageJSON) throws JSONException, DataAccessException {
        Long pageId = pageJSON.optLong("PAGE_ID");
        final String pageName = String.valueOf(pageJSON.get("PAGE_NAME"));
        final String storePageId = pageJSON.optString("STORE_PAGE_ID");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayoutPage"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("StoreLayoutPage", "PAGE_ID"), (Object)pageId, 0));
        DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("StoreLayoutPage");
            row.set("PAGE_NAME", (Object)pageName);
            row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
            row.set("STORE_PAGE_ID", (Object)storePageId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("StoreLayoutPage");
            row.set("PAGE_NAME", (Object)pageName);
            row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
            row.set("STORE_PAGE_ID", (Object)storePageId);
            dO.updateRow(row);
        }
        dO = DataAccess.update(dO);
        pageId = (Long)dO.getFirstValue("StoreLayoutPage", "PAGE_ID");
        if (pageJSON.has("StoreLayoutClusters")) {
            final JSONArray clusters = pageJSON.getJSONArray("StoreLayoutClusters");
            final List latestClustersIdList = new ArrayList();
            for (int i = 0; i < clusters.length(); ++i) {
                final JSONObject clusterJSON = clusters.getJSONObject(i);
                clusterJSON.put("CLUSTER_ORDER_NUMBER", i + 1);
                final Long clusterId = this.addOrUpdateStoreLayoutClusters(pageId, clusterJSON);
                latestClustersIdList.add(clusterId);
            }
            final Criteria pageIdCriteria = new Criteria(new Column("StoreLayoutClusters", "PAGE_ID"), (Object)pageId, 0);
            final Criteria clusterIdCriteria = new Criteria(new Column("StoreLayoutClusters", "CLUSTER_ID"), (Object)latestClustersIdList.toArray(), 9);
            DataAccess.delete("StoreLayoutClusters", pageIdCriteria.and(clusterIdCriteria));
        }
        return pageId;
    }
    
    private void addOrUpdateStoreLayoutHomePage(final Long storeLayoutId, final Long pageId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayoutHomePage"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("StoreLayoutHomePage", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("StoreLayoutHomePage");
            row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
            row.set("PAGE_ID", (Object)pageId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("StoreLayoutHomePage");
            row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
            row.set("PAGE_ID", (Object)pageId);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
    }
    
    private Long addOrUpdateStoreLayoutClusters(final Long pageId, final JSONObject clusterJSON) throws JSONException, DataAccessException {
        Long clusterId = clusterJSON.optLong("CLUSTER_ID");
        final String clusterName = String.valueOf(clusterJSON.get("CLUSTER_NAME"));
        final String storeClusterId = clusterJSON.optString("STORE_CLUSTER_ID");
        final Integer orderInPage = clusterJSON.getInt("CLUSTER_ORDER_NUMBER");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayoutClusters"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("StoreLayoutClusters", "CLUSTER_ID"), (Object)clusterId, 0));
        DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("StoreLayoutClusters");
            row.set("CLUSTER_NAME", (Object)clusterName);
            row.set("PAGE_ID", (Object)pageId);
            row.set("CLUSTER_ORDER_NUMBER", (Object)orderInPage);
            row.set("STORE_CLUSTER_ID", (Object)storeClusterId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("StoreLayoutClusters");
            row.set("CLUSTER_NAME", (Object)clusterName);
            row.set("PAGE_ID", (Object)pageId);
            row.set("CLUSTER_ORDER_NUMBER", (Object)orderInPage);
            row.set("STORE_CLUSTER_ID", (Object)storeClusterId);
            dO.updateRow(row);
        }
        dO = DataAccess.update(dO);
        clusterId = (Long)dO.getFirstValue("StoreLayoutClusters", "CLUSTER_ID");
        if (clusterJSON.has("StoreLayoutClusterApps")) {
            this.addOrUpdateStoreLayoutClusterApps(clusterId, clusterJSON.getJSONArray("StoreLayoutClusterApps"));
        }
        return clusterId;
    }
    
    private void addOrUpdateStoreLayoutClusterApps(final Long clusterId, final JSONArray appGrpIdArr) throws DataAccessException, JSONException {
        final Criteria clusterCriteria = new Criteria(new Column("StoreLayoutClusterApps", "CLUSTER_ID"), (Object)clusterId, 0);
        DataAccess.delete(clusterCriteria);
        final DataObject dO = (DataObject)new WritableDataObject();
        for (int i = 0; i < appGrpIdArr.length(); ++i) {
            final Row row = new Row("StoreLayoutClusterApps");
            row.set("CLUSTER_ID", (Object)clusterId);
            row.set("APP_GROUP_ID", appGrpIdArr.get(i));
            dO.addRow(row);
        }
        DataAccess.add(dO);
    }
    
    @Deprecated
    private void addOrUpdateStoreLayoutQuickLinks(final Long pageId, final JSONArray qlPageIdArr) throws DataAccessException, JSONException {
        final Criteria pageCriteria = new Criteria(new Column("StoreLayoutQuickLinks", "PAGE_ID"), (Object)pageId, 0);
        DataAccess.delete(pageCriteria);
        final DataObject dO = (DataObject)new WritableDataObject();
        for (int i = 0; i < qlPageIdArr.length(); ++i) {
            final Row row = new Row("StoreLayoutQuickLinks");
            row.set("PAGE_ID", (Object)pageId);
            row.set("QUICK_LINK_PAGE_ID", qlPageIdArr.get(i));
            dO.addRow(row);
        }
        DataAccess.add(dO);
    }
    
    public void updateStatus(final Long storeLayoutId, final Integer status, final String remarks) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayoutStatus"));
            sQuery.addSelectColumn(new Column("StoreLayoutStatus", "*"));
            sQuery.setCriteria(new Criteria(new Column("StoreLayoutStatus", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0));
            final DataObject dO = DataAccess.get(sQuery);
            if (dO.isEmpty()) {
                final Row row = new Row("StoreLayoutStatus");
                row.set("STORE_LAYOUT_ID", (Object)storeLayoutId);
                row.set("STATUS", (Object)status);
                row.set("REMARKS", (Object)remarks);
                dO.addRow(row);
            }
            else {
                final Row row = dO.getRow("StoreLayoutStatus");
                row.set("STATUS", (Object)status);
                row.set("REMARKS", (Object)remarks);
                dO.updateRow(row);
            }
            DataAccess.update(dO);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    private JSONObject getStoreLayout(final Long storeLayoutId, final Long customerId) throws DataAccessException, JSONException, Exception {
        JSONObject storeLayoutJSON = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("StoreLayout"));
        sQuery.addJoin(new Join("StoreLayout", "StoreLayoutPage", new String[] { "STORE_LAYOUT_ID" }, new String[] { "STORE_LAYOUT_ID" }, 2));
        sQuery.addJoin(new Join("StoreLayoutPage", "StoreLayoutClusters", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        sQuery.addJoin(new Join("StoreLayoutClusters", "StoreLayoutClusterApps", new String[] { "CLUSTER_ID" }, new String[] { "CLUSTER_ID" }, 1));
        sQuery.addJoin(new Join("StoreLayout", "StoreLayoutHomePage", new String[] { "STORE_LAYOUT_ID" }, new String[] { "STORE_LAYOUT_ID" }, 2));
        sQuery.addJoin(new Join("StoreLayoutPage", "StoreLayoutQuickLinks", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("StoreLayoutClusters", "CLUSTER_ORDER_NUMBER"), true);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.addSortColumn(sortColumn);
        sQuery.setCriteria(new Criteria(new Column("StoreLayout", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final JSONArray pageArr = new JSONArray();
            final Row storeLayoutRow = dO.getRow("StoreLayout");
            final Iterator pageIter = dO.getRows("StoreLayoutPage");
            while (pageIter.hasNext()) {
                final JSONObject pageJson = new JSONObject();
                final Row pageRow = pageIter.next();
                pageJson.put("PAGE_ID", pageRow.get("PAGE_ID"));
                pageJson.put("PAGE_NAME", pageRow.get("PAGE_NAME"));
                pageJson.put("STORE_PAGE_ID", pageRow.get("STORE_PAGE_ID"));
                pageJson.put("STORE_LAYOUT_ID", pageRow.get("STORE_LAYOUT_ID"));
                final JSONArray clusterArr = new JSONArray();
                final JSONArray qlArr = new JSONArray();
                final Iterator clusterIter = dO.getRows("StoreLayoutClusters", new Criteria(new Column("StoreLayoutClusters", "PAGE_ID"), pageRow.get("PAGE_ID"), 0));
                while (clusterIter.hasNext()) {
                    final JSONObject clusterJSON = new JSONObject();
                    final Row clusterRow = clusterIter.next();
                    clusterJSON.put("CLUSTER_ID", clusterRow.get("CLUSTER_ID"));
                    clusterJSON.put("CLUSTER_NAME", clusterRow.get("CLUSTER_NAME"));
                    clusterJSON.put("STORE_CLUSTER_ID", clusterRow.get("STORE_CLUSTER_ID"));
                    clusterJSON.put("CLUSTER_ORDER_NUMBER", clusterRow.get("CLUSTER_ORDER_NUMBER"));
                    final JSONArray clusterAppsArr = new JSONArray();
                    final Iterator clusterAppsIter = dO.getRows("StoreLayoutClusterApps", new Criteria(new Column("StoreLayoutClusterApps", "CLUSTER_ID"), clusterRow.get("CLUSTER_ID"), 0));
                    while (clusterAppsIter.hasNext()) {
                        final Row clusterAppsRow = clusterAppsIter.next();
                        clusterAppsArr.put(clusterAppsRow.get("APP_GROUP_ID"));
                    }
                    clusterJSON.put("StoreLayoutClusterApps", (Object)clusterAppsArr);
                    clusterArr.put((Object)clusterJSON);
                }
                final Iterator qlIter = dO.getRows("StoreLayoutQuickLinks", new Criteria(new Column("StoreLayoutQuickLinks", "PAGE_ID"), pageRow.get("PAGE_ID"), 0));
                while (qlIter.hasNext()) {
                    final Row qlRow = qlIter.next();
                    qlArr.put(qlRow.get("QUICK_LINK_PAGE_ID"));
                }
                pageJson.put("StoreLayoutClusters", (Object)clusterArr);
                pageJson.put("StoreLayoutQuickLinks", (Object)qlArr);
                pageArr.put((Object)pageJson);
            }
            storeLayoutJSON.put("StoreLayoutPage", (Object)pageArr);
            storeLayoutJSON.put("LAST_MODIFIED_BY", storeLayoutRow.get("LAST_MODIFIED_BY"));
            storeLayoutJSON.put("LAST_MODIFIED_TIME", storeLayoutRow.get("LAST_MODIFIED_TIME"));
            storeLayoutJSON.put("STORE_LAYOUT_ID", storeLayoutRow.get("STORE_LAYOUT_ID"));
        }
        storeLayoutJSON = this.reOrderHomePage(storeLayoutJSON);
        storeLayoutJSON = this.changeQuickLinksIdToIndex(storeLayoutJSON);
        return storeLayoutJSON;
    }
    
    private JSONObject reOrderHomePage(final JSONObject storeLayoutJSON) throws JSONException, Exception {
        final Long storeLayoutId = storeLayoutJSON.optLong("STORE_LAYOUT_ID", -1L);
        if (storeLayoutId != -1L && storeLayoutId != 0L) {
            final Long homePageId = (Long)DBUtil.getValueFromDB("StoreLayoutHomePage", "STORE_LAYOUT_ID", (Object)storeLayoutId, "PAGE_ID");
            final JSONArray pageArr = storeLayoutJSON.getJSONArray("StoreLayoutPage");
            final JSONArray orderedPageArr = new JSONArray();
            int i = 0;
            int j = 1;
            while (i < pageArr.length()) {
                final JSONObject pageJSON = pageArr.getJSONObject(i);
                if (homePageId.equals(pageJSON.getLong("PAGE_ID"))) {
                    orderedPageArr.put(0, (Object)pageJSON);
                }
                else {
                    orderedPageArr.put(j, (Object)pageJSON);
                    ++j;
                }
                ++i;
            }
            storeLayoutJSON.put("StoreLayoutPage", (Object)orderedPageArr);
        }
        return storeLayoutJSON;
    }
    
    @Deprecated
    private JSONObject changeQuickLinksIdToIndex(final JSONObject storeLayoutJSON) throws JSONException, Exception {
        final Long storeLayoutId = storeLayoutJSON.optLong("STORE_LAYOUT_ID", -1L);
        if (storeLayoutId != -1L && storeLayoutId != 0L) {
            final JSONArray pageArr = storeLayoutJSON.getJSONArray("StoreLayoutPage");
            final HashMap cacheMap = new HashMap();
            for (int i = 0; i < pageArr.length(); ++i) {
                cacheMap.put(pageArr.getJSONObject(i).getLong("PAGE_ID"), i);
            }
            for (int i = 0; i < pageArr.length(); ++i) {
                final JSONObject pageJSON = pageArr.getJSONObject(i);
                final JSONArray qlPageIdArr = pageJSON.optJSONArray("StoreLayoutQuickLinks");
                final JSONArray qlIndexArr = new JSONArray();
                if (qlPageIdArr != null) {
                    for (int j = 0; j < qlPageIdArr.length(); ++j) {
                        qlIndexArr.put(cacheMap.get(qlPageIdArr.getLong(j)));
                    }
                }
                pageJSON.put("StoreLayoutQuickLinks", (Object)qlIndexArr);
                pageArr.put(i, (Object)pageJSON);
            }
            storeLayoutJSON.put("StoreLayoutPage", (Object)pageArr);
        }
        return storeLayoutJSON;
    }
    
    private JSONObject getAppDisplayDetails(final Long customerId) {
        final JSONObject containerApps = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            final Criteria portalAppCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
            final Criteria platformTypeCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(portalAppCriteria.and(platformTypeCriteria).and(customerCriteria));
            try {
                final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
                final List<String> addedIdentifier = new ArrayList<String>();
                while (ds.next()) {
                    final JSONObject appData = new JSONObject();
                    final Object appGroupObj = ds.getValue("APP_GROUP_ID");
                    final Object identifierObj = ds.getValue("IDENTIFIER");
                    final Object groupDisplyNameObj = ds.getValue("GROUP_DISPLAY_NAME");
                    final Object profileNameObj = ds.getValue("PROFILE_NAME");
                    final Object displayNameObj = ds.getValue("PROFILE_NAME");
                    final String identifier = (String)identifierObj;
                    if (!addedIdentifier.contains(identifier)) {
                        final String appName = (String)((profileNameObj != null) ? profileNameObj : ((String)groupDisplyNameObj));
                        appData.put("APP_GROUP_ID", (Object)appGroupObj);
                        appData.put("IDENTIFIER", (Object)identifier);
                        appData.put("GROUP_DISPLAY_NAME", (Object)appName);
                        appData.put("DISPLAY_IMAGE_LOC", displayNameObj);
                        containerApps.put(appGroupObj + "", (Object)appData);
                        addedIdentifier.add(identifier);
                    }
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, "Exception while fetching app details from DB ", ex);
            }
        }
        catch (final Exception ex2) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, null, ex2);
        }
        return containerApps;
    }
    
    @Deprecated
    public void executeTask(final Properties props) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerId"));
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setTaskName(((Hashtable<K, String>)props).get("taskName"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, "Cannot form JSON from the props file ", (Throwable)exp);
        }
    }
    
    @Override
    public void processData(final CommonQueueData data) {
        Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.INFO, "StoreLayoutManager : Started task");
        Long storeLayoutId = null;
        boolean isPublished = false;
        final Long customerId = data.getCustomerId();
        String techName = null;
        String domainName = null;
        try {
            final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            domainName = String.valueOf(playstoreDetails.get("MANAGED_DOMAIN_NAME"));
            final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playstoreDetails);
            final JSONObject storeLayoutJSON = this.getStoreLayoutForBusinessStore(playstoreDetails.getLong("BUSINESSSTORE_ID"), customerId);
            techName = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)storeLayoutJSON.getLong("LAST_MODIFIED_BY"), "FIRST_NAME");
            final JSONArray pageArr = storeLayoutJSON.getJSONArray("StoreLayoutPage");
            storeLayoutId = storeLayoutJSON.getLong("STORE_LAYOUT_ID");
            final List listOfPages = ebs.getPages();
            if (!listOfPages.isEmpty()) {
                for (int i = 0; i < pageArr.length(); ++i) {
                    final String pageId = pageArr.getJSONObject(i).optString("STORE_PAGE_ID");
                    if (!MDMStringUtils.isEmpty(pageId)) {
                        listOfPages.remove(pageId);
                    }
                }
                if (!listOfPages.isEmpty()) {
                    ebs.deleteStoreLayoutPage(new JSONArray((Collection)listOfPages));
                }
            }
            for (int i = 0; i < pageArr.length(); ++i) {
                final JSONObject pageJSON = pageArr.getJSONObject(i);
                final String storePageId = ebs.insertPage(pageJSON);
                pageJSON.put("STORE_PAGE_ID", (Object)storePageId);
                final JSONArray clusterArr = pageJSON.optJSONArray("StoreLayoutClusters");
                final List listOfClusters = ebs.getClusters(storePageId);
                if (!listOfClusters.isEmpty()) {
                    for (int j = 0; j < clusterArr.length(); ++j) {
                        final String clusterId = clusterArr.getJSONObject(j).optString("STORE_CLUSTER_ID");
                        if (clusterId != null) {
                            listOfClusters.remove(clusterId);
                        }
                    }
                    if (!listOfClusters.isEmpty()) {
                        ebs.deleteStoreLayoutClusters(storePageId, new JSONArray((Collection)listOfClusters));
                    }
                }
                for (int j = 0; j < clusterArr.length(); ++j) {
                    final JSONObject clusterJSON = clusterArr.getJSONObject(j);
                    final JSONArray appIdentifierArr = new JSONArray();
                    final JSONArray clusterAppArr = clusterJSON.getJSONArray("StoreLayoutClusterApps");
                    for (int k = 0; k < clusterAppArr.length(); ++k) {
                        appIdentifierArr.put((Object)("app:" + DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)clusterAppArr.getLong(k), "IDENTIFIER")));
                    }
                    clusterJSON.put("apps", (Object)appIdentifierArr);
                    final String storeClusterId = ebs.insertCluster(storePageId, clusterJSON);
                    clusterJSON.put("STORE_CLUSTER_ID", (Object)storeClusterId);
                    clusterArr.put(j, (Object)clusterJSON);
                }
                pageJSON.put("StoreLayoutClusters", (Object)clusterArr);
                pageArr.put(i, (Object)pageJSON);
                this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_INPROGRESS, "dc.mdm.android.pfw.storelayout.creating_pages");
            }
            final String homePageId = String.valueOf(pageArr.getJSONObject(0).get("STORE_PAGE_ID"));
            ebs.setHomePage(homePageId);
            this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_INPROGRESS, "dc.mdm.android.pfw.storelayout.setting_homepage");
            for (int l = 0; l < pageArr.length(); ++l) {
                final JSONObject pageJSON2 = pageArr.getJSONObject(l);
                final String title = String.valueOf(pageJSON2.get("PAGE_NAME"));
                final JSONArray qlArr = pageJSON2.getJSONArray("StoreLayoutQuickLinks");
                final JSONArray qlPageIdArr = new JSONArray();
                for (int m = 0; m < qlArr.length(); ++m) {
                    qlPageIdArr.put((Object)String.valueOf(pageArr.getJSONObject(qlArr.getInt(m)).get("STORE_PAGE_ID")));
                }
                if (qlPageIdArr.length() > 0) {
                    ebs.addLinks(String.valueOf(pageArr.getJSONObject(l).get("STORE_PAGE_ID")), title, qlPageIdArr);
                }
            }
            this.persistStoreLayout(playstoreDetails.getLong("BUSINESSSTORE_ID"), storeLayoutJSON);
            this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_COMPLETED, "dc.mdm.android.pfw.storelayout.completed");
            isPublished = true;
        }
        catch (final IOException ex) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, null, ex);
            if (ex instanceof GoogleJsonResponseException) {
                this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_FAILED, ((GoogleJsonResponseException)ex).getDetails().getMessage());
            }
            else {
                this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_FAILED, "dc.mdm.android.pfw.storelayout.error.networkerror");
            }
        }
        catch (final Exception ex2) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.SEVERE, null, ex2);
            final String remarks = "mdm.android.appmgmt.unknown_error@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(null);
            this.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_FAILED, remarks);
        }
        if (isPublished) {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72508, null, techName, "dc.mdm.actionlog.afw.layoutpublish_success", domainName, customerId);
        }
        else {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72508, null, techName, "dc.mdm.actionlog.afw.layoutpublish_failed", domainName, customerId);
        }
    }
    
    public JSONObject getStoreLayoutStatus(final Long storeLayoutID) {
        final JSONObject statusData = new JSONObject();
        try {
            final Criteria storeLayoutCriteria = new Criteria(Column.getColumn("StoreLayoutStatus", "STORE_LAYOUT_ID"), (Object)storeLayoutID, 0);
            final DataObject statusDO = MDMUtil.getPersistence().get("StoreLayoutStatus", storeLayoutCriteria);
            if (statusDO != null && !statusDO.isEmpty()) {
                final Row row = statusDO.getFirstRow("StoreLayoutStatus");
                statusData.put("STATUS", (Object)row.get("STATUS"));
                statusData.put("REMARKS", row.get("REMARKS"));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.WARNING, "Exception occurred while getStoreLayoutStatus ", ex);
        }
        return statusData;
    }
    
    public boolean removeStoreLayoutStatus(final Long storeLayoutID) {
        boolean status = false;
        try {
            final Criteria storeLayoutCriteria = new Criteria(Column.getColumn("StoreLayoutStatus", "STORE_LAYOUT_ID"), (Object)storeLayoutID, 0);
            final DataObject statusDO = MDMUtil.getPersistence().get("StoreLayoutStatus", storeLayoutCriteria);
            statusDO.deleteRows("StoreLayoutStatus", (Criteria)null);
            MDMUtil.getPersistence().update(statusDO);
            status = true;
        }
        catch (final Exception ex) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.WARNING, "Exception occurred while removeStoreLayoutStatus ", ex);
        }
        return status;
    }
    
    public void deleteStoreLayout(final Long bsId) {
        try {
            final Long storeLayoutId = (Long)DBUtil.getValueFromDB("StoreLayoutToBusinessStore", "BUSINESSSTORE_ID", (Object)bsId, "STORE_LAYOUT_ID");
            final Criteria storeLayoutCriteria = new Criteria(Column.getColumn("StoreLayout", "STORE_LAYOUT_ID"), (Object)storeLayoutId, 0);
            MDMUtil.getPersistence().delete(storeLayoutCriteria);
        }
        catch (final Exception e) {
            Logger.getLogger(StoreLayoutManager.class.getName()).log(Level.WARNING, "Exception occurred while deleteStoreLayout", e);
        }
    }
    
    public void handleStoreLayout(final JSONObject playStoreDetails, final List newlyDetectedApps) {
        if (!newlyDetectedApps.isEmpty()) {
            this.addNewlyDetectedAppsToDefaultCluster(playStoreDetails, newlyDetectedApps);
        }
        try {
            final Long customerId = playStoreDetails.getLong("CUSTOMER_ID");
            final String migrationNeeded = CustomerParamsHandler.getInstance().getParameterValue("storeLayoutMigrationNeeded", (long)customerId);
            if (!MDMStringUtils.isEmpty(migrationNeeded) && Boolean.valueOf(migrationNeeded)) {
                StoreLayoutManager.logger.log(Level.INFO, "Layout migration needed for customer {0}", customerId);
                final JSONObject toBeMigratedLayout = new StoreLayoutMigrationAsyncHandler().getToBeMigratedLayout(customerId);
                if (toBeMigratedLayout == null) {
                    StoreLayoutManager.logger.log(Level.INFO, "Migration for Playstore Layout not needed {0}", customerId);
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("storeLayoutMigrationNeeded", Boolean.FALSE.toString(), (long)customerId);
                }
                else {
                    new StoreLayoutMigrationAsyncHandler().migrateLayoutViaQueue(customerId);
                }
            }
        }
        catch (final Exception e) {
            StoreLayoutManager.logger.log(Level.SEVERE, "Exception in handling store layout", e);
        }
    }
    
    public void addNewlyDetectedAppsToDefaultCluster(final JSONObject playStoreDetails, final List newlyDetectedApps) {
        StoreLayoutManager.logger.log(Level.INFO, "Adding new apps to default cluster");
        GooglePlayEnterpriseBusinessStore ebs = null;
        try {
            ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
            final JSONObject layoutJSON = ebs.getStoreLayout();
            final String layoutType = layoutJSON.getString("LayoutType");
            if (layoutType.equalsIgnoreCase("basic")) {
                StoreLayoutManager.logger.log(Level.INFO, "Since layout type is basic, adding new apps will be taken care by Google, so leaving it alone.");
                return;
            }
            final String homepageId = layoutJSON.getString("HomePageId");
            final List<StoreCluster> clusters = ebs.getClustersAsObj(homepageId);
            StoreCluster defaultCluster = null;
            if (clusters.size() > 0) {
                for (final StoreCluster cluster : clusters) {
                    final List<LocalizedText> names = cluster.getName();
                    for (final LocalizedText text : names) {
                        if (text.getText().equalsIgnoreCase("Newly Approved Apps")) {
                            defaultCluster = cluster;
                            break;
                        }
                    }
                    if (defaultCluster != null) {
                        break;
                    }
                }
            }
            if (defaultCluster == null) {
                final JSONObject defaultClusterJSON = new JSONObject();
                defaultClusterJSON.put("CLUSTER_NAME", (Object)"Newly Approved Apps");
                defaultClusterJSON.put("CLUSTER_ORDER_NUMBER", 1);
                final String clusterId = ebs.insertCluster(homepageId, defaultClusterJSON);
                defaultCluster = ebs.getStoreCluster(clusterId, homepageId);
            }
            final List newlyDetectedAppsList = new ArrayList();
            for (int k = 0; k < newlyDetectedApps.size(); ++k) {
                newlyDetectedApps.add("app:" + DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)newlyDetectedApps.get(k), "IDENTIFIER"));
            }
            newlyDetectedApps.addAll(defaultCluster.getProductId());
            defaultCluster.setProductId(newlyDetectedAppsList);
            ebs.updateStoreCluster(defaultCluster, homepageId);
            StoreLayoutManager.logger.log(Level.INFO, "Added newly detected apps to Default cluster .{0}", new Object[] { newlyDetectedAppsList });
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72508, null, (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)playStoreDetails.getLong("BUSINESSSTORE_ADDED_BY"), "FIRST_NAME"), "dc.mdm.actionlog.afw.apps_layoutpublish_success", playStoreDetails.getString("MANAGED_DOMAIN_NAME"), playStoreDetails.getLong("CUSTOMER_ID"));
        }
        catch (final Exception e) {
            StoreLayoutManager.logger.log(Level.WARNING, "Unable to add newly detected apps to default cluster", e);
        }
    }
    
    public JSONObject getStoreLayoutMigrationNotification(final JSONObject message) throws Exception {
        final JSONObject response = new JSONObject();
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            final String showNotificationString = CustomerParamsHandler.getInstance().getParameterValue("storeLayoutMigrationNotification", (long)customerId);
            final Boolean showNotification = showNotificationString != null && new Boolean(showNotificationString);
            response.put("migrationneeded", (Object)showNotification);
        }
        catch (final Exception e) {
            StoreLayoutManager.logger.log(Level.SEVERE, "Exception in getStoreLayoutMigrationStatus", e);
            throw e;
        }
        return response;
    }
    
    public void updateStoreLayoutMigrationNotification(final JSONObject message) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            JSONObject bodyJSON = new JSONObject();
            if (!message.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            bodyJSON = message.getJSONObject("msg_body");
            CustomerParamsHandler.getInstance().addOrUpdateParameter("storeLayoutMigrationNotification", Boolean.toString(bodyJSON.optBoolean("migrationneeded", false)), (long)customerId);
        }
        catch (final Exception e) {
            StoreLayoutManager.logger.log(Level.SEVERE, "Exception in updateStoreLayoutMigrationNotification", e);
            throw e;
        }
    }
    
    static {
        STORELAYOUT_STATUS_INPROGRESS = 1;
        STORELAYOUT_STATUS_COMPLETED = 2;
        STORELAYOUT_STATUS_FAILED = 3;
        StoreLayoutManager.logger = Logger.getLogger("MDMBStoreLogger");
    }
}
