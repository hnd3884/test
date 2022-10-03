package com.me.devicemanagement.framework.server.util;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfFileLoader
{
    public static final String DEFAULT = "DEFAULT";
    
    public static String getConfFilePath(final String fileName, final String product) {
        String installDir = null;
        try {
            installDir = SyMUtil.getInstallationDir();
        }
        catch (final Exception e) {
            Logger.getLogger(ConfFileLoader.class.getName()).log(Level.SEVERE, null, e);
        }
        final String confFolder = "conf-" + product;
        final String productConfFile = installDir + File.separator + confFolder + File.separator + fileName;
        if (new File(productConfFile).exists() && !product.equalsIgnoreCase("DEFAULT")) {
            return productConfFile;
        }
        return installDir + File.separator + "conf" + File.separator + fileName;
    }
    
    private static List<String> getAllProduct() {
        return ApiFactoryProvider.getUtilAccessAPI().getProductList();
    }
    
    public static HashMap<String, Properties> getAllProductsProps(final String fileName) {
        final HashMap<String, Properties> productProperties = new HashMap<String, Properties>();
        try {
            productProperties.put("DEFAULT", FileAccessUtil.readProperties(getConfFilePath(fileName, "DEFAULT")));
            for (final String product : getAllProduct()) {
                final String filePath = getConfFilePath(fileName, product);
                final Properties props = FileAccessUtil.readProperties(filePath);
                productProperties.put(product, props);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(ConfFileLoader.class.getName()).log(Level.SEVERE, null, e);
        }
        return productProperties;
    }
}
