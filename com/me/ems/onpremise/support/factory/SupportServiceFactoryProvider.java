package com.me.ems.onpremise.support.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class SupportServiceFactoryProvider
{
    static Logger logger;
    private static SupportFileService supportFileService;
    
    public static SupportFileService getSupportFileService() {
        try {
            if (SupportServiceFactoryProvider.supportFileService == null) {
                if (SyMUtil.isProbeServer()) {
                    SupportServiceFactoryProvider.supportFileService = (SupportFileService)Class.forName("com.me.ems.onpremise.support.summaryserver.probe.api.v1.service.PSSupportFileServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    SupportServiceFactoryProvider.supportFileService = (SupportFileService)Class.forName("com.me.ems.onpremise.support.summaryserver.summary.api.v1.service.SSSupportFileServiceImpl").newInstance();
                }
                else {
                    SupportServiceFactoryProvider.supportFileService = (SupportFileService)Class.forName("com.me.ems.onpremise.support.api.v1.service.SupportFileServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            SupportServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting SupportFileServiceObject", e);
        }
        return SupportServiceFactoryProvider.supportFileService;
    }
    
    static {
        SupportServiceFactoryProvider.logger = Logger.getLogger(SupportServiceFactoryProvider.class.getName());
        SupportServiceFactoryProvider.supportFileService = null;
    }
}
