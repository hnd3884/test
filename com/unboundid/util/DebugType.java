package com.unboundid.util;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum DebugType
{
    ASN1("asn1"), 
    CONNECT("connect"), 
    EXCEPTION("exception"), 
    LDAP("ldap"), 
    CONNECTION_POOL("connection-pool"), 
    LDIF("ldif"), 
    MONITOR("monitor"), 
    CODING_ERROR("coding-error"), 
    OTHER("other");
    
    private final String name;
    
    private DebugType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static DebugType forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "asn1": {
                return DebugType.ASN1;
            }
            case "connect": {
                return DebugType.CONNECT;
            }
            case "exception": {
                return DebugType.EXCEPTION;
            }
            case "ldap": {
                return DebugType.LDAP;
            }
            case "pool":
            case "connectionpool":
            case "connection-pool":
            case "connection_pool": {
                return DebugType.CONNECTION_POOL;
            }
            case "ldif": {
                return DebugType.LDIF;
            }
            case "monitor": {
                return DebugType.MONITOR;
            }
            case "codingerror":
            case "coding-error":
            case "coding_error": {
                return DebugType.CODING_ERROR;
            }
            case "other": {
                return DebugType.OTHER;
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getTypeNameList() {
        final StringBuilder buffer = new StringBuilder();
        final DebugType[] types = values();
        for (int i = 0; i < types.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(types[i].name);
        }
        return buffer.toString();
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
