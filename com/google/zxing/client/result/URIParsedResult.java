package com.google.zxing.client.result;

import java.util.Locale;
import java.util.regex.Pattern;

public final class URIParsedResult extends ParsedResult
{
    private static final Pattern USER_IN_HOST;
    private final String uri;
    private final String title;
    
    public URIParsedResult(final String uri, final String title) {
        super(ParsedResultType.URI);
        this.uri = massageURI(uri);
        this.title = title;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public boolean isPossiblyMaliciousURI() {
        return URIParsedResult.USER_IN_HOST.matcher(this.uri).find();
    }
    
    @Override
    public String getDisplayResult() {
        final StringBuilder result = new StringBuilder(30);
        ParsedResult.maybeAppend(this.title, result);
        ParsedResult.maybeAppend(this.uri, result);
        return result.toString();
    }
    
    private static String massageURI(String uri) {
        uri = uri.trim();
        final int protocolEnd = uri.indexOf(58);
        if (protocolEnd < 0) {
            uri = "http://" + uri;
        }
        else if (isColonFollowedByPortNumber(uri, protocolEnd)) {
            uri = "http://" + uri;
        }
        else {
            uri = uri.substring(0, protocolEnd).toLowerCase(Locale.ENGLISH) + uri.substring(protocolEnd);
        }
        return uri;
    }
    
    private static boolean isColonFollowedByPortNumber(final String uri, final int protocolEnd) {
        int nextSlash = uri.indexOf(47, protocolEnd + 1);
        if (nextSlash < 0) {
            nextSlash = uri.length();
        }
        if (nextSlash <= protocolEnd + 1) {
            return false;
        }
        for (int x = protocolEnd + 1; x < nextSlash; ++x) {
            if (uri.charAt(x) < '0' || uri.charAt(x) > '9') {
                return false;
            }
        }
        return true;
    }
    
    static {
        USER_IN_HOST = Pattern.compile(":/*([^/@]+)@[^/]+");
    }
}
