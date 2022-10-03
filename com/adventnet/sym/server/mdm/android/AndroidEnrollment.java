package com.adventnet.sym.server.mdm.android;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.google.json.JsonSanitizer;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class AndroidEnrollment
{
    public static final String DEVICE_NAME = "NAME";
    public static final String REGISTRATION_ID = "REGISTRATION_ID";
    public static final String REGISTRATION_TIME = "REGISTRATION_TIME";
    public static final String ENROLLMENT_TIME = "ENROLLMENT_TIME";
    public static final String REMARKS = "REMARKS";
    public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    public static final String UDID = "UDID";
    public static final String DEVICE_ENROLLMENT = "DEVICE ENROLLMENT";
    private static String sourceClass;
    private static AndroidEnrollment androidenroll;
    private static Logger logger;
    
    private AndroidEnrollment() {
        final String sourceMethod = "AndroidEnrollment";
        SyMLogger.info(AndroidEnrollment.logger = Logger.getLogger("MDMLogger"), AndroidEnrollment.sourceClass, sourceMethod, "Creating instance...");
    }
    
    public static synchronized AndroidEnrollment getInstance() {
        if (AndroidEnrollment.androidenroll == null) {
            AndroidEnrollment.androidenroll = new AndroidEnrollment();
        }
        return AndroidEnrollment.androidenroll;
    }
    
    public HashMap<String, String> getHashFromCmdJSON(final String responseData) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        try {
            final JSONObject joRequest = new JSONObject(responseData);
            AndroidEnrollment.logger.log(Level.INFO, " JSON object is {0}", joRequest);
            hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(joRequest, "Command");
        }
        catch (final Exception je) {
            AndroidEnrollment.logger.log(Level.INFO, "Exception ocurred while parsing ..", je);
        }
        return hmap;
    }
    
    public HashMap<String, String> getHashFromEnrollJSON(final String strData) {
        HashMap<String, String> hmap = new HashMap<String, String>();
        try {
            final JSONObject joRequest = new JSONObject(strData);
            hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(joRequest);
        }
        catch (final Exception je) {
            AndroidEnrollment.logger.log(Level.INFO, "Exception ocurred while parsing ..{0}", je);
        }
        return hmap;
    }
    
    public void updateEnrollmentfailed(final HashMap<String, String> hmap) {
        final Integer errorcode = Integer.parseInt(hmap.get("ErrorCode"));
        final String remarks = this.getErrorRemarks(errorcode);
        final Long reqId = Long.parseLong(hmap.get("EnrollmentReqID"));
        MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(reqId, remarks, errorcode);
    }
    
    public void addOrUpdateELMStatus(final JSONObject data) {
        final Long erReqID = data.optLong("ENROLLMENT_REQUEST_ID");
        try {
            final Criteria erCriteria = new Criteria(Column.getColumn("AndroidELMStatus", "ENROLLMENT_REQUEST_ID"), (Object)erReqID, 0);
            final DataObject elmStatusDO = MDMUtil.getPersistence().get("AndroidELMStatus", erCriteria);
            if (elmStatusDO != null && elmStatusDO.isEmpty()) {
                final Row elmRow = new Row("AndroidELMStatus");
                elmRow.set("ENROLLMENT_REQUEST_ID", (Object)erReqID);
                elmRow.set("ELM_STATUS", (Object)data.optInt("ELM_STATUS"));
                elmStatusDO.addRow(elmRow);
                MDMUtil.getPersistence().add(elmStatusDO);
            }
            else {
                final Row elmRow = elmStatusDO.getFirstRow("AndroidELMStatus");
                elmRow.set("ELM_STATUS", (Object)data.optInt("ELM_STATUS"));
                elmStatusDO.updateRow(elmRow);
                MDMUtil.getPersistence().update(elmStatusDO);
            }
        }
        catch (final Exception ex) {
            AndroidEnrollment.logger.log(Level.WARNING, "Exception occurred while addOrUpdateELMStatus", ex);
        }
    }
    
    public void updateELMFailedRemarks(final HashMap hmap) {
        final String enrollmentRequestId = hmap.get("EnrollmentReqID");
        final String errorMsg = hmap.get("ELMErrorMsg");
        if (enrollmentRequestId != null && errorMsg != null) {
            MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(Long.parseLong(enrollmentRequestId), 0, errorMsg, -1);
        }
    }
    
    private String getErrorRemarks(final int errorCode) {
        if (errorCode == 12005) {
            return "dc.mdm.enroll.server_unavailable";
        }
        if (errorCode == 12006) {
            return "dc.mdm.enroll.acc_missing";
        }
        if (errorCode == 12007) {
            return "dc.mdm.enroll.auth_failed";
        }
        if (errorCode == 12009) {
            return "dc.mdm.enroll.device_reg_failed";
        }
        if (errorCode == 12201) {
            return "dc.mdm.enroll.device_reg_failed";
        }
        if (errorCode == 12204) {
            return "mdm.enroll.device_rooted";
        }
        return "";
    }
    
    public DeviceMessage ProcessELMActivation(final HashMap<String, String> hmap) {
        final DeviceMessage dMsg = new DeviceMessage();
        try {
            dMsg.status = "Acknowledged";
            final JSONObject response = new JSONObject();
            response.put("ELMLicenseKey", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("ELMKey"));
            String elmMessage = hmap.getOrDefault("Message", "{}");
            elmMessage = JsonSanitizer.sanitize(elmMessage);
            final JSONObject elmMessageJsonObject = new JSONObject(elmMessage);
            final boolean isBackwardCompatibilityKeyNeeded = (boolean)elmMessageJsonObject.optBoolean("IsBackwardKeyRequired", false);
            if (isBackwardCompatibilityKeyNeeded) {
                response.put("BackwardLicenseKey", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("BackwardCompatibilityElmKey"));
            }
            dMsg.messageResponse = response;
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidEnrollment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dMsg;
    }
    
    public DeviceMessage ProcessDeviceProvisiongMessage() {
        final DeviceMessage dMsg = new DeviceMessage();
        final String LAGUAGE_SETTINGS = "LanguageSettings";
        final String WAKEUP_SETTINGS = "WakeupSettings";
        try {
            dMsg.setMessageType("DeviceProvisioningSettings");
            dMsg.status = "Acknowledged";
            final JSONObject response = new JSONObject();
            response.put(LAGUAGE_SETTINGS, (Object)this.getLanguageSettings());
            response.put(WAKEUP_SETTINGS, (Object)this.getWakeupSettings());
            dMsg.messageResponse = response;
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidEnrollment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dMsg;
    }
    
    private JSONObject getLanguageSettings() throws JSONException {
        final JSONObject data = new JSONObject();
        final Boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
        data.put("IsLanguagePackEnabled", (Object)isLangPackEnabled);
        return data;
    }
    
    private JSONObject getWakeupSettings() throws JSONException, DataAccessException, Exception {
        final JSONObject data = new JSONObject();
        data.put("DeviceWakeUpPolicy", (Object)"GCMServer");
        data.put("GCMProjectId", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("GCMProjectId"));
        return data;
    }
    
    static {
        AndroidEnrollment.sourceClass = "AndroidEnrollment";
        AndroidEnrollment.androidenroll = null;
        AndroidEnrollment.logger = null;
    }
}
