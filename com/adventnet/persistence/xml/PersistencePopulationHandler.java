package com.adventnet.persistence.xml;

import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.DataAccessException;
import java.net.URL;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.persistence.Persistence;

public class PersistencePopulationHandler implements ConfigurationPopulationHandler
{
    private static Persistence persistence;
    private static String CLASS_NAME;
    private static final Logger LOGGER;
    
    @Override
    public void populate(final ConfUrlInfo info, final DataObject confFile) throws ConfigurationPopulationException, DataAccessException {
        PersistencePopulationHandler.LOGGER.entering(PersistencePopulationHandler.CLASS_NAME, "populate");
        try {
            final String urlString = (String)confFile.getFirstValue("ConfFile", "URL");
            final URL url = info.getResource(urlString);
            if (url == null) {
                throw new ConfigurationPopulationException(urlString + " is not packaged with the module " + info.getModuleName());
            }
            PersistencePopulationHandler.LOGGER.log(Level.INFO, "conf file url:{0}", url);
            final DataObject data = Xml2DoConverter.transform(url);
            final Object fileID = confFile.getFirstValue("ConfFile", "FILEID");
            data.set("UVHValues", 2, fileID);
            PersistencePopulationHandler.LOGGER.log(Level.FINE, "DataObject generated is {0} ", new Object[] { data });
            final String populatorClass = (String)confFile.getFirstValue("ConfFile", "POPULATORCLASS");
            PersistencePopulationHandler.LOGGER.log(Level.INFO, "Populator class:{0}", populatorClass);
            ConfigurationPopulator populator = null;
            if (populatorClass != null) {
                populator = this.getClassByName(populatorClass).newInstance();
                populator.populate(data);
            }
            else {
                PersistencePopulationHandler.persistence.add(data);
            }
        }
        catch (final ConfigurationPopulationException exc) {
            PersistencePopulationHandler.LOGGER.log(Level.SEVERE, "", exc);
            throw exc;
        }
        catch (final Exception exc2) {
            PersistencePopulationHandler.LOGGER.log(Level.SEVERE, "", exc2);
            throw new ConfigurationPopulationException(exc2);
        }
        finally {
            PersistencePopulationHandler.LOGGER.exiting(PersistencePopulationHandler.CLASS_NAME, "populate");
        }
    }
    
    private Class getClassByName(final String className) throws Exception {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }
    
    static {
        PersistencePopulationHandler.CLASS_NAME = PersistencePopulationHandler.class.getName();
        LOGGER = Logger.getLogger(PersistencePopulationHandler.CLASS_NAME);
        try {
            PersistencePopulationHandler.persistence = (Persistence)BeanUtil.lookup("Persistence");
        }
        catch (final Exception exc) {
            throw new RuntimeException(exc);
        }
    }
}
