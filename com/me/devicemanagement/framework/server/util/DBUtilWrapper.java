package com.me.devicemanagement.framework.server.util;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public abstract class DBUtilWrapper
{
    private static final Logger LOGGER;
    
    public int getRecordCount(final SelectQuery selectQuery, final boolean readOnlyMode) throws Exception {
        int recordCount = 0;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            if (readOnlyMode) {
                conn = SyMUtil.getReadOnlyConnection();
            }
            else {
                conn = SyMUtil.getConnection();
            }
            ds = relapi.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    recordCount = Integer.valueOf(value.toString());
                }
            }
        }
        catch (final QueryConstructionException ex) {
            DBUtilWrapper.LOGGER.log(Level.SEVERE, "QueryConstructionException : ", (Throwable)ex);
            throw ex;
        }
        catch (final SQLException ex2) {
            DBUtilWrapper.LOGGER.log(Level.SEVERE, "SQLException : ", ex2);
            throw ex2;
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                ex3.printStackTrace();
            }
        }
        return recordCount;
    }
    
    public SelectQuery constructSelectQuery(final String tableName, final Criteria criteria) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return query;
    }
    
    public int getRecordCount(final SelectQuery selectQuery, final String tableName, final String columnName, final boolean readOnlyMode) throws Exception {
        int recordCount = 0;
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.distinct();
        selCol = selCol.count();
        selectQuery.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            if (readOnlyMode) {
                conn = SyMUtil.getReadOnlyConnection();
            }
            else {
                conn = SyMUtil.getConnection();
            }
            ds = relapi.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    recordCount += (int)value;
                }
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            DBUtilWrapper.LOGGER.log(Level.SEVERE, "QueryConstructionException : ", (Throwable)ex);
            throw ex;
        }
        catch (final SQLException ex2) {
            DBUtilWrapper.LOGGER.log(Level.SEVERE, "SQLException : ", ex2);
            throw ex2;
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                DBUtilWrapper.LOGGER.log(Level.SEVERE, "Exception in closing db connection: ", ex3);
            }
        }
        return recordCount;
    }
    
    public Object getValueFromDB(final String tableName, final String criteriaColumnName, final Object criteriaColumnValue, final String returnColumnName, final boolean readOnlyMode) throws Exception {
        final Column col = Column.getColumn(tableName, criteriaColumnName);
        final Criteria criteria = new Criteria(col, criteriaColumnValue, 0, false);
        DataObject resDO = null;
        if (readOnlyMode) {
            resDO = SyMUtil.getReadOnlyPersistence().get(tableName, criteria);
        }
        else {
            resDO = SyMUtil.getPersistence().get(tableName, criteria);
        }
        if (resDO.isEmpty()) {
            return null;
        }
        return resDO.getFirstValue(tableName, returnColumnName);
    }
    
    static {
        LOGGER = Logger.getLogger(DBUtilWrapper.class.getName());
    }
}
