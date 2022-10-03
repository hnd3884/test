package org.owasp.esapi.crypto;

import java.util.Iterator;
import java.util.Collection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.owasp.esapi.errors.EnterpriseSecurityRuntimeException;
import javax.crypto.SecretKey;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import java.util.EnumSet;
import org.owasp.esapi.Logger;
import java.io.Serializable;

public final class CipherText implements Serializable
{
    public static final int cipherTextVersion = 20130830;
    private static final long serialVersionUID = 20130830L;
    private static final Logger logger;
    private CipherSpec cipherSpec_;
    private byte[] raw_ciphertext_;
    private byte[] separate_mac_;
    private long encryption_timestamp_;
    private int kdfVersion_;
    private int kdfPrfSelection_;
    private final EnumSet<CipherTextFlags> allCtFlags;
    private final EnumSet<CipherTextFlags> fromCipherSpec;
    private EnumSet<CipherTextFlags> progress;
    
    public CipherText() {
        this.cipherSpec_ = null;
        this.raw_ciphertext_ = null;
        this.separate_mac_ = null;
        this.encryption_timestamp_ = 0L;
        this.kdfVersion_ = 20130830;
        this.kdfPrfSelection_ = KeyDerivationFunction.getDefaultPRFSelection();
        this.allCtFlags = EnumSet.of(CipherTextFlags.ALGNAME, CipherTextFlags.CIPHERMODE, CipherTextFlags.PADDING, CipherTextFlags.KEYSIZE, CipherTextFlags.BLOCKSIZE, CipherTextFlags.CIPHERTEXT, CipherTextFlags.INITVECTOR);
        this.fromCipherSpec = EnumSet.of(CipherTextFlags.ALGNAME, CipherTextFlags.CIPHERMODE, CipherTextFlags.PADDING, CipherTextFlags.KEYSIZE, CipherTextFlags.BLOCKSIZE);
        this.progress = EnumSet.noneOf(CipherTextFlags.class);
        this.cipherSpec_ = new CipherSpec();
        this.received(this.fromCipherSpec);
    }
    
    public CipherText(final CipherSpec cipherSpec) {
        this.cipherSpec_ = null;
        this.raw_ciphertext_ = null;
        this.separate_mac_ = null;
        this.encryption_timestamp_ = 0L;
        this.kdfVersion_ = 20130830;
        this.kdfPrfSelection_ = KeyDerivationFunction.getDefaultPRFSelection();
        this.allCtFlags = EnumSet.of(CipherTextFlags.ALGNAME, CipherTextFlags.CIPHERMODE, CipherTextFlags.PADDING, CipherTextFlags.KEYSIZE, CipherTextFlags.BLOCKSIZE, CipherTextFlags.CIPHERTEXT, CipherTextFlags.INITVECTOR);
        this.fromCipherSpec = EnumSet.of(CipherTextFlags.ALGNAME, CipherTextFlags.CIPHERMODE, CipherTextFlags.PADDING, CipherTextFlags.KEYSIZE, CipherTextFlags.BLOCKSIZE);
        this.progress = EnumSet.noneOf(CipherTextFlags.class);
        this.cipherSpec_ = cipherSpec;
        this.received(this.fromCipherSpec);
        if (cipherSpec.getIV() != null) {
            this.received(CipherTextFlags.INITVECTOR);
        }
    }
    
    public CipherText(final CipherSpec cipherSpec, final byte[] cipherText) throws EncryptionException {
        this.cipherSpec_ = null;
        this.raw_ciphertext_ = null;
        this.separate_mac_ = null;
        this.encryption_timestamp_ = 0L;
        this.kdfVersion_ = 20130830;
        this.kdfPrfSelection_ = KeyDerivationFunction.getDefaultPRFSelection();
        this.allCtFlags = EnumSet.of(CipherTextFlags.ALGNAME, CipherTextFlags.CIPHERMODE, CipherTextFlags.PADDING, CipherTextFlags.KEYSIZE, CipherTextFlags.BLOCKSIZE, CipherTextFlags.CIPHERTEXT, CipherTextFlags.INITVECTOR);
        this.fromCipherSpec = EnumSet.of(CipherTextFlags.ALGNAME, CipherTextFlags.CIPHERMODE, CipherTextFlags.PADDING, CipherTextFlags.KEYSIZE, CipherTextFlags.BLOCKSIZE);
        this.progress = EnumSet.noneOf(CipherTextFlags.class);
        this.cipherSpec_ = cipherSpec;
        this.setCiphertext(cipherText);
        this.received(this.fromCipherSpec);
        if (cipherSpec.getIV() != null) {
            this.received(CipherTextFlags.INITVECTOR);
        }
    }
    
    public static CipherText fromPortableSerializedBytes(final byte[] bytes) throws EncryptionException {
        final CipherTextSerializer cts = new CipherTextSerializer(bytes);
        return cts.asCipherText();
    }
    
    public String getCipherTransformation() {
        return this.cipherSpec_.getCipherTransformation();
    }
    
    public String getCipherAlgorithm() {
        return this.cipherSpec_.getCipherAlgorithm();
    }
    
    public int getKeySize() {
        return this.cipherSpec_.getKeySize();
    }
    
    public int getBlockSize() {
        return this.cipherSpec_.getBlockSize();
    }
    
    public String getCipherMode() {
        return this.cipherSpec_.getCipherMode();
    }
    
    public String getPaddingScheme() {
        return this.cipherSpec_.getPaddingScheme();
    }
    
    public byte[] getIV() {
        if (this.isCollected(CipherTextFlags.INITVECTOR)) {
            return this.cipherSpec_.getIV();
        }
        CipherText.logger.error(Logger.SECURITY_FAILURE, "IV not set yet; unable to retrieve; returning null");
        return null;
    }
    
    public boolean requiresIV() {
        return this.cipherSpec_.requiresIV();
    }
    
    public byte[] getRawCipherText() {
        if (this.isCollected(CipherTextFlags.CIPHERTEXT)) {
            final byte[] copy = new byte[this.raw_ciphertext_.length];
            System.arraycopy(this.raw_ciphertext_, 0, copy, 0, this.raw_ciphertext_.length);
            return copy;
        }
        CipherText.logger.error(Logger.SECURITY_FAILURE, "Raw ciphertext not set yet; unable to retrieve; returning null");
        return null;
    }
    
    public int getRawCipherTextByteLength() {
        if (this.raw_ciphertext_ != null) {
            return this.raw_ciphertext_.length;
        }
        return 0;
    }
    
    public String getBase64EncodedRawCipherText() {
        return ESAPI.encoder().encodeForBase64(this.getRawCipherText(), false);
    }
    
    public String getEncodedIVCipherText() {
        if (this.isCollected(CipherTextFlags.INITVECTOR) && this.isCollected(CipherTextFlags.CIPHERTEXT)) {
            final byte[] iv = this.getIV();
            final byte[] raw = this.getRawCipherText();
            final byte[] ivPlusCipherText = new byte[iv.length + raw.length];
            System.arraycopy(iv, 0, ivPlusCipherText, 0, iv.length);
            System.arraycopy(raw, 0, ivPlusCipherText, iv.length, raw.length);
            return ESAPI.encoder().encodeForBase64(ivPlusCipherText, false);
        }
        CipherText.logger.error(Logger.SECURITY_FAILURE, "Raw ciphertext and/or IV not set yet; unable to retrieve; returning null");
        return null;
    }
    
    public void computeAndStoreMAC(final SecretKey authKey) {
        assert !this.macComputed() : "Programming error: Can't store message integrity code while encrypting; computeAndStoreMAC() called multiple times.";
        assert this.collectedAll() : "Have not collected all required information to compute and store MAC.";
        final byte[] result = this.computeMAC(authKey);
        if (result != null) {
            this.storeSeparateMAC(result);
        }
    }
    
    void storeSeparateMAC(final byte[] macValue) {
        if (!this.macComputed()) {
            CryptoHelper.copyByteArray(macValue, this.separate_mac_ = new byte[macValue.length]);
            assert this.macComputed();
        }
    }
    
    public boolean validateMAC(final SecretKey authKey) {
        final boolean requiresMAC = ESAPI.securityConfiguration().useMACforCipherText();
        if (requiresMAC && this.macComputed()) {
            final byte[] mac = this.computeMAC(authKey);
            if (mac.length != this.separate_mac_.length) {
                final String exm = "MACs are of different lengths. Should both be the same length";
                throw new EnterpriseSecurityRuntimeException(exm, "Possible tampering of MAC? " + exm + "computed MAC len: " + mac.length + ", received MAC len: " + this.separate_mac_.length);
            }
            return CryptoHelper.arrayCompare(mac, this.separate_mac_);
        }
        else {
            if (!requiresMAC) {
                return true;
            }
            CipherText.logger.warning(Logger.SECURITY_FAILURE, "MAC may have been tampered with (e.g., length set to 0).");
            return false;
        }
    }
    
    public byte[] asPortableSerializedByteArray() throws EncryptionException {
        if (!this.collectedAll()) {
            final String msg = "Can't serialize this CipherText object yet as not all mandatory information has been collected";
            throw new EncryptionException("Can't serialize incomplete ciphertext info", msg);
        }
        final boolean requiresMAC = ESAPI.securityConfiguration().useMACforCipherText();
        if (requiresMAC && !this.macComputed()) {
            final String msg2 = "Programming error: MAC is required for this cipher mode (" + this.getCipherMode() + "), but MAC has not yet been " + "computed and stored. Call the method " + "computeAndStoreMAC(SecretKey) first before " + "attempting serialization.";
            throw new EncryptionException("Can't serialize ciphertext info: Data integrity issue.", msg2);
        }
        return new CipherTextSerializer(this).asSerializedByteArray();
    }
    
    public void setCiphertext(final byte[] ciphertext) throws EncryptionException {
        if (this.macComputed()) {
            final String logMsg = "Programming error: Attempt to set ciphertext after MAC already computed.";
            CipherText.logger.error(Logger.SECURITY_FAILURE, logMsg);
            throw new EncryptionException("MAC already set; cannot store new raw ciphertext", logMsg);
        }
        if (ciphertext == null || ciphertext.length == 0) {
            throw new EncryptionException("Encryption faled; no ciphertext", "Ciphertext may not be null or 0 length!");
        }
        if (this.isCollected(CipherTextFlags.CIPHERTEXT)) {
            CipherText.logger.warning(Logger.SECURITY_FAILURE, "Raw ciphertext was already set; resetting.");
        }
        CryptoHelper.copyByteArray(ciphertext, this.raw_ciphertext_ = new byte[ciphertext.length]);
        this.received(CipherTextFlags.CIPHERTEXT);
        this.setEncryptionTimestamp();
    }
    
    public void setIVandCiphertext(final byte[] iv, final byte[] ciphertext) throws EncryptionException {
        if (this.isCollected(CipherTextFlags.INITVECTOR)) {
            CipherText.logger.warning(Logger.SECURITY_FAILURE, "IV was already set; resetting.");
        }
        if (this.isCollected(CipherTextFlags.CIPHERTEXT)) {
            CipherText.logger.warning(Logger.SECURITY_FAILURE, "Raw ciphertext was already set; resetting.");
        }
        if (this.macComputed()) {
            final String logMsg = "MAC already computed from previously set IV and raw ciphertext; may not be reset -- object is immutable.";
            CipherText.logger.error(Logger.SECURITY_FAILURE, logMsg);
            throw new EncryptionException("Validation of decryption failed.", logMsg);
        }
        if (ciphertext == null || ciphertext.length == 0) {
            throw new EncryptionException("Encryption faled; no ciphertext", "Ciphertext may not be null or 0 length!");
        }
        if (iv == null || iv.length == 0) {
            if (this.requiresIV()) {
                throw new EncryptionException("Encryption failed -- mandatory IV missing", "Cipher mode " + this.getCipherMode() + " has null or empty IV");
            }
        }
        else if (iv.length != this.getBlockSize()) {
            throw new EncryptionException("Encryption failed -- bad parameters passed to encrypt", "IV length does not match cipher block size of " + this.getBlockSize());
        }
        this.cipherSpec_.setIV(iv);
        this.received(CipherTextFlags.INITVECTOR);
        this.setCiphertext(ciphertext);
    }
    
    public int getKDFVersion() {
        return this.kdfVersion_;
    }
    
    public void setKDFVersion(final int vers) {
        CryptoHelper.isValidKDFVersion(vers, false, true);
        this.kdfVersion_ = vers;
    }
    
    public KeyDerivationFunction.PRF_ALGORITHMS getKDF_PRF() {
        return KeyDerivationFunction.convertIntToPRF(this.kdfPrfSelection_);
    }
    
    int kdfPRFAsInt() {
        return this.kdfPrfSelection_;
    }
    
    public void setKDF_PRF(final int prfSelection) {
        assert prfSelection >= 0 && prfSelection <= 15 : "kdfPrf == " + prfSelection + " must be between 0 and 15.";
        if (prfSelection < 0 || prfSelection > 15) {
            throw new IllegalArgumentException("kdfPrf == " + prfSelection + " must be between 0 and 15, inclusive.");
        }
        this.kdfPrfSelection_ = prfSelection;
    }
    
    public long getEncryptionTimestamp() {
        return this.encryption_timestamp_;
    }
    
    private void setEncryptionTimestamp() {
        if (this.encryption_timestamp_ != 0L) {
            CipherText.logger.warning(Logger.EVENT_FAILURE, "Attempt to reset non-zero CipherText encryption timestamp to current time!");
        }
        this.encryption_timestamp_ = System.currentTimeMillis();
    }
    
    void setEncryptionTimestamp(final long timestamp) {
        if (timestamp <= 0L) {
            throw new IllegalArgumentException("Timestamp must be greater than zero.");
        }
        if (this.encryption_timestamp_ == 0L) {
            CipherText.logger.warning(Logger.EVENT_FAILURE, "Attempt to reset non-zero CipherText encryption timestamp to " + new Date(timestamp) + "!");
        }
        this.encryption_timestamp_ = timestamp;
    }
    
    @Deprecated
    public static long getSerialVersionUID() {
        return 20130830L;
    }
    
    public byte[] getSeparateMAC() {
        if (this.separate_mac_ == null) {
            return null;
        }
        final byte[] copy = new byte[this.separate_mac_.length];
        System.arraycopy(this.separate_mac_, 0, copy, 0, this.separate_mac_.length);
        return copy;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CipherText: ");
        final String creationTime = (this.getEncryptionTimestamp() == 0L) ? "No timestamp available" : new Date(this.getEncryptionTimestamp()).toString();
        final int n = this.getRawCipherTextByteLength();
        final String rawCipherText = (n > 0) ? ("present (" + n + " bytes)") : "absent";
        final String mac = (this.separate_mac_ != null) ? "present" : "absent";
        sb.append("Creation time: ").append(creationTime);
        sb.append(", raw ciphertext is ").append(rawCipherText);
        sb.append(", MAC is ").append(mac).append("; ");
        sb.append(this.cipherSpec_.toString());
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object other) {
        boolean result = false;
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof CipherText) {
            final CipherText that = (CipherText)other;
            if (!this.collectedAll() || !that.collectedAll()) {
                CipherText.logger.warning(Logger.EVENT_FAILURE, "CipherText.equals(): Cannot compare two CipherText objects that are not complete, and therefore immutable!");
                CipherText.logger.info(Logger.EVENT_FAILURE, "This CipherText: " + this.collectedAll() + ";" + "other CipherText: " + that.collectedAll());
                CipherText.logger.info(Logger.EVENT_FAILURE, "CipherText.equals(): Progress comparison: " + ((this.progress == that.progress) ? "Same" : "Different"));
                CipherText.logger.info(Logger.EVENT_FAILURE, "CipherText.equals(): Status this: " + this.progress + "; status other CipherText object: " + that.progress);
                return false;
            }
            result = (that.canEqual(this) && this.cipherSpec_.equals(that.cipherSpec_) && CryptoHelper.arrayCompare(this.raw_ciphertext_, that.raw_ciphertext_) && CryptoHelper.arrayCompare(this.separate_mac_, that.separate_mac_) && this.encryption_timestamp_ == that.encryption_timestamp_);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        if (this.collectedAll()) {
            CipherText.logger.warning(Logger.EVENT_FAILURE, "CipherText.hashCode(): Cannot compute hachCode() of incomplete CipherText object; object not immutable- returning 0.");
            return 0;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.cipherSpec_.hashCode());
        sb.append(this.encryption_timestamp_);
        String raw_ct = null;
        String mac = null;
        try {
            raw_ct = new String(this.raw_ciphertext_, "UTF-8");
            mac = new String((this.separate_mac_ != null) ? this.separate_mac_ : new byte[0], "UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            raw_ct = new String(this.raw_ciphertext_);
            mac = new String((this.separate_mac_ != null) ? this.separate_mac_ : new byte[0]);
        }
        sb.append(raw_ct);
        sb.append(mac);
        return sb.toString().hashCode();
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof CipherText;
    }
    
    private byte[] computeMAC(final SecretKey authKey) {
        assert this.raw_ciphertext_ != null && this.raw_ciphertext_.length != 0 : "Raw ciphertext may not be null or empty.";
        assert authKey != null && authKey.getEncoded().length != 0 : "Authenticity secret key may not be null or zero length.";
        try {
            final SecretKey sk = new SecretKeySpec(authKey.getEncoded(), "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sk);
            if (this.requiresIV()) {
                mac.update(this.getIV());
            }
            final byte[] result = mac.doFinal(this.getRawCipherText());
            return result;
        }
        catch (final NoSuchAlgorithmException e) {
            CipherText.logger.error(Logger.SECURITY_FAILURE, "Cannot compute MAC w/out HmacSHA1.", e);
            return null;
        }
        catch (final InvalidKeyException e2) {
            CipherText.logger.error(Logger.SECURITY_FAILURE, "Cannot comput MAC; invalid 'key' for HmacSHA1.", e2);
            return null;
        }
    }
    
    private boolean macComputed() {
        return this.separate_mac_ != null;
    }
    
    private boolean collectedAll() {
        EnumSet<CipherTextFlags> ctFlags = null;
        if (this.requiresIV()) {
            ctFlags = this.allCtFlags;
        }
        else {
            final EnumSet<CipherTextFlags> initVector = EnumSet.of(CipherTextFlags.INITVECTOR);
            ctFlags = EnumSet.complementOf(initVector);
        }
        final boolean result = this.progress.containsAll(ctFlags);
        return result;
    }
    
    private boolean isCollected(final CipherTextFlags flag) {
        return this.progress.contains(flag);
    }
    
    private void received(final CipherTextFlags flag) {
        this.progress.add(flag);
    }
    
    private void received(final EnumSet<CipherTextFlags> ctSet) {
        final Iterator<CipherTextFlags> it = ctSet.iterator();
        while (it.hasNext()) {
            this.received(it.next());
        }
    }
    
    public int getKDFInfo() {
        final int unusedBit28 = 134217728;
        final int kdfVers = this.getKDFVersion();
        assert CryptoHelper.isValidKDFVersion(kdfVers, true, false);
        int kdfInfo = kdfVers;
        final int macAlg = this.kdfPRFAsInt();
        assert macAlg >= 0 && macAlg <= 15 : "MAC algorithm indicator must be between 0 to 15 inclusion; value is: " + macAlg;
        kdfInfo &= 0xF7FFFFFF;
        kdfInfo |= macAlg << 28;
        return kdfInfo;
    }
    
    static {
        logger = ESAPI.getLogger("CipherText");
    }
    
    private enum CipherTextFlags
    {
        ALGNAME, 
        CIPHERMODE, 
        PADDING, 
        KEYSIZE, 
        BLOCKSIZE, 
        CIPHERTEXT, 
        INITVECTOR;
    }
}
