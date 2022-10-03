package com.theorem.radius3.radutil;

import java.security.Key;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

public class SecretAmplification
{
    public static final int ITERATIONS = 1048576;
    public static final int DKLEN = 12;
    private int a;
    private Mac b;
    private int c;
    private SecretKeySpec d;
    private byte[] e;
    private int f;
    private int g;
    private static final char[] h;
    
    public SecretAmplification(final byte[] array, final byte[] array2) throws NoSuchAlgorithmException, InvalidKeyException {
        this.d = new SecretKeySpec(array, "NONE");
        this.e = ((array2 == null) ? new byte[0] : array2);
        this.f = 1048576;
        this.g = 12;
        this.a();
    }
    
    public SecretAmplification(final byte[] array, final byte[] array2, final int f, final int n) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeyException {
        this.d = new SecretKeySpec(array, "NONE");
        this.e = ((array2 == null) ? new byte[0] : array2);
        this.f = f;
        this.a();
        this.a = -1 * this.c;
        if (n > 1 - 1 * this.c) {
            throw new IllegalArgumentException("Derived key too long - result would be " + n + " bytes.");
        }
    }
    
    private final void a() throws NoSuchAlgorithmException, InvalidKeyException {
        (this.b = Mac.getInstance("HMACSHA1")).init(this.d);
        this.c = this.b.getMacLength();
    }
    
    public final byte[] amplify() {
        final byte[] a = this.a(this.g, this.c);
        final int n = a.length / this.c;
        int n2 = 0;
        for (int i = 1; i <= n; ++i) {
            System.arraycopy(this.a(this.e, this.f, i), 0, a, n2, this.c);
            n2 += this.c;
        }
        final byte[] array = new byte[this.g];
        System.arraycopy(a, 0, array, 0, this.g);
        return this.a(array);
    }
    
    final byte[] a(final byte[] array, final int n, final int n2) {
        this.b.reset();
        this.b.update(array);
        final byte[] doFinal = this.b.doFinal(new byte[] { (byte)(n2 >>> 24), (byte)(n2 >>> 16), (byte)(n2 >>> 8), (byte)n2 });
        byte[] doFinal2 = new byte[this.c];
        System.arraycopy(doFinal, 0, doFinal2, 0, this.c);
        for (int i = n2; i < n; ++i) {
            doFinal2 = this.b.doFinal(doFinal2);
            for (int j = 0; j < this.c; ++j) {
                final byte[] array2 = doFinal;
                final int n3 = j;
                array2[n3] ^= doFinal2[j];
            }
        }
        return doFinal;
    }
    
    private final byte[] a(final byte[] array) {
        final char[] array2 = new char[4 * (array.length + (3 - array.length % 3) % 3) / 3];
        int n = 0;
        int i = 0;
        while (i < array.length) {
            final int n2 = array.length - i;
            if (n2 > 2) {
                final int n3 = ((array[i++] & 0xFF) << 8 | (array[i++] & 0xFF)) << 8 | (array[i++] & 0xFF);
                if (i == 3) {
                    array2[n++] = SecretAmplification.h[((n3 & 0xFC0000) >> 18) % 52];
                    array2[n++] = SecretAmplification.h[((n3 & 0x3F000) >> 12) % 10 + 52];
                }
                else {
                    array2[n++] = SecretAmplification.h[(n3 & 0xFC0000) >> 18];
                    array2[n++] = SecretAmplification.h[(n3 & 0x3F000) >> 12];
                }
                array2[n++] = SecretAmplification.h[(n3 & 0xFC0) >> 6];
                array2[n++] = SecretAmplification.h[n3 & 0x3F];
            }
            else if (n2 == 2) {
                final int n4 = (array[i++] << 8) + array[i++] << 8;
                array2[n++] = SecretAmplification.h[(n4 & 0xFC0000) >> 18];
                array2[n++] = SecretAmplification.h[(n4 & 0x3F000) >> 12];
                array2[n++] = SecretAmplification.h[(n4 & 0xFC0) >> 6];
                array2[n++] = '=';
            }
            else {
                final int n5 = array[i++] << 16;
                array2[n++] = SecretAmplification.h[(n5 & 0xFC0000) >> 18];
                array2[n++] = SecretAmplification.h[(n5 & 0x3F000) >> 12];
                array2[n++] = '=';
                array2[n++] = '=';
            }
        }
        final byte[] array3 = new byte[array2.length];
        for (int j = 0; j < array2.length; ++j) {
            array3[j] = (byte)array2[j];
        }
        return array3;
    }
    
    private final byte[] a(final int n, final int n2) {
        int n3 = (n + n2 - 1) / n2 * n2;
        if (n3 == 0) {
            n3 = n2;
        }
        return new byte[n3];
    }
    
    public static void main(final String[] array) {
        try {
            if (array.length != 2) {
                System.err.println("Usage: SecretAmplification <secret> [salt - optional]");
                System.exit(1);
            }
            new SecretAmplification(Util.toUTF8(array[0]), Util.toUTF8(array[1])).b();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private final void b() throws Exception {
        final byte[] amplify = this.amplify();
        System.out.println(Util.toUTF8(amplify) + " length " + amplify.length);
    }
    
    static {
        h = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1' };
    }
}
