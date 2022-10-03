package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class BatchedTransactionSpecificationRequestControl extends Control
{
    public static final String BATCHED_TRANSACTION_SPECIFICATION_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.1";
    private static final long serialVersionUID = -6817702055586260189L;
    private final ASN1OctetString transactionID;
    
    public BatchedTransactionSpecificationRequestControl(final ASN1OctetString transactionID) {
        super("1.3.6.1.4.1.30221.2.5.1", true, new ASN1OctetString(transactionID.getValue()));
        this.transactionID = transactionID;
    }
    
    public BatchedTransactionSpecificationRequestControl(final Control control) throws LDAPException {
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
        return ControlMessages.INFO_CONTROL_NAME_BATCHED_TXN_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BatchedTransactionSpecificationRequestControl(transactionID='");
        buffer.append(this.transactionID.stringValue());
        buffer.append("')");
    }
}
