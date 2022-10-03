package com.adventnet.client.view.web;

import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import java.util.logging.Level;
import java.util.Comparator;
import java.util.TreeSet;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.client.themes.web.ThemesAPI;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.Map;
import com.adventnet.persistence.DataObject;
import javax.servlet.ServletRequest;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class StateParserGenerator implements JavaScriptConstants, WebConstants
{
    private static Logger logger;
    
    public static String generateCachedViewScript(final ViewContext viewCtx) {
        final StringBuffer strBuf = new StringBuffer("<div class='uicomponent' id='" + IAMEncoder.encodeHTMLAttribute(viewCtx.getUniqueId()) + "_CT1' unique_id='" + IAMEncoder.encodeHTMLAttribute(viewCtx.getUniqueId()) + "' viewholder='true'></div>");
        strBuf.append("<script>importView(");
        StateUtils.generateArgument(strBuf, viewCtx.getUniqueId(), 0);
        final String parRefId = (viewCtx.getParentContext() != null) ? String.valueOf(viewCtx.getParentContext().getReferenceId()) : "null";
        StateUtils.generateArgument(strBuf, parRefId, 2);
        StateUtils.generateArgument(strBuf, (String)viewCtx.getStateParameter("PDCA"), 2);
        strBuf.append(",window)</script>");
        return strBuf.toString();
    }
    
    public static String generateViewPrefix(final ViewContext viewCtx) throws Exception {
        String className = "uicomponent";
        final DataObject dataObject = viewCtx.getModel().getViewConfiguration();
        final String cssName = (String)dataObject.getFirstValue("ViewConfiguration", 12);
        if (cssName != null) {
            className = cssName;
        }
        final StringBuffer strBuf = new StringBuffer("<div class='").append(IAMEncoder.encodeHTMLAttribute(className)).append("' id='");
        strBuf.append(IAMEncoder.encodeHTMLAttribute(viewCtx.getUniqueId())).append("_CT' unique_id='" + IAMEncoder.encodeHTMLAttribute(viewCtx.getUniqueId()) + "'>");
        generateCreateViewScript(viewCtx, (ServletRequest)viewCtx.getRequest(), strBuf);
        return strBuf.toString();
    }
    
    public static void generateCreateViewScript(final ViewContext viewCtx, final ServletRequest request, final StringBuffer dataBuffer) {
        final String params = (String)viewCtx.getStateOrURLStateParameter("_D_RP");
        final String dca = (String)viewCtx.getStateParameter("PDCA");
        final String parentView = (viewCtx.getParentContext() != null) ? viewCtx.getParentContext().getUniqueId() : null;
        dataBuffer.append("<script>");
        dataBuffer.append("V");
        dataBuffer.append(IAMEncoder.encodeJavaScript(viewCtx.getReferenceId()));
        dataBuffer.append("=");
        dataBuffer.append("createView(window,");
        StateUtils.generateArgument(dataBuffer, viewCtx.getUniqueId(), 0);
        StateUtils.generateArgument(dataBuffer, viewCtx.getModel().getViewName(), 2);
        StateUtils.generateArgument(dataBuffer, params, 2);
        StateUtils.generateArgument(dataBuffer, parentView, 2);
        StateUtils.generateArgument(dataBuffer, dca, 2);
        StateUtils.generateArgument(dataBuffer, viewCtx.getReferenceId(), 2);
        StateUtils.generateArgument(dataBuffer, String.valueOf(System.currentTimeMillis()), 1);
        dataBuffer.append("</script>");
    }
    
    public static void generateStateVariables(final ViewContext viewCtx, final StringBuffer dataBuffer) {
        final String params = (String)viewCtx.getStateOrURLStateParameter("_D_RP");
        viewCtx.setStateOrURLStateParam("_D_RP", null, true);
        final String dca = (String)viewCtx.getStateParameter("PDCA");
        viewCtx.setStateParameter("PDCA", null);
        final String stateData = generateJSForState(viewCtx);
        if (stateData != null) {
            dataBuffer.append("<script>");
            dataBuffer.append(stateData);
            dataBuffer.append("</script>");
        }
        viewCtx.setStateOrURLStateParam("_D_RP", params, true);
        viewCtx.setStateParameter("PDCA", dca);
    }
    
    public static String generateSessionStateJS() {
        final Map state = StateAPI.getNewStateMap("_SES");
        if (state == null || state.size() == 0) {
            return "";
        }
        final StringBuffer strBuf = new StringBuffer("<script>");
        strBuf.append("curObj = new Object();");
        strBuf.append("stateData['").append("_SES").append("']= curObj;");
        appendStateScript(strBuf, state, false);
        strBuf.append("</script>");
        return strBuf.toString();
    }
    
    public static String generateViewSuffix(final ViewContext viewCtx) {
        final StringBuffer strBuf = new StringBuffer();
        generateStateVariables(viewCtx, strBuf);
        strBuf.append("</div>");
        return strBuf.toString();
    }
    
    private static String generateJSForState(final ViewContext viewCtx) {
        final StringBuffer dataBuffer = new StringBuffer();
        final Map state = viewCtx.getState();
        if (state == null) {
            return null;
        }
        StateUtils.generateAssignment(dataBuffer, "curObj", "getViewState('" + viewCtx.getReferenceId() + "')", false);
        boolean containsState = appendStateScript(dataBuffer, state, false);
        if (WebClientUtil.isRestful(viewCtx.getRequest())) {
            StateUtils.generateAssignment(dataBuffer, "urlstatecurObj", "getViewURLState('" + viewCtx.getReferenceId() + "')", false);
            final Map urlstate = viewCtx.getURLState();
            if (urlstate != null) {
                containsState = appendStateScript(dataBuffer, urlstate, true);
            }
        }
        return containsState ? dataBuffer.toString() : null;
    }
    
    private static boolean appendStateScript(final StringBuffer dataBuffer, final Map state, final boolean urlstate) {
        boolean hasEntry = false;
        for (final Map.Entry entry : state.entrySet()) {
            final String stateParamName = IAMEncoder.encodeJavaScript((String)entry.getKey());
            final Object stateParamValue = entry.getValue();
            if (stateParamValue == StateParserGenerator.NULLOBJ) {
                continue;
            }
            if (stateParamValue instanceof List && ((List)stateParamValue).size() > 0) {
                hasEntry = true;
                StateUtils.generateAssignment(dataBuffer, "curObj[\"" + stateParamName + "\"]", StateUtils.encodeAsJS((List)stateParamValue), false);
            }
            else if (stateParamValue instanceof Map && ((Map)stateParamValue).size() > 0) {
                hasEntry = true;
                StateUtils.generateAssignment(dataBuffer, "curObj[\"" + stateParamName + "\"]", StateUtils.encodeAsJS((Map)stateParamValue), false);
            }
            else {
                if (!(stateParamValue instanceof String)) {
                    throw new RuntimeException("Encodeing/Decodeing of state information is not supported for data type " + stateParamValue.getClass().getName() + ". Passed Info is " + stateParamName + " = " + stateParamValue);
                }
                hasEntry = true;
                final String value = IAMEncoder.encodeJavaScript((String)stateParamValue);
                if (urlstate) {
                    StateUtils.generateAssignment(dataBuffer, "urlstatecurObj[\"" + stateParamName + "\"]", value, true);
                }
                else {
                    StateUtils.generateAssignment(dataBuffer, "curObj[\"" + stateParamName + "\"]", value, true);
                }
            }
        }
        return hasEntry;
    }
    
    public static String generateRootDetails(final ViewContext ctx, final HttpServletRequest request) throws Exception {
        final StringBuffer dataBuffer = new StringBuffer();
        if (request.getAttribute("ROOT_VIEW_CTX") == ctx) {
            dataBuffer.append("<script>");
            dataBuffer.append("initializeMainView(window,\"").append(IAMEncoder.encodeJavaScript(ctx.getUniqueId())).append("\",");
            dataBuffer.append("\"").append(request.getContextPath()).append("\",");
            dataBuffer.append("\"").append(IAMEncoder.encodeJavaScript(ThemesAPI.getThemeDirForRequest(request))).append("\",");
            dataBuffer.append(WebClientUtil.isRestful(request)).append(",");
            dataBuffer.append(WebClientUtil.isInSasMode(request)).append(");");
            dataBuffer.append("</script>");
        }
        return dataBuffer.toString();
    }
    
    public static void processState(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (StateAPI.prevStateDataRef.get() != null) {
            return;
        }
        final Cookie[] cookiesList = request.getCookies();
        if (cookiesList == null) {
            throw new ClientException(ClientErrorCodes.STATE_COOKIE_NOT_PASSED, null);
        }
        final TreeSet set = new TreeSet(new StateUtils.CookieComparator());
        String contextPath = request.getContextPath();
        contextPath = ((contextPath == null || contextPath.trim().length() == 0) ? "/" : contextPath);
        String sessionIdName = request.getServletContext().getSessionCookieConfig().getName();
        sessionIdName = ((sessionIdName != null) ? sessionIdName : "JSESSIONID");
        for (int i = 0; i < cookiesList.length; ++i) {
            final Cookie cookie = cookiesList[i];
            final String cookieName = cookie.getName();
            final String cVal = cookie.getValue();
            String comment = cookie.getComment();
            String domain = cookie.getDomain();
            final int cAge = cookie.getMaxAge();
            final int cVersion = cookie.getVersion();
            final String cNameValue = cookieName + "=" + cVal;
            final String maxAge = (cAge == -1) ? "" : ("; Max-Age=" + cAge);
            final String path = "; Path=/";
            final String version = "; Version=" + cVersion;
            domain = (isValid(domain) ? ("; Domain=" + domain) : "");
            comment = (isValid(comment) ? ("; Comment=" + comment) : "");
            final String secure = request.isSecure() ? "; Secure" : "";
            final String cookieString = cNameValue + maxAge + path + version + domain + comment + "; HttpOnly" + secure;
            if (cookieName.startsWith("_")) {
                cookiesList[i].setPath(contextPath);
                response.addCookie(cookiesList[i]);
            }
            else if (cookieName.startsWith("STATE_COOKIE")) {
                set.add(cookiesList[i]);
            }
            else if (cookieName.equals(StateParserGenerator.SSOID)) {
                if (cVal.equals(request.getSession().getAttribute(StateParserGenerator.SSOID))) {
                    response.addHeader("SET-COOKIE", cookieString);
                }
                StateParserGenerator.logger.log(Level.FINER, StateParserGenerator.SSOID + " cookie {0} from path {1}", new Object[] { cookiesList[i].getValue(), cookiesList[i].getPath() });
            }
            else if (cookieName.equals(sessionIdName)) {
                final String sessionid = request.getSession().getId();
                if (cookiesList[i].getValue().equals(sessionid)) {
                    final String jsessionCookieStr = sessionIdName + "=" + sessionid + "; Path=" + contextPath + "; HttpOnly" + secure;
                    response.addHeader("SET-COOKIE", jsessionCookieStr);
                }
                StateParserGenerator.logger.log(Level.FINER, sessionIdName + " cookie {0} from path {1}", new Object[] { cookiesList[i].getValue(), cookiesList[i].getPath() });
            }
        }
        if (set.size() != 0) {
            final Iterator iterator = set.iterator();
            final StringBuffer cookieValue = new StringBuffer();
            while (iterator.hasNext()) {
                final Cookie currentCookie = iterator.next();
                final String value = currentCookie.getValue();
                cookieValue.append(value);
            }
            request.setAttribute("PREVCLIENTSTATE", (Object)cookieValue.toString());
            final Map state = parseState(cookieValue.toString());
            final HashMap refIdVsId = new HashMap();
            for (final String uniqueId : state.keySet()) {
                final Map viewMap = state.get(uniqueId);
                refIdVsId.put(viewMap.get("ID") + "", uniqueId);
            }
            StateAPI.prevStateDataRef.set((state != null) ? state : StateParserGenerator.NULLOBJ);
            if (state != null) {
                if (!WebClientUtil.isRestful(request)) {
                    final long urlTime = getTimeFromUrl(request.getRequestURI());
                    final long reqTime = Long.parseLong((String)StateAPI.getRequestState("_TIME"));
                    state.get("_REQS").put("_ISBROWSERREFRESH", String.valueOf(urlTime != reqTime && !StateAPI.isSubRequest(request)));
                }
                final Map sesState = state.get("_SES");
                if (sesState != null) {
                    StateAPI.getNewStatesMap().put("_SES", sesState);
                }
            }
            if (WebClientUtil.isRestful(request)) {
                final HashMap urlstatemap = getURLStateMap(request);
                StateAPI.prevURLStateDataRef.set((urlstatemap != null) ? urlstatemap : StateParserGenerator.NULLOBJ);
            }
            savePreferences(request, state);
            StateAPI.refIdMapRef.set(refIdVsId);
            return;
        }
        request.setAttribute("STATE_MAP", StateParserGenerator.NULLOBJ);
        if (!WebClientUtil.isRestful(request)) {
            throw new ClientException(ClientErrorCodes.STATE_COOKIE_NOT_PASSED, null);
        }
    }
    
    private static HashMap getURLStateMap(final HttpServletRequest request) {
        final HashMap urlstatemap = new HashMap();
        final Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String paramname = names.nextElement();
            if (paramname.indexOf("s:") != -1) {
                final String paramvalue = request.getParameter(paramname);
                paramname = paramname.substring(paramname.indexOf(58) + 1);
                String viewname = paramname.substring(0, paramname.indexOf(58));
                paramname = paramname.substring(paramname.indexOf(58) + 1);
                try {
                    viewname = WebViewAPI.getViewName(new Long(viewname));
                }
                catch (final NumberFormatException ex) {}
                HashMap viewmap = urlstatemap.get(viewname);
                if (viewmap == null) {
                    viewmap = new HashMap();
                    urlstatemap.put(viewname, viewmap);
                }
                viewmap.put(paramname, paramvalue);
            }
        }
        return urlstatemap;
    }
    
    private static void savePreferences(final HttpServletRequest request, final Map state) throws Exception {
        if (WebClientUtil.getAccountId() == -1L) {
            return;
        }
        for (final Map viewState : state.values()) {
            if (viewState.get("_VMD") != null) {
                final String uniqueId = viewState.get("UNIQUE_ID");
                final ViewContext vc = ViewContext.getViewContext(uniqueId, request);
                if (!"PERSONALIZE".equals(vc.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 10))) {
                    continue;
                }
                try {
                    vc.getModel().getController().savePreferences(vc);
                    ViewContext.refreshViewContext(vc.getModel().getViewName(), request);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static long getTimeFromUrl(final String url) {
        final int index = url.indexOf("STATE_ID/") + "STATE_ID/".length();
        final int endIndex = url.indexOf(47, index);
        return Long.parseLong(url.substring(index, endIndex));
    }
    
    public static Map parseState(final String cookieValue) throws UnsupportedEncodingException {
        final HashMap map = new HashMap();
        try {
            final String decodedVal = URLDecoder.decode(cookieValue, "UTF-8");
            final String[] stateArray = getStateArray(decodedVal.getBytes("UTF-8"));
            final int length = stateArray.length;
            HashMap viewMap = null;
            boolean isNextVal = false;
            for (int count = 0; count < length; ++count) {
                final String currentValue = stateArray[count];
                if (currentValue.equals("&")) {
                    final String viewName = stateArray[++count];
                    viewMap = new HashMap();
                    map.put(viewName, viewMap);
                    viewMap.put("UNIQUE_ID", viewName);
                    isNextVal = false;
                }
                else if (currentValue.equals("/")) {
                    if (isNextVal) {
                        String key = stateArray[count - 1];
                        Object value = stateArray[++count];
                        value = URLDecoder.decode((String)value, "UTF-8");
                        if (key.endsWith("_COLL_")) {
                            key = key.substring(0, key.length() - "_COLL_".length());
                            value = StateUtils.parseAsList((String)value);
                        }
                        else if (key.endsWith("_MAP_")) {
                            key = key.substring(0, key.length() - "_MAP_".length());
                            value = StateUtils.parseAsMap((String)value);
                        }
                        viewMap.put(key, value);
                        isNextVal = false;
                    }
                    else {
                        isNextVal = true;
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        StateParserGenerator.logger.log(Level.FINER, "The state data as a map is {0}", map);
        return map;
    }
    
    public static String[] getStateArray(final byte[] stateData) {
        final ArrayList list = new ArrayList();
        int size = 0;
        int count = 0;
        int offSet = 0;
        final int length = stateData.length;
        int specialIndicator = 0;
        while (count < length) {
            final byte current = stateData[count];
            if ((char)current == '&' || (char)current == '/') {
                if (count < length - 1 && (char)stateData[count + 1] == '/') {
                    specialIndicator = 1;
                    size += 2;
                    ++count;
                }
                else {
                    specialIndicator = 0;
                }
                if (specialIndicator == 0) {
                    if (size > 0) {
                        final String value = new String(stateData, offSet, size);
                        final StringBuffer charBuffer = new StringBuffer();
                        for (int len = 0; len < value.length(); ++len) {
                            final char ch = value.charAt(len);
                            if (ch != '/' || len >= value.length() - 1 || value.charAt(len + 1) != '/') {
                                charBuffer.append(ch);
                            }
                        }
                        list.add(charBuffer.toString());
                        size = 0;
                    }
                    list.add(new String(stateData, count, 1));
                    offSet = count + 1;
                }
            }
            else {
                ++size;
            }
            ++count;
        }
        list.add(new String(stateData, offSet, size));
        StateParserGenerator.logger.log(Level.FINER, "The state data as a list is {0}", list);
        final String[] array = new String[0];
        return list.toArray(array);
    }
    
    public static void transferOldStateForView(final ViewContext viewCtx) {
        final Map state = viewCtx.getPreviousState();
        if (state == null) {
            return;
        }
        for (final Map.Entry curEnt : state.entrySet()) {
            final String key = curEnt.getKey();
            if (!key.equals("_VN")) {
                if (key.equals("ID")) {
                    continue;
                }
                if (key.equals("_PV")) {
                    viewCtx.setParentContext(ViewContext.getViewContext(StateAPI.getUniqueId(curEnt.getValue()), viewCtx.getRequest()));
                }
                else {
                    if (key.endsWith("_LIST") && state.get("_PV") == null) {
                        continue;
                    }
                    viewCtx.setStateParameter(key, curEnt.getValue());
                }
            }
        }
        if (!WebViewAPI.isAjaxRequest(viewCtx.getRequest())) {
            viewCtx.setStateParameter("_VMD", "1");
        }
    }
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
    }
    
    static {
        StateParserGenerator.logger = Logger.getLogger("StateParserGenerator");
    }
}
