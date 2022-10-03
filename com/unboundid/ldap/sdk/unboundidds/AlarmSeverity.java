package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AlarmSeverity
{
    INDETERMINATE, 
    NORMAL, 
    WARNING, 
    MINOR, 
    MAJOR, 
    CRITICAL;
    
    public static AlarmSeverity forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "indeterminate": {
                return AlarmSeverity.INDETERMINATE;
            }
            case "normal": {
                return AlarmSeverity.NORMAL;
            }
            case "warning": {
                return AlarmSeverity.WARNING;
            }
            case "minor": {
                return AlarmSeverity.MINOR;
            }
            case "major": {
                return AlarmSeverity.MAJOR;
            }
            case "critical": {
                return AlarmSeverity.CRITICAL;
            }
            default: {
                return null;
            }
        }
    }
}
