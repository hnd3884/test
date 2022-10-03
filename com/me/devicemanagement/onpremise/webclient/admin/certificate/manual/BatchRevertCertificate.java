package com.me.devicemanagement.onpremise.webclient.admin.certificate.manual;

import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.Properties;
import java.util.Date;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Logger;

public class BatchRevertCertificate
{
    static String homeLocation;
    static String apacheLocation;
    private static Logger logger;
    private static BatchLogHandler logHandler;
    
    public static void main(final String[] args) {
        ConsoleOut.println("Reverting process began.. Please wait a while..");
        revertProcess(args[0]);
    }
    
    public static void revertProcess(final String args) {
        BatchRevertCertificate.homeLocation = args;
        BatchRevertCertificate.apacheLocation = BatchRevertCertificate.homeLocation + File.separator + "apache";
        BatchRevertCertificate.logHandler = new BatchLogHandler(Logger.getLogger(BatchCertificateConverter.class.getName()), BatchRevertCertificate.homeLocation);
        (BatchRevertCertificate.logger = BatchRevertCertificate.logHandler.getLogger()).info("");
        BatchRevertCertificate.logger.info("--------------------------START-------------------------");
        BatchRevertCertificate.logger.log(Level.INFO, "Reverting process started at " + new Date());
        final File backUpDirectory = new File(BatchRevertCertificate.homeLocation + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data" + File.separator + "certificate_backup");
        final File workingDirectory = new File(backUpDirectory + File.separator + "freshcertificate");
        final File confDirectory = new File(BatchRevertCertificate.apacheLocation + File.separator + "conf");
        boolean flag = true;
        Properties prop = new Properties();
        try {
            prop = WebServerUtil.getWebServerSettings();
        }
        catch (final NullPointerException ex) {
            BatchRevertCertificate.logger.log(Level.SEVERE, "freshcertificate directory not found..", ex);
            flag = false;
        }
        catch (final Exception ex2) {
            BatchRevertCertificate.logger.log(Level.SEVERE, "Error While getting Properties from WebSettings.conf ... ", ex2);
        }
        final File propServerCrtFile = new File(workingDirectory + File.separator + prop.getProperty("apache.crt.loc"));
        final File propServerKeyFile = new File(workingDirectory + File.separator + prop.getProperty("apache.serverKey.loc"));
        final File propIntermediateFile = new File(workingDirectory + File.separator + prop.getProperty("apache.ssl.intermediate.ca.file"));
        final File propRootFile = new File(workingDirectory + File.separator + prop.getProperty("apache.ssl.root.ca.file"));
        BatchRevertCertificate.logger.log(Level.INFO, "Reverting the changes..");
        revertFiles(propServerCrtFile, confDirectory);
        revertFiles(propServerKeyFile, confDirectory);
        revertFiles(propIntermediateFile, confDirectory);
        revertFiles(propRootFile, confDirectory);
        final File workingDirectoryWebSettings = new File(workingDirectory + File.separator + "websettings.conf");
        if (BatchUtil.checkExistenceOfFile(workingDirectoryWebSettings)) {
            copyFileUsingChannel(workingDirectoryWebSettings, new File(BatchRevertCertificate.homeLocation + File.separator + "conf" + File.separator + "websettings.conf"));
        }
        if (flag) {
            ConsoleOut.println("\n\nReverting process done successfully");
        }
        else {
            ConsoleOut.println("\n\nThere is nothing to revert..You have done no changes..");
        }
        try {
            delete(workingDirectory);
        }
        catch (final IOException ex3) {
            BatchRevertCertificate.logger.log(Level.INFO, "Files in the directory " + workingDirectory + " may be in use/not available");
        }
        catch (final NullPointerException ex4) {
            BatchRevertCertificate.logger.log(Level.INFO, "Error occured because of the previous ones");
        }
        BatchRevertCertificate.logger.info("--------------------------END-------------------------");
    }
    
    public static void copyFileUsingChannel(final File source, final File dest) {
        try {
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
            }
            finally {
                sourceChannel.close();
                destChannel.close();
            }
        }
        catch (final IOException ex) {
            BatchRevertCertificate.logger.log(Level.SEVERE, source + " and " + dest + " files not found ", ex);
        }
        catch (final NullPointerException ex2) {
            BatchRevertCertificate.logger.log(Level.SEVERE, "Null pointer exception in copying the file", ex2);
        }
    }
    
    public static void revertFiles(final File propFile, final File confDirectory) {
        if (BatchUtil.checkExistenceOfFile(propFile)) {
            BatchRevertCertificate.logger.info("Reverting the file " + propFile.getName());
            copyFileUsingChannel(propFile, new File(confDirectory + File.separator + propFile.getName()));
        }
        else {
            BatchRevertCertificate.logger.info(propFile.getName() + " File doesn't exist to revert");
        }
    }
    
    public static void delete(final File file) throws IOException, NullPointerException {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                BatchRevertCertificate.logger.log(Level.INFO, "Deleted the directory.. " + file);
                file.delete();
            }
            else {
                final String[] list;
                final String[] files = list = file.list();
                for (final String temp : list) {
                    final File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if (file.list().length == 0) {
                    BatchRevertCertificate.logger.log(Level.INFO, "Deleted the directory.. " + file);
                    file.delete();
                }
            }
        }
        else if (!file.delete()) {
            BatchRevertCertificate.logger.log(Level.INFO, file.getCanonicalPath() + " couldn't be deleted");
        }
    }
    
    static {
        BatchRevertCertificate.homeLocation = null;
        BatchRevertCertificate.apacheLocation = null;
        BatchRevertCertificate.logger = null;
    }
}
