package sun.security.ssl;

import javax.net.ssl.SSLProtocolException;
import java.util.Arrays;
import java.security.SecureRandom;

final class SessionId
{
    private static final int MAX_LENGTH = 32;
    private final byte[] sessionId;
    
    SessionId(final boolean b, final SecureRandom secureRandom) {
        if (b && secureRandom != null) {
            this.sessionId = new RandomCookie(secureRandom).randomBytes;
        }
        else {
            this.sessionId = new byte[0];
        }
    }
    
    SessionId(final byte[] array) {
        this.sessionId = array.clone();
    }
    
    int length() {
        return this.sessionId.length;
    }
    
    byte[] getId() {
        return this.sessionId.clone();
    }
    
    @Override
    public String toString() {
        if (this.sessionId.length == 0) {
            return "";
        }
        return Utilities.toHexString(this.sessionId);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.sessionId);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SessionId && Arrays.equals(this.sessionId, ((SessionId)o).sessionId));
    }
    
    void checkLength(final int n) throws SSLProtocolException {
        if (this.sessionId.length > 32) {
            throw new SSLProtocolException("Invalid session ID length (" + this.sessionId.length + " bytes)");
        }
    }
}
