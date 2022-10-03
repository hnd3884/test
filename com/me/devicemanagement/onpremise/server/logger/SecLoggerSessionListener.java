package com.me.devicemanagement.onpremise.server.logger;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.OneLineLoggerThreadLocal;
import org.json.simple.JSONObject;
import com.adventnet.authentication.util.AuthDBUtil;
import javax.servlet.http.HttpSessionEvent;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionListener;

public class SecLoggerSessionListener implements HttpSessionListener
{
    private static Logger logger;
    
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
    }
    
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
        try {
            final String sessionId = (String)httpSessionEvent.getSession().getAttribute("JSESSIONIDSSO");
            final List idList = AuthDBUtil.getSessionIds(sessionId);
            if (idList.size() > 0) {
                final Long session_id = idList.get(0);
                final String sessId = "self_client" + session_id;
                final JSONObject jsonObject = new JSONObject();
                if (OneLineLoggerThreadLocal.getSessionId() == null) {
                    OneLineLoggerThreadLocal.setSessionId(sessId);
                }
                final long maxInactiveInterval = httpSessionEvent.getSession().getMaxInactiveInterval() * 1000L;
                if (maxInactiveInterval >= 0L && httpSessionEvent.getSession().getLastAccessedTime() + maxInactiveInterval < System.currentTimeMillis()) {
                    jsonObject.put((Object)"REMARK", (Object)"Logged Out because of session time out");
                }
                jsonObject.put((Object)"LOG_OUT_TIME", (Object)SecurityOneLineLogger.formatTime(Long.valueOf(System.currentTimeMillis())));
                SecurityOneLineLogger.log("Login_Access", "Logged_Out", jsonObject, Level.INFO);
                OneLineLoggerThreadLocal.clearOnelineLoggerThreadLocalDetails();
                final SelectQuery loginQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
                loginQuery.addJoin(new Join("AaaAccSession", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
                loginQuery.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                loginQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
                loginQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
                loginQuery.setCriteria(new Criteria(new Column("AaaAccSession", "SESSION_ID"), (Object)idList.toArray(), 8));
                final DataObject dataObjectLogin = DataAccess.get(loginQuery);
                if (!dataObjectLogin.isEmpty()) {
                    final Row row = dataObjectLogin.getRow("AaaLogin");
                    final Long userId = (Long)row.get("USER_ID");
                    if (userId != null) {
                        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
                        selectQuery.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                        selectQuery.addJoin(new Join("AaaAccount", "AaaAccSession", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
                        selectQuery.addJoin(new Join("AaaAccSession", "AaaAccHttpSession", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 2));
                        selectQuery.addSelectColumn(new Column("AaaAccHttpSession", "SESSION_ID").count());
                        selectQuery.setCriteria(new Criteria(new Column("AaaLogin", "USER_ID"), (Object)userId, 0).and(new Criteria(new Column("AaaAccHttpSession", "SESSION_ID"), (Object)idList.toArray(), 9)));
                        final int count = DBUtil.getRecordCount(selectQuery);
                        if (count == 0) {
                            OneLineLoggerThreadLocal.invalidateRoleNameInCache(userId.toString());
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            SecLoggerSessionListener.logger.log(Level.SEVERE, "Exception occured while logout in clearing role cache : ", ex);
        }
    }
    
    static {
        SecLoggerSessionListener.logger = Logger.getLogger(SecLoggerSessionListener.class.getName());
    }
}
