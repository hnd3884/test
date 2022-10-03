package org.bouncycastle.jcajce.provider.keystore.pkcs12;

import org.bouncycastle.util.Strings;
import java.util.Collections;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.util.Integers;
import java.util.HashMap;
import java.util.Map;
import java.security.Security;
import javax.crypto.Mac;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.BEROutputStream;
import org.bouncycastle.asn1.DEROutputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import org.bouncycastle.jce.provider.JDKPKCS12StoreParameter;
import org.bouncycastle.jcajce.PKCS12StoreParameter;
import java.security.KeyStore;
import org.bouncycastle.util.Properties;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.pkcs.Pfx;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import javax.crypto.SecretKeyFactory;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.Cipher;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.PKCS12Key;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.security.Principal;
import java.io.IOException;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.X509Certificate;
import java.util.Vector;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.Key;
import java.util.Enumeration;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.cert.CertificateFactory;
import java.security.SecureRandom;
import java.util.Hashtable;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.KeyStoreSpi;

public class PKCS12KeyStoreSpi extends KeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers, BCKeyStore
{
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";
    private final JcaJceHelper helper;
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 51200;
    private static final DefaultSecretKeyProvider keySizeProvider;
    private IgnoresCaseHashtable keys;
    private Hashtable localIds;
    private IgnoresCaseHashtable certs;
    private Hashtable chainCerts;
    private Hashtable keyCerts;
    static final int NULL = 0;
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int SECRET = 3;
    static final int SEALED = 4;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    protected SecureRandom random;
    private CertificateFactory certFact;
    private ASN1ObjectIdentifier keyAlgorithm;
    private ASN1ObjectIdentifier certAlgorithm;
    private AlgorithmIdentifier macAlgorithm;
    private int itCount;
    private int saltLength;
    private static Provider provider;
    
    public PKCS12KeyStoreSpi(final Provider provider, final ASN1ObjectIdentifier keyAlgorithm, final ASN1ObjectIdentifier certAlgorithm) {
        this.helper = new BCJcaJceHelper();
        this.keys = new IgnoresCaseHashtable();
        this.localIds = new Hashtable();
        this.certs = new IgnoresCaseHashtable();
        this.chainCerts = new Hashtable();
        this.keyCerts = new Hashtable();
        this.random = new SecureRandom();
        this.macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        this.itCount = 102400;
        this.saltLength = 20;
        this.keyAlgorithm = keyAlgorithm;
        this.certAlgorithm = certAlgorithm;
        try {
            if (provider != null) {
                this.certFact = CertificateFactory.getInstance("X.509", provider);
            }
            else {
                this.certFact = CertificateFactory.getInstance("X.509");
            }
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException("can't create cert factory - " + ex.toString());
        }
    }
    
    private SubjectKeyIdentifier createSubjectKeyId(final PublicKey publicKey) {
        try {
            return new SubjectKeyIdentifier(getDigest(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())));
        }
        catch (final Exception ex) {
            throw new RuntimeException("error creating key");
        }
    }
    
    private static byte[] getDigest(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final Digest sha1 = DigestFactory.createSHA1();
        final byte[] array = new byte[sha1.getDigestSize()];
        final byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        sha1.update(bytes, 0, bytes.length);
        sha1.doFinal(array, 0);
        return array;
    }
    
    public void setRandom(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    public Enumeration engineAliases() {
        final Hashtable hashtable = new Hashtable();
        final Enumeration keys = this.certs.keys();
        while (keys.hasMoreElements()) {
            hashtable.put(keys.nextElement(), "cert");
        }
        final Enumeration keys2 = this.keys.keys();
        while (keys2.hasMoreElements()) {
            final String s = keys2.nextElement();
            if (hashtable.get(s) == null) {
                hashtable.put(s, "key");
            }
        }
        return hashtable.keys();
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.certs.get(s) != null || this.keys.get(s) != null;
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        final Key key = (Key)this.keys.remove(s);
        Certificate certificate = (Certificate)this.certs.remove(s);
        if (certificate != null) {
            this.chainCerts.remove(new CertId(certificate.getPublicKey()));
        }
        if (key != null) {
            final String s2 = this.localIds.remove(s);
            if (s2 != null) {
                certificate = (Certificate)this.keyCerts.remove(s2);
            }
            if (certificate != null) {
                this.chainCerts.remove(new CertId(certificate.getPublicKey()));
            }
        }
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("null alias passed to getCertificate.");
        }
        Certificate certificate = (Certificate)this.certs.get(s);
        if (certificate == null) {
            final String s2 = this.localIds.get(s);
            if (s2 != null) {
                certificate = (Certificate)this.keyCerts.get(s2);
            }
            else {
                certificate = this.keyCerts.get(s);
            }
        }
        return certificate;
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        final Enumeration elements = this.certs.elements();
        final Enumeration keys = this.certs.keys();
        while (elements.hasMoreElements()) {
            final Certificate certificate2 = elements.nextElement();
            final String s = keys.nextElement();
            if (certificate2.equals(certificate)) {
                return s;
            }
        }
        final Enumeration elements2 = this.keyCerts.elements();
        final Enumeration keys2 = this.keyCerts.keys();
        while (elements2.hasMoreElements()) {
            final Certificate certificate3 = (Certificate)elements2.nextElement();
            final String s2 = (String)keys2.nextElement();
            if (certificate3.equals(certificate)) {
                return s2;
            }
        }
        return null;
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("null alias passed to getCertificateChain.");
        }
        if (!this.engineIsKeyEntry(s)) {
            return null;
        }
        Certificate engineGetCertificate = this.engineGetCertificate(s);
        if (engineGetCertificate != null) {
            final Vector vector = new Vector();
            while (engineGetCertificate != null) {
                final X509Certificate x509Certificate = (X509Certificate)engineGetCertificate;
                Certificate certificate = null;
                final byte[] extensionValue = x509Certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (extensionValue != null) {
                    try {
                        final AuthorityKeyIdentifier instance = AuthorityKeyIdentifier.getInstance(new ASN1InputStream(((ASN1OctetString)new ASN1InputStream(extensionValue).readObject()).getOctets()).readObject());
                        if (instance.getKeyIdentifier() != null) {
                            certificate = (Certificate)this.chainCerts.get(new CertId(instance.getKeyIdentifier()));
                        }
                    }
                    catch (final IOException ex) {
                        throw new RuntimeException(ex.toString());
                    }
                }
                if (certificate == null) {
                    final Principal issuerDN = x509Certificate.getIssuerDN();
                    if (!issuerDN.equals(x509Certificate.getSubjectDN())) {
                        final Enumeration keys = this.chainCerts.keys();
                        while (keys.hasMoreElements()) {
                            final X509Certificate x509Certificate2 = this.chainCerts.get(keys.nextElement());
                            if (x509Certificate2.getSubjectDN().equals(issuerDN)) {
                                try {
                                    x509Certificate.verify(x509Certificate2.getPublicKey());
                                    certificate = x509Certificate2;
                                    break;
                                }
                                catch (final Exception ex2) {}
                            }
                        }
                    }
                }
                if (vector.contains(engineGetCertificate)) {
                    engineGetCertificate = null;
                }
                else {
                    vector.addElement(engineGetCertificate);
                    if (certificate != engineGetCertificate) {
                        engineGetCertificate = certificate;
                    }
                    else {
                        engineGetCertificate = null;
                    }
                }
            }
            final Certificate[] array = new Certificate[vector.size()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = (Certificate)vector.elementAt(i);
            }
            return array;
        }
        return null;
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        if (s == null) {
            throw new NullPointerException("alias == null");
        }
        if (this.keys.get(s) == null && this.certs.get(s) == null) {
            return null;
        }
        return new Date();
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (s == null) {
            throw new IllegalArgumentException("null alias passed to getKey.");
        }
        return (Key)this.keys.get(s);
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        return this.certs.get(s) != null && this.keys.get(s) == null;
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        return this.keys.get(s) != null;
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        if (this.keys.get(s) != null) {
            throw new KeyStoreException("There is a key entry with the name " + s + ".");
        }
        this.certs.put(s, certificate);
        this.chainCerts.put(new CertId(certificate.getPublicKey()), certificate);
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        }
        if (key instanceof PrivateKey && array2 == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        if (this.keys.get(s) != null) {
            this.engineDeleteEntry(s);
        }
        this.keys.put(s, key);
        if (array2 != null) {
            this.certs.put(s, array2[0]);
            for (int i = 0; i != array2.length; ++i) {
                this.chainCerts.put(new CertId(array2[i].getPublicKey()), array2[i]);
            }
        }
    }
    
    @Override
    public int engineSize() {
        final Hashtable hashtable = new Hashtable();
        final Enumeration keys = this.certs.keys();
        while (keys.hasMoreElements()) {
            hashtable.put(keys.nextElement(), "cert");
        }
        final Enumeration keys2 = this.keys.keys();
        while (keys2.hasMoreElements()) {
            final String s = keys2.nextElement();
            if (hashtable.get(s) == null) {
                hashtable.put(s, "key");
            }
        }
        return hashtable.size();
    }
    
    protected PrivateKey unwrapKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final char[] array2, final boolean b) throws IOException {
        final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        try {
            if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                final PKCS12PBEParams instance = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(instance.getIV(), this.validateIterationCount(instance.getIterations()));
                final Cipher cipher = this.helper.createCipher(algorithm.getId());
                cipher.init(4, new PKCS12Key(array2, b), pbeParameterSpec);
                return (PrivateKey)cipher.unwrap(array, "", 2);
            }
            if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
                return (PrivateKey)this.createCipher(4, array2, algorithmIdentifier).unwrap(array, "", 2);
            }
        }
        catch (final Exception ex) {
            throw new IOException("exception unwrapping private key - " + ex.toString());
        }
        throw new IOException("exception unwrapping private key - cannot recognise: " + algorithm);
    }
    
    protected byte[] wrapKey(final String s, final Key key, final PKCS12PBEParams pkcs12PBEParams, final char[] array) throws IOException {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(array);
        byte[] wrap;
        try {
            final SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(s);
            final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(pkcs12PBEParams.getIV(), pkcs12PBEParams.getIterations().intValue());
            final Cipher cipher = this.helper.createCipher(s);
            cipher.init(3, secretKeyFactory.generateSecret(pbeKeySpec), pbeParameterSpec);
            wrap = cipher.wrap(key);
        }
        catch (final Exception ex) {
            throw new IOException("exception encrypting data - " + ex.toString());
        }
        return wrap;
    }
    
    protected byte[] cryptData(final boolean b, final AlgorithmIdentifier algorithmIdentifier, final char[] array, final boolean b2, final byte[] array2) throws IOException {
        final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        final int n = b ? 1 : 2;
        if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            final PKCS12PBEParams instance = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
            try {
                final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(instance.getIV(), instance.getIterations().intValue());
                final PKCS12Key pkcs12Key = new PKCS12Key(array, b2);
                final Cipher cipher = this.helper.createCipher(algorithm.getId());
                cipher.init(n, pkcs12Key, pbeParameterSpec);
                return cipher.doFinal(array2);
            }
            catch (final Exception ex) {
                throw new IOException("exception decrypting data - " + ex.toString());
            }
        }
        if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
            try {
                return this.createCipher(n, array, algorithmIdentifier).doFinal(array2);
            }
            catch (final Exception ex2) {
                throw new IOException("exception decrypting data - " + ex2.toString());
            }
        }
        throw new IOException("unknown PBE algorithm: " + algorithm);
    }
    
    private Cipher createCipher(final int n, final char[] array, final AlgorithmIdentifier algorithmIdentifier) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        final PBES2Parameters instance = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
        final PBKDF2Params instance2 = PBKDF2Params.getInstance(instance.getKeyDerivationFunc().getParameters());
        final AlgorithmIdentifier instance3 = AlgorithmIdentifier.getInstance(instance.getEncryptionScheme());
        final SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(instance.getKeyDerivationFunc().getAlgorithm().getId());
        SecretKey secretKey;
        if (instance2.isDefaultPrf()) {
            secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(array, instance2.getSalt(), this.validateIterationCount(instance2.getIterationCount()), PKCS12KeyStoreSpi.keySizeProvider.getKeySize(instance3)));
        }
        else {
            secretKey = secretKeyFactory.generateSecret(new PBKDF2KeySpec(array, instance2.getSalt(), this.validateIterationCount(instance2.getIterationCount()), PKCS12KeyStoreSpi.keySizeProvider.getKeySize(instance3), instance2.getPrf()));
        }
        final Cipher instance4 = Cipher.getInstance(instance.getEncryptionScheme().getAlgorithm().getId());
        final ASN1Encodable parameters = instance.getEncryptionScheme().getParameters();
        if (parameters instanceof ASN1OctetString) {
            instance4.init(n, secretKey, new IvParameterSpec(ASN1OctetString.getInstance(parameters).getOctets()));
        }
        else {
            final GOST28147Parameters instance5 = GOST28147Parameters.getInstance(parameters);
            instance4.init(n, secretKey, new GOST28147ParameterSpec(instance5.getEncryptionParamSet(), instance5.getIV()));
        }
        return instance4;
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException {
        if (inputStream == null) {
            return;
        }
        if (array == null) {
            throw new NullPointerException("No password supplied for PKCS#12 KeyStore.");
        }
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(10);
        if (bufferedInputStream.read() != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
        }
        bufferedInputStream.reset();
        final ASN1InputStream asn1InputStream = new ASN1InputStream(bufferedInputStream);
        Pfx instance;
        try {
            instance = Pfx.getInstance(asn1InputStream.readObject());
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
        final ContentInfo authSafe = instance.getAuthSafe();
        final Vector vector = new Vector();
        boolean b = false;
        boolean b2 = false;
        if (instance.getMacData() != null) {
            final MacData macData = instance.getMacData();
            final DigestInfo mac = macData.getMac();
            this.macAlgorithm = mac.getAlgorithmId();
            final byte[] salt = macData.getSalt();
            this.itCount = this.validateIterationCount(macData.getIterationCount());
            this.saltLength = salt.length;
            final byte[] octets = ((ASN1OctetString)authSafe.getContent()).getOctets();
            try {
                final byte[] calculatePbeMac = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, array, false, octets);
                final byte[] digest = mac.getDigest();
                if (!Arrays.constantTimeAreEqual(calculatePbeMac, digest)) {
                    if (array.length > 0) {
                        throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                    }
                    if (!Arrays.constantTimeAreEqual(this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, array, true, octets), digest)) {
                        throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                    }
                    b2 = true;
                }
            }
            catch (final IOException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                throw new IOException("error constructing MAC: " + ex3.toString());
            }
        }
        this.keys = new IgnoresCaseHashtable();
        this.localIds = new Hashtable();
        if (authSafe.getContentType().equals(PKCS12KeyStoreSpi.data)) {
            final ContentInfo[] contentInfo = AuthenticatedSafe.getInstance(new ASN1InputStream(((ASN1OctetString)authSafe.getContent()).getOctets()).readObject()).getContentInfo();
            for (int i = 0; i != contentInfo.length; ++i) {
                if (contentInfo[i].getContentType().equals(PKCS12KeyStoreSpi.data)) {
                    final ASN1Sequence asn1Sequence = (ASN1Sequence)new ASN1InputStream(((ASN1OctetString)contentInfo[i].getContent()).getOctets()).readObject();
                    for (int j = 0; j != asn1Sequence.size(); ++j) {
                        final SafeBag instance2 = SafeBag.getInstance(asn1Sequence.getObjectAt(j));
                        if (instance2.getBagId().equals(PKCS12KeyStoreSpi.pkcs8ShroudedKeyBag)) {
                            final EncryptedPrivateKeyInfo instance3 = EncryptedPrivateKeyInfo.getInstance(instance2.getBagValue());
                            final PrivateKey unwrapKey = this.unwrapKey(instance3.getEncryptionAlgorithm(), instance3.getEncryptedData(), array, b2);
                            String string = null;
                            ASN1OctetString asn1OctetString = null;
                            if (instance2.getBagAttributes() != null) {
                                final Enumeration objects = instance2.getBagAttributes().getObjects();
                                while (objects.hasMoreElements()) {
                                    final ASN1Sequence asn1Sequence2 = objects.nextElement();
                                    final ASN1ObjectIdentifier asn1ObjectIdentifier = (ASN1ObjectIdentifier)asn1Sequence2.getObjectAt(0);
                                    final ASN1Set set = (ASN1Set)asn1Sequence2.getObjectAt(1);
                                    ASN1Primitive asn1Primitive = null;
                                    if (set.size() > 0) {
                                        asn1Primitive = (ASN1Primitive)set.getObjectAt(0);
                                        if (unwrapKey instanceof PKCS12BagAttributeCarrier) {
                                            final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier = (PKCS12BagAttributeCarrier)unwrapKey;
                                            final ASN1Encodable bagAttribute = pkcs12BagAttributeCarrier.getBagAttribute(asn1ObjectIdentifier);
                                            if (bagAttribute != null) {
                                                if (!bagAttribute.toASN1Primitive().equals(asn1Primitive)) {
                                                    throw new IOException("attempt to add existing attribute with different value");
                                                }
                                            }
                                            else {
                                                pkcs12BagAttributeCarrier.setBagAttribute(asn1ObjectIdentifier, asn1Primitive);
                                            }
                                        }
                                    }
                                    if (asn1ObjectIdentifier.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                                        string = ((DERBMPString)asn1Primitive).getString();
                                        this.keys.put(string, unwrapKey);
                                    }
                                    else {
                                        if (!asn1ObjectIdentifier.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) {
                                            continue;
                                        }
                                        asn1OctetString = (ASN1OctetString)asn1Primitive;
                                    }
                                }
                            }
                            if (asn1OctetString != null) {
                                final String s = new String(Hex.encode(asn1OctetString.getOctets()));
                                if (string == null) {
                                    this.keys.put(s, unwrapKey);
                                }
                                else {
                                    this.localIds.put(string, s);
                                }
                            }
                            else {
                                b = true;
                                this.keys.put("unmarked", unwrapKey);
                            }
                        }
                        else if (instance2.getBagId().equals(PKCS12KeyStoreSpi.certBag)) {
                            vector.addElement(instance2);
                        }
                        else {
                            System.out.println("extra in data " + instance2.getBagId());
                            System.out.println(ASN1Dump.dumpAsString(instance2));
                        }
                    }
                }
                else if (contentInfo[i].getContentType().equals(PKCS12KeyStoreSpi.encryptedData)) {
                    final EncryptedData instance4 = EncryptedData.getInstance(contentInfo[i].getContent());
                    final ASN1Sequence asn1Sequence3 = (ASN1Sequence)ASN1Primitive.fromByteArray(this.cryptData(false, instance4.getEncryptionAlgorithm(), array, b2, instance4.getContent().getOctets()));
                    for (int k = 0; k != asn1Sequence3.size(); ++k) {
                        final SafeBag instance5 = SafeBag.getInstance(asn1Sequence3.getObjectAt(k));
                        if (instance5.getBagId().equals(PKCS12KeyStoreSpi.certBag)) {
                            vector.addElement(instance5);
                        }
                        else if (instance5.getBagId().equals(PKCS12KeyStoreSpi.pkcs8ShroudedKeyBag)) {
                            final EncryptedPrivateKeyInfo instance6 = EncryptedPrivateKeyInfo.getInstance(instance5.getBagValue());
                            final PrivateKey unwrapKey2 = this.unwrapKey(instance6.getEncryptionAlgorithm(), instance6.getEncryptedData(), array, b2);
                            final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier2 = (PKCS12BagAttributeCarrier)unwrapKey2;
                            String string2 = null;
                            ASN1OctetString asn1OctetString2 = null;
                            final Enumeration objects2 = instance5.getBagAttributes().getObjects();
                            while (objects2.hasMoreElements()) {
                                final ASN1Sequence asn1Sequence4 = objects2.nextElement();
                                final ASN1ObjectIdentifier asn1ObjectIdentifier2 = (ASN1ObjectIdentifier)asn1Sequence4.getObjectAt(0);
                                final ASN1Set set2 = (ASN1Set)asn1Sequence4.getObjectAt(1);
                                ASN1Primitive asn1Primitive2 = null;
                                if (set2.size() > 0) {
                                    asn1Primitive2 = (ASN1Primitive)set2.getObjectAt(0);
                                    final ASN1Encodable bagAttribute2 = pkcs12BagAttributeCarrier2.getBagAttribute(asn1ObjectIdentifier2);
                                    if (bagAttribute2 != null) {
                                        if (!bagAttribute2.toASN1Primitive().equals(asn1Primitive2)) {
                                            throw new IOException("attempt to add existing attribute with different value");
                                        }
                                    }
                                    else {
                                        pkcs12BagAttributeCarrier2.setBagAttribute(asn1ObjectIdentifier2, asn1Primitive2);
                                    }
                                }
                                if (asn1ObjectIdentifier2.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                                    string2 = ((DERBMPString)asn1Primitive2).getString();
                                    this.keys.put(string2, unwrapKey2);
                                }
                                else {
                                    if (!asn1ObjectIdentifier2.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) {
                                        continue;
                                    }
                                    asn1OctetString2 = (ASN1OctetString)asn1Primitive2;
                                }
                            }
                            final String s2 = new String(Hex.encode(asn1OctetString2.getOctets()));
                            if (string2 == null) {
                                this.keys.put(s2, unwrapKey2);
                            }
                            else {
                                this.localIds.put(string2, s2);
                            }
                        }
                        else if (instance5.getBagId().equals(PKCS12KeyStoreSpi.keyBag)) {
                            final PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(instance5.getBagValue()));
                            final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier3 = (PKCS12BagAttributeCarrier)privateKey;
                            String string3 = null;
                            ASN1OctetString asn1OctetString3 = null;
                            final Enumeration objects3 = instance5.getBagAttributes().getObjects();
                            while (objects3.hasMoreElements()) {
                                final ASN1Sequence instance7 = ASN1Sequence.getInstance(objects3.nextElement());
                                final ASN1ObjectIdentifier instance8 = ASN1ObjectIdentifier.getInstance(instance7.getObjectAt(0));
                                final ASN1Set instance9 = ASN1Set.getInstance(instance7.getObjectAt(1));
                                if (instance9.size() > 0) {
                                    final ASN1Primitive asn1Primitive3 = (ASN1Primitive)instance9.getObjectAt(0);
                                    final ASN1Encodable bagAttribute3 = pkcs12BagAttributeCarrier3.getBagAttribute(instance8);
                                    if (bagAttribute3 != null) {
                                        if (!bagAttribute3.toASN1Primitive().equals(asn1Primitive3)) {
                                            throw new IOException("attempt to add existing attribute with different value");
                                        }
                                    }
                                    else {
                                        pkcs12BagAttributeCarrier3.setBagAttribute(instance8, asn1Primitive3);
                                    }
                                    if (instance8.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                                        string3 = ((DERBMPString)asn1Primitive3).getString();
                                        this.keys.put(string3, privateKey);
                                    }
                                    else {
                                        if (!instance8.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) {
                                            continue;
                                        }
                                        asn1OctetString3 = (ASN1OctetString)asn1Primitive3;
                                    }
                                }
                            }
                            final String s3 = new String(Hex.encode(asn1OctetString3.getOctets()));
                            if (string3 == null) {
                                this.keys.put(s3, privateKey);
                            }
                            else {
                                this.localIds.put(string3, s3);
                            }
                        }
                        else {
                            System.out.println("extra in encryptedData " + instance5.getBagId());
                            System.out.println(ASN1Dump.dumpAsString(instance5));
                        }
                    }
                }
                else {
                    System.out.println("extra " + contentInfo[i].getContentType().getId());
                    System.out.println("extra " + ASN1Dump.dumpAsString(contentInfo[i].getContent()));
                }
            }
        }
        this.certs = new IgnoresCaseHashtable();
        this.chainCerts = new Hashtable();
        this.keyCerts = new Hashtable();
        for (int l = 0; l != vector.size(); ++l) {
            final SafeBag safeBag = vector.elementAt(l);
            final CertBag instance10 = CertBag.getInstance(safeBag.getBagValue());
            if (!instance10.getCertId().equals(PKCS12KeyStoreSpi.x509Certificate)) {
                throw new RuntimeException("Unsupported certificate type: " + instance10.getCertId());
            }
            Certificate generateCertificate;
            try {
                generateCertificate = this.certFact.generateCertificate(new ByteArrayInputStream(((ASN1OctetString)instance10.getCertValue()).getOctets()));
            }
            catch (final Exception ex4) {
                throw new RuntimeException(ex4.toString());
            }
            ASN1OctetString asn1OctetString4 = null;
            String string4 = null;
            if (safeBag.getBagAttributes() != null) {
                final Enumeration objects4 = safeBag.getBagAttributes().getObjects();
                while (objects4.hasMoreElements()) {
                    final ASN1Sequence instance11 = ASN1Sequence.getInstance(objects4.nextElement());
                    final ASN1ObjectIdentifier instance12 = ASN1ObjectIdentifier.getInstance(instance11.getObjectAt(0));
                    final ASN1Set instance13 = ASN1Set.getInstance(instance11.getObjectAt(1));
                    if (instance13.size() > 0) {
                        final ASN1Primitive asn1Primitive4 = (ASN1Primitive)instance13.getObjectAt(0);
                        if (generateCertificate instanceof PKCS12BagAttributeCarrier) {
                            final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier4 = (PKCS12BagAttributeCarrier)generateCertificate;
                            final ASN1Encodable bagAttribute4 = pkcs12BagAttributeCarrier4.getBagAttribute(instance12);
                            if (bagAttribute4 != null) {
                                if (!bagAttribute4.toASN1Primitive().equals(asn1Primitive4)) {
                                    throw new IOException("attempt to add existing attribute with different value");
                                }
                            }
                            else {
                                pkcs12BagAttributeCarrier4.setBagAttribute(instance12, asn1Primitive4);
                            }
                        }
                        if (instance12.equals(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName)) {
                            string4 = ((DERBMPString)asn1Primitive4).getString();
                        }
                        else {
                            if (!instance12.equals(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId)) {
                                continue;
                            }
                            asn1OctetString4 = (ASN1OctetString)asn1Primitive4;
                        }
                    }
                }
            }
            this.chainCerts.put(new CertId(generateCertificate.getPublicKey()), generateCertificate);
            if (b) {
                if (this.keyCerts.isEmpty()) {
                    final String s4 = new String(Hex.encode(this.createSubjectKeyId(generateCertificate.getPublicKey()).getKeyIdentifier()));
                    this.keyCerts.put(s4, generateCertificate);
                    this.keys.put(s4, this.keys.remove("unmarked"));
                }
            }
            else {
                if (asn1OctetString4 != null) {
                    this.keyCerts.put(new String(Hex.encode(asn1OctetString4.getOctets())), generateCertificate);
                }
                if (string4 != null) {
                    this.certs.put(string4, generateCertificate);
                }
            }
        }
    }
    
    private int validateIterationCount(final BigInteger bigInteger) {
        final int intValue = bigInteger.intValue();
        if (intValue < 0) {
            throw new IllegalStateException("negative iteration count found");
        }
        final BigInteger bigInteger2 = Properties.asBigInteger("org.bouncycastle.pkcs12.max_it_count");
        if (bigInteger2 != null && bigInteger2.intValue() < intValue) {
            throw new IllegalStateException("iteration count " + intValue + " greater than " + bigInteger2.intValue());
        }
        return intValue;
    }
    
    @Override
    public void engineStore(final KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'param' arg cannot be null");
        }
        if (!(loadStoreParameter instanceof PKCS12StoreParameter) && !(loadStoreParameter instanceof JDKPKCS12StoreParameter)) {
            throw new IllegalArgumentException("No support for 'param' of type " + loadStoreParameter.getClass().getName());
        }
        PKCS12StoreParameter pkcs12StoreParameter;
        if (loadStoreParameter instanceof PKCS12StoreParameter) {
            pkcs12StoreParameter = (PKCS12StoreParameter)loadStoreParameter;
        }
        else {
            pkcs12StoreParameter = new PKCS12StoreParameter(((JDKPKCS12StoreParameter)loadStoreParameter).getOutputStream(), loadStoreParameter.getProtectionParameter(), ((JDKPKCS12StoreParameter)loadStoreParameter).isUseDEREncoding());
        }
        final KeyStore.ProtectionParameter protectionParameter = loadStoreParameter.getProtectionParameter();
        char[] password;
        if (protectionParameter == null) {
            password = null;
        }
        else {
            if (!(protectionParameter instanceof KeyStore.PasswordProtection)) {
                throw new IllegalArgumentException("No support for protection parameter of type " + ((KeyStore.PasswordProtection)protectionParameter).getClass().getName());
            }
            password = ((KeyStore.PasswordProtection)protectionParameter).getPassword();
        }
        this.doStore(pkcs12StoreParameter.getOutputStream(), password, pkcs12StoreParameter.isForDEREncoding());
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException {
        this.doStore(outputStream, array, false);
    }
    
    private void doStore(final OutputStream outputStream, final char[] array, final boolean b) throws IOException {
        if (array == null) {
            throw new NullPointerException("No password supplied for PKCS#12 KeyStore.");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration keys = this.keys.keys();
        while (keys.hasMoreElements()) {
            final byte[] array2 = new byte[20];
            this.random.nextBytes(array2);
            final String s = keys.nextElement();
            final PrivateKey privateKey = (PrivateKey)this.keys.get(s);
            final PKCS12PBEParams pkcs12PBEParams = new PKCS12PBEParams(array2, 51200);
            final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(this.keyAlgorithm, pkcs12PBEParams.toASN1Primitive()), this.wrapKey(this.keyAlgorithm.getId(), privateKey, pkcs12PBEParams, array));
            boolean b2 = false;
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            if (privateKey instanceof PKCS12BagAttributeCarrier) {
                final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier = (PKCS12BagAttributeCarrier)privateKey;
                final DERBMPString derbmpString = (DERBMPString)pkcs12BagAttributeCarrier.getBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName);
                if (derbmpString == null || !derbmpString.getString().equals(s)) {
                    pkcs12BagAttributeCarrier.setBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName, new DERBMPString(s));
                }
                if (pkcs12BagAttributeCarrier.getBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId) == null) {
                    pkcs12BagAttributeCarrier.setBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId, this.createSubjectKeyId(this.engineGetCertificate(s).getPublicKey()));
                }
                final Enumeration bagAttributeKeys = pkcs12BagAttributeCarrier.getBagAttributeKeys();
                while (bagAttributeKeys.hasMoreElements()) {
                    final ASN1ObjectIdentifier asn1ObjectIdentifier = bagAttributeKeys.nextElement();
                    final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
                    asn1EncodableVector3.add(asn1ObjectIdentifier);
                    asn1EncodableVector3.add(new DERSet(pkcs12BagAttributeCarrier.getBagAttribute(asn1ObjectIdentifier)));
                    b2 = true;
                    asn1EncodableVector2.add(new DERSequence(asn1EncodableVector3));
                }
            }
            if (!b2) {
                final ASN1EncodableVector asn1EncodableVector4 = new ASN1EncodableVector();
                final Certificate engineGetCertificate = this.engineGetCertificate(s);
                asn1EncodableVector4.add(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId);
                asn1EncodableVector4.add(new DERSet(this.createSubjectKeyId(engineGetCertificate.getPublicKey())));
                asn1EncodableVector2.add(new DERSequence(asn1EncodableVector4));
                final ASN1EncodableVector asn1EncodableVector5 = new ASN1EncodableVector();
                asn1EncodableVector5.add(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName);
                asn1EncodableVector5.add(new DERSet(new DERBMPString(s)));
                asn1EncodableVector2.add(new DERSequence(asn1EncodableVector5));
            }
            asn1EncodableVector.add(new SafeBag(PKCS12KeyStoreSpi.pkcs8ShroudedKeyBag, encryptedPrivateKeyInfo.toASN1Primitive(), new DERSet(asn1EncodableVector2)));
        }
        final BEROctetString berOctetString = new BEROctetString(new DERSequence(asn1EncodableVector).getEncoded("DER"));
        final byte[] array3 = new byte[20];
        this.random.nextBytes(array3);
        final ASN1EncodableVector asn1EncodableVector6 = new ASN1EncodableVector();
        final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(this.certAlgorithm, new PKCS12PBEParams(array3, 51200).toASN1Primitive());
        final Hashtable<Certificate, Certificate> hashtable = new Hashtable<Certificate, Certificate>();
        final Enumeration keys2 = this.keys.keys();
        while (keys2.hasMoreElements()) {
            try {
                final String s2 = keys2.nextElement();
                final Certificate engineGetCertificate2 = this.engineGetCertificate(s2);
                boolean b3 = false;
                final CertBag certBag = new CertBag(PKCS12KeyStoreSpi.x509Certificate, new DEROctetString(engineGetCertificate2.getEncoded()));
                final ASN1EncodableVector asn1EncodableVector7 = new ASN1EncodableVector();
                if (engineGetCertificate2 instanceof PKCS12BagAttributeCarrier) {
                    final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier2 = (PKCS12BagAttributeCarrier)engineGetCertificate2;
                    final DERBMPString derbmpString2 = (DERBMPString)pkcs12BagAttributeCarrier2.getBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName);
                    if (derbmpString2 == null || !derbmpString2.getString().equals(s2)) {
                        pkcs12BagAttributeCarrier2.setBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName, new DERBMPString(s2));
                    }
                    if (pkcs12BagAttributeCarrier2.getBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId) == null) {
                        pkcs12BagAttributeCarrier2.setBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId, this.createSubjectKeyId(engineGetCertificate2.getPublicKey()));
                    }
                    final Enumeration bagAttributeKeys2 = pkcs12BagAttributeCarrier2.getBagAttributeKeys();
                    while (bagAttributeKeys2.hasMoreElements()) {
                        final ASN1ObjectIdentifier asn1ObjectIdentifier2 = bagAttributeKeys2.nextElement();
                        final ASN1EncodableVector asn1EncodableVector8 = new ASN1EncodableVector();
                        asn1EncodableVector8.add(asn1ObjectIdentifier2);
                        asn1EncodableVector8.add(new DERSet(pkcs12BagAttributeCarrier2.getBagAttribute(asn1ObjectIdentifier2)));
                        asn1EncodableVector7.add(new DERSequence(asn1EncodableVector8));
                        b3 = true;
                    }
                }
                if (!b3) {
                    final ASN1EncodableVector asn1EncodableVector9 = new ASN1EncodableVector();
                    asn1EncodableVector9.add(PKCS12KeyStoreSpi.pkcs_9_at_localKeyId);
                    asn1EncodableVector9.add(new DERSet(this.createSubjectKeyId(engineGetCertificate2.getPublicKey())));
                    asn1EncodableVector7.add(new DERSequence(asn1EncodableVector9));
                    final ASN1EncodableVector asn1EncodableVector10 = new ASN1EncodableVector();
                    asn1EncodableVector10.add(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName);
                    asn1EncodableVector10.add(new DERSet(new DERBMPString(s2)));
                    asn1EncodableVector7.add(new DERSequence(asn1EncodableVector10));
                }
                asn1EncodableVector6.add(new SafeBag(PKCS12KeyStoreSpi.certBag, certBag.toASN1Primitive(), new DERSet(asn1EncodableVector7)));
                hashtable.put(engineGetCertificate2, engineGetCertificate2);
                continue;
            }
            catch (final CertificateEncodingException ex) {
                throw new IOException("Error encoding certificate: " + ex.toString());
            }
            break;
        }
        final Enumeration keys3 = this.certs.keys();
        while (keys3.hasMoreElements()) {
            try {
                final String s3 = keys3.nextElement();
                final Certificate certificate = (Certificate)this.certs.get(s3);
                boolean b4 = false;
                if (this.keys.get(s3) != null) {
                    continue;
                }
                final CertBag certBag2 = new CertBag(PKCS12KeyStoreSpi.x509Certificate, new DEROctetString(certificate.getEncoded()));
                final ASN1EncodableVector asn1EncodableVector11 = new ASN1EncodableVector();
                if (certificate instanceof PKCS12BagAttributeCarrier) {
                    final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier3 = (PKCS12BagAttributeCarrier)certificate;
                    final DERBMPString derbmpString3 = (DERBMPString)pkcs12BagAttributeCarrier3.getBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName);
                    if (derbmpString3 == null || !derbmpString3.getString().equals(s3)) {
                        pkcs12BagAttributeCarrier3.setBagAttribute(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName, new DERBMPString(s3));
                    }
                    final Enumeration bagAttributeKeys3 = pkcs12BagAttributeCarrier3.getBagAttributeKeys();
                    while (bagAttributeKeys3.hasMoreElements()) {
                        final ASN1ObjectIdentifier asn1ObjectIdentifier3 = bagAttributeKeys3.nextElement();
                        if (asn1ObjectIdentifier3.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                            continue;
                        }
                        final ASN1EncodableVector asn1EncodableVector12 = new ASN1EncodableVector();
                        asn1EncodableVector12.add(asn1ObjectIdentifier3);
                        asn1EncodableVector12.add(new DERSet(pkcs12BagAttributeCarrier3.getBagAttribute(asn1ObjectIdentifier3)));
                        asn1EncodableVector11.add(new DERSequence(asn1EncodableVector12));
                        b4 = true;
                    }
                }
                if (!b4) {
                    final ASN1EncodableVector asn1EncodableVector13 = new ASN1EncodableVector();
                    asn1EncodableVector13.add(PKCS12KeyStoreSpi.pkcs_9_at_friendlyName);
                    asn1EncodableVector13.add(new DERSet(new DERBMPString(s3)));
                    asn1EncodableVector11.add(new DERSequence(asn1EncodableVector13));
                }
                asn1EncodableVector6.add(new SafeBag(PKCS12KeyStoreSpi.certBag, certBag2.toASN1Primitive(), new DERSet(asn1EncodableVector11)));
                hashtable.put(certificate, certificate);
                continue;
            }
            catch (final CertificateEncodingException ex2) {
                throw new IOException("Error encoding certificate: " + ex2.toString());
            }
            break;
        }
        final Set usedCertificateSet = this.getUsedCertificateSet();
        final Enumeration keys4 = this.chainCerts.keys();
        while (keys4.hasMoreElements()) {
            try {
                final Certificate certificate2 = this.chainCerts.get(keys4.nextElement());
                if (!usedCertificateSet.contains(certificate2)) {
                    continue;
                }
                if (hashtable.get(certificate2) != null) {
                    continue;
                }
                final CertBag certBag3 = new CertBag(PKCS12KeyStoreSpi.x509Certificate, new DEROctetString(certificate2.getEncoded()));
                final ASN1EncodableVector asn1EncodableVector14 = new ASN1EncodableVector();
                if (certificate2 instanceof PKCS12BagAttributeCarrier) {
                    final PKCS12BagAttributeCarrier pkcs12BagAttributeCarrier4 = (PKCS12BagAttributeCarrier)certificate2;
                    final Enumeration bagAttributeKeys4 = pkcs12BagAttributeCarrier4.getBagAttributeKeys();
                    while (bagAttributeKeys4.hasMoreElements()) {
                        final ASN1ObjectIdentifier asn1ObjectIdentifier4 = bagAttributeKeys4.nextElement();
                        if (asn1ObjectIdentifier4.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                            continue;
                        }
                        final ASN1EncodableVector asn1EncodableVector15 = new ASN1EncodableVector();
                        asn1EncodableVector15.add(asn1ObjectIdentifier4);
                        asn1EncodableVector15.add(new DERSet(pkcs12BagAttributeCarrier4.getBagAttribute(asn1ObjectIdentifier4)));
                        asn1EncodableVector14.add(new DERSequence(asn1EncodableVector15));
                    }
                }
                asn1EncodableVector6.add(new SafeBag(PKCS12KeyStoreSpi.certBag, certBag3.toASN1Primitive(), new DERSet(asn1EncodableVector14)));
                continue;
            }
            catch (final CertificateEncodingException ex3) {
                throw new IOException("Error encoding certificate: " + ex3.toString());
            }
            break;
        }
        final AuthenticatedSafe authenticatedSafe = new AuthenticatedSafe(new ContentInfo[] { new ContentInfo(PKCS12KeyStoreSpi.data, berOctetString), new ContentInfo(PKCS12KeyStoreSpi.encryptedData, new EncryptedData(PKCS12KeyStoreSpi.data, algorithmIdentifier, new BEROctetString(this.cryptData(true, algorithmIdentifier, array, false, new DERSequence(asn1EncodableVector6).getEncoded("DER")))).toASN1Primitive()) });
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream derOutputStream;
        if (b) {
            derOutputStream = new DEROutputStream(byteArrayOutputStream);
        }
        else {
            derOutputStream = new BEROutputStream(byteArrayOutputStream);
        }
        derOutputStream.writeObject(authenticatedSafe);
        final ContentInfo contentInfo = new ContentInfo(PKCS12KeyStoreSpi.data, new BEROctetString(byteArrayOutputStream.toByteArray()));
        final byte[] array4 = new byte[this.saltLength];
        this.random.nextBytes(array4);
        final byte[] octets = ((ASN1OctetString)contentInfo.getContent()).getOctets();
        MacData macData;
        try {
            macData = new MacData(new DigestInfo(this.macAlgorithm, this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), array4, this.itCount, array, false, octets)), array4, this.itCount);
        }
        catch (final Exception ex4) {
            throw new IOException("error constructing MAC: " + ex4.toString());
        }
        final Pfx pfx = new Pfx(contentInfo, macData);
        DEROutputStream derOutputStream2;
        if (b) {
            derOutputStream2 = new DEROutputStream(outputStream);
        }
        else {
            derOutputStream2 = new BEROutputStream(outputStream);
        }
        derOutputStream2.writeObject(pfx);
    }
    
    private Set getUsedCertificateSet() {
        final HashSet set = new HashSet();
        final Enumeration keys = this.keys.keys();
        while (keys.hasMoreElements()) {
            final Certificate[] engineGetCertificateChain = this.engineGetCertificateChain(keys.nextElement());
            for (int i = 0; i != engineGetCertificateChain.length; ++i) {
                set.add(engineGetCertificateChain[i]);
            }
        }
        final Enumeration keys2 = this.certs.keys();
        while (keys2.hasMoreElements()) {
            set.add(this.engineGetCertificate((String)keys2.nextElement()));
        }
        return set;
    }
    
    private byte[] calculatePbeMac(final ASN1ObjectIdentifier asn1ObjectIdentifier, final byte[] array, final int n, final char[] array2, final boolean b, final byte[] array3) throws Exception {
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(array, n);
        final Mac mac = this.helper.createMac(asn1ObjectIdentifier.getId());
        mac.init(new PKCS12Key(array2, b), pbeParameterSpec);
        mac.update(array3);
        return mac.doFinal();
    }
    
    private static synchronized Provider getBouncyCastleProvider() {
        if (Security.getProvider("BC") != null) {
            return Security.getProvider("BC");
        }
        if (PKCS12KeyStoreSpi.provider == null) {
            PKCS12KeyStoreSpi.provider = new BouncyCastleProvider();
        }
        return PKCS12KeyStoreSpi.provider;
    }
    
    static {
        keySizeProvider = new DefaultSecretKeyProvider();
        PKCS12KeyStoreSpi.provider = null;
    }
    
    public static class BCPKCS12KeyStore extends PKCS12KeyStoreSpi
    {
        public BCPKCS12KeyStore() {
            super(getBouncyCastleProvider(), BCPKCS12KeyStore.pbeWithSHAAnd3_KeyTripleDES_CBC, BCPKCS12KeyStore.pbeWithSHAAnd40BitRC2_CBC);
        }
    }
    
    public static class BCPKCS12KeyStore3DES extends PKCS12KeyStoreSpi
    {
        public BCPKCS12KeyStore3DES() {
            super(getBouncyCastleProvider(), BCPKCS12KeyStore3DES.pbeWithSHAAnd3_KeyTripleDES_CBC, BCPKCS12KeyStore3DES.pbeWithSHAAnd3_KeyTripleDES_CBC);
        }
    }
    
    private class CertId
    {
        byte[] id;
        
        CertId(final PublicKey publicKey) {
            this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(publicKey).getKeyIdentifier();
        }
        
        CertId(final byte[] id) {
            this.id = id;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.id);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o instanceof CertId && Arrays.areEqual(this.id, ((CertId)o).id));
        }
    }
    
    public static class DefPKCS12KeyStore extends PKCS12KeyStoreSpi
    {
        public DefPKCS12KeyStore() {
            super(null, DefPKCS12KeyStore.pbeWithSHAAnd3_KeyTripleDES_CBC, DefPKCS12KeyStore.pbeWithSHAAnd40BitRC2_CBC);
        }
    }
    
    public static class DefPKCS12KeyStore3DES extends PKCS12KeyStoreSpi
    {
        public DefPKCS12KeyStore3DES() {
            super(null, DefPKCS12KeyStore3DES.pbeWithSHAAnd3_KeyTripleDES_CBC, DefPKCS12KeyStore3DES.pbeWithSHAAnd3_KeyTripleDES_CBC);
        }
    }
    
    private static class DefaultSecretKeyProvider
    {
        private final Map KEY_SIZES;
        
        DefaultSecretKeyProvider() {
            final HashMap hashMap = new HashMap();
            hashMap.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf(128));
            hashMap.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            hashMap.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            hashMap.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            hashMap.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            hashMap.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
            hashMap.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
            hashMap.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
            hashMap.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap((Map<?, ?>)hashMap);
        }
        
        public int getKeySize(final AlgorithmIdentifier algorithmIdentifier) {
            final Integer n = this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (n != null) {
                return n;
            }
            return -1;
        }
    }
    
    private static class IgnoresCaseHashtable
    {
        private Hashtable orig;
        private Hashtable keys;
        
        private IgnoresCaseHashtable() {
            this.orig = new Hashtable();
            this.keys = new Hashtable();
        }
        
        public void put(final String s, final Object o) {
            final String s2 = (s == null) ? null : Strings.toLowerCase(s);
            final String s3 = this.keys.get(s2);
            if (s3 != null) {
                this.orig.remove(s3);
            }
            this.keys.put(s2, s);
            this.orig.put(s, o);
        }
        
        public Enumeration keys() {
            return this.orig.keys();
        }
        
        public Object remove(final String s) {
            final String s2 = this.keys.remove((s == null) ? null : Strings.toLowerCase(s));
            if (s2 == null) {
                return null;
            }
            return this.orig.remove(s2);
        }
        
        public Object get(final String s) {
            final String s2 = this.keys.get((s == null) ? null : Strings.toLowerCase(s));
            if (s2 == null) {
                return null;
            }
            return this.orig.get(s2);
        }
        
        public Enumeration elements() {
            return this.orig.elements();
        }
    }
}
