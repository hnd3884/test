package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum ChangeType
{
    ADD("add"), 
    DELETE("delete"), 
    MODIFY("modify"), 
    MODIFY_DN("moddn");
    
    private final String name;
    
    private ChangeType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static ChangeType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "add": {
                return ChangeType.ADD;
            }
            case "delete":
            case "del": {
                return ChangeType.DELETE;
            }
            case "modify":
            case "mod": {
                return ChangeType.MODIFY;
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
                return ChangeType.MODIFY_DN;
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
