package com.adventnet.sym.server.mdm.macos.event;

import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MacUserLoginInventoryUpdateListener implements ComputerUserLoginListener
{
    public Logger logger;
    
    public MacUserLoginInventoryUpdateListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void userLoggedInComputer(final ComputerUserLoginEvent loginEvent) {
        this.logger.log(Level.INFO, "Entering MacUserLoginInventoryUpdateListener.userChangedInComputer");
        final JSONObject userJSON = new JSONObject();
        try {
            userJSON.put("USER_ID", (Object)loginEvent.newUserID);
            userJSON.put("ORDER", 1);
            userJSON.put("RESOURCE_ID", (Object)loginEvent.resourceID);
            userJSON.put("USER_MANAGEMENT_TYPE", (loginEvent.newUserID == null) ? 2 : 1);
            userJSON.put("LOGON_USER_DISPLAY_NAME", (Object)loginEvent.displayNameInEvent);
            userJSON.put("LOGON_USER_NAME", (Object)loginEvent.userNameInEvent);
            userJSON.put("LOGIN_TIME", (Object)loginEvent.loginEventTime);
            final JSONArray userArray = new JSONArray();
            userArray.put((Object)userJSON);
            AppleMultiUserUtils.addNewUserOnLoginEvent(loginEvent.resourceID, userArray);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacUserLoginInventoryUpdateListener Exception in addOrUpdateMDDeviceRecentUsersInfo", ex);
        }
        this.logger.log(Level.INFO, "Exiting MacUserLoginInventoryUpdateListener.userChangedInComputer");
    }
}
