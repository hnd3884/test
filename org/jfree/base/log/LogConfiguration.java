package org.jfree.base.log;

import org.jfree.base.BaseBoot;

public class LogConfiguration
{
    public static final String DISABLE_LOGGING_DEFAULT = "false";
    public static final String LOGLEVEL = "org.jfree.base.LogLevel";
    public static final String LOGLEVEL_DEFAULT = "Info";
    public static final String LOGTARGET = "org.jfree.base.LogTarget";
    public static final String LOGTARGET_DEFAULT;
    public static final String DISABLE_LOGGING = "org.jfree.base.NoDefaultDebug";
    static /* synthetic */ Class class$org$jfree$util$PrintStreamLogTarget;
    
    static {
        LOGTARGET_DEFAULT = ((LogConfiguration.class$org$jfree$util$PrintStreamLogTarget != null) ? LogConfiguration.class$org$jfree$util$PrintStreamLogTarget : (LogConfiguration.class$org$jfree$util$PrintStreamLogTarget = class$("org.jfree.util.PrintStreamLogTarget"))).getName();
    }
    
    private LogConfiguration() {
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static String getLogLevel() {
        return BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.base.LogLevel", "Info");
    }
    
    public static String getLogTarget() {
        return BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.base.LogTarget", LogConfiguration.LOGTARGET_DEFAULT);
    }
    
    public static boolean isDisableLogging() {
        return BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.base.NoDefaultDebug", "false").equalsIgnoreCase("true");
    }
    
    public static void setDisableLogging(final boolean disableLogging) {
        BaseBoot.getConfiguration().setConfigProperty("org.jfree.base.NoDefaultDebug", String.valueOf(disableLogging));
    }
    
    public static void setLogLevel(final String level) {
        BaseBoot.getConfiguration().setConfigProperty("org.jfree.base.LogLevel", level);
    }
    
    public static void setLogTarget(final String logTarget) {
        BaseBoot.getConfiguration().setConfigProperty("org.jfree.base.LogTarget", logTarget);
    }
}
