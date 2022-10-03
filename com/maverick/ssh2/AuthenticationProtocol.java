package com.maverick.ssh2;

import com.maverick.util.ByteArrayReader;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.events.Event;
import com.maverick.events.EventServiceImplementation;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshKeyExchangeClient;

public class AuthenticationProtocol
{
    public static final int SSH_MSG_USERAUTH_REQUEST = 50;
    TransportProtocol d;
    BannerDisplay c;
    int b;
    public static final String SERVICE_NAME = "ssh-userauth";
    
    public SshKeyExchangeClient getKeyExchange() {
        return this.d.getKeyExchange();
    }
    
    public AuthenticationProtocol(final TransportProtocol d) throws SshException {
        this.b = 2;
        (this.d = d).startService("ssh-userauth");
    }
    
    public void setBannerDisplay(final BannerDisplay c) {
        this.c = c;
    }
    
    public byte[] readMessage() throws SshException, AuthenticationResult {
        byte[] nextMessage;
        while (this.b(nextMessage = this.d.nextMessage())) {}
        return nextMessage;
    }
    
    public int authenticate(final AuthenticationClient authenticationClient, final String s) throws SshException {
        try {
            authenticationClient.authenticate(this, s);
            this.readMessage();
            this.d.disconnect(2, "Unexpected response received from Authentication Protocol");
            throw new SshException("Unexpected response received from Authentication Protocol", 3);
        }
        catch (final AuthenticationResult authenticationResult) {
            this.b = authenticationResult.getResult();
            if (this.b == 1) {
                this.d.i();
            }
            return this.b;
        }
    }
    
    public String getAuthenticationMethods(final String s, final String s2) throws SshException {
        this.sendRequest(s, s2, "none", null);
        try {
            this.readMessage();
            this.d.disconnect(2, "Unexpected response received from Authentication Protocol");
            throw new SshException("Unexpected response received from Authentication Protocol", 3);
        }
        catch (final AuthenticationResult authenticationResult) {
            this.b = authenticationResult.getResult();
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 11, true).addAttribute("AUTHENTICATION_METHODS", authenticationResult.getAuthenticationMethods()));
            return authenticationResult.getAuthenticationMethods();
        }
    }
    
    public void sendRequest(final String s, final String s2, final String s3, final byte[] array) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(50);
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeString(s2);
            byteArrayWriter.writeString(s3);
            if (array != null) {
                byteArrayWriter.write(array);
            }
            this.d.sendMessage(byteArrayWriter.toByteArray(), true);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean isAuthenticated() {
        return this.b == 1;
    }
    
    public byte[] getSessionIdentifier() {
        return this.d.getSessionIdentifier();
    }
    
    private boolean b(final byte[] array) throws SshException, AuthenticationResult {
        try {
            switch (array[0]) {
                case 51: {
                    final ByteArrayReader byteArrayReader = new ByteArrayReader(array);
                    byteArrayReader.skip(1L);
                    final String string = byteArrayReader.readString();
                    if (byteArrayReader.read() == 0) {
                        EventServiceImplementation.getInstance().fireEvent(new Event(this, 14, true));
                        throw new AuthenticationResult(2, string);
                    }
                    EventServiceImplementation.getInstance().fireEvent(new Event(this, 15, true));
                    throw new AuthenticationResult(3, string);
                }
                case 52: {
                    EventServiceImplementation.getInstance().fireEvent(new Event(this, 13, true));
                    throw new AuthenticationResult(1);
                }
                case 53: {
                    final ByteArrayReader byteArrayReader2 = new ByteArrayReader(array);
                    byteArrayReader2.skip(1L);
                    if (this.c != null) {
                        this.c.displayBanner(byteArrayReader2.readString());
                    }
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public void sendMessage(final byte[] array) throws SshException {
        this.d.sendMessage(array, true);
    }
    
    public String getHost() {
        return this.d.lb.getHost();
    }
}
