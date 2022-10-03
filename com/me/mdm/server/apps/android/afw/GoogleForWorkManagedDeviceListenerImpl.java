package com.me.mdm.server.apps.android.afw;

import java.util.Map;
import org.json.JSONObject;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import java.util.HashMap;
import com.me.mdm.server.apps.android.afw.appmgmt.PlayStoreAppDistributionRequestHandler;
import java.util.List;
import com.me.mdm.server.apps.android.afw.appmgmt.AdvPlayStoreAppDistributionHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.target.AFWMigrationDataUpdateManager;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class GoogleForWorkManagedDeviceListenerImpl extends ManagedDeviceListener
{
    @Override
    public void deviceRegistered(final DeviceEvent userEvent) {
        if (userEvent.platformType == 2) {
            GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "GoogleForWorkManagedDeviceListenerImpl starts :: deviceRegistered");
            try {
                if (GoogleForWorkSettings.isEMMTypeAFWConfigured(userEvent.customerID)) {
                    GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "Handling managed account addition");
                    if (!userEvent.resourceJSON.optBoolean("IsMigrated", false)) {
                        new GoogleManagedAccountHandler().sendAFWAccountAdditionCmdForNewDevice(userEvent.resourceID, userEvent.customerID, userEvent.udid);
                    }
                    else {
                        GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "Handling migration of AFW users from previous server {0}", new Object[] { userEvent.udid });
                        new AFWMigrationDataUpdateManager().handleMigradedDeviceAccount(userEvent);
                    }
                }
            }
            catch (final Exception e) {
                Logger.getLogger("MDMLogger").log(Level.SEVERE, "GoogleForWorkManagedDeviceListenerImpl deviceRegistered() error ", e);
            }
        }
        GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "GoogleForWorkManagedDeviceListenerImpl completed");
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent userEvent) {
        if (userEvent.platformType == 2) {
            try {
                GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "GoogleForWorkManagedDeviceListenerImpl starts :: devicePreDelete");
                if (!userEvent.resourceJSON.optBoolean("IsMigrated", false)) {
                    if (GoogleForWorkSettings.isAFWSettingsConfigured(userEvent.customerID)) {
                        final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(userEvent.customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW);
                        if (playstoreDetails.getInt("ENTERPRISE_TYPE") == GoogleForWorkSettings.ENTERPRISE_TYPE_EMM) {
                            final List collectionList = AppsUtil.getInstance().getCollectionForResource(userEvent.resourceID, -1, null);
                            GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "Total number of collections associated with resource {0} : {1}", new Object[] { userEvent.resourceID, collectionList.size() });
                            final ArrayList resourceList = new ArrayList();
                            resourceList.add(userEvent.resourceID);
                            final Long customerId = userEvent.customerID;
                            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                                final AdvPlayStoreAppDistributionHandler dist = new AdvPlayStoreAppDistributionHandler();
                                dist.initialize(customerId, playstoreDetails.getLong("BUSINESSSTORE_ID"));
                                dist.removeAppsByDevices(resourceList, collectionList);
                            }
                            else {
                                final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playstoreDetails);
                                final PlayStoreAppDistributionRequestHandler dist2 = new PlayStoreAppDistributionRequestHandler();
                                final Map<Long, JSONObject> portalAppDetails = AppsUtil.getInstance().getPortalAppDetails(collectionList);
                                dist2.disAssociateAppsByUsers(resourceList, portalAppDetails, ebs, playstoreDetails.getLong("BUSINESSSTORE_ID"), null);
                            }
                            new StoreAccountManagementHandler().deleteStoreUserForDevice(customerId, resourceList);
                        }
                        else {
                            new GoogleAccountChangeHandler().removeAccountOnUnmanage(userEvent.resourceID);
                        }
                    }
                }
                else {
                    GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "Device is migrated to new server, so not removing AFW apps. UDID {0}", userEvent.udid);
                }
                GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "GoogleForWorkManagedDeviceListenerImpl ends :: devicePreDelete");
            }
            catch (final Exception ex) {
                Logger.getLogger("MDMLogger").log(Level.SEVERE, "GoogleForWorkManagedDeviceListenerImpl devicePreDelete() error ", ex);
            }
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
        if (userEvent.platformType == 2) {
            GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "GoogleForWorkManagedDeviceListenerImpl starts :: deviceUnmanaged");
            if (!userEvent.resourceJSON.optBoolean("IsMigrated", false)) {
                try {
                    new GoogleAccountChangeHandler().removeAccountOnUnmanage(userEvent.resourceID);
                }
                catch (final Exception ex) {
                    Logger.getLogger("MDMLogger").log(Level.SEVERE, "GoogleForWorkManagedDeviceListenerImpl deviceUnmanaged() error ", ex);
                }
            }
            else {
                Logger.getLogger("MDMLogger").log(Level.INFO, "Device is migrated to new server, so not removing managed account. UDID {0}", userEvent.udid);
            }
            GoogleForWorkManagedDeviceListenerImpl.mdmlogger.log(Level.INFO, "GoogleForWorkManagedDeviceListenerImpl ends :: deviceUnmanaged");
        }
    }
}
