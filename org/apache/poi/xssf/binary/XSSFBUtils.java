package org.apache.poi.xssf.binary;

import org.apache.poi.ooxml.POIXMLException;
import java.nio.charset.StandardCharsets;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Internal;

@Internal
public class XSSFBUtils
{
    static int readXLNullableWideString(final byte[] data, int offset, final StringBuilder sb) throws XSSFBParseException {
        final long numChars = LittleEndian.getUInt(data, offset);
        if (numChars < 0L) {
            throw new XSSFBParseException("too few chars to read");
        }
        if (numChars == 4294967295L) {
            return 0;
        }
        if (numChars > 4294967295L) {
            throw new XSSFBParseException("too many chars to read");
        }
        int numBytes = 2 * (int)numChars;
        offset += 4;
        if (offset + numBytes > data.length) {
            throw new XSSFBParseException("trying to read beyond data length: offset=" + offset + ", numBytes=" + numBytes + ", data.length=" + data.length);
        }
        sb.append(new String(data, offset, numBytes, StandardCharsets.UTF_16LE));
        numBytes += 4;
        return numBytes;
    }
    
    public static int readXLWideString(final byte[] data, int offset, final StringBuilder sb) throws XSSFBParseException {
        final long numChars = LittleEndian.getUInt(data, offset);
        if (numChars < 0L) {
            throw new XSSFBParseException("too few chars to read");
        }
        if (numChars > 4294967295L) {
            throw new XSSFBParseException("too many chars to read");
        }
        int numBytes = 2 * (int)numChars;
        offset += 4;
        if (offset + numBytes > data.length) {
            throw new XSSFBParseException("trying to read beyond data length");
        }
        sb.append(new String(data, offset, numBytes, StandardCharsets.UTF_16LE));
        numBytes += 4;
        return numBytes;
    }
    
    static int castToInt(final long val) {
        if (val < 2147483647L && val > -2147483648L) {
            return (int)val;
        }
        throw new POIXMLException("val (" + val + ") can't be cast to int");
    }
    
    static short castToShort(final int val) {
        if (val < 32767 && val > -32768) {
            return (short)val;
        }
        throw new POIXMLException("val (" + val + ") can't be cast to short");
    }
    
    static int get24BitInt(final byte[] data, final int offset) {
        int i = offset;
        final int b0 = data[i++] & 0xFF;
        final int b2 = data[i++] & 0xFF;
        final int b3 = data[i] & 0xFF;
        return (b3 << 16) + (b2 << 8) + b0;
    }
}
