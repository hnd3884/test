package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum GetConfigurationType
{
    ACTIVE((byte)(-128), 0), 
    BASELINE((byte)(-127), 1), 
    ARCHIVED((byte)(-126), 2);
    
    static final byte ACTIVE_BER_TYPE = Byte.MIN_VALUE;
    static final byte BASELINE_BER_TYPE = -127;
    static final byte ARCHIVED_BER_TYPE = -126;
    private final byte berType;
    private final int intValue;
    
    private GetConfigurationType(final byte berType, final int intValue) {
        this.berType = berType;
        this.intValue = intValue;
    }
    
    public byte getBERType() {
        return this.berType;
    }
    
    public int getIntValue() {
        return this.intValue;
    }
    
    public static GetConfigurationType forBERType(final byte berType) {
        for (final GetConfigurationType t : values()) {
            if (t.berType == berType) {
                return t;
            }
        }
        return null;
    }
    
    public static GetConfigurationType forIntValue(final int intValue) {
        for (final GetConfigurationType t : values()) {
            if (t.intValue == intValue) {
                return t;
            }
        }
        return null;
    }
    
    public static GetConfigurationType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "active": {
                return GetConfigurationType.ACTIVE;
            }
            case "baseline": {
                return GetConfigurationType.BASELINE;
            }
            case "archived": {
                return GetConfigurationType.ARCHIVED;
            }
            default: {
                return null;
            }
        }
    }
}
