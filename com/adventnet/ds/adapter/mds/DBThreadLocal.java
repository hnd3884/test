package com.adventnet.ds.adapter.mds;

import java.util.HashMap;

public class DBThreadLocal
{
    private static ThreadLocal threadLocal;
    private static final String DSNAME = "dsName";
    private static final String DBNAME = "dbName";
    
    public static void set(final HashMap hashMap) {
        DBThreadLocal.threadLocal.set(hashMap);
    }
    
    public static void set(final String dataSourceName) {
        if (dataSourceName != null) {
            final HashMap hashMap = new HashMap(1);
            hashMap.put("dsName", dataSourceName);
            DBThreadLocal.threadLocal.set(hashMap);
            return;
        }
        throw new IllegalArgumentException("DataSourceName cannot be null");
    }
    
    public static void set(final String dataSourceName, final String dbName) {
        if (dataSourceName != null) {
            final HashMap hashMap = new HashMap(1);
            hashMap.put("dsName", dataSourceName);
            if (dbName != null) {
                hashMap.put("dbName", dbName);
            }
            DBThreadLocal.threadLocal.set(hashMap);
            return;
        }
        throw new IllegalArgumentException("DataSourceName cannot be null");
    }
    
    public static HashMap get() {
        return DBThreadLocal.threadLocal.get();
    }
    
    static {
        DBThreadLocal.threadLocal = new ThreadLocal();
    }
}
