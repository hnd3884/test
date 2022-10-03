package com.me.mdm.onpremise.server.user;

import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import com.adventnet.authentication.PAM;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ActiveSessionsFacade
{
    private Logger logger;
    
    public ActiveSessionsFacade() {
        this.logger = Logger.getLogger("UserManagementLogger");
    }
    
    public JSONObject getActiveSessions(final JSONObject requestJSON) {
        try {
            final Long userId = APIUtil.getResourceID(requestJSON, "active_session_id");
            ArrayList activeSessionIdList = null;
            JSONObject responseJSON = new JSONObject();
            final Long currentSession = requestJSON.optLong("current_session");
            if (userId == -1L) {
                responseJSON = this.getAllActiveSessionDetails(currentSession);
            }
            else {
                final Long accountId = DMUserHandler.getLoginIdForUserId(userId);
                if (accountId == null) {
                    throw new APIHTTPException("USR003", new Object[] { userId });
                }
                final boolean history = APIUtil.getBooleanFilter(requestJSON, "history");
                if (!history) {
                    activeSessionIdList = DMOnPremiseUserUtil.getActiveSession(accountId);
                }
                responseJSON = this.getSessionDetails(activeSessionIdList, accountId, requestJSON, currentSession);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getActiveSessions()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject getAllActiveSessionDetails(final Long currentSessionId) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"ACTIVE", 0, false));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return this.getSessionJSONFromDO(dataObject, currentSessionId);
    }
    
    private JSONObject getSessionJSONFromDO(final DataObject dataObject, final Long currentSessionId) throws DataAccessException, JSONException {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray sessionArray = new JSONArray();
        if (dataObject != null && !dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("AaaAccSession");
            while (iterator.hasNext()) {
                final JSONObject sessionDetails = new JSONObject();
                final Row row = iterator.next();
                final Long accountId = (Long)row.get("ACCOUNT_ID");
                final String userHost = (String)row.get("USER_HOST");
                final String applicationHost = (String)row.get("APPLICATION_HOST");
                final Long openTime = (Long)row.get("OPENTIME");
                final Long closeTime = (Long)row.get("CLOSETIME");
                final String status = (String)row.get("STATUS");
                final String userHostName = (String)row.get("USER_HOST_NAME");
                final String authenticator = (String)row.get("AUTHENTICATOR");
                final Long activeDuration = closeTime - openTime;
                final Long sessionId = (Long)row.get("SESSION_ID");
                sessionDetails.put("account_id", (Object)accountId);
                sessionDetails.put("user_host", (Object)userHost);
                sessionDetails.put("application_host", (Object)applicationHost);
                sessionDetails.put("open_time", (Object)openTime);
                sessionDetails.put("close_time", (Object)closeTime);
                sessionDetails.put("status", (Object)status);
                sessionDetails.put("user_host_name", (Object)userHostName);
                sessionDetails.put("authenticator", (Object)authenticator);
                sessionDetails.put("active_duration", (Object)activeDuration);
                sessionDetails.put("SESSION_ID", (Object)sessionId);
                sessionDetails.put("current_session", sessionId.equals(currentSessionId));
                sessionArray.put((Object)sessionDetails);
            }
        }
        responseJSON.put("sessions", (Object)sessionArray);
        responseJSON.put("count", sessionArray.length());
        return responseJSON;
    }
    
    private JSONObject getSessionDetails(final ArrayList activeSessionIdList, final Long accountId, final JSONObject apiRequest, final Long currentSession) throws JSONException, DataAccessException {
        DataObject dataObject = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "*"));
        final SortColumn sortColumn = new SortColumn(new Column("AaaAccSession", "SESSION_ID"), false);
        selectQuery.addSortColumn(sortColumn);
        if (activeSessionIdList == null) {
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(apiRequest);
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaAccSession", "ACCOUNT_ID"), (Object)accountId, 0));
            selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
        }
        else {
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaAccSession", "SESSION_ID"), (Object)activeSessionIdList.toArray(), 8));
        }
        dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject responseJSON = this.getSessionJSONFromDO(dataObject, currentSession);
        responseJSON.put("account_id", (Object)accountId);
        return responseJSON;
    }
    
    public void logoutSession(final APIRequest apiRequest) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final ArrayList<String> sessionIdList = DMOnPremiseUserUtil.getSessionValue();
            final String currentSession = (String)apiRequest.httpServletRequest.getSession().getAttribute("JSESSIONIDSSO");
            sessionIdList.remove(currentSession);
            secLog.put((Object)"DELETE_SESSION", (Object)"all except current");
            DMOnPremiseUserUtil.logoutAllSessions((ArrayList)sessionIdList);
            this.addOrUpdateSessionInvalidatorUsedCount("Kill All Session");
            remarks = "delete-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "Session_Deletion", secLog, Level.INFO);
        }
    }
    
    public void deleteActiveSession(final JSONObject apiRequest) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final Long accountId = DMOnPremiseUserUtil.getAccountID();
            if (accountId != null) {
                final ArrayList activeSession = DMOnPremiseUserUtil.getActiveSession(accountId);
                final Long session = APIUtil.getResourceID(apiRequest, "active_session_id");
                secLog.put((Object)"DELETE_SESSION", (Object)session);
                if (activeSession.contains(session)) {
                    PAM.logout(session);
                    this.addOrUpdateSessionInvalidatorUsedCount("Kill Specified Session");
                    remarks = "delete-success";
                }
            }
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "Session_Deletion", secLog, Level.INFO);
        }
    }
    
    private void addOrUpdateSessionInvalidatorUsedCount(final String paramName) {
        try {
            int usedCount = 1;
            final Object count = UserMgmtUtil.getUserMgmtParameter(paramName);
            if (count != null) {
                usedCount = Integer.parseInt(count.toString());
                UserMgmtUtil.updateUserMgmtParameter(paramName, String.valueOf(usedCount + 1));
            }
            else {
                UserMgmtUtil.updateUserMgmtParameter(paramName, String.valueOf(usedCount));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while addOrUpdate SessionInvalidator Used Count:", e);
        }
    }
}
