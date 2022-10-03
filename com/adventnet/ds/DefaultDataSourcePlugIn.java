package com.adventnet.ds;

import java.util.logging.Level;
import com.zoho.cp.TxDataSource;
import com.zoho.cp.MultiTxMgrTxDataSource;
import com.zoho.cp.TxMgr;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.cp.IdleConnectionRemover;
import com.zoho.cp.ConnectionCreator;
import com.adventnet.persistence.xml.ConfigurationPopulationException;
import com.zoho.metrics.Metrics;
import com.zoho.cp.ConnectionPool;
import javax.transaction.TransactionManager;
import java.util.Properties;
import javax.sql.DataSource;
import java.util.logging.Logger;

public class DefaultDataSourcePlugIn implements DataSourcePlugIn
{
    private static final Logger LOGGER;
    private DataSource dataSource;
    private Properties initProperties;
    private TransactionManager transactionManager;
    private ConnectionPool connPool;
    private Metrics<ConnectionPool.ConnectionPoolStats> stats;
    
    public DefaultDataSourcePlugIn() {
        this.dataSource = null;
        this.transactionManager = null;
    }
    
    @Override
    public String getDataSourcePlugInImplClass() throws Exception {
        return "com.adventnet.ds.DefaultDataSourcePlugIn";
    }
    
    @Override
    public void initialize(final Properties props) throws Exception {
        if (props == null) {
            throw new NullPointerException("Properties are required to initialize DefaultDataSourcePlugIn");
        }
        this.initProperties = props;
        if (props.getProperty("url") == null && props.getProperty("username") == null && props.getProperty("url").indexOf("user") == -1) {
            throw new ConfigurationPopulationException("DB Params are not populated");
        }
        final ConnectionCreator creator = new ConnectionCreator();
        creator.initialize(props);
        if (this.connPool != null) {
            this.connPool.setActive(false);
        }
        IdleConnectionRemover.register(this.connPool = this.createConnectionPool(props, creator));
        this.dataSource = this.createDataSource();
        final String configValue = PersistenceInitializer.getConfigurationValue("TransactionTimeOut");
        final int transTimeOut = Integer.parseInt((configValue != null && !configValue.equals("")) ? configValue : "300");
        this.setTxManager();
        ((TxMgr)this.transactionManager).setDefaultTxnTimeout(transTimeOut);
    }
    
    protected DataSource createDataSource() {
        if (PersistenceInitializer.getConfigurationValue("EnableMWSR") != null && PersistenceInitializer.getConfigurationValue("EnableMWSR").equals("true")) {
            return (DataSource)new MultiTxMgrTxDataSource(this.connPool);
        }
        return (DataSource)new TxDataSource(this.connPool);
    }
    
    private ConnectionPool createConnectionPool(final Properties props, final ConnectionCreator creator) throws Exception {
        Integer maxSize = new Integer(props.getProperty("maxsize"));
        if (maxSize == null || maxSize < 1) {
            maxSize = new Integer(5);
        }
        final int blockingTimeout = Integer.parseInt(props.getProperty("blockingtimeout", "30").trim());
        final Integer idleTimeout = Integer.parseInt(props.getProperty("idletimeout", "1800").trim());
        final ConnectionPool connPool = new ConnectionPool((int)maxSize, creator, blockingTimeout, (int)idleTimeout);
        return connPool;
    }
    
    @Override
    public DataSource getDataSource() throws Exception {
        DefaultDataSourcePlugIn.LOGGER.log(Level.INFO, "DataSource" + this.dataSource);
        return this.dataSource;
    }
    
    public void setTxManager() throws Exception {
        if (this.dataSource instanceof TxDataSource) {
            this.transactionManager = ((TxDataSource)this.dataSource).getTxManager();
            return;
        }
        throw new IllegalArgumentException("Unknown TransactionManager implementation found :(");
    }
    
    @Override
    public TransactionManager getTxManager() throws Exception {
        return this.transactionManager;
    }
    
    @Override
    public void flush() throws Exception {
        this.connPool.flush();
    }
    
    @Override
    public void setMaxSize(final int maxConnections) throws Exception {
        this.connPool.setMaxConnections(maxConnections);
    }
    
    @Override
    public int getMaxSize() throws Exception {
        this.stats = (Metrics<ConnectionPool.ConnectionPoolStats>)this.connPool.getStats();
        return this.stats.get((Enum)ConnectionPool.ConnectionPoolStats.max_connections);
    }
    
    @Override
    public void setMinSize(final int arg0) throws Exception {
        throw new Exception("setMinSize not supported");
    }
    
    @Override
    public int getMinSize() throws Exception {
        return 0;
    }
    
    @Override
    public long getAvailableConnectionCount() throws Exception {
        return this.stats.get((Enum)ConnectionPool.ConnectionPoolStats.free_connections);
    }
    
    @Override
    public int getInUseConnectionCount() throws Exception {
        this.stats = (Metrics<ConnectionPool.ConnectionPoolStats>)this.connPool.getStats();
        return this.stats.get((Enum)ConnectionPool.ConnectionPoolStats.used_connections);
    }
    
    @Override
    public Properties getInitProperties() throws Exception {
        return this.initProperties;
    }
    
    @Override
    public void abortAllConnections() {
        this.connPool.abortAllConnections();
    }
    
    @Override
    public void abortAllConnections(final boolean forceAbort) {
        this.connPool.abortAllConnections(forceAbort);
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultDataSourcePlugIn.class.getName());
    }
}
