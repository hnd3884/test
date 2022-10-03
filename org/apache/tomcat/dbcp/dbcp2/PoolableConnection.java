package org.apache.tomcat.dbcp.dbcp2;

import java.lang.management.ManagementFactory;
import java.sql.ResultSet;
import java.util.concurrent.Executor;
import java.sql.SQLException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ObjectName;
import java.util.Collection;
import java.sql.PreparedStatement;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import javax.management.MBeanServer;
import java.sql.Connection;

public class PoolableConnection extends DelegatingConnection<Connection> implements PoolableConnectionMXBean
{
    private static MBeanServer MBEAN_SERVER;
    private final ObjectPool<PoolableConnection> pool;
    private final ObjectNameWrapper jmxObjectName;
    private PreparedStatement validationPreparedStatement;
    private String lastValidationSql;
    private boolean fatalSqlExceptionThrown;
    private final Collection<String> disconnectionSqlCodes;
    private final boolean fastFailValidation;
    
    public PoolableConnection(final Connection conn, final ObjectPool<PoolableConnection> pool, final ObjectName jmxObjectName, final Collection<String> disconnectSqlCodes, final boolean fastFailValidation) {
        super(conn);
        this.pool = pool;
        this.jmxObjectName = ObjectNameWrapper.wrap(jmxObjectName);
        this.disconnectionSqlCodes = disconnectSqlCodes;
        this.fastFailValidation = fastFailValidation;
        if (jmxObjectName != null) {
            try {
                PoolableConnection.MBEAN_SERVER.registerMBean(this, jmxObjectName);
            }
            catch (final InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {}
        }
    }
    
    public PoolableConnection(final Connection conn, final ObjectPool<PoolableConnection> pool, final ObjectName jmxName) {
        this(conn, pool, jmxName, null, true);
    }
    
    @Override
    protected void passivate() throws SQLException {
        super.passivate();
        this.setClosedInternal(true);
        if (this.getDelegateInternal() instanceof PoolingConnection) {
            ((DelegatingConnection<PoolingConnection>)this).getDelegateInternal().connectionReturnedToPool();
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        if (this.isClosedInternal()) {
            return true;
        }
        if (this.getDelegateInternal().isClosed()) {
            this.close();
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized void close() throws SQLException {
        if (this.isClosedInternal()) {
            return;
        }
        boolean isUnderlyingConnectionClosed;
        try {
            isUnderlyingConnectionClosed = this.getDelegateInternal().isClosed();
        }
        catch (final SQLException e) {
            try {
                this.pool.invalidateObject(this);
            }
            catch (final IllegalStateException ise) {
                this.passivate();
                this.getInnermostDelegate().close();
            }
            catch (final Exception ex) {}
            throw new SQLException("Cannot close connection (isClosed check failed)", e);
        }
        if (isUnderlyingConnectionClosed) {
            try {
                this.pool.invalidateObject(this);
                return;
            }
            catch (final IllegalStateException e2) {
                this.passivate();
                this.getInnermostDelegate().close();
                return;
            }
            catch (final Exception e3) {
                throw new SQLException("Cannot close connection (invalidating pooled object failed)", e3);
            }
        }
        try {
            this.pool.returnObject(this);
        }
        catch (final IllegalStateException e2) {
            this.passivate();
            this.getInnermostDelegate().close();
        }
        catch (final SQLException | RuntimeException e3) {
            throw e3;
        }
        catch (final Exception e3) {
            throw new SQLException("Cannot close connection (return to pool failed)", e3);
        }
    }
    
    @Override
    public void reallyClose() throws SQLException {
        if (this.jmxObjectName != null) {
            this.jmxObjectName.unregisterMBean();
        }
        if (this.validationPreparedStatement != null) {
            try {
                this.validationPreparedStatement.close();
            }
            catch (final SQLException ex) {}
        }
        super.closeInternal();
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        if (this.jmxObjectName != null) {
            this.jmxObjectName.unregisterMBean();
        }
        super.abort(executor);
    }
    
    @Override
    public String getToString() {
        return this.toString();
    }
    
    public void validate(final String sql, int timeoutSeconds) throws SQLException {
        if (this.fastFailValidation && this.fatalSqlExceptionThrown) {
            throw new SQLException(Utils.getMessage("poolableConnection.validate.fastFail"));
        }
        if (sql != null && !sql.isEmpty()) {
            if (!sql.equals(this.lastValidationSql)) {
                this.lastValidationSql = sql;
                this.validationPreparedStatement = this.getInnermostDelegateInternal().prepareStatement(sql);
            }
            if (timeoutSeconds > 0) {
                this.validationPreparedStatement.setQueryTimeout(timeoutSeconds);
            }
            try (final ResultSet rs = this.validationPreparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("validationQuery didn't return a row");
                }
            }
            catch (final SQLException sqle) {
                throw sqle;
            }
            return;
        }
        if (timeoutSeconds < 0) {
            timeoutSeconds = 0;
        }
        if (!this.isValid(timeoutSeconds)) {
            throw new SQLException("isValid() returned false");
        }
    }
    
    private boolean isDisconnectionSqlException(final SQLException e) {
        boolean fatalException = false;
        final String sqlState = e.getSQLState();
        if (sqlState != null) {
            fatalException = ((this.disconnectionSqlCodes == null) ? (sqlState.startsWith("08") || Utils.DISCONNECTION_SQL_CODES.contains(sqlState)) : this.disconnectionSqlCodes.contains(sqlState));
            if (!fatalException) {
                final SQLException nextException = e.getNextException();
                if (nextException != null && nextException != e) {
                    fatalException = this.isDisconnectionSqlException(e.getNextException());
                }
            }
        }
        return fatalException;
    }
    
    @Override
    protected void handleException(final SQLException e) throws SQLException {
        this.fatalSqlExceptionThrown |= this.isDisconnectionSqlException(e);
        super.handleException(e);
    }
    
    public Collection<String> getDisconnectionSqlCodes() {
        return this.disconnectionSqlCodes;
    }
    
    public boolean isFastFailValidation() {
        return this.fastFailValidation;
    }
    
    static {
        try {
            PoolableConnection.MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
        }
        catch (final NoClassDefFoundError | Exception ex) {}
    }
}
