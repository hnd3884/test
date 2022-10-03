package com.me.mdm.server.inv.actions;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.api.command.container.ContainerCommandWrapper;
import com.me.mdm.server.inv.actions.resource.InventoryAction;
import com.me.mdm.api.command.device.DeviceCommandWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.me.mdm.server.device.DeviceFacade;
import org.json.simple.JSONArray;
import java.util.Set;
import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import com.me.mdm.server.device.resource.Device;
import java.util.Locale;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class InvActionUtil
{
    protected static Logger logger;
    static HashMap<String, String> actionMap;
    
    public static String getStatusDescription(final int statusCode) {
        return APIUtil.getEnglishString(getI18NKeyforStatus(statusCode), new Object[0]);
    }
    
    public String getI18NKeyForAction(final String action) {
        switch (action) {
            case "scan": {
                return "dc.common.SCAN_NOW";
            }
            case "lock": {
                return "dc.mdm.inv.remote_lock";
            }
            case "remote_alarm": {
                return "dc.mdm.inv.ring_device";
            }
            case "remote_control": {
                return "dc.mdm.inv.remote_troubleshoot";
            }
            case "remote_view": {
                return "dc.mdm.inv.remote_view";
            }
            case "remote_debug": {
                return "dc.mdm.inv.remote_debug";
            }
            case "complete_wipe": {
                return "dc.mdm.inv.remote_wipe";
            }
            case "corporate_wipe": {
                return "dc.mdm.inv.corporate_wipe";
            }
            case "clear_passcode":
            case "clear_container_password": {
                return "dc.mdm.inv.clear_passcode";
            }
            case "reset_passcode": {
                return "dc.mdm.inv.reset_passcode";
            }
            case "fetch_location": {
                return "dc.mdm.inv.get_Location";
            }
            case "restart": {
                return "dc.common.reboot";
            }
            case "shutdown": {
                return "dc.common.SHUTDOWN";
            }
            case "enable_lost_mode": {
                return "dc.mdm.geoLoc.find_my_phone.when_lost.enable_lost_mode";
            }
            case "disable_lost_mode": {
                return "dc.mdm.geoLoc.find_my_phone.when_lost.stop_lost_mode";
            }
            case "create_container": {
                return "dc.mdm.knox.container.create";
            }
            case "remove_container": {
                return "dc.mdm.android.knox.deactivate_knox";
            }
            case "lock_container": {
                return "dc.mdm.knox.container.lock";
            }
            case "unlock_container": {
                return "dc.mdm.knox.container.unlock";
            }
            case "pause_kiosk": {
                return "mdm.inv.pause_kiosk";
            }
            case "re_apply_kiosk": {
                return "mdm.inv.resume_kiosk";
            }
            case "unlock_user_account": {
                return "dc.mdm.inv.unlock_user_account";
            }
            case "rotate_filevault_personal_key": {
                return "dc.mdm.inv.filevault_personal_rotate";
            }
            case "clear_app_data": {
                return "dc.mdm.inv.clear_app_data";
            }
            case "logout_user": {
                return "dc.mdm.inv.logout_user";
            }
            default: {
                return null;
            }
        }
    }
    
    private static String getI18NKeyforStatus(final int status) {
        switch (status) {
            case 2: {
                return "dc.mdm.general.command.succeeded";
            }
            case 1: {
                return "dc.mdm.general.command.initiated";
            }
            case 3: {
                return "dc.mdm.general.command.not_initiated";
            }
            case -1: {
                return "dc.mdm.general.command.timed_out";
            }
            default: {
                return "dc.mdm.general.command.failed";
            }
        }
    }
    
    public static String getLocalizedStatusDescription(final int statusCode) {
        Locale locale = null;
        try {
            locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
        }
        catch (final Exception e) {
            InvActionUtil.logger.log(Level.SEVERE, "exception occured while fetching locale");
        }
        return APIUtil.getLocalizedString(getI18NKeyforStatus(statusCode), locale, new Object[0]);
    }
    
    public static String getLocalizedString(final String key, final Object... args) {
        Locale locale = null;
        try {
            locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
        }
        catch (final Exception e) {
            InvActionUtil.logger.log(Level.SEVERE, "exception occured while fetching locale");
        }
        return APIUtil.getLocalizedString(key, locale, args);
    }
    
    public static String getLocalizedString(final String key) {
        Locale locale = null;
        try {
            locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
        }
        catch (final Exception e) {
            InvActionUtil.logger.log(Level.SEVERE, "exception occured while fetching locale");
        }
        return APIUtil.getLocalizedString(key, locale, new Object[0]);
    }
    
    public abstract InventoryActionList getApplicableActions(final Device p0, final Long p1);
    
    public abstract JSONArray getApplicableBulkActionDevices(final Set p0, final String p1, final Long p2);
    
    public InventoryActionList getApplicableActions(final Long deviceId, final Long customerId) {
        final JSONObject deviceJSON = new DeviceFacade().getDevice(deviceId, customerId);
        final Gson gson = new Gson();
        final Device device = (Device)gson.fromJson(deviceJSON.toString(), (Class)Device.class);
        return InvActionUtilProvider.getInvActionUtil(device.getPlatformType()).getApplicableActions(device, customerId);
    }
    
    public static String getEquivalentCommandName(final String commandName) {
        return InvActionUtil.actionMap.get(commandName);
    }
    
    public SelectQuery getBulkDeviceQuery(final Set deviceSet, final Long customerID, final int platform) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        query.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "USER_RESOURCE", 1));
        query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1));
        final Criteria resourceCriteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)deviceSet.toArray(), 8);
        final Criteria managedStatusCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria platformCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria overAllCriteria = resourceCriteria.and(managedStatusCriteria).and(customerCriteria).and(platformCriteria);
        query.setCriteria(overAllCriteria);
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        return query;
    }
    
    public static int getEquivalentActionType(final String action_name) {
        if (action_name.equals("shutdown") || action_name.equals("ShutDownDevice")) {
            return 0;
        }
        if (action_name.equals("restart") || action_name.equals("RestartDevice")) {
            return 1;
        }
        if (action_name.equals("clear_app_data") || action_name.equals("ClearAppData")) {
            return 2;
        }
        return -1;
    }
    
    public abstract Boolean isCommandApplicable(final JSONObject p0, final String p1);
    
    public static boolean validateDeviceAction(final String commandName, final Device device, final JSONObject message) {
        final Long customerId = message.getLong("CUSTOMER_ID");
        final InventoryActionList inventoryActionList = InvActionUtilProvider.getInvActionUtil(device.getPlatformType()).getApplicableActions(device, customerId);
        boolean isValid = false;
        List actionList = inventoryActionList.actions;
        if (actionList != null) {
            final DeviceCommandWrapper deviceCommandWrapper = new DeviceCommandWrapper();
            for (final Object obj : actionList) {
                final InventoryAction inventoryAction = (InventoryAction)obj;
                if (deviceCommandWrapper.getEquivalentCommandName(inventoryAction.name).equalsIgnoreCase(commandName) && inventoryAction.isEnabled) {
                    isValid = true;
                }
            }
        }
        actionList = inventoryActionList.knoxActions;
        if (actionList != null) {
            final ContainerCommandWrapper containerCommandWrapper = new ContainerCommandWrapper();
            for (final Object obj : actionList) {
                final InventoryAction inventoryAction = (InventoryAction)obj;
                if (containerCommandWrapper.getEquivalentCommandName(inventoryAction.name).equalsIgnoreCase(commandName) && inventoryAction.isEnabled) {
                    isValid = true;
                }
            }
        }
        if (!isValid && commandName.equalsIgnoreCase("EnableLostMode") && new LostModeDataHandler().isDeviceInLostMode(device.getResourceId())) {
            isValid = true;
        }
        return isValid;
    }
    
    static {
        InvActionUtil.logger = Logger.getLogger("InventoryLogger");
        (InvActionUtil.actionMap = new HashMap<String, String>()).put("scan", "scan");
        InvActionUtil.actionMap.put("lock", "DeviceLock");
        InvActionUtil.actionMap.put("remote_control", "RemoteSession");
        InvActionUtil.actionMap.put("remote_view", "RemoteSession");
        InvActionUtil.actionMap.put("remote_alarm", "DeviceRing");
        InvActionUtil.actionMap.put("complete_wipe", "EraseDevice");
        InvActionUtil.actionMap.put("corporate_wipe", "CorporateWipe");
        InvActionUtil.actionMap.put("clear_passcode", "ClearPasscode");
        InvActionUtil.actionMap.put("reset_passcode", "ResetPasscode");
        InvActionUtil.actionMap.put("remote_debug", "RemoteDebug");
        InvActionUtil.actionMap.put("fetch_location", "GetLocation");
        InvActionUtil.actionMap.put("shutdown", "ShutDownDevice");
        InvActionUtil.actionMap.put("restart", "RestartDevice");
        InvActionUtil.actionMap.put("enable_lost_mode", "EnableLostMode");
        InvActionUtil.actionMap.put("disable_lost_mode", "DisableLostMode");
        InvActionUtil.actionMap.put("pause_kiosk", "PauseKioskCommand");
        InvActionUtil.actionMap.put("re_apply_kiosk", "ResumeKioskCommand");
        InvActionUtil.actionMap.put("unlock_user_account", "UnlockUserAccount");
        InvActionUtil.actionMap.put("rotate_filevault_personal_key", "MacFileVaultPersonalKeyRotate");
        InvActionUtil.actionMap.put("clear_app_data", "ClearAppData");
        InvActionUtil.actionMap.put("create_container", "ActivateKnox");
        InvActionUtil.actionMap.put("remove_container", "DeactivateKnox");
        InvActionUtil.actionMap.put("lock_container", "ContainerLock");
        InvActionUtil.actionMap.put("unlock_container", "ContainerUnlock");
        InvActionUtil.actionMap.put("clear_container_password", "ClearContainerPasscode");
        InvActionUtil.actionMap.put("logout_user", "LogOutUser");
    }
}
