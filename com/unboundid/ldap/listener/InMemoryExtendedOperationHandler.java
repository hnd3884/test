package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class InMemoryExtendedOperationHandler
{
    public abstract String getExtendedOperationHandlerName();
    
    public abstract List<String> getSupportedExtendedRequestOIDs();
    
    public abstract ExtendedResult processExtendedOperation(final InMemoryRequestHandler p0, final int p1, final ExtendedRequest p2);
    
    @Override
    public String toString() {
        return this.getExtendedOperationHandlerName();
    }
}
