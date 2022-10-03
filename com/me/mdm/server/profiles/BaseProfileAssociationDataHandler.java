package com.me.mdm.server.profiles;

import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.me.mdm.server.config.ProfileAssociateHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;

public abstract class BaseProfileAssociationDataHandler
{
    public DataObject profileDO;
    public DataObject finalDO;
    public DataObject existingResourceProfileDO;
    public DataObject existingGroupProfileDO;
    public Integer chunckSize;
    public Integer profileLenSize;
    public Boolean processAsChucks;
    public Logger logger;
    
    public BaseProfileAssociationDataHandler() {
        this.profileDO = null;
        this.finalDO = null;
        this.existingResourceProfileDO = null;
        this.existingGroupProfileDO = null;
        this.processAsChucks = Boolean.FALSE;
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public abstract void associateProfileForGroup(final JSONObject p0) throws Exception;
    
    public abstract void associateProfileForDevice(final JSONObject p0) throws Exception;
    
    public abstract void associateProfileToMDMResource(final JSONObject p0) throws Exception;
    
    public abstract void disassociateProfileToMDMResource(final JSONObject p0) throws Exception;
    
    public abstract void disassociateProfileForGroup(final JSONObject p0) throws Exception;
    
    public abstract void disassociateProfileForDevice(final JSONObject p0) throws Exception;
    
    public void addOrUpdateRecentProfileForResource(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray resourceJson = requestJSON.getJSONArray("resource_list");
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final Boolean markForDelete = requestJSON.optBoolean("marked_for_delete", false);
            final ArrayList resourceList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(resourceJson);
            final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, ((boolean)this.processAsChucks) ? ((int)this.chunckSize) : resourceList.size());
            for (final List curResList : resSplitList) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
                final Criteria resCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)curResList.toArray(), 8);
                final Criteria collnCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
                selectQuery.setCriteria(resCriteria.and(collnCriteria));
                if (this.processAsChucks) {
                    this.existingResourceProfileDO = MDMUtil.getPersistenceLite().get(selectQuery);
                }
                for (final Object resourceId : curResList) {
                    Row resRow = null;
                    if (!this.existingResourceProfileDO.isEmpty()) {
                        final Criteria cResource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
                        final Criteria cProfile = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
                        resRow = this.existingResourceProfileDO.getRow("RecentProfileForResource", cResource.and(cProfile));
                    }
                    if (resRow == null) {
                        if (markForDelete) {
                            continue;
                        }
                        resRow = new Row("RecentProfileForResource");
                        resRow.set("RESOURCE_ID", (Object)resourceId);
                        resRow.set("PROFILE_ID", (Object)profileId);
                        resRow.set("COLLECTION_ID", (Object)collectionId);
                        resRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                        this.finalDO.addRow(resRow);
                    }
                    else {
                        resRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                        resRow.set("COLLECTION_ID", (Object)collectionId);
                        this.finalDO.updateBlindly(resRow);
                    }
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateRecentProfileForResource()    >   Error   ", e);
            throw e;
        }
    }
    
    public void addOrUpdateResourceProfileHistory(final JSONObject requestJSON) throws JSONException, DataAccessException {
        final JSONArray resourceJson = requestJSON.getJSONArray("resource_list");
        final Long profileId = requestJSON.getLong("profile_id");
        final Long collectionId = requestJSON.getLong("collection_id");
        final Long userId = requestJSON.getLong("user_id");
        final Boolean markForDelete = requestJSON.optBoolean("marked_for_delete", false);
        final Boolean profileOrigin = requestJSON.optBoolean("profile_origin");
        try {
            final ArrayList resourceList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(resourceJson);
            final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, ((boolean)this.processAsChucks) ? ((int)this.chunckSize) : resourceList.size());
            final Iterator iterator = resSplitList.iterator();
            final long currentTime = System.currentTimeMillis();
            while (iterator.hasNext()) {
                final List curResList = iterator.next();
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProfileHistory"));
                selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "*"));
                final Criteria resCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"), (Object)curResList.toArray(), 8);
                final Criteria collnCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
                selectQuery.setCriteria(resCriteria.and(collnCriteria));
                if (this.processAsChucks) {
                    this.existingResourceProfileDO = MDMUtil.getPersistenceLite().get(selectQuery);
                }
                for (final Object resourceId : curResList) {
                    Row historyRow = null;
                    if (!this.existingResourceProfileDO.isEmpty()) {
                        final Criteria cResource = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
                        final Criteria cCollection = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                        historyRow = this.existingResourceProfileDO.getRow("ResourceToProfileHistory", cResource.and(cCollection));
                    }
                    if (historyRow == null) {
                        if (markForDelete) {
                            continue;
                        }
                        historyRow = new Row("ResourceToProfileHistory");
                        historyRow.set("RESOURCE_ID", (Object)resourceId);
                        historyRow.set("PROFILE_ID", (Object)profileId);
                        historyRow.set("COLLECTION_ID", (Object)collectionId);
                        historyRow.set("ASSOCIATED_BY", (Object)userId);
                        historyRow.set("ASSOCIATED_TIME", (Object)currentTime);
                        historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                        historyRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                        historyRow.set("PROFILE_ORIGIN_TYPE", (Object)profileOrigin);
                        historyRow.set("REMARKS", (Object)"");
                        this.finalDO.addRow(historyRow);
                    }
                    else {
                        historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                        historyRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                        historyRow.set("PROFILE_ORIGIN_TYPE", (Object)profileOrigin);
                        historyRow.set("REMARKS", (Object)"");
                        if (markForDelete) {
                            historyRow.set("REMARKS", (Object)"disassociated");
                        }
                        else {
                            historyRow.set("REMARKS", (Object)"");
                        }
                        this.finalDO.updateBlindly(historyRow);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateResourceProfileHistory()    >   Error   ", e);
            throw e;
        }
    }
    
    public void addOrUpdateCollnToResources(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            final Long collectionId = requestJSON.getLong("collection_id");
            final JSONArray resourceJson = requestJSON.getJSONArray("resource_list");
            final ArrayList resourceList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(resourceJson);
            final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, ((boolean)this.processAsChucks) ? ((int)this.chunckSize) : resourceList.size());
            final Iterator iterator = resSplitList.iterator();
            final String remarks = requestJSON.optString("remarks", "--");
            final int status = requestJSON.getInt("status");
            final Boolean markedForDelete = requestJSON.getBoolean("marked_for_delete");
            while (iterator.hasNext()) {
                final List curResList = iterator.next();
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
                selectQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
                selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "*"));
                selectQuery.addSelectColumn(Column.getColumn("MDMCollnToResErrorCode", "*"));
                final Criteria resCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)curResList.toArray(), 8);
                final Criteria collnCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
                selectQuery.setCriteria(resCriteria.and(collnCriteria));
                if (this.processAsChucks) {
                    this.existingResourceProfileDO = MDMUtil.getPersistenceLite().get(selectQuery);
                }
                for (final Object resourceId : curResList.toArray()) {
                    Row collectionRow = null;
                    Row collectionErrorRow = null;
                    if (!this.existingResourceProfileDO.isEmpty()) {
                        final Criteria cResource = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
                        final Criteria cCollection = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
                        collectionRow = this.existingResourceProfileDO.getRow("CollnToResources", cResource.and(cCollection));
                        collectionErrorRow = this.existingResourceProfileDO.getRow("MDMCollnToResErrorCode", cResource.and(cCollection));
                    }
                    if (collectionRow == null) {
                        if (!markedForDelete) {
                            this.logger.log(Level.INFO, "Inserting resource {0} and collection {1} in CollnToResources", new Object[] { resourceId, collectionId });
                            final long currentTime = System.currentTimeMillis();
                            collectionRow = new Row("CollnToResources");
                            collectionRow.set("COLLECTION_ID", (Object)collectionId);
                            collectionRow.set("RESOURCE_ID", (Object)resourceId);
                            collectionRow.set("STATUS", (Object)status);
                            collectionRow.set("APPLIED_TIME", (Object)currentTime);
                            collectionRow.set("AGENT_APPLIED_TIME", (Object)currentTime);
                            collectionRow.set("REMARKS", (Object)remarks);
                            collectionRow.set("REMARKS_EN", (Object)"--");
                            this.finalDO.addRow(collectionRow);
                        }
                    }
                    else {
                        final long currentTime = System.currentTimeMillis();
                        collectionRow.set("STATUS", (Object)status);
                        collectionRow.set("APPLIED_TIME", (Object)currentTime);
                        collectionRow.set("AGENT_APPLIED_TIME", (Object)currentTime);
                        collectionRow.set("REMARKS", (Object)remarks);
                        collectionRow.set("REMARKS_EN", (Object)"--");
                        this.finalDO.updateBlindly(collectionRow);
                    }
                    if (collectionErrorRow != null) {
                        this.finalDO.deleteRow(collectionErrorRow);
                    }
                }
            }
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateCollnToResources error ".concat(e.toString()), requestJSON);
            throw e;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateCollnToResources error ".concat(e.toString()), requestJSON);
        }
    }
    
    public void deleteRecentProfileForResource(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final JSONArray resourceList = requestJSON.getJSONArray("resource_list");
            final List resList = new ArrayList();
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceList, i, -1L);
                resList.add(resourceId);
            }
            final Criteria cResource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resList.toArray(), 8);
            final Criteria cProfile = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria cCollection = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
            MDMUtil.getPersistence().delete(cProfile.and(cResource).and(cCollection));
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- deleteRecentProfileForResource() >   Error   ", e);
            throw e;
        }
    }
    
    public void deleteRecentProfileForGroup(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final JSONArray resourceList = requestJSON.getJSONArray("resource_list");
            final List resList = new ArrayList();
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceList, i, -1L);
                resList.add(resourceId);
            }
            final Criteria cResource = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)resList.toArray(), 8);
            final Criteria cProfile = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria cCollection = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionId, 0);
            MDMUtil.getPersistence().delete(cProfile.and(cResource).and(cCollection));
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- deleteRecentProfileForResource() >   Error   ", e);
            throw e;
        }
    }
    
    public abstract void handleDirectProfileRemoval(final JSONObject p0) throws JSONException;
    
    public void updateGrouptoProfileDetails(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            this.addOrUpdateRecentProfileForGroup(requestJSON);
            this.addOrUpdateGroupToProfileHistory(requestJSON);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- updateGrouptoProfileDetails() >   Error   ", e);
            throw e;
        }
    }
    
    public void addOrUpdateRecentProfileForGroup(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final List groupList = new ArrayList();
            final JSONArray groupJSONArray = requestJSON.getJSONArray("resource_list");
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final Boolean markedForDelete = requestJSON.getBoolean("marked_for_delete");
            for (int i = 0; i < groupJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(groupJSONArray, i, -1L);
                groupList.add(resourceId);
            }
            for (final Object groupId : groupList) {
                Row recRow = null;
                if (!this.existingGroupProfileDO.isEmpty()) {
                    final Criteria cGroup = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
                    final Criteria cProfile = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
                    recRow = this.existingGroupProfileDO.getRow("RecentProfileForGroup", cGroup.and(cProfile));
                }
                if (recRow == null) {
                    recRow = new Row("RecentProfileForGroup");
                    recRow.set("GROUP_ID", (Object)groupId);
                    recRow.set("PROFILE_ID", (Object)profileId);
                    recRow.set("COLLECTION_ID", (Object)collectionId);
                    recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                    this.finalDO.addRow(recRow);
                }
                else {
                    recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                    recRow.set("COLLECTION_ID", (Object)collectionId);
                    this.existingGroupProfileDO.updateRow(recRow);
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateRecentProfileForGroup() >   Error   ", e);
            throw e;
        }
    }
    
    public void addOrUpdateGroupToProfileHistory(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray groupJSONArray = requestJSON.getJSONArray("resource_list");
            final List groupList = new ArrayList();
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final Long userId = requestJSON.getLong("user_id");
            Boolean markedForDelete = false;
            for (int i = 0; i < groupJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(groupJSONArray, i, -1L);
                groupList.add(resourceId);
            }
            for (final Object groupId : groupList) {
                Row historyRow = null;
                if (!this.existingGroupProfileDO.isEmpty()) {
                    final Criteria cGroup = new Criteria(new Column("GroupToProfileHistory", "GROUP_ID"), (Object)groupId, 0);
                    final Criteria cProfile = new Criteria(new Column("GroupToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
                    final Criteria cCollection = new Criteria(new Column("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                    historyRow = this.existingGroupProfileDO.getRow("GroupToProfileHistory", cGroup.and(cProfile).and(cCollection));
                }
                if (historyRow == null) {
                    historyRow = new Row("GroupToProfileHistory");
                    historyRow.set("GROUP_ID", (Object)groupId);
                    historyRow.set("PROFILE_ID", (Object)profileId);
                    historyRow.set("COLLECTION_ID", (Object)collectionId);
                    historyRow.set("ASSOCIATED_BY", (Object)userId);
                    historyRow.set("COLLECTION_STATUS", (Object)2);
                    historyRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("REMARKS", (Object)"");
                    this.finalDO.addRow(historyRow);
                }
                else {
                    markedForDelete = requestJSON.optBoolean("marked_for_delete", (boolean)Boolean.FALSE);
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("COLLECTION_STATUS", (Object)2);
                    if (markedForDelete) {
                        historyRow.set("REMARKS", (Object)"disassociated");
                    }
                    else {
                        historyRow.set("REMARKS", (Object)"");
                    }
                    this.existingGroupProfileDO.updateRow(historyRow);
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateGroupToProfileHistory() >   Error   ", e);
            throw e;
        }
    }
    
    public void getExistingGroupProfileDO(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            final JSONArray profileJSONArray = requestJSON.getJSONArray("profile_list");
            final List profileList = new ArrayList();
            for (int i = 0; i < profileJSONArray.length(); ++i) {
                final Long profileId = JSONUtil.optLongForUVH(profileJSONArray, i, -1L);
                profileList.add(profileId);
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            sQuery.addJoin(new Join("Profile", "RecentProfileForGroup", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            sQuery.addJoin(new Join("Profile", "GroupToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            sQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "GROUP_HISTORY_ID"));
            sQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "GROUP_ID"));
            sQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "PROFILE_ID"));
            sQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "COLLECTION_ID"));
            sQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
            sQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            sQuery.setCriteria(cProfile);
            this.existingGroupProfileDO = MDMUtil.getPersistence().get(sQuery);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- getExistingGroupProfileDO() >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public void getExistingDeviceProfileDO(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            if (this.processAsChucks) {
                this.existingResourceProfileDO = null;
            }
            else {
                final JSONArray resourceJSONArray = requestJSON.getJSONArray("resource_list");
                final JSONArray profileJSONArray = requestJSON.getJSONArray("profile_list");
                final List resourceList = new ArrayList();
                final List profileList = new ArrayList();
                for (int i = 0; i < resourceJSONArray.length(); ++i) {
                    final Long resourceId = resourceJSONArray.getLong(i);
                    resourceList.add(resourceId);
                }
                for (int i = 0; i < profileJSONArray.length(); ++i) {
                    final Long profileId = profileJSONArray.getLong(i);
                    profileList.add(profileId);
                }
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToProfileHistory"));
                sQuery.addJoin(new Join("ResourceToProfileHistory", "RecentProfileForResource", new String[] { "RESOURCE_ID", "PROFILE_ID" }, new String[] { "RESOURCE_ID", "PROFILE_ID" }, 1));
                sQuery.addJoin(new Join("ResourceToProfileHistory", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
                sQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
                sQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
                sQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                sQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
                sQuery.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("MDMCollnToResErrorCode", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("MDMCollnToResErrorCode", "COLLECTION_ID"));
                sQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "COLLECTION_ID"));
                final Criteria cGroup = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria cProfile = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileList.toArray(), 8);
                sQuery.setCriteria(cGroup.and(cProfile));
                this.existingResourceProfileDO = MDMUtil.getPersistence().get(sQuery);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- getExistingDeviceProfileDO() >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public void getExistingMDMResourceProfileDO(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray resourceJSONArray = requestJSON.getJSONArray("resource_list");
            final JSONArray profileJSONArray = requestJSON.getJSONArray("profile_list");
            final List resourceList = new ArrayList();
            final List profileList = new ArrayList();
            for (int i = 0; i < resourceJSONArray.length(); ++i) {
                resourceList.add(resourceJSONArray.getLong(i));
            }
            for (int i = 0; i < profileJSONArray.length(); ++i) {
                profileList.add(profileJSONArray.getLong(i));
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            sQuery.addJoin(new Join("Profile", "RecentProfileForMDMResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            sQuery.addJoin(new Join("Profile", "MDMResourceToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            sQuery.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"));
            sQuery.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MDMResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
            sQuery.addSelectColumn(Column.getColumn("MDMResourceToProfileHistory", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MDMResourceToProfileHistory", "PROFILE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MDMResourceToProfileHistory", "COLLECTION_ID"));
            final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Criteria resourceIDCriteria = new Criteria(new Column("MDMResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            sQuery.setCriteria(cProfile.and(resourceIDCriteria));
            this.existingResourceProfileDO = MDMUtil.getPersistence().get(sQuery);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getExistingMDMResourceProfileDO() >   Error   ", e);
            throw e;
        }
    }
    
    public abstract void disassociateProfileToDirectoryUser(final JSONObject p0) throws Exception;
    
    public abstract void disassociateProfileToManagedUser(final JSONObject p0) throws Exception;
    
    public abstract void associateProfileToDirectoryUser(final JSONObject p0) throws Exception;
    
    public abstract void associateProfileToManagedUser(final JSONObject p0) throws Exception;
    
    public void updateMDMResourceToProfileDetails(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            this.addOrUpdateRecentProfileForMDMResource(requestJSON);
            this.addOrUpdateMDMResourceToProfileHistory(requestJSON);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- updateMDMResourceToProfileDetails() >   Error   ", e);
            throw e;
        }
    }
    
    private void addOrUpdateMDMResourceToProfileHistory(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray resourceJSONArray = requestJSON.getJSONArray("resource_list");
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            Long loggedInUserId = requestJSON.optLong("user_id", 0L);
            final int profileOrigin = requestJSON.optInt("profile_origin", 0);
            for (int i = 0; i < resourceJSONArray.length(); ++i) {
                final Long resourceId = resourceJSONArray.getLong(i);
                requestJSON.put("resource_id", (Object)resourceId);
                if (loggedInUserId == 0L) {
                    loggedInUserId = this.getAssociatedByForProfile(requestJSON);
                }
                Row historyRow = null;
                if (!this.existingResourceProfileDO.isEmpty()) {
                    final Criteria cUser = new Criteria(new Column("MDMResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria cProfile = new Criteria(new Column("MDMResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
                    final Criteria cCollection = new Criteria(new Column("MDMResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                    historyRow = this.existingResourceProfileDO.getRow("MDMResourceToProfileHistory", cUser.and(cProfile).and(cCollection));
                }
                if (historyRow == null) {
                    historyRow = new Row("MDMResourceToProfileHistory");
                    historyRow.set("RESOURCE_ID", (Object)resourceId);
                    historyRow.set("PROFILE_ID", (Object)profileId);
                    historyRow.set("COLLECTION_ID", (Object)collectionId);
                    historyRow.set("ASSOCIATED_BY", (Object)loggedInUserId);
                    historyRow.set("COLLECTION_STATUS", (Object)2);
                    historyRow.set("PROFILE_ORIGIN", (Object)profileOrigin);
                    historyRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("REMARKS", (Object)"");
                    this.finalDO.addRow(historyRow);
                }
                else {
                    historyRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    historyRow.set("COLLECTION_STATUS", (Object)2);
                    historyRow.set("REMARKS", (Object)"");
                    this.finalDO.updateBlindly(historyRow);
                }
            }
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateMDMResourceToProfileHistory() >   Error   ", e);
            throw e;
        }
    }
    
    private void addOrUpdateRecentProfileForMDMResource(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray resourceJSONArray = requestJSON.getJSONArray("resource_list");
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final Boolean markedForDelete = requestJSON.optBoolean("marked_for_delete", (boolean)Boolean.FALSE);
            Long loggedInUserId = requestJSON.getLong("user_id");
            for (int i = 0; i < resourceJSONArray.length(); ++i) {
                final Long resourceId = resourceJSONArray.getLong(i);
                if (loggedInUserId == null) {
                    loggedInUserId = ProfileAssociateHandler.getInstance().getAssociatedByForProfile(profileId, resourceId);
                }
                Row recRow = null;
                if (!this.existingResourceProfileDO.isEmpty()) {
                    final Criteria cResource = new Criteria(new Column("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria cProfile = new Criteria(new Column("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
                    recRow = this.existingResourceProfileDO.getRow("RecentProfileForMDMResource", cResource.and(cProfile));
                }
                if (recRow == null) {
                    recRow = new Row("RecentProfileForMDMResource");
                    recRow.set("RESOURCE_ID", (Object)resourceId);
                    recRow.set("PROFILE_ID", (Object)profileId);
                    recRow.set("COLLECTION_ID", (Object)collectionId);
                    recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                    recRow.set("ASSOCIATED_BY", (Object)loggedInUserId);
                    recRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                    recRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                    recRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    this.finalDO.addRow(recRow);
                }
                else {
                    recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                    recRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                    recRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                    this.finalDO.updateBlindly(recRow);
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateRecentProfileForMDMResource() >   Error   ", e);
            throw e;
        }
    }
    
    public Long getAssociatedByForProfile(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            final Long profileId = requestJSON.getLong("profile_id");
            final Long resourceId = requestJSON.getLong("resource_id");
            final Criteria cr1 = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cr2 = new Criteria(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("RecentProfileForMDMResource", cr1.and(cr2));
            Long associatedBy = null;
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("RecentProfileForMDMResource");
                associatedBy = (Long)row.get("ASSOCIATED_BY");
            }
            return associatedBy;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getAssociatedByForProfile() >   Error   ", e);
            throw e;
        }
    }
    
    public boolean hasIOSNativeAppForIOSResource(final Long resourceId) throws DataAccessException {
        boolean result = false;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("IOSNativeAppStatus"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("IOSNativeAppStatus", "RESOURCE_ID"), (Object)resourceId, 0));
        selectQuery.addSelectColumn(Column.getColumn("IOSNativeAppStatus", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("IOSNativeAppStatus");
        if (iterator.hasNext()) {
            final Row row = iterator.next();
            result = ((int)row.get("INSTALLATION_STATUS") == 1);
            return result;
        }
        return true;
    }
    
    public void deleteRecentProfileForMDMResource(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final Long profileId = requestJSON.getLong("profile_id");
            final Long collectionId = requestJSON.getLong("collection_id");
            final JSONArray resourceList = requestJSON.getJSONArray("resource_list");
            final List resList = new ArrayList();
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceList, i, -1L);
                resList.add(resourceId);
            }
            final Criteria cResource = new Criteria(new Column("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)resList.toArray(), 8);
            final Criteria cProfile = new Criteria(new Column("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria cCollection = new Criteria(new Column("RecentProfileForMDMResource", "COLLECTION_ID"), (Object)collectionId, 0);
            MDMUtil.getPersistence().delete(cProfile.and(cResource).and(cCollection));
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- deleteRecentProfileForResource() >   Error   ", e);
            throw e;
        }
    }
    
    public void updateGroupCollectionStatusSummary(final JSONObject requestJSON) throws Exception {
        final JSONArray groupJSONArray = requestJSON.getJSONArray("resource_list");
        final List groupList = new ArrayList();
        final Long profileId = requestJSON.getLong("profile_id");
        final Long collectionId = requestJSON.getLong("collection_id");
        for (int i = 0; i < groupJSONArray.length(); ++i) {
            final Long resourceId = JSONUtil.optLongForUVH(groupJSONArray, i, -1L);
            groupList.add(resourceId);
        }
        if (groupList.size() > 0) {
            for (int i = 0; i < groupList.size(); ++i) {
                GroupCollectionStatusSummary.getInstance().addOrUpdateGroupCollectionStatusSummary(groupList.get(i), collectionId);
            }
        }
    }
    
    public void evaluateChunkFlag(final List allDevice, final List profileList) {
        if (allDevice.size() > this.chunckSize && profileList.size() > this.profileLenSize) {
            this.processAsChucks = Boolean.TRUE;
        }
    }
}
