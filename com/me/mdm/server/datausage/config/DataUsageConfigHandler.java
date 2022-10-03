package com.me.mdm.server.datausage.config;

import java.util.Hashtable;
import com.me.mdm.server.datausage.android.AndroidDataProfileHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;

public class DataUsageConfigHandler extends MDMConfigHandler
{
    public static final int[] PLATFORMS;
    
    @Override
    public Properties persistCollection(final Properties collectionProps) throws SyMException {
        Long collectionID = null;
        Long profileID = null;
        int platformType = -1;
        Long modifiedUserID = null;
        try {
            DataUsageConfigHandler.profileLogger.log(Level.FINE, "persistCollection(): datausage tracking collectionProps: ", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            profileID = ((Hashtable<K, Long>)collectionProps).get("PROFILE_ID");
            platformType = ((Hashtable<K, Integer>)collectionProps).get("PLATFORM_TYPE");
            modifiedUserID = ((Hashtable<K, Long>)collectionProps).get("LAST_MODIFIED_BY");
            final Boolean isMovedToTrash = ((Hashtable<K, Boolean>)collectionProps).get("IS_MOVED_TO_TRASH");
            final JSONObject profileMap = new JSONObject();
            profileMap.put("PROFILE_ID", (Object)profileID);
            profileMap.put("LAST_MODIFIED_BY", (Object)modifiedUserID);
            if (isMovedToTrash != null) {
                profileMap.put("IS_MOVED_TO_TRASH", (Object)isMovedToTrash);
            }
            profileID = ProfileHandler.addOrUpdateProfile(profileMap);
            ProfileHandler.addOrUpdateRecentPublishedProfileToCollection(profileID, collectionID);
            for (final int i : DataUsageConfigHandler.PLATFORMS) {
                final DataProfileHandler dataProfileHandler = this.getProfileHandler(i);
                if (dataProfileHandler != null) {
                    dataProfileHandler.publishProfileEntity(collectionProps);
                }
            }
            ProfileHandler.addOrUpdateProfileCollectionStatus(collectionID, 110);
            DataUsageConfigHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\t{4};", new Object[] { profileID, collectionID, "DataUsage", platformType, "PROFILE_PUBLISHED" });
        }
        catch (final Exception e) {
            DataUsageConfigHandler.profileLogger.log(Level.SEVERE, "Exception while creating Data Usage profile : ", e);
        }
        return collectionProps;
    }
    
    private DataProfileHandler getProfileHandler(final int platform) {
        if (platform == 2) {
            return new AndroidDataProfileHandler();
        }
        return null;
    }
    
    static {
        PLATFORMS = new int[] { 1, 2, 3, 4 };
    }
}
