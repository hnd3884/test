package sun.security.pkcs11;

import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.Callback;
import java.util.Set;
import java.util.Map;
import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import sun.security.util.DerValue;
import sun.security.pkcs11.wrapper.Functions;
import java.security.interfaces.ECPublicKey;
import javax.crypto.interfaces.DHPublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import javax.security.auth.x500.X500Principal;
import java.security.PublicKey;
import java.security.Provider;
import sun.security.util.ECUtil;
import java.security.InvalidKeyException;
import java.math.BigInteger;
import sun.security.rsa.RSAKeyFactory;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.security.interfaces.ECPrivateKey;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.X509Certificate;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Enumeration;
import java.security.cert.CertificateException;
import java.security.KeyStore;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.util.HashMap;
import sun.security.util.Debug;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.security.KeyStoreSpi;

final class P11KeyStore extends KeyStoreSpi
{
    private static final CK_ATTRIBUTE ATTR_CLASS_CERT;
    private static final CK_ATTRIBUTE ATTR_CLASS_PKEY;
    private static final CK_ATTRIBUTE ATTR_CLASS_SKEY;
    private static final CK_ATTRIBUTE ATTR_X509_CERT_TYPE;
    private static final CK_ATTRIBUTE ATTR_TOKEN_TRUE;
    private static CK_ATTRIBUTE ATTR_SKEY_TOKEN_TRUE;
    private static final CK_ATTRIBUTE ATTR_TRUSTED_TRUE;
    private static final CK_ATTRIBUTE ATTR_PRIVATE_TRUE;
    private static final long NO_HANDLE = -1L;
    private static final long FINDOBJECTS_MAX = 100L;
    private static final String ALIAS_SEP = "/";
    private static final boolean NSS_TEST = false;
    private static final Debug debug;
    private static boolean CKA_TRUSTED_SUPPORTED;
    private final Token token;
    private boolean writeDisabled;
    private HashMap<String, AliasInfo> aliasMap;
    private final boolean useSecmodTrust;
    private Secmod.TrustType nssTrustType;
    private static final long[] LONG0;
    
    P11KeyStore(final Token token) {
        this.writeDisabled = false;
        this.token = token;
        this.useSecmodTrust = token.provider.nssUseSecmodTrust;
    }
    
    @Override
    public synchronized Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        this.token.ensureValid();
        if (array != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new NoSuchAlgorithmException("password must be null");
        }
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        if (aliasInfo == null || aliasInfo.type == P11KeyStore.ATTR_CLASS_CERT) {
            return null;
        }
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            if (aliasInfo.type == P11KeyStore.ATTR_CLASS_PKEY) {
                final THandle tokenObject = this.getTokenObject(opSession, aliasInfo.type, aliasInfo.id, null);
                if (tokenObject.type == P11KeyStore.ATTR_CLASS_PKEY) {
                    return this.loadPkey(opSession, tokenObject.handle);
                }
            }
            else {
                final THandle tokenObject2 = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_SKEY, null, s);
                if (tokenObject2.type == P11KeyStore.ATTR_CLASS_SKEY) {
                    return this.loadSkey(opSession, tokenObject2.handle);
                }
            }
            return null;
        }
        catch (final PKCS11Exception | KeyStoreException ex) {
            throw new ProviderException((Throwable)ex);
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    @Override
    public synchronized Certificate[] engineGetCertificateChain(final String s) {
        this.token.ensureValid();
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        if (aliasInfo == null || aliasInfo.type != P11KeyStore.ATTR_CLASS_PKEY) {
            return null;
        }
        return aliasInfo.chain;
    }
    
    @Override
    public synchronized Certificate engineGetCertificate(final String s) {
        this.token.ensureValid();
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        if (aliasInfo == null) {
            return null;
        }
        return aliasInfo.cert;
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        this.token.ensureValid();
        throw new ProviderException(new UnsupportedOperationException());
    }
    
    @Override
    public synchronized void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        this.token.ensureValid();
        this.checkWrite();
        if (!(key instanceof PrivateKey) && !(key instanceof SecretKey)) {
            throw new KeyStoreException("key must be PrivateKey or SecretKey");
        }
        if (key instanceof PrivateKey && array2 == null) {
            throw new KeyStoreException("PrivateKey must be accompanied by non-null chain");
        }
        if (key instanceof SecretKey && array2 != null) {
            throw new KeyStoreException("SecretKey must be accompanied by null chain");
        }
        if (array != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new KeyStoreException("Password must be null");
        }
        KeyStore.Entry entry = null;
        try {
            if (key instanceof PrivateKey) {
                entry = new KeyStore.PrivateKeyEntry((PrivateKey)key, array2);
            }
            else if (key instanceof SecretKey) {
                entry = new KeyStore.SecretKeyEntry((SecretKey)key);
            }
        }
        catch (final NullPointerException | IllegalArgumentException ex) {
            throw new KeyStoreException((Throwable)ex);
        }
        this.engineSetEntry(s, entry, new KeyStore.PasswordProtection(array));
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        this.token.ensureValid();
        throw new ProviderException(new UnsupportedOperationException());
    }
    
    @Override
    public synchronized void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        this.token.ensureValid();
        this.checkWrite();
        if (certificate == null) {
            throw new KeyStoreException("invalid null certificate");
        }
        this.engineSetEntry(s, new KeyStore.TrustedCertificateEntry(certificate), null);
    }
    
    @Override
    public synchronized void engineDeleteEntry(final String s) throws KeyStoreException {
        this.token.ensureValid();
        if (this.token.isWriteProtected()) {
            throw new KeyStoreException("token write-protected");
        }
        this.checkWrite();
        this.deleteEntry(s);
    }
    
    private boolean deleteEntry(final String s) throws KeyStoreException {
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        if (aliasInfo != null) {
            this.aliasMap.remove(s);
            try {
                if (aliasInfo.type == P11KeyStore.ATTR_CLASS_CERT) {
                    return this.destroyCert(aliasInfo.id);
                }
                if (aliasInfo.type == P11KeyStore.ATTR_CLASS_PKEY) {
                    return this.destroyPkey(aliasInfo.id) && this.destroyChain(aliasInfo.id);
                }
                if (aliasInfo.type == P11KeyStore.ATTR_CLASS_SKEY) {
                    return this.destroySkey(s);
                }
                throw new KeyStoreException("unexpected entry type");
            }
            catch (final PKCS11Exception | CertificateException ex) {
                throw new KeyStoreException((Throwable)ex);
            }
        }
        return false;
    }
    
    @Override
    public synchronized Enumeration<String> engineAliases() {
        this.token.ensureValid();
        return Collections.enumeration(new HashSet<String>(this.aliasMap.keySet()));
    }
    
    @Override
    public synchronized boolean engineContainsAlias(final String s) {
        this.token.ensureValid();
        return this.aliasMap.containsKey(s);
    }
    
    @Override
    public synchronized int engineSize() {
        this.token.ensureValid();
        return this.aliasMap.size();
    }
    
    @Override
    public synchronized boolean engineIsKeyEntry(final String s) {
        this.token.ensureValid();
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        return aliasInfo != null && aliasInfo.type != P11KeyStore.ATTR_CLASS_CERT;
    }
    
    @Override
    public synchronized boolean engineIsCertificateEntry(final String s) {
        this.token.ensureValid();
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        return aliasInfo != null && aliasInfo.type == P11KeyStore.ATTR_CLASS_CERT;
    }
    
    @Override
    public synchronized String engineGetCertificateAlias(final Certificate certificate) {
        this.token.ensureValid();
        final Enumeration<String> engineAliases = this.engineAliases();
        while (engineAliases.hasMoreElements()) {
            final String s = engineAliases.nextElement();
            final Certificate engineGetCertificate = this.engineGetCertificate(s);
            if (engineGetCertificate != null && engineGetCertificate.equals(certificate)) {
                return s;
            }
        }
        return null;
    }
    
    @Override
    public synchronized void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.token.ensureValid();
        if (outputStream != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new IOException("output stream must be null");
        }
        if (array != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new IOException("password must be null");
        }
    }
    
    @Override
    public synchronized void engineStore(final KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.token.ensureValid();
        if (loadStoreParameter != null) {
            throw new IllegalArgumentException("LoadStoreParameter must be null");
        }
    }
    
    @Override
    public synchronized void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.token.ensureValid();
        if (inputStream != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new IOException("input stream must be null");
        }
        if (this.useSecmodTrust) {
            this.nssTrustType = Secmod.TrustType.ALL;
        }
        try {
            if (array == null) {
                this.login(null);
            }
            else {
                this.login(new PasswordCallbackHandler(array));
            }
        }
        catch (final LoginException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof PKCS11Exception && ((PKCS11Exception)cause).getErrorCode() == 160L) {
                throw new IOException("load failed", new UnrecoverableKeyException().initCause(ex));
            }
            throw new IOException("load failed", ex);
        }
        try {
            if (this.mapLabels()) {
                this.writeDisabled = true;
            }
            if (P11KeyStore.debug != null) {
                this.dumpTokenMap();
                P11KeyStore.debug.println("P11KeyStore load. Entry count: " + this.aliasMap.size());
            }
        }
        catch (final KeyStoreException | PKCS11Exception ex2) {
            throw new IOException("load failed", (Throwable)ex2);
        }
    }
    
    @Override
    public synchronized void engineLoad(final KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.token.ensureValid();
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("invalid null LoadStoreParameter");
        }
        if (this.useSecmodTrust) {
            if (loadStoreParameter instanceof Secmod.KeyStoreLoadParameter) {
                this.nssTrustType = ((Secmod.KeyStoreLoadParameter)loadStoreParameter).getTrustType();
            }
            else {
                this.nssTrustType = Secmod.TrustType.ALL;
            }
        }
        final KeyStore.ProtectionParameter protectionParameter = loadStoreParameter.getProtectionParameter();
        CallbackHandler callbackHandler;
        if (protectionParameter instanceof KeyStore.PasswordProtection) {
            final char[] password = ((KeyStore.PasswordProtection)protectionParameter).getPassword();
            if (password == null) {
                callbackHandler = null;
            }
            else {
                callbackHandler = new PasswordCallbackHandler(password);
            }
        }
        else {
            if (!(protectionParameter instanceof KeyStore.CallbackHandlerProtection)) {
                throw new IllegalArgumentException("ProtectionParameter must be either PasswordProtection or CallbackHandlerProtection");
            }
            callbackHandler = ((KeyStore.CallbackHandlerProtection)protectionParameter).getCallbackHandler();
        }
        try {
            this.login(callbackHandler);
            if (this.mapLabels()) {
                this.writeDisabled = true;
            }
            if (P11KeyStore.debug != null) {
                this.dumpTokenMap();
            }
        }
        catch (final LoginException | KeyStoreException | PKCS11Exception ex) {
            throw new IOException("load failed", (Throwable)ex);
        }
    }
    
    private void login(final CallbackHandler callbackHandler) throws LoginException {
        if ((this.token.tokenInfo.flags & 0x100L) == 0x0L) {
            this.token.provider.login(null, callbackHandler);
        }
        else {
            if (callbackHandler != null && !this.token.config.getKeyStoreCompatibilityMode()) {
                throw new LoginException("can not specify password if token supports protected authentication path");
            }
            this.token.provider.login(null, null);
        }
    }
    
    @Override
    public synchronized KeyStore.Entry engineGetEntry(final String s, final KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        this.token.ensureValid();
        if (protectionParameter != null && protectionParameter instanceof KeyStore.PasswordProtection && ((KeyStore.PasswordProtection)protectionParameter).getPassword() != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new KeyStoreException("ProtectionParameter must be null");
        }
        final AliasInfo aliasInfo = this.aliasMap.get(s);
        if (aliasInfo == null) {
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("engineGetEntry did not find alias [" + s + "] in map");
            }
            return null;
        }
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            if (aliasInfo.type == P11KeyStore.ATTR_CLASS_CERT) {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("engineGetEntry found trusted cert entry");
                }
                return new KeyStore.TrustedCertificateEntry(aliasInfo.cert);
            }
            if (aliasInfo.type == P11KeyStore.ATTR_CLASS_SKEY) {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("engineGetEntry found secret key entry");
                }
                final THandle tokenObject = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_SKEY, null, aliasInfo.label);
                if (tokenObject.type != P11KeyStore.ATTR_CLASS_SKEY) {
                    throw new KeyStoreException("expected but could not find secret key");
                }
                return new KeyStore.SecretKeyEntry(this.loadSkey(opSession, tokenObject.handle));
            }
            else {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("engineGetEntry found private key entry");
                }
                final THandle tokenObject2 = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_PKEY, aliasInfo.id, null);
                if (tokenObject2.type != P11KeyStore.ATTR_CLASS_PKEY) {
                    throw new KeyStoreException("expected but could not find private key");
                }
                final PrivateKey loadPkey = this.loadPkey(opSession, tokenObject2.handle);
                final X509Certificate[] access$800 = aliasInfo.chain;
                if (loadPkey != null && access$800 != null) {
                    return new KeyStore.PrivateKeyEntry(loadPkey, access$800);
                }
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("engineGetEntry got null cert chain or private key");
                }
                return null;
            }
        }
        catch (final PKCS11Exception ex) {
            throw new KeyStoreException(ex);
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    @Override
    public synchronized void engineSetEntry(final String s, final KeyStore.Entry entry, final KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        this.token.ensureValid();
        this.checkWrite();
        if (protectionParameter != null && protectionParameter instanceof KeyStore.PasswordProtection && ((KeyStore.PasswordProtection)protectionParameter).getPassword() != null && !this.token.config.getKeyStoreCompatibilityMode()) {
            throw new KeyStoreException(new UnsupportedOperationException("ProtectionParameter must be null"));
        }
        if (this.token.isWriteProtected()) {
            throw new KeyStoreException("token write-protected");
        }
        if (entry instanceof KeyStore.TrustedCertificateEntry) {
            if (!this.useSecmodTrust) {
                throw new KeyStoreException(new UnsupportedOperationException("trusted certificates may only be set by token initialization application"));
            }
            final Secmod.Module nssModule = this.token.provider.nssModule;
            if (nssModule.type != Secmod.ModuleType.KEYSTORE && nssModule.type != Secmod.ModuleType.FIPS) {
                throw new KeyStoreException("Trusted certificates can only be added to the NSS KeyStore module");
            }
            final Certificate trustedCertificate = ((KeyStore.TrustedCertificateEntry)entry).getTrustedCertificate();
            if (!(trustedCertificate instanceof X509Certificate)) {
                throw new KeyStoreException("Certificate must be an X509Certificate");
            }
            final X509Certificate x509Certificate = (X509Certificate)trustedCertificate;
            if (this.aliasMap.get(s) != null) {
                this.deleteEntry(s);
            }
            try {
                this.storeCert(s, x509Certificate);
                nssModule.setTrust(this.token, x509Certificate);
                this.mapLabels();
            }
            catch (final PKCS11Exception | CertificateException ex) {
                throw new KeyStoreException((Throwable)ex);
            }
        }
        else {
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                final PrivateKey privateKey = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
                if (!(privateKey instanceof P11Key) && !(privateKey instanceof RSAPrivateKey) && !(privateKey instanceof DSAPrivateKey) && !(privateKey instanceof DHPrivateKey) && !(privateKey instanceof ECPrivateKey)) {
                    throw new KeyStoreException("unsupported key type: " + privateKey.getClass().getName());
                }
                final Certificate[] certificateChain = ((KeyStore.PrivateKeyEntry)entry).getCertificateChain();
                if (!(certificateChain instanceof X509Certificate[])) {
                    throw new KeyStoreException(new UnsupportedOperationException("unsupported certificate array type: " + ((X509Certificate[])certificateChain).getClass().getName()));
                }
                try {
                    boolean b = false;
                    final Iterator<String> iterator = this.aliasMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        final AliasInfo aliasInfo = this.aliasMap.get(iterator.next());
                        if (aliasInfo.type == P11KeyStore.ATTR_CLASS_PKEY && aliasInfo.cert.getPublicKey().equals(certificateChain[0].getPublicKey())) {
                            this.updatePkey(s, aliasInfo.id, (X509Certificate[])certificateChain, !aliasInfo.cert.equals(certificateChain[0]));
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        this.engineDeleteEntry(s);
                        this.storePkey(s, (KeyStore.PrivateKeyEntry)entry);
                    }
                }
                catch (final PKCS11Exception | CertificateException ex2) {
                    throw new KeyStoreException((Throwable)ex2);
                }
            }
            else {
                if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                    throw new KeyStoreException(new UnsupportedOperationException("unsupported entry type: " + entry.getClass().getName()));
                }
                final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)entry;
                secretKeyEntry.getSecretKey();
                try {
                    if (this.aliasMap.get(s) != null) {
                        this.engineDeleteEntry(s);
                    }
                    this.storeSkey(s, secretKeyEntry);
                }
                catch (final PKCS11Exception ex3) {
                    throw new KeyStoreException(ex3);
                }
            }
            try {
                this.mapLabels();
                if (P11KeyStore.debug != null) {
                    this.dumpTokenMap();
                }
            }
            catch (final PKCS11Exception | CertificateException ex4) {
                throw new KeyStoreException((Throwable)ex4);
            }
        }
        if (P11KeyStore.debug != null) {
            P11KeyStore.debug.println("engineSetEntry added new entry for [" + s + "] to token");
        }
    }
    
    @Override
    public synchronized boolean engineEntryInstanceOf(final String s, final Class<? extends KeyStore.Entry> clazz) {
        this.token.ensureValid();
        return super.engineEntryInstanceOf(s, clazz);
    }
    
    private X509Certificate loadCert(final Session session, final long n) throws PKCS11Exception, CertificateException {
        final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L) };
        this.token.p11.C_GetAttributeValue(session.id(), n, array);
        final byte[] byteArray = array[0].getByteArray();
        if (byteArray == null) {
            throw new CertificateException("unexpectedly retrieved null byte array");
        }
        return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(byteArray));
    }
    
    private X509Certificate[] loadChain(final Session session, final X509Certificate x509Certificate) throws PKCS11Exception, CertificateException {
        if (x509Certificate.getSubjectX500Principal().equals(x509Certificate.getIssuerX500Principal())) {
            return new X509Certificate[] { x509Certificate };
        }
        final ArrayList list = new ArrayList();
        list.add(x509Certificate);
        X509Certificate loadCert = x509Certificate;
        do {
            final long[] objects = findObjects(session, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_CERT, new CK_ATTRIBUTE(257L, loadCert.getIssuerX500Principal().getEncoded()) });
            if (objects == null) {
                break;
            }
            if (objects.length == 0) {
                break;
            }
            if (P11KeyStore.debug != null && objects.length > 1) {
                P11KeyStore.debug.println("engineGetEntry found " + objects.length + " certificate entries for subject [" + loadCert.getIssuerX500Principal().toString() + "] in token - using first entry");
            }
            loadCert = this.loadCert(session, objects[0]);
            list.add(loadCert);
        } while (!loadCert.getSubjectX500Principal().equals(loadCert.getIssuerX500Principal()));
        return list.toArray(new X509Certificate[list.size()]);
    }
    
    private SecretKey loadSkey(final Session session, final long n) throws PKCS11Exception {
        final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(256L) };
        this.token.p11.C_GetAttributeValue(session.id(), n, array);
        final long long1 = array[0].getLong();
        String s = null;
        int n2 = -1;
        if (long1 == 19L || long1 == 21L) {
            if (long1 == 19L) {
                s = "DES";
                n2 = 64;
            }
            else if (long1 == 21L) {
                s = "DESede";
                n2 = 192;
            }
        }
        else {
            if (long1 == 31L) {
                s = "AES";
            }
            else if (long1 == 32L) {
                s = "Blowfish";
            }
            else if (long1 == 18L) {
                s = "ARCFOUR";
            }
            else {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("unknown key type [" + long1 + "] - using 'Generic Secret'");
                }
                s = "Generic Secret";
            }
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(353L) };
            this.token.p11.C_GetAttributeValue(session.id(), n, array2);
            n2 = (int)array2[0].getLong();
        }
        return P11Key.secretKey(session, n, s, n2, null);
    }
    
    private PrivateKey loadPkey(final Session session, final long n) throws PKCS11Exception, KeyStoreException {
        final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(256L) };
        this.token.p11.C_GetAttributeValue(session.id(), n, array);
        final long long1 = array[0].getLong();
        if (long1 == 0L) {
            final String s = "RSA";
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(288L) };
            this.token.p11.C_GetAttributeValue(session.id(), n, array2);
            final int bitLength = array2[0].getBigInteger().bitLength();
            try {
                RSAKeyFactory.checkKeyLengths(bitLength, null, -1, Integer.MAX_VALUE);
            }
            catch (final InvalidKeyException ex) {
                throw new KeyStoreException(ex.getMessage());
            }
            return P11Key.privateKey(session, n, s, bitLength, null);
        }
        if (long1 == 1L) {
            final String s2 = "DSA";
            final CK_ATTRIBUTE[] array3 = { new CK_ATTRIBUTE(304L) };
            this.token.p11.C_GetAttributeValue(session.id(), n, array3);
            return P11Key.privateKey(session, n, s2, array3[0].getBigInteger().bitLength(), null);
        }
        if (long1 == 2L) {
            final String s3 = "DH";
            final CK_ATTRIBUTE[] array4 = { new CK_ATTRIBUTE(304L) };
            this.token.p11.C_GetAttributeValue(session.id(), n, array4);
            return P11Key.privateKey(session, n, s3, array4[0].getBigInteger().bitLength(), null);
        }
        if (long1 == 3L) {
            final CK_ATTRIBUTE[] array5 = { new CK_ATTRIBUTE(384L) };
            this.token.p11.C_GetAttributeValue(session.id(), n, array5);
            final byte[] byteArray = array5[0].getByteArray();
            int fieldSize;
            try {
                fieldSize = ECUtil.getECParameterSpec(null, byteArray).getCurve().getField().getFieldSize();
            }
            catch (final IOException ex2) {
                throw new KeyStoreException("Unsupported parameters", ex2);
            }
            return P11Key.privateKey(session, n, "EC", fieldSize, null);
        }
        if (P11KeyStore.debug != null) {
            P11KeyStore.debug.println("unknown key type [" + long1 + "]");
        }
        throw new KeyStoreException("unknown key type");
    }
    
    private void updatePkey(final String s, final byte[] array, final X509Certificate[] array2, final boolean b) throws KeyStoreException, CertificateException, PKCS11Exception {
        final boolean b2 = true;
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            final THandle tokenObject = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_PKEY, array, null);
            if (tokenObject.type != P11KeyStore.ATTR_CLASS_PKEY) {
                throw new KeyStoreException("expected but could not find private key with CKA_ID " + getID(array));
            }
            final long access$700 = tokenObject.handle;
            final THandle tokenObject2 = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_CERT, array, null);
            if (tokenObject2.type != P11KeyStore.ATTR_CLASS_CERT) {
                throw new KeyStoreException("expected but could not find certificate with CKA_ID " + getID(array));
            }
            if (b2) {
                this.destroyChain(array);
            }
            else {
                this.token.p11.C_SetAttributeValue(opSession.id(), tokenObject2.handle, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(3L, s), new CK_ATTRIBUTE(258L, s) });
            }
            if (b2) {
                this.storeChain(s, array2);
            }
            else {
                this.storeCaCerts(array2, 1);
            }
            this.token.p11.C_SetAttributeValue(opSession.id(), access$700, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(258L, s) });
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("updatePkey set new alias [" + s + "] for private key entry");
            }
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private void updateP11Pkey(final String s, final CK_ATTRIBUTE ck_ATTRIBUTE, final P11Key p11Key) throws PKCS11Exception {
        Session opSession = null;
        final long keyID = p11Key.getKeyID();
        try {
            opSession = this.token.getOpSession();
            if (p11Key.tokenObject) {
                this.token.p11.C_SetAttributeValue(opSession.id(), keyID, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(258L, s) });
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("updateP11Pkey set new alias [" + s + "] for key entry");
                }
            }
            else {
                CK_ATTRIBUTE[] addAttribute = { P11KeyStore.ATTR_TOKEN_TRUE, new CK_ATTRIBUTE(258L, s) };
                if (ck_ATTRIBUTE != null) {
                    addAttribute = addAttribute(addAttribute, ck_ATTRIBUTE);
                }
                this.token.p11.C_CopyObject(opSession.id(), keyID, addAttribute);
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("updateP11Pkey copied private session key for [" + s + "] to token entry");
                }
            }
        }
        finally {
            this.token.releaseSession(opSession);
            p11Key.releaseKeyID();
        }
    }
    
    private void storeCert(final String s, final X509Certificate x509Certificate) throws PKCS11Exception, CertificateException {
        final ArrayList list = new ArrayList();
        list.add(P11KeyStore.ATTR_TOKEN_TRUE);
        list.add(P11KeyStore.ATTR_CLASS_CERT);
        list.add(P11KeyStore.ATTR_X509_CERT_TYPE);
        list.add(new CK_ATTRIBUTE(257L, x509Certificate.getSubjectX500Principal().getEncoded()));
        list.add(new CK_ATTRIBUTE(129L, x509Certificate.getIssuerX500Principal().getEncoded()));
        list.add(new CK_ATTRIBUTE(130L, x509Certificate.getSerialNumber().toByteArray()));
        list.add(new CK_ATTRIBUTE(17L, x509Certificate.getEncoded()));
        if (s != null) {
            list.add(new CK_ATTRIBUTE(3L, s));
            list.add(new CK_ATTRIBUTE(258L, s));
        }
        else {
            list.add(new CK_ATTRIBUTE(258L, this.getID(x509Certificate.getSubjectX500Principal().getName("CANONICAL"), x509Certificate)));
        }
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            this.token.p11.C_CreateObject(opSession.id(), list.toArray(new CK_ATTRIBUTE[list.size()]));
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private void storeChain(final String s, final X509Certificate[] array) throws PKCS11Exception, CertificateException {
        this.storeCert(s, array[0]);
        this.storeCaCerts(array, 1);
    }
    
    private void storeCaCerts(final X509Certificate[] array, final int n) throws PKCS11Exception, CertificateException {
        Session opSession = null;
        final HashSet set = new HashSet();
        try {
            opSession = this.token.getOpSession();
            final long[] objects = findObjects(opSession, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_CERT });
            for (int length = objects.length, i = 0; i < length; ++i) {
                set.add(this.loadCert(opSession, objects[i]));
            }
        }
        finally {
            this.token.releaseSession(opSession);
        }
        for (int j = n; j < array.length; ++j) {
            if (!set.contains(array[j])) {
                this.storeCert(null, array[j]);
            }
            else if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("ignoring duplicate CA cert for [" + array[j].getSubjectX500Principal() + "]");
            }
        }
    }
    
    private void storeSkey(final String s, final KeyStore.SecretKeyEntry secretKeyEntry) throws PKCS11Exception, KeyStoreException {
        final SecretKey secretKey = secretKeyEntry.getSecretKey();
        final CK_ATTRIBUTE[] array = { P11KeyStore.ATTR_SKEY_TOKEN_TRUE, P11KeyStore.ATTR_PRIVATE_TRUE, new CK_ATTRIBUTE(3L, s) };
        try {
            P11SecretKeyFactory.convertKey(this.token, secretKey, null, array);
        }
        catch (final InvalidKeyException ex) {
            throw new KeyStoreException("Cannot convert to PKCS11 keys", ex);
        }
        this.aliasMap.put(s, new AliasInfo(s));
        if (P11KeyStore.debug != null) {
            P11KeyStore.debug.println("storeSkey created token secret key for [" + s + "]");
        }
    }
    
    private static CK_ATTRIBUTE[] addAttribute(final CK_ATTRIBUTE[] array, final CK_ATTRIBUTE ck_ATTRIBUTE) {
        final int length = array.length;
        final CK_ATTRIBUTE[] array2 = new CK_ATTRIBUTE[length + 1];
        System.arraycopy(array, 0, array2, 0, length);
        array2[length] = ck_ATTRIBUTE;
        return array2;
    }
    
    private void storePkey(final String s, final KeyStore.PrivateKeyEntry privateKeyEntry) throws PKCS11Exception, CertificateException, KeyStoreException {
        final PrivateKey privateKey = privateKeyEntry.getPrivateKey();
        if (privateKey instanceof P11Key) {
            final P11Key p11Key = (P11Key)privateKey;
            if (p11Key.tokenObject && p11Key.token == this.token) {
                this.updateP11Pkey(s, null, p11Key);
                this.storeChain(s, (X509Certificate[])privateKeyEntry.getCertificateChain());
                return;
            }
        }
        final boolean nssNetscapeDbWorkaround = this.token.config.getNssNetscapeDbWorkaround();
        final PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();
        CK_ATTRIBUTE[] array;
        if (privateKey instanceof RSAPrivateKey) {
            array = this.getRsaPrivKeyAttrs(s, (RSAPrivateKey)privateKey, ((X509Certificate)privateKeyEntry.getCertificate()).getSubjectX500Principal());
        }
        else if (privateKey instanceof DSAPrivateKey) {
            final DSAPrivateKey dsaPrivateKey = (DSAPrivateKey)privateKey;
            final CK_ATTRIBUTE[] idAttributes = this.getIdAttributes(privateKey, publicKey, false, nssNetscapeDbWorkaround);
            if (idAttributes[0] == null) {
                idAttributes[0] = new CK_ATTRIBUTE(258L, s);
            }
            CK_ATTRIBUTE[] addAttribute = { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_PKEY, P11KeyStore.ATTR_PRIVATE_TRUE, new CK_ATTRIBUTE(256L, 1L), idAttributes[0], new CK_ATTRIBUTE(304L, dsaPrivateKey.getParams().getP()), new CK_ATTRIBUTE(305L, dsaPrivateKey.getParams().getQ()), new CK_ATTRIBUTE(306L, dsaPrivateKey.getParams().getG()), new CK_ATTRIBUTE(17L, dsaPrivateKey.getX()) };
            if (idAttributes[1] != null) {
                addAttribute = addAttribute(addAttribute, idAttributes[1]);
            }
            array = this.token.getAttributes("import", 3L, 1L, addAttribute);
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("storePkey created DSA template");
            }
        }
        else if (privateKey instanceof DHPrivateKey) {
            final DHPrivateKey dhPrivateKey = (DHPrivateKey)privateKey;
            final CK_ATTRIBUTE[] idAttributes2 = this.getIdAttributes(privateKey, publicKey, false, nssNetscapeDbWorkaround);
            if (idAttributes2[0] == null) {
                idAttributes2[0] = new CK_ATTRIBUTE(258L, s);
            }
            CK_ATTRIBUTE[] addAttribute2 = { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_PKEY, P11KeyStore.ATTR_PRIVATE_TRUE, new CK_ATTRIBUTE(256L, 2L), idAttributes2[0], new CK_ATTRIBUTE(304L, dhPrivateKey.getParams().getP()), new CK_ATTRIBUTE(306L, dhPrivateKey.getParams().getG()), new CK_ATTRIBUTE(17L, dhPrivateKey.getX()) };
            if (idAttributes2[1] != null) {
                addAttribute2 = addAttribute(addAttribute2, idAttributes2[1]);
            }
            array = this.token.getAttributes("import", 3L, 2L, addAttribute2);
        }
        else if (privateKey instanceof ECPrivateKey) {
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)privateKey;
            final CK_ATTRIBUTE[] idAttributes3 = this.getIdAttributes(privateKey, publicKey, false, nssNetscapeDbWorkaround);
            if (idAttributes3[0] == null) {
                idAttributes3[0] = new CK_ATTRIBUTE(258L, s);
            }
            CK_ATTRIBUTE[] addAttribute3 = { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_PKEY, P11KeyStore.ATTR_PRIVATE_TRUE, new CK_ATTRIBUTE(256L, 3L), idAttributes3[0], new CK_ATTRIBUTE(17L, ecPrivateKey.getS()), new CK_ATTRIBUTE(384L, ECUtil.encodeECParameterSpec(null, ecPrivateKey.getParams())) };
            if (idAttributes3[1] != null) {
                addAttribute3 = addAttribute(addAttribute3, idAttributes3[1]);
            }
            array = this.token.getAttributes("import", 3L, 3L, addAttribute3);
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("storePkey created EC template");
            }
        }
        else {
            if (!(privateKey instanceof P11Key)) {
                throw new KeyStoreException("unsupported key type: " + privateKey);
            }
            final P11Key p11Key2 = (P11Key)privateKey;
            if (p11Key2.token != this.token) {
                throw new KeyStoreException("Cannot move sensitive keys across tokens");
            }
            CK_ATTRIBUTE ck_ATTRIBUTE = null;
            if (nssNetscapeDbWorkaround) {
                ck_ATTRIBUTE = this.getIdAttributes(privateKey, publicKey, false, true)[1];
            }
            this.updateP11Pkey(s, ck_ATTRIBUTE, p11Key2);
            this.storeChain(s, (X509Certificate[])privateKeyEntry.getCertificateChain());
            return;
        }
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            this.token.p11.C_CreateObject(opSession.id(), array);
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("storePkey created token key for [" + s + "]");
            }
        }
        finally {
            this.token.releaseSession(opSession);
        }
        this.storeChain(s, (X509Certificate[])privateKeyEntry.getCertificateChain());
    }
    
    private CK_ATTRIBUTE[] getRsaPrivKeyAttrs(final String s, final RSAPrivateKey rsaPrivateKey, final X500Principal x500Principal) throws PKCS11Exception {
        CK_ATTRIBUTE[] array;
        if (rsaPrivateKey instanceof RSAPrivateCrtKey) {
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("creating RSAPrivateCrtKey attrs");
            }
            final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)rsaPrivateKey;
            array = this.token.getAttributes("import", 3L, 0L, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_PKEY, P11KeyStore.ATTR_PRIVATE_TRUE, new CK_ATTRIBUTE(256L, 0L), new CK_ATTRIBUTE(258L, s), new CK_ATTRIBUTE(288L, rsaPrivateCrtKey.getModulus()), new CK_ATTRIBUTE(291L, rsaPrivateCrtKey.getPrivateExponent()), new CK_ATTRIBUTE(290L, rsaPrivateCrtKey.getPublicExponent()), new CK_ATTRIBUTE(292L, rsaPrivateCrtKey.getPrimeP()), new CK_ATTRIBUTE(293L, rsaPrivateCrtKey.getPrimeQ()), new CK_ATTRIBUTE(294L, rsaPrivateCrtKey.getPrimeExponentP()), new CK_ATTRIBUTE(295L, rsaPrivateCrtKey.getPrimeExponentQ()), new CK_ATTRIBUTE(296L, rsaPrivateCrtKey.getCrtCoefficient()) });
        }
        else {
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("creating RSAPrivateKey attrs");
            }
            array = this.token.getAttributes("import", 3L, 0L, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_PKEY, P11KeyStore.ATTR_PRIVATE_TRUE, new CK_ATTRIBUTE(256L, 0L), new CK_ATTRIBUTE(258L, s), new CK_ATTRIBUTE(288L, rsaPrivateKey.getModulus()), new CK_ATTRIBUTE(291L, rsaPrivateKey.getPrivateExponent()) });
        }
        return array;
    }
    
    private CK_ATTRIBUTE[] getIdAttributes(final PrivateKey privateKey, final PublicKey publicKey, final boolean b, final boolean b2) {
        final CK_ATTRIBUTE[] array = new CK_ATTRIBUTE[2];
        if (!b && !b2) {
            return array;
        }
        final String algorithm = privateKey.getAlgorithm();
        if (algorithm.equals("RSA") && publicKey instanceof RSAPublicKey) {
            if (b) {
                array[0] = new CK_ATTRIBUTE(258L, P11Util.sha1(P11Util.getMagnitude(((RSAPublicKey)publicKey).getModulus())));
            }
        }
        else if (algorithm.equals("DSA") && publicKey instanceof DSAPublicKey) {
            final BigInteger y = ((DSAPublicKey)publicKey).getY();
            if (b) {
                array[0] = new CK_ATTRIBUTE(258L, P11Util.sha1(P11Util.getMagnitude(y)));
            }
            if (b2) {
                array[1] = new CK_ATTRIBUTE(3584088832L, y);
            }
        }
        else if (algorithm.equals("DH") && publicKey instanceof DHPublicKey) {
            final BigInteger y2 = ((DHPublicKey)publicKey).getY();
            if (b) {
                array[0] = new CK_ATTRIBUTE(258L, P11Util.sha1(P11Util.getMagnitude(y2)));
            }
            if (b2) {
                array[1] = new CK_ATTRIBUTE(3584088832L, y2);
            }
        }
        else {
            if (!algorithm.equals("EC") || !(publicKey instanceof ECPublicKey)) {
                throw new RuntimeException("Unknown key algorithm " + algorithm);
            }
            final ECPublicKey ecPublicKey = (ECPublicKey)publicKey;
            final byte[] encodePoint = ECUtil.encodePoint(ecPublicKey.getW(), ecPublicKey.getParams().getCurve());
            if (b) {
                array[0] = new CK_ATTRIBUTE(258L, P11Util.sha1(encodePoint));
            }
            if (b2) {
                array[1] = new CK_ATTRIBUTE(3584088832L, encodePoint);
            }
        }
        return array;
    }
    
    private boolean destroyCert(final byte[] array) throws PKCS11Exception, KeyStoreException {
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            final THandle tokenObject = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_CERT, array, null);
            if (tokenObject.type != P11KeyStore.ATTR_CLASS_CERT) {
                return false;
            }
            this.token.p11.C_DestroyObject(opSession.id(), tokenObject.handle);
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("destroyCert destroyed cert with CKA_ID [" + getID(array) + "]");
            }
            return true;
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private boolean destroyChain(final byte[] array) throws PKCS11Exception, CertificateException, KeyStoreException {
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            final THandle tokenObject = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_CERT, array, null);
            if (tokenObject.type != P11KeyStore.ATTR_CLASS_CERT) {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("destroyChain could not find end entity cert with CKA_ID [0x" + Functions.toHexString(array) + "]");
                }
                return false;
            }
            final X509Certificate loadCert = this.loadCert(opSession, tokenObject.handle);
            this.token.p11.C_DestroyObject(opSession.id(), tokenObject.handle);
            if (P11KeyStore.debug != null) {
                P11KeyStore.debug.println("destroyChain destroyed end entity cert with CKA_ID [" + getID(array) + "]");
            }
            X509Certificate loadCert2 = loadCert;
            while (!loadCert2.getSubjectX500Principal().equals(loadCert2.getIssuerX500Principal())) {
                final long[] objects = findObjects(opSession, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_CERT, new CK_ATTRIBUTE(257L, loadCert2.getIssuerX500Principal().getEncoded()) });
                if (objects != null) {
                    if (objects.length != 0) {
                        if (P11KeyStore.debug != null && objects.length > 1) {
                            P11KeyStore.debug.println("destroyChain found " + objects.length + " certificate entries for subject [" + loadCert2.getIssuerX500Principal() + "] in token - using first entry");
                        }
                        loadCert2 = this.loadCert(opSession, objects[0]);
                        final long[] objects2 = findObjects(opSession, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_CERT, new CK_ATTRIBUTE(129L, loadCert2.getSubjectX500Principal().getEncoded()) });
                        boolean b = false;
                        if (objects2 == null || objects2.length == 0) {
                            b = true;
                        }
                        else if (objects2.length == 1 && loadCert2.equals(this.loadCert(opSession, objects2[0]))) {
                            b = true;
                        }
                        if (b) {
                            this.token.p11.C_DestroyObject(opSession.id(), objects[0]);
                            if (P11KeyStore.debug == null) {
                                continue;
                            }
                            P11KeyStore.debug.println("destroyChain destroyed cert in chain with subject [" + loadCert2.getSubjectX500Principal() + "]");
                        }
                        else {
                            if (P11KeyStore.debug == null) {
                                continue;
                            }
                            P11KeyStore.debug.println("destroyChain did not destroy shared cert in chain with subject [" + loadCert2.getSubjectX500Principal() + "]");
                        }
                        continue;
                    }
                }
                return true;
            }
            return true;
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private boolean destroySkey(final String s) throws PKCS11Exception, KeyStoreException {
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            final THandle tokenObject = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_SKEY, null, s);
            if (tokenObject.type != P11KeyStore.ATTR_CLASS_SKEY) {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("destroySkey did not find secret key with CKA_LABEL [" + s + "]");
                }
                return false;
            }
            this.token.p11.C_DestroyObject(opSession.id(), tokenObject.handle);
            return true;
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private boolean destroyPkey(final byte[] array) throws PKCS11Exception, KeyStoreException {
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            final THandle tokenObject = this.getTokenObject(opSession, P11KeyStore.ATTR_CLASS_PKEY, array, null);
            if (tokenObject.type != P11KeyStore.ATTR_CLASS_PKEY) {
                if (P11KeyStore.debug != null) {
                    P11KeyStore.debug.println("destroyPkey did not find private key with CKA_ID [" + getID(array) + "]");
                }
                return false;
            }
            this.token.p11.C_DestroyObject(opSession.id(), tokenObject.handle);
            return true;
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private String getID(final String s, final X509Certificate x509Certificate) {
        return s + "/" + x509Certificate.getIssuerX500Principal().getName("CANONICAL") + "/" + x509Certificate.getSerialNumber().toString();
    }
    
    private static String getID(final byte[] array) {
        boolean b = true;
        for (int i = 0; i < array.length; ++i) {
            if (!DerValue.isPrintableStringChar((char)array[i])) {
                b = false;
                break;
            }
        }
        if (!b) {
            return "0x" + Functions.toHexString(array);
        }
        try {
            return new String(array, "UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            return "0x" + Functions.toHexString(array);
        }
    }
    
    private THandle getTokenObject(final Session session, final CK_ATTRIBUTE ck_ATTRIBUTE, final byte[] array, final String s) throws PKCS11Exception, KeyStoreException {
        CK_ATTRIBUTE[] array2;
        if (ck_ATTRIBUTE == P11KeyStore.ATTR_CLASS_SKEY) {
            array2 = new CK_ATTRIBUTE[] { P11KeyStore.ATTR_SKEY_TOKEN_TRUE, new CK_ATTRIBUTE(3L, s), ck_ATTRIBUTE };
        }
        else {
            array2 = new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, new CK_ATTRIBUTE(258L, array), ck_ATTRIBUTE };
        }
        final long[] objects = findObjects(session, array2);
        if (objects.length == 0) {
            if (P11KeyStore.debug != null) {
                if (ck_ATTRIBUTE == P11KeyStore.ATTR_CLASS_SKEY) {
                    P11KeyStore.debug.println("getTokenObject did not find secret key with CKA_LABEL [" + s + "]");
                }
                else if (ck_ATTRIBUTE == P11KeyStore.ATTR_CLASS_CERT) {
                    P11KeyStore.debug.println("getTokenObject did not find cert with CKA_ID [" + getID(array) + "]");
                }
                else {
                    P11KeyStore.debug.println("getTokenObject did not find private key with CKA_ID [" + getID(array) + "]");
                }
            }
            return new THandle(-1L, (CK_ATTRIBUTE)null);
        }
        if (objects.length == 1) {
            return new THandle(objects[0], ck_ATTRIBUTE);
        }
        if (ck_ATTRIBUTE == P11KeyStore.ATTR_CLASS_SKEY) {
            final ArrayList list = new ArrayList<THandle>(objects.length);
            for (int i = 0; i < objects.length; ++i) {
                final CK_ATTRIBUTE[] array3 = { new CK_ATTRIBUTE(3L) };
                this.token.p11.C_GetAttributeValue(session.id(), objects[i], array3);
                if (array3[0].pValue != null && s.equals(new String(array3[0].getCharArray()))) {
                    list.add(new THandle(objects[i], P11KeyStore.ATTR_CLASS_SKEY));
                }
            }
            if (list.size() == 1) {
                return list.get(0);
            }
            throw new KeyStoreException("invalid KeyStore state: found " + list.size() + " secret keys sharing CKA_LABEL [" + s + "]");
        }
        else {
            if (ck_ATTRIBUTE == P11KeyStore.ATTR_CLASS_CERT) {
                throw new KeyStoreException("invalid KeyStore state: found " + objects.length + " certificates sharing CKA_ID " + getID(array));
            }
            throw new KeyStoreException("invalid KeyStore state: found " + objects.length + " private keys sharing CKA_ID " + getID(array));
        }
    }
    
    private boolean mapLabels() throws PKCS11Exception, CertificateException, KeyStoreException {
        final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(134L) };
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            final ArrayList list = new ArrayList();
            for (final long n : findObjects(opSession, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_PKEY })) {
                final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(258L) };
                this.token.p11.C_GetAttributeValue(opSession.id(), n, array2);
                if (array2[0].pValue != null) {
                    list.add(array2[0].getByteArray());
                }
            }
            final HashMap<String, HashSet<?>> hashMap = new HashMap<String, HashSet<?>>();
            for (final long n2 : findObjects(opSession, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_CERT })) {
                final CK_ATTRIBUTE[] array3 = { new CK_ATTRIBUTE(3L) };
                String id = null;
                byte[] byteArray = null;
                try {
                    this.token.p11.C_GetAttributeValue(opSession.id(), n2, array3);
                    if (array3[0].pValue != null) {
                        id = new String(array3[0].getCharArray());
                    }
                }
                catch (final PKCS11Exception ex) {
                    if (ex.getErrorCode() != 18L) {
                        throw ex;
                    }
                }
                final CK_ATTRIBUTE[] array4 = { new CK_ATTRIBUTE(258L) };
                this.token.p11.C_GetAttributeValue(opSession.id(), n2, array4);
                Label_0545: {
                    if (array4[0].pValue == null) {
                        if (id == null) {
                            break Label_0545;
                        }
                    }
                    else {
                        if (id == null) {
                            id = getID(array4[0].getByteArray());
                        }
                        byteArray = array4[0].getByteArray();
                    }
                    final X509Certificate loadCert = this.loadCert(opSession, n2);
                    boolean b = false;
                    if (this.useSecmodTrust) {
                        b = Secmod.getInstance().isTrusted(loadCert, this.nssTrustType);
                    }
                    else if (P11KeyStore.CKA_TRUSTED_SUPPORTED) {
                        try {
                            this.token.p11.C_GetAttributeValue(opSession.id(), n2, array);
                            b = array[0].getBoolean();
                        }
                        catch (final PKCS11Exception ex2) {
                            if (ex2.getErrorCode() == 18L) {
                                P11KeyStore.CKA_TRUSTED_SUPPORTED = false;
                                if (P11KeyStore.debug != null) {
                                    P11KeyStore.debug.println("CKA_TRUSTED attribute not supported");
                                }
                            }
                        }
                    }
                    HashSet<?> set = hashMap.get(id);
                    if (set == null) {
                        set = new HashSet<Object>(2);
                        hashMap.put(id, set);
                    }
                    set.add(new AliasInfo(id, byteArray, b, loadCert));
                }
            }
            final HashMap hashMap2 = new HashMap();
            for (final long n3 : findObjects(opSession, new CK_ATTRIBUTE[] { P11KeyStore.ATTR_SKEY_TOKEN_TRUE, P11KeyStore.ATTR_CLASS_SKEY })) {
                final CK_ATTRIBUTE[] array5 = { new CK_ATTRIBUTE(3L) };
                this.token.p11.C_GetAttributeValue(opSession.id(), n3, array5);
                if (array5[0].pValue != null) {
                    final String s = new String(array5[0].getCharArray());
                    if (hashMap2.get(s) != null) {
                        throw new KeyStoreException("invalid KeyStore state: found multiple secret keys sharing same CKA_LABEL [" + s + "]");
                    }
                    hashMap2.put(s, new AliasInfo(s));
                }
            }
            final boolean mapCerts = this.mapCerts(this.mapPrivateKeys(list, (HashMap<String, HashSet<AliasInfo>>)hashMap), (HashMap<String, HashSet<AliasInfo>>)hashMap);
            this.mapSecretKeys(hashMap2);
            return mapCerts;
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private ArrayList<AliasInfo> mapPrivateKeys(final ArrayList<byte[]> list, final HashMap<String, HashSet<AliasInfo>> hashMap) throws PKCS11Exception, CertificateException {
        this.aliasMap = new HashMap<String, AliasInfo>();
        final ArrayList list2 = new ArrayList();
        for (final byte[] array : list) {
            boolean b = false;
            for (final String s : hashMap.keySet()) {
                final HashSet set = hashMap.get(s);
                for (final AliasInfo aliasInfo : set) {
                    if (Arrays.equals(array, aliasInfo.id)) {
                        if (set.size() == 1) {
                            aliasInfo.matched = true;
                            this.aliasMap.put(s, aliasInfo);
                        }
                        else {
                            aliasInfo.matched = true;
                            this.aliasMap.put(this.getID(s, aliasInfo.cert), aliasInfo);
                        }
                        list2.add(aliasInfo);
                        b = true;
                        break;
                    }
                }
                if (b) {
                    break;
                }
            }
            if (!b && P11KeyStore.debug != null) {
                P11KeyStore.debug.println("did not find match for private key with CKA_ID [" + getID(array) + "] (ignoring entry)");
            }
        }
        return list2;
    }
    
    private boolean mapCerts(final ArrayList<AliasInfo> list, final HashMap<String, HashSet<AliasInfo>> hashMap) throws PKCS11Exception, CertificateException {
        for (final AliasInfo aliasInfo : list) {
            Session opSession = null;
            try {
                opSession = this.token.getOpSession();
                aliasInfo.chain = this.loadChain(opSession, aliasInfo.cert);
            }
            finally {
                this.token.releaseSession(opSession);
            }
        }
        boolean b = false;
        for (final String s : hashMap.keySet()) {
            final HashSet set = hashMap.get(s);
            for (final AliasInfo aliasInfo2 : set) {
                if (aliasInfo2.matched) {
                    aliasInfo2.trusted = false;
                }
                else {
                    if (!P11KeyStore.CKA_TRUSTED_SUPPORTED) {
                        continue;
                    }
                    if (!aliasInfo2.trusted || !this.mapTrustedCert(s, aliasInfo2, set)) {
                        continue;
                    }
                    b = true;
                }
            }
        }
        return b;
    }
    
    private boolean mapTrustedCert(final String s, final AliasInfo aliasInfo, final HashSet<AliasInfo> set) {
        boolean b = false;
        aliasInfo.type = P11KeyStore.ATTR_CLASS_CERT;
        aliasInfo.trusted = true;
        if (set.size() == 1) {
            this.aliasMap.put(s, aliasInfo);
        }
        else {
            b = true;
            this.aliasMap.put(this.getID(s, aliasInfo.cert), aliasInfo);
        }
        return b;
    }
    
    private void mapSecretKeys(final HashMap<String, AliasInfo> hashMap) throws KeyStoreException {
        for (final String s : hashMap.keySet()) {
            if (this.aliasMap.containsKey(s)) {
                throw new KeyStoreException("invalid KeyStore state: found secret key sharing CKA_LABEL [" + s + "] with another token object");
            }
        }
        this.aliasMap.putAll(hashMap);
    }
    
    private void dumpTokenMap() {
        final Set<String> keySet = this.aliasMap.keySet();
        System.out.println("Token Alias Map:");
        if (keySet.isEmpty()) {
            System.out.println("  [empty]");
        }
        else {
            for (final String s : keySet) {
                System.out.println("  " + s + this.aliasMap.get(s));
            }
        }
    }
    
    private void checkWrite() throws KeyStoreException {
        if (this.writeDisabled) {
            throw new KeyStoreException("This PKCS11KeyStore does not support write capabilities");
        }
    }
    
    private static long[] findObjects(final Session session, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
        final Token token = session.token;
        long[] array2 = P11KeyStore.LONG0;
        token.p11.C_FindObjectsInit(session.id(), array);
        while (true) {
            final long[] c_FindObjects = token.p11.C_FindObjects(session.id(), 100L);
            if (c_FindObjects.length == 0) {
                break;
            }
            array2 = P11Util.concat(array2, c_FindObjects);
        }
        token.p11.C_FindObjectsFinal(session.id());
        return array2;
    }
    
    static {
        ATTR_CLASS_CERT = new CK_ATTRIBUTE(0L, 1L);
        ATTR_CLASS_PKEY = new CK_ATTRIBUTE(0L, 3L);
        ATTR_CLASS_SKEY = new CK_ATTRIBUTE(0L, 4L);
        ATTR_X509_CERT_TYPE = new CK_ATTRIBUTE(128L, 0L);
        ATTR_TOKEN_TRUE = new CK_ATTRIBUTE(1L, true);
        P11KeyStore.ATTR_SKEY_TOKEN_TRUE = P11KeyStore.ATTR_TOKEN_TRUE;
        ATTR_TRUSTED_TRUE = new CK_ATTRIBUTE(134L, true);
        ATTR_PRIVATE_TRUE = new CK_ATTRIBUTE(2L, true);
        debug = Debug.getInstance("pkcs11keystore");
        P11KeyStore.CKA_TRUSTED_SUPPORTED = true;
        LONG0 = new long[0];
    }
    
    private static class AliasInfo
    {
        private CK_ATTRIBUTE type;
        private String label;
        private byte[] id;
        private boolean trusted;
        private X509Certificate cert;
        private X509Certificate[] chain;
        private boolean matched;
        
        public AliasInfo(final String label) {
            this.type = null;
            this.label = null;
            this.id = null;
            this.trusted = false;
            this.cert = null;
            this.chain = null;
            this.matched = false;
            this.type = P11KeyStore.ATTR_CLASS_SKEY;
            this.label = label;
        }
        
        public AliasInfo(final String label, final byte[] id, final boolean trusted, final X509Certificate cert) {
            this.type = null;
            this.label = null;
            this.id = null;
            this.trusted = false;
            this.cert = null;
            this.chain = null;
            this.matched = false;
            this.type = P11KeyStore.ATTR_CLASS_PKEY;
            this.label = label;
            this.id = id;
            this.trusted = trusted;
            this.cert = cert;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if (this.type == P11KeyStore.ATTR_CLASS_PKEY) {
                sb.append("\ttype=[private key]\n");
            }
            else if (this.type == P11KeyStore.ATTR_CLASS_SKEY) {
                sb.append("\ttype=[secret key]\n");
            }
            else if (this.type == P11KeyStore.ATTR_CLASS_CERT) {
                sb.append("\ttype=[trusted cert]\n");
            }
            sb.append("\tlabel=[" + this.label + "]\n");
            if (this.id == null) {
                sb.append("\tid=[null]\n");
            }
            else {
                sb.append("\tid=" + getID(this.id) + "\n");
            }
            sb.append("\ttrusted=[" + this.trusted + "]\n");
            sb.append("\tmatched=[" + this.matched + "]\n");
            if (this.cert == null) {
                sb.append("\tcert=[null]\n");
            }
            else {
                sb.append("\tcert=[\tsubject: " + this.cert.getSubjectX500Principal() + "\n\t\tissuer: " + this.cert.getIssuerX500Principal() + "\n\t\tserialNum: " + this.cert.getSerialNumber().toString() + "]");
            }
            return sb.toString();
        }
    }
    
    private static class PasswordCallbackHandler implements CallbackHandler
    {
        private char[] password;
        
        private PasswordCallbackHandler(final char[] array) {
            if (array != null) {
                this.password = array.clone();
            }
        }
        
        @Override
        public void handle(final Callback[] array) throws IOException, UnsupportedCallbackException {
            if (!(array[0] instanceof PasswordCallback)) {
                throw new UnsupportedCallbackException(array[0]);
            }
            ((PasswordCallback)array[0]).setPassword(this.password);
        }
        
        @Override
        protected void finalize() throws Throwable {
            if (this.password != null) {
                Arrays.fill(this.password, ' ');
            }
            super.finalize();
        }
    }
    
    private static class THandle
    {
        private final long handle;
        private final CK_ATTRIBUTE type;
        
        private THandle(final long handle, final CK_ATTRIBUTE type) {
            this.handle = handle;
            this.type = type;
        }
    }
}
