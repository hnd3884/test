package com.adventnet.sym.server.mdm.enroll;

import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.adventnet.persistence.WritableDataObject;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Properties;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.adventnet.sym.server.mdm.util.MDMAgentBuildVersionsUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.UUID;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.DeviceModel;
import java.io.BufferedReader;
import java.util.logging.Logger;

public class MDMModelNameMappingHandler
{
    public static Logger logger;
    String completedFileNamePath;
    protected BufferedReader reader;
    DeviceModel deviceModel;
    String folderPath;
    String checkSum;
    public static int SUPPORTED_MODEL_IDX;
    public static int MANUFACTURER_IDX;
    public static int MODEL_NAME_IDX;
    public static int MODEL_CODE_IDX;
    public static int FORM_FACTOR_IDX;
    public static int SCREEN_SIZE_IDX;
    public static int PLATFORM_TYPE_IDX;
    public static int OPERATION_IDX;
    
    public MDMModelNameMappingHandler() {
        this.completedFileNamePath = null;
        this.reader = null;
        this.deviceModel = null;
        this.folderPath = null;
        this.checkSum = null;
    }
    
    public String getiOSDeviceSpecificModel(final String productName) throws DataAccessException {
        String deviceSpecificModel = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDSupportedDevices"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(new Column("MDSupportedDevices", "MODEL_CODE"), (Object)productName, 2);
            selectQuery.setCriteria(criteria);
            final DataObject MdIOSDeviceModelDO = MDMUtil.getCachedPersistence().get(selectQuery);
            final Row iosModelRow = MdIOSDeviceModelDO.getRow("MDSupportedDevices");
            if (iosModelRow != null) {
                deviceSpecificModel = (String)iosModelRow.get("MODEL_NAME");
            }
            else {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdIOSDeviceModel"));
                sq.addSelectColumn(new Column((String)null, "*"));
                final Criteria modelCodeCriteria = new Criteria(new Column("MdIOSDeviceModel", "MODEL_NAME"), (Object)productName, 2);
                sq.setCriteria(modelCodeCriteria);
                final DataObject mdIosDeviceModelDO = MDMUtil.getPersistenceLite().get(sq);
                final Row iosDeviceModelRow = mdIosDeviceModelDO.getRow("MdIOSDeviceModel");
                if (iosDeviceModelRow != null) {
                    deviceSpecificModel = (String)iosDeviceModelRow.get("MODEL_SPECIFIC_NAME");
                }
                else {
                    deviceSpecificModel = productName;
                }
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMInvDataPopulator.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            throw ex;
        }
        return deviceSpecificModel;
    }
    
    public String getiOSDeviceResolution(final String modelCode) {
        String resolution = null;
        DataObject dataObject = null;
        try {
            final Criteria modelCodeCriteria = new Criteria(new Column("MDSupportedDevices", "MODEL_CODE"), (Object)modelCode, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MDSupportedDevices"));
            query.addSelectColumn(new Column("MDSupportedDevices", "SUPPORTED_DEVICE_ID"));
            query.addSelectColumn(new Column("MDSupportedDevices", "SCREEN_SIZE"));
            query.setCriteria(modelCodeCriteria);
            dataObject = MDMUtil.getPersistence().get(query);
            final Row iOSDeviceModel = dataObject.getFirstRow("MDSupportedDevices");
            if (iOSDeviceModel != null) {
                resolution = (String)iOSDeviceModel.get("SCREEN_SIZE");
            }
        }
        catch (final Exception e) {
            MDMModelNameMappingHandler.logger.log(Level.SEVERE, "Exception in model name mapping - getiOSDeviceResolution", e);
        }
        return resolution;
    }
    
    public List<String> getiOSUniqueDeviceResolutions(final Integer orientation) {
        final List resolutions = new ArrayList();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MDSupportedDevices"));
            final Column distinctScreenSize = new Column("MDSupportedDevices", "SCREEN_SIZE");
            final Column modelName = new Column("MDSupportedDevices", "MODEL_NAME");
            final Criteria manufacturerCriteria = new Criteria(new Column("MDSupportedDevices", "MANUFACTURER"), (Object)"Apple", 2);
            final Criteria valueCriteria = new Criteria(new Column("MDSupportedDevices", "SCREEN_SIZE"), (Object)"--", 3);
            final Criteria nullCriteria = new Criteria(new Column("MDSupportedDevices", "SCREEN_SIZE"), (Object)null, 1);
            query.addSelectColumn(distinctScreenSize);
            query.addSelectColumn(new Column("MDSupportedDevices", "SUPPORTED_DEVICE_ID"));
            query.addSelectColumn(modelName);
            query.setCriteria(nullCriteria.and(valueCriteria).and(manufacturerCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            Iterator iterator = dataObject.getRows("MDSupportedDevices");
            final HashMap<String, Integer> tempHashMap = new HashMap<String, Integer>();
            while (iterator.hasNext()) {
                int temp = 0;
                final Row modelRow = iterator.next();
                final String screenSize = (String)modelRow.get("SCREEN_SIZE");
                final String modelSpecificName = (String)modelRow.get("MODEL_NAME");
                final Object result = tempHashMap.get(screenSize);
                if (modelSpecificName.contains("iPhone")) {
                    temp = 1;
                }
                else if (modelSpecificName.contains("iPad")) {
                    temp = 4;
                }
                else if (modelSpecificName.contains("iPod")) {
                    temp = 2;
                }
                final int tempCount = (result != null) ? ((int)result | temp) : temp;
                tempHashMap.put(screenSize, tempCount);
            }
            if (orientation == 2) {
                final Set resolutionSet = tempHashMap.keySet();
                iterator = resolutionSet.iterator();
                while (iterator.hasNext()) {
                    final String key = iterator.next();
                    final Integer value = tempHashMap.get(key);
                    if (value == 4) {
                        final String[] resolution = key.split("x");
                        final Integer widthResolution = Integer.parseInt(resolution[0]);
                        final Integer heightResolution = Integer.parseInt(resolution[1]);
                        final String changedKey = heightResolution + "x" + widthResolution;
                        resolutions.add(changedKey);
                    }
                    else if (value > 4) {
                        final String[] resolution = key.split("x");
                        final Integer widthResolution = Integer.parseInt(resolution[0]);
                        final Integer heightResolution = Integer.parseInt(resolution[1]);
                        final String changedKey = heightResolution + "x" + widthResolution;
                        resolutions.add(changedKey);
                        resolutions.add(key);
                    }
                    else {
                        resolutions.add(key);
                    }
                }
                MDMModelNameMappingHandler.logger.log(Level.FINE, "Final unique resolution:{0}", tempHashMap.toString());
            }
        }
        catch (final Exception e) {
            MDMModelNameMappingHandler.logger.log(Level.SEVERE, "Exception while getting unique Device Resolution list for iOS devices", e);
        }
        return resolutions;
    }
    
    public String getModelNameForAndroidDevices(final String modelCode, final String manufacturer, final String productName) throws DataAccessException {
        String deviceSpecificModel = null;
        try {
            final SelectQuery androidModelQuery = (SelectQuery)new SelectQueryImpl(new Table("MDSupportedDevices"));
            androidModelQuery.addSelectColumn(new Column((String)null, "*"));
            final Column lowercase = (Column)Column.createFunction("lower", new Object[] { Column.getColumn("MDSupportedDevices", "MANUFACTURER") });
            lowercase.setDataType("CHAR");
            final Criteria modelCodeCriteria = new Criteria(new Column("MDSupportedDevices", "MODEL_CODE"), (Object)modelCode, 0);
            final Criteria manufacturerCriteria = new Criteria(lowercase, (Object)manufacturer.toLowerCase(), 2);
            final Criteria criteria = manufacturerCriteria.and(modelCodeCriteria);
            androidModelQuery.setCriteria(criteria);
            final DataObject dO = SyMUtil.getPersistence().get(androidModelQuery);
            final Row androidModelRow = dO.getRow("MDSupportedDevices");
            if (androidModelRow != null) {
                deviceSpecificModel = (String)androidModelRow.get("MODEL_NAME");
            }
            else {
                deviceSpecificModel = productName;
            }
        }
        catch (final DataAccessException ex) {
            MDMModelNameMappingHandler.logger.log(Level.SEVERE, "Exception while getting ModelName in {0}", this.getClass().getName());
            throw ex;
        }
        return deviceSpecificModel;
    }
    
    public DownloadStatus downloadFileFromStaticServer() throws Exception {
        final UUID randomId = UUID.randomUUID();
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        this.folderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + randomId + File.separator + "temp";
        this.completedFileNamePath = this.folderPath + File.separator + "supported_devices.csv";
        final String sourcePath = MDMAgentBuildVersionsUtil.getMDMAgentInfo("agentstaticserverurl") + "MISC/Android/SupportedDevices/supported_devices.csv";
        final DownloadStatus downloadStatus = DownloadManager.getInstance().downloadFile(sourcePath, this.completedFileNamePath, (Properties)null, true, new SSLValidationType[0]);
        return downloadStatus;
    }
    
    public void downloadFileFromProductBundle() {
        MDMModelNameMappingHandler.logger.log(Level.INFO, "Downloading supported-devices file from api fails, hence fetching csv data from product path");
        this.folderPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDMConfiguration";
        final String buildInfilePath = this.folderPath + File.separator + "supported_devices.csv";
        if (ApiFactoryProvider.getFileAccessAPI().isFileExists(buildInfilePath)) {
            this.completedFileNamePath = buildInfilePath;
        }
    }
    
    public String getConstructedFilePath() {
        return this.completedFileNamePath;
    }
    
    protected void updateFileDiff(final String filePath) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String currentLine = "";
            this.reader.readLine();
            final DataObject contentDiff = (DataObject)new WritableDataObject();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MDSupportedDevices"));
            final Column column = new Column("MDSupportedDevices", "*");
            query.addSelectColumn(column);
            final DataObject remoteDO = MDMUtil.getPersistence().get(query);
            MDMModelNameMappingHandler.logger.log(Level.INFO, "Remote Data Object has been fetched successfully");
            long chunkDataObjectCount = 0L;
            final Integer operationProps = MDMModelNameMappingHandler.OPERATION_IDX;
            while ((currentLine = this.reader.readLine()) != null) {
                ++chunkDataObjectCount;
                final String[] entries = currentLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                this.deviceModel = new DeviceModel(entries);
                final Row row = new Row("MDSupportedDevices");
                row.set("SUPPORTED_DEVICE_ID", (Object)this.deviceModel.getSupportedModelId());
                row.set("MANUFACTURER", (Object)this.deviceModel.getManufacturer());
                row.set("MODEL_NAME", (Object)this.deviceModel.getModelName());
                row.set("MODEL_CODE", (Object)this.deviceModel.getModelCode());
                row.set("FORM_FACTOR", (Object)this.deviceModel.getFormFactor());
                row.set("SCREEN_SIZE", (Object)this.deviceModel.getScreenSize());
                row.set("PLATFORM_TYPE", (Object)this.deviceModel.getPlatformType());
                if (entries[operationProps].equalsIgnoreCase("[INSERT]") && remoteDO.findRow(row) == null) {
                    contentDiff.addRow(row);
                }
                if (entries[operationProps].equalsIgnoreCase("[DELETE]") && remoteDO.findRow(row) != null) {
                    final Criteria deleteRowCriteria = new Criteria(new Column("MDSupportedDevices", "SUPPORTED_DEVICE_ID"), (Object)this.deviceModel.getSupportedModelId(), 0);
                    remoteDO.deleteRows("MDSupportedDevices", deleteRowCriteria);
                    MDMModelNameMappingHandler.logger.log(Level.INFO, "Primary key of {0} row has been deleted {1}", new Object[] { this.deviceModel.getSupportedModelId(), this.getClass().getName() });
                }
                if (entries[operationProps].equalsIgnoreCase("[UPDATE]")) {
                    final Row r = new Row("MDSupportedDevices");
                    r.set("SUPPORTED_DEVICE_ID", (Object)this.deviceModel.getSupportedModelId());
                    r.set("MODEL_NAME", (Object)this.deviceModel.getModelName());
                    r.set("MANUFACTURER", (Object)this.deviceModel.getManufacturer());
                    r.set("MODEL_CODE", (Object)this.deviceModel.getModelCode());
                    r.set("FORM_FACTOR", (Object)this.deviceModel.getFormFactor());
                    r.set("SCREEN_SIZE", (Object)this.deviceModel.getScreenSize());
                    r.set("PLATFORM_TYPE", (Object)this.deviceModel.getPlatformType());
                    if (remoteDO.findRow(row) == null) {
                        contentDiff.addRow(r);
                    }
                    else {
                        remoteDO.updateRow(r);
                    }
                    MDMModelNameMappingHandler.logger.log(Level.INFO, "Primary key of {0} row has been updated {1}", new Object[] { this.deviceModel.getSupportedModelId(), this.getClass().getName() });
                }
                if (chunkDataObjectCount > 1000L) {
                    MDMUtil.getPersistence().add(contentDiff);
                    chunkDataObjectCount = 0L;
                    contentDiff.deleteRows("MDSupportedDevices", (Criteria)null);
                    MDMModelNameMappingHandler.logger.log(Level.INFO, "Data Object chunk has been persisted in table {0}", this.getClass().getName());
                }
            }
            MDMUtil.getPersistence().add(contentDiff);
            MDMUtil.getPersistence().update(remoteDO);
            MDMModelNameMappingHandler.logger.log(Level.INFO, "Last data object chunk has been persisted in table {0}", this.getClass().getName());
        }
        catch (final Exception ex) {
            MDMModelNameMappingHandler.logger.log(Level.WARNING, "Exception occurred while constructing data object", ex);
            try {
                if (this.reader != null) {
                    this.reader.close();
                }
            }
            catch (final Exception e) {
                MDMModelNameMappingHandler.logger.log(Level.WARNING, e, () -> "Exception occurred while closing the reader object in " + this.getClass().getName());
            }
        }
        finally {
            try {
                if (this.reader != null) {
                    this.reader.close();
                }
            }
            catch (final Exception e2) {
                MDMModelNameMappingHandler.logger.log(Level.WARNING, e2, () -> "Exception occurred while closing the reader object in " + this.getClass().getName());
            }
        }
    }
    
    public void addOrUpdateSupportedDevices(final String filePath, final int cacheType) {
        try {
            final String checkSumValue = this.getChecksum();
            final Object cachedFileHash = ApiFactoryProvider.getCacheAccessAPI().getCache("ANDROID_SUPPORTED_DEVICES", cacheType);
            if (!checkSumValue.equals(cachedFileHash)) {
                MDMModelNameMappingHandler.logger.log(Level.INFO, "Checksum value changes, so proceeding with inserting new rows to database");
                this.updateFileDiff(filePath);
                ApiFactoryProvider.getCacheAccessAPI().putCache("ANDROID_SUPPORTED_DEVICES", (Object)checkSumValue, cacheType);
                MDMModelNameMappingHandler.logger.log(Level.INFO, "New checksum value persisted in cache {0}", checkSumValue);
            }
            else {
                MDMModelNameMappingHandler.logger.log(Level.INFO, "Checksum not changed...");
            }
        }
        catch (final Exception exception) {
            MDMModelNameMappingHandler.logger.log(Level.WARNING, "Exception occurred while addOrUpdateSupportedDevices method execution", exception);
        }
    }
    
    public void calculateCheckSum(final String filePath) {
        this.checkSum = ChecksumProvider.getInstance().GetSHA256CheckSum(filePath, Boolean.valueOf(true));
    }
    
    private String getChecksum() {
        return this.checkSum;
    }
    
    static {
        MDMModelNameMappingHandler.logger = Logger.getLogger("MDMLogger");
        MDMModelNameMappingHandler.SUPPORTED_MODEL_IDX = 0;
        MDMModelNameMappingHandler.MANUFACTURER_IDX = 1;
        MDMModelNameMappingHandler.MODEL_NAME_IDX = 2;
        MDMModelNameMappingHandler.MODEL_CODE_IDX = 3;
        MDMModelNameMappingHandler.FORM_FACTOR_IDX = 5;
        MDMModelNameMappingHandler.SCREEN_SIZE_IDX = 8;
        MDMModelNameMappingHandler.PLATFORM_TYPE_IDX = 13;
        MDMModelNameMappingHandler.OPERATION_IDX = 14;
    }
}
