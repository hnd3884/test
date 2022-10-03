package com.theorem.radius3;

import java.io.UnsupportedEncodingException;
import com.theorem.radius3.radutil.BitArray;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.radutil.RadRand;
import com.theorem.radius3.radutil.MD4Digest;
import javax.crypto.Cipher;

public class MSChap
{
    public static final boolean NTHASH_PASSWORD = true;
    public static final int NTHASH_PASSWORD_LENGTH = 16;
    public static final boolean PLAINTEXT_PASSWORD = false;
    public static final int RESPONSE_LENGTH = 24;
    public static final int CHALLENGE_LENGTH = 8;
    private static final byte[] a;
    private Cipher b;
    protected MD4Digest c;
    protected static final RadRand d;
    
    public MSChap() throws RADIUSException {
        this.c = new MD4Digest();
        try {
            this.b = Cipher.getInstance("DES");
        }
        catch (final Exception ex) {
            throw new RADIUSException(ex.getMessage());
        }
    }
    
    public final byte[] LmChallengeResponse(final byte[] array, final byte[] array2) throws RADIUSException {
        return this.ChallengeResponse(array, this.LmPasswordHash(array2));
    }
    
    public final byte[] LmPasswordHash(final byte[] array) throws RADIUSException {
        final byte[] utf8 = Util.toUTF8(Util.toUTF8(array).toUpperCase());
        final byte[] array2 = new byte[14];
        System.arraycopy(utf8, 0, array2, 0, (utf8.length >= 14) ? 14 : utf8.length);
        final byte[] array3 = array2;
        final byte[] array4 = new byte[7];
        System.arraycopy(array3, 0, array4, 0, (array3.length < 7) ? array3.length : 7);
        final byte[] desHash = this.DesHash(array4);
        if (array3.length > 7) {
            System.arraycopy(array3, 7, array4, 0, array3.length - 7);
        }
        final byte[] desHash2 = this.DesHash(array4);
        final byte[] array5 = new byte[16];
        System.arraycopy(desHash, 0, array5, 0, 8);
        System.arraycopy(desHash2, 0, array5, 8, 8);
        return array5;
    }
    
    public final byte[] DesHash(final byte[] array) throws RADIUSException {
        return this.DesEncrypt(MSChap.a, array);
    }
    
    public final byte[] DesEncrypt(final byte[] array, byte[] parityKey) throws RADIUSException {
        parityKey = this.parityKey(parityKey);
        final byte[] array2 = new byte[8];
        System.arraycopy(parityKey, 0, array2, 0, (parityKey.length > array2.length) ? array2.length : parityKey.length);
        try {
            (this.b = Cipher.getInstance("DES")).init(1, new SecretKeySpec(array2, "DES"));
            final byte[] array3 = new byte[this.b.getOutputSize(array.length)];
            this.b.doFinal(array, 0, array.length, array3, 0);
            final byte[] array4 = new byte[8];
            System.arraycopy(array3, 0, array4, 0, array4.length);
            return array4;
        }
        catch (final Exception ex) {
            throw new RADIUSException(ex.getMessage());
        }
    }
    
    public final byte[] NtChallengeResponse(final byte[] array, byte[] array2, final boolean b) throws RADIUSException {
        if (array2 == null || array2.length == 0) {
            array2 = new byte[0];
        }
        byte[] ntPasswordHash;
        if (!b) {
            ntPasswordHash = this.NtPasswordHash(this.toUnicode(array2));
        }
        else {
            ntPasswordHash = array2;
        }
        return this.ChallengeResponse(array, ntPasswordHash);
    }
    
    public final byte[] NtPasswordHash(final byte[] array) {
        byte[] array3;
        if (array.length > 256) {
            final byte[] array2 = new byte[256];
            System.arraycopy(array, 0, array2, 0, 256);
            array3 = array2;
        }
        else {
            array3 = array;
        }
        this.c.reset();
        this.c.update(array3);
        return this.c.digest();
    }
    
    public static final byte[] createChallenge() {
        final byte[] array = new byte[8];
        MSChap.d.nextBytes(array);
        return array;
    }
    
    public final byte[] ChallengeResponse(final byte[] array, final byte[] array2) throws RADIUSException {
        final byte[] array3 = new byte[21];
        System.arraycopy(array2, 0, array3, 0, array2.length);
        final byte[] array4 = new byte[7];
        System.arraycopy(array3, 0, array4, 0, 7);
        final byte[] desEncrypt = this.DesEncrypt(array, array4);
        System.arraycopy(array3, 7, array4, 0, 7);
        final byte[] desEncrypt2 = this.DesEncrypt(array, array4);
        System.arraycopy(array3, 14, array4, 0, 7);
        final byte[] desEncrypt3 = this.DesEncrypt(array, array4);
        final byte[] array5 = new byte[24];
        System.arraycopy(desEncrypt, 0, array5, 0, 8);
        System.arraycopy(desEncrypt2, 0, array5, 8, 8);
        System.arraycopy(desEncrypt3, 0, array5, 16, 8);
        return array5;
    }
    
    public final byte[] LmEncryptedPasswordHash(final byte[] array, final byte[] array2) throws RADIUSException {
        byte[] array4;
        if (array.length > 256) {
            final byte[] array3 = new byte[256];
            System.arraycopy(array, 0, array3, 0, 256);
            array4 = array3;
        }
        else {
            array4 = array;
        }
        return this.PasswordHashEncryptedWithBlock(this.LmPasswordHash(array4), array2);
    }
    
    public final byte[] PasswordHashEncryptedWithBlock(final byte[] array, final byte[] array2) throws RADIUSException {
        final byte[] array3 = new byte[8];
        System.arraycopy(array, 0, array3, 0, 8);
        final byte[] array4 = new byte[7];
        System.arraycopy(array2, 0, array4, 0, 7);
        final byte[] desEncrypt = this.DesEncrypt(array3, array4);
        System.arraycopy(array, 0, array3, 8, 8);
        final byte[] desEncrypt2 = this.DesEncrypt(array3, array4);
        final byte[] array5 = new byte[16];
        System.arraycopy(desEncrypt, 0, array5, 0, 8);
        System.arraycopy(desEncrypt2, 0, array5, 8, 8);
        return array5;
    }
    
    public final byte[] NtEncryptedPasswordHash(final byte[] array, final byte[] array2, final boolean b) throws RADIUSException {
        byte[] ntPasswordHash;
        if (!b) {
            ntPasswordHash = this.NtPasswordHash(this.toUnicode(array));
        }
        else {
            ntPasswordHash = array;
        }
        return this.PasswordHashEncryptedWithBlock(ntPasswordHash, array2);
    }
    
    public byte[] HashNtPasswordHash(final byte[] array) {
        this.c.reset();
        this.c.update(array);
        return this.c.digest();
    }
    
    public final byte[] parityKey(final byte[] array) {
        final BitArray bitArray = new BitArray(array);
        final BitArray bitArray2 = new BitArray((array.length + (array.length + 7 - 1) / 7) * 8);
        final int maximumBits = bitArray.getMaximumBits();
        int n = 0;
        for (int i = 0; i < maximumBits; i += 7) {
            for (int j = 0; j < 7; ++j) {
                bitArray2.set(n++, bitArray.get(i + j));
            }
            bitArray2.set(n++);
        }
        bitArray2.set(n);
        return bitArray2.toByteArray();
    }
    
    public final byte[] toUnicode(final byte[] array) {
        byte[] bytes;
        try {
            bytes = new String(array).getBytes("UnicodeLittleUnmarked");
        }
        catch (final UnsupportedEncodingException ex) {
            bytes = new byte[array.length * 2];
            for (int i = 0; i < array.length; ++i) {
                bytes[i * 2 + 1] = array[i];
                bytes[i * 2] = 0;
            }
        }
        return bytes;
    }
    
    public final byte getIdent(final byte b) {
        byte nextByte;
        while ((nextByte = MSChap.d.nextByte()) == b) {}
        return nextByte;
    }
    
    static {
        a = "KGS!@#$%".getBytes();
        d = new RadRand();
    }
}
