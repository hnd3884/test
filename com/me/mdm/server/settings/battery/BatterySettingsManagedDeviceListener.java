package com.me.mdm.server.settings.battery;

import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsHandler;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.HashSet;
import com.me.mdm.api.inventory.FeatureSettingConstants;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsDBHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class BatterySettingsManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        try {
            Logger.getLogger(BatterySettingsManagedDeviceListener.class.getName()).log(Level.INFO, "Checking whether battery configuration is needed for the maanged device");
            final Long resourceID = deviceEvent.resourceID;
            final Long customerID = deviceEvent.customerID;
            final List<Long> groupID = MDMEnrollmentRequestHandler.getInstance().getGroupEnrollmentId(deviceEvent.enrollmentRequestId);
            final JSONObject jsonObject = MDMFeatureSettingsDBHandler.getFeatureDetails(1, customerID);
            final boolean isFeatureEnabled = jsonObject.getBoolean(FeatureSettingConstants.Api.Key.is_enabled);
            if (isFeatureEnabled) {
                Logger.getLogger(BatterySettingsManagedDeviceListener.class.getName()).log(Level.INFO, "Feature enabled overall");
                if (groupID == null || groupID.isEmpty()) {
                    boolean isFeatureEnabledForDevice = false;
                    final boolean applyToAll = jsonObject.getBoolean(FeatureSettingConstants.Api.Key.apply_to_all);
                    final JSONArray groupArr = jsonObject.getJSONArray(FeatureSettingConstants.Api.Key.groups);
                    final Set<Long> devices = MDMFeatureSettingsDBHandler.getDevicesFromGroupArr(groupArr);
                    if ((!applyToAll && devices.contains(resourceID)) || (applyToAll && !devices.contains(resourceID))) {
                        isFeatureEnabledForDevice = true;
                    }
                    if (isFeatureEnabledForDevice) {
                        Logger.getLogger(BatterySettingsManagedDeviceListener.class.getName()).log(Level.INFO, "battery feature enabled overall");
                        this.handleBatteryCommands(resourceID);
                    }
                }
                else {
                    boolean isGroupIncluded = false;
                    final Set groups = new HashSet();
                    final JSONArray jsonArray = jsonObject.getJSONArray(FeatureSettingConstants.Api.Key.groups);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        groups.add(jsonArray.getLong(i));
                    }
                    if (groups.contains(groupID)) {
                        isGroupIncluded = true;
                    }
                    if (isGroupIncluded) {
                        Logger.getLogger(BatterySettingsManagedDeviceListener.class.getName()).log(Level.INFO, "battery feature is applicable for the device");
                        this.handleBatteryCommands(resourceID);
                    }
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(BatterySettingsManagedDeviceListener.class.getName()).log(Level.WARNING, "Exception while handling BatterySettingsManagedDeviceListener", e);
        }
    }
    
    private void handleBatteryCommands(final Long resourceID) throws Exception {
        final HashSet<Long> devicesSet = new HashSet<Long>();
        devicesSet.add(resourceID);
        final DataObject dataObject = MDMFeatureSettingsHandler.getMdDeviceDo(devicesSet);
        MDMFeatureSettingsHandler.addFeatureSettingsCommandsToCommandRepo(dataObject);
    }
}
