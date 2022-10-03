package com.me.mdm.server.msp.sync;

import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class AppConfigurationDeleteSyncHandler extends AppsSyncEngine
{
    AppConfigurationDeleteSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void sync() {
        try {
            this.setParentDO();
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
            AppConfigurationDeleteSyncHandler.logger.log(Level.INFO, "Deleting app configuration from parent customer {0} request -> {1}", new Object[] { this.customerId, this.requestJSON });
            for (final Long childCustomerId : customerList) {
                try {
                    CustomerInfoThreadLocal.setCustomerId(childCustomerId.toString());
                    this.childSpecificRequest = new JSONObject(this.requestJSON.toString());
                    this.updateCustomerSpecificUVHKeys(childCustomerId);
                    AppConfigurationDeleteSyncHandler.logger.log(Level.INFO, "Deleting app configuration for child customer {0} request -> {1}", new Object[] { childCustomerId, this.childSpecificRequest });
                    this.getInstance().deleteAppConfiguration(this.childSpecificRequest);
                }
                catch (final Exception ex) {
                    AppConfigurationDeleteSyncHandler.logger.log(Level.SEVERE, "Exception in delete app configuration for child customer {0} props :{1} {2}", new Object[] { childCustomerId, this.parentProfileDO, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppConfigurationDeleteSyncHandler.logger.log(Level.SEVERE, "Exception in AppConfigurationDeleteSyncHandler", ex2);
        }
    }
}
