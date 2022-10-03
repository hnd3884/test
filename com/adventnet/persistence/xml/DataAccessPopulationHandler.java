package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataAccess;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import com.adventnet.persistence.PersistenceInitializer;
import java.net.URL;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DataAccessPopulationHandler implements ConfigurationPopulationHandler
{
    private static String CLASS_NAME;
    private static final Logger LOGGER;
    
    public static void addConfFileRows(final ConfUrlInfo info, final DataObject data, final Row confFileRow) throws DataAccessException {
        final String fileName = (String)confFileRow.get(2);
        confFileRow.set(2, ConfUrlInfo.getRelativePath(info.getResource(fileName).toString()));
        if (!data.containsTable("ConfFile")) {
            data.addRow(confFileRow);
            DataAccessPopulationHandler.LOGGER.log(Level.FINEST, "Added the ConfFileRow :: {0}", confFileRow);
        }
        else {
            final Row row = data.getRow("ConfFile");
            confFileRow.set(1, row.get(1));
            data.updateRow(confFileRow);
            DataAccessPopulationHandler.LOGGER.log(Level.FINEST, "Updated the ConfFileRow :: {0}", confFileRow);
        }
        final Row row = data.getRow("ConfFile");
        final Row cf_module = new Row("ConfFileToModule");
        cf_module.set(1, row.get(1));
        cf_module.set(2, info.getModuleId());
        data.addRow(cf_module);
    }
    
    public static boolean isToBePopulated(final URL url) {
        final String location = url.toExternalForm();
        return !PersistenceInitializer.onSAS() || (location.indexOf("/bean.xml") <= 0 && location.indexOf("/service.xml") <= 0);
    }
    
    @Override
    public void populate(final ConfUrlInfo info, final DataObject confFile) throws DataAccessException, ConfigurationPopulationException {
        DataAccessPopulationHandler.LOGGER.entering(DataAccessPopulationHandler.CLASS_NAME, "populate");
        URL url = null;
        try {
            final String urlString = (String)confFile.getFirstValue("ConfFile", "URL");
            url = info.getResource(urlString);
            if (url == null) {
                throw new ConfigurationPopulationException(urlString + " is not packaged with the module " + info.getModuleName());
            }
            if (!isToBePopulated(url)) {
                return;
            }
            DataObject data = null;
            try {
                data = Xml2DoConverter.transform(url, true, info.getModuleName());
            }
            catch (final Exception se) {
                throw new ConfigurationPopulationException("Exception occured while parsing the url :: " + url, se);
            }
            addConfFileRows(info, data, confFile.getFirstRow("ConfFile"));
            final String populatorClass = (String)confFile.getFirstValue("ConfFile", "POPULATORCLASS");
            if (populatorClass != null) {
                ConfigurationPopulator populator = null;
                try {
                    populator = this.getClassByName(populatorClass).newInstance();
                }
                catch (final Exception e) {
                    throw new ConfigurationPopulationException("Exception occured while instantiating the populator class :: " + populatorClass + " for the url :: " + url, e);
                }
                populator.populate(data);
            }
            else {
                try {
                    this.addToDB(data, confFile);
                }
                catch (final DataAccessException e2) {
                    if (e2.getCause() == null) {
                        throw e2;
                    }
                    final int code = e2.getErrorCode();
                    if (e2.getCause() instanceof BatchUpdateException) {
                        if (code == 1004) {
                            DataAccessPopulationHandler.LOGGER.log(Level.WARNING, "\n\nNOTE: Check the populator XML file and ensure the following\n1.Values were given for all the columns with the constraint NOT-NULL\n2.ColumnNames were specified correctly in populator XML for the table [" + e2.getTableName() + "] as defined in the data-dictionary\n");
                        }
                        else if (code == 1015) {
                            DataAccessPopulationHandler.LOGGER.log(Level.WARNING, "\n\nNOTE: Check the parent row(s) for the above mentioned row(s) and ensure the following\n1. Parent rows were defined in populator XML for the above row(s)\n2. If the problem is not beacause of 1, please verify the <foreign-keys> of the table [" + e2.getTableName() + "] in your data-dictionaryand ensure that <fk-local-column> and <fk-reference-column> were defined as per your intension..\n");
                        }
                        else {
                            DataAccessPopulationHandler.LOGGER.log(Level.WARNING, "Problem while populating table [{0}] from file [{1}]", new Object[] { e2.getTableName(), url });
                        }
                    }
                    else if (e2.getCause() instanceof SQLException && code == -9999) {
                        DataAccessPopulationHandler.LOGGER.log(Level.WARNING, "\n\nProbable cause may be that UVH patterns were not specified correctly.So check all columns whose value is given in the form of UVH Pattern.If the UVH Pattern is derived from a parent table,check the parent table and ensure that the patterns defined in [" + e2.getTableName() + "] were available in it's parent table\n");
                    }
                    throw e2;
                }
            }
        }
        finally {
            DataAccessPopulationHandler.LOGGER.exiting(DataAccessPopulationHandler.CLASS_NAME, "populate");
        }
    }
    
    protected DataObject addToDB(final DataObject data, final DataObject confFile) throws DataAccessException, ConfigurationPopulationException {
        return DataAccess.add(data);
    }
    
    private Class getClassByName(final String className) throws Exception {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }
    
    static {
        DataAccessPopulationHandler.CLASS_NAME = DataAccessPopulationHandler.class.getName();
        LOGGER = Logger.getLogger(DataAccessPopulationHandler.CLASS_NAME);
    }
}
