package com.zoho.clustering.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtil
{
    public static String createURL(final String urlPart, final String uriPart) {
        final boolean uriStartWithSlash = uriPart.startsWith("/");
        return uriStartWithSlash ? (urlPart + uriPart) : (urlPart + '/' + uriPart);
    }
    
    public static String encode(final String value) {
        return encode(value, "utf-8");
    }
    
    public static String encode(final String value, final String enc) {
        try {
            return URLEncoder.encode(value, enc);
        }
        catch (final UnsupportedEncodingException exp) {
            throw new RuntimeException(exp);
        }
    }
}
