package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.LDAPSDKException;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPException extends LDAPSDKException
{
    private static final long serialVersionUID = -4257171063946350327L;
    protected static final Control[] NO_CONTROLS;
    protected static final String[] NO_REFERRALS;
    private final Control[] responseControls;
    private final ResultCode resultCode;
    private final String[] referralURLs;
    private final String diagnosticMessage;
    private final String matchedDN;
    
    public LDAPException(final ResultCode resultCode) {
        super(resultCode.getName());
        this.resultCode = resultCode;
        this.matchedDN = null;
        this.diagnosticMessage = null;
        this.referralURLs = LDAPException.NO_REFERRALS;
        this.responseControls = LDAPException.NO_CONTROLS;
    }
    
    public LDAPException(final ResultCode resultCode, final Throwable cause) {
        super(resultCode.getName(), cause);
        this.resultCode = resultCode;
        this.matchedDN = null;
        this.diagnosticMessage = null;
        this.referralURLs = LDAPException.NO_REFERRALS;
        this.responseControls = LDAPException.NO_CONTROLS;
    }
    
    public LDAPException(final ResultCode resultCode, final String errorMessage) {
        super(errorMessage);
        this.resultCode = resultCode;
        this.matchedDN = null;
        this.diagnosticMessage = null;
        this.referralURLs = LDAPException.NO_REFERRALS;
        this.responseControls = LDAPException.NO_CONTROLS;
    }
    
    public LDAPException(final ResultCode resultCode, final String errorMessage, final Throwable cause) {
        super(errorMessage, cause);
        this.resultCode = resultCode;
        this.matchedDN = null;
        this.diagnosticMessage = null;
        this.referralURLs = LDAPException.NO_REFERRALS;
        this.responseControls = LDAPException.NO_CONTROLS;
    }
    
    public LDAPException(final ResultCode resultCode, final String errorMessage, final String matchedDN, final String[] referralURLs) {
        super(errorMessage);
        this.resultCode = resultCode;
        this.matchedDN = matchedDN;
        if (referralURLs == null) {
            this.referralURLs = LDAPException.NO_REFERRALS;
        }
        else {
            this.referralURLs = referralURLs;
        }
        this.diagnosticMessage = null;
        this.responseControls = LDAPException.NO_CONTROLS;
    }
    
    public LDAPException(final ResultCode resultCode, final String errorMessage, final String matchedDN, final String[] referralURLs, final Throwable cause) {
        super(errorMessage, cause);
        this.resultCode = resultCode;
        this.matchedDN = matchedDN;
        if (referralURLs == null) {
            this.referralURLs = LDAPException.NO_REFERRALS;
        }
        else {
            this.referralURLs = referralURLs;
        }
        this.diagnosticMessage = null;
        this.responseControls = LDAPException.NO_CONTROLS;
    }
    
    public LDAPException(final ResultCode resultCode, final String errorMessage, final String matchedDN, final String[] referralURLs, final Control[] controls) {
        super(errorMessage);
        this.resultCode = resultCode;
        this.matchedDN = matchedDN;
        this.diagnosticMessage = null;
        if (referralURLs == null) {
            this.referralURLs = LDAPException.NO_REFERRALS;
        }
        else {
            this.referralURLs = referralURLs;
        }
        if (controls == null) {
            this.responseControls = LDAPException.NO_CONTROLS;
        }
        else {
            this.responseControls = controls;
        }
    }
    
    public LDAPException(final ResultCode resultCode, final String errorMessage, final String matchedDN, final String[] referralURLs, final Control[] controls, final Throwable cause) {
        super(errorMessage, cause);
        this.resultCode = resultCode;
        this.matchedDN = matchedDN;
        this.diagnosticMessage = null;
        if (referralURLs == null) {
            this.referralURLs = LDAPException.NO_REFERRALS;
        }
        else {
            this.referralURLs = referralURLs;
        }
        if (controls == null) {
            this.responseControls = LDAPException.NO_CONTROLS;
        }
        else {
            this.responseControls = controls;
        }
    }
    
    public LDAPException(final LDAPResult ldapResult) {
        super((ldapResult.getDiagnosticMessage() == null) ? ldapResult.getResultCode().getName() : ldapResult.getDiagnosticMessage());
        this.resultCode = ldapResult.getResultCode();
        this.matchedDN = ldapResult.getMatchedDN();
        this.diagnosticMessage = ldapResult.getDiagnosticMessage();
        this.referralURLs = ldapResult.getReferralURLs();
        this.responseControls = ldapResult.getResponseControls();
    }
    
    public LDAPException(final LDAPResult ldapResult, final Throwable cause) {
        super((ldapResult.getDiagnosticMessage() == null) ? ldapResult.getResultCode().getName() : ldapResult.getDiagnosticMessage(), cause);
        this.resultCode = ldapResult.getResultCode();
        this.matchedDN = ldapResult.getMatchedDN();
        this.diagnosticMessage = ldapResult.getDiagnosticMessage();
        this.referralURLs = ldapResult.getReferralURLs();
        this.responseControls = ldapResult.getResponseControls();
    }
    
    public LDAPException(final LDAPException e) {
        super(e.getMessage(), e.getCause());
        this.resultCode = e.getResultCode();
        this.matchedDN = e.getMatchedDN();
        this.diagnosticMessage = e.getDiagnosticMessage();
        this.referralURLs = e.getReferralURLs();
        this.responseControls = e.getResponseControls();
    }
    
    public final ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public final String getMatchedDN() {
        return this.matchedDN;
    }
    
    public final String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public final String[] getReferralURLs() {
        return this.referralURLs;
    }
    
    public final boolean hasResponseControl() {
        return this.responseControls.length > 0;
    }
    
    public final boolean hasResponseControl(final String oid) {
        for (final Control c : this.responseControls) {
            if (c.getOID().equals(oid)) {
                return true;
            }
        }
        return false;
    }
    
    public final Control[] getResponseControls() {
        return this.responseControls;
    }
    
    public final Control getResponseControl(final String oid) {
        for (final Control c : this.responseControls) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        return null;
    }
    
    public LDAPResult toLDAPResult() {
        if (this.diagnosticMessage == null && this.getMessage() != null) {
            return new LDAPResult(-1, this.resultCode, this.getMessage(), this.matchedDN, this.referralURLs, this.responseControls);
        }
        return new LDAPResult(-1, this.resultCode, this.diagnosticMessage, this.matchedDN, this.referralURLs, this.responseControls);
    }
    
    public String getResultString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("result code='");
        buffer.append(this.resultCode);
        buffer.append('\'');
        if (this.diagnosticMessage != null && !this.diagnosticMessage.isEmpty()) {
            buffer.append(" diagnostic message='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        if (this.matchedDN != null && !this.matchedDN.isEmpty()) {
            buffer.append("  matched DN='");
            buffer.append(this.matchedDN);
            buffer.append('\'');
        }
        if (this.referralURLs != null && this.referralURLs.length > 0) {
            buffer.append("  referral URLs={");
            for (int i = 0; i < this.referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        final boolean includeCause = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeCauseInExceptionMessages");
        final boolean includeStackTrace = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeStackTraceInExceptionMessages");
        this.toString(buffer, includeCause, includeStackTrace);
    }
    
    public void toString(final StringBuilder buffer, final boolean includeCause, final boolean includeStackTrace) {
        buffer.append("LDAPException(resultCode=");
        buffer.append(this.resultCode);
        final String errorMessage = this.getMessage();
        if (errorMessage != null && !errorMessage.equals(this.diagnosticMessage)) {
            buffer.append(", errorMessage='");
            buffer.append(errorMessage);
            buffer.append('\'');
        }
        if (this.diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        if (this.matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(this.matchedDN);
            buffer.append('\'');
        }
        if (this.referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < this.referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        if (this.responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int i = 0; i < this.responseControls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.responseControls[i]);
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
        buffer.append(')');
    }
    
    @Override
    public final String getExceptionMessage() {
        return this.toString();
    }
    
    @Override
    public final String getExceptionMessage(final boolean includeCause, final boolean includeStackTrace) {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer, includeCause, includeStackTrace);
        return buffer.toString();
    }
    
    static {
        NO_CONTROLS = StaticUtils.NO_CONTROLS;
        NO_REFERRALS = StaticUtils.NO_STRINGS;
    }
}
