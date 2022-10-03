package org.apache.tika.detect;

import java.io.IOException;
import java.util.regex.Matcher;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.io.CharArrayWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.tika.mime.MediaType;

public class MagicDetector implements Detector
{
    private final MediaType type;
    private final int length;
    private final byte[] pattern;
    private final int patternLength;
    private final boolean isRegex;
    private final boolean isStringIgnoreCase;
    private final byte[] mask;
    private final int offsetRangeBegin;
    private final int offsetRangeEnd;
    
    public MagicDetector(final MediaType type, final byte[] pattern) {
        this(type, pattern, 0);
    }
    
    public MagicDetector(final MediaType type, final byte[] pattern, final int offset) {
        this(type, pattern, null, offset, offset);
    }
    
    public MagicDetector(final MediaType type, final byte[] pattern, final byte[] mask, final int offsetRangeBegin, final int offsetRangeEnd) {
        this(type, pattern, mask, false, offsetRangeBegin, offsetRangeEnd);
    }
    
    public MagicDetector(final MediaType type, final byte[] pattern, final byte[] mask, final boolean isRegex, final int offsetRangeBegin, final int offsetRangeEnd) {
        this(type, pattern, mask, isRegex, false, offsetRangeBegin, offsetRangeEnd);
    }
    
    public MagicDetector(final MediaType type, final byte[] pattern, final byte[] mask, final boolean isRegex, final boolean isStringIgnoreCase, final int offsetRangeBegin, final int offsetRangeEnd) {
        if (type == null) {
            throw new IllegalArgumentException("Matching media type is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Magic match pattern is null");
        }
        if (offsetRangeBegin < 0 || offsetRangeEnd < offsetRangeBegin) {
            throw new IllegalArgumentException("Invalid offset range: [" + offsetRangeBegin + "," + offsetRangeEnd + "]");
        }
        this.type = type;
        this.isRegex = isRegex;
        this.isStringIgnoreCase = isStringIgnoreCase;
        this.patternLength = Math.max(pattern.length, (mask != null) ? mask.length : 0);
        if (this.isRegex) {
            this.length = 8192;
        }
        else {
            this.length = this.patternLength;
        }
        this.mask = new byte[this.patternLength];
        this.pattern = new byte[this.patternLength];
        for (int i = 0; i < this.patternLength; ++i) {
            if (mask != null && i < mask.length) {
                this.mask[i] = mask[i];
            }
            else {
                this.mask[i] = -1;
            }
            if (i < pattern.length) {
                this.pattern[i] = (byte)(pattern[i] & this.mask[i]);
            }
            else {
                this.pattern[i] = 0;
            }
        }
        this.offsetRangeBegin = offsetRangeBegin;
        this.offsetRangeEnd = offsetRangeEnd;
    }
    
    public static MagicDetector parse(final MediaType mediaType, final String type, final String offset, final String value, final String mask) {
        int start = 0;
        int end = 0;
        if (offset != null) {
            final int colon = offset.indexOf(58);
            if (colon == -1) {
                start = (end = Integer.parseInt(offset));
            }
            else {
                start = Integer.parseInt(offset.substring(0, colon));
                end = Integer.parseInt(offset.substring(colon + 1));
            }
        }
        final byte[] patternBytes = decodeValue(value, type);
        byte[] maskBytes = null;
        if (mask != null) {
            maskBytes = decodeValue(mask, type);
        }
        return new MagicDetector(mediaType, patternBytes, maskBytes, type.equals("regex"), type.equals("stringignorecase"), start, end);
    }
    
    private static byte[] decodeValue(final String value, final String type) {
        if (value == null || type == null) {
            return null;
        }
        byte[] decoded = null;
        String tmpVal = null;
        int radix = 8;
        if (value.startsWith("0x")) {
            tmpVal = value.substring(2);
            radix = 16;
        }
        else {
            tmpVal = value;
            radix = 8;
        }
        switch (type) {
            case "string":
            case "regex":
            case "unicodeLE":
            case "unicodeBE": {
                decoded = decodeString(value, type);
                break;
            }
            case "stringignorecase": {
                decoded = decodeString(value.toLowerCase(Locale.ROOT), type);
                break;
            }
            case "byte": {
                decoded = tmpVal.getBytes(StandardCharsets.UTF_8);
                break;
            }
            case "host16":
            case "little16": {
                final int i = Integer.parseInt(tmpVal, radix);
                decoded = new byte[] { (byte)(i & 0xFF), (byte)(i >> 8) };
                break;
            }
            case "big16": {
                final int i = Integer.parseInt(tmpVal, radix);
                decoded = new byte[] { (byte)(i >> 8), (byte)(i & 0xFF) };
                break;
            }
            case "host32":
            case "little32": {
                final long j = Long.parseLong(tmpVal, radix);
                decoded = new byte[] { (byte)(j & 0xFFL), (byte)((j & 0xFF00L) >> 8), (byte)((j & 0xFF0000L) >> 16), (byte)((j & 0xFFFFFFFFFF000000L) >> 24) };
                break;
            }
            case "big32": {
                final long j = Long.parseLong(tmpVal, radix);
                decoded = new byte[] { (byte)((j & 0xFFFFFFFFFF000000L) >> 24), (byte)((j & 0xFF0000L) >> 16), (byte)((j & 0xFF00L) >> 8), (byte)(j & 0xFFL) };
                break;
            }
        }
        return decoded;
    }
    
    private static byte[] decodeString(final String value, final String type) {
        if (value.startsWith("0x")) {
            final byte[] vals = new byte[(value.length() - 2) / 2];
            for (int i = 0; i < vals.length; ++i) {
                vals[i] = (byte)Integer.parseInt(value.substring(2 + i * 2, 4 + i * 2), 16);
            }
            return vals;
        }
        final CharArrayWriter decoded = new CharArrayWriter();
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == '\\') {
                if (value.charAt(i + 1) == '\\') {
                    decoded.write(92);
                    ++i;
                }
                else if (value.charAt(i + 1) == 'x') {
                    decoded.write(Integer.parseInt(value.substring(i + 2, i + 4), 16));
                    i += 3;
                }
                else if (value.charAt(i + 1) == 'r') {
                    decoded.write(13);
                    ++i;
                }
                else if (value.charAt(i + 1) == 'n') {
                    decoded.write(10);
                    ++i;
                }
                else {
                    int j;
                    for (j = i + 1; j < i + 4 && j < value.length() && Character.isDigit(value.charAt(j)); ++j) {}
                    decoded.write(Short.decode("0" + value.substring(i + 1, j)).byteValue());
                    i = j - 1;
                }
            }
            else {
                decoded.write(value.charAt(i));
            }
        }
        final char[] chars = decoded.toCharArray();
        byte[] bytes;
        if ("unicodeLE".equals(type)) {
            bytes = new byte[chars.length * 2];
            for (int k = 0; k < chars.length; ++k) {
                bytes[k * 2] = (byte)(chars[k] & '\u00ff');
                bytes[k * 2 + 1] = (byte)(chars[k] >> 8);
            }
        }
        else if ("unicodeBE".equals(type)) {
            bytes = new byte[chars.length * 2];
            for (int k = 0; k < chars.length; ++k) {
                bytes[k * 2] = (byte)(chars[k] >> 8);
                bytes[k * 2 + 1] = (byte)(chars[k] & '\u00ff');
            }
        }
        else {
            bytes = new byte[chars.length];
            for (int k = 0; k < bytes.length; ++k) {
                bytes[k] = (byte)chars[k];
            }
        }
        return bytes;
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        if (input == null) {
            return MediaType.OCTET_STREAM;
        }
        input.mark(this.offsetRangeEnd + this.length);
        try {
            int offset = 0;
            while (offset < this.offsetRangeBegin) {
                final long n = input.skip(this.offsetRangeBegin - offset);
                if (n > 0L) {
                    offset += (int)n;
                }
                else {
                    if (input.read() == -1) {
                        return MediaType.OCTET_STREAM;
                    }
                    ++offset;
                }
            }
            final byte[] buffer = new byte[this.length + (this.offsetRangeEnd - this.offsetRangeBegin)];
            int n2 = input.read(buffer);
            if (n2 > 0) {
                offset += n2;
            }
            while (n2 != -1 && offset < this.offsetRangeEnd + this.length) {
                final int bufferOffset = offset - this.offsetRangeBegin;
                n2 = input.read(buffer, bufferOffset, buffer.length - bufferOffset);
                if (n2 > 0) {
                    offset += n2;
                }
            }
            if (this.isRegex) {
                int flags = 0;
                if (this.isStringIgnoreCase) {
                    flags = 2;
                }
                final Pattern p = Pattern.compile(new String(this.pattern, StandardCharsets.UTF_8), flags);
                final ByteBuffer bb = ByteBuffer.wrap(buffer);
                final CharBuffer result = StandardCharsets.ISO_8859_1.decode(bb);
                final Matcher m = p.matcher(result);
                boolean match = false;
                for (int i = 0; i <= this.offsetRangeEnd - this.offsetRangeBegin; ++i) {
                    m.region(i, this.length + i);
                    match = m.lookingAt();
                    if (match) {
                        return this.type;
                    }
                }
            }
            else {
                if (offset < this.offsetRangeBegin + this.length) {
                    return MediaType.OCTET_STREAM;
                }
                for (int j = 0; j <= this.offsetRangeEnd - this.offsetRangeBegin; ++j) {
                    boolean match2 = true;
                    int masked;
                    for (int k = 0; match2 && k < this.length; match2 = (masked == this.pattern[k]), ++k) {
                        masked = (buffer[j + k] & this.mask[k]);
                        if (this.isStringIgnoreCase) {
                            masked = Character.toLowerCase(masked);
                        }
                    }
                    if (match2) {
                        return this.type;
                    }
                }
            }
            return MediaType.OCTET_STREAM;
        }
        finally {
            input.reset();
        }
    }
    
    public int getLength() {
        return this.patternLength;
    }
    
    @Override
    public String toString() {
        return "Magic Detection for " + this.type + " looking for " + this.pattern.length + " bytes = " + this.pattern + " mask = " + this.mask;
    }
}
