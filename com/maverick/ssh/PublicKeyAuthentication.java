package com.maverick.ssh;

import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.SshPrivateKey;

public class PublicKeyAuthentication implements SshAuthentication
{
    String t;
    SshPrivateKey r;
    SshPublicKey q;
    boolean s;
    
    public PublicKeyAuthentication() {
        this.s = true;
    }
    
    public void setUsername(final String t) {
        this.t = t;
    }
    
    public String getUsername() {
        return this.t;
    }
    
    public void setPrivateKey(final SshPrivateKey r) {
        this.r = r;
    }
    
    public String getMethod() {
        return "publickey";
    }
    
    public SshPrivateKey getPrivateKey() {
        return this.r;
    }
    
    public void setPublicKey(final SshPublicKey q) {
        this.q = q;
    }
    
    public SshPublicKey getPublicKey() {
        return this.q;
    }
    
    public void setAuthenticating(final boolean s) {
        this.s = s;
    }
    
    public boolean isAuthenticating() {
        return this.s;
    }
}
