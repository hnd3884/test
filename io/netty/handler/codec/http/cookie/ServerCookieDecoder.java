package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;
import java.util.TreeSet;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public final class ServerCookieDecoder extends CookieDecoder
{
    private static final String RFC2965_VERSION = "$Version";
    private static final String RFC2965_PATH = "$Path";
    private static final String RFC2965_DOMAIN = "$Domain";
    private static final String RFC2965_PORT = "$Port";
    public static final ServerCookieDecoder STRICT;
    public static final ServerCookieDecoder LAX;
    
    private ServerCookieDecoder(final boolean strict) {
        super(strict);
    }
    
    public List<Cookie> decodeAll(final String header) {
        final List<Cookie> cookies = new ArrayList<Cookie>();
        this.decode(cookies, header);
        return Collections.unmodifiableList((List<? extends Cookie>)cookies);
    }
    
    public Set<Cookie> decode(final String header) {
        final Set<Cookie> cookies = new TreeSet<Cookie>();
        this.decode(cookies, header);
        return cookies;
    }
    
    private void decode(final Collection<? super Cookie> cookies, final String header) {
        final int headerLen = ObjectUtil.checkNotNull(header, "header").length();
        if (headerLen == 0) {
            return;
        }
        int i = 0;
        boolean rfc2965Style = false;
        if (header.regionMatches(true, 0, "$Version", 0, "$Version".length())) {
            i = header.indexOf(59) + 1;
            rfc2965Style = true;
        }
        while (i != headerLen) {
            final char c = header.charAt(i);
            if (c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r' || c == ' ' || c == ',' || c == ';') {
                ++i;
            }
            else {
                final int nameBegin = i;
                int nameEnd;
                int valueBegin;
                int valueEnd;
                while (true) {
                    final char curChar = header.charAt(i);
                    if (curChar == ';') {
                        nameEnd = i;
                        valueEnd = (valueBegin = -1);
                        break;
                    }
                    if (curChar == '=') {
                        nameEnd = i;
                        if (++i == headerLen) {
                            valueEnd = (valueBegin = 0);
                            break;
                        }
                        valueBegin = i;
                        final int semiPos = header.indexOf(59, i);
                        i = (valueEnd = ((semiPos > 0) ? semiPos : headerLen));
                        break;
                    }
                    else {
                        if (++i == headerLen) {
                            nameEnd = headerLen;
                            valueEnd = (valueBegin = -1);
                            break;
                        }
                        continue;
                    }
                }
                if (rfc2965Style) {
                    if (header.regionMatches(nameBegin, "$Path", 0, "$Path".length()) || header.regionMatches(nameBegin, "$Domain", 0, "$Domain".length())) {
                        continue;
                    }
                    if (header.regionMatches(nameBegin, "$Port", 0, "$Port".length())) {
                        continue;
                    }
                }
                final DefaultCookie cookie = this.initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
                if (cookie == null) {
                    continue;
                }
                cookies.add(cookie);
            }
        }
    }
    
    static {
        STRICT = new ServerCookieDecoder(true);
        LAX = new ServerCookieDecoder(false);
    }
}
