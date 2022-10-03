package com.me.mdm.server.inv.ios;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSDevNameResProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        IOSDevNameResProcessor.logger.log(Level.FINE, "Immediate seq command processing for device Name.Params:{0}", new Object[] { params });
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqResponse = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            response.put("action", 1);
            seqResponse.put("isNeedToAddQueue", true);
            response.put("resourceID", (Object)resourceID);
            response.put("commandUUID", (Object)commandUUID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", false);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            IOSDevNameResProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing Device Rename immediate seq processing for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final String strStatus = params.optString("strStatus");
            final String strData = params.optString("strData");
            final Long resourceID = params.optLong("resourceId");
            final Long customerId = params.optLong("customerId");
            final String strUDID = params.optString("strUDID");
            IOSDevNameResProcessor.logger.log(Level.INFO, "Device name update command status : {0}", strStatus);
            String status = "";
            final String serialNumber = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "SERIAL_NUMBER");
            final long userID = (long)DBUtil.getValueFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)resourceID, "USER_ID");
            final String userName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(Long.valueOf(userID)));
            if (strStatus.equalsIgnoreCase("Acknowledged")) {
                final NSArray nsArr = PlistWrapper.getInstance().getArrayForKey("Settings", strData);
                final NSDictionary statusDict = (NSDictionary)nsArr.lastObject();
                String specificStatus = "";
                specificStatus = statusDict.objectForKey("Status").toString();
                if (specificStatus.equalsIgnoreCase("Error")) {
                    status = "dc.mdm.inv.device_name_failure";
                    if (this.isDeviceNameRestricted(resourceID)) {
                        status = "mdm.inv.ios.device_name_failure";
                    }
                    try {
                        final NSArray errorArr = (NSArray)statusDict.get((Object)"ErrorChain");
                        final NSDictionary errorDict = (NSDictionary)errorArr.lastObject();
                        IOSDevNameResProcessor.logger.log(Level.INFO, "Error remarks for Resource ID {0} - Device name update status : {1}", new Object[] { resourceID, errorDict.objectForKey("USEnglishDescription").toString() });
                    }
                    catch (final Exception e) {
                        IOSDevNameResProcessor.logger.log(Level.SEVERE, "Exception while retrieving error remarks for device name update", e);
                    }
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(29050, resourceID, userName, status, serialNumber, customerId);
                }
                else {
                    status = "dc.mdm.inv.device_name_success";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(29051, resourceID, userName, status, serialNumber, customerId);
                }
            }
            else if (strStatus.equalsIgnoreCase("Error") || strStatus.equalsIgnoreCase("CommandFormatError")) {
                status = "dc.mdm.inv.device_name_failure";
                final IOSErrorStatusHandler errorHandler = new IOSErrorStatusHandler();
                final JSONObject json = errorHandler.getIOSErrors(strUDID, strData, strStatus);
                IOSDevNameResProcessor.logger.log(Level.INFO, "Error remarks for Resource ID {0} - Device name update status : {1}", new Object[] { resourceID, json.get("EnglishRemarks").toString() });
                MDMEventLogHandler.getInstance().MDMEventLogEntry(29050, resourceID, userName, status, serialNumber, customerId);
            }
            IOSDevNameResProcessor.logger.log(Level.INFO, "Remarks for Resource ID {0} - Device name update status : {1}", new Object[] { resourceID, status });
        }
        catch (final Exception ex) {
            IOSDevNameResProcessor.logger.log(Level.SEVERE, "Exception in processDeviceNameUpdatedResponse", ex);
        }
        return new JSONObject();
    }
    
    public boolean isDeviceNameRestricted(final Long resourceId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdIOSRestriction"));
            selectQuery.addSelectColumn(new Column("MdIOSRestriction", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("MdIOSRestriction", "ALLOW_MODIFI_DEVICE_NAME"));
            final Criteria deviceNameCriteria = new Criteria(new Column("MdIOSRestriction", "RESOURCE_ID"), (Object)resourceId, 0).and(new Criteria(new Column("MdIOSRestriction", "ALLOW_MODIFI_DEVICE_NAME"), (Object)2, 0));
            selectQuery.setCriteria(deviceNameCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final DataAccessException e) {
            IOSDevNameResProcessor.logger.log(Level.SEVERE, "Exception while checking device name restriction", (Throwable)e);
        }
        return false;
    }
    
    static {
        IOSDevNameResProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
