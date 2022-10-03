package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import javax.security.sasl.SaslClient;
import java.util.List;

final class SASLHelper
{
    private final Control[] controls;
    private volatile int messageID;
    private final LDAPConnection connection;
    private final List<String> unhandledCallbackMessages;
    private final long responseTimeoutMillis;
    private final SASLBindRequest bindRequest;
    private final SaslClient saslClient;
    private final String mechanism;
    
    SASLHelper(final SASLBindRequest bindRequest, final LDAPConnection connection, final String mechanism, final SaslClient saslClient, final Control[] controls, final long responseTimeoutMillis, final List<String> unhandledCallbackMessages) {
        this.bindRequest = bindRequest;
        this.connection = connection;
        this.mechanism = mechanism;
        this.saslClient = saslClient;
        this.controls = controls;
        this.responseTimeoutMillis = responseTimeoutMillis;
        this.unhandledCallbackMessages = unhandledCallbackMessages;
        this.messageID = -1;
    }
    
    BindResult processSASLBind() throws LDAPException {
        try {
            byte[] credBytes = null;
            try {
                if (this.saslClient.hasInitialResponse()) {
                    credBytes = this.saslClient.evaluateChallenge(new byte[0]);
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (this.unhandledCallbackMessages.isEmpty()) {
                    throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SASL_CANNOT_CREATE_INITIAL_REQUEST.get(this.mechanism, StaticUtils.getExceptionMessage(e)), e);
                }
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SASL_CANNOT_CREATE_INITIAL_REQUEST_UNHANDLED_CALLBACKS.get(this.mechanism, StaticUtils.getExceptionMessage(e), StaticUtils.concatenateStrings(this.unhandledCallbackMessages)), e);
            }
            ASN1OctetString saslCredentials;
            if (credBytes == null || credBytes.length == 0) {
                saslCredentials = null;
            }
            else {
                saslCredentials = new ASN1OctetString(credBytes);
            }
            BindResult bindResult = this.bindRequest.sendBindRequest(this.connection, "", saslCredentials, this.controls, this.responseTimeoutMillis);
            this.messageID = this.bindRequest.getLastMessageID();
            if (!bindResult.getResultCode().equals(ResultCode.SASL_BIND_IN_PROGRESS)) {
                return bindResult;
            }
            ASN1OctetString serverCreds = bindResult.getServerSASLCredentials();
            byte[] serverCredBytes;
            if (serverCreds == null) {
                serverCredBytes = null;
            }
            else {
                serverCredBytes = serverCreds.getValue();
            }
            while (true) {
                try {
                    credBytes = this.saslClient.evaluateChallenge(serverCredBytes);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    if (this.unhandledCallbackMessages.isEmpty()) {
                        throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SASL_CANNOT_CREATE_SUBSEQUENT_REQUEST.get(this.mechanism, StaticUtils.getExceptionMessage(e2)), e2);
                    }
                    throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SASL_CANNOT_CREATE_SUBSEQUENT_REQUEST_UNHANDLED_CALLBACKS.get(this.mechanism, StaticUtils.getExceptionMessage(e2), StaticUtils.concatenateStrings(this.unhandledCallbackMessages)), e2);
                }
                if (credBytes == null || credBytes.length == 0) {
                    saslCredentials = null;
                }
                else {
                    saslCredentials = new ASN1OctetString(credBytes);
                }
                bindResult = this.bindRequest.sendBindRequest(this.connection, "", saslCredentials, this.controls, this.responseTimeoutMillis);
                this.messageID = this.bindRequest.getLastMessageID();
                if (!bindResult.getResultCode().equals(ResultCode.SASL_BIND_IN_PROGRESS)) {
                    break;
                }
                serverCreds = bindResult.getServerSASLCredentials();
                if (serverCreds == null) {
                    serverCredBytes = null;
                }
                else {
                    serverCredBytes = serverCreds.getValue();
                }
            }
            final ASN1OctetString serverCredentials = bindResult.getServerSASLCredentials();
            if (serverCredentials != null) {
                try {
                    this.saslClient.evaluateChallenge(serverCredentials.getValue());
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
            return bindResult;
        }
        finally {
            boolean hasNegotiatedSecurity = false;
            if (this.saslClient.isComplete()) {
                final Object qopObject = this.saslClient.getNegotiatedProperty("javax.security.sasl.qop");
                if (qopObject != null) {
                    final String qopString = StaticUtils.toLowerCase(String.valueOf(qopObject));
                    if (qopString.contains(SASLQualityOfProtection.AUTH_INT.toString()) || qopString.contains(SASLQualityOfProtection.AUTH_CONF.toString())) {
                        hasNegotiatedSecurity = true;
                    }
                }
            }
            if (hasNegotiatedSecurity) {
                this.connection.applySASLQoP(this.saslClient);
            }
            else {
                try {
                    this.saslClient.dispose();
                }
                catch (final Exception e4) {
                    Debug.debugException(e4);
                }
            }
        }
    }
    
    int getMessageID() {
        return this.messageID;
    }
}
