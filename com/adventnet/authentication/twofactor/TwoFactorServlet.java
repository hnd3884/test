package com.adventnet.authentication.twofactor;

import java.util.Set;
import com.adventnet.authentication.realm.CustomJAASRealm;
import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.authentication.Credential;
import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import com.adventnet.authentication.util.AuthUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.security.Principal;
import com.adventnet.authentication.PAM;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class TwoFactorServlet extends HttpServlet
{
    private static Logger logger;
    private static final String FIRST_PAGE = "firstpage";
    private static final String SECOND_PAGE = "secondpage";
    private static final String SYSTEM = "System";
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("j_username");
        String domainname = request.getParameter(PAM.DOMAINNAME);
        final String password = request.getParameter("j_password");
        Principal principal = null;
        final Principal principalCreated = (Principal)request.getSession().getAttribute("2FactorPrincipal");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html");
        if (principalCreated != null) {
            RequestDispatcher rd = null;
            if (username != null && !username.equalsIgnoreCase(principalCreated.getName())) {
                request.getSession().setAttribute("2FactorPrincipal", (Object)null);
                rd = request.getRequestDispatcher(this.getInitParameter("firstpage").toString());
                rd.forward((ServletRequest)request, (ServletResponse)response);
            }
            final Long userId = Long.valueOf(request.getSession().getAttribute("userId").toString());
            if (request.getParameter("resendOTP") != null && Boolean.valueOf(request.getParameter("resendOTP").toString())) {
                this.handleTwoFactorAuth(request, response, userId);
            }
            rd = request.getRequestDispatcher(this.getInitParameter("secondpage").toString());
            rd.include((ServletRequest)request, (ServletResponse)response);
        }
        else {
            if ("true".equals(request.getParameter("ntlm"))) {
                boolean ntlmauth = false;
                try {
                    request.setAttribute("IGNORE_TFA", (Object)"true");
                    ntlmauth = AuthUtil.tryNtlmAuthentication(request, response);
                    if ("true".equals(request.getAttribute("NTLMCOMM"))) {
                        return;
                    }
                    if (!ntlmauth) {
                        request.setAttribute("ERROR", (Object)"NTLM failed");
                        request.getSession().setAttribute("NTLMAUTH", (Object)"failed");
                        final RequestDispatcher trd = request.getRequestDispatcher(this.getInitParameter("firstpage").toString());
                        trd.forward((ServletRequest)request, (ServletResponse)response);
                        return;
                    }
                    username = request.getAttribute("userName").toString();
                    domainname = request.getAttribute("domainName").toString();
                    request.getSession().setAttribute("NTLMAUTH", (Object)"success");
                    if ("domain.backslash.username".equals(System.getProperty("ADUserNameSyntax"))) {
                        principal = PAM.login(domainname + "\\" + username, "System", request);
                    }
                    else {
                        principal = PAM.login(username, "System", request);
                    }
                    request.getSession().setAttribute("2FactorPrincipal", (Object)principal);
                    request.getSession().setAttribute("NTLMAUTH", (Object)"success");
                    request.getSession().setAttribute("username", (Object)username);
                    request.getSession().setAttribute("domainname", (Object)domainname);
                }
                catch (final Exception e) {
                    TwoFactorServlet.logger.log(Level.INFO, "Exception occured while trying to ntlm authenticate");
                    e.printStackTrace();
                }
            }
            else if ("true".equals(request.getParameter("ntlmv2"))) {
                try {
                    request.setAttribute("IGNORE_TFA", (Object)"true");
                    final String nv2UserName = username = request.getSession().getAttribute("NtlmUserName").toString();
                    final String nv2DomainName = domainname = request.getSession().getAttribute("NtlmDomainName").toString();
                    request.setAttribute("domainName", (Object)nv2DomainName);
                    if ("domain.backslash.username".equals(System.getProperty("ADUserNameSyntax"))) {
                        principal = PAM.login(nv2DomainName + "\\" + nv2UserName, "System", request);
                    }
                    else {
                        principal = PAM.login(nv2UserName, "System", request);
                    }
                    request.getSession().setAttribute("2FactorPrincipal", (Object)principal);
                    request.getSession().setAttribute("NTLMAUTH", (Object)"success");
                    request.getSession().setAttribute("username", (Object)username);
                    request.getSession().setAttribute("domainname", (Object)domainname);
                }
                catch (final Exception exp) {
                    request.getSession().setAttribute("NTLMPAMEXCEPTION", (Object)"true");
                    exp.printStackTrace();
                }
            }
            else if ("true".equals(request.getParameter("smartcard"))) {
                try {
                    request.setAttribute("IGNORE_TFA", (Object)"true");
                    final String smartCardUserName = username = request.getSession().getAttribute("SmartCardUserName").toString();
                    principal = PAM.login(smartCardUserName, "System", request);
                    request.getSession().setAttribute("2FactorPrincipal", (Object)principal);
                    request.getSession().setAttribute("SMARTCARDAUTH", (Object)"success");
                    request.getSession().setAttribute("username", (Object)username);
                    request.getSession().setAttribute("domainname", (Object)domainname);
                }
                catch (final Exception exp) {
                    request.getSession().setAttribute("SMARTCARDPAMEXCEPTION", (Object)"true");
                    exp.printStackTrace();
                }
            }
            else if (password != null && !password.isEmpty()) {
                final String csrfPreventionSaltFromSession = (String)request.getSession().getAttribute("loginPageCsrfPreventionSalt");
                final String csrfPreventionSaltFromRequest = request.getParameter("loginPageCsrfPreventionSalt");
                if (csrfPreventionSaltFromSession == null || csrfPreventionSaltFromRequest == null || !csrfPreventionSaltFromSession.equals(csrfPreventionSaltFromRequest)) {
                    TwoFactorServlet.logger.log(Level.INFO, "Login CSRF token validation failed");
                    response.sendError(403, "Login CSRF token validation failed");
                    return;
                }
                try {
                    request.setAttribute("IGNORE_TFA", (Object)"true");
                    request.getSession().setAttribute("username", (Object)username);
                    request.getSession().setAttribute("domainname", (Object)domainname);
                    final Subject subject = PAM.login(username, password, "System", request);
                    if (subject == null) {
                        throw new LoginException("Login failed for user : " + username + " as subject obtained is null");
                    }
                    final Credential cr = this.getCredential(subject);
                    if (cr != null) {
                        TwoFactorServlet.logger.log(Level.FINEST, "credential obj set as attribute to request");
                        request.getSession().setAttribute("com.adventnet.authentication.Credential", (Object)cr);
                    }
                    principal = this.createPrincipal(username, subject);
                    request.getSession().setAttribute("2FactorPrincipal", (Object)principal);
                    request.getSession().setAttribute("LOCALAUTH", (Object)"success");
                    request.getSession().setAttribute("org.apache.catalina.session.PASSWORD", (Object)password);
                }
                catch (final LoginException e2) {
                    e2.printStackTrace();
                    request.getSession().setAttribute("login_status", (Object)e2.getMessage());
                    final RequestDispatcher reqDisp = request.getRequestDispatcher(this.getInitParameter("firstpage").toString());
                    reqDisp.forward((ServletRequest)request, (ServletResponse)response);
                    return;
                }
            }
            Long userId2 = null;
            try {
                username = (String)((request.getSession().getAttribute("username") != null) ? request.getSession().getAttribute("username") : username);
                domainname = (String)((request.getSession().getAttribute("domainname") != null) ? request.getSession().getAttribute("domainname") : domainname);
                if (domainname != null) {
                    try {
                        if ("domain.backslash.username".equals(System.getProperty("ADUserNameSyntax")) && !username.contains("\\")) {
                            userId2 = AuthUtil.getUserId(domainname + "\\" + username, domainname);
                        }
                        else {
                            userId2 = AuthUtil.getUserId(username, domainname);
                        }
                    }
                    catch (final Exception e) {
                        request.setAttribute("ERROR", (Object)"Error Occured kindly retry");
                        request.getSession().setAttribute("NTLMAUTH", (Object)"failed");
                        final RequestDispatcher trd2 = request.getRequestDispatcher(this.getInitParameter("firstpage").toString());
                        trd2.forward((ServletRequest)request, (ServletResponse)response);
                    }
                }
                else {
                    userId2 = AuthUtil.getUserId(username);
                }
            }
            catch (final Exception e) {
                userId2 = null;
            }
            RequestDispatcher rd2 = null;
            try {
                if (username == null || userId2 == null) {
                    request.setAttribute("ERROR", (Object)"Error Occured kindly retry");
                    request.getSession().setAttribute("NTLMAUTH", (Object)"failed");
                    rd2 = request.getRequestDispatcher(this.getInitParameter("firstpage").toString());
                    throw new Exception("No account for this user");
                }
                request.getSession().setAttribute("userId", (Object)userId2);
                if (AuthUtil.isTwofactorLoginEnabled(userId2)) {
                    final boolean handle = this.handleTwoFactorAuth(request, response, userId2);
                    if (handle) {
                        rd2 = request.getRequestDispatcher(this.getInitParameter("secondpage").toString());
                        rd2.include((ServletRequest)request, (ServletResponse)response);
                    }
                    else {
                        request.getSession().setAttribute("skipTwoFactor", (Object)true);
                        String url = "j_security_check?j_username=" + username;
                        if (domainname != null) {
                            url = url + "&domainName=" + domainname;
                        }
                        response.sendRedirect(url);
                    }
                }
                else if (principal == null && System.getProperty("2factor.auth") != null) {
                    rd2 = request.getRequestDispatcher(this.getInitParameter("secondpage").toString());
                    rd2.include((ServletRequest)request, (ServletResponse)response);
                }
                else {
                    if (principal == null) {
                        throw new Exception("two_factor_auth request came for a user who dont have two factor enabled");
                    }
                    request.getSession().setAttribute("2FactorPrincipal", (Object)null);
                    request.getSession().setAttribute("2FactorPrincipalRedirect", (Object)principal);
                    response.sendRedirect("j_security_check?ntlm=redirect&twofactor=disabled");
                }
            }
            catch (final Exception e3) {
                TwoFactorServlet.logger.log(Level.SEVERE, "Exception Occured" + e3.getMessage());
                rd2 = request.getRequestDispatcher(this.getInitParameter("firstpage").toString());
                rd2.include((ServletRequest)request, (ServletResponse)response);
            }
        }
    }
    
    private Principal createPrincipal(final String username, final Subject subject) {
        final CustomJAASRealm realm = new CustomJAASRealm();
        final Principal principal = realm.createPrincipal(username, subject);
        TwoFactorServlet.logger.log(Level.SEVERE, "principal object constructed for loginName : {0} is : {1}", new Object[] { "*****", principal });
        if (principal == null) {
            TwoFactorServlet.logger.log(Level.SEVERE, "authentication failed for user (principal is null) : {0}", username);
            return null;
        }
        return principal;
    }
    
    private Credential getCredential(final Subject subject) {
        final Set set = subject.getPublicCredentials(Credential.class);
        Credential cr = null;
        if (set != null) {
            final Object[] objArr = set.toArray();
            cr = (Credential)((objArr.length > 0) ? objArr[0] : null);
        }
        return cr;
    }
    
    public boolean handleTwoFactorAuth(final HttpServletRequest request, final HttpServletResponse response, final Long userId) {
        boolean handle = true;
        try {
            handle = ((TwoFactorAuth)AuthUtil.getTwoFactorImpl(userId)).handle(userId, (ServletRequest)request, (ServletResponse)response);
        }
        catch (final NullPointerException e) {
            TwoFactorServlet.logger.log(Level.WARNING, "Two factor implementation class is null");
        }
        catch (final Exception e2) {
            TwoFactorServlet.logger.log(Level.SEVERE, e2.getMessage());
        }
        return handle;
    }
    
    public void destroy() {
    }
    
    static {
        TwoFactorServlet.logger = Logger.getLogger(TwoFactorServlet.class.getName());
    }
}
