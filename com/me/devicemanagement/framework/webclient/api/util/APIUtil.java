package com.me.devicemanagement.framework.webclient.api.util;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import java.util.TreeMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.lang.reflect.Method;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.me.devicemanagement.framework.server.factory.RestAPIOptionals;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import org.json.JSONException;
import java.util.Iterator;
import java.io.Reader;
import java.io.IOException;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.net.URLDecoder;
import org.json.JSONObject;
import com.adventnet.ds.query.DataSet;
import java.util.Locale;
import com.me.devicemanagement.framework.webclient.api.mapper.RequestMapper;
import java.io.PrintWriter;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONArray;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class APIUtil
{
    private static final String DM_API_UTIL = "DM_API_UTIL";
    private static final String RESTAPIOPTIONALS_CLASS = "DM_RESTAPIOPTIONALS_CLASS";
    public String errorCode;
    public String errorMessage;
    public Object output;
    public static APIUtil apiUtil;
    private Logger logger;
    
    public APIUtil() {
        this.errorCode = null;
        this.errorMessage = null;
        this.output = null;
        this.logger = Logger.getLogger("DCAPILogger");
    }
    
    public static APIUtil getInstance() {
        if (APIUtil.apiUtil == null) {
            APIUtil.apiUtil = new APIUtil();
        }
        return APIUtil.apiUtil;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Object getOutput() {
        return this.output;
    }
    
    public void setOutput(final Object output) {
        this.output = output;
    }
    
    public void processRequest(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws Exception {
        try {
            HashMap parametersList = new HashMap();
            parametersList = this.constructParametersList(parametersList, servletRequest);
            final Object apiVersion = parametersList.get("apiVersion");
            final Object module = parametersList.get("module");
            final Object entity = parametersList.get("entity");
            final Object operation = parametersList.get("operation");
            APIRequestMapper.createRequestMapper();
            if (apiVersion != null) {
                try {
                    Float.parseFloat(String.valueOf(apiVersion));
                }
                catch (final Exception exception) {
                    this.logger.log(Level.SEVERE, "The api version provided in the request is not acceptable", exception);
                    this.setErrorDetails("10022", "API Endpoint is not supported by current server.");
                    this.setOutput(this.constructMessageResponse(null, null, null, "", null));
                    servletResponse.setHeader("Content-Type", "text/plain;charset=UTF-8");
                    servletResponse.setHeader("Cache-Control", "no-store");
                    servletResponse.setHeader("Pragma", "no-cache");
                    servletResponse.setCharacterEncoding("UTF-8");
                    final PrintWriter pout = servletResponse.getWriter();
                    pout.print(this.output.toString());
                    pout.flush();
                    pout.close();
                    return;
                }
            }
            if (apiVersion == null) {
                this.setErrorDetails("10023", "API version not provided.");
                this.setOutput(this.constructMessageResponse(null, null, null, "", apiVersion));
                this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                return;
            }
            if (!this.validateAPIVersion(apiVersion)) {
                if (Float.parseFloat(String.valueOf(apiVersion)) < Float.parseFloat("1.2")) {
                    this.setErrorDetails("10004", "The API Versions 1.0 and 1.1 are no longer supported. Please use API version 1.3.");
                    this.setOutput(this.constructMessageResponse(null, null, null, "", apiVersion));
                    this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                    return;
                }
                this.setErrorDetails("10021", "Request API Version is higher than  supported version.");
                this.setOutput(this.constructMessageResponse(null, null, null, "", apiVersion));
                this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
            }
            else {
                if (entity == null || module == null || this.isInValidAPIURL(parametersList, servletRequest)) {
                    this.setErrorDetails("10022", "API Endpoint is not supported by current server.");
                    this.setOutput(this.constructMessageResponse(null, null, null, "", apiVersion));
                    this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                    return;
                }
                if (apiVersion != null && entity != null && module != null) {
                    final DataObject apiAuthDO = ApiFactoryProvider.getRestApiUtil().getAuthDO(servletRequest);
                    final boolean isValidLicense = this.isValidProductLicense(String.valueOf(parametersList.get("apiVersion")));
                    if (!isValidLicense && !operation.equals("getAPIKeyForSDPUser") && !operation.equals("computers")) {
                        this.setErrorDetails("1011", "Product License is Expired. Upgrade License to use the API");
                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                        return;
                    }
                    final Long loginIDFromMickey = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    final boolean isValidAuthToken = ApiFactoryProvider.getRestApiUtil().authenticateRequest(apiAuthDO, entity);
                    if (loginIDFromMickey == null && !isValidAuthToken) {
                        this.setErrorDetails("10002", "Invalid or expired token");
                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                        return;
                    }
                    final Object integrationHeader = servletRequest.getHeader("DC-Integ-Param");
                    if (loginIDFromMickey != null && integrationHeader != null && !integrationHeader.toString().equalsIgnoreCase("APIExplorer")) {
                        parametersList.put("loginid", loginIDFromMickey);
                        parametersList = ApiFactoryProvider.getRestApiUtil().fillLoginParams(parametersList, null);
                    }
                    else {
                        parametersList = ApiFactoryProvider.getRestApiUtil().fillLoginParams(parametersList, apiAuthDO);
                    }
                    if (ApiFactoryProvider.getRestApiUtil().isCSRFAttackPresent(servletRequest, servletResponse, isValidAuthToken, entity)) {
                        this.setErrorDetails("IAM0010", "The CSRF token does not match");
                        this.setOutput(this.constructMessageResponse(null, null, null, "", apiVersion));
                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                        return;
                    }
                    final Object resourceID = parametersList.get("resID");
                    final RequestMapper.Entity.Request request = APIRequestMapper.getRequestForUri(module.toString().toLowerCase(), entity.toString().toLowerCase(), operation.toString().toLowerCase());
                    if (request == null) {
                        this.setErrorDetails("10022", "API Endpoint is not supported by current server.");
                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                    }
                    else {
                        APIRequest apiRequest = new APIRequest();
                        apiRequest.setParameterList(parametersList);
                        apiRequest.setRequest(request);
                        apiRequest.setHttpServletRequest(servletRequest);
                        apiRequest.setHttpServletResponse(servletResponse);
                        apiRequest = this.setPageLimits(apiRequest);
                        if (this.isInvalidEdition(apiRequest.getRequest().getDenyEdition())) {
                            this.setErrorDetails("1013", "This API is not supported for your license.");
                            this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                            this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                            return;
                        }
                        if (!parametersList.containsKey("loginid")) {
                            final ArrayList userList = DMUserHandler.getDefaultAdministratorRoleUserList();
                            final Hashtable adminTable = userList.get(0);
                            parametersList.put("loginid", adminTable.get("LOGIN_ID"));
                        }
                        final String roleNames = apiRequest.getRequest().getRoleList();
                        final boolean isAuthorizedUser = this.isAuthorizedUser(parametersList.get("loginid"), roleNames, String.valueOf(parametersList.get("apiVersion")));
                        if (!isAuthorizedUser) {
                            this.setErrorDetails("1010", "User is not authorized to access this API");
                            this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                            this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                            return;
                        }
                        final Locale userLocaleFromDB = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocaleFromDB(parametersList.get("loginid"));
                        I18N.setRequestLocale(userLocaleFromDB);
                        apiRequest.getHttpServletRequest().getSession().setAttribute("isAPI", (Object)"true");
                        apiRequest.getHttpServletRequest().getSession().setAttribute("loginid", (Object)parametersList.get("loginid"));
                        apiRequest.getHttpServletRequest().getSession().setAttribute("userid", (Object)parametersList.get("userid"));
                        apiRequest.getHttpServletRequest().getSession().setAttribute("username", (Object)parametersList.get("username"));
                        if (!parametersList.containsKey("customername") || parametersList.get("customername") == null) {
                            if (parametersList.containsKey("resid") || parametersList.containsKey("resource_id")) {
                                Long resID;
                                if (parametersList.get("resid") != null) {
                                    resID = Long.valueOf(parametersList.get("resid").toString());
                                }
                                else {
                                    resID = Long.valueOf(parametersList.get("resource_id").toString());
                                }
                                final ArrayList<Long> resourceList = new ArrayList<Long>();
                                resourceList.add(resID);
                                final Long loginID = parametersList.get("loginid");
                                if (loginID != null && !resourceList.isEmpty()) {
                                    final boolean isManaged = this.isResourcesManagedByTech(loginID, resourceList);
                                    if (!isManaged) {
                                        this.setErrorDetails("1012", "Provided resource is not managed by the user");
                                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                                        return;
                                    }
                                }
                            }
                            if (parametersList.containsKey("resourceids") || parametersList.containsKey("resource_ids")) {
                                JSONArray resIDS = parametersList.get("resourceids");
                                if (resIDS == null) {
                                    resIDS = parametersList.get("resource_ids");
                                }
                                final Long loginID2 = parametersList.get("loginid");
                                final List resourceList2 = this.castArrayToList(resIDS);
                                if (loginID2 != null && !resourceList2.isEmpty()) {
                                    final boolean isManaged = this.isResourcesManagedByTech(loginID2, resourceList2);
                                    if (!isManaged) {
                                        this.setErrorDetails("1012", "Provided resource is not managed by the user");
                                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                                        return;
                                    }
                                }
                            }
                        }
                        if (CustomerInfoUtil.getInstance().isMSP() && !this.skipMSPCheck(entity.toString()) && !this.skipCheckByModule(module.toString())) {
                            if (parametersList.containsKey("customername") && parametersList.get("customername") != null) {
                                final String cusName = parametersList.get("customername");
                                final Long customerid = CustomerInfoUtil.getInstance().getCustomerId(cusName);
                                if (customerid != null && customerid != -1L) {
                                    parametersList.put("customerid", String.valueOf(customerid));
                                }
                                if (customerid != null && customerid == -1L) {
                                    this.setErrorDetails("1010", "Customer is not available in EndpointCentral");
                                    this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                    this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                                    return;
                                }
                            }
                            if (parametersList.containsKey("customerid") && parametersList.get("customerid") != null) {
                                final Long loginID3 = parametersList.get("loginid");
                                final String cusID = parametersList.get("customerid");
                                final ArrayList loginIDForcustomer = CustomerInfoUtil.getInstance().getUsersForCustomer(Long.valueOf(cusID));
                                if (!loginIDForcustomer.contains(loginID3)) {
                                    this.setErrorDetails("1006", "Customer ID value is not Associated to this user.");
                                    this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                    this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                                    return;
                                }
                                if (parametersList.containsKey("resourceids") || parametersList.containsKey("resource_ids")) {
                                    JSONArray resIDS2 = parametersList.get("resourceids");
                                    if (resIDS2 == null) {
                                        resIDS2 = parametersList.get("resource_ids");
                                    }
                                    final List resourceList3 = this.castArrayToList(resIDS2);
                                    if (!this.isResourcesManagedByCustomer(cusID, resourceList3)) {
                                        this.setErrorDetails("1007", "Resource ID value is not associated to this customer.");
                                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                                        return;
                                    }
                                }
                                if (parametersList.containsKey("resid") || parametersList.containsKey("resource_id")) {
                                    Long resID2;
                                    if (parametersList.get("resid") != null) {
                                        resID2 = Long.valueOf(parametersList.get("resid").toString());
                                    }
                                    else {
                                        resID2 = Long.valueOf(parametersList.get("resource_id").toString());
                                    }
                                    final ArrayList<Long> resourceList4 = new ArrayList<Long>();
                                    resourceList4.add(resID2);
                                    if (!this.isResourcesManagedByCustomer(cusID, resourceList4)) {
                                        this.setErrorDetails("1007", "Resource ID value is not associated to this customer.");
                                        this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                                        return;
                                    }
                                }
                                CustomerInfoThreadLocal.setSummaryPage("false");
                                CustomerInfoThreadLocal.setIsClientCall("true");
                                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
                                CustomerInfoThreadLocal.setCustomerId(cusID);
                                MSPWebClientUtil.setCustomerInCookie(servletRequest, servletResponse, cusID);
                                MSPWebClientUtil.setSummaryPageInCookie(servletRequest, servletResponse, "false");
                            }
                            else {
                                this.setErrorDetails("1008", "Customer ID param is Mandatory for MSP");
                                this.setOutput(this.constructMessageResponse(null, null, null, entity.toString(), apiVersion));
                                this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                            }
                        }
                        this.handleMethodInvocation(apiRequest);
                        this.setOutput(this.constructMessageResponse(apiRequest, this.getOutput(), resourceID, entity.toString(), apiVersion));
                        this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                    }
                }
                else {
                    this.setErrorDetails("1004", "Invalid request. Please correct the request and try again.");
                    this.setOutput(this.constructMessageResponse(null, null, null, "", apiVersion));
                    this.writeOutputResponse(apiVersion, this.getOutput(), servletResponse);
                }
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean validateAPIVersion(final Object requestedVersion) {
        try {
            final float requestVersion = Float.parseFloat(String.valueOf(requestedVersion));
            final float serverApiVersion = Float.parseFloat(APIRequestMapper.apiSupportedVersion);
            final float unsupportedApiVersion = Float.parseFloat("1.2");
            if (requestVersion > serverApiVersion || requestVersion < unsupportedApiVersion) {
                return false;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while parsing the api version from {0}", e);
            return false;
        }
        return true;
    }
    
    private APIRequest setPageLimits(final APIRequest apiRequest) {
        final HashMap parameterList = apiRequest.getParameterList();
        if (parameterList.containsKey("page")) {
            apiRequest.setPageIndex(Integer.parseInt(parameterList.get("page").toString()));
        }
        if (parameterList.containsKey("pagelimit")) {
            apiRequest.setPageLimit(Integer.parseInt(parameterList.get("pagelimit").toString()));
        }
        if (parameterList.containsKey("sortorder")) {
            apiRequest.setSortorder(parameterList.get("sortorder").toString());
        }
        if (parameterList.containsKey("orderby")) {
            apiRequest.setOrderby(parameterList.get("orderby").toString());
        }
        return apiRequest;
    }
    
    public JSONArray constructJSONFromDS(final DataSet dataSet) {
        final JSONArray jsonArray = new JSONArray();
        final ArrayList longValueColumnList = new ArrayList();
        longValueColumnList.add("resource_id");
        longValueColumnList.add("package_id");
        try {
            if (dataSet != null) {
                final int columnCount = dataSet.getColumnCount();
                while (dataSet.next()) {
                    final JSONObject jsonObject = new JSONObject();
                    for (int i = 1; i <= columnCount; ++i) {
                        final String columnName = dataSet.getColumnName(i);
                        final Object columnValue = dataSet.getValue(i);
                        if (!jsonObject.has(columnName.toLowerCase())) {
                            if (longValueColumnList.contains(columnName.toLowerCase())) {
                                jsonObject.put(columnName.toLowerCase() + "_string", (Object)((columnValue == null) ? "--" : String.valueOf(columnValue)));
                            }
                            jsonObject.put(columnName.toLowerCase(), (columnValue == null) ? "--" : columnValue);
                        }
                    }
                    jsonArray.put((Object)jsonObject);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while constructing JSON object from DataSet {0}", e);
        }
        return jsonArray;
    }
    
    public void writeOutputResponse(final Object apiVersion, final Object output, final HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        if (apiVersion != null) {
            final float requestVersion = Float.parseFloat(String.valueOf(apiVersion));
            if (requestVersion < 1.2) {
                response.setHeader("Warning", "Deprecated API Version.Use latest API Version " + APIRequestMapper.apiSupportedVersion);
            }
        }
        response.setCharacterEncoding("UTF-8");
        final PrintWriter pout = response.getWriter();
        pout.print(output.toString());
        pout.flush();
        pout.close();
    }
    
    public String[] getPaths(final String pathInfo) {
        String[] paths = null;
        if (pathInfo != null) {
            paths = pathInfo.split("/");
        }
        return paths;
    }
    
    public HashMap constructParametersList(final HashMap parametersList, final HttpServletRequest servletRequest) {
        final String pathInfo = servletRequest.getPathInfo();
        final String[] paths = this.getPaths(pathInfo);
        if (paths != null) {
            parametersList.put("apiVersion", this.getParameterForIndex(paths, 1));
            parametersList.put("module", this.getParameterForIndex(paths, 2));
            parametersList.put("entity", this.getParameterForIndex(paths, 3));
            parametersList.put("operation", this.getParameterForIndex(paths, 4));
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
        Reader reader = null;
        try {
            final String contentType = servletRequest.getContentType();
            if ("application/json".equalsIgnoreCase(contentType)) {
                final StringBuilder requestData = new StringBuilder();
                reader = SYMClientUtil.getInstance().getProperEncodedReader(servletRequest, reader);
                int read = 0;
                final char[] chBuf = new char[500];
                while ((read = reader.read(chBuf)) > -1) {
                    requestData.append(chBuf, 0, read);
                }
                if (requestData.length() > 0) {
                    final JSONObject jsonObject = new JSONObject(requestData.toString());
                    if (jsonObject.length() > 0) {
                        final Iterator keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            final String key = keys.next().toString();
                            parametersList.put(key.toLowerCase(), jsonObject.get(key));
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while constructing parameter list from received request object {0}", e2);
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e3) {
                this.logger.log(Level.SEVERE, "Exception while closing the reader object {0}", e3);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e4) {
                this.logger.log(Level.SEVERE, "Exception while closing the reader object {0}", e4);
            }
        }
        final String userAgentValue = servletRequest.getHeader("User-Agent");
        parametersList.put("User-Agent", userAgentValue);
        if (parametersList.get("operation") == null) {
            parametersList.put("operation", parametersList.get("entity"));
        }
        return parametersList;
    }
    
    private Object getParameterForIndex(final String[] paths, final int index) {
        try {
            if (index < paths.length) {
                return paths[index];
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting parameter for the index" + index + ": {0}", e);
        }
        return null;
    }
    
    public String constructMessageResponse(final APIRequest apiRequest, final Object output, final Object resourceID, final String messageType, final Object apiVersion) throws Exception {
        final JSONObject successResponse = new JSONObject();
        try {
            successResponse.put("message_type", (Object)messageType);
            if (apiVersion != null) {
                successResponse.put("message_version", (Object)String.valueOf(apiVersion));
            }
            successResponse.put("status", (Object)"error");
            successResponse.put("message_response", (Object)"Unable to retrieve details, Please try again later.");
            if (this.getErrorCode() != null) {
                successResponse.put("error_code", (Object)this.getErrorCode());
                successResponse.put("error_description", (Object)this.getErrorMessage());
                successResponse.remove("message_response");
            }
            else if (output != null) {
                final JSONObject messageResponse = new JSONObject();
                if (output instanceof JSONObject) {
                    successResponse.put("status", (Object)"success");
                    messageResponse.put(messageType.toLowerCase(), (Object)new JSONObject(output.toString()));
                }
                else if (output instanceof JSONArray) {
                    successResponse.put("status", (Object)"success");
                    messageResponse.put(messageType.toLowerCase(), (Object)new JSONArray(output.toString()));
                }
                if (apiRequest.getRequest().getViewConfiguration().getCvName() != null) {
                    messageResponse.put("page", apiRequest.getPageIndex());
                    messageResponse.put("limit", apiRequest.getPageLimit());
                    messageResponse.put("total", apiRequest.getTotalRecords());
                }
                successResponse.put("message_response", (Object)messageResponse);
            }
            if (resourceID != null) {
                successResponse.put("ResourceID", resourceID);
            }
        }
        catch (final JSONException e) {
            throw new RuntimeException((Throwable)e);
        }
        return successResponse.toString();
    }
    
    public void handleMethodInvocation(final APIRequest apiRequest) throws Exception {
        try {
            final RequestMapper.Entity.Request request = apiRequest.getRequest();
            final JSONObject apiDetails = new JSONObject();
            if (apiRequest.getHttpServletRequest() != null) {
                final HttpServletRequest servletRequest = apiRequest.getHttpServletRequest();
                final HashMap parametersList = apiRequest.getParameterList();
                if (apiRequest.getHttpServletRequest().getHeader("DC-Integ-Param") != null) {
                    apiDetails.put("integrationName", (Object)apiRequest.getHttpServletRequest().getHeader("DC-Integ-Param"));
                }
                apiDetails.put("requestUri", (Object)request.getUri());
                if (request.getOperationName() != null) {
                    apiDetails.put("operationName", (Object)request.getOperationName());
                }
                apiDetails.put("request_method", (Object)servletRequest.getMethod());
                final String[] classNames = ProductClassLoader.getMultiImplProductClass("DM_RESTAPIOPTIONALS_CLASS");
                if (classNames != null && classNames[0].trim().length() > 0) {
                    for (final String className : classNames) {
                        final RestAPIOptionals restAPIOptionals = (RestAPIOptionals)Class.forName(className).newInstance();
                        if (restAPIOptionals.preInvoker(parametersList, apiDetails) == FrameworkStatusCodes.NO_CONTENT_RESPONSE_CODE) {
                            this.logger.log(Level.SEVERE, "Error code returned while invoking prePostInvoker method in RestAPIOptionals");
                        }
                    }
                }
            }
            final Object classToBeInvoked = request.getClassName();
            if (classToBeInvoked != null) {
                final Class apiClass = Class.forName(classToBeInvoked.toString());
                final Method apiMethod = apiClass.getMethod(request.getMethodName(), APIRequest.class);
                final Object returnValue = apiMethod.invoke(apiClass.newInstance(), apiRequest);
                if (returnValue != null) {
                    this.checkForError(returnValue);
                    this.setOutput(returnValue);
                }
                else {
                    this.getErrorObject("1003", "Unable to retrieve details, Please try again later.");
                }
            }
        }
        catch (final ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, "ClassNotFoundException while handling method invocation {0}", e);
            throw e;
        }
        catch (final NoSuchMethodException e2) {
            this.logger.log(Level.SEVERE, "NoSuchMethodException while handling method invocation {0}", e2);
            throw e2;
        }
        catch (final IllegalAccessException e3) {
            this.logger.log(Level.SEVERE, "IllegalAccessException while handling method invocation {0}", e3);
            throw e3;
        }
        catch (final Exception e4) {
            this.logger.log(Level.SEVERE, "Exception while handling method invocation {0}", e4);
            throw new RuntimeException(e4);
        }
    }
    
    private void checkForError(final Object returnedObject) {
        try {
            if (returnedObject instanceof JSONObject && ((JSONObject)returnedObject).opt("error_code") != null) {
                this.setErrorDetails(((JSONObject)returnedObject).get("error_code"), ((JSONObject)returnedObject).get("error_description"));
            }
            else if (returnedObject instanceof JSONArray && ((JSONArray)returnedObject).length() > 0) {
                final JSONObject jsonObject = ((JSONArray)returnedObject).getJSONObject(0);
                if (jsonObject.opt("error_code") != null) {
                    this.setErrorDetails(jsonObject.get("error_code"), jsonObject.get("error_description"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while error in output to be sent for the receive request {0}", e);
        }
    }
    
    public void setErrorDetails(final Object errorCode, final Object errorDes) {
        this.setErrorCode(errorCode.toString());
        this.setErrorMessage(errorDes.toString());
    }
    
    public JSONObject getErrorObject(final String errorCode, final String errorMessage) {
        final JSONObject jsonObject = new JSONObject();
        try {
            this.setErrorDetails(errorCode, errorMessage);
            jsonObject.put("error_code", (Object)this.errorCode);
            jsonObject.put("error_description", (Object)this.errorMessage);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting error object {0}", e);
        }
        return jsonObject;
    }
    
    boolean checkForErrorCode(final Object returnedObject) {
        try {
            if (returnedObject instanceof JSONObject && ((JSONObject)returnedObject).opt("error_code") != null) {
                return true;
            }
            if (returnedObject instanceof JSONArray) {
                final JSONObject jsonObject = ((JSONArray)returnedObject).getJSONObject(0);
                if (jsonObject.opt("error_code") != null) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking for error code {0}", e);
        }
        return false;
    }
    
    public HashMap fillLoginParams(final HashMap parametersList, final DataObject apiAuthDO) throws DataAccessException {
        if (parametersList.containsKey("loginid") && apiAuthDO == null) {
            try {
                final Long userid = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final String username = DMUserHandler.getUserNameFromUserID(userid);
                parametersList.put("userid", userid);
                parametersList.put("username", username);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception while filling Login Params from mickey", ex);
            }
        }
        else if (apiAuthDO != null && !apiAuthDO.isEmpty()) {
            final Row aaaLoginRow = apiAuthDO.getRow("AaaLogin");
            parametersList.put("userid", aaaLoginRow.get("USER_ID"));
            parametersList.put("username", aaaLoginRow.get("NAME"));
            parametersList.put("loginid", aaaLoginRow.get("LOGIN_ID"));
        }
        return parametersList;
    }
    
    public boolean skipAuthenticationForOperation(final String operation) {
        return !operation.equalsIgnoreCase("Discover") && !operation.equalsIgnoreCase("Authentication");
    }
    
    public boolean checkAnnouncementAPI(final HttpServletRequest request) {
        final String pathInfo = request.getPathInfo().toLowerCase();
        return pathInfo.contains("announcement");
    }
    
    public JSONArray setPageLength(final APIRequest apiRequest, final JSONArray jsonArray) throws JSONException {
        final int pageLength = apiRequest.getPageLimit();
        final int pageIndex = apiRequest.getPageIndex();
        final int startIndex = pageLength * pageIndex - (pageLength - 1);
        final JSONArray tempArray = new JSONArray();
        if (jsonArray.length() > 0) {
            for (int i = startIndex - 1; i < jsonArray.length() && i < startIndex + pageLength - 1; ++i) {
                tempArray.put((Object)jsonArray.getJSONObject(i));
            }
        }
        return tempArray;
    }
    
    public TreeMap<String, Object> getRolesListFromAuthKey(String authKey, final String serviceType) throws Exception {
        final TreeMap<String, Object> rolesListFromAuthKeyWithLoginID = new TreeMap<String, Object>();
        TreeMap<String, Long> authorizedRoleMap = new TreeMap<String, Long>();
        try {
            if (authKey == null || "".equals(authKey) || "null".equalsIgnoreCase(authKey)) {
                this.logger.log(Level.INFO, "Authentication key is null or empty. Unable to authenticate.");
                return null;
            }
            authKey = AuthenticationKeyUtil.getInstance().getEncryptedTechKey(authKey, serviceType);
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("APIKeyDetails"));
            final Join apiKeyDefnJoin = new Join("APIKeyDetails", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1);
            selectQuery.addJoin(apiKeyDefnJoin);
            selectQuery.addSelectColumn(Column.getColumn("APIKeyDetails", "*"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
            final Criteria authKeyCri = new Criteria(new Column("APIKeyDetails", "APIKEY"), (Object)authKey, 0);
            selectQuery.setCriteria(authKeyCri);
            final DataObject loginAccountIDDO = DataAccess.get((SelectQuery)selectQuery);
            if (loginAccountIDDO != null && !loginAccountIDDO.isEmpty()) {
                try {
                    final Row accRow = loginAccountIDDO.getRow("AaaAccount");
                    final Long accId = (Long)accRow.get("ACCOUNT_ID");
                    final Long loginId = (Long)accRow.get("LOGIN_ID");
                    rolesListFromAuthKeyWithLoginID.put("LOGIN_ID", loginId);
                    authorizedRoleMap = DMUserHandler.getAuthorizedRolesForAccId(accId);
                    rolesListFromAuthKeyWithLoginID.put("ROLES_LIST_FOR_AUTHKEY", authorizedRoleMap);
                    return rolesListFromAuthKeyWithLoginID;
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception when trying to get Login Account ID from Authentication Key from DO... " + ex.getMessage(), ex);
                    throw ex;
                }
            }
            this.logger.log(Level.INFO, "Authentication Key not present for any user - auth fail - Unable to get RoleList for this AuthKey");
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in getRolesListFromAuthKey....." + exp);
            throw exp;
        }
        return rolesListFromAuthKeyWithLoginID;
    }
    
    public boolean isResourcesManagedByTech(final Long loginID, final List resourceIDList) throws Exception {
        final String[] classname = ProductClassLoader.getMultiImplProductClass("DM_API_UTIL");
        boolean isManged = false;
        DMApi dmApi = null;
        for (final String className : classname) {
            dmApi = (DMApi)Class.forName(className).newInstance();
            isManged = dmApi.isResourcesManagedByTech(loginID, resourceIDList);
        }
        return isManged;
    }
    
    public boolean isResourcesManagedByCustomer(final String customerID, final List resourceIDList) throws Exception {
        final String[] classname = ProductClassLoader.getMultiImplProductClass("DM_API_UTIL");
        boolean isManged = false;
        DMApi dmApi = null;
        for (final String className : classname) {
            dmApi = (DMApi)Class.forName(className).newInstance();
            isManged = dmApi.isResourcesManagedByCustomer(customerID, resourceIDList);
        }
        return isManged;
    }
    
    private List castArrayToList(final JSONArray objectList) throws Exception {
        final ArrayList<Long> objInLongList = new ArrayList<Long>();
        for (int i = 0; i < objectList.length(); ++i) {
            objInLongList.add(Long.parseLong(String.valueOf(objectList.get(i))));
        }
        return objInLongList;
    }
    
    private boolean skipMSPCheck(final String entity) {
        return entity.equals("authentication") || entity.equals("discover") || entity.equals("customers") || entity.equals("users") || entity.equals("moduleurl") || entity.equals("serverinfo");
    }
    
    private boolean skipCheckByModule(final String module) {
        return module.equals("fwserver");
    }
    
    private boolean isAuthorizedUser(final Long loginId, final String roleNames, final String apiVersion) {
        try {
            final String className = ProductClassLoader.getSingleImplProductClass("DM_API_UTIL");
            if (className != null && className.trim().length() != 0) {
                final DMApi dmApi = (DMApi)Class.forName(className).newInstance();
                return dmApi.getRolesAndCheckAuthorization(loginId, roleNames, apiVersion);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occured while check role previledge for Users :", e);
        }
        return false;
    }
    
    private boolean isValidProductLicense(final String apiVersion) {
        try {
            final String className = ProductClassLoader.getSingleImplProductClass("DM_API_UTIL");
            if (className != null && className.trim().length() != 0) {
                final DMApi dmApi = (DMApi)Class.forName(className).newInstance();
                return dmApi.checkProductValidity(apiVersion);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occured while check role previledge for Users :", e);
        }
        return false;
    }
    
    public boolean isInvalidEdition(final List denyEditionList) throws Exception {
        if (denyEditionList == null) {
            return false;
        }
        final String[] classname = ProductClassLoader.getMultiImplProductClass("DM_API_UTIL");
        boolean isInvalidEdition = false;
        DMApi dmApi = null;
        for (final String className : classname) {
            dmApi = (DMApi)Class.forName(className).newInstance();
            isInvalidEdition = dmApi.isInvalidEdition(denyEditionList);
        }
        return isInvalidEdition;
    }
    
    public boolean isInValidAPIURL(final HashMap parameterList, final HttpServletRequest httpServletRequest) throws Exception {
        final String pathInfo = httpServletRequest.getPathInfo();
        final String[] paths = this.getPaths(pathInfo);
        final Object extraInfoInURL = this.getParameterForIndex(paths, 5);
        if (extraInfoInURL != null) {
            return true;
        }
        final Object module = this.getParameterForIndex(paths, 2);
        final Object entity = this.getParameterForIndex(paths, 3);
        final Object operation = this.getParameterForIndex(paths, 4);
        if (operation != null) {
            final RequestMapper.Entity.Request request = APIRequestMapper.getRequestForUri(module.toString().toLowerCase(), entity.toString().toLowerCase(), operation.toString().toLowerCase());
            if (request == null) {
                return true;
            }
        }
        return false;
    }
    
    static {
        APIUtil.apiUtil = null;
    }
}
