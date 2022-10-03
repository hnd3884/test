package sun.security.provider;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore;
import java.util.Enumeration;
import java.security.KeyStoreException;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import sun.security.util.Debug;
import java.security.KeyStoreSpi;

class KeyStoreDelegator extends KeyStoreSpi
{
    private static final String KEYSTORE_TYPE_COMPAT = "keystore.type.compat";
    private static final Debug debug;
    private final String primaryType;
    private final String secondaryType;
    private final Class<? extends KeyStoreSpi> primaryKeyStore;
    private final Class<? extends KeyStoreSpi> secondaryKeyStore;
    private String type;
    private KeyStoreSpi keystore;
    private boolean compatModeEnabled;
    
    public KeyStoreDelegator(final String s, final Class<? extends KeyStoreSpi> clazz, final String secondaryType, final Class<? extends KeyStoreSpi> secondaryKeyStore) {
        this.compatModeEnabled = true;
        this.compatModeEnabled = "true".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("keystore.type.compat");
            }
        }));
        if (this.compatModeEnabled) {
            this.primaryType = s;
            this.secondaryType = secondaryType;
            this.primaryKeyStore = clazz;
            this.secondaryKeyStore = secondaryKeyStore;
        }
        else {
            this.primaryType = s;
            this.secondaryType = null;
            this.primaryKeyStore = clazz;
            this.secondaryKeyStore = null;
            if (KeyStoreDelegator.debug != null) {
                KeyStoreDelegator.debug.println("WARNING: compatibility mode disabled for " + s + " and " + secondaryType + " keystore types");
            }
        }
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return this.keystore.engineGetKey(s, array);
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        return this.keystore.engineGetCertificateChain(s);
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        return this.keystore.engineGetCertificate(s);
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        return this.keystore.engineGetCreationDate(s);
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        this.keystore.engineSetKeyEntry(s, key, array, array2);
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        this.keystore.engineSetKeyEntry(s, array, array2);
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        this.keystore.engineSetCertificateEntry(s, certificate);
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        this.keystore.engineDeleteEntry(s);
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return this.keystore.engineAliases();
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.keystore.engineContainsAlias(s);
    }
    
    @Override
    public int engineSize() {
        return this.keystore.engineSize();
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        return this.keystore.engineIsKeyEntry(s);
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        return this.keystore.engineIsCertificateEntry(s);
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        return this.keystore.engineGetCertificateAlias(certificate);
    }
    
    @Override
    public KeyStore.Entry engineGetEntry(final String s, final KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        return this.keystore.engineGetEntry(s, protectionParameter);
    }
    
    @Override
    public void engineSetEntry(final String s, final KeyStore.Entry entry, final KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        this.keystore.engineSetEntry(s, entry, protectionParameter);
    }
    
    @Override
    public boolean engineEntryInstanceOf(final String s, final Class<? extends KeyStore.Entry> clazz) {
        return this.keystore.engineEntryInstanceOf(s, clazz);
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (KeyStoreDelegator.debug != null) {
            KeyStoreDelegator.debug.println("Storing keystore in " + this.type + " format");
        }
        this.keystore.engineStore(outputStream, array);
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (inputStream != null) {
            if (this.compatModeEnabled) {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bufferedInputStream.mark(Integer.MAX_VALUE);
                try {
                    this.keystore = (KeyStoreSpi)this.primaryKeyStore.newInstance();
                    this.type = this.primaryType;
                    this.keystore.engineLoad(bufferedInputStream, array);
                }
                catch (final Exception ex) {
                    if (ex instanceof IOException && ex.getCause() instanceof UnrecoverableKeyException) {
                        throw (IOException)ex;
                    }
                    try {
                        this.keystore = (KeyStoreSpi)this.secondaryKeyStore.newInstance();
                        this.type = this.secondaryType;
                        bufferedInputStream.reset();
                        this.keystore.engineLoad(bufferedInputStream, array);
                        if (KeyStoreDelegator.debug != null) {
                            KeyStoreDelegator.debug.println("WARNING: switching from " + this.primaryType + " to " + this.secondaryType + " keystore file format has altered the keystore security level");
                        }
                    }
                    catch (final InstantiationException | IllegalAccessException ex2) {}
                    catch (final IOException | NoSuchAlgorithmException | CertificateException ex3) {
                        if (ex3 instanceof IOException && ((Throwable)ex3).getCause() instanceof UnrecoverableKeyException) {
                            throw (IOException)ex3;
                        }
                        if (ex instanceof IOException) {
                            throw (IOException)ex;
                        }
                        if (ex instanceof CertificateException) {
                            throw (CertificateException)ex;
                        }
                        if (ex instanceof NoSuchAlgorithmException) {
                            throw (NoSuchAlgorithmException)ex;
                        }
                    }
                }
                if (KeyStoreDelegator.debug != null) {
                    KeyStoreDelegator.debug.println("Loaded a keystore in " + this.type + " format");
                }
                return;
            }
        }
        try {
            this.keystore = (KeyStoreSpi)this.primaryKeyStore.newInstance();
        }
        catch (final InstantiationException | IllegalAccessException ex4) {}
        this.type = this.primaryType;
        if (KeyStoreDelegator.debug != null && inputStream == null) {
            KeyStoreDelegator.debug.println("Creating a new keystore in " + this.type + " format");
        }
        this.keystore.engineLoad(inputStream, array);
    }
    
    static {
        debug = Debug.getInstance("keystore");
    }
}
