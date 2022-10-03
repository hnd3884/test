package com.unboundid.util;

public enum ThreadSafetyLevel
{
    COMPLETELY_THREADSAFE, 
    MOSTLY_THREADSAFE, 
    MOSTLY_NOT_THREADSAFE, 
    NOT_THREADSAFE, 
    INTERFACE_THREADSAFE, 
    INTERFACE_NOT_THREADSAFE, 
    METHOD_THREADSAFE, 
    METHOD_NOT_THREADSAFE;
    
    public static ThreadSafetyLevel forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "completelythreadsafe":
            case "completely-threadsafe":
            case "completely_threadsafe": {
                return ThreadSafetyLevel.COMPLETELY_THREADSAFE;
            }
            case "mostlythreadsafe":
            case "mostly-threadsafe":
            case "mostly_threadsafe": {
                return ThreadSafetyLevel.MOSTLY_THREADSAFE;
            }
            case "mostlynotthreadsafe":
            case "mostly-not-threadsafe":
            case "mostly_not_threadsafe": {
                return ThreadSafetyLevel.MOSTLY_NOT_THREADSAFE;
            }
            case "notthreadsafe":
            case "not-threadsafe":
            case "not_threadsafe": {
                return ThreadSafetyLevel.NOT_THREADSAFE;
            }
            case "interfacethreadsafe":
            case "interface-threadsafe":
            case "interface_threadsafe": {
                return ThreadSafetyLevel.INTERFACE_THREADSAFE;
            }
            case "interfacenotthreadsafe":
            case "interface-not-threadsafe":
            case "interface_not_threadsafe": {
                return ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE;
            }
            case "methodthreadsafe":
            case "method-threadsafe":
            case "method_threadsafe": {
                return ThreadSafetyLevel.METHOD_THREADSAFE;
            }
            case "methodnotthreadsafe":
            case "method-not-threadsafe":
            case "method_not_threadsafe": {
                return ThreadSafetyLevel.METHOD_NOT_THREADSAFE;
            }
            default: {
                return null;
            }
        }
    }
}
