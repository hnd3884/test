package com.me.mdm.onpremise.server.enrollment.ad;

import java.util.Hashtable;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.Iterator;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class ADGroupsTreeNodeDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) {
        final List treeNodeList = new ArrayList();
        try {
            final String searchValue = requestMap.get("search");
            final String resourceJSONArrStr = requestMap.get("resourceJSONArr");
            final JSONArray resourceJSONArr = new JSONArray(resourceJSONArrStr);
            HashMap<String, List<Properties>> map = new HashMap<String, List<Properties>>();
            map = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDMADGroups", 2);
            if (map == null || map.isEmpty()) {
                map = new HashMap<String, List<Properties>>();
                final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                final List domainList = MDMEnrollmentUtil.getInstance().getDomainNames(customerID);
                for (final String domainName : domainList) {
                    final List<Properties> groupList = this.getADGroupList(domainName, "All", customerID);
                    if (groupList != null) {
                        map.put(domainName, groupList);
                    }
                }
                if (map != null && !map.isEmpty()) {
                    ApiFactoryProvider.getCacheAccessAPI().putCache("MDMADGroups", (Object)map, 2);
                }
            }
            for (final String domainName2 : map.keySet()) {
                final List<Properties> groupList2 = map.get(domainName2);
                for (final Properties groupDetails : groupList2) {
                    if (!MDMUtil.getInstance().isEmpty(searchValue) && !((Hashtable<K, String>)groupDetails).get("name").toLowerCase().contains(searchValue.toLowerCase())) {
                        continue;
                    }
                    final TreeNode childNode = new TreeNode();
                    final Properties userDataProperties = new Properties();
                    childNode.id = ((Hashtable<K, String>)groupDetails).get("objectGUID");
                    childNode.child = false;
                    childNode.nocheckbox = false;
                    childNode.text = ((Hashtable<K, String>)groupDetails).get("name");
                    childNode.style = "color:#000; font:12px 'Lato', 'Roboto', sans-serif; text-decoration:none;display: inline-block; width:90%;";
                    ((Hashtable<String, String>)userDataProperties).put("DOMAIN_NETBIOS_NAME", domainName2);
                    if (groupDetails.containsKey("distinguishedName")) {
                        ((Hashtable<String, Object>)userDataProperties).put("DN", ((Hashtable<K, Object>)groupDetails).get("distinguishedName"));
                    }
                    ((Hashtable<String, Object>)userDataProperties).put("RESOURCE_NAME", ((Hashtable<K, Object>)groupDetails).get("name"));
                    ((Hashtable<String, Object>)userDataProperties).put("DIRECTORY_IDENTIFIER", ((Hashtable<K, Object>)groupDetails).get("objectGUID"));
                    if (this.isGroupPresentInGroupList(childNode.id, resourceJSONArr)) {
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
        }
        catch (final JSONException ex) {
            Logger.getLogger(ADGroupsTreeNodeDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(ADGroupsTreeNodeDataHandler.class.getName()).log(Level.SEVERE, null, ex2);
        }
        return treeNodeList;
    }
    
    private boolean isGroupPresentInGroupList(final String guid, final JSONArray groupList) {
        for (int groupIndex = 0; groupIndex < groupList.length(); ++groupIndex) {
            try {
                if (guid.equalsIgnoreCase(groupList.getJSONObject(groupIndex).optString("DIRECTORY_IDENTIFIER", "--"))) {
                    return true;
                }
            }
            catch (final JSONException ex) {
                Logger.getLogger(ADGroupsTreeNodeDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return false;
    }
    
    private List<Properties> getADGroupList(final String domainName, final String searchValue, final Long customerID) {
        try {
            final List<String> listAttributes = new ArrayList<String>();
            listAttributes.add("name");
            listAttributes.add("distinguishedName");
            listAttributes.add("objectGUID");
            return IdpsFactoryProvider.getIdpsAccessAPI(domainName, customerID).getAvailableADObjectList(domainName, 7, (List)listAttributes, searchValue, customerID);
        }
        catch (final Exception ex) {
            Logger.getLogger(ADGroupsTreeNodeDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Properties>();
        }
    }
}
