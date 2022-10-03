package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ClientRequestProcessor implements SchedulerExecutionInterface
{
    private static Logger wsFrameworkLogger;
    private ClientManager clientMgr;
    
    public void executeTask(final Properties props) {
        try {
            final Long clientId = ((Hashtable<K, Long>)props).get("clientId");
            this.clientMgr = WSClientMapper.getInstance().getClientManager(clientId);
            this.processClientRequest();
        }
        catch (final Exception ex) {
            ClientRequestProcessor.wsFrameworkLogger.log(Level.INFO, "Exception occured while executing client request processor task", ex);
        }
    }
    
    private void processClientRequest() {
        if (this.isValidClient()) {
            try {
                ClientRequestProcessor.wsFrameworkLogger.log(Level.INFO, "Calling the ClientManager's onSocketReady()");
                this.clientMgr.handleSocketReady();
                ClientRequestProcessor.wsFrameworkLogger.log(Level.INFO, "ClientManager's onSocketReady() returned");
            }
            catch (final Exception ex) {
                ClientRequestProcessor.wsFrameworkLogger.log(Level.SEVERE, "Exception while calling ClientManager's onSocketReady()", ex);
            }
        }
    }
    
    private boolean isValidClient() {
        final String sourceMethod = "ClientRequestProcessor::isValidClient";
        boolean validationResult = false;
        try {
            if (this.clientMgr.getClientId() >= 0L) {
                validationResult = true;
            }
        }
        catch (final Exception ex) {
            ClientRequestProcessor.wsFrameworkLogger.log(Level.SEVERE, sourceMethod + " -> Exception while validating client", ex);
        }
        ClientRequestProcessor.wsFrameworkLogger.log(Level.FINE, "Performed basic level validations - Validation Result - {0}", validationResult);
        return validationResult;
    }
    
    static {
        ClientRequestProcessor.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
