package com.me.devicemanagement.framework.server.sql;

import java.util.Iterator;
import java.util.ArrayList;

public class ValidateDCNativeSQL
{
    private static ValidateDCNativeSQL objValidateDCNativeSQL;
    
    public static ValidateDCNativeSQL getInstance() {
        if (ValidateDCNativeSQL.objValidateDCNativeSQL == null) {
            ValidateDCNativeSQL.objValidateDCNativeSQL = new ValidateDCNativeSQL();
        }
        return ValidateDCNativeSQL.objValidateDCNativeSQL;
    }
    
    public void validateSQLFORAttribute(final ArrayList arrDBNames) {
        final Iterator iterator = arrDBNames.iterator();
        while (iterator.hasNext()) {
            final String sDBName = String.valueOf(iterator.next());
            switch (DBNames.toDBName(sDBName)) {
                case mysql: {
                    continue;
                }
                case mssql: {
                    continue;
                }
                case postgres: {
                    continue;
                }
                case common: {
                    continue;
                }
                case saspg: {
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Given DB Type is not matched with Available DB Types. Please check forsql attribute.");
                }
            }
        }
    }
    
    static {
        ValidateDCNativeSQL.objValidateDCNativeSQL = null;
    }
    
    public enum DBNames
    {
        mysql, 
        mssql, 
        postgres, 
        common, 
        saspg;
        
        public static DBNames toDBName(final String sDBName) {
            return valueOf(sDBName);
        }
    }
}
