package com.me.mdm.server.resource;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import com.me.devicemanagement.framework.server.resource.ResourceDataPopulator;

public class MDMResourceDataPopulator extends ResourceDataPopulator
{
    private static final Integer MDM_RESOURCE_TABLE_LOCK;
    
    public static DataObject addOrUpdateResourceForMDMDevice(final Properties resProps) throws SyMException {
        final String sourceMethod = "addOrUpdateResourceForMDMDevice";
        DataObject resourceDO = null;
        try {
            if (resProps == null) {
                final String errMsg = "Insufficient data to create resource";
                throw new SyMException(1001, errMsg, (Throwable)null);
            }
            final String sUDID = ((Hashtable<K, String>)resProps).get("UDID");
            resourceDO = addOrUpdateResourceRowForMDMDevice(resProps, sUDID);
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details in DB: " + resProps, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return resourceDO;
    }
    
    private static DataObject addOrUpdateResourceRowForMDMDevice(final Properties resProps, final String sUDID) throws SyMException {
        final String sourceMethod = "addOrUpdateResourceRowForMDMDevice";
        DataObject resourceDO = null;
        final Row resourceRow = constructResourceRow(resProps);
        if (resourceRow == null) {
            final String errMsg = "Can't add/update Resource data in DB. Insufficient data: " + resProps;
            SyMLogger.warning(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, errMsg);
            throw new SyMException(1001, errMsg, (Throwable)null);
        }
        try {
            synchronized (MDMResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                resourceDO = MDMResourceDataProvider.getResourceDOFromDBForMDMDevice(resProps, sUDID);
                if (resourceDO.isEmpty()) {
                    SyMLogger.debug(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Resource details does not exist in DB. Going to add: {0}", new Object[] { resourceRow });
                    resourceDO.addRow(resourceRow);
                    resourceDO = MDMUtil.getPersistence().add(resourceDO);
                    addMDMResource((Long)resourceRow.get("RESOURCE_ID"));
                }
                else {
                    final Row resDBRow = resourceDO.getRow("Resource");
                    SyMLogger.debug(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Resource details already exists in DB. Going to update the data: {0}  Data from DB: {1}", new Object[] { resourceRow, resDBRow });
                    resourceRow.set("RESOURCE_ID", resDBRow.get("RESOURCE_ID"));
                    resourceDO.updateRow(resourceRow);
                    resourceDO = MDMUtil.getPersistence().update(resourceDO);
                    SyMLogger.warning(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Resource updation is called. Not populating MDMResource " + resourceRow);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details in DB: ", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return resourceDO;
    }
    
    private static void addMDMResource(final Long resourceID) throws DataAccessException {
        final Row mdmResourceRow = new Row("MDMResource");
        mdmResourceRow.set("RESOURCE_ID", (Object)resourceID);
        final DataObject dO = (DataObject)new WritableDataObject();
        dO.addRow(mdmResourceRow);
        MDMUtil.getPersistence().add(dO);
        SyMLogger.info(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, "addMDMResource", "Added Resource ID " + resourceID + "in MDMResource");
    }
    
    public static Row constructResourceRow(final Properties resProps) throws SyMException {
        final String sourceMethod = "constructResourceRow";
        if (resProps == null || resProps.isEmpty()) {
            return null;
        }
        final Row resRow = new Row("Resource");
        try {
            String name = ((Hashtable<K, String>)resProps).get("NAME");
            if (name != null) {
                if (name.length() > 100) {
                    name = name.substring(0, 99);
                }
                resRow.set("NAME", (Object)name);
            }
            resRow.set("RESOURCE_TYPE", (Object)Integer.parseInt(resProps.getProperty("RESOURCE_TYPE")));
            if (resProps.get("DOMAIN_NETBIOS_NAME") != null) {
                resRow.set("DOMAIN_NETBIOS_NAME", ((Hashtable<K, Object>)resProps).get("DOMAIN_NETBIOS_NAME"));
            }
            if (resProps.get("CUSTOMER_ID") != null) {
                resRow.set("CUSTOMER_ID", (Object)((Hashtable<K, Long>)resProps).get("CUSTOMER_ID"));
            }
            if (resProps.get("DB_ADDED_TIME") != null) {
                resRow.set("DB_ADDED_TIME", (Object)((Hashtable<K, Long>)resProps).get("DB_ADDED_TIME"));
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details from AD.", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return resRow;
    }
    
    public static DataObject addOrUpdateResource(final Properties resProps) throws SyMException {
        final String sourceMethod = "addOrUpdateResource";
        DataObject resourceDO = null;
        try {
            final Row resourceRow = constructResourceRow(resProps);
            if (resourceRow == null) {
                final String errMsg = "Can't add/update Resource data in DB. Insufficient data: " + resProps;
                SyMLogger.warning(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, errMsg);
                throw new SyMException(1001, errMsg, (Throwable)null);
            }
            resourceDO = addOrUpdateResourceRow(resourceRow);
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details in DB: " + resProps, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return resourceDO;
    }
    
    public static DataObject addOrUpdateMDMResource(final Properties resProps) throws SyMException {
        final String sourceMethod = "addOrUpdateMDMResource";
        DataObject resourceDO = null;
        try {
            final Row resourceRow = constructResourceRow(resProps);
            if (resourceRow == null) {
                final String errMsg = "Can't add/update MDMResource data in DB. Insufficient data: " + resProps;
                SyMLogger.warning(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, errMsg);
                throw new SyMException(1001, errMsg, (Throwable)null);
            }
            resourceDO = addOrUpdateMDMResourceRow(resourceRow);
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while populating resource details in DB: " + resProps, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return resourceDO;
    }
    
    public static DataObject addOrUpdateMDMResourceRow(final Row mdmResourceRow) throws SyMException {
        final String sourceMethod = "addOrUpdateMDMResource";
        DataObject resourceDO = null;
        resourceDO = addOrUpdateResourceRow(mdmResourceRow);
        synchronized (MDMResourceDataPopulator.MDM_RESOURCE_TABLE_LOCK) {
            try {
                final Long resourceId = (Long)resourceDO.getRow("Resource").get("RESOURCE_ID");
                final Criteria resCriteria = new Criteria(Column.getColumn("MDMResource", "RESOURCE_ID"), (Object)resourceId, 0);
                final DataObject resultDO = SyMUtil.getPersistence().get("MDMResource", resCriteria);
                if (resultDO.isEmpty()) {
                    final Row mdmResRow = new Row("MDMResource");
                    mdmResRow.set("RESOURCE_ID", (Object)resourceId);
                    resultDO.addRow(mdmResRow);
                    SyMUtil.getPersistence().add(resultDO);
                }
            }
            catch (final DataAccessException ex) {
                SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Excption while adding resource in MDMresource", (Throwable)ex);
            }
        }
        return resourceDO;
    }
    
    public static Row renameResource(final Long resourceId, final String newName) throws SyMException {
        final String sourceMethod = "renameResource";
        Row resultRow = null;
        if (resourceId == null || newName == null) {
            return null;
        }
        try {
            Row resRow = new Row("Resource");
            resRow.set("RESOURCE_ID", (Object)resourceId);
            DataObject resultDO = MDMUtil.getPersistence().get("Resource", resRow);
            if (!resultDO.isEmpty()) {
                resRow = resultDO.getRow("Resource");
                SyMLogger.info(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Renaming resource with new name: " + newName + "\t for resource row: " + resRow);
                resRow.set("NAME", (Object)newName);
                resultDO.updateRow(resRow);
                synchronized (MDMResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                    resultDO = MDMUtil.getPersistence().update(resultDO);
                }
                resultRow = resultDO.getRow("Resource");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while renaming resource with resourceId: " + resourceId + "\t new name: " + newName, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return resultRow;
    }
    
    public static Row updateDomainName(final Long resourceId, final String domainName) {
        final String sourceMethod = "updateDomainName";
        Row resultRow = null;
        if (resourceId == null || MDMStringUtils.isEmpty(domainName)) {
            return null;
        }
        try {
            Row resRow = new Row("Resource");
            resRow.set("RESOURCE_ID", (Object)resourceId);
            DataObject resultDO = MDMUtil.getPersistence().get("Resource", resRow);
            if (!resultDO.isEmpty()) {
                resRow = resultDO.getRow("Resource");
                SyMLogger.info(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Updating domain name of resource with new domain name: " + domainName + "\t for resource row: " + resRow);
                resRow.set("DOMAIN_NETBIOS_NAME", (Object)domainName);
                resultDO.updateRow(resRow);
                synchronized (MDMResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                    resultDO = MDMUtil.getPersistence().update(resultDO);
                }
                resultRow = resultDO.getRow("Resource");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, sourceMethod, "Caught exception while updating domain name of resource with resourceId: " + resourceId + "\t new domain name: " + domainName, (Throwable)ex);
        }
        return resultRow;
    }
    
    public static void removeResourceNameForDevicePrivacy(final int ownedBy, final long customerId) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("Resource");
            uQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            uQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            uQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
            final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            uQuery.setCriteria(cOwnedBy.and(cCustomerId));
            uQuery.setUpdateColumn("NAME", (Object)I18N.getMsg("mdm.common.DEFAULT_DEVICE_NAME", new Object[0]));
            synchronized (MDMResourceDataPopulator.RESOURCE_TABLE_LOCK) {
                MDMUtil.getPersistence().update(uQuery);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, "removeResourceNameForDevicePrivacy", "Caught exception in removeResourceNameForDevicePrivacy ", (Throwable)e);
        }
    }
    
    public static void updateDBUpdatedTime(final Long res) {
        try {
            final DataObject dataObject = DataAccess.get("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)res, 0));
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("Resource");
                row.set("DB_UPDATED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
                dataObject.updateRow(row);
                DataAccess.update(dataObject);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MDMResourceDataPopulator.logger, MDMResourceDataPopulator.sourceClass, "updateDBUpdatedTime", "Caught exception in updateDBUpdatedTime ", (Throwable)e);
        }
    }
    
    static {
        MDM_RESOURCE_TABLE_LOCK = new Integer(2);
    }
}
