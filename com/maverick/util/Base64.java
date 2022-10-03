package com.maverick.util;

import java.io.FilterOutputStream;
import java.io.FilterInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

public class Base64
{
    public static final boolean ENCODE = true;
    public static final boolean DECODE = false;
    private static final byte[] b;
    private static final byte[] c;
    
    public static byte[] decode(final String s) {
        final byte[] bytes = s.getBytes();
        return decode(bytes, 0, bytes.length);
    }
    
    public static byte[] decode(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2 * 3 / 4];
        int n3 = 0;
        final byte[] array3 = new byte[4];
        int n4 = 0;
        for (int i = n; i < n2; ++i) {
            final byte b = (byte)(array[i] & 0x7F);
            final byte b2 = Base64.c[b];
            if (b2 < -5) {
                System.err.println("Bad Base64 input character at " + i + ": " + array[i] + "(decimal)");
                return null;
            }
            if (b2 >= -1) {
                array3[n4++] = b;
                if (n4 > 3) {
                    n3 += b(array3, 0, array2, n3);
                    n4 = 0;
                    if (b == 61) {
                        break;
                    }
                }
            }
        }
        final byte[] array4 = new byte[n3];
        System.arraycopy(array2, 0, array4, 0, n3);
        return array4;
    }
    
    public static Object decodeToObject(final String s) {
        final byte[] decode = decode(s);
        java.io.InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = new ByteArrayInputStream(decode);
            objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        }
        catch (final IOException ex) {
            return null;
        }
        catch (final ClassNotFoundException ex2) {
            return null;
        }
        finally {
            try {
                ((ByteArrayInputStream)inputStream).close();
            }
            catch (final Exception ex3) {}
            try {
                objectInputStream.close();
            }
            catch (final Exception ex4) {}
        }
    }
    
    public static String decodeToString(final String s) {
        return new String(decode(s));
    }
    
    public static String encodeBytes(final byte[] array, final boolean b) {
        return encodeBytes(array, 0, array.length, b);
    }
    
    public static String encodeBytes(final byte[] array, final int n, final int n2, final boolean b) {
        final int n3 = n2 * 4 / 3;
        final byte[] array2 = new byte[n3 + ((n2 % 3 > 0) ? 4 : 0) + n3 / 64];
        int i = 0;
        int n4 = 0;
        final int n5 = n2 - 2;
        int n6 = 0;
        while (i < n5) {
            b(array, i + n, 3, array2, n4);
            n6 += 4;
            if (!b && n6 == 64) {
                array2[n4 + 4] = 10;
                ++n4;
                n6 = 0;
            }
            i += 3;
            n4 += 4;
        }
        if (i < n2) {
            b(array, i + n, n2 - i, array2, n4);
            n4 += 4;
        }
        return new String(array2, 0, n4);
    }
    
    public static String encodeObject(final Serializable s) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        java.io.OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            outputStream = new OutputStream(byteArrayOutputStream, true);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(s);
        }
        catch (final IOException ex) {
            return null;
        }
        finally {
            try {
                objectOutputStream.close();
            }
            catch (final Exception ex2) {}
            try {
                outputStream.close();
            }
            catch (final Exception ex3) {}
            try {
                byteArrayOutputStream.close();
            }
            catch (final Exception ex4) {}
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
    
    public static String encodeString(final String s, final boolean b) {
        return encodeBytes(s.getBytes(), b);
    }
    
    private static byte[] b(final byte[] array) {
        final byte[] array2 = new byte[3];
        final int b = b(array, 0, array2, 0);
        final byte[] array3 = new byte[b];
        for (int i = 0; i < b; ++i) {
            array3[i] = array2[i];
        }
        return array3;
    }
    
    private static int b(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (array[n + 2] == 61) {
            array2[n2] = (byte)((Base64.c[array[n]] << 24 >>> 6 | Base64.c[array[n + 1]] << 24 >>> 12) >>> 16);
            return 1;
        }
        if (array[n + 3] == 61) {
            final int n3 = Base64.c[array[n]] << 24 >>> 6 | Base64.c[array[n + 1]] << 24 >>> 12 | Base64.c[array[n + 2]] << 24 >>> 18;
            array2[n2] = (byte)(n3 >>> 16);
            array2[n2 + 1] = (byte)(n3 >>> 8);
            return 2;
        }
        final int n4 = Base64.c[array[n]] << 24 >>> 6 | Base64.c[array[n + 1]] << 24 >>> 12 | Base64.c[array[n + 2]] << 24 >>> 18 | Base64.c[array[n + 3]] << 24 >>> 24;
        array2[n2] = (byte)(n4 >> 16);
        array2[n2 + 1] = (byte)(n4 >> 8);
        array2[n2 + 2] = (byte)n4;
        return 3;
    }
    
    private static byte[] b(final byte[] array, final int n) {
        final byte[] array2 = new byte[4];
        b(array, 0, n, array2, 0);
        return array2;
    }
    
    private static byte[] b(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        final int n4 = ((n2 > 0) ? (array[n] << 24 >>> 8) : 0) | ((n2 > 1) ? (array[n + 1] << 24 >>> 16) : 0) | ((n2 > 2) ? (array[n + 2] << 24 >>> 24) : 0);
        switch (n2) {
            case 3: {
                array2[n3] = Base64.b[n4 >>> 18];
                array2[n3 + 1] = Base64.b[n4 >>> 12 & 0x3F];
                array2[n3 + 2] = Base64.b[n4 >>> 6 & 0x3F];
                array2[n3 + 3] = Base64.b[n4 & 0x3F];
                return array2;
            }
            case 2: {
                array2[n3] = Base64.b[n4 >>> 18];
                array2[n3 + 1] = Base64.b[n4 >>> 12 & 0x3F];
                array2[n3 + 2] = Base64.b[n4 >>> 6 & 0x3F];
                array2[n3 + 3] = 61;
                return array2;
            }
            case 1: {
                array2[n3] = Base64.b[n4 >>> 18];
                array2[n3 + 1] = Base64.b[n4 >>> 12 & 0x3F];
                array2[n3 + 3] = (array2[n3 + 2] = 61);
                return array2;
            }
            default: {
                return array2;
            }
        }
    }
    
    static {
        b = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        c = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9 };
    }
    
    public static class InputStream extends FilterInputStream
    {
        private byte[] c;
        private boolean e;
        private int d;
        private int f;
        private int b;
        
        public InputStream(final java.io.InputStream inputStream) {
            this(inputStream, false);
        }
        
        public InputStream(final java.io.InputStream inputStream, final boolean e) {
            super(inputStream);
            this.e = e;
            this.d = (e ? 4 : 3);
            this.c = new byte[this.d];
            this.b = -1;
        }
        
        public int read() throws IOException {
            if (this.b < 0) {
                if (this.e) {
                    final byte[] array = new byte[3];
                    this.f = 0;
                    for (int i = 0; i < 3; ++i) {
                        try {
                            final int read = super.in.read();
                            if (read >= 0) {
                                array[i] = (byte)read;
                                ++this.f;
                            }
                        }
                        catch (final IOException ex) {
                            if (i == 0) {
                                throw ex;
                            }
                        }
                    }
                    if (this.f > 0) {
                        b(array, 0, this.f, this.c, 0);
                        this.b = 0;
                    }
                }
                else {
                    final byte[] array2 = new byte[4];
                    int j;
                    for (j = 0; j < 4; ++j) {
                        int read2;
                        do {
                            read2 = super.in.read();
                        } while (read2 >= 0 && Base64.c[read2 & 0x7F] < -5);
                        if (read2 < 0) {
                            break;
                        }
                        array2[j] = (byte)read2;
                    }
                    if (j == 4) {
                        this.f = b(array2, 0, this.c, 0);
                        this.b = 0;
                    }
                }
            }
            if (this.b < 0) {
                return -1;
            }
            if (!this.e && this.b >= this.f) {
                return -1;
            }
            final byte b = this.c[this.b++];
            if (this.b >= this.d) {
                this.b = -1;
            }
            return b;
        }
        
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            int i;
            for (i = 0; i < n2; ++i) {
                final int read = this.read();
                if (read < 0) {
                    return -1;
                }
                array[n + i] = (byte)read;
            }
            return i;
        }
    }
    
    public static class OutputStream extends FilterOutputStream
    {
        private byte[] d;
        private boolean f;
        private int e;
        private int c;
        private int b;
        
        public OutputStream(final java.io.OutputStream outputStream) {
            this(outputStream, true);
        }
        
        public OutputStream(final java.io.OutputStream outputStream, final boolean f) {
            super(outputStream);
            this.f = f;
            this.e = (f ? 3 : 4);
            this.d = new byte[this.e];
            this.b = 0;
            this.c = 0;
        }
        
        public void close() throws IOException {
            this.flush();
            super.close();
            super.out.close();
            this.d = null;
            super.out = null;
        }
        
        public void flush() throws IOException {
            if (this.b > 0) {
                if (!this.f) {
                    throw new IOException("Base64 input not properly padded.");
                }
                super.out.write(b(this.d, this.b));
            }
            super.flush();
            super.out.flush();
        }
        
        public void write(final int n) throws IOException {
            this.d[this.b++] = (byte)n;
            if (this.b >= this.e) {
                if (this.f) {
                    super.out.write(b(this.d, this.e));
                    this.c += 4;
                    if (this.c >= 64) {
                        super.out.write(10);
                        this.c = 0;
                    }
                }
                else {
                    super.out.write(b(this.d));
                }
                this.b = 0;
            }
        }
        
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            for (int i = 0; i < n2; ++i) {
                this.write(array[n + i]);
            }
        }
    }
}
