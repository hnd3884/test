package com.me.mdm.agent.servlets.ios;

import java.util.HashMap;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AppleSharedIPadCheckInServlet extends MacCheckInServlet
{
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "AppleSharedIPadCheckInServlet => (POST) Received request from APPLE ");
        this.checkinLogger.log(Level.INFO, "AppleSharedIPadCheckInServlet => (POST) Received request from APPLE ");
        super.doPost(request, response, deviceRequest);
    }
    
    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "AppleSharedIPadCheckInServlet => (PUT) Received request from APPLE ");
        this.checkinLogger.log(Level.INFO, "AppleSharedIPadCheckInServlet => (PUT) Received request from APPLE ");
        super.doPut(request, response, deviceRequest);
    }
    
    @Override
    protected void handleTokenUpdateMessage(final HashMap hashPlist, final String strData, final Long enrollmentRequestId, final String isAppleConfig, final String messageType) throws Exception {
        this.className = "AppleSharedIPadCheckInServlet";
        this.handleMultiUserTokenUpdate(hashPlist, strData, enrollmentRequestId, isAppleConfig, messageType);
    }
}
