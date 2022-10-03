package sun.security.pkcs12;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.InvalidAlgorithmParameterException;
import java.io.InputStream;
import java.util.Iterator;
import javax.crypto.Mac;
import java.security.MessageDigest;
import java.security.PKCS12Attribute;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import sun.security.pkcs.ContentInfo;
import java.io.OutputStream;
import java.util.Enumeration;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.spec.AlgorithmParameterSpec;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import sun.security.util.DerOutputStream;
import java.security.KeyStoreException;
import javax.security.auth.DestroyFailedException;
import java.util.Set;
import java.security.KeyStore;
import java.util.Date;
import java.util.Arrays;
import java.security.cert.Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.AlgorithmParameters;
import sun.security.util.DerInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import sun.security.x509.AlgorithmId;
import javax.crypto.Cipher;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import java.security.UnrecoverableKeyException;
import java.util.Locale;
import java.security.Key;
import java.util.Collections;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.security.SecureRandom;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Debug;
import java.security.KeyStoreSpi;

public final class PKCS12KeyStore extends KeyStoreSpi
{
    public static final int VERSION_3 = 3;
    private static final String[] KEY_PROTECTION_ALGORITHM;
    private static final int MAX_ITERATION_COUNT = 5000000;
    private static final int PBE_ITERATION_COUNT = 50000;
    private static final int MAC_ITERATION_COUNT = 100000;
    private static final int SALT_LEN = 20;
    private static final String[] CORE_ATTRIBUTES;
    private static final Debug debug;
    private static final int[] keyBag;
    private static final int[] certBag;
    private static final int[] secretBag;
    private static final int[] pkcs9Name;
    private static final int[] pkcs9KeyId;
    private static final int[] pkcs9certType;
    private static final int[] pbeWithSHAAnd40BitRC2CBC;
    private static final int[] pbeWithSHAAnd3KeyTripleDESCBC;
    private static final int[] pbes2;
    private static final int[] TrustedKeyUsage;
    private static final int[] AnyExtendedKeyUsage;
    private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
    private static ObjectIdentifier CertBag_OID;
    private static ObjectIdentifier SecretBag_OID;
    private static ObjectIdentifier PKCS9FriendlyName_OID;
    private static ObjectIdentifier PKCS9LocalKeyId_OID;
    private static ObjectIdentifier PKCS9CertType_OID;
    private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
    private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
    private static ObjectIdentifier pbes2_OID;
    private static ObjectIdentifier TrustedKeyUsage_OID;
    private static ObjectIdentifier[] AnyUsage;
    private int counter;
    private int privateKeyCount;
    private int secretKeyCount;
    private int certificateCount;
    private SecureRandom random;
    private Map<String, Entry> entries;
    private ArrayList<KeyEntry> keyList;
    private LinkedHashMap<X500Principal, X509Certificate> certsMap;
    private ArrayList<CertEntry> certEntries;
    
    public PKCS12KeyStore() {
        this.counter = 0;
        this.privateKeyCount = 0;
        this.secretKeyCount = 0;
        this.certificateCount = 0;
        this.entries = Collections.synchronizedMap(new LinkedHashMap<String, Entry>());
        this.keyList = new ArrayList<KeyEntry>();
        this.certsMap = new LinkedHashMap<X500Principal, X509Certificate>();
        this.certEntries = new ArrayList<CertEntry>();
    }
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (entry == null || !(entry instanceof KeyEntry)) {
            return null;
        }
        byte[] array2;
        if (entry instanceof PrivateKeyEntry) {
            array2 = ((PrivateKeyEntry)entry).protectedPrivKey;
        }
        else {
            if (!(entry instanceof SecretKeyEntry)) {
                throw new UnrecoverableKeyException("Error locating key");
            }
            array2 = ((SecretKeyEntry)entry).protectedSecretKey;
        }
        AlgorithmParameters algParameters;
        try {
            final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(array2);
            encryptedPrivateKeyInfo.getEncryptedData();
            final DerInputStream derInputStream = new DerValue(encryptedPrivateKeyInfo.getAlgorithm().encode()).toDerInputStream();
            algParameters = this.parseAlgParameters(derInputStream.getOID(), derInputStream);
        }
        catch (final IOException ex) {
            final UnrecoverableKeyException ex2 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + ex);
            ex2.initCause(ex);
            throw ex2;
        }
        Key key;
        try {
            if (algParameters != null) {
                PBEParameterSpec pbeParameterSpec;
                try {
                    pbeParameterSpec = algParameters.getParameterSpec(PBEParameterSpec.class);
                }
                catch (final InvalidParameterSpecException ex3) {
                    throw new IOException("Invalid PBE algorithm parameters");
                }
                final int iterationCount = pbeParameterSpec.getIterationCount();
                if (iterationCount > 5000000) {
                    throw new IOException("PBE iteration count too large");
                }
            }
            else {
                final int iterationCount = 0;
            }
            key = RetryWithZero.run(array5 -> {
                this.getPBEKey(array5);
                Cipher.getInstance(mapPBEParamsToAlgorithm(objectIdentifier, algorithmParameters));
                final Cipher cipher;
                final SecretKey secretKey;
                cipher.init(2, secretKey, algorithmParameters);
                cipher.doFinal(array4);
                final byte[] array6;
                new DerValue(array6).toDerInputStream();
                final DerInputStream derInputStream2;
                derInputStream2.getInteger();
                derInputStream2.getSequence(2);
                final DerValue[] array7;
                if (array7.length < 1 || array7.length > 2) {
                    throw new IOException("Invalid length for AlgorithmIdentifier");
                }
                else {
                    new AlgorithmId(array7[0].getOID()).getName();
                    if (entry2 instanceof PrivateKeyEntry) {
                        final String s3;
                        KeyFactory.getInstance(s3).generatePrivate(new PKCS8EncodedKeySpec(array6));
                        if (PKCS12KeyStore.debug != null) {
                            PKCS12KeyStore.debug.println("Retrieved a protected private key at alias '" + s2 + "' (" + new AlgorithmId(objectIdentifier).getName() + " iterations: " + n + ")");
                        }
                        return;
                    }
                    else {
                        final String s3;
                        final SecretKeySpec secretKeySpec = new SecretKeySpec(derInputStream2.getOctetString(), s3);
                        SecretKey generateSecret;
                        if (s3.startsWith("PBE")) {
                            SecretKeyFactory.getInstance(s3);
                            final SecretKeyFactory secretKeyFactory;
                            generateSecret = secretKeyFactory.generateSecret(secretKeyFactory.getKeySpec(secretKeySpec, PBEKeySpec.class));
                        }
                        else {
                            generateSecret = secretKeySpec;
                        }
                        if (PKCS12KeyStore.debug != null) {
                            PKCS12KeyStore.debug.println("Retrieved a protected secret key at alias '" + s2 + "' (" + new AlgorithmId(objectIdentifier).getName() + " iterations: " + n + ")");
                        }
                        return (PrivateKey)generateSecret;
                    }
                }
            }, array);
        }
        catch (final Exception ex4) {
            final UnrecoverableKeyException ex5 = new UnrecoverableKeyException("Get Key failed: " + ex4.getMessage());
            ex5.initCause(ex4);
            throw ex5;
        }
        return key;
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (entry == null || !(entry instanceof PrivateKeyEntry)) {
            return null;
        }
        if (((PrivateKeyEntry)entry).chain == null) {
            return null;
        }
        if (PKCS12KeyStore.debug != null) {
            PKCS12KeyStore.debug.println("Retrieved a " + ((PrivateKeyEntry)entry).chain.length + "-certificate chain at alias '" + s + "'");
        }
        return ((PrivateKeyEntry)entry).chain.clone();
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (entry == null) {
            return null;
        }
        if (entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null) {
            if (PKCS12KeyStore.debug != null) {
                if (Arrays.equals(PKCS12KeyStore.AnyUsage, ((CertEntry)entry).trustedKeyUsage)) {
                    PKCS12KeyStore.debug.println("Retrieved a certificate at alias '" + s + "' (trusted for any purpose)");
                }
                else {
                    PKCS12KeyStore.debug.println("Retrieved a certificate at alias '" + s + "' (trusted for limited purposes)");
                }
            }
            return ((CertEntry)entry).cert;
        }
        if (!(entry instanceof PrivateKeyEntry)) {
            return null;
        }
        if (((PrivateKeyEntry)entry).chain == null) {
            return null;
        }
        if (PKCS12KeyStore.debug != null) {
            PKCS12KeyStore.debug.println("Retrieved a certificate at alias '" + s + "'");
        }
        return ((PrivateKeyEntry)entry).chain[0];
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (entry != null) {
            return new Date(entry.date.getTime());
        }
        return null;
    }
    
    @Override
    public synchronized void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        final KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(array);
        try {
            this.setKeyEntry(s, key, passwordProtection, array2, null);
        }
        finally {
            try {
                passwordProtection.destroy();
            }
            catch (final DestroyFailedException ex) {}
        }
    }
    
    private void setKeyEntry(final String s, final Key key, final KeyStore.PasswordProtection passwordProtection, final Certificate[] array, final Set<KeyStore.Entry.Attribute> set) throws KeyStoreException {
        try {
            PrivateKeyEntry privateKeyEntry2;
            if (key instanceof PrivateKey) {
                final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry();
                privateKeyEntry.date = new Date();
                if (!key.getFormat().equals("PKCS#8") && !key.getFormat().equals("PKCS8")) {
                    throw new KeyStoreException("Private key is not encodedas PKCS#8");
                }
                if (PKCS12KeyStore.debug != null) {
                    PKCS12KeyStore.debug.println("Setting a protected private key at alias '" + s + "'");
                }
                privateKeyEntry.protectedPrivKey = this.encryptPrivateKey(key.getEncoded(), passwordProtection);
                if (array != null) {
                    if (array.length > 1 && !this.validateChain(array)) {
                        throw new KeyStoreException("Certificate chain is not valid");
                    }
                    privateKeyEntry.chain = array.clone();
                    this.certificateCount += array.length;
                    if (PKCS12KeyStore.debug != null) {
                        PKCS12KeyStore.debug.println("Setting a " + array.length + "-certificate chain at alias '" + s + "'");
                    }
                }
                ++this.privateKeyCount;
                privateKeyEntry2 = privateKeyEntry;
            }
            else {
                if (!(key instanceof SecretKey)) {
                    throw new KeyStoreException("Unsupported Key type");
                }
                final SecretKeyEntry secretKeyEntry = new SecretKeyEntry();
                secretKeyEntry.date = new Date();
                final DerOutputStream derOutputStream = new DerOutputStream();
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.putInteger(0);
                AlgorithmId.get(key.getAlgorithm()).encode(derOutputStream2);
                derOutputStream2.putOctetString(key.getEncoded());
                derOutputStream.write((byte)48, derOutputStream2);
                secretKeyEntry.protectedSecretKey = this.encryptPrivateKey(derOutputStream.toByteArray(), passwordProtection);
                if (PKCS12KeyStore.debug != null) {
                    PKCS12KeyStore.debug.println("Setting a protected secret key at alias '" + s + "'");
                }
                ++this.secretKeyCount;
                privateKeyEntry2 = (PrivateKeyEntry)secretKeyEntry;
            }
            privateKeyEntry2.attributes = new HashSet<KeyStore.Entry.Attribute>();
            if (set != null) {
                privateKeyEntry2.attributes.addAll(set);
            }
            privateKeyEntry2.keyId = ("Time " + privateKeyEntry2.date.getTime()).getBytes("UTF8");
            privateKeyEntry2.alias = s.toLowerCase(Locale.ENGLISH);
            this.entries.put(s.toLowerCase(Locale.ENGLISH), privateKeyEntry2);
        }
        catch (final Exception ex) {
            throw new KeyStoreException("Key protection  algorithm not found: " + ex, ex);
        }
    }
    
    @Override
    public synchronized void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        try {
            new EncryptedPrivateKeyInfo(array);
        }
        catch (final IOException ex) {
            throw new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + ex, ex);
        }
        final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry();
        privateKeyEntry.date = new Date();
        if (PKCS12KeyStore.debug != null) {
            PKCS12KeyStore.debug.println("Setting a protected private key at alias '" + s + "'");
        }
        try {
            privateKeyEntry.keyId = ("Time " + privateKeyEntry.date.getTime()).getBytes("UTF8");
        }
        catch (final UnsupportedEncodingException ex2) {}
        privateKeyEntry.alias = s.toLowerCase(Locale.ENGLISH);
        privateKeyEntry.protectedPrivKey = array.clone();
        if (array2 != null) {
            if (array2.length > 1 && !this.validateChain(array2)) {
                throw new KeyStoreException("Certificate chain is not valid");
            }
            privateKeyEntry.chain = array2.clone();
            this.certificateCount += array2.length;
            if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("Setting a " + privateKeyEntry.chain.length + "-certificate chain at alias '" + s + "'");
            }
        }
        ++this.privateKeyCount;
        this.entries.put(s.toLowerCase(Locale.ENGLISH), privateKeyEntry);
    }
    
    private byte[] getSalt() {
        final byte[] array = new byte[20];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(array);
        return array;
    }
    
    private AlgorithmParameters getPBEAlgorithmParameters(final String s) throws IOException {
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(this.getSalt(), 50000);
        AlgorithmParameters instance;
        try {
            instance = AlgorithmParameters.getInstance(s);
            instance.init(pbeParameterSpec);
        }
        catch (final Exception ex) {
            throw new IOException("getPBEAlgorithmParameters failed: " + ex.getMessage(), ex);
        }
        return instance;
    }
    
    private AlgorithmParameters parseAlgParameters(final ObjectIdentifier objectIdentifier, final DerInputStream derInputStream) throws IOException {
        AlgorithmParameters algorithmParameters = null;
        try {
            DerValue derValue;
            if (derInputStream.available() == 0) {
                derValue = null;
            }
            else {
                derValue = derInputStream.getDerValue();
                if (derValue.tag == 5) {
                    derValue = null;
                }
            }
            if (derValue != null) {
                if (objectIdentifier.equals((Object)PKCS12KeyStore.pbes2_OID)) {
                    algorithmParameters = AlgorithmParameters.getInstance("PBES2");
                }
                else {
                    algorithmParameters = AlgorithmParameters.getInstance("PBE");
                }
                algorithmParameters.init(derValue.toByteArray());
            }
        }
        catch (final Exception ex) {
            throw new IOException("parseAlgParameters failed: " + ex.getMessage(), ex);
        }
        return algorithmParameters;
    }
    
    private SecretKey getPBEKey(final char[] array) throws IOException {
        SecretKey generateSecret;
        try {
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(array);
            generateSecret = SecretKeyFactory.getInstance("PBE").generateSecret(pbeKeySpec);
            pbeKeySpec.clearPassword();
        }
        catch (final Exception ex) {
            throw new IOException("getSecretKey failed: " + ex.getMessage(), ex);
        }
        return generateSecret;
    }
    
    private byte[] encryptPrivateKey(final byte[] array, final KeyStore.PasswordProtection passwordProtection) throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        byte[] encoded;
        try {
            String protectionAlgorithm = passwordProtection.getProtectionAlgorithm();
            AlgorithmParameters algorithmParameters;
            if (protectionAlgorithm != null) {
                final AlgorithmParameterSpec protectionParameters = passwordProtection.getProtectionParameters();
                if (protectionParameters != null) {
                    algorithmParameters = AlgorithmParameters.getInstance(protectionAlgorithm);
                    algorithmParameters.init(protectionParameters);
                }
                else {
                    algorithmParameters = this.getPBEAlgorithmParameters(protectionAlgorithm);
                }
            }
            else {
                protectionAlgorithm = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        String s = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[0]);
                        if (s == null) {
                            s = Security.getProperty(PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[1]);
                        }
                        return s;
                    }
                });
                if (protectionAlgorithm == null || protectionAlgorithm.isEmpty()) {
                    protectionAlgorithm = "PBEWithSHA1AndDESede";
                }
                algorithmParameters = this.getPBEAlgorithmParameters(protectionAlgorithm);
            }
            final ObjectIdentifier mapPBEAlgorithmToOID = mapPBEAlgorithmToOID(protectionAlgorithm);
            if (mapPBEAlgorithmToOID == null) {
                throw new IOException("PBE algorithm '" + protectionAlgorithm + " 'is not supported for key entry protection");
            }
            final SecretKey pbeKey = this.getPBEKey(passwordProtection.getPassword());
            final Cipher instance = Cipher.getInstance(protectionAlgorithm);
            instance.init(1, pbeKey, algorithmParameters);
            final byte[] doFinal = instance.doFinal(array);
            final AlgorithmId algorithmId = new AlgorithmId(mapPBEAlgorithmToOID, instance.getParameters());
            if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("  (Cipher algorithm: " + instance.getAlgorithm() + ")");
            }
            encoded = new EncryptedPrivateKeyInfo(algorithmId, doFinal).getEncoded();
        }
        catch (final Exception ex) {
            final UnrecoverableKeyException ex2 = new UnrecoverableKeyException("Encrypt Private Key failed: " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        return encoded;
    }
    
    private static ObjectIdentifier mapPBEAlgorithmToOID(final String s) throws NoSuchAlgorithmException {
        if (s.toLowerCase(Locale.ENGLISH).startsWith("pbewithhmacsha")) {
            return PKCS12KeyStore.pbes2_OID;
        }
        return AlgorithmId.get(s).getOID();
    }
    
    private static String mapPBEParamsToAlgorithm(final ObjectIdentifier objectIdentifier, final AlgorithmParameters algorithmParameters) throws NoSuchAlgorithmException {
        if (objectIdentifier.equals((Object)PKCS12KeyStore.pbes2_OID) && algorithmParameters != null) {
            return algorithmParameters.toString();
        }
        return objectIdentifier.toString();
    }
    
    @Override
    public synchronized void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        this.setCertEntry(s, certificate, null);
    }
    
    private void setCertEntry(final String s, final Certificate certificate, final Set<KeyStore.Entry.Attribute> set) throws KeyStoreException {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (entry != null && entry instanceof KeyEntry) {
            throw new KeyStoreException("Cannot overwrite own certificate");
        }
        final CertEntry certEntry = new CertEntry((X509Certificate)certificate, null, s, PKCS12KeyStore.AnyUsage, set);
        ++this.certificateCount;
        this.entries.put(s.toLowerCase(Locale.ENGLISH), certEntry);
        if (PKCS12KeyStore.debug != null) {
            PKCS12KeyStore.debug.println("Setting a trusted certificate at alias '" + s + "'");
        }
    }
    
    @Override
    public synchronized void engineDeleteEntry(final String s) throws KeyStoreException {
        if (PKCS12KeyStore.debug != null) {
            PKCS12KeyStore.debug.println("Removing entry at alias '" + s + "'");
        }
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (entry instanceof PrivateKeyEntry) {
            final PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry)entry;
            if (privateKeyEntry.chain != null) {
                this.certificateCount -= privateKeyEntry.chain.length;
            }
            --this.privateKeyCount;
        }
        else if (entry instanceof CertEntry) {
            --this.certificateCount;
        }
        else if (entry instanceof SecretKeyEntry) {
            --this.secretKeyCount;
        }
        this.entries.remove(s.toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(this.entries.keySet());
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        return this.entries.containsKey(s.toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public int engineSize() {
        return this.entries.size();
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        return entry != null && entry instanceof KeyEntry;
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        return entry != null && entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null;
    }
    
    @Override
    public boolean engineEntryInstanceOf(final String s, final Class<? extends KeyStore.Entry> clazz) {
        if (clazz == KeyStore.TrustedCertificateEntry.class) {
            return this.engineIsCertificateEntry(s);
        }
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (clazz == KeyStore.PrivateKeyEntry.class) {
            return entry != null && entry instanceof PrivateKeyEntry;
        }
        return clazz == KeyStore.SecretKeyEntry.class && entry != null && entry instanceof SecretKeyEntry;
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        Certificate cert = null;
        final Enumeration<String> engineAliases = this.engineAliases();
        while (engineAliases.hasMoreElements()) {
            final String s = engineAliases.nextElement();
            final Entry entry = this.entries.get(s);
            if (entry instanceof PrivateKeyEntry) {
                if (((PrivateKeyEntry)entry).chain != null) {
                    cert = ((PrivateKeyEntry)entry).chain[0];
                }
            }
            else {
                if (!(entry instanceof CertEntry) || ((CertEntry)entry).trustedKeyUsage == null) {
                    continue;
                }
                cert = ((CertEntry)entry).cert;
            }
            if (cert != null && cert.equals(certificate)) {
                return s;
            }
        }
        return null;
    }
    
    @Override
    public synchronized void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (array == null) {
            throw new IllegalArgumentException("password can't be null");
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(3);
        derOutputStream.write(derOutputStream2.toByteArray());
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        if (this.privateKeyCount > 0 || this.secretKeyCount > 0) {
            if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("Storing " + (this.privateKeyCount + this.secretKeyCount) + " protected key(s) in a PKCS#7 data");
            }
            new ContentInfo(this.createSafeContent()).encode(derOutputStream4);
        }
        if (this.certificateCount > 0) {
            if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("Storing " + this.certificateCount + " certificate(s) in a PKCS#7 encryptedData");
            }
            new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue(this.createEncryptedData(array))).encode(derOutputStream4);
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream4);
        final byte[] byteArray = derOutputStream5.toByteArray();
        new ContentInfo(byteArray).encode(derOutputStream3);
        derOutputStream.write(derOutputStream3.toByteArray());
        derOutputStream.write(this.calculateMac(array, byteArray));
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write((byte)48, derOutputStream);
        outputStream.write(derOutputStream6.toByteArray());
        outputStream.flush();
    }
    
    @Override
    public KeyStore.Entry engineGetEntry(final String s, final KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        if (!this.engineContainsAlias(s)) {
            return null;
        }
        final Entry entry = this.entries.get(s.toLowerCase(Locale.ENGLISH));
        if (protectionParameter == null) {
            if (!this.engineIsCertificateEntry(s)) {
                throw new UnrecoverableKeyException("requested entry requires a password");
            }
            if (entry instanceof CertEntry && ((CertEntry)entry).trustedKeyUsage != null) {
                if (PKCS12KeyStore.debug != null) {
                    PKCS12KeyStore.debug.println("Retrieved a trusted certificate at alias '" + s + "'");
                }
                return new KeyStore.TrustedCertificateEntry(((CertEntry)entry).cert, this.getAttributes(entry));
            }
        }
        if (protectionParameter instanceof KeyStore.PasswordProtection) {
            if (this.engineIsCertificateEntry(s)) {
                throw new UnsupportedOperationException("trusted certificate entries are not password-protected");
            }
            if (this.engineIsKeyEntry(s)) {
                final Key engineGetKey = this.engineGetKey(s, ((KeyStore.PasswordProtection)protectionParameter).getPassword());
                if (engineGetKey instanceof PrivateKey) {
                    return new KeyStore.PrivateKeyEntry((PrivateKey)engineGetKey, this.engineGetCertificateChain(s), this.getAttributes(entry));
                }
                if (engineGetKey instanceof SecretKey) {
                    return new KeyStore.SecretKeyEntry((SecretKey)engineGetKey, this.getAttributes(entry));
                }
            }
            else if (!this.engineIsKeyEntry(s)) {
                throw new UnsupportedOperationException("untrusted certificate entries are not password-protected");
            }
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public synchronized void engineSetEntry(final String s, final KeyStore.Entry entry, final KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        if (protectionParameter != null && !(protectionParameter instanceof KeyStore.PasswordProtection)) {
            throw new KeyStoreException("unsupported protection parameter");
        }
        KeyStore.PasswordProtection passwordProtection = null;
        if (protectionParameter != null) {
            passwordProtection = (KeyStore.PasswordProtection)protectionParameter;
        }
        if (entry instanceof KeyStore.TrustedCertificateEntry) {
            if (protectionParameter != null && passwordProtection.getPassword() != null) {
                throw new KeyStoreException("trusted certificate entries are not password-protected");
            }
            final KeyStore.TrustedCertificateEntry trustedCertificateEntry = (KeyStore.TrustedCertificateEntry)entry;
            this.setCertEntry(s, trustedCertificateEntry.getTrustedCertificate(), trustedCertificateEntry.getAttributes());
        }
        else if (entry instanceof KeyStore.PrivateKeyEntry) {
            if (passwordProtection == null || passwordProtection.getPassword() == null) {
                throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
            }
            final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)entry;
            this.setKeyEntry(s, privateKeyEntry.getPrivateKey(), passwordProtection, privateKeyEntry.getCertificateChain(), privateKeyEntry.getAttributes());
        }
        else {
            if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                throw new KeyStoreException("unsupported entry type: " + entry.getClass().getName());
            }
            if (passwordProtection == null || passwordProtection.getPassword() == null) {
                throw new KeyStoreException("non-null password required to create SecretKeyEntry");
            }
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)entry;
            this.setKeyEntry(s, secretKeyEntry.getSecretKey(), passwordProtection, null, secretKeyEntry.getAttributes());
        }
    }
    
    private Set<KeyStore.Entry.Attribute> getAttributes(final Entry entry) {
        if (entry.attributes == null) {
            entry.attributes = new HashSet<KeyStore.Entry.Attribute>();
        }
        entry.attributes.add(new PKCS12Attribute(PKCS12KeyStore.PKCS9FriendlyName_OID.toString(), entry.alias));
        final byte[] keyId = entry.keyId;
        if (keyId != null) {
            entry.attributes.add(new PKCS12Attribute(PKCS12KeyStore.PKCS9LocalKeyId_OID.toString(), Debug.toString(keyId)));
        }
        if (entry instanceof CertEntry) {
            final ObjectIdentifier[] trustedKeyUsage = ((CertEntry)entry).trustedKeyUsage;
            if (trustedKeyUsage != null) {
                if (trustedKeyUsage.length == 1) {
                    entry.attributes.add(new PKCS12Attribute(PKCS12KeyStore.TrustedKeyUsage_OID.toString(), trustedKeyUsage[0].toString()));
                }
                else {
                    entry.attributes.add(new PKCS12Attribute(PKCS12KeyStore.TrustedKeyUsage_OID.toString(), Arrays.toString(trustedKeyUsage)));
                }
            }
        }
        return entry.attributes;
    }
    
    private byte[] generateHash(final byte[] array) throws IOException {
        byte[] digest;
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA1");
            instance.update(array);
            digest = instance.digest();
        }
        catch (final Exception ex) {
            throw new IOException("generateHash failed: " + ex, ex);
        }
        return digest;
    }
    
    private byte[] calculateMac(final char[] array, final byte[] array2) throws IOException {
        final String s = "SHA1";
        byte[] byteArray;
        try {
            final byte[] salt = this.getSalt();
            final Mac instance = Mac.getInstance("HmacPBESHA1");
            instance.init(this.getPBEKey(array), new PBEParameterSpec(salt, 100000));
            instance.update(array2);
            final MacData macData = new MacData(s, instance.doFinal(), salt, 100000);
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.write(macData.getEncoded());
            byteArray = derOutputStream.toByteArray();
        }
        catch (final Exception ex) {
            throw new IOException("calculateMac failed: " + ex, ex);
        }
        return byteArray;
    }
    
    private boolean validateChain(final Certificate[] array) {
        for (int i = 0; i < array.length - 1; ++i) {
            if (!((X509Certificate)array[i]).getIssuerX500Principal().equals(((X509Certificate)array[i + 1]).getSubjectX500Principal())) {
                return false;
            }
        }
        return new HashSet(Arrays.asList(array)).size() == array.length;
    }
    
    private byte[] getBagAttributes(final String s, final byte[] array, final Set<KeyStore.Entry.Attribute> set) throws IOException {
        return this.getBagAttributes(s, array, null, set);
    }
    
    private byte[] getBagAttributes(final String s, final byte[] array, final ObjectIdentifier[] array2, final Set<KeyStore.Entry.Attribute> set) throws IOException {
        byte[] byteArray = null;
        byte[] byteArray2 = null;
        byte[] byteArray3 = null;
        if (s == null && array == null && byteArray3 == null) {
            return null;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (s != null) {
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.putOID(PKCS12KeyStore.PKCS9FriendlyName_OID);
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream3.putBMPString(s);
            derOutputStream2.write((byte)49, derOutputStream3);
            derOutputStream4.write((byte)48, derOutputStream2);
            byteArray2 = derOutputStream4.toByteArray();
        }
        if (array != null) {
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.putOID(PKCS12KeyStore.PKCS9LocalKeyId_OID);
            final DerOutputStream derOutputStream6 = new DerOutputStream();
            final DerOutputStream derOutputStream7 = new DerOutputStream();
            derOutputStream6.putOctetString(array);
            derOutputStream5.write((byte)49, derOutputStream6);
            derOutputStream7.write((byte)48, derOutputStream5);
            byteArray = derOutputStream7.toByteArray();
        }
        if (array2 != null) {
            final DerOutputStream derOutputStream8 = new DerOutputStream();
            derOutputStream8.putOID(PKCS12KeyStore.TrustedKeyUsage_OID);
            final DerOutputStream derOutputStream9 = new DerOutputStream();
            final DerOutputStream derOutputStream10 = new DerOutputStream();
            for (int length = array2.length, i = 0; i < length; ++i) {
                derOutputStream9.putOID(array2[i]);
            }
            derOutputStream8.write((byte)49, derOutputStream9);
            derOutputStream10.write((byte)48, derOutputStream8);
            byteArray3 = derOutputStream10.toByteArray();
        }
        final DerOutputStream derOutputStream11 = new DerOutputStream();
        if (byteArray2 != null) {
            derOutputStream11.write(byteArray2);
        }
        if (byteArray != null) {
            derOutputStream11.write(byteArray);
        }
        if (byteArray3 != null) {
            derOutputStream11.write(byteArray3);
        }
        if (set != null) {
            for (final KeyStore.Entry.Attribute attribute : set) {
                final String name = attribute.getName();
                if (!PKCS12KeyStore.CORE_ATTRIBUTES[0].equals(name) && !PKCS12KeyStore.CORE_ATTRIBUTES[1].equals(name)) {
                    if (PKCS12KeyStore.CORE_ATTRIBUTES[2].equals(name)) {
                        continue;
                    }
                    derOutputStream11.write(((PKCS12Attribute)attribute).getEncoded());
                }
            }
        }
        derOutputStream.write((byte)49, derOutputStream11);
        return derOutputStream.toByteArray();
    }
    
    private byte[] createEncryptedData(final char[] array) throws CertificateException, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final Enumeration<String> engineAliases = this.engineAliases();
        while (engineAliases.hasMoreElements()) {
            final Entry entry = this.entries.get(engineAliases.nextElement());
            Certificate[] chain;
            if (entry instanceof PrivateKeyEntry) {
                final PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry)entry;
                if (privateKeyEntry.chain != null) {
                    chain = privateKeyEntry.chain;
                }
                else {
                    chain = new Certificate[0];
                }
            }
            else if (entry instanceof CertEntry) {
                chain = new Certificate[] { ((CertEntry)entry).cert };
            }
            else {
                chain = new Certificate[0];
            }
            for (int i = 0; i < chain.length; ++i) {
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.putOID(PKCS12KeyStore.CertBag_OID);
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.putOID(PKCS12KeyStore.PKCS9CertType_OID);
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                final X509Certificate x509Certificate = (X509Certificate)chain[i];
                derOutputStream4.putOctetString(x509Certificate.getEncoded());
                derOutputStream3.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream4);
                final DerOutputStream derOutputStream5 = new DerOutputStream();
                derOutputStream5.write((byte)48, derOutputStream3);
                final byte[] byteArray = derOutputStream5.toByteArray();
                final DerOutputStream derOutputStream6 = new DerOutputStream();
                derOutputStream6.write(byteArray);
                derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream6);
                byte[] array2;
                if (i == 0) {
                    if (entry instanceof KeyEntry) {
                        final KeyEntry keyEntry = (KeyEntry)entry;
                        array2 = this.getBagAttributes(keyEntry.alias, keyEntry.keyId, keyEntry.attributes);
                    }
                    else {
                        final CertEntry certEntry = (CertEntry)entry;
                        array2 = this.getBagAttributes(certEntry.alias, certEntry.keyId, certEntry.trustedKeyUsage, certEntry.attributes);
                    }
                }
                else {
                    array2 = this.getBagAttributes(x509Certificate.getSubjectX500Principal().getName(), null, entry.attributes);
                }
                if (array2 != null) {
                    derOutputStream2.write(array2);
                }
                derOutputStream.write((byte)48, derOutputStream2);
            }
        }
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write((byte)48, derOutputStream);
        final byte[] encryptContent = this.encryptContent(derOutputStream7.toByteArray(), array);
        final DerOutputStream derOutputStream8 = new DerOutputStream();
        final DerOutputStream derOutputStream9 = new DerOutputStream();
        derOutputStream8.putInteger(0);
        derOutputStream8.write(encryptContent);
        derOutputStream9.write((byte)48, derOutputStream8);
        return derOutputStream9.toByteArray();
    }
    
    private byte[] createSafeContent() throws CertificateException, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final Enumeration<String> engineAliases = this.engineAliases();
        while (engineAliases.hasMoreElements()) {
            final String s = engineAliases.nextElement();
            final Entry entry = this.entries.get(s);
            if (entry != null) {
                if (!(entry instanceof KeyEntry)) {
                    continue;
                }
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                final KeyEntry keyEntry = (KeyEntry)entry;
                if (keyEntry instanceof PrivateKeyEntry) {
                    derOutputStream2.putOID(PKCS12KeyStore.PKCS8ShroudedKeyBag_OID);
                    final byte[] protectedPrivKey = ((PrivateKeyEntry)keyEntry).protectedPrivKey;
                    EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
                    try {
                        encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(protectedPrivKey);
                    }
                    catch (final IOException ex) {
                        throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + ex.getMessage());
                    }
                    final DerOutputStream derOutputStream3 = new DerOutputStream();
                    derOutputStream3.write(encryptedPrivateKeyInfo.getEncoded());
                    derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream3);
                }
                else {
                    if (!(keyEntry instanceof SecretKeyEntry)) {
                        continue;
                    }
                    derOutputStream2.putOID(PKCS12KeyStore.SecretBag_OID);
                    final DerOutputStream derOutputStream4 = new DerOutputStream();
                    derOutputStream4.putOID(PKCS12KeyStore.PKCS8ShroudedKeyBag_OID);
                    final DerOutputStream derOutputStream5 = new DerOutputStream();
                    derOutputStream5.putOctetString(((SecretKeyEntry)keyEntry).protectedSecretKey);
                    derOutputStream4.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream5);
                    final DerOutputStream derOutputStream6 = new DerOutputStream();
                    derOutputStream6.write((byte)48, derOutputStream4);
                    final byte[] byteArray = derOutputStream6.toByteArray();
                    final DerOutputStream derOutputStream7 = new DerOutputStream();
                    derOutputStream7.write(byteArray);
                    derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream7);
                }
                derOutputStream2.write(this.getBagAttributes(s, entry.keyId, entry.attributes));
                derOutputStream.write((byte)48, derOutputStream2);
            }
        }
        final DerOutputStream derOutputStream8 = new DerOutputStream();
        derOutputStream8.write((byte)48, derOutputStream);
        return derOutputStream8.toByteArray();
    }
    
    private byte[] encryptContent(final byte[] array, final char[] array2) throws IOException {
        final AlgorithmParameters pbeAlgorithmParameters = this.getPBEAlgorithmParameters("PBEWithSHA1AndRC2_40");
        final DerOutputStream derOutputStream = new DerOutputStream();
        new AlgorithmId(PKCS12KeyStore.pbeWithSHAAnd40BitRC2CBC_OID, pbeAlgorithmParameters).encode(derOutputStream);
        final byte[] byteArray = derOutputStream.toByteArray();
        byte[] doFinal;
        try {
            final SecretKey pbeKey = this.getPBEKey(array2);
            final Cipher instance = Cipher.getInstance("PBEWithSHA1AndRC2_40");
            instance.init(1, pbeKey, pbeAlgorithmParameters);
            doFinal = instance.doFinal(array);
            if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("  (Cipher algorithm: " + instance.getAlgorithm() + ")");
            }
        }
        catch (final Exception ex) {
            throw new IOException("Failed to encrypt safe contents entry: " + ex, ex);
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOID(ContentInfo.DATA_OID);
        derOutputStream2.write(byteArray);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString(doFinal);
        derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)0), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream2);
        return derOutputStream4.toByteArray();
    }
    
    @Override
    public synchronized void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (inputStream == null) {
            return;
        }
        this.counter = 0;
        final DerInputStream derInputStream = new DerValue(inputStream).toDerInputStream();
        if (derInputStream.getInteger() != 3) {
            throw new IOException("PKCS12 keystore not in version 3 format");
        }
        this.entries.clear();
        final ContentInfo contentInfo = new ContentInfo(derInputStream);
        if (contentInfo.getContentType().equals((Object)ContentInfo.DATA_OID)) {
            final DerValue[] sequence = new DerInputStream(contentInfo.getData()).getSequence(2);
            final int length = sequence.length;
            this.privateKeyCount = 0;
            this.secretKeyCount = 0;
            this.certificateCount = 0;
            for (int i = 0; i < length; ++i) {
                final ContentInfo contentInfo2 = new ContentInfo(new DerInputStream(sequence[i].toByteArray()));
                final ObjectIdentifier contentType = contentInfo2.getContentType();
                if (contentType.equals((Object)ContentInfo.DATA_OID)) {
                    if (PKCS12KeyStore.debug != null) {
                        PKCS12KeyStore.debug.println("Loading PKCS#7 data");
                    }
                    this.loadSafeContents(new DerInputStream(contentInfo2.getData()));
                }
                else {
                    if (!contentType.equals((Object)ContentInfo.ENCRYPTED_DATA_OID)) {
                        throw new IOException("public key protected PKCS12 not supported");
                    }
                    if (array == null) {
                        if (PKCS12KeyStore.debug != null) {
                            PKCS12KeyStore.debug.println("Warning: skipping PKCS#7 encryptedData - no password was supplied");
                        }
                    }
                    else {
                        final DerInputStream derInputStream2 = contentInfo2.getContent().toDerInputStream();
                        derInputStream2.getInteger();
                        final DerValue[] sequence2 = derInputStream2.getSequence(3);
                        if (sequence2.length != 3) {
                            throw new IOException("Invalid length for EncryptedContentInfo");
                        }
                        sequence2[0].getOID();
                        sequence2[1].toByteArray();
                        if (!sequence2[2].isContextSpecific((byte)0)) {
                            throw new IOException("unsupported encrypted content type " + sequence2[2].tag);
                        }
                        byte b = 4;
                        if (sequence2[2].isConstructed()) {
                            b |= 0x20;
                        }
                        sequence2[2].resetTag(b);
                        sequence2[2].getOctetString();
                        final DerInputStream derInputStream3 = sequence2[1].toDerInputStream();
                        final ObjectIdentifier oid = derInputStream3.getOID();
                        final AlgorithmParameters algParameters = this.parseAlgParameters(oid, derInputStream3);
                        int iterationCount = 0;
                        if (algParameters != null) {
                            PBEParameterSpec pbeParameterSpec;
                            try {
                                pbeParameterSpec = algParameters.getParameterSpec(PBEParameterSpec.class);
                            }
                            catch (final InvalidParameterSpecException ex) {
                                throw new IOException("Invalid PBE algorithm parameters");
                            }
                            iterationCount = pbeParameterSpec.getIterationCount();
                            if (iterationCount > 5000000) {
                                throw new IOException("PBE iteration count too large");
                            }
                        }
                        if (PKCS12KeyStore.debug != null) {
                            PKCS12KeyStore.debug.println("Loading PKCS#7 encryptedData (" + new AlgorithmId(oid).getName() + " iterations: " + iterationCount + ")");
                        }
                        try {
                            RetryWithZero.run(array5 -> {
                                this.getPBEKey(array5);
                                Cipher.getInstance(mapPBEParamsToAlgorithm(objectIdentifier, algorithmParameters));
                                final Cipher cipher;
                                final SecretKey secretKey;
                                cipher.init(2, secretKey, algorithmParameters);
                                this.loadSafeContents(new DerInputStream(cipher.doFinal(array4)));
                                return null;
                            }, array);
                        }
                        catch (final Exception ex2) {
                            throw new IOException("keystore password was incorrect", new UnrecoverableKeyException("failed to decrypt safe contents entry: " + ex2));
                        }
                    }
                }
            }
            if (array != null && derInputStream.available() > 0) {
                final int n2 = new MacData(derInputStream).getIterations();
                try {
                    if (n2 > 5000000) {
                        throw new InvalidAlgorithmParameterException("MAC iteration count too large: " + n2);
                    }
                    RetryWithZero.run(array8 -> {
                        final MacData macData2;
                        Mac.getInstance("HmacPBE" + macData2.getDigestAlgName().toUpperCase(Locale.ENGLISH).replace("-", ""));
                        final int n2;
                        final Object o = new PBEParameterSpec(macData2.getSalt(), n2);
                        mac.init(this.getPBEKey(array8), pbeParameterSpec2);
                        mac.update(array7);
                        mac.doFinal();
                        if (PKCS12KeyStore.debug != null) {
                            PKCS12KeyStore.debug.println("Checking keystore integrity (" + mac.getAlgorithm() + " iterations: " + n + ")");
                        }
                        final byte[] array9;
                        if (!MessageDigest.isEqual(macData.getDigest(), array9)) {
                            throw new UnrecoverableKeyException("Failed PKCS12 integrity checking");
                        }
                        else {
                            return (Void)null;
                        }
                    }, array);
                }
                catch (final Exception ex3) {
                    throw new IOException("Integrity check failed: " + ex3, ex3);
                }
            }
            final PrivateKeyEntry[] array2 = this.keyList.toArray(new PrivateKeyEntry[this.keyList.size()]);
            for (int j = 0; j < array2.length; ++j) {
                final PrivateKeyEntry privateKeyEntry = array2[j];
                if (privateKeyEntry.keyId != null) {
                    final ArrayList list = new ArrayList();
                    X500Principal issuerX500Principal;
                Label_1055:
                    for (X509Certificate matchedCertificate = this.findMatchedCertificate(privateKeyEntry); matchedCertificate != null; matchedCertificate = this.certsMap.get(issuerX500Principal)) {
                        if (!list.isEmpty()) {
                            final Iterator iterator = list.iterator();
                            while (iterator.hasNext()) {
                                if (matchedCertificate.equals(iterator.next())) {
                                    if (PKCS12KeyStore.debug != null) {
                                        PKCS12KeyStore.debug.println("Loop detected in certificate chain. Skip adding repeated cert to chain. Subject: " + matchedCertificate.getSubjectX500Principal().toString());
                                        break Label_1055;
                                    }
                                    break Label_1055;
                                }
                            }
                        }
                        list.add(matchedCertificate);
                        issuerX500Principal = matchedCertificate.getIssuerX500Principal();
                        if (issuerX500Principal.equals(matchedCertificate.getSubjectX500Principal())) {
                            break;
                        }
                    }
                    if (list.size() > 0) {
                        privateKeyEntry.chain = (Certificate[])list.toArray(new Certificate[list.size()]);
                    }
                }
            }
            if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("PKCS12KeyStore load: private key count: " + this.privateKeyCount + ". secret key count: " + this.secretKeyCount + ". certificate count: " + this.certificateCount);
            }
            this.certEntries.clear();
            this.certsMap.clear();
            this.keyList.clear();
            return;
        }
        throw new IOException("public key protected PKCS12 not supported");
    }
    
    private X509Certificate findMatchedCertificate(final PrivateKeyEntry privateKeyEntry) {
        CertEntry certEntry = null;
        CertEntry certEntry2 = null;
        for (final CertEntry certEntry3 : this.certEntries) {
            if (Arrays.equals(privateKeyEntry.keyId, certEntry3.keyId)) {
                certEntry = certEntry3;
                if (privateKeyEntry.alias.equalsIgnoreCase(certEntry3.alias)) {
                    return certEntry3.cert;
                }
                continue;
            }
            else {
                if (!privateKeyEntry.alias.equalsIgnoreCase(certEntry3.alias)) {
                    continue;
                }
                certEntry2 = certEntry3;
            }
        }
        if (certEntry != null) {
            return certEntry.cert;
        }
        if (certEntry2 != null) {
            return certEntry2.cert;
        }
        return null;
    }
    
    private void loadSafeContents(final DerInputStream derInputStream) throws IOException, NoSuchAlgorithmException, CertificateException {
        final DerValue[] sequence = derInputStream.getSequence(2);
        for (int length = sequence.length, i = 0; i < length; ++i) {
            Object o = null;
            final DerInputStream derInputStream2 = sequence[i].toDerInputStream();
            final ObjectIdentifier oid = derInputStream2.getOID();
            final DerValue derValue = derInputStream2.getDerValue();
            if (!derValue.isContextSpecific((byte)0)) {
                throw new IOException("unsupported PKCS12 bag value type " + derValue.tag);
            }
            final DerValue derValue2 = derValue.data.getDerValue();
            if (oid.equals((Object)PKCS12KeyStore.PKCS8ShroudedKeyBag_OID)) {
                final PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry();
                privateKeyEntry.protectedPrivKey = derValue2.toByteArray();
                o = privateKeyEntry;
                ++this.privateKeyCount;
            }
            else if (oid.equals((Object)PKCS12KeyStore.CertBag_OID)) {
                final DerValue[] sequence2 = new DerInputStream(derValue2.toByteArray()).getSequence(2);
                if (sequence2.length != 2) {
                    throw new IOException("Invalid length for CertBag");
                }
                sequence2[0].getOID();
                if (!sequence2[1].isContextSpecific((byte)0)) {
                    throw new IOException("unsupported PKCS12 cert value type " + sequence2[1].tag);
                }
                o = CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(sequence2[1].data.getDerValue().getOctetString()));
                ++this.certificateCount;
            }
            else if (oid.equals((Object)PKCS12KeyStore.SecretBag_OID)) {
                final DerValue[] sequence3 = new DerInputStream(derValue2.toByteArray()).getSequence(2);
                if (sequence3.length != 2) {
                    throw new IOException("Invalid length for SecretBag");
                }
                sequence3[0].getOID();
                if (!sequence3[1].isContextSpecific((byte)0)) {
                    throw new IOException("unsupported PKCS12 secret value type " + sequence3[1].tag);
                }
                final DerValue derValue3 = sequence3[1].data.getDerValue();
                final SecretKeyEntry secretKeyEntry = new SecretKeyEntry();
                secretKeyEntry.protectedSecretKey = derValue3.getOctetString();
                o = secretKeyEntry;
                ++this.secretKeyCount;
            }
            else if (PKCS12KeyStore.debug != null) {
                PKCS12KeyStore.debug.println("Unsupported PKCS12 bag type: " + oid);
            }
            DerValue[] set;
            try {
                set = derInputStream2.getSet(3);
            }
            catch (final IOException ex) {
                set = null;
            }
            String alias = null;
            byte[] keyId = null;
            ObjectIdentifier[] array = null;
            final HashSet set2 = new HashSet();
            if (set != null) {
                for (int j = 0; j < set.length; ++j) {
                    final byte[] byteArray = set[j].toByteArray();
                    final DerValue[] sequence4 = new DerInputStream(byteArray).getSequence(2);
                    if (sequence4.length != 2) {
                        throw new IOException("Invalid length for Attribute");
                    }
                    final ObjectIdentifier oid2 = sequence4[0].getOID();
                    final DerInputStream derInputStream3 = new DerInputStream(sequence4[1].toByteArray());
                    DerValue[] set3;
                    try {
                        set3 = derInputStream3.getSet(1);
                    }
                    catch (final IOException ex2) {
                        throw new IOException("Attribute " + oid2 + " should have a value " + ex2.getMessage());
                    }
                    if (oid2.equals((Object)PKCS12KeyStore.PKCS9FriendlyName_OID)) {
                        alias = set3[0].getBMPString();
                    }
                    else if (oid2.equals((Object)PKCS12KeyStore.PKCS9LocalKeyId_OID)) {
                        keyId = set3[0].getOctetString();
                    }
                    else if (oid2.equals((Object)PKCS12KeyStore.TrustedKeyUsage_OID)) {
                        array = new ObjectIdentifier[set3.length];
                        for (int k = 0; k < set3.length; ++k) {
                            array[k] = set3[k].getOID();
                        }
                    }
                    else {
                        set2.add(new PKCS12Attribute(byteArray));
                    }
                }
            }
            if (o instanceof KeyEntry) {
                final KeyEntry keyEntry = (KeyEntry)o;
                if (o instanceof PrivateKeyEntry && keyId == null) {
                    if (this.privateKeyCount != 1) {
                        continue;
                    }
                    keyId = "01".getBytes("UTF8");
                }
                keyEntry.keyId = keyId;
                final String s = new String(keyId, "UTF8");
                Date date = null;
                if (s.startsWith("Time ")) {
                    try {
                        date = new Date(Long.parseLong(s.substring(5)));
                    }
                    catch (final Exception ex3) {
                        date = null;
                    }
                }
                if (date == null) {
                    date = new Date();
                }
                keyEntry.date = date;
                if (o instanceof PrivateKeyEntry) {
                    this.keyList.add(keyEntry);
                }
                if (keyEntry.attributes == null) {
                    keyEntry.attributes = new HashSet<KeyStore.Entry.Attribute>();
                }
                keyEntry.attributes.addAll(set2);
                if (alias == null) {
                    alias = this.getUnfriendlyName();
                }
                keyEntry.alias = alias;
                this.entries.put(alias.toLowerCase(Locale.ENGLISH), keyEntry);
            }
            else if (o instanceof X509Certificate) {
                final X509Certificate x509Certificate = (X509Certificate)o;
                if (keyId == null && this.privateKeyCount == 1 && i == 0) {
                    keyId = "01".getBytes("UTF8");
                }
                if (array != null) {
                    if (alias == null) {
                        alias = this.getUnfriendlyName();
                    }
                    this.entries.put(alias.toLowerCase(Locale.ENGLISH), new CertEntry(x509Certificate, keyId, alias, array, set2));
                }
                else {
                    this.certEntries.add(new CertEntry(x509Certificate, keyId, alias));
                }
                final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
                if (subjectX500Principal != null && !this.certsMap.containsKey(subjectX500Principal)) {
                    this.certsMap.put(subjectX500Principal, x509Certificate);
                }
            }
        }
    }
    
    private String getUnfriendlyName() {
        ++this.counter;
        return String.valueOf(this.counter);
    }
    
    static {
        KEY_PROTECTION_ALGORITHM = new String[] { "keystore.pkcs12.keyProtectionAlgorithm", "keystore.PKCS12.keyProtectionAlgorithm" };
        CORE_ATTRIBUTES = new String[] { "1.2.840.113549.1.9.20", "1.2.840.113549.1.9.21", "2.16.840.1.113894.746875.1.1" };
        debug = Debug.getInstance("pkcs12");
        keyBag = new int[] { 1, 2, 840, 113549, 1, 12, 10, 1, 2 };
        certBag = new int[] { 1, 2, 840, 113549, 1, 12, 10, 1, 3 };
        secretBag = new int[] { 1, 2, 840, 113549, 1, 12, 10, 1, 5 };
        pkcs9Name = new int[] { 1, 2, 840, 113549, 1, 9, 20 };
        pkcs9KeyId = new int[] { 1, 2, 840, 113549, 1, 9, 21 };
        pkcs9certType = new int[] { 1, 2, 840, 113549, 1, 9, 22, 1 };
        pbeWithSHAAnd40BitRC2CBC = new int[] { 1, 2, 840, 113549, 1, 12, 1, 6 };
        pbeWithSHAAnd3KeyTripleDESCBC = new int[] { 1, 2, 840, 113549, 1, 12, 1, 3 };
        pbes2 = new int[] { 1, 2, 840, 113549, 1, 5, 13 };
        TrustedKeyUsage = new int[] { 2, 16, 840, 1, 113894, 746875, 1, 1 };
        AnyExtendedKeyUsage = new int[] { 2, 5, 29, 37, 0 };
        try {
            PKCS12KeyStore.PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(PKCS12KeyStore.keyBag);
            PKCS12KeyStore.CertBag_OID = new ObjectIdentifier(PKCS12KeyStore.certBag);
            PKCS12KeyStore.SecretBag_OID = new ObjectIdentifier(PKCS12KeyStore.secretBag);
            PKCS12KeyStore.PKCS9FriendlyName_OID = new ObjectIdentifier(PKCS12KeyStore.pkcs9Name);
            PKCS12KeyStore.PKCS9LocalKeyId_OID = new ObjectIdentifier(PKCS12KeyStore.pkcs9KeyId);
            PKCS12KeyStore.PKCS9CertType_OID = new ObjectIdentifier(PKCS12KeyStore.pkcs9certType);
            PKCS12KeyStore.pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(PKCS12KeyStore.pbeWithSHAAnd40BitRC2CBC);
            PKCS12KeyStore.pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(PKCS12KeyStore.pbeWithSHAAnd3KeyTripleDESCBC);
            PKCS12KeyStore.pbes2_OID = new ObjectIdentifier(PKCS12KeyStore.pbes2);
            PKCS12KeyStore.TrustedKeyUsage_OID = new ObjectIdentifier(PKCS12KeyStore.TrustedKeyUsage);
            PKCS12KeyStore.AnyUsage = new ObjectIdentifier[] { new ObjectIdentifier(PKCS12KeyStore.AnyExtendedKeyUsage) };
        }
        catch (final IOException ex) {}
    }
    
    private static class Entry
    {
        Date date;
        String alias;
        byte[] keyId;
        Set<KeyStore.Entry.Attribute> attributes;
    }
    
    private static class KeyEntry extends Entry
    {
    }
    
    private static class PrivateKeyEntry extends KeyEntry
    {
        byte[] protectedPrivKey;
        Certificate[] chain;
    }
    
    private static class SecretKeyEntry extends KeyEntry
    {
        byte[] protectedSecretKey;
    }
    
    private static class CertEntry extends Entry
    {
        final X509Certificate cert;
        ObjectIdentifier[] trustedKeyUsage;
        
        CertEntry(final X509Certificate x509Certificate, final byte[] array, final String s) {
            this(x509Certificate, array, s, null, null);
        }
        
        CertEntry(final X509Certificate cert, final byte[] keyId, final String alias, final ObjectIdentifier[] trustedKeyUsage, final Set<? extends KeyStore.Entry.Attribute> set) {
            this.date = new Date();
            this.cert = cert;
            this.keyId = keyId;
            this.alias = alias;
            this.trustedKeyUsage = trustedKeyUsage;
            this.attributes = new HashSet<KeyStore.Entry.Attribute>();
            if (set != null) {
                this.attributes.addAll(set);
            }
        }
    }
    
    @FunctionalInterface
    private interface RetryWithZero<T>
    {
        T tryOnce(final char[] p0) throws Exception;
        
        default <S> S run(final RetryWithZero<S> retryWithZero, final char[] array) throws Exception {
            try {
                return retryWithZero.tryOnce(array);
            }
            catch (final Exception ex) {
                if (array.length == 0) {
                    if (PKCS12KeyStore.debug != null) {
                        PKCS12KeyStore.debug.println("Retry with a NUL password");
                    }
                    return retryWithZero.tryOnce(new char[1]);
                }
                throw ex;
            }
        }
    }
}
