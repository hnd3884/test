package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Properties;
import java.util.logging.Logger;

public class SBCacheDynamicValueHandler implements DynamicValueHandler
{
    private static final Logger LOGGER;
    
    @Override
    public String getAttributeValue(final String tableName, final String columnName, final Properties configuredDHVP, final Object columnValue) throws DynamicValueHandlingException {
        try {
            final String criteria_column = configuredDHVP.getProperty("criteria-column");
            final String criteria_comparator = configuredDHVP.getProperty("criteria-comparator");
            final String referred_table = configuredDHVP.getProperty("referred-table");
            final String referred_column = configuredDHVP.getProperty("referred-column");
            final String returnObject = null;
            return returnObject;
        }
        catch (final Exception e) {
            throw new DynamicValueHandlingException(e.getMessage(), e);
        }
    }
    
    @Override
    public Object getColumnValue(final String tableName, final String columnName, final Properties configuredDHVP, final String xmlAttribute) throws DynamicValueHandlingException {
        try {
            if (configuredDHVP == null) {
                return null;
            }
            final String criteria_column = configuredDHVP.getProperty("criteria-column");
            final String criteria_comparator = configuredDHVP.getProperty("criteria-comparator");
            final String referred_table = configuredDHVP.getProperty("referred-table");
            final String referred_column = configuredDHVP.getProperty("referred-column");
            Object returnObject = null;
            if (referred_table.equalsIgnoreCase("Module") && referred_column.equalsIgnoreCase("MODULE_ID")) {
                returnObject = PersistenceInitializer.getModuleId(xmlAttribute);
            }
            return returnObject;
        }
        catch (final Exception e) {
            throw new DynamicValueHandlingException(e.getMessage(), e);
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
        LOGGER = Logger.getLogger(SBCacheDynamicValueHandler.class.getName());
    }
}
