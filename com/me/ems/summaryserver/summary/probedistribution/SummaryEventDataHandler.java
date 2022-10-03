package com.me.ems.summaryserver.summary.probedistribution;

import java.util.Map;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.ArrayList;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.io.File;
import java.util.logging.Logger;
import java.util.List;

public class SummaryEventDataHandler
{
    private static List<String> tablesListForSync;
    public static final String SYNC_REQUIRED_TABLES = "newProbeSyncRequiredTables";
    private static SummaryEventDataHandler eventDataHandler;
    public static Logger logger;
    private static final String summaryEventDir;
    
    private void populateSyncRequiredTablesForNewProbe() {
        try {
            final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DMSummaryServer" + File.separator + "syncSettings.json";
            if (new File(filePath).exists()) {
                final File syncSettingsFile = new File(filePath);
                final JSONObject syncSettingsJSON = JsonUtils.loadJsonFile(syncSettingsFile);
                final JSONArray syncRequiredTables = syncSettingsJSON.getJSONArray("newProbeSyncRequiredTables");
                for (int index = 0; index < syncRequiredTables.length(); ++index) {
                    SummaryEventDataHandler.tablesListForSync.add(syncRequiredTables.getString(index));
                }
            }
            else {
                SummaryEventDataHandler.logger.log(Level.SEVERE, "syncSettings.json File Doesn't exists");
            }
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while populateSyncRequiredTablesForNewProbe", e);
        }
    }
    
    public static SummaryEventDataHandler getInstance() {
        if (SummaryEventDataHandler.eventDataHandler == null) {
            SummaryEventDataHandler.eventDataHandler = new SummaryEventDataHandler();
        }
        return SummaryEventDataHandler.eventDataHandler;
    }
    
    private void cleanUpSummaryEventData(final int eventCode, final Long uniqueID) {
        final int apiRecreationType = this.getAPIRecreationType(eventCode);
        switch (apiRecreationType) {
            case 2: {
                this.deleteAlreadyExistingSummaryEventData(this.getCriteriaForLatestApi(eventCode, -1L));
                return;
            }
            case 3: {
                if (uniqueID != -1L) {
                    this.deleteAlreadyExistingSummaryEventData(this.getCriteriaForLatestApi(eventCode, uniqueID));
                }
                return;
            }
            case 4: {
                final int eventGroupId = this.getEventGroupId(eventCode);
                if (eventGroupId != -1) {
                    this.deleteSummaryEventDataApplicableForEventGroup(eventGroupId, uniqueID);
                }
            }
            default: {}
        }
    }
    
    private void deleteSummaryEventDataApplicableForEventGroup(final int eventGroupId, final Long uniqueID) {
        try {
            if (eventGroupId != -1) {
                this.deleteEventFilesForGroup(eventGroupId, uniqueID);
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("SummaryEventData");
                deleteQuery.addJoin(new Join("SummaryEventData", "EventCodeSummaryExtn", new String[] { "EVENT_ID" }, new String[] { "EVENT_ID" }, 2));
                Criteria criteria = new Criteria(Column.getColumn("EventCodeSummaryExtn", "EVENT_GROUP_ID"), (Object)eventGroupId, 0);
                criteria = criteria.and(new Criteria(Column.getColumn("SummaryEventData", "EVENT_UNIQUE_ID"), (Object)uniqueID, 0));
                deleteQuery.setCriteria(criteria);
                DataAccess.delete(deleteQuery);
            }
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while deleteEventFilesForEventGroup", e);
        }
    }
    
    private Criteria getCriteriaForDeletedApi(final int eventGroupId, final Long uniqueID) {
        Criteria criteria = new Criteria(Column.getColumn("SummaryEventData", "EVENT_UNIQUE_ID"), (Object)uniqueID, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("EventCodeSummaryExtn", "EVENT_GROUP_ID"), (Object)eventGroupId, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("SummaryEventData", "IS_REQUIRED_FOR_NEW_PROBE"), (Object)true, 0));
        return criteria;
    }
    
    private Criteria getCriteriaForLatestApi(final int eventId, final Long uniqueID) {
        Criteria criteria = new Criteria(Column.getColumn("SummaryEventData", "EVENT_ID"), (Object)eventId, 0);
        if (uniqueID != -1L) {
            criteria = criteria.and(new Criteria(Column.getColumn("SummaryEventData", "EVENT_UNIQUE_ID"), (Object)uniqueID, 0));
        }
        criteria = criteria.and(new Criteria(Column.getColumn("SummaryEventData", "IS_REQUIRED_FOR_NEW_PROBE"), (Object)true, 0));
        return criteria;
    }
    
    public Criteria getApplicableForNewProbeCriteria() {
        return new Criteria(Column.getColumn("SummaryEventData", "IS_REQUIRED_FOR_NEW_PROBE"), (Object)true, 0);
    }
    
    public Criteria getEventListCriteria(final List<Integer> eventIdList) {
        final Criteria criteria = new Criteria(Column.getColumn("SummaryEventData", "EVENT_ID"), (Object)eventIdList.toArray(), 8);
        return criteria;
    }
    
    private void deleteAlreadyExistingSummaryEventData(final Criteria criteria) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SummaryEventData"));
            selectQuery.addJoin(new Join("SummaryEventData", "EventCodeSummaryExtn", new String[] { "EVENT_ID" }, new String[] { "EVENT_ID" }, 2));
            selectQuery.addSelectColumn(new Column("SummaryEventData", "EVENT_FILE_PATH"));
            selectQuery.addSelectColumn(new Column("SummaryEventData", "SUMMARY_EVENT_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("SummaryEventData");
                this.deleteExistingEventDataFileIfExists((String)row.get("EVENT_FILE_PATH"));
                dataObject.deleteRow(row);
            }
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while deleteAlreadyExistingData", e);
        }
    }
    
    private void deleteEventFilesForGroup(final int eventGroupId, final Long uniqueID) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SummaryEventData"));
            selectQuery.addJoin(new Join("SummaryEventData", "EventCodeSummaryExtn", new String[] { "EVENT_ID" }, new String[] { "EVENT_ID" }, 2));
            selectQuery.addSelectColumn(new Column("SummaryEventData", "SUMMARY_EVENT_ID"));
            selectQuery.addSelectColumn(new Column("SummaryEventData", "EVENT_FILE_PATH"));
            selectQuery.setCriteria(this.getCriteriaForDeletedApi(eventGroupId, uniqueID));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rowIterator = dataObject.getRows("SummaryEventData");
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                this.deleteExistingEventDataFileIfExists((String)row.get("EVENT_FILE_PATH"));
            }
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while deleteEventFilesForGroupId", e);
        }
    }
    
    private int getAPIRecreationType(final int eventId) {
        int apiRecreationType = 1;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventCodeSummaryExtn"));
            selectQuery.addSelectColumn(new Column("EventCodeSummaryExtn", "API_RECREATION_TYPE"));
            selectQuery.addSelectColumn(new Column("EventCodeSummaryExtn", "EVENT_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("EventCodeSummaryExtn", "EVENT_ID"), (Object)eventId, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("EventCodeSummaryExtn");
                apiRecreationType = (int)((row != null) ? row.get("API_RECREATION_TYPE") : apiRecreationType);
            }
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while getAPIRecreationType", e);
        }
        return apiRecreationType;
    }
    
    private int getEventGroupId(final int eventId) {
        int eventGroupID = -1;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventCodeSummaryExtn"));
            selectQuery.addSelectColumn(new Column("EventCodeSummaryExtn", "EVENT_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("EventCodeSummaryExtn", "EVENT_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("EventCodeSummaryExtn", "EVENT_ID"), (Object)eventId, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("EventCodeSummaryExtn");
                eventGroupID = (int)((row != null) ? row.get("EVENT_GROUP_ID") : eventGroupID);
            }
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while getEventGroupId", e);
        }
        return eventGroupID;
    }
    
    public Long storeEventData(final int eventCode, final boolean isApplicableForAllProbes, final JSONObject reqObj, final long eventUniqueID, final boolean isRequiredForNewProbe) {
        long summaryEventID = -1L;
        final Long currentTime = System.currentTimeMillis();
        try {
            this.cleanUpSummaryEventData(eventCode, eventUniqueID);
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row row = new Row("SummaryEventData");
            row.set("EVENT_ID", (Object)eventCode);
            row.set("EVENT_CREATED_TIME", (Object)currentTime);
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userID != null) {
                row.set("EVENT_CREATED_BY", (Object)userID);
            }
            else {
                final Long systemUserID = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
                row.set("EVENT_CREATED_BY", (Object)systemUserID);
            }
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("IS_APPLICABLE_TO_ALL_PROBES", (Object)isApplicableForAllProbes);
            row.set("IS_REQUIRED_FOR_NEW_PROBE", (Object)isRequiredForNewProbe);
            row.set("EVENT_UNIQUE_ID", (Object)eventUniqueID);
            final String[] fileNameAndCheckSum = this.createEventDataFile(customerID, eventCode, currentTime, reqObj);
            row.set("EVENT_FILE_PATH", (Object)fileNameAndCheckSum[0]);
            row.set("FILE_CHECKSUM", (Object)fileNameAndCheckSum[1]);
            dataObject.addRow(row);
            final DataObject dataObject2 = DataAccess.add(dataObject);
            summaryEventID = (long)dataObject2.getFirstRow("SummaryEventData").get("SUMMARY_EVENT_ID");
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while storeEventData", e);
        }
        return summaryEventID;
    }
    
    private void deleteExistingEventDataFileIfExists(final String filePathFromExistingRow) {
        try {
            if (filePathFromExistingRow != null && !filePathFromExistingRow.equals("NA")) {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(filePathFromExistingRow);
            }
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while deleteExistingEventDataFileIfExists", e);
        }
    }
    
    private String[] createEventDataFile(final Long customerID, final long summaryEventCode, final Long currentTime, final JSONObject reqObj) throws Exception {
        final String[] fileNameAndCheckSum = { "", "" };
        final String fileName = SummaryEventDataHandler.summaryEventDir + File.separator + summaryEventCode + File.separator + customerID + "_" + currentTime + ".txt";
        String checkSum = "";
        try {
            final String encryptedData = ApiFactoryProvider.getCryptoAPI().encrypt(reqObj.toString());
            ApiFactoryProvider.getFileAccessAPI().writeFile(fileName, encryptedData.getBytes());
            checkSum = ChecksumProvider.getInstance().GetSHA256CheckSum(fileName);
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Excepion while writeFile", e);
            throw e;
        }
        fileNameAndCheckSum[0] = fileName;
        fileNameAndCheckSum[1] = checkSum;
        return fileNameAndCheckSum;
    }
    
    public void processSummaryEventData(final Long probeID) {
        this.processSummaryEventData(probeID, this.getApplicableForNewProbeCriteria());
    }
    
    public void processSummaryEventData(final Long probeID, final Criteria criteria) {
        try {
            int currentEventCount = 0;
            final String tableName = "SummaryEventData";
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            selectQuery.addSelectColumn(new Column(tableName, "EVENT_ID"));
            selectQuery.addSelectColumn(new Column(tableName, "SUMMARY_EVENT_ID"));
            selectQuery.addSelectColumn(new Column(tableName, "EVENT_FILE_PATH"));
            selectQuery.addSelectColumn(new Column(tableName, "FILE_CHECKSUM"));
            selectQuery.addSelectColumn(new Column(tableName, "IS_REQUIRED_FOR_NEW_PROBE"));
            final SortColumn sortColumn = new SortColumn(tableName, "EVENT_CREATED_TIME", true);
            selectQuery.addSortColumn(sortColumn);
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            selectQuery.setCriteria(criteria);
            int startingIndex = 1;
            final int offSetValue = 10000;
            DataObject dataObject = DataAccess.constructDataObject();
            do {
                selectQuery.setRange(new Range(startingIndex, offSetValue));
                dataObject = DataAccess.get(selectQuery);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator<Row> rowIterator = dataObject.getRows(tableName);
                    while (rowIterator.hasNext()) {
                        final Row row = rowIterator.next();
                        if (this.validateSummaryEventDataRow(row)) {
                            if (currentEventCount == 29) {
                                Thread.sleep(60000L);
                                currentEventCount = 0;
                            }
                            this.processEventsDataToProbe(probeID, row);
                            ++currentEventCount;
                        }
                        else {
                            SummaryEventDataHandler.logger.log(Level.WARNING, "CheckSum Validation Failed for FilePath--> " + row.get("EVENT_FILE_PATH"));
                        }
                    }
                }
                startingIndex += offSetValue;
            } while (dataObject != null && !dataObject.isEmpty());
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while processSummaryEventData", e);
        }
    }
    
    private boolean validateSummaryEventDataRow(final Row row) {
        boolean isValidRow;
        try {
            isValidRow = ChecksumProvider.getInstance().ValidateSHA256CheckSum((String)row.get("EVENT_FILE_PATH"), (String)row.get("FILE_CHECKSUM"));
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while validateSummaryEventDataRow", e);
            isValidRow = false;
        }
        return isValidRow;
    }
    
    private void processEventsDataToProbe(final Long probeId, final Row row) {
        try {
            final List<Long> targetProbe = new ArrayList<Long>();
            targetProbe.add(probeId);
            final String filePath = (String)row.get("EVENT_FILE_PATH");
            final String fileContent = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(filePath);
            final String decryptedContent = ApiFactoryProvider.getCryptoAPI().decrypt(fileContent);
            final JSONObject jsonObject = new JSONObject(decryptedContent);
            jsonObject.put("summaryEventID", row.get("SUMMARY_EVENT_ID"));
            jsonObject.put("requestBodyType", (Object)"json");
            final Map<String, String> authProp = ProbeMgmtFactoryProvider.getProbeSyncAPI().getUserDomainDetails(true);
            final ProbeDistributionInitializer probeDistributionInitializer = ProbeMgmtFactoryProvider.getProbeDistributionInitializer();
            probeDistributionInitializer.addRequestAuthProperties(authProp, jsonObject);
            probeDistributionInitializer.addToProbeQueue(jsonObject, targetProbe);
        }
        catch (final Exception e) {
            SummaryEventDataHandler.logger.log(Level.SEVERE, "Exception while processEventsDataToProbe", e);
        }
    }
    
    public void syncTablesData(final Long probeID) {
        if (SummaryEventDataHandler.tablesListForSync.isEmpty()) {
            this.populateSyncRequiredTablesForNewProbe();
        }
        for (final String table : SummaryEventDataHandler.tablesListForSync) {
            ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().syncSSTableData(table, null, probeID);
        }
    }
    
    static {
        SummaryEventDataHandler.tablesListForSync = new ArrayList<String>();
        SummaryEventDataHandler.eventDataHandler = null;
        SummaryEventDataHandler.logger = Logger.getLogger("probeActionsLogger");
        summaryEventDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "summary-event";
    }
}
