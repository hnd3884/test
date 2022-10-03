package org.owasp.esapi.reference.crypto;

import java.security.NoSuchProviderException;
import org.owasp.esapi.crypto.SecurityProviderLoader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.owasp.esapi.crypto.KeyDerivationFunction;
import java.util.Date;
import org.owasp.esapi.errors.IntegrityException;
import org.owasp.esapi.EncoderConstants;
import java.security.Signature;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import org.owasp.esapi.codecs.Hex;
import org.owasp.esapi.errors.ConfigurationException;
import org.owasp.esapi.crypto.CipherSpec;
import javax.crypto.Cipher;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.crypto.SecretKey;
import java.util.Set;
import java.util.Iterator;
import java.security.Provider;
import org.owasp.esapi.crypto.CryptoHelper;
import java.security.SecureRandom;
import org.owasp.esapi.ESAPI;
import java.util.Map;
import java.util.TreeMap;
import java.security.Security;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.Logger;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.spec.SecretKeySpec;
import org.owasp.esapi.Encryptor;

public final class JavaEncryptor implements Encryptor
{
    private static volatile Encryptor singletonInstance;
    private static boolean initialized;
    private static SecretKeySpec secretKeySpec;
    private static String encryptAlgorithm;
    private static String encoding;
    private static int encryptionKeyLength;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static String signatureAlgorithm;
    private static String randomAlgorithm;
    private static int signatureKeyLength;
    private static String hashAlgorithm;
    private static int hashIterations;
    private static Logger logger;
    private static int encryptCounter;
    private static int decryptCounter;
    private static final int logEveryNthUse = 25;
    private static final String DECRYPTION_FAILED = "Decryption failed; see logs for details.";
    private static int N_SECS;
    
    public static Encryptor getInstance() throws EncryptionException {
        if (JavaEncryptor.singletonInstance == null) {
            synchronized (JavaEncryptor.class) {
                if (JavaEncryptor.singletonInstance == null) {
                    JavaEncryptor.singletonInstance = new JavaEncryptor();
                }
            }
        }
        return JavaEncryptor.singletonInstance;
    }
    
    public static void main(final String[] args) throws Exception {
        System.out.println("Generating a new secret master key");
        if (args.length == 1 && args[0].equalsIgnoreCase("-print")) {
            System.out.println("AVAILABLE ALGORITHMS");
            final Provider[] providers = Security.getProviders();
            final TreeMap<String, String> tm = new TreeMap<String, String>();
            for (int i = 0; i != providers.length; ++i) {
                System.out.println("===== Provider " + i + ":" + providers[i].getName() + " ======");
                for (final String key : providers[i].keySet()) {
                    final String value = providers[i].getProperty(key);
                    tm.put(key, value);
                    System.out.println("\t\t   " + key + " -> " + value);
                }
            }
            final Set<Map.Entry<String, String>> keyValueSet = tm.entrySet();
            for (final Map.Entry<String, String> entry : keyValueSet) {
                final String key2 = entry.getKey();
                final String value2 = entry.getValue();
                System.out.println("   " + key2 + " -> " + value2);
            }
        }
        else {
            System.out.println("\tuse '-print' to also show available crypto algorithms from all the security providers");
        }
        JavaEncryptor.encryptAlgorithm = ESAPI.securityConfiguration().getEncryptionAlgorithm();
        JavaEncryptor.encryptionKeyLength = ESAPI.securityConfiguration().getEncryptionKeyLength();
        JavaEncryptor.randomAlgorithm = ESAPI.securityConfiguration().getRandomAlgorithm();
        final SecureRandom random = SecureRandom.getInstance(JavaEncryptor.randomAlgorithm);
        final SecretKey secretKey = CryptoHelper.generateSecretKey(JavaEncryptor.encryptAlgorithm, JavaEncryptor.encryptionKeyLength);
        final byte[] raw = secretKey.getEncoded();
        final byte[] salt = new byte[20];
        random.nextBytes(salt);
        final String eol = System.getProperty("line.separator", "\n");
        System.out.println(eol + "Copy and paste these lines into your ESAPI.properties" + eol);
        System.out.println("#==============================================================");
        System.out.println("Encryptor.MasterKey=" + ESAPI.encoder().encodeForBase64(raw, false));
        System.out.println("Encryptor.MasterSalt=" + ESAPI.encoder().encodeForBase64(salt, false));
        System.out.println("#==============================================================" + eol);
    }
    
    private JavaEncryptor() throws EncryptionException {
        final byte[] salt = ESAPI.securityConfiguration().getMasterSalt();
        final byte[] skey = ESAPI.securityConfiguration().getMasterKey();
        assert salt != null : "Can't obtain master salt, Encryptor.MasterSalt";
        assert salt.length >= 16 : "Encryptor.MasterSalt must be at least 16 bytes. Length is: " + salt.length + " bytes.";
        assert skey != null : "Can't obtain master key, Encryptor.MasterKey";
        assert skey.length >= 7 : "Encryptor.MasterKey must be at least 7 bytes. Length is: " + skey.length + " bytes.";
        synchronized (JavaEncryptor.class) {
            if (!JavaEncryptor.initialized) {
                JavaEncryptor.secretKeySpec = new SecretKeySpec(skey, JavaEncryptor.encryptAlgorithm);
                try {
                    final SecureRandom prng = SecureRandom.getInstance(JavaEncryptor.randomAlgorithm);
                    final byte[] seed = this.hash(new String(skey, JavaEncryptor.encoding), new String(salt, JavaEncryptor.encoding)).getBytes(JavaEncryptor.encoding);
                    prng.setSeed(seed);
                    initKeyPair(prng);
                }
                catch (final Exception e) {
                    throw new EncryptionException("Encryption failure", "Error creating Encryptor", e);
                }
                JavaEncryptor.initialized = true;
            }
        }
    }
    
    @Override
    public String hash(final String plaintext, final String salt) throws EncryptionException {
        return this.hash(plaintext, salt, JavaEncryptor.hashIterations);
    }
    
    @Override
    public String hash(final String plaintext, final String salt, final int iterations) throws EncryptionException {
        byte[] bytes = null;
        try {
            final MessageDigest digest = MessageDigest.getInstance(JavaEncryptor.hashAlgorithm);
            digest.reset();
            digest.update(ESAPI.securityConfiguration().getMasterSalt());
            digest.update(salt.getBytes(JavaEncryptor.encoding));
            digest.update(plaintext.getBytes(JavaEncryptor.encoding));
            bytes = digest.digest();
            for (int i = 0; i < iterations; ++i) {
                digest.reset();
                bytes = digest.digest(bytes);
            }
            final String encoded = ESAPI.encoder().encodeForBase64(bytes, false);
            return encoded;
        }
        catch (final NoSuchAlgorithmException e) {
            throw new EncryptionException("Internal error", "Can't find hash algorithm " + JavaEncryptor.hashAlgorithm, e);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new EncryptionException("Internal error", "Can't find encoding for " + JavaEncryptor.encoding, ex);
        }
    }
    
    @Override
    public CipherText encrypt(final PlainText plaintext) throws EncryptionException {
        return this.encrypt(JavaEncryptor.secretKeySpec, plaintext);
    }
    
    @Override
    public CipherText encrypt(final SecretKey key, final PlainText plain) throws EncryptionException {
        if (key == null) {
            throw new IllegalArgumentException("(Master) encryption key arg may not be null. Is Encryptor.MasterKey set?");
        }
        if (plain == null) {
            throw new IllegalArgumentException("PlainText may arg not be null");
        }
        final byte[] plaintext = plain.asBytes();
        final boolean overwritePlaintext = ESAPI.securityConfiguration().overwritePlainText();
        boolean success = false;
        String xform = null;
        final int keySize = key.getEncoded().length * 8;
        try {
            xform = ESAPI.securityConfiguration().getCipherTransformation();
            final String[] parts = xform.split("/");
            assert parts.length == 3 : "Malformed cipher transformation: " + xform;
            final String cipherMode = parts[1];
            if (!CryptoHelper.isAllowedCipherMode(cipherMode)) {
                throw new EncryptionException("Encryption failure: invalid cipher mode ( " + cipherMode + ") for encryption", "Encryption failure: Cipher transformation " + xform + " specifies invalid " + "cipher mode " + cipherMode);
            }
            final Cipher encrypter = Cipher.getInstance(xform);
            final String cipherAlg = encrypter.getAlgorithm();
            final int keyLen = ESAPI.securityConfiguration().getEncryptionKeyLength();
            if (keySize != keyLen) {
                JavaEncryptor.logger.warning(Logger.SECURITY_FAILURE, "Encryption key length mismatch. ESAPI.EncryptionKeyLength is " + keyLen + " bits, but length of actual encryption key is " + keySize + " bits.  Did you remember to regenerate your master key (if that is what you are using)???");
            }
            if (keySize < keyLen) {
                JavaEncryptor.logger.warning(Logger.SECURITY_FAILURE, "Actual key size of " + keySize + " bits SMALLER THAN specified " + "encryption key length (ESAPI.EncryptionKeyLength) of " + keyLen + " bits with cipher algorithm " + cipherAlg);
            }
            if (keySize < 112) {
                JavaEncryptor.logger.warning(Logger.SECURITY_FAILURE, "Potentially unsecure encryption. Key size of " + keySize + "bits " + "not sufficiently long for " + cipherAlg + ". Should use appropriate algorithm with key size " + "of *at least* 112 bits except when required by legacy apps. See NIST Special Pub 800-57.");
            }
            final String skeyAlg = key.getAlgorithm();
            if (!cipherAlg.startsWith(skeyAlg + "/") && !cipherAlg.equals(skeyAlg)) {
                JavaEncryptor.logger.warning(Logger.SECURITY_FAILURE, "Encryption mismatch between cipher algorithm (" + cipherAlg + ") and SecretKey algorithm (" + skeyAlg + "). Cipher will use algorithm " + cipherAlg);
            }
            byte[] ivBytes = null;
            final CipherSpec cipherSpec = new CipherSpec(encrypter, keySize);
            final boolean preferredCipherMode = CryptoHelper.isCombinedCipherMode(cipherMode);
            SecretKey encKey = null;
            if (preferredCipherMode) {
                encKey = key;
            }
            else {
                encKey = this.computeDerivedKey(20130830, this.getDefaultPRF(), key, keySize, "encryption");
            }
            if (cipherSpec.requiresIV()) {
                final String ivType = ESAPI.securityConfiguration().getIVType();
                IvParameterSpec ivSpec = null;
                if (ivType.equalsIgnoreCase("random")) {
                    ivBytes = ESAPI.randomizer().getRandomBytes(encrypter.getBlockSize());
                }
                else {
                    if (!ivType.equalsIgnoreCase("fixed")) {
                        throw new ConfigurationException("Property Encryptor.ChooseIVMethod must be set to 'random' or 'fixed'");
                    }
                    final String fixedIVAsHex = ESAPI.securityConfiguration().getFixedIV();
                    ivBytes = Hex.decode(fixedIVAsHex);
                }
                ivSpec = new IvParameterSpec(ivBytes);
                cipherSpec.setIV(ivBytes);
                encrypter.init(1, encKey, ivSpec);
            }
            else {
                encrypter.init(1, encKey);
            }
            JavaEncryptor.logger.debug(Logger.EVENT_SUCCESS, "Encrypting with " + cipherSpec);
            final byte[] raw = encrypter.doFinal(plaintext);
            final CipherText ciphertext = new CipherText(cipherSpec, raw);
            if (!preferredCipherMode) {
                final SecretKey authKey = this.computeDerivedKey(20130830, this.getDefaultPRF(), key, keySize, "authenticity");
                ciphertext.computeAndStoreMAC(authKey);
            }
            JavaEncryptor.logger.debug(Logger.EVENT_SUCCESS, "JavaEncryptor.encrypt(SecretKey,byte[],boolean,boolean) -- success!");
            success = true;
            return ciphertext;
        }
        catch (final InvalidKeyException ike) {
            throw new EncryptionException("Encryption failure: Invalid key exception.", "Requested key size: " + keySize + "bits greater than 128 bits. Must install unlimited strength crypto extension from Sun: " + ike.getMessage(), ike);
        }
        catch (final ConfigurationException cex) {
            throw new EncryptionException("Encryption failure: Configuration error. Details in log.", "Key size mismatch or unsupported IV method. Check encryption key size vs. ESAPI.EncryptionKeyLength or Encryptor.ChooseIVMethod property.", cex);
        }
        catch (final InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Encryption failure (invalid IV)", "Encryption problem: Invalid IV spec: " + e.getMessage(), e);
        }
        catch (final IllegalBlockSizeException e2) {
            throw new EncryptionException("Encryption failure (no padding used; invalid input size)", "Encryption problem: Invalid input size without padding (" + xform + "). " + e2.getMessage(), e2);
        }
        catch (final BadPaddingException e3) {
            throw new EncryptionException("Encryption failure", "[Note: Should NEVER happen in encryption mode.] Encryption problem: " + e3.getMessage(), e3);
        }
        catch (final NoSuchAlgorithmException e4) {
            throw new EncryptionException("Encryption failure (unavailable cipher requested)", "Encryption problem: specified algorithm in cipher xform " + xform + " not available: " + e4.getMessage(), e4);
        }
        catch (final NoSuchPaddingException e5) {
            throw new EncryptionException("Encryption failure (unavailable padding scheme requested)", "Encryption problem: specified padding scheme in cipher xform " + xform + " not available: " + e5.getMessage(), e5);
        }
        finally {
            if (success && overwritePlaintext) {
                plain.overwrite();
            }
        }
    }
    
    @Override
    public PlainText decrypt(final CipherText ciphertext) throws EncryptionException {
        return this.decrypt(JavaEncryptor.secretKeySpec, ciphertext);
    }
    
    @Override
    public PlainText decrypt(final SecretKey key, final CipherText ciphertext) throws EncryptionException, IllegalArgumentException {
        final long start = System.nanoTime();
        if (key == null) {
            throw new IllegalArgumentException("SecretKey arg may not be null");
        }
        if (ciphertext == null) {
            throw new IllegalArgumentException("Ciphertext may arg not be null");
        }
        if (!CryptoHelper.isAllowedCipherMode(ciphertext.getCipherMode())) {
            throw new EncryptionException("Decryption failed; see logs for details.", "Invalid cipher mode " + ciphertext.getCipherMode() + " not permitted for decryption or encryption operations.");
        }
        JavaEncryptor.logger.debug(Logger.EVENT_SUCCESS, "Args valid for JavaEncryptor.decrypt(SecretKey,CipherText): " + ciphertext);
        PlainText plaintext = null;
        boolean caughtException = false;
        int progressMark = 0;
        try {
            final boolean valid = CryptoHelper.isCipherTextMACvalid(key, ciphertext);
            if (!valid) {
                try {
                    this.handleDecryption(key, ciphertext);
                }
                catch (final Exception ex2) {}
                throw new EncryptionException("Decryption failed; see logs for details.", "Decryption failed because MAC invalid for " + ciphertext);
            }
            ++progressMark;
            plaintext = this.handleDecryption(key, ciphertext);
            ++progressMark;
        }
        catch (final EncryptionException ex) {
            caughtException = true;
            String logMsg = null;
            switch (progressMark) {
                case 1: {
                    logMsg = "Decryption failed because MAC invalid. See logged exception for details.";
                    break;
                }
                case 2: {
                    logMsg = "Decryption failed because handleDecryption() failed. See logged exception for details.";
                    break;
                }
                default: {
                    logMsg = "Programming error: unexpected progress mark == " + progressMark;
                    break;
                }
            }
            JavaEncryptor.logger.error(Logger.SECURITY_FAILURE, logMsg);
            throw ex;
        }
        finally {
            if (caughtException) {
                final long now = System.nanoTime();
                final long elapsed = now - start;
                final long NANOSECS_IN_SEC = 1000000000L;
                final long nSecs = JavaEncryptor.N_SECS * 1000000000L;
                if (elapsed < nSecs) {
                    final long extraSleep = nSecs - elapsed;
                    final long millis = extraSleep / 1000000L;
                    final long nanos = extraSleep - millis * 1000000L;
                    assert nanos >= 0L && nanos <= 2147483647L : "Nanosecs out of bounds; nanos = " + nanos;
                    try {
                        Thread.sleep(millis, (int)nanos);
                    }
                    catch (final InterruptedException ex3) {}
                }
            }
        }
        return plaintext;
    }
    
    private PlainText handleDecryption(final SecretKey key, final CipherText ciphertext) throws EncryptionException {
        int keySize = 0;
        try {
            final Cipher decrypter = Cipher.getInstance(ciphertext.getCipherTransformation());
            keySize = key.getEncoded().length * 8;
            final boolean preferredCipherMode = CryptoHelper.isCombinedCipherMode(ciphertext.getCipherMode());
            SecretKey encKey = null;
            if (preferredCipherMode) {
                encKey = key;
            }
            else {
                encKey = this.computeDerivedKey(ciphertext.getKDFVersion(), ciphertext.getKDF_PRF(), key, keySize, "encryption");
            }
            if (ciphertext.requiresIV()) {
                decrypter.init(2, encKey, new IvParameterSpec(ciphertext.getIV()));
            }
            else {
                decrypter.init(2, encKey);
            }
            final byte[] output = decrypter.doFinal(ciphertext.getRawCipherText());
            return new PlainText(output);
        }
        catch (final InvalidKeyException ike) {
            throw new EncryptionException("Decryption failed; see logs for details.", "Must install JCE Unlimited Strength Jurisdiction Policy Files from Sun", ike);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new EncryptionException("Decryption failed; see logs for details.", "Invalid algorithm for available JCE providers - " + ciphertext.getCipherTransformation() + ": " + e.getMessage(), e);
        }
        catch (final NoSuchPaddingException e2) {
            throw new EncryptionException("Decryption failed; see logs for details.", "Invalid padding scheme (" + ciphertext.getPaddingScheme() + ") for cipher transformation " + ciphertext.getCipherTransformation() + ": " + e2.getMessage(), e2);
        }
        catch (final InvalidAlgorithmParameterException e3) {
            throw new EncryptionException("Decryption failed; see logs for details.", "Decryption problem: " + e3.getMessage(), e3);
        }
        catch (final IllegalBlockSizeException e4) {
            throw new EncryptionException("Decryption failed; see logs for details.", "Decryption problem: " + e4.getMessage(), e4);
        }
        catch (final BadPaddingException e5) {
            SecretKey authKey;
            try {
                authKey = this.computeDerivedKey(ciphertext.getKDFVersion(), ciphertext.getKDF_PRF(), key, keySize, "authenticity");
            }
            catch (final Exception e6) {
                throw new EncryptionException("Decryption failed; see logs for details.", "Decryption problem -- failed to compute derived key for authenticity: " + e6.getMessage(), e6);
            }
            final boolean success = ciphertext.validateMAC(authKey);
            if (success) {
                throw new EncryptionException("Decryption failed; see logs for details.", "Decryption problem: " + e5.getMessage(), e5);
            }
            throw new EncryptionException("Decryption failed; see logs for details.", "Decryption problem: WARNING: Adversary may have tampered with CipherText object orCipherText object mangled in transit: " + e5.getMessage(), e5);
        }
    }
    
    @Override
    public String sign(final String data) throws EncryptionException {
        try {
            final Signature signer = Signature.getInstance(JavaEncryptor.signatureAlgorithm);
            signer.initSign(JavaEncryptor.privateKey);
            signer.update(data.getBytes(JavaEncryptor.encoding));
            final byte[] bytes = signer.sign();
            return ESAPI.encoder().encodeForBase64(bytes, false);
        }
        catch (final InvalidKeyException ike) {
            throw new EncryptionException("Encryption failure", "Must install unlimited strength crypto extension from Sun", ike);
        }
        catch (final Exception e) {
            throw new EncryptionException("Signature failure", "Can't find signature algorithm " + JavaEncryptor.signatureAlgorithm, e);
        }
    }
    
    @Override
    public boolean verifySignature(final String signature, final String data) {
        try {
            final byte[] bytes = ESAPI.encoder().decodeFromBase64(signature);
            final Signature signer = Signature.getInstance(JavaEncryptor.signatureAlgorithm);
            signer.initVerify(JavaEncryptor.publicKey);
            signer.update(data.getBytes(JavaEncryptor.encoding));
            return signer.verify(bytes);
        }
        catch (final Exception e) {
            final EncryptionException ex = new EncryptionException("Invalid signature", "Problem verifying signature: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String seal(final String data, final long expiration) throws IntegrityException {
        if (data == null) {
            throw new IllegalArgumentException("Data to be sealed may not be null.");
        }
        try {
            String b64data = null;
            try {
                b64data = ESAPI.encoder().encodeForBase64(data.getBytes("UTF-8"), false);
            }
            catch (final UnsupportedEncodingException ex) {}
            final String nonce = ESAPI.randomizer().getRandomString(10, EncoderConstants.CHAR_ALPHANUMERICS);
            final String plaintext = expiration + ":" + nonce + ":" + b64data;
            final String sig = this.sign(plaintext);
            final CipherText ciphertext = this.encrypt(new PlainText(plaintext + ":" + sig));
            final String sealedData = ESAPI.encoder().encodeForBase64(ciphertext.asPortableSerializedByteArray(), false);
            return sealedData;
        }
        catch (final EncryptionException e) {
            throw new IntegrityException(e.getUserMessage(), e.getLogMessage(), e);
        }
    }
    
    @Override
    public String unseal(final String seal) throws EncryptionException {
        PlainText plaintext = null;
        try {
            final byte[] encryptedBytes = ESAPI.encoder().decodeFromBase64(seal);
            CipherText cipherText = null;
            try {
                cipherText = CipherText.fromPortableSerializedBytes(encryptedBytes);
            }
            catch (final AssertionError e) {
                throw new EncryptionException("Invalid seal", "Seal passed garbarge data resulting in AssertionError: " + e);
            }
            plaintext = this.decrypt(cipherText);
            final String[] parts = plaintext.toString().split(":");
            if (parts.length != 4) {
                throw new EncryptionException("Invalid seal", "Seal was not formatted properly.");
            }
            final String timestring = parts[0];
            final long now = new Date().getTime();
            final long expiration = Long.parseLong(timestring);
            if (now > expiration) {
                throw new EncryptionException("Invalid seal", "Seal expiration date of " + new Date(expiration) + " has past.");
            }
            final String nonce = parts[1];
            final String b64data = parts[2];
            final String sig = parts[3];
            if (!this.verifySignature(sig, timestring + ":" + nonce + ":" + b64data)) {
                throw new EncryptionException("Invalid seal", "Seal integrity check failed");
            }
            return new String(ESAPI.encoder().decodeFromBase64(b64data), "UTF-8");
        }
        catch (final EncryptionException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            throw new EncryptionException("Invalid seal", "Invalid seal:" + e3.getMessage(), e3);
        }
    }
    
    @Override
    public boolean verifySeal(final String seal) {
        try {
            this.unseal(seal);
            return true;
        }
        catch (final EncryptionException e) {
            return false;
        }
    }
    
    @Override
    public long getTimeStamp() {
        return new Date().getTime();
    }
    
    @Override
    public long getRelativeTimeStamp(final long offset) {
        return new Date().getTime() + offset;
    }
    
    private void logWarning(String where, final String msg) {
        int counter = 0;
        if (where.equals("encrypt")) {
            counter = JavaEncryptor.encryptCounter++;
            where = "JavaEncryptor.encrypt(): [count=" + counter + "]";
        }
        else if (where.equals("decrypt")) {
            counter = JavaEncryptor.decryptCounter++;
            where = "JavaEncryptor.decrypt(): [count=" + counter + "]";
        }
        else {
            where = "JavaEncryptor: Unknown method: ";
        }
        if (counter % 25 == 0) {
            JavaEncryptor.logger.warning(Logger.SECURITY_FAILURE, where + msg);
        }
    }
    
    private KeyDerivationFunction.PRF_ALGORITHMS getPRF(final String name) {
        String prfName = null;
        if (name == null) {
            prfName = ESAPI.securityConfiguration().getKDFPseudoRandomFunction();
        }
        else {
            prfName = name;
        }
        final KeyDerivationFunction.PRF_ALGORITHMS prf = KeyDerivationFunction.convertNameToPRF(prfName);
        return prf;
    }
    
    private KeyDerivationFunction.PRF_ALGORITHMS getDefaultPRF() {
        final String prfName = ESAPI.securityConfiguration().getKDFPseudoRandomFunction();
        return this.getPRF(prfName);
    }
    
    private SecretKey computeDerivedKey(final int kdfVersion, final KeyDerivationFunction.PRF_ALGORITHMS prf, final SecretKey kdk, final int keySize, final String purpose) throws NoSuchAlgorithmException, InvalidKeyException, EncryptionException {
        assert prf != null : "Pseudo Random Function for KDF cannot be null";
        assert kdk != null : "Key derivation key cannot be null.";
        assert keySize >= 56 : "Key has size of " + keySize + ", which is less than minimum of 56-bits.";
        assert keySize % 8 == 0 : "Key size (" + keySize + ") must be a even multiple of 8-bits.";
        assert purpose != null : "Purpose cannot be null. Should be 'encryption' or 'authenticity'.";
        assert purpose.equals("encryption") || purpose.equals("authenticity") : "Purpose must be \"encryption\" or \"authenticity\".";
        final KeyDerivationFunction kdf = new KeyDerivationFunction(prf);
        if (kdfVersion != 0) {
            kdf.setVersion(kdfVersion);
        }
        return kdf.computeDerivedKey(kdk, keySize, purpose);
    }
    
    private static void setupAlgorithms() {
        JavaEncryptor.encryptAlgorithm = ESAPI.securityConfiguration().getEncryptionAlgorithm();
        JavaEncryptor.signatureAlgorithm = ESAPI.securityConfiguration().getDigitalSignatureAlgorithm();
        JavaEncryptor.randomAlgorithm = ESAPI.securityConfiguration().getRandomAlgorithm();
        JavaEncryptor.hashAlgorithm = ESAPI.securityConfiguration().getHashAlgorithm();
        JavaEncryptor.hashIterations = ESAPI.securityConfiguration().getHashIterations();
        JavaEncryptor.encoding = ESAPI.securityConfiguration().getCharacterEncoding();
        JavaEncryptor.encryptionKeyLength = ESAPI.securityConfiguration().getEncryptionKeyLength();
        JavaEncryptor.signatureKeyLength = ESAPI.securityConfiguration().getDigitalSignatureKeyLength();
    }
    
    private static void initKeyPair(final SecureRandom prng) throws NoSuchAlgorithmException {
        String sigAlg = JavaEncryptor.signatureAlgorithm.toLowerCase();
        if (sigAlg.endsWith("withdsa")) {
            sigAlg = "DSA";
        }
        else if (sigAlg.endsWith("withrsa")) {
            sigAlg = "RSA";
        }
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(sigAlg);
        keyGen.initialize(JavaEncryptor.signatureKeyLength, prng);
        final KeyPair pair = keyGen.generateKeyPair();
        JavaEncryptor.privateKey = pair.getPrivate();
        JavaEncryptor.publicKey = pair.getPublic();
    }
    
    static {
        JavaEncryptor.initialized = false;
        JavaEncryptor.secretKeySpec = null;
        JavaEncryptor.encryptAlgorithm = "AES";
        JavaEncryptor.encoding = "UTF-8";
        JavaEncryptor.encryptionKeyLength = 128;
        JavaEncryptor.privateKey = null;
        JavaEncryptor.publicKey = null;
        JavaEncryptor.signatureAlgorithm = "SHA1withDSA";
        JavaEncryptor.randomAlgorithm = "SHA1PRNG";
        JavaEncryptor.signatureKeyLength = 1024;
        JavaEncryptor.hashAlgorithm = "SHA-512";
        JavaEncryptor.hashIterations = 1024;
        JavaEncryptor.logger = ESAPI.getLogger("JavaEncryptor");
        JavaEncryptor.encryptCounter = 0;
        JavaEncryptor.decryptCounter = 0;
        JavaEncryptor.N_SECS = 2;
        try {
            SecurityProviderLoader.loadESAPIPreferredJCEProvider();
        }
        catch (final NoSuchProviderException ex) {
            JavaEncryptor.logger.fatal(Logger.SECURITY_FAILURE, "JavaEncryptor failed to load preferred JCE provider.", ex);
            throw new ExceptionInInitializerError(ex);
        }
        setupAlgorithms();
    }
}
