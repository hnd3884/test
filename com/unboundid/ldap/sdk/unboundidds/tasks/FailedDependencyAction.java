package com.unboundid.ldap.sdk.unboundidds.tasks;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum FailedDependencyAction
{
    PROCESS("process"), 
    CANCEL("cancel"), 
    DISABLE("disable");
    
    private final String name;
    
    private FailedDependencyAction(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static FailedDependencyAction forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "process": {
                return FailedDependencyAction.PROCESS;
            }
            case "cancel": {
                return FailedDependencyAction.CANCEL;
            }
            case "disable": {
                return FailedDependencyAction.DISABLE;
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
