package com.adventnet.nms.store.relational;

import com.adventnet.management.log.LogMgr;
import com.adventnet.management.transaction.PreparedStatementWrapper;
import java.util.Vector;
import java.sql.Statement;
import java.sql.PreparedStatement;
import com.adventnet.nms.store.NmsStorageException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.management.log.LogUser;
import com.adventnet.management.transaction.TransactionAPI;
import com.adventnet.management.transaction.ConnectionPool;
import java.sql.DatabaseMetaData;
import java.util.Hashtable;

public class RelationalAPI
{
    public boolean newConnection;
    public String url;
    public String userName;
    public String password;
    public String driverName;
    static Hashtable connectionTable;
    private static int connectionNumber;
    private DatabaseMetaData dbmd;
    private static ConnectionPool conPool;
    private TransactionAPI transapi;
    private static LogUser MISCERR;
    
    public TransactionAPI getTransactionAPI() {
        return this.transapi;
    }
    
    public ConnectionPool getConnectionPool() {
        return RelationalAPI.conPool;
    }
    
    public RelationalAPI(final String url, final String driverName, final boolean newConnection) {
        this.url = "jdbc:odbc:WebNmsDB";
        this.userName = "admin";
        this.password = "public";
        this.driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
        this.dbmd = null;
        this.transapi = new TransactionAPI();
        if (driverName != null) {
            this.driverName = driverName;
        }
        if (url != null) {
            this.url = url;
        }
        this.userName = null;
        this.password = null;
        this.newConnection = newConnection;
        if (RelationalAPI.conPool == null) {
            RelationalAPI.conPool = new ConnectionPool(this.url, this.userName, this.password, this.driverName);
        }
        this.transapi.setConnectionPool(RelationalAPI.conPool);
    }
    
    public RelationalAPI(final String url, final String userName, final String password, final String driverName, final boolean newConnection) {
        this.url = "jdbc:odbc:WebNmsDB";
        this.userName = "admin";
        this.password = "public";
        this.driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
        this.dbmd = null;
        this.transapi = new TransactionAPI();
        if (driverName != null) {
            this.driverName = driverName;
        }
        if (url != null) {
            this.url = url;
        }
        this.userName = userName;
        this.password = password;
        this.newConnection = newConnection;
        if (RelationalAPI.conPool == null) {
            RelationalAPI.conPool = new ConnectionPool(this.url, this.userName, this.password, this.driverName);
        }
        this.transapi.setConnectionPool(RelationalAPI.conPool);
    }
    
    public RelationalAPI(final String url, final String userName, final String password, final String driverName, final boolean b, final int n, final int n2, final String s) {
        this.url = "jdbc:odbc:WebNmsDB";
        this.userName = "admin";
        this.password = "public";
        this.driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
        this.dbmd = null;
        this.transapi = new TransactionAPI();
        if (driverName != null) {
            this.driverName = driverName;
        }
        if (url != null) {
            this.url = url;
        }
        this.userName = userName;
        this.password = password;
        if (RelationalAPI.conPool == null) {
            RelationalAPI.conPool = new ConnectionPool(this.url, this.userName, this.password, this.driverName, b, n, n2, s);
        }
        this.transapi.setConnectionPool(RelationalAPI.conPool);
    }
    
    public Connection getConnection() {
        return RelationalAPI.conPool.getConnection();
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        return RelationalAPI.conPool.getMetaData();
    }
    
    public DatabaseMetaData getMetaData(final Connection connection) throws SQLException {
        return RelationalAPI.conPool.getMetaData(connection);
    }
    
    public static void disconnect() throws SQLException {
        RelationalAPI.conPool.disconnectAll();
    }
    
    public void disconnectOnlyThis() throws SQLException {
    }
    
    public ResultSet query(final Object o) throws NmsStorageException {
        final Connection connection = this.getConnection();
        if (connection == null) {
            RelationalAPI.MISCERR.fail("Connection object is null", null);
            throw new NmsStorageException("Connection object is null", new NullPointerException("Connection object is null"));
        }
        ResultSet set = null;
        if (o instanceof PreparedStatement) {
            final PreparedStatement preparedStatement = (PreparedStatement)o;
            try {
                set = preparedStatement.executeQuery();
                return set;
            }
            catch (final SQLException ex) {
                throw new NmsStorageException(ex.getMessage(), ex);
            }
        }
        if (o instanceof String) {
            final String s = (String)o;
            try {
                set = connection.createStatement().executeQuery(s);
                return set;
            }
            catch (final SQLException ex2) {
                throw new NmsStorageException(ex2.getMessage() + "\n Statement being executed was " + s, ex2);
            }
        }
        RelationalAPI.MISCERR.fail(" Unknown type received by RelationalAPI. Was expecting String or a PreparedStatement ", null);
        return set;
    }
    
    public Statement query(final String s, final boolean b) throws NmsStorageException {
        return RelationalAPI.conPool.query(s, b);
    }
    
    public ResultSet[] query(final Vector vector) throws NmsStorageException {
        if (vector == null || vector.size() == 0) {
            throw new NmsStorageException("No SQL Query to execute.", new NullPointerException("No SQL Query to execute"));
        }
        final ResultSet[] array = new ResultSet[vector.size()];
        for (int i = 0; i < vector.size(); ++i) {
            array[i] = this.query(vector.elementAt(i));
        }
        return array;
    }
    
    public ResultSet[] query(final String s, final String s2) throws NmsStorageException {
        final Connection connection = this.getConnection();
        if (connection == null) {
            RelationalAPI.MISCERR.fail("Connection object is null", null);
            throw new NmsStorageException("Connection object is null", new NullPointerException("Connection object is null"));
        }
        final Vector andGetTheTokens = this.parseAndGetTheTokens(s, s2);
        if (andGetTheTokens == null) {
            return null;
        }
        final int size = andGetTheTokens.size();
        if (size == 0) {
            return null;
        }
        Statement statement;
        try {
            statement = connection.createStatement();
        }
        catch (final SQLException ex) {
            throw new NmsStorageException(ex.getMessage(), ex);
        }
        final ResultSet[] array = new ResultSet[size];
        for (int i = 0; i < size; ++i) {
            try {
                array[i] = statement.executeQuery((String)andGetTheTokens.elementAt(i));
            }
            catch (final SQLException ex2) {
                throw new NmsStorageException(ex2.getMessage(), ex2);
            }
        }
        return array;
    }
    
    private Vector parseAndGetTheTokens(final String s, final String s2) {
        try {
            if (s == null || s.equals("")) {
                return null;
            }
            final Vector vector = new Vector();
            String substring = new String(s);
            final int length = s2.length();
            if (length == 0) {
                vector.addElement(substring);
                return vector;
            }
            while (!substring.equals("")) {
                final int index = substring.indexOf(s2);
                if (index < 0) {
                    vector.addElement(substring);
                }
                else {
                    final String substring2 = substring.substring(0, index);
                    if (substring2 != null && !substring2.equals("")) {
                        vector.addElement(substring2);
                    }
                    final int n = index + length;
                    if (n < substring.length()) {
                        substring = substring.substring(n);
                        continue;
                    }
                }
                return vector;
            }
            return vector;
        }
        catch (final Exception ex) {
            RelationalAPI.MISCERR.fail(" Error trying to tokenize complex Statement in RelationalAPI:" + s, ex);
            return null;
        }
    }
    
    public ResultSet executeQuery(final PreparedStatement preparedStatement) throws NmsStorageException {
        return RelationalAPI.conPool.executeQuery(preparedStatement);
    }
    
    public int executeUpdate(final PreparedStatement preparedStatement) throws NmsStorageException {
        return RelationalAPI.conPool.executeUpdate(preparedStatement);
    }
    
    public PreparedStatementWrapper fetchPreparedStatement(final int n) {
        return RelationalAPI.conPool.fetchPreparedStatement(n);
    }
    
    public boolean isInTransaction() {
        return RelationalAPI.conPool.isInTransaction();
    }
    
    public void returnPreparedStatement(final PreparedStatementWrapper preparedStatementWrapper) {
        RelationalAPI.conPool.returnPreparedStatement(preparedStatementWrapper);
    }
    
    public PreparedStatement getPreparedStatement(final String s, final boolean b) {
        return RelationalAPI.conPool.getPreparedStatement(s, b);
    }
    
    public int getPreparedStatementID(final String s) {
        return RelationalAPI.conPool.addPSToPool(s);
    }
    
    public PreparedStatement getPreparedStatement(final String s) {
        return this.getPreparedStatement(s, false);
    }
    
    public void executeTheStatement(final Object o) throws NmsStorageException {
        RelationalAPI.conPool.executeTheStatement(o);
    }
    
    public void execute(final String s) throws NmsStorageException {
        RelationalAPI.conPool.execute(s);
    }
    
    public boolean setAutoCommit(final boolean b) throws SQLException {
        return true;
    }
    
    public Hashtable getConnectionTable() {
        return RelationalAPI.connectionTable;
    }
    
    static {
        RelationalAPI.connectionTable = new Hashtable();
        RelationalAPI.connectionNumber = 0;
        RelationalAPI.MISCERR = LogMgr.getLogUser("MISCUSER");
    }
}
