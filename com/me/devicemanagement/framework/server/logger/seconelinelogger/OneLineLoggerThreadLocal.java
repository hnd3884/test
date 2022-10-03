package com.me.devicemanagement.framework.server.logger.seconelinelogger;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.ServletRequest;
import java.util.logging.Logger;

public class OneLineLoggerThreadLocal
{
    static Logger out;
    static ThreadLocal<String> userId;
    static ThreadLocal<String> sessionId;
    static ThreadLocal<String> ipAddress;
    private static String role_cache_name;
    private static int timeout_umrolecache;
    private static boolean isSecOneLineEnabled;
    
    public static String getSessionId() {
        return OneLineLoggerThreadLocal.sessionId.get();
    }
    
    public static String getUserIdVal() {
        return OneLineLoggerThreadLocal.userId.get();
    }
    
    public static void setOnelineLoggerDetails(final ServletRequest request) {
        if (OneLineLoggerThreadLocal.isSecOneLineEnabled) {
            setUserId(getUserId());
            setSessionId(ApiFactoryProvider.getSecOnlineLogAPI().getSessionUniqId(request));
            setIpAddress(request.getRemoteAddr());
        }
    }
    
    private static String getUserId() {
        String user = "";
        try {
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId != null && userId != -1L) {
                user = String.valueOf(userId);
            }
        }
        catch (final Exception exception) {
            OneLineLoggerThreadLocal.out.log(Level.SEVERE, exception.getMessage());
        }
        return user;
    }
    
    public static void setSessionId(final String sessId) {
        OneLineLoggerThreadLocal.sessionId.set(sessId);
    }
    
    public static void setIpAddress(final String ip_Address) {
        OneLineLoggerThreadLocal.ipAddress.set(ip_Address);
    }
    
    public static void setUserId(final String user_Id) {
        OneLineLoggerThreadLocal.userId.set(user_Id);
    }
    
    public static String getIpAddress() {
        return OneLineLoggerThreadLocal.ipAddress.get();
    }
    
    public static void clearOnelineLoggerThreadLocalDetails() {
        OneLineLoggerThreadLocal.sessionId.remove();
        OneLineLoggerThreadLocal.ipAddress.remove();
        OneLineLoggerThreadLocal.userId.remove();
    }
    
    public static String getLoggedInRoleNameFromCache(final String userId) {
        String roleName = null;
        if (userId != null) {
            roleName = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(OneLineLoggerThreadLocal.role_cache_name + userId, 3);
            if (roleName == null) {
                roleName = updateRoleNameInCache(userId);
            }
        }
        return roleName;
    }
    
    private static String updateRoleNameInCache(final String userId) {
        String roleName = null;
        if (userId != null && !userId.isEmpty()) {
            final Long user_id = Long.parseLong(userId);
            roleName = DMUserHandler.getRoleForUser(DMUserHandler.getLoginIdForUserId(user_id));
            if (roleName != null) {
                ApiFactoryProvider.getCacheAccessAPI().putCache(OneLineLoggerThreadLocal.role_cache_name + userId, roleName, 3, OneLineLoggerThreadLocal.timeout_umrolecache * 60 * 60);
            }
        }
        return roleName;
    }
    
    public static void invalidateRoleNameInCache(final String user_id) {
        ApiFactoryProvider.getCacheAccessAPI().removeCache(OneLineLoggerThreadLocal.role_cache_name + user_id, 3);
    }
    
    public static boolean isSecOneLineEnabled() {
        return OneLineLoggerThreadLocal.isSecOneLineEnabled;
    }
    
    static {
        OneLineLoggerThreadLocal.out = Logger.getLogger(OneLineLoggerThreadLocal.class.getName());
        OneLineLoggerThreadLocal.userId = new ThreadLocal<String>();
        OneLineLoggerThreadLocal.sessionId = new ThreadLocal<String>();
        OneLineLoggerThreadLocal.ipAddress = new ThreadLocal<String>();
        OneLineLoggerThreadLocal.role_cache_name = "USERROLE_INCACHE";
        OneLineLoggerThreadLocal.timeout_umrolecache = 24;
        OneLineLoggerThreadLocal.isSecOneLineEnabled = ApiFactoryProvider.getSecOnlineLogAPI().isSecurityLoggerEnabled();
    }
}
