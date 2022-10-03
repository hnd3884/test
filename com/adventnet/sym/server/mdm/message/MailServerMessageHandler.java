package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.logging.Logger;

public class MailServerMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        try {
            if (!MDMEnrollmentUtil.getInstance().isMailServerConfigured()) {
                if (LicenseProvider.getInstance().getLicenseType() != "T") {
                    return false;
                }
                if (MDMEnrollmentUtil.getInstance().getEnrolledDeviceCount(customerId) >= 5) {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            MailServerMessageHandler.logger.log(Level.SEVERE, "Exception while getting device count details ", ex);
        }
        return true;
    }
    
    static {
        MailServerMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
