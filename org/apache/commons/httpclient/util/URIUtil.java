package org.apache.commons.httpclient.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.URI;
import java.util.BitSet;

public class URIUtil
{
    protected static final BitSet empty;
    
    public static String getName(final String uri) {
        if (uri == null || uri.length() == 0) {
            return uri;
        }
        final String path = getPath(uri);
        final int at = path.lastIndexOf("/");
        final int to = path.length();
        return (at >= 0) ? path.substring(at + 1, to) : path;
    }
    
    public static String getQuery(final String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        int at = uri.indexOf("//");
        int from = uri.indexOf("/", (at >= 0) ? ((uri.lastIndexOf("/", at - 1) >= 0) ? 0 : (at + 2)) : 0);
        int to = uri.length();
        at = uri.indexOf("?", from);
        if (at >= 0) {
            from = at + 1;
            if (uri.lastIndexOf("#") > from) {
                to = uri.lastIndexOf("#");
            }
            return (from < 0 || from == to) ? null : uri.substring(from, to);
        }
        return null;
    }
    
    public static String getPath(final String uri) {
        if (uri == null) {
            return null;
        }
        final int at = uri.indexOf("//");
        final int from = uri.indexOf("/", (at >= 0) ? ((uri.lastIndexOf("/", at - 1) >= 0) ? 0 : (at + 2)) : 0);
        int to = uri.length();
        if (uri.indexOf(63, from) != -1) {
            to = uri.indexOf(63, from);
        }
        if (uri.lastIndexOf("#") > from && uri.lastIndexOf("#") < to) {
            to = uri.lastIndexOf("#");
        }
        return (from < 0) ? ((at >= 0) ? "/" : uri) : uri.substring(from, to);
    }
    
    public static String getPathQuery(final String uri) {
        if (uri == null) {
            return null;
        }
        final int at = uri.indexOf("//");
        final int from = uri.indexOf("/", (at >= 0) ? ((uri.lastIndexOf("/", at - 1) >= 0) ? 0 : (at + 2)) : 0);
        int to = uri.length();
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        return (from < 0) ? ((at >= 0) ? "/" : uri) : uri.substring(from, to);
    }
    
    public static String getFromPath(final String uri) {
        if (uri == null) {
            return null;
        }
        final int at = uri.indexOf("//");
        final int from = uri.indexOf("/", (at >= 0) ? ((uri.lastIndexOf("/", at - 1) >= 0) ? 0 : (at + 2)) : 0);
        return (from < 0) ? ((at >= 0) ? "/" : uri) : uri.substring(from);
    }
    
    public static String encodeAll(final String unescaped) throws URIException {
        return encodeAll(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodeAll(final String unescaped, final String charset) throws URIException {
        return encode(unescaped, URIUtil.empty, charset);
    }
    
    public static String encodeWithinAuthority(final String unescaped) throws URIException {
        return encodeWithinAuthority(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodeWithinAuthority(final String unescaped, final String charset) throws URIException {
        return encode(unescaped, URI.allowed_within_authority, charset);
    }
    
    public static String encodePathQuery(final String unescaped) throws URIException {
        return encodePathQuery(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodePathQuery(final String unescaped, final String charset) throws URIException {
        final int at = unescaped.indexOf(63);
        if (at < 0) {
            return encode(unescaped, URI.allowed_abs_path, charset);
        }
        return encode(unescaped.substring(0, at), URI.allowed_abs_path, charset) + '?' + encode(unescaped.substring(at + 1), URI.allowed_query, charset);
    }
    
    public static String encodeWithinPath(final String unescaped) throws URIException {
        return encodeWithinPath(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodeWithinPath(final String unescaped, final String charset) throws URIException {
        return encode(unescaped, URI.allowed_within_path, charset);
    }
    
    public static String encodePath(final String unescaped) throws URIException {
        return encodePath(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodePath(final String unescaped, final String charset) throws URIException {
        return encode(unescaped, URI.allowed_abs_path, charset);
    }
    
    public static String encodeWithinQuery(final String unescaped) throws URIException {
        return encodeWithinQuery(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodeWithinQuery(final String unescaped, final String charset) throws URIException {
        return encode(unescaped, URI.allowed_within_query, charset);
    }
    
    public static String encodeQuery(final String unescaped) throws URIException {
        return encodeQuery(unescaped, URI.getDefaultProtocolCharset());
    }
    
    public static String encodeQuery(final String unescaped, final String charset) throws URIException {
        return encode(unescaped, URI.allowed_query, charset);
    }
    
    public static String encode(final String unescaped, final BitSet allowed) throws URIException {
        return encode(unescaped, allowed, URI.getDefaultProtocolCharset());
    }
    
    public static String encode(final String unescaped, final BitSet allowed, final String charset) throws URIException {
        final byte[] rawdata = URLCodec.encodeUrl(allowed, EncodingUtil.getBytes(unescaped, charset));
        return EncodingUtil.getAsciiString(rawdata);
    }
    
    public static String decode(final String escaped) throws URIException {
        try {
            final byte[] rawdata = URLCodec.decodeUrl(EncodingUtil.getAsciiBytes(escaped));
            return EncodingUtil.getString(rawdata, URI.getDefaultProtocolCharset());
        }
        catch (final DecoderException e) {
            throw new URIException(e.getMessage());
        }
    }
    
    public static String decode(final String escaped, final String charset) throws URIException {
        return Coder.decode(escaped.toCharArray(), charset);
    }
    
    static {
        empty = new BitSet(1);
    }
    
    protected static class Coder extends URI
    {
        public static char[] encode(final String unescapedComponent, final BitSet allowed, final String charset) throws URIException {
            return URI.encode(unescapedComponent, allowed, charset);
        }
        
        public static String decode(final char[] escapedComponent, final String charset) throws URIException {
            return URI.decode(escapedComponent, charset);
        }
        
        public static boolean verifyEscaped(final char[] original) {
            for (int i = 0; i < original.length; ++i) {
                final int c = original[i];
                if (c > 128) {
                    return false;
                }
                if (c == 37 && (Character.digit(original[++i], 16) == -1 || Character.digit(original[++i], 16) == -1)) {
                    return false;
                }
            }
            return true;
        }
        
        public static String replace(String original, final char[] from, final char[] to) {
            for (int i = from.length; i > 0; --i) {
                original = replace(original, from[i], to[i]);
            }
            return original;
        }
        
        public static String replace(final String original, final char from, final char to) {
            final StringBuffer result = new StringBuffer(original.length());
            int saved = 0;
            int at;
            do {
                at = original.indexOf(from);
                if (at >= 0) {
                    result.append(original.substring(0, at));
                    result.append(to);
                }
                else {
                    result.append(original.substring(saved));
                }
                saved = at;
            } while (at >= 0);
            return result.toString();
        }
    }
}
