package com.me.mdm.server.license;

import java.util.Hashtable;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.List;
import com.me.mdm.server.compliance.ComplianceHandler;
import com.me.mdm.server.updates.osupdates.task.OSUpdateStandardLicenseProfileRemovalTask;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.settings.location.LocationSettingsRequestHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMEditionLicenseChangeTask implements SchedulerExecutionInterface
{
    Logger logger;
    
    public MDMEditionLicenseChangeTask() {
        this.logger = Logger.getLogger("InventoryLogger");
    }
    
    public void executeTask(final Properties props) {
        try {
            this.logger.log(Level.INFO, "Executing MDMEditionLicenseChangeTask with props: {0}", props.toString());
            final Long userId = ((Hashtable<K, Long>)props).get("user_id");
            LocationSettingsRequestHandler.getInstance().disableLocationOnEditionChange();
            final List resList = ManagedDeviceHandler.getInstance().getNavtiveAppInstallediOSDevices();
            DeviceCommandRepository.getInstance().addMDMDefaultAppConfiguration(resList);
            DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(resList, 2);
            NotificationHandler.getInstance().SendNotification(resList, 1);
            final List androidList = ManagedDeviceHandler.getInstance().getAndroidManagedDeviceResourceIDs();
            DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(androidList);
            DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(androidList, 1);
            NotificationHandler.getInstance().SendNotification(androidList, 2);
            this.handleProfileRemovalForNonStandardFeatures(1, userId);
            this.handleProfileRemovalForNonStandardFeatures(8, userId);
            new OSUpdateStandardLicenseProfileRemovalTask().handleLicenseChangeForOSUpdate(true, userId);
            ComplianceHandler.getInstance().disassociateComplianceOnLicenseDowngrade(userId);
            this.logger.log(Level.INFO, "Finished executing MDMEditionLicenseChangeTask");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in MDMEditionLicenseChangeTask", e);
        }
    }
    
    private void handleProfileRemovalForNonStandardFeatures(final Integer profileType, final Long userId) {
        try {
            this.logger.log(Level.INFO, "Going to handle the license change for filevault");
            final List<Integer> configId = ProfileUtil.STANDARDLICENSE_NOTAPPLICABLE_CONFIG;
            final Criteria notTrashed = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            final JSONObject profileJSON = ProfileUtil.getInstance().getProfileIdsFromConfig(configId, notTrashed);
            final Iterator customerKeys = profileJSON.keys();
            while (customerKeys.hasNext()) {
                final List<Long> profileList = new ArrayList<Long>();
                final Long customerId = Long.parseLong(customerKeys.next());
                final JSONObject profileObject = profileJSON.getJSONObject(customerId.toString());
                final Iterator profileKeys = profileObject.keys();
                while (profileKeys.hasNext()) {
                    profileList.add(Long.parseLong(profileKeys.next()));
                }
                this.logger.log(Level.INFO, "Profile ids going to remove:{0}", new Object[] { profileList });
                ProfileUtil.getInstance().moveProfilesToTrash(profileList, customerId, profileType);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in filevault license handling", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in filevault license handling", e2);
        }
    }
}
