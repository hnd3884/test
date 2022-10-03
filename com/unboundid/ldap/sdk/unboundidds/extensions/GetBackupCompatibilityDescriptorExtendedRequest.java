package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedRequest;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetBackupCompatibilityDescriptorExtendedRequest extends ExtendedRequest
{
    public static final String GET_BACKUP_COMPATIBILITY_DESCRIPTOR_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.30";
    private static final byte TYPE_BASE_DN = Byte.MIN_VALUE;
    private static final long serialVersionUID = 8170562432854535935L;
    private final String baseDN;
    
    public GetBackupCompatibilityDescriptorExtendedRequest(final String baseDN, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.30", encodeValue(baseDN), controls);
        this.baseDN = baseDN;
    }
    
    public GetBackupCompatibilityDescriptorExtendedRequest(final ExtendedRequest r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_BACKUP_COMPAT_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.baseDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_BACKUP_COMPAT_REQUEST_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String baseDN) {
        Validator.ensureNotNull(baseDN);
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString((byte)(-128), baseDN) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public GetBackupCompatibilityDescriptorExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new GetBackupCompatibilityDescriptorExtendedResult(extendedResponse);
    }
    
    @Override
    public GetBackupCompatibilityDescriptorExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetBackupCompatibilityDescriptorExtendedRequest duplicate(final Control[] controls) {
        final GetBackupCompatibilityDescriptorExtendedRequest r = new GetBackupCompatibilityDescriptorExtendedRequest(this.baseDN, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_GET_BACKUP_COMPAT.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetBackupCompatibilityDescriptorExtendedRequest(baseDN='");
        buffer.append(this.baseDN);
        buffer.append('\'');
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
