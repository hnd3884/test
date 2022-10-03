package com.me.mdm.server.conditionalaccess;

import java.util.Hashtable;
import com.me.mdm.api.error.APIHTTPException;
import com.me.idps.core.crud.DomainDataProvider;
import com.me.idps.core.api.DirectoryAPIFacade;
import com.me.idps.core.api.IdpsAPIException;
import com.me.mdm.api.APIUtil;
import org.json.simple.JSONArray;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.idps.core.service.azure.AzureADAccessProvider;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.ClientProtocolException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.HttpHost;
import org.json.JSONException;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class AzureWinCEA
{
    public static final String GRAPH_API_RESOURCE_URL = "https://graph.windows.net";
    public static final String DEVICES_CONTEXT_PATH = "devices";
    public static final String API_VERSION = "api-version=1.6";
    public static final int AZURE_CA_EVENT_ID = 2082;
    public static final int AZURE_MANAGED_STATUS = 3;
    public static final int AZURE_UNMANAGED_STATUS = 0;
    private static AzureWinCEA azureWinCEA;
    
    public static AzureWinCEA getInstance() {
        if (AzureWinCEA.azureWinCEA == null) {
            AzureWinCEA.azureWinCEA = new AzureWinCEA();
        }
        return AzureWinCEA.azureWinCEA;
    }
    
    private String getProxy(final String connectionURL) throws Exception {
        final Properties proxyConf = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        String proxyHost = null;
        String proxyPort = null;
        String userName = null;
        String password = null;
        String proxyScript = null;
        if (proxyConf != null) {
            if (proxyConf.containsKey("proxyScriptEna") && ((Hashtable<K, Object>)proxyConf).get("proxyScriptEna").toString().equals("1")) {
                proxyScript = ((Hashtable<K, String>)proxyConf).get("proxyScript");
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyConf).get("proxyHost");
                proxyPort = ((Hashtable<K, String>)proxyConf).get("proxyPort");
            }
            userName = ((Hashtable<K, String>)proxyConf).get("proxyUser");
            password = ((Hashtable<K, String>)proxyConf).get("proxyPass");
            if (proxyScript != null) {
                final PacProxySelector pacProxySelector = new PacProxySelector((PacScriptSource)new UrlPacScriptSource(proxyScript));
                final List<Proxy> proxyList = pacProxySelector.select(new URI(connectionURL));
                if (proxyList != null && !proxyList.isEmpty()) {
                    for (final Proxy proxy : proxyList) {
                        final SocketAddress address = proxy.address();
                        if (address != null) {
                            proxyHost = ((InetSocketAddress)address).getHostName();
                            proxyPort = Integer.toString(((InetSocketAddress)address).getPort());
                        }
                    }
                }
            }
        }
        String proxy2 = "--,--";
        if (proxyHost != null && !proxyHost.equalsIgnoreCase("") && proxyPort != null && !proxyPort.equalsIgnoreCase("")) {
            proxy2 = proxyHost + "," + proxyPort;
        }
        if (userName != null && !userName.equalsIgnoreCase("") && password != null && !password.equalsIgnoreCase("")) {
            proxy2 = proxy2 + "," + userName + "," + password;
        }
        else {
            proxy2 += ",--,--";
        }
        return proxy2;
    }
    
    private JSONObject responseToJson(final CloseableHttpResponse response) throws IOException, JSONException {
        String responseString = "--";
        int statusCode = -1;
        if (response.getEntity() != null) {
            responseString = EntityUtils.toString(response.getEntity());
        }
        if (response.getStatusLine() != null) {
            statusCode = response.getStatusLine().getStatusCode();
        }
        final JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status-code", statusCode);
        jsonResponse.put("response", (Object)responseString);
        return jsonResponse;
    }
    
    private JSONObject execute(final String uri, final JSONObject body, final JSONObject headers) throws Exception {
        CloseableHttpResponse response2 = null;
        CloseableHttpClient httpclient = null;
        JSONObject jsonResponse = null;
        try {
            final String[] proxyconfig = this.getProxy(uri).split(",");
            RequestConfig config = null;
            CredentialsProvider credsProvider = null;
            if (!proxyconfig[0].equalsIgnoreCase("--") && !proxyconfig[1].equalsIgnoreCase("--")) {
                final HttpHost proxy = new HttpHost(proxyconfig[0], Integer.parseInt(proxyconfig[1]));
                config = RequestConfig.custom().setProxy(proxy).build();
            }
            if (!proxyconfig[2].equalsIgnoreCase("--") && !proxyconfig[3].equalsIgnoreCase("--")) {
                credsProvider = (CredentialsProvider)new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(proxyconfig[0], Integer.parseInt(proxyconfig[1])), (Credentials)new UsernamePasswordCredentials(proxyconfig[2], proxyconfig[3]));
            }
            if (credsProvider != null) {
                httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
            }
            else {
                httpclient = HttpClients.createDefault();
            }
            final HttpPatch httpPatch = new HttpPatch(uri);
            if (config != null) {
                httpPatch.setConfig(config);
            }
            final StringEntity jsonEntity = new StringEntity(body.toString());
            httpPatch.setEntity((HttpEntity)jsonEntity);
            for (int i = 0; i < headers.names().length(); ++i) {
                final String key = headers.names().getString(i);
                final String value = String.valueOf(headers.get(key));
                httpPatch.setHeader(key, value);
            }
            response2 = httpclient.execute((HttpUriRequest)httpPatch);
            jsonResponse = this.responseToJson(response2);
            final HttpEntity entity2 = response2.getEntity();
            EntityUtils.consume(entity2);
        }
        catch (final UnsupportedEncodingException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred UnsupportedEncodingException", e);
        }
        catch (final ClientProtocolException e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred ClientProtocolException", (Throwable)e2);
        }
        finally {
            response2.close();
            httpclient.close();
        }
        return jsonResponse;
    }
    
    public String getDeviceNameByOguid(final String guid) {
        String deviceProps = null;
        try {
            final SelectQuery derivedTableQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
            derivedTableQuery.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
            derivedTableQuery.setCriteria(new Criteria(Column.getColumn("DirResRel", "GUID"), (Object)guid, 0));
            final String innerTableAlias = "innerTable";
            final Table baseTable = new Table("DirObjRegStrVal");
            final DerivedTable derivedTable = new DerivedTable(innerTableAlias, (Query)derivedTableQuery);
            final SelectQuery selectQuery = SyMUtil.formSelectQuery("DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)111L, 0), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DirObjRegStrVal", "OBJ_ID"), Column.getColumn("DirObjRegStrVal", "VALUE"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join(baseTable, (Table)derivedTable, new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2))), (Criteria)null);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSet.next()) {
                deviceProps = (String)dataSet.getValue("VALUE");
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred getDeviceNameByOguid AzureWinCEA", e);
        }
        return deviceProps;
    }
    
    public String getDeviceIDByOguid(final String guid) {
        String deviceProps = null;
        try {
            final SelectQuery derivedTableQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
            derivedTableQuery.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
            derivedTableQuery.setCriteria(new Criteria(Column.getColumn("DirResRel", "GUID"), (Object)guid, 0));
            final String innerTableAlias = "innerTable";
            final Table baseTable = new Table("DirObjRegStrVal");
            final DerivedTable derivedTable = new DerivedTable(innerTableAlias, (Query)derivedTableQuery);
            final SelectQuery selectQuery = SyMUtil.formSelectQuery("DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)121L, 0), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DirObjRegStrVal", "OBJ_ID"), Column.getColumn("DirObjRegStrVal", "VALUE"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join(baseTable, (Table)derivedTable, new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2))), (Criteria)null);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSet.next()) {
                deviceProps = (String)dataSet.getValue("VALUE");
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred getDeviceNameByOguid AzureWinCEA", e);
        }
        return deviceProps;
    }
    
    public void markDeviceStatus(final Long domainId, final String deviceObjId, final Long resourceId, final boolean isManaged, final boolean isCompliant, final String userModified) {
        try {
            final Properties domainProps = DMDomainDataHandler.getInstance().getDomainById(domainId);
            final Long customerID = ((Hashtable<K, Long>)domainProps).get("CUSTOMER_ID");
            final String adDomainName = domainProps.getProperty("AD_DOMAIN_NAME");
            final JSONObject authAccessTokenDetails = AzureADAccessProvider.getInstance().getAccessTokenForDomain(adDomainName, customerID);
            final String requestUrl = "https://graph.windows.net/" + adDomainName.toLowerCase() + "/" + "devices" + "/" + deviceObjId + "?" + "api-version=1.6";
            if (authAccessTokenDetails != null && authAccessTokenDetails.has("access_token")) {
                final String accessToken = authAccessTokenDetails.getString("access_token");
                final JSONObject requestBody = new JSONObject();
                requestBody.put("isManaged", isManaged);
                requestBody.put("isCompliant", isCompliant);
                final JSONObject headers = new JSONObject();
                headers.put("Authorization", (Object)("Bearer " + accessToken));
                headers.put("Content-Type", (Object)"application/json");
                final JSONObject resp = this.execute(requestUrl, requestBody, headers);
                final int statusCode = (int)resp.get("status-code");
                if (statusCode != 201 && statusCode != 204 && statusCode != 200) {
                    throw new Exception();
                }
                final String device_name = getInstance().getDeviceNameByOguid(deviceObjId);
                final String device_ID = getInstance().getDeviceIDByOguid(deviceObjId);
                if (isCompliant && isManaged) {
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2082, resourceId, userModified, "mdm.cea.azure.device_marked_compliant", device_name + "@@@" + device_ID, customerID);
                }
                else {
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2082, resourceId, userModified, "mdm.cea.azure.device_marked_uncompliant", device_name + "@@@" + device_ID, customerID);
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred markDeviceStatus AzureWinCEA", e);
        }
    }
    
    public static void updateMangedDeviceRel(final Long dmdomainId) {
        try {
            final SelectQuery derivedTableQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegIntVal"));
            derivedTableQuery.addSelectColumn(Column.getColumn("DirObjRegIntVal", "OBJ_ID"));
            derivedTableQuery.setCriteria(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)1, 0).and(new Criteria(Column.getColumn("DirObjRegIntVal", "DIR_RESOURCE_TYPE"), (Object)201, 0))));
            final String innerTableAlias = "innerTable";
            final DerivedTable derivedTable = new DerivedTable(innerTableAlias, (Query)derivedTableQuery);
            final Table baseTable = new Table("DirResRel");
            final JSONArray dsJSArray = MDMUtil.executeSelectQuery(SyMUtil.formSelectQuery("DirResRel", new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmdomainId, 0).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)123L, 0)), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("ManagedDevice", "RESOURCE_ID"), Column.getColumn("ManagedDevice", "MANAGED_STATUS"), Column.getColumn("DirResRel", "OBJ_ID"), Column.getColumn("DirResRel", "DM_DOMAIN_ID"), Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), Column.getColumn("DirResRel", "GUID"), Column.getColumn("DirObjRegIntVal", "OBJ_ID"), Column.getColumn("DirObjRegIntVal", "ATTR_ID"), Column.getColumn("DirObjRegIntVal", "VALUE"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DirResRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1), new Join("DirResRel", "DirObjRegIntVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 1), new Join(baseTable, (Table)derivedTable, new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2))), (Criteria)null));
            final String domainUserName = DMDomainSyncDetailsDataHandler.getInstance().getSyncIntiatedByUsername(dmdomainId);
            for (int i = 0; i < dsJSArray.size(); ++i) {
                final org.json.simple.JSONObject jsObject = (org.json.simple.JSONObject)dsJSArray.get(i);
                final Long resourceID = (Long)jsObject.get((Object)"RESOURCE_ID");
                final String oGuid = (String)jsObject.get((Object)"GUID");
                final int azureStatus = (int)jsObject.get((Object)"VALUE");
                if (resourceID != null && (int)jsObject.get((Object)"MANAGED_STATUS") == 2) {
                    if (azureStatus != 3) {
                        getInstance().markDeviceStatus(dmdomainId, oGuid, resourceID, true, true, domainUserName);
                    }
                }
                else if (azureStatus != 0) {
                    getInstance().markDeviceStatus(dmdomainId, oGuid, null, false, false, domainUserName);
                }
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred updateMangedDeviceRel", ex);
        }
    }
    
    public JSONObject getCASummary(final JSONObject apirequest) {
        JSONObject result = null;
        try {
            final Long domain_id = APIUtil.getResourceID(apirequest, "director_id");
            final Properties domainPropsInDb = DMDomainDataHandler.getInstance().getDomainById(domain_id);
            final int domainClientID = ((Hashtable<K, Integer>)domainPropsInDb).get("CLIENT_ID");
            if (domainClientID != 3) {
                throw new IdpsAPIException("AD007");
            }
            final List<Integer> syncObjects = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(domain_id);
            if (syncObjects == null || !syncObjects.contains(201)) {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(domainPropsInDb, 201, Boolean.valueOf(true));
            }
            result = new JSONObject();
            SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DirResRel"));
            final Column countColumn = new Column("DirResRel", "OBJ_ID").count();
            countColumn.setColumnAlias("COUNT");
            sq.addSelectColumn(countColumn);
            final Criteria resourceType = new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)201, 0);
            final Criteria domainIdCriteria = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)domain_id, 0);
            sq.setCriteria(domainIdCriteria.and(resourceType));
            DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)sq);
            if (ds != null) {
                while (ds.next()) {
                    result.put("total_device_count", ds.getValue("COUNT"));
                }
            }
            sq = (SelectQuery)new SelectQueryImpl(new Table("DirResRel"));
            sq.addSelectColumn(countColumn);
            final Criteria resourceIdNull = new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 1);
            sq.setCriteria(domainIdCriteria.and(resourceType.and(resourceIdNull)));
            ds = DMDataSetWrapper.executeQuery((Object)sq);
            if (ds != null) {
                while (ds.next()) {
                    result.put("enrolled_device_count", ds.getValue("COUNT"));
                }
            }
            final Object timeInMs = DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(domain_id, "LAST_SUCCESSFUL_SYNC");
            boolean isPolicyApplied = false;
            if (syncObjects.contains(205)) {
                isPolicyApplied = true;
            }
            result.put("last_Successful_sync", timeInMs);
            result.put("domain_id", ((Hashtable<K, Object>)domainPropsInDb).get("DOMAIN_ID"));
            result.put("domain_name", ((Hashtable<K, Object>)domainPropsInDb).get("NAME"));
            result.put("policy_applied", isPolicyApplied);
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error APIHTTPException Occured in getCASummary", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error Exception Occurred in getCASummary", e2);
            throw new IdpsAPIException("COM0004");
        }
        return result;
    }
    
    public void configureCA(final JSONObject apirequest) throws Exception {
        final Long userID = APIUtil.getUserID(apirequest);
        final Long customer_id = APIUtil.getCustomerID(apirequest);
        if (DirectoryAPIFacade.getInstance().isUserCustomerRelevant(userID, customer_id)) {
            final SelectQuery query = DomainDataProvider.getDMManagedDomainQuery(customer_id, (String)null, (String)null, Integer.valueOf(3));
            final List<Properties> dmDomainProps = DMDomainDataHandler.getInstance().getDomains(query);
            if (dmDomainProps != null && !dmDomainProps.isEmpty()) {
                for (int i = 0; i < dmDomainProps.size(); ++i) {
                    final Properties dmDomainProp = dmDomainProps.get(i);
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(dmDomainProp, 201, Boolean.valueOf(true));
                }
            }
            return;
        }
        throw new APIHTTPException("COM0013", new Object[0]);
    }
    
    static {
        AzureWinCEA.azureWinCEA = null;
    }
}
