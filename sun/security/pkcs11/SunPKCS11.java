package sun.security.pkcs11;

import java.io.NotSerializableException;
import java.io.Serializable;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.InvalidParameterException;
import java.security.Key;
import sun.security.util.GCMParameters;
import sun.security.util.ECParameters;
import java.security.NoSuchAlgorithmException;
import java.io.ObjectStreamException;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import javax.security.auth.login.LoginException;
import java.security.Permission;
import java.security.SecurityPermission;
import javax.security.auth.Subject;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;
import java.security.Provider;
import sun.security.pkcs11.wrapper.Functions;
import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import sun.security.pkcs11.wrapper.CK_SLOT_INFO;
import sun.security.pkcs11.wrapper.CK_INFO;
import java.util.Iterator;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProviderException;
import java.util.List;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.util.Debug;
import java.security.AuthProvider;

public final class SunPKCS11 extends AuthProvider
{
    private static final long serialVersionUID = -1354835039035306505L;
    static final Debug debug;
    private static int dummyConfigId;
    final PKCS11 p11;
    private final String configName;
    final Config config;
    final long slotID;
    private CallbackHandler pHandler;
    private final Object LOCK_HANDLER;
    final boolean removable;
    final Secmod.Module nssModule;
    final boolean nssUseSecmodTrust;
    private volatile Token token;
    private TokenPoller poller;
    private static final Map<Integer, List<Descriptor>> descriptors;
    private static final String MD = "MessageDigest";
    private static final String SIG = "Signature";
    private static final String KPG = "KeyPairGenerator";
    private static final String KG = "KeyGenerator";
    private static final String AGP = "AlgorithmParameters";
    private static final String KF = "KeyFactory";
    private static final String SKF = "SecretKeyFactory";
    private static final String CIP = "Cipher";
    private static final String MAC = "Mac";
    private static final String KA = "KeyAgreement";
    private static final String KS = "KeyStore";
    private static final String SR = "SecureRandom";
    
    Token getToken() {
        return this.token;
    }
    
    public SunPKCS11() {
        super("SunPKCS11-Dummy", 1.8, "SunPKCS11-Dummy");
        this.LOCK_HANDLER = new Object();
        throw new ProviderException("SunPKCS11 requires configuration file argument");
    }
    
    public SunPKCS11(final String s) {
        this(checkNull(s), null);
    }
    
    public SunPKCS11(final InputStream inputStream) {
        this(getDummyConfigName(), checkNull(inputStream));
    }
    
    private static <T> T checkNull(final T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }
    
    private static synchronized String getDummyConfigName() {
        return "---DummyConfig-" + ++SunPKCS11.dummyConfigId + "---";
    }
    
    @Deprecated
    public SunPKCS11(final String configName, final InputStream inputStream) {
        super("SunPKCS11-" + Config.getConfig(configName, inputStream).getName(), 1.8, Config.getConfig(configName, inputStream).getDescription());
        this.LOCK_HANDLER = new Object();
        this.configName = configName;
        this.config = Config.removeConfig(configName);
        if (SunPKCS11.debug != null) {
            System.out.println("SunPKCS11 loading " + configName);
        }
        String s = this.config.getLibrary();
        String functionList = this.config.getFunctionList();
        long slotID = this.config.getSlotID();
        int n = this.config.getSlotListIndex();
        final boolean nssUseSecmod = this.config.getNssUseSecmod();
        boolean nssUseSecmodTrust = this.config.getNssUseSecmodTrust();
        Secmod.Module nssModule = null;
        if (nssUseSecmod) {
            final Secmod instance = Secmod.getInstance();
            final Secmod.DbMode nssDbMode = this.config.getNssDbMode();
            try {
                final String nssLibraryDirectory = this.config.getNssLibraryDirectory();
                final String nssSecmodDirectory = this.config.getNssSecmodDirectory();
                final boolean nssOptimizeSpace = this.config.getNssOptimizeSpace();
                if (instance.isInitialized()) {
                    if (nssSecmodDirectory != null) {
                        final String configDir = instance.getConfigDir();
                        if (configDir != null && !configDir.equals(nssSecmodDirectory)) {
                            throw new ProviderException("Secmod directory " + nssSecmodDirectory + " invalid, NSS already initialized with " + configDir);
                        }
                    }
                    if (nssLibraryDirectory != null) {
                        final String libDir = instance.getLibDir();
                        if (libDir != null && !libDir.equals(nssLibraryDirectory)) {
                            throw new ProviderException("NSS library directory " + nssLibraryDirectory + " invalid, NSS already initialized with " + libDir);
                        }
                    }
                }
                else {
                    if (nssDbMode != Secmod.DbMode.NO_DB) {
                        if (nssSecmodDirectory == null) {
                            throw new ProviderException("Secmod not initialized and nssSecmodDirectory not specified");
                        }
                    }
                    else if (nssSecmodDirectory != null) {
                        throw new ProviderException("nssSecmodDirectory must not be specified in noDb mode");
                    }
                    instance.initialize(nssDbMode, nssSecmodDirectory, nssLibraryDirectory, nssOptimizeSpace);
                }
            }
            catch (final IOException ex) {
                throw new ProviderException("Could not initialize NSS", ex);
            }
            final List<Secmod.Module> modules = instance.getModules();
            if (this.config.getShowInfo()) {
                System.out.println("NSS modules: " + modules);
            }
            String nssModule2 = this.config.getNssModule();
            if (nssModule2 == null) {
                nssModule = instance.getModule(Secmod.ModuleType.FIPS);
                if (nssModule != null) {
                    nssModule2 = "fips";
                }
                else {
                    nssModule2 = ((nssDbMode == Secmod.DbMode.NO_DB) ? "crypto" : "keystore");
                }
            }
            if (nssModule2.equals("fips")) {
                nssModule = instance.getModule(Secmod.ModuleType.FIPS);
                nssUseSecmodTrust = true;
                functionList = "FC_GetFunctionList";
            }
            else if (nssModule2.equals("keystore")) {
                nssModule = instance.getModule(Secmod.ModuleType.KEYSTORE);
                nssUseSecmodTrust = true;
            }
            else if (nssModule2.equals("crypto")) {
                nssModule = instance.getModule(Secmod.ModuleType.CRYPTO);
            }
            else if (nssModule2.equals("trustanchors")) {
                nssModule = instance.getModule(Secmod.ModuleType.TRUSTANCHOR);
                nssUseSecmodTrust = true;
            }
            else {
                if (!nssModule2.startsWith("external-")) {
                    throw new ProviderException("Unknown NSS module: " + nssModule2);
                }
                int int1;
                try {
                    int1 = Integer.parseInt(nssModule2.substring("external-".length()));
                }
                catch (final NumberFormatException ex2) {
                    int1 = -1;
                }
                if (int1 < 1) {
                    throw new ProviderException("Invalid external module: " + nssModule2);
                }
                int n2 = 0;
                for (final Secmod.Module module : modules) {
                    if (module.getType() == Secmod.ModuleType.EXTERNAL && ++n2 == int1) {
                        nssModule = module;
                        break;
                    }
                }
                if (nssModule == null) {
                    throw new ProviderException("Invalid module " + nssModule2 + ": only " + n2 + " external NSS modules available");
                }
            }
            if (nssModule == null) {
                throw new ProviderException("NSS module not available: " + nssModule2);
            }
            if (nssModule.hasInitializedProvider()) {
                throw new ProviderException("Secmod module already configured");
            }
            s = nssModule.libraryName;
            n = nssModule.slot;
        }
        this.nssUseSecmodTrust = nssUseSecmodTrust;
        this.nssModule = nssModule;
        if (new File(s).getName().equals(s) || new File(s).isFile()) {
            try {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("Initializing PKCS#11 library " + s);
                }
                CK_C_INITIALIZE_ARGS ck_C_INITIALIZE_ARGS = new CK_C_INITIALIZE_ARGS();
                final String nssArgs = this.config.getNssArgs();
                if (nssArgs != null) {
                    ck_C_INITIALIZE_ARGS.pReserved = nssArgs;
                }
                ck_C_INITIALIZE_ARGS.flags = 2L;
                PKCS11 p2;
                try {
                    p2 = PKCS11.getInstance(s, functionList, ck_C_INITIALIZE_ARGS, this.config.getOmitInitialize());
                }
                catch (final PKCS11Exception ex3) {
                    if (SunPKCS11.debug != null) {
                        SunPKCS11.debug.println("Multi-threaded initialization failed: " + ex3);
                    }
                    if (!this.config.getAllowSingleThreadedModules()) {
                        throw ex3;
                    }
                    if (nssArgs == null) {
                        ck_C_INITIALIZE_ARGS = null;
                    }
                    else {
                        ck_C_INITIALIZE_ARGS.flags = 0L;
                    }
                    p2 = PKCS11.getInstance(s, functionList, ck_C_INITIALIZE_ARGS, this.config.getOmitInitialize());
                }
                this.p11 = p2;
                final CK_INFO c_GetInfo = this.p11.C_GetInfo();
                if (c_GetInfo.cryptokiVersion.major < 2) {
                    throw new ProviderException("Only PKCS#11 v2.0 and later supported, library version is v" + c_GetInfo.cryptokiVersion);
                }
                final boolean showInfo = this.config.getShowInfo();
                if (showInfo) {
                    System.out.println("Information for provider " + this.getName());
                    System.out.println("Library info:");
                    System.out.println(c_GetInfo);
                }
                if (slotID < 0L || showInfo) {
                    long[] array = this.p11.C_GetSlotList(false);
                    if (showInfo) {
                        System.out.println("All slots: " + toString(array));
                        array = this.p11.C_GetSlotList(true);
                        System.out.println("Slots with tokens: " + toString(array));
                    }
                    if (slotID < 0L) {
                        if (n < 0 || n >= array.length) {
                            throw new ProviderException("slotListIndex is " + n + " but token only has " + array.length + " slots");
                        }
                        slotID = array[n];
                    }
                }
                this.slotID = slotID;
                final CK_SLOT_INFO c_GetSlotInfo = this.p11.C_GetSlotInfo(slotID);
                this.removable = ((c_GetSlotInfo.flags & 0x2L) != 0x0L);
                this.initToken(c_GetSlotInfo);
                if (nssModule != null) {
                    nssModule.setProvider(this);
                }
            }
            catch (final Exception ex4) {
                if (this.config.getHandleStartupErrors() == 2) {
                    throw new UnsupportedOperationException("Initialization failed", ex4);
                }
                throw new ProviderException("Initialization failed", ex4);
            }
            return;
        }
        final String string = "Library " + s + " does not exist";
        if (this.config.getHandleStartupErrors() == 1) {
            throw new ProviderException(string);
        }
        throw new UnsupportedOperationException(string);
    }
    
    private static String toString(final long[] array) {
        if (array.length == 0) {
            return "(none)";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        for (int i = 1; i < array.length; ++i) {
            sb.append(", ");
            sb.append(array[i]);
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    private static String[] s(final String... array) {
        return array;
    }
    
    private static int[] m(final long n) {
        return new int[] { (int)n };
    }
    
    private static int[] m(final long n, final long n2) {
        return new int[] { (int)n, (int)n2 };
    }
    
    private static int[] m(final long n, final long n2, final long n3) {
        return new int[] { (int)n, (int)n2, (int)n3 };
    }
    
    private static int[] m(final long n, final long n2, final long n3, final long n4) {
        return new int[] { (int)n, (int)n2, (int)n3, (int)n4 };
    }
    
    private static void d(final String s, final String s2, final String s3, final int[] array) {
        register(new Descriptor(s, s2, s3, (String[])null, array));
    }
    
    private static void d(final String s, final String s2, final String s3, final String[] array, final int[] array2) {
        register(new Descriptor(s, s2, s3, array, array2));
    }
    
    private static void register(final Descriptor descriptor) {
        for (int i = 0; i < descriptor.mechanisms.length; ++i) {
            final Integer value = descriptor.mechanisms[i];
            List list = SunPKCS11.descriptors.get(value);
            if (list == null) {
                list = new ArrayList();
                SunPKCS11.descriptors.put(value, list);
            }
            list.add(descriptor);
        }
    }
    
    private void createPoller() {
        if (this.poller != null) {
            return;
        }
        final TokenPoller poller = new TokenPoller(this);
        final Thread thread = new Thread(poller, "Poller " + this.getName());
        thread.setDaemon(true);
        thread.setPriority(1);
        thread.start();
        this.poller = poller;
    }
    
    private void destroyPoller() {
        if (this.poller != null) {
            this.poller.disable();
            this.poller = null;
        }
    }
    
    private boolean hasValidToken() {
        final Token token = this.token;
        return token != null && token.isValid();
    }
    
    synchronized void uninitToken(final Token token) {
        if (this.token != token) {
            return;
        }
        this.destroyPoller();
        this.token = null;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                SunPKCS11.this.clear();
                return null;
            }
        });
        this.createPoller();
    }
    
    private void initToken(CK_SLOT_INFO c_GetSlotInfo) throws PKCS11Exception {
        if (c_GetSlotInfo == null) {
            c_GetSlotInfo = this.p11.C_GetSlotInfo(this.slotID);
        }
        if (this.removable && (c_GetSlotInfo.flags & 0x1L) == 0x0L) {
            this.createPoller();
            return;
        }
        this.destroyPoller();
        final boolean showInfo = this.config.getShowInfo();
        if (showInfo) {
            System.out.println("Slot info for slot " + this.slotID + ":");
            System.out.println(c_GetSlotInfo);
        }
        final Token token = new Token(this);
        if (showInfo) {
            System.out.println("Token info for token in slot " + this.slotID + ":");
            System.out.println(token.tokenInfo);
        }
        final long[] c_GetMechanismList = this.p11.C_GetMechanismList(this.slotID);
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < c_GetMechanismList.length; ++i) {
            final long n = c_GetMechanismList[i];
            final boolean enabled = this.config.isEnabled(n);
            if (showInfo) {
                final CK_MECHANISM_INFO c_GetMechanismInfo = this.p11.C_GetMechanismInfo(this.slotID, n);
                System.out.println("Mechanism " + Functions.getMechanismName(n) + ":");
                if (!enabled) {
                    System.out.println("DISABLED in configuration");
                }
                System.out.println(c_GetMechanismInfo);
            }
            if (enabled) {
                if (n >>> 32 == 0L) {
                    final int n2 = (int)n;
                    final Integer value = n2;
                    final List list = SunPKCS11.descriptors.get(value);
                    if (list != null) {
                        for (final Descriptor descriptor : list) {
                            final Integer n3 = (Integer)hashMap.get(descriptor);
                            if (n3 == null) {
                                hashMap.put(descriptor, value);
                            }
                            else {
                                final int intValue = n3;
                                for (int j = 0; j < descriptor.mechanisms.length; ++j) {
                                    final int n4 = descriptor.mechanisms[j];
                                    if (n2 == n4) {
                                        hashMap.put(descriptor, value);
                                        break;
                                    }
                                    if (intValue == n4) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                for (final Map.Entry entry : hashMap.entrySet()) {
                    Provider.this.putService(((Descriptor)entry.getKey()).service(token, (int)entry.getValue()));
                }
                if ((token.tokenInfo.flags & 0x1L) != 0x0L && SunPKCS11.this.config.isEnabled(2147483424L) && !token.sessionManager.lowMaxSessions()) {
                    Provider.this.putService(new P11Service(token, "SecureRandom", "PKCS11", "sun.security.pkcs11.P11SecureRandom", null, 2147483424L));
                }
                if (SunPKCS11.this.config.isEnabled(2147483425L)) {
                    Provider.this.putService(new P11Service(token, "KeyStore", "PKCS11", "sun.security.pkcs11.P11KeyStore", s(new String[] { "PKCS11-" + SunPKCS11.this.config.getName() }), 2147483425L));
                }
                return null;
            }
        });
        this.token = token;
    }
    
    @Override
    public void login(final Subject subject, final CallbackHandler callbackHandler) throws LoginException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (SunPKCS11.debug != null) {
                SunPKCS11.debug.println("checking login permission");
            }
            securityManager.checkPermission(new SecurityPermission("authProvider." + this.getName()));
        }
        if (!this.hasValidToken()) {
            throw new LoginException("No token present");
        }
        if ((this.token.tokenInfo.flags & 0x4L) == 0x0L) {
            if (SunPKCS11.debug != null) {
                SunPKCS11.debug.println("login operation not required for token - ignoring login request");
            }
            return;
        }
        try {
            if (this.token.isLoggedInNow(null)) {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("user already logged in");
                }
                return;
            }
        }
        catch (final PKCS11Exception ex) {}
        char[] password = null;
        if ((this.token.tokenInfo.flags & 0x100L) == 0x0L) {
            final CallbackHandler callbackHandler2 = this.getCallbackHandler(callbackHandler);
            if (callbackHandler2 == null) {
                throw new LoginException("no password provided, and no callback handler available for retrieving password");
            }
            final PasswordCallback passwordCallback = new PasswordCallback(new MessageFormat(ResourcesMgr.getString("PKCS11.Token.providerName.Password.")).format(new Object[] { this.getName() }), false);
            final Callback[] array = { passwordCallback };
            try {
                callbackHandler2.handle(array);
            }
            catch (final Exception ex2) {
                final LoginException ex3 = new LoginException("Unable to perform password callback");
                ex3.initCause(ex2);
                throw ex3;
            }
            password = passwordCallback.getPassword();
            passwordCallback.clearPassword();
            if (password == null && SunPKCS11.debug != null) {
                SunPKCS11.debug.println("caller passed NULL pin");
            }
        }
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            this.p11.C_Login(opSession.id(), 1L, password);
            if (SunPKCS11.debug != null) {
                SunPKCS11.debug.println("login succeeded");
            }
        }
        catch (final PKCS11Exception ex4) {
            if (ex4.getErrorCode() == 256L) {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("user already logged in");
                }
                return;
            }
            if (ex4.getErrorCode() == 160L) {
                final FailedLoginException ex5 = new FailedLoginException();
                ex5.initCause(ex4);
                throw ex5;
            }
            final LoginException ex6 = new LoginException();
            ex6.initCause(ex4);
            throw ex6;
        }
        finally {
            this.token.releaseSession(opSession);
            if (password != null) {
                Arrays.fill(password, ' ');
            }
        }
    }
    
    @Override
    public void logout() throws LoginException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SecurityPermission("authProvider." + this.getName()));
        }
        if (!this.hasValidToken()) {
            return;
        }
        if ((this.token.tokenInfo.flags & 0x4L) == 0x0L) {
            if (SunPKCS11.debug != null) {
                SunPKCS11.debug.println("logout operation not required for token - ignoring logout request");
            }
            return;
        }
        try {
            if (!this.token.isLoggedInNow(null)) {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("user not logged in");
                }
                return;
            }
        }
        catch (final PKCS11Exception ex) {}
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            this.p11.C_Logout(opSession.id());
            if (SunPKCS11.debug != null) {
                SunPKCS11.debug.println("logout succeeded");
            }
        }
        catch (final PKCS11Exception ex2) {
            if (ex2.getErrorCode() == 257L) {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("user not logged in");
                }
                return;
            }
            final LoginException ex3 = new LoginException();
            ex3.initCause(ex2);
            throw ex3;
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    @Override
    public void setCallbackHandler(final CallbackHandler pHandler) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SecurityPermission("authProvider." + this.getName()));
        }
        synchronized (this.LOCK_HANDLER) {
            this.pHandler = pHandler;
        }
    }
    
    private CallbackHandler getCallbackHandler(final CallbackHandler callbackHandler) {
        if (callbackHandler != null) {
            return callbackHandler;
        }
        if (SunPKCS11.debug != null) {
            SunPKCS11.debug.println("getting provider callback handler");
        }
        synchronized (this.LOCK_HANDLER) {
            if (this.pHandler != null) {
                return this.pHandler;
            }
            try {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("getting default callback handler");
                }
                return this.pHandler = AccessController.doPrivileged((PrivilegedExceptionAction<CallbackHandler>)new PrivilegedExceptionAction<CallbackHandler>() {
                    @Override
                    public CallbackHandler run() throws Exception {
                        final String property = Security.getProperty("auth.login.defaultCallbackHandler");
                        if (property == null || property.length() == 0) {
                            if (SunPKCS11.debug != null) {
                                SunPKCS11.debug.println("no default handler set");
                            }
                            return null;
                        }
                        return (CallbackHandler)Class.forName(property, true, Thread.currentThread().getContextClassLoader()).newInstance();
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                if (SunPKCS11.debug != null) {
                    SunPKCS11.debug.println("Unable to load default callback handler");
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new SunPKCS11Rep(this);
    }
    
    static {
        debug = Debug.getInstance("sunpkcs11");
        descriptors = new HashMap<Integer, List<Descriptor>>();
        final String s = "sun.security.pkcs11.P11Digest";
        final String s2 = "sun.security.pkcs11.P11MAC";
        final String s3 = "sun.security.pkcs11.P11KeyPairGenerator";
        final String s4 = "sun.security.pkcs11.P11KeyGenerator";
        final String s5 = "sun.security.pkcs11.P11RSAKeyFactory";
        final String s6 = "sun.security.pkcs11.P11DSAKeyFactory";
        final String s7 = "sun.security.pkcs11.P11DHKeyFactory";
        final String s8 = "sun.security.pkcs11.P11KeyAgreement";
        final String s9 = "sun.security.pkcs11.P11SecretKeyFactory";
        final String s10 = "sun.security.pkcs11.P11Cipher";
        final String s11 = "sun.security.pkcs11.P11RSACipher";
        final String s12 = "sun.security.pkcs11.P11AEADCipher";
        final String s13 = "sun.security.pkcs11.P11Signature";
        final String s14 = "sun.security.pkcs11.P11PSSSignature";
        d("MessageDigest", "MD2", s, m(512L));
        d("MessageDigest", "MD5", s, m(528L));
        d("MessageDigest", "SHA1", s, s("SHA", "SHA-1", "1.3.14.3.2.26", "OID.1.3.14.3.2.26"), m(544L));
        d("MessageDigest", "SHA-224", s, s("2.16.840.1.101.3.4.2.4", "OID.2.16.840.1.101.3.4.2.4"), m(597L));
        d("MessageDigest", "SHA-256", s, s("2.16.840.1.101.3.4.2.1", "OID.2.16.840.1.101.3.4.2.1"), m(592L));
        d("MessageDigest", "SHA-384", s, s("2.16.840.1.101.3.4.2.2", "OID.2.16.840.1.101.3.4.2.2"), m(608L));
        d("MessageDigest", "SHA-512", s, s("2.16.840.1.101.3.4.2.3", "OID.2.16.840.1.101.3.4.2.3"), m(624L));
        d("MessageDigest", "SHA-512/224", s, s("2.16.840.1.101.3.4.2.5", "OID.2.16.840.1.101.3.4.2.5"), m(72L));
        d("MessageDigest", "SHA-512/256", s, s("2.16.840.1.101.3.4.2.6", "OID.2.16.840.1.101.3.4.2.6"), m(76L));
        d("Mac", "HmacMD5", s2, m(529L));
        d("Mac", "HmacSHA1", s2, s("1.2.840.113549.2.7", "OID.1.2.840.113549.2.7"), m(545L));
        d("Mac", "HmacSHA224", s2, s("1.2.840.113549.2.8", "OID.1.2.840.113549.2.8"), m(598L));
        d("Mac", "HmacSHA256", s2, s("1.2.840.113549.2.9", "OID.1.2.840.113549.2.9"), m(593L));
        d("Mac", "HmacSHA384", s2, s("1.2.840.113549.2.10", "OID.1.2.840.113549.2.10"), m(609L));
        d("Mac", "HmacSHA512", s2, s("1.2.840.113549.2.11", "OID.1.2.840.113549.2.11"), m(625L));
        d("Mac", "HmacSHA512/224", s2, s("1.2.840.113549.2.12", "OID.1.2.840.113549.2.12"), m(73L));
        d("Mac", "HmacSHA512/256", s2, s("1.2.840.113549.2.13", "OID.1.2.840.113549.2.13"), m(77L));
        d("Mac", "SslMacMD5", s2, m(896L));
        d("Mac", "SslMacSHA1", s2, m(897L));
        d("KeyPairGenerator", "RSA", s3, s("1.2.840.113549.1.1", "OID.1.2.840.113549.1.1"), m(0L));
        d("KeyPairGenerator", "DSA", s3, s("1.3.14.3.2.12", "1.2.840.10040.4.1", "OID.1.2.840.10040.4.1"), m(16L));
        d("KeyPairGenerator", "DH", s3, s("DiffieHellman"), m(32L));
        d("KeyPairGenerator", "EC", s3, m(4160L));
        d("KeyGenerator", "ARCFOUR", s4, s("RC4"), m(272L));
        d("KeyGenerator", "DES", s4, m(288L));
        d("KeyGenerator", "DESede", s4, m(305L, 304L));
        d("KeyGenerator", "AES", s4, m(4224L));
        d("KeyGenerator", "Blowfish", s4, m(4240L));
        d("KeyFactory", "RSA", s5, s("1.2.840.113549.1.1", "OID.1.2.840.113549.1.1"), m(0L, 1L, 3L));
        d("KeyFactory", "DSA", s6, s("1.3.14.3.2.12", "1.2.840.10040.4.1", "OID.1.2.840.10040.4.1"), m(16L, 17L, 18L));
        d("KeyFactory", "DH", s7, s("DiffieHellman"), m(32L, 33L));
        d("KeyFactory", "EC", s7, m(4160L, 4176L, 4161L, 4162L));
        d("AlgorithmParameters", "EC", "sun.security.util.ECParameters", s("1.2.840.10045.2.1"), m(4160L, 4176L, 4161L, 4162L));
        d("AlgorithmParameters", "GCM", "sun.security.util.GCMParameters", m(4231L));
        d("KeyAgreement", "DH", s8, s("DiffieHellman"), m(33L));
        d("KeyAgreement", "ECDH", "sun.security.pkcs11.P11ECDHKeyAgreement", m(4176L));
        d("SecretKeyFactory", "ARCFOUR", s9, s("RC4"), m(273L));
        d("SecretKeyFactory", "DES", s9, m(290L));
        d("SecretKeyFactory", "DESede", s9, m(307L));
        d("SecretKeyFactory", "AES", s9, s("2.16.840.1.101.3.4.1", "OID.2.16.840.1.101.3.4.1"), m(4226L));
        d("SecretKeyFactory", "Blowfish", s9, m(4241L));
        d("Cipher", "ARCFOUR", s10, s("RC4"), m(273L));
        d("Cipher", "DES/CBC/NoPadding", s10, m(290L));
        d("Cipher", "DES/CBC/PKCS5Padding", s10, m(293L, 290L));
        d("Cipher", "DES/ECB/NoPadding", s10, m(289L));
        d("Cipher", "DES/ECB/PKCS5Padding", s10, s("DES"), m(289L));
        d("Cipher", "DESede/CBC/NoPadding", s10, m(307L));
        d("Cipher", "DESede/CBC/PKCS5Padding", s10, m(310L, 307L));
        d("Cipher", "DESede/ECB/NoPadding", s10, m(306L));
        d("Cipher", "DESede/ECB/PKCS5Padding", s10, s("DESede"), m(306L));
        d("Cipher", "AES/CBC/NoPadding", s10, m(4226L));
        d("Cipher", "AES_128/CBC/NoPadding", s10, s("2.16.840.1.101.3.4.1.2", "OID.2.16.840.1.101.3.4.1.2"), m(4226L));
        d("Cipher", "AES_192/CBC/NoPadding", s10, s("2.16.840.1.101.3.4.1.22", "OID.2.16.840.1.101.3.4.1.22"), m(4226L));
        d("Cipher", "AES_256/CBC/NoPadding", s10, s("2.16.840.1.101.3.4.1.42", "OID.2.16.840.1.101.3.4.1.42"), m(4226L));
        d("Cipher", "AES/CBC/PKCS5Padding", s10, m(4229L, 4226L));
        d("Cipher", "AES/ECB/NoPadding", s10, m(4225L));
        d("Cipher", "AES_128/ECB/NoPadding", s10, s("2.16.840.1.101.3.4.1.1", "OID.2.16.840.1.101.3.4.1.1"), m(4225L));
        d("Cipher", "AES_192/ECB/NoPadding", s10, s("2.16.840.1.101.3.4.1.21", "OID.2.16.840.1.101.3.4.1.21"), m(4225L));
        d("Cipher", "AES_256/ECB/NoPadding", s10, s("2.16.840.1.101.3.4.1.41", "OID.2.16.840.1.101.3.4.1.41"), m(4225L));
        d("Cipher", "AES/ECB/PKCS5Padding", s10, s("AES"), m(4225L));
        d("Cipher", "AES/CTR/NoPadding", s10, m(4230L));
        d("Cipher", "AES/GCM/NoPadding", s12, m(4231L));
        d("Cipher", "AES_128/GCM/NoPadding", s12, s("2.16.840.1.101.3.4.1.6", "OID.2.16.840.1.101.3.4.1.6"), m(4231L));
        d("Cipher", "AES_192/GCM/NoPadding", s12, s("2.16.840.1.101.3.4.1.26", "OID.2.16.840.1.101.3.4.1.26"), m(4231L));
        d("Cipher", "AES_256/GCM/NoPadding", s12, s("2.16.840.1.101.3.4.1.46", "OID.2.16.840.1.101.3.4.1.46"), m(4231L));
        d("Cipher", "Blowfish/CBC/NoPadding", s10, m(4241L));
        d("Cipher", "Blowfish/CBC/PKCS5Padding", s10, m(4241L));
        d("Cipher", "RSA/ECB/PKCS1Padding", s11, s("RSA"), m(1L));
        d("Cipher", "RSA/ECB/NoPadding", s11, m(3L));
        d("Signature", "RawDSA", s13, s("NONEwithDSA"), m(17L));
        d("Signature", "DSA", s13, s("SHA1withDSA", "1.3.14.3.2.13", "1.3.14.3.2.27", "1.2.840.10040.4.3", "OID.1.2.840.10040.4.3"), m(18L, 17L));
        d("Signature", "SHA224withDSA", s13, s("2.16.840.1.101.3.4.3.1", "OID.2.16.840.1.101.3.4.3.1"), m(19L));
        d("Signature", "SHA256withDSA", s13, s("2.16.840.1.101.3.4.3.2", "OID.2.16.840.1.101.3.4.3.2"), m(20L));
        d("Signature", "SHA384withDSA", s13, s("2.16.840.1.101.3.4.3.3", "OID.2.16.840.1.101.3.4.3.3"), m(21L));
        d("Signature", "SHA512withDSA", s13, s("2.16.840.1.101.3.4.3.4", "OID.2.16.840.1.101.3.4.3.4"), m(22L));
        d("Signature", "NONEwithECDSA", s13, m(4161L));
        d("Signature", "SHA1withECDSA", s13, s("ECDSA", "1.2.840.10045.4.1", "OID.1.2.840.10045.4.1"), m(4162L, 4161L));
        d("Signature", "SHA224withECDSA", s13, s("1.2.840.10045.4.3.1", "OID.1.2.840.10045.4.3.1"), m(4161L));
        d("Signature", "SHA256withECDSA", s13, s("1.2.840.10045.4.3.2", "OID.1.2.840.10045.4.3.2"), m(4161L));
        d("Signature", "SHA384withECDSA", s13, s("1.2.840.10045.4.3.3", "OID.1.2.840.10045.4.3.3"), m(4161L));
        d("Signature", "SHA512withECDSA", s13, s("1.2.840.10045.4.3.4", "OID.1.2.840.10045.4.3.4"), m(4161L));
        d("Signature", "MD2withRSA", s13, s("1.2.840.113549.1.1.2", "OID.1.2.840.113549.1.1.2"), m(4L, 1L, 3L));
        d("Signature", "MD5withRSA", s13, s("1.2.840.113549.1.1.4", "OID.1.2.840.113549.1.1.4"), m(5L, 1L, 3L));
        d("Signature", "SHA1withRSA", s13, s("1.2.840.113549.1.1.5", "OID.1.2.840.113549.1.1.5", "1.3.14.3.2.29"), m(6L, 1L, 3L));
        d("Signature", "SHA224withRSA", s13, s("1.2.840.113549.1.1.14", "OID.1.2.840.113549.1.1.14"), m(70L, 1L, 3L));
        d("Signature", "SHA256withRSA", s13, s("1.2.840.113549.1.1.11", "OID.1.2.840.113549.1.1.11"), m(64L, 1L, 3L));
        d("Signature", "SHA384withRSA", s13, s("1.2.840.113549.1.1.12", "OID.1.2.840.113549.1.1.12"), m(65L, 1L, 3L));
        d("Signature", "SHA512withRSA", s13, s("1.2.840.113549.1.1.13", "OID.1.2.840.113549.1.1.13"), m(66L, 1L, 3L));
        d("Signature", "RSASSA-PSS", s14, s("1.2.840.113549.1.1.10", "OID.1.2.840.113549.1.1.10"), m(13L));
        d("Signature", "SHA1withRSASSA-PSS", s14, m(14L));
        d("Signature", "SHA224withRSASSA-PSS", s14, m(71L));
        d("Signature", "SHA256withRSASSA-PSS", s14, m(67L));
        d("Signature", "SHA384withRSASSA-PSS", s14, m(68L));
        d("Signature", "SHA512withRSASSA-PSS", s14, m(69L));
        d("KeyGenerator", "SunTlsRsaPremasterSecret", "sun.security.pkcs11.P11TlsRsaPremasterSecretGenerator", s("SunTls12RsaPremasterSecret"), m(880L, 884L));
        d("KeyGenerator", "SunTlsMasterSecret", "sun.security.pkcs11.P11TlsMasterSecretGenerator", m(881L, 885L, 883L, 887L));
        d("KeyGenerator", "SunTls12MasterSecret", "sun.security.pkcs11.P11TlsMasterSecretGenerator", m(992L, 994L));
        d("KeyGenerator", "SunTlsKeyMaterial", "sun.security.pkcs11.P11TlsKeyMaterialGenerator", m(882L, 886L));
        d("KeyGenerator", "SunTls12KeyMaterial", "sun.security.pkcs11.P11TlsKeyMaterialGenerator", m(993L));
        d("KeyGenerator", "SunTlsPrf", "sun.security.pkcs11.P11TlsPrfGenerator", m(888L, 2147484531L));
        d("KeyGenerator", "SunTls12Prf", "sun.security.pkcs11.P11TlsPrfGenerator", m(996L));
    }
    
    private static final class Descriptor
    {
        final String type;
        final String algorithm;
        final String className;
        final String[] aliases;
        final int[] mechanisms;
        
        private Descriptor(final String type, final String algorithm, final String className, final String[] aliases, final int[] mechanisms) {
            this.type = type;
            this.algorithm = algorithm;
            this.className = className;
            this.aliases = aliases;
            this.mechanisms = mechanisms;
        }
        
        private P11Service service(final Token token, final int n) {
            return new P11Service(token, this.type, this.algorithm, this.className, this.aliases, n);
        }
        
        @Override
        public String toString() {
            return this.type + "." + this.algorithm;
        }
    }
    
    private static class TokenPoller implements Runnable
    {
        private final SunPKCS11 provider;
        private volatile boolean enabled;
        
        private TokenPoller(final SunPKCS11 provider) {
            this.provider = provider;
            this.enabled = true;
        }
        
        @Override
        public void run() {
            final int insertionCheckInterval = this.provider.config.getInsertionCheckInterval();
            while (this.enabled) {
                try {
                    Thread.sleep(insertionCheckInterval);
                }
                catch (final InterruptedException ex) {
                    break;
                }
                if (!this.enabled) {
                    break;
                }
                try {
                    this.provider.initToken(null);
                }
                catch (final PKCS11Exception ex2) {}
            }
        }
        
        void disable() {
            this.enabled = false;
        }
    }
    
    private static final class P11Service extends Service
    {
        private final Token token;
        private final long mechanism;
        
        P11Service(final Token token, final String s, final String s2, final String s3, final String[] array, final long n) {
            super(token.provider, s, s2, s3, toList(array), null);
            this.token = token;
            this.mechanism = (n & 0xFFFFFFFFL);
        }
        
        private static List<String> toList(final String[] array) {
            return (array == null) ? null : Arrays.asList(array);
        }
        
        @Override
        public Object newInstance(final Object o) throws NoSuchAlgorithmException {
            if (!this.token.isValid()) {
                throw new NoSuchAlgorithmException("Token has been removed");
            }
            try {
                return this.newInstance0(o);
            }
            catch (final PKCS11Exception ex) {
                throw new NoSuchAlgorithmException(ex);
            }
        }
        
        public Object newInstance0(final Object o) throws PKCS11Exception, NoSuchAlgorithmException {
            final String algorithm = this.getAlgorithm();
            final String type = this.getType();
            if (type == "MessageDigest") {
                return new P11Digest(this.token, algorithm, this.mechanism);
            }
            if (type == "Cipher") {
                if (algorithm.startsWith("RSA")) {
                    return new P11RSACipher(this.token, algorithm, this.mechanism);
                }
                if (algorithm.endsWith("GCM/NoPadding")) {
                    return new P11AEADCipher(this.token, algorithm, this.mechanism);
                }
                return new P11Cipher(this.token, algorithm, this.mechanism);
            }
            else if (type == "Signature") {
                if (algorithm.indexOf("RSASSA-PSS") != -1) {
                    return new P11PSSSignature(this.token, algorithm, this.mechanism);
                }
                return new P11Signature(this.token, algorithm, this.mechanism);
            }
            else {
                if (type == "Mac") {
                    return new P11Mac(this.token, algorithm, this.mechanism);
                }
                if (type == "KeyPairGenerator") {
                    return new P11KeyPairGenerator(this.token, algorithm, this.mechanism);
                }
                if (type == "KeyAgreement") {
                    if (algorithm.equals("ECDH")) {
                        return new P11ECDHKeyAgreement(this.token, algorithm, this.mechanism);
                    }
                    return new P11KeyAgreement(this.token, algorithm, this.mechanism);
                }
                else {
                    if (type == "KeyFactory") {
                        return this.token.getKeyFactory(algorithm);
                    }
                    if (type == "SecretKeyFactory") {
                        return new P11SecretKeyFactory(this.token, algorithm);
                    }
                    if (type == "KeyGenerator") {
                        if (algorithm == "SunTlsRsaPremasterSecret") {
                            return new P11TlsRsaPremasterSecretGenerator(this.token, algorithm, this.mechanism);
                        }
                        if (algorithm == "SunTlsMasterSecret" || algorithm == "SunTls12MasterSecret") {
                            return new P11TlsMasterSecretGenerator(this.token, algorithm, this.mechanism);
                        }
                        if (algorithm == "SunTlsKeyMaterial" || algorithm == "SunTls12KeyMaterial") {
                            return new P11TlsKeyMaterialGenerator(this.token, algorithm, this.mechanism);
                        }
                        if (algorithm == "SunTlsPrf" || algorithm == "SunTls12Prf") {
                            return new P11TlsPrfGenerator(this.token, algorithm, this.mechanism);
                        }
                        return new P11KeyGenerator(this.token, algorithm, this.mechanism);
                    }
                    else {
                        if (type == "SecureRandom") {
                            return this.token.getRandom();
                        }
                        if (type == "KeyStore") {
                            return this.token.getKeyStore();
                        }
                        if (type != "AlgorithmParameters") {
                            throw new NoSuchAlgorithmException("Unknown type: " + type);
                        }
                        if (algorithm == "EC") {
                            return new ECParameters();
                        }
                        if (algorithm == "GCM") {
                            return new GCMParameters();
                        }
                        throw new NoSuchAlgorithmException("Unsupported algorithm: " + algorithm);
                    }
                }
            }
        }
        
        @Override
        public boolean supportsParameter(final Object o) {
            if (o == null || !this.token.isValid()) {
                return false;
            }
            if (!(o instanceof Key)) {
                throw new InvalidParameterException("Parameter must be a Key");
            }
            final String algorithm = this.getAlgorithm();
            final String type = this.getType();
            final Key key = (Key)o;
            final String algorithm2 = key.getAlgorithm();
            if ((type == "Cipher" && algorithm.startsWith("RSA")) || (type == "Signature" && algorithm.indexOf("RSA") != -1)) {
                return algorithm2.equals("RSA") && (this.isLocalKey(key) || key instanceof RSAPrivateKey || key instanceof RSAPublicKey);
            }
            if ((type == "KeyAgreement" && algorithm.equals("ECDH")) || (type == "Signature" && algorithm.endsWith("ECDSA"))) {
                return algorithm2.equals("EC") && (this.isLocalKey(key) || key instanceof ECPrivateKey || key instanceof ECPublicKey);
            }
            if (type == "Signature" && algorithm.endsWith("DSA")) {
                return algorithm2.equals("DSA") && (this.isLocalKey(key) || key instanceof DSAPrivateKey || key instanceof DSAPublicKey);
            }
            if (type == "Cipher" || type == "Mac") {
                return this.isLocalKey(key) || "RAW".equals(key.getFormat());
            }
            if (type == "KeyAgreement") {
                return algorithm2.equals("DH") && (this.isLocalKey(key) || key instanceof DHPrivateKey || key instanceof DHPublicKey);
            }
            throw new AssertionError((Object)("SunPKCS11 error: " + type + ", " + algorithm));
        }
        
        private boolean isLocalKey(final Key key) {
            return key instanceof P11Key && ((P11Key)key).token == this.token;
        }
        
        @Override
        public String toString() {
            return super.toString() + " (" + Functions.getMechanismName(this.mechanism) + ")";
        }
    }
    
    private static class SunPKCS11Rep implements Serializable
    {
        static final long serialVersionUID = -2896606995897745419L;
        private final String providerName;
        private final String configName;
        
        SunPKCS11Rep(final SunPKCS11 sunPKCS11) throws NotSerializableException {
            this.providerName = sunPKCS11.getName();
            this.configName = sunPKCS11.configName;
            if (Security.getProvider(this.providerName) != sunPKCS11) {
                throw new NotSerializableException("Only SunPKCS11 providers installed in java.security.Security can be serialized");
            }
        }
        
        private Object readResolve() throws ObjectStreamException {
            final SunPKCS11 sunPKCS11 = (SunPKCS11)Security.getProvider(this.providerName);
            if (sunPKCS11 == null || !sunPKCS11.configName.equals(this.configName)) {
                throw new NotSerializableException("Could not find " + this.providerName + " in installed providers");
            }
            return sunPKCS11;
        }
    }
}
