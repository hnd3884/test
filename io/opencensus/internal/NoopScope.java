package io.opencensus.internal;

import io.opencensus.common.Scope;

public final class NoopScope implements Scope
{
    private static final Scope INSTANCE;
    
    private NoopScope() {
    }
    
    public static Scope getInstance() {
        return NoopScope.INSTANCE;
    }
    
    @Override
    public void close() {
    }
    
    static {
        INSTANCE = new NoopScope();
    }
}
