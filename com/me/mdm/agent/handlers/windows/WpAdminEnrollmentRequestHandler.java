package com.me.mdm.agent.handlers.windows;

import org.json.JSONException;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WpAdminEnrollmentRequestHandler extends WpEnrollmentRequestHandler
{
    private Logger logger;
    
    public WpAdminEnrollmentRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    boolean handleAuthentication(final JSONObject jsonObject) {
        boolean isDeviceAllowed = Boolean.FALSE;
        try {
            final String userName = jsonObject.optString("Username", (String)null);
            final String templateToken = String.valueOf(jsonObject.get("templateToken"));
            String imei = "";
            String udid = "";
            String serialNumber = "";
            if (jsonObject.has("MobileEquipmentId")) {
                if (jsonObject.get("MobileEquipmentId") instanceof JSONArray) {
                    final JSONArray imeiArray = jsonObject.getJSONArray("MobileEquipmentId");
                    final String[] imeiCriteriaVal = StringUtils.stripAll(imeiArray.join(",").split(","), "\"");
                    final DataObject assignedIMEIDO = SyMUtil.getPersistence().get("DeviceForEnrollment", new Criteria(Column.getColumn("DeviceForEnrollment", "IMEI"), (Object)imeiCriteriaVal, 8));
                    if (assignedIMEIDO.isEmpty()) {
                        imei = jsonObject.getJSONArray("MobileEquipmentId").get(0).toString();
                    }
                    else {
                        imei = (String)assignedIMEIDO.getFirstValue("DeviceForEnrollment", "IMEI");
                    }
                }
                else {
                    imei = String.valueOf(jsonObject.get("MobileEquipmentId"));
                }
            }
            if (jsonObject.has("DeviceID")) {
                udid = (String)jsonObject.get("DeviceID");
                if (udid.contains("urn:uuid:")) {
                    final String[] ret = udid.split("urn:uuid:");
                    udid = ret[1];
                }
            }
            if (jsonObject.has("SerialNumber")) {
                serialNumber = String.valueOf(jsonObject.get("SerialNumber"));
            }
            final int templateType = new EnrollmentTemplateHandler().getEnrollmentTemplateTypeForTemplateToken(templateToken);
            MICSFeatureTrackerUtil.windowsAdminEnrollmentStart(templateType);
            final JSONObject authenticateMessageRequest = new JSONObject();
            authenticateMessageRequest.put("MsgRequestType", (Object)"WindowsAdminEnrollment");
            final JSONObject authenticateMessage = new JSONObject();
            authenticateMessage.put("IMEI", (Object)imei);
            authenticateMessage.put("SerialNumber", (Object)serialNumber);
            authenticateMessage.put("TemplateToken", (Object)templateToken);
            authenticateMessage.put("UDID", (Object)udid);
            authenticateMessage.put("TEMPLATE_TYPE", templateType);
            if (templateType == 32) {
                this.setAzureADEnrollmentProperties(jsonObject, authenticateMessage);
            }
            authenticateMessageRequest.put("MsgRequest", (Object)authenticateMessage);
            final AdminEnrollmentHandler depHandler = new AdminEnrollmentHandler();
            final JSONObject authenticateResponseJSON = depHandler.processMessage(authenticateMessageRequest);
            if (String.valueOf(authenticateResponseJSON.get("Status")).equalsIgnoreCase("Acknowledged")) {
                final JSONObject msgResponseJSON = authenticateResponseJSON.getJSONObject("MsgResponse");
                final Long enrollmentRequestID = msgResponseJSON.getLong("EnrollmentReqID");
                final Long managedUserId = msgResponseJSON.getLong("ManagedUserId");
                jsonObject.put("erid", (Object)enrollmentRequestID);
                jsonObject.put("muid", (Object)managedUserId);
                isDeviceAllowed = Boolean.TRUE;
                jsonObject.put("isDeviceAllowed", (Object)Boolean.TRUE);
            }
            else {
                isDeviceAllowed = false;
                jsonObject.put("isDeviceAllowed", (Object)Boolean.FALSE);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during user authentication \n", exp);
        }
        return isDeviceAllowed;
    }
    
    private void setAzureADEnrollmentProperties(final JSONObject jsonObject, final JSONObject authenticateMessage) throws JSONException {
        final String azureADWebToken = new String(Base64.decodeBase64(String.valueOf(jsonObject.getJSONObject("UserAuthJson").get("tokenValue"))));
        final String[] jwtTokens = azureADWebToken.split("\\.");
        final JSONObject azureWebTokenHeaderJSON = new JSONObject(new String(Base64.decodeBase64(jwtTokens[0])));
        final JSONObject azureWebTokenBodyJSON = new JSONObject(new String(Base64.decodeBase64(jwtTokens[1])));
        final String userPrincipalName = String.valueOf(azureWebTokenBodyJSON.get("upn"));
        final String emailAddress = String.valueOf(azureWebTokenBodyJSON.get("unique_name"));
        final String userName = String.valueOf(azureWebTokenBodyJSON.get("name"));
        final String domainName = userPrincipalName.split("@")[1];
        final JSONObject azureADUserDetails = new JSONObject();
        azureADUserDetails.put("NAME", (Object)userPrincipalName);
        azureADUserDetails.put("FIRST_NAME", (Object)userName);
        azureADUserDetails.put("EMAIL_ADDRESS", (Object)emailAddress);
        azureADUserDetails.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
        authenticateMessage.put("EnrolledUserDetails", (Object)azureADUserDetails);
    }
}
