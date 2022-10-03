package com.unboundid.ldap.sdk.unboundidds.extensions;

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
public final class EndBatchedTransactionExtendedRequest extends ExtendedRequest
{
    public static final String END_BATCHED_TRANSACTION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.2";
    private static final long serialVersionUID = -8569129721687583552L;
    private final ASN1OctetString transactionID;
    private final boolean commit;
    
    public EndBatchedTransactionExtendedRequest(final ASN1OctetString transactionID, final boolean commit) {
        this(transactionID, commit, null);
    }
    
    public EndBatchedTransactionExtendedRequest(final ASN1OctetString transactionID, final boolean commit, final Control[] controls) {
        super("1.3.6.1.4.1.30221.2.6.2", encodeValue(transactionID, commit), controls);
        this.transactionID = transactionID;
        this.commit = commit;
    }
    
    public EndBatchedTransactionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
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
    
    public EndBatchedTransactionExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new EndBatchedTransactionExtendedResult(extendedResponse);
    }
    
    @Override
    public EndBatchedTransactionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public EndBatchedTransactionExtendedRequest duplicate(final Control[] controls) {
        final EndBatchedTransactionExtendedRequest r = new EndBatchedTransactionExtendedRequest(this.transactionID, this.commit, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_END_BATCHED_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EndBatchedTransactionExtendedRequest(transactionID='");
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
