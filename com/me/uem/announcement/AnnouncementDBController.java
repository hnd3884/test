package com.me.uem.announcement;

import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collection;
import java.util.HashMap;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AnnouncementDBController
{
    private Logger logger;
    
    public AnnouncementDBController() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    protected List getAnnouncementColumnList() {
        final List columnList = new ArrayList();
        columnList.add(new Column("Announcement", "*"));
        columnList.add(new Column("AnnouncementDetail", "*"));
        columnList.add(new Column("AnnouncementImageDetails", "*"));
        return columnList;
    }
    
    protected List getAnnouncementCollectionColumnList() {
        final List columnList = new ArrayList();
        columnList.add(new Column("Announcement", "*"));
        columnList.add(new Column("AnnouncementDetail", "*"));
        columnList.add(new Column("AnnouncementImageDetails", "*"));
        columnList.add(new Column("AnnouncementConfigData", "*"));
        columnList.add(new Column("ConfigDataItem", "*"));
        columnList.add(new Column("ConfigData", "*"));
        columnList.add(new Column("CfgDataToCollection", "*"));
        columnList.add(new Column("Collection", "*"));
        columnList.add(new Column("ProfileToCollection", "*"));
        columnList.add(new Column("Profile", "*"));
        columnList.add(Column.getColumn("CREATED_USER", "FIRST_NAME", "created_by_user"));
        columnList.add(Column.getColumn("LAST_MODIFIED_USER", "FIRST_NAME", "last_modified_by_user"));
        return columnList;
    }
    
    protected Criteria getAnnouncementCriteria(final List announcementList) {
        final Criteria announcementCri = new Criteria(new Column("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementList.toArray(), 8);
        return announcementCri;
    }
    
    protected SelectQuery getAnnouncementQuery(final long announcement_id) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Announcement"));
        sQuery.addJoin(new Join("Announcement", "AnnouncementDetail", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementDetail", "AnnouncementImageDetails", new String[] { "ANNOUNCEMENT_IMG_ID" }, new String[] { "ANNOUNCEMENT_IMG_ID" }, 2));
        final Criteria annoucC = new Criteria(new Column("Announcement", "ANNOUNCEMENT_ID"), (Object)announcement_id, 0);
        sQuery.setCriteria(annoucC);
        sQuery.addSelectColumns(this.getAnnouncementColumnList());
        return sQuery;
    }
    
    protected DataObject getAnnouncementDO(final long announcement_id) throws DataAccessException {
        final SelectQuery sQuery = this.getAnnouncementQuery(announcement_id);
        final DataObject DO = SyMUtil.getPersistence().get(sQuery);
        return DO;
    }
    
    protected void addAnnouncementRow(final DataObject announcementDO, final JSONObject announcementJSON) throws DataAccessException {
        final long announcement_id = announcementJSON.optLong("announcement_id", -1L);
        Row announcementRow;
        Boolean isAdd;
        if (announcement_id != -1L && !announcementDO.isEmpty()) {
            announcementRow = announcementDO.getRow("Announcement");
            isAdd = false;
        }
        else {
            announcementRow = new Row("Announcement");
            isAdd = true;
        }
        announcementRow.set("ANNOUNCEMENT_NAME", (Object)announcementJSON.getString("announcement_name"));
        announcementRow.set("ANNOUNCEMENT_FORMAT", (Object)announcementJSON.getInt("announcement_format"));
        if (isAdd) {
            announcementDO.addRow(announcementRow);
        }
        else {
            announcementDO.updateRow(announcementRow);
        }
    }
    
    protected void addAnnouncementImageRow(final DataObject announcementDO, final JSONObject announcementJSON) throws DataAccessException {
        final JSONObject announcementDetails = announcementJSON.getJSONObject("announcement_detail");
        final String nbarIconUrl = announcementDetails.getString("nbar_icon");
        final Criteria cri = new Criteria(new Column("AnnouncementImageDetails", "IMAGE_URL"), (Object)nbarIconUrl, 0);
        DataObject imageDO = MDMUtil.getPersistence().get("AnnouncementImageDetails", cri);
        if (imageDO.isEmpty()) {
            final Row announcementImageRow = new Row("AnnouncementImageDetails");
            announcementImageRow.set("IMAGE_URL", (Object)nbarIconUrl);
            imageDO.addRow(announcementImageRow);
            imageDO = MDMUtil.getPersistence().add(imageDO);
        }
        final Long imgId = (Long)imageDO.getValue("AnnouncementImageDetails", "ANNOUNCEMENT_IMG_ID", (Criteria)null);
        announcementJSON.put("announcement_detail", (Object)announcementJSON.getJSONObject("announcement_detail").put("ANNOUNCEMENT_IMG_ID", (Object)imgId));
    }
    
    protected void addAnnouncementDetailRow(final DataObject announcementDO, final JSONObject announcementJSON) throws DataAccessException {
        Boolean isAdd = false;
        final Row announcementRow = announcementDO.getRow("Announcement");
        final long announcement_id = announcementJSON.optLong("announcement_id", -1L);
        Row announcementDetailRow;
        if (announcement_id != -1L) {
            announcementDetailRow = announcementDO.getRow("AnnouncementDetail");
            if (announcementDetailRow == null) {
                announcementDetailRow = new Row("AnnouncementDetail");
                isAdd = true;
            }
        }
        else {
            announcementDetailRow = new Row("AnnouncementDetail");
            isAdd = true;
        }
        final JSONObject announcementDetails = announcementJSON.getJSONObject("announcement_detail");
        announcementDetailRow.set("ANNOUNCEMENT_ID", announcementRow.get("ANNOUNCEMENT_ID"));
        announcementDetailRow.set("TITLE", (Object)announcementDetails.optString("title"));
        announcementDetailRow.set("TITLE_COLOR", (Object)announcementDetails.getString("title_color"));
        announcementDetailRow.set("ANNOUNCEMENT_IMG_ID", announcementDetails.get("ANNOUNCEMENT_IMG_ID"));
        announcementDetailRow.set("NBAR_MESSAGE", (Object)announcementDetails.getString("nbar_message"));
        announcementDetailRow.set("DETAIL_MESSAGE", (Object)announcementDetails.getString("detail_message"));
        announcementDetailRow.set("NEEDS_ACKNOWLEDGEMENT", (Object)announcementDetails.getBoolean("needs_acknowledgement"));
        announcementDetailRow.set("ACK_BUTTON", (Object)announcementDetails.getString("ack_button"));
        if (isAdd) {
            announcementDO.addRow(announcementDetailRow);
        }
        else {
            announcementDO.updateRow(announcementDetailRow);
        }
    }
    
    protected void addAnnouncementSpanRow(final DataObject announcementDO, final JSONObject announcementJSON) throws DataAccessException {
        Boolean isAdd = false;
        final Row announcementRow = announcementDO.getRow("Announcement");
        final long announcement_id = announcementJSON.optLong("announcement_id", -1L);
        Row announcementSpanRow;
        if (announcement_id != -1L) {
            announcementSpanRow = announcementDO.getRow("AnnouncementSpan");
            if (announcementSpanRow == null) {
                announcementSpanRow = new Row("AnnouncementSpan");
                isAdd = true;
            }
        }
        else {
            announcementSpanRow = new Row("AnnouncementSpan");
            isAdd = true;
        }
        final JSONObject announcementSpanJson = announcementJSON.getJSONObject("announcement_span");
        announcementSpanRow.set("ANNOUNCEMENT_ID", announcementRow.get("ANNOUNCEMENT_ID"));
        announcementSpanRow.set("START_TIME", (Object)announcementSpanJson.optLong("start_time"));
        announcementSpanRow.set("END_TIME", (Object)announcementSpanJson.optLong("end_time"));
        announcementSpanRow.set("REPEAT_FREQUENCY", (Object)announcementSpanJson.optInt("repeat_frequency"));
        announcementSpanRow.set("REPEAT_DURATION", (Object)announcementSpanJson.optInt("repeat_duration"));
        if (isAdd) {
            announcementDO.addRow(announcementSpanRow);
        }
        else {
            announcementDO.updateRow(announcementSpanRow);
        }
    }
    
    protected SelectQuery getAnnouncementConfigSelectQuery(final long customerid, final long announcementid, final int startIndex, final int limit, final long timestamp, final Boolean isMovedToTrash) {
        return this.getAnnouncementConfigSelectQuery(customerid, announcementid, startIndex, limit, timestamp, -1L, isMovedToTrash);
    }
    
    protected SelectQuery getAnnouncementConfigSelectQuery(final long customerid, final long announcementid, final int startIndex, final int limit, final long timestamp, final long endtime, final Boolean isMovedToTrash) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Announcement"));
        sQuery.addJoin(new Join("Announcement", "AnnouncementDetail", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementDetail", "AnnouncementImageDetails", new String[] { "ANNOUNCEMENT_IMG_ID" }, new String[] { "ANNOUNCEMENT_IMG_ID" }, 1));
        sQuery.addJoin(new Join("Announcement", "AnnouncementConfigData", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        sQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "Profile", "CREATED_USER", 2));
        sQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "Profile", "LAST_MODIFIED_USER", 2));
        Criteria criteria = null;
        if (customerid != -1L) {
            final Criteria cCustomer = criteria = new Criteria(new Column("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerid, 0);
        }
        if (announcementid != -1L) {
            final Criteria annoucC = new Criteria(new Column("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementid, 0);
            if (criteria != null) {
                criteria = criteria.and(annoucC);
            }
            else {
                criteria = annoucC;
            }
        }
        if (isMovedToTrash != null) {
            final Criteria profileTrashCri = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)isMovedToTrash, 0);
            if (criteria != null) {
                criteria = criteria.and(profileTrashCri);
            }
            else {
                criteria = profileTrashCri;
            }
        }
        if (criteria != null) {
            sQuery.setCriteria(criteria);
        }
        if (startIndex != -1 && limit != -1) {
            sQuery.setRange(new Range(startIndex, limit));
            final SortColumn sortColumn = new SortColumn(new Column("Profile", "LAST_MODIFIED_TIME"), false);
            final ArrayList list = new ArrayList();
            list.add(sortColumn);
            sQuery.addSortColumns((List)list);
        }
        return sQuery;
    }
    
    protected void updateAnnouncementAcknowledgment(final HashMap<Long, Long> announcementMap, final long resourceId) throws Exception {
        final List<Long> announcementIds = new ArrayList<Long>(announcementMap.keySet());
        final Criteria announcementListCriteria = new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementIds.toArray(), 8);
        final Criteria resourceCriteria = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        final DataObject dataObject = SyMUtil.getPersistenceLite().get("AnnouncementToResources", announcementListCriteria.and(resourceCriteria));
        for (final Long announcementId : announcementIds) {
            final Criteria announcementCriteria = new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
            final Row row = dataObject.getRow("AnnouncementToResources", announcementCriteria);
            if (row != null) {
                final Long time = announcementMap.get(announcementId);
                row.set("ACK_TIME", (Object)time);
                dataObject.updateRow(row);
            }
            else {
                this.logger.log(Level.INFO, "Announcement may be removed from the device. {0} - {1}", new Object[] { announcementId, resourceId });
            }
        }
        MDMUtil.getPersistenceLite().update(dataObject);
    }
    
    protected void updateAnnouncementRead(final HashMap<Long, Long> announcementMap, final long resourceId) throws Exception {
        final List<Long> announcementIds = new ArrayList<Long>(announcementMap.keySet());
        final Criteria announcementListCriteria = new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementIds.toArray(), 8);
        final Criteria resourceCriteria = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        final DataObject dataObject = SyMUtil.getPersistenceLite().get("AnnouncementToResources", announcementListCriteria.and(resourceCriteria));
        for (final Long announcementId : announcementIds) {
            final Criteria announcementCriteria = new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
            final Row row = dataObject.getRow("AnnouncementToResources", announcementCriteria);
            if (row != null) {
                final Long time = announcementMap.get(announcementId);
                row.set("READ_TIME", (Object)time);
                dataObject.updateRow(row);
            }
            else {
                this.logger.log(Level.INFO, "Announcement may be removed from the device. {0} - {1}", new Object[] { announcementId, resourceId });
            }
        }
        MDMUtil.getPersistenceLite().update(dataObject);
    }
    
    protected void updateAnnouncementDelivery(final long collectionId, final long resourceId, final long time) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "AnnouncementConfigData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementConfigData", "Announcement", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("Announcement", "AnnouncementToResources", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 1));
        sQuery.addSelectColumn(Column.getColumn("AnnouncementToResources", "*"));
        sQuery.addSelectColumn(Column.getColumn("Announcement", "*"));
        final Criteria annouCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria resCriteria = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("AnnouncementToResources", annouCriteria.and(resCriteria));
        final Row ackRow = dataObject.getRow("AnnouncementToResources");
        if (ackRow != null) {
            final Row announcementRow = dataObject.getFirstRow("Announcement");
            ackRow.set("ANNOUNCEMENT_ID", announcementRow.get("ANNOUNCEMENT_ID"));
            ackRow.set("RESOURCE_ID", (Object)resourceId);
            ackRow.set("DELIVERED_TIME", (Object)time);
            dataObject.addRow(ackRow);
        }
        else {
            ackRow.set("DELIVERED_TIME", (Object)time);
            dataObject.updateRow(ackRow);
        }
        SyMUtil.getPersistence().update(dataObject);
    }
    
    protected void updateAnnouncementDelivery(final List<Long> announcementIds, final long resourceId, final long time) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AnnouncementToResources");
        updateQuery.setUpdateColumn("DELIVERED_TIME", (Object)time);
        updateQuery.setCriteria(new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementIds.toArray(), 8).and(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0));
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    private SelectQuery getAnnouncementResourceQuery(final Long collectionId, final List resourceList) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "AnnouncementConfigData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementConfigData", "Announcement", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("Announcement", "AnnouncementToResources", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 1));
        sQuery.addSelectColumn(Column.getColumn("AnnouncementToResources", "*"));
        sQuery.addSelectColumn(Column.getColumn("Announcement", "*"));
        final Criteria annouCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria resCriteria = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria resCriteriaNullCri = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)null, 0);
        sQuery.setCriteria(annouCriteria.and(resCriteriaNullCri.or(resCriteria)));
        return sQuery;
    }
    
    public void updateAnnouncementDistributedTime(final Long collectionId, final JSONArray resourceList, final long time) throws DataAccessException {
        final SelectQuery sQuery = this.getAnnouncementResourceQuery(collectionId, JSONUtil.convertJSONArrayToList(resourceList));
        final DataObject dataObject = SyMUtil.getPersistence().get(sQuery);
        final Row announcementRow = dataObject.getFirstRow("Announcement");
        final Long announcementId = (Long)announcementRow.get("ANNOUNCEMENT_ID");
        for (int i = 0; i < resourceList.length(); ++i) {
            final Long resourceId = resourceList.getLong(i);
            Row announcementToResRow = dataObject.getRow("AnnouncementToResources", new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0));
            if (announcementToResRow == null) {
                announcementToResRow = new Row("AnnouncementToResources");
                announcementToResRow.set("ANNOUNCEMENT_ID", (Object)announcementId);
                announcementToResRow.set("RESOURCE_ID", (Object)resourceId);
                announcementToResRow.set("DISTRIBUTED_TIME", (Object)time);
                dataObject.addRow(announcementToResRow);
            }
            else {
                announcementToResRow.set("DISTRIBUTED_TIME", (Object)time);
                dataObject.updateRow(announcementToResRow);
            }
        }
        SyMUtil.getPersistence().update(dataObject);
    }
    
    public HashMap getAnnouncementSpecificDynamicVariable(final Long collectionId, final Long resourceId) throws DataAccessException {
        final HashMap hashMap = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProfileHistory"));
        selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
        selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "LAST_MODIFIED_TIME"));
        final Criteria resourceCriteria = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria collectionCriteria = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
        selectQuery.setCriteria(resourceCriteria.and(collectionCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row profileHistoryRow = dataObject.getRow("ResourceToProfileHistory");
            final Long time = (Long)profileHistoryRow.get("LAST_MODIFIED_TIME");
            hashMap.put("%last_distributred_time%", String.valueOf(time));
        }
        return hashMap;
    }
    
    protected void deleteAnnouncement(final long announcementId) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Announcement");
        final Criteria cAck = new Criteria(new Column("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
        deleteQuery.setCriteria(cAck);
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    public void deleteAnnouncement(final List announcementList) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Announcement");
        final Criteria cAck = new Criteria(new Column("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementList.toArray(), 8);
        deleteQuery.setCriteria(cAck);
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    public SelectQuery getCollectionAnnouncementQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "AnnouncementConfigData", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        return selectQuery;
    }
    
    public SelectQuery getAnnounceToResourceQuery() {
        final SelectQuery selectQuery = this.getCollectionAnnouncementQuery();
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        return selectQuery;
    }
    
    public JSONArray getCollectionProfileMapForAnnouncemnetList(final List<Long> announcementIdList) {
        final JSONArray collectionProfileMap = new JSONArray();
        try {
            final SelectQuery selectQuery = this.getCollectionAnnouncementQuery();
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "*"));
            selectQuery.addSelectColumn(new Column("ConfigDataItem", "*"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
            selectQuery.addSelectColumn(new Column("AnnouncementConfigData", "*"));
            final Criteria announcementCriteria = new Criteria(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"), (Object)announcementIdList.toArray(), 8);
            selectQuery.setCriteria(announcementCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ProfileToCollection");
                while (iterator.hasNext()) {
                    final JSONObject json = new JSONObject();
                    final Row profileRow = iterator.next();
                    final Long collectionId = (Long)profileRow.get("COLLECTION_ID");
                    final Long profileId = (Long)profileRow.get("PROFILE_ID");
                    final Long configDataId = (Long)dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
                    final Long announcementId = (Long)dataObject.getValue("AnnouncementConfigData", "ANNOUNCEMENT_ID", new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataId, 0));
                    json.put("ANNOUNCEMENT_ID", (Object)announcementId);
                    json.put("PROFILE_ID", (Object)profileId);
                    json.put("COLLECTION_ID", (Object)collectionId);
                    collectionProfileMap.put((Object)json);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getCollectionIdFromAnnouncementId", ex);
        }
        return collectionProfileMap;
    }
    
    public List<Long> getCollectionIdFromAnnouncementId(final List<Long> announcementIds) {
        final List<Long> collectionIds = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = this.getCollectionAnnouncementQuery();
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            final Criteria announcementCriteria = new Criteria(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"), (Object)announcementIds.toArray(), 8);
            selectQuery.setCriteria(announcementCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ProfileToCollection");
                while (iterator.hasNext()) {
                    final Row profileRow = iterator.next();
                    final Long collectionId = (Long)profileRow.get("COLLECTION_ID");
                    collectionIds.add(collectionId);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getCollectionIdFromAnnouncementId", (Throwable)e);
        }
        return collectionIds;
    }
    
    public HashMap<Long, Long> getCollectionIdsFromAnnouncement(final List<Long> announcementIds, final Long distributedTime, final Long resourceId) {
        final HashMap<Long, Long> collectionRel = new HashMap<Long, Long>();
        try {
            final SelectQuery selectQuery = this.getAnnounceToResourceQuery();
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"));
            final Criteria announcementCriteria = new Criteria(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"), (Object)announcementIds.toArray(), 8);
            Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            if (distributedTime != -1L) {
                resourceCriteria = resourceCriteria.and(new Criteria(new Column("ResourceToProfileHistory", "LAST_MODIFIED_TIME"), (Object)distributedTime, 7));
            }
            selectQuery.setCriteria(announcementCriteria.and(resourceCriteria));
            final DMDataSetWrapper wrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (wrapper.next()) {
                final Long collectionId = (Long)wrapper.getValue("COLLECTION_ID");
                final Long announcementId = (Long)wrapper.getValue("ANNOUNCEMENT_ID");
                collectionRel.put(announcementId, collectionId);
            }
        }
        catch (final Exception ex) {}
        return collectionRel;
    }
    
    public void addOrUpdateAnnouncementToResourceRelation(final List announcementIdsList, final List resourceList) throws Exception {
        final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 500);
        final List announcementSplitList = MDMUtil.getInstance().splitListIntoSubLists(announcementIdsList, 5);
        for (final Object resSplitSubListObj : resSplitList) {
            final List resSplitSubObj = (List)resSplitSubListObj;
            for (final Object announcementSplitSubListObj : announcementSplitList) {
                final List announcementSplitSubLis = (List)announcementSplitSubListObj;
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AnnouncementToResources"));
                sQuery.addSelectColumn(Column.getColumn("AnnouncementToResources", "*"));
                final Criteria annouCriteria = new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementSplitSubLis.toArray(), 8);
                final Criteria resCriteria = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resSplitSubObj.toArray(), 8);
                sQuery.setCriteria(annouCriteria.and(resCriteria));
                final DataObject dataObject = SyMUtil.getPersistence().get(sQuery);
                for (int j = 0; j < announcementSplitSubLis.size(); ++j) {
                    final Long announcementId = announcementSplitSubLis.get(j);
                    for (int i = 0; i < resSplitSubObj.size(); ++i) {
                        final Long resourceId = resSplitSubObj.get(i);
                        final Criteria resCri = new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0);
                        final Criteria announcementCri = new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementId, 0);
                        Row announcementToResRow = dataObject.isEmpty() ? null : dataObject.getRow("AnnouncementToResources", resCri.and(announcementCri));
                        if (announcementToResRow == null) {
                            announcementToResRow = new Row("AnnouncementToResources");
                            announcementToResRow.set("ANNOUNCEMENT_ID", (Object)announcementId);
                            announcementToResRow.set("RESOURCE_ID", (Object)resourceId);
                            dataObject.addRow(announcementToResRow);
                        }
                        else {
                            dataObject.updateRow(announcementToResRow);
                        }
                    }
                }
                SyMUtil.getPersistence().update(dataObject);
            }
        }
    }
    
    public void deleteAnnouncementToResourceRel(final List<Long> announcementIds, final Long resourceId) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AnnouncementToResources");
        deleteQuery.setCriteria(new Criteria(new Column("AnnouncementToResources", "ANNOUNCEMENT_ID"), (Object)announcementIds.toArray(), 8).and(new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0)));
        MDMUtil.getPersistenceLite().delete(deleteQuery);
    }
}
