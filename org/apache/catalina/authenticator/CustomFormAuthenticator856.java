package org.apache.catalina.authenticator;

import com.adventnet.authentication.twofactor.TwoFactorAuth;
import org.apache.catalina.connector.Response;
import org.apache.catalina.Realm;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.buf.CharChunk;
import com.adventnet.authentication.PAM;
import com.adventnet.authentication.util.AuthUtil;
import java.security.Principal;
import java.io.IOException;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.catalina.connector.Request;
import org.apache.catalina.Session;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class CustomFormAuthenticator856 extends FormAuthenticator
{
    private static final Logger LOGGER;
    private Method associateSsoSessionMethod;
    private Method setRequestInThreadLocalMethod;
    
    public CustomFormAuthenticator856() {
        this.associateSsoSessionMethod = null;
        this.setRequestInThreadLocalMethod = null;
    }
    
    public static void handleNtlmRedirect(final HttpServletResponse hres, final String requestURI, final boolean success) {
        final String url = hres.encodeRedirectURL(requestURI);
        try {
            hres.setStatus(401);
            final PrintWriter out = hres.getWriter();
            out.println("<html>");
            out.println("<head>");
            if (success) {
                out.println("<title>NTLM Success you are authenticated.</title>");
            }
            else {
                out.println("<title>NTLM Failure you are being redirected.</title>");
            }
            out.println("<script language=\"javascript\">");
            out.println("<!--");
            out.println("location.replace(\"" + url + "\");");
            out.println("//-->");
            out.println("</script>");
            out.println("<noscript>");
            out.println("<meta http-equiv=\"Refresh\" content=\"0; URL=" + url.toString() + "\">");
            out.println("</noscript>");
            out.println("</head>");
            out.println("<body>");
            if (success) {
                out.println("You were logged in successfully.  Click <a href=\"" + url.toString() + "\">here</a> to continue.");
            }
            else {
                out.println("NTLM failed.  Click <a href=\"" + url.toString() + "\">here</a> to continue.");
            }
            out.println("</body>");
            out.println("</html>");
            hres.flushBuffer();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createInstance() {
        Class authDbUtilClass = null;
        Class customJassRealmClass = null;
        try {
            if (this.associateSsoSessionMethod == null) {
                authDbUtilClass = Class.forName("com.adventnet.authentication.util.AuthDBUtil");
                final Class[] params = { Class.forName("java.lang.String"), Class.forName("com.adventnet.authentication.Credential") };
                this.associateSsoSessionMethod = authDbUtilClass.getDeclaredMethod("associateSsoSession", (Class[])params);
            }
            if (this.setRequestInThreadLocalMethod == null) {
                customJassRealmClass = Class.forName("com.adventnet.authentication.realm.CustomJAASRealm");
                final Class[] params = { Class.forName("javax.servlet.http.HttpServletRequest") };
                this.setRequestInThreadLocalMethod = customJassRealmClass.getDeclaredMethod("setRequestInThreadLocal", (Class[])params);
            }
        }
        catch (final Exception e) {
            CustomFormAuthenticator856.LOGGER.log(Level.SEVERE, "Exception occured while creating method instance for associateSsoSession and setRequestInThreadLocal", e);
        }
    }
    
    private void setRequestInThreadLocal(final HttpServletRequest httpServletRequest) {
        if (this.setRequestInThreadLocalMethod == null) {
            this.createInstance();
        }
        try {
            CustomFormAuthenticator856.LOGGER.log(Level.FINEST, "invoking method setRequestInThreadLocal with params");
            final Object[] args = { httpServletRequest };
            this.setRequestInThreadLocalMethod.invoke(null, args);
        }
        catch (final Exception e) {
            CustomFormAuthenticator856.LOGGER.log(Level.SEVERE, "Exception occured while setting request in threadLocal : ", e);
        }
    }
    
    private void associateSsoSession(final String key, final Object value) {
        CustomFormAuthenticator856.LOGGER.log(Level.FINEST, "associateCredential invoked with Key : {0} and value : {1}", new Object[] { key, value });
        if (this.associateSsoSessionMethod == null) {
            this.createInstance();
        }
        try {
            final Object[] args = { key, value };
            CustomFormAuthenticator856.LOGGER.log(Level.FINEST, "invoking method addUserCredential with params");
            this.associateSsoSessionMethod.invoke(null, args);
        }
        catch (final Exception e) {
            CustomFormAuthenticator856.LOGGER.log(Level.SEVERE, "Exception caught while tring to associateSsoId : {0}", e);
        }
    }
    
    protected void associate(final String ssoId, final Session session) {
        super.associate(ssoId, session);
        session.getSession().setAttribute("JSESSIONIDSSO", (Object)ssoId);
    }
    
    protected boolean doAuthenticate(final Request request, final HttpServletResponse response) throws IOException {
        final HttpServletRequest hreq = request.getRequest();
        Session session = null;
        boolean principalCreated = false;
        Principal twoFactorNtlmCachedPrincipal = null;
        Principal principal = request.getUserPrincipal();
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);
        String ssoId = (String)request.getNote("org.apache.catalina.request.SSOID");
        if (this.matchRequest(request)) {
            session = request.getSessionInternal(true);
            CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Restore request from session '" + session.getIdInternal() + "'");
            if (ssoId != null) {
                this.associate(ssoId, session);
                final Object credential = hreq.getSession().getAttribute("com.adventnet.authentication.Credential");
                this.associateSsoSession(ssoId, credential);
            }
            if (this.restoreRequest(request, session)) {
                CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Proceed to restored request");
                return true;
            }
            CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Restore of original request failed");
            response.sendError(400);
            return false;
        }
        else {
            if (this.checkForCachedAuthentication(request, response, true)) {
                if (ssoId == null) {
                    session = request.getSessionInternal(true);
                    final Object cred = session.getSession().getAttribute("com.adventnet.authentication.Credential");
                    if (cred != null) {
                        this.register(request, response, principal, "FORM", (String)null, (String)null);
                        ssoId = (String)request.getNote("org.apache.catalina.request.SSOID");
                        CustomFormAuthenticator856.LOGGER.log(Level.FINEST, "credential obtained for ssoid : {0} is : {1}", new Object[] { ssoId, cred });
                        if (ssoId != null) {
                            this.associateSsoSession(ssoId, cred);
                            this.associate(ssoId, session);
                        }
                    }
                }
                hreq.getSession().setAttribute("JSESSIONIDSSO", (Object)ssoId);
                return true;
            }
            final MessageBytes uriMB = MessageBytes.newInstance();
            final CharChunk uriCC = uriMB.getCharChunk();
            uriCC.setLimit(-1);
            final String contextPath = request.getContextPath();
            String requestURI = request.getDecodedRequestURI();
            final boolean loginAction = requestURI.startsWith(contextPath) && requestURI.endsWith("/j_security_check");
            final LoginConfig config = this.context.getLoginConfig();
            if (!loginAction) {
                if (request.getServletPath().length() == 0 && request.getPathInfo() == null) {
                    final StringBuilder location = new StringBuilder(requestURI);
                    location.append('/');
                    if (request.getQueryString() != null) {
                        location.append('?');
                        location.append(request.getQueryString());
                    }
                    response.sendRedirect(response.encodeRedirectURL(location.toString()));
                    return false;
                }
                session = request.getSessionInternal(true);
                CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Save request in session '" + session.getIdInternal() + "'");
                try {
                    this.saveRequest(request, session);
                }
                catch (final IOException ioe) {
                    CustomFormAuthenticator856.LOGGER.log(Level.INFO, "Request body too big to save during authentication");
                    response.sendError(403, CustomFormAuthenticator856.sm.getString("authenticator.requestBodyTooBig"));
                    return false;
                }
                this.forwardToLoginPage(request, response, config);
                return false;
            }
            else {
                request.getResponse().sendAcknowledgement();
                final Realm realm = this.context.getRealm();
                if (this.characterEncoding != null) {
                    request.setCharacterEncoding(this.characterEncoding);
                }
                else {
                    request.setCharacterEncoding("UTF-8");
                }
                if ("UTF-8".equals(hreq.getSession().getServletContext().getInitParameter("PARAMETER-ENCODING"))) {
                    hreq.setCharacterEncoding("UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("text/html; charset=utf-8; encoding=UTF-8");
                }
                if (principal == null && hreq.getSession().getAttribute("2FactorPrincipal") != null) {
                    twoFactorNtlmCachedPrincipal = (Principal)hreq.getSession().getAttribute("2FactorPrincipal");
                    final String username = (String)hreq.getSession().getAttribute("username");
                    if (username == null) {
                        throw new IOException("UserName cannot be null");
                    }
                    if (hreq.getSession().getAttribute("skipTwoFactor") != null && "true".equals(hreq.getSession().getAttribute("skipTwoFactor").toString())) {
                        CustomFormAuthenticator856.LOGGER.log(Level.SEVERE, "skiptwofactor", "");
                    }
                    else if (!this.validateTwoFactorAuth(username, hreq, response)) {
                        return false;
                    }
                    principalCreated = true;
                }
                final String username = request.getParameter("j_username");
                final String password = request.getParameter("j_password");
                CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Authenticating username ***** ");
                this.setRequestInThreadLocal(hreq);
                if (principal == null && "redirect".equals(hreq.getParameter("ntlm"))) {
                    twoFactorNtlmCachedPrincipal = (principal = (Principal)hreq.getSession().getAttribute("2FactorPrincipalRedirect"));
                    principalCreated = true;
                }
                if ("true".equals(hreq.getParameter("ntlm"))) {
                    try {
                        final boolean ntlmauth = AuthUtil.tryNtlmAuthentication(hreq, response);
                        if ("true".equals(hreq.getAttribute("NTLMCOMM"))) {
                            hreq.getSession().setAttribute("NTLMAUTH", (Object)"failed");
                            return false;
                        }
                        if (!ntlmauth) {
                            principalCreated = false;
                        }
                        else {
                            if ("domain.backslash.username".equals(System.getProperty("ADUserNameSyntax"))) {
                                principal = PAM.login(hreq.getAttribute("domainName") + "\\" + hreq.getAttribute("userName"), "System", hreq);
                            }
                            else {
                                principal = PAM.login(hreq.getAttribute("userName").toString(), "System", hreq);
                            }
                            request.setUserPrincipal(principal);
                            request.setAuthType("NTLM");
                            session = request.getSessionInternal(true);
                            if (session != null && session.getNote("org.apache.catalina.authenticator.SESSION_ID") == null) {
                                session.setNote("org.apache.catalina.authenticator.SESSION_ID", (Object)request.getRequestedSessionId());
                            }
                            principalCreated = true;
                        }
                    }
                    catch (final Exception exp) {
                        exp.printStackTrace();
                    }
                }
                else if ("true".equals(hreq.getParameter("ntlmv2"))) {
                    try {
                        final String nv2UserName = hreq.getSession().getAttribute("NtlmUserName").toString();
                        final String nv2DomainName = hreq.getSession().getAttribute("NtlmDomainName").toString();
                        hreq.setAttribute("domainName", (Object)nv2DomainName);
                        if ("domain.backslash.username".equals(System.getProperty("ADUserNameSyntax"))) {
                            principal = PAM.login(nv2DomainName + "\\" + nv2UserName, "System", hreq);
                        }
                        else {
                            principal = PAM.login(nv2UserName, "System", hreq);
                        }
                        request.setUserPrincipal(principal);
                        request.setAuthType("NTLM");
                        session = request.getSessionInternal(true);
                        if (session != null && session.getNote("org.apache.catalina.authenticator.SESSION_ID") == null) {
                            session.setNote("org.apache.catalina.authenticator.SESSION_ID", (Object)request.getRequestedSessionId());
                        }
                        principalCreated = true;
                    }
                    catch (final Exception exp) {
                        hreq.setAttribute("NTLMAUTHEN", (Object)"false");
                        hreq.getSession().setAttribute("NTLMFAILED", (Object)"true");
                        hreq.getSession().setAttribute("nv2login", (Object)"false");
                        exp.printStackTrace();
                    }
                }
                else if ("true".equals(hreq.getParameter("smartcard"))) {
                    try {
                        final String smartcardusername = (String)hreq.getSession().getAttribute("SmartCardUserName");
                        if (smartcardusername != null) {
                            principal = PAM.login(smartcardusername, "System", hreq);
                            request.setUserPrincipal(principal);
                            request.setAuthType("SMARTCARD");
                            session = request.getSessionInternal(true);
                            if (session != null && session.getNote("org.apache.catalina.authenticator.SESSION_ID") == null) {
                                session.setNote("org.apache.catalina.authenticator.SESSION_ID", (Object)request.getRequestedSessionId());
                            }
                            principalCreated = true;
                        }
                    }
                    catch (final Exception e) {
                        hreq.getSession().setAttribute("SMARTCARDFAILED", (Object)"true");
                        hreq.getSession().setAttribute("SMARTCARDLOGIN", (Object)"failed");
                        e.printStackTrace();
                    }
                }
                else if ("true".equals(hreq.getSession(false).getAttribute("saml"))) {
                    try {
                        final String userInSession = (String)hreq.getSession(false).getAttribute("j_username");
                        final String domainInSession = (String)hreq.getSession(false).getAttribute(PAM.DOMAINNAME);
                        if (domainInSession != null) {
                            request.setAttribute("domainName", (Object)domainInSession);
                        }
                        principal = PAM.login(userInSession, "System", hreq);
                        request.setUserPrincipal(principal);
                        request.setAuthType("SAML");
                        session = request.getSessionInternal(true);
                        if (session != null && session.getNote("org.apache.catalina.authenticator.SESSION_ID") == null) {
                            session.setNote("org.apache.catalina.authenticator.SESSION_ID", (Object)request.getRequestedSessionId());
                        }
                        principalCreated = true;
                    }
                    catch (final Exception exp) {
                        hreq.setAttribute("SAMLAUTH", (Object)"false");
                        hreq.getSession().setAttribute("SAMLAUTH", (Object)"false");
                        exp.printStackTrace();
                    }
                }
                else if (!principalCreated) {
                    final String csrfPreventionSaltFromSession = (String)request.getSession().getAttribute("loginPageCsrfPreventionSalt");
                    final String csrfPreventionSaltFromRequest = request.getParameter("loginPageCsrfPreventionSalt");
                    if (csrfPreventionSaltFromSession == null || csrfPreventionSaltFromRequest == null || !csrfPreventionSaltFromSession.equals(csrfPreventionSaltFromRequest)) {
                        CustomFormAuthenticator856.LOGGER.log(Level.INFO, "Login CSRF token validation failed");
                        response.sendError(403, "Login CSRF token validation failed");
                        return false;
                    }
                    principal = realm.authenticate(username, password);
                }
                else {
                    principal = twoFactorNtlmCachedPrincipal;
                }
                if (principal == null) {
                    if ("false".equals(hreq.getAttribute("NTLMAUTHEN"))) {
                        String url = config.getLoginPage();
                        if (hreq.getSession().getAttribute("nv2loginpage") != null) {
                            url = hreq.getSession().getAttribute("nv2loginpage").toString();
                        }
                        handleNtlmRedirect(response, url, false);
                    }
                    else {
                        this.forwardToErrorPage(request, response, config);
                    }
                    return false;
                }
                CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Authentication of ***** was successful");
                if (session == null) {
                    session = request.getSessionInternal(false);
                }
                if (session == null) {
                    CustomFormAuthenticator856.LOGGER.log(Level.INFO, "User took so long to log on the session expired");
                    if (this.landingPage == null) {
                        response.sendError(408, CustomFormAuthenticator856.sm.getString("authenticator.sessionExpired"));
                    }
                    else {
                        final String uri = request.getContextPath() + this.landingPage;
                        final SavedRequest saved = new SavedRequest();
                        saved.setMethod("GET");
                        saved.setRequestURI(uri);
                        saved.setDecodedRequestURI(uri);
                        request.getSessionInternal(true).setNote("org.apache.catalina.authenticator.REQUEST", (Object)saved);
                        response.sendRedirect(response.encodeRedirectURL(uri));
                    }
                    return false;
                }
                this.register(request, response, principal, "FORM", username, password);
                if (this.landingPage == null || this.landingPage.isEmpty()) {
                    CustomFormAuthenticator856.LOGGER.info("Setting landing page to home page if requested page could not be retreived or null");
                    this.setLandingPage("/");
                }
                requestURI = this.savedRequestURL(session);
                CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Redirecting to original '" + requestURI + "'");
                CustomFormAuthenticator856.LOGGER.log(Level.FINE, "Flushing creds");
                AuthUtil.flushCredentials();
                if (requestURI == null || requestURI.isEmpty()) {
                    if (this.landingPage == null) {
                        response.sendError(400, CustomFormAuthenticator856.sm.getString("authenticator.formlogin"));
                    }
                    else {
                        final String uri = request.getContextPath() + this.landingPage;
                        final SavedRequest saved = new SavedRequest();
                        saved.setMethod("GET");
                        saved.setRequestURI(uri);
                        saved.setDecodedRequestURI(uri);
                        session.setNote("org.apache.catalina.authenticator.REQUEST", (Object)saved);
                        response.sendRedirect(response.encodeRedirectURL(uri));
                    }
                }
                else {
                    hreq.getSession().setAttribute("2FactorPrincipal", (Object)null);
                    if (hreq.getAttribute("NTLMAUTHEN") == null) {
                        final Response internalResponse = request.getResponse();
                        final String location2 = response.encodeRedirectURL(requestURI);
                        if ("HTTP/1.1".equals(request.getProtocol())) {
                            internalResponse.sendRedirect(location2, 303);
                        }
                        else {
                            internalResponse.sendRedirect(location2, 302);
                        }
                    }
                    else {
                        if (hreq.getAttribute("NTLMAUTHEN").equals("false")) {
                            handleNtlmRedirect(response, requestURI, false);
                            return true;
                        }
                        handleNtlmRedirect(response, requestURI, true);
                        return true;
                    }
                }
                return false;
            }
        }
    }
    
    public boolean validateTwoFactorAuth(final String loginName, final HttpServletRequest request, final HttpServletResponse response) {
        Long userId = null;
        try {
            final String domainName = (String)request.getSession().getAttribute("domainname");
            if (domainName != null) {
                if ("domain.backslash.username".equals(System.getProperty("ADUserNameSyntax")) && !loginName.contains("\\")) {
                    userId = AuthUtil.getUserId(domainName + "\\" + loginName, domainName);
                }
                else {
                    userId = AuthUtil.getUserId(loginName, domainName);
                }
            }
            else {
                userId = AuthUtil.getUserId(loginName);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (System.getProperty("2factor.auth") == null && !AuthUtil.isTwofactorLoginEnabled(userId)) {
                return true;
            }
        }
        catch (final Exception e) {
            CustomFormAuthenticator856.LOGGER.log(Level.SEVERE, "No details for AaaUserTwoFactorDetails table for user " + loginName);
            return true;
        }
        boolean ret = false;
        try {
            final TwoFactorAuth userAuthImpl = (TwoFactorAuth)AuthUtil.getTwoFactorImpl(userId);
            ret = userAuthImpl.validate(userId, request, response);
        }
        catch (final Exception e2) {
            CustomFormAuthenticator856.LOGGER.log(Level.SEVERE, "Exception while instantiating Two Factor implementation class or No details for AaaUserTwoFactorDetails table for user " + loginName);
            return ret;
        }
        return ret;
    }
    
    static {
        LOGGER = Logger.getLogger(CustomFormAuthenticator856.class.getName());
    }
}
