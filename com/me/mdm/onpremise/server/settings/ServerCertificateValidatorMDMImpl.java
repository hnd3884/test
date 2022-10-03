package com.me.mdm.onpremise.server.settings;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Logger;
import com.me.ems.onpremise.security.certificate.api.core.listeners.ServerCertificateValidator;

public class ServerCertificateValidatorMDMImpl implements ServerCertificateValidator
{
    private static final Logger logger;
    
    public boolean canRegenerateServerCertificate() {
        final int managedDeviceCount = ManagedDeviceHandler.getInstance().getAppleManagedDeviceCount() + ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount();
        ServerCertificateValidatorMDMImpl.logger.log(Level.SEVERE, "Apple and Windows Device count before certificate regenertion : {0}", managedDeviceCount);
        if (managedDeviceCount > 0) {
            MessageProvider.getInstance().unhideMessage("MDM_IMPORT_3P_CERT_IOS_ROOT_REGEN");
            return false;
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger("ImportCertificateLogger");
    }
}
