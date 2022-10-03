package com.me.mdm.server.adep;

import com.adventnet.ds.query.Join;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONArray;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class AsynBulkDEPTask extends CSVTask
{
    private static Logger logger;
    private static Logger assignUserLogger;
    private final int range = 25;
    
    protected void performOperation(final JSONObject jsonObj) throws Exception {
        this.checkAndEliminateInvalidRows();
        this.addDEPUserDetails();
    }
    
    public void addDEPUserDetails() throws Exception {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkDEPImportInfo"));
            sQuery.addSelectColumn(Column.getColumn("BulkDEPImportInfo", "*"));
            sQuery.setCriteria(new Criteria(Column.getColumn("BulkDEPImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("BulkDEPImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("BulkDEPImportInfo", "BULK_DEP_ID"), true));
            sQuery.setRange(new Range(0, 25));
            DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            int processed = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("BulkDEP"), (long)this.customerID);
            if (countStr != null) {
                processed = Integer.parseInt(countStr);
            }
            if (dobj != null) {
                do {
                    final Iterator<Row> iter = dobj.getRows("BulkDEPImportInfo", new Criteria(Column.getColumn("BulkDEPImportInfo", "ERROR_REMARKS"), (Object)null, 0));
                    final List<org.json.JSONObject> list = new ArrayList<org.json.JSONObject>();
                    while (iter.hasNext()) {
                        final Row r = iter.next();
                        ++processed;
                        final org.json.JSONObject jsonObj = this.toJSON(r);
                        list.add(jsonObj);
                    }
                    final org.json.JSONObject jsonObj2 = AdminEnrollmentHandler.assignUser(list, this.userID, 10, "BULK_DEP_ID", 1);
                    final JSONArray successList = jsonObj2.getJSONArray("SuccessList");
                    final JSONArray failedList = jsonObj2.getJSONArray("FailedList");
                    if (successList.length() != 0) {
                        final Long[] successArray = new Long[successList.length()];
                        for (int i = 0; i < successList.length(); ++i) {
                            successArray[i] = (Long)successList.get(i);
                        }
                        dobj.deleteRows("BulkDEPImportInfo", new Criteria(Column.getColumn("BulkDEPImportInfo", "BULK_DEP_ID"), (Object)successArray, 8));
                    }
                    for (int j = 0; j < failedList.length(); ++j) {
                        final org.json.JSONObject failureRowJSON = failedList.getJSONObject(j);
                        final Long assignUserID = failureRowJSON.getLong("BULK_DEP_ID");
                        final String errorMsg = String.valueOf(failureRowJSON.get("ErrorMsg"));
                        final Row assignUserRow = dobj.getRow("BulkDEPImportInfo", new Criteria(Column.getColumn("BulkDEPImportInfo", "BULK_DEP_ID"), (Object)assignUserID, 0));
                        assignUserRow.set("ERROR_REMARKS", (Object)errorMsg);
                        dobj.updateRow(assignUserRow);
                    }
                    MDMUtil.getPersistence().update(dobj);
                    CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("BulkDEP"), String.valueOf(processed), (long)this.customerID);
                    dobj = MDMUtil.getPersistence().get(sQuery);
                } while (!dobj.isEmpty());
                this.setFailureCount(this.customerID);
            }
        }
        catch (final Exception ex) {
            AsynBulkDEPTask.assignUserLogger.log(Level.SEVERE, "Exception in sendMultipleEnrollmentRequests :", ex);
            throw ex;
        }
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkDEPImportInfo"));
            final Column countColumn = Column.getColumn("BulkDEPImportInfo", "BULK_DEP_ID").count();
            countColumn.setColumnAlias("DEP_COUNT");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("BulkDEPImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("BulkDEPImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            final int count = (int)ds.getValue("DEP_COUNT");
            jsonObj.put((Object)CSVProcessor.getFailedLabel("BulkDEP"), (Object)String.valueOf(count));
            jsonObj.put((Object)CSVProcessor.getStatusLabel("BulkDEP"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private org.json.JSONObject toJSON(final Row r) throws JSONException {
        final org.json.JSONObject json = new org.json.JSONObject();
        json.put("BULK_DEP_ID", r.get("BULK_DEP_ID"));
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            if (r.get("EMAIL_ADDRESS") != null && !((String)r.get("EMAIL_ADDRESS")).isEmpty() && !((String)r.get("EMAIL_ADDRESS")).equalsIgnoreCase("--")) {
                final org.json.JSONObject userJSON = new org.json.JSONObject();
                userJSON.put("USER_IDENTIFIER", (Object)"EMAIL_ADDRESS");
                userJSON.put("EMAIL_ADDRESS", (Object)r.get("EMAIL_ADDRESS"));
                userJSON.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
                userJSON.put("CUSTOMER_ID", (Object)this.customerID);
                final org.json.JSONObject userDetailsJson = ManagedUserHandler.getInstance().getManagedUserDetails(userJSON);
                if (userDetailsJson != null && userDetailsJson.has("NAME")) {
                    this.addToJson(json, "UserName", String.valueOf(userDetailsJson.get("NAME")));
                    this.addToJson(json, "DomainName", String.valueOf(userDetailsJson.get("DOMAIN_NETBIOS_NAME")));
                }
                else if (r.get("USER_NAME") == null || ((String)r.get("USER_NAME")).isEmpty() || ((String)r.get("USER_NAME")).equalsIgnoreCase("--")) {
                    this.addToJson(json, "UserName", ((String)r.get("EMAIL_ADDRESS")).split("@")[0]);
                    this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
                }
                else {
                    this.addToJson(json, "UserName", (String)r.get("USER_NAME"));
                    this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
                }
            }
            else {
                this.addToJson(json, "UserName", (String)r.get("USER_NAME"));
                this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
            }
        }
        else {
            this.addToJson(json, "UserName", (String)r.get("USER_NAME"));
            this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
        }
        this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
        this.addToJson(json, "EmailAddr", (String)r.get("EMAIL_ADDRESS"));
        this.addToJson(json, "GroupName", (String)r.get("GROUP_NAME"));
        this.addToJson(json, "SerialNumber", (String)r.get("SERIAL_NUMBER"));
        json.put("CustomerId", r.get("CUSTOMER_ID"));
        return json;
    }
    
    private void addToJson(final org.json.JSONObject json, final String columnName, final String columnValue) throws JSONException {
        if (columnValue != null && !columnValue.equals("") && !columnValue.equals(" ") && !columnValue.equals("--")) {
            json.put(columnName, (Object)columnValue);
        }
    }
    
    public void checkAndEliminateInvalidRows() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkDEPImportInfo"));
            sQuery.addJoin(new Join("BulkDEPImportInfo", "DeviceForEnrollment", new String[] { "SERIAL_NUMBER" }, new String[] { "SERIAL_NUMBER" }, 1));
            sQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("BulkDEPImportInfo", "*"));
            sQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "*"));
            sQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "*"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "*"));
            final Criteria cSerialNum = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)null, 0);
            sQuery.setCriteria(cSerialNum);
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            final Iterator item = DO.getRows("BulkDEPImportInfo");
            while (item.hasNext()) {
                final Row bulkRow = item.next();
                bulkRow.set("ERROR_REMARKS", (Object)"dc.mdm.enroll.dep.error_remark_serialnum_error");
                DO.updateRow(bulkRow);
            }
            MDMUtil.getPersistence().update(DO);
        }
        catch (final Exception ex) {
            Logger.getLogger(AsynBulkDEPTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static {
        AsynBulkDEPTask.logger = Logger.getLogger("MDMEnrollment");
        AsynBulkDEPTask.assignUserLogger = Logger.getLogger("MDMAssignUser");
    }
}
