package com.me.devicemanagement.framework.server.csv;

import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import javax.transaction.TransactionManager;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import au.com.bytecode.opencsv.CSVReader;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

class CSVParser
{
    private static Logger logger;
    public static final String CUSTOMER_PARAM_NAME = "CUSTOMER_PARAM_NAME";
    public static final String LENGTH = "LENGTH";
    private static final String INDEX = "INDEX";
    public static final String DATA_TYPE = "DATA_TYPE";
    private String tableName;
    private JSONObject tableDetails;
    private CSVReader csvReader;
    private String totalRecordsLabel;
    private String processedRecordsLabel;
    private String failedRecordsLabel;
    private long customerID;
    private Boolean headerProcessed;
    private Boolean csvImported;
    private String statusLabel;
    private JSONObject filterJSON;
    
    public CSVParser(final String tableName, final JSONObject tableDetails, final InputStream inputStream, final String operationLabel, final long customerID) throws Exception {
        this.csvReader = null;
        this.headerProcessed = false;
        this.csvImported = false;
        this.filterJSON = null;
        this.tableName = tableName;
        this.tableDetails = tableDetails;
        final InputStreamReader is = new InputStreamReader(inputStream, "UTF-8");
        final BufferedReader br = new BufferedReader(is);
        this.csvReader = new CSVReader((Reader)br);
        this.customerID = customerID;
        this.initialize(operationLabel);
    }
    
    public void initialize(final String operationLabel) throws Exception {
        if (operationLabel != null) {
            this.statusLabel = operationLabel + "_" + "CSVStatus";
            this.totalRecordsLabel = operationLabel + "_" + "CSVTotalCount";
            this.processedRecordsLabel = operationLabel + "_" + "CSVCompletedCount";
            this.failedRecordsLabel = operationLabel + "_" + "CSVFailureCount";
            return;
        }
        throw new Exception("Operation Label cannot be null");
    }
    
    public void setFilterJSON(final JSONObject c) {
        this.filterJSON = c;
    }
    
    public void clearTempTable() throws Exception {
        try {
            Criteria criteria = new Criteria(Column.getColumn(this.tableName, "CUSTOMER_ID"), (Object)this.customerID, 0);
            if (this.filterJSON != null) {
                final Set<Map.Entry<String, Object>> entrySet = this.filterJSON.entrySet();
                Iterator<Map.Entry<String, Object>> i = entrySet.iterator();
                i = entrySet.iterator();
                while (i.hasNext()) {
                    final Map.Entry<String, Object> element = i.next();
                    final Criteria filterCriteria = new Criteria(Column.getColumn(this.tableName, (String)element.getKey()), element.getValue(), 0);
                    criteria = criteria.and(filterCriteria);
                }
            }
            SyMUtil.getPersistence().delete(criteria);
            CSVParser.logger.log(Level.INFO, "Cleared temp table {0}", this.tableName);
            final JSONObject initJson = new JSONObject();
            initJson.put((Object)this.totalRecordsLabel, (Object)"0");
            initJson.put((Object)this.processedRecordsLabel, (Object)"0");
            initJson.put((Object)this.failedRecordsLabel, (Object)"0");
            initJson.put((Object)this.statusLabel, (Object)"NO_HISTORY");
            this.persistStateParams(initJson);
            CSVParser.logger.log(Level.INFO, "Updated {0}, {1} & {2} entries in Customer params", new Object[] { this.totalRecordsLabel, this.processedRecordsLabel, this.failedRecordsLabel });
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public int copyCSVDataToTempTable() throws Exception {
        if (this.csvImported) {
            CSVParser.logger.info("CSV File has already been imported using this object");
            throw new Exception("Can call this method only once for an instance of this class.");
        }
        if (!this.headerProcessed) {
            CSVParser.logger.info("Header record has not been processed before");
            this.getCSVHeaders();
            return this.copyCSVDataToTempTable();
        }
        this.clearTempTable();
        final String[] firstRow = null;
        int rowIndex = 0;
        String[] csvRowDetails;
        if ((csvRowDetails = this.readLine()) == null) {
            CSVParser.logger.info("CSV has no records");
            throw new SyMException(13002, "dc.mdm.device_mgmt.error_no_records", null);
        }
        final TransactionManager txMgr = SyMUtil.getUserTransaction();
        try {
            txMgr.begin();
            DataObject dobj = (DataObject)new WritableDataObject();
            do {
                final Row r = this.createRow(csvRowDetails);
                if (r != null) {
                    dobj.addRow(r);
                    if (++rowIndex % 50 != 0) {
                        continue;
                    }
                    SyMUtil.getPersistence().add(dobj);
                    dobj = (DataObject)new WritableDataObject();
                }
            } while ((csvRowDetails = this.readLine()) != null);
            if (rowIndex % 50 != 0) {
                SyMUtil.getPersistence().add(dobj);
            }
            CSVParser.logger.log(Level.INFO, "Copied data to temp table {0}", this.tableName);
            final JSONObject initJson = new JSONObject();
            initJson.put((Object)this.totalRecordsLabel, (Object)String.valueOf(rowIndex));
            initJson.put((Object)this.processedRecordsLabel, (Object)"0");
            initJson.put((Object)this.failedRecordsLabel, (Object)"0");
            initJson.put((Object)this.statusLabel, (Object)"IN_PROGRESS");
            this.persistStateParams(initJson);
            CSVParser.logger.log(Level.INFO, "Updated {0}, {1} & {2} entries in Customer params", new Object[] { this.totalRecordsLabel, this.processedRecordsLabel, this.failedRecordsLabel });
        }
        catch (final Exception e) {
            CSVParser.logger.log(Level.INFO, "Exception occurred..{0}\nRolling back...", e);
            txMgr.rollback();
            throw e;
        }
        txMgr.commit();
        return rowIndex;
    }
    
    public List<String> getCSVHeaders() throws Exception {
        try {
            if (this.headerProcessed) {
                CSVParser.logger.info("CSV header has already been processed for an instance of this class");
                throw new Exception("Can call this method only once for an instance of this class.");
            }
            final String[] firstRow;
            if ((firstRow = this.readLine()) != null) {
                final List<String> columnsInCSV = new ArrayList<String>();
                final JSONObject params = new JSONObject();
                this.initializeStateParams(params);
                for (int length = firstRow.length, i = 0; i < length; ++i) {
                    final String csvColumnName = firstRow[i].trim();
                    if (this.setColumnIndex(csvColumnName, i, params)) {
                        columnsInCSV.add(csvColumnName);
                    }
                }
                this.persistStateParams(params);
                this.headerProcessed = true;
                CSVParser.logger.log(Level.INFO, "Processed CSV Header..Columns in CSV : {0}", columnsInCSV);
                return columnsInCSV;
            }
            CSVParser.logger.info("CSV header itself is not present");
            throw new SyMException(13001, "dc.mdm.device_mgmt.error_empty_csv", null);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private Row createRow(final String[] row) throws Exception {
        try {
            final Row r = new Row(this.tableName);
            final Set<Map.Entry<String, JSONObject>> entrySet = this.tableDetails.entrySet();
            Iterator<Map.Entry<String, JSONObject>> i = entrySet.iterator();
            i = entrySet.iterator();
            int notNullColumnCount = 0;
            while (i.hasNext()) {
                final Map.Entry<String, JSONObject> element = i.next();
                final String columnName = element.getKey();
                final JSONObject columnDetails = element.getValue();
                final Integer columnIndex = (Integer)columnDetails.get((Object)"INDEX");
                final Integer columnLength = (Integer)columnDetails.get((Object)"LENGTH");
                if (columnIndex != null) {
                    this.setColumnValue(r, columnName, row[columnIndex], columnLength);
                    if (r.get(columnName) == null) {
                        continue;
                    }
                    ++notNullColumnCount;
                }
            }
            if (this.filterJSON != null) {
                final Set<Map.Entry<String, Object>> filterSet = this.filterJSON.entrySet();
                for (final Map.Entry<String, Object> filterElement : filterSet) {
                    r.set((String)filterElement.getKey(), filterElement.getValue());
                }
            }
            r.set("CUSTOMER_ID", (Object)this.customerID);
            if (notNullColumnCount > 0) {
                return r;
            }
            CSVParser.logger.fine("Empty Row");
            return null;
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void setColumnValue(final Row row, final String key, String value, final Integer length) throws Exception {
        try {
            if (value != null) {
                value = value.trim();
                if (value.equals("") || value.equals("--")) {
                    value = null;
                }
                else if (length != null && value.length() > length) {
                    value = value.substring(0, length);
                }
            }
            row.set(key, (Object)value);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private void initializeStateParams(final JSONObject params) throws Exception {
        try {
            final Set<Map.Entry<String, JSONObject>> entrySet = this.tableDetails.entrySet();
            for (final Map.Entry<String, JSONObject> element : entrySet) {
                final JSONObject columnDetails = element.getValue();
                final String columnCustomerParamName = (String)columnDetails.get((Object)"CUSTOMER_PARAM_NAME");
                params.put((Object)columnCustomerParamName, (Object)"false");
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private boolean setColumnIndex(final String csvColumnName, final int index, final JSONObject params) throws Exception {
        try {
            final Set<Map.Entry<String, JSONObject>> entrySet = this.tableDetails.entrySet();
            for (final Map.Entry<String, JSONObject> element : entrySet) {
                final JSONObject columnDetails = element.getValue();
                final String columnCustomerParamName = (String)columnDetails.get((Object)"CUSTOMER_PARAM_NAME");
                final String tableColumnName = element.getKey();
                if (csvColumnName.equalsIgnoreCase(tableColumnName) || csvColumnName.replace("_", "").replace(" ", "").toLowerCase().equalsIgnoreCase(tableColumnName.replace("_", "").toLowerCase())) {
                    columnDetails.put((Object)"INDEX", (Object)index);
                    params.put((Object)columnCustomerParamName, (Object)"true");
                    return true;
                }
            }
            return false;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private void persistStateParams(final JSONObject params) throws Exception {
        try {
            CustomerParamsHandler.getInstance().addOrUpdateParameters(params, this.customerID);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private String[] readLine() throws Exception {
        try {
            final String[] line = this.csvReader.readNext();
            if (line != null && line.length == 1 && line[0].equalsIgnoreCase("")) {
                throw new SyMException(13006, "dc.csv.invalid_csv_file_content", null);
            }
            return line;
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new SyMException(13006, "dc.csv.invalid_csv_file_content", ex);
        }
    }
    
    static {
        CSVParser.logger = Logger.getLogger("MDMLogger");
    }
}
