package com.me.mdm.onpremise.server.settings.cca;

import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.settings.cca.CcaChangeListener;

public class CcaChangeListenerMdmImpl implements CcaChangeListener
{
    private static final Logger LOGGER;
    
    public void onClientCertAuthChange(final boolean isBeingEnabled) {
        try {
            CcaChangeListenerMdmImpl.LOGGER.log(Level.INFO, "Is Client Cert auth being enabled : {0} | Invoking necessary changes.", new Object[] { isBeingEnabled });
            DEPEnrollmentUtil.createAndAssignAllDEPProfileAsynchronously();
            AppleConfiguratorEnrollmentHandler.openUrlChangeMsg();
        }
        catch (final Exception e) {
            CcaChangeListenerMdmImpl.LOGGER.log(Level.SEVERE, "Exception while handling client cert auth change event in MDM: ", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("ServerSettingsLogger");
    }
}
