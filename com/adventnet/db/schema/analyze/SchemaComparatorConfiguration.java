package com.adventnet.db.schema.analyze;

import java.util.ArrayList;
import com.zoho.conf.Configuration;
import java.io.File;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.conf.tree.ConfTreeBuilder;
import java.util.Properties;
import com.adventnet.db.schema.analyze.notifier.SchemaAnalyzerProgressNotifier;
import java.util.List;
import com.zoho.conf.tree.ConfTree;
import java.util.logging.Logger;

public class SchemaComparatorConfiguration
{
    static String confFilePath;
    private static final Logger LOGGER;
    static ConfTree confTree;
    private static List<SchemaComparatorPrePostHandler> prePostHandlers;
    private static SchemaComparatorHandler compHandler;
    private static boolean checkAbandonedRows;
    private static SchemaAnalyzerProgressNotifier notifier;
    private static int workerPoolSize;
    private static boolean isMetaDataToBeLoadedFromXML;
    
    public SchemaComparatorConfiguration(final Properties props) throws Exception {
        SchemaComparatorConfiguration.confTree = ((ConfTreeBuilder)ConfTreeBuilder.confTree().withConfigurations(props)).build();
        SchemaComparatorConfiguration.LOGGER.info("configurations:: " + SchemaComparatorConfiguration.confTree);
        this.initializeVariables();
    }
    
    private void initializeVariables() throws Exception {
        final String prePostHandlersClasses = this.getConfigurationValue("schema.comparator.prepost.handlers");
        if (prePostHandlersClasses != null) {
            for (final String handlerClass : prePostHandlersClasses.split(",")) {
                SchemaComparatorConfiguration.prePostHandlers.add((SchemaComparatorPrePostHandler)Class.forName(handlerClass).newInstance());
            }
        }
        final String dbName = PersistenceInitializer.getConfigurationValue("dbName");
        String compHandlerClass = this.getConfigurationValue(dbName + ".schema.comparator.handler");
        if (compHandlerClass != null) {
            SchemaComparatorConfiguration.compHandler = (SchemaComparatorHandler)Class.forName(compHandlerClass).newInstance();
        }
        else {
            compHandlerClass = this.getConfigurationValue("schema.comparator.handler");
            if (compHandlerClass != null) {
                SchemaComparatorConfiguration.compHandler = (SchemaComparatorHandler)Class.forName(compHandlerClass).newInstance();
            }
        }
        if (SchemaComparatorConfiguration.compHandler == null && PersistenceInitializer.onSAS()) {
            SchemaComparatorConfiguration.compHandler = (SchemaComparatorHandler)Class.forName("com.adventnet.db.schema.analyze.ZohoSchemaComparatorHandler").newInstance();
        }
        final String confStr = this.getConfigurationValue("check.abandoned.rows");
        if (confStr != null) {
            SchemaComparatorConfiguration.checkAbandonedRows = Boolean.valueOf(confStr);
        }
        SchemaComparatorConfiguration.notifier = this.getNewProgressNotifier();
        final String size = this.getConfigurationValue("worker.pool.size");
        if (size != null) {
            SchemaComparatorConfiguration.workerPoolSize = Integer.parseInt(size);
        }
        final String configVal = this.getConfigurationValue("load.metadata.from.ddxml");
        if (configVal == null) {
            SchemaComparatorConfiguration.isMetaDataToBeLoadedFromXML = true;
        }
        else {
            SchemaComparatorConfiguration.isMetaDataToBeLoadedFromXML = Boolean.valueOf(configVal);
        }
    }
    
    public SchemaComparatorConfiguration() throws Exception {
        final File schemaComparatorConf = new File(System.getProperty("schema.comparator.config.file.path", SchemaComparatorConfiguration.confFilePath));
        if (!schemaComparatorConf.exists()) {
            SchemaComparatorConfiguration.LOGGER.info("schema_analyzer.conf file not found...");
        }
        else {
            SchemaComparatorConfiguration.confTree = ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(SchemaComparatorConfiguration.confFilePath)).build();
            SchemaComparatorConfiguration.LOGGER.info("schema_analyzer.conf file is initialized");
            SchemaComparatorConfiguration.LOGGER.info("configurations:: " + SchemaComparatorConfiguration.confTree);
        }
        this.initializeVariables();
    }
    
    public SchemaAnalyzerProgressNotifier getNewProgressNotifier() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String notifierClassName = this.getConfigurationValue("schema.analyzer.notifier");
        return (SchemaAnalyzerProgressNotifier)Class.forName((notifierClassName != null) ? notifierClassName.trim() : "com.adventnet.db.schema.analyze.notifier.ConsoleProgressBar").newInstance();
    }
    
    public SchemaAnalyzerProgressNotifier getProgressNotifier() {
        return SchemaComparatorConfiguration.notifier;
    }
    
    public void setProgressNotifier(final SchemaAnalyzerProgressNotifier notifierObj) {
        SchemaComparatorConfiguration.notifier = notifierObj;
    }
    
    public List<SchemaComparatorPrePostHandler> getPrePostHandlers() {
        return SchemaComparatorConfiguration.prePostHandlers;
    }
    
    public void addToSchemaComparatorPrePostHandler(final SchemaComparatorPrePostHandler handler) {
        SchemaComparatorConfiguration.prePostHandlers.add(handler);
    }
    
    public void setSchemaComparatorPrePostHandler(final List<SchemaComparatorPrePostHandler> handlers) {
        SchemaComparatorConfiguration.prePostHandlers = handlers;
    }
    
    public SchemaComparatorHandler getSchemaComparatorHandler() {
        return SchemaComparatorConfiguration.compHandler;
    }
    
    public void setSchemaComparatorHandler(final SchemaComparatorHandler handler) {
        SchemaComparatorConfiguration.compHandler = handler;
    }
    
    public String getConfigurationValue(final String config) {
        return (SchemaComparatorConfiguration.confTree == null) ? null : SchemaComparatorConfiguration.confTree.get(config);
    }
    
    public boolean getConfigurationValueAsBoolean(final String config) {
        final String confStr = (SchemaComparatorConfiguration.confTree == null) ? null : SchemaComparatorConfiguration.confTree.get(config);
        return (confStr == null) ? Boolean.FALSE : Boolean.valueOf(confStr);
    }
    
    public boolean isAbandonedRowsCheckEnabled() {
        return SchemaComparatorConfiguration.checkAbandonedRows;
    }
    
    public void enableAbandonedRowsCheck() {
        SchemaComparatorConfiguration.checkAbandonedRows = true;
    }
    
    public void disableAbandonedRowsCheck() {
        SchemaComparatorConfiguration.checkAbandonedRows = false;
    }
    
    public int getWorkerPoolSize() {
        return SchemaComparatorConfiguration.workerPoolSize;
    }
    
    public void setWorkerPoolSize(final int size) {
        SchemaComparatorConfiguration.workerPoolSize = size;
    }
    
    public boolean isMetaDataToBeLoadedFromDDXml() {
        return SchemaComparatorConfiguration.isMetaDataToBeLoadedFromXML;
    }
    
    public void disableMetaDataLoadingFromXML() {
        SchemaComparatorConfiguration.isMetaDataToBeLoadedFromXML = false;
    }
    
    public void enableMetaDataLoadingFromXML() {
        SchemaComparatorConfiguration.isMetaDataToBeLoadedFromXML = true;
    }
    
    static {
        SchemaComparatorConfiguration.confFilePath = Configuration.getString("app.home") + File.separator + "conf" + File.separator + "schema_analyzer.conf";
        LOGGER = Logger.getLogger(SchemaComparatorConfiguration.class.getName());
        SchemaComparatorConfiguration.confTree = null;
        SchemaComparatorConfiguration.prePostHandlers = new ArrayList<SchemaComparatorPrePostHandler>();
        SchemaComparatorConfiguration.compHandler = null;
        SchemaComparatorConfiguration.checkAbandonedRows = false;
        SchemaComparatorConfiguration.notifier = null;
        SchemaComparatorConfiguration.workerPoolSize = 10;
        SchemaComparatorConfiguration.isMetaDataToBeLoadedFromXML = true;
    }
}
