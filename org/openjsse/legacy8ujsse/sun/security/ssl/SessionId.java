package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SSLProtocolException;
import java.util.Arrays;
import java.security.SecureRandom;

final class SessionId
{
    static int MAX_LENGTH;
    private byte[] sessionId;
    
    SessionId(final boolean isRejoinable, final SecureRandom generator) {
        if (isRejoinable) {
            this.sessionId = new RandomCookie(generator).random_bytes;
        }
        else {
            this.sessionId = new byte[0];
        }
    }
    
    SessionId(final byte[] sessionId) {
        this.sessionId = sessionId;
    }
    
    int length() {
        return this.sessionId.length;
    }
    
    byte[] getId() {
        return this.sessionId.clone();
    }
    
    @Override
    public String toString() {
        final int len = this.sessionId.length;
        final StringBuffer s = new StringBuffer(10 + 2 * len);
        s.append("{");
        for (int i = 0; i < len; ++i) {
            s.append(0xFF & this.sessionId[i]);
            if (i != len - 1) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.sessionId);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SessionId)) {
            return false;
        }
        final SessionId s = (SessionId)obj;
        final byte[] b = s.getId();
        if (b.length != this.sessionId.length) {
            return false;
        }
        for (int i = 0; i < this.sessionId.length; ++i) {
            if (b[i] != this.sessionId[i]) {
                return false;
            }
        }
        return true;
    }
    
    void checkLength(final ProtocolVersion pv) throws SSLProtocolException {
        if (this.sessionId.length > SessionId.MAX_LENGTH) {
            throw new SSLProtocolException("Invalid session ID length (" + this.sessionId.length + " bytes)");
        }
    }
    
    static {
        SessionId.MAX_LENGTH = 32;
    }
}
