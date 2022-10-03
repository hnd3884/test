package com.me.mdm.server.customgroup.csv;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.List;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class DeviceToCustomGroupImportProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String IS_IMEI_IN_CSV = "DeviceToCustomGroupImport_IsIMEIInCSV";
    public static final String IS_SERIAL_NUMBER_IN_CSV = "DeviceToCustomGroupImport_IsSerialNoInCSV";
    public static final String IS_EMAIL_ADDRESS_IN_CSV = "DeviceToCustomGroupImport_IsEmailInCSV";
    public static final String IS_GROUP_NAME_IN_CSV = "DeviceToCustomGroupImport_IsGroupNameInCSV";
    public static final String OPERATION_LABEL = "CustomGroupImport";
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"IMEI", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "DeviceToCustomGroupImport_IsIMEIInCSV"));
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "DeviceToCustomGroupImport_IsSerialNoInCSV"));
            tableDetails.put((Object)"EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), "DeviceToCustomGroupImport_IsEmailInCSV"));
            tableDetails.put((Object)"GROUP_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), "DeviceToCustomGroupImport_IsGroupNameInCSV"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(final List<String> columnsInCSV) throws Exception {
        try {
            if (!columnsInCSV.contains("IMEI") && !columnsInCSV.contains("SERIAL_NUMBER") && !columnsInCSV.contains("EMAIL_ADDRESS")) {
                DeviceToCustomGroupImportProcessor.logger.log(Level.INFO, "IMEI or serial number or Email Address is not available in the first row of csv file");
                throw new SyMException(51017, I18N.getMsg("dc.mdm.device_mgmt.error_no_device_identifier", new Object[0]), (Throwable)null);
            }
            if (!columnsInCSV.contains("GROUP_NAME")) {
                DeviceToCustomGroupImportProcessor.logger.log(Level.INFO, "Group name is mandatory");
                throw new SyMException(51018, I18N.getMsg("dc.mdm.device_mgmt.error_no_group_identifier", new Object[0]), (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected String getOperationLabel() {
        return "CustomGroupImport";
    }
    
    static {
        DeviceToCustomGroupImportProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
