package com.me.ems.summaryserver.common;

public class ProbeActionConstants
{
    public static final String DC_API_PATH = "dcapi";
    public static final String SUMMARY_SERVER_PATH = "summaryserver";
    public static final String PATH_SEPARATOR = "/";
    public static final String SUMMARY_PROBE_ACTION_PATH = "updateActionData";
    public static final String SUMMARY_PROBE_ACTION_JSON = "application/updateActionData.v1+json";
    public static final String ACTION_MODULE = "actionModule";
    public static final String ACTION_TYPE = "actionType";
    public static final String ACTION_STATUS = "actionStatus";
    public static final String ACTION_DATA = "actionData";
    public static final int COMMON = 1;
    public static final int EVENT_LOG = 8;
    public static final int EVENT_LOG_UPDATE = 1;
    
    public static class EventLogData
    {
        public static final String EVENT_LOG_ID = "event_log_id";
        public static final String EVENT_ID = "event_id";
        public static final String USER_NAME = "userName";
        public static final String RES_MAP = "resMap";
        public static final String REMARKS = "remarks";
        public static final String REMARKS_ARGS = "remarks_args";
        public static final String UPDATE_TIME = "updateTime";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String CONSENT_ID = "consent_id";
        public static final String RESOURCE_ID = "resourceID";
    }
}
