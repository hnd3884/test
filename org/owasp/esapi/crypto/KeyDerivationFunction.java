package org.owasp.esapi.crypto;

import java.security.NoSuchAlgorithmException;
import org.owasp.esapi.util.ByteConversionUtil;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import org.owasp.esapi.errors.EncryptionException;
import javax.crypto.SecretKey;
import org.owasp.esapi.errors.ConfigurationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

public class KeyDerivationFunction
{
    public static final int originalVersion = 20110203;
    public static final int kdfVersion = 20130830;
    private static final long serialVersionUID = 20130830L;
    private static final Logger logger;
    private String prfAlg_;
    private int version_;
    private String context_;
    
    public KeyDerivationFunction(final PRF_ALGORITHMS prfAlg) {
        this.prfAlg_ = null;
        this.version_ = 20130830;
        this.context_ = "";
        this.prfAlg_ = prfAlg.getAlgName();
    }
    
    public KeyDerivationFunction() {
        this.prfAlg_ = null;
        this.version_ = 20130830;
        this.context_ = "";
        final String prfName = ESAPI.securityConfiguration().getKDFPseudoRandomFunction();
        if (!isValidPRF(prfName)) {
            throw new ConfigurationException("Algorithm name " + prfName + " not a valid algorithm name for property " + "Encryptor.KDF.PRF");
        }
        this.prfAlg_ = prfName;
    }
    
    public String getPRFAlgName() {
        return this.prfAlg_;
    }
    
    static int getDefaultPRFSelection() {
        final String prfName = ESAPI.securityConfiguration().getKDFPseudoRandomFunction();
        for (final PRF_ALGORITHMS prf : PRF_ALGORITHMS.values()) {
            if (prf.getAlgName().equals(prfName)) {
                return prf.getValue();
            }
        }
        throw new ConfigurationException("Algorithm name " + prfName + " not a valid algorithm name for property " + "Encryptor.KDF.PRF");
    }
    
    public void setVersion(final int version) throws IllegalArgumentException {
        CryptoHelper.isValidKDFVersion(version, false, true);
        this.version_ = version;
    }
    
    public int getVersion() {
        return this.version_;
    }
    
    public void setContext(final String context) {
        if (context == null) {
            throw new IllegalArgumentException("Context may not be null.");
        }
        this.context_ = context;
    }
    
    public String getContext() {
        return this.context_;
    }
    
    public SecretKey computeDerivedKey(final SecretKey keyDerivationKey, int keySize, final String purpose) throws NoSuchAlgorithmException, InvalidKeyException, EncryptionException {
        assert keyDerivationKey != null : "Key derivation key cannot be null.";
        assert keySize >= 56 : "Key has size of " + keySize + ", which is less than minimum of 56-bits.";
        assert keySize % 8 == 0 : "Key size (" + keySize + ") must be a even multiple of 8-bits.";
        assert purpose != null && !purpose.equals("") : "Purpose may not be null or empty.";
        keySize = calcKeySize(keySize);
        final byte[] derivedKey = new byte[keySize];
        byte[] label;
        byte[] context;
        try {
            label = purpose.getBytes("UTF-8");
            context = this.context_.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncryptionException("Encryption failure (internal encoding error: UTF-8)", "UTF-8 encoding is NOT supported as a standard byte encoding: " + e.getMessage(), e);
        }
        final SecretKey sk = new SecretKeySpec(keyDerivationKey.getEncoded(), "HmacSHA1");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(sk);
        }
        catch (final InvalidKeyException ex) {
            KeyDerivationFunction.logger.error(Logger.SECURITY_FAILURE, "Created HmacSHA1 Mac but SecretKey sk has alg " + sk.getAlgorithm(), ex);
            throw ex;
        }
        int ctr = 1;
        int totalCopied = 0;
        int destPos = 0;
        int len = 0;
        byte[] tmpKey = null;
        do {
            mac.update(ByteConversionUtil.fromInt(ctr++));
            mac.update(label);
            mac.update((byte)0);
            mac.update(context);
            tmpKey = mac.doFinal(ByteConversionUtil.fromInt(keySize));
            if (tmpKey.length >= keySize) {
                len = keySize;
            }
            else {
                len = Math.min(tmpKey.length, keySize - totalCopied);
            }
            System.arraycopy(tmpKey, 0, derivedKey, destPos, len);
            label = tmpKey;
            totalCopied += tmpKey.length;
            destPos += len;
        } while (totalCopied < keySize);
        for (int i = 0; i < tmpKey.length; ++i) {
            tmpKey[i] = 0;
        }
        tmpKey = null;
        return new SecretKeySpec(derivedKey, keyDerivationKey.getAlgorithm());
    }
    
    public static boolean isValidPRF(final String prfAlgName) {
        for (final PRF_ALGORITHMS prf : PRF_ALGORITHMS.values()) {
            if (prf.getAlgName().equals(prfAlgName)) {
                return true;
            }
        }
        return false;
    }
    
    public static PRF_ALGORITHMS convertNameToPRF(final String prfAlgName) {
        for (final PRF_ALGORITHMS prf : PRF_ALGORITHMS.values()) {
            if (prf.getAlgName().equals(prfAlgName)) {
                return prf;
            }
        }
        throw new IllegalArgumentException("Algorithm name " + prfAlgName + " not a valid PRF algorithm name for the ESAPI KDF.");
    }
    
    public static PRF_ALGORITHMS convertIntToPRF(final int selection) {
        for (final PRF_ALGORITHMS prf : PRF_ALGORITHMS.values()) {
            if (prf.getValue() == selection) {
                return prf;
            }
        }
        throw new IllegalArgumentException("No KDF PRF algorithm found for value name " + selection);
    }
    
    private static int calcKeySize(final int ks) {
        assert ks > 0 : "Key size must be > 0 bits.";
        int numBytes = 0;
        final int n = ks / 8;
        final int rem = ks % 8;
        if (rem == 0) {
            numBytes = n;
        }
        else {
            numBytes = n + 1;
        }
        return numBytes;
    }
    
    public static final void main(final String[] args) {
        System.out.println("Supported pseudo-random functions for KDF (version: 20130830)");
        System.out.println("Enum Name\tAlgorithm\t# bits");
        for (final PRF_ALGORITHMS prf : PRF_ALGORITHMS.values()) {
            System.out.println(prf + "\t" + prf.getAlgName() + "\t" + prf.getBits());
        }
    }
    
    static {
        logger = ESAPI.getLogger("KeyDerivationFunction");
    }
    
    public enum PRF_ALGORITHMS
    {
        HmacSHA1(0, 160, "HmacSHA1"), 
        HmacSHA256(1, 256, "HmacSHA256"), 
        HmacSHA384(2, 384, "HmacSHA384"), 
        HmacSHA512(3, 512, "HmacSHA512");
        
        private final byte value;
        private final short bits;
        private final String algName;
        
        private PRF_ALGORITHMS(final int value, final int bits, final String algName) {
            this.value = (byte)value;
            this.bits = (short)bits;
            this.algName = algName;
        }
        
        public byte getValue() {
            return this.value;
        }
        
        public short getBits() {
            return this.bits;
        }
        
        public String getAlgName() {
            return this.algName;
        }
    }
}
