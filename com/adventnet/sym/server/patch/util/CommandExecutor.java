package com.adventnet.sym.server.patch.util;

import java.io.IOException;
import java.io.File;
import com.adventnet.mfw.ConsoleOut;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandExecutor
{
    private static Logger logger;
    
    public static void dumpData(final String dumpFile) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        final ResultSet rs = null;
        final long buffer_size = 1024L;
        BufferedReader in = null;
        String line = null;
        String queryLine = "";
        long debugTime = 1L;
        try {
            CommandExecutor.logger.log(Level.INFO, "SQL Parsing started for ::{0}", dumpFile);
            final RelationalAPI relapiadd = RelationalAPI.getInstance();
            conn = relapiadd.getConnection();
            stmt = conn.createStatement();
            final String debug_db_time = SyMUtil.getSyMParameter("DEBUG_DB_TIME");
            if (debug_db_time != null && !debug_db_time.equalsIgnoreCase("")) {
                debugTime = new Long(debug_db_time);
            }
            final String dbServer = PersistenceInitializer.getConfigurationValue("DBName");
            in = new BufferedReader(new InputStreamReader(new FileInputStream(dumpFile), "UTF-8"));
            line = in.readLine();
            long size_bytes = line.length();
            String templine = "";
            final long starttime = System.currentTimeMillis();
            int i = 0;
            while (line != null) {
                line = line.trim();
                if (line.length() > 5) {
                    if (line.startsWith("#")) {
                        line = in.readLine();
                        continue;
                    }
                    if ((line.startsWith("set") || line.startsWith("CREATE") || line.startsWith("LOCK") || line.contains("Other_Linux") || line.startsWith("UNLOCK") || line.startsWith("commit") || line.startsWith("INSERT into MSComment values")) && (dbServer.equalsIgnoreCase("postgres") || dbServer.equalsIgnoreCase("mssql"))) {
                        if (!line.endsWith(";")) {
                            for (line = in.readLine(); line != null; line = in.readLine()) {
                                if (line.endsWith(";")) {
                                    break;
                                }
                            }
                        }
                        line = in.readLine();
                        ++i;
                        continue;
                    }
                }
                line = line.trim();
                if (line.length() > 5) {
                    while (line != null) {
                        if (line.endsWith(";")) {
                            templine = "";
                            line = line.replaceAll("\"", "\\\"");
                            line = line.replaceAll("'NULL'", "NULL");
                            line = line.replaceAll("\\n", "");
                            if (dbServer.equalsIgnoreCase("mssql") || dbServer.equalsIgnoreCase("postgres")) {
                                line = line.replaceAll("\\\\\\\\", "\\\\");
                            }
                            if (line.startsWith("INSERT into")) {
                                final int totallength = line.length();
                                final int valuesindex = line.indexOf("(");
                                final String insertstr = line.substring(0, valuesindex);
                                String remainingstr = line.substring(valuesindex, totallength);
                                int remaininglength = remainingstr.length();
                                String valuesstr = "";
                                final int nextindex = valuesindex;
                                while (remaininglength > 0) {
                                    final int rowindex = remainingstr.indexOf("),(");
                                    if (rowindex != -1) {
                                        valuesstr = remainingstr.substring(0, rowindex + 1);
                                        remainingstr = remainingstr.substring(rowindex + 2, remaininglength);
                                        String rowvalues = insertstr + valuesstr + ";";
                                        remaininglength = remainingstr.length();
                                        rowvalues = rowvalues.replaceAll("\"", "\\\"");
                                        stmt.addBatch(rowvalues);
                                        queryLine += rowvalues;
                                    }
                                    else {
                                        if (remainingstr.endsWith(");")) {
                                            String rowvalues = insertstr + remainingstr;
                                            rowvalues = rowvalues.replaceAll("\"", "\\\"");
                                            stmt.addBatch(rowvalues);
                                            queryLine += rowvalues;
                                            break;
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            line = line.replaceAll("\"", "'");
                            stmt.addBatch(line);
                            queryLine += line;
                            break;
                        }
                        else {
                            templine = in.readLine();
                            ++i;
                            if (templine == null) {
                                continue;
                            }
                            templine = templine.trim();
                            size_bytes += templine.length();
                            line += templine;
                        }
                    }
                }
                if (size_bytes > buffer_size) {
                    final long dumpStartTime = System.currentTimeMillis();
                    stmt.executeBatch();
                    final long dumpEndTime = System.currentTimeMillis();
                    final Long timeDiff = dumpEndTime - dumpStartTime;
                    if (timeDiff > 1000L * debugTime) {
                        CommandExecutor.logger.log(Level.FINE, "Time Of Query Execution is {0} sec", timeDiff / 1000L);
                        CommandExecutor.logger.log(Level.FINE, "Query taking more time is {0}", queryLine);
                    }
                    queryLine = "";
                    size_bytes = 0L;
                }
                line = in.readLine();
                ++i;
                if (line != null) {
                    size_bytes += line.length();
                }
                else {
                    if (size_bytes <= 0L) {
                        continue;
                    }
                    stmt.executeBatch();
                    queryLine = "";
                }
            }
        }
        catch (final SQLException ex) {
            CommandExecutor.logger.info("Error Query is" + line);
            CommandExecutor.logger.log(Level.INFO, "DB sync failue excepion prints :: ", ex);
            ex.printStackTrace();
            ex.getNextException();
            throw ex;
        }
        catch (final Exception ex2) {
            CommandExecutor.logger.info("Error Query is" + line);
            CommandExecutor.logger.log(Level.INFO, "DB sync failue excepion prints :: ", ex2);
            ex2.printStackTrace();
            throw ex2;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                CommandExecutor.logger.log(Level.INFO, "DB sync failue excepion prints :: ", ex3);
                ex3.printStackTrace();
            }
        }
    }
    
    public static void dumpDataMYSQL(final String dumpFile) throws Exception {
        final String osName = System.getProperty("os.name");
        String cmd = "";
        MySQLDSParser.parseFile();
        String mysqlHome = MySQLDSParser.getMySQLHome();
        final String user = MySQLDSParser.getMySQLUser();
        final String password = MySQLDSParser.getMySQLPassword();
        final String dbName = MySQLDSParser.getMySQLDBName();
        final String serverHome = MySQLDSParser.getServerHome();
        final int mysqlPort = MySQLDSParser.getMySQLPort();
        final String mysqlHost = MySQLDSParser.getMySQLHost();
        ConsoleOut.println("The System properties : " + System.getProperties());
        if (mysqlHome == null || mysqlHome == "") {
            ConsoleOut.println("mysqlHome NOT SET. Taking default Home");
            mysqlHome = serverHome + File.separator + "mysql";
        }
        if (osName.indexOf("Windows") >= 0) {
            final File f = new File(mysqlHome);
            mysqlHome = f.getAbsolutePath();
            ConsoleOut.println("The mysql home here : " + mysqlHome);
            cmd = ".." + File.separator + "mysql" + File.separator + "bin" + File.separator + "mysql.exe --user=" + user + " --password=" + password + " -h " + mysqlHost + " -P " + mysqlPort + " " + dbName + " < " + dumpFile;
            execute(new String[] { "cmd.exe", "/c", cmd });
        }
        else if (osName.indexOf("Linux") >= 0) {
            try {
                final String sockFile = mysqlHome + File.separator + "tmp" + File.separator + "mysql.sock";
                cmd = mysqlHome + File.separator + "bin" + File.separator + "mysql --user=" + user + " --password=" + password + " -h " + mysqlHost + " -P " + mysqlPort + " -S " + sockFile + " " + dbName + " < " + dumpFile;
                ConsoleOut.println("COMMAND IS " + cmd);
                execute(new String[] { "/bin/sh", "-c", cmd });
            }
            catch (final Exception e) {
                if (e.getMessage().startsWith("Error while executing")) {
                    ConsoleOut.println("Exception while updating data");
                    throw e;
                }
                throw e;
            }
        }
        ConsoleOut.println("Command " + cmd);
    }
    
    public static String execute(final String[] command) throws Exception {
        final StringBuffer strBuff = new StringBuffer();
        final String[] cmd = command;
        try {
            InputStream consoleInput = null;
            InputStream consoleError = null;
            String readstr = "";
            final Process process = Runtime.getRuntime().exec(cmd);
            consoleInput = process.getInputStream();
            consoleError = process.getErrorStream();
            final BufferedReader readinput = new BufferedReader(new InputStreamReader(consoleInput));
            final BufferedReader readerr = new BufferedReader(new InputStreamReader(consoleError));
            final Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String readStr;
                        while ((readStr = readinput.readLine()) != null) {
                            strBuff.append(readStr);
                            strBuff.append(System.getProperty("line.separator"));
                            try {
                                if (process.exitValue() == 0) {
                                    return;
                                }
                                continue;
                            }
                            catch (final IllegalThreadStateException ex) {}
                        }
                    }
                    catch (final IOException ioex) {
                        CommandExecutor.logger.log(Level.INFO, "DB sync failue excepion prints :: ", ioex);
                        ioex.printStackTrace();
                    }
                }
            }, "ExecInputRead");
            th.start();
            try {
                if ((readstr = readerr.readLine()) != null) {
                    ConsoleOut.println("Error while executing command----> " + readstr);
                    throw new Exception("Error while executing command " + readstr);
                }
            }
            catch (final IOException io) {
                ConsoleOut.println("Input error" + io);
                throw new Exception(io.getMessage());
            }
            process.waitFor();
            th.join();
            return strBuff.toString();
        }
        catch (final Exception e) {
            CommandExecutor.logger.log(Level.INFO, "DB sync failue excepion prints :: ", e);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            ConsoleOut.println("USAGE : java CommandExecutor <sqlfilename>");
            System.exit(1);
        }
        final String dbServer = PersistenceInitializer.getConfigurationValue("DBName");
        if (dbServer.equalsIgnoreCase("mssql")) {
            dumpData(args[0]);
        }
        else {
            dumpDataMYSQL(args[0]);
        }
    }
    
    static {
        CommandExecutor.logger = Logger.getLogger("PatchManagementLogger");
    }
}
