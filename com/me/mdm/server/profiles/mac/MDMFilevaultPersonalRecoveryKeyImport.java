package com.me.mdm.server.profiles.mac;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.command.mac.querygenerator.filevault.MacFilevaultPersonalRecoveyKeyRotateGenerator;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.logging.Logger;

public class MDMFilevaultPersonalRecoveryKeyImport
{
    public static final int FILEVAULT_KEY_IMPORT_DEVICE_IDENTIFIER_SERIAL_NO = 1;
    private static final Logger LOGGER;
    
    public static String getSerialNumberForResourceID(final Long resourceID) throws Exception {
        final String serialNo = (String)MDMDBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "SERIAL_NUMBER");
        return serialNo;
    }
    
    private static Row getFilevaultImportRow(final Long resourceID) throws Exception {
        final List<Long> resIDs = new ArrayList<Long>();
        resIDs.add(resourceID);
        final SelectQuery fvKeyImportQuery = MacFilevaultPersonalRecoveyKeyRotateGenerator.getFilevaultImportQuery(resIDs);
        final DataObject DO = MDMUtil.getPersistence().get(fvKeyImportQuery);
        if (DO.containsTable("MDMFileVaultRotateKeyImportInfo")) {
            final Row row = DO.getFirstRow("MDMFileVaultRotateKeyImportInfo");
            return row;
        }
        MDMFilevaultPersonalRecoveryKeyImport.LOGGER.log(Level.INFO, "[Filevault]No Rows found in MDMFILEVAULTROTATEKEYIMPORTINFO for resource:{0}", resourceID);
        return null;
    }
    
    public static void updateErrorRemarksForFVKeyRotation(final Long resourceID, final String errorRemaks) throws Exception {
        final List<Long> resIDs = new ArrayList<Long>();
        resIDs.add(resourceID);
        final SelectQuery fvKeyImportQuery = MacFilevaultPersonalRecoveyKeyRotateGenerator.getFilevaultImportQuery(resIDs);
        final DataObject DO = MDMUtil.getPersistence().get(fvKeyImportQuery);
        if (DO.containsTable("MDMFileVaultRotateKeyImportInfo")) {
            final Row row = DO.getFirstRow("MDMFileVaultRotateKeyImportInfo");
            row.set("ERROR_REMARKS", (Object)errorRemaks);
            DO.updateRow(row);
            MDMUtil.getPersistence().update(DO);
            MDMFilevaultPersonalRecoveryKeyImport.LOGGER.log(Level.INFO, "[Filevault] Error Remakrs [{0}] updated  MDMFILEVAULTROTATEKEYIMPORTINFO for resource:{1}", new Object[] { errorRemaks, resourceID });
        }
    }
    
    public static void deleteFVKeyImportRotate(final Long resourceID) throws Exception {
        final DeleteQuery dq = (DeleteQuery)new DeleteQueryImpl("MDMFileVaultRotateKeyImportInfo");
        final String serialNumber = getSerialNumberForResourceID(resourceID);
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
        final Criteria serialNumberCri = new Criteria(new Column("MDMFileVaultRotateKeyImportInfo", "DEVICE_IDENTIFIER"), (Object)serialNumber, 0);
        final Criteria customerIDCriteria = new Criteria(new Column("MDMFileVaultRotateKeyImportInfo", "CUSTOMER_ID"), (Object)customerID, 0);
        dq.setCriteria(serialNumberCri.and(customerIDCriteria));
        MDMUtil.getPersistence().delete(dq);
        MDMFilevaultPersonalRecoveryKeyImport.LOGGER.log(Level.INFO, "[Filevault] Deleted MDMFILEVAULTROTATEKEYIMPORTINFO for :{0} SerialNumber:{1}", new Object[] { resourceID, serialNumber });
    }
    
    public static void addOrUpdateFilevaultKeyImport(final Long customerID, final int deviceIdentifierType, final HashMap<String, String> serialRotationKeyMap) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMFileVaultRotateKeyImportInfo"));
        final Criteria custCri = new Criteria(Column.getColumn("MDMFileVaultRotateKeyImportInfo", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria serialNoCriteria = new Criteria(Column.getColumn("MDMFileVaultRotateKeyImportInfo", "DEVICE_IDENTIFIER"), (Object)serialRotationKeyMap.keySet().toArray(), 8);
        selectQuery.setCriteria(custCri.and(serialNoCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject obj = MDMUtil.getPersistence().get(selectQuery);
        for (final String serialNO : serialRotationKeyMap.keySet()) {
            final Criteria serialNo = new Criteria(Column.getColumn("MDMFileVaultRotateKeyImportInfo", "DEVICE_IDENTIFIER"), (Object)serialNO, 0);
            Row existingRow = obj.getRow("MDMFileVaultRotateKeyImportInfo", serialNo);
            if (existingRow == null) {
                existingRow = new Row("MDMFileVaultRotateKeyImportInfo");
            }
            existingRow.set("CUSTOMER_ID", (Object)customerID);
            existingRow.set("DEVICE_IDENTIFIER", (Object)serialNO);
            existingRow.set("DEVICE_IDENTIFIER_TYPE", (Object)deviceIdentifierType);
            existingRow.set("PERSONAL_RECOVERY_KEY", (Object)serialRotationKeyMap.get(serialNO));
            existingRow.set("ERROR_REMARKS", (Object)null);
            if (existingRow.hasUVGColInPK()) {
                obj.addRow(existingRow);
            }
            else {
                obj.updateRow(existingRow);
            }
        }
        MDMFilevaultPersonalRecoveryKeyImport.LOGGER.log(Level.INFO, "[Filevault] Imported Existing recovery keys for devices:{0}", serialRotationKeyMap.keySet());
        MDMUtil.getPersistence().update(obj);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
