package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface AsyncSearchResultListener extends SearchResultListener
{
    void searchResultReceived(final AsyncRequestID p0, final SearchResult p1);
}
