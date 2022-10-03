package com.me.devicemanagement.framework.server.logger;

import java.util.Hashtable;
import java.util.Collection;
import java.util.Arrays;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Enumeration;
import java.util.Iterator;
import org.json.JSONException;
import java.util.Properties;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashSet;

public class DMSecurityLogger
{
    private static HashSet sensitiveKeyHash;
    private static String sensitiveDataFilePath;
    private static final Logger LOGGER;
    
    private DMSecurityLogger() {
    }
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object params) {
        try {
            final Object logData = encryptSensitiveData(params);
            logger.logp(Level.INFO, sourceClass, sourceMethod, msg, logData);
        }
        catch (final Exception ex) {
            DMSecurityLogger.LOGGER.log(Level.WARNING, "Exception while printing data...", ex);
        }
    }
    
    private static Object encryptSensitiveData(final Object object) throws JSONException {
        Object returnObject = null;
        if (object instanceof JSONObject) {
            final JSONObject logJSONObject = new JSONObject(object.toString());
            returnObject = parseJSON(logJSONObject);
        }
        else if (object instanceof Properties) {
            final Properties logProperties = new Properties((Properties)object);
            returnObject = parseProperties(logProperties);
        }
        else if (object instanceof String) {
            returnObject = parseString(object.toString());
        }
        return returnObject;
    }
    
    private static JSONObject parseJSON(final JSONObject jSONObject) {
        final Iterator iterator = jSONObject.keys();
        String key = null;
        Object value = null;
        while (iterator.hasNext()) {
            try {
                key = iterator.next();
                value = jSONObject.get(key);
                if (value instanceof JSONObject) {
                    parseJSON((JSONObject)value);
                }
                else {
                    if (!contains(key, DMSecurityLogger.sensitiveKeyHash)) {
                        continue;
                    }
                    final int length = value.toString().length();
                    if (length > 10000) {
                        DMSecurityLogger.LOGGER.log(Level.INFO, "Value is too large to parse");
                        return null;
                    }
                    final char[] jsonCharArray = new char[length];
                    final String jsonValue = new String(jsonCharArray);
                    jSONObject.put(key, (Object)jsonValue.replace('\0', '*'));
                }
            }
            catch (final JSONException ex) {
                DMSecurityLogger.LOGGER.log(Level.SEVERE, "JSONException while parsing JSON object...", (Throwable)ex);
            }
        }
        return jSONObject;
    }
    
    private static Properties parseProperties(final Properties properties) {
        final Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            final String key = (String)enumeration.nextElement();
            if (contains(key, DMSecurityLogger.sensitiveKeyHash)) {
                final String value = properties.getProperty(key);
                final int length = value.length();
                if (length > 10000) {
                    DMSecurityLogger.LOGGER.log(Level.INFO, "Value is too large to parse");
                    return null;
                }
                final char[] propCharArray = new char[length];
                final String propValue = new String(propCharArray);
                ((Hashtable<String, String>)properties).put(key, propValue.replace('\0', '*'));
            }
        }
        return properties;
    }
    
    private static DataObject parseDataObject(final DataObject dataObject) {
        try {
            final List tableNames = dataObject.getTableNames();
            for (final Object tName : tableNames) {
                final Iterator iterator = dataObject.getRows(tName.toString());
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final List columnList = row.getColumns();
                    for (final Object columnName : columnList) {
                        if (contains(columnName.toString(), DMSecurityLogger.sensitiveKeyHash)) {
                            final String columnValue = (String)row.get(columnName.toString());
                            final int length = columnValue.length();
                            if (length > 10000) {
                                DMSecurityLogger.LOGGER.log(Level.INFO, "Value is too large to parse");
                                return null;
                            }
                            final char[] dataObjectCharArray = new char[length];
                            final String dataObjectValue = new String(dataObjectCharArray);
                            row.set(columnName.toString(), (Object)dataObjectValue.replace('\0', '*'));
                            dataObject.updateRow(row);
                        }
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            DMSecurityLogger.LOGGER.log(Level.SEVERE, "DataAccessException while parsing Data Object...", (Throwable)ex);
        }
        return dataObject;
    }
    
    private static String parseString(String string) {
        if (DMSecurityLogger.sensitiveKeyHash != null) {
            for (final String str : DMSecurityLogger.sensitiveKeyHash) {
                if (string != null && !string.trim().equals("") && string.toLowerCase().contains(str.toLowerCase())) {
                    string = restrictPasswordEntry(string, str);
                }
            }
        }
        return string;
    }
    
    private static boolean contains(final String key, final HashSet hashSet) {
        boolean containsStatus = false;
        if (hashSet.contains(key)) {
            containsStatus = true;
        }
        else {
            final Iterator itr = hashSet.iterator();
            String value = "";
            while (itr.hasNext()) {
                value = itr.next().toString();
                if (key.toLowerCase().contains(value)) {
                    containsStatus = true;
                    break;
                }
            }
        }
        return containsStatus;
    }
    
    public static String restrictPasswordEntry(String message, final String str) {
        final String strPrePattern = "(?i)(?:\")*(?:[a-zA-Z0-9_])*";
        final String strMiddlePattern = "[\\W_]*[:=-](?:\\s)*(?:\\S)[^, &\"\\n]*|";
        final String strEndPattern = ">(.+?)</";
        message = message.replaceAll(strPrePattern + str + strMiddlePattern + str + strEndPattern + str, str + "-***********");
        return message;
    }
    
    static {
        DMSecurityLogger.sensitiveKeyHash = new HashSet();
        DMSecurityLogger.sensitiveDataFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "sensitiveLogKeyStorage.properties";
        LOGGER = Logger.getLogger("DMSecurityLogger");
        try {
            if (new File(DMSecurityLogger.sensitiveDataFilePath).exists()) {
                final Properties sensitiveDataList = FileUtils.readPropertyFile(new File(DMSecurityLogger.sensitiveDataFilePath));
                final String sensitiveData = sensitiveDataList.getProperty("sensitive_key");
                if (!"".equals(sensitiveData)) {
                    DMSecurityLogger.sensitiveKeyHash = new HashSet((Collection<? extends E>)Arrays.asList(sensitiveData.replaceAll(" ", "").split(",")));
                }
            }
        }
        catch (final Exception ex) {
            DMSecurityLogger.LOGGER.log(Level.WARNING, "Exception get the sensitive data from property file", ex);
        }
    }
}
