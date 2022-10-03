package com.me.devicemanagement.framework.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtils
{
    public static String serverPath;
    public static String order_file;
    
    public static List<String> loadPropertiesBasedOnKey(final Properties order, final String key) {
        final List valueList = new ArrayList();
        for (int loaderOrder = 1; loaderOrder <= order.size(); ++loaderOrder) {
            final String full_key = key + loaderOrder;
            final String filename = order.getProperty(full_key);
            if (filename != null && !filename.equals("")) {
                valueList.add(filename);
            }
        }
        return valueList;
    }
    
    static {
        PropertyUtils.serverPath = System.getProperty("server.home");
        PropertyUtils.order_file = PropertyUtils.serverPath + File.separator + "conf" + File.separator + "ProductLoaderProperties" + File.separator + "property-file-order.properties";
    }
}
