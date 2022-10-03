package com.unboundid.util;

import com.unboundid.ldap.sdk.ResultCode;
import java.util.Arrays;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.SortedSet;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.ldap.sdk.SearchResultListener;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SubtreeDeleterSearchResultListener implements SearchResultListener
{
    private static final long serialVersionUID = -6828026542462924962L;
    private final AtomicReference<LDAPException> firstException;
    private final DN searchBaseDN;
    private final Filter searchFilter;
    private final SortedSet<DN> dnSet;
    
    SubtreeDeleterSearchResultListener(final DN searchBaseDN, final Filter searchFilter, final SortedSet<DN> dnSet) {
        this.searchBaseDN = searchBaseDN;
        this.searchFilter = searchFilter;
        this.dnSet = dnSet;
        this.firstException = new AtomicReference<LDAPException>();
    }
    
    LDAPException getFirstException() {
        return this.firstException.get();
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        try {
            this.dnSet.add(searchEntry.getParsedDN());
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            this.firstException.compareAndSet(null, e);
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        if (this.firstException.get() == null) {
            final String[] referralURLs = searchReference.getReferralURLs();
            String urlsString;
            if (referralURLs.length == 1) {
                urlsString = referralURLs[0];
            }
            else {
                urlsString = Arrays.toString(referralURLs);
            }
            this.firstException.compareAndSet(null, new LDAPException(ResultCode.REFERRAL, UtilityMessages.ERR_SUBTREE_DELETER_SEARCH_LISTENER_REFERENCE_RETURNED.get(urlsString, String.valueOf(this.searchBaseDN), String.valueOf(this.searchFilter))));
        }
    }
}
