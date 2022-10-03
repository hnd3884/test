package com.me.devicemanagement.framework.server.csv;

import java.util.Hashtable;
import java.util.Set;
import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import java.util.Collection;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.io.InputStream;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public abstract class CSVProcessor
{
    private CSVParser csvParser;
    private Long operationId;
    private String tableName;
    private String taskName;
    private String taskClass;
    private static Logger logger;
    
    protected abstract String getOperationLabel();
    
    protected abstract JSONObject generateTableDetails() throws Exception;
    
    public CSVProcessor() {
        try {
            final Row r = DBUtil.getRowFromDB("CSVOperation", "LABEL", this.getOperationLabel());
            this.operationId = (Long)r.get("OPERATION_ID");
            this.tableName = (String)r.get("CSV_TABLE_NAME");
            this.taskName = (String)r.get("LABEL");
            this.taskClass = (String)r.get("CSV_TASK_CLASS");
        }
        catch (final Exception e) {
            CSVProcessor.logger.log(Level.INFO, "Exception while initializing object of class..{0}", e);
        }
    }
    
    public CSVProcessor(final String operationLabel) {
        try {
            final Row r = DBUtil.getRowFromDB("CSVOperation", "LABEL", operationLabel);
            this.operationId = (Long)r.get("OPERATION_ID");
            this.tableName = (String)r.get("CSV_TABLE_NAME");
            this.taskName = (String)r.get("LABEL");
            this.taskClass = (String)r.get("CSV_TASK_CLASS");
        }
        catch (final Exception e) {
            CSVProcessor.logger.log(Level.INFO, "Exception while initializing object of class..{0}", e);
        }
    }
    
    public final JSONObject persistCSVFile(final InputStream inputStream, final JSONObject input, final JSONObject filterJSON, final Long customerID) {
        try {
            this.persistFile(inputStream, filterJSON, customerID);
            this.invokeAsynchronousCSVTask(input, customerID);
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)"STATUS", (Object)"SUCCESS");
            CSVProcessor.logger.info("Successfully imported all records..");
            return jsonObj;
        }
        catch (final SyMException ex) {
            final JSONObject jsonObj2 = new JSONObject();
            jsonObj2.put((Object)"STATUS", (Object)"FAILURE");
            jsonObj2.put((Object)"CODE", (Object)ex.getErrorCode());
            jsonObj2.put((Object)"CAUSE", (Object)ex.getMessage());
            CSVProcessor.logger.log(Level.INFO, "Failure to import records..{0}", ex);
            return jsonObj2;
        }
        catch (final Exception ex2) {
            final JSONObject jsonObj2 = new JSONObject();
            jsonObj2.put((Object)"STATUS", (Object)"FAILURE");
            jsonObj2.put((Object)"CAUSE", (Object)ex2.getMessage());
            CSVProcessor.logger.log(Level.INFO, "Failure to import records..{0}", ex2);
            return jsonObj2;
        }
    }
    
    public final JSONObject persistCSVFile(final InputStream inputStream, final JSONObject input, final Long customerID) {
        try {
            this.persistFile(inputStream, null, customerID);
            this.invokeAsynchronousCSVTask(input, customerID);
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)"STATUS", (Object)"SUCCESS");
            CSVProcessor.logger.info("Successfully imported all records..");
            return jsonObj;
        }
        catch (final SyMException ex) {
            final JSONObject jsonObj2 = new JSONObject();
            jsonObj2.put((Object)"STATUS", (Object)"FAILURE");
            jsonObj2.put((Object)"CODE", (Object)ex.getErrorCode());
            jsonObj2.put((Object)"CAUSE", (Object)ex.getMessage());
            CSVProcessor.logger.log(Level.INFO, "Failure to import records..{0}", ex);
            return jsonObj2;
        }
        catch (final Exception ex2) {
            final JSONObject jsonObj2 = new JSONObject();
            jsonObj2.put((Object)"STATUS", (Object)"FAILURE");
            jsonObj2.put((Object)"CAUSE", (Object)ex2.getMessage());
            CSVProcessor.logger.log(Level.INFO, "Failure to import records..{0}", ex2);
            return jsonObj2;
        }
    }
    
    protected void persistFile(final InputStream inputStream, final JSONObject filterJSON, final long customerID) throws Exception {
        try {
            this.initialize(inputStream, customerID);
            this.setFilterJSON(filterJSON);
            CSVProcessor.logger.log(Level.INFO, "Generated Table Details");
            this.validateCSVHeader(this.getCSVHeader());
            final int rowCount = this.copyCSVData();
            this.validateRowCount(rowCount);
        }
        catch (final Exception ex) {
            CSVProcessor.logger.log(Level.INFO, "Exception while copying data to temp table .. {0}", ex);
            this.clearTempTable();
            throw ex;
        }
    }
    
    private void initialize(final InputStream inputStream, final long customerID) throws Exception {
        this.csvParser = new CSVParser(this.tableName, this.generateTableDetails(), inputStream, this.getOperationLabel(), customerID);
    }
    
    protected void validateCSVHeader(final List<String> columnsInCSV) throws Exception {
        try {
            final List<String> mandatoryHeaders = this.listMandatoryHeaders();
            if (!mandatoryHeaders.isEmpty()) {
                mandatoryHeaders.removeAll(columnsInCSV);
                String columnsNotInCSV = "";
                final Iterator<String> iter = mandatoryHeaders.iterator();
                while (iter.hasNext()) {
                    if (columnsNotInCSV.equals("")) {
                        columnsNotInCSV = iter.next();
                    }
                    else {
                        columnsNotInCSV = columnsNotInCSV + ", " + iter.next();
                    }
                }
                if (!columnsNotInCSV.equals("")) {
                    CSVProcessor.logger.log(Level.INFO, "Mandatory headers are not present. Columns Not Present: {0}", columnsNotInCSV);
                    throw new SyMException(13005, columnsNotInCSV + " " + I18N.getMsg("dc.mdm.enroll.mandatory_columns_missing", new Object[0]), null);
                }
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private List<String> getCSVHeader() throws Exception {
        return this.csvParser.getCSVHeaders();
    }
    
    protected List<String> listMandatoryHeaders() throws Exception {
        return new ArrayList<String>();
    }
    
    private int copyCSVData() throws Exception {
        final int rowCount = this.csvParser.copyCSVDataToTempTable();
        CSVProcessor.logger.log(Level.INFO, "Finished copying csv data to temp table. Total no. of rows: {0}", rowCount);
        return rowCount;
    }
    
    protected void validateRowCount(final int rowCount) throws Exception {
    }
    
    protected final JSONObject generateColumnDetailsJSON(final Integer length, final String customerParamName) throws Exception {
        try {
            final JSONObject singleColumnDetails = new JSONObject();
            singleColumnDetails.put((Object)"LENGTH", (Object)length);
            singleColumnDetails.put((Object)"CUSTOMER_PARAM_NAME", (Object)customerParamName);
            return singleColumnDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected final JSONObject generateColumnDetailsJSON(final Integer length, final String type, final String customerParamName) throws Exception {
        try {
            final JSONObject singleColumnDetails = new JSONObject();
            singleColumnDetails.put((Object)"LENGTH", (Object)length);
            singleColumnDetails.put((Object)"CUSTOMER_PARAM_NAME", (Object)customerParamName);
            singleColumnDetails.put((Object)"DATA_TYPE", (Object)type);
            return singleColumnDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private void clearTempTable() throws Exception {
        this.csvParser.clearTempTable();
    }
    
    public static final String getTotalLabel(final String label) {
        return label + "_" + "CSVTotalCount";
    }
    
    public static final String getProcessedLabel(final String label) {
        return label + "_" + "CSVCompletedCount";
    }
    
    public static final String getFailedLabel(final String label) {
        return label + "_" + "CSVFailureCount";
    }
    
    public static final String getStatusLabel(final String label) {
        return label + "_" + "CSVStatus";
    }
    
    private void invokeAsynchronousCSVTask(final JSONObject input, final Long customerID) throws Exception {
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", this.taskName);
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        final Properties taskProps = new Properties();
        ((Hashtable<String, String>)taskProps).put("customerID", String.valueOf(customerID));
        ((Hashtable<String, String>)taskProps).put("userID", String.valueOf(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID()));
        ((Hashtable<String, String>)taskProps).put("operationID", String.valueOf(this.operationId));
        if (input != null) {
            final Set<Map.Entry> entrySet = input.entrySet();
            Iterator<Map.Entry> i = (Iterator<Map.Entry>)entrySet.iterator();
            i = (Iterator<Map.Entry>)entrySet.iterator();
            while (i.hasNext()) {
                final Map.Entry element = i.next();
                ((Hashtable<Object, String>)taskProps).put(element.getKey(), String.valueOf(element.getValue()));
            }
        }
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(this.taskClass, taskInfoMap, taskProps);
        }
        catch (final Exception exp) {
            CSVProcessor.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : {0}", exp);
        }
    }
    
    private void setFilterJSON(final JSONObject filterJSON) {
        this.csvParser.setFilterJSON(filterJSON);
    }
    
    static {
        CSVProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
