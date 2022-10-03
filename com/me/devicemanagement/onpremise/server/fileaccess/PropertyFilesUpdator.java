package com.me.devicemanagement.onpremise.server.fileaccess;

import java.util.Hashtable;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.IOException;
import java.util.Iterator;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyFilesUpdator
{
    private final Logger logger;
    public static Properties propsCopied;
    public static Map keyFileMap;
    public static PropertyFilesUpdator propertyFilesUpdator;
    
    PropertyFilesUpdator() {
        (this.logger = Logger.getLogger(PropertyFilesUpdator.class.getName())).log(Level.INFO, "Inside constructor of PropertyFilesUpdator");
    }
    
    public static PropertyFilesUpdator getInstance() {
        if (PropertyFilesUpdator.propertyFilesUpdator == null) {
            PropertyFilesUpdator.propertyFilesUpdator = new PropertyFilesUpdator();
        }
        return PropertyFilesUpdator.propertyFilesUpdator;
    }
    
    private void addFileKeyPairs(final Map map) {
        PropertyFilesUpdator.keyFileMap.putAll(map);
    }
    
    public static void resetPropsSpecified() throws IOException {
        for (final Map.Entry mapEntry : PropertyFilesUpdator.keyFileMap.entrySet()) {
            final String file = mapEntry.getKey();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            final ArrayList listOfKeys = mapEntry.getValue();
            String oldtext = "";
            String line = "";
            final Properties properties = ((Hashtable<K, Properties>)PropertyFilesUpdator.propsCopied).get(file);
            while ((line = bufferedReader.readLine()) != null) {
                for (final String key : listOfKeys) {
                    if (line.startsWith(key + "=")) {
                        line = key + "=" + properties.getProperty(key);
                    }
                }
                oldtext = oldtext + line + "\r\n";
            }
            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(oldtext);
            fileWriter.close();
        }
    }
    
    private Properties copyPropsSpecified() {
        for (final Map.Entry mapEntry : PropertyFilesUpdator.keyFileMap.entrySet()) {
            final String path = mapEntry.getKey();
            Properties properties = null;
            try {
                properties = FileAccessUtil.readProperties(path);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Unable to read  notification server properties");
            }
            final ArrayList listOfKeys = mapEntry.getValue();
            final Iterator iter = listOfKeys.iterator();
            final Properties props = new Properties();
            while (iter.hasNext()) {
                final String key = iter.next();
                ((Hashtable<String, String>)props).put(key, properties.getProperty(String.valueOf(key)));
            }
            if (PropertyFilesUpdator.propsCopied.containsKey(path)) {
                final Properties property = ((Hashtable<K, Properties>)PropertyFilesUpdator.propsCopied).get(path);
                property.putAll(props);
                ((Hashtable<String, Properties>)PropertyFilesUpdator.propsCopied).put(path, property);
            }
            else {
                ((Hashtable<String, Properties>)PropertyFilesUpdator.propsCopied).put(path, props);
            }
        }
        return PropertyFilesUpdator.propsCopied;
    }
    
    public Properties copyPropsSpecifiedInMap(final Map map) {
        this.addFileKeyPairs(map);
        return this.copyPropsSpecified();
    }
    
    static {
        PropertyFilesUpdator.propsCopied = new Properties();
        PropertyFilesUpdator.keyFileMap = new HashMap();
        PropertyFilesUpdator.propertyFilesUpdator = null;
    }
}
