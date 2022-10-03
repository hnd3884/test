package com.me.emsalerts.sms.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import org.json.JSONArray;
import com.adventnet.persistence.WritableDataObject;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.Proxy;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONException;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.logging.Logger;

public class SMSUtil
{
    private static SMSUtil smsUtil;
    private static Logger logger;
    
    public static SMSUtil getInstance() {
        if (SMSUtil.smsUtil == null) {
            SMSUtil.smsUtil = new SMSUtil();
        }
        return SMSUtil.smsUtil;
    }
    
    public static HashMap checkSMSConfig() throws DataAccessException {
        DataObject smsConfigDetailsDO = null;
        final SelectQueryImpl selectQuery = new SelectQueryImpl(new Table("SMSConfig"));
        selectQuery.addSelectColumn(Column.getColumn("SMSConfig", "SMS_CONFIGTYPE"));
        selectQuery.addSelectColumn(Column.getColumn("SMSConfig", "SMS_CONFIGID"));
        smsConfigDetailsDO = DataAccess.get((SelectQuery)selectQuery);
        final HashMap smsConfiguration = new HashMap();
        if (!smsConfigDetailsDO.isEmpty()) {
            final Row row = smsConfigDetailsDO.getFirstRow("SMSConfig");
            final Long smsConfigID = (Long)row.get("SMS_CONFIGID");
            final String smsConfigType = (String)row.get("SMS_CONFIGTYPE");
            smsConfiguration.put("smsConfigID", smsConfigID);
            smsConfiguration.put("smsConfigType", smsConfigType);
        }
        return smsConfiguration;
    }
    
    public static HashMap getServiceDetails(final Long smsConfigID) throws Exception {
        final HashMap serviceDetails = new HashMap();
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(new Table("SMSServices"));
            selectQuery.addSelectColumn(Column.getColumn("SMSServices", "SERVICEID"));
            selectQuery.addSelectColumn(Column.getColumn("SMSServices", "SERVICENAME"));
            selectQuery.addSelectColumn(Column.getColumn("SMSServices", "STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("SMSServices", "SMS_CONFIGID"));
            final Criteria crit = new Criteria(Column.getColumn("SMSServices", "SMS_CONFIGID"), (Object)smsConfigID, 0);
            selectQuery.setCriteria(crit);
            final DataObject dataObject = DataAccess.get((SelectQuery)selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("SMSServices");
                serviceDetails.put("serviceID", row.get("SERVICEID"));
                serviceDetails.put("serviceName", row.get("SERVICENAME"));
            }
        }
        catch (final Exception e) {
            SMSUtil.logger.log(Level.INFO, "Exception while fetching SMS Service details for the smsConfigID  [{0}] : {1}", new Object[] { smsConfigID, e.getMessage() });
            throw e;
        }
        return serviceDetails;
    }
    
    public static Long getActionID(final Long serviceID, final String actionName) {
        Long actionID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ServiceAction"));
            selectQuery.addSelectColumn(Column.getColumn("ServiceAction", "SERVICEID"));
            selectQuery.addSelectColumn(Column.getColumn("ServiceAction", "ACTIONNAME"));
            selectQuery.addSelectColumn(Column.getColumn("ServiceAction", "SERVICE_ACTIONID"));
            final Criteria criteria = new Criteria(Column.getColumn("ServiceAction", "SERVICEID"), (Object)serviceID, 0);
            criteria.and(Column.getColumn("ServiceAction", "ACTIONNAME"), (Object)"sendsms", 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ServiceAction");
                actionID = (Long)row.get("SERVICE_ACTIONID");
            }
        }
        catch (final Exception e) {
            SMSUtil.logger.log(Level.INFO, "Exception while fetching actionID for the serviceID-[{0}] and actionName-{1} " + serviceID + actionName, e);
        }
        return actionID;
    }
    
    public static Hashtable getSMSActionConfigDetails(final Long actionID) throws Exception {
        final Hashtable outputJson = new Hashtable();
        try {
            final Criteria crit = new Criteria(Column.getColumn("ServiceActionConfig", "SERVICE_ACTIONID"), (Object)actionID, 0);
            final DataObject dObj = DataAccess.get("ServiceActionConfig", crit);
            getSMSActionParam(dObj, outputJson, "queryParams");
            getSMSActionParam(dObj, outputJson, "requestHeaders");
            getSMSActionParam(dObj, outputJson, "requestParams");
            getSMSActionParam(dObj, outputJson, "responseFormat");
            getSMSActionParam(dObj, outputJson, "requestPayload");
            getSMSActionParam(dObj, outputJson, "authDetails");
            getSMSActionParam(dObj, outputJson, "unicode");
        }
        catch (final Exception e) {
            SMSUtil.logger.log(Level.INFO, "Exception while fetching  SMS API Request configurations for action with ID [{0}] : {1}", new Object[] { actionID, e.getMessage() });
            throw e;
        }
        return outputJson;
    }
    
    private static Object getJSONForServiceActionConfig(final Iterator itr) {
        final LinkedHashMap jsonObject = new LinkedHashMap();
        while (itr.hasNext()) {
            final Row row = itr.next();
            if (!row.get("PARAM_KEY").toString().equalsIgnoreCase("password")) {
                final String paramValue = (String)row.get("PARAM_VALUE");
                final String paramKey = (String)row.get("PARAM_KEY");
                if (paramKey.equalsIgnoreCase("isConfigured")) {
                    jsonObject.put(paramKey, Boolean.valueOf(paramValue));
                }
                else {
                    jsonObject.put(paramKey, paramValue);
                }
            }
        }
        return jsonObject;
    }
    
    private static Object getJSONArrayForServiceActionConfig(final Iterator itr) {
        final ArrayList serviceActionList = new ArrayList();
        while (itr.hasNext()) {
            final LinkedHashMap serviceActionMap = new LinkedHashMap();
            final Row row = itr.next();
            serviceActionMap.put("key", row.get("PARAM_KEY"));
            serviceActionMap.put("value", row.get("PARAM_VALUE"));
            serviceActionList.add(serviceActionMap);
        }
        return serviceActionList;
    }
    
    private static void getSMSActionParam(final DataObject dObj, final Hashtable outputJson, final String paramType) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("ServiceActionConfig", "PARAM_TYPE"), (Object)paramType, 0);
        if (criteria != null) {
            final Iterator iterator = dObj.getRows("ServiceActionConfig", criteria);
            if (paramType.equalsIgnoreCase("requestHeaders") || paramType.equalsIgnoreCase("queryParams")) {
                final ArrayList arrayList = (ArrayList)getJSONArrayForServiceActionConfig(iterator);
                if (arrayList.size() != 0) {
                    outputJson.put(paramType, arrayList);
                }
            }
            else {
                final LinkedHashMap hashtable = (LinkedHashMap)getJSONForServiceActionConfig(iterator);
                if (hashtable.size() != 0) {
                    outputJson.put(paramType, hashtable);
                }
            }
        }
    }
    
    public static String replacePlaceHolders(String placeHolderString, final JSONObject placeHolderValues) {
        try {
            final Iterator itr = placeHolderValues.keys();
            while (itr.hasNext()) {
                final String key = itr.next();
                if (placeHolderString.contains("$" + key + "$")) {
                    placeHolderString = placeHolderString.replace("$" + key + "$", StringEscapeUtils.escapeJava(placeHolderValues.getString(key)));
                }
            }
        }
        catch (final JSONException e) {
            e.printStackTrace();
        }
        return placeHolderString;
    }
    
    public static HttpURLConnection getResponseAsConnection(final HashMap requestObj, final boolean isHttps) throws Exception {
        final String urlStr = requestObj.get("url");
        final String data = requestObj.get("content");
        HttpURLConnection conn = null;
        OutputStreamWriter wr = null;
        try {
            final URL url = new URL(urlStr);
            final Proxy proxy = getProxy();
            if (proxy != null) {
                conn = (HttpURLConnection)url.openConnection(proxy);
            }
            else {
                conn = (HttpURLConnection)url.openConnection();
            }
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            final String requestType = requestObj.get("requestType");
            if (requestObj != null && requestType != null && requestType.equalsIgnoreCase("POST")) {
                conn.setRequestMethod("POST");
            }
            else {
                conn.setRequestMethod("GET");
            }
            if (requestObj != null && requestObj.get("authorization") != null) {
                String authorizationData = requestObj.get("authorization");
                authorizationData = authorizationData.replace(System.getProperty("line.separator"), "");
                conn.setRequestProperty("Authorization", authorizationData);
            }
            if (requestObj != null && requestObj.get("Content-Type") != null) {
                conn.setRequestProperty("Content-Type", requestObj.get("Content-Type"));
            }
            else {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            if (requestObj != null && requestObj.get("headers") != null) {
                final JSONObject headerParameters = requestObj.get("headers");
                final Iterator<String> headerParametersItr = headerParameters.keys();
                while (headerParametersItr.hasNext()) {
                    final String key = headerParametersItr.next();
                    final String value = headerParameters.getString(key);
                    conn.setRequestProperty(key, value);
                }
            }
            if (data != null) {
                final int contentLength = data.getBytes().length;
                conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
                try {
                    wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
                }
                catch (final IOException ioe) {
                    SMSUtil.logger.log(Level.WARNING, "I/O Error while connecting to SMS Provider server.");
                    throw ioe;
                }
                finally {
                    if (wr != null) {
                        wr.close();
                    }
                }
            }
        }
        catch (final Exception e) {
            SMSUtil.logger.log(Level.INFO, "Exception while initiating Response >> ", e);
            throw e;
        }
        return conn;
    }
    
    private static Proxy getProxy() throws Exception {
        Proxy proxy = null;
        String proxyHost = null;
        Integer proxyPort = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (!proxyDetails.isEmpty()) {
            proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
            proxyPort = Integer.valueOf(((Hashtable<K, String>)proxyDetails).get("proxyPort"));
            final String proxyUsername = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
            final String proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
            if (proxyHost != null) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                if (proxyUsername != null) {
                    final Authenticator authenticator = new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            SMSUtil.logger.log(Level.INFO, "SMSImpl: Proxy authentication completed successfully");
                            return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                        }
                    };
                    Authenticator.setDefault(authenticator);
                }
            }
            SMSUtil.logger.log(Level.INFO, "SMSImpl: Proxy set successfully");
        }
        return proxy;
    }
    
    public static String getResponseFromConnection(final HttpURLConnection httpUrlConnection) throws IOException {
        String result = "";
        InputStream responseInputStream = null;
        int responseCode = -1;
        try {
            responseCode = httpUrlConnection.getResponseCode();
            if (responseCode < 400) {
                responseInputStream = httpUrlConnection.getInputStream();
            }
            else if (responseCode >= 400) {
                responseInputStream = httpUrlConnection.getErrorStream();
            }
            final BufferedReader in = new BufferedReader(new InputStreamReader(responseInputStream));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
        }
        catch (final IOException e) {
            SMSUtil.logger.log(Level.INFO, "Exception while getting Response from Connection >> " + e);
            if (responseInputStream != null) {
                try {
                    responseInputStream.close();
                }
                catch (final IOException e) {
                    throw e;
                }
            }
        }
        finally {
            if (responseInputStream != null) {
                try {
                    responseInputStream.close();
                }
                catch (final IOException e2) {
                    throw e2;
                }
            }
        }
        return result;
    }
    
    public static void saveServiceActionReqConfigDetails(final Long actionID, final JSONObject requestConfigs) throws Exception {
        final JSONArray configArray = getRequestConfigArray(requestConfigs);
        final DataObject dObj = (DataObject)new WritableDataObject();
        try {
            SMSUtil.logger.log(Level.INFO, "Going to add SMS Action configurations in DB.......");
            final int len = configArray.length();
            DataAccess.delete("ServiceActionConfig", new Criteria(Column.getColumn("ServiceActionConfig", "SERVICE_ACTIONID"), (Object)actionID, 0));
            for (int i = 0; i < len; ++i) {
                final JSONObject configObject = configArray.getJSONObject(i);
                Criteria crit = new Criteria(Column.getColumn("ServiceActionConfig", "SERVICE_ACTIONID"), (Object)actionID, 0);
                crit = crit.and(new Criteria(Column.getColumn("ServiceActionConfig", "PARAM_TYPE"), (Object)configObject.getString("param_type"), 0));
                crit = crit.and(new Criteria(Column.getColumn("ServiceActionConfig", "PARAM_KEY"), (Object)configObject.getString("param_key"), 0));
                DataAccess.delete(crit);
                final Row row = new Row("ServiceActionConfig");
                row.set("SERVICE_ACTIONID", (Object)actionID);
                row.set("PARAM_KEY", (Object)configObject.getString("param_key"));
                row.set("PARAM_TYPE", (Object)configObject.getString("param_type"));
                row.set("PARAM_VALUE", (Object)configObject.getString("param_value"));
                dObj.addRow(row);
            }
            DataAccess.add(dObj);
            SMSUtil.logger.log(Level.INFO, "SMS action configurations are added in DB successfully...");
        }
        catch (final Exception e) {
            SMSUtil.logger.log(Level.INFO, "Exception at while saving request config details for action with ID {0}." + new Object[] { actionID }, e);
            throw e;
        }
    }
    
    public static JSONArray getRequestConfigArray(final JSONObject requestConfigs) throws Exception {
        final JSONArray configArray = new JSONArray();
        if (requestConfigs.has("requestParams")) {
            final JSONObject request_params = requestConfigs.getJSONObject("requestParams");
            jsonObjectExpander(configArray, request_params, "requestParams");
        }
        if (requestConfigs.has("queryParams")) {
            final JSONArray queryParams = requestConfigs.getJSONArray("queryParams");
            jsonArrayExpander(configArray, queryParams, "queryParams");
        }
        if (requestConfigs.has("requestHeaders")) {
            final JSONArray requestHeaders = requestConfigs.getJSONArray("requestHeaders");
            jsonArrayExpander(configArray, requestHeaders, "requestHeaders");
        }
        if (requestConfigs.has("authDetails")) {
            final JSONObject auth_details = requestConfigs.getJSONObject("authDetails");
            jsonObjectExpander(configArray, auth_details, "authDetails");
        }
        if (requestConfigs.has("responseFormat")) {
            final JSONObject error_details = requestConfigs.getJSONObject("responseFormat");
            jsonObjectExpander(configArray, error_details, "responseFormat");
        }
        if (requestConfigs.has("requestPayload")) {
            final JSONObject error_details = requestConfigs.getJSONObject("requestPayload");
            jsonObjectExpander(configArray, error_details, "requestPayload");
        }
        if (requestConfigs.has("unicode")) {
            final JSONObject unicode = requestConfigs.getJSONObject("unicode");
            jsonObjectExpander(configArray, unicode, "unicode");
        }
        return configArray;
    }
    
    public static void jsonObjectExpander(final JSONArray configArray, final JSONObject jsonObject, final String paramType) throws Exception {
        final Iterator itr = jsonObject.keys();
        String paramValue = null;
        while (itr.hasNext()) {
            final String key = itr.next();
            if (jsonObject.get(key) instanceof String) {
                final String value = paramValue = jsonObject.getString(key);
            }
            else if (jsonObject.get(key) instanceof JSONObject) {
                final JSONObject value2 = jsonObject.getJSONObject(key);
                paramValue = value2.toString();
            }
            else if (jsonObject.get(key) instanceof Boolean) {
                final boolean value3 = jsonObject.getBoolean(key);
                paramValue = String.valueOf(value3);
            }
            final JSONObject template = new JSONObject();
            template.put("param_key", (Object)key);
            template.put("param_value", (Object)paramValue);
            template.put("param_type", (Object)paramType);
            configArray.put((Object)template);
        }
    }
    
    public static void jsonArrayExpander(final JSONArray configArray, final JSONArray inputArray, final String paramType) throws Exception {
        for (int itrValue = 0; itrValue < inputArray.length(); ++itrValue) {
            final JSONObject inputObj = inputArray.getJSONObject(itrValue);
            final JSONObject formattedObj = new JSONObject();
            formattedObj.put("param_key", inputObj.get("key"));
            formattedObj.put("param_value", inputObj.get("value"));
            formattedObj.put("param_type", (Object)paramType);
            configArray.put((Object)formattedObj);
        }
    }
    
    public static void saveServiceActionDetails(final String actionName, final Long serviceID) throws Exception {
        final DataObject dObj = (DataObject)new WritableDataObject();
        final Row row = new Row("ServiceAction");
        row.set("SERVICEID", (Object)serviceID);
        row.set("ACTIONNAME", (Object)actionName);
        dObj.addRow(row);
        DataAccess.add(dObj);
    }
    
    public static void saveServiceDetails(final Long smsConfigID, final String serviceName) throws Exception {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Row row = new Row("SMSServices");
        row.set("SMS_CONFIGID", (Object)smsConfigID);
        row.set("SERVICENAME", (Object)serviceName);
        row.set("STATUS", (Object)1);
        dataObject.addRow(row);
        DataAccess.add(dataObject);
    }
    
    public static void saveSmsConfigDetails(final String smsConfigType) throws Exception {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Row row = new Row("SMSConfig");
        row.set("SMS_CONFIGTYPE", (Object)smsConfigType);
        dataObject.addRow(row);
        DataAccess.add(dataObject);
    }
    
    public static String convertToUnicode(final String message) throws Exception {
        final byte[] bytesData = message.getBytes("UTF-16BE");
        final StringBuffer uniString = new StringBuffer(bytesData.length * 2);
        for (int i = 0; i < bytesData.length; ++i) {
            final String hexByteStr = Integer.toHexString(bytesData[i] & 0xFF).toUpperCase();
            if (hexByteStr.length() == 1) {
                uniString.append("0");
            }
            uniString.append(hexByteStr);
        }
        return uniString.toString();
    }
    
    public Boolean isSMSSettingsEnabled(final String serviceName) throws Exception {
        try {
            final Criteria serviceNameCrit = new Criteria(Column.getColumn("SMSServices", "SERVICENAME"), (Object)serviceName, 0);
            final DataObject smsSeviceDO = DataAccess.get("SMSServices", serviceNameCrit);
            if (!smsSeviceDO.isEmpty()) {
                final Row serviceRow = smsSeviceDO.getFirstRow("SMSServices");
                final int serviceStatus = (int)serviceRow.get("STATUS");
                if (serviceStatus == 1) {
                    return Boolean.TRUE;
                }
            }
        }
        catch (final DataAccessException e) {
            SMSUtil.logger.log(Level.WARNING, "Exception while checking SMS settings is enabled or not ", (Throwable)e);
            throw e;
        }
        return Boolean.FALSE;
    }
    
    public void updateSMSStatus(final String serviceName, final int status) throws DataAccessException {
        final UpdateQuery serviceUpdateQuery = (UpdateQuery)new UpdateQueryImpl("SMSServices");
        serviceUpdateQuery.setCriteria(new Criteria(new Column("SMSServices", "SERVICENAME"), (Object)serviceName, 0));
        serviceUpdateQuery.setUpdateColumn("STATUS", (Object)status);
        DataAccess.update(serviceUpdateQuery);
    }
    
    public String setQueryParamsInConnection(final List queryParamsList, final JSONObject inputProperties, String url) throws UnsupportedEncodingException {
        int firstIndex = 0;
        for (final Object queryParamObj : queryParamsList) {
            final LinkedHashMap queryParamMap = (LinkedHashMap)queryParamObj;
            final String key = replacePlaceHolders(queryParamMap.get("key"), inputProperties);
            final String value = replacePlaceHolders(queryParamMap.get("value"), inputProperties);
            final String param = key + "=" + URLEncoder.encode(value, "UTF-8");
            if (firstIndex == 0) {
                url = url + "?" + param;
                ++firstIndex;
            }
            else {
                url = url + "&" + param;
            }
        }
        return url;
    }
    
    public JSONObject constructHeaderObjFromList(final List requestHeadersList, final JSONObject inputProperties) throws JSONException {
        final JSONObject requestHeaderJSON = new JSONObject();
        for (final Object requestHeaderObj : requestHeadersList) {
            final LinkedHashMap requestHeaderMap = (LinkedHashMap)requestHeaderObj;
            final String key = replacePlaceHolders(requestHeaderMap.get("key"), inputProperties);
            final String value = replacePlaceHolders(requestHeaderMap.get("value"), inputProperties);
            requestHeaderJSON.put(key, (Object)value);
        }
        return requestHeaderJSON;
    }
    
    static {
        SMSUtil.smsUtil = null;
        SMSUtil.logger = Logger.getLogger("EMSAlertsLogger");
    }
}
