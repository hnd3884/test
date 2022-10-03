package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.LDAPEntrySource;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.EntrySource;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PersistedObjects<T> implements Serializable, Closeable
{
    private static final long serialVersionUID = 7430494946944736169L;
    private final EntrySource entrySource;
    private final LDAPPersister<T> persister;
    
    PersistedObjects(final LDAPPersister<T> persister, final EntrySource entrySource) {
        this.persister = persister;
        this.entrySource = entrySource;
    }
    
    public T next() throws LDAPPersistException {
        Entry entry;
        try {
            entry = this.entrySource.nextEntry();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final Throwable cause = e.getCause();
            if (cause != null && cause instanceof LDAPException) {
                throw new LDAPPersistException((LDAPException)cause);
            }
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_SEARCH_RESULTS_ENTRY_SOURCE_EXCEPTION.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (entry == null) {
            return null;
        }
        return this.persister.decode(entry);
    }
    
    @Override
    public void close() {
        this.entrySource.close();
    }
    
    public SearchResult getSearchResult() {
        if (this.entrySource instanceof LDAPEntrySource) {
            return ((LDAPEntrySource)this.entrySource).getSearchResult();
        }
        return null;
    }
}
