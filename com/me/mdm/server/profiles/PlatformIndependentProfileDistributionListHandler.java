package com.me.mdm.server.profiles;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class PlatformIndependentProfileDistributionListHandler extends ProfileDistributionListHandler
{
    public PlatformIndependentProfileDistributionListHandler() {
        this.platformType = 0;
    }
    
    @Override
    public HashMap getRemainingLicenseCountMap(final Long customerId, final List businessStoreIDList) throws Exception {
        final HashMap iosMap = new IOSProfileDistributionListHandler().getRemainingLicenseCountMap(customerId, businessStoreIDList);
        final HashMap androidMap = new AndroidProfileDistributionListHandler().getRemainingLicenseCountMap(customerId, businessStoreIDList);
        iosMap.putAll(androidMap);
        return iosMap;
    }
    
    @Override
    public HashMap getLicensesAssociatedToGroupsMap(final List groupResourceIds, final long customerId, final List businessStoreIDList) throws Exception {
        final HashMap iosMap = new IOSProfileDistributionListHandler().getLicensesAssociatedToGroupsMap(groupResourceIds, customerId, businessStoreIDList);
        final HashMap androidMap = new AndroidProfileDistributionListHandler().getLicensesAssociatedToGroupsMap(groupResourceIds, customerId, businessStoreIDList);
        iosMap.putAll(androidMap);
        return iosMap;
    }
    
    @Override
    public HashMap getLicensesAssociatedToResourcesMap(final List resourceIds, final long customerId, final List businessStoreIDList) throws Exception {
        return null;
    }
}
