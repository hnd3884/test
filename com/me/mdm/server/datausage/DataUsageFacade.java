package com.me.mdm.server.datausage;

import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.server.datausage.data.DataUsageSummary;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.datausage.data.DataEntity;
import com.me.mdm.server.datausage.data.DataUsageSummaryQuery;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class DataUsageFacade
{
    public static final String TIME_PERIOD = "time_period";
    public static final String ENTITY = "entity";
    public static final String DATA_SUMMARY = "data_summary";
    public static final String DEVICE_SUMMARIES = "device_summaries";
    
    public JSONObject getResourceDataUsageSummary(final JSONObject request) throws DataAccessException, JSONException {
        final JSONObject response = new JSONObject();
        Long deviceId = APIUtil.getResourceID(request, "device_id");
        final Boolean isDetailed = request.optBoolean("detailed", (boolean)Boolean.FALSE);
        if (deviceId == -1L) {
            final String udid = APIUtil.getResourceIDString(request, "udid");
            deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        }
        if (deviceId == null) {
            throw new APIHTTPException("COM0008", new Object[] { "device_id - " + deviceId });
        }
        final DataUsageSummaryQuery dataUsageSummaryQuery = new DataUsageSummaryQuery();
        dataUsageSummaryQuery.addJoin("DataEntity", 2);
        dataUsageSummaryQuery.addJoin("DataTrackingPeriods", 2);
        dataUsageSummaryQuery.addFilters(deviceId, 0, "DataTrackingSummary");
        if (!isDetailed) {
            final DataEntity dataEntity = new DataEntity();
            dataEntity.identifier = "data.device.full";
            dataEntity.type = DataUsageConstants.DataUsages.DataEntities.MOBILE_TYPE;
            dataUsageSummaryQuery.addFilters(dataEntity, 0, "DataTrackingSummary");
        }
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("DataEntity", "*"));
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("DataTrackingPeriods", "*"));
        dataUsageSummaryQuery.applyFilters();
        final DataObject dataObject = MDMUtil.getPersistenceLite().get((SelectQuery)dataUsageSummaryQuery);
        final Iterator iterator = dataObject.getRows("DataTrackingSummary");
        final JSONArray deviceSummary = new JSONArray();
        while (iterator.hasNext()) {
            final JSONObject jsonObject = new JSONObject();
            final Row summary = iterator.next();
            final Row entity = dataObject.getRow("DataEntity", new Criteria(Column.getColumn("DataEntity", "ENTITY_ID"), summary.get("ENTITY_ID"), 0));
            final Row period = dataObject.getRow("DataTrackingPeriods", new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_ID"), summary.get("PERIOD_ID"), 0));
            jsonObject.put("USAGE", (Object)(summary.get("USAGE") + " MB"));
            final JSONObject timeperiod = new JSONObject();
            timeperiod.put("PERIOD_START_TIME", (Object)APIUtil.getNewInstance().getAPITimeFromMillis((Long)period.get("PERIOD_START_TIME")));
            timeperiod.put("PERIOD_END_TIME", (Object)APIUtil.getNewInstance().getAPITimeFromMillis((Long)period.get("PERIOD_END_TIME")));
            final JSONObject trackingEntity = new JSONObject();
            trackingEntity.put("ENTITY_IDENTIFIER", (Object)DataEntity.getIdentifierKey((String)entity.get("ENTITY_IDENTIFIER")));
            trackingEntity.put("ENTITY_TYPE", (Object)DataEntity.getTypeKey((Integer)entity.get("ENTITY_TYPE")));
            jsonObject.put("time_period", (Object)timeperiod);
            jsonObject.put("entity", (Object)trackingEntity);
            deviceSummary.put((Object)jsonObject);
        }
        response.put("data_summary", (Object)deviceSummary);
        response.put("device_id", (Object)deviceId);
        return response;
    }
    
    public JSONObject getAllDevicesDataUsage(final JSONObject request) throws Exception {
        final JSONArray dataUsages = new JSONArray();
        final JSONObject response = new JSONObject();
        final DataUsageSummaryQuery dataUsageSummaryQuery = new DataUsageSummaryQuery(Boolean.FALSE);
        dataUsageSummaryQuery.addJoin("DataEntity", 2);
        dataUsageSummaryQuery.addJoin("DataTrackingPeriods", 2);
        dataUsageSummaryQuery.addJoin("ManagedDevice", 2);
        dataUsageSummaryQuery.addJoin("ManagedDeviceExtn", 2);
        dataUsageSummaryQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        dataUsageSummaryQuery.addCustomerCriteria(APIUtil.getCustomerID(request));
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final PagingUtil pagingUtil = apiUtil.getPagingParams(request);
        final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(request);
        final String platform = request.getJSONObject("msg_header").getJSONObject("filters").optString("platform", (String)null);
        final Object obj = request.getJSONObject("msg_header").getJSONObject("filters").optString("group_id", (String)null);
        final Boolean isDetailed = request.getBoolean("detailed");
        Long group_id = null;
        if (obj != null) {
            group_id = Long.valueOf(obj.toString());
        }
        if (group_id != null) {
            final Join groupRelJoin = new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2);
            dataUsageSummaryQuery.addJoin(groupRelJoin);
            final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)group_id, 0);
            dataUsageSummaryQuery.appendCriteria(groupCriteria);
        }
        final List platforms = MDMUtil.getInstance().getPlatformConstantsForPlatformString(platform);
        if (platforms.size() > 0) {
            dataUsageSummaryQuery.addFilters(platforms.toArray(), 8, "ManagedDevice");
        }
        if (!isDetailed) {
            final DataEntity dataEntity = new DataEntity();
            dataEntity.identifier = "data.device.full";
            dataEntity.type = DataUsageConstants.DataUsages.DataEntities.MOBILE_TYPE;
            dataUsageSummaryQuery.addFilters(dataEntity, 0, "DataTrackingSummary");
        }
        dataUsageSummaryQuery.applyFilters();
        final SelectQuery countQuery = (SelectQuery)dataUsageSummaryQuery.clone();
        final Column resColumn = new Column("DataTrackingSummary", "RESOURCE_ID");
        countQuery.addSelectColumn(resColumn.count());
        final int count = DBUtil.getRecordCount(countQuery);
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("DataTrackingSummary", "*"));
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("DataEntity", "*"));
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("DataTrackingPeriods", "*"));
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        if (deltaTokenUtil != null) {
            final Long lastRequestTime = deltaTokenUtil.getRequestTimestamp();
            final Criteria deltaCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_START_TIME"), (Object)lastRequestTime, 5);
            dataUsageSummaryQuery.appendCriteria(deltaCriteria);
        }
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", count);
        response.put("metadata", (Object)meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(request.getJSONObject("msg_header").get("request_url")));
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            response.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                response.put("paging", (Object)pagingJSON);
            }
            dataUsageSummaryQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get((SelectQuery)dataUsageSummaryQuery);
            final Iterator iterator = dataObject.getRows("DataTrackingSummary");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Row periodRow = dataObject.getRow("DataTrackingPeriods", new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_ID"), row.get("PERIOD_ID"), 0));
                final Row entityRow = dataObject.getRow("DataEntity", new Criteria(Column.getColumn("DataEntity", "ENTITY_ID"), row.get("ENTITY_ID"), 0));
                final JSONObject jsonObject = new DataUsageSummary(row, entityRow, periodRow).toJSON();
                final Row resRow = dataObject.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), row.get("RESOURCE_ID"), 0));
                jsonObject.put("device_name", resRow.get("NAME"));
                dataUsages.put((Object)jsonObject);
            }
        }
        response.put("device_summaries", (Object)dataUsages);
        return response;
    }
    
    public JSONObject getDataUsageSettings(final JSONObject request) throws Exception {
        final JSONObject response = new JSONObject();
        final Long customerID = APIUtil.getCustomerID(request);
        if (customerID == null) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DataTrackingSettings"));
        selectQuery.addSelectColumn(Column.getColumn("DataTrackingSettings", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DataTrackingSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Row row = null;
        if (dataObject.isEmpty()) {
            row = new Row("DataTrackingSettings");
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("RETAIN_DATA_DAYS", (Object)60);
            dataObject.addRow(row);
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        else {
            row = dataObject.getFirstRow("DataTrackingSettings");
        }
        response.put("RETAIN_DATA_DAYS", row.get("RETAIN_DATA_DAYS"));
        return response;
    }
    
    public JSONObject setDataUsageSettings(final JSONObject request) throws Exception {
        final JSONObject response = new JSONObject();
        final Long customerID = APIUtil.getCustomerID(request);
        if (customerID == null) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DataTrackingSettings"));
        selectQuery.addSelectColumn(Column.getColumn("DataTrackingSettings", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DataTrackingSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Row row = null;
        final int days = (int)request.get("RETAIN_DATA_DAYS".toLowerCase());
        if (dataObject.isEmpty()) {
            row = new Row("DataTrackingSettings");
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("RETAIN_DATA_DAYS", (Object)days);
            dataObject.addRow(row);
        }
        else {
            row = dataObject.getFirstRow("DataTrackingSettings");
            row.set("RETAIN_DATA_DAYS", (Object)days);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
        response.put("RETAIN_DATA_DAYS", row.get("RETAIN_DATA_DAYS"));
        return response;
    }
}
