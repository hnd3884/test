package com.me.mdm.server.profiles;

import java.util.HashMap;
import java.util.List;

public class WindowsPhoneProfileDistributionListHandler extends ProfileDistributionListHandler
{
    public WindowsPhoneProfileDistributionListHandler() {
        this.platformType = 3;
    }
    
    @Override
    public HashMap getRemainingLicenseCountMap(final Long customerId, final List businessStoreIDList) throws Exception {
        return new HashMap();
    }
    
    @Override
    public HashMap getLicensesAssociatedToGroupsMap(final List groupResourceIds, final long customerId, final List businessStoreIDList) throws Exception {
        return new HashMap();
    }
    
    @Override
    public HashMap getLicensesAssociatedToResourcesMap(final List resourceIds, final long customerId, final List businessStoreIDList) throws Exception {
        return new HashMap();
    }
}
