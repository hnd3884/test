package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLProtocolException;

final class RenegotiationInfoExtension extends HelloExtension
{
    private final byte[] renegotiated_connection;
    
    RenegotiationInfoExtension(final byte[] clientVerifyData, final byte[] serverVerifyData) {
        super(ExtensionType.EXT_RENEGOTIATION_INFO);
        if (clientVerifyData.length != 0) {
            System.arraycopy(clientVerifyData, 0, this.renegotiated_connection = new byte[clientVerifyData.length + serverVerifyData.length], 0, clientVerifyData.length);
            if (serverVerifyData.length != 0) {
                System.arraycopy(serverVerifyData, 0, this.renegotiated_connection, clientVerifyData.length, serverVerifyData.length);
            }
        }
        else {
            this.renegotiated_connection = new byte[0];
        }
    }
    
    RenegotiationInfoExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_RENEGOTIATION_INFO);
        if (len < 1) {
            throw new SSLProtocolException("Invalid " + this.type + " extension");
        }
        final int renegoInfoDataLen = s.getInt8();
        if (renegoInfoDataLen + 1 != len) {
            throw new SSLProtocolException("Invalid " + this.type + " extension");
        }
        this.renegotiated_connection = new byte[renegoInfoDataLen];
        if (renegoInfoDataLen != 0) {
            s.read(this.renegotiated_connection, 0, renegoInfoDataLen);
        }
    }
    
    @Override
    int length() {
        return 5 + this.renegotiated_connection.length;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        s.putInt16(this.renegotiated_connection.length + 1);
        s.putBytes8(this.renegotiated_connection);
    }
    
    boolean isEmpty() {
        return this.renegotiated_connection.length == 0;
    }
    
    byte[] getRenegotiatedConnection() {
        return this.renegotiated_connection;
    }
    
    @Override
    public String toString() {
        return "Extension " + this.type + ", renegotiated_connection: " + ((this.renegotiated_connection.length == 0) ? "<empty>" : Debug.toString(this.renegotiated_connection));
    }
}
