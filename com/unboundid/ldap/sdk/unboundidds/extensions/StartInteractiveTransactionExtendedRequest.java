package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class StartInteractiveTransactionExtendedRequest extends ExtendedRequest
{
    public static final String START_INTERACTIVE_TRANSACTION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.3";
    private static final byte TYPE_BASE_DN = Byte.MIN_VALUE;
    private static final long serialVersionUID = 4475028061132753546L;
    private final String baseDN;
    
    public StartInteractiveTransactionExtendedRequest() {
        super("1.3.6.1.4.1.30221.2.6.3");
        this.baseDN = null;
    }
    
    public StartInteractiveTransactionExtendedRequest(final String baseDN) {
        super("1.3.6.1.4.1.30221.2.6.3", encodeValue(baseDN));
        this.baseDN = baseDN;
    }
    
    public StartInteractiveTransactionExtendedRequest(final String baseDN, final Control[] controls) {
        super("1.3.6.1.4.1.30221.2.6.3", encodeValue(baseDN), controls);
        this.baseDN = baseDN;
    }
    
    public StartInteractiveTransactionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        if (!extendedRequest.hasValue()) {
            this.baseDN = null;
            return;
        }
        String baseDNStr = null;
        try {
            final ASN1Element valueElement = ASN1Element.decode(extendedRequest.getValue().getValue());
            final ASN1Sequence valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
            for (final ASN1Element e : valueSequence.elements()) {
                if (e.getType() != -128) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_INT_TXN_REQUEST_INVALID_ELEMENT.get(StaticUtils.toHex(e.getType())));
                }
                baseDNStr = ASN1OctetString.decodeAsOctetString(e).stringValue();
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_INT_TXN_REQUEST_VALUE_NOT_SEQUENCE.get(e2.getMessage()), e2);
        }
        this.baseDN = baseDNStr;
    }
    
    private static ASN1OctetString encodeValue(final String baseDN) {
        if (baseDN == null) {
            return null;
        }
        final ASN1Element[] elements = { new ASN1OctetString((byte)(-128), baseDN) };
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public StartInteractiveTransactionExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new StartInteractiveTransactionExtendedResult(extendedResponse);
    }
    
    @Override
    public StartInteractiveTransactionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public StartInteractiveTransactionExtendedRequest duplicate(final Control[] controls) {
        final StartInteractiveTransactionExtendedRequest r = new StartInteractiveTransactionExtendedRequest(this.baseDN, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_START_INTERACTIVE_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StartInteractiveTransactionExtendedRequest(");
        if (this.baseDN != null) {
            buffer.append("baseDN='");
            buffer.append(this.baseDN);
            buffer.append('\'');
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            if (this.baseDN != null) {
                buffer.append(", ");
            }
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
