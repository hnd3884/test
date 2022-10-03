package com.adventnet.authentication.saml;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.authentication.saml.util.SamlUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class SamlLogoutRequestServlet extends HttpServlet
{
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (request.getUserPrincipal() != null) {
            final Auth auth = new Auth(SamlUtil.getSamlSettings(), request, response);
            auth.logout();
        }
        else {
            response.sendRedirect(SamlUtil.getContextPath(request));
        }
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
}
