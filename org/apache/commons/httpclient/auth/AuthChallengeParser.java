package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.Header;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.ParameterParser;
import java.util.HashMap;
import java.util.Map;

public final class AuthChallengeParser
{
    public static String extractScheme(final String challengeStr) throws MalformedChallengeException {
        if (challengeStr == null) {
            throw new IllegalArgumentException("Challenge may not be null");
        }
        final int idx = challengeStr.indexOf(32);
        String s = null;
        if (idx == -1) {
            s = challengeStr;
        }
        else {
            s = challengeStr.substring(0, idx);
        }
        if (s.equals("")) {
            throw new MalformedChallengeException("Invalid challenge: " + challengeStr);
        }
        return s.toLowerCase();
    }
    
    public static Map extractParams(final String challengeStr) throws MalformedChallengeException {
        if (challengeStr == null) {
            throw new IllegalArgumentException("Challenge may not be null");
        }
        final int idx = challengeStr.indexOf(32);
        if (idx == -1) {
            throw new MalformedChallengeException("Invalid challenge: " + challengeStr);
        }
        final Map map = new HashMap();
        final ParameterParser parser = new ParameterParser();
        final List params = parser.parse(challengeStr.substring(idx + 1, challengeStr.length()), ',');
        for (int i = 0; i < params.size(); ++i) {
            final NameValuePair param = params.get(i);
            map.put(param.getName().toLowerCase(), param.getValue());
        }
        return map;
    }
    
    public static Map parseChallenges(final Header[] headers) throws MalformedChallengeException {
        if (headers == null) {
            throw new IllegalArgumentException("Array of challenges may not be null");
        }
        String challenge = null;
        final Map challengemap = new HashMap(headers.length);
        for (int i = 0; i < headers.length; ++i) {
            challenge = headers[i].getValue();
            final String s = extractScheme(challenge);
            challengemap.put(s, challenge);
        }
        return challengemap;
    }
}
