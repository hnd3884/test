package com.zoho.security.eventfwimpl;

import com.adventnet.iam.IAMUtil;
import com.adventnet.iam.security.ActionRule;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.SecurityFilterProperties;
import com.adventnet.iam.security.SecurityUtil;
import com.zoho.logs.logclient.LogClientThreadLocal;
import com.zoho.security.eventfw.CalleeInfo;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.config.DataFields;
import java.util.Map;
import java.util.logging.Logger;
import com.zoho.security.eventfw.builtinfieldsImpl.BuiltInFieldsProvider;

public class ZSecBuiltInFieldsImplProvider implements BuiltInFieldsProvider
{
    public static final Logger LOGGER;
    private static Object logClientThreadLocalClass;
    
    public void fillData(final Map<String, Object> params, final DataFields builtInField, final ExecutionTimer timer, final CalleeInfo calleeInfo) {
        final FIELD_NAMES fieldName = FIELD_NAMES.valueOfDataField(builtInField);
        if (fieldName == null) {
            return;
        }
        switch (fieldName) {
            case RQ_LOG_ID: {
                final String requestId = (ZSecBuiltInFieldsImplProvider.logClientThreadLocalClass != null) ? LogClientThreadLocal.getRequestID() : null;
                if (requestId != null) {
                    params.put(FIELD_NAMES.RQ_LOG_ID.name(), requestId);
                }
                return;
            }
            default: {
                final HttpServletRequest request = SecurityUtil.getCurrentRequest();
                ActionRule rule = null;
                if (request != null) {
                    try {
                        switch (fieldName) {
                            case RQ_URI: {
                                params.put(FIELD_NAMES.RQ_URI.name(), request.getRequestURI());
                                return;
                            }
                            case RQ_METHOD: {
                                params.put(FIELD_NAMES.RQ_METHOD.name(), request.getMethod());
                                return;
                            }
                            case RQ_USER_ZUID: {
                                if (SecurityFilterProperties.isUsingIAMImpl()) {
                                    final String zuid = getCurrentUserZUID(request);
                                    if (zuid != null) {
                                        params.put(FIELD_NAMES.RQ_USER_ZUID.name(), zuid);
                                    }
                                }
                                return;
                            }
                            case RQ_CLIENT: {
                                RQ_CLIENT requestClient = null;
                                if (SecurityFilterProperties.isUsingIAMImpl()) {
                                    requestClient = getRequestClientUsingIAM(request);
                                }
                                if (requestClient != null) {
                                    params.put(FIELD_NAMES.RQ_CLIENT.name(), requestClient.name());
                                }
                                return;
                            }
                            case RQ_REMOTE_IP: {
                                if (request instanceof SecurityRequestWrapper) {
                                    params.put(FIELD_NAMES.RQ_REMOTE_IP.name(), ((SecurityRequestWrapper)request).getRemoteUserIPAddr());
                                }
                                else {
                                    params.put(FIELD_NAMES.RQ_REMOTE_IP.name(), request.getRemoteAddr());
                                }
                                return;
                            }
                            case SS_SERVLET_PATH: {
                                params.put(FIELD_NAMES.SS_SERVLET_PATH.name(), request.getServletPath());
                                return;
                            }
                            case SS_PATH_INFO: {
                                params.put(FIELD_NAMES.SS_PATH_INFO.name(), request.getPathInfo());
                                return;
                            }
                            case WC_URI_PREFIX: {
                                rule = getActionRule(request);
                                if (rule != null) {
                                    params.put(FIELD_NAMES.WC_URI_PREFIX.name(), rule.getPrefix());
                                }
                                return;
                            }
                            case WC_URI: {
                                rule = getActionRule(request);
                                if (rule != null) {
                                    params.put(FIELD_NAMES.WC_URI.name(), rule.getPath());
                                }
                                return;
                            }
                            case WC_METHOD: {
                                rule = getActionRule(request);
                                if (rule != null) {
                                    params.put(FIELD_NAMES.WC_METHOD.name(), rule.getMethod());
                                }
                                return;
                            }
                            case WC_OPERATION: {
                                rule = getActionRule(request);
                                if (rule != null) {
                                    params.put(FIELD_NAMES.WC_OPERATION.name(), rule.getOperationValue());
                                }
                            }
                            default: {}
                        }
                    }
                    catch (final Exception e) {
                        ZSecBuiltInFieldsImplProvider.LOGGER.log(Level.WARNING, "Exception \"{0}\" ocurred while getting field \"{1}\"", new Object[] { e.getMessage(), fieldName });
                    }
                    catch (final Throwable t) {
                        ZSecBuiltInFieldsImplProvider.LOGGER.log(Level.WARNING, "Error \"{0}\" ocurred while getting field \"{1}\"", new Object[] { t.getMessage(), fieldName });
                    }
                }
            }
        }
    }
    
    private static ActionRule getActionRule(final HttpServletRequest request) {
        return (request instanceof SecurityRequestWrapper) ? ((SecurityRequestWrapper)request).getURLActionRule() : null;
    }
    
    private static String getCurrentUserZUID(final HttpServletRequest request) {
        if (request instanceof SecurityRequestWrapper) {
            final SecurityRequestWrapper srw = (SecurityRequestWrapper)request;
            if (SecurityUtil.isValid(srw.getOrgUser())) {
                return srw.getOrgUser();
            }
        }
        if (request.getUserPrincipal() != null) {
            return request.getUserPrincipal().getName();
        }
        return null;
    }
    
    private static RQ_CLIENT getRequestClientUsingIAM(final HttpServletRequest request) {
        final IAMUtil.TokenType tokenType = IAMUtil.getCurrentTokenType();
        if (tokenType == null) {
            return null;
        }
        if (IAMUtil.getCurrentTokenType() == IAMUtil.TokenType.TICKET) {
            return RQ_CLIENT.WEB;
        }
        return RQ_CLIENT.API;
    }
    
    static {
        LOGGER = Logger.getLogger(ZSecBuiltInFieldsImplProvider.class.getName());
        ZSecBuiltInFieldsImplProvider.logClientThreadLocalClass = null;
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final String ceClassName = "com.zoho.logs.logclient.LogClientThreadLocal";
        try {
            ZSecBuiltInFieldsImplProvider.logClientThreadLocalClass = cl.loadClass(ceClassName);
            ZSecBuiltInFieldsImplProvider.LOGGER.log(Level.WARNING, "LogClientThreadLocalClass Loaded Successfully. Class Name : {0} ", new Object[] { ceClassName });
        }
        catch (final ClassNotFoundException e) {
            ZSecBuiltInFieldsImplProvider.LOGGER.log(Level.WARNING, "Unable to Load LogClientThreadLocalClass . Class Name : {0}, Exception {1}", new Object[] { ceClassName, e });
        }
    }
    
    private enum FIELD_NAMES
    {
        RQ_LOG_ID, 
        RQ_URI, 
        RQ_METHOD, 
        RQ_USER_ZUID, 
        RQ_REMOTE_IP, 
        RQ_CLIENT, 
        SS_SERVLET_PATH, 
        SS_PATH_INFO, 
        WC_URI_PREFIX, 
        WC_URI, 
        WC_METHOD, 
        WC_OPERATION;
        
        static FIELD_NAMES valueOfDataField(final DataFields dataField) {
            try {
                return valueOf(dataField.getName());
            }
            catch (final IllegalArgumentException ex) {
                return null;
            }
        }
    }
    
    private enum RQ_CLIENT
    {
        WEB, 
        API;
    }
}
