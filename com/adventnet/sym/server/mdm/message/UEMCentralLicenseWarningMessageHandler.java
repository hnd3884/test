package com.adventnet.sym.server.mdm.message;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Logger;

public class UEMCentralLicenseWarningMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        return MDMApiFactoryProvider.getMDMUtilAPI().showEndpointCentralLicenseMessageBox("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING");
    }
    
    static {
        UEMCentralLicenseWarningMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
