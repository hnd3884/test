package com.adventnet.sym.server.mdm.group;

import java.util.Hashtable;
import java.util.Iterator;
import org.json.simple.JSONArray;
import java.util.Set;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMUserGroupListViewDataHandler extends DefaultListViewNodeDataObject
{
    private static final String DEFAULT_STYLE = "padding-left: 5px; padding-right: 5px; background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black ;width: 280px;background-position: 4% 50%, 96% 47% !important;";
    private static final String SELECTED_STYLE = "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 25px 20px, 17px 17px !important; background-image: url(/images/user_management_28.gif), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;width: 280px;    background-position: 4% 50%, 96% 47% !important;";
    private static final String DISABLED_STYLE = "padding-left: 5px; padding-right: 5px; filter: opacity(80%); background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black; cursor:default;width: 280px;     background-position: 4% 50%, 96% 47% !important;";
    
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
            int startIndex = 0;
            int noOfObj = 50;
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            final JSONObject groupJSON = (JSONObject)new JSONParser().parse(groupJSONStr);
            final JSONObject selectedResorceJSON = (JSONObject)new JSONParser().parse(selectedResorceJSONStr);
            Criteria cri = null;
            SelectQuery groupQuery = null;
            try {
                groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
                groupQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                groupQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
                groupQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
                final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer[] { 2 }, 8);
                final Criteria additionalCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)requestMap.get("cid"), 0);
                final Join managedUserJoin = new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
                groupQuery.addJoin(managedUserJoin);
                final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
                groupQuery.setCriteria(resTypeCri.and(additionalCriteria).and(userNotInTrashCriteria));
            }
            catch (final Exception ex) {
                MDMUtil.logger.log(Level.WARNING, "Exception in getMDMDeviceResourceQuery method : {0}", ex);
            }
            if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                final Join groupJoin = new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2);
                final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                groupQuery.addJoin(groupJoin);
                cri = groupCriteria;
            }
            else if (!filterButtonVal.equalsIgnoreCase(associatedString)) {
                final Set selectedResIdSet = selectedResorceJSON.keySet();
                final Criteria alreadySelectedCri = cri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)selectedResIdSet.toArray(), 9, false);
            }
            if (searchValue != null) {
                final Criteria searchUserCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
                cri = cri.and(searchUserCri.and(groupQuery.getCriteria()));
            }
            final Criteria nonDirUseCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 0, false);
            final Criteria dirUseCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1, false);
            if (filterTreeParams != null && filterTreeParams.contains("901") && !filterTreeParams.contains("902")) {
                cri = cri.and(nonDirUseCri.and(groupQuery.getCriteria()));
            }
            else if (filterTreeParams != null && !filterTreeParams.contains("901") && filterTreeParams.contains("902")) {
                cri = cri.and(dirUseCri.and(groupQuery.getCriteria()));
            }
            else {
                cri = cri.and(groupQuery.getCriteria());
            }
            groupQuery.setCriteria(groupQuery.getCriteria().and(cri));
            if (selectAllValue == null && !filterButtonVal.equalsIgnoreCase(selectedString)) {
                final Range deviceRange = new Range(startIndex, noOfObj);
                groupQuery.setRange(deviceRange);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            groupQuery.addSortColumn(sortCol);
            final JSONArray jsonArray = MDMUtil.executeSelectQuery(groupQuery);
            final Iterator ds = jsonArray.iterator();
            try {
                TreeNode childNode = null;
                Properties userDataProperties = null;
                String resourceName = "";
                String domainName = "";
                while (ds.hasNext()) {
                    childNode = new TreeNode();
                    final JSONObject jsonObject = ds.next();
                    childNode.id = ((Long)jsonObject.get((Object)"RESOURCE_ID")).toString();
                    if (filterButtonVal.equalsIgnoreCase(selectedString) && !groupJSON.containsKey((Object)childNode.id)) {
                        continue;
                    }
                    resourceName = (String)jsonObject.get((Object)"NAME");
                    domainName = (String)jsonObject.get((Object)"DOMAIN_NETBIOS_NAME");
                    userDataProperties = new Properties();
                    ((Hashtable<String, String>)userDataProperties).put("userName", resourceName);
                    if (resourceName.length() > 35) {
                        resourceName = resourceName.substring(0, 35).concat("...");
                    }
                    childNode.text = resourceName.concat("<br/> <div class=''>").concat(domainName).concat("</div>");
                    ((Hashtable<String, String>)userDataProperties).put("defaultStyle", "padding-left: 5px; padding-right: 5px; background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black ;width: 280px;background-position: 4% 50%, 96% 47% !important;");
                    ((Hashtable<String, String>)userDataProperties).put("selectedStyle", "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 25px 20px, 17px 17px !important; background-image: url(/images/user_management_28.gif), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;width: 280px;    background-position: 4% 50%, 96% 47% !important;");
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                    if (groupJSON.containsKey((Object)childNode.id)) {
                        childNode.checked = true;
                        childNode.style = "padding-left: 5px; padding-right: 5px; color: rgb(95, 156, 212); background-size: 25px 20px, 17px 17px !important; background-image: url(/images/user_management_28.gif), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;width: 280px;    background-position: 4% 50%, 96% 47% !important;";
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                    }
                    else {
                        childNode.style = "padding-left: 5px; padding-right: 5px; background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black ;width: 280px;background-position: 4% 50%, 96% 47% !important;";
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                        if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                            ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", false);
                            childNode.style = "padding-left: 5px; padding-right: 5px; filter: opacity(80%); background-size: 25px 20px, 17px 17px !important; background-image: url(\"/images/user_management_28.gif\") !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black; cursor:default;width: 280px;     background-position: 4% 50%, 96% 47% !important;";
                        }
                    }
                    childNode.userData = userDataProperties;
                    treeNodeList.add(childNode);
                }
            }
            catch (final Exception ex2) {
                throw ex2;
            }
        }
        catch (final Exception ex3) {
            Logger.getLogger(MDMGroupListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex3);
        }
        return treeNodeList;
    }
}
