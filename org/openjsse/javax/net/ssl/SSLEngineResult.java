package org.openjsse.javax.net.ssl;

public class SSLEngineResult extends javax.net.ssl.SSLEngineResult
{
    private final long sequenceNumber;
    private final boolean needUnwrapAgain;
    
    public SSLEngineResult(final Status status, final HandshakeStatus handshakeStatus, final int bytesConsumed, final int bytesProduced) {
        this(status, handshakeStatus, bytesConsumed, bytesProduced, -1L, false);
    }
    
    public SSLEngineResult(final Status status, final HandshakeStatus handshakeStatus, final int bytesConsumed, final int bytesProduced, final long sequenceNumber, final boolean needUnwrapAgain) {
        super(status, handshakeStatus, bytesConsumed, bytesProduced);
        this.sequenceNumber = sequenceNumber;
        this.needUnwrapAgain = needUnwrapAgain;
    }
    
    public final long sequenceNumber() {
        return this.sequenceNumber;
    }
    
    public final boolean needUnwrapAgain() {
        return this.needUnwrapAgain;
    }
    
    @Override
    public String toString() {
        return super.toString() + ((this.sequenceNumber == -1L) ? "" : (" sequenceNumber = " + Long.toUnsignedString(this.sequenceNumber)));
    }
}
