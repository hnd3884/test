package com.adventnet.sym.server.mdm.group;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Map;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import com.me.mdm.server.tracker.MDMTrackerUtil;
import com.adventnet.ds.query.CaseExpression;
import com.me.idps.mdm.sync.MDMIdpsUtil;
import java.util.Stack;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Set;
import java.util.HashSet;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import com.me.mdm.server.inv.actions.InvActionUtil;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.me.mdm.server.tracker.mics.MICSGroupFeatureController;
import java.util.HashMap;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.android.knox.core.MDMCustomGroupHandler;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.idps.core.util.DirectoryUtil;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import org.apache.commons.lang3.time.DurationFormatUtils;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupInterface;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;

public class MDMGroupHandler extends CustomGroupingHandler implements CustomGroupInterface
{
    public static final int MDM_GROUP_TYPE = 3;
    @Deprecated
    public static final int MDM_IOS_GROUP_TYPE = 3;
    @Deprecated
    public static final int MDM_ANDROID_GROUP_TYPE = 4;
    @Deprecated
    public static final int MDM_WINDOWS_GROUP_TYPE = 5;
    public static final int MDM_INVISIBLE_GROUP_TYPE = 8;
    private static final String ADD_DEVICE = "ADDED_DEVICE";
    private static final String REMOVE_DEVICE = "REMOVE_DEVICE";
    public static final int MEMBER_ADDED = 1;
    public static final int MEMBER_REMOVED = 2;
    private static MDMGroupHandler mdmCgHandler;
    public static final int CUSTOM_GROUP_TYPE_COMPUTER = 1;
    public static final int CUSTOM_GROUP_TYPE_USER = 2;
    public static final int CUSTOM_GROUP_CTGY_STATIC = 1;
    public static final int CUSTOM_GROUP_CTGY_DYNAMIC = 2;
    public static final int CUSTOM_GROUP_CTGY_STATIC_UNIQUE = 5;
    public static final int CUSTOM_GROUP_ADDED = 1;
    public static final int CUSTOM_GROUP_MODIFIED = 2;
    public static final int CUSTOM_GROUP_DELETED = 3;
    public static final String DUMMY_CRITERIA_GROUP_DOMAIN = "CRITERIA_GROUP";
    public static final int PLATFORM_INDEPENDENT_GROUP_TYPE = 6;
    public static final int USER_GROUP_TYPE = 7;
    public static final int MIGRATION_GROUP_TYPE = 10;
    public static final int MDM_MODERN_MGMT_SCOPE_GROUP_TYPE = 9;
    public Logger profileDistributionLog;
    private static Logger logger;
    private List<MDMGroupMemberListener> memberListernerList;
    
    public static MDMGroupHandler getInstance() {
        if (MDMGroupHandler.mdmCgHandler == null) {
            MDMGroupHandler.mdmCgHandler = new MDMGroupHandler();
        }
        return MDMGroupHandler.mdmCgHandler;
    }
    
    private MDMGroupHandler() {
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
        this.memberListernerList = new ArrayList<MDMGroupMemberListener>();
    }
    
    public void addGroupMemberListener(final MDMGroupMemberListener memberListener) {
        this.memberListernerList.add(memberListener);
    }
    
    public void invokeGroupMemberListener(final MDMGroupMemberEvent groupEvent, final int operation) {
        this.profileDistributionLog.log(Level.INFO, "Invoking Group Listener with groupEvent : {0} and Operation : {1}", new Object[] { groupEvent, operation });
        final StringBuilder logMsg = new StringBuilder();
        logMsg.append(System.lineSeparator());
        final int l = this.memberListernerList.size();
        final int memberLength = groupEvent.memberIds.length;
        final String groupOpAction = (operation == 1) ? "added to" : " removed from";
        for (int s = 0; s < l; ++s) {
            final MDMGroupMemberListener listener = this.memberListernerList.get(s);
            final long start = System.currentTimeMillis();
            if (operation == 1) {
                listener.groupMemberAdded(groupEvent);
            }
            else if (operation == 2) {
                listener.groupMemberRemoved(groupEvent);
            }
            final long end = System.currentTimeMillis();
            final String groupMemberListenerName = listener.getClass().getSimpleName();
            String logMsgBase = "{0} Members {1} group:{2};{3}:duration{4}" + System.lineSeparator();
            logMsgBase = logMsgBase.replace("{0}", String.valueOf(memberLength));
            logMsgBase = logMsgBase.replace("{1}", groupOpAction);
            logMsgBase = logMsgBase.replace("{2}", String.valueOf(groupEvent.groupID));
            logMsgBase = logMsgBase.replace("{3}", groupMemberListenerName);
            logMsgBase = logMsgBase.replace("{4}", DurationFormatUtils.formatDurationHMS(end - start));
            logMsg.append(logMsgBase);
        }
        this.profileDistributionLog.log(Level.INFO, logMsg.toString());
        if (groupEvent.isMove == null || (groupEvent.isMove != null && !groupEvent.isMove)) {
            this.addMemeberToGroupEventLogEntry(groupEvent, operation);
        }
    }
    
    public void addGroup(final CustomGroupDetails cgDetails) {
        cgDetails.resourceId = this.addOrUpdateMDMGroupDB(cgDetails);
        final long lastUpdatedTime = System.currentTimeMillis();
        updateDBTime(cgDetails.resourceId, lastUpdatedTime);
    }
    
    public static void updateDBTime(final Long resourceID, final Long updatedTime) {
        try {
            final UpdateQuery s = (UpdateQuery)new UpdateQueryImpl("Resource");
            final Criteria c = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            s.setCriteria(c);
            s.setUpdateColumn("DB_UPDATED_TIME", (Object)new Long(updatedTime));
            MDMUtil.getPersistence().update(s);
        }
        catch (final Exception ex) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in updateDBTime....", ex);
        }
    }
    
    public JSONObject handleDeleteGroup(final HttpServletRequest request, Long[] resourceIds, final Long userId) {
        final JSONObject groupResponseJSON = new JSONObject();
        try {
            resourceIds = DirectoryUtil.getInstance().removeActiveADGroupFromList(resourceIds);
            this.handleBeforeDelete(request, resourceIds, groupResponseJSON);
            this.deleteGroup(resourceIds, userId);
            groupResponseJSON.put("IS_SUCCESS", true);
        }
        catch (final JSONException | DataAccessException ex) {
            try {
                groupResponseJSON.put("IS_SUCCESS", false);
            }
            catch (final JSONException ex2) {
                Logger.getLogger(MDMGroupHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
            }
        }
        return groupResponseJSON;
    }
    
    @Deprecated
    public void deleteGroup(final Long[] resourceIds) {
        try {
            final DataObject dobj = MDMUtil.getPersistence().get(MDMUtil.formSelectQuery("Resource", new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resourceIds, 8), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("Resource", "RESOURCE_ID"), Column.getColumn("Resource", "CUSTOMER_ID"), Column.getColumn("CustomGroup", "GROUP_TYPE"), Column.getColumn("CustomGroup", "RESOURCE_ID"), Column.getColumn("CustomGroup", "GROUP_CATEGORY"), Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2), new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1))), (Criteria)null));
            final List<GroupEvent> groupEvents = new ArrayList<GroupEvent>();
            final List<MDMGroupMemberEvent> memberEvents = new ArrayList<MDMGroupMemberEvent>();
            if (dobj != null && !dobj.isEmpty() && dobj.containsTable("Resource")) {
                final Iterator itr = dobj.getRows("Resource");
                while (itr != null && itr.hasNext()) {
                    final Row resourceRow = itr.next();
                    final Long groupResID = (Long)resourceRow.get("RESOURCE_ID");
                    final Long customerId = (Long)resourceRow.get("CUSTOMER_ID");
                    final List<Long> memberList = new ArrayList<Long>();
                    final Iterator membersItr = dobj.getRows("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResID, 0));
                    while (membersItr != null && membersItr.hasNext()) {
                        final Row cgMemberRelRow = membersItr.next();
                        memberList.add((Long)cgMemberRelRow.get("MEMBER_RESOURCE_ID"));
                    }
                    final Long[] memberIds = (memberList == null || memberList.isEmpty()) ? new Long[0] : memberList.toArray(new Long[memberList.size()]);
                    final Row customGroupRow = dobj.getRow("CustomGroup", new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupResID, 0));
                    final Integer groupType = (Integer)customGroupRow.get("GROUP_TYPE");
                    final Integer groupCategory = (Integer)customGroupRow.get("GROUP_CATEGORY");
                    final Properties props = new Properties();
                    ((Hashtable<String, Boolean>)props).put("noGroupCountUpdate", Boolean.TRUE);
                    final MDMGroupMemberEvent memberEvent = new MDMGroupMemberEvent(groupResID, memberIds);
                    memberEvent.groupType = groupType;
                    memberEvent.customerId = customerId;
                    memberEvent.groupProp = props;
                    memberEvents.add(memberEvent);
                    final GroupEvent groupEvent = new GroupEvent(groupResID, customerId, memberIds);
                    groupEvent.groupCategory = groupCategory;
                    groupEvents.add(groupEvent);
                }
            }
            final DeleteQuery memberRelDelQuery = (DeleteQuery)new DeleteQueryImpl("CustomGroupMemberRel");
            memberRelDelQuery.setCriteria(new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)resourceIds, 8));
            MDMUtil.getPersistence().delete(memberRelDelQuery);
            for (final MDMGroupMemberEvent memberEvent2 : memberEvents) {
                this.invokeGroupMemberListener(memberEvent2, 2);
            }
            final DeleteQuery groupDelQuery = (DeleteQuery)new DeleteQueryImpl("Resource");
            groupDelQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIds, 8).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)));
            MDMUtil.getPersistence().delete(groupDelQuery);
            for (final GroupEvent groupEvent2 : groupEvents) {
                MDMCustomGroupHandler.getInstance().invokeGroupListeners(groupEvent2, 2);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in MDMGroupHandler deleteGroup....", e);
        }
    }
    
    public Long[] deleteGroup(Long[] resourceIds, final Long userId) {
        try {
            resourceIds = DirectoryUtil.getInstance().removeActiveADGroupFromList(resourceIds);
            final DataObject dobj = MDMUtil.getPersistence().get(MDMUtil.formSelectQuery("Resource", new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resourceIds, 8), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("Resource", "RESOURCE_ID"), Column.getColumn("Resource", "CUSTOMER_ID"), Column.getColumn("CustomGroup", "GROUP_TYPE"), Column.getColumn("CustomGroup", "RESOURCE_ID"), Column.getColumn("CustomGroup", "GROUP_CATEGORY"), Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2), new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1))), (Criteria)null));
            final List<GroupEvent> groupEvents = new ArrayList<GroupEvent>();
            final List<MDMGroupMemberEvent> memberEvents = new ArrayList<MDMGroupMemberEvent>();
            if (dobj != null && !dobj.isEmpty() && dobj.containsTable("Resource")) {
                final Iterator itr = dobj.getRows("Resource");
                while (itr != null && itr.hasNext()) {
                    final Row resourceRow = itr.next();
                    final Long groupResID = (Long)resourceRow.get("RESOURCE_ID");
                    final Long customerId = (Long)resourceRow.get("CUSTOMER_ID");
                    final List<Long> memberList = new ArrayList<Long>();
                    final Iterator membersItr = dobj.getRows("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResID, 0));
                    while (membersItr != null && membersItr.hasNext()) {
                        final Row cgMemberRelRow = membersItr.next();
                        memberList.add((Long)cgMemberRelRow.get("MEMBER_RESOURCE_ID"));
                    }
                    final Long[] memberIds = (memberList == null || memberList.isEmpty()) ? new Long[0] : memberList.toArray(new Long[memberList.size()]);
                    final Row customGroupRow = dobj.getRow("CustomGroup", new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupResID, 0));
                    final Integer groupType = (Integer)customGroupRow.get("GROUP_TYPE");
                    final Integer groupCategory = (Integer)customGroupRow.get("GROUP_CATEGORY");
                    final Properties props = new Properties();
                    ((Hashtable<String, Boolean>)props).put("noGroupCountUpdate", Boolean.TRUE);
                    if (memberIds.length != 0) {
                        final MDMGroupMemberEvent memberEvent = new MDMGroupMemberEvent(groupResID, memberIds);
                        memberEvent.groupType = groupType;
                        memberEvent.customerId = customerId;
                        memberEvent.groupProp = props;
                        memberEvent.userId = userId;
                        memberEvents.add(memberEvent);
                    }
                    final GroupEvent groupEvent = new GroupEvent(groupResID, customerId, memberIds);
                    groupEvent.groupCategory = groupCategory;
                    groupEvents.add(groupEvent);
                }
            }
            final List<HashMap> groupDetails = this.getGroupDetails(new ArrayList<Long>(Arrays.asList(resourceIds)));
            for (final HashMap groupDetail : groupDetails) {
                MICSGroupFeatureController.addTrackingData(groupDetail.get("GROUP_TYPE"), MICSGroupFeatureController.GroupOperation.DELETE, "MDM".equalsIgnoreCase(groupDetail.get("DOMAIN_NETBIOS_NAME")));
            }
            final DeleteQuery memberRelDelQuery = (DeleteQuery)new DeleteQueryImpl("CustomGroupMemberRel");
            memberRelDelQuery.setCriteria(new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)resourceIds, 8));
            MDMUtil.getPersistence().delete(memberRelDelQuery);
            for (final MDMGroupMemberEvent memberEvent2 : memberEvents) {
                this.invokeGroupMemberListener(memberEvent2, 2);
            }
            final DeleteQuery groupDelQuery = (DeleteQuery)new DeleteQueryImpl("Resource");
            groupDelQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIds, 8));
            MDMUtil.getPersistence().delete(groupDelQuery);
            for (final GroupEvent groupEvent2 : groupEvents) {
                MDMCustomGroupHandler.getInstance().invokeGroupListeners(groupEvent2, 2);
            }
            return resourceIds;
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in MDMGroupHandler deleteGroup....", e);
            return null;
        }
    }
    
    private JSONObject handleBeforeDelete(final HttpServletRequest request, final Long[] resourceIds, final JSONObject groupResponseJSON) {
        try {
            Long resourceId = null;
            if (resourceIds.length == 1) {
                resourceId = resourceIds[0];
                final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(resourceId);
                final String groupName = groupMap.get("NAME");
                MDMCustomGroupUtil.getInstance().addCGActionLog(request, groupName, 2073);
                groupResponseJSON.put("SINGLE_GROUP_DELETE", true);
                groupResponseJSON.put("NAME", (Object)groupName);
            }
            else {
                for (int resLen = resourceIds.length, i = 0; i < resLen; ++i) {
                    resourceId = resourceIds[i];
                    final HashMap groupMap2 = MDMCustomGroupUtil.getInstance().getResourceProperties(resourceId);
                    final String groupName2 = groupMap2.get("NAME");
                    MDMCustomGroupUtil.getInstance().addCGActionLog(request, groupName2, 2073);
                }
                groupResponseJSON.put("MULTIPE_GROUP_DELETE", true);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occoured in.... handleAfterDelete", e);
        }
        return groupResponseJSON;
    }
    
    public void populateDeviceActionDetails(final String command_name, final String reasonMsg, final JSONObject commandDetailsJSON) {
        try {
            final DataObject actionCommandDO = MDMUtil.getPersistence().constructDataObject();
            final Iterator<String> resourceItr = commandDetailsJSON.keys();
            while (resourceItr.hasNext()) {
                final String resourceID = resourceItr.next();
                final JSONObject commandJSON = commandDetailsJSON.getJSONArray(resourceID).getJSONObject(0);
                final Long cmdHisID = commandJSON.getLong("COMMAND_HISTORY_ID");
                final Row deviceActionRow = new Row("DeviceActionHistory");
                deviceActionRow.set("ACTION_ID", (Object)InvActionUtil.getEquivalentActionType(command_name));
                deviceActionRow.set("COMMAND_HISTORY_ID", (Object)cmdHisID);
                actionCommandDO.addRow(deviceActionRow);
                final Row cmdHisReasonRow = new Row("ReasonForCommandHistory");
                cmdHisReasonRow.set("COMMAND_HISTORY_ID", (Object)cmdHisID);
                cmdHisReasonRow.set("REASON_MESSAGE", (Object)reasonMsg);
                actionCommandDO.addRow(cmdHisReasonRow);
            }
            MDMUtil.getPersistence().add(actionCommandDO);
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occurred in populateDeviceActionDetails....", e);
        }
    }
    
    public void updateGroup(final CustomGroupDetails cgDetails) {
        final Long resourceId = cgDetails.resourceId;
        Long userId = null;
        if (cgDetails instanceof MDMCustomGroupDetails) {
            userId = ((MDMCustomGroupDetails)cgDetails).userId;
        }
        this.renameGroup(resourceId, cgDetails.groupName);
        super.updateGroupDescription(resourceId, cgDetails.groupDescription);
        super.addOrUpdateCustomGroupExtn(resourceId);
        this.updateMDMGroupResource(resourceId, cgDetails.resourceIds, cgDetails.customerId, userId);
        CustomGroupingHandler.updateDBTime(cgDetails.resourceId);
    }
    
    @Deprecated
    public static List getMDMGroups(final Integer platformType) {
        return getMDMGroups();
    }
    
    public static List getMDMGroups() {
        List mdmGroupList = null;
        try {
            mdmGroupList = getCustomGroups();
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in getMDMGroups....", e);
        }
        return mdmGroupList;
    }
    
    public static List getEnrollmentRBDARestrictedMDMGroups(final Integer platformType) {
        List mdmGroupList = null;
        try {
            mdmGroupList = getCustomGroupsWithStaticGroupsRestrictedForAdmin();
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in getMDMGroups....", e);
        }
        return mdmGroupList;
    }
    
    @Deprecated
    public static List getCustomGroups(final Integer groupType) {
        return getCustomGroups();
    }
    
    public static List getCustomGroups() {
        final List groupTypeList = getMDMGroupType();
        final SelectQuery query = getCustomGroupsQuery(groupTypeList);
        final List mdmCustomGroupList = getCustomGroupDetailsList(query);
        return mdmCustomGroupList;
    }
    
    public static List getMDMNonUserGroups() {
        List mdmCustomGroupList = null;
        try {
            final List groupTypeList = getMDMGroupType();
            groupTypeList.remove(new Integer(7));
            final SelectQuery query = getCustomGroupsQuery(groupTypeList, Boolean.FALSE);
            query.setCriteria(query.getCriteria().and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0)));
            mdmCustomGroupList = getCustomGroupDetailsList(query);
        }
        catch (final Exception ex) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in getMDMNonUserGroups....{0}", ex);
        }
        return mdmCustomGroupList;
    }
    
    private static List getCustomGroupsWithStaticGroupsRestrictedForAdmin() {
        final List groupTypeList = getMDMGroupType();
        groupTypeList.remove(new Integer(7));
        final SelectQuery query = getCustomGroupsQuery(groupTypeList, Boolean.FALSE);
        final List mdmCustomGroupList = getCustomGroupDetailsList(query);
        return mdmCustomGroupList;
    }
    
    public JSONArray getMDMGroupsAsJSONArray(final List groupList) throws Exception {
        final JSONArray jsonArr = new JSONArray();
        for (int i = 0; i < groupList.size(); ++i) {
            final JSONObject json = new JSONObject();
            final Hashtable hm = groupList.get(i);
            for (final String key : hm.keySet()) {
                if (hm.get(key) instanceof Long) {
                    json.put(key, (Object)String.valueOf(hm.get(key)));
                }
                else {
                    json.put(key, hm.get(key));
                }
            }
            jsonArr.put((Object)json);
        }
        return jsonArr;
    }
    
    public static SelectQuery getCustomGroupsQuery(final List groupTypeList) {
        return getCustomGroupsQuery(Boolean.FALSE);
    }
    
    public static SelectQuery getCustomGroupsQuery(final List groupTypeList, final Boolean restrictStaticGroupsCreatedByTechForAdmin) {
        return getCustomGroupsQuery(groupTypeList, restrictStaticGroupsCreatedByTechForAdmin, Boolean.FALSE);
    }
    
    public static SelectQuery getCustomGroupsQuery(final List groupTypeList, final Boolean restrictStaticGroupsCreatedByTechForAdmin, final boolean filter) {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join customGroupJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join customGroupExtnJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join userCustomGroupJoin = new Join("CustomGroup", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        query.addJoin(customGroupJoin);
        query.addJoin(customGroupExtnJoin);
        query.addJoin(userCustomGroupJoin);
        if (filter) {
            setRBDAGroupsCriteriaBasedOnRole(query, restrictStaticGroupsCreatedByTechForAdmin, groupTypeList);
        }
        else {
            query = setRBDAGroupsCriteriaBasedOnRole(query, restrictStaticGroupsCreatedByTechForAdmin);
        }
        query.setCriteria(query.getCriteria().and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8)));
        final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
        query.addSortColumn(sortCol);
        final Column distinctResourceCol = Column.getColumn("Resource", "RESOURCE_ID").distinct();
        distinctResourceCol.setColumnAlias("RESOURCE_ID");
        query.addSelectColumn(distinctResourceCol);
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "DESCRIPTION"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "IS_EDITABLE"));
        return query;
    }
    
    public static SelectQuery getCustomGroupsCountQuery(final List groupTypeList, final Boolean restrictStaticGroupsCreatedByTechForAdmin) {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join customGroupJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join customGroupExtnJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join userCustomGroupJoin = new Join("CustomGroup", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        query.addJoin(customGroupJoin);
        query.addJoin(customGroupExtnJoin);
        query.addJoin(userCustomGroupJoin);
        query = setRBDAGroupsCriteriaBasedOnRole(query, restrictStaticGroupsCreatedByTechForAdmin);
        query.setCriteria(query.getCriteria().and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8)));
        final Column distinctResourceCol = Column.getColumn("Resource", "RESOURCE_ID").distinct().count();
        distinctResourceCol.setColumnAlias("RESOURCE_ID");
        query.addSelectColumn(distinctResourceCol);
        return query;
    }
    
    private static SelectQuery getCustomGroupsQuery(final Boolean restrictStaticGroupsCreatedByTechForAdmin) {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join customGroupJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join customGroupExtnJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join userCustomGroupJoin = new Join("CustomGroup", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        query.addJoin(customGroupJoin);
        query.addJoin(customGroupExtnJoin);
        query.addJoin(userCustomGroupJoin);
        query = setRBDAGroupsCriteriaBasedOnRole(query, restrictStaticGroupsCreatedByTechForAdmin);
        final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
        query.addSortColumn(sortCol);
        final Column distinctResourceCol = Column.getColumn("Resource", "RESOURCE_ID").distinct();
        distinctResourceCol.setColumnAlias("RESOURCE_ID");
        query.addSelectColumn(distinctResourceCol);
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "*"));
        return query;
    }
    
    private static SelectQuery setRBDAGroupsCriteriaBasedOnRole(final SelectQuery query, final Boolean restrictStaticGroupsCreatedByTechForAdmin) {
        return setRBDAGroupsCriteriaBasedOnRole(query, restrictStaticGroupsCreatedByTechForAdmin, null);
    }
    
    private static SelectQuery setRBDAGroupsCriteriaBasedOnRole(SelectQuery query, final Boolean restrictStaticGroupsCreatedByTechForAdmin, final List groupTypeList) {
        Long loginId = null;
        Long userId = null;
        try {
            loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error in obtaining the login ID", ex);
        }
        Criteria groupTypeCriteria = null;
        if (groupTypeList == null || groupTypeList.isEmpty()) {
            groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)getMDMGroupType().toArray(), 8);
        }
        else {
            groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        }
        final Criteria createdByCriteria = new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)userId, 0);
        final Criteria loginIdCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginId, 0);
        Criteria finalCriteriaBasedOnRole = null;
        if (DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices")) {
            if (restrictStaticGroupsCreatedByTechForAdmin) {
                query = RBDAUtil.getInstance().getModifiedUserRoleCheckQuery(query, "CustomGroupExtn", "CREATED_BY");
                final Criteria aaaRoleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"All_Managed_Mobile_Devices", 0);
                groupTypeCriteria = groupTypeCriteria.and(aaaRoleCriteria);
            }
            finalCriteriaBasedOnRole = groupTypeCriteria;
        }
        else {
            finalCriteriaBasedOnRole = groupTypeCriteria.and(createdByCriteria.or(loginIdCriteria));
        }
        query.setCriteria(finalCriteriaBasedOnRole);
        return query;
    }
    
    public static List getStaticUniqueMDMCustomGroups() {
        List customGpList = null;
        try {
            final List groupTypeList = getMDMGroupType();
            customGpList = getCustomGroupsList(groupTypeList, Integer.valueOf(5));
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in getCustomGroups....", e);
        }
        return customGpList;
    }
    
    public static List getAssignedCustomGroupsForUser(final Long loginID) {
        final List groupList = getMDMGroupType();
        return getAssignedCustomGroupsForUser(groupList, loginID);
    }
    
    public static List getAssignedCustomGroupsForUser(final List groupTypeList, final Long loginId) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join customGroupJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join customGroupExtnJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join userCustomGroupJoin = new Join("CustomGroup", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
        query.addJoin(customGroupJoin);
        query.addJoin(customGroupExtnJoin);
        query.addJoin(userCustomGroupJoin);
        final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria groupUserCriteria = RBDAUtil.getInstance().getUserCustomGroupCriteria(loginId);
        final Criteria finalCriteriaBasedOnRole = groupTypeCriteria.and(groupUserCriteria);
        query.setCriteria(finalCriteriaBasedOnRole);
        final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
        query.addSortColumn(sortCol);
        final Column distinctResourceCol = Column.getColumn("Resource", "RESOURCE_ID").distinct();
        distinctResourceCol.setColumnAlias("RESOURCE_ID");
        query.addSelectColumn(distinctResourceCol);
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("CustomGroup", "*"));
        final List mdmCustomGroupList = getCustomGroupDetailsList(query);
        return mdmCustomGroupList;
    }
    
    public boolean removeMemberfromGroup(final Long groupId, final Long[] resourceIds) {
        final boolean isDeleted = super.removeMemberfromGroup(groupId, resourceIds);
        if (isDeleted) {
            final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupId, resourceIds);
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(groupId);
            groupEvent.customerId = groupMap.get("CUSTOMER_ID");
            groupEvent.groupType = groupMap.get("GROUP_TYPE");
            this.invokeGroupMemberListener(groupEvent, 2);
        }
        return isDeleted;
    }
    
    public boolean removeMemberfromGroup(final JSONObject membergroupObjects) throws JSONException {
        final Long groupId = (Long)membergroupObjects.get("groupId");
        final Long[] resourceIds = (Long[])membergroupObjects.get("resourceId");
        final Boolean isMove = (Boolean)membergroupObjects.get("isMove");
        final Long userId = (Long)membergroupObjects.opt("userId");
        final boolean isDeleted = super.removeMemberfromGroup(groupId, resourceIds);
        if (isDeleted) {
            final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupId, resourceIds);
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(groupId);
            groupEvent.customerId = groupMap.get("CUSTOMER_ID");
            groupEvent.groupType = groupMap.get("GROUP_TYPE");
            groupEvent.userId = userId;
            groupEvent.isMove = isMove;
            this.invokeGroupMemberListener(groupEvent, 2);
        }
        return isDeleted;
    }
    
    public boolean addMembertoMultipleGroups(final List<Long> groupId, final Long[] resourceId, final Long customerId, final Long userId) {
        final boolean isAdded = this.addTableEntriesForMembertoMultipleGroups(groupId, resourceId[0]);
        if (isAdded) {
            final HashMap groupMap = this.getResourcePropertiesForMultipleGroups(groupId);
            for (final Long gid : groupId) {
                final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(gid, resourceId);
                groupEvent.customerId = customerId;
                final HashMap hmap = groupMap.get(gid);
                groupEvent.groupType = hmap.get("GROUP_TYPE");
                groupEvent.userId = userId;
                this.invokeGroupMemberListener(groupEvent, 1);
            }
        }
        return isAdded;
    }
    
    public boolean addMembertoGroup(final JSONObject membergroupObjects) throws JSONException {
        final Long groupId = (Long)membergroupObjects.get("groupId");
        final Long[] resourceId = (Long[])membergroupObjects.get("resourceId");
        final Long customerId = (Long)membergroupObjects.get("customerId");
        final Boolean isMove = (Boolean)membergroupObjects.get("isMove");
        final Boolean newGroup = membergroupObjects.optBoolean("newGroup");
        final Long userId = (Long)membergroupObjects.opt("userId");
        this.profileDistributionLog.log(Level.INFO, "Adding Members : {0} to the group : {1} user id : {2} and isMove: {3}", new Object[] { groupId, Arrays.asList(resourceId), userId, isMove });
        final boolean isAdded = super.addMembertoGroup(groupId, resourceId);
        if (isAdded) {
            if (newGroup) {
                ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
                return isAdded;
            }
            final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupId, resourceId);
            groupEvent.customerId = customerId;
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(groupId);
            groupEvent.groupType = groupMap.get("GROUP_TYPE");
            groupEvent.userId = userId;
            if (isMove != null) {
                groupEvent.isMove = isMove;
            }
            this.invokeGroupMemberListener(groupEvent, 1);
        }
        return isAdded;
    }
    
    private Long addOrUpdateMDMGroupDB(final CustomGroupDetails cgDetails) {
        MDMGroupHandler.logger.log(Level.INFO, "Adding group: GroupID {0}", cgDetails.resourceId);
        Long resourceId = null;
        JSONObject integrationJson = null;
        try {
            resourceId = super.getGroupResourceId(cgDetails.groupName, cgDetails.customerId, cgDetails.domainName);
            final Properties properties = new Properties();
            properties.setProperty("NAME", cgDetails.groupName);
            ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", cgDetails.domainName);
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", cgDetails.customerId);
            properties.setProperty("RESOURCE_TYPE", String.valueOf(101));
            final long createdTime = System.currentTimeMillis();
            ((Hashtable<String, Long>)properties).put("DB_ADDED_TIME", createdTime);
            MDMResourceDataPopulator.addOrUpdateMDMResource(properties);
            int categoryType = 1;
            Long userId = 0L;
            if (cgDetails instanceof MDMCustomGroupDetails) {
                categoryType = ((MDMCustomGroupDetails)cgDetails).groupCategory;
                userId = ((MDMCustomGroupDetails)cgDetails).userId;
                integrationJson = ((MDMCustomGroupDetails)cgDetails).integrationJson;
            }
            super.addorUpdateGroup(resourceId, cgDetails.groupType, categoryType, cgDetails.isEditable, cgDetails.groupDescription);
            super.addOrUpdateCustomGroupExtn(resourceId);
            if (cgDetails.resourceIds != null) {
                this.updateMDMGroupResource(resourceId, cgDetails.resourceIds, cgDetails.customerId, userId);
            }
            GroupEvent groupEvent;
            if (integrationJson != null) {
                groupEvent = new GroupEvent(resourceId, cgDetails.customerId, cgDetails.resourceIds, integrationJson);
            }
            else {
                groupEvent = new GroupEvent(resourceId, cgDetails.customerId, cgDetails.resourceIds);
            }
            MDMCustomGroupHandler.getInstance().invokeGroupListeners(groupEvent, 1);
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in addOrUpdateMDMGroupDB....", e);
        }
        return resourceId;
    }
    
    public boolean updateMDMGroupResource(final Long groupResourceId, final Long[] resourceIds, final Long customerId, final Long userId) {
        boolean addorUpdateStatus = false;
        try {
            final HashMap memberActionMap = this.getMemberDetailsWithAction(groupResourceId, resourceIds);
            final List<Long> addList = memberActionMap.get("ADDED_DEVICE");
            final List<Long> removeList = memberActionMap.get("REMOVE_DEVICE");
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(groupResourceId);
            if (!addList.isEmpty()) {
                final DataObject resourceDO = MDMUtil.getPersistence().constructDataObject();
                super.addGroupMemberRel(resourceDO, groupResourceId, (Long[])addList.toArray(new Long[addList.size()]));
                final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupResourceId, addList.toArray(new Long[addList.size()]));
                groupEvent.customerId = customerId;
                groupEvent.groupType = groupMap.get("GROUP_TYPE");
                groupEvent.userId = userId;
                this.invokeGroupMemberListener(groupEvent, 1);
            }
            if (!removeList.isEmpty()) {
                final MDMGroupMemberEvent groupEvent2 = new MDMGroupMemberEvent(groupResourceId, removeList.toArray(new Long[removeList.size()]));
                groupEvent2.customerId = customerId;
                groupEvent2.groupType = groupMap.get("GROUP_TYPE");
                groupEvent2.userId = userId;
                this.invokeGroupMemberListener(groupEvent2, 2);
            }
            addorUpdateStatus = true;
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in updateMDMGroupResource....", e);
        }
        return addorUpdateStatus;
    }
    
    public Long getDefaultMDMSelfEnrollGroupId(final Long customerId, final int platformType, final int ownedBy) {
        int groupIdentifier = -1;
        Long groupId = null;
        try {
            groupIdentifier = this.getDefaultGroupIdentifier(platformType, ownedBy);
            final SelectQuery groupSettingQuery = this.getDefaultGroupSettingsQuery(groupIdentifier, ownedBy, customerId);
            final DataObject dObj = MDMUtil.getPersistence().get(groupSettingQuery);
            if (!dObj.isEmpty()) {
                final Row settingsRow = dObj.getRow("DefaultGroupSettings", new Criteria(new Column("DefaultGroupSettings", "GROUP_IDENTIFIER"), (Object)groupIdentifier, 0));
                if (settingsRow != null) {
                    groupId = (Long)settingsRow.get("GROUP_ID");
                }
                else {
                    groupId = (Long)dObj.getFirstValue("DefaultGroupSettings", "GROUP_ID");
                }
            }
        }
        catch (final Exception exp) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception in getDefaultMDMGroupId", exp);
        }
        return groupId;
    }
    
    public void updateDefaultMDMGroupId(final Long customerId, final int groupIdentifier, final Long newGroupId) {
        try {
            final List groupList = new ArrayList();
            groupList.add(groupIdentifier);
            final SelectQuery groupSettingQuery = this.getDefaultGroupSettingsQuery(groupList, customerId);
            final DataObject dObj = MDMUtil.getPersistence().get(groupSettingQuery);
            if (!dObj.isEmpty()) {
                final Row groupRow = dObj.getFirstRow("DefaultGroupSettings");
                groupRow.set("GROUP_ID", (Object)newGroupId);
                dObj.updateRow(groupRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception exp) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception in updateDefaultMDMGroupId", exp);
        }
    }
    
    private SelectQuery getDefaultGroupSettingsQuery(final List groupIdentifier, final Long customerId) {
        final SelectQuery groupSettingQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DefaultGroupSettings"));
        groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_SETTINGS_ID"));
        groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_ID"));
        final Join enrollSettingsJoin = new Join("DefaultGroupSettings", "EnrollmentSettings", new String[] { "ENROLLMENT_SETTINGS_ID" }, new String[] { "ENROLLMENT_SETTINGS_ID" }, 2);
        groupSettingQuery.addJoin(enrollSettingsJoin);
        final Criteria customerCri = new Criteria(Column.getColumn("EnrollmentSettings", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria groupIdentifierCri = new Criteria(Column.getColumn("DefaultGroupSettings", "GROUP_IDENTIFIER"), (Object)groupIdentifier.toArray(), 8);
        final Criteria cri = customerCri.and(groupIdentifierCri);
        groupSettingQuery.setCriteria(cri);
        return groupSettingQuery;
    }
    
    private SelectQuery getDefaultGroupSettingsQuery(final int groupIdentifier, final int ownedBy, final Long customerId) {
        final List groupIdentifierList = new ArrayList();
        groupIdentifierList.add(groupIdentifier);
        if (groupIdentifier != 7 && groupIdentifier != 8) {
            groupIdentifierList.add(this.getDefaultGroupIdentifier(0, ownedBy));
        }
        return this.getDefaultGroupSettingsQuery(groupIdentifierList, customerId);
    }
    
    private int getDefaultGroupIdentifier(final int platformType, final int ownedBy) {
        int groupIdentifier = -1;
        if (platformType == 1 && ownedBy == 1) {
            groupIdentifier = 1;
        }
        else if (platformType == 1 && ownedBy == 2) {
            groupIdentifier = 2;
        }
        else if (platformType == 2 && ownedBy == 1) {
            groupIdentifier = 3;
        }
        else if (platformType == 2 && ownedBy == 2) {
            groupIdentifier = 4;
        }
        else if (platformType == 3 && ownedBy == 1) {
            groupIdentifier = 5;
        }
        else if (platformType == 3 && ownedBy == 2) {
            groupIdentifier = 6;
        }
        else if (platformType == 0 && ownedBy == 2) {
            groupIdentifier = 8;
        }
        else if (platformType == 0 && ownedBy == 1) {
            groupIdentifier = 7;
        }
        return groupIdentifier;
    }
    
    private ArrayList createDefaultgroupDetailList() {
        ArrayList groupDetailList = null;
        MDMCustomGroupDetails cgDetails = null;
        try {
            groupDetailList = new ArrayList();
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupName = "Default_iOS_Corporate";
            cgDetails.groupIdentifier = 1;
            cgDetails.groupTypeStr = "iOS";
            cgDetails.groupType = 3;
            cgDetails.groupOwnedBy = 1;
            groupDetailList.add(cgDetails);
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupName = "Default_iOS_Personal";
            cgDetails.groupIdentifier = 2;
            cgDetails.groupTypeStr = "iOS";
            cgDetails.groupType = 3;
            cgDetails.groupOwnedBy = 2;
            groupDetailList.add(cgDetails);
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupName = "Default_Android_Corporate";
            cgDetails.groupIdentifier = 3;
            cgDetails.groupTypeStr = "Android";
            cgDetails.groupType = 4;
            cgDetails.groupOwnedBy = 1;
            groupDetailList.add(cgDetails);
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupName = "Default_Android_Personal";
            cgDetails.groupIdentifier = 4;
            cgDetails.groupTypeStr = "Android";
            cgDetails.groupType = 4;
            cgDetails.groupOwnedBy = 2;
            groupDetailList.add(cgDetails);
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupName = "Default_Windows_Corporate";
            cgDetails.groupIdentifier = 5;
            cgDetails.groupTypeStr = "Windows";
            cgDetails.groupType = 5;
            cgDetails.groupOwnedBy = 1;
            groupDetailList.add(cgDetails);
            cgDetails = new MDMCustomGroupDetails();
            cgDetails.groupName = "Default_Windows_Personal";
            cgDetails.groupIdentifier = 6;
            cgDetails.groupTypeStr = "Windows";
            cgDetails.groupType = 5;
            cgDetails.groupOwnedBy = 2;
            groupDetailList.add(cgDetails);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in createDefaultgroupDetailList....", e);
        }
        return groupDetailList;
    }
    
    private MDMCustomGroupDetails setDefaultMDMGroupDetails(final MDMCustomGroupDetails cgDetails) {
        try {
            cgDetails.domainName = "MDM";
            cgDetails.resourceIds = null;
            cgDetails.groupDescription = "";
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in setDefaultMDMGroupDetails....", e);
        }
        return cgDetails;
    }
    
    public void deleteMemberFromAllGroups(final Long resourceId) {
        try {
            final Criteria resCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("CustomGroupMemberRel", resCri);
            dObj.deleteRows("CustomGroupMemberRel", resCri);
            MDMUtil.getPersistence().update(dObj);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in deleteMemberFromAllGroups....", e);
        }
    }
    
    public ArrayList getDefaultGroups() {
        final ArrayList defautGroupList = new ArrayList();
        defautGroupList.add("Default_iOS_Corporate");
        defautGroupList.add("Default_iOS_Personal");
        defautGroupList.add("Default_Android_Corporate");
        defautGroupList.add("Default_Android_Personal");
        defautGroupList.add("Default_Windows_Corporate");
        defautGroupList.add("Default_Windows_Personal");
        return defautGroupList;
    }
    
    public boolean isDefaultGroup(final Long groupId) {
        final boolean isDefaultGroup = false;
        final String groupName = this.getGroupName(groupId);
        return this.isDefaultGroup(groupName);
    }
    
    public boolean isDefaultGroup(final String groupName) {
        boolean isDefaultGroup = false;
        final ArrayList defautGroupList = this.getDefaultGroups();
        isDefaultGroup = defautGroupList.contains(groupName);
        return isDefaultGroup;
    }
    
    public boolean isSelfEnrolledDefaultGroup(final Long groupId) {
        boolean isSelfEnrolledDefaultGroup = false;
        try {
            final Row defaultGroupRow = DBUtil.getRowFromDB("DefaultGroupSettings", "GROUP_ID", (Object)groupId);
            if (defaultGroupRow != null) {
                isSelfEnrolledDefaultGroup = true;
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in isDefaultGroup....", e);
        }
        return isSelfEnrolledDefaultGroup;
    }
    
    private HashMap getMemberDetailsWithAction(final Long groupResourceId, Long[] resourceIds) throws Exception {
        final HashMap actionMap = new HashMap();
        final Criteria customGroupUpdate = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResourceId, 0);
        final DataObject resourceDO = DataAccess.get("CustomGroupMemberRel", customGroupUpdate);
        final List<Long> removedList = new ArrayList<Long>();
        List<Long> addList = null;
        int qCons = 9;
        if (resourceIds != null) {
            addList = new ArrayList<Long>(Arrays.asList(resourceIds));
        }
        else {
            addList = new ArrayList<Long>();
            qCons = 1;
        }
        if (!resourceDO.isEmpty()) {
            final Criteria removedListCriteria = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceIds, qCons);
            final Iterator removedItem = resourceDO.getRows("CustomGroupMemberRel", removedListCriteria);
            while (removedItem.hasNext()) {
                final Row relRow = removedItem.next();
                removedList.add((Long)relRow.get("MEMBER_RESOURCE_ID"));
            }
            actionMap.put("REMOVE_DEVICE", removedList);
            if (resourceIds == null) {
                resourceIds = new Long[0];
            }
            final Criteria addedListCriteria = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceIds, 8);
            final Iterator addItem = resourceDO.getRows("CustomGroupMemberRel", addedListCriteria);
            while (addItem.hasNext()) {
                final Row relRow2 = addItem.next();
                addList.remove(relRow2.get("MEMBER_RESOURCE_ID"));
            }
            actionMap.put("ADDED_DEVICE", addList);
            resourceDO.deleteRows("CustomGroupMemberRel", removedListCriteria);
            MDMUtil.getPersistence().update(resourceDO);
        }
        actionMap.put("ADDED_DEVICE", addList);
        actionMap.put("REMOVE_DEVICE", removedList);
        return actionMap;
    }
    
    public void renameGroup(final Long resourceId, final String groupName) {
        try {
            MDMResourceDataPopulator.renameResource(resourceId, groupName);
            final GroupEvent groupEvent = new GroupEvent(resourceId);
            MDMCustomGroupHandler.getInstance().invokeGroupListeners(groupEvent, 3);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in renameGroup....", e);
        }
    }
    
    public static org.json.simple.JSONObject getCustomGroupDetails(final String customGroupName, final Long userID, final Long customerID) throws SyMException {
        final org.json.simple.JSONObject json = new org.json.simple.JSONObject();
        try {
            final Long loginId = DMUserHandler.getLoginIdForUserId(userID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroup", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
            final Criteria nameCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)customGroupName, 0, false);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)getMDMGroupType().toArray(), 8);
            final Criteria createdByCriteria = new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)userID, 0);
            final Criteria loginIdCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginId, 0);
            final Criteria baseCriteria = nameCriteria.and(customerCriteria);
            Criteria finalCriteriaBasedOnRole = null;
            if (DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices")) {
                finalCriteriaBasedOnRole = baseCriteria;
            }
            else {
                finalCriteriaBasedOnRole = baseCriteria.and(createdByCriteria.or(loginIdCriteria));
            }
            selectQuery.setCriteria(finalCriteriaBasedOnRole);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            MDMGroupHandler.logger.log(Level.FINE, "Select Query to get Custom Group : {0}", selectQuery);
            final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
            MDMGroupHandler.logger.log(Level.FINE, "DataObject Obtained : {0}", dobj);
            if (dobj.isEmpty() || !dobj.containsTable("CustomGroup")) {
                throw new SyMException(14006, "Group not found", "dc.mdm.enroll.group_not_available", (Throwable)null);
            }
            final Row row = dobj.getRow("CustomGroup", groupTypeCriteria);
            if (row == null) {
                throw new SyMException(14008, "Group belongs to a different platform", "dc.mdm.device_mgmt.group_type_mismatch", (Throwable)null);
            }
            json.put((Object)"RESOURCE_ID", (Object)row.get("RESOURCE_ID"));
            json.put((Object)"GROUP_TYPE", (Object)row.get("GROUP_TYPE"));
            json.put((Object)"GROUP_CATEGORY", (Object)row.get("GROUP_CATEGORY"));
            json.put((Object)"DESCRIPTION", (Object)row.get("DESCRIPTION"));
            json.put((Object)"IS_EDITABLE", (Object)row.get("IS_EDITABLE"));
        }
        catch (final SyMException ex) {
            throw ex;
        }
        catch (final DataAccessException ex2) {
            Logger.getLogger(MDMGroupHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
        }
        return json;
    }
    
    public Long getStaticUniqueGroupForResourceId(final Long resourceId) {
        Long groupId = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria customGroupMemberRel = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria customGroupCategory = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)5, 0);
            query.addJoin(customGroupJoin);
            query.setCriteria(customGroupMemberRel.and(customGroupCategory));
            final DataObject dao = MDMUtil.getPersistence().get(query);
            if (!dao.isEmpty()) {
                final Row row = dao.getFirstRow("CustomGroupMemberRel");
                groupId = (Long)row.get("GROUP_RESOURCE_ID");
            }
        }
        catch (final DataAccessException ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error in obtaining static unique group for resource id {0}", (Throwable)ex);
        }
        return groupId;
    }
    
    public List<Long> getStaticUniqueGroupsForResourceId(final Long resourceId) {
        final List<Long> groupId = new ArrayList<Long>();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria customGroupMemberRel = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria customGroupCategory = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)5, 0);
            query.addJoin(customGroupJoin);
            query.setCriteria(customGroupMemberRel.and(customGroupCategory));
            final DataObject dao = MDMUtil.getPersistence().get(query);
            if (!dao.isEmpty()) {
                final Iterator iterator = dao.getRows("CustomGroupMemberRel");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    groupId.add((Long)row.get("GROUP_RESOURCE_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error in obtaining static unique group for resource id {0}", (Throwable)ex);
        }
        return groupId;
    }
    
    public void reassignDevicesToTechnicianCreatedGroupsOnScopeModification(final long technicanId, final List<Long> assignedStaticUniqueGroupIDs, final Long loggedOnUserId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        sQuery.setCriteria(new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)assignedStaticUniqueGroupIDs.toArray(), 8));
        final List groupByColumns = new ArrayList();
        groupByColumns.add(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        sQuery.addSelectColumns(groupByColumns);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        sQuery.setGroupByClause(grpByCls);
        DMDataSetWrapper ds = null;
        final HashMap<Long, List> usermap = new HashMap<Long, List>();
        final List deviceList = new ArrayList();
        try {
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Long deviceId = (Long)ds.getValue("MEMBER_RESOURCE_ID");
                deviceList.add(deviceId);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error  while getting getting device list", e);
        }
        final Set deviceSet = new HashSet(deviceList);
        final SelectQuery memberQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        memberQuery.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria criteria = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceSet.toArray(), 9);
        criteria = criteria.and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)getMDMGroupType().toArray(), 8));
        memberQuery.setCriteria(criteria);
        final List groupByMemberColumns = new ArrayList();
        groupByMemberColumns.add(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        memberQuery.addSelectColumns(groupByMemberColumns);
        final GroupByClause grpByMemberCls = new GroupByClause(groupByMemberColumns);
        memberQuery.setGroupByClause(grpByMemberCls);
        DMDataSetWrapper memberds = null;
        final List memberDeviceList = new ArrayList();
        try {
            memberds = DMDataSetWrapper.executeQuery((Object)memberQuery);
            while (memberds.next()) {
                final Long deviceId2 = (Long)memberds.getValue("MEMBER_RESOURCE_ID");
                memberDeviceList.add(deviceId2);
            }
        }
        catch (final Exception e2) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error while getting excluding device list", e2);
        }
        usermap.put(technicanId, memberDeviceList);
        this.reassignDevicesToTechnicianCreatedGroupsOnScopeModification(usermap, CustomerInfoUtil.getInstance().getDefaultCustomer(), loggedOnUserId);
    }
    
    public void reassignDevicesToTechnicianCreatedGroupsOnScopeModification(final HashMap<Long, List> loginIdToDeviceMap, final Long customerId, final Long loggedOnUserId) throws DataAccessException {
        final Set LoginIdSet = loginIdToDeviceMap.keySet();
        for (final Long loginId : LoginIdSet) {
            final List resourceList = loginIdToDeviceMap.get(loginId);
            if (!resourceList.isEmpty()) {
                final SelectQuery delCustomGroupResource = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
                final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                final Join customGroupExtnJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                final Join aaaLoginJoin = new Join("CustomGroupExtn", "AaaLogin", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, 2);
                delCustomGroupResource.addJoin(customGroupJoin);
                delCustomGroupResource.addJoin(customGroupExtnJoin);
                delCustomGroupResource.addJoin(aaaLoginJoin);
                final Criteria userCriteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
                final Criteria customGroupMemberIDCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                delCustomGroupResource.setCriteria(userCriteria.and(customGroupMemberIDCriteria));
                delCustomGroupResource.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
                MDMGroupHandler.logger.log(Level.INFO, "Deleting resource IDs {0} from static custom groups created by technicians {1} as the resources have been removed from his scope group", new Object[] { resourceList, loginId });
                final DataObject groupDO = SyMUtil.getPersistence().get(delCustomGroupResource);
                final HashMap<Long, List> groupDeviceMap = new HashMap<Long, List>();
                if (groupDO.isEmpty()) {
                    continue;
                }
                final Iterator item = groupDO.getRows("CustomGroupMemberRel");
                while (item.hasNext()) {
                    final Row relRow = item.next();
                    final Long groupId = (Long)relRow.get("GROUP_RESOURCE_ID");
                    final Long deviceId = (Long)relRow.get("MEMBER_RESOURCE_ID");
                    List deviceList = groupDeviceMap.get(groupId);
                    if (deviceList == null) {
                        deviceList = new ArrayList();
                    }
                    deviceList.add(deviceId);
                    groupDeviceMap.put(groupId, deviceList);
                }
                groupDO.deleteRows("CustomGroupMemberRel", (Criteria)null);
                MDMUtil.getPersistence().update(groupDO);
                final Iterator groupItem = groupDeviceMap.keySet().iterator();
                while (groupItem.hasNext()) {
                    final Long groupId = groupItem.next();
                    final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupId, (Long[])groupDeviceMap.get(groupId).toArray(new Long[groupDeviceMap.get(groupId).size()]));
                    groupEvent.customerId = customerId;
                    groupEvent.groupType = 6;
                    groupEvent.userId = loggedOnUserId;
                    this.invokeGroupMemberListener(groupEvent, 2);
                }
            }
        }
    }
    
    private SelectQuery getGroupQuery(final Object groupId) {
        final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        final Join groupResourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join cusGroupExtnJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        groupQuery.addJoin(groupResourceJoin);
        groupQuery.addJoin(cusGroupExtnJoin);
        groupQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        Criteria groupIdCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), groupId, 0);
        if (groupId instanceof List) {
            groupIdCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)((List)groupId).toArray(), 8);
        }
        else {
            groupIdCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), groupId, 0);
        }
        groupQuery.setCriteria(groupIdCriteria);
        return groupQuery;
    }
    
    public HashMap getGroupDetails(final Long groupId) {
        HashMap groupMap = null;
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery groupQuery = this.getGroupQuery(groupId);
            ds = DMDataSetWrapper.executeQuery((Object)groupQuery);
            if (ds.next()) {
                groupMap = new HashMap();
                groupMap.put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                groupMap.put("DESCRIPTION", ds.getValue("DESCRIPTION"));
                groupMap.put("GROUP_CATEGORY", ds.getValue("GROUP_CATEGORY"));
                groupMap.put("GROUP_TYPE", ds.getValue("GROUP_TYPE"));
                groupMap.put("IS_EDITABLE", ds.getValue("IS_EDITABLE"));
                groupMap.put("NAME", ds.getValue("NAME"));
                groupMap.put("DOMAIN_NETBIOS_NAME", ds.getValue("DOMAIN_NETBIOS_NAME"));
                groupMap.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                groupMap.put("DB_ADDED_TIME", ds.getValue("DB_ADDED_TIME"));
                groupMap.put("DB_UPDATED_TIME", ds.getValue("DB_UPDATED_TIME"));
                groupMap.put("CREATED_BY", ds.getValue("CREATED_BY"));
                groupMap.put("LAST_MODIFIED_BY", ds.getValue("LAST_MODIFIED_BY"));
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in getGroupDetails....", e);
        }
        return groupMap;
    }
    
    public List<HashMap> getGroupDetails(final List<Long> groupId) {
        final List<HashMap> groupMaps = new ArrayList<HashMap>();
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery groupQuery = this.getGroupQuery(groupId);
            ds = DMDataSetWrapper.executeQuery((Object)groupQuery);
            while (ds.next()) {
                final HashMap groupMap = new HashMap();
                groupMap.put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                groupMap.put("DESCRIPTION", ds.getValue("DESCRIPTION"));
                groupMap.put("GROUP_CATEGORY", ds.getValue("GROUP_CATEGORY"));
                groupMap.put("GROUP_TYPE", ds.getValue("GROUP_TYPE"));
                groupMap.put("IS_EDITABLE", ds.getValue("IS_EDITABLE"));
                groupMap.put("NAME", ds.getValue("NAME"));
                groupMap.put("DOMAIN_NETBIOS_NAME", ds.getValue("DOMAIN_NETBIOS_NAME"));
                groupMap.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                groupMap.put("DB_ADDED_TIME", ds.getValue("DB_ADDED_TIME"));
                groupMap.put("DB_UPDATED_TIME", ds.getValue("DB_UPDATED_TIME"));
                groupMap.put("CREATED_BY", ds.getValue("CREATED_BY"));
                groupMap.put("LAST_MODIFIED_BY", ds.getValue("LAST_MODIFIED_BY"));
                groupMaps.add(groupMap);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in getGroupDetails....", e);
        }
        return groupMaps;
    }
    
    public String getGroupName(final Long groupId) {
        String groupName = "";
        try {
            final HashMap groupMap = this.getGroupDetails(groupId);
            groupName = groupMap.get("NAME");
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in groupName :{0}", ex);
        }
        return groupName;
    }
    
    public int getGroupType(final Long groupId) {
        int groupType = -1;
        try {
            final HashMap groupMap = this.getGroupDetails(groupId);
            groupType = groupMap.get("GROUP_TYPE");
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in getGroupType :{0}", ex);
        }
        return groupType;
    }
    
    private SelectQuery getGroupMemberQuery(final Long groupId) {
        final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        final ArrayList colList = new ArrayList();
        colList.add(Column.getColumn("Resource", "RESOURCE_ID"));
        colList.add(Column.getColumn("Resource", "NAME"));
        colList.add(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        colList.add(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        final Join groupMemberRelJoin = new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        groupQuery.addJoin(groupMemberRelJoin);
        groupQuery.addSelectColumns((List)colList);
        final Criteria groupIdCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
        groupQuery.setCriteria(groupIdCriteria);
        return groupQuery;
    }
    
    public org.json.simple.JSONObject getGroupMemberTreeJSON(final Long groupId) {
        org.json.simple.JSONObject resourceJSON = null;
        DMDataSetWrapper ds = null;
        try {
            org.json.simple.JSONObject resourcePropJSON = null;
            final SelectQuery groupQuery = this.getGroupMemberQuery(groupId);
            ds = DMDataSetWrapper.executeQuery((Object)groupQuery);
            Long resourceId = null;
            resourceJSON = new org.json.simple.JSONObject();
            while (ds.next()) {
                resourcePropJSON = new org.json.simple.JSONObject();
                resourceId = (Long)ds.getValue("RESOURCE_ID");
                resourcePropJSON.put((Object)"NODE_ID", (Object)ds.getValue("RESOURCE_ID"));
                resourcePropJSON = JSONUtil.getInstance().convertLongToString(resourcePropJSON);
                resourceJSON.put((Object)resourceId, (Object)resourcePropJSON);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occured in getGroupMemberTreeJSON....", e);
        }
        return resourceJSON;
    }
    
    public Long[] decodeGroupMemberIds(final String deviceArrStr) {
        Long[] deviceArr = null;
        try {
            final JSONArray deviceJSONArr = new JSONArray(deviceArrStr);
            deviceArr = new Long[deviceJSONArr.length()];
            for (int i = 0; i < deviceJSONArr.length(); ++i) {
                deviceArr[i] = JSONUtil.optLongForUVH(deviceJSONArr, i, null);
            }
        }
        catch (final Exception ex) {}
        return deviceArr;
    }
    
    public void addorUpdateTempGroupMemberIds(final Long[] deviceIds, final int memberType) throws Exception {
        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        final Criteria userIdCri = new Criteria(Column.getColumn("MemberViewTemp", "USER_ID"), (Object)userId, 0);
        final Criteria memberTypeCri = new Criteria(Column.getColumn("MemberViewTemp", "MEMBER_RESOURCE_TYPE"), (Object)memberType, 0);
        final Criteria cri = userIdCri.and(memberTypeCri);
        final DataObject resourceDO = DataAccess.get("MemberViewTemp", cri);
        int qCons = 9;
        if (deviceIds == null) {
            qCons = 1;
        }
        if (!resourceDO.isEmpty()) {
            final Criteria removedListCriteria = new Criteria(new Column("MemberViewTemp", "MEMBER_RESOURCE_ID"), (Object)deviceIds, qCons);
            resourceDO.deleteRows("MemberViewTemp", removedListCriteria);
        }
        if (deviceIds != null) {
            Row memberRow = null;
            Criteria deviceCri = null;
            Long deviceId = null;
            for (int i = 0; i < deviceIds.length; ++i) {
                deviceId = deviceIds[i];
                deviceCri = new Criteria(Column.getColumn("MemberViewTemp", "MEMBER_RESOURCE_ID"), (Object)deviceId, 0);
                memberRow = resourceDO.getRow("MemberViewTemp", deviceCri);
                if (memberRow == null) {
                    memberRow = new Row("MemberViewTemp");
                    memberRow.set("USER_ID", (Object)userId);
                    memberRow.set("MEMBER_RESOURCE_TYPE", (Object)memberType);
                    memberRow.set("MEMBER_RESOURCE_ID", (Object)deviceId);
                    resourceDO.addRow(memberRow);
                }
            }
        }
        MDMUtil.getPersistence().update(resourceDO);
    }
    
    public JSONObject addOrUpdateMDMGroup(final MDMCustomGroupDetails cgDetails) {
        final JSONObject groupResponseJSON = new JSONObject();
        try {
            groupResponseJSON.put("NAME", (Object)cgDetails.groupName);
            if (cgDetails.resourceId != null && cgDetails.resourceId != -1L) {
                getInstance().updateGroup(cgDetails);
                groupResponseJSON.put("IS_UPDATE_SUCCESS", true);
                MICSGroupFeatureController.addTrackingData(cgDetails.groupType, MICSGroupFeatureController.GroupOperation.EDIT, "MDM".equalsIgnoreCase(cgDetails.domainName));
            }
            else {
                if (cgDetails.groupType != 9) {
                    cgDetails.isEditable = true;
                }
                getInstance().addGroup(cgDetails);
                groupResponseJSON.put("IS_CREATE_SUCCESS", true);
                MICSGroupFeatureController.addTrackingData(cgDetails.groupType, MICSGroupFeatureController.GroupOperation.CREATE, "MDM".equalsIgnoreCase(cgDetails.domainName));
            }
            groupResponseJSON.put("RESOURCE_ID", (Object)cgDetails.resourceId);
            groupResponseJSON.put("IS_SUCCESS", true);
        }
        catch (final Exception e) {
            try {
                groupResponseJSON.put("IS_SUCCESS", false);
            }
            catch (final JSONException ex) {
                Logger.getLogger(MDMGroupHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occoured in.... addOrUpdateMDMGroup", e);
        }
        return groupResponseJSON;
    }
    
    public void configureSelfEnrollmentGroups(final JSONObject reqestedData) {
        try {
            final Long customerId = JSONUtil.optLongForUVH(reqestedData, "CUSTOMER_ID", (Long)null);
            final JSONObject configuredGroups = JSONUtil.getInstance().convertLongToString(reqestedData).optJSONObject("SELF_ENROLLMENT_GROUPS");
            final List<Integer> configuredIdentifier = new ArrayList<Integer>();
            Iterator groupItendifierIterator = configuredGroups.keys();
            while (groupItendifierIterator.hasNext()) {
                final String identifier = groupItendifierIterator.next();
                final Integer ident = Integer.parseInt(identifier);
                if (ident != -1) {
                    configuredIdentifier.add(ident);
                }
            }
            final SelectQuery groupSettingQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DefaultGroupSettings"));
            final Join enrollSettingsJoin = new Join("DefaultGroupSettings", "EnrollmentSettings", new String[] { "ENROLLMENT_SETTINGS_ID" }, new String[] { "ENROLLMENT_SETTINGS_ID" }, 2);
            groupSettingQuery.addJoin(enrollSettingsJoin);
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_SETTINGS_ID"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_ID"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_IDENTIFIER"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "ENROLLMENT_SETTINGS_ID"));
            final Criteria customerCri = new Criteria(Column.getColumn("EnrollmentSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            groupSettingQuery.setCriteria(customerCri);
            final DataObject selfEnrollGroupDataObject = MDMUtil.getPersistence().get(groupSettingQuery);
            Long enrollmentSettingsId = null;
            if (!selfEnrollGroupDataObject.isEmpty()) {
                final Criteria removeUnconfigredGroups = configuredIdentifier.isEmpty() ? null : new Criteria(Column.getColumn("DefaultGroupSettings", "GROUP_IDENTIFIER"), (Object)configuredIdentifier.toArray(), 9);
                selfEnrollGroupDataObject.deleteRows("DefaultGroupSettings", removeUnconfigredGroups);
                final Iterator selfEnrolGroupIterator = selfEnrollGroupDataObject.getRows("DefaultGroupSettings");
                while (selfEnrolGroupIterator.hasNext()) {
                    final Row row = selfEnrolGroupIterator.next();
                    final String identifier2 = row.get("GROUP_IDENTIFIER") + "";
                    final Row row3 = row;
                    final String s = "GROUP_ID";
                    JSONUtil.getInstance();
                    row3.set(s, (Object)JSONUtil.optLongForUVH(configuredGroups, identifier2, (Long)null));
                    selfEnrollGroupDataObject.updateRow(row);
                    configuredGroups.remove(identifier2);
                }
            }
            final Row enrollmentSettingsRow = DBUtil.getRowFromDB("EnrollmentSettings", "CUSTOMER_ID", (Object)customerId);
            if (enrollmentSettingsRow != null) {
                enrollmentSettingsId = (Long)enrollmentSettingsRow.get("ENROLLMENT_SETTINGS_ID");
            }
            groupItendifierIterator = configuredGroups.keys();
            while (groupItendifierIterator.hasNext()) {
                final String identifier3 = groupItendifierIterator.next();
                final Long groupId = JSONUtil.optLongForUVH(configuredGroups, identifier3, (Long)null);
                if (groupId != -1L) {
                    final Row row2 = new Row("DefaultGroupSettings");
                    row2.set("ENROLLMENT_SETTINGS_ID", (Object)enrollmentSettingsId);
                    row2.set("GROUP_IDENTIFIER", (Object)Integer.parseInt(identifier3));
                    row2.set("GROUP_ID", (Object)groupId);
                    selfEnrollGroupDataObject.addRow(row2);
                }
            }
            MDMUtil.getPersistence().update(selfEnrollGroupDataObject);
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occurred while configureSelfEnrollmentGroups", ex);
        }
    }
    
    public JSONObject getSelfEnrollmentConfiguredGroups(final Long customerId) {
        final JSONObject configuredGroups = new JSONObject();
        try {
            final SelectQuery groupSettingQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DefaultGroupSettings"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_SETTINGS_ID"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_ID"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "ENROLLMENT_SETTINGS_ID"));
            groupSettingQuery.addSelectColumn(Column.getColumn("DefaultGroupSettings", "GROUP_IDENTIFIER"));
            final Join enrollSettingsJoin = new Join("DefaultGroupSettings", "EnrollmentSettings", new String[] { "ENROLLMENT_SETTINGS_ID" }, new String[] { "ENROLLMENT_SETTINGS_ID" }, 2);
            groupSettingQuery.addJoin(enrollSettingsJoin);
            final Criteria customerCri = new Criteria(Column.getColumn("EnrollmentSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            groupSettingQuery.setCriteria(customerCri);
            final DataObject selfEnrollGroupDO = MDMUtil.getPersistence().get(groupSettingQuery);
            if (!selfEnrollGroupDO.isEmpty()) {
                final Iterator selfEnrollGroupIterator = selfEnrollGroupDO.getRows("DefaultGroupSettings");
                while (selfEnrollGroupIterator.hasNext()) {
                    final Row row = selfEnrollGroupIterator.next();
                    configuredGroups.put(row.get("GROUP_IDENTIFIER") + "", (Object)(row.get("GROUP_ID") + ""));
                }
            }
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occurred while getSelfEnrollmentConfiguredGroups", ex);
        }
        return configuredGroups;
    }
    
    public JSONObject filterStaticUniqueGroupDevices(final Long groupId, final Long[] devicesId) {
        JSONObject staticUniqueDevice = null;
        if (devicesId.length > 0) {
            try {
                final Criteria notInGroupId = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId, 1);
                final Criteria staticUniqueCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)5, 0);
                final Criteria deviceIdCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)devicesId, 8);
                final Criteria criteria = staticUniqueCriteria.and(deviceIdCriteria).and(notInGroupId);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
                final Join customGroup = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
                final Join managedDeviceExtnJoin = new Join("CustomGroupMemberRel", "ManagedDeviceExtn", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
                selectQuery.addJoin(customGroup);
                selectQuery.addJoin(managedDeviceExtnJoin);
                selectQuery.setCriteria(criteria);
                selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
                final DataObject staticUniqueDeviceDO = MDMUtil.getPersistence().get(selectQuery);
                if (!staticUniqueDeviceDO.isEmpty()) {
                    staticUniqueDevice = new JSONObject();
                    final Iterator staticUniqueDeviceIterator = staticUniqueDeviceDO.getRows("ManagedDeviceExtn");
                    while (staticUniqueDeviceIterator.hasNext()) {
                        final Row managedDeviceExtnRow = staticUniqueDeviceIterator.next();
                        staticUniqueDevice.put(managedDeviceExtnRow.get("MANAGED_DEVICE_ID") + "", managedDeviceExtnRow.get("NAME"));
                    }
                }
            }
            catch (final Exception ex) {
                MDMGroupHandler.logger.log(Level.WARNING, "Exception occurred while filterStaticUniqueGroupDevices", ex);
            }
        }
        return staticUniqueDevice;
    }
    
    public static List<Integer> getMDMGroupType() {
        final List<Integer> groupTypeList = new ArrayList<Integer>();
        groupTypeList.add(3);
        groupTypeList.add(4);
        groupTypeList.add(5);
        groupTypeList.add(6);
        return groupTypeList;
    }
    
    public static Integer[] getAllMDMGroupTypes() {
        final Integer[] groupTypes = { 3, 4, 5, 6, 7 };
        return groupTypes;
    }
    
    public static SelectQuery getMDMGroupsCreatedByDCSYSTEMUserQuery(final Long customerId, final List groupType) {
        SelectQuery selectQuery = null;
        try {
            Long dcSysUserId = null;
            try {
                dcSysUserId = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
            }
            catch (final Exception e) {
                MDMGroupHandler.logger.log(Level.WARNING, "Exception occured while obtaining DC System User ID. For Cloud exception is harmless |", e);
            }
            final Criteria customerIdCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria groupTypeCrit = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupType.toArray(), 8);
            final Criteria sysUserCrit = new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)dcSysUserId, 0);
            final Criteria criteria = customerIdCrit.and(groupTypeCrit).and(sysUserCrit);
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(criteria);
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            selectQuery.addSortColumn(sortCol);
            final Column distinctResourceCol = Column.getColumn("Resource", "RESOURCE_ID").distinct();
            distinctResourceCol.setColumnAlias("RESOURCE_ID");
            selectQuery.addSelectColumn(distinctResourceCol);
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "*"));
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occurred while getMDMGroupsCreatedByDCSYSTEMUserQuery", ex);
        }
        return selectQuery;
    }
    
    public static List getMDMGroupsCreatedByDCSYSTEMUSER(final Long customerId) {
        List defGroups = null;
        try {
            defGroups = getCustomGroupDetailsList(getMDMGroupsCreatedByDCSYSTEMUserQuery(customerId, getMDMGroupType()));
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occurred while getMDMGroupsCreatedByDCSYSTEMUSER", ex);
        }
        return defGroups;
    }
    
    public static List<Long> getMemberIdListForGroups(final List groupList, final int resourceType) {
        final List<Integer> list = new ArrayList<Integer>();
        list.add(resourceType);
        return getMemberIdListForGroups(groupList, list);
    }
    
    public static List<Long> getMemberIdListForGroups(final List groupList, final List<Integer> resourceType) {
        List<Long> memberIdList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            final Criteria resourceTypeCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resourceType.toArray(), 8);
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(new Object[groupList.size()]), 8);
            selectQuery.setCriteria(resourceTypeCriteria.and(groupIdCri));
            final DataObject memberIdDO = SyMUtil.getPersistence().get(selectQuery);
            if (!memberIdDO.isEmpty()) {
                Long memberId = null;
                memberIdList = new ArrayList<Long>();
                final Iterator memberIdItr = memberIdDO.getRows("CustomGroupMemberRel");
                Row memberIdRow = null;
                while (memberIdItr.hasNext()) {
                    memberIdRow = memberIdItr.next();
                    memberId = (Long)memberIdRow.get("MEMBER_RESOURCE_ID");
                    memberIdList.add(memberId);
                }
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in getGroupMemberIdList....", e);
        }
        return memberIdList;
    }
    
    public HashMap<Integer, List<Long>> getGroupTypeBasedMapForGroups(final List<Long> groupList) {
        final HashMap<Integer, List<Long>> groupMap = new HashMap<Integer, List<Long>>();
        try {
            final Criteria groupListCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupList.toArray(new Long[groupList.size()]), 8);
            final Column groupTypeCol = new Column("CustomGroup", "GROUP_TYPE");
            final List deviceGroupList = this.getColumnValueAsLongList("CustomGroup", "RESOURCE_ID", groupListCriteria.and(new Criteria(groupTypeCol, (Object)new Integer[] { 3, 4, 5, 6 }, 8)));
            final List userGroupList = this.getColumnValueAsLongList("CustomGroup", "RESOURCE_ID", groupListCriteria.and(new Criteria(groupTypeCol, (Object)7, 0)));
            groupMap.put(6, deviceGroupList);
            groupMap.put(7, userGroupList);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, null, e);
        }
        return groupMap;
    }
    
    private List getColumnValueAsLongList(final String tableName, final String columnName, final Criteria criteria) {
        final List distinctValue = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.distinct();
        query.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    distinctValue.add(Long.valueOf(String.valueOf(value)));
                }
            }
            ds.close();
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "error in getColumnValueAsLongList", e);
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                MDMGroupHandler.logger.log(Level.SEVERE, "error while closing connection in getColumnValueAsLongList", e);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                MDMGroupHandler.logger.log(Level.SEVERE, "error while closing connection in getColumnValueAsLongList", e2);
            }
        }
        return distinctValue;
    }
    
    public List<Long> getSubGroupList(final Collection<Long> groupList) throws Exception {
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        DMDataSetWrapper ds = null;
        try {
            final Set<Long> subGroups = new HashSet<Long>();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                subGroups.add(Long.valueOf(String.valueOf(ds.getValue("MEMBER_RESOURCE_ID"))));
            }
            final List<Long> result = this.getSubGroupList(subGroups);
            if (result != null) {
                subGroups.addAll(result);
            }
            return new ArrayList<Long>(subGroups);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, e, () -> "Exception occurred in getSubGroupList() - input - groupList : " + collection);
            throw e;
        }
    }
    
    public boolean addMembertoGroups(final Long memberResID, final Long[] groupIDs) {
        final boolean isAdded = super.addMembertoGroups(memberResID, groupIDs);
        if (isAdded) {
            final Long[] membersResIDs = { memberResID };
            for (final Long groupID : groupIDs) {
                try {
                    final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupID, membersResIDs);
                    final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(groupID);
                    groupEvent.customerId = groupMap.get("CUSTOMER_ID");
                    groupEvent.groupType = groupMap.get("GROUP_TYPE");
                    this.invokeGroupMemberListener(groupEvent, 1);
                }
                catch (final Exception ex) {
                    MDMGroupHandler.logger.log(Level.SEVERE, ex, () -> "exception while triggering listeners while adding " + n + " to " + n2);
                }
            }
        }
        return isAdded;
    }
    
    public boolean removeMemberfromGroups(final Long memberResID, final Long[] groupIDs, final Long userId) {
        final boolean isDeleted = super.removeMemberfromGroups(memberResID, groupIDs);
        if (isDeleted) {
            final Long[] membersResIDs = { memberResID };
            for (final Long groupID : groupIDs) {
                try {
                    final MDMGroupMemberEvent groupEvent = new MDMGroupMemberEvent(groupID, membersResIDs);
                    final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(groupID);
                    groupEvent.customerId = groupMap.get("CUSTOMER_ID");
                    groupEvent.groupType = groupMap.get("GROUP_TYPE");
                    groupEvent.userId = userId;
                    this.invokeGroupMemberListener(groupEvent, 2);
                }
                catch (final Exception ex) {
                    MDMGroupHandler.logger.log(Level.SEVERE, ex, () -> "exception while triggering listeners while adding " + n + " to " + n2);
                }
            }
        }
        return isDeleted;
    }
    
    public Long[] getDirectGroupMembers(final Long groupID) {
        final List<Long> directGroupMemberIDs = new ArrayList<Long>();
        try {
            final SelectQuery directGroupMembersQuery = SyMUtil.formSelectQuery("CustomGroupMemberRel", new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0).and(new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupID, 0)), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2))), (Criteria)null);
            final DataObject dataObject = MDMUtil.getPersistence().get(directGroupMembersQuery);
            if (dataObject != null && !dataObject.isEmpty() && dataObject.containsTable("CustomGroupMemberRel")) {
                final Iterator iterator = dataObject.getRows("CustomGroupMemberRel");
                while (iterator != null && iterator.hasNext()) {
                    final Row row = iterator.next();
                    directGroupMemberIDs.add((Long)row.get("MEMBER_RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, null, ex);
        }
        return directGroupMemberIDs.toArray(new Long[directGroupMemberIDs.size()]);
    }
    
    public boolean isInCycle(final long groupID) {
        final Set<Long> visited = new HashSet<Long>();
        final Properties initNode = new Properties();
        ((Hashtable<String, Long>)initNode).put("NODE", groupID);
        final HashSet<Long> hashSet = new HashSet<Long>();
        hashSet.add(groupID);
        ((Hashtable<String, HashSet<Long>>)initNode).put("PATH", hashSet);
        final Stack<Properties> stack = new Stack<Properties>();
        stack.push(initNode);
        while (!stack.isEmpty()) {
            final Properties curNodeProps = stack.pop();
            final Long curNode = ((Hashtable<K, Long>)curNodeProps).get("NODE");
            final Set<Long> path = ((Hashtable<K, HashSet<Long>>)curNodeProps).get("PATH");
            if (curNode != null) {
                visited.add(curNode);
                final Long[] directGroupMembers = this.getDirectGroupMembers(curNode);
                for (int i = 0; i < directGroupMembers.length; ++i) {
                    final Long adjacentNode = directGroupMembers[i];
                    if (!visited.contains(adjacentNode)) {
                        final Properties adjacentNodeProps = new Properties();
                        ((Hashtable<String, Long>)adjacentNodeProps).put("NODE", adjacentNode);
                        final HashSet<Long> curentPath = new HashSet<Long>(path);
                        curentPath.add(adjacentNode);
                        ((Hashtable<String, HashSet<Long>>)adjacentNodeProps).put("PATH", curentPath);
                        stack.push(adjacentNodeProps);
                    }
                    else if (path.contains(adjacentNode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isInCycle(final Long[] groupIds) {
        for (int i = 0; i < groupIds.length; ++i) {
            if (this.isInCycle(groupIds[i])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInCycle(final Collection groupIds) {
        for (final Object groupID : groupIds) {
            if (this.isInCycle(Long.valueOf(String.valueOf(groupID)))) {
                return true;
            }
        }
        return false;
    }
    
    public String domainNameForGroupID(final String groupIdStr) {
        String groupDomainName = "MDM";
        try {
            if (!SyMUtil.isStringEmpty(groupIdStr)) {
                final Long groupID = Long.valueOf(groupIdStr);
                groupDomainName = (String)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)groupID, "DOMAIN_NETBIOS_NAME");
            }
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, null, ex);
        }
        return groupDomainName;
    }
    
    public boolean isGroupInSync(final Long[] groupIds, final Long customerId) {
        return MDMIdpsUtil.isGroupInSync(groupIds, customerId);
    }
    
    public HashMap getResourcePropertiesForMultipleGroups(final List<Long> resID) {
        final HashMap resultHash = new HashMap();
        try {
            DMDataSetWrapper ds = null;
            final SelectQuery resPropQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join cusGroupJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join cusGroupUserJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            resPropQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            resPropQuery.addJoin(cusGroupJoin);
            resPropQuery.addJoin(cusGroupUserJoin);
            final Criteria propertyCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resID.toArray(), 8);
            resPropQuery.setCriteria(propertyCriteria);
            try {
                ds = DMDataSetWrapper.executeQuery((Object)resPropQuery);
                while (ds.next()) {
                    final HashMap propertyHash = new HashMap();
                    propertyHash.put("GROUP_ID", ds.getValue("RESOURCE_ID"));
                    propertyHash.put("NAME", ds.getValue("NAME"));
                    propertyHash.put("DOMAIN_NAME", ds.getValue("DOMAIN_NETBIOS_NAME"));
                    propertyHash.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                    propertyHash.put("DB_ADDED_TIME", ds.getValue("DB_ADDED_TIME"));
                    propertyHash.put("DB_UPDATED_TIME", ds.getValue("DB_UPDATED_TIME"));
                    propertyHash.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                    propertyHash.put("GROUP_TYPE", ds.getValue("GROUP_TYPE"));
                    propertyHash.put("GROUP_CATEGORY", ds.getValue("GROUP_CATEGORY"));
                    propertyHash.put("IS_EDITABLE", ds.getValue("IS_EDITABLE"));
                    propertyHash.put("DESCRIPTION", ds.getValue("GROUP_DESCRIPTION"));
                    propertyHash.put("CREATED_BY", ds.getValue("CREATED_BY"));
                    propertyHash.put("LAST_MODIFIED_BY", ds.getValue("LAST_MODIFIED_BY"));
                    resultHash.put(ds.getValue("RESOURCE_ID"), propertyHash);
                }
            }
            catch (final Exception e) {
                MDMGroupHandler.logger.log(Level.WARNING, "Exception occoured in getGroupMemberCount Query Execution....", e);
            }
            MDMGroupHandler.logger.log(Level.INFO, "Properties obtained : ", resultHash);
        }
        catch (final Exception e2) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occoured in getResourceProperties....", e2);
        }
        return resultHash;
    }
    
    public boolean addTableEntriesForMembertoMultipleGroups(final List<Long> groupId, final Long resourceId) {
        boolean returnType = false;
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId.toArray(), 8);
            final Criteria resourceIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cri = groupIdCri.and(resourceIdCri);
            final DataObject dObj = MDMUtil.getPersistenceLite().get("CustomGroupMemberRel", cri);
            dObj.deleteRows("CustomGroupMemberRel", cri);
            for (int i = 0; i < groupId.size(); ++i) {
                final Row cgRelRow = new Row("CustomGroupMemberRel");
                cgRelRow.set("GROUP_RESOURCE_ID", (Object)groupId.get(i));
                cgRelRow.set("MEMBER_RESOURCE_ID", (Object)resourceId);
                dObj.addRow(cgRelRow);
            }
            MDMUtil.getPersistenceLite().update(dObj);
            returnType = true;
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in addMembertoMultipleGroups....", e);
        }
        return returnType;
    }
    
    public boolean checkGroupsCategory(final List<Long> groupId, final int categoryType) {
        Boolean doesGroupBelongToCategory = Boolean.FALSE;
        try {
            final Criteria cr = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId.toArray(), 8);
            final Criteria grpCategoryCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)categoryType, 1);
            final DataObject dataObj = MDMUtil.getPersistenceLite().get("CustomGroup", cr.and(grpCategoryCriteria));
            doesGroupBelongToCategory = !dataObj.isEmpty();
        }
        catch (final DataAccessException ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        return doesGroupBelongToCategory;
    }
    
    public static List<Long> getCustomGroupDetailsForMultipleGroups(final String customGroupName, final Long technicianUserID, final Long customerID) throws SyMException {
        final List<Long> returnList = new ArrayList<Long>();
        try {
            final String[] groupNameArray = customGroupName.split(";");
            final Long loginId = DMUserHandler.getLoginIdForUserId(technicianUserID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroup", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
            final Criteria nameCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)groupNameArray, 8, false);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)getMDMGroupType().toArray(), 8);
            final Criteria createdByCriteria = new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)technicianUserID, 0);
            final Criteria loginIdCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginId, 0);
            final Criteria baseCriteria = nameCriteria.and(customerCriteria);
            Criteria finalCriteriaBasedOnRole = null;
            if (DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices")) {
                finalCriteriaBasedOnRole = baseCriteria;
            }
            else {
                finalCriteriaBasedOnRole = baseCriteria.and(createdByCriteria.or(loginIdCriteria));
            }
            selectQuery.setCriteria(finalCriteriaBasedOnRole);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            MDMGroupHandler.logger.log(Level.FINE, "Query to get Custom Group : {0}", selectQuery);
            final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
            MDMGroupHandler.logger.log(Level.FINE, "DataObject Obtained : {0}", dobj);
            if (dobj.isEmpty() || !dobj.containsTable("CustomGroup")) {
                throw new SyMException(14006, "Group not found", "dc.mdm.enroll.group_not_available", (Throwable)null);
            }
            final Iterator iterator = dobj.getRows("CustomGroup", groupTypeCriteria);
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (row == null) {
                    throw new SyMException(14008, "Group belongs to a different platform", "dc.mdm.device_mgmt.group_type_mismatch", (Throwable)null);
                }
                returnList.add((Long)row.get("RESOURCE_ID"));
            }
        }
        catch (final SyMException ex) {
            throw ex;
        }
        catch (final DataAccessException ex2) {
            MDMGroupHandler.logger.log(Level.SEVERE, null, (Throwable)ex2);
        }
        return returnList;
    }
    
    public List<Long> getGroupsForResourceId(final Long resourceId) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria customGroupMemberRel = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0);
            query.addJoin(customGroupJoin);
            query.setCriteria(customGroupMemberRel);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final List<Long> groups = new ArrayList<Long>();
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("CustomGroupMemberRel");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    groups.add((Long)row.get("GROUP_RESOURCE_ID"));
                }
            }
            return groups;
        }
        catch (final DataAccessException ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error in obtaining static unique group for resource id {0}", (Throwable)ex);
            return null;
        }
    }
    
    public JSONObject getGroupTypeWiseCount(final Long customerID) {
        JSONObject response = null;
        final SelectQuery selectQuery = this.getGroupTypeWiseCountQuery(customerID);
        final org.json.simple.JSONArray result = MDMUtil.executeSelectQuery(selectQuery);
        try {
            if (result.size() > 0) {
                final org.json.simple.JSONObject object = (org.json.simple.JSONObject)result.get(0);
                response = new JSONObject();
                for (final Object key : object.keySet()) {
                    response.put(key.toString(), (Object)object.get(key).toString());
                }
            }
        }
        catch (final JSONException e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception occurred in getGroupTypeWiseCount", (Throwable)e);
        }
        return response;
    }
    
    public SelectQuery getGroupTypeWiseCountQuery(final Long customerID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria deviceGroupCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)getMDMGroupType().toArray(), 8);
        final CaseExpression deviceGroupCase = new CaseExpression("Device_Group_Count");
        deviceGroupCase.addWhen(deviceGroupCriteria, (Object)1);
        final Criteria userGroupCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 2));
        final CaseExpression userGroupCase = new CaseExpression("User_Group_Count");
        userGroupCase.addWhen(userGroupCriteria, (Object)1);
        final Criteria adGroupCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 3));
        final CaseExpression adUserGroupCase = new CaseExpression("AD_User_Group_Count");
        adUserGroupCase.addWhen(adGroupCriteria, (Object)1);
        final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
        selectQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(deviceGroupCase, 4, "Device_Group_Count"));
        selectQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(userGroupCase, 4, "User_Group_Count"));
        selectQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(adUserGroupCase, 4, "AD_User_Group_Count"));
        selectQuery.setCriteria(customerCriteria);
        return selectQuery;
    }
    
    public MultiMap getGroupsForResourceIdList(final List<Long> resourceId) {
        try {
            final MultiMap multiMap = (MultiMap)new MultiHashMap();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria customGroupMemberRel = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId.toArray(), 8);
            query.addJoin(customGroupJoin);
            query.setCriteria(customGroupMemberRel);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final List<Long> groups = new ArrayList<Long>();
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("CustomGroupMemberRel");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    multiMap.put((Object)row.get("MEMBER_RESOURCE_ID"), (Object)row.get("GROUP_RESOURCE_ID"));
                }
            }
            return multiMap;
        }
        catch (final DataAccessException ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error in obtaining static unique group for resource id list!", (Throwable)ex);
            return null;
        }
    }
    
    public static List getMDMNonUserGroupsWithEncodedGroupName() {
        final List mdmCustomGroupList = new ArrayList();
        try {
            final List groupTypeList = getMDMGroupType();
            groupTypeList.remove(new Integer(7));
            final SelectQuery query = getCustomGroupsQuery(groupTypeList, Boolean.FALSE);
            query.setCriteria(query.getCriteria().and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0)));
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Hashtable customGpHash = new Hashtable();
                final Long groupId = (Long)ds.getValue("RESOURCE_ID");
                customGpHash.put("CUSTOM_GP_ID", ds.getValue("RESOURCE_ID"));
                customGpHash.put("CUSTOM_GP_NAME", DMIAMEncoder.encodeHTML((String)ds.getValue("NAME")));
                customGpHash.put("CUSTOM_GP_TYPE", ds.getValue("GROUP_TYPE"));
                customGpHash.put("CUSTOM_GP_MEMBER_COUNT", getGroupMemberCount(groupId));
                customGpHash.put("CUSTOM_GP_CATEGORY", ds.getValue("GROUP_CATEGORY"));
                mdmCustomGroupList.add(customGpHash);
            }
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.WARNING, "Exception occoured in getMDMNonUserGroupsWithEncodedGroupName....{0}", ex);
        }
        return mdmCustomGroupList;
    }
    
    public List getGroupList(final List resourceList) {
        if (resourceList != null && resourceList.size() != 0) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
            selectQuery.addJoin(new Join("CustomGroup", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
            List groupList = null;
            try {
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator itr = dataObject.getRows("CustomGroup");
                while (itr.hasNext()) {
                    if (groupList == null) {
                        groupList = new ArrayList();
                    }
                    final Row row = itr.next();
                    groupList.add(Long.valueOf(String.valueOf(row.get("RESOURCE_ID"))));
                }
            }
            catch (final Exception e) {
                MDMGroupHandler.logger.log(Level.SEVERE, "Exception occurred...", e);
            }
            return groupList;
        }
        return null;
    }
    
    private void addMemeberToGroupEventLogEntry(final MDMGroupMemberEvent groupMemberEvent, final int operation) {
        final int eventConstant = 72424;
        String sEventLogRemarksKey = null;
        final Long[] memberIds = groupMemberEvent.memberIds;
        try {
            Object remarksArgs = null;
            Map<Long, String> resourceNames = null;
            if (groupMemberEvent.groupType == 7) {
                resourceNames = ManagedUserHandler.getInstance().getManagedUserMapForUserIds(memberIds);
                sEventLogRemarksKey = ((operation == 1) ? "mdm.user.group.member.added" : "mdm.user.group.member.removed");
            }
            else if (groupMemberEvent.groupType == 6) {
                resourceNames = ManagedDeviceHandler.getInstance().getCustomDeviceNameMapFromDB(memberIds);
                sEventLogRemarksKey = ((operation == 1) ? "mdm.device.group.member.added" : "mdm.device.group.member.removed");
            }
            String sLoggedOnUserName = null;
            if (groupMemberEvent.userId != null) {
                sLoggedOnUserName = DMUserHandler.getUserNameFromUserID(groupMemberEvent.userId);
            }
            final String groupName = MDMResourceDataProvider.getResourceName(groupMemberEvent.groupID);
            if (sEventLogRemarksKey != null && resourceNames != null) {
                for (final Map.Entry<Long, String> entry : resourceNames.entrySet()) {
                    remarksArgs = entry.getValue() + "@@@" + groupName;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(eventConstant, null, sLoggedOnUserName, sEventLogRemarksKey, remarksArgs, groupMemberEvent.customerId);
                }
            }
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in addMemeberToGroupEventLogEntry", ex);
        }
    }
    
    public void moveMemberToGroupEventLogEntry(final JSONObject moveGroupJson) {
        try {
            Object remarksArgs = null;
            final int groupType = moveGroupJson.optInt("groupType");
            if (groupType != 7) {
                final int eventConstant = 72424;
                final String sEventLogRemarksKey = "mdm.device.group.member.moved";
                final Long sourceGroupId = moveGroupJson.optLong("sourceGroupId");
                final Long targetGroupId = moveGroupJson.optLong("targetGroupId");
                final Long customerId = moveGroupJson.optLong("customerId");
                final JSONArray memberIds = moveGroupJson.getJSONArray("memberIds");
                final String sourceGroupName = MDMResourceDataProvider.getResourceName(sourceGroupId);
                final String targetGroupName = MDMResourceDataProvider.getResourceName(targetGroupId);
                final String sLoggedOnUserName = String.valueOf(moveGroupJson.get("UserName"));
                final List<Long> memberIdList = new ArrayList<Long>();
                for (int index = 0; index < memberIds.length(); ++index) {
                    memberIdList.add(memberIds.optLong(index));
                }
                final Map<Long, String> resourceNames = ManagedDeviceHandler.getInstance().getCustomDeviceNameMapFromDB(memberIdList.toArray(new Long[memberIdList.size()]));
                for (final Map.Entry<Long, String> entry : resourceNames.entrySet()) {
                    remarksArgs = entry.getValue() + "@@@" + sourceGroupName + "@@@" + targetGroupName;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(eventConstant, null, sLoggedOnUserName, sEventLogRemarksKey, remarksArgs, customerId);
                }
            }
        }
        catch (final Exception ex) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in moveMemberToGroupEventLogEntry", ex);
        }
    }
    
    public List<Long> getMDMUserGroupList() {
        List customGpList = null;
        final List groupTypeList = new ArrayList();
        groupTypeList.add(new Integer(7));
        final SelectQuery query = getCustomGroupsQuery(groupTypeList, Boolean.FALSE, Boolean.TRUE);
        query.setCriteria(query.getCriteria().and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0)));
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)query);
            customGpList = new ArrayList();
            while (dmDataSetWrapper.next()) {
                final Long groupId = (Long)dmDataSetWrapper.getValue("RESOURCE_ID");
                customGpList.add(groupId);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occoured in getMDMUserGroupList....", e);
        }
        return customGpList;
    }
    
    public void updateGroupActionStatus(final long groupActionID, final String status, final String remarks) {
        try {
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("GroupActionHistory");
            final Criteria actionIdCriteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionID, 0);
            uq.setCriteria(actionIdCriteria);
            uq.setUpdateColumn("ACTION_STATUS", (Object)status);
            uq.setUpdateColumn("ACTION_REMARKS", (Object)remarks);
            MDMUtil.getPersistence().update(uq);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error while updating ActionStatus for groupActionID:{0}", groupActionID);
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in updateGroupActionStatus", e);
        }
    }
    
    public void updateGroupRemarks(final long groupActionID, final String reasonMessage, final Long userID) {
        try {
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("GroupActionHistory");
            final Criteria actionIdCriteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionID, 0);
            uq.setCriteria(actionIdCriteria);
            uq.setUpdateColumn("REASON_MESSAGE", (Object)reasonMessage);
            uq.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            uq.setUpdateColumn("LAST_MODIFIED_BY", (Object)userID);
            MDMUtil.getPersistence().update(uq);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Error while updating the remarks for groupActionID{0}", groupActionID);
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in updateGroupRemarks", e);
        }
    }
    
    public long populateGroupActionDetails(final Map groupActionMap) {
        Long groupActionId = -1L;
        try {
            final Long groupId = Long.valueOf(groupActionMap.get("group_id").toString());
            final Long userId = Long.valueOf(groupActionMap.get("user_id").toString());
            Integer groupActionStatus = 4;
            String groupActionRemarks = "mdm.bulkaction.inProgress_remarks";
            if (groupActionMap.containsKey("scheduled")) {
                groupActionStatus = 7;
                groupActionRemarks = "mdm.bulkaction.device_schedule_remarks";
            }
            final Long time = System.currentTimeMillis();
            DataObject groupActionDO = MDMUtil.getPersistence().constructDataObject();
            Row groupActionRow = new Row("GroupActionHistory");
            groupActionRow.set("GROUP_ID", (Object)groupId);
            groupActionRow.set("ACTION_ID", groupActionMap.get("action"));
            groupActionRow.set("REASON_MESSAGE", groupActionMap.get("reason"));
            groupActionRow.set("ACTION_REMARKS", (Object)groupActionRemarks);
            groupActionRow.set("INITIATED_BY", (Object)userId);
            groupActionRow.set("INITIATED_TIME", (Object)time);
            groupActionRow.set("ACTION_STATUS", (Object)groupActionStatus);
            groupActionRow.set("LAST_MODIFIED_BY", (Object)userId);
            groupActionRow.set("LAST_MODIFIED_TIME", (Object)time);
            groupActionDO.addRow(groupActionRow);
            groupActionDO = MDMUtil.getPersistence().add(groupActionDO);
            groupActionRow = groupActionDO.getFirstRow("GroupActionHistory");
            groupActionId = (Long)groupActionRow.get("GROUP_ACTION_ID");
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occurred in populateGroupActionDetails....", e);
        }
        return groupActionId;
    }
    
    public void addGroupActionToClearedAppMapping(final Map infoMap, final Long groupActionId) {
        try {
            final Collection<Long> app_group_ids = infoMap.get("app_group_ids");
            final DataObject actionCommandDO = MDMUtil.getPersistence().constructDataObject();
            for (final Long app_group_id : app_group_ids) {
                final Row actionCommandRow = new Row("GroupActionToResetApps");
                actionCommandRow.set("GROUP_ACTION_ID", (Object)groupActionId);
                actionCommandRow.set("APP_GROUP_ID", (Object)app_group_id);
                actionCommandDO.addRow(actionCommandRow);
            }
            MDMUtil.getPersistence().add(actionCommandDO);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception occurred in  addGroupActionToClearedAppMapping()   ", e);
        }
    }
    
    public void addGroupToCommandMapping(final JSONObject commandDetailsJSON, final Long groupActionId) {
        try {
            final DataObject actionCommandDO = MDMUtil.getPersistence().constructDataObject();
            final Iterator<String> resourceItr = commandDetailsJSON.keys();
            while (resourceItr.hasNext()) {
                final JSONObject commandJSON = commandDetailsJSON.getJSONArray((String)resourceItr.next()).getJSONObject(0);
                final Long commandHistoryID = commandJSON.getLong("COMMAND_HISTORY_ID");
                final Row actionCommandRow = new Row("GroupActionToCommand");
                actionCommandRow.set("GROUP_ACTION_ID", (Object)groupActionId);
                actionCommandRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryID);
                actionCommandDO.addRow(actionCommandRow);
            }
            MDMUtil.getPersistence().add(actionCommandDO);
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception occurred in  addGroupToCommandMapping()   ", e);
        }
    }
    
    public void populateGroupActionDetails(final Long actionId, final int resCount, final JSONObject commandJSON, final String commandName, final Map info) {
        try {
            this.addOrUpdateGroupActionsResourceCount(actionId, resCount);
            this.addGroupToCommandMapping(commandJSON, actionId);
            if (commandName.equals("ClearAppData")) {
                this.addGroupActionToClearedAppMapping(info, actionId);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception occurred in  populateGroupActionDetails()   ", e);
        }
    }
    
    public void addOrUpdateGroupActionsResourceCount(final Long actionID, final int count) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupActionHistory"));
            selectQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionID, 0));
            final DataObject groupActionDO = MDMUtil.getPersistence().get(selectQuery);
            if (groupActionDO != null) {
                final Row groupActionRow = groupActionDO.getRow("GroupActionHistory");
                final int inProgCount = Integer.valueOf(groupActionRow.get("INITIATED_COUNT").toString());
                groupActionRow.set("INPROGRESS_COUNT", (Object)(count + inProgCount));
                groupActionRow.set("INITIATED_COUNT", (Object)(count + inProgCount));
                groupActionDO.updateRow(groupActionRow);
                MDMUtil.getPersistence().update(groupActionDO);
            }
        }
        catch (final Exception e) {
            MDMGroupHandler.out.log(Level.WARNING, "Exception occurred in addOrUpdateGroupActionsResourceCount....", e);
        }
    }
    
    public List getDeviceListFromResourceList(final List resList) {
        final List deviceList = new ArrayList();
        final List groupList = getInstance().getGroupList(resList);
        List userList = ManagedUserHandler.getInstance().getUserList(resList);
        final List resTypeList = new ArrayList();
        resTypeList.add(120);
        resTypeList.add(121);
        if (groupList != null) {
            resList.removeAll(groupList);
            deviceList.addAll(getMemberIdListForGroups(groupList, resTypeList));
            if (userList == null) {
                userList = new ArrayList();
            }
            userList.addAll(getMemberIdListForGroups(groupList, 2));
        }
        if (userList != null) {
            resList.removeAll(userList);
            deviceList.addAll(ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList));
        }
        final Set set = new HashSet(deviceList);
        final Iterator iterator = set.iterator();
        final ArrayList finalResourceList = new ArrayList();
        while (iterator.hasNext()) {
            finalResourceList.add(iterator.next());
        }
        finalResourceList.addAll(resList);
        return finalResourceList;
    }
    
    public void updateGroupDescription(final Long groupId, final String description) {
        super.updateGroupDescription(groupId, description);
        final GroupEvent groupEvent = new GroupEvent(groupId);
        MDMCustomGroupHandler.getInstance().invokeGroupListeners(groupEvent, 3);
    }
    
    public List filterGroupsByGroupType(final List groupList, final Integer groupType) throws Exception {
        try {
            MDMGroupHandler.logger.log(Level.INFO, "Getting userGroups from the givenGroupsList{0}", new Object[] { groupList });
            final Criteria groupCriteria = new Criteria(new Column("CustomGroup", "RESOURCE_ID"), (Object)groupList.toArray(), 8);
            final Criteria groupTypeCriteria = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)groupType, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("CustomGroup", groupCriteria.and(groupTypeCriteria));
            final Iterator<Row> iterator = dataObject.getRows("CustomGroup");
            return DBUtil.getColumnValuesAsList((Iterator)iterator, "RESOURCE_ID");
        }
        catch (final Exception e) {
            MDMGroupHandler.logger.log(Level.SEVERE, "Exception in getUserGroupsFromGroups", e);
            throw e;
        }
    }
    
    public void addImmutableGroup(final Long groupId) {
        try {
            DataObject dataObject = DBUtil.getDataObjectFromDB("ImmutableCustomGroups", "CUSTOM_GROUP_ID", (Object)groupId);
            if (dataObject != null) {
                MDMGroupHandler.out.log(Level.INFO, "Group {0} is already a Immutable group", new Object[] { groupId });
                return;
            }
            dataObject = (DataObject)new WritableDataObject();
            final Row customGroupRow = new Row("ImmutableCustomGroups");
            customGroupRow.set("CUSTOM_GROUP_ID", (Object)groupId);
            dataObject.addRow(customGroupRow);
            SyMUtil.getPersistence().add(dataObject);
            MDMGroupHandler.out.log(Level.INFO, "Added group {0} as Immutable group", new Object[] { groupId });
        }
        catch (final Exception ex) {
            MDMGroupHandler.out.log(Level.SEVERE, "Exception occurred while adding group {0} as Immutable group: {1}", new Object[] { groupId, ex });
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        MDMGroupHandler.mdmCgHandler = null;
        MDMGroupHandler.logger = Logger.getLogger("MDMLogger");
    }
}
