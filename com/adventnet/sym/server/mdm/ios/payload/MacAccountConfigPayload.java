package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import java.util.logging.Logger;

public class MacAccountConfigPayload extends IOSCommandPayload
{
    public static Logger logger;
    
    public void setSkipUserAccountCreation(final Boolean value) {
        this.getCommandDict().put("SkipPrimarySetupAccountCreation", (Object)value);
    }
    
    public void setCreateRegularAccount(final Boolean value) {
        this.getCommandDict().put("SetPrimarySetupAccountAsRegularUser", (Object)value);
    }
    
    public void setAutoSetupAdminAccounts(final NSArray value) {
        this.getCommandDict().put("AutoSetupAdminAccounts", (NSObject)value);
    }
    
    static {
        MacAccountConfigPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
