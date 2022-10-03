package com.me.devicemanagement.framework.server.dms;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Collections;
import java.util.function.Consumer;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.stream.Stream;
import java.util.Map;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DMSFeatureDownloader implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    private final DMSDownloadUtil downloadUtil;
    private final DMSScheduleWorker scheduleWorker;
    private final Date startTimeOfFeatureDownloader;
    private final List<Long> successCriteria;
    
    public DMSFeatureDownloader() {
        this.downloadUtil = DMSDownloadUtil.getInstance();
        this.scheduleWorker = DMSScheduleWorker.getInstance();
        this.startTimeOfFeatureDownloader = new Date(System.currentTimeMillis());
        this.successCriteria = Arrays.asList(null, 0L, 10010L);
    }
    
    @Override
    public void executeTask(final Properties props) {
        DMSFeatureDownloader.LOGGER.log(Level.INFO, "DMSFeatureDownloader started at : " + this.startTimeOfFeatureDownloader);
        DMSFeatureDownloader.LOGGER.log(Level.INFO, "Currently running tasks before Feature Download : " + this.scheduleWorker.getCurrentlyAliveTasks());
        final Map<String, Object> filteredComponentMap = ((Hashtable<K, Map<String, Object>>)props).get("modifiedComponents");
        filteredComponentMap.entrySet().stream().parallel().forEach(entry -> this.executeTask(entry.getKey(), entry.getValue()));
        DMSFeatureDownloader.LOGGER.log(Level.INFO, "DMSFeatureDownloader has ended which started at : " + this.startTimeOfFeatureDownloader);
        DMSFeatureDownloader.LOGGER.log(Level.INFO, "Currently running tasks after Feature Download : " + this.scheduleWorker.getCurrentlyAliveTasks());
    }
    
    private void executeTask(final String componentName, final Map<String, Object> oldLocalComponentCrsMetaData) {
        final Map<String, Object> currentLocalComponentCrsMetaData = this.downloadUtil.getLocalComponentFile(componentName);
        final ProductUrlLoader instance = ProductUrlLoader.getInstance();
        final DataObject componentData = this.downloadUtil.getAllDataForComponent(componentName);
        final List<Row> featureRows = new ArrayList<Row>();
        try {
            final Iterator<Row> componentIterator = componentData.getRows("DMSDownloadFeatures");
            componentIterator.forEachRemaining(featureRows::add);
        }
        catch (final Exception ex) {
            DMSFeatureDownloader.LOGGER.log(Level.SEVERE, "Exception occurred while getting features to sync for the component : " + componentName, ex);
        }
        if (!currentLocalComponentCrsMetaData.isEmpty()) {
            final Map<String, Object> metaProperties = currentLocalComponentCrsMetaData.getOrDefault("META_PROPERTIES", Collections.emptyMap());
            final Map<String, Object> oldMetaProperties = oldLocalComponentCrsMetaData.getOrDefault("META_PROPERTIES", Collections.emptyMap());
            final String checkSumType = (String)this.downloadUtil.getValueForComponent(componentName, "CHECKSUM_TYPE");
            featureRows.stream().parallel().filter(row -> this.isFeatureDownloadRequired(componentName2, row.get("FEATURE_NAME").toString(), map.get(productUrlLoader.getValue(row.get("FEATURE_NAME").toString(), row.get("FEATURE_NAME").toString())), map2.get(productUrlLoader.getValue(row.get("FEATURE_NAME").toString(), row.get("FEATURE_NAME").toString())), row)).forEach(row -> this.executeFeatureDownload(componentName3, row.get("FEATURE_NAME").toString(), map3.get(productUrlLoader2.getValue(row.get("FEATURE_NAME").toString(), row.get("FEATURE_NAME").toString())), checkSumType2, row));
        }
    }
    
    private boolean isFeatureDownloadRequired(final String componentName, final String featureName, final Object oldFeatureProperties, final Object newFeatureProperties, final Row featureRow) {
        boolean isFeatureDownloadRequired;
        try {
            final String featureAction = (String)featureRow.get("ACTIONS");
            if (featureAction == null) {
                isFeatureDownloadRequired = false;
            }
            else {
                isFeatureDownloadRequired = (oldFeatureProperties == null);
                if (!isFeatureDownloadRequired) {
                    isFeatureDownloadRequired = !((Map)newFeatureProperties).getOrDefault("checksum", "").equals(((Map)oldFeatureProperties).get("checksum"));
                    isFeatureDownloadRequired = (isFeatureDownloadRequired || !this.successCriteria.contains(this.downloadUtil.getValueFromMeta(componentName, featureName, "LAST_DOWNLOADED_STATUS")));
                }
                isFeatureDownloadRequired = (isFeatureDownloadRequired && featureAction.equals("SAVE_AS_FILE"));
            }
        }
        catch (final Exception ex) {
            DMSFeatureDownloader.LOGGER.log(Level.SEVERE, "Exception occurred while checking if feature download required for the feature : " + featureName, ex);
            isFeatureDownloadRequired = false;
        }
        return isFeatureDownloadRequired;
    }
    
    private void executeFeatureDownload(final String componentName, final String featureName, final Map<String, Object> featureProperties, final String checkSumType, final Row featureRow) {
        try {
            final Row metaRow = this.downloadUtil.getMetaRow((Long)featureRow.get("FEATURE_ID"));
            final String checkSum = featureProperties.get("checksum");
            final Long fileVersion = Long.parseLong(featureProperties.getOrDefault("version", "0"));
            final UtilAccessAPI utilAccessAPI = ApiFactoryProvider.getUtilAccessAPI();
            final String crsFilePath = utilAccessAPI.getCrsBaseUrl() + "/" + metaRow.get("FILE_PATH");
            final String destinationPath = utilAccessAPI.getServerHome().concat(File.separator).concat(featureRow.get("DESTINATION_PATH").toString());
            final DMSDownloadListener listener = this.downloadUtil.getDownloadListener(featureRow.get("HANDLER_CLASS").toString());
            final DMSDownloadEvent downloadEvent = new DMSDownloadEvent(featureName, crsFilePath, destinationPath);
            final DMSDownloadMeta metaData = new DMSDownloadMeta(componentName, featureName, checkSum, checkSumType, fileVersion);
            final DMSTaskRunner taskRunner = new DMSTaskRunner();
            taskRunner.setMeta(metaData);
            taskRunner.setEvent(downloadEvent);
            taskRunner.setListener(listener);
            taskRunner.setTaskStartedAt(this.startTimeOfFeatureDownloader);
            this.scheduleWorker.runImmediately(taskRunner);
        }
        catch (final Exception ex) {
            DMSDownloadUtil.getInstance().updateFileVersionAndDownloadStatusForFeature(componentName, featureName, null, 10008);
            DMSFeatureDownloader.LOGGER.log(Level.SEVERE, "Exception occurred while executing download for the feature : " + featureName, ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("dms");
    }
}
