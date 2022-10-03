package com.me.devicemanagement.framework.server.util;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadOnlyDBUtil extends DBUtilWrapper
{
    private static final Logger LOGGER;
    
    public static ReadOnlyDBUtil getInstance() {
        ReadOnlyDBUtil.LOGGER.log(Level.INFO, "ReadOnlyDBUtil - getInstance");
        return new ReadOnlyDBUtil();
    }
    
    public static int getRecordCount(final SelectQuery selectQuery) throws Exception {
        return getInstance().getRecordCount(selectQuery, true);
    }
    
    public static int getRecordCount(final String tableName, final String columnName, final Criteria criteria) throws Exception {
        int recordCount = 0;
        final SelectQuery query = getInstance().constructSelectQuery(tableName, criteria);
        recordCount = getRecordCount(query, tableName, columnName);
        return recordCount;
    }
    
    public static int getRecordCount(final SelectQuery selectQuery, final String tableName, final String columnName) throws Exception {
        return getInstance().getRecordCount(selectQuery, tableName, columnName, true);
    }
    
    public static Object getValueFromDB(final String tableName, final String criteriaColumnName, final Object criteriaColumnValue, final String returnColumnName) throws Exception {
        return getInstance().getValueFromDB(tableName, criteriaColumnName, criteriaColumnValue, returnColumnName, true);
    }
    
    static {
        LOGGER = Logger.getLogger(ReadOnlyDBUtil.class.getName());
    }
}
