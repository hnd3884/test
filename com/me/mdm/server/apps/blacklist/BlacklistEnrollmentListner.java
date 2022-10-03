package com.me.mdm.server.apps.blacklist;

import com.adventnet.persistence.DataAccessException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class BlacklistEnrollmentListner extends ManagedDeviceListener
{
    private Logger logger;
    
    public BlacklistEnrollmentListner() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void userAssigned(final DeviceEvent userEvent) {
        Long oldUserId = null;
        try {
            oldUserId = (Long)userEvent.resourceJSON.get("oldUserId");
            final List resourceList = Arrays.asList(userEvent.resourceID);
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final List addedProfileForManagedUser = handler.getAddedProfileForManagedUser(oldUserId, 4);
            HashMap profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileForManagedUser);
            final HashMap profileProperties = new HashMap();
            for (final Object profileObj : addedProfileForManagedUser) {
                final HashMap profileMap = (HashMap)profileObj;
                final HashMap curProfileProps = new HashMap();
                final Long profileId = profileMap.get("profileId");
                final Long userID = profileMap.get("associatedByUser");
                final String associatedUsername = profileMap.get("associatedByUserName");
                curProfileProps.put("associatedByUserName", associatedUsername);
                curProfileProps.put("associatedByUser", userID);
                profileProperties.put(profileId, curProfileProps);
            }
            final HashMap params = new HashMap();
            params.put("profileCollectionMap", profileCollectionMap);
            params.put("resourceIDs", resourceList);
            params.put("CUSTOMER_ID", userEvent.customerID);
            params.put("profileProperties", profileProperties);
            params.put("profileOriginInt", 2);
            this.logger.log(Level.INFO, "Apps blacklisting removal after user assignment resource id : {0}, profile collection map; {1}", new Object[] { resourceList, profileCollectionMap });
            try {
                if (!profileCollectionMap.isEmpty()) {
                    new DeviceBlacklistHandler().removeBlacklistAppInResource(params);
                }
                final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(userEvent.resourceID).get("MANAGED_USER_ID");
                final List addedProfileManagedUserList = handler.getAddedProfileForManagedUser(userId, 4);
                profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileManagedUserList);
                params.put("profileCollectionMap", profileCollectionMap);
                if (!profileCollectionMap.isEmpty()) {
                    new DeviceBlacklistHandler().blacklistAppInResource(params);
                }
                this.logger.log(Level.INFO, "Apps blacklisting  after user assignment resource id : {0}, profile collection map; {1}", new Object[] { resourceList, profileCollectionMap });
            }
            catch (final Exception e) {
                this.logger.log(Level.INFO, "Exception in blacklist app handling", e);
            }
        }
        catch (final JSONException e2) {
            this.logger.log(Level.INFO, "Exception in blacklist app handling", (Throwable)e2);
        }
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        super.deviceManaged(deviceEvent);
        try {
            final HashMap hashMap = new BlacklistAppHandler().getNetworkLevelBlacklistedApps(deviceEvent.customerID, deviceEvent.platformType);
            HashMap profileCollectionMap = hashMap.get("profileCollectionMap");
            final HashMap profileProperties = hashMap.get("profileProperties");
            if (!profileCollectionMap.isEmpty()) {
                final HashMap params = new HashMap();
                params.put("profileCollectionMap", profileCollectionMap);
                final List resList = new ArrayList();
                resList.add(deviceEvent.resourceID);
                params.put("resourceIDs", resList);
                params.put("CUSTOMER_ID", deviceEvent.customerID);
                params.put("profileProperties", profileProperties);
                this.logger.log(Level.INFO, "Apps blacklisting after enwollment resource id : {0}, profile collection map; {1}", new Object[] { resList, profileCollectionMap });
                new DeviceBlacklistHandler().blacklistAppInResource(params);
            }
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceEvent.resourceID).get("MANAGED_USER_ID");
            final List addedProfileManagedUserList = handler.getAddedProfileForManagedUser(userId, 4);
            profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileManagedUserList);
            final HashMap params2 = new HashMap();
            for (final Object profileObj : addedProfileManagedUserList) {
                final HashMap profileMap = (HashMap)profileObj;
                final HashMap curProfileProps = new HashMap();
                final Long profileId = profileMap.get("profileId");
                final Long userID = profileMap.get("associatedByUser");
                final String associatedUsername = profileMap.get("associatedByUserName");
                curProfileProps.put("associatedByUserName", associatedUsername);
                curProfileProps.put("associatedByUser", userID);
                profileProperties.put(profileId, curProfileProps);
            }
            if (!profileCollectionMap.isEmpty()) {
                params2.put("profileCollectionMap", profileCollectionMap);
                final List resList2 = new ArrayList();
                resList2.add(deviceEvent.resourceID);
                params2.put("resourceIDs", resList2);
                params2.put("CUSTOMER_ID", deviceEvent.customerID);
                params2.put("profileProperties", profileProperties);
                this.logger.log(Level.INFO, "Apps blacklisting after enwollment resource id : {0}, profile collection map; {1}", new Object[] { resList2, profileCollectionMap });
                new DeviceBlacklistHandler().blacklistAppInResource(params2);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Error during blacklisting app via enrollment listner", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Error during blacklisting app via enrollment listner", e2);
        }
    }
}
