package com.sun.jmx.remote.security;

import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.util.Arrays;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.security.AccessControlException;
import java.io.FilePermission;
import java.io.FileInputStream;
import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import com.sun.jmx.remote.util.EnvHelp;
import javax.security.auth.login.LoginException;
import com.sun.jmx.mbeanserver.Util;
import java.util.Properties;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import javax.management.remote.JMXPrincipal;
import com.sun.jmx.remote.util.ClassLogger;
import javax.security.auth.spi.LoginModule;

public class FileLoginModule implements LoginModule
{
    private static final String DEFAULT_PASSWORD_FILE_NAME;
    private static final String USERNAME_KEY = "javax.security.auth.login.name";
    private static final String PASSWORD_KEY = "javax.security.auth.login.password";
    private static final ClassLogger logger;
    private boolean useFirstPass;
    private boolean tryFirstPass;
    private boolean storePass;
    private boolean clearPass;
    private boolean succeeded;
    private boolean commitSucceeded;
    private String username;
    private char[] password;
    private JMXPrincipal user;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;
    private Map<String, ?> options;
    private String passwordFile;
    private String passwordFileDisplayName;
    private boolean userSuppliedPasswordFile;
    private boolean hasJavaHomePermission;
    private Properties userCredentials;
    
    public FileLoginModule() {
        this.useFirstPass = false;
        this.tryFirstPass = false;
        this.storePass = false;
        this.clearPass = false;
        this.succeeded = false;
        this.commitSucceeded = false;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> map, final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = Util.cast(map);
        this.options = options;
        this.tryFirstPass = "true".equalsIgnoreCase((String)options.get("tryFirstPass"));
        this.useFirstPass = "true".equalsIgnoreCase((String)options.get("useFirstPass"));
        this.storePass = "true".equalsIgnoreCase((String)options.get("storePass"));
        this.clearPass = "true".equalsIgnoreCase((String)options.get("clearPass"));
        this.passwordFile = (String)options.get("passwordFile");
        this.passwordFileDisplayName = this.passwordFile;
        this.userSuppliedPasswordFile = true;
        if (this.passwordFile == null) {
            this.passwordFile = FileLoginModule.DEFAULT_PASSWORD_FILE_NAME;
            this.userSuppliedPasswordFile = false;
            try {
                System.getProperty("java.home");
                this.hasJavaHomePermission = true;
                this.passwordFileDisplayName = this.passwordFile;
            }
            catch (final SecurityException ex) {
                this.hasJavaHomePermission = false;
                this.passwordFileDisplayName = "jmxremote.password";
            }
        }
    }
    
    @Override
    public boolean login() throws LoginException {
        try {
            this.loadPasswordFile();
        }
        catch (final IOException ex) {
            throw EnvHelp.initCause(new LoginException("Error: unable to load the password file: " + this.passwordFileDisplayName), ex);
        }
        if (this.userCredentials == null) {
            throw new LoginException("Error: unable to locate the users' credentials.");
        }
        if (FileLoginModule.logger.debugOn()) {
            FileLoginModule.logger.debug("login", "Using password file: " + this.passwordFileDisplayName);
        }
        Label_0214: {
            if (this.tryFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    if (FileLoginModule.logger.debugOn()) {
                        FileLoginModule.logger.debug("login", "Authentication using cached password has succeeded");
                    }
                    return true;
                }
                catch (final LoginException ex2) {
                    this.cleanState();
                    FileLoginModule.logger.debug("login", "Authentication using cached password has failed");
                    break Label_0214;
                }
            }
            if (this.useFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    if (FileLoginModule.logger.debugOn()) {
                        FileLoginModule.logger.debug("login", "Authentication using cached password has succeeded");
                    }
                    return true;
                }
                catch (final LoginException ex3) {
                    this.cleanState();
                    FileLoginModule.logger.debug("login", "Authentication using cached password has failed");
                    throw ex3;
                }
            }
        }
        if (FileLoginModule.logger.debugOn()) {
            FileLoginModule.logger.debug("login", "Acquiring password");
        }
        try {
            this.attemptAuthentication(false);
            this.succeeded = true;
            if (FileLoginModule.logger.debugOn()) {
                FileLoginModule.logger.debug("login", "Authentication has succeeded");
            }
            return true;
        }
        catch (final LoginException ex4) {
            this.cleanState();
            FileLoginModule.logger.debug("login", "Authentication has failed");
            throw ex4;
        }
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.subject.isReadOnly()) {
            this.cleanState();
            throw new LoginException("Subject is read-only");
        }
        if (!this.subject.getPrincipals().contains(this.user)) {
            this.subject.getPrincipals().add(this.user);
        }
        if (FileLoginModule.logger.debugOn()) {
            FileLoginModule.logger.debug("commit", "Authentication has completed successfully");
        }
        this.cleanState();
        return this.commitSucceeded = true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        if (FileLoginModule.logger.debugOn()) {
            FileLoginModule.logger.debug("abort", "Authentication has not completed successfully");
        }
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanState();
            this.user = null;
        }
        else {
            this.logout();
        }
        return true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        if (this.subject.isReadOnly()) {
            this.cleanState();
            throw new LoginException("Subject is read-only");
        }
        this.subject.getPrincipals().remove(this.user);
        this.cleanState();
        this.succeeded = false;
        this.commitSucceeded = false;
        this.user = null;
        if (FileLoginModule.logger.debugOn()) {
            FileLoginModule.logger.debug("logout", "Subject is being logged out");
        }
        return true;
    }
    
    private void attemptAuthentication(final boolean b) throws LoginException {
        this.getUsernamePassword(b);
        final String property;
        if ((property = this.userCredentials.getProperty(this.username)) == null || !property.equals(new String(this.password))) {
            if (FileLoginModule.logger.debugOn()) {
                FileLoginModule.logger.debug("login", "Invalid username or password");
            }
            throw new FailedLoginException("Invalid username or password");
        }
        if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
            this.sharedState.put("javax.security.auth.login.name", this.username);
            this.sharedState.put("javax.security.auth.login.password", this.password);
        }
        this.user = new JMXPrincipal(this.username);
        if (FileLoginModule.logger.debugOn()) {
            FileLoginModule.logger.debug("login", "User '" + this.username + "' successfully validated");
        }
    }
    
    private void loadPasswordFile() throws IOException {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(this.passwordFile);
        }
        catch (final SecurityException ex) {
            if (this.userSuppliedPasswordFile || this.hasJavaHomePermission) {
                throw ex;
            }
            final AccessControlException ex2 = new AccessControlException("access denied " + new FilePermission(this.passwordFileDisplayName, "read").toString());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            try {
                (this.userCredentials = new Properties()).load(bufferedInputStream);
            }
            finally {
                bufferedInputStream.close();
            }
        }
        finally {
            fileInputStream.close();
        }
    }
    
    private void getUsernamePassword(final boolean b) throws LoginException {
        if (b) {
            this.username = this.sharedState.get("javax.security.auth.login.name");
            this.password = this.sharedState.get("javax.security.auth.login.password");
            return;
        }
        if (this.callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
        }
        final Callback[] array = { new NameCallback("username"), new PasswordCallback("password", false) };
        try {
            this.callbackHandler.handle(array);
            this.username = ((NameCallback)array[0]).getName();
            final char[] password = ((PasswordCallback)array[1]).getPassword();
            System.arraycopy(password, 0, this.password = new char[password.length], 0, password.length);
            ((PasswordCallback)array[1]).clearPassword();
        }
        catch (final IOException ex) {
            throw EnvHelp.initCause(new LoginException(ex.toString()), ex);
        }
        catch (final UnsupportedCallbackException ex2) {
            throw EnvHelp.initCause(new LoginException("Error: " + ex2.getCallback().toString() + " not available to garner authentication information from the user"), ex2);
        }
    }
    
    private void cleanState() {
        this.username = null;
        if (this.password != null) {
            Arrays.fill(this.password, ' ');
            this.password = null;
        }
        if (this.clearPass) {
            this.sharedState.remove("javax.security.auth.login.name");
            this.sharedState.remove("javax.security.auth.login.password");
        }
    }
    
    static {
        DEFAULT_PASSWORD_FILE_NAME = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.home")) + File.separatorChar + "lib" + File.separatorChar + "management" + File.separatorChar + "jmxremote.password";
        logger = new ClassLogger("javax.management.remote.misc", "FileLoginModule");
    }
}
