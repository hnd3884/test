package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.util.HashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DereferencePolicy implements Serializable
{
    public static final DereferencePolicy NEVER;
    public static final DereferencePolicy SEARCHING;
    public static final DereferencePolicy FINDING;
    public static final DereferencePolicy ALWAYS;
    private static final HashMap<Integer, DereferencePolicy> UNDEFINED_POLICIES;
    private static final long serialVersionUID = 3722883359911755096L;
    private final int intValue;
    private final String name;
    
    private DereferencePolicy(final int intValue) {
        this.intValue = intValue;
        this.name = String.valueOf(intValue);
    }
    
    private DereferencePolicy(final String name, final int intValue) {
        this.name = name;
        this.intValue = intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static DereferencePolicy valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return DereferencePolicy.NEVER;
            }
            case 1: {
                return DereferencePolicy.SEARCHING;
            }
            case 2: {
                return DereferencePolicy.FINDING;
            }
            case 3: {
                return DereferencePolicy.ALWAYS;
            }
            default: {
                synchronized (DereferencePolicy.UNDEFINED_POLICIES) {
                    DereferencePolicy p = DereferencePolicy.UNDEFINED_POLICIES.get(intValue);
                    if (p == null) {
                        p = new DereferencePolicy(intValue);
                        DereferencePolicy.UNDEFINED_POLICIES.put(intValue, p);
                    }
                    return p;
                }
                break;
            }
        }
    }
    
    public static DereferencePolicy definedValueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return DereferencePolicy.NEVER;
            }
            case 1: {
                return DereferencePolicy.SEARCHING;
            }
            case 2: {
                return DereferencePolicy.FINDING;
            }
            case 3: {
                return DereferencePolicy.ALWAYS;
            }
            default: {
                return null;
            }
        }
    }
    
    public static DereferencePolicy[] values() {
        return new DereferencePolicy[] { DereferencePolicy.NEVER, DereferencePolicy.SEARCHING, DereferencePolicy.FINDING, DereferencePolicy.ALWAYS };
    }
    
    @Override
    public int hashCode() {
        return this.intValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof DereferencePolicy && this.intValue == ((DereferencePolicy)o).intValue));
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static {
        NEVER = new DereferencePolicy("NEVER", 0);
        SEARCHING = new DereferencePolicy("SEARCHING", 1);
        FINDING = new DereferencePolicy("FINDING", 2);
        ALWAYS = new DereferencePolicy("ALWAYS", 3);
        UNDEFINED_POLICIES = new HashMap<Integer, DereferencePolicy>(StaticUtils.computeMapCapacity(10));
    }
}
