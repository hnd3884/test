package com.adventnet.authentication.saml;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.adventnet.authentication.saml.util.SamlUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class SamlLogoutResponseServlet extends HttpServlet
{
    private static final Logger LOGGER;
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final Auth auth = new Auth(SamlUtil.getSamlSettings(), request, response);
        auth.processSLO();
        if (request.getParameter("SAMLRequest") != null) {
            return;
        }
        response.sendRedirect(SamlUtil.getContextPath(request));
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            SamlLogoutResponseServlet.LOGGER.log(Level.SEVERE, "Exception occurred while logout request/response handling.. redirecting to home page ");
            response.sendRedirect(SamlUtil.getContextPath(request));
        }
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            SamlLogoutResponseServlet.LOGGER.log(Level.SEVERE, "Exception occurred while logout response handling.. redirecting to home page ");
            response.sendRedirect(SamlUtil.getContextPath(request));
        }
    }
    
    static {
        LOGGER = Logger.getLogger(SamlLogoutResponseServlet.class.getName());
    }
}
