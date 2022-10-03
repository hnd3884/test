package com.me.uem.announcement;

import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.Collection;
import java.util.HashMap;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.i18n.I18N;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.api.APIUtil;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AnnouncementHandler
{
    AnnouncementDBController dbController;
    AnnouncementJSONController jsonController;
    private final Logger logger;
    private static final String ANNOUNCEMENT_INSTALL_JSON = "announcement_install.json";
    
    public AnnouncementHandler() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
        this.dbController = new AnnouncementDBController();
        this.jsonController = new AnnouncementJSONController();
    }
    
    public static AnnouncementHandler newInstance() {
        return new AnnouncementHandler();
    }
    
    public long addorUpdateAnnouncement(final JSONObject announcementJSON) throws DataAccessException, AnnouncementException {
        long announcement_id = announcementJSON.optLong("announcement_id", -1L);
        try {
            this.validateAnnouncementInfo(announcementJSON);
            DataObject announcementDO;
            if (announcement_id != -1L) {
                announcementDO = this.dbController.getAnnouncementDO(announcement_id);
            }
            else {
                announcementDO = (DataObject)new WritableDataObject();
            }
            this.dbController.addAnnouncementRow(announcementDO, announcementJSON);
            this.dbController.addAnnouncementImageRow(announcementDO, announcementJSON);
            this.dbController.addAnnouncementDetailRow(announcementDO, announcementJSON);
            final DataObject updatedDO = SyMUtil.getPersistence().update(announcementDO);
            final Row announcementRow = updatedDO.getRow("Announcement");
            announcement_id = (long)announcementRow.get("ANNOUNCEMENT_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while modifying announcement", e);
            throw e;
        }
        return announcement_id;
    }
    
    public JSONObject getAnnouncementInfo(final long announcementId) throws Exception {
        JSONObject annJson = new JSONObject();
        final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(-1L, announcementId, -1, -1, -1L, false);
        final List columnList = new ArrayList();
        columnList.add(new Column("Announcement", "ANNOUNCEMENT_FORMAT"));
        columnList.add(new Column("Announcement", "ANNOUNCEMENT_NAME"));
        columnList.add(new Column("AnnouncementDetail", "*"));
        columnList.add(new Column("AnnouncementImageDetails", "IMAGE_URL"));
        columnList.add(new Column("AnnouncementConfigData", "CONFIG_DATA_ITEM_ID"));
        columnList.add(new Column("Collection", "COLLECTION_ID"));
        columnList.add(new Column("Profile", "PROFILE_ID"));
        columnList.add(new Column("Profile", "LAST_MODIFIED_TIME"));
        columnList.add(new Column("Profile", "IS_MOVED_TO_TRASH"));
        columnList.add(new Column("Profile", "CREATION_TIME"));
        columnList.add(Column.getColumn("CREATED_USER", "FIRST_NAME", "created_by_user"));
        columnList.add(Column.getColumn("LAST_MODIFIED_USER", "FIRST_NAME", "last_modified_by_user"));
        sQuery.addSelectColumns(columnList);
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (dataSet.next()) {
                annJson = this.jsonController.getAnnouncementJSONFromDataSet(dataSet);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting announcement", e);
            throw e;
        }
        return annJson;
    }
    
    public JSONObject getAllAnnouncementInfo(final long customerId, final int startIndex, final int limit, final long timestamp, final JSONObject apiRequest) throws Exception {
        final JSONObject announcementJSON = new JSONObject();
        final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(customerId, -1L, startIndex, limit, timestamp, false);
        final String search = APIUtil.optStringFilter(apiRequest, "search", null);
        final List columnList = new ArrayList();
        columnList.add(new Column("Announcement", "ANNOUNCEMENT_FORMAT"));
        columnList.add(new Column("Announcement", "ANNOUNCEMENT_NAME"));
        columnList.add(new Column("AnnouncementDetail", "*"));
        columnList.add(new Column("AnnouncementImageDetails", "IMAGE_URL"));
        columnList.add(new Column("AnnouncementConfigData", "CONFIG_DATA_ITEM_ID"));
        columnList.add(new Column("Collection", "COLLECTION_ID"));
        columnList.add(new Column("Profile", "PROFILE_ID"));
        columnList.add(new Column("Profile", "LAST_MODIFIED_TIME"));
        columnList.add(new Column("Profile", "IS_MOVED_TO_TRASH"));
        columnList.add(new Column("Profile", "CREATION_TIME"));
        columnList.add(Column.getColumn("CREATED_USER", "FIRST_NAME", "created_by_user"));
        columnList.add(Column.getColumn("LAST_MODIFIED_USER", "FIRST_NAME", "last_modified_by_user"));
        sQuery.addSelectColumns(columnList);
        Criteria criteria = sQuery.getCriteria();
        final Criteria filterCriteria = new Criteria(Column.getColumn("Announcement", "ANNOUNCEMENT_NAME"), (Object)search, 12, false);
        if (search != null) {
            criteria = ((criteria == null) ? filterCriteria : criteria.and(filterCriteria));
            sQuery.setCriteria(criteria);
        }
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)sQuery);
            final JSONArray announcementArry = this.jsonController.getArrayFromAnnouncementDS(dataSet);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", announcementArry.length());
            announcementJSON.put("metadata", (Object)meta);
            announcementJSON.put("announcement", (Object)announcementArry);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while getting announcement", (Throwable)e);
            throw e;
        }
        return announcementJSON;
    }
    
    public JSONArray getNBarIcons() throws Exception {
        final JSONArray imageJsonArray = new JSONArray();
        JSONObject imageJson = new JSONObject();
        final String imagePath = "/images/announcement/nbaricon/";
        imageJson.put("type", (Object)"alert");
        imageJson.put("url", (Object)(imagePath + "alert.png"));
        imageJson.put("title", (Object)I18N.getMsg("mdm.announcement.type_alert", new Object[0]));
        imageJsonArray.put((Object)imageJson);
        imageJson = new JSONObject();
        imageJson.put("type", (Object)"information");
        imageJson.put("url", (Object)(imagePath + "info.png"));
        imageJson.put("title", (Object)I18N.getMsg("mdm.announcement.type_info", new Object[0]));
        imageJsonArray.put((Object)imageJson);
        imageJson = new JSONObject();
        imageJson.put("type", (Object)"warning");
        imageJson.put("url", (Object)(imagePath + "warning.png"));
        imageJson.put("title", (Object)I18N.getMsg("mdm.announcement.type_warning", new Object[0]));
        imageJsonArray.put((Object)imageJson);
        imageJson = new JSONObject();
        imageJson.put("type", (Object)"announcement");
        imageJson.put("url", (Object)(imagePath + "announcement.png"));
        imageJson.put("title", (Object)I18N.getMsg("mdm.announcement", new Object[0]));
        imageJsonArray.put((Object)imageJson);
        return imageJsonArray;
    }
    
    public JSONObject getAnnouncementDistributedInfo(final long announcementId, final JSONObject annJson) throws Exception {
        final Long profileId = (Long)annJson.get("PROFILE_ID");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        sQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID").count());
        final Criteria recentProfileForRes = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria recentProfileForResNotDeleteCri = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria managedDeviceCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        sQuery.setCriteria(recentProfileForRes.and(recentProfileForResNotDeleteCri).and(managedDeviceCri));
        final Criteria recentProfileForGroup = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria recentProfileForGroupNotDeleteCri = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        final int distributedDeviceCount = DBUtil.getRecordCount(sQuery, "RecentProfileForResource", "RESOURCE_ID");
        final int distributedGroupCount = DBUtil.getRecordCount("RecentProfileForGroup", "GROUP_ID", recentProfileForGroup.and(recentProfileForGroupNotDeleteCri));
        annJson.put("no_of_devices_distributed", distributedDeviceCount);
        annJson.put("no_of_groups_distributed", distributedGroupCount);
        final Criteria profileIDCri = new Criteria(Column.getColumn("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
        Long lastDistributedTime = (Long)DBUtil.getMaxOfValue("ResourceToProfileHistory", "ASSOCIATED_TIME", profileIDCri);
        if (lastDistributedTime == null) {
            lastDistributedTime = -1L;
        }
        annJson.put("last_distributed_time", (Object)lastDistributedTime);
        return annJson;
    }
    
    public JSONObject getAnnouncementCollectionInfo(final long announcementId) throws DataAccessException {
        final JSONObject annJson = new JSONObject();
        final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(-1L, announcementId, -1, -1, -1L, false);
        sQuery.addSelectColumns(this.dbController.getAnnouncementCollectionColumnList());
        try {
            final DataObject DO = SyMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Row colRow = DO.getFirstRow("Collection");
                annJson.put("COLLECTION_ID", (long)colRow.get("COLLECTION_ID"));
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while getting announcement", (Throwable)e);
            throw e;
        }
        return annJson;
    }
    
    public int getAnnouncementCount(final long customerId) throws Exception {
        int count = 0;
        try {
            final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(customerId, -1L, -1, -1, -1L, false);
            Column annColumn = new Column("Announcement", "ANNOUNCEMENT_ID");
            annColumn = annColumn.distinct();
            annColumn = annColumn.count();
            sQuery.addSelectColumn(annColumn);
            count = DBUtil.getRecordCount(sQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting announcement count", e);
            throw e;
        }
        return count;
    }
    
    public boolean isCustomerEligible(final long customerId, final long announcementId) throws DataAccessException {
        try {
            final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(customerId, announcementId, -1, -1, -1L, false);
            sQuery.addSelectColumns(this.dbController.getAnnouncementColumnList());
            final DataObject DO = SyMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking whether customer eligible for announcement edit", e);
            throw e;
        }
        return false;
    }
    
    public boolean isCustomerEligible(final long customerId, final List announcementIdList) throws Exception {
        try {
            final String chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_chunk_size");
            final int chunckSize = (chunkSizeStr == null) ? 500 : Integer.parseInt(chunkSizeStr);
            final List announcementSplitList = MDMUtil.getInstance().splitListIntoSubLists(announcementIdList, chunckSize);
            for (final List curAnnouncementList : announcementSplitList) {
                final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(customerId, -1L, -1, -1, -1L, false);
                sQuery.addSelectColumns(this.dbController.getAnnouncementColumnList());
                final Criteria cri = sQuery.getCriteria().and(this.dbController.getAnnouncementCriteria(curAnnouncementList));
                sQuery.setCriteria(cri);
                final DataObject DO = SyMUtil.getPersistence().get(sQuery);
                final Iterator<Row> rows = DO.getRows("Announcement");
                final int count = this.getCountForIterator(rows);
                if (count != curAnnouncementList.size()) {
                    return false;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking whether customer eligible for announcement List", e);
            throw e;
        }
        return true;
    }
    
    private int getCountForIterator(final Iterator iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            ++count;
            iterator.next();
        }
        return count;
    }
    
    public boolean isAnnouncementAlive(final long announcementId) throws DataAccessException, QueryConstructionException {
        try {
            final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(-1L, announcementId, -1, -1, -1L, SyMUtil.getCurrentTime(), false);
            sQuery.addSelectColumns(this.dbController.getAnnouncementColumnList());
            final String selectQuery = RelationalAPI.getInstance().getSelectSQL((Query)sQuery);
            final DataObject DO = SyMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking whether customer eligible for announcement edit", e);
            throw e;
        }
        return false;
    }
    
    public void updateAcknowledgment(final JSONObject anJSON, final long resourceId) throws Exception {
        try {
            final JSONArray announcementArray = anJSON.getJSONArray("announcements");
            final HashMap<Long, Long> announcementMap = new HashMap<Long, Long>();
            for (int i = 0; i < announcementArray.length(); ++i) {
                final JSONObject announcementJSON = announcementArray.getJSONObject(i);
                final long announcementId = announcementJSON.getLong("announcement_id");
                final long ackTime = announcementJSON.optLong("acknowledged_time", System.currentTimeMillis());
                announcementMap.put(announcementId, ackTime);
            }
            final List<Long> announcementIds = new ArrayList<Long>(announcementMap.keySet());
            final List<Long> collectionIds = this.dbController.getCollectionIdFromAnnouncementId(announcementIds);
            this.logger.log(Level.INFO, "Going to update status for ack.CollectionId:{0} announcementIds:{1} resourceId:{2}", new Object[] { collectionIds, announcementIds, resourceId });
            final String remarks = I18N.getMsg("mdm.announcement.acknowledgedInDevice", new Object[0]);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionIds, 962, remarks);
            this.dbController.updateAnnouncementAcknowledgment(announcementMap, resourceId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while adding announcement acknowledgement", e);
            throw e;
        }
    }
    
    public void updateAnnouncementRead(final JSONObject anJSON, final long resourceId) throws Exception {
        try {
            final JSONArray announcementArray = anJSON.getJSONArray("announcements");
            final HashMap<Long, Long> announcementMap = new HashMap<Long, Long>();
            for (int i = 0; i < announcementArray.length(); ++i) {
                final JSONObject announcementJSON = announcementArray.getJSONObject(i);
                final long announcementId = announcementJSON.getLong("announcement_id");
                final long readTime = announcementJSON.optLong("read_time", System.currentTimeMillis());
                announcementMap.put(announcementId, readTime);
            }
            final List<Long> announcementIds = new ArrayList<Long>(announcementMap.keySet());
            final List<Long> collectionIds = this.dbController.getCollectionIdFromAnnouncementId(announcementIds);
            this.logger.log(Level.INFO, "Going to update status for read.CollectionId:{0} announcementIds:{1} resourceId:{2}", new Object[] { collectionIds, announcementIds, resourceId });
            final String remarks = I18N.getMsg("mdm.announcement.readInDevice", new Object[0]);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionIds, 961, remarks);
            this.dbController.updateAnnouncementRead(announcementMap, resourceId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateAnnouncementRead", e);
            throw e;
        }
    }
    
    public void updateAnnouncementDelivery(final long collectionId, final long resourceId) throws DataAccessException {
        try {
            final long deliveredTime = System.currentTimeMillis();
            this.dbController.updateAnnouncementDelivery(collectionId, resourceId, deliveredTime);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in updateAnnouncementDelivery", (Throwable)e);
        }
    }
    
    public void updateAnnouncementDistributedTime(final Long collectionId, final JSONArray resourceArray, final long time) throws DataAccessException {
        try {
            this.dbController.updateAnnouncementDistributedTime(collectionId, resourceArray, time);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in updateAnnouncementDelivery", (Throwable)e);
        }
    }
    
    public void addOrUpdateAnnouncementToResourceRelation(final Long announcementId, final List resourceList) {
        try {
            final ArrayList announcementList = new ArrayList();
            announcementList.add(announcementId);
            this.dbController.addOrUpdateAnnouncementToResourceRelation(announcementList, resourceList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in add or update announcement", e);
        }
    }
    
    public void addOrUpdateAnnouncementToResourceRelation(final List announcementList, final List resourceList) {
        try {
            this.dbController.addOrUpdateAnnouncementToResourceRelation(announcementList, resourceList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in add or update announcement", e);
        }
    }
    
    public void deleteAnnouncement(final long announcementId) throws DataAccessException {
        try {
            this.dbController.deleteAnnouncement(announcementId);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while adding announcement acknowledgement", (Throwable)e);
            throw e;
        }
    }
    
    public void deleteAnnouncement(final List announcementList) throws DataAccessException {
        try {
            this.dbController.deleteAnnouncement(announcementList);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while adding announcement acknowledgement", (Throwable)e);
            throw e;
        }
    }
    
    private void validateAnnouncementInfo(final JSONObject announcementJSON) throws AnnouncementException {
        final int announcementFormat = announcementJSON.getInt("announcement_format");
        final JSONObject announcementDetailsJson = announcementJSON.getJSONObject("announcement_detail");
        this.validateAnnouncementDetails(announcementFormat, announcementDetailsJson);
    }
    
    private void validateAnnouncementSpan(final JSONObject announcementSpanJson) throws AnnouncementException {
        final long startTime = announcementSpanJson.optLong("start_time");
        final long endTime = announcementSpanJson.optLong("end_time");
        final long currentTime = SyMUtil.getCurrentTime();
        if (startTime != 0L && endTime != 0L && startTime > endTime) {
            throw new AnnouncementException("Invalid start and end time");
        }
        if (endTime != 0L && endTime < currentTime) {
            throw new AnnouncementException("Invalid end time");
        }
    }
    
    private void validateAnnouncementDetails(final int announcementFormat, final JSONObject announcementDetails) throws AnnouncementException {
        final String nbar = announcementDetails.getString("nbar_message");
        final String details = announcementDetails.getString("detail_message");
        final String title = announcementDetails.optString("title");
        if (announcementFormat == 1) {
            if ((title == null || title.isEmpty()) && (nbar == null || nbar.isEmpty())) {
                throw new AnnouncementException("Invalid information for notification bar");
            }
        }
        else {
            if (announcementFormat == 2 && (details == null || details.isEmpty())) {
                throw new AnnouncementException("Invalid information for detailed message");
            }
            if (announcementFormat == 4) {
                if (title == null || title.isEmpty() || nbar == null || nbar.isEmpty()) {
                    throw new AnnouncementException("Invalid information for notification bar");
                }
            }
            else {
                if (announcementFormat != 3) {
                    throw new AnnouncementException("Invalid announcement format");
                }
                if ((title == null || title.isEmpty()) && (nbar == null || nbar.isEmpty())) {
                    throw new AnnouncementException("Invalid information for notification bar");
                }
                if (details == null || details.isEmpty()) {
                    throw new AnnouncementException("Invalid information for detailed message");
                }
            }
        }
    }
    
    public JSONObject getAnnouncementAsPushMessage(final Long announcementId) throws Exception {
        final JSONObject announcementJSON = this.getAnnouncementInfo(announcementId).getJSONObject("announcement_detail");
        final JSONObject messageJSON = new JSONObject();
        messageJSON.put("title", announcementJSON.get("title"));
        messageJSON.put("message", announcementJSON.get("nbar_message"));
        messageJSON.put("announcement_id", (Object)announcementId);
        messageJSON.put("nbar_icon", announcementJSON.get("nbar_icon"));
        messageJSON.put("nbar_icon_id", announcementJSON.get("ANNOUNCEMENT_IMG_ID"));
        messageJSON.put("type", 2);
        messageJSON.put("category", 1);
        final String nbar = announcementJSON.optString("nbar_message");
        if (nbar != null) {
            messageJSON.put("message", (Object)nbar);
        }
        else {
            messageJSON.put("message", announcementJSON.get("detail_message"));
        }
        return messageJSON;
    }
    
    public HashSet<Long> getTrashedAnnouncement(final ArrayList announcementIdList) throws Exception {
        final SelectQuery sQuery = this.dbController.getAnnouncementConfigSelectQuery(-1L, -1L, -1, -1, -1L, true);
        sQuery.addSelectColumns(this.dbController.getAnnouncementCollectionColumnList());
        final Criteria cri = sQuery.getCriteria().and(this.dbController.getAnnouncementCriteria(announcementIdList));
        sQuery.setCriteria(cri);
        try {
            final DataObject DO = SyMUtil.getPersistence().get(sQuery);
            final HashSet<Long> trashedProfiles = new HashSet<Long>();
            if (!DO.isEmpty()) {
                final Iterator<Row> rows = DO.getRows("Announcement");
                while (rows.hasNext()) {
                    trashedProfiles.add((Long)rows.next().get("ANNOUNCEMENT_ID"));
                }
            }
            return trashedProfiles;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in  getting getTrashedAnnouncement", e);
            throw e;
        }
    }
    
    public void processAnnouncementStatusForResource(final Long resourceId, final JSONObject requestJSON) throws DataAccessException {
        final List<Long> addedList = JSONUtil.getInstance().convertLongJSONArrayTOList(requestJSON.optJSONArray("added_announcement"));
        final List<Long> modifiedList = JSONUtil.getInstance().convertLongJSONArrayTOList(requestJSON.optJSONArray("modified_announcement"));
        final List<Long> deletedList = JSONUtil.getInstance().convertLongJSONArrayTOList(requestJSON.optJSONArray("deleted_announcement"));
        final Long syncTime = requestJSON.optLong("LastSyncTime", -1L);
        final Long deliveredTime = requestJSON.getLong("delivered_time");
        addedList.addAll(modifiedList);
        final List<Long> resourceIds = new ArrayList<Long>();
        resourceIds.add(resourceId);
        final int succeeded = 6;
        if (!addedList.isEmpty()) {
            final HashMap<Long, Long> announcementCollectionRelMap = new AnnouncementDBController().getCollectionIdsFromAnnouncement(addedList, syncTime, resourceId);
            final List<Long> collectionIds = new ArrayList<Long>(announcementCollectionRelMap.values());
            final List<Long> announcementIds = new ArrayList<Long>(announcementCollectionRelMap.keySet());
            final String successRemarks = "mdm.announcement.distributed_announcement";
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIds, collectionIds, succeeded, successRemarks);
            this.dbController.updateAnnouncementDelivery(announcementIds, resourceId, deliveredTime);
        }
        if (!deletedList.isEmpty() && syncTime != -1L) {
            final String removeRemarks = "mdm.announcement.removed_announcement";
            final HashMap<Long, Long> deleteMap = new AnnouncementDBController().getCollectionIdsFromAnnouncement(deletedList, syncTime, resourceId);
            final List<Long> deletedCollectionIds = new ArrayList<Long>(deleteMap.values());
            final List<Long> deletedAnnouncementId = new ArrayList<Long>(deleteMap.keySet());
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIds, deletedCollectionIds, succeeded, removeRemarks);
            new OSUpdatePolicyHandler().deleteRecentProfileForResourceListCollection(resourceIds, deletedCollectionIds);
            this.dbController.deleteAnnouncementToResourceRel(deletedAnnouncementId, resourceId);
        }
        ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
    }
    
    public HashMap getDynamicVariableForAnnouncement(final Long collectionId, final Long resourceId) throws DataAccessException {
        final HashMap announcementInfo = this.dbController.getAnnouncementSpecificDynamicVariable(collectionId, resourceId);
        return announcementInfo;
    }
    
    public void publishAnnouncement(final JSONObject profileJSON, final JSONObject announcementJSON) throws Exception {
        final long customerID = profileJSON.getLong("CUSTOMER_ID");
        final long collectionID = profileJSON.getLong("COLLECTION_ID");
        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
        final JSONObject publishedJSON = new JSONObject(announcementJSON.toString());
        final JSONObject publishedDetail = publishedJSON.getJSONObject("announcement_detail");
        publishedDetail.put("nbar_icon_id", publishedDetail.getLong("ANNOUNCEMENT_IMG_ID"));
        publishedDetail.put("nbar_icon", (Object)("https://%ServerName%:%ServerPort%" + publishedDetail.getString("nbar_icon")));
        publishedDetail.remove("ANNOUNCEMENT_IMG_ID");
        final String profilePath = mdmProfileDir + File.separator + "announcement_install.json";
        ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, publishedJSON.toString().getBytes());
    }
    
    public JSONObject getAnnouncementDetailForCollection(final Long collectionId, final Long customerId, final Long resourceId, final String deviceUDID) throws DataAccessException {
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerId, collectionId);
        final String installCmdRelPath = mdmProfileRelativeDirPath + File.separator + "announcement_install.json";
        final String clientDataParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
        final String completePath = clientDataParentDir + File.separator + installCmdRelPath;
        String strQuery = PayloadHandler.getInstance().readProfileFromFile(completePath);
        final HashMap dynamicMap = newInstance().getDynamicVariableForAnnouncement(collectionId, resourceId);
        strQuery = DynamicVariableHandler.replaceDynamicVariables(strQuery, deviceUDID);
        strQuery = DynamicVariableHandler.replaceDynamicVariable(strQuery, dynamicMap);
        return new JSONObject(strQuery);
    }
    
    public void addRemarksForiOSNotApplicableList(final List<Long> resourceList, final JSONArray collection_list) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(new Column("ManagedDevice", "AGENT_VERSION_CODE"));
        query.addSelectColumn(new Column("IOSNativeAppStatus", "RESOURCE_ID"));
        query.addSelectColumn(new Column("IOSNativeAppStatus", "INSTALLATION_STATUS"));
        query.setCriteria(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
        List higherVersion = new ArrayList();
        List appInstalled = new ArrayList();
        if (!dataObject.isEmpty()) {
            final Iterator appInstalledIterator = dataObject.getRows("IOSNativeAppStatus", new Criteria(new Column("IOSNativeAppStatus", "INSTALLATION_STATUS"), (Object)1, 0));
            appInstalled = MDMDBUtil.getColumnValuesAsList(appInstalledIterator, "RESOURCE_ID");
            resourceList.removeAll(appInstalled);
            final Iterator notApplicableDeviceCriteria = dataObject.getRows("ManagedDevice", new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)appInstalled.toArray(), 8).and(new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)1538, 7)));
            higherVersion = MDMDBUtil.getColumnValuesAsList(notApplicableDeviceCriteria, "RESOURCE_ID");
            appInstalled.removeAll(higherVersion);
        }
        for (int j = 0; j < collection_list.length(); ++j) {
            final Long collectionId = Long.parseLong(collection_list.get(j).toString());
            if (!higherVersion.isEmpty()) {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(higherVersion, collectionId, 12, "dc.db.mdm.apps.status.UpgradeApp");
            }
            if (!appInstalled.isEmpty()) {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(appInstalled, collectionId, 12, "mdm.announcment.ios.remarks.permission_not_given");
            }
            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceList, collectionId, 12, "mdm.collection.ios.resource.remarks");
        }
    }
    
    public HashMap<Long, String> getAnnouncementNames(final List<Long> announcementList) {
        final HashMap<Long, String> announcementMap = new HashMap<Long, String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Announcement"));
            selectQuery.addSelectColumn(new Column("Announcement", "ANNOUNCEMENT_NAME"));
            selectQuery.addSelectColumn(new Column("Announcement", "ANNOUNCEMENT_ID"));
            selectQuery.setCriteria(new Criteria(new Column("Announcement", "ANNOUNCEMENT_ID"), (Object)announcementList.toArray(), 8));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Announcement");
                while (iterator.hasNext()) {
                    final Row announcementRow = iterator.next();
                    final String name = (String)announcementRow.get("ANNOUNCEMENT_NAME");
                    final Long announcementId = (Long)announcementRow.get("ANNOUNCEMENT_ID");
                    announcementMap.put(announcementId, name);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "exception in getAnnouncementNames {0}", (Throwable)e);
        }
        return announcementMap;
    }
}
