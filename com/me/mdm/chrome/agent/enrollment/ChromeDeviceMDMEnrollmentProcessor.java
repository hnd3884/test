package com.me.mdm.chrome.agent.enrollment;

import java.util.logging.Level;
import com.me.mdm.chrome.agent.commands.inventory.HardwareDetails;
import com.me.mdm.chrome.agent.core.CommandConstants;
import com.me.mdm.chrome.agent.core.MessageUtil;
import org.json.JSONException;
import com.me.mdm.chrome.agent.core.communication.CommunicationStatus;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class ChromeDeviceMDMEnrollmentProcessor extends ChromeDeviceEnrollmentProcessor
{
    Logger logger;
    
    public ChromeDeviceMDMEnrollmentProcessor() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public boolean processEnrollment(final Context context, final JSONObject enrollmentData) throws JSONException {
        final JSONObject enrollmentReqJSON = this.getEnrollmentRequest(context, enrollmentData);
        final CommunicationStatus status = this.sendDataForDeviceEnrollment(context, enrollmentReqJSON, enrollmentData);
        return status.getStatus() == 0;
    }
    
    private JSONObject getEnrollmentRequest(final Context context, final JSONObject deviceData) throws JSONException {
        final JSONObject msgContent = new JSONObject();
        final String templateToken = String.valueOf(deviceData.get("TEMPLATE_TOKEN"));
        msgContent.put("TemplateToken", (Object)templateToken);
        msgContent.put("SerialNumber", (Object)String.valueOf(deviceData.get("SerialNumber")));
        msgContent.put("IMEI", (Object)"--");
        msgContent.put("UDID", deviceData.get("UDID"));
        final MessageUtil msgUtil = new MessageUtil(context);
        msgUtil.messageType = "ChromeEnrollAgentSolicitation";
        msgUtil.setMessageContent(msgContent);
        final CommunicationStatus status = msgUtil.postMessageData();
        return new JSONObject(status.getUrlDataBuffer()).getJSONObject("MessageResponse");
    }
    
    private JSONObject constructEnrollmentData(final JSONObject enrollmentReqData, final JSONObject deviceData, final MessageUtil msgUtil) {
        final JSONObject enrollDetails = new JSONObject();
        try {
            final JSONObject deviceInfo = new JSONObject();
            enrollDetails.put("EnrollmentReqID", enrollmentReqData.getLong("EnrollmentReqID"));
            enrollDetails.put("EmailAddress", (Object)String.valueOf(enrollmentReqData.get("EmailAddress")));
            enrollDetails.put("AgentVersion", (Object)"0.0.1 CMPA");
            enrollDetails.put("AgentVersionCode", (Object)CommandConstants.AGENT_VERSION_CODE);
            enrollDetails.put("DeviceName", (Object)(enrollmentReqData.get("UserName") + "_" + deviceData.optString("Model", "Chromebook")));
            enrollDetails.put("DeviceType", (Object)HardwareDetails.DEVICE_TYPE_LAPTOP);
            deviceInfo.put("DeviceType", (Object)HardwareDetails.DEVICE_TYPE_LAPTOP);
            deviceInfo.put("SerialNumber", (Object)String.valueOf(deviceData.get("SerialNumber")));
            deviceInfo.put("Model", (Object)deviceData.optString("Model", "Chromebook"));
            deviceInfo.put("ModelName", (Object)HardwareDetails.DEVICE_TYPE_LAPTOP);
            deviceInfo.put("OSVersion", (Object)deviceData.optString("OSVersion", "--"));
            deviceInfo.put("EASDeviceIdentifier", deviceData.get("UDID"));
            deviceInfo.put("ProductName", (Object)"Chromebook");
            enrollDetails.put("DeviceInfo", (Object)deviceInfo);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while constructing enrollment data", (Throwable)ex);
            msgUtil.setMsgStatus("Error");
            msgUtil.setMsgRemarks("Enrollment Failed");
        }
        return enrollDetails;
    }
    
    private CommunicationStatus sendDataForDeviceEnrollment(final Context context, final JSONObject enrollmentReqData, final JSONObject deviceData) {
        CommunicationStatus status = new CommunicationStatus(1);
        try {
            final MessageUtil msgUtil = new MessageUtil(context);
            msgUtil.messageType = "Enrollment";
            final String enrollRemarks = "Enrollment Completed";
            msgUtil.setMsgStatus("Acknowledged");
            msgUtil.setMsgRemarks(enrollRemarks);
            msgUtil.setMessageData(this.constructEnrollmentData(enrollmentReqData, deviceData, msgUtil));
            status = msgUtil.postMessageData();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception ocurred in posting enrollment data", exp);
        }
        return status;
    }
}
