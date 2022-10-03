package com.me.mdm.server.msp.sync;

import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class AppVersionApprovalSyncHandler extends AppsSyncEngine
{
    AppVersionApprovalSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void sync() {
        try {
            AppVersionApprovalSyncHandler.logger.log(Level.INFO, "App version deleted for parent customer {0} request -> {1}", new Object[] { this.customerId, this.requestJSON });
            this.setParentDO();
            final List<Long> customerList = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            for (final Long customerID : customerList) {
                CustomerInfoThreadLocal.setCustomerId(customerID.toString());
                this.childSpecificRequest = new JSONObject(this.requestJSON.toString());
                try {
                    this.updateCustomerSpecificUVHKeys(customerID);
                    AppVersionApprovalSyncHandler.logger.log(Level.INFO, "App version approval for child customer {0} request -> {1}", new Object[] { customerID, this.childSpecificRequest });
                    this.getInstance().approveAppVersion(this.childSpecificRequest);
                }
                catch (final Exception ex) {
                    AppVersionApprovalSyncHandler.logger.log(Level.SEVERE, "Exception in AppVersionApprovalSyncHandler for customer {0} for props {1} {2}", new Object[] { customerID, this.parentProfileDO, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppVersionApprovalSyncHandler.logger.log(Level.SEVERE, "Exception in AppVersionApprovalSyncHandler {0} {1}", new Object[] { this.parentProfileDO, ex2 });
        }
    }
}
