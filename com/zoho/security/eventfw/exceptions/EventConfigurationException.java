package com.zoho.security.eventfw.exceptions;

public class EventConfigurationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    public static final String DATATEMPLATE_NOT_FOUND = "DATATEMPLATE_NOT_FOUND";
    public static final String PARSER_NOT_INITIALISED = "PARSER_NOT_INITIALISED";
    public static final String DISPATCHER_TEMPLATE_NOT_FOUND = "DISPATCHER_TEMPLATE_NOT_FOUND";
    public static final String DOCUMENT_NULL = "DOCUMENT_NULL";
    public static final String LOGAPI_IMPL_EXCEPTION = "LOGAPI_IMPL_EXCEPTION";
    public static final String UNSUPPORTED_DISPATCHER_FOR_EVENT = "UNSUPPORTED_DISPATCHER_FOR_EVENT";
    public static final String BUILTIN_FIELD_IMPL_EXCEPTION = "BUILTIN_FIELD_IMPL_EXCEPTION";
    public static final String EVENT_INSTANCE_NOT_FOUND = "EVENT_INSTANCE_NOT_FOUND";
    public static final String DISPATCHER_TEMPLATE_CUSTOM_IMPL_ISSUE = "DISPATCHER_TEMPLATE_CUSTOM_IMPL_ISSUE";
    public static final String DUPLICATE_CONFIGURATION = "DUPLICATE_CONFIGURATION";
    public static final String DATATEMPLATE_REF_UNDEFINED = "DATATEMPLATE_REF_UNDEFINED";
    
    public EventConfigurationException(final String msg) {
        super(msg);
    }
}
