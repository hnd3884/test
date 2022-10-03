package com.me.mdm.api.factory;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Logger;
import com.me.mdm.api.internaltool.InternalToolInterface;
import com.me.mdm.api.metainfo.UserMetaAPI;
import com.me.mdm.api.metainfo.ProductMetaAPI;
import com.me.mdm.api.support.MDMSupportFacade;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.server.user.TechniciansFacade;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.customgroup.UserGroupFacade;
import com.me.mdm.server.user.ManagedUserFacade;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class MDMRestAPIFactoryProvider extends ApiFactoryProvider
{
    private static ManagedUserFacade managedUserFacade;
    private static UserGroupFacade userGroupFacade;
    private static AppFacade appFacade;
    private static TechniciansFacade techniciansFacade;
    private static EnrollmentFacade enrollmentFacade;
    private static MDMSupportFacade mdmSupportFacade;
    private static ProductMetaAPI productMetaAPI;
    private static UserMetaAPI userMetaAPI;
    private static InternalToolInterface internalToolInterface;
    private static Logger logger;
    private static AppsUtil appsUtilAPI;
    private static MDMAppMgmtHandler mdmAppMgmtHandler;
    
    public static ManagedUserFacade getManagedUserFacade() {
        if (MDMRestAPIFactoryProvider.managedUserFacade == null) {
            MDMRestAPIFactoryProvider.managedUserFacade = (ManagedUserFacade)getImplClassInstance("USER_FACADE_IMPL");
        }
        return MDMRestAPIFactoryProvider.managedUserFacade;
    }
    
    public static UserGroupFacade getUserGroupFacade() {
        if (MDMRestAPIFactoryProvider.userGroupFacade == null) {
            MDMRestAPIFactoryProvider.userGroupFacade = (UserGroupFacade)getImplClassInstance("USER_GROUP_FACADE_IMPL");
        }
        return MDMRestAPIFactoryProvider.userGroupFacade;
    }
    
    public static AppFacade getAppFacade() {
        if (MDMRestAPIFactoryProvider.appFacade == null) {
            MDMRestAPIFactoryProvider.appFacade = (AppFacade)getImplClassInstance("APP_FACADE_IMPL");
        }
        return MDMRestAPIFactoryProvider.appFacade;
    }
    
    public static TechniciansFacade getTechnicianFacade() {
        if (MDMRestAPIFactoryProvider.techniciansFacade == null) {
            MDMRestAPIFactoryProvider.techniciansFacade = (TechniciansFacade)getImplClassInstance("TECHNICIAN_FACADE_IMPL");
        }
        return MDMRestAPIFactoryProvider.techniciansFacade;
    }
    
    public static EnrollmentFacade getEnrollmentFacade() {
        if (MDMRestAPIFactoryProvider.enrollmentFacade == null) {
            MDMRestAPIFactoryProvider.enrollmentFacade = (EnrollmentFacade)getImplClassInstance("ENROLLMENT_FACADE_IMPL");
        }
        return MDMRestAPIFactoryProvider.enrollmentFacade;
    }
    
    public static APIUtil getAPIUtil() {
        return (APIUtil)getImplClassInstance("API_UTIL_IMPL");
    }
    
    public static MDMSupportFacade getMdmSupportFacade() {
        if (MDMRestAPIFactoryProvider.mdmSupportFacade == null) {
            MDMRestAPIFactoryProvider.mdmSupportFacade = (MDMSupportFacade)getImplClassInstance("SUPPORT_FACADE_IMPL");
        }
        return MDMRestAPIFactoryProvider.mdmSupportFacade;
    }
    
    public static ProductMetaAPI getProductMetaAPI() {
        if (MDMRestAPIFactoryProvider.productMetaAPI == null) {
            MDMRestAPIFactoryProvider.productMetaAPI = (ProductMetaAPI)getImplClassInstance("DM_PRODUCT_META_API_CLASS");
        }
        return MDMRestAPIFactoryProvider.productMetaAPI;
    }
    
    public static UserMetaAPI getUserMetaAPI() {
        if (MDMRestAPIFactoryProvider.userMetaAPI == null) {
            MDMRestAPIFactoryProvider.userMetaAPI = (UserMetaAPI)getImplClassInstance("DM_USER_META_API_CLASS");
        }
        return MDMRestAPIFactoryProvider.userMetaAPI;
    }
    
    public static InternalToolInterface getBaseInternalToolHandler() {
        if (!checkForDevMode()) {
            MDMRestAPIFactoryProvider.logger.log(Level.WARNING, "This API request is forbidden/not accessible");
            throw new APIHTTPException("COM0001", new Object[0]);
        }
        if (MDMRestAPIFactoryProvider.internalToolInterface == null) {
            MDMRestAPIFactoryProvider.internalToolInterface = (InternalToolInterface)getImplClassInstance("INTERNAL_TOOL_API_IMPL");
        }
        return MDMRestAPIFactoryProvider.internalToolInterface;
    }
    
    private static boolean checkForDevMode() {
        try {
            final Object developerMode = DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"developer_mode", "PARAM_VALUE");
            return developerMode != null && Boolean.valueOf(developerMode.toString());
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public static AppsUtil getAppsUtilAPI() {
        if (MDMRestAPIFactoryProvider.appsUtilAPI == null) {
            MDMRestAPIFactoryProvider.appsUtilAPI = (AppsUtil)getImplClassInstance("APP_UTIL_IMPL");
        }
        return MDMRestAPIFactoryProvider.appsUtilAPI;
    }
    
    public static MDMAppMgmtHandler getMdmAppMgmtHandlerAPI() {
        if (MDMRestAPIFactoryProvider.mdmAppMgmtHandler == null) {
            MDMRestAPIFactoryProvider.mdmAppMgmtHandler = (MDMAppMgmtHandler)getImplClassInstance("MDM_APP_MGMT_HANDLER_IMPL");
        }
        return MDMRestAPIFactoryProvider.mdmAppMgmtHandler;
    }
    
    static {
        MDMRestAPIFactoryProvider.managedUserFacade = null;
        MDMRestAPIFactoryProvider.userGroupFacade = null;
        MDMRestAPIFactoryProvider.appFacade = null;
        MDMRestAPIFactoryProvider.techniciansFacade = null;
        MDMRestAPIFactoryProvider.enrollmentFacade = null;
        MDMRestAPIFactoryProvider.mdmSupportFacade = null;
        MDMRestAPIFactoryProvider.productMetaAPI = null;
        MDMRestAPIFactoryProvider.userMetaAPI = null;
        MDMRestAPIFactoryProvider.internalToolInterface = null;
        MDMRestAPIFactoryProvider.logger = Logger.getLogger("MDMLogger");
        MDMRestAPIFactoryProvider.appsUtilAPI = null;
        MDMRestAPIFactoryProvider.mdmAppMgmtHandler = null;
    }
}
