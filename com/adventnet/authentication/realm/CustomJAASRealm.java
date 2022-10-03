package com.adventnet.authentication.realm;

import org.apache.catalina.Wrapper;
import com.adventnet.authentication.LoginHandler;
import java.util.Set;
import javax.servlet.http.HttpSession;
import com.adventnet.authentication.Credential;
import java.util.Map;
import javax.security.auth.Subject;
import java.util.Objects;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.AccountExpiredException;
import com.adventnet.authentication.PAM;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.authentication.util.AuthDBUtil;
import java.util.logging.Level;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import org.apache.catalina.realm.JAASRealm;

public class CustomJAASRealm extends JAASRealm
{
    private static Logger logger;
    protected static final String info = "com.adventnet.authentication.realm.CustomJAASRealm";
    protected static final String name = "CustomJAASRealm";
    private static ThreadLocal<HttpServletRequest> threadLocal;
    
    public CustomJAASRealm() {
        this.userClasses.add("com.adventnet.authentication.UserPrincipal");
        this.roleClasses.add("com.adventnet.authentication.RolePrincipal");
    }
    
    public Principal authenticate(String loginName, final String password) {
        final long startTime = System.currentTimeMillis();
        final HttpServletRequest request = CustomJAASRealm.threadLocal.get();
        CustomJAASRealm.logger.log(Level.FINEST, "httpServletReq obtained from threadLocal is : {0}", request);
        if (request == null) {
            CustomJAASRealm.logger.log(Level.SEVERE, "request object not set in thread local and hence unable to authenticate");
            return null;
        }
        if (loginName == null || loginName.equals("")) {
            CustomJAASRealm.logger.log(Level.INFO, "loginName cannot be null");
            request.setAttribute("login_status", (Object)"Login name cannot be null");
            return null;
        }
        if (password == null || password.equals("")) {
            CustomJAASRealm.logger.log(Level.INFO, "password cannot be null");
            request.setAttribute("login_status", (Object)"password cannot be null");
            return null;
        }
        Subject subject = null;
        String service = null;
        String domainName = null;
        try {
            final String contextPath = request.getContextPath();
            if (contextPath != null) {
                service = AuthDBUtil.getServiceNameForContext(contextPath);
                CustomJAASRealm.logger.log(Level.FINEST, "service name obtained for contextPath : {0} is {1}", new String[] { contextPath, service });
                if (service == null) {
                    CustomJAASRealm.logger.log(Level.WARNING, "service name obtained for context is null, using System as service name");
                    service = "System";
                }
            }
            else {
                CustomJAASRealm.logger.log(Level.FINEST, "contextpath obtained from request is null. using default service System");
                service = "System";
            }
            final Map<String, Object> userMap = AuthUtil.getRememberMeService().hasValidAuthToken(request);
            Label_0542: {
                if (userMap == null) {
                    try {
                        subject = PAM.login(loginName, password, service, request);
                        if (subject == null) {
                            CustomJAASRealm.logger.log(Level.INFO, "login failed for user : {0}", loginName);
                            request.setAttribute("login_status", (Object)"Authentication failed");
                            return null;
                        }
                        break Label_0542;
                    }
                    catch (final AccountExpiredException aee) {
                        CustomJAASRealm.logger.log(Level.FINEST, "AccountExpired for user : {0} with msg : {1}", new Object[] { loginName, aee.getMessage() });
                        request.setAttribute("login_status", (Object)aee.getMessage());
                        return null;
                    }
                    catch (final CredentialExpiredException cee) {
                        CustomJAASRealm.logger.log(Level.FINEST, "CredentialExpired for user : {0} with msg : {1}", new Object[] { loginName, cee.getMessage() });
                        request.setAttribute("login_status", (Object)cee.getMessage());
                        return null;
                    }
                    catch (final FailedLoginException fle) {
                        CustomJAASRealm.logger.log(Level.FINEST, "FailedLogin for user : {0} with msg : {1}", new Object[] { loginName, fle.getMessage() });
                        request.setAttribute("login_status", (Object)fle.getMessage());
                        return null;
                    }
                    catch (final LoginException le) {
                        CustomJAASRealm.logger.log(Level.FINEST, "LoginException occured for user : {0} with msg : {1}", new Object[] { loginName, le.getMessage() });
                        request.setAttribute("login_status", (Object)le.getMessage());
                        return null;
                    }
                    catch (final Exception e) {
                        CustomJAASRealm.logger.log(Level.SEVERE, "Unexpected error occured while login : ", e);
                        request.setAttribute("login_status", (Object)e.getMessage());
                        return null;
                    }
                }
                loginName = userMap.get("loginName");
                domainName = userMap.get("domainName");
                userMap.put("serviceName", service);
                subject = AuthUtil.getRememberMeService().constructSubject(subject, userMap, request);
            }
            final Credential credential = this.getCredential(subject);
            CustomJAASRealm.logger.log(Level.FINEST, "Credential obtained after authenticating : {0}", credential);
            if (credential != null) {
                CustomJAASRealm.logger.log(Level.FINEST, "credential obj set as attribute to request");
                request.getSession().setAttribute("com.adventnet.authentication.Credential", (Object)credential);
            }
            request.setAttribute("login_status", (Object)"Authenticated");
            final Principal principal = this.createPrincipal(loginName, subject, (LoginContext)null);
            CustomJAASRealm.logger.log(Level.FINEST, "principal object constructed for loginName : {0} is : {1}", new Object[] { "*****", principal });
            if (principal == null) {
                CustomJAASRealm.logger.log(Level.FINEST, "authentication failed for user : {0}", loginName);
                return null;
            }
            CustomJAASRealm.logger.log(Level.INFO, "successfully authenticated user : {0} in : {1}ms", new Object[] { "*****", new Long(System.currentTimeMillis() - startTime) });
            this.getLoginHandler(request).authenticate(request, subject, this.getCredential(subject));
            domainName = request.getParameter(PAM.DOMAINNAME);
            final HttpSession session = request.getSession();
            final Object isRememberMeEnabled = session.getAttribute("isRememberMeEnabled");
            if (Objects.equals(isRememberMeEnabled, true)) {
                session.setAttribute("UpdateRemCookie", (Object)true);
                session.removeAttribute("isRememberMeEnabled");
                session.setAttribute("authenticatedUserId", (Object)AuthUtil.getUserId(loginName, domainName));
            }
            return principal;
        }
        catch (final Exception e2) {
            CustomJAASRealm.logger.log(Level.SEVERE, "Exception occured while login : ", e2);
            request.setAttribute("login_status", (Object)e2.getMessage());
            return null;
        }
    }
    
    public Principal createPrincipal(final String loginName, final Subject subject) {
        final Principal principal = super.createPrincipal(loginName, subject, (LoginContext)null);
        return principal;
    }
    
    public static void setRequestInThreadLocal(final HttpServletRequest hreq) {
        CustomJAASRealm.logger.log(Level.FINEST, "httpServletReq object set in thread local variable is : {0}", hreq);
        CustomJAASRealm.threadLocal.set(hreq);
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
    
    private LoginHandler getLoginHandler(final HttpServletRequest request) throws Exception {
        String handler = request.getSession().getServletContext().getInitParameter("LOGIN_HANDLER");
        if (handler == null) {
            handler = "com.adventnet.authentication.LoginHandlerImpl";
        }
        return (LoginHandler)Thread.currentThread().getContextClassLoader().loadClass(handler).newInstance();
    }
    
    public boolean hasRole(final Wrapper wrapper, final Principal principal, final String role) {
        final Credential cr = AuthUtil.getUserCredential();
        if (cr != null) {
            CustomJAASRealm.logger.log(Level.FINEST, "Credential obtained from the thread context is {0}", cr);
            return cr.getRoles().contains(role);
        }
        CustomJAASRealm.logger.log(Level.FINEST, "Couldn't get the associated credential. So, returning false.");
        return false;
    }
    
    static {
        CustomJAASRealm.logger = Logger.getLogger(CustomJAASRealm.class.getName());
        CustomJAASRealm.threadLocal = new ThreadLocal<HttpServletRequest>();
    }
}
