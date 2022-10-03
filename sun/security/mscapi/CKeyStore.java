package sun.security.mscapi;

import java.util.UUID;
import java.util.Objects;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.security.Permission;
import java.security.SecurityPermission;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Enumeration;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.KeyStoreException;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.Key;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import sun.security.util.Debug;
import java.security.cert.CertificateFactory;
import java.security.KeyStoreSpi;

abstract class CKeyStore extends KeyStoreSpi
{
    private CertificateFactory certificateFactory;
    private static final String KEYSTORE_COMPATIBILITY_MODE_PROP = "sun.security.mscapi.keyStoreCompatibilityMode";
    private final boolean keyStoreCompatibilityMode;
    private static final Debug debug;
    private Map<String, KeyEntry> entries;
    private final String storeName;
    
    CKeyStore(final String storeName) {
        this.certificateFactory = null;
        this.entries = new HashMap<String, KeyEntry>();
        if ("false".equalsIgnoreCase(AccessController.doPrivileged(() -> System.getProperty("sun.security.mscapi.keyStoreCompatibilityMode")))) {
            this.keyStoreCompatibilityMode = false;
        }
        else {
            this.keyStoreCompatibilityMode = true;
        }
        this.storeName = storeName;
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (s == null) {
            return null;
        }
        if (array != null && !this.keyStoreCompatibilityMode) {
            throw new UnrecoverableKeyException("Password must be null");
        }
        if (!this.engineIsKeyEntry(s)) {
            return null;
        }
        final KeyEntry keyEntry = this.entries.get(s);
        return (keyEntry == null) ? null : keyEntry.getPrivateKey();
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        if (s == null) {
            return null;
        }
        final KeyEntry keyEntry = this.entries.get(s);
        final X509Certificate[] array = (X509Certificate[])((keyEntry == null) ? null : keyEntry.getCertificateChain());
        return (Certificate[])((array == null) ? null : ((Certificate[])array.clone()));
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        if (s == null) {
            return null;
        }
        final KeyEntry keyEntry = this.entries.get(s);
        final X509Certificate[] array = (X509Certificate[])((keyEntry == null) ? null : keyEntry.getCertificateChain());
        return (array == null || array.length == 0) ? null : array[0];
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        if (s == null) {
            return null;
        }
        return new Date();
    }
    
    @Override
    public void engineSetKeyEntry(final String alias, final Key rsaPrivateKey, final char[] array, final Certificate[] array2) throws KeyStoreException {
        if (alias == null) {
            throw new KeyStoreException("alias must not be null");
        }
        if (array != null && !this.keyStoreCompatibilityMode) {
            throw new KeyStoreException("Password must be null");
        }
        if (rsaPrivateKey instanceof RSAPrivateCrtKey) {
            KeyEntry keyEntry = this.entries.get(alias);
            X509Certificate[] certificateChain;
            if (array2 != null) {
                if (array2 instanceof X509Certificate[]) {
                    certificateChain = (X509Certificate[])array2;
                }
                else {
                    certificateChain = new X509Certificate[array2.length];
                    System.arraycopy(array2, 0, certificateChain, 0, array2.length);
                }
            }
            else {
                certificateChain = null;
            }
            if (keyEntry == null) {
                keyEntry = new KeyEntry(alias, null, certificateChain);
                this.storeWithUniqueAlias(alias, keyEntry);
            }
            keyEntry.setAlias(alias);
            try {
                keyEntry.setRSAPrivateKey(rsaPrivateKey);
                keyEntry.setCertificateChain(certificateChain);
            }
            catch (final CertificateException ex) {
                throw new KeyStoreException(ex);
            }
            catch (final InvalidKeyException ex2) {
                throw new KeyStoreException(ex2);
            }
            return;
        }
        throw new UnsupportedOperationException("Cannot assign the key to the given alias.");
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        throw new UnsupportedOperationException("Cannot assign the encoded key to the given alias.");
    }
    
    @Override
    public void engineSetCertificateEntry(final String alias, final Certificate certificate) throws KeyStoreException {
        if (alias == null) {
            throw new KeyStoreException("alias must not be null");
        }
        if (certificate instanceof X509Certificate) {
            final X509Certificate[] certificateChain = { (X509Certificate)certificate };
            KeyEntry keyEntry = this.entries.get(alias);
            if (keyEntry == null) {
                keyEntry = new KeyEntry(alias, null, certificateChain);
                this.storeWithUniqueAlias(alias, keyEntry);
            }
            if (keyEntry.getPrivateKey() == null) {
                keyEntry.setAlias(alias);
                try {
                    keyEntry.setCertificateChain(certificateChain);
                }
                catch (final CertificateException ex) {
                    throw new KeyStoreException(ex);
                }
            }
            return;
        }
        throw new UnsupportedOperationException("Cannot assign the certificate to the given alias.");
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        if (s == null) {
            throw new KeyStoreException("alias must not be null");
        }
        final KeyEntry keyEntry = this.entries.remove(s);
        if (keyEntry != null) {
            final X509Certificate[] certificateChain = keyEntry.getCertificateChain();
            if (certificateChain != null && certificateChain.length > 0) {
                try {
                    final byte[] encoded = certificateChain[0].getEncoded();
                    this.removeCertificate(this.getName(), keyEntry.getAlias(), encoded, encoded.length);
                }
                catch (final CertificateException ex) {
                    throw new KeyStoreException("Cannot remove entry: ", ex);
                }
            }
            final CKey privateKey = keyEntry.getPrivateKey();
            if (privateKey != null) {
                this.destroyKeyContainer(CKey.getContainerName(privateKey.getHCryptProvider()));
            }
        }
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return new Enumeration<String>() {
            final /* synthetic */ Iterator val$iter = CKeyStore.this.entries.keySet().iterator();
            
            @Override
            public boolean hasMoreElements() {
                return this.val$iter.hasNext();
            }
            
            @Override
            public String nextElement() {
                return this.val$iter.next();
            }
        };
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.entries.containsKey(s);
    }
    
    @Override
    public int engineSize() {
        return this.entries.size();
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        if (s == null) {
            return false;
        }
        final KeyEntry keyEntry = this.entries.get(s);
        return keyEntry != null && keyEntry.getPrivateKey() != null;
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        if (s == null) {
            return false;
        }
        final KeyEntry keyEntry = this.entries.get(s);
        return keyEntry != null && keyEntry.getPrivateKey() == null;
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        final Iterator<Map.Entry<String, KeyEntry>> iterator = this.entries.entrySet().iterator();
        while (iterator.hasNext()) {
            final KeyEntry keyEntry = ((Map.Entry<K, KeyEntry>)iterator.next()).getValue();
            if (keyEntry.certChain != null && keyEntry.certChain.length > 0 && keyEntry.certChain[0].equals(certificate)) {
                return keyEntry.getAlias();
            }
        }
        return null;
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (outputStream != null && !this.keyStoreCompatibilityMode) {
            throw new IOException("Keystore output stream must be null");
        }
        if (array != null && !this.keyStoreCompatibilityMode) {
            throw new IOException("Keystore password must be null");
        }
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (inputStream != null && !this.keyStoreCompatibilityMode) {
            throw new IOException("Keystore input stream must be null");
        }
        if (array != null && !this.keyStoreCompatibilityMode) {
            throw new IOException("Keystore password must be null");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SecurityPermission("authProvider.SunMSCAPI"));
        }
        this.entries.clear();
        try {
            this.loadKeysOrCertificateChains(this.getName());
        }
        catch (final KeyStoreException ex) {
            throw new IOException(ex);
        }
        if (CKeyStore.debug != null) {
            CKeyStore.debug.println("MSCAPI keystore load: entry count: " + this.entries.size());
        }
    }
    
    private void storeWithUniqueAlias(final String s, final KeyEntry keyEntry) {
        String string = s;
        for (int n = 1; this.entries.putIfAbsent(string, keyEntry) != null; string = s + " (" + n++ + ")") {}
    }
    
    private void generateCertificateChain(final String s, final Collection<? extends Certificate> collection) {
        try {
            final X509Certificate[] array = new X509Certificate[collection.size()];
            int n = 0;
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                array[n] = (X509Certificate)iterator.next();
                ++n;
            }
            this.storeWithUniqueAlias(s, new KeyEntry(s, null, array));
        }
        catch (final Throwable t) {}
    }
    
    private void generateKeyAndCertificateChain(final boolean b, final String s, final long n, final long n2, final int n3, final Collection<? extends Certificate> collection) {
        try {
            final X509Certificate[] array = new X509Certificate[collection.size()];
            int n4 = 0;
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                array[n4] = (X509Certificate)iterator.next();
                ++n4;
            }
            this.storeWithUniqueAlias(s, new KeyEntry(s, CPrivateKey.of(b ? "RSA" : "EC", n, n2, n3), array));
        }
        catch (final Throwable t) {}
    }
    
    private void generateCertificate(final byte[] array, final Collection<Certificate> collection) {
        try {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
            if (this.certificateFactory == null) {
                this.certificateFactory = CertificateFactory.getInstance("X.509", "SUN");
            }
            collection.addAll(this.certificateFactory.generateCertificates(byteArrayInputStream));
        }
        catch (final CertificateException ex) {}
        catch (final Throwable t) {}
    }
    
    private String getName() {
        return this.storeName;
    }
    
    private native void loadKeysOrCertificateChains(final String p0) throws KeyStoreException;
    
    private native void storeCertificate(final String p0, final String p1, final byte[] p2, final int p3, final long p4, final long p5) throws CertificateException, KeyStoreException;
    
    private native void removeCertificate(final String p0, final String p1, final byte[] p2, final int p3) throws CertificateException, KeyStoreException;
    
    private native void destroyKeyContainer(final String p0) throws KeyStoreException;
    
    private native byte[] generateRSAPrivateKeyBlob(final int p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5, final byte[] p6, final byte[] p7, final byte[] p8) throws InvalidKeyException;
    
    private native CPrivateKey storePrivateKey(final String p0, final byte[] p1, final String p2, final int p3) throws KeyStoreException;
    
    static {
        debug = Debug.getInstance("keystore");
    }
    
    public static final class MY extends CKeyStore
    {
        public MY() {
            super("MY");
        }
    }
    
    public static final class ROOT extends CKeyStore
    {
        public ROOT() {
            super("ROOT");
        }
    }
    
    class KeyEntry
    {
        private CKey privateKey;
        private X509Certificate[] certChain;
        private String alias;
        
        KeyEntry(final CKeyStore cKeyStore, final CKey cKey, final X509Certificate[] array) {
            this(cKeyStore, null, cKey, array);
        }
        
        KeyEntry(final String alias, final CKey privateKey, final X509Certificate[] certChain) {
            this.privateKey = privateKey;
            this.certChain = certChain;
            if (alias == null) {
                this.alias = Integer.toString(certChain[0].hashCode());
            }
            else {
                this.alias = alias;
            }
        }
        
        String getAlias() {
            return this.alias;
        }
        
        void setAlias(final String alias) {
            this.alias = alias;
        }
        
        CKey getPrivateKey() {
            return this.privateKey;
        }
        
        void setRSAPrivateKey(final Key key) throws InvalidKeyException, KeyStoreException {
            final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)key;
            final byte[] byteArray = rsaPrivateCrtKey.getModulus().toByteArray();
            final int n = (byteArray[0] == 0) ? ((byteArray.length - 1) * 8) : (byteArray.length * 8);
            this.privateKey = CKeyStore.this.storePrivateKey("RSA", Objects.requireNonNull(CKeyStore.this.generateRSAPrivateKeyBlob(n, byteArray, rsaPrivateCrtKey.getPublicExponent().toByteArray(), rsaPrivateCrtKey.getPrivateExponent().toByteArray(), rsaPrivateCrtKey.getPrimeP().toByteArray(), rsaPrivateCrtKey.getPrimeQ().toByteArray(), rsaPrivateCrtKey.getPrimeExponentP().toByteArray(), rsaPrivateCrtKey.getPrimeExponentQ().toByteArray(), rsaPrivateCrtKey.getCrtCoefficient().toByteArray())), "{" + UUID.randomUUID().toString() + "}", n);
        }
        
        X509Certificate[] getCertificateChain() {
            return this.certChain;
        }
        
        void setCertificateChain(final X509Certificate[] certChain) throws CertificateException, KeyStoreException {
            for (int i = 0; i < certChain.length; ++i) {
                final byte[] encoded = certChain[i].getEncoded();
                if (i == 0 && this.privateKey != null) {
                    CKeyStore.this.storeCertificate(CKeyStore.this.getName(), this.alias, encoded, encoded.length, this.privateKey.getHCryptProvider(), this.privateKey.getHCryptKey());
                }
                else {
                    CKeyStore.this.storeCertificate(CKeyStore.this.getName(), this.alias, encoded, encoded.length, 0L, 0L);
                }
            }
            this.certChain = certChain;
        }
    }
}
