package com.me.mdm.server.apps;

import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppStatusRefreshHandler
{
    private static final Logger LOGGER;
    private static final int NOT_INSTALLED_APP = 1;
    private static final int INSTALLED_APP = 2;
    private static final int ALL_APP = 0;
    
    public void refreshAppStatusForAppGroup(final JSONObject appRefreshJSON) throws Exception {
        try {
            final Long customerId = appRefreshJSON.getLong("CUSTOMER_ID");
            final JSONArray appGroupIds = appRefreshJSON.getJSONArray("APP_IDS");
            final List<Long> appGroupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(appGroupIds);
            final JSONArray resourceIds = appRefreshJSON.optJSONArray("DEVICE_IDS");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            if (appGroupIdList.isEmpty() && resourceList.isEmpty()) {
                AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Empty resourcelist & applist so returning for resource");
                return;
            }
            Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Integer status = appRefreshJSON.optInt("STATUS", 1);
            final Criteria statusCriteria = this.getCriteriaForStatus(status);
            if (statusCriteria != null) {
                customerCriteria = customerCriteria.and(statusCriteria);
            }
            final DataObject dataObject = AppsUtil.getInstance().getResourcesForAppGroup(false, appGroupIdList, resourceList, customerCriteria);
            AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Going to refresh app status for app group. App groupIds:{0}", new Object[] { appGroupIdList });
            this.refreshAppGroupStatus(dataObject, appGroupIdList, customerId);
        }
        catch (final JSONException e) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForAppGroup", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForAppGroup", e2);
            throw e2;
        }
    }
    
    private void refreshAppGroupStatus(final DataObject dataObject, final List<Long> appGroupIdList, final Long customerId) throws Exception {
        try {
            final JSONObject appRefreshTaskJSON = new JSONObject();
            if (dataObject != null && !dataObject.isEmpty()) {
                for (final Long appGroupId : appGroupIdList) {
                    final Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
                    final Row appGroupRow = dataObject.getRow("MdAppGroupDetails", appGroupCriteria);
                    final Integer platformType = (Integer)appGroupRow.get("PLATFORM_TYPE");
                    final Criteria appGroupResourceCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
                    final Iterator iterator = dataObject.getRows("MdAppCatalogToResource", appGroupResourceCriteria);
                    final JSONArray appGroupApplicableList = new JSONArray();
                    while (iterator.hasNext()) {
                        final Row appResourceRow = iterator.next();
                        final Long resourceId = (Long)appResourceRow.get("RESOURCE_ID");
                        appGroupApplicableList.put((Object)resourceId);
                    }
                    if (appRefreshTaskJSON.has(platformType.toString())) {
                        final JSONObject appGroupResourceJSON = appRefreshTaskJSON.getJSONObject(platformType.toString());
                        appGroupResourceJSON.put(appGroupId.toString(), (Object)appGroupApplicableList);
                    }
                    else {
                        final JSONObject appGroupResourceJSON = new JSONObject();
                        appGroupResourceJSON.put(appGroupId.toString(), (Object)appGroupApplicableList);
                        appRefreshTaskJSON.put(platformType.toString(), (Object)appGroupResourceJSON);
                    }
                }
                AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Going to add queue for app refresh. JSON:{0} customerid:{1}", new Object[] { appRefreshTaskJSON, customerId });
                this.addRefreshAppStatusToQueue(appRefreshTaskJSON, customerId);
            }
        }
        catch (final DataAccessException e) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppGroupStatus", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppGroupStatus", (Throwable)e2);
            throw e2;
        }
    }
    
    public void refreshAppStatusForGroup(final JSONObject appRefreshJSON) throws Exception {
        try {
            final Long customerId = appRefreshJSON.getLong("CUSTOMER_ID");
            final JSONArray appGroupIds = appRefreshJSON.optJSONArray("APP_IDS");
            final List<Long> appGroupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(appGroupIds);
            final JSONArray resourceIds = appRefreshJSON.getJSONArray("GROUP_IDS");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            final JSONArray deviceIds = appRefreshJSON.optJSONArray("DEVICE_IDS");
            final List<Long> deviceList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIds);
            if (appGroupIdList.isEmpty() && resourceList.isEmpty()) {
                AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Empty resourcelist & applist so returning for group");
                return;
            }
            Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Integer status = appRefreshJSON.optInt("STATUS", 1);
            final Criteria statusCriteria = this.getCriteriaForStatus(status);
            if (statusCriteria != null) {
                customerCriteria = customerCriteria.and(statusCriteria);
            }
            if (deviceList != null && !deviceList.isEmpty()) {
                customerCriteria = customerCriteria.and(new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceList.toArray(), 8));
            }
            final DataObject dataObject = AppsUtil.getInstance().getAppGroupIdsForResource(appGroupIdList, resourceList, true, customerCriteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                if (appGroupIdList.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("MdAppGroupDetails");
                    while (iterator.hasNext()) {
                        final Row appGroupRow = iterator.next();
                        final Long appGroupId = (Long)appGroupRow.get("APP_GROUP_ID");
                        appGroupIdList.add(appGroupId);
                    }
                }
                AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Going to refresh app status for groups. App groupIds:{0}", new Object[] { appGroupIdList });
                this.refreshAppGroupStatus(dataObject, appGroupIdList, customerId);
            }
        }
        catch (final JSONException e) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForGroup", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForGroup", e2);
            throw e2;
        }
    }
    
    private Criteria getCriteriaForStatus(final Integer status) {
        Criteria criteria = null;
        final Column statusColumn = new Column("MdAppCatalogToResource", "STATUS");
        switch (status) {
            case 1: {
                criteria = new Criteria(statusColumn, (Object)new Integer[] { 0, 1 }, 8);
                break;
            }
            case 2: {
                criteria = new Criteria(statusColumn, (Object)2, 0);
                break;
            }
        }
        return criteria;
    }
    
    public void refreshAppStatusForResource(final JSONObject appRefreshJSON) throws Exception {
        try {
            final Long customerId = appRefreshJSON.getLong("CUSTOMER_ID");
            final JSONArray appGroupIds = appRefreshJSON.optJSONArray("APP_IDS");
            final List<Long> appGroupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(appGroupIds);
            final JSONArray resourceIds = appRefreshJSON.getJSONArray("DEVICE_IDS");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            if (appGroupIdList.isEmpty() && resourceList.isEmpty()) {
                AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Empty resourcelist & applist so returning for resource");
                return;
            }
            Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Integer status = appRefreshJSON.optInt("STATUS", 1);
            final Criteria statusCriteria = this.getCriteriaForStatus(status);
            if (statusCriteria != null) {
                customerCriteria = customerCriteria.and(statusCriteria);
            }
            final DataObject dataObject = AppsUtil.getInstance().getAppGroupIdsForResource(appGroupIdList, resourceList, false, customerCriteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                if (appGroupIdList.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("MdAppGroupDetails");
                    while (iterator.hasNext()) {
                        final Row appGroupRow = iterator.next();
                        final Long appGroupId = (Long)appGroupRow.get("APP_GROUP_ID");
                        appGroupIdList.add(appGroupId);
                    }
                }
                AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Going to refresh app status for resource. App groupIds:{0}", new Object[] { appGroupIdList });
                this.refreshAppGroupStatus(dataObject, appGroupIdList, customerId);
            }
        }
        catch (final DataAccessException e) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForResource", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForResource", (Throwable)e2);
            throw e2;
        }
        catch (final Exception e3) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in refreshAppStatusForResource", e3);
            throw e3;
        }
    }
    
    private void addRefreshAppStatusToQueue(final JSONObject queueJSON, final Long customerId) {
        try {
            final CommonQueueData queueItem = new CommonQueueData();
            queueItem.setCustomerId(customerId);
            queueItem.setClassName("com.me.mdm.server.apps.scheduledapptask.AppStatusRefreshTask");
            queueItem.setTaskName("AppStatusRefreshTask");
            queueItem.setJsonQueueData(queueJSON);
            CommonQueueUtil.getInstance().addToQueue(queueItem, CommonQueues.MDM_APP_MGMT);
            AppStatusRefreshHandler.LOGGER.log(Level.INFO, "Added to queue for app refresh");
        }
        catch (final Exception e) {
            AppStatusRefreshHandler.LOGGER.log(Level.SEVERE, "Exception in addRefreshAppStatusToQueue", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
