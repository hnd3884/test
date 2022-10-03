package sun.security.krb5.internal.crypto.dk;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import sun.misc.HexDumpEncoder;
import java.io.ByteArrayOutputStream;
import sun.security.krb5.KrbCryptoException;
import java.util.Arrays;
import sun.security.krb5.Confounder;
import sun.security.krb5.internal.crypto.KeyUsage;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;

public abstract class DkCrypto
{
    protected static final boolean debug = false;
    static final byte[] KERBEROS_CONSTANT;
    
    protected abstract int getKeySeedLength();
    
    protected abstract byte[] randomToKey(final byte[] p0);
    
    protected abstract Cipher getCipher(final byte[] p0, final byte[] p1, final int p2) throws GeneralSecurityException;
    
    public abstract int getChecksumLength();
    
    protected abstract byte[] getHmac(final byte[] p0, final byte[] p1) throws GeneralSecurityException;
    
    public byte[] encrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final byte[] array4, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        byte[] dk = null;
        byte[] dk2 = null;
        try {
            final byte[] array5 = { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), -86 };
            dk = this.dk(array, array5);
            final Cipher cipher = this.getCipher(dk, array2, 1);
            final int blockSize = cipher.getBlockSize();
            final byte[] bytes = Confounder.bytes(blockSize);
            final int roundup = this.roundup(bytes.length + n3, blockSize);
            final byte[] array6 = new byte[roundup];
            System.arraycopy(bytes, 0, array6, 0, bytes.length);
            System.arraycopy(array4, n2, array6, bytes.length, n3);
            Arrays.fill(array6, bytes.length + n3, roundup, (byte)0);
            final int outputSize = cipher.getOutputSize(roundup);
            final byte[] array7 = new byte[outputSize + this.getChecksumLength()];
            cipher.doFinal(array6, 0, roundup, array7, 0);
            if (array3 != null && array3.length == blockSize) {
                System.arraycopy(array7, outputSize - blockSize, array3, 0, blockSize);
            }
            array5[4] = 85;
            dk2 = this.dk(array, array5);
            System.arraycopy(this.getHmac(dk2, array6), 0, array7, outputSize, this.getChecksumLength());
            return array7;
        }
        finally {
            if (dk != null) {
                Arrays.fill(dk, 0, dk.length, (byte)0);
            }
            if (dk2 != null) {
                Arrays.fill(dk2, 0, dk2.length, (byte)0);
            }
        }
    }
    
    public byte[] encryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        final Cipher cipher = this.getCipher(array, array2, 1);
        final int blockSize = cipher.getBlockSize();
        if (n3 % blockSize != 0) {
            throw new GeneralSecurityException("length of data to be encrypted (" + n3 + ") is not a multiple of the blocksize (" + blockSize + ")");
        }
        final byte[] array4 = new byte[cipher.getOutputSize(n3)];
        cipher.doFinal(array3, 0, n3, array4, 0);
        return array4;
    }
    
    public byte[] decryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        final Cipher cipher = this.getCipher(array, array2, 2);
        final int blockSize = cipher.getBlockSize();
        if (n3 % blockSize != 0) {
            throw new GeneralSecurityException("length of data to be decrypted (" + n3 + ") is not a multiple of the blocksize (" + blockSize + ")");
        }
        return cipher.doFinal(array3, n2, n3);
    }
    
    public byte[] decrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        byte[] dk = null;
        byte[] dk2 = null;
        try {
            final byte[] array4 = { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), -86 };
            dk = this.dk(array, array4);
            final Cipher cipher = this.getCipher(dk, array2, 2);
            final int blockSize = cipher.getBlockSize();
            final int checksumLength = this.getChecksumLength();
            final int n4 = n3 - checksumLength;
            final byte[] doFinal = cipher.doFinal(array3, n2, n4);
            array4[4] = 85;
            dk2 = this.dk(array, array4);
            final byte[] hmac = this.getHmac(dk2, doFinal);
            boolean b = false;
            if (hmac.length >= checksumLength) {
                for (int i = 0; i < checksumLength; ++i) {
                    if (hmac[i] != array3[n4 + i]) {
                        b = true;
                        break;
                    }
                }
            }
            if (b) {
                throw new GeneralSecurityException("Checksum failed");
            }
            if (array2 != null && array2.length == blockSize) {
                System.arraycopy(array3, n2 + n4 - blockSize, array2, 0, blockSize);
            }
            final byte[] array5 = new byte[doFinal.length - blockSize];
            System.arraycopy(doFinal, blockSize, array5, 0, array5.length);
            return array5;
        }
        finally {
            if (dk != null) {
                Arrays.fill(dk, 0, dk.length, (byte)0);
            }
            if (dk2 != null) {
                Arrays.fill(dk2, 0, dk2.length, (byte)0);
            }
        }
    }
    
    int roundup(final int n, final int n2) {
        return (n + n2 - 1) / n2 * n2;
    }
    
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] dk = this.dk(array, new byte[] { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), -103 });
        try {
            final byte[] hmac = this.getHmac(dk, array2);
            if (hmac.length == this.getChecksumLength()) {
                return hmac;
            }
            if (hmac.length > this.getChecksumLength()) {
                final byte[] array3 = new byte[this.getChecksumLength()];
                System.arraycopy(hmac, 0, array3, 0, array3.length);
                return array3;
            }
            throw new GeneralSecurityException("checksum size too short: " + hmac.length + "; expecting : " + this.getChecksumLength());
        }
        finally {
            Arrays.fill(dk, 0, dk.length, (byte)0);
        }
    }
    
    byte[] dk(final byte[] array, final byte[] array2) throws GeneralSecurityException {
        return this.randomToKey(this.dr(array, array2));
    }
    
    private byte[] dr(final byte[] array, byte[] nfold) throws GeneralSecurityException {
        final Cipher cipher = this.getCipher(array, null, 1);
        final int blockSize = cipher.getBlockSize();
        if (nfold.length != blockSize) {
            nfold = nfold(nfold, blockSize * 8);
        }
        byte[] array2 = nfold;
        final int n = this.getKeySeedLength() >> 3;
        final byte[] array3 = new byte[n];
        byte[] doFinal;
        int n2;
        for (int i = 0; i < n; i += n2, array2 = doFinal) {
            doFinal = cipher.doFinal(array2);
            n2 = ((n - i <= doFinal.length) ? (n - i) : doFinal.length);
            System.arraycopy(doFinal, 0, array3, i, n2);
        }
        return array3;
    }
    
    static byte[] nfold(final byte[] array, int n) {
        final int length = array.length;
        int n2;
        n = (n2 = n >> 3);
        int n3;
        for (int i = length; i != 0; i = n2 % i, n2 = n3) {
            n3 = i;
        }
        final int n4 = n * length / n2;
        final byte[] array2 = new byte[n];
        Arrays.fill(array2, (byte)0);
        int n5 = 0;
        for (int j = n4 - 1; j >= 0; --j) {
            final int n6 = ((length << 3) - 1 + ((length << 3) + 13) * (j / length) + (length - j % length << 3)) % (length << 3);
            final int n7 = n5 + (((array[(length - 1 - (n6 >>> 3)) % length] & 0xFF) << 8 | (array[(length - (n6 >>> 3)) % length] & 0xFF)) >>> (n6 & 0x7) + 1 & 0xFF) + (array2[j % n] & 0xFF);
            array2[j % n] = (byte)(n7 & 0xFF);
            n5 = n7 >>> 8;
        }
        if (n5 != 0) {
            for (int k = n - 1; k >= 0; --k) {
                final int n8 = n5 + (array2[k] & 0xFF);
                array2[k] = (byte)(n8 & 0xFF);
                n5 = n8 >>> 8;
            }
        }
        return array2;
    }
    
    static String bytesToString(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if ((array[i] & 0xFF) < 16) {
                sb.append("0" + Integer.toHexString(array[i] & 0xFF));
            }
            else {
                sb.append(Integer.toHexString(array[i] & 0xFF));
            }
        }
        return sb.toString();
    }
    
    private static byte[] binaryStringToBytes(final String s) {
        final char[] charArray = s.toCharArray();
        final byte[] array = new byte[charArray.length / 2];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (byte)(Byte.parseByte(new String(charArray, i * 2, 1), 16) << 4 | Byte.parseByte(new String(charArray, i * 2 + 1, 1), 16));
        }
        return array;
    }
    
    static void traceOutput(final String s, final byte[] array, final int n, final int n2) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(n2);
            new HexDumpEncoder().encodeBuffer(new ByteArrayInputStream(array, n, n2), byteArrayOutputStream);
            System.err.println(s + ":" + byteArrayOutputStream.toString());
        }
        catch (final Exception ex) {}
    }
    
    static byte[] charToUtf8(final char[] array) {
        final ByteBuffer encode = Charset.forName("UTF-8").encode(CharBuffer.wrap(array));
        final int limit = encode.limit();
        final byte[] array2 = new byte[limit];
        encode.get(array2, 0, limit);
        return array2;
    }
    
    static byte[] charToUtf16(final char[] array) {
        final ByteBuffer encode = Charset.forName("UTF-16LE").encode(CharBuffer.wrap(array));
        final int limit = encode.limit();
        final byte[] array2 = new byte[limit];
        encode.get(array2, 0, limit);
        return array2;
    }
    
    static {
        KERBEROS_CONSTANT = new byte[] { 107, 101, 114, 98, 101, 114, 111, 115 };
    }
}
