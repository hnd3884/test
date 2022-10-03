package com.maverick.ssh2;

import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.components.SshDsaPublicKey;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.SshPrivateKey;

public class Ssh2HostbasedAuthentication implements AuthenticationClient
{
    String n;
    String m;
    String l;
    SshPrivateKey j;
    SshPublicKey k;
    
    public void authenticate(final AuthenticationProtocol authenticationProtocol, final String s) throws SshException, AuthenticationResult {
        if (this.m == null) {
            throw new SshException("Username not set!", 4);
        }
        if (this.n == null) {
            throw new SshException("Client hostname not set!", 4);
        }
        if (this.l == null) {
            this.l = this.m;
        }
        if (this.j == null || this.k == null) {
            throw new SshException("Client host keys not set!", 4);
        }
        if (!(this.k instanceof SshRsaPublicKey) && !(this.k instanceof SshDsaPublicKey)) {
            throw new SshException("Invalid public key type for SSH2 authentication!", 4);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(this.k.getAlgorithm());
            byteArrayWriter.writeBinaryString(this.k.getEncoded());
            byteArrayWriter.writeString(this.n);
            byteArrayWriter.writeString(this.l);
            final ByteArrayWriter byteArrayWriter2 = new ByteArrayWriter();
            byteArrayWriter2.writeBinaryString(authenticationProtocol.getSessionIdentifier());
            byteArrayWriter2.write(50);
            byteArrayWriter2.writeString(this.m);
            byteArrayWriter2.writeString(s);
            byteArrayWriter2.writeString("hostbased");
            byteArrayWriter2.writeString(this.k.getAlgorithm());
            byteArrayWriter2.writeBinaryString(this.k.getEncoded());
            byteArrayWriter2.writeString(this.n);
            byteArrayWriter2.writeString(this.l);
            final ByteArrayWriter byteArrayWriter3 = new ByteArrayWriter();
            byteArrayWriter3.writeString(this.k.getAlgorithm());
            byteArrayWriter3.writeBinaryString(this.j.sign(byteArrayWriter2.toByteArray()));
            byteArrayWriter.writeBinaryString(byteArrayWriter3.toByteArray());
            authenticationProtocol.sendRequest(this.getUsername(), s, "hostbased", byteArrayWriter.toByteArray());
            throw new SshException("Unexpected message returned from authentication protocol: " + authenticationProtocol.readMessage()[0], 3);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public String getMethod() {
        return "hostbased";
    }
    
    public void setClientHostname(final String n) {
        this.n = n;
    }
    
    public void setUsername(final String m) {
        this.m = m;
    }
    
    public String getUsername() {
        return this.m;
    }
    
    public void setPublicKey(final SshPublicKey k) {
        this.k = k;
    }
    
    public void setPrivateKey(final SshPrivateKey j) {
        this.j = j;
    }
    
    public void setClientUsername(final String l) {
        this.l = l;
    }
    
    public String getClientUsername() {
        return this.l;
    }
    
    public SshPrivateKey getPrivateKey() {
        return this.j;
    }
    
    public SshPublicKey getPublicKey() {
        return this.k;
    }
}
