package com.me.mdm.webclient.filter;

import java.util.Enumeration;
import org.json.JSONObject;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class AuthenticationHandlerUtil
{
    public static String WP_CHECK_IN_V1;
    public static String WP_CHECK_IN_ADMIN_V1;
    public static String WP_CHECK_IN_ADMIN_V2;
    public static Pattern WP_DISCOVER_SERVLET_PATTERN;
    public static Pattern MODERN_MAC_ENROLL_SERVLET_PATTERN;
    public static Pattern IOS_DEP_SERVLET_PATTERN;
    public static Pattern DEP_SERVLET_PATTERN;
    
    public static Hashtable<String, UnifiedAuthenticationService> getAuthenticationHandlers() {
        final Hashtable<String, UnifiedAuthenticationService> authenticatorClassesMap = new Hashtable<String, UnifiedAuthenticationService>();
        authenticatorClassesMap.put("default", MDMApiFactoryProvider.getSecureKeyProviderAPI().getDefaultUnifiedAuthHandler());
        authenticatorClassesMap.put("user-auth", (UnifiedAuthenticationService)new MDMUserUnifiedAuthenticationHandler());
        authenticatorClassesMap.put("purpose-auth", (UnifiedAuthenticationService)new MDMPurposeTokenUnifiedAuthenticationHandler());
        authenticatorClassesMap.put("device-auth", (UnifiedAuthenticationService)new MDMDeviceUnifiedAuthenticationHandler());
        authenticatorClassesMap.put("device-provision-auth", (UnifiedAuthenticationService)new MDMDeviceProvisioningUnifiedAuthenticationHandler());
        authenticatorClassesMap.put("erid-auth", (UnifiedAuthenticationService)new MDMEridUnifiedAuthenticationHandler());
        authenticatorClassesMap.put("migration-auth", (UnifiedAuthenticationService)new MDMMigrationUnifiedAuthenticationHandler());
        return authenticatorClassesMap;
    }
    
    public static Hashtable<String, UnifiedAuthenticationService> getAuthenticationHandlersForDC() {
        Hashtable<String, UnifiedAuthenticationService> authenticatorClassesMap = new Hashtable<String, UnifiedAuthenticationService>();
        authenticatorClassesMap = getAuthenticationHandlers();
        authenticatorClassesMap.remove("default");
        return authenticatorClassesMap;
    }
    
    public static DeviceRequest prepareDeviceRequest(final HttpServletRequest request, final Logger logger) throws IOException {
        try {
            byte[] requestBytes = null;
            try {
                requestBytes = IOUtils.toByteArray((InputStream)request.getInputStream());
            }
            catch (final IOException ex) {
                logger.log(Level.WARNING, "IOException occured while reading request : {0}", ex);
                throw ex;
            }
            final DeviceRequest devicerequest = new DeviceRequest();
            devicerequest.deviceRequestDatabytes = requestBytes;
            devicerequest.deviceRequestData = new String(requestBytes, StandardCharsets.UTF_8);
            (devicerequest.requestMap = new HashMap()).put("ServletPath", request.getServletPath());
            request.setAttribute("DeviceRequest", (Object)devicerequest);
            return devicerequest;
        }
        catch (final Exception ex2) {
            logger.log(Level.WARNING, "Exception occured while reading request : {0}", ex2);
            throw ex2;
        }
    }
    
    public static String getTemplateToken(final HttpServletRequest request, final DeviceRequest deviceRequest, final Logger logger) {
        if (AuthenticationHandlerUtil.IOS_DEP_SERVLET_PATTERN.matcher(request.getRequestURI()).matches()) {
            return request.getPathInfo().substring(1);
        }
        String templateToken = request.getParameter("templateToken");
        if (templateToken == null && AuthenticationHandlerUtil.DEP_SERVLET_PATTERN.matcher(request.getRequestURI()).matches()) {
            final String strData = (String)deviceRequest.deviceRequestData;
            final JSONObject requestJSON = new JSONObject(strData);
            logger.log(Level.INFO, "request json data is  {0}", requestJSON);
            templateToken = requestJSON.getJSONObject("Message").optString("TemplateToken");
        }
        return templateToken;
    }
    
    public static HashMap getParameterValueMap(final HttpServletRequest request) {
        final HashMap parameterValueMap = new HashMap();
        final Enumeration enume = request.getParameterNames();
        while (enume.hasMoreElements()) {
            final String attrName = enume.nextElement();
            parameterValueMap.put(attrName, request.getParameter(attrName));
        }
        parameterValueMap.put("ServletPath", request.getServletPath());
        return parameterValueMap;
    }
    
    public static String sanitizeXML(String strData) {
        strData = removeAllNonPrintableCharacters(strData);
        return strData;
    }
    
    public static String removeAllNonPrintableCharacters(final String strData) {
        return strData.replaceAll("[^\t\r\n -\ud7ff\ue000-\ufffd\ud800\udc00-\udbff\udfff]", "");
    }
    
    static {
        AuthenticationHandlerUtil.WP_CHECK_IN_V1 = "/mdm/client/v1/wpcheckin";
        AuthenticationHandlerUtil.WP_CHECK_IN_ADMIN_V1 = "/mdm/client/v1/wpcheckin/admin";
        AuthenticationHandlerUtil.WP_CHECK_IN_ADMIN_V2 = "/mdm/client/v2/wpcheckin/admin";
        AuthenticationHandlerUtil.WP_DISCOVER_SERVLET_PATTERN = Pattern.compile("^(/mdm/client/(v1/wpdiscover/|(v1|v2)/wpdiscover/admin/)[0-9]{1,20})$");
        AuthenticationHandlerUtil.MODERN_MAC_ENROLL_SERVLET_PATTERN = Pattern.compile("^(/mdm/client/v1/modern/mac/[a-zA-Z0-9]*)$");
        AuthenticationHandlerUtil.IOS_DEP_SERVLET_PATTERN = Pattern.compile("^(/mdm/client/v1/ios/(dep|ac)/[a-zA-Z0-9]*)$");
        AuthenticationHandlerUtil.DEP_SERVLET_PATTERN = Pattern.compile("^(/mdm/client/v1/dep)$");
    }
}
