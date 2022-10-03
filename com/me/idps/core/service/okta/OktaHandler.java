package com.me.idps.core.service.okta;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.me.idps.core.crud.DomainDataPopulator;
import com.adventnet.i18n.I18N;
import java.sql.Connection;
import com.me.idps.core.api.IdpsAPIException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import com.me.idps.core.util.DirectoryGroupOnConfig;
import com.me.idps.core.sync.asynch.DirectoryDataReceiver;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.ArrayList;
import org.json.JSONArray;
import com.me.idps.core.util.DirectoryUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.List;
import org.json.JSONException;
import java.util.Properties;
import org.json.JSONObject;
import com.me.idps.core.factory.IdpsAccessAPI;

public class OktaHandler implements IdpsAccessAPI
{
    private static OktaHandler oktaHandler;
    OktaAPIs oktaAPIs;
    
    public OktaHandler() {
        this.oktaAPIs = new OktaAPIs();
    }
    
    public static OktaHandler getInstance() {
        if (OktaHandler.oktaHandler == null) {
            OktaHandler.oktaHandler = new OktaHandler();
        }
        return OktaHandler.oktaHandler;
    }
    
    private Properties convertUserToProp(final JSONObject j) throws JSONException {
        final Properties userProps = new Properties();
        ((Hashtable<String, Object>)userProps).put("objectGUID", j.get("id"));
        final JSONObject iprofile = j.getJSONObject("profile");
        ((Hashtable<String, String>)userProps).put("givenName", String.valueOf(iprofile.get("firstName")));
        ((Hashtable<String, String>)userProps).put("sn", String.valueOf(iprofile.get("lastName")));
        ((Hashtable<String, String>)userProps).put("displayName", iprofile.get("firstName") + " " + iprofile.get("lastName"));
        ((Hashtable<String, String>)userProps).put("userPrincipalName", String.valueOf(iprofile.get("login")));
        ((Hashtable<String, String>)userProps).put("mail", String.valueOf(iprofile.get("email")));
        if (iprofile.has("mobilePhone") && String.valueOf(iprofile.get("mobilePhone")) != null) {
            ((Hashtable<String, String>)userProps).put("mobile", String.valueOf(iprofile.get("mobilePhone")));
        }
        return userProps;
    }
    
    private Properties convertGroupToProp(final JSONObject j) throws JSONException {
        final Properties groupProps = new Properties();
        ((Hashtable<String, Object>)groupProps).put("objectGUID", j.get("id"));
        final JSONObject iprofile = j.getJSONObject("profile");
        ((Hashtable<String, String>)groupProps).put("name", String.valueOf(iprofile.get("name")));
        return groupProps;
    }
    
    @Override
    public boolean isUserMemberOfAnyGroup(final String arg_netBIOSName, final String domainUserName, final String emailAddress, final String domainPassword, final List<String> distinguishedNames, final List<String> guids, final Long customerID) throws Exception {
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(arg_netBIOSName, customerID, 301);
        final String api_key = domainProps.getProperty("CRD_PASSWORD");
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)domainUserName, 0, false).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)arg_netBIOSName, 0, false));
        final Properties props = DirectoryUtil.getInstance().getObjectAttributes(criteria);
        final String userId = props.getProperty("GUID");
        final JSONObject urlParams = new JSONObject();
        boolean isMember = false;
        JSONObject groups;
        do {
            urlParams.put("limit", (Object)"200");
            groups = this.oktaAPIs.getUsersGroups(arg_netBIOSName, userId, api_key, urlParams);
            if (groups.has("Link")) {
                urlParams.put("after", (Object)String.valueOf(groups.get("Link")));
            }
            else {
                urlParams.remove("after");
            }
            if (groups.has("response") && groups.getJSONArray("response").length() > 0) {
                final JSONArray igroups = groups.getJSONArray("response");
                for (int i = 0; i < igroups.length(); ++i) {
                    final JSONObject igroup = igroups.getJSONObject(i);
                    final String _id = (String)igroup.get("id");
                    if (guids.contains(_id)) {
                        isMember = true;
                    }
                }
            }
            else {
                if (groups.has("error")) {
                    final String e = String.valueOf(groups.getJSONObject("error").get("errorCode"));
                    throw new OktaErrorCodes().getOktaErrorCodes(e);
                }
                continue;
            }
        } while (groups.has("Link"));
        return isMember;
    }
    
    @Override
    public List getAvailableADObjectList(final String arg_netBIOSName, final int resourceType, final List listAttributes, final String filter, final Long customerID) throws Exception {
        final JSONObject urlParams = new JSONObject();
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(arg_netBIOSName, customerID, 301);
        final String domainName = domainProps.getProperty("NAME");
        final String api_key = domainProps.getProperty("CRD_PASSWORD");
        final ArrayList<Properties> syncData = new ArrayList<Properties>();
        if (resourceType == 7) {
            JSONObject groups;
            do {
                urlParams.put("limit", (Object)"200");
                if (filter != null && !filter.isEmpty()) {
                    urlParams.put("q", (Object)filter);
                }
                groups = this.oktaAPIs.getGroups(domainName, api_key, urlParams);
                if (groups.has("Link")) {
                    urlParams.put("after", (Object)String.valueOf(groups.get("Link")));
                }
                else {
                    urlParams.remove("after");
                }
                if (groups.has("response") && groups.getJSONArray("response").length() > 0) {
                    final JSONArray igroups = groups.getJSONArray("response");
                    for (int i = 0; i < igroups.length(); ++i) {
                        final JSONObject igroup = igroups.getJSONObject(i);
                        final Properties groupProps = new Properties();
                        ((Hashtable<String, Object>)groupProps).put("objectGUID", igroup.get("id"));
                        final JSONObject iprofile = igroup.getJSONObject("profile");
                        ((Hashtable<String, String>)groupProps).put("name", String.valueOf(iprofile.get("name")));
                        syncData.add(groupProps);
                    }
                }
                else {
                    if (groups.has("error")) {
                        final String e = String.valueOf(groups.getJSONObject("error").get("errorCode"));
                        IDPSlogger.SOM.log(Level.SEVERE, null, new OktaErrorCodes().getOktaErrorCodes(e));
                        throw new OktaErrorCodes().getOktaErrorCodes(e);
                    }
                    continue;
                }
            } while (groups.has("Link"));
        }
        return syncData;
    }
    
    @Override
    public Properties getThisADObjectProperties(final String arg_netBIOSName, final int resourceType, final List listAttributes, final String name, final Long customerID) throws Exception {
        Properties p = null;
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(arg_netBIOSName, customerID, 301);
        final String api_token = domainProps.getProperty("CRD_PASSWORD");
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)name, 0, false).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)arg_netBIOSName, 0, false)).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resourceType, 0, false));
        final Properties props = DirectoryUtil.getInstance().getObjectAttributes(criteria);
        final String _id = props.getProperty("GUID");
        if (resourceType == 2) {
            final JSONObject user = this.oktaAPIs.getUser(arg_netBIOSName, api_token, _id);
            if (user.has("response")) {
                p = this.convertUserToProp(user.getJSONObject("response"));
            }
        }
        else if (resourceType == 7) {
            final JSONObject group = this.oktaAPIs.getGroup(arg_netBIOSName, api_token, _id);
            if (group.has("response")) {
                p = this.convertUserToProp(group.getJSONObject("response"));
            }
        }
        return p;
    }
    
    @Override
    public Properties getThisADUserProperties(final String arg_netBIOSName, final String userName, final String password, final List listAttributes, final Long customerID) {
        try {
            return this.getThisADObjectProperties(arg_netBIOSName, 2, listAttributes, userName, customerID);
        }
        catch (final Exception e) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured : ", e);
            return null;
        }
    }
    
    @Override
    public boolean validatePassword(final String arg_netBIOSName, final String userName, final String password, final Long customerID) {
        try {
            if (this.oktaAPIs.isValidPassword(arg_netBIOSName, userName, password)) {
                return true;
            }
        }
        catch (final Throwable e) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in validating password : ", e);
        }
        return false;
    }
    
    @Override
    public void fetchBulkADData(final Properties dmDomainProperties, final List<Integer> syncObjects, final boolean doFullSync) throws Exception {
        final String domainName = ((Hashtable<K, String>)dmDomainProperties).get("NAME");
        final Long customerId = ((Hashtable<K, Long>)dmDomainProperties).get("CUSTOMER_ID");
        final String api_key = ((Hashtable<K, String>)dmDomainProperties).get("CRD_PASSWORD");
        boolean isGropSync = false;
        if (syncObjects.contains(7)) {
            isGropSync = true;
        }
        final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerId, 301);
        final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        this.syncUsers(dmDomainID, domainName, customerId, api_key, isGropSync, doFullSync);
        if (isGropSync) {
            this.syncGroups(dmDomainID, domainName, customerId, api_key, doFullSync);
        }
    }
    
    public void syncGroups(final Long dmDomainID, final String domainName, final Long customerId, final String api_key, final boolean doFullSync) throws Exception {
        final boolean isfirstList = false;
        boolean islastList = false;
        final JSONObject urlParams = new JSONObject();
        if (!doFullSync) {
            final Object timeInMs = DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "LAST_SUCCESSFUL_SYNC");
            final Date date = new Date((long)timeInMs);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String timeInISO = sdf.format(date);
            final String paramsLastUpdated = "lastUpdated gt \"" + timeInISO + "\" or lastMembershipUpdated gt \"" + timeInISO + "\"";
            urlParams.put("filter", (Object)paramsLastUpdated);
        }
        final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
        JSONObject groups;
        do {
            urlParams.put("limit", (Object)"200");
            groups = this.oktaAPIs.getGroups(domainName, api_key, urlParams);
            if (groups.has("Link")) {
                urlParams.put("after", (Object)String.valueOf(groups.get("Link")));
            }
            else {
                islastList = true;
                urlParams.remove("after");
            }
            final ArrayList<Properties> syncData = new ArrayList<Properties>();
            if (groups.has("response") && groups.getJSONArray("response").length() > 0) {
                final JSONArray igroups = groups.getJSONArray("response");
                for (int i = 0; i < igroups.length(); ++i) {
                    final JSONObject igroup = igroups.getJSONObject(i);
                    final Properties groupProps = this.convertGroupToProp(igroup);
                    ((Hashtable<String, Integer>)groupProps).put(statusAttrKey, 1);
                    syncData.add(groupProps);
                }
            }
            else if (groups.has("error")) {
                final String e = String.valueOf(groups.getJSONObject("error").get("errorCode"));
                IDPSlogger.SOM.log(Level.SEVERE, null, new OktaErrorCodes().getOktaErrorCodes(e));
                throw new OktaErrorCodes().getOktaErrorCodes(e);
            }
            new DirectoryDataReceiver().proccessFetchedADData(null, syncData, domainName, customerId, 7, syncData.size(), 0, syncData.size() - 1, isfirstList, islastList);
        } while (groups.has("Link"));
    }
    
    public void syncUsers(final Long dmDomainID, final String domainName, final Long customerId, final String api_key, final boolean isgroupSync, final boolean doFullSync) throws Exception {
        boolean isfirstList = true;
        boolean islastList = false;
        final JSONObject urlParams = new JSONObject();
        if (!doFullSync) {
            final Object timeInMs = DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "LAST_SUCCESSFUL_SYNC");
            final Date date = new Date((long)timeInMs);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String timeInISO = sdf.format(date);
            final String params = "lastUpdated gt \"" + timeInISO + "\"";
            urlParams.put("filter", (Object)params);
        }
        final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
        JSONObject users;
        do {
            urlParams.put("limit", (Object)"200");
            users = this.oktaAPIs.getUsers(domainName, api_key, urlParams);
            if (users.has("Link")) {
                urlParams.put("after", (Object)String.valueOf(users.get("Link")));
            }
            else {
                urlParams.remove("after");
                if (!isgroupSync) {
                    islastList = true;
                }
            }
            final ArrayList<Properties> syncData = new ArrayList<Properties>();
            if (users.has("response") && users.getJSONArray("response").length() > 0) {
                final JSONArray iusers = users.getJSONArray("response");
                for (int i = 0; i < iusers.length(); ++i) {
                    final JSONObject iuser = iusers.getJSONObject(i);
                    final Properties userProps = this.convertUserToProp(iuser);
                    ((Hashtable<String, Integer>)userProps).put(statusAttrKey, 1);
                    syncData.add(userProps);
                }
            }
            else if (users.has("error")) {
                final String e = String.valueOf(users.getJSONObject("error").get("errorCode"));
                IDPSlogger.SOM.log(Level.SEVERE, null, new OktaErrorCodes().getOktaErrorCodes(e));
                throw new OktaErrorCodes().getOktaErrorCodes(e);
            }
            new DirectoryDataReceiver().proccessFetchedADData(null, syncData, domainName, customerId, 2, syncData.size(), 0, syncData.size() - 1, isfirstList, islastList);
            isfirstList = false;
        } while (users.has("Link"));
    }
    
    @Override
    public boolean isADDomainReachable(final Properties dmDomainProperties) {
        try {
            final JSONObject user = this.oktaAPIs.getUsersMe(dmDomainProperties.getProperty("NAME"), dmDomainProperties.getProperty("CRD_PASSWORD"));
            if (user.length() >= 1 && user.has("id")) {
                return true;
            }
        }
        catch (final JSONException e) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in JSON Parsing : ", (Throwable)e);
        }
        return false;
    }
    
    @Override
    public Properties preSyncOperations(final org.json.simple.JSONObject dmDomainProps, final int resType, final org.json.simple.JSONArray directoryData, final boolean isFirstList, final boolean isLastList) throws Exception {
        final Properties processedSyncInput = new Properties();
        ((Hashtable<String, Boolean>)processedSyncInput).put("LAST_COUNT", isLastList);
        ((Hashtable<String, Boolean>)processedSyncInput).put("FIRST_COUNT", isFirstList);
        final String domainName = (String)dmDomainProps.get((Object)"NAME");
        final Properties dmProps = DMDomainDataHandler.getInstance().getDomainById((Long)dmDomainProps.get((Object)"DOMAIN_ID"));
        final String api_key = ((Hashtable<K, String>)dmProps).get("CRD_PASSWORD");
        if (resType == 7) {
            for (int i = 0; i < directoryData.size(); ++i) {
                final org.json.simple.JSONObject j = (org.json.simple.JSONObject)directoryData.get(i);
                if (j.containsKey((Object)"DELTA_TOKEN") && isLastList) {
                    j.put((Object)"DELTA_TOKEN", (Object)System.currentTimeMillis());
                    break;
                }
                final String groupId = (String)j.get((Object)"objectGUID");
                final JSONObject urlParams = new JSONObject();
                final ArrayList<String> usersidForGroup = new ArrayList<String>();
                JSONObject groupUsers;
                do {
                    urlParams.put("limit", (Object)"200");
                    groupUsers = this.oktaAPIs.getGroupUsers(domainName, groupId, api_key, urlParams);
                    if (groupUsers.has("Link")) {
                        urlParams.put("after", (Object)String.valueOf(groupUsers.get("Link")));
                    }
                    else {
                        urlParams.remove("after");
                    }
                    final JSONArray igroupUsers = groupUsers.getJSONArray("response");
                    for (int k = 0; k < igroupUsers.length(); ++k) {
                        final JSONObject iuser = igroupUsers.getJSONObject(k);
                        usersidForGroup.add(String.valueOf(iuser.get("id")));
                    }
                } while (groupUsers.has("Link"));
                ((org.json.simple.JSONObject)directoryData.get(i)).put((Object)"member", (Object)usersidForGroup);
            }
        }
        ((Hashtable<String, org.json.simple.JSONArray>)processedSyncInput).put("DirResRel", directoryData);
        if (isLastList) {
            ((Hashtable<String, Long>)processedSyncInput).put("DELTA_TOKEN", System.currentTimeMillis());
        }
        return processedSyncInput;
    }
    
    @Override
    public void postSyncOperations(final org.json.simple.JSONObject dmDomainProps, final Boolean isFullSync, final org.json.simple.JSONObject postSyncOPdetail) throws Exception {
    }
    
    @Override
    public int getResourceType(final int resType) {
        if (resType == 7) {
            return 101;
        }
        return resType;
    }
    
    @Override
    public List<DirectoryGroupOnConfig> getGroupOnProps(final List<Integer> objectsToBeSynced) {
        final List<DirectoryGroupOnConfig> groupOnDetails = new ArrayList<DirectoryGroupOnConfig>();
        if (objectsToBeSynced.contains(7)) {
            final DirectoryGroupOnConfig directoryGroupOnConfig = new DirectoryGroupOnConfig(102L, 7, new ArrayList<Integer>(Arrays.asList(7, 2)));
            groupOnDetails.add(directoryGroupOnConfig);
        }
        return groupOnDetails;
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
    public String addOrUpdateAD(final HashMap domainDetails) {
        try {
            final String domainName = domainDetails.get("DOMAINNAME");
            final String api_key = domainDetails.get("PASSWORD");
            final JSONObject obj = this.oktaAPIs.getUsersMe(domainName, api_key);
            final String username = String.valueOf(obj.getJSONObject("profile").get("login"));
            domainDetails.put("USERNAME", username);
            if (obj.has("errorCode")) {
                IDPSlogger.SOM.log(Level.SEVERE, "exception occured in validating The domain : ", new OktaErrorCodes().getOktaErrorCodes(String.valueOf(obj.get("errorCode"))));
                throw new OktaErrorCodes().getOktaErrorCodes(String.valueOf(obj.get("errorCode")));
            }
            this.addOktaDomainToDb(domainDetails);
            return String.valueOf(true);
        }
        catch (final JSONException e) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in JSON Parsing : ", (Throwable)e);
        }
        catch (final DataAccessException e2) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in DataAccess  : ", (Throwable)e2);
        }
        catch (final SyMException e3) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in SysException : ", (Throwable)e3);
        }
        catch (final IdpsAPIException oktaErrorCodes) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in OKTA  API : ", oktaErrorCodes);
        }
        catch (final Exception e4) {
            IDPSlogger.SOM.log(Level.SEVERE, "gen exception occured : ", e4);
        }
        return String.valueOf(false);
    }
    
    @Override
    public boolean alwaysDoFullSync() {
        return false;
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
    }
    
    @Override
    public void handleSuccess(final String dmDomainName, final Long customerID, final Long dmDomainID) {
    }
    
    private void addOktaDomainToDb(final HashMap domainDetails) throws Exception {
        final Properties newDomainProperties = new Properties();
        ((Hashtable<String, String>)newDomainProperties).put("DNS_SUFFIX", "--");
        ((Hashtable<String, Boolean>)newDomainProperties).put("IS_AD_DOMAIN", false);
        ((Hashtable<String, Integer>)newDomainProperties).put("CLIENT_ID", 301);
        ((Hashtable<String, String>)newDomainProperties).put("DC_NAME", I18N.getMsg("mdm.ad.okta", new Object[0]));
        ((Hashtable<String, Object>)newDomainProperties).put("NAME", domainDetails.get("DOMAINNAME"));
        ((Hashtable<String, Object>)newDomainProperties).put("CUSTOMER_ID", domainDetails.get("CUSTOMER_ID"));
        ((Hashtable<String, Object>)newDomainProperties).put("CRD_USERNAME", domainDetails.get("USERNAME"));
        ((Hashtable<String, Object>)newDomainProperties).put("CRD_PASSWORD", domainDetails.get("PASSWORD"));
        ((Hashtable<String, Object>)newDomainProperties).put("AD_DOMAIN_NAME", domainDetails.get("DOMAINNAME"));
        final DataObject dmDomainDO = DomainDataPopulator.getInstance().addOrUpdateDMManagedDomain(newDomainProperties);
        if (dmDomainDO == null) {
            throw new DataAccessException();
        }
    }
    
    static {
        OktaHandler.oktaHandler = null;
    }
}
