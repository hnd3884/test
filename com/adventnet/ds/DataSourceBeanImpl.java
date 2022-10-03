package com.adventnet.ds;

import com.adventnet.ds.query.QueryConstructionException;
import java.util.logging.Level;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;

public class DataSourceBeanImpl implements DataSourceBean
{
    private static final Logger LOGGER;
    
    @Override
    public <T extends ResultSetHandler> T executeQuery(final Class<T> rsHandlerClass, final String sql, final Object... values) throws SQLException {
        return RelationalAPI.getInstance().executeQuery(rsHandlerClass, sql, values);
    }
    
    @Override
    public int executeUpdate(final String sql, final Object... values) throws SQLException {
        return RelationalAPI.getInstance().executeUpdate(sql, values);
    }
    
    @Override
    public int[] executeBatch(final String sql, final Collection<Object[]> paramsList) throws SQLException {
        Connection conn = null;
        PreparedStatement pStmt = null;
        final RelationalAPI relAPI = RelationalAPI.getInstance();
        try {
            conn = relAPI.getConnection();
            pStmt = conn.prepareStatement(sql);
            for (final Object[] params : paramsList) {
                for (int j = 0; j < params.length; ++j) {
                    pStmt.setObject(j + 1, params[j]);
                }
                pStmt.addBatch();
            }
            return relAPI.executeBatch(pStmt);
        }
        finally {
            this.Close(pStmt);
            this.Close(conn);
        }
    }
    
    private void Close(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final SQLException ex) {
            DataSourceBeanImpl.LOGGER.log(Level.SEVERE, "Exception while closing", ex);
        }
    }
    
    private void Close(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (final SQLException ex) {
            DataSourceBeanImpl.LOGGER.log(Level.SEVERE, "Exception while closing", ex);
        }
    }
    
    @Override
    public int executeDeleteSQL(final String deleteSql, final Object... values) throws SQLException, QueryConstructionException {
        final String sql = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().processDeleteSQLString(deleteSql);
        return this.executeUpdate(sql, values);
    }
    
    static {
        LOGGER = Logger.getLogger(DataSourceBeanImpl.class.getName());
    }
}
