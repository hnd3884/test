package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.util.StaticUtils;
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
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EndInteractiveTransactionExtendedRequest extends ExtendedRequest
{
    public static final String END_INTERACTIVE_TRANSACTION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.4";
    private static final byte TYPE_TXN_ID = Byte.MIN_VALUE;
    private static final byte TYPE_COMMIT = -127;
    private static final long serialVersionUID = -7404929482337917353L;
    private final ASN1OctetString transactionID;
    private final boolean commit;
    
    public EndInteractiveTransactionExtendedRequest(final ASN1OctetString transactionID, final boolean commit) {
        this(transactionID, commit, null);
    }
    
    public EndInteractiveTransactionExtendedRequest(final ASN1OctetString transactionID, final boolean commit, final Control[] controls) {
        super("1.3.6.1.4.1.30221.2.6.4", encodeValue(transactionID, commit), controls);
        this.transactionID = transactionID;
        this.commit = commit;
    }
    
    public EndInteractiveTransactionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_INT_TXN_REQUEST_NO_VALUE.get());
        }
        ASN1OctetString txnID = null;
        boolean shouldCommit = true;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(valueElement).elements();
            for (final ASN1Element e : arr$) {
                if (e.getType() == -128) {
                    txnID = ASN1OctetString.decodeAsOctetString(e);
                }
                else {
                    if (e.getType() != -127) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_INT_TXN_REQUEST_INVALID_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                    shouldCommit = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_INT_TXN_REQUEST_CANNOT_DECODE.get(e2), e2);
        }
        if (txnID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_INT_TXN_REQUEST_NO_TXN_ID.get());
        }
        this.transactionID = txnID;
        this.commit = shouldCommit;
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString transactionID, final boolean commit) {
        Validator.ensureNotNull(transactionID);
        ASN1Element[] valueElements;
        if (commit) {
            valueElements = new ASN1Element[] { new ASN1OctetString((byte)(-128), transactionID.getValue()) };
        }
        else {
            valueElements = new ASN1Element[] { new ASN1OctetString((byte)(-128), transactionID.getValue()), new ASN1Boolean((byte)(-127), commit) };
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }
    
    public boolean commit() {
        return this.commit;
    }
    
    @Override
    public EndInteractiveTransactionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public EndInteractiveTransactionExtendedRequest duplicate(final Control[] controls) {
        final EndInteractiveTransactionExtendedRequest r = new EndInteractiveTransactionExtendedRequest(this.transactionID, this.commit, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_END_INTERACTIVE_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EndInteractiveTransactionExtendedRequest(transactionID='");
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
