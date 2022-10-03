package com.adventnet.sym.server.mdm.group;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import java.util.Set;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMGroupListViewDataHandler extends DefaultListViewNodeDataObject
{
    static String[][] imgUrl;
    
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        final String selectedString = "selected";
        final String associatedString = "associated";
        final List treeNodeList = new ArrayList();
        try {
            final String searchValue = requestMap.get("search");
            final String filterButtonVal = requestMap.get("filterButtonVal");
            final String filterTreeParams = requestMap.get("filterTreeParams");
            final String groupJSONStr = requestMap.get("groupJSON");
            final String selectedResorceJSONStr = requestMap.get("selectedGroupJSON");
            final String startIndexStr = requestMap.get("start");
            final String noOfObjStr = requestMap.get("noOfObj");
            final String groupCategoryStr = requestMap.get("groupCategory");
            final String groupIdStr = requestMap.get("groupId");
            final String selectAllValue = requestMap.get("selectAllValue");
            Long[] groupIds = null;
            Long groupId = null;
            if (groupIdStr != null && !groupIdStr.equals("")) {
                if (groupIdStr.contains("[")) {
                    groupIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(groupIdStr);
                    groupId = groupIds[0];
                }
                else {
                    groupId = Long.valueOf(groupIdStr);
                }
            }
            final int groupCategory = Integer.parseInt(groupCategoryStr);
            int startIndex = 0;
            int noOfObj = 50;
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            final JSONObject groupJSON = (JSONObject)new JSONParser().parse(groupJSONStr);
            final JSONObject selectedResorceJSON = (JSONObject)new JSONParser().parse(selectedResorceJSONStr);
            Criteria cri = null;
            final SelectQuery groupQuery = MDMUtil.getInstance().getMDMDeviceResourceQuery();
            final Criteria managedDeviceCriteria = cri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                final Join groupJoin = new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2);
                final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                groupQuery.addJoin(groupJoin);
                cri = cri.and(groupCriteria);
            }
            else if (!filterButtonVal.equalsIgnoreCase(associatedString)) {
                final Set selectedResIdSet = selectedResorceJSON.keySet();
                final Criteria alreadySelectedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)selectedResIdSet.toArray(), 9, false);
                cri = cri.and(alreadySelectedCri);
            }
            if (searchValue != null) {
                final Criteria searchUserCri = new Criteria(Column.getColumn("ManagedUser", "NAME"), (Object)searchValue, 12, false);
                final Criteria searchDeviceCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchValue, 12, false);
                final Criteria searchCri = searchUserCri.or(searchDeviceCri);
                cri = cri.and(searchCri.and(groupQuery.getCriteria()));
            }
            if (filterTreeParams != null) {
                final JSONArray filterTreeJSON = (JSONArray)new JSONParser().parse(filterTreeParams);
                if (filterTreeJSON.size() > 0) {
                    final Criteria filterCri = MDMGroupFilterHandler.getInstance().getFilterCriteria(groupId, filterTreeJSON);
                    cri = cri.and(filterCri.and(groupQuery.getCriteria()));
                }
            }
            groupQuery.setCriteria(cri);
            if (groupCategory == 5 && !filterButtonVal.equalsIgnoreCase(associatedString)) {
                final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
                final Join customGroup = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                subQuery.addJoin(customGroup);
                final Criteria groupCategoryCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)5, 0);
                subQuery.setCriteria(groupCategoryCriteria);
                subQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "*"));
                final DataObject dao = MDMUtil.getPersistence().get(subQuery);
                final List excludeMemberResourceIds = DBUtil.getColumnValuesAsList(dao.getRows("CustomGroupMemberRel"), "MEMBER_RESOURCE_ID");
                final Criteria excludeMemberResIdCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)excludeMemberResourceIds.toArray(), 9);
                groupQuery.setCriteria(groupQuery.getCriteria().and(excludeMemberResIdCri));
            }
            final long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (groupCategory == 1 && !RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, true)) {
                final Join userDeviceMappingJoin = new Join("ManagedDevice", "UserDeviceMapping", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                groupQuery.addJoin(userDeviceMappingJoin);
                final Criteria userIDCriteria = new Criteria(Column.getColumn("UserDeviceMapping", "LOGIN_ID"), (Object)loginId, 0);
                groupQuery.setCriteria(groupQuery.getCriteria().and(userIDCriteria));
            }
            if (selectAllValue == null) {
                final Range deviceRange = new Range(startIndex, noOfObj);
                groupQuery.setRange(deviceRange);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("ManagedUser", "NAME"), true);
            groupQuery.addSortColumn(sortCol);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)groupQuery);
                TreeNode childNode = null;
                Properties userDataProperties = null;
                String deviceName = "";
                String userName = "";
                int deviceModelType = 1;
                while (ds.next()) {
                    childNode = new TreeNode();
                    childNode.id = ((Long)ds.getValue("RESOURCE_ID")).toString();
                    if (filterButtonVal.equalsIgnoreCase(selectedString) && !groupJSON.containsKey((Object)childNode.id)) {
                        continue;
                    }
                    deviceName = (String)ds.getValue("ManagedDeviceExtn.NAME");
                    deviceModelType = (int)ds.getValue("MODEL_TYPE");
                    userName = (String)ds.getValue("ManagedUser");
                    userDataProperties = new Properties();
                    ((Hashtable<String, String>)userDataProperties).put("userName", userName);
                    if (deviceName.length() > 18) {
                        deviceName = deviceName.substring(0, 18).concat("...");
                    }
                    if (userName.length() > 18) {
                        userName = userName.substring(0, 18).concat("...");
                    }
                    final int platform = (int)ds.getValue("PLATFORM_TYPE");
                    String resourceImage = "";
                    if (platform == 1) {
                        resourceImage = "/images/applelogo.png";
                    }
                    if (platform == 2) {
                        resourceImage = "/images/androidlogo.png";
                    }
                    if (platform == 3) {
                        resourceImage = "/images/windowslogo.png";
                    }
                    if (platform == 6) {
                        resourceImage = "/images/applelogo.png";
                    }
                    if (platform == 7) {
                        resourceImage = "/images/applelogo.png";
                    }
                    childNode.text = deviceName.concat("<br/> <div class='infoText' style='background-image: url(" + resourceImage + ") !important;background-repeat:no-repeat;padding-left:20px'>").concat(userName).concat("</div>");
                    final String[] imageUrls = this.getImageUrlForModelType(deviceModelType);
                    final String defaultStyle = "padding-left: 5px; padding-right: 5px; background-size: 20px 20px, 17px 17px !important; background-image: url(\"" + imageUrls[0] + "\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black ;";
                    final String selectedStyle = "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 20px 20px, 17px 17px !important; background-image: url(" + imageUrls[1] + "), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;";
                    final String disabledStyle = "padding-left: 5px; padding-right: 5px; filter: opacity(80%); background-size: 20px 20px, 17px 17px !important; background-image: url(\"" + imageUrls[0] + "\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black; cursor:default";
                    ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
                    ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                    if (groupJSON.containsKey((Object)childNode.id)) {
                        childNode.checked = true;
                        childNode.style = selectedStyle;
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                    }
                    else {
                        childNode.style = defaultStyle;
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                        if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                            ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", false);
                            childNode.style = disabledStyle;
                        }
                    }
                    childNode.userData = userDataProperties;
                    treeNodeList.add(childNode);
                }
            }
            catch (final Exception ex) {
                throw ex;
            }
        }
        catch (final Exception ex2) {
            Logger.getLogger(MDMGroupListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex2);
        }
        return treeNodeList;
    }
    
    private String[] getImageUrlForModelType(int modelType) {
        if (modelType == 0) {
            modelType = 1;
        }
        if (modelType == 5) {
            modelType = 4;
        }
        return MDMGroupListViewDataHandler.imgUrl[modelType - 1];
    }
    
    static {
        MDMGroupListViewDataHandler.imgUrl = new String[][] { { "/images/device_2.png", "/images/device_select.png" }, { "/images/tablet.png", "/images/tablet_select.png" }, { "/images/laptop.png", "/images/laptop_select.png" }, { "/images/desktop.png", "/images/desktop_select.png" } };
    }
}
