package com.unboundid.ldif;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum TrailingSpaceBehavior
{
    STRIP, 
    RETAIN, 
    REJECT;
    
    public static TrailingSpaceBehavior forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "strip": {
                return TrailingSpaceBehavior.STRIP;
            }
            case "retain": {
                return TrailingSpaceBehavior.RETAIN;
            }
            case "reject": {
                return TrailingSpaceBehavior.REJECT;
            }
            default: {
                return null;
            }
        }
    }
}
