package com.me.mdm.api.reports.integ;

import com.me.devicemanagement.framework.webclient.reports.ReportBIDataValueTransformer;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.reportcriteria.CriteriaColumnValueUtil;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.apache.commons.lang3.time.DurationFormatUtils;
import java.util.List;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.reports.ReportBIUtil;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class SyncAPIRequestHandler extends ApiRequestHandler
{
    private static Logger out;
    private static final String MSP_ERROR_CODE = "15005";
    private static final String MSP_ERROR_MSG = "Report API not available for MSP yet..!";
    private static final String INVALID_MODULE_ERROR_CODE = "15001";
    private static final String INVALID_MODULE_ERROR_MSG = "tableid is invalid";
    private static final String INVALID_TABLE_ERROR_CODE = "15000";
    private static final String INVALID_TABLE_ERROR_MSG = "TableID is not present in the request";
    private static final String TABLE_NA_ERROR_CODE = "15003";
    private static final String TABLE_NA_ERROR_MSG = "Table is not available in this version";
    private static final String USER_NOT_AUTHORIZED_ERROR_CODE = "15002";
    private static final String USER_NOT_AUTHORIZED_ERROR_MSG = "User does not have privileges over this table";
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            final JSONObject apiResponse = new JSONObject();
            final JSONObject msgRespone = new JSONObject();
            final JSONObject tableRespone = this.getTableDataForTableID(apiRequest.toJSONObject());
            if (tableRespone.has("status")) {
                response.put("RESPONSE", (Object)tableRespone);
            }
            else {
                msgRespone.put("sync", (Object)tableRespone);
                apiResponse.put("message_type", (Object)"sync");
                apiResponse.put("message_response", (Object)msgRespone);
                apiResponse.put("status", (Object)"success");
                response.put("RESPONSE", (Object)apiResponse);
            }
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in SyncAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getTableDataForTableID(final JSONObject apiRequest) throws JSONException {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        if (isMSP) {
            return this.getErrorJSON("15005", "Report API not available for MSP yet..!");
        }
        final Long userID = APIUtil.getUserID(apiRequest);
        final String tableID = APIUtil.getStringFilter(apiRequest, "tableid");
        final String modifiedTimeString = APIUtil.getStringFilter(apiRequest, "modifiedtime");
        String offset = APIUtil.getStringFilter(apiRequest, "offset");
        final String apiVersion = "1.2";
        final String rowCount = APIUtil.getStringFilter(apiRequest, "rowcount");
        final Integer rowCountNumber = (rowCount == null) ? 1000 : Integer.parseInt(rowCount);
        offset = ((offset == null) ? "0" : offset);
        final Long modifiedTime = (modifiedTimeString == null) ? null : Long.valueOf(modifiedTimeString);
        final JSONArray metaDataForTableData = new JSONArray();
        JSONObject dataForTable = new JSONObject();
        SelectQuery tableSelectQuery = null;
        final String query = null;
        Boolean isTableAvailable = false;
        if (tableID == null) {
            return this.getErrorJSON("15000", "TableID is not present in the request");
        }
        final Long moduleID = getModuleIDForTableID(Long.valueOf(tableID));
        if (moduleID == null) {
            return this.getErrorJSON("15001", "tableid is invalid");
        }
        final Boolean isUserModule = ReportBIUtil.moduleApplicableForUser(moduleID, userID);
        if (isUserModule) {
            final Long loginID = APIUtil.getLoginID(apiRequest);
            try {
                final Boolean isUserAdmin = DMUserHandler.isUserInRole(loginID, "Common_Write");
                final ArrayList customersInfo = CustomerInfoUtil.getInstance().getCustomersForUser(userID);
                final Long[] customerIDS = getCustomerIDSfromCustomersInfo(customersInfo);
                tableSelectQuery = ReportBIUtil.getSelectQueryForModuleTable(Long.valueOf(tableID), isUserAdmin, customerIDS, userID, modifiedTime, Integer.valueOf(Integer.parseInt(offset)), apiVersion, Integer.valueOf(rowCountNumber + 1));
                if (tableSelectQuery != null) {
                    dataForTable = getJSONDataFromSelectQuery(Long.valueOf(tableID), tableSelectQuery, metaDataForTableData, rowCountNumber);
                    isTableAvailable = true;
                }
                if (!isTableAvailable) {
                    return this.getErrorJSON("15003", "Table is not available in this version");
                }
            }
            catch (final Exception e) {
                SyncAPIRequestHandler.out.log(Level.WARNING, " Exception while getting Table Data for Table ID", e);
            }
            return dataForTable;
        }
        return this.getErrorJSON("15002", "User does not have privileges over this table");
    }
    
    private JSONObject getErrorJSON(final String errorCode, final String errorMsg) throws JSONException {
        final JSONObject errorResponse = new JSONObject();
        errorResponse.put("error_description", (Object)errorMsg);
        errorResponse.put("error_code", (Object)errorCode);
        errorResponse.put("status", (Object)"error");
        errorResponse.put("message_type", (Object)"sync");
        return errorResponse;
    }
    
    private static Long getModuleIDForTableID(final Long tableID) {
        Long moduleID = null;
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBIQuery", "TABLE_ID"), (Object)tableID, 0);
            final DataObject reportTableIDDO = ReportBIUtil.getReportBITableDetails("ReportBIQuery", tableIDCriteria);
            if (reportTableIDDO != null && !reportTableIDDO.isEmpty()) {
                final Row reportTableRow = reportTableIDDO.getFirstRow("ReportBIQuery");
                moduleID = (Long)reportTableRow.get("MODULE_ID");
            }
        }
        catch (final Exception e) {
            SyncAPIRequestHandler.out.log(Level.WARNING, "Exception while fetching module ID for Table ID", e);
        }
        return moduleID;
    }
    
    private static Long[] getCustomerIDSfromCustomersInfo(final ArrayList customersInfo) {
        final Long[] customerIDS = new Long[customersInfo.size()];
        try {
            for (int i = 0; i < customersInfo.size(); ++i) {
                final HashMap customerDetails = customersInfo.get(i);
                final Long customerID = customerDetails.get("CUSTOMER_ID");
                customerIDS[i] = customerID;
            }
        }
        catch (final Exception e) {
            SyncAPIRequestHandler.out.log(Level.WARNING, " Exception while converting CustomersInfo to Array", e);
        }
        return customerIDS;
    }
    
    private static JSONArray getMetaDataForSelectQueryModuleTable(final SelectQuery tableSelect) {
        final JSONArray metaDataJSON = new JSONArray();
        try {
            if (tableSelect != null) {
                final List listOfColumns = tableSelect.getSelectColumns();
                for (int i = 0; i < listOfColumns.size(); ++i) {
                    final JSONObject columnDetails = new JSONObject();
                    final Column singleColumn = listOfColumns.get(i);
                    final String columnName = singleColumn.getColumnAlias();
                    final String columnDataType = singleColumn.getDataType();
                    columnDetails.put("COLUMN_NAME", (Object)columnName);
                    columnDetails.put("COLUMN_DATA_TYPE", (Object)columnDataType);
                    metaDataJSON.put((Object)columnDetails);
                }
            }
        }
        catch (final Exception e) {
            SyncAPIRequestHandler.out.log(Level.WARNING, " Exception while constructing meta data For Select Query", e);
        }
        return metaDataJSON;
    }
    
    private static JSONObject getJSONDataFromSelectQuery(final Long tableID, final SelectQuery tableSelect, JSONArray metaData, final Integer rowCountNumber) {
        final long start = System.currentTimeMillis();
        SyncAPIRequestHandler.out.log(Level.INFO, "Get JSON Data From SelectQuery Started at {0}", DurationFormatUtils.formatDurationHMS(start));
        final JSONObject tableDataWithMeta = new JSONObject();
        final JSONArray tableData = new JSONArray();
        int count = 0;
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)tableSelect);
            long end = System.currentTimeMillis();
            SyncAPIRequestHandler.out.log(Level.INFO, "Get JSON Data From SelectQuery Execution of main query takes time {0}", DurationFormatUtils.formatDurationHMS(end - start));
            tableDataWithMeta.put("nextSyncRowsAvailable", false);
            final Row tableRow = DBUtil.getRowFromDB("ReportBIQuery", "TABLE_ID", (Object)tableID);
            metaData = getMetaDataForSelectQueryModuleTable(tableSelect);
            long end2 = System.currentTimeMillis();
            SyncAPIRequestHandler.out.log(Level.INFO, "Get JSON Data From SelectQuery Execution of sub query 1 takes time {0}", DurationFormatUtils.formatDurationHMS(end2 - end));
            end = end2;
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBITableColumn", "TABLE_ID"), (Object)tableID, 0);
            final Criteria typeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)new long[] { 4L, 5L, 6L }, 8);
            final Criteria tableColumnCriteria = tableIDCriteria.and(typeCriteria);
            final Join tableColumnDetailsJoin = new Join("ReportBITableColumn", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            final DataObject tableColumnDO = ReportBIUtil.getReportBITableColumnID(tableColumnCriteria, tableColumnDetailsJoin);
            end2 = System.currentTimeMillis();
            SyncAPIRequestHandler.out.log(Level.INFO, "Get JSON Data From SelectQuery Execution of sub query 2 takes time {0}", DurationFormatUtils.formatDurationHMS(end2 - end));
            end = end2;
            final HashMap dataValueMap = new HashMap();
            while (dataSet.next()) {
                final JSONObject tableRowData = new JSONObject();
                for (int i = 0; i < metaData.length(); ++i) {
                    final JSONObject columnObject = (JSONObject)metaData.get(i);
                    String columnName = (String)columnObject.get("COLUMN_NAME");
                    final String columnDataType = (String)(columnObject.has("COLUMN_DATA_TYPE") ? columnObject.get("COLUMN_DATA_TYPE") : "");
                    Object dataValue = dataSet.getValue(columnName);
                    if ((dataValue = getTransformedDataValue(tableColumnDO, columnName, dataValue, dataValueMap)) == null || dataValue.equals("--NA--") || dataValue.equals("--") || dataValue.equals("-")) {
                        dataValue = "";
                    }
                    if (columnDataType.equalsIgnoreCase("Boolean")) {
                        final String dataValueStr = String.valueOf(dataValue);
                        if (Boolean.valueOf(dataValueStr)) {
                            dataValue = "Yes";
                        }
                        else {
                            dataValue = "No";
                        }
                    }
                    if (tableRow != null) {
                        final Boolean hasDistinct = (Boolean)tableRow.get("SET_DISTINCT");
                        if (hasDistinct) {
                            columnName = columnName.replace('_', ' ');
                        }
                    }
                    tableRowData.put(columnName, dataValue);
                }
                ++count;
                tableData.put((Object)tableRowData);
                if (count == rowCountNumber) {
                    tableDataWithMeta.put("nextSyncRowsAvailable", true);
                    break;
                }
            }
            end2 = System.currentTimeMillis();
            SyncAPIRequestHandler.out.log(Level.INFO, "Get JSON Data From SelectQuery data iteration takes time {0}", DurationFormatUtils.formatDurationHMS(end2 - end));
            tableDataWithMeta.put("dataCount", count);
            tableDataWithMeta.put("metaData", (Object)metaData);
            tableDataWithMeta.put("data", (Object)tableData);
            end2 = System.currentTimeMillis();
            SyncAPIRequestHandler.out.log(Level.INFO, "Get JSON Data From SelectQuery total time taken {0}", DurationFormatUtils.formatDurationHMS(end2 - start));
        }
        catch (final Exception e) {
            SyncAPIRequestHandler.out.log(Level.WARNING, " Exception while constructing JSON data For Select Query", e);
        }
        return tableDataWithMeta;
    }
    
    public static Object getTransformedDataValue(final DataObject tableColumnDO, final String columnName, final Object dataValue, final HashMap dataValueMap) {
        LinkedHashMap<Object, String> transformValue = new LinkedHashMap<Object, String>();
        try {
            if (dataValueMap.containsKey(columnName)) {
                transformValue = dataValueMap.get(columnName);
                if (transformValue != null && !transformValue.isEmpty()) {
                    return transformValue.get(String.valueOf(dataValue));
                }
            }
            final Criteria columnNameCriteria = new Criteria(new Column("CRColumns", "COLUMN_NAME_ALIAS"), (Object)columnName, 0);
            Long columnType = null;
            final Row columnTypeRow = tableColumnDO.getRow("ReportBITableColumn", columnNameCriteria);
            if (columnTypeRow != null) {
                columnType = (Long)columnTypeRow.get("TYPE");
            }
            final Row columnNameRow = tableColumnDO.getRow("CRColumns", columnNameCriteria);
            if (columnNameRow != null && columnType != null) {
                final Long columnID = (Long)columnNameRow.get("COLUMN_ID");
                if (columnType.equals(4L)) {
                    transformValue = CriteriaColumnValueUtil.getInstance().getTranformValueList(columnID, (List)null);
                    dataValueMap.put(columnName, transformValue);
                    if (transformValue != null && !transformValue.isEmpty()) {
                        return transformValue.get(String.valueOf(dataValue));
                    }
                }
                else {
                    if (columnType.equals(5L)) {
                        return I18N.getMsg(String.valueOf(dataValue), new Object[0]);
                    }
                    if (columnType.equals(6L)) {
                        final String className = ReportCriteriaUtil.getInstance().hasSpecialHandlerClass(columnID);
                        if (className != null) {
                            final ReportBIDataValueTransformer reportBIDataValueTransformer = (ReportBIDataValueTransformer)Class.forName(className).newInstance();
                            return reportBIDataValueTransformer.transformValue(columnName, dataValue);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            SyncAPIRequestHandler.out.log(Level.WARNING, " Exception while getting Modified time applicable for Table ID", e);
        }
        return dataValue;
    }
    
    static {
        SyncAPIRequestHandler.out = Logger.getLogger(SyncAPIRequestHandler.class.getName());
    }
}
