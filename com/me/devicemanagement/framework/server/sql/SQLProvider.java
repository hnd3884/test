package com.me.devicemanagement.framework.server.sql;

import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.Hashtable;

public class SQLProvider
{
    private static SQLProvider sqlPro;
    private Hashtable sqlCache;
    private Hashtable handlerCache;
    private Logger logger;
    
    private SQLProvider() {
        this.sqlCache = new Hashtable();
        this.handlerCache = new Hashtable();
        this.logger = Logger.getLogger(SQLProvider.class.getName());
    }
    
    public static synchronized SQLProvider getInstance() {
        if (SQLProvider.sqlPro == null) {
            SQLProvider.sqlPro = new SQLProvider();
        }
        return SQLProvider.sqlPro;
    }
    
    public String getSQLString(final int sqlID, final Properties variableValues) throws Exception {
        this.logger.log(Level.FINEST, "getSQLString() invoked with sqlID: {0} with valriable values: {1}", new Object[] { new Integer(sqlID), variableValues });
        String sqlStr = null;
        String handlerClass = null;
        try {
            sqlStr = this.sqlCache.get(new Integer(sqlID));
            handlerClass = this.handlerCache.get(new Integer(sqlID));
            if (sqlStr == null) {
                final Row sqlRow = this.getSQLRowFromDB(sqlID);
                if (sqlRow == null) {
                    this.logger.log(Level.WARNING, "SQL Command is not found for sqlID: " + sqlID);
                    throw new SQLException("SQL Command is not found in DB for sqlID: " + sqlID);
                }
                sqlStr = (String)sqlRow.get("SQL_COMMAND");
                if (sqlStr != null) {
                    this.sqlCache.put(sqlID, sqlStr);
                }
                handlerClass = (String)sqlRow.get("VARIABLE_HANDLER");
                if (handlerClass != null) {
                    this.handlerCache.put(sqlID, handlerClass);
                }
            }
            this.logger.log(Level.FINEST, "Going to process SQL String: {0} with handler class: {1}", new Object[] { sqlStr, handlerClass });
            if (handlerClass != null) {
                final VariablesHandler handler = (VariablesHandler)Class.forName(handlerClass).newInstance();
                sqlStr = handler.handleVariables(sqlStr, variableValues);
            }
            this.logger.log(Level.FINEST, "Final SQL String after processing through the handler: {0}", new Object[] { sqlStr });
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while retrieving SQL for sqlID: " + sqlID + " with variable values: " + variableValues, ex);
            throw ex;
        }
        return sqlStr;
    }
    
    public String getSQLStringFromDB(final int sqlID, final Properties variableValues) throws Exception {
        this.logger.log(Level.FINEST, "getSQLString() invoked with sqlID: {0} with valriable values: {1}", new Object[] { new Integer(sqlID), variableValues });
        String sqlStr = null;
        String handlerClass = null;
        try {
            final Row sqlRow = this.getSQLRowFromDB(sqlID);
            if (sqlRow == null) {
                this.logger.log(Level.WARNING, "SQL Command is not found for sqlID: " + sqlID);
                throw new SQLException("SQL Command is not found in DB for sqlID: " + sqlID);
            }
            sqlStr = (String)sqlRow.get("SQL_COMMAND");
            handlerClass = (String)sqlRow.get("VARIABLE_HANDLER");
            this.logger.log(Level.FINEST, "Going to process SQL String: {0} with handler class: {1}", new Object[] { sqlStr, handlerClass });
            if (handlerClass != null) {
                final VariablesHandler handler = (VariablesHandler)Class.forName(handlerClass).newInstance();
                sqlStr = handler.handleVariables(sqlStr, variableValues);
            }
            this.logger.log(Level.FINEST, "Final SQL String after processing through the handler: {0}", new Object[] { sqlStr });
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while retrieving SQL for sqlID: " + sqlID + " with variable values: " + variableValues, ex);
            throw ex;
        }
        return sqlStr;
    }
    
    private Row getSQLRowFromDB(final int sqlID) throws Exception {
        Row sqlRow = null;
        try {
            final Criteria sqlIDCri = new Criteria(Column.getColumn("DCNativeSQLString", "SQL_ID"), (Object)sqlID, 0);
            final DataObject resultDO = SyMUtil.getPersistence().get("DCNativeSQLString", sqlIDCri);
            if (!resultDO.isEmpty()) {
                sqlRow = resultDO.getRow("DCNativeSQLString");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while retrieving SQL from DB for sqlID: " + sqlID, ex);
            throw ex;
        }
        return sqlRow;
    }
    
    static {
        SQLProvider.sqlPro = null;
    }
}
