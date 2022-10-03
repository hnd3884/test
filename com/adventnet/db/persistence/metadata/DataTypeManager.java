package com.adventnet.db.persistence.metadata;

import java.util.HashMap;
import com.zoho.conf.Configuration;
import java.sql.Connection;
import com.adventnet.db.adapter.DTTransformer;
import com.adventnet.db.adapter.DTResultSetAdapter;
import com.adventnet.db.adapter.DTSQLGenerator;
import com.adventnet.db.adapter.DTAdapter;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.DataTypeValidator;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.zoho.conf.tree.ConfTree;
import java.util.logging.Level;
import com.zoho.conf.AppResources;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.conf.tree.ConfTreeBuilder;
import java.io.FileFilter;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public class DataTypeManager
{
    private static final Logger LOGGER;
    private static String server_home;
    private static final String DATA_TYPE_CONF_DIR;
    private static boolean validate;
    private static Map<String, DataTypeDefinition> dataTypeDefinitions;
    private static Map<String, Integer> sqlTypes;
    private static int sqlTypeVal;
    
    @Deprecated
    public static void initialize() throws Exception {
        initialize(null);
    }
    
    public static void initialize(final String additionalDataTypeConfDir) throws Exception {
        if (!DataTypeManager.validate) {
            String dataTypeConfDir = DataTypeManager.DATA_TYPE_CONF_DIR;
            if (additionalDataTypeConfDir != null) {
                dataTypeConfDir = additionalDataTypeConfDir;
            }
            if (new File(dataTypeConfDir).exists()) {
                final File[] dataTypeConfFiles = new File(dataTypeConfDir).listFiles(new DataTypeFileNameFilter());
                if (dataTypeConfFiles != null && dataTypeConfFiles.length > 0) {
                    for (final File dataTypeConfFile : dataTypeConfFiles) {
                        final ConfTree datatypes = ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(dataTypeConfFile.getAbsolutePath())).build();
                        if (!PersistenceInitializer.onSAS()) {
                            if (!new File(dataTypeConfFile.getAbsolutePath() + ".digest").exists()) {
                                throw new RuntimeException("Digest file not found for configuration file :: " + dataTypeConfFile.getAbsolutePath());
                            }
                            final String newDigest = FileUtils.getDigest(dataTypeConfFile.getAbsolutePath());
                            final String oldDigest = new String(Files.readAllBytes(Paths.get(dataTypeConfFile.getAbsolutePath() + ".digest", new String[0])));
                            if (!oldDigest.equals(newDigest)) {
                                if (!AppResources.getBoolean("development.mode", Boolean.valueOf(false))) {
                                    throw new RuntimeException("DataType Configuration File :: " + dataTypeConfFile.getAbsolutePath() + " is modified");
                                }
                                DataTypeManager.LOGGER.log(Level.SEVERE, "DataType Configuration File :: {0} is modified. Regenerate digest file.", dataTypeConfFile.getAbsolutePath());
                            }
                        }
                        loadDataTypes(datatypes);
                    }
                    DataTypeManager.LOGGER.log(Level.SEVERE, "DataTypes initialized from :: {0}", dataTypeConfDir);
                }
                else {
                    DataTypeManager.LOGGER.log(Level.SEVERE, "No datatype configuration found in :: {0}", dataTypeConfDir);
                }
            }
            else {
                DataTypeManager.LOGGER.log(Level.SEVERE, "Data Type Configuration directory :: {0} not found", dataTypeConfDir);
            }
        }
    }
    
    private static void loadDataTypes(final ConfTree datatypes) throws Exception {
        if (datatypes == null || datatypes.isEmpty()) {
            return;
        }
        for (String dataType : datatypes.getImmediateChildren()) {
            ConfTree dataTypeConfTree = datatypes.getSubTree(dataType, true);
            DataTypeDefinition udt = null;
            if (dataTypeConfTree != null && !dataTypeConfTree.isEmpty()) {
                try {
                    if (dataTypeConfTree.getSubTree(dataType + ".basic") != null && dataTypeConfTree.getSubTree(dataType + ".advanced") != null) {
                        throw new IllegalArgumentException("Configuration can be either basic or advanced");
                    }
                    if (dataTypeConfTree.getSubTree(dataType + ".basic") != null && !dataTypeConfTree.getSubTree(dataType + ".basic").isEmpty()) {
                        dataTypeConfTree = dataTypeConfTree.getSubTree(dataType + ".basic", true);
                        String baseType = dataTypeConfTree.get("baseType");
                        final int maxLength = (dataTypeConfTree.get("maxLength") != null) ? Integer.parseInt(dataTypeConfTree.get("maxLength").trim()) : 0;
                        final int precision = (dataTypeConfTree.get("precision") != null) ? Integer.parseInt(dataTypeConfTree.get("precision").trim()) : 0;
                        final String allowedList = dataTypeConfTree.get("allowedList");
                        final String allowedRangeFrom = dataTypeConfTree.get("allowedRangeFrom");
                        final String allowedRangeTo = dataTypeConfTree.get("allowedRangeTo");
                        final String allowedPattern = dataTypeConfTree.get("allowedPattern");
                        final String defaultValueStr = dataTypeConfTree.get("defaultValue");
                        final String validator = dataTypeConfTree.get("validator");
                        if (baseType == null) {
                            throw new IllegalArgumentException("Base Type is not defined for type :: " + dataType);
                        }
                        baseType = baseType.trim();
                        AllowedValues allowedValue = null;
                        if ((allowedList != null && !allowedList.equals("")) || (allowedRangeFrom != null && !allowedRangeFrom.equals("")) || (allowedRangeTo != null && !allowedRangeTo.equals("")) || (allowedPattern != null && !allowedPattern.equals(""))) {
                            allowedValue = new AllowedValues();
                            if (allowedList != null) {
                                final String[] split;
                                final String[] splitValues = split = allowedList.trim().replaceAll("^[,\\s]+", "").split(",");
                                for (final String value : split) {
                                    allowedValue.addValue(MetaDataUtil.convert(value.trim(), baseType));
                                }
                            }
                            else if (allowedRangeFrom != null || allowedRangeTo != null) {
                                if (allowedRangeFrom != null) {
                                    allowedValue.setFromVal(MetaDataUtil.convert(allowedRangeFrom.trim(), baseType));
                                }
                                if (allowedRangeTo != null) {
                                    allowedValue.setToVal(MetaDataUtil.convert(allowedRangeTo.trim(), baseType));
                                }
                            }
                            else if (allowedPattern != null) {
                                allowedValue.setPattern(allowedPattern.trim());
                            }
                        }
                        Object defaultValue = null;
                        if (defaultValueStr != null) {
                            defaultValue = MetaDataUtil.convert(defaultValueStr.trim(), baseType);
                        }
                        udt = new DataTypeDefinition(dataType, baseType, maxLength, precision, allowedValue, defaultValue);
                        if (validator != null) {
                            udt.setValidator((DataTypeValidator)getInstance(validator.trim()));
                        }
                    }
                    else {
                        if (dataTypeConfTree.getSubTree(dataType + ".advanced") == null || dataTypeConfTree.getSubTree(dataType + ".advanced").isEmpty()) {
                            throw new IllegalArgumentException("Unknown Configuration " + dataTypeConfTree.getSubTree(dataType) + " for dataType :: " + dataType);
                        }
                        dataTypeConfTree = dataTypeConfTree.getSubTree(dataType + ".advanced", true);
                        if (dataTypeConfTree.get("meta") == null) {
                            throw new IllegalArgumentException("Meta not defined for advanced udt type :: " + dataType);
                        }
                        final DataTypeMetaInfo dtmi = (DataTypeMetaInfo)getInstance(dataTypeConfTree.get("meta").trim());
                        udt = new DataTypeDefinition(dataType, dtmi);
                        final List<String> udtInterfaces = new ArrayList<String>();
                        udtInterfaces.add("dtadapter");
                        udtInterfaces.add("dtsqlgenerator");
                        udtInterfaces.add("dtresultsetadapter");
                        udtInterfaces.add("dttransformer");
                        final List<String> definedDatabases = dataTypeConfTree.getImmediateChildren();
                        if (definedDatabases.containsAll(udtInterfaces)) {
                            definedDatabases.clear();
                            definedDatabases.add("postgres");
                            definedDatabases.add("mysql");
                            definedDatabases.add("mssql");
                        }
                        else {
                            definedDatabases.remove("meta");
                            definedDatabases.remove("dtadapter");
                            definedDatabases.remove("dtsqlgenerator");
                            definedDatabases.remove("dtresultsetadapter");
                            definedDatabases.remove("dttransformer");
                        }
                        buildAdvancedTypes(definedDatabases, dataTypeConfTree, dataType);
                        for (final String database : definedDatabases) {
                            initializeInstances(dataTypeConfTree.getSubTree("advanced." + database, true), database, udt);
                        }
                    }
                }
                catch (final IllegalArgumentException e) {
                    if (AppResources.getString("ignore.udt.on.misconfiguration", "false").equalsIgnoreCase("true")) {
                        DataTypeManager.LOGGER.log(Level.WARNING, "Exception while loading data type :: " + dataType);
                        e.printStackTrace();
                        continue;
                    }
                    throw e;
                }
                dataType = dataType.toUpperCase(Locale.ENGLISH);
                if (udt == null) {
                    continue;
                }
                if (DataTypeManager.dataTypeDefinitions.containsKey(dataType)) {
                    throw new IllegalArgumentException("DataType :: " + dataType + " already defined");
                }
                udt.validate();
                DataTypeManager.dataTypeDefinitions.put(dataType, udt);
                if (udt.getMeta() != null) {
                    DataTypeManager.sqlTypes.put(dataType, new Integer(DataTypeManager.sqlTypeVal++));
                }
                DataTypeManager.LOGGER.log(Level.INFO, "Loaded data type :: {0}", udt.getDataType());
            }
        }
        DataTypeManager.validate = true;
    }
    
    private static Object getInstance(final String className) throws Exception {
        try {
            final Object instance = Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
            return instance;
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        catch (final Exception ex) {
            DataTypeManager.LOGGER.log(Level.SEVERE, "Error while trying to instantiate Object for class :: {0} with exception {1}", new Object[] { className, ex });
            throw ex;
        }
    }
    
    public static DataTypeDefinition getDataTypeDefinition(final String dataType) {
        if (dataType != null) {
            return DataTypeManager.dataTypeDefinitions.get(dataType.toUpperCase(Locale.ENGLISH));
        }
        if (AppResources.getString("nodatatype.throw.exception", "true").equalsIgnoreCase("true")) {
            throw new IllegalArgumentException("Datatype cannot be null.");
        }
        return null;
    }
    
    public static List<String> getDataTypes() {
        final List<String> dataTypes = new ArrayList<String>();
        final Iterator<String> iterator = DataTypeManager.dataTypeDefinitions.keySet().iterator();
        while (iterator.hasNext()) {
            dataTypes.add(iterator.next());
        }
        return dataTypes;
    }
    
    public static boolean isDataTypeSupported(final int sqlType) {
        return DataTypeManager.sqlTypes.containsValue(sqlType);
    }
    
    public static boolean isDataTypeSupported(final String dataTypeName) {
        if (dataTypeName != null) {
            return DataTypeManager.dataTypeDefinitions.containsKey(dataTypeName.toUpperCase(Locale.ENGLISH));
        }
        throw new IllegalArgumentException("Datatype cannot be null.");
    }
    
    public static synchronized void addDataType(String dataType, final DataTypeDefinition udtDefinition) throws IOException {
        if (!udtDefinition.isValidated()) {
            udtDefinition.validate();
        }
        dataType = dataType.toUpperCase(Locale.ENGLISH);
        if (!DataTypeManager.dataTypeDefinitions.containsKey(dataType)) {
            File dataTypeFile;
            for (dataTypeFile = new File(DataTypeManager.DATA_TYPE_CONF_DIR + File.separator + UUID.randomUUID().toString() + ".dt"); dataTypeFile.exists(); dataTypeFile = new File(DataTypeManager.DATA_TYPE_CONF_DIR + File.separator + UUID.randomUUID().toString() + ".dt")) {}
            final Properties p = new Properties();
            if (udtDefinition.getBaseType() != null) {
                p.setProperty(dataType + ".basic.baseType", udtDefinition.getBaseType());
                p.setProperty(dataType + ".basic.maxLength", "" + udtDefinition.getMaxLength());
                p.setProperty(dataType + ".basic.precision", "" + udtDefinition.getPrecision());
                if (udtDefinition.getAllowedValues() != null) {
                    if (udtDefinition.getAllowedValues().getValueList() != null && udtDefinition.getAllowedValues().getValueList().size() != 0) {
                        p.setProperty(dataType + ".basic.allowedList", getAllowedList(udtDefinition.getAllowedValues()));
                    }
                    else if (udtDefinition.getAllowedValues().getFromVal() != null || udtDefinition.getAllowedValues().getToVal() != null) {
                        if (udtDefinition.getAllowedValues().getFromVal() != null) {
                            p.setProperty(dataType + ".basic.allowedRangeFrom", udtDefinition.getAllowedValues().getFromVal().toString());
                        }
                        if (udtDefinition.getAllowedValues().getToVal() != null) {
                            p.setProperty(dataType + ".basic.allowedRangeTo", udtDefinition.getAllowedValues().getToVal().toString());
                        }
                    }
                    else if (udtDefinition.getAllowedValues().getPattern() != null) {
                        p.setProperty(dataType + ".basic.allowedPattern", udtDefinition.getAllowedValues().getPattern());
                    }
                }
                if (udtDefinition.getDefaultValue() != null) {
                    p.setProperty(dataType + ".basic.defaultValue", udtDefinition.getDefaultValue().toString());
                }
                if (udtDefinition.getValidator() != null) {
                    p.setProperty(dataType + ".basic.validator", udtDefinition.getValidator().getClass().getCanonicalName());
                }
            }
            else if (udtDefinition.getMeta() != null) {
                throw new IllegalArgumentException("Advanced UDT cannot be created dynamically.");
            }
            try {
                FileUtils.writeToFile(dataTypeFile, p, (String)null);
                FileUtils.generateDigestFile(dataTypeFile.getAbsolutePath());
            }
            catch (final IOException e) {
                DataTypeManager.LOGGER.log(Level.SEVERE, "Problem while writing new data type conf file for type :: " + dataType);
                FileUtils.deleteFile(dataTypeFile);
                throw e;
            }
            DataTypeManager.dataTypeDefinitions.put(dataType, udtDefinition);
            return;
        }
        throw new IllegalArgumentException("DataType :: " + dataType + " is already defined.");
    }
    
    private static void buildAdvancedTypes(final List<String> definedDatabases, final ConfTree dataTypeConfTree, final String datatype) {
        if (dataTypeConfTree.isEmpty()) {
            return;
        }
        for (final String database : definedDatabases) {
            final String[] array;
            final String[] udtClasses = array = new String[] { "dtadapter", "dtsqlgenerator", "dtresultsetadapter", "dttransformer" };
            for (final String udtClass : array) {
                if (dataTypeConfTree.getSubTree("advanced." + database + "." + udtClass) == null || dataTypeConfTree.getSubTree("advanced." + database + "." + udtClass).isEmpty()) {
                    if (dataTypeConfTree.get(udtClass) == null) {
                        throw new IllegalArgumentException("Property [" + udtClass + "] not defined for type :: " + datatype + " database:: " + database);
                    }
                    dataTypeConfTree.put("advanced." + database + "." + udtClass, dataTypeConfTree.get(udtClass));
                }
            }
        }
    }
    
    private static void initializeInstances(final ConfTree confTree, final String database, final DataTypeDefinition udtDefinition) throws Exception {
        if (confTree.isEmpty()) {
            return;
        }
        final String dtadapter = confTree.get("dtadapter").trim();
        final String dtsqlgenerator = confTree.get("dtsqlgenerator").trim();
        final String dtresultsetadapter = confTree.get("dtresultsetadapter").trim();
        final String dttransformer = confTree.get("dttransformer").trim();
        udtDefinition.setDTAdapter((DTAdapter)getInstance(dtadapter), database);
        udtDefinition.setDTSQLGenerator((DTSQLGenerator)getInstance(dtsqlgenerator), database);
        udtDefinition.setDTResultSetAdapter((DTResultSetAdapter)getInstance(dtresultsetadapter), database);
        udtDefinition.setDTTransformer((DTTransformer)getInstance(dttransformer), database);
    }
    
    private static String getAllowedList(final AllowedValues allowedValues) {
        final StringBuilder sb = new StringBuilder();
        final List<?> allowedList = allowedValues.getValueList();
        for (int i = 0; i < allowedList.size(); ++i) {
            sb.append(allowedList.get(i));
            if (i < allowedList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    public static void validateDataTypes(final Connection conn, final String dbType) {
        for (final String dataType : DataTypeManager.dataTypeDefinitions.keySet()) {
            final DataTypeDefinition dataTypeDefinition = DataTypeManager.dataTypeDefinitions.get(dataType);
            dataTypeDefinition.validateDTAdapters(conn, dbType);
        }
    }
    
    public static void reload() throws Exception {
        unload();
        initialize(null);
    }
    
    public static void unload() {
        DataTypeManager.validate = false;
        if (!DataTypeManager.dataTypeDefinitions.isEmpty()) {
            DataTypeManager.dataTypeDefinitions.clear();
        }
        if (!DataTypeManager.sqlTypes.isEmpty()) {
            DataTypeManager.sqlTypes.clear();
        }
    }
    
    public static int getSQLType(final String dataType) {
        if (DataTypeManager.sqlTypes.get(dataType.toUpperCase(Locale.ENGLISH)) != null) {
            return DataTypeManager.sqlTypes.get(dataType.toUpperCase(Locale.ENGLISH));
        }
        return -9999;
    }
    
    public static DataTypeDefinition getDataTypeDefinition(final int sqlType) {
        for (final String dataType : DataTypeManager.dataTypeDefinitions.keySet()) {
            final DataTypeDefinition dtd = getDataTypeDefinition(dataType);
            if (getSQLType(dataType) == sqlType) {
                return dtd;
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(DataTypeManager.class.getName());
        DataTypeManager.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        DATA_TYPE_CONF_DIR = DataTypeManager.server_home + File.separator + "conf" + File.separator + "udt";
        DataTypeManager.validate = false;
        DataTypeManager.dataTypeDefinitions = new HashMap<String, DataTypeDefinition>();
        DataTypeManager.sqlTypes = new HashMap<String, Integer>();
        DataTypeManager.sqlTypeVal = 5000;
    }
    
    static class DataTypeFileNameFilter implements FileFilter
    {
        @Override
        public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".dt");
        }
    }
}
