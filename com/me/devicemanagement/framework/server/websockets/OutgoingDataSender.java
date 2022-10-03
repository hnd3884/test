package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class OutgoingDataSender implements SchedulerExecutionInterface
{
    private static Logger wsFrameworkLogger;
    
    public void executeTask(final Properties props) {
        try {
            final Constants.ClientSocketType socketType = ((Hashtable<K, Constants.ClientSocketType>)props).get("socketType");
            final Long clientId = ((Hashtable<K, Long>)props).get("clientId");
            final ClientManager clientMgr = WSClientMapper.getInstance().getClientManager(clientId);
            if (socketType == Constants.ClientSocketType.WEBSOCKET) {
                final WSSocketImpl wsSocket = (WSSocketImpl)clientMgr.getClientSocket();
                wsSocket.sendDataFromQueue();
            }
            else {
                final TCPSocketImpl tcpSocket = (TCPSocketImpl)clientMgr.getClientSocket();
                tcpSocket.sendDataFromQueue();
            }
        }
        catch (final Exception ex) {
            OutgoingDataSender.wsFrameworkLogger.log(Level.SEVERE, "Exception while executing outgoing data sender task", ex);
        }
    }
    
    static {
        OutgoingDataSender.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
