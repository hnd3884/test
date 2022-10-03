package com.maverick.ssh.components;

public class SshKeyPair
{
    SshPrivateKey c;
    SshPublicKey b;
    
    public SshPrivateKey getPrivateKey() {
        return this.c;
    }
    
    public SshPublicKey getPublicKey() {
        return this.b;
    }
    
    public static SshKeyPair getKeyPair(final SshPrivateKey c, final SshPublicKey b) {
        final SshKeyPair sshKeyPair = new SshKeyPair();
        sshKeyPair.b = b;
        sshKeyPair.c = c;
        return sshKeyPair;
    }
    
    public void setPrivateKey(final SshPrivateKey c) {
        this.c = c;
    }
    
    public void setPublicKey(final SshPublicKey b) {
        this.b = b;
    }
}
