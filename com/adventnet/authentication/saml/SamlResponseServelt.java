package com.adventnet.authentication.saml;

import java.io.IOException;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.authentication.util.AuthUtil;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.adventnet.authentication.saml.util.SamlUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class SamlResponseServelt extends HttpServlet
{
    private static final Logger LOGGER;
    private static String contextPath;
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        SamlResponseServelt.contextPath = SamlUtil.getContextPath(request);
        if (!SamlUtil.isSamlEnabled()) {
            SamlResponseServelt.LOGGER.log(Level.INFO, "SAML authentication is disabled");
            request.getSession(false).setAttribute("SAML_errorCode", (Object)"SAML authentication is disabled");
            response.sendRedirect(SamlResponseServelt.contextPath);
        }
        final Auth auth = new Auth(SamlUtil.getSamlSettings(), request, response);
        try {
            String redirectURL = "";
            String domain = null;
            String name = null;
            auth.processResponse();
            if (auth.isAuthenticated()) {
                SamlResponseServelt.LOGGER.log(Level.INFO, "User has been Authenticated Successfully");
                request.getSession(false).setAttribute("saml", (Object)"true");
                request.getSession(false).setAttribute("login_status", (Object)"success");
                name = auth.getNameId();
                redirectURL = "j_security_check?";
                if (name.contains("\\")) {
                    domain = name.substring(0, name.indexOf(92));
                    name = name.substring(name.indexOf(92) + 1);
                    redirectURL = redirectURL + "j_username=" + name + "&domain=" + domain + "&j_password=dummy";
                }
                else {
                    redirectURL = redirectURL + "j_username=" + name + "&j_password=dummy";
                }
                final int loggedInUser = this.checkIfUserAlreadyLoggedIn(name, domain, request);
                if (loggedInUser == 1) {
                    SamlResponseServelt.LOGGER.log(Level.INFO, "User already authenticated");
                    response.sendRedirect(SamlResponseServelt.contextPath);
                    return;
                }
                if (loggedInUser == 2) {
                    request.getSession().invalidate();
                }
                if (this.validateUserAccount(name, domain, request)) {
                    request.getSession().setAttribute("saml", (Object)"true");
                    request.getSession(false).setAttribute("login_status", (Object)"success");
                    response.sendRedirect(redirectURL);
                }
                else {
                    SamlResponseServelt.LOGGER.log(Level.SEVERE, "User account validation failed.");
                    request.getSession(false).setAttribute("login_status", (Object)"failed");
                    request.getSession(false).setAttribute("skipSAML", (Object)"true");
                    response.sendRedirect(SamlResponseServelt.contextPath);
                    request.getSession(false).setAttribute("ERROR_MESSAGE", (Object)"User account validation failed.");
                }
            }
            else {
                final int loggedInUser = this.checkIfUserAlreadyLoggedIn(name, domain, request);
                if (loggedInUser != 0) {
                    request.getSession().invalidate();
                    request.getSession(true);
                }
                SamlResponseServelt.LOGGER.log(Level.WARNING, "Authentication failed");
                request.getSession(false).setAttribute("login_status", (Object)"failed");
                request.getSession(false).setAttribute("skipSAML", (Object)"true");
                request.getSession(false).setAttribute("ERROR_MESSAGE", (Object)"Authentication failed");
                response.sendRedirect(SamlResponseServelt.contextPath);
            }
        }
        catch (final Exception e) {
            SamlResponseServelt.LOGGER.log(Level.WARNING, "Authentication failed for user :: " + auth.getNameId());
            request.getSession(false).setAttribute("login_status", (Object)"failed");
            request.getSession(false).setAttribute("ERROR_MESSAGE", (Object)e.getMessage());
            request.getSession(false).setAttribute("skipSAML", (Object)"true");
            if (e instanceof SamlException) {
                request.getSession(false).setAttribute("SAML_errorCode", (Object)((SamlException)e).getErrorCode());
            }
            response.sendRedirect(SamlResponseServelt.contextPath);
            throw new ServletException("Exception occurred while processing response", (Throwable)e);
        }
    }
    
    private int checkIfUserAlreadyLoggedIn(final String userName, final String domain, final HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return 0;
        }
        if (request.getUserPrincipal().getName().equalsIgnoreCase(userName) && domain != null && domain.equalsIgnoreCase(AuthUtil.getUserCredential().getDomainName())) {
            return 1;
        }
        return 2;
    }
    
    protected boolean validateUserAccount(final String name, final String domainName, final HttpServletRequest request) throws Exception {
        final DataObject accDO = AuthDBUtil.getAccountDO(name, "System", domainName);
        if (accDO.isEmpty()) {
            final String defaultRole = this.getInitParameter("DEFAULT_ROLE");
            if (defaultRole == null || defaultRole.isEmpty()) {
                SamlResponseServelt.LOGGER.log(Level.SEVERE, "User validated successfully from IDP but no such user is configured in our system");
                return false;
            }
            AuthDBUtil.addAccountDO(name, domainName, defaultRole);
            SamlResponseServelt.LOGGER.log(Level.FINE, "user successfully created");
        }
        else {
            SamlResponseServelt.LOGGER.log(Level.FINE, "user already exists, hence going to create a local session");
        }
        return true;
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
        LOGGER = Logger.getLogger(SamlResponseServelt.class.getName());
        SamlResponseServelt.contextPath = null;
    }
}
