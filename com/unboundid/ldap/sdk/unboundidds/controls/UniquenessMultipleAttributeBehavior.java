package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum UniquenessMultipleAttributeBehavior
{
    UNIQUE_WITHIN_EACH_ATTRIBUTE(0), 
    UNIQUE_ACROSS_ALL_ATTRIBUTES_INCLUDING_IN_SAME_ENTRY(1), 
    UNIQUE_ACROSS_ALL_ATTRIBUTES_EXCEPT_IN_SAME_ENTRY(2), 
    UNIQUE_IN_COMBINATION(3);
    
    private final int intValue;
    
    private UniquenessMultipleAttributeBehavior(final int intValue) {
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static UniquenessMultipleAttributeBehavior valueOf(final int intValue) {
        switch (intValue) {
            case 0: {
                return UniquenessMultipleAttributeBehavior.UNIQUE_WITHIN_EACH_ATTRIBUTE;
            }
            case 1: {
                return UniquenessMultipleAttributeBehavior.UNIQUE_ACROSS_ALL_ATTRIBUTES_INCLUDING_IN_SAME_ENTRY;
            }
            case 2: {
                return UniquenessMultipleAttributeBehavior.UNIQUE_ACROSS_ALL_ATTRIBUTES_EXCEPT_IN_SAME_ENTRY;
            }
            case 3: {
                return UniquenessMultipleAttributeBehavior.UNIQUE_IN_COMBINATION;
            }
            default: {
                return null;
            }
        }
    }
    
    public static UniquenessMultipleAttributeBehavior forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "uniquewithineachattribute":
            case "unique-within-each-attribute":
            case "unique_within_each_attribute": {
                return UniquenessMultipleAttributeBehavior.UNIQUE_WITHIN_EACH_ATTRIBUTE;
            }
            case "uniqueacrossallattributesincludinginsameentry":
            case "unique-across-all-attributes-including-in-same-entry":
            case "unique_across_all_attributes_including_in_same_entry": {
                return UniquenessMultipleAttributeBehavior.UNIQUE_ACROSS_ALL_ATTRIBUTES_INCLUDING_IN_SAME_ENTRY;
            }
            case "uniqueacrossallattributesexceptinsameentry":
            case "unique-across-all-attributes-except-in-same-entry":
            case "unique_across_all_attributes_except_in_same_entry": {
                return UniquenessMultipleAttributeBehavior.UNIQUE_ACROSS_ALL_ATTRIBUTES_EXCEPT_IN_SAME_ENTRY;
            }
            case "uniqueincombination":
            case "unique-in-combination":
            case "unique_in_combination": {
                return UniquenessMultipleAttributeBehavior.UNIQUE_IN_COMBINATION;
            }
            default: {
                return null;
            }
        }
    }
}
