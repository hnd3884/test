package com.me.mdm.chrome.agent.commands.profiles.payloads;

import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;

public class PrivacyCommandHandler extends ProcessRequestHandler
{
    public Logger logger;
    
    public PrivacyCommandHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public void processRequest(final Request request, final Response response) {
        final JSONObject requestData = (JSONObject)request.requestData;
        final Context context = request.getContainer().getContext();
        ChromeDeviceManager.getInstance().getPrivacyManager().setPrivacySettings(context, requestData);
    }
}
