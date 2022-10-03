package com.theorem.radius3.radutil;

public class ByteIterator
{
    private byte[] a;
    private int b;
    private static final char[] c;
    private byte[] d;
    private int e;
    private int f;
    private int[] g;
    private int[] h;
    
    public ByteIterator(final byte[] array) {
        System.arraycopy(array, 0, this.a = new byte[array.length], 0, array.length);
        this.b = 0;
    }
    
    public ByteIterator(final byte[] a, final boolean b) {
        if (b) {
            System.arraycopy(a, 0, this.a = new byte[a.length], 0, a.length);
        }
        else {
            this.a = a;
        }
        this.b = 0;
    }
    
    public ByteIterator(final byte[] array, final int n, final int n2) {
        System.arraycopy(array, n, this.a = new byte[n2], 0, n2);
        this.b = 0;
    }
    
    public final int available() {
        return this.a.length - this.b;
    }
    
    public final byte[] getBuffer() {
        return this.a;
    }
    
    public final byte[] getBuffer(final int n, final int n2) {
        final byte[] array = new byte[n2];
        if (n < n2 && n2 != 0) {
            int n3 = this.a.length - n;
            if (n2 < n3) {
                n3 = n2;
            }
            System.arraycopy(this.a, n, array, 0, n3);
            return array;
        }
        return array;
    }
    
    public final byte first() {
        this.b = 0;
        return this.a[0];
    }
    
    public final byte last() {
        this.b = this.a.length - 1;
        return this.a[this.b];
    }
    
    public final void setIndex(final int b) {
        this.b = b;
    }
    
    public final void move(final int n) {
        this.b += n;
    }
    
    public final int current() {
        return this.b;
    }
    
    public final long nextLong() {
        return concoctLong(this.a, this.b);
    }
    
    public final void moveLong() {
        this.b += 8;
    }
    
    public final int nextInt() {
        return concoctInt(this.a, this.b);
    }
    
    public final void moveInt() {
        this.b += 4;
    }
    
    public final void move24() {
        this.b += 3;
    }
    
    public final short nextShort() {
        return concoctShort(this.a, this.b);
    }
    
    public final int nextUnsignedShort() {
        return concoctShort(this.a, this.b) & 0xFFFF;
    }
    
    public final int next24() {
        return concoct24(this.a, this.b);
    }
    
    public final int nextUnsignedByte() {
        return this.a[this.b] & 0xFF;
    }
    
    public final void moveShort() {
        this.b += 2;
    }
    
    public final byte nextByte() {
        return this.a[this.b];
    }
    
    public final void moveByte() {
        ++this.b;
    }
    
    public final void setByte(final byte b) {
        this.a[this.b] = b;
    }
    
    public final void setShort(final short n) {
        final byte[] decoct = decoct(n);
        this.a[this.b] = decoct[0];
        this.a[this.b + 1] = decoct[1];
    }
    
    public final void setInt(final int n) {
        final byte[] decoct = decoct(n);
        this.a[this.b] = decoct[0];
        this.a[this.b + 1] = decoct[1];
        this.a[this.b + 2] = decoct[2];
        this.a[this.b + 3] = decoct[3];
    }
    
    public final void set24(final int n) {
        final byte[] decoct = decoct(n);
        this.a[this.b] = decoct[1];
        this.a[this.b + 1] = decoct[2];
        this.a[this.b + 2] = decoct[3];
    }
    
    public final void setLong(final long n) {
        final byte[] decoct = decoct(n);
        this.a[this.b] = decoct[0];
        this.a[this.b + 1] = decoct[1];
        this.a[this.b + 2] = decoct[2];
        this.a[this.b + 3] = decoct[3];
        this.a[this.b + 4] = decoct[4];
        this.a[this.b + 5] = decoct[5];
        this.a[this.b + 6] = decoct[6];
        this.a[this.b + 7] = decoct[7];
    }
    
    public static long toLong(final byte[] array) {
        return concoctLong(array);
    }
    
    public static long toLong(final byte[] array, final int n) {
        return concoctLong(array, n);
    }
    
    public static long concoctLong(final byte[] array) {
        return ((long)array[0] & 0xFFL) << 56 | ((long)array[1] & 0xFFL) << 48 | ((long)array[2] & 0xFFL) << 40 | ((long)array[3] & 0xFFL) << 32 | ((long)array[4] & 0xFFL) << 24 | ((long)array[5] & 0xFFL) << 16 | ((long)array[6] & 0xFFL) << 8 | ((long)array[7] & 0xFFL);
    }
    
    public static long concoctLong(final byte[] array, final int n) {
        return ((long)concoctInt(array, n) & 0xFFFFFFFFL) << 32 | ((long)concoctInt(array, n + 4) & 0xFFFFFFFFL);
    }
    
    public static int toInt(final byte[] array) {
        return concoctInt(array);
    }
    
    public static int toInt(final byte[] array, final int n) {
        return concoctInt(array, n);
    }
    
    public static int concoctInt(final byte[] array) {
        return array[0] << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF);
    }
    
    public static int to24(final byte[] array) {
        return concoct24(array);
    }
    
    public static int to24(final byte[] array, final int n) {
        return concoct24(array, n);
    }
    
    public static int concoct24(final byte[] array) {
        return array[0] << 16 | (array[1] & 0xFF) << 8 | (array[2] & 0xFF);
    }
    
    public static int concoct24(final byte[] array, final int n) {
        return array[n + 0] << 16 | (array[n + 1] & 0xFF) << 8 | (array[n + 2] & 0xFF);
    }
    
    public static int concoctInt(final byte[] array, final int n) {
        return array[n] << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF);
    }
    
    public static short toShort(final byte[] array) {
        return concoctShort(array);
    }
    
    public static short toShort(final byte[] array, final int n) {
        return concoctShort(array, n);
    }
    
    public static short concoctShort(final byte[] array) {
        return (short)((array[0] & 0xFF) << 8 | (array[1] & 0xFF));
    }
    
    public static short concoctShort(final byte[] array, final int n) {
        return (short)((array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF));
    }
    
    public static byte[] decoct(final short n) {
        return new byte[] { (byte)(n >>> 8), (byte)n };
    }
    
    public static byte[] toBytes(final int n) {
        return decoct(n);
    }
    
    public static byte[] decoct(final int n) {
        return new byte[] { (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n };
    }
    
    public static byte[] toBytes(final long n) {
        return decoct(n);
    }
    
    public static byte[] toBytes24High(final int n) {
        return new byte[] { (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8) };
    }
    
    public static byte[] toBytes24Low(final int n) {
        return new byte[] { (byte)(n >>> 16), (byte)(n >>> 8), (byte)n };
    }
    
    public static byte[] decoct(final long n) {
        return new byte[] { (byte)(n >>> 56), (byte)(n >>> 48), (byte)(n >>> 40), (byte)(n >>> 32), (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n };
    }
    
    public final int readInt() {
        final int nextInt = this.nextInt();
        this.moveInt();
        return nextInt;
    }
    
    public final short readShort() {
        final short nextShort = this.nextShort();
        this.moveShort();
        return nextShort;
    }
    
    public final int read24() {
        final int concoct24 = concoct24(this.a, this.b);
        this.move24();
        return concoct24;
    }
    
    public final long readLong() {
        final long nextLong = this.nextLong();
        this.moveLong();
        return nextLong;
    }
    
    public final int readUnsignedShort() {
        final int nextUnsignedShort = this.nextUnsignedShort();
        this.moveShort();
        return nextUnsignedShort;
    }
    
    public final int readUnsignedByte() {
        final int n = this.nextByte() & 0xFF;
        this.moveByte();
        return n;
    }
    
    public final byte readByte() {
        final byte nextByte = this.nextByte();
        this.moveByte();
        return nextByte;
    }
    
    public final void skipBytes(final int n) {
        this.move(n);
    }
    
    public final int read(final byte[] array) {
        return this.read(array, 0, array.length);
    }
    
    public final int read(final byte[] array, final int n, int n2) {
        if (n + n2 > array.length) {
            n2 = array.length - n;
        }
        if (n2 > this.a.length - this.b) {
            n2 = this.a.length - this.b;
        }
        if (n2 <= 0) {
            return 0;
        }
        System.arraycopy(this.a, this.b, array, n, n2);
        this.move(n2);
        return n2;
    }
    
    public final void seek(final long n) {
        this.setIndex((int)n);
    }
    
    public final void seek(final int index) {
        this.setIndex(index);
    }
    
    public final void writeByte(final byte byte1) {
        this.setByte(byte1);
        this.moveByte();
    }
    
    public final void writeByte(final int n) {
        this.writeByte((byte)n);
    }
    
    public final void writeInt(final int int1) {
        this.setInt(int1);
        this.moveInt();
    }
    
    public final void write24(final int n) {
        this.set24(n);
        this.move24();
    }
    
    public final void writeLong(final long long1) {
        this.setLong(long1);
        this.moveLong();
    }
    
    public final void writeShort(final short short1) {
        this.setShort(short1);
        this.moveShort();
    }
    
    public final void writeShort(final int n) {
        this.writeShort((short)n);
    }
    
    public final int write(final byte[] array) {
        final int length = array.length;
        System.arraycopy(array, 0, this.a, this.current(), length);
        this.move(length);
        return length;
    }
    
    public final int write(final byte[] array, final int n, final int n2) {
        final int length = array.length;
        if (this.current() + length > this.a.length) {
            return -1;
        }
        System.arraycopy(array, n, this.a, this.current(), n2);
        this.move(length);
        return length;
    }
    
    public final long length() {
        return this.a.length;
    }
    
    public final byte[] extend(final int n) {
        final byte[] a = new byte[this.a.length + n];
        System.arraycopy(this.a, 0, a, 0, (a.length > this.a.length) ? this.a.length : a.length);
        return this.a = a;
    }
    
    public final String readLine() {
        if (this.b == this.a.length) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        try {
            byte nextByte;
            while ((nextByte = this.nextByte()) != -1) {
                ++this.b;
                if (nextByte == 13) {
                    break;
                }
                if (nextByte == 10) {
                    break;
                }
                sb.append((char)nextByte);
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            this.last();
        }
        return sb.toString();
    }
    
    public static String dump(final byte[] array, final int n, final int n2) {
        return new ByteIterator(array, false).dump(n, n2);
    }
    
    public final String dump(final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        final int current = this.current();
        final byte[] array = new byte[16];
        final int n3 = n2 + n;
        final int a = this.a(n3);
        this.seek(n);
        for (int i = n; i < n3; i += 16) {
            int read = this.read(array, 0, 16);
            if (i + 16 > n3) {
                read = n2 % 16;
            }
            final int n4 = (read > 8) ? 8 : read;
            final int n5 = (8 - n4) * 3;
            final int n6 = read - n4;
            final int n7 = (8 - n6) * 3 - 1;
            sb.append(this.a(i, a)).append(": ");
            sb.append(a(array, 0, n4));
            if (n5 > 0) {
                sb.append("                        ".substring(0, n5));
            }
            sb.append(" - ");
            sb.append(a(array, 8, n6));
            if (n7 > 0) {
                sb.append("                        ".substring(0, n7));
            }
            sb.append("  ");
            for (int j = 0; j < read; ++j) {
                if (j == 8) {
                    sb.append(" - ");
                }
                if (Ctype.isprint(array[j])) {
                    if (Ctype.isspace(array[j])) {
                        sb.append(' ');
                    }
                    else {
                        sb.append((char)array[j]);
                    }
                }
                else {
                    sb.append('.');
                }
            }
            sb.append('\n');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        this.seek(current);
        return sb.toString();
    }
    
    private final int a(final int n) {
        int n2 = 0;
        for (int i = n, n3 = 0; i > 0; i >>>= 4, ++n2, ++n3) {}
        if (++n2 < 2) {
            n2 = 2;
        }
        return n2;
    }
    
    private final String a(final int n, final int n2) {
        final StringBuffer sb = new StringBuffer(Integer.toHexString(n));
        while (sb.length() < n2) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }
    
    private static String a(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int length = array.length, n3 = 0, n4 = n; n4 < length && n3 < n2; ++n4, ++n3) {
            sb.append(ByteIterator.c[array[n4] >>> 4 & 0xF]).append(ByteIterator.c[array[n4] & 0xF]).append(' ');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public final void compile(final String s) {
        if (this.g == null) {
            this.g = new int[256];
        }
        this.d = s.getBytes();
        this.e = this.d.length;
        final int[] array = new int[this.e];
        this.h = new int[this.e];
        final int e = this.e;
        for (int i = 0; i < 256; ++i) {
            this.g[i] = e;
        }
        for (int j = 1; j <= e; ++j) {
            this.h[j - 1] = (e << 1) - j;
            this.g[this.d[j - 1]] = e - j;
        }
        int n = e + 1;
        for (int k = e; k > 0; --k) {
            array[k - 1] = n;
            while (n <= e && this.d[k - 1] != this.d[n - 1]) {
                this.h[n - 1] = ((this.h[n - 1] < e - k) ? this.h[n - 1] : (e - k));
                n = array[n - 1];
            }
            --n;
        }
        int l = n;
        int n2 = e + 1 - l;
        int n3 = 1;
        int n4 = 0;
        for (int n5 = 1; n5 <= n2; ++n5) {
            array[n5 - 1] = n4;
            while (n4 >= 1 && this.d[n5 - 1] != this.d[n4 - 1]) {
                n4 = array[n4 - 1];
            }
            ++n4;
        }
        while (l < e) {
            for (int n6 = n3; n6 <= l; ++n6) {
                this.h[n6 - 1] = ((this.h[n6 - 1] < e + l - n6) ? this.h[n6 - 1] : (e + l - n6));
            }
            n3 = l + 1;
            l = l + n2 - array[n2 - 1];
            n2 = array[n2 - 1];
        }
    }
    
    public final int search(final int n, final int n2) {
        final byte[] a = this.a;
        final int n3 = n2 + n;
        this.f = -1;
        if (n > this.a.length) {
            return -1;
        }
        if (this.h == null) {
            return -1;
        }
        final int e = this.e;
        if (e == 0) {
            return 0;
        }
        int n4 = 0;
        int n5;
        int i;
        for (n5 = 0, i = n + e - 1; i < n3; i += n5) {
            for (n4 = e - 1; n4 >= 0 && a[i] == this.d[n4]; --i, --n4) {}
            if (n4 == -1) {
                return i + 1;
            }
            final int n6 = this.g[a[i]];
            n5 = ((n6 > this.h[n4]) ? n6 : this.h[n4]);
        }
        if (i >= n3 && n4 > 0) {
            this.f = i - n5 - 1;
            return -1;
        }
        return -1;
    }
    
    public final int partialMatch() {
        return this.f;
    }
    
    static {
        c = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
