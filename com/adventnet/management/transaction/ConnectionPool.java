package com.adventnet.management.transaction;

import com.adventnet.nms.store.relational.ConnectionAuthenticationInterface;
import java.sql.Statement;
import java.util.LinkedList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import com.adventnet.nms.store.NmsStorageException;
import com.adventnet.management.log.LogBaseWriter;
import com.adventnet.management.log.DefaultLogUser;
import com.adventnet.management.log.LogMgr;
import java.sql.DatabaseMetaData;
import com.adventnet.management.scheduler.Scheduler;
import com.adventnet.management.log.LogUser;
import java.util.Vector;
import java.util.Hashtable;

public class ConnectionPool
{
    public String url;
    public String userName;
    public String password;
    private TransactionAPI transapi;
    public String driverName;
    ConnectionWrapper[] freeConnectionList;
    Hashtable threadVsConn;
    int num_trans;
    int num_nontrans;
    int totalcon;
    private int PS_OBJECTS;
    private long MAX_TIME;
    private Vector timedOutThreads;
    private String conAuth;
    public LogUser transUser;
    Scheduler sch;
    private DatabaseMetaData dbmd;
    private static LogUser MISCERR;
    private int STAT_OBJECTS;
    private static ConnectionPool conPool;
    int tracker;
    int i;
    int totalCount;
    int tracker1;
    int tracker2;
    int tracker3;
    static int count;
    private boolean transactionSupport;
    private boolean issolid;
    private boolean isOracle;
    private Vector conVect;
    private int timeOutValue;
    private ConnectionBreakHandler connectionBreakHandler;
    
    public ConnectionPool() {
        this.url = "jdbc:odbc:WebNmsDB";
        this.userName = "admin";
        this.password = "public";
        this.transapi = new TransactionAPI();
        this.driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
        this.threadVsConn = new Hashtable();
        this.PS_OBJECTS = 200;
        this.MAX_TIME = 10000L;
        this.timedOutThreads = new Vector();
        this.conAuth = null;
        this.transUser = null;
        this.sch = null;
        this.dbmd = null;
        this.STAT_OBJECTS = 5;
        this.tracker = this.num_trans;
        this.i = 0;
        this.totalCount = 0;
        this.tracker1 = 0;
        this.tracker2 = this.num_trans;
        this.tracker3 = this.num_trans;
        this.transactionSupport = false;
        this.issolid = false;
        this.isOracle = false;
        this.conVect = new Vector();
        this.timeOutValue = 20000;
        this.connectionBreakHandler = null;
    }
    
    public ConnectionPool(final String url, final String userName, final String password, final String driverName) {
        this.url = "jdbc:odbc:WebNmsDB";
        this.userName = "admin";
        this.password = "public";
        this.transapi = new TransactionAPI();
        this.driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
        this.threadVsConn = new Hashtable();
        this.PS_OBJECTS = 200;
        this.MAX_TIME = 10000L;
        this.timedOutThreads = new Vector();
        this.conAuth = null;
        this.transUser = null;
        this.sch = null;
        this.dbmd = null;
        this.STAT_OBJECTS = 5;
        this.tracker = this.num_trans;
        this.i = 0;
        this.totalCount = 0;
        this.tracker1 = 0;
        this.tracker2 = this.num_trans;
        this.tracker3 = this.num_trans;
        this.transactionSupport = false;
        this.issolid = false;
        this.isOracle = false;
        this.conVect = new Vector();
        this.timeOutValue = 20000;
        this.connectionBreakHandler = null;
        if (driverName != null) {
            this.driverName = driverName;
        }
        if (this.url != null) {
            this.url = url;
        }
        this.userName = userName;
        this.password = password;
        this.transUser = LogMgr.getLogUser("TUSER");
        if (this.transUser == null) {
            (this.transUser = new DefaultLogUser(null, 0, null)).setStatus(false);
        }
        if (ConnectionPool.MISCERR == null) {
            ConnectionPool.MISCERR = new DefaultLogUser(null, 0, null);
        }
        this.transapi.setConnectionPool(this);
        ConnectionPool.conPool = this;
    }
    
    public ConnectionPool(final String url, final String userName, final String password, final String driverName, final boolean b, final int n, final int n2, final String conAuth) {
        this.url = "jdbc:odbc:WebNmsDB";
        this.userName = "admin";
        this.password = "public";
        this.transapi = new TransactionAPI();
        this.driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
        this.threadVsConn = new Hashtable();
        this.PS_OBJECTS = 200;
        this.MAX_TIME = 10000L;
        this.timedOutThreads = new Vector();
        this.conAuth = null;
        this.transUser = null;
        this.sch = null;
        this.dbmd = null;
        this.STAT_OBJECTS = 5;
        this.tracker = this.num_trans;
        this.i = 0;
        this.totalCount = 0;
        this.tracker1 = 0;
        this.tracker2 = this.num_trans;
        this.tracker3 = this.num_trans;
        this.transactionSupport = false;
        this.issolid = false;
        this.isOracle = false;
        this.conVect = new Vector();
        this.timeOutValue = 20000;
        this.connectionBreakHandler = null;
        if (driverName != null) {
            this.driverName = driverName;
        }
        if (this.url != null) {
            this.url = url;
        }
        this.userName = userName;
        this.password = password;
        this.transUser = LogMgr.getLogUser("TUSER");
        this.transapi.setConnectionPool(this);
        if (this.transUser == null) {
            (this.transUser = new DefaultLogUser(null, 0, null)).setStatus(false);
        }
        if (ConnectionPool.MISCERR == null) {
            ConnectionPool.MISCERR = new DefaultLogUser(null, 0, null);
        }
        try {
            this.conAuth = conAuth;
            this.createConnection(n, n2);
        }
        catch (final NmsStorageException ex) {
            System.err.println("Exception in creating connections " + ex);
        }
        ConnectionPool.conPool = this;
    }
    
    public TransactionAPI getTransactionAPI() {
        return this.transapi;
    }
    
    public static ConnectionPool getInstance() {
        return ConnectionPool.conPool;
    }
    
    private void checkIfTransactionIsSupported(int num_trans, int num_nontrans) throws NmsStorageException {
        Connection connection = null;
        boolean transactionMode = false;
        try {
            try {
                Class.forName(this.driverName);
            }
            catch (final ClassNotFoundException ex) {
                throw new NmsStorageException("The driver name given , is not in the classpath." + " " + ex.getMessage(), ex);
            }
            try {
                if (this.userName == null && this.password == null) {
                    connection = DriverManager.getConnection(this.url);
                }
                else {
                    connection = DriverManager.getConnection(this.url, this.userName, this.password);
                }
            }
            catch (final SQLException ex2) {
                throw new NmsStorageException(ex2.getMessage(), ex2);
            }
            if (connection == null) {
                System.err.println("Couldn't establish connection with the database.Check if the parameters are correct.");
                throw new NmsStorageException("Error in getting the Connection", new Exception());
            }
            if (num_trans < 0) {
                num_trans = 0;
            }
            if (num_nontrans <= 0) {
                num_nontrans = 3;
            }
            final DatabaseMetaData metaData = connection.getMetaData();
            if (!metaData.supportsTransactions() && num_trans != 0) {
                System.err.println(metaData.getDatabaseProductName() + " does not support transaction. Hence , disabling transaction support");
                num_nontrans += num_trans;
                num_trans = 0;
            }
            else if (num_trans != 0) {
                System.out.println("Transaction is enabled ");
                transactionMode = true;
            }
            if (num_trans + num_nontrans <= 0) {
                num_trans = 0;
                num_nontrans = 2;
                System.out.println("Total Number of connections is set to a invalid value . Hence , defaulting to 2 non transactional connections and disabling transaction.");
            }
            this.setTransactionMode(transactionMode);
            this.num_nontrans = num_nontrans;
            this.num_trans = num_trans;
        }
        catch (final SQLException ex3) {
            this.handleConnectionFail(connection, ex3.getErrorCode());
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final SQLException ex4) {}
        }
    }
    
    private synchronized void updateCounter() {
        if (this.tracker < this.totalcon - 1) {
            ++this.tracker;
        }
        else {
            this.tracker = this.num_trans;
        }
    }
    
    public Connection getConnection() {
        if (this.freeConnectionList == null) {
            return null;
        }
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(Thread.currentThread());
        if (connectionWrapper != null) {
            return connectionWrapper.con;
        }
        this.updateCounter();
        final ConnectionWrapper connectionWrapper2 = this.freeConnectionList[this.tracker];
        if (connectionWrapper2 != null) {
            return connectionWrapper2.con;
        }
        return null;
    }
    
    public void disconnectAll() {
        synchronized (this.freeConnectionList) {
            for (int i = 0; i < this.freeConnectionList.length; ++i) {
                if (this.freeConnectionList[i] != null) {
                    try {
                        this.freeConnectionList[i].con.close();
                    }
                    catch (final Exception ex) {
                        System.err.println("Exception while closing the connections" + ex);
                    }
                    this.freeConnectionList[i].con = null;
                }
            }
            System.out.println(this.freeConnectionList.length + " Connections disconnected from database in Connection pool");
        }
    }
    
    public synchronized int addPSToPool(final String s) {
        int totalCount = -1;
        if (s == null) {
            return -1;
        }
        try {
            synchronized (this.freeConnectionList) {
                synchronized (this.threadVsConn) {
                    final Enumeration keys = this.threadVsConn.keys();
                    while (keys.hasMoreElements()) {
                        this.formPreparedStatement((ConnectionWrapper)this.threadVsConn.get(keys.nextElement()), s, this.totalCount);
                    }
                    for (int i = 0; i < this.freeConnectionList.length; ++i) {
                        final ConnectionWrapper connectionWrapper = this.freeConnectionList[i];
                        if (connectionWrapper != null) {
                            this.formPreparedStatement(connectionWrapper, s, this.totalCount);
                        }
                    }
                }
            }
            totalCount = this.totalCount;
            ++this.totalCount;
            this.transUser.log(" Created PreparedStatement  " + s + " with id " + totalCount, 3);
        }
        catch (final Exception ex) {
            System.err.println("Exception while preparing the statements " + ex);
        }
        return totalCount;
    }
    
    public void setNumberInCache(final int ps_OBJECTS) {
        this.PS_OBJECTS = ps_OBJECTS;
    }
    
    private void formPreparedStatement(final ConnectionWrapper connectionWrapper, final String sqlString, final int id) throws SQLException {
        final PreparedStatementWrapper preparedStatementWrapper = new PreparedStatementWrapper();
        preparedStatementWrapper.setId(id);
        preparedStatementWrapper.setStatus(false);
        preparedStatementWrapper.setSqlString(sqlString);
        connectionWrapper.psList.addElement(preparedStatementWrapper);
    }
    
    public boolean lockConnectionForTransaction(final int n) {
        final ConnectionWrapper freeConnectionForTransaction = this.getFreeConnectionForTransaction(n);
        if (freeConnectionForTransaction != null) {
            this.threadVsConn.put(Thread.currentThread(), freeConnectionForTransaction);
            return true;
        }
        return false;
    }
    
    private synchronized void updateCounterForTransConnections() {
        if (this.tracker1 < this.num_trans - 1) {
            ++this.tracker1;
        }
        else {
            this.tracker1 = 0;
        }
    }
    
    public ConnectionWrapper getFreeConnectionForTransaction(final int n) {
        if (this.num_trans == 0) {
            return null;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        this.updateCounterForTransConnections();
        int tracker1 = this.tracker1;
        while (this.hasWaitTimedOut(currentTimeMillis, n)) {
            for (int i = 0; i < this.num_trans; ++i) {
                final ConnectionWrapper connectionWrapper = this.freeConnectionList[tracker1];
                if (connectionWrapper != null) {
                    this.freeConnectionList[tracker1] = null;
                    return connectionWrapper;
                }
                if (tracker1 != this.num_trans - 1) {
                    ++tracker1;
                }
                else {
                    tracker1 = 0;
                }
            }
        }
        return null;
    }
    
    private boolean hasWaitTimedOut(final long n, final int n2) {
        return n2 == -1 || System.currentTimeMillis() - n < n2;
    }
    
    public Connection getConnectionForTransaction() {
        return this.getConnectionForTransaction(Thread.currentThread());
    }
    
    public Connection getConnectionForTransaction(final Thread thread) {
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(thread);
        if (connectionWrapper != null) {
            return connectionWrapper.con;
        }
        this.timedOutThreads.remove(thread);
        return null;
    }
    
    public void freeConnectionForTransaction() {
        this.freeConnectionForTransaction(Thread.currentThread());
    }
    
    void freeConnectionForTransaction(final Thread thread) {
        final ConnectionWrapper connectionWrapper = this.threadVsConn.remove(thread);
        if (connectionWrapper != null) {
            this.freeConnectionList[connectionWrapper.con_num] = connectionWrapper;
        }
    }
    
    void addTimedOutThread(final Thread thread) {
        this.timedOutThreads.addElement(thread);
    }
    
    void setTimeOutStatusForConnection(final boolean isTimedOut, final Thread thread) {
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(thread);
        if (connectionWrapper != null) {
            synchronized (connectionWrapper) {
                connectionWrapper.isTimedOut = isTimedOut;
            }
        }
    }
    
    public void setMaxTime(final long max_TIME) {
        this.MAX_TIME = max_TIME;
    }
    
    public void setPSCacheSize(final int ps_OBJECTS) {
        this.PS_OBJECTS = ps_OBJECTS;
    }
    
    public void setStmtCacheSize(final int stat_OBJECTS) {
        if (stat_OBJECTS > 0) {
            this.STAT_OBJECTS = stat_OBJECTS;
        }
        else {
            this.STAT_OBJECTS = 5;
        }
    }
    
    public PreparedStatement prepareStatement(final String s) {
        try {
            final Thread currentThread = Thread.currentThread();
            final ConnectionWrapper connectionWrapper = this.threadVsConn.get(currentThread);
            if (connectionWrapper != null) {
                return connectionWrapper.con.prepareStatement(s);
            }
            if (this.timedOutThreads.remove(currentThread)) {
                return null;
            }
            return this.freeConnectionList[this.num_trans].con.prepareStatement(s);
        }
        catch (final Exception ex) {
            System.err.println("Exception in preparing the statement " + ex);
            return null;
        }
    }
    
    public boolean isInTransaction() {
        return this.threadVsConn.get(Thread.currentThread()) != null;
    }
    
    private synchronized void updateCounterForNonTransConnections() {
        if (this.tracker2 < this.totalcon - 1) {
            ++this.tracker2;
        }
        else {
            this.tracker2 = this.num_trans;
        }
    }
    
    public boolean isInTimedOutThreads() {
        return this.timedOutThreads.contains(Thread.currentThread());
    }
    
    private synchronized void updateCounterForConnections() {
        if (this.tracker3 < this.totalcon - 1) {
            ++this.tracker3;
        }
        else {
            this.tracker3 = this.num_trans;
        }
    }
    
    PreparedStatementWrapper fetchStatement() throws TransactionException {
        final Thread currentThread = Thread.currentThread();
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(currentThread);
        if (connectionWrapper != null) {
            return connectionWrapper.getStatement();
        }
        if (this.timedOutThreads.remove(currentThread)) {
            throw new TransactionException(" Operation timedout on Connection object psid   for the thread " + currentThread);
        }
        this.updateCounterForConnections();
        return this.freeConnectionList[this.tracker3].getStatement();
    }
    
    public int executeUpdateStmt(final String s) throws NmsStorageException {
        try {
            final PreparedStatementWrapper fetchStatement = this.fetchStatement();
            synchronized (fetchStatement) {
                return fetchStatement.getStatement().executeUpdate(s);
            }
        }
        catch (final SQLException ex) {
            this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex.getErrorCode());
            throw new NmsStorageException(ex.getMessage(), ex);
        }
    }
    
    public ResultSet executeQueryStmt(final String s) throws NmsStorageException {
        try {
            final PreparedStatementWrapper fetchStatement = this.fetchStatement();
            synchronized (fetchStatement) {
                final ResultSet executeQuery = fetchStatement.getStatement().executeQuery(s);
                if (this.issolid && !this.isInTransaction()) {
                    this.commitAllNonTransactionConnections();
                }
                return executeQuery;
            }
        }
        catch (final SQLException ex) {
            this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex.getErrorCode());
            throw new NmsStorageException(ex.getMessage(), ex);
        }
    }
    
    public PreparedStatementWrapper fetchPreparedStatement(final int n) throws TransactionException {
        if (n < 0 || n > this.totalCount) {
            return null;
        }
        final Thread currentThread = Thread.currentThread();
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(currentThread);
        if (connectionWrapper != null) {
            final PreparedStatementWrapper preparedStatementWrapper = connectionWrapper.psList.elementAt(n);
            preparedStatementWrapper.setStatus(true);
            preparedStatementWrapper.setTimeOfConnection(System.currentTimeMillis());
            this.updateCache(connectionWrapper, preparedStatementWrapper);
            return preparedStatementWrapper;
        }
        if (this.timedOutThreads.remove(currentThread)) {
            throw new TransactionException(" Operation timedout on Connection object psid " + n + " for the thread " + currentThread);
        }
        this.updateCounterForNonTransConnections();
        ConnectionWrapper connectionWrapper2 = null;
        PreparedStatementWrapper preparedStatementWrapper2 = null;
    Block_4:
        while (true) {
            int n2 = this.tracker2;
            String sqlString = null;
            for (int i = this.num_trans; i < this.totalcon; ++i) {
                connectionWrapper2 = this.freeConnectionList[n2];
                preparedStatementWrapper2 = connectionWrapper2.psList.elementAt(n);
                sqlString = preparedStatementWrapper2.getSqlString();
                if (!preparedStatementWrapper2.setAsUsed(this.MAX_TIME)) {
                    break Block_4;
                }
                if (n2 != this.totalcon - 1) {
                    ++n2;
                }
                else {
                    n2 = this.num_trans;
                }
            }
            this.transUser.log("Search not succesful  " + n + " the probelm statement is " + sqlString, 3);
        }
        preparedStatementWrapper2.setStatus(true);
        preparedStatementWrapper2.setTimeOfConnection(System.currentTimeMillis());
        this.updateCache(connectionWrapper2, preparedStatementWrapper2);
        return preparedStatementWrapper2;
    }
    
    private PreparedStatementWrapper relievePreparedStat(final ConnectionWrapper connectionWrapper, final PreparedStatementWrapper preparedStatementWrapper) {
        preparedStatementWrapper.setStatus(true);
        preparedStatementWrapper.setTimeOfConnection(System.currentTimeMillis());
        this.updateCache(connectionWrapper, preparedStatementWrapper);
        return preparedStatementWrapper;
    }
    
    private void updateCache(final ConnectionWrapper connectionWrapper, final PreparedStatementWrapper preparedStatementWrapper) {
        synchronized (connectionWrapper.psCache) {
            try {
                if (preparedStatementWrapper.getPreparedStatement() == null) {
                    int i = 0;
                    final int size = connectionWrapper.psCache.size();
                    if (size >= this.PS_OBJECTS) {
                        while (i < size) {
                            final PreparedStatementWrapper preparedStatementWrapper2 = connectionWrapper.psCache.get(i);
                            if (!preparedStatementWrapper2.getStatus() || System.currentTimeMillis() - preparedStatementWrapper2.getTimeOfConnection() > this.MAX_TIME) {
                                preparedStatementWrapper2.getPreparedStatement().close();
                                preparedStatementWrapper2.setPreparedStatement(null);
                                connectionWrapper.psCache.remove(i);
                                break;
                            }
                            ++i;
                        }
                        if (i == size) {
                            this.transUser.log("Cache size growing for connection " + connectionWrapper.con + " Size = " + connectionWrapper.psCache.size(), 3);
                        }
                    }
                    preparedStatementWrapper.setPreparedStatement(connectionWrapper.con.prepareStatement(preparedStatementWrapper.getSqlString()));
                    connectionWrapper.psCache.add(preparedStatementWrapper);
                }
                else {
                    connectionWrapper.psCache.remove(preparedStatementWrapper);
                    connectionWrapper.psCache.add(preparedStatementWrapper);
                }
            }
            catch (final Exception ex) {
                throw new TransactionException(" Exception while preparing the statement with id " + preparedStatementWrapper.getId());
            }
        }
    }
    
    public void setConAuthInterfaceClassName(final String conAuth) {
        this.conAuth = conAuth;
    }
    
    public boolean isTransactionEnabled() {
        return this.transactionSupport;
    }
    
    public void setTransactionMode(final boolean transactionSupport) {
        this.transactionSupport = transactionSupport;
    }
    
    public int getNumOfNonTransactionConnections() {
        return this.num_nontrans;
    }
    
    public int getNumOfTransactionConnections() {
        return this.num_trans;
    }
    
    public void createConnection(final int n, final int n2) throws NmsStorageException {
        this.checkIfTransactionIsSupported(n, n2);
        this.totalcon = this.num_trans + this.num_nontrans;
        this.freeConnectionList = new ConnectionWrapper[this.totalcon];
        for (int i = 0; i < this.totalcon; ++i) {
            final Connection connection = this.createConnection(i >= this.num_trans);
            final Vector statements = this.createStatements(connection);
            final ConnectionWrapper connectionWrapper = new ConnectionWrapper();
            connectionWrapper.setStatementCacheSize(this.STAT_OBJECTS);
            connectionWrapper.con_num = i;
            connectionWrapper.con = connection;
            connectionWrapper.psList = new Vector();
            connectionWrapper.psCache = new LinkedList();
            connectionWrapper.setStatementList(statements);
            this.freeConnectionList[i] = connectionWrapper;
        }
        this.tracker = this.num_trans;
        this.tracker1 = 0;
        this.tracker2 = this.num_trans;
        this.tracker3 = this.num_trans;
    }
    
    private Vector createStatements(final Connection connection) {
        if (connection == null) {
            return null;
        }
        final Vector vector = new Vector();
        try {
            for (int i = 0; i < this.STAT_OBJECTS; ++i) {
                final Statement statement = connection.createStatement();
                final PreparedStatementWrapper preparedStatementWrapper = new PreparedStatementWrapper();
                preparedStatementWrapper.setStatement(statement);
                vector.addElement(preparedStatementWrapper);
            }
        }
        catch (final SQLException ex) {
            this.handleConnectionFail(connection, ex.getErrorCode());
            ex.printStackTrace();
        }
        return vector;
    }
    
    public void startConnectionMaintainer(final String s) {
        new ConnectionMaintainer(this.conVect, this.getScheduler(), s).run();
    }
    
    public void setScheduler(final Scheduler sch) {
        this.sch = sch;
    }
    
    public Scheduler getScheduler() {
        if (this.sch == null) {
            this.sch = Scheduler.createScheduler("transaction", 1);
            if (!this.sch.isAlive()) {
                this.sch.start();
            }
        }
        return this.sch;
    }
    
    private Connection createConnection(final boolean autoCommit) throws NmsStorageException {
        Connection connection = null;
        try {
            Class.forName(this.driverName);
        }
        catch (final ClassNotFoundException ex) {
            throw new NmsStorageException("The driver name given is not in the classpath." + ex.getMessage(), ex);
        }
        try {
            if (this.userName == null && this.password == null) {
                connection = DriverManager.getConnection(this.url);
            }
            else {
                connection = DriverManager.getConnection(this.url, this.userName, this.password);
            }
            if (this.conAuth != null) {
                try {
                    ((ConnectionAuthenticationInterface)Class.forName(this.conAuth).newInstance()).authenticateConnection(connection);
                }
                catch (final Exception ex2) {
                    throw new NmsStorageException("Exception in executing Connection Authentication Interface " + ex2.getMessage(), ex2);
                }
            }
            if (this.isTransactionEnabled()) {
                connection.setAutoCommit(autoCommit);
            }
            try {
                final String lowerCase = connection.getMetaData().getDatabaseProductName().toLowerCase();
                if (lowerCase.toLowerCase().indexOf("solid") != -1 || lowerCase.toLowerCase().indexOf("flowengine") != -1) {
                    this.issolid = true;
                }
                if (lowerCase.toLowerCase().indexOf("oracle") != -1) {
                    this.isOracle = true;
                }
                if (lowerCase.indexOf("mysql") >= 0) {
                    final Statement statement = connection.createStatement();
                    statement.executeUpdate("set SQL_BIG_TABLES =  1 ");
                    statement.close();
                }
            }
            catch (final Exception ex3) {
                System.err.println("error while executing query set SQL_BIG_TABLES =  1 ");
            }
            this.conVect.addElement(connection);
        }
        catch (final SQLException ex4) {
            try {
                this.handleConnectionFail(connection, ex4.getErrorCode());
            }
            catch (final Exception ex5) {}
            throw new NmsStorageException(ex4.getMessage(), ex4);
        }
        return connection;
    }
    
    public boolean isNestedTransaction() {
        return this.threadVsConn.containsKey(Thread.currentThread());
    }
    
    public int getNestingLevel() {
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(Thread.currentThread());
        return (connectionWrapper == null) ? -1 : connectionWrapper.getNestingLevel();
    }
    
    public void resetNestingLevel(final Thread thread) {
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(thread);
        if (connectionWrapper != null) {
            connectionWrapper.resetNestingLevel();
        }
    }
    
    public void updateNestingLevel(final boolean b) {
        final ConnectionWrapper connectionWrapper = this.threadVsConn.get(Thread.currentThread());
        if (connectionWrapper != null) {
            connectionWrapper.updateNestingLevel(b);
        }
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        final Connection connection = this.getConnection();
        if (connection != null) {
            this.dbmd = connection.getMetaData();
        }
        return this.dbmd;
    }
    
    public DatabaseMetaData getMetaData(final Connection connection) throws SQLException {
        DatabaseMetaData metaData = null;
        if (connection != null) {
            metaData = connection.getMetaData();
        }
        return metaData;
    }
    
    public void disconnect() throws SQLException {
        this.disconnectAll();
    }
    
    public Statement query(final String s, final boolean b) throws NmsStorageException {
        final Connection connection = this.getConnection();
        if (connection == null) {
            ConnectionPool.MISCERR.fail("Connection object is null", null);
            throw new NmsStorageException("Connection object is null", new NullPointerException("Connection object is null"));
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(s);
            if (this.issolid && !this.isInTransaction()) {
                this.commitAllNonTransactionConnections();
            }
        }
        catch (final SQLException ex) {
            final String string = ex.getMessage() + "\n Statement being executed was " + s;
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final SQLException ex2) {}
            this.handleConnectionFail(connection, ex.getErrorCode());
            throw new NmsStorageException(string, ex);
        }
        return statement;
    }
    
    public ResultSet executeQuery(final PreparedStatement preparedStatement) throws NmsStorageException {
        if (preparedStatement == null) {
            throw new NmsStorageException("PreparedStatement is null.", new NullPointerException("PreparedStatement is null"));
        }
        ResultSet executeQuery;
        try {
            executeQuery = preparedStatement.executeQuery();
            if (this.issolid && !this.isInTransaction()) {
                this.commitAllNonTransactionConnections();
            }
        }
        catch (final SQLException ex) {
            this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex.getErrorCode());
            throw new NmsStorageException(ex.getMessage(), ex);
        }
        return executeQuery;
    }
    
    public int executeUpdate(final PreparedStatement preparedStatement) throws NmsStorageException {
        if (preparedStatement == null) {
            return 0;
        }
        int executeUpdate;
        try {
            executeUpdate = preparedStatement.executeUpdate();
            if (this.issolid && !this.isInTransaction()) {
                this.commitAllNonTransactionConnections();
            }
        }
        catch (final SQLException ex) {
            this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex.getErrorCode());
            throw new NmsStorageException(ex.getMessage(), ex);
        }
        return executeUpdate;
    }
    
    public void returnPreparedStatement(final PreparedStatementWrapper preparedStatementWrapper) {
        if (preparedStatementWrapper != null) {
            preparedStatementWrapper.setStatus(false);
            preparedStatementWrapper.setTimeOfConnection(0L);
        }
    }
    
    public PreparedStatement getPreparedStatement(final String s, final boolean b) {
        final Connection connection = this.getConnection();
        PreparedStatement preparedStatement = null;
        if (b) {
            preparedStatement = this.prepareStatement(s);
        }
        else {
            try {
                if (connection != null) {
                    preparedStatement = connection.prepareStatement(s);
                }
            }
            catch (final SQLException ex) {
                this.handleConnectionFail(connection, ex.getErrorCode());
                ConnectionPool.MISCERR.fail("Exception in preparing the Statement " + ex.getMessage(), ex);
            }
        }
        return preparedStatement;
    }
    
    public int getPreparedStatementID(final String s) {
        return this.addPSToPool(s);
    }
    
    public PreparedStatement getPreparedStatement(final String s) {
        return this.getPreparedStatement(s, false);
    }
    
    public void executeTheStatement(final Object o) throws NmsStorageException {
        final Connection connection = this.getConnection();
        if (connection == null || o == null) {
            final String s = "Connection object or the object passed as argument is null";
            ConnectionPool.MISCERR.fail(s, null);
            throw new NmsStorageException(s, new NullPointerException(s));
        }
        if (o instanceof PreparedStatement) {
            final PreparedStatement preparedStatement = (PreparedStatement)o;
            try {
                preparedStatement.executeUpdate();
                return;
            }
            catch (final SQLException ex) {
                try {
                    this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex.getErrorCode());
                }
                catch (final Exception ex2) {}
                throw new NmsStorageException(ex.getMessage(), ex);
            }
        }
        if (o instanceof String) {
            final String s2 = (String)o;
            try {
                final Statement statement = connection.createStatement();
                statement.execute(s2);
                statement.close();
                return;
            }
            catch (final SQLException ex3) {
                try {
                    this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex3.getErrorCode());
                }
                catch (final Exception ex4) {}
                throw new NmsStorageException(ex3.getMessage() + "\n Statement being executed was " + s2, ex3);
            }
        }
        ConnectionPool.MISCERR.fail(" Unknown type received by relational API . was expecting String or a PreparedStatement ", null);
    }
    
    private void executeTheStatements(final Vector vector) throws NmsStorageException {
        if (this.getConnection() == null) {
            ConnectionPool.MISCERR.fail("Connection object is null", null);
            throw new NmsStorageException("Connection object is null", new NullPointerException("Connection object is null"));
        }
        if (vector == null) {
            throw new NmsStorageException("statements are null.", new NullPointerException("Statements are null"));
        }
        for (int i = 0; i < vector.size(); ++i) {
            this.executeTheStatement(vector.elementAt(i));
        }
    }
    
    public void commitAllNonTransactionConnections() throws NmsStorageException {
        try {
            for (int i = this.num_trans; i < this.totalcon; ++i) {
                this.freeConnectionList[i].con.commit();
            }
        }
        catch (final Exception ex) {
            throw new NmsStorageException("Exception occoured on commit all Non Transaction Connections :" + ex.getMessage(), ex);
        }
    }
    
    public void execute(final String s) throws NmsStorageException {
        final Connection connection = this.getConnection();
        if (connection == null) {
            ConnectionPool.MISCERR.fail("Connection object is null", null);
            throw new NmsStorageException("Connection object is null", new NullPointerException("Connection object is null"));
        }
        try {
            final Statement statement = connection.createStatement();
            statement.execute(s);
            statement.close();
        }
        catch (final SQLException ex) {
            this.handleConnectionFail(ConnectionPool.conPool.getConnection(), ex.getErrorCode());
            throw new NmsStorageException(ex.getMessage() + "\n Statement being executed was " + s, ex);
        }
    }
    
    public void setTransactionTimeOut(final int timeOutValue) {
        this.timeOutValue = timeOutValue;
    }
    
    public int getTransactionTimeOut() {
        return this.timeOutValue;
    }
    
    public void setConnectionBreakHandler(final ConnectionBreakHandler connectionBreakHandler) {
        this.connectionBreakHandler = connectionBreakHandler;
    }
    
    private void handleConnectionFail(final Connection connection, final int n) {
        if (this.connectionBreakHandler == null) {
            return;
        }
        if (this.isOracle && n == 17002) {
            this.connectionBreakHandler.handleConnectionFail();
            return;
        }
        boolean closed = false;
        try {
            closed = connection.isClosed();
        }
        catch (final Exception ex) {}
        if (closed) {
            this.connectionBreakHandler.handleConnectionFail();
        }
    }
    
    static {
        ConnectionPool.MISCERR = LogMgr.getLogUser("MISCUSER");
        ConnectionPool.conPool = null;
        ConnectionPool.count = 0;
    }
}
