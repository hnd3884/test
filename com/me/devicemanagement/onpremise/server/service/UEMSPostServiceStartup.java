package com.me.devicemanagement.onpremise.server.service;

import com.me.devicemanagement.onpremise.server.license.handler.CommonOnpremiseServicehandler;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class UEMSPostServiceStartup implements Service
{
    private Logger logger;
    
    public UEMSPostServiceStartup() {
        this.logger = Logger.getLogger("DCServiceLogger");
    }
    
    public void create(final DataObject d) throws Exception {
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        this.logger.log(Level.INFO, "Creating LicenseHandlerService Service...");
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void start() throws Exception {
        this.logger.log(Level.INFO, "*****************Starting LicenseHandlerService Service*************");
        CommonOnpremiseServicehandler.getInstance().startUpHandling();
    }
    
    public void stop() throws Exception {
        this.logger.log(Level.INFO, "*****************Stopping LicenseHandlerService *************");
    }
    
    public void destroy() throws Exception {
        this.logger.log(Level.INFO, "*****************Destroying LicenseHandlerService *************");
    }
}
