package com.adventnet.persistence.migration;

import java.util.Hashtable;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import com.zoho.conf.Configuration;
import java.util.Properties;
import com.adventnet.db.adapter.DBAdapter;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.persistence.ConfigurationParser;
import java.util.List;
import java.util.logging.Logger;

public class DefaultConfigurationUpdater implements ConfigurationUpdater
{
    private static Logger logger;
    private List<String> filesInPPM;
    private ConfigChangeListener configChangeListener;
    private ConfigurationParser cp;
    private static String server_home;
    
    private void loadConfigurationParser(final String confDir, final String backupConfDir) throws Exception {
        String fileToBeLoaded = backupConfDir + File.separator + "Persistence" + File.separator + "persistence-configurations.xml";
        if (!new File(fileToBeLoaded).exists()) {
            fileToBeLoaded = confDir + File.separator + "Persistence" + File.separator + "persistence-configurations.xml";
        }
        this.cp = new ConfigurationParser(fileToBeLoaded);
    }
    
    @Override
    public void applyDBParamsChange(final String confDir, final String backupConfDir, final String listenerClass, final boolean overwrite, final List<String> filesInPPM) throws Exception {
        this.filesInPPM = filesInPPM;
        if (this.cp == null) {
            this.loadConfigurationParser(confDir, backupConfDir);
        }
        final String[] dbs = { "mysql", "mssql", "postgres", "firebird" };
        for (int i = 0; i < dbs.length; ++i) {
            final String dbName = dbs[i];
            final File productOldFile = new File(confDir + dbName + "_database_params_old.conf");
            final File productNewFile = new File(confDir + dbName + "_database_params_new.conf");
            final File oldParamsFile = new File(confDir + "database_params.conf");
            if (productOldFile.exists() && productNewFile.exists()) {
                final File backupFile = new File(backupConfDir + dbName + "_database_params.conf");
                if (backupFile.exists()) {
                    throw new UnsupportedOperationException("DB specific database params file should not be present in PPM.");
                }
                if (!filesInPPM.contains("conf/" + dbName + "_database_params_old.conf") || !filesInPPM.contains("conf/" + dbName + "_database_params_new.conf")) {
                    DefaultConfigurationUpdater.logger.log(Level.INFO, "Deleting product's old and new files files which were not deleted during previous installation.");
                    productOldFile.delete();
                    productNewFile.delete();
                    return;
                }
                final DBAdapter dbAdapter = this.getDBAdapter(dbName, this.cp);
                final File customerFile = new File(confDir + dbName + "_database_params.conf");
                this.copyFile(customerFile.getAbsolutePath(), backupConfDir + dbName + "_database_params.conf");
                final DBParamsChanges dbPropsChangeObj = new DBParamsChanges();
                dbPropsChangeObj.setDBName(dbName);
                dbPropsChangeObj.setProductOldFile(productOldFile);
                dbPropsChangeObj.setProductNewFile(productNewFile);
                dbPropsChangeObj.setCustomerFile(customerFile);
                final Properties mergedProps = this.getMergedDBProps(dbPropsChangeObj, dbAdapter);
                dbPropsChangeObj.setMergedProps(mergedProps);
                if (this.configChangeListener == null) {
                    this.configChangeListener = (ConfigChangeListener)Thread.currentThread().getContextClassLoader().loadClass(listenerClass).newInstance();
                }
                if (this.configChangeListener.handleDBPropChanges(dbPropsChangeObj)) {
                    this.writeDBParamsFile(dbPropsChangeObj.getCustomerFile(), dbPropsChangeObj.getMergedProps());
                }
                productOldFile.delete();
                productNewFile.delete();
            }
            else if (oldParamsFile.exists()) {
                final String db = this.cp.getConfigurationValue("DBName");
                final String dbSpecificParamsFilePath = confDir + db + "_database_params.conf";
                final File dbSpecificParamsFile = new File(dbSpecificParamsFilePath);
                if (dbSpecificParamsFile.exists()) {
                    this.copyFile(oldParamsFile.getCanonicalPath(), dbSpecificParamsFilePath);
                    this.copyFile(oldParamsFile.getCanonicalPath(), backupConfDir + "database_params.conf");
                    final File oldDBParamsHandleFile = new File(backupConfDir + "olddbparams.handled");
                    oldDBParamsHandleFile.createNewFile();
                    oldParamsFile.delete();
                }
            }
        }
    }
    
    @Override
    public void applyPersistenceConfChanges(final String confDir, final String backupConfDir, final String listenerClass) throws Exception {
        if (this.cp == null) {
            this.loadConfigurationParser(confDir, backupConfDir);
        }
        final File backupPersConf = new File(backupConfDir + File.separator + "Persistence" + File.separator + "persistence-configurations.xml");
        if (!backupPersConf.exists()) {
            return;
        }
        final String defaultPath = Configuration.getString("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
        final File customerConfig = new File(defaultPath);
        if (customerConfig.exists()) {
            DefaultConfigurationUpdater.logger.log(Level.INFO, "Customer config file exists, hence creation is skipped");
            return;
        }
        DefaultConfigurationUpdater.logger.log(Level.INFO, "Creating customer specific config file using values from framework's conf file in the backup folder");
        final File customerConfigHandleFile = new File(backupConfDir + "customerconfig.handled");
        customerConfigHandleFile.createNewFile();
        final HashMap<String, String> nameVsValue = new HashMap<String, String>();
        nameVsValue.put("DBName", this.cp.getConfigurationValue("DBName"));
        nameVsValue.put("DSAdapter", this.cp.getConfigurationValue("DSAdapter"));
        nameVsValue.put("StartDBServer", this.cp.getConfigurationValue("StartDBServer"));
        final PersistenceConfigChanges persConfChangeObj = new PersistenceConfigChanges();
        persConfChangeObj.setConfNameVsValue(nameVsValue);
        persConfChangeObj.setConfNameVsProps(new HashMap());
        persConfChangeObj.setConfNameVsList(new HashMap());
        persConfChangeObj.setConfigToBeReplaced(new ArrayList());
        persConfChangeObj.setExtendedConfigXml(customerConfig);
        if (this.configChangeListener == null) {
            this.configChangeListener = (ConfigChangeListener)Thread.currentThread().getContextClassLoader().loadClass(listenerClass).newInstance();
        }
        if (this.configChangeListener.handlePersConfChanges(persConfChangeObj)) {
            ConfigurationParser.writeExtendedPersistenceConfFile(persConfChangeObj.getConfNameVsValue(), persConfChangeObj.getConfNameVsProps(), persConfChangeObj.getConfNameVsList(), persConfChangeObj.getConfigToBeReplaced());
        }
    }
    
    @Override
    public void revertDBParamsChanges(final String backUpDir) throws Exception {
        final File oldDBParamsCheckFile = new File(backUpDir + File.separator + "conf" + File.separator + "olddbparams.handled");
        if (oldDBParamsCheckFile.exists()) {
            DefaultConfigurationUpdater.logger.log(Level.INFO, "Old DB params check file exists hence restoring the old DB params file from backup");
            this.copyFile(backUpDir + File.separator + "conf" + File.separator + "database_params.conf", DefaultConfigurationUpdater.server_home + File.separator + "conf" + File.separator + "database_params.conf");
            oldDBParamsCheckFile.delete();
        }
        final String[] dbs = { "mysql", "mssql", "postgres", "firebird" };
        for (int i = 0; i < dbs.length; ++i) {
            final String dbName = dbs[i];
            final File dbParamsBackup = new File(backUpDir + File.separator + "conf" + File.separator + dbName + "_database_params.conf");
            if (dbParamsBackup.exists()) {
                DefaultConfigurationUpdater.logger.log(Level.INFO, "{0}_database_params.conf backup file exists, restoring it", dbName);
                final File restoredFile = new File(DefaultConfigurationUpdater.server_home + File.separator + "conf" + File.separator + dbName + "_database_params.conf");
                restoredFile.delete();
                this.copyFile(dbParamsBackup.getCanonicalPath(), restoredFile.getCanonicalPath());
                dbParamsBackup.delete();
                if (restoredFile.exists()) {
                    DefaultConfigurationUpdater.logger.log(Level.INFO, "{0}_database_params.conf file restored successfully", dbName);
                }
            }
        }
    }
    
    @Override
    public void revertPersistenceConfChanges(final String backUpDir) {
        final File customerConfigHandleFile = new File(backUpDir + File.separator + "conf" + File.separator + "customerconfig.handled");
        if (customerConfigHandleFile.exists()) {
            DefaultConfigurationUpdater.logger.log(Level.INFO, "Customer config check file exists hence deleting customer config xml file");
            final File customerConfig = new File(System.getProperty("customer.config"));
            customerConfig.delete();
        }
    }
    
    protected Properties getMergedDBProps(final DBParamsChanges dbPropsChangeObject, final DBAdapter dbAdapter) throws FileNotFoundException, IOException {
        final boolean overwrite = dbPropsChangeObject.getPropsChangePreference();
        DefaultConfigurationUpdater.logger.log(Level.INFO, "DB params overwrite property set to {0}", overwrite);
        final File oldFile = dbPropsChangeObject.getProductOldFile();
        final File newFile = dbPropsChangeObject.getProductNewFile();
        final File customerFile = dbPropsChangeObject.getCustomerFile();
        final Properties oldProps = new Properties();
        final Properties newProps = new Properties();
        oldProps.load(new FileInputStream(oldFile));
        newProps.load(new FileInputStream(newFile));
        final Properties customerProps = new Properties();
        customerProps.load(new FileInputStream(customerFile));
        final Properties mergedProps = new Properties();
        mergedProps.putAll(customerProps);
        final String driverName = "drivername";
        if (!((Hashtable<K, Object>)oldProps).get(driverName).equals(((Hashtable<K, Object>)customerProps).get(driverName))) {
            throw new UnsupportedOperationException("Drivername in the customer setup is different, hence PPM cannot be applied.");
        }
        oldProps.remove(driverName);
        newProps.remove(driverName);
        final List paramsDiff = this.getPropsDiff(oldProps, (Properties)newProps.clone());
        for (final Map diff : paramsDiff) {
            final String key = diff.get("key");
            if (key.equals("url")) {
                final Map customerUrlMap = dbAdapter.splitConnectionURL(((Hashtable<K, String>)customerProps).get("url"));
                final Map productOldUrlMap = dbAdapter.splitConnectionURL(oldProps.getProperty("url"));
                final Map productNewUrlMap = dbAdapter.splitConnectionURL(newProps.getProperty("url"));
                final Properties productOldUrlProps = (productOldUrlMap.get("urlProps") != null) ? productOldUrlMap.get("urlProps") : new Properties();
                final Properties productNewUrlProps = (productNewUrlMap.get("urlProps") != null) ? productNewUrlMap.get("urlProps") : new Properties();
                final Properties customerUrlProps = (customerUrlMap.get("urlProps") != null) ? customerUrlMap.get("urlProps") : new Properties();
                final Properties mergedUrlProps = new Properties();
                mergedUrlProps.putAll(customerUrlProps);
                final List urlPropsDiff = this.getPropsDiff(productOldUrlProps, (Properties)productNewUrlProps.clone());
                for (final Map urlDiff : urlPropsDiff) {
                    this.mergeProps(urlDiff, customerUrlProps, mergedUrlProps, overwrite);
                }
                final String url = customerUrlMap.get("urlWithoutProps");
                final StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(url);
                final Set s = mergedUrlProps.entrySet();
                final Iterator setIterator = s.iterator();
                if (setIterator.hasNext()) {
                    urlBuilder.append(customerUrlMap.get("jdbcurl_props_separator"));
                }
                while (setIterator.hasNext()) {
                    final Map.Entry e = setIterator.next();
                    final String k = e.getKey();
                    final String v = e.getValue();
                    urlBuilder.append(k + "=" + v);
                    if (setIterator.hasNext()) {
                        urlBuilder.append(customerUrlMap.get("url_props_delimiter"));
                    }
                }
                ((Hashtable<String, String>)mergedProps).put(key, urlBuilder.toString());
            }
            else {
                if (key.equals("username")) {
                    continue;
                }
                if (key.equals("password")) {
                    continue;
                }
                this.mergeProps(diff, customerProps, mergedProps, overwrite);
            }
        }
        return mergedProps;
    }
    
    protected DBAdapter getDBAdapter(final String dbType, final ConfigurationParser cp) {
        final Properties props = cp.getConfigurationProps(dbType);
        final String dbAdapterClass = props.getProperty("dbadapter");
        DBAdapter dbAdapter = null;
        try {
            dbAdapter = (DBAdapter)Thread.currentThread().getContextClassLoader().loadClass(dbAdapterClass).newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dbAdapter;
    }
    
    private List getPropsDiff(final Properties oldProps, final Properties newProps) throws FileNotFoundException, IOException {
        final List mergedPropsMap = new ArrayList();
        Set s = oldProps.entrySet();
        Iterator i = s.iterator();
        while (i.hasNext()) {
            final Map diff = new HashMap();
            final Map.Entry e = i.next();
            final String key = e.getKey();
            final String oldValue = e.getValue();
            if (newProps.containsKey(key) && !((Hashtable<K, Object>)newProps).get(key).equals(oldValue)) {
                diff.put("key", key);
                diff.put("oldvalue", oldValue);
                diff.put("newvalue", ((Hashtable<K, Object>)newProps).get(key));
                diff.put("state", "modified");
                mergedPropsMap.add(diff);
            }
            else if (!newProps.containsKey(key)) {
                diff.put("key", key);
                diff.put("value", oldValue);
                diff.put("state", "removed");
                mergedPropsMap.add(diff);
            }
            newProps.remove(key);
        }
        if (newProps.size() > 0) {
            s = newProps.entrySet();
            i = s.iterator();
            while (i.hasNext()) {
                final Map diff = new HashMap();
                final Map.Entry e = i.next();
                final String key = e.getKey();
                final String value = e.getValue();
                diff.put("key", key);
                diff.put("value", value);
                diff.put("state", "added");
                mergedPropsMap.add(diff);
            }
        }
        return mergedPropsMap;
    }
    
    private void mergeProps(final Map diff, final Properties customerProps, final Properties mergedProps, final boolean overwrite) {
        final String state = diff.get("state");
        final String key = diff.get("key");
        if (state.equals("added") && (!customerProps.containsKey(key) || (!((Hashtable<K, Object>)customerProps).get(key).equals(diff.get("value")) && overwrite))) {
            ((Hashtable<String, Object>)mergedProps).put(key, diff.get("value"));
        }
        else if (state.equals("removed") && ((!diff.get("value").equals(((Hashtable<K, Object>)customerProps).get(key)) && overwrite) || diff.get("value").equals(((Hashtable<K, Object>)customerProps).get(key)))) {
            mergedProps.remove(key);
        }
        else if (state.equals("modified") && ((!customerProps.containsKey(key) && overwrite) || diff.get("oldvalue").equals(((Hashtable<K, Object>)customerProps).get(key)) || (!diff.get("oldvalue").equals(((Hashtable<K, Object>)customerProps).get(key)) && overwrite))) {
            ((Hashtable<String, Object>)mergedProps).put(key, diff.get("newvalue"));
        }
    }
    
    private void writeDBParamsFile(final File newFile, final Properties props) throws Exception {
        DefaultConfigurationUpdater.logger.log(Level.INFO, "Writing params file {0} using merged properties", newFile.getAbsoluteFile());
        final StringBuilder contents = new StringBuilder();
        final FileReader fr = new FileReader(newFile);
        final BufferedReader br = new BufferedReader(fr);
        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                contents.append(line + "\n");
            }
            else {
                if (line.trim().length() <= 0) {
                    continue;
                }
                final String[] arr = line.split("=");
                final String name = arr[0];
                final String value = (arr.length == 2) ? arr[1] : "";
                if (props.containsKey(name) && props.getProperty(name).equals(value)) {
                    contents.append(line + "\n\n");
                    props.remove(name);
                }
                else if (props.containsKey(name) && !props.getProperty(name).equals(value)) {
                    contents.append(name + "=" + props.getProperty(name) + "\n\n");
                    props.remove(name);
                }
                else {
                    if (props.containsKey(name)) {
                        continue;
                    }
                    contents.append("#" + name + "=" + value + "\n\n");
                }
            }
        }
        if (props.size() > 0) {
            final Set s = props.entrySet();
            for (final Map.Entry e : s) {
                final String name2 = e.getKey();
                final String value2 = e.getValue();
                contents.append(name2 + "=" + value2 + "\n\n");
            }
        }
        newFile.delete();
        final FileWriter fw = new FileWriter(newFile, false);
        try {
            fw.write(contents.toString());
        }
        finally {
            fw.flush();
            fw.close();
        }
    }
    
    private void copyFile(final String oldPath, final String newPath) throws Exception {
        DefaultConfigurationUpdater.logger.log(Level.INFO, "Copying contents from {0} into {1}", new String[] { oldPath, newPath });
        final StringBuilder contents = new StringBuilder();
        final File oldFile = new File(oldPath);
        final FileReader fr = new FileReader(oldFile);
        final BufferedReader br = new BufferedReader(fr);
        String line = "";
        while ((line = br.readLine()) != null) {
            contents.append(line + "\n");
        }
        final File newFile = new File(newPath);
        final File parentDir = newFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        final FileWriter fw = new FileWriter(newFile);
        try {
            fw.write(contents.toString());
            fw.flush();
            fw.close();
            if (newFile.createNewFile()) {
                DefaultConfigurationUpdater.logger.log(Level.INFO, "Contents copied successfully");
            }
        }
        finally {
            br.close();
            fr.close();
        }
    }
    
    static {
        DefaultConfigurationUpdater.logger = Logger.getLogger(ConfigurationUpdater.class.getName());
        DefaultConfigurationUpdater.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
