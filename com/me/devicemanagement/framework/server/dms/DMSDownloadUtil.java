package com.me.devicemanagement.framework.server.dms;

import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.dms.exception.DMSException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.persistence.Persistence;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.Collections;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class DMSDownloadUtil
{
    private static final Logger LOGGER;
    private static DMSDownloadUtil instance;
    private Properties productConfigProps;
    private String dmsLocalMetaDir;
    private final Map<String, DMSDownloadListener> instanceMap;
    
    private DMSDownloadUtil() {
        this.productConfigProps = null;
        this.dmsLocalMetaDir = null;
        this.instanceMap = new ConcurrentHashMap<String, DMSDownloadListener>();
    }
    
    public static DMSDownloadUtil getInstance() {
        if (DMSDownloadUtil.instance == null) {
            DMSDownloadUtil.instance = new DMSDownloadUtil();
        }
        return DMSDownloadUtil.instance;
    }
    
    protected DMSDownloadListener getDownloadListener(final String className) {
        try {
            DMSDownloadListener listenerObject = this.instanceMap.get(className);
            if (listenerObject == null) {
                listenerObject = (DMSDownloadListener)Class.forName(className).newInstance();
                this.instanceMap.put(className, listenerObject);
            }
            return listenerObject;
        }
        catch (final InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception occurred during instantiation : ", ex);
            return this.instanceMap.get(DefaultDMSDownloadListener.class.getCanonicalName());
        }
    }
    
    protected String getProductSpecificProps(final String key) {
        try {
            if (this.productConfigProps == null) {
                this.productConfigProps = FileAccessUtil.readProperties(System.getProperty("server.home") + File.separator + "conf" + File.separator + "dms" + File.separator + "dms-product-specific.props");
            }
            return this.productConfigProps.getProperty(key, "");
        }
        catch (final Exception e) {
            DMSDownloadUtil.LOGGER.log(Level.WARNING, "Exception occurred : ", e);
            return "";
        }
    }
    
    public Map<String, String> getStaticDMSParams(final List<String> params) {
        try {
            final Column col = Column.getColumn("DMSStaticParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)params.toArray(), 8, false);
            final DataObject staticDO = SyMUtil.getPersistence().get("DMSStaticParams", criteria);
            if (staticDO.isEmpty()) {
                return Collections.emptyMap();
            }
            final Map<String, String> paramValues = new HashMap<String, String>(staticDO.size("DMSStaticParams"));
            final Iterator iterator = staticDO.getRows("DMSStaticParams");
            while (iterator.hasNext()) {
                final Row staticParamRow = iterator.next();
                paramValues.put(String.valueOf(staticParamRow.get("PARAM_NAME")), (String)staticParamRow.get("PARAM_VALUE"));
            }
            return paramValues;
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception while getting data from table - DMSSTATICPARAMS", ex);
            return Collections.emptyMap();
        }
    }
    
    protected void updateStaticDMSParams(final String paramName, final String paramValue) {
        try {
            final Column col = Column.getColumn("DMSStaticParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject paramDO = persistence.get("DMSStaticParams", criteria);
            if (paramDO.isEmpty()) {
                final Row paramRow = new Row("DMSStaticParams");
                paramRow.set("PARAM_NAME", (Object)paramName);
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                paramDO.addRow(paramRow);
                persistence.update(paramDO);
                DMSDownloadUtil.LOGGER.log(Level.INFO, "DMSSTATICPARAMS:Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                final Row paramRow = paramDO.getFirstRow("DMSStaticParams");
                if (paramValue != null && !paramValue.equals(paramRow.get("PARAM_VALUE"))) {
                    paramRow.set("PARAM_VALUE", (Object)paramValue);
                    paramDO.updateRow(paramRow);
                    persistence.update(paramDO);
                    DMSDownloadUtil.LOGGER.log(Level.INFO, "DMSSTATICPARAMS:Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
                }
            }
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception while updating DMSSTATICPARAMS table", ex);
        }
    }
    
    protected void deleteStaticDMSParams(final List<String> paramNames) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DMSStaticParams", "PARAM_NAME"), (Object)paramNames.toArray(), 8, false);
            SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Caught exception while deleting DMSSTATICPARAMS:" + paramNames.toString() + " from DB.", ex);
        }
    }
    
    String getFeatureKeyUsedInJson(final String featureName) {
        return ProductUrlLoader.getInstance().getValue(featureName, featureName);
    }
    
    protected String getDMSLocalMetaDir() {
        if (this.dmsLocalMetaDir == null) {
            this.dmsLocalMetaDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "dms" + File.separator + "localmeta";
        }
        return this.dmsLocalMetaDir;
    }
    
    protected DataObject getComponentsDataObj(final Criteria criteria) {
        try {
            return SyMUtil.getCachedPersistence().get("DMSDownloadComponents", criteria);
        }
        catch (final Exception e) {
            DMSDownloadUtil.LOGGER.log(Level.WARNING, "Unable to retrieve the download components details : ", e);
            return null;
        }
    }
    
    protected Row getComponentRow(final String componentName) {
        try {
            final Criteria componentCriteria = new Criteria(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"), (Object)componentName, 0);
            return this.getComponentsDataObj(componentCriteria).getFirstRow("DMSDownloadComponents");
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.WARNING, "Unable to retrieve the download components row : ", ex);
            return null;
        }
    }
    
    protected Object getValueForComponent(final String componentName, final String columnName) {
        return this.getComponentRow(componentName).get(columnName);
    }
    
    protected Map<String, Object> getLocalComponentFile(final String componentName) {
        final String localFilePath = this.getDMSLocalMetaDir() + File.separator + componentName + ".json";
        try (final FileReader reader = new FileReader(localFilePath)) {
            return (Map)new JSONParser().parse((Reader)reader);
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception occurred while loading componentFile for Component : " + componentName, ex);
            return Collections.emptyMap();
        }
    }
    
    protected DataObject getDownloadFeatureDetails(final String componentName) {
        try {
            final SelectQuery selectQuery = this.getComponentQuery();
            selectQuery.addSelectColumn(Column.getColumn("DMSDownloadMeta", "FEATURE_ID", "DMSDownloadMeta.FEATURE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DMSDownloadMeta", "FILE_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("DMSDownloadMeta", "FILE_VERSION"));
            selectQuery.addSelectColumn(Column.getColumn("DMSDownloadMeta", "FILE_CHECKSUM"));
            selectQuery.addSelectColumn(Column.getColumn("DMSDownloadMeta", "IS_ALWAYS_ALIVE"));
            selectQuery.addJoin(new Join("DMSDownloadFeatures", "DMSDownloadMeta", new String[] { "FEATURE_ID" }, new String[] { "FEATURE_ID" }, 1));
            final Criteria criteria = new Criteria(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"), (Object)componentName, 0);
            selectQuery.setCriteria(criteria);
            return SyMUtil.getPersistenceLite().get(selectQuery);
        }
        catch (final Exception e) {
            DMSDownloadUtil.LOGGER.log(Level.WARNING, "Exception occurred while retrieve feature and meta table.", e);
            return null;
        }
    }
    
    DataObject getAllDataForComponent(final String componentName) {
        try {
            final SelectQuery selectQuery = this.getComponentQuery();
            selectQuery.addSelectColumn(Column.getColumn("DMSDownloadComponents", "CHECKSUM_TYPE"));
            final Criteria criteria = new Criteria(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"), (Object)componentName, 0);
            selectQuery.setCriteria(criteria);
            return SyMUtil.getCachedPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception while fetching all data for a component", ex);
            return null;
        }
    }
    
    SelectQuery getComponentQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMSDownloadComponents"));
        selectQuery.addSelectColumn(Column.getColumn("DMSDownloadComponents", "DOWNLOAD_COMPONENT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DMSDownloadFeatures", "*"));
        selectQuery.addJoin(new Join("DMSDownloadComponents", "DMSDownloadFeatures", new String[] { "DOWNLOAD_COMPONENT_ID" }, new String[] { "DOWNLOAD_COMPONENT_ID" }, 2));
        return selectQuery;
    }
    
    protected Row getFeatureRow(final String componentName, final String featureName) {
        try {
            final SelectQuery featureQuery = this.getComponentQuery();
            final Criteria componentCriteria = new Criteria(Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME"), (Object)componentName, 0);
            final Criteria featureCriteria = new Criteria(Column.getColumn("DMSDownloadFeatures", "FEATURE_NAME"), (Object)featureName, 0);
            featureQuery.setCriteria(componentCriteria.and(featureCriteria));
            return SyMUtil.getCachedPersistence().get(featureQuery).getFirstRow("DMSDownloadFeatures");
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.WARNING, "Exception occurred while retrieve feature row.", ex);
            return null;
        }
    }
    
    protected Object getValueForFeature(final String componentName, final String featureName, final String columnName) {
        return this.getFeatureRow(componentName, featureName).get(columnName);
    }
    
    protected Object getValueFromMeta(final String componentName, final String featureName, final String columnName) {
        return this.getMetaRow(Long.valueOf(this.getFeatureRow(componentName, featureName).get("FEATURE_ID").toString())).get(columnName);
    }
    
    protected Row getMetaRow(final Long featureID) {
        try {
            return DBUtil.getRowFromDB("DMSDownloadMeta", "FEATURE_ID", featureID);
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.WARNING, "Exception occurred while retrieve meta row.", ex);
            return null;
        }
    }
    
    protected void updateFileVersionAndDownloadStatusForFeature(final String componentName, final String featureName, final Long fileVersion, final Integer downloadStatus) {
        try {
            final Row featureRow = this.getFeatureRow(componentName, featureName);
            final Row metaRow = this.getMetaRow((Long)featureRow.get("FEATURE_ID"));
            metaRow.set("LAST_DOWNLOADED_VERSION", (Object)fileVersion);
            metaRow.set("LAST_DOWNLOADED_STATUS", (Object)(long)downloadStatus);
            final DataObject metaDO = (DataObject)new WritableDataObject();
            metaDO.updateBlindly(metaRow);
            SyMUtil.getPersistenceLite().update(metaDO);
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception occurred while updating feature download status to DB : ", ex);
        }
    }
    
    protected List getFailedComponents() throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DMSDownloadComponents"));
        final Column componentName = Column.getColumn("DMSDownloadComponents", "COMPONENT_NAME");
        query.addSelectColumns((List)Arrays.asList(Column.getColumn("DMSDownloadComponents", "DOWNLOAD_COMPONENT_ID"), componentName));
        query.addJoin(new Join("DMSDownloadComponents", "DMSDownloadFeatures", new String[] { "DOWNLOAD_COMPONENT_ID" }, new String[] { "DOWNLOAD_COMPONENT_ID" }, 2));
        query.addJoin(new Join("DMSDownloadFeatures", "DMSDownloadMeta", new String[] { "FEATURE_ID" }, new String[] { "FEATURE_ID" }, 2));
        final Criteria notNull = new Criteria(Column.getColumn("DMSDownloadMeta", "LAST_DOWNLOADED_STATUS"), (Object)null, 1);
        final Criteria successCriteria = new Criteria(Column.getColumn("DMSDownloadMeta", "LAST_DOWNLOADED_STATUS"), (Object)new Object[] { 0, 10010 }, 9);
        final Criteria alwaysAlive = new Criteria(Column.getColumn("DMSDownloadMeta", "LAST_DOWNLOADED_STATUS"), (Object)404, 0).and(new Criteria(Column.getColumn("DMSDownloadMeta", "IS_ALWAYS_ALIVE"), (Object)Boolean.FALSE, 0));
        query.setCriteria(notNull.and(successCriteria));
        final DataObject componentNames = SyMUtil.getPersistenceLite().get(query);
        return DBUtil.getColumnValuesAsList(componentNames.getRows("DMSDownloadComponents", alwaysAlive.negate()), "COMPONENT_NAME");
    }
    
    public void initiateStaticServerUpdate(final List<String> components) {
        new DMSDownloadHandler().syncStaticServerUpdate(components, Boolean.TRUE);
    }
    
    public DMSDownloadEvent downloadRequestedFileForComponent(final String componentName, final String featureName, String destinationPath, final Properties formData, final Properties headers) throws DMSException, Exception {
        DMSDownloadUtil.LOGGER.log(Level.INFO, "Ondemand download for Feature : " + featureName);
        this.initiateStaticServerUpdate(new ArrayList<String>(Arrays.asList(componentName)));
        final Row featureRow = this.getFeatureRow(componentName, featureName);
        final Row metaRow = this.getMetaRow((Long)featureRow.get("FEATURE_ID"));
        final String statusOfMetaDownload = this.getStaticDMSParams(Arrays.asList(componentName + ".status")).getOrDefault(componentName + ".status", "");
        if (!statusOfMetaDownload.equals("DMS001")) {
            throw new DMSException(statusOfMetaDownload);
        }
        if (metaRow != null) {
            final String checksum = (String)metaRow.get("FILE_CHECKSUM");
            final String checksumType = (String)this.getValueForComponent(componentName, "CHECKSUM_TYPE");
            final UtilAccessAPI utilAccessAPI = ApiFactoryProvider.getUtilAccessAPI();
            final String crsFilePath = utilAccessAPI.getCrsBaseUrl() + "/" + metaRow.get("FILE_PATH");
            destinationPath = ((destinationPath == null) ? utilAccessAPI.getServerHome().concat(File.separator).concat(featureRow.get("DESTINATION_PATH").toString()) : destinationPath);
            final DMSDownloadEvent downloadEvent = new DMSDownloadEvent(featureName, crsFilePath, destinationPath);
            final DownloadStatus status = DownloadManager.getInstance().downloadFileWithCheckSumValidation(crsFilePath, destinationPath, checksum, checksumType, formData, headers, new SSLValidationType[0]);
            downloadEvent.setStatusCode(status.getStatus());
            downloadEvent.setDownloadStatus(status);
            return downloadEvent;
        }
        DMSDownloadUtil.LOGGER.log(Level.INFO, "No meta row found, need to sync component meta, will be done in next task cycle");
        final DMSDownloadEvent downloadEvent2 = new DMSDownloadEvent(featureName, null, destinationPath);
        return downloadEvent2;
    }
    
    public void clearLastModifiedForResync() {
        try {
            final DataObject cachedComponentsDataObj = this.getComponentsDataObj(null);
            if (cachedComponentsDataObj != null && !cachedComponentsDataObj.isEmpty()) {
                final Iterator<Row> downloadComponentsRows = cachedComponentsDataObj.getRows("DMSDownloadComponents");
                final List<String> lastModifiedComponentNames = new ArrayList<String>(cachedComponentsDataObj.size("DMSDownloadComponents"));
                downloadComponentsRows.forEachRemaining(row -> list.add((String)row.get("COMPONENT_NAME") + ".ifModifiedSince"));
                this.deleteStaticDMSParams(lastModifiedComponentNames);
            }
        }
        catch (final Exception ex) {
            DMSDownloadUtil.LOGGER.log(Level.SEVERE, "Exception occurred while clearing static params : ", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("dms");
        DMSDownloadUtil.instance = null;
    }
}
