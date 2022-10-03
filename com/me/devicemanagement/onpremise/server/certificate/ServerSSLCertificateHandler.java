package com.me.devicemanagement.onpremise.server.certificate;

import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ServerSSLCertificateHandler
{
    Logger logger;
    private static List<ServerSSLCertificateListener> serverSSLCertificateListenerList;
    private static ServerSSLCertificateHandler serverSSLCertificateHandler;
    
    public ServerSSLCertificateHandler() {
        this.logger = Logger.getLogger("ImportCertificateLogger");
    }
    
    public static ServerSSLCertificateHandler getInstance() {
        if (ServerSSLCertificateHandler.serverSSLCertificateHandler == null) {
            ServerSSLCertificateHandler.serverSSLCertificateHandler = new ServerSSLCertificateHandler();
        }
        return ServerSSLCertificateHandler.serverSSLCertificateHandler;
    }
    
    public void addServerSSLCertificateListener(final ServerSSLCertificateListener serverSSLCertificateListener) {
        this.logger.log(Level.INFO, "addServerSSLCertificateListener() called : {0}", serverSSLCertificateListener.getClass().getName());
        ServerSSLCertificateHandler.serverSSLCertificateListenerList.add(serverSSLCertificateListener);
    }
    
    public void removeServerSSLCertificateListener(final ServerSSLCertificateListener serverSSLCertificateListener) {
        this.logger.log(Level.INFO, "removeServerSSLCertificateListener() called : {0}", serverSSLCertificateListener.getClass().getName());
        ServerSSLCertificateHandler.serverSSLCertificateListenerList.remove(serverSSLCertificateListener);
    }
    
    public void invokeServerSSLCertificateChangeListeners() {
        final int l = ServerSSLCertificateHandler.serverSSLCertificateListenerList.size();
        this.logger.log(Level.INFO, "invokeServerSSLCertificateChangeListeners() called : {0}", ServerSSLCertificateHandler.serverSSLCertificateListenerList.toString());
        for (int s = 0; s < l; ++s) {
            final ServerSSLCertificateListener listener = ServerSSLCertificateHandler.serverSSLCertificateListenerList.get(s);
            listener.certificateChanged();
        }
    }
    
    public JSONObject canUploadCertificateListener(final ServerSSLCertificateChangeEvent event) {
        JSONObject remarks = null;
        final int l = ServerSSLCertificateHandler.serverSSLCertificateListenerList.size();
        this.logger.log(Level.INFO, "preCertificateUploadListener() called : {0}", ServerSSLCertificateHandler.serverSSLCertificateListenerList.toString());
        for (int s = 0; s < l; ++s) {
            final ServerSSLCertificateListener listener = ServerSSLCertificateHandler.serverSSLCertificateListenerList.get(s);
            remarks = listener.canUploadCertificate(event);
            if (remarks != null && !remarks.optBoolean("status")) {
                return remarks;
            }
        }
        return remarks;
    }
    
    static {
        ServerSSLCertificateHandler.serverSSLCertificateListenerList = new ArrayList<ServerSSLCertificateListener>();
        ServerSSLCertificateHandler.serverSSLCertificateHandler = null;
    }
}
