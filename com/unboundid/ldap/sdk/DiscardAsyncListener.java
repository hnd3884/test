package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class DiscardAsyncListener implements AsyncResultListener, AsyncCompareResultListener
{
    private static final DiscardAsyncListener INSTANCE;
    
    private DiscardAsyncListener() {
    }
    
    static DiscardAsyncListener getInstance() {
        return DiscardAsyncListener.INSTANCE;
    }
    
    @Override
    public void ldapResultReceived(final AsyncRequestID requestID, final LDAPResult ldapResult) {
    }
    
    @Override
    public void compareResultReceived(final AsyncRequestID requestID, final CompareResult compareResult) {
    }
    
    static {
        INSTANCE = new DiscardAsyncListener();
    }
}
