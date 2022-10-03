package javax.security.auth.login;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import sun.security.util.PendingException;
import java.util.HashMap;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.util.ResourcesMgr;
import java.security.Permission;
import javax.security.auth.AuthPermission;
import sun.security.util.Debug;
import java.security.AccessControlContext;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;

public class LoginContext
{
    private static final String INIT_METHOD = "initialize";
    private static final String LOGIN_METHOD = "login";
    private static final String COMMIT_METHOD = "commit";
    private static final String ABORT_METHOD = "abort";
    private static final String LOGOUT_METHOD = "logout";
    private static final String OTHER = "other";
    private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
    private Subject subject;
    private boolean subjectProvided;
    private boolean loginSucceeded;
    private CallbackHandler callbackHandler;
    private Map<String, ?> state;
    private Configuration config;
    private AccessControlContext creatorAcc;
    private ModuleInfo[] moduleStack;
    private ClassLoader contextClassLoader;
    private static final Class<?>[] PARAMS;
    private int moduleIndex;
    private LoginException firstError;
    private LoginException firstRequiredError;
    private boolean success;
    private static final Debug debug;
    
    private void init(final String s) throws LoginException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && this.creatorAcc == null) {
            securityManager.checkPermission(new AuthPermission("createLoginContext." + s));
        }
        if (s == null) {
            throw new LoginException(ResourcesMgr.getString("Invalid.null.input.name"));
        }
        if (this.config == null) {
            this.config = AccessController.doPrivileged((PrivilegedAction<Configuration>)new PrivilegedAction<Configuration>() {
                @Override
                public Configuration run() {
                    return Configuration.getConfiguration();
                }
            });
        }
        AppConfigurationEntry[] array = this.config.getAppConfigurationEntry(s);
        if (array == null) {
            if (securityManager != null && this.creatorAcc == null) {
                securityManager.checkPermission(new AuthPermission("createLoginContext.other"));
            }
            array = this.config.getAppConfigurationEntry("other");
            if (array == null) {
                throw new LoginException(new MessageFormat(ResourcesMgr.getString("No.LoginModules.configured.for.name")).format(new Object[] { s }));
            }
        }
        this.moduleStack = new ModuleInfo[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.moduleStack[i] = new ModuleInfo(new AppConfigurationEntry(array[i].getLoginModuleName(), array[i].getControlFlag(), array[i].getOptions()), null);
        }
        this.contextClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                return classLoader;
            }
        });
    }
    
    private void loadDefaultCallbackHandler() throws LoginException {
        try {
            this.callbackHandler = AccessController.doPrivileged((PrivilegedExceptionAction<CallbackHandler>)new PrivilegedExceptionAction<CallbackHandler>() {
                final /* synthetic */ ClassLoader val$finalLoader = LoginContext.this.contextClassLoader;
                
                @Override
                public CallbackHandler run() throws Exception {
                    final String property = Security.getProperty("auth.login.defaultCallbackHandler");
                    if (property == null || property.length() == 0) {
                        return null;
                    }
                    return (CallbackHandler)Class.forName(property, true, this.val$finalLoader).asSubclass(CallbackHandler.class).newInstance();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new LoginException(ex.getException().toString());
        }
        if (this.callbackHandler != null && this.creatorAcc == null) {
            this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), this.callbackHandler);
        }
    }
    
    public LoginContext(final String s) throws LoginException {
        this.subject = null;
        this.subjectProvided = false;
        this.loginSucceeded = false;
        this.state = new HashMap<String, Object>();
        this.creatorAcc = null;
        this.contextClassLoader = null;
        this.moduleIndex = 0;
        this.firstError = null;
        this.firstRequiredError = null;
        this.success = false;
        this.init(s);
        this.loadDefaultCallbackHandler();
    }
    
    public LoginContext(final String s, final Subject subject) throws LoginException {
        this.subject = null;
        this.subjectProvided = false;
        this.loginSucceeded = false;
        this.state = new HashMap<String, Object>();
        this.creatorAcc = null;
        this.contextClassLoader = null;
        this.moduleIndex = 0;
        this.firstError = null;
        this.firstRequiredError = null;
        this.success = false;
        this.init(s);
        if (subject == null) {
            throw new LoginException(ResourcesMgr.getString("invalid.null.Subject.provided"));
        }
        this.subject = subject;
        this.subjectProvided = true;
        this.loadDefaultCallbackHandler();
    }
    
    public LoginContext(final String s, final CallbackHandler callbackHandler) throws LoginException {
        this.subject = null;
        this.subjectProvided = false;
        this.loginSucceeded = false;
        this.state = new HashMap<String, Object>();
        this.creatorAcc = null;
        this.contextClassLoader = null;
        this.moduleIndex = 0;
        this.firstError = null;
        this.firstRequiredError = null;
        this.success = false;
        this.init(s);
        if (callbackHandler == null) {
            throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
        }
        this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), callbackHandler);
    }
    
    public LoginContext(final String s, final Subject subject, final CallbackHandler callbackHandler) throws LoginException {
        this(s, subject);
        if (callbackHandler == null) {
            throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
        }
        this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), callbackHandler);
    }
    
    public LoginContext(final String s, final Subject subject, final CallbackHandler callbackHandler, final Configuration config) throws LoginException {
        this.subject = null;
        this.subjectProvided = false;
        this.loginSucceeded = false;
        this.state = new HashMap<String, Object>();
        this.creatorAcc = null;
        this.contextClassLoader = null;
        this.moduleIndex = 0;
        this.firstError = null;
        this.firstRequiredError = null;
        this.success = false;
        this.config = config;
        if (config != null) {
            this.creatorAcc = AccessController.getContext();
        }
        this.init(s);
        if (subject != null) {
            this.subject = subject;
            this.subjectProvided = true;
        }
        if (callbackHandler == null) {
            this.loadDefaultCallbackHandler();
        }
        else if (this.creatorAcc == null) {
            this.callbackHandler = new SecureCallbackHandler(AccessController.getContext(), callbackHandler);
        }
        else {
            this.callbackHandler = callbackHandler;
        }
    }
    
    public void login() throws LoginException {
        this.loginSucceeded = false;
        if (this.subject == null) {
            this.subject = new Subject();
        }
        try {
            this.invokePriv("login");
            this.invokePriv("commit");
            this.loginSucceeded = true;
        }
        catch (final LoginException ex) {
            try {
                this.invokePriv("abort");
            }
            catch (final LoginException ex2) {
                throw ex;
            }
            throw ex;
        }
    }
    
    public void logout() throws LoginException {
        if (this.subject == null) {
            throw new LoginException(ResourcesMgr.getString("null.subject.logout.called.before.login"));
        }
        this.invokePriv("logout");
    }
    
    public Subject getSubject() {
        if (!this.loginSucceeded && !this.subjectProvided) {
            return null;
        }
        return this.subject;
    }
    
    private void clearState() {
        this.moduleIndex = 0;
        this.firstError = null;
        this.firstRequiredError = null;
        this.success = false;
    }
    
    private void throwException(final LoginException ex, final LoginException ex2) throws LoginException {
        this.clearState();
        throw (ex != null) ? ex : ex2;
    }
    
    private void invokePriv(final String s) throws LoginException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws LoginException {
                    LoginContext.this.invoke(s);
                    return null;
                }
            }, this.creatorAcc);
        }
        catch (final PrivilegedActionException ex) {
            throw (LoginException)ex.getException();
        }
    }
    
    private void invoke(final String s) throws LoginException {
        for (int i = this.moduleIndex; i < this.moduleStack.length; ++i, ++this.moduleIndex) {
            try {
                Method[] array;
                if (this.moduleStack[i].module != null) {
                    array = this.moduleStack[i].module.getClass().getMethods();
                }
                else {
                    this.moduleStack[i].module = Class.forName(this.moduleStack[i].entry.getLoginModuleName(), true, this.contextClassLoader).getConstructor(LoginContext.PARAMS).newInstance(new Object[0]);
                    int n;
                    for (array = this.moduleStack[i].module.getClass().getMethods(), n = 0; n < array.length && !array[n].getName().equals("initialize"); ++n) {}
                    array[n].invoke(this.moduleStack[i].module, this.subject, this.callbackHandler, this.state, this.moduleStack[i].entry.getOptions());
                }
                int n2;
                for (n2 = 0; n2 < array.length && !array[n2].getName().equals(s); ++n2) {}
                if (array[n2].invoke(this.moduleStack[i].module, new Object[0])) {
                    if (!s.equals("abort") && !s.equals("logout") && this.moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT && this.firstRequiredError == null) {
                        this.clearState();
                        if (LoginContext.debug != null) {
                            LoginContext.debug.println(s + " SUFFICIENT success");
                        }
                        return;
                    }
                    if (LoginContext.debug != null) {
                        LoginContext.debug.println(s + " success");
                    }
                    this.success = true;
                }
                else if (LoginContext.debug != null) {
                    LoginContext.debug.println(s + " ignored");
                }
            }
            catch (final NoSuchMethodException ex) {
                this.throwException(null, new LoginException(new MessageFormat(ResourcesMgr.getString("unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor")).format(new Object[] { this.moduleStack[i].entry.getLoginModuleName() })));
            }
            catch (final InstantiationException ex2) {
                this.throwException(null, new LoginException(ResourcesMgr.getString("unable.to.instantiate.LoginModule.") + ex2.getMessage()));
            }
            catch (final ClassNotFoundException ex3) {
                this.throwException(null, new LoginException(ResourcesMgr.getString("unable.to.find.LoginModule.class.") + ex3.getMessage()));
            }
            catch (final IllegalAccessException ex4) {
                this.throwException(null, new LoginException(ResourcesMgr.getString("unable.to.access.LoginModule.") + ex4.getMessage()));
            }
            catch (final InvocationTargetException ex5) {
                if (ex5.getCause() instanceof PendingException && s.equals("login")) {
                    throw (PendingException)ex5.getCause();
                }
                Throwable firstError;
                if (ex5.getCause() instanceof LoginException) {
                    firstError = ex5.getCause();
                }
                else if (ex5.getCause() instanceof SecurityException) {
                    firstError = new LoginException("Security Exception");
                    firstError.initCause(new SecurityException());
                    if (LoginContext.debug != null) {
                        LoginContext.debug.println("original security exception with detail msg replaced by new exception with empty detail msg");
                        LoginContext.debug.println("original security exception: " + ex5.getCause().toString());
                    }
                }
                else {
                    final StringWriter stringWriter = new StringWriter();
                    ex5.getCause().printStackTrace(new PrintWriter(stringWriter));
                    stringWriter.flush();
                    firstError = new LoginException(stringWriter.toString());
                }
                if (this.moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
                    if (LoginContext.debug != null) {
                        LoginContext.debug.println(s + " REQUISITE failure");
                    }
                    if (s.equals("abort") || s.equals("logout")) {
                        if (this.firstRequiredError == null) {
                            this.firstRequiredError = (LoginException)firstError;
                        }
                    }
                    else {
                        this.throwException(this.firstRequiredError, (LoginException)firstError);
                    }
                }
                else if (this.moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
                    if (LoginContext.debug != null) {
                        LoginContext.debug.println(s + " REQUIRED failure");
                    }
                    if (this.firstRequiredError == null) {
                        this.firstRequiredError = (LoginException)firstError;
                    }
                }
                else {
                    if (LoginContext.debug != null) {
                        LoginContext.debug.println(s + " OPTIONAL failure");
                    }
                    if (this.firstError == null) {
                        this.firstError = (LoginException)firstError;
                    }
                }
            }
        }
        if (this.firstRequiredError != null) {
            this.throwException(this.firstRequiredError, null);
        }
        else if (!this.success && this.firstError != null) {
            this.throwException(this.firstError, null);
        }
        else {
            if (this.success) {
                this.clearState();
                return;
            }
            this.throwException(new LoginException(ResourcesMgr.getString("Login.Failure.all.modules.ignored")), null);
        }
    }
    
    static {
        PARAMS = new Class[0];
        debug = Debug.getInstance("logincontext", "\t[LoginContext]");
    }
    
    private static class SecureCallbackHandler implements CallbackHandler
    {
        private final AccessControlContext acc;
        private final CallbackHandler ch;
        
        SecureCallbackHandler(final AccessControlContext acc, final CallbackHandler ch) {
            this.acc = acc;
            this.ch = ch;
        }
        
        @Override
        public void handle(final Callback[] array) throws IOException, UnsupportedCallbackException {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws IOException, UnsupportedCallbackException {
                        SecureCallbackHandler.this.ch.handle(array);
                        return null;
                    }
                }, this.acc);
            }
            catch (final PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (UnsupportedCallbackException)ex.getException();
            }
        }
    }
    
    private static class ModuleInfo
    {
        AppConfigurationEntry entry;
        Object module;
        
        ModuleInfo(final AppConfigurationEntry entry, final Object module) {
            this.entry = entry;
            this.module = module;
        }
    }
}
