package com.me.devicemanagement.onpremise.webclient.support;

import java.util.Hashtable;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.onpremise.server.util.FileFilterUtil;
import java.io.IOException;
import java.util.Iterator;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.me.devicemanagement.onpremise.webclient.util.SupportUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.io.File;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.logging.Logger;

public class Compress
{
    private static String fs;
    private static String className;
    private static Logger print;
    private static ZipOutputStream out;
    private static GZIPOutputStream out1;
    private static File zipFile;
    
    public static String createSupportFile(String fileName, final Properties supportProps) throws Exception {
        Compress.print.log(Level.INFO, "Invoked Copy & zip file creation method !!");
        String outFilename = null;
        final String dblockfile = SyMUtil.getDbLocksFilePath();
        final String s = ".." + Compress.fs + "logs";
        final String wanAgentLogs = ".." + Compress.fs + "agent-logs";
        final String mdmLogs = ".." + Compress.fs + "mdm-logs";
        final String wanDSLogs = ".." + Compress.fs + "ds-logs";
        final SupportUtil supportUtil = new SupportUtil();
        final boolean isServerLogUpload = ((Hashtable<K, Boolean>)supportProps).get("hasServerLogs");
        final boolean isAgentLogUpload = ((Hashtable<K, Boolean>)supportProps).get("hasAgentLogs");
        final boolean isMDMLogUpload = ((Hashtable<K, Boolean>)supportProps).get("hasMDMLogs");
        final boolean isDbLockFileUpload = ((Hashtable<K, Boolean>)supportProps).get("hasDBLockLogs");
        final boolean isDSLogUpload = ((Hashtable<K, Boolean>)supportProps).get("hasDSLogs");
        final String[] uploadLogIDs = ((Hashtable<K, Object>)supportProps).get("uploadLogIDs").toString().split(",");
        try {
            Compress.print.log(Level.INFO, "Copying files from logs directory..");
            final File logsDir = new File(s);
            final File supportDir = new File(s + Compress.fs + "support");
            if (!supportDir.exists()) {
                supportDir.mkdir();
            }
            if (supportUtil.getSupportConfFile() != null && !supportUtil.getSupportConfFile().isEmpty()) {
                if (uploadLogIDs != null && uploadLogIDs.length > 0) {
                    JSONArray supportLogListFromFile = supportUtil.getLogListFromConfFile();
                    if (supportLogListFromFile != null && supportLogListFromFile.size() > 0) {
                        supportLogListFromFile = supportUtil.filterLogList(supportLogListFromFile, uploadLogIDs);
                        Compress.print.log(Level.FINE, "Started to copy log files");
                        for (final Object objectItr : supportLogListFromFile) {
                            final JSONObject jsonObject = (JSONObject)objectItr;
                            final Boolean isNegation = (Boolean)jsonObject.getOrDefault((Object)"isNegate", (Object)false);
                            JSONArray fileList;
                            if (isNegation) {
                                fileList = (JSONArray)jsonObject.get((Object)"negate");
                            }
                            else {
                                fileList = (JSONArray)jsonObject.get((Object)"logFilePath");
                            }
                            for (final Object filePath : fileList) {
                                final String filename = System.getProperty("server.home") + Compress.fs + filePath;
                                final File srcDir = new File(filename);
                                File destDir = supportDir;
                                if (jsonObject.containsKey((Object)"newFolderName")) {
                                    destDir = new File(supportDir.getPath() + Compress.fs + jsonObject.get((Object)"newFolderName"));
                                }
                                if (srcDir.exists() && srcDir.isDirectory()) {
                                    copyDirectory(srcDir, destDir);
                                }
                                else {
                                    if (!srcDir.isFile()) {
                                        continue;
                                    }
                                    destDir = new File(destDir.getAbsolutePath() + Compress.fs + srcDir.getName());
                                    copyFile(srcDir, destDir);
                                }
                            }
                        }
                    }
                }
            }
            else {
                final File mysqldata = new File(System.getProperty("server.home") + Compress.fs + "mysql" + Compress.fs + "data");
                if (isServerLogUpload) {
                    copyDirectory(logsDir, supportDir);
                    copymysqllog(mysqldata, supportDir);
                }
                if (isMDMLogUpload) {
                    Compress.print.log(Level.INFO, "Copying MDM Logs to the directory..");
                    final File mdmLogsDir = new File(mdmLogs);
                    final File mdmSupportDir = new File(s + Compress.fs + "support" + Compress.fs + "mdm-logs");
                    copyDirectory(mdmLogsDir, mdmSupportDir);
                }
                Compress.print.log(Level.INFO, "Copying AGENT LOGS TO directory..");
                final File wanAgentLogsDir = new File(wanAgentLogs);
                final File agentSupportDir = new File(s + Compress.fs + "support" + Compress.fs + "wan-agents");
                if (isAgentLogUpload) {
                    copyDirectory(wanAgentLogsDir, agentSupportDir);
                }
                Compress.print.log(Level.INFO, "Copying DS LOGS TO directory..");
                final File wanDSLogsDir = new File(wanDSLogs);
                final File dsSupportDir = new File(s + Compress.fs + "support" + Compress.fs + "wan-ds");
                if (isDSLogUpload) {
                    copyDirectory(wanDSLogsDir, dsSupportDir);
                }
                final File dblockfiledir = new File(dblockfile);
                if (isDbLockFileUpload) {
                    final File BuildHistoryFile = new File(System.getProperty("server.home") + Compress.fs + "logs" + Compress.fs + "build-history.txt");
                    if (BuildHistoryFile.exists()) {
                        performFileCopy(BuildHistoryFile, new File(supportDir, "build-history.txt"));
                    }
                    Compress.print.log(Level.INFO, "Copying DBLOCKS LOGS TO directory..");
                    copyDirectory(dblockfiledir, supportDir);
                }
                Compress.print.log(Level.INFO, "Copying files from bin directory..");
                final String srdir = System.getProperty("server.home");
                final File serverDir = new File(System.getProperty("server.home") + Compress.fs + "bin");
                if (!isDbLockFileUpload) {
                    copyDirectory(serverDir, supportDir);
                }
                Compress.print.log(Level.INFO, "Copying install.conf ..");
                final File installConfFile = new File(System.getProperty("server.home") + Compress.fs + "conf" + Compress.fs + "install.conf");
                if (installConfFile.exists()) {
                    performFileCopy(installConfFile, new File(supportDir, "install.conf"));
                }
                if (!isServerLogUpload) {
                    Compress.print.log(Level.INFO, "Copying install.conf ..");
                    final File serverLogFile = new File(System.getProperty("server.home") + Compress.fs + "logs" + Compress.fs + "server_info.props");
                    if (serverLogFile.exists()) {
                        performFileCopy(serverLogFile, new File(supportDir, "server_info.props"));
                    }
                    Compress.print.log(Level.INFO, "Copying install.conf ..");
                    final File wrapperLogFile = new File(System.getProperty("server.home") + Compress.fs + "logs" + Compress.fs + "wrapper.txt");
                    if (wrapperLogFile.exists()) {
                        performFileCopy(wrapperLogFile, new File(supportDir, "wrapper.txt"));
                    }
                }
            }
            Compress.print.log(Level.INFO, "Creating Zip file ..");
            final String sevenZipFileName = fileName;
            outFilename = sevenZipFileName + ".7z";
            fileName += ".7z";
            final File supportLogsDir = new File(s + Compress.fs + "supportLogs" + Compress.fs);
            if (!supportLogsDir.exists()) {
                supportLogsDir.mkdir();
            }
            final ZipUtil zipprocess = new ZipUtil();
            final String dest = "logs" + Compress.fs + supportLogsDir.getName() + Compress.fs + outFilename;
            final String source = "logs" + Compress.fs + supportDir.getName() + Compress.fs;
            final String[] arguments = { System.getProperty("server.home") + File.separator + "bin" + Compress.fs + "7za.exe", "a", dest, source, "-mmt=" + ZipUtil.get7ZipCoreCount() };
            final boolean iszipped = zipprocess.SevenZipCommand(arguments, "");
            if (!iszipped) {
                Compress.print.log(Level.INFO, "7 Zip file creation Failed. Trying zip format");
                outFilename = performZipOperation(sevenZipFileName, zipprocess);
            }
            else {
                Compress.print.log(Level.INFO, "7 Zip file creation ended");
            }
        }
        catch (final Exception e) {
            Compress.print.log(Level.WARNING, "Error in zipping", e);
        }
        return outFilename;
    }
    
    public static String createFailoverSupportFile(final String source, final String dest, final String sevenZipFileName) throws IOException {
        final ZipUtil zipprocess = new ZipUtil();
        String outFilename = sevenZipFileName;
        final String[] arguments = { System.getProperty("server.home") + File.separator + "bin" + Compress.fs + "7za.exe", "a", dest, source, "-mmt=" + ZipUtil.get7ZipCoreCount() };
        final boolean iszipped = zipprocess.SevenZipCommand(arguments, "");
        if (!iszipped) {
            try {
                Compress.print.log(Level.INFO, "7 Zip file creation Failed. Trying zip format");
                File srcFile = null;
                File destFile = null;
                if (source.startsWith("logs")) {
                    srcFile = new File(".." + File.separator + "logs" + File.separator + "support" + File.separator);
                    destFile = new File(".." + File.separator + dest);
                }
                else {
                    srcFile = new File(source);
                    destFile = new File(".." + File.separator + dest.replaceFirst(".7z", ".zip"));
                }
                zipprocess.createZipFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath(), false, null);
                outFilename = sevenZipFileName + ".zip";
            }
            catch (final Exception ex) {
                Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            Compress.print.log(Level.INFO, "7 Zip file creation ended");
        }
        return outFilename;
    }
    
    public static String performZipOperation(final String fileName, final ZipUtil zipprocess) throws IOException {
        try {
            Compress.print.log(Level.INFO, "Zip Creation STARTED!!");
            final String s = ".." + Compress.fs + "logs";
            final File supportLogsDir = new File(s + Compress.fs + "supportLogs" + Compress.fs);
            final File tempDir = new File(s + Compress.fs + "support" + Compress.fs);
            final String dest = supportLogsDir.getAbsolutePath() + Compress.fs + fileName + ".zip";
            zipprocess.createZipFile(tempDir.getAbsolutePath(), dest, false, null);
            Compress.print.log(Level.INFO, "Zip file creation ended");
        }
        catch (final IOException ex) {
            Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, "Error During zipping Process", ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, "Error During Zipping Process", ex2);
        }
        return fileName + ".zip";
    }
    
    public static void copyDirectory(final File srcDir, final File dstDir) throws IOException {
        Compress.print.log(Level.FINEST, "Copy action started for -> source dir : " + srcDir);
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }
            final String[] subdirectory = srcDir.list();
            for (int i = 0; i < subdirectory.length; ++i) {
                if (!subdirectory[i].equalsIgnoreCase("support") && !subdirectory[i].equalsIgnoreCase("archive")) {
                    copyDirectory(new File(srcDir, subdirectory[i]), new File(dstDir, subdirectory[i]));
                }
            }
        }
        else {
            copyFile(srcDir, dstDir);
        }
        Compress.print.log(Level.FINEST, "Copy action ended for -> source dir : " + srcDir);
    }
    
    public static void copyFile(final File src, final File dst) throws IOException {
        Compress.print.log(Level.FINEST, "Getting file extention before: " + src);
        boolean isAllowed = FileFilterUtil.isAllowed(src.getName(), 100);
        final String fileName = src.getName();
        if (fileName.contains("localhost_log")) {
            final long fileTime = src.lastModified();
            Compress.print.log(Level.FINEST, fileName + "-> last modified date :" + Utils.getTime(Long.valueOf(fileTime)));
            if (!isRecentFile(fileTime)) {
                isAllowed = false;
            }
        }
        if (isAllowed) {
            performFileCopy(src, dst);
        }
        Compress.print.log(Level.FINEST, "Getting file extention after: " + src);
    }
    
    private static boolean isRecentFile(final long fileTime) {
        final long noOfDays = 3L;
        final long noOfDayL = noOfDays & 0xFFFFFFFFL;
        final long timeNow = System.currentTimeMillis();
        final long timePeriod = noOfDayL * 24L * 60L * 60L * 1000L;
        final long timeBefore = timeNow - timePeriod;
        return fileTime <= timeNow && fileTime >= timeBefore;
    }
    
    public static void performFileCopy(final File src, final File dst) throws IOException {
        Compress.print.log(Level.FINEST, "Enters for performing file copy " + src);
        final InputStream in = new FileInputStream(src);
        final OutputStream out = new FileOutputStream(dst);
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        Compress.print.log(Level.FINEST, "Copied file : " + src.getName());
    }
    
    public static void cancelOperation() throws Exception {
        if (Compress.out != null) {
            Compress.out.close();
        }
        if (Compress.out1 != null) {
            Compress.out1.finish();
            Compress.out1.close();
        }
        if (Compress.zipFile != null) {
            Compress.zipFile.delete();
        }
    }
    
    public static void copymysqllog(final File srcDir, final File dstDir) {
        Compress.print.log(Level.FINEST, "Copy action started for -> source dir : " + srcDir);
        Label_0187: {
            if (srcDir.isDirectory()) {
                if (!dstDir.exists()) {
                    dstDir.mkdirs();
                }
                final String[] subdirectory = srcDir.list();
                for (int i = 0; i < subdirectory.length; ++i) {
                    if (!subdirectory[i].equalsIgnoreCase("desktopcentral") && !subdirectory[i].equalsIgnoreCase("mysql") && !subdirectory[i].equalsIgnoreCase("webnmsdb")) {
                        copymysqllog(new File(srcDir, subdirectory[i]), new File(dstDir, subdirectory[i]));
                    }
                }
            }
            else {
                final String fileName = srcDir.getName();
                if (!fileName.contains(".err")) {
                    if (!fileName.contains(".log")) {
                        break Label_0187;
                    }
                }
                try {
                    performFileCopy(srcDir, dstDir);
                }
                catch (final IOException ex) {
                    Compress.print.log(Level.WARNING, "Exception in copy of " + srcDir);
                }
            }
        }
        Compress.print.log(Level.FINEST, "Copy action ended for -> source dir : " + srcDir);
    }
    
    static {
        Compress.fs = File.separator;
        Compress.className = Compress.class.getName();
        Compress.print = Logger.getLogger(Compress.className);
        Compress.out = null;
        Compress.out1 = null;
        Compress.zipFile = null;
    }
}
