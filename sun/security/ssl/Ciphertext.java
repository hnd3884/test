package sun.security.ssl;

import javax.net.ssl.SSLEngineResult;

final class Ciphertext
{
    static final Ciphertext CIPHERTEXT_NULL;
    final byte contentType;
    final byte handshakeType;
    final long recordSN;
    SSLEngineResult.HandshakeStatus handshakeStatus;
    
    private Ciphertext() {
        this.contentType = 0;
        this.handshakeType = -1;
        this.recordSN = -1L;
        this.handshakeStatus = null;
    }
    
    Ciphertext(final byte contentType, final byte handshakeType, final long recordSN) {
        this.contentType = contentType;
        this.handshakeType = handshakeType;
        this.recordSN = recordSN;
        this.handshakeStatus = null;
    }
    
    static {
        CIPHERTEXT_NULL = new Ciphertext();
    }
}
