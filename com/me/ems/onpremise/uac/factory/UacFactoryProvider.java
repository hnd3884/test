package com.me.ems.onpremise.uac.factory;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.ems.onpremise.uac.api.v1.service.factory.UserAccountService;
import java.util.logging.Logger;

public class UacFactoryProvider
{
    static final Logger LOGGER;
    static final String USER_VALIDATOR_STANDALONE = "com.me.ems.onpremise.uac.validators.UserAccountValidatorImpl";
    static final String USER_VALIDATOR_SUMMARY = "com.me.ems.onpremise.uac.summaryserver.summary.api.validators.SSUserAccountValidatorImpl";
    static final String USER_VALIDATOR_PROBE = "com.me.ems.onpremise.uac.summaryserver.probe.api.validators.PSUserAccountValidatorImpl";
    static final String USER_SERVICE_STANDALONE = "com.me.ems.onpremise.uac.api.v1.service.UserAccountServiceImpl";
    static final String USER_SERVICE_SUMMARY = "com.me.ems.onpremise.uac.api.v1.service.summaryserver.summary.SSUserAccountServiceImpl";
    static final String USER_SERVICE_PROBE = "com.me.ems.onpremise.uac.api.v1.service.summaryserver.probe.PSUserAccountServiceImpl";
    private static UserAccountService userAccountService;
    private static UserAccountValidator userAccountValidator;
    
    private UacFactoryProvider() {
    }
    
    public static void resetObjects() {
        UacFactoryProvider.userAccountService = null;
        UacFactoryProvider.userAccountValidator = null;
    }
    
    public static UserAccountService getUserAccountServiceObject() {
        try {
            if (UacFactoryProvider.userAccountService == null) {
                UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountService' Object is null, Hence creating a new Object.");
                if (SyMUtil.isProbeServer()) {
                    UacFactoryProvider.userAccountService = (UserAccountService)Class.forName("com.me.ems.onpremise.uac.api.v1.service.summaryserver.probe.PSUserAccountServiceImpl").newInstance();
                    UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountService' Object is created with Probe Server Implementation.");
                }
                else if (SyMUtil.isSummaryServer()) {
                    UacFactoryProvider.userAccountService = (UserAccountService)Class.forName("com.me.ems.onpremise.uac.api.v1.service.summaryserver.summary.SSUserAccountServiceImpl").newInstance();
                    UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountService' Object is created with Summary Server Implementation.");
                }
                else {
                    UacFactoryProvider.userAccountService = (UserAccountService)Class.forName("com.me.ems.onpremise.uac.api.v1.service.UserAccountServiceImpl").newInstance();
                    UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountService' Object is created with Standalone DC Implementation.");
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException exception) {
            UacFactoryProvider.LOGGER.log(Level.SEVERE, "Exception in getting UserAccountService Object", exception);
        }
        return UacFactoryProvider.userAccountService;
    }
    
    public static UserAccountValidator getUserAccountValidatorObject() {
        try {
            if (UacFactoryProvider.userAccountValidator == null) {
                UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountValidator' Object is null, Hence creating a new Object.");
                if (SyMUtil.isProbeServer()) {
                    UacFactoryProvider.userAccountValidator = (UserAccountValidator)Class.forName("com.me.ems.onpremise.uac.summaryserver.probe.api.validators.PSUserAccountValidatorImpl").newInstance();
                    UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountValidator' Object is created with Probe Server Implementation.");
                }
                else if (SyMUtil.isSummaryServer()) {
                    UacFactoryProvider.userAccountValidator = (UserAccountValidator)Class.forName("com.me.ems.onpremise.uac.summaryserver.summary.api.validators.SSUserAccountValidatorImpl").newInstance();
                    UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountValidator' Object is created with Summary Server Implementation.");
                }
                else {
                    UacFactoryProvider.userAccountValidator = (UserAccountValidator)Class.forName("com.me.ems.onpremise.uac.validators.UserAccountValidatorImpl").newInstance();
                    UacFactoryProvider.LOGGER.log(Level.INFO, "'UserAccountValidator' Object is created with Standalone DC Implementation.");
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException exception) {
            UacFactoryProvider.LOGGER.log(Level.SEVERE, "Exception in getting UserAccountValidator Object", exception);
        }
        return UacFactoryProvider.userAccountValidator;
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
        UacFactoryProvider.userAccountService = null;
        UacFactoryProvider.userAccountValidator = null;
    }
}
