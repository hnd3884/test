package com.adventnet.mfw;

import com.zoho.conf.Configuration;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.xml.ConfigurationPopulationHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.adventnet.persistence.xml.ConfUrlInfo;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class ConfPopulator
{
    private static final Logger OUT;
    public static final String DEFAULTHANDLER;
    private static String server_home;
    
    public static void populate(final String confPath, final String moduleName) throws Exception {
        populate(confPath, "conf-files.xml", moduleName);
    }
    
    public static void populate(final String confPath, final String fileName, final String moduleName) throws Exception {
        final File confFile = new File(confPath + "/" + fileName);
        ConfPopulator.OUT.log(Level.FINEST, "ConfFile is {0}", confFile);
        if (!confFile.exists()) {
            return;
        }
        final Long moduleId = PersistenceInitializer.getModuleId(moduleName);
        final ConfUrlInfo info = new ConfUrlInfo(moduleName, moduleId, new File(ConfPopulator.server_home).toURL());
        final DataObject object = Xml2DoConverter.transform(confFile.toURL());
        final Iterator rowIterator = object.getRows("ConfFile");
        while (rowIterator.hasNext()) {
            final Row row = rowIterator.next();
            ConfPopulator.OUT.log(Level.FINEST, "ConfFile Row is {0}", row);
            String handlerClass = (String)row.get("HANDLERCLASS");
            if (handlerClass == null) {
                handlerClass = ConfPopulator.DEFAULTHANDLER;
            }
            final ConfigurationPopulationHandler handler = (ConfigurationPopulationHandler)Class.forName(handlerClass).newInstance();
            handler.populate(info, object.getDataObject(object.getTableNames(), row));
        }
    }
    
    public static Long getModuleId(final String moduleName) throws Exception {
        ConfPopulator.OUT.log(Level.WARNING, "Please donot use this method [ConfPopulator.getModuleId(moduleName)] instead use [PersistenceInitializer.getModuleId(moduleName)]");
        return PersistenceInitializer.getModuleId(moduleName);
    }
    
    static {
        OUT = Logger.getLogger(ConfPopulator.class.getName());
        DEFAULTHANDLER = Configuration.getString("DEFAULT_HANDLER", "com.adventnet.persistence.xml.DataAccessPopulationHandler");
        ConfPopulator.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
