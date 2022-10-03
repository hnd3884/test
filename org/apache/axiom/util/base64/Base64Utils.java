package org.apache.axiom.util.base64;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.axiom.util.activation.DataSourceUtils;
import javax.activation.DataHandler;

public class Base64Utils
{
    private static int getEncodedSize(final int unencodedSize) {
        return (unencodedSize + 2) / 3 * 4;
    }
    
    private static int getBufferSize(final DataHandler dh) {
        final long size = DataSourceUtils.getSize(dh.getDataSource());
        if (size == -1L) {
            return 4096;
        }
        if (size > 2147483647L) {
            throw new IllegalArgumentException("DataHandler is too large to encode to string");
        }
        return getEncodedSize((int)size);
    }
    
    public static String encode(final DataHandler dh) throws IOException {
        final StringBuilder buffer = new StringBuilder(getBufferSize(dh));
        final Base64EncodingStringBufferOutputStream out = new Base64EncodingStringBufferOutputStream(buffer);
        dh.writeTo(out);
        out.complete();
        return buffer.toString();
    }
    
    public static char[] encodeToCharArray(final DataHandler dh) throws IOException {
        final NoCopyCharArrayWriter buffer = new NoCopyCharArrayWriter(getBufferSize(dh));
        final Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(buffer);
        dh.writeTo(out);
        out.complete();
        return buffer.toCharArray();
    }
    
    private static int decode0(final char[] ibuf, final byte[] obuf, int wp) {
        int outlen = 3;
        if (ibuf[3] == '=') {
            outlen = 2;
        }
        if (ibuf[2] == '=') {
            outlen = 1;
        }
        final int b0 = Base64Constants.S_DECODETABLE[ibuf[0]];
        final int b2 = Base64Constants.S_DECODETABLE[ibuf[1]];
        final int b3 = Base64Constants.S_DECODETABLE[ibuf[2]];
        final int b4 = Base64Constants.S_DECODETABLE[ibuf[3]];
        switch (outlen) {
            case 1: {
                obuf[wp] = (byte)((b0 << 2 & 0xFC) | (b2 >> 4 & 0x3));
                return 1;
            }
            case 2: {
                obuf[wp++] = (byte)((b0 << 2 & 0xFC) | (b2 >> 4 & 0x3));
                obuf[wp] = (byte)((b2 << 4 & 0xF0) | (b3 >> 2 & 0xF));
                return 2;
            }
            case 3: {
                obuf[wp++] = (byte)((b0 << 2 & 0xFC) | (b2 >> 4 & 0x3));
                obuf[wp++] = (byte)((b2 << 4 & 0xF0) | (b3 >> 2 & 0xF));
                obuf[wp] = (byte)((b3 << 6 & 0xC0) | (b4 & 0x3F));
                return 3;
            }
            default: {
                throw new RuntimeException("internalError00");
            }
        }
    }
    
    @Deprecated
    public static byte[] decode(final char[] data, final int off, final int len) {
        final char[] ibuf = new char[4];
        int ibufcount = 0;
        final byte[] obuf = new byte[len / 4 * 3 + 3];
        int obufcount = 0;
        for (int i = off; i < off + len; ++i) {
            final char ch = data[i];
            if (ch == '=' || (ch < Base64Constants.S_DECODETABLE.length && Base64Constants.S_DECODETABLE[ch] >= 0)) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    obufcount += decode0(ibuf, obuf, obufcount);
                }
            }
        }
        if (obufcount == obuf.length) {
            return obuf;
        }
        final byte[] ret = new byte[obufcount];
        System.arraycopy(obuf, 0, ret, 0, obufcount);
        return ret;
    }
    
    public static byte[] decode(final String data) {
        int symbols = 0;
        int padding = 0;
        for (int i = 0; i < data.length(); ++i) {
            switch (Base64Constants.S_DECODETABLE[data.charAt(i)]) {
                case -1: {
                    if (padding == 2) {
                        throw new IllegalArgumentException("Too much padding");
                    }
                    ++padding;
                    break;
                }
                case -2: {
                    break;
                }
                case -3: {
                    throw new IllegalArgumentException("Invalid character encountered");
                }
                default: {
                    if (padding > 0) {
                        throw new IllegalArgumentException("Unexpected padding character");
                    }
                    ++symbols;
                    break;
                }
            }
        }
        if ((symbols + padding) % 4 != 0) {
            throw new IllegalArgumentException("Missing padding");
        }
        final byte[] result = new byte[(symbols + padding) / 4 * 3 - padding];
        int pos = 0;
        int resultPos = 0;
        byte accumulator = 0;
        int bits = 0;
        while (symbols > 0) {
            final byte b = Base64Constants.S_DECODETABLE[data.charAt(pos++)];
            if (b == -2) {
                continue;
            }
            if (bits == 0) {
                accumulator = (byte)(b << 2);
                bits = 6;
            }
            else {
                accumulator |= (byte)(b >> bits - 2);
                result[resultPos++] = accumulator;
                accumulator = (byte)(b << 10 - bits);
                bits -= 2;
            }
            --symbols;
        }
        if (accumulator != 0) {
            throw new IllegalArgumentException("Invalid base64 value");
        }
        if (resultPos != result.length) {
            throw new Error("Oops. This is a bug.");
        }
        return result;
    }
    
    @Deprecated
    public static boolean isValidBase64Encoding(final String data) {
        for (int i = 0; i < data.length(); ++i) {
            final char ch = data.charAt(i);
            if (ch != '=') {
                if (ch >= Base64Constants.S_DECODETABLE.length || Base64Constants.S_DECODETABLE[ch] < 0) {
                    if (ch != '\r') {
                        if (ch != '\n') {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @Deprecated
    public static void decode(final char[] data, final int off, final int len, final OutputStream ostream) throws IOException {
        final char[] ibuf = new char[4];
        int ibufcount = 0;
        final byte[] obuf = new byte[3];
        for (int i = off; i < off + len; ++i) {
            final char ch = data[i];
            if (ch == '=' || (ch < Base64Constants.S_DECODETABLE.length && Base64Constants.S_DECODETABLE[ch] >= 0)) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    final int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }
    }
    
    @Deprecated
    public static void decode(final String data, final OutputStream ostream) throws IOException {
        final char[] ibuf = new char[4];
        int ibufcount = 0;
        final byte[] obuf = new byte[3];
        for (int i = 0; i < data.length(); ++i) {
            final char ch = data.charAt(i);
            if (ch == '=' || (ch < Base64Constants.S_DECODETABLE.length && Base64Constants.S_DECODETABLE[ch] >= 0)) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    final int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }
    }
    
    @Deprecated
    public static String encode(final byte[] data) {
        return encode(data, 0, data.length);
    }
    
    @Deprecated
    public static String encode(final byte[] data, final int off, final int len) {
        if (len <= 0) {
            return "";
        }
        final char[] out = new char[len / 3 * 4 + 4];
        int rindex = off;
        int windex = 0;
        int rest;
        for (rest = len - off; rest >= 3; rest -= 3) {
            final int i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 18];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 12 & 0x3F];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 6 & 0x3F];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i & 0x3F];
            rindex += 3;
        }
        if (rest == 1) {
            final int i = data[rindex] & 0xFF;
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 2];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i << 4 & 0x3F];
            out[windex++] = '=';
            out[windex++] = '=';
        }
        else if (rest == 2) {
            final int i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 10];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 4 & 0x3F];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i << 2 & 0x3F];
            out[windex++] = '=';
        }
        return new String(out, 0, windex);
    }
    
    @Deprecated
    public static void encode(final byte[] data, final int off, final int len, final StringBuffer buffer) {
        if (len <= 0) {
            return;
        }
        final char[] out = new char[4];
        int rindex = off;
        int rest;
        for (rest = len - off; rest >= 3; rest -= 3) {
            final int i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 18];
            out[1] = (char)Base64Constants.S_BASE64CHAR[i >> 12 & 0x3F];
            out[2] = (char)Base64Constants.S_BASE64CHAR[i >> 6 & 0x3F];
            out[3] = (char)Base64Constants.S_BASE64CHAR[i & 0x3F];
            buffer.append(out);
            rindex += 3;
        }
        if (rest == 1) {
            final int i = data[rindex] & 0xFF;
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 2];
            out[1] = (char)Base64Constants.S_BASE64CHAR[i << 4 & 0x3F];
            out[3] = (out[2] = '=');
            buffer.append(out);
        }
        else if (rest == 2) {
            final int i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 10];
            out[1] = (char)Base64Constants.S_BASE64CHAR[i >> 4 & 0x3F];
            out[2] = (char)Base64Constants.S_BASE64CHAR[i << 2 & 0x3F];
            out[3] = '=';
            buffer.append(out);
        }
    }
    
    @Deprecated
    public static void encode(final byte[] data, final int off, final int len, final OutputStream ostream) throws IOException {
        if (len <= 0) {
            return;
        }
        final byte[] out = new byte[4];
        int rindex = off;
        int rest;
        for (rest = len - off; rest >= 3; rest -= 3) {
            final int i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[0] = Base64Constants.S_BASE64CHAR[i >> 18];
            out[1] = Base64Constants.S_BASE64CHAR[i >> 12 & 0x3F];
            out[2] = Base64Constants.S_BASE64CHAR[i >> 6 & 0x3F];
            out[3] = Base64Constants.S_BASE64CHAR[i & 0x3F];
            ostream.write(out, 0, 4);
            rindex += 3;
        }
        if (rest == 1) {
            final int i = data[rindex] & 0xFF;
            out[0] = Base64Constants.S_BASE64CHAR[i >> 2];
            out[1] = Base64Constants.S_BASE64CHAR[i << 4 & 0x3F];
            out[3] = (out[2] = 61);
            ostream.write(out, 0, 4);
        }
        else if (rest == 2) {
            final int i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[0] = Base64Constants.S_BASE64CHAR[i >> 10];
            out[1] = Base64Constants.S_BASE64CHAR[i >> 4 & 0x3F];
            out[2] = Base64Constants.S_BASE64CHAR[i << 2 & 0x3F];
            out[3] = 61;
            ostream.write(out, 0, 4);
        }
    }
    
    @Deprecated
    public static void encode(final byte[] data, final int off, final int len, final Writer writer) throws IOException {
        if (len <= 0) {
            return;
        }
        final char[] out = new char[4];
        int rindex = off;
        int rest = len - off;
        int output = 0;
        while (rest >= 3) {
            final int i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 18];
            out[1] = (char)Base64Constants.S_BASE64CHAR[i >> 12 & 0x3F];
            out[2] = (char)Base64Constants.S_BASE64CHAR[i >> 6 & 0x3F];
            out[3] = (char)Base64Constants.S_BASE64CHAR[i & 0x3F];
            writer.write(out, 0, 4);
            rindex += 3;
            rest -= 3;
            output += 4;
            if (output % 76 == 0) {
                writer.write("\n");
            }
        }
        if (rest == 1) {
            final int i = data[rindex] & 0xFF;
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 2];
            out[1] = (char)Base64Constants.S_BASE64CHAR[i << 4 & 0x3F];
            out[3] = (out[2] = '=');
            writer.write(out, 0, 4);
        }
        else if (rest == 2) {
            final int i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 10];
            out[1] = (char)Base64Constants.S_BASE64CHAR[i >> 4 & 0x3F];
            out[2] = (char)Base64Constants.S_BASE64CHAR[i << 2 & 0x3F];
            out[3] = '=';
            writer.write(out, 0, 4);
        }
    }
}
