package com.me.mdm.onpremise.server.integration.sdp;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.consents.ConsentStatusUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.i18n.I18N;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.Utils;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.w3c.dom.Attr;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.File;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.sym.server.mdm.api.MdmInvDataProcessor;
import javax.xml.parsers.DocumentBuilderFactory;
import com.adventnet.sym.server.mdm.DeviceDetails;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;
import java.io.FileNotFoundException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import javax.net.ssl.HttpsURLConnection;
import com.me.mdm.onpremise.server.integration.MDMSSLHandler;
import java.net.URL;
import java.io.IOException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import com.adventnet.iam.security.SecurityUtil;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONObject;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class MDMSDPIntegrationUtil
{
    private static final Logger SDPACCESSLOGGER;
    private static final Logger SDPINTEGLOGGER;
    public static final String SDP_SERVER_STATUS_SERVLET = "/servlets/SDServerStatusServlet";
    private static MDMSDPIntegrationUtil integUtil;
    
    public static MDMSDPIntegrationUtil getInstance() {
        if (MDMSDPIntegrationUtil.integUtil == null) {
            MDMSDPIntegrationUtil.integUtil = new MDMSDPIntegrationUtil();
        }
        return MDMSDPIntegrationUtil.integUtil;
    }
    
    public String getSDPBuildNumber() throws Exception {
        final String sdpBuildNumberCache = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("SDP_BUILD_NUMBER");
        MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "SDP Build No in Cache : {0} ", new Object[] { sdpBuildNumberCache });
        String sdpBuildNumberDB = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_BUILD_NUMBER");
        if (sdpBuildNumberCache == null) {
            sdpBuildNumberDB = this.getBuildNumberFromSDP();
        }
        return sdpBuildNumberDB;
    }
    
    public String getBuildNumberFromSDP() throws Exception {
        String sdpBuildNumber = null;
        final String baseURL = this.getServiceDeskBaseURL();
        final String newURL = baseURL + "/api/v3/app_resources/build_info";
        final String responseFromSDP = getInstance().requestSDP(newURL, "GET");
        try {
            if (responseFromSDP != null && responseFromSDP.contains("response_status")) {
                final JSONObject responseJSON = new JSONObject(responseFromSDP);
                final JSONObject statusJSON = responseJSON.getJSONObject("response_status");
                final Long sdpStatus = statusJSON.getLong("status_code");
                if (sdpStatus != null && sdpStatus == 2000L) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = responseJSON.getJSONObject("result");
                    sdpBuildNumber = String.valueOf(jsonObject.getLong("build_number"));
                    MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "getBuildNumberFromSDP() {0} sdpBuildNumber : {1} ", sdpBuildNumber);
                }
                MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, " Response status from sdp : {0}", sdpStatus);
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "getBuildNumberFromSDP Exception e : ", e);
        }
        if (sdpBuildNumber == null) {
            final String oldURL = baseURL + "/servlets/DCPluginServlet?action=getVersionDetails";
            final Map resultMap = this.getValidationResultMap("HelpDesk", oldURL);
            sdpBuildNumber = resultMap.get("buildnumber");
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "getBuildNumberFromSDP() {0} sdpBuildNumber : {1} ", new Object[] { resultMap, sdpBuildNumber });
        }
        final String sdpBuildNumberDB = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_BUILD_NUMBER");
        if (sdpBuildNumberDB == null || (sdpBuildNumber != null && !sdpBuildNumberDB.equalsIgnoreCase(sdpBuildNumber))) {
            this.updateIntegrationParameter("SDP_BUILD_NUMBER", sdpBuildNumber);
        }
        return sdpBuildNumber;
    }
    
    public boolean isSDPIntegrationEnabled() {
        boolean isSDPEnabled = false;
        final String isSDPEnabledStr = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("SDP_INTEGRATION_ENABLED");
        try {
            if (isSDPEnabledStr == null) {
                final Properties props = this.getServerSettings("HelpDesk");
                if (props != null && props.get("IS_ENABLED") != null) {
                    isSDPEnabled = true;
                }
                final ArrayList<String> tableNames = new ArrayList<String>();
                tableNames.add("ApplnServerSettings");
                ApiFactoryProvider.getCacheAccessAPI().putCache("SDP_INTEGRATION_ENABLED", (Object)String.valueOf(isSDPEnabled), (List)tableNames);
            }
            else {
                isSDPEnabled = Boolean.valueOf(isSDPEnabledStr);
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in getSDPIntegrationMode", ex);
        }
        return isSDPEnabled;
    }
    
    public boolean isAEIntegrationEnabled() {
        boolean isSDPEnabled = false;
        final String isSDPEnabledStr = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("AE_INTEGRATION_ENABLED");
        try {
            if (isSDPEnabledStr == null) {
                final Properties props = this.getServerSettings("AssetExplorer");
                if (props != null && props.get("IS_ENABLED") != null) {
                    isSDPEnabled = true;
                }
                final ArrayList<String> tableNames = new ArrayList<String>();
                tableNames.add("ApplnServerSettings");
                ApiFactoryProvider.getCacheAccessAPI().putCache("AE_INTEGRATION_ENABLED", (Object)String.valueOf(isSDPEnabled), (List)tableNames);
            }
            else {
                isSDPEnabled = Boolean.valueOf(isSDPEnabledStr);
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in getAEIntegrationMode", ex);
        }
        return isSDPEnabled;
    }
    
    public boolean isMDMAssetIntegrationEnabled() {
        final String isMDMInvIntegEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_INV_INTEGRATION");
        return Boolean.valueOf(isMDMInvIntegEnabledStr);
    }
    
    public boolean isMDMAEAssetIntegrationEnabled() {
        final String isMDMInvIntegEnabledStr = MDMIntegrationUtil.getInstance().getIntegrationParamValue("AE_MDM_INV_INTEGRATION");
        return Boolean.valueOf(isMDMInvIntegEnabledStr);
    }
    
    public boolean isMDMPostOwner() {
        boolean isMDMSDPPostOwner = false;
        final String isMDMPostUser = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_POST_OWNER");
        if (isMDMPostUser != null && isMDMPostUser.equalsIgnoreCase("post")) {
            isMDMSDPPostOwner = true;
        }
        return isMDMSDPPostOwner;
    }
    
    public String getMDMAssetDelValue() {
        return MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_ASSET_DEL_VALUE");
    }
    
    public String getMDMAssetPostPath(final Long resourceID, final String deviceName, final HashMap hsContentForPost) {
        String postPath = null;
        try {
            String strUDID = hsContentForPost.get("UDID");
            String sAccountName = hsContentForPost.get("customername");
            if (strUDID != null) {
                strUDID = strUDID.trim();
                strUDID = URLEncoder.encode(strUDID);
            }
            if (sAccountName != null) {
                sAccountName = sAccountName.trim();
                sAccountName = URLEncoder.encode(sAccountName);
            }
            final String sdpBuildNumber = getInstance().getSDPBuildNumber();
            final int sdpBN = Integer.parseInt(sdpBuildNumber);
            if (sdpBN > 13001) {
                postPath = "/api/v3/ScanDataServlet?action=postSuccessData&product_name=MDM&machineId=" + resourceID;
            }
            else {
                postPath = "/discoveryServlet/WsDiscoveryServlet?UDID=" + strUDID + "&accountName=" + sAccountName;
            }
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, " MDM Asset postPath  : {0} ", postPath);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in getting MDM Asset Post Path", ex);
        }
        return postPath;
    }
    
    public Map getValidationResultMap(final String appName, final String urlStr) {
        final BufferedInputStream is = null;
        final ByteArrayOutputStream bos = null;
        final Map resultMap = new HashMap();
        try {
            final URLConnection uc = this.createURLConnection(appName, urlStr, "text/xml; charset=\"UTF-8\"", true, true, false);
            final InputStream ic = uc.getInputStream();
            final DocumentBuilder docBuilder = SecurityUtil.getDocumentBuilder();
            final Document doc = docBuilder.parse(ic);
            final Element element = doc.getDocumentElement();
            final NodeList nodelist = element.getChildNodes();
            for (int len = nodelist.getLength(), i = 0; i < len; ++i) {
                final Node node = nodelist.item(i);
                final String nodeName = node.getNodeName();
                final String nodeValue = this.getNodeValue(node);
                resultMap.put(nodeName, nodeValue);
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in getValidationResultMap:", ex);
            try {
                if (is != null) {
                    is.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
            catch (final Exception ex) {
                MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while closing connection in getValidationResultMap", ex);
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
            catch (final Exception ex2) {
                MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while closing connection in getValidationResultMap", ex2);
            }
        }
        return resultMap;
    }
    
    public String getNodeValue(final Node textNode) {
        String nodeValue = null;
        final NodeList subList = textNode.getChildNodes();
        if (subList.getLength() == 0) {
            nodeValue = textNode.getNodeValue();
        }
        else {
            for (int k = 0; k < subList.getLength(); ++k) {
                final Node subNode = subList.item(k);
                if (subNode.getNodeType() == 3) {
                    nodeValue = subNode.getNodeValue();
                }
            }
        }
        return nodeValue;
    }
    
    public void updateIntegrationParameter(final String paramName, final String paramValue) {
        try {
            final Column col = new Column("IntegrationParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, (boolean)Boolean.FALSE);
            final DataObject paramDO = MDMUtil.getPersistence().get("IntegrationParams", criteria);
            if (paramDO.isEmpty()) {
                final Row paramRow = new Row("IntegrationParams");
                paramRow.set("PARAM_NAME", (Object)paramName);
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                paramDO.addRow(paramRow);
                DataAccess.add(paramDO);
                MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Integration Parameter has been added - Param Name: {0}  Param Value: {1}", new Object[] { paramName, paramValue });
            }
            else {
                final Row paramRow = paramDO.getFirstRow("IntegrationParams");
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                paramDO.updateRow(paramRow);
                DataAccess.update(paramDO);
                MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Integration Parameter has been updated - Param Name: {0}  Param Value: {1}", new Object[] { paramName, paramValue });
            }
            final ArrayList tableNames = new ArrayList();
            tableNames.add("IntegrationParams");
            ApiFactoryProvider.getCacheAccessAPI().putCache(paramName, (Object)paramValue, (List)tableNames);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while updating Integration Parameter:{0} {1}", new Object[] { paramName, ex.getMessage() });
        }
    }
    
    public URLConnection createURLConnection(final String appName, final String baseURL, final boolean isDataPost) throws IOException {
        Properties serverProp = this.getServerSettings(appName);
        if (serverProp == null) {
            serverProp = new Properties();
        }
        serverProp.setProperty("BASE_URL", baseURL);
        ((Hashtable<String, Boolean>)serverProp).put("IS_FOR_DATA_POST", isDataPost);
        this.setServerProps(appName, serverProp);
        return this.createURLConnection(serverProp);
    }
    
    public URLConnection createURLConnection(final String appName, final String baseURL, final String contentType, final boolean doInput, final boolean doOutput, final boolean isItForDataPost) throws IOException {
        Properties serverProp = this.getServerSettings(appName);
        if (serverProp == null) {
            serverProp = new Properties();
        }
        serverProp.setProperty("BASE_URL", baseURL);
        serverProp.setProperty("CONTENT_TYPE", contentType);
        ((Hashtable<String, Boolean>)serverProp).put("IS_DO_INPUT", doInput);
        ((Hashtable<String, Boolean>)serverProp).put("IS_DO_OUTPUT", doOutput);
        ((Hashtable<String, Boolean>)serverProp).put("IS_FOR_DATA_POST", isItForDataPost);
        this.setServerProps(appName, serverProp);
        return this.createURLConnection(serverProp);
    }
    
    public URLConnection createSDPURLConnection(final String baseURL, final String contentType, final boolean doInput, final boolean doOutput, final boolean isItForDataPost) throws IOException {
        return this.createURLConnection("HelpDesk", baseURL, contentType, doInput, doOutput, isItForDataPost);
    }
    
    public URLConnection createURLConnection(final Properties serverProp) throws IOException {
        final URL url = ((Hashtable<K, URL>)serverProp).get("URL");
        if (url == null) {
            return null;
        }
        String protocol = "";
        final String contentType = serverProp.getProperty("CONTENT_TYPE");
        final boolean doInput = ((Hashtable<K, Boolean>)serverProp).get("IS_DO_INPUT");
        final boolean doOutput = ((Hashtable<K, Boolean>)serverProp).get("IS_DO_OUTPUT");
        final boolean isItForDataPost = ((Hashtable<K, Boolean>)serverProp).get("IS_FOR_DATA_POST");
        protocol = url.getProtocol();
        if (protocol.equalsIgnoreCase("https")) {
            HttpsURLConnection uc = null;
            final String keystorePath = serverProp.getProperty("KEYSTORE_PATH");
            final char[] keystorePassphrase = ((Hashtable<K, char[]>)serverProp).get("KEYSTORE_PASSPHRASE");
            final SSLSocketFactory sdpSSLSocketFactory = MDMSSLHandler.getInstance().getSSLSocketFactory(keystorePath, keystorePassphrase);
            uc = (HttpsURLConnection)url.openConnection(Proxy.NO_PROXY);
            if (sdpSSLSocketFactory != null) {
                uc.setSSLSocketFactory(sdpSSLSocketFactory);
            }
            uc.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            });
            if (contentType != null && !contentType.equalsIgnoreCase("NO_CONTENT_TYPE")) {
                uc.setRequestProperty("Content-Type", contentType);
            }
            uc.setDoOutput(doOutput);
            uc.setDoInput(doInput);
            uc.addRequestProperty("User-Agent", "Mobile Device Manager Plus");
            if (!isItForDataPost && uc.getResponseCode() == 404) {
                throw new FileNotFoundException();
            }
            return uc;
        }
        else {
            final HttpURLConnection uc2 = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
            if (contentType != null && !contentType.equalsIgnoreCase("NO_CONTENT_TYPE")) {
                uc2.setRequestProperty("Content-Type", contentType);
            }
            uc2.setDoOutput(doOutput);
            uc2.setDoInput(doInput);
            uc2.addRequestProperty("User-Agent", "Mobile Device Manager Plus");
            if (!isItForDataPost && uc2.getResponseCode() == 404) {
                throw new FileNotFoundException();
            }
            return uc2;
        }
    }
    
    public boolean checkAEServerCert(final Properties serverProp) {
        final String serverHost = serverProp.getProperty("SERVER");
        final int serverPort = Integer.valueOf(serverProp.getProperty("PORT"));
        try {
            MDMSSLHandler.getInstance().handleKeystoreFile(serverHost, serverPort, MDMSDPIntegrationConstants.AE_KEYSTORE_PATH, MDMSDPIntegrationConstants.AE_KEYSTORE_PASSPHRASE, "aeCertificate");
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, " Exception while handling AE SSL {0}", ex);
        }
        return true;
    }
    
    public boolean checkSDPServerCert(final Properties serverProp) {
        final String serverHost = serverProp.getProperty("SERVER");
        final int serverPort = Integer.valueOf(serverProp.getProperty("PORT"));
        try {
            MDMSSLHandler.getInstance().handleKeystoreFile(serverHost, serverPort, MDMSDPIntegrationConstants.SDP_KEYSTORE_PATH, MDMSDPIntegrationConstants.SDP_KEYSTORE_PASSPHRASE, "sdpCertificate");
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, " Exception while handling SDP SSL:{0}", ex);
        }
        return true;
    }
    
    public boolean checkSDPConfigServerStatus(final Properties serverProp) {
        final String baseURL = this.getServiceDeskBaseURL(serverProp) + "/servlets/SDServerStatusServlet";
        final String protocol = serverProp.getProperty("PROTOCOL");
        if ("HTTPS".equalsIgnoreCase(protocol)) {
            this.checkSDPServerCert(serverProp);
        }
        serverProp.setProperty("BASE_URL", baseURL);
        this.setServerProps("HelpDesk", serverProp);
        return this.checkServerStatus(serverProp);
    }
    
    public boolean checkSDPServerStatus() {
        final Properties serverProp = this.getServerSettings("HelpDesk");
        final String protocol = serverProp.getProperty("PROTOCOL");
        if ("HTTPS".equalsIgnoreCase(protocol)) {
            this.checkSDPServerCert(serverProp);
        }
        final String baseURL = this.getServiceDeskBaseURL(serverProp) + "/servlets/SDServerStatusServlet";
        serverProp.setProperty("BASE_URL", baseURL);
        this.setServerProps("HelpDesk", serverProp);
        return this.checkServerStatus(serverProp);
    }
    
    public boolean checkAEConfigServerStatus(final Properties aeServerProp) {
        final String baseURL = this.getServiceDeskBaseURL(aeServerProp) + "/servlets/SDServerStatusServlet";
        final String protocol = aeServerProp.getProperty("PROTOCOL");
        if ("HTTPS".equalsIgnoreCase(protocol)) {
            this.checkAEServerCert(aeServerProp);
        }
        aeServerProp.setProperty("BASE_URL", baseURL);
        this.setServerProps("AssetExplorer", aeServerProp);
        return this.checkServerStatus(aeServerProp);
    }
    
    public boolean checkAEServerStatus() {
        final Properties aeServerProp = this.getServerSettings("AssetExplorer");
        final String protocol = aeServerProp.getProperty("PROTOCOL");
        if ("HTTPS".equalsIgnoreCase(protocol)) {
            this.checkAEServerCert(aeServerProp);
        }
        final String baseURL = this.getServiceDeskBaseURL(aeServerProp) + "/servlets/SDServerStatusServlet";
        aeServerProp.setProperty("BASE_URL", baseURL);
        this.setServerProps("AssetExplorer", aeServerProp);
        return this.checkServerStatus(aeServerProp);
    }
    
    public void setServerProps(final String appName, final Properties serverProp) {
        final String baseUrl = serverProp.getProperty("BASE_URL");
        try {
            final URL url = new URL(baseUrl);
            ((Hashtable<String, URL>)serverProp).put("URL", url);
            if (!serverProp.containsKey("CONTENT_TYPE")) {
                serverProp.setProperty("CONTENT_TYPE", "text/xml; charset=\"UTF-8\"");
            }
            if (!serverProp.containsKey("IS_DO_INPUT")) {
                ((Hashtable<String, Boolean>)serverProp).put("IS_DO_INPUT", true);
            }
            if (!serverProp.containsKey("IS_DO_OUTPUT")) {
                ((Hashtable<String, Boolean>)serverProp).put("IS_DO_OUTPUT", true);
            }
            if (!serverProp.containsKey("IS_FOR_DATA_POST")) {
                ((Hashtable<String, Boolean>)serverProp).put("IS_FOR_DATA_POST", true);
            }
            if (appName.equalsIgnoreCase("HelpDesk")) {
                serverProp.setProperty("KEYSTORE_PATH", MDMSDPIntegrationConstants.SDP_KEYSTORE_PATH);
                ((Hashtable<String, char[]>)serverProp).put("KEYSTORE_PASSPHRASE", MDMSDPIntegrationConstants.SDP_KEYSTORE_PASSPHRASE);
            }
            else if (appName.equalsIgnoreCase("AssetExplorer")) {
                if (CustomerInfoUtil.isDC()) {
                    serverProp.setProperty("KEYSTORE_PATH", MDMSDPIntegrationConstants.SDP_KEYSTORE_PATH);
                    ((Hashtable<String, char[]>)serverProp).put("KEYSTORE_PASSPHRASE", MDMSDPIntegrationConstants.SDP_KEYSTORE_PASSPHRASE);
                }
                else {
                    serverProp.setProperty("KEYSTORE_PATH", MDMSDPIntegrationConstants.AE_KEYSTORE_PATH);
                    ((Hashtable<String, char[]>)serverProp).put("KEYSTORE_PASSPHRASE", MDMSDPIntegrationConstants.AE_KEYSTORE_PASSPHRASE);
                }
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception occurred in setServerProps()", ex);
        }
    }
    
    public boolean checkServerStatus(final Properties serverProp) {
        BufferedInputStream is = null;
        ByteArrayOutputStream bos = null;
        URLConnection uc = null;
        boolean serverStatus = false;
        try {
            uc = this.createURLConnection(serverProp);
            uc.connect();
            serverStatus = true;
            final InputStream ic = uc.getInputStream();
            is = new BufferedInputStream(ic);
            bos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];
            int length = -1;
            if (is.available() > 0) {
                while ((length = is.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, length);
                }
                final byte[] result = bos.toByteArray();
                if (result.length > 0) {
                    serverStatus = true;
                }
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, " Exception while creating URL connection to SDP:", ex);
            serverStatus = false;
            try {
                if (is != null) {
                    is.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
            catch (final Exception exp) {
                exp.printStackTrace();
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
            catch (final Exception exp2) {
                exp2.printStackTrace();
            }
        }
        return serverStatus;
    }
    
    public boolean checkSDPServerKeyStatus(final Properties serverProp) {
        final String baseURL = this.getServiceDeskBaseURL(serverProp);
        final String authKey = serverProp.getProperty("AUTHENTICATION_KEY");
        return this.checkServerKeyStatus(baseURL, authKey);
    }
    
    public boolean checkSDPServerKeyStatus(final String appName, final String authKey) {
        final Properties serverProp = this.getServerSettings(appName);
        final String baseURL = this.getServiceDeskBaseURL(serverProp);
        return this.checkServerKeyStatus(baseURL, authKey);
    }
    
    public boolean checkServerKeyStatus(final String baseURL, final String authKey) {
        boolean isKeyValid = false;
        final String newBaseURL = baseURL + "/api/v3/app_resources/build_info";
        try {
            final Properties sdpSettingsProps = new Properties();
            sdpSettingsProps.setProperty("AUTHENTICATION_KEY", authKey);
            getInstance().addOrUpdateServerSettings("HelpDesk", sdpSettingsProps);
            final String responseFromSDP = getInstance().requestSDP(newBaseURL, "GET");
            if (responseFromSDP != null && responseFromSDP.contains("response_status")) {
                final JSONObject responseJSON = new JSONObject(responseFromSDP);
                final JSONObject statusJSON = responseJSON.getJSONObject("response_status");
                final Long sdpStatus = statusJSON.getLong("status_code");
                if (sdpStatus != null && sdpStatus == 2000L) {
                    isKeyValid = true;
                }
                else if (sdpStatus != null && sdpStatus == 4000L) {
                    final JSONArray responseArray = statusJSON.getJSONArray("messages");
                    final JSONObject statuscode = responseArray.getJSONObject(0);
                    final int sPropertyValue = statuscode.getInt("status_code");
                    if (sPropertyValue == 401) {
                        isKeyValid = false;
                    }
                }
                return isKeyValid;
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "getLicenseDetailsfromSDP Exception e : ", e);
        }
        if (!isKeyValid) {
            final String oldBaseURL = baseURL + "/DcServlet?operation=validateKey&KEY=" + authKey;
            final Map resultMap = this.getValidationResultMap("HelpDesk", oldBaseURL);
            isKeyValid = this.isValid(resultMap);
        }
        return isKeyValid;
    }
    
    public boolean isValid(final Map resultMap) {
        final String result = resultMap.get("Result");
        return result != null && result.equalsIgnoreCase("Success");
    }
    
    public HashMap handleMDMAssetContentforSDPXML(final Long resourceID, final HashMap hsContentForPost) {
        HashMap deviceContent = null;
        final DeviceDetails deviceDetails = new DeviceDetails((long)resourceID);
        final int modelType = deviceDetails.modelType;
        if (modelType == 3 || modelType == 4) {
            deviceContent = this.addMDMAssetComputerContentforSDPXML(resourceID, hsContentForPost);
        }
        else {
            deviceContent = this.addMDMAssetContentforSDPXML(resourceID, hsContentForPost);
        }
        return deviceContent;
    }
    
    public HashMap addMDMAssetContentforSDPXML(final Long resourceID, final HashMap hsContentForPost) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();
            final String strUDID = hsContentForPost.get("UDID");
            final HashMap deviceHash = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
            final int platform = deviceHash.get("PLATFORM_TYPE");
            final int agentType = deviceHash.get("AGENT_TYPE");
            final Element rootEle = dom.createElement("MobileDevice");
            dom.appendChild(rootEle);
            switch (platform) {
                case 1: {
                    dom = this.createMobilePlatformElement(resourceID, strUDID, dom, rootEle, "iOS");
                    break;
                }
                case 2: {
                    dom = this.createMobilePlatformElement(resourceID, strUDID, dom, rootEle, "android");
                    break;
                }
                case 3: {
                    dom = this.createMobilePlatformElement(resourceID, strUDID, dom, rootEle, "windows");
                    break;
                }
                case 4: {
                    dom = this.createMobilePlatformElement(resourceID, strUDID, dom, rootEle, "windows");
                    break;
                }
            }
            dom = this.createDeviceInfoElement(resourceID, dom, rootEle);
            dom = this.createRestrictionsElement(resourceID, dom, rootEle, platform, agentType);
            dom = this.createSecurityInfoElement(resourceID, dom, rootEle);
            dom = this.createAccountInfoElement(dom, rootEle, hsContentForPost);
            final Element installedApp = this.createElement(dom, "InstalledApplicationList", rootEle);
            final List appID = MdmInvDataProcessor.getInstance().getAppIDFromResourceID((long)resourceID);
            for (int i = 0; i < appID.size(); ++i) {
                final long applicationID = appID.get(i);
                dom = this.createInstalledAppElement(applicationID, dom, installedApp);
            }
            final Element certList = this.createElement(dom, "CertificateList", rootEle);
            final List certID = MdmInvDataProcessor.getInstance().getCertificateIDFromResourceID((long)resourceID);
            for (int j = 0; j < certID.size(); ++j) {
                final long certificateID = certID.get(j);
                dom = this.createCertificateElement(certificateID, dom, certList);
            }
            if (KnoxUtil.getInstance().doesContainerActive(resourceID)) {
                dom = this.createKnoxInfoElement(resourceID, dom, rootEle);
                dom = this.createKnoxRestrictionsElement(resourceID, dom, rootEle, platform, agentType);
                final Element knoxInstalledApp = this.createElement(dom, "KnoxInstalledApplicationList", rootEle);
                final List containerAppID = MdmInvDataProcessor.getInstance().getContainerAppIDFromResourceID((long)resourceID);
                for (int k = 0; k < containerAppID.size(); ++k) {
                    final long applicationID2 = containerAppID.get(k);
                    dom = this.createInstalledAppElement(applicationID2, dom, knoxInstalledApp);
                }
            }
            final String sXMLContent = this.DocToString(dom.getFirstChild());
            hsContentForPost.put("xmlcontent", sXMLContent);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception occurred in addMDMAssetContentforSDPXML() {0}", ex);
        }
        return hsContentForPost;
    }
    
    public HashMap addMDMAssetComputerContentforSDPXML(final Long resourceID, final HashMap hsContentForPost) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();
            final Element rootEle = dom.createElement("DocRoot");
            dom.appendChild(rootEle);
            final Element nodeEle = this.createNodeElement(resourceID, dom, rootEle);
            dom = this.createComputerInfoElement(resourceID, dom, nodeEle);
            dom = this.createHardwareInfoElement(resourceID, dom, nodeEle);
            final String sXMLContent = this.DocToString(dom.getFirstChild());
            hsContentForPost.put("xmlcontent", sXMLContent);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception occurred in addMDMAssetComputerContentforSDPXML() {0}", ex);
        }
        return hsContentForPost;
    }
    
    public String getServiceDeskBaseURL() throws Exception {
        final Properties props = this.getServerSettings("HelpDesk");
        return this.getServiceDeskBaseURL(props);
    }
    
    public String getAssetExplorerBaseURL() throws Exception {
        final Properties props = this.getServerSettings("AssetExplorer");
        return this.getServiceDeskBaseURL(props);
    }
    
    public void addOrUpdateServerSettings(final String appName, final Properties props) throws SyMException {
        try {
            final DataObject dataObject = this.getServerSettingsDO(appName);
            Row applnSettingsRow = null;
            if (dataObject.isEmpty()) {
                applnSettingsRow = new Row("ApplnServerSettings");
                applnSettingsRow = this.constructServerSettingsRow(applnSettingsRow, appName, props);
                applnSettingsRow.set("CREATED_TIME", (Object)System.currentTimeMillis());
                dataObject.addRow(applnSettingsRow);
                MDMUtil.getPersistence().add(dataObject);
            }
            else {
                applnSettingsRow = dataObject.getFirstRow("ApplnServerSettings");
                applnSettingsRow = this.constructServerSettingsRow(applnSettingsRow, appName, props);
                dataObject.updateRow(applnSettingsRow);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.SEVERE, ex, () -> "Caught exception while add/update of addOrUpdateServerSettings. Given props: " + props);
        }
    }
    
    public void deleteServerSettings(final String appName) throws SyMException {
        try {
            final DataObject sdpDObj = this.getServerSettingsDO(appName);
            if (!sdpDObj.isEmpty()) {
                final Row applnSettingsRow = sdpDObj.getFirstRow("ApplnServerSettings");
                sdpDObj.deleteRow(applnSettingsRow);
                MDMUtil.getPersistence().update(sdpDObj);
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.SEVERE, "Caught exception while deleting server settings, ex");
        }
    }
    
    public Row constructServerSettingsRow(final Row serverSettingsRow, final String appName, final Properties props) throws Exception {
        if (appName != null) {
            serverSettingsRow.set("APPNAME", (Object)appName);
        }
        if (props.getProperty("SERVER") != null) {
            serverSettingsRow.set("SERVER", (Object)props.getProperty("SERVER"));
        }
        if (props.getProperty("PORT") != null) {
            serverSettingsRow.set("PORT", (Object)props.getProperty("PORT"));
        }
        if (props.getProperty("PROTOCOL") != null) {
            serverSettingsRow.set("PROTOCOL", (Object)props.getProperty("PROTOCOL"));
        }
        if (props.getProperty("AUTHENTICATION_KEY") != null) {
            serverSettingsRow.set("AUTHENDICATION_KEY", (Object)props.getProperty("AUTHENTICATION_KEY"));
        }
        if (props.getProperty("IS_ENABLED") != null) {
            serverSettingsRow.set("IS_ENABLED", (Object)Boolean.valueOf(props.getProperty("IS_ENABLED")));
        }
        return serverSettingsRow;
    }
    
    public Properties getServerSettings(final String appName) {
        Properties appServerProp = null;
        try {
            final DataObject dataObject = this.getServerSettingsDO(appName);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ApplnServerSettings");
                appServerProp = new Properties();
                if (row != null) {
                    final List colList = row.getColumns();
                    for (final String colName : colList) {
                        final Object colValue = row.get(colName);
                        if (colValue != null) {
                            ((Hashtable<String, Object>)appServerProp).put(colName, colValue);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while getting SDP settings from ApplnServerSettings table:", ex);
        }
        return appServerProp;
    }
    
    public DataObject getServerSettingsDO(final String appName) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("ApplnServerSettings"));
        query.addSelectColumn(new Column("ApplnServerSettings", "*"));
        final Criteria criteria = new Criteria(new Column("ApplnServerSettings", "APPNAME"), (Object)appName, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public String getServiceDeskBaseURL(final Properties props) {
        String host = null;
        String port = null;
        String protocol = null;
        String baseURL = null;
        if (props != null) {
            if (props.getProperty("SERVER") != null) {
                host = props.getProperty("SERVER");
            }
            if (props.getProperty("PORT") != null) {
                port = props.getProperty("PORT");
            }
            if (props.getProperty("PROTOCOL") != null) {
                protocol = props.getProperty("PROTOCOL");
            }
            baseURL = protocol + "://" + host + ":" + port + "/";
            if (port == null || port.trim().length() == 0) {
                baseURL = protocol + "://" + host;
            }
            else {
                baseURL = protocol + "://" + host + ":" + port;
            }
        }
        return baseURL;
    }
    
    public void addSDPAccesslog(final String operationName, final String strUDID, final boolean status) {
        try {
            final Long deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName((long)deviceId);
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceId);
            final String customerName = CustomerInfoUtil.getInstance().getCustomerName(customerId);
            final HashMap accessLogMap = new HashMap();
            accessLogMap.put("operationName", operationName);
            accessLogMap.put("deviceName", deviceName);
            accessLogMap.put("udid", strUDID);
            accessLogMap.put("customerName", customerName);
            this.doAccessLogEntries(accessLogMap, status);
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while adding access log : {0}", e);
        }
    }
    
    private String getStringFromHashmap(final HashMap map, final String key) {
        return String.valueOf(map.get(key));
    }
    
    public void doAccessLogEntries(final HashMap hsContentForPost, final boolean dataPosted) {
        try {
            final String operationName = this.getStringFromHashmap(hsContentForPost, "operationName");
            final String deviceName = this.getStringFromHashmap(hsContentForPost, "deviceName");
            final String udid = this.getStringFromHashmap(hsContentForPost, "udid");
            final String customerName = this.getStringFromHashmap(hsContentForPost, "customerName");
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId(customerName);
            this.doAccessFileHandling();
            final int gapLen = 10;
            String printstr = "";
            printstr += operationName;
            printstr = this.fillgaps(printstr, operationName.length() + gapLen);
            printstr += deviceName;
            printstr = this.fillgaps(printstr, deviceName.length() + gapLen);
            printstr += udid;
            printstr = this.fillgaps(printstr, udid.length() + gapLen);
            printstr += customerName;
            printstr = this.fillgaps(printstr, customerName.length() + gapLen);
            printstr += customerId;
            printstr = this.fillgaps(printstr, customerId.toString().length() + gapLen);
            if (dataPosted) {
                printstr += "YES";
            }
            else {
                printstr += "NO";
            }
            MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, printstr);
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while adding SDP access log : {0}", e);
        }
    }
    
    private String fillgaps(String strVal, final int gaplen) {
        for (int i = 0; i < gaplen; ++i) {
            strVal += " ";
        }
        return strVal;
    }
    
    private void doAccessFileHandling() {
        try {
            boolean bRet = false;
            final String fileName = "mdmsdpaccesslog0.txt";
            final String str = System.getProperty("server.home") + File.separator + "logs" + File.separator + fileName;
            final File file = new File(str);
            if (file.exists()) {
                final FileReader fr = new FileReader(file);
                final LineNumberReader ln = new LineNumberReader(fr);
                int count = 0;
                while (ln.readLine() != null) {
                    if (count >= 2) {
                        bRet = true;
                        break;
                    }
                    ++count;
                }
                ln.close();
                fr.close();
            }
            else {
                MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, "Access log file does not exists!");
                bRet = false;
            }
            if (!bRet) {
                MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, "Access log file -> No Data");
                final String line1 = "OPERATION NAME      DEVICE_NAME     UDID        CUSTOMER_NAME       CUSTOMER_ID       IS_DATA_POSTED";
                final String line2 = "--------------      -----------     ----        -------------       -----------       --------------";
                MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, line1);
                MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, line2);
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, "Exception in doAccessFileHandling", ex);
        }
    }
    
    public String DocToString(final Node node) {
        try {
            final Source source = new DOMSource(node);
            final StringWriter stringWriter = new StringWriter();
            final Result result = new StreamResult(stringWriter);
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPACCESSLOGGER.log(Level.INFO, "Exception in SDP DocToString", ex);
            return null;
        }
    }
    
    public Element createAPIElement(final Document doc) {
        final Element apiElement = doc.createElement("API");
        final Attr attr1 = doc.createAttribute("version");
        attr1.setValue("1.0");
        apiElement.setAttributeNode(attr1);
        doc.appendChild(apiElement);
        return apiElement;
    }
    
    public Element createCITypeElement(final Document doc, final String ciType) {
        final Element citype = doc.createElement("citype");
        final Element name = doc.createElement("name");
        name.setTextContent(ciType);
        citype.appendChild(name);
        return citype;
    }
    
    public Element createCriteriaElement(final Document doc, final String strUDID) {
        final Element criterias = doc.createElement("criterias");
        final Element criteria = doc.createElement("criteria");
        criterias.appendChild(criteria);
        final Element parameter = doc.createElement("parameter");
        criteria.appendChild(parameter);
        final Element name1 = doc.createElement("name");
        name1.setTextContent("CI Name");
        final Attr attr = doc.createAttribute("compOperator");
        attr.setValue("CONTAINS");
        name1.setAttributeNode(attr);
        parameter.appendChild(name1);
        final Element value = doc.createElement("value");
        value.setTextContent(strUDID);
        parameter.appendChild(value);
        this.addAccountSiteCriteria(doc, criteria, strUDID);
        return criterias;
    }
    
    private void addAccountSiteCriteria(final Document doc, final Element criteria, final String strUDID) {
        try {
            if (CustomerInfoUtil.getInstance().isMSP()) {
                final Element reloperator1 = doc.createElement("reloperator");
                reloperator1.setTextContent("AND");
                criteria.appendChild(reloperator1);
                final Long deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceId);
                final String customerName = CustomerInfoUtil.getInstance().getCustomerNameFromID(customerID);
                final Element parameter2 = doc.createElement("parameter");
                criteria.appendChild(parameter2);
                final Element name2 = doc.createElement("name");
                name2.setTextContent("Account");
                final Attr attr = doc.createAttribute("compOperator");
                attr.setValue("IS");
                name2.setAttributeNode(attr);
                parameter2.appendChild(name2);
                final Element value2 = doc.createElement("value");
                value2.setTextContent(customerName);
                parameter2.appendChild(value2);
                final Element reloperator2 = doc.createElement("reloperator");
                reloperator2.setTextContent("OR");
                criteria.appendChild(reloperator2);
                final Element parameter3 = doc.createElement("parameter");
                criteria.appendChild(parameter3);
                final Element name3 = doc.createElement("name");
                name3.setTextContent("Site");
                final Attr attr2 = doc.createAttribute("compOperator");
                attr2.setValue("IS");
                name3.setAttributeNode(attr2);
                parameter3.appendChild(name3);
                final Element value3 = doc.createElement("value");
                value3.setTextContent(customerName);
                parameter3.appendChild(value3);
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in setting Account Criteria : ", e);
        }
    }
    
    public Element createNewValueElement(final Document doc, final String assetStatus) {
        final Element newvalue = doc.createElement("newvalue");
        final Element record = doc.createElement("record");
        newvalue.appendChild(record);
        final Element parameter1 = doc.createElement("parameter");
        record.appendChild(parameter1);
        final Element name1 = doc.createElement("name");
        name1.setTextContent("Asset State");
        parameter1.appendChild(name1);
        final Element value1 = doc.createElement("value");
        value1.setTextContent(assetStatus);
        parameter1.appendChild(value1);
        return newvalue;
    }
    
    public Element createNewOwnerValueElement(final Document doc, final String userName) {
        final Element newvalue = doc.createElement("newvalue");
        final Element record = doc.createElement("record");
        newvalue.appendChild(record);
        final Element parameter1 = doc.createElement("parameter");
        record.appendChild(parameter1);
        final Element name1 = doc.createElement("name");
        name1.setTextContent("Asset State");
        parameter1.appendChild(name1);
        final Element value1 = doc.createElement("value");
        value1.setTextContent("In use");
        parameter1.appendChild(value1);
        final Element multiValuedParam = doc.createElement("multi-valued-parameter");
        final Attr name2 = doc.createAttribute("name");
        name2.setValue("Assign Ownership");
        multiValuedParam.setAttributeNode(name2);
        record.appendChild(multiValuedParam);
        final Element userRecord = doc.createElement("record");
        multiValuedParam.appendChild(userRecord);
        final Element userParameter = doc.createElement("parameter");
        userRecord.appendChild(userParameter);
        final Element user = doc.createElement("name");
        user.setTextContent("User");
        userParameter.appendChild(user);
        final Element uservalue = doc.createElement("value");
        uservalue.setTextContent(userName);
        userParameter.appendChild(uservalue);
        return newvalue;
    }
    
    public Map retrieveStatusFromResponseMap(final String response) {
        final Map responseMap = new HashMap();
        try {
            final DocumentBuilder builder = SecurityUtil.getDocumentBuilder();
            final Document doc = builder.parse(new InputSource(new StringReader(response)));
            final Element statusElement = (Element)doc.getElementsByTagName("status").item(0);
            final String statusValue = statusElement.getChildNodes().item(0).getNodeValue();
            responseMap.put("status", statusValue);
            final Element messageElement = (Element)doc.getElementsByTagName("message").item(0);
            final String messageValue = messageElement.getChildNodes().item(0).getNodeValue();
            responseMap.put("message", messageValue);
            if (statusValue.equalsIgnoreCase("success")) {
                final NodeList detailsList = doc.getElementsByTagName("Details");
                if (detailsList != null && detailsList.getLength() > 0) {
                    for (int j = 0; j < detailsList.getLength(); ++j) {
                        final Element detailEle = (Element)detailsList.item(j);
                        final NodeList paramList = detailEle.getElementsByTagName("parameter");
                        if (paramList != null && paramList.getLength() > 0) {
                            for (int i = 0; i < paramList.getLength(); ++i) {
                                final Element ele = (Element)paramList.item(i);
                                final Element workOrderElement = (Element)ele.getElementsByTagName("name").item(0);
                                final String workOrderName = workOrderElement.getChildNodes().item(0).getNodeValue();
                                if (ele.getElementsByTagName("value") != null && ele.getElementsByTagName("value").item(0).getChildNodes().item(0) != null) {
                                    final String workOrderValue = ele.getElementsByTagName("value").item(0).getChildNodes().item(0).getNodeValue();
                                    responseMap.put(workOrderName, workOrderValue);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "retrieveStatusFromResponseMap Exception  : ", e);
        }
        return responseMap;
    }
    
    public void addMETrackingSDPData(final int statuscode) {
        switch (statuscode) {
            case 5000: {
                METrackerUtil.incrementMETrackParams("TotalCount");
                break;
            }
            case 4000: {
                METrackerUtil.incrementMETrackParams("SuccessCount");
                break;
            }
            case 4001: {
                METrackerUtil.incrementMETrackParams("DiffNoFilesAttachedCount");
                break;
            }
            case 4008: {
                METrackerUtil.incrementMETrackParams("InvalidXMLFormatCount");
                break;
            }
            case 4003: {
                METrackerUtil.incrementMETrackParams("MaxSizeReachedCount");
                break;
            }
            case 4004: {
                METrackerUtil.incrementMETrackParams("EmptyFileCount");
                break;
            }
            case 4005: {
                METrackerUtil.incrementMETrackParams("FullScanFailCount");
                break;
            }
            case 4006: {
                METrackerUtil.incrementMETrackParams("DiffScanFailCount");
                break;
            }
            case 4007: {
                METrackerUtil.incrementMETrackParams("CompNACount");
                break;
            }
            case 5001: {
                METrackerUtil.incrementMETrackParams("FailCount");
                break;
            }
            case 5005: {
                METrackerUtil.incrementMETrackParams("SDPNotReachableCount");
                break;
            }
        }
    }
    
    public boolean retrieveStatusFromJSONResponse(final String response, final Properties operationProp) {
        boolean sdpStatus = false;
        try {
            final String typeOfOperation = String.valueOf(operationProp.getProperty("TYPE_OF_OPERATION"));
            final Boolean isNotTrial = Boolean.valueOf(operationProp.getProperty("IS_NOT_TRAIL"));
            JSONObject responseJSON = new JSONObject(response);
            if (typeOfOperation.equalsIgnoreCase("updateasset") || typeOfOperation.equalsIgnoreCase("deleteasset")) {
                responseJSON = responseJSON.getJSONObject("API").getJSONObject("response");
            }
            final JSONObject operationResponse = responseJSON.getJSONObject("operation");
            final JSONObject resultofResponse = operationResponse.getJSONObject("result");
            final String statusValue = (String)resultofResponse.get("status");
            final String messageValue = (String)resultofResponse.get("message");
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "The Response From SDP Server:  Status :   {0}  Message is :   {1}", new Object[] { statusValue, messageValue });
            if (isNotTrial && (statusValue.equalsIgnoreCase("true") || statusValue.equalsIgnoreCase("success"))) {
                sdpStatus = true;
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in retrieving status from SDP Response", e);
        }
        return sdpStatus;
    }
    
    public Document createMappedUsersElement(final String userName, final String domainName) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.newDocument();
        final Element details = doc.createElement("Details");
        doc.appendChild(details);
        Element parameter = doc.createElement("parameter");
        Element name = doc.createElement("name");
        name.setTextContent("loginname");
        parameter.appendChild(name);
        Element vlaue = doc.createElement("value");
        vlaue.setTextContent(userName);
        parameter.appendChild(vlaue);
        details.appendChild(parameter);
        if (domainName != null && domainName.length() > 0) {
            parameter = doc.createElement("parameter");
            name = doc.createElement("name");
            name.setTextContent("domainname");
            parameter.appendChild(name);
            vlaue = doc.createElement("value");
            vlaue.setTextContent(domainName);
            parameter.appendChild(vlaue);
            details.appendChild(parameter);
        }
        return doc;
    }
    
    public Element createElement(final Document document, final String sElementName, final Element parentElement) {
        final Element element = document.createElement(sElementName);
        parentElement.appendChild(element);
        return element;
    }
    
    public static Document mdCreateElement(final Document document, final String sElementName, final Element parentElement, final HashMap arrElementProperties) {
        try {
            final Element element = document.createElement(sElementName);
            final Map map = arrElementProperties;
            for (final Map.Entry entry : map.entrySet()) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.FINE, "mdCreateElement: Key = {0}, Value = {1}", new Object[] { key, value });
                if (value != "--") {
                    element.setAttribute(key.toString(), value.toString());
                }
            }
            parentElement.appendChild(element);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in mdCreateElement() {0}", ex);
        }
        return document;
    }
    
    public Document createComputerInfoElement(final long resourceID, Document document, final Element parentElement) throws SyMException {
        try {
            final Element compInfoEle = this.createElement(document, "Computer_Info", parentElement);
            final String sElementName = "Computer";
            final HashMap compHash = new HashMap();
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
            compHash.put("Name", deviceName);
            document = mdCreateElement(document, sElementName, compInfoEle, compHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception occurred in createComputerInfoElement() {0}", ex);
        }
        return document;
    }
    
    public Document createHardwareInfoElement(final Long deviceId, Document document, final Element parentElement) throws SyMException {
        try {
            final Element hardwareInfoEle = this.createElement(document, "Hardware_Info", parentElement);
            final HashMap deviceHash = MdmInvDataProcessor.getInstance().getCompleteDeviceInfo((long)deviceId, new HashMap());
            final HashMap deviceSDPHash = new HashMap();
            deviceSDPHash.put("Name", deviceHash.get("NAME"));
            deviceSDPHash.put("Model", deviceHash.get("MODEL"));
            deviceSDPHash.put("isLaptop", "9");
            document = mdCreateElement(document, "Computer", hardwareInfoEle, deviceSDPHash);
            final HashMap osSDPHash = new HashMap();
            final int platformType = deviceHash.get("PLATFORM_TYPE");
            final String platformName = MDMUtil.getInstance().getPlatformName(platformType);
            osSDPHash.put("Name", platformName);
            osSDPHash.put("BuildNumber", deviceHash.get("BUILD_VERSION"));
            osSDPHash.put("Version", deviceHash.get("OS_VERSION"));
            osSDPHash.put("SerialNumber", deviceHash.get("SERIAL_NUMBER"));
            document = mdCreateElement(document, "OperatingSystem", hardwareInfoEle, osSDPHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception occurred in createHardwareInfoElement() {0}", ex);
        }
        return document;
    }
    
    public Document createDeviceInfoElement(final long resourceID, Document document, final Element parentElement) throws SyMException {
        try {
            final String sElementName = "DeviceInfo";
            HashMap deviceInfoHash = new HashMap();
            deviceInfoHash = MdmInvDataProcessor.getInstance().getCompleteDeviceInfo(resourceID, deviceInfoHash);
            final int platformType = deviceInfoHash.getOrDefault("PLATFORM_TYPE", 1);
            if (platformType == 1) {
                deviceInfoHash.put("PRODUCT_NAME", deviceInfoHash.getOrDefault("MODEL_NAME", "--"));
            }
            else {
                deviceInfoHash.put("PRODUCT_NAME", deviceInfoHash.getOrDefault("MANUFACTURER", "--"));
            }
            final int modelType = deviceInfoHash.getOrDefault("MODEL_TYPE", 1);
            final String modelTypeStr = (modelType == 2) ? "Tablets" : "Smartphones";
            deviceInfoHash.put("MODEL_TYPE", modelTypeStr);
            String imei = deviceInfoHash.getOrDefault("IMEI", "");
            imei = (imei.equalsIgnoreCase("--") ? "" : imei);
            deviceInfoHash.put("IMEI", imei);
            document = mdCreateElement(document, sElementName, parentElement, deviceInfoHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createDeviceInfoElement() {0}", ex);
        }
        return document;
    }
    
    public Document createMobilePlatformElement(final long resourceID, final String strUDID, Document document, final Element parentElement, final String platformName) throws SyMException {
        try {
            final String sElementName = "MobilePlatform";
            final HashMap deviceInfoHash = new HashMap();
            deviceInfoHash.put("Type", platformName);
            deviceInfoHash.put("UDID", strUDID);
            deviceInfoHash.put("RESOURCE_ID", String.valueOf(resourceID));
            document = mdCreateElement(document, sElementName, parentElement, deviceInfoHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createMobilePlatformElement() {0}", ex);
        }
        return document;
    }
    
    public Document createAccountInfoElement(Document document, final Element parentElement, final HashMap hsContentForPost) throws SyMException {
        try {
            final String sElementName = "Account_Info";
            final HashMap accountInfoHash = new HashMap();
            accountInfoHash.put("AccountName", hsContentForPost.get("customername"));
            document = mdCreateElement(document, sElementName, parentElement, accountInfoHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createAccountInfoElement() {0}", ex);
        }
        return document;
    }
    
    public Element createNodeElement(final Long deviceId, final Document document, final Element parentElement) throws SyMException {
        Element nodeEle = null;
        try {
            nodeEle = document.createElement("Node");
            final HashMap deviceHash = MdmInvDataProcessor.getInstance().getNetworkDetails((long)deviceId, new HashMap());
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName((long)deviceId);
            nodeEle.setAttribute("ComputerName", deviceName);
            nodeEle.setAttribute("MacAddress", deviceHash.get("WIFI_MAC"));
            parentElement.appendChild(nodeEle);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception occured in createNodeElement() {0}", ex);
        }
        return nodeEle;
    }
    
    public Document createKnoxInfoElement(final long resourceID, Document document, final Element parentElement) throws SyMException {
        try {
            final String sElementName = "KnoxInfo";
            HashMap deviceInfoHash = new HashMap();
            deviceInfoHash = MdmInvDataProcessor.getInstance().getCompleteKnoxInfo(resourceID, deviceInfoHash);
            document = mdCreateElement(document, sElementName, parentElement, deviceInfoHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createDeviceInfoElement() {0}", ex);
        }
        return document;
    }
    
    public Document createSecurityInfoElement(final long resourceID, Document document, final Element parentElement) throws SyMException {
        try {
            final String sElementName = "SecurityInfo";
            HashMap securityHash = new HashMap();
            securityHash = MdmInvDataProcessor.getInstance().getSecurityDetails(resourceID, securityHash);
            document = mdCreateElement(document, sElementName, parentElement, securityHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createSecurityInfoElement() {0}", ex);
        }
        return document;
    }
    
    public Document createRestrictionsElement(final long resourceID, Document document, final Element parentElement, final int platform, final int agentType) throws SyMException {
        try {
            String sElementName = "Restrictions";
            HashMap restHash = new HashMap();
            switch (platform) {
                case 1: {
                    restHash = MdmInvDataProcessor.getInstance().getIOSRestrictionsDetails(resourceID, restHash);
                    break;
                }
                case 2: {
                    sElementName = "AndoridRestrictions";
                    restHash = MdmInvDataProcessor.getInstance().getAndroidRestrictionsDetails(Long.valueOf(resourceID), restHash, 0);
                    break;
                }
                case 3: {
                    restHash = MdmInvDataProcessor.getInstance().getWindowsRestrictionsDetails(Long.valueOf(resourceID), restHash);
                    break;
                }
            }
            document = mdCreateElement(document, sElementName, parentElement, restHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createRestrictionsElement() ", ex);
        }
        return document;
    }
    
    public Document createKnoxRestrictionsElement(final long resourceID, Document document, final Element parentElement, final int platform, final int agentType) throws SyMException {
        try {
            final String sElementName = "KnoxRestriction";
            HashMap restHash = new HashMap();
            restHash = MdmInvDataProcessor.getInstance().getAndroidRestrictionsDetails(Long.valueOf(resourceID), restHash, 1);
            document = mdCreateElement(document, sElementName, parentElement, restHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createKnoxRestrictionsElement() {0}", ex);
        }
        return document;
    }
    
    public Document createKnoxInstalledApplicationList(final long resourceID, Document document, final Element parentElement) throws SyMException {
        try {
            final String sElementName = "KnoxInstalledApplicationList";
            HashMap deviceInfoHash = new HashMap();
            deviceInfoHash = MdmInvDataProcessor.getInstance().getAppsInstalledInContainer(Long.valueOf(resourceID), deviceInfoHash);
            document = mdCreateElement(document, sElementName, parentElement, deviceInfoHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createKnoxInstalledApplicationList() {0}", ex);
        }
        return document;
    }
    
    public Document createInstalledAppElement(final long appID, Document document, final Element parentElement) throws SyMException {
        try {
            final String sElementName = "App";
            HashMap appHash = new HashMap();
            appHash = MdmInvDataProcessor.getInstance().getInstalledAppDetailsForAppID(appID, appHash);
            document = mdCreateElement(document, sElementName, parentElement, appHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createInstalledAppElement() {0}", ex);
        }
        return document;
    }
    
    public Document createCertificateElement(final long certID, Document document, final Element parentElement) throws SyMException {
        try {
            final String sElementName = "Certificate";
            HashMap certHash = new HashMap();
            certHash = MdmInvDataProcessor.getInstance().getCertificateDetailsForCertID(certID, certHash);
            document = mdCreateElement(document, sElementName, parentElement, certHash);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Exception Occured in createCertificateElement() {0}", ex);
        }
        return document;
    }
    
    public String requestSDP(final String baseURL, final String method) {
        String responseFromSDP = "";
        try {
            String authenticationKey = SolutionUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
            CustomerInfoUtil.getInstance();
            final Boolean isMDMP = CustomerInfoUtil.isMDMP();
            if (!isMDMP) {
                authenticationKey = Encoder.convertFromBase(authenticationKey);
            }
            final HttpURLConnection httpURLConnection = (HttpURLConnection)this.createURLConnection("HelpDesk", baseURL, true);
            if (authenticationKey != null) {
                httpURLConnection.setRequestProperty("AUTHTOKEN", authenticationKey);
            }
            final int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                final BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                final StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                responseFromSDP = sb.toString();
            }
            else {
                responseFromSDP = String.valueOf(code);
            }
            return responseFromSDP;
        }
        catch (final ConnectException cex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "unable to connect with servicedesk plus  ", cex);
            return "SDPNotReached";
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception while creating connection  ", e);
            return null;
        }
    }
    
    public String requestSDP(final URLConnection conn, final Document doc, final Properties operationProp) {
        String params = "";
        String line = "";
        StringBuffer strbuff = null;
        try {
            String authenticationKey = String.valueOf(operationProp.getProperty("AUTHENTICATION_KEY"));
            final String operationName = String.valueOf(operationProp.getProperty("OPERATION_NAME"));
            CustomerInfoUtil.getInstance();
            final Boolean isMDMP = CustomerInfoUtil.isMDMP();
            if (!isMDMP) {
                authenticationKey = Encoder.convertFromBase(authenticationKey);
            }
            if (authenticationKey != null && operationName != null) {
                params = "TECHNICIAN_KEY=" + authenticationKey.trim() + "&" + "OPERATION_NAME" + "=" + operationName.trim();
            }
            if (doc != null) {
                final StringWriter strWrite = new StringWriter();
                final Source src = new DOMSource(doc);
                final Transformer trans = TransformerFactory.newInstance().newTransformer();
                final Result res = new StreamResult(strWrite);
                trans.setOutputProperty("indent", "yes");
                trans.transform(src, res);
                if (authenticationKey != null && operationName != null && strWrite.toString() != null) {
                    if (CustomerInfoUtil.getInstance().isMSP() && operationName.equals("ADD_REQUEST")) {
                        params = params + "&" + "data" + "=" + strWrite.toString().trim();
                    }
                    else {
                        params = params + "&" + "INPUT_DATA" + "=" + strWrite.toString().trim();
                    }
                }
            }
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Request send to SDP  -  {0}  -  {1} ", new Object[] { operationName, params });
            final OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            osw.write(params);
            osw.flush();
            final InputStreamReader inpStream = new InputStreamReader(conn.getInputStream());
            final BufferedReader rd = new BufferedReader(inpStream);
            strbuff = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                strbuff.append(line);
            }
            osw.close();
            inpStream.close();
            rd.close();
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception when SDP request is made", e);
        }
        return strbuff.toString();
    }
    
    public String requestSDP(final URLConnection conn, final JSONObject addRequest, final Properties operationProp) {
        String params = "";
        String line = "";
        StringBuffer strbuff = null;
        String authenticationKey = String.valueOf(operationProp.getProperty("AUTHENTICATION_KEY"));
        final String operationName = String.valueOf(operationProp.getProperty("OPERATION_NAME"));
        CustomerInfoUtil.getInstance();
        final Boolean isMDMP = CustomerInfoUtil.isMDMP();
        try {
            if (!isMDMP) {
                authenticationKey = Encoder.convertFromBase(authenticationKey);
            }
            if (authenticationKey != null && operationName != null) {
                params = "TECHNICIAN_KEY=" + authenticationKey.trim() + "&" + "OPERATION_NAME" + "=" + operationName.trim() + "&" + "format" + "=json";
            }
            if (addRequest != null) {
                final String strAddRequest = URLEncoder.encode(addRequest.toString(), "UTF-8");
                if (authenticationKey != null && operationName != null && strAddRequest != null) {
                    if (CustomerInfoUtil.getInstance().isMSP()) {
                        params = params + "&" + "data" + "=" + strAddRequest;
                    }
                    else {
                        params = params + "&" + "INPUT_DATA" + "=" + strAddRequest;
                    }
                }
            }
            final OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            osw.write(params);
            osw.flush();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            strbuff = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                strbuff.append(line);
            }
            osw.close();
            rd.close();
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "---SDP HelpDesk Request Exception---", e);
        }
        return strbuff.toString();
    }
    
    public JSONObject appendJSONElement(final Iterator itr, final String type) {
        final JSONObject addRequest = new JSONObject();
        String jsonDetail = new String();
        final String jsonOperation = "operation";
        jsonDetail = "details";
        MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "appendJSONElement() for '{0}'", type);
        try {
            final JSONObject requestDetails = new JSONObject();
            final JSONObject details = new JSONObject();
            if (type.equalsIgnoreCase("ADD_REQUEST") || type.equalsIgnoreCase("GET_REQUEST") || type.equalsIgnoreCase("EDIT_REQUEST") || type.equalsIgnoreCase("CLOSE_REQUEST")) {
                this.populateJSON(itr, requestDetails);
            }
            else if (type.equalsIgnoreCase("ADD_WORKLOG")) {
                final JSONObject worklogs = new JSONObject();
                final JSONObject worklog = new JSONObject();
                this.populateJSON(itr, worklog);
                worklogs.put("worklog", (Object)worklog);
                requestDetails.put("worklogs", (Object)worklogs);
            }
            details.put(jsonDetail, (Object)requestDetails);
            addRequest.put(jsonOperation, (Object)details);
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.FINE, "---JSON Object contains---{0}", addRequest);
            return addRequest;
        }
        catch (final Exception e) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "---SDP appendJSONElement Method Exception---", e);
            return null;
        }
    }
    
    public JSONObject populateJSON(final Iterator itr, final JSONObject json) throws Exception {
        while (itr.hasNext()) {
            final Map.Entry entry = itr.next();
            final String propKey = entry.getKey().toString();
            final String propValue = entry.getValue().toString();
            json.put(propKey, (Object)propValue);
        }
        return json;
    }
    
    public String getSDPMappedLoginName(String loginName, final String domainName) {
        if (domainName == null && loginName != null && loginName.equalsIgnoreCase("admin")) {
            loginName = "administrator";
        }
        return loginName;
    }
    
    public String getScheduleScanTime(final String taskName) {
        String nextScheduledTime = "";
        try {
            final boolean invScanDisabled = ApiFactoryProvider.getSchedulerAPI().isSchedulerDisabled(taskName);
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Is inventory schedule is disabled:{0}", invScanDisabled);
            if (!invScanDisabled) {
                final Long nextExecTime = ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(taskName);
                if (nextExecTime == null || nextExecTime == -1L) {
                    nextScheduledTime = "--";
                }
                else {
                    nextScheduledTime = Utils.getEventTime(nextExecTime);
                }
            }
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.WARNING, "Exception in getting Inventory Schedule Time- ", ex);
        }
        return nextScheduledTime;
    }
    
    public JSONObject getIntegrationstatus() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final boolean sdpInvIntegrated = SolutionUtil.getInstance().getIntegrationParams("SDP_MDM_INV_INTEGRATION");
        final boolean aeInvIntegrated = SolutionUtil.getInstance().getIntegrationParams("AE_MDM_INV_INTEGRATION");
        final boolean isSDPAlertEnabled = SolutionUtil.getInstance().getIntegrationParams("SDP_MDM_HELPDESK_ALERT");
        jsonObject.put("SDP_MDM_INV_INTEGRATION", sdpInvIntegrated);
        jsonObject.put("AE_MDM_INV_INTEGRATION", aeInvIntegrated);
        jsonObject.put("SDP_MDM_HELPDESK_ALERT", isSDPAlertEnabled);
        return jsonObject;
    }
    
    public void updateConsentforSdp(final String isSDPMDMInvEnabledStr, final Long consentId) throws Exception {
        final Boolean assetEnabled = Boolean.parseBoolean(isSDPMDMInvEnabledStr);
        int status = 1;
        if (!assetEnabled) {
            status = 2;
        }
        int consentStatus;
        int eventId;
        String consentState;
        if (status == 1) {
            consentStatus = 1;
            eventId = 2201;
            consentState = I18N.getMsg("dc.common.APPROVED", new Object[0]);
        }
        else {
            consentStatus = 2;
            eventId = 2202;
            consentState = I18N.getMsg("mdm.privacy.common.denied", new Object[0]);
        }
        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        final String consentName = (String)DBUtil.getValueFromDB("Consent", "CONSENT_ID", (Object)consentId, "CONSENT_DESCRIPTION");
        final String remarksParam = I18N.getMsg(consentName, new Object[0]) + "@@@" + consentState + "@@@" + DMUserHandler.getUserName(userId);
        final JSONObject consentEventDetails = new JSONObject();
        consentEventDetails.put("event_id", eventId);
        consentEventDetails.put("remarks", (Object)"mdm.privacy.consent.remark.update");
        consentEventDetails.put("remarksArgs", (Object)remarksParam);
        final int code = ConsentStatusUtil.saveConsentStatus(userId, consentStatus, consentId, consentEventDetails, CustomerInfoUtil.getInstance().getCustomerId());
        if (1001 == code) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.SEVERE, "Error while saving the consent({0}) with status({1})", new Object[] { consentId, status });
        }
        else {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.INFO, "Consent({0}) with status({1}) Saved Successfully", new Object[] { consentId, status });
        }
    }
    
    public void handleSDPUIMETrack(final String reqURI, final String queryString) {
        String integParam = "";
        try {
            if (this.containsStr(reqURI, "mdm/groups")) {
                integParam = "MDMP_SDP_ASSIGN_GROUP_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/profiles") && this.containsStr(reqURI, "groups")) {
                integParam = "MDMP_SDP_ASSIGN_PROFILE_GROUP_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/profiles") && this.containsStr(reqURI, "devices")) {
                integParam = "MDMP_SDP_ASSIGN_PROFILE_DEVICE_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/apps") && this.containsStr(reqURI, "groups")) {
                integParam = "MDMP_SDP_ASSIGN_APP_GROUP_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/apps") && this.containsStr(reqURI, "devices")) {
                integParam = "MDMP_SDP_ASSIGN_APP_DEVICE_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/staged_devices")) {
                integParam = "MDMP_SDP_ASSIGN_STAGED_DEVICE_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/devices")) {
                if (this.containsStr(reqURI, "locations")) {
                    integParam = "MDMP_SDP_LOCATE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "deprovision")) {
                    integParam = "MDMP_SDP_DEPROVISION_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "lock")) {
                    integParam = "MDMP_SDP_LOCK_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "alarm")) {
                    integParam = "MDMP_SDP_ALARM_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "enterprise/erase")) {
                    integParam = "MDMP_SDP_CORPORATE_WIPE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "erase")) {
                    integParam = "MDMP_SDP_COMPLETE_WIPE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "passcode/clear")) {
                    integParam = "MDMP_SDP_CLEAR_PASSCODE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "lostmode/enable")) {
                    integParam = "MDMP_SDP_LOST_MODE_DEVICE_COUNT";
                }
            }
            MDMIntegrationUtil.getInstance().incrementIntegCount(integParam);
        }
        catch (final Exception ex) {
            MDMSDPIntegrationUtil.SDPINTEGLOGGER.log(Level.SEVERE, "Exception in handling ME track for SDP UI", ex);
        }
    }
    
    private boolean containsStr(final String queryString, final String containVal) {
        final int index = queryString.indexOf(containVal);
        return index != -1;
    }
    
    static {
        SDPACCESSLOGGER = Logger.getLogger("MDMSDPAccessLog");
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSDPIntegrationUtil.integUtil = null;
    }
}
