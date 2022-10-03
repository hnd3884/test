package com.me.idps.mdmop;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.sql.Connection;
import org.json.simple.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.idps.core.util.DirectoryGroupOnConfig;
import org.json.simple.JSONObject;
import com.me.idps.core.api.IdpsAPIException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.idps.core.util.IdpsUtil;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.sym.winaccess.ADReportsHandler;
import com.me.devicemanagement.framework.winaccess.DomainInfo;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.List;
import com.me.devicemanagement.framework.server.admin.ADdomainHandler;
import java.util.HashMap;
import com.me.idps.core.factory.IdpsAccessAPI;
import com.me.devicemanagement.onpremise.winaccess.ADAccessProvider;

public class OnpremiseADAccessProvider extends ADAccessProvider implements IdpsAccessAPI
{
    private static final int ADS_UF_ACCOUNT_DISABLE = 2;
    private static OnpremiseADAccessProvider adAccessProvider;
    
    public static synchronized OnpremiseADAccessProvider getInstance() {
        if (OnpremiseADAccessProvider.adAccessProvider == null) {
            OnpremiseADAccessProvider.adAccessProvider = new OnpremiseADAccessProvider();
        }
        return OnpremiseADAccessProvider.adAccessProvider;
    }
    
    public String addOrUpdateAD(final HashMap domainDetails) {
        return new ADdomainHandler().addOrUpdateStandaloneOnPremiseADInDMAlso(domainDetails);
    }
    
    public List getAvailableADObjectList(final String arg_netBIOSName, final int resourceType, final List listAttributes, String filter, final Long customerID) throws SyMException {
        if (filter == null || filter.isEmpty() || filter.equalsIgnoreCase("All")) {
            filter = "";
        }
        return this.searchADObject(arg_netBIOSName, resourceType, filter, (String)null, listAttributes, 2, 200);
    }
    
    public Properties getThisADObjectProperties(final String arg_netBIOSName, final int resourceType, final List listAttributes, final String name, final Long customerID) throws Exception {
        return this.getThisADObjectByName(arg_netBIOSName, name, resourceType, listAttributes);
    }
    
    public boolean validatePassword(final String arg_netBIOSName, final String userName, final String password, final Long customerID) {
        try {
            final Properties props = DomainHandler.getInstance().getDomainInfo(arg_netBIOSName);
            final String strDomainName = ((Hashtable<K, String>)props).get("AD_DOMAIN_NAME");
            final String strDCName = ((Hashtable<K, String>)props).get("DC_NAME");
            final Boolean isSSL = ((Hashtable<K, Boolean>)props).get("USE_SSL");
            final int portNo = ((Hashtable<K, Integer>)props).get("PORT_NO");
            return super.validatePassword(arg_netBIOSName, strDomainName, strDCName, userName, password, (boolean)isSSL, portNo);
        }
        catch (final SyMException ex) {
            IDPSlogger.SOM.log(Level.SEVERE, null, (Throwable)ex);
            return false;
        }
    }
    
    private Long adjustLastSuccessfulSyncTimeforAdSync(final Long lastSuccessfulSync) {
        final long oneDayInMillis = TimeUnit.DAYS.toMillis(1L);
        if (lastSuccessfulSync != null && lastSuccessfulSync >= oneDayInMillis) {
            return lastSuccessfulSync - oneDayInMillis;
        }
        return 0L;
    }
    
    private String getADSearchFilter(final int nResType, final long nModifiedTime) throws SyMException {
        return (nModifiedTime == 0L) ? this.getADSearchFilter(nResType) : this.getADSearchFilterWithTime(nResType, nModifiedTime);
    }
    
    private void fetchADdata(final Properties dmDomainProps, final long nModifiedTime, final int nResType, final String workflow) throws Exception {
        IDPSlogger.SOM.log(Level.INFO, "making ad request for " + String.valueOf(nResType));
        final String domainName = ((Hashtable<K, String>)dmDomainProps).get("NAME");
        final boolean isSSL = ((Hashtable<K, Boolean>)dmDomainProps).get("USE_SSL");
        final int portNo = ((Hashtable<K, Integer>)dmDomainProps).get("PORT_NO");
        final List<String> lisAttributes = this.getAttributes(nResType);
        if (nResType == 2) {
            if (!lisAttributes.contains("mail")) {
                lisAttributes.add("mail");
            }
            if (!lisAttributes.contains("sAMAccountName")) {
                lisAttributes.add("sAMAccountName");
            }
            if (!lisAttributes.contains("sn")) {
                lisAttributes.add("sn");
            }
            if (!lisAttributes.contains("givenName")) {
                lisAttributes.add("givenName");
            }
            if (!lisAttributes.contains("initials")) {
                lisAttributes.add("initials");
            }
            if (!lisAttributes.contains("displayName")) {
                lisAttributes.add("displayName");
            }
            if (!lisAttributes.contains("userPrincipalName")) {
                lisAttributes.add("userPrincipalName");
            }
            final Boolean isDirPolicyEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabledInDB("statusSyncEnabled");
            if (isDirPolicyEnabled) {
                if (!lisAttributes.contains("isDeleted")) {
                    lisAttributes.add("isDeleted");
                }
                if (!lisAttributes.contains("userAccountControl")) {
                    lisAttributes.add("userAccountControl");
                }
            }
        }
        if ((nResType == 2 || nResType == 7) && !lisAttributes.contains("memberOf")) {
            lisAttributes.add("memberOf");
        }
        if (!lisAttributes.contains("distinguishedName")) {
            lisAttributes.add("distinguishedName");
        }
        if (!lisAttributes.contains("objectGUID")) {
            lisAttributes.add("objectGUID");
        }
        final DomainInfo domainObj = new DomainInfo(domainName);
        domainObj.validateDomainInfoObject();
        final int count = ADReportsHandler.getInstance().fetchBulkADdata(domainName, domainObj.strDCName, domainObj.getLdapPath(), domainObj.getBindFormatUserName(), domainObj.strPassword, nResType, (List)lisAttributes, this.getADSearchFilter(nResType, nModifiedTime), 2, nModifiedTime, 500, false, workflow, isSSL, portNo);
        IDPSlogger.SOM.log(Level.INFO, "Total no. of AD objects of type : " + String.valueOf(nResType) + " fetched : " + count + " , for " + domainName);
    }
    
    public void fetchBulkADData(final Properties dmDomainProps, final List<Integer> syncObjects, final boolean doFullSync) throws Exception {
        final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        Long modifiedTime = 0L;
        if (!doFullSync) {
            modifiedTime = (Long)DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "LAST_SUCCESSFUL_SYNC");
            modifiedTime = this.adjustLastSuccessfulSyncTimeforAdSync(modifiedTime);
        }
        final String workflow = "DM_ADSYNC_IMPL_CLASS";
        for (int i = 0; i < syncObjects.size(); ++i) {
            this.fetchADdata(dmDomainProps, modifiedTime, syncObjects.get(i), workflow);
        }
    }
    
    public Properties getThisADUserProperties(final String arg_netBIOSName, final String userName, final String password, final List listAttributes, final Long customerID) {
        Properties userProperties = null;
        if (!IdpsUtil.isStringEmpty(password)) {
            try {
                userProperties = this.getThisADObjectByName(arg_netBIOSName, userName, password, userName, 2, listAttributes);
            }
            catch (final SyMException ex) {
                IDPSlogger.SOM.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return (userProperties == null) ? new Properties() : userProperties;
    }
    
    public boolean isADDomainReachable(final Properties dmDomainProps) {
        String errMsg = null;
        final String dmDomainName = ((Hashtable<K, String>)dmDomainProps).get("NAME");
        final Long customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
        final int portNo = ((Hashtable<K, Integer>)dmDomainProps).get("PORT_NO");
        final String userName = ((Hashtable<K, String>)dmDomainProps).get("CRD_USERNAME");
        final String password = ((Hashtable<K, String>)dmDomainProps).get("CRD_PASSWORD");
        final String strDCName = ((Hashtable<K, String>)dmDomainProps).get("DC_NAME");
        final boolean isSSL = ((Hashtable<K, Boolean>)dmDomainProps).get("USE_SSL");
        final String adDomainName = ((Hashtable<K, String>)dmDomainProps).get("AD_DOMAIN_NAME");
        try {
            final Properties credentialValidation = new ADdomainHandler().validatePassWordForDomain(customerID, dmDomainName, adDomainName, strDCName, userName, password, 2, isSSL, portNo);
            final boolean validPassword = ((Hashtable<K, Boolean>)credentialValidation).get("VALID_PASSWORD");
            if (!validPassword) {
                errMsg = ((Hashtable<K, String>)credentialValidation).get("ERROR_MESSAGE");
                IDPSlogger.ASYNCH.log(Level.INFO, "domain credential validation failed with {0}", new Object[] { errMsg });
            }
            else {
                IDPSlogger.ASYNCH.log(Level.INFO, "domain credentials validation is success");
                final String i18n = ApiFactoryProvider.getADAccessAPI().getNetBIOSName(adDomainName, strDCName, userName, password, isSSL, portNo);
                IDPSlogger.ASYNCH.log(Level.INFO, "Domain Netbios name for ad domainName :" + adDomainName + " is :" + i18n);
                if (!i18n.equalsIgnoreCase(dmDomainName)) {
                    errMsg = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Invalid_domain_name_Change_the_domain_name", new Object[] { i18n });
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.WARNING, "Exception while getting domain netbios name for the given ad domain details...", e);
        }
        if (!IdpsUtil.isStringEmpty(errMsg)) {
            throw new IdpsAPIException(errMsg);
        }
        return true;
    }
    
    public void postSyncOperations(final JSONObject dmDomainProperties, final Boolean isFullSync, final JSONObject postSyncOPdetail) throws Exception {
    }
    
    public int getResourceType(final int resType) {
        if (resType == 7) {
            return 101;
        }
        return resType;
    }
    
    public List<DirectoryGroupOnConfig> getGroupOnProps(final List<Integer> objectsToBeSynced) {
        final DirectoryGroupOnConfig directoryGroupOnConfig = new DirectoryGroupOnConfig(101L, 7, new ArrayList<Integer>(Arrays.asList(7, 2)));
        final List<DirectoryGroupOnConfig> groupOnDetails = new ArrayList<DirectoryGroupOnConfig>();
        if (objectsToBeSynced.contains(7)) {
            groupOnDetails.add(directoryGroupOnConfig);
        }
        return groupOnDetails;
    }
    
    public int getCollateWaitTime() {
        return 0;
    }
    
    public Set<Integer> getDefaultSyncObjectTypes() {
        return new HashSet<Integer>(Arrays.asList(2));
    }
    
    private JSONObject getNewJSONobject(final JSONObject jsonObject, final String statusAttrKey) {
        String deptAttrKey = null;
        try {
            deptAttrKey = DirectoryAttributeConstants.getAttrKey(128L);
        }
        catch (final DataAccessException e) {
            IDPSlogger.ERR.log(Level.SEVERE, null, (Throwable)e);
        }
        final JSONObject newJsonObject = new JSONObject();
        if (jsonObject.containsKey((Object)"memberOf")) {
            newJsonObject.put((Object)"memberOf", jsonObject.get((Object)"memberOf"));
        }
        if (jsonObject.containsKey((Object)"description")) {
            newJsonObject.put((Object)"description", jsonObject.get((Object)"description"));
        }
        if (jsonObject.containsKey((Object)"distinguishedName")) {
            newJsonObject.put((Object)"distinguishedName", jsonObject.get((Object)"distinguishedName"));
        }
        if (jsonObject.containsKey((Object)"name")) {
            newJsonObject.put((Object)"name", jsonObject.get((Object)"name"));
        }
        if (jsonObject.containsKey((Object)"objectGUID")) {
            newJsonObject.put((Object)"objectGUID", jsonObject.get((Object)"objectGUID"));
        }
        if (jsonObject.containsKey((Object)"mail")) {
            newJsonObject.put((Object)"mail", jsonObject.get((Object)"mail"));
        }
        if (jsonObject.containsKey((Object)"sAMAccountName")) {
            newJsonObject.put((Object)"sAMAccountName", jsonObject.get((Object)"sAMAccountName"));
        }
        if (jsonObject.containsKey((Object)"sn")) {
            newJsonObject.put((Object)"sn", jsonObject.get((Object)"sn"));
        }
        if (jsonObject.containsKey((Object)"givenName")) {
            newJsonObject.put((Object)"givenName", jsonObject.get((Object)"givenName"));
        }
        if (jsonObject.containsKey((Object)"initials")) {
            newJsonObject.put((Object)"initials", jsonObject.get((Object)"initials"));
        }
        if (IdpsUtil.isStringEmpty(deptAttrKey) && jsonObject.containsKey((Object)deptAttrKey)) {
            newJsonObject.put((Object)deptAttrKey, jsonObject.get((Object)deptAttrKey));
        }
        if (jsonObject.containsKey((Object)"displayName")) {
            newJsonObject.put((Object)"displayName", jsonObject.get((Object)"displayName"));
        }
        if (jsonObject.containsKey((Object)"userPrincipalName")) {
            newJsonObject.put((Object)"userPrincipalName", jsonObject.get((Object)"userPrincipalName"));
        }
        int curStatus = 1;
        if (jsonObject.containsKey((Object)"isDeleted")) {
            final Boolean val = Boolean.valueOf(String.valueOf(jsonObject.get((Object)"isDeleted")));
            if (val) {
                curStatus = 5;
            }
        }
        final String statusKeyStr = "userAccountControl";
        if (curStatus != 5 && jsonObject.containsKey((Object)statusKeyStr)) {
            final String strVal = String.valueOf(jsonObject.get((Object)statusKeyStr));
            int decVal = 0;
            try {
                decVal = Integer.valueOf(strVal);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.INFO, "userAccountControl parsing failed {0}", new String[] { strVal });
            }
            if ((decVal & 0x2) == 0x2) {
                curStatus = 3;
            }
        }
        newJsonObject.put((Object)statusAttrKey, (Object)curStatus);
        return newJsonObject;
    }
    
    public org.json.JSONObject getCustomParams(final JSONObject props) {
        return null;
    }
    
    public Properties preSyncOperations(final JSONObject dmDomainProps, final int resType, final JSONArray directoryData, boolean isFirstList, boolean isLastList) throws Exception {
        final List<Integer> syncObjects = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced((Long)dmDomainProps.get((Object)"DOMAIN_ID"));
        if (!syncObjects.isEmpty()) {
            final int firstObjType = syncObjects.get(0);
            final int lastObjType = syncObjects.get(syncObjects.size() - 1);
            isFirstList = (resType == firstObjType && isFirstList);
            isLastList = (resType == lastObjType && isLastList);
        }
        final JSONArray newDirectoryData = new JSONArray();
        final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
        for (int i = 0; i < directoryData.size(); ++i) {
            final JSONObject jsonObject = (JSONObject)directoryData.get(i);
            final JSONObject newJsonObject = this.getNewJSONobject(jsonObject, statusAttrKey);
            newDirectoryData.add((Object)newJsonObject);
        }
        final Properties processedSyncInput = new Properties();
        ((Hashtable<String, JSONArray>)processedSyncInput).put("DirResRel", newDirectoryData);
        ((Hashtable<String, Boolean>)processedSyncInput).put("LAST_COUNT", isLastList);
        ((Hashtable<String, Boolean>)processedSyncInput).put("FIRST_COUNT", isFirstList);
        return processedSyncInput;
    }
    
    public boolean alwaysDoFullSync() {
        return true;
    }
    
    public boolean isGUIDresTypeunique() {
        return false;
    }
    
    public void doHealthCheck(final Connection connection) {
        try {
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("statusSyncEnabled")) {
                IDPSlogger.AUDIT.log(Level.INFO, "statusSyncEnabled feature is enabled");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomainSyncDetails"));
                selectQuery.addJoin(new Join("DMDomainSyncDetails", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2));
                selectQuery.setCriteria(new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)2, 0));
                selectQuery.addSelectColumn(IdpsUtil.getCountOfColumn("DMDomainSyncDetails", "DM_DOMAIN_ID", "count"));
                final int failedOPcount = DBUtil.getRecordCount(connection, RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
                IDPSlogger.AUDIT.log(Level.INFO, "{0} op dir sync in failed state", new Object[] { failedOPcount });
                if (failedOPcount > 0) {
                    IDPSlogger.AUDIT.log(Level.INFO, "hence disabling statusSyncEnabled");
                    MDMFeatureParamsHandler.updateMDMFeatureParameter("statusSyncEnabled", String.valueOf(false));
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in reseting sync progress", e);
        }
    }
    
    public void validateData(final Connection connection, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Integer syncType, final String domainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClientID) throws Exception {
    }
    
    public void handleError(final Properties dmDomainProps, final Throwable thrown, final String errorType) {
        if (!IdpsUtil.isStringEmpty(errorType) && errorType.startsWith("DOMAIN_UNREACHABLE_ERROR")) {
            String errMsg = thrown.getMessage();
            final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            errMsg = errMsg.substring("DOMAIN_UNREACHABLE_ERROR".length());
            try {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "REMARKS", errMsg);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.INFO, null, ex);
            }
        }
    }
    
    public void handleSuccess(final String dmDomainName, final Long customerID, final Long dmDomainID) {
    }
    
    static {
        OnpremiseADAccessProvider.adAccessProvider = null;
    }
}
