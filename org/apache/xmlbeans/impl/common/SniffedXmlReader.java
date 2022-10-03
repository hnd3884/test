package org.apache.xmlbeans.impl.common;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.io.BufferedReader;

public class SniffedXmlReader extends BufferedReader
{
    public static int MAX_SNIFFED_CHARS;
    private static Charset dummy1;
    private static Charset dummy2;
    private static Charset dummy3;
    private static Charset dummy4;
    private static Charset dummy5;
    private static Charset dummy6;
    private static Charset dummy7;
    private String _encoding;
    
    public SniffedXmlReader(final Reader reader) throws IOException {
        super(reader);
        this._encoding = this.sniffForXmlDecl();
    }
    
    private int readAsMuchAsPossible(final char[] buf, final int startAt, final int len) throws IOException {
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
    
    private String sniffForXmlDecl() throws IOException {
        this.mark(SniffedXmlReader.MAX_SNIFFED_CHARS);
        try {
            final char[] buf = new char[SniffedXmlReader.MAX_SNIFFED_CHARS];
            final int limit = this.readAsMuchAsPossible(buf, 0, SniffedXmlReader.MAX_SNIFFED_CHARS);
            return SniffedXmlInputStream.extractXmlDeclEncoding(buf, 0, limit);
        }
        finally {
            this.reset();
        }
    }
    
    public String getXmlEncoding() {
        return this._encoding;
    }
    
    static {
        SniffedXmlReader.MAX_SNIFFED_CHARS = 192;
        SniffedXmlReader.dummy1 = Charset.forName("UTF-8");
        SniffedXmlReader.dummy2 = Charset.forName("UTF-16");
        SniffedXmlReader.dummy3 = Charset.forName("UTF-16BE");
        SniffedXmlReader.dummy4 = Charset.forName("UTF-16LE");
        SniffedXmlReader.dummy5 = Charset.forName("ISO-8859-1");
        SniffedXmlReader.dummy6 = Charset.forName("US-ASCII");
        SniffedXmlReader.dummy7 = Charset.forName("Cp1252");
    }
}
