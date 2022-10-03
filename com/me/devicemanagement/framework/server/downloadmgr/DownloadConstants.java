package com.me.devicemanagement.framework.server.downloadmgr;

public class DownloadConstants
{
    public static final String FCM_SEND_ENDPOINT = "1";
    public static final int SUCCESS = 0;
    public static final int ERROR_INVALID_SOURCE_URL = 10001;
    public static final int ERROR_NOTFOUND_SOURCE_URL = 10002;
    public static final int ERROR_RESTRICT_BINARY_URL = 10003;
    public static final int ERROR_NOTFOUND_DESTINATION_FILE = 10004;
    public static final int ERROR_DATA_NOTAVAILABLE = 10005;
    public static final int ERROR_STREAM_CLOSE = 10006;
    public static final int ERROR_RESPONSE_RECEIVED_EMPTY = 10007;
    public static final int ERROR_DOWNLOAD_FAILED = 10008;
    public static final int ERROR_CHECKSUM_FAILED = 10009;
    public static final int ERROR_FILE_NOT_MODIFIED = 10010;
    public static final int ERROR_SERVICE_NOTAVAILABLE = 10011;
    public static final int IN_PROGRESS = 20000;
    public static final int DIRECT_CONNECTION = 1;
    public static final int MANUAL_PROXY = 2;
    public static final int NO_CONNECTION_TO_INTERNET = 3;
    public static final int AUTO_PROXY_SCRIPT = 4;
    public static final int NOT_CONFIGURED = 0;
    public static final int SUCCESSFUL_SSL_VALIDATION = 200;
    public static final int SSL_VALIDATION_FAILED = 525;
    public static final int SSL_VALIDATION_NOT_ENABLED = 100;
}
