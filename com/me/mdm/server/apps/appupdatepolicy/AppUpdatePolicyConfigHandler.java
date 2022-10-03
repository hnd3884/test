package com.me.mdm.server.apps.appupdatepolicy;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;

public class AppUpdatePolicyConfigHandler extends MDMConfigHandler
{
    @Override
    public Properties persistCollection(final Properties collectionProps) {
        try {
            AppUpdatePolicyConfigHandler.logger.log(Level.FINE, " App update policy config persistCollection(): collectionProps: ", collectionProps);
            final Long collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            Long profileID = ((Hashtable<K, Long>)collectionProps).get("PROFILE_ID");
            final int platformType = ((Hashtable<K, Integer>)collectionProps).get("PLATFORM_TYPE");
            final Long modifiedUserID = ((Hashtable<K, Long>)collectionProps).get("LAST_MODIFIED_BY");
            final Boolean isMovedToTrash = ((Hashtable<K, Boolean>)collectionProps).get("IS_MOVED_TO_TRASH");
            final JSONObject profileMap = new JSONObject();
            profileMap.put("PROFILE_ID", (Object)profileID);
            profileMap.put("LAST_MODIFIED_BY", (Object)modifiedUserID);
            if (isMovedToTrash != null) {
                profileMap.put("IS_MOVED_TO_TRASH", (Object)isMovedToTrash);
            }
            profileID = ProfileHandler.addOrUpdateProfile(profileMap);
            ProfileHandler.addOrUpdateRecentPublishedProfileToCollection(profileID, collectionID);
            ProfileHandler.addOrUpdateProfileCollectionStatus(collectionID, 110);
            AppUpdatePolicyConfigHandler.logger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\t{4};", new Object[] { profileID, collectionID, "App Update policy Config Profile", platformType, "Profile Published" });
        }
        catch (final Exception ex) {
            AppUpdatePolicyConfigHandler.logger.log(Level.SEVERE, "Exception in persist collection of app configuration handler", ex);
        }
        return collectionProps;
    }
}
