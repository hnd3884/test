package com.me.mdm.agent.servlets.windows;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.iam.security.SecurityUtil;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

public class WpTermsOfUseServlet extends DeviceRequestServlet
{
    public Logger logger;
    public static final String REDIRECT_URI = "redirect_uri";
    
    public WpTermsOfUseServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.logger.log(Level.INFO, "============================================================================");
        this.logger.log(Level.INFO, "WPTermsOfUse (GET) Received Data: {0}", SecurityUtil.getRequestPath(request));
        this.logger.log(Level.INFO, "============================================================================");
        final String redirectUrl = request.getParameter("redirect_uri");
        if ((redirectUrl.contains("Microsoft.AAD.BrokerPlugin") || redirectUrl.contains("microsoftonline") || redirectUrl.contains("CloudDomainJoin")) && (redirectUrl.startsWith("ms-appx-web://Microsoft.AAD.BrokerPlugin/") || redirectUrl.equals("ms-appx-web://Microsoft.AAD.BrokerPlugin") || redirectUrl.startsWith("ms-aadj-redir://auth/mdm/") || redirectUrl.startsWith("https://login.microsoftonline.com/"))) {
            response.sendRedirect(redirectUrl + "?IsAccepted=true&OpaqueBlob=AzureADEnrollment");
        }
        else {
            this.logger.log(Level.INFO, "Redirect URL obtained is ''{0}''. Sending 404 error response as redirect URL does not contain AAD Broker Plugin app name.", redirectUrl);
            response.sendError(400);
        }
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }
}
