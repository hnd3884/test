package com.adventnet.persistence.util;

import java.util.ArrayList;
import com.zoho.conf.Configuration;
import java.util.Iterator;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Properties;
import java.util.Collections;
import java.io.IOException;
import com.zoho.conf.tree.ConfTreeBuilder;
import java.util.logging.Level;
import java.io.File;
import com.zoho.conf.tree.ConfTree;
import java.util.List;
import java.util.logging.Logger;

public class DCManager
{
    private static final String DC_PROPERTY_FILE;
    private static final Logger LOGGER;
    private static List<String> dcTypes;
    private static ConfTree dcConfigurations;
    private static boolean validate;
    
    public static void initialize() throws IOException {
        if (DCManager.validate) {
            return;
        }
        final File dcPropFile = new File(DCManager.DC_PROPERTY_FILE);
        if (!dcPropFile.exists()) {
            DCManager.LOGGER.log(Level.INFO, "dynamic-column-types not defined at " + DCManager.DC_PROPERTY_FILE);
            return;
        }
        DCManager.dcConfigurations = ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(DCManager.DC_PROPERTY_FILE)).build();
        DCManager.dcTypes = DCManager.dcConfigurations.getImmediateChildren();
        validate();
        DCManager.LOGGER.log(Level.FINE, "dc-types defined :: {0}", DCManager.dcConfigurations);
        DCManager.LOGGER.log(Level.INFO, "dc-types defined in dynamic-column-types.props :: {0}", DCManager.dcTypes);
    }
    
    public static List<String> getDCTypes() {
        return Collections.unmodifiableList((List<? extends String>)DCManager.dcTypes);
    }
    
    public static ConfTree getAllPropsAsTree() {
        return DCManager.dcConfigurations;
    }
    
    public static Properties getProps(final String path) {
        if (DCManager.dcTypes == null || DCManager.dcTypes.isEmpty()) {
            throw new IllegalArgumentException("dc-types are not defined in dynamic-column-types.props");
        }
        final Properties props = new Properties();
        if (DCManager.dcConfigurations.get(path + ".dcadapter") == null) {
            return null;
        }
        props.setProperty("dcadapter", DCManager.dcConfigurations.get(path + ".dcadapter"));
        props.setProperty("dcsqlgenerator", DCManager.dcConfigurations.get(path + ".dcsqlgenerator"));
        if (DCManager.dcConfigurations.get(path + ".dcmhandler") != null) {
            props.setProperty("dcmhandler", DCManager.dcConfigurations.get(path + ".dcmhandler"));
        }
        DCManager.LOGGER.log(Level.FINE, "Returning props from DCManager.getProps() for path [{0}] and props [{1}]", new Object[] { path, props });
        return props;
    }
    
    private static void validate() {
        for (final String dcType : DCManager.dcTypes) {
            final ConfTree dcConfTree = DCManager.dcConfigurations.getSubTree(dcType, true);
            final List<String> definedDatabases = dcConfTree.getImmediateChildren();
            if (definedDatabases.contains("dcadapter")) {
                final String dcadapter = dcConfTree.get("dcadapter");
                final String dcsqlgenerator = dcConfTree.get("dcsqlgenerator");
                final String dcmhandler = dcConfTree.get("dcmhandler");
                if (!definedDatabases.contains("mysql")) {
                    DCManager.dcConfigurations.put(dcType + ".mysql.dcadapter", dcadapter);
                    DCManager.dcConfigurations.put(dcType + ".mysql.dcsqlgenerator", dcsqlgenerator);
                    DCManager.dcConfigurations.put(dcType + ".mysql.dcmhandler", dcmhandler);
                }
                if (!definedDatabases.contains("postgres")) {
                    DCManager.dcConfigurations.put(dcType + ".postgres.dcadapter", dcadapter);
                    DCManager.dcConfigurations.put(dcType + ".postgres.dcsqlgenerator", dcsqlgenerator);
                    DCManager.dcConfigurations.put(dcType + ".postgres.dcmhandler", dcmhandler);
                }
                if (!definedDatabases.contains("mssql")) {
                    DCManager.dcConfigurations.put(dcType + ".mssql.dcadapter", dcadapter);
                    DCManager.dcConfigurations.put(dcType + ".mssql.dcsqlgenerator", dcsqlgenerator);
                    DCManager.dcConfigurations.put(dcType + ".mssql.dcmhandler", dcmhandler);
                }
            }
            definedDatabases.remove("dcadapter");
            definedDatabases.remove("dcsqlgenerator");
            definedDatabases.remove("dcmhandler");
            for (final String database : definedDatabases) {
                if (dcConfTree.getSubTree(dcType + "." + database) == null || dcConfTree.getSubTree(dcType + "." + database).isEmpty()) {
                    throw new IllegalArgumentException("Dynamic Column Handlers not found for type :: " + dcType + " database :: " + database);
                }
                final String dcadapter2 = dcConfTree.getSubTree(dcType + "." + database, true).get("dcadapter");
                final String dcsqlgenerator2 = dcConfTree.getSubTree(dcType + "." + database, true).get("dcsqlgenerator");
                validateDCProps(dcType, database, dcadapter2, dcsqlgenerator2);
            }
            for (final String database : PersistenceInitializer.getDatabases()) {
                if (dcConfTree.getSubTree(dcType + "." + database) == null) {
                    throw new IllegalArgumentException("Dynamic Column Handlers not found for type :: " + dcType + " database :: " + database);
                }
            }
        }
        DCManager.validate = true;
    }
    
    private static void validateDCProps(final String dcType, final String database, final String dcadapter, final String dcsqlgenerator) {
        if (dcadapter == null) {
            throw new IllegalArgumentException("Property [dcadapter] is missing for dctype :: " + dcType + ", database :: " + database);
        }
        if (dcsqlgenerator != null) {
            DCManager.LOGGER.log(Level.INFO, "All valid properties are available for dctype :: {0}, database :: {1}", new Object[] { dcType, database });
            return;
        }
        throw new IllegalArgumentException("Property [dcsqlgenerator] is missing for dctype :: " + dcType + ", database :: " + database);
    }
    
    static {
        DC_PROPERTY_FILE = Configuration.getString("server.home") + File.separator + "conf" + File.separator + "dynamic-column-types.props";
        LOGGER = Logger.getLogger(DCManager.class.getName());
        DCManager.dcTypes = new ArrayList<String>();
        DCManager.dcConfigurations = null;
        DCManager.validate = false;
    }
}
