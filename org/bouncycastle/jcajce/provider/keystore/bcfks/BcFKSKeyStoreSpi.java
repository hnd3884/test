package org.bouncycastle.jcajce.provider.keystore.bcfks;

import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.crypto.util.PBKDFConfig;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectDataSequence;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import org.bouncycastle.jcajce.BCFKSStoreParameter;
import java.security.KeyStore;
import java.security.InvalidKeyException;
import javax.crypto.Mac;
import org.bouncycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.PBEParametersGenerator;
import java.io.IOException;
import org.bouncycastle.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Enumeration;
import java.security.cert.CertificateEncodingException;
import java.security.SecureRandom;
import org.bouncycastle.util.Strings;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.KeyStoreException;
import java.text.ParseException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.asn1.bc.SecretKeyData;
import org.bouncycastle.asn1.bc.EncryptedSecretKeyData;
import java.security.UnrecoverableKeyException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.Provider;
import java.security.KeyFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.bc.EncryptedPrivateKeyData;
import java.security.Key;
import java.util.HashMap;
import java.util.Date;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PrivateKey;
import org.bouncycastle.asn1.bc.ObjectData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;
import java.security.KeyStoreSpi;

class BcFKSKeyStoreSpi extends KeyStoreSpi
{
    private static final Map<String, ASN1ObjectIdentifier> oidMap;
    private static final Map<ASN1ObjectIdentifier, String> publicAlgMap;
    private static final BigInteger CERTIFICATE;
    private static final BigInteger PRIVATE_KEY;
    private static final BigInteger SECRET_KEY;
    private static final BigInteger PROTECTED_PRIVATE_KEY;
    private static final BigInteger PROTECTED_SECRET_KEY;
    private final BouncyCastleProvider provider;
    private final Map<String, ObjectData> entries;
    private final Map<String, PrivateKey> privateKeyCache;
    private AlgorithmIdentifier hmacAlgorithm;
    private KeyDerivationFunc hmacPkbdAlgorithm;
    private Date creationDate;
    private Date lastModifiedDate;
    
    private static String getPublicKeyAlg(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = BcFKSKeyStoreSpi.publicAlgMap.get(asn1ObjectIdentifier);
        if (s != null) {
            return s;
        }
        return asn1ObjectIdentifier.getId();
    }
    
    BcFKSKeyStoreSpi(final BouncyCastleProvider provider) {
        this.entries = new HashMap<String, ObjectData>();
        this.privateKeyCache = new HashMap<String, PrivateKey>();
        this.provider = provider;
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null) {
            if (objectData.getType().equals(BcFKSKeyStoreSpi.PRIVATE_KEY) || objectData.getType().equals(BcFKSKeyStoreSpi.PROTECTED_PRIVATE_KEY)) {
                final PrivateKey privateKey = this.privateKeyCache.get(s);
                if (privateKey != null) {
                    return privateKey;
                }
                final EncryptedPrivateKeyInfo instance = EncryptedPrivateKeyInfo.getInstance(EncryptedPrivateKeyData.getInstance(objectData.getData()).getEncryptedPrivateKeyInfo());
                try {
                    final PrivateKeyInfo instance2 = PrivateKeyInfo.getInstance(this.decryptData("PRIVATE_KEY_ENCRYPTION", instance.getEncryptionAlgorithm(), array, instance.getEncryptedData()));
                    KeyFactory keyFactory;
                    if (this.provider != null) {
                        keyFactory = KeyFactory.getInstance(instance2.getPrivateKeyAlgorithm().getAlgorithm().getId(), this.provider);
                    }
                    else {
                        keyFactory = KeyFactory.getInstance(getPublicKeyAlg(instance2.getPrivateKeyAlgorithm().getAlgorithm()));
                    }
                    final PrivateKey generatePrivate = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(instance2.getEncoded()));
                    this.privateKeyCache.put(s, generatePrivate);
                    return generatePrivate;
                }
                catch (final Exception ex) {
                    throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover private key (" + s + "): " + ex.getMessage());
                }
            }
            if (objectData.getType().equals(BcFKSKeyStoreSpi.SECRET_KEY) || objectData.getType().equals(BcFKSKeyStoreSpi.PROTECTED_SECRET_KEY)) {
                final EncryptedSecretKeyData instance3 = EncryptedSecretKeyData.getInstance(objectData.getData());
                try {
                    final SecretKeyData instance4 = SecretKeyData.getInstance(this.decryptData("SECRET_KEY_ENCRYPTION", instance3.getKeyEncryptionAlgorithm(), array, instance3.getEncryptedKeyData()));
                    SecretKeyFactory secretKeyFactory;
                    if (this.provider != null) {
                        secretKeyFactory = SecretKeyFactory.getInstance(instance4.getKeyAlgorithm().getId(), this.provider);
                    }
                    else {
                        secretKeyFactory = SecretKeyFactory.getInstance(instance4.getKeyAlgorithm().getId());
                    }
                    return secretKeyFactory.generateSecret(new SecretKeySpec(instance4.getKeyBytes(), instance4.getKeyAlgorithm().getId()));
                }
                catch (final Exception ex2) {
                    throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + s + "): " + ex2.getMessage());
                }
            }
            throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + s + "): type not recognized");
        }
        return null;
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null && (objectData.getType().equals(BcFKSKeyStoreSpi.PRIVATE_KEY) || objectData.getType().equals(BcFKSKeyStoreSpi.PROTECTED_PRIVATE_KEY))) {
            final org.bouncycastle.asn1.x509.Certificate[] certificateChain = EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain();
            final X509Certificate[] array = new X509Certificate[certificateChain.length];
            for (int i = 0; i != array.length; ++i) {
                array[i] = (X509Certificate)this.decodeCertificate(certificateChain[i]);
            }
            return array;
        }
        return null;
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null) {
            if (objectData.getType().equals(BcFKSKeyStoreSpi.PRIVATE_KEY) || objectData.getType().equals(BcFKSKeyStoreSpi.PROTECTED_PRIVATE_KEY)) {
                return this.decodeCertificate(EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain()[0]);
            }
            if (objectData.getType().equals(BcFKSKeyStoreSpi.CERTIFICATE)) {
                return this.decodeCertificate(objectData.getData());
            }
        }
        return null;
    }
    
    private Certificate decodeCertificate(final Object o) {
        if (this.provider != null) {
            try {
                return CertificateFactory.getInstance("X.509", this.provider).generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(o).getEncoded()));
            }
            catch (final Exception ex) {
                return null;
            }
        }
        try {
            return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(o).getEncoded()));
        }
        catch (final Exception ex2) {
            return null;
        }
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null) {
            try {
                return objectData.getLastModifiedDate().getDate();
            }
            catch (final ParseException ex) {
                return new Date();
            }
        }
        return null;
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        final Date lastModifiedDate;
        Date creationDate = lastModifiedDate = new Date();
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null) {
            creationDate = this.extractCreationDate(objectData, creationDate);
        }
        this.privateKeyCache.remove(s);
        Label_0679: {
            if (key instanceof PrivateKey) {
                if (array2 == null) {
                    throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage.");
                }
                try {
                    final byte[] encoded = key.getEncoded();
                    final KeyDerivationFunc generatePkbdAlgorithmIdentifier = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                    final byte[] generateKey = this.generateKey(generatePkbdAlgorithmIdentifier, "PRIVATE_KEY_ENCRYPTION", (array != null) ? array : new char[0]);
                    Cipher cipher;
                    if (this.provider == null) {
                        cipher = Cipher.getInstance("AES/CCM/NoPadding");
                    }
                    else {
                        cipher = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
                    }
                    cipher.init(1, new SecretKeySpec(generateKey, "AES"));
                    this.entries.put(s, new ObjectData(BcFKSKeyStoreSpi.PRIVATE_KEY, s, creationDate, lastModifiedDate, this.createPrivateKeySequence(new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(cipher.getParameters().getEncoded())))), cipher.doFinal(encoded)), array2).getEncoded(), null));
                    break Label_0679;
                }
                catch (final Exception ex) {
                    throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + ex.toString(), ex);
                }
            }
            if (key instanceof SecretKey) {
                if (array2 != null) {
                    throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key.");
                }
                try {
                    final byte[] encoded2 = key.getEncoded();
                    final KeyDerivationFunc generatePkbdAlgorithmIdentifier2 = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                    final byte[] generateKey2 = this.generateKey(generatePkbdAlgorithmIdentifier2, "SECRET_KEY_ENCRYPTION", (array != null) ? array : new char[0]);
                    Cipher cipher2;
                    if (this.provider == null) {
                        cipher2 = Cipher.getInstance("AES/CCM/NoPadding");
                    }
                    else {
                        cipher2 = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
                    }
                    cipher2.init(1, new SecretKeySpec(generateKey2, "AES"));
                    final String upperCase = Strings.toUpperCase(key.getAlgorithm());
                    byte[] array3;
                    if (upperCase.indexOf("AES") > -1) {
                        array3 = cipher2.doFinal(new SecretKeyData(NISTObjectIdentifiers.aes, encoded2).getEncoded());
                    }
                    else {
                        final ASN1ObjectIdentifier asn1ObjectIdentifier = BcFKSKeyStoreSpi.oidMap.get(upperCase);
                        if (asn1ObjectIdentifier == null) {
                            throw new KeyStoreException("BCFKS KeyStore cannot recognize secret key (" + upperCase + ") for storage.");
                        }
                        array3 = cipher2.doFinal(new SecretKeyData(asn1ObjectIdentifier, encoded2).getEncoded());
                    }
                    this.entries.put(s, new ObjectData(BcFKSKeyStoreSpi.SECRET_KEY, s, creationDate, lastModifiedDate, new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier2, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(cipher2.getParameters().getEncoded())))), array3).getEncoded(), null));
                    break Label_0679;
                }
                catch (final Exception ex2) {
                    throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + ex2.toString(), ex2);
                }
            }
            throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
        }
        this.lastModifiedDate = lastModifiedDate;
    }
    
    private SecureRandom getDefaultSecureRandom() {
        return new SecureRandom();
    }
    
    private EncryptedPrivateKeyData createPrivateKeySequence(final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, final Certificate[] array) throws CertificateEncodingException {
        final org.bouncycastle.asn1.x509.Certificate[] array2 = new org.bouncycastle.asn1.x509.Certificate[array.length];
        for (int i = 0; i != array.length; ++i) {
            array2[i] = org.bouncycastle.asn1.x509.Certificate.getInstance(array[i].getEncoded());
        }
        return new EncryptedPrivateKeyData(encryptedPrivateKeyInfo, array2);
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        final Date lastModifiedDate;
        Date creationDate = lastModifiedDate = new Date();
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null) {
            creationDate = this.extractCreationDate(objectData, creationDate);
        }
        if (array2 != null) {
            EncryptedPrivateKeyInfo instance;
            try {
                instance = EncryptedPrivateKeyInfo.getInstance(array);
            }
            catch (final Exception ex) {
                throw new ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", ex);
            }
            try {
                this.privateKeyCache.remove(s);
                this.entries.put(s, new ObjectData(BcFKSKeyStoreSpi.PROTECTED_PRIVATE_KEY, s, creationDate, lastModifiedDate, this.createPrivateKeySequence(instance, array2).getEncoded(), null));
            }
            catch (final Exception ex2) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + ex2.toString(), ex2);
            }
        }
        else {
            try {
                this.entries.put(s, new ObjectData(BcFKSKeyStoreSpi.PROTECTED_SECRET_KEY, s, creationDate, lastModifiedDate, array, null));
            }
            catch (final Exception ex3) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + ex3.toString(), ex3);
            }
        }
        this.lastModifiedDate = lastModifiedDate;
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        final ObjectData objectData = this.entries.get(s);
        final Date lastModifiedDate;
        Date creationDate = lastModifiedDate = new Date();
        if (objectData != null) {
            if (!objectData.getType().equals(BcFKSKeyStoreSpi.CERTIFICATE)) {
                throw new KeyStoreException("BCFKS KeyStore already has a key entry with alias " + s);
            }
            creationDate = this.extractCreationDate(objectData, creationDate);
        }
        try {
            this.entries.put(s, new ObjectData(BcFKSKeyStoreSpi.CERTIFICATE, s, creationDate, lastModifiedDate, certificate.getEncoded(), null));
        }
        catch (final CertificateEncodingException ex) {
            throw new ExtKeyStoreException("BCFKS KeyStore unable to handle certificate: " + ex.getMessage(), ex);
        }
        this.lastModifiedDate = lastModifiedDate;
    }
    
    private Date extractCreationDate(final ObjectData objectData, Date date) {
        try {
            date = objectData.getCreationDate().getDate();
        }
        catch (final ParseException ex) {}
        return date;
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        if (this.entries.get(s) == null) {
            return;
        }
        this.privateKeyCache.remove(s);
        this.entries.remove(s);
        this.lastModifiedDate = new Date();
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return new Enumeration() {
            final /* synthetic */ Iterator val$it = new HashSet(BcFKSKeyStoreSpi.this.entries.keySet()).iterator();
            
            public boolean hasMoreElements() {
                return this.val$it.hasNext();
            }
            
            public Object nextElement() {
                return this.val$it.next();
            }
        };
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        if (s == null) {
            throw new NullPointerException("alias value is null");
        }
        return this.entries.containsKey(s);
    }
    
    @Override
    public int engineSize() {
        return this.entries.size();
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        final ObjectData objectData = this.entries.get(s);
        if (objectData != null) {
            final BigInteger type = objectData.getType();
            return type.equals(BcFKSKeyStoreSpi.PRIVATE_KEY) || type.equals(BcFKSKeyStoreSpi.SECRET_KEY) || type.equals(BcFKSKeyStoreSpi.PROTECTED_PRIVATE_KEY) || type.equals(BcFKSKeyStoreSpi.PROTECTED_SECRET_KEY);
        }
        return false;
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        final ObjectData objectData = this.entries.get(s);
        return objectData != null && objectData.getType().equals(BcFKSKeyStoreSpi.CERTIFICATE);
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        byte[] encoded;
        try {
            encoded = certificate.getEncoded();
        }
        catch (final CertificateEncodingException ex) {
            return null;
        }
        for (final String s : this.entries.keySet()) {
            final ObjectData objectData = this.entries.get(s);
            if (objectData.getType().equals(BcFKSKeyStoreSpi.CERTIFICATE)) {
                if (Arrays.areEqual(objectData.getData(), encoded)) {
                    return s;
                }
                continue;
            }
            else {
                if (!objectData.getType().equals(BcFKSKeyStoreSpi.PRIVATE_KEY)) {
                    if (!objectData.getType().equals(BcFKSKeyStoreSpi.PROTECTED_PRIVATE_KEY)) {
                        continue;
                    }
                }
                try {
                    if (Arrays.areEqual(EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain()[0].toASN1Primitive().getEncoded(), encoded)) {
                        return s;
                    }
                    continue;
                }
                catch (final IOException ex2) {}
            }
        }
        return null;
    }
    
    private byte[] generateKey(final KeyDerivationFunc keyDerivationFunc, final String s, final char[] array) throws IOException {
        final byte[] pkcs12PasswordToBytes = PBEParametersGenerator.PKCS12PasswordToBytes(array);
        final byte[] pkcs12PasswordToBytes2 = PBEParametersGenerator.PKCS12PasswordToBytes(s.toCharArray());
        if (MiscObjectIdentifiers.id_scrypt.equals(keyDerivationFunc.getAlgorithm())) {
            final ScryptParams instance = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            return SCrypt.generate(Arrays.concatenate(pkcs12PasswordToBytes, pkcs12PasswordToBytes2), instance.getSalt(), instance.getCostParameter().intValue(), instance.getBlockSize().intValue(), instance.getBlockSize().intValue(), instance.getKeyLength().intValue());
        }
        if (!keyDerivationFunc.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBKDF2)) {
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
        }
        final PBKDF2Params instance2 = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
        if (instance2.getPrf().getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA512)) {
            final PKCS5S2ParametersGenerator pkcs5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA512Digest());
            pkcs5S2ParametersGenerator.init(Arrays.concatenate(pkcs12PasswordToBytes, pkcs12PasswordToBytes2), instance2.getSalt(), instance2.getIterationCount().intValue());
            return ((KeyParameter)pkcs5S2ParametersGenerator.generateDerivedParameters(instance2.getKeyLength().intValue() * 8)).getKey();
        }
        if (instance2.getPrf().getAlgorithm().equals(NISTObjectIdentifiers.id_hmacWithSHA3_512)) {
            final PKCS5S2ParametersGenerator pkcs5S2ParametersGenerator2 = new PKCS5S2ParametersGenerator(new SHA3Digest(512));
            pkcs5S2ParametersGenerator2.init(Arrays.concatenate(pkcs12PasswordToBytes, pkcs12PasswordToBytes2), instance2.getSalt(), instance2.getIterationCount().intValue());
            return ((KeyParameter)pkcs5S2ParametersGenerator2.generateDerivedParameters(instance2.getKeyLength().intValue() * 8)).getKey();
        }
        throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD PRF: " + instance2.getPrf().getAlgorithm());
    }
    
    private void verifyMac(final byte[] array, final PbkdMacIntegrityCheck pbkdMacIntegrityCheck, final char[] array2) throws NoSuchAlgorithmException, IOException {
        if (!Arrays.constantTimeAreEqual(this.calculateMac(array, pbkdMacIntegrityCheck.getMacAlgorithm(), pbkdMacIntegrityCheck.getPbkdAlgorithm(), array2), pbkdMacIntegrityCheck.getMac())) {
            throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed.");
        }
    }
    
    private byte[] calculateMac(final byte[] array, final AlgorithmIdentifier algorithmIdentifier, final KeyDerivationFunc keyDerivationFunc, final char[] array2) throws NoSuchAlgorithmException, IOException {
        final String id = algorithmIdentifier.getAlgorithm().getId();
        Mac mac;
        if (this.provider != null) {
            mac = Mac.getInstance(id, this.provider);
        }
        else {
            mac = Mac.getInstance(id);
        }
        try {
            mac.init(new SecretKeySpec(this.generateKey(keyDerivationFunc, "INTEGRITY_CHECK", (array2 != null) ? array2 : new char[0]), id));
        }
        catch (final InvalidKeyException ex) {
            throw new IOException("Cannot set up MAC calculation: " + ex.getMessage());
        }
        return mac.doFinal(array);
    }
    
    @Override
    public void engineStore(final KeyStore.LoadStoreParameter loadStoreParameter) throws CertificateException, NoSuchAlgorithmException, IOException {
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'parameter' arg cannot be null");
        }
        if (!(loadStoreParameter instanceof BCFKSStoreParameter)) {
            throw new IllegalArgumentException("no support for 'parameter' of type " + loadStoreParameter.getClass().getName());
        }
        final BCFKSStoreParameter bcfksStoreParameter = (BCFKSStoreParameter)loadStoreParameter;
        final KeyStore.ProtectionParameter protectionParameter = bcfksStoreParameter.getProtectionParameter();
        char[] array;
        if (protectionParameter == null) {
            array = null;
        }
        else if (protectionParameter instanceof KeyStore.PasswordProtection) {
            array = ((KeyStore.PasswordProtection)protectionParameter).getPassword();
        }
        else {
            if (!(protectionParameter instanceof KeyStore.CallbackHandlerProtection)) {
                throw new IllegalArgumentException("no support for protection parameter of type " + ((KeyStore.CallbackHandlerProtection)protectionParameter).getClass().getName());
            }
            final CallbackHandler callbackHandler = ((KeyStore.CallbackHandlerProtection)protectionParameter).getCallbackHandler();
            final PasswordCallback passwordCallback = new PasswordCallback("password: ", false);
            try {
                callbackHandler.handle(new Callback[] { passwordCallback });
                array = passwordCallback.getPassword();
            }
            catch (final UnsupportedCallbackException ex) {
                throw new IllegalArgumentException("PasswordCallback not recognised: " + ex.getMessage(), ex);
            }
        }
        if (bcfksStoreParameter.getStorePBKDFConfig().getAlgorithm().equals(MiscObjectIdentifiers.id_scrypt)) {
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(bcfksStoreParameter.getStorePBKDFConfig(), 64);
        }
        else {
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(bcfksStoreParameter.getStorePBKDFConfig(), 64);
        }
        this.engineStore(bcfksStoreParameter.getOutputStream(), array);
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        final ObjectData[] array2 = this.entries.values().toArray(new ObjectData[this.entries.size()]);
        final KeyDerivationFunc generatePkbdAlgorithmIdentifier = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, 32);
        final byte[] generateKey = this.generateKey(generatePkbdAlgorithmIdentifier, "STORE_ENCRYPTION", (array != null) ? array : new char[0]);
        final ObjectStoreData objectStoreData = new ObjectStoreData(this.hmacAlgorithm, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(array2), null);
        EncryptedObjectStoreData encryptedObjectStoreData;
        try {
            Cipher cipher;
            if (this.provider == null) {
                cipher = Cipher.getInstance("AES/CCM/NoPadding");
            }
            else {
                cipher = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
            }
            cipher.init(1, new SecretKeySpec(generateKey, "AES"));
            encryptedObjectStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(generatePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(cipher.getParameters().getEncoded())))), cipher.doFinal(objectStoreData.getEncoded()));
        }
        catch (final NoSuchPaddingException ex) {
            throw new NoSuchAlgorithmException(ex.toString());
        }
        catch (final BadPaddingException ex2) {
            throw new IOException(ex2.toString());
        }
        catch (final IllegalBlockSizeException ex3) {
            throw new IOException(ex3.toString());
        }
        catch (final InvalidKeyException ex4) {
            throw new IOException(ex4.toString());
        }
        if (MiscObjectIdentifiers.id_scrypt.equals(this.hmacPkbdAlgorithm.getAlgorithm())) {
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, ScryptParams.getInstance(this.hmacPkbdAlgorithm.getParameters()).getKeyLength().intValue());
        }
        else {
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters()).getKeyLength().intValue());
        }
        outputStream.write(new ObjectStore(encryptedObjectStoreData, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, this.calculateMac(encryptedObjectStoreData.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, array)))).getEncoded());
        outputStream.flush();
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.entries.clear();
        this.privateKeyCache.clear();
        final Date date = null;
        this.creationDate = date;
        this.lastModifiedDate = date;
        this.hmacAlgorithm = null;
        if (inputStream == null) {
            final Date date2 = new Date();
            this.creationDate = date2;
            this.lastModifiedDate = date2;
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 64);
            return;
        }
        final ASN1InputStream asn1InputStream = new ASN1InputStream(inputStream);
        ObjectStore instance;
        try {
            instance = ObjectStore.getInstance(asn1InputStream.readObject());
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
        final ObjectStoreIntegrityCheck integrityCheck = instance.getIntegrityCheck();
        if (integrityCheck.getType() != 0) {
            throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
        }
        final PbkdMacIntegrityCheck instance2 = PbkdMacIntegrityCheck.getInstance(integrityCheck.getIntegrityCheck());
        this.hmacAlgorithm = instance2.getMacAlgorithm();
        this.hmacPkbdAlgorithm = instance2.getPbkdAlgorithm();
        this.verifyMac(instance.getStoreData().toASN1Primitive().getEncoded(), instance2, array);
        final ASN1Encodable storeData = instance.getStoreData();
        ObjectStoreData objectStoreData;
        if (storeData instanceof EncryptedObjectStoreData) {
            final EncryptedObjectStoreData encryptedObjectStoreData = (EncryptedObjectStoreData)storeData;
            objectStoreData = ObjectStoreData.getInstance(this.decryptData("STORE_ENCRYPTION", encryptedObjectStoreData.getEncryptionAlgorithm(), array, encryptedObjectStoreData.getEncryptedContent().getOctets()));
        }
        else {
            objectStoreData = ObjectStoreData.getInstance(storeData);
        }
        try {
            this.creationDate = objectStoreData.getCreationDate().getDate();
            this.lastModifiedDate = objectStoreData.getLastModifiedDate().getDate();
        }
        catch (final ParseException ex2) {
            throw new IOException("BCFKS KeyStore unable to parse store data information.");
        }
        if (!objectStoreData.getIntegrityAlgorithm().equals(this.hmacAlgorithm)) {
            throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm.");
        }
        final Iterator<ASN1Encodable> iterator = objectStoreData.getObjectDataSequence().iterator();
        while (iterator.hasNext()) {
            final ObjectData instance3 = ObjectData.getInstance(iterator.next());
            this.entries.put(instance3.getIdentifier(), instance3);
        }
    }
    
    private byte[] decryptData(final String s, final AlgorithmIdentifier algorithmIdentifier, final char[] array, final byte[] array2) throws IOException {
        if (!algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2)) {
            throw new IOException("BCFKS KeyStore cannot recognize protection algorithm.");
        }
        final PBES2Parameters instance = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
        final EncryptionScheme encryptionScheme = instance.getEncryptionScheme();
        if (!encryptionScheme.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_CCM)) {
            throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm.");
        }
        try {
            final CCMParameters instance2 = CCMParameters.getInstance(encryptionScheme.getParameters());
            Cipher cipher;
            AlgorithmParameters algorithmParameters;
            if (this.provider == null) {
                cipher = Cipher.getInstance("AES/CCM/NoPadding");
                algorithmParameters = AlgorithmParameters.getInstance("CCM");
            }
            else {
                cipher = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
                algorithmParameters = AlgorithmParameters.getInstance("CCM", this.provider);
            }
            algorithmParameters.init(instance2.getEncoded());
            cipher.init(2, new SecretKeySpec(this.generateKey(instance.getKeyDerivationFunc(), s, (array != null) ? array : new char[0]), "AES"), algorithmParameters);
            return cipher.doFinal(array2);
        }
        catch (final Exception ex) {
            throw new IOException(ex.toString());
        }
    }
    
    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(final PBKDFConfig pbkdfConfig, final int n) {
        if (MiscObjectIdentifiers.id_scrypt.equals(pbkdfConfig.getAlgorithm())) {
            final ScryptConfig scryptConfig = (ScryptConfig)pbkdfConfig;
            final byte[] array = new byte[scryptConfig.getSaltLength()];
            this.getDefaultSecureRandom().nextBytes(array);
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, new ScryptParams(array, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter(), n));
        }
        final PBKDF2Config pbkdf2Config = (PBKDF2Config)pbkdfConfig;
        final byte[] array2 = new byte[pbkdf2Config.getSaltLength()];
        this.getDefaultSecureRandom().nextBytes(array2);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(array2, pbkdf2Config.getIterationCount(), n, pbkdf2Config.getPRF()));
    }
    
    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(final KeyDerivationFunc keyDerivationFunc, final int n) {
        if (MiscObjectIdentifiers.id_scrypt.equals(keyDerivationFunc.getAlgorithm())) {
            final ScryptParams instance = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            final byte[] array = new byte[instance.getSalt().length];
            this.getDefaultSecureRandom().nextBytes(array);
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, new ScryptParams(array, instance.getCostParameter(), instance.getBlockSize(), instance.getParallelizationParameter(), BigInteger.valueOf(n)));
        }
        final PBKDF2Params instance2 = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
        final byte[] array2 = new byte[instance2.getSalt().length];
        this.getDefaultSecureRandom().nextBytes(array2);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(array2, instance2.getIterationCount().intValue(), n, instance2.getPrf()));
    }
    
    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final int n) {
        final byte[] array = new byte[64];
        this.getDefaultSecureRandom().nextBytes(array);
        if (PKCSObjectIdentifiers.id_PBKDF2.equals(asn1ObjectIdentifier)) {
            return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(array, 51200, n, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE)));
        }
        throw new IllegalStateException("unknown derivation algorithm: " + asn1ObjectIdentifier);
    }
    
    static {
        oidMap = new HashMap<String, ASN1ObjectIdentifier>();
        publicAlgMap = new HashMap<ASN1ObjectIdentifier, String>();
        BcFKSKeyStoreSpi.oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
        BcFKSKeyStoreSpi.oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
        BcFKSKeyStoreSpi.oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
        BcFKSKeyStoreSpi.oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
        BcFKSKeyStoreSpi.oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
        BcFKSKeyStoreSpi.oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
        BcFKSKeyStoreSpi.oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
        BcFKSKeyStoreSpi.oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
        BcFKSKeyStoreSpi.publicAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        BcFKSKeyStoreSpi.publicAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
        BcFKSKeyStoreSpi.publicAlgMap.put(OIWObjectIdentifiers.elGamalAlgorithm, "DH");
        BcFKSKeyStoreSpi.publicAlgMap.put(PKCSObjectIdentifiers.dhKeyAgreement, "DH");
        BcFKSKeyStoreSpi.publicAlgMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
        CERTIFICATE = BigInteger.valueOf(0L);
        PRIVATE_KEY = BigInteger.valueOf(1L);
        SECRET_KEY = BigInteger.valueOf(2L);
        PROTECTED_PRIVATE_KEY = BigInteger.valueOf(3L);
        PROTECTED_SECRET_KEY = BigInteger.valueOf(4L);
    }
    
    public static class Def extends BcFKSKeyStoreSpi
    {
        public Def() {
            super(null);
        }
    }
    
    private static class ExtKeyStoreException extends KeyStoreException
    {
        private final Throwable cause;
        
        ExtKeyStoreException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
    
    public static class Std extends BcFKSKeyStoreSpi
    {
        public Std() {
            super(new BouncyCastleProvider());
        }
    }
}
