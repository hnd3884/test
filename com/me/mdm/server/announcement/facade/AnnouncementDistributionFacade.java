package com.me.mdm.server.announcement.facade;

import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.LinkedHashMap;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.Map;
import com.me.mdm.server.device.DeviceFacade;
import java.util.Iterator;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.server.customgroup.GroupFacade;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.ems.framework.common.api.utils.APIException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.announcement.handler.AnnouncementDBHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIUtil;
import java.util.Collection;
import org.json.JSONArray;
import java.util.List;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.logging.Level;
import com.me.mdm.server.announcement.handler.AnnouncementAssociationHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AnnouncementDistributionFacade
{
    private final Logger logger;
    private static final int DEVICE_TYPE = 1;
    private static final int USER_TYPE = 2;
    private static final int GROUP_TYPE = 3;
    
    public AnnouncementDistributionFacade() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    public static AnnouncementDistributionFacade getNewInstance() {
        return new AnnouncementDistributionFacade();
    }
    
    public void distributeAnnouncementToDevices(final JSONObject apiRequestJSON) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "distribution-failed";
        try {
            final JSONObject distributionJson = this.checkAndGetDistributionJSON(apiRequestJSON, 1);
            secLog.put((Object)"ANNOUNCEMENT_IDs", (Object)distributionJson.optJSONArray("announcement_list"));
            secLog.put((Object)"DEVICE_IDs", (Object)distributionJson.optJSONArray("resource_list"));
            AnnouncementAssociationHandler.getNewInstance().associateProfileForDevice(distributionJson);
            remarks = "distribution-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISTRIBUTE_ANNOUNCEMENT", secLog);
        }
    }
    
    public void distributeAnnouncementToDevices(final Long customerID, final Long announcementId, final List deviceList, final Long profileId, final Long collectionId) throws Exception {
        this.logger.log(Level.INFO, "Associating the Announcement to devices Profile Id {0} GroupList:{1}", new Object[] { profileId, deviceList });
        final JSONArray announcmentJSONArray = new JSONArray();
        announcmentJSONArray.put((Object)announcementId);
        final JSONObject distributionJson = this.checkAndGetDistributionJSON(customerID, announcmentJSONArray, new JSONArray((Collection)deviceList), profileId, collectionId);
        AnnouncementAssociationHandler.getNewInstance().associateProfileForDevice(distributionJson);
    }
    
    private JSONObject setResListInAnnouncementMap(final JSONObject distributionJson) {
        final JSONArray resList = (JSONArray)distributionJson.get("resource_list");
        final JSONArray clonedList = new JSONArray(resList.toString());
        final JSONArray profile_announcement_map = (JSONArray)distributionJson.get("profile_announcement_map");
        final JSONArray profile_announcement_remap = new JSONArray();
        for (int i = 0; i < profile_announcement_map.length(); ++i) {
            final JSONObject json = (JSONObject)profile_announcement_map.get(i);
            json.put("resource_list", (Object)clonedList);
            profile_announcement_remap.put((Object)json);
        }
        return distributionJson;
    }
    
    public void disassociateAnnouncementFromDevice(final JSONObject apiRequestJSON) throws Exception {
        JSONObject distributionJson = this.checkAndGetDistributionJSON(apiRequestJSON, 1);
        distributionJson = this.setResListInAnnouncementMap(distributionJson);
        AnnouncementAssociationHandler.getNewInstance().disassociateProfileForDevice(distributionJson);
    }
    
    public void distributeAnnouncementToGroup(final JSONObject apiRequestJSON) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "distribution-failed";
        try {
            final JSONObject distributionJson = this.checkAndGetDistributionJSON(apiRequestJSON, 3);
            secLog.put((Object)"ANNOUNCEMENT_IDs", (Object)distributionJson.optJSONArray("announcement_list"));
            secLog.put((Object)"GROUP_IDs", (Object)distributionJson.optJSONArray("resource_list"));
            AnnouncementAssociationHandler.getNewInstance().associateProfileForGroup(distributionJson);
            remarks = "distribution-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISTRIBUTE_ANNOUNCEMENT", secLog);
        }
    }
    
    public void distributeAnnouncementToGroup(final Long customerID, final Long announcementId, final List groupList, final Long profileId, final Long collectionId) throws Exception {
        this.logger.log(Level.INFO, "Associating the Announcement to group Profile Id {0} GroupList:{1}", new Object[] { profileId, groupList });
        final JSONArray announcmentJSONArray = new JSONArray();
        announcmentJSONArray.put((Object)announcementId);
        final JSONObject distributionJson = this.checkAndGetDistributionJSON(customerID, announcmentJSONArray, new JSONArray((Collection)groupList), profileId, collectionId);
        AnnouncementAssociationHandler.getNewInstance().associateProfileForGroup(distributionJson);
    }
    
    public void disassociateAnnouncementFromGroup(final JSONObject apiRequestJSON) throws Exception {
        JSONObject distributionJson = this.checkAndGetDistributionJSON(apiRequestJSON, 3);
        distributionJson = this.setResListInAnnouncementMap(distributionJson);
        AnnouncementAssociationHandler.getNewInstance().disassociateProfileForGroup(distributionJson);
    }
    
    public void disassociateAnnouncementFromUser(final JSONObject apiRequestJSON) throws Exception {
        final JSONObject distributionJson = this.checkAndGetDistributionJSON(apiRequestJSON, 2);
        AnnouncementAssociationHandler.getNewInstance().disassociateProfileToManagedUser(distributionJson);
    }
    
    public void distributeAnnouncementToUser(final JSONObject apiRequestJSON) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "distribution-failed";
        try {
            final JSONObject distributionJson = this.checkAndGetDistributionJSON(apiRequestJSON, 2);
            secLog.put((Object)"ANNOUNCEMENT_IDs", (Object)distributionJson.optJSONArray("announcement_list"));
            secLog.put((Object)"USER_IDs", (Object)distributionJson.optJSONArray("resource_list"));
            AnnouncementAssociationHandler.getNewInstance().associateProfileToManagedUser(distributionJson);
            remarks = "distribution-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISTRIBUTE_ANNOUNCEMENT", secLog);
        }
    }
    
    private JSONObject checkAndGetDistributionJSON(final JSONObject apiRequestJSON, final int type) throws Exception {
        final Long customerID = APIUtil.getCustomerID(apiRequestJSON);
        final Long announcementId = JSONUtil.optLongForUVH(apiRequestJSON.getJSONObject("msg_header").getJSONObject("resource_identifier"), "announcement_id", (Long)null);
        final JSONObject messageBody = apiRequestJSON.getJSONObject("msg_body");
        JSONArray deviceArray = null;
        if (type == 1) {
            deviceArray = messageBody.getJSONArray("device_ids");
        }
        else if (type == 3) {
            deviceArray = messageBody.getJSONArray("group_ids");
        }
        else {
            deviceArray = messageBody.getJSONArray("user_ids");
        }
        JSONArray announcementArray = new JSONArray();
        if (announcementId == null) {
            announcementArray = messageBody.getJSONArray("announcement_ids");
        }
        else {
            announcementArray.put((Object)announcementId);
        }
        return this.checkAndGetDistributionJSON(customerID, announcementArray, deviceArray, null, null);
    }
    
    private JSONObject checkAndGetDistributionJSON(final Long customerID, final JSONArray announcementArray, final JSONArray resourceArray, final Long profileId, final Long collectionId) throws Exception {
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        final JSONObject baseJsonObject = new JSONObject();
        final Long userID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
        baseJsonObject.put("user_id", (Object)userID);
        baseJsonObject.put("announcement_list", (Object)announcementArray);
        final JSONArray profileJSONArray = new JSONArray();
        final JSONArray collectionJSONArray = new JSONArray();
        final JSONArray announcementJSONArray = new JSONArray();
        final List annArray = JSONUtil.getInstance().convertLongJSONArrayTOList(announcementArray);
        final JSONArray profileAnnouncementMapJson = announcementHandler.getCollectionIdsForAnnouncement(annArray);
        for (int i = 0; i < profileAnnouncementMapJson.length(); ++i) {
            final JSONObject json = (JSONObject)profileAnnouncementMapJson.get(i);
            profileJSONArray.put(json.get("PROFILE_ID"));
            collectionJSONArray.put(json.get("COLLECTION_ID"));
            announcementJSONArray.put(json.get("ANNOUNCEMENT_ID"));
        }
        baseJsonObject.put("profile_list", (Object)profileJSONArray);
        baseJsonObject.put("collection_list", (Object)collectionJSONArray);
        baseJsonObject.put("announcement_list", (Object)announcementJSONArray);
        baseJsonObject.put("profile_announcement_map", (Object)profileAnnouncementMapJson);
        baseJsonObject.put("customer_id", (Object)customerID);
        baseJsonObject.put("marked_for_delete", (Object)Boolean.FALSE);
        baseJsonObject.put("resource_list", (Object)JSONUtil.convertToLongJSONArray(resourceArray));
        return baseJsonObject;
    }
    
    public JSONObject getAnnouncementToGroupsDistributionDetails(final JSONObject apiRequestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final PagingUtil pagingUtil = apiUtil.getPagingParams(apiRequestJSON);
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        final Long customerID = APIUtil.getCustomerID(apiRequestJSON);
        final Long announcementId = JSONUtil.optLongForUVH(apiRequestJSON.getJSONObject("msg_header").getJSONObject("resource_identifier"), "announcement_id", (Long)null);
        if (announcementId == -1L || !announcementHandler.isCustomerEligible(customerID, announcementId)) {
            throw new APIException("COM0028");
        }
        final JSONObject profileJson = announcementHandler.getCollectionIdForAnnouncement(announcementId);
        final Long profileId = profileJson.getLong("PROFILE_ID");
        final String search = apiRequestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final boolean selectAll = APIUtil.getBooleanFilter(apiRequestJSON, "select_all", false);
        SelectQuery groupProfileQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForGroup"));
        final JSONObject responseJSON = new JSONObject();
        final JSONArray groupJSONArray = new JSONArray();
        final Table subTable = Table.getTable("CustomGroup", "GROUP");
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(subTable);
        final Join groupMemberJoin = new Join(subTable, Table.getTable("CustomGroupMemberRel"), new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        subQuery.addJoin(groupMemberJoin);
        subQuery.addSelectColumn(Column.getColumn("GROUP", "RESOURCE_ID"));
        final Column groupMemberCountColumn = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").count();
        groupMemberCountColumn.setColumnAlias("COUNT");
        subQuery.addSelectColumn(groupMemberCountColumn);
        final List columns = new ArrayList();
        columns.add(Column.getColumn("GROUP", "RESOURCE_ID"));
        final GroupByClause groupByClause = new GroupByClause(columns);
        subQuery.setGroupByClause(groupByClause);
        final DerivedTable derivedTable = new DerivedTable("GROUP", (Query)subQuery);
        groupProfileQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Table baseTable = Table.getTable("CustomGroup");
        final Join derivedTableJoin = new Join(baseTable, (Table)derivedTable, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        groupProfileQuery.addJoin(derivedTableJoin);
        groupProfileQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        groupProfileQuery.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
        groupProfileQuery.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
        groupProfileQuery.addSelectColumn(new Column("CustomGroup", "RESOURCE_ID"));
        groupProfileQuery.addSelectColumn(new Column("CustomGroup", "GROUP_TYPE"));
        groupProfileQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        groupProfileQuery.addSelectColumn(new Column("Resource", "NAME"));
        groupProfileQuery.addSelectColumn(new Column("Resource", "DOMAIN_NETBIOS_NAME"));
        groupProfileQuery.addSelectColumn(Column.getColumn("GROUP", "COUNT"));
        groupProfileQuery.setCriteria(new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0));
        groupProfileQuery = RBDAUtil.getInstance().getRBDAQuery(groupProfileQuery);
        final SelectQuery countQuery = (SelectQuery)groupProfileQuery.clone();
        final ArrayList<Column> selectColumnsList = (ArrayList<Column>)countQuery.getSelectColumns();
        for (final Column selectColumn : selectColumnsList) {
            countQuery.removeSelectColumn(selectColumn);
        }
        Column countColumn = new Column("CustomGroup", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        countQuery.addSelectColumn(countColumn);
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false);
            Criteria criteria = groupProfileQuery.getCriteria().and(searchCriteria);
            groupProfileQuery.setCriteria(criteria);
            criteria = countQuery.getCriteria().and(searchCriteria);
            countQuery.setCriteria(criteria);
        }
        final int count = DBUtil.getRecordCount(countQuery);
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", count);
        responseJSON.put("metadata", (Object)meta);
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
            if (!selectAll) {
                groupProfileQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("groupname")) {
                        groupProfileQuery.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    groupProfileQuery.addSortColumn(new SortColumn("CustomGroup", "RESOURCE_ID", true));
                }
            }
            final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(groupProfileQuery);
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                groupJSONArray.put((Object)GroupFacade.getGroupJSON(tempJSON));
            }
        }
        responseJSON.put("groups", (Object)groupJSONArray);
        return responseJSON;
    }
    
    public JSONObject getAnnouncementToDeviceDistributionDetails(final JSONObject apiRequestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        final Long customerID = APIUtil.getCustomerID(apiRequestJSON);
        final Long announcementId = JSONUtil.optLongForUVH(apiRequestJSON.getJSONObject("msg_header").getJSONObject("resource_identifier"), "announcement_id", (Long)null);
        if (announcementId == -1L || !announcementHandler.isCustomerEligible(customerID, announcementId)) {
            throw new APIException("COM0028");
        }
        final PagingUtil pagingUtil = apiUtil.getPagingParams(apiRequestJSON);
        final String platform = apiRequestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("platform", "--");
        final boolean selectAll = APIUtil.getBooleanFilter(apiRequestJSON, "select_all", false);
        final String search = apiRequestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject profileJson = announcementHandler.getCollectionIdForAnnouncement(announcementId);
        final Long profileId = profileJson.getLong("PROFILE_ID");
        final JSONObject summaryJSON = new JSONObject();
        final Table recentProfile = new Table("RecentProfileForResource");
        final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        SelectQuery deviceProfileQuery = (SelectQuery)new SelectQueryImpl(recentProfile);
        deviceProfileQuery.addJoin(managedDeviceJoin);
        final Join managedDeviceExtnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        deviceProfileQuery.addJoin(managedDeviceExtnJoin);
        deviceProfileQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        deviceProfileQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        deviceProfileQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        deviceProfileQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria removeCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        deviceProfileQuery.setCriteria(profileCriteria.and(removeCriteria).and(managedDeviceCriteria));
        deviceProfileQuery = RBDAUtil.getInstance().getRBDAQuery(deviceProfileQuery);
        final SelectQuery countQuery = (SelectQuery)deviceProfileQuery.clone();
        final ArrayList<Column> selectColumnsList = (ArrayList<Column>)deviceProfileQuery.getSelectColumns();
        for (final Column selectColumn : selectColumnsList) {
            countQuery.removeSelectColumn(selectColumn);
        }
        Column countColumn = new Column("ManagedDevice", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        countQuery.addSelectColumn(countColumn);
        if (!platform.equalsIgnoreCase("--")) {
            final String[] filters = platform.split(",");
            final List platFormList = new ArrayList();
            for (final String filterKey : filters) {
                final int platformType = DeviceFacade.getPlatformType(filterKey);
                platFormList.add(platformType);
            }
            Criteria platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormList.toArray(), 8);
            platformTypeCriteria = deviceProfileQuery.getCriteria().and(platformTypeCriteria);
            deviceProfileQuery.setCriteria(platformTypeCriteria);
            platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormList.toArray(), 8);
            platformTypeCriteria = countQuery.getCriteria().and(platformTypeCriteria);
            countQuery.setCriteria(platformTypeCriteria);
        }
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)search, 12, false);
            Criteria criteria = deviceProfileQuery.getCriteria().and(searchCriteria);
            deviceProfileQuery.setCriteria(criteria);
            criteria = countQuery.getCriteria().and(searchCriteria);
            countQuery.setCriteria(criteria);
        }
        final int count = DBUtil.getRecordCount(countQuery);
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", count);
        summaryJSON.put("metadata", (Object)meta);
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                summaryJSON.put("paging", (Object)pagingJSON);
            }
            if (!selectAll) {
                deviceProfileQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("devicename")) {
                        deviceProfileQuery.addSortColumn(new SortColumn("ManagedDeviceExtn", "NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    deviceProfileQuery.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
                }
            }
            final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(deviceProfileQuery);
            final JSONArray deviceJSONArray = new JSONArray();
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                deviceJSONArray.put((Map)tempJSON);
            }
            summaryJSON.put("devices", (Object)deviceJSONArray);
            if (apiRequestJSON.has("filters")) {
                summaryJSON.put("filters", (Object)String.valueOf(apiRequestJSON.get("filters")));
            }
        }
        else {
            summaryJSON.put("devices", (Object)new JSONArray());
        }
        return summaryJSON;
    }
    
    public JSONObject getAnnouncementToUserDistributionDetails(final JSONObject apiRequestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        final Long customerID = APIUtil.getCustomerID(apiRequestJSON);
        final Long announcementId = JSONUtil.optLongForUVH(apiRequestJSON.getJSONObject("msg_header").getJSONObject("resource_identifier"), "announcement_id", (Long)null);
        if (announcementId == -1L || !announcementHandler.isCustomerEligible(customerID, announcementId)) {
            throw new APIException("COM0028");
        }
        final PagingUtil pagingUtil = apiUtil.getPagingParams(apiRequestJSON);
        final boolean selectAll = APIUtil.getBooleanFilter(apiRequestJSON, "select_all", false);
        final String search = apiRequestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject profileJson = announcementHandler.getCollectionIdForAnnouncement(announcementId);
        final Long profileId = profileJson.getLong("PROFILE_ID");
        final JSONObject summaryJSON = new JSONObject();
        final Table recentProfile = new Table("RecentProfileForMDMResource");
        final Join managedDeviceJoin = new Join("RecentProfileForMDMResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final SelectQuery deviceProfileQuery = (SelectQuery)new SelectQueryImpl(recentProfile);
        deviceProfileQuery.addJoin(managedDeviceJoin);
        final Join managedDeviceExtnJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2);
        deviceProfileQuery.addJoin(managedDeviceExtnJoin);
        deviceProfileQuery.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"));
        deviceProfileQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        deviceProfileQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        final Criteria profileCriteria = new Criteria(new Column("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria removeCriteria = new Criteria(new Column("RecentProfileForMDMResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        deviceProfileQuery.setCriteria(profileCriteria.and(removeCriteria));
        final SelectQuery countQuery = (SelectQuery)deviceProfileQuery.clone();
        final ArrayList<Column> selectColumnsList = (ArrayList<Column>)deviceProfileQuery.getSelectColumns();
        for (final Column selectColumn : selectColumnsList) {
            countQuery.removeSelectColumn(selectColumn);
        }
        Column countColumn = new Column("Resource", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        countQuery.addSelectColumn(countColumn);
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false);
            Criteria criteria = deviceProfileQuery.getCriteria().and(searchCriteria);
            deviceProfileQuery.setCriteria(criteria);
            criteria = countQuery.getCriteria().and(searchCriteria);
            countQuery.setCriteria(criteria);
        }
        final int count = DBUtil.getRecordCount(countQuery);
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", count);
        summaryJSON.put("metadata", (Object)meta);
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                summaryJSON.put("paging", (Object)pagingJSON);
            }
            if (!selectAll) {
                deviceProfileQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("username")) {
                        deviceProfileQuery.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    deviceProfileQuery.addSortColumn(new SortColumn("Resource", "RESOURCE_ID", true));
                }
            }
            final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(deviceProfileQuery);
            final JSONArray deviceJSONArray = new JSONArray();
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                deviceJSONArray.put((Map)tempJSON);
            }
            summaryJSON.put("users", (Object)deviceJSONArray);
            if (apiRequestJSON.has("filters")) {
                summaryJSON.put("filters", (Object)String.valueOf(apiRequestJSON.get("filters")));
            }
        }
        else {
            summaryJSON.put("users", (Object)new JSONArray());
        }
        return summaryJSON;
    }
    
    private JSONArray putResListForAnnouncement(final JSONArray jsonArray, final HashMap profileResMap) {
        JSONObject json = new JSONObject();
        final JSONArray newArray = new JSONArray();
        for (int j = 0; j < jsonArray.length(); ++j) {
            json = (JSONObject)jsonArray.get(j);
            final Long profileId = (Long)json.get("PROFILE_ID");
            final List resList = profileResMap.get(profileId);
            json.put("resource_list", (Collection)resList);
            newArray.put((Object)json);
        }
        return newArray;
    }
    
    public void moveProfilesToTrash(final Long customerID, final List announcementList) throws Exception {
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        final AnnouncementAssociationHandler announcementAssociationHandler = AnnouncementAssociationHandler.getNewInstance();
        final JSONArray profileAnnouncementMapJson = announcementHandler.getCollectionIdsForAnnouncement(announcementList);
        final JSONArray profileJSONArray = new JSONArray();
        final JSONArray collectionJSONArray = new JSONArray();
        final JSONArray announcementJSONArray = new JSONArray();
        for (int i = 0; i < profileAnnouncementMapJson.length(); ++i) {
            final JSONObject json = (JSONObject)profileAnnouncementMapJson.get(i);
            profileJSONArray.put(json.get("PROFILE_ID"));
            collectionJSONArray.put(json.get("COLLECTION_ID"));
            announcementJSONArray.put(json.get("ANNOUNCEMENT_ID"));
        }
        final JSONObject baseJsonObject = new JSONObject();
        baseJsonObject.put("profile_list", (Object)profileJSONArray);
        baseJsonObject.put("collection_list", (Object)collectionJSONArray);
        baseJsonObject.put("announcement_list", (Object)announcementJSONArray);
        final Long userID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
        baseJsonObject.put("user_id", (Object)userID);
        final HashMap profileGroupMap = new HashMap();
        for (int j = 0; j < profileJSONArray.length(); ++j) {
            final Long profileId = (Long)profileJSONArray.get(j);
            final Iterator groupRowIter = DBUtil.getRowsFromDB("RecentProfileForGroup", "PROFILE_ID", (Object)profileId);
            final List localGrpList = new ArrayList();
            while (groupRowIter != null && groupRowIter.hasNext()) {
                final Row profileRow = groupRowIter.next();
                localGrpList.add(profileRow.get("GROUP_ID"));
            }
            profileGroupMap.put(profileId, localGrpList);
        }
        this.putResListForAnnouncement(profileAnnouncementMapJson, profileGroupMap);
        baseJsonObject.put("is_delete", true);
        baseJsonObject.put("profile_announcement_map", (Object)profileAnnouncementMapJson);
        baseJsonObject.put("customer_id", (Object)customerID);
        baseJsonObject.put("marked_for_delete", (Object)Boolean.FALSE);
        announcementAssociationHandler.disassociateProfileForGroup(baseJsonObject);
        final HashMap profileResMap = new HashMap();
        for (int k = 0; k < profileJSONArray.length(); ++k) {
            final Long profileId2 = (Long)profileJSONArray.get(k);
            final Iterator resRowIter = DBUtil.getRowsFromDB("RecentProfileForResource", "PROFILE_ID", (Object)profileId2);
            final List localResList = new ArrayList();
            while (resRowIter != null && resRowIter.hasNext()) {
                final Row profileRow2 = resRowIter.next();
                localResList.add(profileRow2.get("RESOURCE_ID"));
            }
            profileResMap.put(profileId2, localResList);
        }
        this.putResListForAnnouncement(profileAnnouncementMapJson, profileResMap);
        baseJsonObject.put("profile_announcement_map", (Object)profileAnnouncementMapJson);
        announcementAssociationHandler.disassociateProfileForDevice(baseJsonObject);
        for (int k = 0; k < profileJSONArray.length(); ++k) {
            final Long profileId2 = (Long)profileJSONArray.get(k);
            final JSONObject profileDetails = new JSONObject();
            profileDetails.put("PROFILE_ID", (Object)profileId2);
            profileDetails.put("IS_MOVED_TO_TRASH", true);
            ProfileHandler.addOrUpdateProfile(profileDetails);
        }
    }
    
    public boolean redistributeAnnouncement(final Long customerID, final Long announcementId, final Long profileId, final Long collectionId) throws Exception {
        final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
        this.logger.log(Level.INFO, "Announcement moved to trash.Profile Ids:{0}", profileId);
        boolean isRedistributed = false;
        final LinkedHashMap grpToCOllectionMap = (LinkedHashMap)ProfileUtil.getInstance().getManagedGroupsAssignedForProfile(profileId);
        final List groupList = new ArrayList(grpToCOllectionMap.keySet());
        final List groupResList = MDMGroupHandler.getMemberIdListForGroups(groupList, 120);
        final HashMap deviceMap = (HashMap)ProfileUtil.getInstance().getManagedDevicesAssignedForProfile(profileId);
        final List deviceList = new ArrayList(deviceMap.keySet());
        deviceList.removeAll(groupResList);
        if (!groupList.isEmpty()) {
            this.distributeAnnouncementToGroup(customerID, announcementId, groupList, profileId, collectionId);
            isRedistributed = true;
        }
        if (!deviceList.isEmpty()) {
            this.distributeAnnouncementToDevices(customerID, announcementId, deviceList, profileId, collectionId);
            isRedistributed = true;
        }
        return isRedistributed;
    }
}
