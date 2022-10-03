package com.sun.jmx.remote.security;

import java.util.Collections;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import com.sun.jmx.remote.util.EnvHelp;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.Configuration;
import javax.security.auth.Subject;
import java.security.PrivilegedExceptionAction;
import java.security.Permission;
import javax.security.auth.AuthPermission;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import com.sun.jmx.remote.util.ClassLogger;
import javax.security.auth.login.LoginContext;
import javax.management.remote.JMXAuthenticator;

public final class JMXPluggableAuthenticator implements JMXAuthenticator
{
    private LoginContext loginContext;
    private String username;
    private String password;
    private static final String LOGIN_CONFIG_PROP = "jmx.remote.x.login.config";
    private static final String LOGIN_CONFIG_NAME = "JMXPluggableAuthenticator";
    private static final String PASSWORD_FILE_PROP = "jmx.remote.x.password.file";
    private static final ClassLogger logger;
    
    public JMXPluggableAuthenticator(final Map<?, ?> map) {
        String s = null;
        String s2 = null;
        if (map != null) {
            s = (String)map.get("jmx.remote.x.login.config");
            s2 = (String)map.get("jmx.remote.x.password.file");
        }
        try {
            if (s != null) {
                this.loginContext = new LoginContext(s, new JMXCallbackHandler());
            }
            else {
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityManager.checkPermission(new AuthPermission("createLoginContext.JMXPluggableAuthenticator"));
                }
                final String s3 = s2;
                try {
                    this.loginContext = AccessController.doPrivileged((PrivilegedExceptionAction<LoginContext>)new PrivilegedExceptionAction<LoginContext>() {
                        @Override
                        public LoginContext run() throws LoginException {
                            return new LoginContext("JMXPluggableAuthenticator", null, new JMXCallbackHandler(), new FileLoginConfig(s3));
                        }
                    });
                }
                catch (final PrivilegedActionException ex) {
                    throw (LoginException)ex.getException();
                }
            }
        }
        catch (final LoginException ex2) {
            authenticationFailure("authenticate", ex2);
        }
        catch (final SecurityException ex3) {
            authenticationFailure("authenticate", ex3);
        }
    }
    
    @Override
    public Subject authenticate(final Object o) {
        if (!(o instanceof String[])) {
            if (o == null) {
                authenticationFailure("authenticate", "Credentials required");
            }
            authenticationFailure("authenticate", "Credentials should be String[] instead of " + o.getClass().getName());
        }
        final String[] array = (String[])o;
        if (array.length != 2) {
            authenticationFailure("authenticate", "Credentials should have 2 elements not " + array.length);
        }
        this.username = array[0];
        this.password = array[1];
        if (this.username == null || this.password == null) {
            authenticationFailure("authenticate", "Username or password is null");
        }
        try {
            this.loginContext.login();
            final Subject subject = this.loginContext.getSubject();
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    subject.setReadOnly();
                    return null;
                }
            });
            return subject;
        }
        catch (final LoginException ex) {
            authenticationFailure("authenticate", ex);
            return null;
        }
    }
    
    private static void authenticationFailure(final String s, final String s2) throws SecurityException {
        final String string = "Authentication failed! " + s2;
        final SecurityException ex = new SecurityException(string);
        logException(s, string, ex);
        throw ex;
    }
    
    private static void authenticationFailure(final String s, final Exception ex) throws SecurityException {
        String s2;
        SecurityException ex2;
        if (ex instanceof SecurityException) {
            s2 = ex.getMessage();
            ex2 = (SecurityException)ex;
        }
        else {
            s2 = "Authentication failed! " + ex.getMessage();
            final SecurityException ex3 = new SecurityException(s2);
            EnvHelp.initCause(ex3, ex);
            ex2 = ex3;
        }
        logException(s, s2, ex2);
        throw ex2;
    }
    
    private static void logException(final String s, final String s2, final Exception ex) {
        if (JMXPluggableAuthenticator.logger.traceOn()) {
            JMXPluggableAuthenticator.logger.trace(s, s2);
        }
        if (JMXPluggableAuthenticator.logger.debugOn()) {
            JMXPluggableAuthenticator.logger.debug(s, ex);
        }
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.misc", "JMXPluggableAuthenticator");
    }
    
    private final class JMXCallbackHandler implements CallbackHandler
    {
        @Override
        public void handle(final Callback[] array) throws IOException, UnsupportedCallbackException {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof NameCallback) {
                    ((NameCallback)array[i]).setName(JMXPluggableAuthenticator.this.username);
                }
                else {
                    if (!(array[i] instanceof PasswordCallback)) {
                        throw new UnsupportedCallbackException(array[i], "Unrecognized Callback");
                    }
                    ((PasswordCallback)array[i]).setPassword(JMXPluggableAuthenticator.this.password.toCharArray());
                }
            }
        }
    }
    
    private static class FileLoginConfig extends Configuration
    {
        private AppConfigurationEntry[] entries;
        private static final String FILE_LOGIN_MODULE;
        private static final String PASSWORD_FILE_OPTION = "passwordFile";
        
        public FileLoginConfig(final String s) {
            Map<Object, Object> emptyMap;
            if (s != null) {
                emptyMap = (Map<Object, Object>)new HashMap<String, String>(1);
                emptyMap.put("passwordFile", s);
            }
            else {
                emptyMap = (Map<Object, Object>)Collections.emptyMap();
            }
            this.entries = new AppConfigurationEntry[] { new AppConfigurationEntry(FileLoginConfig.FILE_LOGIN_MODULE, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, (Map<String, ?>)emptyMap) };
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String s) {
            return (AppConfigurationEntry[])(s.equals("JMXPluggableAuthenticator") ? this.entries : null);
        }
        
        @Override
        public void refresh() {
        }
        
        static {
            FILE_LOGIN_MODULE = FileLoginModule.class.getName();
        }
    }
}
