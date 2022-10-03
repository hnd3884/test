package com.me.mdm.server.enrollment.adminenroll;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.simple.JSONObject;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.csv.CSVImportStatusHandler;

public class UserAssignmentStatusHandler extends CSVImportStatusHandler
{
    private static UserAssignmentStatusHandler userAssignmentStatusHandler;
    
    public JSONObject processCSVFile(final InputStream inputStream, final JSONObject input, final Long customerId, final String operationLabel) throws Exception {
        try {
            final String className = (String)DBUtil.getValueFromDB("CSVOperation", "LABEL", (Object)operationLabel, "PARSER_CLASS");
            final AssignUserCSVProcessor reader = (AssignUserCSVProcessor)Class.forName(className).newInstance();
            final JSONObject filterJSON = new JSONObject();
            filterJSON.put((Object)"TEMPLATE_TYPE", input.get((Object)"EnrollmentTemplate"));
            return reader.persistCSVFile(inputStream, input, filterJSON, customerId);
        }
        catch (final Exception ex) {
            Logger.getLogger(CSVImportStatusHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    private UserAssignmentStatusHandler() {
    }
    
    public static UserAssignmentStatusHandler getInstance() {
        if (UserAssignmentStatusHandler.userAssignmentStatusHandler == null) {
            UserAssignmentStatusHandler.userAssignmentStatusHandler = new UserAssignmentStatusHandler();
        }
        return UserAssignmentStatusHandler.userAssignmentStatusHandler;
    }
    
    static {
        UserAssignmentStatusHandler.userAssignmentStatusHandler = null;
    }
}
