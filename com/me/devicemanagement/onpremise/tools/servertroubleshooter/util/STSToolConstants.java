package com.me.devicemanagement.onpremise.tools.servertroubleshooter.util;

import java.io.File;

public class STSToolConstants
{
    public static final String STS_CONF_FILE;
    public static final String LOCK_FILE;
    public static final String METRACKING_JSON_FILE = "STSMETrackingDetails.json";
    public static final String STS_TOOL_EXEC_ALLOW_KEY = "sts.tool.execution.allow";
    public static final String TOOLS_LIST = "tools.list";
    public static final String LOCKFILE_CHECK_INTERVAL = "lockfile.check.interval";
    public static final String WORKERPOOL_SIZE = "sts.tool.workerpool.size";
    public static final String SCHEDULER_WORKERPOOL_SIZE = "sts.tool.scheduler.workerpool.size";
    public static final String LOGS_FOLDER = "logs";
    
    static {
        STS_CONF_FILE = "conf" + File.separator + "STSTool.conf";
        LOCK_FILE = "bin" + File.separator + "sts.lock";
    }
}
