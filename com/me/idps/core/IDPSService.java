package com.me.idps.core;

import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.adventnet.mfw.service.Service;

public class IDPSService implements Service
{
    public void create(final DataObject dataObject) {
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
        IDPSlogger.AUDIT.log(Level.INFO, "Creating IDPS Service...");
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void start() {
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
        IDPSlogger.AUDIT.log(Level.INFO, "Started IDPS Service...");
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void stop() {
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
        IDPSlogger.AUDIT.log(Level.INFO, "Stopping IDPS Service...");
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
    }
    
    public void destroy() {
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
        IDPSlogger.AUDIT.log(Level.INFO, "Destroyed IDPS Service...");
        IDPSlogger.AUDIT.log(Level.INFO, "_____________________________________________________________________________________________");
    }
}
