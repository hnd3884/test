package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import com.me.mdm.server.doc.DocMgmt;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMDocListViewDataHandler extends DefaultListViewNodeDataObject
{
    private static String availableString;
    private static String selectedString;
    private static String associatedString;
    
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        final String profileJSONStr = requestMap.get("profileAppJSON");
        final String filterButtonVal = requestMap.get("filterButtonVal");
        final String selectAllValue = requestMap.get("selectAllValue");
        final JSONObject profileJSON = (JSONObject)new JSONParser().parse(profileJSONStr);
        final List treeNodeList = new ArrayList();
        final SelectQuery query = this.getDocQuery(requestMap);
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (dataObject != null && dataObject.containsTable("DocumentDetails")) {
            final Iterator iterator = dataObject.getRows("DocumentDetails");
            while (iterator != null && iterator.hasNext()) {
                final Row docRow = iterator.next();
                final Long docID = (Long)docRow.get("DOC_ID");
                final int docType = (int)docRow.get("DOC_TYPE");
                String docName = (String)docRow.get("DOC_NAME");
                final Long docSize = (Long)docRow.get("SIZE");
                final TreeNode childNode = new TreeNode();
                childNode.id = String.valueOf(docID);
                if (filterButtonVal.equalsIgnoreCase(MDMDocListViewDataHandler.selectedString) && !profileJSON.containsKey((Object)childNode.id)) {
                    continue;
                }
                String sizeLong = null;
                if (1048576L < docSize) {
                    sizeLong = docSize / 1048576L + " MB";
                }
                else if (1024L < docSize) {
                    sizeLong = docSize / 1024L + " KB";
                }
                else {
                    sizeLong = docSize + " B";
                }
                if (docName.length() > 15) {
                    String docNameFull = docName;
                    docName = docName.substring(0, 15).concat("...");
                    docNameFull = docNameFull.replaceAll("'", "\\\\'");
                    docName = "<f onmouseout=\"return nd();\" onmouseover=\"overlib('" + docNameFull + "' , WIDTH , '10',WRAP, HAUTO,FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66',CSSCLASS,TEXTFONTCLASS,'bodytext',FGCLASS,'bodybg tablebg',BGCLASS,'bgClass')\">" + docName + "</f>";
                }
                final Properties userDataProperties = new Properties();
                ((Hashtable<String, Long>)userDataProperties).put("collectionId", docID);
                final String docImageUrl = DocMgmtDataHandler.getInstance().getDocImage(docType);
                final String defaultStyle = "background-image: url(" + docImageUrl + ") !important; background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important; background-size: 36px 25px, 17px 17px !important; background-position: 2% 46%, 92% 47% !important;";
                final String selectedStyle = "background-image: url(" + docImageUrl + "), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;color: rgb(95, 156, 212) !important; background-size: 36px 25px, 17px 17px !important; background-position: 2% 46%, 92% 47% !important;";
                ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
                ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
                ((Hashtable<String, String>)userDataProperties).put("latestVer", "");
                ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                ((Hashtable<String, Boolean>)userDataProperties).put("isUpgrade", false);
                ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", false);
                if (profileJSON.containsKey((Object)childNode.id)) {
                    childNode.checked = true;
                    childNode.style = selectedStyle;
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                }
                else {
                    childNode.style = defaultStyle;
                    ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                }
                childNode.text = docName.concat("<br/><div class='infoText' id='" + docID + "'>").concat(sizeLong).concat("</div>");
                childNode.userData = userDataProperties;
                treeNodeList.add(childNode);
            }
        }
        return treeNodeList;
    }
    
    private SelectQuery getSubQuery(final Column docCountColumn, final Long[] groupIds, final Long[] deviceIds) {
        if (groupIds != null) {
            return SyMUtil.formSelectQuery("DocumentToDeviceGroup", new Criteria(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"), (Object)groupIds, 8), new ArrayList((Collection<? extends E>)Arrays.asList(docCountColumn)), new ArrayList((Collection<? extends E>)Arrays.asList(docCountColumn)), (ArrayList)null, (ArrayList)null, new Criteria(docCountColumn.count(), (Object)groupIds.length, 0));
        }
        if (deviceIds != null) {
            return SyMUtil.formSelectQuery("DocumentManagedDeviceRel", new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)deviceIds, 8).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0)), new ArrayList((Collection<? extends E>)Arrays.asList(docCountColumn)), new ArrayList((Collection<? extends E>)Arrays.asList(docCountColumn)), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2))), new Criteria(docCountColumn.count(), (Object)deviceIds.length, 0));
        }
        return null;
    }
    
    private SelectQuery getDocQuery(final Map requestMap) {
        Long[] groupIds = null;
        Long[] deviceIds = null;
        Long customerID = null;
        final String groupIdsStr = requestMap.get("groupId");
        final String deviceIdsStr = requestMap.get("deviceId");
        final String filterButtonVal = requestMap.get("filterButtonVal");
        final String selectAllValue = requestMap.get("selectAllValue");
        Column docCountColumn = null;
        String subQueryTableName = null;
        if (groupIdsStr != null && !groupIdsStr.equals("") && !groupIdsStr.equals("[]") && !groupIdsStr.equals("['']")) {
            subQueryTableName = "DocumentToDeviceGroup";
            groupIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(groupIdsStr);
            customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(groupIds[0]);
            docCountColumn = Column.getColumn("DocumentToDeviceGroup", "DOC_ID");
        }
        else if (deviceIdsStr != null && !deviceIdsStr.equals("") && !deviceIdsStr.equals("[]") && !deviceIdsStr.equals("['']")) {
            subQueryTableName = "DocumentManagedDeviceRel";
            deviceIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(deviceIdsStr);
            customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceIds[0]);
            docCountColumn = Column.getColumn("DocumentManagedDeviceRel", "DOC_ID");
        }
        final SelectQuery subQuery = this.getSubQuery(docCountColumn, groupIds, deviceIds);
        final DerivedTable dTable = new DerivedTable(subQueryTableName, (Query)subQuery);
        final Table baseTable = Table.getTable("DocumentDetails");
        SelectQuery docQuery = (SelectQuery)new SelectQueryImpl(baseTable);
        Criteria criteria = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1));
        criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "SIZE"), (Object)0L, 1));
        if (MDMDocListViewDataHandler.availableString.equalsIgnoreCase(filterButtonVal)) {
            criteria = criteria.and(docCountColumn, (Object)null, 0);
        }
        else if (MDMDocListViewDataHandler.associatedString.equalsIgnoreCase(filterButtonVal)) {
            criteria = criteria.and(docCountColumn, (Object)null, 1);
        }
        docQuery = this.applyWhereCriteria(docQuery, requestMap, criteria);
        docQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        docQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        docQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        docQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        docQuery.addJoin(new Join(baseTable, (Table)dTable, new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        if (selectAllValue == null) {
            final String noOfObjStr = requestMap.get("noOfObj");
            final String startIndexStr = requestMap.get("start");
            int startIndex = 0;
            int noOfObj = 50;
            if (startIndexStr != null) {
                noOfObj = Integer.parseInt(noOfObjStr);
                startIndex = Integer.parseInt(startIndexStr);
            }
            final Range docRange = new Range(startIndex, noOfObj);
            docQuery.setRange(docRange);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("DocumentDetails", "DOC_NAME"), true);
        docQuery.addSortColumn(sortCol);
        return docQuery;
    }
    
    private SelectQuery applyWhereCriteria(SelectQuery docQuery, final Map requestMap, Criteria criteria) {
        final String searchValue = requestMap.get("search");
        final String filterTreeParams = requestMap.get("filterTreeParams");
        if (searchValue != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "DOC_NAME"), (Object)searchValue, 12, false));
        }
        if (filterTreeParams != null) {
            try {
                final JSONArray filterTreeJSON = (JSONArray)new JSONParser().parse(filterTreeParams);
                if (filterTreeJSON.size() > 0) {
                    docQuery = this.applyFilterCriteria(docQuery, filterTreeJSON);
                }
            }
            catch (final ParseException ex) {
                DocMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        final Criteria filterCri = docQuery.getCriteria();
        if (filterCri != null) {
            criteria = criteria.and(filterCri);
        }
        docQuery.setCriteria(criteria);
        return docQuery;
    }
    
    private SelectQuery applyFilterCriteria(final SelectQuery docQuery, final JSONArray filterTreeJSON) {
        Criteria docTagTypeCri = null;
        Criteria docTypeCri = null;
        for (final JSONObject filterJSON : filterTreeJSON) {
            final int filterType = Integer.parseInt((String)filterJSON.get((Object)"FILTER_TYPE"));
            final long filterMemberId = Long.parseLong((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
            switch (filterType) {
                case 1: {
                    final Criteria docTagTypeNewCri = this.getTagCriteria(filterMemberId);
                    docTagTypeCri = ((docTagTypeCri == null) ? docTagTypeNewCri : docTagTypeCri.or(docTagTypeNewCri));
                    continue;
                }
                case 2: {
                    final Criteria docTypeNewCri = this.getDocTypeCriteria(filterMemberId);
                    docTypeCri = ((docTypeCri == null) ? docTypeNewCri : docTypeCri.or(docTypeNewCri));
                    continue;
                }
            }
        }
        Criteria filterCri = null;
        if (docTagTypeCri != null) {
            docQuery.addJoin(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
            filterCri = ((filterCri == null) ? docTagTypeCri : filterCri.and(docTagTypeCri));
        }
        if (docTypeCri != null) {
            filterCri = ((filterCri == null) ? docTypeCri : filterCri.and(docTypeCri));
        }
        if (filterCri != null) {
            docQuery.setCriteria(filterCri);
        }
        return docQuery;
    }
    
    private Criteria getTagCriteria(final long tagId) {
        final Criteria filtercri = new Criteria(new Column("DocumentTagRel", "TAG_ID"), (Object)tagId, 0);
        return filtercri;
    }
    
    private Criteria getDocTypeCriteria(final long docTypeId) {
        final Criteria filtercri = new Criteria(new Column("DocumentDetails", 5), (Object)docTypeId, 0);
        return filtercri;
    }
    
    static {
        MDMDocListViewDataHandler.availableString = "all";
        MDMDocListViewDataHandler.selectedString = "selected";
        MDMDocListViewDataHandler.associatedString = "associated";
    }
}
