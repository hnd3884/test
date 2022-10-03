package com.adventnet.sym.server.service;

import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class DesktopCentralService implements Service
{
    private Logger logger;
    
    public DesktopCentralService() {
        this.logger = Logger.getLogger("DCServiceLogger");
    }
    
    public void create(final DataObject dobj) {
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        this.logger.log(Level.INFO, "Creating DesktopCentral Service...");
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void destroy() {
    }
    
    public void start() throws Exception {
        this.logger.log(Level.INFO, "Starting DesktopCentral Service...");
        DCHandler.initialize();
    }
    
    public void stop() throws Exception {
        DCHandler.destroy();
    }
}
