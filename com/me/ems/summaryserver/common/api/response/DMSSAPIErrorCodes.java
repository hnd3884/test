package com.me.ems.summaryserver.common.api.response;

import com.me.ems.framework.common.api.response.APIErrorCodes;

public class DMSSAPIErrorCodes extends APIErrorCodes
{
    public static final String UPDATE_AUTH_KEY_FAILED = "PRBE9500301";
    public static final String PROBE_AUTH_KEY_NOT_PROVIDED = "PRBE9500302";
    public static final String SS_AUTH_KEY_NOT_PROVIDED = "PRBE9500303";
    public static final String PROBE_NAME_ALREADY_EXISTS = "PRBE9500304";
    public static final String API_KEY_MISMATCH = "PRBE9500305";
    public static final String SERVER_DOWN = "PRBE9500306";
    public static final Long PROBE_NOT_REACHABLE;
    
    static {
        PROBE_NOT_REACHABLE = 9500001L;
    }
}
