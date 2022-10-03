package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedRequest;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IdentifyBackupCompatibilityProblemsExtendedRequest extends ExtendedRequest
{
    public static final String IDENTIFY_BACKUP_COMPATIBILITY_PROBLEMS_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.32";
    private static final byte TYPE_SOURCE_DESCRIPTOR = Byte.MIN_VALUE;
    private static final byte TYPE_TARGET_DESCRIPTOR = -127;
    private static final long serialVersionUID = 6723590129573376599L;
    private final ASN1OctetString sourceDescriptor;
    private final ASN1OctetString targetDescriptor;
    
    public IdentifyBackupCompatibilityProblemsExtendedRequest(final ASN1OctetString sourceDescriptor, final ASN1OctetString targetDescriptor, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.32", encodeValue(sourceDescriptor, targetDescriptor), controls);
        this.sourceDescriptor = new ASN1OctetString((byte)(-128), sourceDescriptor.getValue());
        this.targetDescriptor = new ASN1OctetString((byte)(-127), targetDescriptor.getValue());
    }
    
    public IdentifyBackupCompatibilityProblemsExtendedRequest(final ExtendedRequest r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.sourceDescriptor = new ASN1OctetString((byte)(-128), elements[0].getValue());
            this.targetDescriptor = new ASN1OctetString((byte)(-128), elements[1].getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_REQUEST_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString sourceDescriptor, final ASN1OctetString targetDescriptor) {
        Validator.ensureNotNull(sourceDescriptor);
        Validator.ensureNotNull(targetDescriptor);
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString((byte)(-128), sourceDescriptor.getValue()), new ASN1OctetString((byte)(-127), targetDescriptor.getValue()) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public ASN1OctetString getSourceDescriptor() {
        return this.sourceDescriptor;
    }
    
    public ASN1OctetString getTargetDescriptor() {
        return this.targetDescriptor;
    }
    
    public IdentifyBackupCompatibilityProblemsExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new IdentifyBackupCompatibilityProblemsExtendedResult(extendedResponse);
    }
    
    @Override
    public IdentifyBackupCompatibilityProblemsExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public IdentifyBackupCompatibilityProblemsExtendedRequest duplicate(final Control[] controls) {
        final IdentifyBackupCompatibilityProblemsExtendedRequest r = new IdentifyBackupCompatibilityProblemsExtendedRequest(this.sourceDescriptor, this.targetDescriptor, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_IDENTIFY_BACKUP_COMPAT_PROBLEMS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IdentifyBackupCompatibilityProblemsExtendedRequest(sourceDescriptorLength=");
        buffer.append(this.sourceDescriptor.getValueLength());
        buffer.append(", targetDescriptorLength=");
        buffer.append(this.targetDescriptor.getValueLength());
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
