package com.zoho.security.eventfw.config;

public class EventFWConstants
{
    public static final String ZOHO_LOGS = "ZohoLogs";
    public static final String DEFAULT = "DEFAULT";
    public static final String TEMPLATE_NAME = "eventframework.vtl";
    public static final String ZOHOLOGS = "ZohoLogs";
    public static final String DEFAULT_JAVA_LOGS = "JavaLogs";
    public static final Object DEFAULT_DISPATCHER;
    public static final String EVENT_CONFIG_FILE = "zsec-events.xml";
    
    static {
        DEFAULT_DISPATCHER = "default";
    }
    
    public enum ATTRIBUTES
    {
        NAME("name"), 
        SEVERITY("severity"), 
        TYPE("type"), 
        PACKAGE("package"), 
        REF("ref"), 
        DATATEMPLATE("data-template"), 
        IMPL("impl"), 
        DISPATCHER_TEMPLATE("dispatcher-template"), 
        TIME_THRESHOLD("time-threshold"), 
        COUNT_THRESHOLD("count-threshold"), 
        VALUE("value"), 
        TRANSFERAPI("transfer-api"), 
        CATEGORY("category"), 
        CALLER_INFERRER("caller-inferrer");
        
        private String attribute;
        
        private ATTRIBUTES(final String name) {
            this.attribute = null;
            this.attribute = name;
        }
        
        public String value() {
            return this.attribute;
        }
    }
    
    public enum TYPE
    {
        LOG("log"), 
        EVENT("event");
        
        private String type;
        
        private TYPE(final String name) {
            this.type = null;
            this.type = name;
        }
        
        public String value() {
            return this.type;
        }
        
        public static TYPE getType(final String eventType) {
            try {
                return valueOf(eventType.toUpperCase());
            }
            catch (final Exception e) {
                return null;
            }
        }
    }
    
    public enum DISPATCHER_TYPE
    {
        BATCH("batch"), 
        TIMER("timer"), 
        DISTINCT("distinct"), 
        CUSTOM("custom");
        
        private String type;
        
        private DISPATCHER_TYPE(final String name) {
            this.type = null;
            this.type = name;
        }
        
        public String value() {
            return this.type;
        }
        
        public static DISPATCHER_TYPE getType(final String type) {
            try {
                return valueOf(type.toUpperCase());
            }
            catch (final Exception e) {
                return null;
            }
        }
    }
    
    public enum THRESHOLD_TYPE
    {
        COUNT("count"), 
        TIME("time"), 
        EXECUTION_TIME("execution_time");
        
        private String type;
        
        private THRESHOLD_TYPE(final String type) {
            this.type = null;
            this.type = type;
        }
        
        public String value() {
            return this.type;
        }
        
        public static THRESHOLD_TYPE getType(final String type) {
            try {
                return valueOf(type.toUpperCase());
            }
            catch (final Exception e) {
                return null;
            }
        }
    }
    
    public enum TAG
    {
        EVENTS("events"), 
        EVENT("event"), 
        LOGS("logs"), 
        LOG("log"), 
        SUB_TYPE("sub-type"), 
        FIELDS("fields"), 
        FIELD("field"), 
        DATA_TEMPLATES("data-templates"), 
        DATA_TEMPLATE("data-template"), 
        TRANSFERAPIS("transfer-apis"), 
        TRANSFERAPI("transfer-api"), 
        DISPATCHER_TEMPLATES("dispatcher-templates"), 
        DISPATCHER_TEMPLATE("dispatcher-template"), 
        TYPE("type"), 
        THRESHOLD("threshold"), 
        BUILTINFIELDS("builtin-fields");
        
        private String tagName;
        
        private TAG(final String name) {
            this.tagName = null;
            this.tagName = name;
        }
        
        public String value() {
            return this.tagName;
        }
    }
    
    public enum KEY
    {
        TYPE, 
        SUB_TYPE, 
        NAME, 
        SEVERITY, 
        DATA;
    }
}
