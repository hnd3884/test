package com.me.devicemanagement.framework.webclient.common;

import java.util.Hashtable;
import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.DCPluginUtil;
import com.me.devicemanagement.framework.server.util.ConfFileLoader;
import java.util.Properties;
import java.util.HashMap;

public class ProductUrlLoader
{
    protected static ProductUrlLoader productUrlLoader;
    private HashMap<String, Properties> productGenProps;
    public static final String TRACING_CODE_KEY = "trackingcode";
    public static final String GET_EXTN_URL_KEY = "get_extn_license";
    public static final String GET_ADDITIONAL_USER_LIC_URL_KEY = "get_additional_user_license";
    public static final String QUESTION_MARK_SYMBOL = "?";
    
    protected ProductUrlLoader() {
        try {
            this.productGenProps = ConfFileLoader.getAllProductsProps("general_properties.conf");
            for (final Properties generalProperties : this.productGenProps.values()) {
                if (DCPluginUtil.getInstance().isPlugin()) {
                    final String fname = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "general_plugin_properties.conf";
                    final Properties generalPluginProperties = FileAccessUtil.readProperties(fname);
                    for (final String key : ((Hashtable<Object, V>)generalPluginProperties).keySet()) {
                        final String value = generalPluginProperties.getProperty(key);
                        generalProperties.setProperty(key, value);
                    }
                }
                generalProperties.setProperty("buildnumber", SyMUtil.getProductProperty("buildnumber"));
                generalProperties.setProperty("productversion", SyMUtil.getProductProperty("productversion"));
                generalProperties.setProperty("agentVersion", SyMUtil.getProductProperty("agentversion"));
                generalProperties.setProperty("dsVersion", SyMUtil.getProductProperty("distributionserversion"));
                generalProperties.setProperty("title", generalProperties.getProperty("productname"));
                Logger.getLogger(ProductUrlLoader.class.getName()).log(Level.WARNING, "generalProperties values {0}", generalProperties);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ProductUrlLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ProductUrlLoader getInstance() {
        if (ProductUrlLoader.productUrlLoader == null) {
            ProductUrlLoader.productUrlLoader = new ProductUrlLoader();
        }
        return ProductUrlLoader.productUrlLoader;
    }
    
    public Properties getGeneralProperites() {
        return ApiFactoryProvider.getUtilAccessAPI().getGeneralProperties();
    }
    
    public Properties getGeneralPropertiesDefaultImpl() {
        final String customerProduct = ApiFactoryProvider.getUtilAccessAPI().getCustomerProduct();
        if (customerProduct != null && this.productGenProps.containsKey(customerProduct)) {
            return this.productGenProps.get(customerProduct);
        }
        return this.productGenProps.get("DEFAULT");
    }
    
    public void updateGeneralPropsCache(final Properties properties) {
        this.getGeneralProperites().putAll(properties);
    }
    
    public String getValue(final String key) {
        final String value = ((Hashtable<K, String>)this.getGeneralProperites()).get(key);
        return value;
    }
    
    public String getValue(final String key, final String defaultValue) {
        String value = ((Hashtable<K, String>)this.getGeneralProperites()).get(key);
        value = ((value == null) ? defaultValue : value);
        return value;
    }
    
    public Properties getGeneralProperties(final String productName) {
        return ApiFactoryProvider.getUtilAccessAPI().getGeneralProperties(productName);
    }
    
    public Properties getGeneralPropertiesDefaultImpl(final String productName) {
        if (this.productGenProps.get(productName) != null) {
            return this.productGenProps.get(productName);
        }
        return null;
    }
    
    public String getValueForProduct(final String product, final String key) {
        if (this.productGenProps.get(product) != null) {
            return ((Hashtable<K, String>)this.productGenProps.get(product)).get(key);
        }
        return null;
    }
    
    static {
        ProductUrlLoader.productUrlLoader = null;
    }
}
