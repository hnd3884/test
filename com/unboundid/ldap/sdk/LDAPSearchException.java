package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPSearchException extends LDAPException
{
    private static final long serialVersionUID = 350230437196125113L;
    private final SearchResult searchResult;
    
    public LDAPSearchException(final ResultCode resultCode, final String errorMessage) {
        super(resultCode, errorMessage);
        this.searchResult = new SearchResult(-1, resultCode, errorMessage, null, StaticUtils.NO_STRINGS, 0, 0, StaticUtils.NO_CONTROLS);
    }
    
    public LDAPSearchException(final ResultCode resultCode, final String errorMessage, final Throwable cause) {
        super(resultCode, errorMessage, cause);
        this.searchResult = new SearchResult(-1, resultCode, errorMessage, null, StaticUtils.NO_STRINGS, 0, 0, StaticUtils.NO_CONTROLS);
    }
    
    public LDAPSearchException(final LDAPException ldapException) {
        super(ldapException.getResultCode(), ldapException.getMessage(), ldapException.getMatchedDN(), ldapException.getReferralURLs(), ldapException.getResponseControls(), ldapException);
        if (ldapException instanceof LDAPSearchException) {
            final LDAPSearchException lse = (LDAPSearchException)ldapException;
            this.searchResult = lse.searchResult;
        }
        else {
            this.searchResult = new SearchResult(-1, ldapException.getResultCode(), ldapException.getMessage(), ldapException.getMatchedDN(), ldapException.getReferralURLs(), 0, 0, ldapException.getResponseControls());
        }
    }
    
    public LDAPSearchException(final SearchResult searchResult) {
        super(searchResult);
        this.searchResult = searchResult;
    }
    
    public SearchResult getSearchResult() {
        return this.searchResult;
    }
    
    public int getEntryCount() {
        return this.searchResult.getEntryCount();
    }
    
    public int getReferenceCount() {
        return this.searchResult.getReferenceCount();
    }
    
    public List<SearchResultEntry> getSearchEntries() {
        return this.searchResult.getSearchEntries();
    }
    
    public List<SearchResultReference> getSearchReferences() {
        return this.searchResult.getSearchReferences();
    }
    
    @Override
    public SearchResult toLDAPResult() {
        return this.searchResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        super.toString(buffer);
    }
    
    @Override
    public void toString(final StringBuilder buffer, final boolean includeCause, final boolean includeStackTrace) {
        buffer.append("LDAPException(resultCode=");
        buffer.append(this.getResultCode());
        buffer.append(", numEntries=");
        buffer.append(this.searchResult.getEntryCount());
        buffer.append(", numReferences=");
        buffer.append(this.searchResult.getReferenceCount());
        final String errorMessage = this.getMessage();
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (errorMessage != null && !errorMessage.equals(diagnosticMessage)) {
            buffer.append(", errorMessage='");
            buffer.append(errorMessage);
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
