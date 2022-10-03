package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.Collections;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchResult extends LDAPResult
{
    private static final long serialVersionUID = 1938208530894131198L;
    private int numEntries;
    private int numReferences;
    private List<SearchResultEntry> searchEntries;
    private List<SearchResultReference> searchReferences;
    
    public SearchResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final int numEntries, final int numReferences, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
        this.numEntries = numEntries;
        this.numReferences = numReferences;
        this.searchEntries = null;
        this.searchReferences = null;
    }
    
    public SearchResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final List<SearchResultEntry> searchEntries, final List<SearchResultReference> searchReferences, final int numEntries, final int numReferences, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
        this.numEntries = numEntries;
        this.numReferences = numReferences;
        this.searchEntries = searchEntries;
        this.searchReferences = searchReferences;
    }
    
    public SearchResult(final LDAPResult ldapResult) {
        super(ldapResult);
        if (ldapResult instanceof SearchResult) {
            final SearchResult searchResult = (SearchResult)ldapResult;
            this.numEntries = searchResult.numEntries;
            this.numReferences = searchResult.numReferences;
            this.searchEntries = searchResult.searchEntries;
            this.searchReferences = searchResult.searchReferences;
        }
        else {
            this.numEntries = -1;
            this.numReferences = -1;
            this.searchEntries = null;
            this.searchReferences = null;
        }
    }
    
    public SearchResult(final LDAPException ldapException) {
        this(ldapException.toLDAPResult());
    }
    
    static SearchResult readSearchResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        final LDAPResult r = LDAPResult.readLDAPResultFrom(messageID, messageSequence, reader);
        return new SearchResult(messageID, r.getResultCode(), r.getDiagnosticMessage(), r.getMatchedDN(), r.getReferralURLs(), -1, -1, r.getResponseControls());
    }
    
    public int getEntryCount() {
        return this.numEntries;
    }
    
    public int getReferenceCount() {
        return this.numReferences;
    }
    
    public List<SearchResultEntry> getSearchEntries() {
        if (this.searchEntries == null) {
            return null;
        }
        return Collections.unmodifiableList((List<? extends SearchResultEntry>)this.searchEntries);
    }
    
    public SearchResultEntry getSearchEntry(final String dn) throws LDAPException {
        if (this.searchEntries == null) {
            return null;
        }
        final DN parsedDN = new DN(dn);
        for (final SearchResultEntry e : this.searchEntries) {
            if (parsedDN.equals(e.getParsedDN())) {
                return e;
            }
        }
        return null;
    }
    
    public List<SearchResultReference> getSearchReferences() {
        if (this.searchReferences == null) {
            return null;
        }
        return Collections.unmodifiableList((List<? extends SearchResultReference>)this.searchReferences);
    }
    
    void setCounts(final int numEntries, final List<SearchResultEntry> searchEntries, final int numReferences, final List<SearchResultReference> searchReferences) {
        this.numEntries = numEntries;
        this.numReferences = numReferences;
        if (searchEntries == null) {
            this.searchEntries = null;
        }
        else {
            this.searchEntries = Collections.unmodifiableList((List<? extends SearchResultEntry>)searchEntries);
        }
        if (searchReferences == null) {
            this.searchReferences = null;
        }
        else {
            this.searchReferences = Collections.unmodifiableList((List<? extends SearchResultReference>)searchReferences);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SearchResult(resultCode=");
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
        if (this.numEntries >= 0) {
            buffer.append(", entriesReturned=");
            buffer.append(this.numEntries);
        }
        if (this.numReferences >= 0) {
            buffer.append(", referencesReturned=");
            buffer.append(this.numReferences);
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
