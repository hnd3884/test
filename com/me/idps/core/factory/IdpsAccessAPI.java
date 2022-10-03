package com.me.idps.core.factory;

import com.adventnet.ds.query.Criteria;
import java.sql.Connection;
import org.json.JSONException;
import java.util.Set;
import com.me.idps.core.util.DirectoryGroupOnConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;

public interface IdpsAccessAPI
{
    String addOrUpdateAD(final HashMap p0);
    
    boolean isUserMemberOfAnyGroup(final String p0, final String p1, final String p2, final String p3, final List<String> p4, final List<String> p5, final Long p6) throws Exception;
    
    List getAvailableADObjectList(final String p0, final int p1, final List p2, final String p3, final Long p4) throws Exception;
    
    Properties getThisADObjectProperties(final String p0, final int p1, final List p2, final String p3, final Long p4) throws Exception;
    
    Properties getThisADUserProperties(final String p0, final String p1, final String p2, final List p3, final Long p4);
    
    boolean validatePassword(final String p0, final String p1, final String p2, final Long p3);
    
    void fetchBulkADData(final Properties p0, final List<Integer> p1, final boolean p2) throws Exception;
    
    boolean isADDomainReachable(final Properties p0) throws Exception;
    
    Properties preSyncOperations(final JSONObject p0, final int p1, final JSONArray p2, final boolean p3, final boolean p4) throws Exception;
    
    void postSyncOperations(final JSONObject p0, final Boolean p1, final JSONObject p2) throws Exception;
    
    int getResourceType(final int p0);
    
    List<DirectoryGroupOnConfig> getGroupOnProps(final List<Integer> p0);
    
    int getCollateWaitTime();
    
    Set<Integer> getDefaultSyncObjectTypes();
    
    boolean alwaysDoFullSync();
    
    boolean isGUIDresTypeunique();
    
    org.json.JSONObject getCustomParams(final JSONObject p0) throws JSONException;
    
    void doHealthCheck(final Connection p0);
    
    void validateData(final Connection p0, final Criteria p1, final HashMap<String, Criteria> p2, final Integer p3, final String p4, final Long p5, final Long p6, final Integer p7) throws Exception;
    
    void handleError(final Properties p0, final Throwable p1, final String p2);
    
    void handleSuccess(final String p0, final Long p1, final Long p2);
}
