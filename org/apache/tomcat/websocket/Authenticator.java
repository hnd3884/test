package org.apache.tomcat.websocket;

import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Authenticator
{
    private static final Pattern pattern;
    
    public abstract String getAuthorization(final String p0, final String p1, final Map<String, Object> p2) throws AuthenticationException;
    
    public abstract String getSchemeName();
    
    public Map<String, String> parseWWWAuthenticateHeader(final String WWWAuthenticate) {
        final Matcher m = Authenticator.pattern.matcher(WWWAuthenticate);
        final Map<String, String> challenge = new HashMap<String, String>();
        while (m.find()) {
            final String key = m.group(1);
            final String qtedValue = m.group(3);
            final String value = m.group(4);
            challenge.put(key, (qtedValue != null) ? qtedValue : value);
        }
        return challenge;
    }
    
    static {
        pattern = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|([^,=\"]+))\\s*,?");
    }
}
