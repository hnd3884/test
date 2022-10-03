package com.adventnet.sym.server.crawler;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.net.URL;
import com.adventnet.persistence.xml.ConfigurationPopulationException;
import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.xml.ConfUrlInfo;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.ConfigurationPopulationHandler;

public class CRSDataPopulationHandler implements ConfigurationPopulationHandler
{
    private static String className;
    private static Logger out;
    
    public void populate(final ConfUrlInfo info, final DataObject confFile) throws DataAccessException, ConfigurationPopulationException {
        URL url = null;
        try {
            final String urlString = (String)confFile.getFirstValue("ConfFile", "URL");
            url = info.getResource(urlString);
            if (url == null) {
                return;
            }
            final String ModuleName = info.getModuleName();
            CRSDataPopulationHandler.out.log(Level.INFO, "Going to populate files in : {0}", url);
            final String sysBaseDir = System.getProperty("server.home");
            DataObject data = null;
            data = Xml2DoConverter.transform(url);
            final Iterator iter = data.getRows("DefinitionMetaData");
            while (iter.hasNext()) {
                final Row defRow = iter.next();
                final String defPath = (String)defRow.get("DEFINITION_FILE_PATH");
                final String defFile = (String)defRow.get("DEFINITION_FILE_NAME");
                final String relativePath = this.getNativePath(defPath + "/" + defFile);
                final String fileLocation = sysBaseDir + File.separator + "conf" + File.separator + ModuleName + File.separator + relativePath;
                CRSDataPopulationHandler.out.log(Level.INFO, "Processing definition : {0}", fileLocation);
                final DataObject defData = Xml2DoConverter.transform(fileLocation);
                defData.addRow(defRow);
                this.addToDB(defData);
            }
            this.resetCRSMetaData(new Long(3L));
        }
        catch (final Exception e) {
            CRSDataPopulationHandler.out.log(Level.WARNING, "Exception in populating data : ", e);
            throw new ConfigurationPopulationException("Exception in populating crs data...");
        }
    }
    
    private String getNativePath(final String urlPath) {
        final String nativePath = urlPath.replace('/', File.separatorChar);
        CRSDataPopulationHandler.out.log(Level.FINE, "File path : {0}", nativePath);
        return nativePath;
    }
    
    protected DataObject addToDB(final DataObject data) throws Exception {
        return DataAccess.add(data);
    }
    
    protected void resetCRSMetaData(final Long metadataID) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("LocalCRSModuleData", "CRS_MODULE_ID"), (Object)metadataID, 0);
        final DataObject dobj = DataAccess.get("LocalCRSModuleData", criteria);
        if (!dobj.isEmpty()) {
            final Row metadatRow = dobj.getFirstRow("LocalCRSModuleData");
            metadatRow.set("CRS_MODULE_VERSION", (Object)new Long(0L));
            dobj.updateRow(metadatRow);
            DataAccess.update(dobj);
        }
    }
    
    static {
        CRSDataPopulationHandler.className = CRSDataPopulationHandler.class.getName();
        CRSDataPopulationHandler.out = Logger.getLogger(CRSDataPopulationHandler.className);
    }
}
