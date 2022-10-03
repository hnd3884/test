package com.adventnet.sym.server.mdm.config;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.List;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class WebContentFilterCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String OPERATION_LABEL = "WebContentFilter";
    public String payloadName;
    
    protected String getOperationLabel() {
        return "WebContentFilter";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"URL", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "url"));
            tableDetails.put((Object)"BOOKMARK_TITLE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "bookmarkTitle"));
            tableDetails.put((Object)"BOOKMARK_PATH", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "String", "bookmarkPath"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateCSVHeader(final List<String> columnsInCSV) throws Exception {
        try {
            if (!columnsInCSV.contains("URL")) {
                WebContentFilterCSVProcessor.logger.log(Level.INFO, "URL is not available in the first row of csv file");
                throw new SyMException(13003, "mdm.profl.wcf.error.pk", (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected void validateRowCount(final int rowCount) throws Exception {
        if (this.payloadName.equalsIgnoreCase("webcontentfilterpolicy") && rowCount > 500) {
            throw new SyMException(13007, "mdm.profl.wcf.error.rowCount", (Throwable)null);
        }
    }
    
    static {
        WebContentFilterCSVProcessor.logger = Logger.getLogger("MDMConfigLogger");
    }
}
