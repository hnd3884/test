package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataObject;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.text.ParseException;
import java.sql.Timestamp;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class ColumnFormatDynamicValueHandler implements DynamicValueHandler
{
    private static final Logger LOGGER;
    
    @Override
    public Object getColumnValue(final String tableName, final String columnName, final Properties configuredDHVP, final String xmlAttribute) throws DynamicValueHandlingException {
        try {
            final TableDefinition tabdef = MetaDataUtil.getTableDefinitionByName(tableName);
            final ColumnDefinition colmdef = tabdef.getColumnDefinitionByName(columnName);
            final String datatype = colmdef.getDataType();
            if (xmlAttribute == null || !configuredDHVP.containsKey("format")) {
                return null;
            }
            if (datatype.equals("DOUBLE") || datatype.equals("FLOAT")) {
                final String pattern = configuredDHVP.getProperty("format");
                final DecimalFormat formatter = new DecimalFormat(pattern);
                return formatter.parse(xmlAttribute);
            }
            if (datatype.equals("DATE")) {
                final String pattern = configuredDHVP.getProperty("format");
                final SimpleDateFormat formatter2 = new SimpleDateFormat(pattern);
                final java.util.Date utildate = formatter2.parse(xmlAttribute);
                final Date sqlDate = new Date(utildate.getTime());
                return sqlDate;
            }
            if (datatype.equals("DATETIME") || datatype.equals("TIMESTAMP")) {
                final String pattern = configuredDHVP.getProperty("format");
                final SimpleDateFormat formatter2 = new SimpleDateFormat(pattern);
                final java.util.Date utildate = formatter2.parse(xmlAttribute);
                final Timestamp timestamp = new Timestamp(utildate.getTime());
                return timestamp;
            }
            return xmlAttribute.toString();
        }
        catch (final ParseException ex) {
            return xmlAttribute;
        }
        catch (final Exception ex2) {
            throw new DynamicValueHandlingException("Exception in getting colume value from formatted value for column  " + columnName + "Exception Message :: ", ex2);
        }
    }
    
    @Override
    public String getAttributeValue(final String tableName, final String columnName, final Properties configuredDHVP, final Object columnValue) throws DynamicValueHandlingException {
        try {
            final TableDefinition tabdef = MetaDataUtil.getTableDefinitionByName(tableName);
            final ColumnDefinition colmdef = tabdef.getColumnDefinitionByName(columnName);
            final String datatype = colmdef.getDataType();
            if (columnValue == null || !configuredDHVP.containsKey("format")) {
                return null;
            }
            if (datatype.equals("DOUBLE") || datatype.equals("FLOAT")) {
                final String pattern = configuredDHVP.getProperty("format");
                final DecimalFormat formatter = new DecimalFormat(pattern);
                return formatter.format(columnValue);
            }
            if (datatype.equals("DATE") || datatype.equals("DATETIME") || datatype.equals("TIMESTAMP")) {
                final String pattern = configuredDHVP.getProperty("format");
                final SimpleDateFormat formatter2 = new SimpleDateFormat(pattern);
                return formatter2.format(columnValue);
            }
            return columnValue.toString();
        }
        catch (final Exception ex) {
            throw new DynamicValueHandlingException("Exception in formatting colume value for column  " + columnName + "Exception Message :: ", ex);
        }
    }
    
    @Override
    public void set(final Object obj) {
    }
    
    @Override
    public DataObject get() {
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ColumnFormatDynamicValueHandler.class.getName());
    }
}
