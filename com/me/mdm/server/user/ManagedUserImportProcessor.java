package com.me.mdm.server.user;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class ManagedUserImportProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String IS_DOMAIN_NAME_IN_CSV = "ManagedUserImport_IsDomainNameInCSV";
    public static final String IS_USER_NAME_IN_CSV = "ManagedUserImport_IsUserNameInCSV";
    public static final String IS_EMAIL_ADDRESS_IN_CSV = "ManagedUserImport_IsEmailInCSV";
    public static final String IS_NEW_EMAIL_ADDRESS_IN_CSV = "ManagedUserImport_IsNewEmailInCSV";
    public static final String IS_PHONE_NUMBER_IN_CSV = "ManagedUserImport_IsPhoneNumberInCSV";
    public static final String OPERATION_LABEL = "ManagedUserImport";
    
    protected String getOperationLabel() {
        return "ManagedUserImport";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"USER_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "ManagedUserImport_IsUserNameInCSV"));
            tableDetails.put((Object)"EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), "ManagedUserImport_IsEmailInCSV"));
            tableDetails.put((Object)"NEW_EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), "ManagedUserImport_IsNewEmailInCSV"));
            tableDetails.put((Object)"PHONE_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(30), "ManagedUserImport_IsPhoneNumberInCSV"));
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected List<String> listMandatoryHeaders() throws Exception {
        try {
            final List<String> mandatoryHeaders = new ArrayList<String>();
            mandatoryHeaders.add("USER_NAME");
            mandatoryHeaders.add("EMAIL_ADDRESS");
            return mandatoryHeaders;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    static {
        ManagedUserImportProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
