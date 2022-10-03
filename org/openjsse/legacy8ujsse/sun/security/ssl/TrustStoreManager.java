package org.openjsse.legacy8ujsse.sun.security.ssl;

import org.openjsse.legacy8ujsse.sun.security.validator.TrustStoreUtil;
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
    private static final Debug debug;
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
        debug = Debug.getInstance("ssl");
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
            if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                System.out.println("trustStore is: " + storeName + "\ntrustStore type is: " + storeType + "\ntrustStore provider is: " + storeProvider + "\nthe last modified time is: " + new Date(lastModified));
            }
        }
        
        static TrustStoreDescriptor createInstance() {
            return AccessController.doPrivileged((PrivilegedAction<TrustStoreDescriptor>)new PrivilegedAction<TrustStoreDescriptor>() {
                @Override
                public TrustStoreDescriptor run() {
                    final String storePropName = System.getProperty("javax.net.ssl.trustStore", TrustStoreDescriptor.jsseDefaultStore);
                    final String storePropType = System.getProperty("javax.net.ssl.trustStoreType", KeyStore.getDefaultType());
                    final String storePropProvider = System.getProperty("javax.net.ssl.trustStoreProvider", "");
                    final String storePropPassword = System.getProperty("javax.net.ssl.trustStorePassword", "");
                    String temporaryName = "";
                    File temporaryFile = null;
                    long temporaryTime = 0L;
                    if (!"NONE".equals(storePropName)) {
                        final String[] array;
                        final String[] fileNames = array = new String[] { storePropName, TrustStoreDescriptor.defaultStore };
                        for (final String fileName : array) {
                            final File f = new File(fileName);
                            if (f.isFile() && f.canRead()) {
                                temporaryName = fileName;
                                temporaryFile = f;
                                temporaryTime = f.lastModified();
                                break;
                            }
                            if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                                System.out.println("Inaccessible trust store: " + storePropName);
                            }
                        }
                    }
                    else {
                        temporaryName = storePropName;
                    }
                    return new TrustStoreDescriptor(temporaryName, storePropType, storePropProvider, storePropPassword, temporaryFile, temporaryTime);
                }
            });
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof TrustStoreDescriptor) {
                final TrustStoreDescriptor that = (TrustStoreDescriptor)obj;
                return this.lastModified == that.lastModified && Objects.equals(this.storeName, that.storeName) && Objects.equals(this.storeType, that.storeType) && Objects.equals(this.storeProvider, that.storeProvider);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            if (this.storeName != null && !this.storeName.isEmpty()) {
                result = 31 * result + this.storeName.hashCode();
            }
            if (this.storeType != null && !this.storeType.isEmpty()) {
                result = 31 * result + this.storeType.hashCode();
            }
            if (this.storeProvider != null && !this.storeProvider.isEmpty()) {
                result = 31 * result + this.storeProvider.hashCode();
            }
            if (this.storeFile != null) {
                result = 31 * result + this.storeFile.hashCode();
            }
            if (this.lastModified != 0L) {
                result = (int)(31 * result + this.lastModified);
            }
            return result;
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
            final TrustStoreDescriptor temporaryDesc = this.descriptor;
            KeyStore ks = this.ksRef.get();
            if (ks != null && descriptor.equals(temporaryDesc)) {
                return ks;
            }
            if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                System.out.println("Reload the trust store");
            }
            ks = loadKeyStore(descriptor);
            this.descriptor = descriptor;
            this.ksRef = new WeakReference<KeyStore>(ks);
            return ks;
        }
        
        synchronized Set<X509Certificate> getTrustedCerts(final TrustStoreDescriptor descriptor) throws Exception {
            KeyStore ks = null;
            final TrustStoreDescriptor temporaryDesc = this.descriptor;
            Set<X509Certificate> certs = this.csRef.get();
            if (certs != null) {
                if (descriptor.equals(temporaryDesc)) {
                    return certs;
                }
                this.descriptor = descriptor;
            }
            else if (descriptor.equals(temporaryDesc)) {
                ks = this.ksRef.get();
            }
            else {
                this.descriptor = descriptor;
            }
            if (ks == null) {
                if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                    System.out.println("Reload the trust store");
                }
                ks = loadKeyStore(descriptor);
            }
            if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                System.out.println("Reload trust certs");
            }
            certs = loadTrustedCerts(ks);
            if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                System.out.println("Reloaded " + certs.size() + " trust certs");
            }
            this.csRef = new WeakReference<Set<X509Certificate>>(certs);
            return certs;
        }
        
        private static KeyStore loadKeyStore(final TrustStoreDescriptor descriptor) throws Exception {
            if (!"NONE".equals(descriptor.storeName) && descriptor.storeFile == null) {
                if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                    System.out.println("No available key store");
                }
                return null;
            }
            KeyStore ks;
            if (descriptor.storeProvider.isEmpty()) {
                ks = KeyStore.getInstance(descriptor.storeType);
            }
            else {
                ks = KeyStore.getInstance(descriptor.storeType, descriptor.storeProvider);
            }
            char[] password = null;
            if (!descriptor.storePassword.isEmpty()) {
                password = descriptor.storePassword.toCharArray();
            }
            if (!"NONE".equals(descriptor.storeName)) {
                try {
                    try (final FileInputStream fis = AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new OpenFileInputStreamAction(descriptor.storeFile))) {
                        ks.load(fis, password);
                    }
                    return ks;
                }
                catch (final FileNotFoundException fnfe) {
                    if (TrustStoreManager.debug != null && Debug.isOn("trustmanager")) {
                        System.out.println("Not available key store: " + descriptor.storeName);
                    }
                    return null;
                }
            }
            ks.load(null, password);
            return ks;
        }
        
        private static Set<X509Certificate> loadTrustedCerts(final KeyStore ks) {
            if (ks == null) {
                return Collections.emptySet();
            }
            return TrustStoreUtil.getTrustedCerts(ks);
        }
    }
}
