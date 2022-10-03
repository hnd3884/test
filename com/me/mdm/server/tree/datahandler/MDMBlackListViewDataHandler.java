package com.me.mdm.server.tree.datahandler;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import java.util.HashMap;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

@Deprecated
public class MDMBlackListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) throws Exception {
        final List treeNodeList = new ArrayList();
        try {
            final Long customerID = (Long)requestMap.get("cid");
            final String searchValue = (String)requestMap.get("search");
            final String filterValue = (String)requestMap.get("filter");
            final String resourceJSONStr = (String)requestMap.get("resourceJSON");
            final String noOfObjStr = (String)requestMap.get("noOfObj");
            final String startIndexStr = (String)requestMap.get("start");
            final String selectAllValue = (String)requestMap.get("selectAllValue");
            final JSONObject resourceJSON = new JSONObject(resourceJSONStr);
            requestMap.put("loginID", ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID());
            int startIndex = 0;
            int noOfObj = 50;
            final DocMgmtGroupsListViewDataHandler docHandler = new DocMgmtGroupsListViewDataHandler();
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            if (filterValue.equals("groups")) {
                final SelectQuery groupViewQuery = BlacklistQueryUtils.getInstance().getGroupTreeForAppGroups((HashMap)requestMap);
                final List customGroupsList = CustomGroupingHandler.getCustomGroupDetailsList(groupViewQuery);
                docHandler.addGrpsInTreeNode(treeNodeList, customGroupsList, resourceJSON, false);
            }
            if (filterValue.equals("devices")) {
                final SelectQuery deviceViewQuery = BlacklistQueryUtils.getInstance().getResourceTreeForAppGroups((HashMap)requestMap);
                this.addDevicesInTreeNode(searchValue, customerID, resourceJSON, treeNodeList, false, noOfObj, startIndex, selectAllValue, deviceViewQuery);
            }
            if (filterValue.equals("selected")) {
                final SelectQuery groupViewQuery = BlacklistQueryUtils.getInstance().getGroupTreeForAppGroups((HashMap)requestMap);
                final SelectQuery deviceViewQuery2 = BlacklistQueryUtils.getInstance().getResourceTreeForAppGroups((HashMap)requestMap);
                final List customGroupsList2 = CustomGroupingHandler.getCustomGroupDetailsList(groupViewQuery);
                docHandler.addGrpsInTreeNode(treeNodeList, customGroupsList2, resourceJSON, true);
                this.addDevicesInTreeNode(searchValue, customerID, resourceJSON, treeNodeList, true, noOfObj, startIndex, selectAllValue, deviceViewQuery2);
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
    
    public void addDevicesInTreeNode(final String searchValue, final Long customerID, final JSONObject resourceJSON, final List treeNodeList, final Boolean selected, final int noOfObj, final int startIndex, final String selectAllValue, final SelectQuery selectQuery) {
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
}
