package com.adventnet.swissqlapi.sql;

import java.util.Enumeration;
import java.util.Hashtable;

public class SQLCache
{
    private static Hashtable SQLTable;
    private static int maxCacheSize;
    private static boolean cacheToBePersisted;
    
    public static void putConvertedSQL(final String gvnSQL, final String cnvtSQL) {
        if (SQLCache.SQLTable.size() >= SQLCache.maxCacheSize) {
            final Enumeration e = SQLCache.SQLTable.keys();
            if (e.hasMoreElements()) {
                SQLCache.SQLTable.remove(e.nextElement());
            }
        }
        SQLCache.SQLTable.put(gvnSQL, cnvtSQL);
    }
    
    public static String getConvertedSQL(final String gvnSQL) {
        try {
            return SQLCache.SQLTable.get(gvnSQL);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static void setMaxCacheSize(final int size) {
        SQLCache.maxCacheSize = size;
    }
    
    public static int getMaxCacheSize() {
        return SQLCache.maxCacheSize;
    }
    
    public static void persistSQLCache(final boolean persist) {
        SQLCache.cacheToBePersisted = persist;
    }
    
    static {
        SQLCache.SQLTable = new Hashtable();
        SQLCache.maxCacheSize = 1000;
        SQLCache.cacheToBePersisted = false;
    }
}
