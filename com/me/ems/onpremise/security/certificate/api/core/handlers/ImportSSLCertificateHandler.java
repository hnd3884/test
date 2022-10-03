package com.me.ems.onpremise.security.certificate.api.core.handlers;

import java.util.ArrayList;
import org.json.JSONObject;
import com.me.ems.onpremise.security.certificate.api.core.events.ImportSSLCertificateChangeEvent;
import java.util.logging.Level;
import com.me.ems.onpremise.security.certificate.api.core.listeners.ImportSSLCertificateListener;
import java.util.List;
import java.util.logging.Logger;

public class ImportSSLCertificateHandler
{
    Logger logger;
    private static List<ImportSSLCertificateListener> importSSLCertificateListenerList;
    private static ImportSSLCertificateHandler importSSLCertificateHandler;
    
    public ImportSSLCertificateHandler() {
        this.logger = Logger.getLogger("ImportCertificateLogger");
    }
    
    public static ImportSSLCertificateHandler getInstance() {
        if (ImportSSLCertificateHandler.importSSLCertificateHandler == null) {
            ImportSSLCertificateHandler.importSSLCertificateHandler = new ImportSSLCertificateHandler();
        }
        return ImportSSLCertificateHandler.importSSLCertificateHandler;
    }
    
    public void addImportSSLCertificateListener(final ImportSSLCertificateListener importSSLCertificateListener) {
        this.logger.log(Level.INFO, "addImportSSLCertificateListener() called : {0}", importSSLCertificateListener.getClass().getName());
        ImportSSLCertificateHandler.importSSLCertificateListenerList.add(importSSLCertificateListener);
    }
    
    public void removeImportSSLCertificateListener(final ImportSSLCertificateListener importSSLCertificateListener) {
        this.logger.log(Level.INFO, "removeImportSSLCertificateListener() called : {0}", importSSLCertificateListener.getClass().getName());
        ImportSSLCertificateHandler.importSSLCertificateListenerList.remove(importSSLCertificateListener);
    }
    
    public void invokeImportSSLCertificateChangeListeners() {
        final int l = ImportSSLCertificateHandler.importSSLCertificateListenerList.size();
        this.logger.log(Level.INFO, "invokeImportSSLCertificateChangeListeners() called : {0}", ImportSSLCertificateHandler.importSSLCertificateListenerList.toString());
        for (int s = 0; s < l; ++s) {
            final ImportSSLCertificateListener listener = ImportSSLCertificateHandler.importSSLCertificateListenerList.get(s);
            listener.certificateChanged();
        }
    }
    
    public JSONObject canUploadCertificateListener(final ImportSSLCertificateChangeEvent event) {
        JSONObject remarks = null;
        final int l = ImportSSLCertificateHandler.importSSLCertificateListenerList.size();
        this.logger.log(Level.INFO, "preCertificateUploadListener() called : {0}", ImportSSLCertificateHandler.importSSLCertificateListenerList.toString());
        for (int s = 0; s < l; ++s) {
            final ImportSSLCertificateListener listener = ImportSSLCertificateHandler.importSSLCertificateListenerList.get(s);
            remarks = listener.canUploadCertificate(event);
            if (remarks != null && !remarks.optBoolean("status")) {
                return remarks;
            }
        }
        return remarks;
    }
    
    static {
        ImportSSLCertificateHandler.importSSLCertificateListenerList = new ArrayList<ImportSSLCertificateListener>();
        ImportSSLCertificateHandler.importSSLCertificateHandler = null;
    }
}
