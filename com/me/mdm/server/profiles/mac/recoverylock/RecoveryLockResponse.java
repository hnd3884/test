package com.me.mdm.server.profiles.mac.recoverylock;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import java.util.HashMap;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class RecoveryLockResponse implements CommandResponseProcessor.SeqQueuedResponseProcessor
{
    static final Logger LOGGER;
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject responseJson) {
        final Long resourceId = responseJson.getLong("resourceId");
        final String commandUDID = responseJson.optString("strCommandUuid");
        final String status = responseJson.getString("strStatus");
        responseJson.put("resourceID", (Object)resourceId);
        responseJson.put("commandUUID", (Object)commandUDID);
        responseJson.put("params", (Object)new JSONObject());
        responseJson.put("isNeedToAddQueue", true);
        final int handler = this.getSequentialCommandHandler(status);
        responseJson.put("action", handler);
        return responseJson;
    }
    
    private int getSequentialCommandHandler(final String status) {
        int handler = 2;
        if (status.equalsIgnoreCase("Acknowledged")) {
            handler = 1;
        }
        else if (status.equalsIgnoreCase("NotNow")) {
            handler = 5;
        }
        return handler;
    }
    
    protected Long getCollectionID(final JSONObject params) {
        final JSONObject sequentialCommandParams = params.getJSONObject("PARAMS");
        final JSONObject commandLevelParams = sequentialCommandParams.getJSONObject("CommandLevelParams");
        return commandLevelParams.getLong("CollectionID");
    }
    
    protected List<Long> getResourceList(final JSONObject params) {
        final Long resourceId = params.getLong("resourceId");
        final List<Long> resourceIdList = new ArrayList<Long>();
        resourceIdList.add(resourceId);
        return resourceIdList;
    }
    
    public void updateSecurityInfo(final Long resourceID, final JSONObject params) throws Exception {
        final String udid = params.getString("strUDID");
        RecoveryLockResponse.LOGGER.log(Level.INFO, "Updating security Info for resource: {0}", new Object[] { udid });
        final String strData = params.getString("strData");
        final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("SecurityInfo", strData);
        final HashMap securityInfoMap = PlistWrapper.getInstance().getHashFromDict(nsDict);
        MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, securityInfoMap);
        RecoveryLockResponse.LOGGER.log(Level.INFO, "Successfully updated security Info for resource: {0}", new Object[] { udid });
    }
    
    public void handleError(final JSONObject params) throws DataAccessException {
        final long collectionId = this.getCollectionID(params);
        final List<Long> resourceIdList = this.getResourceList(params);
        final String strData = String.valueOf(params.get("strData"));
        final JSONObject errorJSON = new IOSErrorStatusHandler().getIOSErrors(strData, strData, "Error");
        RecoveryLockResponse.LOGGER.log(Level.SEVERE, "Error JSON for recovery lock response: {0}", new Object[] { strData });
        final String errorRemarks = errorJSON.optString("EnglishRemarks");
        MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIdList, collectionId, 7, errorRemarks);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
