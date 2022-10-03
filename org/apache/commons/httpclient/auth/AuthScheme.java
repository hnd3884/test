package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.Credentials;

public interface AuthScheme
{
    void processChallenge(final String p0) throws MalformedChallengeException;
    
    String getSchemeName();
    
    String getParameter(final String p0);
    
    String getRealm();
    
    String getID();
    
    boolean isConnectionBased();
    
    boolean isComplete();
    
    String authenticate(final Credentials p0, final String p1, final String p2) throws AuthenticationException;
    
    String authenticate(final Credentials p0, final HttpMethod p1) throws AuthenticationException;
}
