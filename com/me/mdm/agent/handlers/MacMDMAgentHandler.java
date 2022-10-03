package com.me.mdm.agent.handlers;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAgentSettingsHandler;
import java.util.Properties;
import java.util.HashMap;
import java.util.Collection;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.omg.CORBA.SystemException;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import java.util.regex.Pattern;
import com.dd.plist.Base64;
import com.dd.plist.NSArray;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.dd.plist.NSObject;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.core.auth.APIKey;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.logging.Level;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.util.MDMAgentBuildVersionsUtil;
import java.util.logging.Logger;
import com.me.mdm.apps.handler.AppAutoDeploymentHandler;

public class MacMDMAgentHandler implements AppAutoDeploymentHandler
{
    public Logger logger;
    private static final String DY_AGENT_CONFIGURATION = "%agentconfiguration%";
    
    public MacMDMAgentHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    private String getPackageSHA() {
        return MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentsha256");
    }
    
    private String getPackageMD5() throws Exception {
        return MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentmd5");
    }
    
    private Integer getPackageSize() throws Exception {
        return Integer.parseInt(MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentsize"));
    }
    
    private String getFileLocation() throws Exception {
        final String folderLocation = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "agent";
        final String fileLocation = folderLocation + File.separator + "MDMMacAgent.pkg";
        final String agentDownloadURL = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macagenturl") + MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentversioncode") + "/" + "MDMMacAgent.pkg";
        this.logger.log(Level.INFO, "MacMDMAgentHandler: Downloading Mac MDM Agent...");
        final DownloadStatus downloadStatus = DownloadManager.getInstance().downloadBinaryFile(agentDownloadURL, fileLocation, this.getPackageMD5(), new SSLValidationType[0]);
        this.logger.log(Level.INFO, "MacMDMAgentHandler: Is Mac MDM Agent downloaded successfully? {0}. Download Status code: {1}", new Object[] { downloadStatus.getStatus() == 0, downloadStatus.getStatus() });
        String destinationLocation = folderLocation + File.separator + "macmdmagent";
        final File destFile = new File(destinationLocation);
        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        destinationLocation = destinationLocation + File.separator + "MDMMacAgent.pkg";
        ApiFactoryProvider.getFileAccessAPI().copyFile(fileLocation, destinationLocation);
        return destinationLocation;
    }
    
    @Override
    public JSONObject getAgentAppData(final Long customerID) {
        final JSONObject jsonObject = new JSONObject();
        try {
            String fileLocation = null;
            String shaHash = null;
            String md5Hash = null;
            Integer fileSize = null;
            Boolean isServerDeploy = Boolean.parseBoolean(MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentdeployfromserver"));
            CustomerInfoUtil.getInstance();
            final Boolean isCloudCustomer = CustomerInfoUtil.isSAS();
            isServerDeploy = ((isServerDeploy == null) ? Boolean.FALSE : (isServerDeploy && !isCloudCustomer));
            final String cloudURL = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macagenturl");
            final String agentDownloadURL = cloudURL + MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentversioncode") + "/" + "MDMMacAgent.pkg";
            if (customerID != -1L && customerID != null) {
                if (isServerDeploy) {
                    fileLocation = this.getFileLocation();
                }
                else {
                    fileLocation = agentDownloadURL;
                }
                shaHash = this.getPackageSHA();
                md5Hash = this.getPackageMD5();
                fileSize = this.getPackageSize();
            }
            final String agentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentversion");
            final JSONObject packageAppDataJson = new JSONObject();
            packageAppDataJson.put("SUPPORTED_DEVICES", 16);
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageAppDataJson);
            jsonObject.put("MdPackageToAppGroupForm", (Object)new JSONObject());
            jsonObject.put("PLATFORM_TYPE", 1);
            jsonObject.put("APP_TITLE", (Object)"MDM_Agent_Title");
            jsonObject.put("APP_NAME", (Object)"MDM_AGENT");
            jsonObject.put("APP_VERSION", (Object)agentVersion);
            jsonObject.put("APP_NAME_SHORT_VERSION", (Object)agentVersion);
            jsonObject.put("APP_TITLE", (Object)"MDM Agent Configuration");
            jsonObject.put("PACKAGE_ADDED_BY", (Object)MDMUtil.getAdminUserId());
            jsonObject.put("IS_MODERN_APP", (Object)Boolean.TRUE);
            jsonObject.put("IDENTIFIER", (Object)"com.manageengine.mdm.mac");
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)"com.manageengine.mdm.mac");
            jsonObject.put("packageIdentifier", (Object)"com.manageengine.mdm.mac");
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"Profile for MacOS MDM Agent");
            jsonObject.put("BUNDLE_SIZE", 0L);
            jsonObject.put("APP_CATEGORY_ID", 1);
            jsonObject.put("APP_CATEGORY_NAME", (Object)"Business");
            jsonObject.put("COUNTRY_CODE", (Object)"US");
            jsonObject.put("PACKAGE_TYPE", 0);
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.AppMgmtFormBean");
            jsonObject.put("PACKAGE_SHA", (Object)shaHash);
            jsonObject.put("PACKAGE_MD5", (Object)md5Hash);
            jsonObject.put("PACKAGE_SIZE", (Object)fileSize);
            jsonObject.put("FILE_LOCATION", (Object)fileLocation);
            jsonObject.put("SUPPORTED_DEVICES", 16);
            if (isServerDeploy) {
                jsonObject.put("APP_FILE", (Object)fileLocation);
            }
            else {
                jsonObject.put("STATIC_SERVER_URL", (Object)agentDownloadURL);
            }
            jsonObject.put("DISPLAY_IMAGE_LOC", (Object)"");
            jsonObject.put("FULL_IMAGE", (Object)"");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            jsonObject.put("PACKAGE_TYPE", 2);
            jsonObject.put("APP_CONFIG", (Object)Boolean.TRUE);
            jsonObject.put("HAS_APP_CONFIGURATION", (Object)Boolean.TRUE);
            jsonObject.put("AGENT_CONFIGURATION", (Object)"%agentconfiguration%");
            jsonObject.put("IS_PACKAGE_DEPLOY", (Object)Boolean.TRUE);
            jsonObject.put("TABLE_NAME", (Object)"MdPackage");
            jsonObject.put("IS_NATIVE_AGENT", (Object)Boolean.TRUE);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            appPolicyJSON.put("CONFIG_DATA_IDENTIFIER", (Object)"MDM_Agent");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(1).isCurrentPackageNewToAppRepo("com.manageengine.mdm.mac", customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception at MACAgentHandler", e);
        }
        return jsonObject;
    }
    
    private NSDictionary getUrlDict() {
        final NSDictionary urlDict = new NSDictionary();
        urlDict.put("DeviceRegistrationServlet", (Object)"/mdm/client/v1/drs");
        urlDict.put("IOSNativeAppServlet", (Object)"/mdm/client/v1/macnativeappserver");
        urlDict.put("MDMLogUploaderServlet", (Object)"/mdm/client/v1/mdmLogUploader");
        return urlDict;
    }
    
    private NSDictionary getServiceDict(final APIKey key) {
        final NSDictionary servicesDict = new NSDictionary();
        final Boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
        final NSDictionary urlsDict = this.getUrlDict();
        if (isProfessional) {
            urlsDict.put("mdmDocsServlet", (Object)"/mdm/client/v1/docs");
        }
        servicesDict.put("urls", (NSObject)this.getUrlDict());
        servicesDict.put("token_name", (Object)key.getKeyName());
        servicesDict.put("token_value", (Object)key.getKeyValue());
        return servicesDict;
    }
    
    private String getConfiguration(final String strUDID) throws Exception {
        final NSDictionary root = new NSDictionary();
        root.put("ServerName", (Object)"%ServerName%");
        root.put("ServerPort", (Object)"%ServerPort%");
        root.put("UDID", (Object)"%udid%");
        root.put("ErID", (Object)"%erid%");
        root.put("PayloadType", (Object)"com.manageengine.mdm.mac");
        root.put("PayloadVersion", (Object)1);
        root.put("PayloadDisplayName", (Object)"MDM Agent Configuration");
        root.put("PayloadIdentifier", (Object)"com.manageengine.mdm.mac");
        root.put("PayloadUUID", (Object)"com.manageengine.mdm.mac--agent");
        final JSONObject apiKeyJson = new JSONObject();
        final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromUdid(strUDID);
        apiKeyJson.put("ENROLLMENT_REQUEST_ID", (Object)erid);
        final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiKeyJson);
        final NSDictionary serviceDict = this.getServiceDict(key);
        serviceDict.put("urls", (NSObject)this.getUrlDict());
        root.put("Services", (NSObject)serviceDict);
        final NSArray configArray = new NSArray(1);
        configArray.setValue(0, (Object)root);
        final NSDictionary finalDict = new NSDictionary();
        finalDict.put("PayloadContent", (NSObject)configArray);
        finalDict.put("PayloadRemovalDisallowed", (Object)Boolean.TRUE);
        finalDict.put("PayloadScope", (Object)"System");
        finalDict.put("PayloadType", (Object)"Configuration");
        finalDict.put("PayloadOrganization", (Object)"MDM");
        finalDict.put("PayloadVersion", (Object)1);
        finalDict.put("PayloadDisplayName", (Object)"MDM Agent Configuration");
        finalDict.put("PayloadIdentifier", (Object)"com.manageengine.mdm.mac");
        finalDict.put("PayloadUUID", (Object)"com.manageengine.mdm.mac");
        return finalDict.toXMLPropertyList();
    }
    
    private String base64Encode(final String data) {
        final String encodedString = Base64.encodeBytes(data.getBytes());
        return encodedString;
    }
    
    private String replaceVariable(final String replaceIn, final String replaceWhat, final String replaceWith) {
        return Pattern.compile(replaceWhat, 2).matcher(replaceIn).replaceAll(replaceWith);
    }
    
    @Override
    public String replaceDynamicVariables(String payload, final Long customerID, final String strUDID) {
        try {
            String configuration = this.getConfiguration(strUDID);
            configuration = DynamicVariableHandler.replaceDynamicVariables(configuration, strUDID);
            payload = this.replaceVariable(payload, this.base64Encode("%agentconfiguration%"), this.base64Encode(configuration));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in replacing agent dynamic variables!!", e);
        }
        return payload;
    }
    
    public List getApplicableDevicesViaLocationSetting() throws Exception {
        final Long collectionID = AppsAutoDeployment.getInstance().getCollectionIDFromAgentID(2);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("LocationDeviceStatus"));
        final Criteria criteria = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)Boolean.TRUE, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        final Iterator<Row> iterator = dataObject.getRows("LocationDeviceStatus");
        final List resourceList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long resourceID = (Long)row.get("MANAGED_DEVICE_ID");
            resourceList.add(resourceID);
        }
        return MDMCollectionUtil.getResourceNotAssociatedWithCollection(resourceList, collectionID);
    }
    
    public void removeMacAgentFromDevice(final Long resourceID) {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceID);
            AppsAutoDeployment.getInstance().removeAgentFromEnrolledDevices(customerID, resourceList, 2);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception: Unable to remove Mac Agent from the device.", e);
        }
    }
    
    public void removeMacAgentFromDevicesOfCustomer(final Long customerID) {
        try {
            List<Long> resourceList = new ArrayList<Long>();
            final Criteria agentTypecri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_TYPE"), (Object)8, 0);
            final Criteria customercri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria cri = agentTypecri.and(customercri);
            resourceList = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs(cri);
            AppsAutoDeployment.getInstance().removeAgentFromEnrolledDevices(customerID, resourceList, 2);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception: Unable to remove Mac Agent from the devices", e);
        }
    }
    
    public void updateAgentApp(final long customerId) throws Exception {
        try {
            final String agentVersion = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macosagentversion");
            final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage(this.getBundleIdentifier(), this.getPlatformType(), customerId);
            if (isAppExists) {
                final String currentVersion = AppsUtil.getInstance().getLatestAppVersionForBundleIdentifier(customerId, this.getBundleIdentifier());
                if (!agentVersion.equals(currentVersion)) {
                    final JSONObject jsonObject = this.getAgentAppData(customerId);
                    jsonObject.put("PACKAGE_ADDED_BY", (Object)MDMUtil.getAdminUserId());
                    try {
                        MDMUtil.getUserTransaction().begin();
                        MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(jsonObject);
                        MDMUtil.getUserTransaction().commit();
                        this.logger.log(Level.INFO, "Mac MDM Native agent {0} updated to {1}", new Object[] { this.getBundleIdentifier(), agentVersion });
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Failed to add new macOS agent to the repository. Going to rollback", e);
                        try {
                            MDMUtil.getUserTransaction().rollback();
                            this.logger.log(Level.SEVERE, "Succesfully rollbacked transaction.");
                        }
                        catch (final SystemException exp) {
                            this.logger.log(Level.SEVERE, "Rollback while adding macOS agent failed", exp);
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, " Exception in updating native agent app ", e2);
            throw e2;
        }
    }
    
    public void installAgentToDevices(final JSONObject jsData) {
        try {
            this.logger.log(Level.INFO, "Going to distribute agent to all the applicable MacOS device.");
            final Boolean isFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MacMDMAgent");
            final List resourceList = this.getApplicableDevicesViaLocationSetting();
            final Long customerID = JSONUtil.optLongForUVH(jsData, "CUSTOMER_ID", Long.valueOf(-1L));
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("IS_NATIVE_APP_ENABLE", (Object)isFeatureEnabled);
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("AGENT_TYPE", 2);
            jsonObject.put("RESOURCE_LIST", (Object)new JSONArray((Collection)resourceList));
            AppsAutoDeployment.getInstance().handleNativeAgent(jsonObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to install agent for Mac devices", e);
        }
    }
    
    public void addMacMDMAgentAsynchronously(final Long customerID) {
        this.logger.log(Level.INFO, "Going to add ME MDM Mac Agent asynchronously");
        try {
            final HashMap taskInfoMap = new HashMap();
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            taskInfoMap.put("taskName", "AddMemdmMacOSAppTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.apps.mac.AddMEMDMMacAppTask", taskInfoMap, properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in adding MacOS ME MDM agent asynchronously", e);
        }
    }
    
    public void checkAndinstallMacAgentForDevice(final Long resourceID, final Long customerID) {
        try {
            this.logger.log(Level.INFO, "Going to distribute agent to resource {0}", new Object[] { resourceID });
            final Boolean isFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MacMDMAgent");
            final Boolean isGeoTrackingEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
            final Boolean isAgentInstallEnabled = new IosNativeAgentSettingsHandler().isIOSNativeAgentEnable(customerID);
            final Boolean isAgentInstallTriggerReqd = isAgentInstallEnabled && isGeoTrackingEnabled && isFeatureEnabled;
            if (!isAgentInstallTriggerReqd) {
                this.logger.log(Level.INFO, "Skipping agent distribution to resource {0} as condition does not satisfy", new Object[] { resourceID });
                return;
            }
            final List<Long> resourceList = new ArrayList<Long>() {
                {
                    this.add(resourceID);
                }
            };
            final Properties properties = AppsAutoDeployment.getInstance().getAppProfileDetails(customerID, resourceList, 1, 2, "InstallApplication");
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            this.logger.log(Level.INFO, "Agent distribution command added for resource {0}", new Object[] { resourceID });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to install agent for Mac device", e);
        }
    }
    
    @Override
    public List filterDevices(List resourceList) {
        final List filteredList = new ArrayList();
        try {
            final Long collectionID = AppsAutoDeployment.getInstance().getCollectionIDFromAgentID(2);
            final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
            SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 3, 4 }, 8));
            criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            resourceList = new ArrayList();
            if (dataObject.isEmpty()) {
                return new ArrayList();
            }
            Iterator<Row> iterator = dataObject.getRows("ManagedDevice");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                resourceList.add(row.get("RESOURCE_ID"));
            }
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
            criteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            criteria = criteria.and(new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0));
            query.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
            query.setCriteria(criteria);
            final DataObject collectionDO = MDMUtil.getPersistence().get(query);
            if (collectionDO.isEmpty()) {
                return resourceList;
            }
            iterator = collectionDO.getRows("CollnToResources");
            while (iterator.hasNext()) {
                final Row row2 = iterator.next();
                final Long resourceID = (Long)row2.get("RESOURCE_ID");
                resourceList.remove(resourceID);
                filteredList.add(resourceID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to filter resource list before sending agent", e);
        }
        this.logger.log(Level.SEVERE, "Filtered devices for macOS MDM Native Agent distributed as macOS Agent is already installed in device :{0}", filteredList);
        return resourceList;
    }
    
    @Override
    public int getPlatformType() {
        return 1;
    }
    
    @Override
    public int getSupportedDevices() {
        return 16;
    }
    
    @Override
    public String getBundleIdentifier() {
        return "com.manageengine.mdm.mac";
    }
}
