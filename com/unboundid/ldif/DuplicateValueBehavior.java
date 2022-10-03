package com.unboundid.ldif;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum DuplicateValueBehavior
{
    STRIP, 
    RETAIN, 
    REJECT;
    
    public static DuplicateValueBehavior forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "strip": {
                return DuplicateValueBehavior.STRIP;
            }
            case "retain": {
                return DuplicateValueBehavior.RETAIN;
            }
            case "reject": {
                return DuplicateValueBehavior.REJECT;
            }
            default: {
                return null;
            }
        }
    }
}
