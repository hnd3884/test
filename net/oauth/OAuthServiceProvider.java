package net.oauth;

import java.io.Serializable;

public class OAuthServiceProvider implements Serializable
{
    private static final long serialVersionUID = 3306534392621038574L;
    public final String requestTokenURL;
    public final String userAuthorizationURL;
    public final String accessTokenURL;
    
    public OAuthServiceProvider(final String requestTokenURL, final String userAuthorizationURL, final String accessTokenURL) {
        this.requestTokenURL = requestTokenURL;
        this.userAuthorizationURL = userAuthorizationURL;
        this.accessTokenURL = accessTokenURL;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.accessTokenURL == null) ? 0 : this.accessTokenURL.hashCode());
        result = 31 * result + ((this.requestTokenURL == null) ? 0 : this.requestTokenURL.hashCode());
        result = 31 * result + ((this.userAuthorizationURL == null) ? 0 : this.userAuthorizationURL.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final OAuthServiceProvider other = (OAuthServiceProvider)obj;
        if (this.accessTokenURL == null) {
            if (other.accessTokenURL != null) {
                return false;
            }
        }
        else if (!this.accessTokenURL.equals(other.accessTokenURL)) {
            return false;
        }
        if (this.requestTokenURL == null) {
            if (other.requestTokenURL != null) {
                return false;
            }
        }
        else if (!this.requestTokenURL.equals(other.requestTokenURL)) {
            return false;
        }
        if (this.userAuthorizationURL == null) {
            if (other.userAuthorizationURL != null) {
                return false;
            }
        }
        else if (!this.userAuthorizationURL.equals(other.userAuthorizationURL)) {
            return false;
        }
        return true;
    }
}
