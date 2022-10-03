package com.me.ems.framework.common.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class CommonServiceFactoryProvider
{
    static Logger logger;
    private static ExportSettingsService exportSettingsService;
    private static ActionLogService actionLogService;
    
    public static ExportSettingsService getExportSettingsService() {
        try {
            if (CommonServiceFactoryProvider.exportSettingsService == null) {
                if (SyMUtil.isProbeServer()) {
                    CommonServiceFactoryProvider.exportSettingsService = (ExportSettingsService)Class.forName("com.me.ems.framework.common.summaryserver.probe.api.v1.service.PSExportSettingsServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    CommonServiceFactoryProvider.exportSettingsService = (ExportSettingsService)Class.forName("com.me.ems.framework.common.summaryserver.summary.api.v1.service.SSExportSettingsServiceImpl").newInstance();
                }
                else {
                    CommonServiceFactoryProvider.exportSettingsService = (ExportSettingsService)Class.forName("com.me.ems.framework.common.api.v1.service.ExportSettingsServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            CommonServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting ExportSettingsServiceObject", e);
        }
        return CommonServiceFactoryProvider.exportSettingsService;
    }
    
    public static ActionLogService getActionLogService() {
        try {
            if (CommonServiceFactoryProvider.actionLogService == null) {
                if (SyMUtil.isProbeServer()) {
                    CommonServiceFactoryProvider.actionLogService = (ActionLogService)Class.forName("com.me.ems.framework.common.summaryserver.probe.api.v1.service.PSActionLogServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    CommonServiceFactoryProvider.actionLogService = (ActionLogService)Class.forName("com.me.ems.framework.common.summaryserver.summary.api.v1.service.SSActionLogServiceImpl").newInstance();
                }
                else {
                    CommonServiceFactoryProvider.actionLogService = (ActionLogService)Class.forName("com.me.ems.framework.common.api.v1.service.ActionLogServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            CommonServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting ActionLogServiceObject", e);
        }
        return CommonServiceFactoryProvider.actionLogService;
    }
    
    static {
        CommonServiceFactoryProvider.logger = Logger.getLogger(CommonServiceFactoryProvider.class.getName());
        CommonServiceFactoryProvider.exportSettingsService = null;
        CommonServiceFactoryProvider.actionLogService = null;
    }
}
