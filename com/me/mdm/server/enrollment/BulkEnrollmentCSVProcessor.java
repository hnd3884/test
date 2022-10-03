package com.me.mdm.server.enrollment;

import com.adventnet.sym.server.mdm.core.EREvent;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestHandler;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class BulkEnrollmentCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String IS_USER_NAME_IN_CSV = "BulkEnroll_IsUserNameInCSV";
    public static final String IS_DOMAIN_NAME_IN_CSV = "BulkEnroll_IsDomainNameInCSV";
    public static final String IS_EMAIL_ADDRESS_IN_CSV = "BulkEnroll_IsEmailInCSV";
    public static final String IS_OWNED_BY_IN_CSV = "BulkEnroll_IsOwnedByInCSV";
    public static final String IS_PLATFORM_TYPE_IN_CSV = "BulkEnroll_IsPlatformTypeInCSV";
    public static final String IS_GROUP_NAME_IN_CSV = "BulkEnroll_IsGroupNameInCSV";
    public static final String IS_UDID_IN_CSV = "BulkEnroll_IsUdidInCSV";
    public static final String IS_PHONE_NUMBER_IN_CSV = "BulkEnroll_IsPhoneNumberInCSV";
    public static final String OPERATION_LABEL = "BulkEnroll";
    
    protected String getOperationLabel() {
        return "BulkEnroll";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"USER_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "BulkEnroll_IsUserNameInCSV"));
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                tableDetails.put((Object)"DOMAIN_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "BulkEnroll_IsDomainNameInCSV"));
            }
            tableDetails.put((Object)"EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(200), "BulkEnroll_IsEmailInCSV"));
            tableDetails.put((Object)"PLATFORM_TYPE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "BulkEnroll_IsPlatformTypeInCSV"));
            tableDetails.put((Object)"OWNED_BY", (Object)this.generateColumnDetailsJSON(Integer.valueOf(15), "BulkEnroll_IsOwnedByInCSV"));
            tableDetails.put((Object)"GROUP_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "BulkEnroll_IsGroupNameInCSV"));
            tableDetails.put((Object)"UDID", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "BulkEnroll_IsUdidInCSV"));
            tableDetails.put((Object)"PHONE_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(30), "BulkEnroll_IsPhoneNumberInCSV"));
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
            mandatoryHeaders.add("PLATFORM_TYPE");
            return mandatoryHeaders;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public void persistFile(final InputStream inputStream, final JSONObject filterJSON, final long customerID) throws Exception {
        super.persistFile(inputStream, filterJSON, customerID);
        EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(new EREvent(""), 5);
    }
    
    static {
        BulkEnrollmentCSVProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
