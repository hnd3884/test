package com.me.devicemanagement.onpremise.server.metrack;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Logger;
import com.me.tools.zcutil.ZCDataHandler;

public abstract class METrackerAPI implements ZCDataHandler
{
    private static final Logger LOGGER;
    static METrackerAPI meTrackerAPI;
    private static final String METRACKER_CLASSNAME = "meTrackerClass";
    
    public static METrackerAPI getMETrackerAPI() {
        try {
            if (METrackerAPI.meTrackerAPI == null) {
                final String meTrackerClassName = ProductClassLoader.getSingleImplProductClass("METRACKER_IMPL_CLASS");
                if (meTrackerClassName != null && meTrackerClassName.trim().length() != 0) {
                    METrackerAPI.meTrackerAPI = (METrackerAPI)Class.forName(meTrackerClassName).newInstance();
                }
            }
        }
        catch (final ClassNotFoundException ce) {
            METrackerAPI.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getMETrackerAPI...", ce);
        }
        catch (final InstantiationException ie) {
            METrackerAPI.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getMETrackerAPI...", ie);
        }
        catch (final IllegalAccessException ie2) {
            METrackerAPI.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getMETrackerAPI...", ie2);
        }
        catch (final Exception ex) {
            METrackerAPI.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getMETrackerAPI...", ex);
        }
        return METrackerAPI.meTrackerAPI;
    }
    
    public void uploadODData(final long jobid) {
    }
    
    static {
        LOGGER = Logger.getLogger(METrackerAPI.class.getName());
        METrackerAPI.meTrackerAPI = null;
    }
}
