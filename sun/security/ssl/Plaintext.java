package sun.security.ssl;

import javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;

final class Plaintext
{
    static final Plaintext PLAINTEXT_NULL;
    final byte contentType;
    final byte majorVersion;
    final byte minorVersion;
    final int recordEpoch;
    final long recordSN;
    final ByteBuffer fragment;
    SSLEngineResult.HandshakeStatus handshakeStatus;
    
    private Plaintext() {
        this.contentType = 0;
        this.majorVersion = 0;
        this.minorVersion = 0;
        this.recordEpoch = -1;
        this.recordSN = -1L;
        this.fragment = null;
        this.handshakeStatus = null;
    }
    
    Plaintext(final byte contentType, final byte majorVersion, final byte minorVersion, final int recordEpoch, final long recordSN, final ByteBuffer fragment) {
        this.contentType = contentType;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.recordEpoch = recordEpoch;
        this.recordSN = recordSN;
        this.fragment = fragment;
        this.handshakeStatus = null;
    }
    
    @Override
    public String toString() {
        return "contentType: " + this.contentType + "/majorVersion: " + this.majorVersion + "/minorVersion: " + this.minorVersion + "/recordEpoch: " + this.recordEpoch + "/recordSN: 0x" + Long.toHexString(this.recordSN) + "/fragment: " + this.fragment;
    }
    
    static {
        PLAINTEXT_NULL = new Plaintext();
    }
}
