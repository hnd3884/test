package com.me.idps.core.service.gsuite;

import java.util.Hashtable;
import org.json.simple.JSONArray;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.ds.query.Criteria;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import com.me.idps.core.util.DirectoryGroupOnConfig;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.idps.core.sync.asynch.DirectoryDataReceiver;
import com.me.idps.core.util.DirectoryAttributeConstants;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.factory.IdpsFactoryConstant;
import com.me.idps.core.factory.GsuiteAccessAPI;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.me.idps.core.factory.IdpsAccessAPI;

public class GSuiteDirectoryServiceAccessProvider implements IdpsAccessAPI
{
    private static GSuiteDirectoryServiceAccessProvider gSuiteDirectoryServiceAccessProvider;
    
    public static GSuiteDirectoryServiceAccessProvider getInstance() {
        if (GSuiteDirectoryServiceAccessProvider.gSuiteDirectoryServiceAccessProvider == null) {
            GSuiteDirectoryServiceAccessProvider.gSuiteDirectoryServiceAccessProvider = new GSuiteDirectoryServiceAccessProvider();
        }
        return GSuiteDirectoryServiceAccessProvider.gSuiteDirectoryServiceAccessProvider;
    }
    
    @Override
    public String addOrUpdateAD(final HashMap domainDetails) {
        return null;
    }
    
    @Override
    public boolean isUserMemberOfAnyGroup(final String arg_netBIOSName, final String domainUserName, final String emailAddress, final String domainPassword, final List<String> distinguishedNames, final List<String> guids, final Long customerID) throws Exception {
        return false;
    }
    
    @Override
    public List getAvailableADObjectList(final String arg_netBIOSName, final int resourceType, final List listAttributes, final String filter, final Long customerID) throws Exception {
        return new ArrayList();
    }
    
    @Override
    public Properties getThisADObjectProperties(final String arg_netBIOSName, final int resourceType, final List listAttributes, final String name, final Long customerID) throws Exception {
        return new Properties();
    }
    
    @Override
    public Properties getThisADUserProperties(final String arg_netBIOSName, final String userName, final String password, final List listAttributes, final Long customerID) {
        return new Properties();
    }
    
    @Override
    public boolean validatePassword(final String arg_netBIOSName, final String userName, final String password, final Long customerID) {
        return true;
    }
    
    @Override
    public void fetchBulkADData(final Properties dmDomainProperties, final List syncObjects, final boolean doFullSync) throws Exception {
        final String domainName = ((Hashtable<K, String>)dmDomainProperties).get("NAME");
        final Long customerId = ((Hashtable<K, Long>)dmDomainProperties).get("CUSTOMER_ID");
        final GsuiteAccessAPI gSuiteAccessImpl = (GsuiteAccessAPI)IdpsFactoryProvider.getSingleImplClassInstance(IdpsFactoryConstant.GSUITE_IMPL);
        final JSONObject users = gSuiteAccessImpl.getUsersForIdps(customerId);
        final ArrayList<Properties> syncData = new ArrayList<Properties>();
        final JSONObject syncedUserDetails = users.getJSONObject("users");
        final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
        if (syncedUserDetails != null) {
            final Iterator iterator = syncedUserDetails.keys();
            while (iterator != null && iterator.hasNext()) {
                final String key = iterator.next();
                final JSONObject userJSON = syncedUserDetails.getJSONObject(key);
                final Properties userProps = new Properties();
                ((Hashtable<String, String>)userProps).put("objectGUID", String.valueOf(userJSON.get("objectGUID")));
                ((Hashtable<String, String>)userProps).put("sn", userJSON.optString("sn"));
                ((Hashtable<String, String>)userProps).put("givenName", userJSON.optString("givenName"));
                ((Hashtable<String, String>)userProps).put("displayName", userJSON.optString("displayName"));
                ((Hashtable<String, String>)userProps).put("mail", String.valueOf(userJSON.get("mail")));
                ((Hashtable<String, Integer>)userProps).put(statusAttrKey, 1);
                syncData.add(userProps);
            }
        }
        new DirectoryDataReceiver().proccessFetchedADData(null, syncData, domainName, customerId, 2, syncData.size(), 0, syncData.size() - 1, true, true);
    }
    
    @Override
    public boolean isADDomainReachable(final Properties dmDomainProperties) {
        return true;
    }
    
    @Override
    public void postSyncOperations(final org.json.simple.JSONObject dmDomainProperties, final Boolean isFullSync, final org.json.simple.JSONObject postSyncOPdetail) throws Exception {
    }
    
    @Override
    public int getResourceType(final int resType) {
        return resType;
    }
    
    @Override
    public List<DirectoryGroupOnConfig> getGroupOnProps(final List<Integer> objectsToBeSynced) {
        return new ArrayList<DirectoryGroupOnConfig>();
    }
    
    @Override
    public int getCollateWaitTime() {
        return 0;
    }
    
    @Override
    public Set<Integer> getDefaultSyncObjectTypes() {
        return new HashSet<Integer>(Arrays.asList(2));
    }
    
    @Override
    public boolean alwaysDoFullSync() {
        return true;
    }
    
    @Override
    public boolean isGUIDresTypeunique() {
        return false;
    }
    
    @Override
    public JSONObject getCustomParams(final org.json.simple.JSONObject props) {
        return null;
    }
    
    @Override
    public void doHealthCheck(final Connection connection) {
    }
    
    @Override
    public void validateData(final Connection connection, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Integer syncType, final String domainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClientID) throws Exception {
    }
    
    @Override
    public void handleError(final Properties dmDomainProps, final Throwable thrown, final String errorType) {
        final String domainName = ((Hashtable<K, String>)dmDomainProps).get("NAME");
        final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        final Long customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
        final String failureResponse = "webclient#/uems/mdm/enrollment/activeDirectory/integrateGsuite";
        try {
            DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "REMARKS", failureResponse);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, null, e);
        }
    }
    
    @Override
    public void handleSuccess(final String dmDomainName, final Long customerID, final Long dmDomainID) {
    }
    
    @Override
    public Properties preSyncOperations(final org.json.simple.JSONObject dmDomainProps, final int resType, final JSONArray directoryData, final boolean isFirstList, final boolean isLastList) {
        final Properties processedSyncInput = new Properties();
        ((Hashtable<String, Boolean>)processedSyncInput).put("LAST_COUNT", isLastList);
        ((Hashtable<String, Boolean>)processedSyncInput).put("FIRST_COUNT", isFirstList);
        ((Hashtable<String, JSONArray>)processedSyncInput).put("DirResRel", directoryData);
        return processedSyncInput;
    }
    
    static {
        GSuiteDirectoryServiceAccessProvider.gSuiteDirectoryServiceAccessProvider = null;
    }
}
