package com.me.devicemanagement.framework.server.service;

import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.message.Messenger;
import com.me.devicemanagement.framework.server.message.MickeyMessageFilter;
import com.me.devicemanagement.framework.server.message.MickeyMessageListener;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class MickeyNotificationService implements Service
{
    Logger logger;
    
    public MickeyNotificationService() {
        this.logger = Logger.getLogger("ProbeSyncLogger");
    }
    
    public void create(final DataObject serviceDO) {
    }
    
    public void start() throws Exception {
        this.logger.log(Level.INFO, "\n <------ Entering start() in MickeyNotificationService.java ------> \n");
        final MickeyMessageListener mml = new MickeyMessageListener();
        final MickeyMessageFilter mmf = new MickeyMessageFilter();
        Messenger.subscribe("PersistenceTopic", (MessageListener)mml, true, (MessageFilter)mmf);
        this.logger.log(Level.INFO, "\n <------ Exiting start() in MickeyNotificationService.java ------> \n");
    }
    
    public void stop() {
    }
    
    public void destroy() {
    }
}
