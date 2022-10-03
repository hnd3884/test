package com.adventnet.persistence.internal;

import java.sql.Statement;
import java.util.Iterator;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.Table;
import com.adventnet.db.archive.TableArchiverUtil;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Query;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class GetUtil
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    
    private GetUtil(final String tableName) {
    }
    
    public static DataObject get(final SelectQuery query) throws DataAccessException {
        GetUtil.OUT.entering(GetUtil.CLASS_NAME, "get", query);
        WritableDataObject dob = null;
        Connection conn = null;
        DataSet dataSet = null;
        final RelationalAPI relAPI = getRelationalAPI();
        try {
            conn = relAPI.getConnection();
            dataSet = relAPI.executeQuery(query, conn);
            dob = new WritableDataObject(dataSet);
        }
        catch (final QueryConstructionException qce) {
            GetUtil.OUT.log(Level.FINER, "Exception occured while trying to form SQL for the SelectQuery {0}", query);
            GetUtil.OUT.log(Level.FINER, "Exception Stack trace:", qce);
            throw new DataAccessException(qce);
        }
        catch (final SQLException sqle) {
            GetUtil.OUT.log(Level.FINER, "SQLException occured while trying to execute query {0}", query);
            GetUtil.OUT.log(Level.FINER, "Exception Stack trace:", sqle);
            try {
                if (TableArchiverUtil.isArchiveEnabled()) {
                    for (final Object table : query.getTableList()) {
                        relAPI.getArchiveAdapter().restoreUnArchivedInvisibleTable(((Table)table).getTableName(), conn, sqle);
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            throw new DataAccessException(sqle);
        }
        finally {
            safeClose(dataSet);
            safeClose(conn);
        }
        dob.clearOperations();
        GetUtil.OUT.exiting(GetUtil.CLASS_NAME, "get", dob);
        return dob;
    }
    
    private static void safeClose(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception exc) {
            exc.printStackTrace();
        }
    }
    
    private static void safeClose(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (final Exception exc) {
            exc.printStackTrace();
        }
    }
    
    private static void safeClose(final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception exc) {
            GetUtil.OUT.log(Level.FINEST, "Exception occured while closing DataSet {0}", ds);
            GetUtil.OUT.log(Level.FINEST, "Exception Stack trace:", exc);
        }
    }
    
    private static RelationalAPI getRelationalAPI() {
        return RelationalAPI.getInstance();
    }
    
    static {
        CLASS_NAME = GetUtil.class.getName();
        OUT = Logger.getLogger(GetUtil.CLASS_NAME);
    }
}
