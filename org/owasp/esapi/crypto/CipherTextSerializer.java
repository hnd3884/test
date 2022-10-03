package org.owasp.esapi.crypto;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.util.ByteConversionUtil;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.Logger;

public class CipherTextSerializer
{
    public static final int cipherTextSerializerVersion = 20130830;
    private static final long serialVersionUID = 20130830L;
    private static final Logger logger;
    private CipherText cipherText_;
    
    public CipherTextSerializer(final CipherText cipherTextObj) {
        this.cipherText_ = null;
        if (cipherTextObj == null) {
            throw new IllegalArgumentException("CipherText object must not be null.");
        }
        assert cipherTextObj != null : "CipherText object must not be null.";
        this.cipherText_ = cipherTextObj;
    }
    
    public CipherTextSerializer(final byte[] cipherTextSerializedBytes) throws EncryptionException {
        this.cipherText_ = null;
        this.cipherText_ = this.convertToCipherText(cipherTextSerializedBytes);
    }
    
    public byte[] asSerializedByteArray() {
        final int kdfInfo = this.cipherText_.getKDFInfo();
        this.debug("asSerializedByteArray: kdfInfo = " + kdfInfo);
        final long timestamp = this.cipherText_.getEncryptionTimestamp();
        final String cipherXform = this.cipherText_.getCipherTransformation();
        assert this.cipherText_.getKeySize() < 32767 : "Key size too large. Max is 32767";
        final short keySize = (short)this.cipherText_.getKeySize();
        assert this.cipherText_.getBlockSize() < 32767 : "Block size too large. Max is 32767";
        final short blockSize = (short)this.cipherText_.getBlockSize();
        final byte[] iv = this.cipherText_.getIV();
        assert iv.length < 32767 : "IV size too large. Max is 32767";
        final short ivLen = (short)iv.length;
        final byte[] rawCiphertext = this.cipherText_.getRawCipherText();
        final int ciphertextLen = rawCiphertext.length;
        assert ciphertextLen >= 1 : "Raw ciphertext length must be >= 1 byte.";
        final byte[] mac = this.cipherText_.getSeparateMAC();
        assert mac.length < 32767 : "MAC length too large. Max is 32767";
        final short macLen = (short)mac.length;
        final byte[] serializedObj = this.computeSerialization(kdfInfo, timestamp, cipherXform, keySize, blockSize, ivLen, iv, ciphertextLen, rawCiphertext, macLen, mac);
        return serializedObj;
    }
    
    public CipherText asCipherText() {
        assert this.cipherText_ != null;
        return this.cipherText_;
    }
    
    private byte[] computeSerialization(final int kdfInfo, final long timestamp, final String cipherXform, final short keySize, final short blockSize, final short ivLen, final byte[] iv, final int ciphertextLen, final byte[] rawCiphertext, final short macLen, final byte[] mac) {
        this.debug("computeSerialization: kdfInfo = " + kdfInfo);
        this.debug("computeSerialization: timestamp = " + new Date(timestamp));
        this.debug("computeSerialization: cipherXform = " + cipherXform);
        this.debug("computeSerialization: keySize = " + keySize);
        this.debug("computeSerialization: blockSize = " + blockSize);
        this.debug("computeSerialization: ivLen = " + ivLen);
        this.debug("computeSerialization: ciphertextLen = " + ciphertextLen);
        this.debug("computeSerialization: macLen = " + macLen);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.writeInt(baos, kdfInfo);
        this.writeLong(baos, timestamp);
        final String[] parts = cipherXform.split("/");
        assert parts.length == 3 : "Malformed cipher transformation";
        this.writeString(baos, cipherXform);
        this.writeShort(baos, keySize);
        this.writeShort(baos, blockSize);
        this.writeShort(baos, ivLen);
        if (ivLen > 0) {
            baos.write(iv, 0, iv.length);
        }
        this.writeInt(baos, ciphertextLen);
        baos.write(rawCiphertext, 0, rawCiphertext.length);
        this.writeShort(baos, macLen);
        if (macLen > 0) {
            baos.write(mac, 0, mac.length);
        }
        return baos.toByteArray();
    }
    
    private void writeString(final ByteArrayOutputStream baos, final String str) {
        try {
            assert str != null && str.length() > 0;
            final byte[] bytes = str.getBytes("UTF8");
            assert bytes.length < 32767 : "writeString: String exceeds max length";
            this.writeShort(baos, (short)bytes.length);
            baos.write(bytes, 0, bytes.length);
        }
        catch (final UnsupportedEncodingException e) {
            CipherTextSerializer.logger.error(Logger.EVENT_FAILURE, "Ignoring caught UnsupportedEncodingException converting string to UTF8 encoding. Results suspect. Corrupt rt.jar????");
        }
    }
    
    private String readString(final ByteArrayInputStream bais, final short sz) throws NullPointerException, IOException {
        final byte[] bytes = new byte[sz];
        final int ret = bais.read(bytes, 0, sz);
        assert ret == sz : "readString: Failed to read " + sz + " bytes.";
        return new String(bytes, "UTF8");
    }
    
    private void writeShort(final ByteArrayOutputStream baos, final short s) {
        final byte[] shortAsByteArray = ByteConversionUtil.fromShort(s);
        assert shortAsByteArray.length == 2;
        baos.write(shortAsByteArray, 0, 2);
    }
    
    private short readShort(final ByteArrayInputStream bais) throws NullPointerException, IndexOutOfBoundsException {
        final byte[] shortAsByteArray = new byte[2];
        final int ret = bais.read(shortAsByteArray, 0, 2);
        assert ret == 2 : "readShort: Failed to read 2 bytes.";
        return ByteConversionUtil.toShort(shortAsByteArray);
    }
    
    private void writeInt(final ByteArrayOutputStream baos, final int i) {
        final byte[] intAsByteArray = ByteConversionUtil.fromInt(i);
        baos.write(intAsByteArray, 0, 4);
    }
    
    private int readInt(final ByteArrayInputStream bais) throws NullPointerException, IndexOutOfBoundsException {
        final byte[] intAsByteArray = new byte[4];
        final int ret = bais.read(intAsByteArray, 0, 4);
        assert ret == 4 : "readInt: Failed to read 4 bytes.";
        return ByteConversionUtil.toInt(intAsByteArray);
    }
    
    private void writeLong(final ByteArrayOutputStream baos, final long l) {
        final byte[] longAsByteArray = ByteConversionUtil.fromLong(l);
        assert longAsByteArray.length == 8;
        baos.write(longAsByteArray, 0, 8);
    }
    
    private long readLong(final ByteArrayInputStream bais) throws NullPointerException, IndexOutOfBoundsException {
        final byte[] longAsByteArray = new byte[8];
        final int ret = bais.read(longAsByteArray, 0, 8);
        assert ret == 8 : "readLong: Failed to read 8 bytes.";
        return ByteConversionUtil.toLong(longAsByteArray);
    }
    
    private CipherText convertToCipherText(final byte[] cipherTextSerializedBytes) throws EncryptionException {
        try {
            assert cipherTextSerializedBytes != null : "cipherTextSerializedBytes cannot be null.";
            assert cipherTextSerializedBytes.length > 0 : "cipherTextSerializedBytes must be > 0 in length.";
            final ByteArrayInputStream bais = new ByteArrayInputStream(cipherTextSerializedBytes);
            final int kdfInfo = this.readInt(bais);
            this.debug("kdfInfo: " + kdfInfo);
            final int kdfPrf = kdfInfo >>> 28;
            this.debug("kdfPrf: " + kdfPrf);
            assert kdfPrf >= 0 && kdfPrf <= 15 : "kdfPrf == " + kdfPrf + " must be between 0 and 15.";
            final int kdfVers = kdfInfo & 0x7FFFFFF;
            if (!CryptoHelper.isValidKDFVersion(kdfVers, false, false)) {
                final String logMsg = "KDF version read from serialized ciphertext (" + kdfVers + ") is out of range. " + "Valid range for KDF version is [" + 20110203 + ", " + "99991231].";
                throw new EncryptionException("Version info from serialized ciphertext not in valid range.", "Likely tampering with KDF version on serialized ciphertext." + logMsg);
            }
            this.debug("convertToCipherText: kdfPrf = " + kdfPrf + ", kdfVers = " + kdfVers);
            if (!versionIsCompatible(kdfVers)) {
                throw new EncryptionException("This version of ESAPI is not compatible with the version of ESAPI that encrypted your data.", "KDF version " + kdfVers + " from serialized ciphertext not compatibile with current KDF version of " + 20130830);
            }
            final long timestamp = this.readLong(bais);
            this.debug("convertToCipherText: timestamp = " + new Date(timestamp));
            final short strSize = this.readShort(bais);
            this.debug("convertToCipherText: length of cipherXform = " + strSize);
            final String cipherXform = this.readString(bais, strSize);
            this.debug("convertToCipherText: cipherXform = " + cipherXform);
            final String[] parts = cipherXform.split("/");
            assert parts.length == 3 : "Malformed cipher transformation";
            final String cipherMode = parts[1];
            if (!CryptoHelper.isAllowedCipherMode(cipherMode)) {
                final String msg = "Cipher mode " + cipherMode + " is not an allowed cipher mode";
                throw new EncryptionException(msg, msg);
            }
            final short keySize = this.readShort(bais);
            this.debug("convertToCipherText: keySize = " + keySize);
            final short blockSize = this.readShort(bais);
            this.debug("convertToCipherText: blockSize = " + blockSize);
            final short ivLen = this.readShort(bais);
            this.debug("convertToCipherText: ivLen = " + ivLen);
            byte[] iv = null;
            if (ivLen > 0) {
                iv = new byte[ivLen];
                bais.read(iv, 0, iv.length);
            }
            final int ciphertextLen = this.readInt(bais);
            this.debug("convertToCipherText: ciphertextLen = " + ciphertextLen);
            assert ciphertextLen > 0 : "convertToCipherText: Invalid cipher text length";
            final byte[] rawCiphertext = new byte[ciphertextLen];
            bais.read(rawCiphertext, 0, rawCiphertext.length);
            final short macLen = this.readShort(bais);
            this.debug("convertToCipherText: macLen = " + macLen);
            byte[] mac = null;
            if (macLen > 0) {
                mac = new byte[macLen];
                bais.read(mac, 0, mac.length);
            }
            final CipherSpec cipherSpec = new CipherSpec(cipherXform, keySize);
            cipherSpec.setBlockSize(blockSize);
            cipherSpec.setIV(iv);
            this.debug("convertToCipherText: CipherSpec: " + cipherSpec);
            final CipherText ct = new CipherText(cipherSpec);
            if (ivLen <= 0 || !ct.requiresIV()) {
                throw new EncryptionException("convertToCipherText: Mismatch between IV length and cipher mode.", "Possible tampering of serialized ciphertext?");
            }
            ct.setCiphertext(rawCiphertext);
            ct.setEncryptionTimestamp(timestamp);
            if (macLen > 0) {
                ct.storeSeparateMAC(mac);
            }
            ct.setKDF_PRF(kdfPrf);
            ct.setKDFVersion(kdfVers);
            return ct;
        }
        catch (final EncryptionException ex) {
            throw new EncryptionException("Cannot deserialize byte array into CipherText object", "Cannot deserialize byte array into CipherText object", ex);
        }
        catch (final IOException e) {
            throw new EncryptionException("Cannot deserialize byte array into CipherText object", "Cannot deserialize byte array into CipherText object", e);
        }
    }
    
    private static boolean versionIsCompatible(final int readKdfVers) {
        assert readKdfVers > 0 : "Extracted KDF version is negative!";
        switch (readKdfVers) {
            case 20110203: {
                return true;
            }
            case 20130830: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void debug(final String msg) {
        if (CipherTextSerializer.logger.isDebugEnabled()) {
            CipherTextSerializer.logger.debug(Logger.EVENT_SUCCESS, msg);
        }
    }
    
    static {
        logger = ESAPI.getLogger("CipherTextSerializer");
    }
}
