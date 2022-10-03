package com.me.mdm.chrome.agent.utils;

public class ChromeAgentUtil
{
    public static Object opt(final Object value, final Object defaultValue) {
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
    
    public static String optString(final String value, final String defaultValue) throws Exception {
        return opt(value, defaultValue).toString();
    }
    
    public static Long optLong(final Long value, final Long defaultValue) throws Exception {
        return (Long)opt(value, defaultValue);
    }
}
