package com.me.ems.onpremise.common.authentication;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import java.io.IOException;
import java.util.Map;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.iam.security.Authenticator;
import com.adventnet.iam.security.SecurityRequestWrapper;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.adventnet.authentication.PAM;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class APIUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    private static final Logger logger;
    private static Boolean shouldThrottleSessions;
    private static Boolean shouldClearOldSessions;
    private static final String IS_FIRST_LOGIN_COMPLETED = "isFirstLoginCompleted";
    
    public void init() {
    }
    
    private DataObject getActiveSessionsDO() {
        DataObject dataObject = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
            final Criteria statusCriteria = new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"ACTIVE", 0);
            selectQuery.setCriteria(statusCriteria);
            selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "SESSION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "USER_HOST"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "OPENTIME"));
            dataObject = SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Exception in getting Active Sessions count ", e);
        }
        return dataObject;
    }
    
    private void throttleUserSessions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        try {
            final DataObject activeSessionsDO = this.getActiveSessionsDO();
            int maxActiveSessions = 10000;
            final int activeSessions = activeSessionsDO.size("AaaAccSession");
            try {
                maxActiveSessions = Integer.parseInt(SyMUtil.getSyMParameter("max_active_sessions"));
            }
            catch (final Exception e) {
                APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Error while getting max active sessions", e);
            }
            if (activeSessions > maxActiveSessions) {
                String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                final String domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
                if (domainName != null && !domainName.equals("-")) {
                    loginName = loginName + "\\" + domainName;
                }
                DCEventLogUtil.getInstance().addEvent(720, loginName, (HashMap)null, "desktopcentral.common.login.restricted_threshold_log", (Object)false, false);
                APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Logged user out since threshold is reached. User: {0}", new Object[] { loginName });
                final String ssoID = (String)request.getSession().getAttribute("JSESSIONIDSSO");
                PAM.logout(ssoID);
                request.getSession(false).invalidate();
                final RequestDispatcher disp = request.getServletContext().getRequestDispatcher("/login");
                request.setAttribute("login_status", (Object)"Login restricted. Exceeded the maximum number of user sessions, try again after some time.");
                disp.forward((ServletRequest)request, (ServletResponse)response);
                throw new ServletException("Logging user out since max sessions reached");
            }
        }
        catch (final ServletException se) {
            throw se;
        }
        catch (final Exception e2) {
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Exception while logging out due to session count", e2);
        }
    }
    
    private void closeOlderSessions(final HttpServletRequest request) {
        try {
            final DataObject activeSessionsDO = this.getActiveSessionsDO();
            if (activeSessionsDO != null && !activeSessionsDO.isEmpty()) {
                final long fiveMinutesInMillis = 300000L;
                final String currentHost = request.getRemoteHost();
                final Criteria criteria1 = new Criteria(new Column("AaaAccSession", "USER_HOST"), (Object)currentHost, 0);
                final Criteria criteria2 = new Criteria(new Column("AaaAccSession", "OPENTIME"), (Object)(new Long(System.currentTimeMillis()) - fiveMinutesInMillis), 7);
                final Criteria criteria3 = criteria1.and(criteria2);
                final Iterator iterator = activeSessionsDO.getRows("AaaAccSession", criteria3);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    PAM.logout((Long)row.get("SESSION_ID"));
                }
            }
        }
        catch (final Exception e) {
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Error while closing older sessions", e);
        }
    }
    
    public boolean authentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        boolean isAuthenticatedFlag = false;
        final SecurityRequestWrapper secureRequest = (SecurityRequestWrapper)request;
        final String authToken = request.getHeader("Authorization");
        try {
            if (request.getSession(false).getAttribute("isFirstLoginCompleted") == null) {
                APIUnifiedAuthenticationHandler.logger.log(Level.FINE, "UserSessionThrottling: Entered handling");
                if (APIUnifiedAuthenticationHandler.shouldClearOldSessions == null) {
                    final String clearOldSessionsStr = SyMUtil.getSyMParameter("clear_old_host_sessions");
                    APIUnifiedAuthenticationHandler.shouldClearOldSessions = (clearOldSessionsStr != null && clearOldSessionsStr.equalsIgnoreCase("true"));
                }
                if (APIUnifiedAuthenticationHandler.shouldClearOldSessions) {
                    this.closeOlderSessions(request);
                }
                APIUnifiedAuthenticationHandler.logger.log(Level.FINE, "UserSessionThrottling: Completed shouldClearOldSessions, which was {0} ", new Object[] { APIUnifiedAuthenticationHandler.shouldClearOldSessions });
                if (APIUnifiedAuthenticationHandler.shouldThrottleSessions == null) {
                    final String throttleSessionsStr = SyMUtil.getSyMParameter("throttle_user_sessions");
                    APIUnifiedAuthenticationHandler.shouldThrottleSessions = (throttleSessionsStr != null && throttleSessionsStr.equalsIgnoreCase("true"));
                }
                if (APIUnifiedAuthenticationHandler.shouldThrottleSessions) {
                    this.throttleUserSessions(request, response);
                }
                APIUnifiedAuthenticationHandler.logger.log(Level.FINE, "UserSessionThrottling: Completed shouldThrottleSessions, which was {0} ", new Object[] { APIUnifiedAuthenticationHandler.shouldThrottleSessions });
            }
            request.getSession(false).setAttribute("isFirstLoginCompleted", (Object)true);
        }
        catch (final ServletException se) {
            request.getSession(false).setAttribute("isFirstLoginCompleted", (Object)true);
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Exception thrown", (Throwable)se);
            throw se;
        }
        catch (final Exception e) {
            request.getSession(false).setAttribute("isFirstLoginCompleted", (Object)true);
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "UserSessionThrottling: Exception thrown", e);
        }
        try {
            final Long loginID = this.getLoginIDIFValidAuthToken(authToken);
            final Long loginIDFromMickey = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID == null && loginIDFromMickey != null) {
                request.setAttribute("ZSEC_AUTH_TYPE", (Object)Authenticator.AUTH_TYPE.PASSWORD.getValue());
                isAuthenticatedFlag = true;
            }
            else if (loginID != null) {
                final Map dcUser = this.getLoginDetails(loginID);
                final String loginName = dcUser.get("NAME");
                final String domainName = dcUser.get("DOMAINNAME");
                final Long userID = dcUser.get("USER_ID");
                ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(loginName, "system", domainName, userID);
                request.setAttribute("isAPILogin", (Object)true);
                isAuthenticatedFlag = true;
            }
            return isAuthenticatedFlag;
        }
        catch (final IAMSecurityException ex) {
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "Exception in isAuthenticatedFlag while fetching loginID from Factory Provider", (Throwable)ex);
            throw ex;
        }
        catch (final Exception ex2) {
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "Exception in isAuthenticatedFlag while fetching loginID from Factory Provider", ex2);
            return false;
        }
    }
    
    public boolean authorization(final HttpServletRequest request, final HttpServletResponse response) {
        return true;
    }
    
    public Long getLoginIDIFValidAuthToken(final String authToken) {
        Long loginID = null;
        if (authToken == null || authToken.trim().equals("")) {
            return null;
        }
        final DataObject authDO = AuthenticationKeyUtil.getInstance().authenticateAPIKey(authToken, "301");
        try {
            if (authDO != null && !authDO.isEmpty()) {
                final Row authRow = authDO.getRow("APIKeyDetails");
                loginID = (Long)authRow.get("LOGIN_ID");
            }
        }
        catch (final Exception ex) {
            APIUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "Exception occurred while fetching loginID from AuthToken", ex);
        }
        return loginID;
    }
    
    public Map<String, Object> getLoginDetails(final Long loginID) {
        final Map dcUser = DMUserHandler.getLoginDetails(loginID);
        return dcUser;
    }
    
    static {
        logger = Logger.getLogger(APIUnifiedAuthenticationHandler.class.getName());
        APIUnifiedAuthenticationHandler.shouldThrottleSessions = null;
        APIUnifiedAuthenticationHandler.shouldClearOldSessions = null;
    }
}
