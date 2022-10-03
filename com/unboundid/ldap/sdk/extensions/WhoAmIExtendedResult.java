package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class WhoAmIExtendedResult extends ExtendedResult
{
    private static final long serialVersionUID = 7466531316632846077L;
    private final String authorizationID;
    
    public WhoAmIExtendedResult(final ExtendedResult extendedResult) {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.authorizationID = null;
        }
        else {
            this.authorizationID = value.stringValue();
        }
    }
    
    public WhoAmIExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String authorizationID, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(authorizationID), responseControls);
        this.authorizationID = authorizationID;
    }
    
    private static ASN1OctetString encodeValue(final String authorizationID) {
        if (authorizationID == null) {
            return null;
        }
        return new ASN1OctetString(authorizationID);
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_WHO_AM_I.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("WhoAmIExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.authorizationID != null) {
            buffer.append(", authorizationID='");
            buffer.append(this.authorizationID);
            buffer.append('\'');
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
