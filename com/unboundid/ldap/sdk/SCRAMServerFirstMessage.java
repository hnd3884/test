package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.util.Base64;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SCRAMServerFirstMessage implements Serializable
{
    private static final int MINIMUM_ALLOWED_ITERATION_COUNT = 4096;
    private static final long serialVersionUID = 3888813341685523286L;
    private final BindResult bindResult;
    private final byte[] salt;
    private final int iterationCount;
    private final SCRAMBindRequest bindRequest;
    private final SCRAMClientFirstMessage clientFirstMessage;
    private final String serverFirstMessage;
    private final String combinedNonce;
    private final String serverNonce;
    
    SCRAMServerFirstMessage(final SCRAMBindRequest bindRequest, final SCRAMClientFirstMessage clientFirstMessage, final BindResult bindResult) throws LDAPBindException {
        this.bindRequest = bindRequest;
        this.clientFirstMessage = clientFirstMessage;
        this.bindResult = bindResult;
        final ASN1OctetString serverSASLCredentials = bindResult.getServerSASLCredentials();
        if (serverSASLCredentials == null) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_NO_CREDS.get(bindRequest.getSASLMechanismName()), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        this.serverFirstMessage = serverSASLCredentials.stringValue();
        if (!this.serverFirstMessage.startsWith("r=")) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_NO_NONCE.get(bindRequest.getSASLMechanismName(), this.serverFirstMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        final int commaSEqualsPos = this.serverFirstMessage.indexOf(",s=");
        if (commaSEqualsPos < 0) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_NO_SALT.get(bindRequest.getSASLMechanismName(), this.serverFirstMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        final int commaIEqualsPos = this.serverFirstMessage.indexOf(",i=", commaSEqualsPos);
        if (commaIEqualsPos < 0) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_NO_ITERATION_COUNT.get(bindRequest.getSASLMechanismName(), this.serverFirstMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        this.combinedNonce = this.serverFirstMessage.substring(2, commaSEqualsPos);
        if (!this.combinedNonce.startsWith(clientFirstMessage.getClientNonce())) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_NONCE_MISSING_CLIENT.get(bindRequest.getSASLMechanismName(), this.serverFirstMessage, this.combinedNonce, clientFirstMessage.getClientNonce(), clientFirstMessage.getClientFirstMessage()), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        if (this.combinedNonce.equals(clientFirstMessage.getClientNonce())) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_NONCE_MISSING_SERVER.get(bindRequest.getSASLMechanismName(), this.serverFirstMessage, this.combinedNonce, clientFirstMessage.getClientFirstMessage()), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        this.serverNonce = this.combinedNonce.substring(clientFirstMessage.getClientNonce().length());
        final String saltString = this.serverFirstMessage.substring(commaSEqualsPos + 3, commaIEqualsPos);
        if (saltString.isEmpty()) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_SALT_EMPTY.get(bindRequest.getSASLMechanismName(), this.serverFirstMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        try {
            this.salt = Base64.decode(saltString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_SALT_NOT_BASE64.get(bindRequest.getSASLMechanismName(), saltString, this.serverFirstMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        final int extensionCommaPos = this.serverFirstMessage.indexOf(44, commaIEqualsPos + 1);
        String iterationCountString;
        if (extensionCommaPos > 0) {
            iterationCountString = this.serverFirstMessage.substring(commaIEqualsPos + 3, extensionCommaPos);
        }
        else {
            iterationCountString = this.serverFirstMessage.substring(commaIEqualsPos + 3);
        }
        try {
            this.iterationCount = Integer.parseInt(iterationCountString);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_ITERATION_COUNT_NOT_INTEGER.get(bindRequest.getSASLMechanismName(), iterationCountString, this.serverFirstMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
        if (this.iterationCount < 4096) {
            throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FIRST_MESSAGE_ITERATION_COUNT_BELOW_MINIMUM.get(bindRequest.getSASLMechanismName(), this.iterationCount, this.serverFirstMessage, 4096), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
        }
    }
    
    SCRAMBindRequest getBindRequest() {
        return this.bindRequest;
    }
    
    SCRAMClientFirstMessage getClientFirstMessage() {
        return this.clientFirstMessage;
    }
    
    BindResult getBindResult() {
        return this.bindResult;
    }
    
    String getCombinedNonce() {
        return this.combinedNonce;
    }
    
    String getServerNonce() {
        return this.serverNonce;
    }
    
    byte[] getSalt() {
        return this.salt;
    }
    
    int getIterationCount() {
        return this.iterationCount;
    }
    
    String getServerFirstMessage() {
        return this.serverFirstMessage;
    }
    
    @Override
    public String toString() {
        return this.serverFirstMessage;
    }
}
