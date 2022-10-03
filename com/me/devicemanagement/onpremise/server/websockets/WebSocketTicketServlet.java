package com.me.devicemanagement.onpremise.server.websockets;

import java.io.IOException;
import java.io.PrintWriter;
import com.me.devicemanagement.framework.server.websockets.SocketAdapterConfManager;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class WebSocketTicketServlet extends HttpServlet
{
    private static Logger wsFrameworkLogger;
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        WebSocketTicketServlet.wsFrameworkLogger.log(Level.INFO, "################################ Inside WebSocketTicketServlet get method ######################### ");
        final String clientType = request.getParameter("clientType");
        final String paramName = SocketAdapterConfManager.getInstance().getParamName(clientType);
        final String paramValue = request.getParameter(paramName);
        WebSocketTicketServlet.wsFrameworkLogger.log(Level.INFO, "clientType : {0} , paramName : {1} , paramValue : {2} of servlet request", new Object[] { clientType, paramName, paramValue });
        if (clientType != null && paramName != null && paramValue != null) {
            final String ticket = WebSocketUtil.generateTicket(clientType, paramName, paramValue);
            response.setContentType("text/plain");
            final PrintWriter printWriter = response.getWriter();
            printWriter.print(ticket);
            printWriter.close();
            response.setStatus(200);
            WebSocketTicketServlet.wsFrameworkLogger.log(Level.INFO, "success 200 status set.");
        }
        else {
            WebSocketTicketServlet.wsFrameworkLogger.log(Level.SEVERE, "Request params are null");
            response.sendError(400);
        }
    }
    
    static {
        WebSocketTicketServlet.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
