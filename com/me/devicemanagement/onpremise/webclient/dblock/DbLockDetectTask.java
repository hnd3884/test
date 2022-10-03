package com.me.devicemanagement.onpremise.webclient.dblock;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import com.zoho.framework.utils.FileUtils;
import com.me.devicemanagement.onpremise.webclient.support.UploadAction;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import org.json.JSONObject;
import org.json.JSONArray;
import com.me.devicemanagement.onpremise.server.util.CommonUpdatesUtil;
import java.sql.Timestamp;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.util.DebugUtil;
import java.util.Calendar;
import java.io.File;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.sql.SQLProvider;
import com.adventnet.db.api.RelationalAPI;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DbLockDetectTask implements SchedulerExecutionInterface
{
    public static final String TASK_NAME = "DbLockDetectTask";
    private static Logger logger;
    private static Logger transactionLogger;
    private ArrayList<Integer> sessionIDToBeKilled;
    private String transactionLogPropsSQL;
    private String log_reuse_wait_desc;
    private String recovery_model_desc;
    private Double log_size;
    private BigDecimal log_percentage_used;
    private Long log_percentage_used_long;
    private String sourceClass;
    
    public DbLockDetectTask() {
        this.sessionIDToBeKilled = new ArrayList<Integer>();
        this.transactionLogPropsSQL = null;
        this.log_reuse_wait_desc = "";
        this.recovery_model_desc = "";
        this.log_size = 0.0;
        this.log_percentage_used = BigDecimal.valueOf(0.0);
        this.log_percentage_used_long = 0L;
        this.sourceClass = "DbLockDetectTask";
        this.transactionLogPropsSQL = "SELECT name,log_reuse_wait_desc,log_reuse_wait,recovery_model_desc, pc1.cntr_value/1024.0  AS [log_size],cast(pc2.cntr_value*100.0/pc1.cntr_value as dec(5,2))as [log_percentage_used]\n FROM   sys.databases as b inner join  sys.dm_os_performance_counters pc1 on pc1.instance_name = b.name \n JOIN   sys.dm_os_performance_counters as pc2 ON pc1.instance_name = pc2.instance_name\n WHERE  pc1.instance_name like '%%DBNAME%%' AND    pc2.object_name LIKE '%Databases%' AND    pc2.object_name LIKE '%Databases%' AND    pc1.counter_name = 'Log File(s) Size (KB)'\n AND    pc2.counter_name = 'Log File(s) Used Size (KB)' AND    pc1.instance_name not in ('_Total', 'mssqlsystemresource') AND    pc1.cntr_value > 0";
        final String dbname = SYMClientUtil.getDataBaseName();
        this.transactionLogPropsSQL = this.transactionLogPropsSQL.replaceAll("%DBNAME%", dbname);
    }
    
    public void executeTask(final Properties taskProps) {
        this.checkForDBLock();
        if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
            this.checkForActiveTransactions();
        }
        else {
            DbLockDetectTask.transactionLogger.log(Level.FINE, "Not Supported for the current db ");
        }
    }
    
    private void checkForDBLock() {
        final String sourceMethod = "checkForDBLock";
        final Long startTime = new Long(System.currentTimeMillis());
        SyMLogger.info(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "DbLockDetect Task is invoked at " + startTime);
        if (DBUtil.getActiveDBName().equalsIgnoreCase("mysql")) {
            SyMLogger.info(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Table Lock Detection Feature is not supported in MySql");
        }
        else {
            Connection con = null;
            DataSet rs = null;
            try {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                final RelationalAPI relapi = RelationalAPI.getInstance();
                final String dbname = SYMClientUtil.getDataBaseName();
                final Properties variableProps = new Properties();
                variableProps.setProperty("%DB_NAME%", dbname);
                final String dblockSQL = SQLProvider.getInstance().getSQLString(17000, variableProps);
                con = relapi.getConnection();
                rs = relapi.executeQuery(dblockSQL, con);
                final StringBuilder lock = new StringBuilder();
                final long current_time = System.currentTimeMillis();
                final int col = rs.getColumnCount();
                long longerquerytime = 0L;
                int number_of_queries_locked = 0;
                final DataObject dblocksettingsdo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
                SyMLogger.info(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "SQL    " + dblocksettingsdo.toString());
                final Row settingsRow = dblocksettingsdo.getRow("DbLockSettings");
                final long lockMaxTime = (long)settingsRow.get("LOCK_TIME_LIMIT");
                while (rs.next()) {
                    final Timestamp t = rs.getAsTimestamp("time");
                    final long time = current_time - t.getTime();
                    final long minutes = time / 60000L;
                    if (minutes >= lockMaxTime) {
                        lock.append("Query executed for Time in minutes: ");
                        lock.append(String.valueOf(minutes));
                        lock.append("\n");
                        for (int i = 1; i <= col; ++i) {
                            lock.append(rs.getColumnName(i)).append("\t");
                            lock.append(rs.getValue(i)).append("\n");
                        }
                        lock.append("\n");
                        if (longerquerytime < time) {
                            longerquerytime = time;
                        }
                        ++number_of_queries_locked;
                    }
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
                if (number_of_queries_locked > 0) {
                    final String path = SyMUtil.getDbLocksFilePath();
                    final File dblocksdir = new File(path);
                    if (!dblocksdir.exists()) {
                        dblocksdir.mkdir();
                    }
                    final String time2 = dateFormat.format(Calendar.getInstance().getTime()).toString().trim();
                    final String pathforfile = path + File.separator + "dblock_" + time2 + ".txt";
                    lock.append("Thread Dump:\n");
                    lock.append(DebugUtil.generateThreaddump());
                    ApiFactoryProvider.getFileAccessAPI().writeFile(pathforfile, lock.toString().getBytes());
                    final int blockedThreadsCount = this.getBlockedThreadsCount();
                    final Row r = new Row("DbLockInfo");
                    r.set("FILE_NAME", (Object)("dblock_" + time2.trim() + ".txt"));
                    r.set("CREATED_TIME", (Object)current_time);
                    r.set("NO_OF_QUERIES_LOCKED", (Object)new Integer(number_of_queries_locked));
                    r.set("LOCKED_QUERY_MAX_TIME", (Object)longerquerytime);
                    r.set("THREADS_BLOCKED", (Object)blockedThreadsCount);
                    final DataObject d = (DataObject)new WritableDataObject();
                    d.addRow(r);
                    SyMUtil.getPersistence().add(d);
                    if (blockedThreadsCount > 100) {
                        final Boolean automatic_mail = (Boolean)settingsRow.get("IS_AUTOMATIC");
                        if (automatic_mail) {
                            this.checkAndUpload();
                        }
                    }
                }
                final Long endTime = new Long(System.currentTimeMillis());
                SyMLogger.info(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "DbLockDetect Task took " + (endTime - startTime) + " ms");
                SyMLogger.info(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Completed execution of DBDeadLockDetect Task.");
            }
            catch (final Exception ex) {
                SyMLogger.error(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Caught exception while executing DBDeadLockDetect task.", (Throwable)ex);
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final SQLException ex2) {
                        SyMLogger.error(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Exception while closing dataset ", (Throwable)ex2);
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (final SQLException ex2) {
                        SyMLogger.error(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Exception while closing connection ", (Throwable)ex2);
                    }
                }
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final SQLException ex3) {
                        SyMLogger.error(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Exception while closing dataset ", (Throwable)ex3);
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (final SQLException ex3) {
                        SyMLogger.error(DbLockDetectTask.logger, this.sourceClass, sourceMethod, "Exception while closing connection ", (Throwable)ex3);
                    }
                }
            }
        }
    }
    
    private void checkForActiveTransactions() {
        final String criticalDurationS = CommonUpdatesUtil.getInstance().getValue("criticalDuration");
        final String criticalLogPercentageUsedS = CommonUpdatesUtil.getInstance().getValue("criticalLogPercentageUsed");
        final String minimumLogSizeS = CommonUpdatesUtil.getInstance().getValue("minimumLogSize");
        final Long criticalDuration = (criticalDurationS == null) ? 180L : Long.valueOf(criticalDurationS);
        final Long criticalLogPercentageUsed = (criticalLogPercentageUsedS == null) ? 80L : Long.valueOf(criticalLogPercentageUsedS);
        final Double minimumLogSize = (minimumLogSizeS == null) ? 4000.0 : Double.valueOf(minimumLogSizeS);
        Connection con = null;
        DataSet rs = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            DbLockDetectTask.transactionLogger.log(Level.INFO, "Active transaction log check has started");
            this.getTransactionLogProperties();
            if (this.log_size > minimumLogSize && this.log_reuse_wait_desc.equalsIgnoreCase("ACTIVE_TRANSACTION")) {
                Long maxDuration = 0L;
                Long xactID = 0L;
                String xactIDS = "";
                con = relapi.getConnection();
                final String openTransactionSQL = "SELECT st.session_id AS [SPID], DB_NAME(dt.database_id) AS [DBName], dt.database_transaction_begin_time AS [StartedAt],DATEDIFF(mi,dt.database_transaction_begin_time,GETDATE()) AS Duration,\n at.name AS [Transactiontype], st.transaction_id AS [TransactionID],st.open_transaction_count,sh.text as most_recent_sql_text FROM sys.dm_tran_database_transactions dt \n INNER JOIN sys.dm_tran_session_transactions st on dt.transaction_id = st.transaction_id \n INNER JOIN sys.dm_tran_active_transactions at on at.transaction_id = st.transaction_id \n INNER JOIN sys.dm_exec_connections dc on dc.session_id = st.session_id\n outer apply sys.dm_exec_sql_text(most_recent_sql_handle)sh\n WHERE database_transaction_state=4 AND database_transaction_type = 1 ORDER BY dt.database_transaction_begin_time";
                final JSONArray activeTransaction = new JSONArray();
                rs = relapi.executeQuery(openTransactionSQL, con);
                if (rs != null) {
                    while (rs.next()) {
                        final JSONObject json = new JSONObject();
                        final int spid = (int)rs.getValue(1);
                        json.put("SPID", spid);
                        json.put("DBName", rs.getValue(2));
                        json.put("StartedAt", rs.getValue(3));
                        final Long duration = rs.getAsLong(4);
                        if (duration > criticalDuration && this.log_percentage_used_long > criticalLogPercentageUsed) {
                            this.sessionIDToBeKilled.add(spid);
                        }
                        if (maxDuration < duration) {
                            maxDuration = duration;
                        }
                        json.put("Duration", (Object)duration);
                        json.put("TransactionType", rs.getValue(5));
                        xactID = rs.getAsLong(6);
                        if (xactIDS.equals("")) {
                            xactIDS += xactID;
                        }
                        else {
                            xactIDS = xactIDS + "," + xactID;
                        }
                        json.put("TransactionID", (Object)xactID);
                        json.put("OpenTransactionCount", rs.getValue(7));
                        String recentQuery = rs.getAsString(8);
                        recentQuery = recentQuery.replaceAll("\"", "");
                        json.put("MostRecentSQLQuery", (Object)recentQuery);
                        activeTransaction.put((Object)json);
                    }
                    DbLockDetectTask.transactionLogger.log(Level.INFO, "ACTIVE TRANSACTIONS DETAILS :\n" + activeTransaction.toString());
                    if (!xactIDS.equalsIgnoreCase("") && this.sessionIDToBeKilled.size() > 0) {
                        DbLockDetectTask.transactionLogger.log(Level.INFO, "Log Percentage used is more than :" + criticalLogPercentageUsed + " and transaction was active for more than " + criticalDuration + " mins ");
                        this.getTableDetailsOnTransaction(xactIDS);
                        DbLockDetectTask.transactionLogger.log(Level.INFO, "Exiting the task. Control will be passed to new thread for killing the active sessions");
                        final KillSessions ks = new KillSessions();
                        ks.start();
                    }
                }
                else {
                    DbLockDetectTask.transactionLogger.log(Level.INFO, "No Active Transactions Found ");
                }
            }
            DbLockDetectTask.transactionLogger.log(Level.INFO, "Active transaction log check has been completed ");
        }
        catch (final Exception e) {
            DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while checking active transaction : ", e);
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing dataset ", ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException ex) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing connection ", ex);
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex2) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing dataset ", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException ex2) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing connection ", ex2);
                }
            }
        }
    }
    
    private void getTransactionLogProperties() {
        Connection con = null;
        DataSet rs = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            con = relapi.getConnection();
            rs = relapi.executeQuery(this.transactionLogPropsSQL, con);
            if (rs != null) {
                if (rs.next()) {
                    this.log_size = (Double)rs.getValue("log_size");
                    this.log_percentage_used = (BigDecimal)rs.getValue("log_percentage_used");
                    this.log_percentage_used_long = this.log_percentage_used.longValue();
                    this.recovery_model_desc = (String)rs.getValue("recovery_model_desc");
                    this.log_reuse_wait_desc = (String)rs.getValue("log_reuse_wait_desc");
                }
                DbLockDetectTask.transactionLogger.log(Level.INFO, "Database Transaction Log Properties :\n LOG_SIZE :" + this.log_size + " MB, LOG_PERCENTAGE_USED :" + this.log_percentage_used + ", RECOVERY_MODEL :" + this.recovery_model_desc + ", LOG_REUSE_WAIT :" + this.log_reuse_wait_desc);
            }
        }
        catch (final Exception e) {
            DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while retrieving transaction logs properties : ", e);
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing dataset ", ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException ex) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing connection ", ex);
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex2) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing dataset ", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException ex2) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing connection ", ex2);
                }
            }
        }
    }
    
    private void getTableDetailsOnTransaction(final String xactIDS) {
        Connection con = null;
        DataSet rs = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            con = relapi.getConnection();
            final String transDetails = "select top 100 Operation,[Transaction ID],AllocUnitName,[Transaction Name],[Xact Type],spid,[Oldest Active Transaction ID],[Parent Transaction ID] FROM   fn_dblog(null,null)where [Transaction ID] in (select [Transaction ID] from fn_dblog(null,null) where [Xact ID] in (" + xactIDS + "));";
            rs = relapi.executeQuery(transDetails, con);
            final JSONArray transDetail = new JSONArray();
            if (rs != null) {
                while (rs.next()) {
                    final JSONObject transDetailRow = new JSONObject();
                    transDetailRow.put("Operation", rs.getValue(1));
                    transDetailRow.put("TransactionID", rs.getValue(2));
                    transDetailRow.put("Associated", rs.getValue(3));
                    transDetailRow.put("Name", rs.getValue(4));
                    transDetailRow.put("spid", rs.getValue(6));
                    transDetailRow.put("OldestActiveTransID", rs.getValue(7));
                    transDetail.put((Object)transDetailRow);
                }
            }
            DbLockDetectTask.transactionLogger.log(Level.INFO, "MORE DETAILS ON THE TRANSACTION \n" + transDetail.toString());
        }
        catch (final Exception e) {
            DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while retrieving transaction table details: ", e);
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing dataset ", ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException ex) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing connection ", ex);
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex2) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing dataset ", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException ex2) {
                    DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while closing connection ", ex2);
                }
            }
        }
    }
    
    private int getBlockedThreadsCount() {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadinfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
        int blockedthreadscount = 0;
        for (final ThreadInfo threadInfo : threadinfos) {
            if (threadInfo.getThreadState().name().equalsIgnoreCase("BLOCKED")) {
                ++blockedthreadscount;
            }
        }
        return blockedthreadscount;
    }
    
    private void checkAndUpload() {
        try {
            final SelectQuery Query = (SelectQuery)new SelectQueryImpl(Table.getTable("DbLockInfo"));
            final Column selCol = Column.getColumn((String)null, "*");
            Query.addSelectColumn(selCol);
            Query.addSortColumn(new SortColumn(Column.getColumn("DbLockInfo", "CREATED_TIME"), true));
            final Criteria c = new Criteria(new Column("DbLockInfo", "IS_DELETED"), (Object)"false", 0);
            Query.setCriteria(c);
            final DataObject lockdo = SyMUtil.getPersistence().get(Query);
            if (!lockdo.isEmpty()) {
                final Row r = lockdo.getFirstRow("DbLockInfo");
                final Long time = (Long)r.get("CREATED_TIME");
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(5, -1);
                final Long lasttime = cal.getTimeInMillis();
                DbLockDetectTask.logger.log(Level.INFO, "lasttime{0} time{1}", new Object[] { lasttime, time });
                if (time < lasttime && uploadAndDeleteFilesFromDir()) {
                    final DataObject lockDoFalse = SyMUtil.getPersistence().get("DbLockInfo", c);
                    final Iterator dblockfiles = lockDoFalse.getRows("DbLockInfo");
                    while (dblockfiles.hasNext()) {
                        final Row lockfile = dblockfiles.next();
                        lockfile.set("IS_DELETED", (Object)Boolean.TRUE);
                        lockDoFalse.updateRow(lockfile);
                        DbLockDetectTask.logger.log(Level.INFO, lockfile.toString());
                    }
                    SyMUtil.getPersistence().update(lockDoFalse);
                }
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(DbLockDetectTask.class.getName()).log(Level.SEVERE, "Exception in retrieving dblockinfo table.", (Throwable)ex);
        }
    }
    
    private static Boolean uploadAndDeleteFilesFromDir() {
        try {
            final String path = System.getProperty("server.home") + File.separator + "logs" + File.separator;
            final String frAdd = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails().get("mail.fromAddress");
            final String subject = I18N.getMsg("dc.admin.dblock.mail.subject", new Object[0]);
            final File f = new File(SyMUtil.getDbLocksFilePath());
            final File server_info = new File(path + "server_info.props");
            final File build_history = new File(path + "build-history.txt");
            if (build_history.exists()) {
                performFileCopy(build_history, new File(f, "build-history.txt"));
            }
            if (server_info.exists()) {
                performFileCopy(server_info, new File(f, "server_info.props"));
            }
            final ZipUtil zipprocess = new ZipUtil();
            final File dest = new File(path + "Dblocks.7z");
            if (dest.exists()) {
                dest.delete();
            }
            zipprocess.createZipFile(f.getAbsolutePath(), dest.getAbsolutePath(), false, null);
            if (UploadAction.doUpload("DbLocks.7z", frAdd, subject, null, false)) {
                return FileUtils.deleteDir(f);
            }
            DbLockDetectTask.logger.log(Level.SEVERE, "Dblocks support file upload failed");
            return false;
        }
        catch (final Exception ex) {
            Logger.getLogger(DbLockDetectTask.class.getName()).log(Level.SEVERE, "Exception in sending mail and deleting files", ex);
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "DbLockDetectTask";
    }
    
    private static void performFileCopy(final File src, final File dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        catch (final IOException e) {
            Logger.getLogger(DbLockDetectTask.class.getName()).log(Level.SEVERE, "Exception in performing file copy", e);
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (final IOException ex) {
                Logger.getLogger(DbLockDetectTask.class.getName()).log(Level.SEVERE, "Exception in closing input and output streams ..", ex);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (final IOException ex2) {
                Logger.getLogger(DbLockDetectTask.class.getName()).log(Level.SEVERE, "Exception in closing input and output streams ..", ex2);
            }
        }
    }
    
    static {
        DbLockDetectTask.logger = Logger.getLogger(DbLockDetectTask.class.getName());
        DbLockDetectTask.transactionLogger = Logger.getLogger("ActiveTransactionLogger");
    }
    
    private class KillSessions extends Thread
    {
        @Override
        public void run() {
            try {
                DbLockDetectTask.transactionLogger.log(Level.INFO, "THREAD DUMP BEFORE KILLING SESSION\n\n" + DebugUtil.generateThreaddump());
                final RelationalAPI relapi = RelationalAPI.getInstance();
                final String openTransactionKilledInMSSQLS = SyMUtil.getServerParameter("open_transaction_killed_in_mssql");
                Long openTransactionKilledInMSSQL = (openTransactionKilledInMSSQLS == null) ? 0L : Long.valueOf(openTransactionKilledInMSSQLS);
                DbLockDetectTask.transactionLogger.log(Level.INFO, "Session IDs to be killed :" + DbLockDetectTask.this.sessionIDToBeKilled.toString());
                for (final int spid : DbLockDetectTask.this.sessionIDToBeKilled) {
                    try {
                        final String killQuery = "KILL " + spid;
                        relapi.execute(killQuery);
                        Thread.sleep(10000L);
                    }
                    catch (final Exception e) {
                        DbLockDetectTask.transactionLogger.log(Level.INFO, "Exception while killing open transaction" + spid, e);
                    }
                }
                openTransactionKilledInMSSQL += (Long)DbLockDetectTask.this.sessionIDToBeKilled.size();
                SyMUtil.updateServerParameter("open_transaction_killed_in_mssql", String.valueOf(openTransactionKilledInMSSQL));
                DbLockDetectTask.transactionLogger.log(Level.INFO, "THREAD DUMP AFTER KILLING SESSION\n\n" + DebugUtil.generateThreaddump());
                Thread.sleep(5000L);
                DbLockDetectTask.transactionLogger.log(Level.INFO, "Transaction Log properties after killing active sessions");
                DbLockDetectTask.this.getTransactionLogProperties();
            }
            catch (final Exception e2) {
                DbLockDetectTask.transactionLogger.log(Level.WARNING, "Exception while killing open sessions", e2);
            }
        }
    }
}
