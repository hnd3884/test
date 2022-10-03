package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.approval.EnrollmentApprovalHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class SelectedADGroupsTreeNodeDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) {
        final List treeNodeList = new ArrayList();
        try {
            JSONArray groupList = new JSONArray();
            final Boolean inDialog = requestMap.get("inDialog") != null;
            if (requestMap.get("resourceJSONArr") != null && new JSONArray(String.valueOf(requestMap.get("resourceJSONArr"))).length() > 0) {
                groupList = new JSONArray(String.valueOf(requestMap.get("resourceJSONArr")));
            }
            else if (!inDialog) {
                groupList = this.getADGroupList();
            }
            for (int groupIndex = 0; groupIndex < groupList.length(); ++groupIndex) {
                treeNodeList.add(this.generateTreeNode(groupList.getJSONObject(groupIndex)));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SelectedADGroupsTreeNodeDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return treeNodeList;
    }
    
    private JSONArray getADGroupList() throws SyMException, JSONException {
        final JSONObject criteriaDetails = EnrollmentApprovalHandler.getInstance().getCriteria(1);
        if (criteriaDetails != null) {
            return criteriaDetails.getJSONArray("RESOURCE_LIST");
        }
        return new JSONArray();
    }
    
    private TreeNode generateTreeNode(final JSONObject groupJSON) throws JSONException {
        final Properties userDataProperties = new Properties();
        ((Hashtable<String, Object>)userDataProperties).put("DOMAIN_NETBIOS_NAME", groupJSON.get("DOMAIN_NETBIOS_NAME"));
        if (!MDMStringUtils.isEmpty((String)groupJSON.opt("DN"))) {
            ((Hashtable<String, Object>)userDataProperties).put("DN", groupJSON.get("DN"));
        }
        ((Hashtable<String, Object>)userDataProperties).put("RESOURCE_NAME", groupJSON.get("RESOURCE_NAME"));
        ((Hashtable<String, Object>)userDataProperties).put("DIRECTORY_IDENTIFIER", groupJSON.get("DIRECTORY_IDENTIFIER"));
        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
        final TreeNode childNode = new TreeNode();
        childNode.id = String.valueOf(groupJSON.get("DIRECTORY_IDENTIFIER"));
        childNode.child = false;
        childNode.nocheckbox = false;
        childNode.text = String.valueOf(groupJSON.get("RESOURCE_NAME"));
        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
        childNode.userData = userDataProperties;
        return childNode;
    }
}
