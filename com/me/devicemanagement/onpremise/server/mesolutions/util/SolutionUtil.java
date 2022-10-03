package com.me.devicemanagement.onpremise.server.mesolutions.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.net.HttpURLConnection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Map;
import java.net.URLConnection;
import org.json.JSONObject;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.util.Encoder;
import java.net.URL;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.mesolutions.notification.SDPNotificationUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.cache.CacheManager;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class SolutionUtil
{
    protected static Logger logger;
    private static Logger sdpodlogger;
    private static String className;
    protected static SolutionUtil solutionUtil;
    protected static Boolean isIntegrationMode;
    public static final String SDP_INV_INTEGRATION = "SDP_INV_INTEGRATION";
    public static final String SDP_MDM_INV_INTEGRATION = "SDP_MDM_INV_INTEGRATION";
    public static final String SDP_SWD_INTEGRATION = "SDP_SWD_INTEGRATION";
    public static final String SDP_HTR_INTEGRATION = "SDP_HTR_INTEGRATION";
    public static final String APPNAME = "HelpDesk";
    public static Properties appServerProp;
    protected static boolean icConfigListenerRegistered;
    protected static HashMap pluginParams;
    public static final String SDP_PNAME = "SDPEE";
    public static final String SDP_PLUGIN_ENABLED = "SDP_PLUGIN_ENABLED";
    public static final String SDP_RDS_INTEGRATION = "SDP_RDS_INTEGRATION";
    public static final String SDP_INTEGRATION_ENABLED = "SDP_INTEGRATION_ENABLED";
    public static final String SDPOD_INTEGRATION_ENABLED = "SDPOD_INTEGRATION_ENABLED";
    public static final String SERVER_RESTART = "SERVER_RESTART";
    public static final String AE_INTEGRATION_ENABLED = "AE_INTEGRATION_ENABLED";
    public static final String APPNAME_SDPOD = "HelpDeskOD";
    public static final String APPNAME_AE = "AssetExplorer";
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DISABLED = 2;
    
    public static SolutionUtil getInstance() {
        if (SolutionUtil.solutionUtil == null) {
            SolutionUtil.solutionUtil = new SolutionUtil();
        }
        return SolutionUtil.solutionUtil;
    }
    
    protected SolutionUtil() {
        this.setIntegrationMode(null, "SDP_INV_INTEGRATION");
        this.setIntegrationMode(null, "SDP_MDM_INV_INTEGRATION");
        this.setIntegrationMode(null, "SDP_SWD_INTEGRATION");
        this.setIntegrationMode(null, "SDP_HTR_INTEGRATION");
        this.setIntegrationMode(null, "SERVER_RESTART");
        this.setSDPIntegrationMode();
        this.initIntegParams();
    }
    
    private void initIntegParams() {
        (SolutionUtil.pluginParams = new HashMap()).put("SDPEE", "SDP_PLUGIN_ENABLED");
    }
    
    public void setIntegrationMode(final boolean bool) {
        this.setIntegrationMode(bool, "SDP_INV_INTEGRATION");
        this.setIntegrationMode(bool, "SDP_MDM_INV_INTEGRATION");
        this.setIntegrationMode(false, "SDP_SWD_INTEGRATION");
        this.setIntegrationMode(false, "SDP_HTR_INTEGRATION");
        this.setIntegrationMode(false, "SDP_RDS_INTEGRATION");
    }
    
    public boolean isSDPODIntegrationMode() {
        return this.getSDPODIntegrationMode();
    }
    
    public boolean getSDPODIntegrationMode() {
        String isShowDCMenu = (String)CacheManager.getCacheRepository().getFromCache((Object)"SDPOD_INTEGRATION_ENABLED");
        if (isShowDCMenu == null) {
            try {
                final Properties props = this.getServerSettings("HelpDeskOD");
                if (props != null && props.get("IS_ENABLED") != null) {
                    isShowDCMenu = "" + ((Hashtable<K, Object>)props).get("IS_ENABLED");
                }
                else {
                    isShowDCMenu = "false";
                }
            }
            catch (final Exception ex) {
                SolutionUtil.sdpodlogger.log(Level.WARNING, SolutionUtil.className + "Caught exception while getting SDPOD Enabled flag", ex);
            }
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("ApplnServerSettings");
            CacheManager.getCacheRepository().addToCache((Object)"SDPOD_INTEGRATION_ENABLED", (Object)String.valueOf(isShowDCMenu), (List)tableNames);
        }
        return Boolean.valueOf(isShowDCMenu);
    }
    
    public boolean isAEIntegrationMode() {
        return this.getAEIntegrationMode();
    }
    
    public boolean getAEIntegrationMode() {
        String isShowDCMenu = (String)CacheManager.getCacheRepository().getFromCache((Object)"AE_INTEGRATION_ENABLED");
        if (isShowDCMenu == null) {
            try {
                final Properties props = this.getServerSettings("AssetExplorer");
                if (props != null && props.get("IS_ENABLED") != null) {
                    isShowDCMenu = "" + ((Hashtable<K, Object>)props).get("IS_ENABLED");
                }
                else {
                    isShowDCMenu = "false";
                }
            }
            catch (final Exception ex) {
                SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Caught exception while getting AE Enabled flag", ex);
            }
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("ApplnServerSettings");
            CacheManager.getCacheRepository().addToCache((Object)"AE_INTEGRATION_ENABLED", (Object)String.valueOf(isShowDCMenu), (List)tableNames);
        }
        return Boolean.valueOf(isShowDCMenu);
    }
    
    public void setIntegrationMode(Boolean bool, final String paramName) {
        SolutionUtil.logger.log(Level.INFO, SolutionUtil.className + "Setting Integration Mode = " + bool + " Params Name : " + paramName);
        Boolean dbVal = Boolean.FALSE;
        try {
            if (bool == null) {
                dbVal = this.getIntegrationParams(paramName);
                SolutionUtil.logger.log(Level.INFO, SolutionUtil.className + "Value From DB -- Integration Mode = " + dbVal);
                bool = dbVal;
            }
        }
        catch (final Exception ex) {
            SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Caught exception while setting Integration Mode. Setting default value: false", ex);
            if (bool == null) {
                bool = Boolean.FALSE;
            }
        }
        this.updateIntegrationParameter(paramName, bool.toString());
    }
    
    public void updateIntegrationParameter(final String paramName, final String paramValue) {
        try {
            if (paramName != null && paramValue != null) {
                final Column col = new Column("IntegrationParams", "PARAM_NAME");
                final Criteria criteria = new Criteria(col, (Object)paramName, 0, (boolean)Boolean.FALSE);
                final DataObject paramDO = SyMUtil.getPersistence().get("IntegrationParams", criteria);
                if (paramDO.isEmpty()) {
                    final Row paramRow = new Row("IntegrationParams");
                    paramRow.set("PARAM_NAME", (Object)paramName);
                    paramRow.set("PARAM_VALUE", (Object)paramValue);
                    paramDO.addRow(paramRow);
                    DataAccess.add(paramDO);
                    SolutionUtil.logger.log(Level.INFO, SolutionUtil.className + "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
                }
                else {
                    final Row paramRow = paramDO.getFirstRow("IntegrationParams");
                    paramRow.set("PARAM_VALUE", (Object)paramValue);
                    paramDO.updateRow(paramRow);
                    DataAccess.update(paramDO);
                    SolutionUtil.logger.log(Level.INFO, SolutionUtil.className + "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
                }
                final ArrayList tableNames = new ArrayList();
                tableNames.add("IntegrationParams");
                try {
                    ApiFactoryProvider.getCacheAccessAPI().putCache(paramName, (Object)paramValue, (List)tableNames);
                }
                catch (final Exception e) {
                    SolutionUtil.logger.log(Level.SEVERE, "Error while adding data to  cachemanager : " + e.getMessage());
                }
            }
        }
        catch (final Exception ex) {
            SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Caught exception while updating Parameter:" + paramName + " in DB." + ex.getMessage());
        }
    }
    
    public void setSDPIntegrationMode() {
        SolutionUtil.isIntegrationMode = false;
        try {
            final Properties props = this.getServerSettings("HelpDesk");
            if (props != null && props.get("IS_ENABLED") != null) {
                final Boolean isSDPEnabled = SolutionUtil.isIntegrationMode = ((Hashtable<K, Boolean>)props).get("IS_ENABLED");
            }
            else {
                SolutionUtil.isIntegrationMode = Boolean.FALSE;
            }
        }
        catch (final Exception ex) {
            SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Caught exception while getting SDP Enabled flag", ex);
        }
        final ArrayList tableNames = new ArrayList();
        tableNames.add("ApplnServerSettings");
        ApiFactoryProvider.getCacheAccessAPI().putCache("SDP_INTEGRATION_ENABLED", (Object)String.valueOf(SolutionUtil.isIntegrationMode), (List)tableNames);
    }
    
    public Properties getServerSettings(final String appName) throws SyMException {
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
            SolutionUtil.logger.log(Level.WARNING, "Exception while getting help desk server settings from ApplnServerSettings table:", ex);
            throw new SyMException(1002, (Throwable)ex);
        }
        return appServerProp;
    }
    
    public DataObject getServerSettingsDO(final String appName) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("ApplnServerSettings"));
        query.addSelectColumn(new Column("ApplnServerSettings", "*"));
        final Criteria criteria = new Criteria(new Column("ApplnServerSettings", "APPNAME"), (Object)appName, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = SyMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public String getSDPPropertyForTracking() {
        final StringBuffer sdpProps = new StringBuffer();
        final boolean isINVIntegrated = this.getIntegrationParams("SDP_INV_INTEGRATION");
        this.appendProps(sdpProps, isINVIntegrated, "A");
        final boolean isMDMINVIntegrated = this.getIntegrationParams("SDP_MDM_INV_INTEGRATION");
        this.appendProps(sdpProps, isMDMINVIntegrated, "M");
        final boolean isSWDIntegrated = this.getIntegrationParams("SDP_SWD_INTEGRATION");
        this.appendProps(sdpProps, isSWDIntegrated, "S");
        final boolean isHTIntegrated = this.getIntegrationParams("SDP_HTR_INTEGRATION");
        this.appendProps(sdpProps, isHTIntegrated, "H");
        final boolean isSDPPluginEnabled = this.getIntegrationParams("SDP_PLUGIN_ENABLED");
        this.appendProps(sdpProps, isSDPPluginEnabled, "P");
        SolutionUtil.logger.log(Level.INFO, "getSDPPropertyForTracking : {0} ", sdpProps.toString());
        return sdpProps.toString();
    }
    
    private void appendProps(final StringBuffer sdpProps, final boolean isIntegMode, final String key) {
        if (isIntegMode) {
            sdpProps.append(key);
        }
    }
    
    public boolean getSDPIntegrationMode() {
        String isShowDCMenu = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("SDP_INTEGRATION_ENABLED");
        if (isShowDCMenu == null) {
            try {
                final Properties props = this.getServerSettings("HelpDesk");
                if (props != null && props.get("IS_ENABLED") != null) {
                    isShowDCMenu = "" + ((Hashtable<K, Object>)props).get("IS_ENABLED");
                }
                else {
                    isShowDCMenu = "false";
                }
            }
            catch (final Exception ex) {
                SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Caught exception while getting SDP Enabled flag", ex);
            }
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("ApplnServerSettings");
            ApiFactoryProvider.getCacheAccessAPI().putCache("SDP_INTEGRATION_ENABLED", (Object)String.valueOf(isShowDCMenu), (List)tableNames);
        }
        return Boolean.valueOf(isShowDCMenu);
    }
    
    public boolean isIntegrationMode() {
        return this.getSDPIntegrationMode();
    }
    
    public void addAssetDataToSDPQueue(final DCQueueData dcQData, final int queueDataType) {
        try {
            if (dcQData == null) {
                SolutionUtil.logger.log(Level.FINEST, "=================================================================");
                SolutionUtil.logger.log(Level.FINEST, "DCQueueData is null, so it is not Que data population");
                SolutionUtil.logger.log(Level.FINEST, "=================================================================");
                return;
            }
            SolutionUtil.logger.log(Level.FINEST, "=================================================================");
            SolutionUtil.logger.log(Level.FINEST, "============= Adding SDP INV Data post QUEUE=====================");
            SolutionUtil.logger.log(Level.FINEST, "=================================================================");
            final boolean bServiceDeskEnabled = getInstance().isIntegrationMode();
            final boolean bServiceDeskAssetEnabled = getInstance().isInvIntegrationMode();
            final boolean bServiceDeskMDMAssetEnabled = getInstance().isMDMInvIntegrationMode();
            final boolean bserviceDeskODEnabled = getInstance().isSDPODIntegrationMode();
            final boolean bAssetExplorerEnabled = getInstance().isAEIntegrationMode();
            final boolean bServiceDeskODAssetEnabled = getInstance().isInvIntegrationMode();
            SolutionUtil.logger.log(Level.FINEST, "ServiceDeskEnabled : " + bServiceDeskEnabled);
            SolutionUtil.logger.log(Level.FINEST, "ServiceDeskAssetEnabled : " + bServiceDeskAssetEnabled);
            SolutionUtil.logger.log(Level.FINEST, "ServiceDesk Plus On-Demand Enabled : " + bserviceDeskODEnabled);
            if (bserviceDeskODEnabled && bServiceDeskODAssetEnabled && queueDataType != 18 && queueDataType != 19 && queueDataType != 20) {
                dcQData.queueDataType = queueDataType;
                DCQueueHandler.addToQueue("sdp-inv-data", dcQData);
                SolutionUtil.logger.log(Level.FINEST, "SDPOD Inventory added to Queue... ");
            }
            if ((bServiceDeskEnabled || bAssetExplorerEnabled) && bServiceDeskAssetEnabled && queueDataType != 18 && queueDataType != 19 && queueDataType != 20) {
                dcQData.queueDataType = queueDataType;
                DCQueueHandler.addToQueue("sdp-inv-data", dcQData);
                SolutionUtil.logger.log(Level.FINEST, "SDP Inventory added to Queue... ");
            }
            if (((bServiceDeskEnabled || bAssetExplorerEnabled) && bServiceDeskMDMAssetEnabled && queueDataType == 18) || queueDataType == 19 || queueDataType == 20) {
                dcQData.queueDataType = queueDataType;
                DCQueueHandler.addToQueue("sdp-inv-data", dcQData);
                SolutionUtil.logger.log(Level.FINEST, "SDP MDM Inventory added to Queue... ");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getBuildNumberFromSDP() throws Exception {
        String sdpBuildNumber = null;
        if (getInstance().isIntegrationMode()) {
            String baseURL = SDPNotificationUtil.getServiceDeskBaseURL();
            final Boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (!isMSP) {
                baseURL += "/api/v3/app_resources/build_info";
                final String authenticationKey = getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
                final URL url = new URL(baseURL);
                final URLConnection uc = SDPNotificationUtil.getInstance().createSDPURLConnection(url, "text/xml; charset=\"UTF-8\"", true, true, false);
                if (authenticationKey != null) {
                    uc.setRequestProperty("AUTHTOKEN", Encoder.convertFromBase(authenticationKey));
                }
                final BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                final StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                final String responseFromSDP = sb.toString();
                try {
                    if (responseFromSDP != null && responseFromSDP.contains("response_status")) {
                        final JSONObject responseJSON = new JSONObject(responseFromSDP);
                        final JSONObject statusJSON = responseJSON.getJSONObject("response_status");
                        final Long sdpStatus = statusJSON.getLong("status_code");
                        if (sdpStatus != null && sdpStatus == 2000L) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = responseJSON.getJSONObject("result");
                            sdpBuildNumber = String.valueOf(jsonObject.getLong("build_number"));
                            SolutionUtil.logger.log(Level.INFO, "getBuildNumberFromSDP() {0} sdpBuildNumber : {1} ", sdpBuildNumber);
                        }
                        SolutionUtil.logger.log(Level.INFO, " Response status from sdp : " + sdpStatus);
                    }
                }
                catch (final Exception e) {
                    SolutionUtil.logger.log(Level.WARNING, "getBuildNumberFromSDP Exception e : ", e);
                }
            }
            if (sdpBuildNumber == null) {
                baseURL = SDPNotificationUtil.getServiceDeskBaseURL() + "/servlets/DCPluginServlet?action=getVersionDetails";
                String authKey = getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
                authKey = Encoder.convertFromBase(authKey);
                if (isMSP) {
                    baseURL = baseURL + "&KEY=" + authKey;
                }
                final Map resultMap = this.getValidationResultMap(baseURL);
                sdpBuildNumber = resultMap.get("buildnumber");
                SolutionUtil.logger.log(Level.INFO, "getBuildNumberFromSDP() {0} sdpBuildNumber : {1} ", new Object[] { resultMap, sdpBuildNumber });
            }
            sdpBuildNumber = ((sdpBuildNumber == null || sdpBuildNumber.equalsIgnoreCase("Invalid API Key")) ? null : sdpBuildNumber);
            if (sdpBuildNumber != null) {
                final String sdpBuildNumberDB = getInstance().getIntegrationParamsValue("SDP_BUILD_NUMBER");
                if (sdpBuildNumberDB == null || sdpBuildNumberDB.equalsIgnoreCase("Invalid API Key") || (sdpBuildNumber != null && !sdpBuildNumberDB.equalsIgnoreCase(sdpBuildNumber))) {
                    getInstance().updateIntegrationParameter("SDP_BUILD_NUMBER", sdpBuildNumber);
                }
            }
        }
        return sdpBuildNumber;
    }
    
    public String getSDPBuildNumber() throws Exception {
        final String sdpBuildNumberCache = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("SDP_BUILD_NUMBER");
        SolutionUtil.logger.log(Level.WARNING, "sdpBuildNumberCache : {0} ", new Object[] { sdpBuildNumberCache });
        String sdpBuildNumberDB = getInstance().getIntegrationParamsValue("SDP_BUILD_NUMBER");
        if (sdpBuildNumberCache == null || sdpBuildNumberCache.equalsIgnoreCase("Invalid API Key")) {
            sdpBuildNumberDB = getInstance().getBuildNumberFromSDP();
        }
        return sdpBuildNumberDB;
    }
    
    public boolean isInvIntegrationMode() {
        return this.getIntegrationParams("SDP_INV_INTEGRATION");
    }
    
    public boolean isMDMInvIntegrationMode() {
        return this.getIntegrationParams("SDP_MDM_INV_INTEGRATION");
    }
    
    public boolean getIntegrationParams(final String paramName) {
        String isShowDCMenu = this.getIntegrationParamsValue(paramName);
        if (isShowDCMenu == null) {
            SolutionUtil.logger.log(Level.WARNING, "*** getIntegrationParams()  param Name " + paramName + "  Value  +" + isShowDCMenu);
            isShowDCMenu = "false";
            final ArrayList tableNames = new ArrayList();
            tableNames.add("IntegrationParams");
            ApiFactoryProvider.getCacheAccessAPI().putCache(paramName, (Object)isShowDCMenu, (List)tableNames);
        }
        return Boolean.valueOf(isShowDCMenu);
    }
    
    public String getIntegrationParamsValue(final String paramName) {
        String isShowDCMenu = null;
        try {
            try {
                isShowDCMenu = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(paramName);
            }
            catch (final Exception e) {
                SolutionUtil.logger.log(Level.SEVERE, "Error while getting data from  cachemanager : " + e.getMessage());
            }
            if (isShowDCMenu == null) {
                final Criteria crit = new Criteria(Column.getColumn("IntegrationParams", "PARAM_NAME"), (Object)paramName, 0);
                final DataObject formatDO = SyMUtil.getPersistence().get("IntegrationParams", crit);
                final Row formatRow = formatDO.getRow("IntegrationParams");
                if (formatRow != null) {
                    isShowDCMenu = (String)formatRow.get("PARAM_VALUE");
                }
                final ArrayList tableNames = new ArrayList();
                tableNames.add("IntegrationParams");
                ApiFactoryProvider.getCacheAccessAPI().putCache(paramName, (Object)isShowDCMenu, (List)tableNames);
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error Message : " + e.getMessage());
        }
        return isShowDCMenu;
    }
    
    public Map getValidationResultMap(final String urlStr) {
        final BufferedInputStream is = null;
        final ByteArrayOutputStream bos = null;
        final Map resultMap = new HashMap();
        try {
            final URL url = new URL(urlStr);
            final URLConnection uc = SDPNotificationUtil.getInstance().createSDPURLConnection(url, "text/xml; charset=\"UTF-8\"", true, true, false);
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
            ex.printStackTrace();
            SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Exception while creating URL connection to SDP:", ex);
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
    
    public boolean isIframeIntegrationModeEnabled() {
        final Iterator itr = SolutionUtil.pluginParams.keySet().iterator();
        boolean isIntegMode = false;
        while (itr.hasNext()) {
            final String integParam = SolutionUtil.pluginParams.get(itr.next());
            isIntegMode = this.getIntegrationParams(integParam);
            if (isIntegMode) {
                break;
            }
        }
        return isIntegMode;
    }
    
    public String getProductAppName(final String productCode) {
        final String appname = "HelpDesk";
        return appname;
    }
    
    public boolean getLeftTreeOption(final String appName, final boolean isReinit) {
        Boolean isShowLeftMenu = (Boolean)CacheManager.getCacheRepository().getFromCache((Object)(appName + "_IS_LEFT_TREE_ENABLE"));
        if (!isReinit) {
            if (isShowLeftMenu != null) {
                return isShowLeftMenu;
            }
        }
        try {
            final Properties props = this.getServerSettings(appName);
            if (props != null && props.get("IS_LEFT_TREE_ENABLE") != null) {
                isShowLeftMenu = ((Hashtable<K, Boolean>)props).get("IS_LEFT_TREE_ENABLE");
            }
            else {
                isShowLeftMenu = Boolean.TRUE;
            }
            final ArrayList tableNames = new ArrayList();
            tableNames.add("ApplnServerSettings");
            CacheManager.getCacheRepository().addToCache((Object)(appName + "_IS_LEFT_TREE_ENABLE"), (Object)isShowLeftMenu, (List)tableNames);
        }
        catch (final Exception ex) {
            SolutionUtil.logger.log(Level.WARNING, SolutionUtil.className + "Caught exception while getting SDP Enabled flag", ex);
            isShowLeftMenu = Boolean.TRUE;
        }
        return isShowLeftMenu;
    }
    
    public String getsdpProtocol(final String url) {
        return url;
    }
    
    public HttpURLConnection setAuthDetailsForSDPConnection(final HttpURLConnection urlConnection) {
        return null;
    }
    
    public boolean showorhideSDPODSettings() {
        return true;
    }
    
    public void deleteHiddenUser(final Long loginID, final Long appID) {
        try {
            Criteria criteria = new Criteria(new Column("IntegratedApplicationUsers", "APPLICATION_ID"), (Object)appID, 0);
            criteria = criteria.and(new Criteria(new Column("IntegratedApplicationUsers", "LOGIN_ID"), (Object)loginID, 0));
            criteria = criteria.and(new Criteria(new Column("IntegratedApplicationUsers", "STATUS"), (Object)1, 0));
            com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Exception while deleting the user in integratedservice", e);
        }
    }
    
    public void addIntegratedServiceUser(final Properties props) {
        try {
            Criteria criteria = new Criteria(new Column("IntegratedApplicationUsers", "APPLICATION_ID"), (Object)props.getProperty("applicationID"), 0);
            criteria = criteria.and(new Criteria(new Column("IntegratedApplicationUsers", "LOGIN_ID"), (Object)props.getProperty("loginID"), 0));
            final DataObject existingDO = DataAccess.get("IntegratedApplicationUsers", criteria);
            final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
            if (existingDO.isEmpty()) {
                final Row row = new Row("IntegratedApplicationUsers");
                row.set("APPLICATION_ID", (Object)props.getProperty("applicationID"));
                row.set("LOGIN_ID", (Object)props.getProperty("loginID"));
                row.set("STATUS", (Object)Long.parseLong(props.getProperty("status")));
                row.set("HD_ROLE", (Object)String.valueOf(props.getProperty("role")));
                final DataObject integratedUserDO = DataAccess.constructDataObject();
                integratedUserDO.addRow(row);
                DataAccess.update(integratedUserDO);
                jsonObject.put((Object)"REMARK", (Object)"Integrated Application user has been created");
                SecurityOneLineLogger.log("DC_Integration", "DC_Integration_User", jsonObject, Level.INFO);
            }
            else {
                String role = props.getProperty("role");
                final Row row2 = existingDO.getFirstRow("IntegratedApplicationUsers");
                row2.set("STATUS", (Object)props.getProperty("status"));
                if (role == null && row2.get("HD_ROLE") != null) {
                    role = String.valueOf(row2.get("HD_ROLE"));
                }
                row2.set("HD_ROLE", (Object)String.valueOf(role));
                existingDO.updateRow(row2);
                SyMUtil.getPersistence().update(existingDO);
                jsonObject.put((Object)"REMARK", (Object)"Integrated Application user has been updated");
                SecurityOneLineLogger.log("DC_Integration", "DC_Integration_User", jsonObject, Level.INFO);
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error occurred in addintegratedServiceUser()", e);
        }
    }
    
    public long getIntegratedApplicationId(final String appName) {
        Long appID = -1L;
        try {
            final DataObject existingDO = DataAccess.get("IntegrationApplications", new Criteria(Column.getColumn("IntegrationApplications", "APPLICATIONNAME"), (Object)appName, 0, false));
            if (!existingDO.isEmpty()) {
                final Row row = existingDO.getFirstRow("IntegrationApplications");
                if (Integer.valueOf(String.valueOf(row.get("STATUS"))) == 1) {
                    appID = Long.parseLong(row.get("APPLICATION_ID").toString());
                }
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error occurred while getting integrated application id");
        }
        return appID;
    }
    
    public void deleteIntegratedUsers(final Long appID) {
        try {
            Criteria criteria = new Criteria(new Column("IntegratedApplicationUsers", "APPLICATION_ID"), (Object)appID, 0);
            final Criteria criteria2 = new Criteria(new Column("IntegratedApplicationUsers", "STATUS"), (Object)1, 0);
            criteria = criteria2.and(criteria);
            com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error Occurred while deleting integrated users");
        }
    }
    
    public void createIntegrationApplication(final String applicationName, final int status) {
        try {
            final DataObject existingDO = DataAccess.get("IntegrationApplications", new Criteria(Column.getColumn("IntegrationApplications", "APPLICATIONNAME"), (Object)applicationName, 0, false));
            if (existingDO.isEmpty()) {
                final Row row = new Row("IntegrationApplications");
                row.set("APPLICATIONNAME", (Object)applicationName);
                row.set("STATUS", (Object)1);
                final DataObject integrationDO = DataAccess.constructDataObject();
                integrationDO.addRow(row);
                DataAccess.update(integrationDO);
            }
            else {
                final Row row = existingDO.getFirstRow("IntegrationApplications");
                row.set("STATUS", (Object)status);
                existingDO.updateRow(row);
                DataAccess.update(existingDO);
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error occurred in createIntegrationApplications()", e);
        }
    }
    
    public List getDisabledUsers(final String appName) {
        final List<Long> userList = new ArrayList<Long>();
        try {
            final Long appId = this.getIntegratedApplicationId(appName);
            Criteria criteria = new Criteria(new Column("IntegratedApplicationUsers", "APPLICATION_ID"), (Object)appId, 0);
            criteria = criteria.and(new Criteria(new Column("IntegratedApplicationUsers", "STATUS"), (Object)1, 0));
            final DataObject existingDO = DataAccess.get("IntegratedApplicationUsers", criteria);
            if (!existingDO.isEmpty()) {
                final Iterator userItr = existingDO.getRows("IntegratedApplicationUsers");
                while (userItr.hasNext()) {
                    final Row row = userItr.next();
                    final Long userID = Long.parseLong(row.get("LOGIN_ID").toString());
                    userList.add(userID);
                }
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error occurred in getting hidden Users for Role Change", e);
        }
        return userList;
    }
    
    public long getSDPUserStatus(final Long loginID, final String appName) {
        Long status = -1L;
        try {
            final Long appId = this.getIntegratedApplicationId(appName);
            Criteria criteria = new Criteria(new Column("IntegratedApplicationUsers", "APPLICATION_ID"), (Object)appId, 0);
            criteria = criteria.and(new Criteria(new Column("IntegratedApplicationUsers", "LOGIN_ID"), (Object)loginID, 0));
            final DataObject existingDO = DataAccess.get("IntegratedApplicationUsers", criteria);
            if (!existingDO.isEmpty()) {
                final Row row = existingDO.getFirstRow("IntegratedApplicationUsers");
                if (row.get("STATUS") != null) {
                    status = Long.parseLong(row.get("STATUS").toString());
                }
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error occurred while getting SDPUserStatus ", e);
        }
        return status;
    }
    
    public String getSDPUserRole(final String appName, final Long loginID) {
        String role = null;
        try {
            final Long appId = getInstance().getIntegratedApplicationId(appName);
            Criteria criteria = new Criteria(new Column("IntegratedApplicationUsers", "APPLICATION_ID"), (Object)appId, 0);
            criteria = criteria.and(new Criteria(new Column("IntegratedApplicationUsers", "LOGIN_ID"), (Object)loginID, 0));
            final DataObject existingDO = DataAccess.get("IntegratedApplicationUsers", criteria);
            if (!existingDO.isEmpty()) {
                final Row row = existingDO.getFirstRow("IntegratedApplicationUsers");
                if (row.get("HD_ROLE") != null) {
                    role = row.get("HD_ROLE").toString();
                }
            }
        }
        catch (final Exception e) {
            SolutionUtil.logger.log(Level.SEVERE, "Error occurred while getting SDPUserRole ", e);
        }
        return role;
    }
    
    static {
        SolutionUtil.logger = Logger.getLogger("SDPIntegrationLog");
        SolutionUtil.sdpodlogger = Logger.getLogger("SDPODIntegrationLog");
        SolutionUtil.className = "SolutionUtilIns | ";
        SolutionUtil.solutionUtil = null;
        SolutionUtil.isIntegrationMode = Boolean.FALSE;
        SolutionUtil.appServerProp = null;
        SolutionUtil.icConfigListenerRegistered = false;
        SolutionUtil.pluginParams = null;
    }
}
