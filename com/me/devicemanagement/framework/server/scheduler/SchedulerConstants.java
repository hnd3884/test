package com.me.devicemanagement.framework.server.scheduler;

public class SchedulerConstants
{
    public static final String ONPREMISE_SCHEDULER_EXECUTOR_CLASSNAME = "com.me.devicemanagement.onpremise.server.scheduler.SchedulerExecutionTask";
    public static final String CLOUD_SCHEDULER_EXECUTOR_CLASSNAME = "com.me.devicemanagement.cloud.server.scheduler.SchedulerExecutionTask";
    public static final Boolean ENABLE;
    public static final Boolean DISABLE;
    public static final int MDM_INV_SCAN_TASK = 7000;
    public static final String STARTUP_POOL = "startupPool";
    public static final String MDM_POOL = "mdmPool";
    public static final String MDM_INV_TASKNAME = "DeviceScanTaskCallerTemplate";
    public static final String MDM_TASK_EXECUTER = "MdmAsyncTaskExecuter";
    public static final String ASYNC_POOL = "asynchThreadPool";
    public static final String DOWNLOAD_POOL = "downloadPool";
    public static final String SYNC_POOL = "syncPool";
    public static final String CHAT_POOL = "chatPool";
    public static final String NIO_POOL = "nioPool";
    public static final String MAC_DOWNLOAD_POOL = "macDownloadPool";
    public static final String SOM_POOL = "somPool";
    public static final String WS_CLIENT_REQ_PROCESSOR_POOL = "wsConnAcceptPool";
    public static final String WS_OUTGOING_DATA_SENDER_POOL = "wsOutgoingDataPool";
    public static final String CONFIG_POOL = "configPool";
    public static final String PATCH_POOL = "patchPool";
    public static final String SOM_COMMON_POOL = "somCommonPool";
    public static final String REPORTS_POOL = "asynchThreadPool";
    public static final String EXEHOURS = "exeHours";
    public static final String EXEMINUTES = "exeMinutes";
    public static final String EXESECONDS = "exeSeconds";
    public static final String STARTYEAR = "startYear";
    public static final String STARTMONTH = "startMonth";
    public static final String STARTDATE = "startDate";
    public static final String SCHEDTYPE = "schedType";
    public static final String ONCE = "Once";
    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";
    public static final String HOURLY = "Hourly";
    public static final String DAILYINTERVALTYPE = "dailyIntervalType";
    public static final String EVERYDAY = "everyDay";
    public static final String WEEKDAYS = "weekDays";
    public static final String ALTERNATIVE_DAYS = "alternativeDays";
    public static final String DAYSOFWEEK = "daysOfWeek";
    public static final String MONTHS = "months";
    public static final String MONTHLYPERFORM = "monthlyPerform";
    public static final String DAY = "Day";
    public static final String WEEKDAY = "WeekDay";
    public static final String DATES = "dates";
    public static final String MONTHLYWEEKDAY = "monthlyWeekDay";
    public static final String MONTHLYWEEKNUM = "monthlyWeekNum";
    public static final String SCHEDULERDISABLED = "schedulerDisabled";
    public static final String SCHEDULER_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String SCHEDULER_TIME_ZONE = "taskTimeZone";
    
    static {
        ENABLE = Boolean.TRUE;
        DISABLE = Boolean.FALSE;
    }
}
