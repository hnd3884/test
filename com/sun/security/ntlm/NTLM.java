package com.sun.security.ntlm;

import java.util.Locale;
import java.io.UnsupportedEncodingException;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.KeySpec;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import sun.misc.HexDumpEncoder;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import sun.security.provider.MD4;
import javax.crypto.Mac;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;

class NTLM
{
    private final SecretKeyFactory fac;
    private final Cipher cipher;
    private final MessageDigest md4;
    private final Mac hmac;
    private final MessageDigest md5;
    private static final boolean DEBUG;
    final Version v;
    final boolean writeLM;
    final boolean writeNTLM;
    
    protected NTLM(String s) throws NTLMException {
        if (s == null) {
            s = "LMv2/NTLMv2";
        }
        final String s2 = s;
        switch (s2) {
            case "LM": {
                this.v = Version.NTLM;
                this.writeLM = true;
                this.writeNTLM = false;
                break;
            }
            case "NTLM": {
                this.v = Version.NTLM;
                this.writeLM = false;
                this.writeNTLM = true;
                break;
            }
            case "LM/NTLM": {
                this.v = Version.NTLM;
                final boolean b = true;
                this.writeNTLM = b;
                this.writeLM = b;
                break;
            }
            case "NTLM2": {
                this.v = Version.NTLM2;
                final boolean b2 = true;
                this.writeNTLM = b2;
                this.writeLM = b2;
                break;
            }
            case "LMv2": {
                this.v = Version.NTLMv2;
                this.writeLM = true;
                this.writeNTLM = false;
                break;
            }
            case "NTLMv2": {
                this.v = Version.NTLMv2;
                this.writeLM = false;
                this.writeNTLM = true;
                break;
            }
            case "LMv2/NTLMv2": {
                this.v = Version.NTLMv2;
                final boolean b3 = true;
                this.writeNTLM = b3;
                this.writeLM = b3;
                break;
            }
            default: {
                throw new NTLMException(5, "Unknown version " + s);
            }
        }
        try {
            this.fac = SecretKeyFactory.getInstance("DES");
            this.cipher = Cipher.getInstance("DES/ECB/NoPadding");
            this.md4 = MD4.getInstance();
            this.hmac = Mac.getInstance("HmacMD5");
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchPaddingException ex) {
            throw new AssertionError();
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new AssertionError();
        }
    }
    
    public void debug(final String s, final Object... array) {
        if (NTLM.DEBUG) {
            System.out.printf(s, array);
        }
    }
    
    public void debug(final byte[] array) {
        if (NTLM.DEBUG) {
            try {
                new HexDumpEncoder().encodeBuffer(array, System.out);
            }
            catch (final IOException ex) {}
        }
    }
    
    byte[] makeDesKey(final byte[] array, final int n) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = ((array[i] < 0) ? (array[i] + 256) : array[i]);
        }
        return new byte[] { (byte)array2[n + 0], (byte)((array2[n + 0] << 7 & 0xFF) | array2[n + 1] >> 1), (byte)((array2[n + 1] << 6 & 0xFF) | array2[n + 2] >> 2), (byte)((array2[n + 2] << 5 & 0xFF) | array2[n + 3] >> 3), (byte)((array2[n + 3] << 4 & 0xFF) | array2[n + 4] >> 4), (byte)((array2[n + 4] << 3 & 0xFF) | array2[n + 5] >> 5), (byte)((array2[n + 5] << 2 & 0xFF) | array2[n + 6] >> 6), (byte)(array2[n + 6] << 1 & 0xFF) };
    }
    
    byte[] calcLMHash(final byte[] array) {
        final byte[] array2 = { 75, 71, 83, 33, 64, 35, 36, 37 };
        final byte[] array3 = new byte[14];
        int length = array.length;
        if (length > 14) {
            length = 14;
        }
        System.arraycopy(array, 0, array3, 0, length);
        try {
            final DESKeySpec desKeySpec = new DESKeySpec(this.makeDesKey(array3, 0));
            final DESKeySpec desKeySpec2 = new DESKeySpec(this.makeDesKey(array3, 7));
            final SecretKey generateSecret = this.fac.generateSecret(desKeySpec);
            final SecretKey generateSecret2 = this.fac.generateSecret(desKeySpec2);
            this.cipher.init(1, generateSecret);
            final byte[] doFinal = this.cipher.doFinal(array2, 0, 8);
            this.cipher.init(1, generateSecret2);
            final byte[] doFinal2 = this.cipher.doFinal(array2, 0, 8);
            final byte[] array4 = new byte[21];
            System.arraycopy(doFinal, 0, array4, 0, 8);
            System.arraycopy(doFinal2, 0, array4, 8, 8);
            return array4;
        }
        catch (final InvalidKeyException ex) {
            assert false;
        }
        catch (final InvalidKeySpecException ex2) {
            assert false;
        }
        catch (final IllegalBlockSizeException ex3) {
            assert false;
        }
        catch (final BadPaddingException ex4) {
            assert false;
        }
        return null;
    }
    
    byte[] calcNTHash(final byte[] array) {
        final byte[] digest = this.md4.digest(array);
        final byte[] array2 = new byte[21];
        System.arraycopy(digest, 0, array2, 0, 16);
        return array2;
    }
    
    byte[] calcResponse(final byte[] array, final byte[] array2) {
        try {
            assert array.length == 21;
            final DESKeySpec desKeySpec = new DESKeySpec(this.makeDesKey(array, 0));
            final DESKeySpec desKeySpec2 = new DESKeySpec(this.makeDesKey(array, 7));
            final DESKeySpec desKeySpec3 = new DESKeySpec(this.makeDesKey(array, 14));
            final SecretKey generateSecret = this.fac.generateSecret(desKeySpec);
            final SecretKey generateSecret2 = this.fac.generateSecret(desKeySpec2);
            final SecretKey generateSecret3 = this.fac.generateSecret(desKeySpec3);
            this.cipher.init(1, generateSecret);
            final byte[] doFinal = this.cipher.doFinal(array2, 0, 8);
            this.cipher.init(1, generateSecret2);
            final byte[] doFinal2 = this.cipher.doFinal(array2, 0, 8);
            this.cipher.init(1, generateSecret3);
            final byte[] doFinal3 = this.cipher.doFinal(array2, 0, 8);
            final byte[] array3 = new byte[24];
            System.arraycopy(doFinal, 0, array3, 0, 8);
            System.arraycopy(doFinal2, 0, array3, 8, 8);
            System.arraycopy(doFinal3, 0, array3, 16, 8);
            return array3;
        }
        catch (final IllegalBlockSizeException ex) {
            assert false;
        }
        catch (final BadPaddingException ex2) {
            assert false;
        }
        catch (final InvalidKeySpecException ex3) {
            assert false;
        }
        catch (final InvalidKeyException ex4) {
            assert false;
        }
        return null;
    }
    
    byte[] hmacMD5(final byte[] array, final byte[] array2) {
        try {
            this.hmac.init(new SecretKeySpec(Arrays.copyOf(array, 16), "HmacMD5"));
            return this.hmac.doFinal(array2);
        }
        catch (final InvalidKeyException ex) {
            assert false;
        }
        catch (final RuntimeException ex2) {
            assert false;
        }
        return null;
    }
    
    byte[] calcV2(final byte[] array, final String s, final byte[] array2, final byte[] array3) {
        try {
            final byte[] hmacMD5 = this.hmacMD5(array, s.getBytes("UnicodeLittleUnmarked"));
            final byte[] array4 = new byte[array2.length + 8];
            System.arraycopy(array3, 0, array4, 0, 8);
            System.arraycopy(array2, 0, array4, 8, array2.length);
            final byte[] array5 = new byte[16 + array2.length];
            System.arraycopy(this.hmacMD5(hmacMD5, array4), 0, array5, 0, 16);
            System.arraycopy(array2, 0, array5, 16, array2.length);
            return array5;
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
            return null;
        }
    }
    
    static byte[] ntlm2LM(final byte[] array) {
        return Arrays.copyOf(array, 24);
    }
    
    byte[] ntlm2NTLM(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] copy = Arrays.copyOf(array3, 16);
        System.arraycopy(array2, 0, copy, 8, 8);
        return this.calcResponse(array, Arrays.copyOf(this.md5.digest(copy), 8));
    }
    
    static byte[] getP1(final char[] array) {
        try {
            return new String(array).toUpperCase(Locale.ENGLISH).getBytes("ISO8859_1");
        }
        catch (final UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    static byte[] getP2(final char[] array) {
        try {
            return new String(array).getBytes("UnicodeLittleUnmarked");
        }
        catch (final UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    static {
        DEBUG = (System.getProperty("ntlm.debug") != null);
    }
    
    static class Reader
    {
        private final byte[] internal;
        
        Reader(final byte[] internal) {
            this.internal = internal;
        }
        
        int readInt(final int n) throws NTLMException {
            try {
                return (this.internal[n] & 0xFF) + ((this.internal[n + 1] & 0xFF) << 8) + ((this.internal[n + 2] & 0xFF) << 16) + ((this.internal[n + 3] & 0xFF) << 24);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                throw new NTLMException(1, "Input message incorrect size");
            }
        }
        
        int readShort(final int n) throws NTLMException {
            try {
                return (this.internal[n] & 0xFF) + (this.internal[n + 1] & 0xFF00);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                throw new NTLMException(1, "Input message incorrect size");
            }
        }
        
        byte[] readBytes(final int n, final int n2) throws NTLMException {
            try {
                return Arrays.copyOfRange(this.internal, n, n + n2);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                throw new NTLMException(1, "Input message incorrect size");
            }
        }
        
        byte[] readSecurityBuffer(final int n) throws NTLMException {
            final int int1 = this.readInt(n + 4);
            if (int1 == 0) {
                return new byte[0];
            }
            try {
                return Arrays.copyOfRange(this.internal, int1, int1 + this.readShort(n));
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                throw new NTLMException(1, "Input message incorrect size");
            }
        }
        
        String readSecurityBuffer(final int n, final boolean b) throws NTLMException {
            final byte[] securityBuffer = this.readSecurityBuffer(n);
            try {
                String s;
                if (securityBuffer == null) {
                    s = null;
                }
                else {
                    final String s2;
                    s = s2;
                    s2 = new String(securityBuffer, b ? "UnicodeLittleUnmarked" : "ISO8859_1");
                }
                return s;
            }
            catch (final UnsupportedEncodingException ex) {
                throw new NTLMException(1, "Invalid input encoding");
            }
        }
    }
    
    static class Writer
    {
        private byte[] internal;
        private int current;
        
        Writer(final int n, final int current) {
            assert current < 256;
            this.internal = new byte[256];
            this.current = current;
            System.arraycopy(new byte[] { 78, 84, 76, 77, 83, 83, 80, 0, (byte)n }, 0, this.internal, 0, 9);
        }
        
        void writeShort(final int n, final int n2) {
            this.internal[n] = (byte)n2;
            this.internal[n + 1] = (byte)(n2 >> 8);
        }
        
        void writeInt(final int n, final int n2) {
            this.internal[n] = (byte)n2;
            this.internal[n + 1] = (byte)(n2 >> 8);
            this.internal[n + 2] = (byte)(n2 >> 16);
            this.internal[n + 3] = (byte)(n2 >> 24);
        }
        
        void writeBytes(final int n, final byte[] array) {
            System.arraycopy(array, 0, this.internal, n, array.length);
        }
        
        void writeSecurityBuffer(final int n, final byte[] array) {
            if (array == null) {
                this.writeShort(n + 4, this.current);
            }
            else {
                final int length = array.length;
                if (this.current + length > this.internal.length) {
                    this.internal = Arrays.copyOf(this.internal, this.current + length + 256);
                }
                this.writeShort(n, length);
                this.writeShort(n + 2, length);
                this.writeShort(n + 4, this.current);
                System.arraycopy(array, 0, this.internal, this.current, length);
                this.current += length;
            }
        }
        
        void writeSecurityBuffer(final int n, final String s, final boolean b) {
            try {
                this.writeSecurityBuffer(n, (byte[])((s == null) ? null : s.getBytes(b ? "UnicodeLittleUnmarked" : "ISO8859_1")));
            }
            catch (final UnsupportedEncodingException ex) {
                assert false;
            }
        }
        
        byte[] getBytes() {
            return Arrays.copyOf(this.internal, this.current);
        }
    }
}
