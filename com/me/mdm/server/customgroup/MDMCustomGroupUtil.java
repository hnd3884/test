package com.me.mdm.server.customgroup;

import java.util.Collection;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.SortColumn;
import java.util.Arrays;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import java.util.Set;
import java.util.HashSet;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.me.mdm.server.role.RBDAUtil;
import java.util.HashMap;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;

public class MDMCustomGroupUtil extends CustomGroupUtil
{
    private static final int MDM_GROUP_APP_INSTALLED = 4;
    private static MDMCustomGroupUtil mdmcgUtil;
    protected static final Logger LOGGER;
    
    public static MDMCustomGroupUtil getInstance() {
        if (MDMCustomGroupUtil.mdmcgUtil == null) {
            MDMCustomGroupUtil.mdmcgUtil = new MDMCustomGroupUtil();
        }
        return MDMCustomGroupUtil.mdmcgUtil;
    }
    
    public boolean checkIfExist(final String groupName, final String domainName) {
        boolean ret = true;
        try {
            final SelectQuery sq = this.getCheckIfExistQuery(groupName, domainName, null);
            final DataObject contains = MDMUtil.getPersistence().get(sq);
            ret = (contains.size("Resource") > 0);
        }
        catch (final Exception e) {
            MDMCustomGroupUtil.LOGGER.log(Level.WARNING, "Exception occured in checkIfExist....", e);
        }
        return ret;
    }
    
    public boolean checkIfExist(final String groupName, final String domainName, final Long customerId) {
        boolean ret = true;
        try {
            final SelectQuery sq = this.getCheckIfExistQuery(groupName, domainName, customerId);
            final DataObject contains = MDMUtil.getPersistence().get(sq);
            ret = (contains.size("Resource") > 0);
        }
        catch (final Exception e) {
            MDMCustomGroupUtil.LOGGER.log(Level.WARNING, "Exception occured in checkIfExist....", e);
        }
        return ret;
    }
    
    public SelectQuery getCheckIfExistQuery(final String groupName, final String domainName, final Long customerId) {
        SelectQuery sq = null;
        try {
            Criteria isPresent = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer(101), 0);
            if (customerId != null) {
                final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                isPresent = isPresent.and(customerCri);
            }
            if (groupName != null && !groupName.equals("")) {
                final Criteria isPresent2 = new Criteria(Column.getColumn("Resource", "NAME"), (Object)groupName, 0, false);
                isPresent = isPresent.and(isPresent2);
            }
            if (domainName != null && !domainName.equals("")) {
                final Criteria domainCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false);
                isPresent = isPresent.and(domainCri);
            }
            final Table resourceTable = Table.getTable("Resource");
            sq = (SelectQuery)new SelectQueryImpl(resourceTable);
            final ArrayList colList = new ArrayList();
            colList.add(Column.getColumn("Resource", "RESOURCE_ID"));
            colList.add(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
            colList.add(Column.getColumn("Resource", "RESOURCE_TYPE"));
            colList.add(Column.getColumn("Resource", "NAME"));
            colList.add(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            colList.add(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            sq.addSelectColumns((List)colList);
            sq.setCriteria(isPresent);
            final Join join1 = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            sq.addJoin(join1);
        }
        catch (final Exception e) {
            MDMCustomGroupUtil.LOGGER.log(Level.WARNING, "Exception occoured in getCheckIfExistQuery....", e);
        }
        return sq;
    }
    
    public SelectQuery getGroupProfileQuery(final Long resID) {
        final SelectQuery profileCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        final Join profileJoin = new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Column profileCol = new Column("RecentProfileForGroup", "PROFILE_ID");
        profileCountQuery.addJoin(profileJoin);
        final Column groupCol = new Column("RecentProfileForGroup", "GROUP_ID");
        final Column profileIdCol = new Column("Profile", "PROFILE_ID");
        final Column profileCountCol = profileCol.count();
        profileCountCol.setColumnAlias("PROFILE_COUNT");
        final Column appCountCol = profileCol.count();
        appCountCol.setColumnAlias("APP_COUNT");
        profileCountQuery.addSelectColumn(profileIdCol);
        profileCountQuery.addSelectColumn(profileCol);
        profileCountQuery.addSelectColumn(profileCountCol);
        profileCountQuery.addSelectColumn(appCountCol);
        profileCountQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        final Criteria cri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)resID, 0);
        profileCountQuery.setCriteria(cri);
        final List list = new ArrayList();
        list.add(groupCol);
        final GroupByClause groupBy = new GroupByClause(list);
        profileCountQuery.setGroupByClause(groupBy);
        return profileCountQuery;
    }
    
    public int getAppGroupInstalledCount(final Long resID) {
        int appInstallCount = 0;
        try {
            final SelectQuery appInstallCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupToProfileHistory"));
            final Column resIdCol = Column.getColumn("GroupToProfileHistory", "GROUP_ID");
            final Column installCountCol = resIdCol.count();
            installCountCol.setColumnAlias("APP_INSTALL_COUNT");
            appInstallCountQuery.addSelectColumn(installCountCol);
            final Join profileJoin = new Join("GroupToProfileHistory", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join recentProfileJoin = new Join("GroupToProfileHistory", "RecentProfileForGroup", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            appInstallCountQuery.addJoin(profileJoin);
            appInstallCountQuery.addJoin(recentProfileJoin);
            final Criteria resIdCri = new Criteria(Column.getColumn("GroupToProfileHistory", "GROUP_ID"), (Object)resID, 0);
            final Criteria statusCri = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_STATUS"), (Object)4, 0);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
            final Criteria cri = resIdCri.and(statusCri).and(profileTypeCri);
            appInstallCountQuery.setCriteria(cri);
            final List list = new ArrayList();
            list.add(resIdCol);
            final GroupByClause groupBy = new GroupByClause(list);
            appInstallCountQuery.setGroupByClause(groupBy);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)appInstallCountQuery);
                if (ds.next()) {
                    appInstallCount = (int)ds.getValue("APP_INSTALL_COUNT");
                }
            }
            catch (final Exception e) {
                MDMCustomGroupUtil.LOGGER.log(Level.WARNING, "Exception occoured in getAppGroupInstalledCount Query Execution....", e);
            }
        }
        catch (final Exception e2) {
            MDMCustomGroupUtil.LOGGER.log(Level.WARNING, "Exception occoured in getAppGroupInstalledCount....", e2);
        }
        return appInstallCount;
    }
    
    public int getGroupMemberCount(final Long resID) {
        int memberCount = 0;
        try {
            final SelectQuery memberCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Join resourceJoin = new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            memberCountQuery.addJoin(resourceJoin);
            final Column memberCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
            final Column memberCountCol = memberCol.count();
            memberCountCol.setColumnAlias("MEMBER_COUNT");
            memberCountQuery.addSelectColumn(memberCountCol);
            Criteria cri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)resID, 0);
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(resID);
            final Integer groupType = groupMap.get("GROUP_TYPE");
            if (groupType != null && groupType != 7) {
                final Join managedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                memberCountQuery.addJoin(managedDeviceJoin);
                cri = cri.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            }
            memberCountQuery.setCriteria(cri);
            final Column customGroupColumn = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
            final List list = new ArrayList();
            list.add(customGroupColumn);
            final GroupByClause groupBy = new GroupByClause(list);
            memberCountQuery.setGroupByClause(groupBy);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)memberCountQuery);
                if (ds.next()) {
                    memberCount = (int)ds.getValue("MEMBER_COUNT");
                }
            }
            catch (final Exception e) {
                MDMCustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in getGroupMemberCount Query Execution....", e);
            }
        }
        catch (final Exception e2) {
            MDMCustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in getGroupMemberCount....", e2);
        }
        return memberCount;
    }
    
    public List getAssociatedGroupName(final Long resId) {
        final List groupList = new ArrayList();
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        final Join customGroup = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join resourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        query.addJoin(customGroup);
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        query.addJoin(resourceJoin);
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        final Criteria cRes = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resId, 0);
        final Criteria gRes = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)new int[] { 9, 8 }, 9);
        query.setCriteria(cRes.and(gRes));
        try {
            final DataObject DO = MDMUtil.getPersistence().get(query);
            if (!DO.isEmpty()) {
                final Iterator iGroup = DO.getRows("Resource");
                while (iGroup.hasNext()) {
                    final Row row = iGroup.next();
                    groupList.add(row.get("NAME"));
                }
            }
        }
        catch (final Exception ex) {
            MDMCustomGroupUtil.logger.log(Level.SEVERE, "Exception while getting associated group name", ex);
        }
        return groupList;
    }
    
    public SelectQuery getQueryforGroupControllers(final SelectQuery selectQuery, final boolean isMDM) {
        try {
            final Table resourceTable = Table.getTable("Resource");
            final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            subSQ.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            subSQ.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria notNullcri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
            cri = cri.and(notNullcri);
            final Criteria nullcri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
            final Column column_config_data = Column.getColumn("ManagedDevice", "RESOURCE_ID").count();
            column_config_data.setColumnAlias("MEMBER_RESOURCE_ID");
            subSQ.addSelectColumn(column_config_data);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
            list.add(groupByCol);
            final GroupByClause memberGroupBy = new GroupByClause(list);
            subSQ.setGroupByClause(memberGroupBy);
            final DerivedTable groupDerievedTab = new DerivedTable("CustomGroupMemberRel", (Query)subSQ);
            selectQuery.addJoin(new Join(resourceTable, (Table)groupDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
        }
        catch (final Exception e) {
            MDMCustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in queryforGroupControllerViews....", e);
        }
        return selectQuery;
    }
    
    public HashMap getPlatformBasedMemberIdForGroups(final List groupList) {
        final HashMap map = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.setDistinct(true);
            final Set ios = new HashSet();
            final Set android = new HashSet();
            final Set windows = new HashSet();
            final Set chrome = new HashSet();
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final int platform = (int)ds.getValue("PLATFORM_TYPE");
                final Long resourceId = (Long)ds.getValue("MEMBER_RESOURCE_ID");
                switch (platform) {
                    case 1: {
                        ios.add(resourceId);
                        continue;
                    }
                    case 2: {
                        android.add(resourceId);
                        continue;
                    }
                    case 3: {
                        windows.add(resourceId);
                        continue;
                    }
                    case 4: {
                        chrome.add(resourceId);
                        continue;
                    }
                }
            }
            map.put(1, ios);
            map.put(2, android);
            map.put(3, windows);
            map.put(4, chrome);
        }
        catch (final Exception ex) {
            MDMCustomGroupUtil.logger.log(Level.SEVERE, "Exception in getMemberGroupsId", ex);
        }
        return map;
    }
    
    public HashMap getMemberIdsForGroup(final List groupList, final Integer platformType) {
        final HashMap groupMembers = new HashMap();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomGroupMemberRel"));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria criteria = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
            if (platformType != null) {
                criteria = criteria.and(new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0));
            }
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(new Column("CustomGroupMemberRel", "*"));
            selectQuery.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(new Column("CustomGroup", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("CustomGroup");
                while (iterator.hasNext()) {
                    final Row customGroupRow = iterator.next();
                    final Long groupId = (Long)customGroupRow.get("RESOURCE_ID");
                    final Criteria groupCriteria = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                    final Iterator deviceIterator = dataObject.getRows("ManagedDevice", groupCriteria);
                    final HashSet ios = new HashSet();
                    final HashSet android = new HashSet();
                    final HashSet windows = new HashSet();
                    final HashSet chrome = new HashSet();
                    final Map groupPlatformMember = new HashMap();
                    while (deviceIterator.hasNext()) {
                        final Row deviceRow = deviceIterator.next();
                        final Long resourceId = (Long)deviceRow.get("RESOURCE_ID");
                        final int devicePlatform = (int)deviceRow.get("PLATFORM_TYPE");
                        switch (devicePlatform) {
                            case 1: {
                                ios.add(resourceId);
                                continue;
                            }
                            case 2: {
                                android.add(resourceId);
                                continue;
                            }
                            case 3: {
                                windows.add(resourceId);
                                continue;
                            }
                            case 4: {
                                chrome.add(resourceId);
                                continue;
                            }
                        }
                    }
                    groupPlatformMember.put(1, ios);
                    groupPlatformMember.put(2, android);
                    groupPlatformMember.put(3, windows);
                    groupPlatformMember.put(4, chrome);
                    groupMembers.put(groupId, groupPlatformMember);
                }
            }
        }
        catch (final DataAccessException e) {
            MDMCustomGroupUtil.logger.log(Level.SEVERE, "Exception in getting group platform member", (Throwable)e);
        }
        return groupMembers;
    }
    
    public List getModifiedCollectionIDList(final long lastUpdateTime) {
        MDMCustomGroupUtil.logger.log(Level.FINE, "Inside getModifiedCollectionIDList Method");
        final List collectionList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
            selectQuery.addJoin(new Join("CollnToResources", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CollnToResources", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Column collidColumn = Column.getColumn("CollnToResources", "COLLECTION_ID");
            selectQuery.addSelectColumn(collidColumn);
            Criteria criteria = new Criteria(Column.getColumn("CollnToResources", "AGENT_APPLIED_TIME"), (Object)lastUpdateTime, 4);
            final Criteria appProfile = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new Integer[] { 1, 2, 10, 9 }, 8);
            criteria = criteria.and(appProfile).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 1));
            selectQuery.setCriteria(criteria);
            selectQuery.setGroupByClause(new GroupByClause((List)Arrays.asList(collidColumn)));
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper.next()) {
                collectionList.add(dataSetWrapper.getValue("COLLECTION_ID"));
            }
        }
        catch (final Exception exp) {
            MDMCustomGroupUtil.logger.log(Level.SEVERE, "exception in getModifiedCollectionIDList...", exp);
        }
        MDMCustomGroupUtil.logger.log(Level.FINE, "getModifiedCollectionIDList {0}", collectionList);
        return collectionList;
    }
    
    @Deprecated
    public HashMap<Long, String> getGroupNamesWithResourceID() {
        return this.getGroupNamesWithResourceID(-1L);
    }
    
    public HashMap<Long, String> getGroupNamesWithResourceID(final Long customerId) {
        final int customGroup8 = 8;
        final int customGroup9 = 9;
        final HashMap groupForResourceIDMap = new HashMap();
        try {
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            squery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            squery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            squery.addJoin(new Join("CustomGroupMemberRel", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addSortColumn(new SortColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID", true));
            final Criteria groupTypeCri1 = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)customGroup8, 1);
            final Criteria groupTypeCri2 = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)customGroup9, 1);
            Criteria criteria = groupTypeCri1.and(groupTypeCri2);
            if (customerId != -1L) {
                final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = criteria.and(customerCri);
            }
            squery.setCriteria(criteria);
            final DataObject groupDO = MDMUtil.getPersistence().get(squery);
            final Iterator grpResIter = groupDO.getRows("CustomGroupMemberRel");
            while (grpResIter.hasNext()) {
                String groupNameString = "--";
                final Row grpResRow = grpResIter.next();
                final Long memberResourceID = (Long)grpResRow.get("MEMBER_RESOURCE_ID");
                final Long grpResourceID = (Long)grpResRow.get("GROUP_RESOURCE_ID");
                final String groupName = (String)groupDO.getValue("Resource", "NAME", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)grpResourceID, 0));
                if (groupForResourceIDMap.containsKey(memberResourceID)) {
                    final String existingGroup = groupForResourceIDMap.get(memberResourceID);
                    groupNameString = existingGroup + "," + groupName;
                }
                else {
                    groupNameString = groupName;
                }
                groupForResourceIDMap.put(memberResourceID, groupNameString);
            }
        }
        catch (final Exception ex) {
            MDMCustomGroupUtil.logger.log(Level.SEVERE, "Exception occoured in getGroupNamesWithResourceID....", ex);
        }
        return groupForResourceIDMap;
    }
    
    public List getModifiedGroupActionIDList(final long lastUpdateTime) throws DataAccessException {
        MDMCustomGroupUtil.logger.log(Level.FINE, "Inside getModifiedGroupActionIDList Method");
        List actionList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
        selectQuery.addJoin(new Join("GroupActionHistory", "GroupActionToCommand", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2));
        selectQuery.addJoin(new Join("GroupActionToCommand", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
        selectQuery.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        final Column actionIdColumn = Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID");
        selectQuery.addSelectColumn(actionIdColumn);
        Criteria cmdHisCriteria = new Criteria(Column.getColumn("CommandHistory", "UPDATED_TIME"), (Object)lastUpdateTime, 4);
        final Criteria cmdCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)new String[] { "RestartDevice", "ShutDownDevice", "ClearAppData" }, 8);
        cmdHisCriteria = cmdHisCriteria.and(cmdCriteria);
        selectQuery.setCriteria(cmdHisCriteria);
        selectQuery.setGroupByClause(new GroupByClause((List)Arrays.asList(actionIdColumn)));
        final DataObject actionObj = MDMUtil.getPersistence().get(selectQuery);
        if (actionObj != null) {
            final Iterator rowIterator = actionObj.getRows("GroupActionHistory");
            while (rowIterator.hasNext()) {
                final Row grpActionRow = rowIterator.next();
                actionList.add(grpActionRow.get("GROUP_ACTION_ID"));
            }
        }
        final SelectQuery squery2 = (SelectQuery)new SelectQueryImpl(new Table("CustomGroupMemberRel"));
        squery2.addJoin(new Join("CustomGroupMemberRel", "GroupActionHistory", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_ID" }, 2));
        final SelectQuery subquery = (SelectQuery)new SelectQueryImpl(new Table("CommandHistory"));
        subquery.addJoin(new Join("CommandHistory", "GroupActionToCommand", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
        final Column updatedTime = new Column("CommandHistory", "UPDATED_TIME");
        updatedTime.setColumnAlias("UPDATED_TIME");
        subquery.addSelectColumn(updatedTime);
        final Column resIdOfSubQuery = new Column("CommandHistory", "RESOURCE_ID");
        resIdOfSubQuery.setColumnAlias("RES_ID");
        subquery.addSelectColumn(resIdOfSubQuery);
        final DerivedTable groupActionDerievedTable = new DerivedTable("subquery", (Query)subquery);
        squery2.addJoin(new Join(Table.getTable("CustomGroupMemberRel"), (Table)groupActionDerievedTable, new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RES_ID" }, 1));
        final Column sumCol = Column.getColumn("subquery", "UPDATED_TIME").summation();
        sumCol.setColumnAlias("UPD_TIME_SUM");
        final Column groupActionIdCol = Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID");
        squery2.addSelectColumn(groupActionIdCol);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(sumCol, (Object)null, 0));
        squery2.setGroupByClause(groupByClause);
        final Criteria notApplicableCri = new Criteria(Column.getColumn("GroupActionHistory", "ACTION_STATUS"), (Object)1000, 1);
        squery2.setCriteria(notApplicableCri);
        final DataObject actionUpdObj = MDMUtil.getPersistence().get(squery2);
        if (actionUpdObj != null) {
            final Iterator rowIterator2 = actionUpdObj.getRows("GroupActionHistory");
            while (rowIterator2.hasNext()) {
                final Row grpActionRow2 = rowIterator2.next();
                actionList.add(grpActionRow2.get("GROUP_ACTION_ID"));
            }
        }
        if (!actionList.isEmpty()) {
            actionList = new ArrayList(new HashSet(actionList));
        }
        MDMCustomGroupUtil.logger.log(Level.FINE, "getModifiedGroupActionIDList {0}", actionList);
        return actionList;
    }
    
    static {
        MDMCustomGroupUtil.mdmcgUtil = null;
        LOGGER = Logger.getLogger(MDMCustomGroupUtil.class.getName());
    }
}
