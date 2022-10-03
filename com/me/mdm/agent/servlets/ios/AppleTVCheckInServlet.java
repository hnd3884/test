package com.me.mdm.agent.servlets.ios;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.webclient.mdm.IOSCheckInServlet;

public class AppleTVCheckInServlet extends IOSCheckInServlet
{
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "AppleTVCheckInServlet => (POST) Received request from APPLE ");
        this.checkinLogger.log(Level.INFO, "AppleTVCheckInServlet => (POST) Received request from APPLE ");
        super.doPost(request, response, deviceRequest);
    }
    
    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "AppleTVCheckInServlet => (PUT) Received request from APPLE ");
        this.checkinLogger.log(Level.INFO, "AppleTVCheckInServlet => (PUT) Received request from APPLE ");
        super.doPut(request, response, deviceRequest);
    }
}
