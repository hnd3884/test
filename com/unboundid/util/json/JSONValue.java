package com.unboundid.util.json;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class JSONValue implements Serializable
{
    private static final long serialVersionUID = -4446120225858980451L;
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(final Object p0);
    
    public abstract boolean equals(final JSONValue p0, final boolean p1, final boolean p2, final boolean p3);
    
    @Override
    public abstract String toString();
    
    public abstract void toString(final StringBuilder p0);
    
    public abstract String toSingleLineString();
    
    public abstract void toSingleLineString(final StringBuilder p0);
    
    public abstract String toNormalizedString();
    
    public abstract void toNormalizedString(final StringBuilder p0);
    
    public abstract String toNormalizedString(final boolean p0, final boolean p1, final boolean p2);
    
    public abstract void toNormalizedString(final StringBuilder p0, final boolean p1, final boolean p2, final boolean p3);
    
    public abstract void appendToJSONBuffer(final JSONBuffer p0);
    
    public abstract void appendToJSONBuffer(final String p0, final JSONBuffer p1);
}
