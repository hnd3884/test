package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum MatchingEntryCountType
{
    EXAMINED_COUNT((byte)(-128)), 
    UNEXAMINED_COUNT((byte)(-127)), 
    UPPER_BOUND((byte)(-126)), 
    UNKNOWN((byte)(-125));
    
    private final byte berType;
    
    private MatchingEntryCountType(final byte berType) {
        this.berType = berType;
    }
    
    public byte getBERType() {
        return this.berType;
    }
    
    public boolean isMoreSpecificThan(final MatchingEntryCountType t) {
        switch (this) {
            case EXAMINED_COUNT: {
                return t != MatchingEntryCountType.EXAMINED_COUNT;
            }
            case UNEXAMINED_COUNT: {
                return t != MatchingEntryCountType.EXAMINED_COUNT && t != MatchingEntryCountType.UNEXAMINED_COUNT;
            }
            case UPPER_BOUND: {
                return t != MatchingEntryCountType.EXAMINED_COUNT && t != MatchingEntryCountType.UNEXAMINED_COUNT && t != MatchingEntryCountType.UPPER_BOUND;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isLessSpecificThan(final MatchingEntryCountType t) {
        switch (this) {
            case UNKNOWN: {
                return t != MatchingEntryCountType.UNKNOWN;
            }
            case UPPER_BOUND: {
                return t != MatchingEntryCountType.UNKNOWN && t != MatchingEntryCountType.UPPER_BOUND;
            }
            case UNEXAMINED_COUNT: {
                return t != MatchingEntryCountType.UNKNOWN && t != MatchingEntryCountType.UPPER_BOUND && t != MatchingEntryCountType.UNEXAMINED_COUNT;
            }
            default: {
                return false;
            }
        }
    }
    
    public static MatchingEntryCountType valueOf(final byte berType) {
        for (final MatchingEntryCountType t : values()) {
            if (t.berType == berType) {
                return t;
            }
        }
        return null;
    }
    
    public static MatchingEntryCountType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "examinedcount":
            case "examined-count":
            case "examined_count": {
                return MatchingEntryCountType.EXAMINED_COUNT;
            }
            case "unexaminedcount":
            case "unexamined-count":
            case "unexamined_count": {
                return MatchingEntryCountType.UNEXAMINED_COUNT;
            }
            case "upperbound":
            case "upper-bound":
            case "upper_bound": {
                return MatchingEntryCountType.UPPER_BOUND;
            }
            case "unknown": {
                return MatchingEntryCountType.UNKNOWN;
            }
            default: {
                return null;
            }
        }
    }
}
