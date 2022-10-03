package com.adventnet.persistence;

import java.util.Hashtable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.db.persistence.SequenceGenerator;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.persistence.xml.DynamicValueHandler;
import com.adventnet.persistence.xml.DVHandlerTemplate;
import com.adventnet.persistence.xml.DynamicValueHandlerRepositry;
import java.io.InputStream;
import java.sql.Connection;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Properties;
import java.sql.Statement;
import java.util.logging.Level;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;

public class MigrationSqlExecutor
{
    private static final Logger LOGGER;
    private static RelationalAPI relAPI;
    public static final int INSTALL = 1;
    public static final int REVERT = 2;
    public static final int PREINSTALL = 3;
    public static final int SLEEP = 4;
    private static String dvhPatternStr;
    private static Pattern dp;
    private static Matcher dm;
    private static String uvhPatternStr;
    private static Pattern up;
    private static Matcher um;
    
    public static void installDDLs(final String sqlFileStr) throws Exception {
        installDDLs(sqlFileStr, 0);
    }
    
    public static void installDDLs(final String sqlFileStr, final int startpoint) throws Exception {
        if (sqlFileStr == null || !new File(sqlFileStr).exists()) {
            throw new IllegalArgumentException("DDL sqlFile [" + sqlFileStr + "] cannot be null or invalid File");
        }
        executeInstallDDLs(getDDLsList(sqlFileStr), 1, (startpoint == 0) ? 0 : (startpoint - 1));
    }
    
    public static void installDDLs(final StringBuilder sb) throws Exception {
        installDDLs(sb, 0);
    }
    
    public static void installDDLs(final StringBuilder sb, final int startpoint) throws Exception {
        executeInstallDDLs(getDDLsList(sb), 1, (startpoint == 0) ? 0 : (startpoint - 1));
    }
    
    public static void revertDDLs(final String sqlFileStr) throws Exception {
        revertDDLs(sqlFileStr, 0);
    }
    
    public static void revertDDLs(final String sqlFileStr, final int startpoint) throws Exception {
        if (sqlFileStr == null || !new File(sqlFileStr).exists()) {
            throw new IllegalArgumentException("DDL sqlFile [" + sqlFileStr + "] cannot be null or invalid File");
        }
        executeRevertDDLs(getDDLsList(sqlFileStr), 2, startpoint);
    }
    
    public static void revertDDLs(final StringBuilder sb) throws Exception {
        revertDDLs(sb, 0);
    }
    
    public static void revertDDLs(final StringBuilder sb, final int startpoint) throws Exception {
        executeRevertDDLs(getDDLsList(sb), 2, startpoint);
    }
    
    public static void revertDMLs(final String revertSQLFileName) throws Exception {
        if (revertSQLFileName == null || !new File(revertSQLFileName).exists()) {
            throw new IllegalArgumentException("DML sqlFile [" + revertSQLFileName + "] cannot be null or invalid File");
        }
        executeForDOChanges(revertSQLFileName, 2, null);
    }
    
    public static void installDMLs(final String sqlFileStr, final String revertSQLFileName) throws Exception {
        if (sqlFileStr == null || !new File(sqlFileStr).exists()) {
            throw new IllegalArgumentException("DML sqlFile cannot be null or invalid File");
        }
        if (revertSQLFileName == null) {
            throw new IllegalArgumentException("revertSQLFileName should not be null");
        }
        final File revSqlFile = new File(revertSQLFileName);
        final File parentDir = revSqlFile.getParentFile();
        if (!parentDir.isDirectory()) {
            parentDir.mkdirs();
        }
        revSqlFile.createNewFile();
        MigrationSqlExecutor.LOGGER.log(Level.INFO, "An empty file created for generating the revert DML SQLs :: [{0}]", revertSQLFileName);
        executeForDOChanges(sqlFileStr, 1, revertSQLFileName);
    }
    
    private static void executeQuery(final Statement s, final String query, final boolean throwException) throws Exception {
        try {
            MigrationSqlExecutor.LOGGER.log(Level.INFO, "Executing the SQL :: [{0}]", query);
            s.execute(query);
        }
        catch (final Exception e) {
            if (throwException) {
                MigrationSqlExecutor.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
            MigrationSqlExecutor.LOGGER.log(Level.SEVERE, "PREINSTALL Exception Ignored :: [{0}]", e.getMessage());
        }
    }
    
    private static void executeForDOChanges(final String sqlFileStr, final int operationType, final String revertSQLFileName) throws Exception {
        MigrationSqlExecutor.LOGGER.log(Level.INFO, "Entered executeForDOChanges method");
        final Properties patternVsValue = new Properties();
        Connection c = null;
        Statement s = null;
        BufferedReader reader = null;
        InputStream is = null;
        final StringBuilder revertBuffer = new StringBuilder();
        try {
            String line = null;
            is = new FileInputStream(new File(sqlFileStr));
            reader = new BufferedReader(new InputStreamReader(is));
            DataAccess.getTransactionManager().begin();
            c = MigrationSqlExecutor.relAPI.getConnection();
            s = c.createStatement();
            try {
                while ((line = reader.readLine()) != null) {
                    line = trimSQL(line);
                    if (line != null) {
                        final int currentOperationType = getOperationType(line);
                        line = removeOperationType(line);
                        if (operationType == 1) {
                            line = replaceDVHInSQL(patternVsValue, line);
                            line = replaceUVHInSQL(patternVsValue, line);
                        }
                        if (currentOperationType == operationType) {
                            executeQuery(s, line, true);
                        }
                        if (operationType != 1 || currentOperationType != 2) {
                            continue;
                        }
                        revertBuffer.append("\nREVERT   $ ");
                        revertBuffer.append(line);
                    }
                }
                if (operationType == 1) {
                    MigrationSqlGenerator.generateSql(revertBuffer, revertSQLFileName, true);
                    MigrationSqlExecutor.LOGGER.log(Level.INFO, "revertSQLs has been successfully generated in this file :: [{0}]", revertSQLFileName);
                }
                DataAccess.getTransactionManager().commit();
            }
            catch (final Exception e) {
                DataAccess.getTransactionManager().rollback();
                throw e;
            }
        }
        finally {
            safeClose(c, s, is);
        }
    }
    
    protected static void safeClose(final Connection c, final Statement s, final InputStream is) {
        if (s != null) {
            try {
                s.close();
            }
            catch (final Exception e) {
                MigrationSqlExecutor.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        if (c != null) {
            try {
                c.close();
            }
            catch (final Exception e) {
                MigrationSqlExecutor.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        if (is != null) {
            try {
                is.close();
            }
            catch (final Exception ee) {
                MigrationSqlExecutor.LOGGER.log(Level.SEVERE, ee.getMessage(), ee);
            }
        }
    }
    
    protected static String trimSQL(String line) {
        line = line.trim();
        if (line.length() == 0 || line.startsWith("#") || line.startsWith("--")) {
            return null;
        }
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }
        return line;
    }
    
    protected static int getOperationType(final String line) {
        if (line.startsWith("PREINSTALL")) {
            return 3;
        }
        if (line.startsWith("INSTALL")) {
            return 1;
        }
        if (line.startsWith("REVERT")) {
            return 2;
        }
        if (line.startsWith("SLEEP")) {
            return 4;
        }
        throw new IllegalArgumentException("Unable to resolve the operationType for the line :: [" + line + "]");
    }
    
    private static String removeOperationType(final String line) {
        return line.substring(line.indexOf(36) + 1).trim();
    }
    
    private static String replaceDVHInSQL(final Properties patternVsValue, String sql) throws Exception {
        String tableName = sql.substring(sql.indexOf(96) + 1);
        tableName = tableName.substring(0, tableName.indexOf(96));
        MigrationSqlExecutor.dm = MigrationSqlExecutor.dp.matcher(sql);
        while (MigrationSqlExecutor.dm.find()) {
            final String dvhString = sql.substring(MigrationSqlExecutor.dm.start() + 1, MigrationSqlExecutor.dm.end() - 1);
            final String dvhValue = getValueForDVHString(patternVsValue, tableName, dvhString);
            sql = MigrationSqlExecutor.dm.replaceFirst(dvhValue);
            MigrationSqlExecutor.dm = MigrationSqlExecutor.dp.matcher(sql);
        }
        return sql;
    }
    
    private static String getValueForDVHString(final Properties patternVsValue, final String tableName, String dvhString) throws Exception {
        dvhString = dvhString.replaceAll("&quote;", "'");
        String retStr = patternVsValue.getProperty(dvhString);
        if (retStr == null) {
            final String[] s = dvhString.split("::");
            final String columnName = s[1];
            final String attributeValue = s[2];
            Object returnValue = null;
            final DVHandlerTemplate dvt = DynamicValueHandlerRepositry.getDVHandlerTemplate(tableName, columnName);
            if (dvt != null) {
                final DynamicValueHandler handler = dvt.getDynamicValueHandler();
                if (handler != null) {
                    returnValue = handler.getColumnValue(dvt.getTableName(), dvt.getColumnName(), dvt.getConfiguredAttributes(), attributeValue);
                }
            }
            retStr = "'" + returnValue + "'";
            ((Hashtable<String, String>)patternVsValue).put(dvhString, retStr);
        }
        return retStr;
    }
    
    private static String replaceUVHInSQL(final Properties patternVsValue, String sql) throws Exception {
        MigrationSqlExecutor.um = MigrationSqlExecutor.up.matcher(sql);
        while (MigrationSqlExecutor.um.find()) {
            final String uvhString = sql.substring(MigrationSqlExecutor.um.start() + 1, MigrationSqlExecutor.um.end() - 1);
            final String uvhValue = getValueForUVHString(patternVsValue, uvhString);
            sql = MigrationSqlExecutor.um.replaceFirst(uvhValue);
            MigrationSqlExecutor.um = MigrationSqlExecutor.up.matcher(sql);
        }
        return sql;
    }
    
    private static String getValueForUVHString(final Properties patternVsValue, final String uvhString) throws Exception {
        String retStr = patternVsValue.getProperty(uvhString);
        if (retStr != null) {
            return retStr;
        }
        final String[] s = uvhString.split("::");
        final String genName = s[1];
        final SequenceGenerator seqGen = SequenceGeneratorRepository.getOrCreate(genName, "BIGINT");
        retStr = String.valueOf(seqGen.nextValue());
        patternVsValue.setProperty(uvhString, retStr);
        return retStr;
    }
    
    private static List[] getDDLsList(final String sqlFileStr) throws Exception {
        MigrationSqlExecutor.LOGGER.log(Level.INFO, "Entered getDDLsList method   ");
        InputStream is = null;
        List[] sqlsForDDLs = null;
        try {
            is = new FileInputStream(new File(sqlFileStr));
            sqlsForDDLs = getDDLsList(new InputStreamReader(is));
        }
        finally {
            safeClose(null, null, is);
        }
        return sqlsForDDLs;
    }
    
    private static List[] getDDLsList(final Reader reader) throws Exception {
        final List[] sqlsForDDLs = { new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList() };
        final BufferedReader buffreader = new BufferedReader(reader);
        String line = null;
        while ((line = buffreader.readLine()) != null) {
            line = trimSQL(line);
            if (line == null) {
                continue;
            }
            final int curOperation = getOperationType(line);
            switch (curOperation) {
                case 3: {
                    sqlsForDDLs[0].add(line);
                    continue;
                }
                case 1: {
                    sqlsForDDLs[1].add(line);
                    continue;
                }
                case 2: {
                    sqlsForDDLs[2].add(line);
                    continue;
                }
                case 4: {
                    sqlsForDDLs[3].add(line);
                    sqlsForDDLs[4].add(line);
                    continue;
                }
            }
        }
        return sqlsForDDLs;
    }
    
    private static List[] getDDLsList(final StringBuilder sb) throws Exception {
        MigrationSqlExecutor.LOGGER.log(Level.INFO, "Entered getDDLsList method   ");
        final List[] sqlsForDDLs = getDDLsList(new StringReader(sb.toString()));
        return sqlsForDDLs;
    }
    
    private static void executeInstallDDLs(final List[] ddlSQLs, int operation, final int startpoint) throws Exception {
        if (ddlSQLs == null || (!ddlSQLs[0].isEmpty() && ddlSQLs[0].size() != ddlSQLs[1].size() && startpoint > 0)) {
            throw new IllegalArgumentException(" PREINSTALL and INSTALL queries should be same size when given a startpoint ");
        }
        Connection c = null;
        Statement s = null;
        String sleep_time = null;
        String line = null;
        try {
            c = MigrationSqlExecutor.relAPI.getConnection();
            s = c.createStatement();
            if (!ddlSQLs[0].isEmpty()) {
                for (int index = ddlSQLs[0].size() - 1; index >= ((startpoint == 0) ? 0 : startpoint); --index) {
                    line = ddlSQLs[0].get(index);
                    line = removeOperationType(line);
                    line = line.replace("${querynumber}", String.valueOf(index));
                    executeQuery(s, line, false);
                }
            }
            for (int index = startpoint; index < ddlSQLs[1].size(); ++index) {
                line = ddlSQLs[1].get(index);
                line = removeOperationType(line);
                line = line.replace("${querynumber}", String.valueOf((ddlSQLs[1].size() != index + 1) ? (index + 1) : 0));
                executeQuery(s, line, true);
                if (!ddlSQLs[3].isEmpty()) {
                    sleep_time = ddlSQLs[3].get(index);
                    operation = getOperationType(sleep_time);
                    sleep_time = removeOperationType(sleep_time);
                    final int sleep_millisecond = (sleep_time != null) ? Integer.parseInt(sleep_time) : 0;
                    Thread.sleep(sleep_millisecond);
                }
            }
        }
        finally {
            safeClose(c, s, null);
        }
    }
    
    private static void executeRevertDDLs(final List[] ddlSQLs, int operation, final int startpoint) throws Exception {
        if (ddlSQLs[2] == null) {
            throw new IllegalArgumentException("sqlForDDLs list cannot be null or empty");
        }
        Connection c = null;
        Statement s = null;
        String sleep_time = null;
        String line = null;
        try {
            c = MigrationSqlExecutor.relAPI.getConnection();
            s = c.createStatement();
            for (int index = (startpoint == 0) ? (ddlSQLs[2].size() - 1) : (startpoint - 1); index >= 0; --index) {
                line = ddlSQLs[2].get(index);
                line = removeOperationType(line);
                line = line.replace("${querynumber}", String.valueOf(index));
                executeQuery(s, line, true);
                if (!ddlSQLs[4].isEmpty()) {
                    sleep_time = ddlSQLs[4].get(index);
                    operation = getOperationType(sleep_time);
                    sleep_time = removeOperationType(sleep_time);
                    final int sleep_millisecond = (sleep_time != null) ? Integer.parseInt(sleep_time) : 0;
                    Thread.sleep(sleep_millisecond);
                }
            }
        }
        finally {
            safeClose(c, s, null);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MigrationSqlExecutor.class.getName());
        MigrationSqlExecutor.relAPI = RelationalAPI.getInstance();
        MigrationSqlExecutor.dvhPatternStr = "'DVH::\\S*::[^']*::DVH'";
        MigrationSqlExecutor.dp = Pattern.compile(MigrationSqlExecutor.dvhPatternStr);
        MigrationSqlExecutor.dm = null;
        MigrationSqlExecutor.uvhPatternStr = "'UVG::\\S*::UVH@\\S*'";
        MigrationSqlExecutor.up = Pattern.compile(MigrationSqlExecutor.uvhPatternStr);
        MigrationSqlExecutor.um = null;
    }
}
