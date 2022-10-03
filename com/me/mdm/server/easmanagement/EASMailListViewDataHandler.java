package com.me.mdm.server.easmanagement;

import java.util.Hashtable;
import org.json.simple.parser.ParseException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Set;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class EASMailListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        return this.createChildTreeNodeList(requestMap);
    }
    
    private List createChildTreeNodeList(final Map requestMap) throws ParseException {
        final String selectedString = "selected";
        final List treeNodeList = new ArrayList();
        try {
            final String searchValue = requestMap.get("search");
            final String filterValue = requestMap.get("filter");
            String resourceJSONStr = requestMap.get("resourceJSON");
            if (resourceJSONStr.equalsIgnoreCase("")) {
                resourceJSONStr = "{}";
            }
            final String startIndexStr = requestMap.get("start");
            final String noOfObjStr = requestMap.get("noOfObj");
            int startIndex = 0;
            int noOfObj = 50;
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            final JSONObject resourceJSON = (JSONObject)new JSONParser().parse(resourceJSONStr);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EASMailboxDetails"));
            selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"));
            final Column nameCol = Column.getColumn("EASMailboxDetails", "DISPLAY_NAME");
            if (searchValue != null && !selectedString.equalsIgnoreCase(filterValue)) {
                final Criteria nameCri = new Criteria(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"), (Object)searchValue, 12, false);
                final Criteria emailCri = new Criteria(Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), (Object)searchValue, 12, false);
                final Criteria searchCri = nameCri.or(emailCri);
                selectQuery.setCriteria(searchCri);
            }
            if (filterValue != null && !filterValue.equalsIgnoreCase("all") && !filterValue.equalsIgnoreCase(selectedString)) {
                final String[] filterArr = filterValue.split(",");
                Criteria filterCri = null;
                Criteria nameFilterCri = null;
                for (int i = 0; i <= filterArr.length - 1; ++i) {
                    nameFilterCri = new Criteria(Column.getColumn("EASMailboxDetails", "DISPLAY_NAME"), (Object)filterArr[i], 10, false);
                    if (filterCri != null) {
                        filterCri = filterCri.or(nameFilterCri);
                    }
                    else {
                        filterCri = nameFilterCri;
                    }
                }
                selectQuery.setCriteria(filterCri.and(selectQuery.getCriteria()));
            }
            if (filterValue != null && filterValue.equalsIgnoreCase(selectedString)) {
                final Set resIdSet = resourceJSON.keySet();
                final Criteria selectCri = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"), (Object)resIdSet.toArray(), 8, false);
                selectQuery.setCriteria(selectCri.and(selectQuery.getCriteria()));
            }
            final Range mailBoxRange = new Range(startIndex, noOfObj);
            selectQuery.setRange(mailBoxRange);
            final SortColumn sortCol = new SortColumn(nameCol, true);
            selectQuery.addSortColumn(sortCol);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator iter = dataObject.getRows("EASMailboxDetails");
            String userDisplayName = "";
            String userEmailAddr = "";
            while (iter.hasNext()) {
                final TreeNode childNode = new TreeNode();
                final Properties userDataProperties = new Properties();
                final Row row = iter.next();
                childNode.id = ((Long)row.get("EAS_MAILBOX_ID")).toString();
                userDisplayName = (String)row.get("DISPLAY_NAME");
                userEmailAddr = (String)row.get("EMAIL_ADDRESS");
                if (filterValue != null && filterValue.equalsIgnoreCase(selectedString) && !resourceJSON.containsKey((Object)childNode.id)) {
                    continue;
                }
                childNode.child = false;
                childNode.nocheckbox = false;
                ((Hashtable<String, String>)userDataProperties).put("name", userDisplayName);
                ((Hashtable<String, String>)userDataProperties).put("email", userEmailAddr);
                ((Hashtable<String, Boolean>)userDataProperties).put("isGroup", true);
                if (userDisplayName.length() > 25) {
                    userDisplayName = userDisplayName.substring(0, 25).concat("...");
                }
                if (userEmailAddr.length() > 25) {
                    userEmailAddr = userEmailAddr.substring(0, 25).concat("...");
                }
                childNode.text = userDisplayName.concat("<br> <div class='infoText'>").concat(userEmailAddr).concat("</div>");
                if (resourceJSON.containsKey((Object)childNode.id)) {
                    childNode.checked = true;
                    childNode.style = "background-image: url(\"/images/user.png\"),  url(\"/images/select.png\") !important; background-position: 5px center,188px !important; background-color: #f4f9fe !important; solid rgb(203, 224, 245); color: #3E7EB9;";
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                }
                else {
                    childNode.style = "background-image: url(\"/images/user.png\") !important; background-position: 5px center,188px !important; background-color: rgb(249, 249, 249) !important; border: 1px solid #e9e9e9; color: black;";
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                }
                childNode.userData = userDataProperties;
                treeNodeList.add(childNode);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(EASMailListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return treeNodeList;
    }
}
