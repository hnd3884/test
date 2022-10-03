package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum OperationType
{
    ABANDON, 
    ADD, 
    BIND, 
    COMPARE, 
    DELETE, 
    EXTENDED, 
    MODIFY, 
    MODIFY_DN, 
    SEARCH, 
    UNBIND;
    
    public static OperationType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "abandon": {
                return OperationType.ABANDON;
            }
            case "add": {
                return OperationType.ADD;
            }
            case "bind": {
                return OperationType.BIND;
            }
            case "compare": {
                return OperationType.COMPARE;
            }
            case "delete":
            case "del": {
                return OperationType.DELETE;
            }
            case "extended":
            case "extendedoperation":
            case "extended-operation":
            case "extended_operation":
            case "extendedop":
            case "extended-op":
            case "extended_op":
            case "extop":
            case "ext-op":
            case "ext_op": {
                return OperationType.EXTENDED;
            }
            case "modify":
            case "mod": {
                return OperationType.MODIFY;
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
                return OperationType.MODIFY_DN;
            }
            case "search": {
                return OperationType.SEARCH;
            }
            case "unbind": {
                return OperationType.UNBIND;
            }
            default: {
                return null;
            }
        }
    }
}
