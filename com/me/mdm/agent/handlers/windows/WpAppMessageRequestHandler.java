package com.me.mdm.agent.handlers.windows;

import com.me.mdm.core.auth.APIKey;
import com.me.mdm.server.privacy.PrivacyDeviceMessageHandler;
import com.adventnet.sym.server.mdm.terms.MDMTermsHandler;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class WpAppMessageRequestHandler extends BaseProcessDeviceRequestHandler
{
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        String responseData = null;
        final String messageType = String.valueOf(new JSONObject((String)request.deviceRequestData).get("MsgRequestType"));
        DeviceMessage deviceMsg = new DeviceMessage();
        boolean addToQueue = true;
        if (messageType != null && messageType.equalsIgnoreCase("TermsOfUse")) {
            addToQueue = false;
            final JSONObject requestJSON = new JSONObject((String)request.deviceRequestData);
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(request.requestMap);
            deviceMsg = MDMTermsHandler.getInstance().getDiffAndUpdateStatus(requestJSON.optJSONObject("MsgRequest").toString(), key);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("PrivacySettings")) {
            addToQueue = false;
            deviceMsg = PrivacyDeviceMessageHandler.getInstance().processPrivacySettingsRequest(request);
        }
        if (addToQueue) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 123);
            deviceMsg.messageType = messageType;
            deviceMsg.status = "Acknowledged";
            deviceMsg.messageResponse = new JSONObject("{}");
            deviceMsg.messageVersion = "2.0";
        }
        responseData = this.constructMessage(deviceMsg).toString();
        return responseData;
    }
}
