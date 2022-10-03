package com.unboundid.ldap.sdk;

import com.unboundid.util.Base64;
import java.security.SecureRandom;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SCRAMClientFirstMessage implements Serializable
{
    private static final String GS2_HEADER_NO_CHANNEL_BINDING = "n,,";
    private static final String GS2_HEADER_NO_CHANNEL_BINDING_BASE64;
    private static final long serialVersionUID = 7117556259158222514L;
    private final SCRAMBindRequest bindRequest;
    private final String clientFirstMessage;
    private final String clientFirstMessageBare;
    private final String clientNonce;
    private final String gs2HeaderBase64;
    private final String gs2HeaderRaw;
    
    SCRAMClientFirstMessage(final SCRAMBindRequest bindRequest) {
        this(bindRequest, null);
    }
    
    SCRAMClientFirstMessage(final SCRAMBindRequest bindRequest, final String clientNonce) {
        this.bindRequest = bindRequest;
        if (clientNonce == null) {
            final SecureRandom random = new SecureRandom();
            final byte[] clientNonceBytes = new byte[16];
            random.nextBytes(clientNonceBytes);
            this.clientNonce = Base64.urlEncode(clientNonceBytes, false);
        }
        else {
            this.clientNonce = clientNonce;
        }
        this.gs2HeaderRaw = "n,,";
        this.gs2HeaderBase64 = SCRAMClientFirstMessage.GS2_HEADER_NO_CHANNEL_BINDING_BASE64;
        this.clientFirstMessageBare = "n=" + bindRequest.getUsername() + ",r=" + this.clientNonce;
        this.clientFirstMessage = this.gs2HeaderRaw + this.clientFirstMessageBare;
    }
    
    SCRAMBindRequest getBindRequest() {
        return this.bindRequest;
    }
    
    String getGS2HeaderRaw() {
        return this.gs2HeaderRaw;
    }
    
    String getGS2HeaderBase64() {
        return this.gs2HeaderBase64;
    }
    
    String getClientNonce() {
        return this.clientNonce;
    }
    
    String getClientFirstMessage() {
        return this.clientFirstMessage;
    }
    
    String getClientFirstMessageBare() {
        return this.clientFirstMessageBare;
    }
    
    @Override
    public String toString() {
        return this.clientFirstMessage;
    }
    
    static {
        GS2_HEADER_NO_CHANNEL_BINDING_BASE64 = Base64.encode("n,,");
    }
}
