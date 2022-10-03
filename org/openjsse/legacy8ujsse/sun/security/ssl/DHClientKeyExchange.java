package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.PrintStream;
import java.io.IOException;
import javax.net.ssl.SSLHandshakeException;
import java.math.BigInteger;

final class DHClientKeyExchange extends HandshakeMessage
{
    private byte[] dh_Yc;
    
    @Override
    int messageType() {
        return 16;
    }
    
    BigInteger getClientPublicKey() {
        return (this.dh_Yc == null) ? null : new BigInteger(1, this.dh_Yc);
    }
    
    DHClientKeyExchange(final BigInteger publicKey) {
        this.dh_Yc = HandshakeMessage.toByteArray(publicKey);
    }
    
    DHClientKeyExchange() {
        this.dh_Yc = null;
    }
    
    DHClientKeyExchange(final HandshakeInStream input) throws IOException {
        if (input.available() >= 2) {
            this.dh_Yc = input.getBytes16();
            return;
        }
        throw new SSLHandshakeException("Unsupported implicit client DiffieHellman public key");
    }
    
    @Override
    int messageLength() {
        if (this.dh_Yc == null) {
            return 0;
        }
        return this.dh_Yc.length + 2;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        if (this.dh_Yc != null && this.dh_Yc.length != 0) {
            s.putBytes16(this.dh_Yc);
        }
    }
    
    @Override
    void print(final PrintStream s) throws IOException {
        s.println("*** ClientKeyExchange, DH");
        if (DHClientKeyExchange.debug != null && Debug.isOn("verbose")) {
            Debug.println(s, "DH Public key", this.dh_Yc);
        }
    }
}
