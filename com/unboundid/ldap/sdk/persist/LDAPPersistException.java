package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.LDAPException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPPersistException extends LDAPException
{
    private static final long serialVersionUID = 8625904586803506713L;
    private final Object partiallyDecodedObject;
    
    public LDAPPersistException(final LDAPException e) {
        super(e);
        this.partiallyDecodedObject = null;
    }
    
    public LDAPPersistException(final String message) {
        super(ResultCode.LOCAL_ERROR, message);
        this.partiallyDecodedObject = null;
    }
    
    public LDAPPersistException(final String message, final Throwable cause) {
        super(ResultCode.LOCAL_ERROR, message, cause);
        this.partiallyDecodedObject = null;
    }
    
    public LDAPPersistException(final String message, final Object partiallyDecodedObject, final Throwable cause) {
        super(ResultCode.LOCAL_ERROR, message, cause);
        this.partiallyDecodedObject = partiallyDecodedObject;
    }
    
    public Object getPartiallyDecodedObject() {
        return this.partiallyDecodedObject;
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
        if (this.partiallyDecodedObject != null) {
            buffer.append(", partiallyDecodedObject=");
            buffer.append(this.partiallyDecodedObject);
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
        if (includeCause || includeStackTrace) {
            final Throwable cause = this.getCause();
            if (cause != null) {
                buffer.append(", cause=");
                buffer.append(StaticUtils.getExceptionMessage(cause, true, includeStackTrace));
            }
        }
        final String ldapSDKVersionString = ", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
        if (buffer.indexOf(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb") < 0) {
            buffer.append(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        }
        buffer.append("')");
    }
}
