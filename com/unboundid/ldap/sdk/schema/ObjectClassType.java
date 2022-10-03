package com.unboundid.ldap.sdk.schema;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum ObjectClassType
{
    ABSTRACT("ABSTRACT"), 
    STRUCTURAL("STRUCTURAL"), 
    AUXILIARY("AUXILIARY");
    
    private final String name;
    
    private ObjectClassType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static ObjectClassType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "abstract": {
                return ObjectClassType.ABSTRACT;
            }
            case "structural": {
                return ObjectClassType.STRUCTURAL;
            }
            case "auxiliary": {
                return ObjectClassType.AUXILIARY;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
