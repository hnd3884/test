package com.adventnet.ds.adapter.mds;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.ds.adapter.DataSourceAdapter;
import com.adventnet.ds.DataSourceManager;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Properties;
import javax.transaction.TransactionManager;
import javax.sql.DataSource;
import java.util.logging.Logger;
import com.adventnet.ds.DefaultDataSourcePlugIn;

public class MDSDataSourcePlugIn extends DefaultDataSourcePlugIn
{
    private static final Logger LOGGER;
    DataSource dataSource;
    TransactionManager transactionManager;
    DataSource mdsDataSource;
    
    public MDSDataSourcePlugIn() {
        this.dataSource = null;
        this.transactionManager = null;
        this.mdsDataSource = null;
    }
    
    @Override
    public String getDataSourcePlugInImplClass() throws Exception {
        return "com.adventnet.ds.DefaultDataSourcePlugIn";
    }
    
    @Override
    public void initialize(final Properties props) throws Exception {
        try {
            super.initialize(props);
            this.dataSource = super.getDataSource();
            String adapterType = props.getProperty("DefaultDSAdapter");
            if (adapterType == null || adapterType.equals("")) {
                adapterType = PersistenceInitializer.getConfigurationValue("DSAdapter");
            }
            final Properties dbAdapterProps = PersistenceInitializer.getConfigurationProps(adapterType);
            dbAdapterProps.setProperty("DSName", "default");
            DBAdapter dbadapter = (DBAdapter)DataSourceManager.getDSAdapter("default");
            if (dbadapter == null) {
                dbadapter = PersistenceInitializer.createDBAdapter(dbAdapterProps);
            }
            this.transactionManager = super.getTxManager();
            DataSourceManager.addDataSource("default", this.dataSource, dbadapter);
            dbAdapterProps.putAll(props);
            dbadapter.initialize(dbAdapterProps);
        }
        catch (final Exception e) {
            MDSDataSourcePlugIn.LOGGER.log(Level.WARNING, "Exception occured while instantiating the default datasource and dbadapter.");
            throw new RuntimeException(e);
        }
        this.mdsDataSource = new MDSDataSource();
    }
    
    @Override
    public DataSource getDataSource() throws Exception {
        return this.mdsDataSource;
    }
    
    @Override
    public TransactionManager getTxManager() throws Exception {
        return this.transactionManager;
    }
    
    static {
        LOGGER = Logger.getLogger(MDSDataSourcePlugIn.class.getName());
    }
}
