package org.apache.tomcat.dbcp.dbcp2;

import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;

public class PoolablePreparedStatement<K> extends DelegatingPreparedStatement
{
    private final KeyedObjectPool<K, PoolablePreparedStatement<K>> pool;
    private final K key;
    private volatile boolean batchAdded;
    
    public PoolablePreparedStatement(final PreparedStatement stmt, final K key, final KeyedObjectPool<K, PoolablePreparedStatement<K>> pool, final DelegatingConnection<?> conn) {
        super(conn, stmt);
        this.pool = pool;
        this.key = key;
        this.removeThisTrace(this.getConnectionInternal());
    }
    
    @Override
    public void addBatch() throws SQLException {
        super.addBatch();
        this.batchAdded = true;
    }
    
    @Override
    public void clearBatch() throws SQLException {
        this.batchAdded = false;
        super.clearBatch();
    }
    
    @Override
    public void close() throws SQLException {
        if (!this.isClosed()) {
            try {
                this.pool.returnObject(this.key, this);
            }
            catch (final SQLException | RuntimeException e) {
                throw e;
            }
            catch (final Exception e) {
                throw new SQLException("Cannot close preparedstatement (return to pool failed)", e);
            }
        }
    }
    
    @Override
    public void activate() throws SQLException {
        this.setClosedInternal(false);
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().addTrace(this);
        }
        super.activate();
    }
    
    @Override
    public void passivate() throws SQLException {
        if (this.batchAdded) {
            this.clearBatch();
        }
        this.setClosedInternal(true);
        this.removeThisTrace(this.getConnectionInternal());
        final List<AbandonedTrace> resultSetList = this.getTrace();
        if (resultSetList != null) {
            final List<Exception> thrownList = new ArrayList<Exception>();
            final ResultSet[] arr$;
            final ResultSet[] resultSets = arr$ = resultSetList.toArray(Utils.EMPTY_RESULT_SET_ARRAY);
            for (final ResultSet resultSet : arr$) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    }
                    catch (final Exception e) {
                        thrownList.add(e);
                    }
                }
            }
            this.clearTrace();
            if (!thrownList.isEmpty()) {
                throw new SQLExceptionList(thrownList);
            }
        }
        super.passivate();
    }
}
