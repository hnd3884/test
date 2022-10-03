package com.me.mdm.server.notification;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.orglock.FairLockImpAPI;
import com.me.devicemanagement.framework.server.orglock.FairLockInvocationHandler;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import com.me.mdm.server.metracker.METrackParamManager;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class NotificationHandler
{
    private static NotificationHandler notificationHandler;
    private static Logger logger;
    private Logger wakeupLogger;
    public static final int YET_TO_NOTIFY = 0;
    public static final int NOTIFICATION_SUCESS = 1;
    public static final int NOTIFICATION_FAIL = 2;
    public static final int NOTIFICATION_INPROGRESS = 3;
    private static int noOfRecord;
    private static final Integer NOTIFICATION_LOCK;
    public static final int IOS_MDM_NOTIFICATION = 1;
    public static final int NOTIFICATION_NOT_FOUND = -1;
    public static final String SUCCESS_LIST = "success";
    public static final String FAILURE_LIST = "failure";
    public static final Long THRESHOLD_TIME;
    
    public NotificationHandler() {
        this.wakeupLogger = Logger.getLogger("MDMWakupReqLogger");
    }
    
    public static synchronized NotificationHandler getInstance() {
        if (NotificationHandler.notificationHandler == null) {
            NotificationHandler.notificationHandler = new NotificationHandler();
            try {
                NotificationHandler.noOfRecord = Integer.valueOf(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("no.of.records.for.notification.batch", NotificationHandler.noOfRecord + ""));
            }
            catch (final Exception e) {
                NotificationHandler.logger.log(Level.INFO, "Exception Occurred while changing range ", e);
            }
        }
        return NotificationHandler.notificationHandler;
    }
    
    public void SendNotification(final List<Long> resourceList) throws Exception {
        if (resourceList.isEmpty()) {
            return;
        }
        final List iOSResourceList = new ArrayList();
        final List androidResourceList = new ArrayList();
        final List windowsResourceList = new ArrayList();
        final List<JSONObject> deviceProps = ManagedDeviceHandler.getInstance().getPlatformTypeForListOfDeviceIDs(resourceList);
        for (int i = 0; i < deviceProps.size(); ++i) {
            try {
                final JSONObject deviceDetails = deviceProps.get(i);
                final Long resourceId = (Long)deviceDetails.get("RESOURCE_ID");
                final Integer devicePlatform = (Integer)deviceDetails.get("PLATFORM_TYPE");
                if (devicePlatform == 2) {
                    androidResourceList.add(resourceId);
                }
                else if (devicePlatform == 1) {
                    iOSResourceList.add(resourceId);
                }
                else if (devicePlatform == 3) {
                    windowsResourceList.add(resourceId);
                }
            }
            catch (final Exception ex) {
                NotificationHandler.logger.log(Level.SEVERE, null, ex);
            }
        }
        if (!iOSResourceList.isEmpty()) {
            this.SendNotification(iOSResourceList, 1);
        }
        else if (!androidResourceList.isEmpty()) {
            this.SendNotification(androidResourceList, 2);
        }
        else if (!windowsResourceList.isEmpty()) {
            this.SendNotification(windowsResourceList, 303);
            this.SendNotification(windowsResourceList, 3);
        }
    }
    
    public void SendNotification(List resourceList, final int notificationType) throws Exception {
        NotificationHandler.logger.log(Level.INFO, "NotificationHandler: inside SendNotification");
        if (resourceList != null && !resourceList.isEmpty()) {
            if (notificationType == 3 || notificationType == 303) {
                resourceList = this.removeWindows8AndBelowDevices(resourceList);
            }
            final int notificationCount = this.addNotification(resourceList, notificationType);
            if (notificationType == 3 || notificationType == 303) {
                METrackParamManager.incrementMETrackParams("Win_Total_Notification_Count", notificationCount);
            }
            this.addToQueue(notificationType);
        }
    }
    
    public void SendNotification(final HashMap notificationMap) throws Exception {
        NotificationHandler.logger.log(Level.INFO, "NotificationHandler: inside SendNotification");
        final ArrayList notificationTypeList = new ArrayList(notificationMap.keySet());
        final ArrayList completeResourceList = new ArrayList();
        for (final Object notificationType : notificationTypeList) {
            completeResourceList.addAll(notificationMap.get(notificationType));
        }
        if (!completeResourceList.isEmpty()) {
            final HashMap notificationCountMap = this.addNotification(notificationMap);
            if (notificationCountMap.containsKey(3) || notificationCountMap.containsKey(303)) {
                Integer notificationCount = 0;
                if (notificationCountMap.containsKey(3)) {
                    final ArrayList tempList = notificationCountMap.get(3);
                    notificationCount += tempList.size();
                }
                if (notificationCountMap.containsKey(303)) {
                    final ArrayList tempList = notificationCountMap.get(303);
                    notificationCount += tempList.size();
                }
                METrackParamManager.incrementMETrackParams("Win_Total_Notification_Count", notificationCount);
            }
            for (final Object notificationType2 : notificationTypeList) {
                this.addToQueue((int)notificationType2);
            }
        }
    }
    
    public void notifyAllDevicesToWakeUp(final int notificationType) throws Exception {
        this.addToQueue(notificationType);
    }
    
    private List removeWindows8AndBelowDevices(final List resourceList) {
        final List newResList = new ArrayList();
        for (int i = 0; i < resourceList.size(); ++i) {
            if (ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(resourceList.get(i))) {
                newResList.add(resourceList.get(i));
            }
        }
        return newResList;
    }
    
    private void addToQueue(final int notificationType) throws Exception {
        final DCQueue queue = DCQueueHandler.getQueue("notification-processor");
        final DCQueueData queueData = new DCQueueData();
        queueData.postTime = System.currentTimeMillis();
        queueData.queueData = Integer.toString(notificationType);
        queue.addToQueue(queueData);
    }
    
    public void addNotificationOnly(final List resourceList, final int notificationType) {
        this.addNotification(resourceList, notificationType);
    }
    
    public int addNotification(final List resourceList, final int notificationType) {
        int notificationCount = 0;
        try {
            NotificationHandler.logger.log(Level.INFO, "NotificationHandler: inside addNotification -> resourceList{0}, notificationType: {1}, Applied Commands {2}", new Object[] { resourceList, notificationType, MDMUtil.getAndDeleteCommandsFromThreadLocal() });
            if (notificationType == 0 || notificationType == -1) {
                NotificationHandler.logger.log(Level.WARNING, "NotificationType is Zero. It has to be fixed !!! {0}", notificationType);
                Thread.dumpStack();
                return 0;
            }
            final FairLockImpAPI fairLock = object -> {
                Integer notificationAddedCount = 0;
                final Criteria cri = new Criteria(new Column("DeviceNotification", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria newCri = new Criteria(new Column("DeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0).and(cri);
                final DataObject dObj = MDMUtil.getPersistence().get("DeviceNotification", newCri);
                if (dObj.isEmpty()) {
                    for (int j = 0; j < resourceList.size(); ++j) {
                        final Row notificationRow = new Row("DeviceNotification");
                        notificationRow.set("RESOURCE_ID", resourceList.get(j));
                        notificationRow.set("NOTIFICATION_TYPE", (Object)notificationType);
                        notificationRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                        notificationRow.set("STATUS", (Object)0);
                        dObj.addRow(notificationRow);
                    }
                    notificationAddedCount = resourceList.size();
                }
                else {
                    Criteria criteria = null;
                    Criteria newCriteria = null;
                    Row notificationRow2 = null;
                    for (int i = 0; i < resourceList.size(); ++i) {
                        criteria = new Criteria(Column.getColumn("DeviceNotification", "RESOURCE_ID"), resourceList.get(i), 0);
                        newCriteria = new Criteria(Column.getColumn("DeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0).and(criteria);
                        notificationRow2 = dObj.getRow("DeviceNotification", newCriteria);
                        if (notificationRow2 == null) {
                            notificationRow2 = new Row("DeviceNotification");
                            notificationRow2.set("RESOURCE_ID", resourceList.get(i));
                            notificationRow2.set("NOTIFICATION_TYPE", (Object)notificationType);
                            notificationRow2.set("ADDED_TIME", (Object)System.currentTimeMillis());
                            notificationRow2.set("STATUS", (Object)0);
                            dObj.addRow(notificationRow2);
                            ++notificationAddedCount;
                        }
                        else {
                            final Integer notificationStatus = (Integer)notificationRow2.get("STATUS");
                            if (notificationStatus == 2 || notificationStatus == 1) {
                                notificationRow2.set("STATUS", (Object)0);
                                notificationRow2.set("NOTIFICATION_TYPE", (Object)notificationType);
                                notificationRow2.set("ADDED_TIME", (Object)System.currentTimeMillis());
                                dObj.updateRow(notificationRow2);
                                ++notificationAddedCount;
                            }
                            else if (notificationStatus == 3) {
                                this.wakeupLogger.log(Level.INFO, "Device Struck in inProgress. Resource:{0},notificationtype:{1}", new Object[] { resourceList.get(i), notificationType });
                                final Long addedTime = (Long)notificationRow2.get("ADDED_TIME");
                                final Long currentTime = System.currentTimeMillis();
                                if (currentTime - addedTime > NotificationHandler.THRESHOLD_TIME) {
                                    this.wakeupLogger.log(Level.INFO, "Device struck inProgress for more than 3hrs resending wakeup.Resource:{0},notificationtype:{1}", new Object[] { resourceList.get(i), notificationType });
                                    notificationRow2.set("STATUS", (Object)0);
                                    notificationRow2.set("NOTIFICATION_TYPE", (Object)notificationType);
                                    notificationRow2.set("ADDED_TIME", (Object)System.currentTimeMillis());
                                    dObj.updateRow(notificationRow2);
                                    ++notificationAddedCount;
                                }
                            }
                        }
                    }
                }
                MDMUtil.getPersistence().update(dObj);
                return notificationAddedCount;
            };
            notificationCount = (int)FairLockInvocationHandler.execute((Object)NotificationHandler.NOTIFICATION_LOCK, fairLock);
        }
        catch (final Exception ex) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while adding the notification", ex);
        }
        return notificationCount;
    }
    
    public HashMap addNotification(final HashMap notificationMap) {
        final HashMap notificationCountMap = new HashMap();
        try {
            NotificationHandler.logger.log(Level.INFO, "NotificationHandler: inside addNotification -> notificationMap: {0}", new Object[] { notificationMap });
            final ArrayList notificationTypeList = new ArrayList(notificationMap.keySet());
            if (notificationTypeList.contains(0) || notificationTypeList.contains(-1)) {
                NotificationHandler.logger.log(Level.WARNING, "NotificationType is Zero. It has to be fixed !!!");
            }
            final ArrayList completeResourceList = new ArrayList();
            for (final Object notificationType : notificationTypeList) {
                completeResourceList.addAll(notificationMap.get(notificationType));
            }
            final FairLockImpAPI fairLock = object -> {
                final Criteria cri = new Criteria(new Column("DeviceNotification", "RESOURCE_ID"), (Object)completeResourceList.toArray(), 8);
                final Criteria newCri = new Criteria(new Column("DeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationTypeList.toArray(), 8).and(cri);
                final DataObject dataObject = MDMUtil.getPersistence().get("DeviceNotification", newCri);
                for (final Object notificationType : notificationTypeList) {
                    final Iterator iterator = dataObject.getRows("DeviceNotification", new Criteria(Column.getColumn("DeviceNotification", "NOTIFICATION_TYPE"), notificationType, 0));
                    if (!iterator.hasNext()) {
                        final ArrayList resourceList = notificationMap.get(notificationType);
                        for (int j = 0; j < resourceList.size(); ++j) {
                            final Row notificationRow = new Row("DeviceNotification");
                            notificationRow.set("RESOURCE_ID", resourceList.get(j));
                            notificationRow.set("NOTIFICATION_TYPE", (Object)notificationType);
                            notificationRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                            notificationRow.set("STATUS", (Object)0);
                            dataObject.addRow(notificationRow);
                        }
                        notificationCountMap.put(notificationType, resourceList.size());
                    }
                    else {
                        Criteria criteria = null;
                        Criteria newCriteria = null;
                        Row notificationRow = null;
                        final ArrayList resourceList2 = notificationMap.get(notificationType);
                        int notificationCount = 0;
                        for (int i = 0; i < resourceList2.size(); ++i) {
                            criteria = new Criteria(Column.getColumn("DeviceNotification", "RESOURCE_ID"), resourceList2.get(i), 0);
                            newCriteria = new Criteria(Column.getColumn("DeviceNotification", "NOTIFICATION_TYPE"), notificationType, 0).and(criteria);
                            notificationRow = dataObject.getRow("DeviceNotification", newCriteria);
                            if (notificationRow == null) {
                                notificationRow = new Row("DeviceNotification");
                                notificationRow.set("RESOURCE_ID", resourceList2.get(i));
                                notificationRow.set("NOTIFICATION_TYPE", notificationType);
                                notificationRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                                notificationRow.set("STATUS", (Object)0);
                                dataObject.addRow(notificationRow);
                                ++notificationCount;
                            }
                            else {
                                final Integer notificationStatus = (Integer)notificationRow.get("STATUS");
                                if (notificationStatus == 2 || notificationStatus == 1) {
                                    notificationRow.set("STATUS", (Object)0);
                                    notificationRow.set("NOTIFICATION_TYPE", notificationType);
                                    notificationRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                                    dataObject.updateRow(notificationRow);
                                    ++notificationCount;
                                }
                                else if (notificationStatus == 3) {
                                    this.wakeupLogger.log(Level.INFO, "Device Struck in inProgress. Resource:{0},notificationtype:{1}", new Object[] { resourceList2.get(i), notificationType });
                                    final Long addedTime = (Long)notificationRow.get("ADDED_TIME");
                                    final Long currentTime = System.currentTimeMillis();
                                    if (currentTime - addedTime > NotificationHandler.THRESHOLD_TIME) {
                                        this.wakeupLogger.log(Level.INFO, "Device struck inProgress for more than 3hrs resending wakeup.Resource:{0},notificationtype:{1}", new Object[] { resourceList2.get(i), notificationType });
                                        notificationRow.set("STATUS", (Object)0);
                                        notificationRow.set("NOTIFICATION_TYPE", notificationType);
                                        notificationRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                                        dataObject.updateRow(notificationRow);
                                        ++notificationCount;
                                    }
                                }
                            }
                        }
                        notificationCountMap.put(notificationType, notificationCount);
                    }
                }
                MDMUtil.getPersistence().update(dataObject);
                return object;
            };
            FairLockInvocationHandler.execute((Object)NotificationHandler.NOTIFICATION_LOCK, fairLock);
        }
        catch (final Exception ex) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while adding the notification", ex);
        }
        return notificationMap;
    }
    
    public void updateNotificationStatus(final List resourceList, final int status, final int notificationType, final Criteria criteria) {
        try {
            NotificationHandler.logger.log(Level.INFO, "Updating the notification for resource {0} with status{1}", new Object[] { resourceList, status });
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("DeviceNotification");
            final Criteria cri = new Criteria(new Column("DeviceNotification", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria notificationTypeCri = new Criteria(new Column("DeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
            Criteria notificationCriteria = cri.and(notificationTypeCri);
            if (criteria != null) {
                notificationCriteria = notificationCriteria.and(criteria);
            }
            uQuery.setCriteria(notificationCriteria);
            uQuery.setUpdateColumn("STATUS", (Object)status);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while updating the notification status", ex);
        }
    }
    
    public void removeNotification(final List resourceList) {
        final Criteria cri = new Criteria(new Column("DeviceNotification", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        try {
            DataAccess.delete(cri);
        }
        catch (final Exception ex) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while notification request", ex);
        }
    }
    
    public HashMap<Long, List<Long>> getResourcesForNotification(final int notificationType) {
        NotificationHandler.logger.log(Level.INFO, "Inside getResourcesForNotification {0}", notificationType);
        final HashMap<Long, List<Long>> notificationMap = new HashMap<Long, List<Long>>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceNotification"));
        final Join resourceJoin = new Join("DeviceNotification", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Criteria cPlatform = new Criteria(new Column("DeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
        final Criteria cStatus = new Criteria(new Column("DeviceNotification", "STATUS"), (Object)0, 0);
        sQuery.setCriteria(cPlatform.and(cStatus));
        sQuery.addJoin(resourceJoin);
        sQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        final SortColumn sortColumn = new SortColumn("DeviceNotification", "ADDED_TIME", false);
        sQuery.addSortColumn(sortColumn);
        final Range range = new Range(0, NotificationHandler.noOfRecord);
        sQuery.setRange(range);
        try {
            DataObject DO = null;
            DO = MDMUtil.getPersistence().get(sQuery);
            if (DO != null && !DO.isEmpty()) {
                final Iterator item = DO.getRows("Resource");
                while (item.hasNext()) {
                    final Row row = item.next();
                    final Long resourceId = (Long)row.get("RESOURCE_ID");
                    final Long customerId = (Long)row.get("CUSTOMER_ID");
                    if (notificationMap.containsKey(customerId)) {
                        notificationMap.get(customerId).add(resourceId);
                    }
                    else {
                        final ArrayList<Long> resourceList = new ArrayList<Long>();
                        resourceList.add(resourceId);
                        notificationMap.put(customerId, resourceList);
                    }
                }
            }
        }
        catch (final Exception ex) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while getting the resource for notification", ex);
        }
        return notificationMap;
    }
    
    public int getNotificationStatus(final Long resourceID) {
        return this.getNotificationStatus(resourceID, null);
    }
    
    public int getNotificationStatus(final Long resourceID, final Integer notificationType) {
        int status = -1;
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceNotification", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria notifTypeCriteria = new Criteria(Column.getColumn("DeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
            final Criteria criteria = (notificationType != null) ? resourceCriteria.and(notifTypeCriteria) : resourceCriteria;
            final DataObject dO = MDMUtil.getPersistence().get("DeviceNotification", criteria);
            if (!dO.isEmpty()) {
                final Object statusObject = dO.getFirstValue("DeviceNotification", "STATUS");
                if (statusObject != null) {
                    status = (int)statusObject;
                }
            }
        }
        catch (final Exception ex) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception raised while getting status of the device Resource ID : {0} {1}", new Object[] { resourceID, ex.getLocalizedMessage() });
        }
        return status;
    }
    
    public static int getNotificationType(final int platform) {
        int notificationType = -1;
        if (platform == 1) {
            notificationType = 1;
        }
        else if (platform == 2) {
            notificationType = 2;
        }
        else if (platform == 3) {
            notificationType = 3;
        }
        else if (platform == 4) {
            notificationType = 4;
        }
        return notificationType;
    }
    
    public static void wakeUpDeviceStruckInProgress(final Long customerId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceNotification"));
            selectQuery.addJoin(new Join("DeviceNotification", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria inProgressCriteria = new Criteria(new Column("DeviceNotification", "STATUS"), (Object)3, 0);
            final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Long inProgressTime = System.currentTimeMillis() - NotificationHandler.THRESHOLD_TIME;
            final Criteria inProgressTimeCriteria = new Criteria(new Column("DeviceNotification", "ADDED_TIME"), (Object)inProgressTime, 7);
            final Criteria finalCriteria = inProgressCriteria.and(managedDeviceCriteria).and(customerCriteria).and(inProgressTimeCriteria);
            selectQuery.setCriteria(finalCriteria);
            selectQuery.addSelectColumn(new Column("DeviceNotification", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("DeviceNotification", "NOTIFICATION_TYPE"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final HashMap<Integer, List> resourceObject = new HashMap<Integer, List>();
                final Iterator iterator = dataObject.getRows("DeviceNotification");
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    final Long resourceId = (Long)resourceRow.get("RESOURCE_ID");
                    final Integer notificationType = (Integer)resourceRow.get("NOTIFICATION_TYPE");
                    List resourceList = resourceObject.get(notificationType);
                    if (resourceList != null && !resourceList.isEmpty()) {
                        resourceList.add(resourceId);
                    }
                    else {
                        resourceList = new ArrayList();
                        resourceList.add(resourceId);
                        resourceObject.put(notificationType, resourceList);
                    }
                }
                NotificationHandler.logger.log(Level.INFO, "Device inprogress for more than 3 hours.Device Map:{0}", new Object[] { resourceObject });
                for (final Integer key : resourceObject.keySet()) {
                    final List<Long> resourceList2 = resourceObject.get(key);
                    getInstance().SendNotification(resourceList2, key);
                }
            }
            else {
                NotificationHandler.logger.log(Level.INFO, "No resources in inprogress for customer:{0}", new Object[] { customerId });
            }
        }
        catch (final DataAccessException e) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while getting resource list for processing", (Throwable)e);
        }
        catch (final Exception e2) {
            NotificationHandler.logger.log(Level.SEVERE, "Exception while sending notification", e2);
        }
    }
    
    static {
        NotificationHandler.notificationHandler = null;
        NotificationHandler.logger = Logger.getLogger("MDMLogger");
        NotificationHandler.noOfRecord = 50;
        NOTIFICATION_LOCK = new Integer(1);
        THRESHOLD_TIME = 10800000L;
    }
}
