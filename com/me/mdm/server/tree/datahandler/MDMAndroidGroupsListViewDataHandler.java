package com.me.mdm.server.tree.datahandler;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.android.knox.KnoxUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMAndroidGroupsListViewDataHandler extends DefaultListViewNodeDataObject
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
            final JSONObject resourceJSON = new JSONObject(resourceJSONStr);
            final List groupTypeList = new ArrayList();
            groupTypeList.add(6);
            final SelectQuery customGroupsForUserQuery = MDMGroupHandler.getCustomGroupsQuery(groupTypeList);
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            customGroupsForUserQuery.setCriteria(customGroupsForUserQuery.getCriteria().and(customerCri));
            if (searchValue != null) {
                final Criteria searchCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
                customGroupsForUserQuery.setCriteria(customGroupsForUserQuery.getCriteria().and(searchCri));
            }
            final List customGroupsList = MDMGroupHandler.getCustomGroupDetailsList(customGroupsForUserQuery);
            final Iterator groupItr = customGroupsList.iterator();
            String resourceName = "";
            final String resourceImage = "";
            while (groupItr.hasNext()) {
                final Hashtable groupDetails = groupItr.next();
                final TreeNode childNode = new TreeNode();
                final Properties userDataProperties = new Properties();
                final int knoxCount = KnoxUtil.getInstance().getKnoxCountInGroup(groupDetails.get("CUSTOM_GP_ID"));
                childNode.id = groupDetails.get("CUSTOM_GP_ID").toString();
                resourceName = groupDetails.get("CUSTOM_GP_NAME") + " (" + knoxCount + ")";
                childNode.child = false;
                childNode.nocheckbox = false;
                childNode.text = resourceName;
                childNode.style = "color:#000; font:12px 'Lato', 'Roboto', sans-serif; text-decoration:none;background: url(" + resourceImage + ");background-position: center right;background-repeat: no-repeat; display: inline-block; width:90%;";
                ((Hashtable<String, Boolean>)userDataProperties).put("isGroup", true);
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
}
