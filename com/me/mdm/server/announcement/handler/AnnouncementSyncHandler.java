package com.me.mdm.server.announcement.handler;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.me.uem.announcement.AnnouncementHandler;
import com.me.uem.announcement.AnnouncementDBController;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.agent.handlers.DeviceMessageRequest;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import java.util.logging.Logger;

public class AnnouncementSyncHandler
{
    private static Logger logger;
    
    public JSONArray getAnnouncementMetaDataForResource(final Long resourceId, final Long lastSyncTime) throws Exception {
        try {
            final long startTime = System.currentTimeMillis();
            Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            if (lastSyncTime != -1L) {
                resourceCriteria = resourceCriteria.and(new Criteria(new Column("ResourceToProfileHistory", "LAST_MODIFIED_TIME"), (Object)lastSyncTime, 4));
            }
            else {
                resourceCriteria = resourceCriteria.and(new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0));
            }
            final Criteria profileTypeCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)9, 0);
            final SelectQuery derivedTableQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            derivedTableQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
            derivedTableQuery.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "PROFILE_ID", "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID", "RESOURCE_ID" }, 2));
            derivedTableQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            derivedTableQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            derivedTableQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            derivedTableQuery.addJoin(new Join("ConfigDataItem", "AnnouncementConfigData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            derivedTableQuery.setCriteria(resourceCriteria.and(profileTypeCriteria));
            derivedTableQuery.addSelectColumn(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"));
            derivedTableQuery.addSelectColumn(new Column("ResourceToProfileHistory", "LAST_MODIFIED_TIME"));
            derivedTableQuery.addSelectColumn(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"));
            final Table derivedTable = (Table)new DerivedTable("AnnouncementResource", (Query)derivedTableQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(derivedTable);
            selectQuery.addJoin(new Join(derivedTable, new Table("AnnouncementDetail"), new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
            selectQuery.addJoin(new Join("AnnouncementDetail", "AnnouncementToResources", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 1));
            selectQuery.addJoin(new Join("AnnouncementDetail", "AnnouncementImageDetails", new String[] { "ANNOUNCEMENT_IMG_ID" }, new String[] { "ANNOUNCEMENT_IMG_ID" }, 1));
            final Criteria announcementResourceCriteria = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(announcementResourceCriteria);
            selectQuery.addSelectColumn(new Column("AnnouncementDetail", "ANNOUNCEMENT_ID"));
            selectQuery.addSelectColumn(new Column("AnnouncementDetail", "TITLE"));
            selectQuery.addSelectColumn(new Column("AnnouncementDetail", "TITLE_COLOR"));
            selectQuery.addSelectColumn(new Column("AnnouncementImageDetails", "ANNOUNCEMENT_IMG_ID"));
            selectQuery.addSelectColumn(new Column("AnnouncementImageDetails", "IMAGE_URL"));
            selectQuery.addSelectColumn(new Column("AnnouncementDetail", "NBAR_MESSAGE"));
            selectQuery.addSelectColumn(new Column("AnnouncementDetail", "NEEDS_ACKNOWLEDGEMENT"));
            selectQuery.addSelectColumn(new Column("AnnouncementToResources", "READ_TIME"));
            selectQuery.addSelectColumn(new Column("AnnouncementToResources", "ACK_TIME"));
            selectQuery.addSelectColumn(new Column("AnnouncementToResources", "DELIVERED_TIME"));
            selectQuery.addSelectColumn(new Column("AnnouncementResource", "LAST_MODIFIED_TIME"));
            selectQuery.addSelectColumn(new Column("AnnouncementResource", "MARKED_FOR_DELETE"));
            final DMDataSetWrapper wrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            final JSONArray array = this.processAnnouncementMeta(wrapper, lastSyncTime);
            return array;
        }
        catch (final Exception e) {
            AnnouncementSyncHandler.logger.log(Level.SEVERE, "Exception in getAnnouncementMetaDataForResource", e);
            throw e;
        }
    }
    
    public JSONArray processAnnouncementMeta(final DMDataSetWrapper wrapper, final Long lastSyncTime) throws Exception {
        final JSONArray array = new JSONArray();
        while (wrapper.next()) {
            final Long announcementId = (Long)wrapper.getValue("ANNOUNCEMENT_ID");
            final boolean deleted = (boolean)wrapper.getValue("MARKED_FOR_DELETE");
            final JSONObject object = new JSONObject();
            object.put("ANNOUNCEMENT_ID".toLowerCase(), (Object)announcementId);
            if (deleted) {
                object.put("announcement_deleted", true);
            }
            else {
                final String title = (String)wrapper.getValue("TITLE");
                String nBarIcon = (String)wrapper.getValue("IMAGE_URL");
                final HashMap hm = new HashMap();
                hm.put("path", nBarIcon);
                hm.put("IS_SERVER", false);
                hm.put("IS_AUTHTOKEN", false);
                nBarIcon = MDMEnrollmentUtil.getInstance().getServerBaseURL() + ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                final String nBarMessage = (String)wrapper.getValue("NBAR_MESSAGE");
                final Long iconId = (Long)wrapper.getValue("ANNOUNCEMENT_IMG_ID");
                final Long distributedTime = (Long)wrapper.getValue("LAST_MODIFIED_TIME");
                final String readTime = String.valueOf(wrapper.getValue("READ_TIME"));
                final boolean needsAck = (boolean)wrapper.getValue("NEEDS_ACKNOWLEDGEMENT");
                final String ackTime = String.valueOf(wrapper.getValue("ACK_TIME"));
                final String deliveredTime = String.valueOf(wrapper.getValue("DELIVERED_TIME"));
                object.put("TITLE".toLowerCase(), (Object)title);
                object.put("nbar_icon", (Object)nBarIcon);
                object.put("nbar_icon_id", (Object)iconId);
                object.put("NBAR_MESSAGE".toLowerCase(), (Object)nBarMessage);
                if (this.compareAnnouncementTimeWithSyncTime(readTime, lastSyncTime)) {
                    object.put("announcement_read", false);
                }
                else {
                    object.put("announcement_read", true);
                }
                if (this.compareAnnouncementTimeWithSyncTime(ackTime, lastSyncTime) && needsAck) {
                    object.put("announcement_acknowledged", false);
                }
                else if (needsAck) {
                    object.put("announcement_acknowledged", true);
                }
                if (this.compareAnnouncementTimeWithSyncTime(deliveredTime, lastSyncTime)) {
                    object.put("announcement_need_delivery", true);
                }
                else {
                    object.put("announcement_need_delivery", false);
                }
                object.put("announcement_distributed_time", (Object)String.valueOf(distributedTime));
            }
            array.put((Object)object);
        }
        return array;
    }
    
    private boolean compareAnnouncementTimeWithSyncTime(final String announcementTime, final Long syncTime) {
        return MDMStringUtils.isEmpty(announcementTime) || Long.valueOf(announcementTime) < syncTime;
    }
    
    public DeviceMessage processSyncAnnouncementMetaData(final Long resourceId, final DeviceMessageRequest request) throws Exception {
        final DeviceMessage deviceMessage = new DeviceMessage();
        final JSONObject messageObject = request.messageRequest;
        final Object lastSyncTimeObj = messageObject.get("LastSyncTime");
        Long lastSyncTime = -1L;
        if (lastSyncTimeObj instanceof String) {
            if (SyMUtil.isStringValid((String)lastSyncTimeObj)) {
                lastSyncTime = Long.valueOf((String)lastSyncTimeObj);
            }
        }
        else {
            lastSyncTime = (long)lastSyncTimeObj;
        }
        AnnouncementSyncHandler.logger.log(Level.INFO, "Last sync time is {0} from device {1}", new Object[] { lastSyncTime, resourceId });
        final Long syncTime = System.currentTimeMillis();
        final JSONArray announceArray = this.getAnnouncementMetaDataForResource(resourceId, lastSyncTime);
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("announcementdetails", (Object)announceArray);
        jsonObject.put("LastSyncTime", (Object)syncTime);
        deviceMessage.messageResponse = jsonObject;
        deviceMessage.status = "Acknowledged";
        deviceMessage.messageType = request.messageType;
        return deviceMessage;
    }
    
    public DeviceMessage getAnnouncementForResource(final Long resourceId, final DeviceMessageRequest request, final String deviceUDID, final Long customerId) throws DataAccessException {
        final DeviceMessage deviceMessage = new DeviceMessage();
        final JSONObject messageObject = request.messageRequest;
        final Long announcementId = messageObject.getLong("announcement_id");
        final List<Long> announcementList = new ArrayList<Long>();
        announcementList.add(announcementId);
        final List<Long> collectionList = new AnnouncementDBController().getCollectionIdFromAnnouncementId(announcementList);
        deviceMessage.messageResponse = new AnnouncementHandler().getAnnouncementDetailForCollection(collectionList.get(0), customerId, resourceId, deviceUDID);
        deviceMessage.status = "Acknowledged";
        deviceMessage.messageType = request.messageType;
        return deviceMessage;
    }
    
    public DeviceMessage getSyncAnnouncementAckResponse(final DeviceMessageRequest request) {
        final DeviceMessage deviceMessage = new DeviceMessage();
        final JSONObject messageObject = request.messageRequest;
        final Long lastSyncTime = messageObject.getLong("LastSyncTime");
        final JSONObject object = new JSONObject();
        object.put("LastSyncTime", (Object)lastSyncTime);
        deviceMessage.messageResponse = object;
        deviceMessage.status = "Acknowledged";
        deviceMessage.messageType = request.messageType;
        return deviceMessage;
    }
    
    public void populateiOSFCMNotificationToken() {
        try {
            final Criteria criteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0).and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            for (int count = MDMDBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", criteria), i = 0; i < count; i += 500) {
                final SelectQuery managedQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
                managedQuery.setCriteria(criteria);
                managedQuery.setRange(new Range(i, i + 500));
                managedQuery.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
                managedQuery.addSelectColumn(new Column("ManagedDevice", "UDID"));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(managedQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("ManagedDevice");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final Long resourceId = (Long)row.get("RESOURCE_ID");
                        final String udid = (String)row.get("UDID");
                        final JSONObject object = new JSONObject();
                        object.put("NOTIFICATION_TOKEN_ENCRYPTED", (Object)udid);
                        PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceId, 101, object);
                    }
                }
            }
        }
        catch (final Exception e) {
            AnnouncementSyncHandler.logger.log(Level.SEVERE, "Exception in adding notification token", e);
        }
    }
    
    static {
        AnnouncementSyncHandler.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
}
