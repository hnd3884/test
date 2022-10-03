package com.unboundid.ldap.sdk.unboundidds.tasks;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum FileRetentionTaskTimestampFormat
{
    GENERALIZED_TIME_UTC_WITH_MILLISECONDS(true, "yyyyMMddHHmmss.SSS'Z'", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9]\\.[0-9][0-9][0-9]Z)"), 
    GENERALIZED_TIME_UTC_WITH_SECONDS(true, "yyyyMMddHHmmss'Z'", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9]Z)"), 
    GENERALIZED_TIME_UTC_WITH_MINUTES(true, "yyyyMMddHHmm'Z'", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])[0-5][0-9]Z)"), 
    LOCAL_TIME_WITH_MILLISECONDS(false, "yyyyMMddHHmmss.SSS", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9]\\.[0-9][0-9][0-9])"), 
    LOCAL_TIME_WITH_SECONDS(false, "yyyyMMddHHmmss", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9])"), 
    LOCAL_TIME_WITH_MINUTES(false, "yyyyMMddHHmm", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])[0-5][0-9])"), 
    LOCAL_DATE(false, "yyyyMMdd", "((19|20|21)[0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))");
    
    private static final String REGEX_FRAGMENT_BEGIN_CAPTURE_GROUP = "(";
    private static final String REGEX_FRAGMENT_YEAR = "(19|20|21)[0-9][0-9]";
    private static final String REGEX_FRAGMENT_MONTH = "(0[1-9]|1[0-2])";
    private static final String REGEX_FRAGMENT_DAY = "(0[1-9]|[1-2][0-9]|3[0-1])";
    private static final String REGEX_FRAGMENT_HOUR = "([0-1][0-9]|2[0-3])";
    private static final String REGEX_FRAGMENT_MINUTE = "[0-5][0-9]";
    private static final String REGEX_FRAGMENT_SECOND = "[0-5][0-9]";
    private static final String REGEX_FRAGMENT_MILLISECOND = "\\.[0-9][0-9][0-9]";
    private static final String REGEX_FRAGMENT_LITERAL_Z = "Z";
    private static final String REGEX_FRAGMENT_END_CAPTURE_GROUP = ")";
    private final boolean isInUTCTimeZone;
    private final String simpleDateFormatString;
    private final String regexString;
    
    private FileRetentionTaskTimestampFormat(final boolean isInUTCTimeZone, final String simpleDateFormatString, final String regexString) {
        this.isInUTCTimeZone = isInUTCTimeZone;
        this.simpleDateFormatString = simpleDateFormatString;
        this.regexString = regexString;
    }
    
    public boolean isInUTCTimeZone() {
        return this.isInUTCTimeZone;
    }
    
    public String getSimpleDateFormatString() {
        return this.simpleDateFormatString;
    }
    
    public String getRegexString() {
        return this.regexString;
    }
    
    public static FileRetentionTaskTimestampFormat forName(final String name) {
        final String upperName = StaticUtils.toUpperCase(name).replace('-', '_');
        for (final FileRetentionTaskTimestampFormat f : values()) {
            if (f.name().equals(upperName)) {
                return f;
            }
        }
        return null;
    }
}
