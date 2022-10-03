package org.owasp.esapi.crypto;

import java.util.Arrays;
import java.util.List;
import org.owasp.esapi.ESAPI;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import org.owasp.esapi.errors.EncryptionException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.owasp.esapi.Logger;

public class CryptoHelper
{
    private static final Logger logger;
    
    public static SecretKey generateSecretKey(final String alg, final int keySize) throws EncryptionException {
        assert alg != null : "Algorithm must not be null.";
        assert !alg.equals("") : "Algorithm must not be empty";
        assert keySize > 0 : "Key size must be positive.";
        final String[] cipherSpec = alg.split("/");
        String cipherAlg = cipherSpec[0];
        try {
            if (cipherAlg.toUpperCase().startsWith("PBEWITH")) {
                cipherAlg = "PBE";
            }
            final KeyGenerator kgen = KeyGenerator.getInstance(cipherAlg);
            kgen.init(keySize);
            return kgen.generateKey();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new EncryptionException("Failed to generate random secret key", "Invalid algorithm. Failed to generate secret key for " + alg + " with size of " + keySize + " bits.", e);
        }
        catch (final InvalidParameterException e2) {
            throw new EncryptionException("Failed to generate random secret key - invalid key size specified.", "Invalid key size. Failed to generate secret key for " + alg + " with size of " + keySize + " bits.", e2);
        }
    }
    
    @Deprecated
    public static SecretKey computeDerivedKey(final SecretKey keyDerivationKey, final int keySize, final String purpose) throws NoSuchAlgorithmException, InvalidKeyException, EncryptionException {
        assert keyDerivationKey != null : "Key derivation key cannot be null.";
        assert keySize >= 56 : "Key has size of " + keySize + ", which is less than minimum of 56-bits.";
        assert keySize % 8 == 0 : "Key size (" + keySize + ") must be a even multiple of 8-bits.";
        assert purpose != null;
        assert purpose.equals("encryption") || purpose.equals("authenticity") : "Purpose must be \"encryption\" or \"authenticity\".";
        final KeyDerivationFunction kdf = new KeyDerivationFunction(KeyDerivationFunction.PRF_ALGORITHMS.HmacSHA1);
        return kdf.computeDerivedKey(keyDerivationKey, keySize, purpose);
    }
    
    public static boolean isCombinedCipherMode(final String cipherMode) {
        assert cipherMode != null : "Cipher mode may not be null";
        assert !cipherMode.equals("") : "Cipher mode may not be empty string";
        final List<String> combinedCipherModes = ESAPI.securityConfiguration().getCombinedCipherModes();
        return combinedCipherModes.contains(cipherMode);
    }
    
    public static boolean isAllowedCipherMode(final String cipherMode) {
        if (isCombinedCipherMode(cipherMode)) {
            return true;
        }
        final List<String> extraCipherModes = ESAPI.securityConfiguration().getAdditionalAllowedCipherModes();
        return extraCipherModes.contains(cipherMode);
    }
    
    public static boolean isMACRequired(final CipherText ct) {
        final boolean preferredCipherMode = isCombinedCipherMode(ct.getCipherMode());
        final boolean wantsMAC = ESAPI.securityConfiguration().useMACforCipherText();
        return !preferredCipherMode && wantsMAC;
    }
    
    public static boolean isCipherTextMACvalid(final SecretKey sk, final CipherText ct) {
        if (isMACRequired(ct)) {
            try {
                final SecretKey authKey = computeDerivedKey(sk, ct.getKeySize(), "authenticity");
                final boolean validMAC = ct.validateMAC(authKey);
                return validMAC;
            }
            catch (final Exception ex) {
                CryptoHelper.logger.warning(Logger.SECURITY_FAILURE, "Unable to validate MAC for ciphertext " + ct, ex);
                return false;
            }
        }
        return true;
    }
    
    public static void overwrite(final byte[] bytes, final byte x) {
        Arrays.fill(bytes, x);
    }
    
    public static void overwrite(final byte[] bytes) {
        overwrite(bytes, (byte)42);
    }
    
    public static void copyByteArray(final byte[] src, final byte[] dest, final int length) {
        System.arraycopy(src, 0, dest, 0, length);
    }
    
    public static void copyByteArray(final byte[] src, final byte[] dest) {
        copyByteArray(src, dest, src.length);
    }
    
    public static boolean arrayCompare(final byte[] b1, final byte[] b2) {
        if (b1 == b2) {
            return true;
        }
        if (b1 == null || b2 == null) {
            return b1 == b2;
        }
        if (b1.length != b2.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < b1.length; ++i) {
            result |= (b1[i] ^ b2[i]);
        }
        return result == 0;
    }
    
    public static boolean isValidKDFVersion(final int kdfVers, final boolean restrictToCurrent, final boolean throwIfError) throws IllegalArgumentException {
        boolean ret = true;
        if (kdfVers < 20110203 || kdfVers > 99991231) {
            ret = false;
        }
        else if (restrictToCurrent) {
            ret = (kdfVers <= 20130830);
        }
        if (ret) {
            return ret;
        }
        CryptoHelper.logger.warning(Logger.SECURITY_FAILURE, "Possible data tampering. Encountered invalid KDF version #. " + (throwIfError ? "Throwing IllegalArgumentException" : ""));
        if (throwIfError) {
            throw new IllegalArgumentException("Version (" + kdfVers + ") invalid. " + "Must be date in format of YYYYMMDD between " + 20110203 + "and 99991231.");
        }
        return false;
    }
    
    private CryptoHelper() {
    }
    
    static {
        logger = ESAPI.getLogger("CryptoHelper");
    }
}
