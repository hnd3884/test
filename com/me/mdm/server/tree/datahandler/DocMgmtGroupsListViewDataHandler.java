package com.me.mdm.server.tree.datahandler;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.util.Hashtable;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.server.doc.DocMgmt;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class DocMgmtGroupsListViewDataHandler extends DefaultListViewNodeDataObject
{
    private static final String DEFAULT_STYLE = "padding-left: 5px; padding-right: 5px; background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black ;width: 280px;background-position: 4% 50%, 96% 47% !important;";
    private static final String SELECTED_STYLE = "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 25px 20px, 17px 17px !important; background-image: url(/images/user_management_28.gif), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;width: 280px;    background-position: 4% 50%, 96% 47% !important;";
    private static final String DISABLED_STYLE = "padding-left: 5px; padding-right: 5px; filter: opacity(80%); background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black; cursor:default;width: 280px;     background-position: 4% 50%, 96% 47% !important;";
    
    private List getCustomGroupsList(final Criteria groupCri, final int noOfObj, final int startIndex, final String selectAllValue) {
        List customGpList = null;
        try {
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (groupCri != null) {
                selectQuery.setCriteria(groupCri);
            }
            if (selectAllValue == null) {
                final Range groupRange = new Range(startIndex, noOfObj);
                selectQuery.setRange(groupRange);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            selectQuery.addSortColumn(sortCol);
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID", "CUSTOMGROUP_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            customGpList = CustomGroupingHandler.getCustomGroupDetailsList(selectQuery);
        }
        catch (final Exception e) {
            DocMgmt.logger.log(Level.WARNING, "Exception occoured in getCustomGroupsList....", e);
        }
        return customGpList;
    }
    
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) throws Exception {
        final List treeNodeList = new ArrayList();
        try {
            final Long customerID = requestMap.get("cid");
            final String searchValue = requestMap.get("search");
            final String filterValue = requestMap.get("filter");
            final String resourceJSONStr = requestMap.get("resourceJSON");
            final String noOfObjStr = requestMap.get("noOfObj");
            final String startIndexStr = requestMap.get("start");
            final String selectAllValue = requestMap.get("selectAllValue");
            final JSONObject resourceJSON = new JSONObject(resourceJSONStr);
            JSONArray platformArray = null;
            final String platformStr = requestMap.get("platform");
            if (platformStr != null) {
                platformArray = new JSONArray(platformStr);
            }
            int startIndex = 0;
            int noOfObj = 50;
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            if (filterValue.equals("groups")) {
                final Criteria cri = this.getGrpCri(customerID, searchValue);
                final List customGroupsList = this.getCustomGroupsList(cri, noOfObj, startIndex, selectAllValue);
                this.addGrpsInTreeNode(treeNodeList, customGroupsList, resourceJSON, false);
            }
            if (filterValue.equals("devices")) {
                this.addDevicesInTreeNode(searchValue, customerID, resourceJSON, treeNodeList, false, noOfObj, startIndex, selectAllValue, platformArray);
            }
            if (filterValue.equals("users")) {
                this.addUsersInTreeNode(searchValue, customerID, resourceJSON, treeNodeList, false, noOfObj, startIndex, selectAllValue);
            }
            if (filterValue.equals("selected")) {
                final Criteria cri = this.getGrpCri(customerID, searchValue);
                final List customGroupsList = this.getCustomGroupsList(cri, noOfObj, startIndex, selectAllValue);
                this.addGrpsInTreeNode(treeNodeList, customGroupsList, resourceJSON, true);
                this.addDevicesInTreeNode(searchValue, customerID, resourceJSON, treeNodeList, true, noOfObj, startIndex, selectAllValue, platformArray);
                this.addUsersInTreeNode(searchValue, customerID, resourceJSON, treeNodeList, true, noOfObj, startIndex, selectAllValue);
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(GroupListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final DataAccessException ex2) {
            Logger.getLogger(GroupListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
        }
        catch (final Exception ex3) {
            Logger.getLogger(GroupListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex3);
        }
        return treeNodeList;
    }
    
    public Criteria getGrpCri(final Long customerID, final String searchValue) {
        final List groupTypeList = MDMGroupHandler.getMDMGroupType();
        groupTypeList.add(7);
        final Criteria typeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        Criteria cri = typeCri.and(customerCri);
        if (searchValue != null) {
            final Criteria searchCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
            cri = cri.and(searchCri);
        }
        return cri;
    }
    
    public void addGrpsInTreeNode(final List treeNodeList, final List customGroupsList, final JSONObject resourceJSON, final Boolean selected) throws Exception {
        final Iterator groupItr = customGroupsList.iterator();
        String resourceName = "";
        final String resourceImage = "/images/folder.png";
        while (groupItr.hasNext()) {
            final Hashtable groupDetails = groupItr.next();
            final TreeNode childNode = new TreeNode();
            final Properties userDataProperties = new Properties();
            final Long groupID = groupDetails.get("CUSTOM_GP_ID");
            childNode.id = "groups_" + groupID.toString();
            resourceName = groupDetails.get("CUSTOM_GP_NAME");
            childNode.child = false;
            childNode.nocheckbox = false;
            if (resourceName.length() > 21) {
                String resourceNameFull = resourceName;
                resourceName = resourceName.substring(0, 18).concat("...");
                resourceNameFull = resourceNameFull.replaceAll("'", "\\\\'");
                resourceName = "<f onmouseout=\"return nd();\" onmouseover=\"overlib('" + resourceNameFull + "' , WIDTH , '10',WRAP, HAUTO,FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66',CSSCLASS,TEXTFONTCLASS,'bodytext',FGCLASS,'bodybg tablebg',BGCLASS,'bgClass')\">" + resourceName + "</f>";
            }
            final String defaultStyle = "background-image: url(" + resourceImage + "), url(/images/group-member.png) !important; background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
            final String selectedStyle = "background-image: url(" + resourceImage + "),url(/images/group-member.png), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important; color: rgb(95, 156, 212) !important;";
            ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
            ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
            final int platformType = 0;
            childNode.style = defaultStyle;
            childNode.text = resourceName.concat("<div class='infoText'> &nbsp; &nbsp; &nbsp;").concat(String.valueOf(CustomGroupingHandler.getGroupMemberCount(groupID)) + " " + I18N.getMsg("dc.common.MEMBERS_SINGLE_MULTIPLE", new Object[0])).concat("</div>");
            ((Hashtable<String, Integer>)userDataProperties).put("platform", platformType);
            ((Hashtable<String, Boolean>)userDataProperties).put("isGroup", true);
            ((Hashtable<String, Integer>)userDataProperties).put("deviceCount", CustomGroupingHandler.getGroupMemberCount(groupID));
            if (resourceJSON.has(childNode.id)) {
                childNode.checked = true;
                childNode.style = selectedStyle;
                ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                if (selected) {
                    treeNodeList.add(childNode);
                }
            }
            else {
                ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
            }
            childNode.userData = userDataProperties;
            if (!selected) {
                treeNodeList.add(childNode);
            }
        }
    }
    
    public void addDevicesInTreeNode(final String searchValue, final Long customerID, final JSONObject resourceJSON, final List treeNodeList, final Boolean selected, final int noOfObj, final int startIndex, final String selectAllValue, final JSONArray platformArray) {
        Criteria baseCri = null;
        if (platformArray != null) {
            baseCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)JSONUtil.getInstance().convertJSONArrayTOList(platformArray).toArray(), 8);
        }
        if (searchValue != null) {
            final Criteria searchCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchValue, 12, false);
            if (baseCri != null) {
                baseCri = baseCri.and(searchCri);
            }
            else {
                baseCri = searchCri;
            }
        }
        SelectQuery selectQuery = ManagedDeviceHandler.getInstance().getManagedDeviceQuery(customerID, baseCri);
        if (selectAllValue == null) {
            final Range groupRange = new Range(startIndex, noOfObj);
            selectQuery.setRange(groupRange);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("ManagedDeviceExtn", "NAME"), true);
        selectQuery.addSortColumn(sortCol);
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final ArrayList deviceDetailsList = ManagedDeviceHandler.getInstance().getManagedDeviceDetailslist(selectQuery);
        final Iterator deviceItr = deviceDetailsList.iterator();
        String resourceName2 = "";
        String resourceImage2 = "";
        String userName = "";
        while (deviceItr.hasNext()) {
            final HashMap managedDeviceDetails = deviceItr.next();
            final TreeNode childNode = new TreeNode();
            Properties userDataProperties = new Properties();
            childNode.id = "devices_" + managedDeviceDetails.get("RESOURCE_ID").toString();
            resourceName2 = managedDeviceDetails.get("NAME");
            userName = managedDeviceDetails.get("USER_RESOURCE_NAME");
            userDataProperties = new Properties();
            ((Hashtable<String, String>)userDataProperties).put("userName", userName);
            childNode.child = false;
            childNode.nocheckbox = false;
            if (resourceName2.length() > 21) {
                String resourceNameFull = resourceName2;
                resourceName2 = resourceName2.substring(0, 18).concat("...");
                resourceNameFull = resourceNameFull.replaceAll("'", "\\\\'");
                resourceName2 = "<f onmouseout=\"return nd();\" onmouseover=\"overlib('" + resourceNameFull + "' , WIDTH , '10',WRAP, HAUTO,FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66',CSSCLASS,TEXTFONTCLASS,'bodytext',FGCLASS,'bodybg tablebg',BGCLASS,'bgClass')\">" + resourceName2 + "</f>";
            }
            final int platformType = managedDeviceDetails.get("PLATFORM_TYPE");
            if (platformType == 1) {
                resourceImage2 = "/images/applelogo.png";
            }
            if (platformType == 2) {
                resourceImage2 = "/images/androidlogo.png";
            }
            if (platformType == 3) {
                resourceImage2 = "/images/windowslogo.png";
            }
            final String defaultStyle = "background-image: url(" + resourceImage2 + "); background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
            final String selectedStyle = "background-image: url(" + resourceImage2 + "),url(), url(/images/select.png); background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important; color: rgb(95, 156, 212) !important;";
            ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
            ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
            if (userName.length() > 18) {
                userName = userName.substring(0, 18).concat("...");
            }
            childNode.text = resourceName2.concat("<br/> <div class='infoText'>").concat(userName).concat("</div>");
            childNode.style = defaultStyle;
            ((Hashtable<String, Integer>)userDataProperties).put("platform", platformType);
            ((Hashtable<String, Boolean>)userDataProperties).put("isGroup", true);
            if (resourceJSON.has(childNode.id)) {
                childNode.checked = true;
                childNode.style = selectedStyle;
                ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                if (selected) {
                    treeNodeList.add(childNode);
                }
            }
            else {
                ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
            }
            childNode.userData = userDataProperties;
            if (!selected) {
                treeNodeList.add(childNode);
            }
        }
    }
    
    private List addUsersInTreeNode(final String searchValue, final Long customerID, final JSONObject resourceJSON, final List treeNodeList, final Boolean selected, final int noOfObj, final int startIndex, final String selectAllValue) {
        final List userList = null;
        try {
            SelectQuery query = null;
            try {
                query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
                query.addSelectColumn(Column.getColumn("Resource", "NAME"));
                query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                query.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
                final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0);
                query.setCriteria(resTypeCri.and(custCri));
            }
            catch (final Exception ex) {
                MDMUtil.logger.log(Level.WARNING, "Exception in getMDMDeviceResourceQuery method : {0}", ex);
            }
            if (searchValue != null) {
                final Criteria searchUserCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
                query.setCriteria(query.getCriteria().and(searchUserCri));
            }
            if (selectAllValue == null) {
                final Range userRange = new Range(startIndex, noOfObj);
                query.setRange(userRange);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            query.addSortColumn(sortCol);
            final org.json.simple.JSONArray jsonArray = MDMUtil.executeSelectQuery(query);
            final Iterator ds = jsonArray.iterator();
            try {
                TreeNode childNode = null;
                String domainName = "";
                String resourceName = "";
                Properties userDataProperties = null;
                while (ds.hasNext()) {
                    final org.json.simple.JSONObject jsonObject = ds.next();
                    childNode = new TreeNode();
                    childNode.child = false;
                    childNode.nocheckbox = false;
                    resourceName = (String)jsonObject.get((Object)"NAME");
                    childNode.id = "users_" + ((Long)jsonObject.get((Object)"RESOURCE_ID")).toString();
                    domainName = (String)jsonObject.get((Object)"DOMAIN_NETBIOS_NAME");
                    userDataProperties = new Properties();
                    ((Hashtable<String, String>)userDataProperties).put("userName", resourceName);
                    if (resourceName.length() > 35) {
                        resourceName = resourceName.substring(0, 35).concat("...");
                    }
                    childNode.text = resourceName.concat("<br/> <div class=''>").concat(domainName).concat("</div>");
                    ((Hashtable<String, String>)userDataProperties).put("defaultStyle", childNode.style = "padding-left: 5px; padding-right: 5px; background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black ;width: 280px;background-position: 4% 50%, 96% 47% !important;");
                    ((Hashtable<String, String>)userDataProperties).put("selectedStyle", "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 25px 20px, 17px 17px !important; background-image: url(/images/user_management_28.gif), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;width: 280px;    background-position: 4% 50%, 96% 47% !important;");
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                    if (resourceJSON.has(childNode.id)) {
                        childNode.checked = true;
                        childNode.style = "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 25px 20px, 17px 17px !important; background-image: url(/images/user_management_28.gif), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;width: 280px;    background-position: 4% 50%, 96% 47% !important;";
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                        if (selected) {
                            treeNodeList.add(childNode);
                        }
                    }
                    else {
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                    }
                    childNode.userData = userDataProperties;
                    if (!selected) {
                        treeNodeList.add(childNode);
                    }
                }
            }
            catch (final Exception ex2) {
                throw ex2;
            }
        }
        catch (final Exception e) {
            DocMgmt.logger.log(Level.WARNING, "Exception occoured in getCustomGroupsList....", e);
        }
        return userList;
    }
}
