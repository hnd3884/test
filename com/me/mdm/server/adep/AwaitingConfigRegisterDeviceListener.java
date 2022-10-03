package com.me.mdm.server.adep;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.mdm.server.deviceaccounts.AccountDetailsHandler;
import com.me.mdm.server.adep.mac.AccountConfiguration;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import org.json.JSONObject;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class AwaitingConfigRegisterDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    
    public AwaitingConfigRegisterDeviceListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    private void associateCollectionToResource(final Long customerID, final Long collectionID, final Long resourceID) throws Exception {
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceID);
            final ProfileHandler handler = new ProfileHandler();
            final Long profileID = handler.getProfileIDFromCollectionID(collectionID);
            final Map profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            final JSONObject createdUserDetailsJSON = ProfileUtil.getCreatedUserDetailsForProfile(profileID);
            final Properties properties = new Properties();
            ((Hashtable<String, Map>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, String>)properties).put("commandName", "InstallProfile");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Integer>)properties).put("platformtype", 1);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Object>)properties).put("loggedOnUserName", createdUserDetailsJSON.get("FIRST_NAME"));
            ((Hashtable<String, Object>)properties).put("loggedOnUser", createdUserDetailsJSON.get("USER_ID"));
            ((Hashtable<String, Object>)properties).put("UserId", createdUserDetailsJSON.get("USER_ID"));
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Failed to associate the Account Configuration collection with resource: " + String.valueOf(n));
            throw e;
        }
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        try {
            if (deviceEvent.platformType != 1) {
                return;
            }
            this.logger.log(Level.INFO, "START: Awaiting configuration Listener");
            final Long resourceID = deviceEvent.resourceID;
            final DataObject accountDO = AccountConfiguration.getInstance().getAccountConfigIDToResourceIDDO(resourceID);
            if (accountDO.isEmpty()) {
                this.logger.log(Level.INFO, "No account configuration associated with this device so ending listener");
                return;
            }
            final Row templateRow = accountDO.getFirstRow("DEPEnrollmentTemplate");
            final Boolean isAwaitingConfig = (Boolean)templateRow.get("ENABLE_AWAIT_CONFIG");
            final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
            logJSON.put((Object)"RESOURCE_ID", (Object)resourceID);
            if (isAwaitingConfig) {
                final Row accountConfigRow = accountDO.getFirstRow("AccountConfigToDEPEnroll");
                final Long accountConfigID = (Long)accountConfigRow.get("ACCOUNT_CONFIG_ID");
                try {
                    if (accountConfigID != null) {
                        final AccountDetailsHandler handler = new AccountDetailsHandler();
                        final Long collectionID = handler.getCollectionIDForAccountConfig(accountConfigID);
                        this.associateCollectionToResource(deviceEvent.customerID, collectionID, resourceID);
                        handler.addOrUpdateAccountConfigToResource(accountConfigID, resourceID, 3);
                    }
                    logJSON.put((Object)"REMARKS", (Object)"account-config-success");
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, e, () -> "Failed during collection association in awaiting configuration Listener for " + String.valueOf(n));
                    logJSON.put((Object)"REMARKS", (Object)"account-config-failed");
                }
            }
            this.logger.log(Level.INFO, "END: Awaiting configuration Listener");
            MDMOneLineLogger.log(Level.INFO, "ACCOUNT_CONFIG", logJSON);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception in Awaiting configuration Listener!!!", e2);
        }
    }
}
