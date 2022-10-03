package com.maverick.ssh2;

import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.SshException;
import com.maverick.ssh.PasswordAuthentication;

public class Ssh2PasswordAuthentication extends PasswordAuthentication implements AuthenticationClient
{
    String w;
    boolean v;
    
    public Ssh2PasswordAuthentication() {
        this.v = false;
    }
    
    public void setNewPassword(final String w) {
        this.w = w;
    }
    
    public boolean requiresPasswordChange() {
        return this.v;
    }
    
    public void authenticate(final AuthenticationProtocol authenticationProtocol, final String s) throws SshException, AuthenticationResult {
        try {
            if (this.getUsername() == null || this.getPassword() == null) {
                throw new SshException("Username or password not set!", 4);
            }
            if (this.v && this.w == null) {
                throw new SshException("You must set a new password!", 4);
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeBoolean(this.v);
            byteArrayWriter.writeString(this.getPassword());
            if (this.v) {
                byteArrayWriter.writeString(this.w);
            }
            authenticationProtocol.sendRequest(this.getUsername(), s, "password", byteArrayWriter.toByteArray());
            if (authenticationProtocol.readMessage()[0] != 60) {
                authenticationProtocol.d.disconnect(2, "Unexpected message received");
                throw new SshException("Unexpected response from Authentication Protocol", 3);
            }
            this.v = true;
            throw new AuthenticationResult(2);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
}
