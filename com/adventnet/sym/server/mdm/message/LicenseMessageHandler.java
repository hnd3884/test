package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.logging.Logger;

public class LicenseMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        try {
            if (!MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId)) {
                return true;
            }
            return false;
        }
        catch (final Exception ex) {
            LicenseMessageHandler.logger.log(Level.SEVERE, "Exception while getting details if mobile device license has reached", ex);
            return false;
        }
    }
    
    static {
        LicenseMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
