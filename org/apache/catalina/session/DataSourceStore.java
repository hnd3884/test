package org.apache.catalina.session;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Session;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;

public class DataSourceStore extends JDBCStore
{
    @Override
    public String[] expiredKeys() throws IOException {
        return this.keys(true);
    }
    
    @Override
    public String[] keys() throws IOException {
        return this.keys(false);
    }
    
    private String[] keys(final boolean expiredOnly) throws IOException {
        String[] keys = null;
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            final Connection _conn = this.getConnection();
            if (_conn == null) {
                return new String[0];
            }
            try {
                String keysSql = "SELECT " + this.sessionIdCol + " FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                if (expiredOnly) {
                    keysSql = keysSql + " AND (" + this.sessionLastAccessedCol + " + " + this.sessionMaxInactiveCol + " * 1000 < ?)";
                }
                try (final PreparedStatement preparedKeysSql = _conn.prepareStatement(keysSql)) {
                    preparedKeysSql.setString(1, this.getName());
                    if (expiredOnly) {
                        preparedKeysSql.setLong(2, System.currentTimeMillis());
                    }
                    try (final ResultSet rst = preparedKeysSql.executeQuery()) {
                        final List<String> tmpkeys = new ArrayList<String>();
                        if (rst != null) {
                            while (rst.next()) {
                                tmpkeys.add(rst.getString(1));
                            }
                        }
                        keys = tmpkeys.toArray(new String[0]);
                        numberOfTries = 0;
                    }
                }
            }
            catch (final SQLException e) {
                this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".SQLException", new Object[] { e }));
                keys = new String[0];
            }
            finally {
                this.release(_conn);
            }
        }
        return keys;
    }
    
    @Override
    public int getSize() throws IOException {
        int size = 0;
        final String sizeSql = "SELECT COUNT(" + this.sessionIdCol + ") FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            final Connection _conn = this.getConnection();
            if (_conn == null) {
                return size;
            }
            try (final PreparedStatement preparedSizeSql = _conn.prepareStatement(sizeSql)) {
                preparedSizeSql.setString(1, this.getName());
                try (final ResultSet rst = preparedSizeSql.executeQuery()) {
                    if (rst.next()) {
                        size = rst.getInt(1);
                    }
                    numberOfTries = 0;
                }
            }
            catch (final SQLException e) {
                this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".SQLException", new Object[] { e }));
            }
            finally {
                this.release(_conn);
            }
        }
        return size;
    }
    
    @Override
    public Session load(final String id) throws ClassNotFoundException, IOException {
        StandardSession _session = null;
        final Context context = this.getManager().getContext();
        final Log contextLog = context.getLogger();
        int numberOfTries = 2;
        final String loadSql = "SELECT " + this.sessionIdCol + ", " + this.sessionDataCol + " FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ? AND " + this.sessionAppCol + " = ?";
        while (numberOfTries > 0) {
            final Connection _conn = this.getConnection();
            if (_conn == null) {
                return null;
            }
            final ClassLoader oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, (ClassLoader)null);
            try (final PreparedStatement preparedLoadSql = _conn.prepareStatement(loadSql)) {
                preparedLoadSql.setString(1, id);
                preparedLoadSql.setString(2, this.getName());
                try (final ResultSet rst = preparedLoadSql.executeQuery()) {
                    if (rst.next()) {
                        try (final ObjectInputStream ois = this.getObjectInputStream(rst.getBinaryStream(2))) {
                            if (contextLog.isDebugEnabled()) {
                                contextLog.debug((Object)DataSourceStore.sm.getString(this.getStoreName() + ".loading", new Object[] { id, this.sessionTable }));
                            }
                            _session = (StandardSession)this.manager.createEmptySession();
                            _session.readObjectData(ois);
                            _session.setManager(this.manager);
                        }
                    }
                    else if (context.getLogger().isDebugEnabled()) {
                        contextLog.debug((Object)(this.getStoreName() + ": No persisted data object found"));
                    }
                    numberOfTries = 0;
                }
            }
            catch (final SQLException e) {
                contextLog.error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".SQLException", new Object[] { e }));
            }
            finally {
                context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                this.release(_conn);
            }
            --numberOfTries;
        }
        return _session;
    }
    
    @Override
    public void remove(final String id) throws IOException {
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            final Connection _conn = this.getConnection();
            if (_conn == null) {
                return;
            }
            try {
                this.remove(id, _conn);
                numberOfTries = 0;
            }
            catch (final SQLException e) {
                this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".SQLException", new Object[] { e }));
            }
            finally {
                this.release(_conn);
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)DataSourceStore.sm.getString(this.getStoreName() + ".removing", new Object[] { id, this.sessionTable }));
        }
    }
    
    private void remove(final String id, final Connection _conn) throws SQLException {
        final String removeSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ?  AND " + this.sessionAppCol + " = ?";
        try (final PreparedStatement preparedRemoveSql = _conn.prepareStatement(removeSql)) {
            preparedRemoveSql.setString(1, id);
            preparedRemoveSql.setString(2, this.getName());
            preparedRemoveSql.execute();
        }
    }
    
    @Override
    public void clear() throws IOException {
        final String clearSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
        for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
            final Connection _conn = this.getConnection();
            if (_conn == null) {
                return;
            }
            try (final PreparedStatement preparedClearSql = _conn.prepareStatement(clearSql)) {
                preparedClearSql.setString(1, this.getName());
                preparedClearSql.execute();
                numberOfTries = 0;
            }
            catch (final SQLException e) {
                this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".SQLException", new Object[] { e }));
            }
            finally {
                this.release(_conn);
            }
        }
    }
    
    @Override
    public void save(final Session session) throws IOException {
        ByteArrayOutputStream bos = null;
        final String saveSql = "INSERT INTO " + this.sessionTable + " (" + this.sessionIdCol + ", " + this.sessionAppCol + ", " + this.sessionDataCol + ", " + this.sessionValidCol + ", " + this.sessionMaxInactiveCol + ", " + this.sessionLastAccessedCol + ") VALUES (?, ?, ?, ?, ?, ?)";
        synchronized (session) {
            for (int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
                final Connection _conn = this.getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    this.remove(session.getIdInternal(), _conn);
                    bos = new ByteArrayOutputStream();
                    try (final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos))) {
                        ((StandardSession)session).writeObjectData(oos);
                    }
                    final byte[] obs = bos.toByteArray();
                    final int size = obs.length;
                    try (final ByteArrayInputStream bis = new ByteArrayInputStream(obs, 0, size);
                         final InputStream in = new BufferedInputStream(bis, size);
                         final PreparedStatement preparedSaveSql = _conn.prepareStatement(saveSql)) {
                        preparedSaveSql.setString(1, session.getIdInternal());
                        preparedSaveSql.setString(2, this.getName());
                        preparedSaveSql.setBinaryStream(3, in, size);
                        preparedSaveSql.setString(4, session.isValid() ? "1" : "0");
                        preparedSaveSql.setInt(5, session.getMaxInactiveInterval());
                        preparedSaveSql.setLong(6, session.getLastAccessedTime());
                        preparedSaveSql.execute();
                        numberOfTries = 0;
                    }
                }
                catch (final SQLException e) {
                    this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".SQLException", new Object[] { e }));
                }
                catch (final IOException ex) {}
                finally {
                    this.release(_conn);
                }
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)DataSourceStore.sm.getString(this.getStoreName() + ".saving", new Object[] { session.getIdInternal(), this.sessionTable }));
        }
    }
    
    @Override
    protected Connection open() throws SQLException {
        if (this.dataSourceName != null && this.dataSource == null) {
            final Context context = this.getManager().getContext();
            ClassLoader oldThreadContextCL = null;
            if (this.getLocalDataSource()) {
                oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, (ClassLoader)null);
            }
            try {
                final javax.naming.Context initCtx = new InitialContext();
                final javax.naming.Context envCtx = (javax.naming.Context)initCtx.lookup("java:comp/env");
                this.dataSource = (DataSource)envCtx.lookup(this.dataSourceName);
            }
            catch (final NamingException e) {
                context.getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".wrongDataSource", new Object[] { this.dataSourceName }), (Throwable)e);
            }
            finally {
                if (this.getLocalDataSource()) {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                }
            }
        }
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        }
        throw new IllegalStateException(DataSourceStore.sm.getString(this.getStoreName() + ".missingDataSource"));
    }
    
    @Override
    protected void close(final Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        }
        catch (final SQLException e) {
            this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".commitSQLException"), (Throwable)e);
        }
        try {
            dbConnection.close();
        }
        catch (final SQLException e) {
            this.manager.getContext().getLogger().error((Object)DataSourceStore.sm.getString(this.getStoreName() + ".close", new Object[] { e.toString() }));
        }
    }
}
