package com.me.mdm.server.tree.apidatahandler;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import org.json.JSONArray;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;

public class ApiGroupListViewDataHandler extends ApiListViewDataHandler
{
    @Override
    protected SelectQuery getSelectQuery() {
        this.selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join groupResourceJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join groupExtnResourceJoin = new Join("Resource", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        this.selectQuery.addJoin(groupResourceJoin);
        this.selectQuery.addJoin(groupExtnResourceJoin);
        this.selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        this.selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
        this.selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
        this.selectQuery.addSortColumn(sortCol);
        return this.selectQuery;
    }
    
    @Override
    protected SelectQuery setCriteria() {
        final Long customerId = this.requestJson.optLong("customerId");
        final Long loginId = this.requestJson.optLong("loginId");
        final Long userId = this.requestJson.optLong("userId");
        final String searchValue = this.requestJson.optString("searchValue");
        final JSONArray deviceIdsArray = this.requestJson.optJSONArray("deviceIds");
        final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdsArray);
        final String filterButtonVal = this.requestJson.optString("filterButtonVal");
        final Integer startIndex = this.requestJson.optInt("startIndex");
        final Integer noOfObj = this.requestJson.optInt("noOfObj");
        String selectAllValue = null;
        if (this.requestJson.optBoolean("selectAll")) {
            selectAllValue = "all";
        }
        Criteria availableAddGrpCri = this.getCommonGroupCriteria(searchValue, customerId);
        final SelectQuery grpSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        grpSubQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        final Column countCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").count();
        final Criteria devicesInGrpCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceIdList.toArray(), 8);
        grpSubQuery.setCriteria(devicesInGrpCri);
        final GroupByColumn groupByCustomGrpCol = new GroupByColumn(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByCustomGrpCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)deviceIdList.size(), 4));
        grpSubQuery.setGroupByClause(groupByClause);
        final DerivedTable groupDerievedTable = new DerivedTable("CustomGroupMemberRel", (Query)grpSubQuery);
        final Criteria joinCri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), 0);
        this.selectQuery.addJoin(new Join(Table.getTable("Resource"), (Table)groupDerievedTable, joinCri, 1));
        if (filterButtonVal.equalsIgnoreCase("all")) {
            final Criteria excludeAddedGrpsCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0);
            availableAddGrpCri = availableAddGrpCri.and(excludeAddedGrpsCri);
        }
        else if (filterButtonVal.equalsIgnoreCase("associated")) {
            final Criteria addedGrpsCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 1);
            availableAddGrpCri = availableAddGrpCri.and(addedGrpsCri);
        }
        if (loginId != null && userId != null) {
            final List<String> currentLogInRoles = DMUserHandler.getRoleNameListForLoginUser(loginId);
            final boolean groupWrite = currentLogInRoles.contains("MDM_GroupMgmt_Write") || currentLogInRoles.contains("ModernMgmt_MDMGroupMgmt_Write");
            final boolean groupAdmin = currentLogInRoles.contains("MDM_GroupMgmt_Admin") || currentLogInRoles.contains("ModernMgmt_MDMGroupMgmt_Admin");
            if (groupWrite && !groupAdmin && filterButtonVal.equalsIgnoreCase("all")) {
                availableAddGrpCri = availableAddGrpCri.and(new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)userId, 0));
            }
        }
        this.selectQuery.setCriteria(availableAddGrpCri);
        return this.selectQuery = RBDAUtil.getInstance().getRBDAQuery(this.selectQuery);
    }
    
    @Override
    protected JSONObject fetchResultObject() {
        try {
            final String filterButtonVal = this.requestJson.optString("filterButtonVal");
            final JSONObject resultJson = new JSONObject();
            final JSONArray successfullyAppliedArray = new JSONArray();
            final JSONArray yetToApplyArray = new JSONArray();
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)this.selectQuery);
            while (dataSetWrapper.next()) {
                final JSONObject jsonObject = new JSONObject();
                final Long groupId = (Long)dataSetWrapper.getValue("RESOURCE_ID");
                jsonObject.put("group_id", dataSetWrapper.getValue("RESOURCE_ID"));
                jsonObject.put("group_name", dataSetWrapper.getValue("NAME"));
                jsonObject.put("group_type", dataSetWrapper.getValue("GROUP_TYPE"));
                jsonObject.put("member_count", MDMGroupHandler.getGroupMemberCount(groupId));
                jsonObject.put("group_category", dataSetWrapper.getValue("GROUP_CATEGORY"));
                if (dataSetWrapper.getValue("GROUP_RESOURCE_ID") != null) {
                    successfullyAppliedArray.put((Object)jsonObject);
                }
                else {
                    yetToApplyArray.put((Object)jsonObject);
                }
            }
            if (filterButtonVal.equalsIgnoreCase("all") || filterButtonVal == "") {
                resultJson.put("yet_to_apply", (Object)yetToApplyArray);
            }
            if (filterButtonVal.equalsIgnoreCase("associated") || filterButtonVal == "") {
                resultJson.put("successfull_applied", (Object)successfullyAppliedArray);
            }
            return resultJson;
        }
        catch (final Exception ex) {
            ApiGroupListViewDataHandler.logger.log(Level.SEVERE, "exception in getting profile for distribution", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Criteria getCommonGroupCriteria(final String searchValue, final Long customerID) {
        final List groupTypeList = MDMGroupHandler.getMDMGroupType();
        Criteria commonCri = null;
        try {
            final Criteria typeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
            final Criteria category = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)1, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            commonCri = typeCri.and(customerCri).and(category);
            if (searchValue != null && !"".equals(searchValue)) {
                final Criteria searchCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
                commonCri = commonCri.and(searchCri);
            }
        }
        catch (final Exception ex) {
            ApiGroupListViewDataHandler.logger.log(Level.SEVERE, null, ex);
        }
        return commonCri;
    }
}
