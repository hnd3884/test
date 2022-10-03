package com.me.mdm.server.settings;

import com.me.devicemanagement.framework.server.license.LicenseEvent;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MailServerMessageListener extends ManagedDeviceListener implements LicenseListener
{
    public static Logger logger;
    
    @Override
    public void deviceManaged(final DeviceEvent userEvent) {
        MailServerMessageListener.mdmlogger.info("Entering MailServerMessageListener:deviceManaged");
        MDMMessageHandler.getInstance().messageAction("MAIL_SERVER_NOT_CONFIGURED", null);
        MailServerMessageListener.mdmlogger.info("Exiting MailServerMessageListener:deviceManaged");
    }
    
    public void licenseChanged(final LicenseEvent licenseEvent) {
        MailServerMessageListener.mdmlogger.info("Entering MailServerMessageListener:licenseChanged");
        MDMMessageHandler.getInstance().messageAction("MAIL_SERVER_NOT_CONFIGURED", null);
        MailServerMessageListener.mdmlogger.info("Exiting MailServerMessageListener:licenseChanged");
    }
    
    static {
        MailServerMessageListener.logger = Logger.getLogger("MDMLogger");
    }
}
