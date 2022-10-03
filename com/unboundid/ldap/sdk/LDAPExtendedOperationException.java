package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPExtendedOperationException extends LDAPException
{
    private static final long serialVersionUID = -5674215690199642408L;
    private final ExtendedResult extendedResult;
    
    public LDAPExtendedOperationException(final ExtendedResult extendedResult) {
        super(extendedResult);
        this.extendedResult = extendedResult;
    }
    
    @Override
    public LDAPResult toLDAPResult() {
        return this.extendedResult;
    }
    
    public ExtendedResult getExtendedResult() {
        return this.extendedResult;
    }
    
    public String getResponseOID() {
        return this.extendedResult.getOID();
    }
    
    public ASN1OctetString getResponseValue() {
        return this.extendedResult.getValue();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        super.toString(buffer);
    }
    
    @Override
    public void toString(final StringBuilder buffer, final boolean includeCause, final boolean includeStackTrace) {
        buffer.append("LDAPException(resultCode=");
        buffer.append(this.getResultCode());
        final String errorMessage = this.getMessage();
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (errorMessage != null && !errorMessage.equals(diagnosticMessage)) {
            buffer.append(", errorMessage='");
            buffer.append(errorMessage);
            buffer.append('\'');
        }
        final String responseOID = this.getResponseOID();
        if (responseOID != null) {
            buffer.append(", responseOID='");
            buffer.append(responseOID);
            buffer.append('\'');
        }
        final String responseName = this.extendedResult.getExtendedResultName();
        if (responseName != null && !responseName.equals(responseOID)) {
            buffer.append(", responseName='");
            buffer.append(responseName);
            buffer.append('\'');
        }
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
        if (includeStackTrace) {
            buffer.append(", trace='");
            StaticUtils.getStackTrace(this.getStackTrace(), buffer);
            buffer.append('\'');
        }
        final String ldapSDKVersionString = ", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
        if (buffer.indexOf(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb") < 0) {
            buffer.append(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        }
        buffer.append(')');
    }
}
