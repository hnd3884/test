package com.me.mdm.server.tree.datahandler;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.i18n.I18N;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.util.Hashtable;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class GroupMoveListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        final String selectedString = "selected";
        final String associatedString = "associated";
        final String availableString = "all";
        final List treeNodeList = new ArrayList();
        try {
            final String searchString = requestMap.get("search");
            final String filterBtnString = requestMap.get("filterButtonVal");
            final String groupJSONStr = requestMap.get("resourceJSON");
            final String selectAllValue = requestMap.get("selectAllValue");
            final String rangeStartStr = requestMap.get("start");
            final String rangeGrpCount = requestMap.get("noOfObj");
            final String deviceIDsStr = requestMap.get("deviceId");
            final Long customerID = requestMap.get("cid");
            int rangeStartIndex = 0;
            int rangeNoOfGrps = 50;
            if (rangeStartStr != null) {
                rangeStartIndex = Integer.parseInt(rangeStartStr);
                rangeNoOfGrps = Integer.parseInt(rangeGrpCount);
            }
            final JSONObject groupJSON = (JSONObject)new JSONParser().parse(groupJSONStr);
            final Criteria grpCriteria = this.getCommonGroupCriteria(searchString, customerID);
            final Long[] deviceIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(deviceIDsStr);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join groupResourceJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(groupResourceJoin);
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            selectQuery.addSortColumn(sortCol);
            Criteria availableAddGrpCri = grpCriteria;
            final SelectQuery grpSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            grpSubQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            final Column countCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").count();
            final Criteria devicesInGrpCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceIds, 8);
            grpSubQuery.setCriteria(devicesInGrpCri);
            final GroupByColumn groupByCustomGrpCol = new GroupByColumn(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), true);
            final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
            groupByList.add(groupByCustomGrpCol);
            final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)deviceIds.length, 4));
            grpSubQuery.setGroupByClause(groupByClause);
            final DerivedTable groupDerievedTable = new DerivedTable("CustomGroupMemberRel", (Query)grpSubQuery);
            final Criteria joinCri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), 0);
            selectQuery.addJoin(new Join(Table.getTable("Resource"), (Table)groupDerievedTable, joinCri, 1));
            if (filterBtnString.equalsIgnoreCase(availableString)) {
                final Criteria excludeAddedGrpsCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0);
                availableAddGrpCri = availableAddGrpCri.and(excludeAddedGrpsCri);
            }
            else if (filterBtnString.equalsIgnoreCase(associatedString)) {
                final Criteria addedGrpsCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 1);
                availableAddGrpCri = availableAddGrpCri.and(addedGrpsCri);
            }
            selectQuery.setCriteria(availableAddGrpCri);
            if (selectAllValue == null) {
                final Range grpListRange = new Range(rangeStartIndex, rangeNoOfGrps);
                selectQuery.setRange(grpListRange);
            }
            final List customGroupsList = MDMGroupHandler.getCustomGroupDetailsList(selectQuery);
            final Iterator groupItr = customGroupsList.iterator();
            String resourceName = "";
            while (groupItr.hasNext()) {
                final Hashtable groupDetails = groupItr.next();
                final TreeNode childNode = new TreeNode();
                final Properties userDataProperties = new Properties();
                childNode.id = groupDetails.get("CUSTOM_GP_ID").toString();
                if (filterBtnString != null && filterBtnString.equalsIgnoreCase(selectedString) && !groupJSON.containsKey((Object)childNode.id)) {
                    continue;
                }
                resourceName = groupDetails.get("CUSTOM_GP_NAME");
                ((Hashtable<String, String>)userDataProperties).put("title", resourceName);
                if (resourceName.length() > 21) {
                    String resourceNameFull = resourceName;
                    resourceName = resourceName.substring(0, 18).concat("...");
                    resourceNameFull = resourceNameFull.replaceAll("'", "\\\\'");
                    resourceName = "<f onmouseout=\"return nd();\" onmouseover=\"overlib('" + resourceNameFull + "' , WIDTH , '10',WRAP, HAUTO,FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66',CSSCLASS,TEXTFONTCLASS,'bodytext',FGCLASS,'bodybg tablebg',BGCLASS,'bgClass')\">" + resourceName + "</f>";
                }
                final int memberCount = groupDetails.get("CUSTOM_GP_MEMBER_COUNT");
                childNode.child = false;
                childNode.text = resourceName.concat("<div class='infoText'> &nbsp; &nbsp; &nbsp;").concat(String.valueOf(memberCount) + " " + I18N.getMsg("dc.common.MEMBERS_SINGLE_MULTIPLE", new Object[0])).concat("</div>");
                ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                final String defaultStyle = "background-image: url(/images/folder.png), url(/images/group-member.png) !important; background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
                final String selectedStyle = "background-image: url(/images/folder-select.png),url(/images/group-member.png), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important; color: rgb(95, 156, 212) !important;";
                final String disabledStyle = "background-image: url(/images/folder.png),url(/images/group-member.png) !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:default";
                ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
                ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
                if (groupJSON.containsKey((Object)childNode.id)) {
                    childNode.checked = true;
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                    childNode.style = selectedStyle;
                }
                else {
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                    childNode.style = defaultStyle;
                }
                if (filterBtnString.equalsIgnoreCase(associatedString)) {
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", false);
                    childNode.style = disabledStyle;
                }
                else {
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                }
                childNode.userData = userDataProperties;
                treeNodeList.add(childNode);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(GroupMoveListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return treeNodeList;
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
            Logger.getLogger(GroupMoveListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return commonCri;
    }
}
