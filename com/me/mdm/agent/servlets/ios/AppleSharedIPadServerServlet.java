package com.me.mdm.agent.servlets.ios;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AppleSharedIPadServerServlet extends MacServerServlet
{
    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.className = "AppleSharedIPadServerServlet";
        this.handleMultiUserServerServlet(request, response, deviceRequest);
    }
}
