package com.me.mdm.server.device;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.List;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class DeviceListCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String ISSLNOINCSV = "IsSerialNoInCSV";
    public static final String ISUDIDINCSV = "IsUDIDInCSV";
    public static final String OPERATION_LABEL = "DeviceList";
    
    protected String getOperationLabel() {
        return "DeviceList";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "IsSerialNoInCSV"));
            tableDetails.put((Object)"UDID", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "IsUDIDInCSV"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(List<String> columnsInCSV) throws Exception {
        try {
            columnsInCSV = this.modifyHeadersInDeviceListCSVForChecking(columnsInCSV);
            if (!columnsInCSV.contains("SERIAL_NUMBER".replace("_", "").toLowerCase()) || !columnsInCSV.contains("UDID".toLowerCase())) {
                DeviceListCSVProcessor.logger.log(Level.INFO, "Serial number or UDID is not available in the first row of csv file");
                throw new SyMException(13003, I18N.getMsg("mdm.csv.device_import_header_missing", new Object[0]), (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private List<String> modifyHeadersInDeviceListCSVForChecking(final List<String> inputFromCSV) {
        final List<String> convertedList = new ArrayList<String>();
        for (int i = 0; i < inputFromCSV.size(); ++i) {
            final String headerString = inputFromCSV.get(i);
            if (!MDMUtil.getInstance().isEmpty(headerString)) {
                convertedList.add(headerString.replaceAll("_|\\s", "").toLowerCase());
            }
        }
        return convertedList;
    }
    
    static {
        DeviceListCSVProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
