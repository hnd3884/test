package com.me.devicemanagement.onpremise.server.mail;

import com.me.ems.onpremise.server.core.EnforceSecurePortUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.onpremise.uac.core.TFAUtil;
import java.util.logging.Logger;

public class MailServerOnPremiseListenerImpl implements MailListener
{
    private static String sourceClass;
    private final Logger logger;
    
    public MailServerOnPremiseListenerImpl() {
        this.logger = Logger.getLogger(MailServerOnPremiseListenerImpl.sourceClass);
    }
    
    @Override
    public void mailConfigured() {
        final String sourceMethod = "mailConfigured";
        this.logger.info(MailServerOnPremiseListenerImpl.sourceClass + " : " + sourceMethod + ":: " + "invoked");
        if (TFAUtil.isTFABannerEnabled()) {
            SyMUtil.updateServerParameter("IsTFABannerEnabled", "true");
        }
        else {
            EnforceSecurePortUtil.updateEnforcingDate();
        }
    }
    
    static {
        MailServerOnPremiseListenerImpl.sourceClass = MailServerOnPremiseListenerImpl.class.getName();
    }
}
