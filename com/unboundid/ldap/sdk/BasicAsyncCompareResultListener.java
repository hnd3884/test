package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class BasicAsyncCompareResultListener implements AsyncCompareResultListener, Serializable
{
    private static final long serialVersionUID = 8119461093491566432L;
    private volatile CompareResult compareResult;
    
    public BasicAsyncCompareResultListener() {
        this.compareResult = null;
    }
    
    @InternalUseOnly
    @Override
    public void compareResultReceived(final AsyncRequestID requestID, final CompareResult compareResult) {
        this.compareResult = compareResult;
    }
    
    public CompareResult getCompareResult() {
        return this.compareResult;
    }
}
