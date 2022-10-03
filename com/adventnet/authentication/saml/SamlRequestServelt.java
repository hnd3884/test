package com.adventnet.authentication.saml;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.adventnet.authentication.saml.util.SamlUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class SamlRequestServelt extends HttpServlet
{
    private static final Logger LOGGER;
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (!SamlUtil.isSamlEnabled()) {
            SamlRequestServelt.LOGGER.log(Level.INFO, "SAML authentication is disabled");
            request.getSession(false).setAttribute("SAML_errorCode", (Object)"SAML authentication is disabled");
            response.sendRedirect(SamlUtil.getContextPath(request));
            return;
        }
        final Auth auth = new Auth(SamlUtil.getSamlSettings(), request, response);
        auth.login();
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new ServletException("Exception during SAML authentication" + e);
        }
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new ServletException("Exception during SAML authentication" + e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(SamlRequestServelt.class.getName());
    }
}
