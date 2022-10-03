package com.adventnet.authentication.filter;

import java.io.IOException;
import com.adventnet.authentication.Credential;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.authentication.util.AuthDBUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import com.adventnet.authentication.RoleAssociator;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class AssociateCredential implements Filter
{
    private static Logger logger;
    private static final String SSOID;
    private static RoleAssociator roleAssociator;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        AssociateCredential.logger.log(Level.FINER, "init invoked with Filter Config : {0}", filterConfig);
        String roleAssociatorClass = filterConfig.getInitParameter("DynamicRoleAssociator");
        if (roleAssociatorClass == null || roleAssociatorClass.isEmpty()) {
            roleAssociatorClass = "com.adventnet.authentication.DefaultRoleAssociator";
        }
        try {
            AssociateCredential.logger.log(Level.INFO, "roleAssociator :: " + roleAssociatorClass);
            AssociateCredential.roleAssociator = (RoleAssociator)Class.forName(roleAssociatorClass).newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new ServletException("Exception occurred while instantiating RoleAssociator class :: " + e.getMessage());
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        AssociateCredential.logger.log(Level.FINER, "doFilter invoked");
        String reqURI = null;
        final HttpServletRequest hreq = (HttpServletRequest)servletRequest;
        StringBuffer debugmsg = new StringBuffer("debug info : ");
        String sessionIdName = servletRequest.getServletContext().getSessionCookieConfig().getName();
        sessionIdName = ((sessionIdName != null) ? sessionIdName : "JSESSIONID");
        try {
            reqURI = hreq.getRequestURI();
            final Principal principal = hreq.getUserPrincipal();
            if (principal == null) {
                AssociateCredential.logger.log(Level.FINE, "unauthenticated request, valve ignored");
                filterChain.doFilter(servletRequest, servletResponse);
                debugmsg = null;
                return;
            }
            debugmsg.append("\n principal = " + principal);
            if (reqURI != null && (reqURI.endsWith(".png") || reqURI.endsWith(".jpg") || reqURI.endsWith(".css") || reqURI.endsWith(".js") || reqURI.endsWith(".gif") || reqURI.endsWith(".jpeg") || reqURI.endsWith(".bmp") || reqURI.endsWith(".html") || reqURI.endsWith(".eot"))) {
                AssociateCredential.logger.log(Level.FINE, "ignored for reqURI : {0}", reqURI);
                filterChain.doFilter(servletRequest, servletResponse);
                debugmsg = null;
                return;
            }
            debugmsg.append("\n reqURI = " + reqURI);
            final HttpSession httpSession = hreq.getSession();
            if (httpSession == null) {
                AssociateCredential.logger.log(Level.SEVERE, "httpSession obtained is null, ignoring credentialAssociation valve");
                AssociateCredential.logger.log(Level.FINE, "{0}", debugmsg.toString());
                filterChain.doFilter(servletRequest, servletResponse);
                debugmsg = null;
                return;
            }
            debugmsg.append("\n httpSession = " + httpSession);
            final String ssoId = (String)httpSession.getAttribute("JSESSIONIDSSO");
            if (ssoId == null) {
                AssociateCredential.logger.log(Level.SEVERE, "ssoId obtained is null, ignoring credentialAssociation valve");
                AssociateCredential.logger.log(Level.FINE, "{0}", debugmsg.toString());
                filterChain.doFilter(servletRequest, servletResponse);
                debugmsg = null;
                return;
            }
            debugmsg.append("\n ssoId = " + ssoId);
            final Credential credential = AuthDBUtil.constructCredential(hreq.getContextPath(), ssoId, hreq, AssociateCredential.roleAssociator.getDynamicRoles(hreq));
            if (credential != null && !credential.isEmpty()) {
                AssociateCredential.logger.log(Level.FINE, "credential constructed via ssoId is set in thread local");
                credential.setAuthRuleName((String)httpSession.getAttribute("AUTHRULE_NAME"));
                AuthUtil.setUserCredential(credential);
                httpSession.setAttribute("com.adventnet.authentication.Credential", (Object)credential);
                filterChain.doFilter(servletRequest, servletResponse);
                AuthUtil.flushCredentials();
                debugmsg = null;
                return;
            }
            debugmsg.append("\n credential constructed = " + credential);
            AssociateCredential.logger.log(Level.SEVERE, "unhandled case occured where credential could not be constructed");
            AssociateCredential.logger.log(Level.INFO, "{0}", debugmsg.toString());
            reqURI = hreq.getContextPath();
            httpSession.invalidate();
            AssociateCredential.logger.log(Level.WARNING, "session invalidated as credential could not be constructed, forwarding to servlet path : {0}", reqURI);
            final HttpServletResponse hres = (HttpServletResponse)servletResponse;
            AuthUtil.clearCookies(hreq, hres, new String[] { AssociateCredential.SSOID, sessionIdName });
            hres.sendRedirect(reqURI);
            debugmsg = null;
        }
        finally {
            AssociateCredential.logger.log(Level.FINE, "associateCredential filter returned for reqURI : {0}", reqURI);
        }
    }
    
    public void destroy() {
        AssociateCredential.logger.log(Level.INFO, "destroy invoked");
    }
    
    static {
        AssociateCredential.logger = Logger.getLogger(AssociateCredential.class.getName());
        SSOID = System.getProperty("org.apache.catalina.authenticator.Constants.SSO_SESSION_COOKIE_NAME", "JSESSIONIDSSO");
        AssociateCredential.roleAssociator = null;
    }
}
