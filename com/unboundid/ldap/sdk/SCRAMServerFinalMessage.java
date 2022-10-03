package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Base64;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SCRAMServerFinalMessage implements Serializable
{
    private static final byte[] SERVER_KEY_INPUT_BYTES;
    private static final long serialVersionUID = -8799438618265483051L;
    private final BindResult bindResult;
    private final SCRAMBindRequest bindRequest;
    private final SCRAMClientFirstMessage clientFirstMessage;
    private final SCRAMClientFinalMessage clientFinalMessage;
    private final String serverFinalMessage;
    private final String serverSignatureBase64;
    
    SCRAMServerFinalMessage(final SCRAMBindRequest bindRequest, final SCRAMClientFirstMessage clientFirstMessage, final SCRAMClientFinalMessage clientFinalMessage, final BindResult bindResult) throws LDAPBindException {
        this.bindRequest = bindRequest;
        this.clientFirstMessage = clientFirstMessage;
        this.clientFinalMessage = clientFinalMessage;
        this.bindResult = bindResult;
        final ASN1OctetString serverSASLCredentials = bindResult.getServerSASLCredentials();
        if (serverSASLCredentials == null) {
            if (bindResult.getResultCode() == ResultCode.SUCCESS) {
                throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FINAL_MESSAGE_NO_CREDS.get(bindRequest.getSASLMechanismName()), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
            }
            throw new LDAPBindException(bindResult);
        }
        else {
            this.serverFinalMessage = serverSASLCredentials.stringValue();
            if (bindResult.getResultCode() != ResultCode.SUCCESS) {
                if (!this.serverFinalMessage.startsWith("e=")) {
                    throw new LDAPBindException(bindResult);
                }
                final int commaPos = this.serverFinalMessage.indexOf(44);
                String errorValue;
                if (commaPos > 0) {
                    errorValue = this.serverFinalMessage.substring(2, commaPos);
                }
                else {
                    errorValue = this.serverFinalMessage.substring(2);
                }
                final String diagnosticMessage = bindResult.getDiagnosticMessage();
                if (diagnosticMessage == null) {
                    throw new LDAPBindException(new BindResult(bindResult.getMessageID(), bindResult.getResultCode(), LDAPMessages.ERR_SCRAM_SERVER_FINAL_MESSAGE_ERROR_VALUE_NO_DIAG.get(bindRequest.getSASLMechanismName(), errorValue), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
                }
                throw new LDAPBindException(new BindResult(bindResult.getMessageID(), bindResult.getResultCode(), LDAPMessages.ERR_SCRAM_SERVER_FINAL_MESSAGE_ERROR_VALUE_WITH_DIAG.get(bindRequest.getSASLMechanismName(), errorValue, diagnosticMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
            }
            else {
                if (!this.serverFinalMessage.startsWith("v=")) {
                    throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FINAL_MESSAGE_NO_VERIFIER.get(bindRequest.getSASLMechanismName(), this.serverFinalMessage), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
                }
                final int commaPos2 = this.serverFinalMessage.indexOf(44);
                if (commaPos2 > 0) {
                    this.serverSignatureBase64 = this.serverFinalMessage.substring(2, commaPos2);
                }
                else {
                    this.serverSignatureBase64 = this.serverFinalMessage.substring(2);
                }
                final byte[] serverKey = bindRequest.mac(clientFinalMessage.getSaltedPassword(), SCRAMServerFinalMessage.SERVER_KEY_INPUT_BYTES);
                final byte[] serverSignature = bindRequest.mac(serverKey, clientFinalMessage.getAuthMessageBytes());
                final String expectedServerSignatureBase64 = Base64.encode(serverSignature);
                if (!expectedServerSignatureBase64.equals(this.serverSignatureBase64)) {
                    throw new LDAPBindException(new BindResult(bindResult.getMessageID(), ResultCode.DECODING_ERROR, LDAPMessages.ERR_SCRAM_SERVER_FINAL_MESSAGE_INCORRECT_VERIFIER.get(bindRequest.getSASLMechanismName(), this.serverFinalMessage, this.serverSignatureBase64, expectedServerSignatureBase64), bindResult.getMatchedDN(), bindResult.getReferralURLs(), bindResult.getResponseControls(), serverSASLCredentials));
                }
            }
        }
    }
    
    SCRAMBindRequest getBindRequest() {
        return this.bindRequest;
    }
    
    SCRAMClientFirstMessage getClientFirstMessage() {
        return this.clientFirstMessage;
    }
    
    SCRAMClientFinalMessage getClientFinalMessage() {
        return this.clientFinalMessage;
    }
    
    String getServerSignatureBase64() {
        return this.serverSignatureBase64;
    }
    
    String getServerFinalMessage() {
        return this.serverFinalMessage;
    }
    
    @Override
    public String toString() {
        return this.serverFinalMessage;
    }
    
    static {
        SERVER_KEY_INPUT_BYTES = StaticUtils.getBytes("Server Key");
    }
}
