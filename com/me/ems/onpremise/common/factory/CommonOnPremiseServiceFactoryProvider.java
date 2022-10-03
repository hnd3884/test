package com.me.ems.onpremise.common.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class CommonOnPremiseServiceFactoryProvider
{
    private static Logger logger;
    private static SmtpService smtpService;
    private static RequestDemoService requestDemoService;
    
    public static SmtpService getSmtpService() {
        try {
            if (CommonOnPremiseServiceFactoryProvider.smtpService == null) {
                if (SyMUtil.isProbeServer()) {
                    CommonOnPremiseServiceFactoryProvider.smtpService = (SmtpService)Class.forName("com.me.ems.onpremise.common.summaryserver.probe.api.v1.service.PSSmtpServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    CommonOnPremiseServiceFactoryProvider.smtpService = (SmtpService)Class.forName("com.me.ems.onpremise.common.summaryserver.summary.api.v1.service.SSSmtpServiceImpl").newInstance();
                }
                else {
                    CommonOnPremiseServiceFactoryProvider.smtpService = (SmtpService)Class.forName("com.me.ems.onpremise.common.api.v1.service.SmtpServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            CommonOnPremiseServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting SmtpServiceObject", e);
        }
        return CommonOnPremiseServiceFactoryProvider.smtpService;
    }
    
    public static RequestDemoService getRequestDemoService() {
        try {
            if (CommonOnPremiseServiceFactoryProvider.requestDemoService == null) {
                if (SyMUtil.isProbeServer()) {
                    CommonOnPremiseServiceFactoryProvider.requestDemoService = (RequestDemoService)Class.forName("com.me.ems.onpremise.common.summaryserver.probe.api.v1.service.PSRequestDemoServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    CommonOnPremiseServiceFactoryProvider.requestDemoService = (RequestDemoService)Class.forName("com.me.ems.onpremise.common.summaryserver.summary.api.v1.service.SSRequestDemoServiceImpl").newInstance();
                }
                else {
                    CommonOnPremiseServiceFactoryProvider.requestDemoService = (RequestDemoService)Class.forName("com.me.ems.onpremise.common.api.v1.service.RequestDemoServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            CommonOnPremiseServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting RequestDemoServiceObject", e);
        }
        return CommonOnPremiseServiceFactoryProvider.requestDemoService;
    }
    
    static {
        CommonOnPremiseServiceFactoryProvider.logger = Logger.getLogger(CommonOnpremiseFactoryConstant.class.getName());
        CommonOnPremiseServiceFactoryProvider.smtpService = null;
        CommonOnPremiseServiceFactoryProvider.requestDemoService = null;
    }
}
