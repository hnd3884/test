package com.me.mdm.server.datausage;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.datausage.data.DataUsageHistoryQuery;
import com.me.mdm.server.datausage.data.DataEntity;
import com.me.mdm.server.datausage.data.DataPeriodCriteria;
import com.me.mdm.server.datausage.data.DataEntityCriteria;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.datausage.data.DataUsageSummaryQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.me.mdm.server.datausage.data.DataPeriod;
import java.util.Calendar;
import org.json.JSONObject;
import com.me.mdm.server.datausage.android.AndroidDataUsageParser;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.datausage.data.DataUsageHistory;
import com.me.mdm.server.datausage.data.DataUsageSummary;
import java.util.List;

public class DataUsageTrackingHandler
{
    List<DataUsageSummary> reportedSummary;
    List<DataUsageHistory> reportedHistory;
    DataUsageParsingInterface dataUsageParsingInterface;
    DataUsageUtil dataUsageUtil;
    DataObject timePeriodsDO;
    DataObject entitiesDO;
    DataObject historyFinalDO;
    DataObject summaryFinalDO;
    Long resourceID;
    
    public DataUsageTrackingHandler(final int platform, final Long resourceID) {
        this.reportedSummary = new ArrayList<DataUsageSummary>();
        this.reportedHistory = new ArrayList<DataUsageHistory>();
        this.dataUsageParsingInterface = this.getDataParsingInterface(platform);
        this.historyFinalDO = (DataObject)new WritableDataObject();
        this.resourceID = resourceID;
        this.dataUsageUtil = new DataUsageUtil();
    }
    
    private DataUsageParsingInterface getDataParsingInterface(final int platform) {
        if (platform == 2) {
            return new AndroidDataUsageParser();
        }
        throw new UnsupportedOperationException("Feature not supported for this platform ");
    }
    
    public void parseAndPersistResourceReport(final JSONObject data) throws Exception {
        this.dataUsageParsingInterface.parseDataUsageSummary(this.resourceID, data, this.reportedHistory);
        this.reportedHistory = this.addOrUpdateHistoryReportToDB();
        this.convertHistoryListToSummary();
        this.addOrUpdateSummaryReportToDB();
        this.reportedHistory.clear();
        this.dataUsageParsingInterface.parsePerAppUsageSummary(this.resourceID, data, this.reportedHistory);
        this.addOrUpdateHistoryReportToDB();
    }
    
    private void convertHistoryListToSummary() throws DataAccessException {
        final List<DataPeriod> dataPeriods = this.getValidDataPeriodsForResource();
        for (final DataUsageHistory dataUsageHistory : this.reportedHistory) {
            final DataUsageSummary dataUsageSummary = new DataUsageSummary();
            dataUsageSummary.resourceID = dataUsageHistory.resourceID;
            dataUsageSummary.usage = dataUsageHistory.getDelta();
            dataUsageSummary.dataEntity = dataUsageHistory.dataEntity;
            dataUsageSummary.dataPeriod = dataUsageHistory.dataPeriod.getBucketPeriod(dataPeriods);
            if (dataUsageSummary.dataPeriod == null) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dataUsageHistory.dataPeriod.startTime);
                final DataPeriod period = this.dataUsageUtil.getBucketFromCycleAndDate(calendar, this.getBillingCycleOfResource());
                if (!dataPeriods.contains(period)) {
                    dataPeriods.add(period);
                }
                dataUsageSummary.dataPeriod = period;
            }
            this.reportedSummary.add(dataUsageSummary);
        }
    }
    
    private List<DataPeriod> getValidDataPeriodsForResource() throws DataAccessException {
        final List<DataPeriod> validPeriods = new ArrayList<DataPeriod>();
        final DataUsageSummaryQuery dataUsageSummaryQuery = new DataUsageSummaryQuery();
        dataUsageSummaryQuery.addJoin("DataTrackingPeriods", 2);
        dataUsageSummaryQuery.addSelectColumn(Column.getColumn("DataTrackingPeriods", "*"));
        dataUsageSummaryQuery.setCriteria(new Criteria(Column.getColumn("DataTrackingSummary", "RESOURCE_ID"), (Object)this.resourceID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get((SelectQuery)dataUsageSummaryQuery);
        final Iterator iterator = dataObject.getRows("DataTrackingPeriods");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final DataPeriod period = new DataPeriod(row);
            if (!validPeriods.contains(period)) {
                validPeriods.add(period);
            }
        }
        final int billingDate = this.getBillingCycleOfResource();
        final Calendar calendar = Calendar.getInstance();
        this.dataUsageUtil.makeCalendarCompatible(calendar, true);
        final DataPeriod period2 = this.dataUsageUtil.getBucketFromCycleAndDate(calendar, billingDate);
        if (!validPeriods.contains(period2)) {
            validPeriods.add(period2);
        }
        return validPeriods;
    }
    
    private List<DataUsageHistory> addOrUpdateHistoryReportToDB() throws Exception {
        final List<DataUsageHistory> historyAddedList = new ArrayList<DataUsageHistory>();
        this.populateSubDOs(Boolean.TRUE);
        for (final DataUsageHistory dataUsageHistory : this.reportedHistory) {
            final Row entityRow = this.entitiesDO.getRow("DataEntity", new DataEntityCriteria(dataUsageHistory.dataEntity, 0).getFinalCriteria());
            final Row periodRow = this.timePeriodsDO.getRow("DataTrackingPeriods", new DataPeriodCriteria(dataUsageHistory.dataPeriod, 0).getFinalCriteria());
            if (entityRow != null) {
                dataUsageHistory.dataEntity = new DataEntity(entityRow);
            }
            if (periodRow != null) {
                dataUsageHistory.dataPeriod = new DataPeriod(periodRow);
            }
        }
        final DataUsageHistoryQuery selectQuery = new DataUsageHistoryQuery();
        selectQuery.setCriteria(this.reportedHistory, 8);
        this.historyFinalDO = MDMUtil.getPersistenceLite().get((SelectQuery)selectQuery);
        for (final DataUsageHistory dataUsageHistory2 : this.reportedHistory) {
            dataUsageHistory2.addRowToDO(this.historyFinalDO);
            historyAddedList.add(dataUsageHistory2);
        }
        MDMUtil.getPersistenceLite().update(this.historyFinalDO);
        return historyAddedList;
    }
    
    private void addOrUpdateSummaryReportToDB() throws Exception {
        this.populateSubDOs(Boolean.FALSE);
        for (final DataUsageSummary dataUsageSummary : this.reportedSummary) {
            final Row entityRow = this.entitiesDO.getRow("DataEntity", new DataEntityCriteria(dataUsageSummary.dataEntity, 0).getFinalCriteria());
            final Row periodRow = this.timePeriodsDO.getRow("DataTrackingPeriods", new DataPeriodCriteria(dataUsageSummary.dataPeriod, 0).getFinalCriteria());
            if (entityRow != null) {
                dataUsageSummary.dataEntity = new DataEntity(entityRow);
            }
            if (periodRow != null) {
                dataUsageSummary.dataPeriod = new DataPeriod(periodRow);
            }
        }
        final DataUsageSummaryQuery selectQuery = new DataUsageSummaryQuery();
        selectQuery.setCriteria(this.reportedSummary, 8);
        this.summaryFinalDO = MDMUtil.getPersistenceLite().get((SelectQuery)selectQuery);
        for (final DataUsageSummary dataUsageSummary2 : this.reportedSummary) {
            dataUsageSummary2.addOrUpdateDataUsageSummary(this.summaryFinalDO);
        }
        MDMUtil.getPersistenceLite().update(this.summaryFinalDO);
    }
    
    private void populateSubDOs(final Boolean isHistory) throws DataAccessException {
        final List<DataPeriod> timePeriods = new ArrayList<DataPeriod>();
        final List<DataEntity> dataEntities = new ArrayList<DataEntity>();
        if (isHistory) {
            for (final DataUsageHistory dataUsageHistory : this.reportedHistory) {
                if (!timePeriods.contains(dataUsageHistory.dataPeriod)) {
                    timePeriods.add(dataUsageHistory.dataPeriod);
                }
                if (!dataEntities.contains(dataUsageHistory.dataEntity)) {
                    dataEntities.add(dataUsageHistory.dataEntity);
                }
            }
        }
        else {
            for (final DataUsageSummary dataUsageSummary : this.reportedSummary) {
                if (!timePeriods.contains(dataUsageSummary.dataPeriod)) {
                    timePeriods.add(dataUsageSummary.dataPeriod);
                }
                if (!dataEntities.contains(dataUsageSummary.dataEntity)) {
                    dataEntities.add(dataUsageSummary.dataEntity);
                }
            }
        }
        this.entitiesDO = DataEntity.getDataEntitiesFromDB(dataEntities);
        this.timePeriodsDO = DataPeriod.getDataPeriodsFromDB(timePeriods);
    }
    
    private int getBillingCycleOfResource() throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "DataTrackingPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("DataTrackingPolicy", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DataTrackingPolicy", "BILLING_CYCLE"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)this.resourceID, 0);
        final Criteria cfgDataID = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)901, 0);
        selectQuery.setCriteria(resourceCriteria.and(cfgDataID));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Row row = dataObject.getFirstRow("DataTrackingPolicy");
        return (int)row.get("BILLING_CYCLE");
    }
}
