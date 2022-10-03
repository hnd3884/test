package com.me.mdm.server.enrollment;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.List;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class BulkDeprovisionCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String ISIMEIINCSV = "IsIMEIInCSV";
    public static final String ISSLNOINCSV = "IsSerialNoInCSV";
    public static final String ISUDIDINCSV = "IsUDIDInCSV";
    public static final String OPERATION_LABEL = "BulkDeprovision";
    public static final String IOSDEVICE_COUNT = "BulkDeprovision_IOSDeviceCount";
    public final String operationLabel = "BulkDeprovision";
    
    protected String getOperationLabel() {
        return "BulkDeprovision";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"IMEI", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "IsIMEIInCSV"));
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "IsSerialNoInCSV"));
            tableDetails.put((Object)"UDID", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "IsUDIDInCSV"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(final List<String> columnsInCSV) throws Exception {
        try {
            if (!columnsInCSV.contains("IMEI") && !columnsInCSV.contains("SERIAL_NUMBER") && !columnsInCSV.contains("UDID")) {
                BulkDeprovisionCSVProcessor.logger.log(Level.INFO, "IMEI or serial number or udid is not available in the first row of csv file");
                throw new SyMException(13003, "dc.mdm.device_mgmt.error_no_pk", (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    static {
        BulkDeprovisionCSVProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
