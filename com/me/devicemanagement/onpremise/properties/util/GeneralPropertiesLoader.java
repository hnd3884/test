package com.me.devicemanagement.onpremise.properties.util;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.HashMap;

public class GeneralPropertiesLoader
{
    private static final String DEFAULT = "DEFAULT";
    public static final String IS_PLUGIN = "isPlugin";
    public static final String SERVER_HOME_KEY = "server.home";
    public static HashMap<String, Properties> productGenProps;
    public static Properties allpropsOfActiveProducts;
    private static Boolean isPlugin;
    private static GeneralPropertiesLoader generalPropertiesLoader;
    private static Logger logger;
    
    private GeneralPropertiesLoader() {
        GeneralPropertiesLoader.productGenProps = getProductProps("general_properties.conf");
    }
    
    public static GeneralPropertiesLoader getInstance() {
        if (GeneralPropertiesLoader.generalPropertiesLoader == null) {
            GeneralPropertiesLoader.generalPropertiesLoader = new GeneralPropertiesLoader();
        }
        return GeneralPropertiesLoader.generalPropertiesLoader;
    }
    
    public Map getPropertiesMap() {
        return GeneralPropertiesLoader.productGenProps;
    }
    
    public Properties getPropsBasedOnProduct(final ArrayList list) {
        final Properties allpropsOfProducts = new Properties();
        final Properties allTotalPropsOfproducts = new Properties();
        try {
            ((Hashtable<String, Properties>)allpropsOfProducts).put("DEFAULT", readProperties(getConfFilePath("general_properties.conf", "DEFAULT")));
            allTotalPropsOfproducts.putAll(((Hashtable<K, Map<?, ?>>)allpropsOfProducts).get("DEFAULT"));
            for (final String productcode : list) {
                ((Hashtable<String, Properties>)allpropsOfProducts).put(productcode, readProperties(getConfFilePath("general_properties.conf", productcode)));
                allTotalPropsOfproducts.putAll(((Hashtable<K, Map<?, ?>>)allpropsOfProducts).get(productcode));
            }
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "general_plugin_properties.conf";
            final Properties generalPluginProperties = readProperties(fname);
            for (final String key : ((Hashtable<Object, V>)generalPluginProperties).keySet()) {
                final String value = generalPluginProperties.getProperty(key);
                allTotalPropsOfproducts.setProperty(key, value);
            }
            allTotalPropsOfproducts.setProperty("buildnumber", getProductProperty("buildnumber"));
            allTotalPropsOfproducts.setProperty("productversion", getProductProperty("productversion"));
            allTotalPropsOfproducts.setProperty("agentVersion", getProductProperty("agentversion"));
            allTotalPropsOfproducts.setProperty("dsVersion", getProductProperty("distributionserversion"));
            allTotalPropsOfproducts.setProperty("title", allTotalPropsOfproducts.getProperty("productname"));
            allTotalPropsOfproducts.putAll(allTotalPropsOfproducts);
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Exception while fetching GeneralProperties based on Product code");
        }
        return allTotalPropsOfproducts;
    }
    
    public Properties getProperties() {
        final HashMap<String, Properties> propsWithoutDefault = new HashMap<String, Properties>();
        if (GeneralPropertiesLoader.allpropsOfActiveProducts == null) {
            if (GeneralPropertiesLoader.productGenProps != null) {
                final Properties properties = GeneralPropertiesLoader.productGenProps.get("DEFAULT");
                (GeneralPropertiesLoader.allpropsOfActiveProducts = new Properties()).putAll(properties);
                final String[] productList = getAllActiveProduct();
                if (productList != null) {
                    for (final String product : productList) {
                        final Properties props = GeneralPropertiesLoader.productGenProps.get(product);
                        GeneralPropertiesLoader.allpropsOfActiveProducts.putAll(props);
                        propsWithoutDefault.put(product, props);
                    }
                }
            }
            if (!propsWithoutDefault.isEmpty()) {
                for (final Properties generalProperties : propsWithoutDefault.values()) {
                    if (generalProperties != null && !generalProperties.isEmpty()) {
                        try {
                            this.setGeneralProperties(generalProperties);
                        }
                        catch (final Exception ex) {
                            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Exception while setting GeneralProperties", ex);
                        }
                    }
                }
            }
            else {
                GeneralPropertiesLoader.productGenProps = getProductProps("general_properties.conf");
                this.getProperties();
            }
            GeneralPropertiesLoader.allpropsOfActiveProducts.setProperty("buildnumber", getProductProperty("buildnumber"));
            GeneralPropertiesLoader.allpropsOfActiveProducts.setProperty("productversion", getProductProperty("productversion"));
            GeneralPropertiesLoader.allpropsOfActiveProducts.setProperty("agentVersion", getProductProperty("agentversion"));
            GeneralPropertiesLoader.allpropsOfActiveProducts.setProperty("dsVersion", getProductProperty("distributionserversion"));
            GeneralPropertiesLoader.allpropsOfActiveProducts.setProperty("title", GeneralPropertiesLoader.allpropsOfActiveProducts.getProperty("productname"));
        }
        return GeneralPropertiesLoader.allpropsOfActiveProducts;
    }
    
    private void setGeneralProperties(final Properties generalProperties) throws Exception {
        if (isPlugin()) {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "general_plugin_properties.conf";
            final Properties generalPluginProperties = readProperties(fname);
            for (final String key : ((Hashtable<Object, V>)generalPluginProperties).keySet()) {
                final String value = generalPluginProperties.getProperty(key);
                generalProperties.setProperty(key, value);
            }
        }
        generalProperties.setProperty("buildnumber", getProductProperty("buildnumber"));
        generalProperties.setProperty("productversion", getProductProperty("productversion"));
        generalProperties.setProperty("agentVersion", getProductProperty("agentversion"));
        generalProperties.setProperty("dsVersion", getProductProperty("distributionserversion"));
        generalProperties.setProperty("title", generalProperties.getProperty("productname"));
        GeneralPropertiesLoader.allpropsOfActiveProducts.putAll(generalProperties);
        GeneralPropertiesLoader.logger.log(Level.FINE, "generalProperties values {0}", generalProperties);
    }
    
    private static String getProductProperty(final String key) {
        String value = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "product.conf";
            GeneralPropertiesLoader.logger.log(Level.FINE, "***********getProductProperty***********fname: " + fname);
            final Properties props = readProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Caught exception while getting product property: " + key, ex);
        }
        return value;
    }
    
    public static String getInstallationDir() throws Exception {
        String path = null;
        try {
            final String serverHome = getServerHome();
            path = getCanonicalPath(serverHome);
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Caught exception while getting Installation Directory. ", ex);
            throw ex;
        }
        return path;
    }
    
    private static String getCanonicalPath(final String filePath) throws Exception {
        final String canonicalPath = new File(filePath).getCanonicalPath();
        return canonicalPath;
    }
    
    private static String getServerHome() {
        String serverHome = System.getProperty("server.home");
        if (serverHome == null) {
            serverHome = "..";
            System.setProperty("server.home", serverHome);
        }
        return System.getProperty("server.home");
    }
    
    private static String getPluginProperties(final String key) {
        String value = null;
        try {
            final String fname = getInstallationDir() + File.separator + "conf" + File.separator + "plugin_properties.conf";
            final Properties props = readProperties(fname);
            GeneralPropertiesLoader.logger.log(Level.FINE, "plugin_properties === {0} ", props);
            value = props.getProperty(key);
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Caught exception while getting property from plugin_properties file : ", ex);
        }
        return value;
    }
    
    private static boolean isPlugin() {
        if (GeneralPropertiesLoader.isPlugin == null) {
            GeneralPropertiesLoader.logger.log(Level.INFO, "Inside isPlugin method, isPlugin value null. Hence reading from plugin_properties file and set the value");
            final String val = getPluginProperties("isPlugin");
            setIsPluginProperty(val);
        }
        return GeneralPropertiesLoader.isPlugin;
    }
    
    private static void setIsPluginProperty(final String value) {
        if (value != null && value.equals("true")) {
            GeneralPropertiesLoader.isPlugin = Boolean.TRUE;
        }
        else {
            GeneralPropertiesLoader.isPlugin = Boolean.FALSE;
        }
    }
    
    public static Properties readProperties(final String confFileName) throws Exception {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    private static HashMap<String, Properties> getProductProps(final String fileName) {
        final HashMap<String, Properties> productProperties = new HashMap<String, Properties>();
        try {
            productProperties.put("DEFAULT", readProperties(getConfFilePath(fileName, "DEFAULT")));
            final String[] productList = getAllActiveProduct();
            if (productList != null) {
                for (final String product : productList) {
                    final String filePath = getConfFilePath(fileName, product);
                    final Properties props = readProperties(filePath);
                    productProperties.put(product, props);
                }
            }
        }
        catch (final Exception e) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, null, e);
        }
        return productProperties;
    }
    
    public static String[] getAllActiveProduct() {
        String installDir = null;
        try {
            installDir = getInstallationDir();
        }
        catch (final Exception e) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, null, e);
        }
        final String productConfFile = installDir + File.separator + "conf" + File.separator + "product.conf";
        String[] formattedProductCodes = null;
        try {
            final Properties props = readProperties(productConfFile);
            if (props.containsKey("activeproductcodes")) {
                final String products = ((Hashtable<K, String>)props).get("activeproductcodes");
                final String[] productCodes = products.split(",");
                formattedProductCodes = new String[productCodes.length];
                int i = 0;
                for (String code : productCodes) {
                    code = code.toLowerCase();
                    formattedProductCodes[i] = code;
                    ++i;
                }
            }
        }
        catch (final Exception e2) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, null, e2);
        }
        return formattedProductCodes;
    }
    
    private static String getConfFilePath(String fileName, final String product) {
        String installDir = null;
        try {
            installDir = getInstallationDir();
        }
        catch (final Exception e) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, null, e);
        }
        if (!product.equals("DEFAULT")) {
            fileName = product + "_" + fileName;
        }
        final String productSpecificConfFile = installDir + File.separator + "conf" + File.separator + fileName;
        if (new File(productSpecificConfFile).exists() && !product.equalsIgnoreCase("DEFAULT")) {
            return productSpecificConfFile;
        }
        return installDir + File.separator + "conf" + File.separator + fileName;
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName) {
        storeProperties(newprops, confFileName, null);
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName, final String comments) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
            final Enumeration keys = newprops.propertyNames();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                props.setProperty(key, newprops.getProperty(key));
            }
            fos = new FileOutputStream(confFileName);
            props.store(fos, comments);
            fos.close();
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Caught exception: " + ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static void removeProperties(final ArrayList<String> keys, final String confFileName) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
            if (!props.isEmpty()) {
                for (final String key : keys) {
                    props.remove(key);
                }
                fos = new FileOutputStream(confFileName);
                props.store(fos, null);
                fos.close();
            }
        }
        catch (final Exception ex) {
            GeneralPropertiesLoader.logger.log(Level.SEVERE, "Caught exception: " + ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    static {
        GeneralPropertiesLoader.productGenProps = null;
        GeneralPropertiesLoader.allpropsOfActiveProducts = null;
        GeneralPropertiesLoader.isPlugin = null;
        GeneralPropertiesLoader.generalPropertiesLoader = null;
        GeneralPropertiesLoader.logger = Logger.getLogger(GeneralPropertiesLoader.class.getName());
    }
}
