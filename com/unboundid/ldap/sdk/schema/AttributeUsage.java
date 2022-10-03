package com.unboundid.ldap.sdk.schema;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AttributeUsage
{
    USER_APPLICATIONS("userApplications", false), 
    DIRECTORY_OPERATION("directoryOperation", true), 
    DISTRIBUTED_OPERATION("distributedOperation", true), 
    DSA_OPERATION("dSAOperation", true);
    
    private final boolean isOperational;
    private final String name;
    
    private AttributeUsage(final String name, final boolean isOperational) {
        this.name = name;
        this.isOperational = isOperational;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isOperational() {
        return this.isOperational;
    }
    
    public static AttributeUsage forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "userapplications":
            case "user-applications":
            case "user_applications": {
                return AttributeUsage.USER_APPLICATIONS;
            }
            case "directoryoperation":
            case "directory-operation":
            case "directory_operation": {
                return AttributeUsage.DIRECTORY_OPERATION;
            }
            case "distributedoperation":
            case "distributed-operation":
            case "distributed_operation": {
                return AttributeUsage.DISTRIBUTED_OPERATION;
            }
            case "dsaoperation":
            case "dsa-operation":
            case "dsa_operation": {
                return AttributeUsage.DSA_OPERATION;
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
