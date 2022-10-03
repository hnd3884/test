package com.me.mdm.server.apps.ios.task;

import org.json.JSONArray;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.apache.commons.io.FileUtils;
import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import com.me.mdm.server.apps.IOSAppDatahandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.ios.IosIPAExtractor;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.FileFacade;
import java.io.File;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class IOSExtarctProvisioningDetails implements SchedulerExecutionInterface
{
    private final Logger logger;
    
    public IOSExtarctProvisioningDetails() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        Long customerId = null;
        try {
            final String isiOSEnterpriseAppExtractionNeeded = MDMUtil.getSyMParameter("isiOSEnterpriseAppExtractionNeeded");
            final String ipaFilesMigrationStatus = MDMUtil.getSyMParameter("RePublishAppProfiles");
            boolean isIPAFilesMigrating = false;
            if (ipaFilesMigrationStatus != null && !ipaFilesMigrationStatus.isEmpty()) {
                isIPAFilesMigrating = Boolean.parseBoolean(ipaFilesMigrationStatus);
            }
            if (isiOSEnterpriseAppExtractionNeeded != null && !isiOSEnterpriseAppExtractionNeeded.isEmpty() && Boolean.parseBoolean(isiOSEnterpriseAppExtractionNeeded) && !isIPAFilesMigrating) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
                sQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                sQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
                sQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                sQuery.addSelectColumn(new Column("MdPackageToAppData", "*"));
                sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "*"));
                sQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
                sQuery.addSelectColumn(new Column("MdAppDetails", "*"));
                final Criteria enterprisePkgTypeCri = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
                final Criteria platformCri = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
                final Criteria notMacOnlyCrit = new Criteria(new Column("MdPackageToAppData", "SUPPORTED_DEVICES"), (Object)16, 1);
                sQuery.setCriteria(enterprisePkgTypeCri.and(platformCri).and(notMacOnlyCrit));
                final DataObject packageDO = MDMUtil.getPersistence().get(sQuery);
                final Iterator iter = packageDO.getRows("MdPackageToAppData");
                while (iter.hasNext()) {
                    final JSONObject ipaProps = new JSONObject();
                    final Row mdPackageToAPpDataRow = iter.next();
                    final Long appId = (Long)mdPackageToAPpDataRow.get("APP_ID");
                    final Long appGroupId = (Long)mdPackageToAPpDataRow.get("APP_GROUP_ID");
                    final Long packageId = (Long)mdPackageToAPpDataRow.get("PACKAGE_ID");
                    final String appFileLoc = MDMAppMgmtHandler.getInstance().getAppRepositoryBaseFolderPath() + mdPackageToAPpDataRow.get("APP_FILE_LOC");
                    customerId = (Long)packageDO.getValue("MdAppGroupDetails", "CUSTOMER_ID", new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0));
                    final String appSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(customerId);
                    final String appSourceIPADestPath = appSourceFolderDestPath + File.separator + "ipafile.ipa";
                    try {
                        new FileFacade().writeFile(appSourceIPADestPath, ApiFactoryProvider.getFileAccessAPI().readFile(appFileLoc));
                        final JSONArray metaData = new IosIPAExtractor().extractInfoAndPPFilesFromIPA(appSourceIPADestPath, appSourceFolderDestPath);
                        ipaProps.put("metaData", (Object)metaData);
                        ipaProps.put("metaDataLoc", (Object)appSourceFolderDestPath);
                        ipaProps.put("APP_ID", (Object)appId);
                        ipaProps.put("APP_GROUP_ID", (Object)appGroupId);
                        ipaProps.put("PACKAGE_ID", (Object)packageId);
                        ipaProps.put("CUSTOMER_ID", (Object)customerId);
                        this.logger.log(Level.SEVERE, "START::Going to extract App details for App{0}", ipaProps);
                        new IOSAppDatahandler(ipaProps).saveMetaData(ipaProps);
                        this.logger.log(Level.SEVERE, "END:: Extracted App details");
                        final int provType = new IOSAppUtils().getEnterpriseAppProvisionSignedType(appId);
                        if ((provType != 1 && provType != 2) || provType == -1) {
                            continue;
                        }
                        this.logger.log(Level.INFO, "App provisioning type for the app with app_id = {0} is {1}", new Object[] { appId, provType });
                        this.logger.log(Level.INFO, "START:: Going to remove App Catalog details and change distribution status for Ad-Hoc unregistered devices at : {0}", new Object[] { MDMUtil.getCurrentTime() });
                        new IOSAppUtils().updateAlreadyDistributedAdhocAppDetails(appId, customerId);
                        this.logger.log(Level.INFO, "END:: Removed App Catalog details and Changed distribution status for Ad-Hoc unregistered devices at : {0}", new Object[] { MDMUtil.getCurrentTime() });
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Exception in IOSExtarctProvisioningDetails", e);
                    }
                    finally {
                        final File file = new File(appSourceFolderDestPath);
                        FileUtils.deleteDirectory(file);
                    }
                }
                MDMUtil.updateSyMParameter("isiOSEnterpriseAppExtractionNeeded", "false");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in IOSExtarctProvisioningDetails", ex);
            MDMUtil.updateSyMParameter("isiOSEnterpriseAppExtractionNeeded", "false");
        }
    }
}
