package com.me.mdm.server.datausage.data;

import com.adventnet.ds.query.Join;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQueryImpl;

public class DataUsageSummaryQuery extends SelectQueryImpl
{
    HashMap joins;
    List<Criteria> filters;
    
    public DataUsageSummaryQuery() {
        this(Boolean.TRUE);
    }
    
    public DataUsageSummaryQuery(final Boolean addSelectColumns) {
        super(new Table("DataTrackingSummary"));
        if (addSelectColumns) {
            this.addSelectColumn(Column.getColumn("DataTrackingSummary", "*"));
        }
        this.filters = new ArrayList<Criteria>();
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
        final JoinEntities managedextnDevice = new JoinEntities();
        managedextnDevice.tableName = "ManagedDeviceExtn";
        managedextnDevice.thisTableCol = "RESOURCE_ID";
        managedextnDevice.joinTableCol = "MANAGED_DEVICE_ID";
        this.joins.put("ManagedDevice", managedDevice);
        this.joins.put("ManagedDeviceExtn", managedextnDevice);
        this.joins.put("DataTrackingPeriods", trackingPeriod);
        this.joins.put("DataEntity", trackingEntity);
    }
    
    public void setCriteria(final Object dataUsageSummary, final int operator) {
        this.setCriteria(this.getCriteria(dataUsageSummary, operator));
    }
    
    public Criteria getCriteria(final Object dataUsageSummary, final int operator) {
        Criteria finalCriteria = null;
        switch (operator) {
            case 0: {
                final Criteria resCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "RESOURCE_ID"), (Object)((DataUsageSummary)dataUsageSummary).resourceID, 0);
                final Criteria periodCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "PERIOD_ID"), (Object)((DataUsageSummary)dataUsageSummary).dataPeriod.periodID, 0);
                final Criteria entityCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "ENTITY_ID"), (Object)((DataUsageSummary)dataUsageSummary).dataEntity.entityID, 0);
                finalCriteria = resCriteria.and(periodCriteria).and(entityCriteria);
                break;
            }
            case 8: {
                final List<DataUsageSummary> list = (List<DataUsageSummary>)dataUsageSummary;
                final Iterator iterator = list.iterator();
                final List resList = new ArrayList();
                final List perList = new ArrayList();
                final List entList = new ArrayList();
                while (iterator.hasNext()) {
                    final DataUsageSummary summary = iterator.next();
                    if (!resList.contains(summary.resourceID)) {
                        resList.add(summary.resourceID);
                    }
                    if (!perList.contains(summary.dataPeriod.periodID)) {
                        perList.add(summary.dataPeriod.periodID);
                    }
                    if (!entList.contains(summary.dataEntity.entityID)) {
                        entList.add(summary.dataEntity.entityID);
                    }
                }
                final Criteria resCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "RESOURCE_ID"), (Object)resList.toArray(), 8);
                final Criteria periodCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "PERIOD_ID"), (Object)perList.toArray(), 8);
                final Criteria entityCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "ENTITY_ID"), (Object)entList.toArray(), 8);
                finalCriteria = resCriteria.and(periodCriteria).and(entityCriteria);
                break;
            }
        }
        return finalCriteria;
    }
    
    public void addJoin(final String tableName, final int joinType) {
        final JoinEntities joinEntities = this.joins.get(tableName);
        this.addJoin(new Join("DataTrackingSummary", tableName, new String[] { joinEntities.thisTableCol }, new String[] { joinEntities.joinTableCol }, joinType));
    }
    
    public void addFilters(final Object value, final int operator, final String table) {
        Criteria finalCriteria = null;
        int n = -1;
        switch (table.hashCode()) {
            case 1863809989: {
                if (table.equals("DataTrackingSummary")) {
                    n = 0;
                    break;
                }
                break;
            }
            case -118499659: {
                if (table.equals("ManagedDevice")) {
                    n = 1;
                    break;
                }
                break;
            }
        }
        Label_0339: {
            switch (n) {
                case 0: {
                    switch (operator) {
                        case 0: {
                            if (value instanceof Long) {
                                finalCriteria = new Criteria(Column.getColumn("DataTrackingSummary", "RESOURCE_ID"), value, 0);
                                break;
                            }
                            if (value instanceof DataPeriod) {
                                final Criteria startCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_START_TIME"), (Object)((DataPeriod)value).startTime, 0);
                                final Criteria endCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_END_TIME"), (Object)((DataPeriod)value).endTime, 0);
                                finalCriteria = startCriteria.and(endCriteria);
                                break;
                            }
                            if (value instanceof DataEntity) {
                                final Criteria entCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_IDENTIFIER"), (Object)((DataEntity)value).identifier, 0);
                                final Criteria typeCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_TYPE"), (Object)((DataEntity)value).type, 0);
                                finalCriteria = entCriteria.and(typeCriteria);
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (operator) {
                        case 8: {
                            if (value instanceof Object[] && this.getTableList().contains(Table.getTable("ManagedDevice"))) {
                                finalCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), value, 8);
                                break Label_0339;
                            }
                            break Label_0339;
                        }
                    }
                    break;
                }
            }
        }
        if (finalCriteria != null) {
            this.filters.add(finalCriteria);
        }
    }
    
    public void applyFilters() {
        if (this.filters.size() > 0) {
            final Iterator iterator = this.filters.iterator();
            Criteria criteria = iterator.next();
            while (iterator.hasNext()) {
                criteria = criteria.and((Criteria)iterator.next());
            }
            if (this.getCriteria() != null) {
                this.setCriteria(this.getCriteria().and(criteria));
            }
            else {
                this.setCriteria(criteria);
            }
        }
    }
    
    public void addCustomerCriteria(final Long customerID) {
        if (!this.getTableList().contains(Table.getTable("Resource"))) {
            this.addJoin(new Join("DataTrackingSummary", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "Resource" }, 2));
        }
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        this.appendCriteria(customerCriteria);
    }
    
    public void appendCriteria(final Criteria criteria) {
        if (this.getCriteria() == null) {
            this.setCriteria(criteria);
        }
        else {
            this.setCriteria(this.getCriteria().and(criteria));
        }
    }
    
    private class JoinEntities
    {
        String tableName;
        String thisTableCol;
        String joinTableCol;
    }
}
