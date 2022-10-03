package com.adventnet.iam.security;

import java.util.regex.Pattern;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MaskUtil
{
    private static final Logger LOGGER;
    private static final String MASKED_VALUE = "*****";
    private static final String LOG_CUSTOM_FIELDS = "_custom_fields";
    
    static void maskSensitiveValues(final HttpServletRequest request, final HttpServletResponse response, final SecurityRequestWrapper securedRequest, final SecurityFilterProperties filterConfig) {
        final SecurityLogRequestWrapper logReq = (SecurityLogRequestWrapper)SecurityUtil.getCurrentLogRequest();
        if (logReq != null) {
            ActionRule actionRule = null;
            if (securedRequest == null || securedRequest.getURLActionRule() == null) {
                if (filterConfig.maskAllParamValuesOnError()) {
                    logReq.maskAllParamValues = true;
                }
                else {
                    final List<String> globalSecretParams = filterConfig.getSecretRequestParamsFromProperty();
                    final List<String> defaultSecretParams = SecurityRequestWrapper.getDefaultSecretParameters();
                    final boolean globalSecretConfigExist = globalSecretParams != null;
                    if (globalSecretConfigExist || defaultSecretParams.size() > 0) {
                        final Enumeration<String> requestParams = request.getParameterNames();
                        while (requestParams.hasMoreElements()) {
                            final String paramName = requestParams.nextElement();
                            if (paramName.equals(filterConfig.getCSRFParamName())) {
                                final String csrfParamValue = request.getParameter(filterConfig.getCSRFParamName());
                                logReq.addPartiallyMaskedParameter(filterConfig.getCSRFParamName(), Arrays.asList(getCSRFMaskedValue(csrfParamValue)));
                            }
                            else {
                                if ((!globalSecretConfigExist || !globalSecretParams.contains(paramName.toLowerCase())) && !defaultSecretParams.contains(paramName)) {
                                    continue;
                                }
                                logReq.addSecretParameter(paramName);
                            }
                        }
                    }
                }
            }
            else {
                actionRule = securedRequest.getURLActionRule();
                setActionRuleToLogRequest(logReq, actionRule);
                if (!securedRequest.paramValidationCompleted || actionRule.hasJSONSecretParam || SecurityFilterProperties.getPiiDetector() != null) {
                    final Enumeration<String> requestParams2 = securedRequest.getParameterNames();
                    if (requestParams2.hasMoreElements()) {
                        final List<String> configuredSecretParamList = new ArrayList<String>();
                        configuredSecretParamList.addAll(actionRule.getSecretParameters());
                        configuredSecretParamList.addAll(SecurityRequestWrapper.getDefaultSecretParameters());
                        configuredSecretParamList.add(filterConfig.getCSRFParamName());
                        final boolean partialMaskingEnabled = actionRule.getPartialMaskingParamRules() != null;
                        final boolean extraParamRuleExist = SecurityUtil.getExtraParameterRule((HttpServletRequest)securedRequest, actionRule) != null;
                        while (requestParams2.hasMoreElements()) {
                            final String requestParam = requestParams2.nextElement();
                            if (!logReq.isSecretParameter(requestParam)) {
                                if (logReq.isExtraParameter(requestParam)) {
                                    continue;
                                }
                                boolean valueMasked = false;
                                if (!securedRequest.getValidatedParameters().contains(requestParam)) {
                                    if (isSecretParam(requestParam, configuredSecretParamList, actionRule, filterConfig)) {
                                        logReq.addSecretParameter(requestParam);
                                        valueMasked = true;
                                    }
                                    else if (partialMaskingEnabled) {
                                        final ParameterRule paramRule = actionRule.getPartialMaskingParamRule(requestParam);
                                        if (paramRule != null) {
                                            final List<String> maskedValues = new ArrayList<String>();
                                            for (final String value : securedRequest.getParameterValuesForLogging(requestParam)) {
                                                maskedValues.add(getPartiallyMaskedValue(value, paramRule));
                                            }
                                            logReq.addPartiallyMaskedParameter(requestParam, maskedValues);
                                            valueMasked = true;
                                        }
                                    }
                                    else if (!securedRequest.isDefaultParam(requestParam) && actionRule.getParameterRule(requestParam) == null) {
                                        if (extraParamRuleExist || !actionRule.isIgnoreExtraParam()) {
                                            if (filterConfig.isExtraParamMaskingEnabled()) {
                                                logReq.addExtraParameter(requestParam);
                                            }
                                        }
                                        else if (filterConfig.isIgnoreExtraParamMaskingEnabled()) {
                                            logReq.addIgnoredExtraParameter(requestParam);
                                        }
                                    }
                                }
                                if (valueMasked) {
                                    continue;
                                }
                                handleJSONParameter(securedRequest, requestParam, actionRule, logReq);
                            }
                        }
                    }
                }
                if (filterConfig.isInputStreamLogEnabled()) {
                    handleInputStream(logReq, securedRequest, actionRule);
                }
            }
            maskSecretHeaders(logReq, request, filterConfig, actionRule);
            final SecurityLogResponseWrapper logResponse = new SecurityLogResponseWrapper(response);
            if (filterConfig.getSecretResponseHeadersFromProperty() != null) {
                for (final String resHeaderName : response.getHeaderNames()) {
                    if (filterConfig.getSecretResponseHeadersFromProperty().contains(resHeaderName.toLowerCase())) {
                        logResponse.addSecretHeader(resHeaderName);
                    }
                }
            }
            logReq.setAttribute("ZSEC_LOG_RESPONSE_COOKIES", (Object)logResponse.cookiesList);
            request.setAttribute(SecurityLogRequestWrapper.class.getName(), (Object)logReq);
            request.setAttribute(SecurityLogResponseWrapper.class.getName(), (Object)logResponse);
        }
        final Map<String, Object> customFieldsMap = (Map<String, Object>)((request.getAttribute("_custom_fields") != null) ? request.getAttribute("_custom_fields") : new HashMap<String, Object>());
        customFieldsMap.put("_c_zsec_browser_session_id", SecurityUtil.getBrowserSessionID(request));
        request.setAttribute("_custom_fields", (Object)customFieldsMap);
    }
    
    private static void maskSecretHeaders(final SecurityLogRequestWrapper logReq, final HttpServletRequest request, final SecurityFilterProperties filterConfig, final ActionRule actionRule) {
        final List<String> requestSecretHeaders = new ArrayList<String>();
        final List<String> configuredSecretHeaders = new ArrayList<String>();
        configuredSecretHeaders.addAll(filterConfig.getInternalSecretRequestHeaders());
        if (SecurityFilterProperties.RequestHeaderValidationMode.DISABLE != filterConfig.getReqHeaderValidationMode()) {
            if (actionRule != null) {
                configuredSecretHeaders.addAll(actionRule.getSecretRequestHeaders());
            }
            configuredSecretHeaders.addAll(filterConfig.getDefaultSecretRequestHeaders());
        }
        for (final String secretHeader : filterConfig.getSecretRequestHeadersFromProperty()) {
            if (!configuredSecretHeaders.contains(secretHeader)) {
                configuredSecretHeaders.add(secretHeader);
            }
        }
        for (final String headerName : configuredSecretHeaders) {
            if (logReq.isHeaderExist(headerName)) {
                logReq.addSecretHeader(headerName);
            }
        }
        for (final ParameterRule headerRule : filterConfig.getPartialMaskingInternalReqHeaderRules()) {
            if (logReq.isHeaderExist(headerRule.getParamName())) {
                logReq.addSecretHeader(headerRule.getParamName(), getPartiallyMaskedHeaderValues(request, headerRule));
            }
        }
        if (actionRule != null && actionRule.getPartialMaskingRequestHeaderRules() != null) {
            for (final ParameterRule headerRule : actionRule.getPartialMaskingRequestHeaderRules().values()) {
                if (logReq.isHeaderExist(headerRule.getParamName())) {
                    logReq.addSecretHeader(headerRule.getParamName(), getPartiallyMaskedHeaderValues(request, headerRule));
                }
            }
        }
    }
    
    private static List<String> getPartiallyMaskedHeaderValues(final HttpServletRequest request, final ParameterRule headerRule) {
        final List<String> maskedValues = new ArrayList<String>();
        final Enumeration<String> headerValues = request.getHeaders(headerRule.getParamName());
        while (headerValues.hasMoreElements()) {
            final String headerValue = headerValues.nextElement();
            maskedValues.add(getPartiallyMaskedValue(headerValue, headerRule));
        }
        return maskedValues;
    }
    
    private static boolean isSecretParam(final String requestParam, final List<String> configuredSecretParamList, final ActionRule actionRule, final SecurityFilterProperties filterConfig) {
        if (!SecurityUtil.isOperationParamOrHipDigestParam(requestParam, actionRule)) {
            if (configuredSecretParamList.contains(requestParam)) {
                return true;
            }
            if (filterConfig.getSecretRequestParamsFromProperty() != null && filterConfig.getSecretRequestParamsFromProperty().contains(requestParam.toLowerCase())) {
                return true;
            }
            if (!actionRule.getSecretParamNameRegexRules().isEmpty() && !SecurityRequestWrapper.getDefaultParameters().containsKey(requestParam) && !actionRule.getParamRuleMap().containsKey(requestParam)) {
                for (final ParameterRule paramRule : actionRule.getSecretParamNameRegexRules()) {
                    if (paramRule.getParamNameRegex().matcher(requestParam).matches()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static void handleJSONParameter(final SecurityRequestWrapper securedRequest, final String requestParam, final ActionRule actionRule, final SecurityLogRequestWrapper logReq) {
        if (actionRule != null && (actionRule.hasJSONSecretParam || SecurityFilterProperties.getPiiDetector() != null)) {
            ParameterRule paramRule = actionRule.getParameterRule(requestParam);
            if (paramRule == null) {
                final ParameterRule paramOrStreamRule = actionRule.getParamOrStreamRule();
                paramRule = ((paramOrStreamRule != null && requestParam.equals(paramOrStreamRule.getParamName())) ? paramOrStreamRule : null);
            }
            if (paramRule != null && JSONTemplateRule.isTemplateDefinedJSONType(paramRule)) {
                final String[] paramValues = securedRequest.getParameterValuesForLogging(requestParam);
                if (paramValues != null) {
                    final List<String> maskedValues = new ArrayList<String>(paramValues.length);
                    for (int i = 0; i < paramValues.length; ++i) {
                        maskedValues.add(getMaskedJSONValue(paramValues[i], paramRule));
                    }
                    logReq.addJSONParamValues(requestParam, maskedValues);
                }
            }
            else {
                logReq.addPiiParams(requestParam);
            }
        }
    }
    
    private static void handleInputStream(final SecurityLogRequestWrapper logReq, final SecurityRequestWrapper securedRequest, final ActionRule actionRule) {
        ParameterRule inputStreamRule = actionRule.getInputStreamRule();
        inputStreamRule = ((inputStreamRule == null) ? actionRule.getParamOrStreamRule() : inputStreamRule);
        if (inputStreamRule != null) {
            String inputStreamVal = securedRequest.getOriginalInputStreamContent();
            if (inputStreamVal != null) {
                if (inputStreamRule.isSecret()) {
                    inputStreamVal = "*****";
                }
                else if (actionRule.hasJSONSecretParam && JSONTemplateRule.isTemplateDefinedJSONType(inputStreamRule)) {
                    inputStreamVal = getMaskedJSONValue(inputStreamVal, inputStreamRule);
                }
                else {
                    inputStreamVal = ((SecurityFilterProperties.getPiiDetector() != null) ? SecurityFilterProperties.getPiiDetector().detect(inputStreamVal).toString() : inputStreamVal);
                }
                logReq.setAttribute("ACCESSLOG_STREAM_CONTENT", (Object)inputStreamVal);
            }
        }
    }
    
    private static String getMaskedJSONValue(final String value, final ParameterRule paramRule) {
        try {
            String dataType = paramRule.getDataType();
            if ("JSONObject|JSONArray".equals(dataType)) {
                dataType = (value.trim().startsWith("{") ? "JSONObject" : "JSONArray");
            }
            if ("JSONObject".equals(dataType)) {
                return maskJSONObject(new JSONObject(value), new JSONObject(), paramRule).toString();
            }
            return maskJSONArray(new JSONArray(value), new JSONArray(), paramRule).toString();
        }
        catch (final Exception e) {
            MaskUtil.LOGGER.log(Level.FINE, "Error occurred while masking sensitive values in JSON", e);
            return value;
        }
    }
    
    private static Object maskJSONObject(final JSONObject requestObject, final JSONObject resultObject, final ParameterRule paramRule) throws JSONException {
        if (requestObject.length() != 0) {
            final HttpServletRequest request = SecurityUtil.getCurrentRequest();
            final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(request);
            final JSONTemplateRule templateRule = JSONTemplateRule.resolveTemplateSelection(request, requestObject, paramRule);
            if (templateRule != null) {
                final List<String> jsonSecretParamNamesInRegex = templateRule.getListOfJSONSecretParamNamesInRegex();
                final List<String> jsonSecretParamNamesInNonRegex = templateRule.getListOfJSONSecretParamNamesInNonRegex();
                final Iterator iterator = requestObject.keys();
                while (iterator.hasNext()) {
                    final String keyName = iterator.next().toString();
                    final Object keyValue = requestObject.get(keyName);
                    if (jsonSecretParamNamesInNonRegex.contains(keyName) || (!jsonSecretParamNamesInRegex.isEmpty() && isKeyNameMatchedWithRegex(keyName, jsonSecretParamNamesInRegex, filterProps) && !templateRule.getKeyValueRule().containsKey(keyName))) {
                        resultObject.put(keyName, (Object)"*****");
                    }
                    else if (keyValue instanceof JSONObject || keyValue instanceof JSONArray) {
                        final ParameterRule innerKeyRule = templateRule.getParameterRule(keyName);
                        if (innerKeyRule != null) {
                            resultObject.put(keyName, (Object)getKeyValue(keyValue.toString(), innerKeyRule));
                        }
                        else {
                            resultObject.put(keyName, (SecurityFilterProperties.getPiiDetector() != null) ? SecurityFilterProperties.getPiiDetector().detect(keyValue.toString()).toString() : keyValue);
                        }
                    }
                    else {
                        resultObject.put(keyName, (SecurityFilterProperties.getPiiDetector() != null) ? SecurityFilterProperties.getPiiDetector().detect(keyValue.toString()).toString() : keyValue);
                    }
                }
            }
        }
        return resultObject;
    }
    
    private static Object maskJSONArray(final JSONArray requestJSONArray, final JSONArray resultJSONArray, final ParameterRule paramRule) throws JSONException {
        final JSONTemplateRule templateRule = JSONTemplateRule.getKeyRule(paramRule.getJSONTemplate());
        final String dynamicKey = paramRule.getTemplateParam();
        Object jsonArrayElement = null;
        for (int i = 0; i < requestJSONArray.length(); ++i) {
            jsonArrayElement = requestJSONArray.get(i);
            if (dynamicKey != null && jsonArrayElement instanceof JSONObject && (((JSONObject)jsonArrayElement).has(dynamicKey) || templateRule == null)) {
                resultJSONArray.put(maskJSONObject((JSONObject)jsonArrayElement, new JSONObject(), paramRule));
            }
            else if (templateRule != null) {
                final ParameterRule indexRule = templateRule.getJSONArrayIndexRule(i);
                if (indexRule != null) {
                    resultJSONArray.put((Object)getKeyValue(jsonArrayElement.toString(), indexRule));
                }
                else if (requestJSONArray.get(i) instanceof JSONObject) {
                    resultJSONArray.put(maskJSONObject((JSONObject)jsonArrayElement, new JSONObject(), paramRule));
                }
                else {
                    resultJSONArray.put((Object)jsonArrayElement.toString());
                }
            }
        }
        return resultJSONArray;
    }
    
    private static String getKeyValue(final String value, final ParameterRule keyRule) {
        if (keyRule.isSecret()) {
            return "*****";
        }
        if (JSONTemplateRule.isTemplateDefinedJSONType(keyRule)) {
            return getMaskedJSONValue(value, keyRule);
        }
        return value;
    }
    
    private static boolean isKeyNameMatchedWithRegex(final String keyName, final List<String> jsonSecretParamNamesInRegex, final SecurityFilterProperties filterProps) {
        for (final String paramNameRegex : jsonSecretParamNamesInRegex) {
            final Pattern paramNameRegexPattern = filterProps.getRegexPattern(paramNameRegex);
            if (paramNameRegexPattern != null && paramNameRegexPattern.matcher(keyName).matches()) {
                return true;
            }
        }
        return false;
    }
    
    private static void setActionRuleToLogRequest(final SecurityLogRequestWrapper logReq, final ActionRule actionRule) {
        logReq.setAttribute("path", (Object)actionRule.getPath());
        logReq.setAttribute("method", (Object)actionRule.getMethod());
        logReq.setAttribute("operation-param", (Object)actionRule.getOperationParam());
        logReq.setAttribute("operation-value", (Object)actionRule.getOperationValue());
    }
    
    static String getPartiallyMaskedValue(final String paramValue, final ParameterRule paramRule) {
        return getPartiallyMaskedValue(paramValue, paramRule.getPreserveCharPrefix(), paramRule.getPreserveCharSuffix(), paramRule.getPreserveCharLimit());
    }
    
    static String getPartiallyMaskedValue(final String paramValue, final int prefix, final int suffix) {
        return getPartiallyMaskedValue(paramValue, prefix, suffix, 10);
    }
    
    private static String getPartiallyMaskedValue(final String paramValue, final int prefix, final int suffix, final int charLimitInPercent) {
        final int totalCharsToBeShown = prefix + suffix;
        if (totalCharsToBeShown <= charLimitInPercent * paramValue.length() / 100) {
            return paramValue.substring(0, prefix) + "***" + paramValue.substring(paramValue.length() - suffix);
        }
        return "*****";
    }
    
    static String getCSRFMaskedValue(final String csrfValue) {
        if (SecurityUtil.isValid(csrfValue) && csrfValue.length() > 5) {
            return "***" + csrfValue.substring(csrfValue.length() - 5);
        }
        return csrfValue;
    }
    
    static boolean isSecretComponent(final String paramName, final SecurityFilterProperties filterConfig, final ParameterRule rule) {
        if (filterConfig.isSecretParamLoggingMasked()) {
            if (rule.isMaskingEnabled()) {
                return true;
            }
            if (rule.ruleName == "param") {
                final HttpServletRequest logRequest = SecurityUtil.getCurrentLogRequest();
                return logRequest != null && ((SecurityLogRequestWrapper)logRequest).isSecretParameter(paramName);
            }
            if (filterConfig.isRequestHeaderValidationEnabled() && rule.ruleName == "header") {
                return filterConfig.getSecretRequestHeadersFromProperty().contains(paramName);
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(MaskUtil.class.getName());
    }
}
