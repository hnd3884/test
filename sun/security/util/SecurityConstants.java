package sun.security.util;

import java.security.Permission;
import java.net.SocketPermission;
import java.security.SecurityPermission;
import java.net.NetPermission;
import java.security.AllPermission;

public final class SecurityConstants
{
    public static final String FILE_DELETE_ACTION = "delete";
    public static final String FILE_EXECUTE_ACTION = "execute";
    public static final String FILE_READ_ACTION = "read";
    public static final String FILE_WRITE_ACTION = "write";
    public static final String FILE_READLINK_ACTION = "readlink";
    public static final String SOCKET_RESOLVE_ACTION = "resolve";
    public static final String SOCKET_CONNECT_ACTION = "connect";
    public static final String SOCKET_LISTEN_ACTION = "listen";
    public static final String SOCKET_ACCEPT_ACTION = "accept";
    public static final String SOCKET_CONNECT_ACCEPT_ACTION = "connect,accept";
    public static final String PROPERTY_RW_ACTION = "read,write";
    public static final String PROPERTY_READ_ACTION = "read";
    public static final String PROPERTY_WRITE_ACTION = "write";
    public static final AllPermission ALL_PERMISSION;
    public static final NetPermission SPECIFY_HANDLER_PERMISSION;
    public static final NetPermission SET_PROXYSELECTOR_PERMISSION;
    public static final NetPermission GET_PROXYSELECTOR_PERMISSION;
    public static final NetPermission SET_COOKIEHANDLER_PERMISSION;
    public static final NetPermission GET_COOKIEHANDLER_PERMISSION;
    public static final NetPermission SET_RESPONSECACHE_PERMISSION;
    public static final NetPermission GET_RESPONSECACHE_PERMISSION;
    public static final NetPermission SET_SOCKETIMPL_PERMISSION;
    public static final RuntimePermission CREATE_CLASSLOADER_PERMISSION;
    public static final RuntimePermission CHECK_MEMBER_ACCESS_PERMISSION;
    public static final RuntimePermission MODIFY_THREAD_PERMISSION;
    public static final RuntimePermission MODIFY_THREADGROUP_PERMISSION;
    public static final RuntimePermission GET_PD_PERMISSION;
    public static final RuntimePermission GET_CLASSLOADER_PERMISSION;
    public static final RuntimePermission STOP_THREAD_PERMISSION;
    public static final RuntimePermission GET_STACK_TRACE_PERMISSION;
    public static final SecurityPermission CREATE_ACC_PERMISSION;
    public static final SecurityPermission GET_COMBINER_PERMISSION;
    public static final SecurityPermission GET_POLICY_PERMISSION;
    public static final SocketPermission LOCAL_LISTEN_PERMISSION;
    public static final Double PROVIDER_VER;
    
    private SecurityConstants() {
    }
    
    static {
        ALL_PERMISSION = new AllPermission();
        SPECIFY_HANDLER_PERMISSION = new NetPermission("specifyStreamHandler");
        SET_PROXYSELECTOR_PERMISSION = new NetPermission("setProxySelector");
        GET_PROXYSELECTOR_PERMISSION = new NetPermission("getProxySelector");
        SET_COOKIEHANDLER_PERMISSION = new NetPermission("setCookieHandler");
        GET_COOKIEHANDLER_PERMISSION = new NetPermission("getCookieHandler");
        SET_RESPONSECACHE_PERMISSION = new NetPermission("setResponseCache");
        GET_RESPONSECACHE_PERMISSION = new NetPermission("getResponseCache");
        SET_SOCKETIMPL_PERMISSION = new NetPermission("setSocketImpl");
        CREATE_CLASSLOADER_PERMISSION = new RuntimePermission("createClassLoader");
        CHECK_MEMBER_ACCESS_PERMISSION = new RuntimePermission("accessDeclaredMembers");
        MODIFY_THREAD_PERMISSION = new RuntimePermission("modifyThread");
        MODIFY_THREADGROUP_PERMISSION = new RuntimePermission("modifyThreadGroup");
        GET_PD_PERMISSION = new RuntimePermission("getProtectionDomain");
        GET_CLASSLOADER_PERMISSION = new RuntimePermission("getClassLoader");
        STOP_THREAD_PERMISSION = new RuntimePermission("stopThread");
        GET_STACK_TRACE_PERMISSION = new RuntimePermission("getStackTrace");
        CREATE_ACC_PERMISSION = new SecurityPermission("createAccessControlContext");
        GET_COMBINER_PERMISSION = new SecurityPermission("getDomainCombiner");
        GET_POLICY_PERMISSION = new SecurityPermission("getPolicy");
        LOCAL_LISTEN_PERMISSION = new SocketPermission("localhost:0", "listen");
        PROVIDER_VER = 1.8;
    }
    
    public static class AWT
    {
        private static final String AWTFactory = "sun.awt.AWTPermissionFactory";
        private static final PermissionFactory<?> factory;
        public static final Permission TOPLEVEL_WINDOW_PERMISSION;
        public static final Permission ACCESS_CLIPBOARD_PERMISSION;
        public static final Permission CHECK_AWT_EVENTQUEUE_PERMISSION;
        public static final Permission TOOLKIT_MODALITY_PERMISSION;
        public static final Permission READ_DISPLAY_PIXELS_PERMISSION;
        public static final Permission CREATE_ROBOT_PERMISSION;
        public static final Permission WATCH_MOUSE_PERMISSION;
        public static final Permission SET_WINDOW_ALWAYS_ON_TOP_PERMISSION;
        public static final Permission ALL_AWT_EVENTS_PERMISSION;
        public static final Permission ACCESS_SYSTEM_TRAY_PERMISSION;
        
        private AWT() {
        }
        
        private static PermissionFactory<?> permissionFactory() {
            Class<?> forName;
            try {
                forName = Class.forName("sun.awt.AWTPermissionFactory", false, AWT.class.getClassLoader());
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
            try {
                return (PermissionFactory<?>)forName.newInstance();
            }
            catch (final ReflectiveOperationException ex2) {
                throw new InternalError(ex2);
            }
        }
        
        private static Permission newAWTPermission(final String s) {
            return (Permission)((AWT.factory == null) ? null : AWT.factory.newPermission(s));
        }
        
        static {
            factory = permissionFactory();
            TOPLEVEL_WINDOW_PERMISSION = newAWTPermission("showWindowWithoutWarningBanner");
            ACCESS_CLIPBOARD_PERMISSION = newAWTPermission("accessClipboard");
            CHECK_AWT_EVENTQUEUE_PERMISSION = newAWTPermission("accessEventQueue");
            TOOLKIT_MODALITY_PERMISSION = newAWTPermission("toolkitModality");
            READ_DISPLAY_PIXELS_PERMISSION = newAWTPermission("readDisplayPixels");
            CREATE_ROBOT_PERMISSION = newAWTPermission("createRobot");
            WATCH_MOUSE_PERMISSION = newAWTPermission("watchMousePointer");
            SET_WINDOW_ALWAYS_ON_TOP_PERMISSION = newAWTPermission("setWindowAlwaysOnTop");
            ALL_AWT_EVENTS_PERMISSION = newAWTPermission("listenToAllAWTEvents");
            ACCESS_SYSTEM_TRAY_PERMISSION = newAWTPermission("accessSystemTray");
        }
    }
}
