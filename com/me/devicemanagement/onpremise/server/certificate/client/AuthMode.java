package com.me.devicemanagement.onpremise.server.certificate.client;

public enum AuthMode
{
    CLIENT_CERT_VERIFICATION(1), 
    AUTH_TOKEN(2);
    
    private final int authMode;
    
    private AuthMode(final int authMode) {
        this.authMode = authMode;
    }
    
    public int getAuthMode() {
        return this.authMode;
    }
}
