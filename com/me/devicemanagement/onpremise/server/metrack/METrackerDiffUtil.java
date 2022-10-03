package com.me.devicemanagement.onpremise.server.metrack;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import java.util.Enumeration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class METrackerDiffUtil
{
    private static Logger logger;
    private static String sourceClass;
    private static METrackerDiffUtil meTrackerDiffUtil;
    
    public static METrackerDiffUtil getInstance() {
        if (METrackerDiffUtil.meTrackerDiffUtil == null) {
            METrackerDiffUtil.meTrackerDiffUtil = new METrackerDiffUtil();
        }
        return METrackerDiffUtil.meTrackerDiffUtil;
    }
    
    public Properties getTrackerDiff(final String formName, final Properties currentProperties) {
        try {
            final String existsStringFromFile = this.getExistsStringFromFile(formName);
            if (existsStringFromFile != null && !existsStringFromFile.equals("") && this.isJson(existsStringFromFile)) {
                return this.getDiffProperties(new JSONObject(existsStringFromFile), currentProperties);
            }
        }
        catch (final JSONException je) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getTrackerDiff", "JSONException occurred : ", (Throwable)je);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getTrackerDiff", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    private Properties getDiffProperties(final JSONObject existsJson, final Properties currentProperties) {
        final Properties diffProperties = new Properties();
        try {
            final Set<Object> zcColumnNames = ((Hashtable<Object, V>)currentProperties).keySet();
            for (final Object zcColumnName : zcColumnNames) {
                String currentValue = ((Hashtable<K, Object>)currentProperties).get(zcColumnName).toString();
                if (existsJson.has(zcColumnName.toString())) {
                    String existsValue = existsJson.get(zcColumnName.toString()).toString();
                    if (this.isJson(currentValue) && this.isJson(existsValue)) {
                        currentValue = new JSONObject(currentValue).toString();
                        existsValue = new JSONObject(existsValue).toString();
                    }
                    if (existsValue.equals(currentValue)) {
                        continue;
                    }
                    ((Hashtable<Object, String>)diffProperties).put(zcColumnName, currentValue);
                }
                else {
                    ((Hashtable<Object, String>)diffProperties).put(zcColumnName, currentValue);
                }
            }
            return diffProperties;
        }
        catch (final JSONException je) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getDiffProperties", "JSONException occurred : ", (Throwable)je);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getDiffProperties", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    private boolean isJson(final String value) {
        try {
            final JSONObject json = new JSONObject(value);
            return true;
        }
        catch (final JSONException e) {
            return false;
        }
    }
    
    private String getExistsStringFromFile(final String formName) {
        try {
            final String lastSuccessfullyPostDataDir = this.getMETrackDir() + File.separator + "last_successfully_post_data.properties";
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(lastSuccessfullyPostDataDir)) {
                final Properties properties = FileAccessUtil.readProperties(lastSuccessfullyPostDataDir);
                if (properties.containsKey(formName)) {
                    return ((Hashtable<K, Object>)properties).get(formName).toString().trim();
                }
            }
        }
        catch (final FileNotFoundException fnfe) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getExistsStringFromFile", "FileNotFoundException occurred : ", (Throwable)fnfe);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getExistsStringFromFile", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    public JSONObject getZCUpdateStatus(final String xmlResponse) {
        final JSONObject jsonObject = new JSONObject();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8));
            final DocumentBuilder dBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            if (doc.getElementsByTagName("errorlist").getLength() == 0) {
                final NodeList formNodeList = doc.getElementsByTagName("form");
                for (int i = 0, len = formNodeList.getLength(); i < len; ++i) {
                    final Node formNode = formNodeList.item(i);
                    if (formNode.getNodeType() == 1) {
                        final Element formElements = (Element)formNode;
                        final String formName = formElements.getAttribute("name");
                        final Element statusElement = (Element)formElements.getLastChild();
                        final String status = statusElement.getElementsByTagName("status").item(0).getTextContent();
                        jsonObject.put(formName, (Object)status);
                    }
                }
                return jsonObject;
            }
            final Element errorNodeList = (Element)doc.getElementsByTagName("error").item(0);
            final String value = errorNodeList.getElementsByTagName("message").item(0).getTextContent() + " (" + errorNodeList.getElementsByTagName("code").item(0).getTextContent() + ")";
            jsonObject.put("error", (Object)value);
            return jsonObject;
        }
        catch (final IOException e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "Exception occurred : ", (Throwable)e);
        }
        catch (final ParserConfigurationException e2) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "ParserConfigurationException occurred : ", (Throwable)e2);
        }
        catch (final JSONException e3) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "JSONException occurred : ", (Throwable)e3);
        }
        catch (final DOMException e4) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "DOMException occurred : ", (Throwable)e4);
        }
        catch (final SAXException e5) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "SAXException occurred : ", (Throwable)e5);
        }
        catch (final Exception e6) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "Exception occurred : ", (Throwable)e6);
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (final Exception e7) {
                SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "Unable to close inputStream : ", (Throwable)e7);
            }
        }
        try {
            jsonObject.put("error", (Object)"--");
        }
        catch (final JSONException e3) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "getZCUpdateStatus", "JSONException occurred", (Throwable)e3);
        }
        return null;
    }
    
    public JSONObject updatePostSuccessValues(final JSONObject status, final Properties currentValues) {
        final JSONObject failedFormNames = new JSONObject();
        try {
            final Properties properties = new Properties();
            final Enumeration<Object> formNames = ((Hashtable<Object, V>)currentValues).keys();
            while (formNames.hasMoreElements()) {
                final String formName = formNames.nextElement();
                if ("Success".equalsIgnoreCase(status.get(formName).toString())) {
                    properties.setProperty(formName, ((Hashtable<K, Object>)currentValues).get(formName).toString());
                }
                else {
                    failedFormNames.put(formName, status.get(formName));
                }
            }
            this.updateProps("last_successfully_post_data.properties", properties);
            return failedFormNames;
        }
        catch (final JSONException je) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "updateSuccessValues", "JSONException occurred : ", (Throwable)je);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "updateSuccessValues", "Exception occurred : ", (Throwable)e);
        }
        return failedFormNames;
    }
    
    public void updatePostFailedValues(final JSONObject jsonObject) {
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("LastPostFailedStatus", jsonObject.toString());
            this.updateProps("last_post_failed_status.properties", properties);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "updatePostFailedValues", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public boolean ifDiffApplicable() {
        try {
            final String fileDir = this.getMETrackDir() + File.separator + "metrack_config.properties";
            final Properties properties = FileAccessUtil.readProperties(fileDir);
            if (properties.containsKey("LastDownloadStatus")) {
                final int lastDownloadStatus = Integer.valueOf(((Hashtable<K, Object>)properties).get("LastDownloadStatus").toString());
                if ((0 == lastDownloadStatus || 10010 == lastDownloadStatus) && properties.containsKey("METrackDiffMinBuildNo")) {
                    final Long applyBuildNum = Long.valueOf(((Hashtable<K, Object>)properties).get("METrackDiffMinBuildNo").toString());
                    final Integer buildNumFromDB = DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB();
                    if (buildNumFromDB >= applyBuildNum) {
                        return true;
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "ifDiffApplicable", "Exception occurred : ", (Throwable)e);
        }
        return false;
    }
    
    public void updateProps(final String fileName, final Properties properties) {
        try {
            final String metrackDir = this.getMETrackDir();
            final String fileDir = metrackDir + File.separator + fileName;
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(metrackDir)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(metrackDir);
                ApiFactoryProvider.getFileAccessAPI().createNewFile(fileDir);
            }
            else if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(fileDir)) {
                ApiFactoryProvider.getFileAccessAPI().createNewFile(fileDir);
            }
            FileAccessUtil.storeProperties(properties, fileDir, true);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerDiffUtil.logger, METrackerDiffUtil.sourceClass, "updateProps", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public String getMETrackDir() {
        return System.getProperty("server.home") + File.separator + "conf" + File.separator + "METracking";
    }
    
    static {
        METrackerDiffUtil.logger = Logger.getLogger("METrackLog");
        METrackerDiffUtil.sourceClass = "meTrackerDiffUtil";
        METrackerDiffUtil.meTrackerDiffUtil = null;
    }
}
