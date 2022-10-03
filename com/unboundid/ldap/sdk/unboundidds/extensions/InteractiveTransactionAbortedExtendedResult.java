package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InteractiveTransactionAbortedExtendedResult extends ExtendedResult
{
    public static final String INTERACTIVE_TRANSACTION_ABORTED_RESULT_OID = "1.3.6.1.4.1.30221.2.6.5";
    private static final long serialVersionUID = 296814913448182605L;
    
    public InteractiveTransactionAbortedExtendedResult(final ExtendedResult extendedResult) {
        super(extendedResult);
    }
    
    public InteractiveTransactionAbortedExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, "1.3.6.1.4.1.30221.2.6.5", null, responseControls);
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_INTERACTIVE_TXN_ABORTED.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InteractiveTransactionAbortedExtendedResult(resultCode=");
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
        buffer.append("1.3.6.1.4.1.30221.2.6.5");
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
