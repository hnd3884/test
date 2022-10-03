package com.maverick.ssh2;

public class AuthenticationResult extends Throwable
{
    int b;
    String c;
    
    public AuthenticationResult(final int b) {
        this.b = b;
    }
    
    public AuthenticationResult(final int b, final String c) {
        this.b = b;
        this.c = c;
    }
    
    public int getResult() {
        return this.b;
    }
    
    public String getAuthenticationMethods() {
        return this.c;
    }
}
