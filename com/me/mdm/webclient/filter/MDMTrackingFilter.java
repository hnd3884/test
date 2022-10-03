package com.me.mdm.webclient.filter;

import java.io.IOException;
import java.util.Iterator;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class MDMTrackingFilter implements Filter
{
    protected static final Logger LOGGER;
    protected Pattern skipPattern;
    public static final String TRACKING_NAME = "trackingname";
    public static final String SUB_TRACKING_NAME = "subtrackingname";
    public static final String PARAMETERS = "parameters";
    public static final String PATH = "path";
    public static final String PARAM_NAME = "paramname";
    public static final String PARAM_VALUE = "paramvalue";
    public static final String IS_API = "isapi";
    
    public MDMTrackingFilter() {
        this.skipPattern = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String skip = filterConfig.getInitParameter("skip");
        if (skip != null && skip.length() != 0) {
            this.skipPattern = Pattern.compile(skip);
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            MDMTrackingFilter.LOGGER.log(Level.INFO, "Entered {0} Filter", MDMTrackingFilter.class.getName());
        }
        try {
            final HttpServletRequest request = (HttpServletRequest)servletRequest;
            final String path = SecurityUtil.getRequestPath(request);
            if (this.skipPattern != null && !this.skipPattern.matcher(path).matches()) {
                final SecurityRequestWrapper securityRequestWrapper = SecurityRequestWrapper.getInstance(request);
                final String trackingName = securityRequestWrapper.getURLActionRule().getCustomAttribute("trackingName");
                if (trackingName != null && !trackingName.trim().isEmpty()) {
                    final JSONObject dataFromSecurity = new JSONObject();
                    dataFromSecurity.put("trackingname", (Object)trackingName);
                    dataFromSecurity.put("path", (Object)path);
                    final String traceableParamsFromSecurity = securityRequestWrapper.getURLActionRule().getCustomAttribute("trackingParams");
                    final ArrayList<String> qStringTraceableParams = new ArrayList<String>();
                    final ArrayList<String> bodyTraceableParams = new ArrayList<String>();
                    if (traceableParamsFromSecurity != null && !traceableParamsFromSecurity.trim().isEmpty()) {
                        final String[] split;
                        final String[] traceableParamArr = split = traceableParamsFromSecurity.split(",");
                        for (String traceableParam : split) {
                            traceableParam = traceableParam.trim();
                            if (traceableParam.startsWith("$")) {
                                bodyTraceableParams.add(traceableParam);
                            }
                            else {
                                qStringTraceableParams.add(traceableParam.trim());
                            }
                        }
                    }
                    final String operationParam = securityRequestWrapper.getURLActionRule().getOperationParam();
                    final JSONArray params = new JSONArray();
                    if (operationParam != null && !operationParam.isEmpty()) {
                        final String paramValue = request.getParameter(operationParam);
                        if (paramValue != null) {
                            final JSONObject param = new JSONObject();
                            param.put("paramname", (Object)operationParam);
                            param.put("paramvalue", (Object)paramValue);
                            params.put((Object)param);
                        }
                    }
                    for (final String paramName : qStringTraceableParams) {
                        final String paramValue2 = request.getParameter(paramName);
                        if (!paramName.equals(operationParam) && paramValue2 != null) {
                            final JSONObject param2 = new JSONObject();
                            param2.put("paramname", (Object)paramName);
                            param2.put("paramvalue", (Object)paramValue2);
                            params.put((Object)param2);
                        }
                    }
                    if (this.isApi(path)) {
                        dataFromSecurity.put("isapi", true);
                        final String method = this.getRequestMethod(request.getMethod());
                        JSONObject param = new JSONObject();
                        param.put("paramname", (Object)"apiOperation");
                        param.put("paramvalue", (Object)method);
                        params.put((Object)param);
                        final String subTrackingName = this.getSubTrackingName(securityRequestWrapper, trackingName);
                        dataFromSecurity.put("subtrackingname", (Object)subTrackingName);
                        final JSONObject bodyData = this.getBodyData(request);
                        if (bodyData != null) {
                            for (final String bodyParamJsonPath : bodyTraceableParams) {
                                final String value = this.getJsonPathValue(bodyParamJsonPath, bodyData);
                                if (value != null) {
                                    param = new JSONObject();
                                    param.put("paramname", (Object)bodyParamJsonPath.substring(bodyParamJsonPath.lastIndexOf(".") + 1));
                                    param.put("paramvalue", (Object)value);
                                    params.put((Object)param);
                                }
                            }
                        }
                    }
                    dataFromSecurity.put("parameters", (Object)params);
                    final MDMURLTrackingAPI urlTrackingAPI = MDMApiFactoryProvider.getUrlTrackingAPI();
                    urlTrackingAPI.postData(dataFromSecurity, request);
                }
            }
        }
        catch (final Exception e) {
            MDMTrackingFilter.LOGGER.log(Level.SEVERE, "Exception in MDMTrackingFilter : ", e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    private JSONObject getBodyData(final HttpServletRequest request) throws Exception {
        final String bodyString = request.getParameter("zoho-inputstream");
        return (bodyString == null) ? null : new JSONObject(bodyString);
    }
    
    private boolean isApi(final String path) throws Exception {
        boolean status = false;
        if (path.startsWith("/api/v")) {
            status = true;
        }
        return status;
    }
    
    private String getJsonPathValue(final String jsonPath, final JSONObject bodyData) throws Exception {
        final String[] nodes = jsonPath.split("\\.");
        Object value = null;
        for (final String node : nodes) {
            if (!node.equals("$")) {
                if (value == null) {
                    value = bodyData.opt(node);
                    if (value == null) {
                        MDMTrackingFilter.LOGGER.log(Level.SEVERE, "No data available for the node {0} in the body", new Object[] { node });
                        return null;
                    }
                }
                else {
                    if (!(value instanceof JSONObject)) {
                        MDMTrackingFilter.LOGGER.log(Level.SEVERE, "{0} is not a JSON Object to get data for the node {1}", new Object[] { value, node });
                        return null;
                    }
                    value = ((JSONObject)value).opt(node);
                }
            }
        }
        return (value == null) ? null : value.toString();
    }
    
    private String getSubTrackingName(final SecurityRequestWrapper securityRequestWrapper, final String trackingName) throws Exception {
        final String subTrackingName = securityRequestWrapper.getURLActionRule().getCustomAttribute("subTrackingName");
        if (subTrackingName != null && !subTrackingName.trim().isEmpty()) {
            return subTrackingName.trim();
        }
        return trackingName;
    }
    
    private String getRequestMethod(final String httpMethod) {
        if (httpMethod.equalsIgnoreCase("GET")) {
            return "READ";
        }
        if (httpMethod.equalsIgnoreCase("POST")) {
            return "CREATE";
        }
        if (httpMethod.equalsIgnoreCase("PUT")) {
            return "UPDATE";
        }
        if (httpMethod.equalsIgnoreCase("DELETE")) {
            return "DELETE";
        }
        return httpMethod;
    }
    
    public void destroy() {
    }
    
    static {
        LOGGER = Logger.getLogger(MDMTrackingFilter.class.getName());
    }
}
