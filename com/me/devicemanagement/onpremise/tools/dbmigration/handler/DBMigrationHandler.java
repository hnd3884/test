package com.me.devicemanagement.onpremise.tools.dbmigration.handler;

import java.util.Hashtable;
import java.net.InetAddress;
import java.io.FilenameFilter;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.tools.dbmigration.utils.DBMigrationUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.util.Properties;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import java.io.BufferedWriter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.io.FileReader;
import com.adventnet.persistence.ConfigurationParser;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.logging.Level;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.logging.Logger;

public class DBMigrationHandler
{
    private static Logger logger;
    
    public void updateDBConfigFile(final String server, final String host, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final String user, final String pwd, final String serverdir, final String instanceName, final String dataBaseName, final String driver, final String exceptionSorter) throws Exception {
        if (!DBMigrationUtil.getDestDBType().equals("MSSQL")) {
            DBMigrationHandler.logger.log(Level.INFO, "Working with startDB and stopDB files");
        }
        if (DBMigrationUtil.getDestDBType().equals("POSTGRES")) {
            DBMigrationHandler.logger.log(Level.INFO, "Going to check postgres_extn.conf");
            this.checkPostgresExtnConf();
        }
        if (host != null && !host.trim().equals("")) {
            final String connection_url = this.createURL(server, dataBaseName, host, port, isWinAuthType, isNTLMEnabled, domain);
            this.writeDatabaseParams(server, user, pwd, connection_url, driver, exceptionSorter);
            return;
        }
        throw new Exception("Invalid host name!!!");
    }
    
    private void writeDatabaseParams(final String server, final String usrName, final String pswd, final String connection_url, final String driver, final String exceptionSorter) throws Exception {
        final String activeDBName = this.getActiveDBName();
        if (System.getProperty("server.home") == null) {
            throw new Exception("System Property \"server.home\" not configured");
        }
        final String fileURL = System.getProperty("databaseparams.file", System.getProperty("server.home") + "/conf/database_params.conf");
        final File file = new File(fileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
        }
        if (!server.equalsIgnoreCase(activeDBName)) {
            final String sBackupDir = System.getProperty("server.home") + File.separator + "dbmigration";
            if (!new File(sBackupDir).exists()) {
                new File(sBackupDir).mkdirs();
            }
            final String backupFileURL = sBackupDir + File.separator + "database_params_backup_" + activeDBName + ".conf";
            copyfile(fileURL, backupFileURL);
        }
        PersistenceInitializer.loadPersistenceConfigurations();
        this.findAndReplaceStringInFile(fileURL, "r_username.*=.*", "");
        this.findAndReplaceStringInFile(fileURL, "drivername.*=.*", "drivername=" + driver);
        this.findAndReplaceStringInFile(fileURL, "username.*=.*", "username=" + usrName);
        this.findAndReplaceStringInFile(fileURL, ".*password.*=.*", "password=" + pswd);
        this.findAndReplaceStringInFile(fileURL, "url.*=.*", "url=" + connection_url);
        this.findAndReplaceStringInFile(fileURL, "exceptionsorterclassname.*=.*", "exceptionsorterclassname=" + exceptionSorter);
    }
    
    public String getActiveDBName() {
        try {
            final String fileName = System.getProperty("server.home") + "/conf/Persistence/persistence-configurations.xml";
            final ConfigurationParser parser = new ConfigurationParser(fileName);
            return String.valueOf(parser.getConfigurationValue("DBName"));
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "Exception while getting Active DB Name {0}", ex);
            return null;
        }
    }
    
    private void findAndReplaceStringInFile(final String fileName, final String findStr, final String replaceStr) throws Exception {
        FileReader filereader = null;
        FileWriter filewriter = null;
        try {
            filereader = new FileReader(fileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = filereader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            filereader.close();
            String finalStr = strBuilder.toString();
            final Pattern findStrPattern = Pattern.compile(findStr);
            final Matcher matcher = findStrPattern.matcher(finalStr);
            if (matcher.find()) {
                finalStr = finalStr.replaceAll(findStr, replaceStr);
            }
            else {
                finalStr = finalStr.concat("\n\n" + replaceStr);
            }
            filewriter = new FileWriter(fileName, false);
            filewriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception in findAndReplaceStringInFile() for fileName: " + fileName, ex);
            throw ex;
        }
        finally {
            if (filereader != null) {
                filereader.close();
            }
            if (filewriter != null) {
                filewriter.close();
            }
        }
    }
    
    private static void copyfile(final String sourcerFilePath, final String destinationFilePath) {
        try {
            final File sourcerFile = new File(sourcerFilePath);
            final File destinationFile = new File(destinationFilePath);
            final InputStream in = new FileInputStream(sourcerFile);
            final OutputStream out = new FileOutputStream(destinationFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            DBMigrationHandler.logger.log(Level.INFO, "File [{0}] has been backed up.", sourcerFilePath);
        }
        catch (final Exception e) {
            DBMigrationHandler.logger.log(Level.WARNING, "Exception while copying file {0}", e.toString());
        }
    }
    
    private void writePersistenceConfiguration(final String dbName, final String host) throws Exception {
        final String activeDBName = this.getActiveDBName();
        final String fileURL = System.getProperty("server.home") + "/conf/customer-config.xml";
        final File file = new File(fileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
        }
        if (!dbName.equalsIgnoreCase(activeDBName)) {
            final String sBackupDir = System.getProperty("server.home") + File.separator + "dbmigration";
            if (!new File(sBackupDir).exists()) {
                new File(sBackupDir).mkdirs();
            }
            final String backupFileURL = sBackupDir + File.separator + "customer-config_backup_" + activeDBName + ".xml";
            copyfile(fileURL, backupFileURL);
        }
        final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
        final Document doc = docBuilder.parse(file);
        final Element root = doc.getDocumentElement();
        final NodeList connList = root.getElementsByTagName("configuration");
        for (int length = connList.getLength(), i = 0; i < length; ++i) {
            final Element connectorEl = (Element)connList.item(i);
            final String name = connectorEl.getAttribute("name");
            if (name != null && name.equals("DBName")) {
                connectorEl.setAttribute("value", dbName);
            }
            if (name != null && name.equals("StartDBServer")) {
                connectorEl.setAttribute("value", this.startDBServer(dbName, host));
            }
            if (name != null && name.equals("DSAdapter")) {
                connectorEl.setAttribute("value", dbName);
            }
        }
        this.writeToXML(file, root);
    }
    
    private String startDBServer(final String serverType, final String hostName) {
        if ("mssql".equalsIgnoreCase(serverType)) {
            return "false";
        }
        boolean remoteDB = false;
        if (hostName != null && !hostName.equalsIgnoreCase("localhost") && !hostName.equalsIgnoreCase("127.0.0.1")) {
            remoteDB = true;
        }
        return String.valueOf(!remoteDB);
    }
    
    private void writeToXML(final File file, final Element root) throws Exception {
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
    
    private String createURL(final String server, final String dataBaseName, final String host, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain) {
        String url = null;
        final String dbURLFiller = ";databaseName=";
        if (server.equalsIgnoreCase("mysql")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + dataBaseName + "?useUnicode=true&characterEncoding=UTF-8&jdbcCompliantTruncation=false";
        }
        else if (server.equalsIgnoreCase("postgres")) {
            url = "jdbc:postgresql://" + host + ":" + port + "/" + dataBaseName;
        }
        else if (server.equalsIgnoreCase("mssql")) {
            url = DBMigrationUtils.getMssqlDBUrl(host, port, domain, isWinAuthType, isNTLMEnabled, dataBaseName, dbURLFiller);
        }
        DBMigrationHandler.logger.log(Level.INFO, "DB Url is = ", url);
        return url;
    }
    
    public void isAuthenticatedUsersAvailable(final String homeDir, final String dsName) {
        try {
            if (dsName.equalsIgnoreCase("POSTGRES")) {
                final String binDir = homeDir + File.separator + "bin";
                final String dbHome = homeDir + File.separator + "pgsql" + File.separator + "data";
                final String systemRoot = System.getenv("SystemRoot") + File.separator + "system32";
                DBMigrationHandler.logger.log(Level.INFO, "System Root Location : " + systemRoot);
                final String icaclsEXE = systemRoot + File.separator + "icacls.exe";
                if (new File(icaclsEXE).exists()) {
                    DBMigrationHandler.logger.log(Level.INFO, "Executable exe Availbale in : " + icaclsEXE);
                    final File filepath = new File(binDir);
                    final List<String> command = new ArrayList<String>();
                    PrintWriter out = null;
                    BufferedReader in = null;
                    try {
                        command.add("icacls.exe");
                        command.add(dbHome);
                        command.add("/findsid");
                        command.add("*s-1-5-11");
                        final ProcessBuilder processBuilder = new ProcessBuilder(command);
                        processBuilder.directory(filepath);
                        DBMigrationHandler.logger.log(Level.INFO, "COMMAND : {0}", processBuilder.command());
                        final Process process = processBuilder.start();
                        in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        final File dbDataPrivilegeFile = new File(homeDir + File.separator + "logs" + File.separator + "dbdata_privilege.txt");
                        if (dbDataPrivilegeFile.exists()) {
                            out = new PrintWriter(new FileOutputStream(dbDataPrivilegeFile, true));
                            out.append("\n***************************************Start********************************************\n");
                        }
                        else {
                            out = new PrintWriter(dbDataPrivilegeFile);
                            out.write("***************************************Start********************************************\n");
                        }
                        String outputLine = null;
                        String isAuthenticatedUserAvailable = null;
                        boolean flag = false;
                        if (in != null) {
                            while ((outputLine = in.readLine()) != null) {
                                if (!flag) {
                                    isAuthenticatedUserAvailable = outputLine;
                                    flag = true;
                                }
                                final Calendar cal = Calendar.getInstance();
                                cal.getTime();
                                final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                                out.append(sdf.format(cal.getTime()) + " : " + outputLine + "\n");
                            }
                            DBMigrationHandler.logger.log(Level.INFO, "icacls.exe command executed successfully.");
                            out.append("***************************************End**********************************************");
                        }
                        else {
                            DBMigrationHandler.logger.log(Level.INFO, "icacls.exe command execution failed.");
                        }
                        final Properties ppmUpgradeProps = new Properties();
                        DBMigrationHandler.logger.log(Level.INFO, "Is Authenricated Users SID Availale : " + isAuthenticatedUserAvailable);
                        if (isAuthenticatedUserAvailable != null && !isAuthenticatedUserAvailable.startsWith("SID Found")) {
                            DBMigrationHandler.logger.log(Level.INFO, "Authenticated Users Privilege is Not availbale in data folder");
                            this.setAuthenticateUsers(homeDir, dbDataPrivilegeFile);
                            DBMigrationHandler.logger.log(Level.INFO, "Authenticated Users Privilege is set to data folder successfully..");
                        }
                    }
                    catch (final Exception ex) {
                        DBMigrationHandler.logger.log(Level.WARNING, "Exception in icacls.exe command execution : ", ex);
                        try {
                            if (out != null) {
                                out.close();
                            }
                            if (in != null) {
                                in.close();
                            }
                        }
                        catch (final Exception ex) {
                            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while closing stream objects ", ex);
                        }
                    }
                    finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                            if (in != null) {
                                in.close();
                            }
                        }
                        catch (final Exception ex2) {
                            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while closing stream objects ", ex2);
                        }
                    }
                }
                else {
                    DBMigrationHandler.logger.log(Level.WARNING, "icacls.exe file does not exist in : ", icaclsEXE);
                }
            }
        }
        catch (final Exception ex3) {
            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while Finding Authenticated Users privilege for data directory ", ex3);
        }
    }
    
    private void setAuthenticateUsers(final String homeDir, final File dbDataPrivilegeFile) {
        final String binDir = homeDir + File.separator + "bin";
        final File filepath = new File(binDir);
        final List<String> command = new ArrayList<String>();
        DBMigrationHandler.logger.log(Level.INFO, "Going to set AuthenticatedUsers Prvilege for Data Folder");
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            command.add("cmd.exe");
            command.add("/c");
            command.add("initPgsql.bat");
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(filepath);
            DBMigrationHandler.logger.log(Level.INFO, "COMMAND : {0}", processBuilder.command());
            final Process process = processBuilder.start();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            out = new PrintWriter(new FileOutputStream(dbDataPrivilegeFile, true));
            out.append("\n***************************************Start of initPgsql.bat Execution ********************************************\n");
            String outputLine = null;
            if (in != null) {
                while ((outputLine = in.readLine()) != null) {
                    out.append(outputLine + "\n");
                }
                out.append("***************************************End of initPgsql.bat Execution ***********************************************");
                out.close();
                DBMigrationHandler.logger.log(Level.INFO, "initPgsql.bat command executed successfully. ");
                in.close();
            }
            else {
                DBMigrationHandler.logger.log(Level.INFO, "initPgsql.bat command execution failed.");
            }
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while set Authenticated Users privilege for data directory using initpgsql.bat ", ex);
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex) {
                DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while closing stream objects ", ex);
            }
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex2) {
                DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while closing stream objects ", ex2);
            }
        }
    }
    
    private void checkPostgresExtnConf() {
        String srcPath = null;
        String destPath = null;
        try {
            final String serverHome = System.getProperty("server.home");
            srcPath = serverHome + File.separator + "conf" + File.separator + "postgres_ext.conf";
            destPath = serverHome + File.separator + "pgsql" + File.separator + "ext_conf" + File.separator + "postgres_ext.conf";
            if (new File(destPath).exists()) {
                DBMigrationHandler.logger.log(Level.INFO, "postgres_ext.conf file is already present in pgsql ext_conf folder");
            }
            else {
                DBMigrationHandler.logger.log(Level.INFO, "Going to copy postgres_ext.conf from conf directory");
                copyfile(srcPath, destPath);
                DBMigrationHandler.logger.log(Level.INFO, "postgres_ext.conf file copied");
            }
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "#############Exception occurs when copying postgres_ext.conf file############# ", ex);
        }
    }
    
    public void updateMysqlINIFiles() {
        final String homeDir = System.getProperty("server.home");
        final String mysqlNormalINIFile = homeDir + File.separator + "mysql" + File.separator + "mysql-normal.ini";
        final String mysqlHugeINIFile = homeDir + File.separator + "mysql" + File.separator + "mysql-huge.ini";
        final String mysqlLargeINIFile = homeDir + File.separator + "mysql" + File.separator + "mysql-large.ini";
        this.updateMysqlINIFile(mysqlNormalINIFile);
        this.updateMysqlINIFile(mysqlHugeINIFile);
        this.updateMysqlINIFile(mysqlLargeINIFile);
    }
    
    private void updateMysqlINIFile(final String filePath) {
        if (!new File(filePath).exists()) {
            DBMigrationHandler.logger.log(Level.INFO, filePath + " File not found. Ignoring timeout changes");
            return;
        }
        DBMigrationHandler.logger.log(Level.INFO, "Going to add timeout properties in Mysql");
        String writeTimeout = null;
        String connectionTimeoutKey = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            final String writeTimeoutKey = "net_write_timeout";
            connectionTimeoutKey = "connect_timeout";
            final Properties fileProps = new Properties();
            fis = new FileInputStream(filePath);
            fileProps.load(fis);
            writeTimeout = fileProps.getProperty(writeTimeoutKey);
            if (writeTimeout == null) {
                final Properties newProps = new Properties();
                newProps.setProperty(writeTimeoutKey, "600");
                newProps.setProperty(connectionTimeoutKey, "50");
                fos = new FileOutputStream(filePath, true);
                newProps.store(fos, "Added this entry for avoiding timeout errors");
                DBMigrationHandler.logger.log(Level.INFO, "Successfully added the timeout property");
            }
            else {
                DBMigrationHandler.logger.log(Level.INFO, "Timeout property already exists");
            }
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while updating " + filePath, ex);
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex) {
                DBMigrationHandler.logger.log(Level.WARNING, "Exception while closing streams. Can be ignored." + ex);
            }
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
            catch (final Exception ex2) {
                DBMigrationHandler.logger.log(Level.WARNING, "Exception while closing streams. Can be ignored." + ex2);
            }
        }
    }
    
    public void removeJunkPIDFiles() {
        final String homeDir = System.getProperty("server.home");
        final String mysqlDataPath = homeDir + File.separator + "mysql" + File.separator + "data";
        final File dataFolder = new File(mysqlDataPath);
        try {
            if (!dataFolder.exists()) {
                DBMigrationHandler.logger.log(Level.INFO, mysqlDataPath + " Directory not found. Ignoring junk pid files cleanup");
                return;
            }
            DBMigrationHandler.logger.log(Level.INFO, "Going to remove junk pid files");
            final FilenameFilter pidFileFilter = new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    final String filePath = dir.getAbsolutePath() + File.separator + name;
                    return !new File(filePath).isDirectory() && (name.endsWith(".pid") || name.endsWith(".PID")) && DBMigrationHandler.this.getHostName() != null && !name.equalsIgnoreCase(DBMigrationHandler.this.getHostName() + ".pid");
                }
            };
            final File[] listFiles;
            final File[] fileList = listFiles = dataFolder.listFiles(pidFileFilter);
            for (final File pidFile : listFiles) {
                if (pidFile.delete()) {
                    DBMigrationHandler.logger.log(Level.INFO, pidFile.getName() + " is deleted successfully");
                }
            }
            DBMigrationHandler.logger.log(Level.INFO, "junk pid files removed successfully");
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "Exception occurs while deleting mysql pid files", ex);
        }
    }
    
    private String getHostName() {
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (final Exception ex) {
            DBMigrationHandler.logger.log(Level.WARNING, "Caught exception while getting host name", ex);
        }
        return hostName;
    }
    
    static {
        DBMigrationHandler.logger = Logger.getLogger("DBMigrationAction");
    }
}
