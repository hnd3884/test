package com.me.devicemanagement.onpremise.server.factory;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.onpremise.security.securegatewayserver.proxy.SGSProxyFileDataDefaultImpl;
import com.me.ems.onpremise.security.securitysettings.SecurityEnforcementAPI;
import com.me.ems.onpremise.uac.core.UserManagementAPI;
import com.me.ems.onpremise.uac.api.v1.service.factory.TFAService;
import com.me.devicemanagement.onpremise.webclient.configurations.LoginHandler;
import com.me.ems.onpremise.common.core.RequestDemoHandler;
import com.me.devicemanagement.onpremise.server.util.AvTestEligibilityHandler;
import com.me.devicemanagement.onpremise.server.admin.DMZendeskAPI;
import com.me.devicemanagement.onpremise.server.metrack.EvaluationTrackHandler;
import java.util.logging.Logger;

public class ApiFactoryProvider extends com.me.devicemanagement.framework.server.factory.ApiFactoryProvider
{
    public static final String DM_EVALUATION_TRACK_HANDLER_CLASS = "DM_EVALUATION_TRACK_HANDLER_CLASS";
    public static final String DM_ANTIVIRUS_DETECTION_UTIL_CLASS = "DM_ANTIVIRUS_DETECTION_UTIL_CLASS";
    public static final String DM_REQUEST_DEMO_HANDLER_CLASS = "DM_REQUEST_DEMO_HANDLER_CLASS";
    private static final Logger LOGGER;
    private static EvaluationTrackHandler evaluationTrackHandler;
    private static DMZendeskAPI dmZendeskAPI;
    private static AvTestEligibilityHandler avTestEligibilityAPI;
    private static RequestDemoHandler requestDemoHandler;
    private static LoginHandler loginHandler;
    private static TFAService tfaService;
    private static UserManagementAPI userManagementAPI;
    private static SecurityEnforcementAPI securityEnforcementAPI;
    private static SGSProxyFileDataDefaultImpl sgsProxyFileDataDefaultImpl;
    
    public static EvaluationTrackHandler getEvaluationTrackHandler() {
        if (ApiFactoryProvider.evaluationTrackHandler == null) {
            ApiFactoryProvider.evaluationTrackHandler = (EvaluationTrackHandler)getImplClassInstance("DM_EVALUATION_TRACK_HANDLER_CLASS");
        }
        return ApiFactoryProvider.evaluationTrackHandler;
    }
    
    public static DMZendeskAPI getZendeskAPI() {
        if (ApiFactoryProvider.dmZendeskAPI == null) {
            ApiFactoryProvider.dmZendeskAPI = (DMZendeskAPI)getImplClassInstance("DM_ZENDESK_CLASS");
        }
        return ApiFactoryProvider.dmZendeskAPI;
    }
    
    public static AvTestEligibilityHandler getAvTestEligibilityAPI() {
        if (ApiFactoryProvider.avTestEligibilityAPI == null) {
            ApiFactoryProvider.avTestEligibilityAPI = (AvTestEligibilityHandler)getImplClassInstance("DM_ANTIVIRUS_DETECTION_UTIL_CLASS");
        }
        return ApiFactoryProvider.avTestEligibilityAPI;
    }
    
    public static RequestDemoHandler getRequestDemoHandler() {
        if (ApiFactoryProvider.requestDemoHandler == null) {
            ApiFactoryProvider.requestDemoHandler = (RequestDemoHandler)getImplClassInstance("DM_REQUEST_DEMO_HANDLER_CLASS");
        }
        return ApiFactoryProvider.requestDemoHandler;
    }
    
    public static LoginHandler getLoginHandler() {
        if (ApiFactoryProvider.loginHandler == null) {
            ApiFactoryProvider.loginHandler = (LoginHandler)getImplClassInstance("DM_OP_LOGIN_HANDLER_CLASS");
        }
        return ApiFactoryProvider.loginHandler;
    }
    
    public static TFAService getTFAService() {
        try {
            if (ApiFactoryProvider.tfaService == null) {
                if (SyMUtil.isProbeServer()) {
                    ApiFactoryProvider.tfaService = (TFAService)getImplClassInstance("PS_TFA_SERVICE_CLASS");
                }
                else if (SyMUtil.isSummaryServer()) {
                    ApiFactoryProvider.tfaService = (TFAService)getImplClassInstance("SS_TFA_SERVICE_CLASS");
                }
                else {
                    ApiFactoryProvider.tfaService = (TFAService)getImplClassInstance("DM_TFA_SERVICE_CLASS");
                }
            }
        }
        catch (final Exception e) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception in getting SecuritySettingsServiceObject", e);
        }
        return ApiFactoryProvider.tfaService;
    }
    
    public static SecurityEnforcementAPI getSecurityEnforcementAPI() {
        if (ApiFactoryProvider.securityEnforcementAPI == null) {
            ApiFactoryProvider.securityEnforcementAPI = (SecurityEnforcementAPI)getImplClassInstance("SECURITY_ENFORCEMENT_API_CLASS");
        }
        return ApiFactoryProvider.securityEnforcementAPI;
    }
    
    public static UserManagementAPI getUserManagementAPIHandler() {
        if (ApiFactoryProvider.userManagementAPI == null) {
            ApiFactoryProvider.userManagementAPI = (UserManagementAPI)getImplClassInstance("DM_USER_MANAGEMENT_HANDLER_CLASS");
        }
        return ApiFactoryProvider.userManagementAPI;
    }
    
    public static SGSProxyFileDataDefaultImpl getSGSProxyFileDataHandler() {
        if (ApiFactoryProvider.sgsProxyFileDataDefaultImpl == null) {
            ApiFactoryProvider.sgsProxyFileDataDefaultImpl = (SGSProxyFileDataDefaultImpl)getImplClassInstance("SGS_PROXY_FILE_DATA_IMPL");
            if (ApiFactoryProvider.sgsProxyFileDataDefaultImpl == null) {
                ApiFactoryProvider.sgsProxyFileDataDefaultImpl = new SGSProxyFileDataDefaultImpl();
            }
        }
        return ApiFactoryProvider.sgsProxyFileDataDefaultImpl;
    }
    
    public static void resetUserManagementAPIHandler() {
        ApiFactoryProvider.userManagementAPI = null;
    }
    
    static {
        LOGGER = Logger.getLogger(ApiFactoryProvider.class.getName());
        ApiFactoryProvider.evaluationTrackHandler = null;
        ApiFactoryProvider.dmZendeskAPI = null;
        ApiFactoryProvider.avTestEligibilityAPI = null;
        ApiFactoryProvider.requestDemoHandler = null;
        ApiFactoryProvider.loginHandler = null;
        ApiFactoryProvider.tfaService = null;
        ApiFactoryProvider.userManagementAPI = null;
        ApiFactoryProvider.securityEnforcementAPI = null;
        ApiFactoryProvider.sgsProxyFileDataDefaultImpl = null;
    }
}
