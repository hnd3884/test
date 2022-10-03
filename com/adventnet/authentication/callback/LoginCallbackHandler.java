package com.adventnet.authentication.callback;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.io.Serializable;
import javax.security.auth.callback.CallbackHandler;

public class LoginCallbackHandler implements CallbackHandler, Serializable
{
    private static Logger logger;
    private String loginName;
    private String password;
    private String serviceName;
    private String domainName;
    private HttpServletRequest request;
    
    public LoginCallbackHandler(final String loginName, final String password, final String serviceName, final HttpServletRequest request, final String domainName) {
        this.loginName = null;
        this.password = null;
        this.serviceName = null;
        this.domainName = null;
        this.request = null;
        LoginCallbackHandler.logger.log(Level.FINEST, "LoginCallbackHandler initialized with loginname : {0}, password, service : {1} and request : {2}", new Object[] { "*****", serviceName, request });
        this.loginName = loginName;
        this.domainName = domainName;
        this.password = password;
        this.serviceName = serviceName;
        this.request = request;
        LoginCallbackHandler.logger.log(Level.INFO, "domainName :: [{0}] and loginName :: [{1}]", new Object[] { domainName, "*****" });
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback)callback).setName(this.loginName);
                LoginCallbackHandler.logger.log(Level.FINEST, "Loginname set in NameCallback");
            }
            else if (callback instanceof PasswordCallback) {
                ((PasswordCallback)callback).setPassword(this.password.toCharArray());
                LoginCallbackHandler.logger.log(Level.FINEST, "Password set in PasswordCallback");
            }
            else if (callback instanceof ServiceCallback) {
                ((ServiceCallback)callback).setServiceName(this.serviceName);
                LoginCallbackHandler.logger.log(Level.FINEST, "ServiceName set in ServiceCallback");
            }
            else if (callback instanceof ServletCallback) {
                ((ServletCallback)callback).setHttpServletRequest(this.request);
                LoginCallbackHandler.logger.log(Level.FINEST, "ServletRequest set in ServletCallback");
            }
            else if (callback instanceof DomainCallback) {
                ((DomainCallback)callback).setDomainName(this.domainName);
                LoginCallbackHandler.logger.log(Level.FINEST, "domainName set in DomainCallback");
            }
            else {
                LoginCallbackHandler.logger.log(Level.FINEST, "Unknown callback handler obtained in LoginCallbackHandler.handle : {0}", callback);
            }
        }
    }
    
    static {
        LoginCallbackHandler.logger = Logger.getLogger(LoginCallbackHandler.class.getName());
    }
}
