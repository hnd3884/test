package com.adventnet.taskengine.internal;

import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.DynamicValueHandler;

public class SchedulerDatetimeValueHandler implements DynamicValueHandler
{
    private static final Logger LOGGER;
    
    public Object getColumnValue(final String tableName, final String columnName, final Properties params, final String xmlAttrValue) {
        if (xmlAttrValue == null || xmlAttrValue == "") {
            return null;
        }
        if (xmlAttrValue.indexOf("-") == 4) {
            return xmlAttrValue;
        }
        final long value = Long.parseLong(xmlAttrValue);
        if (value == -1L) {
            return null;
        }
        return new Timestamp(value);
    }
    
    public String getAttributeValue(final String tableName, final String columnName, final Properties params, final Object columnValue) {
        if (columnValue instanceof Date) {
            return ((Date)columnValue).getTime() + "";
        }
        SchedulerDatetimeValueHandler.LOGGER.log(Level.WARNING, "Unknown value type received [{0}] for DATE/TIME column, hence replacing it with NULL", columnValue);
        return null;
    }
    
    public void set(final Object obj) {
    }
    
    public DataObject get() {
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(SchedulerDatetimeValueHandler.class.getName());
    }
}
