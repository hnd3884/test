package com.me.mdm.agent.servlets.macos;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.agent.servlets.ios.IOSNativeAppServlet;

public class MacOSNativeAppServlet extends IOSNativeAppServlet
{
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        MacOSNativeAppServlet.logger.log(Level.INFO, "MacOSNativeAppServlet => (POST) Received request from APPLE ");
        MacOSNativeAppServlet.mdmdevicedatalogger.log(Level.INFO, "MacOSNativeAppServlet => (POST) Received request from APPLE ");
        super.doPost(request, response, deviceRequest);
    }
}
