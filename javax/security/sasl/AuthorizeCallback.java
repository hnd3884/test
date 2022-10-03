package javax.security.sasl;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class AuthorizeCallback implements Callback, Serializable
{
    private String authenticationID;
    private String authorizationID;
    private String authorizedID;
    private boolean authorized;
    private static final long serialVersionUID = -2353344186490470805L;
    
    public AuthorizeCallback(final String authenticationID, final String authorizationID) {
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public boolean isAuthorized() {
        return this.authorized;
    }
    
    public void setAuthorized(final boolean authorized) {
        this.authorized = authorized;
    }
    
    public String getAuthorizedID() {
        if (!this.authorized) {
            return null;
        }
        return (this.authorizedID == null) ? this.authorizationID : this.authorizedID;
    }
    
    public void setAuthorizedID(final String authorizedID) {
        this.authorizedID = authorizedID;
    }
}
