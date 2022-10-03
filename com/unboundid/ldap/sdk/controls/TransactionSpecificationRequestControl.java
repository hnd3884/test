package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TransactionSpecificationRequestControl extends Control
{
    public static final String TRANSACTION_SPECIFICATION_REQUEST_OID = "1.3.6.1.1.21.2";
    private static final long serialVersionUID = 6489819774149849092L;
    private final ASN1OctetString transactionID;
    
    public TransactionSpecificationRequestControl(final ASN1OctetString transactionID) {
        super("1.3.6.1.1.21.2", true, new ASN1OctetString(transactionID.getValue()));
        Validator.ensureNotNull(transactionID);
        this.transactionID = transactionID;
    }
    
    public TransactionSpecificationRequestControl(final Control control) throws LDAPException {
        super(control);
        this.transactionID = control.getValue();
        if (this.transactionID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_REQUEST_CONTROL_NO_VALUE.get());
        }
    }
    
    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_TXN_SPECIFICATION_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("TransactionSpecificationRequestControl(transactionID='");
        buffer.append(this.transactionID.stringValue());
        buffer.append("')");
    }
}
