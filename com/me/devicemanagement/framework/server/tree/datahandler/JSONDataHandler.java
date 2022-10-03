package com.me.devicemanagement.framework.server.tree.datahandler;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

public class JSONDataHandler
{
    public JSONObject createJSONObject(final HashMap rootTreeNodeMap) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 0);
        final JSONArray rootJSONArray = new JSONArray();
        if (rootTreeNodeMap != null && rootTreeNodeMap.size() > 0) {
            final Set keySet = rootTreeNodeMap.keySet();
            for (final TreeNode rootTreeNode : keySet) {
                final ArrayList childList = rootTreeNodeMap.get(rootTreeNode);
                final JSONObject rootNodeObject = this.createJSONObject(rootTreeNode);
                if (childList.size() > 0) {
                    final JSONArray childJSONArray = new JSONArray();
                    for (int i = 0; i < childList.size(); ++i) {
                        Object childNodeObject = childList.get(i);
                        JSONObject childJSONObject = null;
                        if (childNodeObject instanceof TreeNode) {
                            final TreeNode childNode = childList.get(i);
                            childJSONObject = this.createJSONObject(childNode);
                        }
                        else if (childNodeObject instanceof Map) {
                            final Map childNodeMap = (Map)childNodeObject;
                            childNodeObject = new JSONObject(childNodeMap);
                        }
                        childJSONArray.put((Object)childJSONObject);
                    }
                    rootNodeObject.put("item", (Object)childJSONArray);
                    rootJSONArray.put((Object)rootNodeObject);
                }
            }
            jsonObject.put("item", (Object)rootJSONArray);
        }
        return jsonObject;
    }
    
    public JSONArray createJSONArray(final List treeNodeChildList) throws Exception {
        final ArrayList childList = (ArrayList)treeNodeChildList;
        final JSONArray childJSONArray = new JSONArray();
        if (childList.size() > 0) {
            for (int i = 0; i < childList.size(); ++i) {
                Object childNodeObject = childList.get(i);
                JSONObject childJSONObject = null;
                if (childNodeObject instanceof TreeNode) {
                    final TreeNode childNode = childList.get(i);
                    childJSONObject = this.createJSONObject(childNode);
                }
                else if (childNodeObject instanceof Map) {
                    final Map childNodeMap = (Map)childNodeObject;
                    childNodeObject = new JSONObject(childNodeMap);
                }
                childJSONArray.put((Object)childJSONObject);
            }
        }
        return childJSONArray;
    }
    
    public JSONObject createJSONObject(final String parent_id, final List treeNodeChildList) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", (Object)parent_id);
        final JSONArray childJSONArray = this.createJSONArray(treeNodeChildList);
        jsonObject.put("item", (Object)childJSONArray);
        return jsonObject;
    }
    
    public JSONObject createJSONObject(final Properties properties) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        if (properties != null) {
            final Enumeration enumerator = properties.keys();
            while (enumerator.hasMoreElements()) {
                final String key = enumerator.nextElement();
                final Object value = ((Hashtable<K, Object>)properties).get(key);
                jsonObject.put(key, value);
            }
        }
        return jsonObject;
    }
    
    private JSONArray createUserDataJSONArray(final Properties properties) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        if (properties != null) {
            final Enumeration enumerator = properties.keys();
            while (enumerator.hasMoreElements()) {
                final String key = enumerator.nextElement();
                final Object value = ((Hashtable<K, Object>)properties).get(key);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", (Object)key);
                jsonObject.put("content", value);
                jsonArray.put((Object)jsonObject);
            }
        }
        return jsonArray;
    }
    
    public JSONObject createJSONObject(final TreeNode node) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", (Object)node.id);
        jsonObject.put("text", (Object)node.text);
        jsonObject.put("child", node.child);
        jsonObject.put("parent_id", (Object)node.parent_id);
        jsonObject.put("im0", (Object)node.imageClosed);
        jsonObject.put("im1", (Object)node.imageOpen);
        jsonObject.put("im2", (Object)node.imageLeaf);
        if (node.nocheckbox) {
            jsonObject.put("nocheckbox", true);
        }
        if (node.checked) {
            jsonObject.put("checked", 1);
        }
        if (node.style != null) {
            jsonObject.put("style", (Object)node.style);
        }
        if (node.userData != null) {
            final JSONArray jsonArray = this.createUserDataJSONArray(node.userData);
            jsonObject.put("userdata", (Object)jsonArray);
        }
        return jsonObject;
    }
    
    private JSONObject createDummyChildJSONObject(final TreeNode treeNode) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", (Object)(treeNode.id + "_dummy"));
        jsonObject.put("text", (Object)(treeNode.text + "_dummy"));
        return jsonObject;
    }
    
    public JSONArray convertListToJSONArray(final List list) throws JSONException {
        final JSONArray array = new JSONArray();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            array.put(iterator.next());
        }
        return array;
    }
}
