package com.me.devicemanagement.framework.server.util;

import javax.resource.NotSupportedException;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import com.me.devicemanagement.framework.utils.PropertyUtils;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.HashMap;

public class ProductClassLoader
{
    static HashMap<String, String> singleImplMap;
    static HashMap<String, String[]> multiImplMap;
    private static Logger logger;
    
    private static void addPropToHashMap(final Properties order) {
        final String key = "loader.property.order.";
        for (String filename : PropertyUtils.loadPropertiesBasedOnKey(order, key)) {
            if (filename != null && filename.length() != 0) {
                filename = PropertyUtils.serverPath + File.separator + filename;
                if (new File(filename).exists()) {
                    try {
                        final Properties keyFileOrder = FileUtils.readPropertyFile(new File(filename));
                        if (keyFileOrder.size() > 0) {
                            final Set keyValueOrder = keyFileOrder.keySet();
                            for (final Object Obj : keyValueOrder) {
                                final String propObj = (String)Obj;
                                final String[] value = keyFileOrder.getProperty(propObj).split(",");
                                if (value.length <= 1) {
                                    ProductClassLoader.singleImplMap.put(propObj, value[0]);
                                }
                                else {
                                    for (final String str : value) {
                                        if (str.equals("")) {
                                            ProductClassLoader.logger.log(Level.SEVERE, filename + " contains some of empty values for key : " + propObj);
                                            System.exit(0);
                                        }
                                    }
                                    ProductClassLoader.multiImplMap.put(propObj, value);
                                }
                            }
                        }
                        else {
                            ProductClassLoader.logger.log(Level.WARNING, filename + "has no properties to load...");
                        }
                    }
                    catch (final IOException e) {
                        ProductClassLoader.logger.log(Level.INFO, "Exception while trying to read the file" + filename);
                        System.exit(0);
                    }
                }
                else {
                    ProductClassLoader.logger.log(Level.SEVERE, filename + " does not exist...");
                    System.exit(0);
                }
            }
            else {
                ProductClassLoader.logger.log(Level.INFO, key + " was empty");
            }
        }
    }
    
    public static String[] getMultiImplProductClass(final String key) {
        if (!ProductClassLoader.singleImplMap.containsKey(key)) {
            return ProductClassLoader.multiImplMap.get(key);
        }
        if (ProductClassLoader.singleImplMap.containsKey(key)) {
            return new String[] { ProductClassLoader.singleImplMap.get(key) };
        }
        ProductClassLoader.logger.log(Level.SEVERE, key + " is not exist...");
        return null;
    }
    
    public static String getSingleImplProductClass(final String key) throws NotSupportedException {
        if (!ProductClassLoader.multiImplMap.containsKey(key)) {
            final String value = ProductClassLoader.singleImplMap.get(key);
            ProductClassLoader.logger.log(Level.FINEST, "Key : " + key + " value : " + value);
            return value;
        }
        if (ProductClassLoader.multiImplMap.containsKey(key)) {
            throw new NotSupportedException(key + " is not supported for single implementation");
        }
        ProductClassLoader.logger.log(Level.SEVERE, key + " is not exist...");
        return null;
    }
    
    static {
        ProductClassLoader.singleImplMap = new HashMap<String, String>();
        ProductClassLoader.multiImplMap = new HashMap<String, String[]>();
        (ProductClassLoader.logger = Logger.getLogger("ProductClassLoader")).log(Level.INFO, "***********Going to load Product specific classes***********");
        ProductClassLoader.logger.log(Level.INFO, "File path : " + PropertyUtils.serverPath);
        if (PropertyUtils.serverPath != null && PropertyUtils.serverPath.trim().length() != 0) {
            final File propertyFile = new File(PropertyUtils.order_file);
            if (propertyFile.exists()) {
                try {
                    final Properties order = FileUtils.readPropertyFile(propertyFile);
                    addPropToHashMap(order);
                    ProductClassLoader.logger.log(Level.INFO, "Loaded Single Implementation Values : " + ProductClassLoader.singleImplMap);
                    ProductClassLoader.logger.log(Level.INFO, "Loaded Multi Implementation Values : " + ProductClassLoader.multiImplMap);
                    ProductClassLoader.logger.log(Level.INFO, "***********Product specific classes are loaded***********");
                }
                catch (final Exception e) {
                    ProductClassLoader.logger.log(Level.SEVERE, "Exception while loading product specific loader..." + e);
                }
            }
            else {
                ProductClassLoader.logger.log(Level.SEVERE, PropertyUtils.order_file + " does not exist...");
            }
        }
        else {
            ProductClassLoader.logger.log(Level.SEVERE, "server  home was not found...");
        }
    }
}
