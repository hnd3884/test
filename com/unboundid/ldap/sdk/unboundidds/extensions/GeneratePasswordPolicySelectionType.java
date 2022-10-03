package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum GeneratePasswordPolicySelectionType
{
    DEFAULT_POLICY((byte)(-128)), 
    PASSWORD_POLICY_DN((byte)(-127)), 
    TARGET_ENTRY_DN((byte)(-126));
    
    private final byte berType;
    
    private GeneratePasswordPolicySelectionType(final byte type) {
        this.berType = type;
    }
    
    public byte getBERType() {
        return this.berType;
    }
    
    public static GeneratePasswordPolicySelectionType forType(final byte berType) {
        for (final GeneratePasswordPolicySelectionType t : values()) {
            if (t.berType == berType) {
                return t;
            }
        }
        return null;
    }
}
