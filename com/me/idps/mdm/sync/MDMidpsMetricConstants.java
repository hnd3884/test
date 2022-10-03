package com.me.idps.mdm.sync;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MDMidpsMetricConstants
{
    static final String DIR_MANAGEDUSER_SHARE = "DIR_MANAGEDUSER_SHARE";
    static final String MANAGEDUSER_DIR_SHARE = "MANAGEDUSER_DIR_SHARE";
    static final String MANAGEDUSER_DUPLICATION_COUNT = "MANAGEDUSER_DUPLICATION_COUNT";
    static final String MANAGEDUSER_DUPL_ACROSS_DOMAINS_COUNT = "MANAGEDUSER_DUPL_ACROSS_DOMAINS_COUNT";
    
    static List<String> getTrackingKeys() {
        final List<String> dirTrackingKeys = new ArrayList<String>(Arrays.asList("DIR_MANAGEDUSER_SHARE", "MANAGEDUSER_DIR_SHARE", "MANAGEDUSER_DUPLICATION_COUNT", "MANAGEDUSER_DUPL_ACROSS_DOMAINS_COUNT"));
        return dirTrackingKeys;
    }
}
