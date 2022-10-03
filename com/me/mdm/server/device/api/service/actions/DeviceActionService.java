package com.me.mdm.server.device.api.service.actions;

import com.me.mdm.server.device.api.model.DeviceDetailsModel;
import com.me.mdm.server.inv.actions.InvActionUtil;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import com.me.mdm.server.device.resource.Device;
import com.google.gson.Gson;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.device.api.service.DeviceService;
import java.util.List;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import java.util.ArrayList;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.device.api.model.SearchDevice;
import com.me.mdm.server.device.api.model.actions.DeviceActionModel;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFilevaultUtils;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.mdm.server.profiles.mac.MDMFilevaultPersonalRecoveryKeyImport;
import com.me.mdm.server.device.api.model.actions.FilevaultKeyImportModel;
import java.util.logging.Logger;

public class DeviceActionService
{
    protected static Logger logger;
    
    public boolean importFilevaultPersonalRotateKey(final FilevaultKeyImportModel model) {
        final Long deviceID = model.getResourceID();
        final String importKey = model.getExistingPersonalKey();
        final Long customerID = model.getCustomerId();
        try {
            final String serialNo = MDMFilevaultPersonalRecoveryKeyImport.getSerialNumberForResourceID(deviceID);
            final HashMap<String, String> serialMap = new HashMap<String, String>();
            serialMap.put(serialNo, importKey);
            MDMFilevaultPersonalRecoveryKeyImport.addOrUpdateFilevaultKeyImport(customerID, 1, serialMap);
        }
        catch (final Exception ex) {
            DeviceActionService.logger.log(Level.SEVERE, ex, () -> "[Filevault] Unable to Import Filevaultkeys for resource: " + n);
            return false;
        }
        return true;
    }
    
    public boolean rotateFilevaultPersonalRecoveryKeyForInventryAction(final Long resourceID, final Long userID) {
        final boolean existingKey = MDMFilevaultUtils.isPersonalRecoveryKeyAvailable(resourceID);
        if (existingKey) {
            MDMFilevaultUtils.rotateFilevaultPersonalRecoveryKey(resourceID, userID);
        }
        else {
            DeviceActionService.logger.log(Level.SEVERE, "[Filevault] Existing key NA so not proceeding to rotate:{0}", resourceID);
        }
        return false;
    }
    
    public void logoutUser(final DeviceActionModel model, final SearchDevice searchDevice) throws Exception {
        if (!this.validateDeviceAction(model, searchDevice)) {
            throw new APIHTTPException("CMD0001", new Object[0]);
        }
        final List<Long> resourceList = new ArrayList<Long>();
        resourceList.add(model.getDeviceId());
        final HashMap infoMap = new HashMap();
        infoMap.put("technicianID", model.getUserId());
        infoMap.put("isSilentCommand", "false");
        DeviceInvCommandHandler.getInstance().invokeCommand(resourceList, model.getCommandName(), infoMap);
        MEMDMTrackParamManager.getInstance().incrementTrackValue(model.getCustomerId(), "INVENTORY_ACTIONS_MODULE", model.getCommandName());
    }
    
    private boolean validateDeviceAction(final DeviceActionModel actionModel, final SearchDevice searchDevice) throws Exception {
        final DeviceDetailsModel deviceModel = new DeviceService().getDevice(searchDevice);
        final ObjectMapper objectMapper = new ObjectMapper();
        final JSONObject deviceJSON = new JSONObject(objectMapper.writeValueAsString((Object)deviceModel));
        final Gson gson = new Gson();
        final Device device = (Device)gson.fromJson(deviceJSON.toString(), (Class)Device.class);
        final JSONObject message = new JSONObject();
        message.put("CUSTOMER_ID", (Object)actionModel.getCustomerId());
        InvActionUtilProvider.getInvActionUtil(device.getPlatformType());
        return InvActionUtil.validateDeviceAction(actionModel.getCommandName(), device, message);
    }
    
    static {
        DeviceActionService.logger = Logger.getLogger("MDMConfigLogger");
    }
}
