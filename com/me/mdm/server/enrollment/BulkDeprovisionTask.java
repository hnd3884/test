package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import java.util.List;
import com.me.mdm.server.enrollment.deprovision.DeprovisionRequest;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.csv.CSVImportStatusHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class BulkDeprovisionTask extends CSVTask
{
    private static Logger logger;
    private int range;
    private int deprovisionReason;
    private int deprovisionType;
    private String comment;
    private BulkDeprovisionCSVProcessor bulkDeprovisionCSVProcessor;
    
    public BulkDeprovisionTask() {
        this.range = 100;
        this.deprovisionReason = 4;
        this.deprovisionType = 1;
        this.comment = "--";
        this.bulkDeprovisionCSVProcessor = new BulkDeprovisionCSVProcessor();
    }
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        final Long customerID = Long.parseLong(((Hashtable<K, String>)taskProps).get("customerID"));
        final int deprovisionReason = Integer.parseInt(((Hashtable<K, String>)taskProps).get("DEPROVISION_REASON"));
        final int deprovisionType = Integer.parseInt(((Hashtable<K, String>)taskProps).get("DEPROVISION_TYPE"));
        final String comment = ((Hashtable<K, String>)taskProps).get("COMMENT");
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put((Object)"USER_ID", (Object)Long.parseLong(((Hashtable<K, String>)taskProps).get("userID")));
        jsonObj.put((Object)"CUSTOMER_ID", (Object)customerID);
        jsonObj.put((Object)"DEPROVISION_TYPE", (Object)deprovisionType);
        jsonObj.put((Object)"DEPROVISION_REASON", (Object)deprovisionReason);
        jsonObj.put((Object)"COMMENT", (Object)comment);
        BulkDeprovisionTask.logger.log(Level.INFO, "In executeTask of bulk deprovision input json:{0}", jsonObj.toJSONString());
        return jsonObj;
    }
    
    protected void performOperation(final JSONObject json) throws Exception {
        try {
            final int totalRowCount = DBUtil.getRecordCount("BulkDeprovisionImportInfo", "BULK_DEPROVISION_ID", new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "ERROR_REMARKS_ARGS"), (Object)null, 0)).and(new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)));
            int iosDeviceCount = 0;
            for (int j = 0; j <= totalRowCount / this.range; ++j) {
                this.deprovisionReason = (int)json.get((Object)"DEPROVISION_REASON");
                this.deprovisionType = (int)json.get((Object)"DEPROVISION_TYPE");
                this.comment = (String)json.get((Object)"COMMENT");
                this.customerID = (Long)json.get((Object)"CUSTOMER_ID");
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkDeprovisionImportInfo"));
                sQuery.addSelectColumn(Column.getColumn("BulkDeprovisionImportInfo", "BULK_DEPROVISION_ID"));
                sQuery.addSelectColumn(Column.getColumn("BulkDeprovisionImportInfo", "IMEI"));
                sQuery.addSelectColumn(Column.getColumn("BulkDeprovisionImportInfo", "SERIAL_NUMBER"));
                sQuery.addSelectColumn(Column.getColumn("BulkDeprovisionImportInfo", "UDID"));
                sQuery.addSelectColumn(Column.getColumn("BulkDeprovisionImportInfo", "CUSTOMER_ID"));
                sQuery.setCriteria(new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)));
                sQuery.setRange(new Range((j * this.range == 0) ? 0 : (j * this.range + 1), this.range));
                sQuery.addSortColumn(new SortColumn(Column.getColumn("BulkDeprovisionImportInfo", "BULK_DEPROVISION_ID"), true));
                final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                int processed = 0;
                final CustomerParamsHandler instance = CustomerParamsHandler.getInstance();
                this.bulkDeprovisionCSVProcessor.getClass();
                final String countStr = instance.getParameterValue(CSVProcessor.getProcessedLabel("BulkDeprovision"), (long)this.customerID);
                if (countStr != null) {
                    processed = Integer.parseInt(countStr);
                }
                if (!dobj.isEmpty()) {
                    final Iterator<Row> iter = dobj.getRows("BulkDeprovisionImportInfo", new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "ERROR_REMARKS"), (Object)null, 0));
                    final JSONArray devicelist = new JSONArray();
                    JSONArray failedList = new JSONArray();
                    while (iter.hasNext()) {
                        final Row r = iter.next();
                        ++processed;
                        final org.json.JSONObject jsonObj = this.toJSON(r);
                        final org.json.JSONObject failedJSON = this.validateBulkDeprovisionColumns(jsonObj);
                        if (failedJSON.has("ErrorMsg")) {
                            failedList.put((Object)failedJSON);
                        }
                        else {
                            devicelist.put((Object)jsonObj);
                        }
                    }
                    final org.json.JSONObject jsonObj2 = this.bulkDeprovisionDevice(devicelist);
                    failedList = JSONUtil.mergeJSONArray(failedList, jsonObj2.optJSONArray("FailureList"));
                    for (int i = 0; i < failedList.length(); ++i) {
                        final org.json.JSONObject failureRowJSON = failedList.getJSONObject(i);
                        final Long assignUserID = failureRowJSON.getLong("BULK_DEPROVISION_ID");
                        final String errorMsg = failureRowJSON.optString("ErrorMsg", "--");
                        final Row assignUserRow = dobj.getRow("BulkDeprovisionImportInfo", new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "BULK_DEPROVISION_ID"), (Object)assignUserID, 0));
                        assignUserRow.set("ERROR_REMARKS", (Object)errorMsg);
                        dobj.updateRow(assignUserRow);
                    }
                    MDMUtil.getPersistence().update(dobj);
                    iosDeviceCount += jsonObj2.optInt("IOSDeviceCount", 0);
                    final CustomerParamsHandler instance2 = CustomerParamsHandler.getInstance();
                    this.bulkDeprovisionCSVProcessor.getClass();
                    instance2.addOrUpdateParameter(CSVProcessor.getProcessedLabel("BulkDeprovision"), String.valueOf(processed), (long)this.customerID);
                }
                this.setFailureCount(this.customerID);
            }
            final CSVImportStatusHandler instance3 = CSVImportStatusHandler.getInstance();
            final Long customerID = this.customerID;
            this.bulkDeprovisionCSVProcessor.getClass();
            final org.json.JSONObject bulkDeprovisionDetail = new org.json.JSONObject(instance3.getImportStatus(customerID, "BulkDeprovision").toJSONString());
            if (String.valueOf(bulkDeprovisionDetail.get("STATUS")).equals("COMPLETED")) {
                final String loginUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId((Long)json.get((Object)"USER_ID")));
                ManagedDeviceHandler.getInstance().addMDMEventLogForBulkDeprovision(loginUserName, (int)bulkDeprovisionDetail.getJSONObject("DETAILS").get("SUCCESS"));
            }
            final CustomerParamsHandler instance4 = CustomerParamsHandler.getInstance();
            final BulkDeprovisionCSVProcessor bulkDeprovisionCSVProcessor = this.bulkDeprovisionCSVProcessor;
            instance4.addOrUpdateParameter("BulkDeprovision_IOSDeviceCount", Integer.toString(iosDeviceCount), (long)this.customerID);
        }
        catch (final Exception ex) {
            BulkDeprovisionTask.logger.log(Level.SEVERE, "Exception in bulkdeprovision :{0}", ex);
            throw ex;
        }
    }
    
    private org.json.JSONObject toJSON(final Row r) throws JSONException {
        final org.json.JSONObject json = new org.json.JSONObject();
        json.put("BULK_DEPROVISION_ID", r.get("BULK_DEPROVISION_ID"));
        this.addToJson(json, "IMEI", (String)r.get("IMEI"));
        this.addToJson(json, "SERIAL_NUMBER", (String)r.get("SERIAL_NUMBER"));
        this.addToJson(json, "UDID", (String)r.get("UDID"));
        json.put("CUSTOMER_ID", r.get("CUSTOMER_ID"));
        return json;
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkDeprovisionImportInfo"));
            final Column countColumn = Column.getColumn("BulkDeprovisionImportInfo", "BULK_DEPROVISION_ID").count();
            countColumn.setColumnAlias("COUNT_COLUMN");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("BulkDeprovisionImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObject;
            final JSONObject jsonObj = jsonObject = new JSONObject();
            this.bulkDeprovisionCSVProcessor.getClass();
            jsonObject.put((Object)CSVProcessor.getFailedLabel("BulkDeprovision"), ds.getValue("COUNT_COLUMN"));
            final JSONObject jsonObject2 = jsonObj;
            this.bulkDeprovisionCSVProcessor.getClass();
            jsonObject2.put((Object)CSVProcessor.getStatusLabel("BulkDeprovision"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            BulkDeprovisionTask.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void addToJson(final org.json.JSONObject json, final String columnName, final String columnValue) throws JSONException {
        if (columnValue != null && !columnValue.equals("") && !columnValue.equals(" ") && !columnValue.equals("--")) {
            json.put(columnName, (Object)columnValue);
        }
    }
    
    private boolean isEmpty(final JSONObject json, final String key) {
        return !json.containsKey((Object)key) || json.get((Object)key) == null || ((String)json.get((Object)key)).isEmpty() || ((String)json.get((Object)key)).equals("--");
    }
    
    private org.json.JSONObject validateBulkDeprovisionColumns(final org.json.JSONObject inputJson) throws Exception {
        final org.json.JSONObject failureJSON = new org.json.JSONObject();
        final JSONArray invalidEntries = new JSONArray();
        final MDMEnrollmentUtil enrollmentUtil = new MDMEnrollmentUtil();
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "IMEI") && !enrollmentUtil.isValidInputs(inputJson.getString("IMEI"), "IMEI")) {
            invalidEntries.put((Object)"IMEI");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "SERIAL_NUMBER") && !enrollmentUtil.isValidInputs(inputJson.getString("SERIAL_NUMBER"), "SerialNumber")) {
            invalidEntries.put((Object)"SERIAL_NUMBER");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "UDID") && !enrollmentUtil.isValidInputs(inputJson.getString("UDID"), "UDID")) {
            invalidEntries.put((Object)"UDID");
        }
        final String invalidEntriesString = JSONUtil.getInstance().convertJSONArrayToString(invalidEntries);
        if (invalidEntries.length() > 0) {
            failureJSON.put("BULK_DEPROVISION_ID", inputJson.get("BULK_DEPROVISION_ID"));
            failureJSON.put("ErrorMsg", (Object)I18N.getMsg("mdm.csv.invalid_inputs", new Object[] { invalidEntriesString.substring(0, invalidEntriesString.length() - 1) }));
        }
        return failureJSON;
    }
    
    private org.json.JSONObject bulkDeprovisionDevice(final JSONArray deviceList) throws Exception {
        final List<Long> resourceList = new ArrayList<Long>();
        final org.json.JSONObject resourceIdToPrimaryKey = new org.json.JSONObject();
        final JSONArray csvErrorList = new JSONArray();
        for (int i = 0; i < deviceList.length(); ++i) {
            final org.json.JSONObject deviceJSON = deviceList.getJSONObject(i);
            String slno = null;
            String udid = null;
            String imei = null;
            if (deviceJSON.opt("SERIAL_NUMBER") != null) {
                slno = String.valueOf(deviceJSON.get("SERIAL_NUMBER"));
            }
            if (deviceJSON.opt("IMEI") != null) {
                imei = String.valueOf(deviceJSON.get("IMEI"));
            }
            if (deviceJSON.opt("UDID") != null) {
                udid = String.valueOf(deviceJSON.get("UDID"));
            }
            try {
                final Long resourecId = ManagedDeviceHandler.getInstance().getResourceIdForDeprovision(slno, imei, udid);
                if (resourceList.contains(resourecId)) {
                    throw new SyMException(51033, "Device is not in enrolled state!", "mdm.deprovision.error.not_in_enrolled_state", (Throwable)null);
                }
                resourceList.add(resourecId);
                resourceIdToPrimaryKey.put(String.valueOf(resourecId), deviceJSON.get("BULK_DEPROVISION_ID"));
            }
            catch (final SyMException exp) {
                final org.json.JSONObject errorJson = new org.json.JSONObject();
                errorJson.put("BULK_DEPROVISION_ID", deviceJSON.get("BULK_DEPROVISION_ID"));
                errorJson.put("ErrorMsg", (Object)I18N.getMsg(exp.getErrorKey(), new Object[0]));
                csvErrorList.put((Object)errorJson);
            }
        }
        org.json.JSONObject returnJSON = new org.json.JSONObject();
        final JSONArray failedList = new JSONArray();
        if (resourceList.size() != 0) {
            final DeprovisionRequest deprovisionRequest = new DeprovisionRequest(this.customerID, this.userID, this.deprovisionType, this.deprovisionReason, this.comment, resourceList);
            deprovisionRequest.setForceDeprovision(true);
            returnJSON = ManagedDeviceHandler.getInstance().deprovisionDevice(deprovisionRequest);
            if (returnJSON.has("FailureList")) {
                for (final Object eachObj : returnJSON.getJSONArray("FailureList")) {
                    final org.json.JSONObject jsonObject = (org.json.JSONObject)eachObj;
                    jsonObject.put("BULK_DEPROVISION_ID", resourceIdToPrimaryKey.get(String.valueOf(jsonObject.get("RESOURCE_ID"))));
                    jsonObject.remove("RESOURCE_ID");
                    failedList.put((Object)jsonObject);
                }
            }
        }
        for (int j = 0; j < csvErrorList.length(); ++j) {
            failedList.put((Object)csvErrorList.getJSONObject(j));
        }
        returnJSON.put("FailureList", (Object)failedList);
        return returnJSON;
    }
    
    static {
        BulkDeprovisionTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
