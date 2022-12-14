package javax.net.ssl;

public class SSLEngineResult
{
    private final Status status;
    private final HandshakeStatus handshakeStatus;
    private final int bytesConsumed;
    private final int bytesProduced;
    
    public SSLEngineResult(final Status status, final HandshakeStatus handshakeStatus, final int bytesConsumed, final int bytesProduced) {
        if (status == null || handshakeStatus == null || bytesConsumed < 0 || bytesProduced < 0) {
            throw new IllegalArgumentException("Invalid Parameter(s)");
        }
        this.status = status;
        this.handshakeStatus = handshakeStatus;
        this.bytesConsumed = bytesConsumed;
        this.bytesProduced = bytesProduced;
    }
    
    public final Status getStatus() {
        return this.status;
    }
    
    public final HandshakeStatus getHandshakeStatus() {
        return this.handshakeStatus;
    }
    
    public final int bytesConsumed() {
        return this.bytesConsumed;
    }
    
    public final int bytesProduced() {
        return this.bytesProduced;
    }
    
    @Override
    public String toString() {
        return "Status = " + this.status + " HandshakeStatus = " + this.handshakeStatus + "\nbytesConsumed = " + this.bytesConsumed + " bytesProduced = " + this.bytesProduced;
    }
    
    public enum Status
    {
        BUFFER_UNDERFLOW, 
        BUFFER_OVERFLOW, 
        OK, 
        CLOSED;
    }
    
    public enum HandshakeStatus
    {
        NOT_HANDSHAKING, 
        FINISHED, 
        NEED_TASK, 
        NEED_WRAP, 
        NEED_UNWRAP;
    }
}
