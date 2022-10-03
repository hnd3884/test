package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.dd.plist.NSDictionary;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import org.json.JSONArray;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.AppsPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2IOSAppPayload implements DO2Payload
{
    public Logger logger;
    
    public DO2IOSAppPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        AppsPayload appsPayload = null;
        final AppsPayload[] appsPayloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("InstallAppPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final long appId = (long)row.get("APP_ID");
                final long appGroupId = (long)row.get("APP_GROUP_ID");
                final long collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
                final Criteria criteria = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appId, 0);
                final Row apppkgRow = dataObject.getRow("MdPackageToAppData", criteria);
                String manifestFileUrl = (String)apppkgRow.get("MANIFEST_FILE_URL");
                final Row pkgRow = dataObject.getFirstRow("MdPackagePolicy");
                final boolean removeAppWithMdmProfile = (boolean)pkgRow.get("REMOVE_APP_WITH_PROFILE");
                final boolean preventBackup = (boolean)pkgRow.get("PREVENT_BACKUP");
                final Row appGrpPkgRow = dataObject.getFirstRow("MdPackageToAppGroup");
                appsPayload = new AppsPayload();
                appsPayload.setRequestType("InstallApplication");
                if (manifestFileUrl != null && !manifestFileUrl.equals("")) {
                    manifestFileUrl = manifestFileUrl.replace('\\', '/');
                    final HashMap hm = new HashMap();
                    hm.put("path", manifestFileUrl);
                    hm.put("IS_SERVER", false);
                    hm.put("IS_AUTHTOKEN", true);
                    manifestFileUrl = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthTokenAndUDID(hm);
                    final String manifestFileUrlFullPath = MDMAppMgmtHandler.getDynamicServerBaseURL() + manifestFileUrl;
                    appsPayload.setManifestURL(manifestFileUrlFullPath);
                }
                else {
                    final int iTunesStoreID = Integer.parseInt((String)apppkgRow.get("STORE_ID"));
                    appsPayload.setiTunesStoreID(iTunesStoreID);
                }
                appsPayload.setInstallAsManaged(Boolean.TRUE);
                Integer managementFlags = 1;
                if (preventBackup && removeAppWithMdmProfile) {
                    managementFlags = 5;
                }
                else if (preventBackup) {
                    managementFlags = 4;
                }
                else if (removeAppWithMdmProfile) {
                    managementFlags = 1;
                }
                else {
                    managementFlags = 0;
                }
                appsPayload.setManagementFlags(managementFlags);
                final Row configRow = dataObject.getRow("ManagedAppConfigurationData");
                if (configRow != null && AppConfigPolicyDBHandler.getInstance().isConfigurationApplicableForApp(collectionId)) {
                    String filePath = (String)configRow.get("APP_CONFIG_PATH");
                    filePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filePath;
                    final String configdata = new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
                    final NSDictionary configurationDict = new AppConfigDataHandler().parseJsonToIosAppConfig(new JSONArray(configdata));
                    appsPayload.setConfiguration(configurationDict);
                }
                final Criteria appIdCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
                final Row appDetailsRow = dataObject.getRow("MdAppDetails", appIdCriteria);
                final String identifier = (String)appDetailsRow.get("IDENTIFIER");
                if (identifier.equalsIgnoreCase("com.manageengine.mdm.iosagent")) {
                    appsPayload.setConfiguration(MDMiOSEntrollmentUtil.getMDMDefaultAppConfiguration());
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in creating IOS AppsPayload ", ex);
        }
        appsPayloadArray[0] = appsPayload;
        return appsPayloadArray;
    }
}
