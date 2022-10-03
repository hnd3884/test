package com.me.mdm.server.apps.businessstore;

import java.util.Properties;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public interface StoreInterface
{
    public static final Integer BS_SERVICE_VPP = 101;
    public static final Integer BS_SERVICE_AFW = 201;
    public static final Integer BS_SERVICE_WBS = 301;
    
    JSONObject getAllStoreSyncStatus(final JSONObject p0) throws Exception;
    
    JSONArray getAllStoreSyncStatus() throws Exception;
    
    JSONObject verifyAccountRemoval() throws Exception;
    
    JSONObject syncLicenseStatus(final JSONObject p0) throws Exception;
    
    JSONObject addStoreDetails(final JSONObject p0) throws Exception;
    
    JSONObject removeStoreDetails(final JSONObject p0) throws Exception;
    
    Object getStoreDetails(final JSONObject p0) throws Exception;
    
    JSONObject getAllStoreDetails() throws Exception;
    
    void modifyStoreDetails(final JSONObject p0) throws Exception;
    
    JSONObject getStorePromoStatus(final JSONObject p0) throws Exception;
    
    JSONObject syncStore(final JSONObject p0) throws Exception;
    
    Object getSyncStoreStatus(final JSONObject p0) throws Exception;
    
    void clearSyncStoreStatus() throws Exception;
    
    void syncLicense(final JSONObject p0) throws Exception;
    
    JSONObject getLicenseSyncStatus(final JSONObject p0) throws Exception;
    
    void validateAppToBusinessStoreProps(final List p0, final Properties p1, final Properties p2) throws Exception;
    
    void updateStoreSyncKey() throws Exception;
    
    Object getAppsFailureDetails(final JSONObject p0) throws Exception;
    
    void addLicenseRemovalTaskToQueue(final JSONObject p0, final Long p1, final List p2, final boolean p3) throws Exception;
    
    String getBusinessStoreName(final Long p0) throws Exception;
}
