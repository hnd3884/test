package com.zoho.security.agent;

import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_NOTIFICATION;
import org.json.JSONObject;
import java.util.logging.Handler;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.logImpl.ZohoLogsImplProvider;
import java.util.logging.Logger;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import com.adventnet.iam.security.SecurityUtil;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_APP_REGISTER;
import java.net.InetAddress;
import com.adventnet.iam.security.SecurityFilterProperties;

public class ServiceRegistration
{
    private static String appSenseRegistrationURL;
    private static String appSenseRulesLoaderURL;
    private static boolean appSenseRegistrationStatus;
    private static String logServiceName;
    
    public static void register() {
        try {
            final String service = SecurityFilterProperties.getServiceName();
            final String ip = InetAddress.getLocalHost().getHostAddress();
            register(service, ip);
        }
        catch (final Exception e) {
            ZSEC_APP_REGISTER.pushException(getAppSenseAppRegistrationURL(), e.getMessage(), (ExecutionTimer)null);
        }
    }
    
    public static boolean register(final String service, final String ip) {
        try {
            final String account = System.getProperty("user.name", "sas");
            final String type = SecurityFilterProperties.isUsingIAMAuth() ? "iam" : "me";
            final String urlString = getAppSenseAppRegistrationURL();
            String postParams = "iscsignature=" + SecurityUtil.sign() + "&service=" + service;
            if (getLogServiceName() != null) {
                postParams = postParams + "&logservice=" + getLogServiceName();
            }
            postParams = postParams + "&account=" + account + "&ip=" + ip + "&type=" + type;
            final int statusCode = SecurityFrameworkUtil.getURLConnection(urlString, postParams, "POST").getResponseCode();
            if (statusCode == 200) {
                ServiceRegistration.appSenseRegistrationStatus = true;
                ZSEC_APP_REGISTER.pushSuccess(urlString, "registration success", (ExecutionTimer)null);
                return true;
            }
            ZSEC_APP_REGISTER.pushError(statusCode, "INVALID STATUS CODE", (ExecutionTimer)null);
        }
        catch (final Exception e) {
            ZSEC_APP_REGISTER.pushException(getAppSenseAppRegistrationURL(), e.getMessage(), (ExecutionTimer)null);
        }
        return false;
    }
    
    private static String getAppSenseAppRegistrationURL() {
        return ServiceRegistration.appSenseRegistrationURL;
    }
    
    public static String getAppSenseAppFireWallRulesLoaderURL() {
        return ServiceRegistration.appSenseRulesLoaderURL;
    }
    
    private static String getLogServiceName() {
        if (ServiceRegistration.logServiceName == null) {
            final Handler[] handlers2;
            final Handler[] handlers = handlers2 = Logger.getLogger("").getHandlers();
            for (final Handler handler : handlers2) {
                if ("com.zoho.logs.logclient.logger.ApplicationLogHandler".equals(handler.getClass().getName())) {
                    ServiceRegistration.logServiceName = EventDataProcessor.getParser().getLogAPIImplProviderConfigMap().get("ZohoLogs").getLogServiceName();
                }
            }
        }
        return ServiceRegistration.logServiceName;
    }
    
    public static boolean isRegisteredInAppSense() {
        return ServiceRegistration.appSenseRegistrationStatus;
    }
    
    public static void setRegistrationStatus(final boolean status) {
        ServiceRegistration.appSenseRegistrationStatus = status;
    }
    
    public static void notifyAgentError(final String cause, final String details) {
        final String service = SecurityFilterProperties.getServiceName();
        final String urlString = AppSenseConstants.getErrorNotificationURL();
        try {
            final JSONObject data = new JSONObject();
            data.put("CAUSE", (Object)cause);
            data.put("DETAILS", (Object)details);
            String postParams = "iscsignature=" + SecurityUtil.sign() + "&service=" + service;
            postParams = postParams + "&data=" + data;
            final int statusCode = SecurityFrameworkUtil.getURLConnection(urlString, postParams, "POST").getResponseCode();
            if (statusCode == 200) {
                ZSEC_APPSENSE_NOTIFICATION.pushSuccess(urlString, "ERRORNOTIFICATION SUCCESS", (ExecutionTimer)null);
            }
            else {
                ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent(Integer.toString(statusCode), "ERRORNOTIFICATION : INVALID STATUS CODE", (ExecutionTimer)null);
            }
        }
        catch (final Exception e) {
            ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent(urlString, e.getMessage(), (ExecutionTimer)null);
        }
    }
    
    static {
        ServiceRegistration.appSenseRegistrationURL = "http://appsense/zsecagent/v1/register";
        ServiceRegistration.appSenseRulesLoaderURL = "http://appsense/zsecagent/v1/appfirewallrule";
        ServiceRegistration.appSenseRegistrationStatus = false;
        ServiceRegistration.logServiceName = null;
    }
}
