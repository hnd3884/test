package com.me.mdm.onpremise.server.support;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.server.util.FileFilterUtil;
import java.io.IOException;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Properties;
import com.me.devicemanagement.onpremise.webclient.support.SupportFileCreation;
import java.io.File;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.logging.Logger;
import com.me.mdm.server.support.MDMCompressAPI;

public class Compress implements MDMCompressAPI
{
    private static String fs;
    private static String className;
    private static Logger print;
    private static ZipOutputStream out;
    private static GZIPOutputStream out1;
    private static File zipFile;
    private com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress;
    private SupportFileCreation dmOnpremisSupportFileCreation;
    
    public Compress() {
        this.dmOnpremiseCompress = new com.me.devicemanagement.onpremise.webclient.support.Compress();
        this.dmOnpremisSupportFileCreation = SupportFileCreation.getInstance();
    }
    
    public void createSupportFile(final Properties supportProps) throws Exception {
        Compress.print.log(Level.INFO, "Invoked Copy & zip file creation method !!");
        final String dblockfile = SyMUtil.getDbLocksFilePath();
        final String s = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "logs";
        final String mdmLogs = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "mdm-logs";
        final boolean isServerLogUpload = ((Hashtable<K, Boolean>)supportProps).get("hasServerLogs");
        final boolean isMDMLogUpload = ((Hashtable<K, Boolean>)supportProps).get("hasMDMLogs");
        final boolean isDbLockFileUpload = ((Hashtable<K, Boolean>)supportProps).get("hasDBLockLogs");
        try {
            Compress.print.log(Level.INFO, "Copying files from logs directory..");
            final File logsDir = new File(s);
            final File supportDir = new File(s + Compress.fs + "support");
            if (!supportDir.exists()) {
                supportDir.mkdirs();
            }
            final File mysqldata = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "mysql" + Compress.fs + "data");
            if (isServerLogUpload) {
                final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress = this.dmOnpremiseCompress;
                com.me.devicemanagement.onpremise.webclient.support.Compress.copyDirectory(logsDir, supportDir);
                final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress2 = this.dmOnpremiseCompress;
                com.me.devicemanagement.onpremise.webclient.support.Compress.copymysqllog(mysqldata, supportDir);
            }
            if (isMDMLogUpload) {
                Compress.print.log(Level.INFO, "Copying MDM Logs to the directory..");
                final File fromDir = new File(mdmLogs);
                final File toDir = new File(s + Compress.fs + "support" + Compress.fs + "mdm-logs");
                final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress3 = this.dmOnpremiseCompress;
                com.me.devicemanagement.onpremise.webclient.support.Compress.copyDirectory(fromDir, toDir);
            }
            final File dblockfiledir = new File(dblockfile);
            if (isDbLockFileUpload) {
                final File BuildHistoryFile = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "logs" + Compress.fs + "build-history.txt");
                if (BuildHistoryFile.exists()) {
                    final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress4 = this.dmOnpremiseCompress;
                    com.me.devicemanagement.onpremise.webclient.support.Compress.performFileCopy(BuildHistoryFile, new File(supportDir, "build-history.txt"));
                }
                Compress.print.log(Level.INFO, "Copying DBLOCKS LOGS TO directory..");
                final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress5 = this.dmOnpremiseCompress;
                com.me.devicemanagement.onpremise.webclient.support.Compress.copyDirectory(dblockfiledir, supportDir);
            }
            Compress.print.log(Level.INFO, "Copying files from bin directory..");
            final String srdir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final File serverDir = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "bin");
            if (!isDbLockFileUpload) {
                final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress6 = this.dmOnpremiseCompress;
                com.me.devicemanagement.onpremise.webclient.support.Compress.copyDirectory(serverDir, supportDir);
            }
            Compress.print.log(Level.INFO, "Copying install.conf ..");
            final File installConfFile = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "conf" + Compress.fs + "install.conf");
            if (installConfFile.exists()) {
                final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress7 = this.dmOnpremiseCompress;
                com.me.devicemanagement.onpremise.webclient.support.Compress.performFileCopy(installConfFile, new File(supportDir, "install.conf"));
            }
            if (!isServerLogUpload) {
                Compress.print.log(Level.INFO, "Copying install.conf ..");
                final File serverLogFile = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "logs" + Compress.fs + "server_info.props");
                if (serverLogFile.exists()) {
                    final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress8 = this.dmOnpremiseCompress;
                    com.me.devicemanagement.onpremise.webclient.support.Compress.performFileCopy(serverLogFile, new File(supportDir, "server_info.props"));
                }
                Compress.print.log(Level.INFO, "Copying install.conf ..");
                final File wrapperLogFile = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "logs" + Compress.fs + "wrapper.txt");
                if (wrapperLogFile.exists()) {
                    final com.me.devicemanagement.onpremise.webclient.support.Compress dmOnpremiseCompress9 = this.dmOnpremiseCompress;
                    com.me.devicemanagement.onpremise.webclient.support.Compress.performFileCopy(wrapperLogFile, new File(supportDir, "wrapper.txt"));
                }
            }
        }
        catch (final Exception var31) {
            Compress.print.log(Level.WARNING, "Error in zipping", var31);
        }
    }
    
    public String compressSupportFile(String fileName) throws Exception {
        Compress.print.log(Level.INFO, "Creating Zip file ..");
        String outFilename = "";
        final String s = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + Compress.fs + "logs";
        final String sevenZipFileName = fileName;
        fileName = (outFilename = fileName + ".7z");
        final File wrapperLogFile = new File(s + Compress.fs + "supportLogs" + Compress.fs);
        if (!wrapperLogFile.exists()) {
            wrapperLogFile.mkdirs();
        }
        final String dest = "logs" + Compress.fs + wrapperLogFile.getName() + Compress.fs + outFilename;
        final String source = "logs" + Compress.fs + new File(s + Compress.fs + "support").getName() + Compress.fs;
        final String[] arguments = { ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "bin" + Compress.fs + "7za.exe", "a", dest, source };
        final boolean iszipped = new ZipUtil().SevenZipCommand(arguments, "");
        if (!iszipped) {
            Compress.print.log(Level.INFO, "7 Zip file creation Failed. Trying zip format");
            outFilename = performZipOperation(sevenZipFileName);
        }
        else {
            Compress.print.log(Level.INFO, "7 Zip file creation ended");
        }
        return outFilename;
    }
    
    public String getSupportFileName() throws Exception {
        return "MDMPLogsToSupportTeam";
    }
    
    public String getSupportMailSubject() throws Exception {
        return "MDM Logs For Diagnosis";
    }
    
    public static String performZipOperation(final String fileName) throws IOException {
        try {
            Compress.print.log(Level.INFO, "Zip Creation STARTED!!");
            final String s = ".." + Compress.fs + "logs";
            final File supportLogsDir = new File(s + Compress.fs + "supportLogs" + Compress.fs);
            final File tempDir = new File(s + Compress.fs + "support" + Compress.fs);
            final String dest = supportLogsDir.getAbsolutePath() + Compress.fs + fileName + ".zip";
            new ZipUtil().createZipFile(tempDir.getAbsolutePath(), dest, false, (String)null);
            Compress.print.log(Level.INFO, "Zip file creation ended");
        }
        catch (final IOException var6) {
            Logger.getLogger(com.me.devicemanagement.onpremise.webclient.support.Compress.class.getName()).log(Level.SEVERE, "Error During zipping Process", var6);
        }
        catch (final Exception var7) {
            Logger.getLogger(com.me.devicemanagement.onpremise.webclient.support.Compress.class.getName()).log(Level.SEVERE, "Error During Zipping Process", var7);
        }
        return fileName + ".zip";
    }
    
    public String createFailoverSupportFile(final String source, final String dest, final String sevenZipFileName) throws IOException {
        return null;
    }
    
    public void cancelOperation() throws Exception {
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
    
    public void cleanSupportFolder(final boolean clear) throws Exception {
        try {
            Compress.print.log(Level.INFO, "Clean support folder method is invoked.");
            final String serverDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String desPath = serverDir + Compress.fs + "logs" + Compress.fs + "support";
            File dir = new File(desPath);
            if (!dir.exists()) {
                Compress.print.log(Level.INFO, "No support folder found!!");
            }
            else {
                final boolean deletionsuccess = this.deleteDir(dir, clear);
                if (deletionsuccess) {
                    if (clear) {
                        Compress.print.log(Level.INFO, "Support folder files other than desktopcentral zip are cleaned !!");
                    }
                    else {
                        Compress.print.log(Level.INFO, "Support folder is deleted !!");
                    }
                }
                else {
                    Compress.print.log(Level.INFO, "Support folder deletion failed !!");
                }
            }
            if (!clear) {
                final String serverHome = serverDir + Compress.fs + "agent-logs";
                dir = new File(serverHome);
                if (!dir.exists()) {
                    Compress.print.log(Level.INFO, "No support folder found!!");
                }
                else {
                    final boolean deletionsuccess2 = this.dmOnpremisSupportFileCreation.deleteDir(dir, clear);
                    if (deletionsuccess2) {
                        if (clear) {
                            Compress.print.log(Level.INFO, "Support folder files other than desktopcentral zip are cleaned !!");
                        }
                        else {
                            Compress.print.log(Level.INFO, "Support folder is deleted !!");
                        }
                    }
                    else {
                        Compress.print.log(Level.INFO, "Support folder deletion failed !!");
                    }
                }
            }
            if (clear) {
                final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
                final String mdmAgentLogs = serverHome + Compress.fs + "mdm-logs";
                final File agentDir = new File(mdmAgentLogs);
                if (!agentDir.exists()) {
                    Compress.print.log(Level.INFO, "No logs folder found!!");
                }
                else {
                    final boolean deletionsuccess3 = this.deleteDir(agentDir, !clear);
                    if (deletionsuccess3) {
                        Compress.print.log(Level.INFO, "MDM Agent files and folder deletion success!!");
                    }
                    else {
                        Compress.print.log(Level.INFO, "MDM Agent files and folder deletion failed !!");
                    }
                }
            }
        }
        catch (final Exception var9) {
            Compress.print.log(Level.WARNING, var9, () -> "Error while deleting support file" + var9.toString());
        }
    }
    
    public void cleanSupportLogFolder(final boolean clear) throws Exception {
        boolean deletionsuccess = false;
        final String serverDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String desPath = serverDir + Compress.fs + "logs" + Compress.fs + "supportLogs";
        final File dir = new File(desPath);
        if (!dir.exists()) {
            Compress.print.log(Level.INFO, "No supportLogs folder found to delete!!");
        }
        else {
            deletionsuccess = this.deleteDir(dir, clear);
            if (deletionsuccess) {
                Compress.print.log(Level.INFO, "Support Log folder deleted!!");
            }
            else {
                Compress.print.log(Level.INFO, "SupportLog folder Not deleted!!");
            }
        }
    }
    
    private boolean deleteDir(final File dir, final boolean clear) {
        if (dir.isDirectory()) {
            final String[] dirobject = dir.list();
            for (int i = 0; i < dirobject.length; ++i) {
                final boolean isAllowed = this.deleteDir(new File(dir, dirobject[i]), clear);
                if (!isAllowed) {
                    Compress.print.log(Level.WARNING, "This support folder file is not deleted : {0}", dirobject[i]);
                    return false;
                }
            }
        }
        boolean delAction = true;
        final String fileName = dir.getName();
        if (fileName.contains(".err") && clear) {
            delAction = dir.delete();
            return delAction;
        }
        if (clear) {
            final boolean isAllowed = FileFilterUtil.isAllowed(dir.getName(), 100);
            if (!isAllowed) {
                delAction = true;
            }
            else if (!dir.getName().equals("support")) {
                delAction = dir.delete();
            }
        }
        else {
            delAction = dir.delete();
        }
        return delAction;
    }
    
    static {
        Compress.fs = File.separator;
        Compress.className = Compress.class.getName();
        Compress.print = Logger.getLogger(Compress.className);
    }
}
