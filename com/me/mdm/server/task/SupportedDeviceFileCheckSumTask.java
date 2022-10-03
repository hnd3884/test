package com.me.mdm.server.task;

import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Properties;
import com.adventnet.sym.server.mdm.enroll.MDMModelNameMappingHandler;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SupportedDeviceFileCheckSumTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    MDMModelNameMappingHandler modelNameMappingHandler;
    
    public SupportedDeviceFileCheckSumTask() {
        this.modelNameMappingHandler = null;
    }
    
    public void executeTask(final Properties properties) {
        boolean isBundledFileSource = false;
        try {
            this.modelNameMappingHandler = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI();
            final DownloadStatus downloadStatus = this.modelNameMappingHandler.downloadFileFromStaticServer();
            if (downloadStatus.getStatus() != 0) {
                this.modelNameMappingHandler.downloadFileFromProductBundle();
                isBundledFileSource = true;
            }
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(this.modelNameMappingHandler.getConstructedFilePath())) {
                SupportedDeviceFileCheckSumTask.logger.log(Level.INFO, "Supported device csv file has been downloaded from static server");
                this.modelNameMappingHandler.calculateCheckSum(this.modelNameMappingHandler.getConstructedFilePath());
                this.modelNameMappingHandler.addOrUpdateSupportedDevices(this.modelNameMappingHandler.getConstructedFilePath(), 1);
            }
        }
        catch (final Exception exception) {
            SupportedDeviceFileCheckSumTask.logger.log(Level.WARNING, "Exception while running SupportedDeviceFileCheckSumTask task", exception);
            try {
                if (this.modelNameMappingHandler.getConstructedFilePath() != null && !isBundledFileSource) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.modelNameMappingHandler.getConstructedFilePath() + File.separator + "../" + File.separator + "../");
                    SupportedDeviceFileCheckSumTask.logger.log(Level.INFO, "SupportedDevices CSV file has been deleted from application memory");
                }
            }
            catch (final Exception exception) {
                SupportedDeviceFileCheckSumTask.logger.log(Level.WARNING, "Exception occurred while deleting file ", exception);
            }
        }
        finally {
            try {
                if (this.modelNameMappingHandler.getConstructedFilePath() != null && !isBundledFileSource) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.modelNameMappingHandler.getConstructedFilePath() + File.separator + "../" + File.separator + "../");
                    SupportedDeviceFileCheckSumTask.logger.log(Level.INFO, "SupportedDevices CSV file has been deleted from application memory");
                }
            }
            catch (final Exception exception2) {
                SupportedDeviceFileCheckSumTask.logger.log(Level.WARNING, "Exception occurred while deleting file ", exception2);
            }
        }
    }
    
    static {
        SupportedDeviceFileCheckSumTask.logger = Logger.getLogger("MDMLogger");
    }
}
