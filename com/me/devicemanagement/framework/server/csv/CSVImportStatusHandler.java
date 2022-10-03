package com.me.devicemanagement.framework.server.csv;

import com.me.devicemanagement.framework.server.util.DBUtil;
import java.io.InputStream;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class CSVImportStatusHandler
{
    private static Logger logger;
    private static CSVImportStatusHandler csvImportStatusHandler;
    
    protected CSVImportStatusHandler() {
    }
    
    public static CSVImportStatusHandler getInstance() {
        if (CSVImportStatusHandler.csvImportStatusHandler == null) {
            CSVImportStatusHandler.csvImportStatusHandler = new CSVImportStatusHandler();
        }
        return CSVImportStatusHandler.csvImportStatusHandler;
    }
    
    public JSONObject getImportStatus(final Long customerId, final String operationLabel) throws Exception {
        try {
            final JSONObject importDetails = new JSONObject();
            final String status = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getStatusLabel(operationLabel), customerId);
            if (status == null || status.equalsIgnoreCase("NO_HISTORY")) {
                importDetails.put((Object)"STATUS", (Object)"NO_HISTORY");
                return importDetails;
            }
            int totalCount = 0;
            String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getTotalLabel(operationLabel), customerId);
            if (countStr != null) {
                totalCount = Integer.parseInt(countStr);
            }
            int completedCount = 0;
            countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel(operationLabel), customerId);
            if (countStr != null) {
                completedCount = Integer.parseInt(countStr);
            }
            if (status.equalsIgnoreCase("IN_PROGRESS")) {
                importDetails.put((Object)"STATUS", (Object)status);
                final JSONObject details = new JSONObject();
                details.put((Object)"COMPLETED", (Object)completedCount);
                details.put((Object)"TOTAL", (Object)totalCount);
                importDetails.put((Object)"DETAILS", (Object)details);
            }
            else {
                importDetails.put((Object)"STATUS", (Object)"COMPLETED");
                int failureCount = 0;
                countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getFailedLabel(operationLabel), customerId);
                if (countStr != null) {
                    failureCount = Integer.parseInt(countStr);
                }
                final JSONObject details2 = new JSONObject();
                details2.put((Object)"SUCCESS", (Object)(totalCount - failureCount));
                details2.put((Object)"FAILURE", (Object)failureCount);
                importDetails.put((Object)"DETAILS", (Object)details2);
            }
            return importDetails;
        }
        catch (final Exception ex) {
            CSVImportStatusHandler.logger.log(Level.SEVERE, "Exception while getting Import Status :{0}", ex);
            throw ex;
        }
    }
    
    public void clearImportStatus(final Long customerId, final String operationLabel) throws Exception {
        try {
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getTotalLabel(operationLabel), (Object)"0");
            jsonObj.put((Object)CSVProcessor.getProcessedLabel(operationLabel), (Object)"0");
            jsonObj.put((Object)CSVProcessor.getFailedLabel(operationLabel), (Object)"0");
            jsonObj.put((Object)CSVProcessor.getStatusLabel(operationLabel), (Object)"NO_HISTORY");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerId);
        }
        catch (final Exception ex) {
            CSVImportStatusHandler.logger.log(Level.SEVERE, "Exception while clearing Import Status :{0}", ex);
            throw ex;
        }
    }
    
    public JSONObject processCSVFile(final InputStream inputStream, final JSONObject input, final Long customerId, final String operationLabel) throws Exception {
        try {
            final String className = (String)DBUtil.getValueFromDB("CSVOperation", "LABEL", operationLabel, "PARSER_CLASS");
            final CSVProcessor reader = (CSVProcessor)Class.forName(className).newInstance();
            return reader.persistCSVFile(inputStream, input, customerId);
        }
        catch (final Exception ex) {
            Logger.getLogger(CSVImportStatusHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    static {
        CSVImportStatusHandler.logger = Logger.getLogger("MDMLogger");
        CSVImportStatusHandler.csvImportStatusHandler = null;
    }
}
