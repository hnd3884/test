package com.unboundid.util;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import java.util.logging.Level;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.InvalidKeyException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import java.security.Key;
import javax.crypto.Mac;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public final class PassphraseEncryptedStreamHeader implements Serializable
{
    static final byte TYPE_ENCODING_VERSION = Byte.MIN_VALUE;
    static final byte TYPE_KEY_FACTORY_ALGORITHM = -127;
    static final byte TYPE_KEY_FACTORY_ITERATION_COUNT = -126;
    static final byte TYPE_KEY_FACTORY_SALT = -125;
    static final byte TYPE_KEY_FACTORY_KEY_LENGTH_BITS = -124;
    static final byte TYPE_CIPHER_TRANSFORMATION = -123;
    static final byte TYPE_CIPHER_INITIALIZATION_VECTOR = -122;
    static final byte TYPE_KEY_IDENTIFIER = -121;
    static final byte TYPE_MAC_ALGORITHM = -120;
    static final byte TYPE_MAC_VALUE = -119;
    public static final byte[] MAGIC_BYTES;
    static final int ENCODING_VERSION_1 = 1;
    private static final long serialVersionUID = 6756983626170064762L;
    private final byte[] cipherInitializationVector;
    private final byte[] encodedHeader;
    private final byte[] keyFactorySalt;
    private final byte[] macValue;
    private final int keyFactoryIterationCount;
    private final int keyFactoryKeyLengthBits;
    private final SecretKey secretKey;
    private final String cipherTransformation;
    private final String keyFactoryAlgorithm;
    private final String keyIdentifier;
    private final String macAlgorithm;
    
    private PassphraseEncryptedStreamHeader(final String keyFactoryAlgorithm, final int keyFactoryIterationCount, final byte[] keyFactorySalt, final int keyFactoryKeyLengthBits, final String cipherTransformation, final byte[] cipherInitializationVector, final String keyIdentifier, final SecretKey secretKey, final String macAlgorithm, final byte[] macValue, final byte[] encodedHeader) {
        this.keyFactoryAlgorithm = keyFactoryAlgorithm;
        this.keyFactoryIterationCount = keyFactoryIterationCount;
        this.keyFactorySalt = Arrays.copyOf(keyFactorySalt, keyFactorySalt.length);
        this.keyFactoryKeyLengthBits = keyFactoryKeyLengthBits;
        this.cipherTransformation = cipherTransformation;
        this.cipherInitializationVector = Arrays.copyOf(cipherInitializationVector, cipherInitializationVector.length);
        this.keyIdentifier = keyIdentifier;
        this.secretKey = secretKey;
        this.macAlgorithm = macAlgorithm;
        this.macValue = macValue;
        this.encodedHeader = encodedHeader;
    }
    
    PassphraseEncryptedStreamHeader(final char[] passphrase, final String keyFactoryAlgorithm, final int keyFactoryIterationCount, final byte[] keyFactorySalt, final int keyFactoryKeyLengthBits, final String cipherTransformation, final byte[] cipherInitializationVector, final String keyIdentifier, final String macAlgorithm) throws GeneralSecurityException {
        this.keyFactoryAlgorithm = keyFactoryAlgorithm;
        this.keyFactoryIterationCount = keyFactoryIterationCount;
        this.keyFactorySalt = Arrays.copyOf(keyFactorySalt, keyFactorySalt.length);
        this.keyFactoryKeyLengthBits = keyFactoryKeyLengthBits;
        this.cipherTransformation = cipherTransformation;
        this.cipherInitializationVector = Arrays.copyOf(cipherInitializationVector, cipherInitializationVector.length);
        this.keyIdentifier = keyIdentifier;
        this.macAlgorithm = macAlgorithm;
        this.secretKey = generateKeyReliably(keyFactoryAlgorithm, cipherTransformation, passphrase, keyFactorySalt, keyFactoryIterationCount, keyFactoryKeyLengthBits);
        final ObjectPair<byte[], byte[]> headerPair = encode(keyFactoryAlgorithm, keyFactoryIterationCount, this.keyFactorySalt, keyFactoryKeyLengthBits, cipherTransformation, this.cipherInitializationVector, keyIdentifier, this.secretKey, macAlgorithm);
        this.encodedHeader = headerPair.getFirst();
        this.macValue = headerPair.getSecond();
    }
    
    private static ObjectPair<byte[], byte[]> encode(final String keyFactoryAlgorithm, final int keyFactoryIterationCount, final byte[] keyFactorySalt, final int keyFactoryKeyLengthBits, final String cipherTransformation, final byte[] cipherInitializationVector, final String keyIdentifier, final SecretKey secretKey, final String macAlgorithm) throws GeneralSecurityException {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(10);
        elements.add(new ASN1Integer((byte)(-128), 1));
        elements.add(new ASN1OctetString((byte)(-127), keyFactoryAlgorithm));
        elements.add(new ASN1Integer((byte)(-126), keyFactoryIterationCount));
        elements.add(new ASN1OctetString((byte)(-125), keyFactorySalt));
        elements.add(new ASN1Integer((byte)(-124), keyFactoryKeyLengthBits));
        elements.add(new ASN1OctetString((byte)(-123), cipherTransformation));
        elements.add(new ASN1OctetString((byte)(-122), cipherInitializationVector));
        if (keyIdentifier != null) {
            elements.add(new ASN1OctetString((byte)(-121), keyIdentifier));
        }
        elements.add(new ASN1OctetString((byte)(-120), macAlgorithm));
        final ByteStringBuffer macBuffer = new ByteStringBuffer();
        for (final ASN1Element e : elements) {
            macBuffer.append(e.encode());
        }
        final Mac mac = Mac.getInstance(macAlgorithm);
        mac.init(secretKey);
        final byte[] macValue = mac.doFinal(macBuffer.toByteArray());
        elements.add(new ASN1OctetString((byte)(-119), macValue));
        final byte[] elementBytes = new ASN1Sequence(elements).encode();
        final byte[] headerBytes = new byte[PassphraseEncryptedStreamHeader.MAGIC_BYTES.length + elementBytes.length];
        System.arraycopy(PassphraseEncryptedStreamHeader.MAGIC_BYTES, 0, headerBytes, 0, PassphraseEncryptedStreamHeader.MAGIC_BYTES.length);
        System.arraycopy(elementBytes, 0, headerBytes, PassphraseEncryptedStreamHeader.MAGIC_BYTES.length, elementBytes.length);
        return new ObjectPair<byte[], byte[]>(headerBytes, macValue);
    }
    
    public void writeTo(final OutputStream outputStream) throws IOException {
        outputStream.write(this.encodedHeader);
    }
    
    public static PassphraseEncryptedStreamHeader readFrom(final InputStream inputStream, final char[] passphrase) throws IOException, LDAPException, InvalidKeyException, GeneralSecurityException {
        for (int i = 0; i < PassphraseEncryptedStreamHeader.MAGIC_BYTES.length; ++i) {
            final int magicByte = inputStream.read();
            if (magicByte < 0) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_READ_END_OF_STREAM_IN_MAGIC.get());
            }
            if (magicByte != PassphraseEncryptedStreamHeader.MAGIC_BYTES[i]) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_READ_MAGIC_MISMATCH.get());
            }
        }
        try {
            final ASN1Element headerSequenceElement = ASN1Element.readFrom(inputStream);
            if (headerSequenceElement == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_READ_END_OF_STREAM_AFTER_MAGIC.get());
            }
            final byte[] encodedHeaderSequence = headerSequenceElement.encode();
            final byte[] encodedHeader = new byte[PassphraseEncryptedStreamHeader.MAGIC_BYTES.length + encodedHeaderSequence.length];
            System.arraycopy(PassphraseEncryptedStreamHeader.MAGIC_BYTES, 0, encodedHeader, 0, PassphraseEncryptedStreamHeader.MAGIC_BYTES.length);
            System.arraycopy(encodedHeaderSequence, 0, encodedHeader, PassphraseEncryptedStreamHeader.MAGIC_BYTES.length, encodedHeaderSequence.length);
            final ASN1Sequence headerSequence = ASN1Sequence.decodeAsSequence(headerSequenceElement);
            return decodeHeaderSequence(encodedHeader, headerSequence, passphrase);
        }
        catch (final IOException | LDAPException | GeneralSecurityException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_READ_ASN1_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static PassphraseEncryptedStreamHeader decode(final byte[] encodedHeader, final char[] passphrase) throws LDAPException, InvalidKeyException, GeneralSecurityException {
        if (encodedHeader.length <= PassphraseEncryptedStreamHeader.MAGIC_BYTES.length) {
            throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_DECODE_TOO_SHORT.get());
        }
        for (int i = 0; i < PassphraseEncryptedStreamHeader.MAGIC_BYTES.length; ++i) {
            if (encodedHeader[i] != PassphraseEncryptedStreamHeader.MAGIC_BYTES[i]) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_DECODE_MAGIC_MISMATCH.get());
            }
        }
        ASN1Sequence headerSequence;
        try {
            final byte[] encodedHeaderWithoutMagic = new byte[encodedHeader.length - PassphraseEncryptedStreamHeader.MAGIC_BYTES.length];
            System.arraycopy(encodedHeader, PassphraseEncryptedStreamHeader.MAGIC_BYTES.length, encodedHeaderWithoutMagic, 0, encodedHeaderWithoutMagic.length);
            headerSequence = ASN1Sequence.decodeAsSequence(encodedHeaderWithoutMagic);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_DECODE_ASN1_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
        return decodeHeaderSequence(encodedHeader, headerSequence, passphrase);
    }
    
    private static PassphraseEncryptedStreamHeader decodeHeaderSequence(final byte[] encodedHeader, final ASN1Sequence headerSequence, final char[] passphrase) throws LDAPException, InvalidKeyException, GeneralSecurityException {
        try {
            final ASN1Element[] headerElements = headerSequence.elements();
            final ASN1Integer versionElement = ASN1Integer.decodeAsInteger(headerElements[0]);
            if (versionElement.intValue() != 1) {
                throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_HEADER_SEQUENCE_UNSUPPORTED_VERSION.get(versionElement.intValue()));
            }
            final String keyFactoryAlgorithm = ASN1OctetString.decodeAsOctetString(headerElements[1]).stringValue();
            final int keyFactoryIterationCount = ASN1Integer.decodeAsInteger(headerElements[2]).intValue();
            final byte[] keyFactorySalt = ASN1OctetString.decodeAsOctetString(headerElements[3]).getValue();
            final int keyFactoryKeyLengthBits = ASN1Integer.decodeAsInteger(headerElements[4]).intValue();
            final String cipherTransformation = ASN1OctetString.decodeAsOctetString(headerElements[5]).stringValue();
            final byte[] cipherInitializationVector = ASN1OctetString.decodeAsOctetString(headerElements[6]).getValue();
            byte[] macValue = null;
            int macValuePos = -1;
            String keyIdentifier = null;
            String macAlgorithm = null;
            for (int i = 7; i < headerElements.length; ++i) {
                switch (headerElements[i].getType()) {
                    case -121: {
                        keyIdentifier = ASN1OctetString.decodeAsOctetString(headerElements[i]).stringValue();
                        break;
                    }
                    case -120: {
                        macAlgorithm = ASN1OctetString.decodeAsOctetString(headerElements[i]).stringValue();
                        break;
                    }
                    case -119: {
                        macValuePos = i;
                        macValue = ASN1OctetString.decodeAsOctetString(headerElements[i]).getValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_HEADER_SEQUENCE_UNRECOGNIZED_ELEMENT_TYPE.get(StaticUtils.toHex(headerElements[i].getType())));
                    }
                }
            }
            SecretKey secretKey;
            if (passphrase == null) {
                secretKey = null;
            }
            else {
                secretKey = generateKeyReliably(keyFactoryAlgorithm, cipherTransformation, passphrase, keyFactorySalt, keyFactoryIterationCount, keyFactoryKeyLengthBits);
                final ByteStringBuffer macBuffer = new ByteStringBuffer();
                for (int j = 0; j < headerElements.length; ++j) {
                    if (j != macValuePos) {
                        macBuffer.append(headerElements[j].encode());
                    }
                }
                final Mac mac = Mac.getInstance(macAlgorithm);
                mac.init(secretKey);
                final byte[] computedMacValue = mac.doFinal(macBuffer.toByteArray());
                if (!Arrays.equals(computedMacValue, macValue)) {
                    throw new InvalidKeyException(UtilityMessages.ERR_PW_ENCRYPTED_HEADER_SEQUENCE_BAD_PW.get());
                }
            }
            return new PassphraseEncryptedStreamHeader(keyFactoryAlgorithm, keyFactoryIterationCount, keyFactorySalt, keyFactoryKeyLengthBits, cipherTransformation, cipherInitializationVector, keyIdentifier, secretKey, macAlgorithm, macValue, encodedHeader);
        }
        catch (final LDAPException | GeneralSecurityException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, UtilityMessages.ERR_PW_ENCRYPTED_HEADER_SEQUENCE_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static SecretKey generateKeyReliably(final String keyFactoryAlgorithm, final String cipherTransformation, final char[] passphrase, final byte[] keyFactorySalt, final int keyFactoryIterationCount, final int keyFactoryKeyLengthBits) throws GeneralSecurityException {
        byte[] prev = null;
        byte[] prev2 = null;
        final int iterations = 10;
        for (int i = 0; i < 10; ++i) {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(keyFactoryAlgorithm);
            final String cipherAlgorithm = cipherTransformation.substring(0, cipherTransformation.indexOf(47));
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase, keyFactorySalt, keyFactoryIterationCount, keyFactoryKeyLengthBits);
            final SecretKey secretKey = new SecretKeySpec(keyFactory.generateSecret(pbeKeySpec).getEncoded(), cipherAlgorithm);
            final byte[] encoded = secretKey.getEncoded();
            if (Arrays.equals(encoded, prev) && Arrays.equals(encoded, prev2)) {
                if (i > 2) {
                    Debug.debug(Level.WARNING, DebugType.OTHER, "The secret key was generated inconsistently initially, but after " + i + " iterations, we were able to generate a " + "consistent value.");
                }
                return secretKey;
            }
            prev2 = prev;
            prev = encoded;
        }
        Debug.debug(Level.SEVERE, DebugType.OTHER, "Even after 10 iterations, the secret key could not be reliably generated.");
        throw new InvalidKeyException(UtilityMessages.ERR_PW_ENCRYPTED_STREAM_HEADER_CANNOT_GENERATE_KEY.get());
    }
    
    Cipher createCipher(final int mode) throws InvalidKeyException, GeneralSecurityException {
        if (this.secretKey == null) {
            throw new InvalidKeyException(UtilityMessages.ERR_PW_ENCRYPTED_HEADER_NO_KEY_AVAILABLE.get());
        }
        final Cipher cipher = Cipher.getInstance(this.cipherTransformation);
        cipher.init(mode, this.secretKey, new IvParameterSpec(this.cipherInitializationVector));
        return cipher;
    }
    
    public String getKeyFactoryAlgorithm() {
        return this.keyFactoryAlgorithm;
    }
    
    public int getKeyFactoryIterationCount() {
        return this.keyFactoryIterationCount;
    }
    
    public byte[] getKeyFactorySalt() {
        return Arrays.copyOf(this.keyFactorySalt, this.keyFactorySalt.length);
    }
    
    public int getKeyFactoryKeyLengthBits() {
        return this.keyFactoryKeyLengthBits;
    }
    
    public String getCipherTransformation() {
        return this.cipherTransformation;
    }
    
    public byte[] getCipherInitializationVector() {
        return Arrays.copyOf(this.cipherInitializationVector, this.cipherInitializationVector.length);
    }
    
    public String getKeyIdentifier() {
        return this.keyIdentifier;
    }
    
    public String getMACAlgorithm() {
        return this.macAlgorithm;
    }
    
    public byte[] getEncodedHeader() {
        return Arrays.copyOf(this.encodedHeader, this.encodedHeader.length);
    }
    
    public boolean isSecretKeyAvailable() {
        return this.secretKey != null;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PassphraseEncryptedStreamHeader(keyFactoryAlgorithm='");
        buffer.append(this.keyFactoryAlgorithm);
        buffer.append("', keyFactoryIterationCount=");
        buffer.append(this.keyFactoryIterationCount);
        buffer.append(", keyFactorySaltLengthBytes=");
        buffer.append(this.keyFactorySalt.length);
        buffer.append(", keyFactoryKeyLengthBits=");
        buffer.append(this.keyFactoryKeyLengthBits);
        buffer.append(", cipherTransformation'=");
        buffer.append(this.cipherTransformation);
        buffer.append("', cipherInitializationVectorLengthBytes=");
        buffer.append(this.cipherInitializationVector.length);
        buffer.append('\'');
        if (this.keyIdentifier != null) {
            buffer.append(", keyIdentifier='");
            buffer.append(this.keyIdentifier);
            buffer.append('\'');
        }
        buffer.append(", macAlgorithm='");
        buffer.append(this.macAlgorithm);
        buffer.append("', macValueLengthBytes=");
        buffer.append(this.macValue.length);
        buffer.append(", secretKeyAvailable=");
        buffer.append(this.isSecretKeyAvailable());
        buffer.append(", encodedHeaderLengthBytes=");
        buffer.append(this.encodedHeader.length);
        buffer.append(')');
    }
    
    static {
        MAGIC_BYTES = new byte[] { 80, 85, 76, 83, 80, 69, 83, 72 };
    }
}
