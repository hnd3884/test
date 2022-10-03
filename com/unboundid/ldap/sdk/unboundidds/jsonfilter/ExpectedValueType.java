package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum ExpectedValueType
{
    BOOLEAN("boolean"), 
    EMPTY_ARRAY("empty-array"), 
    NON_EMPTY_ARRAY("non-empty-array"), 
    NULL("null"), 
    NUMBER("number"), 
    OBJECT("object"), 
    STRING("string");
    
    private final String name;
    
    private ExpectedValueType(final String name) {
        this.name = name;
    }
    
    public static ExpectedValueType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "boolean": {
                return ExpectedValueType.BOOLEAN;
            }
            case "emptyarray":
            case "empty-array":
            case "empty_array": {
                return ExpectedValueType.EMPTY_ARRAY;
            }
            case "nonemptyarray":
            case "non-empty-array":
            case "non_empty_array": {
                return ExpectedValueType.NON_EMPTY_ARRAY;
            }
            case "null": {
                return ExpectedValueType.NULL;
            }
            case "number": {
                return ExpectedValueType.NUMBER;
            }
            case "object": {
                return ExpectedValueType.OBJECT;
            }
            case "string": {
                return ExpectedValueType.STRING;
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
