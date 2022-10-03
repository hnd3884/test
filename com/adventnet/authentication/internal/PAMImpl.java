package com.adventnet.authentication.internal;

import com.adventnet.authentication.Credential;
import com.adventnet.authentication.callback.LogoutCallbackHandler;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.authentication.twofactor.TwoFactorAuth;
import com.adventnet.authentication.PAMException;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.authentication.callback.LoginCallbackHandler;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.authentication.PAM;

public class PAMImpl extends PAM
{
    private static Logger logger;
    
    @Override
    public Subject validate(final String loginName, final String password, final String serviceName, final HttpServletRequest request) throws PAMException, LoginException {
        PAMImpl.logger.log(Level.FINEST, "validate invoked with loginname : {0}, servicename : {1} and httpServletReq : {2}", new Object[] { "*****", serviceName, request });
        String domainName = null;
        String authRuleName = request.getParameter("AUTHRULE_NAME");
        domainName = request.getParameter(PAMImpl.DOMAINNAME);
        final LoginCallbackHandler cbh = new LoginCallbackHandler(loginName, password, serviceName, request, domainName);
        LoginContext loginContext = null;
        try {
            if (authRuleName == null || authRuleName.equals("")) {
                authRuleName = AuthUtil.getDefaultAuthRule();
            }
            loginContext = new LoginContext(authRuleName, cbh);
        }
        catch (final Exception e) {
            PAMImpl.logger.log(Level.SEVERE, "Exception occured while initializing loginContext : for the login [" + loginName + "]", e);
            return null;
        }
        Subject subject = null;
        try {
            loginContext.login();
            subject = loginContext.getSubject();
            if (subject == null) {
                PAMImpl.logger.log(Level.INFO, "subject obtained is null, hence login failed for user : {0}", loginName);
                return null;
            }
            loginContext = null;
        }
        catch (final AccountExpiredException aee) {
            PAMImpl.logger.log(Level.INFO, "AccountExpired for user : {0} wit msg : {1}", new Object[] { loginName, aee.getMessage() });
            throw aee;
        }
        catch (final CredentialExpiredException cee) {
            PAMImpl.logger.log(Level.INFO, "CredentialExpired for user : {0} with msg : {1}", new Object[] { loginName, cee.getMessage() });
            throw cee;
        }
        catch (final FailedLoginException fle) {
            PAMImpl.logger.log(Level.INFO, "FailedLogin occured for user : {0} wit hmsg : {1}", new Object[] { loginName, fle.getMessage() });
            throw fle;
        }
        catch (final LoginException le) {
            le.printStackTrace();
            PAMImpl.logger.log(Level.INFO, "LoginException occured for user : {0} with msg : {1}", new Object[] { loginName, le.getMessage() });
            throw le;
        }
        catch (final Exception e2) {
            PAMImpl.logger.log(Level.SEVERE, "Unexpected error occured while login : for the user :: [" + loginName + "]", e2);
            throw new PAMException("Unexpected exception occured while login", e2);
        }
        if ("true".equalsIgnoreCase((String)request.getAttribute("IGNORE_TFA"))) {
            PAMImpl.logger.log(Level.INFO, "Ignoring TwoFactorAuthentication, as the IGNORE_TFA attribute is set to true");
        }
        else {
            final Long userId = this.getUserId(loginName, request);
            if (AuthUtil.isTwofactorLoginEnabled(userId) && !this.validateTwoFactorAuth(loginName, request)) {
                return null;
            }
        }
        return subject;
    }
    
    private Long getUserId(final String loginName, final HttpServletRequest request) {
        Long userId = null;
        try {
            if (request.getParameter(PAMImpl.DOMAINNAME) != null) {
                userId = AuthUtil.getUserId(request.getParameter("j_username"), request.getParameter(PAMImpl.DOMAINNAME));
            }
            else {
                userId = AuthUtil.getUserId(request.getParameter("j_username"));
            }
        }
        catch (final Exception e) {
            userId = null;
        }
        return userId;
    }
    
    public boolean validateTwoFactorAuth(final String loginName, final HttpServletRequest request) {
        Long userId = null;
        try {
            if (request.getParameter(PAMImpl.DOMAINNAME) != null) {
                userId = AuthUtil.getUserId(request.getParameter("j_username"), request.getParameter(PAMImpl.DOMAINNAME));
            }
            else {
                userId = AuthUtil.getUserId(request.getParameter("j_username"));
            }
        }
        catch (final Exception e) {
            userId = null;
        }
        try {
            if (System.getProperty("2factor.auth") == null && !AuthUtil.isTwofactorLoginEnabled(userId)) {
                return true;
            }
        }
        catch (final Exception e) {
            PAMImpl.logger.log(Level.SEVERE, "No details for AaaUserTwoFactorDetails table for user " + loginName);
            return true;
        }
        boolean ret = false;
        try {
            final TwoFactorAuth userAuthImpl = (TwoFactorAuth)AuthUtil.getTwoFactorImpl(userId);
            ret = userAuthImpl.validate(userId, request, null);
        }
        catch (final Exception e2) {
            PAMImpl.logger.log(Level.SEVERE, "Exception while instantiating Two Factor implementation class or No details for AaaUserTwoFactorDetails table for user " + loginName);
            return ret;
        }
        return ret;
    }
    
    @Override
    public void inValidate(final Long sessionId) throws PAMException, LoginException {
        PAMImpl.logger.log(Level.FINEST, "inValidate invoked for sessionId : {0}", sessionId);
        final LogoutCallbackHandler cbh = new LogoutCallbackHandler(sessionId);
        LoginContext loginContext = null;
        try {
            final Credential cr = AuthUtil.getUserCredential();
            String authRuleName = null;
            if (cr == null || cr.getAuthRuleName() == null) {
                PAMImpl.logger.log(Level.WARNING, "Credential in the thread context is null");
                authRuleName = AuthUtil.getDefaultAuthRule();
            }
            else {
                authRuleName = cr.getAuthRuleName();
            }
            PAMImpl.logger.log(Level.FINER, "AuthRuleName is :: {0}", authRuleName);
            loginContext = new LoginContext(authRuleName, new Subject(), cbh);
            loginContext.logout();
        }
        catch (final Exception e) {
            PAMImpl.logger.log(Level.SEVERE, "Exception occured while initializing loginContext : ", e);
            throw new PAMException("Unexpected error occured while logout : ", e);
        }
    }
    
    static {
        PAMImpl.logger = Logger.getLogger(PAMImpl.class.getName());
    }
}
