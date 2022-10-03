package com.adventnet.sym.server.mdm.android.payload.transform;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;

public class AndroidSCEPPayload extends AndroidPayload
{
    public AndroidSCEPPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Scep", payloadIdentifier, payloadDisplayName);
    }
    
    public void setURL(final String url) throws JSONException {
        this.getPayloadJSON().put("URL", (Object)url);
    }
    
    public void setCAName(final String caName) throws JSONException {
        this.getPayloadJSON().put("Name", (Object)caName);
    }
    
    public void setSubject(final String subject) throws JSONException {
        this.getPayloadJSON().put("Subject", (Object)subject);
    }
    
    public void setSANType(final Integer sanType) throws JSONException {
        this.getPayloadJSON().put("SubjectAlternativeNameType", (Object)sanType);
    }
    
    public void setSubjecAltName(final String subjectAltName) throws JSONException {
        this.getPayloadJSON().put("SubjectAlternativeName", (Object)subjectAltName);
    }
    
    public void setChallenge(final String challenge) throws JSONException {
        this.getPayloadJSON().put("Challenge", (Object)challenge);
    }
    
    public void setRetries(final Integer retries) throws JSONException {
        this.getPayloadJSON().put("Retries", (Object)retries);
    }
    
    public void setRetryDelay(final Integer retryDelay) throws JSONException {
        this.getPayloadJSON().put("RetryDelay", (Object)retryDelay);
    }
    
    public void setChallengePassword(final String challengePassword) throws JSONException {
        this.getPayloadJSON().put("ChallengePassword", (Object)challengePassword);
    }
    
    public void setPrivateKeySize(final Integer keySize) throws JSONException {
        this.getPayloadJSON().put("KeySize", (Object)keySize);
    }
    
    public void setCACertificatePayload(final String caCertificatePayload) {
        this.getPayloadJSON().put("CACertificate", (Object)caCertificatePayload);
    }
    
    public void setKeyUsage(final Integer keyUsage) throws JSONException {
        this.getPayloadJSON().put("KeyUsage", (Object)keyUsage);
    }
}
