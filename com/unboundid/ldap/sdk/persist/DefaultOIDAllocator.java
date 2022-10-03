package com.unboundid.ldap.sdk.persist;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DefaultOIDAllocator extends OIDAllocator
{
    private static final DefaultOIDAllocator INSTANCE;
    private static final long serialVersionUID = 4815405566303309719L;
    
    private DefaultOIDAllocator() {
    }
    
    public static DefaultOIDAllocator getInstance() {
        return DefaultOIDAllocator.INSTANCE;
    }
    
    @Override
    public String allocateAttributeTypeOID(final String name) {
        return StaticUtils.toLowerCase(name) + "-oid";
    }
    
    @Override
    public String allocateObjectClassOID(final String name) {
        return StaticUtils.toLowerCase(name) + "-oid";
    }
    
    static {
        INSTANCE = new DefaultOIDAllocator();
    }
}
