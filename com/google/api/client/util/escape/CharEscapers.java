package com.google.api.client.util.escape;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class CharEscapers
{
    private static final Escaper APPLICATION_X_WWW_FORM_URLENCODED;
    private static final Escaper URI_ESCAPER;
    private static final Escaper URI_PATH_ESCAPER;
    private static final Escaper URI_RESERVED_ESCAPER;
    private static final Escaper URI_USERINFO_ESCAPER;
    private static final Escaper URI_QUERY_STRING_ESCAPER;
    
    @Deprecated
    public static String escapeUri(final String value) {
        return CharEscapers.APPLICATION_X_WWW_FORM_URLENCODED.escape(value);
    }
    
    public static String escapeUriConformant(final String value) {
        return CharEscapers.URI_ESCAPER.escape(value);
    }
    
    public static String decodeUri(final String uri) {
        try {
            return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String decodeUriPath(final String path) {
        if (path == null) {
            return null;
        }
        try {
            return URLDecoder.decode(path.replace("+", "%2B"), StandardCharsets.UTF_8.name());
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String escapeUriPath(final String value) {
        return CharEscapers.URI_PATH_ESCAPER.escape(value);
    }
    
    public static String escapeUriPathWithoutReserved(final String value) {
        return CharEscapers.URI_RESERVED_ESCAPER.escape(value);
    }
    
    public static String escapeUriUserInfo(final String value) {
        return CharEscapers.URI_USERINFO_ESCAPER.escape(value);
    }
    
    public static String escapeUriQuery(final String value) {
        return CharEscapers.URI_QUERY_STRING_ESCAPER.escape(value);
    }
    
    private CharEscapers() {
    }
    
    static {
        APPLICATION_X_WWW_FORM_URLENCODED = new PercentEscaper("-_.*", true);
        URI_ESCAPER = new PercentEscaper("-_.*", false);
        URI_PATH_ESCAPER = new PercentEscaper("-_.!~*'()@:$&,;=+");
        URI_RESERVED_ESCAPER = new PercentEscaper("-_.!~*'()@:$&,;=+/?");
        URI_USERINFO_ESCAPER = new PercentEscaper("-_.!~*'():$&,;=");
        URI_QUERY_STRING_ESCAPER = new PercentEscaper("-_.!~*'()@:$,;/?:");
    }
}
