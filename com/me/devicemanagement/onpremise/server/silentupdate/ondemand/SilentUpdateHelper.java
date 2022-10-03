package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.util.Arrays;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.Hashtable;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.Criteria;
import org.json.simple.JSONArray;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class SilentUpdateHelper
{
    private static Logger logger;
    private static String sourceClass;
    private static Properties productConfigProps;
    private static SilentUpdateHelper silentUpdateHelper;
    private Integer currentBuildNumber;
    private static String productCode;
    private static SilentUpdateProductListener silentUpdateProductListener;
    
    public SilentUpdateHelper() {
        this.currentBuildNumber = null;
    }
    
    public static SilentUpdateHelper getInstance() {
        if (SilentUpdateHelper.silentUpdateHelper == null) {
            SilentUpdateHelper.silentUpdateHelper = new SilentUpdateHelper();
        }
        return SilentUpdateHelper.silentUpdateHelper;
    }
    
    public String productSpecificProps(final String key) {
        try {
            if (SilentUpdateHelper.productConfigProps == null) {
                SilentUpdateHelper.productConfigProps = FileAccessUtil.readProperties(System.getProperty("server.home") + File.separator + "conf" + File.separator + "SilentUpdate" + File.separator + "product-specific.props");
            }
            if (SilentUpdateHelper.productConfigProps.containsKey(key)) {
                return ((Hashtable<K, Object>)SilentUpdateHelper.productConfigProps).get(key).toString();
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "productSpecificProps", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    public String customerSpecificProps(final String key) {
        try {
            final Properties properties = FileAccessUtil.readProperties(this.getSilentUpdateUserConfPath() + File.separator + "customer-specific.props");
            if (properties.containsKey(key)) {
                return ((Hashtable<K, Object>)properties).get(key).toString();
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "customerSpecificProps", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    public String getSilentUpdateBinaryHome() {
        return System.getProperty("server.home") + File.separator + "SilentUpdateBinarys";
    }
    
    public String getSilentUpdateMetaLocalPath() {
        return System.getProperty("server.home") + File.separator + "conf" + File.separator + "SilentUpdate" + File.separator + "SilentUpdateMeta.json";
    }
    
    public String getSilentUpdateUserConfPath() {
        return System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "SilentUpdate";
    }
    
    public void updateCustomerConfigProps(final String key, final Object value) {
        try {
            final Properties props = new Properties();
            ((Hashtable<String, String>)props).put(key, String.valueOf(value));
            FileAccessUtil.storeProperties(props, this.getSilentUpdateUserConfPath() + File.separator + "customer-specific.props", true);
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "updateCustomerConfigProps", "Customer config props update failed : key - " + key + ", value - " + value, (Throwable)e);
        }
    }
    
    protected boolean updateTasksTODB(final JSONArray tasks) {
        boolean updateTasksTODB = true;
        try {
            final DataObject dataObject = this.getQPPMDetails(null, false);
            final Long lastModifiedTimeInCRS = dataObject.isEmpty() ? null : Long.valueOf(dataObject.getFirstRow("SilentUpdateDetails").get("MODIFIED_TIME_IN_CRS").toString());
            for (int i = 0, len = tasks.size(); i < len; ++i) {
                final JSONObject taskJson = (JSONObject)tasks.get(i);
                if (taskJson.containsKey((Object)"ID")) {
                    final Long taskId = Long.valueOf(taskJson.get((Object)"ID").toString());
                    if (lastModifiedTimeInCRS == null || this.getTaskModifiedTimeAsLong(taskJson) > lastModifiedTimeInCRS) {
                        Hashtable<String, Row> rowSet = new Hashtable<String, Row>();
                        final Row existsRow = dataObject.isEmpty() ? null : dataObject.getRow("SilentUpdateDetails", new Criteria(Column.getColumn("SilentUpdateDetails", "TASK_ID"), (Object)taskId, 0));
                        final Row existsExtnRow = dataObject.isEmpty() ? null : dataObject.getRow("SilentUpdateDetailsExtn", new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID"), (Object)taskId, 0));
                        if (existsRow == null) {
                            rowSet.put("SilentUpdateDetails", new Row("SilentUpdateDetails"));
                            rowSet.put("SilentUpdateDetailsExtn", new Row("SilentUpdateDetailsExtn"));
                        }
                        else {
                            rowSet.put("SilentUpdateDetails", existsRow);
                            rowSet.put("SilentUpdateDetailsExtn", existsExtnRow);
                        }
                        rowSet = this.insertValues(taskJson, rowSet);
                        if (rowSet != null) {
                            if (existsRow == null) {
                                dataObject.addRow((Row)rowSet.get("SilentUpdateDetails"));
                                dataObject.addRow((Row)rowSet.get("SilentUpdateDetailsExtn"));
                            }
                            else {
                                dataObject.updateRow((Row)rowSet.get("SilentUpdateDetails"));
                                dataObject.updateRow((Row)rowSet.get("SilentUpdateDetailsExtn"));
                            }
                        }
                        else {
                            SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "updateTasksTODB", "Invalid Task : " + taskId);
                        }
                    }
                    else {
                        SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "updateTasksTODB", "Task not modified  : " + taskId);
                    }
                }
                else {
                    SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "updateTasksTODB", "Invalid task ID not found : " + taskJson);
                }
            }
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "updateTasksTODB", "Exception occurred : ", (Throwable)e);
            updateTasksTODB = false;
        }
        return updateTasksTODB;
    }
    
    private Hashtable<String, Row> insertValues(final JSONObject taskJson, final Hashtable<String, Row> rowSet) {
        try {
            final String emptyString = "";
            final String emptyJson = "{}";
            final String emptyJsonArray = "[]";
            final Row row = rowSet.get("SilentUpdateDetails");
            final Row extnRow = rowSet.get("SilentUpdateDetailsExtn");
            row.set("TASK_ID", (Object)Long.valueOf(taskJson.get((Object)"ID").toString()));
            row.set("TASK_TYPE", (Object)taskJson.get((Object)"TaskType").toString());
            final String qppmUniqueID = taskJson.get((Object)"QPPMID").toString();
            row.set("QPPM_ID", (Object)qppmUniqueID);
            row.set("QPPM_URL", (Object)taskJson.get((Object)"QPPMUrl").toString());
            row.set("QPPM_CHECKSUM", (Object)taskJson.get((Object)"QPPMChecksum").toString());
            final String string;
            final String typeOfTheQPPM = string = taskJson.get((Object)"QPPMType").toString();
            switch (string) {
                case "Without Restart": {
                    row.set("QPPM_TYPE", (Object)0);
                    break;
                }
                case "With Restart": {
                    row.set("QPPM_TYPE", (Object)1);
                    break;
                }
                default: {
                    SyMLogger.warning(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "insertValues", "Un supported QPPM type.");
                    return null;
                }
            }
            row.set("MODIFIED_TIME_IN_CRS", (Object)this.getTaskModifiedTimeAsLong(taskJson));
            final Integer baseBuildNumber = Integer.valueOf(taskJson.get((Object)"BaseBuildNumber").toString());
            row.set("BASE_BUILD_NUMBER", (Object)baseBuildNumber);
            if (taskJson.containsKey((Object)"AlertMessageContent") && !"".equals(taskJson.get((Object)"AlertMessageContent").toString())) {
                row.set("ALERT_MSG_CONTENT", (Object)taskJson.get((Object)"AlertMessageContent").toString());
            }
            if (taskJson.containsKey((Object)"ProductComponents")) {
                final String productComponents = taskJson.get((Object)"ProductComponents").toString();
                final String serverComponemtsDetails = ("".equals(productComponents) || "{}".equals(productComponents)) ? null : new org.json.JSONObject(productComponents).toString();
                if (serverComponemtsDetails != null) {
                    row.set("PRODUCT_COMPONENTS", (Object)serverComponemtsDetails);
                }
            }
            if (taskJson.containsKey((Object)"ImmediateMoveProductComponentList")) {
                final String immediateMoveProductComponents = taskJson.get((Object)"ImmediateMoveProductComponentList").toString();
                final String immediateMoveProductComponentList = ("".equals(immediateMoveProductComponents) || "[]".equals(immediateMoveProductComponents)) ? null : new org.json.JSONArray(immediateMoveProductComponents).toString();
                if (immediateMoveProductComponentList != null) {
                    row.set("IMMEDIATE_MOVE_PRODUCT_COMPONENTS", (Object)immediateMoveProductComponentList);
                }
            }
            if (taskJson.containsKey((Object)"HideRemindmeLater")) {
                row.set("HIDE_REMINDME_LATER", (Object)Boolean.parseBoolean(taskJson.get((Object)"HideRemindmeLater").toString()));
            }
            if (taskJson.containsKey((Object)"ShowDismiss")) {
                row.set("SHOW_DISMISS", (Object)Boolean.parseBoolean(taskJson.get((Object)"ShowDismiss").toString()));
            }
            if (taskJson.containsKey((Object)"ShowIgnoreThisFix")) {
                row.set("SHOW_IGNORE_THIS_FIX", (Object)Boolean.parseBoolean(taskJson.get((Object)"ShowIgnoreThisFix").toString()));
            }
            if (taskJson.containsKey((Object)"AlertMsgFrequency") && !"".equals(taskJson.get((Object)"AlertMsgFrequency").toString())) {
                row.set("ALERT_MSG_FREQUENCY", (Object)Integer.parseInt(taskJson.get((Object)"AlertMsgFrequency").toString()));
            }
            if (taskJson.containsKey((Object)"DynamicCheckerClassUrl") && !"".equals(taskJson.get((Object)"DynamicCheckerClassUrl").toString()) && taskJson.containsKey((Object)"DynamicCheckerClassChecksum") && !"".equals(taskJson.get((Object)"DynamicCheckerClassChecksum").toString())) {
                row.set("DYNAMIC_CHECKER_CLASS_URL", (Object)taskJson.get((Object)"DynamicCheckerClassUrl").toString());
                row.set("DYNAMIC_CHECKER_CLASS_CHECKSUM", (Object)taskJson.get((Object)"DynamicCheckerClassChecksum").toString());
            }
            extnRow.set("TASK_ID", (Object)Long.valueOf(taskJson.get((Object)"ID").toString()));
            final Integer currentBuildNumber = this.getCurrentBuildNumber();
            if (currentBuildNumber == (int)baseBuildNumber) {
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "insertValues", "The QPPM(" + qppmUniqueID + ") is applicable for this setup.Set the QPPM status as 'QPPM_DOWNLOAD_PENDING'");
                extnRow.set("TASK_STATUS", (Object)0);
            }
            else {
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "insertValues", "The QPPM(" + qppmUniqueID + ") is not applicable for this setup. Set the QPPM status as 'QPPM_NOT_APPLICABLE'");
                extnRow.set("TASK_STATUS", (Object)(-1));
            }
            if (extnRow.get("IS_FIX_APPROVED") == null) {
                final String typeOfThetask = taskJson.get((Object)"TaskType").toString();
                if (typeOfThetask.equalsIgnoreCase("Security")) {
                    extnRow.set("IS_FIX_APPROVED", (Object)this.isEmergencyFixApproveEnabled());
                }
                else {
                    extnRow.set("IS_FIX_APPROVED", (Object)new SilentUpdateHandler().isAutoApproveEnabled());
                }
            }
            return rowSet;
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "insertValues", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    private Long getTaskModifiedTimeAsLong(final JSONObject taskJson) throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return simpleDateFormat.parse(taskJson.get((Object)"Modified_Time").toString()).getTime();
    }
    
    public DataObject getQPPMDetails(final Criteria criteria, final boolean latestOnly) {
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("SilentUpdateDetails"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetails", "*"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "TASK_ID", "SilentUpdateDetailsExtn.TASK_ID"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "TASK_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "ALERT_MSG_LAST_SHOWN_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "IS_SHOW_ALERT_MSG"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "IS_FIX_APPROVED"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetailsExtn", "QPPM_FIXES_ID"));
            selectQuery.addJoin(new Join("SilentUpdateDetails", "SilentUpdateDetailsExtn", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2));
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("SilentUpdateDetails", "MODIFIED_TIME_IN_CRS"), false));
            if (latestOnly) {
                selectQuery.setRange(new Range(0, 1));
            }
            return DataAccess.get((SelectQuery)selectQuery);
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getQPPMDetails", "Exception occurred while fetching details from 'SilentUpdateDetails' and 'SilentUpdateDetailsExtn' Table : ", (Throwable)e);
            return null;
        }
    }
    
    public ArrayList<String> getLatest3QPPMUniqueIds() {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("SilentUpdateDetails"));
            selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetails", "*"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("SilentUpdateDetails", "MODIFIED_TIME_IN_CRS"), false));
            selectQuery.setRange(new Range(0, 3));
            final Iterator itr = DataAccess.get((SelectQuery)selectQuery).getRows("SilentUpdateDetails");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final String key = row.get("QPPM_ID").toString();
                list.add(key);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getLatest3QPPMUniqueIds", "Exception occurred while fetching latest 3 QPPM UniqueIds : ", (Throwable)e);
        }
        return list;
    }
    
    protected Integer getCurrentBuildNumber() {
        try {
            if (this.currentBuildNumber == null) {
                this.currentBuildNumber = DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB();
            }
            return this.currentBuildNumber;
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "extractQPPM", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    protected String getProductCode() {
        try {
            if (SilentUpdateHelper.productCode == null) {
                if (EMSProductUtil.isEMSFlowSupportedForCurrentProduct()) {
                    return "UEMS";
                }
                final Properties properties = GeneralPropertiesLoader.getInstance().getProperties();
                SilentUpdateHelper.productCode = properties.getProperty("productcode");
            }
            return SilentUpdateHelper.productCode;
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getProductCode", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    protected String extractQPPM(final String qppmUniqueId, final String... includeList) {
        try {
            final File qPPM = new File(getInstance().getSilentUpdateBinaryHome() + File.separator + "QPPMRepo" + File.separator + qppmUniqueId + ".qpm");
            final String destinationFolder = getInstance().getSilentUpdateBinaryHome() + File.separator + "Temp" + File.separator + qppmUniqueId;
            List<String> includeFileList = null;
            if (includeList != null) {
                includeFileList = Arrays.asList(includeList);
            }
            System.setProperty("tools.7zip.win.path", System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za");
            final int unZipStatus = SevenZipUtils.unZip(qPPM, new File(destinationFolder), (List)includeFileList, (List)null);
            SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "extractQPPM", "Status code for qppm unzip : " + unZipStatus);
            if (unZipStatus != 0) {
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "extractQPPM", "Agent binary's extract failed");
                return null;
            }
            return destinationFolder;
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "extractQPPM", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    public JSONObject convertRowTOJson(final Row row) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final List<Object> values = row.getValues();
        final List<String> columns = row.getColumns();
        for (int i = 0; i < values.size(); ++i) {
            if (values.get(i) != null) {
                jsonObject.put((Object)columns.get(i).toString(), values.get(i));
            }
        }
        return jsonObject;
    }
    
    public boolean loadDynamicChecker(final String qppmUniqueId) {
        try {
            final String dynamicCheckerFilePath = getInstance().getSilentUpdateBinaryHome() + File.separator + "DynamicCheckerRepo" + File.separator + qppmUniqueId + File.separator + "DynamicChecker" + File.separator + SilentUpdateDynamicChecker.class.getSimpleName() + ".class";
            if (new File(dynamicCheckerFilePath).exists()) {
                if (ClassReloader.getInstance().redefineClass(dynamicCheckerFilePath, SilentUpdateDynamicChecker.class.getCanonicalName())) {
                    SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "loadDynamicChecker", "SilentUpdateDynamicChecker has been reloaded for this QPPM : " + qppmUniqueId);
                    return true;
                }
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "loadDynamicChecker", "SilentUpdateDynamicChecker has been reload failed for this QPPM : " + qppmUniqueId + ", Proceed with default behaviour");
            }
            else {
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "loadDynamicChecker", "SilentUpdateDynamicChecker not available this QPPM : " + qppmUniqueId + ", Proceed with default behaviour");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "loadDynamicChecker", "Exception occurred : ", (Throwable)e);
        }
        return false;
    }
    
    public boolean isQPPMAlreadyApplied(final String qppmUniqueId, final List<String> qppmFixIdList) {
        try {
            final String fixesIdPropsDir = "conf" + File.separator + "JarTracker" + File.separator + "fixes_id.properties";
            final String fixesIdPropsPath = System.getProperty("server.home") + File.separator + fixesIdPropsDir;
            final String existsQPPMIds = String.valueOf(((Hashtable<K, Object>)FileAccessUtil.readProperties(fixesIdPropsPath)).get("unique_id"));
            SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "Exits fixes id from fixes_id.properties : " + existsQPPMIds);
            final List<String> existsQPPMList = (existsQPPMIds.equalsIgnoreCase("null") || existsQPPMIds.equalsIgnoreCase("")) ? new ArrayList<String>() : Arrays.asList(existsQPPMIds.split(","));
            if (existsQPPMList.size() != 0) {
                if (qppmFixIdList.size() != 0) {
                    for (final String qppmFixId : qppmFixIdList) {
                        if (!existsQPPMIds.contains(qppmFixId)) {
                            return false;
                        }
                    }
                    SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "There is no different fix id in this QPPM(" + qppmUniqueId + ")");
                    return true;
                }
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "There is no fix id to compare in this QPPM(" + qppmUniqueId + ")");
            }
            else {
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "There is no fix id's in this setup, It's fresh setup!!!");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "Exception occurred while checking is QPPM(" + qppmUniqueId + ") already applied : ", (Throwable)e);
        }
        return false;
    }
    
    public String getQPPMFixesIds(final String qppmUniqueId) {
        try {
            final String fixesIdPropsDir = "conf" + File.separator + "JarTracker" + File.separator + "fixes_id.properties";
            final String qppmFixesIdFilePath = this.extractQPPM(qppmUniqueId, fixesIdPropsDir) + File.separator + fixesIdPropsDir;
            if (new File(qppmFixesIdFilePath).exists()) {
                final String qppmFixIds = String.valueOf(((Hashtable<K, Object>)FileAccessUtil.readProperties(qppmFixesIdFilePath)).get("unique_id"));
                return (qppmFixIds.equalsIgnoreCase("null") || qppmFixIds.equalsIgnoreCase("")) ? "" : qppmFixIds;
            }
            SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "Unable to find fixes_id.properties file QPPM(" + qppmUniqueId + ")");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "isQPPMAlreadyApplied", "Exception occurred while checking is QPPM(" + qppmUniqueId + ") already applied : ", (Throwable)e);
        }
        return "";
    }
    
    public void updateDBValue(final String tableName, final String columnName, final Object value) throws Exception {
        this.updateDBValue(tableName, columnName, value, null);
    }
    
    public void updateDBValue(final String tableName, final String columnName, final Object value, final Criteria criteria) throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.setUpdateColumn(columnName, value);
        if (criteria != null) {
            updateQuery.setCriteria(criteria);
        }
        SyMUtil.getPersistence().update(updateQuery);
    }
    
    public SilentUpdateProductListener getProductListenerInstance() {
        try {
            if (SilentUpdateHelper.silentUpdateProductListener == null) {
                final String classname = ProductClassLoader.getSingleImplProductClass("SILENT_UPDATE_PRODUCT_LISTENER");
                if (classname != null && classname.trim().length() != 0) {
                    SilentUpdateHelper.silentUpdateProductListener = (SilentUpdateProductListener)Class.forName(classname).newInstance();
                }
            }
            return SilentUpdateHelper.silentUpdateProductListener;
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getProductListenerInstance", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    protected void copyQPPMTOQuickFix(final String qppmUniqueId) throws IOException {
        try {
            final String ondemandQuickFixHome = System.getProperty("server.home") + File.separator + "quickfixer" + File.separator + "Ondemand";
            if (!Files.exists(Paths.get(ondemandQuickFixHome, new String[0]), new LinkOption[0])) {
                Files.createDirectories(Paths.get(ondemandQuickFixHome, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
                SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "copyQPPMTOQuickFix", "On-demand quick fix home has been created!!!");
            }
            final String newFileName = ondemandQuickFixHome + File.separator + qppmUniqueId + ".qpm";
            final String oldFile = getInstance().getSilentUpdateBinaryHome() + File.separator + "QPPMRepo" + File.separator + qppmUniqueId + ".qpm";
            Files.copy(Paths.get(oldFile, new String[0]), Paths.get(newFileName, new String[0]), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "copyQPPMTOQuickFix", "Exception occurred : ", (Throwable)e);
            throw e;
        }
    }
    
    public ArrayList<String> getQPPMTaskIDForNonSecurityTask() {
        final ArrayList<String> taskList = new ArrayList<String>();
        final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("SilentUpdateDetails"));
        selectQuery.addSelectColumn(Column.getColumn("SilentUpdateDetails", "TASK_ID"));
        final Criteria taskTypeCriteria = new Criteria(Column.getColumn("SilentUpdateDetails", "TASK_TYPE"), (Object)"Security", 1);
        if (taskTypeCriteria != null) {
            selectQuery.setCriteria(taskTypeCriteria);
        }
        try {
            final Iterator itr = DataAccess.get((SelectQuery)selectQuery).getRows("SilentUpdateDetails");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final String key = row.get("TASK_ID").toString();
                taskList.add(key);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getQPPMTaskIDForNonSecurityTask", "Exception occurred : ", (Throwable)ex);
        }
        return taskList;
    }
    
    public String getQPPMTaskType(final String taskID) {
        String taskType = null;
        SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getQPPMTaskType", "task ID in getQPPMTaskType is {0}" + taskID);
        final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("SilentUpdateDetails"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria criteria = new Criteria(Column.getColumn("SilentUpdateDetails", "TASK_ID"), (Object)taskID, 0);
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            final DataObject dob = DataAccess.get((SelectQuery)selectQuery);
            SyMLogger.info(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getQPPMTaskType", "dob in getQPPMTaskType is {0}" + dob);
            if (dob != null) {
                taskType = dob.getFirstRow("SilentUpdateDetails").get("TASK_TYPE").toString();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "getQPPMTaskType", "Exception occurred : ", (Throwable)ex);
        }
        return taskType;
    }
    
    public boolean isQPPMTaskTypeSecurity(final String taskID) {
        final String taskType = this.getQPPMTaskType(taskID);
        return taskType != null && !taskType.isEmpty() && taskType.equalsIgnoreCase("Security");
    }
    
    public boolean isEmergencyFixApproveEnabled() {
        try {
            final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
            final Properties properties = FileAccessUtil.readProperties(systemPropertiesPath);
            if (properties.containsKey("proceed.emergencyfixupdate.autoapprove")) {
                return Boolean.parseBoolean(((Hashtable<K, Object>)properties).get("proceed.emergencyfixupdate.autoapprove").toString());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateHelper.logger, SilentUpdateHelper.sourceClass, "updateCustomerConfigProps", "Exception occurred while read property : ", (Throwable)e);
        }
        return false;
    }
    
    static {
        SilentUpdateHelper.logger = Logger.getLogger("SilentUpdate");
        SilentUpdateHelper.sourceClass = "SilentUpdateHelper";
        SilentUpdateHelper.productConfigProps = null;
        SilentUpdateHelper.silentUpdateHelper = null;
        SilentUpdateHelper.productCode = null;
        SilentUpdateHelper.silentUpdateProductListener = null;
    }
}
