package org.apache.http.auth;

import org.apache.http.util.Args;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class AuthOption
{
    private final AuthScheme authScheme;
    private final Credentials creds;
    
    public AuthOption(final AuthScheme authScheme, final Credentials creds) {
        Args.notNull((Object)authScheme, "Auth scheme");
        Args.notNull((Object)creds, "User credentials");
        this.authScheme = authScheme;
        this.creds = creds;
    }
    
    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }
    
    public Credentials getCredentials() {
        return this.creds;
    }
    
    @Override
    public String toString() {
        return this.authScheme.toString();
    }
}
