package com.dd.plist;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CharsetEncoder;

public class NSString extends NSObject implements Comparable<Object>
{
    private String content;
    private static CharsetEncoder asciiEncoder;
    private static CharsetEncoder utf16beEncoder;
    private static CharsetEncoder utf8Encoder;
    
    public NSString(final byte[] bytes, final String encoding) throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, encoding);
    }
    
    public NSString(final byte[] bytes, final int startIndex, final int endIndex, final String encoding) throws UnsupportedEncodingException {
        this.content = new String(bytes, startIndex, endIndex - startIndex, encoding);
    }
    
    public NSString(final String string) {
        this.content = string;
    }
    
    public int intValue() {
        final double d = this.doubleValue();
        if (d > 2.147483647E9) {
            return Integer.MAX_VALUE;
        }
        if (d < -2.147483648E9) {
            return Integer.MIN_VALUE;
        }
        return (int)d;
    }
    
    public float floatValue() {
        final double d = this.doubleValue();
        if (d > 3.4028234663852886E38) {
            return Float.MAX_VALUE;
        }
        if (d < -3.4028234663852886E38) {
            return -3.4028235E38f;
        }
        return (float)d;
    }
    
    public double doubleValue() {
        final Scanner s = new Scanner(this.content.trim()).useLocale(Locale.ROOT).useDelimiter("[^0-9.+-]+");
        if (s.hasNextDouble()) {
            return s.nextDouble();
        }
        return 0.0;
    }
    
    public boolean boolValue() {
        final Scanner s = new Scanner(this.content.trim()).useLocale(Locale.ROOT);
        return s.hasNext("([+-]?[0]*)?[YyTt1-9].*");
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String c) {
        this.content = c;
    }
    
    public void append(final NSString s) {
        this.append(s.getContent());
    }
    
    public void append(final String s) {
        this.content += s;
    }
    
    public void prepend(final String s) {
        this.content = s + this.content;
    }
    
    public void prepend(final NSString s) {
        this.prepend(s.getContent());
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && this.getClass() == obj.getClass() && this.content.equals(((NSString)obj).content);
    }
    
    @Override
    public int hashCode() {
        return this.content.hashCode();
    }
    
    @Override
    public String toString() {
        return this.content;
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<string>");
        synchronized (NSString.class) {
            if (NSString.utf8Encoder == null) {
                NSString.utf8Encoder = Charset.forName("UTF-8").newEncoder();
            }
            else {
                NSString.utf8Encoder.reset();
            }
            try {
                final ByteBuffer byteBuf = NSString.utf8Encoder.encode(CharBuffer.wrap(this.content));
                final byte[] bytes = new byte[byteBuf.remaining()];
                byteBuf.get(bytes);
                this.content = new String(bytes, "UTF-8");
            }
            catch (final Exception ex) {
                throw new RuntimeException("Could not encode the NSString into UTF-8: " + String.valueOf(ex.getMessage()));
            }
        }
        if (this.content.contains("&") || this.content.contains("<") || this.content.contains(">")) {
            xml.append("<![CDATA[");
            xml.append(this.content.replaceAll("]]>", "]]]]><![CDATA[>"));
            xml.append("]]>");
        }
        else {
            xml.append(this.content);
        }
        xml.append("</string>");
    }
    
    public void toBinary(final BinaryPropertyListWriter out) throws IOException {
        final CharBuffer charBuf = CharBuffer.wrap(this.content);
        int kind;
        ByteBuffer byteBuf;
        synchronized (NSString.class) {
            if (NSString.asciiEncoder == null) {
                NSString.asciiEncoder = Charset.forName("ASCII").newEncoder();
            }
            else {
                NSString.asciiEncoder.reset();
            }
            if (NSString.asciiEncoder.canEncode((CharSequence)charBuf)) {
                kind = 5;
                byteBuf = NSString.asciiEncoder.encode(charBuf);
            }
            else {
                if (NSString.utf16beEncoder == null) {
                    NSString.utf16beEncoder = Charset.forName("UTF-16BE").newEncoder();
                }
                else {
                    NSString.utf16beEncoder.reset();
                }
                kind = 6;
                byteBuf = NSString.utf16beEncoder.encode(charBuf);
            }
        }
        final byte[] bytes = new byte[byteBuf.remaining()];
        byteBuf.get(bytes);
        out.writeIntHeader(kind, this.content.length());
        out.write(bytes);
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append("\"");
        ascii.append(escapeStringForASCII(this.content));
        ascii.append("\"");
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append("\"");
        ascii.append(escapeStringForASCII(this.content));
        ascii.append("\"");
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof NSString) {
            return this.getContent().compareTo(((NSString)o).getContent());
        }
        if (o instanceof String) {
            return this.getContent().compareTo((String)o);
        }
        return -1;
    }
    
    static String escapeStringForASCII(final String s) {
        final StringBuilder out = new StringBuilder();
        final char[] cArray = s.toCharArray();
        for (int i = 0; i < cArray.length; ++i) {
            final char c = cArray[i];
            if (c > '\u007f') {
                out.append("\\U");
                String hex;
                for (hex = Integer.toHexString(c); hex.length() < 4; hex = "0" + hex) {}
                out.append(hex);
            }
            else if (c == '\\') {
                out.append("\\\\");
            }
            else if (c == '\"') {
                out.append("\\\"");
            }
            else if (c == '\b') {
                out.append("\\b");
            }
            else if (c == '\n') {
                out.append("\\n");
            }
            else if (c == '\r') {
                out.append("\\r");
            }
            else if (c == '\t') {
                out.append("\\t");
            }
            else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
