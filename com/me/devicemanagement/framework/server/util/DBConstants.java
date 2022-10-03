package com.me.devicemanagement.framework.server.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DBConstants
{
    public static final Map<Integer, String> MAPPED_DB_NAME;
    public static final String MSSQL = "mssql";
    public static final String MYSQL = "mysql";
    public static final String POSTGRES = "postgres";
    public static final String READ_ONLY = "READ_ONLY";
    public static final String DEFAULT_TYPE = "DEFAULT";
    
    public static String getDBNameByDBType(final Integer db_type) {
        return DBConstants.MAPPED_DB_NAME.get(db_type);
    }
    
    public static Integer getDBTypeByDBName(final String db_name) {
        return getValueForKey(DBConstants.MAPPED_DB_NAME, db_name);
    }
    
    public static Integer getValueForKey(final Map<Integer, String> map, final String value) {
        if (map.containsValue(value)) {
            for (final Integer key : map.keySet()) {
                if (map.get(key).equals(value)) {
                    return key;
                }
            }
        }
        return null;
    }
    
    static {
        (MAPPED_DB_NAME = new HashMap<Integer, String>()).put(1, "mysql");
        DBConstants.MAPPED_DB_NAME.put(2, "mssql");
        DBConstants.MAPPED_DB_NAME.put(3, "postgres");
    }
}
