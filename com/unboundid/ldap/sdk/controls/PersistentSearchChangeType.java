package com.unboundid.ldap.sdk.controls;

import java.util.Iterator;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum PersistentSearchChangeType
{
    ADD("add", 1), 
    DELETE("delete", 2), 
    MODIFY("modify", 4), 
    MODIFY_DN("moddn", 8);
    
    private final int value;
    private final String name;
    
    private PersistentSearchChangeType(final String name, final int value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int intValue() {
        return this.value;
    }
    
    public static PersistentSearchChangeType valueOf(final int intValue) {
        switch (intValue) {
            case 1: {
                return PersistentSearchChangeType.ADD;
            }
            case 2: {
                return PersistentSearchChangeType.DELETE;
            }
            case 4: {
                return PersistentSearchChangeType.MODIFY;
            }
            case 8: {
                return PersistentSearchChangeType.MODIFY_DN;
            }
            default: {
                return null;
            }
        }
    }
    
    public static PersistentSearchChangeType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "add": {
                return PersistentSearchChangeType.ADD;
            }
            case "delete":
            case "del": {
                return PersistentSearchChangeType.DELETE;
            }
            case "modify":
            case "mod": {
                return PersistentSearchChangeType.MODIFY;
            }
            case "modifydn":
            case "modify-dn":
            case "modify_dn":
            case "moddn":
            case "mod-dn":
            case "mod_dn":
            case "modifyrdn":
            case "modify-rdn":
            case "modify_rdn":
            case "modrdn":
            case "mod-rdn":
            case "mod_rdn": {
                return PersistentSearchChangeType.MODIFY_DN;
            }
            default: {
                return null;
            }
        }
    }
    
    public static Set<PersistentSearchChangeType> allChangeTypes() {
        return EnumSet.allOf(PersistentSearchChangeType.class);
    }
    
    public static int encodeChangeTypes(final PersistentSearchChangeType... changeTypes) {
        int changeTypesValue = 0;
        for (final PersistentSearchChangeType changeType : changeTypes) {
            changeTypesValue |= changeType.intValue();
        }
        return changeTypesValue;
    }
    
    public static int encodeChangeTypes(final Collection<PersistentSearchChangeType> changeTypes) {
        int changeTypesValue = 0;
        for (final PersistentSearchChangeType changeType : changeTypes) {
            changeTypesValue |= changeType.intValue();
        }
        return changeTypesValue;
    }
    
    public static Set<PersistentSearchChangeType> decodeChangeTypes(final int changeTypes) {
        final EnumSet<PersistentSearchChangeType> ctSet = EnumSet.noneOf(PersistentSearchChangeType.class);
        if ((changeTypes & PersistentSearchChangeType.ADD.intValue()) == PersistentSearchChangeType.ADD.intValue()) {
            ctSet.add(PersistentSearchChangeType.ADD);
        }
        if ((changeTypes & PersistentSearchChangeType.DELETE.intValue()) == PersistentSearchChangeType.DELETE.intValue()) {
            ctSet.add(PersistentSearchChangeType.DELETE);
        }
        if ((changeTypes & PersistentSearchChangeType.MODIFY.intValue()) == PersistentSearchChangeType.MODIFY.intValue()) {
            ctSet.add(PersistentSearchChangeType.MODIFY);
        }
        if ((changeTypes & PersistentSearchChangeType.MODIFY_DN.intValue()) == PersistentSearchChangeType.MODIFY_DN.intValue()) {
            ctSet.add(PersistentSearchChangeType.MODIFY_DN);
        }
        return ctSet;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
