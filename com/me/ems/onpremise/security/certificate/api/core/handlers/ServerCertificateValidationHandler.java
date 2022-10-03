package com.me.ems.onpremise.security.certificate.api.core.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.ems.onpremise.security.certificate.api.core.listeners.ServerCertificateValidator;
import java.util.List;
import java.util.logging.Logger;

public class ServerCertificateValidationHandler
{
    Logger logger;
    private static List<ServerCertificateValidator> serverCertValidationHandlerList;
    private static ServerCertificateValidationHandler serverCertValidationHandler;
    
    public ServerCertificateValidationHandler() {
        this.logger = Logger.getLogger("ImportCertificateLogger");
    }
    
    public static ServerCertificateValidationHandler getInstance() {
        if (ServerCertificateValidationHandler.serverCertValidationHandler == null) {
            ServerCertificateValidationHandler.serverCertValidationHandler = new ServerCertificateValidationHandler();
        }
        return ServerCertificateValidationHandler.serverCertValidationHandler;
    }
    
    public void addServerCertificateValidationHandler(final ServerCertificateValidator validationHandler) {
        this.logger.log(Level.INFO, "addServerCertificateValidationHandler() called : {0}", validationHandler.getClass().getName());
        ServerCertificateValidationHandler.serverCertValidationHandlerList.add(validationHandler);
    }
    
    public void removeServerCertificateValidationHandler(final ServerCertificateValidator validationHandler) {
        this.logger.log(Level.INFO, "removeServerCertificateValidationHandler() called : {0}", validationHandler.getClass().getName());
        ServerCertificateValidationHandler.serverCertValidationHandlerList.remove(validationHandler);
    }
    
    public boolean canRegenerateCertificateValidator() {
        boolean regen = true;
        this.logger.log(Level.INFO, "canRegenerateCertificateValidator() called : {0}", ServerCertificateValidationHandler.serverCertValidationHandlerList.toString());
        for (final ServerCertificateValidator listener : ServerCertificateValidationHandler.serverCertValidationHandlerList) {
            regen = (listener.canRegenerateServerCertificate() && regen);
        }
        return regen;
    }
    
    static {
        ServerCertificateValidationHandler.serverCertValidationHandlerList = new ArrayList<ServerCertificateValidator>();
        ServerCertificateValidationHandler.serverCertValidationHandler = null;
    }
}
