package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ObjectSearchListener<T>
{
    void objectReturned(final T p0);
    
    void unparsableEntryReturned(final SearchResultEntry p0, final LDAPPersistException p1);
    
    void searchReferenceReturned(final SearchResultReference p0);
}
