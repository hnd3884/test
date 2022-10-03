package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import java.util.logging.Logger;

public class AppUpdatePolicyProfilePublishHandler
{
    private static AppUpdatePolicyProfilePublishHandler appUpdatePolicyProfilePublishHandler;
    private Logger configLogger;
    
    public AppUpdatePolicyProfilePublishHandler() {
        this.configLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AppUpdatePolicyProfilePublishHandler getInstance() {
        if (AppUpdatePolicyProfilePublishHandler.appUpdatePolicyProfilePublishHandler == null) {
            AppUpdatePolicyProfilePublishHandler.appUpdatePolicyProfilePublishHandler = new AppUpdatePolicyProfilePublishHandler();
        }
        return AppUpdatePolicyProfilePublishHandler.appUpdatePolicyProfilePublishHandler;
    }
    
    public void publishAppUpdatePolicy(final AppUpdatePolicyModel appUpdatePolicyModel) throws Exception {
        this.configLogger.log(Level.INFO, "Publish app update policy profile {0} collection {1}", new Object[] { appUpdatePolicyModel.getProfileId(), appUpdatePolicyModel.getCollectionId() });
        final JSONObject profilePublishJSON = new JSONObject();
        profilePublishJSON.put("PROFILE_ID", (Object)appUpdatePolicyModel.getProfileId());
        profilePublishJSON.put("PLATFORM_TYPE", 0);
        profilePublishJSON.put("CUSTOMER_ID", (Object)appUpdatePolicyModel.getCustomerId());
        profilePublishJSON.put("COLLECTION_ID", (Object)appUpdatePolicyModel.getCollectionId());
        profilePublishJSON.put("PROFILE_TYPE", 0);
        profilePublishJSON.put("APP_CONFIG", false);
        profilePublishJSON.put("LAST_MODIFIED_BY", (Object)appUpdatePolicyModel.getUserId());
        profilePublishJSON.put("PROFILE_TYPE", 12);
        ProfileConfigHandler.publishProfile(profilePublishJSON);
    }
    
    static {
        AppUpdatePolicyProfilePublishHandler.appUpdatePolicyProfilePublishHandler = null;
    }
}
