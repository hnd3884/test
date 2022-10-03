package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.ds.query.DerivedColumn;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.ds.query.Criteria;
import java.util.List;

public interface AppUpdatesToResourceInterface
{
    List<Long> getResourceListForWhichTheAppToBeScheduled(final List p0, final Long p1, final Long p2) throws Exception;
    
    void updateApprovedAppIdAndItsStatus(final Criteria p0, final Long p1, final Integer p2) throws Exception;
    
    HashMap scheduleAppUpdatesForResourceBasedOnPolicy(final List p0, final Long p1, final Long p2, final Properties p3) throws Exception;
    
    void associateAppUpdateForResource(final List p0, final Long p1, final Long p2) throws Exception;
    
    DerivedColumn getResourcesForWhichAppUpdatePolicyIsConfiguredForGivenApp(final Long p0);
    
    void invokePostPolicyAssociationListeners(final List p0, final Long p1) throws Exception;
    
    Criteria getCriteria(final DerivedColumn p0, final Long p1, final List p2, final Boolean p3);
    
    Criteria getCriteria(final Long p0, final List p1);
    
    Criteria getCriteria(final List p0, final List p1, final Boolean p2) throws Exception;
    
    Criteria getCriteria(final Long p0, final List p1, final DerivedColumn p2, final Boolean p3) throws Exception;
    
    void invokePostPolicyDisassociationListeners(final List p0, final Long p1) throws Exception;
    
    void invokePostPolicyModificationListener(final List p0, final List p1) throws Exception;
}
