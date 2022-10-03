package com.me.mdm.server.security.mac;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.inv.ProcessorType;
import java.util.Map;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.security.mac.recoverylock.RecoveryLockSequentialCommand;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.server.security.passcode.MDMManagedPasswordHandler;
import org.apache.commons.lang.RandomStringUtils;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.List;
import java.util.logging.Logger;

public class MacFirmwareConfigHandler
{
    private static final Logger LOGGER;
    
    public static void processFirmwareProfileAssociationToDevices(final List resourceIDList, final Long collectionID) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 18, "mdm.profile.distribution.waitingfordeviceinfo");
            addFirmwarePasswordFromCollection(resourceIDList, collectionID);
        }
        catch (final Exception ex) {
            MacFirmwareConfigHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception in  MacFirmwareNotApplicableHandler setNotApplicableStatus():", ex);
            try {
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 7, ex.getMessage());
            }
            catch (final Exception ex2) {
                MacFirmwareConfigHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception in  Updating Collection failure status():", ex2);
            }
        }
    }
    
    public static void processFirmwareProfileDisAssociationToDevices(final List resourceIDList, final Long collectionID) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 18, "mdm.profile.distribution.waitingfordeviceinfo");
            removeFirmwarePasswordFromCollection(resourceIDList, collectionID);
        }
        catch (final Exception ex) {
            MacFirmwareConfigHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception in  MacFirmwareNotApplicableHandler setNotApplicableStatus():", ex);
            try {
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 7, ex.getMessage());
            }
            catch (final Exception ex2) {
                MacFirmwareConfigHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception in  Updating Collection failure status():", ex2);
            }
        }
    }
    
    public static JSONObject getFirmwarePolicyConfiguration(final Long collectionID) {
        try {
            final SelectQuery selectQueryImpl = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
            selectQueryImpl.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQueryImpl.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQueryImpl.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQueryImpl.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQueryImpl.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQueryImpl.addJoin(new Join("ConfigDataItem", "MacFirmwarePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            selectQueryImpl.addSelectColumn(new Column("MacFirmwarePolicy", "*"));
            selectQueryImpl.addSelectColumn(new Column("Profile", "*"));
            selectQueryImpl.addSelectColumn(new Column("ProfileToCustomerRel", "*"));
            selectQueryImpl.setCriteria(new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
            final DataObject dao = MDMUtil.getPersistence().get(selectQueryImpl);
            if (dao != null && !dao.isEmpty()) {
                final Row firmwarePolicyRow = dao.getRow("MacFirmwarePolicy");
                final JSONObject policyJSON = MDMDBUtil.rowToJSON(firmwarePolicyRow);
                final Row profileRow = dao.getRow("Profile");
                final JSONObject profileRowJSON = MDMDBUtil.rowToJSON(profileRow);
                JSONUtil.putAll(policyJSON, profileRowJSON);
                return policyJSON;
            }
        }
        catch (final Exception ex) {
            MacFirmwareConfigHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception in  getFirmwarePolicyConfiguration():", ex);
        }
        return null;
    }
    
    private static void addFirmwarePasswordFromCollection(final List<Long> resourceIDList, final Long collectionID) throws Exception {
        final JSONObject policyJSON = getFirmwarePolicyConfiguration(collectionID);
        final Map<Long, ProcessorType> macProcessorMap = MDMUtil.getMacProcessorType(resourceIDList);
        final List<Long> siliconMacs = MDMUtil.filterSiliconMacs(macProcessorMap);
        final JSONObject rowJSON = new JSONObject();
        final JSONObject commandParams = new JSONObject();
        rowJSON.put("MANAGED_PASSWORD_STATUS", 1);
        rowJSON.put("MANAGED_PASSWORD_ID", (Object)String.valueOf(policyJSON.get("FIRMWARE_NEW_PASSWORD")));
        rowJSON.put("ADDED_BY", policyJSON.get("CREATED_BY"));
        final int firmwarePasswordType = policyJSON.getInt("FIRMWARE_PASSWORD_TYPE");
        if (firmwarePasswordType != 3) {
            commandParams.put("isClearPassword", false);
            if (firmwarePasswordType == 1) {
                for (final Long resourceID : resourceIDList) {
                    rowJSON.put("RESOURCE_ID", (Object)resourceID);
                    final boolean isSiliconMac = siliconMacs.contains(resourceID);
                    MacFirmwarePasswordDeviceAssociationHandler.addFirmwarePasswordToDevice(isSiliconMac, rowJSON);
                }
            }
            else if (firmwarePasswordType == 2) {
                final Long userID = JSONUtil.optLongForUVH(policyJSON, "CREATED_BY", (Long)null);
                final Long customerID = JSONUtil.optLongForUVH(policyJSON, "CUSTOMER_ID", (Long)null);
                for (final Long resourceID2 : resourceIDList) {
                    final boolean isSiliconMac2 = siliconMacs.contains(resourceID2);
                    rowJSON.put("RESOURCE_ID", (Object)resourceID2);
                    final String managedPassword = RandomStringUtils.randomAlphanumeric(8);
                    final Long passwordID = MDMManagedPasswordHandler.getMDMManagedPasswordID(managedPassword, customerID, userID);
                    rowJSON.put("MANAGED_PASSWORD_ID", (Object)passwordID);
                    MacFirmwarePasswordDeviceAssociationHandler.addFirmwarePasswordToDevice(isSiliconMac2, rowJSON);
                }
            }
        }
        else if (firmwarePasswordType == 3) {
            commandParams.put("isClearPassword", true);
        }
        final List<Long> intelMacs = new ArrayList<Long>(resourceIDList);
        intelMacs.removeAll(siliconMacs);
        if (!intelMacs.isEmpty()) {
            MacFirmwareConfigHandler.LOGGER.log(Level.INFO, "Adding firmware Sequential command for collection: {0}, Resources: {1}", new Object[] { collectionID, resourceIDList });
            MacFirmwareUtil.addFirmwarePolicySequentialCommand(collectionID);
            final List<Long> seqCommandIDList = new ArrayList<Long>();
            seqCommandIDList.add(DeviceCommandRepository.getInstance().getCommandID(MacFirmwareUtil.getFirmwareBaseSeqCommandStr(collectionID)));
            SeqCmdRepository.getInstance().executeSequentially(intelMacs, seqCommandIDList, commandParams);
        }
        if (!siliconMacs.isEmpty()) {
            MacFirmwareConfigHandler.LOGGER.log(Level.INFO, "Adding Recovery Lock Sequential command for collection: {0}, Resources: {1}", new Object[] { collectionID, resourceIDList });
            RecoveryLockSequentialCommand.addSequentialCommand(collectionID);
            final List<Long> seqCommandIDList = new ArrayList<Long>();
            final String recLockBaseCmd = "SetRecoveryLock".concat(";Colln=").concat(collectionID.toString());
            seqCommandIDList.add(DeviceCommandRepository.getInstance().getCommandID(recLockBaseCmd));
            SeqCmdRepository.getInstance().executeSequentially(siliconMacs, seqCommandIDList, commandParams);
        }
        NotificationHandler.getInstance().SendNotification(resourceIDList, 1);
    }
    
    private static void removeFirmwarePasswordFromCollection(final List<Long> resourceIDList, final Long collectionID) throws Exception {
        final Map<Long, ProcessorType> macProcessorMap = MDMUtil.getMacProcessorType(resourceIDList);
        final List<Long> siliconMacs = MDMUtil.filterSiliconMacs(macProcessorMap);
        final List<Long> intelMacs = new ArrayList<Long>(resourceIDList);
        intelMacs.removeAll(siliconMacs);
        if (!intelMacs.isEmpty()) {
            MacFirmwareConfigHandler.LOGGER.log(Level.INFO, "Removing Firmware password profile for resources: {0}", new Object[] { intelMacs });
            MacFirmwareUtil.addFirmwarePolicySequentialCommand(collectionID);
            final JSONObject commandParams = new JSONObject();
            commandParams.put("isClearPassword", true);
            final List<Long> seqCommandIDList = new ArrayList<Long>();
            final Long firmwareBaseCommandId = DeviceCommandRepository.getInstance().getCommandID(MacFirmwareUtil.getFirmwareBaseSeqCommandStr(collectionID));
            seqCommandIDList.add(firmwareBaseCommandId);
            SeqCmdRepository.getInstance().executeSequentially(intelMacs, seqCommandIDList, commandParams);
        }
        if (!siliconMacs.isEmpty()) {
            MacFirmwareConfigHandler.LOGGER.log(Level.INFO, "Removing Recovery Lock password profile for resources: {0}", new Object[] { siliconMacs });
            RecoveryLockSequentialCommand.addSequentialCommand(collectionID);
            final JSONObject commandParams = new JSONObject();
            commandParams.put("isClearPassword", true);
            final List<Long> seqCommandIDList = new ArrayList<Long>();
            final String recoveryLockBaseCommandUuid = "SetRecoveryLock".concat(";Colln=").concat(collectionID.toString());
            final Long seqBaseCommandId = DeviceCommandRepository.getInstance().getCommandID(recoveryLockBaseCommandUuid);
            seqCommandIDList.add(seqBaseCommandId);
            SeqCmdRepository.getInstance().executeSequentially(siliconMacs, seqCommandIDList, commandParams);
        }
        NotificationHandler.getInstance().SendNotification(resourceIDList, 1);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
