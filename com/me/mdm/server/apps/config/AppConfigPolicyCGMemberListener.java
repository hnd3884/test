package com.me.mdm.server.apps.config;

import com.adventnet.sym.server.mdm.config.ProfileDistributionCGMemberListener;

public class AppConfigPolicyCGMemberListener extends ProfileDistributionCGMemberListener
{
    public int getProfileType() {
        return 10;
    }
    
    public String getCommandName(final Boolean isMemberAdded) {
        return isMemberAdded ? "InstallApplicationConfiguration" : "RemoveApplicationConfiguration";
    }
}
