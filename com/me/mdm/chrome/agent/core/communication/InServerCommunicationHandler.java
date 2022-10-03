package com.me.mdm.chrome.agent.core.communication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import org.json.JSONObject;

public class InServerCommunicationHandler extends CommunicationHandler
{
    private String CHROME_OS_CHECKIN_SERVLET;
    
    public InServerCommunicationHandler() {
        this.CHROME_OS_CHECKIN_SERVLET = "com.me.mdm.agent.servlets.chromeos.ChromeOsCheckInServlet";
    }
    
    @Override
    public CommunicationStatus postData(final JSONObject postData, final HashMap<String, String> params) throws IOException {
        try {
            final VirtualDeviceRequestServlet.VirtualHttpServletRequest request = new VirtualDeviceRequestServlet.VirtualHttpServletRequest(postData.toString(), params);
            final VirtualDeviceRequestServlet.VirtualHttpServletResponse response = new VirtualDeviceRequestServlet.VirtualHttpServletResponse();
            ((VirtualDeviceRequestServlet)Class.forName(this.CHROME_OS_CHECKIN_SERVLET).newInstance()).doVirtualPost(request, response);
            final CommunicationStatus status = new CommunicationStatus(0, response.getResponseData().toString());
            return status;
        }
        catch (final InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            Logger.getLogger(InServerCommunicationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return new CommunicationStatus(1);
        }
    }
}
