package com.me.devicemanagement.onpremise.server.clock;

public class SNTPConstants
{
    public static int DEFAULT_TIMEOUT;
    public static String LOCAL_TIME_LONG;
    public static String INTERNET_TIME_LONG;
    public static String NTP_HOST;
    public static String TIME_DIFFERENCE_LONG;
    public static String LOCAL_TIME_DATE_FORMAT;
    public static String INTERNET_TIME_DATE_FORMAT;
    public static String LOCAL_TIME_PERSONALIZED_FORMAT;
    public static String INTERNET_TIME_PERSONALIZED_FORMAT;
    public static String DIFFERENCE_IN_MINUTES;
    public static String DIFFERENCE_IN_SECONDS;
    public static String DIFFERENCE_IN_HOURS;
    public static String DIFFERENCE_IN_DAYS;
    public static String DIFFERENCE_IN_MILLI_SECONDS;
    public static String DIFFERENCE_TYPE;
    public static String DIFFERENCE_TYPE_CLOCK_BEHIND;
    public static String DIFFERENCE_TYPE_CLOCK_AHEAD;
    public static String DIFFERENCE_TYPE_CLOCK_EQUAL;
    public static String MESSAGE;
    
    static {
        SNTPConstants.DEFAULT_TIMEOUT = 10000;
        SNTPConstants.LOCAL_TIME_LONG = "long_local_time";
        SNTPConstants.INTERNET_TIME_LONG = "long_internet_time";
        SNTPConstants.NTP_HOST = "ntp_host";
        SNTPConstants.TIME_DIFFERENCE_LONG = "time_difference";
        SNTPConstants.LOCAL_TIME_DATE_FORMAT = "formatted_local_time";
        SNTPConstants.INTERNET_TIME_DATE_FORMAT = "formatted_internet_time";
        SNTPConstants.LOCAL_TIME_PERSONALIZED_FORMAT = "personalized_format_local_time";
        SNTPConstants.INTERNET_TIME_PERSONALIZED_FORMAT = "personalized_format_internet_time";
        SNTPConstants.DIFFERENCE_IN_MINUTES = "differed_minutes";
        SNTPConstants.DIFFERENCE_IN_SECONDS = "differed_seconds";
        SNTPConstants.DIFFERENCE_IN_HOURS = "differed_hours";
        SNTPConstants.DIFFERENCE_IN_DAYS = "differed_days";
        SNTPConstants.DIFFERENCE_IN_MILLI_SECONDS = "differed_milliseconds";
        SNTPConstants.DIFFERENCE_TYPE = "differed_type";
        SNTPConstants.DIFFERENCE_TYPE_CLOCK_BEHIND = "clock_behind";
        SNTPConstants.DIFFERENCE_TYPE_CLOCK_AHEAD = "clock_ahead";
        SNTPConstants.DIFFERENCE_TYPE_CLOCK_EQUAL = "clock_time_equal";
        SNTPConstants.MESSAGE = "message";
    }
}
