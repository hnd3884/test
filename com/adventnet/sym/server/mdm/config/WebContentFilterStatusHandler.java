package com.adventnet.sym.server.mdm.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.simple.JSONObject;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.csv.CSVImportStatusHandler;

public class WebContentFilterStatusHandler extends CSVImportStatusHandler
{
    private static WebContentFilterStatusHandler webContentFilterStatusHandler;
    
    public JSONObject processCSVFile(final InputStream inputStream, final JSONObject input, final Long customerId, final String operationLabel) throws Exception {
        try {
            final String className = (String)DBUtil.getValueFromDB("CSVOperation", "LABEL", (Object)operationLabel, "PARSER_CLASS");
            final WebContentFilterCSVProcessor reader = (WebContentFilterCSVProcessor)Class.forName(className).newInstance();
            reader.payloadName = (String)input.get((Object)"payload_name");
            return reader.persistCSVFile(inputStream, input, (JSONObject)null, customerId);
        }
        catch (final Exception ex) {
            Logger.getLogger(CSVImportStatusHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    private WebContentFilterStatusHandler() {
    }
    
    public static WebContentFilterStatusHandler getInstance() {
        if (WebContentFilterStatusHandler.webContentFilterStatusHandler == null) {
            WebContentFilterStatusHandler.webContentFilterStatusHandler = new WebContentFilterStatusHandler();
        }
        return WebContentFilterStatusHandler.webContentFilterStatusHandler;
    }
    
    static {
        WebContentFilterStatusHandler.webContentFilterStatusHandler = null;
    }
}
