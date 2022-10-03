package com.btr.proxy.util;

public class PlatformUtil
{
    public static Platform getCurrentPlattform() {
        final String osName = System.getProperty("os.name");
        Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detecting platform. Name is: {0}", osName);
        if (osName.toLowerCase().contains("windows")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Windows platform: {0}", osName);
            return Platform.WIN;
        }
        if (osName.toLowerCase().contains("linux")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Linux platform: {0}", osName);
            return Platform.LINUX;
        }
        if (osName.startsWith("Mac OS")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Mac OS platform: {0}", osName);
            return Platform.MAC_OS;
        }
        if (osName.startsWith("SunOS")) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Solaris platform: {0}", osName);
            return Platform.SOLARIS;
        }
        return Platform.OTHER;
    }
    
    public static Browser getDefaultBrowser() {
        if (getCurrentPlattform() == Platform.WIN) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Browser is InternetExplorer", new Object[0]);
            return Browser.IE;
        }
        Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Browser Firefox. Fallback?", new Object[0]);
        return Browser.FIREFOX;
    }
    
    public static Desktop getCurrentDesktop() {
        final Platform platf = getCurrentPlattform();
        if (platf == Platform.WIN) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Windows desktop", new Object[0]);
            return Desktop.WIN;
        }
        if (platf == Platform.MAC_OS) {
            Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Mac OS desktop", new Object[0]);
            return Desktop.MAC_OS;
        }
        if (platf == Platform.LINUX || platf == Platform.SOLARIS || platf == Platform.OTHER) {
            if (isKDE()) {
                Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected KDE desktop", new Object[0]);
                return Desktop.KDE;
            }
            if (isGnome()) {
                Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Gnome desktop", new Object[0]);
                return Desktop.GNOME;
            }
        }
        Logger.log(PlatformUtil.class, Logger.LogLevel.TRACE, "Detected Unknown desktop", new Object[0]);
        return Desktop.OTHER;
    }
    
    private static boolean isGnome() {
        return System.getenv("GNOME_DESKTOP_SESSION_ID") != null;
    }
    
    private static boolean isKDE() {
        return System.getenv("KDE_SESSION_VERSION") != null;
    }
    
    public enum Platform
    {
        WIN, 
        LINUX, 
        MAC_OS, 
        SOLARIS, 
        OTHER;
    }
    
    public enum Desktop
    {
        WIN, 
        KDE, 
        GNOME, 
        MAC_OS, 
        OTHER;
    }
    
    public enum Browser
    {
        IE, 
        FIREFOX;
    }
}
