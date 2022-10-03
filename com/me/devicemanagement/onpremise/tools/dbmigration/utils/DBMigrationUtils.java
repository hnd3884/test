package com.me.devicemanagement.onpremise.tools.dbmigration.utils;

import java.util.Hashtable;
import java.sql.Connection;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;
import com.me.devicemanagement.onpremise.tools.dbmigration.gui.ChangeDBServerGUI;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import java.net.URISyntaxException;
import java.awt.Desktop;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.JEditorPane;
import java.sql.DriverManager;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.nio.file.StandardCopyOption;
import com.me.devicemanagement.onpremise.tools.dbmigration.action.ChangeDBServer;
import org.apache.commons.io.FileUtils;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import java.io.BufferedWriter;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.zoho.framework.utils.crypto.CryptoUtil;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.FileNotFoundException;
import org.w3c.dom.NodeList;
import java.util.Random;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBMigrationUtils
{
    private static final String MYSQL_HOME = "mysql.home";
    private static final String PGSQL_HOME = "pgsql.home";
    private static final String MASTERKEY_PASSWORD = "masterkey.password";
    private static final String ENABLE_SECURED_CONN;
    private static Logger logger;
    public static final String BACKUPPATH;
    
    public static void setDBHome() {
        try {
            final String dbname = getDBName();
            if (dbname == null || dbname.trim().equals("")) {
                DBMigrationUtils.logger.log(Level.WARNING, "DB Name is not known !!!");
                return;
            }
            if (dbname.equalsIgnoreCase("mssql")) {
                DBMigrationUtils.logger.log(Level.INFO, "Mssql DB, No need to set DB home");
                return;
            }
            String dbhome = System.getProperty("db.home");
            DBMigrationUtils.logger.log(Level.INFO, "old db.home before setting: " + dbhome);
            if (dbname.equalsIgnoreCase("mysql")) {
                dbhome = System.getProperty("mysql.home");
            }
            else if (dbname.equalsIgnoreCase("pgsql")) {
                dbhome = System.getProperty("pgsql.home");
            }
            DBMigrationUtils.logger.log(Level.INFO, "db.home going to be set: " + dbhome);
            if (dbhome != null && dbhome.trim().length() > 0) {
                System.setProperty("db.home", dbhome);
                DBMigrationUtils.logger.log(Level.INFO, "new db.home after setting: " + dbhome);
            }
        }
        catch (final Exception ex) {
            DBMigrationUtils.logger.log(Level.WARNING, "Caught exception while setting db.home System propery.");
        }
    }
    
    public static String getDBName() {
        String dbName = null;
        try {
            final String fname = getServerHome() + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties dbProps = getProperties(fname);
            if (dbProps != null) {
                final String url = dbProps.getProperty("url");
                DBMigrationUtils.logger.log(Level.INFO, "URL to find DB name is : " + url + "\t from file: " + fname);
                if (url != null) {
                    if (url.toLowerCase().contains("mysql")) {
                        dbName = "mysql";
                    }
                    else if (url.toLowerCase().contains("postgresql")) {
                        dbName = "pgsql";
                    }
                    else if (url.toLowerCase().contains("sqlserver")) {
                        dbName = "mssql";
                    }
                }
            }
        }
        catch (final Exception ex) {
            DBMigrationUtils.logger.log(Level.WARNING, "Caught exception while getting dbname...");
        }
        return dbName;
    }
    
    private static String getServerHome() throws Exception {
        return new File(System.getProperty("server.home")).getCanonicalPath();
    }
    
    public static Properties getProperties(final String path) {
        FileInputStream fis = null;
        final Properties props = new Properties();
        try {
            if (new File(path).exists()) {
                fis = new FileInputStream(path);
                props.load(fis);
            }
        }
        catch (final Exception ex) {
            DBMigrationUtils.logger.log(Level.WARNING, "Caught exception while loading properties", ex);
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e) {
                    DBMigrationUtils.logger.log(Level.WARNING, "Exception while copying file {0}", e.toString());
                }
            }
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e2) {
                    DBMigrationUtils.logger.log(Level.WARNING, "Exception while copying file {0}", e2.toString());
                }
            }
        }
        return props;
    }
    
    public static JSONObject loadJSONFile(final String jsonFilePath) throws IOException, JSONException {
        DBMigrationUtils.logger.log(Level.INFO, "Loading JSON file");
        JSONObject jsonObject = null;
        final File jsonFile = new File(jsonFilePath);
        if (jsonFile.exists()) {
            jsonObject = new JSONObject(readFileAsString(jsonFile));
            DBMigrationUtils.logger.log(Level.WARNING, "loaded existing JSON file");
        }
        return jsonObject;
    }
    
    public static String readFileAsString(final File file) throws IOException {
        DBMigrationUtils.logger.log(Level.INFO, "Reading JSON file");
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        final StringBuilder fileString = new StringBuilder();
        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                fileString.append(line);
            }
            DBMigrationUtils.logger.log(Level.FINE, "JSON file is converted to String successfully");
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
                DBMigrationUtils.logger.log(Level.WARNING, "Caught exception in closing reader :", ex);
            }
        }
        return fileString.toString();
    }
    
    public static void writeJSONFile(final JSONObject json, final File jsonFile) {
        FileWriter jsonFileWriter = null;
        try {
            jsonFile.getParentFile().mkdirs();
            jsonFileWriter = new FileWriter(jsonFile);
            final String jsonString = json.toString();
            DBMigrationUtils.logger.log(Level.INFO, "Going to write JSON : " + jsonString);
            jsonFileWriter.write(jsonString);
            DBMigrationUtils.logger.log(Level.INFO, "Finished writing JSON Object to JSON file");
        }
        catch (final IOException ioEx) {
            DBMigrationUtils.logger.log(Level.WARNING, "Caught exception in writing JSON file", ioEx);
            try {
                if (jsonFileWriter != null) {
                    jsonFileWriter.flush();
                    jsonFileWriter.close();
                }
            }
            catch (final Exception ex) {
                DBMigrationUtils.logger.log(Level.WARNING, "Caught Exception in closing FileWriter stream : ", ex);
            }
        }
        finally {
            try {
                if (jsonFileWriter != null) {
                    jsonFileWriter.flush();
                    jsonFileWriter.close();
                }
            }
            catch (final Exception ex2) {
                DBMigrationUtils.logger.log(Level.WARNING, "Caught Exception in closing FileWriter stream : ", ex2);
            }
        }
    }
    
    public static String createDynamicMasterKey(final String userPassword) {
        final StringBuilder str = new StringBuilder();
        for (int i = 0; i < userPassword.length(); ++i) {
            str.append(getRandomCharacter(userPassword.charAt(i)));
        }
        return str.toString();
    }
    
    private static char getRandomCharacter(final char c) {
        final Random r = new Random();
        if (c > '`' && c < '{') {
            final String alphabet = "abcdefghijklmnopqrstuvwxyz";
            final int length = alphabet.length();
            return alphabet.charAt(r.nextInt(length));
        }
        if (c > '@' && c < 'Z') {
            final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            final int length = alphabets.length();
            return alphabets.charAt(r.nextInt(length));
        }
        if (c > '/' && c < ':') {
            final String numeric = "1234567890";
            final int length = numeric.length();
            return numeric.charAt(r.nextInt(length));
        }
        final String symbols = "~!@#$%^&*_-+=`|\\(){}[]:;\"'<>,.?/";
        final int length = symbols.length();
        return symbols.charAt(r.nextInt(length));
    }
    
    public static NodeList writePersistenceConfiguration(final String masterKeyPassword) {
        final String fileURL = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
        NodeList propertyList = null;
        Node propertyEl = null;
        boolean isMasterKeyPropertySet = false;
        try {
            final File file = new File(fileURL);
            if (!file.exists()) {
                throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
            }
            final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document doc = docBuilder.parse(file);
            final Element root = doc.getDocumentElement();
            final NodeList connList = root.getElementsByTagName("configuration");
            for (int length = connList.getLength(), i = 0; i < length; ++i) {
                final Element connectorEl = (Element)connList.item(i);
                final String name = connectorEl.getAttribute("name");
                if (name != null && name.equalsIgnoreCase("mssql")) {
                    propertyList = connectorEl.getElementsByTagName("property");
                    if (propertyList != null && propertyList.getLength() > 0) {
                        propertyEl = propertyList.item(0);
                        if (propertyEl.getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("masterkey.password")) {
                            isMasterKeyPropertySet = true;
                        }
                    }
                }
            }
            if (!isMasterKeyPropertySet && masterKeyPassword != null) {
                final Element configuration = doc.createElement("configuration");
                configuration.setAttribute("name", "mssql");
                configuration.setAttribute("value", " ");
                root.appendChild(configuration);
                final Element property = doc.createElement("property");
                property.setAttribute("name", "masterkey.password");
                property.setAttribute("value", CryptoUtil.encrypt(masterKeyPassword));
                configuration.appendChild(property);
            }
            if (isMasterKeyPropertySet && masterKeyPassword != null) {
                propertyEl.getAttributes().getNamedItem("value").setNodeValue(CryptoUtil.encrypt(masterKeyPassword));
            }
            writeToXML(file, root);
        }
        catch (final Exception ex) {
            DBMigrationUtils.logger.log(Level.WARNING, "Exception while reading the File" + fileURL, ex);
        }
        return propertyList;
    }
    
    public static void writeToXML(final File file, final Element root) {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            final String encoding = "ISO-8859-1";
            final Transformer transformer = XMLUtils.getTransformerInstance();
            final DOMSource source = new DOMSource(root);
            final StreamResult result = new StreamResult(writer);
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("indent", "yes");
            ((Hashtable<String, String>)prop).put("encoding", encoding);
            ((Hashtable<String, String>)prop).put("method", "xml");
            transformer.setOutputProperties(prop);
            transformer.transform(source, result);
        }
        catch (final Exception e) {
            DBMigrationUtils.logger.log(Level.SEVERE, "Exception while writing the changes in custom_config.xml", e);
        }
    }
    
    public static void createBackupFile(final String srcFile, final String destDir) {
        Path srcFilePath = null;
        Path destPath = null;
        try {
            srcFilePath = Paths.get(srcFile, new String[0]);
            if (!new File(destDir).exists()) {
                new File(destDir).mkdir();
                DBMigrationUtils.logger.log(Level.INFO, "Creating new Dir...");
            }
            destPath = Paths.get(destDir, new String[0]);
            Files.copy(srcFilePath, destPath.resolve(srcFilePath.getFileName()), new CopyOption[0]);
        }
        catch (final Exception ex) {
            DBMigrationUtils.logger.log(Level.SEVERE, "Exception while copy the file :" + srcFilePath + " to: " + destPath + " ", ex);
        }
    }
    
    public static void deleteBackUpFile(final String fileDir) {
        final File file = new File(fileDir);
        try {
            if (file.exists()) {
                DBMigrationUtils.logger.log(Level.INFO, " Going to delete file:  ", file);
                FileUtils.deleteDirectory(file);
                DBMigrationUtils.logger.log(Level.INFO, " file deleted successfully ");
            }
            else {
                DBMigrationUtils.logger.log(Level.INFO, " file does not exist!", file);
            }
        }
        catch (final IOException e) {
            DBMigrationUtils.logger.log(Level.SEVERE, "Exception occured while deleting the directory" + file, e);
        }
    }
    
    public static void checkDBMigrationConfBackUP() {
        if (ChangeDBServer.ismigrationRevertFound()) {
            DBMigrationUtils.logger.log(Level.INFO, "Migration revert found");
            final String backupFolder = System.getProperty("backupfile.path", DBMigrationUtils.BACKUPPATH);
            final String backupPath = backupFolder + File.separator + "customer-config.xml";
            final String destinationPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
            final Path sourceDirectory = Paths.get(backupPath, new String[0]);
            final Path targetDirectory = Paths.get(destinationPath, new String[0]);
            try {
                if (new File(backupPath).exists()) {
                    DBMigrationUtils.logger.log(Level.INFO, "Going to replace the " + targetDirectory + " with " + sourceDirectory);
                    Files.copy(sourceDirectory, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
                    deleteBackUpFile(backupFolder);
                    DBMigrationUtils.logger.log(Level.INFO, "Customer-config file reverted successfully");
                }
            }
            catch (final IOException e) {
                DBMigrationUtils.logger.log(Level.SEVERE, "Excetion occured while replace the customer-config file with the backup file", e);
            }
        }
    }
    
    public static String getMssqlDBUrl(final String host, final String port, final String domain, final boolean isWinAuthType, final boolean isNTLMEnabled, final String dataBaseName, final String dbURLFiller) {
        String dbUrl = null;
        if (dataBaseName != null) {
            if (isWinAuthType) {
                if (isNTLMEnabled) {
                    dbUrl = "jdbc:sqlserver://" + host + ":" + port + dbURLFiller + dataBaseName + ";Domain=" + domain + ";authenticationScheme=NTLM" + ";integratedSecurity=true";
                }
                else {
                    dbUrl = "jdbc:sqlserver://" + host + ":" + port + dbURLFiller + dataBaseName + ";Domain=" + domain + ";integratedSecurity=true";
                }
            }
            else {
                dbUrl = "jdbc:sqlserver://" + host + ":" + port + dbURLFiller + dataBaseName;
            }
        }
        else if (isWinAuthType) {
            if (isNTLMEnabled) {
                dbUrl = "jdbc:sqlserver://" + host + ":" + port + ";Domain=" + domain + ";authenticationScheme=NTLM" + ";integratedSecurity=true";
            }
            else {
                dbUrl = "jdbc:sqlserver://" + host + ":" + port + ";Domain=" + domain + ";integratedSecurity=true";
            }
        }
        else {
            dbUrl = "jdbc:sqlserver://" + host + ":" + port;
        }
        dbUrl = addSSLConnProps(dbUrl);
        return dbUrl;
    }
    
    private static String addSSLConnProps(final String dbURL) {
        return dbURL + ";ssl=request";
    }
    
    public static void checkAndWaitForPermissionForBakBeforeMigration(final String server, final String port, final String domain, final boolean isWinAuthType, final boolean isNTLMEnabled, final String dbName, final String driver, final String user, final String pwd) {
        final String messageText = BackupRestoreUtil.getString("desktopcentral.tools.common.no_backup_permission", (Locale)null);
        final String title = BackupRestoreUtil.getString("desktopcentral.tools.common.warning_message.title", (Locale)null);
        final String close = BackupRestoreUtil.getString("desktopcentral.tools.common.close", (Locale)null);
        final String retry = BackupRestoreUtil.getString("desktopcentral.tools.common.retry", (Locale)null);
        Connection connection = null;
        try {
            final String dbUrl = getMssqlDBUrl(server, port, domain, isWinAuthType, isNTLMEnabled, dbName, ";databaseName=");
            DBMigrationUtils.logger.log(Level.INFO, dbUrl);
            Class.forName(driver);
            connection = DriverManager.getConnection(dbUrl, user, pwd);
            final JEditorPane message = new JEditorPane("text/html", messageText);
            message.setEditable(false);
            message.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        try {
                            Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                        }
                        catch (final IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            while (!ScheduleDBBackupUtil.isMssqlDBPermissionsAvailableToTakeBakBackup(dbName, connection)) {
                DBMigrationUtils.logger.log(Level.INFO, "User " + user + " doesn't have BAK backup permissions in db " + dbName + ". Waiting for user response..");
                final int res = JOptionPane.showOptionDialog(ChangeDBServerGUI.getInstance(), message, title, 0, 0, null, new String[] { retry, close }, retry);
                if (res == 1) {
                    System.exit(0);
                }
            }
        }
        catch (final Exception e) {
            DBMigrationUtils.logger.log(Level.SEVERE, "Exception while checking for backup permissions: " + e.getMessage());
            final int res2 = JOptionPane.showOptionDialog(ChangeDBServerGUI.getInstance(), e.getMessage(), "Exception while checking for backup permissions", 0, 0, null, new String[] { close }, close);
            System.exit(0);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e) {
                DBMigrationUtils.logger.log(Level.WARNING, "Exception while closing connection: " + e.getMessage());
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                DBMigrationUtils.logger.log(Level.WARNING, "Exception while closing connection: " + e2.getMessage());
            }
        }
        DBMigrationUtils.logger.log(Level.INFO, " User : " + user + " have permission for BAK backup");
    }
    
    static {
        ENABLE_SECURED_CONN = System.getProperty("enable.secured.connection", "true");
        DBMigrationUtils.logger = Logger.getLogger("DBMigrationAction");
        BACKUPPATH = System.getProperty("server.home") + File.separator + "backup";
    }
}
