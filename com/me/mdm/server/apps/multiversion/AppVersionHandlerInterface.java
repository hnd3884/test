package com.me.mdm.server.apps.multiversion;

import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import java.util.Map;
import java.util.Properties;
import java.util.List;

public interface AppVersionHandlerInterface
{
    Map<String, List<Long>> removeDevicesContainingLatestVersionOfApp(final List<Long> p0, final Long p1, final Long p2, final Properties p3) throws Exception;
    
    Map<String, List<Long>> removeGroupsContainingLatestVersionOfApp(final List<Long> p0, final Long p1, final Long p2, final Properties p3) throws Exception;
    
    Map<String, List<Long>> removeUsersContainingLatestVersionOfApp(final List<Long> p0, final Long p1, final Long p2, final Properties p3) throws Exception;
    
    @Deprecated
    JSONObject getPossibleChannelsToMergeApp(final JSONObject p0) throws Exception;
    
    @Deprecated
    void checkIfChannelAllowedToBeMerged(final JSONObject p0) throws Exception;
    
    Criteria getDistributedDeviceListForAppCriteria(final Long p0, final Long p1) throws Exception;
    
    Criteria getDistributedGroupListForAppCriteria(final Long p0, final Long p1) throws Exception;
    
    void validateAppVersionForUploadWithReleaseLabel(final JSONObject p0) throws Exception;
    
    void validateAppVersionForUpload(final JSONObject p0) throws Exception;
    
    Boolean isCurrentPackageNewToAppRepo(final String p0, final Long p1) throws Exception;
    
    JSONObject getLowerVersionAppsThanGiveApp(final JSONObject p0) throws Exception;
    
    JSONObject getHigherVersionAppsThanGivenApps(final JSONObject p0) throws Exception;
    
    void handleDBChangesForAppApproval(final JSONObject p0) throws Exception;
}
