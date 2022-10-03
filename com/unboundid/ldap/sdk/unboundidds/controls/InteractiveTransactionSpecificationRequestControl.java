package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InteractiveTransactionSpecificationRequestControl extends Control
{
    public static final String INTERACTIVE_TRANSACTION_SPECIFICATION_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.4";
    private static final byte TYPE_TXN_ID = Byte.MIN_VALUE;
    private static final byte TYPE_ABORT_ON_FAILURE = -127;
    private static final byte TYPE_WRITE_LOCK = -126;
    private static final long serialVersionUID = -6473934815135786621L;
    private final ASN1OctetString transactionID;
    private final boolean abortOnFailure;
    private final boolean writeLock;
    
    public InteractiveTransactionSpecificationRequestControl(final ASN1OctetString transactionID) {
        this(transactionID, false, true);
    }
    
    public InteractiveTransactionSpecificationRequestControl(final ASN1OctetString transactionID, final boolean abortOnFailure, final boolean writeLock) {
        super("1.3.6.1.4.1.30221.2.5.4", true, encodeValue(transactionID, abortOnFailure, writeLock));
        this.transactionID = transactionID;
        this.abortOnFailure = abortOnFailure;
        this.writeLock = writeLock;
    }
    
    public InteractiveTransactionSpecificationRequestControl(final Control control) throws LDAPException {
        super(control);
        if (!control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_REQUEST_NO_VALUE.get());
        }
        ASN1Element[] elements;
        try {
            final ASN1Element e = ASN1Element.decode(control.getValue().getValue());
            elements = ASN1Sequence.decodeAsSequence(e).elements();
        }
        catch (final Exception e2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_REQUEST_VALUE_NOT_SEQUENCE.get(e2.getMessage()), e2);
        }
        ASN1OctetString txnID = null;
        boolean shouldAbortOnFailure = false;
        boolean shouldWriteLock = true;
        for (final ASN1Element element : elements) {
            switch (element.getType()) {
                case Byte.MIN_VALUE: {
                    txnID = ASN1OctetString.decodeAsOctetString(element);
                    break;
                }
                case -127: {
                    try {
                        shouldAbortOnFailure = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                        break;
                    }
                    catch (final Exception e3) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_REQUEST_ABORT_ON_FAILURE_NOT_BOOLEAN.get(e3.getMessage()), e3);
                    }
                }
                case -126: {
                    try {
                        shouldWriteLock = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                        break;
                    }
                    catch (final Exception e3) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_REQUEST_WRITE_LOCK_NOT_BOOLEAN.get(e3.getMessage()), e3);
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_REQUEST_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(element.getType())));
                }
            }
        }
        if (txnID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_INT_TXN_REQUEST_NO_TXN_ID.get());
        }
        this.transactionID = txnID;
        this.abortOnFailure = shouldAbortOnFailure;
        this.writeLock = shouldWriteLock;
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString transactionID, final boolean abortOnFailure, final boolean writeLock) {
        Validator.ensureNotNull(transactionID);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString((byte)(-128), transactionID.getValue()));
        if (abortOnFailure) {
            elements.add(new ASN1Boolean((byte)(-127), abortOnFailure));
        }
        if (!writeLock) {
            elements.add(new ASN1Boolean((byte)(-126), writeLock));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }
    
    public boolean abortOnFailure() {
        return this.abortOnFailure;
    }
    
    public boolean writeLock() {
        return this.writeLock;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_INTERACTIVE_TXN_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InteractiveTransactionSpecificationRequestControl(transactionID='");
        buffer.append(this.transactionID.stringValue());
        buffer.append("', abortOnFailure=");
        buffer.append(this.abortOnFailure);
        buffer.append(", writeLock=");
        buffer.append(this.writeLock);
        buffer.append(')');
    }
}
