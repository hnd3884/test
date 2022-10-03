package com.unboundid.ldap.sdk;

import java.util.List;
import java.security.MessageDigest;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class SCRAMBindRequest extends SASLBindRequest
{
    private static final long serialVersionUID = -1141722265190138366L;
    private final ASN1OctetString password;
    private final String username;
    
    public SCRAMBindRequest(final String username, final ASN1OctetString password, final Control... controls) {
        super(controls);
        Validator.ensureNotNullOrEmpty(username, "SCRAMBindRequest.username must not be null or empty");
        Validator.ensureTrue(password != null && password.getValueLength() > 0, "SCRAMBindRequest.password must not be null or empty");
        this.username = username;
        this.password = password;
    }
    
    public final String getUsername() {
        return this.username;
    }
    
    public final String getPasswordString() {
        return this.password.stringValue();
    }
    
    public final byte[] getPasswordBytes() {
        return this.password.getValue();
    }
    
    protected abstract String getDigestAlgorithmName();
    
    protected abstract String getMACAlgorithmName();
    
    @Override
    protected final BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final SCRAMClientFirstMessage clientFirstMessage = new SCRAMClientFirstMessage(this);
        if (Debug.debugEnabled()) {
            Debug.debug(Level.INFO, DebugType.LDAP, "Sending " + this.getSASLMechanismName() + " client first message " + clientFirstMessage);
        }
        final BindResult serverFirstResult = this.sendBindRequest(connection, null, new ASN1OctetString(clientFirstMessage.getClientFirstMessage()), this.getControls(), this.getResponseTimeoutMillis(connection));
        if (serverFirstResult.getResultCode() != ResultCode.SASL_BIND_IN_PROGRESS) {
            return serverFirstResult;
        }
        final SCRAMServerFirstMessage serverFirstMessage = new SCRAMServerFirstMessage(this, clientFirstMessage, serverFirstResult);
        if (Debug.debugEnabled()) {
            Debug.debug(Level.INFO, DebugType.LDAP, "Received " + this.getSASLMechanismName() + " server first message " + serverFirstMessage);
        }
        final SCRAMClientFinalMessage clientFinalMessage = new SCRAMClientFinalMessage(this, clientFirstMessage, serverFirstMessage);
        if (Debug.debugEnabled()) {
            Debug.debug(Level.INFO, DebugType.LDAP, "Sending " + this.getSASLMechanismName() + " client final message " + clientFinalMessage);
        }
        final BindResult serverFinalResult = this.sendBindRequest(connection, null, new ASN1OctetString(clientFinalMessage.getClientFinalMessage()), this.getControls(), this.getResponseTimeoutMillis(connection));
        final SCRAMServerFinalMessage serverFinalMessage = new SCRAMServerFinalMessage(this, clientFirstMessage, clientFinalMessage, serverFinalResult);
        if (Debug.debugEnabled()) {
            Debug.debug(Level.INFO, DebugType.LDAP, "Received " + this.getSASLMechanismName() + " server final message " + serverFinalMessage);
        }
        return serverFinalResult;
    }
    
    final byte[] mac(final byte[] key, final byte[] data) throws LDAPBindException {
        return this.getMac(key).doFinal(data);
    }
    
    final Mac getMac(final byte[] key) throws LDAPBindException {
        try {
            final Mac mac = Mac.getInstance(this.getMACAlgorithmName());
            final SecretKeySpec macKey = new SecretKeySpec(key, this.getMACAlgorithmName());
            mac.init(macKey);
            return mac;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPBindException(new BindResult(-1, ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SCRAM_BIND_REQUEST_CANNOT_GET_MAC.get(this.getSASLMechanismName(), this.getMACAlgorithmName()), null, null, null, null));
        }
    }
    
    final byte[] digest(final byte[] data) throws LDAPBindException {
        try {
            final MessageDigest digest = MessageDigest.getInstance(this.getDigestAlgorithmName());
            return digest.digest(data);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPBindException(new BindResult(-1, ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SCRAM_BIND_REQUEST_CANNOT_GET_DIGEST.get(this.getSASLMechanismName(), this.getDigestAlgorithmName()), null, null, null, null));
        }
    }
    
    @Override
    public abstract SCRAMBindRequest getRebindRequest(final String p0, final int p1);
    
    @Override
    public abstract SCRAMBindRequest duplicate();
    
    @Override
    public abstract SCRAMBindRequest duplicate(final Control[] p0);
    
    @Override
    public abstract void toString(final StringBuilder p0);
    
    @Override
    public abstract void toCode(final List<String> p0, final String p1, final int p2, final boolean p3);
}
