package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import org.json.JSONObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import java.util.logging.Logger;

public class AppUpdatePolicyProfileHandler
{
    private static AppUpdatePolicyProfileHandler appUpdatePolicyProfileHandler;
    private Logger logger;
    
    public AppUpdatePolicyProfileHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AppUpdatePolicyProfileHandler getInstance() {
        if (AppUpdatePolicyProfileHandler.appUpdatePolicyProfileHandler == null) {
            AppUpdatePolicyProfileHandler.appUpdatePolicyProfileHandler = new AppUpdatePolicyProfileHandler();
        }
        return AppUpdatePolicyProfileHandler.appUpdatePolicyProfileHandler;
    }
    
    public void addOrUpdateAppUpdatePolicyProfile(final AppUpdatePolicyModel appUpdatePolicyModel) throws Exception {
        final JSONObject profileJSON = new JSONObject();
        profileJSON.put("PROFILE_NAME", (Object)appUpdatePolicyModel.getPolicyName());
        profileJSON.put("PROFILE_ID", (Object)appUpdatePolicyModel.getProfileId());
        profileJSON.put("PLATFORM_TYPE", 0);
        profileJSON.put("PROFILE_TYPE", 12);
        profileJSON.put("PROFILE_DESCRIPTION", (Object)appUpdatePolicyModel.getDescription());
        profileJSON.put("CREATED_BY", (Object)appUpdatePolicyModel.getUserId());
        profileJSON.put("CUSTOMER_ID", (Object)appUpdatePolicyModel.getCustomerId());
        profileJSON.put("SECURITY_TYPE", -1);
        ProfileConfigHandler.addProfileCollection(profileJSON);
        final Long profileId = JSONUtil.optLongForUVH(profileJSON, "PROFILE_ID", Long.valueOf(-1L));
        final Long collectionId = JSONUtil.optLongForUVH(profileJSON, "COLLECTION_ID", Long.valueOf(-1L));
        this.logger.log(Level.INFO, "Picked Profile Id {0} collection {1} for app update policy", new Object[] { profileId, collectionId });
        appUpdatePolicyModel.setProfileId(profileId);
        appUpdatePolicyModel.setCollectionId(collectionId);
    }
    
    public void getAppUpdatePolicyProfile(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdateDO) throws DataAccessException {
        final Row profileRow = appUpdateDO.getFirstRow("Profile");
        final Row createdUserRow = appUpdateDO.getFirstRow("CREATED_BY_USER");
        final Row lastModifiedByUser = appUpdateDO.getFirstRow("LAST_MODIFIED_BY_USER");
        appUpdatePolicyModel.setProfileId((Long)profileRow.get("PROFILE_ID"));
        appUpdatePolicyModel.setPolicyName((String)profileRow.get("PROFILE_NAME"));
        appUpdatePolicyModel.setCreationTime((Long)profileRow.get("CREATION_TIME"));
        appUpdatePolicyModel.setModifiedTime((Long)profileRow.get("LAST_MODIFIED_TIME"));
        appUpdatePolicyModel.setDescription((String)profileRow.get("PROFILE_DESCRIPTION"));
        if (createdUserRow != null) {
            appUpdatePolicyModel.setCreatedUser((String)createdUserRow.get("FIRST_NAME"));
        }
        if (lastModifiedByUser != null) {
            appUpdatePolicyModel.setModifiedUser((String)lastModifiedByUser.get("FIRST_NAME"));
        }
    }
    
    static {
        AppUpdatePolicyProfileHandler.appUpdatePolicyProfileHandler = null;
    }
}
