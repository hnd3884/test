package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class EndTransactionExtendedRequest extends ExtendedRequest
{
    public static final String END_TRANSACTION_REQUEST_OID = "1.3.6.1.1.21.3";
    private static final long serialVersionUID = -7135468264026410702L;
    private final ASN1OctetString transactionID;
    private final boolean commit;
    
    public EndTransactionExtendedRequest(final ASN1OctetString transactionID, final boolean commit, final Control... controls) {
        super("1.3.6.1.1.21.3", encodeValue(transactionID, commit), controls);
        this.transactionID = transactionID;
        this.commit = commit;
    }
    
    public EndTransactionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            if (elements.length == 1) {
                this.commit = true;
                this.transactionID = ASN1OctetString.decodeAsOctetString(elements[0]);
            }
            else {
                this.commit = ASN1Boolean.decodeAsBoolean(elements[0]).booleanValue();
                this.transactionID = ASN1OctetString.decodeAsOctetString(elements[1]);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_REQUEST_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString transactionID, final boolean commit) {
        Validator.ensureNotNull(transactionID);
        ASN1Element[] valueElements;
        if (commit) {
            valueElements = new ASN1Element[] { transactionID };
        }
        else {
            valueElements = new ASN1Element[] { new ASN1Boolean(commit), transactionID };
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }
    
    public boolean commit() {
        return this.commit;
    }
    
    public EndTransactionExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new EndTransactionExtendedResult(extendedResponse);
    }
    
    @Override
    public EndTransactionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public EndTransactionExtendedRequest duplicate(final Control[] controls) {
        final EndTransactionExtendedRequest r = new EndTransactionExtendedRequest(this.transactionID, this.commit, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_END_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EndTransactionExtendedRequest(transactionID='");
        buffer.append(this.transactionID.stringValue());
        buffer.append("', commit=");
        buffer.append(this.commit);
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append("controls={");
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
