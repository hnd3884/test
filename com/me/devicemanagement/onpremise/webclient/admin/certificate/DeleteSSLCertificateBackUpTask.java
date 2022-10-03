package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.onpremise.server.certificate.ServerSANCertificateGeneratorTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeleteSSLCertificateBackUpTask implements SchedulerExecutionInterface
{
    private static String backUpDirectory;
    private static FileAccessAPI fileAccessAPI;
    
    public void executeTask(final Properties taskProps) {
        final Logger logger = Logger.getLogger("ImportCertificateLogger");
        try {
            logger.log(Level.INFO, "Invoking ServerSANCertificateGeneratorTask scheduler");
            new ServerSANCertificateGeneratorTask().executeTask(null);
        }
        catch (final Exception exception) {
            logger.log(Level.SEVERE, "Exception In ServerSANCertificateGeneratorTask ", exception);
        }
        logger.log(Level.INFO, "back updirectory deletion started..");
        try {
            if (DeleteSSLCertificateBackUpTask.fileAccessAPI.isFileExists(DeleteSSLCertificateBackUpTask.backUpDirectory) && DeleteSSLCertificateBackUpTask.fileAccessAPI.isDirectory(DeleteSSLCertificateBackUpTask.backUpDirectory)) {
                logger.info("Directories to be deleted " + CertificateUtil.getInstance().getDirectoriesIn(DeleteSSLCertificateBackUpTask.backUpDirectory));
                logger.info("Fresh back up directory files " + DeleteSSLCertificateBackUpTask.fileAccessAPI.getAllFilesList(DeleteSSLCertificateBackUpTask.backUpDirectory + File.separator + "freshcertificate", (String)null, (String)null));
                if (DeleteSSLCertificateBackUpTask.fileAccessAPI.deleteDirectory(DeleteSSLCertificateBackUpTask.backUpDirectory)) {
                    logger.info("back up directory deletion completed successfully..");
                }
                else {
                    logger.info("back up directory deletion failed.. File may have been in use/not available");
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Deletion of ssl backup directory failed..directory may not available/in use", ex);
        }
    }
    
    static {
        DeleteSSLCertificateBackUpTask.backUpDirectory = System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data" + File.separator + "certificate_backup";
        DeleteSSLCertificateBackUpTask.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
    }
}
