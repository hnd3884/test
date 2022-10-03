package com.sun.security.auth.module;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.security.auth.DestroyFailedException;
import java.util.Iterator;
import java.security.Principal;
import java.util.Set;
import sun.security.krb5.KerberosSecrets;
import javax.security.auth.RefreshFailedException;
import java.util.Date;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.NameCallback;
import java.text.MessageFormat;
import javax.security.auth.callback.Callback;
import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.jgss.krb5.Krb5Util;
import java.io.File;
import sun.security.krb5.KrbException;
import javax.security.auth.login.LoginException;
import sun.security.krb5.Config;
import java.util.ResourceBundle;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KerberosPrincipal;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Credentials;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.EncryptionKey;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import jdk.Exported;
import javax.security.auth.spi.LoginModule;

@Exported
public class Krb5LoginModule implements LoginModule
{
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;
    private Map<String, ?> options;
    private boolean debug;
    private boolean storeKey;
    private boolean doNotPrompt;
    private boolean useTicketCache;
    private boolean useKeyTab;
    private String ticketCacheName;
    private String keyTabName;
    private String princName;
    private boolean useFirstPass;
    private boolean tryFirstPass;
    private boolean storePass;
    private boolean clearPass;
    private boolean refreshKrb5Config;
    private boolean renewTGT;
    private boolean isInitiator;
    private boolean succeeded;
    private boolean commitSucceeded;
    private String username;
    private EncryptionKey[] encKeys;
    KeyTab ktab;
    private Credentials cred;
    private PrincipalName principal;
    private KerberosPrincipal kerbClientPrinc;
    private KerberosTicket kerbTicket;
    private KerberosKey[] kerbKeys;
    private StringBuffer krb5PrincName;
    private boolean unboundServer;
    private char[] password;
    private static final String NAME = "javax.security.auth.login.name";
    private static final String PWD = "javax.security.auth.login.password";
    private static final ResourceBundle rb;
    
    public Krb5LoginModule() {
        this.debug = false;
        this.storeKey = false;
        this.doNotPrompt = false;
        this.useTicketCache = false;
        this.useKeyTab = false;
        this.ticketCacheName = null;
        this.keyTabName = null;
        this.princName = null;
        this.useFirstPass = false;
        this.tryFirstPass = false;
        this.storePass = false;
        this.clearPass = false;
        this.refreshKrb5Config = false;
        this.renewTGT = false;
        this.isInitiator = true;
        this.succeeded = false;
        this.commitSucceeded = false;
        this.encKeys = null;
        this.ktab = null;
        this.cred = null;
        this.principal = null;
        this.kerbClientPrinc = null;
        this.kerbTicket = null;
        this.kerbKeys = null;
        this.krb5PrincName = null;
        this.unboundServer = false;
        this.password = null;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = (Map<String, Object>)sharedState;
        this.options = options;
        this.debug = "true".equalsIgnoreCase((String)options.get("debug"));
        this.storeKey = "true".equalsIgnoreCase((String)options.get("storeKey"));
        this.doNotPrompt = "true".equalsIgnoreCase((String)options.get("doNotPrompt"));
        this.useTicketCache = "true".equalsIgnoreCase((String)options.get("useTicketCache"));
        this.useKeyTab = "true".equalsIgnoreCase((String)options.get("useKeyTab"));
        this.ticketCacheName = (String)options.get("ticketCache");
        this.keyTabName = (String)options.get("keyTab");
        if (this.keyTabName != null) {
            this.keyTabName = sun.security.krb5.internal.ktab.KeyTab.normalize(this.keyTabName);
        }
        this.princName = (String)options.get("principal");
        this.refreshKrb5Config = "true".equalsIgnoreCase((String)options.get("refreshKrb5Config"));
        this.renewTGT = "true".equalsIgnoreCase((String)options.get("renewTGT"));
        final String s = (String)options.get("isInitiator");
        if (s != null) {
            this.isInitiator = "true".equalsIgnoreCase(s);
        }
        this.tryFirstPass = "true".equalsIgnoreCase((String)options.get("tryFirstPass"));
        this.useFirstPass = "true".equalsIgnoreCase((String)options.get("useFirstPass"));
        this.storePass = "true".equalsIgnoreCase((String)options.get("storePass"));
        this.clearPass = "true".equalsIgnoreCase((String)options.get("clearPass"));
        if (this.debug) {
            System.out.print("Debug is  " + this.debug + " storeKey " + this.storeKey + " useTicketCache " + this.useTicketCache + " useKeyTab " + this.useKeyTab + " doNotPrompt " + this.doNotPrompt + " ticketCache is " + this.ticketCacheName + " isInitiator " + this.isInitiator + " KeyTab is " + this.keyTabName + " refreshKrb5Config is " + this.refreshKrb5Config + " principal is " + this.princName + " tryFirstPass is " + this.tryFirstPass + " useFirstPass is " + this.useFirstPass + " storePass is " + this.storePass + " clearPass is " + this.clearPass + "\n");
        }
    }
    
    @Override
    public boolean login() throws LoginException {
        if (this.refreshKrb5Config) {
            try {
                if (this.debug) {
                    System.out.println("Refreshing Kerberos configuration");
                }
                Config.refresh();
            }
            catch (final KrbException ex) {
                final LoginException ex2 = new LoginException(ex.getMessage());
                ex2.initCause(ex);
                throw ex2;
            }
        }
        final String property = System.getProperty("sun.security.krb5.principal");
        if (property != null) {
            this.krb5PrincName = new StringBuffer(property);
        }
        else if (this.princName != null) {
            this.krb5PrincName = new StringBuffer(this.princName);
        }
        this.validateConfiguration();
        if (this.krb5PrincName != null && this.krb5PrincName.toString().equals("*")) {
            this.unboundServer = true;
        }
        Label_0278: {
            if (this.tryFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    if (this.debug) {
                        System.out.println("\t\t[Krb5LoginModule] authentication succeeded");
                    }
                    this.succeeded = true;
                    this.cleanState();
                    return true;
                }
                catch (final LoginException ex3) {
                    this.cleanState();
                    if (this.debug) {
                        System.out.println("\t\t[Krb5LoginModule] tryFirstPass failed with:" + ex3.getMessage());
                    }
                    break Label_0278;
                }
            }
            if (this.useFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    this.cleanState();
                    return true;
                }
                catch (final LoginException ex4) {
                    if (this.debug) {
                        System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + ex4.getMessage());
                    }
                    this.succeeded = false;
                    this.cleanState();
                    throw ex4;
                }
            }
            try {
                this.attemptAuthentication(false);
                this.succeeded = true;
                this.cleanState();
                return true;
            }
            catch (final LoginException ex5) {
                if (this.debug) {
                    System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + ex5.getMessage());
                }
                this.succeeded = false;
                this.cleanState();
                throw ex5;
            }
        }
    }
    
    private void attemptAuthentication(final boolean b) throws LoginException {
        if (this.krb5PrincName != null) {
            try {
                this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
            }
            catch (final KrbException ex) {
                final LoginException ex2 = new LoginException(ex.getMessage());
                ex2.initCause(ex);
                throw ex2;
            }
        }
        try {
            if (this.useTicketCache) {
                if (this.debug) {
                    System.out.println("Acquire TGT from Cache");
                }
                this.cred = Credentials.acquireTGTFromCache(this.principal, this.ticketCacheName);
                if (this.cred != null) {
                    if (this.renewTGT && isOld(this.cred)) {
                        final Credentials renewCredentials = this.renewCredentials(this.cred);
                        if (renewCredentials != null) {
                            renewCredentials.setProxy(this.cred.getProxy());
                            this.cred = renewCredentials;
                        }
                    }
                    if (!isCurrent(this.cred)) {
                        this.cred = null;
                        if (this.debug) {
                            System.out.println("Credentials are no longer valid");
                        }
                    }
                }
                if (this.cred != null && this.principal == null) {
                    this.principal = this.cred.getClient();
                }
                if (this.debug) {
                    System.out.println("Principal is " + this.principal);
                    if (this.cred == null) {
                        System.out.println("null credentials from Ticket Cache");
                    }
                }
            }
            if (this.cred == null) {
                if (this.principal == null) {
                    this.promptForName(b);
                    this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
                }
                if (this.useKeyTab) {
                    if (!this.unboundServer) {
                        final KerberosPrincipal kerberosPrincipal = new KerberosPrincipal(this.principal.getName());
                        this.ktab = ((this.keyTabName == null) ? KeyTab.getInstance(kerberosPrincipal) : KeyTab.getInstance(kerberosPrincipal, new File(this.keyTabName)));
                    }
                    else {
                        this.ktab = ((this.keyTabName == null) ? KeyTab.getUnboundInstance() : KeyTab.getUnboundInstance(new File(this.keyTabName)));
                    }
                    if (this.isInitiator && Krb5Util.keysFromJavaxKeyTab(this.ktab, this.principal).length == 0) {
                        this.ktab = null;
                        if (this.debug) {
                            System.out.println("Key for the principal " + this.principal + " not available in " + ((this.keyTabName == null) ? "default key tab" : this.keyTabName));
                        }
                    }
                }
                KrbAsReqBuilder krbAsReqBuilder;
                if (this.ktab == null) {
                    this.promptForPass(b);
                    krbAsReqBuilder = new KrbAsReqBuilder(this.principal, this.password);
                    if (this.isInitiator) {
                        this.cred = krbAsReqBuilder.action().getCreds();
                    }
                    if (this.storeKey) {
                        this.encKeys = krbAsReqBuilder.getKeys(this.isInitiator);
                    }
                }
                else {
                    krbAsReqBuilder = new KrbAsReqBuilder(this.principal, this.ktab);
                    if (this.isInitiator) {
                        this.cred = krbAsReqBuilder.action().getCreds();
                    }
                }
                krbAsReqBuilder.destroy();
                if (this.debug) {
                    System.out.println("principal is " + this.principal);
                    final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
                    if (this.ktab != null) {
                        System.out.println("Will use keytab");
                    }
                    else if (this.storeKey) {
                        for (int i = 0; i < this.encKeys.length; ++i) {
                            System.out.println("EncryptionKey: keyType=" + this.encKeys[i].getEType() + " keyBytes (hex dump)=" + hexDumpEncoder.encodeBuffer(this.encKeys[i].getBytes()));
                        }
                    }
                }
                if (this.isInitiator && this.cred == null) {
                    throw new LoginException("TGT Can not be obtained from the KDC ");
                }
            }
        }
        catch (final KrbException ex3) {
            final LoginException ex4 = new LoginException(ex3.getMessage());
            ex4.initCause(ex3);
            throw ex4;
        }
        catch (final IOException ex5) {
            final LoginException ex6 = new LoginException(ex5.getMessage());
            ex6.initCause(ex5);
            throw ex6;
        }
    }
    
    private void promptForName(final boolean b) throws LoginException {
        this.krb5PrincName = new StringBuffer("");
        if (b) {
            this.username = this.sharedState.get("javax.security.auth.login.name");
            if (this.debug) {
                System.out.println("username from shared state is " + this.username + "\n");
            }
            if (this.username == null) {
                System.out.println("username from shared state is null\n");
                throw new LoginException("Username can not be obtained from sharedstate ");
            }
            if (this.debug) {
                System.out.println("username from shared state is " + this.username + "\n");
            }
            if (this.username != null && this.username.length() > 0) {
                this.krb5PrincName.insert(0, this.username);
                return;
            }
        }
        if (this.doNotPrompt) {
            throw new LoginException("Unable to obtain Principal Name for authentication ");
        }
        if (this.callbackHandler == null) {
            throw new LoginException("No CallbackHandler available to garner authentication information from the user");
        }
        try {
            final String property = System.getProperty("user.name");
            final Callback[] array = { new NameCallback(new MessageFormat(Krb5LoginModule.rb.getString("Kerberos.username.defUsername.")).format(new Object[] { property })) };
            this.callbackHandler.handle(array);
            this.username = ((NameCallback)array[0]).getName();
            if (this.username == null || this.username.length() == 0) {
                this.username = property;
            }
            this.krb5PrincName.insert(0, this.username);
        }
        catch (final IOException ex) {
            throw new LoginException(ex.getMessage());
        }
        catch (final UnsupportedCallbackException ex2) {
            throw new LoginException(ex2.getMessage() + " not available to garner  authentication information  from the user");
        }
    }
    
    private void promptForPass(final boolean b) throws LoginException {
        if (b) {
            this.password = this.sharedState.get("javax.security.auth.login.password");
            if (this.password == null) {
                if (this.debug) {
                    System.out.println("Password from shared state is null");
                }
                throw new LoginException("Password can not be obtained from sharedstate ");
            }
            if (this.debug) {
                System.out.println("password is " + new String(this.password));
            }
        }
        else {
            if (this.doNotPrompt) {
                throw new LoginException("Unable to obtain password from user\n");
            }
            if (this.callbackHandler == null) {
                throw new LoginException("No CallbackHandler available to garner authentication information from the user");
            }
            try {
                final Callback[] array = { new PasswordCallback(new MessageFormat(Krb5LoginModule.rb.getString("Kerberos.password.for.username.")).format(new Object[] { this.krb5PrincName.toString() }), false) };
                this.callbackHandler.handle(array);
                final char[] password = ((PasswordCallback)array[0]).getPassword();
                if (password == null) {
                    throw new LoginException("No password provided");
                }
                System.arraycopy(password, 0, this.password = new char[password.length], 0, password.length);
                ((PasswordCallback)array[0]).clearPassword();
                for (int i = 0; i < password.length; ++i) {
                    password[i] = ' ';
                }
                if (this.debug) {
                    System.out.println("\t\t[Krb5LoginModule] user entered username: " + (Object)this.krb5PrincName);
                    System.out.println();
                }
            }
            catch (final IOException ex) {
                throw new LoginException(ex.getMessage());
            }
            catch (final UnsupportedCallbackException ex2) {
                throw new LoginException(ex2.getMessage() + " not available to garner  authentication information from the user");
            }
        }
    }
    
    private void validateConfiguration() throws LoginException {
        if (this.doNotPrompt && !this.useTicketCache && !this.useKeyTab && !this.tryFirstPass && !this.useFirstPass) {
            throw new LoginException("Configuration Error - either doNotPrompt should be  false or at least one of useTicketCache,  useKeyTab, tryFirstPass and useFirstPass should be true");
        }
        if (this.ticketCacheName != null && !this.useTicketCache) {
            throw new LoginException("Configuration Error  - useTicketCache should be set to true to use the ticket cache" + this.ticketCacheName);
        }
        if (this.keyTabName != null & !this.useKeyTab) {
            throw new LoginException("Configuration Error - useKeyTab should be set to true to use the keytab" + this.keyTabName);
        }
        if (this.storeKey && this.doNotPrompt && !this.useKeyTab && !this.tryFirstPass && !this.useFirstPass) {
            throw new LoginException("Configuration Error - either doNotPrompt should be set to  false or at least one of tryFirstPass, useFirstPass or useKeyTab must be set to true for storeKey option");
        }
        if (this.renewTGT && !this.useTicketCache) {
            throw new LoginException("Configuration Error - either useTicketCache should be  true or renewTGT should be false");
        }
        if (this.krb5PrincName != null && this.krb5PrincName.toString().equals("*") && this.isInitiator) {
            throw new LoginException("Configuration Error - principal cannot be * when isInitiator is true");
        }
    }
    
    private static boolean isCurrent(final Credentials credentials) {
        final Date endTime = credentials.getEndTime();
        return endTime == null || System.currentTimeMillis() <= endTime.getTime();
    }
    
    private static boolean isOld(final Credentials credentials) {
        final Date endTime = credentials.getEndTime();
        if (endTime == null) {
            return false;
        }
        final Date authTime = credentials.getAuthTime();
        final long currentTimeMillis = System.currentTimeMillis();
        if (authTime != null) {
            return currentTimeMillis - authTime.getTime() > endTime.getTime() - currentTimeMillis;
        }
        return currentTimeMillis <= endTime.getTime() - 7200000L;
    }
    
    private Credentials renewCredentials(final Credentials credentials) {
        Credentials renew;
        try {
            if (!credentials.isRenewable()) {
                throw new RefreshFailedException("This ticket is not renewable");
            }
            if (credentials.getRenewTill() == null) {
                return credentials;
            }
            if (System.currentTimeMillis() > this.cred.getRenewTill().getTime()) {
                throw new RefreshFailedException("This ticket is past its last renewal time.");
            }
            renew = credentials.renew();
            if (this.debug) {
                System.out.println("Renewed Kerberos Ticket");
            }
        }
        catch (final Exception ex) {
            renew = null;
            if (this.debug) {
                System.out.println("Ticket could not be renewed : " + ex.getMessage());
            }
        }
        return renew;
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.isInitiator && this.cred == null) {
            this.succeeded = false;
            throw new LoginException("Null Client Credential");
        }
        if (this.subject.isReadOnly()) {
            this.cleanKerberosCred();
            throw new LoginException("Subject is Readonly");
        }
        final Set<Object> privateCredentials = this.subject.getPrivateCredentials();
        final Set<Principal> principals = this.subject.getPrincipals();
        this.kerbClientPrinc = new KerberosPrincipal(this.principal.getName());
        if (this.isInitiator) {
            this.kerbTicket = Krb5Util.credsToTicket(this.cred);
            if (this.cred.getProxy() != null) {
                KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetProxy(this.kerbTicket, Krb5Util.credsToTicket(this.cred.getProxy()));
            }
        }
        if (this.storeKey && this.encKeys != null) {
            if (this.encKeys.length == 0) {
                this.succeeded = false;
                throw new LoginException("Null Server Key ");
            }
            this.kerbKeys = new KerberosKey[this.encKeys.length];
            for (int i = 0; i < this.encKeys.length; ++i) {
                final Integer keyVersionNumber = this.encKeys[i].getKeyVersionNumber();
                this.kerbKeys[i] = new KerberosKey(this.kerbClientPrinc, this.encKeys[i].getBytes(), this.encKeys[i].getEType(), (keyVersionNumber == null) ? 0 : keyVersionNumber);
            }
        }
        if (!this.unboundServer && !principals.contains(this.kerbClientPrinc)) {
            principals.add(this.kerbClientPrinc);
        }
        if (this.kerbTicket != null && !privateCredentials.contains(this.kerbTicket)) {
            privateCredentials.add(this.kerbTicket);
        }
        if (this.storeKey) {
            if (this.encKeys == null) {
                if (this.ktab == null) {
                    this.succeeded = false;
                    throw new LoginException("No key to store");
                }
                if (!privateCredentials.contains(this.ktab)) {
                    privateCredentials.add(this.ktab);
                }
            }
            else {
                for (int j = 0; j < this.kerbKeys.length; ++j) {
                    if (!privateCredentials.contains(this.kerbKeys[j])) {
                        privateCredentials.add(this.kerbKeys[j]);
                    }
                    this.encKeys[j].destroy();
                    this.encKeys[j] = null;
                    if (this.debug) {
                        System.out.println("Added server's key" + this.kerbKeys[j]);
                        System.out.println("\t\t[Krb5LoginModule] added Krb5Principal  " + this.kerbClientPrinc.toString() + " to Subject");
                    }
                }
            }
        }
        this.commitSucceeded = true;
        if (this.debug) {
            System.out.println("Commit Succeeded \n");
        }
        return true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanKerberosCred();
        }
        else {
            this.logout();
        }
        return true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        if (this.debug) {
            System.out.println("\t\t[Krb5LoginModule]: Entering logout");
        }
        if (this.subject.isReadOnly()) {
            this.cleanKerberosCred();
            throw new LoginException("Subject is Readonly");
        }
        this.subject.getPrincipals().remove(this.kerbClientPrinc);
        final Iterator<Object> iterator = this.subject.getPrivateCredentials().iterator();
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            if (next instanceof KerberosTicket || next instanceof KerberosKey || next instanceof KeyTab) {
                iterator.remove();
            }
        }
        this.cleanKerberosCred();
        this.succeeded = false;
        this.commitSucceeded = false;
        if (this.debug) {
            System.out.println("\t\t[Krb5LoginModule]: logged out Subject");
        }
        return true;
    }
    
    private void cleanKerberosCred() throws LoginException {
        try {
            if (this.kerbTicket != null) {
                this.kerbTicket.destroy();
            }
            if (this.kerbKeys != null) {
                for (int i = 0; i < this.kerbKeys.length; ++i) {
                    this.kerbKeys[i].destroy();
                }
            }
        }
        catch (final DestroyFailedException ex) {
            throw new LoginException("Destroy Failed on Kerberos Private Credentials");
        }
        this.kerbTicket = null;
        this.kerbKeys = null;
        this.kerbClientPrinc = null;
    }
    
    private void cleanState() {
        if (this.succeeded) {
            if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
                this.sharedState.put("javax.security.auth.login.name", this.username);
                this.sharedState.put("javax.security.auth.login.password", this.password);
            }
        }
        else {
            this.encKeys = null;
            this.ktab = null;
            this.principal = null;
        }
        this.username = null;
        this.password = null;
        if (this.krb5PrincName != null && this.krb5PrincName.length() != 0) {
            this.krb5PrincName.delete(0, this.krb5PrincName.length());
        }
        this.krb5PrincName = null;
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
