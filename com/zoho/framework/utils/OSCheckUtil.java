package com.zoho.framework.utils;

import java.util.Locale;

public class OSCheckUtil
{
    private static String osName;
    private static int currentOS;
    public static final int UNIX = 1;
    public static final int WINDOWS = 2;
    public static final int OTHERS = 0;
    
    protected OSCheckUtil() {
    }
    
    public static int getOS() {
        return OSCheckUtil.currentOS;
    }
    
    public static String getOSName() {
        return OSCheckUtil.osName;
    }
    
    public static boolean isWindows(final int osConstant) {
        return osConstant == 2;
    }
    
    public static String getSimpleOSName(final String osname) {
        if (osname.toLowerCase(Locale.ENGLISH).contains("windows")) {
            return "windows";
        }
        if (osname.toLowerCase(Locale.ENGLISH).startsWith("mac")) {
            return "mac";
        }
        return "linux";
    }
    
    static {
        OSCheckUtil.osName = System.getProperty("os.name").trim().toLowerCase(Locale.ENGLISH);
        if (OSCheckUtil.osName.indexOf("windows") >= 0) {
            OSCheckUtil.currentOS = 2;
        }
        else if (OSCheckUtil.osName.equals("linux") || OSCheckUtil.osName.equalsIgnoreCase("sunos") || OSCheckUtil.osName.contains("mac os")) {
            OSCheckUtil.currentOS = 1;
        }
        else {
            OSCheckUtil.currentOS = 0;
        }
    }
}
