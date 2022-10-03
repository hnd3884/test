package com.me.mdm.server.enrollment.adminenroll;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class AssignUserCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public final String isIMEIInCSV;
    public final String isSerialNumberInCSV;
    public final String isExchangeIDInCSV;
    public final String isUserNameInCSV;
    public final String isDomainNameInCSV;
    public final String isEmailAddressInCSV;
    public final String isGroupNameInCSV;
    public final String isDeviceNameInCSV;
    public final String isUDIDInCSV;
    public static final String ENROLLMENT_TEMPLATE = "EnrollmentTemplate";
    public final String operationLabel;
    
    AssignUserCSVProcessor(final String operationLabel) {
        super(operationLabel);
        this.operationLabel = operationLabel;
        this.isIMEIInCSV = operationLabel + "_IsIMEIInCSV";
        this.isSerialNumberInCSV = operationLabel + "_isSerialNoInCSV";
        this.isExchangeIDInCSV = operationLabel + "_isExchangeIDInCSV";
        this.isUserNameInCSV = operationLabel + "_IsUserNameInCSV";
        this.isDomainNameInCSV = operationLabel + "_IsDomainNameInCSV";
        this.isEmailAddressInCSV = operationLabel + "_IsEmailInCSV";
        this.isGroupNameInCSV = operationLabel + "_IsGroupNameInCSV";
        this.isDeviceNameInCSV = operationLabel + "_IsDeviceNameInCSV";
        this.isUDIDInCSV = operationLabel + "_IsUDIDInCSV";
    }
    
    protected String getOperationLabel() {
        return this.operationLabel;
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"IMEI", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), this.isIMEIInCSV));
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), this.isSerialNumberInCSV));
            tableDetails.put((Object)"UDID", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), this.isUDIDInCSV));
            tableDetails.put((Object)"EXCHANGE_ID", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), this.isExchangeIDInCSV));
            tableDetails.put((Object)"USER_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), this.isUserNameInCSV));
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                tableDetails.put((Object)"DOMAIN_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), this.isDomainNameInCSV));
            }
            tableDetails.put((Object)"EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), this.isEmailAddressInCSV));
            tableDetails.put((Object)"GROUP_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), this.isGroupNameInCSV));
            tableDetails.put((Object)"DEVICE_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), this.isDeviceNameInCSV));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(List<String> columnsInCSV) throws Exception {
        try {
            columnsInCSV = MDMUtil.getInstance().modifyHeadersInBulkCSVForValidation(columnsInCSV);
            if (!columnsInCSV.contains("IMEI".toLowerCase()) && !columnsInCSV.contains("SERIAL_NUMBER".replace("_", "").toLowerCase()) && !columnsInCSV.contains("UDID".toLowerCase()) && !columnsInCSV.contains("EXCHANGE_ID".replace("_", "").toLowerCase())) {
                AssignUserCSVProcessor.logger.log(Level.INFO, "IMEI or serial number is not available in the first row of csv file");
                throw new SyMException(13003, "dc.mdm.device_mgmt.error_no_pk", (Throwable)null);
            }
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                if (!columnsInCSV.contains("EMAIL_ADDRESS".replace("_", "").toLowerCase())) {
                    AssignUserCSVProcessor.logger.log(Level.INFO, "A field to modify is not available");
                    throw new SyMException(13004, "dc.mdm.device_mgmt.error_no_editable_columns", (Throwable)null);
                }
            }
            else if (!columnsInCSV.contains("USER_NAME".replace("_", "").toLowerCase()) || !columnsInCSV.contains("EMAIL_ADDRESS".replace("_", "").toLowerCase())) {
                AssignUserCSVProcessor.logger.log(Level.INFO, "A field to modify is not available");
                throw new SyMException(13004, "dc.mdm.device_mgmt.error_no_editable_columns", (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    static {
        AssignUserCSVProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
