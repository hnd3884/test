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

public class AppVersionAddedAsNewSyncHandler extends AppsSyncEngine
{
    AppVersionAddedAsNewSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)this.platform, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(appIdentifierCriteria.and(platformCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row packageRow = dataObject.getFirstRow("MdPackage");
            final Long packageId = (Long)packageRow.get("PACKAGE_ID");
            response.put("app_id", (Object)packageId);
        }
        else {
            AppVersionAddedAsNewSyncHandler.logger.log(Level.SEVERE, "App identifier not present in child customer {0} props {1}", new Object[] { customerID, this.parentProfileDO });
        }
        return response;
    }
    
    @Override
    protected void updateCustomerSpecificUVHKeys(final Long customerID) throws Exception {
        this.childSpecificRequest.put("customerID", (Object)customerID);
        if (this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("filters").has("customer_id")) {
            this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("filters").put("customer_id", (Object)customerID);
        }
        final JSONObject childSpecificUVH = this.getChildSpecificUVH(customerID);
        if (this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").has("app_id")) {
            this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").put("app_id", childSpecificUVH.getLong("app_id"));
        }
        if (this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").has("label_id")) {
            this.cloneReleaseLabelForChildCustomer(customerID);
        }
    }
    
    @Override
    protected Long getLabelId() {
        return this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").getLong("label_id");
    }
    
    @Override
    protected void setLabelId(final Long labelId) {
        this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").put("label_id", (Object)labelId);
    }
    
    @Override
    public void sync() {
        try {
            this.setParentDO();
            AppVersionAddedAsNewSyncHandler.logger.log(Level.INFO, "AppVersionAdded as new triggered from parent customer {0} request -> {1}", new Object[] { this.customerId, this.requestJSON });
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
                    if (MDMStringUtils.isEmpty(this.appTempFileName)) {
                        AppVersionAddedAsNewSyncHandler.logger.log(Level.SEVERE, "App path is empty");
                        throw new NullPointerException();
                    }
                    this.childSpecificRequest.put("app_file", (Object)this.appTempFileName);
                    this.childSpecificRequest.put("parent_app_path", (Object)this.parentAppFileLoc);
                    if (!MDMStringUtils.isEmpty(this.displayImageTempFileName)) {
                        this.childSpecificRequest.put("display_image", (Object)this.displayImageTempFileName);
                        this.childSpecificRequest.put("parent_display_image_path", (Object)this.parentDisplayImageLoc);
                    }
                    if (!MDMStringUtils.isEmpty(this.fullImageTempFileName)) {
                        this.childSpecificRequest.put("full_image", (Object)this.fullImageTempFileName);
                        this.childSpecificRequest.put("parent_full_image_path", (Object)this.parentFullImageLoc);
                    }
                    this.updateCustomerSpecificUVHKeys(customerID);
                    final String appName = this.childSpecificRequest.optString("app_name");
                    Long labelId = APIUtil.getResourceID(this.childSpecificRequest, "label_id");
                    labelId = ((labelId == -1L) ? AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID) : labelId);
                    final String channelName = AppVersionDBUtil.getInstance().getChannelName(labelId);
                    AppVersionAddedAsNewSyncHandler.logger.log(Level.INFO, "Adding new app version for child customer {0} request ->", new Object[] { customerID, this.childSpecificRequest });
                    final JSONObject response = this.getInstance().updateEnterpriseApp(this.childSpecificRequest);
                    final String sEventLogRemarks = "mdm.actionlog.appmgmt.version_added_as_separate";
                    final String remarksArgs = appName + "@@@" + response.optString("APP_VERSION", "__") + "@@@" + channelName;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(this.eventLogConstant, null, this.userName, sEventLogRemarks, remarksArgs, customerID);
                }
                catch (final Exception ex) {
                    AppVersionAddedAsNewSyncHandler.logger.log(Level.SEVERE, "Exception in AppVersionAddedAsNewSyncHandler for customer {0} for props {1} {2}", new Object[] { customerID, this.parentProfileDO, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppVersionAddedAsNewSyncHandler.logger.log(Level.SEVERE, "Exception in AppVersionAddedAsNewSyncHandler for props {0} {1}", new Object[] { this.parentProfileDO, ex2 });
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
            catch (final Exception ex2) {
                AppVersionAddedAsNewSyncHandler.logger.log(Level.SEVERE, "Exception in deleting temp files in AppVersionAddedAdNewSyncHandler", ex2);
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
            catch (final Exception ex3) {
                AppVersionAddedAsNewSyncHandler.logger.log(Level.SEVERE, "Exception in deleting temp files in AppVersionAddedAdNewSyncHandler", ex3);
            }
        }
    }
}
