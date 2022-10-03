package com.me.devicemanagement.framework.server.util;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Set;
import java.util.logging.Logger;

public class NotifyUpdatesUtil
{
    private static Logger logger;
    
    private Set<Long> getLoginIDs() {
        NotifyUpdatesUtil.logger.log(Level.INFO, "Invoke getLoginIDs");
        final Set<Long> loginIDs = new HashSet<Long>();
        Connection con = null;
        DataSet dataSet = null;
        try {
            final Table table = new Table("AaaLogin");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
            final Column column = Column.getColumn(table.getTableName(), "LOGIN_ID");
            selectQuery.addSelectColumn(column);
            final RelationalAPI relationalAPI = RelationalAPI.getInstance();
            con = relationalAPI.getConnection();
            dataSet = relationalAPI.executeQuery((Query)selectQuery, con);
            while (dataSet.next()) {
                loginIDs.add((Long)dataSet.getValue("LOGIN_ID"));
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.INFO, "Exception in Fetching the LoginIds ", e);
            if (dataSet != null) {
                try {
                    dataSet.close();
                }
                catch (final Exception e) {
                    NotifyUpdatesUtil.logger.log(Level.INFO, "Exception in Closing the DataSet ", e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e) {
                    NotifyUpdatesUtil.logger.log(Level.INFO, "Exception in Closing the Connection ", e);
                }
            }
        }
        finally {
            if (dataSet != null) {
                try {
                    dataSet.close();
                }
                catch (final Exception e2) {
                    NotifyUpdatesUtil.logger.log(Level.INFO, "Exception in Closing the DataSet ", e2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e2) {
                    NotifyUpdatesUtil.logger.log(Level.INFO, "Exception in Closing the Connection ", e2);
                }
            }
        }
        return loginIDs;
    }
    
    public void addLoginIdsToNotifyTable(final String functionality) throws DataAccessException {
        NotifyUpdatesUtil.logger.log(Level.INFO, "Inside addNotifyChangesToUser");
        final Set loginIds = this.getLoginIDs();
        final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("NotifyChangesToUser"));
        sq.addSelectColumn(new Column("NotifyChangesToUser", "*"));
        try {
            NotifyUpdatesUtil.logger.log(Level.INFO, "SQL Checking presence in NotifyTable : " + RelationalAPI.getInstance().getSelectSQL((Query)sq));
            final DataObject notifyloginObj = DataAccess.get((SelectQuery)sq);
            final WritableDataObject writableDO = new WritableDataObject();
            for (final Long id : loginIds) {
                final Criteria notifychangesCriteria = new Criteria(Column.getColumn("NotifyChangesToUser", "LOGIN_ID"), (Object)id, 0);
                final Row newnotifyRow = new Row("NotifyChangesToUser");
                newnotifyRow.set("LOGIN_ID", (Object)id);
                newnotifyRow.set("FUNCTIONALITY", (Object)functionality);
                if (notifyloginObj != null && !notifyloginObj.isEmpty()) {
                    if (notifyloginObj.getRow("NotifyChangesToUser", notifychangesCriteria) != null) {
                        continue;
                    }
                    writableDO.addRow(newnotifyRow);
                }
                else {
                    writableDO.addRow(newnotifyRow);
                }
            }
            if (!writableDO.isEmpty()) {
                DataAccess.add((DataObject)writableDO);
            }
        }
        catch (final Exception e) {
            NotifyUpdatesUtil.logger.log(Level.INFO, "Exception in adding LoginIds to Notifychangestouser ", e);
        }
    }
    
    public void deleteLoginIdsFromNotifyTable() throws DataAccessException {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get("NotifiedUserForUpdates", (Criteria)null);
            if (!dataObject.isEmpty()) {
                dataObject.deleteRows("NotifiedUserForUpdates", (Criteria)null);
                DataAccess.update(dataObject);
            }
        }
        catch (final DataAccessException ex) {
            NotifyUpdatesUtil.logger.log(Level.WARNING, "Exception got while clearing NotifiedUserForUpdates table ", (Throwable)ex);
        }
    }
    
    static {
        NotifyUpdatesUtil.logger = Logger.getLogger(NotifyUpdatesUtil.class.getName());
    }
}
