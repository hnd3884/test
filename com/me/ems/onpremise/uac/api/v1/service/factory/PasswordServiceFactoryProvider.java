package com.me.ems.onpremise.uac.api.v1.service.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class PasswordServiceFactoryProvider
{
    static Logger logger;
    static final String PASSWORD_SERVICE_STANDALONE = "com.me.ems.onpremise.uac.api.v1.service.ChangePasswordServiceImpl";
    static final String PASSWORD_SERVICE_SUMMARY = "com.me.ems.onpremise.uac.api.v1.service.summaryserver.summary.SSChangePasswordServiceImpl";
    static final String PASSWORD_SERVICE_PROBE = "com.me.ems.onpremise.uac.api.v1.service.summaryserver.probe.PSChangePasswordServiceImpl";
    private static ChangePasswordService changePasswordService;
    
    private PasswordServiceFactoryProvider() {
    }
    
    public static ChangePasswordService getChangePasswordServiceObject() {
        try {
            if (PasswordServiceFactoryProvider.changePasswordService == null) {
                if (SyMUtil.isProbeServer()) {
                    PasswordServiceFactoryProvider.changePasswordService = (ChangePasswordService)Class.forName("com.me.ems.onpremise.uac.api.v1.service.summaryserver.probe.PSChangePasswordServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    PasswordServiceFactoryProvider.changePasswordService = (ChangePasswordService)Class.forName("com.me.ems.onpremise.uac.api.v1.service.summaryserver.summary.SSChangePasswordServiceImpl").newInstance();
                }
                else {
                    PasswordServiceFactoryProvider.changePasswordService = (ChangePasswordService)Class.forName("com.me.ems.onpremise.uac.api.v1.service.ChangePasswordServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            PasswordServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting ChangePasswordServiceObject", e);
        }
        return PasswordServiceFactoryProvider.changePasswordService;
    }
    
    static {
        PasswordServiceFactoryProvider.logger = Logger.getLogger(PasswordServiceFactoryProvider.class.getName());
        PasswordServiceFactoryProvider.changePasswordService = null;
    }
}
