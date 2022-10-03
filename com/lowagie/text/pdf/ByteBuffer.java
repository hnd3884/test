package com.lowagie.text.pdf;

import java.util.Locale;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.text.DecimalFormat;
import com.lowagie.text.DocWriter;
import java.text.DecimalFormatSymbols;
import java.io.OutputStream;

public class ByteBuffer extends OutputStream
{
    protected int count;
    protected byte[] buf;
    private static int byteCacheSize;
    private static byte[][] byteCache;
    public static final byte ZERO = 48;
    private static final char[] chars;
    private static final byte[] bytes;
    public static boolean HIGH_PRECISION;
    private static final DecimalFormatSymbols dfs;
    
    public ByteBuffer() {
        this(128);
    }
    
    public ByteBuffer(int size) {
        if (size < 1) {
            size = 128;
        }
        this.buf = new byte[size];
    }
    
    public static void setCacheSize(int size) {
        if (size > 3276700) {
            size = 3276700;
        }
        if (size <= ByteBuffer.byteCacheSize) {
            return;
        }
        final byte[][] tmpCache = new byte[size][];
        System.arraycopy(ByteBuffer.byteCache, 0, tmpCache, 0, ByteBuffer.byteCacheSize);
        ByteBuffer.byteCache = tmpCache;
        ByteBuffer.byteCacheSize = size;
    }
    
    public static void fillCache(final int decimals) {
        int step = 1;
        switch (decimals) {
            case 0: {
                step = 100;
                break;
            }
            case 1: {
                step = 10;
                break;
            }
        }
        for (int i = 1; i < ByteBuffer.byteCacheSize; i += step) {
            if (ByteBuffer.byteCache[i] == null) {
                ByteBuffer.byteCache[i] = convertToBytes(i);
            }
        }
    }
    
    private static byte[] convertToBytes(final int i) {
        int size = (int)Math.floor(Math.log(i) / Math.log(10.0));
        if (i % 100 != 0) {
            size += 2;
        }
        if (i % 10 != 0) {
            ++size;
        }
        if (i < 100) {
            ++size;
            if (i < 10) {
                ++size;
            }
        }
        final byte[] cache = new byte[--size];
        --size;
        if (i < 100) {
            cache[0] = 48;
        }
        if (i % 10 != 0) {
            cache[size--] = ByteBuffer.bytes[i % 10];
        }
        if (i % 100 != 0) {
            cache[size--] = ByteBuffer.bytes[i / 10 % 10];
            cache[size--] = 46;
        }
        size = (int)Math.floor(Math.log(i) / Math.log(10.0)) - 1;
        for (int add = 0; add < size; ++add) {
            cache[add] = ByteBuffer.bytes[i / (int)Math.pow(10.0, size - add + 1) % 10];
        }
        return cache;
    }
    
    public ByteBuffer append_i(final int b) {
        final int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        this.buf[this.count] = (byte)b;
        this.count = newcount;
        return this;
    }
    
    public ByteBuffer append(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0 || len == 0) {
            return this;
        }
        final int newcount = this.count + len;
        if (newcount > this.buf.length) {
            final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count = newcount;
        return this;
    }
    
    public ByteBuffer append(final byte[] b) {
        return this.append(b, 0, b.length);
    }
    
    public ByteBuffer append(final String str) {
        if (str != null) {
            return this.append(DocWriter.getISOBytes(str));
        }
        return this;
    }
    
    public ByteBuffer append(final char c) {
        return this.append_i(c);
    }
    
    public ByteBuffer append(final ByteBuffer buf) {
        return this.append(buf.buf, 0, buf.count);
    }
    
    public ByteBuffer append(final int i) {
        return this.append((double)i);
    }
    
    public ByteBuffer append(final byte b) {
        return this.append_i(b);
    }
    
    public ByteBuffer appendHex(final byte b) {
        this.append(ByteBuffer.bytes[b >> 4 & 0xF]);
        return this.append(ByteBuffer.bytes[b & 0xF]);
    }
    
    public ByteBuffer append(final float i) {
        return this.append((double)i);
    }
    
    public ByteBuffer append(final double d) {
        this.append(formatDouble(d, this));
        return this;
    }
    
    public static String formatDouble(final double d) {
        return formatDouble(d, null);
    }
    
    public static String formatDouble(double d, final ByteBuffer buf) {
        if (ByteBuffer.HIGH_PRECISION) {
            final DecimalFormat dn = new DecimalFormat("0.######", ByteBuffer.dfs);
            final String sform = dn.format(d);
            if (buf == null) {
                return sform;
            }
            buf.append(sform);
            return null;
        }
        else {
            boolean negative = false;
            if (Math.abs(d) < 1.5E-5) {
                if (buf != null) {
                    buf.append((byte)48);
                    return null;
                }
                return "0";
            }
            else {
                if (d < 0.0) {
                    negative = true;
                    d = -d;
                }
                if (d < 1.0) {
                    d += 5.0E-6;
                    if (d >= 1.0) {
                        if (negative) {
                            if (buf != null) {
                                buf.append((byte)45);
                                buf.append((byte)49);
                                return null;
                            }
                            return "-1";
                        }
                        else {
                            if (buf != null) {
                                buf.append((byte)49);
                                return null;
                            }
                            return "1";
                        }
                    }
                    else {
                        if (buf != null) {
                            final int v = (int)(d * 100000.0);
                            if (negative) {
                                buf.append((byte)45);
                            }
                            buf.append((byte)48);
                            buf.append((byte)46);
                            buf.append((byte)(v / 10000 + 48));
                            if (v % 10000 != 0) {
                                buf.append((byte)(v / 1000 % 10 + 48));
                                if (v % 1000 != 0) {
                                    buf.append((byte)(v / 100 % 10 + 48));
                                    if (v % 100 != 0) {
                                        buf.append((byte)(v / 10 % 10 + 48));
                                        if (v % 10 != 0) {
                                            buf.append((byte)(v % 10 + 48));
                                        }
                                    }
                                }
                            }
                            return null;
                        }
                        int x = 100000;
                        final int v2 = (int)(d * x);
                        final StringBuffer res = new StringBuffer();
                        if (negative) {
                            res.append('-');
                        }
                        res.append("0.");
                        while (v2 < x / 10) {
                            res.append('0');
                            x /= 10;
                        }
                        res.append(v2);
                        int cut;
                        for (cut = res.length() - 1; res.charAt(cut) == '0'; --cut) {}
                        res.setLength(cut + 1);
                        return res.toString();
                    }
                }
                else {
                    if (d > 32767.0) {
                        final StringBuffer res2 = new StringBuffer();
                        if (negative) {
                            res2.append('-');
                        }
                        d += 0.5;
                        final long v3 = (long)d;
                        return res2.append(v3).toString();
                    }
                    d += 0.005;
                    final int v = (int)(d * 100.0);
                    if (v < ByteBuffer.byteCacheSize && ByteBuffer.byteCache[v] != null) {
                        if (buf != null) {
                            if (negative) {
                                buf.append((byte)45);
                            }
                            buf.append(ByteBuffer.byteCache[v]);
                            return null;
                        }
                        String tmp = PdfEncodings.convertToString(ByteBuffer.byteCache[v], null);
                        if (negative) {
                            tmp = "-" + tmp;
                        }
                        return tmp;
                    }
                    else {
                        if (buf != null) {
                            if (v < ByteBuffer.byteCacheSize) {
                                int size = 0;
                                if (v >= 1000000) {
                                    size += 5;
                                }
                                else if (v >= 100000) {
                                    size += 4;
                                }
                                else if (v >= 10000) {
                                    size += 3;
                                }
                                else if (v >= 1000) {
                                    size += 2;
                                }
                                else if (v >= 100) {
                                    ++size;
                                }
                                if (v % 100 != 0) {
                                    size += 2;
                                }
                                if (v % 10 != 0) {
                                    ++size;
                                }
                                final byte[] cache = new byte[size];
                                int add = 0;
                                if (v >= 1000000) {
                                    cache[add++] = ByteBuffer.bytes[v / 1000000];
                                }
                                if (v >= 100000) {
                                    cache[add++] = ByteBuffer.bytes[v / 100000 % 10];
                                }
                                if (v >= 10000) {
                                    cache[add++] = ByteBuffer.bytes[v / 10000 % 10];
                                }
                                if (v >= 1000) {
                                    cache[add++] = ByteBuffer.bytes[v / 1000 % 10];
                                }
                                if (v >= 100) {
                                    cache[add++] = ByteBuffer.bytes[v / 100 % 10];
                                }
                                if (v % 100 != 0) {
                                    cache[add++] = 46;
                                    cache[add++] = ByteBuffer.bytes[v / 10 % 10];
                                    if (v % 10 != 0) {
                                        cache[add++] = ByteBuffer.bytes[v % 10];
                                    }
                                }
                                ByteBuffer.byteCache[v] = cache;
                            }
                            if (negative) {
                                buf.append((byte)45);
                            }
                            if (v >= 1000000) {
                                buf.append(ByteBuffer.bytes[v / 1000000]);
                            }
                            if (v >= 100000) {
                                buf.append(ByteBuffer.bytes[v / 100000 % 10]);
                            }
                            if (v >= 10000) {
                                buf.append(ByteBuffer.bytes[v / 10000 % 10]);
                            }
                            if (v >= 1000) {
                                buf.append(ByteBuffer.bytes[v / 1000 % 10]);
                            }
                            if (v >= 100) {
                                buf.append(ByteBuffer.bytes[v / 100 % 10]);
                            }
                            if (v % 100 != 0) {
                                buf.append((byte)46);
                                buf.append(ByteBuffer.bytes[v / 10 % 10]);
                                if (v % 10 != 0) {
                                    buf.append(ByteBuffer.bytes[v % 10]);
                                }
                            }
                            return null;
                        }
                        final StringBuffer res3 = new StringBuffer();
                        if (negative) {
                            res3.append('-');
                        }
                        if (v >= 1000000) {
                            res3.append(ByteBuffer.chars[v / 1000000]);
                        }
                        if (v >= 100000) {
                            res3.append(ByteBuffer.chars[v / 100000 % 10]);
                        }
                        if (v >= 10000) {
                            res3.append(ByteBuffer.chars[v / 10000 % 10]);
                        }
                        if (v >= 1000) {
                            res3.append(ByteBuffer.chars[v / 1000 % 10]);
                        }
                        if (v >= 100) {
                            res3.append(ByteBuffer.chars[v / 100 % 10]);
                        }
                        if (v % 100 != 0) {
                            res3.append('.');
                            res3.append(ByteBuffer.chars[v / 10 % 10]);
                            if (v % 10 != 0) {
                                res3.append(ByteBuffer.chars[v % 10]);
                            }
                        }
                        return res3.toString();
                    }
                }
            }
        }
    }
    
    public void reset() {
        this.count = 0;
    }
    
    public byte[] toByteArray() {
        final byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }
    
    public int size() {
        return this.count;
    }
    
    public void setSize(final int size) {
        if (size > this.count || size < 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("the.new.size.must.be.positive.and.lt.eq.of.the.current.size"));
        }
        this.count = size;
    }
    
    @Override
    public String toString() {
        return new String(this.buf, 0, this.count);
    }
    
    public String toString(final String enc) throws UnsupportedEncodingException {
        return new String(this.buf, 0, this.count, enc);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }
    
    @Override
    public void write(final int b) {
        this.append((byte)b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        this.append(b, off, len);
    }
    
    public byte[] getBuffer() {
        return this.buf;
    }
    
    static {
        ByteBuffer.byteCacheSize = 0;
        ByteBuffer.byteCache = new byte[ByteBuffer.byteCacheSize][];
        chars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        bytes = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
        ByteBuffer.HIGH_PRECISION = false;
        dfs = new DecimalFormatSymbols(Locale.US);
    }
}
