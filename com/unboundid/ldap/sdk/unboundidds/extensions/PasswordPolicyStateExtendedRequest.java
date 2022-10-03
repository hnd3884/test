package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PasswordPolicyStateExtendedRequest extends ExtendedRequest
{
    public static final String PASSWORD_POLICY_STATE_REQUEST_OID = "1.3.6.1.4.1.30221.1.6.1";
    private static final long serialVersionUID = -1644137695182620213L;
    private final PasswordPolicyStateOperation[] operations;
    private final String userDN;
    
    public PasswordPolicyStateExtendedRequest(final String userDN, final PasswordPolicyStateOperation... operations) {
        this(userDN, (Control[])null, operations);
    }
    
    public PasswordPolicyStateExtendedRequest(final String userDN, final Control[] controls, final PasswordPolicyStateOperation... operations) {
        super("1.3.6.1.4.1.30221.1.6.1", encodeValue(userDN, operations), controls);
        this.userDN = userDN;
        this.operations = operations;
    }
    
    public PasswordPolicyStateExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_REQUEST_NO_VALUE.get());
        }
        ASN1Element[] elements;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_REQUEST_VALUE_NOT_SEQUENCE.get(e), e);
        }
        if (elements.length < 1 || elements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_REQUEST_INVALID_ELEMENT_COUNT.get(elements.length));
        }
        this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        if (elements.length == 1) {
            this.operations = new PasswordPolicyStateOperation[0];
        }
        else {
            try {
                final ASN1Element[] opElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                this.operations = new PasswordPolicyStateOperation[opElements.length];
                for (int i = 0; i < opElements.length; ++i) {
                    this.operations[i] = PasswordPolicyStateOperation.decode(opElements[i]);
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_REQUEST_CANNOT_DECODE_OPS.get(e), e);
            }
        }
    }
    
    private static ASN1OctetString encodeValue(final String userDN, final PasswordPolicyStateOperation[] operations) {
        ASN1Element[] elements;
        if (operations == null || operations.length == 0) {
            elements = new ASN1Element[] { new ASN1OctetString(userDN) };
        }
        else {
            final ASN1Element[] opElements = new ASN1Element[operations.length];
            for (int i = 0; i < operations.length; ++i) {
                opElements[i] = operations[i].encode();
            }
            elements = new ASN1Element[] { new ASN1OctetString(userDN), new ASN1Sequence(opElements) };
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getUserDN() {
        return this.userDN;
    }
    
    public PasswordPolicyStateOperation[] getOperations() {
        return this.operations;
    }
    
    public PasswordPolicyStateExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new PasswordPolicyStateExtendedResult(extendedResponse);
    }
    
    @Override
    public PasswordPolicyStateExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public PasswordPolicyStateExtendedRequest duplicate(final Control[] controls) {
        final PasswordPolicyStateExtendedRequest r = new PasswordPolicyStateExtendedRequest(this.userDN, controls, this.operations);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_PW_POLICY_STATE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordPolicyStateExtendedRequest(userDN='");
        buffer.append(this.userDN);
        if (this.operations.length > 0) {
            buffer.append("', operations={");
            for (int i = 0; i < this.operations.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                this.operations[i].toString(buffer);
            }
            buffer.append('}');
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int j = 0; j < controls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
