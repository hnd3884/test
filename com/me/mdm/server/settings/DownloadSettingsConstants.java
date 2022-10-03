package com.me.mdm.server.settings;

public class DownloadSettingsConstants
{
    public static final String DISABLE_RETRY_SCHEDULER_IN_AGENT = "DisableRetrySchedulerInAgent";
    
    public static final class DefaultValues
    {
        public static final int MAX_RETRY_COUNT = 3;
        public static final Long MIN_RETRY_DELAY;
        public static final Long MAX_RETRY_DELAY;
        public static final Long DELAY_RANDOM;
        public static final Long CUSTOM_RETRY_DELAY;
        public static final String EXCLUDED_DOMAIN = "";
        
        static {
            MIN_RETRY_DELAY = 600L;
            MAX_RETRY_DELAY = 1200L;
            DELAY_RANDOM = 60L;
            CUSTOM_RETRY_DELAY = 1800L;
        }
    }
    
    public static final class CommandKeyConstants
    {
        public static final String MAX_RETRY_COUNT = "MaxRetryCount";
        public static final String MIN_RETRY_DELAY = "MinRetryDelay";
        public static final String MAX_RETRY_DELAY = "MaxRetryDelay";
        public static final String EXCLUDED_DOMAIN = "RestrictedDomainsFromRetry";
        public static final String DELAY_RANDOM = "DelayRandomness";
        public static final String CUSTOM_RETRY_DELAY = "CustomRetryDelay";
        public static final String SHOULD_RETRY_DOWNLOAD = "ShouldRetryDownload";
    }
}
