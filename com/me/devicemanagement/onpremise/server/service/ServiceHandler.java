package com.me.devicemanagement.onpremise.server.service;

import java.util.logging.Level;
import com.adventnet.mfw.service.ConnectorInfo;
import java.util.logging.Logger;
import com.adventnet.mfw.service.PortInUseHandler;

public class ServiceHandler implements PortInUseHandler
{
    private static final Logger LOGGER;
    
    public boolean canIgnore(final ConnectorInfo connectorInfo) {
        ServiceHandler.LOGGER.log(Level.WARNING, "Going to execute canIgnore method in DCHandler for ignoring 8022 port usage");
        if (connectorInfo.getPort() == 8022 || connectorInfo.getProtocol().equals("org.apache.coyote.http11.Http11NioProtocol")) {
            ServiceHandler.LOGGER.log(Level.WARNING, "Ignore to stop the server since port in use.");
            System.setProperty("isIgnoreHttpNioPort", "true");
            return true;
        }
        ServiceHandler.LOGGER.log(Level.WARNING, "Don't ignore to stop the server.");
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(ServiceHandler.class.getName());
    }
}
