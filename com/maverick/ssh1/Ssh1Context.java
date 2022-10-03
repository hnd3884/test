package com.maverick.ssh1;

import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.ForwardingRequestListener;
import com.maverick.ssh.HostKeyVerification;
import com.maverick.ssh.SshContext;

public final class Ssh1Context implements SshContext
{
    public static final int CIPHER_DES = 2;
    public static final int CIPHER_3DES = 3;
    String d;
    int c;
    HostKeyVerification h;
    String f;
    byte[] b;
    byte[] e;
    ForwardingRequestListener j;
    int g;
    boolean i;
    
    public Ssh1Context() {
        this.d = "/usr/libexec/sftp-server";
        this.c = 10;
        this.f = null;
        this.b = null;
        this.e = null;
        this.j = null;
        this.g = 3;
        this.i = false;
    }
    
    public void setChannelLimit(final int c) {
        this.c = c;
    }
    
    public int getChannelLimit() {
        return this.c;
    }
    
    public void setSFTPProvider(final String d) {
        this.d = d;
    }
    
    public String getSFTPProvider() {
        return this.d;
    }
    
    public void setX11Display(final String f) {
        this.f = f;
    }
    
    public String getX11Display() {
        return this.f;
    }
    
    public byte[] getX11AuthenticationCookie() throws SshException {
        if (this.b == null) {
            this.b = new byte[16];
            ComponentManager.getInstance().getRND().nextBytes(this.b);
        }
        return this.b;
    }
    
    public void setX11AuthenticationCookie(final byte[] b) {
        this.b = b;
    }
    
    public void setX11RealCookie(final byte[] e) {
        this.e = e;
    }
    
    public byte[] getX11RealCookie() throws SshException {
        if (this.e == null) {
            this.e = this.getX11AuthenticationCookie();
        }
        return this.e;
    }
    
    public void setX11RequestListener(final ForwardingRequestListener j) {
        this.j = j;
    }
    
    public ForwardingRequestListener getX11RequestListener() {
        return this.j;
    }
    
    public int getCipherType(final int n) throws SshException {
        if (this.i) {
            throw new SshException("FIPS mode is enabled but an attempt was made to access an SSH1 type cipher", 4);
        }
        if ((n & 1 << this.g) != 0x0) {
            return this.g;
        }
        if ((n & 0x8) != 0x0) {
            return 3;
        }
        if ((n & 0x4) != 0x0) {
            return 2;
        }
        throw new SshException("Cipher could not be agreed!", 9);
    }
    
    public void setCipherType(final int g) {
        this.g = g;
    }
    
    public SshCipher createCipher(final int n) throws SshException {
        return (SshCipher)ComponentManager.getInstance().supportedSsh1CiphersCS().getInstance(String.valueOf(n));
    }
    
    public void setHostKeyVerification(final HostKeyVerification h) {
        this.h = h;
    }
    
    public HostKeyVerification getHostKeyVerification() {
        return this.h;
    }
    
    public void enableFIPSMode() {
        this.i = true;
    }
}
