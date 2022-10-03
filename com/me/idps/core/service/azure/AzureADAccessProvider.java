package com.me.idps.core.service.azure;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.me.idps.core.crud.DomainDataPopulator;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import com.me.idps.core.util.DirectoryGroupOnConfig;
import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.tree.datahandler.JSONDataHandler;
import java.util.Collection;
import java.util.Arrays;
import com.me.idps.core.util.DirectoryDiffDetailsHandler;
import com.me.idps.core.sync.asynch.DirectoryDataReceiver;
import java.util.Date;
import org.json.JSONArray;
import java.text.ParseException;
import com.adventnet.persistence.DataAccessException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.me.idps.core.util.NumberValidationUtil;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.util.List;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.Properties;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.oauth.OauthException;
import com.me.idps.core.util.DirectorySyncErrorHandler;
import com.me.idps.core.oauth.OauthUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.idps.core.factory.IdpsAccessAPI;

public class AzureADAccessProvider implements IdpsAccessAPI
{
    private static final int MEMBERSHIP_CHANGE_TYPE = 9;
    private HashMap<String, JSONObject> adDomainAccessToken;
    private static AzureADAccessProvider aadAccessProvider;
    private final String[] azureADScopes;
    
    public AzureADAccessProvider() {
        this.azureADScopes = new String[] { "https://graph.windows.net/Directory.Read.All" };
        if (this.adDomainAccessToken == null) {
            this.adDomainAccessToken = new HashMap<String, JSONObject>();
        }
    }
    
    public static AzureADAccessProvider getInstance() {
        if (AzureADAccessProvider.aadAccessProvider == null) {
            AzureADAccessProvider.aadAccessProvider = new AzureADAccessProvider();
        }
        return AzureADAccessProvider.aadAccessProvider;
    }
    
    private JSONObject executeDMHttpRequest(final DMHttpRequest dmHttpRequest, final boolean logResponseInFine) throws JSONException {
        if (logResponseInFine) {
            IDPSlogger.SOM.log(Level.FINE, "hitting url : {0}", new Object[] { String.valueOf(dmHttpRequest.url) });
        }
        final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
        final String responseString = dmHttpResponse.responseBodyAsString;
        if (logResponseInFine) {
            IDPSlogger.SOM.log(Level.FINE, "response : {0}", new Object[] { String.valueOf(responseString) });
        }
        if (SyMUtil.isStringEmpty(responseString)) {
            throw new JSONException("response is empty");
        }
        JSONObject resultJSObject = new JSONObject();
        if (responseString != null) {
            try {
                resultJSObject = new JSONObject(responseString);
                if (resultJSObject.length() == 0) {
                    throw new JSONException("response json is empty");
                }
            }
            catch (final JSONException ex) {
                IDPSlogger.SOM.log(Level.INFO, "resp {0}", new Object[] { responseString });
                throw ex;
            }
        }
        return resultJSObject;
    }
    
    public JSONObject executeDMHttpRequest(final DMHttpRequest dmHttpRequest) throws JSONException {
        return this.executeDMHttpRequest(dmHttpRequest, false);
    }
    
    public void hideOrShowOauthMessage(final Long customerID) {
        try {
            boolean showAzureReAuth = false;
            boolean showAzureAppConfig = false;
            boolean showAzureInvalidClient = false;
            final Criteria criteria = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)3, 0)).and(new Criteria(Column.getColumn("DMDomainSyncDetails", "REMARKS"), (Object)"*oauth*", 2, false));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomainSyncDetails"));
            selectQuery.addJoin(new Join("DMDomainSyncDetails", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject resDO = SyMUtil.getPersistence().get(selectQuery);
            if (resDO != null && !resDO.isEmpty()) {
                final Iterator itr = resDO.getRows("DMDomainSyncDetails");
                while (itr != null && itr.hasNext() && (!showAzureReAuth || !showAzureAppConfig)) {
                    final Row row = itr.next();
                    final String remarks = (String)row.get("REMARKS");
                    if (!SyMUtil.isStringEmpty(remarks)) {
                        if (remarks.contains("oauthid")) {
                            showAzureReAuth = true;
                        }
                        else if (remarks.contains("integrateAzureOauth")) {
                            showAzureAppConfig = true;
                        }
                        else {
                            if (!remarks.contains("oauth")) {
                                continue;
                            }
                            showAzureInvalidClient = true;
                        }
                    }
                }
            }
            if (customerID != null) {
                if (showAzureAppConfig) {
                    MessageProvider.getInstance().unhideMessage("IDP_AZURE_OAUTH_MSG", customerID);
                    IDPSlogger.AUDIT.log(Level.INFO, "opened {0} for {1}", new Object[] { "IDP_AZURE_OAUTH_MSG", String.valueOf(customerID) });
                }
                else {
                    MessageProvider.getInstance().hideMessage("IDP_AZURE_OAUTH_MSG", customerID);
                }
                if (showAzureReAuth) {
                    MessageProvider.getInstance().unhideMessage("IDP_RE_OAUTH", customerID);
                    IDPSlogger.AUDIT.log(Level.INFO, "opened {0} for {1}", new Object[] { "IDP_RE_OAUTH", String.valueOf(customerID) });
                }
                else {
                    MessageProvider.getInstance().hideMessage("IDP_RE_OAUTH", customerID);
                }
                if (showAzureInvalidClient) {
                    MessageProvider.getInstance().unhideMessage("IDP_AZURE_INVALID_CLIENT_MSG", customerID);
                    IDPSlogger.AUDIT.log(Level.INFO, "opened {0} for {1}", new Object[] { "IDP_AZURE_INVALID_CLIENT_MSG", String.valueOf(customerID) });
                }
                else {
                    MessageProvider.getInstance().hideMessage("IDP_AZURE_INVALID_CLIENT_MSG", customerID);
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "hideOrShowOauthMessage", e);
        }
    }
    
    private JSONObject getAccessTokenFormOauthUtil(final Long domainId, final Long customerId) throws JSONException {
        JSONObject obj = null;
        try {
            final String accessToken = OauthUtil.getInstance().fetchAccessTokenFromDomainId(domainId, customerId, this.azureADScopes, 201);
            obj = new JSONObject();
            obj.put("access_token", (Object)accessToken);
        }
        catch (final JSONException e) {
            IDPSlogger.SOM.log(Level.SEVERE, "JSON exception ", (Throwable)e);
            throw e;
        }
        catch (final OauthException e2) {
            final String eMsg = e2.getMessage();
            if (!SyMUtil.isStringEmpty(eMsg) && eMsg.equalsIgnoreCase("invalid_client")) {
                IDPSlogger.ERR.log(Level.SEVERE, "Invalid client for azure oauth", e2);
                DirectorySyncErrorHandler.getInstance().handleError(domainId, null, null, 3, e2, "OAUTH_INVALID_CLIENT_ERROR");
            }
            else {
                IDPSlogger.ERR.log(Level.SEVERE, "Oauth exception getAccessTokenFormOauthUtil", e2);
                DirectorySyncErrorHandler.getInstance().handleError(domainId, null, null, 3, e2, "DOMAIN_EMPTYUSERSLIST_ERROR");
            }
        }
        return obj;
    }
    
    private JSONObject getAccessTokenFromUserCredentials(final Long customerID, final String adDomainName, final String username, final String password) throws JSONException {
        final String tenantAuthority = "https://login.microsoftonline.com/{0}/oauth2/token".replace("{0}", adDomainName);
        final Properties azureAppDetails = IdpsFactoryProvider.getOauthImpl(201).fetchMetadata(customerID, null);
        final HashMap<String, String> bodyData = new HashMap<String, String>();
        bodyData.put("username", username);
        bodyData.put("password", password);
        bodyData.put("client_id", String.valueOf(((Hashtable<K, Object>)azureAppDetails).get("OAUTH_CLIENT_ID")));
        bodyData.put("client_secret", String.valueOf(((Hashtable<K, Object>)azureAppDetails).get("OAUTH_CLIENT_SECRET")));
        bodyData.put("resource", "https://graph.windows.net");
        bodyData.put("scope", "openid");
        bodyData.put("grant_type", "password");
        final String encodedBodyData = SyMUtil.encodeURLbodyParams((HashMap)bodyData);
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = tenantAuthority;
        dmHttpRequest.data = encodedBodyData.getBytes();
        dmHttpRequest.method = "POST";
        final JSONObject headers = new JSONObject();
        headers.put("Content-Length", encodedBodyData.getBytes().length);
        dmHttpRequest.headers = headers;
        final JSONObject resultJSObject = this.executeDMHttpRequest(dmHttpRequest);
        try {
            ((AzureOauthImpl)IdpsFactoryProvider.getOauthImpl(201)).handleErr(customerID, resultJSObject);
        }
        catch (final OauthException e) {
            final String eMsg = e.getMessage();
            if (!SyMUtil.isStringEmpty(eMsg) && eMsg.contains("invalid_client")) {
                MessageProvider.getInstance().unhideMessage("IDP_AZURE_INVALID_CLIENT_MSG", customerID);
            }
        }
        return resultJSObject;
    }
    
    public JSONObject getAccessTokenForDomain(final Properties domain_props) throws Exception {
        final Long domainId = ((Hashtable<K, Long>)domain_props).get("DOMAIN_ID");
        final Long customerID = ((Hashtable<K, Long>)domain_props).get("CUSTOMER_ID");
        final JSONObject accessTokenDetails = getInstance().getAccessTokenFormOauthUtil(domainId, customerID);
        return accessTokenDetails;
    }
    
    public JSONObject getAccessTokenForDomain(final String adDomainName, final Long customerID) throws Exception {
        final Properties domain_props = DMDomainDataHandler.getInstance().getDomainProps(adDomainName, customerID, 3);
        return this.getAccessTokenForDomain(domain_props);
    }
    
    public int validatePasswordWithErrorCode(final Long customerID, final String adDomainName, final String username, final String password) {
        int errorcode = 80006;
        try {
            final JSONObject authAccessTokenDetails = this.getAccessTokenFromUserCredentials(customerID, adDomainName, username, password);
            if (authAccessTokenDetails != null) {
                if (authAccessTokenDetails.has("access_token")) {
                    errorcode = 0;
                    IDPSlogger.SOM.log(Level.INFO, "recieved auth token successfully");
                }
                else if (authAccessTokenDetails.has("ERROR_CODE")) {
                    errorcode = authAccessTokenDetails.getInt("ERROR_CODE");
                }
            }
        }
        catch (final Exception ex) {
            IDPSlogger.SOM.log(Level.SEVERE, null, ex);
        }
        return errorcode;
    }
    
    private Properties parseResponse(final JSONObject result, final List<Properties> compiledList, final List<Integer> parseForResourceTypes) throws JSONException, DataAccessException {
        final Properties tokenProperties = new Properties();
        try {
            final String statusAttrKey = DirectoryAttributeConstants.getAttrKey(118L);
            if (!result.has("value")) {
                throw new JSONException("value key missing in JSON" + result.toString());
            }
            final JSONArray jsArray = result.getJSONArray("value");
            for (int i = 0; i < jsArray.length(); ++i) {
                final JSONObject jsObject = jsArray.getJSONObject(i);
                final String oDataType = (String)jsObject.get("odata.type");
                final int resourceType = this.getResourceType(oDataType);
                if (parseForResourceTypes.contains(resourceType) || (parseForResourceTypes.contains(7) && resourceType == 9)) {
                    final Properties temp = new Properties();
                    ((Hashtable<String, Integer>)temp).put("RESOURCE_TYPE", resourceType);
                    boolean deletedObj = false;
                    if (jsObject.has("aad.isSoftDeleted") || jsObject.has("aad.isDeleted")) {
                        deletedObj = true;
                    }
                    if (jsObject.has("deletionTimeStamp") && SyMUtil.isStringValid(String.valueOf(jsObject.get("deletionTimeStamp")))) {
                        deletedObj = true;
                    }
                    ((Hashtable<String, Integer>)temp).put(statusAttrKey, 1);
                    if (deletedObj) {
                        ((Hashtable<String, Integer>)temp).put(statusAttrKey, 4);
                    }
                    else if (jsObject.has("accountEnabled")) {
                        final Boolean val = Boolean.valueOf(String.valueOf(jsObject.get("accountEnabled")));
                        if (!val) {
                            ((Hashtable<String, Integer>)temp).put(statusAttrKey, 3);
                        }
                    }
                    ((Hashtable<String, String>)temp).put("objectGUID", String.valueOf(jsObject.get("objectId")));
                    switch (resourceType) {
                        case 2: {
                            if (jsObject.has("userPrincipalName") && jsObject.has("objectId")) {
                                ((Hashtable<String, String>)temp).put("mail", jsObject.optString("mail", "---"));
                                ((Hashtable<String, String>)temp).put("userPrincipalName", String.valueOf(jsObject.get("userPrincipalName")));
                                if (jsObject.has("givenName")) {
                                    ((Hashtable<String, String>)temp).put("givenName", String.valueOf(jsObject.get("givenName")));
                                }
                                if (jsObject.has("surname")) {
                                    ((Hashtable<String, String>)temp).put("sn", String.valueOf(jsObject.get("surname")));
                                }
                                if (jsObject.has("displayName")) {
                                    ((Hashtable<String, String>)temp).put("displayName", String.valueOf(jsObject.get("displayName")));
                                }
                                if (jsObject.has("mobile")) {
                                    String parsedPhoneNumber = "--";
                                    final String value = String.valueOf(jsObject.get("mobile"));
                                    try {
                                        if (!SyMUtil.isStringEmpty(value)) {
                                            final String[] parsedResp = NumberValidationUtil.validateWithAutoFillCountryCode(value);
                                            if (parsedResp != null && parsedResp.length > 0) {
                                                parsedPhoneNumber = parsedResp[0];
                                            }
                                        }
                                    }
                                    catch (final Exception ex) {
                                        IDPSlogger.SOM.log(Level.WARNING, "Invalid phone number format {0}", ex.getMessage());
                                    }
                                    ((Hashtable<String, String>)temp).put("mobile", parsedPhoneNumber);
                                }
                                if (jsObject.has("dirSyncEnabled")) {
                                    ((Hashtable<String, String>)temp).put("dirSyncEnabled", String.valueOf(jsObject.get("dirSyncEnabled")));
                                }
                                final String azureDeptKey = DirectoryAttributeConstants.getAttrKey(128L);
                                if (jsObject.has(azureDeptKey)) {
                                    ((Hashtable<String, String>)temp).put(azureDeptKey, jsObject.getString(azureDeptKey));
                                }
                                compiledList.add(temp);
                                break;
                            }
                            break;
                        }
                        case 7: {
                            if (jsObject.has("objectId")) {
                                if (jsObject.has("displayName")) {
                                    ((Hashtable<String, String>)temp).put("name", String.valueOf(jsObject.get("displayName")));
                                }
                                ((Hashtable<String, Integer>)temp).put("RESOURCE_TYPE", 7);
                                compiledList.add(temp);
                                break;
                            }
                            break;
                        }
                        case 9: {
                            if (jsObject.has("sourceObjectId") && jsObject.has("targetObjectId") && jsObject.has("sourceObjectType") && jsObject.has("associationType")) {
                                final String sourceObjType = String.valueOf(jsObject.get("sourceObjectType"));
                                final String associationType = String.valueOf(jsObject.get("associationType"));
                                if ("Member".equalsIgnoreCase(associationType) && "Group".equalsIgnoreCase(sourceObjType)) {
                                    final List<String> membersChangeList = new ArrayList<String>();
                                    membersChangeList.add((String)jsObject.get("targetObjectId"));
                                    ((Hashtable<String, List<String>>)temp).put("member:" + (deletedObj ? "remove" : "add"), membersChangeList);
                                    ((Hashtable<String, String>)temp).put("objectGUID", String.valueOf(jsObject.get("sourceObjectId")));
                                    ((Hashtable<String, Integer>)temp).put("RESOURCE_TYPE", 7);
                                    temp.remove(statusAttrKey);
                                    compiledList.add(temp);
                                }
                                break;
                            }
                            break;
                        }
                        case 201: {
                            if (jsObject.has("objectId") && !deletedObj) {
                                if (jsObject.has("displayName")) {
                                    ((Hashtable<String, String>)temp).put("name", jsObject.optString("displayName", "--"));
                                    ((Hashtable<String, String>)temp).put("displayName", jsObject.optString("displayName", "--"));
                                }
                                if (jsObject.has("deviceTrustType")) {
                                    ((Hashtable<String, String>)temp).put("deviceTrustType", jsObject.optString("deviceTrustType", "--"));
                                }
                                if (jsObject.has("deviceOSType")) {
                                    ((Hashtable<String, String>)temp).put("deviceOSType", jsObject.optString("deviceOSType", "--"));
                                }
                                if (jsObject.has("deviceId")) {
                                    ((Hashtable<String, String>)temp).put("deviceId", jsObject.optString("deviceId", "--"));
                                }
                                if (jsObject.has("approximateLastLogonTimestamp")) {
                                    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    final String dateString = jsObject.optString("approximateLastLogonTimestamp", "--");
                                    if (!SyMUtil.isStringEmpty(dateString)) {
                                        final Date date = format.parse(dateString);
                                        final long millis = date.getTime();
                                        ((Hashtable<String, Long>)temp).put("lastLogonTime", millis);
                                    }
                                    else {
                                        ((Hashtable<String, String>)temp).put("lastLogonTime", "--");
                                    }
                                }
                                if (!deletedObj) {
                                    final boolean m = jsObject.optBoolean("isManaged", false);
                                    final boolean c = jsObject.optBoolean("isCompliant", false);
                                    int status = 0;
                                    if (m) {
                                        status |= 0x1;
                                    }
                                    if (c) {
                                        status |= 0x2;
                                    }
                                    ((Hashtable<String, Integer>)temp).put("azureDeviceStatus", status);
                                }
                                if (jsObject.has("profileType")) {
                                    ((Hashtable<String, String>)temp).put("profileType", jsObject.optString("profileType", "--"));
                                }
                                compiledList.add(temp);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            if (result.has("odata.nextLink")) {
                String pagingNextToken = String.valueOf(result.get("odata.nextLink"));
                final int tokenIndex = pagingNextToken.indexOf("$skiptoken");
                pagingNextToken = pagingNextToken.substring(tokenIndex);
                ((Hashtable<String, String>)tokenProperties).put("odata.nextLink", pagingNextToken);
            }
            if (result.has("aad.nextLink")) {
                String deltaPagingNextLink = String.valueOf(result.get("aad.nextLink"));
                final int tokenIndex = deltaPagingNextLink.indexOf("deltaLink");
                deltaPagingNextLink = deltaPagingNextLink.substring(tokenIndex);
                ((Hashtable<String, String>)tokenProperties).put("aad.nextLink", deltaPagingNextLink);
            }
            if (result.has("aad.deltaLink")) {
                String deltaLink = String.valueOf(result.get("aad.deltaLink"));
                final int tokenIndex = deltaLink.indexOf("deltaLink");
                deltaLink = deltaLink.substring(tokenIndex);
                ((Hashtable<String, String>)tokenProperties).put("aad.deltaLink", deltaLink);
            }
        }
        catch (final JSONException | DataAccessException ex2) {
            IDPSlogger.SOM.log(Level.SEVERE, null, ex2);
            throw ex2;
        }
        catch (final ParseException e) {
            IDPSlogger.SOM.log(Level.SEVERE, "Date format error", e);
        }
        if (compiledList.size() == 0 || compiledList.isEmpty()) {
            IDPSlogger.SOM.log(Level.INFO, "hawww : " + result.toString());
        }
        return tokenProperties;
    }
    
    private String getAzureObjType(final int resType) {
        if (resType == 2) {
            return "Microsoft.DirectoryServices.User";
        }
        if (resType == 7) {
            return "Microsoft.DirectoryServices.Group";
        }
        if (resType == 201) {
            return "Microsoft.DirectoryServices.Device";
        }
        return null;
    }
    
    private int getResourceType(final String azureObjType) {
        switch (azureObjType) {
            case "Microsoft.DirectoryServices.User": {
                return 2;
            }
            case "Microsoft.DirectoryServices.Group": {
                return 7;
            }
            case "Microsoft.DirectoryServices.DirectoryLinkChange": {
                return 9;
            }
            case "Microsoft.DirectoryServices.Device": {
                return 201;
            }
            default: {
                return -1;
            }
        }
    }
    
    private String generateRestAPIurl(final String domainName, final List<Integer> resourceType, final String filterChar, final Integer filterOperator, final String skipToken, final String deltaLink) {
        String restAPIurl = "https://graph.windows.net/" + domainName.toLowerCase() + "/";
        if (filterChar != null && !filterChar.equalsIgnoreCase("All")) {
            String filterBy = "";
            switch (resourceType.get(0)) {
                case 2: {
                    restAPIurl += "users";
                    filterBy = "userPrincipalName";
                    break;
                }
                case 7: {
                    restAPIurl += "groups";
                    filterBy = "displayName";
                    break;
                }
                case 201: {
                    restAPIurl += "devices";
                    filterBy = "displayName";
                    break;
                }
            }
            restAPIurl += "?api-version=1.6&$filter=";
            if (filterOperator == 1) {
                restAPIurl = restAPIurl + "startswith(" + filterBy + ",'" + filterChar + "')";
            }
            else if (filterOperator == 0) {
                restAPIurl = restAPIurl + "(" + filterBy + "+eq+'" + filterChar + "')";
            }
        }
        else {
            restAPIurl += "directoryObjects?api-version=1.6";
            if (resourceType != null && !resourceType.isEmpty()) {
                restAPIurl += "&$filter=";
                boolean putOr = false;
                for (final Integer resType : resourceType) {
                    if (putOr) {
                        restAPIurl += "%20or%20";
                    }
                    restAPIurl = restAPIurl + "isof('" + this.getAzureObjType(resType) + "')";
                    putOr = true;
                }
            }
            if (deltaLink != null) {
                restAPIurl = restAPIurl + "&" + deltaLink;
            }
            else {
                restAPIurl += "&$top=999";
                if (skipToken != null) {
                    restAPIurl = restAPIurl + "&" + skipToken;
                }
            }
        }
        IDPSlogger.SOM.log(Level.FINEST, "rest api url : " + restAPIurl.toString());
        return restAPIurl;
    }
    
    private void postDataToWorkFlow(final ArrayList<Properties> compiledList, final boolean isFirstList, final boolean moreEntries, final Long customerID, final String adDomainName, final String callbackClass, final int startIndex, final int endIndex) throws Exception {
        IDPSlogger.SOM.log(Level.INFO, "posting data to:" + callbackClass + "for " + adDomainName + " isFirtList:" + isFirstList + " isLastList:" + !moreEntries + " chunk size:" + compiledList.size());
        new DirectoryDataReceiver().proccessFetchedADData(null, compiledList, adDomainName, customerID, -1, compiledList.size(), startIndex, endIndex, isFirstList, !moreEntries);
    }
    
    public org.json.simple.JSONArray getGroupMembers(final org.json.simple.JSONArray compiledList, final Properties domainProps) throws Exception {
        final Long customerID = ((Hashtable<K, Long>)domainProps).get("CUSTOMER_ID");
        final String adDomainName = domainProps.getProperty("AD_DOMAIN_NAME");
        final JSONObject accessTokenDetails = this.getAccessTokenForDomain(adDomainName, customerID);
        final String accessToken = String.valueOf(accessTokenDetails.get("access_token"));
        for (int i = 0; i < compiledList.size(); ++i) {
            final org.json.simple.JSONObject directoryProps = (org.json.simple.JSONObject)compiledList.get(i);
            final Integer objResType = Integer.valueOf(String.valueOf(directoryProps.get((Object)"RESOURCE_TYPE")));
            if (objResType == 7) {
                String skipToken = "";
                final org.json.simple.JSONArray members = new org.json.simple.JSONArray();
                final String objGUID = (String)directoryProps.get((Object)"objectGUID");
                JSONObject groupMemberObject;
                String rawSkipToken;
                for (boolean loopForMoreMembers = true; loopForMoreMembers; loopForMoreMembers = true, rawSkipToken = String.valueOf(groupMemberObject.get("odata.nextLink")), skipToken = "&" + rawSkipToken.substring(rawSkipToken.indexOf("$skiptoken"))) {
                    final String restAPIurl = "https://graph.windows.net/" + adDomainName.toLowerCase() + "/groups/" + objGUID + "/$links/members?" + "api-version=1.6" + skipToken;
                    loopForMoreMembers = false;
                    final JSONObject jsObject = new JSONObject();
                    jsObject.put("securityEnabledOnly", false);
                    final DMHttpRequest dmHttpRequest = new DMHttpRequest();
                    dmHttpRequest.url = restAPIurl;
                    dmHttpRequest.method = "GET";
                    dmHttpRequest.data = jsObject.toString().getBytes();
                    final JSONObject headers = new JSONObject();
                    headers.put("Authorization", (Object)accessToken);
                    headers.put("Content-Type", (Object)"application/json");
                    dmHttpRequest.headers = headers;
                    groupMemberObject = this.executeDMHttpRequest(dmHttpRequest);
                    if (groupMemberObject.has("value")) {
                        final JSONArray membersTemp = groupMemberObject.getJSONArray("value");
                        for (int j = 0; j < membersTemp.length(); ++j) {
                            final JSONObject userDetails = membersTemp.getJSONObject(j);
                            final String url = String.valueOf(userDetails.get("url"));
                            final String prefix = "https://graph.windows.net/" + adDomainName.toLowerCase() + "/directoryObjects/";
                            final int beginIndex = url.indexOf(prefix);
                            final int endIndex = url.indexOf("/Microsoft.DirectoryServices.");
                            try {
                                final String objectGUID = url.substring(beginIndex + prefix.length(), endIndex);
                                members.add((Object)objectGUID);
                            }
                            catch (final Exception ex) {
                                IDPSlogger.SOM.log(Level.SEVERE, "exception parsing " + url.toString());
                            }
                        }
                    }
                    if (groupMemberObject.has("odata.nextLink")) {}
                }
                directoryProps.put((Object)"member", (Object)members);
            }
        }
        return compiledList;
    }
    
    private String listToString(final List<Integer> list) {
        final StringBuilder sb = new StringBuilder();
        for (final Object obj : list) {
            sb.append(String.valueOf(obj));
        }
        return sb.toString();
    }
    
    private HashMap getAvailableADObjectList(final Properties dmDomainProps, final JSONObject authAccessTokenDetails, final Long customerID, final String adDomainName, final List<Integer> resourceType, final String filterChar, final Integer filterOperator, String deltaLink, final String callbackClass) throws Exception {
        IDPSlogger.SOM.log(Level.INFO, "Retrieving Ad objects for adDomainName:" + adDomainName + " resourceType:" + this.listToString(resourceType) + " filterChar" + filterChar + "filterOperator:" + filterOperator + " deltaLink present : " + ((boolean)(deltaLink != null) && !deltaLink.equalsIgnoreCase("deltaLink=")) + " callbackclass:" + callbackClass);
        ArrayList<Properties> compiledList = new ArrayList<Properties>();
        boolean isFirstList = true;
        int page = 1;
        try {
            String nextPageToken = null;
            boolean moreEntries = true;
            int startIndex = 0;
            int endIndex = 0;
            while (moreEntries) {
                moreEntries = false;
                final String accessToken = String.valueOf(authAccessTokenDetails.get("access_token"));
                if (resourceType.contains(205)) {
                    resourceType.remove(new Integer(205));
                }
                final String restAPIurl = this.generateRestAPIurl(adDomainName, resourceType, filterChar, filterOperator, nextPageToken, deltaLink);
                final DMHttpRequest dmHttpRequest = new DMHttpRequest();
                dmHttpRequest.url = restAPIurl;
                dmHttpRequest.method = "GET";
                final JSONObject headers = new JSONObject();
                headers.put("Authorization", (Object)accessToken);
                dmHttpRequest.headers = headers;
                IDPSlogger.SOM.log(Level.INFO, "making request... page count = " + page);
                final JSONObject resultJSObject = this.executeDMHttpRequest(dmHttpRequest, true);
                final Properties responseTokens = this.parseResponse(resultJSObject, compiledList, resourceType);
                IDPSlogger.SOM.log(Level.INFO, "received response... page count = " + page++);
                if (responseTokens.containsKey("aad.nextLink")) {
                    deltaLink = responseTokens.getProperty("aad.nextLink");
                    moreEntries = true;
                }
                else {
                    if (responseTokens.containsKey("aad.deltaLink")) {
                        deltaLink = responseTokens.getProperty("aad.deltaLink");
                    }
                    moreEntries = false;
                    IDPSlogger.SOM.log(Level.INFO, " no more entries.. ");
                }
                if (responseTokens.containsKey("odata.nextLink")) {
                    nextPageToken = responseTokens.getProperty("odata.nextLink");
                    moreEntries = true;
                }
                if (callbackClass != null) {
                    if (!moreEntries) {
                        DirectoryDiffDetailsHandler.getInstance().addOrUpdateDirectorySyncDiffState(dmDomainProps, deltaLink);
                    }
                    startIndex = endIndex;
                    endIndex += compiledList.size();
                    this.postDataToWorkFlow(compiledList, isFirstList, moreEntries, customerID, adDomainName, callbackClass, startIndex, endIndex);
                    if (isFirstList) {
                        isFirstList = false;
                    }
                    compiledList = new ArrayList<Properties>();
                }
            }
        }
        catch (final JSONException ex) {
            IDPSlogger.SOM.log(Level.SEVERE, "Exception occurred while parsing JSON", (Throwable)ex);
            throw ex;
        }
        if (callbackClass == null) {
            IDPSlogger.SOM.log(Level.INFO, "number of object received from Azure AD = " + compiledList.size());
        }
        final HashMap queryResult = new HashMap();
        queryResult.put("DELTA_TOKEN", deltaLink);
        queryResult.put("LIST_OF_OBJECTS", compiledList);
        return queryResult;
    }
    
    private HashMap getAvailableADObjectList(final Properties domainProps, final List<Integer> resourceType, final String deltaLink, final String workflow) throws Exception {
        final Long customerID = ((Hashtable<K, Long>)domainProps).get("CUSTOMER_ID");
        final String adDomainName = domainProps.getProperty("AD_DOMAIN_NAME");
        final JSONObject authAccessTokenDetails = this.getAccessTokenForDomain(domainProps);
        if (authAccessTokenDetails != null && authAccessTokenDetails.has("access_token")) {
            return this.getAvailableADObjectList(domainProps, authAccessTokenDetails, customerID, adDomainName, resourceType, null, null, deltaLink, workflow);
        }
        return null;
    }
    
    public ArrayList<Properties> getAvailableADObjectList(final Properties domainProps, final Integer resourceType, final String filterChar, final int filterOperator) throws Exception {
        final Long customerID = ((Hashtable<K, Long>)domainProps).get("CUSTOMER_ID");
        final String adDomainName = domainProps.getProperty("AD_DOMAIN_NAME");
        final JSONObject authAccessTokenDetails = this.getAccessTokenForDomain(adDomainName, customerID);
        if (authAccessTokenDetails != null && authAccessTokenDetails.has("access_token")) {
            final HashMap result = this.getAvailableADObjectList(domainProps, authAccessTokenDetails, customerID, adDomainName, new ArrayList<Integer>(Arrays.asList(resourceType)), filterChar, filterOperator, "deltaLink=", null);
            return result.get("LIST_OF_OBJECTS");
        }
        return null;
    }
    
    @Override
    public boolean isUserMemberOfAnyGroup(final String domainName, final String domainUserName, final String emailAddress, final String domainPassword, final List<String> distinguishedNames, final List<String> guids, final Long customerID) throws Exception {
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, 3);
        final JSONObject authAccessTokenDetails = this.getAccessTokenFormOauthUtil(((Hashtable<K, Long>)domainProps).get("DOMAIN_ID"), customerID);
        return authAccessTokenDetails != null && authAccessTokenDetails.has("access_token") && this.isUserMemberOfAnyGroup(authAccessTokenDetails, domainProps.getProperty("AD_DOMAIN_NAME"), domainUserName, guids);
    }
    
    private boolean isUserMemberOfAnyGroup(final JSONObject authAccessTokenDetails, final String adDomainName, final String userName, final List<String> guids) throws Exception {
        try {
            IDPSlogger.SOM.log(Level.INFO, "In isUserMemberOfAnyGroup: authAccessTokenDetails: " + authAccessTokenDetails.toString() + " userName:" + userName + " guids:" + guids);
            final String accessToken = String.valueOf(authAccessTokenDetails.get("access_token"));
            final String restAPIurl = this.getAPIURLForGroupMembership(adDomainName, userName);
            final JSONObject json = new JSONObject();
            json.put("groupIds", (Object)new JSONDataHandler().convertListToJSONArray((List)guids));
            final DMHttpRequest dmHttpRequest = new DMHttpRequest();
            dmHttpRequest.url = restAPIurl;
            dmHttpRequest.method = "POST";
            dmHttpRequest.headers = new JSONObject().put("Authorization", (Object)accessToken).put("Content-Type", (Object)"application/json");
            dmHttpRequest.data = json.toString().getBytes();
            final JSONObject resultJSONObject = this.executeDMHttpRequest(dmHttpRequest);
            if (!resultJSONObject.has("value")) {
                throw new JSONException("JSON does not have value key");
            }
            final boolean result = resultJSONObject.getJSONArray("value").length() > 0;
            IDPSlogger.SOM.log(Level.INFO, "isUserMemberOfAnyGroup:" + result);
            return result;
        }
        catch (final JSONException ex) {
            IDPSlogger.SOM.log(Level.SEVERE, "Exception occurred while parsing JSON : {0}", (Throwable)ex);
            throw ex;
        }
    }
    
    private String getAPIURLForGroupMembership(final String domainName, final String userName) {
        final String restAPIURL = "https://graph.windows.net/" + domainName.toLowerCase() + "/" + "users" + "/" + userName + "/checkMemberGroups?" + "api-version=1.6";
        IDPSlogger.SOM.log(Level.INFO, "APIURL for: domainName: " + domainName + " userName:" + userName + " restAPIURL:" + restAPIURL);
        return restAPIURL;
    }
    
    @Override
    public List getAvailableADObjectList(final String domainName, final int resourceType, final List listAttributes, final String filter, final Long customerID) throws Exception {
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, 3);
        return this.getAvailableADObjectList(domainProps, resourceType, filter, 1);
    }
    
    @Override
    public boolean validatePassword(final String adDomainName, final String userName, final String password, final Long customerID) {
        try {
            return this.validatePasswordWithErrorCode(customerID, adDomainName, userName, password) == 0;
        }
        catch (final Exception ex) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured in validating password : ", ex);
            return false;
        }
    }
    
    @Override
    public void fetchBulkADData(final Properties dmDomainProps, final List<Integer> syncObjects, final boolean doFullSync) throws Exception {
        final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        String deltaToken = "deltaLink=";
        if (!doFullSync) {
            final org.json.simple.JSONObject deltaLinkDetails = DirectoryDiffDetailsHandler.getInstance().getDirectorySyncDiffState(dmDomainID);
            if (deltaLinkDetails.containsKey((Object)"ATTRIBUTE_3")) {
                deltaToken = (String)deltaLinkDetails.get((Object)"ATTRIBUTE_3");
            }
        }
        if (SyMUtil.isStringEmpty(deltaToken)) {
            deltaToken = "deltaLink=";
        }
        final String workFlow = "com.me.idps.core.sync.asynch.DirectoryDataReceiver";
        this.getAvailableADObjectList(dmDomainProps, syncObjects, deltaToken, workFlow);
    }
    
    @Override
    public Properties getThisADObjectProperties(final String domainName, final int resourceType, final List listAttributes, final String name, final Long customerID) throws Exception {
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, 3);
        final ArrayList<Properties> userDetails = this.getAvailableADObjectList(domainProps, resourceType, name, 0);
        if (userDetails == null || userDetails.isEmpty()) {
            return new Properties();
        }
        return userDetails.iterator().next();
    }
    
    @Override
    public Properties getThisADUserProperties(final String domainName, final String userName, final String password, final List listAttributes, final Long customerID) {
        final Properties domainProperties = null;
        try {
            DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, 3);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
        List<Properties> userDetails = null;
        if (!SyMUtil.isStringEmpty(password)) {
            final String adminUserName = domainProperties.getProperty("CRD_USERNAME");
            final String adminPassword = domainProperties.getProperty("CRD_PASSWORD");
            ((Hashtable<String, String>)domainProperties).put("CRD_USERNAME", userName);
            ((Hashtable<String, String>)domainProperties).put("CRD_PASSWORD", password);
            try {
                userDetails = this.getAvailableADObjectList(domainProperties, 2, userName, 0);
            }
            catch (final Exception ex2) {
                IDPSlogger.SOM.log(Level.SEVERE, null, ex2);
            }
            ((Hashtable<String, String>)domainProperties).put("CRD_USERNAME", adminUserName);
            ((Hashtable<String, String>)domainProperties).put("CRD_PASSWORD", adminPassword);
        }
        Label_0183: {
            if (userDetails != null) {
                if (!userDetails.isEmpty()) {
                    break Label_0183;
                }
            }
            try {
                userDetails = this.getAvailableADObjectList(domainProperties, 2, userName, 0);
            }
            catch (final Exception ex3) {
                IDPSlogger.SOM.log(Level.SEVERE, null, ex3);
            }
        }
        if (userDetails == null || userDetails.isEmpty()) {
            return new Properties();
        }
        return userDetails.iterator().next();
    }
    
    @Override
    public boolean isADDomainReachable(final Properties dmDomainProps) throws JSONException {
        final Long domainId = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        final Long customerId = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
        final JSONObject s = this.getAccessTokenFormOauthUtil(domainId, customerId);
        return s != null && !IdpsUtil.isStringEmpty(s.optString("access_token", "--"));
    }
    
    @Override
    public void postSyncOperations(final org.json.simple.JSONObject dmDomainProperties, final Boolean isFullSync, final org.json.simple.JSONObject postSyncOPDetails) throws DataAccessException {
    }
    
    @Override
    public int getResourceType(final int resType) {
        if (resType == 7) {
            return 101;
        }
        return resType;
    }
    
    private org.json.simple.JSONArray getAzureDeviceProp(final String accessToken, final String domainName, final String registeredMembers, final String deviceObjId) throws JSONException {
        final org.json.simple.JSONArray compiledList = new org.json.simple.JSONArray();
        final int page = 1;
        try {
            final String restAPIurl = "https://graph.windows.net/" + domainName.toLowerCase() + "/devices/" + deviceObjId + "/" + registeredMembers + "?" + "api-version=1.6";
            final DMHttpRequest dmHttpRequest = new DMHttpRequest();
            dmHttpRequest.url = restAPIurl;
            dmHttpRequest.method = "GET";
            final JSONObject headers = new JSONObject();
            headers.put("Authorization", (Object)accessToken);
            dmHttpRequest.headers = headers;
            IDPSlogger.SOM.log(Level.INFO, "making device request... page count = " + page);
            final JSONObject resultJSObject = this.executeDMHttpRequest(dmHttpRequest, true);
            if (resultJSObject.has("value")) {
                final JSONArray jsArray = resultJSObject.getJSONArray("value");
                for (int i = 0; i < jsArray.length(); ++i) {
                    final JSONObject jsObject = jsArray.getJSONObject(i);
                    final String oDataType = (String)jsObject.get("odata.type");
                    if (oDataType.equalsIgnoreCase(this.getAzureObjType(2))) {
                        final String oid = jsObject.getString("objectId");
                        compiledList.add((Object)oid);
                    }
                }
            }
            return compiledList;
        }
        catch (final JSONException ex) {
            IDPSlogger.SOM.log(Level.SEVERE, "Exception occurred while parsing JSON", (Throwable)ex);
            throw ex;
        }
    }
    
    @Override
    public List<DirectoryGroupOnConfig> getGroupOnProps(final List<Integer> objectsToBeSynced) {
        final DirectoryGroupOnConfig directoryGroupOnConfig = new DirectoryGroupOnConfig(102L, 7, new ArrayList<Integer>(Arrays.asList(7, 2)));
        final List<DirectoryGroupOnConfig> groupOnDetails = new ArrayList<DirectoryGroupOnConfig>();
        if (objectsToBeSynced.contains(7)) {
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
    public boolean alwaysDoFullSync() {
        return false;
    }
    
    @Override
    public boolean isGUIDresTypeunique() {
        return false;
    }
    
    @Override
    public JSONObject getCustomParams(final org.json.simple.JSONObject props) throws JSONException {
        final JSONObject urlObject = new JSONObject();
        final Long userID = (Long)props.get((Object)"USER_ID");
        final Long customerID = (Long)props.get((Object)"CUSTOMER_ID");
        final String spaceSeperatedScopes = (String)props.get((Object)"scope");
        String[] scopes = null;
        if (spaceSeperatedScopes != null) {
            scopes = spaceSeperatedScopes.split(" ");
        }
        final String state = (String)props.get((Object)"state");
        final String url = IdpsFactoryProvider.getOauthImpl(201).getAuthorizeUrl(customerID, userID, scopes, state);
        urlObject.put("url", (Object)url);
        return urlObject;
    }
    
    @Override
    public void doHealthCheck(final Connection connection) {
    }
    
    @Override
    public void validateData(final Connection connection, final Criteria tempCri, final HashMap<String, Criteria> tempValCriMap, final Integer syncType, final String domainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClientID) throws Exception {
        final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.dmDomainProps = dmDomainProps;
        dirProdImplRequest.eventType = IdpEventConstants.CUSTOM_OPS;
        dirProdImplRequest.args = new Object[] { "HANDLE_AZURE_USERS_POSTED_AS_CG", connection, syncType, domainName, customerID, dmDomainID, tempCri };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
    }
    
    @Override
    public void handleError(final Properties dmDomainProps, final Throwable thrown, final String errorType) {
        final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
        final Long customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
        if (errorType.equalsIgnoreCase("DELTA_TOKEN_EXPIRED_ERROR")) {
            try {
                new DirectoryDiffDetailsHandler().addOrUpdateDirectorySyncDiffState(dmDomainID, "");
            }
            catch (final DataAccessException e) {
                IDPSlogger.ERR.log(Level.SEVERE, "Unable to delete delta token");
            }
            DirectoryUtil.getInstance().syncDomain(dmDomainProps, true);
        }
        else if (errorType.equalsIgnoreCase("DOMAIN_EMPTYUSERSLIST_ERROR")) {
            final String state = "%server_url%/webclient#/uems/mdm/enrollment/activeDirectory/list/domains/3?oauthid=-1";
            final String failureResponse = IdpsFactoryProvider.getOauthImpl(201).getAuthorizeUrl(customerID, null, this.azureADScopes, state);
            if (SyMUtil.isStringValid(failureResponse)) {
                try {
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "REMARKS", failureResponse);
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                }
            }
        }
        else if (errorType.equalsIgnoreCase("AZURE_OAUTH_ERROR")) {
            final String state = "/webclient#/uems/mdm/enrollment/activeDirectory/integrateAzureOauth";
            if (SyMUtil.isStringValid(state)) {
                try {
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "REMARKS", state);
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                }
            }
        }
        else if (errorType.equalsIgnoreCase("OAUTH_INVALID_CLIENT_ERROR")) {
            final String state = "/webclient#/uems/mdm/enrollment/activeDirectory/list/oauth";
            if (SyMUtil.isStringValid(state)) {
                try {
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "REMARKS", state);
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                }
            }
        }
        this.hideOrShowOauthMessage(customerID);
    }
    
    @Override
    public void handleSuccess(final String dmDomainName, final Long customerID, final Long dmDomainID) {
        this.hideOrShowOauthMessage(customerID);
    }
    
    @Override
    public Properties preSyncOperations(final org.json.simple.JSONObject dmDomainProps, final int resType, final org.json.simple.JSONArray directoryData, final boolean isFirstList, final boolean isLastList) {
        final Properties processedSyncInput = new Properties();
        ((Hashtable<String, Boolean>)processedSyncInput).put("LAST_COUNT", isLastList);
        ((Hashtable<String, Boolean>)processedSyncInput).put("FIRST_COUNT", isFirstList);
        try {
            final Long customerID = (Long)dmDomainProps.get((Object)"CUSTOMER_ID");
            final String adDomainName = (String)dmDomainProps.get((Object)"NAME");
            final JSONObject authAccessTokenDetails = this.getAccessTokenForDomain(adDomainName, customerID);
            final String accessToken = authAccessTokenDetails.getString("access_token");
            for (int i = 0; i < directoryData.size(); ++i) {
                final org.json.simple.JSONObject j = (org.json.simple.JSONObject)directoryData.get(i);
                if ((long)j.get((Object)"RESOURCE_TYPE") == 201L) {
                    final String deviceoid = (String)j.get((Object)"objectGUID");
                    final org.json.simple.JSONArray registeredOwners = this.getAzureDeviceProp(accessToken, adDomainName, "registeredOwners", deviceoid);
                    final org.json.simple.JSONArray registeredUsers = this.getAzureDeviceProp(accessToken, adDomainName, "registeredUsers", deviceoid);
                    ((org.json.simple.JSONObject)directoryData.get(i)).put((Object)"registeredOwners", (Object)registeredOwners);
                    ((org.json.simple.JSONObject)directoryData.get(i)).put((Object)"registeredUsers", (Object)registeredUsers);
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred while parsing JSON", e);
        }
        ((Hashtable<String, org.json.simple.JSONArray>)processedSyncInput).put("DirResRel", directoryData);
        return processedSyncInput;
    }
    
    private String addOrUpdateAzureAD(final HashMap domainDetails) {
        try {
            final String domainName = domainDetails.get("NAME");
            final Long customerID = domainDetails.get("CUSTOMER_ID");
            final Integer networkType = domainDetails.get("CLIENT_ID");
            final Properties props = new Properties();
            ((Hashtable<String, String>)props).put("NAME", domainName);
            ((Hashtable<String, Integer>)props).put("CLIENT_ID", networkType);
            ((Hashtable<String, Long>)props).put("CUSTOMER_ID", customerID);
            ((Hashtable<String, String>)props).put("DNS_SUFFIX", "---");
            ((Hashtable<String, String>)props).put("CRD_USERNAME", "NA");
            ((Hashtable<String, String>)props).put("CRD_PASSWORD", "NA");
            ((Hashtable<String, String>)props).put("AD_DOMAIN_NAME", domainName);
            ((Hashtable<String, String>)props).put("DC_NAME", domainDetails.get("DC_NAME"));
            ((Hashtable<String, String>)props).put("IS_AD_DOMAIN", domainDetails.get("IS_AD_DOMAIN"));
            final DataObject dmDomainDO = DomainDataPopulator.getInstance().addOrUpdateDMManagedDomain(props);
            IDPSlogger.SOM.log(Level.INFO, "Azure AD Domain details added successfully");
            if (dmDomainDO == null || dmDomainDO.isEmpty()) {
                return String.valueOf(false);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.SOM.log(Level.SEVERE, null, ex);
            return DomainHandler.getInstance().getErrorMessageForErrorID(80006, 3);
        }
        return String.valueOf(true);
    }
    
    @Override
    public String addOrUpdateAD(final HashMap domainDetails) {
        domainDetails.put("IS_AD_DOMAIN", String.valueOf(false));
        domainDetails.put("DC_NAME", "Azure AD Services");
        domainDetails.put("CLIENT_ID", 3);
        return this.addOrUpdateAzureAD(domainDetails);
    }
    
    static {
        AzureADAccessProvider.aadAccessProvider = null;
    }
}
