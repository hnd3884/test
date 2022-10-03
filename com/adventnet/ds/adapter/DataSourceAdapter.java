package com.adventnet.ds.adapter;

import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import java.util.Properties;

public interface DataSourceAdapter
{
    void initialize(final Properties p0);
    
    DataSet executeQuery(final MDSContext p0, final Query p1) throws DataSourceException;
    
    void initForExecution(final MDSContext p0) throws DataSourceException;
    
    void cleanUp(final MDSContext p0) throws DataSourceException;
    
    String getName();
}
