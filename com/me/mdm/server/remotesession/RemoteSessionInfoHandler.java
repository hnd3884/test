package com.me.mdm.server.remotesession;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RemoteSessionInfoHandler
{
    private static final Logger LOGGER;
    
    public Long addRemoteSession(final JSONObject sessionInfo) throws JSONException, DataAccessException {
        final String sessionKey = String.valueOf(sessionInfo.get("SESSION_KEY"));
        final String sessionUrl = String.valueOf(sessionInfo.get("SESSION_URL"));
        DataObject dO = DataAccess.constructDataObject();
        final Row row = new Row("RemoteSessionInfo");
        row.set("SESSION_KEY", (Object)sessionKey);
        row.set("SESSION_URL", (Object)sessionUrl);
        dO.addRow(row);
        dO = DataAccess.add(dO);
        RemoteSessionInfoHandler.LOGGER.log(Level.INFO, "Remote session added {0}", row);
        return (Long)dO.getFirstRow("RemoteSessionInfo").get("SESSION_ID");
    }
    
    public JSONObject getSessionDetails(final Long sessionId) throws DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RemoteSessionInfo"));
        final Criteria cmdHistCriteria = new Criteria(new Column("RemoteSessionInfo", "SESSION_ID"), (Object)sessionId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getRow("RemoteSessionInfo");
            final JSONObject sessionInfo = new JSONObject();
            sessionInfo.put("SESSION_ID", (Object)sessionId);
            sessionInfo.put("SESSION_KEY", row.get("SESSION_KEY"));
            sessionInfo.put("SESSION_URL", row.get("SESSION_URL"));
            return sessionInfo;
        }
        return null;
    }
    
    public void addOrUpdateResourceToRemoteSession(final JSONObject data) throws JSONException, DataAccessException {
        final Long commandHistoryId = data.getLong("COMMAND_HISTORY_ID");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RemoteSessionCommandHistory"));
        final Criteria cmdHistCriteria = new Criteria(new Column("RemoteSessionCommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Long sessionId = data.getLong("SESSION_ID");
            final Integer status = data.getInt("STATUS");
            final Row row = new Row("RemoteSessionCommandHistory");
            row.set("COMMAND_HISTORY_ID", (Object)commandHistoryId);
            row.set("SESSION_ID", (Object)sessionId);
            row.set("STATUS", (Object)status);
            dO.addRow(row);
        }
        else {
            final Integer status2 = data.getInt("STATUS");
            final Row row2 = dO.getRow("RemoteSessionCommandHistory");
            row2.set("STATUS", (Object)status2);
            dO.updateRow(row2);
        }
        DataAccess.update(dO);
    }
    
    public int getSessionStatus(final Long commandHistoryId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RemoteSessionCommandHistory"));
        final Criteria cmdHistCriteria = new Criteria(new Column("RemoteSessionCommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return -1;
        }
        return (int)dO.getRow("RemoteSessionCommandHistory").get("STATUS");
    }
    
    public String getSessionKey(final Long commandHistoryId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RemoteSessionCommandHistory"));
        final Join sessionJoin = new Join("RemoteSessionCommandHistory", "RemoteSessionInfo", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 2);
        final Criteria cmdHistCriteria = new Criteria(new Column("RemoteSessionCommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
        sQuery.addJoin(sessionJoin);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return null;
        }
        return (String)dO.getRow("RemoteSessionInfo").get("SESSION_KEY");
    }
    
    public Long getSessionIdForCmdHisId(final Long commandHistoryId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RemoteSessionCommandHistory"));
        final Criteria cmdHistCriteria = new Criteria(new Column("RemoteSessionCommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return -1L;
        }
        return (Long)dO.getRow("RemoteSessionCommandHistory").get("SESSION_ID");
    }
    
    public Long getSessionAddedTime(final Long commandHistoryId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CommandHistory"));
        final Criteria cmdHistCriteria = new Criteria(new Column("CommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return -1L;
        }
        return (Long)dO.getRow("CommandHistory").get("ADDED_TIME");
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
