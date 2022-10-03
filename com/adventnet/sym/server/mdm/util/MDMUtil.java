package com.adventnet.sym.server.mdm.util;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.inv.ProcessorType;
import java.text.SimpleDateFormat;
import com.me.mdm.server.security.MDMBaseSecurityUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import org.apache.commons.io.IOUtils;
import com.me.mdm.api.error.APIHTTPException;
import java.net.URISyntaxException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Collection;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import org.json.simple.parser.JSONParser;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import com.me.mdm.uem.queue.ModernMgmtOperationData;
import com.me.mdm.uem.queue.ModernMgmtQueueOperation;
import com.me.mdm.uem.queue.ModernMgmtContactTimeData;
import com.me.mdm.uem.ModernDeviceUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import org.json.JSONException;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashSet;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Map;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.GroupByClause;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.io.IOException;
import java.io.Reader;
import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import java.util.Date;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.InetAddress;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import java.util.Set;
import java.util.logging.Logger;
import com.me.idps.core.util.IdpsUtil;

public class MDMUtil extends IdpsUtil
{
    public static Logger logger;
    public static Logger mdmEnrollmentLogger;
    private static MDMUtil mdmUtil;
    public static final String MDM_DOMAIN_NAME = "MDM";
    private static final String MDM_APPLICATION_CONF = "mdmApplication.conf";
    private static final String MDM_VIEW_BACKUP_PARAMS = "mdmViewBackupParams.properties";
    private static final String MDM_SECURE_KEYS_FOR_OP_CONF = "mdmSecureKeysForOP.conf";
    private static ThreadLocal<Set<Long>> appliedCommandList;
    public static final int MDM_ROLE = 1;
    public static final int MODERN_MANAGEMENT_ROLE = 2;
    public static final String NULL_VALUE = "NULL_VALUE";
    
    public static MDMUtil getInstance() {
        if (MDMUtil.mdmUtil == null) {
            MDMUtil.mdmUtil = new MDMUtil();
        }
        return MDMUtil.mdmUtil;
    }
    
    public static void addOrUpdateMDMServerInfo() {
        try {
            MDMUtil.logger.log(Level.INFO, "############## addOrUpdateMDMServerInfo S T A R T############");
            final DataObject serverInfoDO = getPersistence().get("MDMServerInfo", (Criteria)null);
            final String androidAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagentversion");
            final String androidAgent2 = MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagent23version");
            final String windowsAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("windowsagentversion");
            final String safeAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("safeagentversion");
            final String knoxAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("knoxagentversion");
            final String iosAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("iosagentversion");
            final String macosAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentversion");
            final String adminAgent = MDMAgentBuildVersionsUtil.getMDMAgentInfo("adminagentversion");
            final String androidAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagentversioncode");
            final String androidAgentVersion2 = MDMAgentBuildVersionsUtil.getMDMAgentInfo("androidagent23versioncode");
            final String safeAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("safeagentversioncode");
            final String knoxAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("knoxagentversioncode");
            final String adminAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("adminagentversioncode");
            final String iosAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("iosagentversioncode");
            final String macosAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentversioncode");
            final String windowsAgentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("windowsagentversioncode");
            final String mandatoryIosCode = MDMAgentBuildVersionsUtil.getMDMAgentInfo("mandatoryiosagentversioncode");
            final String mandatoryMacCode = MDMAgentBuildVersionsUtil.getMDMAgentInfo("mandatorymacosagentversioncode");
            final String mandatoryAndroidCode = MDMAgentBuildVersionsUtil.getMDMAgentInfo("mandatoryandroidagentversioncode");
            final String mandatoryAndroidCode2 = MDMAgentBuildVersionsUtil.getMDMAgentInfo("mandatoryandroidagent23versioncode");
            final String mandatorySafeCode = MDMAgentBuildVersionsUtil.getMDMAgentInfo("mandatorysafeagentversioncode");
            final String mandatoryWindowsCode = MDMAgentBuildVersionsUtil.getMDMAgentInfo("windowsagentmandatoryversioncode");
            final String mandatoryAndroidAdminCode = MDMAgentBuildVersionsUtil.getMDMAgentInfo("adminagentmandatoryversioncode");
            String serverName = InetAddress.getLocalHost().getHostName();
            Integer portNumber = new Integer(ApiFactoryProvider.getUtilAccessAPI().getWebServerPort());
            final Properties natInfo = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            if (!natInfo.isEmpty()) {
                serverName = natInfo.getProperty("NAT_ADDRESS");
                portNumber = ((Hashtable<K, Integer>)natInfo).get("NAT_HTTPS_PORT");
            }
            if (serverInfoDO.isEmpty()) {
                final Row serverResourceRow = new Row("MDMServerInfo");
                serverResourceRow.set("MDM_SERVER_NAME", (Object)serverName);
                serverResourceRow.set("HTTPS_PORT", (Object)portNumber);
                serverResourceRow.set("ANDROID_AGENT_VERSION", (Object)androidAgent);
                serverResourceRow.set("ANDROID_AGENT_23_VERSION", (Object)androidAgent2);
                serverResourceRow.set("WINDOWS_AGENT_VERSION", (Object)windowsAgent);
                serverResourceRow.set("SAFE_AGENT_VERSION", (Object)safeAgent);
                serverResourceRow.set("KNOX_AGENT_VERSION", (Object)knoxAgent);
                serverResourceRow.set("IOS_AGENT_VERSION", (Object)iosAgent);
                serverResourceRow.set("MAC_AGENT_VERSION", (Object)macosAgent);
                serverResourceRow.set("ANDROID_ADMIN_VERSION", (Object)adminAgent);
                serverResourceRow.set("WINDOWS_AGENT_VERSION_CODE", (Object)windowsAgentVersion);
                serverResourceRow.set("ANDROID_AGENT_VERSION_CODE", (Object)androidAgentVersion);
                serverResourceRow.set("ANDROID_AGENT_23_VERSION_CODE", (Object)androidAgentVersion2);
                serverResourceRow.set("SAFE_AGENT_VERSION_CODE", (Object)safeAgentVersion);
                serverResourceRow.set("KNOX_AGENT_VERSION_CODE", (Object)knoxAgentVersion);
                serverResourceRow.set("IOS_AGENT_VERSION_CODE", (Object)iosAgentVersion);
                serverResourceRow.set("MAC_AGENT_VERSION_CODE", (Object)macosAgentVersion);
                serverResourceRow.set("ANDROID_ADMIN_VERSION_CODE", (Object)Long.parseLong(adminAgentVersion));
                serverResourceRow.set("MANDATORY_IOS_CODE", (Object)(mandatoryIosCode.equals("") ? -1L : Long.parseLong(mandatoryIosCode)));
                serverResourceRow.set("MANDATORY_MAC_CODE", (Object)(mandatoryMacCode.equals("") ? -1L : Long.parseLong(mandatoryMacCode)));
                serverResourceRow.set("MANDATORY_ANDROID_CODE", (Object)(mandatoryAndroidCode.equals("") ? -1L : Long.parseLong(mandatoryAndroidCode)));
                serverResourceRow.set("MANDATORY_ANDROID_23_CODE", (Object)(mandatoryAndroidCode2.equals("") ? -1L : Long.parseLong(mandatoryAndroidCode)));
                serverResourceRow.set("MANDATORY_SAFE_CODE", (Object)(mandatorySafeCode.equals("") ? -1L : Long.parseLong(mandatorySafeCode)));
                serverResourceRow.set("MANDATORY_WINDOWS_CODE", (Object)(mandatoryWindowsCode.equals("") ? -1L : Long.parseLong(mandatoryWindowsCode)));
                serverResourceRow.set("MANDATORY_ADMIN_CODE", (Object)(mandatoryAndroidAdminCode.equals("") ? -1L : Long.parseLong(mandatoryAndroidAdminCode)));
                serverInfoDO.addRow(serverResourceRow);
                getPersistence().add(serverInfoDO);
            }
            else {
                final Row serverResourceRow = serverInfoDO.getRow("MDMServerInfo");
                serverResourceRow.set("MDM_SERVER_NAME", (Object)serverName);
                serverResourceRow.set("HTTPS_PORT", (Object)portNumber);
                serverResourceRow.set("ANDROID_AGENT_VERSION", (Object)androidAgent);
                serverResourceRow.set("ANDROID_AGENT_23_VERSION", (Object)androidAgent2);
                serverResourceRow.set("WINDOWS_AGENT_VERSION", (Object)windowsAgent);
                serverResourceRow.set("SAFE_AGENT_VERSION", (Object)safeAgent);
                serverResourceRow.set("KNOX_AGENT_VERSION", (Object)knoxAgent);
                serverResourceRow.set("IOS_AGENT_VERSION", (Object)iosAgent);
                serverResourceRow.set("MAC_AGENT_VERSION", (Object)macosAgent);
                serverResourceRow.set("ANDROID_ADMIN_VERSION", (Object)adminAgent);
                serverResourceRow.set("WINDOWS_AGENT_VERSION_CODE", (Object)Long.parseLong(windowsAgentVersion));
                serverResourceRow.set("ANDROID_AGENT_VERSION_CODE", (Object)Long.parseLong(androidAgentVersion));
                serverResourceRow.set("ANDROID_AGENT_23_VERSION_CODE", (Object)androidAgentVersion2);
                serverResourceRow.set("SAFE_AGENT_VERSION_CODE", (Object)Long.parseLong(safeAgentVersion));
                serverResourceRow.set("KNOX_AGENT_VERSION_CODE", (Object)Long.parseLong(knoxAgentVersion));
                serverResourceRow.set("IOS_AGENT_VERSION_CODE", (Object)Long.parseLong(iosAgentVersion));
                serverResourceRow.set("MAC_AGENT_VERSION_CODE", (Object)Long.parseLong(macosAgentVersion));
                serverResourceRow.set("ANDROID_ADMIN_VERSION_CODE", (Object)Long.parseLong(adminAgentVersion));
                serverResourceRow.set("MANDATORY_IOS_CODE", (Object)(mandatoryIosCode.equals("") ? -1L : Long.parseLong(mandatoryIosCode)));
                serverResourceRow.set("MANDATORY_MAC_CODE", (Object)(mandatoryMacCode.equals("") ? -1L : Long.parseLong(mandatoryMacCode)));
                serverResourceRow.set("MANDATORY_ANDROID_CODE", (Object)(mandatoryAndroidCode.equals("") ? -1L : Long.parseLong(mandatoryAndroidCode)));
                serverResourceRow.set("MANDATORY_ANDROID_23_CODE", (Object)(mandatoryAndroidCode2.equals("") ? -1L : Long.parseLong(mandatoryAndroidCode)));
                serverResourceRow.set("MANDATORY_SAFE_CODE", (Object)(mandatorySafeCode.equals("") ? -1L : Long.parseLong(mandatorySafeCode)));
                serverResourceRow.set("MANDATORY_WINDOWS_CODE", (Object)(mandatoryWindowsCode.equals("") ? -1L : Long.parseLong(mandatoryWindowsCode)));
                serverResourceRow.set("MANDATORY_ADMIN_CODE", (Object)(mandatoryAndroidAdminCode.equals("") ? -1L : Long.parseLong(mandatoryAndroidAdminCode)));
                serverInfoDO.updateRow(serverResourceRow);
                getPersistence().update(serverInfoDO);
            }
            MDMUtil.logger.log(Level.INFO, "############## addOrUpdateMDMServerInfo E N D############");
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occured while adding/updating the MDMServerInfo table", e);
        }
    }
    
    public Properties getPropertiesFromconf(final String fileName) throws Exception {
        FileInputStream inputStream = null;
        final Properties confUrls = new Properties();
        try {
            final String confDir = System.getProperty("server.home") + File.separator + "conf";
            final File confContent = new File(confDir + File.separator + fileName);
            inputStream = new FileInputStream(confContent.getCanonicalPath());
            confUrls.load(inputStream);
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final Exception ex2) {
                    MDMUtil.logger.log(Level.SEVERE, "exception in closing inputstream", ex2);
                }
            }
        }
        return confUrls;
    }
    
    public Properties getMDMApplicationProperties() throws Exception {
        return this.getPropertiesFromconf("mdmApplication.conf");
    }
    
    public Properties getMDMViewBackupParamsProperties() throws Exception {
        return this.getPropertiesFromconf("mdmViewBackupParams.properties");
    }
    
    public Properties getMDMSecureKeysForOPProperties() throws Exception {
        return this.getPropertiesFromconf("mdmSecureKeysForOP.conf");
    }
    
    public static Properties getMDMServerInfo() {
        Properties props = null;
        try {
            final DataObject resultDO = SyMUtil.getPersistence().get("MDMServerInfo", (Criteria)null);
            if (!resultDO.isEmpty()) {
                props = new Properties();
                ((Hashtable<String, Object>)props).put("MDM_SERVER_NAME", resultDO.getFirstValue("MDMServerInfo", "MDM_SERVER_NAME"));
                ((Hashtable<String, Object>)props).put("HTTPS_PORT", resultDO.getFirstValue("MDMServerInfo", "HTTPS_PORT"));
                ((Hashtable<String, Object>)props).put("ANDROID_AGENT_VERSION", resultDO.getFirstValue("MDMServerInfo", "ANDROID_AGENT_VERSION"));
                ((Hashtable<String, Object>)props).put("ANDROID_AGENT_23_VERSION", resultDO.getFirstValue("MDMServerInfo", "ANDROID_AGENT_23_VERSION"));
                ((Hashtable<String, Object>)props).put("SAFE_AGENT_VERSION", resultDO.getFirstValue("MDMServerInfo", "SAFE_AGENT_VERSION"));
                ((Hashtable<String, Object>)props).put("KNOX_AGENT_VERSION", resultDO.getFirstValue("MDMServerInfo", "KNOX_AGENT_VERSION"));
                ((Hashtable<String, Object>)props).put("IOS_AGENT_VERSION", resultDO.getFirstValue("MDMServerInfo", "IOS_AGENT_VERSION"));
                ((Hashtable<String, Object>)props).put("MAC_AGENT_VERSION", resultDO.getFirstValue("MDMServerInfo", "MAC_AGENT_VERSION"));
                ((Hashtable<String, Object>)props).put("WINDOWS_AGENT_VERSION", resultDO.getFirstValue("MDMServerInfo", "WINDOWS_AGENT_VERSION"));
                ((Hashtable<String, Object>)props).put("ANDROID_ADMIN_VERSION", resultDO.getFirstValue("MDMServerInfo", "ANDROID_ADMIN_VERSION"));
                ((Hashtable<String, Object>)props).put("WINDOWS_AGENT_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "WINDOWS_AGENT_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("ANDROID_AGENT_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "ANDROID_AGENT_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("ANDROID_AGENT_23_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "ANDROID_AGENT_23_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("SAFE_AGENT_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "SAFE_AGENT_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("KNOX_AGENT_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "KNOX_AGENT_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("ANDROID_ADMIN_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "ANDROID_ADMIN_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("IOS_AGENT_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "IOS_AGENT_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("MAC_AGENT_VERSION_CODE", resultDO.getFirstValue("MDMServerInfo", "MAC_AGENT_VERSION_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_IOS_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_IOS_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_MAC_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_MAC_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_ANDROID_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_ANDROID_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_ANDROID_23_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_ANDROID_23_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_SAFE_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_SAFE_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_WINDOWS_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_WINDOWS_CODE"));
                ((Hashtable<String, Object>)props).put("MANDATORY_ADMIN_CODE", resultDO.getFirstValue("MDMServerInfo", "MANDATORY_ADMIN_CODE"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Caught exception while getting DCServerInfo details. ", ex);
        }
        return props;
    }
    
    public int getInactiveDeviceCountByPeriod(int noOfDays, final Long customerID) throws Exception {
        int inactiveCount = 0;
        if (noOfDays != 0) {
            final Calendar cal = Calendar.getInstance();
            noOfDays *= -1;
            final Date today = new Date();
            cal.setTime(today);
            cal.add(5, noOfDays);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            final long filter = cal.getTime().getTime();
            final Criteria periodCrit = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long(filter), 7);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AgentContact"));
            query.addJoin(new Join("AgentContact", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria managedDevice = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Long(2L), 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            query.addSelectColumn(new Column((String)null, "*").count());
            query.setCriteria(periodCrit.and(managedDevice).and(customerCriteria));
            inactiveCount = DBUtil.getRecordCount(query);
        }
        return inactiveCount;
    }
    
    public List getStringList(final String strValue, final String separator) {
        final List valueList = new ArrayList();
        final StringTokenizer strTok = new StringTokenizer(strValue, separator);
        while (strTok.hasMoreTokens()) {
            valueList.add(strTok.nextToken().trim());
        }
        return valueList;
    }
    
    public String readRequest(final HttpServletRequest request) throws IOException, Exception {
        try {
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            try {
                final Reader reader = getInstance().getProperEncodedReader(request, null);
                while ((read = reader.read(chBuf)) > -1) {
                    strBuilder.append(chBuf, 0, read);
                }
            }
            catch (final IOException ex) {
                MDMUtil.logger.log(Level.WARNING, "IOSServerServlet => IOException occured while reading request : {0}", ex);
                throw ex;
            }
            return strBuilder.toString();
        }
        catch (final Exception ex2) {
            MDMUtil.logger.log(Level.WARNING, "IOSServerServlet => Exception occured while reading request : {0}", ex2);
            throw ex2;
        }
    }
    
    public String getUDIDFromResourceID(final Long resourceID) {
        String deviceUDID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObj = getPersistence().get("ManagedDevice", criteria);
            if (!dataObj.isEmpty()) {
                deviceUDID = (String)dataObj.getFirstValue("ManagedDevice", "UDID");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return deviceUDID;
    }
    
    public Long getResourceIDFromUDID(final String strUDID) {
        Long resourceID = null;
        MDMUtil.logger.log(Level.INFO, "Inside getResourceIDFromUDID(){0}", strUDID);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)strUDID, 0);
            final DataObject dataObj = getPersistence().get("ManagedDevice", criteria);
            MDMUtil.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data: dataObj: {0}", dataObj);
            if (!dataObj.isEmpty()) {
                resourceID = (Long)dataObj.getFirstValue("ManagedDevice", "RESOURCE_ID");
                MDMUtil.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data: resourceID: {0}", resourceID);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occurred in getResourceIDFromUDID(){0}", ex);
        }
        MDMUtil.logger.log(Level.FINE, "getResourceIDFromUDID() -> returning resourceID {0}", resourceID);
        return resourceID;
    }
    
    public int getEnrollmentTypeForResourceId(final Long resourceId) {
        Integer enrollType = 0;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            query.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            query.addSelectColumn(new Column("DeviceEnrollmentRequest", "*"));
            final Criteria managedDevice = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            query.setCriteria(managedDevice);
            MDMUtil.logger.log(Level.INFO, "getEnrollmentTypeQuery {0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
            final DataObject dobj = getPersistence().get(query);
            MDMUtil.logger.log(Level.INFO, "Result DO {0}", dobj);
            if (!dobj.isEmpty()) {
                enrollType = (Integer)dobj.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_TYPE");
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in getEnrollmentType", e);
        }
        return enrollType;
    }
    
    public Long getApplicableAppCollectionForResource(final Long appID, final Long resourceID) throws Exception {
        Long collectionID = (Long)DBUtil.getValueFromDB("MdAppToCollection", "APP_ID", (Object)appID, "COLLECTION_ID");
        if (collectionID == null) {
            MDMUtil.logger.log(Level.WARNING, "A app catalog request for app {0} on resource {1} came without entry in MDApptocollection ", new Object[] { appID, resourceID });
            final Long appgroupID = (Long)DBUtil.getValueFromDB("MdAppToGroupRel", "APP_ID", (Object)appID, "APP_GROUP_ID");
            if (appgroupID != null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
                selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                final Criteria resCritera = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
                final Criteria appGroupCritera = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appgroupID, 0);
                selectQuery.setCriteria(appGroupCritera.and(resCritera));
                final DataObject dataObject = getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getRow("RecentProfileForResource");
                    collectionID = (Long)row.get("COLLECTION_ID");
                    MDMUtil.logger.log(Level.WARNING, "collectionID Picked for app {0} on resource {1} is {2}", new Object[] { appID, resourceID, collectionID });
                }
            }
        }
        if (collectionID == null) {
            MDMUtil.logger.log(Level.WARNING, "failed to pick collectionID  for app {0} on resource {1} reverting to old flow", new Object[] { appID, resourceID });
            collectionID = this.getCollectionIDfromAppID(appID);
        }
        return collectionID;
    }
    
    public Long getCollectionIDfromAppID(final Long appId) {
        Long collectionId = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
            sq.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sq.setCriteria(new Criteria(new Column("MdAppToGroupRel", "APP_ID"), (Object)appId, 0));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = getPersistence().get(sq);
            if (!dO.isEmpty()) {
                collectionId = (Long)dO.getValue("MdAppToCollection", "COLLECTION_ID", new Criteria(new Column("MdAppToCollection", "APP_ID"), (Object)appId, 0));
                if (collectionId == null) {
                    return (Long)dO.getFirstRow("MdAppToCollection").get("COLLECTION_ID");
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return collectionId;
    }
    
    public Long getCollectionAppIDForStoreApp(final Long appId) {
        final Long pkgAppId = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
            sq.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sq.setCriteria(new Criteria(new Column("MdAppToGroupRel", "APP_ID"), (Object)appId, 0));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = getPersistence().get(sq);
            if (!dO.isEmpty()) {
                return (Long)dO.getFirstRow("MdPackageToAppData").get("APP_ID");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return pkgAppId;
    }
    
    public List getAppIDsFromCollectionID(final Long collectionID) throws DataAccessException {
        final List appIds = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
        final Criteria collnCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
        selectQuery.setCriteria(collnCriteria);
        final DataObject dataObject = getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("MdAppToCollection");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                appIds.add(row.get("APP_ID"));
            }
        }
        return appIds;
    }
    
    public static void updateUserParameters(final Long userId, final JSONObject params) throws SyMException {
        try {
            if (userId == null || params == null) {
                throw new SyMException(1002, "Any of given input is null", (Throwable)null);
            }
            Iterator iterator = params.keys();
            final ArrayList paramNameList = new ArrayList();
            while (iterator.hasNext()) {
                paramNameList.add(iterator.next());
            }
            final Column userIdCol = Column.getColumn("UserParams", "USER_ACCOUNT_ID");
            Criteria criteria = new Criteria(userIdCol, (Object)userId, 0);
            final Column paramNameCol = Column.getColumn("UserParams", "PARAM_NAME");
            final Criteria paramCri = new Criteria(paramNameCol, (Object)paramNameList.toArray(), 8, false);
            criteria = criteria.and(paramCri);
            final DataObject resultDO = getPersistence().get("UserParams", criteria);
            iterator = paramNameList.iterator();
            while (iterator.hasNext()) {
                final String paramName = iterator.next();
                final Row row = resultDO.getRow("UserParams", new Criteria(Column.getColumn("UserParams", "PARAM_NAME"), (Object)paramName, 0).and(criteria));
                if (row != null) {
                    row.set("PARAM_VALUE", params.get(paramName));
                    resultDO.updateRow(row);
                }
                else {
                    final Row userParamRow = new Row("UserParams");
                    userParamRow.set("USER_ACCOUNT_ID", (Object)userId);
                    userParamRow.set("PARAM_NAME", (Object)paramName);
                    userParamRow.set("PARAM_VALUE", params.get(paramName));
                    resultDO.addRow(userParamRow);
                }
            }
            getPersistence().update(resultDO);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, ex, () -> "Caught exception while updating User Parameter in DB:- user id: " + n + " param name: " + jsonObject);
            throw new SyMException(1002, (Throwable)ex);
        }
    }
    
    public static JSONObject getUserParameters(final Long userID, final String[] userParams) throws SyMException {
        try {
            if (userID == null || userParams == null) {
                throw new SyMException(1002, "Given input is null", (Throwable)null);
            }
            final Column userIdCol = Column.getColumn("UserParams", "USER_ACCOUNT_ID");
            Criteria criteria = new Criteria(userIdCol, (Object)userID, 0);
            final Column paramNameCol = Column.getColumn("UserParams", "PARAM_NAME");
            final Criteria paramCri = new Criteria(paramNameCol, (Object)userParams, 8, false);
            criteria = criteria.and(paramCri);
            final DataObject resultDO = getPersistence().get("UserParams", criteria);
            final JSONObject result = new JSONObject();
            final Iterator iterator = resultDO.getRows("UserParams");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                result.put((String)row.get("PARAM_NAME"), row.get("PARAM_VALUE"));
            }
            return result;
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, ex, () -> "Caught exception while retrieving User Parameter: " + array);
            throw new SyMException(1002, (Throwable)ex);
        }
    }
    
    @Deprecated
    public HashMap getProfiletoCollectionMap(final Long appId) {
        final HashMap profileCollectionMap = new HashMap();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
            sq.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sq.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            Criteria criteria = new Criteria(new Column("MdAppToGroupRel", "APP_ID"), (Object)appId, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appId, 0));
            sq.setCriteria(criteria);
            sq.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
            final DataObject dO = getPersistence().get(sq);
            if (!dO.isEmpty()) {
                final Row profileCollRow = dO.getFirstRow("ProfileToCollection");
                final Long profileId = (Long)profileCollRow.get("PROFILE_ID");
                final Long collectionId = (Long)profileCollRow.get("COLLECTION_ID");
                profileCollectionMap.put(profileId, collectionId);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return profileCollectionMap;
    }
    
    public Long getAppIDFromCollection(final Long collectionID) {
        Long appId = null;
        try {
            appId = (Long)DBUtil.getValueFromDB("MdAppToCollection", "COLLECTION_ID", (Object)collectionID, "APP_ID");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return appId;
    }
    
    public Long getAppIdAssociatedForResource(final Long appGrpID, final Long resourceID) {
        Long appID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
            final Criteria appGrpCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGrpID, 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0);
            selectQuery.setCriteria(resCriteria.and(appGrpCriteria));
            final DataObject dataObject = getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdAppCatalogToResource");
                appID = (Long)row.get("PUBLISHED_APP_ID");
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getting associated ID", ex);
        }
        return appID;
    }
    
    public Long getAppGroupIDFromCollection(final Long collectionID) {
        Long appGroupId = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
            sQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
            sQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
            final DataObject appDo = getPersistence().get(sQuery);
            if (appDo != null && !appDo.isEmpty()) {
                final Row groupRow = appDo.getFirstRow("MdAppToGroupRel");
                appGroupId = (Long)groupRow.get("APP_GROUP_ID");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return appGroupId;
    }
    
    private DataObject getIOSDeviceCommDetailsDO(final List resourceList) throws Exception {
        MDMUtil.logger.log(Level.FINE, "Inside getIOSDeviceCommDetailsDO(){0}", resourceList);
        final SelectQueryImpl query = new SelectQueryImpl(new Table("IOSDeviceCommDetails"));
        query.addSelectColumn(new Column("IOSDeviceCommDetails", "*"));
        final Criteria criteria = new Criteria(new Column("IOSDeviceCommDetails", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        query.setCriteria(criteria);
        final DataObject dataObject = getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public void wakeUpDeviceToInstallApp(final Long resId, final Long collectionId) {
        try {
            final String commandUUID = "InstallApplication;Collection=" + Long.toString(collectionId);
            DeviceCommandRepository.getInstance().assignCommandToDevice(commandUUID, resId);
            final List resourceList = new ArrayList();
            resourceList.add(resId);
            NotificationHandler.getInstance().SendNotification(resourceList, 1);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occurred in wakeUpDeviceToInstallApp() method : {0}", ex);
        }
    }
    
    public ArrayList updateDeviceScanStatus(final Criteria criteria, final Integer scanStatus) {
        final ArrayList arrUpdatedDeviceIds = new ArrayList();
        try {
            final DataObject dobj = getPersistence().get("MdDeviceScanStatus", criteria);
            if (!dobj.isEmpty()) {
                final Iterator rowIterator = dobj.getRows("MdDeviceScanStatus");
                Long resourceId = null;
                while (rowIterator.hasNext()) {
                    final Row row = rowIterator.next();
                    resourceId = (Long)row.get("RESOURCE_ID");
                    row.set("SCAN_STATUS", (Object)scanStatus);
                    row.set("REMARKS", (Object)"dc.wc.inv.common.SCAN_FAILED_INTERRUPTED");
                    dobj.updateRow(row);
                    arrUpdatedDeviceIds.add(resourceId);
                }
                getPersistence().update(dobj);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception while updating Device Scan Status : {0}", ex);
        }
        return arrUpdatedDeviceIds;
    }
    
    public void removeDeviceCommandFromCache(final List<Long> resList, final String commandName) {
        for (final Long resourceID : resList) {
            final String sUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
            DeviceCommandRepository.getInstance().removeDeviceCommand(sUDID, commandName, 1);
        }
    }
    
    public Criteria getSuccessfullyEnrolledCriteria() {
        final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        return cri;
    }
    
    public SelectQuery getMDMDeviceResourceQuery() {
        return this.getMDMDeviceResourceQuery(Boolean.TRUE);
    }
    
    public SelectQuery getMDMDeviceResourceQuery(final Boolean isUserJoinRequired) {
        SelectQuery deviceModelResourceQuery = null;
        try {
            deviceModelResourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join mdResJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join mdInfoResJoin = new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            final Join mdDeviceModelJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1);
            final Join agentContactJoin = new Join("Resource", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            final Join customDetailsJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            deviceModelResourceQuery.addJoin(mdResJoin);
            if (isUserJoinRequired) {
                final Join mdToManagedUserJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
                final Join muToResourceJoin = new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "ManagedUser", 2);
                deviceModelResourceQuery.addJoin(mdToManagedUserJoin);
                deviceModelResourceQuery.addJoin(muToResourceJoin);
                deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedUser", "RESOURCE_ID", "MANAGED_USER_ID"));
                deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedUser", "NAME", "ManagedUser"));
            }
            deviceModelResourceQuery.addJoin(mdInfoResJoin);
            deviceModelResourceQuery.addJoin(mdDeviceModelJoin);
            deviceModelResourceQuery.addJoin(agentContactJoin);
            deviceModelResourceQuery.addJoin(customDetailsJoin);
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_TYPE"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_NAME"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "PROCESSOR_ARCHITECTURE"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "PRODUCT_NAME"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "ManagedDeviceExtn.MANAGED_USER_ID"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "ManagedDeviceExtn.NAME"));
            deviceModelResourceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "DESCRIPTION", "ManagedDeviceExtn.DESCRIPTION"));
            final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer[] { 120, 121 }, 8);
            deviceModelResourceQuery.setCriteria(resTypeCri);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getMDMDeviceResourceQuery method : {0}", ex);
        }
        return deviceModelResourceQuery;
    }
    
    public HashMap getMDMDeviceProperties(final Long resourceId) {
        final List resList = new ArrayList();
        resList.add(resourceId);
        final HashMap deviceMapList = this.getMDMDeviceProperties(resList);
        final HashMap resHashMap = deviceMapList.get(resourceId);
        return resHashMap;
    }
    
    public HashMap getMDMDeviceProperties(final List resourceId, final Boolean isUserJoinRequired) {
        MDMUtil.logger.log(Level.INFO, "Inside getMDMDeviceProperties");
        HashMap deviceMapList = null;
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery deviceModelResourceQuery = this.getMDMDeviceResourceQuery(isUserJoinRequired);
            MDMUtil.logger.log(Level.INFO, "query constrcuted getMDMDeviceProperties");
            final Criteria resQuery = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceId.toArray(), 8);
            final Criteria cri = deviceModelResourceQuery.getCriteria().and(resQuery);
            deviceModelResourceQuery.setCriteria(cri);
            ds = DMDataSetWrapper.executeQuery((Object)deviceModelResourceQuery);
            deviceMapList = new HashMap();
            MDMUtil.logger.log(Level.INFO, "query executed getMDMDeviceProperties");
            while (ds.next()) {
                final HashMap deviceMap = new HashMap();
                final Long resID = (Long)ds.getValue("RESOURCE_ID");
                deviceMap.put("customDeviceDescription", ds.getValue("ManagedDeviceExtn.DESCRIPTION"));
                deviceMap.put("NAME", ds.getValue("ManagedDeviceExtn.NAME"));
                deviceMap.put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                deviceMap.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                deviceMap.put("AGENT_TYPE", ds.getValue("AGENT_TYPE"));
                deviceMap.put("AGENT_VERSION", ds.getValue("AGENT_VERSION"));
                deviceMap.put("OS_VERSION", ds.getValue("OS_VERSION"));
                deviceMap.put("MODEL_NAME", ds.getValue("MODEL_NAME"));
                deviceMap.put("MODEL_TYPE", ds.getValue("MODEL_TYPE"));
                deviceMap.put("PROCESSOR_ARCHITECTURE", ds.getValue("PROCESSOR_ARCHITECTURE"));
                deviceMap.put("PRODUCT_NAME", ds.getValue("PRODUCT_NAME"));
                deviceMap.put("LAST_CONTACT_TIME", ds.getValue("LAST_CONTACT_TIME"));
                deviceMap.put("OS_NAME", ds.getValue("OS_NAME"));
                deviceMapList.put(resID, deviceMap);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getMDMDeviceProperties  : {0}", ex);
        }
        return deviceMapList;
    }
    
    public HashMap getMDMGroupDeviceProperties(final Long resourceId) {
        HashMap devicePropMap = null;
        try {
            final SelectQuery mdmGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join cusRelJoin = new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2);
            final Join mdDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            mdmGroupQuery.addJoin(cusRelJoin);
            mdmGroupQuery.addJoin(mdDeviceJoin);
            mdmGroupQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            mdmGroupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            mdmGroupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            mdmGroupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REGISTERED_TIME"));
            mdmGroupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "ADDED_TIME"));
            mdmGroupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UNREGISTERED_TIME"));
            final Criteria cri = new Criteria(Column.getColumn("Resource", "GROUP_RESOURCE_ID"), (Object)resourceId, 0);
            mdmGroupQuery.setCriteria(cri);
            final DataObject dObj = getPersistence().get(mdmGroupQuery);
            if (!dObj.isEmpty()) {
                devicePropMap = new HashMap();
                final Row mdeviceRow = dObj.getFirstRow("ManagedDevice");
                devicePropMap.put("PLATFORM_TYPE", mdeviceRow.get("PLATFORM_TYPE"));
                devicePropMap.put("MANAGED_STATUS", mdeviceRow.get("MANAGED_STATUS"));
                devicePropMap.put("REGISTERED_TIME", mdeviceRow.get("REGISTERED_TIME"));
                devicePropMap.put("ENROLLED_TIME", mdeviceRow.get("ADDED_TIME"));
                devicePropMap.put("UNREGISTERED_TIME", mdeviceRow.get("UNREGISTERED_TIME"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getMDMGroupDeviceProperties method : {0}", ex);
        }
        return devicePropMap;
    }
    
    public HashMap getMDMDeviceProperties(final List resourceId) {
        return this.getMDMDeviceProperties(resourceId, Boolean.TRUE);
    }
    
    public ArrayList getMDMModelNameList() {
        ArrayList modelNameList = null;
        String modelName = "";
        try {
            modelNameList = new ArrayList();
            final HashMap modelNameListMap = new HashMap();
            final SelectQuery modelNameQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
            modelNameQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
            modelNameQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
            modelNameQuery.setDistinct(true);
            final DataObject dObj = getPersistence().get(modelNameQuery);
            if (!dObj.isEmpty()) {
                final Iterator itr = dObj.getRows("MdModelInfo");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    modelName = (String)row.get("MODEL_NAME");
                    if (modelNameListMap.get(modelName) == null) {
                        modelNameListMap.put(modelName, modelName);
                        modelNameList.add(modelName);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getMDMModelNameList....", ex);
        }
        return modelNameList;
    }
    
    public HashMap getMDMDeviceTypeMap() {
        HashMap modelTypeListMap = null;
        int modelType = -1;
        String modelTypeName = "";
        try {
            modelTypeListMap = new HashMap();
            final SelectQuery modelTypeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
            modelTypeQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            modelTypeQuery.setDistinct(true);
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)modelTypeQuery);
            while (ds.next()) {
                modelType = (int)ds.getValue("MODEL_TYPE");
                modelTypeName = this.getModelTypeName(modelType);
                modelTypeListMap.put(modelType, modelTypeName);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getMDMDeviceTypeMap....", ex);
        }
        return modelTypeListMap;
    }
    
    public HashMap getPlatformTypeMap() {
        HashMap platformTypeListMap = null;
        int platformType = -1;
        String platformName = "";
        try {
            platformTypeListMap = new HashMap();
            final SelectQuery platformTypeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            platformTypeQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            platformTypeQuery.setDistinct(true);
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)platformTypeQuery);
            while (ds.next()) {
                platformType = (int)ds.getValue("PLATFORM_TYPE");
                platformName = this.getPlatformName(platformType);
                platformTypeListMap.put(platformType, platformName);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getPlatformTypeMap....", ex);
        }
        return platformTypeListMap;
    }
    
    public HashMap getAppPackageTypeMap(final int platformType) {
        HashMap packageTypeMap = null;
        try {
            packageTypeMap = new HashMap();
            if (platformType == 1) {
                packageTypeMap.put(1, I18N.getMsg("dc.mdm.actionlog.appmgmt.appStoreApp", new Object[0]));
                packageTypeMap.put(2, I18N.getMsg("dc.mdm.actionlog.appmgmt.enterpriseApp", new Object[0]));
            }
            else if (platformType == 2) {
                packageTypeMap.put(3, I18N.getMsg("dc.mdm.actionlog.appmgmt.playStoreApp", new Object[0]));
                packageTypeMap.put(4, I18N.getMsg("dc.mdm.actionlog.appmgmt.android_enterpriseApp", new Object[0]));
            }
            else if (platformType == 3) {
                packageTypeMap.put(5, I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_businessStoreApp", new Object[0]));
                packageTypeMap.put(6, I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_enterpriseApp", new Object[0]));
                packageTypeMap.put(7, I18N.getMsg("mdm.appmgmt.windows.msi_apps", new Object[0]));
            }
            else {
                packageTypeMap.put(2000, I18N.getMsg("dc.mdm.group.app.store_apps", new Object[0]));
                packageTypeMap.put(2001, I18N.getMsg("dc.mdm.group.app.enterprise_apps", new Object[0]));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getAppPackageTypeMap....", ex);
        }
        return packageTypeMap;
    }
    
    public HashMap getAppPackageTypeMap() {
        HashMap packageTypeMap = null;
        try {
            packageTypeMap = new HashMap();
            packageTypeMap.put(1, I18N.getMsg("dc.mdm.actionlog.appmgmt.appStoreApp", new Object[0]));
            packageTypeMap.put(2, I18N.getMsg("dc.mdm.actionlog.appmgmt.enterpriseApp", new Object[0]));
            packageTypeMap.put(3, I18N.getMsg("dc.mdm.actionlog.appmgmt.playStoreApp", new Object[0]));
            packageTypeMap.put(4, I18N.getMsg("dc.mdm.actionlog.appmgmt.android_enterpriseApp", new Object[0]));
            packageTypeMap.put(6, I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_enterpriseApp", new Object[0]));
            packageTypeMap.put(5, I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_businessStoreApp", new Object[0]));
            packageTypeMap.put(7, I18N.getMsg("mdm.appmgmt.windows.msi_apps", new Object[0]));
            packageTypeMap.put(8, I18N.getMsg("Chrome Web Store App", new Object[0]));
            packageTypeMap.put(9, I18N.getMsg("Chrome Custom App", new Object[0]));
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getAppPackageTypeMap....", ex);
        }
        return packageTypeMap;
    }
    
    public Criteria getPackageTypeCriteria(final long packageType) {
        Criteria packageTypeCriteria = null;
        try {
            final Criteria iOSPlatformCri = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria androidPlatformCri = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria windowsPlatformCri = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)3, 0);
            final Criteria chromePlatformCri = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)4, 0);
            final Criteria freeCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)0, 0);
            final Criteria paidCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)1, 0);
            final Criteria enterpriseCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
            final Criteria msiCri = new Criteria(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"), (Object)".msi", 11, (boolean)Boolean.FALSE);
            final Criteria privateAppCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "PRIVATE_APP_TYPE"), (Object)1, 0);
            if (packageType == 1L) {
                packageTypeCriteria = iOSPlatformCri.and(freeCri.or(paidCri));
            }
            else if (packageType == 2L) {
                packageTypeCriteria = iOSPlatformCri.and(enterpriseCri);
            }
            else if (packageType == 3L) {
                packageTypeCriteria = androidPlatformCri.and(freeCri.or(paidCri).and(privateAppCri.negate()));
            }
            else if (packageType == 4L) {
                packageTypeCriteria = androidPlatformCri.and(enterpriseCri.or(privateAppCri));
            }
            else if (packageType == 5L) {
                packageTypeCriteria = windowsPlatformCri.and(freeCri.or(paidCri));
            }
            else if (packageType == 6L) {
                packageTypeCriteria = windowsPlatformCri.and(enterpriseCri).and(msiCri.negate());
            }
            else if (packageType == 7L) {
                packageTypeCriteria = windowsPlatformCri.and(enterpriseCri).and(msiCri);
            }
            else if (packageType == 2000L) {
                packageTypeCriteria = freeCri.or(paidCri).and(privateAppCri.negate());
            }
            else if (packageType == 2001L) {
                packageTypeCriteria = enterpriseCri.or(privateAppCri);
            }
            else if (packageType == 8L) {
                packageTypeCriteria = chromePlatformCri.and(freeCri.or(paidCri));
            }
            else if (packageType == 9L) {
                packageTypeCriteria = chromePlatformCri.and(enterpriseCri);
            }
            else if (packageType == 10L) {
                packageTypeCriteria = androidPlatformCri.and(privateAppCri);
            }
            else if (packageType == 11L) {
                packageTypeCriteria = androidPlatformCri.and(enterpriseCri);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getPackageTypeCriteria....", ex);
        }
        return packageTypeCriteria;
    }
    
    public int getDeviceGroupCount(final Long resID) {
        int groupCount = 0;
        try {
            SelectQuery memberCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Column groupCol = new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
            final Column groupCountCol = groupCol.count();
            groupCountCol.setColumnAlias("GROUP_COUNT");
            memberCountQuery.addSelectColumn(groupCountCol);
            final Column memberColumn = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
            final Criteria cri = new Criteria(memberColumn, (Object)resID, 0);
            memberCountQuery.setCriteria(cri.and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new int[] { 9, 8 }, 9)));
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            memberCountQuery.addJoin(customGroupJoin);
            memberCountQuery = RBDAUtil.getInstance().getRBDAQuery(memberCountQuery);
            final List list = new ArrayList();
            list.add(memberColumn);
            final GroupByClause groupBy = new GroupByClause(list);
            memberCountQuery.setGroupByClause(groupBy);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)memberCountQuery);
                if (ds.next()) {
                    groupCount = (int)ds.getValue("GROUP_COUNT");
                }
            }
            catch (final Exception ex) {
                MDMUtil.logger.log(Level.WARNING, "Exception occoured in getDeviceGroupCount Query Execution....", ex);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getDeviceGroupCount....", e);
        }
        return groupCount;
    }
    
    public int getProfileCountforResource(final Long resID) {
        int profileCount = 0;
        try {
            final Criteria cri = new Criteria(Column.getColumn("ResourceToProfileSummary", "RESOURCE_ID"), (Object)resID, 0);
            final DataObject dObj = DataAccess.get("ResourceToProfileSummary", cri);
            if (!dObj.isEmpty()) {
                profileCount = (int)dObj.getValue("ResourceToProfileSummary", "PROFILE_COUNT", cri);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getGroupProfileCount....", e);
        }
        return profileCount;
    }
    
    public int getAppCountforResource(final Long resID) {
        int appCount = 0;
        try {
            final Criteria cri = new Criteria(Column.getColumn("ResourceToProfileSummary", "RESOURCE_ID"), (Object)resID, 0);
            final DataObject dObj = DataAccess.get("ResourceToProfileSummary", cri);
            if (!dObj.isEmpty()) {
                appCount = (int)dObj.getValue("ResourceToProfileSummary", "APP_COUNT", cri);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getGroupAppsCount....", e);
        }
        return appCount;
    }
    
    public HashMap getDeviceProfileStatusSummary(final Long resourceId) throws Exception {
        final HashMap deviceProfileStatusMap = new HashMap();
        final String tableName = "CollnToResources";
        final String columnName = "COLLECTION_ID";
        final Criteria resourceIdCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        Criteria yetToApplyCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)12, 0);
        Criteria successCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)6, 0);
        Criteria failedCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)7, 0);
        yetToApplyCriteria = yetToApplyCriteria.and(resourceIdCriteria);
        successCriteria = successCriteria.and(resourceIdCriteria);
        failedCriteria = failedCriteria.and(resourceIdCriteria);
        final int totalCount = DBUtil.getRecordCount(tableName, columnName, resourceIdCriteria);
        final int yetToApplyCount = DBUtil.getRecordCount(tableName, columnName, yetToApplyCriteria);
        final int successCount = DBUtil.getRecordCount(tableName, columnName, successCriteria);
        final int failedCount = DBUtil.getRecordCount(tableName, columnName, failedCriteria);
        deviceProfileStatusMap.put("totalCount", totalCount);
        deviceProfileStatusMap.put("yetToApplyCount", yetToApplyCount);
        deviceProfileStatusMap.put("successCount", successCount);
        deviceProfileStatusMap.put("failedCount", failedCount);
        return deviceProfileStatusMap;
    }
    
    public int getPlatformType(final int packageTypeIdentifier) {
        int platformType = -1;
        if (packageTypeIdentifier == 1 || packageTypeIdentifier == 2) {
            platformType = 1;
        }
        else if (packageTypeIdentifier == 3 || packageTypeIdentifier == 4) {
            platformType = 2;
        }
        else if (packageTypeIdentifier == 6 || packageTypeIdentifier == 5) {
            platformType = 3;
        }
        return platformType;
    }
    
    public String getPlatformName(final int platformType) {
        String platformName = "";
        try {
            if (platformType == 1) {
                platformName = I18N.getMsg("mdm.os.Apple", new Object[0]);
            }
            else if (platformType == 2) {
                platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
            }
            else if (platformType == 3) {
                platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if (platformType == 4) {
                platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
            else if (platformType == 6) {
                platformName = I18N.getMsg("mdm.os.mac", new Object[0]);
            }
            else if (platformType == 7) {
                platformName = I18N.getMsg("mdm.os.tvos", new Object[0]);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getPlatformName....", e);
        }
        return platformName;
    }
    
    public String getPlatformImage(final int platformType) {
        String platformImage = "";
        try {
            if (platformType == 1) {
                platformImage = "<img src=\"/images/applelogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/>";
            }
            else if (platformType == 2) {
                platformImage = "<img src=\"/images/androidlogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/>";
            }
            else if (platformType == 3) {
                platformImage = "<img src=\"/images/windowslogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/>";
            }
            else if (platformType == 4) {
                platformImage = "<img src=\"/images/windowlogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/>";
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getPlatformImage....", e);
        }
        return platformImage;
    }
    
    public String getPlatformColumnValue(final int platformType, final String isExport) {
        String platformName = "";
        try {
            if ((isExport == null || isExport.equalsIgnoreCase("false")) && platformType == 1) {
                platformName = "<img src=\"/images/applelogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/> " + I18N.getMsg("mdm.os.Apple", new Object[0]);
            }
            else if ((isExport == null || isExport.equalsIgnoreCase("false")) && platformType == 2) {
                platformName = "<img src=\"/images/androidlogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/> " + I18N.getMsg("dc.mdm.android", new Object[0]);
            }
            else if ((isExport == null || isExport.equalsIgnoreCase("false")) && platformType == 3) {
                platformName = "<img src=\"/images/windowslogo.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/> " + I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if ((isExport == null || isExport.equalsIgnoreCase("false")) && platformType == 4) {
                platformName = "<img src=\"/images/chrome.png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\"/> " + I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
            else if (platformType == 1) {
                platformName = I18N.getMsg("mdm.os.Apple", new Object[0]);
            }
            else if (platformType == 2) {
                platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
            }
            else if (platformType == 3) {
                platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if (platformType == 4) {
                platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
            else if (platformType == 7) {
                platformName = I18N.getMsg("mdm.os.tvos", new Object[0]);
            }
            else if (platformType == 0) {
                platformName = I18N.getMsg("mdm.os.neutralPlatform", new Object[0]);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getPlatformColumnValue....", ex);
        }
        return platformName;
    }
    
    public static Map getAPNSCertificateDetails() {
        final Map certificateDetails = new HashMap();
        try {
            final DataObject apnsInfo = getPersistence().get("APNSCertificateInfo", (Criteria)null);
            if (!apnsInfo.isEmpty()) {
                final Row apnsInfoRow = apnsInfo.getFirstRow("APNSCertificateInfo");
                certificateDetails.put("CERTIFICATE_FILE_NAME", apnsInfoRow.get("CERTIFICATE_FILE_NAME"));
                certificateDetails.put("CERTIFICATE_PASSWORD", apnsInfoRow.get("CERTIFICATE_PASSWORD"));
                final DataObject apnsCertificateDetailsDO = getPersistence().get("APNSCertificateDetails", (Criteria)null);
                final Row apnsCertificateDetailsRow = apnsCertificateDetailsDO.getFirstRow("APNSCertificateDetails");
                certificateDetails.put("EXPIRY_DATE", apnsCertificateDetailsRow.get("EXPIRY_DATE"));
                certificateDetails.put("CERTIFICATE_NAME", apnsCertificateDetailsRow.get("CERTIFICATE_NAME"));
                certificateDetails.put("EXPIRY_DATE_STRING", getDate((long)apnsCertificateDetailsRow.get("EXPIRY_DATE")));
                certificateDetails.put("CREATION_DATE_STRING", getDate((long)apnsCertificateDetailsRow.get("CREATION_DATE")));
                certificateDetails.put("ISSUER_NAME", apnsCertificateDetailsRow.get("ISSUER_NAME"));
                certificateDetails.put("ISSUER_OU_NAME", apnsCertificateDetailsRow.get("ISSUER_OU_NAME"));
                certificateDetails.put("ISSUER_ORG_NAME", apnsCertificateDetailsRow.get("ISSUER_ORG_NAME"));
                certificateDetails.put("CREATION_DATE", apnsCertificateDetailsRow.get("CREATION_DATE"));
                certificateDetails.put("TOPIC", apnsCertificateDetailsRow.get("TOPIC"));
                certificateDetails.put("APPLE_ID", apnsCertificateDetailsRow.get("APPLE_ID"));
                certificateDetails.put("EMAIL_ADDRESS", apnsCertificateDetailsRow.get("EMAIL_ADDRESS"));
                certificateDetails.put("CERTIFICATE_ID", apnsCertificateDetailsRow.get("CERTIFICATE_ID"));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return certificateDetails;
    }
    
    public static String getAPNsCertificateFolderPath() throws Exception {
        final String apnsCertificateFolder = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("apnsCertificate");
        return apnsCertificateFolder;
    }
    
    public static String getAppCatalogWebClipsImagePath() throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appCatalogWebClipsImagePath = webappsDir + File.separator + "mdm" + File.separator + "webclips" + File.separator + "appCatalog.png";
        return appCatalogWebClipsImagePath;
    }
    
    public static String getCredentialCertificateFolder(final Long customerId) {
        final String clientDataDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("credentials");
        final String serverCertificateFilePath = clientDataDir + File.separator + customerId + File.separator + "credential";
        return serverCertificateFilePath;
    }
    
    public String getCollectionIdFromCommandUUID(final String commandUUID) {
        String strCollectionID = null;
        final List valueList = getInstance().getStringList(commandUUID, ";");
        final String collectionIdStr = valueList.get(1);
        final List valueList2 = getInstance().getStringList(collectionIdStr, "=");
        strCollectionID = valueList2.get(1);
        return strCollectionID;
    }
    
    public Long[] decodeMDMMemberIds(final String resourceIdList) {
        Long[] resourceIds = null;
        if (resourceIdList != null && !resourceIdList.equals("")) {
            final String[] resourceIdsString = resourceIdList.split(",");
            final HashSet<Long> tempResourceIds = new HashSet<Long>();
            for (int i = 0; i < resourceIdsString.length; ++i) {
                tempResourceIds.add(Long.parseLong(resourceIdsString[i]));
            }
            resourceIds = tempResourceIds.toArray(new Long[tempResourceIds.size()]);
        }
        return resourceIds;
    }
    
    public HashMap getAppDetails(final Long appId) {
        HashMap appsMap = null;
        try {
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
            final DataObject dObj = DataAccess.get("MdAppDetails", appIdCri);
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdAppDetails");
                appsMap = new HashMap();
                appsMap.put("APP_ID", appRow.get("APP_ID"));
                appsMap.put("APP_NAME", appRow.get("APP_NAME"));
                appsMap.put("APP_TYPE", appRow.get("APP_TYPE"));
                appsMap.put("IDENTIFIER", appRow.get("IDENTIFIER"));
                appsMap.put("APP_VERSION", appRow.get("APP_VERSION"));
                appsMap.put("PLATFORM_TYPE", appRow.get("PLATFORM_TYPE"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getAppDetailsFromAppFile....", ex);
        }
        return appsMap;
    }
    
    public HashMap getAppGroupDetails(final Long appGroupId) {
        HashMap appsMap = null;
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dObj = DataAccess.get("MdAppGroupDetails", appIdCri.and(customerCriteria));
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdAppGroupDetails");
                appsMap = new HashMap();
                appsMap.put("APP_GROUP_ID", appRow.get("APP_GROUP_ID"));
                appsMap.put("GROUP_DISPLAY_NAME", appRow.get("GROUP_DISPLAY_NAME"));
                appsMap.put("APP_TYPE", appRow.get("APP_TYPE"));
                appsMap.put("IDENTIFIER", appRow.get("IDENTIFIER"));
                appsMap.put("PLATFORM_TYPE", appRow.get("PLATFORM_TYPE"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getAppDetailsFromAppFile....", ex);
        }
        return appsMap;
    }
    
    public HashMap getAppPackageDataDetails(final Long appID) {
        HashMap appsMap = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            sQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            final Criteria appIdCri = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appID, 0);
            sQuery.setCriteria(appIdCri);
            final DataObject dObj = DataAccess.get(sQuery);
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdPackageToAppData");
                appsMap = new HashMap();
                appsMap.put("APP_ID", appRow.get("APP_ID"));
                appsMap.put("APP_GROUP_ID", appRow.get("APP_GROUP_ID"));
                appsMap.put("STORE_ID", appRow.get("STORE_ID"));
                appsMap.put("PACKAGE_ID", appRow.get("PACKAGE_ID"));
                final Row appGroupRow = dObj.getFirstRow("MdPackageToAppGroup");
                appsMap.put("PACKAGE_TYPE", appGroupRow.get("PACKAGE_TYPE"));
                appsMap.put("IS_PAID_APP", appGroupRow.get("IS_PAID_APP"));
                appsMap.put("IS_PURCHASED_FROM_PORTAL", appGroupRow.get("IS_PURCHASED_FROM_PORTAL"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getAppPackageDataDetails....", ex);
        }
        return appsMap;
    }
    
    @Deprecated
    public long getAppIdFromCollectionId(final Long collectionId) {
        return this.getAppIDFromCollection(collectionId);
    }
    
    public int getDeviceSupport(final JSONObject rawAppDetails) throws JSONException {
        final JSONArray supportedDevicesArray = rawAppDetails.optJSONArray("supportedDevices");
        final String kind = (String)rawAppDetails.get("kind");
        int deviceSupport = 0;
        boolean iPadSupport = false;
        boolean iPhoneSupport = false;
        boolean appleTVSupport = false;
        boolean macOSSupport = false;
        boolean ipodSupport = false;
        if (kind.toLowerCase().contains("desktopapp") || kind.toLowerCase().contains("mac")) {
            macOSSupport = true;
        }
        if (supportedDevicesArray != null) {
            for (int j = 0; j < supportedDevicesArray.length(); ++j) {
                final String sDevice = (String)supportedDevicesArray.get(j);
                if (sDevice.toLowerCase().contains("ipad")) {
                    iPadSupport = true;
                }
                else if (sDevice.toLowerCase().contains("iphone")) {
                    iPhoneSupport = true;
                }
                else if (sDevice.toLowerCase().contains("appletv") || sDevice.toLowerCase().contains("tvos")) {
                    appleTVSupport = true;
                }
                else if (sDevice.toLowerCase().contains("pod")) {
                    ipodSupport = true;
                }
                else if (sDevice.toLowerCase().contains("mac")) {
                    macOSSupport = true;
                }
                else if (sDevice.toLowerCase().contains("all")) {
                    iPadSupport = true;
                    iPhoneSupport = true;
                    appleTVSupport = true;
                    ipodSupport = true;
                }
            }
        }
        if (iPhoneSupport) {
            deviceSupport |= 0x2;
        }
        if (iPadSupport) {
            deviceSupport |= 0x1;
        }
        if (appleTVSupport) {
            deviceSupport |= 0x8;
        }
        if (macOSSupport) {
            deviceSupport |= 0x10;
        }
        if (ipodSupport) {
            deviceSupport |= 0x4;
        }
        return deviceSupport;
    }
    
    public HashMap executeCountQuery(final SelectQuery query) {
        final HashMap graphData = new HashMap();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object key = ds.getValue(1);
                final Object value = ds.getValue(2);
                if (key != null && value != null) {
                    graphData.put(key, value);
                }
            }
            ds.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return graphData;
    }
    
    public void scheduleMDMCommand(final Long resourceID, final String commandName, final Long scheduleTime) {
        this.scheduleMDMCommand(resourceID, commandName, scheduleTime, null);
    }
    
    public void scheduleMDMCommand(final Long resourceID, final String commandName, final Long scheduleTime, final Properties taskProperties) {
        MDMUtil.logger.log(Level.INFO, "scheduleMDMCommand: resourceID: {0} commandName: {1} scheduleTime: {2}", new Object[] { resourceID, commandName, scheduleTime });
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        this.scheduleMDMCommand(resourceList, commandName, scheduleTime, taskProperties);
    }
    
    public void scheduleMDMCommand(final List<Long> resourceList, final String commandName, final Long scheduleTime, final Properties taskProperties) {
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("commandName", commandName);
        ((Hashtable<String, String>)properties).put("resourceList", resourceList.toString());
        if (taskProperties != null) {
            properties.putAll(taskProperties);
        }
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "ExecuteMdmCommand");
        taskInfoMap.put("schedulerTime", scheduleTime);
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.adventnet.sym.server.mdm.task.MDMScheduledCommandTask", taskInfoMap, properties);
        }
        catch (final Exception exp) {
            MDMUtil.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : {0}", exp);
        }
    }
    
    public String getCurrentlyLoggedOnUserName() throws Exception {
        return ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
    }
    
    public Long getCurrentlyLoggedOnUserID() throws Exception {
        final Long currentlyLoggedInUserId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        return currentlyLoggedInUserId;
    }
    
    public HashMap getCurrentlyLoggenOnUserInfo() {
        final HashMap hash = new HashMap();
        Long currentlyLoggedInUserLoginId = null;
        try {
            currentlyLoggedInUserLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (currentlyLoggedInUserLoginId != null) {
            final String sUserName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
            final String sDomainName = DMUserHandler.getDCUserDomain(currentlyLoggedInUserLoginId);
            hash.put("UserId", currentlyLoggedInUserLoginId);
            hash.put("UserName", sUserName);
            hash.put("DomainName", sDomainName);
        }
        return hash;
    }
    
    public HashMap getProfileDetails(final Long profileId) {
        HashMap profileMap = null;
        try {
            final Criteria profileIdCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0);
            final DataObject dObj = DataAccess.get("Profile", profileIdCri);
            if (!dObj.isEmpty()) {
                final Row profileRow = dObj.getFirstRow("Profile");
                profileMap = new HashMap();
                profileMap.put("PROFILE_ID", profileRow.get("PROFILE_ID"));
                profileMap.put("PROFILE_NAME", profileRow.get("PROFILE_NAME"));
                profileMap.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
                profileMap.put("PROFILE_TYPE", profileRow.get("PROFILE_TYPE"));
                profileMap.put("CREATED_BY", profileRow.get("CREATED_BY"));
                profileMap.put("LAST_MODIFIED_BY", profileRow.get("LAST_MODIFIED_BY"));
                profileMap.put("PROFILE_DESCRIPTION", profileRow.get("PROFILE_DESCRIPTION"));
                profileMap.put("SCOPE", profileRow.get("SCOPE"));
                profileMap.put("PROFILE_PAYLOAD_IDENTIFIER", profileRow.get("PROFILE_PAYLOAD_IDENTIFIER"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getProfileDetails....", ex);
        }
        return profileMap;
    }
    
    public ArrayList getProfileDetails(final String profileIds) {
        final ArrayList multipleProfileDetails = new ArrayList();
        try {
            final String[] sArrProfileID = profileIds.split(",");
            final Criteria profileIdCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)sArrProfileID, 8);
            final DataObject dObj = DataAccess.get("Profile", profileIdCri);
            final Iterator profileItr = dObj.getRows("Profile");
            while (profileItr.hasNext()) {
                final HashMap profileMap = new HashMap();
                final Row profileRow = profileItr.next();
                profileMap.put("PROFILE_ID", profileRow.get("PROFILE_ID"));
                profileMap.put("PROFILE_NAME", profileRow.get("PROFILE_NAME"));
                profileMap.put("PLATFORM_TYPE", profileRow.get("PLATFORM_TYPE"));
                multipleProfileDetails.add(profileMap);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getProfileDetails....", ex);
        }
        return multipleProfileDetails;
    }
    
    public boolean validate(final String imageFileName) {
        final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg|ico|ioswallpaper))$)";
        final Pattern pattern = Pattern.compile(IMAGE_PATTERN);
        final Matcher matcher = pattern.matcher(imageFileName);
        return matcher.matches();
    }
    
    public int getSupportedDevice(final Long appId) throws Exception {
        final int supuportedDev = (int)DBUtil.getValueFromDB("MdPackageToAppData", "APP_ID", (Object)appId, "SUPPORTED_DEVICES");
        return supuportedDev;
    }
    
    public int getModelType(final Long resourceId) throws Exception {
        int modelTypeIdentifier = 0;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final Criteria resCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.setCriteria(resCriteria);
        sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
        final DataObject DO = getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            final Row modelRow = DO.getFirstRow("MdModelInfo");
            final int modelType = (int)modelRow.get("MODEL_TYPE");
            if (modelType == 1) {
                modelTypeIdentifier = 2;
            }
            else if (modelType == 2) {
                modelTypeIdentifier = 3;
            }
            else {
                modelTypeIdentifier = 0;
            }
        }
        return modelTypeIdentifier;
    }
    
    public Boolean isMacDevice(final Long resourceID) {
        int modelType = -1;
        int platformType = -1;
        try {
            modelType = this.getiOSDeivceModelType(resourceID);
            platformType = this.getPlatformType(resourceID);
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, "Problem in Getting model type | platform type of the device", e);
        }
        return modelType == 16 && platformType == 1;
    }
    
    public int getModelTypeFromDB(final Long resourceId) throws Exception {
        int modelType = 0;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final Criteria resCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.setCriteria(resCriteria);
        sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
        final DataObject DO = getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            final Row modelRow = DO.getFirstRow("MdModelInfo");
            modelType = (int)modelRow.get("MODEL_TYPE");
        }
        return modelType;
    }
    
    public int getiOSDeivceModelType(final Long resourceId) throws Exception {
        int modelTypeIdentifier = 0;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final Criteria resCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.setCriteria(resCriteria);
        sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
        final DataObject DO = getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            final Row modelRow = DO.getFirstRow("MdModelInfo");
            final int modelType = (int)modelRow.get("MODEL_TYPE");
            if (modelType == 2) {
                modelTypeIdentifier = 1;
            }
            else if (modelType == 0) {
                modelTypeIdentifier = 4;
            }
            else if (modelType == 1) {
                modelTypeIdentifier = 2;
            }
            else if (modelType == 5) {
                modelTypeIdentifier = 8;
            }
            else if (modelType == 3 || modelType == 4) {
                modelTypeIdentifier = 16;
            }
        }
        return modelTypeIdentifier;
    }
    
    public String getModelTypeName(final int modelType) throws Exception {
        String modelTypeName = "";
        if (modelType == 1) {
            modelTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
        }
        else if (modelType == 2) {
            modelTypeName = I18N.getMsg("dc.mdm.graphs.tablet", new Object[0]);
        }
        else if (modelType == 3) {
            modelTypeName = I18N.getMsg("dc.common.LAPTOP", new Object[0]);
        }
        else if (modelType == 4) {
            modelTypeName = I18N.getMsg("dc.common.DESKTOP", new Object[0]);
        }
        else if (modelType == 5) {
            modelTypeName = I18N.getMsg("dc.common.TV", new Object[0]);
        }
        else {
            modelTypeName = I18N.getMsg("dc.common.OTHERS", new Object[0]);
        }
        return modelTypeName;
    }
    
    public HashMap getProfileDetailsForCollectionId(final Long collectionId) {
        HashMap profileMap = null;
        try {
            final Criteria cri = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final DataObject dObj = DataAccess.get("ProfileToCollection", cri);
            if (!dObj.isEmpty()) {
                final Row profileToCollRow = dObj.getFirstRow("ProfileToCollection");
                final Long profileId = (Long)profileToCollRow.get("PROFILE_ID");
                profileMap = this.getProfileDetails(profileId);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getProfileDetails....", ex);
        }
        return profileMap;
    }
    
    public LinkedHashMap getMDMByodStatus() {
        return this.getMDMByodStatus(Boolean.FALSE);
    }
    
    public LinkedHashMap getMDMByodStatus(final Boolean isCalledFromMETrack) {
        final LinkedHashMap hashMap = new LinkedHashMap();
        hashMap.put("corporate", 0L);
        hashMap.put("personal", 0L);
        hashMap.put("not_specified", 0L);
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria managed = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        query.setCriteria(managed);
        final Column ownedByStatusColumn = new Column("DeviceEnrollmentRequest", "OWNED_BY");
        query.addSelectColumn(ownedByStatusColumn);
        Column countCol = new Column("Resource", "RESOURCE_ID");
        countCol = countCol.distinct();
        countCol = countCol.count();
        query.addSelectColumn(countCol);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(ownedByStatusColumn);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        query.setGroupByClause(grpByCls);
        if (!isCalledFromMETrack) {
            query = RBDAUtil.getInstance().getRBDAQuery(query);
        }
        final HashMap graphData = this.executeCountQuery(query);
        for (final Map.Entry pairs : graphData.entrySet()) {
            final int ownedBy = pairs.getKey();
            final long count = pairs.getValue();
            if (ownedBy == 1) {
                hashMap.put("corporate", count);
            }
            else if (ownedBy == 2) {
                hashMap.put("personal", count);
            }
            else {
                hashMap.put("not_specified", count);
            }
        }
        return hashMap;
    }
    
    public boolean getMarkedForDeleteStatus(final Long resourceId, final Long collectionId) {
        boolean isMarkedForDelete = false;
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria collIdCri = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria cri = resIdCri.and(collIdCri);
            final DataObject dObj = DataAccess.get("RecentProfileForResource", cri);
            if (!dObj.isEmpty()) {
                isMarkedForDelete = (boolean)dObj.getValue("RecentProfileForResource", "MARKED_FOR_DELETE", cri);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in getMarkedForDeleteStatus", ex);
        }
        return isMarkedForDelete;
    }
    
    public boolean addorUpdateAppSettings(final Properties appProp) {
        Boolean addedSuccess = Boolean.TRUE;
        final Boolean isWhiteList = ((Hashtable<K, Boolean>)appProp).get("IS_WHITE_LIST");
        final Boolean alertBlacklist = ((Hashtable<K, Boolean>)appProp).get("ENABLE_BLACKLIST_ALERT");
        final Boolean alertAppdiscovery = ((Hashtable<K, Boolean>)appProp).get("ENABLE_APP_DISCOVERY_ALERT");
        final Integer notifyCount = ((Hashtable<K, Integer>)appProp).get("NOTIFICATION_COUNT");
        final Long customerId = ((Hashtable<K, Long>)appProp).get("CUSTOMER_ID");
        final Boolean summaryAlert = ((Hashtable<K, Boolean>)appProp).get("ENABLE_SUMMARY_ALERT");
        final Integer actionOnCorporateDevice = (((Hashtable<K, Integer>)appProp).get("ACTION_ON_CORPORATE_DEVICE") == null) ? 2 : ((Hashtable<K, Integer>)appProp).get("ACTION_ON_CORPORATE_DEVICE");
        final Integer applyCorpConfigImmediately = (((Hashtable<K, Integer>)appProp).get("APPLY_CORP_CONFIG_IMMEDIATELY") == null) ? 1 : ((Hashtable<K, Integer>)appProp).get("APPLY_CORP_CONFIG_IMMEDIATELY");
        final Integer corpEmailNotifyCount = (((Hashtable<K, Integer>)appProp).get("CORP_EMAIL_NOTIFICATION_COUNT") == null) ? 3 : ((Hashtable<K, Integer>)appProp).get("CORP_EMAIL_NOTIFICATION_COUNT");
        final Integer actionOnPersonalDevice = (((Hashtable<K, Integer>)appProp).get("ACTION_ON_BYOD_DEVICE") == null) ? 2 : ((Hashtable<K, Integer>)appProp).get("ACTION_ON_BYOD_DEVICE");
        final Integer applyPersonalConfigImmediately = (((Hashtable<K, Integer>)appProp).get("APPLY_BYOD_CONFIG_IMMEDIATELY") == null) ? 1 : ((Hashtable<K, Integer>)appProp).get("APPLY_BYOD_CONFIG_IMMEDIATELY");
        final Integer personalEmailNotifyCount = (((Hashtable<K, Integer>)appProp).get("BYOD_EMAIL_NOTIFICATION_COUNT") == null) ? 3 : ((Hashtable<K, Integer>)appProp).get("BYOD_EMAIL_NOTIFICATION_COUNT");
        final Criteria cCust = new Criteria(new Column("MdAppBlackListSetting", "CUSTOMER_ID"), (Object)customerId, 0);
        try {
            final DataObject DO = getPersistence().get("MdAppBlackListSetting", cCust);
            if (DO.isEmpty()) {
                final Row row = new Row("MdAppBlackListSetting");
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("IS_WHITE_LIST", (Object)(isWhiteList == null || isWhiteList));
                row.set("ENABLE_BLACKLIST_ALERT", (Object)alertBlacklist);
                row.set("ENABLE_APP_DISCOVERY_ALERT", (Object)alertAppdiscovery);
                row.set("NOTIFICATION_COUNT", (Object)((notifyCount == null) ? 0 : notifyCount));
                row.set("ENABLE_SUMMARY_ALERT", (Object)summaryAlert);
                row.set("ACTION_ON_CORPORATE_DEVICE", (Object)actionOnCorporateDevice);
                row.set("APPLY_CORP_CONFIG_IMMEDIATELY", (Object)applyCorpConfigImmediately);
                row.set("CORP_EMAIL_NOTIFICATION_COUNT", (Object)corpEmailNotifyCount);
                row.set("ACTION_ON_BYOD_DEVICE", (Object)actionOnPersonalDevice);
                row.set("APPLY_BYOD_CONFIG_IMMEDIATELY", (Object)applyPersonalConfigImmediately);
                row.set("BYOD_EMAIL_NOTIFICATION_COUNT", (Object)personalEmailNotifyCount);
                DO.addRow(row);
                getPersistence().add(DO);
            }
            else {
                final Row row = DO.getFirstRow("MdAppBlackListSetting");
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("IS_WHITE_LIST", (Object)(isWhiteList == null || isWhiteList));
                row.set("ENABLE_BLACKLIST_ALERT", (Object)alertBlacklist);
                row.set("ENABLE_APP_DISCOVERY_ALERT", (Object)alertAppdiscovery);
                row.set("NOTIFICATION_COUNT", (Object)((notifyCount == null) ? 0 : notifyCount));
                row.set("ENABLE_SUMMARY_ALERT", (Object)summaryAlert);
                row.set("ACTION_ON_CORPORATE_DEVICE", (Object)actionOnCorporateDevice);
                row.set("APPLY_CORP_CONFIG_IMMEDIATELY", (Object)applyCorpConfigImmediately);
                row.set("CORP_EMAIL_NOTIFICATION_COUNT", (Object)corpEmailNotifyCount);
                row.set("ACTION_ON_BYOD_DEVICE", (Object)actionOnPersonalDevice);
                row.set("APPLY_BYOD_CONFIG_IMMEDIATELY", (Object)applyPersonalConfigImmediately);
                row.set("BYOD_EMAIL_NOTIFICATION_COUNT", (Object)personalEmailNotifyCount);
                DO.updateRow(row);
                getPersistence().update(DO);
            }
        }
        catch (final DataAccessException ex) {
            MDMUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
            addedSuccess = Boolean.FALSE;
        }
        return addedSuccess;
    }
    
    public DataObject getAppSettingsDO(final Long customerId) {
        DataObject DO = null;
        final Criteria cCust = new Criteria(new Column("MdAppBlackListSetting", "CUSTOMER_ID"), (Object)customerId, 0);
        try {
            DO = getPersistence().get("MdAppBlackListSetting", cCust);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, null, ex);
        }
        return DO;
    }
    
    public JSONObject getBlacklistAppSettings(final Long customerID) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final DataObject dataObject = this.getAppSettingsDO(customerID);
        final Row row = dataObject.getFirstRow("MdAppBlackListSetting");
        jsonObject.put("ENABLE_APP_DISCOVERY_ALERT", row.get("ENABLE_APP_DISCOVERY_ALERT"));
        jsonObject.put("ENABLE_SUMMARY_ALERT", row.get("ENABLE_SUMMARY_ALERT"));
        jsonObject.put("ENABLE_BLACKLIST_ALERT", row.get("ENABLE_BLACKLIST_ALERT"));
        return jsonObject;
    }
    
    public void adddefaultAppSettings(final Long customerId) {
        final Properties appProp = new Properties();
        ((Hashtable<String, Boolean>)appProp).put("IS_WHITE_LIST", Boolean.TRUE);
        ((Hashtable<String, Boolean>)appProp).put("ENABLE_BLACKLIST_ALERT", Boolean.FALSE);
        ((Hashtable<String, Boolean>)appProp).put("ENABLE_APP_DISCOVERY_ALERT", Boolean.FALSE);
        ((Hashtable<String, Boolean>)appProp).put("ENABLE_SUMMARY_ALERT", Boolean.FALSE);
        ((Hashtable<String, Integer>)appProp).put("NOTIFICATION_COUNT", 3);
        ((Hashtable<String, Long>)appProp).put("CUSTOMER_ID", customerId);
        this.addorUpdateAppSettings(appProp);
    }
    
    public String getUserNameforDevice(final long resourceID) {
        String sUserName = null;
        try {
            final Long userId = (Long)DBUtil.getValueFromDB("ManagedUserToDevice", "MANAGED_DEVICE_ID", (Object)resourceID, "MANAGED_USER_ID");
            sUserName = (String)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)userId, "NAME");
        }
        catch (final Exception exp) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in getDeviceUserName method ", exp);
        }
        return sUserName;
    }
    
    public int getRootedDeviceCount(final Long customerId) {
        int blackListCount = 0;
        try {
            final Criteria customerCr = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdSecurityInfo"));
            sQuery.addJoin(new Join("MdSecurityInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria cRooted = new Criteria(new Column("MdSecurityInfo", "DEVICE_ROOTED"), (Object)Boolean.TRUE, 0);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria platformCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            Criteria criteria = cRooted.and(cManagedStatus).and(platformCri);
            if (customerId != null) {
                criteria = criteria.and(customerCr);
            }
            sQuery.setCriteria(criteria);
            sQuery = RBDAUtil.getInstance().getRBDAQuery(sQuery);
            blackListCount = DBUtil.getRecordCount(sQuery, "MdSecurityInfo", "RESOURCE_ID");
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while getting rooted device count", ex);
        }
        return blackListCount;
    }
    
    public int getJailBrokenDeviceCount(final Long customerId) {
        final Criteria cRooted = new Criteria(new Column("MdSecurityInfo", "DEVICE_ROOTED"), (Object)Boolean.TRUE, 0);
        int jailBrokenCount = 0;
        try {
            SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdSecurityInfo"));
            sQuery.addJoin(new Join("MdSecurityInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCr = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria platformCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            sQuery.setCriteria(customerCr.and(cRooted).and(cManagedStatus).and(platformCri));
            sQuery = RBDAUtil.getInstance().getRBDAQuery(sQuery);
            jailBrokenCount = DBUtil.getRecordCount(sQuery, "MdSecurityInfo", "RESOURCE_ID");
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while getting rooted device count", ex);
        }
        return jailBrokenCount;
    }
    
    public int getBlackListAppCount(final Long customerId) {
        int blackListCount = 0;
        try {
            final SelectQuery selectQuery = BlacklistQueryUtils.getInstance().getDeviceWithBlacklistAppCount(customerId);
            blackListCount = MDMDBUtil.getDistinctCount(selectQuery, "ManagedDevice", "RESOURCE_ID").optInt("DISTINCT_COUNT", 0);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while getBlackListApp count", ex);
        }
        return blackListCount;
    }
    
    public String getAgentValue(final int groupType, final String isExport) {
        String platformName = "";
        try {
            if (groupType == 1) {
                platformName = I18N.getMsg("dc.mdm.ios", new Object[0]);
            }
            else if (groupType == 2) {
                platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
            }
            else if (groupType == 3) {
                platformName = I18N.getMsg("dc.mdm.safe", new Object[0]);
            }
            else if (groupType == 4) {
                platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if (groupType == 8) {
                platformName = I18N.getMsg("mdm.os.MacOS", new Object[0]);
            }
            else if (groupType == 7) {
                platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getAgentValue....", ex);
        }
        return platformName;
    }
    
    public HashMap getDeviceDetailsFromUDID(final String udid) {
        HashMap deviceMap = null;
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join deviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            deviceQuery.addJoin(deviceJoin);
            deviceQuery.addJoin(extnJoin);
            deviceQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            deviceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            deviceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
            deviceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
            final Criteria udidCri = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
            deviceQuery.setCriteria(udidCri);
            ds = DMDataSetWrapper.executeQuery((Object)deviceQuery);
            if (ds.next()) {
                deviceMap = new HashMap();
                deviceMap.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                deviceMap.put("NAME", ds.getValue("NAME"));
                deviceMap.put("MANAGEDDEVICEEXTN.NAME", ds.getValue("MANAGEDDEVICEEXTN.NAME"));
                deviceMap.put("DOMAIN_NETBIOS_NAME", ds.getValue("DOMAIN_NETBIOS_NAME"));
                deviceMap.put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                deviceMap.put("UDID", ds.getValue("UDID"));
                deviceMap.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                deviceMap.put("AGENT_TYPE", ds.getValue("AGENT_TYPE"));
                deviceMap.put("MANAGED_STATUS", ds.getValue("MANAGED_STATUS"));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, " Exception thrown in getDeviceDetailsFromUDID ", ex);
        }
        return deviceMap;
    }
    
    public void addOrUpdateOSDetailsInTemp(final Long resourceID, final String sOSVersion) {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("MdOSDetailsTemp"));
            query.addSelectColumn(new Column("MdOSDetailsTemp", "*"));
            final Criteria criteria = new Criteria(new Column("MdOSDetailsTemp", "RESOURCE_ID"), (Object)resourceID, 0, false);
            query.setCriteria(criteria);
            final DataObject mdOSDetailsTempDO = getPersistence().get((SelectQuery)query);
            Row mdOSDetailsTempRow = null;
            if (mdOSDetailsTempDO.isEmpty()) {
                mdOSDetailsTempRow = new Row("MdOSDetailsTemp");
                mdOSDetailsTempRow.set("RESOURCE_ID", (Object)resourceID);
                mdOSDetailsTempRow.set("OS_VERSION", (Object)sOSVersion);
                mdOSDetailsTempDO.addRow(mdOSDetailsTempRow);
                getPersistence().add(mdOSDetailsTempDO);
            }
            else {
                mdOSDetailsTempRow = mdOSDetailsTempDO.getFirstRow("MdOSDetailsTemp");
                mdOSDetailsTempRow.set("OS_VERSION", (Object)sOSVersion);
                mdOSDetailsTempDO.updateRow(mdOSDetailsTempRow);
                getPersistence().update(mdOSDetailsTempDO);
            }
        }
        catch (final DataAccessException ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in addOrUpdateOSDetailsInTemp method : {0}", (Throwable)ex);
        }
    }
    
    public String getMDMPropertyForTracking() {
        final StringBuffer mdmProps = new StringBuffer();
        try {
            final int enroledDeviceRequestCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount();
            this.appendProps(mdmProps, enroledDeviceRequestCount, "edrc");
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
            this.appendProps(mdmProps, managedDeviceCount, "mdc");
            final int managediOSDeviceCount = ManagedDeviceHandler.getInstance().getAppleManagedDeviceCount();
            this.appendProps(mdmProps, managediOSDeviceCount, "midc");
            final int managedAndroidDeviceCount = ManagedDeviceHandler.getInstance().getAndroidManagedDeviceCount();
            this.appendProps(mdmProps, managedAndroidDeviceCount, "madc");
            final int managedSafeDeviceCount = ManagedDeviceHandler.getInstance().getSAFEDeviceCount();
            this.appendProps(mdmProps, managedSafeDeviceCount, "masdc");
            final int managedWindowsDeviceCount = ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount();
            this.appendProps(mdmProps, managedWindowsDeviceCount, "mwpdc");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        MDMUtil.logger.log(Level.INFO, "getMDMPropertyForTracking : {0} ", mdmProps.toString());
        return mdmProps.toString();
    }
    
    private void appendProps(final StringBuffer sdpProps, final int mdmCount, final String key) {
        if (mdmCount != -1) {
            sdpProps.append(key).append("-").append(mdmCount).append("|");
        }
    }
    
    public void updateSecurityCommandsStatus(final String deviceUDID, final String commandUUID, final int commandStatus, final Long customerID, final String remarksKey, final String englishRemarks) throws Exception {
        final String commandDisplayName = CommandUtil.getInstance().getCommandDisplayName(commandUUID);
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
        final Long userId = JSONUtil.optLongForUVH(statusJSON, "ADDED_BY", Long.valueOf(-1L));
        final String userName = DMUserHandler.getUserNameFromUserID(userId);
        if (resourceID != null) {
            final Object remarksArgs = commandDisplayName + "@@@" + ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
            String remarks;
            String actionLogRemarks;
            int dbStatusConstant;
            if (commandStatus == 200 || commandStatus == 202) {
                remarks = "mdm.scan.scanning_successful";
                actionLogRemarks = "dc.mdm.actionlog.securitycommands.success";
                dbStatusConstant = 2;
            }
            else if (commandUUID.equalsIgnoreCase("DeviceLock")) {
                remarks = "dc.db.mdm.collection.failed_lock";
                actionLogRemarks = "dc.db.mdm.collection.failed_lock";
                dbStatusConstant = 0;
            }
            else if (commandUUID.equalsIgnoreCase("ResetPasscode")) {
                remarks = "dc.db.mdm.collection.failed_reset_passcode";
                actionLogRemarks = "dc.db.mdm.collection.failed_reset_passcode";
                dbStatusConstant = 0;
            }
            else if (commandUUID.equalsIgnoreCase("GetLocation")) {
                remarks = (I18N.getMsg(remarksKey, new Object[0]).equalsIgnoreCase(remarksKey) ? englishRemarks : remarksKey);
                actionLogRemarks = "dc.mdm.actionlog.securitycommands.failure";
                dbStatusConstant = 0;
            }
            else if (commandUUID.equalsIgnoreCase("RestartDevice")) {
                remarks = "dc.mdm.collection.failed_device_reboot";
                actionLogRemarks = "dc.mdm.collection.failed_device_reboot";
                dbStatusConstant = 0;
            }
            else if (commandUUID.equalsIgnoreCase("UnlockUserAccount")) {
                remarks = "dc.mdm.actionlog.macunlockuser.failure";
                actionLogRemarks = "dc.mdm.actionlog.macunlockuser.failure";
                dbStatusConstant = 0;
            }
            else if (commandUUID.equalsIgnoreCase("MacFileVaultPersonalKeyRotate")) {
                remarks = "dc.mdm.actionlog.macunlockuser.failure";
                actionLogRemarks = "dc.mdm.actionlog.macunlockuser.failure";
                dbStatusConstant = 0;
            }
            else {
                remarks = "dc.common.SCAN_FAILED";
                actionLogRemarks = "dc.mdm.actionlog.securitycommands.failure";
                dbStatusConstant = 0;
            }
            statusJSON.put("COMMAND_STATUS", dbStatusConstant);
            statusJSON.put("REMARKS", (Object)remarks);
            statusJSON.put("REMARKS_ARGS", (Object)actionLogRemarks);
            statusJSON.put("RESOURCE_ID", (Object)resourceID);
            new CommandStatusHandler().populateCommandStatus(statusJSON);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, userName, actionLogRemarks, remarksArgs, customerID);
        }
    }
    
    public Long getProdCollectionIdFromAppGroupId(final Long appGroupId) {
        return this.getApprovedCollectionIdFromAppGroupId(appGroupId, Boolean.FALSE);
    }
    
    public Long getProdCollectionIdFromAppGroupIdNotInTrash(final Long appGroupId) {
        return this.getApprovedCollectionIdFromAppGroupId(appGroupId, Boolean.TRUE);
    }
    
    public Long getApprovedCollectionIdFromAppGroupId(final Long appGroupId, final Boolean appNotInTrashCriteria) {
        Long collectionId = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            sQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            Criteria appGrpCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
            if (appNotInTrashCriteria) {
                final Criteria trashCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0);
                appGrpCriteria = appGrpCriteria.and(trashCriteria);
            }
            sQuery.setCriteria(appGrpCriteria.and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria()));
            sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
            final DataObject appDo = getPersistence().get(sQuery);
            if (appDo != null && !appDo.isEmpty()) {
                final Row groupRow = appDo.getFirstRow("AppGroupToCollection");
                collectionId = (Long)groupRow.get("COLLECTION_ID");
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.INFO, "Exception in getting collectionID from app grp ID", e);
        }
        return collectionId;
    }
    
    public static void addOrupdateAgentLastContact(final Long resourceId, final Long contactTime, final String contactType, String loggedOnUsers, final Long lastBootUpTime, String activeLoggedOnUsers) {
        if (resourceId != null) {
            try {
                final Row acRow = new Row("AgentContact");
                acRow.set("RESOURCE_ID", (Object)resourceId);
                acRow.set("LAST_CONTACT_TIME", (Object)contactTime);
                acRow.set("LAST_BOOTUP_TIME", (Object)lastBootUpTime);
                if (contactType != null) {
                    acRow.set("CONTACT_TYPE", (Object)contactType);
                }
                if (loggedOnUsers == null || loggedOnUsers.equals("")) {
                    loggedOnUsers = "--";
                }
                acRow.set("LOGGED_ON_USERS", (Object)loggedOnUsers);
                if (activeLoggedOnUsers == null || activeLoggedOnUsers.equalsIgnoreCase("--")) {
                    activeLoggedOnUsers = loggedOnUsers;
                }
                acRow.set("ACTIVE_LOGGED_ON_USERS", (Object)activeLoggedOnUsers);
                final DataObject resultDO = getPersistence().get("AgentContact", acRow);
                if (resultDO.isEmpty()) {
                    resultDO.addRow(acRow);
                    getPersistence().add(resultDO);
                }
                else {
                    resultDO.updateRow(acRow);
                    getPersistence().update(resultDO);
                }
                if (ModernDeviceUtil.isModernManagementCapableResource(resourceId)) {
                    new ModernMgmtQueueOperation(3, new ModernMgmtContactTimeData(resourceId, contactTime)).addToModernMgmtOperationQueue();
                }
            }
            catch (final Exception ex) {
                MDMUtil.logger.log(Level.WARNING, ex, () -> "Caught Exception while updating Agent Last Contact details in DB for resourceId: " + n);
            }
        }
    }
    
    public int getPlatformType(final Long resourceId) {
        int platformType = -1;
        try {
            final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            final DataObject dObj = DataAccess.get("ManagedDevice", cri);
            if (!dObj.isEmpty()) {
                platformType = (int)dObj.getValue("ManagedDevice", "PLATFORM_TYPE", cri);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured in getPlatformType....", e);
        }
        return platformType;
    }
    
    public void forceDeleteonExitFolder(final String folderPath) {
        MDMUtil.logger.log(Level.FINE, "Inside forceDeleteonExitFolder()");
        MDMUtil.logger.log(Level.INFO, "Folder to be Deleted  :  {0}", folderPath);
        try {
            final File file = new File(folderPath);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in deleteFolder...", ex);
        }
    }
    
    public void deleteFolder(final String folderPath) {
        MDMUtil.logger.log(Level.FINE, "Inside deleteFolder()");
        MDMUtil.logger.log(Level.INFO, "Folder to be Deleted  :  {0}", folderPath);
        try {
            final File file = new File(folderPath);
            if (file.isDirectory()) {
                FileUtils.cleanDirectory(file);
                file.delete();
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in deleteFolder...", ex);
        }
    }
    
    public JSONObject getServerProps() throws Exception {
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        String serverIP = null;
        String serverPort = null;
        if (natProps.size() > 0) {
            serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            serverPort = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
        }
        else {
            final Properties serverProp = getDCServerInfo();
            serverIP = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_NAME");
            serverPort = String.valueOf(((Hashtable<K, Object>)serverProp).get("SERVER_PORT"));
        }
        final JSONObject json = new JSONObject();
        json.put("SERVER_IP", (Object)serverIP);
        json.put("SERVER_PORT", (Object)serverPort);
        return json;
    }
    
    public Reader getProperEncodedReader(final HttpServletRequest request, Reader reader) throws IOException {
        try {
            reader = new BufferedReader(new InputStreamReader((InputStream)request.getInputStream(), Charset.forName("UTF-8")));
            return reader;
        }
        catch (final IOException ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occurred while converting the HTTPServlet Request's InputStream to Reader in getProperEncodedReader() of SYMClientUtil ... ", ex);
            throw ex;
        }
    }
    
    public String getSupportFileUploadUrl() {
        final String baseURLStr = "/webclient#/uems/mdm/support";
        return baseURLStr;
    }
    
    public String getSupportFileUploadUrl(String supportMsg) {
        String url = "/webclient#/uems/mdm/support/supportFile";
        try {
            if (supportMsg != null && !MDMStringUtils.isEmpty(url)) {
                supportMsg = URLEncoder.encode(supportMsg, "UTF-8");
                url = url + "?message=" + supportMsg;
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while fetching support file url", ex);
        }
        return url;
    }
    
    public boolean isEmpty(final String str) {
        return SyMUtil.isStringEmpty(str);
    }
    
    public String getCommandUUIDFromCommandID(final Long commandID) {
        try {
            return (String)DBUtil.getValueFromDB("MdCommands", "COMMAND_ID", (Object)commandID, "COMMAND_UUID");
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception while get command uuid for id", e);
            return null;
        }
    }
    
    public static String getNormalizedStringAndDeleteFile(final File file) {
        try {
            Thread.sleep(1000L);
        }
        catch (final InterruptedException ex) {
            MDMUtil.logger.log(Level.SEVERE, null, ex);
        }
        if (!file.exists() || file.length() == 0L) {
            return String.valueOf("-1");
        }
        try {
            final boolean readOnlyAscii = true;
            final String normalizedString = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(file.getCanonicalPath(), readOnlyAscii);
            MDMUtil.logger.log(Level.INFO, "normalized json String is {0}", normalizedString);
            try {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(file.getAbsolutePath());
            }
            catch (final Exception ex2) {
                MDMUtil.logger.log(Level.SEVERE, "exception occured while deleting file", ex2);
            }
            return normalizedString;
        }
        catch (final IOException ex3) {
            MDMUtil.logger.log(Level.SEVERE, null, ex3);
            return "-1";
        }
    }
    
    public static Object getNormalizedJSONAndDeleteFile(final File file) {
        final String normalizedString = getNormalizedStringAndDeleteFile(file);
        try {
            if (normalizedString.equals("-1")) {
                return null;
            }
            return new JSONParser().parse(normalizedString);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "exception occured while converting string to JSON : {0}", ex.getMessage());
            return normalizedString;
        }
    }
    
    public static void setDomainValFailedAttributes() {
        final String sourceMethod = "setDomainValFailedAttributes";
        try {
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            MessageProvider.getInstance().hideMessage("UNABLE_TO_REACH_DOMAINS_MDM");
            if (proxyType != 3) {
                if (SyMUtil.getSyMParameter("mdm_domain_validation") != null) {
                    final String mdmDomainStatus = SyMUtil.getSyMParameter("mdm_domain_validation");
                    if (mdmDomainStatus.equals("failed")) {
                        MessageProvider.getInstance().unhideMessage("UNABLE_TO_REACH_DOMAINS_MDM");
                    }
                    else {
                        MessageProvider.getInstance().hideMessage("UNABLE_TO_REACH_DOMAINS_MDM");
                    }
                }
            }
            else {
                MDMUtil.logger.log(Level.INFO, "{0} No Internet connection configured", sourceMethod);
            }
        }
        catch (final Exception exp) {
            MDMUtil.logger.log(Level.WARNING, exp, () -> s + "--> Exception while adding customer");
        }
    }
    
    public static String replaceProductUrlLoaderValuesinText(String textStr, final String pageSource) {
        if (textStr.contains("$(traceurl)")) {
            textStr = textStr.replaceAll("\\$\\(traceurl\\)", ProductUrlLoader.getInstance().getValue("trackingcode"));
        }
        if (textStr.contains("$(mdmUrl)")) {
            textStr = textStr.replaceAll("\\$\\(mdmUrl\\)", ProductUrlLoader.getInstance().getValue("mdmUrl"));
        }
        if (textStr.contains("$(prodUrl)")) {
            textStr = textStr.replaceAll("\\$\\(prodUrl\\)", ProductUrlLoader.getInstance().getValue("prodUrl"));
        }
        if (textStr.contains("$(did)")) {
            textStr = textStr.replaceAll("\\$\\(did\\)", "did=" + (String)ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2));
        }
        if (pageSource != null && textStr.contains("$(pageSource)")) {
            textStr = textStr.replaceAll("\\$\\(pageSource\\)", pageSource);
        }
        return textStr;
    }
    
    public static void addOrIncrementClickCountForView(final String viewName) {
        Integer finalCount = 1;
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            if (customerId != null) {
                final String countObj = CustomerParamsHandler.getInstance().getParameterValue(viewName, (long)customerId);
                if (countObj != null) {
                    finalCount = Integer.parseInt(countObj) + 1;
                }
                CustomerParamsHandler.getInstance().addOrUpdateParameter(viewName, finalCount.toString(), (long)customerId);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, ex, () -> "Exception in addOrIncrementClickCountForView " + s + " is the view name {0}");
        }
    }
    
    public HashSet<Long> parseDevicesFromJSON(final JSONObject requestJSON) throws SyMException, JSONException {
        List<Long> resourceList = new ArrayList<Long>();
        if (requestJSON.has("imeis")) {
            final JSONArray imeiArray = requestJSON.getJSONArray("imeis");
            final List imeiList = JSONUtil.getInstance().convertJSONArrayTOList(imeiArray);
            imeiList.removeIf(imei -> imei == null || "".equals(imei) || "--".equals(imei));
            final String[] imeis = imeiList.toArray(new String[imeiList.size()]);
            resourceList = ManagedDeviceHandler.getInstance().getManagedResourcesWithSIMInfo(new Criteria(Column.getColumn("MdSIMInfo", "IMEI"), (Object)imeis, 8));
        }
        else if (requestJSON.has("serial_numbers")) {
            final JSONArray slnoArray = requestJSON.getJSONArray("serial_numbers");
            final String[] slnos = JSONUtil.getInstance().convertJSONArrayTOList(slnoArray).toArray(new String[slnoArray.length()]);
            resourceList = ManagedDeviceHandler.getInstance().getManagedResourcesWithDeviceInfo(new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)slnos, 8));
        }
        else if (requestJSON.has("device_ids")) {
            final JSONArray deviceIDArray = requestJSON.getJSONArray("device_ids");
            final Long[] deviceIds = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIDArray).toArray(new Long[deviceIDArray.length()]);
            resourceList = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceIds, 8));
        }
        if (resourceList.isEmpty()) {
            throw new SyMException(404, "Device not found", (Throwable)null);
        }
        return new HashSet<Long>(resourceList);
    }
    
    public HashSet<Long> parseGroupsFromJSON(final JSONObject requestJSON) throws SyMException {
        try {
            final HashSet<Long> groupList = new HashSet<Long>();
            if (requestJSON.has("group_ids")) {
                final JSONArray groupArray = requestJSON.getJSONArray("group_ids");
                for (int i = 0; i < groupArray.length(); ++i) {
                    groupList.add(Long.valueOf(groupArray.get(i).toString()));
                }
            }
            return groupList;
        }
        catch (final JSONException ex) {
            throw new SyMException(400, (String)null, (Throwable)null);
        }
    }
    
    public Boolean isiOS11FeaturesEnabled() {
        return MDMFeatureParamsHandler.getInstance().isFeatureAvailableGlobally("enableiOS11Feature", false);
    }
    
    public static String generateNewRandomToken(final String tableName, final String criteriaColumn, final String outputColumn) {
        final int maxRetryCount = 5;
        String randomToken = null;
        Long value = -1L;
        for (int i = 0; i < maxRetryCount && value != null; ++i) {
            final SecureRandom random = new SecureRandom();
            randomToken = new BigInteger(32, random).toString(16);
            try {
                value = (Long)DBUtil.getValueFromDB(tableName, criteriaColumn, (Object)randomToken, outputColumn);
            }
            catch (final Exception e) {
                MDMUtil.logger.log(Level.SEVERE, "Exception in Random Token generation", e);
            }
        }
        return randomToken;
    }
    
    public List parseStringForElements(final String str) {
        final StringTokenizer st = new StringTokenizer(str, ",");
        final List listRet = new ArrayList();
        while (st.hasMoreTokens()) {
            listRet.add(st.nextToken());
        }
        return listRet;
    }
    
    public boolean updateFreeEditionDetails(final String mobileUserIds) {
        boolean updated = false;
        try {
            final Long currentlyLoggedInUserLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (mobileUserIds != null) {
                Criteria criteria = null;
                Criteria selectedCriteria = null;
                final String[] mobileUserIDArr = mobileUserIds.split(",");
                final String loggedInUserName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
                MDMUtil.logger.log(Level.INFO, "mobileUserIDArr.length :{0}", mobileUserIDArr.length);
                if (mobileUserIDArr.length > 0) {
                    final Long[] arrMobileUserID = new Long[mobileUserIDArr.length];
                    if (!mobileUserIDArr[0].equals("")) {
                        for (int i = 0; i < mobileUserIDArr.length; ++i) {
                            arrMobileUserID[i] = Long.valueOf(mobileUserIDArr[i]);
                            MDMUtil.logger.log(Level.INFO, arrMobileUserID[i].toString());
                        }
                        selectedCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)arrMobileUserID, 8);
                    }
                    final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
                    final String[] joinCol1 = { "ENROLLMENT_REQUEST_ID" };
                    final String[] joinCol2 = { "ENROLLMENT_REQUEST_ID" };
                    final Join join = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", joinCol1, joinCol2, 1);
                    query.addJoin(join);
                    final String[] joinCol3 = { "MANAGED_DEVICE_ID" };
                    final String[] joinCol4 = { "RESOURCE_ID" };
                    final Join join2 = new Join("EnrollmentRequestToDevice", "ManagedDevice", joinCol3, joinCol4, 1);
                    query.addJoin(join2);
                    query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                    final Criteria enrolled = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                    final Criteria awaitingLicense = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)6, 0);
                    final Criteria waitingForUserAssignment = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0);
                    if (selectedCriteria == null) {
                        selectedCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)new Long[0], 8);
                    }
                    criteria = selectedCriteria.or(waitingForUserAssignment.or(enrolled.or(awaitingLicense)));
                    query.setCriteria(criteria.negate());
                    DataObject dObj = DataAccess.get(query);
                    Iterator item = dObj.getRows("DeviceEnrollmentRequest");
                    while (item.hasNext()) {
                        final Row row = item.next();
                        final Long reqId = (Long)row.get("ENROLLMENT_REQUEST_ID");
                        final Long userId = (Long)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)reqId, "MANAGED_USER_ID");
                        final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(userId);
                        MDMEnrollmentUtil.getInstance().removeDevice(String.valueOf(reqId), loggedInUserName, customerID);
                    }
                    MDMUtil.logger.log(Level.INFO, "Mobile devices got removed Successfully");
                    final Criteria stagedCriteria = enrolled.and(selectedCriteria.negate());
                    query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                    query.setCriteria(stagedCriteria);
                    dObj = DataAccess.get(query);
                    item = dObj.getRows("DeviceEnrollmentRequest");
                    final List erid = DBUtil.getColumnValuesAsList(item, "ENROLLMENT_REQUEST_ID");
                    final JSONArray resourceIds = new JSONArray((Collection)ManagedDeviceHandler.getInstance().getManagedDeviceIdFromErids(erid, "RESOURCE_ID"));
                    final JSONObject managedDeviceDetails = new JSONObject();
                    managedDeviceDetails.put("MANAGED_STATUS", 6);
                    managedDeviceDetails.put("REMARKS", (Object)"dc.db.mdm.managedStatus.waiting_for_license");
                    managedDeviceDetails.put("customer_id", (Object)CustomerInfoUtil.getInstance().getCustomerId());
                    managedDeviceDetails.put("resourceIds", (Object)resourceIds);
                    managedDeviceDetails.put("requestIds", (Collection)erid);
                    ManagedDeviceHandler.getInstance().bulkUpdateManagedDeviceDetails(managedDeviceDetails);
                    ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
                    MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
                }
                updated = true;
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, " Exception occurred while removing Mobile Devices ", ex);
        }
        return updated;
    }
    
    public void addOrUpdateCollnToResources(final Long resourceId, final Long collectionID, final int collStatus, final String remarks) throws Exception {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
            selectQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 1));
            final Criteria cResource = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cCollection = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
            selectQuery.setCriteria(cResource.and(cCollection));
            selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MDMCollnToResErrorCode", "*"));
            final DataObject existRelDO = DataAccess.get(selectQuery);
            Row collectionRow = existRelDO.getRow("CollnToResources");
            final Row collectionErrorRow = existRelDO.getRow("MDMCollnToResErrorCode");
            final DataObject finalDO = DataAccess.constructDataObject();
            if (collectionRow == null) {
                MDMUtil.logger.log(Level.INFO, "Inserting resource {0} and collection {1} in CollnToResources", new Object[] { resourceId, collectionID });
                collectionRow = new Row("CollnToResources");
                collectionRow.set("COLLECTION_ID", (Object)collectionID);
                collectionRow.set("RESOURCE_ID", (Object)resourceId);
                collectionRow.set("STATUS", (Object)collStatus);
                collectionRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                collectionRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                collectionRow.set("REMARKS", (Object)remarks);
                collectionRow.set("REMARKS_EN", (Object)"--");
                finalDO.addRow(collectionRow);
            }
            else {
                collectionRow.set("STATUS", (Object)collStatus);
                collectionRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                collectionRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                collectionRow.set("REMARKS", (Object)remarks);
                collectionRow.set("REMARKS_EN", (Object)"--");
                finalDO.updateBlindly(collectionRow);
            }
            if (collectionErrorRow != null) {
                finalDO.deleteRow(collectionErrorRow);
            }
            DataAccess.update(finalDO);
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in AddOrUpdateCollnToResource", e);
            throw e;
        }
    }
    
    public String getCurrentlyLoggedInUserEmail() throws Exception {
        String email = "";
        try {
            final Long userID = getInstance().getCurrentlyLoggedOnUserID();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaContactInfo"));
            sq.addJoin(new Join("AaaContactInfo", "AaaUserContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            sq.addJoin(new Join("AaaUserContactInfo", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            final Criteria resCriteria = new Criteria(new Column("AaaUser", "USER_ID"), (Object)userID, 0);
            sq.setCriteria(resCriteria);
            sq.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
            sq.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
            sq.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
            final DataObject DO = getPersistenceLite().get(sq);
            final Row row = DO.getFirstRow("AaaContactInfo");
            email = (String)row.get("EMAILID");
        }
        catch (final Exception exp) {
            MDMUtil.logger.log(Level.SEVERE, " Exception occurred while getCurrentlyLoggedInUserEmail ", exp);
        }
        return email;
    }
    
    public void addModernMgmtAAARolesForExistingUmRoles() throws QueryConstructionException, DataAccessException {
        MDMUtil.logger.log(Level.INFO, "Going to migrate UmRoles and AaaAuthRoles by adding ModernMgmt roles.");
        try {
            final Map<String, Long> aaaRoleNameToUmModule = new HashMap<String, Long>();
            final SelectQuery umModuleQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UMModule"));
            umModuleQuery.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            umModuleQuery.addSelectColumn(Column.getColumn("UMModule", "UM_MODULE_ID"));
            umModuleQuery.addSelectColumn(Column.getColumn("UMModule", "ROLE_ID"));
            umModuleQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            umModuleQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            umModuleQuery.setCriteria(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"ModernMgmt_", 10, (boolean)Boolean.FALSE));
            final DataObject umModuleDO = getPersistence().get(umModuleQuery);
            final Iterator<Row> umModuleRows = umModuleDO.getRows("UMModule");
            while (umModuleRows.hasNext()) {
                final Row umModuleRow = umModuleRows.next();
                final Long aaaRoleId = (Long)umModuleRow.get("ROLE_ID");
                final Long umModuleId = (Long)umModuleRow.get("UM_MODULE_ID");
                final Row aaaRoleRow = umModuleDO.getRow("AaaRole", new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), (Object)aaaRoleId, 0));
                final String aaaRoleName = (String)aaaRoleRow.get("NAME");
                aaaRoleNameToUmModule.put(aaaRoleName, umModuleId);
            }
            final SelectQuery umRoleModuleRelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UMRoleModuleRelation"));
            final Join umModuleJoin = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2);
            final Join aaaRoleJoin = new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            umRoleModuleRelQuery.addJoin(umModuleJoin);
            umRoleModuleRelQuery.addJoin(aaaRoleJoin);
            umRoleModuleRelQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"));
            umRoleModuleRelQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "UM_MODULE_ID"));
            umRoleModuleRelQuery.addSelectColumn(Column.getColumn("UMModule", "UM_MODULE_ID"));
            umRoleModuleRelQuery.addSelectColumn(Column.getColumn("UMModule", "ROLE_ID"));
            umRoleModuleRelQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            umRoleModuleRelQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            Criteria aaaRoleNameStartsWithMDMCri = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_", 10, (boolean)Boolean.FALSE);
            final Criteria aaaRoleNameNotMdmClientAdmin = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_Client_Admin", 1, (boolean)Boolean.FALSE);
            aaaRoleNameStartsWithMDMCri = aaaRoleNameStartsWithMDMCri.and(aaaRoleNameNotMdmClientAdmin);
            final Criteria aaaRoleNameStartsWithModernMgmtCri = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"ModernMgmt_", 10, (boolean)Boolean.FALSE);
            umRoleModuleRelQuery.setCriteria(aaaRoleNameStartsWithMDMCri.or(aaaRoleNameStartsWithModernMgmtCri));
            final DataObject umRoleModuleDO = getPersistence().get(umRoleModuleRelQuery);
            final DataObject umRoleModuleDOClone = (DataObject)umRoleModuleDO.clone();
            final Iterator<Row> umRoleModuleRows = umRoleModuleDO.getRows("UMRoleModuleRelation");
            while (umRoleModuleRows.hasNext()) {
                final Row umRolemoduleRow = umRoleModuleRows.next();
                final Long umModuleId2 = (Long)umRolemoduleRow.get("UM_MODULE_ID");
                final Long umRoleId = (Long)umRolemoduleRow.get("UM_ROLE_ID");
                final Row umModuleRow2 = umRoleModuleDO.getRow("UMModule", new Criteria(Column.getColumn("UMModule", "UM_MODULE_ID"), (Object)umModuleId2, 0));
                final Long aaaRoleId2 = (Long)umModuleRow2.get("ROLE_ID");
                final Row aaaRoleRow2 = umRoleModuleDO.getRow("AaaRole", new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), (Object)aaaRoleId2, 0));
                final String aaaRoleName2 = (String)aaaRoleRow2.get("NAME");
                final String modernMgmtRoleName = aaaRoleName2.replace("MDM_", "ModernMgmt_");
                final Long modernMgmtUmModuleId = aaaRoleNameToUmModule.get(modernMgmtRoleName);
                if (modernMgmtUmModuleId != null) {
                    final Row modernMgmtUmRoleModuleRow = new Row("UMRoleModuleRelation");
                    modernMgmtUmRoleModuleRow.set("UM_ROLE_ID", (Object)umRoleId);
                    modernMgmtUmRoleModuleRow.set("UM_MODULE_ID", (Object)modernMgmtUmModuleId);
                    if (umRoleModuleDO.findRow(modernMgmtUmRoleModuleRow) != null) {
                        continue;
                    }
                    umRoleModuleDOClone.addRow(modernMgmtUmRoleModuleRow);
                }
            }
            getPersistence().update(umRoleModuleDOClone);
            MDMUtil.logger.log(Level.INFO, "Migration successfully completed for UMRoleModuleRelation table");
            MDMUtil.logger.log(Level.INFO, "Going to migrate AaaAuthorizedRole table by adding entries for ModernMgmt AaaRoles");
            final Map<Long, Long> aaaRoleToModernMgmtAaaRole = new HashMap<Long, Long>();
            final SelectQuery aaaRoleQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
            aaaRoleQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            aaaRoleQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            aaaRoleQuery.setCriteria(aaaRoleNameStartsWithMDMCri.or(aaaRoleNameStartsWithModernMgmtCri));
            final DataObject aaaRoleDO = getPersistence().get(aaaRoleQuery);
            final Iterator<Row> aaaRoleRows = aaaRoleDO.getRows("AaaRole");
            while (aaaRoleRows.hasNext()) {
                final Row aaaRoleRow3 = aaaRoleRows.next();
                final Long aaaRoleId3 = (Long)aaaRoleRow3.get("ROLE_ID");
                final String aaaRoleName2 = (String)aaaRoleRow3.get("NAME");
                if (aaaRoleName2.startsWith("MDM_")) {
                    final String modernMgmtRoleName = aaaRoleName2.replace("MDM_", "ModernMgmt_");
                    final Row modernMgmtRoleRow = aaaRoleDO.getRow("AaaRole", new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)modernMgmtRoleName, 0, (boolean)Boolean.FALSE));
                    if (modernMgmtRoleRow == null) {
                        continue;
                    }
                    final Long modernMgmtRoleId = (Long)modernMgmtRoleRow.get("ROLE_ID");
                    aaaRoleToModernMgmtAaaRole.put(aaaRoleId3, modernMgmtRoleId);
                }
            }
            final SelectQuery aaaAuthroizedRoleQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAuthorizedRole"));
            aaaAuthroizedRoleQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            aaaAuthroizedRoleQuery.addSelectColumn(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"));
            aaaAuthroizedRoleQuery.addSelectColumn(Column.getColumn("AaaAuthorizedRole", "ROLE_ID"));
            aaaAuthroizedRoleQuery.setCriteria(aaaRoleNameStartsWithMDMCri.or(aaaRoleNameStartsWithModernMgmtCri));
            final DataObject aaaAuthRoleDO = getPersistence().get(aaaAuthroizedRoleQuery);
            final Iterator<Row> aaaAuthRoleRows = aaaAuthRoleDO.getRows("AaaAuthorizedRole", new Criteria(Column.getColumn("AaaAuthorizedRole", "ROLE_ID"), (Object)aaaRoleToModernMgmtAaaRole.keySet().toArray(new Long[1]), 8));
            while (aaaAuthRoleRows.hasNext()) {
                final Row aaaAuthRoleRow = aaaAuthRoleRows.next();
                final Long aaaRoleId4 = (Long)aaaAuthRoleRow.get("ROLE_ID");
                final Long aaaAccountId = (Long)aaaAuthRoleRow.get("ACCOUNT_ID");
                final Long modernMgmtRoleId2 = aaaRoleToModernMgmtAaaRole.get(aaaRoleId4);
                Row modernMgmtAaaAuthRoleRow = aaaAuthRoleDO.getRow("AaaAuthorizedRole", new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)aaaAccountId, 0).and(new Criteria(Column.getColumn("AaaAuthorizedRole", "ROLE_ID"), (Object)modernMgmtRoleId2, 0)));
                if (modernMgmtAaaAuthRoleRow == null) {
                    modernMgmtAaaAuthRoleRow = new Row("AaaAuthorizedRole");
                    modernMgmtAaaAuthRoleRow.set("ACCOUNT_ID", (Object)aaaAccountId);
                    modernMgmtAaaAuthRoleRow.set("ROLE_ID", (Object)modernMgmtRoleId2);
                    aaaAuthRoleDO.addRow(modernMgmtAaaAuthRoleRow);
                }
            }
            getPersistence().update(aaaAuthRoleDO);
            MDMUtil.logger.log(Level.INFO, "Migration completed successfully for AaaAuthorizedRole tables by adding ModernMgmt roles");
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while migrating existing UMRoles and AaaAuthRoles by adding ModernMgmt roles", ex);
            throw ex;
        }
    }
    
    public String[] populateRoleListOverridedValue(final String[] roleList) throws DataAccessException {
        final Set<String> overridedRoleList = new HashSet<String>();
        final SelectQuery aaaRoleListQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
        aaaRoleListQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
        aaaRoleListQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
        aaaRoleListQuery.setCriteria(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_", 10, (boolean)Boolean.FALSE).or(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"ModernMgmt_", 10, (boolean)Boolean.FALSE)));
        final DataObject aaaRoleDO = getPersistence().get(aaaRoleListQuery);
        for (final String sRoleId : roleList) {
            overridedRoleList.add(sRoleId);
            final Long roleId = Long.valueOf(sRoleId);
            final Criteria userSelectedRoleCri = new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), (Object)roleId, 0);
            final Row aaaRoleRow = aaaRoleDO.getRow("AaaRole", userSelectedRoleCri);
            if (aaaRoleRow != null) {
                String aaaRoleName = (String)aaaRoleRow.get("NAME");
                if (aaaRoleName.contains("MDM_Configurations") || aaaRoleName.contains("MDM_AppMgmt") || aaaRoleName.contains("MDM_Inventory") || aaaRoleName.contains("MDM_Report") || aaaRoleName.contains("MDM_Enrollment") || aaaRoleName.contains("MDM_Compliance") || aaaRoleName.contains("MDM_Geofence")) {
                    if (aaaRoleName.contains("MDM_Inventory")) {
                        final List geofencingRoleList = this.getGeoFenceRoleList(aaaRoleName, 1, aaaRoleDO);
                        for (final Object roleIdString : geofencingRoleList) {
                            overridedRoleList.add(String.valueOf(roleIdString));
                        }
                    }
                    else if (aaaRoleName.contains("ModernMgmt_Inventory")) {
                        final List geofencingRoleList = this.getGeoFenceRoleList(aaaRoleName, 2, aaaRoleDO);
                        for (final Object roleIdString : geofencingRoleList) {
                            overridedRoleList.add(String.valueOf(roleIdString));
                        }
                    }
                    aaaRoleName = aaaRoleName.replace("MDM_", "ModernMgmt_");
                    final Criteria modernMgmtRoleCri = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)aaaRoleName, 0, false);
                    final Long modernMgmtRoleId = (Long)aaaRoleDO.getRow("AaaRole", modernMgmtRoleCri).get("ROLE_ID");
                    overridedRoleList.add(String.valueOf(modernMgmtRoleId));
                }
            }
        }
        return overridedRoleList.toArray(new String[overridedRoleList.size()]);
    }
    
    public static String getDate(final Long time, final boolean includeTime) {
        if (includeTime) {
            return getDate((long)time);
        }
        String timeStr = time.toString();
        try {
            final String dateFormat = DMUserHandler.getUserDateFormat();
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            timeStr = Utils.getTime(time, dateFormat, locale, timeZone);
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while getting time", ex);
        }
        return timeStr;
    }
    
    public static String getDuration(Long millis) {
        try {
            final Long seconds = millis / 1000L;
            Integer secondsInt = seconds.intValue();
            millis %= 1000L;
            Integer minutes = secondsInt / 60;
            secondsInt %= 60;
            final Integer hours = minutes / 60;
            minutes %= 60;
            String duration = "";
            if (!hours.equals(0)) {
                duration = hours.toString() + " " + I18N.getMsg("dc.common.HOURS", new Object[0]) + ", ";
            }
            if (!minutes.equals(0)) {
                duration = duration + minutes.toString() + " " + I18N.getMsg("dc.common.MINUTES", new Object[0]) + ", ";
            }
            if (!duration.isEmpty()) {
                duration = duration.substring(0, duration.length() - 2);
            }
            return duration;
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception while getting duration", ex);
            return "--";
        }
    }
    
    public List<List> splitListIntoSubLists(final List list, final int chunkSize) {
        final List parts = new ArrayList();
        for (int N = list.size(), i = 0; chunkSize > 0 && i < N; i += chunkSize) {
            parts.add(list.subList(i, Math.min(N, i + chunkSize)));
        }
        return parts;
    }
    
    public List deepCloneList(final List list) {
        final List clonedList = new ArrayList(list.size());
        for (final Object element : list) {
            clonedList.add(element);
        }
        return clonedList;
    }
    
    public boolean isMDMQueueSplitAvailableGlobally() {
        return MDMFeatureParamsHandler.getInstance().isFeatureAvailableGlobally("MdmDataQueueSplit", false);
    }
    
    public List getGeoFenceRoleList(final String aaaRoleName, final int roleType, final DataObject aaaRoleDO) throws DataAccessException {
        Criteria selectCriteria = null;
        final List selectList = new ArrayList();
        final List roleList = new ArrayList();
        if (roleType == 1) {
            if (aaaRoleName.contains("_Write")) {
                selectList.add("MDM_Geofence_Write");
                selectList.add("MDM_Compliance_Write");
                selectCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)selectList.toArray(), 8);
            }
            else if (aaaRoleName.contains("_Read")) {
                selectList.add("MDM_Geofence_Read");
                selectList.add("MDM_Compliance_Read");
                selectCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)selectList.toArray(), 8);
            }
            else if (aaaRoleName.contains("_Admin")) {
                selectList.add("MDM_Geofence_Admin");
                selectList.add("MDM_Compliance_Admin");
                selectCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)selectList.toArray(), 8);
            }
            if (selectCriteria != null) {
                final Iterator iterator = aaaRoleDO.getRows("AaaRole", selectCriteria);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long roleId = (Long)row.get("ROLE_ID");
                    roleList.add(String.valueOf(roleId));
                }
            }
        }
        else if (roleType == 2) {
            if (aaaRoleName.contains("_Write")) {
                selectList.add("ModernMgmt_Geofence_Write");
                selectList.add("ModernMgmt_Compliance_Write");
                selectCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)selectList.toArray(), 8);
            }
            else if (aaaRoleName.contains("_Read")) {
                selectList.add("ModernMgmt_Geofence_Read");
                selectList.add("ModernMgmt_Compliance_Read");
                selectCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)selectList.toArray(), 8);
            }
            else if (aaaRoleName.contains("_Admin")) {
                selectList.add("ModernMgmt_Geofence_Admin");
                selectList.add("ModernMgmt_Compliance_Admin");
                selectCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)selectList.toArray(), 8);
            }
            if (selectCriteria != null) {
                final Iterator iterator = aaaRoleDO.getRows("AaaRole");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long roleId = (Long)row.get("ROLE_ID");
                    roleList.add(String.valueOf(roleId));
                }
            }
        }
        return roleList;
    }
    
    public static void addCommandToThreadLocal(final Long commandID) {
        Set<Long> appliedCommands = MDMUtil.appliedCommandList.get();
        if (appliedCommands == null) {
            appliedCommands = new HashSet<Long>();
            MDMUtil.appliedCommandList.set(appliedCommands);
        }
        appliedCommands.add(commandID);
    }
    
    public static Set<Long> getAndDeleteCommandsFromThreadLocal() {
        final Set<Long> appliedCommands = MDMUtil.appliedCommandList.get();
        MDMUtil.appliedCommandList.set(new HashSet<Long>());
        return appliedCommands;
    }
    
    public ArrayList getSecurityCommandList() {
        final ArrayList commandList = new ArrayList();
        commandList.add("DeviceLock");
        commandList.add("EraseDevice");
        commandList.add("CorporateWipe");
        commandList.add("ClearPasscode");
        commandList.add("GetLocation");
        commandList.add("ResetPasscode");
        commandList.add("DeviceRing");
        commandList.add("RemoteAlarm");
        commandList.add("PlayLostModeSound");
        commandList.add("ShutDownDevice");
        commandList.add("RestartDevice");
        commandList.add("UnlockUserAccount");
        commandList.add("FetchLocation");
        commandList.add("EnableLostMode");
        commandList.add("DisableLostMode");
        commandList.add("AssetScan");
        commandList.add("AssetScanContainer");
        commandList.add("PauseKioskCommand");
        commandList.add("ResumeKioskCommand");
        commandList.add("RemoteSession");
        commandList.add("RemoteDebug");
        commandList.add("ClearAppData");
        commandList.add("DeviceInformation");
        commandList.add("AndroidInvScan");
        commandList.add("AndroidInvScanContainer");
        commandList.add("CreateContainer");
        commandList.add("RemoveContainer");
        commandList.add("ContainerLock");
        commandList.add("ContainerUnlock");
        commandList.add("ClearContainerPasscode");
        commandList.add("ActivateKnox");
        commandList.add("DeactivateKnox");
        commandList.add("MacFileVaultPersonalKeyRotate");
        commandList.add("LostModeDeviceLocation");
        commandList.add("LogOutUser");
        return commandList;
    }
    
    public ArrayList getScanCommandList() {
        final ArrayList commandList = new ArrayList();
        commandList.add("AssetScan");
        commandList.add("AssetScanContainer");
        commandList.add("DeviceInformation");
        commandList.add("AndroidInvScan");
        commandList.add("AndroidInvScanContainer");
        return commandList;
    }
    
    public static JSONArray executeSelectQueryAndGetOrgJSONArray(final SelectQuery selectQuery) {
        Connection connection = null;
        DataSet dataSet = null;
        JSONArray dsJSArray = new JSONArray();
        try {
            connection = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, connection);
            dsJSArray = convertDSToJSONArray(dataSet, selectQuery.getSelectColumns());
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in executeSelectQuery", ex);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(connection, dataSet);
        }
        return dsJSArray;
    }
    
    private static JSONArray convertDSToJSONArray(final DataSet dataSet, final List columns) throws SQLException {
        final JSONArray dsJSArray = new JSONArray();
        while (dataSet.next()) {
            final JSONObject jsObject = new JSONObject();
            for (int i = 0; i < columns.size(); ++i) {
                final Column column = columns.get(i);
                try {
                    if (dataSet.getValue(column.getColumnAlias()) != null) {
                        jsObject.put(column.getColumnAlias(), dataSet.getValue(column.getColumnAlias()));
                    }
                    else {
                        jsObject.put(column.getColumnAlias(), (Object)"NULL_VALUE");
                    }
                }
                catch (final Exception ex) {
                    MDMUtil.logger.log(Level.WARNING, "exception in convertDataSetToJSONArray", ex);
                    try {
                        if (dataSet.getValue(column.getColumnAlias().toLowerCase()) != null) {
                            jsObject.put(column.getColumnAlias(), dataSet.getValue(column.getColumnAlias().toLowerCase()));
                        }
                        else {
                            jsObject.put(column.getColumnAlias(), (Object)"NULL_VALUE");
                        }
                    }
                    catch (final Exception e) {
                        MDMUtil.logger.log(Level.SEVERE, "exception in convertDataSetToJSONArray", e);
                    }
                }
            }
            dsJSArray.put((Object)jsObject);
        }
        return dsJSArray;
    }
    
    public String removeParamsFromURL(final String url, final List<String> params) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(url);
        final List<NameValuePair> queryParameters = uriBuilder.getQueryParams();
        final Iterator<NameValuePair> queryParameterItr = queryParameters.iterator();
        while (queryParameterItr.hasNext()) {
            final NameValuePair queryParameter = queryParameterItr.next();
            if (params.contains(queryParameter.getName())) {
                queryParameterItr.remove();
            }
        }
        uriBuilder.setParameters((List)queryParameters);
        return uriBuilder.build().toString();
    }
    
    public Long getLastScanTime(final Long resourceId) {
        Long scanTime = -1L;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceScanStatus"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "LAST_SUCCESSFUL_SCAN"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceId, 0));
            final DataObject dataObject = getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                scanTime = (Long)dataObject.getFirstRow("MdDeviceScanStatus").get("LAST_SUCCESSFUL_SCAN");
            }
        }
        catch (final DataAccessException e) {
            MDMUtil.logger.log(Level.SEVERE, "exception in getLastScanTime", (Throwable)e);
        }
        return scanTime;
    }
    
    public Long getLastContactTime(final Long resourceId) {
        Long lastContactTime = -1L;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentContact"));
            selectQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AgentContact", "RESOURCE_ID"), (Object)resourceId, 0));
            final DataObject dataObject = getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                lastContactTime = (Long)dataObject.getFirstRow("AgentContact").get("LAST_CONTACT_TIME");
            }
        }
        catch (final DataAccessException e) {
            MDMUtil.logger.log(Level.SEVERE, "exception in getLastScanTime", (Throwable)e);
        }
        return lastContactTime;
    }
    
    public void validateViewSearchText(final String colName, final String colVal) throws SyMException {
        final Pattern safestringregex = Pattern.compile("^[a-zA-Z0-9\\s\\+?!,()@%.\\'\\-:_*\\./\\\\=]+$");
        if (!safestringregex.matcher(colVal).matches()) {
            throw new SyMException(51035, "Invalid search value: Search text:" + colVal, (Throwable)null);
        }
        if (!safestringregex.matcher(colName).matches()) {
            throw new SyMException(51035, "Invalid search column: Column name:" + colName, (Throwable)null);
        }
    }
    
    public String sanitizeViewSearchText(final String searchText) {
        return searchText.replaceAll(";", "");
    }
    
    public static List<String> getColumnNamesFromQuery(final SelectQuery selectQuery) {
        final List<String> columnList = new ArrayList<String>();
        try {
            if (selectQuery != null) {
                final List<Column> queryColumns = selectQuery.getSelectColumns();
                for (final Column column : queryColumns) {
                    final String columnName = getColumnName(column);
                    columnList.add(columnName);
                }
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in get select columns", e);
        }
        return columnList;
    }
    
    static String getColumnName(final Column c) throws Exception {
        String name = c.getColumnAlias();
        if (c != null && name == null) {
            name = c.getColumnName();
        }
        if (c != null && name == null) {
            MDMUtil.logger.log(Level.INFO, "Column name is null:{0}", c);
        }
        return name;
    }
    
    public JSONObject getColumnDetails(final Long columnID) {
        final JSONObject columnDetails = new JSONObject();
        try {
            if (columnID != null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
                selectQuery.addSelectColumn(Column.getColumn("CRColumns", "COLUMN_ID"));
                selectQuery.addSelectColumn(Column.getColumn("CRColumns", "TABLE_NAME_ALIAS"));
                selectQuery.addSelectColumn(Column.getColumn("CRColumns", "COLUMN_NAME_ALIAS"));
                selectQuery.addSelectColumn(Column.getColumn("CRColumns", "DATA_TYPE"));
                selectQuery.addSelectColumn(Column.getColumn("CRColumns", "DISPLAY_NAME"));
                selectQuery.setCriteria(new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)columnID, 0));
                final DataObject dataObject = getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("CRColumns");
                    if (row != null) {
                        columnDetails.put("tableName", (Object)row.get("TABLE_NAME_ALIAS"));
                        columnDetails.put("columnName", (Object)row.get("COLUMN_NAME_ALIAS"));
                        columnDetails.put("dataType", (Object)row.get("DATA_TYPE"));
                        columnDetails.put("displayName", (Object)row.get("DISPLAY_NAME"));
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, e, () -> "Error getColumnDetails ColumnID: " + n);
        }
        return columnDetails;
    }
    
    public String getColumnAliasName(final JSONObject columnDetails) {
        String columnAlias = "";
        try {
            if (!columnDetails.isNull("tableName") && !columnDetails.isNull("columnName")) {
                columnAlias = columnDetails.get("tableName") + "." + columnDetails.get("columnName");
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, e, () -> "Error getColumnAliasName: " + jsonObject.toString());
        }
        return columnAlias;
    }
    
    public static List<Long> getMDMAlertIds() {
        final List<Long> alertConstantList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCAlertType"));
        final Column alertConstantsCol = new Column("DCAlertType", "ALERT_TYPE_ID");
        final Join dcModuleJoin = new Join("DCAlertType", "DCModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
        final Criteria mdmCriteria = new Criteria(Column.getColumn("DCModule", "MODULE_ID"), (Object)12, 0);
        selectQuery.addJoin(dcModuleJoin);
        selectQuery.setCriteria(mdmCriteria);
        selectQuery.addSelectColumn(alertConstantsCol);
        try {
            final DataObject dObject = DataAccess.get(selectQuery);
            final Iterator iterator = dObject.getRows("DCAlertType");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                alertConstantList.add(Long.valueOf(row.get("ALERT_TYPE_ID").toString()));
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Error in APIUtil.getMDMAlertIds() {0}", ex.getMessage());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return alertConstantList;
    }
    
    public Properties parseXMLToProperties(final String resultXML) throws Exception {
        final InputStream isr = IOUtils.toInputStream(resultXML);
        final DocumentBuilder dBuilder = DMSecurityUtil.getDocumentBuilder();
        final Document doc = dBuilder.parse(isr);
        final Properties prop = new Properties();
        doc.getDocumentElement().normalize();
        final NodeList nList = doc.getElementsByTagName("field");
        for (int temp = 0; temp < nList.getLength(); ++temp) {
            final Node nNode = nList.item(temp);
            if (nNode.getNodeType() == 1) {
                final Element eElement = (Element)nNode;
                prop.setProperty(eElement.getAttribute("name"), eElement.getElementsByTagName("value").item(0).getTextContent());
            }
        }
        MDMUtil.logger.info("Result Parsed to Properties");
        return prop;
    }
    
    public boolean isGeoTrackingEnabled() {
        return MDMFeatureParamsHandler.getInstance().isFeatureEnabled("GeoTracking");
    }
    
    public List getPlatformConstantsForPlatformString(final String platforms) {
        final List<Integer> platformList = new ArrayList<Integer>();
        if (platforms != null && platforms.matches("[1-4](,[1-4])*|(android|windows|(ios|macos)|chrome)(,(android|windows|ios|chrome))*") && !MDMStringUtils.isEmpty(platforms)) {
            if (platforms.toLowerCase().contains("android") || platforms.toLowerCase().contains("2")) {
                platformList.add(2);
            }
            else if (platforms.equalsIgnoreCase("ios") || platforms.toLowerCase().contains("1") || platforms.equalsIgnoreCase("macos")) {
                platformList.add(1);
            }
            else if (platforms.equalsIgnoreCase("windows") || platforms.toLowerCase().contains("3")) {
                platformList.add(3);
            }
            else if (platforms.equalsIgnoreCase("chrome") || platforms.toLowerCase().contains("4")) {
                platformList.add(4);
            }
        }
        return platformList;
    }
    
    public List getCollectionStatusToBeIgnoredForGroupReDistribution() {
        final List<Integer> listOfCollectionStatusToBeIgnored = new ArrayList<Integer>();
        listOfCollectionStatusToBeIgnored.add(4);
        listOfCollectionStatusToBeIgnored.add(6);
        listOfCollectionStatusToBeIgnored.add(3);
        return listOfCollectionStatusToBeIgnored;
    }
    
    public String convertSetToString(final Set set) {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
    
    public void addOrUpdateMdUserConfigParams(final Long userId, final String sParamName, final String sParamValue) throws DataAccessException {
        try {
            final Criteria paramNameCri = new Criteria(Column.getColumn("MdUserConfigParams", "PARAM_NAME"), (Object)sParamName, 0);
            final Criteria userIdCri = new Criteria(Column.getColumn("MdUserConfigParams", "USER_ID"), (Object)userId, 0);
            final Criteria userconfigCri = paramNameCri.and(userIdCri);
            final DataObject dObj = DataAccess.get("MdUserConfigParams", userconfigCri);
            if (dObj.isEmpty()) {
                MDMUtil.logger.log(Level.INFO, "MdUserConfigParams: Adding param...");
                final Row row = new Row("MdUserConfigParams");
                row.set("USER_ID", (Object)userId);
                row.set("PARAM_NAME", (Object)sParamName);
                row.set("PARAM_VALUE", (Object)sParamValue);
                dObj.addRow(row);
                getPersistence().add(dObj);
            }
            else {
                MDMUtil.logger.log(Level.INFO, "MdUserConfigParams: Updating param...");
                final Row row = dObj.getRow("MdUserConfigParams");
                row.set("USER_ID", (Object)userId);
                row.set("PARAM_VALUE", (Object)sParamValue);
                dObj.updateRow(row);
                getPersistence().update(dObj);
            }
            MDMUtil.logger.log(Level.INFO, "MdUserConfigParams: Param added/ Updated");
        }
        catch (final DataAccessException e) {
            MDMUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateMdUserConfigParams...", (Throwable)e);
            throw e;
        }
    }
    
    public String getMdUserConfigParams(final Long userId, final String sParamName) {
        String sParamValue = "";
        try {
            final Criteria paramNameCri = new Criteria(Column.getColumn("MdUserConfigParams", "PARAM_NAME"), (Object)sParamName, 0);
            final Criteria userIdCri = new Criteria(Column.getColumn("MdUserConfigParams", "USER_ID"), (Object)userId, 0);
            final Criteria userconfigCri = paramNameCri.and(userIdCri);
            final DataObject dObj = DataAccess.get("MdUserConfigParams", userconfigCri);
            if (!dObj.isEmpty()) {
                MDMUtil.logger.log(Level.INFO, "MdUserConfigparams: Param available.");
                final Row row = dObj.getFirstRow("MdUserConfigParams");
                sParamValue = (String)row.get("PARAM_VALUE");
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in addOrUpdateMdUserConfigParams...", ex);
        }
        return sParamValue;
    }
    
    public List<String> modifyHeadersInBulkCSVForValidation(final List<String> columnsInCSV) {
        final List<String> convertedList = new ArrayList<String>();
        for (int i = 0; i < columnsInCSV.size(); ++i) {
            final String headerString = columnsInCSV.get(i);
            if (!getInstance().isEmpty(headerString)) {
                convertedList.add(headerString.replaceAll("_|\\s", "").toLowerCase());
            }
        }
        return convertedList;
    }
    
    public static long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }
    
    public void enableOrDisableCheckSumTask(final Long customerId, final boolean enable, final int platform) throws Exception {
        MDMBaseSecurityUtil.getInstance(platform).toggleCheckSumValidation(customerId, enable);
    }
    
    public Long convertDateToMillis(final String date) throws Exception {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone());
        final Date date2 = simpleDateFormat.parse(date);
        final Long dateInMillis = date2.getTime();
        return dateInMillis;
    }
    
    public boolean isMacDevice(final long resourceId, final long customerId) {
        MDMUtil.logger.log(Level.INFO, "Checking whether the device with the given resource Id {0} and customer Id {1} is mac, ", new Object[] { resourceId, customerId });
        boolean isMac = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            selectQuery.addSelectColumn(new Column("MdModelInfo", "MODEL_ID"));
            selectQuery.addSelectColumn(new Column("MdModelInfo", "MODEL_TYPE"));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            final Criteria resourceIdCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria customerIdCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria platformTypeCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            selectQuery.setCriteria(resourceIdCriteria.and(customerIdCriteria).and(platformTypeCriteria));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                MDMUtil.logger.log(Level.INFO, "DO is not empty for resource Id: {0}", new Object[] { resourceId });
                final Row row = dataObject.getRow("MdModelInfo");
                if (row != null) {
                    final int modelType = (int)row.get("MODEL_TYPE");
                    MDMUtil.logger.log(Level.INFO, "Model type obtained from DB for the given resource Id: {0} is {1}", new Object[] { resourceId, modelType });
                    isMac = (modelType == 3 || modelType == 4);
                }
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, e, () -> "Exception while checking if the device with the given resource Id is a mac: " + n);
            isMac = false;
        }
        MDMUtil.logger.log(Level.INFO, "Is the device with the given resource Id {0} is a mac?", new Object[] { isMac });
        return isMac;
    }
    
    public static boolean isSiliconMac(final long resourceId) throws Exception {
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final List<Long> siliconMacs = getSiliconMacs(resourceList);
            return siliconMacs.contains(resourceId);
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, e, () -> "Exception while checking if the device with the given resource Id is a silicon mac: " + n);
            throw e;
        }
    }
    
    public static List<Long> getSiliconMacs(final List<Long> macList) throws Exception {
        final Map<Long, ProcessorType> macProcessorMap = getMacProcessorType(macList);
        final List<Long> siliconMacs = new ArrayList<Long>();
        for (final Long resource : macList) {
            final ProcessorType processorType = macProcessorMap.get(resource);
            if (processorType == ProcessorType.SILICON_M1_MAC) {
                siliconMacs.add(resource);
            }
        }
        return siliconMacs;
    }
    
    public static List<Long> filterSiliconMacs(final Map<Long, ProcessorType> macProcessorMap) {
        final Set<Map.Entry<Long, ProcessorType>> macEntries = macProcessorMap.entrySet();
        final List<Long> siliconMacs = new ArrayList<Long>();
        for (final Map.Entry<Long, ProcessorType> mac : macEntries) {
            final Long resourceId = mac.getKey();
            final ProcessorType processorType = mac.getValue();
            if (processorType == ProcessorType.SILICON_M1_MAC) {
                siliconMacs.add(resourceId);
            }
        }
        return siliconMacs;
    }
    
    public static List<Long> filterUnknownProcessorMacs(final Map<Long, ProcessorType> macProcessorMap) {
        final Set<Map.Entry<Long, ProcessorType>> macEntries = macProcessorMap.entrySet();
        final List<Long> unknownProcessorMacs = new ArrayList<Long>();
        for (final Map.Entry<Long, ProcessorType> mac : macEntries) {
            final Long resourceId = mac.getKey();
            final ProcessorType processorType = mac.getValue();
            if (processorType == null) {
                unknownProcessorMacs.add(resourceId);
            }
        }
        return unknownProcessorMacs;
    }
    
    public static Map<Long, ProcessorType> getMacProcessorType(final List<Long> resourceIds) throws Exception {
        final Map<Long, ProcessorType> macProcessorMap = new HashMap<Long, ProcessorType>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "*"));
            selectQuery.setCriteria(new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final long resourceId = (long)row.get("RESOURCE_ID");
                    final String processorType = (String)row.get("PROCESSOR_TYPE");
                    if (processorType == null) {
                        macProcessorMap.put(resourceId, null);
                    }
                    else if (processorType.equals(ProcessorType.SILICON_M1_MAC.alias)) {
                        macProcessorMap.put(resourceId, ProcessorType.SILICON_M1_MAC);
                    }
                    else {
                        macProcessorMap.put(resourceId, ProcessorType.INTEL_MAC);
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.SEVERE, e, () -> "Exception while checking if the device with the given resource Id is a silicon mac: " + list);
            throw e;
        }
        return macProcessorMap;
    }
    
    static {
        MDMUtil.logger = Logger.getLogger("MDMLogger");
        MDMUtil.mdmEnrollmentLogger = Logger.getLogger("MDMEnrollment");
        MDMUtil.mdmUtil = null;
        MDMUtil.appliedCommandList = new ThreadLocal<Set<Long>>();
    }
}
