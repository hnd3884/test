package com.me.mdm.api;

import com.adventnet.persistence.DataAccessException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLDecoder;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Map;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.mdm.api.version.APIVersion;
import com.me.mdm.api.error.APIHTTPException;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class APIRequestProcessor
{
    private Logger logger;
    
    public static APIRequestProcessor getNewInstance() {
        return new APIRequestProcessor();
    }
    
    protected APIRequestProcessor() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
        try {
            HashMap parametersList = new HashMap();
            parametersList = this.constructParametersList(parametersList, servletRequest);
            final Object apiVersion = parametersList.get("apiVersion");
            final Object module = parametersList.get("module");
            final Object entity = parametersList.get("entity");
            final Object operation = parametersList.get("operation");
            APIRequestMapper.createRequestMapper();
            if (apiVersion == null) {
                new APIHTTPException("COM0012", new Object[0]).setErrorResponse(servletResponse);
                return;
            }
            if (new APIVersion(String.valueOf(apiVersion)).compareTo(new APIVersion(APIRequestMapper.apiSupportedVersion)) > 0) {
                new APIHTTPException("COM0012", new Object[0]).setErrorResponse(servletResponse);
                return;
            }
            if (entity == null || module == null) {
                new APIHTTPException("COM0001", new Object[0]).setErrorResponse(servletResponse);
                return;
            }
            if (apiVersion != null && entity != null && module != null) {
                RequestMapper.Entity.Request request;
                try {
                    request = APIRequestMapper.getRequestForUri(module.toString().toLowerCase(), servletRequest.getPathInfo(), servletRequest.getMethod().toLowerCase());
                }
                catch (final APIHTTPException e) {
                    e.setErrorResponse(servletResponse);
                    return;
                }
                if (request == null) {
                    new APIHTTPException("COM0001", new Object[0]).setErrorResponse(servletResponse);
                }
                else {
                    Long customerId = null;
                    try {
                        customerId = this.getCustomerIdFromRequest(servletRequest);
                    }
                    catch (final APIHTTPException ex) {
                        ex.setErrorResponse(servletResponse);
                        return;
                    }
                    if (customerId != null) {
                        CustomerInfoThreadLocal.setSummaryPage("false");
                        parametersList.put("customer_id", customerId);
                        CustomerInfoThreadLocal.setCustomerId("" + customerId);
                    }
                    parametersList.put("customer_id", customerId);
                    final APIRequest apiRequest = new APIRequest(servletRequest, servletResponse, request);
                    try {
                        final String apiKey = servletRequest.getHeader("Authorization");
                        final HashMap temp = MDMApiFactoryProvider.getAPIUserDetailsUtil().getAPIUserDetails(apiKey, customerId);
                        if (temp == null) {
                            new APIHTTPException("COM0013", new Object[0]).setErrorResponse(servletResponse);
                            return;
                        }
                        parametersList.putAll(temp);
                    }
                    catch (final Exception e2) {
                        if (e2 instanceof APIHTTPException) {
                            ((APIHTTPException)e2).setErrorResponse(servletResponse);
                        }
                        else {
                            new APIHTTPException("COM0004", new Object[0]).setErrorResponse(servletResponse);
                        }
                        return;
                    }
                    apiRequest.setParameterList(parametersList);
                    if (!parametersList.containsKey("login_id")) {
                        final ArrayList userList = DMUserHandler.getDefaultAdministratorRoleUserList();
                        final Hashtable adminTable = userList.get(0);
                        parametersList.put("login_id", adminTable.get("LOGIN_ID"));
                        parametersList.put("user_name", adminTable.get("NAME"));
                        parametersList.put("user_id", adminTable.get("USER_ID"));
                    }
                    this.handleMethodInvocation(apiRequest);
                }
            }
            else {
                new APIHTTPException("COM0001", new Object[0]).setErrorResponse(servletResponse);
            }
        }
        catch (final Exception e3) {
            try {
                new APIHTTPException("COM0004", new Object[0]).setErrorResponse(servletResponse);
            }
            catch (final IOException ex2) {
                this.logger.log(Level.SEVERE, "error while setting error response", ex2);
            }
        }
    }
    
    public void writeOutputResponse(final Object output, final HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        final PrintWriter pout = response.getWriter();
        pout.print(output.toString());
        pout.flush();
        pout.close();
    }
    
    private String[] getPaths(final String pathInfo) {
        String[] paths = null;
        if (pathInfo != null) {
            paths = pathInfo.split("/");
        }
        return paths;
    }
    
    private HashMap constructParametersList(final HashMap parametersList, final HttpServletRequest servletRequest) {
        final String pathInfo = servletRequest.getPathInfo();
        final String[] paths = this.getPaths(pathInfo);
        final String[] servletPath = this.getPaths(servletRequest.getServletPath());
        if (paths != null) {
            final APIUtil apiUtil = MDMRestAPIFactoryProvider.getAPIUtil();
            parametersList.put("apiVersion", apiUtil.getParameterForIndex(servletPath, 2));
            parametersList.put("module", apiUtil.getParameterForIndex(servletPath, 3));
            parametersList.put("entity", apiUtil.getParameterForIndex(paths, 1));
            parametersList.put("operation", apiUtil.getParameterForIndex(paths, 2));
        }
        try {
            final String queryString = servletRequest.getQueryString();
            if (queryString != null && !"".equalsIgnoreCase(queryString)) {
                final String[] queryStrings = queryString.split("&");
                boolean firstQuery = Boolean.TRUE;
                for (final String query : queryStrings) {
                    final String[] queryValues = query.split("=");
                    if (queryValues.length == 2) {
                        final String paramName = queryValues[0];
                        final String paramValue = queryValues[1];
                        parametersList.put(paramName.toLowerCase(), URLDecoder.decode(paramValue, "UTF-8"));
                        if (firstQuery && parametersList.get("operation") == null) {
                            parametersList.put("operation", paramName);
                            firstQuery = Boolean.FALSE;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while constructing parameter list from received request object {0}", e);
        }
        final String userAgentValue = servletRequest.getHeader("User-Agent");
        parametersList.put("User-Agent", userAgentValue);
        if (parametersList.get("operation") == null) {
            parametersList.put("operation", parametersList.get("entity"));
        }
        return parametersList;
    }
    
    private void handleMethodInvocation(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final RequestMapper.Entity.Request request = apiRequest.request;
            final Object classToBeInvoked = request.getClassName();
            final Boolean caseInsensitiveResponse = Boolean.valueOf(request.getCaseInsensitiveResponse());
            if (classToBeInvoked != null) {
                final Class apiClass = Class.forName(classToBeInvoked.toString());
                final StringBuilder methodName = new StringBuilder("do");
                methodName.append(apiRequest.method.substring(0, 1).toUpperCase());
                methodName.append(apiRequest.method.substring(1).toLowerCase());
                final Method apiMethod = apiClass.getMethod(methodName.toString(), APIRequest.class);
                final Object responseObj = apiMethod.invoke(apiClass.newInstance(), apiRequest);
                if (responseObj == null) {
                    return;
                }
                if (responseObj instanceof JSONObject) {
                    final JSONObject returnValue = (JSONObject)responseObj;
                    if (returnValue.getInt("status") == 200 || (returnValue.getInt("status") == 201 && returnValue.has("RESPONSE"))) {
                        JSONObject responseJSON;
                        if (returnValue.get("RESPONSE") instanceof JSONArray) {
                            responseJSON = new JSONObject();
                            responseJSON.put((String)apiRequest.getParameterList().get("entity"), returnValue.get("RESPONSE"));
                        }
                        else {
                            responseJSON = returnValue.getJSONObject("RESPONSE");
                        }
                        apiRequest.httpServletResponse.setStatus(returnValue.getInt("status"));
                        apiRequest.httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
                        apiRequest.httpServletResponse.setCharacterEncoding("UTF-8");
                        final PrintWriter pout = apiRequest.httpServletResponse.getWriter();
                        if (caseInsensitiveResponse != null && caseInsensitiveResponse) {
                            pout.print(new APIUtil().wrapServerJSONToCaseInsensitiveUserJSON(responseJSON).toString());
                        }
                        else {
                            pout.print(new APIUtil().wrapServerJSONToUserJSON(responseJSON).toString());
                        }
                        pout.close();
                        return;
                    }
                    apiRequest.httpServletResponse.setStatus(returnValue.getInt("status"));
                }
                else if (responseObj instanceof JSONArray) {
                    final JSONObject responseJSON2 = new JSONObject();
                    responseJSON2.put((String)apiRequest.getParameterList().get("entity"), (Object)new APIUtil().wrapServerJSONToUserJSON((JSONArray)responseObj));
                    apiRequest.httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
                    apiRequest.httpServletResponse.setCharacterEncoding("UTF-8");
                    final PrintWriter pout2 = apiRequest.httpServletResponse.getWriter();
                    if (caseInsensitiveResponse != null && caseInsensitiveResponse) {
                        pout2.print(new APIUtil().wrapServerJSONToCaseInsensitiveUserJSON(responseJSON2).toString());
                    }
                    else {
                        pout2.print(new APIUtil().wrapServerJSONToUserJSON(responseJSON2).toString());
                    }
                    pout2.flush();
                    pout2.close();
                }
            }
        }
        catch (final ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, "ClassNotFoundException while handling method invocation {0}", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final NoSuchMethodException e2) {
            this.logger.log(Level.SEVERE, "NoSuchMethodException while handling method invocation {0}", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final IllegalAccessException e3) {
            this.logger.log(Level.SEVERE, "IllegalAccessException while handling method invocation {0}", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final InvocationTargetException e4) {
            this.logger.log(Level.SEVERE, "InvocationTargetException while handling method invocation {0}", e4.getTargetException());
            this.logger.log(Level.SEVERE, "InvocationTargetException while handling method invocation cause :", e4.getCause());
            this.logger.log(Level.SEVERE, "InvocationTargetException while handling method invocation exception :", e4);
            if (e4.getTargetException() instanceof APIHTTPException) {
                try {
                    ((APIHTTPException)e4.getTargetException()).setErrorResponse(apiRequest.httpServletResponse);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "IOException while handling method invocation {0}", e4);
                }
                return;
            }
            throw new RuntimeException(e4);
        }
        catch (final Exception e5) {
            this.logger.log(Level.SEVERE, "Exception while handling method invocation {0}", e5);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Long getCustomerIdFromRequest(final HttpServletRequest servletRequest) throws APIHTTPException, DataAccessException {
        Long customerId = null;
        if (servletRequest.getHeader("X-Customer-Name") != null && !servletRequest.getHeader("X-Customer-Name").equalsIgnoreCase("All")) {
            final String customerName = servletRequest.getHeader("X-Customer-Name");
            customerId = CustomerInfoUtil.getInstance().getCustomerId(customerName);
            if (customerId != -1L) {
                final Properties temp = CustomerInfoUtil.getInstance().getAllDetailsOfCustomer(customerId);
                if (temp.isEmpty()) {
                    throw new APIHTTPException("COM0030", new Object[0]);
                }
            }
        }
        else if (servletRequest.getHeader("X-Customer") != null && !servletRequest.getHeader("X-Customer").equalsIgnoreCase("All")) {
            customerId = Long.valueOf(servletRequest.getHeader("X-Customer"));
            final Properties temp = CustomerInfoUtil.getInstance().getAllDetailsOfCustomer(customerId);
            if (temp.isEmpty()) {
                throw new APIHTTPException("COM0022", new Object[0]);
            }
        }
        else {
            customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
        }
        return customerId;
    }
}
