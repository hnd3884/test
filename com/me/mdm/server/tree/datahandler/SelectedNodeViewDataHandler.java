package com.me.mdm.server.tree.datahandler;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Iterator;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class SelectedNodeViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) {
        final List treeNodeList = new ArrayList();
        try {
            final String resourceJSONStr = requestMap.get("resourceJSON");
            final JSONObject resourceJSON = new JSONObject(resourceJSONStr);
            final Iterator nodeItr = resourceJSON.keys();
            while (nodeItr.hasNext()) {
                final String resourceIdStr = nodeItr.next();
                final JSONObject resourcePropJSON = resourceJSON.optJSONObject(resourceIdStr);
                final JSONObject userDataJson = resourcePropJSON.optJSONObject("USERDATA");
                final TreeNode childNode = new TreeNode();
                final Properties userDataProperties = this.getUserDataProps(userDataJson);
                final String style = resourcePropJSON.optString("NODE_STYLE", "");
                childNode.id = resourcePropJSON.optString("NODE_ID", "");
                childNode.child = false;
                childNode.nocheckbox = true;
                childNode.text = resourcePropJSON.optString("NODE_NAME", "");
                ((Hashtable<String, String>)userDataProperties).put("style", childNode.style = style);
                childNode.userData = userDataProperties;
                treeNodeList.add(childNode);
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(SelectedNodeViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return treeNodeList;
    }
    
    private Properties getUserDataProps(final JSONObject userDataJSON) {
        final Properties userDataProps = new Properties();
        if (userDataJSON != null) {
            final Iterator userJsonKeyIterator = userDataJSON.keys();
            while (userJsonKeyIterator.hasNext()) {
                final String key = userJsonKeyIterator.next();
                final String value = String.valueOf(userDataJSON.opt(key));
                userDataProps.setProperty(key, value);
            }
        }
        return userDataProps;
    }
}
