package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ScheduledCertificateBackUpHandler implements BackUpHandler
{
    private static Logger logger;
    
    @Override
    public List<String> backUpFile(final String destination) throws Exception {
        InputStream in = null;
        try {
            ScheduledCertificateBackUpHandler.logger.log(Level.INFO, "Entered into backupFile method of ScheduledCertificateBackUpHandler class");
            final String apacheWebServer = "apache";
            final String nginxWebServer = "nginx";
            final String serverHome = System.getProperty("server.home");
            final File webSettingsFile = new File(serverHome + File.separator + "conf" + File.separator + "websettings.conf");
            if (webSettingsFile.isFile()) {
                final Properties webSettingsProp = new Properties();
                in = new FileInputStream(webSettingsFile);
                webSettingsProp.load(in);
                final String apacheConfDir = serverHome + File.separator + "apache" + File.separator + "conf";
                final String nginxConfDir = serverHome + File.separator + "nginx" + File.separator + "conf";
                ScheduledCertificateBackUpHandler.logger.log(Level.INFO, "Apache Conf Directory " + apacheConfDir);
                ScheduledCertificateBackUpHandler.logger.log(Level.INFO, "Nginx Conf Directory " + nginxConfDir);
                final String serverCrtLocProp = "server.crt.loc";
                final String serverKeyLocProp = "server.key.loc";
                final String apacheCrtLocProp = "apache.crt.loc";
                final String apacheServerKeyLocProp = "apache.serverKey.loc";
                final String serverIntermediateCertProp = "apache.ssl.intermediate.ca.file";
                final String serverRootCertProp = "apache.ssl.root.ca.file";
                final String dmRootCAKeyFile = "DMRootCA.key";
                final String apacheDMRootCAKeyLoc = apacheConfDir + File.separator + dmRootCAKeyFile;
                final String nginxDMRootCAKEyLoc = nginxConfDir + File.separator + dmRootCAKeyFile;
                String serverCertFile;
                if (webSettingsProp.containsKey(serverCrtLocProp)) {
                    serverCertFile = webSettingsProp.getProperty(serverCrtLocProp);
                }
                else {
                    serverCertFile = webSettingsProp.getProperty(apacheCrtLocProp);
                }
                String serverPrivateKeyFile;
                if (webSettingsProp.containsKey(serverKeyLocProp)) {
                    serverPrivateKeyFile = webSettingsProp.getProperty(serverKeyLocProp);
                }
                else {
                    serverPrivateKeyFile = webSettingsProp.getProperty(apacheServerKeyLocProp);
                }
                ScheduledCertificateBackUpHandler.logger.log(Level.INFO, "Server Certificate Property Name " + serverCertFile);
                ScheduledCertificateBackUpHandler.logger.log(Level.INFO, "Server Key Property Name " + serverPrivateKeyFile);
                final String intermediateCrtFile = webSettingsProp.getProperty(serverIntermediateCertProp);
                final String rootCertFile = webSettingsProp.getProperty(serverRootCertProp);
                final ArrayList<String> backedUpFiles = new ArrayList<String>();
                backedUpFiles.add(this.takeBackUp(new File(apacheConfDir + File.separator + serverCertFile), destination, apacheWebServer));
                backedUpFiles.add(this.takeBackUp(new File(nginxConfDir + File.separator + serverCertFile), destination, nginxWebServer));
                backedUpFiles.add(this.takeBackUp(new File(apacheConfDir + File.separator + serverPrivateKeyFile), destination, apacheWebServer));
                backedUpFiles.add(this.takeBackUp(new File(nginxConfDir + File.separator + serverPrivateKeyFile), destination, nginxWebServer));
                if (null != intermediateCrtFile) {
                    backedUpFiles.add(this.takeBackUp(new File(apacheConfDir + File.separator + intermediateCrtFile), destination, apacheWebServer));
                    backedUpFiles.add(this.takeBackUp(new File(nginxConfDir + File.separator + intermediateCrtFile), destination, nginxWebServer));
                }
                if (null != rootCertFile) {
                    backedUpFiles.add(this.takeBackUp(new File(apacheConfDir + File.separator + rootCertFile), destination, apacheWebServer));
                    backedUpFiles.add(this.takeBackUp(new File(nginxConfDir + File.separator + rootCertFile), destination, nginxWebServer));
                }
                if (new File(apacheDMRootCAKeyLoc).exists()) {
                    backedUpFiles.add(this.takeBackUp(new File(apacheDMRootCAKeyLoc), destination, apacheWebServer));
                }
                if (new File(nginxDMRootCAKEyLoc).exists()) {
                    backedUpFiles.add(this.takeBackUp(new File(nginxDMRootCAKEyLoc), destination, nginxWebServer));
                }
                backedUpFiles.add(this.takeBackUp(new File(nginxConfDir + File.separator + "nginx-ssl.conf"), destination, nginxWebServer));
                if (backedUpFiles.contains(null)) {
                    backedUpFiles.remove(null);
                }
                for (int i = 0; i < backedUpFiles.size(); ++i) {
                    ScheduledCertificateBackUpHandler.logger.log(Level.INFO, " Backup FIles " + backedUpFiles.get(i));
                }
                return backedUpFiles;
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException ex) {
                ScheduledCertificateBackUpHandler.logger.log(Level.SEVERE, "IO Exception in closing the inputstream..", ex);
            }
        }
        return null;
    }
    
    private String takeBackUp(final File certificateFile, final String destination, final String webServerName) {
        try {
            if (certificateFile.isFile() && this.copyFile(certificateFile, new File(destination + File.separator + webServerName + File.separator + "conf" + File.separator + certificateFile.getName()))) {
                ScheduledCertificateBackUpHandler.logger.log(Level.INFO, "returning file " + webServerName + File.separator + "conf" + File.separator + certificateFile.getName());
                return webServerName + File.separator + "conf" + File.separator + certificateFile.getName();
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ScheduledCertificateBackUpHandler.class.getName()).log(Level.SEVERE, certificateFile + "cannot be backed up..back up failed..", ex);
        }
        return null;
    }
    
    private boolean copyFile(final File source, final File destination) {
        InputStream input = null;
        OutputStream output = null;
        try {
            final File destinationParentDir = destination.getParentFile();
            if (!destinationParentDir.exists()) {
                destinationParentDir.mkdirs();
            }
            input = new FileInputStream(source);
            output = new FileOutputStream(destination);
            final byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }
        catch (final Exception ex) {
            ScheduledCertificateBackUpHandler.logger.log(Level.SEVERE, "Exception in copying the file", ex);
            return false;
        }
        finally {
            if (input != null && output != null) {
                try {
                    input.close();
                    output.close();
                }
                catch (final IOException ex2) {
                    Logger.getLogger(ScheduledCertificateBackUpHandler.class.getName()).log(Level.SEVERE, "IO Exception in closing the stream", ex2);
                }
            }
        }
        return true;
    }
    
    static {
        ScheduledCertificateBackUpHandler.logger = Logger.getLogger(ScheduledCertificateBackUpHandler.class.getName());
    }
}
