package com.zoho.mickey.api;

import java.util.Iterator;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.DataTypeManager;

public class DataTypeUtil
{
    public static boolean isUDT(final String dataType) {
        return DataTypeManager.getDataTypeDefinition(dataType) != null && DataTypeManager.getDataTypeDefinition(dataType).getMeta() != null;
    }
    
    public static boolean isEDT(final String dataType) {
        return DataTypeManager.getDataTypeDefinition(dataType) != null && DataTypeManager.getDataTypeDefinition(dataType).getBaseType() != null;
    }
    
    public static boolean isUDT(final int type) {
        return DataTypeManager.getDataTypeDefinition(type) != null && DataTypeManager.getDataTypeDefinition(type).getMeta() != null;
    }
    
    public static boolean isEDT(final int type) {
        return DataTypeManager.getDataTypeDefinition(type) != null && DataTypeManager.getDataTypeDefinition(type).getBaseType() != null;
    }
    
    public static String getSQLTypeAsString(final int sqlTypeVal) throws IllegalArgumentException {
        switch (sqlTypeVal) {
            case 1:
            case 12: {
                return "CHAR";
            }
            case -6: {
                return "TINYINT";
            }
            case 2:
            case 4:
            case 5: {
                return "INTEGER";
            }
            case -5: {
                return "BIGINT";
            }
            case 16: {
                return "BOOLEAN";
            }
            case 6:
            case 7: {
                return "FLOAT";
            }
            case 3: {
                return "DECIMAL";
            }
            case 8: {
                return "DOUBLE";
            }
            case 91: {
                return "DATE";
            }
            case 92: {
                return "TIME";
            }
            case 93: {
                return "TIMESTAMP";
            }
            case 2004: {
                return "BLOB";
            }
            default: {
                if (DataTypeManager.isDataTypeSupported(sqlTypeVal)) {
                    return getUserDefinedDataType(sqlTypeVal);
                }
                throw new IllegalArgumentException("Unknown type received: " + sqlTypeVal);
            }
        }
    }
    
    public static int getJavaSQLType(String dataType) {
        if (DataTypeManager.isDataTypeSupported(dataType)) {
            if (isEDT(dataType)) {
                dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            }
            else {
                if (isUDT(dataType)) {
                    return getUDTSQLType(dataType);
                }
                throw new IllegalArgumentException("Unknown data type :: " + dataType);
            }
        }
        final String upperCase;
        dataType = (upperCase = dataType.toUpperCase(Locale.ENGLISH));
        switch (upperCase) {
            case "INTEGER": {
                return 4;
            }
            case "BIGINT": {
                return -5;
            }
            case "CHAR":
            case "SCHAR":
            case "NCHAR": {
                return 12;
            }
            case "BOOLEAN": {
                return 16;
            }
            case "FLOAT": {
                return 6;
            }
            case "DOUBLE": {
                return 8;
            }
            case "DECIMAL": {
                return 3;
            }
            case "DATE": {
                return 91;
            }
            case "TIME": {
                return 92;
            }
            case "TIMESTAMP":
            case "DATETIME": {
                return 93;
            }
            case "BLOB":
            case "SBLOB": {
                return 2004;
            }
            case "TINYINT": {
                return -6;
            }
            case "CLOB": {
                return 2005;
            }
            case "DCJSON": {
                return 1111;
            }
            default: {
                throw new IllegalArgumentException("Unknown data type:" + dataType);
            }
        }
    }
    
    private static int getUDTSQLType(final String dataType) {
        if (dataType != null) {
            return DataTypeManager.getSQLType(dataType);
        }
        throw new IllegalArgumentException("Datatype cannot be null.");
    }
    
    private static String getUserDefinedDataType(final int sqlType) {
        for (final String dataType : DataTypeManager.getDataTypes()) {
            final int type = DataTypeManager.getSQLType(dataType);
            if (type == sqlType) {
                return dataType;
            }
        }
        return null;
    }
}
