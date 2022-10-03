package com.me.mdm.server.adep;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class BulkDEPCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String OPERATION_LABEL = "BulkDEP";
    public static final String IS_USER_NAME_IN_CSV = "BulkDEP_IsUserNameInCSV";
    public static final String IS_DOMAIN_NAME_IN_CSV = "BulkDEP_IsDomainNameInCSV";
    public static final String IS_EMAIL_ADDRESS_IN_CSV = "BulkDEP_IsEmailInCSV";
    public static final String IS_SERIAL_NUMBER_IN_CSV = "BulkDEP_IsSerialNumberInCSV";
    public static final String IS_GROUP_NAME_IN_CSV = "BulkDEP_IsGroupNameInCSV";
    
    protected String getOperationLabel() {
        return "BulkDEP";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"USER_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "BulkDEP_IsUserNameInCSV"));
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                tableDetails.put((Object)"DOMAIN_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "BulkDEP_IsDomainNameInCSV"));
            }
            tableDetails.put((Object)"EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), "BulkDEP_IsEmailInCSV"));
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "BulkDEP_IsSerialNumberInCSV"));
            tableDetails.put((Object)"GROUP_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "BulkDEP_IsGroupNameInCSV"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected List<String> listMandatoryHeaders() throws Exception {
        try {
            final List<String> mandatoryHeaders = new ArrayList<String>();
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                mandatoryHeaders.add("USER_NAME");
            }
            mandatoryHeaders.add("EMAIL_ADDRESS");
            mandatoryHeaders.add("SERIAL_NUMBER");
            return mandatoryHeaders;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(final List<String> columnsInCSV) throws Exception {
        try {
            if (!columnsInCSV.contains("SERIAL_NUMBER")) {
                BulkDEPCSVProcessor.logger.log(Level.INFO, "IMEI or serial number is not available in the first row of csv file");
                throw new SyMException(13003, "dc.mdm.device_mgmt.error_no_pk", (Throwable)null);
            }
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                if (!columnsInCSV.contains("EMAIL_ADDRESS")) {
                    BulkDEPCSVProcessor.logger.log(Level.INFO, "A field to modify is not available");
                    throw new SyMException(13004, "dc.mdm.device_mgmt.error_no_editable_columns", (Throwable)null);
                }
            }
            else if (!columnsInCSV.contains("USER_NAME") || !columnsInCSV.contains("EMAIL_ADDRESS")) {
                BulkDEPCSVProcessor.logger.log(Level.INFO, "A field to modify is not available");
                throw new SyMException(13004, "dc.mdm.device_mgmt.error_no_editable_columns", (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    static {
        BulkDEPCSVProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
