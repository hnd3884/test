package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import com.zoho.framework.utils.crypto.EnDecrypt;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.net.URISyntaxException;
import java.io.IOException;
import java.awt.Desktop;
import javax.swing.event.HyperlinkEvent;
import javax.swing.JEditorPane;
import java.sql.DriverManager;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.util.Locale;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstructionException;
import org.json.JSONException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import org.json.simple.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONArray;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Stream;
import com.adventnet.db.api.RelationalAPI;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.StringCharacterIterator;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Blob;
import java.io.BufferedReader;
import java.sql.Clob;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.logging.Level;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import com.me.devicemanagement.onpremise.server.util.EMSExecutorPool;
import java.util.logging.Logger;

public class MssqlBackupRestoreUtil
{
    private static int attempt;
    private String newline;
    private String newlinechar;
    private String crglinechar;
    private static String newchar;
    private static String crgchar;
    private String separator;
    private String dbserver;
    private boolean dbhealthstatus;
    public Logger logger;
    private static MssqlBackupRestoreUtil objMssqlBackupRestoreUtil;
    private EMSExecutorPool executor;
    public static volatile AtomicInteger executeBatchStartedCount;
    public static volatile AtomicInteger executeBatchEndedCount;
    public static final String BATCH_START_LOCK = "BATCH_START_LOCK";
    Logger out;
    
    public MssqlBackupRestoreUtil() {
        this.newline = "__NEW_LINE__";
        this.newlinechar = "__#CRLF10#__";
        this.crglinechar = "__#CRLF13#__";
        this.separator = File.separator;
        this.dbserver = null;
        this.dbhealthstatus = true;
        this.logger = Logger.getLogger("DCBackupRestoreUI");
        this.out = Logger.getLogger("DCBackupRestoreUI");
        this.dbserver = this.getActiveDBServer();
    }
    
    public static MssqlBackupRestoreUtil getInstance() {
        if (MssqlBackupRestoreUtil.objMssqlBackupRestoreUtil == null) {
            MssqlBackupRestoreUtil.objMssqlBackupRestoreUtil = new MssqlBackupRestoreUtil();
        }
        return MssqlBackupRestoreUtil.objMssqlBackupRestoreUtil;
    }
    
    public String getActiveDBServer() {
        try {
            if (BackupRestoreUtil.getDBProps().getProperty("exceptionsorterclassname").contains("PostgresExceptionSorter")) {
                return "pgsql";
            }
            if (BackupRestoreUtil.getDBProps().getProperty("exceptionsorterclassname").contains("MssqlExceptionSorter")) {
                return "mssql";
            }
            if (BackupRestoreUtil.getDBProps().getProperty("exceptionsorterclassname").contains("MysqlExceptionSorter")) {
                return "mysql";
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting Active DB Name " + ex);
            return null;
        }
        return null;
    }
    
    public void dumpTable(final String table, final String pkColumn, final Connection conn, final String backupDir) throws Exception {
        this.logger.log(Level.INFO, "Starting table : " + table);
        MssqlBackupRestoreUtil.attempt = 1;
        this.dumpTableUsingLimit(table, conn, backupDir);
        this.logger.log(Level.INFO, "Completed");
    }
    
    private void dumpTableUsingLimit(final String table, final Connection conn, final String backupDir) throws Exception {
        if (MssqlBackupRestoreUtil.attempt >= 3) {
            throw new Exception("Unable to get the data from [" + table + "] table");
        }
        Statement cstmt = null;
        ResultSet rcount = null;
        final int rowFetchSize = 5000;
        int count = 0;
        String sql = null;
        try {
            sql = "SELECT count(*) FROM " + table;
            if (table.equalsIgnoreCase("tabledetails")) {
                sql = getSqlWithExclusionsForTableDetails(sql);
            }
            cstmt = conn.createStatement();
            rcount = cstmt.executeQuery(sql);
            if (rcount.next()) {
                count = rcount.getInt(1);
            }
        }
        catch (final Exception e) {
            this.dbhealthstatus = false;
            ++MssqlBackupRestoreUtil.attempt;
            this.logger.log(Level.WARNING, "ERROR = *" + e.getMessage() + "*");
            this.dumpTableUsingLimit(table, conn, backupDir);
            return;
        }
        finally {
            try {
                if (cstmt != null) {
                    cstmt.close();
                }
                if (rcount != null) {
                    rcount.close();
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception while closing connection after getting row count" + ex.getMessage());
            }
        }
        this.logger.log(Level.INFO, "row count = " + count);
        final HashMap metadata = this.getMetaData(table, conn);
        Statement stmt = null;
        ResultSet rs = null;
        conn.setAutoCommit(false);
        sql = "SELECT * FROM " + table;
        if ("mysql".equalsIgnoreCase(this.dbserver)) {
            stmt = conn.createStatement(1003, 1007);
            stmt.setFetchSize(Integer.MIN_VALUE);
        }
        else if ("postgres".equalsIgnoreCase(this.dbserver)) {
            stmt = conn.createStatement(1003, 1007);
            stmt.setFetchSize(rowFetchSize);
        }
        else {
            stmt = conn.createStatement(1003, 1007);
            stmt.setFetchSize(rowFetchSize);
        }
        BufferedWriter out = null;
        FileOutputStream fout = null;
        try {
            rs = stmt.executeQuery(sql);
            final ResultSetMetaData metaData = rs.getMetaData();
            final int colCount = metaData.getColumnCount();
            final File backupfile = new File(backupDir + this.separator + table.toLowerCase() + ".sql");
            backupfile.createNewFile();
            fout = new FileOutputStream(backupfile);
            out = new BufferedWriter(new OutputStreamWriter(fout, "UTF8"));
            int max_col_size = 0;
            try {
                StringBuffer buffer = new StringBuffer();
                buffer.append("INSERT INTO " + table + " (`" + metaData.getColumnName(1) + "`");
                final String comma = ",";
                for (int i = 1; i < colCount; ++i) {
                    buffer.append(comma + "`" + metaData.getColumnName(i + 1) + "`");
                }
                buffer.append(") VALUES\n");
                final String nullString = "NULL";
                final String bitType = "bit";
                final String tinyIntType = "tinyint";
                final String falseString = "false";
                final String booleanTrue = "1";
                final String booleanFalse = "0";
                final String emptyString = "";
                final String singleQuote = "'";
                final String doubleQuote = "''";
                final String backSlash = "\\";
                final String i18nchar = "N'";
                final String parenthesis = "(";
                final String commaspace = ", ";
                while (rs.next()) {
                    buffer.append(parenthesis);
                    for (int j = 0; j < colCount; ++j) {
                        if (j > 0) {
                            buffer.append(commaspace);
                        }
                        final Object value = rs.getObject(j + 1);
                        if (value == null) {
                            buffer.append(nullString);
                        }
                        else {
                            String colData = null;
                            if (value instanceof Clob) {
                                final Clob clob = (Clob)value;
                                final BufferedReader read = (BufferedReader)clob.getCharacterStream();
                                for (String tmp = read.readLine(); tmp != null; tmp = read.readLine()) {
                                    if (colData == null) {
                                        colData = tmp;
                                    }
                                    else {
                                        colData = colData + this.newline + tmp;
                                    }
                                }
                                if (colData == null) {
                                    colData = emptyString;
                                }
                            }
                            else if (value instanceof Blob) {
                                colData = "0x" + rs.getString(j + 1);
                            }
                            else if (value instanceof byte[]) {
                                colData = "0x" + binaryArrayToHex((byte[])value);
                            }
                            else {
                                colData = value.toString();
                            }
                            if (isTextColumn(metaData.getColumnTypeName(j + 1))) {
                                colData = this.format(colData);
                                colData = colData.replaceAll(this.newline, this.newlinechar);
                                try {
                                    max_col_size = Integer.parseInt(metadata.get(metaData.getColumnName(j + 1)).toString());
                                }
                                catch (final Exception e2) {
                                    max_col_size = 0;
                                }
                                if (max_col_size > 0 && max_col_size < 4000) {
                                    if (colData.length() > max_col_size) {
                                        for (--max_col_size, colData = colData.substring(0, max_col_size); colData.endsWith(backSlash); colData = colData.substring(0, max_col_size)) {
                                            --max_col_size;
                                        }
                                    }
                                    else {
                                        for (max_col_size = colData.length(); colData.endsWith(backSlash) || colData.endsWith(singleQuote) || colData.endsWith(doubleQuote); colData = colData.substring(0, max_col_size)) {
                                            --max_col_size;
                                        }
                                    }
                                }
                                buffer.append(i18nchar + colData + singleQuote);
                            }
                            else if (isDateTimeColumn(metaData.getColumnTypeName(j + 1))) {
                                colData = colData.replaceAll("-", "--D--");
                                buffer.append(i18nchar + colData + singleQuote);
                            }
                            else if (isBooleanColumn(metaData.getColumnTypeName(j + 1))) {
                                if (falseString.equals(colData) || booleanFalse.equals(colData) || "f".equals(colData)) {
                                    buffer.append(singleQuote + booleanFalse + singleQuote);
                                }
                                else {
                                    buffer.append(singleQuote + booleanTrue + singleQuote);
                                }
                            }
                            else {
                                buffer.append(colData);
                            }
                        }
                    }
                    buffer.append(");\n");
                    out.write(buffer.toString());
                    buffer = new StringBuffer();
                    out.flush();
                }
            }
            catch (final SQLException sqle) {
                if (sqle.getMessage().contains("Connection object is closed")) {
                    this.logger.log(Level.WARNING, "ERROR = *" + sqle.getMessage() + "* \n");
                    sqle.printStackTrace();
                    try {
                        this.logger.log(Level.WARNING, "Backup table in progress is " + table.toLowerCase() + ".sql \n");
                        new File(backupDir + this.separator + table.toLowerCase() + ".sql").delete();
                        this.logger.log(Level.WARNING, "SQL File deleted :: " + table.toLowerCase() + ".sql \n");
                    }
                    catch (final Exception ex2) {
                        ex2.printStackTrace();
                        throw ex2;
                    }
                    this.logger.log(Level.WARNING, "It looks like database connection get lost while taking the backup, and now application will try to establish the db connection and continue with the backup in two attempts with 10 sec intervals. Attempt " + MssqlBackupRestoreUtil.attempt + ". \n");
                    Thread.sleep(10000L);
                    ++MssqlBackupRestoreUtil.attempt;
                    this.dbhealthstatus = false;
                    this.logger.log(Level.WARNING, "Reestablishing the connection again. Attempt:" + MssqlBackupRestoreUtil.attempt + ". \n");
                    this.dumpTableUsingLimit(table, conn, backupDir);
                    return;
                }
                this.logger.log(Level.INFO, "ERROR = *" + sqle.getMessage() + "*\n");
                sqle.printStackTrace();
                throw sqle;
            }
            catch (final Exception e3) {
                e3.printStackTrace();
                throw e3;
            }
            MssqlBackupRestoreUtil.attempt = 1;
            try {
                conn.commit();
            }
            catch (final Exception exp) {
                this.logger.log(Level.WARNING, "Exception in committing transaction, Going to roll Back", exp);
                conn.rollback();
            }
        }
        catch (final Exception ex3) {
            ++MssqlBackupRestoreUtil.attempt;
            this.logger.log(Level.WARNING, "ERROR = *" + ex3.getMessage() + "*");
            this.dumpTableUsingLimit(table, conn, backupDir);
            this.dbhealthstatus = false;
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (out != null) {
                    out.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception ex4) {
                this.logger.log(Level.WARNING, "Exception in closing statements " + ex4);
            }
            try {
                conn.setAutoCommit(true);
            }
            catch (final Exception ex4) {
                this.logger.log(Level.WARNING, "Exception in setting auto commit " + ex4);
            }
        }
    }
    
    private HashMap getMetaData(final String table_name, final Connection conn) {
        final HashMap hash = new LinkedHashMap();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT TABLE_ID FROM TableDetails where TABLE_NAME = '" + table_name + "'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                sql = "SELECT COLUMN_NAME, MAX_SIZE FROM ColumnDetails where TABLE_ID = '" + String.valueOf(rs.getString(1)) + "'";
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    hash.put(rs.getString(1), rs.getString(2));
                }
            }
        }
        catch (final Exception ex) {}
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (final Exception ex2) {}
            }
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex3) {}
            }
        }
        return hash;
    }
    
    public boolean getDBHealthStatus() {
        return this.dbhealthstatus;
    }
    
    private static boolean isTextColumn(final String columnType) {
        boolean flag = false;
        if ("varchar".equalsIgnoreCase(columnType) || "nvarchar".equalsIgnoreCase(columnType)) {
            flag = true;
        }
        else if ("char".equalsIgnoreCase(columnType) || "nchar".equalsIgnoreCase(columnType)) {
            flag = true;
        }
        else if ("text".equalsIgnoreCase(columnType) || "ntext".equalsIgnoreCase(columnType)) {
            flag = true;
        }
        else if ("citext".equalsIgnoreCase(columnType)) {
            flag = true;
        }
        return flag;
    }
    
    private static boolean isDateTimeColumn(final String columnType) {
        boolean flag = false;
        if ("datetime".equalsIgnoreCase(columnType) || "date".equalsIgnoreCase(columnType) || "timestamp".equalsIgnoreCase(columnType)) {
            flag = true;
        }
        return flag;
    }
    
    private String format(final String colData) {
        final StringBuffer buffer = new StringBuffer();
        final StringCharacterIterator iterator = new StringCharacterIterator(colData);
        for (char character = iterator.current(); character != '\uffff'; character = iterator.next()) {
            if (character == '\'') {
                buffer.append("''");
            }
            else if (character == '\\') {
                if ("mysql".equals(this.dbserver)) {
                    buffer.append("\\\\");
                }
                else {
                    buffer.append("\\");
                }
            }
            else if (character == '\r') {
                if ("mysql".equals(this.dbserver)) {
                    buffer.append("\\r");
                }
                else {
                    buffer.append(this.crglinechar);
                }
            }
            else if (character == '\n') {
                if ("mysql".equals(this.dbserver)) {
                    buffer.append("\\n");
                }
                else {
                    buffer.append(this.newlinechar);
                }
            }
            else if (character == '\0') {
                this.logger.log(Level.INFO, "Ignoring ^@ char");
            }
            else {
                buffer.append(character);
            }
        }
        return buffer.toString();
    }
    
    public void restoreTable(final String file, final String backupDataDB, final Connection con, final long start, long size, final String header, final AtomicBoolean restoringTableStatus, final int bathesPerTransaction, final String tableName, final boolean isFirstRestore) throws Exception {
        try (final Statement stmt = con.createStatement()) {
            if (!"mssql".equals(this.dbserver)) {
                this.logger.log(Level.INFO, "Only mssql supported for dump backup restore");
                throw new UnsupportedOperationException("Only mssql supported for dump backup restore");
            }
            Stream<String> stream = Files.lines(Paths.get(file, new String[0])).skip(start);
            size = Math.min(size, stream.count());
            stream.close();
            stream = Files.lines(Paths.get(file, new String[0])).skip(start);
            stream.limit(size).forEach(line -> {
                if (line != null && !line.trim().isEmpty()) {
                    line = this.replaceUnwantedChar(line);
                    line = line.replaceAll("--D--", "");
                    line = this.replaceBSChar(line, backupDataDB2);
                    try {
                        statement2.addBatch(s + this.replaceCRTF(line));
                    }
                    catch (final Exception exception) {
                        this.logger.log(Level.INFO, "Exception while adding to statement batch.", exception);
                        atomicBoolean.set(false);
                        throw new RuntimeException(exception);
                    }
                }
                return;
            });
            stream.close();
            synchronized ("BATCH_START_LOCK") {
                if (MssqlBackupRestoreUtil.executeBatchStartedCount.get() >= bathesPerTransaction) {
                    while (MssqlBackupRestoreUtil.executeBatchStartedCount.get() != 0) {
                        TimeUnit.MILLISECONDS.sleep(100L);
                    }
                }
                MssqlBackupRestoreUtil.executeBatchStartedCount.incrementAndGet();
            }
            stmt.executeBatch();
            MssqlBackupRestoreUtil.executeBatchEndedCount.incrementAndGet();
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while adding to batch", ex);
            if (isFirstRestore) {
                MssqlBackupRestoreUtil.executeBatchEndedCount.incrementAndGet();
                try (final Connection connection = RelationalAPI.getInstance().getConnection();
                     final Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE " + tableName + " REBUILD WITH (IGNORE_DUP_KEY = ON)");
                    this.restoreTable(file, backupDataDB, connection, start, size, header, restoringTableStatus, bathesPerTransaction, tableName, false);
                    statement.executeUpdate("ALTER TABLE " + tableName + " REBUILD WITH (IGNORE_DUP_KEY = OFF)");
                }
                return;
            }
            throw ex;
        }
    }
    
    public static boolean isToBackupTable(final String tableName) {
        final ArrayList<String> excludedTables = new ArrayList<String>();
        final String excludedTablesFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DeviceManagementOnpremise" + File.separator + "tablesExcludedInBackupRestore.props";
        final String excludedTablesListString = BackupRestoreUtil.getInstance().getPropertiesFromFile(excludedTablesFilePath).getProperty("excluded-tables");
        if (excludedTablesListString != null) {
            excludedTables.addAll(Arrays.asList(excludedTablesListString.split(",")));
        }
        if (!excludedTables.isEmpty()) {
            for (final String excludedTable : excludedTables) {
                if (tableName.equalsIgnoreCase(excludedTable)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static String getSqlWithExclusionsForTableDetails(String sql) {
        final ArrayList<String> excludedTables = new ArrayList<String>();
        final String excludedTablesFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DeviceManagementOnpremise" + File.separator + "tablesExcludedInBackupRestore.props";
        final String excludedTablesListString = BackupRestoreUtil.getInstance().getPropertiesFromFile(excludedTablesFilePath).getProperty("excluded-tables");
        if (excludedTablesListString != null) {
            excludedTables.addAll(Arrays.asList(excludedTablesListString.split(",")));
        }
        final StringBuilder sqlBuilder = new StringBuilder(sql + " where LOWER(TABLE_NAME) not in (");
        for (final String table : excludedTables) {
            sqlBuilder.append("'").append(table.toLowerCase()).append("',");
        }
        sql = sqlBuilder.toString();
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        return sql;
    }
    
    private String replaceUnwantedChar(String data) {
        final StringBuffer buffer = new StringBuffer();
        try {
            data = data.replaceAll(this.crglinechar, MssqlBackupRestoreUtil.crgchar);
            data = data.replaceAll(this.newlinechar, MssqlBackupRestoreUtil.newchar);
            final StringCharacterIterator iterator = new StringCharacterIterator(data);
            for (char character = iterator.current(); character != '\uffff'; character = iterator.next()) {
                if (character != '\0') {
                    buffer.append(character);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e.toString());
        }
        buffer.append("\n");
        return buffer.toString();
    }
    
    private String replaceBSChar(String data, final String backupDataDB) {
        try {
            if (this.dbserver.equals("mysql") && backupDataDB.equals("mssql")) {
                data = data.replaceAll("\\\\", "\\\\\\\\");
            }
            if (this.dbserver.equals("mssql") && backupDataDB.equals("mysql")) {
                data = data.replaceAll("\\\\\\\\", "\\\\");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e.toString());
        }
        return data;
    }
    
    private String replaceCRTF(String data) {
        try {
            data = data.replaceAll("\\\\r\\\\n", "\r\n");
            data = data.replaceAll("\\\\t", "\t");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e.toString());
        }
        return data;
    }
    
    public static Properties getProperties(final String confFileName) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        return props;
    }
    
    private static boolean isBooleanColumn(final String columnType) {
        boolean flag = false;
        if ("boolean".equalsIgnoreCase(columnType) || "bool".equalsIgnoreCase(columnType) || "bit".equalsIgnoreCase(columnType) || "tiny".equalsIgnoreCase(columnType) || "tinyint".equalsIgnoreCase(columnType)) {
            flag = true;
        }
        return flag;
    }
    
    public void deleteFiles(final String path) {
        try {
            final File direc = new File(path);
            final File[] files = direc.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    final boolean deleted = files[i].delete();
                    if (!deleted) {
                        this.out.log(Level.FINE, "Error occurred while deleting the file :: " + files[i].getName());
                    }
                }
            }
            direc.delete();
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, "error occurred while deleting :: " + e);
        }
    }
    
    public JSONArray getDynamicColumnSchemaFromSetup() {
        final JSONArray dynamicColumnDetails = new JSONArray();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("TableDetails"));
            query.addSelectColumn(Column.getColumn("TableDetails", "TABLE_NAME"));
            query.addSelectColumn(Column.getColumn("TableDetails", "TABLE_ID"));
            query.addSelectColumn(Column.getColumn("ColumnDetails", "COLUMN_ID"));
            query.addSelectColumn(Column.getColumn("ColumnDetails", "TABLE_ID"));
            query.addSelectColumn(new Column("ColumnDetails", "COLUMN_NAME"));
            query.addSelectColumn(new Column("ColumnDetails", "IS_DYNAMIC"));
            final Join dynamicJoin = new Join("TableDetails", "ColumnDetails", new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2);
            final Criteria criteria = new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), (Object)true, 0);
            query.addJoin(dynamicJoin);
            query.setCriteria(criteria);
            final DataObject dobj = DataAccess.get(query);
            int index = 0;
            if (!dobj.isEmpty()) {
                String tableName = "";
                String columnName = "";
                final Iterator userItr = dobj.getRows("TableDetails");
                while (userItr.hasNext()) {
                    final Row row = userItr.next();
                    tableName = (String)row.get("TABLE_NAME");
                    final Long tableID = (Long)row.get("TABLE_ID");
                    final Criteria cri = new Criteria(Column.getColumn("ColumnDetails", "TABLE_ID"), (Object)tableID, 0);
                    final Iterator columnIter = dobj.getRows("ColumnDetails", cri);
                    while (columnIter.hasNext()) {
                        final Row colRow = columnIter.next();
                        columnName = (String)colRow.get("COLUMN_NAME");
                        if (tableName != null && tableName.trim().length() > 0 && columnName.trim().length() > 0) {
                            dynamicColumnDetails.add(index++, (Object)this.getDynamicColumnDetails(tableName, columnName));
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting used locale from db : ", e);
        }
        return dynamicColumnDetails;
    }
    
    private JSONObject getDynamicColumnDetails(final String tableName, final String columnName) throws MetaDataException, JSONException, QueryConstructionException {
        TableDefinition tableDef = null;
        ColumnDefinition columnDefinition = null;
        tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
        columnDefinition = tableDef.getColumnDefinitionByName(columnName);
        final JSONObject dynamicColumn = new JSONObject();
        final JSONObject dynamicColumnObj = new JSONObject();
        dynamicColumn.put((Object)"tableName", (Object)tableName);
        dynamicColumn.put((Object)"columnName", (Object)columnName);
        dynamicColumn.put((Object)"dataType", (Object)columnDefinition.getDataType());
        dynamicColumn.put((Object)"maxlength", (Object)columnDefinition.getMaxLength());
        dynamicColumn.put((Object)"displayName", (Object)columnDefinition.getDisplayName());
        final Object defaultVal = columnDefinition.getDefaultValue();
        if (defaultVal != null) {
            if (defaultVal instanceof Integer) {
                dynamicColumn.put((Object)"defaultValue", (Object)Integer.valueOf(defaultVal.toString()));
            }
            else if (defaultVal instanceof String) {
                final String defaultValue = (String)columnDefinition.getDefaultValue();
                dynamicColumn.put((Object)"defaultValue", (Object)defaultValue);
            }
            else {
                dynamicColumn.put((Object)"defaultValue", (Object)columnDefinition.getDefaultValue().toString());
            }
        }
        final String description = columnDefinition.getDescription();
        if (description != null && description.trim().length() > 0) {
            dynamicColumn.put((Object)"description", (Object)description);
        }
        final AlterTableQueryImpl alterQuery = new AlterTableQueryImpl(tableName);
        alterQuery.addColumn(columnDefinition);
        dynamicColumn.put((Object)"addquery", (Object)RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getSQLForAlterTable((AlterTableQuery)alterQuery));
        dynamicColumnObj.put((Object)"DynamicColumnDetails", (Object)dynamicColumn);
        return dynamicColumnObj;
    }
    
    public void dropDynamicColumns(final JSONArray dynamicColumnList) throws QueryConstructionException, SQLException, JSONException {
        for (int i = 0; i < dynamicColumnList.size(); ++i) {
            final JSONObject jsonObj = (JSONObject)dynamicColumnList.get(i);
            if (jsonObj != null && jsonObj.containsKey((Object)"DynamicColumnDetails")) {
                final JSONObject dynamicColumnDetails = (JSONObject)jsonObj.get((Object)"DynamicColumnDetails");
                final String tableName = this.getValueFromJSON(dynamicColumnDetails, "tableName");
                final String columnName = this.getValueFromJSON(dynamicColumnDetails, "columnName");
                final String constraintName = this.getConstrintForColumn(tableName, columnName);
                if (constraintName != null && constraintName.trim().length() > 0) {
                    final String dropConstratintQuery = "ALTER TABLE ManagedComputerCustomFields DROP CONSTRAINT " + constraintName;
                    executeQuery(dropConstratintQuery);
                }
                final String dropColumnQuery = "ALTER TABLE ManagedComputerCustomFields DROP Column " + columnName;
                executeQuery(dropColumnQuery);
            }
        }
    }
    
    public void createDynamicColumns(final JSONArray dynamicColumnList) throws QueryConstructionException, SQLException {
        for (int i = 0; i < dynamicColumnList.size(); ++i) {
            final JSONObject jsonObj = (JSONObject)dynamicColumnList.get(i);
            if (jsonObj != null && jsonObj.containsKey((Object)"DynamicColumnDetails")) {
                final JSONObject dynamicColumnDetails = (JSONObject)jsonObj.get((Object)"DynamicColumnDetails");
                final String query = this.getValueFromJSON(dynamicColumnDetails, "addquery");
                if (query != null && query.trim().length() > 0) {
                    executeQuery(query);
                }
            }
        }
    }
    
    public String getValueFromJSON(final JSONObject jsonObject, final String key) {
        String query = null;
        if (jsonObject.containsKey((Object)key)) {
            query = jsonObject.get((Object)key).toString();
            if (query.trim().length() > 0) {
                return query;
            }
        }
        return query;
    }
    
    private String getConstrintForColumn(final String tableName, final String columnName) throws SQLException, QueryConstructionException {
        DataSet ds = null;
        String constraintName = null;
        final String constraintQuery = "select name FROM SYS.DEFAULT_CONSTRAINTS WHERE PARENT_OBJECT_ID = OBJECT_ID('" + tableName + "') AND PARENT_COLUMN_ID =\n" + "            (SELECT column_id FROM sys.columns WHERE NAME = '" + columnName + "' AND object_id = OBJECT_ID('" + tableName + "'))";
        Connection con = null;
        try {
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            con = relAPI.getConnection();
            ds = RelationalAPI.getInstance().executeQuery(constraintQuery, con);
            if (ds.next()) {
                constraintName = ds.getAsString(1);
            }
        }
        finally {
            if (con != null) {
                con.close();
            }
            if (ds != null) {
                ds.close();
            }
        }
        return constraintName;
    }
    
    public static void executeQuery(final String query) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            con = relAPI.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
        catch (final Exception e) {
            throw e;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    public static String binaryArrayToHex(final byte[] byteArray) {
        final StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; ++i) {
            hexStringBuffer.append(Integer.toString((byteArray[i] & 0xFF) + 256, 16).substring(1).toUpperCase());
        }
        return hexStringBuffer.toString();
    }
    
    private void closeQuietly(final AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (final Exception e) {
            this.out.log(Level.INFO, "Exception while closing.", e);
        }
    }
    
    public static void checkAndWaitForPermissionForBackupRestore() throws Exception {
        if (getInstance().getActiveDBServer().equalsIgnoreCase("mssql")) {
            final Logger logger = Logger.getLogger("ScheduleDBBackup");
            String user = null;
            String messageText = null;
            final String title = BackupRestoreUtil.getString("desktopcentral.tools.common.warning_message.title", null);
            final String close = BackupRestoreUtil.getString("desktopcentral.tools.common.close", null);
            final String retry = BackupRestoreUtil.getString("desktopcentral.tools.common.retry", null);
            Connection connection = null;
            try {
                final String dbParamsPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
                final Properties dbParams = new Properties();
                dbParams.load(new FileInputStream(dbParamsPath));
                final String dbUrl = dbParams.getProperty("url");
                final String driver = dbParams.getProperty("drivername");
                String databaseName = dbUrl.substring(dbUrl.lastIndexOf("databaseName=") + 13);
                databaseName = databaseName.substring(0, databaseName.indexOf(";"));
                user = dbParams.getProperty("username");
                String pwd = dbParams.getProperty("password");
                final EnDecrypt cryptInstance = (EnDecrypt)new EnDecryptAES256Impl();
                CryptoUtil.setEnDecryptInstance(cryptInstance);
                pwd = CryptoUtil.decrypt(pwd);
                logger.log(Level.INFO, dbUrl);
                logger.log(Level.INFO, "Database: " + databaseName);
                Class.forName(driver);
                connection = DriverManager.getConnection(dbUrl, user, pwd);
                messageText = getMssqlPermissionMsgTxt(databaseName, connection);
                if (messageText != null) {
                    final JEditorPane message = new JEditorPane("text/html", messageText);
                    message.setEditable(false);
                    message.addHyperlinkListener(hyperlinkEvent -> {
                        if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                            try {
                                Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                            }
                            catch (final IOException | URISyntaxException e3) {
                                logger2.log(Level.INFO, e3.getMessage(), e3);
                            }
                        }
                        return;
                    });
                    logger.log(Level.INFO, "User " + user + " doesn't have BAK backup permissions in db " + databaseName + ". Waiting for user response..");
                    int res = JOptionPane.showOptionDialog(null, message, title, 0, 0, null, new String[] { retry, close }, retry);
                    if (res == 1) {
                        System.exit(0);
                    }
                    while (messageText != null) {
                        logger.log(Level.INFO, "User " + user + " doesn't have BAK backup permissions in db " + databaseName + ". Waiting for user response..");
                        res = JOptionPane.showOptionDialog(null, message, title, 0, 0, null, new String[] { retry, close }, retry);
                        if (res == 1) {
                            System.exit(0);
                        }
                        else {
                            messageText = getMssqlPermissionMsgTxt(databaseName, connection);
                        }
                    }
                }
            }
            catch (final Exception e) {
                logger.log(Level.SEVERE, "Exception while checking for backup permissions: " + e.getMessage());
                throw e;
            }
            finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception e2) {
                    logger.log(Level.WARNING, "Exception while closing connection: " + e2.getMessage());
                }
            }
            logger.log(Level.INFO, " User : " + user + " have permission for BAK backup");
        }
    }
    
    private static String getMssqlPermissionMsgTxt(final String databaseName, final Connection connection) throws Exception {
        String messageText = null;
        if (!ScheduleDBBackupUtil.isMssqlDBPermissionsAvailableToTakeBakBackup(databaseName, connection)) {
            messageText = BackupRestoreUtil.getString("desktopcentral.tools.common.no_backup_permission", null);
        }
        if (!ScheduleDBBackupUtil.isMssqlServiceUserHasPrevilegeForScheduleDBLocation(databaseName, connection)) {
            if (messageText != null) {
                messageText += "\n";
            }
            else {
                messageText = "";
            }
            messageText += BackupRestoreUtil.getString("desktopcentral.tools.common.no_backup_file_perm", null);
        }
        return messageText;
    }
    
    static {
        MssqlBackupRestoreUtil.attempt = 1;
        MssqlBackupRestoreUtil.newchar = "\r";
        MssqlBackupRestoreUtil.crgchar = "\n";
        MssqlBackupRestoreUtil.objMssqlBackupRestoreUtil = null;
        MssqlBackupRestoreUtil.executeBatchStartedCount = new AtomicInteger(0);
        MssqlBackupRestoreUtil.executeBatchEndedCount = new AtomicInteger(0);
    }
}
