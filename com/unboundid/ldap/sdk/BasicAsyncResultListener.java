package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class BasicAsyncResultListener implements AsyncResultListener, Serializable
{
    private static final long serialVersionUID = -2701328904233458257L;
    private volatile LDAPResult ldapResult;
    
    public BasicAsyncResultListener() {
        this.ldapResult = null;
    }
    
    @InternalUseOnly
    @Override
    public void ldapResultReceived(final AsyncRequestID requestID, final LDAPResult ldapResult) {
        this.ldapResult = ldapResult;
    }
    
    public LDAPResult getLDAPResult() {
        return this.ldapResult;
    }
}
