package com.me.devicemanagement.framework.server.resource;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class ResourceDataPopulator
{
    protected static final Integer RESOURCE_TABLE_LOCK;
    protected static Logger logger;
    protected static String sourceClass;
    
    public static Row renameResource(final Long resourceId, final String newName) throws SyMException {
        return renameResource(resourceId, newName, Boolean.TRUE);
    }
    
    public static Row renameResource(final Long resourceId, final String newName, final Boolean addEventLog) throws SyMException {
        return renameResource(resourceId, newName, null, addEventLog);
    }
    
    public static Row renameResource(final Long resourceId, final String newName, String newDomainName, final Boolean addEventLog) throws SyMException {
        final String sourceMethod = "renameResource";
        Row resultRow = null;
        if (resourceId == null || newName == null) {
            return null;
        }
        try {
            Row resRow = new Row("Resource");
            resRow.set("RESOURCE_ID", (Object)resourceId);
            DataObject resultDO = SyMUtil.getPersistence().get("Resource", resRow);
            if (!resultDO.isEmpty()) {
                resRow = resultDO.getRow("Resource");
                newDomainName = (String)((newDomainName == null) ? resRow.get("DOMAIN_NETBIOS_NAME") : newDomainName);
                SyMLogger.info(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Renaming resource name: " + newName + "\t domain: " + newDomainName + "\t for resource row: " + resRow);
                if (newName != null) {
                    resRow.set("NAME", (Object)newName);
                }
                if (newDomainName != null) {
                    resRow.set("DOMAIN_NETBIOS_NAME", (Object)newDomainName);
                }
                resultDO.updateRow(resRow);
                synchronized (ResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                    resultDO = SyMUtil.getPersistence().update(resultDO);
                }
                resultRow = resultDO.getRow("Resource");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while renaming resource with resourceId: " + resourceId + "\t new name: " + newName, ex);
            throw new SyMException(1001, ex);
        }
        return resultRow;
    }
    
    public static Row renameResourceUniqueValue(final Long resourceID, final String uniqueValue) throws SyMException {
        final String sourceMethod = "sourceMethod";
        Row resultRow = null;
        if (resourceID == null || uniqueValue == null || uniqueValue.trim().isEmpty()) {
            return null;
        }
        try {
            Row resRow = new Row("Resource");
            resRow.set("RESOURCE_ID", (Object)resourceID);
            DataObject resultDO = SyMUtil.getPersistence().get("Resource", resRow);
            if (!resultDO.isEmpty()) {
                resRow = resultDO.getRow("Resource");
                SyMLogger.info(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Renaming resource name: " + uniqueValue + "\t for resource row: " + resRow);
                if (uniqueValue != null) {
                    resRow.set("UNIQUE_VALUE", (Object)uniqueValue);
                }
                resultDO.updateRow(resRow);
                synchronized (ResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                    resultDO = SyMUtil.getPersistence().update(resultDO);
                }
                resultRow = resultDO.getRow("Resource");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while renaming resource unique value with resourceId: " + resourceID + "\t UniqueValue: " + uniqueValue, ex);
            throw new SyMException(1001, ex);
        }
        return resultRow;
    }
    
    public static DataObject addOrUpdateResource(final Properties resProps) throws SyMException {
        final String sourceMethod = "addOrUpdateResource";
        DataObject resourceDO = null;
        try {
            final Row resourceRow = constructResourceRow(resProps);
            if (resourceRow == null) {
                final String errMsg = "Can't add/update Resource data in DB. Insufficient data: " + resProps;
                SyMLogger.warning(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, errMsg);
                throw new SyMException(1001, errMsg, null);
            }
            resourceDO = addOrUpdateResourceRow(resourceRow);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details in DB: " + resProps, ex);
            throw new SyMException(1001, ex);
        }
        return resourceDO;
    }
    
    public static Row constructResourceRow(final Properties resProps) throws SyMException {
        final String sourceMethod = "constructResourceRow";
        if (resProps == null || resProps.isEmpty()) {
            return null;
        }
        final Row resRow = new Row("Resource");
        try {
            resRow.set("NAME", ((Hashtable<K, Object>)resProps).get("NAME"));
            resRow.set("RESOURCE_TYPE", (Object)new Integer(Integer.parseInt(resProps.getProperty("RESOURCE_TYPE"))));
            if (resProps.get("DOMAIN_NETBIOS_NAME") != null) {
                resRow.set("DOMAIN_NETBIOS_NAME", ((Hashtable<K, Object>)resProps).get("DOMAIN_NETBIOS_NAME"));
            }
            if (resProps.get("CUSTOMER_ID") != null) {
                resRow.set("CUSTOMER_ID", (Object)((Hashtable<K, Long>)resProps).get("CUSTOMER_ID"));
            }
            if (resProps.get("UNIQUE_VALUE") != null) {
                resRow.set("UNIQUE_VALUE", ((Hashtable<K, Object>)resProps).get("UNIQUE_VALUE"));
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details from AD.", ex);
            throw new SyMException(1001, ex);
        }
        return resRow;
    }
    
    public static DataObject addOrUpdateResourceRow(final Row resourceRow) throws SyMException {
        final String sourceMethod = "addOrUpdateResourceRow";
        final Long crntTime = SyMUtil.getCurrentTime();
        DataObject resourceDO = null;
        try {
            synchronized (ResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                resourceDO = ResourceDataProvider.getResourceDOFromDB(resourceRow);
                if (resourceDO.isEmpty()) {
                    SyMLogger.debug(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Resource details does not exist in DB. Going to add: {0}", new Object[] { resourceRow });
                    resourceRow.set("DB_ADDED_TIME", (Object)crntTime);
                    resourceRow.set("DB_UPDATED_TIME", (Object)crntTime);
                    resourceDO.addRow(resourceRow);
                    resourceDO = SyMUtil.getPersistence().add(resourceDO);
                }
                else {
                    final Row resDBRow = resourceDO.getRow("Resource");
                    SyMLogger.debug(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Resource details already exists in DB. Going to update the data: {0}  Data from DB: {1}", new Object[] { resourceRow, resDBRow });
                    resourceRow.set("RESOURCE_ID", resDBRow.get("RESOURCE_ID"));
                    resourceRow.set("DB_UPDATED_TIME", (Object)crntTime);
                    resourceDO.updateRow(resourceRow);
                    resourceDO = SyMUtil.getPersistence().update(resourceDO);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataPopulator.logger, ResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details in DB: " + resourceRow, ex);
            throw new SyMException(1001, ex);
        }
        return resourceDO;
    }
    
    static {
        RESOURCE_TABLE_LOCK = new Integer(1);
        ResourceDataPopulator.logger = Logger.getLogger("ADLogger");
        ResourceDataPopulator.sourceClass = "DataPopulator";
    }
}
