package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.PrintStream;
import java.io.IOException;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.interfaces.ECPublicKey;
import java.security.PublicKey;

final class ECDHClientKeyExchange extends HandshakeMessage
{
    private byte[] encodedPoint;
    
    @Override
    int messageType() {
        return 16;
    }
    
    byte[] getEncodedPoint() {
        return this.encodedPoint;
    }
    
    ECDHClientKeyExchange(final PublicKey publicKey) {
        final ECPublicKey ecKey = (ECPublicKey)publicKey;
        final ECPoint point = ecKey.getW();
        final ECParameterSpec params = ecKey.getParams();
        this.encodedPoint = JsseJce.encodePoint(point, params.getCurve());
    }
    
    ECDHClientKeyExchange(final HandshakeInStream input) throws IOException {
        this.encodedPoint = input.getBytes8();
    }
    
    @Override
    int messageLength() {
        return this.encodedPoint.length + 1;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putBytes8(this.encodedPoint);
    }
    
    @Override
    void print(final PrintStream s) throws IOException {
        s.println("*** ECDHClientKeyExchange");
        if (ECDHClientKeyExchange.debug != null && Debug.isOn("verbose")) {
            Debug.println(s, "ECDH Public value", this.encodedPoint);
        }
    }
}
