package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;
import java.net.URISyntaxException;
import java.net.URI;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;

public class QueryStringEncoder
{
    private final Charset charset;
    private final StringBuilder uriBuilder;
    private boolean hasParams;
    private static final byte WRITE_UTF_UNKNOWN = 63;
    private static final char[] CHAR_MAP;
    
    public QueryStringEncoder(final String uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }
    
    public QueryStringEncoder(final String uri, final Charset charset) {
        ObjectUtil.checkNotNull(charset, "charset");
        this.uriBuilder = new StringBuilder(uri);
        this.charset = (CharsetUtil.UTF_8.equals(charset) ? null : charset);
    }
    
    public void addParam(final String name, final String value) {
        ObjectUtil.checkNotNull(name, "name");
        if (this.hasParams) {
            this.uriBuilder.append('&');
        }
        else {
            this.uriBuilder.append('?');
            this.hasParams = true;
        }
        this.encodeComponent(name);
        if (value != null) {
            this.uriBuilder.append('=');
            this.encodeComponent(value);
        }
    }
    
    private void encodeComponent(final CharSequence s) {
        if (this.charset == null) {
            this.encodeUtf8Component(s);
        }
        else {
            this.encodeNonUtf8Component(s);
        }
    }
    
    public URI toUri() throws URISyntaxException {
        return new URI(this.toString());
    }
    
    @Override
    public String toString() {
        return this.uriBuilder.toString();
    }
    
    private void encodeNonUtf8Component(final CharSequence s) {
        char[] buf = null;
        for (int i = 0, len = s.length(); i < len; ++i) {
            char c = s.charAt(i);
            if (dontNeedEncoding(c)) {
                this.uriBuilder.append(c);
            }
            else {
                int index = 0;
                if (buf == null) {
                    buf = new char[s.length() - i];
                }
                do {
                    buf[index] = c;
                    ++index;
                } while (++i < s.length() && !dontNeedEncoding(c = s.charAt(i)));
                final byte[] bytes2;
                final byte[] bytes = bytes2 = new String(buf, 0, index).getBytes(this.charset);
                for (final byte b : bytes2) {
                    this.appendEncoded(b);
                }
            }
        }
    }
    
    private void encodeUtf8Component(final CharSequence s) {
        for (int i = 0, len = s.length(); i < len; ++i) {
            final char c = s.charAt(i);
            if (!dontNeedEncoding(c)) {
                this.encodeUtf8Component(s, i, len);
                return;
            }
        }
        this.uriBuilder.append(s);
    }
    
    private void encodeUtf8Component(final CharSequence s, final int encodingStart, final int len) {
        if (encodingStart > 0) {
            this.uriBuilder.append(s, 0, encodingStart);
        }
        this.encodeUtf8ComponentSlow(s, encodingStart, len);
    }
    
    private void encodeUtf8ComponentSlow(final CharSequence s, final int start, final int len) {
        for (int i = start; i < len; ++i) {
            final char c = s.charAt(i);
            if (c < '\u0080') {
                if (dontNeedEncoding(c)) {
                    this.uriBuilder.append(c);
                }
                else {
                    this.appendEncoded(c);
                }
            }
            else if (c < '\u0800') {
                this.appendEncoded(0xC0 | c >> 6);
                this.appendEncoded(0x80 | (c & '?'));
            }
            else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    this.appendEncoded(63);
                }
                else {
                    if (++i == s.length()) {
                        this.appendEncoded(63);
                        break;
                    }
                    this.writeUtf8Surrogate(c, s.charAt(i));
                }
            }
            else {
                this.appendEncoded(0xE0 | c >> 12);
                this.appendEncoded(0x80 | (c >> 6 & 0x3F));
                this.appendEncoded(0x80 | (c & '?'));
            }
        }
    }
    
    private void writeUtf8Surrogate(final char c, final char c2) {
        if (!Character.isLowSurrogate(c2)) {
            this.appendEncoded(63);
            this.appendEncoded(Character.isHighSurrogate(c2) ? '?' : c2);
            return;
        }
        final int codePoint = Character.toCodePoint(c, c2);
        this.appendEncoded(0xF0 | codePoint >> 18);
        this.appendEncoded(0x80 | (codePoint >> 12 & 0x3F));
        this.appendEncoded(0x80 | (codePoint >> 6 & 0x3F));
        this.appendEncoded(0x80 | (codePoint & 0x3F));
    }
    
    private void appendEncoded(final int b) {
        this.uriBuilder.append('%').append(forDigit(b >> 4)).append(forDigit(b));
    }
    
    private static char forDigit(final int digit) {
        return QueryStringEncoder.CHAR_MAP[digit & 0xF];
    }
    
    private static boolean dontNeedEncoding(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '-' || ch == '_' || ch == '.' || ch == '*' || ch == '~';
    }
    
    static {
        CHAR_MAP = "0123456789ABCDEF".toCharArray();
    }
}
