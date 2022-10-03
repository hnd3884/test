package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InteractiveTransactionSpecificationResponseControl extends Control implements DecodeableControl
{
    public static final String INTERACTIVE_TRANSACTION_SPECIFICATION_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.4";
    private static final byte TYPE_TXN_VALID = Byte.MIN_VALUE;
    private static final byte TYPE_BASE_DNS = -95;
    private static final long serialVersionUID = -4323085263241417543L;
    private final boolean transactionValid;
    private final List<String> baseDNs;
    
    InteractiveTransactionSpecificationResponseControl() {
        this.transactionValid = false;
        this.baseDNs = null;
    }
    
    public InteractiveTransactionSpecificationResponseControl(final boolean transactionValid, final List<String> baseDNs) {
        super("1.3.6.1.4.1.30221.2.5.4", false, encodeValue(transactionValid, baseDNs));
        this.transactionValid = transactionValid;
        if (baseDNs == null) {
            this.baseDNs = null;
        }
        else {
            this.baseDNs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(baseDNs));
        }
    }
    
    public InteractiveTransactionSpecificationResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_RESPONSE_NO_VALUE.get());
        }
        ASN1Element[] elements;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
        }
        catch (final Exception e) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_RESPONSE_VALUE_NOT_SEQUENCE.get(e.getMessage()), e);
        }
        Boolean isValid = null;
        List<String> baseDNList = null;
        for (final ASN1Element element : elements) {
            switch (element.getType()) {
                case Byte.MIN_VALUE: {
                    try {
                        isValid = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                        break;
                    }
                    catch (final Exception e2) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_RESPONSE_TXN_VALID_NOT_BOOLEAN.get(e2.getMessage()), e2);
                    }
                }
                case -95: {
                    try {
                        final ASN1Sequence s = ASN1Sequence.decodeAsSequence(element);
                        baseDNList = new ArrayList<String>(s.elements().length);
                        for (final ASN1Element e3 : s.elements()) {
                            baseDNList.add(ASN1OctetString.decodeAsOctetString(e3).stringValue());
                        }
                        break;
                    }
                    catch (final Exception e2) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_RESPONSE_BASE_DNS_NOT_SEQUENCE.get(e2.getMessage()), e2);
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_RESPONSE_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(element.getType())));
                }
            }
        }
        if (isValid == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_RESPONSE_NO_TXN_VALID.get());
        }
        this.transactionValid = isValid;
        if (baseDNList == null) {
            this.baseDNs = null;
        }
        else {
            this.baseDNs = Collections.unmodifiableList((List<? extends String>)baseDNList);
        }
    }
    
    private static ASN1OctetString encodeValue(final boolean transactionValid, final List<String> baseDNs) {
        ASN1Element[] elements;
        if (baseDNs == null) {
            elements = new ASN1Element[] { new ASN1Boolean((byte)(-128), transactionValid) };
        }
        else {
            final ASN1Element[] baseDNElements = new ASN1Element[baseDNs.size()];
            for (int i = 0; i < baseDNElements.length; ++i) {
                baseDNElements[i] = new ASN1OctetString(baseDNs.get(i));
            }
            elements = new ASN1Element[] { new ASN1Boolean((byte)(-128), transactionValid), new ASN1Sequence((byte)(-95), baseDNElements) };
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    @Override
    public InteractiveTransactionSpecificationResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new InteractiveTransactionSpecificationResponseControl(oid, isCritical, value);
    }
    
    public static InteractiveTransactionSpecificationResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.4");
        if (c == null) {
            return null;
        }
        if (c instanceof InteractiveTransactionSpecificationResponseControl) {
            return (InteractiveTransactionSpecificationResponseControl)c;
        }
        return new InteractiveTransactionSpecificationResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public boolean transactionValid() {
        return this.transactionValid;
    }
    
    public List<String> getBaseDNs() {
        return this.baseDNs;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_INTERACTIVE_TXN_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InteractiveTransactionSpecificationResponseControl(");
        buffer.append("transactionValid=");
        buffer.append(this.transactionValid);
        buffer.append(", baseDNs=");
        if (this.baseDNs == null) {
            buffer.append("null");
        }
        else {
            buffer.append('{');
            for (int i = 0; i < this.baseDNs.size(); ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.baseDNs.get(i));
                buffer.append('\'');
            }
            buffer.append('}');
        }
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
