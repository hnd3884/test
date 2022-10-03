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

public class AppConfigurationAddSyncHandler extends AppsSyncEngine
{
    AppConfigurationAddSyncHandler(final DCQueueData dcQueueData) {
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
            final Iterator<Long> iterator = customerList.iterator();
            AppConfigurationAddSyncHandler.logger.log(Level.INFO, "Adding app configuration for child customer from parent {0} request -> {1}", new Object[] { this.customerId, this.requestJSON });
            while (iterator.hasNext()) {
                final Long childCustomerId = iterator.next();
                try {
                    CustomerInfoThreadLocal.setCustomerId(childCustomerId.toString());
                    this.childSpecificRequest = new JSONObject(this.requestJSON.toString());
                    this.updateCustomerSpecificUVHKeys(childCustomerId);
                    AppConfigurationAddSyncHandler.logger.log(Level.INFO, "Adding app configuration for child customer {0} request -> {1}", new Object[] { childCustomerId, this.childSpecificRequest });
                    this.getInstance().addAppConfiguration(this.childSpecificRequest);
                }
                catch (final Exception ex) {
                    AppConfigurationAddSyncHandler.logger.log(Level.SEVERE, "Exception in add/update app configuration for child customer {0} props :{1} {2}", new Object[] { childCustomerId, this.parentProfileDO, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppConfigurationAddSyncHandler.logger.log(Level.SEVERE, "Exception in AppConfigurationAddSyncHandler", ex2);
        }
    }
}
