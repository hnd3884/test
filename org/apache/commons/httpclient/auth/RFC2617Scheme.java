package org.apache.commons.httpclient.auth;

import java.util.Map;

public abstract class RFC2617Scheme implements AuthScheme
{
    private Map params;
    
    public RFC2617Scheme() {
        this.params = null;
    }
    
    public RFC2617Scheme(final String challenge) throws MalformedChallengeException {
        this.params = null;
        this.processChallenge(challenge);
    }
    
    public void processChallenge(final String challenge) throws MalformedChallengeException {
        final String s = AuthChallengeParser.extractScheme(challenge);
        if (!s.equalsIgnoreCase(this.getSchemeName())) {
            throw new MalformedChallengeException("Invalid " + this.getSchemeName() + " challenge: " + challenge);
        }
        this.params = AuthChallengeParser.extractParams(challenge);
    }
    
    protected Map getParameters() {
        return this.params;
    }
    
    public String getParameter(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        if (this.params == null) {
            return null;
        }
        return this.params.get(name.toLowerCase());
    }
    
    public String getRealm() {
        return this.getParameter("realm");
    }
    
    public String getID() {
        return this.getRealm();
    }
}
