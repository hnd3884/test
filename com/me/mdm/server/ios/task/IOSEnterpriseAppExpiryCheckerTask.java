package com.me.mdm.server.ios.task;

import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class IOSEnterpriseAppExpiryCheckerTask
{
    private static final Logger LOGGER;
    
    public void executeTask() {
        try {
            final Long startTime = MDMUtil.getCurrentTime();
            IOSEnterpriseAppExpiryCheckerTask.LOGGER.log(Level.INFO, "START:: Removing distribution details for expired enterprise app details at : {0}", new Object[] { MDMUtil.getCurrentTime() });
            final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (int i = 0; i < customerIDs.length; ++i) {
                try {
                    new IOSAppUtils().removeExpiredEnterpriseAppFromAppCatalog(customerIDs[i]);
                    final Long endTime = MDMUtil.getCurrentTime();
                    IOSEnterpriseAppExpiryCheckerTask.LOGGER.log(Level.INFO, "END:: Removed distribution details at: {0}", new Object[] { MDMUtil.getCurrentTime() });
                    IOSEnterpriseAppExpiryCheckerTask.LOGGER.log(Level.INFO, "Time spent on task: {0}", endTime - startTime);
                }
                catch (final Exception e) {
                    IOSEnterpriseAppExpiryCheckerTask.LOGGER.log(Level.SEVERE, "Exception while updating AppCatalog Details for expired Apps", e);
                }
            }
        }
        catch (final Exception ex) {
            IOSEnterpriseAppExpiryCheckerTask.LOGGER.log(Level.SEVERE, "Exception in IOSEnterpriseAppExpiryCheckerTask", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMAppMgmtLogger");
    }
}
