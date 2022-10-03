package com.me.mdm.server.datausage.data;

import com.adventnet.ds.query.Join;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQueryImpl;

public class DataUsageHistoryQuery extends SelectQueryImpl
{
    HashMap joins;
    
    public DataUsageHistoryQuery() {
        super(new Table("DataTrackingHistory"));
        this.addSelectColumn(Column.getColumn("DataTrackingHistory", "*"));
        this.joins = new HashMap();
        final JoinEntities managedDevice = new JoinEntities();
        managedDevice.tableName = "ManagedDevice";
        managedDevice.thisTableCol = "RESOURCE_ID";
        managedDevice.joinTableCol = "RESOURCE_ID";
        final JoinEntities trackingPeriod = new JoinEntities();
        trackingPeriod.tableName = "DataTrackingPeriods";
        trackingPeriod.thisTableCol = "PERIOD_ID";
        trackingPeriod.joinTableCol = "PERIOD_ID";
        final JoinEntities trackingEntity = new JoinEntities();
        trackingEntity.tableName = "DataEntity";
        trackingEntity.thisTableCol = "ENTITY_ID";
        trackingEntity.joinTableCol = "ENTITY_ID";
        this.joins.put("ManagedDevice", managedDevice);
        this.joins.put("DataTrackingPeriods", trackingPeriod);
        this.joins.put("DataEntity", trackingEntity);
    }
    
    public void setCriteria(final Object dataUsageHistory, final int operator) {
        this.setCriteria(this.getCriteria(dataUsageHistory, operator));
    }
    
    public Criteria getCriteria(final Object dataUsageHist, final int operator) {
        Criteria finalCriteria = null;
        switch (operator) {
            case 0: {
                final Criteria resCriteria = new Criteria(Column.getColumn("DataTrackingHistory", "RESOURCE_ID"), (Object)((DataUsageHistory)dataUsageHist).resourceID, 0);
                final Criteria periodCriteria = new Criteria(Column.getColumn("DataTrackingHistory", "PERIOD_ID"), (Object)((DataUsageHistory)dataUsageHist).dataPeriod.periodID, 0);
                final Criteria entityCriteria = new Criteria(Column.getColumn("DataTrackingHistory", "ENTITY_ID"), (Object)((DataUsageHistory)dataUsageHist).dataEntity.entityID, 0);
                finalCriteria = resCriteria.and(periodCriteria).and(entityCriteria);
                break;
            }
            case 8: {
                final List<DataUsageHistory> list = (List<DataUsageHistory>)dataUsageHist;
                final Iterator iterator = list.iterator();
                final List resList = new ArrayList();
                final List perList = new ArrayList();
                final List entList = new ArrayList();
                while (iterator.hasNext()) {
                    final DataUsageHistory dataUsageHistory = iterator.next();
                    if (!resList.contains(dataUsageHistory.resourceID)) {
                        resList.add(dataUsageHistory.resourceID);
                    }
                    if (!perList.contains(dataUsageHistory.dataPeriod.periodID)) {
                        perList.add(dataUsageHistory.dataPeriod.periodID);
                    }
                    if (!entList.contains(dataUsageHistory.dataEntity.entityID)) {
                        entList.add(dataUsageHistory.dataEntity.entityID);
                    }
                }
                final Criteria resCriteria = new Criteria(Column.getColumn("DataTrackingHistory", "RESOURCE_ID"), (Object)resList.toArray(), 8);
                final Criteria periodCriteria = new Criteria(Column.getColumn("DataTrackingHistory", "PERIOD_ID"), (Object)perList.toArray(), 8);
                final Criteria entityCriteria = new Criteria(Column.getColumn("DataTrackingHistory", "ENTITY_ID"), (Object)entList.toArray(), 8);
                finalCriteria = resCriteria.and(periodCriteria).and(entityCriteria);
                break;
            }
        }
        return finalCriteria;
    }
    
    public void addJoin(final String tableName, final int joinType) {
        final JoinEntities joinEntities = this.joins.get(tableName);
        this.addJoin(new Join("DataTrackingHistory", tableName, new String[] { joinEntities.thisTableCol }, new String[] { joinEntities.joinTableCol }, joinType));
    }
    
    private class JoinEntities
    {
        String tableName;
        String thisTableCol;
        String joinTableCol;
    }
}
