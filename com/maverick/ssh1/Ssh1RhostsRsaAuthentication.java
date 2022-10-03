package com.maverick.ssh1;

import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.SshAuthentication;

public class Ssh1RhostsRsaAuthentication implements SshAuthentication
{
    String g;
    String f;
    SshPrivateKey d;
    SshPublicKey e;
    
    public void setUsername(final String g) {
        this.g = g;
    }
    
    public String getUsername() {
        return this.g;
    }
    
    public String getMethod() {
        return "rhosts";
    }
    
    public void setPublicKey(final SshPublicKey e) {
        this.e = e;
    }
    
    public void setPrivateKey(final SshPrivateKey d) {
        this.d = d;
    }
    
    public void setClientUsername(final String f) {
        this.f = f;
    }
    
    public String getClientUsername() {
        return (this.f == null) ? this.g : this.f;
    }
    
    public SshPrivateKey getPrivateKey() {
        return this.d;
    }
    
    public SshPublicKey getPublicKey() {
        return this.e;
    }
}
