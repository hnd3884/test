package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface SearchResultListener extends Serializable
{
    void searchEntryReturned(final SearchResultEntry p0);
    
    void searchReferenceReturned(final SearchResultReference p0);
}
