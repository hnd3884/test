package com.adventnet.cp;

import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.ParameterMetaData;
import java.net.URL;
import java.util.Calendar;
import java.sql.ResultSetMetaData;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLWarning;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Logger;
import java.sql.PreparedStatement;

public class WrappedPreparedStatement implements PreparedStatement
{
    static Logger OUT;
    private int i;
    private PreparedStatement[] preparedStatements;
    
    public WrappedPreparedStatement() {
        this.preparedStatements = new PreparedStatement[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.preparedStatements[i] = null;
        }
    }
    
    public void addPreparedStatement(final PreparedStatement pstmt) {
        this.preparedStatements[this.i++] = pstmt;
    }
    
    public PreparedStatement getPreparedStatement(final int i) {
        return this.preparedStatements[i];
    }
    
    private int getIndexOfReadPreparedStatement() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return readDBIndex;
        }
        return 0;
    }
    
    private PreparedStatement getReadPreparedStatement() {
        return this.preparedStatements[this.getIndexOfReadPreparedStatement()];
    }
    
    private PreparedStatement getDefaultPreparedStatement() {
        return this.preparedStatements[0];
    }
    
    private int checkResult(final int[] r) {
        for (int i = 1; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null && r[i] != r[0]) {
                WrappedPreparedStatement.OUT.log(Level.INFO, "Default database result is: " + r[0] + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + r[i]);
            }
        }
        return r[0];
    }
    
    private boolean compareBooleanResult(final boolean[] r) {
        for (int i = 1; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null && r[i] != r[0]) {
                WrappedPreparedStatement.OUT.log(Level.INFO, "Default database result is: " + r[0] + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + r[i]);
            }
        }
        return r[0];
    }
    
    private int[] checkBatchResult(final int[][] r) {
        for (int i = 1; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null && !Arrays.equals(r[i], r[0])) {
                WrappedPreparedStatement.OUT.log(Level.INFO, "Default database result is: " + Arrays.toString(r[0]) + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + Arrays.toString(r[i]));
            }
        }
        return r[0];
    }
    
    @Override
    public int executeUpdate(final String sql) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final int[] result = new int[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].executeUpdate(sql);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadPreparedStatement().executeUpdate(sql);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        try {
            return this.getReadPreparedStatement().executeQuery(sql);
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e);
            throw sqle;
        }
    }
    
    @Override
    public void close() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].close();
            }
        }
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.getReadPreparedStatement().getMaxFieldSize();
    }
    
    @Override
    public void setMaxFieldSize(final int max) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setMaxFieldSize(max);
            }
        }
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        return this.getReadPreparedStatement().getMaxRows();
    }
    
    @Override
    public void setMaxRows(final int max) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setMaxRows(max);
            }
        }
    }
    
    @Override
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setEscapeProcessing(enable);
            }
        }
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        return this.getReadPreparedStatement().getQueryTimeout();
    }
    
    @Override
    public void setQueryTimeout(final int seconds) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setQueryTimeout(seconds);
            }
        }
    }
    
    @Override
    public void cancel() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].cancel();
            }
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.getReadPreparedStatement().getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].clearWarnings();
            }
        }
    }
    
    @Override
    public void setCursorName(final String name) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setCursorName(name);
            }
        }
    }
    
    @Override
    public boolean execute(final String sql) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final boolean[] result = new boolean[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].execute(sql);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadPreparedStatement().execute(sql);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.getReadPreparedStatement().getResultSet();
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        return this.getReadPreparedStatement().getUpdateCount();
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return this.getReadPreparedStatement().getMoreResults();
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this.getReadPreparedStatement().setFetchDirection(direction);
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return this.getReadPreparedStatement().getFetchDirection();
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        this.getReadPreparedStatement().setFetchSize(rows);
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return this.getReadPreparedStatement().getFetchSize();
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.getReadPreparedStatement().getResultSetConcurrency();
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        return this.getReadPreparedStatement().getResultSetType();
    }
    
    @Override
    public void addBatch(final String sql) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].addBatch(sql);
            }
        }
    }
    
    @Override
    public void clearBatch() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].clearBatch();
            }
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        final int[][] result = new int[3][];
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                try {
                    result[i] = this.preparedStatements[i].executeBatch();
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        return this.checkBatchResult(result);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        final Connection conn = new WrappedConnection();
        try {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    ((WrappedConnection)conn).addConnection(this.preparedStatements[i].getConnection());
                }
            }
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException("Connection couldn't be established " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Connection couldn't be established", e);
            throw sqle;
        }
        return conn;
    }
    
    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        return this.getReadPreparedStatement().getMoreResults(current);
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.getReadPreparedStatement().getGeneratedKeys();
    }
    
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final int[] result = new int[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].executeUpdate(sql, autoGeneratedKeys);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadPreparedStatement().executeUpdate(sql, autoGeneratedKeys);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final int[] result = new int[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].executeUpdate(sql, columnIndexes);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadPreparedStatement().executeUpdate(sql, columnIndexes);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final int[] result = new int[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].executeUpdate(sql, columnNames);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadPreparedStatement().executeUpdate(sql, columnNames);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final boolean[] result = new boolean[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].execute(sql, autoGeneratedKeys);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadPreparedStatement().execute(sql, autoGeneratedKeys);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final boolean[] result = new boolean[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].execute(sql, columnIndexes);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadPreparedStatement().execute(sql, columnIndexes);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final boolean[] result = new boolean[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.preparedStatements[i] != null) {
                    try {
                        result[i] = this.preparedStatements[i].execute(sql, columnNames);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadPreparedStatement().execute(sql, columnNames);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.getReadPreparedStatement().getResultSetHoldability();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.getReadPreparedStatement().isClosed();
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        this.getReadPreparedStatement().setPoolable(poolable);
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return this.getReadPreparedStatement().isPoolable();
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].closeOnCompletion();
            }
        }
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.getReadPreparedStatement().isCloseOnCompletion();
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.getReadPreparedStatement().unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.getReadPreparedStatement().isWrapperFor(iface);
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            return this.getReadPreparedStatement().executeQuery();
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadPreparedStatement()), e);
            throw sqle;
        }
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        final int[] result = new int[3];
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                try {
                    result[i] = this.preparedStatements[i].executeUpdate();
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        return this.checkResult(result);
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNull(parameterIndex, sqlType);
            }
        }
    }
    
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBoolean(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setByte(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setShort(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setInt(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setLong(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setFloat(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setDouble(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBigDecimal(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setString(final int parameterIndex, final String x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setString(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBytes(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setDate(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setTime(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setTimestamp(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setAsciiStream(parameterIndex, x, length);
            }
        }
    }
    
    @Override
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setUnicodeStream(parameterIndex, x, length);
            }
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBinaryStream(parameterIndex, x, length);
            }
        }
    }
    
    @Override
    public void clearParameters() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].clearParameters();
            }
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setObject(parameterIndex, x, targetSqlType);
            }
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setObject(parameterIndex, x);
            }
        }
    }
    
    @Override
    public boolean execute() throws SQLException {
        final boolean[] result = new boolean[3];
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                try {
                    result[i] = this.preparedStatements[i].execute();
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedPreparedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        return this.compareBooleanResult(result);
    }
    
    @Override
    public void addBatch() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].addBatch();
            }
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setCharacterStream(parameterIndex, reader, length);
            }
        }
    }
    
    @Override
    public void setRef(final int parameterIndex, final Ref x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setRef(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final Blob x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBlob(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Clob x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setClob(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setArray(final int parameterIndex, final Array x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setArray(parameterIndex, x);
            }
        }
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.getReadPreparedStatement().getMetaData();
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setDate(parameterIndex, x, cal);
            }
        }
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setTime(parameterIndex, x, cal);
            }
        }
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setTimestamp(parameterIndex, x, cal);
            }
        }
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNull(parameterIndex, sqlType, typeName);
            }
        }
    }
    
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setURL(parameterIndex, x);
            }
        }
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.getReadPreparedStatement().getParameterMetaData();
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setRowId(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNString(parameterIndex, value);
            }
        }
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNCharacterStream(parameterIndex, value, length);
            }
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNClob(parameterIndex, value);
            }
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setClob(parameterIndex, reader, length);
            }
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBlob(parameterIndex, inputStream, length);
            }
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNClob(parameterIndex, reader, length);
            }
        }
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setSQLXML(parameterIndex, xmlObject);
            }
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scaleOrLength) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setObject(parameterIndex, x, targetSqlType, scaleOrLength);
            }
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setAsciiStream(parameterIndex, x, length);
            }
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBinaryStream(parameterIndex, x, length);
            }
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setCharacterStream(parameterIndex, reader, length);
            }
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setAsciiStream(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBinaryStream(parameterIndex, x);
            }
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setCharacterStream(parameterIndex, reader);
            }
        }
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNCharacterStream(parameterIndex, value);
            }
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setClob(parameterIndex, reader);
            }
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setBlob(parameterIndex, inputStream);
            }
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.preparedStatements[i] != null) {
                this.preparedStatements[i].setNClob(parameterIndex, reader);
            }
        }
    }
    
    static {
        WrappedPreparedStatement.OUT = Logger.getLogger(WrappedPreparedStatement.class.getName());
    }
}
