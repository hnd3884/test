package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

public class RevertCertificate
{
    private static String sourceClass;
    private static Logger logger;
    
    public static void undoneChanges() {
        final String sourceMethod = "undoneChanges";
        try {
            RevertCertificate.logger.info("Reverting process started at " + new Date());
            final String serverHome = System.getProperty("server.home");
            final String confDirectory = serverHome + File.separator + "apache" + File.separator + "conf";
            final String backUpDirectory = serverHome + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data" + File.separator + "certificate_backup";
            final String freshBackUpDirectory = backUpDirectory + File.separator + "freshcertificate";
            boolean flag = true;
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(freshBackUpDirectory) && ApiFactoryProvider.getFileAccessAPI().isDirectory(freshBackUpDirectory)) {
                Properties prop = new Properties();
                try {
                    prop = WebServerUtil.getWebServerSettings(Boolean.TRUE, freshBackUpDirectory + File.separator + "websettings.conf");
                }
                catch (final NullPointerException ex) {
                    RevertCertificate.logger.log(Level.INFO, "freshcertificate directory not found to revert....", ex);
                    flag = false;
                }
                catch (final Exception ex2) {
                    RevertCertificate.logger.log(Level.SEVERE, "Error Occurred while reading Properties from websettings.conf...", ex2);
                    flag = false;
                }
                final String propServerCrtFile = freshBackUpDirectory + File.separator + prop.getProperty("apache.crt.loc");
                final String propServerKeyFile = freshBackUpDirectory + File.separator + prop.getProperty("apache.serverKey.loc");
                final String propIntermediateFile = freshBackUpDirectory + File.separator + prop.getProperty("apache.ssl.intermediate.ca.file");
                final String propRootFile = freshBackUpDirectory + File.separator + prop.getProperty("apache.ssl.root.ca.file");
                RevertCertificate.logger.info("Undoing the changes..");
                RevertCertificate.logger.info("Following are the old files reverted back to..");
                RevertCertificate.logger.info("Server Certificate " + CertificateUtil.getInstance().getNameOfTheFile(propServerCrtFile));
                revertFiles(propServerCrtFile, confDirectory);
                RevertCertificate.logger.info("Server Key " + CertificateUtil.getInstance().getNameOfTheFile(propServerKeyFile));
                revertFiles(propServerKeyFile, confDirectory);
                RevertCertificate.logger.info("Intermediate Certificate " + CertificateUtil.getInstance().getNameOfTheFile(propIntermediateFile));
                revertFiles(propIntermediateFile, confDirectory);
                revertFiles(propRootFile, confDirectory);
                final String freshBackUpDirectoryWebSettings = freshBackUpDirectory + File.separator + "websettings.conf";
                if (CertificateUtil.getInstance().checkExistenceOfFile(freshBackUpDirectoryWebSettings)) {
                    CertificateUtil.getInstance().copyFileSrcToDest(freshBackUpDirectoryWebSettings, serverHome + File.separator + "conf" + File.separator + "websettings.conf");
                }
                if (flag) {
                    RevertCertificate.logger.info("Reverting process done successsfully");
                }
                else {
                    RevertCertificate.logger.info("No changes done by customer to revert..");
                }
                try {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(freshBackUpDirectory);
                }
                catch (final NullPointerException ex3) {
                    RevertCertificate.logger.log(Level.INFO, "Error occured because of the previous ones");
                }
                catch (final Exception ex4) {
                    RevertCertificate.logger.log(Level.SEVERE, "deleting the freshcertificate directory failed..");
                }
            }
            else {
                RevertCertificate.logger.severe("There is nothing to revert..No changes done since the last use");
            }
        }
        catch (final Exception ex5) {
            RevertCertificate.logger.logp(Level.SEVERE, RevertCertificate.sourceClass, sourceMethod, "Checking freshbackup directory existence failed..", ex5);
        }
    }
    
    private static void revertFiles(final String propertyFile, final String confDirectory) {
        if (CertificateUtil.getInstance().checkExistenceOfFile(propertyFile)) {
            CertificateUtil.getInstance().copyFileSrcToDest(propertyFile, confDirectory + File.separator + CertificateUtil.getInstance().getNameOfTheFile(propertyFile));
        }
    }
    
    static {
        RevertCertificate.sourceClass = "RevertChange";
        RevertCertificate.logger = Logger.getLogger("ImportCertificateLogger");
    }
}
