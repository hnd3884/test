package com.adventnet.client.view.web;

import java.io.PrintWriter;
import com.zoho.mickeyclient.action.HttpUtil;
import com.adventnet.client.util.web.JSUtil;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.struts.action.ActionForward;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.client.util.DataUtils;
import com.adventnet.client.view.State;
import com.adventnet.client.cache.web.ClientDataObjectCache;
import java.util.ArrayList;
import com.adventnet.client.cache.web.CacheConfiguration;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.client.util.StaticLists;
import com.adventnet.client.util.LookUpUtil;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class WebViewAPI implements JavaScriptConstants, WebConstants
{
    public static final Logger LOGGER;
    
    public static String getParamsForView(final DataObject viewConfig, final HttpServletRequest request) {
        try {
            if (!viewConfig.containsTable("TemplateViewParams")) {
                return null;
            }
            final String viewName = (String)viewConfig.getFirstValue("ViewConfiguration", 2);
            final StringBuffer urlStuff = new StringBuffer();
            final Iterator<Row> ite = viewConfig.getRows("TemplateViewParams");
            while (ite.hasNext()) {
                final Row r = ite.next();
                String paramName = (String)r.get(2);
                final String[] values = request.getParameterValues(paramName);
                if (values == null) {
                    if (r.get(3)) {
                        throw new RuntimeException("The view " + viewName + " needs " + paramName + " parameter");
                    }
                    continue;
                }
                else {
                    for (String value : values) {
                        try {
                            paramName = IAMEncoder.encodeURL(paramName);
                            value = IAMEncoder.encodeURL(value);
                        }
                        catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                        urlStuff.append(paramName).append('=').append(value);
                        urlStuff.append('&');
                    }
                }
            }
            if (urlStuff.length() > 0) {
                urlStuff.deleteCharAt(urlStuff.length() - 1);
            }
            return urlStuff.toString();
        }
        catch (final DataAccessException dae) {
            throw new RuntimeException((Throwable)dae);
        }
    }
    
    public static HashMap<String, Object> getURLStateParameterMap(final HttpServletRequest request) {
        final HashMap<String, Object> urlstatemap = new HashMap<String, Object>();
        final Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String paramname = names.nextElement();
            if (paramname.contains("s:")) {
                final String paramvalue = request.getParameter(paramname);
                paramname = paramname.substring(paramname.indexOf(58) + 1);
                urlstatemap.put(paramname, paramvalue);
            }
        }
        return urlstatemap;
    }
    
    public static String getAsURLStateParameters(final HashMap urlstatemap) {
        final Iterator keys = urlstatemap.keySet().iterator();
        final StringBuffer urlstateparams = new StringBuffer();
        while (keys.hasNext()) {
            final Object key = keys.next();
            final Object value = urlstatemap.get(key);
            urlstateparams.append("s:" + key + "=" + value + "&");
        }
        return urlstateparams.toString();
    }
    
    public static String getCombinedURLStateParameters(final HttpServletRequest request, final HashMap params) {
        final HashMap<String, Object> urlstatemap = getURLStateParameterMap(request);
        for (final String key : params.keySet()) {
            final Object value = params.get(key);
            urlstatemap.put(key, value);
        }
        return getAsURLStateParameters(urlstatemap);
    }
    
    public static DataObject getViewConfiguration(final Object viewName) throws Exception {
        final Row viewConfigRow = new Row("ViewConfiguration");
        if (viewName instanceof String) {
            viewConfigRow.set(2, viewName);
        }
        else if (viewName instanceof Long) {
            viewConfigRow.set(1, viewName);
        }
        return LookUpUtil.getPersistence().getForPersonalities((List)StaticLists.VIEWCONFIGURATIONPERS, (List)StaticLists.VIEWCONFIGURATIONPERS, viewConfigRow);
    }
    
    public static Long getViewNameNo(final Object viewName) {
        try {
            final DataObject dobj = getConfigModel(viewName, false).getViewConfiguration();
            return (Long)dobj.getFirstValue("ViewConfiguration", "VIEWNAME_NO");
        }
        catch (final Exception exp) {
            WebViewAPI.LOGGER.log(Level.FINEST, "Exception occurred when trying to get viewname_no for the given view name : " + viewName + "::" + exp.getMessage());
            return null;
        }
    }
    
    public static String getViewName(final Object viewNameNo) {
        if (viewNameNo == null) {
            return null;
        }
        try {
            final DataObject dobj = getConfigModel(viewNameNo, false).getViewConfiguration();
            return (String)dobj.getFirstValue("ViewConfiguration", "VIEWNAME");
        }
        catch (final Exception exp) {
            WebViewAPI.LOGGER.log(Level.WARNING, "Exception occurred when trying to get viewname for the given view name no : " + viewNameNo + " :: " + exp.getMessage());
            return null;
        }
    }
    
    public static String getMenuName(final Object menuNameNo) {
        try {
            final Persistence persistence = LookUpUtil.getPersistence();
            final Column id = new Column("Menu", "MENUID_NO");
            final Criteria cri = new Criteria(id, menuNameNo, 0);
            final DataObject dobj = persistence.get("Menu", cri);
            final Row menuRow = dobj.getFirstRow("Menu");
            return (String)menuRow.get("MENUID");
        }
        catch (final Exception e) {
            WebViewAPI.LOGGER.log(Level.INFO, "Exception while searching menuName for given menuNameNo");
            return null;
        }
    }
    
    public static WebViewModel getConfigModel(final Object originalViewName, final boolean fetchPersonalizedView) {
        Object viewName = originalViewName;
        final long accountID = -1L;
        if (fetchPersonalizedView) {
            viewName = UserPersonalizationAPI.getPersonalizedViewName(originalViewName, WebClientUtil.getAccountId());
            if (viewName == null) {
                viewName = originalViewName;
            }
        }
        CacheConfiguration cacheConfig = new CacheConfiguration(accountID, viewName.toString(), null, "CONFIG_DATA", "ACCOUNT", null, null);
        final WebViewModel model = (WebViewModel)ClientDataObjectCache.getFromCache(cacheConfig);
        if (model != null) {
            return model;
        }
        try {
            final DataObject viewConfig = getViewConfiguration(viewName);
            if (!viewConfig.containsTable("ViewConfiguration")) {
                throw new RuntimeException("The component configuration " + originalViewName + " passed is not present in the database");
            }
            final WebViewModel vm = new WebViewModel(viewConfig);
            final Long compName = (Long)viewConfig.getFirstValue("ViewConfiguration", 3);
            if (compName != null) {
                vm.setUIComponentConfig(getUIComponentConfig(compName));
            }
            vm.setController(getViewController(vm.getViewConfiguration()));
            if (viewName instanceof Long) {
                final String str = (String)viewConfig.getFirstValue("ViewConfiguration", 2);
                cacheConfig = new CacheConfiguration(accountID, viewName.toString(), null, "CONFIG_DATA", "ACCOUNT", vm, null);
                ClientDataObjectCache.addToCache(cacheConfig);
                cacheConfig = new CacheConfiguration(accountID, str, null, "CONFIG_DATA", "ACCOUNT", vm, null);
                ClientDataObjectCache.addToCache(cacheConfig);
            }
            else {
                final Long no = (Long)viewConfig.getFirstValue("ViewConfiguration", 1);
                cacheConfig = new CacheConfiguration(accountID, (String)viewName, null, "CONFIG_DATA", "ACCOUNT", vm, null);
                ClientDataObjectCache.addToCache(cacheConfig);
                cacheConfig = new CacheConfiguration(accountID, no.toString(), null, "CONFIG_DATA", "ACCOUNT", vm, null);
                ClientDataObjectCache.addToCache(cacheConfig);
            }
            return vm;
        }
        catch (final Exception ex) {
            throw new RuntimeException("Exception occurred while fetching the ViewConfiguration personality DO for the given view id : " + originalViewName, ex);
        }
    }
    
    @Deprecated
    public static WebViewModel getConfigModel(final Object originalViewName, final boolean fetchPersonalizedView, final State state) {
        return getConfigModel(originalViewName, fetchPersonalizedView);
    }
    
    public static String getViewForwardURL(final String viewName, String uniqueId, final String queryString) {
        if (uniqueId == null) {
            uniqueId = viewName;
        }
        return '/' + viewName + ".cc?" + "UNIQUE_ID" + "=" + uniqueId + ((queryString != null) ? ("&" + queryString) : "");
    }
    
    public static String getUIComponentName(final ViewContext viewContext) throws DataAccessException {
        if (viewContext != null) {
            final DataObject uiDO = viewContext.getModel().getUIComponentConfig();
            if (uiDO != null) {
                return (String)uiDO.getFirstValue("UIComponent", "NAME");
            }
        }
        return null;
    }
    
    @Deprecated
    public static boolean isEmberComponent(final ViewContext viewContext) throws DataAccessException {
        if (viewContext != null) {
            final DataObject uiDO = viewContext.getModel().getUIComponentConfig();
            return uiDO != null && (boolean)uiDO.getValue("WebUIComponent", "ISCSRCOMPONENT", (Criteria)null);
        }
        return false;
    }
    
    public static boolean isFilterComponent(final ViewContext viewContext) throws DataAccessException {
        return viewContext != null && viewContext.getModel().getViewConfiguration().containsTable("ACTableFilterListRel");
    }
    
    @Deprecated
    public static boolean isEmberComponent(final Object componentName) throws Exception {
        return (boolean)getUIComponentConfig(componentName).getValue("WebUIComponent", "ISCSRCOMPONENT", (Criteria)null);
    }
    
    public static boolean isCSRComponent(final String viewName) throws Exception {
        final Long componentName_No = (Long)getViewConfiguration(viewName).getValue("ViewConfiguration", 3, (Criteria)null);
        return componentName_No != null && (boolean)getUIComponentConfig(componentName_No).getValue("WebUIComponent", "ISCSRCOMPONENT", (Criteria)null);
    }
    
    public static String getViewTemplateName(final ViewContext viewContext) throws DataAccessException {
        if (viewContext == null) {
            return null;
        }
        final DataObject viewConfigDo = viewContext.getModel().getViewConfiguration();
        String templateName = (String)viewConfigDo.getValue("ViewConfiguration", "TEMPLATENAME", (Row)null);
        if (templateName == null) {
            final DataObject uiConfigDO = viewContext.getModel().getUIComponentConfig();
            if (uiConfigDO != null && uiConfigDO.containsTable("WebUIComponent")) {
                templateName = uiConfigDO.getRow("WebUIComponent").get("TEMPLATENAME").toString();
            }
            else {
                templateName = "htmlView";
            }
        }
        return templateName;
    }
    
    public static ViewContext getRootViewContext(final HttpServletRequest request) {
        ViewContext viewCtx = (ViewContext)request.getAttribute("ROOT_VIEW_CTX");
        if (viewCtx == null) {
            final String uniqueId = (String)StateAPI.getRequestState("_RVID");
            if (uniqueId != null) {
                viewCtx = ViewContext.getViewContext(uniqueId, request);
            }
        }
        return viewCtx;
    }
    
    public static String getRootViewURL(final HttpServletRequest request) {
        return getViewURL(request, (String)StateAPI.getRequiredState("_REQS", "_RVID"));
    }
    
    public static String getRootView(final HttpServletRequest request, final ViewContext viewContext) {
        String rootview = (String)request.getAttribute("rootview");
        if (rootview == null) {
            rootview = (String)viewContext.getURLStateParameter("rootview");
        }
        if (rootview == null) {
            rootview = getRootViewContext(request).toString();
        }
        return rootview;
    }
    
    public static String getViewURL(final HttpServletRequest request, final String viewUniqueId) {
        final ViewContext viewCtx = ViewContext.getViewContext(viewUniqueId, request);
        final String viewName = (String)viewCtx.getStateParameter("_VN");
        final String params = (String)viewCtx.getStateOrURLStateParameter("_D_RP");
        final String url = "/" + viewName + ".cc?" + "UNIQUE_ID" + "=" + viewCtx.getUniqueId() + ((params != null) ? ("&" + params) : "");
        return url;
    }
    
    public static String getViewUrlWithoutQueryString(final String viewName) {
        return "/" + viewName + ".cc";
    }
    
    public static String getAdditionalParamsAsURLQueryString(final DataObject viewConfig) {
        try {
            if (!viewConfig.containsTable("AdditionalViewParams")) {
                return "";
            }
            final StringBuffer addParamsUrl = new StringBuffer();
            final List addParams = DataUtils.getSortedList(viewConfig, "AdditionalViewParams", "PARAMINDEX");
            for (int i = 0; i < addParams.size(); ++i) {
                final Row addParam = addParams.get(i);
                addParamsUrl.append(addParam.get("PARAMNAME"));
                addParamsUrl.append("=");
                addParamsUrl.append(addParam.get("PARAMVALUE"));
                addParamsUrl.append("&");
            }
            return addParamsUrl.toString();
        }
        catch (final DataAccessException dae) {
            throw new RuntimeException((Throwable)dae);
        }
    }
    
    public static DataObject getUIComponentConfig(final Object uiCompName) throws Exception {
        if (uiCompName instanceof String) {
            return DataUtils.getFromCache(StaticLists.UICOMPONENTPERS, "UIComponent", "NAME", uiCompName);
        }
        return DataUtils.getFromCache(StaticLists.UICOMPONENTPERS, "UIComponent", "NAME_NO", uiCompName);
    }
    
    public static Long getUIComponentNameNo(final String uiCompName) throws Exception {
        return (Long)getUIComponentConfig(uiCompName).getValue("UIComponent", "NAME_NO", (Row)null);
    }
    
    public static String getUIComponentName(final Long uiCompNameNo) throws Exception {
        return (String)getUIComponentConfig(uiCompNameNo).getValue("UIComponent", "NAME", (Row)null);
    }
    
    public static ViewController getViewController(final DataObject viewConfig) throws Exception {
        Row webConfigRow = viewConfig.getRow("WebViewConfig");
        String vcClassName = null;
        if (webConfigRow != null) {
            vcClassName = (String)webConfigRow.get(3);
        }
        if (vcClassName == null) {
            final Long compName = (Long)viewConfig.getFirstValue("ViewConfiguration", 3);
            if (compName != null) {
                final DataObject compConfig = getUIComponentConfig(compName);
                webConfigRow = compConfig.getRow("WebUIComponent");
                if (webConfigRow != null) {
                    vcClassName = (String)webConfigRow.get(3);
                }
            }
        }
        return (vcClassName != null) ? ((ViewController)WebClientUtil.createInstance(vcClassName)) : new DefaultViewController();
    }
    
    public static String getOriginalRootViewId(final HttpServletRequest request, final boolean throwException) {
        String uniqueId = (String)StateAPI.getRequestState("_ORVID");
        if (uniqueId == null) {
            final ViewContext viewCtx = (ViewContext)request.getAttribute("ROOT_VIEW_CTX");
            if (viewCtx != null) {
                uniqueId = viewCtx.getUniqueId();
            }
        }
        if (uniqueId == null) {
            uniqueId = (String)StateAPI.getRequestState("_RVID");
        }
        if (uniqueId == null && throwException) {
            throw new RuntimeException("Unable to find the root view for request.It is neither in request  state nor has the view rendering started");
        }
        return uniqueId;
    }
    
    public static String getRootViewIdFromInitParams(final HttpServletRequest request) {
        return request.getSession().getServletContext().getInitParameter("rootview");
    }
    
    public static final boolean isAjaxRequest(final HttpServletRequest request) {
        final String subReq = request.getParameter("SUBREQUEST");
        final String targetURL = request.getParameter("targetURL");
        final boolean sasmoderequest = targetURL != null && targetURL.indexOf("SUBREQUEST") != -1;
        return subReq != null || sasmoderequest || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
    
    public static final boolean isAjaxIframeRequest(final HttpServletRequest request) {
        return "true".equals(request.getParameter("SUBREQUEST")) && !"XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
    
    @Deprecated
    public static ActionForward sendResponse(final HttpServletRequest request, final HttpServletResponse response, final boolean isSuccess, final String statusMsg, final Map respParams, final String forwardurl) throws Exception {
        return sendResponse(request, response, isSuccess, statusMsg, respParams, forwardurl, true);
    }
    
    @Deprecated
    public static ActionForward sendResponse(final HttpServletRequest request, final HttpServletResponse response, final boolean isSuccess, final String statusMsg, final Map respParams, final String forwardurl, final boolean escapeStr) throws Exception {
        WebClientUtil.setStatusMessage(request, statusMsg, isSuccess, escapeStr);
        if (isAjaxRequest(request)) {
            includeAjaxResponse(request, response, respParams);
            if (forwardurl != null && isSuccess) {
                final String path = request.getContextPath() + "/STATE_ID/";
                final RequestDispatcher rd = request.getRequestDispatcher(path + forwardurl);
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
            return null;
        }
        request.setAttribute("RESPONSE_PARAMS", (Object)respParams);
        return new ActionForward(getRootViewURL(request));
    }
    
    @Deprecated
    public static ActionForward sendResponse(final HttpServletRequest request, final HttpServletResponse response, final boolean isSuccess, final String statusMsg, final Map respParams) throws Exception {
        final String forwardurl = request.getParameter("FORWARDURL");
        WebClientUtil.validatePath(forwardurl);
        return sendResponse(request, response, isSuccess, statusMsg, respParams, forwardurl);
    }
    
    private static void includeAjaxResponse(final HttpServletRequest request, final HttpServletResponse response, final Map respParams) throws Exception {
        if (respParams != null && respParams.size() > 0) {
            final StringBuilder strBuilder = new StringBuilder();
            for (final Map.Entry entry : respParams.entrySet()) {
                strBuilder.append("\"").append(JSUtil.getEscapedString(entry.getKey())).append("\"").append(":\"").append(JSUtil.getEscapedString(entry.getValue())).append("\",");
            }
            strBuilder.deleteCharAt(strBuilder.length() - 1);
            request.setAttribute("RESPONSE_PARAMS", (Object)strBuilder.toString());
        }
        setContentType(request, response);
        HttpUtil.include("/framework/jsp/AjaxResponse.jsp", request, response);
    }
    
    @Deprecated
    public static ActionForward sendCustomResponse(final HttpServletRequest request, final HttpServletResponse response, final boolean isSuccess, final String statusMsg, final Map respParams, final String responseStr) throws Exception {
        WebClientUtil.setStatusMessage(request, statusMsg, isSuccess);
        if (isAjaxRequest(request)) {
            includeAjaxResponse(request, response, respParams);
            if (responseStr != null && isSuccess) {
                response.getWriter().write(responseStr);
            }
            return null;
        }
        request.setAttribute("RESPONSE_PARAMS", (Object)respParams);
        return new ActionForward(getRootViewURL(request));
    }
    
    @Deprecated
    public static void writeInResponse(final HttpServletRequest request, final HttpServletResponse response, final String data) throws Exception {
        writeInResponse(request, response, data, 200, null);
    }
    
    @Deprecated
    public static void writeInResponse(final HttpServletRequest request, final HttpServletResponse response, final String data, final int responseStatusCode) throws Exception {
        writeInResponse(request, response, data, responseStatusCode, null);
    }
    
    @Deprecated
    public static void writeInResponse(final HttpServletRequest request, final HttpServletResponse response, final String data, final int responseStatusCode, final String encodeResponseType) throws Exception {
        response.setStatus(responseStatusCode);
        String encodedData = data;
        if (encodeResponseType != null) {
            switch (encodeResponseType) {
                case "html": {
                    encodedData = IAMEncoder.encodeHTML(data);
                    break;
                }
                case "html_attr": {
                    encodedData = IAMEncoder.encodeHTMLAttribute(data);
                    break;
                }
                case "js": {
                    encodedData = IAMEncoder.encodeJavaScript(data);
                    break;
                }
                case "css": {
                    encodedData = IAMEncoder.encodeCSS(data);
                    break;
                }
            }
        }
        try (final PrintWriter printWriter = response.getWriter()) {
            printWriter.write(encodedData);
            printWriter.flush();
        }
    }
    
    public static void setContentType(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (isAjaxIframeRequest(request)) {
            if (response.isCommitted()) {
                WebViewAPI.LOGGER.log(Level.SEVERE, "The response has already been committed !!! Could create problems with IE . The request is {0}", request.getRequestURL());
            }
            response.setContentType("text/plain; charset=utf-8");
        }
        else {
            response.setContentType("text/html; charset=utf-8");
        }
    }
    
    static {
        LOGGER = Logger.getLogger(WebViewAPI.class.getName());
    }
}
