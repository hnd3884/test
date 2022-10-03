package com.me.mdm.server.profiles.mac;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.List;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class MDMFileVaultRotateKeyImportCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String OPERATION_LABEL = "MacFilevaultKeyImport";
    
    protected String getOperationLabel() {
        return "MacFilevaultKeyImport";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"PERSONAL_RECOVERY_KEY", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "PERSONAL_RECOVERY_KEY"));
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "SERIAL_NUMBER"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(final List<String> columnsInCSV) throws Exception {
        try {
            if (!columnsInCSV.contains("SERIAL_NUMBER") || !columnsInCSV.contains("PERSONAL_RECOVERY_KEY")) {
                MDMFileVaultRotateKeyImportCSVProcessor.logger.log(Level.INFO, "SerialNo/PRK not available");
                throw new SyMException(13003, "mdm.profl.filevault.import.unavilable", (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    static {
        MDMFileVaultRotateKeyImportCSVProcessor.logger = Logger.getLogger("MDMConfigLogger");
    }
}
