package com.zoho.tools.util;

import java.util.Locale;

public class OSCheckUtil
{
    private static final String OS_NAME;
    
    public static boolean isWindowsPlatform() {
        return OSCheckUtil.OS_NAME.toLowerCase(Locale.ENGLISH).contains("windows");
    }
    
    public static boolean isMacPlatform() {
        return !isWindowsPlatform() && !OSCheckUtil.OS_NAME.toLowerCase(Locale.ENGLISH).contains("linux");
    }
    
    public static boolean is64BitArchitecture() {
        return System.getProperty("sun.arch.data.model").equals("64");
    }
    
    static {
        OS_NAME = System.getProperty("os.name").trim();
    }
}
