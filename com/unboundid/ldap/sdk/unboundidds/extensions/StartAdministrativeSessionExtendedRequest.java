package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class StartAdministrativeSessionExtendedRequest extends ExtendedRequest
{
    public static final String START_ADMIN_SESSION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.13";
    private static final byte TYPE_CLIENT_NAME = Byte.MIN_VALUE;
    private static final byte TYPE_USE_DEDICATED_THREAD_POOL = -127;
    private static final long serialVersionUID = -2684374559100906505L;
    private final boolean useDedicatedThreadPool;
    private final String clientName;
    
    public StartAdministrativeSessionExtendedRequest(final String clientName, final boolean useDedicatedThreadPool, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.13", encodeValue(clientName, useDedicatedThreadPool), controls);
        this.clientName = clientName;
        this.useDedicatedThreadPool = useDedicatedThreadPool;
    }
    
    public StartAdministrativeSessionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_ADMIN_SESSION_REQUEST_NO_VALUE.get());
        }
        String appName = null;
        boolean dedicatedPool = false;
        try {
            final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
            for (final ASN1Element e : valueSequence.elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        appName = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        dedicatedPool = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_ADMIN_SESSION_REQUEST_UNKNOWN_VALUE_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_ADMIN_SESSION_REQUEST_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        this.clientName = appName;
        this.useDedicatedThreadPool = dedicatedPool;
    }
    
    private static ASN1OctetString encodeValue(final String clientName, final boolean useDedicatedThreadPool) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (clientName != null) {
            elements.add(new ASN1OctetString((byte)(-128), clientName));
        }
        if (useDedicatedThreadPool) {
            elements.add(new ASN1Boolean((byte)(-127), true));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getClientName() {
        return this.clientName;
    }
    
    public boolean useDedicatedThreadPool() {
        return this.useDedicatedThreadPool;
    }
    
    @Override
    public StartAdministrativeSessionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public StartAdministrativeSessionExtendedRequest duplicate(final Control[] controls) {
        return new StartAdministrativeSessionExtendedRequest(this.clientName, this.useDedicatedThreadPool, controls);
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_START_ADMIN_SESSION.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StartAdministrativeSessionExtendedRequest(");
        if (this.clientName != null) {
            buffer.append("clientName='");
            buffer.append(this.clientName);
            buffer.append("', ");
        }
        buffer.append("useDedicatedThreadPool=");
        buffer.append(this.useDedicatedThreadPool);
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
