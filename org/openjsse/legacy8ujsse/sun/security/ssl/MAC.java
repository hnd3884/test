package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.Mac;

final class MAC extends Authenticator
{
    static final MAC NULL;
    private static final byte[] nullMAC;
    private final CipherSuite.MacAlg macAlg;
    private final Mac mac;
    
    private MAC() {
        this.macAlg = CipherSuite.M_NULL;
        this.mac = null;
    }
    
    MAC(final CipherSuite.MacAlg macAlg, final ProtocolVersion protocolVersion, final SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        super(protocolVersion);
        this.macAlg = macAlg;
        final boolean tls = protocolVersion.v >= ProtocolVersion.TLS10.v;
        String algorithm;
        if (macAlg == CipherSuite.M_MD5) {
            algorithm = (tls ? "HmacMD5" : "SslMacMD5");
        }
        else if (macAlg == CipherSuite.M_SHA) {
            algorithm = (tls ? "HmacSHA1" : "SslMacSHA1");
        }
        else if (macAlg == CipherSuite.M_SHA256) {
            algorithm = "HmacSHA256";
        }
        else {
            if (macAlg != CipherSuite.M_SHA384) {
                throw new RuntimeException("Unknown Mac " + macAlg);
            }
            algorithm = "HmacSHA384";
        }
        (this.mac = JsseJce.getMac(algorithm)).init(key);
    }
    
    int MAClen() {
        return this.macAlg.size;
    }
    
    int hashBlockLen() {
        return this.macAlg.hashBlockSize;
    }
    
    int minimalPaddingLen() {
        return this.macAlg.minimalPaddingSize;
    }
    
    final byte[] compute(final byte type, final byte[] buf, final int offset, final int len, final boolean isSimulated) {
        if (this.macAlg.size == 0) {
            return MAC.nullMAC;
        }
        if (!isSimulated) {
            final byte[] additional = this.acquireAuthenticationBytes(type, len);
            this.mac.update(additional);
        }
        this.mac.update(buf, offset, len);
        return this.mac.doFinal();
    }
    
    final byte[] compute(final byte type, final ByteBuffer bb, final boolean isSimulated) {
        if (this.macAlg.size == 0) {
            return MAC.nullMAC;
        }
        if (!isSimulated) {
            final byte[] additional = this.acquireAuthenticationBytes(type, bb.remaining());
            this.mac.update(additional);
        }
        this.mac.update(bb);
        return this.mac.doFinal();
    }
    
    static {
        NULL = new MAC();
        nullMAC = new byte[0];
    }
}
