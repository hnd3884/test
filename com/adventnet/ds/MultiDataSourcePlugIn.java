package com.adventnet.ds;

import java.util.Iterator;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Map;
import com.zoho.cp.WrappedTxMgr;
import com.adventnet.cp.WrappedDataSource;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.logging.Logger;
import javax.transaction.TransactionManager;
import javax.sql.DataSource;
import java.util.Properties;

public class MultiDataSourcePlugIn extends DefaultDataSourcePlugIn
{
    private Properties dbProps;
    private DataSource wdatasource;
    private TransactionManager wtransactionmanager;
    private static final Logger LOGGER;
    
    public MultiDataSourcePlugIn() {
        this.dbProps = new Properties();
    }
    
    @Override
    public void initialize(final Properties props) throws Exception {
        if (Boolean.getBoolean("development.mode")) {
            if (!DBMigrationUtil.isDBMigrationRunning()) {
                this.wdatasource = new WrappedDataSource();
                this.wtransactionmanager = (TransactionManager)new WrappedTxMgr();
                this.dbProps.putAll(props);
                this.dbProps.setProperty(props.getProperty("DSAdapter") + ".url", props.getProperty("url"));
                this.dbProps.setProperty(props.getProperty("DSAdapter") + ".username", props.getProperty("username"));
                this.dbProps.setProperty(props.getProperty("DSAdapter") + ".password", props.getProperty("password"));
                this.dbProps.setProperty(props.getProperty("DSAdapter") + ".drivername", props.getProperty("drivername"));
                this.dbProps.setProperty(props.getProperty("DSAdapter") + ".exceptionsorterclassname", props.getProperty("exceptionsorterclassname"));
                this.dbProps.setProperty(props.getProperty("DSAdapter") + ".aborthandlerclassname", props.getProperty("aborthandlerclassname"));
                for (final String db : PersistenceInitializer.getDatabases()) {
                    final Properties specificDBProps = new Properties();
                    specificDBProps.putAll(this.dbProps);
                    specificDBProps.putAll(this.loadProperties(db));
                    super.initialize(specificDBProps);
                    ((WrappedDataSource)this.wdatasource).addDataSource(super.getDataSource());
                    ((WrappedTxMgr)this.wtransactionmanager).addTransactionManager(super.getTxManager());
                }
                this.loadProperties(this.dbProps.getProperty("DSAdapter"));
                super.initialize(this.dbProps);
            }
            else {
                super.initialize(props);
            }
        }
        else {
            MultiDataSourcePlugIn.LOGGER.log(Level.SEVERE, "Development mode should be set while using MWSR");
            ConsoleOut.println("Development mode should be set while using MWSR");
            System.exit(1);
        }
    }
    
    @Override
    public String getDataSourcePlugInImplClass() throws Exception {
        return "com.adventnet.ds.MultiDataSourcePlugIn";
    }
    
    @Override
    public DataSource getDataSource() throws Exception {
        if (!Boolean.getBoolean("development.mode")) {
            MultiDataSourcePlugIn.LOGGER.log(Level.SEVERE, "Development mode should be set while using MWSR");
            ConsoleOut.println("Development mode should be set while using MWSR");
            System.exit(1);
            return this.wdatasource;
        }
        if (!DBMigrationUtil.isDBMigrationRunning()) {
            return this.wdatasource;
        }
        return super.getDataSource();
    }
    
    @Override
    public TransactionManager getTxManager() throws Exception {
        if (!Boolean.getBoolean("development.mode")) {
            MultiDataSourcePlugIn.LOGGER.log(Level.SEVERE, "Development mode should be set while using MWSR");
            ConsoleOut.println("Development mode should be set while using MWSR");
            System.exit(1);
            return this.wtransactionmanager;
        }
        if (!DBMigrationUtil.isDBMigrationRunning()) {
            return this.wtransactionmanager;
        }
        return super.getTxManager();
    }
    
    private Properties loadProperties(final String dbName) {
        final Properties dbProps = new Properties();
        dbProps.setProperty("url", this.dbProps.getProperty(dbName + ".url"));
        dbProps.setProperty("username", this.dbProps.getProperty(dbName + ".username"));
        dbProps.setProperty("password", this.dbProps.getProperty(dbName + ".password", ""));
        dbProps.setProperty("drivername", this.dbProps.getProperty(dbName + ".drivername"));
        dbProps.setProperty("exceptionsorterclassname", this.dbProps.getProperty(dbName + ".exceptionsorterclassname"));
        return dbProps;
    }
    
    static {
        LOGGER = Logger.getLogger(MultiDataSourcePlugIn.class.getName());
    }
}
