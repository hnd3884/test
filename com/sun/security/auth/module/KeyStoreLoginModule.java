package com.sun.security.auth.module;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.security.Provider;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.security.AuthProvider;
import java.security.Key;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.util.List;
import java.security.cert.CertificateFactory;
import java.util.LinkedList;
import javax.security.auth.login.FailedLoginException;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import java.net.MalformedURLException;
import java.security.NoSuchProviderException;
import java.security.KeyStoreException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import java.io.InputStream;
import java.io.IOException;
import sun.security.util.Password;
import java.net.URL;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import java.util.Arrays;
import javax.security.auth.login.LoginException;
import java.io.File;
import javax.security.auth.x500.X500PrivateCredential;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import javax.security.auth.x500.X500Principal;
import java.security.KeyStore;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.TextOutputCallback;
import java.util.ResourceBundle;
import jdk.Exported;
import javax.security.auth.spi.LoginModule;

@Exported
public class KeyStoreLoginModule implements LoginModule
{
    private static final ResourceBundle rb;
    private static final int UNINITIALIZED = 0;
    private static final int INITIALIZED = 1;
    private static final int AUTHENTICATED = 2;
    private static final int LOGGED_IN = 3;
    private static final int PROTECTED_PATH = 0;
    private static final int TOKEN = 1;
    private static final int NORMAL = 2;
    private static final String NONE = "NONE";
    private static final String P11KEYSTORE = "PKCS11";
    private static final TextOutputCallback bannerCallback;
    private final ConfirmationCallback confirmationCallback;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;
    private Map<String, ?> options;
    private char[] keyStorePassword;
    private char[] privateKeyPassword;
    private KeyStore keyStore;
    private String keyStoreURL;
    private String keyStoreType;
    private String keyStoreProvider;
    private String keyStoreAlias;
    private String keyStorePasswordURL;
    private String privateKeyPasswordURL;
    private boolean debug;
    private X500Principal principal;
    private Certificate[] fromKeyStore;
    private CertPath certP;
    private X500PrivateCredential privateCredential;
    private int status;
    private boolean nullStream;
    private boolean token;
    private boolean protectedPath;
    
    public KeyStoreLoginModule() {
        this.confirmationCallback = new ConfirmationCallback(0, 2, 3);
        this.certP = null;
        this.status = 0;
        this.nullStream = false;
        this.token = false;
        this.protectedPath = false;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = (Map<String, Object>)sharedState;
        this.options = options;
        this.processOptions();
        this.status = 1;
    }
    
    private void processOptions() {
        this.keyStoreURL = (String)this.options.get("keyStoreURL");
        if (this.keyStoreURL == null) {
            this.keyStoreURL = "file:" + System.getProperty("user.home").replace(File.separatorChar, '/') + '/' + ".keystore";
        }
        else if ("NONE".equals(this.keyStoreURL)) {
            this.nullStream = true;
        }
        this.keyStoreType = (String)this.options.get("keyStoreType");
        if (this.keyStoreType == null) {
            this.keyStoreType = KeyStore.getDefaultType();
        }
        if ("PKCS11".equalsIgnoreCase(this.keyStoreType)) {
            this.token = true;
        }
        this.keyStoreProvider = (String)this.options.get("keyStoreProvider");
        this.keyStoreAlias = (String)this.options.get("keyStoreAlias");
        this.keyStorePasswordURL = (String)this.options.get("keyStorePasswordURL");
        this.privateKeyPasswordURL = (String)this.options.get("privateKeyPasswordURL");
        this.protectedPath = "true".equalsIgnoreCase((String)this.options.get("protected"));
        this.debug = "true".equalsIgnoreCase((String)this.options.get("debug"));
        if (this.debug) {
            this.debugPrint(null);
            this.debugPrint("keyStoreURL=" + this.keyStoreURL);
            this.debugPrint("keyStoreType=" + this.keyStoreType);
            this.debugPrint("keyStoreProvider=" + this.keyStoreProvider);
            this.debugPrint("keyStoreAlias=" + this.keyStoreAlias);
            this.debugPrint("keyStorePasswordURL=" + this.keyStorePasswordURL);
            this.debugPrint("privateKeyPasswordURL=" + this.privateKeyPasswordURL);
            this.debugPrint("protectedPath=" + this.protectedPath);
            this.debugPrint(null);
        }
    }
    
    @Override
    public boolean login() throws LoginException {
        switch (this.status) {
            default: {
                throw new LoginException("The login module is not initialized");
            }
            case 1:
            case 2: {
                if (this.token && !this.nullStream) {
                    throw new LoginException("if keyStoreType is PKCS11 then keyStoreURL must be NONE");
                }
                if (this.token && this.privateKeyPasswordURL != null) {
                    throw new LoginException("if keyStoreType is PKCS11 then privateKeyPasswordURL must not be specified");
                }
                if (this.protectedPath && (this.keyStorePasswordURL != null || this.privateKeyPasswordURL != null)) {
                    throw new LoginException("if protected is true then keyStorePasswordURL and privateKeyPasswordURL must not be specified");
                }
                if (this.protectedPath) {
                    this.getAliasAndPasswords(0);
                }
                else if (this.token) {
                    this.getAliasAndPasswords(1);
                }
                else {
                    this.getAliasAndPasswords(2);
                }
                try {
                    this.getKeyStoreInfo();
                }
                finally {
                    if (this.privateKeyPassword != null && this.privateKeyPassword != this.keyStorePassword) {
                        Arrays.fill(this.privateKeyPassword, '\0');
                        this.privateKeyPassword = null;
                    }
                    if (this.keyStorePassword != null) {
                        Arrays.fill(this.keyStorePassword, '\0');
                        this.keyStorePassword = null;
                    }
                }
                this.status = 2;
                return true;
            }
            case 3: {
                return true;
            }
        }
    }
    
    private void getAliasAndPasswords(final int n) throws LoginException {
        if (this.callbackHandler == null) {
            switch (n) {
                case 0: {
                    this.checkAlias();
                    break;
                }
                case 1: {
                    this.checkAlias();
                    this.checkStorePass();
                    break;
                }
                case 2: {
                    this.checkAlias();
                    this.checkStorePass();
                    this.checkKeyPass();
                    break;
                }
            }
        }
        else {
            NameCallback nameCallback;
            if (this.keyStoreAlias == null || this.keyStoreAlias.length() == 0) {
                nameCallback = new NameCallback(KeyStoreLoginModule.rb.getString("Keystore.alias."));
            }
            else {
                nameCallback = new NameCallback(KeyStoreLoginModule.rb.getString("Keystore.alias."), this.keyStoreAlias);
            }
            PasswordCallback passwordCallback = null;
            PasswordCallback passwordCallback2 = null;
            switch (n) {
                case 2: {
                    passwordCallback2 = new PasswordCallback(KeyStoreLoginModule.rb.getString("Private.key.password.optional."), false);
                }
                case 1: {
                    passwordCallback = new PasswordCallback(KeyStoreLoginModule.rb.getString("Keystore.password."), false);
                    break;
                }
            }
            this.prompt(nameCallback, passwordCallback, passwordCallback2);
        }
        if (this.debug) {
            this.debugPrint("alias=" + this.keyStoreAlias);
        }
    }
    
    private void checkAlias() throws LoginException {
        if (this.keyStoreAlias == null) {
            throw new LoginException("Need to specify an alias option to use KeyStoreLoginModule non-interactively.");
        }
    }
    
    private void checkStorePass() throws LoginException {
        if (this.keyStorePasswordURL == null) {
            throw new LoginException("Need to specify keyStorePasswordURL option to use KeyStoreLoginModule non-interactively.");
        }
        InputStream openStream = null;
        try {
            openStream = new URL(this.keyStorePasswordURL).openStream();
            this.keyStorePassword = Password.readPassword(openStream);
        }
        catch (final IOException ex) {
            final LoginException ex2 = new LoginException("Problem accessing keystore password \"" + this.keyStorePasswordURL + "\"");
            ex2.initCause(ex);
            throw ex2;
        }
        finally {
            if (openStream != null) {
                try {
                    openStream.close();
                }
                catch (final IOException ex3) {
                    final LoginException ex4 = new LoginException("Problem closing the keystore password stream");
                    ex4.initCause(ex3);
                    throw ex4;
                }
            }
        }
    }
    
    private void checkKeyPass() throws LoginException {
        if (this.privateKeyPasswordURL == null) {
            this.privateKeyPassword = this.keyStorePassword;
        }
        else {
            InputStream openStream = null;
            try {
                openStream = new URL(this.privateKeyPasswordURL).openStream();
                this.privateKeyPassword = Password.readPassword(openStream);
            }
            catch (final IOException ex) {
                final LoginException ex2 = new LoginException("Problem accessing private key password \"" + this.privateKeyPasswordURL + "\"");
                ex2.initCause(ex);
                throw ex2;
            }
            finally {
                if (openStream != null) {
                    try {
                        openStream.close();
                    }
                    catch (final IOException ex3) {
                        final LoginException ex4 = new LoginException("Problem closing the private key password stream");
                        ex4.initCause(ex3);
                        throw ex4;
                    }
                }
            }
        }
    }
    
    private void prompt(final NameCallback nameCallback, final PasswordCallback passwordCallback, final PasswordCallback passwordCallback2) throws LoginException {
        if (passwordCallback == null) {
            try {
                this.callbackHandler.handle(new Callback[] { KeyStoreLoginModule.bannerCallback, nameCallback, this.confirmationCallback });
            }
            catch (final IOException ex) {
                final LoginException ex2 = new LoginException("Problem retrieving keystore alias");
                ex2.initCause(ex);
                throw ex2;
            }
            catch (final UnsupportedCallbackException ex3) {
                throw new LoginException("Error: " + ex3.getCallback().toString() + " is not available to retrieve authentication  information from the user");
            }
            if (this.confirmationCallback.getSelectedIndex() == 2) {
                throw new LoginException("Login cancelled");
            }
            this.saveAlias(nameCallback);
        }
        else if (passwordCallback2 == null) {
            try {
                this.callbackHandler.handle(new Callback[] { KeyStoreLoginModule.bannerCallback, nameCallback, passwordCallback, this.confirmationCallback });
            }
            catch (final IOException ex4) {
                final LoginException ex5 = new LoginException("Problem retrieving keystore alias and password");
                ex5.initCause(ex4);
                throw ex5;
            }
            catch (final UnsupportedCallbackException ex6) {
                throw new LoginException("Error: " + ex6.getCallback().toString() + " is not available to retrieve authentication  information from the user");
            }
            if (this.confirmationCallback.getSelectedIndex() == 2) {
                throw new LoginException("Login cancelled");
            }
            this.saveAlias(nameCallback);
            this.saveStorePass(passwordCallback);
        }
        else {
            try {
                this.callbackHandler.handle(new Callback[] { KeyStoreLoginModule.bannerCallback, nameCallback, passwordCallback, passwordCallback2, this.confirmationCallback });
            }
            catch (final IOException ex7) {
                final LoginException ex8 = new LoginException("Problem retrieving keystore alias and passwords");
                ex8.initCause(ex7);
                throw ex8;
            }
            catch (final UnsupportedCallbackException ex9) {
                throw new LoginException("Error: " + ex9.getCallback().toString() + " is not available to retrieve authentication  information from the user");
            }
            if (this.confirmationCallback.getSelectedIndex() == 2) {
                throw new LoginException("Login cancelled");
            }
            this.saveAlias(nameCallback);
            this.saveStorePass(passwordCallback);
            this.saveKeyPass(passwordCallback2);
        }
    }
    
    private void saveAlias(final NameCallback nameCallback) {
        this.keyStoreAlias = nameCallback.getName();
    }
    
    private void saveStorePass(final PasswordCallback passwordCallback) {
        this.keyStorePassword = passwordCallback.getPassword();
        if (this.keyStorePassword == null) {
            this.keyStorePassword = new char[0];
        }
        passwordCallback.clearPassword();
    }
    
    private void saveKeyPass(final PasswordCallback passwordCallback) {
        this.privateKeyPassword = passwordCallback.getPassword();
        if (this.privateKeyPassword == null || this.privateKeyPassword.length == 0) {
            this.privateKeyPassword = this.keyStorePassword;
        }
        passwordCallback.clearPassword();
    }
    
    private void getKeyStoreInfo() throws LoginException {
        try {
            if (this.keyStoreProvider == null) {
                this.keyStore = KeyStore.getInstance(this.keyStoreType);
            }
            else {
                this.keyStore = KeyStore.getInstance(this.keyStoreType, this.keyStoreProvider);
            }
        }
        catch (final KeyStoreException ex) {
            final LoginException ex2 = new LoginException("The specified keystore type was not available");
            ex2.initCause(ex);
            throw ex2;
        }
        catch (final NoSuchProviderException ex3) {
            final LoginException ex4 = new LoginException("The specified keystore provider was not available");
            ex4.initCause(ex3);
            throw ex4;
        }
        InputStream openStream = null;
        try {
            if (this.nullStream) {
                this.keyStore.load(null, this.keyStorePassword);
            }
            else {
                openStream = new URL(this.keyStoreURL).openStream();
                this.keyStore.load(openStream, this.keyStorePassword);
            }
        }
        catch (final MalformedURLException ex5) {
            final LoginException ex6 = new LoginException("Incorrect keyStoreURL option");
            ex6.initCause(ex5);
            throw ex6;
        }
        catch (final GeneralSecurityException ex7) {
            final LoginException ex8 = new LoginException("Error initializing keystore");
            ex8.initCause(ex7);
            throw ex8;
        }
        catch (final IOException ex9) {
            final LoginException ex10 = new LoginException("Error initializing keystore");
            ex10.initCause(ex9);
            throw ex10;
        }
        finally {
            if (openStream != null) {
                try {
                    openStream.close();
                }
                catch (final IOException ex11) {
                    final LoginException ex12 = new LoginException("Error initializing keystore");
                    ex12.initCause(ex11);
                    throw ex12;
                }
            }
        }
        try {
            this.fromKeyStore = this.keyStore.getCertificateChain(this.keyStoreAlias);
            if (this.fromKeyStore == null || this.fromKeyStore.length == 0 || !(this.fromKeyStore[0] instanceof X509Certificate)) {
                throw new FailedLoginException("Unable to find X.509 certificate chain in keystore");
            }
            final LinkedList<Certificate> list = new LinkedList<Certificate>();
            for (int i = 0; i < this.fromKeyStore.length; ++i) {
                list.add(this.fromKeyStore[i]);
            }
            this.certP = CertificateFactory.getInstance("X.509").generateCertPath(list);
        }
        catch (final KeyStoreException ex13) {
            final LoginException ex14 = new LoginException("Error using keystore");
            ex14.initCause(ex13);
            throw ex14;
        }
        catch (final CertificateException ex15) {
            final LoginException ex16 = new LoginException("Error: X.509 Certificate type unavailable");
            ex16.initCause(ex15);
            throw ex16;
        }
        try {
            final X509Certificate x509Certificate = (X509Certificate)this.fromKeyStore[0];
            this.principal = new X500Principal(x509Certificate.getSubjectDN().getName());
            final Key key = this.keyStore.getKey(this.keyStoreAlias, this.privateKeyPassword);
            if (key == null || !(key instanceof PrivateKey)) {
                throw new FailedLoginException("Unable to recover key from keystore");
            }
            this.privateCredential = new X500PrivateCredential(x509Certificate, (PrivateKey)key, this.keyStoreAlias);
        }
        catch (final KeyStoreException ex17) {
            final LoginException ex18 = new LoginException("Error using keystore");
            ex18.initCause(ex17);
            throw ex18;
        }
        catch (final NoSuchAlgorithmException ex19) {
            final LoginException ex20 = new LoginException("Error using keystore");
            ex20.initCause(ex19);
            throw ex20;
        }
        catch (final UnrecoverableKeyException ex21) {
            final FailedLoginException ex22 = new FailedLoginException("Unable to recover key from keystore");
            ex22.initCause(ex21);
            throw ex22;
        }
        if (this.debug) {
            this.debugPrint("principal=" + this.principal + "\n certificate=" + this.privateCredential.getCertificate() + "\n alias =" + this.privateCredential.getAlias());
        }
    }
    
    @Override
    public boolean commit() throws LoginException {
        switch (this.status) {
            default: {
                throw new LoginException("The login module is not initialized");
            }
            case 1: {
                this.logoutInternal();
                throw new LoginException("Authentication failed");
            }
            case 2: {
                if (this.commitInternal()) {
                    return true;
                }
                this.logoutInternal();
                throw new LoginException("Unable to retrieve certificates");
            }
            case 3: {
                return true;
            }
        }
    }
    
    private boolean commitInternal() throws LoginException {
        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject is set readonly");
        }
        this.subject.getPrincipals().add(this.principal);
        this.subject.getPublicCredentials().add(this.certP);
        this.subject.getPrivateCredentials().add(this.privateCredential);
        this.status = 3;
        return true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        switch (this.status) {
            default: {
                return false;
            }
            case 1: {
                return false;
            }
            case 2: {
                this.logoutInternal();
                return true;
            }
            case 3: {
                this.logoutInternal();
                return true;
            }
        }
    }
    
    @Override
    public boolean logout() throws LoginException {
        if (this.debug) {
            this.debugPrint("Entering logout " + this.status);
        }
        switch (this.status) {
            case 0: {
                throw new LoginException("The login module is not initialized");
            }
            default: {
                return false;
            }
            case 3: {
                this.logoutInternal();
                return true;
            }
        }
    }
    
    private void logoutInternal() throws LoginException {
        if (this.debug) {
            this.debugPrint("Entering logoutInternal");
        }
        LoginException ex = null;
        final Provider provider = this.keyStore.getProvider();
        if (provider instanceof AuthProvider) {
            final AuthProvider authProvider = (AuthProvider)provider;
            try {
                authProvider.logout();
                if (this.debug) {
                    this.debugPrint("logged out of KeyStore AuthProvider");
                }
            }
            catch (final LoginException ex2) {
                ex = ex2;
            }
        }
        if (this.subject.isReadOnly()) {
            this.principal = null;
            this.certP = null;
            this.status = 1;
            for (final Destroyable next : this.subject.getPrivateCredentials()) {
                if (this.privateCredential.equals(next)) {
                    this.privateCredential = null;
                    try {
                        next.destroy();
                        if (this.debug) {
                            this.debugPrint("Destroyed private credential, " + next.getClass().getName());
                        }
                        break;
                    }
                    catch (final DestroyFailedException ex3) {
                        final LoginException ex4 = new LoginException("Unable to destroy private credential, " + next.getClass().getName());
                        ex4.initCause(ex3);
                        throw ex4;
                    }
                }
            }
            throw new LoginException("Unable to remove Principal (X500Principal ) and public credential (certificatepath) from read-only Subject");
        }
        if (this.principal != null) {
            this.subject.getPrincipals().remove(this.principal);
            this.principal = null;
        }
        if (this.certP != null) {
            this.subject.getPublicCredentials().remove(this.certP);
            this.certP = null;
        }
        if (this.privateCredential != null) {
            this.subject.getPrivateCredentials().remove(this.privateCredential);
            this.privateCredential = null;
        }
        if (ex != null) {
            throw ex;
        }
        this.status = 1;
    }
    
    private void debugPrint(final String s) {
        if (s == null) {
            System.err.println();
        }
        else {
            System.err.println("Debug KeyStoreLoginModule: " + s);
        }
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
        bannerCallback = new TextOutputCallback(0, KeyStoreLoginModule.rb.getString("Please.enter.keystore.information"));
    }
}
