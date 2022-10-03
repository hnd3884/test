package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.GenericSASLBindRequest;
import com.unboundid.util.LDAPSDKUsageException;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class BindRequestProtocolOp implements ProtocolOp
{
    public static final byte CRED_TYPE_SIMPLE = Byte.MIN_VALUE;
    public static final byte CRED_TYPE_SASL = -93;
    private static final long serialVersionUID = 6661208657485444954L;
    private final ASN1OctetString saslCredentials;
    private final ASN1OctetString simplePassword;
    private final byte credentialsType;
    private final int version;
    private final String bindDN;
    private final String saslMechanism;
    
    public BindRequestProtocolOp(final String bindDN, final String password) {
        if (bindDN == null) {
            this.bindDN = "";
        }
        else {
            this.bindDN = bindDN;
        }
        if (password == null) {
            this.simplePassword = new ASN1OctetString((byte)(-128));
        }
        else {
            this.simplePassword = new ASN1OctetString((byte)(-128), password);
        }
        this.version = 3;
        this.credentialsType = -128;
        this.saslMechanism = null;
        this.saslCredentials = null;
    }
    
    public BindRequestProtocolOp(final String bindDN, final byte[] password) {
        if (bindDN == null) {
            this.bindDN = "";
        }
        else {
            this.bindDN = bindDN;
        }
        if (password == null) {
            this.simplePassword = new ASN1OctetString((byte)(-128));
        }
        else {
            this.simplePassword = new ASN1OctetString((byte)(-128), password);
        }
        this.version = 3;
        this.credentialsType = -128;
        this.saslMechanism = null;
        this.saslCredentials = null;
    }
    
    public BindRequestProtocolOp(final String bindDN, final String saslMechanism, final ASN1OctetString saslCredentials) {
        this.saslMechanism = saslMechanism;
        this.saslCredentials = saslCredentials;
        if (bindDN == null) {
            this.bindDN = "";
        }
        else {
            this.bindDN = bindDN;
        }
        this.version = 3;
        this.credentialsType = -93;
        this.simplePassword = null;
    }
    
    public BindRequestProtocolOp(final SimpleBindRequest request) throws LDAPSDKUsageException {
        this.version = 3;
        this.credentialsType = -128;
        this.bindDN = request.getBindDN();
        this.simplePassword = request.getPassword();
        this.saslMechanism = null;
        this.saslCredentials = null;
        if (this.simplePassword == null) {
            throw new LDAPSDKUsageException(ProtocolMessages.ERR_BIND_REQUEST_CANNOT_CREATE_WITH_PASSWORD_PROVIDER.get());
        }
    }
    
    public BindRequestProtocolOp(final GenericSASLBindRequest request) {
        this.version = 3;
        this.credentialsType = -93;
        this.bindDN = request.getBindDN();
        this.simplePassword = null;
        this.saslMechanism = request.getSASLMechanismName();
        this.saslCredentials = request.getCredentials();
    }
    
    BindRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            reader.beginSequence();
            this.version = reader.readInteger();
            this.bindDN = reader.readString();
            this.credentialsType = (byte)reader.peek();
            Validator.ensureNotNull(this.bindDN);
            switch (this.credentialsType) {
                case Byte.MIN_VALUE: {
                    this.simplePassword = new ASN1OctetString(this.credentialsType, reader.readBytes());
                    this.saslMechanism = null;
                    this.saslCredentials = null;
                    Validator.ensureNotNull(this.bindDN);
                    break;
                }
                case -93: {
                    final ASN1StreamReaderSequence saslSequence = reader.beginSequence();
                    Validator.ensureNotNull(this.saslMechanism = reader.readString());
                    if (saslSequence.hasMoreElements()) {
                        this.saslCredentials = new ASN1OctetString(reader.readBytes());
                    }
                    else {
                        this.saslCredentials = null;
                    }
                    this.simplePassword = null;
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_BIND_REQUEST_INVALID_CRED_TYPE.get(StaticUtils.toHex(this.credentialsType)));
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_BIND_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private BindRequestProtocolOp(final int version, final String bindDN, final byte credentialsType, final ASN1OctetString simplePassword, final String saslMechanism, final ASN1OctetString saslCredentials) {
        this.version = version;
        this.bindDN = bindDN;
        this.credentialsType = credentialsType;
        this.simplePassword = simplePassword;
        this.saslMechanism = saslMechanism;
        this.saslCredentials = saslCredentials;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public String getBindDN() {
        return this.bindDN;
    }
    
    public byte getCredentialsType() {
        return this.credentialsType;
    }
    
    public ASN1OctetString getSimplePassword() {
        return this.simplePassword;
    }
    
    public String getSASLMechanism() {
        return this.saslMechanism;
    }
    
    public ASN1OctetString getSASLCredentials() {
        return this.saslCredentials;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 96;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        ASN1Element credentials;
        if (this.credentialsType == -128) {
            credentials = this.simplePassword;
        }
        else if (this.saslCredentials == null) {
            credentials = new ASN1Sequence((byte)(-93), new ASN1Element[] { new ASN1OctetString(this.saslMechanism) });
        }
        else {
            credentials = new ASN1Sequence((byte)(-93), new ASN1Element[] { new ASN1OctetString(this.saslMechanism), this.saslCredentials });
        }
        return new ASN1Sequence((byte)96, new ASN1Element[] { new ASN1Integer(this.version), new ASN1OctetString(this.bindDN), credentials });
    }
    
    public static BindRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final int version = ASN1Integer.decodeAsInteger(elements[0]).intValue();
            final String bindDN = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            ASN1OctetString simplePassword = null;
            String saslMechanism = null;
            ASN1OctetString saslCredentials = null;
            switch (elements[2].getType()) {
                case Byte.MIN_VALUE: {
                    simplePassword = ASN1OctetString.decodeAsOctetString(elements[2]);
                    saslMechanism = null;
                    saslCredentials = null;
                    break;
                }
                case -93: {
                    final ASN1Element[] saslElements = ASN1Sequence.decodeAsSequence(elements[2]).elements();
                    saslMechanism = ASN1OctetString.decodeAsOctetString(saslElements[0]).stringValue();
                    if (saslElements.length == 1) {
                        saslCredentials = null;
                    }
                    else {
                        saslCredentials = ASN1OctetString.decodeAsOctetString(saslElements[1]);
                    }
                    simplePassword = null;
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_BIND_REQUEST_INVALID_CRED_TYPE.get(StaticUtils.toHex(elements[2].getType())));
                }
            }
            return new BindRequestProtocolOp(version, bindDN, elements[2].getType(), simplePassword, saslMechanism, saslCredentials);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_BIND_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)96);
        buffer.addInteger(this.version);
        buffer.addOctetString(this.bindDN);
        if (this.credentialsType == -128) {
            buffer.addElement(this.simplePassword);
        }
        else {
            final ASN1BufferSequence saslSequence = buffer.beginSequence((byte)(-93));
            buffer.addOctetString(this.saslMechanism);
            if (this.saslCredentials != null) {
                buffer.addElement(this.saslCredentials);
            }
            saslSequence.end();
        }
        opSequence.end();
        buffer.setZeroBufferOnClear();
    }
    
    public BindRequest toBindRequest(final Control... controls) {
        if (this.credentialsType == -128) {
            return new SimpleBindRequest(this.bindDN, this.simplePassword.getValue(), controls);
        }
        return new GenericSASLBindRequest(this.bindDN, this.saslMechanism, this.saslCredentials, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BindRequestProtocolOp(version=");
        buffer.append(this.version);
        buffer.append(", bindDN='");
        buffer.append(this.bindDN);
        buffer.append("', type=");
        if (this.credentialsType == -128) {
            buffer.append("simple");
        }
        else {
            buffer.append("SASL, mechanism=");
            buffer.append(this.saslMechanism);
        }
        buffer.append(')');
    }
}
