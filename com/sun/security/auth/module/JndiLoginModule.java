package com.sun.security.auth.module;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.naming.NamingEnumeration;
import java.io.UnsupportedEncodingException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.security.auth.login.FailedLoginException;
import javax.naming.directory.SearchControls;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.LinkedList;
import com.sun.security.auth.UnixNumericGroupPrincipal;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import javax.naming.directory.DirContext;
import java.util.ResourceBundle;
import jdk.Exported;
import javax.security.auth.spi.LoginModule;

@Exported
public class JndiLoginModule implements LoginModule
{
    private static final ResourceBundle rb;
    public final String USER_PROVIDER = "user.provider.url";
    public final String GROUP_PROVIDER = "group.provider.url";
    private boolean debug;
    private boolean strongDebug;
    private String userProvider;
    private String groupProvider;
    private boolean useFirstPass;
    private boolean tryFirstPass;
    private boolean storePass;
    private boolean clearPass;
    private boolean succeeded;
    private boolean commitSucceeded;
    private String username;
    private char[] password;
    DirContext ctx;
    private UnixPrincipal userPrincipal;
    private UnixNumericUserPrincipal UIDPrincipal;
    private UnixNumericGroupPrincipal GIDPrincipal;
    private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;
    private Map<String, ?> options;
    private static final String CRYPT = "{crypt}";
    private static final String USER_PWD = "userPassword";
    private static final String USER_UID = "uidNumber";
    private static final String USER_GID = "gidNumber";
    private static final String GROUP_ID = "gidNumber";
    private static final String NAME = "javax.security.auth.login.name";
    private static final String PWD = "javax.security.auth.login.password";
    
    public JndiLoginModule() {
        this.debug = false;
        this.strongDebug = false;
        this.useFirstPass = false;
        this.tryFirstPass = false;
        this.storePass = false;
        this.clearPass = false;
        this.succeeded = false;
        this.commitSucceeded = false;
        this.supplementaryGroups = new LinkedList<UnixNumericGroupPrincipal>();
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = (Map<String, Object>)sharedState;
        this.options = options;
        this.debug = "true".equalsIgnoreCase((String)options.get("debug"));
        this.strongDebug = "true".equalsIgnoreCase((String)options.get("strongDebug"));
        this.userProvider = (String)options.get("user.provider.url");
        this.groupProvider = (String)options.get("group.provider.url");
        this.tryFirstPass = "true".equalsIgnoreCase((String)options.get("tryFirstPass"));
        this.useFirstPass = "true".equalsIgnoreCase((String)options.get("useFirstPass"));
        this.storePass = "true".equalsIgnoreCase((String)options.get("storePass"));
        this.clearPass = "true".equalsIgnoreCase((String)options.get("clearPass"));
    }
    
    @Override
    public boolean login() throws LoginException {
        if (this.userProvider == null) {
            throw new LoginException("Error: Unable to locate JNDI user provider");
        }
        if (this.groupProvider == null) {
            throw new LoginException("Error: Unable to locate JNDI group provider");
        }
        if (this.debug) {
            System.out.println("\t\t[JndiLoginModule] user provider: " + this.userProvider);
            System.out.println("\t\t[JndiLoginModule] group provider: " + this.groupProvider);
        }
        Label_0230: {
            if (this.tryFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    if (this.debug) {
                        System.out.println("\t\t[JndiLoginModule] tryFirstPass succeeded");
                    }
                    return true;
                }
                catch (final LoginException ex) {
                    this.cleanState();
                    if (this.debug) {
                        System.out.println("\t\t[JndiLoginModule] tryFirstPass failed with:" + ex.toString());
                    }
                    break Label_0230;
                }
            }
            if (this.useFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    if (this.debug) {
                        System.out.println("\t\t[JndiLoginModule] useFirstPass succeeded");
                    }
                    return true;
                }
                catch (final LoginException ex2) {
                    this.cleanState();
                    if (this.debug) {
                        System.out.println("\t\t[JndiLoginModule] useFirstPass failed");
                    }
                    throw ex2;
                }
            }
            try {
                this.attemptAuthentication(false);
                this.succeeded = true;
                if (this.debug) {
                    System.out.println("\t\t[JndiLoginModule] regular authentication succeeded");
                }
                return true;
            }
            catch (final LoginException ex3) {
                this.cleanState();
                if (this.debug) {
                    System.out.println("\t\t[JndiLoginModule] regular authentication failed");
                }
                throw ex3;
            }
        }
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.subject.isReadOnly()) {
            this.cleanState();
            throw new LoginException("Subject is Readonly");
        }
        if (!this.subject.getPrincipals().contains(this.userPrincipal)) {
            this.subject.getPrincipals().add(this.userPrincipal);
        }
        if (!this.subject.getPrincipals().contains(this.UIDPrincipal)) {
            this.subject.getPrincipals().add(this.UIDPrincipal);
        }
        if (!this.subject.getPrincipals().contains(this.GIDPrincipal)) {
            this.subject.getPrincipals().add(this.GIDPrincipal);
        }
        for (int i = 0; i < this.supplementaryGroups.size(); ++i) {
            if (!this.subject.getPrincipals().contains(this.supplementaryGroups.get(i))) {
                this.subject.getPrincipals().add(this.supplementaryGroups.get(i));
            }
        }
        if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]: added UnixPrincipal,");
            System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
            System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
            System.out.println("\t\t\t to Subject");
        }
        this.cleanState();
        return this.commitSucceeded = true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]: aborted authentication failed");
        }
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanState();
            this.userPrincipal = null;
            this.UIDPrincipal = null;
            this.GIDPrincipal = null;
            this.supplementaryGroups = new LinkedList<UnixNumericGroupPrincipal>();
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
            throw new LoginException("Subject is Readonly");
        }
        this.subject.getPrincipals().remove(this.userPrincipal);
        this.subject.getPrincipals().remove(this.UIDPrincipal);
        this.subject.getPrincipals().remove(this.GIDPrincipal);
        for (int i = 0; i < this.supplementaryGroups.size(); ++i) {
            this.subject.getPrincipals().remove(this.supplementaryGroups.get(i));
        }
        this.cleanState();
        this.succeeded = false;
        this.commitSucceeded = false;
        this.userPrincipal = null;
        this.UIDPrincipal = null;
        this.GIDPrincipal = null;
        this.supplementaryGroups = new LinkedList<UnixNumericGroupPrincipal>();
        if (this.debug) {
            System.out.println("\t\t[JndiLoginModule]: logged out Subject");
        }
        return true;
    }
    
    private void attemptAuthentication(final boolean b) throws LoginException {
        this.getUsernamePassword(b);
        try {
            final InitialContext initialContext = new InitialContext();
            this.ctx = (DirContext)initialContext.lookup(this.userProvider);
            final NamingEnumeration<SearchResult> search = this.ctx.search("", "(uid=" + this.username + ")", new SearchControls());
            if (!search.hasMore()) {
                if (this.debug) {
                    System.out.println("\t\t[JndiLoginModule]: User not found");
                }
                throw new FailedLoginException("User not found");
            }
            final Attributes attributes = search.next().getAttributes();
            if (!this.verifyPassword(new String((byte[])attributes.get("userPassword").get(), "UTF8").substring("{crypt}".length()), new String(this.password))) {
                if (this.debug) {
                    System.out.println("\t\t[JndiLoginModule] attemptAuthentication() failed");
                }
                throw new FailedLoginException("Login incorrect");
            }
            if (this.debug) {
                System.out.println("\t\t[JndiLoginModule] attemptAuthentication() succeeded");
            }
            if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
                this.sharedState.put("javax.security.auth.login.name", this.username);
                this.sharedState.put("javax.security.auth.login.password", this.password);
            }
            this.userPrincipal = new UnixPrincipal(this.username);
            final String s = (String)attributes.get("uidNumber").get();
            this.UIDPrincipal = new UnixNumericUserPrincipal(s);
            if (this.debug && s != null) {
                System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has UID: " + s);
            }
            final String s2 = (String)attributes.get("gidNumber").get();
            this.GIDPrincipal = new UnixNumericGroupPrincipal(s2, true);
            if (this.debug && s2 != null) {
                System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has GID: " + s2);
            }
            this.ctx = (DirContext)initialContext.lookup(this.groupProvider);
            final NamingEnumeration<SearchResult> search2 = this.ctx.search("", new BasicAttributes("memberUid", this.username));
            while (search2.hasMore()) {
                final String s3 = (String)search2.next().getAttributes().get("gidNumber").get();
                if (!s2.equals(s3)) {
                    this.supplementaryGroups.add(new UnixNumericGroupPrincipal(s3, false));
                    if (!this.debug || s3 == null) {
                        continue;
                    }
                    System.out.println("\t\t[JndiLoginModule] user: '" + this.username + "' has Supplementary Group: " + s3);
                }
            }
        }
        catch (final NamingException ex) {
            if (this.debug) {
                System.out.println("\t\t[JndiLoginModule]:  User not found");
                ex.printStackTrace();
            }
            throw new FailedLoginException("User not found");
        }
        catch (final UnsupportedEncodingException ex2) {
            if (this.debug) {
                System.out.println("\t\t[JndiLoginModule]:  password incorrectly encoded");
                ex2.printStackTrace();
            }
            throw new LoginException("Login failure due to incorrect password encoding in the password database");
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
        final String substring = this.userProvider.substring(0, this.userProvider.indexOf(":"));
        final Callback[] array = { new NameCallback(substring + " " + JndiLoginModule.rb.getString("username.")), new PasswordCallback(substring + " " + JndiLoginModule.rb.getString("password."), false) };
        try {
            this.callbackHandler.handle(array);
            this.username = ((NameCallback)array[0]).getName();
            final char[] password = ((PasswordCallback)array[1]).getPassword();
            System.arraycopy(password, 0, this.password = new char[password.length], 0, password.length);
            ((PasswordCallback)array[1]).clearPassword();
        }
        catch (final IOException ex) {
            throw new LoginException(ex.toString());
        }
        catch (final UnsupportedCallbackException ex2) {
            throw new LoginException("Error: " + ex2.getCallback().toString() + " not available to garner authentication information from the user");
        }
        if (this.strongDebug) {
            System.out.println("\t\t[JndiLoginModule] user entered username: " + this.username);
            System.out.print("\t\t[JndiLoginModule] user entered password: ");
            for (int i = 0; i < this.password.length; ++i) {
                System.out.print(this.password[i]);
            }
            System.out.println();
        }
    }
    
    private boolean verifyPassword(final String s, final String s2) {
        if (s == null) {
            return false;
        }
        final Crypt crypt = new Crypt();
        try {
            final byte[] bytes = s.getBytes("UTF8");
            final byte[] crypt2 = crypt.crypt(s2.getBytes("UTF8"), bytes);
            if (crypt2.length != bytes.length) {
                return false;
            }
            for (int i = 0; i < crypt2.length; ++i) {
                if (bytes[i] != crypt2[i]) {
                    return false;
                }
            }
        }
        catch (final UnsupportedEncodingException ex) {
            return false;
        }
        return true;
    }
    
    private void cleanState() {
        this.username = null;
        if (this.password != null) {
            for (int i = 0; i < this.password.length; ++i) {
                this.password[i] = ' ';
            }
            this.password = null;
        }
        this.ctx = null;
        if (this.clearPass) {
            this.sharedState.remove("javax.security.auth.login.name");
            this.sharedState.remove("javax.security.auth.login.password");
        }
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
    }
}
