package org.openjsse.sun.security.ssl;

import javax.net.ssl.SSLProtocolException;
import java.util.Arrays;
import java.security.SecureRandom;

final class SessionId
{
    private static final int MAX_LENGTH = 32;
    private final byte[] sessionId;
    
    SessionId(final boolean isRejoinable, final SecureRandom generator) {
        if (isRejoinable && generator != null) {
            this.sessionId = new RandomCookie(generator).randomBytes;
        }
        else {
            this.sessionId = new byte[0];
        }
    }
    
    SessionId(final byte[] sessionId) {
        this.sessionId = sessionId.clone();
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
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SessionId) {
            final SessionId that = (SessionId)obj;
            return Arrays.equals(this.sessionId, that.sessionId);
        }
        return false;
    }
    
    void checkLength(final int protocolVersion) throws SSLProtocolException {
        if (this.sessionId.length > 32) {
            throw new SSLProtocolException("Invalid session ID length (" + this.sessionId.length + " bytes)");
        }
    }
}
