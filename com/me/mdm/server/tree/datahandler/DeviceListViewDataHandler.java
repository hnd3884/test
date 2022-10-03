package com.me.mdm.server.tree.datahandler;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.util.HashMap;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class DeviceListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) {
        final List treeNodeList = new ArrayList();
        try {
            final Long customerID = requestMap.get("cid");
            final String searchValue = requestMap.get("search");
            final String resourceJSONStr = requestMap.get("resourceJSON");
            final String noOfObjStr = requestMap.get("noOfObj");
            final String startIndexStr = requestMap.get("start");
            final String selectAllValue = requestMap.get("selectAllValue");
            int startIndex = 0;
            int noOfObj = 50;
            final JSONObject resourceJSON = new JSONObject(resourceJSONStr);
            SelectQuery deviceQuery = null;
            Criteria searchCri = null;
            if (searchValue != null) {
                searchCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchValue, 12, false);
            }
            deviceQuery = ManagedDeviceHandler.getInstance().getManagedDeviceQuery(customerID, searchCri);
            if (selectAllValue == null) {
                if (startIndexStr != null) {
                    noOfObj = Integer.parseInt(noOfObjStr);
                    startIndex = Integer.parseInt(startIndexStr);
                }
                final Range docRange = new Range(startIndex, noOfObj);
                deviceQuery.setRange(docRange);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("ManagedUser", "NAME"), true);
            deviceQuery.addSortColumn(sortCol);
            final ArrayList deviceDetailsList = ManagedDeviceHandler.getInstance().getManagedDeviceDetailslist(deviceQuery);
            final Iterator deviceItr = deviceDetailsList.iterator();
            String resourceName = "";
            String resourceImage = "";
            while (deviceItr.hasNext()) {
                final HashMap managedDeviceDetails = deviceItr.next();
                final TreeNode childNode = new TreeNode();
                final Properties userDataProperties = new Properties();
                childNode.id = managedDeviceDetails.get("RESOURCE_ID").toString();
                resourceName = managedDeviceDetails.get("NAME");
                childNode.child = false;
                childNode.nocheckbox = false;
                final int platformType = managedDeviceDetails.get("PLATFORM_TYPE");
                if (platformType == 1) {
                    resourceImage = "/images/applelogo.png";
                }
                if (platformType == 2) {
                    resourceImage = "/images/androidlogo.png";
                }
                if (platformType == 3) {
                    resourceImage = "/images/windowslogo.png";
                }
                childNode.text = resourceName;
                childNode.style = "color:#000; font:12px 'Lato', 'Roboto', sans-serif; text-decoration:none;background: url(" + resourceImage + ");background-position: center right;background-repeat: no-repeat; display: inline-block; width:90%;";
                ((Hashtable<String, Integer>)userDataProperties).put("platform", platformType);
                ((Hashtable<String, Boolean>)userDataProperties).put("isGroup", false);
                if (resourceJSON.has(childNode.id)) {
                    childNode.checked = true;
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                }
                else {
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                }
                childNode.userData = userDataProperties;
                treeNodeList.add(childNode);
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(DeviceListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return treeNodeList;
    }
}
