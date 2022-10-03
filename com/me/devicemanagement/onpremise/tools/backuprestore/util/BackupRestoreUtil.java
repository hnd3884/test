package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import java.util.Hashtable;
import com.adventnet.tools.update.installer.ConsoleOut;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMRedisBackupRestore;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMFileBackup;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMDBBackupRestore;
import com.adventnet.mfw.Starter;
import com.adventnet.db.api.RelationalAPI;
import java.sql.Connection;
import com.zoho.framework.utils.crypto.EnDecrypt;
import java.sql.DriverManager;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.PersistenceInitializer;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.net.Socket;
import java.util.StringTokenizer;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.ArrayList;
import com.zoho.framework.utils.OSCheckUtil;
import java.util.List;
import com.adventnet.persistence.fos.FOSUtil;
import com.adventnet.persistence.fos.FOS;
import java.io.StringWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import javax.xml.transform.dom.DOMSource;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import com.me.devicemanagement.framework.utils.XMLUtils;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class BackupRestoreUtil
{
    public static final String RESERVED_LOCATION = "reserved.location";
    public static final String FILE = "file";
    public static final String KEY_NAMES = "key.names";
    public static final String DUMP_BACKUP_RETAIN_COUNT = "dump.backup.retain.count";
    public static final String DUMP_BACKUP_INTERVAL = "dump.backup.interval";
    public static final String BACKUP_CONTENT_TYPE = "backup.content.type";
    public static final String BACKUP_CONFIGURATION_KEY = "backup_configuration";
    public static final String DEFAULT_BACKUP_DIRECTORY = "default.backup.directory";
    private static String tempDeletionWaitingTime;
    private static Logger logger;
    Properties productProperties;
    Properties generalProperties;
    Properties failoverProperties;
    private String backupType;
    private static final String MYSQL_HOME = "mysql.home";
    private static final String PGSQL_HOME = "pgsql.home";
    private static Properties dbProps;
    private static Boolean isScheduleDBBackup;
    private static BackupRestoreUtil utilObj;
    private static String tempFolderName;
    
    public BackupRestoreUtil() {
        this.backupType = null;
        try {
            BackupRestoreUtil.logger.log(Level.INFO, "Reading product.conf");
            final String productConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "product.conf";
            this.productProperties = this.getPropertiesFromFile(productConfFile);
            this.generalProperties = this.getGeneralProperties();
            final String fosConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
            this.failoverProperties = this.getPropertiesFromFile(fosConfFile);
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception in initiating BackupRestoreUtil ", e);
        }
    }
    
    public static BackupRestoreUtil getInstance() {
        if (BackupRestoreUtil.utilObj == null) {
            BackupRestoreUtil.utilObj = new BackupRestoreUtil();
        }
        return BackupRestoreUtil.utilObj;
    }
    
    public String getDefaultBackupLocation() {
        String defaultLocation = null;
        try {
            final Properties backupConfigProperties = this.getMickeyBackupConfigProperties();
            final String confFile = System.getProperty("server.home") + File.separator + backupConfigProperties.getProperty("default.backup.directory", "ScheduledDBBackup");
            defaultLocation = new File(confFile).getCanonicalPath();
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Failed to get default backup location from DB", e);
        }
        return defaultLocation;
    }
    
    public void updateBackupType(final String backupType) {
        BackupRestoreUtil.logger.log(Level.INFO, "Going to update backup Type property for db backup : " + backupType);
        this.backupType = backupType;
    }
    
    public String getBackupType() {
        if (this.backupType == null) {
            try {
                this.backupType = this.getMickeyBackupConfigProperties().getProperty("backup.content.type");
            }
            catch (final Exception exception) {
                BackupRestoreUtil.logger.log(Level.INFO, "Exception while getting backup props from onpremise_setting.json. Hence setting backup type binary.", exception);
                this.backupType = "binary";
            }
        }
        return this.backupType;
    }
    
    public Properties getMickeyBackupConfigProperties() throws Exception {
        final Properties properties = new Properties();
        final JSONObject json = FrameworkConfigurations.getFrameworkConfigurations().getJSONObject("backup_configuration");
        json.remove("default.backup.directory");
        json.remove("reserved.location");
        if (this.backupType != null) {
            json.put("backup.content.type", (Object)this.backupType);
        }
        final Iterator keys = json.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final String value = String.valueOf(json.get(key));
            ((Hashtable<String, String>)properties).put(key, value);
        }
        return properties;
    }
    
    public Properties getReservedPathProps() throws Exception {
        final JSONObject backupConfigurationJson = FrameworkConfigurations.getFrameworkConfigurations().getJSONObject("backup_configuration");
        final Properties reservedPathProps = new Properties();
        if (backupConfigurationJson.has("reserved.location")) {
            final JSONArray jsonArray = backupConfigurationJson.getJSONArray("reserved.location");
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject json = jsonArray.getJSONObject(i);
                final String filePath = String.valueOf(json.get("file"));
                final JSONArray keyNames = json.getJSONArray("key.names");
                if (StringUtils.isEmpty((CharSequence)filePath) || keyNames.length() < 1) {
                    throw new IOException("Required attributes were missing inside reserved.location JsonArray");
                }
                final String fpath = new File(System.getProperty("server.home") + File.separator + filePath).getCanonicalPath();
                final Properties props = FileAccessUtil.readProperties(fpath);
                for (int j = 0; j < keyNames.length(); ++j) {
                    final String key = String.valueOf(keyNames.get(j));
                    final String path = props.getProperty(key);
                    reservedPathProps.setProperty(key, new File(path).getCanonicalPath());
                }
            }
        }
        return reservedPathProps;
    }
    
    private Properties getGeneralProperties() {
        Properties props = new Properties();
        try {
            props = GeneralPropertiesLoader.getInstance().getProperties();
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception in getGeneralProperties : BackupRestoreUtil ", e);
        }
        return props;
    }
    
    public String getValueFromGenProps(final String key) {
        String value = "";
        value = ((Hashtable<K, String>)this.generalProperties).get(key);
        if (value != null) {
            return value;
        }
        return key;
    }
    
    private boolean isPlugin(final Properties generalPluginProperties) {
        final String value = ((Hashtable<K, String>)generalPluginProperties).get("isPlugin");
        if (value != null && value.equals("true")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public static String getString(final String key, final String param, final Locale locale) {
        return getString(key, new Object[] { param }, locale);
    }
    
    public static String getString(final String key, Locale locale) {
        String value = null;
        try {
            if (locale == null) {
                locale = Locale.US;
            }
            if (ResourceBundle.getBundle("resources/backuptool/ApplicationResources", locale).containsKey(key)) {
                value = ResourceBundle.getBundle("resources/backuptool/ApplicationResources", locale).getString(key);
            }
            else {
                value = ResourceBundle.getBundle("resources/device_mgmt_framework/ApplicationResources", locale).getString(key);
            }
        }
        catch (final MissingResourceException mre) {
            BackupRestoreUtil.logger.log(Level.WARNING, "MissingResourceException in getResourceBundle for locale : " + locale + " ; Key : " + key, mre);
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception in getResourceBundle for locale : " + locale + " ; Key : " + key, ex);
        }
        return value;
    }
    
    public static String getString(final String key, final Object[] params, final Locale locale) {
        String value = null;
        if (params != null) {
            value = getString(key, locale);
            value = MessageFormat.format(value, params);
        }
        return value;
    }
    
    public String getBuildNumber() {
        String buildNumber = "";
        buildNumber = this.productProperties.getProperty("buildnumber");
        return buildNumber;
    }
    
    public String getProductName() {
        String productName = "";
        productName = this.generalProperties.getProperty("productname").trim();
        return productName;
    }
    
    public String getFormattedName(String format) {
        format = format.replace("$BUILDNUMBER", this.getBuildNumber());
        format = format.replace("$TIMESTAMP", this.getTimeStamp());
        return format;
    }
    
    private String getTimeStamp() {
        final DateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy-HH-mm", Locale.ENGLISH);
        final String timeStamp = formatter.format(new Date());
        return timeStamp;
    }
    
    public static void checkZipTempFile(final String zipFileName) {
        int loopCount = 0;
        final int waitTimeForTempDeletion = Integer.parseInt(BackupRestoreUtil.tempDeletionWaitingTime);
        final int loopTotalCount = waitTimeForTempDeletion * 60 / 10;
        try {
            final String tempZipFile = zipFileName + ".tmp";
            BackupRestoreUtil.logger.log(Level.INFO, "Going to check zip temp file in location " + tempZipFile);
            BackupRestoreUtil.logger.log(Level.INFO, "is temp file present? : " + new File(tempZipFile).exists());
            while (FileUtil.isFileExists(tempZipFile) && loopCount < loopTotalCount) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Temporary Zip file still exists...");
                Thread.sleep(TimeUnit.SECONDS.toMillis(10L));
                ++loopCount;
            }
            if (FileUtil.isFileExists(tempZipFile)) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Going to delete the temp file", tempZipFile);
                new File(tempZipFile).delete();
            }
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Exception while deleting backup zip temp file", e);
        }
    }
    
    public static void findAndReplaceStrings(final String sourceFileName, final String destFileName, final Properties findReplPair, final String keyPrefixSuffix, final boolean isQuoteReplacement) throws Exception {
        FileReader freader = null;
        FileWriter fwriter = null;
        try {
            BackupRestoreUtil.logger.log(Level.INFO, "Invoked  findAndReplaceStringInFile with source fileName: " + sourceFileName + " dest fileName: " + destFileName + " input strings: " + findReplPair);
            final File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Source File does not exist. " + sourceFileName);
                throw new FileNotFoundException("File does not exist: " + sourceFileName);
            }
            freader = new FileReader(sourceFileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = freader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            freader.close();
            String finalStr = strBuilder.toString();
            final Enumeration prkeys = findReplPair.propertyNames();
            while (prkeys.hasMoreElements()) {
                String findStr = prkeys.nextElement();
                final String replaceStr = findReplPair.getProperty(findStr);
                findStr = keyPrefixSuffix + findStr + keyPrefixSuffix;
                if (isQuoteReplacement) {
                    finalStr = finalStr.replace(findStr, replaceStr);
                }
                else {
                    finalStr = finalStr.replaceAll(findStr, replaceStr);
                }
                BackupRestoreUtil.logger.log(Level.INFO, "Invoked  findAndReplaceStrings findStr: " + findStr + " replaceStr: " + replaceStr);
            }
            fwriter = new FileWriter(destFileName, false);
            fwriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Caught exception in findAndReplaceStrings() source fileName: " + sourceFileName + " dest fileName: " + destFileName + " input strings: " + findReplPair + " exception: ", ex);
            throw ex;
        }
        finally {
            if (freader != null) {
                freader.close();
            }
            if (fwriter != null) {
                fwriter.close();
            }
        }
    }
    
    public Properties getPropertiesFromFile(final String confFileName) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception in getProperties() : ", e);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                BackupRestoreUtil.logger.log(Level.INFO, "Exception in closing stream : ", e);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                BackupRestoreUtil.logger.log(Level.INFO, "Exception in closing stream : ", e2);
            }
        }
        return props;
    }
    
    public Document parseXML(final InputStream inputStream) throws Exception {
        final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
        final Document document = docBuilder.parse(inputStream);
        return document;
    }
    
    public HashMap<Integer, Properties> getFileListFromXML(final Document document) {
        final HashMap<Integer, Properties> backupFiles = new HashMap<Integer, Properties>();
        final NodeList nodeList = document.getElementsByTagName("BackupFile");
        for (int length = nodeList.getLength(), i = 0; i < length; ++i) {
            final Element element = (Element)nodeList.item(i);
            final Properties props = this.getElementAttributes(element);
            final int fileID = Integer.parseInt(props.getProperty("file_id"));
            backupFiles.put(fileID, props);
        }
        BackupRestoreUtil.logger.log(Level.FINE, "Backup File List :: {0}", backupFiles);
        return backupFiles;
    }
    
    private Properties getElementAttributes(final Element element) {
        final Properties props = new Properties();
        final NamedNodeMap attributeMap = element.getAttributes();
        for (int size = attributeMap.getLength(), i = 0; i < size; ++i) {
            final Node node = attributeMap.item(i);
            props.setProperty(node.getNodeName(), node.getNodeValue());
        }
        return props;
    }
    
    public Properties getBackupAttributes(final Document document) {
        final Properties props = new Properties();
        final NodeList nodeList = document.getElementsByTagName("BackupAttribute");
        for (int length = nodeList.getLength(), i = 0; i < length; ++i) {
            final Element element = (Element)nodeList.item(i);
            props.setProperty(element.getAttribute("attribute_name"), element.getAttribute("attribute_value"));
        }
        BackupRestoreUtil.logger.log(Level.FINE, "Backup attributes :: {0}", props);
        return props;
    }
    
    public Properties getBackupDetails(final Document document) {
        final NodeList nodeList = document.getElementsByTagName("BackupDetail");
        final Element element = (Element)nodeList.item(0);
        return this.getElementAttributes(element);
    }
    
    public void createXML(final HashMap<Integer, Properties> backedupList, final int dbBackupContentType, final Properties redisProps, final String xmlFile) throws Exception {
        BackupRestoreUtil.logger.log(Level.INFO, "Creating XML :: {0}", xmlFile);
        final DocumentBuilder db = XMLUtils.getDocumentBuilderInstance();
        final Document outputDoc = db.newDocument();
        final Element rootElement = outputDoc.createElement("backup-files");
        outputDoc.appendChild(rootElement);
        for (final Map.Entry<Integer, Properties> entry : backedupList.entrySet()) {
            final Properties backupProps = entry.getValue();
            final long size = Long.parseLong(backupProps.getProperty("size"));
            if (size != 0L) {
                Element outputElement = outputDoc.createElement("BackupFile");
                if (backupProps.getProperty("db_type") != null) {
                    outputElement = outputDoc.createElement("BackupDB");
                    outputElement.setAttribute("db_type", "" + backupProps.getProperty("db_type"));
                }
                outputElement.setAttribute("file_id", "" + entry.getKey());
                outputElement.setAttribute("file_path", backupProps.getProperty("file_path"));
                outputElement.setAttribute("backup_type", backupProps.getProperty("backup_type"));
                outputElement.setAttribute("backup_options", backupProps.getProperty("backup_types"));
                outputElement.setAttribute("size", "" + size);
                outputElement.setAttribute("ignore_error", backupProps.getProperty("ignore_error"));
                final String backupFile = backupProps.getProperty("file_name");
                if (backupFile != null) {
                    outputElement.setAttribute("file_name", backupFile);
                }
                final String handlerClass = backupProps.getProperty("handler_class");
                if (handlerClass != null) {
                    outputElement.setAttribute("handler_class", handlerClass);
                }
                rootElement.appendChild(outputElement);
            }
        }
        final Element backupElement = outputDoc.createElement("BackupDetail");
        backupElement.setAttribute("build_no", this.getBuildNumber());
        backupElement.setAttribute("product_name", this.getProductName());
        try {
            final Boolean isDCProduct64bit = isDCProduct64bit();
            if (isDCProduct64bit == null) {
                final String excep = "Exception on createXML() method while checking whether the product is 32-bit/64-bit...";
                throw new Exception(excep);
            }
            if (isDCProduct64bit) {
                backupElement.setAttribute("product_arch", "64-bit");
            }
            else {
                backupElement.setAttribute("product_arch", "32-bit");
            }
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.INFO, "Exception on createXML() method while checking whether the product is 32-bit/64-bit...", e);
            throw e;
        }
        backupElement.setAttribute("database", MssqlBackupRestoreUtil.getInstance().getActiveDBServer());
        backupElement.setAttribute("backup_content_type", dbBackupContentType + "");
        if (redisProps != null) {
            final boolean isRedisBackupRestoreNeeded = ((Hashtable<K, Boolean>)redisProps).get("isRedisBackupRestoreNeeded");
            final boolean isRedisAOFEnabled = ((Hashtable<K, Boolean>)redisProps).get("isRedisAOFEnabled");
            backupElement.setAttribute("isRedisBackupRestoreNeeded", isRedisBackupRestoreNeeded + "");
            backupElement.setAttribute("isRedisAOFEnabled", isRedisAOFEnabled + "");
        }
        final String folderName = new File(xmlFile).getParentFile().getName();
        backupElement.setAttribute("folder_name", folderName);
        rootElement.appendChild(backupElement);
        final String serverHome = System.getProperty("server.home");
        final String parentzipName = new File(xmlFile).getAbsoluteFile().getParent();
        BackupRestoreUtil.logger.log(Level.INFO, "Parent Zip file :: {0}", parentzipName);
        final File parentDir = new File(parentzipName).getAbsoluteFile().getParentFile();
        BackupRestoreUtil.logger.log(Level.INFO, "Parent Directory is :: {0}", parentDir);
        final String backupFileName_PPM = "backup-files-pmm" + System.currentTimeMillis() + ".xml";
        BackupRestoreUtil.logger.log(Level.INFO, "PPM BackUp file Name is :: {0}", backupFileName_PPM);
        final Element backupAttrElement = outputDoc.createElement("BackupAttribute");
        backupAttrElement.setAttribute("attribute_id", "1");
        backupAttrElement.setAttribute("attribute_name", "backup-ppm-filename");
        backupAttrElement.setAttribute("attribute_value", backupFileName_PPM);
        rootElement.appendChild(backupAttrElement);
        this.writeDocToXML(outputDoc, xmlFile);
    }
    
    public boolean writeDocToXML(final Document doc, final String xmlFileName) throws Exception {
        boolean status = false;
        FileOutputStream fosXML = null;
        try {
            final Transformer transformer = XMLUtils.getTransformerInstance();
            final DOMSource source = new DOMSource(doc);
            fosXML = new FileOutputStream(xmlFileName);
            final StreamResult result = new StreamResult(fosXML);
            transformer.transform(source, result);
            status = true;
        }
        finally {
            try {
                fosXML.close();
            }
            catch (final Exception e) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception while closing backup-files.xml stream", e);
            }
        }
        return status;
    }
    
    public void checkFreeDiskSpace(final HashMap<Integer, Properties> fileList, final String location, final float factor, final String operationKey) throws DCBackupRestoreException {
        final FileUtil fileUtil = new FileUtil();
        final String serverHome = System.getProperty("server.home");
        double requiredSpace = 0.0;
        for (final Map.Entry<Integer, Properties> entry : fileList.entrySet()) {
            final Properties backupProps = entry.getValue();
            final String filePath = backupProps.getProperty("file_path");
            final File file = new File(serverHome, filePath);
            requiredSpace += FileUtil.getFileOrFolderSize(file);
        }
        requiredSpace *= factor;
        final long availableSpace = fileUtil.getAvailableSpace(location);
        final String availableSpaceString = fileUtil.convertBytesToGBorMB(availableSpace);
        BackupRestoreUtil.logger.log(Level.INFO, "Available free space :: {0}", availableSpaceString);
        final String requiredSpaceString = fileUtil.convertBytesToGBorMB((long)requiredSpace);
        BackupRestoreUtil.logger.log(Level.INFO, "Required Space :: {0}", requiredSpaceString);
        if (availableSpace < requiredSpace) {
            final String operationName = getString(operationKey, null);
            final Object[] arguments = { operationName, availableSpaceString, requiredSpaceString };
            throw createException(-5, arguments, null);
        }
    }
    
    public static DCBackupRestoreException createException(final int errorCode) {
        return createException(errorCode, null, null);
    }
    
    public static DCBackupRestoreException createException(final int errorCode, final Object[] arguments, final Exception e) {
        final DCBackupRestoreException exception = new DCBackupRestoreException(errorCode, e);
        final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
        String errorMessageKey = null;
        Object[] errorMessageArg = null;
        String errorDetailKey = null;
        switch (errorCode) {
            case -6: {
                errorMessageKey = "desktopcentral.tools.common.error.general.title";
                errorDetailKey = "desktopcentral.tools.common.error.general.detail";
                break;
            }
            case -4: {
                errorMessageKey = "desktopcentral.tools.restore.error.server_running.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.server_running.detail";
                break;
            }
            case -18: {
                errorMessageKey = "dc.tools.restore.error.maintenance.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "dc.tools.restore.error.maintenance.detail";
                break;
            }
            case -16: {
                errorMessageKey = "desktopcentral.tools.restore.error.fos_server_running.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.fos_server_running.detail";
                break;
            }
            case -9: {
                errorMessageKey = "desktopcentral.tools.restore.error.incompatible_product.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.incompatible_product.detail";
                break;
            }
            case -3: {
                errorMessageKey = "desktopcentral.tools.restore.error.incompatible_build.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.incompatible_build.detail";
                break;
            }
            case -10: {
                errorMessageKey = "desktopcentral.tools.restore.error.incompatible_db.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.incompatible_db.detail";
                break;
            }
            case -2: {
                errorMessageKey = "desktopcentral.tools.restore.error.file_corrupted.title";
                errorDetailKey = "desktopcentral.tools.restore.error.file_corrupted.detail";
                break;
            }
            case -5: {
                errorMessageKey = "desktopcentral.tools.common.error.insufficient_space.title";
                errorDetailKey = "desktopcentral.tools.common.error.insufficient_space.detail";
                break;
            }
            case -8: {
                errorMessageKey = "desktopcentral.tools.common.error.remote_db_not_running.title";
                errorDetailKey = "desktopcentral.tools.common.error.remote_db_not_running.detail";
                break;
            }
            case -11: {
                errorMessageKey = "desktopcentral.tools.restore.error.current_backup_failed.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.current_backup_failed.detail";
                break;
            }
            case -12: {
                errorMessageKey = "desktopcentral.tools.restore.error.revert_failed.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.restore.error.revert_failed.detail";
                break;
            }
            case -13: {
                errorMessageKey = "desktopcentral.tools.backup.error.files_missing.title";
                errorMessageArg = new Object[] { displayName };
                errorDetailKey = "desktopcentral.tools.backup.error.files_missing.detail";
                break;
            }
            case -14: {
                errorMessageKey = "desktopcentral.tools.backup.error.incompatible_architecture.title";
                errorDetailKey = "desktopcentral.tools.backup.error.incompatible_architecture.detail";
                break;
            }
            case -15: {
                errorMessageKey = "desktopcentral.tools.backup.error.failover_peer_not_reachable.title";
                errorDetailKey = "desktopcentral.tools.backup.error.failover_peer_not_reachable.detail";
                break;
            }
            case -17: {
                errorMessageKey = "desktopcentral.tools.backup.error.sqlfile.title";
                errorDetailKey = "desktopcentral.tools.backup.error.sqlfile.detail";
                break;
            }
            case -19: {
                errorMessageKey = "dc.tools.restore.error.exerunning.title";
                errorDetailKey = "dc.tools.restore.error.exerunning.detail";
                errorMessageArg = arguments;
                break;
            }
            case -21: {
                errorMessageKey = "dc.tools.restore.mssqlversionmismatch.title";
                errorDetailKey = "dc.tools.restore.mssqlversionmismatch.detail";
                errorMessageArg = arguments;
                break;
            }
            case -20: {
                errorMessageKey = "dc.tools.restore.mssqlversioncheckfailed.title";
                errorDetailKey = "dc.tools.restore.mssqlversioncheckfailed.detail";
                errorMessageArg = arguments;
                break;
            }
        }
        String errorMessage = null;
        if (errorMessageArg != null) {
            errorMessage = getString(errorMessageKey, errorMessageArg, null);
        }
        else {
            errorMessage = getString(errorMessageKey, null);
        }
        String errorDetail = null;
        if (arguments != null) {
            errorDetail = getString(errorDetailKey, arguments, null);
        }
        else {
            errorDetail = getString(errorDetailKey, null);
        }
        exception.setErrorMessage(errorMessage);
        exception.setErrorDetail(errorDetail);
        getInstance().addBackupTrackingDetails(4, String.valueOf(errorCode));
        return exception;
    }
    
    public static String getDBbackupFileName() {
        final Date today = Calendar.getInstance().getTime();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmm");
        String backupFile = formatter.format(today);
        if (backupFile != null && backupFile != "") {
            backupFile += ".ezip";
        }
        return backupFile;
    }
    
    public HashMap<String, Properties> getFileList(final Document document) {
        final HashMap<String, Properties> backupFiles = new HashMap<String, Properties>();
        final NodeList nodeList = document.getElementsByTagName("BackupFile");
        for (int length = nodeList.getLength(), i = 0; i < length; ++i) {
            final Element element = (Element)nodeList.item(i);
            final Properties props = this.getElementAttributes(element);
            final String fileID = props.getProperty("file_path");
            backupFiles.put(fileID, props);
        }
        return backupFiles;
    }
    
    public static Boolean maintenanceCompletedSuccessfully() {
        String serverHome = System.getProperty("server.home");
        boolean returnVal = true;
        FileInputStream fis = null;
        final Properties maintenanceProps = new Properties();
        try {
            if (serverHome != null) {
                serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            }
            final File lockFile = new File(serverHome + File.separator + "bin" + File.separator + ".maintenanceLock");
            if (lockFile.exists()) {
                fis = new FileInputStream(lockFile);
                maintenanceProps.load(fis);
                String stopServerStart = "false";
                if (maintenanceProps.containsKey("stopServerStartUp")) {
                    stopServerStart = maintenanceProps.getProperty("stopServerStartUp");
                }
                if (stopServerStart != null && stopServerStart.equalsIgnoreCase("true")) {
                    returnVal = false;
                }
                else if (isPidRunning(maintenanceProps.getProperty("pid"))) {
                    returnVal = false;
                }
                else {
                    lockFile.delete();
                    returnVal = true;
                }
            }
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Exception in maintenanceCompletedSuccessfully ", e);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                BackupRestoreUtil.logger.log(Level.SEVERE, "Exception in maintenanceCompletedSuccessfully finally ", e);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                BackupRestoreUtil.logger.log(Level.SEVERE, "Exception in maintenanceCompletedSuccessfully finally ", e2);
            }
        }
        return returnVal;
    }
    
    private static boolean isPidRunning(final String pid) {
        BufferedReader in = null;
        try {
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "tasklist", "/FI", "\"PID eq " + pid + "\""));
            final Process process = builder.start();
            process.getOutputStream().close();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains(pid)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Exception getting isPidRunning in MaintenanceUtil", e);
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e2) {
                BackupRestoreUtil.logger.log(Level.SEVERE, "Exception finally block ", e2);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e3) {
                BackupRestoreUtil.logger.log(Level.SEVERE, "Exception finally block ", e3);
            }
        }
        return false;
    }
    
    public Properties getVersionDetails(final Document document) {
        final NodeList nodeList = document.getElementsByTagName("VERSION-DETAILS");
        final Element element = (Element)nodeList.item(0);
        return this.getElementAttributes(element);
    }
    
    public static void setDBHome() {
        try {
            final int dbType = getDBType();
            if (dbType == 3) {
                return;
            }
            String dbhome = System.getProperty("db.home");
            BackupRestoreUtil.logger.log(Level.INFO, "old db.home before setting: " + dbhome);
            if (dbType == 1) {
                dbhome = System.getProperty("mysql.home");
            }
            else if (dbType == 2) {
                dbhome = System.getProperty("pgsql.home");
            }
            BackupRestoreUtil.logger.log(Level.INFO, "db.home going to be set: " + dbhome);
            if (dbhome != null && dbhome.trim().length() > 0) {
                System.setProperty("db.home", dbhome);
                BackupRestoreUtil.logger.log(Level.INFO, "new db.home after setting: " + dbhome);
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while setting db.home System propery.", ex);
        }
    }
    
    public static int getDBType() {
        int dbType = 1;
        try {
            final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties dbProps = MssqlBackupRestoreUtil.getProperties(fname);
            if (dbProps != null) {
                final String url = dbProps.getProperty("url");
                BackupRestoreUtil.logger.log(Level.INFO, "URL to find DB name is : " + url + "\t from file: " + fname);
                if (url != null) {
                    if (url.toLowerCase().contains("mysql")) {
                        dbType = 1;
                    }
                    else if (url.toLowerCase().contains("postgresql")) {
                        dbType = 2;
                    }
                    else {
                        if (!url.toLowerCase().contains("sqlserver")) {
                            throw new Exception("Unable to find DB type");
                        }
                        dbType = 3;
                    }
                }
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while getting dbname...", ex);
        }
        return dbType;
    }
    
    public static synchronized void deleteRevertLockFile(final String serverHome) {
        try {
            BackupRestoreUtil.logger.log(Level.INFO, "Invoked  deleteRevertLockFile: ");
            final String revertLckFN = serverHome + File.separator + "bin" + File.separator + "revert.lock";
            final File revertLckFile = new File(revertLckFN);
            if (revertLckFile.exists()) {
                BackupRestoreUtil.logger.log(Level.INFO, "Revert Lock file exists. Going to delete: " + revertLckFN);
                final boolean result = revertLckFile.delete();
                BackupRestoreUtil.logger.log(Level.INFO, "Revert Lock file exists. Going to delete: " + revertLckFN + " delete result: " + result);
            }
            else {
                BackupRestoreUtil.logger.log(Level.INFO, "Revert Lock file does not exist: " + revertLckFN);
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while deleting Revert Lock file.", ex);
        }
    }
    
    public static int executeCommandFile(final String command) {
        int exitStatus = -1;
        InputStream is = null;
        InputStream es = null;
        InputStreamReader isr = null;
        InputStreamReader esr = null;
        BufferedReader isbr = null;
        BufferedReader esbr = null;
        try {
            BackupRestoreUtil.logger.log(Level.INFO, "Executing the command: " + command);
            final ProcessBuilder builder = new ProcessBuilder(new String[] { command });
            final Process pr = builder.start();
            is = pr.getInputStream();
            es = pr.getErrorStream();
            isr = new InputStreamReader(is);
            esr = new InputStreamReader(es);
            isbr = new BufferedReader(isr);
            esbr = new BufferedReader(esr);
            final StringWriter isw = new StringWriter();
            final StringWriter esw = new StringWriter();
            String input = null;
            while ((input = isbr.readLine()) != null) {
                isw.append(input);
            }
            while ((input = esbr.readLine()) != null) {
                esw.append(input);
            }
            BackupRestoreUtil.logger.log(Level.INFO, "Command output stream: " + isw.toString());
            BackupRestoreUtil.logger.log(Level.INFO, "Command error stream: " + esw.toString());
            exitStatus = pr.waitFor();
            BackupRestoreUtil.logger.log(Level.INFO, "Exit status of the command: " + command + "is " + exitStatus);
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Error while executing the command: " + command, e);
            if (isbr != null) {
                try {
                    isbr.close();
                }
                catch (final Exception ex) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (esbr != null) {
                try {
                    esbr.close();
                }
                catch (final Exception ex) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                }
                catch (final Exception ex) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (esr != null) {
                try {
                    esr.close();
                }
                catch (final Exception ex) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (es != null) {
                try {
                    es.close();
                }
                catch (final Exception ex) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
        }
        finally {
            if (isbr != null) {
                try {
                    isbr.close();
                }
                catch (final Exception ex2) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (esbr != null) {
                try {
                    esbr.close();
                }
                catch (final Exception ex2) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                }
                catch (final Exception ex2) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (esr != null) {
                try {
                    esr.close();
                }
                catch (final Exception ex2) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex2) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (es != null) {
                try {
                    es.close();
                }
                catch (final Exception ex2) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
        }
        return exitStatus;
    }
    
    public static Boolean isDCProduct64bit() throws Exception {
        try {
            Boolean isDCProduct64bit = null;
            final String javaOSArch = System.getProperty("sun.arch.data.model");
            if (javaOSArch != null && !javaOSArch.isEmpty() && javaOSArch.equalsIgnoreCase("32")) {
                isDCProduct64bit = false;
                BackupRestoreUtil.logger.log(Level.INFO, "DC Product Architecture is: " + javaOSArch + "  (32-bit)");
            }
            else if (javaOSArch != null && !javaOSArch.isEmpty() && javaOSArch.equalsIgnoreCase("64")) {
                isDCProduct64bit = true;
                BackupRestoreUtil.logger.log(Level.INFO, "DC Product Architecture is: " + javaOSArch + "  (64-bit)");
            }
            return isDCProduct64bit;
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception occurred in method isDCProduct64bit()... Exception : ", ex);
            throw ex;
        }
    }
    
    public void checkAndExecuteNetusePrefixCommand(final String peerIP) throws Exception {
        final Boolean useCredentials = Boolean.valueOf(this.failoverProperties.getProperty("repl.usecredentials", "false"));
        String netusePrefix = null;
        if (useCredentials) {
            final String shareName = this.failoverProperties.getProperty("repl.sharename");
            final String userName = this.failoverProperties.getProperty("repl.username");
            final String password = this.failoverProperties.getProperty("repl.password");
            netusePrefix = "net use \\\\" + peerIP + "\\" + shareName + " /user:" + userName + " " + password;
            this.executeNetUseCommand(netusePrefix);
        }
    }
    
    public void checkAndExecuteNetuseSuffixCommand(final String peerIP) throws Exception {
        final Boolean useCredentials = Boolean.valueOf(this.failoverProperties.getProperty("repl.usecredentials", "false"));
        String netuseSuffix = null;
        if (useCredentials) {
            final String shareName = this.failoverProperties.getProperty("repl.sharename");
            netuseSuffix = "net use \\\\" + peerIP + "\\" + shareName + " /d";
            this.executeNetUseCommand(netuseSuffix);
        }
    }
    
    public void copyToPeer() throws Exception {
        final FOS fos = new FOS();
        fos.initialize();
        final String peerIP = fos.getOtherNode();
        final String serverHome = System.getProperty("server.home");
        if (peerIP != null) {
            BackupRestoreUtil.logger.log(Level.INFO, "Fail Over is enabled.. Pushing changes to other node");
            final String remoteInstallationDir = this.failoverProperties.getProperty("repl.remoteinstallationDir");
            this.checkAndExecuteNetusePrefixCommand(peerIP);
            final String toExcludeDir = "logs,pgsql";
            final String toExcludeFiles = "conf\\fos.conf,conf\\fos.conf.template,conf\\fos_user.conf,bin\\UEMS.exe,bin\\migration.lock";
            final String path = new File(serverHome).getCanonicalPath();
            this.pushChanges(peerIP, remoteInstallationDir, "", FOSUtil.getList("", toExcludeDir), FOSUtil.getList(path + "\\", toExcludeFiles));
            this.checkAndExecuteNetuseSuffixCommand(peerIP);
        }
        else {
            BackupRestoreUtil.logger.log(Level.INFO, "No other node is present in the system, Thus skipping the replication");
        }
    }
    
    public boolean isOtherServerInFosReachable() {
        Boolean isServerReachable = Boolean.FALSE;
        try {
            final FOS fos = new FOS();
            fos.initialize();
            final String peerIP = fos.getOtherNode();
            if (peerIP == null) {
                return true;
            }
            this.checkAndExecuteNetusePrefixCommand(peerIP);
            final String remoteInstallationDir = this.failoverProperties.getProperty("repl.remoteinstallationDir");
            final String location = "\\\\" + peerIP + "\\" + remoteInstallationDir;
            isServerReachable = this.isOtherServerReachable(location);
            this.checkAndExecuteNetuseSuffixCommand(peerIP);
        }
        catch (final Exception ex) {
            Logger.getLogger(BackupRestoreUtil.class.getName()).log(Level.SEVERE, "Exception while checking peer node reachability..", ex);
        }
        return isServerReachable;
    }
    
    public boolean isOtherServerReachable(final String location) {
        boolean retVal = false;
        try {
            final File sharePathLocation = new File(location);
            final boolean direxists = sharePathLocation.isDirectory();
            BackupRestoreUtil.logger.log(Level.INFO, "Share path Location {0} exists ? :: {1}", new Object[] { sharePathLocation, direxists });
            final Long timeStamp = System.currentTimeMillis();
            final String tempDirName = "testAccess-" + timeStamp;
            final String targetDir = sharePathLocation.getAbsolutePath() + File.separator + tempDirName;
            BackupRestoreUtil.logger.log(Level.INFO, "Temp File Path to be created :: {0}", targetDir.replace("\\", "\\\\"));
            final File testDir = new File(targetDir);
            retVal = testDir.mkdir();
            BackupRestoreUtil.logger.log(Level.INFO, "Is the Share has Write Access ? :: {0}", retVal);
            if (retVal && testDir.isDirectory()) {
                testDir.delete();
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while creating share access conf", ex);
        }
        return retVal;
    }
    
    public void pushChanges(final String peerIP, final String remoteInstallationDir, final String dirName, final List<String> toExclude, final List<String> toExcludeFiles) throws Exception {
        Process p = null;
        int exitValue = 0;
        final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
        final List<String> commandList = new ArrayList<String>();
        if (isWindows) {
            commandList.add("cmd");
            commandList.add("/c");
            commandList.add("robocopy");
            commandList.add("..\\" + dirName);
            commandList.add("\\\\" + peerIP + "\\" + remoteInstallationDir + "\\" + dirName);
            commandList.add("/MIR");
            commandList.add("/tee");
            if (toExclude != null && toExclude.size() > 0) {
                commandList.add("/XD");
                commandList.addAll(toExclude);
            }
            if (toExcludeFiles != null && toExcludeFiles.size() > 0) {
                commandList.add("/XF");
                commandList.addAll(toExcludeFiles);
            }
            commandList.add("/R:0");
            commandList.add("/W:0");
            BackupRestoreUtil.logger.log(Level.INFO, "robcopy command to be executed{0}", commandList);
            final File dirPath = new File(System.getProperty("server.home") + File.separator + "bin/");
            try {
                p = this.executeCommand(commandList, null, dirPath);
                dump(p, BackupRestoreUtil.logger);
                p.waitFor();
                exitValue = p.exitValue();
                if (exitValue < 8) {
                    BackupRestoreUtil.logger.info("Mirroring completed. ExitValue :[ " + exitValue + " ]");
                }
                else {
                    if (exitValue > 7 && exitValue < 17) {
                        throw new Exception("Error in mirroring Files. ErrorCode :[ " + exitValue + " ]");
                    }
                    throw new Exception("Error in executing script. ErrorCode :[ " + exitValue + " ]");
                }
            }
            finally {
                p.destroy();
            }
        }
    }
    
    public void pushFile(final String peerIP, final String remoteInstallationDir, final String dirName, final String file) throws Exception {
        Process p = null;
        int exitValue = 0;
        final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
        final List<String> commandList = new ArrayList<String>();
        if (isWindows) {
            commandList.add("cmd");
            commandList.add("/c");
            commandList.add("robocopy");
            commandList.add("..\\" + dirName);
            commandList.add("\\\\" + peerIP + "\\" + remoteInstallationDir + "\\" + dirName);
            commandList.add(file);
            BackupRestoreUtil.logger.log(Level.INFO, "robcopy command to be executed{0}", commandList);
            final File dirPath = new File(System.getProperty("server.home") + File.separator + "bin/");
            try {
                p = this.executeCommand(commandList, null, dirPath);
                dump(p, BackupRestoreUtil.logger);
                p.waitFor();
                exitValue = p.exitValue();
                if (exitValue < 8) {
                    BackupRestoreUtil.logger.info("Mirroring completed. ExitValue :[ " + exitValue + " ]");
                }
                else {
                    if (exitValue > 7 && exitValue < 17) {
                        throw new Exception("Error in mirroring Files. ErrorCode :[ " + exitValue + " ]");
                    }
                    throw new Exception("Error in executing script. ErrorCode :[ " + exitValue + " ]");
                }
            }
            finally {
                p.destroy();
            }
        }
    }
    
    public static void dump(final Process p, final Logger logger) {
        final ProcessWriter pw = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getErrorStream())), logger);
        final ProcessWriter pw2 = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getInputStream())), logger);
        pw.start();
        pw2.start();
    }
    
    public Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath) throws IOException {
        return this.executeCommand(commandList, envProps, directoryPath, false, true);
    }
    
    public Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath, final boolean writeToFile, final boolean executeCmd) throws IOException {
        final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
        if (!writeToFile || isWindows) {
            BackupRestoreUtil.logger.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            if (directoryPath != null) {
                processBuilder.directory(directoryPath);
            }
            setEnvProps(processBuilder, envProps);
            return processBuilder.start();
        }
        final File extFile = new File(new File(System.getProperty("server.home")).getAbsolutePath() + File.separator + "ext.sh");
        BackupRestoreUtil.logger.log(Level.INFO, "Writing comman to ext.sh file ::: {0}", commandList);
        final RandomAccessFile f = new RandomAccessFile(extFile.getAbsolutePath(), "rw");
        if (extFile.length() != 0L) {
            f.seek(extFile.length());
            f.write(System.getProperty("line.separator").getBytes());
        }
        for (final String cmd : commandList) {
            f.write(cmd.toString().getBytes());
            f.write(" ".getBytes());
        }
        f.close();
        if (executeCmd) {
            BackupRestoreUtil.logger.info("Executing all commands in ext.sh ");
            final List<String> extCmdList = new ArrayList<String>();
            extCmdList.add("sh");
            extCmdList.add(extFile.getAbsolutePath());
            BackupRestoreUtil.logger.log(Level.INFO, "Command to be executed ::: {0}", extCmdList);
            final ProcessBuilder processBuilder2 = new ProcessBuilder(extCmdList);
            processBuilder2.directory(directoryPath);
            setEnvProps(processBuilder2, envProps);
            return processBuilder2.start();
        }
        return null;
    }
    
    public static void setEnvProps(final ProcessBuilder processBuilder, final Properties envVariables) {
        if (envVariables != null) {
            final Map<String, String> environment = processBuilder.environment();
            final Enumeration<Object> keys = ((Hashtable<Object, V>)envVariables).keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                environment.put(key, envVariables.getProperty(key));
            }
        }
    }
    
    public void executeNetUseCommand(final String command) throws Exception {
        Process p = null;
        try {
            final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
            if (isWindows) {
                final List<String> commandList = new ArrayList<String>();
                commandList.add("cmd");
                commandList.add("/c");
                commandList.add(command);
                BackupRestoreUtil.logger.log(Level.INFO, "Command to be executed {0}", new Object[] { commandList });
                final File dirPath = new File(System.getProperty("server.home") + File.separator + "bin/");
                p = this.executeCommand(commandList, null, null);
                dump(p, BackupRestoreUtil.logger);
                p.waitFor();
                final int exitValue = p.exitValue();
                BackupRestoreUtil.logger.log(Level.INFO, "Net use Process completed with exit code: [{0}]", new Object[] { exitValue });
            }
            else {
                BackupRestoreUtil.logger.info("FOS in not supported in Linux yet");
            }
        }
        catch (final Exception e) {
            throw e;
        }
        finally {
            p.destroy();
        }
    }
    
    public static void setSevenZipLoc() {
        String sevenZipLoc = null;
        final String sevenZipLocKey = "tools.7zip.win.path";
        if (System.getProperty(sevenZipLocKey) == null) {
            sevenZipLoc = System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe";
            System.setProperty(sevenZipLocKey, sevenZipLoc);
        }
    }
    
    public static Properties getDBProps() {
        if (BackupRestoreUtil.dbProps == null) {
            setDBProps();
        }
        return BackupRestoreUtil.dbProps;
    }
    
    public static void setDBProps() {
        final String dbConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        FileInputStream fis = null;
        try {
            BackupRestoreUtil.dbProps = new Properties();
            if (new File(dbConfFile).exists()) {
                fis = new FileInputStream(dbConfFile);
                BackupRestoreUtil.dbProps.load(fis);
                fis.close();
            }
            addSpecificDBProps();
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: ", ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception in closing stream ", ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception in closing stream ", ex2);
            }
        }
    }
    
    public static void addSpecificDBProps() {
        String connectionURL = BackupRestoreUtil.dbProps.getProperty("url");
        if (MssqlBackupRestoreUtil.getInstance().getActiveDBServer().equals("mssql")) {
            StringTokenizer stk = new StringTokenizer(connectionURL, "//", false);
            stk.nextToken();
            String tok = stk.nextToken();
            stk = new StringTokenizer(tok, ";", false);
            tok = stk.nextToken();
            String hostName = null;
            String portStr = "1433";
            if (tok.indexOf(":") < 0) {
                hostName = tok;
            }
            else {
                final StringTokenizer stk2 = new StringTokenizer(tok, ":", false);
                hostName = stk2.nextToken();
                portStr = stk2.nextToken();
            }
            int port = 1433;
            try {
                port = Integer.parseInt(portStr);
            }
            catch (final Exception e) {
                e.printStackTrace();
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception occured while finding the port, hence taking the default port", e);
            }
            tok = stk.nextToken();
            final String dbName = tok.substring(tok.indexOf("=") + 1);
            ((Hashtable<String, String>)BackupRestoreUtil.dbProps).put("server", hostName);
            ((Hashtable<String, String>)BackupRestoreUtil.dbProps).put("port", port + "");
            ((Hashtable<String, String>)BackupRestoreUtil.dbProps).put("dbname", dbName);
        }
        else {
            String server = null;
            String fileName = null;
            String port2 = "";
            final String DB_PROTOCOL = "jdbc:";
            if (connectionURL.startsWith(DB_PROTOCOL)) {
                connectionURL = connectionURL.substring(DB_PROTOCOL.length());
                final int nextIndex = connectionURL.indexOf(":");
                connectionURL = connectionURL.substring(nextIndex + 1);
            }
            connectionURL = connectionURL.trim();
            char hostSepChar;
            char portSepChar;
            if (connectionURL.startsWith("//")) {
                connectionURL = connectionURL.substring(2);
                hostSepChar = '/';
                portSepChar = ':';
            }
            else {
                hostSepChar = ':';
                portSepChar = '/';
            }
            final int sep = connectionURL.indexOf(hostSepChar);
            server = connectionURL.substring(0, sep);
            fileName = connectionURL.substring(sep + 1);
            if (fileName.indexOf("?") != -1) {
                fileName = fileName.substring(0, fileName.indexOf("?"));
            }
            final int portSep = server.indexOf(portSepChar);
            if (portSep > 0) {
                port2 = server.substring(portSep + 1);
                server = server.substring(0, portSep);
                ((Hashtable<String, String>)BackupRestoreUtil.dbProps).put("port", port2);
            }
            ((Hashtable<String, String>)BackupRestoreUtil.dbProps).put("server", server);
            ((Hashtable<String, String>)BackupRestoreUtil.dbProps).put("dbname", fileName);
        }
    }
    
    public static boolean isDBRunning() {
        final int port = Integer.parseInt(getDBProps().getProperty("port"));
        return isPortEngaged(port);
    }
    
    private static boolean isPortEngaged(final int portNum) {
        if (portNum < 0) {
            return false;
        }
        try {
            final Socket sock = new Socket((String)null, portNum);
            sock.close();
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static boolean isRemoteDB() {
        final int dbType = getDBType();
        return dbType == 3 || (dbType == 2 && DBUtil.isRemoteDB());
    }
    
    public static boolean isRemoteDBRunning() {
        final Properties databaseProps = getDBProps();
        final String jdbcURL = databaseProps.getProperty("url");
        final String username = databaseProps.getProperty("username");
        String password = databaseProps.getProperty("password");
        final int dbType = getDBType();
        boolean isServerStarted = false;
        Label_0081: {
            if (password == null) {
                if ("".equals(password)) {
                    break Label_0081;
                }
            }
            try {
                PersistenceInitializer.loadPersistenceConfigurations();
                password = PersistenceUtil.getDBPasswordProvider().getPassword((Object)password);
            }
            catch (final Exception ex) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception While decrypting DB password ", ex);
            }
        }
        if (dbType == 2 && isRemoteDB()) {
            try {
                final EnDecrypt cryptInstance = (EnDecrypt)new EnDecryptAES256Impl();
                CryptoUtil.setEnDecryptInstance(cryptInstance);
                password = CryptoUtil.decrypt(databaseProps.getProperty("password"), 2);
            }
            catch (final Exception e) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception While decrypting postgres DB password ", e);
            }
        }
        Connection c = null;
        try {
            Class.forName(databaseProps.getProperty("drivername"));
        }
        catch (final ClassNotFoundException cnfe) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Suitable driver for this DB is not specified in database_params.conf ", cnfe);
            return isServerStarted;
        }
        for (int count = 0; count < 3; ++count) {
            try {
                c = DriverManager.getConnection(jdbcURL, username, password);
                isServerStarted = true;
                return isServerStarted;
            }
            catch (final Exception e2) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception in connecting to remote DB", e2);
                BackupRestoreUtil.logger.log(Level.INFO, "Waiting for 3 seconds ...");
                try {
                    Thread.sleep(3000L);
                }
                catch (final InterruptedException ex2) {}
            }
            finally {
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final Exception ex3) {}
                }
            }
        }
        return isServerStarted;
    }
    
    public static boolean isScheduleDBBackup() {
        if (BackupRestoreUtil.isScheduleDBBackup == null) {
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            if (relAPI != null && !Starter.checkShutdownListenerPort()) {
                BackupRestoreUtil.isScheduleDBBackup = Boolean.TRUE;
            }
            else {
                BackupRestoreUtil.isScheduleDBBackup = Boolean.FALSE;
            }
        }
        return BackupRestoreUtil.isScheduleDBBackup;
    }
    
    public String getTempFolderName() {
        if (BackupRestoreUtil.tempFolderName == null) {
            BackupRestoreUtil.tempFolderName = this.getFormattedName("$BUILDNUMBER-$TIMESTAMP") + "-tmp";
        }
        return BackupRestoreUtil.tempFolderName;
    }
    
    public void createRevertLockFile() {
        FileOutputStream fos = null;
        try {
            BackupRestoreUtil.logger.log(Level.INFO, "Invoked  createRevertLockFile: ");
            final String revertLockFileName = System.getProperty("server.home") + File.separator + "bin" + File.separator + "revert.lock";
            final File revertLockFile = new File(revertLockFileName);
            if (revertLockFile.exists()) {
                BackupRestoreUtil.logger.log(Level.INFO, "Revert Lock file already exists. Going to delete: {0}", revertLockFile);
                revertLockFile.delete();
            }
            final Properties revertProps = new Properties();
            final String timeStamp = new Date(System.currentTimeMillis()).toString();
            revertProps.setProperty("timestamp", timeStamp);
            fos = new FileOutputStream(revertLockFile);
            revertProps.store(fos, null);
            fos.close();
            BackupRestoreUtil.logger.log(Level.INFO, "Revert lock file {0} is created.", revertLockFile);
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception while creation revert.lock file :: ", ex);
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    public void addBackupTrackingDetails(final int status, final String val) {
        final String backupTrackerJSONFilePath = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "MeTrack" + File.separator + File.separator + "BackupTracking.json";
        try {
            final JSONObject backupTrackingJSON = this.getJSONFromFile(backupTrackerJSONFilePath);
            switch (status) {
                case 1: {
                    backupTrackingJSON.put("loc-type", (Object)val);
                    break;
                }
                case 2: {
                    backupTrackingJSON.put("is-mail-configured", (Object)val);
                    break;
                }
                case 3: {
                    this.addBackupRestoreStatus(backupTrackingJSON, "backup-status", val);
                    break;
                }
                case 4: {
                    this.addBackupRestoreStatus(backupTrackingJSON, "restore-status", val);
                    break;
                }
                case 5: {
                    backupTrackingJSON.put("backup-count", (Object)val);
                    break;
                }
                case 6: {
                    backupTrackingJSON.put("errcode", (Object)val);
                    break;
                }
                case 7: {
                    backupTrackingJSON.put("use-nat", (Object)val);
                    if (val != null && val.trim().equalsIgnoreCase("true")) {
                        backupTrackingJSON.put("use-nat-his", (Object)val);
                        break;
                    }
                    break;
                }
                case 8: {
                    backupTrackingJSON.put("isPasswordProtected", (Object)val);
                    break;
                }
                case 9: {
                    backupTrackingJSON.put("backup-type", (Object)val);
                    break;
                }
                case 10: {
                    backupTrackingJSON.put("restore-type", (Object)val);
                    break;
                }
                case 11: {
                    backupTrackingJSON.put("backup-folder-permission", (Object)val);
                    break;
                }
                case 12: {
                    backupTrackingJSON.put("bak-backup-permission", (Object)val);
                    break;
                }
                default: {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Unknown status found : " + status + " value received : " + val);
                    break;
                }
            }
            this.writeStringToFile(new File(backupTrackerJSONFilePath), backupTrackingJSON.toString());
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception while creating tracker JSON file :: ", ex);
        }
    }
    
    private void addBackupRestoreStatus(final JSONObject backupTrackingJSON, final String key, final String status) throws Exception {
        this.updateInternalJSON(key, status);
        JSONObject statusJSON = new JSONObject();
        if (backupTrackingJSON.has(key)) {
            statusJSON = backupTrackingJSON.getJSONObject(key);
        }
        if (status.equalsIgnoreCase("success")) {
            final String currentMSString = String.valueOf(System.currentTimeMillis());
            statusJSON.put("last-success-time", (Object)currentMSString);
        }
        final int successCount = this.getStatusCount(key, Boolean.TRUE);
        final int failureCount = this.getStatusCount(key, Boolean.FALSE);
        if (key.equalsIgnoreCase("backup-status")) {
            statusJSON.put("bak-enabled", DMDBBackupRestore.isBakFormatEnabled);
        }
        statusJSON.put("success", successCount);
        statusJSON.put("failure", failureCount);
        final JSONObject topErrors = this.getTopErrors(key);
        statusJSON.put("top-errors", (Object)topErrors);
        backupTrackingJSON.put(key, (Object)statusJSON);
    }
    
    private void updateInternalJSON(final String key, final String status) throws Exception {
        final String internalJSONFilePath = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "MeTrack" + File.separator + File.separator + "BackupTrackingInternal.json";
        final JSONObject internalJSON = this.getJSONFromFile(internalJSONFilePath);
        JSONArray historyJSON = new JSONArray();
        if (internalJSON.has(key)) {
            historyJSON = internalJSON.getJSONArray(key);
        }
        if (historyJSON.length() < 30) {
            historyJSON.put((Object)status);
        }
        else {
            for (int index = 1; index < historyJSON.length(); ++index) {
                historyJSON.put(index - 1, (Object)String.valueOf(historyJSON.get(index)));
            }
            historyJSON.put(historyJSON.length() - 1, (Object)status);
        }
        internalJSON.put(key, (Object)historyJSON);
        this.writeStringToFile(new File(internalJSONFilePath), internalJSON.toString());
    }
    
    private int getStatusCount(final String key, final boolean isSuccessCount) throws Exception {
        int successStatusCount = 0;
        final String internalJSONFilePath = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "MeTrack" + File.separator + File.separator + "BackupTrackingInternal.json";
        final JSONObject internalJSON = this.getJSONFromFile(internalJSONFilePath);
        JSONArray historyJSON = new JSONArray();
        if (internalJSON.has(key)) {
            historyJSON = internalJSON.getJSONArray(key);
        }
        for (int index = 0; index < historyJSON.length(); ++index) {
            if (historyJSON.get(index).toString().equalsIgnoreCase("success")) {
                ++successStatusCount;
            }
        }
        int statusCount;
        if (isSuccessCount) {
            statusCount = successStatusCount;
        }
        else {
            statusCount = historyJSON.length() - successStatusCount;
        }
        return statusCount;
    }
    
    private JSONObject getTopErrors(final String key) throws Exception {
        final JSONObject topErrors = new JSONObject();
        final String internalJSONFilePath = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "MeTrack" + File.separator + File.separator + "BackupTrackingInternal.json";
        final JSONObject internalJSON = this.getJSONFromFile(internalJSONFilePath);
        JSONArray historyJSON = new JSONArray();
        if (internalJSON.has(key)) {
            historyJSON = internalJSON.getJSONArray(key);
        }
        final JSONObject errorCountJSON = new JSONObject();
        for (int index = 0; index < historyJSON.length(); ++index) {
            final String error = historyJSON.get(index).toString();
            if (!error.equalsIgnoreCase("success")) {
                int count = 0;
                if (errorCountJSON.has(error)) {
                    count = errorCountJSON.getInt(error);
                }
                ++count;
                errorCountJSON.put(error, count);
            }
        }
        for (int i = 0; i < 3 && errorCountJSON.length() > 0; ++i) {
            final String error = this.getTopErrorFromJSON(errorCountJSON);
            topErrors.put(error, errorCountJSON.getInt(error));
            errorCountJSON.remove(error);
        }
        return topErrors;
    }
    
    private String getTopErrorFromJSON(final JSONObject errorJSON) throws Exception {
        String topErrorKey = null;
        int topCount = 0;
        final Iterator itr = errorJSON.keys();
        while (itr.hasNext()) {
            final String key = itr.next();
            final int count = errorJSON.getInt(key);
            if (count > topCount) {
                topCount = count;
                topErrorKey = key;
            }
        }
        return topErrorKey;
    }
    
    public JSONObject getJSONFromFile(final String jsonFilePath) throws Exception {
        JSONObject trackingJSON = null;
        final File backupTrackerJsonFile = new File(jsonFilePath);
        if (backupTrackerJsonFile.exists()) {
            trackingJSON = new JSONObject(readFileAsString(jsonFilePath));
            BackupRestoreUtil.logger.log(Level.WARNING, "Successfully loaded existing Backup Tracker JSON file");
        }
        else {
            trackingJSON = new JSONObject();
            BackupRestoreUtil.logger.log(Level.WARNING, "Successfully created a new Backup tracking JSON Object");
        }
        return trackingJSON;
    }
    
    public static String readFileAsString(final String fileLoc) throws IOException {
        BackupRestoreUtil.logger.log(Level.INFO, "Reading METracking JSON file");
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        final StringBuilder fileString = new StringBuilder();
        try {
            reader = new FileReader(fileLoc);
            bufferedReader = new BufferedReader(reader);
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                fileString.append(line);
            }
            BackupRestoreUtil.logger.log(Level.FINE, "METracking JSON file is converted to String successfully");
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception ex) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception in closing reader :", ex);
            }
        }
        return fileString.toString();
    }
    
    public void writeStringToFile(final File file, final String content) {
        FileWriter fileWriter = null;
        try {
            final File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            fileWriter = new FileWriter(file);
            BackupRestoreUtil.logger.log(Level.INFO, "Going to write : " + content + " in " + file.getName());
            fileWriter.write(content);
            BackupRestoreUtil.logger.log(Level.INFO, "Finished writing JSON Object to JSON file");
        }
        catch (final IOException ioEx) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception in writing JSON file", ioEx);
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
            catch (final Exception ex) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Caught Exception in closing FileWriter stream : ", ex);
            }
        }
        finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
            catch (final Exception ex2) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Caught Exception in closing FileWriter stream : ", ex2);
            }
        }
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName, final String comments) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
            final Enumeration keys = newprops.propertyNames();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                props.setProperty(key, newprops.getProperty(key));
            }
            fos = new FileOutputStream(confFileName);
            props.store(fos, comments);
            fos.close();
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Caught exception: {0}", ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static boolean isRedisEnabled() {
        boolean isRedisEnabled = false;
        final String productSettingsFileName = System.getProperty("server.home") + File.separator + BackupRestoreContants.PRODUCT_SETTINGS_LOC;
        final Properties productSettingsProps = getInstance().getPropertiesFromFile(productSettingsFileName);
        if (productSettingsProps != null && productSettingsProps.containsKey("enableRedis")) {
            isRedisEnabled = Boolean.parseBoolean(productSettingsProps.getProperty("enableRedis"));
        }
        return isRedisEnabled;
    }
    
    public static boolean isAOFEnabled() {
        boolean isAOFEnabled = false;
        final String redisSettingsFile = System.getProperty("server.home") + File.separator + BackupRestoreContants.REDIS_SETTINGS_LOC;
        final Properties redisProps = getInstance().getPropertiesFromFile(redisSettingsFile);
        if (redisProps != null && redisProps.containsKey("enableAOF")) {
            final String val = redisProps.getProperty("enableAOF");
            if (val.equalsIgnoreCase("yes")) {
                isAOFEnabled = Boolean.TRUE;
            }
        }
        return isAOFEnabled;
    }
    
    public void checkDBFileSystem(final HashMap<Integer, Properties> fileList, final String location, final float factor, final String operationKey) throws DCBackupRestoreException {
        final String fileSystemLockFile = System.getProperty("server.home") + File.separator + "bin" + File.separator + "filesystem.lock";
        if (new File(fileSystemLockFile).exists()) {
            BackupRestoreUtil.logger.log(Level.WARNING, "File System lock file exists, So Do not Allow to take a backup.");
            final Properties fileSystemProps = this.getPropertiesFromFile(fileSystemLockFile);
            final String LastSuccessfullScheduledBackup = fileSystemProps.getProperty("LastSuccessfullScheduledBackup");
            final String ScheduledBackupLocation = fileSystemProps.getProperty("ScheduledBackupLocation");
            final Object[] arguments = { LastSuccessfullScheduledBackup, ScheduledBackupLocation };
            throw createException(-17, arguments, null);
        }
    }
    
    public void deleteTempFiles() {
        BackupRestoreUtil.logger.log(Level.INFO, " Password hint file deletion status : " + new File(System.getProperty("server.home") + File.separator + "DB_Password_Hint.txt").delete());
        BackupRestoreUtil.logger.log(Level.INFO, " Backup XML file deletion status : " + new File(System.getProperty("server.home") + File.separator + "backup-files.xml").delete());
    }
    
    public void deleteTemporaryZipFile(final String tempZipFile) throws Exception {
        boolean deletionstatus = false;
        int count = 1;
        if (new File(tempZipFile).exists()) {
            while (count <= 10) {
                deletionstatus = new File(tempZipFile).delete();
                BackupRestoreUtil.logger.log(Level.INFO, "Attempt - " + count + " :: TemporaryZip File " + tempZipFile + " deletion status : " + deletionstatus);
                ++count;
                if (deletionstatus) {
                    break;
                }
                Thread.sleep(10L);
            }
        }
    }
    
    public boolean useNativeForExecution() {
        final String useNative = System.getProperty("use.native.execution", "false");
        return Boolean.parseBoolean(useNative.trim());
    }
    
    public void useDLLForBackupExe() {
        BackupRestoreUtil.logger.log(Level.INFO, "Changing Execution type to native");
        System.setProperty("use.native.execution", "true");
        this.addBackupTrackingDetails(7, "true");
        final String backupPropsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
        final StringBuilder fileContent = new StringBuilder();
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(backupPropsFile));
            String line = reader.readLine();
            boolean isUpdated = false;
            while (line != null) {
                if (line.startsWith("use.native.execution")) {
                    line = "use.native.execution=true";
                    isUpdated = true;
                }
                fileContent.append(line + "\n");
                line = reader.readLine();
            }
            if (!isUpdated) {
                fileContent.append("use.native.execution=true");
            }
            reader.close();
            final String fileContentString = fileContent.toString();
            writer = new FileWriter(backupPropsFile);
            writer.write(fileContentString, 0, fileContentString.length());
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception while updating system_properties.conf file", e);
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception ex) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while closing readers", ex);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception ex2) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while closing readers", ex2);
            }
        }
    }
    
    public long preCheck(final String destination) throws Exception {
        final HashMap<Integer, Properties> backupList = DMFileBackup.getInstance().getBackupList();
        DMDBBackupRestore.includeDataFolder(backupList);
        if (isRedisEnabled()) {
            DMRedisBackupRestore.includeRedisDataFolder(backupList);
        }
        this.checkFreeDiskSpace(backupList, destination, 1.0f, "desktopcentral.tools.backup.title");
        final int dbType = getDBType();
        if (dbType == 3) {
            this.checkDBFileSystem(backupList, destination, 1.0f, "desktopcentral.tools.backup.title");
        }
        return FileUtil.getNumberOfFiles(DMFileBackup.getInstance().getBackupList());
    }
    
    public static boolean startDB() {
        if (isRemoteDB()) {
            return true;
        }
        try {
            final List<String> commandList = new ArrayList<String>();
            final String pgctlPath = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "bin" + File.separator + "pg_ctl.exe";
            final File pgctl = new File(pgctlPath);
            commandList.add(pgctl.getCanonicalPath());
            commandList.add("-D");
            final String dataDirectoryPath = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data";
            final File dataDirectory = new File(dataDirectoryPath);
            commandList.add(dataDirectory.getCanonicalPath());
            commandList.add("-o");
            commandList.add("\"-p" + getDBProps().getProperty("port") + "\"");
            commandList.add("start");
            BackupRestoreUtil.logger.log(Level.INFO, "Starting DB Server Using The Command : {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.directory(new File(System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data"));
            final Process process = processBuilder.start();
            final int statusCode = process.waitFor();
            BackupRestoreUtil.logger.log(Level.INFO, "Status Code : " + statusCode);
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Error while starting DB server: " + e);
        }
        return checkAndWaitForDBStartup();
    }
    
    private static boolean checkAndWaitForDBStartup() {
        int dbStartTimer = 0;
        boolean isStarted = false;
        final int maxStartTimer = validateInputTime(System.getProperty("DBMaxStartupTime", "180"));
        try {
            final int dbPort = Integer.parseInt(getDBProps().getProperty("port"));
            boolean dbIsReady = false;
            for (int s = 0; s < maxStartTimer / 3; ++s) {
                dbIsReady = isPortEngaged(dbPort);
                if (s == 0 || s % 10 == 0) {
                    BackupRestoreUtil.logger.log(Level.INFO, "StartDB command Executed " + dbStartTimer + " seconds Ago..");
                    BackupRestoreUtil.logger.log(Level.INFO, "Testing port: " + dbPort + " Ready for connection : " + dbIsReady);
                    BackupRestoreUtil.logger.log(Level.INFO, "DB is Ready: " + dbIsReady);
                }
                if (dbIsReady) {
                    BackupRestoreUtil.logger.log(Level.INFO, "Testing port: " + dbPort + " Returning " + dbIsReady);
                    BackupRestoreUtil.logger.log(Level.INFO, "DB is Ready: " + dbIsReady);
                    BackupRestoreUtil.logger.log(Level.INFO, "Total Time taken to Start DB is " + dbStartTimer + " Seconds.");
                    break;
                }
                Thread.sleep(1000L);
                dbStartTimer += 3;
            }
            final String connURL = getDBProps().getProperty("url");
            final Properties dbProps = getDBProps();
            Class.forName(dbProps.getProperty("drivername"));
            final String userName = dbProps.getProperty("username");
            String password = dbProps.getProperty("password");
            try {
                PersistenceInitializer.loadPersistenceConfigurations();
                password = PersistenceUtil.getDBPasswordProvider().getPassword((Object)password);
            }
            catch (final Exception ex) {
                BackupRestoreUtil.logger.log(Level.WARNING, "Exception While decrypting password:", ex);
            }
            BackupRestoreUtil.logger.log(Level.INFO, "Driver is loaded for connection check...");
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(connURL, userName, password);
                if (conn != null) {
                    BackupRestoreUtil.logger.log(Level.INFO, "DB is Ready with getConnection() check with url: " + connURL);
                    isStarted = true;
                }
            }
            catch (final Exception ex5) {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception ex2) {
                    ex2.printStackTrace();
                }
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception ex3) {
                    ex3.printStackTrace();
                }
            }
        }
        catch (final Exception ex4) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while checking whether DB is ready. ", ex4);
            isStarted = false;
        }
        return isStarted;
    }
    
    public static void stopDB() {
        if (isRemoteDB()) {
            return;
        }
        int dbStopTimer = 0;
        ProcessBuilder processBuilder = null;
        Process process = null;
        final String dbname = getDBProps().getProperty("dbname");
        final File pidFile = getDBPIDFile();
        BackupRestoreUtil.logger.log(Level.INFO, dbname + " PID File : " + pidFile);
        try {
            processBuilder = new ProcessBuilder(new String[] { System.getProperty("server.home") + "/pgsql/bin/pg_ctl", "stop", "-m", "fast" });
            final Map<String, String> env = processBuilder.environment();
            env.put("PGDATA", System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data");
            processBuilder.redirectErrorStream(true);
            BackupRestoreUtil.logger.log(Level.INFO, "STOPPING DB SERVER: {0}", processBuilder.command());
            process = processBuilder.start();
            final InputStream is = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                BackupRestoreUtil.logger.log(Level.INFO, line);
            }
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Error while stopping DB server: " + e);
        }
        try {
            final int dbPort = Integer.parseInt(getDBProps().getProperty("port"));
            boolean dbIsAvailable = true;
            final int maxStopTimer = validateInputTime(System.getProperty("DBMaxStopTime", "180"));
            for (int s = 0; s < maxStopTimer; ++s) {
                dbIsAvailable = isDBRunning();
                if (s == 0 || (s + 1) % 10 == 0) {
                    final Float stopTime = dbStopTimer / 10.0f;
                    BackupRestoreUtil.logger.log(Level.INFO, "StopDB command Executed " + Math.round(stopTime) + " seconds Ago..");
                    BackupRestoreUtil.logger.log(Level.INFO, "Testing port: " + dbPort + " Returning : " + dbIsAvailable);
                    BackupRestoreUtil.logger.log(Level.INFO, "DB is Available: " + dbIsAvailable);
                }
                if (!dbIsAvailable) {
                    final Float stopTime = (Float)(dbStopTimer / 10);
                    BackupRestoreUtil.logger.log(Level.INFO, "Testing port: " + dbPort + ". Returning " + dbIsAvailable);
                    BackupRestoreUtil.logger.log(Level.INFO, "DB is Available: " + dbIsAvailable);
                    BackupRestoreUtil.logger.log(Level.INFO, "Total Time taken to Stop DB is " + Math.round(stopTime) + " Seconds.");
                    break;
                }
                Thread.sleep(900L);
                dbStopTimer += 9;
            }
            if (pidFile != null) {
                for (int s = 0; s < maxStopTimer; ++s) {
                    if (!pidFile.exists()) {
                        BackupRestoreUtil.logger.log(Level.INFO, "PID file is Not exists ");
                        break;
                    }
                    if (s == 0 || (s + 1) % 10 == 0) {
                        final Float stopTime = (Float)(dbStopTimer / 10);
                        BackupRestoreUtil.logger.log(Level.INFO, "StopDB command Executed " + Math.round(stopTime) + " seconds Ago..");
                        BackupRestoreUtil.logger.log(Level.INFO, "PID file is still exists: " + pidFile);
                    }
                    Thread.sleep(900L);
                    dbStopTimer += 9;
                }
            }
            if (dbname.equalsIgnoreCase("pgsql")) {
                final int exitcode = process.waitFor();
                BackupRestoreUtil.logger.log(Level.WARNING, "DB Stop status after process.waitfor() : " + exitcode);
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while checking whether DB is completely stopped. " + ex);
        }
    }
    
    private static File getDBPIDFile() {
        File pidFile = null;
        try {
            final String dbname = getDBProps().getProperty("dbname");
            final String dbDataFolderName = System.getProperty("server.home") + File.separator + dbname + File.separator + "data";
            final File dbDataFolder = new File(dbDataFolderName);
            if (dbDataFolder.isDirectory()) {
                final File[] files = dbDataFolder.listFiles();
                if (files != null && files.length > 0) {
                    for (int s = 0; s < files.length; ++s) {
                        final File f = files[s];
                        if (f.getName().toLowerCase().endsWith(".pid")) {
                            pidFile = f;
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.INFO, "Caught exception while retrieving mysql pid file path", ex);
        }
        return pidFile;
    }
    
    private static int validateInputTime(final String timeString) {
        final int defaultTime = 180;
        try {
            return Integer.parseInt(timeString);
        }
        catch (final NumberFormatException e) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Input StartStop Time Format Incorrect - Provided value = " + timeString + " Setting default time : " + defaultTime);
            return defaultTime;
        }
    }
    
    public static boolean validateDBStartupRequired(final String isRequired) {
        final boolean defaultIsRequired = false;
        try {
            if (!isRequired.equalsIgnoreCase("true") && !isRequired.equalsIgnoreCase("false")) {
                BackupRestoreUtil.logger.log(Level.SEVERE, "Input DBStartUpRequired Value Incorrect - Provided value = " + isRequired + " Setting default value : " + defaultIsRequired);
                return defaultIsRequired;
            }
            return Boolean.parseBoolean(isRequired);
        }
        catch (final Exception e) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Input DBStartUpRequired Value Incorrect - Provided value = " + isRequired + " Setting default value : " + defaultIsRequired);
            return defaultIsRequired;
        }
    }
    
    public static void printOneLineLog(final Level level, final String logmessage) {
        if (System.getProperty("uniformlogformatter.enable", "false").equalsIgnoreCase("true")) {
            DCLogUtil.getOneLineLoggerInstance().log(level, logmessage);
        }
    }
    
    public static Properties readProperties(final String filePath) {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(filePath).exists()) {
                ism = new FileInputStream(filePath);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Caught exception while reading properties from file: " + filePath, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    public static void writeInDataBaseParamsConf(final Properties properties) throws IOException {
        final String dataBaseParamsConfPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final List<String> oldDataBaseParamsLines = Files.readAllLines(Paths.get(dataBaseParamsConfPath, new String[0]));
        final List<String> newDataBaseParamsLines = new ArrayList<String>();
        for (String line : oldDataBaseParamsLines) {
            if (!line.startsWith("#")) {
                for (final Object key : ((Hashtable<Object, V>)properties).keySet()) {
                    if (line.startsWith(key.toString())) {
                        line = key + "=" + properties.getProperty(key.toString());
                    }
                }
            }
            newDataBaseParamsLines.add(line);
        }
        Files.deleteIfExists(Paths.get(dataBaseParamsConfPath, new String[0]));
        Files.write(Paths.get(dataBaseParamsConfPath, new String[0]), newDataBaseParamsLines, StandardOpenOption.CREATE);
    }
    
    public static void removeInDataBaseParamsConf(final String key) throws IOException {
        final String dataBaseParamsConfPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final List<String> oldDataBaseParamsLines = Files.readAllLines(Paths.get(dataBaseParamsConfPath, new String[0]));
        final List<String> newDataBaseParamsLines = new ArrayList<String>();
        for (String line : oldDataBaseParamsLines) {
            if (!line.startsWith("#") && line.startsWith(key)) {
                line = null;
            }
            if (line != null) {
                newDataBaseParamsLines.add(line);
            }
        }
        Files.deleteIfExists(Paths.get(dataBaseParamsConfPath, new String[0]));
        Files.write(Paths.get(dataBaseParamsConfPath, new String[0]), newDataBaseParamsLines, StandardOpenOption.CREATE);
    }
    
    public static void killAssociatedProcesses() throws DCBackupRestoreException {
        killProcess("dmredis-server.exe", 1);
        killProcess("dmredis-check-dump.exe", 1);
        killProcess("dmredis-cli.exe", 1);
    }
    
    public static void killProcess(final String processName, final int retryCount) throws DCBackupRestoreException {
        executeCommand(retryCount, "Taskkill.exe", "/IM", processName, "/F");
        if (isProcessRunning(processName)) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "unable to continue process since " + processName + " is still running");
            throw createException(-19, new String[] { processName }, new Exception());
        }
    }
    
    public static boolean doesProcessExist(final String processName) {
        final String output = executeCommand(0, "tasklist.exe", "/FO", "TABLE", "/FI", "IMAGENAME eq " + processName);
        return output.contains(processName);
    }
    
    public static boolean isProcessRunning(final String processName) {
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "tasklist.exe" });
            final Process process = processBuilder.start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = "";
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                result = result + str + "\n";
            }
            final String[] split;
            final String[] tasks = split = result.split("\n");
            for (final String task : split) {
                if (task.contains(processName)) {
                    BackupRestoreUtil.logger.log(Level.INFO, processName + " process is running ");
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.SEVERE, "Exception while checking if process is running.", ex);
            return Boolean.FALSE;
        }
    }
    
    public static String executeCommand(final int retryCount, final String... commandWithArgs) {
        BackupRestoreUtil.logger.log(Level.INFO, "----------------------- In Execute command ----------------------------");
        String output = "";
        BufferedReader commandOutput = null;
        for (int count = 0; count <= retryCount; ++count) {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
                BackupRestoreUtil.logger.log(Level.INFO, "COMMAND: {0}", processBuilder.command());
                processBuilder.redirectErrorStream(true);
                final Process process = processBuilder.start();
                commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = "";
                while ((s = commandOutput.readLine()) != null) {
                    BackupRestoreUtil.logger.log(Level.INFO, s);
                    output += s;
                }
                final int exitValue = process.waitFor();
                BackupRestoreUtil.logger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
                if (exitValue != 1) {}
            }
            catch (final IOException ioe) {
                BackupRestoreUtil.logger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ioe);
            }
            catch (final InterruptedException ie) {
                BackupRestoreUtil.logger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ie);
            }
            finally {
                try {
                    if (commandOutput != null) {
                        commandOutput.close();
                    }
                }
                catch (final Exception exp) {
                    BackupRestoreUtil.logger.log(Level.WARNING, "Exception : ", exp);
                }
            }
        }
        BackupRestoreUtil.logger.log(Level.INFO, "---------------------- End of Execute command -------------------------");
        return output;
    }
    
    public static void executeInitPgsql(final String serverHome) {
        BufferedReader in = null;
        final String binFolder = serverHome + File.separator + "bin";
        final String initPgsqlBat = binFolder + File.separator + "initPgsql.bat";
        BackupRestoreUtil.logger.log(Level.INFO, "going to execute command" + initPgsqlBat);
        final File filepath = new File(binFolder);
        final List<String> command = new ArrayList<String>();
        BackupRestoreUtil.logger.log(Level.INFO, "Going to set privilage for Data Folder");
        try {
            command.add("cmd.exe");
            command.add("/c");
            command.add("initPgsql.bat");
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            processBuilder.directory(filepath);
            BackupRestoreUtil.logger.log(Level.INFO, "COMMAND : {0}", processBuilder.command());
            final Process process = processBuilder.start();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BackupRestoreUtil.logger.log(Level.INFO, "************Start of initPgsql.bat Execution ******");
            String outputLine = null;
            if (in != null) {
                while ((outputLine = in.readLine()) != null) {
                    BackupRestoreUtil.logger.log(Level.INFO, outputLine);
                }
                BackupRestoreUtil.logger.log(Level.INFO, "************End of initPgsql.bat Execution ********");
                in.close();
            }
            else {
                BackupRestoreUtil.logger.log(Level.INFO, "initPgsql.bat command execution failed.");
            }
            BackupRestoreUtil.logger.log(Level.INFO, "Successfully modified permission for the  Data Folder");
        }
        catch (final Exception ex) {
            BackupRestoreUtil.logger.log(Level.WARNING, "Exception occured while setting permisson for the Data Folder ", ex);
        }
    }
    
    static {
        BackupRestoreUtil.tempDeletionWaitingTime = System.getProperty("temp.deletion.waittime", "3");
        BackupRestoreUtil.logger = Logger.getLogger("ScheduleDBBackup");
        BackupRestoreUtil.isScheduleDBBackup = null;
        BackupRestoreUtil.utilObj = null;
        BackupRestoreUtil.tempFolderName = null;
    }
    
    private static class ProcessWriter extends Thread
    {
        BufferedReader br;
        Logger log;
        
        ProcessWriter(final BufferedReader br, final Logger logger) {
            this.log = null;
            this.br = br;
            this.log = logger;
        }
        
        @Override
        public void run() {
            try {
                String line;
                while ((line = this.br.readLine()) != null) {
                    if (this.log == null) {
                        ConsoleOut.print("\r" + line);
                    }
                    else {
                        this.log.info(line);
                    }
                }
            }
            catch (final Exception exc) {
                this.log.severe(exc.getMessage());
            }
        }
    }
}
