package com.me.mdm.onpremise.server.service;

import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class MDMPService implements Service
{
    private Logger logger;
    
    public MDMPService() {
        this.logger = Logger.getLogger("DCServiceLogger");
    }
    
    public void create(final DataObject dobj) throws Exception {
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        this.logger.log(Level.INFO, "Creating MDMP Service...");
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void destroy() {
    }
    
    public void start() throws Exception {
        this.logger.log(Level.INFO, "*****************Starting MDMP Service*************");
        MDMPHandler.initialize();
    }
    
    public void stop() throws Exception {
        MDMPHandler.destroy();
    }
}
