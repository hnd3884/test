package com.adventnet.db.persistence.metadata.util;

import java.util.Hashtable;
import com.zoho.conf.Configuration;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.zoho.mickey.api.DataTypeUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.ArrayList;
import com.adventnet.ds.query.AlterTableQuery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.util.logging.Level;
import java.math.BigDecimal;
import com.zoho.conf.AppResources;
import java.io.ByteArrayInputStream;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.util.Set;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.List;
import java.util.Enumeration;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.net.URL;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.extended.PiiValueHandler;
import com.adventnet.db.persistence.metadata.extended.CustomAttributeValidator;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class MetaDataUtil
{
    private static MetaDataInfo metaDataInfo;
    private static final Logger OUT;
    private static Properties templateNameVsTemplate;
    private static String server_home;
    public static final int DBOBJECT_NAMELENGTH;
    public static final int DB_CONSTRAINT_NAME_LENGTH = 60;
    private static HashMap<String, CustomAttributeValidator> customAttrVsValidator;
    private static PiiValueHandler piiValueHandler;
    
    private MetaDataUtil() {
    }
    
    public static void dump() throws MetaDataException {
        MetaDataUtil.metaDataInfo.dump();
    }
    
    @Deprecated
    public static DataDictionary loadDataDictionary(final URL url) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.loadDataDictionary(url);
    }
    
    @Deprecated
    public static DataDictionary loadDataDictionary(final URL url, final boolean createTable) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.loadDataDictionary(url, createTable);
    }
    
    public static void addDataDictionaryConfiguration(final DataDictionary dd) throws MetaDataException {
        MetaDataUtil.metaDataInfo.addDataDictionaryConfiguration(dd);
    }
    
    public static DataDictionary loadDataDictionary(final URL url, final boolean createTables, final String moduleName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.loadDataDictionary(url, createTables, moduleName);
    }
    
    public static void addTableDefinition(final String moduleName, final TableDefinition td) throws MetaDataException {
        MetaDataUtil.metaDataInfo.addTableDefinition(moduleName, td);
    }
    
    public static String getModuleNameOfTable(final String tableName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getModuleNameOfTable(tableName);
    }
    
    @Deprecated
    public static Enumeration getAllTableDefinitions() throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getAllTableDefinitions();
    }
    
    public static List getTableDefinitions() throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getTableDefinitions();
    }
    
    public static List<String> getTableNamesInDefinedOrder() throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getTableNamesInDefinedOrder();
    }
    
    public static TableDefinition getTableDefinitionByName(final String tableName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getTableDefinitionByName(tableName);
    }
    
    public static ForeignKeyDefinition getForeignKeyDefinitionByName(final String fkName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getForeignKeyDefinitionByName(fkName);
    }
    
    public static void removeDataDictionaryConfiguration(final String moduleName) throws MetaDataException {
        MetaDataUtil.metaDataInfo.removeDataDictionaryConfiguration(moduleName);
    }
    
    public static void removeTableDefinition(final String tableName) throws MetaDataException {
        MetaDataUtil.metaDataInfo.removeTableDefinition(tableName);
    }
    
    public static String[] getAllDataDictionarNames() throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getAllDataDictionarNames();
    }
    
    public static Set getAllModuleNames() {
        return MetaDataUtil.metaDataInfo.getAllModuleNames();
    }
    
    public static DataDictionary getDataDictionary(final String moduleName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getDataDictionary(moduleName);
    }
    
    public static List getAllRelatedTableDefinitions(final String tableName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getAllRelatedTableDefinitions(tableName);
    }
    
    public static List getForeignKeys(final String tableName1, final String tableName2) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getForeignKeys(tableName1, tableName2);
    }
    
    public static List getReferringForeignKeyDefinitions(final String tableName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getReferringForeignKeyDefinitions(tableName);
    }
    
    public static List getReferringForeignKeyDefinitions(final String tableName, final String columnName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getReferringForeignKeyDefinitions(tableName, columnName);
    }
    
    @Deprecated
    public static Object convertToCorrespondingDataType(final String value, final String dataType) {
        try {
            return convert(value, dataType);
        }
        catch (final MetaDataException mde) {
            throw new IllegalArgumentException(mde.getMessage());
        }
    }
    
    public static String getDefinedTableName(final String tableName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getDefinedTableName(tableName);
    }
    
    public static String getDefinedTableNameByDisplayName(final String displayName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getDefinedTableNameByDisplayName(displayName);
    }
    
    public static String getDefinedColumnName(final String tableName, final String columnName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getDefinedColumnName(tableName, columnName);
    }
    
    public static void validateArray(final Object[] arrayValue, final int type) throws MetaDataException {
        validateArray(arrayValue, getSQLTypeAsString(type));
    }
    
    public static void validateArray(final Object[] arrayValue, String dataType) {
        if (arrayValue == null) {
            return;
        }
        if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
            final String baseType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            if (baseType != null) {
                dataType = baseType;
            }
        }
        for (int length = arrayValue.length, i = 0; i < length; ++i) {
            try {
                validate(arrayValue[i], dataType);
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }
    
    public static void validate(final Object value, final int type) throws MetaDataException {
        validate(value, getSQLTypeAsString(type));
    }
    
    public static void validate(final Object value, String dataType) throws MetaDataException {
        if (value == null) {
            return;
        }
        Object retVal = value;
        if (retVal instanceof String) {
            retVal = convert(String.valueOf(value), dataType);
        }
        if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
            final String baseType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            if (baseType != null) {
                dataType = baseType;
            }
            else {
                if (DataTypeManager.getDataTypeDefinition(dataType).getMeta() != null) {
                    DataTypeManager.getDataTypeDefinition(dataType).getMeta().validate(retVal);
                    return;
                }
                throw new MetaDataException("Unknown data type: " + dataType);
            }
        }
        if (dataType.equals("SCHAR")) {
            if (!(retVal instanceof String) && !(retVal instanceof Character)) {
                throw new MetaDataException("Illegal value specified for SCHAR column: " + value + " Given : " + value.getClass().getName() + " Expected : " + String.class.getName());
            }
        }
        else if (dataType.equals("CHAR") || dataType.equals("NCHAR")) {
            if (retVal instanceof ByteArrayInputStream || retVal instanceof byte[]) {
                throw new MetaDataException("Illegal value specified for Character column: " + value + " Given : " + value.getClass().getName() + " Expected : " + String.class.getName());
            }
            if (!(retVal instanceof String)) {
                retVal = convert(String.valueOf(value), dataType);
            }
            else {
                if (retVal instanceof String) {
                    return;
                }
                throw new MetaDataException("Illegal value specified for Character column: " + value + " Given : " + value.getClass().getName() + " Expected : " + String.class.getName());
            }
        }
        else if (dataType.equals("TINYINT")) {
            if (!(retVal instanceof Integer)) {
                throw new MetaDataException("Illegal value specified for an INTEGER / TINYINT column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Integer.class.getName());
            }
            if (retVal instanceof Integer && !AppResources.getString("ignore.tinyint.validation", "false").equalsIgnoreCase("true")) {
                final int intValue = (int)retVal;
                if (intValue < 0 || intValue > 127) {
                    throw new MetaDataException("TINYINT value out of range. Given value : " + value);
                }
            }
        }
        else if (dataType.equals("INTEGER")) {
            if (retVal instanceof Long) {
                if ((long)retVal < -2147483648L || (long)retVal > 2147483647L) {
                    throw new MetaDataException("INTEGER value out of range. Given value : " + value);
                }
            }
            else if (!(retVal instanceof Integer)) {
                throw new MetaDataException("Illegal value specified for an INTEGER / TINYINT column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Integer.class.getName());
            }
        }
        else if (dataType.equals("BIGINT")) {
            if (retVal instanceof Long || retVal instanceof Integer) {
                return;
            }
            throw new MetaDataException("Illegal value specified for BIGINT column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Long.class.getName());
        }
        else if (dataType.equals("FLOAT")) {
            if (retVal instanceof Double || retVal instanceof BigDecimal) {
                MetaDataUtil.OUT.log(Level.WARNING, "Illegal value specified for float column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Float.class.getName());
            }
            else if (!(retVal instanceof Float) && !(retVal instanceof Integer) && !(retVal instanceof Long)) {
                throw new MetaDataException("Illegal value specified for float column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Float.class.getName());
            }
        }
        else if (dataType.equals("DOUBLE")) {
            if (retVal instanceof BigDecimal) {
                MetaDataUtil.OUT.log(Level.WARNING, "Illegal value specified for float column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Float.class.getName());
            }
            else if (!(retVal instanceof Double) && !(retVal instanceof Float) && !(retVal instanceof Integer) && !(retVal instanceof Long)) {
                throw new MetaDataException("Illegal value specified for double column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Double.class.getName());
            }
        }
        else if (dataType.equals("DECIMAL")) {
            if (!(retVal instanceof BigDecimal) && !(retVal instanceof Double) && !(retVal instanceof Float) && !(retVal instanceof Integer) && !(retVal instanceof Long)) {
                throw new MetaDataException("Illegal value specified for decimal column: " + value + " Given : " + value.getClass().getName() + " Expected : " + BigDecimal.class.getName());
            }
        }
        else if (dataType.equals("BOOLEAN")) {
            if (retVal instanceof Long) {
                final long val = (long)retVal;
                if (val != 0L && val != 1L) {
                    throw new MetaDataException("Illegal value specified for boolean column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Boolean.class.getName());
                }
                retVal = convert(String.valueOf(value), dataType);
            }
            else if (retVal instanceof Integer) {
                final int val2 = (int)retVal;
                if (val2 != 0 && val2 != 1) {
                    throw new MetaDataException("Illegal value specified for boolean column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Boolean.class.getName());
                }
                retVal = convert(String.valueOf(value), dataType);
            }
            else if (!(retVal instanceof Boolean)) {
                throw new MetaDataException("Illegal value specified for boolean column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Boolean.class.getName());
            }
        }
        else if (dataType.equals("DATE")) {
            if (!(retVal instanceof Date) && (retVal instanceof Time || retVal instanceof Timestamp || !(retVal instanceof java.util.Date))) {
                throw new MetaDataException("Illegal value specified for date column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Date.class.getName());
            }
        }
        else if (dataType.equals("TIME")) {
            if (!(retVal instanceof Time)) {
                throw new MetaDataException("Illegal value specified for time column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Time.class.getName());
            }
        }
        else if (dataType.equals("TIMESTAMP") || dataType.equals("DATETIME")) {
            if (!(retVal instanceof Timestamp)) {
                throw new MetaDataException("Illegal value specified for DATETIME/TIMESTAMP column: " + value + " Given : " + value.getClass().getName() + " Expected : " + Timestamp.class.getName());
            }
        }
        else {
            if (!dataType.equals("BLOB") && !dataType.equals("SBLOB")) {
                throw new MetaDataException("Unknown data type: " + dataType);
            }
            if (!(retVal instanceof ByteArrayInputStream) && !(retVal instanceof byte[])) {
                throw new MetaDataException("Illegal value specified for Blob/SBlob column: " + value + " Given : " + value.getClass().getName() + " Expected : " + ByteArrayInputStream.class.getName());
            }
        }
    }
    
    public static Object[] convertArray(final String[] arrayValue, final int type) {
        return convertArray(arrayValue, getSQLTypeAsString(type));
    }
    
    public static Object[] convertArray(final String[] arrayValue, String dataType) {
        if (arrayValue == null) {
            return null;
        }
        if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
            final String baseType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            if (baseType != null) {
                dataType = baseType;
            }
        }
        final int length = arrayValue.length;
        final Object[] newArrayValue = createDataTypeArray(length, dataType);
        for (int i = 0; i < length; ++i) {
            try {
                newArrayValue[i] = convert(arrayValue[i], dataType);
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        return newArrayValue;
    }
    
    private static Object[] createDataTypeArray(final int length, final String dataType) {
        Object[] value = null;
        final String upperCase = dataType.toUpperCase(Locale.ENGLISH);
        switch (upperCase) {
            case "INTEGER":
            case "TINYINT": {
                value = new Integer[length];
                break;
            }
            case "BIGINT": {
                value = new Long[length];
                break;
            }
            case "CHAR":
            case "NCHAR":
            case "SCHAR":
            case "VARCHAR":
            case "LONGVARCHAR": {
                value = new String[length];
                break;
            }
            case "BOOLEAN": {
                value = new Boolean[length];
                break;
            }
            case "FLOAT": {
                value = new Float[length];
                break;
            }
            case "DOUBLE": {
                value = new Double[length];
                break;
            }
            case "DECIMAL": {
                value = new BigDecimal[length];
                break;
            }
            case "DATE": {
                value = new Date[length];
                break;
            }
            case "TIME": {
                value = new Time[length];
                break;
            }
            case "TIMESTAMP":
            case "DATETIME": {
                value = new Timestamp[length];
                break;
            }
            default: {
                value = new Object[length];
                break;
            }
        }
        return value;
    }
    
    public static Object convert(final String value, final int type) throws MetaDataException {
        return convert(value, getSQLTypeAsString(type));
    }
    
    public static Object convert(final String value, String dataType) throws MetaDataException {
        if (value == null) {
            return null;
        }
        if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
            final String baseType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            if (baseType != null) {
                dataType = baseType;
            }
            else {
                if (DataTypeManager.getDataTypeDefinition(dataType).getMeta() != null) {
                    return DataTypeManager.getDataTypeDefinition(dataType).getMeta().convert(value);
                }
                throw new MetaDataException("Unknown data type: " + dataType);
            }
        }
        Object retVal;
        if (dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR")) {
            retVal = String.valueOf(value);
        }
        else {
            Label_0171: {
                if (!dataType.equals("INTEGER")) {
                    if (!dataType.equals("TINYINT")) {
                        break Label_0171;
                    }
                }
                try {
                    retVal = Integer.decode(value);
                    return retVal;
                }
                catch (final NumberFormatException nfe) {
                    throw new MetaDataException("Illegal value specified for an INTEGER / TINYINT column: " + value, nfe);
                }
            }
            if (dataType.equals("BIGINT")) {
                try {
                    retVal = Long.decode(value);
                    return retVal;
                }
                catch (final NumberFormatException nfe) {
                    throw new MetaDataException("Illegal value specified for a BIGINT column: " + value, nfe);
                }
            }
            if (dataType.equals("DECIMAL")) {
                try {
                    retVal = new BigDecimal(value);
                    return retVal;
                }
                catch (final NumberFormatException nfe) {
                    throw new MetaDataException("Illegal value specified for a DECIMAL column: " + value, nfe);
                }
            }
            if (dataType.equals("BOOLEAN")) {
                if (!value.trim().equalsIgnoreCase("true") && !value.trim().equalsIgnoreCase("false") && !value.trim().equalsIgnoreCase("t") && !value.trim().equalsIgnoreCase("f") && !value.trim().equalsIgnoreCase("0") && !value.trim().equalsIgnoreCase("1") && !value.equals("null")) {
                    throw new MetaDataException("Illegal value specified for a BOOLEAN column: " + value);
                }
                if (value.trim().equalsIgnoreCase("0") || value.trim().equalsIgnoreCase("f")) {
                    retVal = Boolean.FALSE;
                }
                else if (value.trim().equalsIgnoreCase("1") || value.trim().equalsIgnoreCase("t")) {
                    retVal = Boolean.TRUE;
                }
                else {
                    retVal = (value.equals("1") || Boolean.valueOf(value));
                }
            }
            else {
                if (dataType.equals("FLOAT")) {
                    try {
                        retVal = new Float(value);
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a FLOAT column: " + value, nfe);
                    }
                }
                if (dataType.equals("DOUBLE")) {
                    try {
                        retVal = new Double(value);
                        return retVal;
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a DOUBLE column: " + value, nfe);
                    }
                }
                if (dataType.equals("DATE")) {
                    try {
                        retVal = new Date(Long.parseLong(value));
                    }
                    catch (final NumberFormatException nfe) {
                        try {
                            retVal = Date.valueOf(value);
                        }
                        catch (final IllegalArgumentException iae) {
                            MetaDataUtil.OUT.log(Level.FINER, "Invalid default value specified for Date column.\n It has to be either a 'long' value or a yyyy-mm-dd value");
                            throw new MetaDataException("Illegal value specified for a Date column: " + value, nfe);
                        }
                    }
                }
                else if (dataType.equals("TIME")) {
                    try {
                        retVal = new Time(Long.parseLong(value));
                    }
                    catch (final NumberFormatException nfe) {
                        try {
                            retVal = Time.valueOf(value);
                        }
                        catch (final IllegalArgumentException iae) {
                            MetaDataUtil.OUT.log(Level.FINER, "Invalid default value specified for TIME column.\n It has to be either a 'long' value or a hh:mm:ss value");
                            throw new MetaDataException("Illegal value specified for a TIME column: " + value, nfe);
                        }
                    }
                }
                else {
                    if (!dataType.equals("TIMESTAMP")) {
                        if (!dataType.equals("DATETIME")) {
                            if (dataType.equals("BLOB") || dataType.equals("SBLOB")) {
                                retVal = new ByteArrayInputStream(value.getBytes());
                                return retVal;
                            }
                            throw new MetaDataException("Unknown data type: " + dataType);
                        }
                    }
                    try {
                        retVal = new Timestamp(Long.parseLong(value));
                        if (Long.parseLong(value) == 0L) {
                            MetaDataUtil.OUT.log(Level.FINE, "ZERO is an invalid value to timestamp which is internally converted to 0000-00-00 00:00:00 by database. Instead we are converting to '1970-01-01 05:30:00'");
                        }
                        else if (Long.parseLong(value) < 1000L) {
                            throw new MetaDataException("TIMESTAMP value out of range. Given value : " + value);
                        }
                    }
                    catch (final NumberFormatException nfe) {
                        try {
                            retVal = Timestamp.valueOf(value);
                        }
                        catch (final IllegalArgumentException iae) {
                            MetaDataUtil.OUT.log(Level.FINER, "Invalid default value specified for TIMESTAMP column.\n It has to be either a 'long' value or a yyyy-mm-dd hh:mm:ss.fffffffff value");
                            throw new MetaDataException("Illegal value specified for a TIMESTAMP column: " + value, nfe);
                        }
                    }
                    if (dataType.equals("DATETIME")) {
                        ((Timestamp)retVal).setNanos(0);
                    }
                }
            }
        }
        return retVal;
    }
    
    public static Object convertArray(final Object value, final String dataType) throws MetaDataException {
        if (dataType.equals("INTEGER") || dataType.equals("TINYINT")) {
            if (value instanceof int[]) {
                final int[] values = (int[])value;
                final Integer[] temp = new Integer[values.length];
                for (int i = 0; i < values.length; ++i) {
                    try {
                        temp[i] = new Integer(values[i]);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for an INTEGER column: " + values[i], nfe);
                    }
                }
                return temp;
            }
            if (!(value instanceof Integer[])) {
                final Object[] values2 = (Object[])value;
                final Integer[] temp = new Integer[values2.length];
                for (int i = 0; i < values2.length; ++i) {
                    if (values2[i] != null) {
                        try {
                            temp[i] = new Integer(values2[i].toString());
                        }
                        catch (final NumberFormatException nfe) {
                            throw new MetaDataException("Illegal value specified for an INTEGER column: " + values2[i], nfe);
                        }
                    }
                }
                return temp;
            }
        }
        else if (dataType.equals("BIGINT")) {
            if (value instanceof long[]) {
                final long[] values3 = (long[])value;
                final Long[] temp2 = new Long[values3.length];
                for (int i = 0; i < values3.length; ++i) {
                    try {
                        temp2[i] = new Long(values3[i]);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a BIGINT column: " + values3[i], nfe);
                    }
                }
                return temp2;
            }
            if (!(value instanceof Long[])) {
                final Object[] values2 = (Object[])value;
                final Long[] temp2 = new Long[values2.length];
                for (int i = 0; i < values2.length; ++i) {
                    if (values2[i] != null) {
                        try {
                            temp2[i] = new Long(values2[i].toString());
                        }
                        catch (final NumberFormatException nfe) {
                            throw new MetaDataException("Illegal value specified for a BIGINT column: " + values2[i], nfe);
                        }
                    }
                }
                return temp2;
            }
        }
        else if (dataType.equals("FLOAT")) {
            if (value instanceof float[]) {
                final float[] values4 = (float[])value;
                final Float[] temp3 = new Float[values4.length];
                for (int i = 0; i < values4.length; ++i) {
                    try {
                        temp3[i] = new Float(values4[i]);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a FLOAT column: " + values4[i], nfe);
                    }
                }
                return temp3;
            }
            if (!(value instanceof Float[])) {
                final Object[] values2 = (Object[])value;
                final Float[] temp3 = new Float[values2.length];
                for (int i = 0; i < values2.length; ++i) {
                    if (values2[i] != null) {
                        try {
                            temp3[i] = new Float(values2[i].toString());
                        }
                        catch (final NumberFormatException nfe) {
                            throw new MetaDataException("Illegal value specified for a FLOAT column: " + values2[i], nfe);
                        }
                    }
                }
                return temp3;
            }
        }
        else if (dataType.equals("DOUBLE")) {
            if (value instanceof double[]) {
                final double[] values5 = (double[])value;
                final Double[] temp4 = new Double[values5.length];
                for (int i = 0; i < values5.length; ++i) {
                    try {
                        temp4[i] = new Double(values5[i]);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a DOUBLE column: " + values5[i], nfe);
                    }
                }
                return temp4;
            }
            if (!(value instanceof Double[])) {
                final Object[] values2 = (Object[])value;
                final Double[] temp4 = new Double[values2.length];
                for (int i = 0; i < values2.length; ++i) {
                    if (values2[i] != null) {
                        try {
                            temp4[i] = new Double(values2[i].toString());
                        }
                        catch (final NumberFormatException nfe) {
                            throw new MetaDataException("Illegal value specified for a DOUBLE column: " + values2[i], nfe);
                        }
                    }
                }
                return temp4;
            }
        }
        else if (dataType.equals("BOOLEAN")) {
            if (value instanceof boolean[]) {
                final boolean[] values6 = (boolean[])value;
                final Boolean[] temp5 = new Boolean[values6.length];
                for (int i = 0; i < values6.length; ++i) {
                    try {
                        temp5[i] = values6[i];
                    }
                    catch (final NumberFormatException nfe) {
                        throw new MetaDataException("Illegal value specified for a BOOLEAN column: " + values6[i], nfe);
                    }
                }
                return temp5;
            }
            if (!(value instanceof Boolean[])) {
                final Object[] values2 = (Object[])value;
                final Boolean[] temp5 = new Boolean[values2.length];
                for (int i = 0; i < values2.length; ++i) {
                    if (values2[i] != null) {
                        try {
                            temp5[i] = (values2[i].equals("1") || Boolean.valueOf(values2[i].toString()));
                        }
                        catch (final NumberFormatException nfe) {
                            throw new MetaDataException("Illegal value specified for a BOOLEAN column: " + values2[i], nfe);
                        }
                    }
                }
                return temp5;
            }
        }
        else if (dataType.equals("DECIMAL")) {
            if (!(value instanceof BigDecimal[])) {
                final Object[] values2 = (Object[])value;
                final BigDecimal[] temp6 = new BigDecimal[values2.length];
                for (int i = 0; i < values2.length; ++i) {
                    if (values2[i] != null) {
                        try {
                            temp6[i] = new BigDecimal(values2[i].toString());
                        }
                        catch (final NumberFormatException nfe) {
                            throw new MetaDataException("Illegal value specified for a DECIMAL column: " + values2[i], nfe);
                        }
                    }
                }
                return temp6;
            }
        }
        else {
            if (dataType.equals("DATE")) {
                final Object[] values2 = (Object[])value;
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final Date[] temp7 = new Date[values2.length];
                for (int j = 0; j < values2.length; ++j) {
                    if (values2[j] != null) {
                        try {
                            temp7[j] = new Date(sdf.parse(values2[j].toString()).getTime());
                            continue;
                        }
                        catch (final ParseException e) {
                            throw new MetaDataException("Illegal value specified for a DATE column: " + values2[j], e);
                        }
                    }
                    temp7[j] = null;
                }
                return temp7;
            }
            if (dataType.equals("TIME")) {
                final Object[] values2 = (Object[])value;
                final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                final Time[] temp8 = new Time[values2.length];
                for (int j = 0; j < values2.length; ++j) {
                    if (values2[j] != null) {
                        try {
                            temp8[j] = new Time(sdf.parse(values2[j].toString()).getTime());
                            continue;
                        }
                        catch (final ParseException e) {
                            throw new MetaDataException("Illegal value specified for a TIME column: " + values2[j], e);
                        }
                    }
                    temp8[j] = null;
                }
                return temp8;
            }
            if (dataType.equals("TIMESTAMP") || dataType.equals("DATETIME")) {
                final Object[] values2 = (Object[])value;
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                final Timestamp[] temp9 = new Timestamp[values2.length];
                for (int j = 0; j < values2.length; ++j) {
                    if (values2[j] != null) {
                        try {
                            temp9[j] = new Timestamp(sdf.parse(values2[j].toString()).getTime());
                            continue;
                        }
                        catch (final ParseException e) {
                            throw new MetaDataException("Illegal value specified for a TIMESTAMP column: " + values2[j], e);
                        }
                    }
                    temp9[j] = null;
                }
                return temp9;
            }
        }
        return value;
    }
    
    public static void alterTableDefinition(final AlterTableQuery alterTableQuery) throws MetaDataException {
        MetaDataUtil.metaDataInfo.alterTableDefinition(alterTableQuery);
    }
    
    public static String getMasterTableName(final String tableName1, final String tableName2) throws MetaDataException {
        if (MetaDataUtil.metaDataInfo.getReferringTableNames(tableName1).contains(tableName2)) {
            return tableName1;
        }
        if (MetaDataUtil.metaDataInfo.getReferringTableNames(tableName2).contains(tableName1)) {
            return tableName2;
        }
        throw new MetaDataException("No relation found between the TableNames [" + tableName1 + "] and [" + tableName2 + "]");
    }
    
    public static String getSlaveTableName(final String tableName1, final String tableName2) throws MetaDataException {
        if (MetaDataUtil.metaDataInfo.getReferringTableNames(tableName1).contains(tableName2)) {
            return tableName2;
        }
        if (MetaDataUtil.metaDataInfo.getReferringTableNames(tableName2).contains(tableName1)) {
            return tableName1;
        }
        throw new MetaDataException("No relation found between the TableNames [" + tableName1 + "] and [" + tableName2 + "]");
    }
    
    public static List getMasterTableNames(final String tableName) throws MetaDataException {
        final TableDefinition tabDef = MetaDataUtil.metaDataInfo.getTableDefinitionByName(tableName);
        final List fkList = tabDef.getForeignKeyList();
        return getTableNames(fkList, true);
    }
    
    public static List getSlaveTableNames(final String tableName) throws MetaDataException {
        final List fkList = MetaDataUtil.metaDataInfo.getReferringForeignKeyDefinitions(tableName);
        return getTableNames(fkList, false);
    }
    
    private static List getTableNames(final List fkList, final boolean isMaster) throws MetaDataException {
        final List tableNames = new ArrayList();
        for (int i = 0; fkList != null && i < fkList.size(); ++i) {
            final ForeignKeyDefinition fkDef = fkList.get(i);
            String tableName = null;
            if (isMaster) {
                tableName = fkDef.getMasterTableName();
            }
            else {
                tableName = fkDef.getSlaveTableName();
            }
            if (!tableNames.contains(tableName)) {
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }
    
    public static String getTemplate(final String templateName) {
        return MetaDataUtil.templateNameVsTemplate.getProperty(templateName);
    }
    
    public static void loadSchemaTemplates(final String moduleName) throws IOException, MetaDataException {
        final Properties tempTemplates = new Properties();
        final String fileName = MetaDataUtil.server_home + "/conf/" + moduleName + "/" + PersistenceInitializer.getConfigurationValue("DBName") + "/SchemaTemplates.conf";
        final File file = new File(fileName);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                tempTemplates.load(fis);
            }
            finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }
        if (MetaDataUtil.templateNameVsTemplate.size() != 0) {
            for (final String key : ((Hashtable<Object, V>)tempTemplates).keySet()) {
                if (MetaDataUtil.templateNameVsTemplate.getProperty(key) != null) {
                    throw new MetaDataException("Already a template [" + key + "] with this name exists in one of the SchemaTemplates.conf");
                }
            }
        }
        MetaDataUtil.templateNameVsTemplate.putAll(tempTemplates);
    }
    
    public static void validateTableDefinition(final TableDefinition tableDefinition) throws MetaDataException {
        MetaDataUtil.metaDataInfo.validateTableDefinition(tableDefinition);
    }
    
    public static void addTemplateInstance(final String templateTableName, final String instanceId) throws MetaDataException {
        MetaDataUtil.metaDataInfo.addTemplateInstance(templateTableName, instanceId);
    }
    
    public static boolean removeTemplateInstance(final String templateTableName, final String instanceId) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.removeTemplateInstance(templateTableName, instanceId);
    }
    
    public static TemplateMetaHandler getTemplateHandler(final String ddName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getTemplateHandler(ddName);
    }
    
    @Deprecated
    public static int getJavaSQLType(final String dataType) {
        return DataTypeUtil.getJavaSQLType(dataType);
    }
    
    public static void validateAlterTableQuery(final AlterTableQuery alterTableQuery) throws MetaDataException {
        MetaDataUtil.metaDataInfo.validateAlterTableQuery(alterTableQuery);
    }
    
    @Deprecated
    public static void updateCache(final DataObject dataObject) throws DataAccessException {
        MetaDataUtil.OUT.log(Level.INFO, "This method(MetadataUtil.updateCache) doesn't do anything. It is retained to handle only backward compatability in Updatemanager .");
    }
    
    public static List<String> getAllSlaveTableNames(final String tableName) throws MetaDataException, DataAccessException {
        final List<String> tableNames = new ArrayList<String>();
        getAllSlaveTableName(tableName, tableNames);
        return PersistenceUtil.sortTables(tableNames);
    }
    
    private static void getAllSlaveTableName(final String tableName, final List<String> listToAddTableNames) throws MetaDataException {
        final List<String> slaveTableNames = getSlaveTableNames(tableName);
        for (final String slaveName : slaveTableNames) {
            if (!listToAddTableNames.contains(slaveName)) {
                listToAddTableNames.add(slaveName);
            }
        }
        for (final String slaveName : slaveTableNames) {
            getAllSlaveTableName(slaveName, listToAddTableNames);
        }
    }
    
    @Deprecated
    public static String getSQLTypeAsString(final int sqlTypeVal) throws IllegalArgumentException {
        return DataTypeUtil.getSQLTypeAsString(sqlTypeVal);
    }
    
    public static String getAttribute(final String tableName, final String columnName, final String attributeName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getAttribute(tableName, columnName, attributeName);
    }
    
    public static String getAttribute(final String tableName, final String attributeName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getAttribute(tableName, attributeName);
    }
    
    public static String getAttribute(final String attrName) throws MetaDataException {
        return MetaDataUtil.metaDataInfo.getAttribute(attrName);
    }
    
    public static boolean setAttribute(final String tableName, final String columnName, final String attributeName, final String value) throws MetaDataException, IOException, DataAccessException {
        return MetaDataUtil.metaDataInfo.setAttribute(tableName, columnName, attributeName, value);
    }
    
    public static boolean setAttribute(final String tableName, final String attributeName, final String value) throws MetaDataException, IOException, DataAccessException {
        return MetaDataUtil.metaDataInfo.setAttribute(tableName, attributeName, value);
    }
    
    private static void initializeCustomAttributeConfigurations() throws Exception {
        if (MetaDataUtil.piiValueHandler == null) {
            String piiValueHandlerClass = PersistenceInitializer.getConfigurationValue("piivaluehandler");
            if (piiValueHandlerClass == null) {
                piiValueHandlerClass = "com.adventnet.db.persistence.metadata.extended.DefaultPiiValueHandler";
            }
            MetaDataUtil.piiValueHandler = (PiiValueHandler)Class.forName(piiValueHandlerClass).newInstance();
        }
        if (MetaDataUtil.customAttrVsValidator.isEmpty()) {
            final Properties validators = PersistenceInitializer.getProps("CustomAttributeValidator");
            if (validators != null) {
                for (final String extAttr : validators.stringPropertyNames()) {
                    final String handlerName = validators.getProperty(extAttr);
                    if (handlerName != null && !handlerName.isEmpty()) {
                        final CustomAttributeValidator handler = (CustomAttributeValidator)Class.forName(handlerName).newInstance();
                        MetaDataUtil.customAttrVsValidator.put(extAttr, handler);
                    }
                }
            }
            if (MetaDataUtil.customAttrVsValidator.get("defaultvalidator") == null) {
                MetaDataUtil.customAttrVsValidator.put("defaultvalidator", (CustomAttributeValidator)Class.forName("com.adventnet.db.persistence.metadata.extended.DefaultCAValidator").newInstance());
            }
            if (MetaDataUtil.customAttrVsValidator.get("pii") == null) {
                MetaDataUtil.customAttrVsValidator.put("pii", (CustomAttributeValidator)Class.forName("com.adventnet.db.persistence.metadata.extended.PIIValidator").newInstance());
            }
        }
    }
    
    public static HashMap<String, String> getCustomAttributes(final List<URL> listOfExtendedDDFiles, final boolean validate) throws Exception {
        final HashMap<String, String> tempCustomAttributes = new HashMap<String, String>();
        initializeCustomAttributeConfigurations();
        if (listOfExtendedDDFiles != null && !listOfExtendedDDFiles.isEmpty()) {
            addAttribute(listOfExtendedDDFiles, validate, tempCustomAttributes);
        }
        return tempCustomAttributes;
    }
    
    private static void validateAndAddExtendedAttr(final Map<String, String> tempCustomAttributes, final String attrName, final String attrValue) throws MetaDataException {
        final String extAttr = attrName.substring(attrName.lastIndexOf(".") + 1);
        CustomAttributeValidator instance = MetaDataUtil.customAttrVsValidator.get(extAttr);
        if (instance == null) {
            instance = MetaDataUtil.customAttrVsValidator.get("defaultvalidator");
        }
        if (instance.validateStaticAttribute(attrName, tempCustomAttributes.get(attrName), attrValue)) {
            tempCustomAttributes.put(attrName, attrValue);
        }
    }
    
    private static void addAttribute(final List<URL> listOfExtendedDDFiles, final boolean validate, final Map<String, String> tempCustomAttributes) throws IOException, InstantiationException, MetaDataException {
        for (final URL filePath : listOfExtendedDDFiles) {
            final Properties properties = new Properties();
            try (final InputStream is = filePath.openStream()) {
                properties.load(is);
            }
            loadToMap(tempCustomAttributes, properties, validate);
        }
    }
    
    private static void loadToMap(final Map<String, String> tempCustomAttributes, final Properties properties, final boolean validate) throws MetaDataException {
        for (final String key : properties.stringPropertyNames()) {
            if (validate) {
                validateAndAddExtendedAttr(tempCustomAttributes, key, properties.getProperty(key));
            }
            else {
                tempCustomAttributes.put(key, properties.getProperty(key));
            }
        }
    }
    
    public static CustomAttributeValidator getValidator(final String customAttribute) {
        return (MetaDataUtil.customAttrVsValidator.get(customAttribute) != null) ? MetaDataUtil.customAttrVsValidator.get(customAttribute) : MetaDataUtil.customAttrVsValidator.get("defaultvalidator");
    }
    
    public static PiiValueHandler getPiiValueHandler() {
        return MetaDataUtil.piiValueHandler;
    }
    
    public static void loadCustomAttributes(final List<URL> listOfExtendedDDFiles, final boolean validate, final boolean loadDynamicAttributes) throws Exception {
        MetaDataUtil.metaDataInfo.loadCustomAttributes(listOfExtendedDDFiles, validate, loadDynamicAttributes);
    }
    
    public static boolean removeAttribute(final String tableName, final String columnName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        return MetaDataUtil.metaDataInfo.removeAttribute(tableName, columnName, attributeName);
    }
    
    public static boolean removeAttribute(final String tableName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        return MetaDataUtil.metaDataInfo.removeAttribute(tableName, attributeName);
    }
    
    public static void removeCustomAttributeConfigurations() {
        MetaDataUtil.metaDataInfo.removeCustomAttributeConfigurations();
    }
    
    static {
        MetaDataUtil.metaDataInfo = null;
        OUT = Logger.getLogger(MetaDataUtil.class.getName());
        MetaDataUtil.templateNameVsTemplate = new Properties();
        MetaDataUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        DBOBJECT_NAMELENGTH = AppResources.getInteger("dbobject.namelength", Integer.valueOf(50));
        MetaDataUtil.customAttrVsValidator = new HashMap<String, CustomAttributeValidator>();
        MetaDataUtil.piiValueHandler = null;
        final String metaDataClass = (Configuration.getString("LocalMetaData") != null) ? Configuration.getString("LocalMetaData") : AppResources.getProperty("LocalMetaData");
        final boolean throughSqlCreation = Boolean.getBoolean("generate.datadic.diffs");
        if (throughSqlCreation || metaDataClass == null) {
            MetaDataUtil.metaDataInfo = new LocalMetaDataInfo();
        }
        else {
            try {
                MetaDataUtil.metaDataInfo = (MetaDataInfo)Class.forName(metaDataClass).newInstance();
            }
            catch (final Exception exc) {
                MetaDataUtil.OUT.log(Level.SEVERE, "Exception occured while getting the Local interface", exc);
            }
        }
    }
}
