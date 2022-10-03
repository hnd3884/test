package com.me.mdm.server.apple.command.response.responseprocessor;

import com.dd.plist.NSArray;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.apple.objects.AppleUserAccount;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class AppleUserListResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    public Logger mdmLogger;
    
    public AppleUserListResponseProcessor() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final JSONObject response = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        final String strData = params.optString("strData");
        final HashMap responseHashMap = PlistWrapper.getInstance().getHashFromPlist(strData);
        if (responseHashMap.get("Status").equals("Acknowledged")) {
            final List<AppleUserAccount> appleUserAccountList = new ArrayList<AppleUserAccount>();
            final NSArray userArray = PlistWrapper.getInstance().getArrayForKey("Users", strData);
            for (int i = 0; i < userArray.count(); ++i) {
                final NSDictionary userDict = (NSDictionary)userArray.objectAtIndex(i);
                appleUserAccountList.add(new AppleUserAccount(userDict));
            }
            AppleMultiUserUtils.populateAppleUserAccountsForResource(resourceID, appleUserAccountList);
        }
        else {
            this.mdmLogger.log(Level.WARNING, "UserList status is error for resourceID:{0}", resourceID);
        }
        return response;
    }
}
