package com.me.mdm.agent.servlets;

import java.util.Enumeration;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Level;
import com.me.mdm.webclient.filter.AuthenticationHandlerUtil;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public abstract class DeviceRequestServlet extends HttpServlet
{
    protected DeviceRequest prepareDeviceRequest(final HttpServletRequest request, final Logger logger) throws IOException, Exception {
        try {
            DeviceRequest devicerequest = null;
            devicerequest = (DeviceRequest)request.getAttribute("DeviceRequest");
            if (devicerequest == null && !MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.getServletPath())) {
                devicerequest = AuthenticationHandlerUtil.prepareDeviceRequest(request, Logger.getLogger("MDMDeviceDataLogger"));
            }
            return devicerequest;
        }
        catch (final Exception ex) {
            logger.log(Level.WARNING, "Exception occured while reading request : {0}", ex);
            throw ex;
        }
    }
    
    protected HashMap getParameterValueMap(final HttpServletRequest request) {
        final HashMap parameterValueMap = new HashMap();
        final Enumeration enume = request.getParameterNames();
        while (enume.hasMoreElements()) {
            final String attrName = enume.nextElement();
            parameterValueMap.put(attrName, request.getParameter(attrName));
        }
        parameterValueMap.put("ServletPath", request.getServletPath());
        return parameterValueMap;
    }
    
    protected HashMap getHeaderValueMap(final HttpServletRequest request) {
        final HashMap headerValueMap = new HashMap();
        final Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            final String headerName = enume.nextElement();
            headerValueMap.put(headerName, request.getHeader(headerName));
        }
        return headerValueMap;
    }
}
