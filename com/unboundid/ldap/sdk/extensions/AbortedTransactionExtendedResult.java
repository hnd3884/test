package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AbortedTransactionExtendedResult extends ExtendedResult
{
    public static final String ABORTED_TRANSACTION_RESULT_OID = "1.3.6.1.1.21.4";
    private static final long serialVersionUID = 7521522597566232465L;
    private final ASN1OctetString transactionID;
    
    public AbortedTransactionExtendedResult(final ASN1OctetString transactionID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Control[] controls) {
        super(0, resultCode, diagnosticMessage, matchedDN, referralURLs, "1.3.6.1.1.21.4", transactionID, controls);
        Validator.ensureNotNull(transactionID, resultCode);
        this.transactionID = transactionID;
    }
    
    public AbortedTransactionExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        this.transactionID = extendedResult.getValue();
        if (this.transactionID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_ABORTED_TXN_NO_VALUE.get());
        }
    }
    
    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_ABORTED_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AbortedTransactionExtendedResult(transactionID='");
        buffer.append(this.transactionID.stringValue());
        buffer.append("', resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(diagnosticMessage);
            buffer.append('\'');
        }
        final String matchedDN = this.getMatchedDN();
        if (matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(matchedDN);
            buffer.append('\'');
        }
        final String[] referralURLs = this.getReferralURLs();
        if (referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        buffer.append(", oid=");
        buffer.append("1.3.6.1.1.21.4");
        final Control[] responseControls = this.getResponseControls();
        if (responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int j = 0; j < responseControls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(responseControls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
