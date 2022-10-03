package com.adventnet.sym.server.mdm.api;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class MdmInvDataProcessor
{
    public static Logger logger;
    private static MdmInvDataProcessor mdmInvDataProcessor;
    
    public static MdmInvDataProcessor getInstance() {
        if (MdmInvDataProcessor.mdmInvDataProcessor == null) {
            MdmInvDataProcessor.mdmInvDataProcessor = new MdmInvDataProcessor();
        }
        return MdmInvDataProcessor.mdmInvDataProcessor;
    }
    
    public HashMap getCompleteDeviceInfo(final long resourceID, HashMap deviceHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor-> getCompleteDeviceInfo() ");
        try {
            deviceHash = this.getDeviceDetails(resourceID, deviceHash);
            deviceHash = this.getNetworkDetails(resourceID, deviceHash);
            deviceHash = this.getSimDetails(resourceID, deviceHash);
            deviceHash = this.getOSDetails(resourceID, deviceHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "detailHash: {0}", deviceHash);
            return deviceHash;
        }
        catch (final Exception exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "getCompleteDeviceInfo : DataAccessException while getting Security Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getCompleteKnoxInfo(final long resourceID, HashMap deviceHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor-> getCompleteDeviceInfo() ");
        try {
            deviceHash = this.getDeviceKnoxDetails(resourceID, deviceHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "detailHash: {0}", deviceHash);
            return deviceHash;
        }
        catch (final Exception exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "getCompleteDeviceInfo : DataAccessException while getting Security Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getDeviceDetails(final long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor:getDeviceDetails()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "NAME"));
            query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
            query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
            query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL"));
            query.addSelectColumn(Column.getColumn("MdModelInfo", "PRODUCT_NAME"));
            query.addSelectColumn(Column.getColumn("MdModelInfo", "MANUFACTURER"));
            query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "BUILD_VERSION"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "DEVICE_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "AVAILABLE_DEVICE_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "BATTERY_LEVEL"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "CELLULAR_TECHNOLOGY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "MEID"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEM_FIRMWARE_VERSION"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_MULTIUSER"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            detailHash.put("NAME", ManagedDeviceHandler.getInstance().getDeviceName(resourceID));
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for Device Detailed info : {0}", detailHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Finished Executing InventoryUtil.getDeviceDetailedInfo()");
            return detailHash;
        }
        catch (final Exception exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "MdInvDataProcessor : DataAccessException while getting Device Detailed info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getNetworkDetails(final long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor:getNetworkDetails()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdNetworkInfo"));
            query.addSelectColumn(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdNetworkInfo", "BLUETOOTH_MAC"));
            query.addSelectColumn(Column.getColumn("MdNetworkInfo", "WIFI_MAC"));
            query.addSelectColumn(Column.getColumn("MdNetworkInfo", "WIFI_IP"));
            query.addSelectColumn(Column.getColumn("MdNetworkInfo", "DATA_ROAMING_ENABLED"));
            query.addSelectColumn(Column.getColumn("MdNetworkInfo", "VOICE_ROAMING_ENABLED"));
            final Criteria criteria = new Criteria(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for Network Detailed Info : {0}", dataObject);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for Network Detailed Info : {0}", detailHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Finished Executing InventoryUtil.getNetworkDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Network Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getSimDetails(final long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor:getSimDetails()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdSIMInfo"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "SIM_ID"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "SLOT"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "ICCID"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "IMEI"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "IMSI"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "PHONE_NUMBER"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "CURRENT_CARRIER_NETWORK"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "CARRIER_SETTING_VERSION"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "CURRENT_MCC"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "CURRENT_MNC"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "SUBSCRIBER_MCC"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "SUBSCRIBER_MNC"));
            query.addSelectColumn(Column.getColumn("MdSIMInfo", "IS_ROAMING"));
            final Criteria criteria = new Criteria(Column.getColumn("MdSIMInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for Sim Detailed Info : {0}", dataObject);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for Sim Detailed Info : {0}", detailHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Finished Executing InventoryUtil.getSimDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Sim Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getOSDetails(final long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getOSDetails()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "BUILD_VERSION"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getOSDetails : {0}", dataObject);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getOSDetails : {0}", detailHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Finished Executing MdInvDataProcessor.getOSDetails()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "InventoryUtil : DataAccessException while getting OS Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getSecurityDetails(final long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getSecurityDetails()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdSecurityInfo"));
            query.addSelectColumn(Column.getColumn("MdSecurityInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdSecurityInfo", "HARDWARE_ENCRYPTION_CAPS"));
            query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_COMPLAINT"));
            query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_COMPLAINT_PROFILES"));
            query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_PRESENT"));
            final Criteria criteria = new Criteria(Column.getColumn("MdSecurityInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getOSDetails : {0}", dataObject);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getOSDetails : {0}", detailHash);
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Finished Executing MdInvDataProcessor.getOSDetails()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public List getAppIDFromResourceID(final long resourceID) {
        List appIdList = null;
        MdmInvDataProcessor.logger.log(Level.FINE, "Inside getAppIDFromResourceID(){0}", resourceID);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria containerCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)0, 0);
            final Criteria criteria = resourceCriteria.and(containerCriteria);
            final DataObject dataObj = MDMUtil.getPersistence().get("MdInstalledAppResourceRel", criteria);
            final Iterator iterator = dataObj.getRows("MdInstalledAppResourceRel");
            appIdList = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long appId = (Long)row.get("APP_ID");
                if (!appIdList.contains(appId)) {
                    appIdList.add(appId);
                }
            }
        }
        catch (final Exception ex) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "Exception occurred in getAppIDFromResourceID()", ex);
        }
        MdmInvDataProcessor.logger.log(Level.FINE, "getAppIDFromResourceID() -> returning appID {0}", appIdList);
        return appIdList;
    }
    
    public List getContainerAppIDFromResourceID(final long resourceID) {
        List appIdList = null;
        MdmInvDataProcessor.logger.log(Level.FINE, "Inside getContainerAppIDFromResourceID(){0}", resourceID);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria containerCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)1, 0);
            final Criteria criteria = resourceCriteria.and(containerCriteria);
            final DataObject dataObj = MDMUtil.getPersistence().get("MdInstalledAppResourceRel", criteria);
            final Iterator iterator = dataObj.getRows("MdInstalledAppResourceRel");
            appIdList = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long appId = (Long)row.get("APP_ID");
                if (!appIdList.contains(appId)) {
                    appIdList.add(appId);
                }
            }
        }
        catch (final Exception ex) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "Exception occurred in getContainerAppIDFromResourceID()", ex);
        }
        MdmInvDataProcessor.logger.log(Level.FINE, "getContainerAppIDFromResourceID() -> returning appID {0}", appIdList);
        return appIdList;
    }
    
    public HashMap getInstalledAppDetailsForResourceID(final long resourceID, HashMap detailHash) throws SyMException, Exception {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getInstalledAppDetails()");
        try {
            final List appID = this.getAppIDFromResourceID(resourceID);
            for (int i = 0; i < appID.size(); ++i) {
                final long applicationID = appID.get(i);
                MdmInvDataProcessor.logger.log(Level.INFO, "MdInvDataProcessor : Data Hash for getInstalledAppDetails: applicationID {0}", applicationID);
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
                query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
                query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
                query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
                query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
                query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
                query.addSelectColumn(Column.getColumn("MdAppDetails", "BUNDLE_SIZE"));
                final Criteria criteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)applicationID, 0);
                if (criteria != null) {
                    query.setCriteria(criteria);
                }
                final DataObject dataObject = MDMUtil.getPersistence().get(query);
                detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
                MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getInstalledAppDetails : {0}", detailHash);
            }
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getInstalledAppDetails : {0}", detailHash);
            return detailHash;
        }
        catch (final Exception exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getInstalledAppDetailsForAppID(final long appID, HashMap detailHash) throws SyMException, Exception {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getInstalledAppDetails()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "BUNDLE_SIZE"));
            final Criteria criteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getInstalledAppDetails : {0}", detailHash);
            return detailHash;
        }
        catch (final Exception exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getIOSRestrictionsDetails(final long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getIOSRestrictionsDetails()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdIOSRestriction"));
        query.addSelectColumn(Column.getColumn("MdIOSRestriction", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdIOSRestriction", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        detailHash = this.getRestrictionsDetails(query, "MdIOSRestriction", "RESOURCE_ID", resourceID, detailHash);
        return detailHash;
    }
    
    public HashMap getAndroidRestrictionsDetails(final Long resourceID, HashMap detailHash, final int scope) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getAndroidRestrictionsDetails()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdRestriction"));
        query.addSelectColumn(Column.getColumn("MdRestriction", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdRestriction", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria cScope = new Criteria(Column.getColumn("MdRestriction", "SCOPE"), (Object)scope, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        detailHash = this.getRestrictionsDetails(query, "MdRestriction", "RESOURCE_ID", resourceID, detailHash);
        return detailHash;
    }
    
    public HashMap getWindowsRestrictionsDetails(final Long resourceID, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getWindowsRestrictionsDetails()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdWindowsDeviceRestriction"));
        query.addSelectColumn(Column.getColumn("MdWindowsDeviceRestriction", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdWindowsDeviceRestriction", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        detailHash = this.getRestrictionsDetails(query, "MdWindowsDeviceRestriction", "RESOURCE_ID", resourceID, detailHash);
        return detailHash;
    }
    
    public HashMap getRestrictionsDetails(final SelectQuery query, final String tableName, final String resourceIdentifier, final Long resourceId, HashMap detailHash) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getRestrictionsDetails()");
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Row row = dataObject.getFirstRow(tableName);
            final List selColumnList = new ArrayList();
            final Iterator mdResItr = row.getColumns().iterator();
            Column selectedCol = null;
            while (mdResItr.hasNext()) {
                selectedCol = Column.getColumn(tableName, (String)mdResItr.next());
                selColumnList.add(selectedCol);
            }
            detailHash = this.getHashFromDO(dataObject, detailHash, true, selColumnList);
            detailHash = this.handleRestrictionDetails(detailHash, resourceIdentifier, resourceId);
            return detailHash;
        }
        catch (final DataAccessException exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "getRestrictionsDetails : DataAccessException while getting ios Device Restriction info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap handleRestrictionDetails(final HashMap detailHash, final String resourceIdentifier, final Long resourceId) {
        Object resVal = null;
        for (final Object key : detailHash.keySet()) {
            if (!key.equals(resourceIdentifier)) {
                resVal = detailHash.get(key);
                if (!(resVal instanceof Integer)) {
                    continue;
                }
                final Integer value = (Integer)resVal;
                if (value == 2) {
                    detailHash.put(key, false);
                }
                else if (value == 1) {
                    detailHash.put(key, true);
                }
                else {
                    if (value == -1) {
                        continue;
                    }
                    detailHash.put(key, value);
                }
            }
            else {
                detailHash.put(resourceIdentifier, resourceId);
            }
        }
        return detailHash;
    }
    
    public List getCertificateIDFromResourceID(final long resourceID) {
        List certIdList = null;
        MdmInvDataProcessor.logger.log(Level.INFO, "Inside getCertificateIDFromResourceID(){0}", resourceID);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdCertificateResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObj = MDMUtil.getPersistence().get("MdCertificateResourceRel", criteria);
            final Iterator iterator = dataObj.getRows("MdCertificateResourceRel");
            certIdList = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long certId = (Long)row.get("CERTIFICATE_ID");
                if (!certIdList.contains(certId)) {
                    certIdList.add(certId);
                }
            }
        }
        catch (final Exception ex) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "Exception occurred in getCertificateIDFromResourceID(){0}", ex);
        }
        MdmInvDataProcessor.logger.log(Level.INFO, "getCertificateIDFromResourceID() -> returning appID {0}", certIdList);
        return certIdList;
    }
    
    public HashMap getCertificateDetailsForCertID(final long certID, HashMap detailHash) throws SyMException, Exception {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getCertificateDetailsForCertID()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCertificateInfo"));
            query.addSelectColumn(Column.getColumn("MdCertificateInfo", "CERTIFICATE_ID"));
            query.addSelectColumn(Column.getColumn("MdCertificateInfo", "CERTIFICATE_NAME"));
            query.addSelectColumn(Column.getColumn("MdCertificateInfo", "IDENTIFY"));
            final Criteria criteria = new Criteria(Column.getColumn("MdCertificateInfo", "CERTIFICATE_ID"), (Object)certID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            detailHash = this.getHashFromDO(dataObject, detailHash, true, query.getSelectColumns());
            MdmInvDataProcessor.logger.log(Level.FINE, "MdInvDataProcessor : Data Hash for getInstalledAppDetails : {0}", detailHash);
            return detailHash;
        }
        catch (final Exception exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "MdInvDataProcessor : DataAccessException while getting Security Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    private HashMap getHashFromDO(final DataObject dataObject, final HashMap detailHash, final boolean nullAllowed, final List selColumnList) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getHashFromDO()");
        try {
            final List tableList = dataObject.getTableNames();
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Row row = dataObject.getFirstRow(tableName);
                final List columnList = this.getSelectedColumnList(selColumnList, tableName);
                for (int j = 0; j < columnList.size(); ++j) {
                    final String columnName = columnList.get(j);
                    final Object obj = row.get(columnName);
                    if (obj != null) {
                        detailHash.put(columnName, obj);
                    }
                    else if (nullAllowed) {
                        detailHash.put(columnName, "--");
                    }
                }
            }
            MdmInvDataProcessor.logger.log(Level.FINE, "Finished Exwcuting InventoryUtil.getHashFromDO()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        catch (final Exception exp2) {
            MdmInvDataProcessor.logger.log(Level.WARNING, "Exception while creating hash from dataObject...", exp2);
            throw new SyMException(1002, (Throwable)exp2);
        }
    }
    
    private List getSelectedColumnList(final List selColumnList, final String tableName) throws SyMException {
        MdmInvDataProcessor.logger.log(Level.FINE, "Entered into MdInvDataProcessor.getSelectedColumn()");
        final List columnList = new ArrayList();
        for (int j = 0; j < selColumnList.size(); ++j) {
            final Column selColumn = selColumnList.get(j);
            if (selColumn.getTableAlias().equalsIgnoreCase(tableName)) {
                columnList.add(selColumn.getColumnName());
            }
        }
        MdmInvDataProcessor.logger.log(Level.INFO, "Selected column list returning from  MdInvDataProcessor.getSelectedColumn() is{0}", columnList.toString());
        return columnList;
    }
    
    public HashMap getAppsInstalledInContainer(final Long resourceId, final HashMap deviceHash) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdInstalledAppResourceRel"));
        final Join appJoin = new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
        sQuery.addJoin(appJoin);
        sQuery.addSelectColumn(new Column("MdAppDetails", "*"));
        sQuery.setCriteria(new Criteria(new Column("MdInstalledAppResourceRel", "SCOPE"), (Object)1, 0));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        final HashMap<Integer, HashMap> appsInstalledInKnox = new HashMap<Integer, HashMap>();
        if (!dO.isEmpty()) {
            final Iterator iterator = dO.getRows("Resource");
            while (iterator.hasNext()) {
                final Row appInfo = iterator.next();
                final HashMap appDetailInfo = new HashMap();
                appDetailInfo.put("APP_ID", appInfo.get("APP_ID"));
                appDetailInfo.put("APP_NAME", appInfo.get("APP_NAME"));
                appDetailInfo.put("IDENTIFIER", appInfo.get("IDENTIFIER"));
                appDetailInfo.put("APP_VERSION", appInfo.get("APP_VERSION"));
                appDetailInfo.put("APP_NAME_SHORT_VERSION", appInfo.get("APP_NAME_SHORT_VERSION"));
                appDetailInfo.put("BUNDLE_SIZE", appInfo.get("BUNDLE_SIZE"));
                deviceHash.put(appInfo.get("APP_ID"), appDetailInfo);
            }
        }
        return deviceHash;
    }
    
    public HashMap getDeviceKnoxDetails(final Long resourceId, final HashMap deviceHash) throws DataAccessException {
        MdmInvDataProcessor.logger.log(Level.INFO, "Inside method :getDeviceKnoxDetails in class {0}", this.getClass().getName());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
        sQuery.setCriteria(new Criteria(new Column("ManagedKNOXContainer", "RESOURCE_ID"), (Object)resourceId, 0));
        sQuery.addSelectColumn(new Column("ManagedKNOXContainer", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        final HashMap retValue = new HashMap();
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("ManagedKNOXContainer");
            while (it.hasNext()) {
                final Row row = it.next();
                deviceHash.put("KNOX_VERSION", row.get("KNOX_VERSION"));
                deviceHash.put("CONTAINER_STATE", row.get("CONTAINER_STATE"));
                deviceHash.put("RESOURCE_ID", row.get("RESOURCE_ID"));
                deviceHash.put("CONTAINER_ID", row.get("CONTAINER_RESOURCE_ID"));
                deviceHash.put("KNOX_API_LEVEL", row.get("KNOX_API_LEVEL"));
            }
        }
        return deviceHash;
    }
    
    public String getBatteryStatus(final Long resourceID) {
        String battery = "-1.0";
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            sQuery.setCriteria(new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0));
            sQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            sQuery.addSelectColumn(new Column("MdDeviceInfo", "BATTERY_LEVEL"));
            final DataObject batteryDO = MDMUtil.getPersistence().get(sQuery);
            if (!batteryDO.isEmpty()) {
                final Row row = batteryDO.getFirstRow("MdDeviceInfo");
                final Float batteryVal = (Float)row.get("BATTERY_LEVEL");
                if (batteryVal != -1.0f) {
                    battery = batteryVal.intValue() + "%";
                }
            }
        }
        catch (final Exception ex) {
            MdmInvDataProcessor.logger.log(Level.INFO, "Exception occurred while public String getBatteryStatus(Long resourceID)", ex);
        }
        return battery;
    }
    
    static {
        MdmInvDataProcessor.logger = Logger.getLogger("MDMLogger");
        MdmInvDataProcessor.mdmInvDataProcessor = null;
    }
}
