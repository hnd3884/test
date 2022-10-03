package com.sun.security.sasl.digest;

import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.logging.Logger;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import java.util.List;
import java.io.IOException;
import java.security.MessageDigest;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import javax.security.sasl.SaslException;
import java.util.Map;
import java.math.BigInteger;
import javax.security.auth.callback.CallbackHandler;
import com.sun.security.sasl.util.AbstractSaslImpl;

abstract class DigestMD5Base extends AbstractSaslImpl
{
    private static final String DI_CLASS_NAME;
    private static final String DP_CLASS_NAME;
    protected static final int MAX_CHALLENGE_LENGTH = 2048;
    protected static final int MAX_RESPONSE_LENGTH = 4096;
    protected static final int DEFAULT_MAXBUF = 65536;
    protected static final int DES3 = 0;
    protected static final int RC4 = 1;
    protected static final int DES = 2;
    protected static final int RC4_56 = 3;
    protected static final int RC4_40 = 4;
    protected static final String[] CIPHER_TOKENS;
    private static final String[] JCE_CIPHER_NAME;
    protected static final byte DES_3_STRENGTH = 4;
    protected static final byte RC4_STRENGTH = 4;
    protected static final byte DES_STRENGTH = 2;
    protected static final byte RC4_56_STRENGTH = 2;
    protected static final byte RC4_40_STRENGTH = 1;
    protected static final byte UNSET = 0;
    protected static final byte[] CIPHER_MASKS;
    private static final String SECURITY_LAYER_MARKER = ":00000000000000000000000000000000";
    protected static final byte[] EMPTY_BYTE_ARRAY;
    protected int step;
    protected CallbackHandler cbh;
    protected SecurityCtx secCtx;
    protected byte[] H_A1;
    protected byte[] nonce;
    protected String negotiatedStrength;
    protected String negotiatedCipher;
    protected String negotiatedQop;
    protected String negotiatedRealm;
    protected boolean useUTF8;
    protected String encoding;
    protected String digestUri;
    protected String authzid;
    private static final char[] pem_array;
    private static final int RAW_NONCE_SIZE = 30;
    private static final int ENCODED_NONCE_SIZE = 40;
    private static final BigInteger MASK;
    
    protected DigestMD5Base(final Map<String, ?> map, final String s, final int step, final String digestUri, final CallbackHandler cbh) throws SaslException {
        super(map, s);
        this.useUTF8 = false;
        this.encoding = "8859_1";
        this.step = step;
        this.digestUri = digestUri;
        this.cbh = cbh;
    }
    
    public String getMechanismName() {
        return "DIGEST-MD5";
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (!this.completed) {
            throw new IllegalStateException("DIGEST-MD5 authentication not completed");
        }
        if (this.secCtx == null) {
            throw new IllegalStateException("Neither integrity nor privacy was negotiated");
        }
        return this.secCtx.unwrap(array, n, n2);
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (!this.completed) {
            throw new IllegalStateException("DIGEST-MD5 authentication not completed");
        }
        if (this.secCtx == null) {
            throw new IllegalStateException("Neither integrity nor privacy was negotiated");
        }
        return this.secCtx.wrap(array, n, n2);
    }
    
    public void dispose() throws SaslException {
        if (this.secCtx != null) {
            this.secCtx = null;
        }
    }
    
    @Override
    public Object getNegotiatedProperty(final String s) {
        if (!this.completed) {
            throw new IllegalStateException("DIGEST-MD5 authentication not completed");
        }
        if (s.equals("javax.security.sasl.strength")) {
            return this.negotiatedStrength;
        }
        if (s.equals("javax.security.sasl.bound.server.name")) {
            return this.digestUri.substring(this.digestUri.indexOf(47) + 1);
        }
        return super.getNegotiatedProperty(s);
    }
    
    protected static final byte[] generateNonce() {
        final Random random = new Random();
        final byte[] array = new byte[30];
        random.nextBytes(array);
        final byte[] array2 = new byte[40];
        int n = 0;
        for (int i = 0; i < array.length; i += 3) {
            final byte b = array[i];
            final byte b2 = array[i + 1];
            final byte b3 = array[i + 2];
            array2[n++] = (byte)DigestMD5Base.pem_array[b >>> 2 & 0x3F];
            array2[n++] = (byte)DigestMD5Base.pem_array[(b << 4 & 0x30) + (b2 >>> 4 & 0xF)];
            array2[n++] = (byte)DigestMD5Base.pem_array[(b2 << 2 & 0x3C) + (b3 >>> 6 & 0x3)];
            array2[n++] = (byte)DigestMD5Base.pem_array[b3 & 0x3F];
        }
        return array2;
    }
    
    protected static void writeQuotedStringValue(final ByteArrayOutputStream byteArrayOutputStream, final byte[] array) {
        for (final byte b : array) {
            if (needEscape((char)b)) {
                byteArrayOutputStream.write(92);
            }
            byteArrayOutputStream.write(b);
        }
    }
    
    private static boolean needEscape(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (needEscape(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean needEscape(final char c) {
        return c == '\"' || c == '\\' || c == '\u007f' || (c >= '\0' && c <= '\u001f' && c != '\r' && c != '\t' && c != '\n');
    }
    
    protected static String quotedStringValue(final String s) {
        if (needEscape(s)) {
            final int length = s.length();
            final char[] array = new char[length + length];
            int n = 0;
            for (int i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                if (needEscape(char1)) {
                    array[n++] = '\\';
                }
                array[n++] = char1;
            }
            return new String(array, 0, n);
        }
        return s;
    }
    
    protected byte[] binaryToHex(final byte[] array) throws UnsupportedEncodingException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if ((array[i] & 0xFF) < 16) {
                sb.append("0" + Integer.toHexString(array[i] & 0xFF));
            }
            else {
                sb.append(Integer.toHexString(array[i] & 0xFF));
            }
        }
        return sb.toString().getBytes(this.encoding);
    }
    
    protected byte[] stringToByte_8859_1(final String s) throws SaslException {
        final char[] charArray = s.toCharArray();
        try {
            if (this.useUTF8) {
                for (int i = 0; i < charArray.length; ++i) {
                    if (charArray[i] > '\u00ff') {
                        return s.getBytes("UTF8");
                    }
                }
            }
            return s.getBytes("8859_1");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new SaslException("cannot encode string in UTF8 or 8859-1 (Latin-1)", ex);
        }
    }
    
    protected static byte[] getPlatformCiphers() {
        final byte[] array = new byte[DigestMD5Base.CIPHER_TOKENS.length];
        for (int i = 0; i < DigestMD5Base.JCE_CIPHER_NAME.length; ++i) {
            try {
                Cipher.getInstance(DigestMD5Base.JCE_CIPHER_NAME[i]);
                DigestMD5Base.logger.log(Level.FINE, "DIGEST01:Platform supports {0}", DigestMD5Base.JCE_CIPHER_NAME[i]);
                final byte[] array2 = array;
                final int n = i;
                array2[n] |= DigestMD5Base.CIPHER_MASKS[i];
            }
            catch (final NoSuchAlgorithmException ex) {}
            catch (final NoSuchPaddingException ex2) {}
        }
        if (array[1] != 0) {
            final byte[] array3 = array;
            final int n2 = 3;
            array3[n2] |= DigestMD5Base.CIPHER_MASKS[3];
            final byte[] array4 = array;
            final int n3 = 4;
            array4[n3] |= DigestMD5Base.CIPHER_MASKS[4];
        }
        return array;
    }
    
    protected byte[] generateResponseValue(final String s, final String s2, final String s3, final String s4, final String s5, final char[] array, final byte[] array2, final byte[] array3, final int n, final byte[] array4) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        final MessageDigest instance = MessageDigest.getInstance("MD5");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write((s + ":" + s2).getBytes(this.encoding));
        if (s3.equals("auth-conf") || s3.equals("auth-int")) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST04:QOP: {0}", s3);
            byteArrayOutputStream.write(":00000000000000000000000000000000".getBytes(this.encoding));
        }
        if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST05:A2: {0}", byteArrayOutputStream.toString());
        }
        instance.update(byteArrayOutputStream.toByteArray());
        final byte[] binaryToHex = this.binaryToHex(instance.digest());
        if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST06:HEX(H(A2)): {0}", new String(binaryToHex));
        }
        final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        byteArrayOutputStream2.write(this.stringToByte_8859_1(s4));
        byteArrayOutputStream2.write(58);
        byteArrayOutputStream2.write(this.stringToByte_8859_1(s5));
        byteArrayOutputStream2.write(58);
        byteArrayOutputStream2.write(this.stringToByte_8859_1(new String(array)));
        instance.update(byteArrayOutputStream2.toByteArray());
        final byte[] digest = instance.digest();
        if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST07:H({0}) = {1}", new Object[] { byteArrayOutputStream2.toString(), new String(this.binaryToHex(digest)) });
        }
        final ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
        byteArrayOutputStream3.write(digest);
        byteArrayOutputStream3.write(58);
        byteArrayOutputStream3.write(array2);
        byteArrayOutputStream3.write(58);
        byteArrayOutputStream3.write(array3);
        if (array4 != null) {
            byteArrayOutputStream3.write(58);
            byteArrayOutputStream3.write(array4);
        }
        instance.update(byteArrayOutputStream3.toByteArray());
        final byte[] digest2 = instance.digest();
        this.H_A1 = digest2;
        final byte[] binaryToHex2 = this.binaryToHex(digest2);
        if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST08:H(A1) = {0}", new String(binaryToHex2));
        }
        final ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream();
        byteArrayOutputStream4.write(binaryToHex2);
        byteArrayOutputStream4.write(58);
        byteArrayOutputStream4.write(array2);
        byteArrayOutputStream4.write(58);
        byteArrayOutputStream4.write(nonceCountToHex(n).getBytes(this.encoding));
        byteArrayOutputStream4.write(58);
        byteArrayOutputStream4.write(array3);
        byteArrayOutputStream4.write(58);
        byteArrayOutputStream4.write(s3.getBytes(this.encoding));
        byteArrayOutputStream4.write(58);
        byteArrayOutputStream4.write(binaryToHex);
        if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST09:KD: {0}", byteArrayOutputStream4.toString());
        }
        instance.update(byteArrayOutputStream4.toByteArray());
        final byte[] binaryToHex3 = this.binaryToHex(instance.digest());
        if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
            DigestMD5Base.logger.log(Level.FINE, "DIGEST10:response-value: {0}", new String(binaryToHex3));
        }
        return binaryToHex3;
    }
    
    protected static String nonceCountToHex(final int n) {
        final String hexString = Integer.toHexString(n);
        final StringBuffer sb = new StringBuffer();
        if (hexString.length() < 8) {
            for (int i = 0; i < 8 - hexString.length(); ++i) {
                sb.append("0");
            }
        }
        return sb.toString() + hexString;
    }
    
    protected static byte[][] parseDirectives(final byte[] array, final String[] array2, final List<byte[]> list, final int n) throws SaslException {
        final byte[][] array3 = new byte[array2.length][];
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
        final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream(10);
        int n2 = 1;
        int n3 = 0;
        int n4 = 0;
        int i = skipLws(array, 0);
        while (i < array.length) {
            final byte b = array[i];
            if (n2 != 0) {
                if (b == 44) {
                    if (byteArrayOutputStream.size() != 0) {
                        throw new SaslException("Directive key contains a ',':" + byteArrayOutputStream);
                    }
                    i = skipLws(array, i + 1);
                }
                else if (b == 61) {
                    if (byteArrayOutputStream.size() == 0) {
                        throw new SaslException("Empty directive key");
                    }
                    n2 = 0;
                    i = skipLws(array, i + 1);
                    if (i >= array.length) {
                        throw new SaslException("Valueless directive found: " + byteArrayOutputStream.toString());
                    }
                    if (array[i] != 34) {
                        continue;
                    }
                    n3 = 1;
                    ++i;
                }
                else if (isLws(b)) {
                    i = skipLws(array, i + 1);
                    if (i >= array.length) {
                        throw new SaslException("'=' expected after key: " + byteArrayOutputStream.toString());
                    }
                    if (array[i] != 61) {
                        throw new SaslException("'=' expected after key: " + byteArrayOutputStream.toString());
                    }
                    continue;
                }
                else {
                    byteArrayOutputStream.write(b);
                    ++i;
                }
            }
            else if (n3 != 0) {
                if (b == 92) {
                    if (++i >= array.length) {
                        throw new SaslException("Unmatched quote found for directive: " + byteArrayOutputStream.toString() + " with value: " + byteArrayOutputStream2.toString());
                    }
                    byteArrayOutputStream2.write(array[i]);
                    ++i;
                }
                else if (b == 34) {
                    ++i;
                    n3 = 0;
                    n4 = 1;
                }
                else {
                    byteArrayOutputStream2.write(b);
                    ++i;
                }
            }
            else if (isLws(b) || b == 44) {
                extractDirective(byteArrayOutputStream.toString(), byteArrayOutputStream2.toByteArray(), array2, array3, list, n);
                byteArrayOutputStream.reset();
                byteArrayOutputStream2.reset();
                n2 = 1;
                n4 = (n3 = 0);
                i = skipLws(array, i + 1);
            }
            else {
                if (n4 != 0) {
                    throw new SaslException("Expecting comma or linear whitespace after quoted string: \"" + byteArrayOutputStream2.toString() + "\"");
                }
                byteArrayOutputStream2.write(b);
                ++i;
            }
        }
        if (n3 != 0) {
            throw new SaslException("Unmatched quote found for directive: " + byteArrayOutputStream.toString() + " with value: " + byteArrayOutputStream2.toString());
        }
        if (byteArrayOutputStream.size() > 0) {
            extractDirective(byteArrayOutputStream.toString(), byteArrayOutputStream2.toByteArray(), array2, array3, list, n);
        }
        return array3;
    }
    
    private static boolean isLws(final byte b) {
        switch (b) {
            case 9:
            case 10:
            case 13:
            case 32: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static int skipLws(final byte[] array, final int n) {
        int i;
        for (i = n; i < array.length; ++i) {
            if (!isLws(array[i])) {
                return i;
            }
        }
        return i;
    }
    
    private static void extractDirective(final String s, final byte[] array, final String[] array2, final byte[][] array3, final List<byte[]> list, final int n) throws SaslException {
        int i = 0;
        while (i < array2.length) {
            if (s.equalsIgnoreCase(array2[i])) {
                if (array3[i] == null) {
                    array3[i] = array;
                    if (DigestMD5Base.logger.isLoggable(Level.FINE)) {
                        DigestMD5Base.logger.log(Level.FINE, "DIGEST11:Directive {0} = {1}", new Object[] { array2[i], new String(array3[i]) });
                        break;
                    }
                    break;
                }
                else {
                    if (list != null && i == n) {
                        if (list.isEmpty()) {
                            list.add(array3[i]);
                        }
                        list.add(array);
                        break;
                    }
                    throw new SaslException("DIGEST-MD5: peer sent more than one " + s + " directive: " + new String(array));
                }
            }
            else {
                ++i;
            }
        }
    }
    
    private static void setParityBit(final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFE;
            array[i] = (byte)(n | ((Integer.bitCount(n) & 0x1) ^ 0x1));
        }
    }
    
    private static byte[] addDesParity(final byte[] array, final int n, final int n2) {
        if (n2 != 7) {
            throw new IllegalArgumentException("Invalid length of DES Key Value:" + n2);
        }
        final byte[] array2 = new byte[7];
        System.arraycopy(array, n, array2, 0, n2);
        final byte[] parityBit = new byte[8];
        BigInteger shiftRight = new BigInteger(array2);
        for (int i = parityBit.length - 1; i >= 0; --i) {
            parityBit[i] = shiftRight.and(DigestMD5Base.MASK).toByteArray()[0];
            final byte[] array3 = parityBit;
            final int n3 = i;
            array3[n3] <<= 1;
            shiftRight = shiftRight.shiftRight(7);
        }
        setParityBit(parityBit);
        return parityBit;
    }
    
    private static SecretKey makeDesKeys(final byte[] array, final String s) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        final byte[] addDesParity = addDesParity(array, 0, 7);
        final SecretKeyFactory instance = SecretKeyFactory.getInstance(s);
        KeySpec keySpec = null;
        switch (s) {
            case "des": {
                keySpec = new DESKeySpec(addDesParity, 0);
                if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                    AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "makeDesKeys", "DIGEST42:DES key input: ", array);
                    AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "makeDesKeys", "DIGEST43:DES key parity-adjusted: ", addDesParity);
                    AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "makeDesKeys", "DIGEST44:DES key material: ", ((DESKeySpec)keySpec).getKey());
                    DigestMD5Base.logger.log(Level.FINEST, "DIGEST45: is parity-adjusted? {0}", DESKeySpec.isParityAdjusted(addDesParity, 0));
                    break;
                }
                break;
            }
            case "desede": {
                final byte[] addDesParity2 = addDesParity(array, 7, 7);
                final byte[] array2 = new byte[addDesParity.length * 2 + addDesParity2.length];
                System.arraycopy(addDesParity, 0, array2, 0, addDesParity.length);
                System.arraycopy(addDesParity2, 0, array2, addDesParity.length, addDesParity2.length);
                System.arraycopy(addDesParity, 0, array2, addDesParity.length + addDesParity2.length, addDesParity.length);
                keySpec = new DESedeKeySpec(array2, 0);
                if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                    AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "makeDesKeys", "DIGEST46:3DES key input: ", array);
                    AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "makeDesKeys", "DIGEST47:3DES key ede: ", array2);
                    AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "makeDesKeys", "DIGEST48:3DES key material: ", ((DESedeKeySpec)keySpec).getKey());
                    DigestMD5Base.logger.log(Level.FINEST, "DIGEST49: is parity-adjusted? ", DESedeKeySpec.isParityAdjusted(array2, 0));
                    break;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid DES strength:" + s);
            }
        }
        return instance.generateSecret(keySpec);
    }
    
    static {
        DI_CLASS_NAME = DigestIntegrity.class.getName();
        DP_CLASS_NAME = DigestPrivacy.class.getName();
        CIPHER_TOKENS = new String[] { "3des", "rc4", "des", "rc4-56", "rc4-40" };
        JCE_CIPHER_NAME = new String[] { "DESede/CBC/NoPadding", "RC4", "DES/CBC/NoPadding" };
        CIPHER_MASKS = new byte[] { 4, 4, 2, 2, 1 };
        EMPTY_BYTE_ARRAY = new byte[0];
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        MASK = new BigInteger("7f", 16);
    }
    
    class DigestIntegrity implements SecurityCtx
    {
        private static final String CLIENT_INT_MAGIC = "Digest session key to client-to-server signing key magic constant";
        private static final String SVR_INT_MAGIC = "Digest session key to server-to-client signing key magic constant";
        protected byte[] myKi;
        protected byte[] peerKi;
        protected int mySeqNum;
        protected int peerSeqNum;
        protected final byte[] messageType;
        protected final byte[] sequenceNum;
        
        DigestIntegrity(final boolean b) throws SaslException {
            this.mySeqNum = 0;
            this.peerSeqNum = 0;
            this.messageType = new byte[2];
            this.sequenceNum = new byte[4];
            try {
                this.generateIntegrityKeyPair(b);
            }
            catch (final UnsupportedEncodingException ex) {
                throw new SaslException("DIGEST-MD5: Error encoding strings into UTF-8", ex);
            }
            catch (final IOException ex2) {
                throw new SaslException("DIGEST-MD5: Error accessing buffers required to create integrity key pairs", ex2);
            }
            catch (final NoSuchAlgorithmException ex3) {
                throw new SaslException("DIGEST-MD5: Unsupported digest algorithm used to create integrity key pairs", ex3);
            }
            AbstractSaslImpl.intToNetworkByteOrder(1, this.messageType, 0, 2);
        }
        
        private void generateIntegrityKeyPair(final boolean b) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
            final byte[] bytes = "Digest session key to client-to-server signing key magic constant".getBytes(DigestMD5Base.this.encoding);
            final byte[] bytes2 = "Digest session key to server-to-client signing key magic constant".getBytes(DigestMD5Base.this.encoding);
            final MessageDigest instance = MessageDigest.getInstance("MD5");
            final byte[] array = new byte[DigestMD5Base.this.H_A1.length + bytes.length];
            System.arraycopy(DigestMD5Base.this.H_A1, 0, array, 0, DigestMD5Base.this.H_A1.length);
            System.arraycopy(bytes, 0, array, DigestMD5Base.this.H_A1.length, bytes.length);
            instance.update(array);
            final byte[] digest = instance.digest();
            System.arraycopy(bytes2, 0, array, DigestMD5Base.this.H_A1.length, bytes2.length);
            instance.update(array);
            final byte[] digest2 = instance.digest();
            if (DigestMD5Base.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST12:Kic: ", digest);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST13:Kis: ", digest2);
            }
            if (b) {
                this.myKi = digest;
                this.peerKi = digest2;
            }
            else {
                this.myKi = digest2;
                this.peerKi = digest;
            }
        }
        
        @Override
        public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
            if (n2 == 0) {
                return DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            final byte[] array2 = new byte[n2 + 10 + 2 + 4];
            System.arraycopy(array, n, array2, 0, n2);
            this.incrementSeqNum();
            final byte[] hmac = this.getHMAC(this.myKi, this.sequenceNum, array, n, n2);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST14:outgoing: ", array, n, n2);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST15:seqNum: ", this.sequenceNum);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST16:MAC: ", hmac);
            }
            System.arraycopy(hmac, 0, array2, n2, 10);
            System.arraycopy(this.messageType, 0, array2, n2 + 10, 2);
            System.arraycopy(this.sequenceNum, 0, array2, n2 + 12, 4);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST17:wrapped: ", array2);
            }
            return array2;
        }
        
        @Override
        public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
            if (n2 == 0) {
                return DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            final byte[] array2 = new byte[10];
            final byte[] array3 = new byte[n2 - 16];
            final byte[] array4 = new byte[2];
            final byte[] array5 = new byte[4];
            System.arraycopy(array, n, array3, 0, array3.length);
            System.arraycopy(array, n + array3.length, array2, 0, 10);
            System.arraycopy(array, n + array3.length + 10, array4, 0, 2);
            System.arraycopy(array, n + array3.length + 12, array5, 0, 4);
            final byte[] hmac = this.getHMAC(this.peerKi, array5, array3, 0, array3.length);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST18:incoming: ", array3);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST19:MAC: ", array2);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST20:messageType: ", array4);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST21:sequenceNum: ", array5);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST22:expectedMAC: ", hmac);
            }
            if (!Arrays.equals(array2, hmac)) {
                DigestMD5Base.logger.log(Level.INFO, "DIGEST23:Unmatched MACs");
                return DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            if (this.peerSeqNum != AbstractSaslImpl.networkByteOrderToInt(array5, 0, 4)) {
                throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + AbstractSaslImpl.networkByteOrderToInt(array5, 0, 4) + " Expected: " + this.peerSeqNum);
            }
            if (!Arrays.equals(this.messageType, array4)) {
                throw new SaslException("DIGEST-MD5: invalid message type: " + AbstractSaslImpl.networkByteOrderToInt(array4, 0, 2));
            }
            ++this.peerSeqNum;
            return array3;
        }
        
        protected byte[] getHMAC(final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2) throws SaslException {
            final byte[] array4 = new byte[4 + n2];
            System.arraycopy(array2, 0, array4, 0, 4);
            System.arraycopy(array3, n, array4, 4, n2);
            try {
                final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "HmacMD5");
                final Mac instance = Mac.getInstance("HmacMD5");
                instance.init(secretKeySpec);
                instance.update(array4);
                final byte[] doFinal = instance.doFinal();
                final byte[] array5 = new byte[10];
                System.arraycopy(doFinal, 0, array5, 0, 10);
                return array5;
            }
            catch (final InvalidKeyException ex) {
                throw new SaslException("DIGEST-MD5: Invalid bytes used for key of HMAC-MD5 hash.", ex);
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new SaslException("DIGEST-MD5: Error creating instance of MD5 digest algorithm", ex2);
            }
        }
        
        protected void incrementSeqNum() {
            AbstractSaslImpl.intToNetworkByteOrder(this.mySeqNum++, this.sequenceNum, 0, 4);
        }
    }
    
    final class DigestPrivacy extends DigestIntegrity implements SecurityCtx
    {
        private static final String CLIENT_CONF_MAGIC = "Digest H(A1) to client-to-server sealing key magic constant";
        private static final String SVR_CONF_MAGIC = "Digest H(A1) to server-to-client sealing key magic constant";
        private Cipher encCipher;
        private Cipher decCipher;
        
        DigestPrivacy(final boolean b) throws SaslException {
            super(b);
            try {
                this.generatePrivacyKeyPair(b);
            }
            catch (final SaslException ex) {
                throw ex;
            }
            catch (final UnsupportedEncodingException ex2) {
                throw new SaslException("DIGEST-MD5: Error encoding string value into UTF-8", ex2);
            }
            catch (final IOException ex3) {
                throw new SaslException("DIGEST-MD5: Error accessing buffers required to generate cipher keys", ex3);
            }
            catch (final NoSuchAlgorithmException ex4) {
                throw new SaslException("DIGEST-MD5: Error creating instance of required cipher or digest", ex4);
            }
        }
        
        private void generatePrivacyKeyPair(final boolean b) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, SaslException {
            final byte[] bytes = "Digest H(A1) to client-to-server sealing key magic constant".getBytes(DigestMD5Base.this.encoding);
            final byte[] bytes2 = "Digest H(A1) to server-to-client sealing key magic constant".getBytes(DigestMD5Base.this.encoding);
            final MessageDigest instance = MessageDigest.getInstance("MD5");
            int n;
            if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[4])) {
                n = 5;
            }
            else if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[3])) {
                n = 7;
            }
            else {
                n = 16;
            }
            final byte[] array = new byte[n + bytes.length];
            System.arraycopy(DigestMD5Base.this.H_A1, 0, array, 0, n);
            System.arraycopy(bytes, 0, array, n, bytes.length);
            instance.update(array);
            final byte[] digest = instance.digest();
            System.arraycopy(bytes2, 0, array, n, bytes2.length);
            instance.update(array);
            final byte[] digest2 = instance.digest();
            if (DigestMD5Base.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST24:Kcc: ", digest);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST25:Kcs: ", digest2);
            }
            byte[] array2;
            byte[] array3;
            if (b) {
                array2 = digest;
                array3 = digest2;
            }
            else {
                array2 = digest2;
                array3 = digest;
            }
            try {
                if (DigestMD5Base.this.negotiatedCipher.indexOf(DigestMD5Base.CIPHER_TOKENS[1]) > -1) {
                    this.encCipher = Cipher.getInstance("RC4");
                    this.decCipher = Cipher.getInstance("RC4");
                    final SecretKeySpec secretKeySpec = new SecretKeySpec(array2, "RC4");
                    final SecretKeySpec secretKeySpec2 = new SecretKeySpec(array3, "RC4");
                    this.encCipher.init(1, secretKeySpec);
                    this.decCipher.init(2, secretKeySpec2);
                }
                else if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2]) || DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[0])) {
                    String s;
                    String s2;
                    if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2])) {
                        s = "DES/CBC/NoPadding";
                        s2 = "des";
                    }
                    else {
                        s = "DESede/CBC/NoPadding";
                        s2 = "desede";
                    }
                    this.encCipher = Cipher.getInstance(s);
                    this.decCipher = Cipher.getInstance(s);
                    final SecretKey access$2600 = makeDesKeys(array2, s2);
                    final SecretKey access$2601 = makeDesKeys(array3, s2);
                    final IvParameterSpec ivParameterSpec = new IvParameterSpec(array2, 8, 8);
                    final IvParameterSpec ivParameterSpec2 = new IvParameterSpec(array3, 8, 8);
                    this.encCipher.init(1, access$2600, ivParameterSpec);
                    this.decCipher.init(2, access$2601, ivParameterSpec2);
                    if (DigestMD5Base.logger.isLoggable(Level.FINER)) {
                        AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST26:" + DigestMD5Base.this.negotiatedCipher + " IVcc: ", ivParameterSpec.getIV());
                        AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST27:" + DigestMD5Base.this.negotiatedCipher + " IVcs: ", ivParameterSpec2.getIV());
                        AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST28:" + DigestMD5Base.this.negotiatedCipher + " encryption key: ", access$2600.getEncoded());
                        AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST29:" + DigestMD5Base.this.negotiatedCipher + " decryption key: ", access$2601.getEncoded());
                    }
                }
            }
            catch (final InvalidKeySpecException ex) {
                throw new SaslException("DIGEST-MD5: Unsupported key specification used.", ex);
            }
            catch (final InvalidAlgorithmParameterException ex2) {
                throw new SaslException("DIGEST-MD5: Invalid cipher algorithem parameter used to create cipher instance", ex2);
            }
            catch (final NoSuchPaddingException ex3) {
                throw new SaslException("DIGEST-MD5: Unsupported padding used for chosen cipher", ex3);
            }
            catch (final InvalidKeyException ex4) {
                throw new SaslException("DIGEST-MD5: Invalid data used to initialize keys", ex4);
            }
        }
        
        @Override
        public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
            if (n2 == 0) {
                return DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            this.incrementSeqNum();
            final byte[] hmac = this.getHMAC(this.myKi, this.sequenceNum, array, n, n2);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST30:Outgoing: ", array, n, n2);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "seqNum: ", this.sequenceNum);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "MAC: ", hmac);
            }
            final int blockSize = this.encCipher.getBlockSize();
            byte[] empty_BYTE_ARRAY;
            if (blockSize > 1) {
                final int n3 = blockSize - (n2 + 10) % blockSize;
                empty_BYTE_ARRAY = new byte[n3];
                for (int i = 0; i < n3; ++i) {
                    empty_BYTE_ARRAY[i] = (byte)n3;
                }
            }
            else {
                empty_BYTE_ARRAY = DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            final byte[] array2 = new byte[n2 + empty_BYTE_ARRAY.length + 10];
            System.arraycopy(array, n, array2, 0, n2);
            System.arraycopy(empty_BYTE_ARRAY, 0, array2, n2, empty_BYTE_ARRAY.length);
            System.arraycopy(hmac, 0, array2, n2 + empty_BYTE_ARRAY.length, 10);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST31:{msg, pad, KicMAC}: ", array2);
            }
            byte[] update;
            try {
                update = this.encCipher.update(array2);
                if (update == null) {
                    throw new IllegalBlockSizeException("" + array2.length);
                }
            }
            catch (final IllegalBlockSizeException ex) {
                throw new SaslException("DIGEST-MD5: Invalid block size for cipher", ex);
            }
            final byte[] array3 = new byte[update.length + 2 + 4];
            System.arraycopy(update, 0, array3, 0, update.length);
            System.arraycopy(this.messageType, 0, array3, update.length, 2);
            System.arraycopy(this.sequenceNum, 0, array3, update.length + 2, 4);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST32:Wrapped: ", array3);
            }
            return array3;
        }
        
        @Override
        public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
            if (n2 == 0) {
                return DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            final byte[] array2 = new byte[n2 - 6];
            final byte[] array3 = new byte[2];
            final byte[] array4 = new byte[4];
            System.arraycopy(array, n, array2, 0, array2.length);
            System.arraycopy(array, n + array2.length, array3, 0, 2);
            System.arraycopy(array, n + array2.length + 2, array4, 0, 4);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                DigestMD5Base.logger.log(Level.FINEST, "DIGEST33:Expecting sequence num: {0}", new Integer(this.peerSeqNum));
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST34:incoming: ", array2);
            }
            byte[] update;
            try {
                update = this.decCipher.update(array2);
                if (update == null) {
                    throw new IllegalBlockSizeException("" + array2.length);
                }
            }
            catch (final IllegalBlockSizeException ex) {
                throw new SaslException("DIGEST-MD5: Illegal block sizes used with chosen cipher", ex);
            }
            final byte[] array5 = new byte[update.length - 10];
            final byte[] array6 = new byte[10];
            System.arraycopy(update, 0, array5, 0, array5.length);
            System.arraycopy(update, array5.length, array6, 0, 10);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST35:Unwrapped (w/padding): ", array5);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST36:MAC: ", array6);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST37:messageType: ", array3);
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST38:sequenceNum: ", array4);
            }
            int length = array5.length;
            if (this.decCipher.getBlockSize() > 1) {
                length -= array5[array5.length - 1];
                if (length < 0) {
                    if (DigestMD5Base.logger.isLoggable(Level.INFO)) {
                        DigestMD5Base.logger.log(Level.INFO, "DIGEST39:Incorrect padding: {0}", new Byte(array5[array5.length - 1]));
                    }
                    return DigestMD5Base.EMPTY_BYTE_ARRAY;
                }
            }
            final byte[] hmac = this.getHMAC(this.peerKi, array4, array5, 0, length);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST40:KisMAC: ", hmac);
            }
            if (!Arrays.equals(array6, hmac)) {
                DigestMD5Base.logger.log(Level.INFO, "DIGEST41:Unmatched MACs");
                return DigestMD5Base.EMPTY_BYTE_ARRAY;
            }
            if (this.peerSeqNum != AbstractSaslImpl.networkByteOrderToInt(array4, 0, 4)) {
                throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + AbstractSaslImpl.networkByteOrderToInt(array4, 0, 4) + " Expected: " + this.peerSeqNum);
            }
            if (!Arrays.equals(this.messageType, array3)) {
                throw new SaslException("DIGEST-MD5: invalid message type: " + AbstractSaslImpl.networkByteOrderToInt(array3, 0, 2));
            }
            ++this.peerSeqNum;
            if (length == array5.length) {
                return array5;
            }
            final byte[] array7 = new byte[length];
            System.arraycopy(array5, 0, array7, 0, length);
            return array7;
        }
    }
}
