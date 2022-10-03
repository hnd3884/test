package com.me.devicemanagement.framework.server.queue;

import java.util.Collection;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.Properties;
import com.me.devicemanagement.framework.utils.JsonUtils;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import com.me.devicemanagement.framework.utils.PropertyUtils;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;

public class QueueConfigurations
{
    private static final Logger LOGGER;
    protected static QueueConfigurations queueConfigurations;
    private JSONObject queueJson;
    private boolean queueConfigurationNeeded;
    private List<String> enabledQueues;
    private final JSONObject modulesByProduct;
    private List<String> defaultModules;
    
    public QueueConfigurations() {
        this.queueConfigurationNeeded = false;
        this.enabledQueues = new ArrayList<String>();
        this.defaultModules = new ArrayList<String>();
        this.queueJson = this.loadQueueJson();
        this.modulesByProduct = this.loadProductModulesJSON();
        this.addDefaultQueues();
    }
    
    private void addDefaultQueues() {
        try {
            for (final String module : this.defaultModules) {
                final JSONArray jsonArray = this.queueJson.getJSONArray(module);
                for (int index = 0; index < jsonArray.length(); ++index) {
                    this.enabledQueues.add(String.valueOf(jsonArray.get(index)));
                }
            }
            QueueConfigurations.LOGGER.log(Level.FINE, "added default queues: {0}", new Object[] { this.enabledQueues });
        }
        catch (final Exception ex) {
            QueueConfigurations.LOGGER.log(Level.WARNING, "Exception in addDefaultQueues() ", ex);
        }
    }
    
    public static QueueConfigurations getInstance() {
        if (QueueConfigurations.queueConfigurations == null) {
            QueueConfigurations.queueConfigurations = new QueueConfigurations();
        }
        return QueueConfigurations.queueConfigurations;
    }
    
    private JSONObject loadProductModulesJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            final File propertyFile = new File(PropertyUtils.order_file);
            if (!propertyFile.exists()) {
                QueueConfigurations.LOGGER.log(Level.SEVERE, "File does not exist: {0}", propertyFile.getName());
                return jsonObject;
            }
            final Properties fileOrders = FileUtils.readPropertyFile(propertyFile);
            final String confFile = fileOrders.getProperty("product.module.mapping.file");
            QueueConfigurations.LOGGER.log(Level.FINE, "Absolute path of property file order: {0}", confFile);
            final String confFilePath = PropertyUtils.serverPath + File.separator + confFile;
            final File jsonFilePath = new File(confFilePath);
            jsonObject = JsonUtils.loadJsonFile(jsonFilePath);
        }
        catch (final Exception e) {
            QueueConfigurations.LOGGER.log(Level.SEVERE, "exception raised in loading product-module-mapping json", e);
        }
        return jsonObject;
    }
    
    public JSONObject loadQueueJson() {
        final JSONObject jsonObject = new JSONObject();
        final String key = "queue.config.loader.order.";
        try {
            final File propertyFile = new File(PropertyUtils.order_file);
            if (!propertyFile.exists()) {
                QueueConfigurations.LOGGER.log(Level.SEVERE, "File does not exist: {0}", propertyFile.getName());
                return jsonObject;
            }
            final Properties order = FileUtils.readPropertyFile(propertyFile);
            final List<String> orderedFileList = PropertyUtils.loadPropertiesBasedOnKey(order, key);
            QueueConfigurations.LOGGER.log(Level.FINE, "ordered File list of queue json: {0}", orderedFileList);
            for (final String confFile : orderedFileList) {
                final String confFilePath = PropertyUtils.serverPath + File.separator + confFile;
                QueueConfigurations.LOGGER.log(Level.FINE, "queue conf file absolute path: {0}", confFilePath);
                final File file = new File(confFilePath);
                final JSONObject tempJson = JsonUtils.loadJsonFile(file);
                if (tempJson != null) {
                    final Iterator<String> keys = tempJson.keys();
                    while (keys.hasNext()) {
                        final String k = keys.next();
                        jsonObject.put(k, tempJson.get(k));
                        if (confFile.contains("framework")) {
                            this.defaultModules.add(k);
                        }
                    }
                }
                QueueConfigurations.LOGGER.log(Level.FINE, "conf file path: {0}, loaded json: {1} ", new Object[] { confFile, tempJson });
            }
        }
        catch (final Exception e) {
            QueueConfigurations.LOGGER.log(Level.SEVERE, "exception in loading queue json", e);
        }
        return jsonObject;
    }
    
    public void checkQueueConfigurationNeeded() {
        try {
            final boolean contextBasedQueues = Boolean.parseBoolean(FrameworkConfigurations.getSpecificPropertyIfExists("queue_process", "context_based_queues", (Object)"false").toString());
            this.queueConfigurationNeeded = (contextBasedQueues && this.isApplicable());
            QueueConfigurations.LOGGER.log(Level.INFO, "Is queue configuration needed?: {0}", this.queueConfigurationNeeded);
        }
        catch (final Exception ex) {
            QueueConfigurations.LOGGER.log(Level.SEVERE, "Exception in setQueueConfigurations: ", ex);
        }
    }
    
    public JSONArray findAllowedModules() {
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        JSONArray modulesArray = new JSONArray();
        try {
            QueueConfigurations.LOGGER.log(Level.FINE, "modules by product: {0} | product code: {1} | is present: {2}", new Object[] { this.modulesByProduct, productCode, this.modulesByProduct.has(productCode) });
            if (this.modulesByProduct.has(productCode)) {
                modulesArray = this.modulesByProduct.getJSONArray(productCode);
            }
            QueueConfigurations.LOGGER.log(Level.FINE, "product code: {0} | modules array: {1}", new Object[] { productCode, modulesArray });
        }
        catch (final Exception ex) {
            QueueConfigurations.LOGGER.log(Level.WARNING, "Exception in findAllowedModules() ", ex);
        }
        return modulesArray;
    }
    
    public List<String> fetchApplicableQueues() {
        final JSONArray allowedModules = this.findAllowedModules();
        final List<String> enabledQueues = new ArrayList<String>();
        if (allowedModules.length() == 0) {
            QueueConfigurations.LOGGER.log(Level.SEVERE, "No modules available in the current applied license");
            return new ArrayList<String>();
        }
        QueueConfigurations.LOGGER.log(Level.INFO, "available modules in the current license: {0}", new Object[] { allowedModules });
        try {
            if (CustomerInfoUtil.isSecurityAddOnApplied()) {
                final JSONArray secAddOnModules = this.modulesByProduct.getJSONArray("SECURITY_ADD_ON");
                for (int index = 0; index < secAddOnModules.length(); ++index) {
                    final String module = String.valueOf(secAddOnModules.get(index));
                    final JSONArray secQueues = this.queueJson.getJSONArray(module);
                    for (int subIndex = 0; subIndex < secQueues.length(); ++subIndex) {
                        this.addEnabledQueue(String.valueOf(secQueues.get(subIndex)));
                    }
                }
            }
            for (int moduleIndex = 0; moduleIndex < allowedModules.length(); ++moduleIndex) {
                final String module2 = allowedModules.getString(moduleIndex);
                if (this.isModulePresent(module2)) {
                    QueueConfigurations.LOGGER.log(Level.FINE, "current processing module: {0}", new Object[] { module2 });
                    final JSONArray jsonArray = this.getQueueArray(module2);
                    for (int index2 = 0; index2 < jsonArray.length(); ++index2) {
                        final String queueName = String.valueOf(jsonArray.get(index2));
                        enabledQueues.add(queueName);
                    }
                }
            }
            QueueConfigurations.LOGGER.log(Level.INFO, "current queues to be enabled:{0} ", new Object[] { enabledQueues });
        }
        catch (final Exception ex) {
            QueueConfigurations.LOGGER.log(Level.SEVERE, "Exception in setApplicableQueues: ", ex);
        }
        return enabledQueues;
    }
    
    public boolean isQueueConfigurationNeeded() {
        return this.queueConfigurationNeeded;
    }
    
    private boolean isApplicable() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        return licenseType.equalsIgnoreCase("R") && EMSProductUtil.isEMSFlowSupportedForCurrentProduct();
    }
    
    public void addEnabledQueue(final String queue) {
        this.enabledQueues.add(queue);
    }
    
    public void addEnabledQueue(final List<String> enabledQueues) {
        this.enabledQueues.addAll(enabledQueues);
    }
    
    public List getEnabledQueues() {
        return this.enabledQueues;
    }
    
    public JSONArray getQueueArray(final String moduleName) {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = this.queueJson.getJSONArray(moduleName);
        }
        catch (final Exception ex) {
            QueueConfigurations.LOGGER.log(Level.WARNING, "Exception in getQueueArray ", ex);
        }
        return jsonArray;
    }
    
    public boolean isModulePresent(final String moduleName) {
        return this.queueJson.has(moduleName);
    }
    
    public boolean isQueueEnabled(final String queueName) {
        return this.enabledQueues.contains(queueName);
    }
    
    static {
        LOGGER = Logger.getLogger("DCQueueLogger");
        QueueConfigurations.queueConfigurations = null;
    }
}
