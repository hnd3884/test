package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AggregatePostConnectProcessor implements PostConnectProcessor
{
    private final List<PostConnectProcessor> processors;
    
    public AggregatePostConnectProcessor(final PostConnectProcessor... processors) {
        this(StaticUtils.toList(processors));
    }
    
    public AggregatePostConnectProcessor(final Collection<? extends PostConnectProcessor> processors) {
        if (processors == null) {
            this.processors = Collections.emptyList();
        }
        else {
            this.processors = Collections.unmodifiableList((List<? extends PostConnectProcessor>)new ArrayList<PostConnectProcessor>(processors));
        }
    }
    
    @Override
    public void processPreAuthenticatedConnection(final LDAPConnection connection) throws LDAPException {
        for (final PostConnectProcessor p : this.processors) {
            p.processPreAuthenticatedConnection(connection);
        }
    }
    
    @Override
    public void processPostAuthenticatedConnection(final LDAPConnection connection) throws LDAPException {
        for (final PostConnectProcessor p : this.processors) {
            p.processPostAuthenticatedConnection(connection);
        }
    }
}
