package com.me.ems.onpremise.productbanner.core;

import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.dms.DMSDownloadUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.scheduler.SchedulerProviderInterface;
import java.util.Properties;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ProductBannerSyncTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    private final ProductBannerUtil productBannerUtil;
    private static final Long DEFAULT_SCHEDULER_FREQUENCY;
    
    public ProductBannerSyncTask() {
        this.productBannerUtil = ProductBannerUtil.getInstance();
    }
    
    public void initialiseTask() {
        try {
            final SchedulerProviderInterface schedulerAPI = ApiFactoryProvider.getSchedulerAPI();
            final Long schedulerLastUpdatedFrequency = schedulerAPI.getPeriodicTimePeriod(ApiFactoryProvider.getSchedulerAPI().getTaskIDForSchedule("BannerSyncTask"));
            final long frequencyToBeUpdated = FrameworkConfigurations.getFrameworkConfigurations().optLong("product_banner_sync_scheduler_frequency", (long)ProductBannerSyncTask.DEFAULT_SCHEDULER_FREQUENCY);
            if (schedulerLastUpdatedFrequency == null) {
                ProductBannerSyncTask.LOGGER.log(Level.INFO, "Going to create ProductBannerSyncTask with frequency as : " + frequencyToBeUpdated + " hours");
                final HashMap<String, String> schedulerProps = new HashMap<String, String>();
                schedulerProps.put("workEngineId", "BannerSyncTask");
                schedulerProps.put("operationType", String.valueOf(5005));
                schedulerProps.put("workflowName", "BannerSyncTask");
                schedulerProps.put("schedulerName", "BannerSyncTask");
                schedulerProps.put("taskName", "BannerSyncTask");
                schedulerProps.put("className", ProductBannerSyncTask.class.getCanonicalName());
                schedulerProps.put("description", "ProductBannerSyncTask scheduled to execute every " + frequencyToBeUpdated + " hours");
                schedulerProps.put("schType", "Hourly");
                schedulerProps.put("timePeriod", String.valueOf(frequencyToBeUpdated));
                schedulerProps.put("unitOfTime", "hours");
                schedulerProps.put("skip_missed_schedule", "false");
                schedulerAPI.createScheduler((HashMap)schedulerProps);
            }
            else if (schedulerLastUpdatedFrequency != TimeUnit.HOURS.toSeconds(frequencyToBeUpdated)) {
                final HashMap<String, Object> schedulerProps2 = new HashMap<String, Object>(6);
                schedulerProps2.put("SCHEDULE_NAME", "BannerSyncTask");
                schedulerProps2.put("TIME_PERIOD", (int)frequencyToBeUpdated);
                schedulerProps2.put("UNIT_OF_TIME", "hours");
                schedulerProps2.put("START_DATE", new Timestamp(System.currentTimeMillis()));
                schedulerAPI.updatePeriodicSchedule((HashMap)schedulerProps2);
                ProductBannerSyncTask.LOGGER.log(Level.INFO, "ProductBannerSyncTask updated with frequency as : " + frequencyToBeUpdated + " hours");
            }
            else {
                ProductBannerSyncTask.LOGGER.log(Level.INFO, "ProductBannerSyncTask scheduler already initialised");
            }
            this.executeTask(null);
        }
        catch (final Exception ex) {
            ProductBannerSyncTask.LOGGER.log(Level.SEVERE, "Exception while initialising ProductBannerSyncTask : ", ex);
        }
    }
    
    public void executeTask(final Properties props) {
        ProductBannerSyncTask.LOGGER.log(Level.INFO, "Sync task starts for ProductBanner");
        final String defaultURL = ApiFactoryProvider.getUtilAccessAPI().getCrsBaseUrl() + "/" + "uems/product-banner.json";
        String crsFilePath = ProductUrlLoader.getInstance().getValue("productBannerSyncUrl");
        crsFilePath = ((crsFilePath == null || crsFilePath.isEmpty()) ? defaultURL : crsFilePath);
        final String destinationPath = this.productBannerUtil.getProductReviewFilePath("product-banner.json");
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        final DownloadManager downloadManager = DownloadManager.getInstance();
        final Properties headers = new Properties();
        headers.setProperty("Pragma", "no-cache");
        headers.setProperty("Cache-Control", "no-cache");
        final String lastModifiedTime = SyMUtil.getSyMParameter("BANNER_JSON_LAST_MODIFIED");
        if (fileAccessAPI.isFileExists(destinationPath) && lastModifiedTime != null) {
            headers.setProperty("If-Modified-Since", lastModifiedTime);
        }
        DownloadStatus downloadStatus = null;
        int statusCode;
        try {
            downloadStatus = DMSDownloadUtil.getInstance().downloadRequestedFileForComponent("Framework", "productBanner", destinationPath, (Properties)null, headers).getDownloadStatus();
            statusCode = downloadStatus.getStatus();
        }
        catch (final Exception ex) {
            ProductBannerSyncTask.LOGGER.log(Level.INFO, "Exception occurred while checksum validation :", ex);
            statusCode = 10009;
        }
        switch (statusCode) {
            case 0: {
                this.productBannerUtil.updateJSONInMemory();
                final String lastModifiedTimeOfJson = downloadStatus.getLastModifiedTime();
                if (lastModifiedTimeOfJson != null) {
                    SyMUtil.updateSyMParameter("BANNER_JSON_LAST_MODIFIED", lastModifiedTimeOfJson);
                    break;
                }
                break;
            }
            case 10010: {
                ProductBannerSyncTask.LOGGER.log(Level.INFO, "product-banner.json not modified");
                break;
            }
            default: {
                ProductBannerSyncTask.LOGGER.log(Level.SEVERE, "Download Failed for product-banner.json with status code : " + statusCode);
                break;
            }
        }
        ProductBannerSyncTask.LOGGER.log(Level.INFO, "Sync task ends for ProductBanner");
    }
    
    static {
        LOGGER = Logger.getLogger(ProductBannerSyncTask.class.getName());
        DEFAULT_SCHEDULER_FREQUENCY = 24L;
    }
}
