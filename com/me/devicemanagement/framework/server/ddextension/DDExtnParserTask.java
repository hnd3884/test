package com.me.devicemanagement.framework.server.ddextension;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.Map;
import java.io.FileFilter;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.xml.sax.XMLReader;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.FileNotFoundException;
import org.xml.sax.ContentHandler;
import com.adventnet.iam.security.SecurityUtil;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DDExtnParserTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    private HashMap<String, HashMap> dataDictionaryDefns;
    public static final Integer DATA_DICTIONARY_EXTN_LOCK;
    public static String cacheName;
    
    public DDExtnParserTask() {
        this.dataDictionaryDefns = null;
        this.dataDictionaryDefns = new HashMap<String, HashMap>();
    }
    
    @Override
    public void executeTask(final Properties taskProps) {
        this.initiateDDExtnLoading();
    }
    
    public void initiateDDExtnLoading() {
        try {
            DDExtnParserTask.logger.log(Level.INFO, "Starting loading of data-dictionary-extn.xml file to cache");
            this.parseXMLAndLoadToCache();
            DDExtnParserTask.logger.log(Level.INFO, "Completed loading of data-dictionary-extn.xml file to cache");
        }
        catch (final Exception ex) {
            DDExtnParserTask.logger.log(Level.SEVERE, "Caught exception while loading data-dictionary-extn.xml. ", ex);
        }
    }
    
    private HashMap<String, HashMap> parseModuleXML(final String fileName) {
        final File ddExtnFile = new File(fileName);
        DDExtnParserTask.logger.log(Level.INFO, "file name is " + ddExtnFile);
        if (ddExtnFile.exists()) {
            try {
                final InputSource inputStream = new InputSource(new FileInputStream(ddExtnFile));
                final XMLReader reader = SecurityUtil.getSAXXMLReader();
                final DDExtnSaxHandler ddExtnSaxHandler = new DDExtnSaxHandler();
                reader.setContentHandler(ddExtnSaxHandler);
                reader.parse(inputStream);
                final HashMap tables = ddExtnSaxHandler.getTables();
                DDExtnParserTask.logger.log(Level.FINE, "tables " + tables);
                return tables;
            }
            catch (final FileNotFoundException e) {
                DDExtnParserTask.logger.log(Level.SEVERE, "FileNotFoundException while parsing DataDictionaryExtn XML", e);
            }
            catch (final SAXException e2) {
                DDExtnParserTask.logger.log(Level.SEVERE, "SAXException while parsing DataDictionaryExtn XML", e2);
            }
            catch (final IOException e3) {
                DDExtnParserTask.logger.log(Level.SEVERE, "IOException while parsing DataDictionaryExtn XML", e3);
            }
            catch (final Exception e4) {
                DDExtnParserTask.logger.log(Level.SEVERE, "Exception while parsing DataDictionaryExtn XML", e4);
            }
        }
        return new HashMap<String, HashMap>();
    }
    
    private String getFileName(final String moduleName) {
        return ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + moduleName;
    }
    
    public void parseXMLAndLoadToCache() {
        try {
            synchronized (DDExtnParserTask.DATA_DICTIONARY_EXTN_LOCK) {
                final DataObject moduleObject = DataAccess.get("Module", (Criteria)null);
                if (null != moduleObject && !moduleObject.isEmpty()) {
                    final Iterator<Row> moduleObjectIte = moduleObject.getRows("Module");
                    while (moduleObjectIte.hasNext()) {
                        final Row moduleRow = moduleObjectIte.next();
                        final String moduleName = String.valueOf(moduleRow.get("MODULENAME"));
                        final File dir = new File(this.getFileName(moduleName));
                        final File[] matchingFiles = dir.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(final File pathname) {
                                return pathname.getName().matches("data-dictionary.*extn.xml");
                            }
                        });
                        if (matchingFiles != null) {
                            for (int j = 0; j < matchingFiles.length; ++j) {
                                this.dataDictionaryDefns.putAll(this.parseModuleXML(matchingFiles[j].toString()));
                            }
                        }
                    }
                }
                if (this.dataDictionaryDefns != null && !this.dataDictionaryDefns.isEmpty()) {
                    if (ApiFactoryProvider.getCacheAccessAPI().getCache(DDExtnParserTask.cacheName, 1) != null) {
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DDExtnParserTask.cacheName, 1);
                    }
                    ApiFactoryProvider.getCacheAccessAPI().putCache(DDExtnParserTask.cacheName, this.dataDictionaryDefns, 1);
                }
            }
        }
        catch (final DataAccessException e) {
            DDExtnParserTask.logger.log(Level.SEVERE, "Exception while loading DataDictionaryExtn XML to cache", (Throwable)e);
        }
    }
    
    static {
        DDExtnParserTask.logger = Logger.getLogger(DDExtnParserTask.class.getName());
        DATA_DICTIONARY_EXTN_LOCK = new Integer(1);
        DDExtnParserTask.cacheName = "dataDictionaryExtnCache";
    }
}
