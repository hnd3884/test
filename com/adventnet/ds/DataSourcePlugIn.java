package com.adventnet.ds;

import javax.transaction.TransactionManager;
import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourcePlugIn
{
    void initialize(final Properties p0) throws Exception;
    
    DataSource getDataSource() throws Exception;
    
    TransactionManager getTxManager() throws Exception;
    
    String getDataSourcePlugInImplClass() throws Exception;
    
    void flush() throws Exception;
    
    void setMaxSize(final int p0) throws Exception;
    
    int getMaxSize() throws Exception;
    
    void setMinSize(final int p0) throws Exception;
    
    int getMinSize() throws Exception;
    
    int getInUseConnectionCount() throws Exception;
    
    long getAvailableConnectionCount() throws Exception;
    
    Properties getInitProperties() throws Exception;
    
    void abortAllConnections();
    
    void abortAllConnections(final boolean p0);
}
