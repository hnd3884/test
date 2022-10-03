package sun.security.ssl;

import sun.security.validator.TrustStoreUtil;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivilegedExceptionAction;
import sun.security.action.OpenFileInputStreamAction;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import sun.security.action.GetPropertyAction;
import java.util.Objects;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Set;

final class TrustStoreManager
{
    private static final TrustAnchorManager tam;
    
    private TrustStoreManager() {
    }
    
    public static Set<X509Certificate> getTrustedCerts() throws Exception {
        return TrustStoreManager.tam.getTrustedCerts(TrustStoreDescriptor.createInstance());
    }
    
    public static KeyStore getTrustedKeyStore() throws Exception {
        return TrustStoreManager.tam.getKeyStore(TrustStoreDescriptor.createInstance());
    }
    
    static {
        tam = new TrustAnchorManager();
    }
    
    private static final class TrustStoreDescriptor
    {
        private static final String fileSep;
        private static final String defaultStorePath;
        private static final String defaultStore;
        private static final String jsseDefaultStore;
        private final String storeName;
        private final String storeType;
        private final String storeProvider;
        private final String storePassword;
        private final File storeFile;
        private final long lastModified;
        
        private TrustStoreDescriptor(final String storeName, final String storeType, final String storeProvider, final String storePassword, final File storeFile, final long lastModified) {
            this.storeName = storeName;
            this.storeType = storeType;
            this.storeProvider = storeProvider;
            this.storePassword = storePassword;
            this.storeFile = storeFile;
            this.lastModified = lastModified;
            if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                SSLLogger.fine("trustStore is: " + storeName + "\ntrustStore type is: " + storeType + "\ntrustStore provider is: " + storeProvider + "\nthe last modified time is: " + new Date(lastModified), new Object[0]);
            }
        }
        
        static TrustStoreDescriptor createInstance() {
            return AccessController.doPrivileged((PrivilegedAction<TrustStoreDescriptor>)new PrivilegedAction<TrustStoreDescriptor>() {
                @Override
                public TrustStoreDescriptor run() {
                    final String property = System.getProperty("javax.net.ssl.trustStore", TrustStoreDescriptor.jsseDefaultStore);
                    final String property2 = System.getProperty("javax.net.ssl.trustStoreType", KeyStore.getDefaultType());
                    final String property3 = System.getProperty("javax.net.ssl.trustStoreProvider", "");
                    final String property4 = System.getProperty("javax.net.ssl.trustStorePassword", "");
                    String s = "";
                    File file = null;
                    long lastModified = 0L;
                    if (!"NONE".equals(property)) {
                        for (final String s2 : new String[] { property, TrustStoreDescriptor.defaultStore }) {
                            final File file2 = new File(s2);
                            if (file2.isFile() && file2.canRead()) {
                                s = s2;
                                file = file2;
                                lastModified = file2.lastModified();
                                break;
                            }
                            if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                                SSLLogger.fine("Inaccessible trust store: " + property, new Object[0]);
                            }
                        }
                    }
                    else {
                        s = property;
                    }
                    return new TrustStoreDescriptor(s, property2, property3, property4, file, lastModified);
                }
            });
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof TrustStoreDescriptor) {
                final TrustStoreDescriptor trustStoreDescriptor = (TrustStoreDescriptor)o;
                return this.lastModified == trustStoreDescriptor.lastModified && Objects.equals(this.storeName, trustStoreDescriptor.storeName) && Objects.equals(this.storeType, trustStoreDescriptor.storeType) && Objects.equals(this.storeProvider, trustStoreDescriptor.storeProvider);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int n = 17;
            if (this.storeName != null && !this.storeName.isEmpty()) {
                n = 31 * n + this.storeName.hashCode();
            }
            if (this.storeType != null && !this.storeType.isEmpty()) {
                n = 31 * n + this.storeType.hashCode();
            }
            if (this.storeProvider != null && !this.storeProvider.isEmpty()) {
                n = 31 * n + this.storeProvider.hashCode();
            }
            if (this.storeFile != null) {
                n = 31 * n + this.storeFile.hashCode();
            }
            if (this.lastModified != 0L) {
                n = (int)(31 * n + this.lastModified);
            }
            return n;
        }
        
        static {
            fileSep = File.separator;
            defaultStorePath = GetPropertyAction.privilegedGetProperty("java.home") + TrustStoreDescriptor.fileSep + "lib" + TrustStoreDescriptor.fileSep + "security";
            defaultStore = TrustStoreDescriptor.defaultStorePath + TrustStoreDescriptor.fileSep + "cacerts";
            jsseDefaultStore = TrustStoreDescriptor.defaultStorePath + TrustStoreDescriptor.fileSep + "jssecacerts";
        }
    }
    
    private static final class TrustAnchorManager
    {
        private TrustStoreDescriptor descriptor;
        private WeakReference<KeyStore> ksRef;
        private WeakReference<Set<X509Certificate>> csRef;
        
        private TrustAnchorManager() {
            this.descriptor = null;
            this.ksRef = new WeakReference<KeyStore>(null);
            this.csRef = new WeakReference<Set<X509Certificate>>(null);
        }
        
        synchronized KeyStore getKeyStore(final TrustStoreDescriptor descriptor) throws Exception {
            final TrustStoreDescriptor descriptor2 = this.descriptor;
            final KeyStore keyStore = this.ksRef.get();
            if (keyStore != null && descriptor.equals(descriptor2)) {
                return keyStore;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                SSLLogger.fine("Reload the trust store", new Object[0]);
            }
            final KeyStore loadKeyStore = loadKeyStore(descriptor);
            this.descriptor = descriptor;
            this.ksRef = new WeakReference<KeyStore>(loadKeyStore);
            return loadKeyStore;
        }
        
        synchronized Set<X509Certificate> getTrustedCerts(final TrustStoreDescriptor trustStoreDescriptor) throws Exception {
            KeyStore loadKeyStore = null;
            final TrustStoreDescriptor descriptor = this.descriptor;
            final Set set = this.csRef.get();
            if (set != null) {
                if (trustStoreDescriptor.equals(descriptor)) {
                    return set;
                }
                this.descriptor = trustStoreDescriptor;
            }
            else if (trustStoreDescriptor.equals(descriptor)) {
                loadKeyStore = this.ksRef.get();
            }
            else {
                this.descriptor = trustStoreDescriptor;
            }
            if (loadKeyStore == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                    SSLLogger.fine("Reload the trust store", new Object[0]);
                }
                loadKeyStore = loadKeyStore(trustStoreDescriptor);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                SSLLogger.fine("Reload trust certs", new Object[0]);
            }
            final Set<X509Certificate> loadTrustedCerts = loadTrustedCerts(loadKeyStore);
            if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                SSLLogger.fine("Reloaded " + loadTrustedCerts.size() + " trust certs", new Object[0]);
            }
            this.csRef = new WeakReference<Set<X509Certificate>>(loadTrustedCerts);
            return loadTrustedCerts;
        }
        
        private static KeyStore loadKeyStore(final TrustStoreDescriptor trustStoreDescriptor) throws Exception {
            if (!"NONE".equals(trustStoreDescriptor.storeName) && trustStoreDescriptor.storeFile == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                    SSLLogger.fine("No available key store", new Object[0]);
                }
                return null;
            }
            KeyStore keyStore;
            if (trustStoreDescriptor.storeProvider.isEmpty()) {
                keyStore = KeyStore.getInstance(trustStoreDescriptor.storeType);
            }
            else {
                keyStore = KeyStore.getInstance(trustStoreDescriptor.storeType, trustStoreDescriptor.storeProvider);
            }
            char[] charArray = null;
            if (!trustStoreDescriptor.storePassword.isEmpty()) {
                charArray = trustStoreDescriptor.storePassword.toCharArray();
            }
            if (!"NONE".equals(trustStoreDescriptor.storeName)) {
                try {
                    try (final FileInputStream fileInputStream = AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new OpenFileInputStreamAction(trustStoreDescriptor.storeFile))) {
                        keyStore.load(fileInputStream, charArray);
                    }
                    return keyStore;
                }
                catch (final FileNotFoundException ex) {
                    if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                        SSLLogger.fine("Not available key store: " + trustStoreDescriptor.storeName, new Object[0]);
                    }
                    return null;
                }
            }
            keyStore.load(null, charArray);
            return keyStore;
        }
        
        private static Set<X509Certificate> loadTrustedCerts(final KeyStore keyStore) {
            if (keyStore == null) {
                return Collections.emptySet();
            }
            return TrustStoreUtil.getTrustedCerts(keyStore);
        }
    }
}
