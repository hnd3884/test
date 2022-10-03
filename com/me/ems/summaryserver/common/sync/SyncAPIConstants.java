package com.me.ems.summaryserver.common.sync;

public class SyncAPIConstants
{
    public static final String UPDATE_SYNC_STATUS = "updateSyncStatus";
    public static final String PROBE_SYNC_API_PATH = "probe/sync";
    public static final String PROBE_UPDATE_SYNC_STATUS_URL = "emsapi/probe/sync/updateSyncStatus";
    public static final String UPDATE_SYNC_STATUS_JSON = "application/probeUpdateSyncStatus.v1+json";
    public static final String UPDATE_SYNC_STATUS_RESULT_JSON = "application/probeUpdateSyncStatusResult.v1+json";
    public static final String SUMMARY_SYNC_API_PATH = "summaryserver/sync";
    public static final String SUMMARY_UPDATE_SYNC_STATUS_URL = "emsapi/summaryserver/sync/updateSyncStatus";
    public static final String SUMMARY_UPDATE_SYNC_STATUS_JSON = "application/summaryUpdateSyncStatus.v1+json";
    public static final String SUMMARY_UPDATE_SYNC_STATUS_RESULT_JSON = "application/summaryUpdateSyncStatusResult.v1+json";
    public static final String FETCH_SYNC_STATUS = "fetchSyncStatus";
    public static final String SUMMARY_FETCH_SYNC_STATUS_URL = "emsapi/summaryserver/sync/fetchSyncStatus";
    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String GZIP_ENCODING = "gzip";
}
