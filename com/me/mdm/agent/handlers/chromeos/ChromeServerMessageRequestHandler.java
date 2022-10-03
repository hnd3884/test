package com.me.mdm.agent.handlers.chromeos;

import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class ChromeServerMessageRequestHandler extends BaseProcessDeviceRequestHandler
{
    private Logger logger;
    private Logger messagesAccessLogger;
    public Logger checkinLogger;
    private String separator;
    
    public ChromeServerMessageRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.messagesAccessLogger = Logger.getLogger("MDMMessagesLogger");
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
        this.separator = "\t";
    }
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        String responseData = null;
        final JSONObject requestJSON = (JSONObject)request.deviceRequestData;
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(requestJSON);
        hmap.put("PlatformType", String.valueOf(4));
        hmap.put("CMDRepType", "" + request.repositoryType);
        final String messageType = hmap.get("MessageType");
        final JSONObject jsonObject = (JSONObject)request.deviceRequestData;
        DeviceMessage deviceMsg = null;
        if (String.valueOf(jsonObject.get("MessageType")).equalsIgnoreCase("Enrollment")) {
            this.checkinLogger.log(Level.INFO, "Chrome MessageType:{0} Erid:{1} Udid:{2}", new Object[] { messageType, hmap.get("EnrollmentReqID"), hmap.get("UDID") });
        }
        else if (String.valueOf(jsonObject.get("MessageType")).equalsIgnoreCase("Enrollment")) {
            this.checkinLogger.log(Level.INFO, "Chrome MessageType:{0} Udid:{1}", new Object[] { messageType, hmap.get("UDID") });
            final String accessMessage = "DATA-IN: " + messageType + this.separator + request.resourceID + this.separator + requestJSON.optString("UDID", "null") + this.separator + requestJSON.optString("Status", "null") + this.separator + MDMUtil.getCurrentTimeInMillis();
            this.messagesAccessLogger.log(Level.INFO, accessMessage);
        }
        if (messageType.equalsIgnoreCase("ChromeEnrollAgentSolicitation")) {
            deviceMsg = null;
            final JSONObject responseJSON = new AdminEnrollmentHandler().processMessage(requestJSON);
            responseData = responseJSON.toString();
        }
        else {
            deviceMsg = new DeviceMessage();
            final int dataQueueType = 126;
            this.addResponseToQueue(request, requestJSON.toString(), dataQueueType);
        }
        if (deviceMsg != null) {
            final JSONObject responseMsgJSON = this.constructMessage(deviceMsg);
            final String accessMessage2 = "DATA-OUT: " + responseMsgJSON.optString("MessageType") + this.separator + request.resourceID + this.separator + request.deviceUDID + this.separator + responseMsgJSON.optString("Status") + this.separator + MDMUtil.getCurrentTimeInMillis();
            responseData = responseMsgJSON.toString();
            this.messagesAccessLogger.log(Level.INFO, accessMessage2);
        }
        return responseData;
    }
    
    JSONObject constructResponseMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating the response message", e);
        }
        return response;
    }
    
    @Override
    protected String getNextDeviceCommandQuery(final DeviceCommand nextCommand, final DeviceRequest request) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
