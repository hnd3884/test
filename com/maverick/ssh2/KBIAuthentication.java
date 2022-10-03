package com.maverick.ssh2;

import java.io.IOException;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.SshException;

public class KBIAuthentication implements AuthenticationClient
{
    String p;
    KBIRequestHandler o;
    
    public String getUsername() {
        return this.p;
    }
    
    public void setUsername(final String p) {
        this.p = p;
    }
    
    public String getMethod() {
        return "keyboard-interactive";
    }
    
    public void setKBIRequestHandler(final KBIRequestHandler o) {
        this.o = o;
    }
    
    public void authenticate(final AuthenticationProtocol authenticationProtocol, final String s) throws SshException, AuthenticationResult {
        try {
            if (this.o == null) {
                throw new SshException("A request handler must be set!", 4);
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString("");
            byteArrayWriter.writeString("");
            authenticationProtocol.sendRequest(this.p, s, "keyboard-interactive", byteArrayWriter.toByteArray());
            while (true) {
                final ByteArrayReader byteArrayReader = new ByteArrayReader(authenticationProtocol.readMessage());
                if (byteArrayReader.read() != 60) {
                    authenticationProtocol.d.disconnect(2, "Unexpected authentication message received!");
                    throw new SshException("Unexpected authentication message received!", 3);
                }
                final String string = byteArrayReader.readString();
                final String string2 = byteArrayReader.readString();
                byteArrayReader.readString();
                final int n = (int)byteArrayReader.readInt();
                final KBIPrompt[] array = new KBIPrompt[n];
                for (int i = 0; i < n; ++i) {
                    array[i] = new KBIPrompt(byteArrayReader.readString(), byteArrayReader.read() == 1);
                }
                if (!this.o.showPrompts(string, string2, array)) {
                    throw new AuthenticationResult(4);
                }
                byteArrayWriter.reset();
                byteArrayWriter.write(61);
                byteArrayWriter.writeInt(array.length);
                for (int j = 0; j < array.length; ++j) {
                    byteArrayWriter.writeString(array[j].getResponse());
                }
                authenticationProtocol.d.sendMessage(byteArrayWriter.toByteArray(), true);
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public String getMethodName() {
        return "keyboard-interactive";
    }
}
