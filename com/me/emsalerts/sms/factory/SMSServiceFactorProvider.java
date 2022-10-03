package com.me.emsalerts.sms.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class SMSServiceFactorProvider
{
    static Logger logger;
    private static SMSService smsService;
    
    public static SMSService getSmsService() {
        try {
            if (SMSServiceFactorProvider.smsService == null) {
                if (SyMUtil.isProbeServer()) {
                    SMSServiceFactorProvider.smsService = (SMSService)Class.forName("com.me.emsalerts.sms.summaryserver.probe.api.v1.service.PSSMSServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    SMSServiceFactorProvider.smsService = (SMSService)Class.forName("com.me.emsalerts.sms.summaryserver.summary.api.v1.service.SSSMSServiceImpl").newInstance();
                }
                else {
                    SMSServiceFactorProvider.smsService = (SMSService)Class.forName("com.me.emsalerts.sms.api.v1.service.SMSServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            SMSServiceFactorProvider.logger.log(Level.SEVERE, "Exception in getting SMSServiceObject", e);
        }
        return SMSServiceFactorProvider.smsService;
    }
    
    static {
        SMSServiceFactorProvider.logger = Logger.getLogger("EMSAlertsLogger");
        SMSServiceFactorProvider.smsService = null;
    }
}
