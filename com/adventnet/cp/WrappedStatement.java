package com.adventnet.cp;

import java.sql.Connection;
import java.sql.SQLWarning;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Logger;
import java.sql.Statement;

public class WrappedStatement implements Statement
{
    static Logger OUT;
    private int i;
    private Statement[] statements;
    
    public WrappedStatement() {
        this.statements = new Statement[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.statements[i] = null;
        }
    }
    
    public void addStatement(final Statement statement) {
        this.statements[this.i++] = statement;
    }
    
    public Statement getStatement(final int i) {
        return this.statements[i];
    }
    
    private int getIndexOfReadStatement() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return readDBIndex;
        }
        return 0;
    }
    
    private Statement getReadStatement() {
        return this.statements[this.getIndexOfReadStatement()];
    }
    
    private Statement getDefaultStatement() {
        return this.statements[0];
    }
    
    private int checkResult(final int[] r) {
        for (int i = 1; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null && r[i] != r[0]) {
                WrappedStatement.OUT.log(Level.INFO, "Default database result is: " + r[0] + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + r[i]);
            }
        }
        return r[0];
    }
    
    private boolean compareBooleanResult(final boolean[] r) {
        for (int i = 1; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null && r[i] != r[0]) {
                WrappedStatement.OUT.log(Level.INFO, "Default database result is: " + r[0] + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + r[i]);
            }
        }
        return r[0];
    }
    
    private int[] checkBatchResult(final int[][] r) {
        for (int i = 1; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null && !Arrays.equals(r[i], r[0])) {
                WrappedStatement.OUT.log(Level.INFO, "Default database result is: " + Arrays.toString(r[0]) + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + Arrays.toString(r[i]));
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].executeUpdate(sql);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadStatement().executeUpdate(sql);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.getReadStatement().unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.getReadStatement().isWrapperFor(iface);
    }
    
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        try {
            return this.getReadStatement().executeQuery(sql);
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e);
            throw sqle;
        }
    }
    
    @Override
    public void close() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].close();
            }
        }
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.getReadStatement().getMaxFieldSize();
    }
    
    @Override
    public void setMaxFieldSize(final int max) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].setMaxFieldSize(max);
            }
        }
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        return this.getReadStatement().getMaxRows();
    }
    
    @Override
    public void setMaxRows(final int max) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].setMaxRows(max);
            }
        }
    }
    
    @Override
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].setEscapeProcessing(enable);
            }
        }
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        return this.getReadStatement().getQueryTimeout();
    }
    
    @Override
    public void setQueryTimeout(final int seconds) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].setQueryTimeout(seconds);
            }
        }
    }
    
    @Override
    public void cancel() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].cancel();
            }
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.getReadStatement().getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].clearWarnings();
            }
        }
    }
    
    @Override
    public void setCursorName(final String name) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].setCursorName(name);
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].execute(sql);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadStatement().execute(sql);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.getReadStatement().getResultSet();
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        return this.getReadStatement().getUpdateCount();
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return this.getReadStatement().getMoreResults();
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this.getReadStatement().setFetchDirection(direction);
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return this.getReadStatement().getFetchDirection();
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        this.getReadStatement().setFetchSize(rows);
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return this.getReadStatement().getFetchSize();
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.getReadStatement().getResultSetConcurrency();
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        return this.getReadStatement().getResultSetType();
    }
    
    @Override
    public void addBatch(final String sql) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].addBatch(sql);
            }
        }
    }
    
    @Override
    public void clearBatch() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].clearBatch();
            }
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        final int[][] result = new int[3][];
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                try {
                    result[i] = this.statements[i].executeBatch();
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
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
                if (this.statements[i] != null) {
                    ((WrappedConnection)conn).addConnection(this.statements[i].getConnection());
                }
            }
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException("Connection couldn't be established " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedStatement.OUT.log(Level.SEVERE, "Connection couldn't be established", e);
            throw sqle;
        }
        return conn;
    }
    
    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        return this.getReadStatement().getMoreResults();
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.getReadStatement().getGeneratedKeys();
    }
    
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equalsIgnoreCase("insert") || first_word.equalsIgnoreCase("update") || first_word.equalsIgnoreCase("delete") || first_word.equalsIgnoreCase("create") || first_word.equalsIgnoreCase("drop") || first_word.equalsIgnoreCase("alter")) {
            final int[] result = new int[3];
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].executeUpdate(sql, autoGeneratedKeys);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadStatement().executeUpdate(sql, autoGeneratedKeys);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].executeUpdate(sql, columnIndexes);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadStatement().executeUpdate(sql, columnIndexes);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].executeUpdate(sql, columnNames);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getReadStatement().executeUpdate(sql, columnNames);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].execute(sql, autoGeneratedKeys);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadStatement().execute(sql, autoGeneratedKeys);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].execute(sql, columnIndexes);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadStatement().execute(sql, columnIndexes);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
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
                if (this.statements[i] != null) {
                    try {
                        result[i] = this.statements[i].execute(sql, columnNames);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getReadStatement().execute(sql, columnNames);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedStatement.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadStatement()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.getReadStatement().getResultSetHoldability();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.getReadStatement().isClosed();
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].setPoolable(poolable);
            }
        }
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return this.getReadStatement().isPoolable();
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.statements[i] != null) {
                this.statements[i].closeOnCompletion();
            }
        }
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.getReadStatement().isCloseOnCompletion();
    }
    
    static {
        WrappedStatement.OUT = Logger.getLogger(WrappedStatement.class.getName());
    }
}
