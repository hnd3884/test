package sun.security.pkcs11;

import java.util.Arrays;
import java.security.KeyStore;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.Provider;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public final class Secmod
{
    private static final boolean DEBUG = false;
    private static final Secmod INSTANCE;
    private static final String NSS_LIB_NAME = "nss3";
    private static final String SOFTTOKEN_LIB_NAME = "softokn3";
    private static final String TRUST_LIB_NAME = "nssckbi";
    private long nssHandle;
    private boolean supported;
    private List<Module> modules;
    private String configDir;
    private String nssLibDir;
    static final String TEMPLATE_EXTERNAL = "library = %s\nname = \"%s\"\nslotListIndex = %d\n";
    static final String TEMPLATE_TRUSTANCHOR = "library = %s\nname = \"NSS Trust Anchors\"\nslotListIndex = 0\nenabledMechanisms = { KeyStore }\nnssUseSecmodTrust = true\n";
    static final String TEMPLATE_CRYPTO = "library = %s\nname = \"NSS SoftToken Crypto\"\nslotListIndex = 0\ndisabledMechanisms = { KeyStore }\n";
    static final String TEMPLATE_KEYSTORE = "library = %s\nname = \"NSS SoftToken KeyStore\"\nslotListIndex = 1\nnssUseSecmodTrust = true\n";
    static final String TEMPLATE_FIPS = "library = %s\nname = \"NSS FIPS SoftToken\"\nslotListIndex = 0\nnssUseSecmodTrust = true\n";
    
    private Secmod() {
    }
    
    public static Secmod getInstance() {
        return Secmod.INSTANCE;
    }
    
    private boolean isLoaded() {
        if (this.nssHandle == 0L) {
            this.nssHandle = nssGetLibraryHandle(System.mapLibraryName("nss3"));
            if (this.nssHandle != 0L) {
                this.fetchVersions();
            }
        }
        return this.nssHandle != 0L;
    }
    
    private void fetchVersions() {
        this.supported = nssVersionCheck(this.nssHandle, "3.7");
    }
    
    public synchronized boolean isInitialized() throws IOException {
        if (!this.isLoaded()) {
            return false;
        }
        if (!this.supported) {
            throw new IOException("An incompatible version of NSS is already loaded, 3.7 or later required");
        }
        return true;
    }
    
    String getConfigDir() {
        return this.configDir;
    }
    
    String getLibDir() {
        return this.nssLibDir;
    }
    
    public void initialize(final String s, final String s2) throws IOException {
        this.initialize(DbMode.READ_WRITE, s, s2, false);
    }
    
    public void initialize(final DbMode dbMode, final String s, final String s2) throws IOException {
        this.initialize(dbMode, s, s2, false);
    }
    
    public synchronized void initialize(final DbMode dbMode, final String configDir, final String nssLibDir, final boolean b) throws IOException {
        if (this.isInitialized()) {
            throw new IOException("NSS is already initialized");
        }
        if (dbMode == null) {
            throw new NullPointerException();
        }
        if (dbMode != DbMode.NO_DB && configDir == null) {
            throw new NullPointerException();
        }
        final String mapLibraryName = System.mapLibraryName("nss3");
        String path;
        if (nssLibDir == null) {
            path = mapLibraryName;
        }
        else {
            final File file = new File(nssLibDir);
            if (!file.isDirectory()) {
                throw new IOException("nssLibDir must be a directory:" + nssLibDir);
            }
            final File file2 = new File(file, mapLibraryName);
            if (!file2.isFile()) {
                throw new FileNotFoundException(file2.getPath());
            }
            path = file2.getPath();
        }
        if (configDir != null) {
            final String s = "sql:/";
            String substring;
            if (!configDir.startsWith(s)) {
                substring = configDir;
            }
            else {
                substring = new StringBuilder(configDir).substring(s.length());
            }
            final File file3 = new File(substring);
            if (!file3.isDirectory()) {
                throw new IOException("configDir must be a directory: " + substring);
            }
            if (!configDir.startsWith(s)) {
                final File file4 = new File(file3, "secmod.db");
                if (!file4.isFile()) {
                    throw new FileNotFoundException(file4.getPath());
                }
            }
        }
        this.nssHandle = nssLoadLibrary(path);
        this.fetchVersions();
        if (!this.supported) {
            throw new IOException("The specified version of NSS is incompatible, 3.7 or later required");
        }
        if (!nssInitialize(dbMode.functionName, this.nssHandle, configDir, b)) {
            throw new IOException("NSS initialization failed");
        }
        this.configDir = configDir;
        this.nssLibDir = nssLibDir;
    }
    
    public synchronized List<Module> getModules() {
        try {
            if (!this.isInitialized()) {
                throw new IllegalStateException("NSS not initialized");
            }
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        if (this.modules == null) {
            this.modules = Collections.unmodifiableList((List<? extends Module>)nssGetModuleList(this.nssHandle, this.nssLibDir));
        }
        return this.modules;
    }
    
    private static byte[] getDigest(final X509Certificate x509Certificate, final String s) {
        try {
            return MessageDigest.getInstance(s).digest(x509Certificate.getEncoded());
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException(ex);
        }
    }
    
    boolean isTrusted(final X509Certificate x509Certificate, final TrustType trustType) {
        final Bytes bytes = new Bytes(getDigest(x509Certificate, "SHA-1"));
        TrustAttributes trustAttributes = this.getModuleTrust(ModuleType.KEYSTORE, bytes);
        if (trustAttributes == null) {
            trustAttributes = this.getModuleTrust(ModuleType.FIPS, bytes);
            if (trustAttributes == null) {
                trustAttributes = this.getModuleTrust(ModuleType.TRUSTANCHOR, bytes);
            }
        }
        return trustAttributes != null && trustAttributes.isTrusted(trustType);
    }
    
    private TrustAttributes getModuleTrust(final ModuleType moduleType, final Bytes bytes) {
        final Module module = this.getModule(moduleType);
        return (module == null) ? null : module.getTrust(bytes);
    }
    
    public Module getModule(final ModuleType moduleType) {
        for (final Module module : this.getModules()) {
            if (module.getType() == moduleType) {
                return module;
            }
        }
        return null;
    }
    
    private static Map<Bytes, TrustAttributes> getTrust(final SunPKCS11 sunPKCS11) throws PKCS11Exception {
        final HashMap hashMap = new HashMap();
        final Token token = sunPKCS11.getToken();
        Session opSession = null;
        try {
            opSession = token.getOpSession();
            final int n = 8192;
            token.p11.C_FindObjectsInit(opSession.id(), new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 3461563219L) });
            final long[] c_FindObjects = token.p11.C_FindObjects(opSession.id(), n);
            token.p11.C_FindObjectsFinal(opSession.id());
            for (final long n2 : c_FindObjects) {
                try {
                    final TrustAttributes trustAttributes = new TrustAttributes(token, opSession, n2);
                    hashMap.put(trustAttributes.getHash(), trustAttributes);
                }
                catch (final PKCS11Exception ex) {}
            }
        }
        finally {
            token.releaseSession(opSession);
        }
        return hashMap;
    }
    
    private static native long nssGetLibraryHandle(final String p0);
    
    private static native long nssLoadLibrary(final String p0) throws IOException;
    
    private static native boolean nssVersionCheck(final long p0, final String p1);
    
    private static native boolean nssInitialize(final String p0, final long p1, final String p2, final boolean p3);
    
    private static native Object nssGetModuleList(final long p0, final String p1);
    
    static {
        PKCS11.loadNative();
        INSTANCE = new Secmod();
    }
    
    public enum ModuleType
    {
        CRYPTO, 
        KEYSTORE, 
        FIPS, 
        TRUSTANCHOR, 
        EXTERNAL;
    }
    
    public static final class Module
    {
        final String libraryName;
        final String commonName;
        final int slot;
        final ModuleType type;
        private String config;
        private SunPKCS11 provider;
        private Map<Bytes, TrustAttributes> trust;
        
        Module(final String s, String mapLibraryName, final String commonName, final boolean b, final int slot) {
            ModuleType type;
            if (mapLibraryName == null || mapLibraryName.length() == 0) {
                mapLibraryName = System.mapLibraryName("softokn3");
                if (!b) {
                    type = ((slot == 0) ? ModuleType.CRYPTO : ModuleType.KEYSTORE);
                }
                else {
                    type = ModuleType.FIPS;
                    if (slot != 0) {
                        throw new RuntimeException("Slot index should be 0 for FIPS slot");
                    }
                }
            }
            else if (mapLibraryName.endsWith(System.mapLibraryName("nssckbi")) || commonName.equals("Builtin Roots Module")) {
                type = ModuleType.TRUSTANCHOR;
            }
            else {
                type = ModuleType.EXTERNAL;
            }
            File file = new File(s, mapLibraryName);
            if (!file.isFile()) {
                final File file2 = new File(s, "nss/" + mapLibraryName);
                if (file2.isFile()) {
                    file = file2;
                }
            }
            this.libraryName = file.getPath();
            this.commonName = commonName;
            this.slot = slot;
            this.type = type;
            this.initConfiguration();
        }
        
        private void initConfiguration() {
            switch (this.type) {
                case EXTERNAL: {
                    this.config = String.format("library = %s\nname = \"%s\"\nslotListIndex = %d\n", this.libraryName, this.commonName + " " + this.slot, this.slot);
                    break;
                }
                case CRYPTO: {
                    this.config = String.format("library = %s\nname = \"NSS SoftToken Crypto\"\nslotListIndex = 0\ndisabledMechanisms = { KeyStore }\n", this.libraryName);
                    break;
                }
                case KEYSTORE: {
                    this.config = String.format("library = %s\nname = \"NSS SoftToken KeyStore\"\nslotListIndex = 1\nnssUseSecmodTrust = true\n", this.libraryName);
                    break;
                }
                case FIPS: {
                    this.config = String.format("library = %s\nname = \"NSS FIPS SoftToken\"\nslotListIndex = 0\nnssUseSecmodTrust = true\n", this.libraryName);
                    break;
                }
                case TRUSTANCHOR: {
                    this.config = String.format("library = %s\nname = \"NSS Trust Anchors\"\nslotListIndex = 0\nenabledMechanisms = { KeyStore }\nnssUseSecmodTrust = true\n", this.libraryName);
                    break;
                }
                default: {
                    throw new RuntimeException("Unknown module type: " + this.type);
                }
            }
        }
        
        @Deprecated
        public synchronized String getConfiguration() {
            return this.config;
        }
        
        @Deprecated
        public synchronized void setConfiguration(final String config) {
            if (this.provider != null) {
                throw new IllegalStateException("Provider instance already created");
            }
            this.config = config;
        }
        
        public String getLibraryName() {
            return this.libraryName;
        }
        
        public ModuleType getType() {
            return this.type;
        }
        
        @Deprecated
        public synchronized Provider getProvider() {
            if (this.provider == null) {
                this.provider = this.newProvider();
            }
            return this.provider;
        }
        
        synchronized boolean hasInitializedProvider() {
            return this.provider != null;
        }
        
        void setProvider(final SunPKCS11 provider) {
            if (this.provider != null) {
                throw new ProviderException("Secmod provider already initialized");
            }
            this.provider = provider;
        }
        
        private SunPKCS11 newProvider() {
            try {
                return new SunPKCS11(new ByteArrayInputStream(this.config.getBytes("UTF8")));
            }
            catch (final Exception ex) {
                throw new ProviderException(ex);
            }
        }
        
        synchronized void setTrust(final Token token, final X509Certificate x509Certificate) {
            final Bytes bytes = new Bytes(getDigest(x509Certificate, "SHA-1"));
            final TrustAttributes trust = this.getTrust(bytes);
            if (trust == null) {
                this.trust.put(bytes, new TrustAttributes(token, x509Certificate, bytes, 3461563218L));
            }
            else if (!trust.isTrusted(TrustType.ALL)) {
                throw new ProviderException("Cannot change existing trust attributes");
            }
        }
        
        TrustAttributes getTrust(final Bytes bytes) {
            if (this.trust == null) {
                synchronized (this) {
                    SunPKCS11 sunPKCS11 = this.provider;
                    if (sunPKCS11 == null) {
                        sunPKCS11 = this.newProvider();
                    }
                    try {
                        this.trust = getTrust(sunPKCS11);
                    }
                    catch (final PKCS11Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            return this.trust.get(bytes);
        }
        
        @Override
        public String toString() {
            return this.commonName + " (" + this.type + ", " + this.libraryName + ", slot " + this.slot + ")";
        }
    }
    
    public enum TrustType
    {
        ALL, 
        CLIENT_AUTH, 
        SERVER_AUTH, 
        CODE_SIGNING, 
        EMAIL_PROTECTION;
    }
    
    public enum DbMode
    {
        READ_WRITE("NSS_InitReadWrite"), 
        READ_ONLY("NSS_Init"), 
        NO_DB("NSS_NoDB_Init");
        
        final String functionName;
        
        private DbMode(final String functionName) {
            this.functionName = functionName;
        }
    }
    
    public static final class KeyStoreLoadParameter implements KeyStore.LoadStoreParameter
    {
        final TrustType trustType;
        final KeyStore.ProtectionParameter protection;
        
        public KeyStoreLoadParameter(final TrustType trustType, final char[] array) {
            this(trustType, new KeyStore.PasswordProtection(array));
        }
        
        public KeyStoreLoadParameter(final TrustType trustType, final KeyStore.ProtectionParameter protection) {
            if (trustType == null) {
                throw new NullPointerException("trustType must not be null");
            }
            this.trustType = trustType;
            this.protection = protection;
        }
        
        @Override
        public KeyStore.ProtectionParameter getProtectionParameter() {
            return this.protection;
        }
        
        public TrustType getTrustType() {
            return this.trustType;
        }
    }
    
    static class TrustAttributes
    {
        final long handle;
        final long clientAuth;
        final long serverAuth;
        final long codeSigning;
        final long emailProtection;
        final byte[] shaHash;
        
        TrustAttributes(final Token token, final X509Certificate x509Certificate, final Bytes bytes, final long n) {
            Session opSession = null;
            try {
                opSession = token.getOpSession();
                this.handle = token.p11.C_CreateObject(opSession.id(), new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(1L, true), new CK_ATTRIBUTE(0L, 3461563219L), new CK_ATTRIBUTE(3461571416L, n), new CK_ATTRIBUTE(3461571418L, n), new CK_ATTRIBUTE(3461571419L, n), new CK_ATTRIBUTE(3461571417L, n), new CK_ATTRIBUTE(3461571508L, bytes.b), new CK_ATTRIBUTE(3461571509L, getDigest(x509Certificate, "MD5")), new CK_ATTRIBUTE(129L, x509Certificate.getIssuerX500Principal().getEncoded()), new CK_ATTRIBUTE(130L, x509Certificate.getSerialNumber().toByteArray()) });
                this.shaHash = bytes.b;
                this.clientAuth = n;
                this.serverAuth = n;
                this.codeSigning = n;
                this.emailProtection = n;
            }
            catch (final PKCS11Exception ex) {
                throw new ProviderException("Could not create trust object", ex);
            }
            finally {
                token.releaseSession(opSession);
            }
        }
        
        TrustAttributes(final Token token, final Session session, final long handle) throws PKCS11Exception {
            this.handle = handle;
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(3461571416L), new CK_ATTRIBUTE(3461571418L), new CK_ATTRIBUTE(3461571419L), new CK_ATTRIBUTE(3461571508L) };
            token.p11.C_GetAttributeValue(session.id(), handle, array);
            this.serverAuth = array[0].getLong();
            this.codeSigning = array[1].getLong();
            this.emailProtection = array[2].getLong();
            this.shaHash = array[3].getByteArray();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(3461571417L) };
            long clientAuth;
            try {
                token.p11.C_GetAttributeValue(session.id(), handle, array2);
                clientAuth = array2[0].getLong();
            }
            catch (final PKCS11Exception ex) {
                clientAuth = this.serverAuth;
            }
            this.clientAuth = clientAuth;
        }
        
        Bytes getHash() {
            return new Bytes(this.shaHash);
        }
        
        boolean isTrusted(final TrustType trustType) {
            switch (trustType) {
                case CLIENT_AUTH: {
                    return this.isTrusted(this.clientAuth);
                }
                case SERVER_AUTH: {
                    return this.isTrusted(this.serverAuth);
                }
                case CODE_SIGNING: {
                    return this.isTrusted(this.codeSigning);
                }
                case EMAIL_PROTECTION: {
                    return this.isTrusted(this.emailProtection);
                }
                case ALL: {
                    return this.isTrusted(TrustType.CLIENT_AUTH) && this.isTrusted(TrustType.SERVER_AUTH) && this.isTrusted(TrustType.CODE_SIGNING) && this.isTrusted(TrustType.EMAIL_PROTECTION);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean isTrusted(final long n) {
            return n == 3461563218L;
        }
    }
    
    private static class Bytes
    {
        final byte[] b;
        
        Bytes(final byte[] b) {
            this.b = b;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.b);
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof Bytes && Arrays.equals(this.b, ((Bytes)o).b));
        }
    }
}
