package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class MSPLicenseMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        try {
            if (CustomerInfoUtil.getInstance().isMSP()) {
                final MDMLicenseImplMSP mspLicense = new MDMLicenseImplMSP();
                MSPLicenseMessageHandler.logger.log(Level.INFO, "Inside MSP DeviceAllocation");
                if (!mspLicense.isMobileDeviceLicenseReached(customerId)) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            MSPLicenseMessageHandler.logger.log(Level.SEVERE, "Exception While Checking License Exceed for  MSP{0}", ex);
        }
        return false;
    }
    
    static {
        MSPLicenseMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
