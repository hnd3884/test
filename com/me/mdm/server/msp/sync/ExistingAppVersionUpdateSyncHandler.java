package com.me.mdm.server.msp.sync;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.files.FileFacade;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ExistingAppVersionUpdateSyncHandler extends AppsSyncEngine
{
    String existingAppUniqueIdentifier;
    String existingAppIdentifier;
    String existingAppVersion;
    String existingAppVersionCode;
    
    ExistingAppVersionUpdateSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.existingAppUniqueIdentifier = this.requestJSON.getString("existing_app_unique_identifier");
        final String[] appUniqueIds = this.existingAppUniqueIdentifier.split("@@@");
        this.existingAppIdentifier = appUniqueIds[0];
        this.existingAppVersion = appUniqueIds[1];
        this.existingAppVersionCode = appUniqueIds[2];
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)this.existingAppIdentifier, 0);
        final Criteria appVersionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)this.existingAppVersion, 0);
        final Criteria appVersionCodeCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)this.existingAppVersionCode, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)this.platform, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(appIdentifierCriteria.and(appVersionCriteria).and(appVersionCodeCriteria).and(platformCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row packageRow = dataObject.getFirstRow("MdPackage");
            final Row releaseLabelRow = dataObject.getFirstRow("AppReleaseLabel");
            final Long packageId = (Long)packageRow.get("PACKAGE_ID");
            final Long labelId = (Long)releaseLabelRow.get("RELEASE_LABEL_ID");
            response.put("app_id", (Object)packageId);
            response.put("label_id", (Object)labelId);
        }
        else {
            ExistingAppVersionUpdateSyncHandler.logger.log(Level.SEVERE, "App version not present in child customer {0} props {1}", new Object[] { customerID, this.parentProfileDO });
        }
        return response;
    }
    
    @Override
    public void sync() {
        try {
            this.setParentDO();
            ExistingAppVersionUpdateSyncHandler.logger.log(Level.INFO, "App version update from parent customer {0} request -> {1}", new Object[] { this.customerId, this.requestJSON });
            List applicableCustomers;
            if (this.childCustomerId == -1L) {
                applicableCustomers = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            }
            else {
                final Long[] array;
                applicableCustomers = new ArrayList(Arrays.asList(array));
                array = new Long[] { this.childCustomerId };
            }
            final List customerList = applicableCustomers;
            this.createTempFilesForBinaries();
            for (final Long customerID : customerList) {
                CustomerInfoThreadLocal.setCustomerId(customerID.toString());
                this.childSpecificRequest = new JSONObject(this.requestJSON.toString());
                try {
                    if (!MDMStringUtils.isEmpty(this.appTempFileName)) {
                        this.childSpecificRequest.put("app_file", (Object)this.appTempFileName);
                        this.childSpecificRequest.put("parent_app_path", (Object)this.parentAppFileLoc);
                    }
                    if (!MDMStringUtils.isEmpty(this.displayImageTempFileName)) {
                        this.childSpecificRequest.put("display_image", (Object)this.displayImageTempFileName);
                        this.childSpecificRequest.put("parent_display_image_path", (Object)this.parentDisplayImageLoc);
                    }
                    if (!MDMStringUtils.isEmpty(this.fullImageTempFileName)) {
                        this.childSpecificRequest.put("full_image", (Object)this.fullImageTempFileName);
                        this.childSpecificRequest.put("parent_full_image_path", (Object)this.parentFullImageLoc);
                    }
                    this.updateCustomerSpecificUVHKeys(customerID);
                    ExistingAppVersionUpdateSyncHandler.logger.log(Level.INFO, "App version update for child customer {0} request {1}", new Object[] { customerID, this.childSpecificRequest });
                    final String appName = this.childSpecificRequest.optString("app_name");
                    Long labelId = APIUtil.getResourceID(this.childSpecificRequest, "label_id");
                    labelId = ((labelId == -1L) ? AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID) : labelId);
                    final String channelName = AppVersionDBUtil.getInstance().getChannelName(labelId);
                    final JSONObject response = this.getInstance().updateEnterpriseApp(this.childSpecificRequest);
                    if (this.appTempFileName == null) {
                        continue;
                    }
                    final String sEventLogRemarks = "mdm.action.app_version_update_over_existing";
                    final String remarksArgs = appName + "@@@" + this.existingAppVersion + "@@@" + channelName + "@@@" + appName + "@@@" + response.optString("APP_VERSION", "__") + "@@@" + channelName;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(this.eventLogConstant, null, this.userName, sEventLogRemarks, remarksArgs, customerID);
                }
                catch (final Exception ex) {
                    ExistingAppVersionUpdateSyncHandler.logger.log(Level.SEVERE, "Exception in ExistingAppVersionUpdateSyncHandler for customer {0} for props {1} {2}", new Object[] { customerID, this.parentProfileDO, ex });
                }
            }
        }
        catch (final Exception ex2) {
            ExistingAppVersionUpdateSyncHandler.logger.log(Level.SEVERE, "Exception in ExistingAppVersionUpdateSyncHandler for props {0} {1}", new Object[] { this.parentProfileDO, ex2 });
            try {
                if (this.appTempFileName != null) {
                    new FileFacade().deleteFile(this.appTempFileName);
                }
                if (this.displayImageTempFileName != null) {
                    new FileFacade().deleteFile(this.displayImageTempFileName);
                }
                if (this.fullImageTempFileName != null) {
                    new FileFacade().deleteFile(this.fullImageTempFileName);
                }
            }
            catch (final Exception exception) {
                ExistingAppVersionUpdateSyncHandler.logger.log(Level.SEVERE, "Exception in deleting temp files in ExistingAppVersionUpdateSyncHandler", exception);
            }
        }
        finally {
            try {
                if (this.appTempFileName != null) {
                    new FileFacade().deleteFile(this.appTempFileName);
                }
                if (this.displayImageTempFileName != null) {
                    new FileFacade().deleteFile(this.displayImageTempFileName);
                }
                if (this.fullImageTempFileName != null) {
                    new FileFacade().deleteFile(this.fullImageTempFileName);
                }
            }
            catch (final Exception exception2) {
                ExistingAppVersionUpdateSyncHandler.logger.log(Level.SEVERE, "Exception in deleting temp files in ExistingAppVersionUpdateSyncHandler", exception2);
            }
        }
    }
}
