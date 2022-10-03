package com.me.devicemanagement.framework.utils;

import java.util.List;
import java.util.Properties;
import com.zoho.framework.utils.FileUtils;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.FileWriter;
import org.json.JSONArray;
import java.util.Iterator;
import org.json.JSONTokener;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;

public class JsonUtils
{
    protected static Logger log;
    
    public static JSONObject loadJsonFiles(final String directoryPath) throws JSONException {
        return loadJsonFile(directoryPath, null);
    }
    
    public static JSONObject loadJsonFile(final String filePath, final String file_pattern) throws JSONException {
        final File frameworkConfigurationFile = new File(filePath);
        JSONObject jsonObject = new JSONObject();
        if (frameworkConfigurationFile.exists()) {
            final File file = new File(filePath);
            if (file.isDirectory()) {
                final File[] files = file.listFiles();
                if (file_pattern != null) {
                    final JSONObject orderedFiles = loadFilesAccordingToPattern(files, file_pattern);
                    jsonObject = loadFilesInOrder(orderedFiles, jsonObject);
                }
                else {
                    for (final File file2 : files) {
                        final JSONObject merge = loadJsonFile(file2);
                        jsonObject = mergeJSON(jsonObject, merge);
                    }
                }
            }
            else {
                jsonObject = loadJsonFile(file);
            }
        }
        return jsonObject;
    }
    
    public static JSONObject loadFilesAccordingToPattern(final File[] files, String file_pattern) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        Integer i = 0;
        final Pattern pattern = Pattern.compile("_([\\d])+");
        for (final File confFile : files) {
            final String fileName = confFile.getName();
            file_pattern = file_pattern.replace("\\\\", "\\");
            if (file_pattern != null) {
                if (fileName.matches(file_pattern)) {
                    final Matcher matcher = pattern.matcher(fileName);
                    String match = null;
                    while (matcher.find()) {
                        match = matcher.group();
                    }
                    if (match != null) {
                        match = match.substring(1, match.length());
                        jsonObject.put(match, (Object)confFile.getAbsolutePath());
                    }
                    else {
                        jsonObject.put("0", (Object)confFile.getAbsolutePath());
                    }
                }
            }
            else {
                jsonObject.put(i.toString(), (Object)confFile.getAbsolutePath());
                ++i;
            }
        }
        return jsonObject;
    }
    
    public static JSONObject loadJsonFile(final File file) throws JSONException {
        String jsonTxt = null;
        JSONObject jsonObject = null;
        InputStream is = null;
        if (file.exists()) {
            try {
                JsonUtils.log.log(Level.INFO, "Going to Load json configurations");
                is = new FileInputStream(file);
                jsonTxt = IOUtils.toString(is, "UTF-8");
                jsonObject = new JSONObject(jsonTxt);
            }
            catch (final IOException ioe) {
                JsonUtils.log.log(Level.INFO, "Exception while loading " + file.getName());
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (final Exception e) {
                    JsonUtils.log.log(Level.WARNING, "unable to close the stream");
                }
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (final Exception e2) {
                    JsonUtils.log.log(Level.WARNING, "unable to close the stream");
                }
            }
        }
        return jsonObject;
    }
    
    public static Object loadJsonFileWithArray(final File file) throws JSONException {
        InputStream is = null;
        if (file.exists()) {
            try {
                JsonUtils.log.log(Level.INFO, "Going to Load json file configurations");
                is = new FileInputStream(file);
                final String jsonTxt = IOUtils.toString(is, "UTF-8");
                return new JSONTokener(jsonTxt).nextValue();
            }
            catch (final IOException ioe) {
                JsonUtils.log.log(Level.INFO, "Exception while loading " + file.getName());
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (final Exception e) {
                    JsonUtils.log.log(Level.WARNING, "unable to close the stream");
                }
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (final Exception e2) {
                    JsonUtils.log.log(Level.WARNING, "unable to close the stream");
                }
            }
        }
        return null;
    }
    
    public static JSONObject loadFilesInOrder(final JSONObject orderedFileList) throws JSONException {
        return loadFilesInOrder(orderedFileList, null);
    }
    
    public static JSONObject loadFilesInOrder(final JSONObject orderedFileList, JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        Integer j = 0;
        int i = 0;
        while (i < orderedFileList.length()) {
            boolean flag = false;
            do {
                if (orderedFileList.has(j.toString())) {
                    final String conf_file = orderedFileList.get(j.toString()).toString();
                    final File file = new File(conf_file);
                    final JSONObject tempJson = loadJsonFile(file);
                    mergeJSON(jsonObject, tempJson);
                    ++i;
                    flag = true;
                }
                ++j;
            } while (!flag);
        }
        return jsonObject;
    }
    
    public static JSONObject mergeJSON(final JSONObject base, final JSONObject merge) throws JSONException {
        final Iterator itr = merge.keys();
        while (itr.hasNext()) {
            final String key = itr.next().toString();
            if (base.has(key) && base.get(key) instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject)merge.get(key);
                final Iterator iter = jsonObject.keys();
                while (iter.hasNext()) {
                    final String keyValue = iter.next().toString();
                    base.getJSONObject(key).put(keyValue, merge.getJSONObject(key).get(keyValue));
                }
            }
            else {
                base.put(key, merge.get(key));
            }
        }
        return base;
    }
    
    public static void mergeJSONWithArray(final JSONObject base, final Object object) throws JSONException {
        if (object instanceof JSONArray) {
            final JSONArray jsonArray = (JSONArray)object;
            for (int i = 0; i < jsonArray.length(); ++i) {
                mergeJSON(base, jsonArray.getJSONObject(i));
            }
        }
        else if (object instanceof JSONObject) {
            mergeJSON(base, (JSONObject)object);
        }
    }
    
    public int writeJsonObjectIntoJsonFile(final JSONObject jsonObject, final String fileName) throws Exception {
        FileWriter file = null;
        try {
            file = new FileWriter(fileName);
            file.write(jsonObject.toString());
        }
        catch (final Exception e) {
            JsonUtils.log.log(Level.INFO, "Exception while storing the redact type" + e);
            throw e;
        }
        finally {
            file.close();
        }
        return 3001;
    }
    
    public static JSONObject createJson(final Hashtable data) throws JSONException {
        final Enumeration keys = data.keys();
        final JSONObject jsonObject = new JSONObject();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            jsonObject.put(key.toString(), (Object)data.get(key).toString());
        }
        return jsonObject;
    }
    
    public static JSONObject loadJsonFromOrderProperties(String orderPropertiesFilePath, final String orderKey, final String jsonFilePathPrefix, JSONObject jsonObject) throws IOException, JSONException {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        if (orderKey == null) {
            return jsonObject;
        }
        if (orderPropertiesFilePath == null) {
            orderPropertiesFilePath = PropertyUtils.order_file;
        }
        final File propertyFile = new File(orderPropertiesFilePath);
        if (propertyFile.exists()) {
            final Properties order = FileUtils.readPropertyFile(propertyFile);
            final List<String> orderedFileList = PropertyUtils.loadPropertiesBasedOnKey(order, orderKey);
            for (String jsonFilePath : orderedFileList) {
                if (jsonFilePathPrefix != null) {
                    jsonFilePath = jsonFilePathPrefix + jsonFilePath;
                }
                final File file = new File(jsonFilePath);
                final JSONObject tempJson = loadJsonFile(file);
                if (tempJson != null) {
                    FrameworkConfigurations.checkAndLoadJsonFromFilePath(tempJson);
                    mergeJSON(jsonObject, tempJson);
                }
            }
        }
        else {
            JsonUtils.log.log(Level.SEVERE, "{0} does not exist...", propertyFile.getName());
        }
        return jsonObject;
    }
    
    static {
        JsonUtils.log = Logger.getLogger(JsonUtils.class.getName());
    }
}
