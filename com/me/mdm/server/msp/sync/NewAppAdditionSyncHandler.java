package com.me.mdm.server.msp.sync;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.files.FileFacade;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class NewAppAdditionSyncHandler extends AppsSyncEngine
{
    NewAppAdditionSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    protected void updateCustomerSpecificUVHKeys(final Long customerID) throws Exception {
        this.childSpecificRequest.put("customerID", (Object)customerID);
        if (this.childSpecificRequest.has("label_id")) {
            this.cloneReleaseLabelForChildCustomer(customerID);
        }
    }
    
    @Override
    protected Long getLabelId() {
        return this.childSpecificRequest.getLong("label_id");
    }
    
    @Override
    protected void setLabelId(final Long labelId) {
        this.childSpecificRequest.put("label_id", (Object)labelId);
    }
    
    @Override
    public void sync() {
        try {
            this.setParentDO();
            NewAppAdditionSyncHandler.logger.log(Level.INFO, "New app added in parent customer {0} request -> {1}", new Object[] { this.customerId, this.requestJSON });
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
                        NewAppAdditionSyncHandler.logger.log(Level.SEVERE, "App path is empty");
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
                    NewAppAdditionSyncHandler.logger.log(Level.INFO, "New app addition for child customer {0} request {1}", new Object[] { customerID, this.childSpecificRequest });
                    final String appName = this.childSpecificRequest.getString("app_name");
                    final Long labelId = this.childSpecificRequest.optLong("label_id", (long)AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID));
                    final String labelName = AppVersionDBUtil.getInstance().getChannelName(labelId);
                    final JSONObject response = this.getInstance().addEnterpriseApp(this.childSpecificRequest);
                    final String remarksArgs = appName + "@@@" + response.optString("APP_VERSION") + "@@@" + labelName;
                    final String sEventLogRemarks = "dc.mdm.actionlog.appmgmt.new_add_success";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(this.eventLogConstant, null, this.userName, sEventLogRemarks, remarksArgs, customerID);
                }
                catch (final Exception ex) {
                    NewAppAdditionSyncHandler.logger.log(Level.SEVERE, "Exception in NewAppAdditionSyncHandler for customer {0} for props {1} {2}", new Object[] { customerID, this.parentProfileDO, ex });
                }
            }
        }
        catch (final Exception ex2) {
            NewAppAdditionSyncHandler.logger.log(Level.SEVERE, "Exception in NewAppAdditionSyncHandler {0} {1}", new Object[] { this.parentProfileDO, ex2 });
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
                NewAppAdditionSyncHandler.logger.log(Level.SEVERE, "Exception in deleting temp files in NewAppAdditionSyncHandler", exception);
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
                NewAppAdditionSyncHandler.logger.log(Level.SEVERE, "Exception in deleting temp files in NewAppAdditionSyncHandler", exception2);
            }
        }
    }
}
