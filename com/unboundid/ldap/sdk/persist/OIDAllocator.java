package com.unboundid.ldap.sdk.persist;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class OIDAllocator implements Serializable
{
    private static final long serialVersionUID = -2031217984148568974L;
    
    public abstract String allocateAttributeTypeOID(final String p0);
    
    public abstract String allocateObjectClassOID(final String p0);
}
