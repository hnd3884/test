package com.me.mdm.server.windows.profile.payload.transform;

import com.me.mdm.core.xmlparser.XmlBeanUtil;
import com.me.mdm.core.windows.xmlbeans.WindowsMSIInstallJob;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.windows.profile.payload.WindowsAppConfigPayload;
import javax.xml.bind.JAXBException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.apache.commons.lang3.StringEscapeUtils;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.server.windows.profile.payload.WinDesktopMSIAppPayload;
import com.me.mdm.server.windows.profile.payload.WinDesktopAppPayload;
import com.me.mdm.server.windows.profile.payload.WindowsAppPayload;
import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.windows.profile.payload.WinMobileAppPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsAppPayload extends DO2WindowsPayload
{
    public WindowsPayload[] createAppPayload(final DataObject dataObject) {
        final WinMobileAppPayload[] appPayloads = new WinMobileAppPayload[3];
        try {
            String enterpriseID = null;
            final DataObject settingsDO = MDMUtil.getPersistence().get("WpAppSettings", (Criteria)null);
            if (!settingsDO.isEmpty()) {
                enterpriseID = (String)settingsDO.getFirstRow("WpAppSettings").get("ENTERPRISE_ID");
            }
            final Row row = dataObject.getFirstRow("InstallAppPolicy");
            final long appId = (long)row.get("APP_ID");
            final DataObject windowsAppsDO = MDMUtil.getPersistence().get("WindowsAppDetails", new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), (Object)appId, 0));
            final Criteria appDetailsCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppDetails"));
            final Join pkgAppDataJoin = new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join apptoGroup = new Join("MdAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join apptoGroupJoin = new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            selectQuery.addJoin(pkgAppDataJoin);
            selectQuery.addJoin(apptoGroup);
            selectQuery.addJoin(apptoGroupJoin);
            selectQuery.setCriteria(appDetailsCriteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject appDetailsDO = MDMUtil.getPersistence().get(selectQuery);
            final HashMap hashMap = new HashMap();
            String productID = null;
            final Row packageRow = appDetailsDO.getFirstRow("MdPackageToAppData");
            final String fileName = (String)packageRow.get("APP_FILE_LOC");
            String commandLine = (String)packageRow.get("COMMAND_LINE");
            final String customizedAppURL = String.valueOf(packageRow.get("CUSTOMIZED_APP_URL"));
            final Iterator iterator = dataObject.getRows("AppDependency");
            final List laptopDependencies = new ArrayList();
            final List mobileDependencies = new ArrayList();
            while (iterator.hasNext()) {
                final Row row2 = iterator.next();
                final String arch = (String)row2.get("SUPPORTED_ARCH");
                if (arch.equals("x86") || arch.equals("x64")) {
                    laptopDependencies.add(row2.get("FILE_LOC"));
                }
                else if (arch.equals("arm")) {
                    mobileDependencies.add(row2.get("FILE_LOC"));
                }
                if (arch.equals("neutral")) {
                    mobileDependencies.add(row2.get("FILE_LOC"));
                    laptopDependencies.add(row2.get("FILE_LOC"));
                }
            }
            if (fileName.contains(".xap") || fileName.toLowerCase().endsWith(".msi")) {
                final Row appDetailsRow = appDetailsDO.getFirstRow("MdAppDetails");
                productID = (String)appDetailsRow.get("IDENTIFIER");
                commandLine = "/qn /lv " + productID + ".log " + commandLine;
                productID = "%7B" + productID + "%7D";
                final String appName = (String)appDetailsRow.get("APP_NAME");
                final String appVersion = (String)appDetailsRow.get("APP_VERSION");
                hashMap.put("AppVersion", appVersion);
                hashMap.put("AppName", appName);
                hashMap.put("CUSTOMIZED_APP_URL", customizedAppURL);
            }
            else {
                final Row appGroupRow = appDetailsDO.getFirstRow("MdAppGroupDetails");
                productID = (String)appGroupRow.get("IDENTIFIER");
                if (!windowsAppsDO.isEmpty()) {
                    final Row row3 = windowsAppsDO.getFirstRow("WindowsAppDetails");
                    final String license = (String)row3.get("LICENSE_CONTENT");
                    if (!MDMStringUtils.isEmpty(license)) {
                        hashMap.put("hasLicense", true);
                    }
                    else {
                        hashMap.put("hasLicense", false);
                    }
                }
                else {
                    hashMap.put("hasLicense", false);
                }
            }
            hashMap.put("enterpriseID", enterpriseID);
            hashMap.put("productID", productID);
            hashMap.put("AppDetailsDO", appDetailsDO);
            hashMap.put("isMSI", fileName.toLowerCase().endsWith(".msi"));
            hashMap.put("commandLine", commandLine);
            hashMap.put("laptopDependency", laptopDependencies);
            hashMap.put("mobileDependency", mobileDependencies);
            appPayloads[0] = this.createAppInstallProfile(dataObject, hashMap, "install");
            appPayloads[1] = this.createAppRemoveProfile(dataObject, hashMap);
            appPayloads[2] = this.createAppUpdateProfile(dataObject, hashMap);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows App payload ", ex);
        }
        return appPayloads;
    }
    
    private WinMobileAppPayload createAppInstallProfile(final DataObject dataObject, final HashMap hashMap, final String type) throws DataAccessException, JSONException, SyMException, ClassNotFoundException, JAXBException {
        WinMobileAppPayload installPayload = null;
        final WinMobileAppPayload win10MobilePayload = new WinMobileAppPayload();
        final WindowsAppPayload windowsAppPayload = new WindowsAppPayload();
        WinDesktopAppPayload winDesktopAppPayload = new WinDesktopAppPayload();
        final Boolean isMSI = hashMap.get("isMSI");
        final Boolean hasLicense = hashMap.get("hasLicense");
        if (isMSI) {
            winDesktopAppPayload = new WinDesktopMSIAppPayload();
        }
        installPayload = new WinMobileAppPayload();
        String payloadType = null;
        if (type.equals("install")) {
            payloadType = "InstallConfigPayload";
            installPayload.getAddPayloadCommand().addRequestItem(installPayload.createTargetItemTagElement("%app_add_payload_xml%"));
        }
        else {
            payloadType = "UpdateConfigPayload";
            installPayload.getAddPayloadCommand().addRequestItem(installPayload.createTargetItemTagElement("%app_add_payload_xml%"));
            installPayload.getReplacePayloadCommand().addRequestItem(installPayload.createTargetItemTagElement("%app_replace_payload_xml%"));
        }
        installPayload.getExecPayloadCommand().addRequestItem(installPayload.createTargetItemTagElement("%app_exec_payload_xml%"));
        installPayload.getNonAtomicDeletePayloadCommand().addRequestItem(installPayload.createTargetItemTagElement("%app_nonAtomicDelete_payload_xml%"));
        installPayload.setPayloadType(payloadType);
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseID", (Object)hashMap.get("enterpriseID"));
        jsonObject.put("productID", (Object)hashMap.get("productID"));
        final DataObject appDetailsDO = hashMap.get("AppDetailsDO");
        final Row apppkgRow = appDetailsDO.getFirstRow("MdPackageToAppData");
        String appDownloadUrl = (String)apppkgRow.get("APP_FILE_LOC");
        String manifestFileUrlFullPath = null;
        if (appDownloadUrl != null && !appDownloadUrl.equals("")) {
            final HashMap hm = new HashMap();
            hm.put("path", appDownloadUrl);
            hm.put("IS_SERVER", false);
            hm.put("IS_AUTHTOKEN", true);
            appDownloadUrl = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthTokenAndUDID(hm);
            manifestFileUrlFullPath = MDMAppMgmtHandler.getDynamicServerBaseURL() + appDownloadUrl.replace('\\', '/');
        }
        if (isMSI) {
            hashMap.put("manifestFileUrlFullPath", StringEscapeUtils.escapeXml(manifestFileUrlFullPath));
            final String appFileUrl = (String)apppkgRow.get("APP_FILE_LOC");
            hashMap.put("appDownloadUrl", appFileUrl);
            jsonObject.put("msiInstallJob", (Object)this.getMSIInstallString(hashMap));
        }
        if (hashMap.get("enterpriseID") != null && !isMSI) {
            windowsAppPayload.setPayloadType(payloadType);
            windowsAppPayload.initializePayload(jsonObject, "install");
            windowsAppPayload.setName(hashMap.get("AppName"));
            windowsAppPayload.setVersion(hashMap.get("AppVersion"));
            windowsAppPayload.setURL(manifestFileUrlFullPath, null);
            windowsAppPayload.assignToAppCatalog();
            windowsAppPayload.enableSilentInstall();
            this.packOsSpecificPayloadToXML(dataObject, windowsAppPayload, type, "WindowsPhone81App");
        }
        if (!manifestFileUrlFullPath.contains(".xap")) {
            manifestFileUrlFullPath = null;
        }
        win10MobilePayload.setPayloadType(payloadType);
        win10MobilePayload.initializePayload(jsonObject, "install");
        win10MobilePayload.setURL(manifestFileUrlFullPath, hashMap.get("mobileDependency"));
        win10MobilePayload.enableSilentInstall();
        winDesktopAppPayload.setPayloadType(payloadType);
        winDesktopAppPayload.initializePayload(jsonObject, "install");
        winDesktopAppPayload.setDeletePayload();
        winDesktopAppPayload.setURL(manifestFileUrlFullPath, hashMap.get("laptopDependency"));
        winDesktopAppPayload.enableSilentInstall();
        if (hasLicense != null && hasLicense) {
            win10MobilePayload.addLicenseBlob();
            winDesktopAppPayload.addLicenseBlob();
        }
        if (!isMSI) {
            this.packOsSpecificPayloadToXML(dataObject, win10MobilePayload, type, "Windows10MobileApp");
        }
        this.packOsSpecificPayloadToXML(dataObject, winDesktopAppPayload, type, "Windows10DesktopApp");
        return installPayload;
    }
    
    private WinMobileAppPayload createAppRemoveProfile(final DataObject dataObject, final HashMap hashMap) throws JSONException {
        WinMobileAppPayload removePayload = null;
        final WinMobileAppPayload win10AppPayload = new WinMobileAppPayload();
        final WindowsAppPayload windowsAppPayload = new WindowsAppPayload();
        WinDesktopAppPayload winDesktopAppPayload = new WinDesktopAppPayload();
        final Boolean isMSI = hashMap.get("isMSI");
        if (isMSI) {
            winDesktopAppPayload = new WinDesktopMSIAppPayload();
        }
        removePayload = new WinMobileAppPayload();
        removePayload.getDeletePayloadCommand().addRequestItem(removePayload.createTargetItemTagElement("%app_payload_xml%"));
        removePayload.getExecPayloadCommand().addRequestItem(removePayload.createTargetItemTagElement("%app_exec_payload_xml%"));
        removePayload.setPayloadType("RemoveConfigPayload");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseID", (Object)hashMap.get("enterpriseID"));
        jsonObject.put("productID", (Object)hashMap.get("productID"));
        if (hashMap.get("enterpriseID") != null && !isMSI) {
            windowsAppPayload.setPayloadType("RemoveConfigPayload");
            windowsAppPayload.initializePayload(jsonObject, "remove");
            windowsAppPayload.deleteApp();
            this.packOsSpecificPayloadToXML(dataObject, windowsAppPayload, "remove", "WindowsPhone81App");
        }
        win10AppPayload.setPayloadType("RemoveConfigPayload");
        win10AppPayload.initializePayload(jsonObject, "remove");
        win10AppPayload.deleteApp();
        winDesktopAppPayload.setPayloadType("RemoveConfigPayload");
        winDesktopAppPayload.initializePayload(jsonObject, "remove");
        winDesktopAppPayload.deleteApp();
        if (!isMSI) {
            this.packOsSpecificPayloadToXML(dataObject, win10AppPayload, "remove", "Windows10MobileApp");
        }
        this.packOsSpecificPayloadToXML(dataObject, winDesktopAppPayload, "remove", "Windows10DesktopApp");
        return removePayload;
    }
    
    public WinMobileAppPayload createAppUpdateProfile(final DataObject dataObject, final HashMap hashMap) throws DataAccessException, JSONException, JAXBException, SyMException, ClassNotFoundException {
        return this.createAppInstallProfile(dataObject, hashMap, "update");
    }
    
    public WindowsPayload createAppConfigPayload(final DataObject dataObject) throws Exception {
        final Row appGroup = dataObject.getFirstRow("MdAppDetails");
        WindowsAppConfigPayload appConfigPayload = null;
        if (appGroup != null) {
            final String packageFamilyName = this.getPFNForApp((Long)appGroup.get("APP_ID"));
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("packageFamilyName", (Object)packageFamilyName);
            appConfigPayload = new WindowsAppConfigPayload(jsonObject);
            final Row configRow = dataObject.getRow("ManagedAppConfigurationData");
            if (configRow != null) {
                String filePath = (String)configRow.get("APP_CONFIG_PATH");
                filePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filePath;
                final String configdata = new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
                appConfigPayload.setConfiguration(new JSONArray(configdata));
            }
        }
        return appConfigPayload;
    }
    
    public String getPFNForApp(final Long appid) throws DataAccessException {
        String pfn = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppToGroupRel", "APP_ID"), (Object)appid, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppGroupDetails");
            pfn = (String)row.get("IDENTIFIER");
        }
        return pfn;
    }
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        return null;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        return null;
    }
    
    private String getMSIInstallString(final HashMap hashMap) throws JSONException, SyMException, ClassNotFoundException, JAXBException {
        final JSONObject msiDetailsJSON = new JSONObject();
        final String customizedAppURL = String.valueOf(hashMap.get("CUSTOMIZED_APP_URL"));
        msiDetailsJSON.put("downloadURL", (Object)(MDMStringUtils.isEmpty(customizedAppURL) ? hashMap.get("manifestFileUrlFullPath") : customizedAppURL));
        msiDetailsJSON.put("version", (Object)hashMap.get("AppVersion"));
        msiDetailsJSON.put("productID", (Object)hashMap.get("productID"));
        msiDetailsJSON.put("fileHash", (Object)ChecksumProvider.getInstance().GetSHA256CheckSum(MDMAppMgmtHandler.getInstance().getAppRepositoryBaseFolderPath() + hashMap.get("appDownloadUrl")));
        msiDetailsJSON.put("commandLine", (Object)hashMap.get("commandLine"));
        return createMSIInstallString(msiDetailsJSON);
    }
    
    public static String createMSIInstallString(final JSONObject msiDetailsJSON) throws JSONException, SyMException, ClassNotFoundException, JAXBException {
        final WindowsMSIInstallJob windowsMSIInstallJob = WindowsMSIInstallJob.getWinMSIInstallJobBean(msiDetailsJSON);
        final JSONObject beanUtilJSON = new JSONObject();
        beanUtilJSON.put("BEAN_OBJECT", (Object)windowsMSIInstallJob);
        beanUtilJSON.put("jaxb.fragment", (Object)Boolean.TRUE);
        beanUtilJSON.put("jaxb.encoding", (Object)"UTF-8");
        final JSONObject customProps = new JSONObject();
        customProps.put("com.sun.xml.internal.bind.xmlHeaders", (Object)"");
        beanUtilJSON.put("customMarshallerProps", (Object)customProps);
        final XmlBeanUtil<WindowsMSIInstallJob> xmlBeanUtil = new XmlBeanUtil<WindowsMSIInstallJob>(beanUtilJSON);
        String msiInstallJob = xmlBeanUtil.beanToXmlString();
        msiInstallJob = StringEscapeUtils.unescapeXml(msiInstallJob);
        return msiInstallJob;
    }
}
