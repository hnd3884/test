package com.me.mdm.server.datausage;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.datausage.data.DataPeriodCriteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.datausage.data.DataPeriod;
import java.util.Calendar;
import java.util.logging.Logger;

public class DataUsageUtil
{
    Logger logger;
    
    public DataUsageUtil() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public DataPeriod getBucketFromCycleAndDate(final Calendar calendar, int billingDate) {
        final int dateOfMonth = calendar.get(5);
        int month = calendar.get(2);
        int year = calendar.get(1);
        final DataPeriod dataPeriod = new DataPeriod();
        final Calendar billingFrom = Calendar.getInstance();
        final Calendar billingTo = Calendar.getInstance();
        if (dateOfMonth >= billingDate) {
            billingFrom.set(year, month, billingDate);
            if (billingDate == 1) {
                billingDate = calendar.getActualMaximum(5);
            }
            else {
                if (month == 11) {
                    month = 0;
                    ++year;
                }
                else {
                    ++month;
                }
                --billingDate;
            }
            billingTo.set(year, month, billingDate);
        }
        else {
            billingTo.set(year, month, billingDate - 1);
            if (month == 0) {
                month = 11;
                --year;
            }
            else {
                --month;
            }
            billingFrom.set(year, month, billingDate);
        }
        this.makeCalendarCompatible(billingFrom, true);
        this.makeCalendarCompatible(billingTo, false);
        dataPeriod.startTime = billingFrom.getTimeInMillis();
        dataPeriod.endTime = billingTo.getTimeInMillis();
        return dataPeriod;
    }
    
    public void makeCalendarCompatible(final Calendar calendar, final Boolean startDate) {
        calendar.set(14, ((boolean)startDate) ? 0 : 999);
        calendar.set(13, ((boolean)startDate) ? 0 : 59);
        calendar.set(12, ((boolean)startDate) ? 0 : 59);
        calendar.set(11, ((boolean)startDate) ? 0 : 23);
    }
    
    public void DeleteOlderDataUsageEntriesInDB() throws Exception {
        final String deletionTime = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("DataUsageDeletePeriod");
        final String deletionOverride = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("DataUsageDeleteOverride");
        Boolean override = Boolean.FALSE;
        int days = 60;
        if (deletionOverride != null && Boolean.valueOf(deletionOverride) && deletionTime != null) {
            days = Integer.parseInt(deletionTime);
            override = Boolean.TRUE;
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DataTrackingSettings"));
        selectQuery.addSelectColumn(Column.getColumn("DataTrackingSettings", "*"));
        final DataObject dataObject = MDMUtil.getReadOnlyPersistence().get(selectQuery);
        final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        if (customerIDs != null) {
            for (final Long customerID : customerIDs) {
                days = 60;
                final Row row = dataObject.getRow("DataTrackingSettings", new Criteria(Column.getColumn("DataTrackingSettings", "CUSTOMER_ID"), (Object)customerID, 0));
                if (!override && row != null) {
                    days = (int)row.get("RETAIN_DATA_DAYS");
                }
                this.logger.log(Level.INFO, "Going to delete old data usage entries. NumDays to be retained : {0}", days);
                Hashtable ht = new Hashtable();
                ht = DateTimeUtil.determine_From_To_Times("today");
                final Long today = ht.get("date1");
                final Long lastDate = today - days * 24 * 60 * 60 * 1000L;
                this.logger.log(Level.INFO, "Computed Time stamp is : {0}", lastDate);
                final DataPeriod dataPeriod = new DataPeriod();
                dataPeriod.endTime = lastDate;
                dataPeriod.startTime = 0L;
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DataTrackingPeriods");
                if (!override) {
                    deleteQuery.addJoin(new Join("DataTrackingPeriods", "DataTrackingHistory", new String[] { "PERIOD_ID" }, new String[] { "PERIOD_ID" }, 2));
                    deleteQuery.addJoin(new Join("DataTrackingHistory", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                    deleteQuery.setCriteria(new DataPeriodCriteria(dataPeriod, 7).getFinalCriteria().and(customerCriteria));
                }
                else {
                    deleteQuery.setCriteria(new DataPeriodCriteria(dataPeriod, 7).getFinalCriteria());
                }
                MDMUtil.getPersistenceLite().delete(deleteQuery);
                this.logger.log(Level.INFO, "Deleted all entries before : {0} Days for customer {1}", new Object[] { days, customerID });
            }
        }
    }
}
