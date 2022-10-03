package com.me.ems.framework.summaryscope.utils;

public class SummaryScopeConstants
{
    public static final Integer CUSTOM_GROUP;
    public static final Integer REMOTE_OFFICE;
    public static final Integer TECHNICIAN;
    public static final Integer CUSTOMER_OR_ALL_MANAGED_COMP;
    public static final Integer EVENT_SCOPE_ADDED;
    public static final Integer EVENT_SCOPE_MODIFIED;
    public static final Integer EVENT_SCOPE_DELETED;
    public static final Integer EVENT_INVOKE_AMC_SUMMARY;
    
    static {
        CUSTOM_GROUP = 0;
        REMOTE_OFFICE = 1;
        TECHNICIAN = 2;
        CUSTOMER_OR_ALL_MANAGED_COMP = 3;
        EVENT_SCOPE_ADDED = 0;
        EVENT_SCOPE_MODIFIED = 1;
        EVENT_SCOPE_DELETED = 2;
        EVENT_INVOKE_AMC_SUMMARY = 3;
    }
}
