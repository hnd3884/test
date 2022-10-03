package com.theorem.radius3;

import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.radutil.RadRand;
import java.security.MessageDigest;

public final class MSChapV2 extends MSChap
{
    public static final boolean NTHASH_PASSWORD = true;
    public static final boolean PLAINTEXT_PASSWORD = false;
    private MessageDigest a;
    protected static RadRand b;
    private static final byte[] c;
    private static final byte[] d;
    private static final byte[] e;
    private static final byte[] f;
    private static final byte[] g;
    private static final byte[] h;
    private static final byte[] i;
    
    public MSChapV2() throws RADIUSException {
        try {
            this.a = MessageDigest.getInstance("SHA-1");
        }
        catch (final Exception ex) {
            throw new RADIUSException(ex.getMessage());
        }
    }
    
    final byte[] a(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4, final boolean b) throws RADIUSException {
        this.toUnicode(array3);
        byte[] ntPasswordHash;
        if (!b) {
            ntPasswordHash = this.NtPasswordHash(this.toUnicode(array4));
        }
        else {
            ntPasswordHash = array4;
        }
        return this.ChallengeResponse(this.a(array2, array, array3), ntPasswordHash);
    }
    
    final byte[] a(final byte[] array, final byte[] array2, final byte[] array3) {
        this.a.reset();
        this.a.update(array);
        this.a.update(array2);
        this.a.update(array3);
        return this.a.digest();
    }
    
    protected final String a(final byte[] array, final boolean b, final byte[] array2, final byte[] array3, final byte[] array4, final byte[] array5) {
        byte[] ntPasswordHash;
        if (!b) {
            ntPasswordHash = this.NtPasswordHash(this.toUnicode(array));
        }
        else {
            ntPasswordHash = array;
        }
        final byte[] hashNtPasswordHash = this.HashNtPasswordHash(ntPasswordHash);
        this.a.reset();
        this.a.update(hashNtPasswordHash);
        this.a.update(array2);
        this.a.update(MSChapV2.c);
        final byte[] digest = this.a.digest();
        final byte[] a = this.a(array3, array4, array5);
        this.a.reset();
        this.a.update(digest);
        this.a.update(a, 0, 8);
        this.a.update(MSChapV2.d);
        return "S=" + Util.toHexString(this.a.digest());
    }
    
    protected final boolean a(final byte[] array, final boolean b, final byte[] array2, final byte[] array3, final byte[] array4, final byte[] array5, final String s) {
        final String a = this.a(array, b, array2, array3, array4, array5);
        final int length = a.length();
        if (length != s.length()) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (a.charAt(i) != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public final byte[] HashNtPasswordHash(final byte[] array) {
        super.c.reset();
        super.c.update(array);
        return super.c.digest();
    }
    
    public final byte[] getMasterKey(final byte[] array, final byte[] array2) {
        this.a.reset();
        this.a.update(array);
        this.a.update(array2);
        this.a.update(MSChapV2.e);
        final byte[] digest = this.a.digest();
        final byte[] array3 = new byte[16];
        System.arraycopy(digest, 0, array3, 0, 16);
        return array3;
    }
    
    public final byte[] getAsymmetricStartKey(final byte[] array, final int n, final boolean b, final boolean b2) {
        byte[] array2;
        if (b) {
            if (b2) {
                array2 = MSChapV2.g;
            }
            else {
                array2 = MSChapV2.f;
            }
        }
        else if (b2) {
            array2 = MSChapV2.f;
        }
        else {
            array2 = MSChapV2.g;
        }
        this.a.reset();
        this.a.update(array);
        this.a.update(MSChapV2.h);
        this.a.update(array2);
        this.a.update(MSChapV2.i);
        final byte[] digest = this.a.digest();
        byte[] array3;
        if (n <= 8) {
            array3 = new byte[8];
            System.arraycopy(digest, 0, array3, 0, 8);
        }
        else {
            array3 = new byte[16];
            System.arraycopy(digest, 0, array3, 0, 16);
        }
        return array3;
    }
    
    public final byte[] getKey(final byte[] array, final byte[] array2, final int n) {
        this.a.reset();
        this.a.update(array);
        this.a.update(MSChapV2.h);
        this.a.update(array2);
        this.a.update(MSChapV2.i);
        final byte[] digest = this.a.digest();
        byte[] array3;
        if (n <= 8) {
            array3 = new byte[8];
            System.arraycopy(digest, 0, array3, 0, 8);
        }
        else {
            array3 = new byte[16];
            System.arraycopy(digest, 0, array3, 0, 16);
        }
        return array3;
    }
    
    static {
        MSChapV2.b = new RadRand();
        c = new byte[] { 77, 97, 103, 105, 99, 32, 115, 101, 114, 118, 101, 114, 32, 116, 111, 32, 99, 108, 105, 101, 110, 116, 32, 115, 105, 103, 110, 105, 110, 103, 32, 99, 111, 110, 115, 116, 97, 110, 116 };
        d = new byte[] { 80, 97, 100, 32, 116, 111, 32, 109, 97, 107, 101, 32, 105, 116, 32, 100, 111, 32, 109, 111, 114, 101, 32, 116, 104, 97, 110, 32, 111, 110, 101, 32, 105, 116, 101, 114, 97, 116, 105, 111, 110 };
        e = new byte[] { 84, 104, 105, 115, 32, 105, 115, 32, 116, 104, 101, 32, 77, 80, 80, 69, 32, 77, 97, 115, 116, 101, 114, 32, 75, 101, 121 };
        f = new byte[] { 79, 110, 32, 116, 104, 101, 32, 99, 108, 105, 101, 110, 116, 32, 115, 105, 100, 101, 44, 32, 116, 104, 105, 115, 32, 105, 115, 32, 116, 104, 101, 32, 115, 101, 110, 100, 32, 107, 101, 121, 59, 32, 111, 110, 32, 116, 104, 101, 32, 115, 101, 114, 118, 101, 114, 32, 115, 105, 100, 101, 44, 32, 105, 116, 32, 105, 115, 32, 116, 104, 101, 32, 114, 101, 99, 101, 105, 118, 101, 32, 107, 101, 121, 46 };
        g = new byte[] { 79, 110, 32, 116, 104, 101, 32, 99, 108, 105, 101, 110, 116, 32, 115, 105, 100, 101, 44, 32, 116, 104, 105, 115, 32, 105, 115, 32, 116, 104, 101, 32, 114, 101, 99, 101, 105, 118, 101, 32, 107, 101, 121, 59, 32, 111, 110, 32, 116, 104, 101, 32, 115, 101, 114, 118, 101, 114, 32, 115, 105, 100, 101, 44, 32, 105, 116, 32, 105, 115, 32, 116, 104, 101, 32, 115, 101, 110, 100, 32, 107, 101, 121, 46 };
        h = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        i = new byte[] { -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14 };
    }
}
