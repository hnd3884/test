package com.me.devicemanagement.onpremise.server.util;

import java.sql.ResultSetMetaData;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.HashMap;
import java.util.Hashtable;
import com.adventnet.db.adapter.BackupResult;
import java.util.Date;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.db.adapter.BackupDBParams;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Map;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;

public class DBUtil extends com.me.devicemanagement.framework.server.util.DBUtil
{
    private static Logger scheduleDBBackupLogger;
    private static final String DEFAULT_BACKUP_DIR;
    
    public static int getRecordCount(final String query) {
        int recordCount = 0;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = relapi.getConnection();
            statement = conn.createStatement();
            rs = relapi.executeQueryForSQL(query, (Map)null, statement);
            while (rs.next()) {
                final Object value = rs.getObject(1);
                if (value != null) {
                    recordCount = Integer.valueOf(value.toString());
                }
            }
        }
        catch (final QueryConstructionException ex) {
            ex.printStackTrace();
        }
        catch (final SQLException ex2) {
            ex2.printStackTrace();
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex3) {}
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex4) {}
        }
        return recordCount;
    }
    
    public static boolean getMySQLAdminPingStatus() throws IOException {
        boolean pingStatus = false;
        final String cmd2;
        String cmd1;
        final String home = cmd1 = (cmd2 = new File(System.getProperty("server.home")).getCanonicalPath().replaceAll("\\\\", "/"));
        final Properties dbProps = PersistenceInitializer.getDefaultDBProps();
        final String mysqlstr = dbProps.getProperty("url");
        final String passwd = dbProps.getProperty("password");
        final int port = PersistenceUtil.getPort(mysqlstr);
        cmd1 = cmd1 + "\\mysql\\bin\\mysqladmin --no-defaults -u root --port=" + port;
        if (passwd != null && !passwd.equals("")) {
            cmd1 = cmd1 + " --password=" + passwd;
        }
        cmd1 += " ping";
        final String[] command = { home, "\\mysql\\bin\\mysqladmin", "--no", "-defaults", "-u", "root", "--port=" + port, "--password=" + passwd, "ping" };
        final ProcessBuilder builder = new ProcessBuilder(command);
        final Process p = builder.start();
        final BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String str = buf.readLine();
        String result = "";
        while (str != null) {
            result = result + str + "\n";
            str = buf.readLine();
        }
        DBUtil.logger.log(Level.INFO, "Result of the Command : " + result);
        if (result.indexOf("mysqld is alive") != -1) {
            pingStatus = true;
        }
        DBUtil.logger.log(Level.INFO, "getMySQLAdminPingStatus :: returning :: " + pingStatus);
        return pingStatus;
    }
    
    public static boolean checkDBStatus() {
        final Properties dbProps = PersistenceInitializer.getDefaultDBProps();
        final String jdbcURL = dbProps.getProperty("url");
        final Properties properties = splitConnectionURL(jdbcURL);
        DBUtil.logger.log(Level.INFO, "DB properties :: {0}", new Object[] { properties });
        boolean isServerStarted = false;
        Connection c = null;
        try {
            Class.forName(dbProps.getProperty("drivername"));
        }
        catch (final ClassNotFoundException cnfe) {
            DBUtil.logger.log(Level.WARNING, "Suitable driver for this DB is not specified in database_params.conf ", cnfe);
            return isServerStarted;
        }
        for (int count = 0; count < 3; ++count) {
            try {
                c = RelationalAPI.getInstance().getConnection();
                isServerStarted = true;
            }
            catch (final Exception e) {
                e.printStackTrace();
                DBUtil.logger.log(Level.INFO, "Waiting for 3 seconds ...");
                try {
                    Thread.sleep(3000L);
                }
                catch (final InterruptedException ex) {}
            }
            finally {
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final Exception ex2) {}
                }
            }
        }
        return isServerStarted;
    }
    
    private static Properties splitConnectionURL(String databaseURL) {
        final Properties mapProperties = new Properties();
        String server = null;
        String fileName = null;
        String port = "";
        final String DB_PROTOCOL = "jdbc:";
        if (databaseURL.startsWith(DB_PROTOCOL)) {
            databaseURL = databaseURL.substring(DB_PROTOCOL.length());
            final int nextIndex = databaseURL.indexOf(":");
            databaseURL = databaseURL.substring(nextIndex + 1);
        }
        databaseURL = databaseURL.trim();
        char hostSepChar;
        char portSepChar;
        if (databaseURL.startsWith("//")) {
            databaseURL = databaseURL.substring(2);
            hostSepChar = '/';
            portSepChar = ':';
        }
        else {
            hostSepChar = ':';
            portSepChar = '/';
        }
        final int sep = databaseURL.indexOf(hostSepChar);
        if (sep <= 0) {
            return mapProperties;
        }
        server = databaseURL.substring(0, sep);
        fileName = databaseURL.substring(sep + 1);
        if (fileName.indexOf("?") != -1) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        final int portSep = server.indexOf(portSepChar);
        if (portSep > 0) {
            port = server.substring(portSep + 1);
            server = server.substring(0, portSep);
            ((Hashtable<String, String>)mapProperties).put("Port", port);
        }
        ((Hashtable<String, String>)mapProperties).put("Server", server);
        ((Hashtable<String, String>)mapProperties).put("DBName", fileName);
        return mapProperties;
    }
    
    public static Map getDBServerProperties() {
        final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
        Map dbServerMap = new LinkedHashMap();
        dbServerMap.put("db.name", currentDB);
        if ("mysql".equals(currentDB)) {
            dbServerMap = getMYSQLDBProperties(dbServerMap);
        }
        else if ("mssql".equals(currentDB)) {
            dbServerMap = getMSSQLDBProperties(dbServerMap);
        }
        else if ("postgres".equals(currentDB)) {
            dbServerMap = getPostgreSQLDBProperties(dbServerMap);
        }
        return dbServerMap;
    }
    
    private static Map getMYSQLDBProperties(final Map dbServerProperties) {
        final String query = "SELECT SUBSTRING_INDEX(USER(),'@',-1) as hostname, SUBSTRING_INDEX(USER(),'@',1) as user,SUBSTRING_INDEX(version(),'-',1) as version, SUBSTRING_INDEX(version(),'-',-3) as edition";
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = relapi.getConnection();
            statement = conn.createStatement();
            rs = relapi.executeQueryForSQL(query, (Map)null, statement);
            while (rs.next()) {
                rs.getMetaData().getColumnCount();
                dbServerProperties.put("db.host.name", rs.getObject(1));
                dbServerProperties.put("db.user.name", rs.getObject(2));
                dbServerProperties.put("db.version", rs.getObject(3));
                dbServerProperties.put("db.edition", rs.getObject(4));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex) {}
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return dbServerProperties;
    }
    
    private static Map getMSSQLDBProperties(final Map dbServerProperties) {
        final String query = "SELECT SERVERPROPERTY ('MachineName') as hostname, SYSTEM_USER, SERVERPROPERTY('productversion') as version, SERVERPROPERTY ('edition') as edition,  @@SERVERNAME as instance, LEFT(@@version, CHARINDEX(' - ', @@version)) ProductName";
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = relapi.getConnection();
            statement = conn.createStatement();
            rs = relapi.executeQueryForSQL(query, (Map)null, statement);
            while (rs.next()) {
                rs.getMetaData().getColumnCount();
                dbServerProperties.put("db.host.name", rs.getObject(1));
                dbServerProperties.put("db.user.name", rs.getObject(2));
                dbServerProperties.put("db.version", rs.getObject(3));
                dbServerProperties.put("db.edition", rs.getObject(4));
                dbServerProperties.put("db.instance.name", rs.getObject(5));
                dbServerProperties.put("db.product.name", rs.getObject(6));
            }
        }
        catch (final Exception e) {
            DBUtil.logger.log(Level.WARNING, "Exception while getting MSSQL Database Props : ", e);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex) {}
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return dbServerProperties;
    }
    
    private static Map getPostgreSQLDBProperties(final Map dbServerProperties) {
        final String query = "SELECT USER as user, SPLIT_PART(VERSION(), ',', 1) as version , SPLIT_PART(VERSION(), ',', 3) as architecture";
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Properties dbProps = PersistenceInitializer.getDefaultDBProps();
        final String jdbcURL = dbProps.getProperty("url");
        dbProps = splitConnectionURL(jdbcURL);
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = relapi.getConnection();
            statement = conn.createStatement();
            rs = relapi.executeQueryForSQL(query, (Map)null, statement);
            while (rs.next()) {
                rs.getMetaData().getColumnCount();
                dbServerProperties.put("db.host.name", dbProps.getProperty("Server"));
                dbServerProperties.put("db.user.name", rs.getObject(1));
                dbServerProperties.put("db.version", rs.getObject(2));
                dbServerProperties.put("db.arch", rs.getObject(3));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex3) {
                ex3.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex4) {
                ex4.printStackTrace();
            }
        }
        return dbServerProperties;
    }
    
    private static String getProductName(final String productVersion) {
        String productName = null;
        if (productVersion != null) {
            final String[] productVersions = productVersion.split("\\.");
            if (productVersions.length > 2) {
                final int majorVersion = Integer.parseInt(productVersions[0]);
                final int minorVersion = Integer.parseInt(productVersions[1]);
                if (majorVersion == 8) {
                    productName = "SQL Server 2000";
                }
                else if (majorVersion == 9) {
                    productName = "SQL Server 2005";
                }
                else if (majorVersion == 10) {
                    productName = "SQL Server 2008";
                    if (minorVersion == 5) {
                        productName = "SQL Server 2008 R2";
                    }
                }
                else {
                    productName = "Unknown";
                }
            }
        }
        return productName;
    }
    
    public static void enableFKConstraint() {
        String query = null;
        final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
        if ("mysql".equals(currentDB)) {
            query = "set SESSION FOREIGN_KEY_CHECKS = 1";
        }
        else if ("mssql".equals(currentDB)) {
            query = "exec sp_MSforeachtable 'ALTER TABLE ? CHECK CONSTRAINT ALL'";
        }
        DBUtil.logger.log(Level.INFO, "enableFKConstraint : Query to set FK contraint : " + query);
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            relapi.execute(query);
            DBUtil.logger.log(Level.INFO, "enableFKConstraint : Successfully ENABLED FK Constraint");
        }
        catch (final Exception ex) {
            DBUtil.logger.log(Level.WARNING, "enableFKConstraint : Exception while ENABLING FK Constraint", ex);
        }
    }
    
    public static synchronized boolean backupDB(String backupDir) throws Exception {
        boolean copyStatus = false;
        if (backupDir == null) {
            backupDir = DBUtil.DEFAULT_BACKUP_DIR;
        }
        final File file = new File(backupDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        RelationalAPI relAPI = null;
        Connection conn = null;
        try {
            DBUtil.scheduleDBBackupLogger.log(Level.WARNING, "BackingUp DB Started");
            relAPI = RelationalAPI.getInstance();
            conn = relAPI.getConnection();
            final Date today = Calendar.getInstance().getTime();
            final SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmm");
            final String backupFileName = formatter.format(today);
            final BackupDBParams backupProps = new BackupDBParams();
            backupProps.zipFileName = backupFileName;
            backupProps.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP;
            backupProps.backupFolder = new File(backupDir);
            final BackupResult backupResult = relAPI.getDBAdapter().getBackupHandler().doBackup(backupProps);
            int backUpStatus = 1;
            if (backupResult.getBackupStatus() != BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED) {
                backUpStatus = -6;
            }
            DBUtil.scheduleDBBackupLogger.log(Level.WARNING, "backupDB " + backUpStatus);
            if (backUpStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue()) {
                copyStatus = true;
                DBUtil.logger.log(Level.WARNING, "BackedUp Successfully");
            }
            else if (backUpStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.getValue()) {
                copyStatus = false;
            }
            else if (backUpStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_IN_PROGRESS.getValue()) {
                copyStatus = false;
                DBUtil.logger.log(Level.WARNING, "\nAlready a backup / restore process is started, so please try again after some time.");
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                DBUtil.logger.log(Level.WARNING, "Problem while closing connection.", e);
            }
        }
        return copyStatus;
    }
    
    public static String getMySQLServerVersion() {
        String mysqlVersion = "";
        try {
            final String queryStr = "SELECT version()";
            final RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = null;
            Statement statement = null;
            ResultSet rs = null;
            try {
                conn = relapi.getConnection();
                statement = conn.createStatement();
                rs = relapi.executeQueryForSQL(queryStr, (Map)new Hashtable(), statement);
                while (rs.next()) {
                    mysqlVersion = (String)rs.getObject(1);
                }
            }
            catch (final Exception ex) {
                throw ex;
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (final Exception ex3) {}
            }
        }
        catch (final Exception ex2) {
            DBUtil.logger.log(Level.WARNING, "Caught exception while retrieving mysql version.", ex2);
        }
        return mysqlVersion;
    }
    
    public HashMap getDBPropertiesFromFile() throws Exception {
        final String sDataBaseParamsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final Properties dbProps = FileAccessUtil.readProperties(sDataBaseParamsFile);
        final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
        final HashMap hash = new HashMap();
        if (dbProps != null) {
            final String connectionUrl = dbProps.getProperty("url");
            final String username = dbProps.getProperty("username");
            final String password = dbProps.getProperty("password");
            if ("mysql".equals(currentDB) || "postgres".equals(currentDB)) {
                String[] tmp = connectionUrl.split(":");
                hash.put("HOST", tmp[2].substring(2));
                tmp = tmp[3].split("/");
                hash.put("PORT", tmp[0]);
                if (!tmp[1].contains("?")) {
                    hash.put("DATABASE", tmp[1]);
                }
                else {
                    hash.put("DATABASE", tmp[1].substring(0, tmp[1].indexOf("?")));
                }
            }
            else if ("mssql".equals(currentDB)) {
                String[] tmp = connectionUrl.split(":");
                hash.put("HOST", tmp[2].substring(2));
                tmp = tmp[3].split(";");
                hash.put("PORT", tmp[0]);
                for (int i = 1; i < tmp.length; ++i) {
                    final String[] tmp2 = tmp[i].split("=");
                    if ("DatabaseName".equalsIgnoreCase(tmp2[0])) {
                        hash.put("DATABASE", tmp2[1]);
                    }
                    else if ("Domain".equals(tmp2[0])) {
                        hash.put("DOMAIN_NAME", tmp2[1]);
                    }
                    else if ("authenticationScheme=NTLM".equalsIgnoreCase(tmp2[0])) {
                        hash.put("NTLMSetting", Boolean.TRUE);
                    }
                }
            }
            if (username != null) {
                hash.put("USER", username);
            }
            else {
                hash.put("USER", "");
            }
            if (password != null) {
                hash.put("PASSWORD", password);
            }
            else {
                hash.put("PASSWORD", "");
            }
        }
        return hash;
    }
    
    public static String getSQLConType() {
        String dbConType = null;
        final String query = "SELECT  c.encrypt_option FROM sys.dm_exec_connections AS c JOIN sys.dm_exec_sessions AS s ON c.session_id = s.session_id WHERE c.session_id = @@SPID; ";
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = relapi.getConnection();
            statement = conn.createStatement();
            rs = relapi.executeQueryForSQL(query, (Map)null, statement);
            while (rs.next()) {
                dbConType = (String)rs.getObject(1);
            }
        }
        catch (final Exception e) {
            DBUtil.logger.log(Level.WARNING, "Exception while getting MSSQL Connection Type  : ", e);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex2) {
                DBUtil.logger.log(Level.WARNING, "Exception while closing dbConnections", ex2);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception ex3) {
                DBUtil.logger.log(Level.WARNING, "Exception while closing dbConnections", ex3);
            }
        }
        return dbConType;
    }
    
    public static boolean isDBSecureConnectionEnabled() {
        try {
            final Properties dbProps = PersistenceInitializer.getDefaultDBProps();
            if (dbProps != null && dbProps.containsKey("url")) {
                final String dbURL = dbProps.getProperty("url");
                if (dbURL.contains("ssl=")) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            DBUtil.logger.log(Level.WARNING, "Caught exception in getting secure connection type", ex);
        }
        return false;
    }
    
    public static Boolean isRemoteDB() {
        String activeDb = null;
        final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final Properties dbBackupProps = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(confFile));
            dbBackupProps.load(fis);
        }
        catch (final Exception e) {
            DBUtil.logger.log(Level.WARNING, "Exception  while reading the file: " + confFile);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                DBUtil.logger.log(Level.WARNING, "Exception  while cosing the file: " + confFile);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                DBUtil.logger.log(Level.WARNING, "Exception  while cosing the file: " + confFile);
            }
        }
        if (dbBackupProps != null) {
            final String url = dbBackupProps.getProperty("url");
            DBUtil.logger.log(Level.INFO, "URL to find DB name is : " + url + "\t from file: " + confFile);
            if (url != null) {
                if (url.toLowerCase().contains("mysql")) {
                    activeDb = "mysql";
                }
                else if (url.toLowerCase().contains("postgresql")) {
                    activeDb = "postgres";
                }
                else if (url.toLowerCase().contains("sqlserver")) {
                    activeDb = "mssql";
                }
            }
        }
        if (activeDb.equalsIgnoreCase("mssql")) {
            DBUtil.logger.log(Level.INFO, "Since the db is mssql, isRemoteDB(): true");
            return true;
        }
        if (activeDb.equalsIgnoreCase("postgres")) {
            DBUtil.logger.log(Level.INFO, "isRemoteDB(): Postgres : ");
            final String fileURL = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
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
                    if (name != null && name.equalsIgnoreCase("StartDBServer") && connectorEl.getAttribute("value").equalsIgnoreCase("true")) {
                        DBUtil.logger.log(Level.INFO, "isRemoteDB(): Postgres : False ");
                        return false;
                    }
                    if (name != null && name.equalsIgnoreCase("StartDBServer") && connectorEl.getAttribute("value").equalsIgnoreCase("false")) {
                        DBUtil.logger.log(Level.INFO, "isRemoteDB(): Postgres : True");
                        return true;
                    }
                }
            }
            catch (final Exception e3) {
                DBUtil.logger.log(Level.WARNING, "caught exception in isRemoteDB(): Postgres : ", e3);
            }
        }
        return false;
    }
    
    public static Object getFirstValue(final String sqlQuery) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        final Connection con = RelationalAPI.getInstance().getConnection();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sqlQuery);
            if (rs.next()) {
                return getObject(rs, 1);
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex) {}
            }
            if (st != null) {
                try {
                    st.close();
                }
                catch (final Exception ex2) {}
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception ex3) {}
            }
        }
        return null;
    }
    
    private static Object getObject(final ResultSet rs, final int columnIndex) throws SQLException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int type = rsmd.getColumnType(columnIndex);
        if (type == 1111 && "citext".equalsIgnoreCase(rsmd.getColumnTypeName(columnIndex))) {
            return rs.getString(columnIndex);
        }
        return rs.getObject(columnIndex);
    }
    
    static {
        DBUtil.scheduleDBBackupLogger = Logger.getLogger("ScheduleDBBackup");
        DEFAULT_BACKUP_DIR = System.getProperty("server.home") + "/Backup/";
    }
}
