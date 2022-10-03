package com.me.ems.summaryserver.common.sync;

public class SyncStatusCodes
{
    public static final int SYNC_SUCCESSFUL = 1;
    public static final int SYNC_PROGRESSING_IN_SUMMARY = 2;
    public static final int SYNC_FAILED_IN_PROBE = 3;
    public static final int SYNC_FAILED_IN_SUMMARY = 4;
    public static final int SYNC_STATUS_NOT_RECEIVED_FROM_SUMMARY = 5;
    public static final int SYNC_STATUS_UNKNOWN = 6;
    public static final int SYNC_FILE_POSTED_TO_SS = 950001;
    public static final int SYNC_FILE_RECEIVED_IN_SS = 950002;
    public static final int SYNC_FILE_POST_FAILED = 950003;
    public static final int SYNC_FILE_SUCCESS = 950100;
    public static final int SYNC_FILE_IN_QUEUE = 950101;
    public static final int CSV_UNKNOWN_PROCESSING_ERROR = 950201;
    public static final int CSV_READ_ERROR = 950202;
    public static final int CSV_EMPTY_CONTENT = 950203;
    public static final int CSV_RECORD_THRESHOLD_EXCEEDS = 950204;
    public static final int JSON_UNKNOWN_PROCESSING_ERROR = 950301;
    public static final int JSON_READ_ERROR = 950302;
    public static final int JSON_EMPTY = 950303;
    public static final int MODULE_PRE_PROCESSING_ERROR = 950401;
    public static final int MODULE_POST_PROCESSING_ERROR = 950402;
    public static final int CONFLICT_META_MISSING = 950501;
    public static final int SEQUENCE_GENERATOR_ERROR = 950502;
    public static final int RESOLUTION_SUMMARY_DATA_MISSING = 950503;
    public static final int CONFLICT_RESOLVE_UNKNOWN_ERROR = 950504;
    public static final int POST_CONFLICT_PROCESSING_ERROR = 950505;
    public static final int BULK_LOAD_STAGING_TABLE_ERROR = 950601;
    public static final int BULK_LOAD_ERROR = 950602;
    public static final int DELETE_QUERY_ERROR = 950701;
    public static final int SYNC_FILE_UNKNOWN_FAILURE = 950801;
}
