package com.me.mdm.server.inv.actions;

import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONArray;
import java.util.Set;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.inv.actions.resource.InventoryAction;
import java.util.ArrayList;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import com.me.mdm.server.device.resource.Device;

public class ChromeInvActionUtil extends InvActionUtil
{
    @Override
    public InventoryActionList getApplicableActions(final Device device, final Long customerID) {
        final int lostModeStatus = new LostModeDataHandler().getLostModeStatus(device.getResourceId());
        final InventoryActionList actionList = new InventoryActionList();
        final List<InventoryAction> actions = new ArrayList<InventoryAction>();
        final boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
        final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
        actionList.actions = actions;
        try {
            for (final String action : ActionConstants.ACTIONS_LIST) {
                final InventoryAction tempAction = new InventoryAction();
                final String s = action;
                switch (s) {
                    case "scan": {
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                    case "enable_lost_mode": {
                        if (isGeotrackingFeatureEnabled && lostModeStatus != 2 && lostModeStatus != 1) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "disable_lost_mode": {
                        if (isGeotrackingFeatureEnabled && (lostModeStatus == 2 || lostModeStatus == 1)) {
                            tempAction.name = action;
                            tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                            tempAction.isEnabled = true;
                            tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                            tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                            tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                            actions.add(tempAction);
                            break;
                        }
                        break;
                    }
                    case "restart": {
                        tempAction.name = action;
                        tempAction.localizedName = InvActionUtil.getLocalizedString(this.getI18NKeyForAction(action));
                        tempAction.isEnabled = true;
                        tempAction.statusCode = DeviceCommandRepository.getInstance().getDeviceCommandStatus(device.getResourceId(), InvActionUtil.getEquivalentCommandName(action));
                        tempAction.statusDescription = InvActionUtil.getStatusDescription(tempAction.statusCode);
                        tempAction.localizedStatusDescription = InvActionUtil.getLocalizedStatusDescription(tempAction.statusCode);
                        actions.add(tempAction);
                        break;
                    }
                }
            }
        }
        catch (final Exception e) {
            ChromeInvActionUtil.logger.log(Level.SEVERE, "Exception occured in ChromeInvActionUtil", e);
        }
        return actionList;
    }
    
    @Override
    public JSONArray getApplicableBulkActionDevices(final Set deviceSet, final String commandName, final Long customerID) {
        final boolean isGeotrackingFeatureEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
        JSONArray validBulkDeviceArray = new JSONArray();
        final SelectQuery deviceQuery = this.getBulkDeviceQuery(deviceSet, customerID, 4);
        switch (commandName) {
            case "enable_lost_mode": {
                if (isGeotrackingFeatureEnabled) {
                    final Criteria statusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 2, 1 }, 9);
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(statusCriteria));
                }
            }
            case "disable_lost_mode": {
                if (isGeotrackingFeatureEnabled) {
                    final Criteria statusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 2, 1 }, 8);
                    deviceQuery.setCriteria(deviceQuery.getCriteria().and(statusCriteria));
                    break;
                }
                break;
            }
            case "restart": {
                break;
            }
            default: {
                return validBulkDeviceArray;
            }
        }
        MDMUtil.getInstance();
        validBulkDeviceArray = MDMUtil.executeSelectQuery(deviceQuery);
        return validBulkDeviceArray;
    }
    
    @Override
    public Boolean isCommandApplicable(final JSONObject deviceDatils, final String commandName) {
        try {
            switch (commandName) {
                case "restart": {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            ChromeInvActionUtil.logger.log(Level.SEVERE, "Exception in checking isCommandApplicable", ex);
            return false;
        }
    }
}
