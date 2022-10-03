package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class SearchListenerBridge<T> implements SearchResultListener
{
    private static final long serialVersionUID = 1939354785788059032L;
    private final LDAPPersister<T> persister;
    private final ObjectSearchListener<T> listener;
    
    SearchListenerBridge(final LDAPPersister<T> persister, final ObjectSearchListener<T> listener) {
        this.persister = persister;
        this.listener = listener;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        try {
            this.listener.objectReturned(this.persister.decode(searchEntry));
        }
        catch (final LDAPPersistException lpe) {
            Debug.debugException(lpe);
            this.listener.unparsableEntryReturned(searchEntry, lpe);
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        this.listener.searchReferenceReturned(searchReference);
    }
}
