package com.adventnet.sym.server.mdm.encryption.ios.filevault;

import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMDeviceEncryptionSettingsHandler
{
    static Logger logger;
    
    public static void addOrUpdateDeviceTOEncrytptionSetitngs(final JSONObject requestJSON) {
        try {
            final Long encryptionSettingsID = requestJSON.getLong("ENCRYPTION_SETTINGS_ID");
            final Long mdResourceID = requestJSON.getLong("RESOURCE_ID");
            final SelectQuery sqStmt = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceToEncrytptionSettingsRel"));
            sqStmt.addSelectColumn(new Column("DeviceToEncrytptionSettingsRel", "ENCRYPTION_SETTINGS_ID"));
            sqStmt.addSelectColumn(new Column("DeviceToEncrytptionSettingsRel", "RESOURCE_ID"));
            sqStmt.setCriteria(new Criteria(new Column("DeviceToEncrytptionSettingsRel", "RESOURCE_ID"), (Object)mdResourceID, 0));
            final DataObject dO = MDMUtil.getPersistence().get(sqStmt);
            Row row = null;
            boolean isAdd = false;
            if (dO != null && !dO.isEmpty() && dO.containsTable("DeviceToEncrytptionSettingsRel")) {
                row = dO.getFirstRow("DeviceToEncrytptionSettingsRel");
            }
            else {
                row = new Row("DeviceToEncrytptionSettingsRel");
                row.set("RESOURCE_ID", (Object)mdResourceID);
                isAdd = true;
            }
            row.set("ENCRYPTION_SETTINGS_ID", (Object)encryptionSettingsID);
            if (isAdd) {
                dO.addRow(row);
                MDMDeviceEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: New row to be added in DEVICETOENCRYTPTIONSETTINGSREL (Map ManagedDevice to EncryptionSettings) :{0}", row.getAsJSON());
            }
            else {
                dO.updateRow(row);
                MDMDeviceEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Updating existing row  DEVICETOENCRYTPTIONSETTINGSREL (Map ManagedDevice to EncryptionSettings) :{0}", row.getAsJSON());
            }
            MDMUtil.getPersistence().update(dO);
        }
        catch (final JSONException ex) {
            MDMDeviceEncryptionSettingsHandler.logger.log(Level.SEVERE, "FileVaultLog: Exception in addOrUpdateDeviceTOEncrytptionSetitngs() :", (Throwable)ex);
        }
        catch (final DataAccessException ex2) {
            MDMDeviceEncryptionSettingsHandler.logger.log(Level.SEVERE, "FileVaultLog: Exception in addOrUpdateDeviceTOEncrytptionSetitngs() :", (Throwable)ex2);
        }
    }
    
    public static List getResourcesAssociatedWithEncryptionId(final Long encryptionId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceToEncrytptionSettingsRel"));
            selectQuery.addJoin(new Join("DeviceToEncrytptionSettingsRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
            selectQuery.setCriteria(new Criteria(new Column("DeviceToEncrytptionSettingsRel", "ENCRYPTION_SETTINGS_ID"), (Object)encryptionId, 0).and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0)));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            return MDMDBUtil.getColumnValuesAsList(dataObject.getRows("ManagedDevice"), "RESOURCE_ID");
        }
        catch (final DataAccessException ex) {}
        catch (final Exception ex2) {}
        return null;
    }
    
    public static boolean isResourceAssociatedForEncryptionId(final Long encryptionId) {
        final List resourceId = getResourcesAssociatedWithEncryptionId(encryptionId);
        return resourceId != null && !resourceId.isEmpty();
    }
    
    static {
        MDMDeviceEncryptionSettingsHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
