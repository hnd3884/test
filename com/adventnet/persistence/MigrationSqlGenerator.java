package com.adventnet.persistence;

import java.util.HashSet;
import com.zoho.conf.Configuration;
import com.adventnet.db.persistence.metadata.parser.DataDictionaryParser;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.conf.AppResources;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.net.URL;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.db.persistence.metadata.MetaDataAccess;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.persistence.xml.DynamicValueHandlerRepositry;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.logging.Level;
import java.io.File;
import java.util.List;
import java.util.Set;
import com.adventnet.db.adapter.SQLGenerator;
import java.util.logging.Logger;

public class MigrationSqlGenerator
{
    private static final Logger LOGGER;
    private static SQLGenerator sqlGenClass;
    static Set<String> ignoreTablesInDOXML;
    
    public static void generateSQLsForDOXML(final StringBuilder sb, final String doXMLFileName, final List<File> dvhFiles) throws Exception {
        Iterator<Row> iterator = null;
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Going to generate INSERT SQLs for the do-xml :: [{0}]", doXMLFileName);
        final DataObject data = Xml2DoConverter.transform(doXMLFileName);
        final DynamicValueHandlerRepositry dvhRep = new DynamicValueHandlerRepositry();
        dvhRep.parse(DataDictionary.class.getResource("conf/dynamic-value-handlers.xml"));
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Loaded persistence dynamic-value-handlers.xml");
        for (final File dvhFile : dvhFiles) {
            dvhRep.parse(dvhFile.toURL());
            MigrationSqlGenerator.LOGGER.log(Level.INFO, "Loaded the dynamic-value-handlers.xml :: " + dvhFile);
        }
        final List<String> tableNames = data.getTableNames();
        final List<String> sortedTableNames = PersistenceUtil.sortTables(tableNames);
        final StringBuilder colBuffer = new StringBuilder();
        final StringBuilder valBuffer = new StringBuilder();
        for (final String tableName : sortedTableNames) {
            if (MigrationSqlGenerator.ignoreTablesInDOXML.contains(tableName)) {
                MigrationSqlGenerator.LOGGER.log(Level.INFO, "Not generating DOSQLs for the table :: [{0}]", tableName);
            }
            else {
                final List<String> columnNames = MetaDataUtil.getTableDefinitionByName(tableName).getColumnNames();
                iterator = data.getRows(tableName);
                while (iterator.hasNext()) {
                    colBuffer.setLength(0);
                    valBuffer.setLength(0);
                    final Row row = iterator.next();
                    final List dirtyColumnIndices = row.getChangedColumnIndices();
                    boolean appendComma = false;
                    for (int i = 0; i < columnNames.size(); ++i) {
                        final int colIdx = i + 1;
                        final Object value = row.get(colIdx);
                        final String columnName = columnNames.get(i);
                        if (dirtyColumnIndices.contains(colIdx) || value instanceof UniqueValueHolder) {
                            if (appendComma) {
                                colBuffer.append(", ");
                                valBuffer.append(", ");
                            }
                            colBuffer.append("`");
                            colBuffer.append(columnName);
                            colBuffer.append("`");
                            if (value instanceof UniqueValueHolder) {
                                final String genName = ((UniqueValueHolder)value).getGeneratorName();
                                valBuffer.append("'UVG::");
                                valBuffer.append(genName);
                                valBuffer.append("::");
                                valBuffer.append(value);
                                valBuffer.append("'");
                            }
                            else if (isDVHColumn(tableName, columnName)) {
                                valBuffer.append("'DVH::");
                                valBuffer.append(columnName);
                                valBuffer.append("::");
                                valBuffer.append(value);
                                valBuffer.append("::DVH'");
                            }
                            else {
                                valBuffer.append("'");
                                valBuffer.append(value);
                                valBuffer.append("'");
                            }
                            appendComma = true;
                        }
                    }
                    final StringBuilder revBuffer = new StringBuilder();
                    final List<String> pkCols = row.getPKColumns();
                    for (int j = 0; j < pkCols.size(); ++j) {
                        if (j > 0) {
                            revBuffer.append(" AND ");
                        }
                        final String columnName = pkCols.get(j);
                        revBuffer.append("`");
                        revBuffer.append(columnName);
                        revBuffer.append("` = ");
                        final Object value2 = row.get(j + 1);
                        if (value2 instanceof UniqueValueHolder) {
                            final String genName2 = ((UniqueValueHolder)value2).getGeneratorName();
                            revBuffer.append("'UVG::");
                            revBuffer.append(genName2);
                            revBuffer.append("::");
                            revBuffer.append(value2);
                            revBuffer.append("'");
                        }
                        else if (isDVHColumn(tableName, columnName)) {
                            revBuffer.append("'DVH::");
                            revBuffer.append(columnName);
                            revBuffer.append("::");
                            revBuffer.append(value2);
                            revBuffer.append("::DVH'");
                        }
                        else {
                            revBuffer.append("'");
                            revBuffer.append(value2);
                            revBuffer.append("'");
                        }
                    }
                    sb.append("INSTALL $ INSERT INTO `");
                    sb.append(tableName);
                    sb.append("` (");
                    sb.append(colBuffer.toString());
                    sb.append(") VALUES (");
                    sb.append(valBuffer.toString());
                    sb.append(")\nREVERT  $ DELETE FROM `");
                    sb.append(tableName);
                    sb.append("` WHERE ");
                    sb.append(revBuffer.toString());
                    sb.append("\n\n");
                }
            }
        }
    }
    
    public static void generateSql(final StringBuilder sb, final String fileName, final boolean isAppend) throws Exception {
        final File sqlFileObj = new File(fileName);
        final File parentDir = sqlFileObj.getParentFile();
        if (!parentDir.isDirectory()) {
            parentDir.mkdirs();
        }
        OutputStream sqlFileWrite = null;
        PrintStream printStream = null;
        try {
            sqlFileWrite = new FileOutputStream(sqlFileObj, isAppend);
            printStream = new PrintStream(sqlFileWrite);
            printStream.append(sb.toString());
        }
        finally {
            if (printStream != null) {
                printStream.close();
            }
            if (sqlFileWrite != null) {
                sqlFileWrite.close();
            }
        }
    }
    
    private static boolean isDVHColumn(final String tableName, final String columnName) {
        return DynamicValueHandlerRepositry.getDVHandlerTemplate(tableName, columnName) != null;
    }
    
    private static File getDDFile(final String baseDir, final String moduleName) throws Exception {
        File ddFile = new File(baseDir + File.separator + "conf" + File.separator + moduleName + File.separator + "dd-files.xml");
        if (!ddFile.exists()) {
            ddFile = new File(baseDir + File.separator + "conf" + File.separator + moduleName + File.separator + "data-dictionary.xml");
        }
        return ddFile;
    }
    
    private static void initializeDDs(final String productHome, final String ppmHome, final List<File> latestDDFiles, final List<File> dvhFiles, final String sqlGenClassName, final String dsname) throws Exception {
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Entered the method initializeDDs");
        final URL metaDDURL = DataDictionary.class.getResource("conf/meta-dd.xml");
        MetaDataAccess.loadDataDictionary(metaDDURL, false);
        final URL persDDURL = DataDictionary.class.getResource("conf/data-dictionary.xml");
        MetaDataAccess.loadDataDictionary(persDDURL, false);
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Loaded persistence module DD successfully.");
        final String existingModuleXML = productHome + File.separator + "conf" + File.separator + "module.xml";
        final String latestModuleXML = ppmHome + File.separator + "conf" + File.separator + "module.xml";
        final boolean latestModuleXMLExists = new File(latestModuleXML).exists();
        DataObject moduleDO = Xml2DoConverter.transform(existingModuleXML);
        Iterator<Row> iterator = moduleDO.getRows("Module");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String moduleName = (String)row.get(3);
            if ((moduleName.equalsIgnoreCase("SAS") || moduleName.equalsIgnoreCase("Scheduler")) && Boolean.getBoolean("skip.sas.modules")) {
                continue;
            }
            final String dvhFileStr = File.separator + "conf" + File.separator + moduleName + File.separator + "dynamic-value-handlers.xml";
            final File existingDDFile = getDDFile(productHome, moduleName);
            if (existingDDFile.exists()) {
                MetaDataAccess.loadDataDictionary(existingDDFile.toURL(), false);
                MigrationSqlGenerator.LOGGER.log(Level.INFO, "Loaded the data-dictionary :: [{0}]", existingDDFile);
            }
            if (latestModuleXMLExists) {
                continue;
            }
            final File latestDDFile = getDDFile(ppmHome, moduleName);
            if (latestDDFile.exists()) {
                latestDDFiles.add(latestDDFile);
            }
            addDVHFiles(dvhFiles, productHome, ppmHome, dvhFileStr);
        }
        if (latestModuleXMLExists) {
            moduleDO = Xml2DoConverter.transform(latestModuleXML);
            iterator = moduleDO.getRows("Module");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String moduleName = (String)row.get(3);
                if ((moduleName.equalsIgnoreCase("SAS") || moduleName.equalsIgnoreCase("Scheduler")) && Boolean.getBoolean("skip.sas.modules")) {
                    continue;
                }
                final String dvhFileStr = File.separator + "conf" + File.separator + moduleName + File.separator + "dynamic-value-handlers.xml";
                final File latestDDFile2 = getDDFile(ppmHome, moduleName);
                if (latestDDFile2.exists()) {
                    latestDDFiles.add(latestDDFile2);
                }
                addDVHFiles(dvhFiles, productHome, ppmHome, dvhFileStr);
            }
        }
        final String dsAdapter = PersistenceInitializer.getConfigurationValue("DSAdapter");
        final Properties dbadapter = PersistenceInitializer.getConfigurationProps((dsname != null) ? dsname : dsAdapter);
        final DBAdapter dbadap = (DBAdapter)Thread.currentThread().getContextClassLoader().loadClass(dbadapter.getProperty("dbadapter")).newInstance();
        if (sqlGenClassName != null) {
            dbadap.setSQLGenerator(MigrationSqlGenerator.sqlGenClass = (SQLGenerator)Thread.currentThread().getContextClassLoader().loadClass(sqlGenClassName).newInstance());
        }
        dbadap.initialize(dbadapter);
    }
    
    private static void removeDDsFromMetaData(final String productHome, final String ppmHome) throws Exception {
        final String oldconf_dir = productHome + File.separator + "conf";
        final String oldModuleXML = oldconf_dir + File.separator + "module.xml";
        final DataObject old_moduleDO = Xml2DoConverter.transform(oldModuleXML);
        final String newconf_dir = ppmHome + File.separator + "conf";
        final String newModuleXML = newconf_dir + File.separator + "module.xml";
        final DataObject new_moduleDO = Xml2DoConverter.transform(newModuleXML);
        removeDDsFromMetaData(oldconf_dir, old_moduleDO);
        removeDDsFromMetaData(newconf_dir, new_moduleDO);
    }
    
    private static void removeDDsFromMetaData(final String conf_dir, final DataObject moduleDO) throws Exception {
        final List<String> metaNames = new ArrayList<String>();
        final Iterator<Row> iterator = moduleDO.getRows("Module");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String moduleName = (String)row.get(3);
            if ((moduleName.equalsIgnoreCase("SAS") || moduleName.equalsIgnoreCase("Scheduler")) && Boolean.getBoolean("skip.sas.modules")) {
                continue;
            }
            metaNames.add(moduleName);
        }
        for (int i = metaNames.size() - 1; i >= 0; --i) {
            final String module_dir = conf_dir + File.separator + metaNames.get(i);
            final File module = new File(module_dir);
            final File ddfiles = new File(module_dir + "/dd-files.xml");
            final File ddxml = new File(module_dir + "/data-dictionary.xml");
            if (metaNames.get(i).equals("Persistence") || (module.isDirectory() && (ddfiles.exists() || ddxml.exists()))) {
                MigrationSqlGenerator.LOGGER.log(Level.INFO, "removeDataDictionaryConfiguration :: [{0}]", metaNames.get(i));
                if (MetaDataUtil.getDataDictionary(metaNames.get(i)) != null) {
                    MetaDataUtil.removeDataDictionaryConfiguration(metaNames.get(i));
                }
            }
        }
        if (MetaDataUtil.getDataDictionary("MetaPersistence") != null) {
            MetaDataUtil.removeDataDictionaryConfiguration("MetaPersistence");
        }
    }
    
    private static void addDVHFiles(final List<File> dvhFiles, final String productHome, final String ppmHome, final String dvhFileStr) {
        File dvhFile = new File(ppmHome + dvhFileStr);
        if (!dvhFile.exists()) {
            dvhFile = new File(productHome + dvhFileStr);
        }
        if (dvhFile.exists()) {
            dvhFiles.add(dvhFile);
        }
    }
    
    public static StringBuilder getQueries(final String[] args) throws Exception {
        final String serverHome = args[0];
        final String ppmHome = args[1];
        final String sqlGenName = args[2];
        final String dsname = args[3];
        String doXMLFileName = null;
        if (args.length > 4) {
            doXMLFileName = args[4];
        }
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "serverHome :: [{0}], ppmHome :: [{1}], sqlGenName :: [{2}], dsname :: [{3}], doXMLFileName :: [{4}]", new Object[] { serverHome, ppmHome, sqlGenName, dsname, doXMLFileName });
        final List<File> latestDDFiles = new ArrayList<File>();
        final List<File> dvhFiles = new ArrayList<File>();
        PersistenceInitializer.loadPersistenceConfigurations();
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Loading UDTs from directory :: {0}", ppmHome + File.separator + "conf" + File.separator + "udt");
        final File propFile = new File(ppmHome + File.separator + "conf" + File.separator + "app.properties");
        if (propFile.exists()) {
            try (final FileInputStream fis = new FileInputStream(propFile)) {
                final Properties appProps = new Properties();
                appProps.load(fis);
                final String propName = "ignore.udt.on.misconfiguration";
                final String ignoreUdtOnMisConfig = appProps.getProperty(propName);
                if (ignoreUdtOnMisConfig != null) {
                    AppResources.setProperty(propName, ignoreUdtOnMisConfig);
                }
            }
        }
        DataTypeManager.initialize(ppmHome + File.separator + "conf" + File.separator + "udt");
        initializeDDs(serverHome, ppmHome, latestDDFiles, dvhFiles, sqlGenName, dsname);
        DataModelUpdateUtil.initSQLGenerator(MigrationSqlGenerator.sqlGenClass);
        StringBuilder queries = new StringBuilder();
        if (doXMLFileName == null) {
            queries = generateDDQueries(queries, latestDDFiles);
        }
        else {
            queries = generateDOQueries(queries, doXMLFileName, dvhFiles);
        }
        removeDDsFromMetaData(serverHome, ppmHome);
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Finished.");
        return queries;
    }
    
    private static StringBuilder generateDDQueries(final StringBuilder sb, final List<File> latestDDFiles) throws Exception {
        sb.append("\n-- INSTALL and REVERT queries of data-dictionary changes\n");
        for (final File latestDDFile : latestDDFiles) {
            MigrationSqlGenerator.LOGGER.log(Level.INFO, "Going to generate the DD diff for the data-dictionary :: [{0}]", latestDDFile);
            final DataDictionary latestDD = DataDictionaryParser.getDataDictionary(latestDDFile.toURL());
            final DataDictionary oldDD = MetaDataUtil.getDataDictionary(latestDD.getName());
            DataModelUpdateUtil.generateChangesForDD(oldDD, latestDD, sb);
        }
        return sb;
    }
    
    private static StringBuilder generateDOQueries(final StringBuilder sb, final String doXMLFileName, final List<File> dvhFiles) throws Exception {
        if (doXMLFileName != null && new File(doXMLFileName).exists()) {
            sb.append("\n-- INSTALL and REVERT queries of the newly added entries of the do-xml :: ");
            sb.append(doXMLFileName);
            sb.append("\n\n");
            generateSQLsForDOXML(sb, doXMLFileName, dvhFiles);
        }
        return sb;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 3) {
            MigrationSqlGenerator.LOGGER.log(Level.INFO, "Three parameters are required to use this tool.\n1. SQLGenerator class name.\n2. Product Home.\n3. PPM Home");
            return;
        }
        final String sqlGenClassName = args[0];
        final String serverHome = args[1];
        final String ppmHome = args[2];
        String doXMLFileName = null;
        if (args.length > 3) {
            doXMLFileName = args[3];
        }
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "sqlGenerator Class :: [{0}], serverHome :: [{1}], ppmHome :: [{2}], doXMLFileName :: [{3}]", new Object[] { sqlGenClassName, serverHome, ppmHome, doXMLFileName });
        AppResources.setProperty("server.home", serverHome);
        final List<File> latestDDFiles = new ArrayList<File>();
        final List<File> dvhFiles = new ArrayList<File>();
        PersistenceInitializer.loadPersistenceConfigurations();
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Loading UDTs from directory :: {0}", Configuration.getString("app.home") + File.separator + "conf" + File.separator + "udt");
        DataTypeManager.initialize(ppmHome + File.separator + "conf" + File.separator + "udt");
        initializeDDs(serverHome, ppmHome, latestDDFiles, dvhFiles, sqlGenClassName, null);
        DataModelUpdateUtil.initSQLGenerator(MigrationSqlGenerator.sqlGenClass);
        StringBuilder sb = new StringBuilder();
        sb = generateDDQueries(sb, latestDDFiles);
        String writeToFile = serverHome + File.separator + "isu" + File.separator + "dd-changes.sql";
        generateSql(sb, writeToFile, false);
        if (doXMLFileName != null && new File(doXMLFileName).exists()) {
            sb = new StringBuilder();
            sb = generateDOQueries(sb, doXMLFileName, dvhFiles);
            writeToFile = serverHome + File.separator + "isu" + File.separator + "do-changes.sql";
            generateSql(sb, writeToFile, true);
        }
        MigrationSqlGenerator.LOGGER.log(Level.INFO, "Finished.");
    }
    
    static {
        LOGGER = Logger.getLogger(MigrationSqlGenerator.class.getName());
        MigrationSqlGenerator.sqlGenClass = null;
        (MigrationSqlGenerator.ignoreTablesInDOXML = new HashSet<String>()).add("ConfFile");
        MigrationSqlGenerator.ignoreTablesInDOXML.add("UVHValues");
    }
}
