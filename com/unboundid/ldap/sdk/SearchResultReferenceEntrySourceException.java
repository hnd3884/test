package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchResultReferenceEntrySourceException extends EntrySourceException
{
    private static final long serialVersionUID = 4389660042011914324L;
    private final SearchResultReference searchReference;
    
    public SearchResultReferenceEntrySourceException(final SearchResultReference searchReference) {
        super(true, new LDAPException(ResultCode.REFERRAL, null, null, searchReference.getReferralURLs(), searchReference.getControls(), null));
        this.searchReference = searchReference;
    }
    
    public SearchResultReference getSearchReference() {
        return this.searchReference;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SearchResultReferenceEntrySourceException(searchReference=");
        this.searchReference.toString(buffer);
        buffer.append("')");
    }
}
