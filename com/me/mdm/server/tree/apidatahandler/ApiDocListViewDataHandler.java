package com.me.mdm.server.tree.apidatahandler;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;

public class ApiDocListViewDataHandler extends ApiListViewDataHandler
{
    @Override
    protected SelectQuery getSelectQuery() {
        final Table baseTable = Table.getTable("DocumentDetails");
        (this.selectQuery = (SelectQuery)new SelectQueryImpl(baseTable)).addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        this.selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        this.selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        return this.selectQuery;
    }
    
    @Override
    protected SelectQuery setCriteria() throws APIHTTPException {
        Long[] groupIds = null;
        Long[] deviceIds = null;
        final Long customerID = this.requestJson.optLong("customerId");
        final JSONArray groupIdsArray = this.requestJson.optJSONArray("groupIds");
        final JSONArray deviceIdsArray = this.requestJson.optJSONArray("deviceIds");
        final String filterButtonVal = this.requestJson.optString("filterButtonVal", "selected");
        String selectAllValue = null;
        if (this.requestJson.optBoolean("selectAll")) {
            selectAllValue = "all";
        }
        final Integer startIndex = this.requestJson.optInt("startIndex", 0);
        final Integer noOfObj = this.requestJson.optInt("noOfObj", 50);
        final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdsArray);
        final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdsArray);
        Column docCountColumn = null;
        String subQueryTableName = null;
        if (groupIdList != null && !groupIdList.isEmpty()) {
            groupIds = groupIdList.toArray(new Long[groupIdList.size()]);
            subQueryTableName = "DocumentToDeviceGroup";
            docCountColumn = Column.getColumn("DocumentToDeviceGroup", "DOC_ID");
        }
        else if (deviceIdList != null && !deviceIdList.isEmpty()) {
            deviceIds = deviceIdList.toArray(new Long[deviceIdList.size()]);
            subQueryTableName = "DocumentManagedDeviceRel";
            docCountColumn = Column.getColumn("DocumentManagedDeviceRel", "DOC_ID");
        }
        final Column distDocId = (Column)docCountColumn.clone();
        distDocId.setColumnAlias("dist_doc_id");
        final Table baseTable = Table.getTable("DocumentDetails");
        final SelectQuery subQuery = this.getSubQuery(docCountColumn, groupIds, deviceIds);
        final DerivedTable dTable = new DerivedTable(subQueryTableName, (Query)subQuery);
        Criteria criteria = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1));
        if ("all".equalsIgnoreCase(filterButtonVal)) {
            criteria = criteria.and(docCountColumn, (Object)null, 0);
        }
        else if ("associated".equalsIgnoreCase(filterButtonVal)) {
            criteria = criteria.and(docCountColumn, (Object)null, 1);
        }
        (this.selectQuery = this.applyWhereCriteria(this.selectQuery, this.requestJson, criteria)).addSelectColumn(distDocId);
        this.selectQuery.addJoin(new Join(baseTable, (Table)dTable, new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        final SortColumn sortCol = new SortColumn(Column.getColumn("DocumentDetails", "DOC_NAME"), true);
        this.selectQuery.addSortColumn(sortCol);
        this.selectQuery.setGroupByClause(new GroupByClause(this.selectQuery.getSelectColumns()));
        return this.selectQuery;
    }
    
    public JSONObject fetchResultObject() throws APIHTTPException {
        ApiDocListViewDataHandler.logger.log(Level.INFO, "Get Filtered values for the OsUpdatePolicy with messages {0}", this.requestJson);
        try {
            final Boolean isGroup = this.requestJson.optBoolean("isGroup");
            final String filterButtonVal = this.requestJson.optString("filterButtonVal");
            final JSONObject resultJson = new JSONObject();
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)this.selectQuery);
            ApiDocListViewDataHandler.logger.log(Level.FINE, "Query formation for Doc filtered values completed with result");
            final JSONArray yetToApplyArray = new JSONArray();
            final JSONArray successfullyAppliedArray = new JSONArray();
            while (dmDataSetWrapper.next()) {
                final Long docID = (Long)dmDataSetWrapper.getValue("DOC_ID");
                final int docType = (int)dmDataSetWrapper.getValue("DOC_TYPE");
                final String docName = (String)dmDataSetWrapper.getValue("DOC_NAME");
                final Long docSize = (Long)dmDataSetWrapper.getValue("SIZE");
                final Object disDocId = dmDataSetWrapper.getValue("dist_doc_id");
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
                final JSONObject docObject = new JSONObject();
                docObject.put("doc_id", (Object)docID);
                docObject.put("name", (Object)docName);
                docObject.put("doc_type", docType);
                docObject.put("doc_size", (Object)sizeLong);
                if (disDocId == null) {
                    yetToApplyArray.put((Object)docObject);
                }
                else {
                    successfullyAppliedArray.put((Object)docObject);
                }
            }
            if (filterButtonVal.equalsIgnoreCase("all") || filterButtonVal == "") {
                resultJson.put("yet_to_apply", (Object)yetToApplyArray);
            }
            if (filterButtonVal.equalsIgnoreCase("associated") || filterButtonVal == "") {
                resultJson.put("successfull_applied", (Object)successfullyAppliedArray);
            }
            return resultJson;
        }
        catch (final Exception ex) {
            ApiDocListViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching filter data for Doc", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
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
    
    private SelectQuery applyWhereCriteria(final SelectQuery docQuery, final JSONObject requestJSON, Criteria criteria) {
        final String searchValue = requestJSON.optString("searchValue", (String)null);
        final JSONArray tagTypeArray = requestJSON.optJSONArray("tagType");
        final Boolean unassignedTag = requestJSON.optBoolean("unassignedTag");
        final JSONArray docTypeArray = requestJSON.optJSONArray("docType");
        if (searchValue != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "DOC_NAME"), (Object)searchValue, 12, false));
        }
        Criteria docTagTypeNewCri = null;
        if (unassignedTag) {
            docQuery.addJoin(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
            docTagTypeNewCri = new Criteria(new Column("DocumentTagRel", "TAG_ID"), (Object)null, 0);
        }
        if (tagTypeArray != null && tagTypeArray.length() > 0) {
            final List docTagTypeIds = JSONUtil.getInstance().convertLongJSONArrayTOList(tagTypeArray);
            if (docTagTypeNewCri == null) {
                docQuery.addJoin(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
            }
            docTagTypeNewCri = ((docTagTypeNewCri != null) ? docTagTypeNewCri.or(this.getTagCriteria(docTagTypeIds)) : this.getTagCriteria(docTagTypeIds));
        }
        if (docTagTypeNewCri != null) {
            criteria = criteria.and(docTagTypeNewCri);
        }
        if (docTypeArray != null && docTypeArray.length() > 0) {
            final List docTypeIds = JSONUtil.getInstance().convertLongJSONArrayTOList(docTypeArray);
            final Criteria docTypeNewCri = this.getDocTypeCriteria(docTypeIds);
            criteria = criteria.and(docTypeNewCri);
        }
        final Criteria filterCri = docQuery.getCriteria();
        if (filterCri != null) {
            criteria = criteria.and(filterCri);
        }
        docQuery.setCriteria(criteria);
        return docQuery;
    }
    
    private Criteria getTagCriteria(final List tagIds) {
        final Criteria filtercri = new Criteria(new Column("DocumentTagRel", "TAG_ID"), (Object)tagIds.toArray(), 8);
        return filtercri;
    }
    
    private Criteria getDocTypeCriteria(final List docTypeIds) {
        final Criteria filtercri = new Criteria(new Column("DocumentDetails", 5), (Object)docTypeIds.toArray(), 8);
        return filtercri;
    }
}
