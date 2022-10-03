package org.apache.commons.httpclient.auth;

public abstract class AuthSchemeBase implements AuthScheme
{
    private String challenge;
    
    public AuthSchemeBase(final String challenge) throws MalformedChallengeException {
        this.challenge = null;
        if (challenge == null) {
            throw new IllegalArgumentException("Challenge may not be null");
        }
        this.challenge = challenge;
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof AuthSchemeBase) {
            return this.challenge.equals(((AuthSchemeBase)obj).challenge);
        }
        return super.equals(obj);
    }
    
    public int hashCode() {
        return this.challenge.hashCode();
    }
    
    public String toString() {
        return this.challenge;
    }
}
