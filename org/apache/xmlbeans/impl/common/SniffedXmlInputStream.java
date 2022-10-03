package org.apache.xmlbeans.impl.common;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.BufferedInputStream;

public class SniffedXmlInputStream extends BufferedInputStream
{
    public static int MAX_SNIFFED_BYTES;
    private static Charset dummy1;
    private static Charset dummy2;
    private static Charset dummy3;
    private static Charset dummy4;
    private static Charset dummy5;
    private static Charset dummy6;
    private static Charset dummy7;
    private String _encoding;
    private static char[] WHITESPACE;
    private static char[] NOTNAME;
    
    public SniffedXmlInputStream(final InputStream stream) throws IOException {
        super(stream);
        this._encoding = this.sniffFourBytes();
        if (this._encoding != null && this._encoding.equals("IBM037")) {
            final String encoding = this.sniffForXmlDecl(this._encoding);
            if (encoding != null) {
                this._encoding = encoding;
            }
        }
        if (this._encoding == null) {
            this._encoding = this.sniffForXmlDecl("UTF-8");
        }
        if (this._encoding == null) {
            this._encoding = "UTF-8";
        }
    }
    
    private int readAsMuchAsPossible(final byte[] buf, final int startAt, final int len) throws IOException {
        int total;
        int count;
        for (total = 0; total < len; total += count) {
            count = this.read(buf, startAt + total, len - total);
            if (count < 0) {
                break;
            }
        }
        return total;
    }
    
    private String sniffFourBytes() throws IOException {
        this.mark(4);
        final int skip = 0;
        try {
            final byte[] buf = new byte[4];
            if (this.readAsMuchAsPossible(buf, 0, 4) < 4) {
                return null;
            }
            final long result = (0xFF000000 & buf[0] << 24) | (0xFF0000 & buf[1] << 16) | (0xFF00 & buf[2] << 8) | (0xFF & buf[3]);
            if (result == 65279L) {
                return "UCS-4";
            }
            if (result == -131072L) {
                return "UCS-4";
            }
            if (result == 60L) {
                return "UCS-4BE";
            }
            if (result == 1006632960L) {
                return "UCS-4LE";
            }
            if (result == 3932223L) {
                return "UTF-16BE";
            }
            if (result == 1006649088L) {
                return "UTF-16LE";
            }
            if (result == 1010792557L) {
                return null;
            }
            if (result == 1282385812L) {
                return "IBM037";
            }
            if ((result & 0xFFFFFFFFFFFF0000L) == 0xFFFFFFFFFEFF0000L) {
                return "UTF-16";
            }
            if ((result & 0xFFFFFFFFFFFF0000L) == 0xFFFFFFFFFFFE0000L) {
                return "UTF-16";
            }
            if ((result & 0xFFFFFFFFFFFFFF00L) == 0xFFFFFFFFEFBBBF00L) {
                return "UTF-8";
            }
            return null;
        }
        finally {
            this.reset();
        }
    }
    
    private String sniffForXmlDecl(final String encoding) throws IOException {
        this.mark(SniffedXmlInputStream.MAX_SNIFFED_BYTES);
        try {
            final byte[] bytebuf = new byte[SniffedXmlInputStream.MAX_SNIFFED_BYTES];
            final int bytelimit = this.readAsMuchAsPossible(bytebuf, 0, SniffedXmlInputStream.MAX_SNIFFED_BYTES);
            final Charset charset = Charset.forName(encoding);
            final Reader reader = new InputStreamReader(new ByteArrayInputStream(bytebuf, 0, bytelimit), charset);
            final char[] buf = new char[bytelimit];
            int limit;
            int count;
            for (limit = 0; limit < bytelimit; limit += count) {
                count = reader.read(buf, limit, bytelimit - limit);
                if (count < 0) {
                    break;
                }
            }
            return extractXmlDeclEncoding(buf, 0, limit);
        }
        finally {
            this.reset();
        }
    }
    
    public String getXmlEncoding() {
        return this._encoding;
    }
    
    static String extractXmlDeclEncoding(final char[] buf, final int offset, final int size) {
        final int limit = offset + size;
        final int xmlpi = firstIndexOf("<?xml", buf, offset, limit);
        if (xmlpi >= 0) {
            int i = xmlpi + 5;
            final ScannedAttribute attr = new ScannedAttribute();
            while (i < limit) {
                i = scanAttribute(buf, i, limit, attr);
                if (i < 0) {
                    return null;
                }
                if (attr.name.equals("encoding")) {
                    return attr.value;
                }
            }
        }
        return null;
    }
    
    private static int firstIndexOf(final String s, final char[] buf, int startAt, int limit) {
        assert s.length() > 0;
        final char[] lookFor = s.toCharArray();
        final char firstchar = lookFor[0];
    Label_0088:
        for (limit -= lookFor.length; startAt < limit; ++startAt) {
            if (buf[startAt] == firstchar) {
                for (int i = 1; i < lookFor.length; ++i) {
                    if (buf[startAt + i] != lookFor[i]) {
                        continue Label_0088;
                    }
                }
                return startAt;
            }
        }
        return -1;
    }
    
    private static int nextNonmatchingByte(final char[] lookFor, final char[] buf, int startAt, final int limit) {
    Label_0000:
        while (startAt < limit) {
            final int thischar = buf[startAt];
            for (int i = 0; i < lookFor.length; ++i) {
                if (thischar == lookFor[i]) {
                    ++startAt;
                    continue Label_0000;
                }
            }
            return startAt;
        }
        return -1;
    }
    
    private static int nextMatchingByte(final char[] lookFor, final char[] buf, int startAt, final int limit) {
        while (startAt < limit) {
            final int thischar = buf[startAt];
            for (int i = 0; i < lookFor.length; ++i) {
                if (thischar == lookFor[i]) {
                    return startAt;
                }
            }
            ++startAt;
        }
        return -1;
    }
    
    private static int nextMatchingByte(final char lookFor, final char[] buf, int startAt, final int limit) {
        while (startAt < limit) {
            if (buf[startAt] == lookFor) {
                return startAt;
            }
            ++startAt;
        }
        return -1;
    }
    
    private static int scanAttribute(final char[] buf, final int startAt, final int limit, final ScannedAttribute attr) {
        final int nameStart = nextNonmatchingByte(SniffedXmlInputStream.WHITESPACE, buf, startAt, limit);
        if (nameStart < 0) {
            return -1;
        }
        final int nameEnd = nextMatchingByte(SniffedXmlInputStream.NOTNAME, buf, nameStart, limit);
        if (nameEnd < 0) {
            return -1;
        }
        final int equals = nextNonmatchingByte(SniffedXmlInputStream.WHITESPACE, buf, nameEnd, limit);
        if (equals < 0) {
            return -1;
        }
        if (buf[equals] != '=') {
            return -1;
        }
        final int valQuote = nextNonmatchingByte(SniffedXmlInputStream.WHITESPACE, buf, equals + 1, limit);
        if (buf[valQuote] != '\'' && buf[valQuote] != '\"') {
            return -1;
        }
        final int valEndquote = nextMatchingByte(buf[valQuote], buf, valQuote + 1, limit);
        if (valEndquote < 0) {
            return -1;
        }
        attr.name = new String(buf, nameStart, nameEnd - nameStart);
        attr.value = new String(buf, valQuote + 1, valEndquote - valQuote - 1);
        return valEndquote + 1;
    }
    
    static {
        SniffedXmlInputStream.MAX_SNIFFED_BYTES = 192;
        SniffedXmlInputStream.dummy1 = Charset.forName("UTF-8");
        SniffedXmlInputStream.dummy2 = Charset.forName("UTF-16");
        SniffedXmlInputStream.dummy3 = Charset.forName("UTF-16BE");
        SniffedXmlInputStream.dummy4 = Charset.forName("UTF-16LE");
        SniffedXmlInputStream.dummy5 = Charset.forName("ISO-8859-1");
        SniffedXmlInputStream.dummy6 = Charset.forName("US-ASCII");
        SniffedXmlInputStream.dummy7 = Charset.forName("Cp1252");
        SniffedXmlInputStream.WHITESPACE = new char[] { ' ', '\r', '\t', '\n' };
        SniffedXmlInputStream.NOTNAME = new char[] { '=', ' ', '\r', '\t', '\n', '?', '>', '<', '\'', '\"' };
    }
    
    private static class ScannedAttribute
    {
        public String name;
        public String value;
    }
}
