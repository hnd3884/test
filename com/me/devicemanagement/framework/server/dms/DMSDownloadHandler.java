package com.me.devicemanagement.framework.server.dms;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.scheduler.SchedulerProviderInterface;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.nio.charset.StandardCharsets;
import com.me.devicemanagement.framework.server.certificate.verifier.CRTThumbPrintVerifier;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import java.nio.file.LinkOption;
import java.io.Reader;
import java.io.InputStream;
import org.json.simple.parser.JSONParser;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.io.File;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.Arrays;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import java.util.Collections;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DMSDownloadHandler implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    private Set<String> currentSyncingComponents;
    private final long currentSchedulerTime;
    private final Map<String, Object> localCrsMetaMap;
    private final DMSDownloadUtil downloadUtil;
    private final DownloadManager downloadManager;
    private final Set<String> modifiedComponents;
    private List failedComponentsDuringLastSync;
    
    public DMSDownloadHandler() {
        this.currentSchedulerTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        this.localCrsMetaMap = new HashMap<String, Object>(6);
        this.downloadUtil = DMSDownloadUtil.getInstance();
        this.downloadManager = DownloadManager.getInstance();
        this.modifiedComponents = new HashSet<String>(5);
    }
    
    public static void startupHandling() {
        addOrUpdateSchedule();
        final HashMap<String, Object> taskMap = new HashMap<String, Object>(4);
        taskMap.put("taskName", "DMSStaticServerSync");
        taskMap.put("schedulerTime", System.currentTimeMillis());
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(DMSDownloadHandler.class.getCanonicalName(), taskMap, new Properties(), "asynchThreadPool");
    }
    
    @Override
    public void executeTask(final Properties props) {
        this.syncStaticServerUpdate(Collections.emptyList(), Boolean.FALSE);
    }
    
    protected void syncStaticServerUpdate(final List<String> components, final Boolean isOnDemand) {
        try {
            DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS component sync has begun");
            final DataObject cachedComponentsDataObj = this.downloadUtil.getComponentsDataObj(null);
            if (cachedComponentsDataObj != null && !cachedComponentsDataObj.isEmpty()) {
                final DataObject clonedDataObj = (DataObject)cachedComponentsDataObj.clone();
                final UtilAccessAPI utilAccessAPI = ApiFactoryProvider.getUtilAccessAPI();
                final CacheAccessAPI cacheAccessAPI = ApiFactoryProvider.getCacheAccessAPI();
                final String cacheNameForCurrentSyncingComponents = utilAccessAPI.getCurrentSyncingComponentCacheName();
                if (cacheNameForCurrentSyncingComponents != null) {
                    this.currentSyncingComponents = (Set)cacheAccessAPI.getCache(cacheNameForCurrentSyncingComponents, 1);
                }
                this.currentSyncingComponents = ((this.currentSyncingComponents == null) ? Collections.synchronizedSet(new HashSet<String>(8)) : this.currentSyncingComponents);
                DMSDownloadHandler.LOGGER.finer("Components during initiation of thread : " + this.currentSyncingComponents.toString());
                final Set<String> currentThreadSyncingComponents = new HashSet<String>(8);
                Iterator<Row> downloadComponentsRows;
                List<Row> downloadComponentRowList;
                if (components.isEmpty()) {
                    this.failedComponentsDuringLastSync = this.downloadUtil.getFailedComponents();
                    this.modifiedComponents.addAll(this.failedComponentsDuringLastSync);
                    downloadComponentsRows = clonedDataObj.getRows("DMSDownloadComponents", new Criteria(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"), (Object)this.currentSyncingComponents.toArray(), 9));
                    downloadComponentRowList = new ArrayList<Row>(clonedDataObj.size("DMSDownloadComponents"));
                }
                else {
                    components.removeAll(Arrays.asList(this.currentSyncingComponents.toArray()));
                    downloadComponentsRows = clonedDataObj.getRows("DMSDownloadComponents", new Criteria(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"), (Object)components.toArray(), 8));
                    downloadComponentRowList = new ArrayList<Row>(components.size());
                }
                downloadComponentsRows.forEachRemaining(downloadComponentRowList::add);
                Row row = null;
                downloadComponentRowList.forEach(row -> {
                    final String compName = (String)row.get("COMPONENT_NAME");
                    this.currentSyncingComponents.add(compName);
                    set.add(compName);
                    return;
                });
                DMSDownloadHandler.LOGGER.finer("Components after adddition of components : " + this.currentSyncingComponents.toString());
                final List<Row> finalFilterList = downloadComponentRowList.stream().parallel().filter(downloadComponentRow -> this.isFrequencyLimitReached(downloadComponentRow, isOnDemand2)).map((Function<? super Object, ?>)this::syncStaticServerUpdate).filter(Objects::nonNull).collect((Collector<? super Object, ?, List<Row>>)Collectors.toList());
                if (!finalFilterList.isEmpty()) {
                    final Iterator<Row> iterator = finalFilterList.iterator();
                    while (iterator.hasNext()) {
                        row = iterator.next();
                        clonedDataObj.updateRow(row);
                    }
                    SyMUtil.getPersistence().update(clonedDataObj);
                    DMSDownloadHandler.LOGGER.log(Level.INFO, "Component data obj updated to db...!");
                    runAsyncSchedulerFrequencyUpdate(CustomerInfoUtil.isSAS());
                }
                if (!CustomerInfoUtil.isSAS()) {
                    final Map<String, Object> filteredMap = this.modifiedComponents.stream().collect(Collectors.toMap(componentName -> componentName, componentName -> this.localCrsMetaMap.getOrDefault(componentName, Collections.emptyMap())));
                    if (!filteredMap.isEmpty()) {
                        final HashMap<String, Object> taskMap = new HashMap<String, Object>(4);
                        taskMap.put("taskName", "DMSAsyncTask");
                        taskMap.put("schedulerTime", System.currentTimeMillis());
                        Properties taskAdditionalProps = new Properties();
                        ((Hashtable<String, Map<String, Object>>)taskAdditionalProps).put("modifiedComponents", filteredMap);
                        taskAdditionalProps = utilAccessAPI.addAdditionalPropertiesForDMSFeatureTask(taskAdditionalProps);
                        if (CustomerInfoUtil.isSAS()) {
                            new DMSFeatureDownloader().executeTask(taskAdditionalProps);
                        }
                        else {
                            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(DMSFeatureDownloader.class.getCanonicalName(), taskMap, taskAdditionalProps, "asynchThreadPool");
                        }
                    }
                }
                this.currentSyncingComponents.removeAll(currentThreadSyncingComponents);
                cacheAccessAPI.putCache(cacheNameForCurrentSyncingComponents, this.currentSyncingComponents, 1);
                DMSDownloadHandler.LOGGER.finer("Components after completion of thread : " + this.currentSyncingComponents.toString());
            }
            else {
                DMSDownloadHandler.LOGGER.log(Level.INFO, "There is no components to sync meta data.");
            }
            DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS component sync has ended");
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.SEVERE, "Exception occurred : ", e);
        }
    }
    
    private boolean isFrequencyLimitReached(final Row downloadComponentRow, final Boolean isOnDemand) {
        final String componentName = downloadComponentRow.get("COMPONENT_NAME").toString();
        final String lastSuccessfulSync = this.downloadUtil.getStaticDMSParams(Arrays.asList("DMS_".concat(componentName))).get("DMS_".concat(componentName));
        boolean isFrequencyLimitReached;
        if (lastSuccessfulSync == null || isOnDemand) {
            isFrequencyLimitReached = true;
        }
        else {
            final long lastSuccessfulUpdateOfComponent = Long.parseLong(lastSuccessfulSync);
            final long frequencyFromDB = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(downloadComponentRow.get("FREQUENCY").toString()));
            isFrequencyLimitReached = (this.currentSchedulerTime - lastSuccessfulUpdateOfComponent >= frequencyFromDB);
        }
        return isFrequencyLimitReached;
    }
    
    private Row syncStaticServerUpdate(final Row downloadComponentRow) {
        Row updateDownloadComponentRow = null;
        try {
            final String componentName = downloadComponentRow.get("COMPONENT_NAME").toString();
            String staticServerMetaJsonPath = downloadComponentRow.get("COMPONENT_META_JSON").toString();
            staticServerMetaJsonPath = ApiFactoryProvider.getUtilAccessAPI().getCrsBaseUrl() + "/" + staticServerMetaJsonPath;
            DMSDownloadHandler.LOGGER.log(Level.INFO, "Going to download and update sync json from CRS to DB - " + componentName);
            DMSDownloadHandler.LOGGER.log(Level.INFO, "Crs export URL - " + staticServerMetaJsonPath + " for component : " + componentName);
            final String destinationFile = this.downloadUtil.getDMSLocalMetaDir() + File.separator + componentName + ".json";
            try (final InputStream fileInputStream = Files.newInputStream(Paths.get(destinationFile, new String[0]), new OpenOption[0])) {
                if (fileInputStream != null) {
                    final Reader fileReader = new InputStreamReader(fileInputStream);
                    final Map<String, Object> localComponentCrsMetaData = (Map<String, Object>)new JSONParser().parse(fileReader);
                    fileReader.close();
                    this.localCrsMetaMap.put(componentName, localComponentCrsMetaData);
                }
            }
            catch (final Exception ex) {
                DMSDownloadHandler.LOGGER.log(Level.INFO, "File does not exist - " + destinationFile + " for component : " + componentName);
            }
            final Map<String, Object> crsMetaData = this.downloadMetaJSON(componentName, staticServerMetaJsonPath);
            if (!crsMetaData.isEmpty()) {
                final Object responseValueFromCrs = crsMetaData.values().stream().findFirst().orElse(null);
                if (responseValueFromCrs instanceof Map) {
                    this.modifiedComponents.add(componentName);
                    final Map<String, Object> syncDetailsJson = (Map<String, Object>)responseValueFromCrs;
                    final boolean featureMetaPopulateStatus = this.populateFeatureMetaJSON(componentName, syncDetailsJson);
                    updateDownloadComponentRow = this.populateMetaConfigJSON(syncDetailsJson, downloadComponentRow);
                    if (!featureMetaPopulateStatus) {
                        DMSDownloadHandler.LOGGER.log(Level.WARNING, "It seems failure occurred while populating data from CRS : " + responseValueFromCrs + " for component : " + componentName);
                    }
                }
                else if (responseValueFromCrs instanceof String) {
                    DMSDownloadHandler.LOGGER.log(Level.WARNING, "It seems failure occurred while download sync meta json from CRS : " + responseValueFromCrs + " for component : " + componentName);
                }
                else {
                    DMSDownloadHandler.LOGGER.log(Level.WARNING, "Un supported operation : " + responseValueFromCrs + " for component : " + componentName);
                }
            }
            final String currentTimeInSeconds = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            this.downloadUtil.updateStaticDMSParams("DMS_".concat(componentName), currentTimeInSeconds);
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.INFO, "Exception occurred while sync static server updates...", e);
        }
        return updateDownloadComponentRow;
    }
    
    private Map<String, Object> downloadMetaJSON(final String componentName, final String crsExportUrl) {
        Map<String, Object> crsMetaData;
        try {
            boolean updateLastSyncDetailsFetchTimeFromCRS = false;
            String currentCRSFetchTime = null;
            if (!componentName.isEmpty() && !crsExportUrl.isEmpty()) {
                final String destinationFile = this.downloadUtil.getDMSLocalMetaDir() + File.separator + componentName + ".json";
                final Properties headers = new Properties();
                final String lastModifiedSince = this.downloadUtil.getStaticDMSParams(Arrays.asList(componentName + ".ifModifiedSince")).getOrDefault(componentName + ".ifModifiedSince", "");
                if (Files.exists(Paths.get(destinationFile, new String[0]), new LinkOption[0]) && !lastModifiedSince.isEmpty()) {
                    ((Hashtable<String, String>)headers).put("If-Modified-Since", lastModifiedSince);
                }
                DMSDownloadHandler.LOGGER.log(Level.INFO, "Headers for crs export : " + headers + " for component : " + componentName);
                final DownloadStatus downloadStatus = this.downloadManager.downloadFile(crsExportUrl, destinationFile, null, Boolean.TRUE, headers, SSLValidationType.DEFAULT_SSL_VALIDATION);
                if (downloadStatus.getStatus() == 0) {
                    if (Files.exists(Paths.get(destinationFile, new String[0]), new LinkOption[0])) {
                        final byte[] unSignedContent = new CRTThumbPrintVerifier().verifyAndUNSignFileContentWithMEMetaCrt(destinationFile);
                        if (unSignedContent != null) {
                            final Map<String, Object> response = (Map<String, Object>)new JSONParser().parse(new String(unSignedContent, StandardCharsets.UTF_8));
                            currentCRSFetchTime = downloadStatus.getLastModifiedTime();
                            Files.write(Paths.get(destinationFile, new String[0]), unSignedContent, new OpenOption[0]);
                            updateLastSyncDetailsFetchTimeFromCRS = true;
                            crsMetaData = (Map<String, Object>)Collections.singletonMap(currentCRSFetchTime, response);
                            this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS001");
                        }
                        else {
                            this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS002");
                            Files.deleteIfExists(Paths.get(destinationFile, new String[0]));
                            crsMetaData = (Map<String, Object>)Collections.singletonMap("Failed", "SignVerificationFailed");
                        }
                    }
                    else {
                        this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS003");
                        crsMetaData = (Map<String, Object>)Collections.singletonMap("Failed", "DownFileNotFound");
                    }
                }
                else {
                    if (downloadStatus.getStatus() == 10010) {
                        DMSDownloadHandler.LOGGER.log(Level.INFO, "File not modified - " + crsExportUrl);
                        this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS001");
                        return Collections.emptyMap();
                    }
                    final String errorMessage = downloadStatus.getErrorMessage();
                    if (errorMessage != null && (errorMessage.contains("ValidatorException") || errorMessage.contains("SSLHandshakeException"))) {
                        this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS006");
                    }
                    else {
                        this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS004");
                    }
                    crsMetaData = (Map<String, Object>)Collections.singletonMap("Failed", String.valueOf(downloadStatus.getStatus()));
                }
            }
            else {
                this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS005");
                crsMetaData = (Map<String, Object>)Collections.singletonMap("Failed", "Invalid arguments");
            }
            if (updateLastSyncDetailsFetchTimeFromCRS) {
                DMSDownloadHandler.LOGGER.log(Level.INFO, "Going to update crs sync details fetch time : " + currentCRSFetchTime);
                this.downloadUtil.updateStaticDMSParams(componentName + ".ifModifiedSince", currentCRSFetchTime);
            }
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.INFO, "Exception occurred : ", e);
            this.downloadUtil.updateStaticDMSParams(componentName + ".status", "DMS004");
            crsMetaData = (Map<String, Object>)Collections.singletonMap("Failed", "Exception");
        }
        return crsMetaData;
    }
    
    private Row populateMetaConfigJSON(final Map<String, Object> featureDetails, final Row downloadComponentRow) {
        try {
            if (featureDetails.containsKey("META_CONFIG")) {
                final Map<String, Object> metaConfiguration = featureDetails.get("META_CONFIG");
                final String frequencyFromDB = downloadComponentRow.get("FREQUENCY").toString();
                final String checkSumTypeFromDB = downloadComponentRow.get("CHECKSUM_TYPE").toString();
                metaConfiguration.computeIfPresent("frequency", (key, frequencyFromMeta) -> {
                    if (!frequencyFromMeta.toString().equals(s)) {
                        row.set("FREQUENCY", (Object)frequencyFromMeta.toString());
                    }
                    return frequencyFromMeta;
                });
                metaConfiguration.computeIfPresent("checksum_type", (key, checkSumTypeFromMeta) -> {
                    if (!checkSumTypeFromMeta.toString().equals(s2)) {
                        row2.set("CHECKSUM_TYPE", (Object)checkSumTypeFromMeta.toString());
                    }
                    return checkSumTypeFromMeta;
                });
                return (downloadComponentRow.getChangedColumnIndex() == null) ? null : downloadComponentRow;
            }
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.WARNING, "Exception occurred while updating meta config to table.", e);
        }
        return null;
    }
    
    private boolean populateFeatureMetaJSON(final String componentName, final Map<String, Object> featureDetailsJson) {
        try {
            final DataObject downloadFeatureDataObj = this.downloadUtil.getDownloadFeatureDetails(componentName);
            final Map<String, Object> metaFeaturesJson = featureDetailsJson.get("META_PROPERTIES");
            final Iterator<Row> featureIterator = downloadFeatureDataObj.getRows("DMSDownloadFeatures");
            while (featureIterator.hasNext()) {
                final Row featureRow = featureIterator.next();
                final String featureNameInDB = (String)featureRow.get("FEATURE_NAME");
                final String featureNameToSync = this.downloadUtil.getFeatureKeyUsedInJson(featureNameInDB);
                final Map<String, Object> featureMeta = metaFeaturesJson.get(featureNameToSync);
                if (featureMeta != null) {
                    final Long featureId = Long.valueOf(featureRow.get("FEATURE_ID").toString());
                    Row existsMetaRow = downloadFeatureDataObj.getRow("DMSDownloadMeta", new Criteria(Column.getColumn("DMSDownloadMeta", "FEATURE_ID"), (Object)featureId, 0));
                    final boolean isMetaRowExists = existsMetaRow != null;
                    existsMetaRow = (isMetaRowExists ? existsMetaRow : new Row("DMSDownloadMeta"));
                    final Row updateMetaRow = this.insertValuesToMetaTableRow(featureId, featureMeta, existsMetaRow);
                    if (updateMetaRow != null) {
                        if (isMetaRowExists) {
                            downloadFeatureDataObj.updateRow(existsMetaRow);
                        }
                        else {
                            downloadFeatureDataObj.addRow(existsMetaRow);
                        }
                    }
                    else {
                        DMSDownloadHandler.LOGGER.log(Level.WARNING, "Invalid feature, Unable to update values to meta row obj - " + featureNameToSync);
                    }
                }
                else {
                    DMSDownloadHandler.LOGGER.log(Level.WARNING, "Download feature not available in this server - " + featureNameToSync);
                }
            }
            SyMUtil.getPersistenceLite().update(downloadFeatureDataObj);
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.WARNING, "Unable to populate download components feature meta json to db.", e);
            return false;
        }
        return true;
    }
    
    private Row insertValuesToMetaTableRow(final Long featureId, final Map<String, Object> featureMeta, final Row row) {
        try {
            row.set("FEATURE_ID", (Object)featureId);
            row.set("FILE_PATH", (Object)featureMeta.get("file_path").toString());
            row.set("FILE_CHECKSUM", (Object)featureMeta.getOrDefault("checksum", "").toString());
            row.set("FILE_VERSION", (Object)Long.valueOf(featureMeta.get("version").toString()));
            row.set("IS_ALWAYS_ALIVE", (Object)Boolean.parseBoolean(featureMeta.getOrDefault("always_alive", "true").toString()));
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.WARNING, "Exception occurred while insert values to meta row : ", e);
            return null;
        }
        return row;
    }
    
    private static void runAsyncSchedulerFrequencyUpdate(final boolean isSas) {
        if (!isSas) {
            final ExecutorService scheduler = Executors.newSingleThreadExecutor();
            scheduler.submit(DMSDownloadHandler::addOrUpdateSchedule);
            scheduler.shutdown();
        }
    }
    
    protected static void addOrUpdateSchedule() {
        try {
            final SchedulerProviderInterface schedulerAPI = ApiFactoryProvider.getSchedulerAPI();
            DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS scheduler frequency update method called.");
            final Long newSchedulerFrequency = TimeUnit.MILLISECONDS.toMinutes(getDMSSchedulerFrequency());
            final Long frequencyInSeconds;
            final Long schedulerLastUpdatedFrequency = ((frequencyInSeconds = schedulerAPI.getPeriodicTimePeriod(schedulerAPI.getTaskIDForSchedule("DMSStaticServerSync"))) != null) ? TimeUnit.SECONDS.toMinutes(frequencyInSeconds) : 0L;
            DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS scheduler last updated frequency : " + schedulerLastUpdatedFrequency + " minutes " + " -- DMS Scheduler latest synced frequency : " + newSchedulerFrequency + " minutes");
            if (!schedulerAPI.isScheduleCreated("DMSStaticServerSync")) {
                final HashMap<String, String> schedulerProps = new HashMap<String, String>();
                schedulerProps.put("workEngineId", "DMSStaticServerSync");
                schedulerProps.put("operationType", String.valueOf(108));
                schedulerProps.put("workflowName", "DMSStaticServerSync");
                schedulerProps.put("schedulerName", "DMSStaticServerSync");
                schedulerProps.put("taskName", "DMSStaticServerSync");
                schedulerProps.put("className", DMSDownloadHandler.class.getCanonicalName());
                schedulerProps.put("description", "dms sync scheduler");
                schedulerProps.put("schType", "Hourly");
                schedulerProps.put("timePeriod", String.valueOf(newSchedulerFrequency));
                schedulerProps.put("unitOfTime", "minutes");
                schedulerAPI.createScheduler(schedulerProps);
                DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS sync scheduler created with frequency : " + newSchedulerFrequency);
            }
            else if (!schedulerLastUpdatedFrequency.equals(newSchedulerFrequency)) {
                final HashMap<String, Object> schedulerProps2 = new HashMap<String, Object>(6);
                schedulerProps2.put("SCHEDULE_NAME", "DMSStaticServerSync");
                schedulerProps2.put("TIME_PERIOD", newSchedulerFrequency.intValue());
                schedulerProps2.put("UNIT_OF_TIME", "minutes");
                schedulerProps2.put("START_DATE", new Timestamp(System.currentTimeMillis()));
                schedulerAPI.updatePeriodicSchedule(schedulerProps2);
                DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS sync scheduler updated with frequency as : " + newSchedulerFrequency);
            }
            else {
                DMSDownloadHandler.LOGGER.log(Level.INFO, "DMS sync scheduler already up-do-date.");
            }
        }
        catch (final Exception e) {
            DMSDownloadHandler.LOGGER.log(Level.WARNING, "Exception occurred : ", e);
        }
    }
    
    protected static long getDMSSchedulerFrequency() {
        Object minFrequency;
        try {
            minFrequency = DBUtil.getMinOfValue("DMSDownloadComponents", "FREQUENCY", null);
            DMSDownloadHandler.LOGGER.log(Level.INFO, "Minimum frequency obtained is : " + TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(minFrequency.toString())) + " minutes");
        }
        catch (final Exception ex) {
            DMSDownloadHandler.LOGGER.log(Level.WARNING, "Exception occurred while fetching minimum frequency : ", ex);
            minFrequency = "900000";
        }
        return Long.parseLong(minFrequency.toString());
    }
    
    static {
        LOGGER = Logger.getLogger("dms");
    }
}
