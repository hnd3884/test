package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum ErrorLogSeverity
{
    DEBUG, 
    FATAL_ERROR, 
    INFORMATION, 
    MILD_ERROR, 
    MILD_WARNING, 
    NOTICE, 
    SEVERE_ERROR, 
    SEVERE_WARNING;
    
    public static ErrorLogSeverity forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "debug": {
                return ErrorLogSeverity.DEBUG;
            }
            case "fatalerror":
            case "fatal-error":
            case "fatal_error": {
                return ErrorLogSeverity.FATAL_ERROR;
            }
            case "information": {
                return ErrorLogSeverity.INFORMATION;
            }
            case "milderror":
            case "mild-error":
            case "mild_error": {
                return ErrorLogSeverity.MILD_ERROR;
            }
            case "mildwarning":
            case "mild-warning":
            case "mild_warning": {
                return ErrorLogSeverity.MILD_WARNING;
            }
            case "notice": {
                return ErrorLogSeverity.NOTICE;
            }
            case "severeerror":
            case "severe-error":
            case "severe_error": {
                return ErrorLogSeverity.SEVERE_ERROR;
            }
            case "severewarning":
            case "severe-warning":
            case "severe_warning": {
                return ErrorLogSeverity.SEVERE_WARNING;
            }
            default: {
                return null;
            }
        }
    }
}
