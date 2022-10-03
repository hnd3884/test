package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.apps.businessstore.StoreInterface;
import java.util.logging.Level;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import java.util.Iterator;
import java.util.Collection;
import org.json.JSONObject;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DeviceAppsLicensesHandlingListener implements AppsLicensesHandlingListener
{
    private Logger logger;
    
    public DeviceAppsLicensesHandlingListener() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void appLicenseDirectRemoval(final AppsLicensesHandlerEvent appsLicensesHandlerEvent) {
        final Properties licenseDetails = appsLicensesHandlerEvent.getLicenseDetails();
        if (licenseDetails != null && !licenseDetails.isEmpty()) {
            List resourceList = new ArrayList();
            List configSourceList = new ArrayList();
            List profileList = new ArrayList();
            if (licenseDetails.containsKey("resourceList")) {
                resourceList = ((Hashtable<K, List>)licenseDetails).get("resourceList");
            }
            if (licenseDetails.containsKey("configSourceList")) {
                configSourceList = ((Hashtable<K, List>)licenseDetails).get("configSourceList");
            }
            if (licenseDetails.containsKey("profileList")) {
                profileList = ((Hashtable<K, List>)licenseDetails).get("profileList");
            }
            final JSONObject platformToProfileMap = ProfileUtil.getInstance().getPlatformToProfileMap(profileList);
            this.removeLicenseForApps(resourceList, configSourceList, appsLicensesHandlerEvent.getCustomerID(), platformToProfileMap);
        }
    }
    
    @Override
    public void appReassignedLicenseRemoval(final AppsLicensesHandlerEvent appsLicensesHandlerEvent) {
        final Properties licenseDetails = appsLicensesHandlerEvent.getLicenseDetails();
        final Long customerID = appsLicensesHandlerEvent.getCustomerID();
        if (licenseDetails != null && !licenseDetails.isEmpty()) {
            Properties profileToBusinessStore = new Properties();
            List configSourceList = new ArrayList();
            if (licenseDetails.containsKey("profileToBusinessStore")) {
                profileToBusinessStore = ((Hashtable<K, Properties>)licenseDetails).get("profileToBusinessStore");
            }
            if (licenseDetails.containsKey("configSourceList")) {
                configSourceList = ((Hashtable<K, List>)licenseDetails).get("configSourceList");
            }
            final List profileList = new ArrayList();
            profileList.addAll(profileToBusinessStore.keySet());
            final JSONObject platformToProfileMap = ProfileUtil.getInstance().getPlatformToProfileMap(profileList);
            final Iterator iter = platformToProfileMap.keySet().iterator();
            while (iter.hasNext()) {
                final int platformType = Integer.parseInt(iter.next().toString());
                if (platformType == 1) {
                    final List tempProfileList = platformToProfileMap.getJSONArray(String.valueOf(platformType)).toList();
                    this.handleReAssigningOfBusinessStoreForProfile(configSourceList, profileToBusinessStore, tempProfileList, platformType, customerID);
                }
            }
        }
    }
    
    public void removeLicenseForApps(final List<Long> resourceList, final List<Long> configSourceList, final Long customerID, final JSONObject platformToProfileMap) {
        try {
            final Iterator iter = platformToProfileMap.keySet().iterator();
            while (iter.hasNext()) {
                final int platformType = Integer.parseInt(iter.next().toString());
                if (platformType == 1) {
                    final List profileList = platformToProfileMap.getJSONArray(String.valueOf(platformType)).toList();
                    final StoreInterface storeInterface = MDBusinessStoreUtil.getInstance(platformType, customerID);
                    final JSONObject appToDeviceLicenseDetails = new MDMResourceToProfileDeploymentConfigHandler().getAppLicenseDetailsForResources(resourceList, configSourceList, profileList, 1);
                    storeInterface.addLicenseRemovalTaskToQueue(appToDeviceLicenseDetails, customerID, configSourceList, Boolean.FALSE);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in removeLicenseForApps");
        }
    }
    
    public void handleReAssigningOfBusinessStoreForProfile(final List configSourceList, final Properties profileToBusinessStore, final List profileList, final int platformType, final Long customerID) {
        try {
            this.logger.log(Level.INFO, "Beginning to check if current location associations is different from existing ones. ProfileToBusinessStore: {0}, platform: {1}, profileList: {2}", new Object[] { profileToBusinessStore, platformType, profileList });
            if (profileToBusinessStore != null && !profileToBusinessStore.isEmpty() && profileList != null && !profileList.isEmpty()) {
                final JSONObject appToDeviceLicenseDetails = new JSONObject();
                final Iterator configSrcItr = configSourceList.iterator();
                final DataObject dataObject = new MDMResourceToProfileDeploymentConfigHandler().getAppsDeploymentConfigDO(null, configSourceList, profileList, platformType);
                while (configSrcItr.hasNext()) {
                    final Long sourceID = configSrcItr.next();
                    final Criteria configSourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)sourceID, 0);
                    final Iterator depConfigRows = dataObject.getRows("MDMResourceToDeploymentConfigs", configSourceCriteria);
                    while (depConfigRows.hasNext()) {
                        final Row depConfigRow = depConfigRows.next();
                        final Long profileID = (Long)depConfigRow.get("PROFILE_ID");
                        final Long deviceID = (Long)depConfigRow.get("RESOURCE_ID");
                        final Long oldBusinessStoreID = (Long)depConfigRow.get("BUSINESSSTORE_ID");
                        final Long newBusinessStoreID = ((Hashtable<K, Long>)profileToBusinessStore).get(profileID);
                        if (!oldBusinessStoreID.equals(newBusinessStoreID)) {
                            JSONObject businessDetails = appToDeviceLicenseDetails.optJSONObject(String.valueOf(profileID));
                            if (businessDetails == null) {
                                businessDetails = new JSONObject();
                            }
                            JSONArray deviceListArray = businessDetails.optJSONArray(String.valueOf(oldBusinessStoreID));
                            if (deviceListArray == null) {
                                deviceListArray = new JSONArray();
                            }
                            if (!deviceListArray.toList().contains(deviceID)) {
                                deviceListArray.put((Object)deviceID);
                            }
                            businessDetails.put(String.valueOf(oldBusinessStoreID), (Object)deviceListArray);
                            appToDeviceLicenseDetails.put(String.valueOf(profileID), (Object)businessDetails);
                        }
                    }
                }
                final StoreInterface storeInterface = MDBusinessStoreUtil.getInstance(platformType, customerID);
                if (appToDeviceLicenseDetails != null && appToDeviceLicenseDetails.length() > 0) {
                    this.logger.log(Level.INFO, "Some apps are found re-assigned with different locations. Hence adding previous licenses removal task to queue. AppToDeviceLicenseDetails: {0}", new Object[] { appToDeviceLicenseDetails });
                    storeInterface.addLicenseRemovalTaskToQueue(appToDeviceLicenseDetails, customerID, configSourceList, Boolean.FALSE);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in handleReAssigningOfBusinessStoreForProfile", e);
        }
    }
}
