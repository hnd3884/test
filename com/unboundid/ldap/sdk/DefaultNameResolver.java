package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DefaultNameResolver extends NameResolver
{
    private static final DefaultNameResolver INSTANCE;
    
    private DefaultNameResolver() {
    }
    
    public static DefaultNameResolver getInstance() {
        return DefaultNameResolver.INSTANCE;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DefaultNameResolver()");
    }
    
    static {
        INSTANCE = new DefaultNameResolver();
    }
}
