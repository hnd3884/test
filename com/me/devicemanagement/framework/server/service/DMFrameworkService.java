package com.me.devicemanagement.framework.server.service;

import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class DMFrameworkService implements Service
{
    private Logger logger;
    private String sourceClass;
    
    public DMFrameworkService() {
        this.logger = Logger.getLogger("DCServiceLogger");
        this.sourceClass = DMFrameworkService.class.getName();
    }
    
    public void create(final DataObject d) throws Exception {
        final String sourceMethod = "create";
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        this.logger.log(Level.INFO, "Creating DMFramework Service...");
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void start() throws Exception {
        final String sourceMethod = "start";
        this.logger.log(Level.INFO, "*****************Starting DMFramework Service*************");
        DMFrameworkHandler.initiate();
    }
    
    public void stop() throws Exception {
        final String sourceMethod = "stop";
        this.logger.log(Level.INFO, "*****************Stopping DMFramework Service*************");
        DMFrameworkHandler.stop();
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Stopping CSVProcessingResumeTask Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
    }
    
    public void destroy() throws Exception {
        final String sourceMethod = "destroy";
        this.logger.log(Level.INFO, "*****************Destroying DMFramework Service*************");
        DMFrameworkHandler.destroy();
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Destroying CSVProcessingResumeTask Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
    }
}
