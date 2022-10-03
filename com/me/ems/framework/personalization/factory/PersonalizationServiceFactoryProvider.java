package com.me.ems.framework.personalization.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class PersonalizationServiceFactoryProvider
{
    static Logger logger;
    private static PersonalizationService personalizationService;
    
    public static PersonalizationService getPersonalizationService() {
        try {
            if (PersonalizationServiceFactoryProvider.personalizationService == null) {
                if (SyMUtil.isProbeServer()) {
                    PersonalizationServiceFactoryProvider.personalizationService = (PersonalizationService)Class.forName("com.me.ems.framework.personalization.summaryserver.probe.api.v1.service.PSPersonalizationServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    PersonalizationServiceFactoryProvider.personalizationService = (PersonalizationService)Class.forName("com.me.ems.framework.personalization.summaryserver.summary.api.v1.service.SSPersonalizationServiceImpl").newInstance();
                }
                else {
                    PersonalizationServiceFactoryProvider.personalizationService = (PersonalizationService)Class.forName("com.me.ems.framework.personalization.api.v1.service.PersonalizationServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            PersonalizationServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting PersonalizationServiceObject", e);
        }
        return PersonalizationServiceFactoryProvider.personalizationService;
    }
    
    static {
        PersonalizationServiceFactoryProvider.logger = Logger.getLogger(PersonalizationServiceFactoryProvider.class.getName());
        PersonalizationServiceFactoryProvider.personalizationService = null;
    }
}
