package com.me.mdm.agent.handlers;

import com.me.mdm.agent.handlers.windows.WpServerRequestHandler;
import com.me.mdm.agent.handlers.windows.WpAdminEnrollmentRequestHandler;
import com.me.mdm.agent.handlers.windows.WpEnrollmentRequestHandler;

public class WpRequestHandler
{
    public String processRequest(final DeviceRequest deviceRequest) throws Exception {
        String responseData = null;
        final String deviceRequestType = deviceRequest.deviceRequestType;
        if (deviceRequestType.equalsIgnoreCase("wpdiscover") || deviceRequestType.equalsIgnoreCase("wpcheckin")) {
            WpEnrollmentRequestHandler handler = new WpEnrollmentRequestHandler();
            if (deviceRequest.requestMap.get("pathPrefix").toString().contains("admin")) {
                handler = new WpAdminEnrollmentRequestHandler();
            }
            responseData = handler.processRequest(deviceRequest);
        }
        else if (deviceRequestType.equalsIgnoreCase("wpserver")) {
            final WpServerRequestHandler handler2 = new WpServerRequestHandler();
            responseData = handler2.processRequest(deviceRequest);
        }
        return responseData;
    }
}
