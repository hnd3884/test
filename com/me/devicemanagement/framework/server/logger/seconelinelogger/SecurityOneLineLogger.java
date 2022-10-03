package com.me.devicemanagement.framework.server.logger.seconelinelogger;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.JSONObject;
import java.util.logging.Level;
import java.text.SimpleDateFormat;

public class SecurityOneLineLogger
{
    private static boolean isSecOneLineEnabled;
    
    public static String formatTime(final Long currentMillis) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("[HH:mm:ss:SSS]|[MM-dd-yyyy]");
        return dateFormatter.format(currentMillis);
    }
    
    public static void log(final String module, final String operation, final String message, final Level level) {
        final Object[] objects = { message };
        logg(module, operation, objects, level);
    }
    
    public static void log(final String module, final String operation, final String[] msgs, final Level level) {
        logg(module, operation, msgs, level);
    }
    
    public static void log(final String module, final String operation, final String message, final Object[] params, final Level level) {
        final Object[] objects = { message, params };
        logg(module, operation, objects, level);
    }
    
    public static void log(final String module, final String operation, final JSONObject message, final Level level) {
        logg(module, operation, message, level);
    }
    
    private static void logg(final String module, final String operation, final Object message, final Level level) {
        if (SecurityOneLineLogger.isSecOneLineEnabled) {
            final Map<String, String> metadata = new HashMap<String, String>();
            metadata.put("MODULE", module);
            metadata.put("OPERATION", operation);
            metadata.put("DONE_BY", OneLineLoggerThreadLocal.getUserIdVal());
            metadata.put("WITH_UMROLE", OneLineLoggerThreadLocal.getLoggedInRoleNameFromCache(OneLineLoggerThreadLocal.getUserIdVal()));
            metadata.put("REMOTE_IP", OneLineLoggerThreadLocal.getIpAddress());
            metadata.put("SESSION_ID", OneLineLoggerThreadLocal.getSessionId());
            log(metadata, message, level);
        }
    }
    
    public static void log(final HttpServletRequest servletRequest, final String module, final String operation, final JSONObject message, final Level level) {
        logg(servletRequest, module, operation, message, level);
    }
    
    public static void log(final HttpServletRequest servletRequest, final String module, final String operation, final String[] msgs, final Level level) {
        logg(servletRequest, module, operation, msgs, level);
    }
    
    public static void log(final HttpServletRequest servletRequest, final String module, final String operation, final String message, final Object[] params, final Level level) {
        final Object[] objects = { message, params };
        logg(servletRequest, module, operation, objects, level);
    }
    
    private static void logg(final HttpServletRequest request, final String module, final String operation, final Object message, final Level level) {
        if (SecurityOneLineLogger.isSecOneLineEnabled) {
            OneLineLoggerThreadLocal.setOnelineLoggerDetails((ServletRequest)request);
            logg(module, operation, message, level);
            OneLineLoggerThreadLocal.clearOnelineLoggerThreadLocalDetails();
        }
    }
    
    private static void log(final Map metadata, final Object message, final Level level) {
        ApiFactoryProvider.getSecOnlineLogAPI().getLogger(String.valueOf(metadata.get("MODULE"))).log(level, "SecurityOnelineLogger", new Object[] { metadata, message });
    }
    
    static {
        SecurityOneLineLogger.isSecOneLineEnabled = ApiFactoryProvider.getSecOnlineLogAPI().isSecurityLoggerEnabled();
    }
}
