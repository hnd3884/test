package com.adventnet.client.view.web;

import java.util.ArrayList;
import javax.servlet.ServletRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class StateAPI implements JavaScriptConstants, WebConstants
{
    private static final Logger LOGGER;
    static ThreadLocal prevStateDataRef;
    static ThreadLocal newStateDataRef;
    static ThreadLocal refIdMapRef;
    static ThreadLocal prevURLStateDataRef;
    
    public static Object getRequiredState(final String grpName, final String stateName) {
        final Object stateValue = getState(grpName, stateName);
        if (stateValue != null) {
            return stateValue;
        }
        if (System.getProperty("STATEVAR" + stateName) != null) {
            return System.getProperty("STATEVAR" + stateName);
        }
        throw new IllegalArgumentException(stateName + " is not present as a part of request state");
    }
    
    public static Map<String, Object> getStateMap(final String uniqueId) {
        final Map<String, Object> stateData = getStateDataForRequest();
        if (stateData == null || !stateData.containsKey(uniqueId)) {
            return null;
        }
        final Map<String, Object> viewMap = stateData.get(uniqueId);
        return viewMap;
    }
    
    public static Map<String, Object> getURLStateMap(final String uniqueId) {
        final Map<String, Object> urlstateData = getURLStateDataForRequest();
        if (urlstateData == null || !urlstateData.containsKey(uniqueId)) {
            return null;
        }
        final Map<String, Object> viewMap = urlstateData.get(uniqueId);
        return viewMap;
    }
    
    public static Map getNewStateMap(final String grpName) {
        return getNewStatesMap().get(grpName);
    }
    
    public static String getClientRequestStateAsString(final HttpServletRequest request) {
        return (String)request.getAttribute("PREVCLIENTSTATE");
    }
    
    static Map getNewStatesMap() {
        Map newStates = StateAPI.newStateDataRef.get();
        if (newStates == null) {
            newStates = new HashMap();
            StateAPI.newStateDataRef.set(newStates);
        }
        return newStates;
    }
    
    public static Object getRequestState(final String stateName) {
        return getState("_REQS", stateName);
    }
    
    public static Object getSessionState(final String stateName) {
        return getState("_SES", stateName);
    }
    
    public static void setSessionState(final String stateName, final String value) {
        Map sesStateMap = getNewStateMap("_SES");
        if (sesStateMap == null) {
            sesStateMap = new HashMap();
            StateAPI.newStateDataRef.get().put("_SES", sesStateMap);
        }
        sesStateMap.put(stateName, value);
    }
    
    public static Object getState(final String viewName, final String key) {
        final Map stateData = getStateDataForRequest();
        if (stateData == null || !stateData.containsKey(viewName)) {
            return null;
        }
        final Map viewMap = stateData.get(viewName);
        return viewMap.get(key);
    }
    
    public static Object getURLState(final String viewName, final String key) {
        final Map stateData = getURLStateDataForRequest();
        if (stateData == null || !stateData.containsKey(viewName)) {
            return null;
        }
        final Map viewMap = stateData.get(viewName);
        return viewMap.get(key);
    }
    
    public static Map<String, Object> getStateDataForRequest() {
        final Object stateData = StateAPI.prevStateDataRef.get();
        return (stateData == StateAPI.NULLOBJ) ? null : ((Map)stateData);
    }
    
    public static Map<String, Object> getURLStateDataForRequest() {
        final Object stateData = StateAPI.prevURLStateDataRef.get();
        return (stateData == StateAPI.NULLOBJ) ? null : ((Map)stateData);
    }
    
    public static void clearStateForThread() {
        StateAPI.prevStateDataRef.set(null);
        StateAPI.refIdMapRef.set(null);
        StateAPI.newStateDataRef.set(null);
        StateAPI.prevURLStateDataRef.set(null);
    }
    
    public static String getUniqueId(final String refId) {
        final HashMap map = StateAPI.refIdMapRef.get();
        String uniqueId = map.get(refId);
        if (uniqueId == null) {
            uniqueId = WebViewAPI.getViewName(refId);
        }
        if (uniqueId == null) {
            StateAPI.LOGGER.log(Level.SEVERE, "State has not been passed  for reference id {0}. Trying to fetch  from database.", refId);
            Long refIdL = null;
            try {
                refIdL = new Long(refId);
                final Criteria cr = new Criteria(new Column("ViewConfiguration", "VIEWNAME_NO"), (Object)refIdL, 0);
                final DataObject dob = LookUpUtil.getPersistence().get("ViewConfiguration", cr);
                if (dob.containsTable("ViewConfiguration")) {
                    return (String)dob.getFirstValue("ViewConfiguration", 2);
                }
            }
            catch (final Exception ex) {}
            throw new RuntimeException("Illegal client state. The uniqueId for reference id " + refId + " is not present.");
        }
        return uniqueId;
    }
    
    public static void generateStateScriptForView(final ViewContext viewCtx, final StringBuffer stateScriptBuf, final boolean transferOldState) {
        if (transferOldState) {
            StateParserGenerator.transferOldStateForView(viewCtx);
        }
        StateParserGenerator.generateCreateViewScript(viewCtx, (ServletRequest)viewCtx.getRequest(), stateScriptBuf);
        StateParserGenerator.generateStateVariables(viewCtx, stateScriptBuf);
    }
    
    public static final boolean isSubRequest(final HttpServletRequest request) {
        return "true".equals(request.getParameter("SUBREQUEST")) || WebViewAPI.isAjaxRequest(request);
    }
    
    public static void clearOldStateForViewHeirarchy(final String uniqueId) {
        final Map stateData = getStateDataForRequest();
        if (stateData == null) {
            return;
        }
        stateData.remove(uniqueId);
        final ArrayList toProcessList = new ArrayList();
        toProcessList.add(uniqueId);
        final Map[] origList = (Map[])stateData.values().toArray(new Map[0]);
        while (toProcessList.size() > 0) {
            final String curUniqueId = toProcessList.remove(toProcessList.size() - 1);
            for (int i = 0; i < origList.length; ++i) {
                if (origList[i] != null && origList[i].get("_PV") != null && curUniqueId.equals(getUniqueId(origList[i].get("_PV")))) {
                    final String toRem = origList[i].get("UNIQUE_ID");
                    toProcessList.add(toRem);
                    final HashMap hm = stateData.remove(toRem);
                    hm.clear();
                    origList[i] = null;
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(StateAPI.class.getName());
        StateAPI.prevStateDataRef = new ThreadLocal();
        StateAPI.newStateDataRef = new ThreadLocal();
        StateAPI.refIdMapRef = new ThreadLocal();
        StateAPI.prevURLStateDataRef = new ThreadLocal();
    }
}
