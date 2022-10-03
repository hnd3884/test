package org.apache.catalina.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.tomcat.util.buf.B2CConverter;
import java.util.BitSet;

public final class URLEncoder implements Cloneable
{
    private static final char[] hexadecimal;
    public static final URLEncoder DEFAULT;
    public static final URLEncoder QUERY;
    private final BitSet safeCharacters;
    private boolean encodeSpaceAsPlus;
    
    public URLEncoder() {
        this(new BitSet(256));
        for (char i = 'a'; i <= 'z'; ++i) {
            this.addSafeCharacter(i);
        }
        for (char i = 'A'; i <= 'Z'; ++i) {
            this.addSafeCharacter(i);
        }
        for (char i = '0'; i <= '9'; ++i) {
            this.addSafeCharacter(i);
        }
    }
    
    private URLEncoder(final BitSet safeCharacters) {
        this.encodeSpaceAsPlus = false;
        this.safeCharacters = safeCharacters;
    }
    
    public void addSafeCharacter(final char c) {
        this.safeCharacters.set(c);
    }
    
    public void removeSafeCharacter(final char c) {
        this.safeCharacters.clear(c);
    }
    
    public void setEncodeSpaceAsPlus(final boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
    }
    
    @Deprecated
    public String encode(final String path) {
        return this.encode(path, "UTF-8");
    }
    
    @Deprecated
    public String encode(final String path, final String encoding) {
        Charset charset;
        try {
            charset = B2CConverter.getCharset(encoding);
        }
        catch (final UnsupportedEncodingException e) {
            charset = Charset.defaultCharset();
        }
        return this.encode(path, charset);
    }
    
    public String encode(final String path, final Charset charset) {
        final int maxBytesPerChar = 10;
        final StringBuilder rewrittenPath = new StringBuilder(path.length());
        final ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
        final OutputStreamWriter writer = new OutputStreamWriter(buf, charset);
        for (int i = 0; i < path.length(); ++i) {
            final int c = path.charAt(i);
            if (this.safeCharacters.get(c)) {
                rewrittenPath.append((char)c);
            }
            else if (this.encodeSpaceAsPlus && c == 32) {
                rewrittenPath.append('+');
            }
            else {
                try {
                    writer.write((char)c);
                    writer.flush();
                }
                catch (final IOException e) {
                    buf.reset();
                    continue;
                }
                final byte[] arr$;
                final byte[] ba = arr$ = buf.toByteArray();
                for (final byte toEncode : arr$) {
                    rewrittenPath.append('%');
                    final int low = toEncode & 0xF;
                    final int high = (toEncode & 0xF0) >> 4;
                    rewrittenPath.append(URLEncoder.hexadecimal[high]);
                    rewrittenPath.append(URLEncoder.hexadecimal[low]);
                }
                buf.reset();
            }
        }
        return rewrittenPath.toString();
    }
    
    public Object clone() {
        final URLEncoder result = new URLEncoder((BitSet)this.safeCharacters.clone());
        result.setEncodeSpaceAsPlus(this.encodeSpaceAsPlus);
        return result;
    }
    
    static {
        hexadecimal = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        DEFAULT = new URLEncoder();
        QUERY = new URLEncoder();
        URLEncoder.DEFAULT.addSafeCharacter('-');
        URLEncoder.DEFAULT.addSafeCharacter('.');
        URLEncoder.DEFAULT.addSafeCharacter('_');
        URLEncoder.DEFAULT.addSafeCharacter('~');
        URLEncoder.DEFAULT.addSafeCharacter('!');
        URLEncoder.DEFAULT.addSafeCharacter('$');
        URLEncoder.DEFAULT.addSafeCharacter('&');
        URLEncoder.DEFAULT.addSafeCharacter('\'');
        URLEncoder.DEFAULT.addSafeCharacter('(');
        URLEncoder.DEFAULT.addSafeCharacter(')');
        URLEncoder.DEFAULT.addSafeCharacter('*');
        URLEncoder.DEFAULT.addSafeCharacter('+');
        URLEncoder.DEFAULT.addSafeCharacter(',');
        URLEncoder.DEFAULT.addSafeCharacter(';');
        URLEncoder.DEFAULT.addSafeCharacter('=');
        URLEncoder.DEFAULT.addSafeCharacter(':');
        URLEncoder.DEFAULT.addSafeCharacter('@');
        URLEncoder.DEFAULT.addSafeCharacter('/');
        URLEncoder.QUERY.setEncodeSpaceAsPlus(true);
        URLEncoder.QUERY.addSafeCharacter('*');
        URLEncoder.QUERY.addSafeCharacter('-');
        URLEncoder.QUERY.addSafeCharacter('.');
        URLEncoder.QUERY.addSafeCharacter('_');
        URLEncoder.QUERY.addSafeCharacter('=');
        URLEncoder.QUERY.addSafeCharacter('&');
    }
}
