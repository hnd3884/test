package org.apache.poi.util;

import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Internal
public class HexDump
{
    public static final String EOL;
    public static final Charset UTF8;
    
    private HexDump() {
    }
    
    public static void dump(final byte[] data, final long offset, final OutputStream stream, final int index, final int length) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (stream == null) {
            throw new IllegalArgumentException("cannot write to nullstream");
        }
        final OutputStreamWriter osw = new OutputStreamWriter(stream, HexDump.UTF8);
        osw.write(dump(data, offset, index, length));
        osw.flush();
    }
    
    public static synchronized void dump(final byte[] data, final long offset, final OutputStream stream, final int index) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        dump(data, offset, stream, index, Integer.MAX_VALUE);
    }
    
    public static String dump(final byte[] data, final long offset, final int index) {
        return dump(data, offset, index, Integer.MAX_VALUE);
    }
    
    public static String dump(final byte[] data, final long offset, final int index, final int length) {
        if (data == null || data.length == 0) {
            return "No Data" + HexDump.EOL;
        }
        final int data_length = (length == Integer.MAX_VALUE || length < 0 || index + length < 0) ? data.length : Math.min(data.length, index + length);
        if (index < 0 || index >= data.length) {
            final String err = "illegal index: " + index + " into array of length " + data.length;
            throw new ArrayIndexOutOfBoundsException(err);
        }
        long display_offset = offset + index;
        final StringBuilder buffer = new StringBuilder(74);
        for (int j = index; j < data_length; j += 16) {
            int chars_read = data_length - j;
            if (chars_read > 16) {
                chars_read = 16;
            }
            writeHex(buffer, display_offset, 8, "");
            for (int k = 0; k < 16; ++k) {
                if (k < chars_read) {
                    writeHex(buffer, data[k + j], 2, " ");
                }
                else {
                    buffer.append("   ");
                }
            }
            buffer.append(' ');
            for (int k = 0; k < chars_read; ++k) {
                buffer.append(toAscii(data[k + j]));
            }
            buffer.append(HexDump.EOL);
            display_offset += chars_read;
        }
        return buffer.toString();
    }
    
    public static char toAscii(final int dataB) {
        char charB = (char)(dataB & 0xFF);
        if (Character.isISOControl(charB)) {
            return '.';
        }
        switch (charB) {
            case '\u00dd':
            case '\u00ff': {
                charB = '.';
                break;
            }
        }
        return charB;
    }
    
    public static String toHex(final byte[] value) {
        final StringBuilder retVal = new StringBuilder();
        retVal.append('[');
        if (value != null && value.length > 0) {
            for (int x = 0; x < value.length; ++x) {
                if (x > 0) {
                    retVal.append(", ");
                }
                retVal.append(toHex(value[x]));
            }
        }
        retVal.append(']');
        return retVal.toString();
    }
    
    public static String toHex(final short[] value) {
        final StringBuilder retVal = new StringBuilder();
        retVal.append('[');
        for (int x = 0; x < value.length; ++x) {
            if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        retVal.append(']');
        return retVal.toString();
    }
    
    public static String toHex(final byte[] value, final int bytesPerLine) {
        if (value.length == 0) {
            return ": 0";
        }
        final int digits = (int)Math.round(Math.log(value.length) / Math.log(10.0) + 0.5);
        final StringBuilder retVal = new StringBuilder();
        writeHex(retVal, 0L, digits, "");
        retVal.append(": ");
        int x = 0;
        int i = -1;
        while (x < value.length) {
            if (++i == bytesPerLine) {
                retVal.append('\n');
                writeHex(retVal, x, digits, "");
                retVal.append(": ");
                i = 0;
            }
            else if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
            ++x;
        }
        return retVal.toString();
    }
    
    public static String toHex(final short value) {
        final StringBuilder sb = new StringBuilder(4);
        writeHex(sb, value & 0xFFFF, 4, "");
        return sb.toString();
    }
    
    public static String toHex(final byte value) {
        final StringBuilder sb = new StringBuilder(2);
        writeHex(sb, value & 0xFF, 2, "");
        return sb.toString();
    }
    
    public static String toHex(final int value) {
        final StringBuilder sb = new StringBuilder(8);
        writeHex(sb, (long)value & 0xFFFFFFFFL, 8, "");
        return sb.toString();
    }
    
    public static String toHex(final long value) {
        final StringBuilder sb = new StringBuilder(16);
        writeHex(sb, value, 16, "");
        return sb.toString();
    }
    
    public static String toHex(final String value) {
        return (value == null || value.length() == 0) ? "[]" : toHex(value.getBytes(LocaleUtil.CHARSET_1252));
    }
    
    public static void dump(final InputStream in, final PrintStream out, final int start, final int bytesToDump) throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if (bytesToDump == -1) {
            for (int c = in.read(); c != -1; c = in.read()) {
                buf.write(c);
            }
        }
        else {
            int bytesRemaining = bytesToDump;
            while (bytesRemaining-- > 0) {
                final int c2 = in.read();
                if (c2 == -1) {
                    break;
                }
                buf.write(c2);
            }
        }
        final byte[] data = buf.toByteArray();
        dump(data, 0L, out, start, data.length);
    }
    
    public static String longToHex(final long value) {
        final StringBuilder sb = new StringBuilder(18);
        writeHex(sb, value, 16, "0x");
        return sb.toString();
    }
    
    public static String intToHex(final int value) {
        final StringBuilder sb = new StringBuilder(10);
        writeHex(sb, (long)value & 0xFFFFFFFFL, 8, "0x");
        return sb.toString();
    }
    
    public static String shortToHex(final int value) {
        final StringBuilder sb = new StringBuilder(6);
        writeHex(sb, (long)value & 0xFFFFL, 4, "0x");
        return sb.toString();
    }
    
    public static String byteToHex(final int value) {
        final StringBuilder sb = new StringBuilder(4);
        writeHex(sb, (long)value & 0xFFL, 2, "0x");
        return sb.toString();
    }
    
    private static void writeHex(final StringBuilder sb, final long value, final int nDigits, final String prefix) {
        sb.append(prefix);
        final char[] buf = new char[nDigits];
        long acc = value;
        for (int i = nDigits - 1; i >= 0; --i) {
            final int digit = Math.toIntExact(acc & 0xFL);
            buf[i] = (char)((digit < 10) ? (48 + digit) : (65 + digit - 10));
            acc >>>= 4;
        }
        sb.append(buf);
    }
    
    public static void main(final String[] args) throws IOException {
        final InputStream in = new FileInputStream(args[0]);
        final byte[] b = IOUtils.toByteArray(in);
        in.close();
        System.out.println(dump(b, 0L, 0));
    }
    
    static {
        EOL = System.getProperty("line.separator");
        UTF8 = StandardCharsets.UTF_8;
    }
}
