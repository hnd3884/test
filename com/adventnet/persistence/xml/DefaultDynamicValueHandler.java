package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataObject;
import java.util.Date;
import java.util.Properties;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class DefaultDynamicValueHandler implements DynamicValueHandler
{
    Logger logger;
    static Column COLUMN_NAME_COLUMN;
    
    public DefaultDynamicValueHandler() {
        this.logger = Logger.getLogger(DefaultDynamicValueHandler.class.getName());
    }
    
    @Override
    public Object getColumnValue(final String tableName, final String columnName, final Properties configuredDHVP, final String xmlAttribute) throws DynamicValueHandlingException {
        if (configuredDHVP.getProperty("return-value").equals("SYSTIME")) {
            return new Long(System.currentTimeMillis());
        }
        if (configuredDHVP.getProperty("return-value").equals("SYSDATE")) {
            return new Date(System.currentTimeMillis());
        }
        return xmlAttribute;
    }
    
    @Override
    public String getAttributeValue(final String tableName, final String columnName, final Properties configuredDHVP, final Object columnValue) throws DynamicValueHandlingException {
        if (configuredDHVP.getProperty("return-value").equals("SYSTIME")) {
            return "SYSTIME";
        }
        if (configuredDHVP.getProperty("return-value").equals("SYSDATE")) {
            return "SYSDATE";
        }
        return columnValue.toString();
    }
    
    @Override
    public void set(final Object obj) {
    }
    
    @Override
    public DataObject get() {
        return null;
    }
    
    static {
        DefaultDynamicValueHandler.COLUMN_NAME_COLUMN = new Column("ColumnDetails", "COLUMN_NAME");
    }
}
