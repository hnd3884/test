package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.asn1.ASN1Null;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetPasswordQualityRequirementsExtendedRequest extends ExtendedRequest
{
    public static final String OID_GET_PASSWORD_QUALITY_REQUIREMENTS_REQUEST = "1.3.6.1.4.1.30221.2.6.43";
    private static final long serialVersionUID = -3652010872400265557L;
    private final GetPasswordQualityRequirementsTargetType targetType;
    private final String targetDN;
    
    private GetPasswordQualityRequirementsExtendedRequest(final GetPasswordQualityRequirementsTargetType targetType, final String targetDN, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.43", encodeValue(targetType, targetDN), controls);
        this.targetType = targetType;
        this.targetDN = targetDN;
    }
    
    public GetPasswordQualityRequirementsExtendedRequest(final ExtendedRequest r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_PW_QUALITY_REQS_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.targetType = GetPasswordQualityRequirementsTargetType.forBERType(elements[0].getType());
            if (this.targetType == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_PW_QUALITY_REQS_REQUEST_UNKNOWN_TARGET_TYPE.get(StaticUtils.toHex(elements[0].getType())));
            }
            switch (this.targetType) {
                case ADD_WITH_SPECIFIED_PASSWORD_POLICY:
                case SELF_CHANGE_FOR_SPECIFIED_USER:
                case ADMINISTRATIVE_RESET_FOR_SPECIFIED_USER: {
                    this.targetDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
                    break;
                }
                default: {
                    this.targetDN = null;
                    break;
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_PW_QUALITY_REQS_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final GetPasswordQualityRequirementsTargetType targetType, final String targetDN) {
        ASN1Element targetElement = null;
        switch (targetType) {
            case ADD_WITH_SPECIFIED_PASSWORD_POLICY:
            case SELF_CHANGE_FOR_SPECIFIED_USER:
            case ADMINISTRATIVE_RESET_FOR_SPECIFIED_USER: {
                targetElement = new ASN1OctetString(targetType.getBERType(), targetDN);
                break;
            }
            default: {
                targetElement = new ASN1Null(targetType.getBERType());
                break;
            }
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { targetElement });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public static GetPasswordQualityRequirementsExtendedRequest createAddWithDefaultPasswordPolicyRequest(final Control... controls) {
        return new GetPasswordQualityRequirementsExtendedRequest(GetPasswordQualityRequirementsTargetType.ADD_WITH_DEFAULT_PASSWORD_POLICY, null, controls);
    }
    
    public static GetPasswordQualityRequirementsExtendedRequest createAddWithSpecifiedPasswordPolicyRequest(final String policyDN, final Control... controls) {
        return new GetPasswordQualityRequirementsExtendedRequest(GetPasswordQualityRequirementsTargetType.ADD_WITH_SPECIFIED_PASSWORD_POLICY, policyDN, controls);
    }
    
    public static GetPasswordQualityRequirementsExtendedRequest createSelfChangeWithSameAuthorizationIdentityRequest(final Control... controls) {
        return new GetPasswordQualityRequirementsExtendedRequest(GetPasswordQualityRequirementsTargetType.SELF_CHANGE_FOR_AUTHORIZATION_IDENTITY, null, controls);
    }
    
    public static GetPasswordQualityRequirementsExtendedRequest createSelfChangeForSpecifiedUserRequest(final String userDN, final Control... controls) {
        return new GetPasswordQualityRequirementsExtendedRequest(GetPasswordQualityRequirementsTargetType.SELF_CHANGE_FOR_SPECIFIED_USER, userDN, controls);
    }
    
    public static GetPasswordQualityRequirementsExtendedRequest createAdministrativeResetForSpecifiedUserRequest(final String userDN, final Control... controls) {
        return new GetPasswordQualityRequirementsExtendedRequest(GetPasswordQualityRequirementsTargetType.ADMINISTRATIVE_RESET_FOR_SPECIFIED_USER, userDN, controls);
    }
    
    public GetPasswordQualityRequirementsTargetType getTargetType() {
        return this.targetType;
    }
    
    public String getTargetDN() {
        return this.targetDN;
    }
    
    public GetPasswordQualityRequirementsExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult result = super.process(connection, depth);
        return new GetPasswordQualityRequirementsExtendedResult(result);
    }
    
    @Override
    public GetPasswordQualityRequirementsExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetPasswordQualityRequirementsExtendedRequest duplicate(final Control[] controls) {
        final GetPasswordQualityRequirementsExtendedRequest r = new GetPasswordQualityRequirementsExtendedRequest(this.targetType, this.targetDN, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_GET_PW_QUALITY_REQS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetPasswordQualityRequirementsExtendedRequest(targetType=");
        buffer.append(this.targetType.name());
        if (this.targetDN != null) {
            buffer.append(", targetDN='");
            buffer.append(this.targetDN);
            buffer.append('\'');
        }
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
