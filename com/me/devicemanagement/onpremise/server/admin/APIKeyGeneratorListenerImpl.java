package com.me.devicemanagement.onpremise.server.admin;

import com.me.ems.onpremise.server.core.EnforceSecurePortUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.onpremise.uac.core.TFAUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.admin.APIKeyGeneratorListener;

public class APIKeyGeneratorListenerImpl implements APIKeyGeneratorListener
{
    private static String sourceClass;
    private final Logger logger;
    
    public APIKeyGeneratorListenerImpl() {
        this.logger = Logger.getLogger(APIKeyGeneratorListenerImpl.sourceClass);
    }
    
    public void addOrUpdateAPIKey(final String scope) {
        final String sourceMethod = "addOrUpdateAPIKey";
        this.logger.info(APIKeyGeneratorListenerImpl.sourceClass + " : " + sourceMethod + ":: " + "invoked");
        if (TFAUtil.isTFABannerEnabled()) {
            SyMUtil.updateServerParameter("IsTFABannerEnabled", "true");
        }
        else {
            EnforceSecurePortUtil.updateEnforcingDate();
        }
    }
    
    static {
        APIKeyGeneratorListenerImpl.sourceClass = APIKeyGeneratorListenerImpl.class.getName();
    }
}
