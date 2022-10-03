package com.adventnet.sym.server.mdm.encryption.ios.filevault;

import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.dd.plist.NSDictionary;
import java.io.File;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.server.command.mac.querygenerator.filevault.MacFilevaultPersonalRecoveyKeyRotateGenerator;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.logging.Logger;

public class MDMFilevaultUtils
{
    static Logger logger;
    
    public static boolean isFilevaultEnabled(final Long resourceID) {
        try {
            final Boolean isEnabled = (Boolean)MDMDBUtil.getValueFromDB("MDMDeviceFileVaultInfo", "RESOURCE_ID", (Object)resourceID, "IS_ENCRYPTION_ENABLED");
            if (isEnabled != null) {
                return isEnabled;
            }
        }
        catch (final Exception e) {
            MDMFilevaultUtils.logger.log(Level.SEVERE, "FileVaultLog: Exception in isFilevaultEnabled()", e);
        }
        return false;
    }
    
    public static boolean isFilevaultPersonalRecoveryProfileDistributedSuccessfully(final Long resourceID) {
        try {
            final SelectQuery sqlStmt = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMFileVaultPersonalKeyConfiguration"));
            sqlStmt.addJoin(new Join("MDMFileVaultPersonalKeyConfiguration", "DeviceToEncrytptionSettingsRel", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
            sqlStmt.addSelectColumn(new Column("MDMFileVaultPersonalKeyConfiguration", "ENCRYPTION_SETTINGS_ID"));
            sqlStmt.setCriteria(new Criteria(new Column("DeviceToEncrytptionSettingsRel", "RESOURCE_ID"), (Object)resourceID, 0));
            final DataObject resDO = MDMUtil.getPersistence().get(sqlStmt);
            return !resDO.isEmpty();
        }
        catch (final Exception e) {
            MDMFilevaultUtils.logger.log(Level.SEVERE, "FileVaultLog: Exception in isFilevaultPersonalRecoveryProfileDistributedSuccessfully()", e);
            return false;
        }
    }
    
    public static boolean isPersonalRecoveryKeyAvailable(final Long resourceID) {
        try {
            final String personalRecoveryKey = (String)MDMDBUtil.getValueFromDB("MDMDeviceFileVaultInfo", "RESOURCE_ID", (Object)resourceID, "PERSONAL_RECOVERY_KEY");
            return !MDMStringUtils.isEmpty(personalRecoveryKey);
        }
        catch (final Exception e) {
            MDMFilevaultUtils.logger.log(Level.SEVERE, "FileVaultLog: Exception in isPersonalRecoveryKeyAvailable()", e);
            return false;
        }
    }
    
    public static boolean isFilevaultPersonalRecoveryKeyImported(final Long resourceID) {
        try {
            final List<Long> resIDs = new ArrayList<Long>();
            resIDs.add(resourceID);
            final SelectQuery sql = MacFilevaultPersonalRecoveyKeyRotateGenerator.getFilevaultImportQuery(resIDs);
            final DataObject daO = MDMUtil.getPersistence().get(sql);
            return daO.containsTable("MDMFileVaultRotateKeyImportInfo");
        }
        catch (final Exception e) {
            MDMFilevaultUtils.logger.log(Level.SEVERE, "FileVaultLog: Exception in isPersonalRecoveryKeyAvailable()", e);
            return false;
        }
    }
    
    public static boolean rotateFilevaultPersonalRecoveryKey(final Long resourceID, final Long userID) {
        final List<Long> resourceList = new ArrayList<Long>();
        resourceList.add(resourceID);
        final HashMap infoMap = new HashMap();
        infoMap.put("technicianID", userID);
        infoMap.put("isSilentCommand", "false");
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            DeviceInvCommandHandler.getInstance().invokeCommand(resourceList, "MacFileVaultPersonalKeyRotate", infoMap);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerID, "INVENTORY_ACTIONS_MODULE", "MacFileVaultPersonalKeyRotate");
            final DeviceDetails deviceDetail = new DeviceDetails(resourceID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2144, resourceID, DMUserHandler.getUserNameFromUserID(userID), "mdm.profile.filevault_rotate_initiated", deviceDetail.name + "@@@" + deviceDetail.serialNumber, deviceDetail.customerId);
            return true;
        }
        catch (final SyMException e) {
            MDMFilevaultUtils.logger.log(Level.SEVERE, (Throwable)e, () -> "[Filevault] Existing key NA so not proceeding to rotate:" + n);
            return false;
        }
    }
    
    public static Long getFilevaultPersonalRecoveryCertificateID(final Long collectionID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MacFileVault2Policy"));
        selectQuery.addJoin(new Join("MacFileVault2Policy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("MacFileVault2Policy", "MDMFileVaultPersonalKeyConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("MDMFileVaultPersonalKeyConfiguration", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject != null && dataObject.containsTable("MDMFileVaultPersonalKeyConfiguration")) {
            final Row fvRow = dataObject.getFirstRow("MDMFileVaultPersonalKeyConfiguration");
            final Long certificateID = (Long)fvRow.get("RECOVERY_ENCRYPT_CERT_ID");
            MDMFilevaultUtils.logger.log(Level.SEVERE, "[Filevault] Personal Recovery Key CertID {0}for that Collection:{1}", new Object[] { certificateID, collectionID });
            return certificateID;
        }
        MDMFilevaultUtils.logger.log(Level.SEVERE, "[Filevault] No Personal Recovery Key Cert Found for given CollectionID {0} Probably because FV Profile is Institutional Only", collectionID);
        return null;
    }
    
    private static JSONObject getAnyCollectionIDMappingEncryptionCertificateID(final Long resourceID) throws DataAccessException {
        final JSONObject customerTOCollectionJSON = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceToEncrytptionSettingsRel"));
        sQuery.addJoin(new Join("DeviceToEncrytptionSettingsRel", "MDMFileVaultPersonalKeyConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
        sQuery.addJoin(new Join("MDMFileVaultPersonalKeyConfiguration", "MacFileVault2Policy", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
        sQuery.addJoin(new Join("MacFileVault2Policy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria resIDCri = new Criteria(Column.getColumn("DeviceToEncrytptionSettingsRel", "RESOURCE_ID"), (Object)resourceID, 0);
        sQuery.setCriteria(resIDCri);
        sQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "*"));
        sQuery.addSelectColumn(Column.getColumn("CollnToCustomerRel", "*"));
        final DataObject resultDO = MDMUtil.getPersistence().get(sQuery);
        if (resultDO != null && resultDO.containsTable("CfgDataToCollection")) {
            final Long collectionID = (Long)resultDO.getFirstRow("CfgDataToCollection").get("COLLECTION_ID");
            final Long customerID = (Long)resultDO.getRow("CollnToCustomerRel", new Criteria(new Column("CollnToCustomerRel", "COLLECTION_ID"), (Object)collectionID, 0)).get("CUSTOMER_ID");
            final Iterator itr = resultDO.getRows("CfgDataToCollection");
            if (MDMDBUtil.getIteratorSize(itr) > 1) {
                MDMFilevaultUtils.logger.log(Level.FINE, "[Filevault] Other CollectionIDs mapped to this resource''s Encryption Settings :{0}", resultDO);
            }
            customerTOCollectionJSON.put("COLLECTION_ID", (Object)collectionID);
            customerTOCollectionJSON.put("CUSTOMER_ID", (Object)customerID);
            MDMFilevaultUtils.logger.log(Level.FINE, "[Filevault] CollectionID for Given Resource :{0}", customerTOCollectionJSON);
            return customerTOCollectionJSON;
        }
        return null;
    }
    
    public static String getFilevaultPersonalRecoveryKeyPath(final Long customerID, final Long collectionID) throws Exception {
        return ProfileUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID) + File.separator + "filevault_personalkey_rotate.xml";
    }
    
    public static NSDictionary getFileVaultPersonalRecoveryCommandXMLForResID(final Long resID) throws Exception {
        final JSONObject customerTOCollectionJSON = getAnyCollectionIDMappingEncryptionCertificateID(resID);
        final Long customerID = JSONUtil.optLongForUVH(customerTOCollectionJSON, "CUSTOMER_ID", (Long)null);
        final Long collectionID = JSONUtil.optLongForUVH(customerTOCollectionJSON, "COLLECTION_ID", (Long)null);
        final String xmlPath = getFilevaultPersonalRecoveryKeyPath(customerID, collectionID);
        MDMFilevaultUtils.logger.log(Level.INFO, "[Filevault] Going to readXML path  :[{0}] for resource:{1}", new Object[] { xmlPath, resID });
        final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(ApiFactoryProvider.getFileAccessAPI().readFile(xmlPath));
        return rootDict;
    }
    
    static {
        MDMFilevaultUtils.logger = Logger.getLogger("MDMConfigLogger");
    }
}
