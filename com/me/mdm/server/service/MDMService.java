package com.me.mdm.server.service;

import com.me.devicemanagement.framework.server.service.ServiceHandlerAPI;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class MDMService implements Service
{
    private Logger logger;
    
    public MDMService() {
        this.logger = Logger.getLogger("DCServiceLogger");
    }
    
    public void create(final DataObject d) throws Exception {
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        this.logger.log(Level.INFO, "Creating MDM Service...");
        this.logger.log(Level.INFO, "_____________________________________________________________________________________________");
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            final ServiceHandlerAPI handlerObj = (ServiceHandlerAPI)Class.forName("com.me.mdm.onpremise.server.service.MDMOnPremiseHandler").newInstance();
            handlerObj.initialize();
        }
    }
    
    public void start() throws Exception {
        this.logger.log(Level.INFO, "*****************Starting MDM Service*************");
        MDMHandler.initiate();
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            final ServiceHandlerAPI handlerObj = (ServiceHandlerAPI)Class.forName("com.me.mdm.onpremise.server.service.MDMOnPremiseHandler").newInstance();
            handlerObj.initiate();
        }
    }
    
    public void stop() throws Exception {
        this.logger.log(Level.INFO, "*****************Stopping MDM Service*************");
        MDMHandler.destroy();
    }
    
    public void destroy() throws Exception {
    }
}
