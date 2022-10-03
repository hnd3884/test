package com.me.mdm.api.core.misc;

import org.json.JSONException;
import java.text.DateFormat;
import com.adventnet.i18n.I18N;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONArray;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.io.OutputStream;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import com.adventnet.ds.query.util.QueryUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class CSVExportsAPIRequestHandler extends ApiRequestHandler
{
    public static final String DEVICE_EXPORT = "device_info";
    public static final String LOCATION_EXPORT = "location_data";
    Logger logger;
    private static HashMap<String, List<String>> dateColMap;
    private static HashMap<String, List<String>> i18NColMap;
    private static final int NORMAL_COL_TYPE = 1;
    private static final int I18N_COL_TYPE = 2;
    private static final int DATE_COL_TYPE = 3;
    private static final int DOUBLE_COL_TYPE = 4;
    private static final int INTEGER_COL_TYPE = 5;
    
    public CSVExportsAPIRequestHandler() {
        this.logger = Logger.getLogger(CSVExportsAPIRequestHandler.class.getSimpleName());
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final String exportName = APIUtil.getStringFilter(requestJSON, "export_name");
            if (exportName != null && exportName.length() != 0) {
                final String uvhString = this.getUVHString(exportName);
                if (uvhString == null) {
                    throw new APIHTTPException("COM0008", new Object[] { "export_name - " + exportName });
                }
                final SelectQuery selectQuery = QueryUtil.getSelectQuery((long)DBUtil.getUVHValue(uvhString));
                this.setCriteriaAndRange(selectQuery, exportName, requestJSON);
                final String data = this.getCsvStringFromSelectQuery(selectQuery, exportName);
                final String fileName = exportName + ".csv";
                apiRequest.httpServletResponse.setContentType("text/csv");
                apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                final OutputStream os = (OutputStream)apiRequest.httpServletResponse.getOutputStream();
                try {
                    os.write(data.getBytes());
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "exception in CSVExportsAPIRequestHandler", e);
                    try {
                        os.flush();
                    }
                    catch (final IOException e2) {
                        this.logger.log(Level.SEVERE, "exception while flushing outputStream", e2);
                    }
                    try {
                        os.close();
                    }
                    catch (final IOException e2) {
                        this.logger.log(Level.SEVERE, "exception while flushing outputStream", e2);
                    }
                }
                finally {
                    try {
                        os.flush();
                    }
                    catch (final IOException e3) {
                        this.logger.log(Level.SEVERE, "exception while flushing outputStream", e3);
                    }
                    try {
                        os.close();
                    }
                    catch (final IOException e3) {
                        this.logger.log(Level.SEVERE, "exception while flushing outputStream", e3);
                    }
                }
            }
        }
        catch (final Exception e4) {
            if (e4 instanceof APIHTTPException) {
                throw (APIHTTPException)e4;
            }
            this.logger.log(Level.SEVERE, "exception in CSVExportsAPIRequestHandler", e4);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    private String getUVHString(final String exportName) {
        switch (exportName) {
            case "device_info": {
                return "SelectQuery:queryid:DeviceInfo";
            }
            case "location_data": {
                return "SelectQuery:queryid:LocationDetailsQuery";
            }
            default: {
                return null;
            }
        }
    }
    
    private void setCriteriaAndRange(final SelectQuery selectQuery, final String exportName, final JSONObject apiRequest) {
        final Long timestamp = APIUtil.getLongFilter(apiRequest, "last_sync_time");
        switch (exportName) {
            case "device_info": {
                if (timestamp != null && timestamp != -1L) {
                    Criteria criteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "LAST_MODIFIED_TIME"), (Object)timestamp, 5);
                    criteria = criteria.or(new Criteria(Column.getColumn("ManagedDevice", "ADDED_TIME"), (Object)timestamp, 5));
                    criteria = criteria.or(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)timestamp, 5));
                    criteria = criteria.or(new Criteria(Column.getColumn("UserResource", "DB_UPDATED_TIME"), (Object)timestamp, 5));
                    selectQuery.setCriteria(criteria);
                    break;
                }
                break;
            }
            case "location_data": {
                if (timestamp != null && timestamp != -1L) {
                    final Criteria criteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), (Object)timestamp, 5);
                    selectQuery.setCriteria(criteria);
                    break;
                }
                break;
            }
        }
        final Integer page = APIUtil.getIntegerFilter(apiRequest, "page");
        final Integer per_page = APIUtil.getIntegerFilter(apiRequest, "per_page");
        if (page != null && per_page != null && page != -1 && per_page != -1) {
            final SortColumn sortColumn = new SortColumn("ManagedDevice", "RESOURCE_ID", true);
            selectQuery.addSortColumn(sortColumn);
            final int start = per_page * (page - 1) + 1;
            selectQuery.setRange(new Range(start, (int)per_page));
        }
    }
    
    private String getCsvStringFromSelectQuery(final SelectQuery selectQuery, final String exportName) {
        final JSONArray array = new JSONArray();
        final List<ExportColumn> reportColumns = this.convertToExportColumns(selectQuery.getSelectColumns(), exportName);
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final JSONObject object = new JSONObject();
                for (final ExportColumn column : reportColumns) {
                    if (dataSet.getValue(column.name) != null && !MDMUtil.getInstance().isEmpty(dataSet.getValue(column.name).toString())) {
                        object.put(column.name, dataSet.getValue(column.name));
                    }
                }
                array.put((Object)object);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error in generating CSV string", e);
        }
        String result = null;
        try {
            result = this.convertJSONArrayToCSV(array, reportColumns);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while converting JSON to CSV....", e2);
        }
        return result;
    }
    
    private List<ExportColumn> convertToExportColumns(final List<Column> columns, final String exportName) {
        final List<ExportColumn> reportColumns = new ArrayList<ExportColumn>();
        final List<String> dateCols = this.getDateColumnNames(exportName);
        final List<String> i18nCols = this.getI18NColumnNames(exportName);
        for (final Column column : columns) {
            final ExportColumn reportColumn = new ExportColumn();
            reportColumn.name = column.getColumnAlias();
            if (dateCols != null && dateCols.contains(reportColumn.name)) {
                reportColumn.columnType = 3;
            }
            else if (i18nCols != null && i18nCols.contains(reportColumn.name)) {
                reportColumn.columnType = 2;
            }
            else if (column.getDataType() != null && column.getDataType().equals("INTEGER")) {
                reportColumn.columnType = 5;
            }
            else if (column.getDataType() != null && column.getDataType().equals("FLOAT")) {
                reportColumn.columnType = 4;
            }
            else {
                reportColumn.columnType = 1;
            }
            reportColumns.add(reportColumn);
        }
        return reportColumns;
    }
    
    private String convertJSONArrayToCSV(final JSONArray array, final List<ExportColumn> columns) throws JSONException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final ExportColumn column : columns) {
            stringBuilder.append(column.name);
            stringBuilder.append(",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append("\n");
        for (int i = 0; i < array.length(); ++i) {
            final JSONObject object = array.getJSONObject(i);
            for (final ExportColumn column2 : columns) {
                if (object.has(column2.name)) {
                    Object value = object.get(column2.name);
                    switch (column2.columnType) {
                        case 3: {
                            final Date date = new Date();
                            date.setTime(Long.valueOf(String.valueOf(value)));
                            final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            value = dateFormat.format(date);
                            break;
                        }
                        case 2: {
                            try {
                                value = I18N.getMsg(String.valueOf(value), new Object[0]);
                                value = "\"" + value + "\"";
                            }
                            catch (final Exception e) {
                                this.logger.log(Level.SEVERE, "Error while converting I18N key", e);
                            }
                        }
                        case 1: {
                            value = "\"" + value + "\"";
                            break;
                        }
                    }
                    stringBuilder.append(value);
                }
                stringBuilder.append(",");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    
    private List<String> getDateColumnNames(final String exportName) {
        return CSVExportsAPIRequestHandler.dateColMap.get(exportName);
    }
    
    private List<String> getI18NColumnNames(final String exportName) {
        return CSVExportsAPIRequestHandler.i18NColMap.get(exportName);
    }
    
    static {
        CSVExportsAPIRequestHandler.dateColMap = new HashMap<String, List<String>>();
        final List<String> deviceInfoDateCols = new ArrayList<String>();
        deviceInfoDateCols.add("DEVICE_DB_ADDED_TIME");
        deviceInfoDateCols.add("DEVICE_REGISTERED_TIME");
        deviceInfoDateCols.add("LAST_CLOUD_BACKUP_DATE");
        deviceInfoDateCols.add("DEVICE_LAST_SUCCESSFUL_SCAN");
        deviceInfoDateCols.add("DEVICE_SCAN_START_TIME");
        deviceInfoDateCols.add("DEVICE_SCAN_END_TIME");
        deviceInfoDateCols.add("DEVICE_LAST_CONTACT_TIME");
        deviceInfoDateCols.add("DEVICE_LOCATION_SYNC_TIME");
        deviceInfoDateCols.add("DEVICE_LOCATION_LOCATED_TIME");
        CSVExportsAPIRequestHandler.dateColMap.put("device_info", deviceInfoDateCols);
        final List<String> locationInfoDateCols = new ArrayList<String>();
        locationInfoDateCols.add("LOCATION_SYNC_TIME");
        locationInfoDateCols.add("LOCATION_LOCATED_TIME");
        CSVExportsAPIRequestHandler.dateColMap.put("location_data", locationInfoDateCols);
        CSVExportsAPIRequestHandler.i18NColMap = new HashMap<String, List<String>>();
        final List<String> deviceInfoI18NCols = new ArrayList<String>();
        deviceInfoI18NCols.add("DEVICE_SCAN_REMARKS");
        CSVExportsAPIRequestHandler.i18NColMap.put("device_info", deviceInfoI18NCols);
        final List<String> locationInfoI18NCols = new ArrayList<String>();
        CSVExportsAPIRequestHandler.i18NColMap.put("location_data", locationInfoI18NCols);
    }
    
    class ExportColumn
    {
        public String name;
        public int columnType;
    }
}
