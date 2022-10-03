package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.util.HashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModificationType implements Serializable
{
    public static final int ADD_INT_VALUE = 0;
    public static final ModificationType ADD;
    public static final int DELETE_INT_VALUE = 1;
    public static final ModificationType DELETE;
    public static final int REPLACE_INT_VALUE = 2;
    public static final ModificationType REPLACE;
    public static final int INCREMENT_INT_VALUE = 3;
    public static final ModificationType INCREMENT;
    private static final HashMap<Integer, ModificationType> UNDEFINED_MOD_TYPES;
    private static final long serialVersionUID = -7863114394728980308L;
    private final int intValue;
    private final String name;
    
    private ModificationType(final int intValue) {
        this.intValue = intValue;
        this.name = String.valueOf(intValue);
    }
    
    private ModificationType(final String name, final int intValue) {
        this.name = name;
        this.intValue = intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static ModificationType valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return ModificationType.ADD;
            }
            case 1: {
                return ModificationType.DELETE;
            }
            case 2: {
                return ModificationType.REPLACE;
            }
            case 3: {
                return ModificationType.INCREMENT;
            }
            default: {
                synchronized (ModificationType.UNDEFINED_MOD_TYPES) {
                    ModificationType t = ModificationType.UNDEFINED_MOD_TYPES.get(intValue);
                    if (t == null) {
                        t = new ModificationType(intValue);
                        ModificationType.UNDEFINED_MOD_TYPES.put(intValue, t);
                    }
                    return t;
                }
                break;
            }
        }
    }
    
    public static ModificationType definedValueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return ModificationType.ADD;
            }
            case 1: {
                return ModificationType.DELETE;
            }
            case 2: {
                return ModificationType.REPLACE;
            }
            case 3: {
                return ModificationType.INCREMENT;
            }
            default: {
                return null;
            }
        }
    }
    
    public static ModificationType[] values() {
        return new ModificationType[] { ModificationType.ADD, ModificationType.DELETE, ModificationType.REPLACE, ModificationType.INCREMENT };
    }
    
    @Override
    public int hashCode() {
        return this.intValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof ModificationType && this.intValue == ((ModificationType)o).intValue));
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static {
        ADD = new ModificationType("ADD", 0);
        DELETE = new ModificationType("DELETE", 1);
        REPLACE = new ModificationType("REPLACE", 2);
        INCREMENT = new ModificationType("INCREMENT", 3);
        UNDEFINED_MOD_TYPES = new HashMap<Integer, ModificationType>(StaticUtils.computeMapCapacity(10));
    }
}
