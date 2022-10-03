package com.me.mdm.agent.servlets.ios;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.webclient.mdm.IOSServerServlet;

public class AppleTVServlet extends IOSServerServlet
{
    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        AppleTVServlet.logger.log(Level.INFO, "AppleTVServlet => (PUT) Received request from APPLE ");
        AppleTVServlet.MDM_DEVICE_DATA_LOGGER.log(Level.INFO, "AppleTVServerServlet => (PUT) Received request from APPLE ");
        super.doPut(request, response, deviceRequest);
    }
}
